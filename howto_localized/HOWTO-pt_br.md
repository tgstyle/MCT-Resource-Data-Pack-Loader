# Resource Data Pack Loader

**Uma pasta que substitui qualquer coisa que o Minecraft ou um mod forneça, define conteúdo novo a partir de JSON e controla o que é gerado, em todos os mundos, em clientes e servidores, sem nada para os jogadores ativarem.**

Treze exemplos funcionais. Coloque qualquer um deles direto em `rdploader` e veja como cada arquivo é escrito.

- [RDPLExamplePack.zip](../example/RDPLExamplePack.zip) usa quase todo tipo de arquivo que o loader lê: blocos, itens, um fluido, uma aba criativa, biomas, um modelo de mundo, uma dimensão atrás de um portal, worldgen, uma poção e sua fermentação, um aldeão e suas trocas, receitas, saque, substituições de coisas do vanilla, um som, um avanço e uma função. O readme diz o que conferir no jogo.
- [RDPLExampleOrePackVoid.zip](../example/RDPLExampleOrePackVoid.zip) transforma o overworld em um vazio, com worldgen suspensa no ar, uma forma por faixa de altura, de modo que cada uma seja fácil de ver isoladamente.
- [RDPLExampleVeinShapes.zip](../example/RDPLExampleVeinShapes.zip) coloca três veios de minério em um overworld comum, um por padrão de veio (simples, em faixas e em tubo), cada um com camadas rica, normal e pobre e alguns blocos marcadores na superfície acima dele para servir de guia na prospecção.
- [RDPLExampleVeinShapesVoid.zip](../example/RDPLExampleVeinShapesVoid.zip) suspende os mesmos três padrões de veio em um vazio, para que cada forma possa ser vista por inteiro.
- [RDPLExampleDeepWorld.zip](../example/RDPLExampleDeepWorld.zip) rebaixa o fundo do overworld para -320, de modo que 256 blocos de mundo gerado ficam abaixo do vanilla: a mistura de pedra profunda, com andesito surgindo aos poucos da ardósia profunda, as próprias cavernas de ruído do jogo até o fundo e três regiões de cavernas para descer, cada uma com sua própria decoração e ambientação.
- [RDPLExampleContainers.zip](../example/RDPLExampleContainers.zip) adiciona blocos e itens carregáveis que guardam um inventário, em todos os tamanhos, de três espaços até o maior permitido, com uma tabela de saque, o modelo de baú tingido a partir da folha vanilla, todas as texturas desenhadas como mapas de pixels e uma bolsa e uma mochila que podem ser usadas no Curios.
- [RDPLExampleMegaCity32.zip](../example/RDPLExampleMegaCity32.zip) cria um mundo superplano coberto de ponta a ponta por distritos urbanos, crescido até mil terrenos e fixado na origem, com ruas de concreto, esgotos sob elas, duas linhas de metrô abaixo deles e uma ferrovia atravessando a cidade, e prédios de estrutura com até 123 blocos de altura.
- [RDPLExampleMegaCity64.zip](../example/RDPLExampleMegaCity64.zip) é essa mesma cidade com o mundo elevado a 512 e as nuvens a 384, árvores nas ruas e torres de estrutura com até 251 blocos de altura em terrenos de 16, 32 e 64 de largura. A cidade não tem fim: cada distrito do mundo é uma cidade por si só, e as ruas continuam aonde quer que você vá.
- [RDPLExampleCityCustomMap.zip](../example/RDPLExampleCityCustomMap.zip) desenha essa mesma cidade a partir de um mapa de cidade, em vez de sorteá-la: uma grade de caracteres com 48 blocos por célula, com uma paleta que nomeia ruas, praças, vielas e escolhas ponderadas de prédios, de modo que a planta dos quarteirões é traçada à mão.
- [MCTKamikazeDemo.zip](../example/MCTKamikazeDemo.zip) coloca quatro facções umas contra as outras em uma arena de bedrock sob noite permanente: cada lado é uma equipe real do placar vanilla à qual seus mobs se juntam ao nascer, um lado pontua por cada mob de outro lado que mata, uma rodada termina em uma carta após dois minutos e três rodadas formam uma partida.
- [RDPLExampleRaid.zip](../example/RDPLExampleRaid.zip) monta um mundo plano em torno de uma das vilas do jogo e dá ao jogador um Mau Presságio ao entrar: cinco ondas de illagers vanilla, uma bruxa e os Pillagers, Capitães da Invasão, Lançadores de Machado e Ravagers do próprio pacote, o presságio e Herói da Vila como efeitos do próprio pacote com ícones em mapa de pixels, o bloco de sino do próprio pacote, uma bebida que traz o presságio de volta e as funções que encerram a invasão.
- [RDPLExampleGameHall.zip](../example/RDPLExampleGameHall.zip) transforma um mundo plano em um salão de jogos: dados ponderados do próprio pacote, um baralho de mobs que se embaralha sozinho e um baralho da sorte que fica vazio até ser embaralhado, um Copo de Dados que rola 2d6 com um clique direito, um Duelo de dados em turnos contra a Casa que resolve um empate por sorteio, e xadrez e damas jogados com mobs contra o computador.
- [RDPLExampleColony.zip](../example/RDPLExampleColony.zip) constrói sozinho um pátio de trabalho ao fechar a introdução e põe aldeões para fazer todo tipo de ordem de trabalho: um minerador cujo alcance cresce com a picareta, um lenhador que guarda o que corta, um fazendeiro que mantém um estoque fixo de trigo e um carregador que transporta pedregulho, cada ordem aberta por uma placa, uma ferramenta, uma contratação ou um baú de estoque.

Este guia é para as versões 1.20.1 e 1.21.1. Elas leem os mesmos pacotes; os poucos pontos em que as duas diferem estão marcados com **1.20.1** e **1.21.1**.

---

## Conteúdo

**Primeiros passos**
- [O que é](#o-que-é)
- [Onde os arquivos ficam](#onde-os-arquivos-ficam)
- [Lendo as tabelas](#lendo-as-tabelas)
- [A única regra](#a-única-regra)
- [Organizando pacotes](#organizando-pacotes)
- [Pacotes de recursos: quem vence](#pacotes-de-recursos-quem-vence)

**Como os pacotes funcionam**
- [Como as definições funcionam](#como-as-definições-funcionam)
- [O que você pode substituir](#o-que-você-pode-substituir)
- [Pacotes do lado do servidor](#pacotes-do-lado-do-servidor)
- [Renomeações de registro](#renomeações-de-registro)
- [API de mods](#api-de-mods)
- [Pacotes escritos para 1.12.2](#pacotes-escritos-para-1122)

**Blocos e itens**
- [Blocos](#blocos)
- [Contêineres](#contêineres)
- [Sinos](#sinos)
- [Modelos, blockstates e texturas](#modelos-blockstates-e-texturas)
- [Fazendo o vanilla tratar seu bloco corretamente](#fazendo-o-vanilla-tratar-seu-bloco-corretamente)
- [Itens](#itens)
- [Fluidos](#fluidos)
- [Materiais, abas, sons, tags](#materiais-abas-sons-tags)
- [Substituições de propriedades](#substituições-de-propriedades)
- [Grupos de dureza](#grupos-de-dureza)

**Criação, saque e comércio**
- [Blocos e itens desativados](#blocos-e-itens-desativados)
- [Receitas de fornalha e combustíveis](#receitas-de-fornalha-e-combustíveis)
- [Poções, tipos de poção e fermentação](#poções-tipos-de-poção-e-fermentação)
- [Trabalho na bigorna](#trabalho-na-bigorna)
- [Drops de blocos](#drops-de-blocos)
- [Saque de jogadores](#saque-de-jogadores)
- [Aldeões e trocas](#aldeões-e-trocas)

**Criaturas e perigos**
- [Variantes de entidades](#variantes-de-entidades)
- [Ordens de trabalho](#ordens-de-trabalho)
- [Exposições](#exposições)

**O mundo**
- [Modelos de mundo](#modelos-de-mundo)
- [Regras do jogo](#regras-do-jogo)
- [Biomas](#biomas)
- [Dimensões](#dimensões)
- [Portais e passagens](#portais-e-passagens)
- [O mundo profundo](#o-mundo-profundo)
- [Regiões de cavernas](#regiões-de-cavernas)

**Gerando o mundo**
- [Entradas de worldgen](#entradas-de-worldgen)
- [Formas](#formas)
- [Dispersões](#dispersões)
- [Mapas de estruturas](#mapas-de-estruturas)
- [Terrenos de vilas](#terrenos-de-vilas)
- [Mapas de layout de cidades](#mapas-de-layout-de-cidades)
- [Cidade contínua](#cidade-contínua)
- [Retrogen](#retrogen)
- [Pré-geração](#pré-geração)

**Modos de jogo**
- [Introdução do mundo](#introdução-do-mundo)
- [Equipes](#equipes)
- [Pontuação](#pontuação)
- [Invasões](#invasões)
- [Cartas](#cartas)
- [Dados e baralhos](#dados-e-baralhos)

**Controle**
- [A camada de controle](#a-camada-de-controle)
- [O que cada grupo faz](#o-que-cada-grupo-faz)

**Outros mods**
- [Integração com Blast Plaster](#integração-com-blast-plaster)

**Referência**
- [Listas de valores](#listas-de-valores)
- [Lista de pastas](#lista-de-pastas)
- [Comandos](#comandos)
- [Bom saber](#bom-saber)
- [Quando algo não funciona](#quando-algo-não-funciona)
- [Bônus: ajustes do vanilla](#bônus-ajustes-do-vanilla)
- [Chaves que não foram mantidas](#chaves-que-não-foram-mantidas)

---

# Primeiros passos

## O que é

*primeiros passos*

O Resource Data Pack Loader (RDPL) lê uma única pasta, `rdploader`, e faz três trabalhos:

- **Substituições.** Um arquivo na pasta substitui aquele que o jogo ou um mod teria carregado. Sem alternância, sem configuração por mundo, nada para os jogadores ativarem.
- **Conteúdo novo.** Definições em JSON registram blocos, itens, fluidos, biomas, dimensões, poções e aldeões. Sem Java, sem jar.
- **Controle.** Bloqueie a geração de minérios, biomas, estruturas ou receitas, achate a bedrock, defina taxas de spawn, esvazie a Superfície, defina os padrões do mundo.

## Onde os arquivos ficam

*primeiros passos*

Um pacote tem duas raízes, as mesmas duas de um pacote vanilla. `assets/` guarda o que o cliente desenha e ouve: modelos, blockstates, texturas, arquivos de idioma, sons e os textos da introdução. `data/` guarda todo o resto: cada definição que este mod lê e os arquivos de dados do vanilla que um pacote substitui. Todo caminho neste guia é escrito a partir do namespace, então `<namespace>/blocks/*.json` é `data/mypack/blocks/ruby_ore.json` no disco para um pacote cujo namespace é `mypack`, e `<namespace>/models/` é `assets/mypack/models/`. Cada seção repete o próprio caminho abaixo do seu título.

Em `data/`:

| Caminho | O que contém |
| --- | --- |
| `<namespace>/blocks/*.json` | Definições de blocos. [Blocos](#blocos) |
| `<namespace>/items/*.json` | Definições de itens. [Itens](#itens) |
| `<namespace>/fluids/*.json` | Fluidos, com um bloco e um balde. [Fluidos](#fluidos) |
| `<namespace>/materials/*.json` | Materiais de ferramentas e armaduras. [Materiais, abas, sons, tags](#materiais-abas-sons-tags) |
| `<namespace>/tabs/*.json` | Abas criativas. [Materiais, abas, sons, tags](#materiais-abas-sons-tags) |
| `<namespace>/sounds/*.json` | Eventos de som. [Materiais, abas, sons, tags](#materiais-abas-sons-tags) |
| `<namespace>/biomes/*.json` | Definições de biomas. [Biomas](#biomas) |
| `<namespace>/worldgen/*.json` | O que é gerado e onde. [Entradas de worldgen](#entradas-de-worldgen) |
| `<namespace>/caveregions/*.json` | Regiões nomeadas pintadas sobre o subsolo. [Regiões de cavernas](#regiões-de-cavernas) |
| `<namespace>/dimensions/*.json` | Definições de dimensões. [Dimensões](#dimensões) |
| `<namespace>/worldtemplates/*.json` | As configurações de um mundo inteiro em um arquivo. [Modelos de mundo](#modelos-de-mundo) |
| `<namespace>/worldintro/*.json` | Páginas exibidas quando um jogador entra no mundo. [Introdução do mundo](#introdução-do-mundo) |
| `<namespace>/gates/*.json` | Condições para portais e dimensões. [Portais e passagens](#portais-e-passagens) |
| `<namespace>/gamerules/*.json` | Regras do jogo para mundos novos. [Regras do jogo](#regras-do-jogo) |
| `<namespace>/teams/*.json` | Lados no placar do vanilla e o que se junta a eles. [Equipes](#equipes) |
| `<namespace>/scoring/*.json` | Objetivos, pontos e como uma partida termina. [Pontuação](#pontuação) |
| `<namespace>/raids/*.json` | Ondas que vão atrás de uma vila quando um jogador leva um presságio até ela. [Invasões](#invasões) |
| `<namespace>/entities/*.json` | Variantes de entidades construídas sobre entidades que já existem. [Variantes de entidades](#variantes-de-entidades) |
| `<namespace>/hardness/*.json` | Tempo de mineração e multiplicadores de explosão para grupos de blocos. [Grupos de dureza](#grupos-de-dureza) |
| `<namespace>/anvils/*.json` | Encantamentos que uma bigorna coloca em um item nomeado, um avanço que ele concede e um bloqueio até lá. [Trabalho na bigorna](#trabalho-na-bigorna) |
| `<namespace>/cards/*.json` | Cartas na tela exibidas por um gatilho e as mensagens que este mod diz por conta própria. [Cartas](#cartas) |
| `<namespace>/dice/*.json` | Dados do pacote com faces ponderadas, baralhos de cartas, quem ouve uma rolagem e o texto dos resultados. [Dados e baralhos](#dados-e-baralhos) |
| `<namespace>/games/*.json` | Jogos de tabuleiro com criaturas como peças: o tabuleiro, as peças e como se movem, e o que um resultado paga. [Jogos de tabuleiro](#jogos-de-tabuleiro) |
| `<namespace>/orders/*.json` | Trabalhos que variantes de entidade fazem para um jogador em um baú. [Ordens de trabalho](#ordens-de-trabalho) |
| `<namespace>/exposures/*.json` | Perigos que expõem jogadores perto de blocos nomeados, carregando itens nomeados ou em dimensões nomeadas. [Exposições](#exposições) |
| `<namespace>/overrides/<target>/<name>.json` | Propriedades de blocos, itens e tipos de poção existentes, alteradas no lugar. [Substituições de propriedades](#substituições-de-propriedades) |
| `<namespace>/villages/*.json` | Terrenos que uma cidade ou vila pode construir. [Terrenos de vilas](#terrenos-de-vilas) |
| `<namespace>/pathintersects/*.json` | Desenhos pintados onde as estradas das vilas se cruzam. [Estradas de vilas](#estradas-de-vilas) |
| `<namespace>/structuremaps/*.json` | Modelos compostos em uma grande construção sobre uma grade. [Mapas de estruturas](#mapas-de-estruturas) |
| `<namespace>/citymaps/*.json` | Uma planta de ruas desenhada a partir da qual uma cidade é montada, em vez de sortear uma. [Mapas de layout de cidades](#mapas-de-layout-de-cidades) |
| `<namespace>/portalframes/*.json` | Molduras que um jogador pode construir e acender. [Molduras de portais](#molduras-de-portais) |
| `<namespace>/blastplaster/*.json` | O que o Blast Plaster faz depois de uma explosão, por dimensão. [Integração com Blast Plaster](#integração-com-blast-plaster) |
| `<namespace>/structures/*.nbt` | Modelos, para mudas, `imprint` e substituições de mods. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/recipes/*.json` | Receitas de criação, adicionadas ou substituídas. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/recipe_removals/*.json` | Receitas removidas por nome, namespace ou resultado. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/disabled/*.json` | Blocos e itens tirados de jogo. [Blocos e itens desativados](#blocos-e-itens-desativados) |
| `<namespace>/furnace/*.json` | Receitas de fornalha adicionadas e removidas. [Receitas de fornalha e combustíveis](#receitas-de-fornalha-e-combustíveis) |
| `<namespace>/fuels/*.json` | Tempos de queima. [Receitas de fornalha e combustíveis](#receitas-de-fornalha-e-combustíveis) |
| `<namespace>/brewing/*.json` | Receitas do suporte de poções. [Poções, tipos de poção e fermentação](#poções-tipos-de-poção-e-fermentação) |
| `<namespace>/potions/*.json` | Efeitos de poções. [Poções, tipos de poção e fermentação](#poções-tipos-de-poção-e-fermentação) |
| `<namespace>/potion_types/*.json` | Poções engarrafadas construídas a partir desses efeitos. [Poções, tipos de poção e fermentação](#poções-tipos-de-poção-e-fermentação) |
| `<namespace>/villagers/*.json` | Profissões de aldeões. [Aldeões e trocas](#aldeões-e-trocas) |
| `<namespace>/trades/*.json` | O que as profissões compram e vendem. [Aldeões e trocas](#aldeões-e-trocas) |
| `<namespace>/loot_tables/*.json` | Tabelas de saque, substituídas. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/loot_injections/*.json` | Um grupo adicionado a uma tabela que já existe. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/block_drops/*.json` | Drops extras ou substitutos para blocos que um pacote não possui. [Drops de blocos](#drops-de-blocos) |
| `<namespace>/player_loot/*.json` | Uma tabela de saque sorteada quando um jogador morre. [Saque de jogadores](#saque-de-jogadores) |
| `<namespace>/advancements/*.json` | Avanços. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/functions/*.mcfunction` | Arquivos de função. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/tags/<kind>/*.json` | Tags, no formato do próprio jogo. [Materiais, abas, sons, tags](#materiais-abas-sons-tags) |
| `<namespace>/registry_remap/*.json` | Nomes antigos mapeados para novos. [Renomeações de registro](#renomeações-de-registro) |

Em `assets/`:

| Caminho | O que contém |
| --- | --- |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | As pastas de assets de sempre. [Modelos, blockstates e texturas](#modelos-blockstates-e-texturas) |
| `<namespace>/sounds.json` | O índice de sons que o jogo lê, ao lado das definições de `sounds/` em `data/` |
| `<namespace>/texts/*.txt` | Arquivos de texto simples, usados pela introdução do mundo. [Introdução do mundo](#introdução-do-mundo) |

A **1.21.1** nomeia as pastas de dados do vanilla no singular: `loot_table/`, `recipe/`, `advancement/`, `function/`, `structure/`, `tags/item/`, `tags/block/`. Um pacote pode usar qualquer das grafias ali; os nomes no plural acima são lidos como seus gêmeos no singular, então um mesmo pacote serve às duas versões.

## Lendo as tabelas

*primeiros passos*

Todo arquivo é JSON padrão. Uma entrada de worldgen representativa:

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

As tabelas de chaves neste documento informam se uma chave é obrigatória, o que ela guarda e o padrão quando omitida. Valores não reconhecidos são registrados no log e substituídos pelo padrão; não derrubam o jogo. Tipos de valor usados em todo o guia:

| Quando uma tabela diz | Você escreve |
| --- | --- |
| int | `8` |
| int, ticks | `100` (20 ticks = 1 segundo) |
| int ou intervalo | `8`, ou `{ "min": 4, "max": 12 }` para sortear entre eles |
| 0 a 15, 1 a 100 e similares | um int dentro desses limites |
| float | `0.5` |
| boolean | `true` ou `false` |
| string | `"palavras entre aspas"` |
| nome de bloco, nome de item | `"minecraft:stone"`. Um estado de bloco é o nome com `properties` ao lado: `{ "block": "minecraft:oak_log", "properties": { "axis": "x" } }` |
| `namespace:name` | `"mypack:ruby_ore"` |
| nome de bioma, nome de som, nome de aba | a mesma forma `namespace:name` entre aspas |
| id de dimensão | `"minecraft:overworld"`, `"minecraft:the_nether"`, `"minecraft:the_end"` ou o próprio `"mypack:verdant"` de um pacote. Os números `0`, `-1` e `1` da 1.12.2 ainda são aceitos como essas três |
| cor hexadecimal | seis dígitos hexadecimais, `"A0C8FF"`, `#` opcional |
| caminho de textura | `"mypack:block/ruby_ore"` |
| lista de ints | `[4, 12]` |
| lista de nomes de blocos | `["minecraft:stone", "minecraft:andesite"]` |
| lista de nomes de biomas | `["minecraft:windswept_hills", "mypack:ruby_hills"]` |
| lista de tipos de bioma | `["mountain", "forest"]`, as palavras de tipo listadas em [Listas de valores](#listas-de-valores), cada uma representando uma tag de bioma |
| lista de ids de mods ou namespaces de pacotes | `["quark", "mypack"]` |
| lista de objetos | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, chaves conforme a tabela do próprio objeto |
| objeto | `{ "type": "cluster" }`, chaves conforme a sua própria tabela |
| objeto de função para bioma, de nome de variante para variante | as chaves são o primeiro item, os valores o segundo: `{ "ocean": "mypack:ruby_ocean" }` |

A maioria das definições também aceita `requires`, uma lista de ids de mods ou namespaces de pacotes que precisam estar presentes, ou o arquivo é ignorado.

## A única regra

*primeiros passos*

Abra o jar, encontre o arquivo que você quer alterar e copie o caminho dele de `assets` ou `data` em diante:

```
assets/minecraft/textures/block/iron_ore.png                 (in the Minecraft jar)
rdploader/assets/minecraft/textures/block/iron_ore.png       (your override)

data/minecraft/loot_tables/blocks/iron_ore.json              (in the Minecraft jar)
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    (your override)
```

O caminho depois de `assets` ou `data` é sempre idêntico ao caminho dentro do jar. Nada é renomeado ou movido.

## Organizando pacotes

*primeiros passos*

Arquivos soltos funcionam em `rdploader/assets/<namespace>/` e `rdploader/data/<namespace>/`. Agrupar também funciona, como um zip. Uma pasta em `rdploader` nunca é um pacote: ela é ignorada com um aviso no log, então compacte o pacote em zip antes de colocá-lo ali. Ao compactar, selecione o conteúdo e compacte isso, não a pasta que o contém: um zip cujo nível superior é uma pasta envolvendo `assets` ou `data` é ignorado, e o log avisa.

```
rdploader/assets/minecraft/textures/block/iron_ore.png
rdploader/MyTextures.zip
```

**Pacotes na pasta errada.** Na inicialização, antes de ler `rdploader`, o RDPL vasculha a pasta `resourcepacks` do jogo e a pasta `datapacks` de cada mundo (em um servidor dedicado, o mundo que `level-name` nomeia) e move para `rdploader` todo zip de pacote RDPL que encontrar. Um zip é um pacote RDPL quando contém arquivos de definição do RDPL, como `data/<namespace>/blocks/` ou, em um pacote da 1.12.2, `assets/<namespace>/blocks/`. Um pacote de recursos ou de dados comum fica onde está. Um zip cujo nome `rdploader` já contém é deixado no lugar, e o mesmo vale para um pacote RDPL em forma de pasta; os dois geram um aviso. Cada movimentação é registrada em `logs/rdpl.log`. O jogo remove sozinho um pacote movido da lista de pacotes de recursos ou dos pacotes de dados do mundo, e o RDPL passa a carregá-lo de `rdploader`.

**Prioridade.** Quando dois pacotes contêm o mesmo arquivo, prefixe os nomes com `RDPL` e um número; números maiores carregam depois e vencem:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Sem diferenciar maiúsculas de minúsculas; um espaço, hífen ou sublinhado depois do número é opcional; o prefixo fica oculto no nome exibido. Um pacote sem prefixo carrega primeiro e perde para qualquer pacote numerado. A prioridade também ordena as entradas de worldgen, o que importa quando um pacote assenta blocos que outro substitui.

**Desative um pacote** acrescentando `.disabled` ao nome dele.

**Um zip para todas as versões.** Um zip pode trazer uma pasta `versions/<version>/` para cada versão do Minecraft que atende: `versions/1.12.2/`, `versions/1.20.1/`, `versions/1.21.1/` e, na 26.x, a versão exata em que roda, `versions/26.1.2/`, `versions/26.2/` ou `versions/26.3/`. Cada uma é organizada como a raiz de um pacote daquela versão, `pack.mcmeta` incluído. Um arquivo na pasta da versão em execução é lido no lugar do mesmo caminho na raiz; a raiz é compartilhada por todas as versões, e a pasta de outra versão nunca é lida. Coloque na raiz o que todas as versões leem igual e só o que difere em uma pasta de versão, e um zip carrega nas quatro.

**Pacotes são convertidos entre versões.** Ao carregar um pacote escrito para outra linha, ele é convertido na primeira vez, do mesmo modo, gravando o que mudou na própria pasta `versions/<version>/` como acima; isso acontece automaticamente no carregamento, inclusive no que um `/rdpl reload` ou `/rdplserver reload` dispara, nunca por um comando próprio. Todo par de versões converte nos dois sentidos, então um pacote da 1.12.2, 1.20.1, 1.21.1 ou 26.x carrega em qualquer das outras. Um pacote da 26.3 também carrega em todas as linhas, lido pelo formato da 26.2 nas mais antigas, e um pacote mais antigo carrega na 26.3. O que um lado tem e o outro não consegue guardar é descartado, e o log nomeia cada descarte: ao descer da 26.3, entre outros, as funções de densidade de depuração, a exclusão de aquífero e o nível de superfície próprios de um pacote e os comandos que a 26.2 não tem; ao subir para a 26.3, o `depth` e o `offset` de um alvo de spawn e as chaves de veios de minério do noise router. [Chaves que não foram mantidas](#chaves-que-não-foram-mantidas) lista todos.

Um `pack.mcmeta` na raiz do zip é bem-vindo, mas não necessário: o mod apresenta todos os pacotes ao jogo sob uma única entrada própria, com o formato de pacote que o jogo espera, então um pacote nunca fica defasado por causa de um número de formato. Coloque um `pack.png` ao lado dele para dar um ícone à entrada da pasta. Sem um, a entrada mostra o ícone do RDPL.

## Pacotes de recursos: quem vence

*primeiros passos*

Por padrão, os arquivos do RDPL ficam acima dos pacotes de recursos que um jogador seleciona, então um pacote de recursos não pode substituí-los. Acrescente `O` ou `N` depois do prefixo `RDPL` para decidir por pacote:

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Pacotes sem letra seguem a opção de configuração `overrideResourcePacks`. `/rdpl list` marca os pacotes que substituem. A letra precisa terminar o prefixo (seguida de espaço, hífen, sublinhado ou nada), então `RDPLOverhaul` é um pacote chamado `Overhaul`, não uma flag `O`.

Os mesmos níveis valem para pacotes de dados. Um pacote marcado com `N` fica abaixo dos pacotes de dados que um mundo carrega na própria pasta `datapacks`, e um marcado com `O` fica acima deles.

---

# Como os pacotes funcionam

## Como as definições funcionam

*como os pacotes funcionam*

Além das pastas que substituem arquivos, há pastas que descrevem coisas novas. Um arquivo de definição agrupa uma ou mais coisas de um tipo em `variants`, e cada chave dentro de `variants` é um nome de registro: `data/mypack/blocks/ore.json` contendo uma variante chamada `ruby_ore` registra `mypack:ruby_ore`. O nome do arquivo em si é só um agrupamento; um arquivo pode conter um bloco ou uma dúzia que compartilham as mesmas configurações.

O registro acontece na menor prioridade que o loader oferece, então, se um mod de verdade registrar o mesmo nome, o mod vence e o seu arquivo é ignorado. Nada aqui pode substituir um mod.

**Onde fica o limite.** Tudo o que precisar de uma entidade de bloco própria, uma tela, um inventário ou uma lógica por tick própria exige um mod de verdade, com uma exceção: o tipo [contêiner](#contêineres), que traz um inventário e uma tela próprios. Tudo aquém disso é permitido.

### Seu namespace é o seu mod

*como as definições funcionam*

O namespace que você escolhe é, para todos os efeitos práticos, um id de mod. Nada é carregado como mod e ele nunca aparece na lista de mods, mas tudo que lê um id de mod lê o seu:

- Os nomes de registro são `mypack:ruby_ore`, exatamente como os de um mod seriam, e são gravados em todo mundo salvo que os contenha.
- As listas de permissão de minérios, biomas e receitas na config o reconhecem, então `oreWhitelist = mypack` mantém o seu minério e os blocos de todos os outros.
- `/rdpl which`, `/rdplserver oregen` e os relatórios agrupam todos por ele.
- JEI, tags e as consultas de outros mods o enxergam do mesmo jeito.

Portanto, escolha um nome no início e nunca o mude. Renomear um namespace deixa órfão tudo o que já foi colocado em um mundo, igual a um mod que muda de id; é para reparar isso que `registry_remap` existe.

Isso vale nos dois sentidos: `requires` aceita um namespace de pacote tão bem quanto o id de um mod instalado, então um pacote pode depender de outro e ser ignorado quando ele não está instalado.

Um mod ou pacote nomeado em `requires` que não esteja instalado faz a definição ser ignorada: uma linha vai para `logs/rdpl.log` nomeando o que faltou, e o jogo segue em frente. Se um bloco que você esperava não está na aba criativa, essa linha do log é o primeiro lugar para olhar.

`requires` aceita apenas ids simples. Não há sintaxe de faixa de versões, então ele pode dizer que um mod precisa estar presente, mas não qual versão.

O id do próprio mod, `resourcedatapackloader`, é reservado. Definir conteúdo sob ele é ignorado e registrado no log, porque reivindicaria a posse de coisas que este mod registra. Substituir os assets do próprio mod continua permitido; só registrar conteúdo ali não é.

Toda tabela abaixo segue as convenções de [Lendo as tabelas](#lendo-as-tabelas).

A maioria das definições também aceita `requires`, uma lista de ids de mods ou namespaces de pacotes que precisam estar presentes, ou o arquivo é ignorado.

### Opções de pacotes

*como as definições funcionam*

Todas as chaves que um arquivo de opções aceita:

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| um nome de opção | sim | boolean ou um objeto | | `true` ou `false` é o padrão da opção. Um objeto traz as três chaves abaixo |
| `hide` no nível superior | não | boolean | `false` | Mantém as opções deste pacote fora da tela de opções e do arquivo gerado por completo, enquanto elas ainda controlam conteúdo em seus padrões |
| `default` | sim | boolean | | O valor da opção até o usuário alterá-lo. Um objeto sem um `default` booleano é ignorado, com um aviso |
| `hide` dentro de uma opção | não | boolean | `false` | Oculta só essa opção, que então não pode ser alternada e fica no padrão |
| `description` | não | string | nenhuma | Exibida sob o nome da opção na tela de opções |

Um pacote pode trazer uma pasta `config` ao lado de `assets` e `data`, com arquivos JSON de opções verdadeiro/falso e seus padrões:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Um arquivo com `"hide": true` no nível superior mantém as opções desse pacote fora da tela de opções e do arquivo gerado por completo, enquanto as opções ainda controlam conteúdo em seus padrões. Duas situações pedem isso: conteúdo que não está pronto para ser lançado e pacotes de modelo, onde as opções são engrenagem que mantém as definições juntas e não uma escolha que alguém deva fazer. Remova a chave para publicá-los. O mesmo vale por opção: `"hide": true` dentro do objeto de uma opção oculta só essa, então um pacote finalizado pode carregar um interruptor para conteúdo inacabado, ou uma porta de modelo, sem que nenhum dos dois apareça:

    { "enablePackB": { "default": false, "hide": true } }

Como uma opção oculta não pode ser alternada, uma oculta com o padrão true fica efetivamente forçada como ligada, para conteúdo que precisa continuar ligado à engrenagem de opções mas não é uma escolha.

Uma opção também pode ser um objeto com uma descrição, exibida sob o nome dela na tela de opções:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

Na inicialização, os arquivos de opções do pacote viram um arquivo de config de verdade que pertence ao usuário, nomeado a partir do pacote, `rdploader/config/PackA.json`, criado com os padrões do pacote e mesclado nas atualizações do pacote, de modo que opções novas chegam sem tocar no que o usuário já definiu. As alterações valem na próxima inicialização do jogo, e o botão Opções do pacote nas telas de seleção de mundo e de criação de mundo é onde um jogador as alterna. As opções pertencem apenas a pacotes nomeados, isto é, zips, já que o arquivo gerado leva o nome do pacote; arquivos soltos em `rdploader/assets` e `rdploader/data` não têm nome de pacote e não trazem opções, então compacte conteúdo solto em um pacote nomeado se ele precisar de um interruptor.

A lista `requires` de qualquer definição pode então nomear uma opção com uma entrada `config:`: `"requires": ["config:enableTestingContent"]` registra esse conteúdo só enquanto a opção for true, exatamente como um mod ausente o faria ser ignorado. Um nome simples verifica o arquivo de todos os pacotes e todo pacote que o define precisa concordar; `"config:PackA:enableTestingContent"` nomeia um pacote. Uma opção que nenhum pacote define conta como false e gera um aviso uma única vez.

Uma opção que controla algo com que um mundo foi criado é lembrada por esse mundo, registrada de novo toda vez que o mundo é salvo. Altere-a e abra o mundo de novo, e, quando a mudança deixa conteúdo que o mundo contém sem registro, é feito antes um backup do mundo, na pasta `backups` do próprio jogo, exatamente como a tela Editar mundo faz; se esse backup falhar, o mundo não é aberto. O primeiro jogador na Superfície é informado de quais opções mudaram e, quando houve, de que a cópia foi feita.

Uma entrada `file:` controla por um arquivo ou pasta existente dentro da pasta do jogo, para acoplar conteúdo a algo fora dos pacotes do próprio RDPL, como o pacote de recursos de outro mod: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registra o conteúdo só enquanto esse arquivo exato estiver instalado. O caminho é relativo à pasta do jogo, sempre com barras normais, e não pode conter `..`.

### Herdando definições

*como as definições funcionam*

Uma definição de bloco ou item pode partir de outra do mesmo tipo com `"inherits"`, nomeando o nome de registro de qualquer variante, e então substituir o que difere:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

O filho copia todas as estatísticas do arquivo do pai e da variante nomeada, a ordem dos arquivos nunca importa, as cadeias são resolvidas começando pelo pai, e um ciclo ou um pai ausente é registrado no log e deixa o filho como foi escrito. Os campos que o filho escreve substituem o valor herdado; as propriedades aninhadas das variantes são substituídas uma a uma, mas listas como `requires` são substituídas por inteiro, então escreva a lista completa que quiser. Blocos herdam apenas de blocos e itens apenas de itens.

### Modelos de blocos e itens

*como as definições funcionam*

Um pai pode ser um modelo puro que nunca entra no jogo, já que a herança lê os próprios arquivos de definição, não o que foi registrado. Proteja o modelo com uma opção oculta forçada como desligada, e ele não registra nada enquanto suas estatísticas continuam herdáveis:

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

O modelo nunca é registrado, enquanto `jacks_ore` é registrado com o material, o som, a ferramenta, a aba, os drops de experiência e a resistência do modelo, substituindo apenas a dureza. O filho precisa escrever o próprio `requires`, aqui esvaziado para uma lista vazia, porque, do contrário, herda o do pai e sumiria junto com ele.

## O que você pode substituir

*como os pacotes funcionam*

- **Qualquer coisa na pasta de assets de um mod**, texturas, modelos, blockstates, arquivos de idioma, sons, fontes, textos de splash, livros-guia, manuais
- **Avanços, tabelas de saque, tags e funções**, do lado do servidor, então funcionam também em servidores dedicados
- **Receitas**, substitua a receita de um mod ou adicione as suas
- **Modelos de estrutura**, os arquivos `.nbt` que os mods usam para construções geradas, em `<namespace>/structures/`
- **Renomeações de registro**, mantenha mundos antigos funcionando quando um mod renomeia um bloco ou item
- **Remoções de receitas**, apague uma receita de criação por nome, namespace ou resultado
- **Blocos e itens desativados**, tire qualquer bloco ou item de jogo, veja [Blocos e itens desativados](#blocos-e-itens-desativados)
- **Injeções de saque**, adicione um grupo a uma tabela de saque em vez de substituí-la inteira
- **Drops de blocos**, acrescente ou substitua o que qualquer bloco solta ao ser quebrado, experiência incluída
- **Saque de jogadores**, sorteie uma tabela de saque quando um jogador morre, além do que ele carregava ou no lugar disso
- **Propriedades de blocos, itens e poções existentes**, dureza, luz, tamanhos de pilha, comida em qualquer coisa, os efeitos de uma poção, veja [Substituições de propriedades](#substituições-de-propriedades)
- **Receitas de fornalha, tempos de queima de combustíveis, abas criativas e eventos de som**

O que um bloco solta é a tabela de saque dele nesta versão: para mudar o que a pedra solta, inclua `data/minecraft/loot_tables/blocks/stone.json` e, para acrescentar sem substituir, uma injeção de saque ou uma regra de [drops de blocos](#drops-de-blocos), que também pode dar experiência.

O RDPL serve para substituir uma ou duas receitas, e as receitas do seu próprio conteúdo devem ser adicionadas no pacote ao lado dele. Para controle total de receitas em um modpack, KubeJS e CraftTweaker são as melhores opções, e um arquivo aqui ainda substitui o original por completo, então, para mudar um ingrediente ou descartar uma entrada de saque, use essas ferramentas.

## Pacotes do lado do servidor

*como os pacotes funcionam*

Um pacote pode viver só no servidor, com jogadores em clientes vanilla puros, sob uma restrição: **nada nele pode registrar coisa alguma**. O mod aceita qualquer remoto; o pacote decide. Um cliente vanilla joga com os registros com que veio, então um pacote que acrescenta a eles precisa estar dos dois lados.

| O servidor sozinho basta | Precisa do pacote também no cliente |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `structuremaps`, `citymaps`, `villages`, `pathintersects`, `caveregions`, `biomes`, `dimensions` | `blocks`, `items`, `fluids`, `materials`, `containers` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `anvils`, `tags`, `disabled` | `potions`, `potion_types`, `sounds`, `tabs`, `exposures` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `entities`, `villagers`, `portalframes` |
| `gates`, `cards`, `trades`, `registry_remap`, `teams`, `scoring`, `raids`, `hardness`, `blastplaster` | `models`, `blockstates`, `textures`, `lang`, `worldintro`, `overrides` (pastas de cliente: sem cliente, deixe-as de fora) |
| toda a camada de controle, as configurações e a pré-geração | |

A coluna da direita é um limite rígido: blocos, itens, tipos de entidade, sons e efeitos de poção que um cliente vanilla não tem não podem ser descritos a ele, e o portal próprio de uma dimensão é um dos blocos do pacote; as exposições só carregam junto com esse conteúdo. A coluna da esquerda funciona porque tudo ali ou roda inteiramente no servidor, ou chega ao cliente como entradas de pacote de dados que o vanilla já lê (biomas, regiões de cavernas, tipos de dimensão), ou chega por pacotes de rede que o vanilla já entende (o espaço de resultado da criação preenchido pelo servidor, pacotes de avanço comuns, recusas de portais em mensagens de status e uma retenção de pré-geração feita de pacotes vanilla de modo de jogo/título/teletransporte).

Configuração:

1. Ative `vanillaClients` na config (categoria `content`, exige reinício). Ele impõe a coluna da direita: essas pastas são ignoradas no carregamento e cada arquivo ignorado é nomeado no log, então um arquivo de bloco que escapou vira uma linha de log em vez de uma conexão recusada.
2. Mantenha as definições fora das pastas da direita mesmo assim; arquivos ignorados são peso morto. Onde o pacote referencia itens (o `hold` de um portal, `killedDrops`, resultados de receitas, trocas), nomeie apenas itens que o vanilla ou os outros mods dos dois lados do servidor forneçam. Um bioma que nomeia os blocos de solo do próprio pacote mantém o solo do bioma base, e uma dimensão aberta pelo próprio portal precisa desse bloco de portal, então envie os jogadores para lá por comando.
3. As variantes de entidades são tipos de entidade próprios nesta versão, então pertencem à coluna da direita: com `vanillaClients` ligado elas são ignoradas, os spawns delas também, e o log as nomeia.
4. Instale no servidor como de costume, com o Blast Plaster, que o mod exige e que também não registra nada. Nada vai para as máquinas dos jogadores; `/rdpl` não existirá para eles.
5. Teste com uma entrada limpa de um cliente vanilla da mesma versão. As falhas são ruidosas: a conexão é recusada na porta, não quebra silenciosamente depois.
6. Duas lacunas cosméticas aceitas: as receitas adicionadas pelo servidor funcionam, mas não aparecem no livro de receitas, e a retenção enquanto o terreno é criado é uma retenção simples de espectador com o progresso na barra de ação, sem a neblina e o logo que o próprio cliente do mod desenha.

## Renomeações de registro

*como os pacotes funcionam*

`<namespace>/registry_remap/*.json`

O nome do arquivo é você quem escolhe, só a pasta é lida, e vários arquivos se acumulam.

Quando um mod renomeia um de seus blocos ou itens, os mundos salvos antes da renomeação os perdem. Coloque um arquivo aqui para mapear o nome antigo para o novo:

```json
{
  "registry": "minecraft:item",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

O registro é aquele a que a entrada pertence, nomeado como o jogo o nomeia: `minecraft:item`, `minecraft:block`, `minecraft:entity_type` e assim por diante. As renomeações se encadeiam, então mapear A para B e depois B para C leva A direto a C.

## API de mods

*como os pacotes funcionam*

Um mod pode distribuir conteúdo RDPL dentro do próprio jar, então não precisa de um pacote separado. Coloque uma pasta chamada `rdploader` na raiz do jar e organize-a exatamente como um pacote:

```
thatmod.jar
  META-INF/mods.toml                (1.21.1: META-INF/neoforge.mods.toml)
  rdploader/data/thatmod/blocks/ruby_ore.json
  rdploader/assets/thatmod/textures/block/ruby_ore.png
```

O que um mod distribui é um padrão, não uma substituição. Ele carrega abaixo de todo pacote da pasta de pacotes, então qualquer coisa que um autor de pacote escreva vence, e um mod só pode fornecer arquivos sob um namespace que declare no próprio arquivo de mods. Arquivos sob qualquer outro namespace são ignorados com um aviso, e o mesmo vale para uma pasta `rdploader` aninhada dentro de um namespace, então um mod não pode redefinir discretamente o conteúdo de outro mod ou de um autor de pacote.

Todo mod que distribui uma ganha uma entrada em `rdploader/config/mods.json` na primeira vez que é visto:

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| Campo | Valores | Padrão | O que faz |
| --- | --- | --- | --- |
| `enabled` | `true` ou `false` | `true` | Desliga o conteúdo desse mod, do jeito que `.disabled` desliga um pacote |
| `priority` | `-1` ou um número | `-1` | `-1` mantém o mod abaixo de todo pacote; qualquer outro número o coloca na ordem de [prioridade](#organizando-pacotes) comum ao lado dos pacotes numerados |

Um pacote de mod nunca entra no nível de substituição de pacotes de recursos, seja qual for o valor de `overrideResourcePacks`, já que só um autor de pacote pode pedir isso com a letra `O`. O log marca os pacotes de mods e lista os pacotes do menor para o maior, então nada carrega sem ser visto.

## Pacotes escritos para 1.12.2

*como os pacotes funcionam*

Um pacote feito para a linha 1.12.2 carrega como está. O loader reconhece um pelo formato do `pack.mcmeta`, por pastas de definição em `assets/` sem `data/` ao lado, ou por um arquivo `.lang`, e o leva adiante. Um zip é convertido uma vez, dentro de si mesmo: todo arquivo que esta versão lê de modo diferente é gravado na pasta `versions/1.20.1/` do zip (1.21.1: `versions/1.21.1/`) com tudo abaixo já feito, e os arquivos da 1.12.2 na raiz ficam como estavam, então o mesmo zip ainda carrega na 1.12.2, como descreve [um zip para todas as versões](#organizando-pacotes). A conversão marca a pasta que grava com um arquivo `port.stamp` contendo a versão do RDPL. Um zip que já tem a pasta desta versão é lido por ela; quando o `port.stamp` nomeia outra versão do RDPL, a conversão grava a pasta de novo, substituindo todos os arquivos nela e nomeando cada um no log, e uma pasta sem `port.stamp`, como uma que o autor do pacote escreveu, nunca é convertida de novo. Os arquivos da 1.12.2 na raiz que a conversão substituiu, como definições em `assets/`, arquivos `.lang`, blockstates e modelos da 1.12.2 e texturas em `textures/blocks/` e `textures/items/`, não são lidos nesta versão; só os arquivos da raiz que a conversão deixa passar sem alterar, como sons, ainda são. O zip é gravado primeiro em um arquivo temporário e só substitui o original quando está completo. Arquivos soltos em `rdploader/assets` não são reescritos; eles são lidos pela mesma conversão toda vez que a pasta é varrida.

- As pastas de definição passam de `assets/<namespace>/` para `data/<namespace>/`, e as pastas de dados do vanilla com elas: receitas, tabelas de saque, injeções de saque, avanços, funções e estruturas, renomeadas para os nomes no singular que a 1.21.1 lê (`recipe`, `loot_table`, `advancement`, `function`, `structure`, `tags/item`).
- `textures/blocks/` e `textures/items/` são servidas como `textures/block/` e `textures/item/`, nos modelos, nos mapas de pixels e nos próprios arquivos. Um modelo de item em `models/item/<file>/<variant>.json` é servido como `models/item/<variant>.json`.
- Todo id com metadados, `minecraft:wool:14` ou `minecraft:dye:4`, passa pelos data fixers do próprio jogo, o mesmo código que atualiza um mundo da 1.12.2, então sai como o bloco ou item em que se tornou: `minecraft:red_wool`, `minecraft:lapis_lazuli`. Um estado de bloco que sobreviveu à flattening como propriedade, `minecraft:log:1` para `minecraft:oak_log` com `axis=y`, sai como um objeto `properties`. Os ids do próprio pacote são resolvidos pelas suas definições: `mypack:materials:5` vira a variante cujo `meta` era 5, e `mypack:ruby_ore` a primeira variante do arquivo, já que cada variante é um bloco próprio aqui. Nomes de entidades e biomas são corrigidos do mesmo modo, e números de dimensão viram ids.
- `variants` mantêm suas chaves; `meta` é descartado e `oreDict` vira `tags` pelo mapeamento do dicionário de minérios para as tags de convenção. Um arquivo `oredict/*.json` vira um arquivo de tag de item para cada nome ao qual acrescenta ou do qual remove: uma remoção `-name` cai na lista `remove` da tag, e remover `*` substitui a tag. Um `creativeTab` simples assume o namespace do pacote, e um rótulo de aba do vanilla da 1.12.2 como `misc` vira a aba vanilla mais próxima.
- Um arquivo `.lang` é servido como o `.json` que o jogo lê, com `tile.mypack:file.variant.name` como `block.mypack.variant`, `item.` do mesmo jeito, `itemGroup.x` como `itemGroup.mypack.x`, `fluid.x` como as duas chaves de fluido e todo o resto como escrito.
- Um blockstate da 1.12.2 não é servido de forma alguma. Em vez dele, as texturas são lidas e servidas com os nomes que o gerador procura, `textures/block/<variant>.png` com `_top` e `_bottom` onde o blockstate tinha `end`, `top` ou `bottom`, de modo que o blockstate e os modelos são gerados para cada variante como seriam para um pacote escrito aqui.
- As receitas perdem o `data` e ganham ids achatados, `forge:ore_shaped` vira `minecraft:crafting_shaped` com ingredientes `ore` como `tag`, as tabelas de saque perdem `set_data` do mesmo modo, e o `item` com `data` de um avanço vira `items`. O `background` de um avanço passa de `textures/blocks/` para `textures/block/`, e o ícone dele nomeia o item como `id`.
- O vocabulário Forge da 1.12.2 de uma receita também é levado: um tipo de ingrediente `forge:ore_dict` ou `minecraft:item` é descartado, um ingrediente `minecraft:item_nbt` vira `forge:nbt` (1.21.1: `neoforge:components`) com seu nbt passado pelos data fixers, `minecraft:item_exists` vira `forge:item_exists` (1.21.1: toda condição assume o nome `neoforge`, sob `neoforge:conditions`), um item sem namespace assume o da receita, e `data` 32767 vira uma lista de todas as variantes. Uma `#CONSTANT` de um `_constants.json` de mod não pode acompanhar, e o log a nomeia. Onde quer que uma lista nomeie itens, `name:*` vira todas as variantes que o item tinha, e um valor único assume a primeira; isso alcança os resultados de `recipe_removals` e as remoções de `furnace`, que também são levados como itens. O `item` ou `with` de uma bigorna escrito como `name:*` vira uma lista de todas as variantes, e qualquer uma delas atende.
- As tabelas de saque renomeadas desde a 1.12.2 são renomeadas onde quer que um pacote nomeie uma: um alvo de `loot_injections`, uma tabela de `player_loot` e uma entrada `loot_table` (1.21.1: seu `value`), então `minecraft:entities/zombie_pigman` vira `minecraft:entities/zombified_piglin`. `killed_by_player` com `inverse` vira uma condição `inverted`, `entity_properties` com `on_fire` vira um predicado `flags`, e nomes de `set_attributes` como `generic.maxHealth` viram `generic.max_health` (1.21.1: a operação assume o novo nome, e o `name` do modificador vira seu `id`).
- Uma substituição de um bloco da 1.12.2 que a flattening dividiu, como `overrides/minecraft/wool.json`, é lida como substituição de todo bloco em que ele se tornou, as dezesseis lãs, já que a 1.12.2 alterava todas as variantes de uma vez.
- O `gameLoopFunction` de um arquivo de regras do jogo vira a tag de função `#minecraft:tick`, gravada como `data/minecraft/tags/function/tick.json`, já que a regra do jogo não existe mais. Os números de dimensão do próprio pacote são lidos pelos seus arquivos `dimensions`, então `"id": 7` em `dimensions/verdant.json` faz de 7 `mypack:verdant` onde quer que o pacote o nomeie. Os dois lados de um par `villageBlocks` são corrigidos, a chance é mantida, e um arquivo `registry_remap` pode manter os plurais `minecraft:blocks` e `minecraft:items` da 1.12.2.
- Um modelo de mundo que desliga toda estrutura que a 1.12.2 tinha em uma dimensão desliga também as estruturas que só esta versão tem ali: `ancient_cities`, `buried_treasures`, `ocean_ruins`, `pillager_outposts`, `ruined_portals`, `shipwrecks`, `trail_ruins` e `trial_chambers` na Superfície e `nether_fossils` no Nether. Deixe um dos nomes da 1.12.2 ligado e elas são deixadas em paz. As chaves de controle de gerador, `blockWorldGenerators` e suas companheiras, são deixadas de fora de um modelo convertido com uma linha no log, já que nada aqui as lê. As camadas de `generatorOptions` de um modelo plano têm os nomes de blocos corrigidos do mesmo modo, então `minecraft:grass` vira `minecraft:grass_block`.
- As funções são reescritas linha por linha para a sintaxe de comandos desta versão. Ids com valores de dados passam pelos mesmos data fixers, então `give @p minecraft:wool 1 14` vira `give @p minecraft:red_wool 1` e `give @p mypack:materials 1 5` dá a variante cujo `meta` era 5, e o nbt de itens, entidades e blocos é corrigido como seria em um mundo (1.21.1: o nbt de um item vira seus componentes). `testforblock`, `testfor` e `scoreboard players test` viram `execute if`, `execute <entity> <x> <y> <z> [detect ...]` vira `execute as ... at @s [positioned ...] [if block ...] run`, `effect` assume `give` e `clear`, `blockdata`, `entitydata` e `replaceitem` viram `data merge` e `item replace`, e `scoreboard teams` e `scoreboard players tag` viram `team` e `tag`. Os seletores trocam `score_X_min` e `score_X` por `scores`, `r` e `rm` por `distance`, `l` e `lm` por `level`, `m` por `gamemode`, `c` por `limit` e `sort`, e `rx` e `ry` por `x_rotation` e `y_rotation`. Números de encantamentos e efeitos, nomes de partículas e sons, números de modo de jogo e dificuldade, uma duração de `weather` em segundos e um `tp` relativo de outra entidade também são levados. Uma linha que a conversão não consegue levar é mantida como escrita e o log nomeia o arquivo, a linha e o motivo; uma função com tal linha não carrega até ser corrigida à mão. `block_drops` é levado como está, com seu `meta` incorporado ao nome do bloco ou às suas `properties`.
- O piso de um mundo plano da 1.12.2 ficava em y 0, e esta versão monta um mundo plano a partir do fundo em y -64, então a conversão desloca as alturas para baixo junto. Onde o `worldType` de um modelo de mundo é `flat` ou `superflat`, seu `worldSpawn` e `resetSendsTo` descem 64 (até o `worldMinHeight` quando ele o nomeia), e o mesmo vale para as alturas de `spawn`, `standIn` `at` e `spawnBox` de uma equipe e para o `opens.lobby` de um arquivo de pontuação quando todo modelo de mundo que o pacote traz é plano. Uma dimensão de pacote com terreno `flat` desloca seu `groundLevel`, e qualquer posição que nomeie essa dimensão, do mesmo modo (pelo seu `minHeight` quando ela tem um). As funções não dizem onde rodam, então, quando a Superfície do pacote é plana, todo y absoluto nas suas funções desce 64, o `y` de um seletor incluído, e o log avisa uma vez; as alturas com `~` e `^` são deixadas em paz. Um mundo em terreno normal mantém todas as coordenadas, já que sua superfície continua no nível do mar.

O log traz uma linha de resumo por pacote convertido e uma linha para cada arquivo que moveu, deixou de fora ou não conseguiu levar, e toda chave que esta versão não lê mais ainda é nomeada pelo parser que a encontra. A conversão é um melhor esforço, não um pacote finalizado: leia essas linhas e termine à mão o que elas nomeiam, começando por qualquer linha de comando que ela manteve como escrita e qualquer textura para a qual não encontrou um nome. Faça essas correções na raiz do pacote ou em um zip separado, nunca na pasta `versions/` que a conversão gravou: outra versão do RDPL grava essa pasta de novo.

---

# Blocos e itens

## Blocos

*blocos e itens*

`<namespace>/blocks/*.json`

Cada chave dentro de `variants` é um bloco, registrado sob o namespace do pacote: um arquivo contendo `ruby_ore` e `deep_ruby_ore` registra `mypack:ruby_ore` e `mypack:deep_ruby_ore`, compartilhando toda configuração que o arquivo escreve fora de `variants`. O nome do arquivo em si é só um agrupamento.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve só as de que precisa. Uma chave marcada para um tipo é lida apenas por esse tipo.

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

*blocos*

| Tipo | O que você obtém |
| --- | --- |
| `basic` | Um bloco simples. Usado quando `type` está ausente |
| `ore` | Solta algo diferente de si mesmo, com Fortuna e Toque Suave |
| `falling` | Cai como areia ou cascalho |
| `slab` | Inferior, superior e dupla, e duas delas se fundem na mão |
| `stairs` | Cantos e rampas tratados para você |
| `fence` | Conecta-se aos vizinhos e a cercas de outros mods |
| `pane` | Conecta-se como os painéis de vidro |
| `wall` | Conecta-se como as muralhas de pedregulho, com o formato de poste |
| `door` | Duas de altura, abre à mão e responde à redstone |
| `trapdoor` | Uma aba articulada na parte superior ou inferior de um bloco, aberta à mão ou por redstone |
| `fence_gate` | Um portão em uma linha de cerca, aberto à mão ou por redstone, e rebaixado onde encontra uma muralha |
| `banner` | Um estandarte em um poste ou contra uma parede, dezesseis rotações em pé, com o seu próprio desenho |
| `ladder` | Escalável, colocada contra uma parede |
| `torch` | Posicionamento em parede e no chão, com uma partícula. Emite a `light` da variante como escrita, então uma tocha em `0` não emite nada |
| `bell` | Um sino como o das vilas do jogo: toca quando usado de lado, por redstone ou quando um projétil o atinge, balança em sua estrutura e faz saqueadores próximos brilharem. O blockstate dele guarda a direção e como ele está pendurado |
| `log` | Gira para a face contra a qual você o coloca e carrega a tag `minecraft:logs`, para que a derrubada de árvores e o Blast Plaster o tratem como tronco |
| `leaves` | Apodrece, é cortada com tesoura, recebe tonalidade e solta uma muda, e carrega a tag `minecraft:leaves`. Deixadas `opaque`, são desenhadas sólidas, como folhas rápidas; defina `"opaque": false` para ver através delas |
| `sapling` | Cresce em uma árvore ou em uma das suas estruturas |
| `crop` | Cresce por estágios, solta uma semente e um item de produção, sementes de trigo e trigo para o que o arquivo deixar de fora |
| `flower` | Uma planta de um bloco em pé sobre o solo |
| `cane` | Cresce para cima em uma coluna, como a cana-de-açúcar ou o cacto |
| `vine` | Sobe e pende nas laterais dos blocos. Com `growth`, cresce para baixo até `maxHeight` e, com `spread`, alcança os lados nas paredes vizinhas; sem ele, fica como foi colocada |
| `portal` | Envia quem entra para outra dimensão |
| `container` | Guarda um inventário que um jogador pode abrir, de qualquer tamanho, e pode se preencher a partir de uma tabela de saque na primeira vez que é aberto. É desenhado como um bloco comum ou como um baú, conforme o pacote pedir. Sem um objeto `container`, tem três fileiras de nove |

### Chaves de arquivo

*blocos*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `variants` | sim | objeto de nome de variante para variante | | Um bloco por entrada. A chave é o nome de registro dele e nomeia seu blockstate, seus modelos, suas texturas e sua chave de lang |
| `type` | não | um dos tipos acima | `basic` | Qual forma o bloco assume |
| `material` | não | um dos [materiais de bloco](#listas-de-valores) | `rock` | O bloco faz o que aquele material fazia na 1.12.2: se solta algo quando quebrado à mão, como os pistões o tratam, se a lava o incendeia, se um líquido em fluxo o leva embora e se um bloco colocado o substitui. Um `log` é sempre `wood`, `leaves` sempre `leaves`, uma `vine` `vine`, uma `torch` ou `ladder` `circuits`, uma `crop` `plants`, e `stairs` e uma `wall` se comportam como o seu `modelBlock` |
| `soundType` | não | um dos [tipos de som](#listas-de-valores) | `stone`; `wood` para um `log`, `plant` para `leaves` e uma `crop`, o do `modelBlock` para `stairs` e uma `wall` | Passos, quebra e colocação |
| `mapColor` | não | uma das [cores de mapa](#listas-de-valores) | a do material | Como aparece em um mapa |
| `harvestTool` | não | `pickaxe`, `axe`, `shovel`, `hoe`, `sword` | `pickaxe` | Qual ferramenta o colhe, escrito para você nas tags `mineable` do jogo; `sword` vai para `resourcedatapackloader:mineable/sword`, que as ferramentas `sword` do próprio pacote mineram. Para drops, só importa em um material que exige ferramenta. Como na 1.12.2, uma picareta também minera `rock`, `iron` e `anvil` em velocidade máxima, e um machado `wood`, `plants` e `vine`. Qualquer outro nome, como `shears`, é registrado no log e deixado de fora |
| `harvestToolLevel` | não | 0 a 4 | `0` | 0 madeira, 1 pedra, 2 ferro, 3 diamante, 4 netherite, escrito para você nas tags `needs_*_tool`. Um bloco `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` ou `wall` o ignora e usa o `harvestLevel` da variante, como na 1.12.2 |
| `silkHarvest` | não | boolean | `true` | Se o Toque Suave devolve o próprio bloco |
| `opensWith` | não | id de item | nenhum | Faz do bloco uma caixa trancada: quebrá-lo solta o próprio bloco, e clicar com o botão direito com o item nomeado consome um, toca o som de quebra do bloco, paga a lista de `drops` da variante e remove o bloco. Qualquer outro clique mostra a linha da barra de ação `block.<pack>.<block>.locked` dos arquivos de lang |
| `openSound` | não | nome de som | o som de quebra | O que uma caixa trancada toca ao ser aberta, em vez do som de quebra. Um nome da 1.12.2 ainda é lido, veja [nomes de sons](#listas-de-valores) |
| `expDrop` | não | objeto com `min` e `max` | nenhum | Experiência solta quando um jogador quebra o bloco, ou quando um mob do pacote que coleta experiência o escava; pistões, água e explosões não soltam nenhuma. O Toque Suave a remove só quando `silkHarvest` está ligado |
| `creativeTab` | não | nome de aba | nenhuma | A aba em que aparece, veja [Abas criativas](#abas-criativas) |
| `renderLayer` | não | `solid`, `cutout`, `cutout_mipped`, `translucent` | conforme o tipo | Como é desenhado |
| `opaque` | não | boolean | `true` | Se bloqueia por completo a visão e a luz |
| `fullCube` | não | boolean | igual a `opaque` | Se preenche todo o seu espaço |
| `lightOpacity` | não | 0 a 255 | `255` quando opaco, senão `0` | Quanta luz absorve: 15 ou mais bloqueia toda ela, e `0` deixa a luz do sol passar direto. Uma `slab` mantém a do próprio jogo, e um contêiner com modelo de baú deixa a luz passar |
| `slipperiness` | não | float | `0.6` | O gelo é `0.98` |
| `flammability` | não | int | `0` | Com que facilidade o fogo o consome |
| `fireSpread` | não | int | `0` | Com que facilidade o fogo se espalha a partir dele |
| `explosionResistanceDivisor` | não | float | `1.0` | Divide a `resistance` de cada variante contra explosões |
| `modelBlock` | não | nome de bloco | `minecraft:stone` | Bloco cujo modelo é emprestado quando o seu não traz textura nem modelo próprio |
| `itemModel` | não | `state`, `item` | `state` | `state` segue o blockstate, `item` procura o próprio arquivo, `models/item/<name>.json` |
| `tint` | não | `biome`, `none` ou uma cor hexadecimal | nenhuma | Precisa de um `tintindex` no modelo para aparecer |
| `plantTypes` | não | lista de [tipos de plantas](#listas-de-valores) | nenhum | O que pode ser plantado nele |
| `behavesAs` | não | lista de `till`, `path`, `bush`, `animals` | nenhum | Comportamentos do vanilla a assumir |
| `bounds` | não | lista de seis números, 0 a 1 | bloco inteiro | A caixa de colisão, como `[x1, y1, z1, x2, y2, z2]` |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | O arquivo é ignorado a menos que todos estejam presentes |
| `particle` | só tocha | `none`, `flame`, `colored` | `flame` | A partícula acima de uma tocha |
| `particleColor` | só tocha | cor hexadecimal | `FFFFFF` | Usada quando `particle` é `colored` |
| `smoke` | só tocha | boolean | `true` | Se solta fumaça |
| `leafSapling` | só folhas | nome de bloco | nenhum | A muda que soltam |
| `leafSaplingChance` | só folhas | int | `5` | Uma em N folhas solta uma |
| `seed` | só cultivo | nome de item | `minecraft:wheat_seeds` | O item que o planta e o que um cultivo imaturo solta |
| `produce` | só cultivo | nome de item | `minecraft:wheat` | O que a colheita rende |
| `maxAge` | só cultivo | int | `7` | Quantos estágios de crescimento |
| `growth` | só plantas | objeto | nenhum | Veja [Crescimento](#crescimento) |
| `sapling` | só muda | objeto | nenhum | Veja [Mudas](#mudas) |
| `portal` | só portal | objeto | nenhum | Veja [Portais e passagens](#portais-e-passagens) |
| `container` | só contêiner | objeto | nenhum | Veja [Contêineres](#contêineres) |
| `bell` | só sino | objeto | nenhum | Veja [Sinos](#sinos) |

### Chaves de variantes

*blocos*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `hardness` | não | float | `1.0` | Quanto tempo leva para quebrar. A obsidiana é `50`, `-1` é inquebrável |
| `resistance` | não | float | `5.0` | Resistência a explosões como a 1.12.2 a lê: o bloco mantém três quintos do valor, então `10` dá os `6` da pedra |
| `light` | não | 0 a 15 | `0` | Luz emitida |
| `harvestLevel` | não | 0 a 4 | `0` | O nível de ferramenta de um bloco `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` ou `wall`, no lugar do `harvestToolLevel` do arquivo. Outros tipos seguem o valor do arquivo |
| `rarity` | não | `common`, `uncommon`, `rare`, `epic` | `common` | Cor do nome na dica de ferramenta |
| `maxSize` | não | 1 a 64 | `64` | Tamanho da pilha |
| `tags` | não | lista de ids de tags | nenhuma | Tags de bloco e de item em que esta variante é gravada, como `forge:ores/ruby` na 1.20.1 ou `c:ores/ruby` na 1.21.1. Os arquivos de tag são gerados para você |
| `drops` | não | lista de drops | solta a si mesmo | O que quebrá-lo rende |
| `portal` | só portal | objeto | o do arquivo | O portal próprio desta variante no lugar do do arquivo, escrito como em [Portais e passagens](#portais-e-passagens). O arquivo ainda precisa de um próprio |

**Os nomes são permanentes.** A chave de uma variante é gravada em todo mundo salvo que a contenha. Renomeá-la depois transforma os blocos colocados em ar, a menos que uma [renomeação de registro](#renomeações-de-registro) mapeie o nome antigo para o novo. Um arquivo pode conter quantas variantes quiser; cada uma é um bloco próprio, e uma chave `meta` de um pacote da 1.12.2 é ignorada com uma nota no log.

### Drops

*blocos*

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `block` | uma das duas | nome de bloco ou item | | O que é solto |
| `entity` | uma das duas | nome de entidade | | Uma entidade liberada quando o bloco quebra, em vez de um item |
| `amount` | não | int ou intervalo | `1` | Quantos |
| `chance` | não | 0 a 100 | `100`, ou `0` quando `guaranteed` está desligado | Com que frequência o drop acontece |
| `weight` | não | int | `0` | Acima de zero, a entrada entra em um grupo que rende exatamente um drop. Veja abaixo |
| `bonusChance` | não | lista de ints | nenhum | Drops extras por nível de Fortuna, uma entrada por nível |
| `guaranteed` | não | boolean | `true` | Atalho legado para `chance`. Ligado é `100`, desligado é `0` |

Toda entrada sem `weight` é decidida por conta própria, então um bloco com três delas pode soltar as três, ou nenhuma. Dê um `weight` às entradas e elas deixam de ser independentes: formam um único grupo, do qual exatamente uma é escolhida cada vez que o bloco quebra, com as chances proporcionais aos pesos. Acima, o diamante e a esmeralda dividem um grupo na proporção de um para quatro, então um dos dois sempre sai e é a esmeralda quatro vezes em cinco, enquanto o rubi e o carvão são decididos separadamente e a traça é um caso à parte. Itens e entidades formam grupos separados, então um item ponderado e uma entidade ponderada não competem.

Uma entrada que nomeia uma `entity` libera uma onde o bloco estava, virada para uma direção aleatória, e um mob recebe o tratamento de spawn habitual para a dificuldade local, então chega com o equipamento e os efeitos que teria tido. `amount` decide quantas, `chance` com que frequência, `weight` a coloca no grupo de entidades. Acontece quando o bloco quebra, seja como for que quebrou, então uma explosão ou um pistão as solta do mesmo modo que uma picareta. `bonusChance` e a Fortuna não significam nada para uma entidade e são ignorados.

Um drop que nomeia tanto um `block` quanto uma `entity` usa a entidade e avisa no log.

Os drops são gravados em uma tabela de saque gerada, `loot_tables/blocks/<name>.json` sob o namespace do pacote, a menos que o pacote traga uma própria nesse caminho, caso em que o arquivo do pacote é o que o bloco usa e `drops` não é lido.

### Crescimento

*blocos*

Para `crop`, `flower`, `cane` e `vine`.

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

| Chave             | Obrigatório | Valor                | Padrão           | O que faz                                                                                                                   |
| ----------------- | -------- | ------------------- | ---------------- | --------------------------------------------------------------------------------------------------------------------------- |
| `stages`          | não      | int                 | `16`             | Estágios de crescimento até ficar pronta. Uma videira tenta crescer em um tick aleatório a cada tantos                      |
| `growth`          | não      | int                 |                  | Chance de 1 em N por tick aleatório de avançar                                                                              |
| `spread`          | não      | int                 | `0`              | Até onde se espalha para os blocos vizinhos. Uma videira deixa de alcançar os lados quando há tantas videiras a até dois blocos dela |
| `maxHeight`       | não      | int                 | `3`              | Cana e videira. Até que altura a coluna cresce, ou até onde uma videira pende para baixo; uma videira com `1` não cresce nem se espalha |
| `soil`            | não      | lista de nomes de blocos | o usual do tipo | Sobre o que ela fica                                                                                                        |
| `drop`            | não      | nome de item        | nenhum           | Cana e videira. O que solta ao ser quebrada; uma flor solta a si mesma                                                      |
| `dropCount`       | não      | int                 | `1`              | Cana e videira. Quantos                                                                                                     |
| `needsSky`        | não      | booleano            | `false`          | Só cresce onde o céu é visível                                                                                              |
| `needsWater`      | não      | booleano            | `false`          | Só cresce perto de água                                                                                                     |
| `waterRange`      | não      | int                 | `1`              | A que distância essa água pode estar                                                                                        |
| `damage`          | não      | booleano            | `false`          | Machuca tudo o que a toca                                                                                                   |
| `damageAmount`    | não      | float, meios corações | `1.0`          | O quanto machuca                                                                                                            |
| `breaksNeighbors` | não      | booleano            | `false`          | Quebra blocos colocados ao lado dela, como o cacto                                                                          |

### Mudas

*blocos*

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa.

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

Uma `structure` substitui a árvore gerada por um dos seus modelos, que é o jeito de construir algo que um gerador não consegue, e nada mais no bloco precisa ser escrito. Nomeie várias em `structures` e a muda escolhe uma a cada crescimento, de modo que um bosque não seja a mesma árvore repetida:

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| Chave        | Obrigatório | Valor               | Padrão                 | O que faz                                                                                                                                                                           |
| ------------ | -------- | ------------------- | ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `soil`       | não      | lista de nomes de blocos | nenhum            | Sobre o que ela vai crescer                                                                                                                                                         |
| `stages`     | não      | int                 | `2`                    | Estágios de crescimento até virar árvore                                                                                                                                            |
| `chance`     | não      | int                 | `7`                    | 1 em N por tick aleatório                                                                                                                                                           |
| `light`      | não      | 0 a 15              | `9`                    | Nível de luz necessário                                                                                                                                                             |
| `log`        | não      | nome de bloco       | `minecraft:oak_log`    | Bloco do tronco                                                                                                                                                                     |
| `leaves`     | não      | nome de bloco       | `minecraft:oak_leaves` | Bloco das folhas                                                                                                                                                                    |
| `height`     | não      | int                 | `4`                    | Altura do tronco                                                                                                                                                                    |
| `vines`      | não      | booleano            | `false`                | Pendura videiras nas folhas                                                                                                                                                         |
| `structure`  | não      | `namespace:name`    | nenhum                 | Cresce neste modelo em vez de uma árvore gerada                                                                                                                                     |
| `structures` | não      | lista               | nenhum                 | Vários modelos nos quais crescer, um escolhido a cada crescimento. Cada entrada é `{ "structure": "namespace:name", "weight": 3 }`, ou um nome simples para chances iguais. Substitui `structure` |

## Contêineres

*blocos e itens*

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

| Configuração | Tipo            | Padrão  | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| ------------ | --------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `rows`       | int             | `3`     | Quantas fileiras de espaços, de 1 a 9                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `columns`    | int             | `9`     | Quantos espaços por fileira, de 1 a 12                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `lootTable`  | texto           | vazio   | Uma tabela de saque sorteada no bloco na primeira vez que algo alcança seu conteúdo, seja um jogador abrindo-o, um funil, um comparador ou a quebra do bloco, exatamente como um baú de masmorra é preenchido. Um contêiner colocado por um jogador nunca a sorteia. Vazio deixa o contêiner começar vazio                                                                                                                                                                                                                                                           |
| `chestModel` | booleano ou texto | `false` | Desenha como um baú com tampa que abre, em vez de um bloco comum a partir de uma textura sua. `true` usa a arte do baú vanilla; um nome de textura como `mypack:entity/chest/strongbox` usa a sua própria folha de baú, tanto para o bloco colocado quanto para o item. Um bloco com modelo de baú também tem `opaque` como `false` por padrão, como um baú vanilla, de modo que a luz não é cortada no bloco e o baú não é desenhado escuro                                                                                                                            |
| `guiTexture` | texto           | vazio   | Sua própria imagem de fundo para a tela. Vazio desenha uma a partir da tela do baú vanilla, no tamanho que as fileiras e colunas exigirem                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `guiWidth`   | int             | nenhum  | Largura da tela, desenhada a partir do canto superior esquerdo dessa imagem lida como uma folha de 256 por 256, obrigatório com `guiTexture`                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `guiHeight`  | int             | nenhum  | Altura da tela, obrigatório com `guiTexture`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `curioSlot`  | texto           | vazio   | Somente para itens: o espaço do Curios em que ele pode ser usado, `back`, `belt`, `body`, `charm`, `head`, `necklace`, `ring` ou qualquer espaço que outro mod adicione. Uma mochila normalmente usa `back`. Ignorado, sem afetar o resto do funcionamento do item, quando o Curios não está instalado. A chave `bauble` do 1.12.2 é lida como esta e aceita os nomes do Baubles: `amulet` vira `necklace`, `ring` dá dois espaços de anel, `belt`, `head`, `body` e `charm` mantêm seus nomes, e `trinket` serve em todos esses espaços. Qualquer outro nome deixa o item sem uso, com uma linha de erro |

**Nove fileiras por doze é o teto**, o máximo que uma tela comporta. Um pacote que pedir mais é limitado a isso, com uma linha de erro avisando. Um aviso sobre a mais alta: uma tela de nove fileiras tem 276 pixels, e um monitor 1080 com escala de GUI `auto` dá 270, então o topo e a base são cortados em três pixels cada; com a escala 3 ela aparece inteira.

**A tela é desenhada, não distribuída.** Um contêiner de nove colunas ou menos e seis fileiras ou menos usa a tela do baú vanilla como está, então fica exatamente como um baú desse tamanho. Qualquer coisa maior é montada a partir da mesma imagem na hora de desenhar: a borda superior, uma fileira de espaços repetida até caber e a parte de baixo com o inventário do próprio jogador, de modo que um pacote pode pedir tamanhos que nenhuma tela vanilla cobre sem distribuir imagem própria. `guiTexture` substitui tudo isso quando um pacote quer sua própria aparência, e então `guiWidth` e `guiHeight` precisam informar o tamanho, ou a tela desenhada é usada e uma linha de erro avisa.

**O que o bloco faz.** Ele mantém seu conteúdo ao salvar e recarregar, solta-o ao ser quebrado, responde a um comparador conforme o quão cheio está e guarda as fileiras e colunas com que foi criado, de modo que mudá-las depois no pacote deixa como estavam os contêineres que já existem em um mundo. Um item contêiner ou um bloco contêiner não pode ser colocado dentro de um contêiner. `chestModel` também lhe dá o som de abertura do baú e a animação da tampa; desativado, o bloco é desenhado a partir da própria textura como qualquer outro, então um caixote, um barril ou um armário funcionam.

**Colorindo um baú.** A folha do baú é uma textura comum, então um mapa de pixels pode recolorir a vanilla sem desenhar um pixel: use `extends` nela, dê-lhe um `tint` e então nomeie esse mapa em `chestModel`.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

**Um item contêiner é uma bolsa**, um item do tipo `container` com o mesmo bloco `container` contendo `rows` e `columns`; abra-a com o botão direito, e ela mantém o conteúdo ao trocar de mãos. Sem o objeto `container`, ela tem uma única fileira de nove. Dê-lhe `curioSlot` e, onde o Curios estiver instalado, ela vai nesse espaço e uma tecla a abre sem precisar tirá-la, `V` por padrão, redefinível em Resource Data Pack Loader nos controles. Pressioná-la de novo, com um contêiner usado já aberto, passa para o próximo que você estiver usando e volta ao primeiro no fim, de modo que vários usados ao mesmo tempo ficam todos ao alcance. A tecla só aparece quando o Curios está presente, e tudo o mais sobre o item, o botão direito e seu inventário, funciona com ou sem ele. A possibilidade de uso da bolsa é escrita para você na tag do Curios para esse espaço.

**A tabela de saque é preenchida no primeiro uso**, não quando o bloco é colocado, o que a torna útil em uma estrutura: quem abrir primeiro recebe o sorteio, e um funil ou um comparador que a alcançar antes a sorteia da mesma forma. A mesma tabela pode ser usada por `lootTable` em uma forma de imprint ou em um terreno de vila, de modo que um pacote pode colocar esses contêineres por worldgen e abastecê-los do mesmo jeito.

## Sinos

*blocos*

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

E seu blockstate, `assets/mypack/blockstates/village_bell.json`, com chaves `attachment` e `facing`:

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

| Configuração    | Tipo       | Padrão                             | O que faz                                                                                                                                                                |
| --------------- | ---------- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `swing`         | booleano   | `true`                             | Desenha a parte oscilante a partir de um arquivo de modelo próprio e a balança quando o sino toca. `false` desenha o sino inteiro a partir dos modelos do blockstate, sem nada animado |
| `sound`         | nome de som | `minecraft:block.note_block.bell`  | Tocado quando o sino soa. Vazio toca em silêncio                                                                                                                         |
| `resonateSound` | nome de som | `minecraft:block.note_block.chime` | Tocado quando o sino ressoa porque há invasores por perto. Vazio ressoa em silêncio                                                                                      |

**Ele fica pendurado como o sino do próprio jogo.** Colocado sobre um bloco, fica no chão, virado para onde você olha; sob um bloco, pende do teto; contra uma parede, pende dessa parede, e entre duas paredes quando o lado oposto também é sólido. Cai quando o que o sustenta some, e um sino entre duas paredes vira um sino de parede única quando uma delas desaparece. Sua caixa de colisão segue a do vanilla para cada um dos quatro, então `bounds` não é lido.

**O que o faz tocar.** Usar o lado do corpo, abaixo da viga: um sino de chão nas duas faces que a viga atravessa, um sino de parede nas duas faces ao lado da parede, um sino de teto em qualquer lado. O topo, a base e qualquer coisa acima do corpo não fazem nada. Um sinal de redstone o faz tocar uma vez ao ligar, e uma flecha, uma bola de neve ou qualquer outro projétil o faz tocar ao atingir um lado que uma mão alcançaria. O corpo balança para longe do lado em que foi atingido por dois segundos e meio; a redstone o balança no sentido para onde o sino aponta.

**O que um toque faz.** Aldeões a até 32 blocos o ouvem e correm para casa para se esconder por quinze segundos, como o sino do próprio jogo os manda fazer. Quando um invasor está a até 32 blocos, o sino ressoa um quarto de segundo depois do toque, e dois segundos depois todo invasor a até 48 blocos brilha por três segundos, com partículas coloridas ao lado do sino, do lado em que cada um está. Um invasor é qualquer coisa enviada por uma [invasão](#invasões), mais os illagers e bruxas do próprio jogo. Um sino deste tipo é um sino de vila para toda invasão: ele toca a cada onda que chega, sem precisar ser nomeado em `bell` na invasão.

**Os modelos.** Um sino é indexado por `attachment` e `facing`, dezesseis estados no total, e `powered` fica fora das chaves. Com `swing` ativado, esses modelos desenham apenas a armação, e a parte oscilante é um arquivo de modelo próprio, `<namespace>:block/<name>_body`: o mod o carrega por esse caminho e o renderizador o desenha, então o nome não é seu para escolher e nunca aparece no blockstate. Ele é modelado no espaço do bloco onde o corpo repousa e inclina em torno do ponto a meio bloco para dentro e três quartos de bloco para cima, como o do vanilla. Com `swing` desativado não há modelo de corpo, e os dezesseis modelos de armação desenham o sino inteiro. O modelo de item escrito para a mão desenha a armação e o corpo juntos, então o sino aparece inteiro na mão; distribua `models/item/<name>.json` para desenhá-lo de outro jeito.

**A oscilação é desenhada pelo cliente.** Um toque chega aos jogadores como um evento de bloco, então um servidor dedicado balança o sino para todos que têm este mod, e um jogador sem ele apenas ouve o sino. Sons, ressonância e brilho acontecem todos no servidor.

## Modelos, blockstates e texturas

*blocos e itens*

Definir um bloco ou item o registra. A aparência dele é um conjunto de arquivos de recursos nas mesmas pastas e no mesmo formato que o jogo usa, sob o seu próprio namespace, e nesta versão a maioria deles é escrita para você.

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**Distribua uma textura e o resto é gerado.** Para todo bloco cujo blockstate o pacote não distribui, o mod escreve o blockstate e os modelos de que o tipo precisa, apontando para `textures/block/<name>.png`, onde `<name>` é a chave da variante, e para todo item sem `models/item/<name>.json`, um modelo de item apontando para `textures/item/<name>.png`. Um bloco sem textura nem modelo próprio toma emprestada a aparência de `modelBlock`, pedra por padrão, então nada nunca é renderizado como o quadrado roxo e preto. Distribua um `blockstates/<name>.json` seu e o mod não gera nada para esse bloco e usa o seu; o mesmo vale para `models/item/<name>.json`.

| Tipo                                                               | Arquivos de textura que procura                                                                        | Gerado a partir de                                                                                                                                                                                                            |
| ------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `basic`, `ore`, `falling`                                          | `<name>`, com `<name>_top` e `<name>_bottom` para as faces de cima e de baixo onde o pacote as distribui | `cube_all`, ou `cube_bottom_top` quando há uma textura de topo ou de base                                                                                                                                                     |
| `flower`, `sapling`, `cane`, `leaves`, `container` sem baú | `<name>`                                                                                               | `cube_all`, `cross` ou `leaves`                                                                                                                                                                                               |
| `log`                                                              | `<name>` para o lado, `<name>_top` para as pontas                                                      | `cube_column`                                                                                                                                                                                                                 |
| `slab`                                                             | `<name>`                                                                                               | `slab`, `slab_top` e um duplo `cube_all`                                                                                                                                                                                      |
| `stairs`                                                           | `<name>`                                                                                               | `stairs`, `inner_stairs`, `outer_stairs`, os quarenta estados escritos por extenso                                                                                                                                            |
| `fence`                                                            | `<name>`                                                                                               | `fence_post` e `fence_side` como multipart, e `fence_inventory` para a mão                                                                                                                                                    |
| `wall`                                                             | `<name>`                                                                                               | os modelos de poste e lado do muro como multipart, e `wall_inventory` para a mão                                                                                                                                              |
| `pane`                                                             | `<name>` para o painel, `<name>_top` para a borda                                                      | os cinco modelos de painel de vidro como multipart                                                                                                                                                                            |
| `door`                                                             | `<name>_top` e `<name>_bottom`, ou `<name>` para ambos                                                 | os oito modelos de porta e seus trinta e dois estados                                                                                                                                                                         |
| `trapdoor`                                                         | `<name>`                                                                                               | os três modelos orientáveis de alçapão                                                                                                                                                                                        |
| `fence_gate`                                                       | `<name>`                                                                                               | os quatro modelos de portão, fechado e aberto, em um muro e fora dele                                                                                                                                                         |
| `ladder`, `vine`, `torch`                                          | `<name>`                                                                                               | o modelo do próprio jogo para cada um                                                                                                                                                                                         |
| `bell`                                                             | `<name>`                                                                                               | as armações de sino do próprio jogo como `<name>_floor`, `<name>_ceiling`, `<name>_wall` e `<name>_between_walls`, o `pack_bell_body` do mod como `<name>_body`, e um blockstate de dezesseis estados `attachment` e `facing` sobre eles |
| `crop`                                                             | `<name>_stage0` até `<name>_stage<maxAge>`, ou `<name>` para todos                                     | um modelo `crop` por estágio, com `age=0` a `7` mapeado neles                                                                                                                                                                 |
| `portal`                                                           | `<name>`, ou a do portal do Nether                                                                     | `cube_all` para um bloco `fullCube`, como um bloco de portal é no 1.12.2; três lajes de portal, uma por eixo, para um que não seja, como o portal de moldura de uma dimensão                                                  |
| `banner`                                                           | sua própria folha, veja [Estandartes](#estandartes)                                                    | o modelo de estandarte do jogo                                                                                                                                                                                                |
| `container` com `chestModel`                                       | a folha de baú nomeada em `chestModel`                                                                 | o modelo `pack_chest` do mod                                                                                                                                                                                                  |

Toda textura é procurada em `textures/block/`, e o nome é a chave da variante, então um bloco registrado como `ruby_ore` quer `textures/block/ruby_ore.png` e nada mais precisa ser escrito. Um bloco cujo item é desenhado plano, uma porta, uma escada, uma tocha, uma muda, uma flor, uma cana, uma videira ou um painel, usa `textures/item/<name>.png` na mão quando existe e a textura do bloco quando não existe.

**Itens** usam `textures/item/<name>.png` e um modelo `item/generated` gerado, ou `item/handheld` para uma ferramenta. Distribua `models/item/<name>.json` para desenhá-lo de outro jeito.

**Fluidos** não precisam de modelo algum; um é gerado a partir das texturas `still` e `flow`.

**Um bloco com várias variantes são vários blocos.** Cada chave em `variants` é registrada por conta própria, então cada uma tem seu próprio blockstate, seus próprios modelos e suas próprias texturas, nomeados conforme a chave. Não existe blockstate compartilhado com uma propriedade `blocks`, e nada em um blockstate precisa dizer qual variante ele é: `blockstates/ruby_ore.json` é o do minério de rubi, e `blockstates/deep_ruby_ore.json` é o do profundo.

### Escrevendo os seus

*modelos, blockstates e texturas*

Tudo o que é gerado pode ser substituído. Um blockstate distribuído pelo pacote é usado como está, no formato do próprio jogo: o `variants` do vanilla indexado pelas propriedades do bloco, ou `multipart`. As propriedades são as do próprio jogo para cada tipo: `axis` em um tronco, `type` em uma laje, `facing`, `half` e `shape` em escadas, `facing`, `half`, `hinge` e `open` em uma porta, `facing`, `half` e `open` em um alçapão, `facing`, `in_wall` e `open` em um portão, `age` em uma plantação e em uma cana, `stage` em uma muda, `north`, `east`, `south`, `west` em uma cerca ou um painel, com `up` acrescentado em um muro e em uma videira, `rotation` em um estandarte de chão e `facing` em um de parede, `axis` em um portal, `attachment` e `facing` em um sino, com `powered` fora das chaves. Um bloco `basic`, `ore`, `falling`, `leaves`, `flower` ou `container` tem um único estado, com a chave `""`.

Aponte os modelos para os pais que recebem texturas, não para os vanilla já prontos: `cube_all` recebe um `all`; `cube_column` um `end` e um `side`; `cross` um `cross`; os pais de escadas `bottom`, `top` e `side`; `fence_post` e `fence_side` um `texture`; `template_wall_post` e `template_wall_side` um `wall`; os modelos de painel de vidro um `pane` e um `edge`; os pais de porta um `top` e um `bottom`; `template_orientable_trapdoor_*` e `template_fence_gate*` um `texture`; `template_torch` um `torch`; `crop` um `crop`; `vine` e `ladder` o próprio nome. Um modelo que nomeia um modelo vanilla pronto, como `oak_door_bottom_left`, herda junto as texturas do vanilla, diga o que disser o blockstate.

### Estandartes

*modelos, blockstates e texturas*

Um estandarte é o único tipo em que a forma do bloco e a forma do modelo se separam, então vale detalhá-lo por completo.

**Ele registra dois blocos.** Uma definição dá o estandarte de chão com o seu nome e um segundo bloco chamado `<name>_wall` para o de parede. Ambos precisam de blockstate; só o de chão recebe um item, e esse item decide qual dos dois coloca: de chão quando você clica no topo de um bloco e de parede quando clica em um lado. Você nunca coloca o bloco de parede diretamente, e ele não precisa de item próprio.

**O de chão gira em dezesseis avos.** Sua propriedade é `rotation`, de `0` a `15`, porque um estandarte gira em dezesseis avos e não em quartos. O `y` de um blockstate só aceita 0, 90, 180 e 270, então cada rotação aponta para um pequeno modelo próprio que toma o seu modelo de estandarte como pai e o gira com um `transform`:

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

…e assim por diante até `15`, cada um `-22.5` graus além do anterior. O sinal corresponde ao dos estandartes do próprio jogo, que giram pelo negativo da rotação. Construa o modelo virado para o sul, já que é para onde um estandarte colocado por um jogador olhando para o sul acaba apontando. O bloco de parede é um blockstate comum com as quatro entradas usuais de `facing` em 0, 90, 180 e 270, já que não há nada fracionário nele. O blockstate Forge de um pacote 1.12.2 é transformado exatamente nisto quando o pacote é convertido.

**O modelo tem quase dois blocos de altura.** Um estandarte ocupa um bloco para colocação e colisão, mas é desenhado bem fora dele, e um modelo que termina no topo do próprio bloco parece atrofiado. As proporções do vanilla, em dezesseis avos de bloco, valem ser copiadas exatamente:

| Parte            | De     | Até     |
| ---------------- | ------ | ------- |
| Poste            | `0`    | `28`    |
| Travessa         | `28`   | `29.33` |
| Pano             | `2.67` | `29.33` |
| Largura do pano  | `1.33` | `14.67` |
| Pano de parede   | `-13`  | `13.67` |

Então um estandarte de chão chega a `29.33`, quase dois blocos, e um estandarte de parede pende treze dezesseis avos *abaixo* do bloco que o segura. Os elementos do modelo podem ir de `-16` a `32`, então ambos cabem. A forma de parede não tem poste nem travessa, só pano.

**O pano tem o dobro da altura da largura, e sua textura também precisa ter.** Essa face mede `13.33` por `26.67`. Mapeie uma textura quadrada nela e o desenho é espremido à metade da altura. As texturas de bloco não podem ter o dobro da altura da largura, já que qualquer coisa não quadrada é lida como animação, então o jeito de contornar é uma folha quadrada maior com o pano em parte dela: um arquivo de 32×32 contendo o pano como uma região de 16×32, endereçada como `"uv": [0, 0, 8, 16]`, com as faixas do poste e da travessa no espaço ao lado. As coordenadas UV sempre vão de 0 a 16, qualquer que seja a resolução do arquivo, então os mesmos números funcionam em qualquer tamanho.

**O item dele quer um modelo próprio.** Um item que herda um modelo tão alto vai estourar para fora do seu espaço na escala usual de bloco, então dê a `models/item/<name>.json` um bloco `display` próprio, com a escala reduzida e o conjunto todo transladado de volta para dentro do quadro.

**Sem blockstate, é desenhado a partir de uma folha.** Um estandarte cujo pacote não distribui blockstate recebe um gerado, e o renderizador de estandartes do jogo o desenha na forma do estandarte vanilla a partir da folha em `textures/entity/banner/<name>.png`, organizada como a do estandarte vanilla. Isto é um acréscimo desta versão; os blocos de chão e de parede decidem isso cada um pelo seu próprio blockstate.

**Não há cores nem padrões nele.** Um estandarte de pacote não tem uma lista de camadas como os estandartes vanilla. O desenho é a textura, do mesmo modo que a aparência de uma porta é a sua textura, e uma definição é um estandarte. Tingi-lo e empilhar padrões nele não é algo que um pacote consiga alcançar.

**Ele usa o `material` que você der.** Um estandarte de pedra é minerado com picareta como a pedra que diz ser.

### Texturas escritas como mapas de pixels

*modelos, blockstates e texturas*

Uma textura pode ser um arquivo JSON em vez de um PNG. Coloque-o onde o PNG iria, com `.json` no fim do nome inteiro, de modo que `textures/block/panel.png.json` responde a toda requisição por `textures/block/panel.png`. Nada mais muda: os modelos apontam para `mypack:block/panel` como sempre, e o atlas, os mipmaps e um `.mcmeta` de animação funcionam, porque o que o jogo recebe ainda é um PNG. O pacote de exemplo não distribui nenhum PNG; toda textura nele é um mapa.

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

| Chave     | Obrigatório       | Valor                       | Padrão  | O que faz                                                                    |
| --------- | ----------------- | --------------------------- | ------- | ---------------------------------------------------------------------------- |
| `size`    | sim, ou herdado   | `larguraxaltura`            |         | Quantos pixels de largura e de altura                                        |
| `rows`    | sim, ou herdado   | lista de textos             |         | Uma string por fileira de pixels, um caractere por pixel, de cima para baixo |
| `palette` | sim, ou herdado   | objeto                      |         | Um caractere para uma cor, `#RRGGBB` ou `#AARRGGBB`                          |
| `extends` | não               | outro mapa de pixels        |         | O mapa a partir do qual este começa                                          |
| `tint`    | não               | objeto com `from` e `to`    |         | Recolore tudo o que foi herdado ao longo de uma rampa entre duas cores       |
| `notes`   | não               | objeto                      |         | Um caractere para uma linha dizendo para que serve, herdada e nunca desenhada |

**Não há nome a declarar.** O próprio caminho do arquivo é o seu nome, exatamente como o de um PNG, então um mapa em `assets/mypack/textures/block/panel.png.json` é `mypack:block/panel` em um modelo, e um mapa em `assets/mypack/textures/item/gem.png.json` é `mypack:item/gem` em um modelo de item. Nada aponta para um mapa de pixels de forma especial; um bloco ou um item nomeia sua textura como sempre fez e nunca fica sabendo qual dos dois recebeu. Isso também significa que as pastas de blocos e de itens continuam separadas, como para PNGs: `textures/block/gem.png.json` e `textures/item/gem.png.json` são duas texturas diferentes e são guardadas em cache como dois arquivos diferentes.

**Qualquer tamanho que você quiser**, até 4096 de cada lado, e os dois lados não precisam coincidir. `16x16` é uma face de bloco comum, `16x32` é o tipo de faixa alta que a metade de uma porta ou uma animação pede. O tamanho é verificado, não adivinhado: dê uma fileira por linha de pixels e um caractere por pixel na largura, ou o mapa é recusado e o log informa a fileira e o que encontrou. Um caractere sem cor na paleta fica transparente, então `.` ou um espaço é um buraco.

**Os modelos são o ponto central.** `extends` nomeia outro mapa de pixels, como `namespace:path` ou um caminho simples no mesmo pacote, e o arquivo que o estende herda seu `size`, suas `rows` e sua `palette`. Tudo o que ele mesmo nomear prevalece, e ele não precisa nomear tudo, então uma variante inteira pode ser um punhado de cores:

```json
{
  "extends": "mypack:textures/block/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

Isso é uma segunda textura completa: a mesma forma em purpur, e se a forma for redesenhada no modelo, todas as variantes a acompanham. Uma variante pode, em vez disso, dar suas próprias `rows` e manter a paleta do modelo, que é o caminho inverso, as mesmas cores em um padrão diferente. A herança vai até oito níveis de profundidade, um laço é detectado e reportado, e um mapa que nomeia um modelo que nada fornece é reportado em vez de desenhado em branco.

**Qual de duas texturas é o modelo** é decidido por qual delas contém mais distinções, não por qual foi desenhada primeiro. Uma variante dá uma cor a cada caractere, então todo pixel que o modelo chama pelo mesmo caractere sai da mesma cor na variante. Um minério desenhado sobre pedra, portanto, não pode herdar as `rows` da pedra: a pedra chama as posições das manchas de pedra comum, e nada que uma variante possa escrever divide um caractere em dois. Inverta e funciona. Faça do minério o modelo, de modo que os tons da pedra e os tons do minério tenham cada um caracteres próprios, e um segundo minério são quatro cores:

```json
{
  "extends": "mypack:textures/block/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

Uma variante que realmente queira um padrão diferente dá suas próprias `rows`, como acima, e então herda apenas a paleta. Vale a pena quando as cores são o ponto e a forma é incidental; quando a forma é o ponto, ponha a forma no modelo e deixe as variantes nomearem cores.

**Um modelo não precisa ser uma textura.** Um mapa só é servido ao jogo quando seu caminho termina em `.png`, então um modelo em `textures/block/ore_template.json` é invisível para o jogo e existe apenas para ser estendido, enquanto um em `textures/block/ore_template.png.json` também responderia a requisições por `ore_template.png`. Nomeie uma forma compartilhada sem o `.png` e nada pode pedi-la por acidente.

**Um modelo pode ser uma imagem real em vez de um mapa.** Aponte `extends` para um PNG que qualquer pacote ou o próprio jogo forneça e a paleta muda de significado: as chaves passam a ser as cores já presentes nessa imagem, e os valores, as cores a pôr no lugar. Nada é traçado e nenhuma `rows` é escrita, então um pacote pode recolorir uma textura do vanilla ou de um mod onde ela está:

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

Isso é um minério de rubi na pedra do próprio vanilla: os quatro tons das manchas são trocados e todos os outros pixels ficam como estavam. Uma cor que a imagem não contém simplesmente nunca corresponde, e o tamanho vem da imagem, a menos que você nomeie um, que então deve concordar.

`extends` dá preferência a um mapa de pixels: procura primeiro o mapa nesse caminho e só recorre à imagem quando nenhum pacote fornece um. Um nome que não é nenhum dos dois é reportado em vez de desenhado em branco. Construir sobre uma imagem é trabalho do lado do cliente, já que são os recursos do próprio jogo sendo lidos, então um servidor dedicado nunca faz isso.

**Um modelo pode ser tingido em vez de repintado.** `tint` nomeia duas cores e recolore tudo o que o mapa herda ao longo da rampa entre elas. O brilho de cada cor herdada é o seu lugar nessa rampa, então o preto cai em `from`, o branco cai em `to`, e todo tom intermediário é misturado em proporção. A transparência é deixada intacta. Isso faz de um modelo em tons de cinza mais duas cores uma variante completa:

```json
{
  "extends": "mypack:textures/item/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` pode ser omitido, caso em que é preto e o tint vira uma multiplicação comum, com a mesma forma de um `tintindex` na hora de renderizar. A diferença é que este é desenhado no PNG uma única vez e guardado em cache, então não custa nada por quadro e alcança uma textura que nada tinge, mas também não consegue acompanhar um bioma como `grass` ou `foliage` conseguem.

O modelo continua sendo um mapa comum: abra-o, olhe para ele, e ele é desenhado como o cinza que é. As duas cores aceitam `#RRGGBB`, `#AARRGGBB` ou um `0x` no início, e um valor que não seja nenhum desses deixa o mapa sem desenho em vez de desenhá-lo na cor errada. Um tint é herdado como todo o resto e o primeiro da cadeia prevalece, então o tint da própria variante vence o do que ela estende. Funciona também em um modelo de imagem, onde roda depois das trocas de cor da paleta.

**Um tint é uma rampa entre duas cores**, então só serve a uma textura cujos tons estejam em uma. Uma forma com duas regiões sem relação, a pedra de um minério contra suas manchas, não é esse caso, e quer a paleta escrita por extenso.

**Saber o que significam os caracteres de um modelo** é a parte incômoda de estender um, e é para isso que serve o bloco `notes` acima: um caractere para uma linha curta, herdado como a paleta é e nunca desenhado. Rotule os caracteres de um modelo e quem o estender sabe quais substituir.

`/rdpl pixelmap <namespace:path>` então informa o que um mapa realmente resultou, que é o jeito confiável de escrever uma variante sem abrir todos os arquivos da cadeia:

```
oretest:textures/block/ruby_ore.png is 16x16
  built from oretest:textures/block/ruby_ore.png.json
  built from oretest:textures/block/gem_ore.png.json
  rows come from oretest:textures/block/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Todo caractere é listado com sua cor, quantos pixels cobre, qual arquivo da cadeia o definiu e para que esse arquivo diz que ele serve. O caminho pode ser dado de forma curta, `mypack:block/panel`, ou completa. Um caractere com 0 pixels é um que a paleta nomeia e as fileiras nunca usam, o que geralmente é um erro de digitação em uma fileira.

**As imagens desenhadas são mantidas em disco** em `rdploader/pixelmap-cache`, em uma pasta por namespace e nomeadas com o nome da textura seguido de um hash da sua origem. O hash cobre a cadeia inteira, o próprio mapa e todos os modelos acima dele, então editar um modelo muda o carimbo de toda variante que herda dele, e todas são redesenhadas. Quando um mapa é redesenhado, seus arquivos mais antigos são varridos.

A pasta também é percorrida a cada vez que os pacotes são examinados, e qualquer imagem cujo mapa nenhum pacote fornece mais é apagada, junto com qualquer pasta que fique vazia. Renomeie uma textura, retire um pacote, apague um mapa, e a imagem em cache vai junto em vez de ficar parada para sempre. Apagar a pasta inteira não custa nada além do tempo de desenhá-las de novo, e ela é ignorada quando os pacotes são examinados, então nunca é confundida com um pacote.

Um PNG sempre vence. Se `panel.png` e `panel.png.json` existirem, o PNG é servido e o mapa nunca é desenhado, de modo que uma textura gerada pode ser substituída por uma pintada depois sem mudar nada do que aponta para ela.

**Ninguém precisa escrever esses arquivos à mão.** O repositório traz scripts para todo o processo de ida e volta em `pixelmap/`: `png_to_pixelmap.py` transforma um PNG em um mapa, `convert_pack.py` faz isso para toda textura que um pacote contém, e `verify_pack.py` desenha os mapas de um pacote convertido e os compara com os PNGs de onde vieram, de modo que uma conversão pode ser confiável antes de os originais serem postos de lado.

### Armadilhas que vale conhecer

*modelos, blockstates e texturas*

**Um modelo que nomeia um modelo vanilla pronto herda também as texturas do vanilla.** `torch`, `ladder`, `oak_door_bottom_left` e `wheat_stage0` carregam todos suas próprias texturas, então um modelo que aponta para um deles fica com a aparência do vanilla, não importa o que você ponha ao lado. Modelos pais como `cube_all`, `cross` e `crop` tomam suas texturas do modelo que os nomeia e se comportam bem, e o mesmo vale para os modelos de porta, alçapão e portão.

**Os nomes vêm do arquivo de idioma.** Um bloco ou item mostra uma chave crua até que `lang/en_us.json` lhe dê um nome, e as chaves são as do próprio jogo: `block.mypack.ruby_ore` para um bloco e o item que o coloca, `item.mypack.ruby` para um item, `itemGroup.mypack.tab` para uma aba criativa, `fluid_type.mypack.molten_ruby` e `fluid.mypack.molten_ruby` para um fluido, `effect.mypack.ruby_sight` para um efeito de poção, `entity.mypack.angry_cow` para uma variante de entidade, `biome.mypack.ruby_forest` para um bioma. Um nome cada; nada nesta versão quer uma chave escrita duas vezes.

## Fazendo o vanilla tratar seu bloco corretamente

*blocos e itens*

O vanilla verifica seus próprios blocos por identidade em uma dúzia de lugares, então um bloco de pacote que obviamente deveria funcionar muitas vezes não funciona. Duas chaves resolvem.

```json
{
  "material": "ground",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "hardness": 0.6 } }
}
```

**`plantTypes`** lista os tipos de planta que seu bloco suporta, para que mudas, plantações e flores possam ser plantadas nele: `plains`, `desert`, `beach`, `cave`, `water`, `nether` e `crop`. O **1.21.1** não tem tipos de planta; lá `bush` em `behavesAs` faz o que `plains` fazia, a própria lista de solos da planta decide o resto, e a chave é lida e ignorada.

**`behavesAs`** faz o vanilla tratar seu bloco como um dos seus:

| Valor     | O que faz                                                                                                                |
| --------- | ------------------------------------------------------------------------------------------------------------------------ |
| `till`    | Uma enxada o transforma em terra arada, ou no que `hoeTillsInto` na config nomear                                       |
| `path`    | Uma pá o transforma em um caminho de terra, ou no que `shovelPathBecomes` nomear                                         |
| `bush`    | Flores, grama e mudas podem ser plantadas nele e permanecem nele, como na terra. O mesmo que `plains` em `plantTypes`    |
| `animals` | Animais nascem nele com luz, como na grama                                                                               |

## Itens

*blocos e itens*

`<namespace>/items/*.json`

Cada chave dentro de `variants` é um item, registrado sob o namespace do pacote, então um arquivo contendo `ruby_apple` e `dried_ruby_apple` registra `mypack:ruby_apple` e `mypack:dried_ruby_apple`; o nome do arquivo é apenas um agrupamento. O modelo de cada um é gerado a partir de `textures/item/<name>.png`, a menos que o pacote distribua `models/item/<name>.json`.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa. Uma chave marcada para um tipo é lida apenas por esse tipo.

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

### Tipos de itens

*itens*

| Tipo            | O que você obtém                                                                  |
| --------------- | --------------------------------------------------------------------------------- |
| `basic`         | Um item simples. Usado quando `type` está ausente                                 |
| `food`          | Comido, com fome e saturação                                                      |
| `drink`         | Bebido em vez de comido, devolvendo um recipiente vazio                           |
| `tool`          | Picareta, machado, pá, enxada ou espada a partir de um material                   |
| `armor`         | Capacete, peitoral, calças ou botas a partir de um material                       |
| `seed`          | Planta uma das suas plantações                                                    |
| `potion`        | Aplica seus efeitos de poção quando usado                                         |
| `potion_bottle` | Contém seus tipos de poção e os mostra em uma aba criativa                        |
| `container`     | Uma bolsa: um inventário carregado na mão, ou usado no corpo, veja [Contêineres](#contêineres) |

Um `potion_bottle` lista o que pode conter com `potionTypes`, um array de nomes de tipos de poção como `["mypack:ruby_tonic"]`. Um com lista vazia não registra nada, e o log avisa.

### Chaves de arquivo de itens

*itens*

| Chave          | Obrigatório | Valor                               | Padrão                 | O que faz                                                                                                                                                                                                                             |
| -------------- | ----------- | ----------------------------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `variants`     | sim         | objeto de nome de variante para variante |                   | Um item por entrada. A chave é seu nome de registro, e nomeia seu modelo, sua textura e sua chave de lang                                                                                                                             |
| `type`         | não         | um dos tipos acima                  | `basic`                | Qual tipo o item assume                                                                                                                                                                                                               |
| `creativeTab`  | não         | nome de aba                         | nenhum                 | A aba em que aparece, veja [Abas criativas](#abas-criativas)                                                                                                                                                                          |
| `material`     | tool, armor | nome de material                    | nenhum                 | De qual dos seus materiais ele é feito                                                                                                                                                                                                |
| `toolClass`    | tool        | `pickaxe`, `axe`, `shovel`, `sword` | nenhum                 | Qual ferramenta é. Uma `sword` é uma ferramenta do 1.12.2 e não uma espada vanilla: ela causa 3 mais o `damage` do material, minera os blocos cujo `harvestTool` é `sword`, aceita encantamentos de mineração, e não varre nem corta teias de aranha |
| `slot`         | armor       | `head`, `chest`, `legs`, `feet`     | nenhum                 | Onde é usada. `helmet`, `chestplate`, `leggings` e `boots` também funcionam                                                                                                                                                           |
| `eat`          | food        | booleano                            | `false`                | Usa a animação de comer                                                                                                                                                                                                               |
| `alwaysEdible` | food        | booleano                            | `false`                | Pode ser comido com a barra de fome cheia                                                                                                                                                                                             |
| `useDuration`  | não         | int, ticks                          | `32`                   | Quanto tempo leva para usá-lo                                                                                                                                                                                                         |
| `attackSpeed`  | não         | float                               | conforme a classe da ferramenta | Para `tool`, o atributo de velocidade de ataque, como na espada, que é `-2.4`                                                                                                                                                |
| `cooldown`     | não         | int, ticks                          | `0`                    | Para `food`, `drink` e `potion`, por quanto tempo o item recusa novo uso depois de consumido; num item com `rolls`, o tempo entre rolagens                                                                                                                                          |
| `container`    | drink       | nome de item                        | nenhum                 | O que fica para trás, como uma garrafa. Em um item `container`, esta chave são as configurações da própria bolsa, veja [Contêineres](#contêineres)                                                                                    |
| `crop`         | seed        | nome de bloco                       | nenhum                 | A plantação que ele planta                                                                                                                                                                                                            |
| `soil`         | seed        | nome de bloco                       | `minecraft:farmland`   | Onde pode ser plantado                                                                                                                                                                                                                |
| `rolls` | não | `coin`, `d6`, `2d6+1`, um dado ou um baralho | nenhum | Num item simples, um clique direito rola como `/rdplserver game` faria e conta ao público padrão do pacote. Veja [Dados e baralhos](#dados-e-baralhos) |
| `passesTurn` | não | booleano | `false` | Num item simples, um clique direito passa a vez de quem o segura, como `/rdplserver game pass` faz. Veja [Turnos](#turnos) |
| `requires`     | não         | lista de ids de mods ou namespaces de pacotes | nenhum       | O arquivo é ignorado a menos que todos estejam presentes                                                                                                                                                                              |

### Chaves de variantes de itens

*itens*

| Chave        | Obrigatório | Valor                                | Padrão   | O que faz                                                                                                                                                                                                                                                   |
| ------------ | ----------- | ------------------------------------ | -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `maxSize`    | não         | 1 a 64                               | `64`     | Tamanho da pilha                                                                                                                                                                                                                                            |
| `rarity`     | não         | `common`, `uncommon`, `rare`, `epic` | `common` | Cor do nome na dica de ferramenta                                                                                                                                                                                                                           |
| `healAmount` | food        | int, meias coxas                     | `0`      | Fome restaurada                                                                                                                                                                                                                                             |
| `saturation` | food        | float                                | `0.0`    | Saturação restaurada                                                                                                                                                                                                                                        |
| `tags`       | não         | lista de ids de tags                 | nenhum   | Tags de item em que esta variante é escrita; os arquivos de tag são gerados para você                                                                                                                                                                       |
| `potion`     | food, drink | `potion,duration,amplifier`          | nenhum   | Um efeito aplicado quando a variante é comida ou bebida. Uma quarta parte, `true`, o torna ambiente. Um efeito benéfico aparece em verde na dica de ferramenta, seguido do amplificador em algarismos romanos quando é maior que 0, e nenhum efeito que ele dê mostra partículas |

## Fluidos

*blocos e itens*

`<namespace>/fluids/*.json`

O caminho do arquivo é o nome de registro do bloco do fluido. `name` nomeia o fluido em si, seu balde e suas chaves de lang, e é o caminho do arquivo a menos que o arquivo o defina.

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

| Chave         | Obrigatório | Valor                              | Padrão                | O que faz |
| ------------- | -------- | ---------------------------------- | --------------------- | --------- |
| `name`        | não      | string                             | o nome do arquivo     | O nome de registro do fluido e de seu balde (`<name>_bucket`); o bloco mantém o caminho do arquivo |
| `still`       | não      | caminho de textura                 | água parada do vanilla | Textura do fluido parado |
| `flow`        | não      | caminho de textura                 | água corrente do vanilla | Textura do fluido em movimento |
| `color`       | não      | cor hexadecimal                    | nenhum                | Cor aplicada a essas texturas. Nas texturas de água padrão ela é multiplicada pelo azul da água do 1.12.2, então uma cor escolhida para o 1.12.2 fica igual aqui |
| `bucket`      | não      | booleano                           | `true`                | Registra um balde para ele |
| `luminosity`  | não      | 0 a 15                             | `0`                   | Luz emitida |
| `density`     | não      | int                                | `1000`                | Negativo flutua para cima, como um gás |
| `temperature` | não      | int, kelvin                        | `300`                 | A água é 300, a lava 1300 |
| `viscosity`   | não      | int                                | `1000`                | Com que lentidão flui: o fluido avança uma vez a cada viscosity / 200 ticks. A água é 1000, a lava 6000 |
| `gaseous`     | não      | booleano                           | `false`               | Tratado como um gás |
| `creativeTab` | não      | nome de aba                        | nenhum                | A aba em que o balde aparece |
| `block`       | não      | objeto                             |                       | O bloco do fluido. `material` (`water`): `water` permite nadar e se afogar, faz barcos flutuarem, apaga criaturas em chamas, mantém a terra arada úmida e evapora quando derramada no Nether; `lava` incendeia tudo o que estiver nela, não permite nadar nem se afogar e usa os sons do balde de lava; qualquer outro material não faz nada disso. `flammability` (`0`) e `fireSpread` (`0`): com que facilidade o fogo consome o bloco e se espalha a partir dele. `quantaPerBlock` (`0`, lido como 8): até onde corre a partir de uma fonte, um bloco a menos que o número, como no 1.12.2; os fluidos aqui chegam a 1, 2, 3 ou 7 blocos, então 4 e 5 correm 3 blocos e 6 ou mais correm 7. `potions` (nenhum, uma lista de efeitos dados a tudo o que estiver nele, cada um escrito `potion,duration,amplifier` com uma quarta parte opcional `true` para um ambiente) |
| `requires`    | não      | lista de ids de mods ou namespaces de pacotes | nenhum     | O arquivo é ignorado a menos que todos estejam presentes |

## Materiais, abas, sons, tags

*blocos e itens*

`<namespace>/materials/*.json`

O caminho do arquivo é o nome do material, que um item de ferramenta ou armadura então nomeia em `material`.

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

| Chave            | Obrigatório | Valor             | Padrão                  | O que faz                                                                                                            |
| ---------------- | -------- | ----------------- | ----------------------- | -------------------------------------------------------------------------------------------------------------------- |
| `harvestLevel`   | não      | 0 a 4             | `1`                     | Nível da ferramenta. 0 madeira, 1 pedra, 2 ferro, 3 diamante, 4 netherite                                            |
| `durability`     | não      | int               | `250`                   | Usos antes de quebrar                                                                                                |
| `efficiency`     | não      | float             | `6.0`                   | Velocidade de mineração. O diamante é 8                                                                              |
| `damage`         | não      | float             | `2.0`                   | Bônus de dano de ataque                                                                                              |
| `enchantability` | não      | int               | `14`                    | Quão bons são os encantamentos. O ouro é 22                                                                          |
| `repairItem`     | não      | nome de item      | nenhum                  | O que repara em uma bigorna uma ferramenta feita dele. A armadura feita dele não é reparada assim, como no 1.12.2    |
| `reduction`      | não      | lista de quatro ints |                      | Pontos de armadura, na ordem botas, calças, peitoral, capacete                                                       |
| `toughness`      | não      | float             | `0.0`                   | Resistência da armadura, como a do diamante                                                                          |
| `equipSound`     | não      | nome de som       | `item.armor.equip_iron` | Som ao vestir a armadura                                                                                             |
| `armorTexture`   | não      | prefixo de textura | o nome do arquivo      | A textura da armadura vestida, lida de `textures/models/armor/<name>_layer_1.png` e `_layer_2.png` sob esse namespace |

### Abas criativas

*materiais, abas, sons, tags*

`<namespace>/tabs/*.json`

Blocos, itens e baldes nomeiam sua aba em `creativeTab`. Um id completo, como `mypack:rubypack` ou `minecraft:combat`, é usado como escrito. Um nome simples é lido como o 1.12.2 lia o rótulo de uma aba: `buildingBlocks` vai para `minecraft:building_blocks`, `decorations` para `minecraft:functional_blocks`, `redstone` para `minecraft:redstone_blocks`, `transportation` e `tools` para `minecraft:tools_and_utilities`, `misc` e `materials` para `minecraft:ingredients`, `food` e `brewing` para `minecraft:food_and_drinks`, e `combat` para `minecraft:combat`, e qualquer outro nome simples é a aba `<namespace>:<name>` no namespace do arquivo que o nomeia. Uma aba que nenhum arquivo declara é criada para você, com título vindo de `itemGroup.<namespace>.<name>` e mostrando o primeiro item dela. O caminho do arquivo de uma aba é o nome da aba, a menos que `label` o substitua.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Chave   | Obrigatório | Valor        | Padrão            | O que faz                                                                                                                                  |
| ------- | -------- | ------------ | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| `label` | não      | string       | o nome do arquivo | O id da aba: blocos e itens a nomeiam em `creativeTab`, e o nome exibido vem de `itemGroup.<namespace>.<label>` nos arquivos de idioma     |
| `icon`  | não      | nome de item | nenhum            | O item mostrado na aba                                                                                                                     |

### Sons

*materiais, abas, sons, tags*

`<namespace>/sounds/*.json`

O nome do arquivo é livre, só a pasta é lida, e vários arquivos se acumulam.

O formato `sounds.json` do vanilla, para que um pacote possa distribuir seu próprio áudio. Um arquivo aqui registra os eventos de som; o cliente ainda lê o áudio pelo `assets/<namespace>/sounds.json` do próprio pacote, então distribua os dois, o índice em `assets` e os eventos em `data`.

### Tags

*materiais, abas, sons, tags*

`<namespace>/tags/<kind>/*.json`

As tags estão no formato do próprio jogo na pasta do próprio jogo, e um pacote as distribui como faria em um data pack: `tags/items/ores/ruby.json` (1.21.1: `tags/item/`) contendo `{ "values": ["mypack:ruby_ore"] }` põe o minério em `mypack:ores/ruby`, e um arquivo em `data/forge/tags/items/ores/ruby.json` (1.21.1: `data/c/...`) acrescenta à tag de convenção compartilhada que todo mod lê. Os blocos e itens do próprio pacote nomeiam as suas em `tags` na variante, e os arquivos são escritos para você; um `harvestTool` e `harvestToolLevel` escrevem as tags `mineable` e `needs_*_tool` da mesma forma.

O dicionário de minérios do 1.12.2 é o que as tags substituíram. Seus nomes correspondem às tags de convenção: `oreRuby` é `forge:ores/ruby` no 1.20.1 e `c:ores/ruby` no 1.21.1, `ingotCopper` é `ingots/copper`, `gemRuby` é `gems/ruby`, `dustX` é `dusts/x`, `nuggetX` é `nuggets/x`, `blockX` é `storage_blocks/x`, e `logWood`, `plankWood` e `stickWood` são `minecraft:logs` e `minecraft:planks` do próprio jogo e a de convenção `rods/wooden`. O `"remove": [...]` de um arquivo de tag retira entradas avulsas de uma tag, e `"replace": true` com um `"values": []` vazio a esvazia, de modo que uma tag de um pacote inferior ou de um mod pode ser reduzida ou limpa. Para tirar itens de todas as tags de uma vez, e do jogo, use [Blocos e itens desativados](#blocos-e-itens-desativados).

## Substituições de propriedades

*blocos e itens*

`<namespace>/overrides/<target>/<name>.json`

O caminho nomeia o alvo: tudo depois de `overrides/` é o namespace e o nome do bloco, item ou tipo de poção que está sendo alterado.

Em todo outro lugar, um pacote substitui um arquivo ou acrescenta um. Uma substituição não faz nenhum dos dois: ela altera as propriedades de um bloco, item ou tipo de poção que já existe, vanilla ou de mod, sem tocar em nenhum dos seus arquivos. O caminho nomeia o alvo, então `overrides/minecraft/stone.json` altera `minecraft:stone`, e `overrides/tconstruct/<name>.json` altera o bloco desse mod do mesmo modo.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa.

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

### Propriedades de blocos

*substituições de propriedades*

Toda chave é opcional e um arquivo altera apenas o que nomeia, então um arquivo em `overrides/minecraft/stone.json` contendo só `hardness`, `light` e `soundType` faz a pedra ser minerada quase instantaneamente, brilhar e soar como vidro. Um arquivo carrega chaves de bloco, de item e de poção juntas. Estas se aplicam quando o alvo é um bloco:

| Chave          | Valor                                        | O que faz                                                                                                                                                                                                                   |
| -------------- | -------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hardness`     | float                                        | Tempo de mineração, o mesmo valor que uma definição de bloco aceita. Sem `resistance`, ele também eleva a resistência a explosões para pelo menos o mesmo valor, como o 1.12.2 faz                                          |
| `resistance`   | float                                        | Resistência a explosões como o 1.12.2 a lê: o bloco fica com três quintos do valor, então `10` dá o `6` da pedra                                                                                                            |
| `slipperiness` | float                                        | `0.6` é chão comum, `0.98` é gelo                                                                                                                                                                                           |
| `light`        | `0` a `15`                                   | Luz emitida                                                                                                                                                                                                                 |
| `lightOpacity` | `0` a `15`                                   | Quanta luz o bloco bloqueia                                                                                                                                                                                                 |
| `soundType`    | um dos tipos de som                          | Sons de passos, de colocação e de quebra                                                                                                                                                                                    |
| `harvestTool`  | `pickaxe`, `axe`, `shovel`, `hoe` ou `sword` | O que o minera rápido, escrito nas tags de ferramenta; `harvestToolLevel`, padrão `0`, define o nível: 1 pedra, 2 ferro, 3 diamante, 4 ou mais netherite. Se o bloco solta itens sem a ferramenta certa continua como o bloco tem |
| `flammability` | int                                          | Com que facilidade queima; `fireSpread`, padrão `5`, com que facilidade o fogo o alcança                                                                                                                                    |

### Propriedades de itens

*substituições de propriedades*

| Chave           | Valor       | O que faz                                                   |
| --------------- | ----------- | ----------------------------------------------------------- |
| `maxStackSize`  | `1` a `64`  | Tamanho da pilha                                            |
| `maxDamage`     | int         | Durabilidade                                                |
| `containerItem` | nome de item | Deixado na grade de criação, como um balde é                |
| `food`          | objeto      | Torna o item comestível, veja abaixo                        |

Um nome que é ao mesmo tempo um bloco e um item, e o item de todo bloco colocável é, recebe os dois grupos de um mesmo arquivo:

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

Em `overrides/minecraft/oak_planks.json` isso faz as tábuas quebrarem quase tão rápido quanto a terra e permite comê-las. `food` aceita `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false`; `true` permite comer com a barra de fome cheia) e `effects`, cujas entradas são escritas exatamente como as de um tipo de poção. Um item que já é comida recebe novos `heal`, `saturation` e `alwaysEdible`; `effects` em um desses não é suportado, e o log avisa. Quando o item comestível coloca um bloco, mire no céu para comer, já que mirar em um bloco o coloca: essa é a ordem de uso do vanilla, não um bug.

### Efeitos de tipos de poção

*substituições de propriedades*

`effects` no nível superior do arquivo reescreve por completo a lista de efeitos de um tipo de poção:

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

Em `overrides/minecraft/swiftness.json` a Poção de Rapidez agora concede Levitação. Cada entrada aceita `potion` (obrigatório), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) e `showParticles` (`true`), o mesmo que em `potion_types/`, e a lista não pode ser vazia.

### Outros mods, recarregamentos e limites

*substituições de propriedades*

Um alvo que outro mod possui deve carregar esse mod em `requires`, para que o arquivo seja ignorado em silêncio quando o mod não está instalado, em vez de ser reportado como alvo ausente:

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

As substituições são ao vivo. Os valores originais são lembrados antes da primeira alteração, então desativar o pacote e executar `/rdplserver reload` restaura tudo ao que era, sem reiniciar; o mesmo acontece a cada entrada em um mundo. Um arquivo por alvo: quando dois pacotes substituem a mesma coisa, o arquivo do pacote posterior substitui o anterior por inteiro, e o log avisa.

Dois limites que vale conhecer. Um bloco ou item cujo próprio código calcula uma propriedade ignora o campo por trás dela, então a substituição se aplica mas não muda nada; o vanilla só faz isso com a resistência a explosões das escadas, mas os mods são livres para fazê-lo em qualquer lugar. E itens tornados comestíveis só funcionam em itens sem comportamento próprio de botão direito: um item que já faz algo quando usado continua fazendo isso.

As substituições precisam do pacote no cliente e também no servidor, já que velocidade de mineração, luz e comer acontecem todos na tela do jogador, então não servem para pacotes do lado do servidor. `overrides` na categoria de config `content` desativa a pasta por completo.

## Grupos de dureza

*blocos e itens*

`<namespace>/hardness/*.json`

O caminho do arquivo nomeia o grupo no log e nada mais o lê, então vários arquivos se acumulam.

Dá a um grupo de blocos um multiplicador de tempo de mineração, sorteado por posição de bloco. O bloco em si nunca é alterado: nada é registrado, nada é escrito no mundo, e um mundo aberto sem o pacote é vanilla comum.

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

### Mineração e detonação

*grupos de dureza*

| Chave             | Obrigatório | Valor                                  | Padrão             | O que faz                                                                                  |
| ----------------- | -------- | -------------------------------------- | ------------------ | ------------------------------------------------------------------------------------------ |
| `blocks`          | sim      | lista de nomes de blocos ou objetos    |                    | O grupo. As mesmas formas de um `replace` de worldgen                                      |
| `except`          | não      | lista de nomes de blocos ou objetos    | nenhum             | Retirado do grupo, diga o que disser `blocks`                                              |
| `miningTime`      | não      | número, ou objeto com `min` e `max`    | `1.0`              | Quantas vezes mais tempo o bloco leva para quebrar, para um jogador e para um mob `digs` igualmente |
| `blastResistance` | não      | número, ou objeto com `min` e `max`    | `1.0`              | Multiplica a resistência a explosões do bloco                                              |
| `buckets`         | não      | 1 a 256                                | `10`               | Em quantos passos o intervalo é dividido                                                   |
| `minHeight`       | não      | int                                    | o fundo do mundo   | Abaixo disto o sorteio é o passo mais duro                                                 |
| `maxHeight`       | não      | int                                    | o topo do mundo    | Acima disto o sorteio é o passo mais duro                                                  |
| `field`           | não      | objeto                                 | veja abaixo        | A forma em que o sorteio se agrupa                                                         |
| `requires`        | não      | lista de ids de mods ou namespaces de pacotes | nenhum      | O arquivo é ignorado a menos que todos estejam presentes                                   |

Um único número dá a todo bloco do grupo o mesmo multiplicador, e nada é sorteado. Um `min` e `max` sorteiam por posição: `max` onde o campo está vazio, `min` no meio de um agrupamento, e os passos entre eles decididos por `buckets`.

### Mineração de aventura e desbloqueios

*grupos de dureza*

| Chave         | Obrigatório | Valor            | Padrão  | O que faz |
| ------------- | -------- | ---------------- | ------- | --------- |
| `keeps`       | não      | booleano         | `false` | O bloco permanece onde está quando é minerado: os drops, a experiência, o desgaste da ferramenta e o som de quebra acontecem, e o bloco continua ali para ser minerado de novo, então o grupo é um veio sem fim no ritmo que `miningTime` definir. O modo criativo o remove como sempre |
| `adventure`   | não      | objeto           | nenhum  | Quem pode quebrar o grupo no modo aventura, onde nada quebra de outra forma. `tools` lista os itens dos quais um deve estar na mão, vazio para qualquer coisa segurada; `teams`, `players` e `entities` dizem quem, uma equipe pelo nome, um jogador pelo nome, um mob pelo id de entidade para a tarefa `digs`, e os três vazios significa qualquer um com a ferramenta. Sobrevivência e criativo não são afetados |
| `advancement` | não      | `namespace:path` | nenhum  | O grupo só vale para um jogador depois que ele tiver esse progresso. Dois grupos podem nomear o mesmo bloco, um com progresso e um sem, e o desbloqueado vence; um jogador sem ele recebe o grupo simples, ou o vanilla se não houver. Mobs não têm progressos, então um grupo restrito nunca chega a uma tarefa `digs`, e a resistência a explosões e o sorteio de textura, que não pertencem a nenhum jogador, vêm do grupo simples |
| `becomes`     | não      | objeto           | nenhum  | Os blocos do grupo se transformam em outro bloco, em todo o mundo, no momento em que qualquer jogador conquista `advancement`: `{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`. Todo chunk carregado é varrido de uma vez, e um chunk carregado ou criado depois é varrido ao entrar, então o bloco antigo desaparece de vez. Dê ao novo bloco um grupo próprio para mudar como ele é minerado |

### O campo

*grupos de dureza*

O sorteio não é feito para cada bloco de forma totalmente isolada, ou duro e macio seriam puro ruído, sem forma alguma. O `field` decide que forma isso toma, e `type` escolhe entre dois caminhos para chegar lá.

```json
{
  "field": { "type": "speckle" }
}
```

| Chave  | Obrigatório | Valor                 | Padrão    | O que faz                       |
| ------ | ----------- | --------------------- | --------- | ------------------------------- |
| `type` | não         | `speckle` ou `seeded` | `speckle` | Qual dos dois abaixo é usado    |

#### speckle

*o campo*

Cada bloco sorteia o seu próprio degrau, e um bloco a uma face de distância pode lhe passar um degrau mais fraco. Isso dá pontinhos densos e de grão fino, a maioria de um único bloco, com uma mancha maior aqui e ali onde eles se encontram. É a que mais se aproxima da sensação de minerar no mod em que isto se inspira.

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| Chave     | Obrigatório | Valor                      | Padrão                                 | O que faz                                                                                              |
| --------- | ----------- | -------------------------- | -------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| `chances` | não         | lista de inteiros, por mil | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Com que frequência um bloco começa em cada degrau, o mais macio por último. O que sobrar é o degrau mais duro |
| `spread`  | não         | 0.0 a 1.0                  | `0.15`                                 | Com que frequência um degrau passa para o bloco vizinho, um degrau mais fraco ou três                  |

A lista é lida do mais macio para trás, então a última entrada é o degrau mais macio e a primeira fica um acima do mais duro. Com os números acima, cerca de sete em cada dez blocos são o degrau mais duro e o resto se espalha entre eles.

#### seeded

*o campo*

As sementes ficam em uma grade calculada a partir do mundo e da posição, e o degrau de um bloco vem de quão perto ele está da semente mais próxima. Isso dá manchas menos numerosas, maiores e mais redondas, que se fundem umas com as outras, e pode fazer crescer braços.

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

| Chave      | Obrigatório | Valor           | Padrão | O que faz                                  |
| ---------- | ----------- | --------------- | ------ | ------------------------------------------ |
| `cell`     | não         | inteiro, blocos | `8`    | A distância entre as sementes              |
| `seeds`    | não         | 1 a 4           | `1`    | Sementes em cada célula                    |
| `reach`    | não         | float, blocos   | `3.0`  | Até onde chega a influência de uma semente |
| `arms`     | não         | 0 a 6           | `0`    | Braços que irradiam de cada semente        |
| `armReach` | não         | float, blocos   | `0.0`  | Até onde chegam os braços                  |

Sem `arms`, as manchas são redondas. Dar braços a uma semente a transforma em um nó com tentáculos, e os braços de nós vizinhos se estendem uns na direção dos outros, o que forma um veio em vez de uma bolha. Mantenha `reach` acima da metade de `cell`, ou as manchas não conseguem se tocar e você acaba com bolas separadas sem nada entre elas.

### Exibindo

*grupos de dureza*

O multiplicador é invisível por si só. Para que o jogador veja quais blocos são resistentes, dê ao bloco um blockstate com uma variante por faixa, todas com o mesmo peso, listadas da mais dura para a mais macia:

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

O Minecraft já escolhe uma variante a partir da posição de um bloco, e um grupo de dureza lhe entrega a faixa no lugar, de modo que a textura e o multiplicador sempre concordam. O pacote de exemplo faz exatamente isso com a sua pedra de rubi.

Três coisas precisam estar certas, e nenhuma delas avisa quando está errada.

**Exatamente `buckets` entradas, todas com o mesmo peso.** A faixa é usada como uma posição na lista, então uma lista de tamanho diferente, ou uma em que os pesos diferem, aponta em silêncio para a textura errada.

**Um nome de modelo com `block/` na frente.** Um blockstate nesta versão nomeia o arquivo do modelo por inteiro, então `"model": "mypack:block/stone_step0"` lê `models/block/stone_step0.json`; um `mypack:stone_step0` sem prefixo procura `models/stone_step0.json`, que não existe, e a entrada é descartada sem nenhum aviso.

**A mesma chave que o jogo pede.** Um bloco com um único estado usa a chave `""`, e a pedra do vanilla é um desses. Um bloco com propriedades usa a chave formada por todas elas, então uma substituição para um tronco quer `axis=x`, `axis=y` e `axis=z`, cada um com a sua própria lista.

Ative `worldgenDebug` e cada grupo de dureza é conferido com o seu modelo compilado ao entrar em um mundo, nomeando o blockstate, quantas variantes sobreviveram, que textura cada uma acabou recebendo e quais pacotes o jogo mesclou para chegar lá. É a forma mais rápida de achar qualquer um dos três problemas acima, e também avisa quando a substituição de um blockstate compartilhado alterou um estado que o grupo nunca nomeou.

### O que não alcança

*grupos de dureza*

Só a mineração do próprio jogador é alterada. Máquinas que quebram blocos leem a dureza do bloco diretamente e não são afetadas. Blocos colocados por um jogador são sorteados como quaisquer outros, já que o sorteio pertence ao lugar e não ao bloco, e um bloco levado para outro lugar assume o que o novo lugar determinar.

---

# Criação, saque e comércio

## Blocos e itens desativados

*criação, saque e comércio*

`<namespace>/disabled/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se acumulam.

Tira blocos e itens de jogo sem removê-los do registro, então os mundos mantêm seus ids e apagar o arquivo traz tudo de volta. Conteúdo do vanilla, de mods e de pacotes é tratado igualmente, incluindo os blocos e itens do próprio pacote, e um bloco desativado desativa o seu item assim como um item desativado desativa o seu bloco.

```json
{
  "requires": ["thermal"],
  "names": ["thermal:tin_ore", "thermal:deepslate_tin_ore", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "tags": ["forge:ores/tin"]
}
```

| Chave        | Obrigatório | Valor                         | Padrão | O que faz                                                                                                                                         |
| ------------ | ----------- | ----------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| `names`      | não         | lista de nomes de blocos e itens | nenhum | O que é desativado. Um nome terminado em `*` corresponde a todo nome que começa com o restante                                                    |
| `namespaces` | não         | lista de ids de mods          | nenhum | Todos os blocos e itens do mod                                                                                                                    |
| `tags`       | não         | lista de nomes de tags        | nenhum | Todo item da tag de itens e todo bloco da tag de blocos com esse nome, e ambas as tags ficam vazias. Um `#` no início é permitido                  |
| `requires`   | não         | lista de ids de mods          | nenhum | O arquivo é ignorado a menos que todos estejam carregados. Entradas `config:` e `file:` funcionam como em todo o resto                            |

Um bloco ou item desativado:

- some de todas as abas criativas e da aba de busca, e fica oculto no JEI
- não tem receita que o produza ou o use: toda receita de qualquer tipo que o tenha como resultado some, seja de criação, cozimento, cortapedra ou ferraria, e o mesmo vale para toda receita com um slot que só ele pode preencher. Um slot que também aceita outra coisa mantém a sua receita, e um slot de tag simplesmente a perde junto com a tag
- é retirado de toda tag de itens e de blocos, e passa a constar em `resourcedatapackloader:disabled`
- é removido de todo sorteio de saque, seja de baús, mobs ou pesca, dos drops de blocos e das trocas de aldeões e do comerciante andarilho, e uma pilha dele que cai some
- não pode ser colocado, usado, brandido nem coletado, e a pilha na mão é apagada quando o jogador tenta
- é apagado onde quer que uma pilha dele apareça: do inventário e do baú do End de um jogador ao entrar e a cada segundo depois, de qualquer contêiner quando um jogador o abre, e de baús e outros inventários quando o seu chunk carrega
- é removido do mundo onde estiver colocado: cada bloco dele vira ar, junto com a sua entidade de bloco, quando o seu chunk carrega

Para trocar blocos colocados por outra coisa em vez de removê-los, dê a eles uma linha `blockReplacements` como `thermal:tin_ore=minecraft:stone` no modelo de mundo, veja [Substituições](#substituições). Um bloco que o processo de substituição troca fica a cargo dele. As receitas que outro mod mantém dentro de suas próprias máquinas pertencem a esse mod e não são alcançadas. Ocultar das abas criativas e do JEI acontece no cliente, então um cliente vanilla ainda lista o item. `content.disabled` na config desliga a pasta.

Para esvaziar uma tag mantendo os seus itens em jogo, use um arquivo de tag com `"replace": true` e um `"values": []` vazio, veja [Tags](#tags).

## Receitas de fornalha e combustíveis

*criação, saque e comércio*

`<namespace>/furnace/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se acumulam.

Adiciona e remove receitas de fornalha. Uma remoção também elimina as receitas correspondentes de alto-forno, defumador e fogueira, já que o 1.12.2 mantinha todas as receitas de cozimento em uma única lista de fornalha; uma adição é apenas uma receita de fornalha.

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

Entradas em `add`:

| Chave        | Obrigatório | Valor        | Padrão | O que faz                                                                                                                                                      |
| ------------ | ----------- | ------------ | ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `input`      | sim         | nome de item | nenhum | O que entra                                                                                                                                                    |
| `output`     | sim         | nome de item | nenhum | O que sai                                                                                                                                                      |
| `count`      | não         | inteiro      | `1`    | Quantos saem                                                                                                                                                   |
| `experience` | não         | número       | `0.0`  | Experiência por item fundido, no máximo uma: 1.0 ou mais dá um ponto por item retirado, então um `count` de 2 dá dois. O minério de ferro dá 0.7                |

Uma adição cuja entrada algo já funde é ignorada e o log nomeia a receita que está no caminho, como o 1.12.2 faz; remova essa receita no mesmo arquivo ou em um anterior para substituí-la.

As entradas em `remove` são um nome de item simples, que remove toda receita que o produz, ou um objeto com `input`, `result` ou ambos para restringir. Uma remoção que não nomeia nenhum dos dois é ignorada e o log avisa.

Os arquivos se aplicam na ordem de carregamento, as remoções de cada arquivo antes das suas adições. Uma remoção em um arquivo posterior, portanto, também elimina uma adição feita por um arquivo anterior, mas nunca alcança uma adição que vem depois dela. Uma adição conta como receita de fornalha do mod ao qual o seu resultado pertence, então `blockFurnaceRecipes` e `blockedFurnaceMods` a bloqueiam como qualquer outra.

`<namespace>/fuels/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se acumulam.

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "tag": "forge:gems/ruby", "burnTime": 800 }
  ]
}
```

| Chave      | Obrigatório   | Valor           | Padrão | O que faz                           |
| ---------- | ------------- | --------------- | ------ | ----------------------------------- |
| `item`     | um dos dois   | nome de item    | nenhum | O item que queima                   |
| `tag`      | um dos dois   | id de tag       | nenhum | Tudo o que está nessa tag queima    |
| `burnTime` | sim           | inteiro, ticks  | `0`    | O carvão é 1600, uma tábua 300      |

## Poções, tipos de poção e fermentação

*criação, saque e comércio*

`<namespace>/potions/*.json`

O caminho do arquivo é o nome de registro do efeito, então `mypack/potions/ruby_sight.json` registra `mypack:ruby_sight`, que um tipo de poção então nomeia.

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

| Chave           | Obrigatório | Valor                  | Padrão                                          | O que faz                                                                                                                              |
| --------------- | ----------- | ---------------------- | ----------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| `name`          | não         | chave de tradução      | `effect.<namespace>.<name>`                     | O que o jogador vê                                                                                                                     |
| `color`         | não         | cor hexadecimal        | `FFFFFF`                                        | Cor da partícula                                                                                                                       |
| `badEffect`     | não         | booleano               | `false`                                         | Conta como prejudicial, então um olho de aranha fermentado o inverte                                                                   |
| `beneficial`    | não         | booleano               | `false`                                         | Exibido como um efeito bom                                                                                                             |
| `instant`       | não         | booleano               | `false`                                         | Aplica-se uma vez em vez de ao longo do tempo                                                                                          |
| `effectiveness` | não         | float                  | `0.5`                                           | Lido para que um arquivo do 1.12.2 carregue; nem o 1.12.2 nem esta versão agem sobre ele                                               |
| `attributes`    | não         | lista de objetos       | nenhum                                          | `attribute` (o id do jogo, como `minecraft:generic.movement_speed`), `uuid`, `amount` (`0.0`), `operation` (`0`)                       |
| `icon`          | não         | objeto                 | nenhum                                          | `x` e `y`, a coluna e a linha do ícone na folha de status do 1.12.2, cada uma `0` se omitida. Lido apenas sem `iconTexture`            |
| `iconTexture`   | não         | caminho de textura     | o ícone do RDPL, ou nenhum quando `icon` é definido | Uma imagem que um pacote fornece, como `mypack:textures/effect/rage.png`, desenhada inteira como o ícone                           |

O ícone do efeito é a textura `assets/<namespace>/textures/mob_effect/<name>.png`, de 18 por 18 como as do próprio jogo, e um pacote que a fornece ali sempre vence. Caso contrário, `iconTexture` nomeia uma imagem que um pacote fornece, que é copiada para lá inteira; `icon` sozinho escolhe o ícone de um efeito do vanilla pela sua posição na folha de status do 1.12.2, `x` na horizontal e `y` na vertical a partir de 0, como `{ "x": 2, "y": 1 }` para o super pulo; e um efeito que não nomeia nenhum dos dois mostra o ícone do RDPL.

### Tipos de poção

*poções, tipos de poção e fermentação*

`<namespace>/potion_types/*.json`

O caminho do arquivo é o nome de registro do tipo de poção, que um item `potion_bottle` então nomeia em `potionTypes`.

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| Chave      | Obrigatório | Valor            | Padrão                  | O que faz                                                                                                                                                                                                                                                                                                         |
| ---------- | ----------- | ---------------- | ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `baseName` | não         | string           | o namespace e o nome    | Nomeia a poção: a chave de lang `item.minecraft.potion.effect.<baseName>`, com `splash_potion`, `lingering_potion` ou `tipped_arrow` no lugar de `potion` para as outras formas. Um `potion_bottle` que a contém mostra o mesmo nome, e as chaves `potion.effect.<baseName>` de um pacote do 1.12.2 são convertidas |
| `effects`  | sim         | lista de objetos |                         | Veja abaixo                                                                                                                                                                                                                                                                                                       |

Cada efeito aceita `potion` (obrigatório), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) e `showParticles` (`true`).

### Poções e fermentação

*poções, tipos de poção e fermentação*

`<namespace>/brewing/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se acumulam.

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

Cada entrada é `input`, `ingredient` e `output`, que fermenta um item em outro, ou `from`, `ingredient` e `to`, que transforma um tipo de poção em outro. `ingredient` é obrigatório nos dois casos, e uma entrada também aceita `requires`, de modo que uma receita possa ser ignorada sem que o arquivo seja.

## Trabalho na bigorna

*criação, saque e comércio*

`<namespace>/anvils/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se acumulam. Cada arquivo é um trabalho.

Coloque o item nomeado no slot esquerdo de uma bigorna e o item `with` no direito, e a bigorna devolve o item da esquerda com os encantamentos listados, ou o seu `result`, pelos níveis indicados; um de cada é gasto, a menos que uma quantidade peça mais, e o resto de cada pilha permanece na bigorna. Retirar o resultado também pode render uma conquista, e o item pode ficar impedido de uso até que essa conquista seja obtida: uma espada que só golpeia depois de trabalhada.

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

| Chave          | Obrigatório | Valor                                                  | Padrão          | O que faz                                                                                                                                                                                                                                                                                  |
| -------------- | ----------- | ------------------------------------------------------ | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`         | sim         | nome de item, uma lista deles ou `{ "item", "count" }` |                 | O que vai no slot esquerdo, qualquer item de uma lista, e quantos dele um trabalho consome, um por padrão; o resto da pilha fica para o próximo. `{ "item": "minecraft:coal", "count": 8 }` com um `result` de diamante são oito carvões por um diamante                                      |
| `with`         | sim         | nome de item, uma lista deles ou `{ "item", "count" }` |                 | O que vai no slot direito, qualquer item de uma lista, e quantos dele são gastos, um por padrão: `{ "item": "minecraft:coal", "count": 10 }` pede uma pilha de pelo menos dez e consome dez. Uma bigorna nunca se manifesta por um item solitário, então todo trabalho é um par             |
| `result`       | não         | nome de item, ou `{ "item", "count" }`                 | o item da esquerda | O que sai no lugar do item da esquerda, e quantos, um por padrão, mantendo as tags do item da esquerda, de modo que uma picareta de ferro inquebrável e dez carvões podem voltar como uma de diamante inquebrável. Os encantamentos vão para o que sair                                  |
| `levels`       | não         | inteiro                                                | `1`             | Os níveis de experiência que o trabalho custa, no mínimo 1                                                                                                                                                                                                                                  |
| `enchantments` | não         | objeto de nome de encantamento para nível              | nenhum          | Com o que o item volta. Um nível que ele já tem naquele valor ou acima é deixado como está, e sem nada a elevar a bigorna não oferece nada, a menos que `grants` esteja definido                                                                                                            |
| `grants`       | não         | `namespace:path`                                       | nenhum          | Uma conquista obtida quando o trabalho é retirado. Forneça-a em `advancements/` com um critério `impossible`, para que nada mais a conceda                                                                                                                                                  |
| `locks`        | não         | booleano                                               | `false`         | Até que o jogador tenha `grants`, o item não pode golpear nada, ser usado nem cavar; ele é informado do que o item aguarda quando o pega na mão. Colocá-lo na bigorna continua permitido, e é assim que ele é desbloqueado                                                                  |

Os reparos e combinações da própria bigorna não são alterados: isto só responde quando a esquerda contém um item nomeado e a direita contém o seu `with`.

Um mob com `collectsExperience` também gasta seus níveis aqui. Enquanto segura `item` na mão principal e `with` na mão secundária e tem `levels` para pagar, ele caminha até uma bigorna, bigorna lascada ou bigorna danificada a até 16 blocos na horizontal e 4 para cima ou para baixo, e a opera quando está a 3 blocos dela: os níveis saem dos seus como saem dos de um jogador, `with` é consumido, a bigorna se desgasta como sob um jogador, e o trabalho termina na sua mão principal. Ao passar por um item caído que algum trabalho de bigorna nomeia em `with`, ele o pega para a mão secundária. `grants` e `locks` só dizem respeito a jogadores, então um mob não obtém nada de `grants` e nenhum bloqueio o detém.

## Drops de blocos

*criação, saque e comércio*

`<namespace>/block_drops/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se acumulam.

A tabela de saque de um bloco decide o que ele solta, mas fornecer uma assume a tabela inteira, e uma tabela de saque não pode dar experiência. Uma regra aqui nomeia um bloco e o que quebrá-lo solta além dos drops habituais, ou no lugar deles, experiência incluída, e deixa a tabela do próprio bloco em paz.

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

| Chave         | Obrigatório | Valor                         | Padrão  | O que faz                                                                                                                                  |
| ------------- | ----------- | ----------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| `block`       | sim         | id de bloco                   |         | O bloco que a regra observa                                                                                                                |
| `properties`  | não         | objeto de propriedade e valor | nenhum  | Só os estados com esses valores, como `{ "axis": "x" }` em um tronco; sem isso, todo estado. Um `meta` do 1.12.2 não é lido                |
| `replace`     | não         | booleano                      | `false` | Se os drops habituais são descartados antes de estes serem sorteados                                                                       |
| `advancement` | não         | `namespace:path`              | nenhum  | A regra só vale para um jogador que tenha essa conquista, de modo que o mesmo bloco pode soltar uma coisa antes e outra depois             |
| `drops`       | sim         | lista de drops                |         | Cada um sorteado por conta própria quando o bloco é quebrado                                                                               |

Cada drop:

| Chave        | Obrigatório                | Valor                          | Padrão   | O que faz                                                                                                                                                                                                                                                                                                                        |
| ------------ | -------------------------- | ------------------------------ | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`       | sim, a menos que `experience` | id de item                  |          | O que cai                                                                                                                                                                                                                                                                                                                        |
| `experience` | não                        | número ou `low-high`           |          | No lugar de um item, essa quantidade de experiência em orbes, sorteada de modo uniforme dentro do intervalo. `chance` e `silkTouch` se aplicam como para um item                                                                                                                                                                  |
| `count`      | não                        | número ou `low-high`           | `1`      | Quantos, sorteados de modo uniforme dentro do intervalo                                                                                                                                                                                                                                                                           |
| `chance`     | não                        | float                          | `1.0`    | A chance de o drop acontecer, sendo `0.05` uma quebra em cada vinte                                                                                                                                                                                                                                                               |
| `fortune`    | não                        | inteiro                        | `0`      | Até essa quantidade extra por nível de Fortuna na ferramenta                                                                                                                                                                                                                                                                      |
| `silkTouch`  | não                        | `either`, `only` ou `never`    | `either` | Se o drop exige uma coleta com Toque Suave, a recusa, ou tanto faz. Vale como uma coleta quando um jogador quebra, com uma ferramenta de Toque Suave, um bloco que pode ser coletado assim pela regra do 1.12.2: um bloco inteiro sem entidade de bloco, ou painéis de vidro, barras de ferro, teias de aranha e baús do End |

As regras veem toda quebra que solta o saque do bloco, como no 1.12.2: a de um jogador, e explosões, pistões, água corrente, mobs e um mob de pacote cavando através do bloco, que sorteiam toda regra sem `advancement`. Onde uma explosão reduz os drops do próprio bloco, cada item sorteado sobrevive com as mesmas chances; a experiência não é reduzida. Várias regras para um mesmo bloco se aplicam todas, e um `replace` em qualquer uma delas limpa antes os drops habituais.

## Saque de jogadores

*criação, saque e comércio*

`<namespace>/player_loot/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se acumulam.

O jogo não dá aos jogadores uma tabela de saque própria: a morte solta apenas o inventário, e não há nome de tabela que um pacote possa substituir. O RDPL acrescenta uma, sorteada quando um jogador morre:

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| Chave                 | Obrigatório | Valor                | Padrão  | O que faz                                                                                  |
| --------------------- | ----------- | -------------------- | ------- | ------------------------------------------------------------------------------------------ |
| `table`               | sim         | nome de tabela       |         | A tabela de saque sorteada quando um jogador morre                                         |
| `mode`                | não         | `add` ou `replace`   | `add`   | Se os itens da tabela se juntam ao inventário ou tomam o seu lugar                         |
| `rollOnKeepInventory` | não         | booleano             | `false` | Se a tabela é sorteada em uma morte que manteve o inventário                               |
| `dropLoose`           | não         | booleano             | `false` | Se os itens são colocados diretamente no chão em vez de se juntarem aos drops da morte     |

`add` solta os itens da tabela junto com o inventário, o que serve para recompensas por abates. `replace` descarta o inventário e solta apenas o que a tabela sorteia.

Com `rollOnKeepInventory` desligado, mortes sob `keepInventory` (e mortes de espectador, que sempre mantêm o inventário) não sorteiam nada. Ligá-lo mantém as mortes custosas em mundos com keep-inventory.

Vários arquivos se acumulam, cada um avaliado por conta própria. Se qualquer entrada aplicável for `replace`, o inventário é limpo uma vez antes do sorteio, então uma entrada `add` ao lado dela ainda é entregue.

A tabela é uma tabela de saque comum procurada por nome: pode ficar no pacote em `loot_tables/entities/player.json`, ser qualquer tabela do vanilla ou de mod, e ser alcançada por `loot_injections`. Contexto de saque: o jogador que morre é a entidade saqueada, o assassino (se houver) é o jogador que matou, e a fonte de dano está definida, então `killed_by_player`, `entity_properties`, `random_chance_with_looting` e o resto se comportam normalmente.

Uma função de saque é própria do RDPL, utilizável em qualquer tabela com uma entidade saqueada: `rdpl:killed_name` dá ao item solto o nome da vítima. `format` molda o nome exibido (`%s` é a vítima, por padrão só o nome), e `tag` escreve o nome simples em uma chave de string NBT para itens que o leem por conta própria.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Mods de túmulos.** Os itens sorteados se juntam aos drops habituais da morte antes que qualquer mod de túmulo os leia, então acabam no túmulo junto com todo o resto (`replace` coloca o conteúdo da tabela no túmulo em vez do inventário). Não requer configuração.

`dropLoose` ignora a lista de drops por completo: os itens são colocados diretamente no mundo, então os mods de túmulos nunca os veem; o inventário vai para o túmulo, os itens da tabela ficam no chão para o assassino. Use para espólios que pertencem ao assassino e não ao túmulo da vítima. Sem um mod de túmulo, muda pouco. Ressalva: os itens existem antes que qualquer coisa adiante possa cancelar os drops, então entradas que não devem sobreviver a uma morte cancelada devem deixá-lo desligado.

Defina `playerLoot` na categoria de config `data` como `false` para desligar a pasta por completo.

## Aldeões e trocas

*criação, saque e comércio*

`<namespace>/villagers/*.json`

O caminho do arquivo é o nome de registro da profissão, então `mypack/villagers/jeweller.json` registra `mypack:jeweller`, que uma troca então nomeia em `profession`.

```json
{
  "jobSite": "mypack:gem_bench",
  "workSound": "minecraft:entity.villager.work_toolsmith"
}
```

| Chave       | Obrigatório | Valor          | Padrão | O que faz                                                                                                                                                                                                                                                                                                              |
| ----------- | ----------- | -------------- | ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `jobSite`   | não         | nome de bloco  | nenhum | O bloco que um aldeão reivindica para assumir esta profissão, como a mesa de ferraria faz de um ferreiro de ferramentas. Sem um, nenhum bloco entrega a profissão: como no 1.12.2, um aldeão gerado ou criado a recebe ao acaso, a mantém e, sem bloco onde trabalhar, nunca reabastece                                  |
| `workSound` | não         | nome de som    | nenhum | O que toca enquanto trabalha nesse bloco                                                                                                                                                                                                                                                                               |

As carreiras são uma ideia do 1.12.2 que o jogo não tem mais: uma profissão é um único conjunto de trocas, então um pacote que tinha duas carreiras fornece dois arquivos de aldeão. A aparência do aldeão é uma textura comum, fornecida em `assets/<namespace>/textures/entity/villager/profession/<name>.png` e `textures/entity/zombie_villager/profession/<name>.png`, exatamente onde o jogo guarda as suas. Uma troca que nomeia uma profissão vanilla do 1.12.2 junto com a sua `career`, como `minecraft:smith` com `armor`, vai para a profissão em que essa carreira se tornou, aqui `minecraft:armorer`. O `texture` e o `zombieTexture` de um arquivo do 1.12.2, uma skin inteira que um pacote fornece, são copiados para esses dois caminhos quando o pacote não tem nada ali, e a skin é desenhada por cima do próprio aldeão.

### Trocas

*aldeões e trocas*

`<namespace>/trades/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se acumulam.

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

| Chave        | Obrigatório | Valor                | Padrão | O que faz                                                                                                         |
| ------------ | ----------- | -------------------- | ------ | ----------------------------------------------------------------------------------------------------------------- |
| `profession` | sim         | nome de profissão    |        | De quem é esta troca. Um nome vanilla do 1.12.2 com a sua `career` também é lido, veja acima                      |
| `level`      | não         | inteiro              | `1`    | Em que nível de troca ela aparece, de 1 a 5. Um nível maior se junta ao nível 5, o mais alto que um aldeão alcança |
| `maxUses`    | não         | inteiro              | `12`   | Vezes que pode ser usada antes de travar                                                                          |
| `xp`         | não         | inteiro              | `2`    | Experiência que o aldeão ganha por troca rumo ao próximo nível                                                    |

Uma pilha é `item` com `min` (`1`) e `max` (`min`), então um preço fixo é apenas `min`.

---

# Criaturas e perigos

## Variantes de entidades

*criaturas e perigos*

`<namespace>/entities/*.json`

O caminho do arquivo é o nome de registro da variante, então `mypack/entities/angry_cow.json` registra `mypack:angry_cow`, que é ao que `becomes`, um ovo de spawn e o salvamento de um mundo se referem.

Um arquivo aqui cria uma nova entidade a partir de uma que já existe. É uma entidade de verdade por mérito próprio, com seu próprio nome de registro, seu próprio nome no mundo, seu próprio ovo de spawn e uma tabela de saque própria se você der uma, construída sobre o comportamento de outra entidade em vez de substituí-la. Nada na entidade que ela copia muda.

Todas as chaves, mostradas de uma vez. Um arquivo de verdade escreve só as que precisa.

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

### Identidade

*variantes de entidades*

| Chave           | Obrigatório | Valor                                 | Padrão | O que faz                                                                                                                                                                                                         |
| --------------- | ----------- | ------------------------------------- | ------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entity`        | sim         | `namespace:name`                      | nenhum | A entidade sobre a qual construir. De qualquer mod, desde que aceite um construtor simples de mundo                                                                                                               |
| `name`          | não         | string                                | nenhum | O nome que ela leva no mundo, nas mensagens de morte e no seu ovo                                                                                                                                                 |
| `showName`      | não         | booleano                              | `false`| Mostra o nome sem precisar olhar para ela                                                                                                                                                                         |
| `profession`    | não         | `namespace:name`                      | aleatória | Para um aldeão, a profissão que ele exerce                                                                                                                                                                     |
| `baby`          | não         | booleano ou 0.0 a 1.0                 | `false`| Com que frequência uma nasce jovem, e continua assim. `true` é sempre, um número é essa fração delas                                                                                                              |
| `becomes`       | não         | lista                                 | nenhum | Outras variantes em que esta pode se transformar ao surgir, por peso. Veja abaixo                                                                                                                                 |
| `egg`           | não         | booleano ou objeto                    | `true` | Um ovo de spawn, colorido como o ovo da entidade que ela copia. `{ "primary": "AABBCC", "secondary": "112233" }` escolhe as suas próprias cores, `false` omite o ovo                                              |
| `keepsBaseBaby` | não         | booleano                              | `false`| Se o sorteio de filhote da própria base também é executado. Sem isso, uma variante baseada em zumbi só nasce jovem como `baby` determina, sem filho do sorteio do próprio zumbi e sem jóquei de galinha         |
| `requires`      | não         | lista de ids de mods ou namespaces de pacotes | nenhum | A variante é omitida a menos que todos estejam presentes                                                                                                                                       |

Uma variante é uma classe própria, então um mundo que contém uma depende do pacote que a criou, do mesmo modo que depende de um mod. Retire o arquivo e as criaturas desse mundo vão junto.

**Um ovo ou spawner dando uma mistura.** Uma variante é uma classe própria, então sozinha ela sempre gera exatamente o que diz. `becomes` é como um pacote quebra isso: uma lista de variantes em que esta pode se transformar ao surgir, cada uma com um peso, decidida por criatura.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Nomear a si mesma é como ela continua como está, e os pesos são as chances. Coloque isso em `mypack:walker` e um ovo e uma entrada de spawn dão principalmente walkers com um pequeno ocasional, como um ovo de zumbi lhe dá um bebê de vez em quando. Acontece quando a criatura entra no mundo, então vale para ovos, `/summon` e spawn natural por igual, e a criatura que chega é uma de verdade da variante escolhida, com tudo o que essa variante diz. Um spawner é mais estrito: ele sorteia só entre as variantes do mesmo mob base e da mesma equipe que a variante a que está configurado, então um spawner de zumbi gera os zumbis dessa equipe e seus filhotes, e nunca uma criatura de outro tipo ou de outra equipe, como um spawner de zumbi vanilla continua sendo um spawner de zumbi. Uma variante alcançada assim não se transforma de novo, então duas variantes podem nomear uma à outra sem entrar em ciclo.

**Onde `baby` se encaixa.** O jogo não tem um zumbi bebê próprio: há um único zumbi que sorteia se é uma criança ao surgir. `baby` diz com que frequência, então `"baby": 0.05` é o hábito do vanilla e `"baby": true` é sempre. Uma variante não recebe por cima o sorteio do próprio zumbi, então nenhuma criança ou jóquei de galinha aparece sem que `baby` o tenha pedido; `keepsBaseBaby` devolve esse sorteio. Entre si, são duas formas de chegar ao mesmo ponto, e qual usar depende da diferença que você quer: `baby` sozinho dá uma variante que às vezes é jovem, `becomes` dá várias variantes que diferem no que você quiser, e uma mistura de ambos funciona.

### Aparência

*variantes de entidades*

| Chave        | Obrigatório | Valor                                  | Padrão       | O que faz                                                                                                         |
| ------------ | ----------- | -------------------------------------- | ------------ | ----------------------------------------------------------------------------------------------------------------- |
| `texture`    | não         | `namespace:textures/entity/<file>.png` | nenhum       | Uma skin própria, organizada do mesmo modo que a da entidade que ela copia                                        |
| `leftHanded` | não         | booleano                               | `false`      | Segura a arma na outra mão                                                                                        |
| `glowing`    | não         | booleano                               | `false`      | Contornada através das paredes                                                                                    |
| `invisible`  | não         | booleano                               | `false`      | Não é desenhada, embora o seu equipamento seja                                                                    |
| `scale`      | não         | float                                  | `1.0`        | O tamanho com que é desenhada, e o tamanho da sua hitbox                                                          |
| `angryScale` | não         | float                                  | `scale`      | O tamanho a que incha enquanto tem algo para atacar, e por três segundos depois de perder o alvo                  |
| `width`      | não         | float                                  | o da base    | A largura da sua hitbox, antes de `scale` ser aplicado                                                            |
| `height`     | não         | float                                  | o da base    | A altura da sua hitbox, antes de `scale` ser aplicado                                                             |
| `bright`     | não         | booleano                               | `false`      | Desenhada com luz total onde quer que esteja, como sob o sol do meio-dia, então nunca é escurecida pela noite, sombra ou caverna |
| `hideArmor`  | não         | booleano                               | `false`      | Veste a sua armadura sem que ela seja desenhada                                                                   |
| `hideHeld`   | não         | booleano                               | `false`      | O mesmo para o que ela estiver segurando                                                                          |
| `tint`       | não         | cor hexadecimal                        | nenhum       | Colore a entidade ao ser desenhada                                                                                |
| `tintParts`  | não         | lista de `body`, `armor`, `held`       | `["body"]`   | Que partes a coloração alcança                                                                                    |

`scale` altera tanto o modelo quanto a hitbox nos dois lados, então o que você vê é o que você pode acertar. Uma criatura que muda o próprio tamanho, um animal crescendo ou um zumbi que é criança, é escalada em torno do tamanho que ela escolheu, de modo que os dois não brigam. `angryScale` a incha enquanto ela tem um alvo e a devolve a `scale` quando o perde. Como o cliente nunca é informado do que a criatura está caçando, a flag de corrida carrega essa notícia: ela é definida em uma variante que usa `angryScale` e em nada mais, então um mod que leia a corrida nas suas variantes a verá mudar. Crescer sob um teto baixo é possível, como quando um slime cresce, então mantenha a diferença modesta.

Uma `texture` é vinculada no lugar da que a entidade usaria normalmente, seja qual for o renderizador que ela herda, então funciona para entidades de mods tanto quanto para as do vanilla. Ela precisa corresponder ao modelo sobre o qual é desenhada, já que o modelo é o da entidade base, uma skin, não uma forma nova. As camadas mantêm as suas próprias texturas, então a armadura ainda parece armadura em um zumbi com skin trocada.

A armadura só é desenhada em uma entidade cujo renderizador tem uma camada de armadura, ou seja, os mobs humanoides e os aldeões. Uma variante de vaca ou de aranha pode carregar armadura e recebe a sua proteção, mas nada a desenha, então `armor` em `attributes` costuma ser o jeito mais limpo de deixar uma criatura assim resistente. `hideArmor` serve ao outro caso: um humanoide que deve manter a armadura nos seus slots, pela proteção ou por um mod que os lê, sem que ela seja vista.

### Seus sons

*variantes de entidades*

| Chave         | Obrigatório | Valor    | Padrão    | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ------------- | ----------- | -------- | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `sounds`      | não         | objeto   | os da base | `ambient`, `hurt` e `death`, cada um um evento de som registrado, e um nome do 1.12.2 ainda é lido, veja [nomes de sons](#listas-de-valores). Mais três para os quais ela não tem som de base: `target` toca uma vez cada vez que ela escolhe um alvo, e `explode` é como soa a sua explosão, seja ao explodir a si mesma com `explodes` ou ao arremessar TNT com `throws`. `throw` toca quando ela arremessa qualquer coisa com `throws`, no lugar do arremesso de bola de neve, ou do chiado do pavio para o TNT. `targetVaries` desloca cada execução de `target` para cima ou para baixo em uma quantidade aleatória dentro desse número de semitons, então `3` varia um quarto de oitava para cada lado; `0` a toca como está. O som da explosão sai no lugar do som próprio do jogo |
| `soundVolume` | não         | número   | `1.0`     | Quão altos são esses sons                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `soundPitch`  | não         | número   | `1.0`     | Quão agudos tocam. Abaixo de 1 é mais grave, acima de 1 é mais estridente                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `silent`      | não         | booleano | `false`   | Não faz som                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |

### Vida, dano e efeitos

*variantes de entidades*

| Chave               | Obrigatório | Valor                                              | Padrão            | O que faz                                                                                                                                                                                                                                                                                                      |
| ------------------- | ----------- | -------------------------------------------------- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `immuneTo`          | não         | lista de tipos de dano                             | nenhum            | Dano que ela ignora, pelos nomes que o 1.12.2 usava, `fall`, `drown`, `explosion`, `explosion.player`, `magic`, `indirectMagic`, `mob`, `player`, `inWall` e os demais, ou por um id de tipo de dano. Veja [tipos de dano](#listas-de-valores)                                                                  |
| `fallDamage`        | não         | float                                              | `1.0`             | Multiplica o dano que uma queda causa. `0` elimina o dano de queda                                                                                                                                                                                                                                             |
| `absorption`        | não         | float                                              | `0`               | Corações extras além da sua vida                                                                                                                                                                                                                                                                               |
| `creatureAttribute` | não         | `undefined`, `undead`, `arthropod` ou `illager`    | o da base         | Como ela é contada, de modo que Julgamento e poções de cura a tratem de acordo                                                                                                                                                                                                                                 |
| `effects`           | não         | lista de objetos                                   | nenhum            | Efeitos que ela sempre tem: `{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                                                                                                               |
| `fireproof`         | não         | booleano                                           | `false`           | Nunca pega fogo de modo algum, então nunca se fere com fogo ou lava e nunca queima à luz do dia                                                                                                                                                                                                                |
| `invulnerable`      | não         | booleano                                           | `false`           | Não sofre dano de nada, exceto do vazio e do criativo                                                                                                                                                                                                                                                          |
| `attributes`        | não         | objeto                                             | nenhum            | `maxHealth`, `movementSpeed`, `attackDamage`, `attackSpeed`, `knockbackResistance`, `followRange`, `armor`. Um atributo que a entidade normalmente não tem lhe é concedido. `attackSpeed` é golpes por segundo para um lutador corpo a corpo, `1` como o jogo tem, então `2` golpeia com o dobro da frequência |
| `hurtResistance`    | não         | inteiro, ticks                                     | os `20` do jogo   | Quanto tempo depois de um golpe ela não pode ser ferida de novo. Golpes mais rápidos que a metade disso são perdidos, então um atacante rápido quer um alvo com menos                                                                                                                                          |
| `ignoresEffects`    | não         | lista de nomes de efeitos                          | nenhum            | Efeitos que nunca pegam nela, quem ou o que quer que os aplique: um golpe, uma poção arremessável, um sinalizador, uma flecha, `/effect`. `all` recusa todo efeito, então uma variante começa como uma folha em branco. Os seus próprios `effects` ainda são aplicados                                         |

### Movimento

*variantes de entidades*

| Chave            | Obrigatório | Valor           | Padrão    | O que faz                                                                                                                                                                                                                                                                                                                                                      |
| ---------------- | ----------- | --------------- | --------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `jumpMultiplier` | não         | float           | `1.0`     | Quanto mais alto ela salta que a entidade que copia                                                                                                                                                                                                                                                                                                            |
| `maxFallHeight`  | não         | inteiro         | o da base | Até que altura ela desce ao traçar caminhos                                                                                                                                                                                                                                                                                                                    |
| `noAI`           | não         | booleano        | `false`   | Fica onde é colocada e não faz nada                                                                                                                                                                                                                                                                                                                            |
| `leashable`      | não         | booleano        | `false`   | Pode ser conduzida em um laço, mesmo que a entidade que ela copia nunca pudesse                                                                                                                                                                                                                                                                                |
| `steerable`      | não         | booleano        | `false`   | Pode ser guiada enquanto montada                                                                                                                                                                                                                                                                                                                               |
| `pathPriorities` | não         | objeto          | nenhum    | Por onde ela andará, como `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` e o resto dos tipos de caminho do jogo, cada um um número em que um negativo significa nunca. Os `DANGER_CACTUS` e `DAMAGE_CACTUS` do 1.12.2 são lidos como `DANGER_OTHER` e `DAMAGE_OTHER`, onde esta versão classifica o cacto junto com os arbustos de bagas doces             |
| `stepHeight`     | não         | float, blocos   | o da base | Que altura de degrau ela sobe sem pular                                                                                                                                                                                                                                                                                                                        |
| `climbs`         | não         | booleano        | o da base | Ligado, escala qualquer parede em que anda, como uma aranha, seja qual for a sua base. Desligado, não escala nada, nem uma escada de mão, e uma aranha fica no chão                                                                                                                                                                                            |
| `teleports`      | não         | booleano        | `true`    | Se um enderman ou um shulker pode se teletransportar. Desligado, ele fica onde está, mesmo à luz do dia e na água                                                                                                                                                                                                                                              |
| `walks`          | não         | booleano        | `false`   | Um coelho anda como os outros animais em vez de se mover aos pulos. Só um coelho lê isto                                                                                                                                                                                                                                                                       |

### Água

*variantes de entidades*

| Chave                | Obrigatório | Valor    | Padrão  | O que faz                                                                                                                                                                                                                                              |
| -------------------- | ----------- | -------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `breathesUnderwater` | não         | booleano | `false` | Nunca se afoga, e afunda para andar no fundo em vez de nadar até a superfície. Ainda se orienta pelo chão, então uma água funda da qual ela não consegue sair a prenderá                                                                                |
| `swims`              | não         | booleano | `false` | Move-se pela água como uma lula ou um guardião, e nunca se afoga. Encontra o caminho pela água em vez de por terra, então pertence à água e fica encalhada fora dela                                                                                   |
| `amphibious`         | não         | booleano | `false` | Anda em terra e nada de verdade na água, mudando a forma como encontra o caminho ao entrar e sair da água. Nunca se afoga. O que quer que perseguisse é esquecido na beira da água, então ela hesita por um instante a cada travessia                  |
| `waterSlowdown`      | não         | float    | `0.8`   | Quanto a água a retarda. Maior é mais rápido                                                                                                                                                                                                           |

### Combate

*variantes de entidades*

| Chave           | Obrigatório | Valor                   | Padrão                | O que faz                                                                                                                                                                                                                                                                                                                                                                                                   |
| --------------- | ----------- | ----------------------- | --------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hostile`       | não         | booleano                | `false`               | Ataca o que consegue alcançar, e revida quando ferida. Uma variante hostil conta como monstro para o jogo, seja qual for a sua base, então o limite de monstros a contém. O modo pacífico a elimina só quando a sua base é um monstro; qualquer outra base permanece, incapaz de ferir um jogador ali. Ela abandona as tarefas de animal que a base trazia, reprodução, ser atraída, seguir um pai, um dono ou a própria espécie, sentar |
| `targets`       | não         | lista de nomes de entidades | o jogador         | O que ela sai procurando enquanto hostil. `minecraft:player` é entendido mesmo que o jogador não seja uma entidade registrada                                                                                                                                                                                                                                                                              |
| `attackReach`   | não         | float, blocos           | o seu tamanho         | Até onde chega um golpe corpo a corpo. O jogo alcança duas vezes a largura, e por isso uma criatura ampliada acerta de mais longe; isto o define de forma direta                                                                                                                                                                                                                                            |
| `knockback`     | não         | float                   | o da base, `0.4`      | Com que força os seus golpes empurram. `0` não empurra nada                                                                                                                                                                                                                                                                                                                                                 |
| `hitEffects`    | não         | booleano                | `true`                | Se ela aplica no que acerta o efeito que a entidade que copia aplica: o Wither de um esqueleto Wither, o veneno de uma aranha das cavernas, a fome de um zumbi-múmia. Desligado, ela acerta só com dano                                                                                                                                                                                                      |
| `hitFire`       | não         | booleano                | `true`                | Se ela incendeia o que acerta quando a entidade que copia o faria: um zumbi em chamas, a bola de fogo de uma chama. Desligado, nada do que ela faz inicia fogo no seu alvo                                                                                                                                                                                                                                  |
| `passive`       | não         | booleano                | `false`               | Impede que ataque qualquer coisa, seja qual for o seu comportamento normal                                                                                                                                                                                                                                                                                                                                  |
| `threatLeast`   | não         | inteiro                 | `0`                   | A menor faixa de ameaça em que um jogador ou outro portador a até 128 blocos precisa estar para que a variante surja naturalmente. `0` gera como de costume                                                                                                                                                                                                                                                 |
| `threatHostile` | não         | inteiro                 | `0`                   | A menor faixa de ameaça em que um jogador precisa estar para que a variante o ataque por conta própria. Abaixo dela a variante é dócil com esse jogador, embora ainda revide quando golpeada. `0` ataca como de costume                                                                                                                                                                                      |

`hostile` também elimina o comportamento que fazia a criatura fugir: um animal que evitava jogadores ou entrava em pânico ao ser ferido não faz nenhum dos dois quando hostil, já que do contrário fugiria daquilo que deve atacar. Requer uma entidade que ande pelo chão, pois usa o mesmo comportamento de ataque que o vanilla dá aos seus próprios mobs. Uma base voadora ou nadadora é registrada no log e deixada em paz. `passive` funciona de modo mais amplo, mas só alcança o comportamento construído do jeito que o vanilla o constrói; um mod cuja hostilidade está escrita no seu próprio código de tick ou de dano não é algo que um pacote consiga dissuadir.

### Equipamento, drops e experiência

*variantes de entidades*

| Chave                | Obrigatório | Valor                       | Padrão    | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| -------------------- | ----------- | --------------------------- | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `lootTable`          | não         | `namespace:entities/<name>` | o da base | O que ela solta. Sem isto, solta o que a entidade que ela copia solta                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `experience`         | não         | inteiro                     | o da base | Quanta experiência ela solta                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `collectsExperience` | não         | booleano                    | `false`   | Recolhe experiência como um jogador: orbes a até oito blocos flutuam até ela e são absorvidos ao toque, o Remendo no seu equipamento é reparado primeiro, e os pontos formam níveis na curva do próprio jogador, mantidos no mob através de um salvamento. O que ela mata solta a sua experiência como se um jogador tivesse feito o abate, um bloco que a sua tarefa `digs` quebra solta a experiência do próprio bloco, e um sorteio de experiência de `block_drops` também cai para ela. Ao morrer, solta sete por nível até cem, a menos que `keepInventory` esteja ligado. Objetivos com o critério `xp` ou `level` carregam o seu total e nível em uma linha nomeada pelo seu UUID, então uma função os lê com `execute if score` ou um seletor `scores={<objective>=N..}`. Ela gasta seus níveis em trabalho de bigorna como um jogador, veja [Trabalho na bigorna](#trabalho-na-bigorna) |
| `dropChance`         | não         | 0 a 1                       | `0`       | Qual a chance de cada peça de equipamento cair                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `picksUpLoot`        | não         | booleano                    | `false`   | Pega o que pisa                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `equipment`          | não         | objeto                      | nenhum    | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, cada um um nome de item                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |

Uma variante solta o que a entidade que ela copia solta, porque a tabela de saque é fixada no código da própria entidade e não procurada por nome. `lootTable` a aponta para uma tabela sua, que você então fornece em `loot_tables/entities/<name>.json` como qualquer outra.

### Comportamentos especiais

*variantes de entidades*

| Chave            | Obrigatório | Valor           | Padrão          | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| ---------------- | ----------- | --------------- | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `digs`           | não         | booleano        | `false`         | Cava através do que quer que esteja entre ela e o seu alvo, com a ferramenta na mão: uma pá através de terra, areia e cascalho, uma picareta através de pedra, um machado através de madeira, e só o que o material daquela ferramenta consegue quebrar, então uma picareta de madeira nunca abre minério de ferro e nada abre obsidiana abaixo do diamante. Um bloco leva o tempo que levaria para um jogador com aquela ferramenta, solta o que soltaria, e desgasta a ferramenta. Dê-lhe a ferramenta com `equipment`; de mãos vazias ela não cava nada, e não cava nada onde `mobGriefing` está desligado. Ela nunca procura um caminho alternativo: com um alvo, anda direto até ele e cava o que estiver no caminho, e onde a ferramenta não abre o bloco ela fica parada empurrando. Requer `hostile`. Ela toma seus alvos sem precisar vê-los, já que aquilo para onde cava está, por natureza, atrás de alguma coisa |
| `throws`         | não         | booleano        | `false`         | Arremessa o que segura no seu alvo à distância, e se for TNT ela o acende e recua. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `throwAmmo`      | não         | inteiro         | nenhum          | Quantos ela tem para arremessar. Omitido, nunca falta                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `throwReload`    | não         | inteiro, segundos | `explosionFuse` | Quanto tempo a mão fica vazia antes de ela sacar outro                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `throwRetreat`   | não         | inteiro, segundos | `explosionFuse` | Quanto tempo ela se mantém afastada depois de um arremesso antes de voltar                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `throwPower`     | não         | float           | `1.0`           | Com que força arremessa. Dobrá-lo dobra aproximadamente o alcance                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `throwArc`       | não         | float           | `0.35`          | Quão alto ela lança. Mais alto paira por mais tempo, perto de zero é um arremesso reto, abaixo de zero arremessa para baixo                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `throwReturns`   | não         | booleano        | `false`         | O que ela arremessa voa como um tridente: acerta com o `attackDamage` da variante, ou 8 em uma base sem ele, e então volta voando para a sua mão como a Lealdade traz um tridente de volta. Nunca se esgota e é mirado no alvo como um esqueleto mira, mais rápido com `throwPower` e com menos dispersão em dificuldades mais altas, e quem arremessa mantém a posição enquanto ele voa, então `throwAmmo`, `throwReload`, `throwRetreat` e `throwArc` não se aplicam. O TNT é arremessado como sempre                                                                                                                                                                                                                                                                                                         |
| `explodes`       | não         | booleano        | `false`         | Explode a si mesma junto ao seu alvo, como um creeper. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `explosionPower` | não         | número          | `3.0`           | Quão grande é a explosão. Um creeper é 3, o TNT é 4. Em uma base de creeper ou ghast, escrever isto ou `explosionFuse` também define a explosão da própria base sem `explodes`: o tamanho da explosão e o pavio de um creeper, a bola de fogo de um ghast, cada um em números inteiros, então um ghast que receba apenas `explosionFuse` explode com 3 em vez do seu 1                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `explosionFuse`  | não         | inteiro, ticks  | `30`            | Quanto tempo ela chia antes de explodir, e o pavio da própria base de creeper                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `explosionFire`  | não         | booleano        | `false`         | Deixa fogo para trás                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `charges`        | não         | booleano        | `false`         | Investe contra o alvo à distância e atinge com um knockback pesado ao contato, como um devastador faz, e então descansa antes da próxima investida. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `pounces`        | não         | booleano        | `false`         | Agacha, então salta sobre o alvo em arco e golpeia ao pousar, como uma raposa faz. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `sniffs`         | não         | inteiro, blocos | `0`             | Ouve jogadores se movendo a essa distância em blocos, com ou sem paredes, e vai até onde os ouviu; um jogador agachado ou parado não é ouvido, e um que ela então vê se torna o seu alvo. `0` não escuta. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `fleesWhenHurt`  | não         | 0.0 a 1.0       | `0`             | Interrompe a luta e foge de quem quer que esteja enfrentando enquanto a sua vida está abaixo dessa fração, e volta quando passa dela. `0` nunca foge. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `sleepsByDay`    | não         | booleano        | `false`         | Procura sombra de dia e fica parada ali até a noite ou até que algo a ataque. Enquanto descansa, fica deitada de lado                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `home`           | não         | inteiro, blocos | `0`             | Fica a essa quantidade de blocos em torno do ponto onde primeiro esteve, vagando dentro dele e voltando quando se afasta. `0` vaga livremente                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `patrols`        | não         | booleano        | `false`         | Percorre a terra em longos trechos com outros da sua espécie seguindo um líder, como uma patrulha de saqueadores faz. Um grupo que surge junto escolhe um líder; os demais ficam a poucos blocos dele, e quando o líder escolhe um alvo todos o fazem. Um seguidor que perde o líder assume a liderança. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `swoops`         | não         | booleano        | `false`         | Circula acima do alvo e mergulha através dele, golpeando na passagem, como um fantasma faz. A variante recebe um auxiliar de voo, então voa enquanto caça e pousa no chão quando ociosa; precisa de uma base que seja uma criatura, um papagaio serve, e um morcego não. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `gusts`          | não         | booleano        | `false`         | Prepara-se e solta uma rajada de vento contra o alvo à distância, lançando tudo ao redor dele para trás e para cima, como a carga de vento de uma brisa faz. Requer `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `gustPower`      | não         | float           | `1.5`           | Com que força uma rajada lança. Um golpe de um mob é 0.4, um encantamento forte de repulsão cerca de 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |

**Arremessar em vez de investir.** `explodes` manda uma criatura se atirar para explodir. `throws` é o outro temperamento: ela mantém a distância, arremessa o que estiver na sua mão principal no que está enfrentando, e se for TNT ela o acende, o arremessa e recua enquanto ele queima.

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

Arremessar esvazia a sua mão, porque ela arremessou a coisa. Ela então se mantém afastada por `throwRetreat`, saca outro depois de `throwReload` e volta ao seu alvo: um ciclo de lançar, recuar, recarregar, se aproximar. Dê-lhe um `throwAmmo` e esse ciclo termina quando a contagem acaba, a sua mão ficando vazia de vez e o seu ataque comum assumindo. Omita `throwAmmo` e ela nunca fica sem munição.

A contagem é gravada na criatura, então não se reabastece só porque um chunk foi descarregado e carregado de novo. Qualquer coisa que não seja TNT voa como um item e cai, o que torna um sapador atirando pedras ou carne podre tão fácil quanto um atirando explosivos.

`explosionFuse` continua sendo o pavio do TNT arremessado, e vale no lugar de qualquer um dos temporizadores que você omitir, então uma variante escrita antes destas chaves se comporta exatamente como antes.

Como o arremesso em si voa é questão de `throwPower` e `throwArc`. O primeiro é um multiplicador do impulso, e como o impulso já cresce com a distância, aumentá-lo alonga o alcance sem mudar quanto tempo o arremesso fica no ar. O segundo é a elevação, e ele muda a forma: alto, ela lança por cima de uma parede e leva o seu tempo; perto de zero, é atirado reto e cai quase de imediato; abaixo de zero, é lançado para baixo em algo que está embaixo. Ambos deixam o pavio em paz, então uma carga lançada em arco e uma reta explodem o mesmo número de segundos depois de deixar a mão, o que decide se uma estoura no alto ou cai primeiro e espera. Até onde ela arremessará é o seu `followRange`, e ela se aproxima como de costume quando você está a menos de três blocos, então é perigosa à distância e comum na sua cara.

### Tarefas

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `tasks` | não | lista | nenhum | Qualquer tarefa que o jogo tenha, adicionada à variante pelo nome, com a prioridade que você escolher, ou removida do que a base trazia. A lista segue abaixo |

**Qualquer tarefa que o jogo tenha.** As chaves acima são comportamentos próprios do RDPL. `tasks` vai além delas e alcança toda tarefa que o próprio vanilla usa, em qualquer base: uma entrada é um objeto que nomeia a `task` e sua `priority`, mais o que aquela tarefa lê; um nome precedido de `-` remove toda tarefa daquele tipo que a base trazia. As prioridades começam em 0, e o vanilla mantém as suas entre 1 e 8, de modo que uma tarefa em 0 vence tudo o que a base faz e uma em 9 só roda quando nada mais quer rodar.

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

A lista é aplicada depois que `hostile`, `passive` e os comportamentos acima já fizeram o seu trabalho, então ela tem a última palavra. Tarefas que movem o corpo se bloqueiam entre si: uma só roda quando nada à sua frente na prioridade está movendo a criatura, e o ataque que um monstro já traz fica em 2, de modo que um salto ou uma fuga em um zumbi precisa de prioridade 1, ou nunca terá a sua vez; a aranha e o lobo mantêm o salto à frente do ataque pelo mesmo motivo. Uma tarefa que a base já executa é adicionada uma segunda vez em vez de substituída; remova a antiga primeiro. Algumas tarefas só fazem sentido em uma base que tenha aquilo que elas controlam: uma luta com arco exige uma base que atire, sentar exige uma base que possa ser domada e comerciar exige um aldeão. Peça uma delas em uma base que não pode comportá-la e o log diz de qual base ela precisa, e a variante fica sem a tarefa. Nesta versão, um aldeão funciona com o cérebro do jogo em vez de tarefas, então as linhas de aldeão abaixo alcançam apenas o que o cérebro deixa para as tarefas.

| Chave | Tipo | Padrão | O que faz |
| ----- | ---- | ------ | --------- |
| `priority` | int | obrigatório | Onde fica entre as tarefas da base. Menor roda primeiro |
| `speed` | número | o usual da tarefa | Com que velocidade se move enquanto a tarefa roda, como multiplicador da velocidade de caminhada |
| `nearSpeed` | número | `1.2` | `avoidEntity`: o multiplicador quando aquilo de que ela foge está perto |
| `distance` | número, blocos | o usual da tarefa | Até onde ela olha, segue, atira ou se mantém afastada |
| `near` | número, blocos | o usual da tarefa | `follow`, `followOwner`, `followOwnerFlying`: quão perto ela chega antes de parar |
| `chance` | número | o usual da tarefa | `wander`: uma chance em tantos ticks; `wanderAvoidWater`: a probabilidade, de 0 a 1, de sair da cobertura; `watchClosest`, `watchClosest2`: a probabilidade, de 0 a 1, de olhar a cada tick |
| `leap` | número | `0.4` | `leapAtTarget`: a altura do salto |
| `cooldown` | int, ticks | `20` | `attackRanged`, `attackRangedBow`: ticks entre os disparos |
| `entity` | nome de entidade | nenhum | Qual entidade a tarefa procura, evita, observa ou com a qual se reproduz. `minecraft:player` é reconhecido |
| `items` | lista de nomes de itens | nenhum | `tempt`: o que um jogador estende na mão |
| `sight` | booleano | `true` | `nearestAttackableTarget`, `targetNonTamed`: apenas o que ela consegue ver |
| `nearby` | booleano | `false` | `nearestAttackableTarget`: apenas o que está dentro do seu próprio alcance de perseguição |
| `help` | booleano | `false` | `hurtByTarget`: outros da mesma espécie por perto se juntam |
| `memory` | booleano | `false` | `attackMelee`, `zombieAttack`: continua atrás de um alvo que perdeu de vista |
| `close` | booleano | `false` | `openDoor`: fecha a porta atrás de si |
| `nocturnal` | booleano | `false` | `moveThroughVillage`: apenas à noite |
| `scared` | booleano | `false` | `tempt`: um jogador que se move rápido demais quebra o encanto |

A coluna `Lista` diz onde a tarefa fica. `tasks` é o que a criatura faz; `targets` é como ela escolhe o que perseguir, e uma tarefa de alvo sem um ataque correspondente não faz nada sozinha.

| Tarefa | Precisa de | Lista | Lê | O que faz |
| ------ | ---------- | ----- | --- | --------- |
| `attackMelee` | uma criatura que anda | `tasks` | `speed`, `memory` | Anda até o alvo e o golpeia |
| `attackRanged` | uma base que atira | `tasks` | `speed`, `cooldown`, `distance` | Mantém a distância e atira o que a sua base atira |
| `attackRangedBow` | um monstro que atira | `tasks` | `speed`, `cooldown`, `distance` | A luta com arco do esqueleto: se desloca, puxa e solta |
| `avoidEntity` | uma criatura que anda | `tasks` | `entity`, `distance`, `speed`, `nearSpeed` | Foge da entidade nomeada quando ela chega a menos de `distance` |
| `beg` | um lobo | `tasks` | `distance` | Pede comida a um jogador que a estende na mão |
| `breakDoor` | qualquer base | `tasks` |  | Quebra as portas de madeira em seu caminho, na dificuldade difícil |
| `creeperSwell` | um creeper | `tasks` |  | Chia e explode ao lado do alvo |
| `defendVillage` | um golem de ferro | `targets` |  | Vai atrás de quem atacou um aldeão |
| `eatGrass` | qualquer base | `tasks` |  | Come grama, como a ovelha faz |
| `findEntityNearest` | qualquer base | `targets` | `entity` | Mira a entidade nomeada mais próxima, como o slime ou o ghast miram |
| `findEntityNearestPlayer` | qualquer base | `targets` |  | Mira o jogador mais próximo que consegue alcançar |
| `fleeSun` | uma criatura que anda | `tasks` | `speed` | Procura sombra quando o sol a atinge |
| `follow` | qualquer base | `tasks` | `speed`, `near`, `distance` | Segue outros da sua própria espécie |
| `followGolem` | um aldeão | `tasks` | | Segue um golem de ferro que estende uma papoula |
| `followOwner` | uma base domável | `tasks` | `speed`, `near`, `distance` | Segue o dono e se teletransporta até ele quando fica muito para trás |
| `followOwnerFlying` | uma base domável | `tasks` | `speed`, `near`, `distance` | O mesmo, voando |
| `followParent` | um animal | `tasks` | `speed` | Um filhote fica perto de um adulto da sua espécie |
| `harvestFarmland` | um aldeão | `tasks` | `speed` | Colhe as plantações maduras e as replanta |
| `hurtByTarget` | uma criatura que anda | `targets` | `help` | Revida contra o que o atingiu |
| `landOnOwnersShoulder` | um papagaio | `tasks` |  | Pousa no ombro do dono |
| `leapAtTarget` | qualquer base | `tasks` | `leap` | Salta sobre o alvo de perto |
| `llamaFollowCaravan` | uma lhama | `tasks` | `speed` | Entra na fila atrás de uma lhama conduzida |
| `lookAtTradePlayer` | um aldeão | `tasks` |  | Encara o jogador com quem está negociando |
| `lookAtVillager` | um golem de ferro | `tasks` |  | De vez em quando estende uma papoula a um aldeão e o observa |
| `lookIdle` | qualquer base | `tasks` |  | Olha ao redor de vez em quando |
| `mate` | um animal | `tasks` | `speed`, `entity` | Se reproduz quando está no amor, com a própria espécie ou com a `entity` nomeada |
| `moveIndoors` | uma criatura que anda | `tasks` |  | Entra em uma casa da vila ao anoitecer |
| `moveThroughVillage` | uma criatura que anda | `tasks` | `speed`, `nocturnal` | Percorre os caminhos da vila de porta em porta |
| `moveTowardsRestriction` | uma criatura que anda | `tasks` | `speed` | Volta para o seu ponto de origem quando se afasta |
| `moveTowardsTarget` | uma criatura que anda | `tasks` | `speed`, `distance` | Se aproxima de um alvo que está longe |
| `nearestAttackableTarget` | uma criatura que anda | `targets` | `entity`, `sight`, `nearby` | Mira a entidade nomeada mais próxima |
| `ocelotAttack` | qualquer base | `tasks` |  | O espreitar e o bote do gato |
| `ocelotSit` | um gato | `tasks` | `speed` | Senta em baús, camas e fornalhas acesas. As jaguatiricas domadas viraram gatos, então esta exige uma base de gato |
| `openDoor` | qualquer base | `tasks` | `close` | Abre as portas de madeira por onde passa |
| `ownerHurtByTarget` | uma base domável | `targets` |  | Vai atrás de quem atingiu o dono |
| `ownerHurtTarget` | uma base domável | `targets` |  | Vai atrás do que o dono atingiu |
| `panic` | uma criatura que anda | `tasks` | `speed` | Corre quando ferida ou em chamas |
| `play` | um aldeão | `tasks` | `speed` | As crianças brincam de pega-pega entre si |
| `restrictOpenDoor` | uma criatura que anda | `tasks` |  | Fica dentro das portas da vila à noite |
| `restrictSun` | uma criatura que anda | `tasks` |  | Fica na sombra durante o dia |
| `runAroundLikeCrazy` | um cavalo, burro, mula ou lhama | `tasks` | `speed` | Derruba um cavaleiro em quem ainda não confia |
| `sit` | uma base domável | `tasks` |  | Senta quando mandada |
| `skeletonRiders` | um cavalo esqueleto | `tasks` |  | Chama cavaleiros esqueleto quando um jogador se aproxima, o cavalo armadilha |
| `swimming` | qualquer base | `tasks` |  | Mantém a cabeça fora da água |
| `targetNonTamed` | uma base domável | `targets` | `entity`, `sight` | Mira a entidade nomeada enquanto ainda não foi domada |
| `tempt` | uma criatura que anda | `tasks` | `items`, `speed`, `scared` | Segue um jogador que estende um dos `items` |
| `tradePlayer` | um aldeão | `tasks` |  | Fica parado durante a troca |
| `villagerInteract` | um aldeão | `tasks` |  | Conversa com outros aldeões |
| `villagerMate` | um aldeão | `tasks` |  | Se reproduz quando a vila tem espaço |
| `wander` | uma criatura que anda | `tasks` | `speed`, `chance` | Vagueia por aí |
| `wanderAvoidWater` | uma criatura que anda | `tasks` | `speed`, `chance` | Vagueia, mantendo-se fora da água |
| `wanderAvoidWaterFlying` | uma criatura que anda | `tasks` | `speed` | Vagueia no ar e pousa nas árvores |
| `watchClosest` | qualquer base | `tasks` | `entity`, `distance`, `chance` | Olha para a entidade nomeada mais próxima, ou para o jogador se nenhuma for nomeada |
| `watchClosest2` | qualquer base | `tasks` | `entity`, `distance`, `chance` | O mesmo, mantido enquanto outra tarefa roda |
| `zombieAttack` | um zumbi | `tasks` | `speed`, `memory` | O ataque do zumbi, de braços erguidos |

### Spawn e despawn

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `despawns` | não | booleano | `true` | Desligada, ela permanece mesmo quando normalmente seria removida |
| `despawnAfter` | não | int, segundos | nenhum | Ela some em silêncio depois de ficar no mundo por este tempo, por mais longe que esteja qualquer pessoa |
| `persistent` | não | booleano | `false` | Nunca sofre despawn |
| `ignoresSpawnRules` | não | booleano | `false` | Surge onde for colocada, ignorando as regras que herdou |
| `spawns` | não | lista de objetos | nenhum | `creatureType`, `weight`, `min` e `max`, no mesmo formato que um bioma usa. `creatureType` é um dos [tipos de criatura](#listas-de-valores), `creature` quando omitido, e escolhe a lista de spawn à qual a entrada se junta; uma entrada com um tipo que o jogo não conhece não adiciona nada |
| `biomes` | não | lista de nomes de biomas | todos os biomas | Onde esses spawns são adicionados, por id de bioma ou pelo nome que o 1.12.2 mostrava para um bioma vanilla, como `Extreme Hills`. Sem esta chave nem `biomeTypes`, todo bioma os recebe, incluindo o Nether e o End, e um bioma que as duas listas alcançam recebe cada spawn uma só vez |
| `biomeTypes` | não | lista de tipos de bioma | nenhum | O mesmo, pela palavra de tipo |

**Uma criatura com prazo de validade.** `despawnAfter` conta em segundos a partir do momento em que a criatura entra no mundo pela primeira vez e a remove em silêncio quando o tempo acaba: sem morte, sem drops, sem som, exatamente como se ela tivesse se afastado e sido removida. O relógio é gravado na própria criatura, então continua correndo através de um salvamento e recarregamento, em vez de recomeçar toda vez que um chunk volta.

É algo independente, e não um ajuste das regras que `despawns` e `persistent` governam. Essas duas decidem se o jogo pode remover uma criatura por estar longe de todos; esta é uma promessa de que ela some em um momento determinado, aconteça o que acontecer. Uma criatura pode ser `persistent` e ainda assim ter prazo de validade, que é o que você quer para algo invocado para uma luta ou um evento e que não deve durar mais do que ele.

O relógio corre no tempo do mundo, então ele para quando ninguém está jogando e não conta os minutos que um chunk passou descarregado.

### Rede

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `trackingRange` | não | int | `80` | A que distância o cliente é informado sobre ela |
| `trackVelocity` | não | booleano | `true` | Envia a velocidade além da posição. Desligado, economiza tráfego em coisas que quase não se movem |
| `trackingFrequency` | não | int | `3` | Com que frequência, em ticks |

### Armazenamento

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

`storage` dá a uma variante de qualquer entidade espaços de itens, um tanque de fluido e um buffer de energia, cada um apenas quando o seu objeto é escrito. Cada um é oferecido como a capability de itens, fluido ou energia da entidade, de modo que tudo o que move itens, fluido ou energia para dentro de uma entidade a alcança. Onde a entidade base responde a essa capability por conta própria, como um mob faz para as mãos e a armadura e como um cavalo ou um carrinho de mina com baú faz para o seu inventário, o armazenamento do pacote responde em seu lugar, em todos os lados. Um jogador abre a tela agachando e clicando com o botão direito na entidade. O conteúdo é salvo junto com a entidade.

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `items` | não | objeto | nenhum | Dá à entidade espaços de itens. A área tem três fileiras de 9: cada barra de fluido ou de energia ocupa uma fileira e os espaços ficam com o resto, então 1x9 com as duas barras, 2x9 com uma e 3x9 com nenhuma. Sem `items`, só as barras aparecem |
| `fluid` | não | objeto | nenhum | Um tanque de fluido |
| `energy` | não | objeto | nenhum | Um buffer de Forge Energy |
| `dropsOnDeath` | não | booleano | `true` | Os itens armazenados se espalham como itens soltos onde a entidade morre. `false` os perde. Fluido e energia se perdem de qualquer modo |
| `runsDry` | não | `stops`, `slows` ou `hurts` | `stops` | O que acontece enquanto ela não consegue pagar um segundo completo de `use`. `stops`: ela deixa de pensar e fica parada onde está, com suas tarefas, alvos e comportamentos todos ociosos até ser reabastecida, embora ainda caia e possa ser empurrada. `slows`: ela se move à metade da velocidade. `hurts`: ela sofre 1 de dano a cada segundo, como de fome, então `immuneTo` com `starve` a poupa |

**Funcionando com o que carrega.** Um `use` no tanque ou no buffer é um custo de funcionamento: uma vez por segundo a entidade retira essa quantidade dele, acima de `transfer`, `buckets` e dos filtros. Quando qualquer um dos dois guarda menos que um segundo completo de `use`, a entidade ficou sem recursos: nada mais é retirado, `runsDry` decide o que acontece, e ela volta ao normal no instante em que é reabastecida. Só uma criatura gasta; em uma base que não está viva, como um carrinho de mina, `use` não faz nada.

`items`:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `filter` | não | lista de entradas de filtro | nenhum | O que os espaços aceitam. Sem ela, aceitam qualquer coisa |

`fluid`:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `capacity` | sim | int, mB | nenhum | Quanto o tanque comporta |
| `filter` | não | lista de entradas de filtro | nenhum | Quais fluidos o tanque aceita. Sem ela, aceita qualquer um |
| `buckets` | não | booleano | `false` | Um clique direito com um balde ou outro recipiente de fluido, sem agachar, o esvazia no tanque ou o enche a partir do tanque. Um clique que não move fluido é deixado para a entidade |
| `use` | não | int, mB por segundo | `0` | Quanto a entidade consome do tanque a cada segundo em que está viva. `0` não custa nada |

`energy`:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `capacity` | sim | int, FE | nenhum | Quanta energia comporta |
| `transfer` | não | int, FE | sem limite | O máximo de energia movido para dentro ou para fora em uma operação |
| `use` | não | int, FE por segundo | `0` | Quanta energia consome a cada segundo em que está viva. `0` não custa nada |

Uma entrada de filtro. A primeira entrada que corresponde decide, e tudo o que nenhuma entrada alcança é recusado:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `item` | uma das três | id de item | nenhum | Um item, como `namespace:name` |
| `tag` | uma das três | id de tag de item | nenhum | Todo item sob aquela tag, como `c:ingots/iron` |
| `fluid` | uma das três | id de fluido | nenhum | Um fluido pelo seu id, como `minecraft:water`. Lida apenas por um filtro de fluido |
| `max` | não | int | `0` | O máximo dele guardado de uma vez, contado em todos os espaços, ou em mB para um fluido. `0` é sem limite |

## Ordens de trabalho

*criaturas e perigos*

`<namespace>/orders/*.json`

O nome do arquivo é você quem escolhe, só a pasta é lida, e vários arquivos se somam. Um arquivo contém uma lista `orders` e, ao lado dela, três ajustes para o pacote inteiro; o primeiro arquivo que define um deles decide.

Uma ordem é um trabalho que as variantes de entidade nomeadas como seus executores fazem para um jogador: elas extraem os blocos que ela nomeia dentro da área, ou trazem os itens dela de outros baús, e levam o que obtêm para o baú dela. Todas as formas de abrir uma funcionam a partir de um cliente vanilla:

- **Uma placa.** Coloque uma placa sobre um baú ou ao lado dele e escreva a palavra `sign` da ordem na primeira linha. A ordem abre naquele baú, e a segunda linha da placa mostra quantos itens ainda faltam. Quebrar a placa, ou o baú, a cancela.
- **Uma ferramenta.** Clique com o botão direito em um trabalhador, sem agachar, com uma ferramenta do tipo `tool` da ordem. O trabalhador pega a ferramenta, a que ele segurava volta para você, e ele executa a ordem no baú mais próximo dentro da área dele.
- **Um baú de estoque.** Renomeie um baú para `stockName` em uma bigorna e coloque-o. Cada item da primeira fileira dele é mantido cheio até uma pilha completa: trabalhadores o trazem de outros baús dentro da área da primeira ordem `haul` do pacote, com os executores, a prioridade e a área dessa ordem.

```json
{
  "stockName": "Stock",
  "hire": "minecraft:emerald",
  "spawnCap": 1,
  "orders": [
    {
      "job": "mine",
      "blocks": "c:ores/iron",
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
      "blocks": "c:crops/wheat",
      "sign": "Farm",
      "tool": "hoe",
      "standing": 64,
      "takers": ["mypack:farmer"]
    },
    {
      "job": "haul",
      "blocks": "minecraft:logs",
      "sign": "Haul",
      "deliver": "chest",
      "takers": ["mypack:porter"]
    }
  ]
}
```

Ajustes ao lado de `orders`:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `stockName` | não | string | `Stock` | O nome que torna um baú colocado um baú de estoque, sem diferenciar maiúsculas. Vazio desliga os baús de estoque |
| `hire` | não | nome de item | nenhum | Contrata um trabalhador livre: clique nele com este item, sem agachar, e ele passa a ser seu e o item é gasto. Um trabalhador livre não pertence a nenhum jogador nem time |
| `spawnCap` | não | int | `1` | Quantos trabalhadores uma ordem pode gerar no baú dela quando nenhum aparece. `0` não gera nenhum |

Chaves da ordem:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `job` | sim | `gather`, `mine`, `farm` ou `haul` | | `gather` e `mine` extraem os blocos que `blocks` nomeia, como o próprio bloco ou como o que ele solta, sempre que um lado esteja aberto ao ar ou a um bloco que não é um cubo completo. `farm` colhe plantações maduras e replanta cada uma com as próprias sementes. `haul` leva os itens que `blocks` nomeia de outros baús e contêineres para o baú da ordem |
| `blocks` | sim, exceto em `farm` | id de tag de item | nenhum | O que a ordem quer, como tag de item, por exemplo `c:ores/iron`, `minecraft:logs` ou `c:crops/wheat`. Em uma ordem `mine` também conta o que um bloco da tag deixa cair, então `c:ores/iron` se enche de ferro bruto. Em uma ordem `farm`, limita a colheita às plantações que o soltam |
| `area` | não | int | `16` | Até onde o trabalho alcança a partir do baú, em blocos ao longo de cada eixo |
| `areaByTier` | não | lista de int | nenhum | O alcance pelo nível de extração da ferramenta do trabalhador: a primeira entrada sem ferramenta ou com nível 0, depois uma entrada por nível, e a última para todo nível além do fim. Substitui `area` |
| `deliver` | não | `chest` ou `self` | `chest` | `chest` leva o que foi coletado para o baú da ordem. `self` guarda no armazenamento do próprio trabalhador, e a ordem conta ao coletar |
| `limit` | não | int | `64` | Quantos itens a ordem quer antes de fechar |
| `standing` | não | int | `0` | Acima de `0` a ordem nunca fecha: mantém essa quantidade dos itens dela no baú e trabalha sempre que houver menos. `limit` então não é lido |
| `workers` | não | int | `1` | O máximo de trabalhadores na ordem ao mesmo tempo |
| `priority` | não | int | `0` | Um trabalhador ocioso pega primeiro a ordem aberta de maior prioridade, depois a mais próxima |
| `takers` | sim | lista | | Quem pode executá-la: um nome de variante, `team:<team>` para qualquer membro de um time do placar, ou `tag:<tag>` para qualquer entidade com essa tag |
| `sign` | não | string | nenhum | A palavra na primeira linha de uma placa que abre a ordem, sem diferenciar maiúsculas |
| `speed` | não | número | `1` | Multiplica a velocidade de extração do trabalhador. `2` extrai duas vezes mais rápido que um jogador com a mesma ferramenta |
| `tool` | não | tipo de ferramenta | nenhum | O tipo de ferramenta, como `pickaxe`, `axe`, `shovel` ou `hoe`, que dá esta ordem a um trabalhador com clique direito. Um trabalhador cuja ferramenta não consegue extrair um bloco volta ao baú atrás de uma desse tipo |

**Como um trabalhador trabalha.** Uma variante que um executor nomeia, ou toda variante quando algum executor é um time ou uma tag, procura trabalho uma vez por segundo enquanto está ociosa. Ela pega a melhor ordem aberta cujo baú esteja dentro da área dela mais 32 blocos, segura a ordem com uma concessão que expira quando para de trabalhar e mantém a concessão ao salvar e recarregar. Extrair leva o mesmo tempo que levaria a um jogador segurando a ferramenta do trabalhador, Eficiência incluída, e Fortuna e Toque Suave contam para o que cai. O que ela extrai vai para os itens de `storage` quando a variante os tem, senão para a mão secundária, nunca para o chão. Quando não consegue carregar mais, ela anda até o baú e põe tudo lá. Cada bloco desgasta a ferramenta, e um trabalhador cuja ferramenta quebra volta ao baú atrás de outra do mesmo tipo, ou descansa um minuto quando não há nenhuma. Ao morrer, um trabalhador solta a ferramenta e o que tem nas mãos, e o armazenamento dele se esvazia uma vez, como o armazenamento sempre faz. `mobGriefing` não o impede, já que um jogador pediu o trabalho.

**Quem trabalha para quem.** Quando o jogador que abre uma ordem está em um time do placar, só os trabalhadores desse time a pegam. Caso contrário a ordem pertence a esse jogador, e um trabalhador pertence ao primeiro jogador que lhe dá um trabalho, por uma ferramenta, por `hire` ou ao pegar uma ordem desse jogador; a partir daí ele só executa ordens desse jogador. Um trabalhador livre contratado ou que recebe uma ferramenta de um jogador em um time entra nesse time.

**Surgimento no baú.** Uma ordem que ficou dez segundos sem trabalhador gera um dos executores variantes dela ao lado do baú, até `spawnCap` para essa ordem. O trabalhador gerado pertence ao time ou ao jogador da ordem.

## Exposições

*criaturas e perigos*

`<namespace>/exposures/*.json`

O caminho do arquivo é o nome do perigo, e sua mensagem de morte vem da chave de lang `death.attack.rdpl.<file name>`. As exposições só carregam enquanto `load` está ligado e `vanillaClients` está desligado.

Um perigo definido pelo pacote: blocos, itens e dimensões nomeados expõem jogadores que estão perto desses blocos, carregam esses itens ou permanecem nessas dimensões, em níveis, cada nível aplicando efeitos e dano periódico. Um perigo também pode ser contraído de mobs e jogadores próximos, ou cair com a chuva ([Contágio e clima](#contágio-e-clima)). Um arquivo define um perigo; vários funcionam lado a lado.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `blocks` | uma das cinco | lista de `block` ou `block=level` | | Blocos que expõem um jogador que está perto deles. Sem nível significa 1 |
| `items` | uma das cinco | lista de `item` ou `item=level` | | Itens que expõem um jogador que os carrega ou veste |
| `dimensions` | uma das cinco | lista de `dim` ou `dim=level` | | Ids de dimensão que expõem qualquer jogador nelas |
| `levels` | sim | lista de níveis | | A escada de gravidade, a primeira entrada é o nível 1. Um jogador recebe o nível mais alto que qualquer fonte alcança |
| `immunity` | não | nome de poção | nenhum | Um efeito cujo portador não é exposto de forma alguma |
| `scanInterval` | não | ticks | `20` | Com que frequência o entorno e o inventário são verificados |
| `range` | não | blocos | `10` | Até onde chega a exposição de um bloco, como uma esfera |
| `sourcesForNextLevel` | não | int | `0` | Esta quantidade de fontes próximas de um nível o empurra um nível adiante. `0` desliga isso |
| `skipsCreative` | não | booleano | `true` | Jogadores no criativo e no espectador são deixados em paz |

### Níveis

*exposições*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `effect` | sim | nome de poção | | O efeito que marca o nível no jogador. Sua presença aciona o dano, então deve ser um que o pacote defina para isso |
| `damage` | não | meios-corações | `0` | Dano causado a cada `damageInterval` ticks enquanto o nível se mantém. Ignora armadura |
| `damageInterval` | não | ticks | `160` | Com que frequência esse dano é aplicado |
| `effects` | não | lista de efeitos | nenhum | Efeitos extras aplicados junto, no mesmo formato que os tipos de poção usam. Sem uma `duration`, seguem a janela de verificação |

Os efeitos do nível duram um pouco além da próxima verificação, então ao se afastar eles se dissipam por conta própria. A morte por dano de exposição lê sua mensagem de `death.attack.rdpl.<file name>`, que os arquivos de lang do pacote fornecem.

### Contágio e clima

*exposições*

Mais duas fontes, escritas no mesmo arquivo. Portadores e carregadores expostos passam o perigo adiante aos receptores ao redor, e a chuva ou a tempestade expõe os jogadores sobre os quais cai.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `carriers` | uma das cinco | lista de `entity` ou `entity=level` | | Mobs, ou `minecraft:player`, que sempre passam o perigo adiante naquele nível. Sem nível significa 1 |
| `contagious` | não | booleano | `false` | Qualquer exposto passa o perigo adiante no nível que possui |
| `catchers` | não | lista de nomes de entidades | `minecraft:player` | Quem pode contraí-lo. Um mob só o contrai de portadores e carregadores, nunca de blocos, itens ou clima |
| `contagionRange` | não | blocos | `4` | Até onde um portador ou carregador alcança, como uma esfera |
| `contagionChance` | não | `0` a `1` | `0.1` | A probabilidade, a cada verificação do portador ou carregador, de que cada receptor ao alcance o contraia |
| `contagionDuration` | não | ticks | `1200` | Por quanto tempo um perigo contraído mantém o nível contraído. Contraí-lo de novo reinicia o tempo |
| `weather` | uma das cinco | lista de `kind` ou `kind=level` | | `rain` expõe um jogador sobre o qual a chuva cai: céu aberto acima, em um bioma onde chove. `thunder` conta durante uma tempestade |
| `weatherDimensions` | não | lista de `dim` | todas as dimensões | Ids de dimensão em que o clima expõe |

Um perigo contraído conta como mais uma fonte na verificação, e o nível mais alto vence, como com qualquer outra, e `immunity` também protege contra ele. Nada se espalha a menos que `contagionRange` e `contagionChance` estejam acima de `0` e que o arquivo nomeie `carriers` ou defina `contagious`, e os mobs só são examinados quando algum arquivo faz isso.

---

# O mundo

## Modelos de mundo

*o mundo*

`<namespace>/worldtemplates/*.json`

O caminho do arquivo é o nome do modelo, que a opção de config `worldTemplate` pode nomear para escolhê-lo diretamente.

Reúne a forma de um mundo em um único arquivo, para que um pacote entregue um mundo inteiro de uma vez, em vez de pedir ao jogador que defina uma dúzia de opções de config.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `name` | não | string | o nome do arquivo | Mostrado no log e nos relatórios |
| `default` | não | nome de bioma ou `void` | `void` | O que preenche um bioma que o bloqueio removeu. `void` deixa o bioma vazio ali. Um bioma que não está registrado é registrado no log e o vazio é usado. `fallback` é a mesma chave com outro nome |
| `roles` | não | objeto de papel para bioma | nenhum | Biomas que preenchem papéis específicos: `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains` e `water`, examinados nessa ordem, qualquer que seja a ordem em que o arquivo os escreve, então um bioma bloqueado que é ao mesmo tempo oceano e nevado assume o papel de oceano. Um papel que nomeia `void` ou um bioma não registrado passa para o próximo. Os papéis se aplicam apenas nas `dimensions` do modelo; em outros lugares um bioma bloqueado vira o vazio |
| `structures` | não | objeto de [nome de estrutura](#listas-de-valores) para booleano | nenhum | Estruturas vanilla ligadas ou desligadas |
| `settings` | não | objeto | nenhum | Valores de config que o modelo define |
| `dimensions` | não | lista de ids de dimensão | todas as dimensões | A quais dimensões se aplica |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | O modelo é ignorado a menos que todos estejam presentes |

`settings` usa os mesmos nomes de chave que a config, então não há tabela de tradução para aprender.

Qual modelo está ativo é decidido pela opção de config `worldTemplate`. Deixada em `auto`, vence o pacote de maior prioridade que entregar um, na mesma ordem que todo o resto segue; quando mais de um pacote entrega um modelo, o log nomeia todos e o que está em vigor, já que os demais não fazem nada, configurações e tudo. Nomear um modelo ali o escolhe diretamente. Cinco são integrados e podem ser nomeados assim: `void`, `vanilla` (oceanos, rios, praias, campos de cogumelos, pântanos e colinas mantidos como os do próprio jogo, planícies em todo o resto), `ocean` (rios e praias mantidos, oceano em todo o resto), `plains` e `desert`. `auto` nunca escolhe um integrado.

**Um bioma pode construir de forma diferente.** Um objeto `biomes` dentro de `settings` guarda configurações de vila próprias para um bioma nomeado, de modo que uma vila do deserto assenta ruas de arenito onde uma de planície assenta concreto, sem que cada uma seja um pacote separado. Nomeie um bioma pelo seu id, `minecraft:desert`, por uma das palavras de tipo que este mod mapeia para tags de bioma (`sandy`, `snowy`, `desert`, `forest`, `jungle`, `mountain`, `ocean`, `swamp`, `hot`, `cold` e as demais), ou por uma tag escrita por extenso, `#minecraft:is_forest`; um id exato é examinado antes dos tipos, de modo que uma regra geral pode ser substituída para um só bioma. Tudo o que não for nomeado dentro de uma seção recorre à configuração simples acima dela.

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

Toda configuração de bloco que uma estrada, uma ponte, uma ferrovia, um metrô, uma estação ou um esgoto aceita responde a isto, e a sintaxe de mistura ponderada funciona dentro de uma seção como fora dela. O bioma é lido à medida que uma peça é construída, e os blocos são tomados de novo onde o terreno muda de bioma, então uma estrada ou ferrovia que sai de um deserto muda de material na própria divisa. Uma linha de log ao carregar o mundo diz quantas seções um pacote entregou e as nomeia, e com debug ligado cada bioma diz qual seção escolheu, ou que não escolheu nenhuma e a que teria correspondido.

## Regras do jogo

*o mundo*

`<namespace>/gamerules/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

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

Cada chave é o id do mundo ao qual as regras pertencem, `minecraft:overworld`, `minecraft:the_nether`, `minecraft:the_end`, o de um pacote ou o que um mod usar; os números do 1.12.2, `0`, `-1` e `1`, ainda são aceitos como os três vanilla. Os valores são strings, como no comando `/gamerule`, então `"false"` e não `false`. Elas são aplicadas a mundos novos. Uma regra que um arquivo omite assume o padrão do próprio jogo naquele mundo, e não o valor que o resto do save usa, e um cliente com o pacote lê as mesmas regras. Um arquivo de dimensão carrega as mesmas regras em um bloco `gameRules`, que se aplica apenas àquele mundo.

## Biomas

*o mundo*

`<namespace>/biomes/*.json`

O caminho do arquivo é o nome de registro do bioma, então `mypack/biomes/ruby_forest.json` registra `mypack:ruby_forest`. `name` é apenas o que o jogador vê, e `biome.mypack.ruby_forest` nos arquivos de lang o diz em todos os idiomas.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa.

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

### O bioma

*biomas*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `name` | não | string | o nome do arquivo | Nome mostrado ao jogador |
| `types` | não | lista de tipos de bioma | deduzido | Escreve o bioma nas tags que essas palavras de tipo representam, como `forest`, `cold`, `wet` ou `nether`, para que outros mods o encontrem. Se omitida, os tipos são deduzidos do bioma como o jogo os deduzia: `forest` ou `jungle` a partir de três árvores ou mais, `plains` caso contrário, `hot`, `cold`, `wet` e `dry` a partir da temperatura e da umidade, `sparse` ou `dense` a partir da contagem de árvores, `snowy` a partir de `snow`, e `sandy`, `mushroom` ou `mesa` a partir de solo de areia, micélio ou terracota |
| `baseBiome` | não | nome de bioma | `minecraft:plains` | Um bioma existente de onde copiar configurações. Um que não seja um bioma que o jogo ou um mod entrega é registrado no log, e as planícies são usadas |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | O arquivo é ignorado a menos que todos estejam presentes |

Um bioma é uma entrada de data pack nesta versão, escrita para você em `worldgen/biome/`, e o terreno sob ele é o das configurações de ruído e não o do bioma, e é por isso que não há `baseHeight` nem `heightVariation`: a forma do terreno vem de onde o clima coloca o bioma, como acontece com os do próprio jogo. Um `id` do 1.12.2 é lido e ignorado.

### Clima

*biomas*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `temperature` | não | float | `0.5` | Abaixo de 0,15 neva, acima de 1,0 é quente como um deserto |
| `rainfall` | não | float, 0 a 1 | `0.5` | Quão úmido é |
| `rain` | não | booleano | `true` | Se há clima de qualquer tipo |
| `snow` | não | booleano | `false` | Se a chuva cai como neve. A neve só cai onde a temperatura é inferior a 0,15, e isto não muda a temperatura, então um bioma mais quente ainda tem chuva |

### Solo e cores

*biomas*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `topBlock` | não | nome de bloco | grama | O bloco da superfície |
| `fillerBlock` | não | nome de bloco | terra | Logo abaixo da superfície |
| `stoneBlock` | não | nome de bloco | pedra | O grosso do solo |
| `waterColor` | não | cor hex | `FFFFFF` | Tonalidade da água |
| `grassColor` | não | cor hex | do clima | Tonalidade da grama, no lugar da cor que a temperatura e a umidade dariam |
| `foliageColor` | não | cor hex | do clima | Tonalidade das folhas, do mesmo modo |
| `snowColor` | não | cor hex | a da dimensão | Tonalidade da neve no chão, acima do `snowColor` da dimensão |

### Decoração e spawns

*biomas*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `decoration` | não | objeto | o do bioma base | Contagens por chunk, alterando o que o bioma base já coloca. Os nomes que ela lê são `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` e `waterlily`, mais os interruptores `falls` (lagos e nascentes), `pumpkins`, `desertwells`, `ice` (espinhos de gelo e manchas de gelo), `fossils` e `rocks` (rochas de floresta), em que qualquer valor acima de zero mantém a taxa do próprio bioma base e zero ou menos remove a característica, e `extratreechance`, uma porcentagem de chance de mais uma árvore, em que `0` também remove a árvore extra que o bioma base sorteia. Uma contagem para um tipo que o bioma base não coloca não adiciona nada; escreva uma entrada de worldgen para isso. Qualquer outro nome é registrado no log e ignorado |
| `spawns` | não | lista de objetos | lista vanilla | Veja abaixo |
| `keepDefaultSpawns` | não | booleano | `false` | Mantém a lista do vanilla junto com a sua |
| `spawnChance` | não | float, abaixo de 1 | `0.1` | Quão provável é que outro rebanho seja colocado quando o terreno é criado pela primeira vez. O jogo continua sorteando enquanto tiver sucesso, então 1 nunca para e enche o mundo até acabar o espaço. Qualquer valor igual ou acima de 0,99 é recusado e 0,99 é usado |
| `spawnRates` | não | objeto de `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` para um multiplicador | nenhum | Com que frequência mobs hostis surgem aqui, no lugar das configurações globais. Veja abaixo |

Uma entrada de spawn aceita `entity` (obrigatória), `type` (`creature`, um entre `monster`, `creature`, `ambient` ou `water`, com os sublinhados de um nome como `water_creature` opcionais), `weight` (`10`), `min` (`1`) e `max` (`min`).

`spawnRates` trata apenas de mobs hostis, e de nada mais. Aceita quatro chaves e nenhuma outra: `surfaceDay` e `surfaceNight` para onde o céu pode ser visto, `undergroundDay` e `undergroundNight` para onde não pode. Cada uma é um multiplicador de com que frequência um mob hostil pode aparecer, `1` é a taxa comum, `0` os impede por completo, abaixo de 1 recusa algumas tentativas, e acima de 1 deixa passar tentativas que o jogo de outro modo teria recusado, então `2` é o dobro. Uma chave omitida significa que o bioma não decide, e a configuração global para aquele momento e lugar é usada. Qualquer outra coisa escrita aqui não é uma chave e é ignorada, então uma taxa com o nome de um tipo de criatura não faz absolutamente nada.

### Onde gera

*biomas*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `placement` | não | objeto | nenhum | Onde ele gera. Veja abaixo |
| `villageType` | não | `oak`, `sandstone`, `acacia` ou `spruce` | nenhum | De que é construída uma vila que fique aqui: a vila de planície, de deserto, de savana ou de taiga. Vazio constrói a de planície, como faria sem a chave |

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `climate` | não | `icy`, `cool`, `medium`, `warm` ou `desert` | nenhum | A qual faixa de clima ele se junta, as mesmas cinco pelas quais os biomas do overworld são distribuídos. Omitida, deixada com `weight` 0 ou nomeando um clima que não está listado aqui, o bioma é registrado mas nunca é colocado, a menos que os `roles` de um modelo, o `biome` de uma dimensão ou uma faixa de altura o peça |
| `weight` | não | int | `10` | Com que frequência é escolhido em relação aos vizinhos naquela faixa |
| `villages` | não | booleano | `false` | Vilas podem gerar |
| `strongholds` | não | booleano | `false` | Fortalezas podem gerar |
| `playerSpawn` | não | booleano | `false` | O spawn do mundo pode ser colocado aqui |

### Faixas de altura

*biomas*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `minHeight` | não | int | nenhum | O menor y em que este bioma assume o controle como um bioma 3D. Definir qualquer uma das alturas transforma o bioma em uma faixa: a coluna mantém seu próprio bioma fora dela, e dentro dela cada célula de 4 por 4 por 4 do mundo informa este |
| `maxHeight` | não | int | nenhum | O maior y dessa faixa |
| `replaces` | não | lista de nomes de biomas | todos os biomas | Restringe a faixa às colunas cujo próprio bioma é nomeado aqui, de modo que uma faixa alpina possa ficar sobre montanhas e nada mais. O bioma próprio da coluna é o da sua superfície. Sem `minHeight` ou `maxHeight`, não faz nada |

### Temperatura por altura

*biomas*

**Temperatura por altura.** Um bioma esfria à medida que sobe, o que põe neve nos topos das montanhas e faz parar a chuva acima de uma linha. Três chaves de `terrain` movem essa curva, o que importa em uma dimensão cujo solo fica muito acima ou abaixo da altura que o jogo presume. Sem definição, a curva do próprio jogo permanece, então um pacote que não mexe nelas não muda nada.

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

| Chave | Valor | Padrão | O que faz |
| ----- | ----- | ------ | --------- |
| `biomeTemperatureCenterY` | int | `80` | A altura a partir da qual a curva é medida. Nela ou abaixo dela, um bioma informa sua própria `temperature` intacta |
| `biomeTemperatureHeightFactor` | float | `-0.00125` | Quanto a temperatura se move por bloco acima dessa altura, os 0,05 em 40 blocos do próprio jogo. Negativo esfria com a altitude, positivo esquenta |
| `biomeTemperatureScaleMaxY` | int | nenhum | A altura em que a curva para, para que um mundo mais alto que o do próprio jogo não continue esfriando até o teto. Sem definição, a curva vai até o topo do mundo |

## Dimensões

*o mundo*

`<namespace>/dimensions/*.json`

O caminho do arquivo é o id da dimensão, então `mypack/dimensions/verdant.json` é `mypack:verdant`, que é o que um portal, um portal de passagem, um arquivo de regras do jogo e `/execute in` nomeiam. Não há id numérico nesta versão, e um `id` ou `suffix` do 1.12.2 é lido e ignorado.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa.

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

### Nível superior

*dimensões*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `gameRules` | não | objeto | nenhum | Regras que se aplicam apenas aqui |
| `portal` | não | objeto | nenhum | Uma moldura que abre esta dimensão. Veja [Abrindo uma dimensão com uma moldura](#abrindo-uma-dimensão-com-uma-moldura) |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | O arquivo é ignorado a menos que todos estejam presentes |

Uma dimensão é uma entrada de data pack nesta versão: o tipo de dimensão e as configurações de ruído são escritos para você sob o namespace do pacote, de modo que um cliente vanilla é informado dela ao entrar e viaja até lá como a qualquer outra. A dimensão mantém sua própria pasta de save dentro do mundo, com o nome do seu id, e é carregada enquanto alguém está nela, ou enquanto um `forceload` mantém um chunk.

### O bloco `terrain`

*dimensões*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `type` | não | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Qual dos geradores do jogo a constrói, com suas configurações de ruído copiadas e alteradas pelas chaves abaixo |
| `minHeight` | não | int, múltiplo de 16 | o do próprio tipo | O piso da dimensão. Mais baixo que o do próprio tipo cria um mundo profundo sob o terreno, veja [O mundo profundo](#o-mundo-profundo) |
| `maxHeight` | não | int, múltiplo de 16 | o do próprio tipo | O bloco acima do seu topo |
| `generatorOptions` | não | objeto, texto ou lista | nenhum | Para `overworld` e os demais, um objeto, ou o texto de um como o 1.12.2 o escrevia, com `seaLevel`, `useLavaOceans`, e `useCaves`, `useRavines`, `useDungeons`, `useLavaLakes`, `useStrongholds`, `useVillages`, `useMineShafts`, `useTemples`, `useMonuments` e `useMansions` definidos como false para deixá-los fora desta dimensão. Para `flat`, as camadas, de baixo para cima, como `"minecraft:bedrock"`, `"59*minecraft:stone"`, `"3*minecraft:dirt"`, `"minecraft:grass_block"`, que também é o solo padrão, ou o texto superflat do 1.12.2, cujo número de bioma define o bioma e cujos nomes `decoration`, `lava_lake` e de estruturas são lidos como o `generatorOptions` do overworld os lê |
| `structures` | não | booleano | `true` | Se as estruturas vanilla geram |

### O bloco `biomes`

*dimensões*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `source` | não | `inherit`, `single` | `inherit` | `inherit` usa o mapa de biomas do próprio overworld, seja qual for o tipo de terreno, então uma dimensão `nether` ou `end` recebe os biomas do overworld em seu próprio solo; `single` usa um só bioma em todo lugar. Uma dimensão `flat` guarda um só bioma de qualquer modo, o de `single` ou o do seu texto superflat |
| `biome` | quando `single` | nome de bioma | `minecraft:plains` | Qual é esse bioma |

### O bloco `sky`

*dimensões*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `hasSkyLight` | não | booleano | `true` | Se a luz do dia chega nela |
| `surfaceWorld` | não | booleano | `true` | Se mapas e bússolas se comportam como no overworld |
| `respawn` | não | booleano | `true` | Se os jogadores renascem aqui |
| `respawnDimension` | não | id de dimensão | nenhum | Onde eles renascem em vez disso |
| `spawning` | não | booleano | `true` | Se mobs surgem. Desligado, impede todo spawn, inclusive de spawners, seja o que for que o grupo `spawning` diga |
| `nether` | não | booleano | `false` | Tratada como o nether para portais e tetos |
| `beds` | não | booleano | `true` | Desligado, as camas explodem |
| `waterVaporizes` | não | booleano | `false` | A água evapora |
| `cloudHeight` | não | int | `128` | Onde ficam as nuvens. Uma configuração `cloudHeight` que nomeie esta dimensão, ou uma sem nome, vence esta |
| `cloudColor` | não | cor hex | nenhum | Tonalidade das nuvens |
| `cloudSpeed` | não | float | `1.0` | Com que velocidade as nuvens derivam. `0` as mantém paradas, um valor negativo as inverte |
| `cloudLayers` | não | lista de objetos | nenhum | Várias camadas de nuvens. Veja [Neblina, luz, nuvens e calor](#neblina-luz-nuvens-e-calor) |
| `groundLevel` | não | int | `63` | Nível do mar, usado para o horizonte, para buscas de spawn e para onde pousa a chegada por um portal de passagem ou uma queda sobre o vazio |
| `movementFactor` | não | float | `1.0` | Razão de distância em relação ao overworld. O nether usa 8 |
| `fogColor` | não | cor hex ou `sample` | nenhum | Tonalidade da neblina ao meio-dia. Escurece à noite como a neblina vanilla. `sample` mistura o céu com o solo ao redor do jogador |
| `showFog` | não | booleano | `false` | Neblina espessa, como no nether |
| `fogDensity` | não | float, 0 a 1 | `0.0` | Quão espessa é a neblina. `0` mantém a distância vanilla, `1` a fecha em 8 blocos |
| `fogGroundWeight` | não | float, 0 a 1 | `0.5` | Com `fogColor: sample`, quanto o solo conta em relação ao céu |
| `skyColor` | não | cor hex | nenhum | Tonalidade do céu ao meio-dia. Escurece à noite e fica cinza na chuva e na tempestade como o céu vanilla |
| `fixedTime` | não | int, ticks | nenhum | Trava a hora do dia |
| `sunriseColors` | não | booleano | `true` | Se o nascer e o pôr do sol são tingidos |
| `ambientLight` | não | float, 0 a 1 | `0.0` | Luz mínima em todo lugar |
| `lightSkyColor` | não | cor hex | nenhum | Tonalidade da luz do dia sobre blocos e mobs |
| `lightBlockColor` | não | cor hex | nenhum | Tonalidade da luz de tochas e de outras luzes de bloco |
| `skyFactor` | não | float, 0 a 1 | `1.0` | Quão brilhante a luz do dia parece. Desenhada apenas no cliente, então o spawn de mobs não muda |
| `starBrightness` | não | float, 0 a 1 | nenhum | Quão brilhantes são as estrelas |
| `sunBrightness` | não | float, 0 a 1 | `1.0` | Quão brilhante o sol é desenhado |
| `moonBrightness` | não | float, 0 a 1 | `1.0` | Quão brilhante a lua é desenhada, e com `bodies` todos os corpos exceto o sol |
| `heat` | não | objeto | nenhum | Uma ondulação de calor sobre a visão. Veja [Neblina, luz, nuvens e calor](#neblina-luz-nuvens-e-calor) |
| `renderSky` | não | booleano | `true` | Desligado, nada desenha o céu, o sol, a lua nem as estrelas, restando a cor da neblina |
| `renderClouds` | não | booleano | `true` | Desligado, nenhuma nuvem é desenhada |
| `renderWeather` | não | booleano | `true` | Desligado, nenhuma chuva ou neve é desenhada |
| `sun` | não | objeto | nenhum | O seu próprio sol. Veja [O renderizador do céu](#o-renderizador-do-céu) |
| `bodies` | não | lista de objetos | nenhum | Planetas e luas pendurados no céu. Veja [O renderizador do céu](#o-renderizador-do-céu) |
| `stars` | não | objeto | nenhum | O seu próprio campo de estrelas. Veja [O renderizador do céu](#o-renderizador-do-céu) |

### O renderizador do céu

*dimensões*

Definir qualquer um entre `sun`, `bodies` ou `stars` troca o céu vanilla pelo próprio do RDPL, que desenha a mesma abóbada, o mesmo brilho do nascer do sol e o mesmo vazio que o vanilla, mas toma o sol, os demais corpos e as estrelas do pacote. Ele é desenhado apenas no cliente, e um servidor dedicado nunca o carrega. `renderSky: false` ainda vence e não desenha nada, e `renderClouds: false` é como se faz um céu sem nuvens.

Sem `bodies`, a lua vanilla e suas fases permanecem. Com `bodies`, a lista é tudo além do sol, então uma lista vazia é um céu sem lua.

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `sun.texture` | não | caminho de textura | o sol vanilla | A imagem do sol |
| `sun.size` | não | float | `30` | Metade da largura do sol a uma distância de 100. `0` o esconde |
| `bodies[].texture` | sim | caminho de textura | | A imagem do corpo |
| `bodies[].size` | não | float | `20` | Metade da sua largura a uma distância de 100. A lua vanilla é `20` |
| `bodies[].angle` | não | float, graus | `180` | Quanto adiante, no caminho do sol, ele fica atrás do sol. `180` é onde está a lua vanilla. Com `followsTime` desligado, é medido a partir do ponto diretamente acima, então `0` é o zênite e `90` o horizonte |
| `bodies[].tilt` | não | float, graus | `0` | Quanto ele fica fora do caminho do sol, ao norte ou ao sul |
| `bodies[].followsTime` | não | booleano | `true` | Desligado, ele fica parado no céu em vez de girar junto com o sol |
| `stars.count` | não | int | `1500` | Quantas estrelas |
| `stars.size` | não | float | `0.15` | A menor estrela; a maior é dois terços maior ainda |

### Neblina, luz, nuvens e calor

*dimensões*

Estas chaves ficam no bloco `sky` ao lado das mais antigas, que continuam funcionando como antes. Todas são desenhadas apenas no cliente, então um servidor dedicado as ignora e nenhum save muda.

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

`fogColor: "sample"` lê os blocos do topo em um quadrado de 33 por 33 blocos ao redor do jogador uma vez por segundo, ilumina suas cores de mapa conforme a hora do dia e as mistura com a cor do céu. A neblina se ajusta suavemente a cada nova amostra. `fogDensity` funciona com uma cor de neblina amostrada, uma definida ou nenhuma. Debaixo d'água, na lava e sob cegueira, a neblina vanilla permanece.

`lightSkyColor` e `lightBlockColor` tingem o mapa de luz, então todo bloco iluminado e todo mob assume a tonalidade. `skyFactor` escala quão brilhante a luz do dia parece, enquanto o nível de luz que o servidor conta para spawn e plantações permanece o mesmo.

Sem `cloudLayers`, `cloudSpeed` muda a velocidade da única camada vanilla em `cloudHeight`. Com `cloudLayers`, cada entrada é uma camada própria, e `cloudHeight`, `cloudSpeed` e `cloudColor` preenchem o que uma entrada omite. `renderClouds: false` ainda não desenha nenhuma.

`sunBrightness` e `moonBrightness` esmaecem o sol e a lua além do esmaecimento vanilla da chuva, no céu vanilla e no seu próprio, do [renderizador do céu](#o-renderizador-do-céu).

`heat` estende uma ondulação de calor sobre a visão enquanto o jogador está em um bioma pelo menos tão quente quanto `minTemperature`. Um deserto é 2,0 e as planícies são 0,8. A ondulação surge e some gradualmente em alguns segundos e fica desligada debaixo d'água. Ela precisa de suporte a shaders da placa de vídeo e fica desligada enquanto outro shader de tela inteira, como a visão de espectador, está ativo.

`mode` define onde a ondulação aparece. `screen` distorce uma faixa fixa na parte inferior da tela, para onde quer que o jogador olhe. `world` acompanha o terreno: o que está a menos de `startDistance` blocos continua nítido, a ondulação vai aumentando em direção à borda distante da distância de renderização, onde a névoa se fecha, e o céu nunca é afetado, seja o jogador olhando para baixo, para a frente ou para cima.

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `cloudLayers[].height` | não | float | `cloudHeight` | Onde a camada fica |
| `cloudLayers[].speed` | não | float | `cloudSpeed` | Com que velocidade ela deriva. `0` a mantém parada, um valor negativo a inverte |
| `cloudLayers[].color` | não | cor hex | `cloudColor` | Sua tonalidade |
| `heat.strength` | não | float, 0 a 1 | `0.1` | Quão forte é a ondulação |
| `heat.minTemperature` | não | float | `1.5` | A temperatura de bioma mais baixa que ondula |
| `heat.dayOnly` | não | booleano | `true` | Ligado, a ondulação esmaece com a luz do dia e some à noite |
| `heat.mode` | não | string | `screen` | Onde a ondulação aparece, `screen` ou `world` |
| `heat.startDistance` | não | float | `32` | No modo `world`, a quantos blocos de distância a ondulação começa |

### Neve, fluidos, estrelas e raios

*dimensões*

Estas chaves também ficam no bloco `sky` e também são desenhadas apenas no cliente.

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

`snowColor` tinge as camadas de neve e os blocos de neve no chão. O `snowColor` próprio de um bioma tem prioridade sobre o da dimensão, e as cores se misturam nas bordas entre biomas, como acontece com a grama.

`waterFogColor` e `lavaFogColor` substituem a cor da neblina que a câmera vê debaixo d'água ou dentro da lava. A noite, a profundidade e a visão noturna continuam escurecendo ou clareando essa cor, como fazem com a cor vanilla.

Nesta versão, `waterFogColor` substitui em toda a dimensão o `water_fog_color` que cada bioma traz nos seus `effects`. Sem a chave, o `water_fog_color` de um JSON de bioma vanilla funciona normalmente.

`starColor` tinge as estrelas, no céu vanilla e no seu próprio, do [renderizador do céu](#o-renderizador-do-céu). `starTwinkle` faz as estrelas cintilarem: elas se dividem em oito grupos que escurecem e clareiam cada um no seu ritmo, e o valor diz o quanto escurecem; com `1`, um grupo some por completo no ponto mais baixo.

`lightningColor` tinge os raios.

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `snowColor` | não | cor hex | branco | Tonalidade das camadas e dos blocos de neve |
| `waterFogColor` | não | cor hex | a do bioma | Cor da neblina debaixo d'água |
| `lavaFogColor` | não | cor hex | `991A00` | Cor da neblina na lava |
| `starColor` | não | cor hex | branco | Tonalidade das estrelas |
| `starTwinkle` | não | float, 0 a 1 | `0.0` | O quanto as estrelas escurecem ao cintilar. `0` as mantém firmes |
| `lightningColor` | não | cor hex | `737380` | Tonalidade dos raios |

### Skybox, aurora e arco-íris

*dimensions*

Essas chaves também ficam no bloco `sky`, e também são desenhadas só no cliente.

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

`skybox` pinta suas próprias imagens no céu, atrás do brilho do amanhecer, do sol, da lua e das estrelas. Informe as seis faces de um cubo como caminhos de textura completos, dispostas como o cubo aberto: `up` encosta na borda de cima de `north`, `down` na borda de baixo, `west` fica à esquerda e `east` à direita, com `south` depois de `east`. Ou informe só `panorama`, uma imagem 2:1 que envolve o céu inteiro: a borda esquerda aponta para o norte e ela segue no sentido horário pelo leste, sul e oeste; a linha de cima fica bem no alto e a de baixo bem embaixo. Uma skybox sem uma das faces e sem panorama é deixada de fora, com um erro no log.

`aurora` pendura cortinas luminosas baixas sobre o céu do norte durante a noite. Elas ondulam devagar, surgem quando o sol se põe e somem de dia e na chuva. `color` é a cor na base delas e `topColor` aquela em que se apagam no alto.

`rainbow` mostra um arco-íris do lado oposto ao sol quando a chuva para durante o dia. Ele se apaga nos dois minutos seguintes ao fim da chuva, e uma nova pancada o desfaz.

Nenhuma chave vanilla dá a uma dimensão suas próprias imagens de céu, uma aurora ou um arco-íris, então essas chaves funcionam igual em todas as versões.

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `skybox.<face>` | não | caminho de textura | nenhum | Uma face do cubo: `up`, `down`, `north`, `east`, `south` ou `west`. As seis são necessárias |
| `skybox.panorama` | não | caminho de textura | nenhum | Uma imagem que envolve o céu inteiro, no lugar das faces |
| `aurora.color` | não | cor hex | `40FF90` | Cor na base das cortinas |
| `aurora.topColor` | não | cor hex | `8040FF` | Cor no alto, onde as cortinas se apagam |
| `rainbow` | não | booleano | `false` | Mostrar um arco-íris depois da chuva |

### O bloco `physics`

*dimensões*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| ----- | ----------- | ----- | ------ | --------- |
| `gravity` | não | float, acima de 0 | `1.0` | Aceleração de queda aqui, como multiplicador do vanilla. `0.17` é como a Lua |
| `fallDamage` | não | float, acima de 0 | `1.0` | Dano de queda aqui, como multiplicador |
| `arrowGravity` | não | float, acima de 0 | segue `gravity` | Com que rapidez as flechas caem aqui, como multiplicador |

São os mesmos multiplicadores que as chaves de modelo de mundo `worldGravity` e `worldFallDamage`, definidos na dimensão. Uma linha `dimension=value` do modelo de mundo para esta dimensão ainda vence; um valor simples do modelo de mundo cobre apenas as dimensões que não definem nada por conta própria.

### O bloco `time`

*dimensões*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `dayLength` | não | int, ticks | `24000` | Quanto dura um dia e uma noite aqui. A fase da lua ainda completa um ciclo a cada 24000 ticks |

### O bloco `weather`

*dimensões*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `precipitation` | não | booleano | `true` | Desligado, nunca chove, neva nem há tempestade aqui |
| `lightning` | não | booleano | `true` | Desligado, chuva e tempestades vêm sem raios |
| `snow` | não | booleano | `true` | Desligado, a neve nunca se acumula |
| `freeze` | não | booleano | `true` | Desligado, a água nunca congela |
| `cycle.rainTicks` | não | int ou `[min, max]` | `[1000, 4600]` | Quanto dura um temporal |
| `cycle.clearTicks` | não | int ou `[min, max]` | `[1000, 3000]` | Quanto dura o período seco entre temporais |
| `cycle.maxStrength` | não | float, acima de 0 até 1 | `0.6` | O máximo de intensidade que um temporal atinge. Cada temporal varia entre um quarto disso e o valor inteiro |
| `cycle.thunderTicks` | não | int ou `[min, max]` | nenhum | Quanto dura uma tempestade com trovões. Sem ele, o ciclo nunca tem tempestade |
| `cycle.calmTicks` | não | int ou `[min, max]` | `[12000, 180000]` | Quanto dura a calmaria entre tempestades |
| `cycle.thunderStrength` | não | float, acima de 0 até 1 | `1` | Quão escura fica uma tempestade. Raios caem apenas acima de `0.9` |
| `rain.particle` | não | id de partícula | `minecraft:rain` | O que respinga onde a chuva cai |
| `rain.sound` | não | nome de som | `minecraft:weather.rain` | O som da chuva |
| `rain.volume` | não | float | `0.2` | Seu volume, reduzido pela metade quando a chuva cai sobre você |
| `rain.interval` | não | int | `3` | Com que raridade o som toca; mais alto é mais esparso, `0` toca a cada oportunidade |
| `rain.color` | não | cor hexadecimal | `#FFFFFF` | Tom da chuva que cai |
| `rain.snowColor` | não | cor hexadecimal | `#FFFFFF` | Tom da neve que cai |
| `rain.angle` | não | float, 0 a 180 | `0` | Graus a partir da vertical para baixo: `90` sopra de lado, `180` sobe em linha reta. É desenhada inclinada em no máximo 75 graus |
| `rain.heading` | não | float, graus | `0` | Para onde sopra: `0` sul, `90` oeste, `180` norte, `270` leste |
| `rain.splashUpward` | não | booleano | `false` | Ligado, a chuva que sobe (`angle` acima de `90`) continua respingando no chão e fazendo barulho |
| `wind.gust` | não | float, 0 a 90 | `15` | Graus que uma rajada soma a `angle` no auge, sem nunca passar da horizontal |
| `wind.every` | não | int ou `[min, max]` | `[200, 600]` | Ticks de uma rajada até a seguinte |
| `wind.swing` | não | float, 0 a 180 | `30` | Graus que uma rajada desvia `heading` para um dos lados |

Um bloco `wind` deixa a chuva com rajadas. De vez em quando, uma rajada a inclina até `gust` graus a mais e desvia sua direção até `swing` graus para um lado; ela cresce e se desfaz em até quatro segundos, e as rajadas vêm a cada `every` ticks. A chuva e a neve se inclinam junto, e as partículas de ambiente da dimensão derivam para onde a chuva se inclina, com ou sem rajadas. Um bloco `wind` sem bloco `rain` dá à chuva os valores padrão.

As outras dimensões compartilham a chuva do overworld. Um `cycle` dá a esta dimensão um clima próprio: os temporais vêm e vão nos tempos acima, não importa o que o overworld esteja fazendo. Com `thunderTicks` ela também tem tempestades, em tempos próprios; uma tempestade que encontra um temporal leva o temporal à força máxima, escurece o céu e, com `lightning` ligado, traz raios. O `weatherCeiling` em um [modelo de mundo](#modelos-de-mundo) ainda limita a altura até onde a chuva chega.

Um bloco `rain` muda a aparência e o som da chuva e da neve aqui, com ou sem `cycle`; sem ele, têm a aparência e o som do vanilla.

### O bloco `ambience`

*dimensões*

| Chave            | Obrigatório | Valor               | Padrão           | O que faz                                                                                                                                                                                                                |
| ---------------- | ----------- | ------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `music`          | não         | nome de som         | nenhum           | Música tocada aqui no lugar das faixas de sempre, no criativo também. Ao chegar, a faixa atual é cortada                                                                                                                 |
| `musicDelay`     | não         | int ou `[min, max]` | `[12000, 24000]` | Ticks de silêncio entre duas faixas                                                                                                                                                                                      |
| `loopSound`      | não         | nome de som         | nenhum           | Um som que toca em loop enquanto você está aqui, subindo ao chegar e sumindo ao sair                                                                                                                                     |
| `ambientSound`   | não         | nome de som         | nenhum           | Um som tocado de vez em quando, como o som adicional (additions) de um bioma                                                                                                                                             |
| `soundChance`    | não         | 0.0 a 1.0           | `0.0111`         | A chance por tick de `ambientSound` tocar                                                                                                                                                                                |
| `particle`       | não         | id de partícula     | nenhum           | Uma partícula flutuando no ar ao seu redor, como `minecraft:ash`, `minecraft:white_ash`, `minecraft:crimson_spore` ou `minecraft:dust`                                                                                   |
| `particleChance` | não         | 0.0 a 1.0           | `0.00625`        | Sua densidade, contada como nos biomas modernos: a cada tick cerca de 667 pontos num raio de 16 blocos e outros 667 num raio de 32 são tentados, e cada um que não é um bloco inteiro mostra a partícula com esta chance |
| `particleColor`  | não         | cor hexadecimal     | nenhum           | A cor de uma partícula que aceita uma: `minecraft:dust` e `minecraft:entity_effect`                                                                                                                                      |

Um bloco `ambience` dá à dimensão música, sons e partículas flutuantes próprios. As chaves alimentam os próprios efeitos de bioma do jogo (música, loop ambiente, som adicional e partícula ambiente), então soam e aparecem como os de um bioma, e a opção “Partículas” as reduz do mesmo jeito. Uma chave definida aqui vale sobre todos os biomas da dimensão; se faltar, cada bioma mantém o seu, então um JSON de bioma vanilla com seus efeitos também funciona.

## Portais e passagens

*o mundo*

`<namespace>/blocks/*.json`

Um portal é uma definição de bloco comum, então a mesma regra de caminho vale e cada variante é um bloco de portal.

Um bloco `portal` carrega uma seção `portal`:

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `dimension` | sim | id de dimensão | | Para onde ele leva você |
| `returnDimension` | não | id de dimensão | `minecraft:overworld` | Para onde ele leva você de volta |
| `gate` | não | nome de passagem | nenhum | Uma passagem que precisa estar aberta para atravessar |
| `cooldown` | não | int, ticks | `60` | Antes que o mesmo jogador possa usá-lo de novo |
| `platform` | não | booleano | `true` | Constrói uma plataforma de pouso na chegada |
| `platformBlock` | não | nome de bloco | a própria moldura do portal | De que é feita essa plataforma |
| `sound` | não | nome de som | nenhum | Tocado ao atravessar. Veja [nomes de sons](#listas-de-valores) |
| `owned` | não | booleano | `true` | Apenas quem o construiu, e aqueles que essa pessoa permitir, pode usá-lo. Um portal com dono também é imune a explosões |
| `walkIn` | não | booleano | `false` | Entrar andando no bloco viaja, como um portal do Nether. Desligado, ele é usado com a mão |

### Molduras de portais

*portais e passagens*

`<namespace>/portalframes/*.json`

O caminho do arquivo é o nome de registro da moldura, que uma dimensão então cita em `frames`.

Uma moldura é um desenho do que o jogador precisa construir, e nada mais: diz quais blocos formam a borda e onde fica o vazio, e não diz nada sobre para onde o portal leva. Isso é proposital, porque uma dimensão reivindica uma moldura em vez de ser dona dela, e duas dimensões podem reivindicar a mesma.

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `name` | não | string | o nome do arquivo | O nome usado no log |
| `axis` | não | `vertical`, `horizontal` ou `both` | `vertical` | Se fica de pé como um portal do Nether, deitada como um portal do End, ou pode ser de qualquer um dos dois jeitos |
| `legend` | sim | objeto de um caractere para um bloco | nenhum | Os blocos que as linhas podem usar. Um nome de bloco com estados é lido da mesma forma que em qualquer outro lugar |
| `rows` | sim | lista de strings | nenhum | O desenho, com a linha de cima primeiro |
| `maxWidth` | não | int | `21` | O vazio mais largo até onde um `*` pode se esticar |
| `maxHeight` | não | int | `21` | O vazio mais alto até onde um `*` pode se esticar |

Três caracteres não são blocos. `.` é o vazio em que o portal fica, e uma moldura sem ele é recusada. Um espaço é uma célula com a qual a moldura não se importa, então um contorno em formato de L é desenhado deixando os cantos em branco. `*` repete: uma linha que seja apenas `*` repete a linha acima quantas vezes o jogador construir, e um `*` dentro de uma linha repete o caractere anterior da mesma forma. Ele pode se repetir nenhuma vez, então o desenho lido com todos os `*` riscados é a menor coisa que vai acender, e os máximos acima são a maior. Um desenho sem nenhum `*` é exato, e o jogador deve construir isso e nada mais.

Uma moldura vertical é encontrada em qualquer um dos eixos horizontais e em qualquer sentido, então não importa para onde o construtor estava virado. Uma horizontal é encontrada nas quatro rotações.

**O tamanho máximo é decisão do pacote.** `maxWidth` e `maxHeight` são o maior vazio até onde um `*` vai se esticar, e qualquer coisa menor, até o mínimo, é aceita, então um pacote decide se sua passagem chega no máximo ao 21 do vanilla ou a 4. O mínimo é um jogador: uma moldura de pé é recusada a menos que seu vazio possa ter pelo menos 1 de largura e 2 de altura, uma deitada pelo menos 1 por 1, e um desenho que nunca pode chegar a isso é recusado no carregamento com uma linha no log, em vez de ser uma moldura que ninguém consegue atravessar.

**Uma moldura custa mais para ser procurada quanto mais ela pode se esticar.** Um `*` de linha e um `*` de coluna juntos significam que toda combinação até os dois máximos é tentada, então uma moldura que se estica nos dois sentidos até 21 são 441 desenhos. A busca desiste em vez de travar, e avisa no log, o que é o sinal para diminuir um máximo ou abandonar um dos esticamentos.

**Nada impede que uma moldura seja de obsidiana acesa com isqueiro, mas ela tem precedência.** Uma moldura é procurada antes que o item faça seu próprio trabalho, então tal moldura abre a dimensão do pacote onde um portal do Nether estaria. Escolha outro bloco ou outro acendedor para deixar o portal do vanilla em paz.

### Abrindo uma dimensão com uma moldura

*portais e passagens*

`<namespace>/dimensions/*.json`

Uma dimensão se abre por uma moldura ao carregar uma seção `portal`. A moldura e o que a acende, juntos, escolhem a dimensão, então um formato de moldura pode levar a vários lugares dependendo do que a acendeu.

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `frames` | sim | lista de nomes de molduras | nenhum | As molduras que abrem esta dimensão |
| `ignitedBy` | não | nome de item | `minecraft:flint_and_steel` | O que um jogador segura para acender uma |
| `color` | não | cor hexadecimal | branco | A cor em que o portal é desenhado |
| `return` | não | `built`, `player` ou `none` | `built` | Se um caminho de volta é fornecido, construído pelo jogador, ou nenhum |
| `gate` | não | nome de passagem | nenhum | Uma passagem que precisa estar aberta para atravessar |
| `cooldown` | não | int, ticks | `60` | Antes que o mesmo jogador possa atravessar de novo |
| `platform` | não | booleano | `true` | Constrói uma plataforma de pouso na chegada |
| `platformBlock` | não | nome de bloco | pedra | De que é feita essa plataforma |
| `sound` | não | nome de som | nenhum | Tocado ao atravessar. Veja [nomes de sons](#listas-de-valores) |
| `owned` | não | booleano | `false` | Apenas quem o acendeu, e aqueles que essa pessoa permitir, pode usá-lo |

O bloco que fica no vazio não é escrito pelo pacote. Uma dimensão com uma seção `portal` recebe um próprio, desenhado na textura de portal do próprio jogo sob `color`, atravessado andando em vez de usado com a mão, e inquebrável. A cor multiplica a textura, como um `tintindex` faz, então `#C77DFF` mantém o violeta do Nether e `#4CFFB0` o torna venenoso. Para um portal que não use a textura do vanilla, escreva um bloco `portal` comum seu, com sua própria textura, desenhada como um [mapa de pixels](#texturas-escritas-como-mapas-de-pixels) se quiser, onde `tint` pode fazer a transição entre duas cores.

`return` decide o que acontece do outro lado. `built` ergue a mesma moldura, no tamanho que o jogador construiu, e a acende, que é o jeito como o vanilla se comporta. `player` não constrói nada, mas deixa a mesma moldura ser acesa lá, então o caminho de casa precisa ser encontrado e feito. `none` se recusa a acender a moldura naquela dimensão, e a viagem é só de ida.

**Uma moldura, várias dimensões.** O par formado por uma moldura e o item que a acende é o que escolhe a dimensão, então o mesmo `standing_gate` aceso com isqueiro e aceso com um acendedor do próprio pacote abre dois lugares diferentes, cada um com sua cor. Duas dimensões reivindicando a mesma moldura *e* o mesmo item é um erro do pacote: a segunda é recusada e avisa no log, em vez de uma delas vencer silenciosamente.

Quebrar qualquer bloco da moldura apaga o portal, como no vanilla.

### Portais

*portais e passagens*

`<namespace>/gates/*.json`

O caminho do arquivo é o nome de registro da passagem, que um portal então cita em `gate`.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as de que precisa.

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `dimension` | sim | id de dimensão | | A dimensão que ela protege |
| `name` | não | string | o nome do arquivo | Mostrado ao jogador |
| `scope` | não | `player`, `global` | `player` | Um jogador por vez, ou o mundo inteiro de uma vez |
| `open` | não | booleano | `false` | Se ela começa aberta |
| `unlock` | não | objeto | | O que a abre. Veja abaixo |
| `unlockedMessage` | não | string | `%dim% is now open` | Mostrada quando ela abre |
| `blockedMessage` | não | string | `You need %item% to enter %dim%` | Mostrada quando ela recusa |
| `safeReturn` | não | booleano | `false` | Um jogador mandado de volta é colocado em um lugar seguro no mundo que tentou deixar: ao lado de sua cama ou de sua âncora de renascimento carregada ali, quando ainda existir, senão no spawn daquele mundo |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | A passagem é ignorada a menos que todos estejam presentes |
| `portalBlocks` | não | lista de nomes de blocos | todos os portais | Limita a passagem a esses blocos de portal, de modo que uma dimensão possa ter uma porta protegida e uma aberta |

`unlock` aceita `hold` (um item que precisa ser segurado), `consume` com `consumeCount` (`1`), `craft` (um item que precisa ter sido fabricado), `advancement` e `killed` (um nome de entidade; a passagem abre para quem matar uma, então um chefe pode guardar a chave de um mundo) com `killedCount` (`1`) quando uma não basta, contados por jogador ou para o mundo inteiro conforme o escopo indicar. Adicionar `killedDrops` (um nome de item) faz as mortes contadas largarem esse item aos pés de quem matou em vez de abrir a passagem, e recomeça a contagem, de modo que uma chave possa ser conquistada de novo e entregue a alguém que nunca lutou por ela; use `hold` ou `consume` do mesmo item para torná-lo a chave. `%item%`, `%mob%` e `%dim%` são preenchidos para você. Uma chave que um mob solta não precisa de nada especial aqui: dê o drop ao mob e use `hold` ou `consume`.

As passagens também protegem as dimensões do próprio jogo: uma passagem cujo `dimension` seja `minecraft:the_nether` fica na frente de todo portal do Nether.

## O mundo profundo

*o mundo*

O overworld pode ser mais alto ou mais profundo do que o jogo o faz, e o espaço que se abre sob o terreno é preenchido com uma geração própria. Quatro chaves de `terrain` fazem isso, no bloco `settings` de um modelo de mundo como as demais; uma dimensão do pacote faz o mesmo com `minHeight` e `maxHeight` em seu próprio `terrain`.

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

| Chave | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| `worldMinHeight` | int, múltiplo de 16, até -2032 | `-64` | O bloco mais baixo do overworld. O fundo do próprio jogo é -64; mais baixo cria um mundo profundo sob o terreno do vanilla, de pedra maciça até que a camada de worldgen a escave ou `noiseCaves` leve as cavernas do jogo para baixo. Só é aplicado pela predefinição gerada, então um mundo criado antes do pacote mantém sua altura |
| `worldMaxHeight` | int, múltiplo de 16, até 2032 e no máximo 4064 acima do fundo | `320` | O bloco acima do topo do overworld. O topo do próprio jogo é 320; mais alto deixa céu aberto acima do terreno do vanilla |
| `deepStone` | nome de bloco | nenhum | O bloco de que é feito o mundo abaixo do terreno do vanilla quando o fundo vai abaixo de -64, como a ardósia abissal própria de um pacote. Ele se mistura à ardósia abissal ao longo das oito camadas abaixo de -64, como a ardósia abissal se mistura à pedra. Vazio mantém a pedra |
| `noiseCaves` | `off`, `deep` ou `world` | `off` | Onde as cavernas, túneis, noodles e aquíferos do jogo continuam quando o fundo vai abaixo de -64: `off` mantém o mundo sob o terreno do vanilla como pedra profunda maciça para a camada de worldgen escavar, `deep` os leva até o fundo com os lagos de lava movidos para as dez camadas inferiores, `world` significa o mesmo nesta versão porque o terreno do vanilla já os tem |

O mundo profundo é onde as entradas de worldgen, as regiões de cavernas e os grupos de dureza do próprio pacote fazem seu trabalho: `minHeight` e `maxHeight` em uma entrada alcançam tão fundo quanto o fundo do mundo vai. As features colocadas do próprio jogo ficam no terreno do vanilla: uma cuja altura conta a partir do fundo do mundo, entre elas os diamantes e o redstone inferior do jogo, ainda conta a partir de -64, e uma colocação que cairia abaixo de -64 é deixada de fora, como em um mundo cujo fundo é -64. As chaves de céu do 1.12.2, `deepRavines`, `oreVeins`, `terrainOffset` e o próprio mundo rubic não têm equivalente aqui, já que a geração própria deste motor já alcança do fundo ao teto.

## Regiões de cavernas

*o mundo*

`<namespace>/caveregions/*.json`

O caminho do arquivo é o nome da região, que uma entrada de worldgen então cita em `caveRegions`. Um nome simples ali assume o namespace da própria entrada.

Pinta regiões nomeadas sobre o subterrâneo, a contraparte em pacote dos biomas de caverna do jogo. O subterrâneo é dividido em células arredondadas, de `caveRegionCells` blocos de largura e `caveRegionCellsY` de altura, ambas chaves de `terrain`, e cada célula sorteia uma região, ou nenhuma, por peso. Tudo o que uma região faz vem de forma determinística da seed, então os chunks concordam entre si sem nunca escrever além de uma borda.

### Arquivos de região

*regiões de cavernas*

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as de que precisa.

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

| Chave | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| `weight` | int | `1` | Fatia das células que esta região vence. `0` a desliga |
| `minHeight` | int | o fundo do mundo | Base da faixa em que a região existe |
| `maxHeight` | int | `48` | Topo dessa faixa. Uma célula cujo centro fica fora da faixa nunca escolhe a região |
| `waterLevel` | int | nenhum | Fixa o lençol freático dentro da região nesta altura, no lugar dos aquíferos dali. Ele fica abaixo do nível do mar e pelo menos dois blocos acima da lava profunda |
| `dimensions` | lista de ids de dimensões | todas | Em quais dimensões a região aparece, incluindo as do próprio pacote. Uma região com `biome` só o mostra onde os biomas são colocados por clima: o overworld, o Nether e uma dimensão do pacote que herda os biomas do overworld |
| `floorCover` | bloco | nenhum | Substitui o bloco do topo dos chãos de caverna dentro da região |
| `floorChance` | 0.0 a 1.0 | `1.0` | Quanto do chão é coberto |
| `ceilingCover` | bloco | nenhum | Substitui os blocos do teto da caverna dentro da região |
| `ceilingChance` | 0.0 a 1.0 | `1.0` | Quanto do teto |
| `coverReplace` | lista de blocos | os blocos base do jogo de pedra, minério, pedregulho, arenito, terracota, pedra do End e obsidiana | O que as coberturas podem substituir. Um bloco listado corresponde apenas a si mesmo: `minecraft:stone` não cobre também andesito, ardósia abissal ou tufo, então liste toda pedra que o chão pode ter |
| `spawns` | lista | nenhum | Mobs que surgem dentro da região, as mesmas entradas que o `spawns` de um bioma aceita: `entity`, `type` (monster, creature, ambient ou water), `weight` (`8`), `min` (`1`) e `max` (`4`) para o tamanho do grupo. Um ponto que enxerga o céu é deixado para o bioma, como as coberturas |
| `keepDefaultSpawns` | booleano | `false` | Mantém a lista de spawns do próprio bioma junto com a da região. Desligado, a lista da região a substitui por completo dentro da região |
| `structures` | lista | nenhum | Uma estrutura colocada uma vez por célula da região, no coração da célula, encaixada em um chão de caverna, como o jogo dá a um bioma de caverna seu marco. As entradas são modelos `namespace:name`, ou `{ "structure": "...", "weight": 3 }` para escolher entre vários |
| `structureChance` | 0.0 a 1.0 | `1.0` | A chance de cada célula da região realmente receber sua estrutura |
| `structureLoot` | `namespace:path` | nenhum | A tabela de loot de que todo baú dentro de uma estrutura colocada é preenchido na primeira vez em que é aberto |
| `biome` | nome de bioma | nenhum | O bioma que a região informa dentro de seu volume, escrito como um bioma 3D. Dá à região sua própria folhagem, cores de grama e água, música e sons ambientes, e deixa o peso de spawn do vanilla lê-lo. A superfície acima não é tocada, já que apenas as células que a região ocupa são escritas. Se omitido, a região mantém o bioma que a cerca e ainda coloca suas coberturas, estruturas e spawns |
| `requires` | lista de ids de mods ou namespaces de pacotes | nenhum | A região é ignorada a menos que todos estejam presentes |
| `ambientSound` | nome de som | nenhum | Um som tocado de vez em quando para um jogador dentro da região, como os biomas do jogo adicionam seus próprios sons de caverna. Enviado pelo servidor apenas àquele jogador |
| `soundChance` | 0.0 a 1.0 | `0.0111` | A chance a cada tick de `ambientSound` tocar |
| `particle` | nome de partícula | nenhum | Uma partícula mostrada ao redor de um jogador dentro da região, uma das partículas do jogo que não aceitam configurações próprias, como `minecraft:dripping_water`, `minecraft:happy_villager` ou `minecraft:underwater`. Um nome do 1.12.2 como `dripWater` é convertido junto com seu pacote. Apenas o ar dentro da região a mostra |
| `particleChance` | 0.0 a 1.0 | `0.00625` | A densidade de partículas própria dos biomas: a cada tick cerca de 667 pontos num raio de 16 blocos são testados, e cada um mostra a partícula com esta chance |

### Células

*regiões de cavernas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `caveRegionCells` | int, blocos | `128` | Quão larga é uma célula de região |
| `caveRegionCellsY` | int, blocos | `64` | Quão alta é uma célula de região |
| `caveRegionPlainWeight` | int | `4` | O peso do subterrâneo simples, sem região, no sorteio de cada célula. Mais alto deixa mais subterrâneo sem região alguma: com uma única região de peso 1, cerca de um quinto das células a recebe |

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

Quanto do subterrâneo permanece simples é a chave de `terrain` `caveRegionPlainWeight`, padrão `4`: com uma única região de peso 1, cerca de um quinto das células recebe a região. As coberturas se aplicam sob um teto, então uma região que alcança acima do solo nunca aparece na superfície. As coberturas funcionam em toda caverna, não importa qual gerador a escavou.

### Características em uma região

*regiões de cavernas*

As características se ligam por duas chaves em [entradas de worldgen](#entradas-de-worldgen) comuns. `caveRegions` lista as regiões em que uma entrada pode gerar, verificadas na posição colocada, então cogumelos, cristais ou qualquer outra coisa aparecem apenas dentro de sua região. `snap` primeiro move cada tentativa verticalmente até a superfície de caverna mais próxima: `floor` para coisas que ficam de pé, `ceiling` para coisas que pendem. Uma região do tipo pedra pontuda não precisa de formas novas:

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

O `replace` de `minecraft:air` importa: aquilo sobre o que uma forma colocada escreve é verificado contra `replace`, cujo padrão é pedra, então qualquer coisa construída em espaço aberto de caverna precisa que o ar esteja listado. A mesma entrada com `"snap": "floor"` e sem `hanging` faz crescer as estalagmites correspondentes. O filtro de região funciona com toda forma colocada; `belt` e `field` colocam por regras próprias e o ignoram.

---

# Gerando o mundo

## Entradas de worldgen

*gerando o mundo*

`<namespace>/worldgen/*.json`

O caminho do arquivo nomeia a entrada, e as formas `belt`, `field` e `vein` semeiam seu ruído a partir dele, então renomear um arquivo muda o que ele gera.

Descreve algo que gera. Toda entrada é uma **forma** colocada por uma **dispersão**, filtrada por onde é permitida.

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

Apenas `block` é obrigatório; tudo o mais pode ser omitido e assume seu padrão. `blocks` substitui `block` quando um só não basta e tem seu próprio exemplo abaixo.

### O que coloca

*entradas de worldgen*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `block` | sim | nome de bloco | | O que é colocado |
| `blocks` | não | lista de objetos | nenhum | Uma lista ponderada, usada no lugar de um bloco. Veja abaixo |
| `size` | não | int ou intervalo | `8` | Quantos blocos uma tentativa coloca, ou quão grande é uma forma com raio |
| `attempts` | não | int ou intervalo | `8` | Quantas vezes por chunk ela tenta |
| `replace` | não | lista de nomes de blocos ou objetos | `["minecraft:stone"]` | O que ela pode substituir. Veja abaixo |
| `adjacent` | não | lista de nomes de blocos ou objetos | nenhum | Só coloca onde um destes estiver entre os 26 blocos que tocam o ponto. Mesmas formas de `replace` |
| `sparse` | não | booleano | `false` | Espalha os blocos em vez de agrupá-los |
| `shape` | não | objeto | `{ "type": "cluster" }` | A forma que ela assume. Veja [Formas](#formas) |
| `spread` | não | objeto | `{ "type": "even" }` | Onde ela é posta. Veja [Dispersões](#dispersões) |

### Onde pode gerar

*entradas de worldgen*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `minHeight` | não | int | `0` | O menor y em que ela coloca |
| `maxHeight` | não | int | `64` | O maior y em que ela coloca |
| `dimensions` | não | lista de ids de dimensões | todas as dimensões | Em quais dimensões ela roda |
| `dimensionsAreBlacklist` | não | booleano | `false` | Transforma essa lista na das que devem ser evitadas |
| `biomes` | não | lista de nomes de biomas | todos os biomas | Em quais biomas ela roda |
| `biomeTypes` | não | lista de tipos de biomas | nenhum | Biomas por palavra de tipo, como `forest` ou `nether` |
| `biomesAreBlacklist` | não | booleano | `false` | Transforma essas listas nas das que devem ser evitadas |
| `minTemperature` | não | float | `-100.0` | O bioma mais frio em que ela gera |
| `maxTemperature` | não | float | `100.0` | O bioma mais quente em que ela gera |
| `minRainfall` | não | float | `-100.0` | O bioma mais seco em que ela gera |
| `maxRainfall` | não | float | `100.0` | O bioma mais úmido em que ela gera |
| `minDistanceFromSpawn` | não | int, blocos | `0` | A que distância do spawn do mundo ela começa |
| `caveRegions` | não | lista de nomes de regiões | nenhum | Gera apenas dentro destas [regiões de cavernas](#regiões-de-cavernas) |
| `snap` | não | `floor` ou `ceiling` | nenhum | Move cada tentativa verticalmente primeiro até o chão ou teto de caverna mais próximo |
| `snapDepth` | não | int | `0` | Quão além da superfície o `snap` então move, para baixo a partir de um chão e para cima a partir de um teto. `0` fica no espaço aberto junto à superfície, `1` é o próprio bloco da superfície, `2` o que está atrás dele. O que ela pode sobrescrever ainda é governado por `replace`, então é assim que um pacote faixeia um bloco logo abaixo do solo em vez de por cima |

### Placas de superfície e seguidores

*entradas de worldgen*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `indicators` | não | lista de `block=weight` | nenhum | Blocos deixados espalhados na superfície sobre um veio que gerou, para que um jogador saiba o que há sob o solo; escolha-os para combinar com o conteúdo do veio. `empty=weight` deixa um ponto vazio. Uma entrada sem peso, ou com peso abaixo de 1, é registrada no log e deixada de fora, e nenhuma é deixada nas ruas e construções de uma vila ou cidade |
| `indicatorCount` | não | int ou intervalo | `1` | Quantos pontos de superfície cada veio gerado recebe |
| `indicatorSpread` | não | int, blocos | `0` | Quão além da pegada do veio um indicador pode cair |
| `then` | não | lista de `name=weight` ou objetos | nenhum | Entradas de worldgen que crescem a partir desta logo depois que ela gera, ligadas a ela: a origem do seguidor é posta logo fora da borda deste veio, na direção que `thenSpread` e `thenDepth` dão, de modo que os dois se toquem. Uma entrada é `name=weight`, ou um objeto com `name`, `weight` e seus próprios `spread` e `depth` (int ou intervalo) que substituem os do veio apenas para aquele seguidor, então uma lista pode mandar uma ponta de diamante para baixo e um ramo para o lado. Um nome simples é lido no namespace deste pacote, `empty=weight` não enfileira nada. Um seguidor mantém sua própria forma, blocos, tamanho e `replace`, mas pula suas próprias tentativas, chance, faixa de altura e filtros de bioma, e pode ele mesmo ter `then`, tão fundo quanto o pacote quiser; uma entrada que já gerou na mesma cadeia a interrompe |
| `thenCount` | não | int ou intervalo | `1` | Quantos seguidores diferentes são escolhidos dessa lista por veio gerado, cada entrada no máximo uma vez, então uma contagem igual ao tamanho da lista faz crescer todos eles |
| `thenSpread` | não | int, blocos | o raio da forma | Quanto para o lado pode se inclinar a direção em que um seguidor cresce, sorteada de menos este valor a mais este valor |
| `thenDepth` | não | int ou intervalo | `0` | Quanto para baixo (negativo) ou para cima a direção se inclina. `0` sem inclinação lateral faz o seguidor pender reto para baixo |
| `prospectAs` | não | string | o nome do arquivo | Como um item de prospecção nomeia esta entrada em sua leitura, p. ex. `Hematite` |

### Retrogen e requisitos

*entradas de worldgen*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `retrogen` | não | booleano | `false` | Também gera em chunks que já existem |
| `retrogenKey` | não | string | a chave da configuração | Substitui a chave de retrogen apenas para esta entrada |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | A entrada é ignorada a menos que todos estejam presentes |

### Blocos ponderados

*entradas de worldgen*

`blocks` substitui `block` quando uma entrada não basta. Os pesos são relativos, então 80 e 20 é quatro para um.

```json
{
  "blocks": [
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "minecraft:oak_log", "weight": 20, "properties": { "axis": "x" } }
  ]
}
```

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `block` | sim | nome de bloco | | O que é colocado |
| `weight` | não | int | `1` | Com que frequência este é escolhido em relação aos outros |
| `properties` | não | objeto de propriedade para valor | nenhum | Propriedades de estado do bloco por nome, para um estado diferente do padrão do bloco |

`block` ainda é obrigatório no nível superior do arquivo mesmo quando `blocks` é usado; a primeira entrada é um bom valor para colocar ali.

### Alvos de substituição

*entradas de worldgen*

`replace` é uma lista, e cada entrada aceita uma de duas formas.

```json
{
  "replace": [
    "minecraft:stone",
    { "block": "minecraft:oak_log", "properties": { "axis": "y" } }
  ]
}
```

| Forma | Exemplo | O que corresponde |
| --- | --- | --- |
| Nome | `"minecraft:stone"` | Todo estado daquele bloco |
| Objeto | `{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }` | Apenas aquele estado |

Um nome do 1.12.2 com metadado no fim, `minecraft:stone:3`, corresponde a todo estado do bloco e avisa no log, já que os blocos que carregavam metadado agora são blocos separados: escreva `minecraft:diorite`. Use `"minecraft:air"` para gerar em espaço aberto.

### Blocos adjacentes

*entradas de worldgen*

`adjacent` aceita as mesmas formas de `replace` e soma uma segunda condição a ela: o ponto só é usado quando pelo menos um dos 26 blocos que o tocam, faces, arestas e cantos, corresponde à lista. Se omitido, nada é verificado.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

Isso coloca enxofre no arenito apenas onde ele já está aberto para uma caverna ou para a superfície, e deixa em paz o arenito enterrado. Vizinhos em chunks que ainda não existem são tratados como não correspondentes em vez de serem lidos, então a verificação nunca faz um chunk ser gerado.

Toda forma a respeita, já que ela faz parte de decidir se um único bloco pode ser tomado. Um `geode` nomeia sua crosta e seu preenchimento separadamente, e esses dois são colocados sem a verificação.

Uma entrada que nomeia apenas blocos que não estão registrados é ignorada com um erro em vez de gerar em todo lugar.

### Entradas de seguidores

*entradas de worldgen*

Uma entrada na lista `then` de uma entrada de worldgen é um nome com um peso, ou um objeto quando esse seguidor precisa de uma direção própria.

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `name` | sim | nome de entrada | | A entrada de worldgen que cresce a partir desta. Um nome simples é lido no namespace deste pacote |
| `weight` | não | int | `1` | Com que frequência este seguidor é escolhido em relação aos outros da lista |
| `spread` | não | int, blocos | o `thenSpread` da entrada | Quanto para o lado a direção deste seguidor pode se inclinar, apenas para esta entrada |
| `depth` | não | int ou intervalo | o `thenDepth` da entrada | Quanto para baixo, negativo, ou para cima a direção deste seguidor se inclina, apenas para esta entrada |

`name=weight` é a forma curta de um objeto com apenas esses dois, e `empty=weight` não enfileira nada. Como `spread` e `depth` são por entrada, uma lista pode mandar uma ponta de diamante reto para baixo e um ramo para o lado a partir do mesmo veio.

## Formas

*gerando o mundo*

Um bloco `shape` com um `type`. Chaves não listadas para um tipo são ignoradas por ele.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as de que precisa. Uma chave marcada para um tipo é lida apenas por esse tipo.

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

| Tipo | O que produz |
| --- | --- |
| `cluster` | A bolha padrão, um veio de minério. Usa `size` |
| `largevein` | Um veio longo e sinuoso com ramos. Usa `size` |
| `plate` | Um disco plano |
| `geode` | Uma bolsa oca com uma crosta |
| `decoration` | Espalhamento na superfície, como flores ou cogumelos. Usa `size` |
| `tree` | Uma árvore inteira |
| `vines` | Trepadeiras sobre o que já está ali. Usa `size` |
| `basin` | Uma bacia que se aprofunda em direção ao meio |
| `spire` | Uma coluna que afina |
| `nodule` | Uma bola irregular |
| `vent` | Uma coluna estreita que para quando bate em algo |
| `imprint` | Um dos seus modelos `.nbt`. Um que cabe dentro de um chunk é deslocado para que caia inteiro no chunk em construção, em vez de alcançar um vizinho que ainda não foi feito, não importa o sentido em que esteja girado; um maior que um chunk só é colocado onde o terreno ao redor já existe |
| `belt` | Um cluster que abrange vários chunks, para regiões de pedra |
| `field` | Veios calculados para todo bloco de uma vez, compartilhando sua forma com os grupos de dureza |
| `vein` | Um depósito calculado como um campo de ruído semeado ao redor de uma origem, como o Immersive Geology faz: cada chunk escreve sua própria fatia de todo veio cujo alcance de 24 blocos o toca, então nada se propaga em cascata, e `/rdplserver vein` pode dizer onde um veio estará antes que a terra seja feita. Usa `size`, `attempts`, `rarity` e a faixa de altura; `pattern` escolhe a aparência |
| `spring` | Um fluido vazando de uma parede de caverna: colocado onde há rocha acima, abaixo e em três lados com um lado aberto, e posto para fluir |

### Tamanho e forma

*formas*

| Chave | Usado por | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `type` | todos | uma das formas acima | `cluster` | Qual forma |
| `radius` | plate, geode, basin, spire, nodule, vent | int ou intervalo | `6` | Quão larga ela é |
| `height` | plate, geode, basin, spire, vent, tree | int ou intervalo | `1`, `8` para geode, `5` para tree | Quão alta ou espessa ela é |
| `width` | geode | int ou intervalo | `12` | A extensão total da bolsa |
| `plane` | plate, basin, spire, vent | `circle`, `square` | `circle` | Sua pegada |
| `slim` | plate, largevein, nodule | booleano | `false` | Plate: uma camada mais fina. Largevein: ramos de um bloco só. Nodule: casca oca |
| `hanging` | spire, vent | booleano | `false` | Cresce para baixo a partir de um teto em vez de para cima a partir de um chão |
| `taper` | spire | `straight`, `bell`, `needle` | `straight` | Como a largura diminui em direção à ponta. `straight` estreita de modo uniforme, `bell` mantém sua largura embaixo e depois cai, `needle` afina de imediato em uma ponta longa |
| `outline` | geode | nome de bloco | nenhum | O bloco da crosta |
| `fill` | geode | nome de bloco | nenhum | O que preenche o meio. Se omitido, o meio é oco |
| `middle` | geode | nome de bloco | nenhum | Uma casca entre o corpo e `outline`, a calcita do geodo de ametista do jogo |
| `budding` | geode | nome de bloco | nenhum | Trocado pelos blocos do corpo voltados para o meio oco, como a ametista em brotamento faz. Precisa de `fill` |
| `buddingChance` | geode | 0.0 a 1.0 | `0.083` | Quantos desses blocos do corpo brotam |
| `crystal` | geode | nome de bloco | nenhum | Crescido no oco ao lado de um bloco `budding`, como um aglomerado de ametista |
| `crystalChance` | geode | 0.0 a 1.0 | `0.35` | Quantos desses pontos fazem crescer um |
| `crack` | geode | 0.0 a 1.0 | `0` | A chance de um geodo estar rachado: um tubo do meio para fora por todas as camadas em um lado, preenchido com `fill`. Os geodos de ametista do jogo usam `0.95` |

### Posicionamento

*formas*

| Chave | Usado por | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `surface` | decoration, tree | lista de nomes de blocos | nenhum | Sobre o que ela vai ficar |
| `seeSky` | decoration | booleano | `true` | Só coloca onde o céu é visível |
| `checkStay` | decoration | booleano | `true` | Só coloca onde o bloco sobreviveria |
| `stackHeight` | decoration | int ou intervalo | `1` | Quantos empilhar um sobre o outro |
| `scatterX` | decoration, tree | int | `8` | Quão longe ela vagueia para o lado |
| `scatterY` | decoration, tree | int | `4` | Quão longe ela vagueia na vertical |
| `scatterZ` | decoration, tree | int | `8` | Quão longe ela vagueia para o lado |
| `rarity` | qualquer | int | nenhum (`400` para belt) | Uma colocação a cada tantos chunks. Em um belt isso espaça os belts; em qualquer outra forma ele controla a entrada inteira, de modo que apenas um chunk em tantos sorteia suas `attempts`. `field` o ignora |
| `rarityIsPerChunk` | qualquer | booleano | `false` | Transforma `rarity` em quantas colocações cada chunk recebe |

### Árvores

*formas*

| Chave | Usado por | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `log` | tree | nome de bloco | nenhum | O bloco do tronco |
| `leaves` | tree | nome de bloco | nenhum | O bloco das folhas |
| `vines` | tree | booleano | `false` | Pendura trepadeiras das folhas |

Uma `tree` sem `log` ou `leaves` não gera nada, e avisa no log. Nomear uma `structure`, ou várias em `structures`, planta esse modelo em cada ponto em vez de fazer crescer uma, e então nenhum `log` ou `leaves` é necessário; uma árvore de modelo lê `turns`, `mirrors`, `integrity`, `lootTable` e `locateAs` exatamente como um `imprint` faz.

### Colocando modelos

*formas*

| Chave | Usado por | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `structure` | imprint, tree | `namespace:name` | nenhum | O modelo a colocar |
| `integrity` | imprint, tree | 1 a 100 | `100` | Porcentagem dos blocos do modelo que realmente aparecem |
| `lootTable` | imprint, tree | `namespace:path` | nenhum | A tabela de loot de que todo baú dentro do modelo colocado é preenchido na primeira vez em que é aberto, e qualquer outro contêiner que aceite uma, entre eles uma shulker box ou um engradado de mod. Cobre `structure` e toda entrada de `structures`; cada baú sorteia sua própria seed |
| `structures` | imprint, tree | lista | nenhum | Vários modelos para escolher, um colocado a cada vez. Cada entrada é `{ "structure": "namespace:name", "weight": 3 }`, ou um nome simples para chances iguais. Substitui `structure` |
| `turns` | imprint, tree | lista | qualquer | Em que sentido ele pode ser colocado: `none`, `quarter`, `half`, `threequarter`. As entradas podem carregar um `weight`. Se omitido, os quatro são igualmente prováveis |
| `mirrors` | imprint, tree | lista | nenhum | Também o espelha: `none`, `leftright`, `frontback`, com `weight` opcional. Uma entrada que nomeia seu próprio peso é escrita `{ "mirror": "leftright", "weight": 2 }`, e uma entrada de `turns` da mesma forma com `turn` |
| `at` | imprint | dois ints, x e z | nenhum | Coloca exatamente uma vez nessas coordenadas de bloco na superfície, quando aquele chunk gera, em vez de por acaso. Veja [Estruturas em locais exatos](#estruturas-em-locais-exatos) |
| `locateAs` | imprint, tree | string | nenhum | Registra toda estrutura que esta entrada coloca sob esse nome, para que `/rdplserver locate <name>` encontre a mais próxima. Veja [Encontrando estruturas colocadas](#encontrando-estruturas-colocadas) |

Para uma forma que nenhum tipo embutido cobre, `imprint` é o caminho: construa-a como um modelo `.nbt` e coloque-o, com `structures` para variá-lo, `turns` e `mirrors` para girá-lo e `integrity` para dissolvê-lo em algo mais áspero do que o arquivo que você desenhou.

### Estruturas em locais exatos

*formas*

Estruturas do vanilla são fixadas em pontos exatos com `structureAt` nas configurações de `terrain`, como entradas `structure=x,z`, uma por linha: `"structureAt": ["villages=1000,-500"]`. **O x e o z são coordenadas de bloco, não de chunk**, e a estrutura gera no chunk que contém esse bloco. Com `terrainAdaptation` dispondo as vilas como ruas de cidade, o poço de uma vila fixada fica naquele próprio bloco, ou o mais perto dele que seu distrito permitir quando o bloco está a poucos blocos da borda do distrito; outras estruturas, e vilas dispostas sem ele, começam onde o jogo as começaria naquele chunk. Uma entrada por instância desejada. Seu espaçamento, separação, distância mínima do spawn e verificações de terreno plano ficam todos de lado, então o ponto é responsabilidade do pacote, e duas fixações a menos de um chunk de distância põem duas estruturas no mesmo chunk. A estrutura se assenta no solo em seu chunk pelas regras de sempre depois de fundada.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `structureAt` | lista de `structure=x,z` | nenhum | Fixa uma estrutura do vanilla em um ponto exato, uma entrada por instância desejada. O x e o z são coordenadas de bloco, e a estrutura gera no chunk que contém esse bloco; seu espaçamento, separação, distância mínima do spawn e verificações de terreno plano ficam todos de lado |

Uma entrada `imprint` fixa da mesma forma com `"at": [x, z]` em sua forma, colocando exatamente uma vez nessas coordenadas na superfície quando aquele chunk gera, em vez de por acaso. Ela se combina com `locateAs`, então uma estrutura fixada também pode ser encontrada.

### Encontrando estruturas colocadas

*formas*

Uma entrada `imprint` com `"locateAs": "Crypt"` registra toda estrutura que ela coloca sob esse nome, e `/rdplserver locate Crypt` então aponta para a mais próxima, com o nome oferecido no autocompletar com Tab; `/rdplserver goto Crypt` leva você até lá. Apenas estruturas que já foram geradas podem ser encontradas, já que as estruturas de pacote são colocadas por acaso conforme os chunks são feitos, e não em uma grade que o jogo poderia prever. Os nomes vivem no save do mundo, então sobrevivem a reinicializações e funcionam em servidores. Um nome registrado assim também pode receber sua própria permissão com `gotoPlaceLevels`, de modo que um pacote decide quem pode ser levado às suas próprias estruturas separadamente das do vanilla.

### Chaves de campos e veios

*formas*

| Chave | Usado por | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `field` | field | objeto | `{ "type": "speckle" }` | Como o campo é calculado. As mesmas chaves do `field` de um grupo de dureza, descritas em [O campo](#o-campo): `speckle` com `chances` e `spread`, ou `seeded` com `cell`, `seeds`, `reach`, `arms` e `armReach` |
| `threshold` | field, vein | 0.0 a 1.0 | `0.5` (`0.4` para vein) | Quão forte o campo precisa estar em um bloco antes de ele ser colocado. Mais baixo preenche mais |
| `fade` | field | int | `0` | Esmaece a parte de cima da faixa em vez de terminá-la reta: nos últimos tantos blocos da faixa de altura, as chances de cada bloco ser colocado diminuem passo a passo, o mesmo visual que o motor dá a `deepStone` onde ele encontra o mundo acima |
| `pattern` | vein | `default`, `banded` ou `tube` | `default` | A aparência do depósito: uma bolha deformada, camadas empilhadas a cada poucos blocos, ou tubos ocos serpenteando pela rocha |
| `density` | vein | 0.0 a 1.0 | `1.0` | A parcela dos blocos qualificados que são realmente colocados, uma moeda por bloco |
| `rich` | vein | nome de bloco | nenhum | Colocado de `richAt` para cima na faixa do campo acima de `threshold`, o coração do depósito, no lugar dos blocos da entrada |
| `poor` | vein | nome de bloco | nenhum | Colocado nos dois quintos inferiores dessa faixa, a franja, no lugar dos blocos da entrada; o meio são os blocos da própria entrada. Qualquer camada omitida coloca ali os blocos da entrada |
| `richAt` | vein | 0.0 a 1.0 | `0.88` | Onde a camada rica começa nessa faixa: `0.88` mantém o bloco rico no oitavo mais forte do depósito, um número menor engorda o núcleo rico, `1.0` não deixa nenhum bloco rico |
| `poorAt` | vein | 0.0 a 1.0 | `0.4` | Onde os blocos da própria entrada começam: abaixo disso o bloco `poor` é colocado, então `0.4` dá uma franja dos dois quintos inferiores e `0.0` não deixa franja pobre. Limitado a `richAt` |

Um veio `field` é a única forma que você descreve em vez de escolher. Ele roda a mesma malha que os grupos de dureza usam, então `seeded` com alguns braços dá nós com tentáculos alcançando seus vizinhos, que é um veio e não uma bolha, e `threshold` decide quanto dele é sólido o bastante para ser colocado:

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

As chaves vão em um objeto `field` próprio, não ao lado de `type`, já que `type` na forma já diz `field`.

### Cinturões

*formas*

Um `belt` é uma bola muito maior que um chunk, usada para regiões de pedra e não para veios de minério. Seu `radius` é o tamanho da bola, e cada chunk calcula por si mesmo onde as bolas próximas começam, a partir da seed do mundo e do nome da própria entrada, então um belt sai inteiro não importa como os chunks sejam gerados e nada nunca é escrito em um chunk vizinho.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Um belt ignora `attempts` e `spread`, já que é colocado por chunk e não por tentativa. `minHeight` e `maxHeight` são a faixa em que os centros ficam, e a bola alcança `radius` além dessa faixa. `replace` decide o que ele consome, `biomes` e os limites de temperatura e chuva são verificados no centro, então um belt aparece por inteiro ou não aparece, em vez de ser cortado na borda de um bioma.

O custo cresce com o cubo de `radius`, e uma `rarity` baixa o multiplica, então comece pelos padrões e aumente o raio devagar.

### Campos

*formas*

Um `field` não coloca nada em um ponto e coloca tudo de uma vez. Em vez de escolher um local e construir uma forma ao redor dele, ele faz uma pergunta a cada bloco do chunk, entre `minHeight` e `maxHeight`, e coloca onde a resposta for no mínimo `threshold`. A pergunta é a mesma que os grupos de dureza fazem, então os dois descrevem os mesmos veios, e um pacote pode criar um grupo e uma entrada que concordam entre si.

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

| Chave       | Obrigatório | Valor      | Padrão   | O que faz                                                                           |
| ----------- | ----------- | ---------- | -------- | ----------------------------------------------------------------------------------- |
| `threshold` | não         | 0.0 a 1.0  | `0.5`    | Qual a intensidade que o campo precisa ter para que um bloco seja colocado          |
| `field`     | sim         | objeto     | nenhum   | O mesmo objeto que um grupo de dureza aceita, com os mesmos tipos `speckle` e `seeded` |

Um `threshold` baixo aproveita a maior parte do campo e gera filões amplos; um alto aproveita só o centro de cada aglomerado e gera bolsões pequenos e espalhados. Com `speckle` você obtém muitas pintinhas minúsculas; com `seeded`, manchas mais arredondadas ou, quando ganha braços, nós com ramificações se estendendo entre eles.

Como um cinturão, um campo ignora `attempts` e `spread`, já que é consultado por chunk e não por tentativa, e nunca escreve em um chunk vizinho. Ele é calculado a partir da seed do mundo e do nome da própria entrada, então a mesma seed sempre gera os mesmos veios, e duas entradas com nomes diferentes nunca coincidem. `replace`, `adjacent`, `biomes` e os limites de clima se aplicam normalmente.

## Dispersões

*gerando o mundo*

Um bloco `spread` com um `type`.

Todas as chaves, mostradas de uma só vez. Um arquivo real escreve apenas as que precisa. Uma chave marcada para um tipo é lida somente por esse tipo.

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

| Tipo        | Onde coloca as coisas                                          |
| ----------- | -------------------------------------------------------------- |
| `even`      | Em qualquer ponto entre as alturas, de forma uniforme. O padrão |
| `centered`  | Ponderado em torno de uma altura, rareando com a distância     |
| `sprawl`    | Veios fractais que abrangem uma faixa de altura                |
| `terrain`   | Acompanhando a superfície                                      |
| `cavern`    | No piso ou no teto das cavernas                                |
| `submerged` | Debaixo d'água ou de outro fluido                              |

| Chave               | Usado por | Valor                    | Padrão                       | O que faz                                                         |
| ------------------- | --------- | ------------------------ | ---------------------------- | ----------------------------------------------------------------- |
| `type`              | todos     | uma das dispersões acima | `even`                       | Qual dispersão                                                    |
| `center`            | centered  | int                      | ponto médio da faixa de altura | A altura em torno da qual se concentra                          |
| `range`             | centered  | int                      | metade da faixa de altura    | Até onde se estende a partir dessa altura                         |
| `smoothness`        | centered  | 1 a 8                    | `2`                          | Quantos sorteios entram na média. Quanto maior, mais estreita a faixa |
| `veinHeight`        | sprawl    | int                      | a faixa de altura            | A altura de um veio                                               |
| `veinDiameter`      | sprawl    | int                      | `12`                         | A largura de um veio                                              |
| `verticalDensity`   | sprawl    | 1 a 100                  | `16`                         | O quão maciço é na vertical                                       |
| `horizontalDensity` | sprawl    | 1 a 100                  | `32`                         | O quão maciço é na horizontal                                     |
| `offsetMin`         | terrain   | int                      | `0`                          | Menor deslocamento em relação à superfície                        |
| `offsetMax`         | terrain   | int                      | `offsetMin`                  | Maior deslocamento em relação à superfície                        |
| `ceiling`           | cavern    | booleano                 | `false`                      | Fixar no teto da caverna em vez de no piso                        |

## Mapas de estruturas

*gerando o mundo*

Um mapa de estruturas compõe modelos em uma única construção nomeada sobre uma grade, muito além do limite de 48 blocos de um único arquivo `.nbt`. Cada camada é desenhada como linhas de caracteres isolados, um caractere por célula, e se empilha uma altura de célula acima da camada anterior. No máximo 8 camadas de 8 por 8 células, o que, com a célula padrão de 32, dá 256 blocos de lado.

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

| Configuração | Tipo                  | Padrão  | O que faz                                                                                                                                              |
| ------------ | --------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `cell`       | número                | `32`    | O passo da grade em blocos, até 48. Um modelo menor que a célula fica no canto da célula, de modo que peças de tamanho total se encaixam sem emendas  |
| `ground`     | número                | `0`     | Qual camada assenta na superfície do terreno. As camadas anteriores cavam para baixo, e é assim que uma construção ganha porões                        |
| `at`         | dois números          | nenhum  | Fixa uma cópia em coordenadas exatas de bloco, como `structureAt` fixa uma vila                                                                        |
| `spacing`    | número                | `0`     | Espalha cópias em uma grade com essa quantidade de chunks de distância, com variação sorteada a partir da seed do mundo. `0` não espalha nenhuma, então um mapa só com `at` constrói exatamente uma vez |
| `chance`     | número                | `100`   | A porcentagem dos pontos da grade que constroem uma cópia                                                                                              |
| `dimensions` | lista de ids de dimensão | todas | Onde o mapa pode ser construído, incluindo as dimensões do próprio pacote                                                                              |
| `layers`     | lista                 | nenhum  | As camadas, de baixo para cima, cada uma com um `palette` e um `map`                                                                                   |

Uma paleta nomeia modelos pela chave de registro, a partir do `<namespace>/structures/` de um pacote.

| Valor                                       | O que faz                                                                                                                                                                                  |
| ------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `"a": "mypack:keep"`                        | Toda célula `a` dessa camada coloca este modelo                                                                                                                                            |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Cada célula `a` sorteia da lista por peso, a partir da seed do mundo e da posição da célula, de modo que duas cópias da construção diferem, mas o mesmo mundo sempre constrói a mesma      |
| `.`                                         | Uma célula vazia, nada é colocado                                                                                                                                                          |

Cada cópia sorteia uma das quatro orientações a partir da seed do mundo e a construção inteira gira junto, modelos incluídos, de modo que paredes que se encontram entre células continuam se encontrando; um mapa gira, mas nunca é espelhado. A camada do solo assenta na superfície do terreno amostrada sob o meio da construção, e o mapa inteiro compartilha essa única altura. Um mapa espalhado é, para o jogo, uma estrutura por si só, colocada por meio de um conjunto de estruturas escrito para você, de modo que cada chunk constrói apenas a sua fatia da grade e uma construção que abrange muitos chunks surge sem geração em cascata, em qualquer ordem em que os chunks carreguem. Um [terreno de vila](#terrenos-de-vilas) do tipo `template` também pode nomear um mapa como seu `structure`, o que faz do composto um prédio da cidade.

## Terrenos de vilas

*gerando o mundo*

`<namespace>/villages/*.json`

O caminho do arquivo é o nome do terreno, que `villagePieces` pode então citar para mantê-lo ou descartá-lo.

Um arquivo aqui adiciona uma peça que as cidades e vilas do pacote podem construir. Há dois tipos, escolhidos com `type`.

Todas as chaves, mostradas de uma só vez. Um arquivo real escreve apenas as que precisa. Uma chave marcada para um tipo é lida somente por esse tipo.

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

### Cada terreno

*terrenos de vilas*

| Chave        | Usado por | Valor                                  | Padrão           | O que faz                                                                                                                                                                                                                                                                                                                                                                              |
| ------------ | --------- | -------------------------------------- | ---------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`       | todos     | `farm` ou `template`                   | `farm`           | Qual tipo de terreno                                                                                                                                                                                                                                                                                                                                                                   |
| `weight`     | todos     | int                                    | `3`              | Com que frequência este terreno é escolhido em relação aos demais do pacote                                                                                                                                                                                                                                                                                                            |
| `leastCount` | todos     | int                                    | `1`              | Mínimo por distrito: um distrito acomoda terrenos ao longo de suas ruas até um limite sorteado entre o menor `leastCount` e o maior `mostCount` dos terrenos que pode construir, ambos aumentados em 16, ou em um trinta e dois avos de `villagePlotsLeast` quando for maior, enquanto uma cidade cresce. Terrenos atrás de outros terrenos não contam                                      |
| `mostCount`  | todos     | int                                    | `4`              | O topo desse sorteio                                                                                                                                                                                                                                                                                                                                                                   |
| `width`      | todos     | int                                    | `7`              | Tamanho ao longo da rua                                                                                                                                                                                                                                                                                                                                                                |
| `height`     | todos     | int                                    | `4`              | Altura liberada acima do solo                                                                                                                                                                                                                                                                                                                                                          |
| `depth`      | todos     | int                                    | `9`              | Tamanho a partir da rua, para dentro                                                                                                                                                                                                                                                                                                                                                   |
| `apron`      | todos     | int                                    | `2`              | O quanto o solo pode ficar fora do nível da rua sob o terreno antes que ele seja recusado ou deslizado ao longo da rua: essa quantidade de blocos de aterro sob ele, ou de corte em uma elevação acima, e não mais do que isso entre seu canto mais alto e o mais baixo. Um terreno largo em colinas precisa de mais. Se for alto demais, o terreno se escalona direto em uma encosta, o que no lugar errado devora uma montanha |
| `ground`     | todos     | nome de bloco                          | `minecraft:dirt` | O que é compactado por baixo em uma encosta                                                                                                                                                                                                                                                                                                                                            |
| `requires`   | todos     | lista de ids de mods ou namespaces de pacotes | nenhum    | O terreno é deixado de fora, a menos que todos estejam presentes                                                                                                                                                                                                                                                                                                                       |

Terrenos são o que as cidades do próprio pacote constroem ao longo de suas ruas, e todo terreno `template` também se junta às vilas do próprio jogo como uma de suas casas, com entrada no meio da frente. Sem nenhum arquivo de terreno, uma cidade constrói as casas de vila do próprio jogo para o tipo de vila do bioma de cada distrito. `weight` decide qual dos seus terrenos é escolhido quando uma rua pede um, e `villagePieces` nas configurações de `villages` nomeia os terrenos que um modelo mantém, como `mypack:smithy`, `smithy` ou a estrutura que o terreno constrói. Como as próprias ruas são traçadas, decoradas, atravessadas por pontes, túneis e trilhos é o que as configurações `village*` definem em [O que cada grupo faz](#o-que-cada-grupo-faz).

### Fazendas

*terrenos de vilas*

Uma `farm` é um campo descrito em vez de codificado: um terreno do tamanho que você pedir, contornado por um bloco, preenchido com fileiras de solo separadas por canais de água, plantado com uma cultura escolhida por bloco a partir da sua lista.

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

| Chave      | Usado por | Valor                  | Padrão               | O que faz                                          |
| ---------- | --------- | ---------------------- | -------------------- | -------------------------------------------------- |
| `crops`    | farm      | lista de nomes de bloco | trigo               | Plantada uma por bloco, em um estágio de crescimento aleatório |
| `edge`     | farm      | nome de bloco          | `minecraft:oak_log`  | A moldura ao redor do terreno                      |
| `soil`     | farm      | nome de bloco          | `minecraft:farmland` | Do que as fileiras são feitas                      |
| `water`    | farm      | booleano               | `true`               | Colocar um canal de água entre as fileiras         |
| `rowWidth` | farm      | int                    | `2`                  | A largura de cada fileira de solo                  |

### Construído a partir de modelos

*terrenos de vilas*

Um `template` coloca, em vez disso, uma das suas estruturas `.nbt`, virada para a rua.

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

Um `template` cujo `structure` nomeia um dos seus [mapas de estruturas](#mapas-de-estruturas) coloca o composto inteiro como terreno. O tamanho do terreno passa então a vir do mapa, sua área e suas camadas empilhadas vezes a célula, de modo que `width`, `height`, `depth` e `integrity` não são lidos. As camadas antes do `ground` do mapa cavam para baixo como porões, e células ponderadas da paleta ainda são sorteadas por construção, então duas torres do mesmo mapa podem ser diferentes.

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| Chave            | Usado por | Valor            | Padrão     | O que faz                                                                                                                                                      |
| ---------------- | --------- | ---------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structure`      | template  | `namespace:name` | nenhum     | O modelo a colocar, ou um dos seus mapas de estruturas, que então define o tamanho do terreno                                                                  |
| `integrity`      | template  | 1 a 100          | `100`      | Porcentagem dos blocos do modelo que aparecem                                                                                                                  |
| `lootTable`      | template  | `namespace:path` | nenhum     | A loot table da qual todo baú dentro do modelo colocado é preenchido na primeira vez em que é aberto. Um terreno que nomeia um mapa de estruturas fica de fora |
| `villagers`      | todos     | int              | `0`        | Quantas pessoas o terreno gera                                                                                                                                 |
| `villagerEntity` | todos     | `namespace:name` | um aldeão  | Quem mora ali, como uma variante de entidade sua                                                                                                               |
| `villagerX`      | todos     | int              | `1`        | Onde aparecem, ao longo do terreno                                                                                                                             |
| `villagerY`      | todos     | int              | `1`        | Onde aparecem, acima do piso                                                                                                                                   |
| `villagerZ`      | todos     | int              | `1`        | Onde aparecem, para dentro do terreno                                                                                                                          |

## Mapas de layout de cidades

*gerando o mundo*

Um mapa de cidade desenha a planta de ruas de uma cidade em uma grade, um caractere por célula, e a cidade é traçada a partir do desenho em vez de sorteada. Ruas, praças e terrenos saem como as mesmas peças que uma cidade sorteada usa, então toda opção de rua, ponte, túnel, metrô, esgoto, poste de luz e peça central de praça se aplica sem alteração. O modelo de mundo nomeia o mapa em `villageLayout`.

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

| Configuração | Tipo   | Padrão  | O que faz                                                                                                                                                                                                                       |
| ------------ | ------ | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `cell`       | número | `48`    | O passo da grade em blocos, de 8 a 128. As ruas correm pelo meio de suas células na largura de rua do pacote e os terrenos ficam centralizados nas suas, então uma célula precisa comportar o terreno mais largo mais espaço para dar frente à rua |
| `palette`    | objeto | nenhum  | O que cada caractere traça, listado abaixo                                                                                                                                                                                      |
| `map`        | lista  | nenhum  | As linhas, até 64 por 64 células. Uma linha mais curta que a mais larga fica aberta depois do seu fim                                                                                                                           |
| `settings`   | objeto | nenhum  | Configurações de cidade só para este mapa, com os nomes que um modelo de mundo usa, como `villagePathCenterBlock`. Elas prevalecem sobre as do modelo, e as configurações do próprio bioma ainda prevalecem sobre elas            |

| Valor                                                                   | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| ----------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `"#": "street"`                                                         | Uma sequência de células de rua ao longo de uma linha ou coluna vira uma única rua na largura do pacote. Onde uma sequência de linha cruza uma de coluna, o cruzamento é pintado como qualquer outro. Uma célula de rua isolada, sem sequência em nenhum dos eixos, é traçada como um pequeno trecho ao longo da linha                                                                                                                                                                                                         |
| `"+": "plaza"`                                                          | Uma praça com sua peça central. As sequências atravessam as células da praça, então as ruas se encontram na praça, e uma praça em um cruzamento ergue sua peça central `villageWellStructure` no meio do cruzamento, como uma rotatória. A primeira praça do arquivo é o centro da própria cidade, o que fixa o mapa onde a cidade é fundada; um mapa sem praça é centralizado ali                                                                                                                                              |
| `"a": "alley"`                                                          | Uma sequência estreita. Prédios dão frente para ela, mas ela não liga nada, a regra do beco como de costume                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `"J": "junction"`                                                       | Uma célula de rua traçada nas duas direções, de modo que um cruzamento se ergue ali mesmo quando o desenho passa por ela em um só sentido. O braço que a atravessa tem uma célula de comprimento                                                                                                                                                                                                                                                                                                                              |
| `"b": "bulb"`                                                           | Uma célula de rua que termina em um beco sem saída com retorno. Quando um mapa tem uma célula bulb, só as pontas de rua que ficam em células bulb ganham um; um mapa sem nenhuma dá retorno a três em cada quatro pontas mortas, sorteadas a partir da seed do mundo. Um retorno só é assentado onde nenhum terreno, outra rua, ferrovia ou peça central de praça esteja ao seu alcance: ele encolhe para caber, até ficar um pouco mais largo que a rua, e uma ponta sem espaço em qualquer tamanho continua uma ponta simples |
| `"E": { "kind": "elevated", "height": 8 }`                              | Uma célula de rua elevada a um tabuleiro `height` blocos, de 2 a 64, acima do solo mais alto sob o seu trecho de células elevadas unidas, com uma rampa de um bloco por linha em cada ponta. Um cruzamento de rua dentro do trecho sobe com ele, e os terrenos ao longo dela permanecem no chão. Um trecho cujo tabuleiro ou rampas alcançariam uma ferrovia ou a peça central da praça permanece no nível do solo, com uma linha no log. Qualquer valor pode ser escrito como objeto desta forma, com `kind` dando o nome da palavra |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | Uma rua traçada e pavimentada com suas próprias chaves de rua, que prevalecem sobre as do mapa e do modelo. Sua largura segue seu próprio `villagePathExtraWidth`, `villagePathSidewalkWidth` e linha, e sua superfície, linhas e calçadas seguem suas próprias chaves de bloco, de modo que uma avenida ou uma travessa é desenhada com uma marca própria. Uma sequência assume as chaves da primeira célula que define alguma. Por mais larga ou estreita que seja, uma rua desenhada continua sendo uma rua: nunca é tomada por um beco |
| `"T": "mypack:tower"`                                                   | Uma célula de terreno, traçada a partir dessa definição de terreno, centralizada na célula e voltada para a rua mais próxima                                                                                                                                                                                                                                                                                                                                                                                                  |
| `"T": ["mypack:a=3", "mypack:b=1"]`                                     | O mesmo, sorteado por peso a partir da seed do mundo e da posição da célula, de modo que o mesmo mundo sempre traça o mesmo terreno ali                                                                                                                                                                                                                                                                                                                                                                                       |
| `"g": "grow"`                                                           | Deixada para o layout sorteado, que preenche essas células e se espalha para fora a partir do mapa                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `.` ou `open`                                                           | Terreno aberto, nada traçado                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |

Todo mapa sorteia uma das quatro orientações a partir da seed do mundo e gira inteiro, de modo que uma planta se lê igual de qualquer lado. As ruas são traçadas primeiro, então um terreno que sobreporia uma rua ou outro terreno é deixado aberto com uma linha no log, e um nome de terreno que nenhum pacote fornece deixa a célula aberta do mesmo modo. O mapa não muda a forma como as peças são decoradas: as chaves de rua, `villageBlocks`, os postes e a peça central da praça se leem como em uma cidade sorteada.

## Cidade contínua

*gerando o mundo*

Uma cidade contínua não tem borda. Com `villageCitySpacing` em `1`, todo distrito do mundo é uma cidade própria: uma praça com o poço no centro e ruas saindo dela que se unem às ruas dos distritos ao redor. A cidade continua sendo gerada para onde quer que os jogadores vão, sem campo aberto entre as cidades.

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `terrainAdaptation` | booleano | `false` | Assenta as ruas de cidade próprias do RDPL. Uma cidade contínua precisa dele ligado |
| `villageCitySpacing` | int, 0 a 256 | `16` | `1` faz de todo distrito uma cidade, e é isso que torna a cidade contínua. Um número maior semeia cidades separadas que terminam na própria borda |
| `villageBlockSizes` | lista de `size=weight` | vazio | Qual a profundidade dos quarteirões entre ruas paralelas, sorteada uma vez por distrito, de modo que a cidade mistura grades finas e grossas |
| `villagePlotsMost` | int, 0 ou mais | `0` | O máximo de terrenos que um distrito acomoda. 0 não define teto |
| `villagePlotsBackRow` | booleano | `true` | Acomoda um terreno atrás de todo terreno voltado para uma rua, para que o interior de cada quarteirão também seja construído |

Todas as outras configurações de rua, dos blocos da pista a postes, pontes, túneis e esgotos, vestem uma cidade contínua como vestem qualquer outra. `villagePlotsLeast` não tem efeito, já que todo distrito já é uma cidade inteira. Um distrito cujo plano tem dois ou menos terrenos e poços fica vazio, como qualquer cidade desse tamanho.

**Como as ruas se encontram.** Todo distrito traça sua cruz principal, as duas ruas que se cruzam no seu poço, no mesmo lugar dentro do distrito. Por isso as ruas da cruz de distritos vizinhos se alinham em avenidas retas que seguem pelo mundo todo, e as ruas que saem de cada praça se unem às do distrito seguinte. O poço de um distrito nunca muda de lugar, não importa como os vizinhos sejam traçados.

**Quanto custa.** Todo chunk de uma cidade contínua é construído, então terreno novo custa em toda parte o que custa o centro de uma cidade grande: colocar os prédios e iluminá-los. Um jogador voando rápido ultrapassa a geração, e a taxa de ticks cai enquanto distritos novos são gerados. Pré-gerar o terreno onde os jogadores começam, com `pregenOnNewWorld` ou `/rdplserver pregen`, e uma distância de visão moderada mantêm isso sob controle, mas o TPS continua mais baixo que em um mundo comum enquanto os jogadores exploram terreno novo. O save cresce com o terreno explorado, como qualquer terreno construído. O jogo mantém no máximo 1024 planos de distrito na memória e refaz a partir da seed, exatamente como antes, um plano que liberou quando precisa dele de novo.

## Retrogen

*gerando o mundo*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "retrogen": true,
    "adoptExistingChunks": false
  }
}
```

| Configuração          | Tipo     | Padrão  | O que faz                                                                                                                                                                                                                                                                                  |
| --------------------- | -------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `retrogen`            | booleano | `false` | Atualiza chunks salvos antes de uma entrada existir, em toda entrada de worldgen marcada com `"retrogen": true`. Desligado, chunks que já existem são deixados em paz. Os chunks são marcados à medida que são gerados de qualquer forma, então ligar isto depois só afeta chunks mais antigos que o pacote |
| `adoptExistingChunks` | booleano | `false` | O que acontece na primeira vez que um chunk antigo é visto: ligado, ele é carimbado como se este pacote já o tivesse gerado e nunca é atualizado; desligado, é atualizado como qualquer outro. Para preencher um mundo existente, ligue `retrogen` e deixe este desligado                  |

Uma entrada com `"retrogen": true` é gerada em chunks que foram salvos antes de você a adicionar. Cada chunk registra o que já recebeu, então nada é feito duas vezes.

A flag da entrada apenas marca uma entrada como elegível. A atualização é ligada pela configuração `retrogen`, que um pacote pode definir em seu bloco `settings` ou um jogador pode definir na config, e ela vem desligada por padrão. Junto dela, `adoptExistingChunks` decide o que acontece na primeira vez que um chunk antigo é visto: ligado, o chunk é carimbado como se este pacote já o tivesse gerado e nunca é atualizado; desligado, é atualizado como qualquer outro. Ligar `retrogen` enquanto `adoptExistingChunks` também está ligado não faz nada, porque todo chunk antigo é dado como resolvido antes de poder entrar na fila. Para preencher um mundo existente, ligue `retrogen` e desligue `adoptExistingChunks` juntos. `retrogenChunksPerTick` na config, com padrão `2`, é quantos chunks antigos são atualizados a cada tick.

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

Mudar `retrogenKey` na config torna todo chunk elegível de novo, o que adiciona os novos veios por cima dos antigos, então a densidade dobra. Isso é proposital, e é por isso que a chave é manual.

## Pré-geração

*gerando o mundo*

Criar o terreno de um mundo com antecedência, para que ninguém gere chunks enquanto joga: sem lag de chunks, um tamanho conhecido em disco e uma única espera no início em vez de uma primeira hora de travadas.

Os primeiros 12 chunks ao redor do spawn são sempre tratados, seja o que for que um pacote ou a config digam, porque o jogo faz exatamente isso por conta própria antes de qualquer um entrar. `pregenOnNewWorld` define até onde ir além disso, e o comando executa uma à mão.

`/rdplserver pregen <radius>` cria todo chunk dentro dessa quantidade de chunks do ponto onde é executado. `status` diz em que ponto está e `stop` o encerra. O terreno é pedido ao sistema de chunks do próprio jogo, `pregenChunksInFlight` chunks por vez, um arquivo de região de 32 por 32 chunks por vez, com as regiões tomadas em anéis a partir do meio e os chunks de uma região inteira ao longo de uma curva de Hilbert, cada região concluída antes de começar a próxima, e volta iluminado e finalizado, então não há passagem de iluminação para rodar depois.

Enquanto uma execução está em andamento, todos ficam retidos: viram espectadores, mantidos no lugar, com uma linha pulsante no meio da tela e o progresso na barra de ação, o céu parado ao redor, toda criatura e máquina em toda dimensão congelada, e o tempo e o clima da dimensão que está sendo criada mantidos onde estavam. O modo em que cada jogador chegou é gravado no jogador no momento em que é retido, de modo que um save feito no meio da execução, uma queda ou uma reentrada nunca deixa ninguém preso como espectador; ao terminar, a execução devolve exatamente o modo que tirou, ou o `worldGameMode` do pacote quando houver um, sobrevivência para `hardcore`. Um cliente com o mod vê a tela com neblina enquanto retido e o logo surgindo depois; um cliente vanilla vê apenas a retenção simples. O quanto cada dimensão foi criada é salvo no mundo, então um mundo concluído nunca roda de novo. O fim é anunciado a todos antes de o backup do mundo ser feito, e uma execução iniciada pelo console ou por um bloco de comando reporta seus números de volta ali.

Em um pacote, estas entram no bloco `settings` de um [modelo de mundo](#modelos-de-mundo), como toda outra chave de `chunks`. Todas elas mostradas, com `pregenBorderLimit` como a única ausência, já que só a config a guarda:

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

### O que é feito

*pré-geração*

| Chave                         | O que faz                                                                                                                                                                                                                                                          | Por que definir                                                                      |
| ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| `pregenOnNewWorld`            | Raio em chunks criado ao redor do spawn antes que alguém jogue. 12 é o piso e 0 significa esse piso, e não nada, já que o jogo cria 12 chunks ao redor do spawn por conta própria de qualquer forma. Aumente para ir além do que o jogo faz                          | Define até onde um pacote alcança além do terreno que o jogo já cria                 |
| `pregenDimensions`            | Quais dimensões são criadas, por id, em ordem, cada uma ao redor do seu próprio spawn                                                                                                                                                                              | Adicione o Nether, o End ou as suas próprias dimensões                               |
| `pregenAllDimensions`         | Toda dimensão que o servidor possui em vez de uma lista, o overworld primeiro e as demais em ordem de id                                                                                                                                                           | Pacotes com muitas dimensões. As dimensões de todos os mods contam, então cuidado com o tamanho |
| `pregenDimensionsWhenEntered` | Estas são criadas na primeira vez que alguém pisa nelas, retendo todos de novo até terminar                                                                                                                                                                        | Dimensões que a maioria dos jogadores nunca visita; quem nunca vai não paga nada     |
| `pregenToBorder`              | Preencher cada dimensão até a borda do mundo em vez de um raio, centrado na borda                                                                                                                                                                                  | Mundos limitados                                                                     |
| `pregenBorderLimit`           | O quanto uma borda pode se estender, em chunks para cada lado, antes que a execução seja recusada. Apenas config, nunca uma chave de pacote                                                                                                                        | Uma proteção contra execuções descontroladas; aumente só sabendo o tempo e o disco que ela permite |

Execute você mesmo antes de distribuir, no raio que será distribuído, do começo ao fim. Os chunks crescem com o quadrado do raio, 63 para cada lado são dezesseis mil chunks, 500 passa de um milhão, então a pasta de regiões e o tempo real do seu mundo de teste são os números honestos para mostrar aos jogadores. Não distribua um raio que nunca foi executado.

### Como uma partida se comporta

*pré-geração*

| Chave                  | O que faz                                                                                                                                                                                                                                                                                                                                                                                         | Por que definir                                                      |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------- |
| `pregenResume`         | Uma execução parada ou interrompida continua de onde parou. A dimensão, o centro e o raio da execução são gravados no save quando ela começa, e a contagem até o momento a cada dez segundos, então uma queda, um corte de energia ou uma saída no meio da execução retomam, no carregamento seguinte, a cerca de dez segundos de onde pararam. Uma execução parada de propósito, por comando ou pelo watchdog de travamento, continua parada | Execuções longas em servidores; execuções pequenas reiniciam barato sem isto |
| `pregenChunksInFlight` | Quantos chunks a execução pede ao jogo de uma vez. Mais mantém as threads de geração mais ocupadas e o servidor menos responsivo para quem está retido assistindo                                                                                                                                                                                                                                  | Aumente em um servidor vazio, diminua em um onde há gente jogando    |

### O que os jogadores veem

*pré-geração*

| Chave                                                          | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | Por que definir                                                                                                         |
| -------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `pregenRunningSays`, `pregenFinishedSays`, `pregenStoppedSays` | As mensagens de cada etapa. A primeira pode conter `%d` para a porcentagem e, depois dele, `%s` para o nome da dimensão, ou `%1$d` e `%2$s` para pô-los em qualquer ordem. Mantidas nos padrões, elas falam o idioma de cada jogador                                                                                                                                                                                                                                                                                                                            | Reescreva-as na voz do seu pacote, nomeie a dimensão quando várias são criadas, ou silencie-as                          |
| `pregenSpectatingSays`                                         | A linha de retenção no meio da tela enquanto o terreno é criado. Mantida no padrão, fala o idioma de cada jogador; vazia, não mostra nada                                                                                                                                                                                                                                                                                                                                                                                                                      | Mantenha abaixo de cerca de trinta e cinco caracteres, ou janelas pequenas a cortam                                     |
| `pregenLogo`                                                   | Onde o logo fica quando a pré-geração termina: `left`, `center` ou `right`, acima do texto no meio da tela, exibido por alguns segundos e depois desaparecendo junto com a neblina                                                                                                                                                                                                                                                                                                                                                                             | É sempre exibido; uma palavra desconhecida é lida como `center`                                                         |
| `welcomeSays`                                                  | A saudação verde, exibida a cada login e após a pré-geração. Uma entrada simples é a linha para todos os lugares; uma entrada `dimension=message` a substitui para essa dimensão e também saúda toda chegada ali, por exemplo `"minecraft:the_nether=Welcome to the Nether!"`. A dimensão também pode ser escrita como `0`, `-1` ou `1` do 1.12.2, e ninguém que chegue enquanto o terreno está sendo criado é saudado. Uma mensagem vazia depois do `=` silencia essa dimensão; uma lista vazia não mostra nada. Mantida no padrão, fala o idioma de cada jogador | Uma linha simples dá nome ao seu pacote; adicione linhas por dimensão para dar tema a cada mundo. Mantenha as linhas abaixo de cerca de trinta e cinco caracteres |
| `saysCard`                                                     | Mostra as linhas que este mod diz, a boas-vindas, a nota de criação de terreno que um jogador que entra no meio da execução recebe e o fim da execução (o progresso em andamento continua na barra de ação), e as linhas de ameaça, como uma carta no canto inferior direito em vez de no chat. A carta desliza para dentro, permanece oito segundos e some, e aparece também sobre uma tela aberta                                                                                                                                                              | Ligue quando o chat estiver movimentado ou quando as linhas devam parecer parte do mundo, e não conversa                |
| `saysIcon`                                                     | Um item desenhado na carta, por exemplo `minecraft:compass`. Vazio não desenha nenhum                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | Dê à carta o emblema do seu pacote                                                                                      |
| `saysColor`                                                    | A cor de fundo da carta em hexadecimal, por exemplo `1E2630`. Vazio usa um cinza-ardósia escuro                                                                                                                                                                                                                                                                                                                                                                                                                                                                | Combine com a paleta do seu pacote                                                                                      |
| `saysImage`                                                    | Um PNG dos assets de cliente do pacote, por exemplo `rubyworld:textures/gui/card.png`, esticado sobre a carta como fundo e desenhado sobre a cor. Vazio não desenha nenhum                                                                                                                                                                                                                                                                                                                                                                                      | Dê à carta um painel pintado; mantenha a imagem larga e baixa, ela é esticada para o que o texto precisar               |
| `saysBackground`                                               | Desenha o painel da carta, sua borda e a faixa de cor, e o fundo escuro atrás da boas-vindas e das notas no meio da tela enquanto um jogador está retido. Desligado deixa apenas o texto, que mantém sua sombra, e `saysImage` se houver um                                                                                                                                                                                                                                                                                                                      | Deixe as linhas flutuarem sobre o mundo, ou deixe um `saysImage` pintado se sustentar sozinho                           |
| `saysFont`                                                     | A fonte em que o texto da carta é desenhado, nomeada como `namespace:name`, por exemplo `rubyworld:runes`. Vazio usa a fonte do RDPL, `resourcedatapackloader:rdpl`. O arquivo que ela nomeia é descrito em Cartas                                                                                                                                                                                                                                                                                                                                              | Dê à carta a tipografia do seu próprio pacote                                                                           |
| `toasts`                                                       | Quais dos toasts do jogo, os pop-ups no canto superior direito, são exibidos. `true` mostra todos e `false` nenhum; uma lista mostra apenas os tipos que nomeia: `advancements`, `recipes` para receitas desbloqueadas, `tutorial` para as dicas de como jogar, `system` para os avisos do próprio jogo e `other` para todo toast que os demais não cobrem, como os de outros mods. O padrão não mostra nenhum. O cliente de um jogador assume o valor quando ele entra                                                                                           | Mantenha `["advancements"]` quando seu pacote guia os jogadores com conquistas e o resto só atrapalha                   |

### Backup e redefinição do mapa

*pré-geração*

| Chave                   | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | Por que definir                                                                          |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------- |
| `pregenBackup`          | Copia o mundo para um backup intocado quando a pré-geração termina, com os jogadores ainda retidos. A geração é então paga uma só vez: uma redefinição posterior, ou um novo mundo com o mesmo pacote e a mesma seed, restaura a cópia em vez de gerar de novo, o que é muito mais rápido do que pré-gerar duas vezes. A cópia é mantida fora do save, em `rdpl-pristine/<world>` ao lado dele, para que os backups de outro mod não a varram e ela não apareça em uma pasta que eles gerenciam. Uma cópia cujos pacotes não correspondem mais aos carregados é descartada e refeita a partir do mundo em uso, então uma mudança de pacote nunca redefine para o mapa de outra pessoa | `false`                                                                                  |
| `pregenBackupSays`      | A linha no meio da tela exibida aos jogadores enquanto essa cópia é feita, com a porcentagem depois dela. Vazio não mostra nada e a cópia é feita em silêncio                                                                                                                                                                                                                                                                                                                                                                                                                                                              | `Pack requested world backup`                                                            |
| `resetSays`             | A linha no meio da tela exibida aos jogadores enquanto `/rdplserver reset` ou o fim de uma rodada recoloca o mapa. Vazio redefine em silêncio                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | `Pack requested map reset`                                                               |
| `resetSendsTo`          | Para onde os jogadores são levados por uma redefinição: `spawn`, uma posição como `x,y,z` ou `x,z`, onde a altura é um acima do nível do mar, ou qualquer uma das duas atrás de `dimension:` para enviá-los a outro mundo, a dimensão por id ou como `0`, `-1` ou `1` do 1.12.2, que é como uma redefinição deixa todos em um lobby em vez de voltar à arena                                                                                                                                                                                                                                                                  | `spawn`                                                                                  |
| `resetRuns`             | Uma função executada depois que uma redefinição limpou o mapa, nomeada `namespace:path`. É isto que constrói a arena de novo, já que um pacote que fez seu mapa a partir de uma função pode simplesmente executá-la uma segunda vez. Vazio não executa nada                                                                                                                                                                                                                                                                                                                                                                | vazio                                                                                    |
| `resetClearsEntities`   | Remove toda entidade que não é jogador. Mobs, itens soltos e experiência somem, o que deixa o mapa como começou                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           | `true`                                                                                   |
| `resetClearsScores`     | Zera todo objetivo que o pacote mantém, para que uma nova partida comece do zero. As equipes em si são mantidas                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | `true`                                                                                   |
| `resetClearsInventory`  | Esvazia o inventário de todo jogador, armadura e mão secundária incluídas, para que uma rodada comece com o que o mapa distribui e não com o que a anterior deixou. O `gives` de um lado é distribuído de novo logo em seguida                                                                                                                                                                                                                                                                                                                                                                                              | `false`                                                                                  |
| `resetClearsExperience` | Volta a experiência de todo jogador ao nível zero                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | `false`                                                                                  |
| `spawnChunkRadius`      | A que distância do ponto de spawn, em blocos, os chunks são mantidos carregados, haja ou não um jogador ali, arredondado para chunks inteiros: `(blocks + 8) / 16` para cada lado, então o padrão `128` mantém 8. Quando um mundo começa, o overworld prepara um quadrado 4 chunks mais largo para cada lado do que isso antes de o servidor ficar pronto. `0` não prepara nem mantém nenhum. O RDPL os mantém com seus próprios tickets de chunk, então no 1.21.1 a regra de jogo `spawnChunkRadius` não faz nada enquanto esta chave estiver em vigor                                                                         | Manter uma máquina ou fazenda no spawn funcionando, ou desligar os chunks de spawn com `0` |
| `spawnChunkRadii`       | Um raio para o overworld escrito como `dimension=blocks`, como em `minecraft:overworld=64`, que substitui `spawnChunkRadius`. Só o overworld tem chunks de spawn, então uma entrada para qualquer outra dimensão não muda nada                                                                                                                                                                                                                                                                                                                                                                                              | Dimensionar a área de spawn em um pacote que define seus raios por dimensão              |

---

# Modos de jogo

## Introdução do mundo

*modos de jogo*

`<namespace>/worldintro/*.json`

O nome do arquivo é você quem escolhe, só a pasta é lida. Toda introdução que um pacote distribui é executada, na ordem dos pacotes.

Mostra uma sequência de páginas quando um jogador entra no mundo, antes de assumir o controle. Texto rolando sobre uma imagem, um cartão de título, uma apresentação de slides, ou os três em sequência.

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

| Chave      | Obrigatório | Valor                                       | Padrão  | O que faz                                                                  |
| ---------- | ----------- | ------------------------------------------- | ------- | -------------------------------------------------------------------------- |
| `pages`    | sim         | lista de páginas                            | nenhum  | Exibidas em ordem. Um arquivo sem páginas é recusado com um erro           |
| `once`     | não         | booleano                                    | `false` | Executar uma vez por jogador por mundo, em vez de a cada entrada           |
| `music`    | não         | nome de evento de som                       | nenhum  | Uma faixa para a sequência toda, iniciada com a primeira página            |
| `requires` | não         | lista de ids de mods ou namespaces de pacotes | nenhum | A introdução é ignorada, a menos que todos estejam presentes               |

### Páginas

*introdução do mundo*

| Chave         | Obrigatório | Valor                       | Padrão                            | O que faz                                                                                                                                                                                                                                                  |
| ------------- | ----------- | --------------------------- | --------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `mode`        | não         | `scroll` ou `static`        | `scroll`                          | Texto que se move, ou texto que fica parado até o jogador seguir adiante                                                                                                                                                                                   |
| `text`        | não         | caminho de um arquivo `.txt` | nenhum                           | As palavras. Omita para uma página só de imagens                                                                                                                                                                                                           |
| `background`  | não         | caminho de textura          | o fundo de terra ladrilhado       | Um fundo                                                                                                                                                                                                                                                   |
| `backgrounds` | não         | lista de caminhos de textura | nenhum                           | Vários, alternados em ciclo. Soma-se a `background` se você der os dois                                                                                                                                                                                    |
| `interval`    | não         | segundos                    | `5.0`                             | Por quanto tempo cada fundo é mantido, quando há mais de um                                                                                                                                                                                                |
| `time`        | não         | segundos                    | calculado a partir do texto       | Quanto tempo uma página rolante leva, do começo ao fim. Em uma página parada, ou na última página de qualquer tipo, é o tempo até a página avançar sozinha, e sem ele elas esperam pelo botão                                                               |
| `direction`   | não         | `up` ou `down`              | `up`                              | Em que sentido o texto rolante viaja                                                                                                                                                                                                                       |
| `textScale`   | não         | número                      | `1.0`                             | Multiplica o tamanho da fonte. Uma página `static` quebra seu texto na largura da tela, menos uma margem de cada lado, e quando ainda passaria por baixo dos botões o texto é desenhado menor, até a metade, até caber                                       |
| `settle`      | não         | booleano                    | `false`                           | Terminar com a última linha centralizada em vez de sair totalmente da tela                                                                                                                                                                                 |

### Texto e tempo

*introdução do mundo*

Os arquivos de texto ficam em `assets/<namespace>/texts/*.txt`. Texto simples, um parágrafo por linha, e as linhas em branco são mantidas como linhas em branco. Um arquivo `.md` é lido do mesmo modo, e ambos os tipos aceitam a formatação abaixo. `PLAYERNAME` é trocado pelo nome do jogador, a mesma substituição que o poema do End do vanilla usa.

`time` define quanto a página dura, então a mesma página leva o mesmo tempo, tenha ela uma linha ou vinte. Ajuste a velocidade de leitura pela quantidade que você põe na página. Omita `time` e a página corre na mesma velocidade dos créditos do vanilla, onde mais texto simplesmente leva mais tempo.

### Formatação de texto

*introdução do mundo*

Os textos da introdução aceitam Markdown. Um arquivo sem marcas aparece exatamente como texto simples.

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

| Marca            | Escrito como                                              | Aparece como                                                                                                                                                                                                                                                                      |
| ---------------- | --------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Título           | `# `, `## `, `### ` no início de uma linha                | Negrito e maior: duas vezes, uma vez e meia e uma vez e um quarto o tamanho do texto, alinhado como o texto do corpo                                                                                                                                                              |
| Negrito          | `**text**`                                                | O corte em negrito da fonte                                                                                                                                                                                                                                                       |
| Itálico          | `*text*`                                                  | O corte em itálico da fonte                                                                                                                                                                                                                                                       |
| Negrito itálico  | `***text***`                                              | O corte em negrito, inclinado                                                                                                                                                                                                                                                     |
| Tachado          | `~~text~~`                                                | Riscado                                                                                                                                                                                                                                                                           |
| Código           | `` `text` ``                                              | Tingido de ciano                                                                                                                                                                                                                                                                  |
| Link             | `[text](url)`                                             | Apenas o texto, sublinhado; não clicável                                                                                                                                                                                                                                          |
| Rúnico           | `{runic}text{/runic}`                                     | O texto na cifra de runas, `resourcedatapackloader:rdpl_runic`, enquanto o resto da linha mantém sua fonte; negrito e itálico dentro dele usam os cortes em negrito e itálico da cifra. Funciona em títulos, itens de lista e citações, e um `{runic}` não fechado aparece como escrito |
| Marcador         | `- ` ou `* ` no início de uma linha                       | Um marcador, com as linhas quebradas recuadas sob o texto; dois espaços antes da marca o aninham um nível                                                                                                                                                                         |
| Numerado         | `1. ` no início de uma linha                              | O número como escrito, recuado do mesmo modo                                                                                                                                                                                                                                      |
| Citação          | `> ` no início de uma linha                               | Recuada e esmaecida                                                                                                                                                                                                                                                               |
| Linha horizontal | `---` em uma linha própria                                | Uma linha horizontal por toda a largura do texto                                                                                                                                                                                                                                  |
| Imagem           | `![alt](namespace:textures/....png)` em uma linha própria | A figura, reduzida à largura do texto e mantendo sua proporção; o texto alternativo aparece se ela não puder ser lida                                                                                                                                                             |
| Escape           | `\` antes de uma marca, por exemplo `\*`                  | A marca como um caractere comum                                                                                                                                                                                                                                                   |

Tabelas e blocos de código cercados (entre linhas ```) são desenhados como texto simples, marcas e tudo. O tempo calculado de uma página rolante e o ajuste por redução de uma página parada contam ambos a altura já diagramada, imagens incluídas. Títulos e linhas de cartas, mensagens Says e as notas de boas-vindas e de retenção aceitam as marcas em linha, do negrito ao rúnico, uma linha cada.

### Como se joga

*introdução do mundo*

Uma página rolante passa para a seguinte quando seu tempo acaba. A última página nunca avança sozinha, ela espera. Na parte de baixo ficam **Next Page** e **Skip All**, ou um único **Continue to World** na última página. Escape faz o mesmo que Skip All. As páginas estáticas centralizam todas as linhas. As páginas rolantes mantêm uma coluna fixa, como os créditos.

No modo um jogador, o mundo fica em pausa atrás da introdução, para que nada se aproxime do jogador enquanto ele lê. A única exceção é o terreno ainda sendo criado quando a introdução abre: então a criação continua atrás das páginas, e o jogador permanece retido como espectador até continuar para o mundo, mesmo que a execução termine antes. Em um servidor o mundo continua rodando, e um cliente vanilla nunca vê a introdução e entra normalmente. A saudação de boas-vindas espera até as páginas serem fechadas, para não se perder atrás delas.

`once` é lembrado nos dados salvos do jogador e sobrevive à morte. `/rdplserver intro` o limpa para quem o executa, de modo que a introdução toca de novo na próxima vez que entrar. Ela não é reexibida na hora, o que impede que seja um caminho de volta à sequência de entrada no meio de uma partida.

Os fundos são esticados para preencher a janela, então uma imagem 16:9 serve a uma janela 16:9 e uma quadrada fica achatada. Corte a imagem no formato em vez de confiar no ajuste. `music` aceita qualquer evento de som registrado, do vanilla ou um que seu próprio pacote adicione por meio de `sounds`. Ela não se repete, então uma faixa curta termina e deixa silêncio atrás de si.

Se mais de um pacote distribui uma introdução, suas páginas rodam em sequência, na ordem dos pacotes, em vez de uma vencer. Condicione-as com `requires` se quiser só uma.

## Equipes

*modos de jogo*

`<namespace>/teams/*.json`

O nome do arquivo é você quem escolhe, só a pasta é lida, e vários arquivos se acumulam. Cada arquivo é um lado.

Um lado é uma equipe de verdade no placar do próprio jogo, então `/team list` a enxerga, ela mantém seus membros através de um save e de um recarregamento, e um cliente sem este mod mostra as cores e as etiquetas de nome como em qualquer equipe vanilla. A associação é por nome, então qualquer coisa com um nome ou um UUID pode estar em um lado: um jogador, um zumbi, um aldeão, um suporte de armadura.

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

### O lado

*equipes*

| Configuração  | Tipo     | Padrão                 | O que faz                                                                                                                                                                                                                                                                      |
| ------------- | -------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`        | texto    | o nome do arquivo      | O nome da equipe no placar, de 1 a 16 caracteres. É o que `/team` e os outros arquivos usam                                                                                                                                                                                    |
| `displayName` | texto    | o nome                 | O que os jogadores veem no lugar do nome                                                                                                                                                                                                                                       |
| `color`       | texto    | `white`                | Uma das dezesseis cores de texto. Ela tinge a etiqueta de nome e é o que os slots da barra lateral por equipe usam como chave                                                                                                                                                  |
| `prefix`      | texto    | vazio                  | Colocado antes do nome de um membro, depois da cor                                                                                                                                                                                                                             |
| `suffix`      | texto    | vazio                  | Colocado depois do nome de um membro                                                                                                                                                                                                                                           |
| `scoreboard`  | booleano | `true`                 | Se o lado existe como equipe no placar do jogo. Desligado, não coloca equipe alguma em campo: seus mobs levam a cor do lado no nome, nada os impede de lutar entre si, e nenhum ponto cai nela, já que a pontuação vai pela equipe                                              |

Um lado só é colocado em campo onde um pacote pede um: sem nenhuma pasta `teams` em lugar algum, o mod não adiciona equipe, não escuta nada e não oferece o comando. Um operador de servidor que edita um arquivo pode executar `/rdplserver reload` para pôr a mudança no mundo em execução sem reiniciar.

### Combate e visibilidade

*equipes*

| Configuração            | Tipo     | Padrão         | O que faz                                                                                                                                                                                      |
| ----------------------- | -------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `friendlyFire`          | booleano | `false`        | Se os membros podem se machucar. Também é o padrão de `mobFriendlyFire`                                                                                                                        |
| `mobFriendlyFire`       | booleano | `friendlyFire` | Se os mobs de um lado podem ferir o próprio lado com explosões e TNT arremessado, o que o jogo sozinho nunca impede. Desligado poupa o lado; ligado deixa como o jogo tem                       |
| `seeFriendlyInvisibles` | booleano | `true`         | Se os membros se enxergam enquanto invisíveis                                                                                                                                                  |
| `nameTags`              | texto    | `always`       | `always`, `never`, `hideForOtherTeams` ou `hideForOwnTeam`, em qualquer caixa de letras                                                                                                        |
| `deathMessages`         | texto    | `always`       | As mesmas quatro palavras, para quem é avisado quando um membro morre                                                                                                                          |
| `collision`             | texto    | `always`       | `always`, `never`, `pushOtherTeams` ou `pushOwnTeam`, em qualquer caixa de letras                                                                                                              |

### Quem entra

*equipes*

| Configuração | Tipo     | Padrão  | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| ------------ | -------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `entities`   | lista    | vazio   | Ids de entidade cujo spawn, todo ele, entra neste lado, como `minecraft:zombie` ou uma das suas                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `players`    | lista    | vazio   | Nomes de jogadores que entram neste lado ao fazer login                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `spawnBox`   | lista    | nenhum  | Seis números inteiros, x y z até x y z. Tudo que nascer dentro entra, e os cantos podem ser dados em qualquer ordem                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `joinable`   | booleano | `true`  | Se um jogador pode entrar com `/rdplserver team join`. Defina como false para um lado que é só de mobs                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `balance`    | booleano | `false` | Se `/rdplserver team join` sem nome pode colocar um jogador aqui. Entre os lados que o permitem, é escolhido o que tem menos jogadores                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `picks`      | número   | `0`     | Quantos membros este lado sorteia ao acaso. A cada rodada aberta, o lado deixa seu último sorteado voltar para onde estava e sorteia de novo a partir de tudo que `picksFrom` nomeia; entre sorteios, um login ou um spawn desse conjunto preenche de imediato uma vaga vazia. Um jogador entre todos, em um lado só dele, é para isso que serve                                                                                                                                                                                                                                                                                                                                                                                                   |
| `picksFrom`  | lista    | vazio   | De onde o sorteio é feito: `players` para todos os que estão online, e ids de entidade para todo mob vivo desse tipo                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `standIn`    | objeto   | nenhum  | Um mob que segura o lado enquanto nenhum jogador está nele: `{ "entity": "mypack:herobrine", "at": "23,31,0" }` mantém uma entidade desse tipo viva naquele ponto do overworld, invocando-a quando falta, e a remove no instante em que um jogador entra no lado, de modo que um jogo é disputado contra a IA até um jogador assumir o papel. Verificado a cada cinco segundos; o ponto deve estar em terreno carregado. Em um jogo com lobby (`opens.by: leader`), um substituto só é invocado enquanto o lobby espera e quando a rodada abre, então um que cai continua ausente pelo resto da rodada e por seu fim, até todos voltarem ao lobby; sem lobby, um substituto caído não é reposto enquanto roda uma rodada que termina em `ends.lastStanding` |

Há três modos de entrar, e um lado pode usar todos. `entities` nomeia ids de entidade, e tudo desse tipo entra ao nascer, que é como um pacote dá lados aos mobs sem mexer neles. `spawnBox` reivindica um canto do mundo, e tudo que nasce dentro entra, o que serve a uma arena onde os dois lados usam o mesmo mob. `players` nomeia jogadores diretamente. Além disso, um jogador pode entrar com `/rdplserver team join <name>`, a menos que o lado defina `joinable` como false, e sair com `/rdplserver team leave`.

### Kit inicial e spawn

*equipes*

| Configuração | Tipo  | Padrão | O que faz                                                                                                                                                                                                                                                                                                                                   |
| ------------ | ----- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `gives`      | lista | vazio  | Itens colocados no inventário de um jogador quando ele entra no lado, um nome de item para um ou `{ "item", "count", "unbreakable" }` para mais, ou para um que nunca se desgasta, em qualquer slot livre e jogados aos seus pés quando não há nenhum. Distribuídos de novo após uma redefinição que limpa inventários (`resetClearsInventory`) |
| `spawn`      | texto | nenhum | `x,y,z` no overworld onde os jogadores do lado são colocados quando uma rodada abre, para que cada lado comece em seu próprio terreno; sem isso eles ficam onde a redefinição ou o lobby os deixou                                                                                                                                          |

### O líder

*equipes*

| Configuração | Tipo  | Padrão                             | O que faz                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| ------------ | ----- | ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `lead`       | texto | `none`                             | Como o líder do lado é escolhido: `none`; `first` para quem entrou no lado primeiro entre os que estão online, de modo que passa pela ordem de entrada enquanto um está ausente e volta com ele; eles são avisados ao chegar, depois da introdução e de qualquer retenção, e de novo quando passa a eles; `topScore` para quem tem a maior pontuação no objetivo que `leadOn` nomeia; `appointed` para o jogador que `leadIs` nomeia; `vote` para quem os membros votam; ou `claim` para quem o reivindica primeiro. Um líder é um rótulo e uma cor e nada mais: não concede poder algum, então um líder que sai não quebra nada |
| `leadOn`     | texto | vazio                              | Com `topScore`, o objetivo pelo qual os membros são classificados. É recalculado a cada vez que é lido, então acompanha a pontuação                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `leadIs`     | texto | vazio                              | Com `appointed`, o jogador que lidera                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `leadSays`   | texto | `You are the current round leader` | Dito a um jogador quando a liderança chega a ele: ao chegar a um lado que lidera, ao reivindicá-la, ou quando um líder `first` passa a ele, caso em que informa quem saiu. `{side}` é o nome de exibição do lado; vazio não diz nada                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `leadRuns`   | texto | vazio                              | Uma função, `namespace:path`, executada uma vez cada vez que a liderança passa a um jogador: o primeiro líder e cada repasse depois. Ela roda como o líder, em sua posição, com a permissão que uma função concedida por conquista tem, então `@s` é o líder. Verificada a cada segundo; um líder que está offline é executado quando estiver online de novo. Uma reinicialização decide o líder de novo                                                                                                                                                                                                                                                       |

## Pontuação

*modos de jogo*

`<namespace>/scoring/*.json`

O nome do arquivo é você quem escolhe, só a pasta é lida, e vários arquivos se acumulam. Cada arquivo é um objetivo.

Um objetivo é um objetivo de verdade no placar do próprio jogo, então `/scoreboard players list` o lê e ele mantém suas pontuações através de um save. `criterion` é o que o jogo conta por conta própria: `dummy` para uma pontuação que só este pacote move, ou `deathCount`, `playerKillCount`, `totalKillCount`, `health`, `air`, `armor`, `food`, `level`, `xp`, `trigger`, ou qualquer estatística escrita do modo que `/scoreboard` aceita, como `minecraft.custom:minecraft.jump`. Uma estatística do 1.12.2 como `stat.jump` é lida como aquela em que se transformou.

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

### O objetivo

*pontuação*

| Configuração  | Tipo     | Padrão                | O que faz                                                                                                                                                          |
| ------------- | -------- | --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`        | texto    | o nome do arquivo     | O nome do objetivo no placar, de 1 a 16 caracteres                                                                                                                 |
| `displayName` | texto    | o nome                | O que os jogadores veem no lugar do nome                                                                                                                           |
| `criterion`   | texto    | `dummy`               | O que o jogo conta por conta própria. Um desconhecido é recusado com uma linha informando isso                                                                     |
| `display`     | texto    | vazio                 | `sidebar`, `list`, `belowName` (`below_name` também é aceito) ou `sidebar.team.<color>`. Vazio não o mostra em lugar nenhum; não há tela de placar para abrir      |
| `render`      | texto    | o próprio do critério | `integer` ou `hearts`                                                                                                                                              |
| `teamTotals`  | booleano | `true`                | Os pontos caem em uma linha com o nome da equipe do membro                                                                                                         |
| `individuals` | booleano | `false`               | Os pontos também caem em uma linha para o próprio membro                                                                                                           |
| `carries`     | booleano | `false`               | O objetivo sobrevive a uma redefinição do mapa em vez de ser apagado com ele. Um placar de vitórias por rodada de uma partida é um exemplo                         |
| `awardsTo`    | texto    | vazio                 | Outro objetivo a quem este entrega um ponto quando termina, ao lado que liderou. A classificação por nível não entrega nada                                        |
| `tiebreak` | booleano | `false` | Uma rodada que termina empatada no topo sorteia um dos lados empatados com o acaso do mundo, registra o sorteio e o premia como de costume. Uma partida, um objetivo sem `awardsTo`, sorteia do mesmo jeito e nomeia o lado sorteado no topo dos resultados |

### Pontos

*pontuação*

| Configuração     | Tipo   | Padrão | O que faz |
| --- | --- | --- | --- |
| `points.kill`    | object | vazio  | Id da entidade para pontos, creditados ao lado de quem matou. `minecraft:player` pontua a morte de um jogador |
| `points.death`   | int    | `0`    | Pontos sempre que um membro morre, seja qual for a causa. Pode ser negativo |
| `points.ownKill` | int    | `0`    | Pontos por matar alguém do próprio lado de quem matou, no lugar do valor de `kill`. 0 não pontua nada por isso; um número negativo é uma penalidade |

`points` é o que este mod acrescenta ao que o jogo já conta, alimentado no mesmo objetivo para que o `/scoreboard` continue lendo tudo. `kill` vale tantos pontos por id de entidade morta, creditados ao lado de quem matou; `death` vale tantos pontos sempre que um membro de um lado morre, e pode ser negativo. Com `teamTotals`, os pontos vão para uma linha com o nome da equipe, o que permite à barra lateral mostrar quatro lados em vez de uma linha para cada mob. `individuals` acrescenta também uma linha por membro e vem desativado por padrão, porque uma linha por UUID de mob vira só ruído.

### Como uma rodada termina

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `ends.atScore`      | int     | `0`                                | A partida termina no instante em que um lado atinge esse valor. 0 nunca termina por pontuação |
| `ends.afterMinutes` | int     | `0`                                | A partida termina depois desta quantidade de minutos. 0 nunca termina por tempo |
| `ends.afterRounds`  | int     | `0`                                | Para um objetivo que outro `awardsTo`: a partida termina quando esta quantidade de rodadas tiver sido concedida no total, não importa quem as levou. 0 nunca termina por rodadas |
| `ends.lastStanding` | boolean | `false`                            | A rodada termina quando resta apenas um lado de pé. Os lados em jogo são os que têm um jogador ou um mob vivo quando a rodada abre, no mínimo dois; um jogador que morre está fora e volta como espectador até a rodada acabar, e um lado cujos jogadores estão todos fora ou ausentes e cujos mobs estão todos mortos caiu. O lado que resta de pé leva a rodada, e `awardsTo` a registra para esse lado, seja qual for a pontuação. Com `resets` e `opens.by: leader`, o jogo volta então ao lobby. O `standIn` de um lado não é invocado de novo enquanto uma rodada assim estiver em andamento |
| `ends.outSays`      | text    | `You are out until the round ends` | O que é dito ao jogador eliminado. Vazio não diz nada |
| `ends.locksTeams`   | boolean | `true`                             | Entrar em um lado enquanto uma rodada está em andamento só vale depois que ela acabar, para que ninguém caia no meio de uma rodada já pontuada |

`ends` encerra a partida, seja no instante em que um lado atinge `atScore`, seja depois de passados `afterMinutes`. A classificação é então exibida, ordenada pelo próprio jogo: como chat, ou como carta se `results` pedir uma. Um jogador sem este mod recebe a mesma classificação em linhas de chat, para que ninguém fique sem resultado. Com `resets`, esse fim é o de uma rodada: a classificação permanece por `intermissionSeconds` enquanto uma contagem regressiva aparece na barra de ação, o mapa é redefinido para o boas-vindas e a próxima rodada abre após uma contagem de cinco segundos. `awardsTo` entrega a rodada ao lado que liderava, em um objetivo que `carries` atravessa a redefinição. Um objetivo carregado pode terminar sozinho -- `atScore` para uma melhor de N, `afterRounds` para um número fixo -- e sua classificação é zerada na redefinição seguinte, de modo que uma nova partida se abre.

### Entre rodadas

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `ends.resets`              | boolean | `false`                       | Terminar a rodada redefine o mapa, como descrevem `resetSays` e as outras configurações de redefinição em [Modelos de mundo](#modelos-de-mundo), e então uma nova rodada abre |
| `ends.intermissionSeconds` | int     | `10`                          | Por quanto tempo a classificação permanece entre o fim e a redefinição |
| `ends.intermissionSays`    | text    | `Round cooldown {seconds}`    | Exibido na barra de ação a cada segundo do intervalo depois que uma rodada termina, com `{seconds}` em contagem regressiva até a redefinição. Vazio não exibe nada |
| `ends.startsSays`          | text    | `Round starting in {seconds}` | Exibido na barra de ação durante a contagem de cinco segundos que abre a próxima rodada após a redefinição, com `{seconds}` em contagem regressiva. Vazio não exibe nada |

### O lobby

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `opens.by`         | text    | `auto`                                             | `auto` abre a próxima rodada sozinho, cinco segundos após a redefinição. `leader` mantém o jogo em um lobby: após a redefinição, e quando o mundo carrega pela primeira vez, nada é pontuado e nenhum relógio corre, os lados podem ser livremente escolhidos e deixados, e a rodada só abre quando o líder de um lado, ou um operador, executa `/rdplserver round start`, e não enquanto alguém ainda estiver lendo a introdução do mundo; então corre a contagem de cinco segundos, os sorteios são feitos e cada lado é colocado em seu `spawn`. Enquanto o mundo espera, até o fim da contagem de cinco segundos, os jogadores permanecem onde estão e não podem quebrar, colocar, usar, bater nem largar nada e não sofrem dano, sendo mostrada a eles a linha de espera quando tentam, e todo o resto que está vivo fica parado: sem IA, sem movimento. Os comandos continuam funcionando, então é possível entrar nos lados e iniciar a rodada |
| `opens.says`       | text    | `Waiting for {leader} to start the round`          | Exibido no meio da tela, como o boas-vindas, a cada jogador que não é líder: quando chega ao lobby depois da introdução, assim que o boas-vindas foi exibido; quando o lobby abre de novo depois de uma rodada; sempre que ele muda, quando um líder chega ou sai; e quando o jogador tenta algo que o lobby recusa. `{leader}` são os líderes de todos os lados, ou `a leader` enquanto ninguém lidera. Vazio não exibe nada |
| `opens.leaderSays` | text    | `Type /rdpl round start`                           | Exibido da mesma forma e nos mesmos momentos a um jogador que lidera um lado, no lugar de `opens.says`. Vazio não exibe nada |
| `opens.lobby`      | text    | nenhum                                             | `x,y,z` no overworld, ou `dimension:x,y,z` em outro mundo, como `minecraft:the_nether:0,64,0`, onde todos esperam enquanto o lobby se mantém: cada jogador, e cada mob vivo de um lado, é posto em um anel ao redor desse ponto, cada um voltado para o centro, de modo que ficam se encarando. Cada um recebe um arco tão largo quanto ele mais dois blocos, para que nenhum se sobreponha a outro, e o anel cresce conforme mais chegam; ele é refeito sempre que alguém entra ou sai. A altura é o chão em que ficam, encontrado dentro de três blocos para cima ou para baixo. Jogadores e mobs passam para esse mundo e voltam diretamente, sem portal construído. Quando a rodada abre, os jogadores vão para o `spawn` de seu lado, e um mob que ainda está de pé é devolvido ao lugar onde estava, em seu próprio mundo |
| `opens.lobbyJoins` | boolean | `false`                                            | Coloca no lobby, como espectador, o jogador que entra no meio de uma rodada até ela terminar, em vez de onde ele saiu. Requer `opens.lobby` |
| `opens.joinsSays`  | text    | `Round is in progress, you can join after it ends` | O que é dito a ele. Vazio não diz nada |

### Redefinindo uma rodada

*pontuação*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `reset.lead`            | text | `none`                                                                                    | O que `/rdplserver round reset` faz pelo líder de um lado enquanto uma rodada corre. `now` encerra a rodada na hora e redefine o mapa; `vote` convoca uma votação; `none` não dá ao líder poder próprio, de modo que ele convoca uma votação como qualquer outro jogador onde `players` permitir. Um operador sempre redefine na hora |
| `reset.players`         | text | `none`                                                                                    | `vote` permite que um jogador de qualquer lado convoque uma votação com `/rdplserver round reset`. `none` deixa a redefinição para o líder |
| `reset.teams`           | list | vazio                                                                                     | Os lados cujos jogadores podem convocar uma votação. Vazio é todos os lados |
| `reset.passPercent`     | int  | `51`                                                                                      | A parcela de votantes, de 1 a 100, que precisa votar sim para a rodada ser redefinida. `51` é mais da metade, `100` é todos |
| `reset.voteSeconds`     | int  | `30`                                                                                      | Quanto tempo uma votação dura, no mínimo cinco segundos. Ela fecha mais cedo assim que o resultado é certo |
| `reset.cooldownSeconds` | int  | `60`                                                                                      | Quanto tempo depois de uma votação fracassada antes que outra possa ser convocada. Um líder com `now` não é barrado por isso |
| `reset.leadSays`        | text | `{player} reset the round`                                                                | Dito a todos quando a rodada é redefinida na hora, sendo `{player}` quem a redefiniu. Vazio não diz nada |
| `reset.voteSays`        | text | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | Dito a todos quando uma votação é convocada, sendo `{player}` quem a convocou. Vazio não diz nada |
| `reset.tallySays`       | text | `Reset the round? {yes} yes, {no} no, {seconds}`                                          | Exibido na barra de ação a cada segundo de uma votação, com `{seconds}` em contagem regressiva. Vazio não exibe nada |
| `reset.passSays`        | text | `The vote passed, so the round is reset`                                                  | Dito a todos quando uma votação passa. Vazio não diz nada |
| `reset.failSays`        | text | `The vote failed, so the round goes on`                                                   | Dito a todos quando uma votação fracassa. Vazio não diz nada |

Uma redefinição interrompe a rodada onde ela está. A classificação é exibida sob `The round was reset`, ninguém recebe a rodada, o intervalo faz a contagem regressiva e o mapa é redefinido como se a rodada tivesse terminado com `ends.resets`, voltando ao lobby onde `opens.by` é `leader`. Funciona quer a rodada fosse ou não terminar sozinha, mas não no lobby, durante a contagem que abre uma rodada, nem quando a rodada já acabou e sua redefinição está a caminho; uma votação ainda em andamento nesse momento é descartada.

Todo jogador online em um lado vota, seja qual for o lado, com `/rdplserver round vote yes` ou `no`, e pode mudar o voto enquanto a votação corre. Quem convoca a votação já votou sim, e um jogador que não votou quando o tempo acaba conta como não. Em um pacote com lados, um jogador sem lado não convoca nem vota; em um pacote sem lados, todo jogador online o faz. O primeiro arquivo de pontuação cujo `reset` permite que alguém redefina é o usado.

### Resultados

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `results.card`       | boolean | `false`                | Exibe a classificação como uma carta em vez de chat |
| `results.title`      | text    | o nome e `results`     | O título da carta |
| `results.icon`       | text    | vazio                  | Um item desenhado na carta, p. ex. `minecraft:tnt` |
| `results.image`      | text    | vazio                  | Uma imagem desenhada na carta no lugar de um item |
| `results.background` | text    | uma ardósia escura      | A cor de fundo da carta |
| `results.seconds`    | int     | `8`                    | Quanto tempo a carta permanece, no mínimo um segundo |

### Turnos

*pontuação*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `turns.order` | texto | `fixed` | Quem joga quando, definido no início de cada ciclo. `fixed` mantém a ordem em que os lados foram vistos pela primeira vez, `random` embaralha a cada ciclo, `lowestFirst` começa pela menor pontuação neste objetivo e `lastWinner` começa pelo lado que venceu a última rodada, o resto na ordem fixa |
| `turns.seconds` | int | `60` | Quanto dura um turno, no mínimo um segundo |
| `turns.gapSeconds` | int | `3` | Uma pausa entre turnos, enquanto todos os lados esperam. 0 segue direto |
| `turns.held` | texto | `frozen` | Como um jogador espera a sua vez: `frozen` o deixa parado e o impede de bater, cavar, construir, usar ou largar, como faz o lobby; `spectator` ou `adventure` o põe nesse modo de jogo até a sua vez e depois devolve o dele |
| `turns.endsAtScore` | int | `0` | Os turnos acabam, e a rodada com eles, assim que um lado chega a esta pontuação no objetivo. 0 nunca acaba por pontuação |
| `turns.cycles` | int | `0` | Os turnos acabam, e a rodada com eles, depois deste número de ciclos, sendo um ciclo um turno para cada lado em jogo. 0 segue até que outra coisa encerre a rodada |
| `turns.mobTags` | lista | vazio | Tags de placar que trazem para os turnos um mob sem time, um grupo por tag |
| `turns.mobTypes` | lista | vazio | IDs de entidade que trazem para os turnos um mob sem time, um grupo por ID |

`turns` faz os lados jogarem por turnos enquanto uma rodada corre. Cada time é um lado; sem times, cada jogador é um lado próprio, e um mob de um time joga com ele. Enquanto um lado tem a vez, todos os outros esperam: os jogadores como `held` manda, os mobs congelados do jeito que o lobby os segura. Um lado sem ninguém em jogo, ou com todos os jogadores eliminados, é pulado.

Cada turno é anunciado no chat, e o relógio dele conta na barra de ação. O lado da vez é avisado no chat com um som quando faltam 10 segundos e de novo aos 3. `/rdplserver game pass`, ou um clique direito com um item que tenha `passesTurn`, encerra um turno mais cedo: um jogador só passa a vez do próprio lado, e um bloco de comando ou o console passa a de quem estiver jogando.

Acabar por `endsAtScore` ou `cycles` encerra a rodada como qualquer outro fim: a classificação aparece, `awardsTo` e `tiebreak` valem, e `ends.resets` reinicia o mapa. Só o primeiro arquivo de pontuação com `turns` joga por turnos. Os textos são as chaves `turn`, `turnclock`, `turnwarn`, `turnout`, `turnpass`, `turngap`, `notturn` e `noturns`, que o `says` de um arquivo de dados pode mudar.

## Invasões

*modos de jogo*

`<namespace>/raids/*.json`

O nome do arquivo é de sua escolha, apenas a pasta é lida, e vários arquivos se acumulam. Cada arquivo é uma invasão.

Uma invasão é do tipo que o jogo tem desde a 1.14, travada em uma das vilas do próprio jogo. Ela começa quando um jogador com o efeito `omen` está dentro de uma vila: o efeito é retirado, e uma barra de chefe aparece para todo jogador a até `reach` do centro da vila. Após `waveDelay` ticks, a primeira onda chega em um anel ao redor da vila e caminha em direção ao centro, atacando jogadores, aldeões e golens de ferro pelo caminho. Os invasores nunca ferem nem escolhem como alvo uns aos outros, de modo que uma flecha ou golpe perdido entre dois deles não faz nada. A barra mostra a vida que resta à onda e passa a contar os invasores quando restam dois ou menos. Quando uma onda acaba, a seguinte espera `waveDelay` ticks. Quando a última onda acaba e nada volta por dois segundos, a invasão é vencida; quando todos os aldeões morrem ou a própria vila desaparece depois que uma onda chegou, ela é perdida. De qualquer forma, a barra informa isso por trinta segundos, e a função correspondente é executada como cada jogador ao alcance.

Uma invasão em andamento é salva com o mundo, e seus invasores retomam a marcha após um recarregamento. Ela termina sem desfecho em modo pacífico, após `timeout` ticks, ou quando nenhum ponto ao redor da vila comporta uma onda. Uma vila é onde o jogo guarda seus pontos de vila, as camas, blocos de trabalho e sinos que os aldeões reivindicam: seu centro é o meio desses pontos, ela alcança ao menos 32 blocos ao redor, e seus aldeões são os que estão dentro desse alcance e a quatro blocos da altura do centro. O `minecraft:bad_omen` do próprio jogo inicia primeiro a invasão do próprio jogo, de modo que uma invasão nomeia um efeito do próprio pacote.

Enquanto uma onda está sobre a vila, seus aldeões correm para casa e ficam lá, como fazem quando o sino do jogo toca. Os invasores derrubam as portas de madeira no caminho para chegar até eles, doze segundos por porta, nas dificuldades normal e difícil enquanto `mobGriefing` está ativado; portas de ferro resistem. Um bloco do tipo `bell` é um sino de vila onde quer que esteja, e toca como descreve [Sinos](#sinos); o `bell` da invasão nomeia qualquer outro bloco que toque como um. Todo sino da vila toca quando uma onda chega, e um bloco nomeado também toca quando um jogador o usa: aldeões a até 48 blocos se escondem por quinze segundos e invasores a até 48 blocos brilham por três. O `minecraft:bell` já existe nas vilas do jogo e pode ser nomeado, e continua tocando também à maneira do jogo; o bloco de sino próprio de um pacote é colocado na vila por meio de uma estrutura NBT, como um terreno ou como a peça central da praça.

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

### A invasão

*invasões*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `omen`          | nome de efeito       | nenhum, obrigatório | O efeito que inicia a invasão quando quem o tem está dentro de uma vila. Qualquer efeito registrado serve, inclusive uma poção do próprio pacote |
| `name`          | text                 | `Raid`              | O título da barra de chefe |
| `color`         | text                 | `red`               | A cor da barra: `pink`, `blue`, `red`, `green`, `yellow`, `purple` ou `white` |
| `waves`         | lista de ondas       | nenhum, obrigatório | Cada onda é uma lista de grupos, e as ondas vêm em ordem |
| `waveDelay`     | int                  | `300`               | Ticks antes da primeira onda, e entre o fim de uma onda e a seguinte |
| `spawnDistance` | int                  | `32`                | A que distância do centro da vila uma onda chega. As primeiras tentativas são ao dobro disso, depois a essa distância, depois dentro da vila |
| `reach`         | int                  | `96`                | Jogadores a até esta quantidade de blocos do centro veem a barra, e a função final é executada como eles. Um invasor que se afasta dezesseis blocos além disso deixa a invasão |
| `timeout`       | int                  | `48000`             | Ticks após os quais uma invasão inacabada termina sem desfecho. `0` nunca a encerra |
| `sound`         | nome de som          | nenhum              | Tocado a cada jogador ao alcance, do lado de onde vem a onda, quando cada onda chega |
| `wins`          | função               | nenhuma             | Executada como cada jogador ao alcance quando a invasão é vencida |
| `loses`         | função               | nenhuma             | Executada como cada jogador ao alcance quando a invasão é perdida |
| `bell`          | nome de bloco ou lista | nenhum            | Outros blocos que tocam como um sino, quando um jogador os usa e sempre que uma onda chega. Um bloco do tipo `bell` toca sem precisar ser nomeado |

### Um grupo

*invasões*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `entity` | nome de entidade          | nenhum, obrigatório | O que vem. Uma variante de entidade mantém todo o seu próprio comportamento e ganha a marcha |
| `count`  | int ou `{ "min", "max" }` | `1`                 | Quantos vêm |

---

## Cartas

*modos de jogo*

`<namespace>/cards/*.json`

O nome do arquivo é de sua escolha, apenas a pasta é lida, e vários arquivos se acumulam. Cada arquivo é uma regra, e seu id é `<namespace>:<nome do arquivo>`. Uma regra espera por um gatilho, verifica seu `when` e mostra uma carta ao seu público; também pode executar uma função. Nada precisa estar no cliente: um jogador sem o mod recebe uma carta de canto como linhas de chat e uma carta central como título.

Toda mensagem que este mod diz por conta própria é uma regra integrada, listada abaixo. Um pacote altera uma delas escrevendo um arquivo com esse id, `rdpl/cards/<nome>.json`, que não precisa de gatilho: o que ele deixar de fora permanece como está hoje, e `{text}` representa a mensagem que o mod teria dito. Um pacote que não escreve nenhuma delas vê todas as mensagens como antes.

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

O segundo arquivo, salvo como `rdpl/cards/gate_blocked.json`, transforma a linha vermelha da barra de ação que um portal fechado mostra em uma carta com um ícone e uma segunda linha, e a mostra no máximo uma vez a cada trinta segundos.

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

O terceiro recebe um jogador na primeira entrada com uma carta central sem painel atrás, apenas o texto e a sombra do texto, desenhados na fonte do próprio pacote.

### Gatilhos

*cartas*

| Gatilho | Requer | Dispara quando |
| --- | --- | --- |
| `command`         | nada               | `/rdplserver card <rule> [players]` é executado. O comando ignora `when`, `repeat` e `cooldown`, e ainda executa `runs`. Qualquer regra pode ser mostrada assim, seja qual for o seu gatilho |
| `first_join`      | nada               | Um jogador entra no mundo pela primeira vez |
| `dimension_enter` | `dimension`        | Um jogador chega a essa dimensão |
| `biome_enter`     | `biomes`           | Um jogador entra em um desses biomas vindo de outro lugar |
| `structure_enter` | `structures`       | Um jogador entra em uma dessas estruturas vindo de fora dela |
| `advancement`     | `advancement`      | Um jogador conquista esse avanço |
| `time_of_day`     | `time`             | O relógio do dia passa por esse tick, `0` a `23999`, enquanto há jogadores na dimensão. Um relógio ajustado por comando ou por uma cama não conta |
| `day`             | nada, ou `day`     | Um novo dia começa na dimensão; com `day`, apenas esse dia |
| `craft`           | `item`             | Um jogador cria esse item |
| `pickup`          | `item`             | Um jogador pega esse item |
| `kill`            | `entity`           | Um jogador mata essa entidade, ou a `count`-ésima dela |
| `respawn`         | nada               | Um jogador renasce após morrer |
| `death`           | nada               | Um jogador morre |
| `y_level`         | `below` ou `above` | Um jogador desce abaixo ou sobe acima dessa altura |
| `play_time`       | `minutes`          | O tempo de um jogador no mundo chega a essa quantidade de minutos, contados a partir de quando a introdução do mundo se fecha, ou da entrada quando nenhuma introdução é mostrada a ele |
| `score`           | `objective`        | A pontuação de um jogador nesse objetivo chega a `score` |

Bioma, estrutura, altura, tempo de jogo e pontuação são verificados uma vez por segundo para cada jogador, e disparam na passagem de fora para dentro, nunca na primeira verificação após uma entrada. Uma regra `time_of_day` ou `day` cujo público não é `player` dispara uma vez para a dimensão, em vez de uma vez para cada jogador nela.

Uma carta que dispara enquanto um jogador ainda tem a introdução do mundo aberta espera e é mostrada quando a introdução se fecha, seja qual for o gatilho, inclusive o `command`. Ela é descartada se o jogador sair antes disso.

### Configurações de gatilhos

*cartas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `trigger`            | text                               | nenhum, obrigatório | Um dos gatilhos acima. Uma regra integrada não leva nenhum |
| `dimension`          | text                               | nenhum         | Um id de dimensão como `minecraft:the_nether`; um id sem namespace é lido como `minecraft:`. Para `dimension_enter` é aquela em que se entra; para todos os outros gatilhos limita a regra a jogadores nessa dimensão |
| `biomes`             | list                               | nenhum         | Ids de bioma como `minecraft:desert`, ou `#tag` para uma tag de bioma como `#minecraft:is_ocean` |
| `structures`         | list                               | nenhum         | Ids de estrutura como `minecraft:village_plains`, ou `#tag` para uma tag de estrutura como `#minecraft:village`, que contam enquanto o jogador está dentro de uma peça de uma delas; ou o nome de uma estrutura que um pacote coloca por meio de `structures`, que conta dentro de `radius` de onde foi colocada |
| `radius`             | int                                | `32`           | A que distância conta como dentro de uma estrutura própria de um pacote |
| `advancement`        | text                               | nenhum         | O id do avanço |
| `item`               | text                               | nenhum         | O item, escrito como em outros pontos de um pacote, como `minecraft:diamond_sword` |
| `entity`             | text                               | nenhum         | O id da entidade, como `minecraft:zombie` |
| `count`              | int                                | `1`            | Para `kill`: quantas mortes são necessárias. A contagem recomeça depois que a regra dispara |
| `below`, `above`     | int                                | nenhum         | Para `y_level`: a altura abaixo ou acima da qual se vai |
| `time`               | int                                | `0`            | Para `time_of_day`: o tick do dia |
| `day`                | int                                | nenhum         | Para `day`: o único dia em que disparar. Sem ele, todos os dias |
| `minutes`            | int                                | nenhum         | Para `play_time` |
| `objective`, `score` | text, int                          | nenhum, `1`    | Para `score`: o objetivo e o valor a atingir |
| `requires`           | lista de ids de mod ou namespaces de pacote | nenhum | O arquivo é ignorado, a menos que todos estejam presentes |

### Quando

*cartas*

`when` contém condições que devem ser todas verdadeiras no momento em que o gatilho dispara.

| Configuração | Tipo | O que verifica |
| --- | --- | --- |
| `biomes`                    | list      | O jogador está em um desses biomas, escritos como no gatilho |
| `timeFrom`, `timeTo`        | int       | O relógio do dia está dentro desta janela, que pode passar da meia-noite, como `13000` a `1000` |
| `dayAtLeast`                | int       | O número do dia é pelo menos este |
| `advancement`               | text      | O jogador tem este avanço |
| `gameMode`                  | text      | O jogador está neste modo de jogo: `survival`, `creative`, `adventure` ou `spectator` |
| `team`                      | text      | O jogador está nesta equipe do placar |
| `objective`, `scoreAtLeast` | text, int | A pontuação do jogador no objetivo é pelo menos esta |

### A carta

*cartas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `title`      | text    | nenhum           | A primeira linha, desenhada maior em uma carta central |
| `lines`      | list    | nenhum           | Até dezesseis linhas. Uma regra precisa de um título ou de linhas, exceto uma integrada. `{player}`, `{dim}`, `{biome}` e `{day}` são preenchidos; `{text}` é a mensagem integrada e, em uma linha própria, fornece todas as linhas dela |
| `style`      | text    | `corner`         | `corner` é a carta no canto inferior direito que `saysCard` mostra; `center` é uma carta no meio da tela; `chat` são linhas de chat; `bar` é a barra de ação |
| `icon`       | text    | `saysIcon`       | Um item desenhado em uma carta de canto. Vazio não desenha nenhum |
| `color`      | text    | `saysColor`      | A cor de fundo da carta em hexadecimal |
| `image`      | text    | `saysImage`      | Um PNG dos recursos de cliente do pacote, esticado sobre a carta como fundo |
| `background` | boolean | `saysBackground` | `false` remove o painel, sua borda e a faixa de cor; o texto mantém a sombra, e uma `image` ainda é desenhada |
| `font`       | text    | `saysFont`       | A fonte em que o texto da carta é desenhado, como `namespace:name`. Vazio usa a fonte RDPL |
| `ticks`      | int     | `160`            | Quanto tempo a carta permanece, incluindo o esmaecimento |
| `audience`   | text    | `player`         | Quem a vê: `player`, `everyone`, `dimension` (todos na dimensão do jogador) ou `team` (a equipe do placar do jogador) |
| `repeat`     | text    | `always`         | `always`, `once_per_player`, `once_per_world` ou `once_per_session` (de novo depois que o jogador entra novamente) |
| `cooldown`   | int     | `0`              | Segundos antes que a regra dispare de novo para o mesmo jogador |
| `runs`       | text    | nenhum           | Uma função executada como o jogador quando a regra dispara |

Uma carta de canto vai para o chat quando `saysCard` está desativado. O que um jogador já viu é guardado com o jogador, de modo que sobrevive à morte e à passagem entre dimensões; `once_per_world` é guardado com o mundo.

A fonte RDPL, `resourcedatapackloader:rdpl`, é a padrão para todo texto: cartas, mensagens Says, as notas de boas-vindas e de retenção, a introdução do mundo e os menus, o chat, o HUD e as dicas do próprio jogo. Seus cortes em negrito e itálico são `resourcedatapackloader:rdpl_bold` e `resourcedatapackloader:rdpl_italic`. As letras da mesa de encantamento continuam as do jogo.

O RDPL traz estas fontes e caracteres. O `font` de uma carta, nota ou introdução pode nomear uma fonte RDPL pelo nome curto ou pelo id completo:

| Nome | O que desenha |
| --- | --- |
| `rdpl` (ou `resourcedatapackloader:rdpl`)             | A fonte RDPL, com cirílico (U+0400 a U+04FF) e o alfabeto rúnico (U+16A0 a U+16F8) |
| `rdpl_runic` (ou `resourcedatapackloader:rdpl_runic`) | Uma cifra de runas: as letras A a Z e a a z são desenhadas como runas, e todos os outros caracteres são desenhados na fonte RDPL. Um trecho em negrito é desenhado em `rdpl_runic_bold` e um em itálico em `rdpl_runic_italic` |
| Runas, U+16A0 a U+16F8                                | Escritas como os próprios caracteres rúnicos (ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ), em qualquer texto que a fonte RDPL desenha, chat inclusive; trechos em negrito e itálico mantêm seu corte |

A fonte de uma carta é uma definição de fonte em `assets/<namespace>/font/<name>.json`, no mesmo formato das fontes do próprio jogo, e a carta é dimensionada de acordo com as larguras dessa fonte. Um provedor `bitmap` cujo `file` é `<namespace>:font/<name>.png` e cujos `chars` são as dezesseis linhas do `ascii.png` do jogo lê o mesmo PNG que a versão 1.12.2 usa em `assets/<namespace>/textures/font/<name>.png`, de modo que um pacote desenha as mesmas letras nas três versões. `minecraft:default` nomeia a fonte do jogo. Uma fonte que nenhum pacote possui recorre à fonte do jogo, com um aviso em `rdpl.log`.

Um pacote altera a fonte RDPL fornecendo seu próprio `assets/resourcedatapackloader/font/rdpl.json`, ou os PNGs em `assets/resourcedatapackloader/textures/font/` a partir dos quais ela é desenhada; qualquer um dos dois a substitui em todo lugar, inclusive no texto do jogo. O texto do jogo usa a fonte RDPL porque o mod traz `assets/minecraft/font/default.json` com a fonte RDPL primeiro e as fontes do próprio jogo depois dela para todos os outros caracteres. O `assets/minecraft/font/default.json` de um pacote é lido antes do do mod, de modo que uma cópia do do vanilla devolve ao texto do jogo a sua própria fonte; o texto do próprio RDPL mantém a fonte RDPL, a menos que `saysFont` seja `minecraft:default`.

Títulos e linhas de cartas, mensagens Says e as notas de boas-vindas e de retenção aceitam as marcações em linha da tabela em Introdução do mundo, Formatação de texto: negrito, itálico, negrito itálico, tachado, código, links e trechos rúnicos. Um trecho em negrito é desenhado no corte `_bold` da fonte e um em itálico no corte `_italic`; para uma fonte sem esse corte, o trecho recebe o estilo negrito ou itálico do jogo, e a carta é dimensionada de acordo com os trechos como desenhados. Jogadores sem o mod recebem as mesmas marcações como formatação de chat, e um trecho rúnico como suas letras comuns.

### Regras integradas

*cartas*

| Id | A mensagem | De onde vem o texto |
| --- | --- | --- |
| `rdpl:gate_unlocked`    | Um portal abre                                                  | `unlockedMessage` em [Portais](#portais) |
| `rdpl:gate_blocked`     | Um portal fechado faz um jogador voltar, na barra de ação       | `blockedMessage` em [Portais](#portais) |
| `rdpl:team_joined`      | Um jogador entra em um lado                                     | o `displayName` do lado |
| `rdpl:team_lead`        | A liderança de um lado passa a um jogador                       | `leadSays` em [Equipes](#equipes) |
| `rdpl:team_picked`      | Um jogador é escolhido para um lado                             | o `displayName` do lado |
| `rdpl:team_round_ended` | A rodada terminou, então um jogador é movido para um lado       | o `displayName` do lado |
| `rdpl:lobby_joins`      | Um jogador que entra no meio de uma rodada é enviado ao lobby   | `opens.joinsSays` em [O lobby](#o-lobby) |
| `rdpl:lobby_note`       | A linha do lobby no meio da tela                                | `opens.says`, `opens.leaderSays` em [O lobby](#o-lobby) |
| `rdpl:scoring_results`  | A classificação ao fim de uma rodada, para cada jogador         | `results.card`, `results.title`, `results.icon`, `results.image`, `results.background`, `results.seconds` em [Resultados](#resultados) |
| `rdpl:scoring_out`      | Um jogador eliminado                                            | `ends.outSays` em [Como uma rodada termina](#como-uma-rodada-termina) |
| `rdpl:reset_lead`       | O líder redefine a rodada                                       | `reset.leadSays` em [Redefinindo uma rodada](#redefinindo-uma-rodada) |
| `rdpl:reset_vote`       | Uma votação de redefinição é convocada                          | `reset.voteSays` |
| `rdpl:reset_pass`       | A votação passa                                                 | `reset.passSays` |
| `rdpl:reset_fail`       | A votação fracassa                                              | `reset.failSays` |
| `rdpl:anvil_waits`      | O trabalho de uma bigorna espera por um avanço                  | [Trabalho na bigorna](#trabalho-na-bigorna) |
| `rdpl:threat`           | A faixa de ameaça de um jogador muda                            | `threatSays` |
| `rdpl:prospect`         | Cada linha que uma descoberta de prospecção informa             | a descoberta |
| `rdpl:prospect_none`    | A prospecção não encontrou nada                                 | o arquivo de idioma |
| `rdpl:board_result` | Uma partida de tabuleiro termina | o arquivo de idioma |
| `rdpl:pregen_ended`     | A pré-geração termina ou é interrompida                         | `pregenFinishedSays`, `pregenStoppedSays` em [Pré-geração](#pré-geração) |
| `rdpl:pregen_running`   | A linha de progresso que um jogador vê ao entrar durante a pré-geração | `pregenRunningSays` |

`welcomeSays` não é uma regra e mantém seu logotipo; uma regra `first_join` ou `dimension_enter` se soma a ele. As contagens regressivas e os placares da barra de ação de uma rodada permanecem como suas configurações os definem.

## Dados e baralhos

*modos de jogo*

`<namespace>/dice/*.json`

O nome do arquivo é escolha sua, e vários arquivos se somam. Um arquivo nomeia dados cujas faces têm pesos, baralhos de cartas tiradas sem reposição, quem ouve uma rolagem por padrão e o texto dos resultados. As rolagens são feitas com [`/rdplserver game`](#jogos) e com qualquer item que tenha [`rolls`](#chaves-de-arquivo-de-itens).

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

| Chave | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `audience` | texto | `all` | Quem ouve uma rolagem que não nomeia a sua: `self`, `team`, `all`, `radius <blocos>` ou `silent`. Vale o primeiro pacote que a define; um posterior fica no log |
| `dice` | objeto | vazio | Nome do dado para um objeto de face e peso. Uma face de peso 2 sai duas vezes mais que uma de peso 1. Os pesos são números inteiros a partir de 1 |
| `decks` | objeto | vazio | Nome do baralho para a sua lista de cartas, ou para um objeto `{ "cards": [...], "reshuffle": false }`. `reshuffle` é `true` se não for definido: tirar de um baralho vazio embaralha todas as cartas e tira. Com `false` o baralho continua vazio até `game deck shuffle` |
| `says` | objeto | vazio | Chave de texto para o texto que substitui o texto próprio do mod em todos os idiomas. As chaves e os seus marcadores estão abaixo |

Um nome de dado ou de baralho pertence ao primeiro pacote que o carrega. Outro pacote com o mesmo nome, ou com o nome `coin`, fica de fora com um erro no log. Um dado do pacote rola com `game die <name>` e mostra a sua face; guardado como pontuação, conta como a posição da face no arquivo, a partir de 1.

Um baralho é uma pilha que vai acabando. `game deck draw <name>` tira uma carta ao acaso do que resta, e nada volta até que o baralho vazio se embaralhe sozinho na próxima carta ou `game deck shuffle <name>` devolva todas as cartas. A pilha é salva com o mundo, então reiniciar não a embaralha.

Cada rolagem usa o acaso do próprio mundo e é gravada no log com quem a fez, o que foi feito e o que saiu. `game last` mostra as mais recentes. Os resultados saem como linhas de chat comuns montadas no servidor, então um jogador sem o mod também as lê. A notação de dados é lida como quantidade, `d`, lados e um modificador opcional: `3d8-2` são três dados de oito lados somados, menos 2, e `d20` é um dado de vinte lados. O chat e o log escrevem a rolagem por extenso, como "Boss rola 3 dados de oito lados, menos 2: [2, 6, 7] = 13", e `{dice}` guarda essa redação.

| Chave de texto | Marcadores |
| --- | --- |
| `coin`, `pickplayer`, `pickteam` | `{player}`, `{result}` |
| `heads`, `tails`, `lastnone`, `nobody`, `notallowed`, `usage` | nenhum |
| `die` | `{player}`, `{dice}`, `{sides}`, `{result}` |
| `packdie` | `{player}`, `{die}`, `{result}` |
| `dice` | `{player}`, `{dice}`, `{rolls}`, `{result}` |
| `advantage`, `disadvantage` | `{player}`, `{dice}`, `{first}`, `{second}`, `{result}` |
| `pickmember` | `{player}`, `{team}`, `{result}` |
| `draw`, `reshuffled` | `{player}`, `{deck}`, `{result}`, `{left}` |
| `shuffle` | `{player}`, `{deck}`, `{left}` |
| `left`, `empty`, `nodeck` | `{deck}`, e `{left}` em `left` |
| `teamroll` | `{member}`, `{dice}`, `{result}` |
| `teamrollwin` | `{result}`, `{score}` |
| `tiebreak` | `{objective}`, `{sides}`, `{result}` |
| `notie`, `noobjective` | `{objective}` |
| `turn`, `turnclock`, `turnwarn` | `{group}`, `{seconds}` |
| `turnout`, `turnpass` | `{group}` |
| `turngap` | `{seconds}` |
| `notturn`, `noturns` | nenhum |
| `badsides`, `badroll`, `badaudience`, `nodie`, `noteam` | `{sides}`, `{roll}`, `{audience}`, `{name}`, `{team}`, nessa ordem |

O texto próprio do mod fica nos seus arquivos de idioma como `rdpl.game.<key>`, então um pacote de recursos também pode mudá-lo idioma por idioma.

## Jogos de tabuleiro

*modos de jogo*

`<namespace>/games/*.json`

Um arquivo é um jogo de tabuleiro, com o nome do seu arquivo. Ele define o tabuleiro, os dois lados, as peças e como se movem, a posição inicial e o que um resultado paga. `game board start <game> <board>` monta um tabuleiro onde está quem enviou, ou na posição dada: as casas ficam um bloco abaixo, as colunas correm para o leste e as fileiras para o sul, e cada peça é uma criatura escolhida pelo pacote, parada, muda e invulnerável, com o nome do seu lado. Uma peça capturada fica ao lado do tabuleiro.

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
      "letter": "p", "value": 1, "mobs": ["minecraft:snow_golem", "minecraft:zombie"],
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
      "letter": "r", "value": 5, "mobs": ["minecraft:iron_golem", "minecraft:wither_skeleton"],
      "moves": [{ "steps": [[1, 0], [0, 1], [-1, 0], [0, -1]], "slides": true }]
    },
    "queen": {
      "letter": "q", "value": 9, "mobs": ["minecraft:polar_bear", "minecraft:blaze"],
      "moves": [{ "steps": [[1, 0], [0, 1], [-1, 0], [0, -1], [1, 1], [1, -1], [-1, -1], [-1, 1]], "slides": true }]
    },
    "king": {
      "letter": "k", "value": 0, "royal": true, "castles": "rook", "mobs": ["minecraft:evoker", "minecraft:vindicator"],
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

| Chave | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `name` | texto | o nome do arquivo | O nome mostrado no chat e no resultado |
| `board` | objeto | 8 por 8 | `files` e `ranks`, de 2 a 16 cada, e os estados de bloco `light` e `dark` postos sob as casas. Se faltarem, o chão fica como está |
| `sides` | lista | `White`, `Black` | Dois objetos de `name` e `color`, uma cor de chat. O primeiro lado joga primeiro e se arma com as letras maiúsculas |
| `pieces` | objeto | nenhum | Nome da peça para um objeto com as chaves de peça abaixo |
| `setup` | lista | nenhum | Um texto por fileira, a fileira 1 primeiro. A letra de uma peça a coloca, maiúscula para o primeiro lado e minúscula para o segundo, e `.` deixa a casa vazia |
| `rules` | objeto | nenhum | `mustCapture`, `chainCaptures`, `quietDraw` e `repeatDraw`, abaixo |
| `clock` | objeto | nenhum | `minutes` para cada lado e `addSeconds` somados depois de cada lance. O lado cujo tempo acaba perde |
| `ai` | número | `2` | O nível do computador, de 1 a 4, para um lado que ninguém tem |
| `result` | objeto | nenhum | O `objective` pago quando a partida acaba, com os pontos `win`, `draw` e `loss`, 1, 0 e 0 se faltarem |

| Chave de peça | Tipo | O que faz |
| --- | --- | --- |
| `letter` | texto | A letra usada em `setup` e em `game board show` |
| `value` | número | Quanto ela vale para o computador |
| `mobs` | lista | A criatura de cada lado, o primeiro lado primeiro. `mob` dá uma para os dois |
| `moves` | lista | Passos como objetos: `steps`, uma lista de deslocamentos `[file, rank]` vistos do próprio lado; `slides`, para seguir até algo bloquear; `captures`, `both` (o padrão), `never`, `only` ou `hop`, que salta uma peça do outro lado para a casa vazia atrás dela e a captura; e `firstRange`, quantos passos ela pode dar no primeiro lance |
| `royal` | verdadeiro ou falso | O lado perde quando esta peça leva xeque-mate, e nenhum lance pode deixá-la atacada. Sem peça real, perde o lado que fica sem lances |
| `enPassant` | verdadeiro ou falso | Pode capturar uma peça que acabou de passar duas casas ao seu lado |
| `castles` | texto | Uma peça parceira: esta peça anda duas casas na direção de uma parceira que não se moveu, que salta por cima dela |
| `promotes` | lista | No que ela pode virar na última fileira. A primeira é usada, a não ser que o lance nomeie outra |

| Regra | O que faz |
| --- | --- |
| `mustCapture` | Um lado que pode capturar tem de capturar |
| `chainCaptures` | Depois de um salto que captura, a mesma peça segue capturando enquanto puder |
| `quietDraw` | Quantos lances seguidos, contando os dois lados, podem passar sem captura nem lance de uma peça que pode ser promovida antes que a partida empate. 0 nunca empata |
| `repeatDraw` | A partida empata quando a mesma posição, com o mesmo lado a jogar, surge esse número de vezes; 3 é a tripla repetição do xadrez. Só contam as posições desde a última captura ou lance de uma peça que pode ser promovida. 0 nunca empata |

Um lado pertence ao primeiro jogador que clica com o botão direito numa das suas peças, ou ao time do placar dele, se estiver num, de modo que qualquer membro pode jogá-lo depois. Um lado que ninguém tem é jogado pelo computador assim que o outro lado é tomado, ou na hora quando `game board ai` define um nível para ele; o computador pensa fora da thread do servidor, e o seu lance é feito nela. Um clique direito numa peça mostra para onde ela pode ir, e então um clique direito numa casa ou numa peça do outro lado a move para lá. `game board move` faz o mesmo com nomes de casa, como `e2 e4`, com um nome de peça no fim para uma promoção. Quando um lance promove e `promotes` lista mais de uma peça, o jogador escolhe entre opções clicáveis no chat; sem escolha em 10 segundos, ou se ele clicar no tabuleiro, vale a primeira. O computador escolhe a que julga melhor.

`game board resign` abandona a partida. `game board draw` oferece empate, que o outro lado aceita com o mesmo comando; o computador recusa quando está melhor. `game board takeback` pede para desfazer o último lance de quem pede, e o outro lado concorda com o mesmo comando; contra o computador ele é desfeito na hora.

Um tabuleiro é guardado nos dados salvos do mundo como a sua lista de lances. Quando o mundo carrega ou o pacote é recarregado, a posição é refeita a partir dessa lista, as peças voltam ao lugar para combinar com ela e qualquer peça mais antiga do tabuleiro é removida.

No fim, `result` paga o detentor de cada lado no seu objetivo, como linha de jogador ou de time. O resultado vai para os jogadores perto do tabuleiro e para os que têm um lado, como o cartão `rdpl:board_result` quando um arquivo de cartões define esse id, e como uma linha de chat caso contrário. O texto fica nos arquivos de idioma do mod como `rdpl.game.board.<key>`.

---

# Controle

## A camada de controle

*controle*

Tudo o que interrompe ou altera a geração é agrupado, e cada grupo tem uma chave na categoria `control` da config, com três valores:

| Valor | O que significa |
| --- | --- |
| `default` | O pacote decide. Os valores da config são o fallback |
| `global`  | A config vence. As seções do pacote são ignoradas |
| `off`     | O grupo é totalmente desativado e nenhum pacote pode ativá-lo |

Os grupos são `ores`, `biomes`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `replacements`, `villages`, `entities`, `chunks`, `blastPlaster`, `commands` e `server`.

As configurações são resolvidas em **seção de bioma → modelo de mundo → config**. O bloco `settings` de um modelo de mundo usa os mesmos nomes de chave da config, de modo que um pacote as define da mesma forma que você faria:

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

Com o controle de um grupo em `default`, elas prevalecem; em `global`, são ignoradas; e em `off`, o grupo inteiro não faz nada, não importa o que qualquer pacote diga. Uma chave que um modelo nomeia e que nada lê é avisada uma vez no log, e o mesmo vale para uma chave em uma seção `biomes` que não é uma configuração de vila.

O arquivo de config é `config/resourcedatapackloader-common.toml`. Toda chave abaixo tem o mesmo nome ali, sob a sua categoria, e uma lista é escrita como a lista TOML que o formato de config do jogo usa.

## O que cada grupo faz

*controle*

Toda configuração abaixo é lida por meio do seu grupo, de modo que a chave `control` do grupo decide se um pacote ou a config tem a última palavra. Uma configuração que um pacote pode definir aparece no bloco `settings` de um modelo de mundo com o mesmo nome; uma marcada como **apenas config** é lida somente da config, e um pacote que a escreva recebe um aviso e é ignorado. Os padrões são os da config.

### Minérios

*o que cada grupo faz*

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

`control.ores` decide este grupo. Bloqueio da geração de minérios por mod e por tipo de minério.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `blockOres`                      | boolean         | `false`         | Impede que todo mod, e o próprio Minecraft, gere minérios. Apenas os mods em oreWhitelist ainda geram. Um minério é um placed feature construído sobre a feature de minério ou de minério disperso do jogo, aquelas para as quais a 1.12.2 levantava seu evento de minério, o que abrange os minérios do Minecraft e da maioria dos mods, terra, cascalho e os tipos de pedra. As entradas de worldgen do próprio pacote nunca são bloqueadas, nem por isto nem por oreTypes |
| `logBlockedOres`                 | boolean         | `true`          | Registra no log a primeira vez que cada mod e tipo de minério é barrado |
| `oreWhitelist`                   | lista de ids de mod | `["minecraft"]` | Os mods ainda autorizados a gerar minério enquanto `blockOres` está ativado |
| `prospectItems`                  | list            | vazio           | Itens que prospectam entradas de worldgen em forma de veio quando um jogador agachado quebra um bloco com um deles, como item=entrada\|entrada[,raio em chunks] ou item=*[,raio], p. ex. minecraft:compass=iron_vein\|coal_seam ou mypack:rod=*,12. A leitura nomeia o minério e uma direção da bússola |
| `prospectItemsAreBlacklist`      | boolean         | `false`         | Ativado, a lista de cada item são as entradas que ele não lê |
| `prospectDrops`                  | boolean         | `false`         | Ativado, um bloco quebrado no modo de prospecção ainda solta itens e dá experiência. Desativado, a amostra é consumida |
| `prospectSlow`                   | int, 1 a 100    | `2`             | Quantas vezes mais tempo um jogador agachado com um item marcado leva para quebrar um bloco. `1` é a velocidade normal |
| `prospectWear`                   | int, 2 a 1000   | `2`             | Quantas vezes o desgaste normal uma quebra de prospecção custa à ferramenta. `2`, o dobro, é o mínimo permitido, e um item sem durabilidade não paga nada |
| `oreTypes`                       | list            | vazio           | Tipos de minério a que isto se aplica, quem quer que os gere e o que quer que a whitelist diga. Tipos conhecidos: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM para qualquer outro minério |
| `oreTypesAreBlacklist`           | boolean         | `true`          | Ativado, os tipos em `oreTypes` são os bloqueados. Desativado, apenas esses tipos geram |
| `blockOreDimensions`             | list            | vazio           | As dimensões a que o bloqueio de minérios se aplica, vazio significando todas. Uma dimensão fora do escopo não é tocada de modo algum, de forma que os minérios de outro mod geram ali enquanto o overworld permanece bloqueado |
| `blockOreDimensionsAreBlacklist` | boolean         | `false`         | Ativado, as dimensões listadas são as deixadas em paz |

### Biomas

*o que cada grupo faz*

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

`control.biomes` decide este grupo. Bloqueio de biomas por mod e por nome, e o que os substitui.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `logBlockedBiomes`                 | boolean         | `true`                    | Registra no log uma contagem por mod de quais biomas foram bloqueados |
| `blockBiomes`                      | boolean         | `false`                   | Impede que todo bioma gere, exceto os dos mods em biomeWhitelist. Os biomas bloqueados viram o bioma vazio, ou o que os roles e o fallback do modelo de mundo nomearem. Bloquear todo bioma enquanto o modelo de mundo é vazio, sem roles e com padrão vazio, faz das voidWorldDimensions um mundo vazio |
| `biomeWhitelist`                   | lista de ids de mod | `["minecraft"]`       | Os mods cujos biomas ainda geram enquanto `blockBiomes` está ativado. Um bioma de pacote usa o namespace do pacote |
| `biomeNames`                       | list            | vazio                     | Biomas a que isto se aplica, por id como minecraft:birch_forest ou pelo nome que o jogo mostra, como Floresta de Bétulas. Como blacklist, são bloqueados seja quem for o dono. Como whitelist, um bioma listado ainda precisa que seu mod esteja em biomeWhitelist enquanto blockBiomes está ativado |
| `biomeNamesAreBlacklist`           | boolean         | `true`                    | Ativado, os nomes em `biomeNames` são bloqueados. Desativado, apenas esses nomes geram |
| `blockBiomeDimensions`             | list            | `["minecraft:overworld"]` | As dimensões a que o bloqueio de biomas se aplica. Vazio significa todas |
| `blockBiomeDimensionsAreBlacklist` | boolean         | `false`                   | Ativado, o bloqueio pula as dimensões listadas. Desativado, aplica-se apenas a elas |

### Geradores

*o que cada grupo faz*

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

`control.generators` decide este grupo. Bloqueio da geração de mundo de outros mods por mod e pelo que ela produz. Um gerador é um placed feature, pertencente ao namespace do seu id; as features do próprio Minecraft, deste mod e as entradas de worldgen de um pacote nunca são bloqueadas.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `blockWorldGenerators`                 | boolean                     | `false`                   | Impede que todo mod gere por meio de seus próprios placed features, que é como os mods adicionam ilhas de slime, cristais de caverna e coisas assim. Apenas os mods em generatorWhitelist ainda geram |
| `generatorWhitelist`                   | lista de ids de mod         | `["minecraft"]`           | Os mods ainda autorizados a gerar enquanto `blockWorldGenerators` está ativado |
| `blockedGenerators`                    | lista de ids de mod ou partes de id | vazio             | Geradores individuais bloqueados de vez, o que quer que a whitelist diga, por id de mod ou por parte do id de um placed feature |
| `blockGeneratorDimensions`             | list                        | `["minecraft:overworld"]` | As dimensões a que o bloqueio de geradores se aplica. Vazio significa todas |
| `blockGeneratorDimensionsAreBlacklist` | boolean                     | `false`                   | Ativado, o bloqueio pula as dimensões listadas. Desativado, aplica-se apenas a elas |
| `generatorTypes`                       | list                        | vazio                     | Tipos a que isto se aplica, quem quer que seja o dono do gerador e o que quer que a whitelist diga: `ores`, `structures`, `flora`, `lakes`, `terrain`, ou `unknown` para os que nada reconheceu. O tipo vem de palavras no id da feature, de modo que `crystal_ore` é ores e `slime_island` é structures |
| `generatorTypesAreBlacklist`           | boolean                     | `true`                    | Ativado, os tipos em `generatorTypes` são os bloqueados. Desativado, apenas esses tipos geram |
| `generatorTypeMap`                     | lista de `pattern=type`     | vazio                     | Tipos para geradores que o id não descreve, sendo o padrão um id de mod ou parte do id de uma feature, p. ex. mymod=ores. As entradas mapeadas são verificadas antes das palavras integradas, de modo que também corrigem uma que as palavras leiam de modo errado |
| `logBlockedGenerators`                 | boolean                     | `true`                    | Registra no log cada gerador com o tipo que recebeu na primeira vez que é bloqueado. `/rdplserver generators` mostra os totais correntes por mod e tipo |

### Substituições

*o que cada grupo faz*

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

`control.replacements` decide este grupo. Substituição de blocos em chunks que já existem.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `blockReplacements`                      | list               | vazio   | Blocos trocados nos chunks conforme carregam, escritos como bloco=bloco com um estado opcional de cada lado, como minecraft:andesite=minecraft:stone ou minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Cada chunk é processado uma vez, os novos inclusive |
| `blockReplacementDimensions`             | list               | vazio   | As dimensões a que isto se aplica. Vazio significa todas |
| `blockReplacementDimensionsAreBlacklist` | boolean            | `false` | Ativado, a substituição pula as dimensões listadas. Desativado, aplica-se apenas a elas |
| `blockReplacementMinHeight`              | int, -2032 a 2031  | `-64`   | O menor y que ela examina |
| `blockReplacementMaxHeight`              | int, -2032 a 2031  | `319`   | O maior y que ela examina |
| `blockReplacementKey`                    | string             | `0000`  | Altere-a e todo chunk passa pela substituição de novo |
| `logBlockReplacements`                   | boolean            | `true`  | Registra no log a primeira vez que cada substituição é feita, e um total quando um mundo se atualiza |

### Vilas e cidades

*o que cada grupo faz*

`control.villages` decide este grupo. As ruas de cidades e vilas que um pacote traça: sua forma, ornamentação, pontes, túneis, trilhos, terrenos e praça.

#### Estradas de vilas

*vilas e cidades*

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

**Misturando blocos.** Algumas configurações de bloco aceitam uma mistura em vez de um único bloco: blocos separados por vírgulas, cada um seguido de um espaço e um peso, como em `"minecraft:stone_bricks 3, minecraft:cobblestone 1"`. Um bloco sem peso conta uma vez. Cada bloco colocado sorteia a mistura a partir da seed do mundo e de sua posição, de modo que o mesmo mundo sempre constrói o mesmo padrão. As configurações que aceitam uma mistura são `villagePathVergeBlock`, `villagePathVergeWaterBlock`, `villagePathTunnelBlock`, `villagePathBridgeFrameBlock`, `villagePathBridgeFrameTopBlock`, `villageRailTunnelBlock`, `villageRailDeckBlock`, `villageRailSupportBlock`, `villageRailBarrierBlock`, `villageRailBridgeFrameBlock`, `villageRailBridgeFrameTopBlock`, `villageSubwayTunnelBlock`, `villageSubwayPlatformBlock`, `villageSubwayRailingBlock`, `villageSubwayBenchEndBlock` e `villageSewerMossBlock`. Toda outra configuração de bloco usa o primeiro bloco de uma mistura em todo lugar.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villagePathBlock`         | text                  | vazio                  | A superfície da estrada quando terrainAdaptation traça estradas de cidade. Vazio mantém o bloco que o bioma usaria: arenito sobre areia, terracota sobre badlands, caminho de terra sobre solo |
| `villagePathExtraWidth`    | int, 0 ou mais        | `0`                    | Blocos extras de largura de estrada de cada lado além dos 3 usuais, quando terrainAdaptation traça as estradas. Alarga as próprias ruas, de modo que os blocos entre elas se afastam das estradas largas |
| `villageBlockSizes`        | lista de `size=weight` | vazio                 | Qual a profundidade dos quarteirões entre as ruas paralelas de uma cidade, sorteada uma vez por distrito a partir da posição de sua praça. Vazio dimensiona todo quarteirão para o maior terreno que o pacote traz |
| `villageCitySpacing`       | int, 0 a 256          | `16`                   | A que distância uns dos outros os distritos de cidade são semeados, em distritos dimensionados a partir dos terrenos (o dobro do maior terreno, mais uma praça e uma rua de cada lado, arredondado para 16 blocos, no mínimo 96): um distrito em cada quadrado desse tamanho abriga uma cidade, e em 1 todo distrito é uma, uma praça com o poço no centro e ruas saindo dela que se unem às do distrito seguinte. 0 não semeia nenhuma. Quando o pacote não o define, `structureSpacing` villages=chunks o define, no mínimo 9 chunks. Um quadrado funda sua cidade em seu distrito mais plano, com no máximo 10 blocos do ponto mais alto ao mais baixo, em um bioma de vila (`structureBiomes` villages= os escolhe), e não onde uma mansão da floresta poderia começar; `structureSeparation`, `structureMinDistanceFromSpawn` e `structureMost` villages= mantêm as cidades afastadas como mantinham as vilas, e `structureAt` villages=x,z funda cidades apenas onde as fixa. Uma cidade cresce apenas sobre distritos ao alcance de seu primeiro poço cujo terreno sobe no máximo 6 blocos e permanece acima do nível da água, e uma cidade de dois terrenos e poços ou menos não é construída |
| `villagePathAlleyBlock`    | block                 | vazio                  | A superfície de uma viela, uma estrada estreita demais para levar linhas e calçadas. Uma viela corre entre as calçadas das ruas que ela encontra e não leva nenhuma própria, e nenhuma faixa de pedestres é pintada onde ela encontra uma rua. Vazio traça as vielas com o bloco da estrada |
| `villagePathAlleyChance`   | int, 0 ou mais        | `0`                    | A chance percentual de uma rua ser traçada como viela em vez de em sua largura total. 0 não traça vielas |
| `villagePathMinimumWidth`  | int, 0 ou mais        | `0`                    | A rua mais estreita permitida. Uma rua que seria traçada mais estreita que isto não é traçada de modo algum, e o distrito se organiza em torno da lacuna. 0 nunca recusa |
| `villagePathFlatRun`       | int, 0 ou mais        | `6`                    | As ruas mantêm cada declive por ao menos esta quantidade de blocos antes de degrau, ancoradas às coordenadas do mundo para que os segmentos concordem entre as peças. 0 ou 1 deixa uma rua dar um degrau a cada bloco |
| `villagePlotsLeast`        | int, 0 ou mais        | `0`                    | Quantos terrenos uma cidade alcança ao crescer: distritos são acrescentados anel a anel em torno de seu centro até conterem ao menos esta quantidade, nunca mais que villagePlotsMost. 0 traça apenas o distrito central |
| `villagePlotsMost`         | int, 0 ou mais        | `0`                    | O máximo de terrenos que uma cidade pode conter: o crescimento para antes do distrito que o ultrapassaria e nenhum distrito acomoda mais, e um distrito que o atinge deixa de fora as vielas que nenhum terreno margeia. 0 não define teto |
| `villagePlotsBackRow`      | boolean               | `true`                 | Depois que a vila cresceu, uma segunda passada acomoda um terreno diretamente atrás de cada terreno que dá para uma rua, voltado para ela, com o mesmo sorteio e o mesmo teste de espaço, de modo que o interior de um quarteirão entre duas ruas é construído em vez de deixado vazio |
| `villageTieStreets`        | boolean               | `true`                 | Ativado, um distrito que não consegue estender suas ruas até a vila existente recebe uma rua de ligação reta traçada até a rua mais próxima com que se alinha. Desativado, tal distrito é desfeito |
| `villageLayout`            | text                  | vazio                  | Um mapa de cidade traçado em vez de planejar o distrito, nomeado como mypack:downtown e lido da pasta citymaps desse pacote. Vazio planeja o distrito como de costume |
| `villagePathCenterBlock`   | block                 | vazio                  | Uma linha central ao longo do meio da estrada. Vazio não desenha nenhuma |
| `villagePathCenterDash`    | int, 0 ou mais        | `0`                    | Tracejado dessa linha: N blocos de linha, depois um de estrada. Ancorado às coordenadas do mundo, de modo que os traços de uma peça de estrada continuam na seguinte. `0` a mantém contínua |
| `villagePathLineBlock`     | block                 | vazio                  | Linhas de borda entre a estrada e a calçada. Vazio não desenha nenhuma |
| `villagePathSidewalkBlock` | block                 | vazio                  | Calçadas, traçadas no nível da estrada fora das linhas de borda. Vazio não traça nenhuma |
| `villagePathSidewalkWidth` | int, 0 ou mais        | `2`                    | Qual a largura de cada calçada, uma vez definido `villagePathSidewalkBlock` |
| `villagePathLampBlock`     | text                  | `minecraft:oak_fence`  | O bloco de que um poste de luz ao longo de uma rua é construído, empilhado com villagePathLampHeight de altura sobre o meio-fio. Uma rua ou viela tem um em cada ponta, um onde outra rua a encontra e um a cada 7 a 12 blocos entre eles, em seu lado mais baixo e no outro apenas onde ele não tem espaço, e um beco sem saída o cerca de postes na borda. Nenhum fica sobre uma ponte, em um túnel ou a menos de dois blocos de uma porta. Vazio não coloca postes de luz |
| `villagePathLampHeight`    | int, 1 ou mais        | `3`                    | Quantos blocos de altura o poste tem antes da cabeça |
| `villagePathLampTopBlock`  | block                 | `minecraft:black_wool` | A cabeça no topo do poste. Vazio a deixa nua |
| `villagePathLampSideBlock` | block                 | `minecraft:torch`      | A luz pendurada de cada lado da cabeça, voltada para fora. Vazio não pendura nenhuma |
| `villagePathLampStructure` | text                  | vazio                  | Um arquivo de estrutura colocado como o poste inteiro, em vez de empilhar os três blocos do poste, nomeado como `mypack:street_lamp` e lido da pasta `structures` desse pacote. Ele é centralizado no ponto do poste com sua camada mais baixa sobre o meio-fio, e os blocos que ele traça são retidos para que nada mais os sobrescreva. Vazio empilha os blocos |
| `villageWellStructure`     | list                  | vazio                  | Arquivos de estrutura colocados como peça central de toda praça, uma entrada ponderada por linha escrita nome=peso como mypack:plaza_spire=3, sorteada uma vez por praça. Ela é centralizada em um quadrado de seis blocos limpo e pavimentado com villagePathBlock, com sua camada mais baixa sobre esse piso. Vazio, a parcela vazia, ou uma estrutura que não pode ser carregada constrói ali o poço do próprio jogo. Uma entrada que não esteja escrita nome=peso é deixada de fora |
| `villagePathDeadEnds`      | list                  | vazio                  | Como uma rua sem saída é fechada, uma entrada por linha, sorteada por ponta: sidewalk pavimenta a fileira final com o bloco da calçada e barrier coloca villagePathBridgeBarrierBlock ao longo dela com villagePathBridgeBarrierHeight de altura; qualquer outra entrada é ignorada. Elas fecham apenas uma ponta que não formou um beco sem saída, um estilo cujo bloco não está definido sai do sorteio, e uma ponta de viela aceita apenas barrier. Vazio deixa tais pontas abertas |
| `villagePathIntersects`    | list                  | vazio                  | Desenhos pintados nos cruzamentos, nomeados por chave de registro a partir de `<namespace>/pathintersects/` de um pacote. Uma entrada pinta todo cruzamento igual; várias são escolhidas por cruzamento conforme o peso |

#### Pontes e cais de vilas

*vilas e cidades*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villagePathSupportBlock`        | text           | vazio                                      | A própria superfície onde o solo é rocha nua, e os cais e pernas sob uma rua sobre a água. Vazio mantém o cascalho do vanilla, arenito em cidades do deserto |
| `villagePathBridgeBlock`         | text           | vazio                                      | O bloco com que uma rua ou cais atravessa a água. Vazio faz o tabuleiro com tábuas da madeira da vila: acácia em uma vila de savana, abeto em uma vila de taiga, carvalho nas demais |
| `villagePathBridgeSidewalkBlock` | block          | vazio                                      | Faz o tabuleiro da calçada onde uma estrada atravessa a água. Vazio leva o bloco de calçada normal através |
| `villagePathBridgeBarrierBlock`  | block          | vazio                                      | Barreiras empilhadas ao longo das duas bordas do tabuleiro de uma ponte. Nenhuma fica onde o tabuleiro repousa sobre o solo. Vazio não constrói nenhuma |
| `villagePathBridgeBarrierHeight` | int, 1 ou mais | `1`                                        | Quantos blocos de altura essas barreiras têm |
| `villagePathBridgeDrop`          | int, 0 ou mais | `0`                                        | O quanto o declive de uma estrada precisa estar livre do solo antes que o vão sob ela seja coberto por ponte em vez de preenchido maciço. `0` mantém as estradas no solo: elas atravessam a água com ponte e nada mais. `3` é a regra que um cavalete ferroviário segue. Isto move o declive, não apenas a ornamentação |
| `villagePathVergeBlock`          | text           | vazio                                      | O bloco com que o solo ao lado de uma rua e sob um terreno é preenchido onde a cidade tem de fazer terra: os sulcos entre terrenos e o aterro até uma rua através de uma lacuna, que é revestido com villagePathBridgeBlock em vez disso onde a rua ali é uma ponte. Vazio segue o solo em que está, areia, terracota, cascalho ou terra com grama por cima onde seria terra |
| `villagePathVergeWaterBlock`     | block          | `minecraft:oak_planks`                     | No que esse aterro se transforma onde está sobre a água, para que uma margem levada para um lago não seja uma coluna de terra. Ela reveste também um degrau de pedra deixado sobre a água |
| `villagePathBridgeFrameBlock`    | block          | vazio                                      | Uma moldura aérea sobre uma ponte longa: um poste de cada lado do tabuleiro e uma viga no topo. Cada moldura leva um pilar até o chão sob o tabuleiro, e nenhum poste de luz é erguido na fileira em que ela está. Vazio não constrói nenhuma |
| `villagePathBridgeFrameTopBlock` | block          | vazio                                      | A viga no topo dessa moldura. Vazio usa `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight`   | int, 1 ou mais | `4`                                        | Quantos blocos de vão livre a moldura deixa sobre o tabuleiro, ficando a viga um bloco acima disso |
| `villagePathBridgeFrameRun`      | int, 1 ou mais | `24`                                       | A quantas fileiras de distância as molduras ficam quando uma ponte é longa o bastante para várias. Elas são distribuídas simetricamente em torno do meio do trecho em ponte |
| `villagePathBridgeFrameLeast`    | int, 1 ou mais | `24`                                       | O menor trecho em ponte que recebe uma moldura. Uma ponte mais curta é deixada simples |
| `villagePathPiers`               | list           | vazio                                      | Estilos de cais para uma rua sem saída sobre a água: a cauda em ponte vira um cais em vez de uma ponte para lugar nenhum. Os estilos são railed, pilings e boardwalk; várias entradas sorteiam uma por cais. Vazio deixa tal cauda como ponte simples |
| `villagePathPierCargo`           | list           | vazio                                      | Carga posta ao longo do lado interno das grades de um cais, como entradas bloco=peso, bloco=peso,altura para empilhá-la, ou empty=peso para a parcela deixada livre. Um bloco pode levar seu estado entre colchetes, e um com facing se volta para o meio do cais. Uma altura que não seja de 1 a 8 empilha um bloco. Toda outra fileira sorteia a lista em cada lado. Vazio deixa os cais nus |
| `villagePathPierLoot`            | text           | `resourcedatapackloader:chests/pier_cargo` | A tabela de saque de que os blocos de carga com inventário são preenchidos, sorteada na primeira vez que um é aberto. Um pacote pode substituir a tabela integrada fornecendo sua própria tabela de saque com esse nome. Vazio os deixa vazios |

#### Túneis de vilas

*vilas e cidades*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villagePathTunnelBlock`      | text           | vazio   | O bloco com que uma rua é revestida onde atravessa um morro em vez de cortá-lo a céu aberto: as paredes de cada lado da perfuração e o teto sobre ela. Vazio não perfura túneis e deixa a rua subir o morro |
| `villagePathTunnelDepth`      | int, 1 ou mais | `10`    | Quanto solo precisa estar sobre a superfície da estrada antes que um trecho seja perfurado em vez de cortado. Uma elevação enterrada tão fundo por doze fileiras ou mais é mantida nivelada e perfurada, suas aproximações mais rasas cortadas a céu aberto; uma lombada mais curta é cortada como antes. Só conta quando `villagePathTunnelBlock` nomeia um bloco |
| `villagePathTunnelLightBlock` | block          | vazio   | Uma luz embutida no teto do túnel ao longo de sua linha central. Vazio não ilumina nada |
| `villagePathTunnelLightRun`   | int, 1 ou mais | `8`     | A quantos blocos de distância essas luzes ficam. Ancorado às coordenadas do mundo, de modo que as luzes de uma peça de estrada continuam na seguinte; um túnel curto demais para alcançar um desses pontos é iluminado uma vez, no meio |

#### Esgotos de vilas

*vilas e cidades*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageSewerBlock`        | nome de bloco  | vazio             | O bloco com que um esgoto é revestido sob as ruas e vielas de uma vila: seu piso, suas duas paredes e seu teto. Vazio não cava esgotos |
| `villageSewerDepth`        | int, 4 ou mais | `8`               | A que profundidade sob a superfície da própria rua fica o piso do esgoto. O esgoto segue a rua sob a qual corre, de modo que uma rua em subida leva um esgoto em subida. Requer `villageSewerBlock` |
| `villageSewerHeight`       | int, 2 ou mais | `3`               | Quantos blocos de vão livre há sobre a passarela |
| `villageSewerWidth`        | int, 3 ou mais | `5`               | Qual a largura do esgoto, contada de lado a lado incluindo suas duas paredes. Um número par é arredondado para cima, para que o canal fique no meio |
| `villageSewerWaterBlock`   | nome de bloco  | `minecraft:water` | O que enche o canal ao longo do meio. Vazio deixa o canal seco |
| `villageSewerWalkBlock`    | nome de bloco  | vazio             | Com que se reveste a superfície das passarelas de cada lado do canal. Vazio caminha sobre o bloco de revestimento |
| `villageSewerLightBlock`   | nome de bloco  | vazio             | O bloco embutido no teto sobre o canal como luz. Vazio não ilumina nada |
| `villageSewerLightRun`     | int, 1 ou mais | `8`               | A quantos blocos de distância essas luzes ficam. Ancorado às coordenadas do mundo, de modo que as luzes de uma peça de estrada continuam na seguinte |
| `villageSewerLadderBlock`  | text           | vazio             | O bloco pelo qual se sobe um poço de acesso, posto ao longo do poço da rua até o teto do esgoto. Vazio deixa o poço aberto |
| `villageSewerCoverBlock`   | nome de bloco  | vazio             | O bloco que cobre um bueiro, posto rente em uma rua leste-oeste onde quer que uma rua ou viela a encontre, e na praça onde essa rua cruza o anel do esgoto. Um alçapão de madeira é a escolha usual: um de ferro recebe sinal de redstone e nenhum jogador consegue abri-lo à mão, o que fecha o esgoto para eles. Vazio deixa a boca do poço aberta |
| `villageSewerMossBlock`    | nome de bloco  | vazio             | Um segundo bloco misturado ao revestimento aqui e ali, pedra com musgo entre pedra comum, por exemplo. Vazio reveste o esgoto com um único bloco por inteiro |
| `villageSewerMossChance`   | 0 a 100        | `25`              | Que porcentagem dos blocos do revestimento sai como esse segundo bloco. Sorteada por posição de bloco a partir da seed do mundo, de modo que o mesmo esgoto sempre sai igual |
| `villageSewerVineBlock`    | nome de bloco  | vazio             | Um bloco pendurado no lado de dentro das paredes do esgoto aqui e ali, videiras por exemplo. Ele se agarra a qualquer parede contra a qual esteja. Vazio não pendura nada |
| `villageSewerVineChance`   | 0 a 100        | `20`              | Que porcentagem das células ao lado de uma parede o leva. Sorteada por posição de bloco a partir da seed do mundo, de modo que o mesmo esgoto sempre pende igual |
| `villageSewerWellEntrance` | boolean        | `true`            | Um anel de esgoto sob o anel da praça em volta do poço, com o esgoto de toda rua passando por ele, e um bueiro na praça descendo até o anel em cada lado onde uma rua leste-oeste o cruza, de modo que os esgotos formam um sistema conectado com uma entrada no centro da cidade. Desativado, o esgoto de cada rua termina no poço e a praça não tem acesso para baixo |

**Esgotos.** Nomear `villageSewerBlock` cava um esgoto sob toda rua e viela, `villageSewerDepth` blocos abaixo da superfície da própria rua. Não é uma rede à parte: ele segue as ruas, de modo que para onde elas vão o esgoto vai, sobe onde elas sobem, e dois esgotos se encontram sob um cruzamento porque as ruas acima deles se encontram; onde uma rua ou viela termina contra outra, seu esgoto segue por baixo dessa para se juntar a ela. Um beco sem saída e um trecho levado sobre uma ponte não levam nenhum. A seção é um piso revestido, um canal ao longo do meio cheio de `villageSewerWaterBlock`, uma passarela de cada lado revestida de `villageSewerWalkBlock`, `villageSewerHeight` blocos de vão livre e um teto revestido, com `villageSewerWidth` de largura de lado a lado incluindo as duas paredes, e `villageSewerLightBlock` embute uma luz no teto sobre o canal a cada `villageSewerLightRun` blocos. Onde uma perfuração de metrô passa pela profundidade do esgoto, sob a rua ou ao lado dela, o esgoto é fechado maciço por uma parede ao atravessá-la e qualquer trilho ali é deixado em paz. O esgoto de uma rua para no anel em volta do poço quando `villageSewerWellEntrance` está ativado, e no próprio poço quando está desativado. Um esgoto nunca sobe o suficiente para perturbar a rua acima dele, e um trecho sem espaço entre a rua e o piso do mundo é pulado em vez de espremido.

#### Ferrovias de vilas

*vilas e cidades*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageRailLines`               | int, 0 ou mais       | `0`     | Quantas linhas ferroviárias correm por uma cidade, traçadas antes de qualquer rua para que a cidade cresça em torno delas, cada uma correndo todo o comprimento da cidade. Com villageCitySpacing 1, todo distrito é uma cidade e leva as suas. 0 não traça nenhuma |
| `villageRailSpacing`             | int, 1 ou mais       | `48`    | O mínimo de blocos de solo livre entre o leito de uma linha ferroviária e o da seguinte da mesma cidade. 1 as traça a um bloco de distância, que é como um pacote constrói um pátio de linhas paralelas |
| `villageRailDirection`           | text                 | `any`   | Em que sentido as linhas correm: `ew` de leste a oeste, `ns` de norte a sul, `any` sorteia por cidade. `e`, `w`, `n` e `s` são lidos da mesma forma |
| `villageRailWidth`               | int, 3 ou mais       | `3`     | O mínimo que o leito tem. `3` leva uma via no meio e `5` leva duas; um leito para o qual se pedem mais vias do que isso comporta se alarga para abrigá-las |
| `villageRailBlock`               | block                | vazio   | A via. Vazio traça trilhos do vanilla, que os carrinhos percorrem; qualquer outro bloco é posto como está |
| `villageRailTrackSeat`           | `auto`, `on` ou `in` | `auto`  | Onde a via se assenta. `auto` assenta um bloco de trilho sobre o leito e embute qualquer outro bloco rente na superfície do leito; `on` sempre a põe sobre o leito; `in` sempre a embute no leito. Uma via embutida no leito é como um pacote faz um trilho parecer feito de blocos ou lajes de ferro em vez de trilhos de carrinho, e uma passagem de nível então corre rente pelo pavimento |
| `villageRailBedBlock`            | block                | vazio   | O leito sob a via. Vazio põe cascalho |
| `villageRailTieBlock`            | text                 | vazio   | O dormente posto atravessado no leito a cada villageRailTieRun fileiras. Vazio põe tábuas de carvalho |
| `villageRailTieRun`              | int, 1 ou mais       | `2`     | A quantas fileiras de distância ficam os dormentes |
| `villageRailTracks`              | int, 0 ou mais       | `0`     | Quantas vias o mesmo leito leva, lado a lado e a `villageRailTrackGap` de distância. **O leito se alarga para abrigar todas**, de modo que três vias compartilham um leito em vez de virarem três linhas. `0` põe uma via em um leito com menos de cinco de largura e duas em um mais largo |
| `villageRailTrackGap`            | int, 2 ou mais       | `2`     | A quantos blocos de distância ficam as vias em um leito, de centro a centro. `2`, o mínimo permitido, deixa um bloco de leito entre elas, o que impede que se curvem uma para a outra como fazem trilhos encostados |
| `villageRailShoulderBlock`       | block                | vazio   | Reveste as colunas mais externas do leito, um caminho de manutenção ao lado da via e a resposta da ferrovia à calçada de uma estrada. Vazio não põe nenhum |
| `villageRailShoulderWidth`       | int, 0 ou mais       | `1`     | Quantas colunas de largura esse acostamento tem de cada lado, acrescentadas fora de `villageRailWidth`. Requer `villageRailShoulderBlock` |
| `villageRailPowerBlock`          | block                | vazio   | A via energizada embutida na linha a cada `villageRailPowerRun` fileiras. Vazio usa um trilho energizado do vanilla; um bloco que não é trilho é simplesmente posto ali |
| `villageRailPowerBase`           | block                | vazio   | O que fica sob uma via energizada para alimentá-la. Vazio usa um bloco de redstone |
| `villageRailPowerRun`            | int, 0 ou mais       | `0`     | A cada tantas fileiras, um trilho energizado sobre um bloco de redstone é embutido em uma via de trilhos do vanilla, para que um carrinho continue rolando. `0` não energiza nenhuma, e qualquer via que não seja de trilhos do vanilla a ignora |
| `villageRailClimb`               | int, 1 ou mais       | `8`     | Quantas fileiras a linha corre nivelada para cada bloco que sobe ou desce. `1` a inclina tão íngreme quanto uma estrada |
| `villageRailTail`                | int, 0 ou mais       | `48`    | Até onde uma linha ferroviária segue além do último distrito da cidade em cada ponta |
| `villageRailSupportBlock`        | text                 | vazio   | O bloco do poste sob um cavalete, onde a linha passa sobre água ou um desnível. Vazio usa troncos de carvalho |
| `villageRailDeckBlock`           | text                 | vazio   | O tabuleiro sobre o qual um cavalete leva o leito. Vazio usa tábuas de carvalho |
| `villageRailBarrierBlock`        | block                | vazio   | Barreiras ao longo das duas bordas do tabuleiro de um cavalete. Vazio não coloca nenhuma |
| `villageRailBridgeFrameBlock`    | block                | vazio   | Uma moldura aérea sobre um cavalete longo: um poste de cada lado do tabuleiro e uma viga no topo. Toda fileira que leva uma leva também seus postes de apoio até o leito. Vazio não constrói nenhuma |
| `villageRailBridgeFrameTopBlock` | block                | vazio   | A viga no topo dessa moldura. Vazio usa `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight`   | int, 2 ou mais       | `4`     | Quantos blocos de vão livre a moldura deixa sobre o tabuleiro, ficando a viga um bloco acima disso |
| `villageRailBridgeFrameRun`      | int, 2 ou mais       | `24`    | A quantas fileiras de distância as molduras ficam quando um cavalete é longo o bastante para várias |
| `villageRailBridgeFrameLeast`    | int, 2 ou mais       | `24`    | O menor cavalete que recebe uma moldura. Um cavalete mais curto é deixado simples |
| `villageRailTunnelBlock`         | text                 | vazio   | O bloco com que uma linha ferroviária é revestida onde atravessa um morro em vez de subi-lo. Vazio não perfura túneis |
| `villageRailTunnelDepth`         | int, 1 ou mais       | `6`     | Quanto solo precisa estar sobre o leito antes que um trecho seja perfurado em vez de cortado. Requer `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock`    | block                | vazio   | Uma luz embutida no teto de um túnel ferroviário ao longo de sua linha central. Vazio não ilumina nada |
| `villageRailTunnelLightRun`      | int, 1 ou mais       | `8`     | A quantos blocos de distância essas luzes do túnel ficam, ancoradas às coordenadas do mundo para que as peças concordem |

**Por onde vai uma linha.** As linhas correm paralelas, no eixo que `villageRailDirection` nomeia, e são espaçadas a partir do primeiro poço da cidade, uma após a outra, primeiro de um lado e depois do outro, cada uma mantendo ao menos `villageRailSpacing` blocos de solo entre seu leito e o da linha seguinte. Uma ferrovia começa longe da praça e dos terrenos ao redor dela e se desvia sempre que correria ao longo de uma rua; um metrô começa na fileira do poço e se desloca até a rua mais próxima dentro de `villageSubwaySpacing`, de modo que corre sob uma estrada. Uma linha é traçada antes dos terrenos, de modo que nenhum terreno fica sobre via aberta, e corre todo o comprimento da cidade e por mais `villageRailTail` além de seu último distrito em cada ponta, parando sete blocos antes de qualquer outra cidade em seu caminho. Todo distrito por que passa traça seu próprio trecho, tenha a cidade crescido ali ou não.

**Declive.** Uma ferrovia não sobe como uma rua. Seu leito segue o solo suavizado ao longo de um trecho longo e muda de nível por um bloco no máximo a cada `villageRailClimb` fileiras; um metrô segue o solo `villageSubwayDepth` abaixo dele, e uma linha que não consegue manter essa profundidade em nenhum ponto de seu percurso, sobre os seis blocos de espaço de que seu revestimento precisa acima do piso do mundo, não é traçada de modo algum. Onde o solo cai mais de três blocos, ou a água o cobre, a linha corre sobre um cavalete: um tabuleiro de `villageRailDeckBlock` com a via sobre ele ou embutida nele e sem dormentes nem acostamento, sobre postes de `villageRailSupportBlock` sob as duas bordas a cada quatro fileiras, cada poste descendo até solo firme em no máximo 24 blocos. Onde o solo sobe, a linha é cortada a céu aberto, ou perfurada com `villageRailTunnelBlock` quando o solo sobre o leito atinge `villageRailTunnelDepth` de profundidade por doze fileiras ou mais; a perfuração segue enquanto um bloco de solo ainda a cobrir. Um corte aberto com água a até três blocos dele é murado com o revestimento de túnel até a água, e o solo ao lado do leito é aterrado onde cai. Quatro blocos são mantidos livres sobre o leito ao longo de toda a linha. Um cavalete fica a uma só altura de ponta a ponta, e o leito de cada lado dele faz rampa para alcançar essa altura; onde manter um cavalete nivelado e a taxa de subida divergem, o nível vence e a rampa ao lado pode dar degrau antes do que `villageRailClimb` diz. Um cavalete de `villageRailBridgeFrameLeast` fileiras ou mais leva molduras aéreas quando `villageRailBridgeFrameBlock` nomeia um bloco, a `villageRailBridgeFrameRun` fileiras de distância e distribuídas simetricamente em torno do meio do cavalete, e toda fileira que leva uma leva também seus postes de apoio. Uma fileira onde uma rua cruza a linha é deixada sem moldura.

**Cruzamentos.** Uma rua cruza uma linha em linha reta. Em um cruzamento, a linha é mantida nivelada através da rua e uma fileira além dela de cada lado, e a rua é nivelada pela linha, nunca o contrário, fazendo rampa até esse nível em sua própria inclinação. O pavimento mantém a superfície e a via corre sobre ele um bloco acima, ou rente nele quando `villageRailTrackSeat` embute a via no leito, de modo que um carrinho cruza a rua e um aldeão cruza a via. Uma linha perfurada sob uma rua que está seis blocos ou mais acima dela não é cruzada de modo algum: a rua mantém seu próprio declive e passa por cima do túnel. Essa altura é lida do solo da rua suavizado para subir no máximo um bloco por fileira, antes de qualquer cruzamento, poço ou ferrovia o manter.

**Degraus de porta.** O degrau diante de toda porta de um terreno é aterrado com solo onde o solo cai, e um degrau de pedra deixado sobre a água é revestido com `villagePathVergeWaterBlock`.

**Via.** Com `villageRailBlock` vazio, a via é trilho do vanilla voltado ao longo da linha, e `villageRailPowerRun` põe um trilho energizado, ligado, sobre um bloco de redstone a cada tantas fileiras, para que um carrinho percorra toda a linha; uma via embutida no leito não leva trilhos energizados. Um pacote que queira blocos de ferro, barras ou qualquer outra coisa os nomeia: um bloco com eixo, um tronco por exemplo, é voltado ao longo da linha, e qualquer outro é posto como está. Todo bloco de trilho, metrô, estação e esgoto pode levar seu estado entre colchetes, e Misturando blocos acima nomeia as configurações de que uma mistura ponderada é sorteada bloco a bloco.

#### Metrôs de vilas

*vilas e cidades*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageSubwayLines`            | int, 0 ou mais | `0`     | Quantas linhas ferroviárias subterrâneas uma cidade cava. 0 não cava nenhuma e não sorteia nada, de modo que a cidade é traçada exatamente como seria sem elas |
| `villageSubwayDepth`            | int, 6 ou mais | `24`    | A que profundidade sob a superfície fica o leito. A linha é nivelada a partir do solo acima dela, de modo que segue o terreno nessa profundidade em vez de correr nivelada |
| `villageSubwaySpacing`          | int, 1 ou mais | `64`    | A que distância umas das outras as linhas de metrô de uma cidade são mantidas |
| `villageSubwayDirection`        | text           | `any`   | Em que sentido as linhas de metrô correm: ew de leste a oeste, ns de norte a sul, ou any para sortear por cidade |
| `villageSubwayWidth`            | int, 3 ou mais | `3`     | Qual a largura do leito, antes dos acostamentos |
| `villageSubwayBlock`            | block          | vazio   | O bloco da via. Vazio traça trilho do vanilla |
| `villageSubwayTrackSeat`        | string         | `auto`  | Se a via se assenta sobre o leito, nele, ou `auto` para deixar o bloco decidir |
| `villageSubwayBedBlock`         | block          | vazio   | O bloco de que o leito é feito. Vazio usa cascalho |
| `villageSubwayTieBlock`         | block          | vazio   | O bloco posto atravessado no leito como dormentes. Vazio usa tábuas |
| `villageSubwayTieRun`           | int, 1 ou mais | `2`     | A quantos blocos de distância ficam os dormentes |
| `villageSubwayTracks`           | int, 0 ou mais | `0`     | Quantas vias paralelas o leito leva. 0 leva quantas a largura permitir |
| `villageSubwayTrackGap`         | int, 2 ou mais | `2`     | A que distância ficam as vias paralelas |
| `villageSubwayShoulderBlock`    | block          | vazio   | O bloco de cada lado do leito. Vazio não deixa acostamento |
| `villageSubwayShoulderWidth`    | int, 0 ou mais | `1`     | Qual a largura desse acostamento |
| `villageSubwayPowerBlock`       | block          | vazio   | O bloco da via energizada. Vazio usa o trilho energizado do vanilla |
| `villageSubwayPowerBase`        | block          | vazio   | O bloco posto sob uma via energizada para acioná-la. Vazio usa um bloco de redstone |
| `villageSubwayPowerRun`         | int, 0 ou mais | `0`     | A quantos blocos de distância ficam as vias energizadas. 0 não põe nenhuma |
| `villageSubwayTunnelBlock`      | text           | vazio   | O bloco com que a perfuração é revestida: as paredes de cada lado e o teto sobre ela. Vazio cava a perfuração e suas estações sem revestimento |
| `villageSubwayTunnelLightBlock` | block          | vazio   | O bloco embutido no teto do túnel como luz. Vazio não ilumina nada |
| `villageSubwayTunnelLightRun`   | int, 1 ou mais | `8`     | A quantos blocos de distância essas luzes ficam, ancoradas às coordenadas do mundo para que as peças concordem |
| `villageSubwayClimb`            | int, 1 ou mais | `8`     | Quantos blocos uma linha corre antes de poder dar um degrau de um bloco para cima ou para baixo |
| `villageSubwayTail`             | int, 0 ou mais | `48`    | Até onde, além das peças da própria cidade, uma linha corre antes de parar |
| `villageSubwaySurfaces`         | int, 0 a 100   | `25`    | A chance em cem de uma linha de metrô subir à superfície em uma ponta e seguir dali como uma ferrovia comum, túnel atrás e via aberta à frente. A subida leva villageSubwayClimb fileiras por bloco, de modo que uma linha funda gasta um longo trecho para subir. 0 mantém todo metrô enterrado em todo o seu comprimento |

**Saindo à superfície.** `villageSubwaySurfaces` é a chance em cem de uma linha, em vez de permanecer enterrada de ponta a ponta, subir à superfície em uma ponta e seguir dali como uma ferrovia comum: túnel atrás, via aberta à frente. A subida obedece a `villageSubwayClimb`, um bloco por essa quantidade de fileiras, de modo que uma linha de `villageSubwayDepth` de profundidade gasta profundidade vezes subida em fileiras só na rampa e precisa de um bom trecho além dela para merecer o nome; uma linha sem espaço para ambos simplesmente permanece no subsolo. A subida começa a não menos que a ponta mais distante das ruas sob as quais a linha corre, de modo que sai além das ruas da cidade em vez de por entre elas, e um terreno sobre o trecho que sobe lhe dá passagem. As estações são reivindicadas quando a subida está decidida e ficam longe da rampa. Com `villageSubwayTunnelBlock` vazio, a perfuração, suas estações e suas escadas são cavadas sem revestimento.

#### Estações de metrô

*vilas e cidades*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | int, 0 ou mais | `0`                    | Quantos blocos de comprimento tem a câmara de uma estação, centrada na fileira onde a linha passa mais perto do poço. 0 não constrói estação alguma |
| `villageSubwayStationRun`    | int, 0 ou mais | `0`                    | A quantos blocos de distância ficam outras estações ao longo de uma linha, além da mais próxima do primeiro poço da cidade. 0 constrói apenas a do poço |
| `villageSubwayPlatformWidth` | int, 0 ou mais | `3`                    | O quanto a câmara é aberta de cada lado do leito para formar uma plataforma |
| `villageSubwayPlatformBlock` | block          | vazio                  | O bloco com que a plataforma é pavimentada. Vazio a pavimenta com o revestimento do túnel |
| `villageSubwayRailingBlock`  | block          | `minecraft:iron_bars`  | O bloco posto em grade em volta da cabeça das escadas de uma estação onde elas se abrem na rua, para que ninguém caia no poço. Vazio deixa a cabeça sem grade |
| `villageSubwayBenchBlock`    | block          | `minecraft:oak_stairs` | O assento dos bancos postos na plataforma de uma estação e ao lado da cabeça de suas escadas. Um bloco de escada é voltado para longe da linha e se lê como um banco; qualquer bloco serve. Vazio deixa os bancos de fora |
| `villageSubwayBenchEndBlock` | block          | `minecraft:oak_log`    | Os braços em cada ponta de um banco de estação. Vazio deixa o assento nu nas duas pontas |
| `villageSubwayBenchLength`   | int, 0 a 32    | `5`                    | Qual o comprimento de um banco de estação, braços incluídos. `0` deixa os bancos de fora |
| `villageSubwayStation`       | text           | vazio                  | Uma estrutura da pasta structures de um pacote usada como estação: seu poço, suas escadas e sua entrada pela rua. Extraia uma de um mundo construído à mão com #scripts/rdpl-grab-template.py: suas células sólidas são postas e suas células de ar são escavadas, de modo que a forma é a construção e não uma descrição dela. Vazio não constrói estação alguma, e um nome que não pode ser carregado registra um erro e não constrói nenhuma |
| `villageSubwayStationFoot`   | int, 0 a 64    | `4`                    | Quantas camadas na base de uma construção de estação são postas uma vez, antes da parte que se repete. O piso e a porta para a plataforma ficam aqui |
| `villageSubwayStationRepeat` | int, 0 a 64    | `12`                   | Quantas camadas de uma construção de estação se repetem, de modo que uma construção serve para qualquer profundidade: o poço cresce por cópias inteiras dessa faixa e o corredor absorve o que sobra. Precisa ser uma volta inteira da escada, ou os lances não se encontrarão. `0` nunca faz a construção crescer |

**Estações.** Uma linha de metrô só recebe estações quando `villageSubwayStation` nomeia uma construção que carrega: com ele vazio não há câmara, nem escadas, nem entrada, e um nome que não pode ser carregado registra um erro e não constrói nenhuma. Com uma construção nomeada, uma linha recebe uma estação na fileira do primeiro poço da cidade, uma vez definidos `villageSubwayStationLength` e `villageSubwayPlatformWidth`, e outras a cada `villageSubwayStationRun` blocos ao longo dela. Cada uma se desloca até 48 blocos para cada lado para achar um lugar para sua construção ao lado de uma rua que corra ao longo da linha por todo o comprimento desse lugar, longe de toda rua, poço e praça, e a ao menos o comprimento da estação mais sete de uma estação já reivindicada; um terreno sobre esse lugar lhe dá passagem, e uma estação sem tal lugar é deixada de fora. Uma linha que não mantém estação não abre câmara, de modo que nunca leva uma sem acesso. A câmara é mantida nivelada ao longo de seu comprimento: o leito aberto `villageSubwayPlatformWidth` de cada lado, pavimentado com `villageSubwayPlatformBlock`, murado e coberto com o revestimento do túnel, iluminado pelos próprios `villageSubwayTunnelLightBlock` e `villageSubwayTunnelLightRun` do túnel, e murado através da perfuração nas duas pontas. Da plataforma, um corredor corre até a construção da estação, que sobe até a rua ao lado da estrada, nunca por baixo dela; a construção sobe no declive da rua mais próxima em até oito blocos, o declive que essa rua mantém no solo e não o de qualquer tabuleiro ou rampa erguido sobre ela, ou na altura do solo onde não há rua, e é deixada de fora quando a cidade é construída onde isso fica a menos de três blocos sobre a plataforma, onde a construção não consegue fazer a subida nem crescida, ou onde seu corredor até a plataforma passaria de 32 blocos; o log diz qual. O solo entre essa rua e a construção é levado à mesma altura, aterrado onde cai e limpo acima, de modo que a estação se acessa pela estrada. Um banco de `villageSubwayBenchBlock` com braços de `villageSubwayBenchEndBlock`, de `villageSubwayBenchLength` de comprimento, fica na plataforma.

**Construindo a estação à mão.** `villageSubwayStation` nomeia um arquivo de estrutura usado como estação, que é como um pacote traz uma forma que alguém construiu em vez de uma descrita em configurações. Construa-a em um mundo, extraia-a com #scripts/rdpl-grab-template.py e coloque-a com o pacote: seus blocos são postos como construídos, as células de esponja viram o revestimento do túnel, suas células de ar são escavadas, e tudo o que estiver nela, um carrinho ou um suporte de armadura, vem junto. Ela é assentada a partir do canto do lugar da estação. Uma construção serve para qualquer profundidade porque o meio dela se repete: `villageSubwayStationFoot` camadas são postas uma vez na base, levando o piso e a porta para a plataforma, e então cópias inteiras das próximas `villageSubwayStationRepeat` camadas se empilham até a construção alcançar a rua. Essa faixa precisa ser uma volta inteira da escada, ou os lances não se encontrarão onde duas cópias se unem. Um corredor de dois blocos de altura corre da porta até a plataforma, voltando ao longo da câmara onde a queda é longa demais para ir em linha reta; o solo sobre a cabeça da construção é limpo oito blocos acima, uma grade de `villageSubwayRailingBlock` cerca a abertura no nível da rua e um banco fica ao lado dela. Uma construção que não pode ser carregada não constrói estação alguma e registra um erro; uma estação que sua construção não consegue levar até a rua é deixada de fora quando a cidade é planejada, sem câmara, e registra o motivo. A construção traz sua própria abertura para a rua.

#### Ligações ferroviárias

*vilas e cidades*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageRailLinks`             | true/false     | `false` | Liga cidades vizinhas cujas primeiras linhas se encaram através de uma junção. Requer `villageRailLines`, ou `villageSubwayLines` em um pacote sem linhas de superfície |
| `villageRailLinkLeast`         | int, 0 ou mais | `128`   | A menor ligação traçada, ramal mais tronco mais ramal, em blocos |
| `villageRailLinkMost`          | int, 0 ou mais | `1024`  | A maior ligação traçada, ramal mais tronco mais ramal, em blocos |
| `villageRailLinkBridgeMost`    | int, 0 ou mais | `96`    | A maior ponte de que uma ligação pode precisar. Uma ligação sobre água mais larga ou um desnível mais fundo não é traçada |
| `villageRailLinkTunnelMost`    | int, 0 ou mais | `192`   | O maior túnel de que uma ligação pode precisar onde `villageRailTunnelBlock` perfura túneis. Uma ligação que perfuraria mais longe não é traçada |
| `villageRailLinkStation`       | text           | `both`  | A estação em cada ramal logo antes do tronco: `both` põe uma plataforma de cada lado da linha, `one` uma única plataforma à esquerda de um trem que chega ao tronco, `none` não constrói nenhuma |
| `villageRailLinkStationLength` | int, 0 ou mais | `16`    | Quantas fileiras de comprimento têm as plataformas da estação. `0` não constrói estações |
| `villageRailLinkPlatformWidth` | int, 0 ou mais | `3`     | Quantos blocos de largura tem cada plataforma |
| `villageRailLinkPlatformBlock` | block          | vazio   | O bloco de que as plataformas são feitas. Vazio usa tijolos de pedra |

**O que é uma ligação.** As ligações ferroviárias unem cidades vizinhas em uma só rede. As cidades são fundadas uma por célula da grade de cidades (`villageCitySpacing`), e uma ligação corre ao longo da junção entre duas células: a primeira linha de cada cidade segue além de sua cauda como um ramal, em linha reta até a junção, e encontra um tronco traçado ao longo da junção em ângulo reto. O tronco corre de um ramal ao outro e nunca além de nenhum. Requer `villageRailLines`, ou `villageSubwayLines` em um pacote sem linhas de superfície, e vem desativada por padrão.

**Quais cidades se ligam.** Duas cidades se ligam apenas quando estão em células vizinhas, suas primeiras linhas correm no eixo que cruza a junção entre elas, e a ligação inteira, medida de poço a poço ao longo da via, fica entre `villageRailLinkLeast` e `villageRailLinkMost` blocos. Cada parte da decisão é calculada a partir da seed e dos dois locais das cidades, de modo que o resultado é o mesmo qualquer que seja a cidade ou o chunk feito primeiro. Uma ligação que não pode ser construída inteira não é traçada de modo algum, nunca meio construída: uma que precisasse de uma ponte ou túnel mais longo do que as configurações permitem, passasse da borda do mundo, esbarrasse em uma mansão da floresta, deixasse duas cidades mais próximas do que `structureSeparation` permite, ou levasse uma junção perto demais de um canto das células. Um tronco só é traçado em direção a uma cidade que foi realmente fundada: quando um limite como `structureMost` detém a vizinha, ou ela cresce pequena demais para ser mantida, nem metade do tronco nem o ramal além da cauda da própria cidade é construído. Cidades fixadas se ligam da mesma forma, uma por célula; uma célula com duas fixações não liga nenhuma. Outras cidades se mantêm longe do ramal e do tronco de uma ligação conforme crescem, como se mantêm longe umas das outras.

**Declive.** Ramais e troncos são linhas ferroviárias e são nivelados, cobertos por ponte, perfurados e cruzados exatamente como uma linha de cidade, com `villageRailClimb` e as configurações de cavalete e túnel acima. Onde um ramal encontra o tronco, ambos ficam nivelados, e a estação ao lado também.

**A junção.** Um ramal se une apenas à via mais próxima do tronco. Essa via é interrompida onde o meio do ramal a encontra, a via esquerda do ramal se curva para a esquerda nela e a direita se curva para a direita, e a via distante segue reta. Com duas vias, o tronco no alto e o ramal subindo por baixo:

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` é leito e `o` é via. Onde os dois ramais chegariam a poucos blocos um do outro, a primeira linha da segunda cidade se desloca para se alinhar com a primeira, e os dois se encontram em um cruzamento: cada ramal se funde apenas à sua própria via mais próxima exatamente como acima, as duas vias do tronco são interrompidas no centro do ramal, e nenhum trilho cruza outro:

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

Um tronco de via única não tem segunda via para dar ao outro ramal, de modo que uma ligação cujos ramais se encontrariam de frente sobre uma via única não é traçada. Com uma via única, a via do ramal se curva para dentro da via do tronco rumo à esquerda, e a via do tronco além dessa curva termina contra ela. As curvas são postas com suas formas fixas, de modo que o trilho do vanilla só faz curva onde o cruzamento é desenhado e em nenhum outro lugar.

**Estações.** As últimas fileiras de um ramal antes da junção são uma estação: plataformas de `villageRailLinkPlatformBlock` niveladas com o trilho, com grade ao longo da borda externa com `villageSubwayRailingBlock`, com um banco de `villageSubwayBenchBlock` na metade de cada plataforma.

**Metrôs.** Em um pacote apenas com linhas de metrô, a ligação leva a primeira linha de metrô de uma cidade. A linha sobe do solo em direção ao tronco, com a rampa de `villageSubwayDepth` vezes `villageSubwayClimb` fileiras de comprimento, e alcança a estação e a junção na superfície; uma cidade desse tipo se liga por um lado apenas, o da ligação mais curta, e o tronco é uma ferrovia de superfície construída com as configurações `villageRail`. Onde um ramal não tem espaço para essa rampa e sua estação, o tronco desce até o metrô: a ligação inteira, ramais e tronco, permanece subterrânea em `villageSubwayDepth`, é construída com as configurações `villageSubway` e se encontra na mesma junção, sem estação.

#### Decoração de vilas

*vilas e cidades*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageDecor` | list | vazio   | Decoração espalhada ao longo das ruas da cidade, como pares nome=peso que nomeiam worldgen de um pacote, mypack:street_flowers=2. O nome empty é a parcela de pontos deixados nus, e uma entrada que não esteja escrita nome=peso é deixada de fora. A cada terceiro bloco de margem de cada lado de uma rua sorteia-se a lista, no solo ali por mais alto que esteja, mas não em um túnel, sob um terreno, em uma praça ou a menos de dois blocos de uma porta. Vazio não espalha nada |

### Estruturas

*o que cada grupo faz*

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

`control.structures` decide este grupo. Estruturas vanilla desativadas, seu espaçamento, separação, distância do spawn, biomas, spawns, fixações e adaptação do terreno.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `structureSpacing` | lista | vazio | A distância entre as estruturas vanilla ao serem geradas, em chunks, como entradas estrutura=chunks: os nomes do 1.12.2 temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities e villages, ou qualquer id de conjunto de estruturas, como pillager_outposts. Em mineshafts o número é um chunk em tantos; em strongholds é a distância entre anéis. As fortalezas do Nether mantêm sua própria grade, que o espaçamento de netherbridges não alcança |
| `structureSeparation` | lista | vazio | A menor distância permitida entre duas estruturas do mesmo tipo, em chunks, como entradas estrutura=chunks; em strongholds é a largura do anel. Temples, mineshafts e netherbridges mantêm sua própria separação, que isto não alcança. Em monuments, uma separação igual ao espaçamento ou maior é reduzida para uma unidade a menos que o espaçamento, e o log avisa |
| `structureMost` | lista | vazio | O máximo de vilarejos que uma dimensão pode ter, como villages=quantidade (por exemplo villages=100); outras estruturas não têm limite: depois que essa quantidade é fundada, nenhum chunk funda mais um, exceto os chunks fixados com structureAt. 0 ou uma entrada ausente não define teto |
| `structureSpawners` | lista de `structure=entity` | vazio | O que o gerador de monstros (spawner) dentro de uma estrutura vanilla gera, separado por vírgulas para um sorteio por spawner. Os quatro que colocam um são dungeons, mineshafts, fortalezas do Nether e strongholds |
| `structureMinDistanceFromSpawn` | lista | vazio | A distância do spawn do mundo em que uma estrutura começa, em blocos, como entradas estrutura=blocos. Medida a partir do ponto de spawn do mundo; enquanto um mundo novo ainda escolhe o seu, a partir do worldSpawn do pacote, se houver, ou da origem do mundo |
| `structureBiomes` | lista | vazio | Onde uma estrutura pode ser gerada, como entradas estrutura=bioma,bioma com ids de bioma, os nomes que o jogo mostra, como Floresta de Bétulas, nomes vanilla simples, como desert, ou tipos de bioma, como SANDY |
| `structureBiomesAreBlacklist` | lista de `structure=true` ou `structure=false` | vazio | O sentido da lista de biomas de cada estrutura |
| `structureSpawns` | lista | vazio | Os mobs que uma estrutura gera seja qual for o bioma, como entradas estrutura=namespace:entidade:peso:mínimo:máximo, separadas por vírgulas. A lista substitui por inteiro a lista de mobs da própria estrutura, seja qual for o tipo de cada mob; uma lista vazia depois do = não gera nada |
| `structureAt` | lista de `structure=x,z` | vazio | Fixa uma estrutura em um ponto exato. Veja [Estruturas em locais exatos](#estruturas-em-locais-exatos) |
| `structureAdaptation` | lista | mansions `beard_thin`; toda outra estrutura mantém sua adaptação vanilla | Como o terreno se adapta a uma estrutura, como entradas estrutura=modo com os modos none, bury, beard_thin, beard_box e encapsulate |
| `terrainAdaptation` | booleano | `false` | Assenta as ruas de cidade próprias do RDPL, encaixadas no terreno em vez de sobre palafitas em cada depressão, e lê com elas as opções villagePath e villageRail. Altera o terreno, portanto um mundo criado com isto ligado difere de um criado sem. As cidades são geradas conforme villageCitySpacing define, e com 0 não há nenhuma |
| `villagePieces` | lista | vazio | Lotes de vilarejo nomeados aqui, um por linha, pelo id completo de um arquivo de villages, como mypack:smithy, pelo nome simples ou pelo nome da estrutura que um lote de modelo constrói. Enquanto villagePiecesAreBlacklist estiver ligado, uma estrutura nomeada aqui também fica vazia onde quer que o jogo a carregue, inclusive as casas de vilarejo do próprio jogo, como minecraft:village/plains/houses/plains_small_house_1 |
| `villagePiecesAreBlacklist` | booleano | `true` | Ligado, os lotes em villagePieces são bloqueados. Desligado, apenas esses lotes são construídos |
| `villageBlocks` | lista | vazio | Blocos com que os lotes de vilarejo são construídos, como pares original=substituto, minecraft:cobblestone=mypack:ruby_brick. Qualquer lado pode levar um estado entre colchetes, que o original então precisa corresponder exatamente. Um par pode acrescentar uma chance em 100, minecraft:cobblestone=minecraft:mossy_cobblestone,20, sorteada a partir da seed do mundo no ponto onde o bloco é colocado, at=bloco para valer só onde esse bloco está, e under=bloco só sobre ele. Pares sem chance ou condição são aplicados primeiro, de modo que um par condicional pode desgastar o resultado deles. Vale para fazendas e para as casas de vilarejo do próprio jogo que uma cidade constrói; ruas, poços, lampiões e as estruturas dos seus lotes de modelo nunca são afetados. Vazio deixa cada bloco como foi colocado |

### Spawn

*o que cada grupo faz*

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

`control.spawning` decide este grupo. Limites de spawn de mobs, taxas de spawn de hostis e o limite de luz.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | número, 0.0 a 4.0 | `1.0` | Multiplicador do spawn de hostis na superfície durante o dia, sendo `1.0` o vanilla, de modo que o spawn na superfície à luz do dia possa ser desligado sem mexer nas cavernas |
| `surfaceNightMonsterRate` | número, 0.0 a 4.0 | `1.0` | O mesmo para a superfície à noite |
| `undergroundDayMonsterRate` | número, 0.0 a 4.0 | `1.0` | O mesmo no subsolo durante o dia |
| `undergroundNightMonsterRate` | número, 0.0 a 4.0 | `1.0` | O mesmo no subsolo à noite |
| `monsterCap` | int, -1 a 1000 | `-1` | Quantos hostis podem estar carregados ao mesmo tempo. O vanilla é 70, e `-1` não mexe nisso |
| `creatureCap` | int, -1 a 1000 | `-1` | O mesmo para animais passivos. O vanilla é 10 |
| `ambientCap` | int, -1 a 1000 | `-1` | O mesmo para morcegos e afins. O vanilla é 15 |
| `waterCreatureCap` | int, -1 a 1000 | `-1` | O mesmo para lulas. O vanilla é 5 |
| `monsterSpawnLight` | int, -1 a 15 | `-1` | A luz de bloco mais forte em que um mob hostil ainda pode nascer, além das verificações vanilla. -1 mantém apenas a regra vanilla. Spawners não são afetados |
| `threatItems` | lista | vazio | Itens que elevam o nível de ameaça de um jogador, como entradas item=nível,quantidade com um ,each ou ,batch opcional no fim, por exemplo minecraft:diamond_sword=5,1 ou minecraft:diamond=1,16,batch. Each, o padrão, soma o nível por cada item carregado, contando no máximo a quantidade informada; batch soma o nível uma vez para cada grupo completo de quantidade itens carregados. Uma quantidade acima do tamanho da pilha do item é reduzida ao tamanho da pilha. Toda entidade carregada que leva itens é portadora: o inventário principal, a armadura e a mão secundária de um jogador, uma pilha largada, qualquer coisa com inventário de itens, como uma mula com baú ou um carrinho com baú, e os itens segurados e a armadura de outros mobs. Vazio desliga o nível de ameaça. Com `control.spawning` em `off`, as configurações de ameaça da própria config ainda valem |
| `threatLevels` | lista | vazio | As pontuações em que cada faixa começa, crescentes, de modo que `10, 25, 50` forma três faixas. Vazio desliga o nível de ameaça |
| `threatMost` | int, -1 a 100000 | `-1` | Limita a pontuação. `-1` deixa sem limite |
| `threatSpawnRate` | número, 0.0 a 8.0 | `1.0` | Escala o spawn de hostis a até 128 blocos de uma portadora na faixa mais alta, somado às outras taxas, com as faixas mais baixas recebendo uma parte proporcional |
| `threatNotice` | número, 0.0 a 64.0 | `0.0` | Quantos blocos mais longe os mobs hostis, inclusive os vanilla, enxergam uma portadora na faixa mais alta, também repartidos entre as faixas mais baixas |
| `threatSays` | lista de `band=message` | vazio | As linhas mostradas em amarelo quando a faixa do próprio jogador muda, sendo a faixa `0` a linha para quando ele volta a ficar abaixo da primeira faixa |

### Bedrock

*o que cada grupo faz*

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

`control.bedrock` decide este grupo. A bedrock plana e suas listas de dimensões e biomas.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `flatBedrock` | booleano | `false` | Substitui a bedrock irregular do fundo do mundo por camadas planas. Só em chunks novos, a menos que `flatBedrockRetrogen` esteja ligado |
| `flatBedrockDimensions` | lista | `["minecraft:overworld"]` | As dimensões em que aplainar. Vazio significa todas |
| `flatBedrockDimensionsAreBlacklist` | booleano | `false` | Ligado, o aplainamento ignora as dimensões listadas. Desligado, vale apenas para elas |
| `bedrockLayers` | int, 1 a 5 | `1` | Quantas camadas de bedrock restam |
| `flatBedrockBiomes` | lista de nomes de bioma | vazio | Os biomas em que aplainar, pelo nome amigável ou de registro. Vazio significa todos os biomas |
| `flatBedrockBiomesAreBlacklist` | booleano | `false` | Ligado, o aplainamento ignora os biomas listados. Desligado, vale apenas para eles |
| `flatBedrockRoof` | booleano | `false` | Aplaina também o teto de bedrock, onde uma dimensão tem um, como o teto do Nether |
| `flatBedrockFiller` | bloco | vazio | O que substitui a bedrock removida. Vazio escolhe por dimensão: pedra, netherrack, pedra do End |
| `flatBedrockFillers` | lista de `dimension=block` | `["minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"]` | Um preenchimento por dimensão, que substitui `flatBedrockFiller` nas dimensões nomeadas |
| `flatBedrockBiomeTypes` | lista | vazio | Tipos de bioma em que aplainar a bedrock, junto com flatBedrockBiomes, por tag de bioma, como minecraft:is_ocean, ou por nome de tipo do 1.12.2, como OCEAN. flatBedrockBiomesAreBlacklist vale para eles também |
| `flatBedrockRetrogen` | booleano | `false` | Aplaina também a bedrock de chunks que já existem. Cada chunk é feito uma vez e guarda essa marca, e não é possível desfazer: o padrão original não é registrado em lugar nenhum |
| `flatBedrockRetrogenKey` | texto | `0000` | Mude para tornar todos os chunks elegíveis de novo para o aplainamento da bedrock |

### Ticks lentos à distância

*o que cada grupo faz*

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

`control.entities` decide este grupo. O ritmo mais lento das entidades longe de todos os jogadores.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `slowDistantEntities` | booleano | `true` | Processa com menos frequência os ticks das entidades longe de todos os jogadores. Nada deixa de receber ticks, apenas os recebe em ritmo mais lento |
| `slowedKinds` | lista | `["items", "experience"]` | Quais tipos recebem menos ticks: items, experience, projectiles, sendo estes últimos flechas, tridentes, bolas de neve, ovos, poções, frascos de experiência e pérolas do End arremessados, e a cuspida de lhama. Tudo que pensa por conta própria é sempre desacelerado, sem precisar ser nomeado aqui: escolhe o que fazer em seguida com menos frequência, mas continua se movendo a cada tick. Máquinas nunca são desaceleradas |
| `slowDistance` | int, 64 a 4096 | `192` | A que distância do jogador mais próximo, em blocos, um chunk passa a ser desacelerado. O jogo deixa de informar ao jogador a maioria das entidades além de 64, então nada abaixo disso |
| `slowRate` | int, 1 a 20 | `4` | Um tick em tantos é dado a um chunk desacelerado. 1 é nenhuma desaceleração, 20 é uma vez por segundo |
| `neverSlowed` | lista | vazio | Entidades deixadas em paz por mais longe que estejam, como namespace:nome |
| `slowRecheck` | int, 1 a 100 | `20` | Com que frequência, em ticks, a distância até o jogador mais próximo é calculada de novo. Cada jogador conta por si, de modo que quem está sozinho e longe ainda tem seu próprio espaço tranquilo ao redor |

### Terreno, retenções e o que o mod diz

*o que cada grupo faz*

`control.chunks` decide este grupo. O raio de chunks do spawn, a pré-geração, o retrogen e o reset, as linhas de boas-vindas, o cartão de falas (says) e os toasts do jogo.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `retrogen` | booleano | `false` | Atualiza chunks existentes com as entradas de worldgen com \"retrogen\": true. Desligado, os chunks que já existem são deixados em paz. Os chunks são marcados ao serem gerados de qualquer forma, então ligar isto depois só afeta chunks mais antigos que o pacote |
| `adoptExistingChunks` | booleano | `false` | Trata os chunks que já existem como se este pacote os tivesse gerado, marcando-os em vez de deixá-los para o retrogen. Ligue ao substituir um mod que já gerava o mesmo minério, para que o retrogen nunca o duplique. Entradas de worldgen adicionadas depois ainda fazem retrogen neles |
| `saysCard` | booleano | `false` | Mostra as linhas que este mod diz, as boas-vindas, o aviso de criação de terreno que um jogador que entra no meio do processo recebe e o fim do processo (o progresso em andamento continua na barra de ações), e as linhas de ameaça, como um cartão no canto inferior direito em vez de no chat. O cartão desliza para dentro, fica oito segundos e some aos poucos, e aparece também sobre uma tela aberta |
| `saysIcon` | texto | vazio | Um item desenhado no cartão, por exemplo minecraft:compass. Vazio não desenha nenhum |
| `saysColor` | texto | vazio | A cor de fundo do cartão em hexadecimal, por exemplo 1E2630. Vazio usa um cinza-ardósia escuro |
| `saysImage` | texto | vazio | Um PNG dos assets de cliente do pacote esticado sobre o cartão como fundo, por exemplo rubyworld:textures/gui/card.png, desenhado sobre a cor. Vazio não desenha nenhum |
| `saysBackground` | booleano | `true` | Desenha o painel, a borda e a faixa de cor do cartão, e o fundo escuro atrás das boas-vindas e dos avisos de retenção. Desligado, resta só o texto, que mantém a sombra, e o saysImage, se houver um |
| `saysFont` | texto | vazio | Uma fonte para o texto do cartão, nomeada como namespace:nome, por exemplo rubyworld:runes para o assets/rubyworld/font/runes.json do pacote. Vazio usa a fonte do RDPL |
| `toasts` | booleano | `false` | Mostra os toasts do jogo, os pop-ups no canto superior direito de avanços, receitas desbloqueadas, dicas do tutorial e avisos do sistema, inclusive os toasts de outros mods. Desligado, não mostra nenhum. Vale a partir do próximo mundo ou servidor em que se entrar. Um modelo de mundo pode listar os tipos a mostrar |
| `pregenOnNewWorld` | int, 0 a 8192 | `0` | Até onde ao redor do spawn, em chunks, um mundo tem seu terreno criado antes de alguém jogar nele. O jogo cria 12 chunks ao redor do spawn por conta própria, então 12 é o piso e 0 significa esse piso, e não nada: o terreno que o jogo criaria de qualquer forma é adotado e iluminado em uma única passagem organizada, em vez de chegar aos poucos. Aumente para ir além do que o jogo faz |
| `pregenToBorder` | booleano | `false` | Se um mundo novo tem seu terreno criado até a borda do mundo em vez de um número fixo de chunks, centrado na borda e não no spawn. Um mundo cuja borda nunca foi reduzida não tem borda a alcançar e é ignorado |
| `pregenAllDimensions` | booleano | `false` | Cria o terreno de todas as dimensões que o servidor tem, inclusive as de mods, a overworld primeiro e as demais por ordem de id, em vez de apenas as de pregenDimensions. As nomeadas em pregenDimensionsWhenEntered continuam reservadas ao primeiro visitante |
| `pregenResume` | booleano | `false` | Se um processo que foi interrompido ou cortado retoma de onde parou na próxima vez que o mundo for carregado, em vez de recomeçar |
| `pregenChunksInFlight` | int, 1 a 512 | `32` | Quantos chunks um processo de criação de terreno pede ao jogo de uma vez. Mais mantém as threads de geração mais ocupadas e o servidor menos responsivo para quem está retido assistindo |
| `pregenBackup` | booleano | `false` | Copia o mundo para um backup intocado quando a pré-geração termina, enquanto os jogadores ainda estão retidos. A cópia é o que um reset restauraria, e uma cópia cujos pacotes não correspondem mais é descartada e refeita |
| `resetClearsEntities` | booleano | `true` | Remove toda entidade que não seja jogador quando o mapa é resetado |
| `resetClearsScores` | booleano | `true` | Volta a zero todo objetivo que o pacote mantém quando o mapa é resetado, de modo que uma nova partida comece do zero. As equipes em si são mantidas |
| `resetClearsInventory` | booleano | `false` | Esvazia o inventário de todos os jogadores, armadura e mão secundária inclusive, quando o mapa é resetado |
| `resetClearsExperience` | booleano | `false` | Volta a experiência de todos os jogadores ao nível zero quando o mapa é resetado |
| `spawnChunkRadius` | int, 0 a 1024 | `128` | A que distância do ponto de spawn, em blocos, os chunks são mantidos carregados haja ou não um jogador lá, arredondada para chunks inteiros como (blocos + 8) / 16 em cada direção, de modo que 128 mantém 8 chunks em cada direção. Quando um mundo inicia, a overworld prepara um quadrado 4 chunks mais largo em cada direção antes de o servidor ficar pronto, 25 por 25 chunks com 128. 0 não prepara nem mantém nenhum, então a área do spawn é descarregada como qualquer outra. O RDPL mantém os chunks com seus próprios tickets, então no 1.21.1 a game rule spawnChunkRadius não faz nada enquanto esta chave estiver em vigor |
| `spawnChunkRadii` | lista | vazio | Um raio para a overworld escrito como dimensão=blocos, como em minecraft:overworld=64, que substitui spawnChunkRadius. Só a overworld tem chunks de spawn, então uma entrada de qualquer outra dimensão não muda nada |
| `welcomeSays` | lista | `[WELCOME]` | Linhas de boas-vindas, mostradas em verde a cada login e após a pré-geração. Uma entrada simples é a linha para todos os lugares; uma entrada dimensão=mensagem a substitui para essa dimensão e também recebe cada chegada ali, por exemplo minecraft:the_nether=Welcome to the Nether!. Uma mensagem vazia depois do = silencia essa dimensão; uma lista vazia não mostra nada. No padrão, fala o idioma de cada jogador |
| `pregenBorderLimit` | int, 1 a 1875000 | `8192` | O mais longe que uma borda pode chegar, em chunks para cada lado, antes de a criação de terreno até ela ser recusada. Serve para impedir que um engano rode por semanas, não para ser aumentado, e um pacote não pode defini-lo. Um quadrado de 8192 tem 268 milhões de chunks **Somente na config.** |
| `pregenDimensions` | lista | `["minecraft:overworld"]` | Em quais dimensões um mundo novo tem seu terreno criado, por id, na ordem dada, uma após a outra |
| `pregenDimensionsWhenEntered` | lista | vazio | Dimensões cujo terreno é criado não de antemão, mas na primeira vez que alguém pisa nelas, com o mesmo alcance, retendo todos da mesma forma até terminar. Uma nomeada aqui e em pregenDimensions é simplesmente criada de antemão |
| `pregenRunningSays` | texto | `World pregeneration running, %d%% done` | A mensagem de progresso que os jogadores veem enquanto o mundo é gerado, onde %d é a porcentagem e um segundo %s a dimensão. Vazio não diz nada a eles. No padrão, fala o idioma de cada jogador |
| `pregenFinishedSays` | texto | `World pregeneration finished` | A mensagem que os jogadores veem quando a geração termina. Vazio não diz nada a eles. No padrão, fala o idioma de cada jogador |
| `pregenStoppedSays` | texto | `World pregeneration stopped` | A mensagem que os jogadores veem quando a geração é interrompida antes do fim. Vazio não diz nada a eles. No padrão, fala o idioma de cada jogador |
| `pregenSpectatingSays` | texto | `Spectating until the world is ready` | A mensagem no meio da tela que os jogadores veem enquanto ficam retidos em espectador durante a geração do mundo. Vazio não mostra nada. No padrão, fala o idioma de cada jogador |
| `pregenLogo` | texto | `center` | Onde o logo fica quando a pré-geração termina: left, center ou right, acima do texto no meio da tela. É sempre mostrado; uma palavra desconhecida é lida como center |
| `pregenBackupSays` | texto | `Pack requested world backup` | A mensagem no meio da tela que os jogadores veem enquanto esse backup é copiado. Vazio não mostra nada |
| `resetSays` | texto | `Pack requested map reset` | A linha no meio da tela mostrada aos jogadores enquanto /rdpl reset restaura o mapa. Vazio reseta em silêncio |
| `resetSendsTo` | texto | `spawn` | Para onde os jogadores são levados por um reset: spawn, uma posição como x,y,z, ou dimensão:x,y,z para enviá-los a outro mundo |
| `resetRuns` | texto | vazio | Uma função executada depois que um reset limpou o mapa, nomeada namespace:caminho. Vazio não executa nada |

### Mundo vazio

*o que cada grupo faz*

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

`control.voidWorld` decide este grupo. A geração de mundo vazio e sua plataforma.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `voidWorld` | booleano | `false` | Gera as dimensões listadas como espaço vazio com uma plataforma no ponto de spawn e nada vivo, pelo preset gerado para as três vanilla e pelos arquivos de dimensão próprios de um pacote; qualquer outra dimensão listada é esvaziada à medida que seu terreno é criado. A escolha é guardada com o mundo quando ele é criado, então ligar ou desligar depois deixa um mundo existente como estava |
| `voidWorldDimensions` | lista | `["minecraft:overworld"]` | Quais dimensões se tornam vazias, por id. Vazio significa nenhuma, ou todas as dimensões quando voidWorldDimensionsAreBlacklist está ligado |
| `voidWorldDimensionsAreBlacklist` | booleano | `false` | Ligado, as dimensões listadas são as deixadas em paz |
| `voidPlatformBlock` | bloco | `minecraft:stone` | De que a plataforma é feita |
| `voidPlatformHeight` | int, -2032 a 2031 | `64` | O y em que a plataforma do mundo vazio fica |
| `voidPlatformSize` | int, 1 ou mais | `9` | A largura da plataforma, arredondada para baixo até um número ímpar para que fique centrada no spawn |
| `voidWorld` | texto | `default` | A geração de mundo vazio e sua plataforma [default\|global\|off] |

### O dragão

*o que cada grupo faz*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `dragonFight` | booleano | `true` | Se a coisa toda acontece ou não: o dragão, sua barra, os cristais, a fonte sobre a qual ele fica e o renascimento que um jogador iniciaria com cristais do End. Pertence ao grupo `structures` |

`dragonFight` pertence ao grupo `structures` e decide se a coisa toda acontece ou não: o dragão, sua barra, os cristais, a fonte sobre a qual ele fica e o renascimento que um jogador iniciaria com cristais do End. Um End esvaziado fica sem ele, a menos que um pacote o peça, e um End comum o tem, a menos que um pacote diga o contrário, então vale a pena definir `dragonFight` nos dois sentidos.

### Terreno

*o que cada grupo faz*

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

`control.terrain` decide este grupo. O nome e a seed do mundo na criação, generatorOptions, as regiões de caverna, a altura das nuvens e as junções entre mundos.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `worldSeed` | string | vazio | A seed com que todo mundo novo é criado, escrita como seria digitada: um número é usado como está, e qualquer outra coisa é convertida em um como o jogo faz. Um servidor dedicado cria seu mundo com ela também e a grava em `server.properties` como `level-seed`. Vazio deixa a escolha em paz |
| `worldName` | texto | vazio | Como um mundo novo se chama quando a tela de criação abre. Vazio deixa como o jogo o nomeia |
| `worldType` | texto | vazio | O tipo de mundo sobre o qual o mundo moldado é construído, um entre default, largebiomes, amplified ou flat, com os nomes do 1.12.2 customized e default_1_1 lidos como default; flat é uma overworld superplana construída a partir das camadas de generatorOptions, com as cidades do pacote sobre ela. A forma abaixo (alturas, pedra profunda, nível do mar, bedrock, vazio) é gerada como um preset de mundo próprio, listado em Tipo de mundo na tela do mundo e escolhido ali seja qual for a escolha feita. Um servidor dedicado o grava em `server.properties` como `level-type`, nomeando esse preset, ou o preset do próprio jogo quando nada é moldado, a menos que `level-type` já nomeie um dos worldTypeExceptions. Vazio constrói sobre default |
| `worldTypeExceptions` | lista | `["flat", "debug_all_block_states"]` | Tipos de mundo que um jogador escolhe e que o preset gerado deixa em paz, como flat ou debug_all_block_states. Vazio significa que toda escolha é substituída |
| `generatorOptions` | texto | vazio | As configurações de terreno da overworld como um objeto JSON, as chaves que o tipo de mundo customized do 1.12.2 escrevia. Lidas aqui: seaLevel, useLavaOceans, fixedBiome, e useCaves, useRavines, useDungeons, useLavaLakes, useStrongholds, useVillages, useMineShafts, useTemples, useMonuments e useMansions definidos como false. Com worldType flat são as camadas, de baixo para cima, como no texto superplano do 1.12.2 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village ou uma lista de camadas; o número depois das camadas é o bioma, e village, biome_1, mineshaft, stronghold, oceanmonument, lava_lake e decoration depois dele os ativam. Aplicadas a um mundo apenas quando ele é criado. Um servidor dedicado as grava em `server.properties` como `generator-settings`, e as camadas planas como o JSON flat do próprio jogo, a menos que `level-type` já nomeie um dos worldTypeExceptions. Vazio deixa o terreno como o tipo de mundo o faz |
| `worldMinHeight` | int, -2032 a 2016 | `-64` | O bloco mais baixo da overworld, um múltiplo de 16 até -2032. O fundo do próprio jogo é -64; mais baixo cria um mundo profundo sob o terreno vanilla, de pedra maciça até a camada de worldgen escavá-lo ou noiseCaves levar as cavernas do jogo para baixo. Aplicado apenas pelo preset gerado |
| `worldMaxHeight` | int, -2016 a 2032 | `320` | O bloco acima do topo da overworld, um múltiplo de 16 até 2032, no máximo 4064 acima de worldMinHeight. O topo do próprio jogo é 320; mais alto deixa céu aberto acima do terreno vanilla |
| `deepStone` | texto | vazio | O bloco de que o mundo abaixo do terreno vanilla é feito quando worldMinHeight vai abaixo de -64, como a deepslate própria de um pacote. Mistura-se à deepslate ao longo das oito camadas abaixo de -64, como a deepslate se mistura à pedra. Vazio mantém pedra |
| `noiseCaves` | texto | `off` | Onde as cavernas, túneis, noodles e aquíferos do jogo continuam quando worldMinHeight vai abaixo de -64: off mantém o mundo sob o terreno vanilla como pedra profunda maciça para a camada de worldgen escavar, deep os leva até o fundo, com os lagos de lava movidos para as dez camadas mais baixas, e world significa o mesmo nesta versão, porque o terreno vanilla já os tem |
| `worldSpawn` | texto | vazio | Onde todo mundo novo nasce, escrito como x,z ou x,y,z. Sem y, usa-se o nível médio do solo do mundo, uma unidade acima do nível do mar, ou o topo das camadas em um mundo flat, e o jogo então encontra um chão seguro ali, como faz com qualquer spawn. Aplicado a um mundo apenas quando ele é criado. Vazio deixa a escolha para o jogo |
| `worldBorder` | int, 0 a 60000000 | `0` | A largura, em blocos, da borda do mundo em todo mundo novo. Aplicada a um mundo apenas quando ele é criado. 0 deixa a borda onde o jogo a coloca |
| `worldTime` | int, -1 a 23999 | `-1` | Trava a hora do dia da overworld, em ticks, o mesmo valor que /time set aceita, então 18000 é meia-noite. O relógio para e nunca se move: /time set não consegue movê-lo, o dia continua contando por baixo, e remover a configuração devolve essa hora. -1 deixa o tempo correr |
| `caveRegionPlainWeight` | int, 0 ou mais | `4` | O peso do subsolo comum, sem região, frente aos pesos próprios das regiões de caverna. Mais alto deixa mais do subsolo sem nenhuma região |
| `caveRegionCells` | int, 16 ou mais | `128` | A largura de uma célula de região de caverna em blocos. As regiões de caverna dos pacotes são pintadas sobre o subsolo em células desse tamanho aproximado |
| `caveRegionCellsY` | int, 16 ou mais | `64` | A altura de uma célula de região de caverna em blocos |
| `worldGravity` | lista | vazio | Escala a gravidade, como um multiplicador do vanilla, em que 1.0 não muda nada e 0.17 é como a da Lua, para toda entidade. Um valor simples vale para todas as dimensões, e uma entrada escrita como dimensão=valor vale só para essa dimensão e prevalece sobre o simples. Vazio deixa a gravidade em paz |
| `worldFallDamage` | lista | vazio | Escala o dano de queda do mesmo modo, 0.5 reduzindo-o à metade e 2.0 dobrando-o |
| `worldJumpStrength` | lista | vazio | Escala a força do pulo do mesmo modo, 1.5 pulando uma vez e meia mais alto |
| `worldTerminalVelocity` | lista | vazio | Escala a maior velocidade com que um mob ou jogador cai do mesmo modo, 0.5 caindo à metade da velocidade máxima do vanilla |
| `weatherCeiling` | lista | vazio | O maior y que a chuva e a neve alcançam, como entradas dimensão=y. Um número simples vale para todas as dimensões. Acima dele a chuva não cai, a neve não se acumula, os caldeirões não enchem, raios não caem e nenhuma precipitação é desenhada; abaixo dele o clima não muda. O gelo depende da temperatura e não da precipitação, então ainda se forma acima da linha. Vazio significa sem teto |
| `cloudHeight` | lista | vazio | O y em que as nuvens são desenhadas, como entradas dimensão=y. Um número simples vale para todas as dimensões. Prevalece sobre o `cloudHeight` próprio de uma dimensão de pacote. Vazio mantém a altura de nuvens do próprio jogo, 192 na overworld |
| `worldBelow` | lista | vazio | Empilha outra dimensão sob esta: cair pelo fundo do mundo leva você à dimensão nomeada, chegando sob o teto dela no mesmo x e z, ainda caindo. As entradas são escritas como dimensão=destino, como minecraft:overworld=minecraft:the_nether para pendurar o Nether sob a overworld; um id simples vale para todas as dimensões. Cavar até atravessar exige que a bedrock do piso seja removida, o que worldSeamBedrock decide. Vazio significa que o piso continua sendo piso |
| `worldAbove` | lista | vazio | O mesmo para o teto: subir além do topo do mundo leva você à dimensão nomeada, chegando acima do piso dela. Escrito do mesmo modo que worldBelow |
| `worldSeamEntities` | booleano | `true` | Se itens largados, mobs e outras entidades também atravessam as junções entre mundos, ou só os jogadores. Cavaleiros e montarias atravessam um de cada vez |
| `worldSeamBedrock` | booleano | `false` | Mantém a bedrock no limite de uma junção mesmo assim. Desligado, uma dimensão cujo piso ou teto tem uma junção worldBelow ou worldAbove não gera bedrock ali, de modo que o caminho possa ser escavado. Chunks já gerados mantêm o que têm |

### Servidor

*o que cada grupo faz*

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

`control.server` decide este grupo: as linhas do `server.properties` que um pacote pode definir, com o modo de jogo, a dificuldade e os comandos em um mundo aberto para LAN. Em um servidor dedicado, todo valor que um pacote define aqui é gravado no `server.properties` quando o servidor inicia, de modo que o arquivo mostra o que está em vigor, e os valores que o servidor já leu também são aplicados a ele. Um mundo de um jogador usa o que um servidor integrado tem, como cada linha indica. Vazio, ou `-1` para um número, mantém o valor do próprio servidor, e com `control.server` em `off` todas as linhas ficam como o servidor as tem.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `worldGameMode` | texto | vazio | Em que modo todo mundo novo é iniciado: sobrevivência, hardcore, criativo, aventura ou espectador (survival, hardcore, creative, adventure ou spectator). Hardcore é sobrevivência em que a morte encerra o mundo, para o save inteiro, igual à escolha na tela de mundo. Vazio mantém o que quem criou o mundo escolheu. A tela de mundo oferece apenas sobrevivência, hardcore e criativo, então aventura e espectador são definidos na criação do mundo. Um servidor dedicado define todo mundo no modo do seu server.properties a cada início, portanto ali o modo do pacote é gravado no server.properties (gamemode e hardcore) antes de o mundo carregar |
| `privacy` | booleano | `true` | Se a telemetria e as denúncias de chat do jogo ficam desativadas: nenhum evento de telemetria é enviado, o cliente não assina nenhuma mensagem de chat, o servidor não mantém sessão de chat nem exige uma, então nenhuma mensagem pode ser denunciada. Um pacote que omite a chave recebe a opção de config `privacy` da categoria `tweaks`. Vale a partir do próximo mundo ou servidor em que se entrar |
| `worldLanCommands` | booleano | `true` | Se um jogador que abre um mundo de um jogador para LAN pode ativar os comandos para todos que entrarem. `false` deixa o botão Permitir comandos da tela Abrir para LAN cinza e o mantém desligado, e o mundo é aberto sem comandos seja qual for o pedido, `/publish` inclusive |
| `worldDifficulty` | lista | vazio | Trava a dificuldade, uma entre pacífico, fácil, normal ou difícil (peaceful, easy, normal ou hard). Uma dificuldade sozinha vale para todas as dimensões, e uma entrada escrita como dimensão=dificuldade, como minecraft:the_nether=hard, vale só para essa dimensão e prevalece sobre a sozinha. A configuração do próprio mundo é deixada como estava e volta quando a entrada é removida. Um servidor dedicado grava a dificuldade do Overworld no `server.properties` como `difficulty`. Vazio mantém a escolhida |
| `worldForceGameMode` | booleano | vazio | Se um jogador que entra é recolocado no modo de jogo do servidor toda vez, a linha `force-gamemode`. Um mundo aberto para LAN já faz isso, e `false` o impede ali também |
| `worldPvp` | booleano | vazio | Se os jogadores podem se ferir, a linha `pvp`. Um mundo de um jogador também a respeita |
| `worldFlight` | booleano | vazio | Se um jogador voando em sobrevivência é deixado em paz em vez de expulso, a linha `allow-flight`. Um mundo de um jogador também a respeita |
| `worldSpawnProtection` | int, -1 ou mais | `-1` | Quantos blocos ao redor do ponto de spawn só operadores podem construir, a linha `spawn-protection`, 0 para nenhum. Só um servidor dedicado protege o seu spawn |
| `worldNether` | booleano | vazio | Se é possível entrar no Nether, a linha `allow-nether`. `false` o fecha em um mundo de um jogador também |
| `worldCommandBlocks` | booleano | vazio | Se os blocos de comando funcionam, a linha `enable-command-block`. Um mundo de um jogador já os executa, e `false` os desativa ali também |
| `worldIdleTimeout` | int, -1 ou mais | `-1` | Quantos minutos um jogador pode ficar parado antes de ser expulso, a linha `player-idle-timeout`, 0 para nunca. Um mundo de um jogador também a respeita |
| `worldMotd` | texto | vazio | A linha exibida sob o nome do servidor na lista de servidores, a linha `motd`. Um mundo de um jogador aberto para LAN a mostra no lugar do dono e do nome do mundo |
| `worldMaxSize` | int, -1 a 29999984 | `-1` | O mais longe, em blocos a partir do centro, que a borda de um mundo pode chegar, a linha `max-world-size`. Um mundo de um jogador também a respeita |
| `worldStructures` | booleano | vazio | Se um mundo novo gera estruturas, a linha `generate-structures` e a opção Gerar estruturas da tela de mundo. Só é aplicado a um mundo no momento em que ele é criado |
| `worldSpawnMonsters` | booleano | vazio | Se monstros hostis nascem, a linha `spawn-monsters`. `false` os impede em um mundo de um jogador também |
| `worldSpawnAnimals` | booleano | vazio | Se animais nascem, a linha `spawn-animals`. `false` os impede em um mundo de um jogador também |
| `worldSpawnNpcs` | booleano | vazio | Se aldeões nascem, a linha `spawn-npcs`. `false` os impede em um mundo de um jogador também |
| `worldViewDistance` | int, -1 a 32 | `-1` | A quantos chunks de distância um servidor dedicado envia o mundo a cada jogador, a linha `view-distance`. Um mundo de um jogador segue a distância de renderização |
| `worldSimulationDistance` | int, -1 a 32 | `-1` | A quantos chunks de distância um servidor dedicado mantém o mundo em tick ao redor de cada jogador, a linha `simulation-distance`. Um mundo de um jogador segue a sua própria configuração |

### Receitas

*o que cada grupo faz*

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

`control.recipes` decide este grupo. Bloqueio de receitas de criação e de fornalha e suas listas de permissão. O bloqueio de fornalha leva junto as receitas do alto-forno, do defumador e da fogueira, já que o 1.12.2 mantinha todas as receitas de cozimento em uma única lista de fornalha. Receitas do cortador de pedra e da mesa de ferraria nunca são bloqueadas, já que o 1.12.2 não tinha nenhuma.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `blockRecipes` | booleano | `false` | Remove todas as receitas de criação, exceto as dos mods em `recipeWhitelist`. Nada fica isento por padrão, então liste o namespace do seu próprio pacote para manter as receitas dele |
| `recipeWhitelist` | lista de ids de mod | `["minecraft"]` | Os mods cujas receitas de criação sobrevivem |
| `blockedRecipeMods` | lista de ids de mod | vazio | Mods cujas receitas de criação são removidas de vez, diga o que disser a lista de permissão |
| `recipeMatch` | `recipe`, `output` ou `both` | `recipe` | De onde o id do mod é lido quando receitas de criação são bloqueadas: o nome da própria receita, o item que ela produz, ou qualquer um dos dois, que bloqueia quando um dos dois coincide e poupa quando um dos dois está na lista de permissão |
| `blockFurnaceRecipes` | booleano | `false` | O mesmo para receitas de fornalha, com o mod lido a partir do item produzido |
| `furnaceWhitelist` | lista de ids de mod | `["minecraft"]` | Os mods cujas receitas de fornalha sobrevivem |
| `blockedFurnaceMods` | lista de ids de mod | vazio | Mods cujas receitas de fornalha são removidas de vez |
| `logBlockedRecipes` | booleano | `true` | Registra no log uma contagem por mod do que foi bloqueado |
| `furnace` | booleano | `true` | Aplica os arquivos furnace/*.json, que adicionam e removem receitas de cozimento da fornalha **Apenas config.** |
| `removals` | booleano | `true` | Aplica os arquivos recipe_removals/*.json, que excluem receitas de criação por nome, namespace ou resultado **Apenas config.** |
| `skipMissingItems` | booleano | `true` | Ignora receitas que usam um item que não está registrado, em vez de deixá-las falhar. A contagem é registrada uma vez **Apenas config.** |

### Comandos

*o que cada grupo faz*

`control.commands` decide este grupo. Quem pode executar os comandos do próprio mod: os níveis de permissão do goto.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `gotoLevel` | int, 0 a 4 | `3` | O nível de permissão necessário para /rdplserver goto <name>, que leva quem o executa ao mais próximo. 3 é operador, o nível em que está todo o resto do comando. 2 também permite que um bloco de comando o execute, de modo que um pacote pode pôr o salto em um botão ou em uma placa de pressão sem entregar a ninguém o resto do comando. 0 deixa qualquer jogador digitá-lo. As outras partes de /rdplserver ficam em 3 seja qual for este valor |
| `gotoNextLevel` | int, 0 a 4 | `3` | O nível de permissão para /rdplserver goto <name> next, que ignora aquele para onde levou o remetente por último e encontra outro. Mesma escala de gotoLevel |
| `gotoBackLevel` | int, 0 a 4 | `3` | O nível de permissão para /rdplserver goto <name> back, que devolve o remetente ao anterior. Mesma escala de gotoLevel |
| `gotoPlaceLevels` | lista | vazio | Níveis de permissão para lugares individuais, como entradas nome=nível, uma por linha, que substituem as três configurações acima só para aquele lugar e nas suas três formas. O nome é o que você digitaria depois de goto, então um nome vanilla como Village ou Mansion, ou um nome que um pacote registrou para as próprias estruturas com locateAs. Mesma escala: 3 operador, 2 também bloco de comando, 0 qualquer um. Assim um pacote pode abrir o caminho para as próprias ruínas enquanto toda estrutura vanilla continua fechada, ou o contrário. Um nome que nada registrou é ignorado, com uma nota no log |

### Worldgen, apenas config

*o que cada grupo faz*

Estas chaves de `worldgen` não pertencem a nenhum grupo e são exclusivas da config.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `worldgenDebug` | booleano | `false` | Grava em logs/rdpl.log as linhas de depuração a que outras mensagens se referem, como qual pacote serviu um arquivo e o que cada comando fez. Muito detalhado |
| `worldTemplate` | texto | `auto` | Quais configurações de modelo de mundo valem. Um pacote adiciona um em worldtemplates/*.json e você o indica aqui como namespace:nome. 'auto' escolhe o modelo a partir do pacote de maior prioridade. Vazio não usa nenhum |
| `tellWorldType` | booleano | `true` | Avisa o jogador no chat, ao entrar em um mundo criado com a predefinição gerada, qual modelo o moldou. Um pacote não pode definir isto |
| `worldBorderLimit` | int, 1 a 60000000 | `60000000` | A maior borda que um pacote pode pedir por meio de worldBorder. Um pacote que pede mais é recusado e a borda fica onde o jogo a coloca. Um pacote não pode definir isto |
| `retrogenKey` | texto | `0000` | Mude isto para tornar todo chunk elegível para retrogen de novo, para toda entrada de worldgen. Veios novos são adicionados sobre o que já existe |
| `retrogenChunksPerTick` | int, 1 ou mais | `2` | Quantos chunks já gerados recuperar por tick. Mais alto é mais rápido, mas causa mais travadas |

### A categoria `packs`

*o que cada grupo faz*

Como as pastas de pacotes são encontradas e servidas. Apenas config.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `rootDirectory` | texto | `rdploader` | Pasta de onde os pacotes são carregados, relativa ao diretório .minecraft. Um caminho absoluto também funciona. Exige reinicialização |
| `overrideResourcePacks` | booleano | `true` | Insere o pacote de assets acima dos pacotes de recursos selecionados pelo jogador e dos pacotes de dados do próprio mundo. Um pacote chamado RDPLO... sempre prevalece, RDPLN... nunca |
| `warnOnCaseMismatch` | booleano | `true` | Avisa quando um arquivo só corresponde porque o sistema de arquivos não diferencia maiúsculas de minúsculas. Tais pacotes quebram no Linux |
| `logContents` | booleano | `false` | Registra no log cada pacote encontrado e quantos arquivos ele fornece |
| `traceUnresolvedVariables` | booleano | `false` | Registra um stack trace na primeira vez que um arquivo com '#' no nome é requisitado, indicando o que o requisitou |

### A categoria `content`

*o que cada grupo faz*

Blocos, itens, fluidos e tudo o mais que os pacotes definem. Apenas config.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `load` | booleano | `true` | Registra os blocos, itens, fluidos, materiais e abas criativas que os pacotes definem, e carrega suas exposições. Exige reinicialização |
| `vanillaClients` | booleano | `false` | Atende clientes vanilla puros: nada de nenhum pacote é registrado, nem blocos, itens, fluidos ou abas criativas, e nenhuma exposição é carregada, então um cliente sem o mod pode entrar. Tudo o que vive só no servidor continua valendo. Exige reinicialização |
| `sounds` | booleano | `true` | Registra os eventos de som nomeados por sounds/*.json, para que os pacotes possam trazer o próprio áudio |
| `fuels` | booleano | `true` | Aplica os arquivos fuels/*.json, que dão aos itens um tempo de queima na fornalha |
| `potions` | booleano | `true` | Registra os efeitos de poção e os tipos de poção descritos por potions/*.json e potion_types/*.json nos pacotes. Exige reinicialização |
| `brewing` | booleano | `true` | Aplica os arquivos brewing/*.json, que adicionam receitas do suporte de poções |
| `villagers` | booleano | `true` | Registra as profissões de aldeão descritas por villagers/*.json e aplica as trocas em trades/*.json. Exige reinicialização |
| `entities` | booleano | `true` | Registra as variantes de entidade descritas por entities/*.json nos pacotes. Exige reinicialização |
| `overrides` | booleano | `true` | Aplica os arquivos overrides/<namespace>/<name>.json, que alteram propriedades de blocos, itens e tipos de poção que já existem, vanilla ou de mods |
| `disabled` | booleano | `true` | Aplica os arquivos disabled/*.json, que tiram blocos e itens de jogo: sem aba criativa, entrada no JEI, receita, loot, troca, tag, colocação, uso ou coleta, e as pilhas deles são excluídas |
| `hardness` | booleano | `true` | Aplica os arquivos hardness/*.json, que dão a um grupo de blocos um multiplicador de tempo de mineração e de resistência a explosões, sorteado por posição de bloco |
| `shovelPaths` | booleano | `true` | Deixa uma pá transformar blocos marcados com behavesAs path em caminho, e reverter um caminho ao se agachar |
| `shovelPathBecomes` | texto | vazio | Em que a pá transforma esses blocos. Vazio usa o caminho de terra |
| `shovelPathReverts` | texto | vazio | Em que agachar com uma pá transforma um caminho de volta. Vazio usa terra |
| `hoeTilling` | booleano | `true` | Deixa uma enxada arar blocos marcados com behavesAs till |
| `hoeTillsInto` | texto | vazio | Em que uma enxada transforma esses blocos. Vazio usa terra arada |
| `caneMaxHeight` | int, 1 a 255 | `3` | Que altura a cana-de-açúcar vanilla atinge. O vanilla é 3. Blocos de cana definidos por pacotes usam a própria seção growth e ignoram isto |
| `cactusMaxHeight` | int, 1 a 255 | `3` | O mesmo para o cacto vanilla |

### A categoria `data`

*o que cada grupo faz*

Loot, funções e nomes de registro. Apenas config.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `lootInjections` | booleano | `true` | Aplica os arquivos loot_injections/*.json, que adicionam pools a tabelas de loot que já existem em vez de substituir a tabela inteira |
| `playerLoot` | booleano | `true` | Aplica os arquivos player_loot/*.json, que sorteiam uma tabela de loot quando um jogador morre e largam o que ela produz, além do inventário ou no lugar dele |
| `registryRemaps` | booleano | `true` | Aplica os arquivos registry_remap, que renomeiam uma entrada de registro para que mundos salvos antes da renomeação mantenham seus blocos e itens em vez de perdê-los |
| `anvils` | booleano | `true` | Aplica os arquivos anvils/*.json, que deixam uma bigorna pôr encantamentos nomeados em um item por um custo em níveis, conceder um avanço ao ser retirado, e reter um item do uso até então |
| `blockDrops` | booleano | `true` | Aplica os arquivos block_drops/*.json, que acrescentam ou substituem o que um bloco larga sempre que quebra, experiência inclusive, para blocos que um pacote não possui |
| `functions` | booleano | `true` | Carrega arquivos .mcfunction dos pacotes, para que funcionem em todo mundo |

### A categoria `tweaks`

*o que cada grupo faz*

Pequenas mudanças em como o vanilla se comporta. Apenas config, exceto `privacy`, que um pacote também pode definir; veja [Bônus: ajustes do vanilla](#bônus-ajustes-do-vanilla).

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `promptLeafDecay` | booleano | `true` | Folhas que perdem a árvore se decompõem em um segundo em vez de esperar por ticks aleatórios |
| `lenientPaths` | booleano | `true` | Caminhos podem ser feitos sob um bloco e permanecem ali quando um é colocado por cima |
| `unbreakableSpawners` | booleano | `false` | Spawners de mobs não podem ser minerados nem explodidos. O modo criativo ainda os remove. Exige reinicialização |
| `experimentalWarning` | booleano | `false` | Mostra o aviso de configurações experimentais do jogo quando um mundo é criado ou aberto. Desligado, responde como se você tivesse clicado em prosseguir |
| `privacy` | booleano | `true` | Desativa a telemetria e as denúncias de chat do jogo: nenhum evento de telemetria é enviado ou registrado, o cliente não assina nenhuma mensagem de chat, o servidor não mantém sessão de chat nem exige uma, então nenhuma mensagem enviada por ninguém pode ser denunciada, e o cliente não mostra o aviso de que um servidor não impõe chat seguro. Um pacote pode defini-la como `privacy` nas `settings` de um modelo de mundo, no grupo [Servidor](#servidor). Vale a partir do próximo mundo ou servidor em que se entrar |
| `darkSplash` | booleano | `true` | Desenha a tela de carregamento escura com o logo do carregador de pacotes no lugar do logo do jogo: o logo é trocado quando a tela é criada, e a opção Logo monocromático do próprio jogo é ligada se ainda estiver desligada, o que vale a partir da próxima inicialização. Desligado deixa a opção como está |

---

# Outros mods

## Integração com Blast Plaster

*outros mods*

`<namespace>/blastplaster/*.json`

O nome do arquivo é livre, só a pasta é lida, e vários arquivos se acumulam.

O Blast Plaster cuida do comportamento pós-explosão: cura de crateras bloco a bloco, derrubada de árvores consciente da árvore inteira, controle de drops. Sozinho, ele lê uma única config global. Conduzido por um pacote, responde **por dimensão**, e o pacote entrega a decisão em vez de pedir aos jogadores que editem uma config. Sem arquivos de pacote, ou sem o Blast Plaster instalado, nada aqui faz coisa alguma, e a pasta é ignorada com uma linha no log.

As chaves escritas no topo do arquivo valem em toda parte; um bloco `dimensions` as substitui para uma dimensão, por id. Tudo que um pacote nunca nomeia mantém o que a própria config do Blast Plaster diz, então um pacote define o punhado que lhe importa e deixa o resto em paz.

Todas as chaves, mostradas de uma vez. Um arquivo de verdade escreve só as de que precisa.

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

`explosionMode` é a chave principal: `HEAL` restaura a cratera com o tempo, `EJECT_DROPS` deixa o buraco e solta cerca de um terço dos blocos (comportamento vanilla), `VISUAL_TOSS` deixa o buraco e não solta nada. Quando conduzido por um pacote, o padrão é `EJECT_DROPS` (não o `HEAL` do Blast Plaster), de modo que uma instalação sem configuração se comporta como o vanilla.

| Chave | Valor | O que faz |
| --- | --- | --- |
| `explosionMode` | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | O que acontece depois do estrondo |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true ou false | Quais explosões são tratadas |
| `processPlayerIgnitedTNT` | true ou false | Se o TNT que um jogador acendeu é tratado junto com o resto |
| `customEntitiesToHeal` | lista de nomes de entidades | Explosões de outros mods, nomeadas como `modid:entity` |
| `healFullTrees` | true ou false | Uma árvore atingida por uma explosão é levada ou restaurada inteira, em vez de cortada pela metade |
| `maxTreeSize` | número | O máximo de blocos que uma árvore pode reivindicar antes de ser deixada em paz |
| `minimumTicksBeforeHeal`, `randomTickVar` | números | Quanto tempo antes de a restauração começar, e quão irregular é o seu ritmo |
| `overrideBlocks` | true ou false | Se a restauração sobrescreve o que foi construído no buraco desde então |
| `enableFakeTossedBlocks` | true ou false | Os destroços que voam da explosão |
| `enableExplosionFlash` | true ou false | O clarão forte no instante da explosão |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | números | Quanto o clarão dura, quão forte ele brilha, quantas partículas lança e quantas vezes pulsa |
| `enableExplosionSmoke` | true ou false | A coluna de fumaça depois |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | números | Quanto a fumaça permanece e quão densa ela fica |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true ou false | O que o TNT de um jogador deixa para trás |
| `enableDropSuppression`, `dtSpecialDrops` | true ou false | Drops dentro de uma explosão, e os drops próprios do Dynamic Trees |
| `preventMobDrops` | true ou false | Se mobs mortos por uma explosão ainda largam itens |
| `blockConversions` | lista de regras | Em que um bloco atingido se transforma em vez de voltar como era, de modo que uma construção se desgasta um passo por explosão |

`blockConversions` decide em que um bloco atingido se transforma em vez de voltar como era. Uma regra se lê `<source>=<result>[@chance]`: a origem é um id de bloco, ou uma tag de bloco com um `#` na frente; o resultado é um id de bloco, ou `nothing` para deixar o espaço vazio; a chance vai de 0.0 a 1.0 e o padrão é 1.0. A primeira regra que coincide vence, então as regras específicas vão acima das amplas, e um bloco que já é o resultado de alguma regra nunca é convertido de novo — uma parede cede um passo por explosão em vez de se desgastar até sumir.

**Aparência totalmente vanilla:** `EJECT_DROPS` mais `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` e `playerTNTAlwaysDrops`, todos desligados. Cada chave pode ser definida por dimensão.

**Clientes vanilla** não veem nada de incomum. O clarão é o único recurso que coloca um bloco, então com `vanillaClients` ativado ele é forçado a desligar; todo o resto são partículas e itens que um cliente puro entende.

Não são chaves de pacote: o log de depuração do Blast Plaster e o seu pareamento tronco-folhas (a identificação de árvores deve ter uma resposta só no jogo todo). Ambos ficam na config do próprio Blast Plaster.

---

# Referência

## Listas de valores

*referência*

### Nomes aceitos

*listas de valores*

Estes são os nomes que o parser aceita sempre que as tabelas acima dizem "um dos materiais", e assim por diante. Qualquer coisa não reconhecida é registrada no log e substituída pelo padrão.

**Materiais de bloco.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`. O jogo em si não tem mais materiais; cada nome faz o que aquele material fazia no 1.12.2: define a cor no mapa, se o bloco exige uma ferramenta para soltar algo, como os pistões o tratam, se a lava o incendeia, se o líquido corrente o leva embora e se um bloco colocado o substitui.

**Tipos de som.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Cores de mapa.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Camadas de renderização.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Se ficar vazio, o bloco escolhe uma adequada ao seu tipo.

**Raridades.** `common`, `uncommon`, `rare`, `epic`.

**Partículas de tocha.** `none`, `flame`, `colored`. `colored` usa `particleColor`.

**Classes de ferramenta.** `pickaxe`, `axe`, `shovel`, `hoe`, `sword`.

**Compartimentos de armadura.** `head` ou `helmet`, `chest` ou `chestplate`, `legs` ou `leggings`, `feet` ou `boots`.

**Tonalidades.** `biome`, `none`, ou uma cor hexadecimal de seis dígitos. As cores em qualquer parte de uma definição são hexadecimais, com ou sem um `#` na frente.

**Comportamentos** para `behavesAs`. `till`, `path`, `bush`, `animals`.

**Tipos de planta** para `plantTypes`. `plains`, `desert`, `beach`, `cave`, `water`, `nether`, `crop`. Apenas 1.20.1; o 1.21.1 lê a chave e a ignora.

**Tipos de bioma**, as palavras que representam uma tag de bioma sempre que uma tabela diz "lista de tipos de bioma", em `biomeTypes`, nos `types` de um bioma, nos `roles` de um modelo e em uma seção `biomes`: `ocean`, `deepocean`, `beach`, `river`, `mountain`, `mesa`, `hills`, `coniferous`, `jungle`, `forest`, `savanna`, `overworld`, `nether`, `end`, `hot`, `cold`, `sparse`, `dense`, `wet`, `dry`, `spooky`, `dead`, `lush`, `mushroom`, `magical`, `rare`, `plateau`, `modified`, `water`, `desert`, `plains`, `swamp`, `sandy`, `snowy`, `wasteland`, `void`. As palavras vanilla correspondem às tags `minecraft:is_*` e as demais às tags de convenção, `forge:is_*` no 1.20.1 e `c:is_*` no 1.21.1. Uma tag escrita por extenso, `minecraft:is_forest` ou `#minecraft:is_forest`, é tomada como está. Maiúsculas e minúsculas não importam, então o `FOREST` do 1.12.2 ainda é lido.

**Papéis** para os `roles` de um modelo de mundo. Qualquer palavra de tipo de bioma acima: cada uma nomeia um bioma que preenche os biomas que carregam aquela tag depois que o bloqueio os removeu, de modo que `"ocean": "mypack:ruby_ocean"` põe o oceano de rubi onde quer que um oceano tenha sido bloqueado.

**Estruturas** para os `structures` de um modelo de mundo e para as listas do próprio grupo `structures`: os nomes do 1.12.2 `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges` e `endcities`, ou qualquer conjunto de estruturas que o jogo ou um mod traga, como `pillager_outposts`, `ancient_cities`, `trail_ruins`, `shipwrecks`, `ocean_ruins`, `ruined_portals`, `nether_fossils`, `buried_treasures`, `desert_pyramids`, `jungle_temples`, `igloos`, `swamp_huts`, `woodland_mansions`, `ocean_monuments`, `nether_complexes`, `end_cities`. Um nome do 1.12.2 é lido como os conjuntos que ele representava, então `temples` são as pirâmides, os templos da selva, os iglus e as cabanas de bruxa juntos. Os nomes de populate do 1.12.2 também são lidos, como as partes do mundo desta versão que eles representam: `caves` os escavadores de cavernas (as cavernas de ruído são `noiseCaves`), `ravines` os cânions, `dungeons` as salas de monstros, `lavalakes` os lagos de lava, `netherlava` as fontes de lava abertas do Nether, `fire` as manchas de fogo do Nether, `glowstone` a sua pedra luminosa, `ice` a camada superior congelada e `animals` os animais colocados quando um chunk é criado. `waterlakes` é aceito e não faz nada, já que esta versão não tem lagos de água.

**Tipos de criatura** para spawns e taxas de bioma. `creature`, `monster`, `ambient`, `water`. O spawn de uma variante de entidade também aceita, pelo nome, as outras listas desta versão, como `water_ambient` ou `underground_water_creature`.

**Tipos de minério** para `oreTypes`. `COAL`, `IRON`, `COPPER`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `TUFF`, `CLAY`, `SILVERFISH`, `CUSTOM` para qualquer outro minério.

**Tipos de dano** para o `immuneTo` de uma variante de entidade, em qualquer caixa e com ou sem sublinhados. Os nomes do 1.12.2, cada um cobrindo o que cobria lá: `inFire` (uma fogueira também), `onFire` (uma bola de fogo que ninguém lançou também), `lava`, `hotFloor`, `inWall` (a borda do mundo também), `cramming`, `drown`, `starve`, `cactus`, `fall`, `flyIntoWall`, `outOfWorld` (`/kill` também), `generic`, `magic`, `indirectMagic`, `wither`, `anvil`, `fallingBlock`, `dragonBreath`, `fireworks`, `lightningBolt`, `thorns`, `arrow`, `fireball`, `thrown`, `mob` para o golpe de uma criatura, a cuspida de uma lhama, o projétil de um shulker ou a caveira de um wither, `player` para o golpe de um jogador, `explosion` para uma explosão que ninguém causou, como a de uma cama, e `explosion.player` para uma explosão que uma criatura ou um jogador causou, um creeper ou TNT aceso. O dano desta versão também tem nomes próprios: `fire` para os dois tipos de queimadura, `lightning`, `void`, `freeze`, `dryOut` e `sweetBerry`. Qualquer outra coisa é lida como id de um tipo de dano, `minecraft:sonic_boom` ou um próprio de um mod.

**Nomes de som** para os `sounds` de uma variante de entidade, o `openSound` de um cofre trancado e o `sound` de um portal: qualquer evento de som registrado, do jogo, de um mod ou um que um pacote adicione por meio de `sounds`. Um nome do 1.12.2 é lido como o nome que esse som tem agora, então `entity.endermen.scream` toca `entity.enderman.scream`, `block.cloth.step` toca `block.wool.step`, `entity.small_slime.squish` toca `entity.slime.squish_small` e `record.cat` toca `music_disc.cat`. As quatro imitações de papagaio que esta versão descartou, do enderman, do urso-polar, do lobo e do zombie pigman, não tocam nada.

## Lista de pastas

*referência*

Todas as pastas, com o caminho completo e um link para a seção que as descreve, estão em [Onde os arquivos ficam](#onde-os-arquivos-ficam).

## Comandos

*referência*

### Seus próprios comandos

*comandos*

`/rdpl` roda na sua própria máquina e não exige permissões, porque tudo o que ele toca é seu. Um reload reexamina a pasta que você possui e atualiza os seus próprios recursos; ele não alcança nenhum servidor, então a cópia do servidor é recarregada com `/rdplserver reload`. No modo de um jogador, as duas são a mesma máquina, então `/rdpl reload` também é o que reaplica as suas [substituições de propriedades](#substituições-de-propriedades) e reescala as suas equipes.

| Comando | Nível | O que faz |
| --- | --- | --- |
| `/rdpl list` | nenhum | Todo pacote carregado, a sua prioridade e o que ele contém. Clique em um pacote para procurar um arquivo nele |
| `/rdpl which <namespace:path>` | nenhum | Qual pacote fornece um dado arquivo, e quais pacotes ele sobrepõe |
| `/rdpl reload` | nenhum | Reexamina a pasta e recarrega tudo, incluindo o reload de recursos do próprio jogo |
| `/rdpl unused` | nenhum | Arquivos dos seus pacotes que nada requisitou ainda, normalmente um erro de digitação em um caminho |
| `/rdpl config unused` | nenhum | Arquivos de opções em `rdploader/config` que nenhum pacote instalado define mais |
| `/rdpl config prune` | nenhum | Exclui esses arquivos |
| `/rdpl pixelmap <namespace:path>` | nenhum | No que um [mapa de pixels](#texturas-escritas-como-mapas-de-pixels) resultou, caractere por caractere |
| `/rdpl biome`, `biome list [all]` | nenhum | Todo bioma que pode ser gerado, e o seu id; `all` inclui os que nada pode gerar |
| `/rdpl biome here` | nenhum | O bioma em que você está: o seu nome, id e número |
| `/rdpl biome find <name>` | o do servidor | Vinculado. Repassado palavra por palavra ao `/rdplserver`, que decide, então veja a tabela abaixo |
| `/rdpl locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen`, `game` | o do servidor | Vinculado. Repassado palavra por palavra ao `/rdplserver`, que decide, então veja a tabela abaixo |

**Quais subcomandos do servidor são vinculados, e por que os demais não são.** `locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen` e `game` só podem significar os do servidor, já que só o servidor conhece o mundo, os seus jogadores e as suas rodadas, então o `/rdpl` os repassa. No modo de um jogador, o autocompletar depois de um deles oferece o que o `/rdplserver` ofereceria; em um servidor, `goto` oferece os nomes de estruturas vanilla. Os demais, `reload`, `list`, `which`, `unused`, `config`, `pixelmap` e `biome`, mantêm o seu próprio sentido, o dos seus pacotes e do seu cliente. A verificação de permissão do próprio servidor decide um comando vinculado, então um cliente não pode burlá-lo nem receber uma resposta forjada.

**Edição no dia a dia:** F3+T recarrega texturas, modelos e arquivos de idioma, e `/reload` os dados do servidor. Use `/rdpl reload` quando você *adicionar* ou *excluir* um arquivo, já que isso muda o que a pasta contém.

### Comandos de servidor

*comandos*

Em um servidor dedicado, `/rdplserver` faz o mesmo pela cópia da pasta do próprio servidor. A coluna Nível é o nível de permissão de que um remetente precisa: `3` é operador, `2` também admite blocos de comando, `0` é qualquer jogador, e `4` está acima de operador e não alcança ninguém.

#### Pacotes e arquivos

*comandos de servidor*

| Comando | Nível | O que faz |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Reexamina a pasta do servidor e recarrega tudo, depois escala as equipes e os objetivos de novo |
| `/rdplserver list` | 3 | Todo pacote que o servidor carregou, a sua prioridade e o que ele contém |
| `/rdplserver which <namespace:path>` | 3 | Qual pacote fornece um dado arquivo, e quais pacotes ele sobrepõe |
| `/rdplserver unused` | 3 | Arquivos dos pacotes do servidor que nada requisitou |
| `/rdplserver config unused` | 3 | Arquivos de opções em `rdploader/config` que nenhum pacote instalado define mais |
| `/rdplserver config prune` | 3 | Exclui esses arquivos |
| `/rdplserver pixelmap <namespace:path>` | 3 | No que um mapa de pixels resultou |

#### Mundo e geração

*comandos de servidor*

| Comando | Nível | O que faz |
| --- | --- | --- |
| `/rdplserver oregen` | 3 | Totais acumulados da geração de minérios que foi bloqueada, por mod e tipo |
| `/rdplserver generators` | 3 | Totais acumulados dos geradores de mundo que foram bloqueados, por mod e tipo |
| `/rdplserver biome list [all]` | 3 | Todo bioma que pode ser gerado no servidor, com o seu número, id e nome; `all` inclui os que nada pode gerar |
| `/rdplserver biome` | 3 | O bioma em que você está e o que o pacote faz com ele: o seu id, número e nome, se `blockBiomes` está ligado e qual modelo de mundo está ativo, e o solo, o bloco sob ele e a pedra em y 40 |
| `/rdplserver biome here [player]` | 3 | O bioma em que você, ou o jogador indicado, está: o seu nome, id e número. O console indica um jogador |
| `/rdplserver biome find <name>` | 3 | O lugar mais próximo em até 6400 blocos onde um bioma com esse id ou nome exibido é gerado: as suas coordenadas e a distância de onde é executado. Avisa quando nenhum está tão perto, ou quando o nome não corresponde a nenhum bioma. `/rdpl biome find` o repassa |
| `/rdplserver dimensions` | 3 | Todas as dimensões, incluindo as que os pacotes adicionaram |
| `/rdplserver vein <entry> [radius]` | 3 | Onde uma entrada de worldgen de formato `vein` tem os seus veios semeados em até esse número de chunks (padrão 8) de onde é executado, do mais próximo ao mais distante, existam ou não esses chunks ainda. `/rdpl vein` o repassa |

#### Comandos de portais

*comandos de servidor*

| Comando | Nível | O que faz |
| --- | --- | --- |
| `/rdplserver gate`, `gate list` | 3 | Todo portal, a sua dimensão, o seu escopo e se está aberto |
| `/rdplserver gate check <player>` | 3 | Quais portais um jogador atravessou |
| `/rdplserver gate grant <player> <gate>` | 3 | Abre um portal para um jogador |
| `/rdplserver gate revoke <player> <gate>` | 3 | Fecha-o de novo |

#### Comandos de pré-geração

*comandos de servidor*

| Comando | Nível | O que faz |
| --- | --- | --- |
| `/rdplserver pregen <radius>` | 3 | Cria todo chunk em até esse número de chunks de onde é executado. Veja [Pré-geração](#pré-geração) |
| `/rdplserver pregen status` | 3 | Em que ponto uma execução está |
| `/rdplserver pregen stop` | 3 | Encerra-a |

#### Jogadores, equipes e rodadas

*comandos de servidor*

| Comando | Nível | O que faz |
| --- | --- | --- |
| `/rdplserver intro` | 0 | Faz a introdução do mundo tocar de novo na sua próxima entrada. Qualquer jogador pode executá-lo, e ele só limpa a do próprio jogador; é recusado quando nenhum pacote tem uma introdução |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0 | Os lados que um pacote escalou e os caminhos para entrar e sair deles, oferecidos só enquanto um pacote escala lados, veja [Equipes](#equipes) |
| `/rdplserver round start`, `round reset`, `round vote yes`, `round vote no` | 0 | Inicia uma rodada que o pacote mantém em um lobby, reinicia uma rodada em andamento ou convoca uma votação para isso, e vota em uma, conforme a pontuação do pacote permitir, e só um jogador inicia uma; oferecido aos jogadores só enquanto um pacote mantém pontuação, veja [Pontuação](#pontuação) |
| `/rdplserver card <rule> [players]` | 2 | Mostra uma [regra de carta](#cartas) aos jogadores indicados, ou a você mesmo, pelo seu id ou nome de arquivo. `when`, `repeat` e `cooldown` são ignorados |
| `/rdplserver reset` | 3 | Devolve o mapa ao estado em que o fim de uma rodada o deixa: todos são retidos, as entidades varridas, as pontuações apagadas, `resetRuns` executados, os jogadores colocados em `resetSendsTo` e liberados, e uma rodada abre com a contagem inicial, como as configurações de reset em [Pré-geração](#pré-geração) descrevem. Não é repassado pelo `/rdpl` |

#### Indo a lugares

*comandos de servidor*

| Comando | Nível | O que faz |
| --- | --- | --- |
| `/rdplserver locate <name>` | 3 | A estrutura mais próxima que um pacote colocou sob esse nome `locateAs` |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | Leva você à mais próxima onde ninguém esteve ainda, procurando sem gerar o terreno no caminho. Um lugar que um pacote registrou com `locateAs` é o mais próximo colocado, visitado ou não. `temple` significa toda feature dispersa: templos do deserto e da selva, cabanas de bruxa e iglus. Recusado enquanto o terreno está sendo criado |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Leva você adiante à mais próxima para onde você não foi levado nesta sessão, tenha sido ela visitada antes ou não. Uma a até oito chunks de você é ignorada; para o lugar de um pacote, é a mais próxima a mais de 128 blocos de distância |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Leva você à anterior, voltando passo a passo pelos lugares para onde esta sessão o enviou |
| `/rdplserver goto <biome>` | `gotoLevel`, `3` | Leva você ao lugar mais próximo desse bioma, indicado pelo ID, como `minecraft:river`, e deixa você na superfície; `next` e `back` funcionam como para uma estrutura. Uma função ou um bloco de comando pode executar qualquer `goto` no nível 2 |

#### Jogos

*comandos de servidor*

| Comando | Nível | O que faz |
| --- | --- | --- |
| `/rdplserver game coin` | 0 | Jogar uma moeda. Cara conta como 1, coroa como 0 |
| `/rdplserver game die <sides>` | 0 | Rolar um dado de 2 a 1000 lados |
| `/rdplserver game die <name>` | 0 | Rolar um [dado do pacote](#dados-e-baralhos) pelos seus pesos |
| `/rdplserver game dice <roll>` | 0 | Rolar até 100 dados e somá-los, como `2d6`, `d20` ou `3d8-2`. Cada dado é mostrado |
| `/rdplserver game advantage [roll]`, `disadvantage [roll]` | 0 | Rolar duas vezes e ficar com o total maior, ou o menor. Sem indicação, a rolagem é `1d20` |
| `/rdplserver game pick player` | 0 | Sortear um jogador conectado |
| `/rdplserver game pick team [team]` | 0 | Sortear um time do placar, ou um membro conectado do time nomeado |
| `/rdplserver game deck draw <name>` | 0 | Tirar uma carta do que resta de um baralho do pacote |
| `/rdplserver game deck left <name>` | 0 | Quantas cartas restam no baralho |
| `/rdplserver game deck shuffle <name>` | 2 | Devolver todas as cartas |
| `/rdplserver game teamroll [roll]` | 0 | Todos do lado do remetente rolam e o maior vence, com empate sorteado. Sem times, o remetente rola sozinho |
| `/rdplserver game tiebreak [objective]` | 2 | Sortear um dos lados empatados no topo de um objetivo: o nomeado, senão o primeiro objetivo de pontuação com `tiebreak`, senão o primeiro |
| `/rdplserver game board list` | 0 | Cada tabuleiro do mundo, com o seu jogo, a sua posição e o seu estado |
| `/rdplserver game board start <game> <board> [x y z]` | 2 | Montar um tabuleiro onde está quem enviou, ou na posição dada, e colocar as suas peças |
| `/rdplserver game board end <board>` | 2 | Remover um tabuleiro e as suas peças |
| `/rdplserver game board show <board>` | 0 | A posição em letras, fileira por fileira, com quem tem cada lado, o seu tempo e de quem é a vez |
| `/rdplserver game board move <board> <from> <to> [piece]` | 0 | Mover uma peça por nomes de casa, como `e2 e4`, com a peça em que uma promoção se torna |
| `/rdplserver game board resign <board>` | 0 | Abandonar a partida |
| `/rdplserver game board draw <board>` | 0 | Oferecer empate, ou aceitar a oferta do outro lado |
| `/rdplserver game board takeback <board>` | 0 | Pedir para desfazer o último lance, ou concordar com o pedido do outro lado |
| `/rdplserver game board ai <board> <side> <level>` | 2 | Fazer o computador jogar um lado num nível de 1 a 4, ou devolvê-lo com 0 |
| `/rdplserver game last [count]` | 0 | As últimas rolagens, a mais recente primeiro: 10, ou a quantidade dada até 50 |
| `/rdplserver game pass` | 0 | Encerra mais cedo a vez do lado de quem envia quando um arquivo de pontuação joga por turnos. De um bloco de comando ou do console, encerra a vez de quem estiver jogando |

Qualquer rolagem pode terminar com `store <objective>`, que grava o seu número na pontuação do próprio remetente nesse objetivo, e com `audience <quem>`, que substitui o padrão do pacote: `self`, `team` (o lado do remetente, ou só o remetente sem times), `all`, `radius <blocos>` (jogadores no mesmo mundo dentro dessa distância) ou `silent`, que só grava. `/rdpl game` é repassado a ele.

### Quem pode usar goto

*comandos*

**Abrindo o `goto`.** Toda parte de `/rdplserver` exige um operador, nível 3, exceto `intro`, `team` e `round`, que qualquer jogador pode executar, como no 1.12.2, e `game`, cujas partes têm [níveis próprios](#quem-pode-usar-game). As três formas de `goto` são a única coisa que um pacote decide: cada uma carrega um nível de permissão próprio que um pacote ou a config pode reduzir, separadamente das outras duas e do resto do comando. Um pacote que quer `reset` ao alcance dos jogadores o põe em um bloco de comando ou em uma função, que roda no nível 3.

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

| Configuração | O que rege |
| --- | --- |
| `gotoLevel` | `goto <structure>` |
| `gotoNextLevel` | `goto <structure> next` |
| `gotoBackLevel` | `goto <structure> back` |
| `gotoPlaceLevels` | Um lugar nomeado, nas três formas |

O valor é o nível de permissão de que um remetente precisa. `3` (operador) é o padrão. `2` também admite blocos de comando, de modo que um pacote pode pôr um salto em um botão ou uma placa de pressão sem expor o resto de `/rdplserver`. `0` o abre a qualquer jogador. As três configurações são independentes: por exemplo, `next` aberto a blocos de comando para um tour por uma vila enquanto `back` continua só para operadores. Um valor abaixo de 0 conta como 0 e um acima de 4 como 4, e a um operador sempre é oferecido o próprio `goto`, digam o que disserem as configurações.

`gotoPlaceLevels` substitui as três configurações para lugares individuais, como entradas `nome=nível`, como no exemplo acima. O nome é o que você digitaria depois de `goto`: um nome vanilla como `village` ou `mansion`, ou um nome registrado com `locateAs` em uma entrada imprint. A comparação ignora maiúsculas e minúsculas. Um nível `4` está acima de operador e fecha esse lugar a todos, o jeito de esconder um lugar enquanto o resto do `goto` está aberto.

Uma entrada define um nível para as três formas daquele lugar. Um lugar não listado recorre às três configurações acima, e um nome não registrado nunca corresponde. O autocompletar segue as mesmas regras, então depois de `goto` um remetente só recebe os lugares para onde realmente pode ser levado.

Estas ficam no grupo `commands`, então `control.commands` na config decide se um pacote pode defini-las, e `off` ali mantém tudo no nível de operador, peça o que um pacote pedir.

### Quem pode usar game

*comandos*

Cada parte de `game` tem o seu próprio nível: 0 para cada rolagem, e 2 para `deck shuffle`, `tiebreak`, `board start`, `board end` e `board ai`. `gameLevels` muda qualquer um deles, como entradas `parte=nível`, onde a parte é o que vem depois de `game`.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "gameLevels": ["coin=0", "deck draw=0", "deck shuffle=3", "tiebreak=4"]
  }
}
```

| Configuração | O que rege |
| --- | --- |
| `gameLevels` | Uma parte de `game`: `coin`, `die`, `dice`, `advantage`, `disadvantage`, `pick`, `deck draw`, `deck shuffle`, `deck left`, `teamroll`, `tiebreak`, `last`, `pass` ou `board` com a sua ação, como `board move` |

A escala é a de `goto`, e `4` fecha uma parte para todos. O preenchimento com Tab oferece só as partes que um remetente pode usar. `gameLevels` fica no grupo `commands` junto às configurações de `goto`.

## Bom saber

*referência*

- KubeJS e CraftTweaker rodam depois do RDPL, então as mudanças deles ainda prevalecem.
- Receitas, tabelas de loot, avanços e funções são aqui os próprios arquivos de dados do jogo, então `/reload` aplica uma edição e `/rdpl reload` um arquivo novo.
- Uma estrutura que já foi gerada permanece carregada até você sair do mundo.
- A caixa do nome do arquivo importa. Se a capitalização do seu arquivo não coincide com o que o jogo pediu, o RDPL ainda o carrega, mas avisa, porque no Linux ele não seria encontrado.
- Ponha um `pack.png` em `rdploader` para dar um ícone ao pacote. Sem um, ele mostra o ícone do RDPL.
- A pasta pode ser movida ou renomeada com a opção `rootDirectory` em `config/resourcedatapackloader-common.toml`. Um caminho absoluto também funciona, e exige reinicialização.
- Um modelo que nomeia um modelo vanilla completo herda também as texturas do vanilla. Modelos pai como `cube_all` e `cross` pegam as texturas do modelo que os nomeia e não há problema.
- A telemetria e as denúncias de chat do jogo ficam desativadas enquanto `privacy` na categoria `tweaks` estiver ligada, o que é o padrão: nada é enviado, nenhuma mensagem de chat é assinada, um servidor que roda o mod não mantém sessão de chat para ninguém, e entrar em um servidor que não impõe chat seguro não mostra nenhum aviso.
- Uma opção de pacote alterada é lembrada pelo mundo que ela muda; quando a mudança deixa conteúdo que o mundo guarda sem registro, é feito um backup do mundo na pasta `backups` do jogo antes de ele ser aberto de novo, e ele permanece fechado se esse backup falhar.

## Quando algo não funciona

*referência*

**Verifique primeiro o `logs/rdpl.log`.** Tudo o que o RDPL faz vai para lá, e não para o log principal. Avanços, tabelas de loot, receitas, funções, estruturas e todo conteúdo são registrados com o pacote de onde vieram, e qualquer coisa malformada é registrada com o motivo.

**Texturas e outros assets são diferentes.** Eles são requisitados com frequência demais para serem registrados um a um, então `/rdpl unused` lista os arquivos dos seus pacotes que nada requisitou. Execute-o quando o jogo terminar de carregar. Um arquivo com o caminho certo é sempre requisitado, então o que aparecer na lista costuma ser um erro de digitação, mas lembre-se de que alguns arquivos só carregam quando são necessários, como idiomas diferentes do que você joga.

**Um zip sem um diretório `assets` ou `data` dentro dele é ignorado,** e o mesmo vale para qualquer pasta em `rdploader`, e o log avisa. Um zip cujo nível superior é uma pasta que envolve esses diretórios é ignorado da mesma forma.

**`/rdpl which minecraft:textures/block/stone.png`** diz exatamente qual pacote está servindo um arquivo e o que ele está sobrepondo.

**Um pacote escrito para 1.12.2 é lido pela portagem de compatibilidade.** Veja [Pacotes escritos para 1.12.2](#pacotes-escritos-para-1122); o log nomeia todo arquivo que ela moveu, omitiu ou não pôde manter.

## Bônus: ajustes do vanilla

*referência*

Pequenas mudanças em como o vanilla se comporta, cada uma ativada na categoria de config `tweaks`.

| Opção | Padrão | O que faz |
| --- | --- | --- |
| `promptLeafDecay` | ligado | Folhas que perdem a árvore se decompõem em um segundo em vez de esperar por ticks aleatórios |
| `lenientPaths` | ligado | Caminhos podem ser feitos sob um bloco e permanecem ali quando um é colocado por cima |
| `unbreakableSpawners` | desligado | Spawners de mobs não podem ser minerados nem explodidos. O modo criativo ainda os remove. Exige reinicialização |
| `experimentalWarning` | desligado | Mostra o aviso de configurações experimentais do jogo quando um mundo é criado ou aberto. Desligado, responde como se você tivesse clicado em prosseguir |
| `privacy` | ligado | Desativa a telemetria e as denúncias de chat do jogo; veja [Bom saber](#bom-saber) |
| `darkSplash` | ligado | Tela de carregamento escura com o logo do carregador de pacotes; a opção Logo monocromático do jogo é ligada para a próxima inicialização |

Mais quatro ficam na categoria `content` e não em `tweaks`:

| Opção | Padrão | O que faz |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | Que altura o cacto vanilla atinge |
| `caneMaxHeight` | `3` | Que altura a cana-de-açúcar vanilla atinge |
| `shovelPaths` | ligado | Uma pá transforma blocos marcados com `behavesAs` path em caminho, e agachar reverte um |
| `hoeTilling` | ligado | Uma enxada ara blocos marcados com `behavesAs` till |

**Nada disto alcança um pacote.** Estas opções só mudam o cacto, a cana, as folhas e os caminhos do próprio Minecraft. Um bloco que o seu pacote define com `"type": "cane"` carrega a sua própria seção `growth` e cresce até a altura que você deu, haja o que houver instalado.

### Spawners inquebráveis

*bônus: ajustes do vanilla*

`unbreakableSpawners` dá ao bloco de spawner de mobs os números da bedrock, uma dureza inquebrável e uma resistência a explosões que nada supera. Um jogador não consegue minerar um por melhor que seja a picareta, e nem creepers, TNT, nem uma entidade de pacote que `explodes` removerão um. O modo criativo ainda os remove, exatamente como ainda remove a bedrock, de modo que um autor de pacote nunca fica trancado fora da própria construção. Exige reinicialização, já que os números do bloco são definidos quando ele é registrado.

**É o bloco, não o spawner.** Não há chave por spawner. A opção muda o próprio `minecraft:spawner`, então alcança todo spawner do mundo de uma vez: as estruturas vanilla que colocam um, qualquer um que um mod coloque e qualquer um que os seus próprios pacotes coloquem. Um spawner dentro de um dos seus modelos `.nbt`, colocado por uma entrada `imprint`, é um bloco de spawner comum com a sua própria block entity, então fica coberto assim que a opção é ligada.

## Chaves que não foram mantidas

*referência*

O que um pacote 1.12.2 pode escrever e esta versão não lê, e por quê. Um pacote que escreve uma delas ainda carrega; a chave não faz nada. Uma linha que diz *ainda não portada* foi adicionada ao 1.12.2 depois que essa parte foi portada e ainda está por vir. Toda outra linha não pode, ou não precisa, ser mantida neste motor. Uma linha marcada *a partir do 26.3* é o que um pacote 26.3 pode escrever e esta versão não consegue guardar; o log nomeia cada uma quando ela é descartada. A tabela é mantida atualizada conforme as linhas avançam.

| Chave | Onde | Por quê |
| --- | --- | --- |
| `meta` | blocos, itens, worldgen, drops de blocos | Os ids não carregam metadados desde a flattening. A portagem passa todo `name:meta` pelos data fixers do jogo e descarta a chave |
| `oreDict` | blocos, itens, fornalha, combustíveis, filtros de armazenamento de entidades | O ore dictionary acabou. A portagem o transforma em `tags` nas tags de convenção, e o de um combustível ou de uma entrada de filtro de armazenamento em `tag` |
| `galacticraft` | variantes de entidade | Não existe Galacticraft para esta versão, então uma variante não tem nível de foguete, tanque de combustível, carga ou carga útil para definir |
| `oreDictionary` | settings | O ore dictionary acabou, então não restam arquivos de ore dictionary para desligar. As tags fazem o seu trabalho, e a portagem as escreve a partir do `oreDict` de um pacote |
| `modelMeta` | blocos | Os modelos são gerados por variante, então não há metadados para mapeá-los |
| `disableOverrides`, `tolerateMissingInAdvancements` | settings | Um pacote de dados substitui uma receita ou um avanço vanilla ao trazer um com o mesmo nome |
| nomes de item `#CONSTANT` | receitas | As constantes de receita viviam no `_constants.json` de um mod 1.12.2, que nenhum pacote traz e nenhum mod desta versão tem. Nomeie o item ou a tag que a constante representava |
| `harvestTool` diferente de `pickaxe`, `axe`, `shovel`, `hoe` e `sword` | substituições de propriedades | Aqui as ferramentas mineram por tags de bloco, e só essas cinco têm uma. Uma classe de ferramenta inventada por um mod 1.12.2 não tem tag para escrever, então as ferramentas do bloco são deixadas como estão e o log avisa |
| `careers` | aldeões | Não há carreiras desde o 1.14, então cada carreira vira um arquivo de aldeão próprio |
| `career` | trocas, entidades | Sem carreiras; nomeie a própria profissão. Uma troca que nomeia uma profissão vanilla do 1.12.2 com a sua carreira vai para a profissão em que essa carreira se transformou |
| `gameLoopFunction` | game rules | A game rule acabou; a tag de função `#minecraft:tick` executa uma função a cada tick, e a portagem escreve uma |
| `id` | biomas, dimensões | Biomas e dimensões são conhecidos pela sua resource location, nunca por um número |
| `suffix`, `keepLoaded` | dimensões | A pasta de save segue o nome da dimensão. Só o Overworld tem chunks de spawn, então uma dimensão que precisa ficar carregada usa um `forceload` |
| `baseHeight`, `heightVariation` | biomas | A altura do terreno pertence às noise settings, não ao bioma |
| `placement.villageSpawn` | biomas | Os aldeões vêm com a própria estrutura da vila |
| `rubicWorld`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist`, `verticalCubeLoadDistance`, `cubeGCInterval`, `cubeGenMillisPerRound`, `cubesSentPerTick` | mundos rubic | O mundo rubic era o jeito do 1.12.2 de passar de um mundo de 256 blocos. Aqui o `minHeight` e o `maxHeight` de uma dimensão definem o seu tamanho e o sistema de chunks o transmite |
| `rubicHeightLimit` | mundos rubic | O teto de altura do mundo rubic se foi com ele; o `minHeight` e o `maxHeight` de uma dimensão definem o seu tamanho, sem um teto separado para elevar |
| `regionCacheLimit` | mundos rubic | O seu cache dos arquivos de região abertos de um mundo rubic se foi com o mundo rubic; o sistema de chunks daqui gerencia os próprios arquivos |
| `skyStone`, `skyShape`, `skyIslands`, `skyThickness`, `skyHeights`, `skyAnimals` | o mundo profundo, biomas, regiões de caverna | Terra no céu vivia acima da janela de terreno de um mundo rubic, e não há mundo rubic para abrigá-la |
| `deepRavines`, `oreVeins` | o mundo profundo | O terreno do próprio motor já corre abaixo de y 0, com ravinas e grandes veios de minério próprios |
| `terrainOffset` | settings | Não há janela vanilla fixa para deslocar; o `minHeight` e o `maxHeight` de uma dimensão definem o seu piso e o seu teto |
| `terrainWorldTypes`, `terrainWorldTypesAreBlacklist` | settings | Os tipos de mundo são predefinições de mundo aqui, e um modelo de mundo nomeia as suas próprias |
| `biomeSize`, `riverSize`, `dungeonChance`, `waterLakeChance`, `lavaLakeChance`, as chaves de tamanho, quantidade e altura de minérios e as chaves de escala de ruído de um `generatorOptions` personalizado | settings, dimensões | O tamanho dos biomas e a forma do terreno pertencem às noise settings, e a frequência com que uma feature ou um minério é colocado à sua própria placed feature, então nenhum número isolado os alcança. Um `fixedBiome` numerado acima de 39 nomeia um bioma que esta versão não consegue mapear |
| `useWaterLakes`, e `lake` e `dungeon` em um texto superflat | settings, dimensões | O jogo não tem lagos de água desde o 1.18. Masmorras em um mundo plano vêm com `decoration` e não sozinhas |
| `inherit` como a fonte de `biomes` de uma dimensão `flat` ou `void` | dimensões | Um gerador plano guarda um único bioma, então tal dimensão usa o que o seu texto superflat nomeia, ou plains |
| a dimensão de outro mod em `flatBedrockDimensions` ou `voidWorldDimensions` | settings | Bedrock e vazio são gravados na predefinição de mundo gerada e nos arquivos de dimensão do próprio pacote; uma dimensão que outro mod cria é construída a partir de arquivos próprios |
| o tipo de mundo de um mod, ou `debug_all_block_states`, como `worldType` | settings | Os tipos de mundo são predefinições de mundo agora, e o mundo moldado é construído sobre o padrão do próprio jogo, biomas grandes, amplificado ou ruído plano. Um mundo de depuração não tem terreno para moldar |
| `pregenKeepLoaded`, `pregenPauseAbove`, `pregenMillisPerRound`, `pregenRelightSays`, `hurryWritesAbove` | pré-geração | Ajustavam o gravador de chunks e a passada de relight do 1.12.2. O sistema de chunks daqui ilumina o terreno enquanto o cria e grava no seu próprio ritmo |
| `readCofhWorldFiles` | settings | O CoFH World e o seu formato de arquivo próprio não existem neste motor. Traduzir esses arquivos para um pacote, como o 1.12.2 já recomendava, continua sendo o caminho |
| `name:meta` em `villagePathLamp*` | vilas | Os ids não carregam metadados, então um bloco de lâmpada é escrito com o seu estado entre colchetes, e dados de bloco entre chaves ainda são lidos |
| `harvestTool` que nomeia `shears` ou uma classe de ferramenta de mod | blocos | Uma ferramenta aqui lê tags de bloco, não um nome de classe, então nada responde a um. Nomeie a tag de bloco própria da ferramenta do mod nas `tags` da variante |
| o rótulo de aba de outro mod em `creativeTab` | blocos, itens, fluidos | Uma aba é conhecida pelo seu id agora, então um rótulo solto é lido como uma aba do próprio pacote. Nomeie a aba do mod pelo seu id, como `modid:main` |
| `/rdpl reload <group>` | comandos | O jogo recarrega todos os recursos de uma vez, então texturas, modelos, idiomas, sons e shaders não podem ser recarregados isoladamente. `/rdpl reload` ou F3+T os recarrega todos |
| `modernChestPlacement` | ajustes do vanilla | O jogo pareia baús assim desde o 1.13: um baú só se junta a um baú simples ao lado quando ambos olham para o mesmo lado, e agachar o mantém simples |
| `loadingScreenPercent` | settings | A própria tela de carregamento de mundo do jogo já mostra quanto da área de spawn está pronta |
| `disableOptimizations` | settings | Desativava as otimizações de pré-geração e geração do 1.12.2, escritas para aquele motor e sem equivalente aqui |
| `fixTinkersModelErrors` | settings | Silenciava os erros de modelo que as versões 1.12.2 do Tinkers' Construct e do Construct's Armory registravam para toda ferramenta, peça e armadura. A correção alcançava aquelas versões, então não há nada em que ela atue aqui |
| critérios `achievement.` | pontuação, funções | As conquistas viraram avanços, que não guardam pontuação para contar. Um critério `stat.` é lido como a estatística em que se transformou |
| `toggledownfall`, `stats` | funções | Os comandos acabaram. `weather` nomeia o clima a definir, e `execute store` guarda o resultado de um comando |
| `gamerule gameLoopFunction`, e uma game rule inventada por um pacote | funções | O jogo decide quais game rules existem. A portagem transforma o `gameLoopFunction` de um arquivo de game rules na tag `#minecraft:tick`, mas uma linha de comando que o define é mantida para você removê-la |
| um nome com o valor de dados `-1` ou `*` que virou vários blocos ou itens | funções: `clear`, `testforblock`, `execute ... detect`, `fill ... replace`, `clone ... filtered` | Um teste nomeia agora um só bloco ou item. Nomeie o pretendido, ou uma tag como `#minecraft:wool` |
| um estado de bloco escrito como pares `name=value` que não é o estado completo do bloco no 1.12.2, e um valor de dados em um bloco ou item de outro mod | funções | Os data fixers só conhecem estados completos do 1.12.2, e os metadados de outro mod não têm nome achatado para onde ir |
| as partículas `footstep` e `take`, `locate Temple`, `spreadplayers` com mais de um alvo | funções | As partículas acabaram, um templo agora são quatro estruturas, e `spreadplayers` aceita um só argumento de alvo |
| `debug_functions` | noise settings, *a partir do 26.3* | O 26.2 e anteriores não têm funções de densidade de depuração, então a lista é descartada |
| `exclusion` e `surface_level` em `aquifers` | noise settings, *a partir do 26.3* | O 26.2 e anteriores embutem os dois, então os do próprio pacote são descartados |
| uma entrada `spawn_target` que nomeia qualquer coisa além de um eixo climático do `noise_router` | noise settings, *a partir do 26.3* | Um alvo de spawn mais antigo cobre só os cinco eixos climáticos, então essa entrada é descartada |
| veios de minério colocados por meio de `material_rule` | noise settings, *a partir do 26.3* | O 26.2 não consegue colocar veios de minério por uma regra de material, então eles são descartados |
| `count`, `thickness`, `weird_thickness_bias` ou `start_vertical_radius_multiplier` próprios de um escavador de cavernas | escavadores, *a partir do 26.3* | O 26.2 não consegue defini-los, então é usada a forma de caverna vanilla do Overworld ou do Nether |
| `creature_world_gen_spawn_probability` como modificador, e um atributo que se acrescenta ao valor da sua dimensão | biomas, *a partir do 26.3* | O 26.2 aceita um valor simples: a chance de spawn padrão é mantida, e o valor do próprio bioma substitui o da dimensão |
| deslocamentos x e z diferentes, e `normalize` definido como false | placed features, ruídos, *a partir do 26.3* | O 26.2 espalha os dois eixos por uma única quantidade, então ambos usam o deslocamento x, e sempre normaliza um ruído |
| um tipo de feature que o 26.2 não tem, ou uma feature do 26.3 construída com blocos que ele não tem | features, biomas, *a partir do 26.3* | Tal feature não coloca nada, e um bioma omite uma placed feature do 26.3 sem equivalente no 26.2 |
| `straw_bed_rule`, e `destroy_on_leave` em uma regra de cama | atributos de ambiente, *a partir do 26.3* | O 26.2 não tem nenhuma das duas regras, então ambas são descartadas |
| um item com quantidade ou componentes entre as decorações de vaso | arquivos de dados, funções, *a partir do 26.3* | O 26.2 lê um item simples, então a quantidade e os componentes são descartados |
| `compute`, `posteffect`, `item fill`, `item override` e `execute if slots`, e a animação de `swing` | funções, *a partir do 26.3* | O 26.2 não tem nenhum desses comandos, então a linha é descartada; uma linha `swing` é mantida e balança de forma simples |
| `shade_direction_override` diferente de `up`, e `trim_overrides` | modelos, assets de equipamento, *a partir do 26.3* | O 26.2 sombreia tal elemento normalmente, e lê paletas de acabamento por equipamento a partir do `override_armor_assets` do material de acabamento |
| componentes em um item preparado, e uma tag ou lista de itens a partir dos quais preparar | receitas de poção, *a partir do 26.3* | Uma receita de poção do 26.2 não pode nomear nenhum dos dois: os componentes são descartados, e uma receita que prepara a partir de uma tag ou lista é descartada inteira |
| um provedor de números para o qual o 26.2 não tem forma, e um fallback diferente de 0 | tabelas de loot, predicados, modificadores de item, *a partir do 26.3* | O provedor vira 0, e uma pontuação ou número armazenado ausente recorre a 0 |
| um teste de condição de bloco diferente de `blocks` e `state`, um conjunto de ids onde o 26.2 lê um, uma tag de predicados ou de modificadores de item, um mapa de explorador para estruturas nomeadas | tabelas de loot, predicados, modificadores de item, *a partir do 26.3* | O teste é descartado, só o primeiro id é mantido, uma tag é lida como o único id que nomeia, e o mapa leva a estruturas de tesouro |

# Resource Data Pack Loader

**Uma pasta que substitui qualquer coisa que o Minecraft ou um mod fornece, define conteúdo novo a partir de JSON e controla o que é gerado, em todos os mundos, em clientes e servidores, sem nada para os jogadores ativarem.**

Catorze exemplos funcionais. Coloque qualquer um deles direto em `rdploader` e veja como cada arquivo é escrito.

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) cobre a maioria dos recursos, blocos, itens, biomas, uma dimensão, um modelo de mundo e todas as formas de worldgen.
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) transforma o overworld em um vazio, com worldgen suspensa no ar, uma forma por faixa de altura, de modo que cada uma seja fácil de ver isoladamente.
- [RDPLExampleVeinShapes.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapes.zip) coloca três veios de minério em um overworld comum, um por padrão de veio (simples, em faixas e em tubo), cada um com camadas rica, normal e pobre e alguns blocos marcadores na superfície acima dele para servir de guia na prospecção.
- [RDPLExampleVeinShapesVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapesVoid.zip) suspende os mesmos três padrões de veio em um vazio, para que cada forma possa ser vista por inteiro.
- [RDPLExampleDeepWorld.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleDeepWorld.zip) transforma o overworld em um mundo rubic com 256 blocos de mundo gerado abaixo do vanilla e 128 acima dele: a mistura de pedra profunda, cavernas de ruído modernas, ravinas, veios de minério em faixas, três regiões de cavernas para descer e ilhas flutuantes no alto, recortadas pelo mesmo ruído.
- [RDPLExampleContainers.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleContainers.zip) adiciona blocos e itens carregáveis que guardam um inventário, em todos os tamanhos, de três espaços até o maior permitido, com uma tabela de saque, o modelo de baú tingido a partir da folha vanilla, todas as texturas desenhadas como mapas de pixels e duas bolsas que podem ser usadas no Baubles.
- [RDPLExampleMegaCity32.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity32.zip) cria um mundo superplano com uma única vila deliberadamente enorme, crescida até mil terrenos e fixada na origem, com as ruas decoradas como estradas de concreto, calçadas, faixas centrais tracejadas e postes de luz, esgotos sob as ruas, duas linhas de metrô com estações abaixo delas e uma ferrovia atravessando a cidade, e os prédios compostos a partir de mapas de estruturas em quatro tamanhos e três fachadas, em vez de casas vanilla.
- [RDPLExampleMegaCity64.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity64.zip) é essa mesma cidade em um mundo rubic com o teto em 512 e as nuvens elevadas para 384, de modo que as torres se erguem 256 blocos acima da rua, e cada distrito sorteia uma profundidade de bloco de 16, 32 ou 64, para que uma grade grossa se misture a uma fina.
- [RDPLExampleCityCustomMap.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleCityCustomMap.zip) desenha essa mesma cidade a partir de um mapa de cidade, em vez de sorteá-la: uma grade de caracteres com 48 blocos por célula, com uma paleta que nomeia ruas, praças, vielas e escolhas ponderadas de prédios, de modo que a planta dos quarteirões é traçada à mão.
- [MCTKamikazeDemo.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/MCTKamikazeDemo.zip) coloca quatro facções umas contra as outras em uma arena de bedrock sob noite permanente: cada lado é uma equipe real do placar vanilla à qual seus mobs se juntam ao nascer, um lado pontua por cada mob de outro lado que mata, uma rodada termina em uma carta após dois minutos e três rodadas formam uma partida.
- [RDPLExampleRaid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleRaid.zip) monta um mundo plano em torno de uma vila cujo poço é um pavilhão com um sino e dá ao jogador um Mau Presságio ao entrar: cinco ondas de illagers vanilla, uma bruxa e os Pillagers, Capitães da Invasão, Lançadores de Machado e Ravagers do próprio pacote, o presságio e Herói da Vila como efeitos do próprio pacote com ícones em mapa de pixels, uma bebida que traz o presságio de volta e as funções que encerram a invasão.
- [RDPLExampleGalacticraft.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGalacticraft.zip) exige o Galacticraft e coloca um sistema estelar próprio no mapa estelar, com um planeta alcançado por um foguete de nível 2 que tem céu, gravidade, duração do dia, clima e atmosfera próprios, um planeta para apenas observar e nunca pousar e, com o GalaxySpace, um planeta de gelo em torno de Tau Ceti.
- [RDPLExampleGameHall.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGameHall.zip) transforma um mundo plano em um salão de jogos: dados ponderados do próprio pacote, um baralho de mobs que se embaralha sozinho e um baralho da sorte que fica vazio até ser embaralhado, um Copo de Dados que rola 2d6 com um clique direito, um Duelo de dados em turnos contra a Casa que resolve um empate por sorteio, e xadrez e damas jogados com mobs contra o computador.
- [RDPLExampleColony.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleColony.zip) constrói sozinho um pátio de trabalho ao fechar a introdução e põe aldeões para fazer todo tipo de ordem de trabalho: um minerador cujo alcance cresce com a picareta, um lenhador que guarda o que corta, um fazendeiro que mantém um estoque fixo de trigo e um carregador que transporta pedregulho, cada ordem aberta por uma placa, uma ferramenta, uma contratação ou um baú de estoque.
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
- [Pacotes escritos para 1.20.1, 1.21.1 e 26.x](#pacotes-escritos-para-1201-1211-e-26x)

**Blocos e itens**
- [Blocos](#blocos)
- [Contêineres](#contêineres)
- [Modelos, blockstates e texturas](#modelos-blockstates-e-texturas)
- [Blockstates por tipo](#blockstates-por-tipo)
- [Fazendo o vanilla tratar seu bloco corretamente](#fazendo-o-vanilla-tratar-seu-bloco-corretamente)
- [Itens](#itens)
- [Fluidos](#fluidos)
- [Materiais, abas, sons, dicionário de minérios](#materiais-abas-sons-dicionário-de-minérios)
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
- [Corpos do Galacticraft](#corpos-do-galacticraft)
- [Portais e passagens](#portais-e-passagens)
- [Mundos Rubic](#mundos-rubic)
- [O mundo profundo](#o-mundo-profundo)
- [Regiões de cavernas](#regiões-de-cavernas)

**Gerando o mundo**
- [Entradas de worldgen](#entradas-de-worldgen)
- [Formas](#formas)
- [Dispersões](#dispersões)
- [Mapas de estruturas](#mapas-de-estruturas)
- [Terrenos de vilas](#terrenos-de-vilas)
- [Mapas de layout de cidades](#mapas-de-layout-de-cidades)
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
- [Universal Tweaks](#universal-tweaks)
- [Mo' Villages](#mo-villages)
- [CoFH World](#cofh-world)
- [Lost Cities](#lost-cities)
- [Integração com Blast Plaster](#integração-com-blast-plaster)
- [Mods de túmulos](#mods-de-túmulos)

**Referência**
- [Listas de valores](#listas-de-valores)
- [Lista de pastas](#lista-de-pastas)
- [Comandos](#comandos)
- [Bom saber](#bom-saber)
- [Quando algo não funciona](#quando-algo-não-funciona)
- [Bônus: ajustes do vanilla](#bônus-ajustes-do-vanilla)
- [Bônus: correção de conflito de plugins do JEI](#bônus-correção-de-conflito-de-plugins-do-jei)
- [Bônus: menos erros na inicialização](#bônus-menos-erros-na-inicialização)

---

# Primeiros passos

## O que é

*primeiros passos*

O Resource Data Pack Loader (RDPL) lê uma única pasta, `rdploader`, e faz três trabalhos:

- **Substituições.** Um arquivo na pasta substitui aquele que o jogo ou um mod teria carregado. Sem interruptor, sem configuração por mundo, nada para os jogadores ativarem.
- **Conteúdo novo.** Definições em JSON registram blocos, itens, fluidos, biomas, dimensões, poções e aldeões. Sem Java, sem jar.
- **Controle.** Bloqueie a geração de minérios, biomas, estruturas ou receitas, achate a bedrock, defina taxas de spawn, esvazie o overworld, defina os padrões do mundo.

## Onde os arquivos ficam

*primeiros passos*

Todo caminho neste guia é escrito a partir de `assets/`, de modo que `<namespace>/blocks/*.json` é `assets/mypack/blocks/ruby_ore.json` no disco para um pacote cujo namespace é `mypack`. Cada seção repete seu próprio caminho sob o título, com uma nota sobre no que esse caminho se transforma.

| Caminho | O que contém |
| --- | --- |
| `<namespace>/blocks/*.json` | Definições de blocos. [Blocos](#blocos) |
| `<namespace>/items/*.json` | Definições de itens. [Itens](#itens) |
| `<namespace>/fluids/*.json` | Fluidos, com um bloco e um balde. [Fluidos](#fluidos) |
| `<namespace>/materials/*.json` | Materiais de ferramentas e armaduras. [Materiais, abas, sons, dicionário de minérios](#materiais-abas-sons-dicionário-de-minérios) |
| `<namespace>/tabs/*.json` | Abas criativas. [Materiais, abas, sons, dicionário de minérios](#materiais-abas-sons-dicionário-de-minérios) |
| `<namespace>/sounds/*.json` | Eventos de som. [Materiais, abas, sons, dicionário de minérios](#materiais-abas-sons-dicionário-de-minérios) |
| `<namespace>/oredict/*.json` | Nomes do dicionário de minérios. [Materiais, abas, sons, dicionário de minérios](#materiais-abas-sons-dicionário-de-minérios) |
| `<namespace>/biomes/*.json` | Definições de biomas. [Biomas](#biomas) |
| `<namespace>/worldgen/*.json` | O que é gerado e onde. [Entradas de worldgen](#entradas-de-worldgen) |
| `<namespace>/caveregions/*.json` | Regiões nomeadas pintadas sobre o subsolo. [Regiões de cavernas](#regiões-de-cavernas) |
| `<namespace>/dimensions/*.json` | Definições de dimensões. [Dimensões](#dimensões) |
| `<namespace>/celestial/*.json` | Sistemas estelares e corpos para o mapa do Galacticraft. [Corpos do Galacticraft](#corpos-do-galacticraft) |
| `<namespace>/worldtemplates/*.json` | As configurações de um mundo inteiro em um só arquivo. [Modelos de mundo](#modelos-de-mundo) |
| `<namespace>/worldintro/*.json` | Páginas exibidas quando um jogador entra no mundo. [Introdução do mundo](#introdução-do-mundo) |
| `<namespace>/gates/*.json` | Condições para portais e dimensões. [Portais e passagens](#portais-e-passagens) |
| `<namespace>/gamerules/*.json` | Regras do jogo para mundos novos. [Regras do jogo](#regras-do-jogo) |
| `<namespace>/teams/*.json` | Lados no placar vanilla e o que se junta a eles. [Equipes](#equipes) |
| `<namespace>/scoring/*.json` | Objetivos, pontos e como uma partida termina. [Pontuação](#pontuação) |
| `<namespace>/raids/*.json` | Ondas que vêm atrás de uma vila quando um jogador leva um presságio até ela. [Invasões](#invasões) |
| `<namespace>/entities/*.json` | Variantes de entidades criadas sobre entidades que já existem. [Variantes de entidades](#variantes-de-entidades) |
| `<namespace>/hardness/*.json` | Tempo de mineração e multiplicadores de explosão para grupos de blocos. [Grupos de dureza](#grupos-de-dureza) |
| `<namespace>/exposures/*.json` | Perigos que expõem jogadores perto de blocos nomeados, carregando itens nomeados ou em dimensões nomeadas. [Exposições](#exposições) |
| `<namespace>/orders/*.json` | Trabalhos que variantes de entidade fazem para um jogador em um baú. [Ordens de trabalho](#ordens-de-trabalho) |
| `<namespace>/overrides/<target>/<name>.json` | Propriedades de blocos, itens e tipos de poção existentes, alteradas no lugar. [Substituições de propriedades](#substituições-de-propriedades) |
| `<namespace>/villages/*.json` | Terrenos que as vilas podem construir. [Terrenos de vilas](#terrenos-de-vilas) |
| `<namespace>/pathintersects/*.json` | Desenhos pintados onde as estradas da vila se cruzam. [Estradas de vilas](#estradas-de-vilas) |
| `<namespace>/structuremaps/*.json` | Modelos compostos em um grande prédio sobre uma grade. [Mapas de estruturas](#mapas-de-estruturas) |
| `<namespace>/citymaps/*.json` | Uma planta de ruas desenhada a partir da qual uma vila é traçada em vez de crescer. [Mapas de layout de cidades](#mapas-de-layout-de-cidades) |
| `<namespace>/portalframes/*.json` | Molduras que um jogador pode construir e acender. [Molduras de portais](#molduras-de-portais) |
| `<namespace>/blastplaster/*.json` | O que o Blast Plaster faz depois de uma explosão, por dimensão. [Integração com Blast Plaster](#integração-com-blast-plaster) |
| `<namespace>/structures/*.nbt` | Modelos, para mudas, `imprint` e substituições de mods. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/recipes/*.json` | Receitas de criação, adicionadas ou substituídas. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/recipe_removals/*.json` | Receitas removidas por nome, namespace ou resultado. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/disabled/*.json` | Blocos e itens retirados de jogo. [Blocos e itens desativados](#blocos-e-itens-desativados) |
| `<namespace>/furnace/*.json` | Receitas de fornalha adicionadas e removidas. [Receitas de fornalha e combustíveis](#receitas-de-fornalha-e-combustíveis) |
| `<namespace>/fuels/*.json` | Tempos de queima. [Receitas de fornalha e combustíveis](#receitas-de-fornalha-e-combustíveis) |
| `<namespace>/brewing/*.json` | Receitas do suporte de poções. [Poções, tipos de poção e fermentação](#poções-tipos-de-poção-e-fermentação) |
| `<namespace>/potions/*.json` | Efeitos de poção. [Poções, tipos de poção e fermentação](#poções-tipos-de-poção-e-fermentação) |
| `<namespace>/potion_types/*.json` | Poções engarrafadas criadas a partir desses efeitos. [Poções, tipos de poção e fermentação](#poções-tipos-de-poção-e-fermentação) |
| `<namespace>/villagers/*.json` | Profissões de aldeões. [Aldeões e trocas](#aldeões-e-trocas) |
| `<namespace>/trades/*.json` | O que as carreiras compram e vendem. [Aldeões e trocas](#aldeões-e-trocas) |
| `<namespace>/loot_tables/*.json` | Tabelas de saque, substituídas. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/loot_injections/*.json` | Um pool adicionado a uma tabela que já existe. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/block_drops/*.json` | Drops extras ou substitutos para blocos que um pacote não possui. [Drops de blocos](#drops-de-blocos) |
| `<namespace>/anvils/*.json` | Encantamentos que uma bigorna aplica a um item nomeado, uma conquista que ele concede e um bloqueio até lá. [Trabalho na bigorna](#trabalho-na-bigorna) |
| `<namespace>/cards/*.json` | Cartas na tela exibidas por um gatilho e as mensagens que este mod diz por conta própria. [Cartas](#cartas) |
| `<namespace>/dice/*.json` | Dados do pacote com faces ponderadas, baralhos de cartas, quem ouve uma rolagem e o texto dos resultados. [Dados e baralhos](#dados-e-baralhos) |
| `<namespace>/games/*.json` | Jogos de tabuleiro com criaturas como peças: o tabuleiro, as peças e como se movem, e o que um resultado paga. [Jogos de tabuleiro](#jogos-de-tabuleiro) |
| `<namespace>/player_loot/*.json` | Uma tabela de saque sorteada quando um jogador morre. [Saque de jogadores](#saque-de-jogadores) |
| `<namespace>/advancements/*.json` | Conquistas. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/functions/*.mcfunction` | Arquivos de função. [O que você pode substituir](#o-que-você-pode-substituir) |
| `<namespace>/registry_remap/*.json` | Nomes antigos mapeados para novos. [Renomeações de registro](#renomeações-de-registro) |
| `<namespace>/texts/*.txt` | Arquivos de texto simples, usados pela introdução do mundo. [Introdução do mundo](#introdução-do-mundo) |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | As pastas de assets de sempre. [Modelos, blockstates e texturas](#modelos-blockstates-e-texturas) |

## Lendo as tabelas

*primeiros passos*

Todo arquivo é JSON padrão. Uma entrada de worldgen representativa:

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

As tabelas de chaves neste documento informam se uma chave é obrigatória, o que ela guarda e o padrão quando omitida. Valores não reconhecidos são registrados no log e substituídos pelo padrão; eles não travam o jogo. Tipos de valor usados em todo o documento:

| Quando uma tabela diz | Você escreve |
| --- | --- |
| int | `8` |
| int, ticks | `100` (20 ticks = 1 segundo) |
| int ou intervalo | `8`, ou `{ "min": 4, "max": 12 }` para sortear entre eles |
| 0 a 15, 1 a 100 e similares | um int dentro desses limites |
| float | `0.5` |
| boolean | `true` ou `false` |
| string | `"palavras entre aspas"` |
| nome de bloco, nome de item | `"minecraft:stone"`, metadados como uma terceira parte: `"minecraft:stone:3"` |
| `namespace:name` | `"mypack:ruby_ore"` |
| nome de bioma, nome de som, nome de aba | a mesma forma `namespace:name` entre aspas |
| cor hexadecimal | seis dígitos hexadecimais, `"A0C8FF"`, `#` opcional |
| caminho de textura | `"mypack:blocks/ruby_ore"` |
| lista de ints | `[0, -1]` |
| lista de nomes de blocos | `["minecraft:stone", "minecraft:andesite"]` |
| lista de nomes de biomas | `["minecraft:extreme_hills", "mypack:ruby_hills"]` |
| lista de tipos do dicionário | `["MOUNTAIN", "FOREST"]` |
| lista de ids de mods ou namespaces de pacotes | `["quark", "mypack"]` |
| lista de objetos | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, chaves conforme a tabela do próprio objeto |
| objeto | `{ "type": "cluster" }`, chaves conforme a própria tabela |
| objeto de papel para bioma, de nome de variante para variante | as chaves são o primeiro item, os valores o segundo: `{ "ocean": "mypack:ruby_ocean" }` |

A maioria das definições também aceita `requires`, uma lista de ids de mods ou namespaces de pacotes que precisam estar presentes, ou o arquivo é ignorado.

## A única regra

*primeiros passos*

Abra o jar, encontre o arquivo que você quer alterar e copie o caminho dele de `assets` em diante:

```
assets/minecraft/textures/blocks/iron_ore.png        (in the Minecraft jar)
rdploader/assets/minecraft/textures/blocks/iron_ore.png    (your override)
```

O caminho depois de `assets` é sempre idêntico ao caminho dentro do jar. Nada é renomeado ou movido.

## Organizando pacotes

*primeiros passos*

Arquivos soltos funcionam em `rdploader/assets/<namespace>/`. Agrupar também funciona, como um zip. Uma pasta em `rdploader` nunca é um pacote: ela é ignorada com um aviso no log, então compacte um pacote em zip antes de colocá-lo ali.

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png
rdploader/MyTextures.zip
```

**Pacotes na pasta errada.** Na inicialização, antes de ler `rdploader`, o RDPL examina a pasta `resourcepacks` do jogo e move para `rdploader` todo zip de pacote RDPL que encontrar. Um zip é um pacote RDPL quando contém arquivos de definição do RDPL, como `assets/<namespace>/blocks/` ou, em um pacote moderno, `data/<namespace>/blocks/`. Um pacote de recursos comum fica onde está. Um zip cujo nome `rdploader` já contém é deixado no lugar, assim como um pacote RDPL dentro de uma pasta; ambos recebem um aviso. Cada movimentação é registrada em `logs/rdpl.log`. Um pacote movido deixa de aparecer na lista de pacotes de recursos, e o RDPL passa a carregá-lo de `rdploader`.

**Prioridade.** Quando dois pacotes contêm o mesmo arquivo, prefixe os nomes com `RDPL` e um número; números maiores carregam depois e vencem:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Sem diferenciar maiúsculas de minúsculas; um espaço, hífen ou sublinhado depois do número é opcional; o prefixo fica oculto no nome exibido. Um pacote sem prefixo carrega primeiro e perde para qualquer pacote numerado. A prioridade também ordena as entradas de worldgen, o que importa quando um pacote coloca blocos que outro substitui.

**Desative um pacote** acrescentando `.disabled` ao nome dele.

**Um zip para todas as versões.** Um zip pode conter uma pasta `versions/<version>/` para cada versão do Minecraft que ele atende: `versions/1.12.2/`, `versions/1.20.1/`, `versions/1.21.1/` e, no 26.x, a versão exata em que roda, `versions/26.1.2/`, `versions/26.2/` ou `versions/26.3/`. Cada uma é organizada como a raiz de um pacote para aquela versão, `pack.mcmeta` incluído. Um arquivo na pasta da versão em execução é lido no lugar do mesmo caminho na raiz; a raiz é compartilhada por todas as versões, e a pasta de outra versão nunca é lida. Coloque na raiz o que todas as versões leem do mesmo jeito e, em uma pasta de versão, apenas o que difere, e um zip carrega nas quatro.

**Os pacotes são convertidos entre versões.** Carregar um pacote escrito para outra linha o converte na primeira vez, da mesma forma, gravando o que mudou em sua própria pasta `versions/<version>/` como acima; isso acontece automaticamente no carregamento, inclusive no carregamento que um `/rdpl reload` ou `/rdplserver reload` dispara, nunca por um comando próprio. Todo par de versões converte nos dois sentidos, então um pacote 1.12.2, 1.20.1, 1.21.1 ou 26.x carrega em qualquer uma das outras. Um pacote 26.3 também carrega em todas as linhas, lido pelo formato 26.2 nas mais antigas, e um pacote mais antigo carrega no 26.3. O que um lado tem e o outro não consegue guardar é descartado, e o log nomeia cada descarte: ao descer do 26.3, entre outros, funções de densidade de depuração, a exclusão de aquífero e o nível de superfície próprios de um pacote e comandos que o 26.2 não tem; ao subir para o 26.3, a `depth` e o `offset` de um alvo de spawn e as chaves de veio de minério do roteador de ruído. No 1.12.2, a worldgen vanilla de um pacote moderno, como seus biomas, características e configurações de ruído, e sua pasta `neoforge` também ficam de fora, já que o 1.12.2 não tem equivalente para nenhum dos dois.

## Pacotes de recursos: quem vence

*primeiros passos*

Por padrão, os arquivos do RDPL ficam acima dos pacotes de recursos que um jogador seleciona, então um pacote de recursos não pode substituí-los. Acrescente `O` ou `N` depois do prefixo `RDPL` para decidir por pacote:

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Pacotes sem letra seguem a opção de config `overrideResourcePacks`. `/rdpl list` marca os pacotes que substituem. A letra precisa encerrar o prefixo (seguida de espaço, hífen, sublinhado ou nada), então `RDPLOverhaul` é um pacote chamado `Overhaul`, não uma flag `O`.

---

# Como os pacotes funcionam

## Como as definições funcionam

*como os pacotes funcionam*

As pastas de um pacote fazem duas coisas: algumas descrevem coisas novas, e as demais substituem arquivos que o jogo ou um mod já tem, o que é tratado em [O que você pode substituir](#o-que-você-pode-substituir). Para o primeiro tipo, o caminho é a identidade: um arquivo em `assets/mypack/blocks/ruby_ore.json` registra um bloco chamado `mypack:ruby_ore`.

O registro acontece na menor prioridade que o Forge oferece, então se um mod de verdade registra o mesmo nome, o mod vence e seu arquivo é ignorado. Nada aqui pode substituir um mod.

**Onde fica o limite.** Tudo que precisa de uma tile entity, uma GUI, um inventário ou lógica própria por tick precisa de um mod de verdade. Tudo que não chega a isso está liberado.

### Seu namespace é o seu mod

*como as definições funcionam*

O namespace que você escolhe é, para todos os efeitos práticos, um id de mod. Nada é carregado como mod e ele nunca aparece na lista de mods, mas tudo que lê um id de mod lê o seu:

- Os nomes de registro são `mypack:ruby_ore`, exatamente como os de um mod, e são gravados em todo mundo salvo que os contenha.
- As listas de permissão de minérios, biomas, geradores e receitas na config o reconhecem, então `oreWhitelist = mypack` mantém seu minério e os blocos de todos os outros.
- `/rdpl which`, `/rdplserver oregen` e os relatórios agrupam tudo por ele.
- O JEI, o dicionário de minérios e as consultas de outros mods o veem do mesmo jeito.

Portanto escolha um nome no começo e nunca o mude. Renomear um namespace deixa órfão tudo que já foi colocado em um mundo, igual a um mod que muda de id; é para reparar isso que `registry_remap` existe.

Isso vale nos dois sentidos: `requires` aceita um namespace de pacote tão bem quanto um id de mod instalado, então um pacote pode depender de outro e ser ignorado quando ele não estiver instalado.

**Um mod ausente interrompe o jogo, como acontece com a dependência de um mod de verdade.** Todo id de mod nomeado por um `requires` em qualquer lugar dos seus pacotes é entregue ao Forge como dependência deste mod, antes de qualquer coisa carregar. Se um não estiver instalado, você recebe a tela padrão de Mods Ausentes nomeando o que é necessário, em um cliente ou em um servidor dedicado, e nada é gerado nem registrado nesse meio-tempo.

Um *pacote* ausente é diferente. Namespaces de pacotes não são mods, então nunca chegam a essa verificação: a definição é ignorada, uma linha vai para `logs/rdpl.log` nomeando o que faltava, e o jogo continua. Se um bloco que você esperava não está na aba criativa, essa linha do log é o primeiro lugar para olhar.

`requires` aceita apenas ids simples. Não há sintaxe de intervalo de versões, então ele pode dizer que um mod precisa estar presente, mas não qual versão.

Os dois ids do próprio mod, `resourcedatapackloader` e `resourcedatapackloader_mixin`, são reservados. Definir conteúdo sob eles é ignorado e registrado no log, porque isso reivindicaria a posse de coisas que este mod registra. Substituir os assets do próprio mod continua permitido; só registrar conteúdo ali não é.

Toda tabela abaixo segue as convenções de [Lendo as tabelas](#lendo-as-tabelas).

A maioria das definições também aceita `requires`, uma lista de ids de mods ou namespaces de pacotes que precisam estar presentes, ou o arquivo é ignorado.

### Opções de pacotes

*como as definições funcionam*

Um pacote pode ter uma pasta `config` ao lado de `assets`, contendo arquivos JSON de opções verdadeiro/falso com seus padrões:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

Um arquivo com `"hide": true` no nível superior mantém as opções daquele pacote fora da tela de opções e do arquivo gerado por completo, enquanto as opções ainda controlam o conteúdo em seus padrões. Duas situações pedem isso: conteúdo que não está pronto para distribuição e pacotes modelo, em que as opções são engrenagem que mantém as definições unidas, e não uma escolha que alguém deva fazer. Remova a chave para publicá-los. O mesmo funciona por opção: `"hide": true` dentro do objeto de uma opção oculta apenas aquela, de modo que um pacote concluído pode ter um interruptor para conteúdo inacabado, ou uma porta de modelo, sem que nenhum dos dois apareça:

    { "enablePackB": { "default": false, "hide": true } }

Como uma opção oculta não pode ser alternada, uma oculta com padrão true fica, na prática, forçada como ligada, para conteúdo que precisa permanecer ligado ao mecanismo de opções mas não é uma escolha.

Uma opção também pode ser um objeto com uma descrição, exibida sob o nome dela na tela de opções:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

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
| um nome de opção | sim | boolean ou um objeto | | `true` ou `false` é o padrão da opção. Um objeto carrega as três chaves abaixo |
| `hide` no nível superior | não | boolean | `false` | Mantém as opções deste pacote fora da tela de opções e do arquivo gerado por completo, enquanto ainda controlam o conteúdo em seus padrões |
| `default` | sim | boolean | | O valor da opção até o usuário alterá-lo. Um objeto sem um `default` booleano é ignorado, com um aviso |
| `hide` dentro de uma opção | não | boolean | `false` | Oculta apenas aquela opção, de modo que ela não pode ser alternada e permanece em seu padrão |
| `description` | não | string | nenhuma | Exibida sob o nome da opção na tela de opções |

Na inicialização, os arquivos de opções do pacote viram um arquivo de config real que pertence ao usuário, com o nome do pacote, `rdploader/config/PackA.json`, criado com os padrões do pacote e mesclado nas atualizações do pacote, de modo que as opções novas chegam sem tocar no que o usuário já definiu. As alterações valem no próximo início do jogo. As opções pertencem apenas a pacotes nomeados, isto é, zips, já que o arquivo gerado leva o nome do pacote; arquivos soltos em `rdploader/assets` não têm nome de pacote e não carregam opções, então compacte o conteúdo solto em um pacote nomeado se ele precisar de um interruptor.

A lista `requires` de qualquer definição pode então nomear uma opção com uma entrada `config:`: `"requires": ["config:enableTestingContent"]` registra aquele conteúdo apenas enquanto a opção for true, exatamente como um mod ausente o ignoraria. Um nome simples verifica o arquivo de todos os pacotes, e todo pacote que o define precisa concordar; `"config:PackA:enableTestingContent"` nomeia um só pacote. Uma opção que nenhum pacote define conta como false e gera um aviso uma única vez.

Uma entrada `file:` condiciona a um arquivo ou pasta existente dentro da pasta do jogo, para acoplar conteúdo a algo fora dos pacotes do próprio RDPL, como o pacote de recursos de outro mod: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registra o conteúdo apenas enquanto esse arquivo exato estiver instalado. O caminho é relativo à pasta do jogo, sempre com barras normais, e não pode conter `..`.

### Herdando definições

*como as definições funcionam*

Uma definição de bloco ou de item pode partir de outra do mesmo tipo com `"inherits"`, nomeando o nome de registro de qualquer variante, e então substituir o que for diferente:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "meta": 0, "hardness": 4.0 } } }

O filho copia todos os atributos do arquivo do pai e da variante nomeada, a ordem dos arquivos nunca importa, as cadeias são resolvidas começando pelo pai, e um círculo ou um pai ausente é registrado no log e deixa o filho como foi escrito. Os campos que o filho escreve substituem o valor herdado; as propriedades aninhadas das variantes são substituídas uma a uma, mas listas como `requires` são substituídas por inteiro, então escreva a lista completa que deseja. Blocos herdam apenas de blocos e itens apenas de itens.

### Modelos de blocos e itens

*como as definições funcionam*

Um pai pode ser um modelo puro que nunca entra no jogo, já que a herança lê os próprios arquivos de definição, e não o que foi registrado. Condicione o modelo a uma opção oculta que fica forçada como desligada, e ele não registra nada enquanto seus atributos continuam herdáveis:

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

O modelo nunca é registrado, enquanto `jacks_ore` é registrado com o material, o som, a ferramenta, a aba, os drops de experiência e a resistência do modelo, substituindo apenas a dureza. O filho precisa escrever o próprio `requires`, aqui esvaziado para uma lista vazia, porque senão herda o do pai e desapareceria junto com ele.

## O que você pode substituir

*como os pacotes funcionam*

- **Qualquer coisa na pasta de assets de um mod**, texturas, modelos, blockstates, arquivos de idioma, sons, fontes, textos de splash, livros-guia, manuais
- **Conquistas e tabelas de saque**, no lado do servidor, então funcionam em servidores dedicados também
- **Receitas**, substitua a receita de um mod ou adicione a sua
- **Modelos de estruturas**, os arquivos `.nbt` que os mods usam para prédios gerados, em `<namespace>/structures/`
- **Funções**, os arquivos `.mcfunction` em `<namespace>/functions/`
- **Renomeações de registro**, mantenha mundos antigos funcionando quando um mod renomeia um bloco ou item
- **Remoções de receitas**, exclua uma receita de criação por nome, namespace ou resultado
- **Blocos e itens desativados**, retire qualquer bloco ou item de jogo, veja [Blocos e itens desativados](#blocos-e-itens-desativados)
- **Injeções de saque**, adicione um pool a uma tabela de saque em vez de substituir tudo
- **Saque de jogadores**, sorteie uma tabela de saque quando um jogador morre, além do que ele carregava ou no lugar disso
- **Drops de blocos**, acrescente ou substitua o que qualquer bloco solta quando um jogador o quebra
- **Propriedades de blocos, itens e poções existentes**, dureza, luz, tamanhos de pilha, comida em qualquer coisa, os efeitos de uma poção, veja [Substituições de propriedades](#substituições-de-propriedades)
- **Nomes do dicionário de minérios, receitas de fornalha, tempos de queima de combustíveis, abas criativas e eventos de som**

O RDPL serve bem para substituir uma ou duas receitas, e as receitas para o seu próprio conteúdo devem ser adicionadas no próprio pacote. Para controle total de receitas em um modpack, CraftTweaker e GroovyScript são as melhores opções, e um arquivo aqui ainda substitui o original por completo, então para mudar um ingrediente ou remover uma entrada de saque, use essas ferramentas.

## Pacotes do lado do servidor

*como os pacotes funcionam*

Um pacote pode viver só no servidor, com jogadores em clientes vanilla puros, sob uma restrição: **nada nele pode registrar coisa alguma**. Os dois ids de mod aceitam qualquer remoto; o pacote decide. Um cliente vanilla joga com os registros com que veio, então um pacote que adiciona a eles precisa estar nos dois lados.

| Basta o servidor | Precisa do pacote no cliente também |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `caveregions` | `blocks`, `items`, `fluids`, `materials` |
| `villages`, `pathintersects`, `structuremaps`, `citymaps` | `potions`, `potion_types`, `sounds`, `tabs` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `oredict`, `disabled` | `biomes`, `dimensions`, `portalframes` |
| `loot_tables`, `loot_injections`, `block_drops`, `anvils`, `player_loot`, `advancements`, `functions` | `villagers` |
| `gates`, `cards`, `registry_remap`, `exposures`, `hardness`, `overrides`, `trades` (para profissões que o cliente conhece: as do vanilla ou as de um mod presente nos dois lados) | `entities`, `worldintro`, `texts` (a introdução nunca é exibida a um cliente vanilla) |
| `teams`, `scoring` | `models`, `blockstates`, `textures`, `lang` (pastas de cliente — sem cliente, deixe-as de fora) |
| toda a camada de controle, as configurações e a pré-geração | |

A coluna da direita é uma parada obrigatória: um cliente vanilla enviado a uma dimensão desconhecida é desconectado, e blocos desconhecidos não podem ser descritos para ele. A coluna da esquerda funciona porque tudo ali roda inteiramente no lado do servidor ou chega ao cliente por pacotes que o vanilla já entende (slot de resultado de criação preenchido pelo servidor, pacotes comuns de conquistas, recusas de portais em mensagens de status e uma retenção de pré-geração feita de pacotes vanilla de modo de jogo, título e teleporte).

`worldtemplates` é do lado do servidor, com uma exceção: **`rubicWorld` não pode ser usado com `vanillaClients`**. Um mundo rubic é feito de cubos, e um cliente sem o mod não pode recebê-los, então seria recusado no login ou não veria nada. Com os dois definidos, os mundos novos são criados planos em vez de rubic e o log diz o porquê, em vez de deixar um servidor que recusa todos os jogadores. Ativar `vanillaClients` para um mundo que *já* foi criado como mundo rubic é o único caso que interrompe o jogo por completo: carregar tal save como um mundo comum o arruinaria, então ele é deixado intocado para você decidir.

Configuração:

1. Ative `vanillaClients` na config (categoria `content`, exige reinício). Ele impõe a coluna da direita: essas pastas são ignoradas no carregamento e cada arquivo ignorado é nomeado no log, de modo que um arquivo de bloco que escapou vira uma linha de log em vez de uma conexão recusada.
2. Mantenha as definições fora das pastas da direita de qualquer forma; arquivos ignorados são peso morto. Onde o pacote referencia itens (o `hold` de um portal, `killedDrops`, resultados de receitas, trocas), nomeie apenas itens que o vanilla ou os outros mods presentes nos dois lados do servidor fornecem.
3. Deixe de fora as variantes de entidades. Cada uma é registrada como uma entidade própria, que um cliente vanilla não tem como gerar, então `vanillaClients` as ignora do mesmo jeito que ignora blocos e as nomeia no log; o `standIn` de um lado ou um spawn que nomeia uma delas fica então sem nada para criar.
4. Instale no servidor como de costume. Nada vai para as máquinas dos jogadores; `/rdpl` não existirá para eles, então eles usam as formas `/rdplserver`: `/rdplserver team join`, `/rdplserver round start`, `round reset`, `round vote yes`. `opens.leaderSays` e `reset.voteSays` nomeiam `/rdpl` por padrão, então redija-os com `/rdplserver` em um pacote servido a clientes vanilla.
5. Teste com uma entrada limpa de um cliente vanilla da mesma versão. As falhas são ruidosas: a conexão é recusada na porta, e não quebrada discretamente mais tarde.
6. Lacunas aceitas:
   - Receitas adicionadas pelo servidor podem ser criadas, mas não aparecem no livro de receitas.
   - A introdução do mundo não é exibida. Um cliente vanilla também não é esperado: ele é recebido de imediato, liberado de uma retenção de pré-geração junto com todos os outros e nunca segura o `/rdpl round start`.
   - Tudo que este mod exibe como carta, como resultados, o aviso de líder ou as linhas de `saysCard`, chega como linhas de chat, e as notas no meio da tela, como as do lobby, chegam como títulos.
   - Um grupo de dureza define quanto tempo o servidor leva para quebrar um bloco, mas a animação de rachaduras do cliente corre no ritmo habitual do bloco. Uma substituição de um número que o cliente também lê, como tamanho de pilha, durabilidade, dureza ou luz, ainda mostra ali o valor antigo.
   - A mineração `adventure` de um grupo de dureza não funciona: um cliente vanilla em modo aventura nunca começa a cavar, a menos que o item na mão nomeie o bloco na própria tag `CanDestroy`.
   - Um bloco ou item desativado ainda aparece nas abas criativas de um cliente vanilla, que ele mesmo monta; todo o resto da desativação funciona a partir do servidor.

## Renomeações de registro

*como os pacotes funcionam*

`<namespace>/registry_remap/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

Quando um mod renomeia um de seus blocos ou itens, os mundos salvos antes da renomeação os perdem. Coloque um arquivo aqui para mapear o nome antigo para o novo:

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

O registro é aquele a que a entrada pertence, geralmente `minecraft:items` ou `minecraft:blocks`. As renomeações se encadeiam, então mapear A para B e depois B para C leva A direto para C.

## API de mods

*como os pacotes funcionam*

Um mod pode distribuir conteúdo do RDPL dentro do próprio jar, então não precisa de um pacote separado. Coloque uma pasta chamada `rdploader` na raiz do jar e organize-a exatamente como um pacote:

```
thatmod.jar
  mcmod.info
  rdploader/assets/thatmod/blocks/ruby_ore.json
```

O que um mod distribui é um padrão, não uma substituição. Ele carrega abaixo de todos os pacotes na pasta de pacotes, então tudo que um autor de pacote escreve vence, e um mod só pode fornecer arquivos sob um namespace que declara no próprio `mcmod.info`. Arquivos sob qualquer outro namespace são ignorados com um aviso, assim como uma pasta `rdploader` aninhada dentro de um namespace, para que um mod não possa redefinir discretamente o conteúdo de outro mod ou de um autor de pacote.

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
| `enabled` | `true` ou `false` | `true` | Desativa o conteúdo desse mod, do mesmo jeito que `.disabled` desativa um pacote |
| `priority` | `-1` ou um número | `-1` | `-1` mantém o mod abaixo de todos os pacotes; qualquer outro número o coloca na ordem de [prioridade](#organizando-pacotes) comum, ao lado dos pacotes numerados |

Um pacote de mod nunca entra no nível de substituição dos pacotes de recursos, seja qual for o valor de `overrideResourcePacks`, já que só o autor de um pacote pode pedir isso, com a letra `O`. O log marca os pacotes de mods e lista os pacotes do menor para o maior, então nada carrega sem ser visto.

## Pacotes escritos para 1.20.1, 1.21.1 e 26.x

*como os pacotes funcionam*

Um pacote feito para a linha 1.20.1, 1.21.1 ou 26.x deste mod também carrega aqui. O carregador reconhece um por um `pack.mcmeta` com formato acima de 3, por uma pasta `data/` ao lado de `assets/`, ou por arquivos de idioma `.json` e `textures/block/` sem equivalente no 1.12.2, e o traz de volta do mesmo jeito, qualquer que seja a linha para a qual foi escrito. Um zip é convertido uma vez, dentro dele mesmo: todo arquivo que o 1.12.2 lê de forma diferente é gravado na pasta `versions/1.12.2/` do zip, e os arquivos modernos na raiz ficam como estavam, então o mesmo zip continua carregando no 1.20.1, 1.21.1 e 26.x, como descreve [um zip para todas as versões](#organizando-pacotes). A conversão marca a pasta que grava com um arquivo `port.stamp` contendo a versão do RDPL. Um zip que já tem uma pasta `versions/1.12.2/` é lido através dela; quando seu `port.stamp` nomeia outra versão do RDPL, a conversão grava a pasta de novo, substituindo todos os arquivos nela e nomeando cada um no log, e uma pasta sem `port.stamp`, como uma que o autor do pacote escreveu, nunca é convertida de novo. Dos arquivos da raiz, apenas aqueles que a conversão repassa sem alteração, como sons e texturas fora de `textures/block/` e `textures/item/`, ainda são lidos. O zip é gravado primeiro em um arquivo temporário e só substitui o original quando está completo. Arquivos soltos em `rdploader/assets` e `rdploader/data` não são reescritos; eles são lidos pela mesma conversão toda vez que a pasta é examinada.

Um arquivo de bloco moderno volta com um `meta` para cada variante, na ordem em que as variantes são escritas, e suas tags como nomes do dicionário de minérios:

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

| Arquivo moderno | Arquivo 1.12.2 |
| --- | --- |
| `data/<ns>/<folder>/` para toda pasta de definição | `assets/<ns>/<folder>/` |
| `recipe/`, `loot_table/`, `advancement/`, `function/`, `structure/` (1.21.1) | `recipes/`, `loot_tables/`, `advancements/`, `functions/`, `structures/` |
| `data/*/tags/items/` (1.21.1: `tags/item/`) | `assets/<ns>/oredict/converted_tags.json` |
| `data/minecraft/tags/functions/tick.json` | `assets/<ns>/gamerules/converted_tick.json`, o `gameLoopFunction` do overworld |
| receitas `minecraft:smelting` | `assets/<ns>/furnace/converted_smelting.json` |
| `assets/<ns>/lang/<lang>.json` | `assets/<ns>/lang/<lang>.lang` |
| `textures/block/`, `textures/item/` | `textures/blocks/`, `textures/items/` |
| `models/item/<variant>.json` | `models/item/<file>/<variant>.json` |
| sem blockstate (gerado na linha moderna) | um blockstate do Forge gerado por arquivo de bloco, com os modelos de que o tipo dele precisa |

- Todo id vanilla moderno é trazido de volta por uma tabela de achatamento distribuída no jar, construída a partir dos próprios data fixers do jogo, então sai como o bloco ou item do 1.12.2 com seus metadados: `minecraft:red_wool` vira `minecraft:wool:14`, `minecraft:oak_log` com `axis=x` vira `minecraft:log:4`. Onde uma chave guarda um bloco e um meta separados, como `block` em worldgen e `modelBlock`, o meta vai para `meta` ou `modelMeta`; um `soil` mantém o bloco inteiro. Os ids do próprio pacote são resolvidos pelos arquivos dele: `mypack:worm` vira `mypack:test_ore:1`. Nomes de entidades, biomas, tabelas de saque, sons, partículas e atributos são trazidos de volta do mesmo jeito, e uma dimensão nomeada vira um número: as do próprio pacote usam o `id` delas ou, sem um, um número estável a partir de 1000 que o log nomeia.
- As tags viram nomes do dicionário de minérios pelo inverso do mapeamento de convenção: `forge:gems/testium` e `c:gems/testium` viram `gemTestium`, `minecraft:logs` vira `logWood`. A `tag` de um ingrediente de receita vira um ingrediente `forge:ore_dict` e a receita vira `forge:ore_shaped` ou `forge:ore_shapeless`; a `tag` de um combustível vira `oreDict`. Todo item de receita recebe um `data`, já que o 1.12.2 recusa um item com subtipos sem ele.
- O `block.mypack.worm` de um arquivo de idioma vira `tile.mypack:test_ore.worm.name`, `item.` do mesmo jeito sob `item.`, `itemGroup.mypack.tab` vira `itemGroup.tab` e `fluid.mypack.x` vira as duas chaves de fluido do 1.12.2.
- As tabelas de saque perdem o que o 1.12.2 não consegue ler: a variante de um item vira `set_data`, os provedores de números viram `min` e `max`, os pools ganham um `name`, as entradas `alternatives` e `group` são achatadas, e uma função, condição ou tipo de entrada que o 1.12.2 não tem é deixado de fora com uma linha no log. O `items` de uma conquista vira `item` e `data`, e uma `tag` vira `forge:ore_dict`.
- As funções são reescritas linha por linha para a sintaxe do 1.12.2: `execute as ... at @s run` vira `execute <entity> ~ ~ ~`, `execute if block` vira `detect`, `tag` e `team` viram `scoreboard players tag` e `scoreboard teams`, `data merge` vira `blockdata` e `entitydata`, e os seletores trocam `distance`, `scores`, `limit` e `gamemode` por `r`, `score_X_min`, `c` e `m`. O `SpawnData` de um spawner perde seu wrapper `entity`. Uma linha que a conversão não consegue levar, como `bossbar` ou uma linha de macro, é transformada em comentário e o log nomeia o arquivo, a linha e o motivo, então a função ainda carrega.
- Um `.nbt` de estrutura acima da data version 1343 tem sua paleta, seus spawners, seus stacks de itens e seus ids de entidades trazidos de volta; um bloco sem equivalente no 1.12.2 é deixado como foi escrito e coloca ar.
- O piso de um mundo plano moderno fica no fundo do mundo, e o 1.12.2 monta um mundo plano a partir de y 0, então a conversão sobe as alturas em 64 (pelo `worldMinHeight` do modelo quando ele o nomeia): o `worldSpawn` e o `resetSendsTo` de um modelo plano, as alturas de spawn de uma equipe, o `groundLevel` de uma dimensão plana e todo y absoluto nas funções quando o overworld do pacote é plano.
- Deixados de fora, cada um com uma linha no log: a worldgen orientada a dados do vanilla, tipos de dimensão, tipos de dano, encantamentos e outros registros que o 1.12.2 não tem; tags de blocos, entidades e fluidos; tags de função diferentes de `tick`; stonecutting, smithing e outros tipos de receita que o 1.12.2 não tem; componentes de itens; as chaves de estrutura que só os mundos modernos geram; um nome `behavesAs` diferente de `till`, `path`, `bush` e `animals`; `jobSite`, e uma profissão sem `careers` ganha uma com o nome do próprio arquivo.

O log traz uma linha de resumo por pacote convertido e uma linha para cada arquivo que ela moveu, converteu, deixou de fora ou não conseguiu levar, e toda chave que o 1.12.2 não lê ainda é nomeada pelo parser que a encontra. A conversão é um melhor esforço, não um pacote finalizado: leia essas linhas e termine à mão o que elas nomeiam, começando por qualquer blockstate gerado cujas texturas ela não conseguiu encontrar. Faça essas correções na raiz do pacote ou em um zip separado, nunca na pasta `versions/1.12.2/` que a conversão gravou: outra versão do RDPL grava essa pasta de novo.

---

# Blocos e itens

## Blocos

*blocos e itens*

`<namespace>/blocks/*.json`

O caminho do arquivo é o nome de registro do bloco, então `mypack/blocks/ruby_ore.json` registra `mypack:ruby_ore`. As chaves dentro de `variants` nomeiam os valores de metadados daquele único bloco; elas não são blocos próprios.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as de que precisa. Uma chave marcada para um tipo é lida apenas por esse tipo.

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

*blocos*

| Tipo | O que você obtém |
| --- | --- |
| `basic` | Um bloco simples. Usado quando `type` está ausente |
| `ore` | Solta algo diferente de si mesmo, com fortuna e toque suave |
| `falling` | Cai como areia ou cascalho |
| `slab` | Inferior, superior e duplo, e dois deles se juntam na mão |
| `stairs` | Cantos e inclinações tratados por você |
| `fence` | Conecta-se aos vizinhos e a cercas de outros mods |
| `pane` | Conecta-se como painéis de vidro |
| `wall` | Conecta-se como muros de pedra, com o formato de poste |
| `door` | Dois blocos de altura, abre à mão e responde a redstone. Usa uma única variante, já que o restante dos metadados carrega a dobradiça, a direção e se está aberta |
| `trapdoor` | Uma aba articulada na parte de cima ou de baixo de um bloco, aberta à mão ou por redstone. Uma variante, os metadados carregam a direção, a metade e se está aberta |
| `fence_gate` | Um portão em uma linha de cerca, aberto à mão ou por redstone, e rebaixado onde encontra um muro. Uma variante |
| `banner` | Um estandarte em um poste ou contra uma parede, dezesseis rotações em pé, com o seu próprio desenho. Registra um segundo bloco chamado `<name>_wall` para o pendurado |
| `ladder` | Escalável, colocada contra uma parede |
| `torch` | Colocação em parede e no chão, com uma partícula |
| `bell` | Um sino como o que as vilas têm a partir da 1.14: toca quando usado pelo lado, por redstone ou quando um projétil o atinge, balança em sua estrutura e faz saqueadores próximos brilharem. Uma variante, os metadados carregam a direção e como ele está pendurado |
| `log` | Gira para a face contra a qual você o coloca e é registrado como `logWood` no dicionário de minérios, para que o corte de árvores e o Blast Plaster o tratem como tronco |
| `leaves` | Apodrece, é cortada com tesoura, é tingida e solta uma muda, e é registrada como `treeLeaves` no dicionário de minérios |
| `sapling` | Cresce até virar uma árvore ou uma das suas estruturas |
| `crop` | Cresce por estágios, solta uma semente e um item de colheita |
| `flower` | Uma planta de um bloco em pé sobre o solo |
| `cane` | Cresce para cima em uma coluna, como cana ou cacto |
| `vine` | Escala e pende nos lados dos blocos |
| `portal` | Envia o que entrar nele para outra dimensão |
| `container` | Guarda um inventário que um jogador pode abrir, de qualquer tamanho, e pode se encher sozinho a partir de uma tabela de saque na primeira vez que é aberto. É desenhado como um bloco comum ou como um baú, conforme o pacote pedir |

### Chaves de arquivo

*blocos*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `variants` | sim | objeto de nome de variante para variante | | Uma entrada por valor de metadados. A chave nomeia esse valor no blockstate, no caminho do modelo e na chave de idioma. O nome de registro vem do próprio caminho do arquivo |
| `type` | não | um dos tipos acima | `basic` | Que forma o bloco assume |
| `material` | não | um dos [materiais de blocos](#listas-de-valores) | `rock` | Comportamento de mineração, pistões, fogo e líquidos |
| `soundType` | não | um dos [tipos de som](#listas-de-valores) | `stone`; `wood` para um `log`, `plant` para `leaves` e um `crop`, o do `modelBlock` para `stairs` e um `wall` | Passos, quebra e colocação |
| `mapColor` | não | uma das [cores de mapa](#listas-de-valores) | do material | Como aparece em um mapa |
| `harvestTool` | não | `pickaxe`, `axe`, `shovel` | `pickaxe` | Que ferramenta o colhe |
| `harvestToolLevel` | não | 0 a 3 | `0` | 0 madeira, 1 pedra, 2 ferro, 3 diamante |
| `silkHarvest` | não | boolean | `true` | Se o toque suave devolve o próprio bloco |
| `opensWith` | não | id de item | nenhum | Faz do bloco um cofre: quebrá-lo solta o próprio bloco, e clicar com o botão direito com o item nomeado consome um, toca o som de quebra do bloco, paga a lista `drops` da variante e remove o bloco. Qualquer outro clique mostra a linha da barra de ação `tile.<pack>:<block>.<variant>.locked` dos arquivos de idioma |
| `openSound` | não | nome de som | o som de quebra | O que um cofre toca ao ser aberto no lugar do som de quebra |
| `expDrop` | não | objeto com `min` e `max` | nenhum | Experiência solta quando quebrado sem toque suave |
| `creativeTab` | não | nome de aba | nenhuma | A aba em que aparece |
| `renderLayer` | não | `solid`, `cutout`, `cutout_mipped`, `translucent` | conforme o tipo | Como é desenhado |
| `opaque` | não | boolean | `true` | Se bloqueia por completo a visão e a luz |
| `fullCube` | não | boolean | igual a `opaque` | Se preenche todo o seu espaço |
| `lightOpacity` | não | 0 a 255 | `255` quando opaco, senão `0` | Quanta luz absorve |
| `slipperiness` | não | float | `0.6` | O gelo é `0.98` |
| `flammability` | não | int | `0` | Com que facilidade o fogo o consome |
| `fireSpread` | não | int | `0` | Com que facilidade o fogo se espalha a partir dele |
| `explosionResistanceDivisor` | não | float | `1.0` | Divide a `resistance` de cada variante contra explosões |
| `modelBlock` | não | nome de bloco | `minecraft:stone` | Bloco cujo modelo é emprestado quando o seu não tem um |
| `modelMeta` | não | int | `0` | Qual variante desse modelo |
| `itemModel` | não | `state`, `item` | `state` | `state` segue o blockstate, `item` procura o próprio arquivo |
| `tint` | não | `biome`, `none` ou uma cor hexadecimal | nenhum | Precisa de um `tintindex` no modelo para aparecer |
| `plantTypes` | não | lista de [tipos de plantas](#listas-de-valores) | nenhum | O que pode ser plantado nele |
| `behavesAs` | não | lista de `till`, `path`, `bush`, `animals` | nenhum | Comportamentos vanilla a assumir |
| `bounds` | não | lista de seis números, 0 a 1 | bloco inteiro | A caixa de colisão, como `[x1, y1, z1, x2, y2, z2]` |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | O arquivo é ignorado a menos que todos estejam presentes |
| `particle` | apenas torch | `none`, `flame`, `colored` | `flame` | A partícula acima de uma tocha |
| `particleColor` | apenas torch | cor hexadecimal | `FFFFFF` | Usada quando `particle` é `colored` |
| `smoke` | apenas torch | boolean | `true` | Se solta fumaça |
| `leafSapling` | apenas leaves | nome de bloco | nenhum | A muda que elas soltam |
| `leafSaplingChance` | apenas leaves | int | `5` | Uma em N folhas solta uma |
| `seed` | apenas crop | nome de item | `minecraft:wheat_seeds` | O item que o planta e o que uma plantação imatura solta |
| `produce` | apenas crop | nome de item | `minecraft:wheat` | O que a colheita rende |
| `maxAge` | apenas crop | int | `7` | Quantos estágios de crescimento |
| `growth` | apenas plantas | objeto | nenhum | Veja [Crescimento](#crescimento) |
| `sapling` | apenas sapling | objeto | nenhum | Veja [Mudas](#mudas) |
| `portal` | apenas portal | objeto | nenhum | Veja [Portais e passagens](#portais-e-passagens) |
| `container` | apenas container | objeto | nenhum | Veja [Contêineres](#contêineres) |
| `bell` | apenas bell | objeto | nenhum | Veja [Sinos](#sinos) |

### Chaves de variantes

*blocos*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `meta` | sim | 0 a 15 | | O valor de metadados que esta variante reivindica |
| `hardness` | não | float | `1.0` | Quanto tempo leva para quebrar. A obsidiana é `50`, `-1` é inquebrável |
| `resistance` | não | float | `5.0` | Resistência a explosões |
| `light` | não | 0 a 15 | `0` | Luz emitida |
| `harvestLevel` | não | 0 a 3 | `0` | Substitui o nível da ferramenta para esta variante |
| `rarity` | não | `common`, `uncommon`, `rare`, `epic` | `common` | Cor do nome na dica |
| `maxSize` | não | 1 a 64 | `64` | Tamanho da pilha |
| `oreDict` | não | lista de nomes do dicionário de minérios | nenhum | Nomes do dicionário de minérios sob os quais esta variante é registrada |
| `drops` | não | lista de drops | solta a si mesmo | O que quebrá-lo rende |

**Os metadados são permanentes.** O número que uma variante reivindica é gravado em todo mundo salvo que a contenha. Renumerar ou reordenar variantes depois transforma blocos colocados em outra coisa. Adicione variantes novas no final e nunca reutilize um número.

Um bloco `basic` pode ter dezesseis variantes; um `slab`, oito; `log` e `leaves`, quatro, porque os sinalizadores de eixo e de decaimento precisam de bits próprios; os tipos de estado único têm uma.

### Drops

*blocos*

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `block` | um dos dois | nome de bloco ou item | | O que é solto |
| `entity` | um dos dois | nome de entidade | | Uma entidade liberada quando o bloco quebra, em vez de um item |
| `meta` | não | int | `0` | Qual variante dele |
| `amount` | não | int ou intervalo | `1` | Quantos |
| `chance` | não | 0 a 100 | `100`, ou `0` quando `guaranteed` está desligado | Com que frequência o drop acontece |
| `weight` | não | int | `0` | Acima de zero, a entrada entra em um pool que rende exatamente um drop. Veja abaixo |
| `bonusChance` | não | lista de ints | nenhum | Drops extras por nível de fortuna, uma entrada por nível |
| `guaranteed` | não | boolean | `true` | Atalho legado para `chance`. Ligado é `100`, desligado é `0` |

Toda entrada sem `weight` é decidida por conta própria, então um bloco com três delas pode soltar as três ou nenhuma. Dê um `weight` às entradas e elas deixam de ser independentes: formam um único pool, do qual exatamente uma é escolhida a cada vez que o bloco quebra, com chances proporcionais aos pesos. Acima, o diamante e a esmeralda compartilham um pool na proporção de um para quatro, então um dos dois sempre sai e é a esmeralda quatro vezes em cinco, enquanto o rubi e o carvão são decididos separadamente e o peixe-prateado é, de novo, uma coisa à parte. Itens e entidades formam pools separados, então um item ponderado e uma entidade ponderada não competem.

Uma entrada que nomeia uma `entity` solta uma onde o bloco estava, virada para um lado aleatório, e um mob recebe o tratamento de spawn habitual para a dificuldade local, então chega com o equipamento e os efeitos que teria tido. `amount` decide quantas, `chance` com que frequência, `weight` a coloca no pool de entidades. Acontece quando o bloco quebra, não importa como quebrou, então uma explosão ou um pistão as solta do mesmo jeito que uma picareta. `meta`, `bonusChance` e fortuna não significam nada para uma entidade e são ignorados.

Um drop que nomeia tanto um `block` quanto uma `entity` usa a entidade e avisa no log.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `stages` | não | int | `16` | Estágios de crescimento antes de concluir |
| `growth` | não | int | | Chance de uma em N por tick aleatório de avançar |
| `spread` | não | int | `0` | Até onde se espalha para os blocos vizinhos |
| `maxHeight` | não | int | `3` | Apenas cane. Que altura a coluna alcança |
| `drop` | não | nome de item | nenhum | O que solta quando quebrado |
| `dropCount` | não | int | `1` | Quantos |
| `needsSky` | não | boolean | `false` | Só cresce onde o céu é visível |
| `needsWater` | não | boolean | `false` | Só cresce perto de água |
| `waterRange` | não | int | `1` | A que distância essa água pode estar |
| `damage` | não | boolean | `false` | Machuca o que o tocar |
| `damageAmount` | não | float, meios corações | `1.0` | O quanto machuca |
| `breaksNeighbors` | não | boolean | `false` | Quebra blocos colocados ao lado, como o cacto |

### Mudas

*blocos*

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as de que precisa.

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

Uma `structure` substitui a árvore gerada por um dos seus modelos, que é o jeito de construir algo que um gerador não consegue, e nada mais no bloco precisa ser escrito. Nomeie vários em `structures` e a muda escolhe um a cada vez que cresce, para que um bosque não seja a mesma árvore repetida:

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `soil` | não | lista de nomes de blocos | nenhum | Em que ela vai crescer |
| `stages` | não | int | `2` | Estágios de crescimento antes de virar uma árvore |
| `chance` | não | int | `7` | Uma em N por tick aleatório |
| `light` | não | 0 a 15 | `9` | Nível de luz necessário |
| `log` | não | nome de bloco | `minecraft:log` | Bloco do tronco |
| `leaves` | não | nome de bloco | `minecraft:leaves` | Bloco das folhas |
| `height` | não | int | `4` | Altura do tronco |
| `vines` | não | boolean | `false` | Pendurar cipós nas folhas |
| `structure` | não | `namespace:name` | nenhum | Crescer para este modelo em vez de uma árvore gerada |
| `structures` | não | lista | nenhum | Vários modelos para os quais crescer, um escolhido a cada crescimento. Cada entrada é `{ "structure": "namespace:name", "weight": 3 }`, ou um nome simples para chances iguais. Substitui `structure` |

## Contêineres

*blocos e itens*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `rows` | inteiro | `3` | Quantas fileiras de espaços, de 1 a 9 |
| `columns` | inteiro | `9` | Quantos espaços por fileira, de 1 a 12 |
| `lootTable` | texto | vazio | Uma tabela de saque sorteada dentro do bloco na primeira vez que um jogador o abre, exatamente como um baú de masmorra é preenchido. Vazio deixa o bloco começar vazio |
| `chestModel` | booleano ou texto | `false` | Desenha como um baú com tampa que abre, em vez de como um bloco comum do seu próprio modelo. `true` usa a arte do baú vanilla; um nome de textura como `mypack:blocks/strongbox_chest` usa a sua própria folha de baú, tanto para o bloco colocado quanto para o item. Dê ao blockstate o modelo `resourcedatapackloader:pack_chest`, com o mesmo nome em `texture`, para que o item na sua mão também tenha formato de baú. Um bloco com modelo de baú também assume `opaque` como `false` por padrão, como um baú vanilla, de modo que a luz não é cortada no bloco e o baú não é desenhado escuro. |
| `guiTexture` | texto | vazio | Sua própria imagem de fundo para a tela. Vazio desenha uma a partir da tela de baú vanilla, no tamanho que as fileiras e colunas exigirem |
| `guiWidth` | inteiro | nenhum | A largura dessa imagem, obrigatória com `guiTexture` |
| `guiHeight` | inteiro | nenhum | A altura dessa imagem, obrigatória com `guiTexture` |
| `bauble` | texto | vazio | Somente para itens: o espaço do Baubles em que ele pode ser usado — `amulet`, `ring`, `belt`, `trinket`, `head`, `body` ou `charm`. Uma mochila geralmente usa `body` ou `charm`. Ignorado, sem afetar o restante do item, quando o Baubles não está instalado. Cada nome corresponde a um quadrado da aba do Baubles, então um item que pede `body` cabe nesse quadrado e em nenhum outro; `ring` são os dois quadrados de anel e `trinket` cabe em todos os quadrados. O Baubles é uma dependência opcional: este mod carrega depois dele quando ele está presente e funciona sem ele quando não está, então um pacote que cita um espaço é seguro em um servidor que nunca ouviu falar do Baubles. |

**Nove fileiras por doze é o limite**, que é o maior que o Iron Chest oferece e o máximo que uma tela comporta. Um pacote que pedir mais é limitado a isso, com uma linha de erro avisando. Um aviso sobre a maior delas: uma tela de nove fileiras tem 276 pixels, e um monitor 1080 com escala de GUI `auto` dá 270, então o topo e a base são cortados em três pixels cada — a escala 3 mostra tudo. O Iron Chest comporta nove fileiras porque traz sua própria arte mais compacta; um pacote que queira o mesmo pode definir `guiTexture` e desenhar a sua.

**A tela é desenhada, não incluída.** Um contêiner de nove colunas ou menos e seis fileiras ou menos usa a tela de baú vanilla como ela é, então fica exatamente como um baú desse tamanho. Qualquer coisa maior é montada a partir da mesma imagem na hora de desenhar -- a borda superior, uma fileira de espaços repetida até caber e a parte inferior com o inventário do próprio jogador -- de modo que um pacote pode pedir tamanhos que nenhuma tela vanilla cobre sem incluir uma imagem própria. `guiTexture` substitui tudo isso quando um pacote quer seu próprio visual, e então `guiWidth` e `guiHeight` precisam informar o tamanho, ou a tela desenhada é usada e uma linha de erro avisa.

**O que o bloco faz.** Ele mantém seu conteúdo ao salvar e recarregar, o solta quando quebrado, responde a um comparador conforme o quanto está cheio e pode ser renomeado em uma bigorna como um baú. `chestModel` também lhe dá o som de abertura do baú e a animação da tampa; desativado, o bloco é desenhado a partir do modelo que o seu próprio `modelBlock` indicar, então um caixote, um barril ou um armário funcionam.

**Colorindo um baú.** A folha do baú é uma textura comum, então um mapa de pixels pode recolorir a vanilla sem desenhar um único pixel: faça `extends` dela e dê um `tint`, depois cite esse mapa em `chestModel` e como `texture` do modelo.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

O bloco colocado e o item na sua mão leem esse mesmo nome, então combinam. Citá-lo em apenas um dos dois deixa o outro no marrom vanilla.

**Um item contêiner pode ser usado no corpo.** Dê a ele `bauble` e, onde o Baubles está instalado, ele vai para esse espaço e uma tecla o abre sem precisar tirá-lo — `V` por padrão, reatribuível em Resource Data Pack Loader nos controles. `B` é o que o Baubles vincula à sua própria aba, então as duas não compartilham a tecla. Pressioná-la de novo, com um contêiner usado já aberto, passa para o próximo que você estiver usando e volta ao começo, de modo que vários usados ao mesmo tempo ficam todos acessíveis. A tecla só aparece quando o Baubles está presente, e todo o resto do item, o clique direito e seu inventário, funciona com ou sem ele. O Baubles não tem espaço próprio para mochila; `body` e `charm` são os dois que uma mochila costuma usar.

**A tabela de saque é preenchida na primeira abertura**, não quando o bloco é colocado, o que a torna útil em uma estrutura: quem abrir primeiro recebe o sorteio. A mesma tabela pode ser usada por `lootTable` em uma forma de imprint ou em um terreno de vila, então um pacote pode colocar esses blocos pelo worldgen e abastecê-los do mesmo jeito.

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
    "sound": "minecraft:block.note.bell",
    "resonateSound": "minecraft:block.note.chime"
  },
  "variants": { "village_bell": { "meta": 0, "hardness": 5.0, "resistance": 30 } }
}
```

E o seu blockstate, `assets/mypack/blockstates/village_bell.json`:

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `swing` | booleano | `true` | Desenha o corpo a partir de um modelo próprio e o balança quando o sino toca. `false` desenha o sino inteiro como um único bloco parado, sem nada animado |
| `sound` | nome de som | `minecraft:block.note.bell` | Tocado quando o sino toca. Vazio toca em silêncio |
| `resonateSound` | nome de som | `minecraft:block.note.chime` | Tocado quando o sino ressoa porque há invasores por perto. Vazio ressoa em silêncio |

**Ele pende como o sino do próprio jogo a partir da 1.14.** Colocado sobre um bloco, fica de pé no chão, virado para o lado em que você olha; sob um bloco, pende do teto; contra uma parede, pende dessa parede, e entre duas paredes quando o lado oposto também é sólido. Ele cai quando o que o sustenta desaparece, e um sino entre duas paredes vira um sino de parede única quando uma delas some. Sua caixa de colisão segue a do vanilla para cada um dos quatro casos, então `bounds` não é lido.

**O que o faz tocar.** Usar o lado do corpo, abaixo da viga: em um sino de chão, nas duas faces que a viga atravessa; em um sino de parede, nas duas faces ao lado da parede; em um sino de teto, em qualquer lado. O topo, a base e qualquer coisa acima do corpo não fazem nada. Um sinal de redstone o faz tocar uma vez ao ser ligado, e uma flecha, uma bola de neve ou qualquer outro projétil o faz tocar ao atingir um lado que uma mão alcançaria. O corpo balança para longe do lado em que foi atingido por dois segundos e meio; a redstone o balança no sentido para o qual o sino está virado.

**O que um toque faz.** Aldeões a até 32 blocos o ouvem e entram em casa por quinze segundos, indo até a porta mais próxima que a vila deles conhece. Quando um invasor está a até 32 blocos, o sino ressoa um quarto de segundo após o toque, e dois segundos depois todo invasor a até 48 blocos brilha por três segundos, com partículas coloridas ao lado do sino, no lado em que cada um está. Um invasor é qualquer coisa enviada por uma [invasão](#invasões), além dos illagers e bruxas do próprio jogo. Um sino deste tipo é um sino de vila para toda invasão: ele toca a cada onda que chega sem precisar ser citado no `bell` da invasão.

**Os modelos.** Um sino não tem a propriedade `blocks`, então seu blockstate é indexado por `facing` e `attachment`. Com `swing` ligado, esses modelos desenham apenas a estrutura, e a parte que balança é mais uma entrada chamada `body`, modelada no espaço do bloco onde ela repousa; ela se inclina em torno do ponto meio bloco para dentro e três quartos de bloco para cima, como a do vanilla. A entrada `inventory` é o item, estrutura e corpo juntos. Com `swing` desligado não há entrada `body`, e os quatro modelos de estrutura desenham o sino também.

**O balanço é desenhado pelo cliente.** Um toque chega aos jogadores como um evento de bloco, então um servidor dedicado balança o sino para todos que têm este mod, e um jogador sem ele apenas ouve o sino. Sons, ressonância e brilho acontecem todos no servidor.

## Modelos, blockstates e texturas

*blocos e itens*

Definir um bloco ou item o registra. Sua *aparência* continua sendo um conjunto comum de arquivos de assets, nas mesmas pastas e no mesmo formato que o Minecraft já usa, sob o seu próprio namespace.

```
assets/mypack/blockstates/ruby_ore.json
assets/mypack/models/block/ruby_ore.json
assets/mypack/models/item/ruby/ruby.json
assets/mypack/textures/blocks/ruby_ore.png
assets/mypack/lang/en_us.lang
```

### Nomeando as variantes

*modelos, blockstates e texturas*

Todo bloco com mais de uma variante ganha uma propriedade chamada `blocks`, e seus valores são os nomes das variantes da definição. Então um arquivo de bloco que registra `ruby_ore` e `deep_ruby_ore` precisa de um blockstate com essas duas variantes:

```json
{
  "variants": {
    "blocks=ruby_ore": { "model": "mypack:ruby_ore" },
    "blocks=deep_ruby_ore": { "model": "mypack:deep_ruby_ore" }
  }
}
```

Um bloco com uma única variante também mantém a propriedade `blocks`, então sua chave continua sendo `blocks=<nome>`, mas apenas nos tipos que têm essa propriedade. Doze tipos gastam todos os seus metadados com a forma, têm uma variante e não carregam a propriedade `blocks`, então são indexados somente pelas próprias propriedades. [Blockstates por tipo](#blockstates-por-tipo) diz qual é qual.

Quando o bloco tem propriedades próprias, elas são unidas por vírgulas na ordem em que o estado as lista: `blocks=ruby_log,axis=y`, `blocks=ruby_slab,half=bottom`, `blocks=ruby_wall,up=true,north=true`. Um bloco de escada não tem a propriedade `blocks`, então é indexado por `facing=east,half=bottom,shape=straight` e nada mais. Duas propriedades ficam de fora de propósito: a propriedade de variante própria de um muro e o `check_decay` e `decayable` de um bloco de folhas, então as folhas precisam apenas de `blocks=ruby_leaves`. Um estandarte não tem propriedade de variante alguma e é indexado por `rotation=0` até `15` quando em pé, ou por `facing=north` em uma parede, o que [Estandartes](#estandartes) explica.

### Blockstates por tipo

*modelos, blockstates e texturas*

Duas coisas decidem o que um arquivo de blockstate precisa conter: se o tipo carrega a propriedade `blocks` e quais propriedades próprias ele tem.

| Tipo | Registra | Propriedades do blockstate | Variantes |
| --- | --- | --- | --- |
| `basic`, `ore`, `falling` | um bloco | `blocks` | 16 |
| `flower` | um bloco | `blocks` | 16 |
| `portal` | um bloco | `blocks` | 16 |
| `fence`, `pane` | um bloco | `blocks`, `north`, `east`, `south`, `west` | 16 |
| `wall` | um bloco | `blocks`, `up`, `north`, `east`, `south`, `west` | 16 |
| `slab` | dois, `<nome>` e `<nome>_double` | a meia laje `blocks` e `half`; a dupla apenas `blocks` | 8 |
| `log` | um bloco | `blocks`, `axis`, que é `x`, `y`, `z` ou `none` | 4 |
| `leaves` | um bloco | `blocks` | 4 |
| `stairs` | um bloco | `facing`, `half`, `shape` | 1 |
| `door` | um bloco | `facing`, `half`, `hinge`, `open` | 1 |
| `trapdoor` | um bloco | `facing`, `half`, `open` | 1 |
| `fence_gate` | um bloco | `facing`, `in_wall`, `open` | 1 |
| `banner` | dois, `<nome>` e `<nome>_wall` | `rotation` em pé, de `0` a `15`; o de parede `facing` | 1 |
| `ladder`, `torch` | um bloco | `facing`, e a tocha acrescenta `up` às quatro paredes | 1 |
| `bell` | um bloco | `facing` e `attachment`, que é `floor`, `ceiling`, `single_wall` ou `double_wall`, mais uma entrada `body` para a parte que balança | 1 |
| `crop` | um bloco | `age`, sempre de `0` a `7`, independentemente do `maxAge` | 1 |
| `cane` | um bloco | `age`, de `0` a `15` | 1 |
| `sapling` | um bloco | `stage`, de `0` até um a menos que `stages` | 1 |
| `vine` | um bloco | `up`, `north`, `east`, `south`, `west`, e somente multipart | 1 |

Quatro propriedades são descartadas para você, então escreva as chaves sem elas: `powered` em portas e portões, `variant` em muros, e `check_decay` e `decayable` em folhas.

Uma plantação mantém os oito valores de `age` do vanilla, qualquer que seja o `maxAge`, já que `maxAge` apenas decide até onde ela cresce; por isso seu blockstate sempre escreve de `age=0` a `age=7`.

Dois tipos registram um segundo bloco. O `<nome>_double` de uma laje precisa de um blockstate próprio, indexado por `blocks` sem `half`, e nunca ganha um item próprio. O `<nome>_wall` de um estandarte está explicado em [Estandartes](#estandartes).

**Vine é o único tipo que não pode usar o formato Forge**, já que `forge_marker` não suporta multipart; por isso seu blockstate é uma lista `multipart` vanilla simples, com a textura embutida no modelo.

**O formato Forge é mais curto, e é o que o pacote de exemplo usa.** Um blockstate vanilla escreve cada combinação como uma chave própria, o que para escadas dá quarenta. Com `"forge_marker": 1` o arquivo lista cada propriedade uma vez e o jogo as combina, então os mesmos quarenta estados viram onze entradas:

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

`defaults` é mesclado em cada entrada, um nome de modelo simples como `stairs` significa `minecraft:block/stairs`, e `inventory` é o modelo que o item na sua mão usa. Os três modelos pai de escadas, `stairs`, `inner_stairs` e `outer_stairs`, aceitam as texturas `bottom`, `top` e `side`.

**Os tipos conectáveis acrescentam um submodelo por lado.** Uma cerca, vidraça ou muro tem um booleano por direção, e um `true` cola outro modelo ao poste em vez de substituí-lo:

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

Os modelos pai são `fence_post` e `fence_side` com uma `texture`; `wall_post` e `wall_side` com uma `wall`, mais `block` para o caso sem poste; e `pane_post`, `pane_side`, `pane_side_alt`, `pane_noside` e `pane_noside_alt` com uma `pane` e uma `edge`. Todos eles querem `"uvlock": true`.

Os demais aceitam um único modelo pai. `cube_all` aceita um `all` e é o que um bloco `basic`, `ore`, `falling` ou `leaves` quer. `cube_column` aceita um `end` e um `side`, o que serve a um `log`, girado por `axis`. `cross` aceita um `cross` e é o que um `flower`, `cane` ou `sapling` quer; uma `crop` usa seus próprios modelos por estágio. Uma laje precisa de dois modelos próprios, uma metade inferior e uma superior, já que é desenhada como uma forma e não como um cubo.

**O pacote de exemplo é a referência prática.** O [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) traz uma definição, um blockstate e modelos para cada tipo da tabela acima, nos formatos vanilla e Forge, então uma forma que não é óbvia é mais rápida de copiar do que de descobrir.

### Modelos de itens

*modelos, blockstates e texturas*

Por padrão o item usa o que o blockstate der para aquela variante, então nada mais é necessário. Definir `"itemModel": "item"` no bloco faz com que ele procure um arquivo próprio, em `models/item/<bloco>/<variante>.json`.

Os itens sempre funcionam desse segundo jeito, porque todo item de pacote tem subtipos:

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

O caminho é o nome de registro do item, seguido do nome da variante.

Fluidos não precisam de modelo algum; um é gerado a partir das texturas `still` e `flow`.

### Portas, alçapões e portões de cerca

*modelos, blockstates e texturas*

Os três gastam todos os seus metadados com a forma que assumem, então cada um é uma única variante, e cada um tem algumas coisas que vale saber antes de escrever os arquivos.

**Eles não carregam a propriedade `blocks`**, então seus blockstates são indexados apenas pela forma: `facing=east,half=lower,hinge=left,open=false` para uma porta, `facing=north,half=bottom,open=false` para um alçapão, `facing=south,in_wall=false,open=false` para um portão. São 32 chaves, 16 e 16.

**`powered` fica de fora de portas e portões.** Ambos realmente a têm, e ambos dobrariam seus blockstates por um eixo que não muda nada visível. Ela é descartada para você, exatamente como o jogo a descarta de suas próprias portas e portões, então escreva as chaves sem ela. Alçapões nunca a tiveram.

**Aponte os modelos para os modelos pai que aceitam texturas**, não para os vanilla já prontos:

| Tipo | Modelos pai |
| --- | --- |
| `door` | `block/door_bottom`, `block/door_bottom_rh`, `block/door_top`, `block/door_top_rh` |
| `trapdoor` | `block/trapdoor_bottom`, `block/trapdoor_top`, `block/trapdoor_open` |
| `fence_gate` | `block/fence_gate_closed`, `block/fence_gate_open`, `block/wall_gate_closed`, `block/wall_gate_open` |

Uma porta aceita duas texturas, `bottom` e `top`; os outros dois aceitam uma, `texture`. Os dois modelos de topo da porta recorrem a `bottom` para revestir a borda superior, então declare as duas nos quatro arquivos mesmo que os de topo pareçam precisar de só uma. As variantes de portão querem `"uvlock": true`, como as do próprio jogo.

**Suas texturas usam cada pixel, e é esta que pega as pessoas.** As faces largas de uma porta são mapeadas como `[0, 0, 16, 16]`, a imagem inteira, e suas bordas estreitas, o topo e a base saem do mesmo quadrado: colunas 0 a 3 para os lados, 13 a 16 para o topo e a base. Um alçapão é igual, com as faces planas ocupando a imagem inteira e as quatro bordas tiradas das linhas 13 a 16.

Portanto, não deixe margem vazia. Se você limpar algumas colunas em uma borda, achando que a forma é mais estreita que o arquivo, abre uma fenda de lado a lado no meio da face e perde por completo o topo e a base. Desenhe a moldura ou os montantes nesses pixels de borda, e eles aparecem como acabamento nas bordas do próprio bloco.

**Seus itens variam conforme o tipo.** O de uma porta é um sprite plano, `item/generated` sobre o seu próprio `textures/items/<nome>.png`, já que uma porta na mão é desenhada como uma imagem e não como uma forma. Os de um alçapão e de um portão herdam um modelo de bloco, a metade inferior e o portão fechado, que é o que o jogo faz com os seus.

Os três aceitam o `material` que você der. Um portão é construído sobre um bloco que se fixa em madeira, então este mod restaura o material para o seu ao registrá-lo, e um portão de pedra é minerado com uma picareta como a pedra que diz ser.

### Estandartes

*modelos, blockstates e texturas*

Um estandarte é o único tipo em que a forma do bloco e a forma do modelo se separam, então vale detalhá-lo por inteiro.

**Ele registra dois blocos.** Uma definição dá o estandarte em pé com o seu nome e um segundo bloco chamado `<nome>_wall` para o pendurado. Ambos precisam de um blockstate; só o de pé ganha um item, e esse item decide qual dos dois coloca: em pé quando você clica no topo de um bloco e de parede quando clica em um lado. Você nunca coloca o bloco de parede diretamente e ele não precisa de um item próprio.

**O de pé precisa de um blockstate Forge.** Sua propriedade é `rotation`, indo de `0` a `15`, porque um estandarte gira em dezesseis avos e não em quartos. Um blockstate vanilla não consegue expressar isso: seu `y` passa por `ModelRotation`, que só aceita 0, 90, 180 e 270 e lança erro com qualquer outro valor. O formato do Forge aceita qualquer ângulo, então as dezesseis entradas são escritas como uma transformação:

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

…e assim por diante até `15`, cada uma `-22.5` graus além da anterior. O sinal combina com os estandartes do próprio jogo, que giram por menos a rotação. Construa o modelo voltado para o sul, já que é para onde fica apontando um estandarte colocado por um jogador olhando para o sul. O bloco de parede é um blockstate vanilla comum, com as quatro entradas `facing` de sempre em 0, 90, 180 e 270, já que não há nada fracionário nele.

**O modelo tem quase dois blocos de altura.** Um estandarte ocupa um bloco para colocação e colisão, mas é desenhado muito além dele, e um modelo que termina no topo do próprio bloco parece atrofiado. As proporções do vanilla, em dezesseis avos de bloco, merecem ser copiadas exatamente:

| Parte | De | Até |
| --- | --- | --- |
| Poste | `0` | `28` |
| Travessa | `28` | `29.33` |
| Tecido | `2.67` | `29.33` |
| Largura do tecido | `1.33` | `14.67` |
| Tecido de parede | `-13` | `13.67` |

Assim, um estandarte em pé chega a `29.33`, quase dois blocos, e um estandarte de parede pende treze dezesseis avos *abaixo* do bloco que o sustenta. Os elementos do modelo podem ir de `-16` a `32`, então ambos cabem. A forma de parede não tem poste nem travessa, apenas tecido.

**O tecido tem o dobro da altura da largura, e a sua textura também precisa ter.** Essa face mede `13.33` por `26.67`. Mapeie uma textura quadrada nela e o desenho é espremido à metade da altura. As texturas de bloco não podem ter o dobro da altura da largura, já que qualquer coisa não quadrada é lida como animação, então a saída é uma folha quadrada maior com o tecido em parte dela: um arquivo de 32×32 com o tecido numa região de 16×32, endereçada como `"uv": [0, 0, 8, 16]`, com as faixas do poste e da travessa no espaço ao lado. As coordenadas UV sempre vão de 0 a 16, qualquer que seja a resolução do arquivo, então os mesmos números funcionam em qualquer tamanho.

**Seu item quer um modelo próprio.** Um item que herda um modelo tão alto vai estourar o espaço do slot na escala de bloco habitual, então dê a `models/item/<nome>.json` um bloco `display` próprio, com a escala reduzida e o conjunto todo deslocado de volta para dentro do quadro.

**Não há cores nem padrões nele.** Um estandarte de pacote não tem entidade de bloco, então nada guarda a lista de camadas que os estandartes vanilla mantêm na sua. O desenho é a textura, do mesmo jeito que a aparência de uma porta é a sua textura, e uma definição é um estandarte. Tingi-lo e empilhar padrões nele não é algo que um pacote alcance.

**Ele usa o `material` que você der.** O bloco em que é baseado se fixa em madeira, então este mod restaura o material para o seu ao registrá-lo, e um estandarte de pedra é minerado com uma picareta como a pedra que diz ser.

### Texturas escritas como mapas de pixels

*modelos, blockstates e texturas*

Uma textura pode ser um arquivo JSON em vez de um PNG. Coloque-o onde o PNG iria, com `.json` no fim do nome inteiro, de modo que `textures/blocks/panel.png.json` responde a todo pedido de `textures/blocks/panel.png`. Nada mais muda: os modelos apontam para `mypack:blocks/panel` como sempre, e o atlas, os mipmaps e um `.mcmeta` de animação funcionam, porque o que o jogo recebe continua sendo um PNG.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `size` | sim, ou herdada | `larguraxaltura` | | Quantos pixels de largura e de altura |
| `rows` | sim, ou herdada | lista de textos | | Uma string por fileira de pixels, um caractere por pixel, de cima para baixo |
| `palette` | sim, ou herdada | objeto | | Um caractere para uma cor, `#RRGGBB` ou `#AARRGGBB` |
| `extends` | não | outro mapa de pixels | | O mapa a partir do qual este começa |
| `tint` | não | objeto com `from` e `to` | | Recolore tudo o que foi herdado ao longo de uma rampa entre duas cores |
| `notes` | não | objeto | | Um caractere para uma linha dizendo para que serve, herdada e nunca desenhada |

**Não há nome para declarar.** O caminho do próprio arquivo é o seu nome, exatamente como o de um PNG, então um mapa em `assets/mypack/textures/blocks/panel.png.json` é `mypack:blocks/panel` em um modelo, e um mapa em `assets/mypack/textures/items/gem.png.json` é `mypack:items/gem` em um modelo de item. Nada aponta para um mapa de pixels de forma especial; um bloco ou item cita sua textura como sempre fez e nunca fica sabendo qual dos dois recebeu. Isso também significa que as pastas de blocos e de itens continuam separadas, como para os PNGs: `textures/blocks/gem.png.json` e `textures/items/gem.png.json` são duas texturas diferentes e ficam em cache como dois arquivos diferentes.

**Qualquer tamanho que você quiser**, até 4096 por lado, e os dois lados não precisam coincidir. `16x16` é uma face de bloco comum, `16x32` é o tipo de faixa alta que a metade de uma porta ou uma animação pede. O tamanho é verificado e não adivinhado: dê uma fileira por linha de pixels e um caractere por pixel na largura, ou o mapa é recusado e o log informa a fileira e o que encontrou. Um caractere sem cor na paleta fica transparente, então `.` ou um espaço é um buraco.

**Os modelos são o objetivo.** `extends` cita outro mapa de pixels, como `namespace:caminho` ou um caminho simples no mesmo pacote, e o arquivo que o estende herda seu `size`, suas `rows` e sua `palette`. O que ele mesmo citar prevalece, e ele não precisa citar tudo, então uma variante inteira pode ser um punhado de cores:

```json
{
  "extends": "mypack:textures/blocks/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

Essa é uma segunda textura completa: a mesma forma em purpur, e se a forma for redesenhada no modelo, todas as variantes a acompanham. Uma variante pode, em vez disso, dar suas próprias `rows` e manter a paleta do modelo, que é o caminho inverso: as mesmas cores em outro padrão. A herança vai até oito níveis, um ciclo é detectado e reportado, e um mapa que cita um modelo que ninguém fornece é reportado em vez de desenhado em branco.

**Qual de duas texturas é o modelo** é decidido por qual delas tem mais distinções, não por qual foi desenhada primeiro. Uma variante dá uma cor a cada caractere, então todo pixel que o modelo chama pelo mesmo caractere sai da mesma cor na variante. Um minério desenhado sobre pedra, portanto, não pode herdar as `rows` da pedra: a pedra chama as posições das manchas de pedra comum, e nada que uma variante possa escrever divide um caractere em dois. Inverta e funciona. Deixe o minério ser o modelo, de modo que os tons da pedra e os do minério tenham cada um caracteres próprios, e um segundo minério custa quatro cores:

```json
{
  "extends": "mypack:textures/blocks/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

Uma variante que realmente queira um padrão diferente dá suas próprias `rows`, como acima, e então herda apenas a paleta. Isso vale a pena quando as cores são o essencial e a forma é incidental; quando a forma é o essencial, ponha a forma no modelo e deixe as variantes citarem cores.

**Um modelo não precisa ser uma textura.** Um mapa só é entregue ao jogo quando seu caminho termina em `.png`, então um modelo em `textures/blocks/ore_template.json` é invisível ao jogo e existe unicamente para ser estendido, enquanto um em `textures/blocks/ore_template.png.json` também responderia a pedidos de `ore_template.png`. Dê a uma forma compartilhada um nome sem o `.png` e nada poderá pedi-la por acidente.

**Um modelo pode ser uma imagem real em vez de um mapa.** Aponte `extends` para um PNG que qualquer pacote ou o próprio jogo forneça e a paleta muda de significado: as chaves passam a ser as cores que já estão nessa imagem, e os valores, as cores a pôr no lugar. Nada é traçado e nenhuma `rows` é escrita, então um pacote pode recolorir uma textura vanilla ou de mod onde ela está:

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

Isso é um minério de rubi na própria pedra do vanilla: os quatro tons das manchas são trocados e todos os outros pixels ficam como estavam. Uma cor que a imagem não contém simplesmente nunca casa, e o tamanho vem da imagem a menos que você informe um, que então deve concordar.

`extends` dá preferência a um mapa de pixels: procura o mapa naquele caminho primeiro e só recorre à imagem quando nenhum pacote fornece um. Um nome que não é nenhum dos dois é reportado em vez de desenhado em branco. Construir sobre uma imagem é trabalho do lado do cliente, já que são os recursos do próprio jogo sendo lidos, então um servidor dedicado nunca o faz.

**Um modelo pode ser tingido em vez de repintado.** `tint` cita duas cores e recolore tudo o que o mapa herda ao longo da rampa entre elas. O brilho de cada cor herdada é o seu lugar nessa rampa: o preto cai em `from`, o branco cai em `to` e cada tom intermediário é misturado em proporção. A transparência não é tocada. Isso faz de um modelo em escala de cinza mais duas cores uma variante completa:

```json
{
  "extends": "mypack:textures/items/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` pode ser omitido, caso em que é preto e o tint vira uma multiplicação comum, no mesmo formato de um `tintindex` na hora de renderizar. A diferença é que este é desenhado no PNG uma vez e fica em cache, então não custa nada por quadro e alcança uma textura que nada tinge, mas também não consegue seguir um bioma como `grass` ou `foliage` conseguem.

O modelo continua sendo um mapa comum: abra-o, olhe para ele, e ele é desenhado no cinza que é. Ambas as cores aceitam `#RRGGBB`, `#AARRGGBB` ou um `0x` na frente, e um valor que não seja nenhum deles deixa o mapa sem desenhar em vez de desenhá-lo na cor errada. Um tint é herdado como todo o resto e o primeiro da cadeia vence, então o tint próprio de uma variante prevalece sobre o do mapa que ela estende. Funciona também em um modelo de imagem, onde roda depois das trocas de cor da paleta.

**Um tint é uma rampa entre duas cores**, então só serve para uma textura cujos tons ficam sobre uma. Uma forma com duas regiões sem relação, a pedra de um minério contra as manchas, não é esse caso e pede que a paleta seja escrita por extenso.

**Saber o que os caracteres de um modelo significam** é a parte incômoda de estender um, e é para isso que serve o bloco `notes` acima: um caractere para uma linha curta, herdada do mesmo modo que a paleta e nunca desenhada. Descreva os caracteres de um modelo e quem o estender saberá quais substituir.

`/rdpl pixelmap <namespace:caminho>` então informa o que um mapa realmente resultou, o que é a forma confiável de escrever uma variante sem abrir todos os arquivos da cadeia:

```
oretest:textures/blocks/ruby_ore.png is 16x16
  built from oretest:textures/blocks/ruby_ore.png.json
  built from oretest:textures/blocks/gem_ore.png.json
  rows come from oretest:textures/blocks/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Cada caractere é listado com sua cor, quantos pixels cobre, qual arquivo da cadeia o definiu e para que esse arquivo diz que ele serve. O caminho pode ser dado de forma curta, `mypack:blocks/panel`, ou por completo. Um caractere que mostra 0 pixels é um que a paleta cita e as fileiras nunca usam, o que geralmente é um erro de digitação em uma fileira.

**As imagens desenhadas ficam guardadas em disco** em `rdploader/pixelmap-cache`, em uma pasta por namespace e nomeadas com o nome da textura seguido de um hash da sua origem. O hash cobre a cadeia inteira, o próprio mapa e cada modelo acima dele, então editar um modelo muda o carimbo de toda variante que herda dele e todas são redesenhadas. Quando um mapa é redesenhado, seus arquivos mais antigos são varridos.

A pasta também é percorrida a cada vez que os pacotes são examinados, e toda imagem cujo mapa nenhum pacote mais fornece é apagada, junto com qualquer pasta que fique vazia. Renomeie uma textura, remova um pacote, apague um mapa, e sua imagem em cache vai embora junto, em vez de ficar lá para sempre. Apagar a pasta inteira não custa nada além do tempo de desenhá-las de novo, e ela é ignorada quando os pacotes são examinados, então nunca é confundida com um pacote.

Um PNG sempre vence. Se `panel.png` e `panel.png.json` existirem, o PNG é entregue e o mapa nunca é desenhado, então uma textura gerada pode ser substituída por uma pintada depois, sem mudar nada do que aponta para ela.

**Ninguém precisa escrever esses arquivos à mão.** O repositório traz scripts para todo o processo de ida e volta em [`pixelmap/`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/pixelmap): `png_to_pixelmap.py` transforma um PNG em um mapa, `convert_pack.py` faz isso com todas as texturas que um pacote contém, e `verify_pack.py` desenha os mapas de um pacote convertido e os compara aos PNGs de onde vieram, de modo que uma conversão pode ser confiável antes de os originais serem postos de lado.

### Armadilhas que vale conhecer

*modelos, blockstates e texturas*

**Um blockstate que cita um modelo vanilla simples herda também as texturas do vanilla.** `normal_torch`, `ladder`, `wooden_door_*` e `wheat_stage*` trazem todos suas próprias texturas, então um bloco que aponta para um deles recebe a aparência do vanilla, não importa o que você ponha no blockstate. Modelos pai como `cube_all`, `cross` e `block/crop` tiram suas texturas do blockstate e se comportam bem, assim como os modelos pai de porta, alçapão e portão listados em [Portas, alçapões e portões de cerca](#portas-alçapões-e-portões-de-cerca).

**`forge_marker: 1` não suporta multipart.** Um blockstate de vine precisa ser multipart vanilla simples, com as texturas embutidas no modelo em vez de passadas de fora.

**Os nomes vêm do arquivo de idioma, e um bloco quer DOIS deles.** Um bloco ou item mostra uma chave bruta até que `lang/en_us.lang` lhe dê um nome. O item que você segura e coloca é indexado pelo nome de registro do bloco com a variante depois, `tile.mypack:ruby_ore.ruby_ore.name=Ruby Ore`, e esse é o que a maioria dos pacotes lembra. O BLOCO em si é indexado apenas pelo nome de registro, `tile.mypack:ruby_ore.name=Ruby Ore`, e é isso que tudo o que pergunta o nome ao bloco colocado lê — a barra de título da tela de um contêiner entre elas. Escreva os dois, ou o item aparece certo na sua mão enquanto a tela que ele abre fica com o título em branco.

**Os tipos de variante única se nomeiam duas vezes.** Um bloco que pode ter várias variantes é indexado apenas pelo nome de registro, como acima. Um bloco cujos metadados vão todos para a forma acrescenta o nome da variante depois, então uma porta definida em `blocks/my_door.json` com uma variante chamada `my_door` é `tile.mypack:my_door.my_door.name=My Door`. Isso vale para `door`, `trapdoor`, `fence_gate`, `banner`, `stairs`, `ladder`, `torch`, `crop`, `cane`, `sapling` e `vine`. Onde um tipo assim tem um item próprio, como uma porta e um estandarte têm, ele quer a mesma chave de novo sob `item.` em vez de `tile.`.

## Fazendo o vanilla tratar seu bloco corretamente

*blocos e itens*

O vanilla verifica seus próprios blocos por identidade em uma dúzia de lugares, então um bloco de pacote que deveria obviamente funcionar muitas vezes não funciona. Duas chaves resolvem isso.

```json
{
  "material": "ground",
  "plantTypes": ["Plains", "Crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "meta": 0, "hardness": 0.6 } }
}
```

**`plantTypes`** lista os tipos de planta do Forge que o seu bloco suporta, para que mudas, plantações e flores possam ser plantadas nele.

**`behavesAs`** faz o vanilla tratar o seu bloco como um dos seus:

| Valor | O que faz |
| --- | --- |
| `till` | Uma enxada o transforma em terra arada |
| `path` | Uma pá o transforma em caminho de grama |
| `bush` | Flores, grama e mudas podem ser plantadas nele e permanecem nele, como na terra. O mesmo que `plains` em `plantTypes` |
| `animals` | Animais nascem nele sob luz, como na grama |

## Itens

*blocos e itens*

`<namespace>/items/*.json`

O caminho do arquivo é o nome de registro do item, então `mypack/items/ruby.json` registra `mypack:ruby`. As chaves dentro de `variants` nomeiam os valores de metadados desse item, e o modelo de cada uma fica em `models/item/ruby/<chave>.json`.

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

### Tipos de itens

*itens*

| Tipo | O que você obtém |
| --- | --- |
| `basic` | Um item simples. Usado quando `type` está ausente |
| `food` | Comido, com fome e saturação |
| `drink` | Bebido em vez de comido, devolvendo um recipiente vazio |
| `tool` | Picareta, machado, pá ou espada a partir de um material |
| `armor` | Capacete, peitoral, calças ou botas a partir de um material |
| `seed` | Planta uma das suas plantações |
| `potion` | Aplica os efeitos de poção que você definiu quando usado |
| `potion_bottle` | Guarda os seus tipos de poção e os mostra em uma aba criativa |
| `rocket` | Coloca uma das suas variantes de foguete do Galacticraft em uma plataforma de lançamento |

Um `potion_bottle` lista o que pode guardar com `potionTypes`, uma lista de nomes de tipos de poção como `["mypack:ruby_tonic"]`. Um com a lista vazia não registra nada, e o log avisa.

Um `rocket` cita a variante de entidade que coloca com `rocket`. Ele precisa do Galacticraft e, sem ele ou sem `rocket`, não registra nada, e o log avisa. Veja os foguetes do Galacticraft em Variantes de entidades.

### Chaves de arquivo de itens

*itens*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `variants` | sim | objeto de nome de variante para variante | | Uma entrada por valor de metadados. A chave dá nome a esse valor no blockstate, no caminho do modelo e na chave de idioma. O nome de registro vem do próprio caminho do arquivo |
| `type` | não | um dos tipos acima | `basic` | Qual tipo o item assume |
| `creativeTab` | não | nome da aba | nenhuma | A aba em que aparece |
| `material` | tool, armor | nome do material | nenhum | De qual dos seus materiais é feito |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | nenhuma | Qual ferramenta é |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | nenhum | Onde é vestido. `helmet`, `chestplate`, `leggings` e `boots` também funcionam |
| `eat` | food | booleano | `false` | Usa a animação de comer |
| `alwaysEdible` | food | booleano | `false` | Pode ser comido com a barra de fome cheia |
| `useDuration` | não | inteiro, ticks | `32` | Quanto tempo leva para usar |
| `attackSpeed` | não | decimal | conforme a classe da ferramenta | Para `tool`, o atributo de velocidade de ataque, como em uma espada, que é `-2.4` |
| `cooldown` | não | inteiro, ticks | `0` | Para `food`, `drink` e `potion`, por quanto tempo o item recusa novo uso depois de consumido; num item com `rolls`, o tempo entre rolagens |
| `container` | drink | nome de item | nenhum | O que sobra, como uma garrafa |
| `crop` | seed | nome de bloco | nenhum | A plantação que planta |
| `soil` | seed | nome de bloco | `minecraft:farmland` | Onde pode ser plantada |
| `rocket` | rocket | `namespace:nome` | nenhum | A variante de entidade que coloca |
| `rolls` | não | `coin`, `d6`, `2d6+1`, um dado ou um baralho | nenhum | Num item simples, um clique direito rola como `/rdplserver game` faria e conta ao público padrão do pacote. Veja [Dados e baralhos](#dados-e-baralhos) |
| `passesTurn` | não | booleano | `false` | Num item simples, um clique direito passa a vez de quem o segura, como `/rdplserver game pass` faz. Veja [Turnos](#turnos) |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhuma | O arquivo é ignorado a menos que todos estejam presentes |

### Chaves de variantes de itens

*itens*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `meta` | sim | 0 a 15 | | O valor de metadados que esta variante reivindica |
| `maxSize` | não | 1 a 64 | `64` | Tamanho da pilha |
| `rarity` | não | `common`, `uncommon`, `rare`, `epic` | `common` | Cor do nome na dica |
| `healAmount` | food | inteiro, meias coxinhas | `0` | Fome restaurada |
| `saturation` | food | decimal | `0.0` | Saturação restaurada |
| `oreDict` | não | lista de nomes do dicionário de minérios | nenhuma | Nomes do dicionário de minérios sob os quais esta variante é registrada |
| `potion` | food, drink | `potion,duration,amplifier` | nenhum | Um efeito aplicado quando a variante é comida ou bebida. Uma quarta parte, `true`, o torna ambiente. Um efeito benéfico é citado na dica |

## Fluidos

*blocos e itens*

`<namespace>/fluids/*.json`

O caminho do arquivo é o nome de registro do fluido, a menos que `name` o substitua.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `name` | não | texto | o nome do arquivo | O nome de registro do fluido |
| `still` | não | caminho de textura | água parada do vanilla | Textura do fluido parado |
| `flow` | não | caminho de textura | água corrente do vanilla | Textura do fluido corrente |
| `color` | não | cor hexadecimal | nenhuma | Cor aplicada a essas texturas |
| `bucket` | não | booleano | `true` | Registra um balde para ele |
| `luminosity` | não | 0 a 15 | `0` | Luz emitida |
| `density` | não | inteiro | `1000` | Negativa flutua para cima, como um gás |
| `temperature` | não | inteiro, kelvin | `300` | A água é 300, a lava 1300 |
| `viscosity` | não | inteiro | `1000` | Quão devagar ele flui. A água é 1000, a lava 6000 |
| `gaseous` | não | booleano | `false` | Tratado como gás |
| `creativeTab` | não | nome da aba | nenhuma | A aba em que o balde aparece |
| `block` | não | objeto | | O bloco do fluido. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`), `potions` (nenhum, uma lista de efeitos dados a tudo o que estiver nele, cada um escrito como `potion,duration,amplifier` com uma quarta parte opcional `true` para um efeito ambiente) |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhuma | O arquivo é ignorado a menos que todos estejam presentes |

## Materiais, abas, sons, dicionário de minérios

*blocos e itens*

`<namespace>/materials/*.json`

O caminho do arquivo é o nome do material, que um item de ferramenta ou armadura então cita em `material`.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `harvestLevel` | não | 0 a 3 | `1` | Nível da ferramenta. 0 madeira, 1 pedra, 2 ferro, 3 diamante |
| `durability` | não | inteiro | `250` | Usos antes de quebrar |
| `efficiency` | não | decimal | `6.0` | Velocidade de mineração. O diamante é 8 |
| `damage` | não | decimal | `2.0` | Bônus de dano de ataque |
| `enchantability` | não | inteiro | `14` | Quão bons são os encantamentos. O ouro é 22 |
| `repairItem` | não | nome de item | nenhum | O que o repara em uma bigorna |
| `reduction` | não | lista de quatro inteiros | | Pontos de armadura, na ordem botas, calças, peitoral, capacete |
| `toughness` | não | decimal | `0.0` | Resistência da armadura, como a do diamante |
| `equipSound` | não | nome de som | `item.armor.equip_iron` | Som ao vestir a armadura |
| `armorTexture` | não | prefixo de textura | o nome do arquivo | A textura da armadura vestida |

### Abas criativas

*materiais, abas, sons, dicionário de minérios*

`<namespace>/tabs/*.json`

O caminho do arquivo é o nome da aba, a menos que `label` o substitua, e blocos e itens a citam em `creativeTab`.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `label` | não | texto | o nome do arquivo | O id da aba: blocos e itens a citam em `creativeTab`, e o nome exibido vem de `itemGroup.<label>` nos arquivos de idioma |
| `icon` | não | nome de item | nenhum | O item exibido na aba |

### Sons

*materiais, abas, sons, dicionário de minérios*

`<namespace>/sounds/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

O formato `sounds.json` do vanilla, para que um pacote possa trazer seu próprio áudio.

### Dicionário de minérios

*materiais, abas, sons, dicionário de minérios*

`<namespace>/oredict/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

Acrescenta nomes do dicionário de minérios a itens que já existem. Cada chave é um nome do dicionário de minérios e seu valor, os itens registrados sob ele, então um arquivo não tem chaves fixas próprias. Os blocos e itens do próprio pacote citam os seus em `oreDict` na variante.

Uma chave começando com `-` remove em vez de acrescentar: `"-ingotCopper": ["thermalfoundation:material:128"]` tira esse item do nome, e `["*"]` esvazia o nome. As receitas que usavam o nome deixam de combinar com o item imediatamente, o que é o objetivo. Um nome que nada registra é recusado com um erro, e o mesmo vale para um item que o nome não contém. Uma entrada registrada para todos os metadados, como o vanilla registra `plankWood`, é removida inteira qualquer que seja o metadado citado, e o log avisa; cite-a com `:*` para dizer a mesma coisa abertamente.

```json
{
  "_note": "ruby equivalents",
  "gemRuby": ["mypack:ruby", "mypack:polished_ruby:1"],
  "oreRuby": ["mypack:ruby_ore", "minecraft:redstone_ore"]
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| um nome do dicionário de minérios | sim | lista de nomes de itens | | Os itens registrados sob ele. Metadados como terceira parte, `"mypack:ruby:1"` |
| um nome começando com `_` | não | qualquer coisa | | Ignorado, para que um arquivo possa carregar uma nota para si mesmo |

## Substituições de propriedades

*blocos e itens*

`<namespace>/overrides/<alvo>/<nome>.json`

O caminho nomeia o alvo: tudo depois de `overrides/` é o namespace e o nome do bloco, item ou tipo de poção que está sendo alterado.

Em todos os outros lugares, um pacote substitui um arquivo ou acrescenta um. Uma substituição não faz nem um nem outro: ela altera as propriedades de um bloco, item ou tipo de poção que já existe, vanilla ou de mod, sem tocar em nenhum de seus arquivos. O caminho nomeia o alvo, então `overrides/minecraft/stone.json` altera `minecraft:stone`, e `overrides/tconstruct/<nome>.json` altera o bloco desse mod do mesmo modo.

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

Toda chave é opcional e um arquivo altera apenas o que cita, então um arquivo em `overrides/minecraft/stone.json` contendo somente `hardness`, `light` e `soundType` faz a pedra ser minerada quase instantaneamente, brilhar e soar como vidro. Um arquivo carrega chaves de bloco, de item e de poção juntas. Estas se aplicam quando o alvo é um bloco:

| Chave | Valor | O que faz |
| --- | --- | --- |
| `hardness` | decimal | Tempo de mineração, o mesmo valor que uma definição de bloco aceita |
| `resistance` | decimal | Resistência a explosões |
| `slipperiness` | decimal | `0.6` é chão comum, `0.98` é gelo |
| `light` | `0` a `15` | Luz emitida |
| `lightOpacity` | `0` a `255` | Quanta luz o bloco detém |
| `soundType` | um dos tipos de som | Sons de passo, de colocação e de quebra |
| `harvestTool` | classe de ferramenta | O que o minera; `harvestToolLevel`, padrão `0`, define o nível |
| `flammability` | inteiro | Com que facilidade ele queima; `fireSpread`, padrão `5`, com que facilidade o fogo o alcança |

### Propriedades de itens

*substituições de propriedades*

E estas quando o alvo é um item:

| Chave | Valor | O que faz |
| --- | --- | --- |
| `maxStackSize` | `1` a `64` | Tamanho da pilha |
| `maxDamage` | inteiro | Durabilidade |
| `containerItem` | nome de item | Deixado na grade de criação, como acontece com um balde |
| `food` | objeto | Torna o item comestível, veja abaixo |

Um nome que é ao mesmo tempo bloco e item, e o item de todo bloco colocável é, aceita os dois grupos em um só arquivo:

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

Em `overrides/minecraft/planks.json` isso faz as tábuas quebrarem quase tão rápido quanto terra e permite comê-las. `food` aceita `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false`; `true` permite comer com a barra de fome cheia) e `effects`, cujas entradas são escritas exatamente como as de um tipo de poção. Um item que já é comida recebe novos `heal`, `saturation` e `alwaysEdible`; `effects` em um deles não é suportado, e o log avisa. Quando o item comestível coloca um bloco, mire no céu para comer, já que mirar em um bloco o coloca: essa é a ordem de uso do vanilla, não um bug.

### Efeitos de tipos de poção

*substituições de propriedades*

`effects` no nível superior do arquivo reescreve de uma vez a lista de efeitos de um tipo de poção:

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

Em `overrides/minecraft/swiftness.json`, a Poção de Agilidade agora concede Levitação. Cada entrada aceita `potion` (obrigatório), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) e `showParticles` (`true`), igual a em `potion_types/`, e a lista não pode ser vazia.

### Outros mods, recarregamentos e limites

*substituições de propriedades*

Um alvo que pertence a outro mod deve levar esse mod em `requires`, para que o arquivo seja ignorado silenciosamente quando o mod não está instalado, em vez de ser reportado como alvo ausente:

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

As substituições são ao vivo. Os valores originais são lembrados antes da primeira alteração, então desativar o pacote e executar `/rdpl reload` devolve tudo ao que era, sem reiniciar; o mesmo acontece a cada entrada em um mundo. Um arquivo por alvo: quando dois pacotes substituem a mesma coisa, o arquivo do pacote posterior substitui o anterior por inteiro, e o log avisa.

Dois limites que vale conhecer. Um bloco ou item cujo próprio código calcula uma propriedade ignora o campo por trás dela, então a substituição se aplica mas não muda nada; o vanilla só faz isso com a resistência a explosões das escadas, mas os mods são livres para fazê-lo em qualquer lugar. E itens tornados comestíveis só funcionam em itens sem comportamento próprio de clique direito: um item que já faz algo quando usado continua fazendo isso.

As substituições exigem o pacote tanto no cliente quanto no servidor, já que a velocidade de mineração, a luz e o ato de comer acontecem todos na tela do jogador, então não servem para pacotes do lado do servidor. `overrides` na categoria de config `content` desativa a pasta por completo.

## Grupos de dureza

*blocos e itens*

`<namespace>/hardness/*.json`

O caminho do arquivo nomeia o grupo no log e nada mais o lê, então vários arquivos se acumulam.

Dá a um grupo de blocos um multiplicador de tempo de mineração, sorteado por posição de bloco. O bloco em si nunca é alterado: nada é registrado, nada é escrito no mundo, e um mundo aberto sem o pacote é vanilla comum.

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

### Mineração e detonação

*grupos de dureza*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `blocks` | sim | lista de nomes de blocos ou objetos | | O grupo. As mesmas três formas de um `replace` do worldgen |
| `except` | não | lista de nomes de blocos ou objetos | nenhuma | Retirados do grupo, seja o que `blocks` disser |
| `miningTime` | não | número, ou objeto com `min` e `max` | `1.0` | Quantas vezes mais tempo o bloco leva para quebrar, tanto para um jogador quanto para um mob com `digs` |
| `blastResistance` | não | número, ou objeto com `min` e `max` | `1.0` | Multiplica a resistência a explosões do bloco |
| `buckets` | não | 1 a 256 | `10` | Em quantos degraus o intervalo é dividido |
| `minHeight` | não | inteiro | `0` | Abaixo disto o sorteio é o degrau mais duro |
| `maxHeight` | não | inteiro | `255` | Acima disto o sorteio é o degrau mais duro |
| `field` | não | objeto | veja abaixo | A forma em que o sorteio se agrupa |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhuma | O arquivo é ignorado a menos que todos estejam presentes |

Um único número dá a todo bloco do grupo o mesmo multiplicador, e nada é sorteado. Um `min` e um `max` sorteiam por posição: `max` onde o campo está vazio, `min` no meio de um aglomerado, e os degraus entre eles decididos por `buckets`.

### Mineração de aventura e desbloqueios

*grupos de dureza*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `keeps` | não | booleano | `false` | O bloco permanece onde está quando é minerado: os drops, a experiência, o desgaste da ferramenta e o som de quebra acontecem e o bloco continua lá para ser minerado de novo, então o grupo é um veio sem fim no ritmo que `miningTime` definir. O criativo o remove como sempre |
| `adventure` | não | objeto | nenhum | Quem pode quebrar o grupo no modo aventura, onde nada quebra de outra forma. `tools` lista os itens dos quais um deve estar na mão, vazio para qualquer coisa segurada; `teams`, `players` e `entities` dizem quem, uma equipe pelo nome, um jogador pelo nome, um mob pelo id de entidade para a tarefa `digs`, e os três vazios significam qualquer um com a ferramenta. Sobrevivência e criativo não são afetados |
| `advancement` | não | `namespace:caminho` | nenhum | O grupo só vale para um jogador depois que ele tiver esse progresso. Dois grupos podem citar o mesmo bloco, um com progresso e outro sem, e o desbloqueado vence; um jogador sem ele recebe o grupo simples, ou o vanilla se não houver. Mobs não têm progressos, então um grupo restrito nunca chega a uma tarefa `digs`, e a resistência a explosões e o sorteio de textura, que não pertencem a nenhum jogador, vêm do grupo simples |
| `becomes` | não | objeto | nenhum | Os blocos do grupo se transformam em outro bloco, no mundo todo, no momento em que qualquer jogador conquista `advancement`: `{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`. Todo chunk carregado é varrido de uma vez, um chunk carregado depois é varrido ao entrar, e um chunk criado depois é varrido logo após seu minério ser colocado, então o bloco antigo some para sempre. Dê ao novo bloco um grupo próprio para mudar como ele é minerado |

### O campo

*grupos de dureza*

O sorteio não é feito para cada bloco inteiramente por conta própria, ou duro e macio seriam pura estática, sem forma alguma. `field` decide que forma ele toma, e `type` escolhe entre duas maneiras de chegar lá.

```json
{
  "field": { "type": "speckle" }
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `type` | não | `speckle` ou `seeded` | `speckle` | Qual das duas abaixo é usada |

#### speckle

*o campo*

Cada bloco sorteia seu próprio degrau, e um bloco a uma face de distância pode passar a ele um degrau mais fraco. Isso dá manchas densas e de grão fino, a maioria de um único bloco, com um ou outro trecho maior onde elas se encontram. É a mais próxima das duas da sensação de mineração do mod de onde isto foi emprestado.

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `chances` | não | lista de inteiros, por mil | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | Com que frequência um bloco começa em cada degrau, o mais macio por último. O que sobrar é o degrau mais duro |
| `spread` | não | 0.0 a 1.0 | `0.15` | Com que frequência um degrau passa ao bloco vizinho, um degrau mais fraco ou três |

A lista é lida do mais macio para o último, então a entrada final é o degrau mais macio e a primeira é um acima do mais duro. Com os números acima, cerca de sete blocos em cada dez ficam no degrau mais duro e o resto se espalha por ele.

#### seeded

*o campo*

As sementes ficam em uma malha calculada a partir do mundo e da posição, e o degrau de um bloco vem da proximidade da semente mais próxima. Isso dá trechos menos numerosos, maiores e mais arredondados que se fundem uns nos outros, e pode fazer crescer braços.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `cell` | não | inteiro, blocos | `8` | A distância entre as sementes |
| `seeds` | não | 1 a 4 | `1` | Sementes em cada célula |
| `reach` | não | decimal, blocos | `3.0` | Até onde chega a influência de uma semente |
| `arms` | não | 0 a 6 | `0` | Braços que irradiam de cada semente |
| `armReach` | não | decimal, blocos | `0.0` | Até onde chegam os braços |

Com `arms` omitido, os trechos são redondos. Dar braços a uma semente a transforma em um nó com tentáculos, e os braços de nós vizinhos se estendem uns para os outros, o que é um veio e não uma bolha. Mantenha `reach` acima da metade de `cell`, ou os trechos não conseguem se tocar e você obtém bolas separadas sem nada entre elas.

### Exibindo

*grupos de dureza*

O multiplicador é invisível por si só. Para que um jogador veja quais blocos são resistentes, dê ao bloco um blockstate com uma variante por bucket, todas de peso igual, listadas da mais dura primeiro:

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

O Minecraft já escolhe uma variante a partir da posição de um bloco, e um grupo de dureza lhe entrega o bucket em vez disso, então a textura e o multiplicador sempre concordam.

Três coisas precisam estar certas, e nenhuma delas se anuncia quando está errada.

**Exatamente `buckets` entradas, todas com o mesmo peso.** O bucket é usado como uma posição na lista, então uma lista de tamanho diferente, ou uma em que os pesos diferem, aponta silenciosamente para a textura errada.

**Um nome de modelo sem `block/` na frente.** Um blockstate acrescenta `block/` sozinho, então `"model": "mypack:step_stone"` lê o arquivo em `models/block/step_stone.json`. Escrever `mypack:block/step_stone` procura `models/block/block/step_stone.json`, que não existe, e a entrada é descartada sem uma palavra.

**A mesma chave que o jogo pede.** Nem todo bloco é indexado como suas propriedades sugerem. A pedra vanilla indexa tudo sob `normal`, não `variant=stone`, então uma substituição que só escreve `variant=stone` é mesclada e depois nunca consultada. Escrever as duas chaves é seguro, já que a mesclagem é por chave e um pacote prevalece sobre o que veio antes.

Ative `worldgenDebug` e todo grupo de dureza é verificado contra seu modelo assado ao entrar em um mundo, nomeando o blockstate, quantas variantes sobreviveram, que textura cada uma acabou recebendo e quais pacotes o jogo mesclou para chegar lá. Essa é a forma mais rápida de achar qualquer um dos três problemas acima, e também avisa quando substituir um blockstate compartilhado alterou um estado que o grupo nunca citou.

### O que não alcança

*grupos de dureza*

Somente a mineração do próprio jogador é alterada. Máquinas que quebram blocos leem a dureza do bloco diretamente e não são afetadas. Blocos que um jogador coloca são sorteados como quaisquer outros, já que o sorteio pertence ao lugar e não ao bloco, e um bloco levado para outro lugar assume o que seu novo lugar disser.

---

# Criação, saque e comércio

## Blocos e itens desativados

*criação, saque e comércio*

`<namespace>/disabled/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

Tira blocos e itens de jogo sem desregistrá-los, então os mundos mantêm seus ids e apagar o arquivo traz tudo de volta. Conteúdo vanilla, de mods e de pacotes é tratado igualmente, inclusive os blocos e itens do próprio pacote, e um bloco desativado desativa seu item assim como um item desativado desativa seu bloco.

```json
{
  "requires": ["thermalfoundation"],
  "names": ["thermalfoundation:ore", "thermalfoundation:material:128", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "oreDict": ["oreTin"]
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `names` | não | lista de nomes de blocos e itens | nenhuma | O que é desativado. Metadados como terceira parte, `"thermalfoundation:material:128"`, desativam apenas esse item, e `:*` todos os metadados. Um nome terminado em `*` casa com todo nome que começa com o restante |
| `namespaces` | não | lista de ids de mods | nenhuma | Todo bloco e item do mod |
| `oreDict` | não | lista de nomes do dicionário de minérios | nenhuma | Todo item registrado sob o nome, e o nome fica vazio |
| `requires` | não | lista de ids de mods | nenhuma | O arquivo é ignorado a menos que todos estejam carregados. Entradas `config:` e `file:` funcionam como em todo o resto |

Um bloco ou item desativado:

- some de todas as abas criativas e da aba de busca, e fica oculto no JEI e no HEI
- não tem receita que o produza nem que o use: toda receita de criação com ele como resultado some, e também toda receita de criação com um espaço que só ele pode preencher, junto com toda receita de fornalha que o derrete ou derrete até ele. Um espaço que também aceita outra coisa mantém sua receita, e um espaço de dicionário de minérios simplesmente a perde junto com o nome
- é retirado de todo nome do dicionário de minérios
- é removido de todo sorteio de saque, baús, mobs e pesca por igual, de drops de blocos e de trocas de aldeões, e uma pilha dele solta desaparece
- não pode ser colocado, usado, brandido nem recolhido, e a pilha na mão é apagada quando um jogador tenta
- é apagado onde quer que uma pilha dele apareça: do inventário e do baú do Fim de um jogador ao entrar e a cada segundo depois, de qualquer contêiner quando um jogador o abre, e de baús e outros inventários quando seu chunk carrega
- é removido do mundo onde está colocado: cada bloco dele vira ar, junto com sua entidade de bloco, quando seu chunk carrega

Para trocar blocos colocados por outra coisa em vez de removê-los, dê a eles uma linha `blockReplacements` como `thermalfoundation:ore=minecraft:stone` no modelo de mundo, veja [Substituições](#substituições). Um bloco que o processo de substituição troca fica por conta dele. As receitas que outro mod mantém dentro de suas próprias máquinas pertencem a esse mod e não são alcançadas. Os arquivos são lidos uma vez na inicialização, e `content.disabled` na config desativa a pasta, o que exige reinício.

Para esvaziar um nome do dicionário de minérios enquanto seus itens continuam em jogo, use `"-name": ["*"]` em um arquivo de [dicionário de minérios](#dicionário-de-minérios).

## Receitas de fornalha e combustíveis

*criação, saque e comércio*

`<namespace>/furnace/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

Acrescenta e remove receitas de fundição.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `input` | sim | nome de item | nenhum | O que entra |
| `output` | sim | nome de item | nenhum | O que sai |
| `count` | não | inteiro | `1` | Quantos saem |
| `experience` | não | número | `0.0` | Experiência por fundição. O minério de ferro dá 0.7 |

Uma adição cuja entrada algo já funde é ignorada, e o log informa em que essa entrada se funde agora; remova essa receita no mesmo arquivo para substituí-la.

As entradas em `remove` são ou um nome de item simples, que remove toda receita que o produz, ou um objeto citando `input`, `result` ou ambos para restringir. Uma remoção que não cita nenhum dos dois é ignorada e o log avisa.

`<namespace>/fuels/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "oreDict": "gemRuby", "burnTime": 800 }
  ]
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `item` | uma das duas | nome de item | nenhum | O item que queima |
| `oreDict` | uma das duas | nome do dicionário de minérios | nenhum | Tudo sob esse nome queima |
| `burnTime` | sim | inteiro, ticks | `0` | O carvão é 1600, uma tábua 300 |

## Poções, tipos de poção e fermentação

*criação, saque e comércio*

`<namespace>/potions/*.json`

O caminho do arquivo é o nome de registro do efeito, então `mypack/potions/ruby_sight.json` registra `mypack:ruby_sight`, que um tipo de poção então cita.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `name` | não | chave de tradução | `effect.<namespace>.<nome>` | O que o jogador vê |
| `color` | não | cor hexadecimal | `FFFFFF` | Cor da partícula |
| `badEffect` | não | booleano | `false` | Conta como prejudicial, então um olho de aranha fermentado o inverte |
| `beneficial` | não | booleano | `false` | Exibido como um efeito bom |
| `instant` | não | booleano | `false` | Aplica uma vez em vez de ao longo do tempo |
| `effectiveness` | não | decimal | `0.5` | Quanto a IA dos mobs o valoriza |
| `icon` | não | objeto com `x` e `y` | `0`, `0` | Onde o ícone fica na folha |
| `iconTexture` | não | caminho de textura | o ícone do RDPL, ou a folha vanilla quando `icon` está definido | Seu próprio ícone de 18 por 18 |
| `attributes` | não | lista de objetos | nenhuma | `attribute`, `uuid`, `amount` (`0.0`), `operation` (`0`) |

### Tipos de poção

*poções, tipos de poção e fermentação*

`<namespace>/potion_types/*.json`

O caminho do arquivo é o nome de registro do tipo de poção, que um item `potion_bottle` então cita em `potionTypes`.

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `baseName` | não | texto | o namespace e o nome | O nome a partir do qual o frasco é construído |
| `effects` | sim | lista de objetos | | Veja abaixo |

Cada efeito aceita `potion` (obrigatório), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) e `showParticles` (`true`).

### Poções e fermentação

*poções, tipos de poção e fermentação*

`<namespace>/brewing/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

Cada entrada é ou `input`, `ingredient` e `output`, que fermenta um item em outro, ou `from`, `ingredient` e `to`, que transforma um tipo de poção em outro. `ingredient` é obrigatório nos dois casos, e uma entrada também aceita `requires`, então uma receita pode ser ignorada sem que o arquivo o seja.

## Trabalho na bigorna

*criação, saque e comércio*

`<namespace>/anvils/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam. Cada arquivo é um trabalho.

Coloque o item indicado no slot esquerdo de uma bigorna e o item `with` no direito, e a bigorna devolve o item da esquerda com os encantamentos listados, ou o seu `result`, pelos níveis indicados; um de cada é consumido, a menos que uma quantidade peça mais, e o restante de cada pilha permanece na bigorna. Retirar o resultado também pode render um progresso, e o item pode ficar impedido de uso até que esse progresso seja obtido: uma espada que só golpeia depois de trabalhada.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `item`         | sim | nome do item, ou `{ "item", "count" }` |               | O que vai no slot esquerdo e quantos dele um trabalho consome, um por padrão; o resto da pilha fica para o próximo. `{ "item": "minecraft:coal", "count": 8 }` com um `result` de diamante são oito carvões por um diamante. Metadata como `minecraft:dye:4` |
| `with`         | sim | nome do item, ou `{ "item", "count" }` |               | O que vai no slot direito e quantos dele são gastos, um por padrão: `{ "item": "minecraft:coal", "count": 10 }` exige uma pilha de pelo menos dez e consome dez. A bigorna nunca se manifesta para um item isolado, então todo trabalho é um par |
| `result`       | não | nome do item, ou `{ "item", "count" }` | o item da esquerda | O que sai no lugar do item da esquerda, e quantos, um por padrão, mantendo as tags do item da esquerda, de modo que uma picareta de ferro inquebrável e dez carvões podem voltar como uma picareta de diamante inquebrável. Os encantamentos vão para o que sair |
| `levels`       | não | int | `1` | Os níveis de experiência que o trabalho custa, no mínimo 1 |
| `enchantments` | não | objeto de nome de encantamento para nível | nenhum | Com o que o item volta. Um nível que ele já tem naquele valor ou acima é deixado como está, e, sem nada a elevar, a bigorna não oferece nada, a menos que `grants` esteja definido |
| `grants`       | não | `namespace:path` | nenhum | Um progresso obtido quando o trabalho é retirado. Inclua-o em `advancements/` com um critério `impossible`, para que nada mais o conceda |
| `locks`        | não | booleano | `false` | Até o jogador ter `grants`, o item não pode golpear nada, ser usado nem cavar; ele é avisado do que o item aguarda assim que o recebe na mão. Colocá-lo na bigorna continua permitido, que é justamente como ele é desbloqueado |

Os reparos e combinações próprios da bigorna permanecem intactos: isto só responde quando a esquerda contém um item indicado e a direita contém o seu `with`.

Um mob com `collectsExperience` também gasta seus níveis aqui. Enquanto segura `item` na mão principal e `with` na mão secundária e tem `levels` para pagar, ele caminha até uma bigorna a até 16 blocos e a opera quando está a até 3 blocos: os níveis saem dos dele como saem dos de um jogador, `with` é consumido, a bigorna se desgasta como sob um jogador, e o trabalho termina na mão principal dele. Ao passar por um item largado que algum trabalho de bigorna cita em `with`, ele o pega para a mão secundária. `grants` e `locks` só dizem respeito a jogadores, então um mob não obtém nada de `grants` e nenhum bloqueio o impede.

## Drops de blocos

*criação, saque e comércio*

`<namespace>/block_drops/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

Os blocos do vanilla 1.12 não têm tabelas de saque, então um pacote podia acrescentar ao que seus próprios blocos soltam, mas não mexer em pedra, em um minério ou no bloco de outro mod. Isto permite: uma regra indica um bloco e o que um jogador que o colhe solta além dos drops habituais, ou no lugar deles.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `block`       | sim | id de bloco |         | O bloco que a regra observa |
| `meta`        | não | int | `-1` | Apenas esta metadata do bloco; `-1` é todo estado |
| `replace`     | não | booleano | `false` | Se os drops habituais são descartados antes de estes serem sorteados |
| `advancement` | não | `namespace:path` | nenhum | A regra só vale para um jogador que tenha esse progresso, de modo que o mesmo bloco pode soltar uma coisa antes e outra depois |
| `drops`       | sim | lista de drops |         | Cada um sorteado separadamente quando um jogador quebra o bloco |

Cada drop:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `item`       | sim, a menos que haja `experience` | id de item |          | O que cai, com metadata como `minecraft:dye:4` |
| `experience` | não | número ou `low-high` |          | No lugar de um item, essa quantidade de experiência em orbes, sorteada de modo uniforme dentro do intervalo. `chance` e `silkTouch` valem como para um item |
| `count`      | não | número ou `low-high` | `1`      | Quantos, sorteados de modo uniforme dentro do intervalo |
| `chance`     | não | float | `1.0`    | A probabilidade de o drop ocorrer, sendo `0.05` uma quebra em vinte |
| `fortune`    | não | int | `0`      | Até esta quantidade extra por nível de Fortuna na ferramenta |
| `silkTouch`  | não | `either`, `only` ou `never` | `either` | Se o drop exige uma ferramenta com Toque Suave, a recusa, ou tanto faz |

As regras só enxergam a colheita de um jogador; explosões, pistões e o grief de mobs não sorteiam nada. Várias regras para um mesmo bloco se aplicam todas, e um `replace` em qualquer uma delas limpa antes os drops habituais.

## Saque de jogadores

*criação, saque e comércio*

`<namespace>/player_loot/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

O vanilla 1.12 não dá tabela de saque aos jogadores — a morte solta apenas o inventário, e não existe nome de tabela que um pacote possa sobrescrever. O RDPL adiciona uma, sorteada quando um jogador morre:

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `table`               | sim | nome de tabela |         | A tabela de saque sorteada quando um jogador morre |
| `mode`                | não | `add` ou `replace` | `add`   | Se os itens da tabela se juntam ao inventário ou tomam o lugar dele |
| `rollOnKeepInventory` | não | booleano | `false` | Se a tabela chega a ser sorteada em uma morte que manteve o inventário |
| `dropLoose`           | não | booleano | `false` | Se os itens são colocados diretamente no chão em vez de se juntarem aos drops da morte |

`add` solta os itens da tabela junto com o inventário — use para recompensas por abate. `replace` descarta o inventário e solta apenas o que a tabela sortear.

Com `rollOnKeepInventory` desativado, mortes sob `keepInventory` (e mortes de espectadores, que sempre mantêm o inventário) não sorteiam nada. Ativá-lo mantém as mortes custosas em mundos com keep-inventory.

Vários arquivos se acumulam, cada um avaliado por conta própria. Se qualquer entrada aplicável for `replace`, o inventário é limpo uma única vez antes do sorteio, de modo que uma entrada `add` ao lado dela ainda é entregue.

A tabela é uma tabela de saque comum, buscada por nome: pode ficar no pacote em `loot_tables/entities/player.json`, ser qualquer tabela do vanilla ou de mod, e ser alcançada por `loot_injections`. Contexto de saque: o jogador que morre é a entidade saqueada, o assassino (se houver) é o jogador que matou, e a fonte de dano está definida — `killed_by_player`, `entity_properties`, `random_chance_with_looting`, `looting_enchant` e `quality` se comportam normalmente.

Uma função de saque é própria do RDPL, utilizável em qualquer tabela com uma entidade saqueada: `rdpl:killed_name` dá ao item largado o nome da vítima. `format` molda o nome exibido (`%s` é a vítima, por padrão só o nome), e `tag` escreve, em vez disso, o nome simples em uma chave de string NBT, para itens que o leem por conta própria.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Mods de túmulos.** Os itens sorteados se juntam aos drops habituais da morte antes de qualquer mod de túmulo lê-los, então acabam no túmulo junto com tudo mais (`replace` coloca o conteúdo da tabela no túmulo em vez do inventário). Vale para Gravestone, GraveStone Mod, Corail Tombstone e qualquer outro que trabalhe a partir da lista de drops da morte. Nenhuma configuração necessária.

`dropLoose` ignora a lista de drops por completo: os itens são colocados diretamente no mundo, então os mods de túmulos nunca os veem — o inventário vai para o túmulo, os itens da tabela ficam no chão para o assassino. Use para espólios que pertencem ao assassino e não ao túmulo da vítima. Sem um mod de túmulo, muda pouco. Ressalva: os itens passam a existir antes que qualquer coisa adiante possa cancelar os drops, então entradas que não devem sobreviver a uma morte cancelada devem deixá-lo desativado.

Defina `playerLoot` na categoria `data` da config como `false` para desativar a pasta por completo.

## Aldeões e trocas

*criação, saque e comércio*

`<namespace>/villagers/*.json`

O caminho do arquivo é o nome de registro da profissão, então `mypack/villagers/jeweller.json` registra `mypack:jeweller`, que uma troca então cita em `profession`.

```json
{
  "careers": ["gem_cutter", "appraiser"],
  "texture": "mypack:textures/entity/villager/jeweller.png",
  "zombieTexture": "mypack:textures/entity/zombie_villager/jeweller.png"
}
```

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `careers`       | sim | lista de nomes | nenhum                        | As carreiras que esta profissão oferece. Uma profissão sem nenhuma é recusada |
| `texture`       | não | caminho de textura | o aldeão do vanilla        | A aparência do aldeão |
| `zombieTexture` | não | caminho de textura | o aldeão zumbi do vanilla | A aparência depois de zumbificado |

### Trocas

*aldeões e trocas*

`<namespace>/trades/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `profession` | sim | nome da profissão |         | De quem é esta troca |
| `career`     | sim | nome da carreira |         | Qual carreira dentro dela |
| `level`      | não | int | `1`     | Em qual nível de troca ela aparece |
| `maxUses`    | não | int | `12`    | Vezes que pode ser usada antes de travar |

Uma pilha é `item` com `min` (`1`) e `max` (`min`), então um preço fixo é apenas `min`.

---

# Criaturas e perigos

## Variantes de entidades

*criaturas e perigos*

`<namespace>/entities/*.json`

O caminho do arquivo é o nome de registro da variante, então `mypack/entities/angry_cow.json` registra `mypack:angry_cow`, que é ao que `becomes`, um ovo de spawn e o save de um mundo se referem.

Um arquivo aqui cria uma nova entidade a partir de uma que já existe. É uma entidade de verdade, com nome de registro próprio, nome próprio no mundo, ovo de spawn próprio e uma tabela de saque própria, se você der uma, construída sobre o comportamento de outra entidade em vez de substituí-la. Nada na entidade copiada muda.

Todas as chaves, mostradas de uma vez. Um arquivo de verdade escreve só as que precisa.

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

### Identidade

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `entity`        | sim | `namespace:name` | nenhum | A entidade em que se basear. De qualquer mod, desde que aceite um construtor simples de mundo |
| `name`          | não | string | nenhum | O nome que ela leva no mundo, nas mensagens de morte e no seu ovo |
| `showName`      | não | booleano | `false` | Mostra o nome sem precisar olhar para ela |
| `egg`           | não | booleano ou objeto | `true`  | Um ovo de spawn, colorido como o ovo da entidade que ela copia. `{ "primary": "AABBCC", "secondary": "112233" }` escolhe cores próprias, `false` omite o ovo |
| `becomes`       | não | lista | nenhum | Outras variantes em que esta pode se transformar ao nascer, por peso. Veja abaixo |
| `baby`          | não | booleano ou 0.0 a 1.0 | `false` | Com que frequência nasce filhote, e ele continua assim. `true` é sempre, um número é essa fração deles |
| `keepsBaseBaby` | não | booleano | `false` | Se o sorteio de filhote da própria base também roda. Sem isso, uma variante baseada em zumbi só nasce filhote como `baby` determina, sem filho do `zombieBabyChance` do Forge e sem galinha montada |
| `profession`    | não | `namespace:name` | aleatória | Para um aldeão, a profissão que exerce |
| `career`        | não | int | aleatória | Qual carreira dentro dessa profissão, a partir de 1 |
| `requires`      | não | lista de ids de mods ou namespaces de pacotes | nenhum | A variante é omitida, a menos que todos estejam presentes |

Uma variante é uma classe própria, então um mundo que contém uma depende do pacote que a criou, do mesmo modo que depende de um mod. Remova o arquivo e as criaturas desse mundo vão junto.

**Um ovo ou spawner que dá uma mistura.** Uma variante é uma classe própria, então, sozinha, sempre gera exatamente o que diz. `becomes` é como um pacote rompe isso: uma lista de variantes em que esta pode se transformar ao nascer, cada uma com um peso, decidido por criatura.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Citar a si mesma é como ela continua como está, e os pesos são as probabilidades. Coloque isso em `mypack:walker` e um ovo e uma entrada de spawn dão em maioria walkers com um pequeno de vez em quando, como um ovo de zumbi dá o filhote ocasional. Acontece quando a criatura entra no mundo, então vale para ovos, `/summon` e spawn natural igualmente, e a criatura que chega é uma de verdade da variante escolhida, com tudo o que essa variante diz. Um spawner é mais restrito: ele sorteia apenas entre as variantes da mesma mob base e da mesma equipe da variante em que está configurado, então um spawner de zumbi dá os zumbis dessa equipe e seus filhotes, e nunca uma criatura de outro tipo ou de outra equipe, como um spawner de zumbi do vanilla continua sendo um spawner de zumbi. Uma variante alcançada assim não se transforma de novo, então duas variantes podem citar uma à outra sem entrar em ciclo.

**Onde `baby` se encaixa.** O jogo não tem um zumbi filhote próprio: existe um único zumbi que sorteia se é criança ao nascer. `baby` diz com que frequência, então `"baby": 0.05` é o hábito do vanilla e `"baby": true` é sempre. Uma variante não recebe por cima o sorteio do próprio zumbi, então não aparece criança nem galinha montada que `baby` não tenha pedido; `keepsBaseBaby` devolve esse sorteio. Os dois são duas formas de chegar ao mesmo ponto, e qual usar depende da diferença que você quer: `baby` sozinho dá uma variante que às vezes é filhote, `becomes` dá várias variantes que diferem no que você quiser, e uma mistura dos dois é válida.

### Aparência

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `texture`    | não | `namespace:textures/entity/<file>.png` | nenhum       | Uma skin própria, com o mesmo layout da entidade que ela copia |
| `tint`       | não | cor hexadecimal | nenhum       | Colore a entidade ao ser desenhada |
| `tintParts`  | não | lista de `body`, `armor`, `held` | `["body"]` | Quais partes a coloração alcança |
| `scale`      | não | float | `1.0`      | O tamanho com que é desenhada, e o tamanho da sua hitbox |
| `angryScale` | não | float | `scale`    | O tamanho a que ela incha enquanto tem algo a atacar, e por três segundos depois de perder o alvo |
| `width`      | não | float | o da base | A largura da hitbox, antes de `scale` ser aplicado |
| `height`     | não | float | o da base | A altura da hitbox, antes de `scale` ser aplicado |
| `glowing`    | não | booleano | `false`    | Contornada através das paredes |
| `bright`     | não | booleano | `false`    | Desenhada com luz total onde quer que esteja, como sob o sol do meio-dia, de modo que nunca é escurecida pela noite, pela sombra ou por uma caverna |
| `invisible`  | não | booleano | `false`    | Não é desenhada, embora o equipamento ainda seja |
| `hideArmor`  | não | booleano | `false`    | Veste a armadura sem que ela seja desenhada |
| `hideHeld`   | não | booleano | `false`    | O mesmo para o que ela estiver segurando |
| `leftHanded` | não | booleano | `false`    | Segura a arma na outra mão |

`scale` muda o modelo e a hitbox nos dois lados, então o que você vê é o que você pode acertar. Uma criatura que muda o próprio tamanho, um animal crescendo ou um zumbi que é criança, é escalada em torno do tamanho que ela escolheu, para que os dois não briguem. `angryScale` a incha enquanto ela tem um alvo e a devolve a `scale` quando o perde. Como o cliente nunca é informado do que a criatura caça, a flag de corrida leva essa notícia: ela é definida em uma variante que usa `angryScale` e em nada mais, então um mod que leia a corrida nas suas variantes verá a mudança. Crescer sob um teto baixo é possível, como acontece com um slime crescendo, então mantenha a diferença modesta.

Uma `texture` é vinculada no lugar da que a entidade normalmente usaria, seja qual for o renderizador que ela herda, então funciona para entidades de mods tanto quanto para as do vanilla. Ela precisa corresponder ao modelo em que é desenhada, já que o modelo é o da entidade base, uma skin, não uma forma nova. As camadas mantêm suas próprias texturas, então a armadura ainda parece armadura em um zumbi com skin trocada.

A armadura só é desenhada em uma entidade cujo renderizador tenha uma camada de armadura, o que nesta versão significa os mobs humanoides e os aldeões. Uma variante de vaca ou de aranha pode carregar armadura e recebe a proteção, mas nada a desenha, então `armor` em `attributes` costuma ser o jeito mais limpo de tornar tal criatura resistente. `hideArmor` serve ao outro caso: um humanoide que deve manter a armadura nos seus slots, pela proteção ou para um mod que os leia, sem que ela seja vista.

### Seus sons

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `sounds`      | não | objeto | o da base | `ambient`, `hurt` e `death`, cada um um evento de som registrado. Mais três para os quais ela não tem som base: `target` toca uma vez cada vez que ela adquire um alvo, e `explode` é como soa a sua explosão no lugar da do jogo, quer ela se exploda com `explodes` quer lance TNT com `throws`. `throw` toca quando ela lança qualquer coisa com `throws`, no lugar do arremesso de bola de neve, ou do chiado do pavio para TNT. `targetVaries` desloca cada toque de `target` para cima ou para baixo em uma quantidade aleatória dentro desse número de semitons, então `3` varia um quarto de oitava para cada lado; `0` o toca como está |
| `soundVolume` | não | número | `1.0`      | O volume desses sons |
| `soundPitch`  | não | número | `1.0`      | A altura com que tocam. Abaixo de 1 é mais grave, acima de 1 é mais agudo |
| `silent`      | não | booleano | `false`    | Não emite som |

### Vida, dano e efeitos

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `attributes`        | não | objeto | nenhum | `maxHealth`, `movementSpeed`, `attackDamage`, `attackSpeed`, `knockbackResistance`, `followRange`, `armor`. Um atributo que a entidade normalmente não tem é concedido a ela. `attackSpeed` é golpes por segundo para um lutador corpo a corpo, `1` como o jogo tem, então `2` golpeia com o dobro da frequência. Qualquer nome que a base já carregue também funciona, `zombie.spawnReinforcements`, `horse.jumpStrength`. `attackDamage` em uma base que atira é o dano das suas flechas |
| `absorption`        | não | float | `0` | Corações extras além da vida |
| `invulnerable`      | não | booleano | `false` | Não sofre dano de nada além do vazio e do criativo |
| `fireproof`         | não | booleano | `false` | Nunca pega fogo, então nunca é ferida por fogo ou lava e nunca queima à luz do dia |
| `immuneTo`          | não | lista de tipos de dano | nenhum | Dano que ela ignora: `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `anvil`, `inWall` e os demais |
| `fallDamage`        | não | float | `1.0` | Multiplica o dano que uma queda causa. `0` elimina o dano de queda |
| `hurtResistance`    | não | int, ticks | o da base, `20` | Quanto tempo depois de um golpe ela não pode ser ferida de novo. Golpes mais rápidos que a metade disso são perdidos, então um atacante rápido pede um alvo com menos |
| `effects`           | não | lista de objetos | nenhum | Efeitos que ela sempre tem: `{ "potion": "minecraft:strength", "amplifier": 1 }` |
| `ignoresEffects`    | não | lista de ids de poção, ou `all` | nenhum | Efeitos que nunca pegam nela, quem ou o que quer que os aplique: um golpe, uma poção arremessável, um sinalizador, uma flecha, `/effect`. `all` recusa todo efeito, então uma variante começa como uma folha em branco. Os seus próprios `effects` continuam sendo aplicados |
| `creatureAttribute` | não | `undefined`, `undead`, `arthropod` ou `illager` | o da base | Como ela é classificada, para que Julgamento e poções de cura a tratem de acordo |

### Movimento

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `jumpMultiplier` | não | float | `1.0`      | Quanto mais alto ela pula que a entidade que copia |
| `stepHeight`     | não | float, blocos | o da base | Que altura de degrau ela sobe sem pular. A maioria das criaturas sobe `0.6`, um zumbi `1.0` |
| `maxFallHeight`  | não | int | o da base | Quanto ela se deixa cair ao traçar caminho |
| `climbs`         | não | booleano | o da base | Escala paredes como uma aranha e faz caminho por elas; `false` prende uma aranha ao chão |
| `teleports`      | não | booleano | `true`     | Se um enderman ou um shulker pode se teletransportar. Desativado, fica onde está, mesmo à luz do dia e na água |
| `walks`          | não | booleano | `false`    | Um coelho anda como os outros animais em vez de se mover aos pulos. Só um coelho lê isto |
| `pathPriorities` | não | objeto | nenhum | Por onde ela andará, como `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` e os demais, cada um um número em que o negativo significa nunca |
| `leashable`      | não | booleano | `false`    | Pode ser conduzida com uma guia, mesmo que a entidade que ela copia nunca pudesse |
| `steerable`      | não | booleano | `false`    | Pode ser conduzida enquanto montada |
| `noAI`           | não | booleano | `false`    | Fica onde é colocada e não faz nada |

### Água

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `breathesUnderwater` | não | booleano | `false` | Nunca se afoga e afunda para andar pelo fundo em vez de nadar até a superfície. Ela ainda se orienta pelo chão, então água funda de que não consiga sair a prenderá |
| `swims`              | não | booleano | `false` | Move-se pela água como uma lula ou um guardião e nunca se afoga. Ela traça caminho pela água em vez de pelo chão, então pertence à água e fica encalhada fora dela |
| `amphibious`         | não | booleano | `false` | Anda em terra e nada de verdade na água, mudando a forma como traça caminho ao entrar e sair da água. Nunca se afoga. O que ela perseguia é esquecido na beira da água, então hesita por um instante cada vez que a atravessa |
| `waterSlowdown`      | não | float | `0.8`   | Quanto a água a desacelera. Mais alto é mais rápido |

### Combate

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `hostile`       | não | booleano | `false`           | Ataca o que consegue alcançar e revida quando ferida. Uma variante hostil conta como monstro para o jogo, seja qual for a base, então o limite de monstros a retém e o modo pacífico a remove, e ela abandona as tarefas de animal que a base trazia: reprodução, ser atraída, seguir um pai, um dono ou a própria espécie, sentar |
| `passive`       | não | booleano | `false`           | Impede que ataque qualquer coisa, por mais que normalmente se comporte |
| `targets`       | não | lista de nomes de entidades | o jogador | O que ela procura enquanto hostil. `minecraft:player` é entendido, embora o jogador não seja uma entidade registrada |
| `attackReach`   | não | float, blocos | seu tamanho | A que distância chega um golpe corpo a corpo. O jogo alcança o dobro da largura, e é por isso que uma criatura ampliada acerta de mais longe; isto define o alcance diretamente |
| `knockback`     | não | float | o da base, `0.4` | Com que força seus golpes empurram. `0` não empurra nada |
| `hitEffects`    | não | booleano | `true`            | Se ela aplica ao que acerta o efeito que a entidade que copia aplicaria: o wither de um esqueleto wither, o veneno de uma aranha das cavernas, a fome de um husk. Desativado, ela acerta apenas com dano |
| `hitFire`       | não | booleano | `true`            | Se ela incendeia o que acerta quando a entidade que copia o faria: um zumbi em chamas, a bola de fogo de um blaze. Desativado, nada do que ela faz inicia fogo no alvo |
| `threatLeast`   | não | int | `0`               | A faixa de ameaça mais baixa em que um jogador ou outro portador a até 128 blocos precisa estar para que a variante apareça naturalmente. `0` gera como de costume |
| `threatHostile` | não | int | `0`               | A faixa de ameaça mais baixa em que um jogador precisa estar para que a variante o ataque por conta própria. Abaixo dela a variante é dócil com esse jogador, embora ainda revide quando atingida. `0` ataca como de costume |

`hostile` também retira o comportamento que fazia a criatura fugir: um animal que evitava jogadores ou entrava em pânico quando ferido não faz nenhum dos dois quando hostil, já que do contrário fugiria da coisa que deve atacar. Exige uma entidade que ande pelo chão, pois usa o mesmo comportamento de ataque que o vanilla dá aos seus próprios mobs. Uma base voadora ou nadadora é registrada no log e deixada em paz. `passive` funciona de forma mais ampla, mas só alcança comportamento construído como o vanilla o constrói; um mod cuja hostilidade está escrita no próprio código de tick ou de dano não é algo que um pacote possa dissuadir.

### Equipamento, drops e experiência

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `equipment`          | não | objeto | nenhum       | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, cada um um nome de item |
| `dropChance`         | não | 0 a 1 | `0`        | A probabilidade de cada peça de equipamento ser largada |
| `picksUpLoot`        | não | booleano | `false`    | Pega o que encontra pelo caminho |
| `lootTable`          | não | `namespace:entities/<name>` | o da base | O que ela solta. Sem isto, solta o que a entidade que copia solta |
| `experience`         | não | int | o da base | Quanta experiência ela solta |
| `collectsExperience` | não | booleano | `false`    | Coleta experiência como um jogador: orbes a até oito blocos flutuam até ela e são recolhidos ao toque, o Remendo no equipamento é reparado primeiro, e os pontos formam níveis na curva do próprio jogador, mantidos no mob através de um save. O que ela mata solta sua experiência como se um jogador tivesse feito o abate, um bloco que sua tarefa `digs` quebra solta a experiência do próprio bloco, e um sorteio de experiência de `block_drops` também cai para ela. Ao morrer, solta sete por nível até cem, a menos que `keepInventory` esteja ativado. Objetivos com o critério `xp` ou `level` carregam seu total e nível em uma linha nomeada pelo seu UUID, para que uma função os leia com `score_<objective>_min`. Ela gasta seus níveis em trabalho de bigorna como um jogador, veja [Trabalho na bigorna](#trabalho-na-bigorna) |

Uma variante solta o que a entidade que ela copia solta, porque a tabela de saque está fixada no código da própria entidade em vez de ser buscada por nome. `lootTable` a aponta para uma tabela sua, que você então fornece em `loot_tables/entities/<name>.json` como qualquer outra.

### Comportamentos especiais

*variantes de entidades*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `throws`         | não | booleano | `false`         | Lança o que segura contra o alvo à distância, e, se for TNT, acende-o e recua. Exige `hostile` |
| `throwAmmo`      | não | int | nenhum          | Quantos ela tem para lançar. Omitido, ela nunca fica sem |
| `throwReload`    | não | int, segundos | `explosionFuse` | Por quanto tempo a mão fica vazia antes de sacar outro |
| `throwRetreat`   | não | int, segundos | `explosionFuse` | Por quanto tempo ela se mantém afastada depois de um arremesso antes de voltar |
| `throwPower`     | não | float | `1.0`           | Com que força lança. Dobrá-la dobra aproximadamente o alcance |
| `throwArc`       | não | float | `0.35`          | Que altura tem o arremesso em arco. Mais alto fica mais tempo no ar, perto de zero é um arremesso reto, abaixo de zero lança para baixo |
| `throwReturns`   | não | booleano | `false`         | O que ela lança voa como um tridente: acerta com o `attackDamage` da variante, ou 8 em uma base sem ele, e depois volta voando para a mão como a Lealdade traz de volta um tridente. Nunca se esgota e é mirado no alvo como um esqueleto mira, mais rápido com `throwPower` e com menos dispersão em dificuldades mais altas, e quem lança fica firme enquanto ele voa, então `throwAmmo`, `throwReload`, `throwRetreat` e `throwArc` não se aplicam. TNT é lançado como sempre |
| `explodes`       | não | booleano | `false`         | Explode ao lado do alvo, como um creeper. Exige `hostile` |
| `explosionPower` | não | número | `3.0`           | O tamanho da explosão. Um creeper é 3, TNT é 4. Em uma base de creeper é também a explosão do próprio creeper, e em um ghast a da bola de fogo |
| `explosionFuse`  | não | int, ticks | `30`            | Por quanto tempo chia antes de explodir. Em uma base de creeper é também o pavio do próprio creeper |
| `explosionFire`  | não | booleano | `false`         | Deixa fogo para trás |
| `charges`        | não | booleano | `false`         | Investe contra o alvo à distância e acerta com um grande knockback ao contato, como um ravager, e depois descansa antes da próxima investida. Exige `hostile` |
| `pounces`        | não | booleano | `false`         | Agacha e depois salta sobre o alvo em arco e golpeia ao pousar, como uma raposa. Exige `hostile` |
| `sniffs`         | não | int, blocos | `0`             | Ouve jogadores se movendo a essa distância em blocos, com ou sem paredes, e vai até onde os ouviu; um jogador agachado ou parado não é ouvido, e um que ela então vê se torna seu alvo. `0` não escuta. Exige `hostile` |
| `fleesWhenHurt`  | não | 0.0 a 1.0 | `0`             | Interrompe a luta e foge de quem enfrenta enquanto sua vida está abaixo dessa fração, e volta quando passa dela. `0` nunca foge. Exige `hostile` |
| `sleepsByDay`    | não | booleano | `false`         | Procura sombra de dia e fica parada ali até a noite ou até algo atacá-la. Enquanto descansa, fica deitada de lado |
| `home`           | não | int, blocos | `0`             | Fica a essa quantidade de blocos em torno do ponto onde primeiro esteve, vagando dentro dele e voltando quando se afasta. `0` vaga livremente |
| `patrols`        | não | booleano | `false`         | Percorre a terra em longos trechos com outros da sua espécie seguindo um líder, como uma patrulha de pillagers. Um grupo que nasce junto escolhe um líder; os demais ficam a poucos blocos dele, e quando o líder adquire um alvo, todos adquirem. Um seguidor que perde o líder assume a liderança. Exige `hostile` |
| `swoops`         | não | booleano | `false`         | Circula acima do alvo e mergulha através dele, golpeando na passada, como um phantom. A variante recebe um auxiliar de voo, então voa enquanto caça e pousa no chão quando ociosa; exige uma base que seja uma criatura, um papagaio serve, e um morcego não. Exige `hostile` |
| `gusts`          | não | booleano | `false`         | Prepara e libera uma rajada de vento contra o alvo à distância, lançando para trás e para cima tudo perto dele, como a carga de vento de uma breeze. Exige `hostile` |
| `gustPower`      | não | float | `1.5`           | Com que força uma rajada lança. Um golpe de mob é 0.4, um encantamento forte de repulsão cerca de 1 |
| `digs`           | não | booleano | `false`         | Cava através do que estiver entre ela e o alvo, com a ferramenta na mão: uma pá em terra, areia e cascalho, uma picareta em pedra, um machado em madeira, e só o que o material dessa ferramenta consegue quebrar, então uma picareta de madeira nunca abre minério de ferro e nada abre obsidiana sem diamante. Um bloco leva o tempo que levaria para um jogador com aquela ferramenta, solta o que soltaria e desgasta a ferramenta. Dê a ferramenta com `equipment`; de mãos vazias ela não cava nada, e não cava nada onde `mobGriefing` está desativado. Ela nunca procura um caminho alternativo: com um alvo, caminha direto até ele e cava o que estiver no caminho, e onde a ferramenta não abre o bloco ela fica parada empurrando. Exige `hostile`. Ela adquire seus alvos sem precisar vê-los, já que aquilo em direção ao que cava está, por natureza, atrás de algo |

**Lançar em vez de investir.** `explodes` manda uma criatura para dentro para se explodir. `throws` é o outro temperamento: ela mantém distância, lança o que estiver na mão principal contra o que está enfrentando e, se for TNT, acende-o, lança-o e recua enquanto ele queima.

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

Lançar esvazia a mão, porque ela lançou a coisa. Em seguida mantém-se afastada por `throwRetreat`, saca outro após `throwReload` e volta ao alvo: um ciclo de arremesso, recuo, recarga e aproximação. Dê um `throwAmmo` e esse ciclo termina quando a contagem se esgota, com a mão ficando vazia de vez e o ataque comum assumindo. Omita `throwAmmo` e ela nunca fica sem.

A contagem é gravada na criatura, então não se reabastece só porque um chunk foi descarregado e carregado de novo. Tudo que não é TNT voa como item e cai, o que torna um sapador lançando pedras ou carne podre tão fácil quanto um lançando explosivos.

`explosionFuse` continua sendo o pavio do TNT lançado e vale no lugar de qualquer um dos temporizadores que você omitir, então uma variante escrita antes destas chaves se comporta exatamente como antes.

Como o arremesso em si voa é `throwPower` e `throwArc`. O primeiro é um multiplicador do empurrão, e como o empurrão já cresce com a distância, aumentá-lo alonga o alcance sem mudar quanto tempo o arremesso fica no ar. O segundo é a elevação, e ele muda a forma: alto, e a carga é lançada por cima de uma parede e leva seu tempo; perto de zero, e é arremessada reta e cai quase de imediato; abaixo de zero, e é lançada para baixo contra algo abaixo. Ambos deixam o pavio em paz, então uma carga em arco e uma reta explodem o mesmo número de segundos depois de deixar a mão, e é isso que decide se uma estoura no alto ou cai primeiro e espera. De que distância ela lança é o seu `followRange`, e ela se aproxima como de costume quando você está a menos de três blocos, então é perigosa à distância e comum na sua cara.

### Tarefas

*variantes de entidades*

**Qualquer tarefa que o jogo tem.** As chaves acima são comportamentos próprios do RDPL. `tasks` vai além delas até toda tarefa que o próprio vanilla usa, em qualquer base: uma entrada é um objeto que cita a `task` e sua `priority`, mais o que essa tarefa lê; um nome depois de um `-` remove toda tarefa desse tipo que a base trazia. As prioridades vão de 0 em primeiro, e o vanilla mantém as suas entre 1 e 8, então uma tarefa em 0 vence tudo o que a base faz e uma em 9 só roda quando nada mais quer.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `tasks` | não | lista | nenhum | Qualquer tarefa que o jogo tem, adicionada à variante pelo nome com a prioridade que você escolher, ou retirada do que a base trazia. A lista abaixo |

A lista é aplicada depois que `hostile`, `passive` e os comportamentos acima fizeram o seu trabalho, então tem a última palavra. Tarefas que movem o corpo se excluem mutuamente: uma só roda quando nada à sua frente em prioridade está movendo a criatura, e o ataque que um monstro traz fica em 2, então um salto ou uma fuga em um zumbi precisa de prioridade 1 ou nunca tem a sua vez; a aranha e o lobo mantêm o salto à frente do ataque pelo mesmo motivo. Uma tarefa que a base já executa é adicionada uma segunda vez em vez de substituída; remova primeiro a antiga. Algumas tarefas só fazem sentido em uma base que tenha aquilo que elas acionam: uma luta com arco exige uma base que atire, sentar exige uma base que possa ser domada, e comerciar exige um aldeão. Peça uma em uma base que não a comporta e o log diz de qual base ela precisa, e a variante fica sem ela.

| Chave | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `priority`  | int | obrigatória | Onde fica entre as tarefas da base. Menor roda primeiro |
| `speed`     | número | o habitual da tarefa | A velocidade com que se move enquanto a tarefa roda, como multiplicador da velocidade de caminhada |
| `nearSpeed` | número | `1.2` | `avoidEntity`: o multiplicador quando aquilo que ela evita está perto |
| `distance`  | número, blocos | o habitual da tarefa | A que distância ela olha, segue, atira ou se mantém afastada |
| `near`      | número, blocos | o habitual da tarefa | `follow`, `followOwner`, `followOwnerFlying`: a que distância chega antes de parar |
| `chance`    | número | o habitual da tarefa | `wander`: um sorteio a cada tantos ticks; `wanderAvoidWater`: a probabilidade, de 0 a 1, de sair do abrigo; `watchClosest`, `watchClosest2`: a probabilidade, de 0 a 1, de olhar a cada tick |
| `leap`      | número | `0.4` | `leapAtTarget`: a altura do salto |
| `cooldown`  | int, ticks | `20` | `attackRanged`, `attackRangedBow`: ticks entre os tiros |
| `entity`    | nome de entidade | nenhum | Qual entidade a tarefa procura, evita, observa ou com a qual cruza. `minecraft:player` é entendido |
| `items`     | lista de nomes de itens | nenhum | `tempt`: o que um jogador estende |
| `sight`     | booleano | `true` | `nearestAttackableTarget`, `targetNonTamed`: só o que ela consegue ver |
| `nearby`    | booleano | `false` | `nearestAttackableTarget`: só o que está dentro do seu próprio alcance de seguimento |
| `help`      | booleano | `false` | `hurtByTarget`: outros da sua espécie por perto se juntam |
| `memory`    | booleano | `false` | `attackMelee`, `zombieAttack`: continua atrás de um alvo que perdeu de vista |
| `close`     | booleano | `false` | `openDoor`: fecha a porta atrás de si |
| `nocturnal` | booleano | `false` | `moveThroughVillage`: só à noite |
| `scared`    | booleano | `false` | `tempt`: um jogador que se move rápido demais quebra o encanto |

A coluna `Lista` diz onde a tarefa vive. `tasks` é o que a criatura faz; `targets` é como ela escolhe o que atacar, e uma tarefa de alvo sem um ataque correspondente não faz nada sozinha.

| Tarefa | Exige | Lista | Lê | O que faz |
| --- | --- | --- | --- | --- |
| `attackMelee`             | uma criatura que anda | `tasks`   | `speed`, `memory`                          | Anda até o alvo e o golpeia |
| `attackRanged`            | uma base que atira | `tasks`   | `speed`, `cooldown`, `distance`            | Mantém a distância e atira o que a base atira |
| `attackRangedBow`         | um monstro que atira | `tasks`   | `speed`, `cooldown`, `distance`            | A luta com arco do esqueleto: se desloca de lado, puxa e solta |
| `avoidEntity`             | uma criatura que anda | `tasks`   | `entity`, `distance`, `speed`, `nearSpeed` | Foge da entidade citada quando ela chega a menos de `distance` |
| `beg`                     | um lobo | `tasks`   | `distance`                                 | Pede comida a um jogador que a estende |
| `breakDoor`               | qualquer base | `tasks`   |                                            | Quebra as portas de madeira no caminho, em dificuldade difícil |
| `creeperSwell`            | um creeper | `tasks`   |                                            | Chia e explode ao lado do alvo |
| `defendVillage`           | um golem de ferro | `targets` |                                            | Ataca quem agrediu um aldeão |
| `eatGrass`                | qualquer base | `tasks`   |                                            | Come grama, como uma ovelha |
| `findEntityNearest`       | qualquer base | `targets` | `entity`                                   | Mira a mais próxima da entidade citada, como um slime ou um ghast faz |
| `findEntityNearestPlayer` | qualquer base | `targets` |                                            | Mira o jogador mais próximo que consegue alcançar |
| `fleeSun`                 | uma criatura que anda | `tasks`   | `speed`                                    | Procura sombra quando o sol a atinge |
| `follow`                  | qualquer base | `tasks`   | `speed`, `near`, `distance`                | Segue outros da sua própria espécie |
| `followGolem`             | um aldeão | `tasks`   |                                            | Segue um golem de ferro que estende uma papoula |
| `followOwner`             | uma base domável | `tasks`   | `speed`, `near`, `distance`                | Segue o dono e se teletransporta até ele quando fica muito para trás |
| `followOwnerFlying`       | uma base domável | `tasks`   | `speed`, `near`, `distance`                | O mesmo, voando |
| `followParent`            | um animal | `tasks`   | `speed`                                    | Um filhote fica perto de um adulto da sua espécie |
| `harvestFarmland`         | um aldeão | `tasks`   | `speed`                                    | Colhe plantações maduras e as replanta |
| `hurtByTarget`            | uma criatura que anda | `targets` | `help`                                     | Revida contra o que a atingiu |
| `landOnOwnersShoulder`    | um papagaio | `tasks`   |                                            | Pousa no ombro do dono |
| `leapAtTarget`            | qualquer base | `tasks`   | `leap`                                     | Salta sobre o alvo de perto |
| `llamaFollowCaravan`      | uma lhama | `tasks`   | `speed`                                    | Entra na fila atrás de uma lhama conduzida |
| `lookAtTradePlayer`       | um aldeão | `tasks`   |                                            | Encara o jogador com quem está negociando |
| `lookAtVillager`          | um golem de ferro | `tasks`   |                                            | Olha para os aldeões |
| `lookIdle`                | qualquer base | `tasks`   |                                            | Olha ao redor de vez em quando |
| `mate`                    | um animal | `tasks`   | `speed`, `entity`                          | Se reproduz quando apaixonado, com a própria espécie ou com a `entity` citada |
| `moveIndoors`             | uma criatura que anda | `tasks`   |                                            | Entra em uma casa da vila ao anoitecer |
| `moveThroughVillage`      | uma criatura que anda | `tasks`   | `speed`, `nocturnal`                       | Anda pelos caminhos da vila de porta em porta |
| `moveTowardsRestriction`  | uma criatura que anda | `tasks`   | `speed`                                    | Volta em direção ao seu ponto de origem quando se afasta |
| `moveTowardsTarget`       | uma criatura que anda | `tasks`   | `speed`, `distance`                        | Se aproxima de um alvo que está longe |
| `nearestAttackableTarget` | uma criatura que anda | `targets` | `entity`, `sight`, `nearby`                | Mira o mais próximo da entidade citada |
| `ocelotAttack`            | qualquer base | `tasks`   |                                            | A espreita e o bote do gato |
| `ocelotSit`               | um gato selvagem | `tasks`   | `speed`                                    | Senta em baús, camas e fornalhas acesas |
| `openDoor`                | qualquer base | `tasks`   | `close`                                    | Abre as portas de madeira por onde passa |
| `ownerHurtByTarget`       | uma base domável | `targets` |                                            | Ataca o que agrediu o dono |
| `ownerHurtTarget`         | uma base domável | `targets` |                                            | Ataca o que o dono agrediu |
| `panic`                   | uma criatura que anda | `tasks`   | `speed`                                    | Corre quando ferida ou em chamas |
| `play`                    | um aldeão | `tasks`   | `speed`                                    | Crianças brincam de pega-pega umas com as outras |
| `restrictOpenDoor`        | uma criatura que anda | `tasks`   |                                            | Fica dentro das portas da vila à noite |
| `restrictSun`             | uma criatura que anda | `tasks`   |                                            | Fica na sombra de dia |
| `runAroundLikeCrazy`      | um cavalo, burro, mula ou lhama | `tasks`   | `speed`                                    | Derruba um cavaleiro em quem ainda não confia |
| `sit`                     | uma base domável | `tasks`   |                                            | Senta quando mandada |
| `skeletonRiders`          | um cavalo esqueleto | `tasks`   |                                            | Chama cavaleiros esqueleto quando um jogador se aproxima, o cavalo armadilha |
| `swimming`                | qualquer base | `tasks`   |                                            | Mantém a cabeça acima da água |
| `targetNonTamed`          | uma base domável | `targets` | `entity`, `sight`                          | Mira a entidade citada enquanto ainda não foi domada |
| `tempt`                   | uma criatura que anda | `tasks`   | `items`, `speed`, `scared`                 | Segue um jogador que estende um dos `items` |
| `tradePlayer`             | um aldeão | `tasks`   |                                            | Fica parado durante a negociação |
| `villagerInteract`        | um aldeão | `tasks`   |                                            | Conversa com outros aldeões |
| `villagerMate`            | um aldeão | `tasks`   |                                            | Se reproduz quando a vila tem espaço |
| `wander`                  | uma criatura que anda | `tasks`   | `speed`, `chance`                          | Vaga por aí |
| `wanderAvoidWater`        | uma criatura que anda | `tasks`   | `speed`, `chance`                          | Vaga, mantendo-se fora da água |
| `wanderAvoidWaterFlying`  | uma criatura que anda | `tasks`   | `speed`                                    | Vaga pelo ar e pousa em árvores |
| `watchClosest`            | qualquer base | `tasks`   | `entity`, `distance`, `chance`             | Olha para a mais próxima da entidade citada, o jogador se nenhuma for citada |
| `watchClosest2`           | qualquer base | `tasks`   | `entity`, `distance`, `chance`             | O mesmo, mantido enquanto outra tarefa roda |
| `zombieAttack`            | um zumbi | `tasks`   | `speed`, `memory`                          | O ataque do zumbi, com os braços erguidos |

### Spawn e despawn

*variantes de entidades*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `spawns` | não | lista de objetos | nenhum | `creatureType`, `weight`, `min` e `max`, o mesmo formato que um bioma usa |
| `biomes` | não | lista de nomes de biomas | todos os biomas | Onde esses spawns são adicionados |
| `biomeTypes` | não | lista de tipos do dicionário | nenhum | O mesmo, por tipo |
| `ignoresSpawnRules` | não | booleano | `false` | Surge onde for colocada, ignorando as regras herdadas |
| `despawns` | não | booleano | `true` | Desativado, ela permanece mesmo quando normalmente seria removida |
| `despawnAfter` | não | int, segundos | nenhum | Some em silêncio depois de passar esse tempo no mundo, por mais longe que esteja qualquer jogador |
| `persistent` | não | booleano | `false` | Nunca sofre despawn |

**Uma criatura com prazo de validade.** `despawnAfter` conta em segundos a partir do momento em que a criatura entra no mundo pela primeira vez e a remove em silêncio quando o tempo acaba: sem morte, sem drops, sem som, exatamente como se ela tivesse se afastado e sido removida. O relógio é gravado na própria criatura, então continua correndo ao salvar e recarregar, em vez de recomeçar cada vez que um chunk volta.

É um mecanismo à parte, não um ajuste das regras que `despawns` e `persistent` governam. Esses dois decidem se o jogo pode remover uma criatura por estar longe de todos; este é uma promessa de que ela some em um momento determinado, aconteça o que acontecer. Uma criatura pode ser `persistent` e ainda ter prazo de validade, que é o que você quer para algo invocado para uma luta ou um evento e que não deve sobreviver a ele.

O relógio corre no tempo do mundo, então para quando ninguém está jogando e não conta os minutos em que um chunk ficou descarregado.

### Rede

*variantes de entidades*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `trackingRange` | não | int | `80` | A que distância o cliente é informado sobre ela |
| `trackVelocity` | não | booleano | `true` | Envia a velocidade além da posição. Desativado, economiza tráfego em coisas que quase não se movem |
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

`storage` dá a uma variante de qualquer entidade slots de itens, um tanque de fluido e um buffer de energia, cada um somente quando o seu objeto está escrito. Cada um é oferecido como a capability de item, fluido ou energia do Forge da entidade, de modo que tudo o que move itens, fluido ou energia para dentro de uma entidade o alcança. Quando a entidade base já responde a essa capability, como um mob faz para as mãos e a armadura e como um cavalo ou uma vagoneta com baú faz para o inventário, o armazenamento do pacote responde no lugar dela, em todos os lados. O jogador abre a tela agachando e clicando com o botão direito na entidade. O conteúdo é salvo junto com a entidade.

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `items` | não | objeto | nenhum | Dá à entidade slots de itens. A área tem três fileiras de 9: cada barra de fluido ou energia ocupa uma fileira e os slots ficam com o resto, então 1x9 com as duas barras, 2x9 com uma e 3x9 com nenhuma. Sem `items`, só as barras aparecem |
| `fluid` | não | objeto | nenhum | Um tanque de fluido |
| `energy` | não | objeto | nenhum | Um buffer de Forge Energy |
| `dropsOnDeath` | não | booleano | `true` | Os itens armazenados caem como itens soltos onde a entidade morre. `false` os perde. Fluido e energia se perdem de qualquer forma |
| `runsDry` | não | `stops`, `slows` ou `hurts` | `stops` | O que acontece enquanto ela não consegue pagar um segundo inteiro de `use`. `stops`: ela deixa de pensar e fica parada onde está, com tarefas, alvos e comportamentos todos ociosos até ser reabastecida, embora ainda caia e possa ser empurrada. `slows`: ela se move à metade da velocidade. `hurts`: ela sofre 1 de dano por segundo, como de fome, então `immuneTo` com `starve` a poupa |

**Funcionando com o que carrega.** Um `use` no tanque ou no buffer é um custo de funcionamento: uma vez por segundo a entidade retira essa quantidade, além de `transfer`, `buckets` e dos filtros. Quando um dos dois tem menos que um segundo inteiro de `use`, a entidade ficou sem recursos: nada mais é retirado, `runsDry` decide o que acontece, e ela volta ao normal no instante em que é reabastecida. Só uma criatura gasta; em uma base que não é viva, como uma vagoneta, `use` não faz nada.

`items`:

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `filter` | não | lista de entradas de filtro | nenhum | O que os slots aceitam. Sem ele, aceitam qualquer coisa |

`fluid`:

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `capacity` | sim | int, mB | nenhum | Quanto o tanque comporta |
| `filter` | não | lista de entradas de filtro | nenhum | Quais fluidos o tanque aceita. Sem ele, aceita qualquer um |
| `buckets` | não | booleano | `false` | Um clique direito com um balde ou outro recipiente de fluido, sem agachar, esvazia-o no tanque ou o enche a partir do tanque. Um clique que não move fluido fica para a entidade |
| `use` | não | int, mB por segundo | `0` | Quanto a entidade consome do tanque a cada segundo em que está viva. `0` não custa nada |

`energy`:

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `capacity` | sim | int, FE | nenhum | Quanta energia comporta |
| `transfer` | não | int, FE | sem limite | O máximo de energia movido, para dentro ou para fora, em uma operação |
| `use` | não | int, FE por segundo | `0` | Quanta energia a entidade consome a cada segundo em que está viva. `0` não custa nada |

Uma entrada de filtro. A primeira entrada que corresponde decide, e tudo o que nenhuma entrada cobre é recusado:

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `item` | um dos três | nome de item | nenhum | Um item, como `namespace:name` ou `namespace:name:meta` |
| `oreDict` | um dos três | nome de minério | nenhum | Todos os itens sob esse nome do dicionário de minérios |
| `fluid` | um dos três | nome de fluido | nenhum | Um fluido pelo seu nome registrado, como `water`. Lido somente por um filtro de fluido |
| `max` | não | int | `0` | O máximo que pode ser mantido de uma vez, contado em todos os slots, ou em mB para um fluido. `0` é sem limite |

### Foguetes do Galacticraft

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

O item que o coloca, em `<namespace>/items/supply_rocket.json`:

```json
{
  "type": "rocket",
  "rocket": "mypack:supply_rocket",
  "variants": {
    "supply_rocket": { "meta": 0 }
  }
}
```

Uma variante de um foguete do Galacticraft (`galacticraftcore:rocket_t1`, `galacticraftplanets:rocket_t2`, `galacticraftplanets:rocket_t3`) lê um objeto `galacticraft`. Em qualquer outra entidade o objeto é ignorado, e o log avisa. O foguete é colocado em uma plataforma de lançamento com um item do tipo `rocket`. Quebrar o foguete, ou pousar com ele em outro corpo celeste, devolve esse item com a carga e o combustível dentro.

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `tier` | não | int | o do foguete base | O nível do foguete, que decide os corpos que ele alcança |
| `fuelTank` | não | int | o do foguete base | Tamanho do tanque de combustível. Um foguete de nível 1 tem `1000` |
| `cargoSlots` | não | `0`, `27`, `36`, `54` | `0` | Os tamanhos de carga do Galacticraft. `27` dá 18 slots, como nos foguetes do próprio Galacticraft |
| `cargo` | não | lista de entradas de filtro | nenhum | O que os slots de carga e um Carregador de Carga podem receber. Sem ele, aceitam qualquer coisa. As entradas são as de `storage`, com `item` ou `oreDict` |
| `requiredPayload` | não | lista | nenhum | O que precisa estar na carga antes de o foguete decolar. Cada entrada é `item` ou `oreDict` e um `count`, padrão `1` |
| `payload` | não | lista | nenhum | O que um item de foguete recém-feito carrega, colocado na carga quando é posicionado pela primeira vez. Cada entrada é `item` e um `count`, padrão `1` |

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

Ajustes ao lado de `orders`:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `stockName` | não | string | `Stock` | O nome que torna um baú colocado um baú de estoque, sem diferenciar maiúsculas. Vazio desliga os baús de estoque |
| `hire` | não | nome de item | nenhum | Contrata um trabalhador livre: clique nele com este item, sem agachar, e ele passa a ser seu e o item é gasto. Um trabalhador livre não pertence a nenhum jogador nem time. Metadados como `minecraft:dye:4` |
| `spawnCap` | não | int | `1` | Quantos trabalhadores uma ordem pode gerar no baú dela quando nenhum aparece. `0` não gera nenhum |

Chaves da ordem:

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `job` | sim | `gather`, `mine`, `farm` ou `haul` | | `gather` e `mine` extraem os blocos que `blocks` nomeia, como o próprio bloco ou como o que ele solta, sempre que um lado esteja aberto ao ar ou a um bloco que não é um cubo completo. `farm` colhe plantações maduras e replanta cada uma com as próprias sementes. `haul` leva os itens que `blocks` nomeia de outros baús e contêineres para o baú da ordem |
| `blocks` | sim, exceto em `farm` | nome do dicionário de minérios | nenhum | O que a ordem quer, como `oreIron`, `logWood` ou `cropWheat`, sem diferenciar maiúsculas. Em uma ordem `farm`, limita a colheita às plantações que o soltam |
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

**Como um trabalhador trabalha.** Uma variante que um executor nomeia, ou toda variante quando algum executor é um time ou uma tag, procura trabalho uma vez por segundo enquanto está ociosa. Ela pega a melhor ordem aberta cujo baú esteja dentro da área dela mais 32 blocos, segura a ordem com uma concessão que expira quando para de trabalhar e mantém a concessão ao salvar e recarregar. Extrair leva o mesmo tempo que levaria a um jogador segurando a ferramenta do trabalhador, Eficiência incluída, e Fortuna conta para o que cai; Toque Suave não. O que ela extrai vai para os itens de `storage` quando a variante os tem, senão para a mão secundária, nunca para o chão. Quando não consegue carregar mais, ela anda até o baú e põe tudo lá. Cada bloco desgasta a ferramenta, e um trabalhador cuja ferramenta quebra volta ao baú atrás de outra do mesmo tipo, ou descansa um minuto quando não há nenhuma. Ao morrer, um trabalhador solta a ferramenta e o que tem nas mãos, e o armazenamento dele se esvazia uma vez, como o armazenamento sempre faz. `mobGriefing` não o impede, já que um jogador pediu o trabalho.

**Quem trabalha para quem.** Quando o jogador que abre uma ordem está em um time do placar, só os trabalhadores desse time a pegam. Caso contrário a ordem pertence a esse jogador, e um trabalhador pertence ao primeiro jogador que lhe dá um trabalho, por uma ferramenta, por `hire` ou ao pegar uma ordem desse jogador; a partir daí ele só executa ordens desse jogador. Um trabalhador livre contratado ou que recebe uma ferramenta de um jogador em um time entra nesse time.

**Surgimento no baú.** Uma ordem que ficou dez segundos sem trabalhador gera um dos executores variantes dela ao lado do baú, até `spawnCap` para essa ordem. O trabalhador gerado pertence ao time ou ao jogador da ordem.

## Exposições

*criaturas e perigos*

`<namespace>/exposures/*.json`

O caminho do arquivo é o nome do perigo, e a mensagem de morte vem da chave de lang `death.attack.rdpl.<file name>`.

Um perigo definido pelo pacote: blocos, itens e dimensões nomeados expõem os jogadores que estão perto desses blocos, carregam esses itens ou permanecem nessas dimensões, em níveis, cada nível aplicando efeitos e dano periódico. Um perigo também pode ser contraído de mobs e jogadores próximos, ou cair junto com a chuva ([Contágio e clima](#contágio-e-clima)). Um arquivo define um perigo; vários funcionam lado a lado. Os padrões de cada chave são os números que a radiação do Immersive World usa.

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `blocks` | um dos cinco | lista de `block` ou `block=level` | | Blocos que expõem um jogador próximo a eles. Sem nível, vale 1 |
| `items` | um dos cinco | lista de `item` ou `item=level` | | Itens que expõem um jogador que os carrega ou veste |
| `dimensions` | um dos cinco | lista de `dim` ou `dim=level` | | Ids numéricos de dimensões que expõem qualquer jogador dentro delas |
| `levels` | sim | lista de níveis | | A escada de gravidade, a primeira entrada é o nível 1. O jogador recebe o mais alto nível que alguma fonte alcança |
| `immunity` | não | nome de poção | nenhum | Um efeito cujo portador não é exposto de forma alguma |
| `scanInterval` | não | ticks | `20` | Com que frequência o entorno e o inventário são verificados |
| `range` | não | blocos | `10` | Até onde a exposição de um bloco alcança, como uma esfera |
| `sourcesForNextLevel` | não | int | `0` | Essa quantidade de fontes próximas de um nível o empurra um nível adiante. `0` desativa isso |
| `skipsCreative` | não | booleano | `true` | Jogadores em modo criativo e espectador são deixados em paz |

### Níveis

*exposições*

Cada nível:

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `effect` | sim | nome de poção | | O efeito que marca o nível no jogador. A sua presença aciona o dano, então deve ser um que o pacote defina para isso |
| `damage` | não | meios-corações | `0` | Dano causado a cada `damageInterval` ticks enquanto o nível se mantém. Ignora armadura |
| `damageInterval` | não | ticks | `160` | Com que frequência esse dano é aplicado |
| `effects` | não | lista de efeitos | nenhum | Efeitos extras aplicados junto, no mesmo formato que os tipos de poção usam. Sem `duration`, seguem a janela de verificação |

Os efeitos do nível duram um pouco além da próxima verificação, então se afastar deixa que eles se dissipem sozinhos. A morte por dano de exposição lê a sua mensagem de `death.attack.rdpl.<file name>`, que os arquivos de lang do pacote fornecem.

### Contágio e clima

*exposições*

Mais duas fontes, escritas no mesmo arquivo. Portadores e expostos passam o perigo adiante para os receptores ao redor, e a chuva ou a tempestade expõem os jogadores em que caem.

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `carriers` | um dos cinco | lista de `entity` ou `entity=level` | | Mobs, ou `minecraft:player`, que sempre passam o perigo adiante naquele nível. Sem nível, vale 1 |
| `contagious` | não | booleano | `false` | Qualquer exposto passa o perigo adiante no nível que possui |
| `catchers` | não | lista de nomes de entidades | `minecraft:player` | Quem pode contraí-lo. Um mob só o contrai de portadores e expostos, nunca de blocos, itens ou clima |
| `contagionRange` | não | blocos | `4` | Até onde um portador ou exposto alcança, como uma esfera |
| `contagionChance` | não | `0` a `1` | `0.1` | A chance, a cada verificação do portador ou exposto, de cada receptor ao alcance contraí-lo |
| `contagionDuration` | não | ticks | `1200` | Por quanto tempo um perigo contraído mantém o nível contraído. Contraí-lo de novo reinicia o tempo |
| `weather` | um dos cinco | lista de `kind` ou `kind=level` | | `rain` expõe um jogador em que a chuva cai: céu aberto acima, em um bioma onde chove. `thunder` vale durante uma tempestade |
| `weatherDimensions` | não | lista de `dim` | todas as dimensões | Ids numéricos de dimensões onde o clima expõe |

Um perigo contraído conta como mais uma fonte na verificação, com o nível mais alto vencendo como em qualquer outra, e `immunity` protege contra ele também. Nada se espalha a menos que `contagionRange` e `contagionChance` estejam acima de `0` e o arquivo nomeie `carriers` ou defina `contagious`, e os mobs só são examinados quando algum arquivo faz isso.

---

# O mundo

## Modelos de mundo

*o mundo*

`<namespace>/worldtemplates/*.json`

O caminho do arquivo é o nome do modelo, que a opção de config `worldTemplate` pode citar para escolhê-lo diretamente.

Reúne a forma de um mundo em um único arquivo, para que um pacote entregue um mundo inteiro de uma vez em vez de pedir ao jogador que defina uma dúzia de opções de config.

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `name` | não | string | o nome do arquivo | Mostrado no log e nos relatórios |
| `default` | não | nome de bioma ou `void` | `void` | O que preenche um bioma que o bloqueio removeu |
| `roles` | não | objeto de função para bioma | nenhum | Biomas que preenchem funções específicas, como oceano ou rio |
| `structures` | não | objeto de [nome de estrutura](#listas-de-valores) para booleano | nenhum | Estruturas vanilla ativadas ou desativadas |
| `settings` | não | objeto | nenhum | Valores de config que o modelo define |
| `dimensions` | não | lista de ints | todas as dimensões | A quais dimensões se aplica |

`settings` usa os mesmos nomes de chave da config, então não há tabela de tradução para aprender.

Qual modelo está ativo é decidido pela opção de config `worldTemplate`. Deixada em `auto`, vence o pacote de maior prioridade que entregar um, na mesma ordem que todo o resto segue. Citar um modelo ali o escolhe diretamente.

## Regras do jogo

*o mundo*

`<namespace>/gamerules/*.json`

O nome do arquivo é você quem escolhe, só a pasta é lida, e vários arquivos se acumulam.

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

Cada chave é o id do mundo ao qual as regras pertencem: `0` para o overworld, `-1` para o nether, `1` para o end, e o que um mod usar para os seus. Os valores são strings, como no comando `/gamerule`, então `"false"` em vez de `false`. Elas são aplicadas a mundos novos. Um arquivo de dimensão carrega as mesmas regras em um bloco `gameRules`, que só se aplica àquele mundo.

## Biomas

*o mundo*

`<namespace>/biomes/*.json`

O caminho do arquivo é o nome de registro do bioma, então `mypack/biomes/ruby_forest.json` registra `mypack:ruby_forest`. `name` é apenas o que o jogador vê.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa.

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

### O bioma

*biomas*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `name` | não | string | o nome do arquivo | Nome mostrado ao jogador |
| `id` | não | int | atribuído para você | Id fixo do bioma. Só defina se precisar que seja estável |
| `types` | não | lista de tipos do dicionário | estimado | Registra o bioma sob esses tipos, como `FOREST`, `COLD`, `WET` ou `NETHER`, para que outros mods o encontrem. Se omitido, o Forge estima da melhor forma a partir da quantidade de árvores, altura, temperatura, precipitação e bloco do solo do bioma |
| `baseBiome` | não | nome de bioma | nenhum | Um bioma existente de onde copiar configurações |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | O arquivo é ignorado a menos que todos estejam presentes |

### Clima

*biomas*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `temperature` | não | float | `0.5` | Abaixo de 0.15 neva, acima de 1.0 é quente como deserto |
| `rainfall` | não | float, 0 a 1 | `0.5` | Quão úmido é |
| `rain` | não | booleano | `true` | Se há clima algum |
| `snow` | não | booleano | `false` | Se a chuva cai como neve |

### Solo e cores

*biomas*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `baseHeight` | não | float | `0.1` | Altura do terreno. O nível do mar é 0, planícies 0.125 |
| `heightVariation` | não | float | `0.2` | Quão acidentado é |
| `topBlock` | não | nome de bloco | grama | O bloco da superfície |
| `fillerBlock` | não | nome de bloco | terra | Logo abaixo da superfície |
| `stoneBlock` | não | nome de bloco | pedra | O grosso do solo |
| `waterColor` | não | cor hexadecimal | `FFFFFF` | Tonalidade da água |
| `grassColor` | não | cor hexadecimal | do clima | Tonalidade da grama, no lugar da cor que a temperatura e a precipitação dariam |
| `foliageColor` | não | cor hexadecimal | do clima | Tonalidade das folhas, do mesmo modo |
| `snowColor` | não | cor hexadecimal | a da dimensão | Tonalidade da neve no chão, acima do `snowColor` da dimensão |

### Decoração e spawns

*biomas*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `decoration` | não | objeto | contagens do vanilla | Contagens por chunk. Os nomes que ele lê são `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` e `waterlily`, mais `falls`, onde acima de zero significa que lagos e nascentes são gerados, e `extratreechance`, a porcentagem de chance de mais uma árvore. Qualquer outro nome é registrado no log e ignorado |
| `spawns` | não | lista de objetos | lista do vanilla | Veja abaixo |
| `keepDefaultSpawns` | não | booleano | `false` | Mantém a lista do vanilla junto com a sua |
| `spawnChance` | não | float, abaixo de 1 | `0.1` | A probabilidade de outro rebanho ser colocado enquanto o terreno é criado pela primeira vez. O jogo continua sorteando enquanto tiver sucesso, então 1 nunca para e preenche o mundo até acabar o espaço. Qualquer valor igual ou acima de 0.99 é recusado e 0.99 é usado |
| `spawnRates` | não | objeto de `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` para um multiplicador | nenhum | Com que frequência mobs hostis surgem aqui, no lugar das configurações globais. Veja abaixo |

Uma entrada de spawn recebe `entity` (obrigatório), `type` (`creature`, um dos [tipos de criatura](#listas-de-valores)), `weight` (`10`), `min` (`1`) e `max` (`min`).

`spawnRates` trata apenas de mobs hostis, e de mais nada. Ele recebe quatro chaves e nenhuma outra: `surfaceDay` e `surfaceNight` para onde o céu pode ser visto, `undergroundDay` e `undergroundNight` para onde não pode. Cada uma é um multiplicador de com que frequência um mob hostil pode aparecer: `1` é a taxa normal, `0` os impede por completo, abaixo de 1 reduz algumas tentativas, e acima de 1 deixa passar tentativas que o jogo de outra forma teria recusado, então `2` é o dobro. Uma chave omitida significa que o bioma não decide, e a configuração global para aquele momento e lugar é usada no lugar. Qualquer outra coisa escrita aqui não é uma chave e é ignorada, então uma taxa com o nome de um tipo de criatura não faz absolutamente nada.

### Onde gera

*biomas*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `placement` | não | objeto | nenhum | Onde ele é gerado. Veja abaixo |
| `villageType` | não | `oak`, `sandstone`, `acacia` ou `spruce` | nenhum | Com o que uma vila construída aqui é feita. Vazio, constrói com carvalho, como faria sem a chave |

`placement`:

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `climate` | não | string | nenhum | A qual grupo climático do vanilla ele se junta |
| `weight` | não | int | `10` | Com que frequência é escolhido em relação aos vizinhos |
| `villages` | não | booleano | `false` | Vilas podem ser geradas |
| `villageSpawn` | não | booleano | `true` | Aldeões podem surgir nelas |
| `strongholds` | não | booleano | `false` | Fortalezas podem ser geradas |
| `playerSpawn` | não | booleano | `false` | O spawn do mundo pode ser colocado aqui |

### Faixas de altura e ilhas flutuantes

*biomas*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `minHeight` | não | int | nenhum | O menor y que este bioma domina como bioma 3D. Definir qualquer uma das alturas transforma o bioma em uma faixa: a coluna mantém o seu próprio bioma fora dela, e dentro dela cada célula de 4 por 4 por 4 do mundo informa este. Somente mundos rubic, e aplicado conforme o terreno é criado, então o terreno existente mantém o que tinha |
| `maxHeight` | não | int | nenhum | O maior y dessa faixa |
| `replaces` | não | lista de nomes de biomas | todos os biomas | Restringe a faixa às colunas cujo bioma próprio está nomeado aqui, de modo que uma faixa alpina possa ficar sobre montanhas e nada mais |
| `skyStone` | não | nome de bloco | a configuração do mundo | O bloco de que as ilhas flutuantes são feitas sob a superfície onde este bioma se aplica. Em uma faixa, `topBlock` e `fillerBlock` pintam a superfície da ilha com ele, então uma faixa é a forma de dar a um trecho de céu ilhas só suas |
| `skyIslands` | não | float, `-1` a `1` | a configuração do mundo | O limiar das ilhas onde este bioma se aplica. Quanto menor, mais terra se reúne |
| `skyThickness` | não | float, `0` ou mais | a configuração do mundo | Quão sólidas são as ilhas onde este bioma se aplica |

### Temperatura por altura

*biomas*

**Temperatura por altura.** Um bioma esfria conforme sobe, o que põe neve no topo das montanhas e impede a chuva acima de uma linha. Três chaves de `terrain` movem essa curva, o que importa em um mundo rubic, onde o solo pode ficar muito acima ou abaixo da altura que o jogo presume. Os padrões são o que o jogo faz, então um pacote que os deixa em paz não muda nada.

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

| Chave | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | int | `64` | A altura a partir da qual a curva é medida. Nela ou abaixo dela, um bioma informa a sua própria `temperature` sem alteração |
| `biomeTemperatureHeightFactor` | float | `-0.001667` | Quanto a temperatura se move por bloco acima dessa altura, os 0.05 em 30 blocos do próprio jogo. Negativo esfria com a altitude, positivo aquece |
| `biomeTemperatureScaleMaxY` | int | `256` | A altura em que a curva para, para que um mundo mais alto que o do próprio jogo não continue esfriando até o teto |

## Dimensões

*o mundo*

`<namespace>/dimensions/*.json`

O caminho do arquivo nomeia a dimensão para `suffix`, cujo padrão é `DIM_<name>`. A dimensão em si é encontrada pelo seu `id`, que é o número ao qual todo o resto se refere.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa.

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

### Nível superior

*dimensões*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `id` | sim | int | | O id da dimensão. Não pode conflitar com outro mod |
| `suffix` | não | string | `DIM_<name>` | A pasta de salvamento |
| `keepLoaded` | não | booleano | `false` | Mantém-na carregada quando ninguém está nela |
| `gameRules` | não | objeto | nenhum | Regras que se aplicam somente aqui |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | O arquivo é ignorado a menos que todos estejam presentes |

### O bloco `terrain`

*dimensões*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `type` | não | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Qual gerador a constrói |
| `generatorOptions` | não | string | nenhum | A string do gerador, como a de uma predefinição de mundo superplano |
| `structures` | não | booleano | `true` | Se as estruturas vanilla são geradas |

### O bloco `biomes`

*dimensões*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `source` | não | `inherit`, `single` | `inherit` | `inherit` usa o mapa de biomas normal, `single` usa um único bioma em toda parte |
| `biome` | quando `single` | nome de bioma | `minecraft:plains` | Qual é esse bioma |

### O bloco `sky`

*dimensões*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | não | booleano | `true` | Se a luz do dia chega até ela |
| `surfaceWorld` | não | booleano | `true` | Se mapas e bússolas se comportam como no overworld |
| `respawn` | não | booleano | `true` | Se os jogadores renascem aqui |
| `respawnDimension` | não | int | nenhum | Onde renascem no lugar |
| `spawning` | não | booleano | `true` | Se mobs surgem |
| `nether` | não | booleano | `false` | Tratada como o nether para portais e tetos |
| `beds` | não | booleano | `true` | Desativado, as camas explodem |
| `waterVaporizes` | não | booleano | `false` | A água evapora |
| `cloudHeight` | não | int | `128` | Onde ficam as nuvens |
| `cloudColor` | não | cor hexadecimal | nenhum | Tonalidade das nuvens |
| `cloudSpeed` | não | float | `1.0` | Com que velocidade as nuvens se deslocam. `0` as mantém paradas, um valor negativo as inverte |
| `cloudLayers` | não | lista de objetos | nenhum | Várias camadas de nuvens. Veja [Neblina, luz, nuvens e calor](#neblina-luz-nuvens-e-calor) |
| `groundLevel` | não | int | `63` | Nível do mar, usado para o horizonte e as buscas de spawn |
| `movementFactor` | não | float | `1.0` | Razão de distância para o overworld. O nether usa 8 |
| `fogColor` | não | cor hexadecimal ou `sample` | nenhum | Tonalidade da neblina ao meio-dia. Escurece à noite como a neblina do vanilla. `sample` mistura o céu com o solo ao redor do jogador |
| `showFog` | não | booleano | `false` | Neblina espessa, como no nether |
| `fogDensity` | não | float, 0 a 1 | `0.0` | Quão espessa é a neblina. `0` mantém a distância do vanilla, `1` a fecha em 8 blocos |
| `fogGroundWeight` | não | float, 0 a 1 | `0.5` | Com `fogColor: sample`, quanto o solo conta em relação ao céu |
| `skyColor` | não | cor hexadecimal | nenhum | Tonalidade do céu ao meio-dia. Escurece à noite e fica cinza na chuva e na tempestade como o céu do vanilla |
| `fixedTime` | não | int, ticks | nenhum | Trava a hora do dia |
| `sunriseColors` | não | booleano | `true` | Se o nascer e o pôr do sol são tingidos |
| `ambientLight` | não | float, 0 a 1 | `0.0` | Luz mínima em toda parte |
| `lightSkyColor` | não | cor hexadecimal | nenhum | Tonalidade da luz do dia em blocos e mobs |
| `lightBlockColor` | não | cor hexadecimal | nenhum | Tonalidade da luz de tochas e de outras luzes de blocos |
| `skyFactor` | não | float, 0 a 1 | `1.0` | Quão brilhante a luz do dia parece. Desenhado apenas no cliente, então o spawn de mobs não muda |
| `starBrightness` | não | float, 0 a 1 | nenhum | Quão brilhantes são as estrelas |
| `sunBrightness` | não | float, 0 a 1 | `1.0` | Quão brilhante o sol é desenhado |
| `moonBrightness` | não | float, 0 a 1 | `1.0` | Quão brilhante a lua é desenhada e, com `bodies`, todos os corpos exceto o sol |
| `heat` | não | objeto | nenhum | Uma distorção de calor sobre a visão. Veja [Neblina, luz, nuvens e calor](#neblina-luz-nuvens-e-calor) |
| `renderSky` | não | booleano | `true` | Desativado, nada desenha o céu, o sol, a lua ou as estrelas, restando a cor da neblina |
| `renderClouds` | não | booleano | `true` | Desativado, nenhuma nuvem é desenhada |
| `renderWeather` | não | booleano | `true` | Desativado, nenhuma chuva ou neve é desenhada |
| `sun` | não | objeto | nenhum | O seu próprio sol. Veja [O renderizador do céu](#o-renderizador-do-céu) |
| `bodies` | não | lista de objetos | nenhum | Planetas e luas pendurados no céu. Veja [O renderizador do céu](#o-renderizador-do-céu) |
| `stars` | não | objeto | nenhum | O seu próprio campo de estrelas. Veja [O renderizador do céu](#o-renderizador-do-céu) |

### O renderizador do céu

*dimensões*

Definir qualquer um entre `sun`, `bodies` ou `stars` troca o céu do vanilla pelo próprio do RDPL, que desenha a mesma cúpula, o mesmo brilho do nascer do sol e o mesmo vazio do vanilla, mas toma o sol, os outros corpos e as estrelas do pacote. Ele é desenhado apenas no cliente, e um servidor dedicado nunca o carrega. `renderSky: false` ainda vence e não desenha nada, e `renderClouds: false` é como se faz um céu sem nuvens.

Sem `bodies`, a lua do vanilla e as suas fases permanecem. Com `bodies`, a lista é tudo o que não for o sol, então uma lista vazia é um céu sem lua.

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `sun.texture` | não | caminho de textura | o sol do vanilla | A imagem do sol |
| `sun.size` | não | float | `30` | Metade da largura do sol a uma distância de 100. `0` o oculta |
| `bodies[].texture` | sim | caminho de textura | | A imagem do corpo |
| `bodies[].size` | não | float | `20` | Metade da sua largura a uma distância de 100. A lua do vanilla é `20` |
| `bodies[].angle` | não | float, graus | `180` | Quão adiante na trajetória do sol ele fica, atrás do sol. `180` é onde está a lua do vanilla. Com `followsTime` desativado, é medido a partir do ponto diretamente acima, então `0` é o zênite e `90` o horizonte |
| `bodies[].tilt` | não | float, graus | `0` | Quão afastado da trajetória do sol ele fica, para o norte ou o sul |
| `bodies[].followsTime` | não | booleano | `true` | Desativado, ele fica parado no céu em vez de girar com o sol |
| `stars.count` | não | int | `1500` | Quantas estrelas |
| `stars.size` | não | float | `0.15` | A menor estrela; a maior é dois terços maior ainda |

### Neblina, luz, nuvens e calor

*dimensões*

Essas chaves ficam no bloco `sky` ao lado das mais antigas, que continuam funcionando como antes. Todas são desenhadas apenas no cliente, então um servidor dedicado as ignora e nenhum save muda.

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

`fogColor: "sample"` lê os blocos do topo em um quadrado de 33 por 33 blocos ao redor do jogador uma vez por segundo, ilumina as cores de mapa deles para a hora do dia e as mistura com a cor do céu. A neblina se ajusta suavemente a cada nova amostra. `fogDensity` funciona com uma cor de neblina amostrada, uma definida ou nenhuma. Debaixo d'água, na lava e sob cegueira, a neblina do vanilla permanece.

`lightSkyColor` e `lightBlockColor` tingem o mapa de luz, então todo bloco iluminado e todo mob assume a tonalidade. `skyFactor` escala quão brilhante a luz do dia parece, enquanto o nível de luz que o servidor conta para spawn e plantações continua o mesmo.

Sem `cloudLayers`, `cloudSpeed` muda a velocidade da única camada do vanilla em `cloudHeight`. Com `cloudLayers`, cada entrada é uma camada própria, e `cloudHeight`, `cloudSpeed` e `cloudColor` preenchem o que uma entrada deixa de fora. `renderClouds: false` ainda não desenha nenhuma.

`sunBrightness` e `moonBrightness` esmaecem o sol e a lua além do esmaecimento da chuva do vanilla, no céu do vanilla e no seu próprio de [O renderizador do céu](#o-renderizador-do-céu).

`heat` coloca uma distorção ondulada sobre a visão enquanto o jogador está em um bioma pelo menos tão quente quanto `minTemperature`. Um deserto é 2.0 e planícies são 0.8. A distorção surge e se dissipa ao longo de alguns segundos e fica desligada debaixo d'água. Ela precisa de suporte a shaders da placa de vídeo e fica desligada enquanto outro shader de tela cheia, como a visão de espectador, está ativo.

`mode` define onde a distorção aparece. `screen` distorce uma faixa fixa na parte inferior da tela, para onde quer que o jogador olhe. `world` acompanha o terreno: o que está a menos de `startDistance` blocos continua nítido, a distorção vai aumentando em direção à borda distante da distância de renderização, onde a névoa se fecha, e o céu nunca é afetado, seja o jogador olhando para baixo, para a frente ou para cima.

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `cloudLayers[].height` | não | float | `cloudHeight` | Onde a camada fica |
| `cloudLayers[].speed` | não | float | `cloudSpeed` | Com que velocidade se desloca. `0` a mantém parada, um valor negativo a inverte |
| `cloudLayers[].color` | não | cor hexadecimal | `cloudColor` | A sua tonalidade |
| `heat.strength` | não | float, 0 a 1 | `0.1` | Quão forte é a distorção |
| `heat.minTemperature` | não | float | `1.5` | A temperatura de bioma mais baixa que provoca distorção |
| `heat.dayOnly` | não | booleano | `true` | Ativado, a distorção esmaece com a luz do dia e some à noite |
| `heat.mode` | não | string | `screen` | Onde a distorção aparece, `screen` ou `world` |
| `heat.startDistance` | não | float | `32` | No modo `world`, a quantos blocos de distância a distorção começa |

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

`starColor` tinge as estrelas, no céu vanilla e no seu próprio, do [renderizador do céu](#o-renderizador-do-céu). `starTwinkle` faz as estrelas cintilarem: elas se dividem em oito grupos que escurecem e clareiam cada um no seu ritmo, e o valor diz o quanto escurecem; com `1`, um grupo some por completo no ponto mais baixo.

`lightningColor` tinge os raios.

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `snowColor` | não | cor hexadecimal | branco | Tonalidade das camadas e dos blocos de neve |
| `waterFogColor` | não | cor hexadecimal | `050533` | Cor da neblina debaixo d'água |
| `lavaFogColor` | não | cor hexadecimal | `991A00` | Cor da neblina na lava |
| `starColor` | não | cor hexadecimal | branco | Tonalidade das estrelas |
| `starTwinkle` | não | float, 0 a 1 | `0.0` | O quanto as estrelas escurecem ao cintilar. `0` as mantém firmes |
| `lightningColor` | não | cor hexadecimal | `737380` | Tonalidade dos raios |

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `gravity` | não | float, acima de 0 | `1.0` | Aceleração de queda aqui, como multiplicador do vanilla. `0.17` é como na lua |
| `fallDamage` | não | float, acima de 0 | `1.0` | Dano de queda aqui, como multiplicador |
| `arrowGravity` | não | float, acima de 0 | segue `gravity` | Com que rapidez as flechas caem aqui, como multiplicador |

São os mesmos multiplicadores de [Física do mundo](#física-do-mundo), definidos na dimensão. Uma linha `dimension=value` de um modelo de mundo para esta dimensão ainda vence; um valor simples de modelo de mundo cobre apenas as dimensões que não definem nada próprio. Em um [corpo do Galacticraft](#corpos-do-galacticraft), o Galacticraft os aplica.

### O bloco `time`

*dimensões*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `dayLength` | não | int, ticks | `24000` | Quanto dura um dia e uma noite aqui. A fase da lua ainda gira uma vez a cada 24000 ticks |

### O bloco `weather`

*dimensões*

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `precipitation` | não | booleano | `true` | Desativado, nunca chove, neva nem há tempestade aqui |
| `lightning` | não | booleano | `true` | Desativado, chuva e tempestades vêm sem raios |
| `snow` | não | booleano | `true` | Desativado, a neve nunca se acumula |
| `freeze` | não | booleano | `true` | Desativado, a água nunca congela |
| `cycle.rainTicks` | não | int ou `[min, max]` | `[1000, 4600]` | Quanto dura um aguaceiro |
| `cycle.clearTicks` | não | int ou `[min, max]` | `[1000, 3000]` | Quanto dura o período seco entre aguaceiros |
| `cycle.maxStrength` | não | float, acima de 0 até 1 | `0.6` | O máximo de intensidade que um aguaceiro alcança. Cada aguaceiro varia entre um quarto disso e o valor inteiro |
| `cycle.thunderTicks` | não | int ou `[min, max]` | nenhum | Quanto dura uma tempestade com trovões. Sem ele, o ciclo nunca tem tempestade |
| `cycle.calmTicks` | não | int ou `[min, max]` | `[12000, 180000]` | Quanto dura a calmaria entre tempestades |
| `cycle.thunderStrength` | não | float, acima de 0 até 1 | `1` | Quão escura fica uma tempestade. Raios só caem acima de `0.9` |
| `rain.particle` | não | nome de partícula | `droplet` | O que espirra onde a chuva cai |
| `rain.sound` | não | nome de som | `minecraft:weather.rain` | O som da chuva |
| `rain.volume` | não | float | `0.2` | O seu volume, reduzido à metade quando a chuva cai acima de você |
| `rain.interval` | não | int | `3` | Quão raramente o som toca; maior é mais esparso, `0` o toca a cada oportunidade |
| `rain.color` | não | cor hexadecimal | `#FFFFFF` | Tonalidade da chuva que cai |
| `rain.snowColor` | não | cor hexadecimal | `#FFFFFF` | Tonalidade da neve que cai |
| `rain.angle` | não | float, 0 a 180 | `0` | Graus a partir da vertical para baixo: `90` sopra de lado, `180` sobe reto para cima. É desenhada inclinada no máximo 75 graus |
| `rain.heading` | não | float, graus | `0` | Para onde sopra: `0` sul, `90` oeste, `180` norte, `270` leste |
| `wind.gust` | não | float, 0 a 90 | `15` | Graus que uma rajada soma a `angle` no auge, sem nunca passar da horizontal |
| `wind.every` | não | int ou `[min, max]` | `[200, 600]` | Ticks de uma rajada até a seguinte |
| `wind.swing` | não | float, 0 a 180 | `30` | Graus que uma rajada desvia `heading` para um dos lados |

Um bloco `wind` deixa a chuva com rajadas. De vez em quando, uma rajada a inclina até `gust` graus a mais e desvia sua direção até `swing` graus para um lado; ela cresce e se desfaz em até quatro segundos, e as rajadas vêm a cada `every` ticks. A chuva e a neve se inclinam junto, e as partículas de ambiente da dimensão derivam para onde a chuva se inclina, com ou sem rajadas. Um bloco `wind` sem bloco `rain` dá à chuva os valores padrão.

As outras dimensões compartilham a chuva do overworld. Um `cycle` dá a esta um clima próprio: aguaceiros vêm e vão nos tempos acima, seja o que for que o overworld esteja fazendo. Com `thunderTicks` ela também tem tempestades, em tempos próprios; uma tempestade que encontra um aguaceiro leva o aguaceiro à força total, escurece o céu e, com `lightning` ativado, traz raios. `weatherCeiling` em um [modelo de mundo](#modelos-de-mundo) ainda limita a altura que a chuva alcança.

Um bloco `rain` muda o aspecto e o som da chuva e da neve aqui, com ou sem um `cycle`; sem um, elas têm o aspecto e o som do vanilla. Funciona do mesmo modo em um planeta ou lua do Galacticraft.

### O bloco `ambience`

*dimensões*

| Chave            | Obrigatório | Valor               | Padrão           | O que faz                                                                                                                                                                                                                |
| ---------------- | ----------- | ------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `music`          | não         | nome de som         | nenhum           | Música tocada aqui no lugar das faixas de sempre, no criativo também. Ao chegar, a faixa atual é cortada                                                                                                                 |
| `musicDelay`     | não         | int ou `[min, max]` | `[12000, 24000]` | Ticks de silêncio entre duas faixas                                                                                                                                                                                      |
| `loopSound`      | não         | nome de som         | nenhum           | Um som que toca em loop enquanto você está aqui, subindo ao chegar e sumindo ao sair                                                                                                                                     |
| `ambientSound`   | não         | nome de som         | nenhum           | Um som tocado de vez em quando, como os biomas modernos adicionam os seus. Enviado pelo servidor só a esse jogador                                                                                                       |
| `soundChance`    | não         | 0.0 a 1.0           | `0.0111`         | A chance por tick de `ambientSound` tocar                                                                                                                                                                                |
| `particle`       | não         | nome de partícula   | nenhum           | Uma partícula flutuando no ar ao seu redor, um dos nomes de partícula do jogo, como `depthsuspend`, `townaura`, `reddust` ou `mobSpellAmbient`                                                                           |
| `particleChance` | não         | 0.0 a 1.0           | `0.00625`        | Sua densidade, contada como nos biomas modernos: a cada tick cerca de 667 pontos num raio de 16 blocos e outros 667 num raio de 32 são tentados, e cada um que não é um bloco inteiro mostra a partícula com esta chance |
| `particleColor`  | não         | cor hexadecimal     | nenhum           | A cor de uma partícula que aceita uma: `reddust`, `mobSpell` e `mobSpellAmbient`                                                                                                                                         |

Um bloco `ambience` dá à dimensão música, sons e partículas flutuantes próprios, como um bioma moderno tem. A opção de vídeo “Partículas” reduz as partículas como faz com as do vanilla, e os sons de caverna do jogo continuam tocando.

## Corpos do Galacticraft

*o mundo*

`<namespace>/celestial/*.json` e o bloco `galacticraft` de `<namespace>/dimensions/*.json`

Com o Galacticraft instalado, um pacote pode colocar os seus próprios sistemas estelares, planetas, luas, cinturões de asteroides e estações espaciais no mapa estelar do Galacticraft, e fazer de uma dimensão do pacote um lugar para onde um foguete voa. Sem o Galacticraft, tudo isso é ignorado: os arquivos de `celestial/` são ignorados e uma dimensão com um bloco `galacticraft` não é registrada, e o log avisa.

O nome de um corpo no mapa vem do arquivo de lang do pacote, sob a chave que o Galacticraft usa: `solarsystem.<name>`, `star.<name>`, `planet.<name>`, `moon.<name>` ou, para uma estação, `satellite.<name>`. Um cinturão de asteroides usa `planet.<name>` ao redor de uma estrela e `moon.<name>` ao redor de um planeta.

### Sistemas estelares e corpos apenas de mapa

*corpos do Galacticraft*

`<namespace>/celestial/*.json`

Um arquivo aqui é um sistema estelar ou um planeta ou lua que fica no mapa sem lugar para pousar.

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `kind` | sim | `system`, `planet`, `moon` | | O que o arquivo cria |
| `galaxy` | não | string | `milky_way` | A galáxia de um sistema |
| `mapPosition` | para um sistema | `[x, y]` ou `[x, y, z]` | | Onde o sistema fica no mapa da galáxia |
| `star` | não | objeto de [chaves de mapa](#chaves-de-mapa) | | A estrela do sistema, desenhada no seu centro |
| `requires` | não | lista de ids de mods ou namespaces de pacotes | nenhum | O arquivo é ignorado a menos que todos estejam presentes |

Um planeta ou lua aqui recebe as [chaves de mapa](#chaves-de-mapa) com um `tier` padrão de `0`. Planetas e luas entram no mapa depois de todos os sistemas, então um planeta pode orbitar um sistema do mesmo pacote.

### Chaves de mapa

*corpos do Galacticraft*

Todo corpo, seja um arquivo de `celestial/`, a `star` de um sistema ou o bloco `galacticraft` de uma dimensão, posiciona-se no mapa com estas chaves.

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `name` | não | string | o nome do arquivo | O nome do corpo, que a sua chave de lang e todo `parent` usam |
| `parent` | para uma lua ou estação | nome | `sol` para um planeta ou cinturão | O sistema que um planeta orbita, o planeta que uma lua orbita, o planeta ou lua que uma estação orbita, ou um sistema ou planeta para um cinturão de asteroides |
| `icon` | não | caminho de textura | o ícone de Marte, Lua, Sol, asteroide ou estação do Galacticraft | A imagem no mapa |
| `relativeSize` | não | float | `1.0`, uma lua ou estação `0.2667` | Seu tamanho no mapa |
| `distance` | não | float | `1.0`, uma lua `13`, uma estação `9` | A que distância ele orbita |
| `scaledDistance` | não | float | `distance` | A distância usada no mapa ampliado |
| `orbitTime` | não | float, anos | `1.0`, uma lua `100`, uma estação `20` | Quanto tempo leva uma órbita no mapa. Negativo corre para trás |
| `phaseShift` | não | float, radianos | `0`, uma estação espaçada | Onde na órbita ele começa. Uma estação sem esse valor começa 2.4 radianos depois da última estação ao redor do mesmo planeta, incluindo as estações das suas luas, para que duas nunca dividam o mesmo ponto |
| `ringColor` | não | cor hexadecimal | `19E599` | A linha da órbita, onde o mapa desenha uma |
| `tier` | não | int | `1` para uma dimensão, o do seu pai para uma estação, `0` nos demais casos | O nível de foguete que o mapa mostra que ele exige. Com o GalaxySpace, o mapa do AsmodeusCore calcula o nível pela distância, e um corpo ao redor de outra estrela exige o nível máximo, a menos que `enableNewTierSystem` esteja desativado em `config/AsmodeusCore/core.conf` |

### O bloco `galacticraft`

*corpos do Galacticraft*

`<namespace>/dimensions/*.json`

Um bloco `galacticraft` em um arquivo de dimensão faz dessa dimensão um planeta ou lua próprio, ou um [cinturão de asteroides](#cinturões-de-asteroides) ou uma [estação espacial](#estações-espaciais). Tudo fora do bloco, o céu, a física, o tempo e o clima, continua sendo da dimensão e funciona igual com ou sem o Galacticraft; o bloco guarda apenas o que o Galacticraft lê.

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

O bloco recebe as [chaves de mapa](#chaves-de-mapa), mais:

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `kind` | não | `planet`, `moon`, `asteroids`, `station` | `planet` | O que a dimensão é |
| `reachable` | não | booleano | `true` | Desativado, ela está no mapa, mas nenhum foguete vai até lá |
| `minTier` | não | int | `tier` | O menor nível de foguete que pode pousar |
| `landing` | não | `lander`, `parachute`, `balloons` | `lander` | Como um jogador desce. `balloons` precisa do Galacticraft Planets e vira um lander sem ele. O `disableLander` do Galacticraft força `parachute` |
| `landingHeight` | não | float | `900`, um paraquedas `250` | A altura em que um jogador chega |
| `arrival` | não | `departure`, `spawn` | `departure` | Pousa acima de onde o foguete decolou ou acima do spawn desta dimensão |
| `exitHeight` | não | float | `1200` | A altura em que um foguete que sai desta dimensão a deixa |
| `rocketGui` | não | caminho de textura | a do overworld do Galacticraft | A tela de voo |
| `checklist` | não | lista de strings | nenhum | Chaves de checklist do Galacticraft mostradas antes do lançamento |
| `meteorFrequency` | não | float | de `density` | Quão raramente meteoros caem, perto de cada jogador cerca de uma vez a cada tantas vezes 750 ticks. `0` os impede |
| `fuelMultiplier` | não | float | `1.0` | Combustível que um foguete queima ao partir daqui |
| `soundReduction` | não | float | de `density` | Quanto mais baixo é o som neste ar |
| `solarEnergy` | não | float | `1.0` | Produção dos painéis solares aqui |
| `netherPortals` | não | booleano | `false` | Se portais do nether acendem aqui |

| Chave de `atmosphere` | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `gases` | não | lista de `NITROGEN`, `OXYGEN`, `CO2`, `WATER`, `METHANE`, `HYDROGEN`, `HELIUM`, `ARGON` | nenhum | O ar. Nenhum é vácuo, e o fogo precisa de `OXYGEN` |
| `breathable` | não | booleano | oxigênio e sem CO2 | Se os jogadores respiram sem traje |
| `corrosive` | não | booleano | `false` | Corrói a armadura sem um controlador de escudo |
| `temperature` | não | float | `0` | O nível térmico do Galacticraft. Abaixo de 0 é frio e acima é quente; o isolamento térmico responde a isso |
| `wind` | não | float | `1.0` com gases, `0` sem | Move bandeiras e aciona a energia eólica |
| `density` | não | float | `1.0` | Quão denso é o ar. Define os padrões de meteoros e de som |

| Chave de `dungeon` | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `spacing` | não | int, blocos | `0` | Distância entre masmorras do Galacticraft. `0` é nenhuma |
| `chest` | não | loot table | nenhum | O saque nos baús delas |

O Galacticraft só constrói as suas masmorras no seu próprio terreno, então `dungeon` só importa onde algo executa o gerador de masmorras do Galacticraft; o terreno do RDPL não o faz.

Como a chuva aparece e soa em um planeta é o bloco [`weather.rain`](#o-bloco-weather) da própria dimensão, como no exemplo acima.

**Quem registra a dimensão.** A dimensão de um corpo alcançável é registrada pelo Galacticraft, para que foguetes e clientes multiplayer a vejam; uma com `reachable: false` é registrada pelo RDPL. Se o corpo não puder ser colocado, porque o seu pai é desconhecido ou o seu nome já está em uso, a dimensão não é registrada, e o log diz o motivo.

### Cinturões de asteroides

*corpos do Galacticraft*

`<namespace>/dimensions/*.json`

Uma dimensão cujo bloco `galacticraft` tem `kind: "asteroids"` é um cinturão de asteroides: o campo de asteroides do próprio Galacticraft, gerado em um vazio. Ela precisa do Galacticraft Planets e de um `terrain` do tipo `void`; sem qualquer um dos dois a dimensão não é registrada, e o log diz o motivo.

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

O `parent` de um cinturão é um sistema estelar ou um planeta. Ao redor de um sistema ele fica no mapa como um planeta, e ao redor de um planeta como uma lua. Ele aceita todas as chaves de [o bloco `galacticraft`](#o-bloco-galacticraft) exceto estas, que um cinturão ignora:

| Chave | Em um cinturão |
| --- | --- |
| `landing` | Um jogador chega em uma cápsula de entrada no asteroide mais próximo, como no cinturão do próprio Galacticraft |
| `landingHeight` | A cápsula de entrada define a sua própria altura |
| `arrival` | A cápsula de entrada escolhe o asteroide |
| `dungeon` | Um cinturão de pacote não tem bases abandonadas |

O céu, a gravidade, o tempo e o clima são as chaves da própria dimensão, como em qualquer planeta de pacote. Cinco delas têm padrões diferentes em um cinturão, para combinar com os do próprio Galacticraft:

| Chave | Sem ela |
| --- | --- |
| `sky.fogColor` | `000000`, então a neblina e o horizonte são pretos |
| `sky.renderClouds` | `false` |
| `sky.sunriseColors` | `false`, um cinturão não tem pôr do sol |
| `sky.sun`, `sky.bodies`, `sky.stars` | O céu de asteroides do próprio Galacticraft é desenhado: um pequeno sol branco, sem lua e um campo de estrelas denso |
| `time.dayLength` | Sem dia: o sol fica parado no horizonte e é sempre dia |

Definir qualquer um entre `sun`, `bodies` ou `stars` desenha o céu do pacote no lugar, e `sky.renderSky` desativado ainda não desenha nenhum. Definir `time.dayLength`, mesmo como `24000`, ou `sky.fixedTime` dá ao cinturão um dia próprio.

### Estações espaciais

*corpos do Galacticraft*

`<namespace>/dimensions/*.json`

Um arquivo de dimensão cujo bloco `galacticraft` tem `kind: "station"` permite que os jogadores construam uma estação espacial em órbita de um planeta ou lua do pacote, pelo próprio botão do mapa do Galacticraft e pela sua própria dimensão de órbita. O arquivo é o tipo de estação, não uma estação: cada estação que um jogador constrói é uma dimensão própria, que o Galacticraft cria e mantém.

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

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `kind` | sim | `station` | | Faz do arquivo uma estação |
| `parent` | sim | nome | | O planeta ou lua que ela orbita: um alcançável que uma dimensão do pacote cria. A estação de uma lua fica no mapa ao lado da lua, ao redor do planeta da lua |
| `tier` | não | int | o do pai | O nível de foguete que alcança a estação |
| `showName` | não | booleano | `false` | Ativado, o mapa lista cada estação sob o `name` deste arquivo onde o Galacticraft escreve `Station: <owner>`; a linha do dono permanece. Uma estação que o dono renomeou mantém o nome do dono |
| `recipe` | não | objeto de ingrediente e quantidade | a receita de estação do próprio Galacticraft | Quanto custa construir uma. Um ingrediente é um nome do dicionário de minérios, `modid:item` ou `modid:item:meta` |
| `checklist` | não | lista de strings | nenhum | Chaves de checklist do Galacticraft mostradas antes do lançamento |

As outras [chaves de mapa](#chaves-de-mapa) posicionam a estação no mapa. Nada mais do bloco `galacticraft` se aplica: uma estação tem o ar, a gravidade e a chegada próprios de estação do Galacticraft.

Fora do bloco, uma estação lê estas chaves de dimensão e nenhuma outra:

| Chave | Sem ela |
| --- | --- |
| `id` | Obrigatório. A estação usa este id e o seguinte |
| `sky.skyColor`, `sky.fogColor` | As cores de órbita do Galacticraft |
| `sky.starBrightness` | As estrelas de órbita do Galacticraft |
| `sky.sun`, `sky.bodies`, `sky.stars` | O céu de órbita do Galacticraft, com o seu pai abaixo |
| `sky.renderSky`, `sky.renderClouds`, `sky.renderWeather` | `true` |
| `time.dayLength` | `24000` |

**Ids.** `id` e `id + 1` são ids de tipo de dimensão, não a dimensão da estação; ambos precisam estar livres. Cada estação construída recebe o próximo id de dimensão livre no momento em que é construída, e o Galacticraft guarda esse id, o dono e o nome da estação no save do mundo, então a estação volta com o mesmo id após uma reinicialização. A sua pasta de salvamento é a do Galacticraft, `DIM_SPACESTATION<id>`.

**Um tipo de estação por corpo.** Um planeta ou lua que já tem uma estação, de outro pacote ou outro mod, a mantém: a segunda não é registrada, e o log avisa.

### GalaxySpace e ExtraPlanets

*corpos do Galacticraft*

`<namespace>/celestial/*.json` e o bloco `galacticraft` de `<namespace>/dimensions/*.json`

Dois complementos do Galacticraft leem mais sobre um corpo do que o Galacticraft. Um objeto `galaxyspace`, em um planeta ou lua de `celestial/`, na `star` de um sistema ou no bloco `galacticraft` de uma dimensão, tem efeito quando o GalaxySpace está instalado. Um objeto `extraplanets`, somente no bloco `galacticraft` de uma dimensão, tem efeito quando o ExtraPlanets está instalado. Sem o complemento, o seu objeto não faz nada e o log avisa uma vez para o corpo, que ainda é criado como um corpo comum do Galacticraft. Um planeta pode orbitar um sistema do GalaxySpace, como `tauceti`, `barnards`, `acentauri` ou `proxima`, com ou sem esses objetos.

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

A `gravity` e o `dayLength` de uma dimensão também são o que o GalaxySpace mostra para o corpo e usa nele; não precisam de chave própria.

| Chave de `galaxyspace` | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `pressure` | não | float | `0` | Pressão do ar. Acima de `10` causa náusea, acima de `25` lentidão, acima de `35` cegueira e acima de `45` dano, a menos que a armadura do GalaxySpace ou a sua config o impeça |
| `radiation` | não | booleano | `false` | Radiação solar, que se acumula em um jogador sob a luz do dia e céu aberto, a menos que a sua armadura o proteja |
| `class` | não | `selena`, `desert`, `terra`, `oceanide`, `gasgiant`, `icegiant`, `asteroid`, `titan`, `iceworld` | nenhum | A classe de planeta que o mapa e o livro-guia do GalaxySpace mostram |
| `orbitEccentricity` | não | `[x, y]` | `[0, 0]` | Estica a órbita que o mapa do GalaxySpace desenha ao longo de cada eixo. `0` ou menos deixa esse eixo redondo |
| `orbitOffset` | não | `[x, y]` | `[0, 0]` | Move o centro dessa órbita |
| `freezeBlocks` | não | booleano | `true` | Se o metano líquido e o hélio-hidrogênio do GalaxySpace colocados fora de ar selado viram fogo sob calor intenso ou somem sob frio intenso; a água nunca é alterada |
| `thermalVariation` | não | float | `0` | Quanto o nível térmico oscila entre o meio-dia e a meia-noite, como uma fração de `atmosphere.temperature`, ou por esse valor quando ela é `0`. Precisa do sistema térmico avançado do GalaxySpace |
| `solarWind` | não | float | o tamanho da estrela ao quadrado | Produção dos painéis de vento solar do GalaxySpace aqui |
| `weather` | não | `dust_storm`, `frozen_storm`, `lightning_storm`, `meteoric_rain` | nenhum | Uma tempestade que vem e vai. Uma tempestade de poeira fere quem estiver sob céu aberto, a chuva de meteoros derruba meteoros, uma tempestade de raios lança raios |
| `weatherFrequency` | não | float | `1` | Com que frequência uma tempestade de raios atinge |
| `starType` | não | `subdwarf`, `dwarf`, `subgiant`, `giant`, `supergiant`, `hypergiant`, `blackhole` | nenhum | O tipo de uma estrela, mostrado no mapa do GalaxySpace |
| `starColor` | não | `brown`, `red`, `orange`, `yellow`, `white`, `lightblue`, `blue`, ou uma classe `M1` a `O3` | nenhum | A classe de cor de uma estrela, mostrada no mapa do GalaxySpace |
| `habitableZone` | não | `[distance, width]` | `[0, 0]` | A faixa ao redor de uma estrela que o mapa do GalaxySpace marca como habitável |

`starType`, `starColor` e `habitableZone` são lidos apenas na `star` de um sistema; os demais, apenas em um planeta ou lua. O GalaxySpace desenha as suas tempestades por planeta, então a tempestade de um pacote age, mas não mostra um céu próprio.

| Chave de `extraplanets` | Obrigatório | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `pressure` | não | int, `0` a `100` | nenhum | Pressão do ExtraPlanets. Acima de `0` fere um jogador sem traje espacial do ExtraPlanets e aparece no seu HUD |
| `radiation` | não | int, `0` a `100` | o padrão do ExtraPlanets para outros complementos | Radiação do ExtraPlanets, que se acumula em um jogador cujo nível de traje não a cobre |
| `temperatureDay` | não | float | `atmosphere.temperature` | O nível térmico de dia, na escala do ExtraPlanets, que vai de cerca de `-140` a `100`. Precisa da opção de isolamento térmico dos níveis 3 e 4 do ExtraPlanets |
| `temperatureNight` | não | float | `temperatureDay` | O nível térmico de noite |
| `lander` | não | `general`, `jupiter`, `saturn`, `mercury`, `neptune`, `uranus` | o lander do Galacticraft | O lander do ExtraPlanets em que um jogador desce, onde `landing` é `lander` |

## Portais e passagens

*o mundo*

`<namespace>/blocks/*.json`

Um portal é uma definição de bloco comum, então vale a mesma regra de caminho: o caminho do arquivo é o nome de registro do bloco.

Um bloco `portal` traz uma seção `portal`:

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `dimension`       | sim | int        |                        | Para onde ele envia você |
| `returnDimension` | não | int        | `0`                    | Para onde ele envia você de volta |
| `gate`            | não | nome de passagem | nenhum           | Uma passagem que precisa estar aberta para atravessar |
| `cooldown`        | não | int, ticks | `60`                   | Tempo até o mesmo jogador poder usá-lo de novo |
| `platform`        | não | booleano   | `true`                 | Constrói uma plataforma de pouso na chegada |
| `platformBlock`   | não | nome de bloco | a própria moldura do portal | De que a plataforma é feita |
| `sound`           | não | nome de som | nenhum                | Tocado ao atravessar |
| `owned`           | não | booleano   | `true`                 | Só quem o construiu, e quem essa pessoa permitir, pode usá-lo. Um portal com dono também é imune a explosões |
| `walkIn`          | não | booleano   | `false`                | Entrar andando no bloco viaja, como num portal do Nether. Desligado, ele é usado com a mão |

### Molduras de portais

*portais e passagens*

`<namespace>/portalframes/*.json`

O caminho do arquivo é o nome de registro da moldura, que uma dimensão então cita em `frames`.

Uma moldura é o desenho do que o jogador precisa construir, e nada mais: ela diz quais blocos formam a borda e onde fica o vão, e não diz nada sobre para onde o portal leva. Isso é proposital, porque uma dimensão reivindica uma moldura em vez de possuí-la, e duas dimensões podem reivindicar a mesma.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `name`      | não | string                             | o nome do arquivo | O nome usado no log |
| `axis`      | não | `vertical`, `horizontal` ou `both` | `vertical`        | Se ela fica em pé como um portal do Nether, deitada como um portal do End, ou pode ser das duas formas |
| `legend`    | sim | objeto de um caractere para um bloco | nenhum          | Os blocos que as linhas podem usar. Um nome de bloco com estados é lido da mesma forma que em qualquer outro lugar |
| `rows`      | sim | lista de strings                   | nenhum            | O desenho, com a linha de cima primeiro |
| `maxWidth`  | não | int                                | `21`              | O vão mais largo até onde um `*` pode se esticar |
| `maxHeight` | não | int                                | `21`              | O vão mais alto até onde um `*` pode se esticar |

Três caracteres não são blocos. `.` é o vão em que o portal fica, e uma moldura sem ele é recusada. Um espaço é uma célula com a qual a moldura não se importa, então um contorno em formato de L é desenhado deixando os cantos em branco. `*` repete: uma linha composta só de `*` repete a linha acima quantas vezes o jogador construir, e um `*` dentro de uma linha repete o caractere anterior da mesma forma. Ele pode repetir zero vezes, então o desenho lido com todos os `*` riscados é a menor coisa que acende, e os máximos abaixo são a maior. Um desenho sem nenhum `*` é exato, e o jogador deve construir exatamente isso e nada além.

Uma moldura vertical é encontrada em qualquer um dos eixos horizontais e de qualquer lado, então não importa para onde o construtor estava virado. Uma horizontal é encontrada nas quatro rotações.

**Quão grande ela pode ser é decisão do pacote.** `maxWidth` e `maxHeight` são o maior vão até onde um `*` se esticará, e qualquer coisa menor, até o mínimo, é aceita, então o pacote decide se sua passagem chega no máximo ao 21 do vanilla ou a 4. O mínimo é um jogador: uma moldura em pé é recusada a menos que seu vão possa ter pelo menos 1 de largura e 2 de altura, uma deitada pelo menos 1 por 1, e um desenho que nunca alcança isso é recusado no carregamento com uma linha no log, em vez de virar uma moldura pela qual ninguém consegue passar.

**Uma moldura custa mais para ser procurada quanto mais ela pode se esticar.** Um `*` de linha e um `*` de coluna juntos significam que toda combinação até os dois máximos é tentada, então uma moldura que se estica nos dois sentidos até 21 são 441 desenhos. A busca desiste em vez de travar, e avisa no log, o que é o sinal para reduzir um máximo ou abrir mão de um dos esticamentos.

**Nada impede que uma moldura seja de obsidiana acesa com isqueiro, mas ela tem precedência.** Uma moldura é procurada antes de o item fazer o seu próprio trabalho, então tal moldura abre a dimensão do pacote onde um portal do Nether estaria. Escolha outro bloco ou outro acendedor para deixar o portal do vanilla em paz.

### Abrindo uma dimensão com uma moldura

*portais e passagens*

`<namespace>/dimensions/*.json`

Uma dimensão se abre por uma moldura ao trazer uma seção `portal`. A moldura e o que a acende, juntas, escolhem a dimensão, então um mesmo formato de moldura pode levar a vários lugares conforme aquilo com que foi acesa.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `frames`        | sim | lista de nomes de moldura   | nenhum                      | As molduras que abrem esta dimensão |
| `ignitedBy`     | não | nome de item                | `minecraft:flint_and_steel` | O que o jogador segura para acender uma |
| `color`         | não | cor hexadecimal             | branco                      | A cor em que o portal é desenhado |
| `return`        | não | `built`, `player` ou `none` | `built`                     | Se um caminho de volta é fornecido, construído pelo jogador, ou nenhum |
| `gate`          | não | nome de passagem            | nenhum                      | Uma passagem que precisa estar aberta para atravessar |
| `cooldown`      | não | int, ticks                  | `60`                        | Tempo até o mesmo jogador poder atravessar de novo |
| `platform`      | não | booleano                    | `true`                      | Constrói uma plataforma de pouso na chegada |
| `platformBlock` | não | nome de bloco               | pedra                       | De que a plataforma é feita |
| `sound`         | não | nome de som                 | nenhum                      | Tocado ao atravessar |
| `owned`         | não | booleano                    | `false`                     | Só quem o acendeu, e quem essa pessoa permitir, pode usá-lo |

O bloco que fica no vão não é escrito pelo pacote. Uma dimensão com uma seção `portal` ganha um próprio, chamado `<namespace>:portal_<dimension>`, desenhado com a textura de portal do próprio jogo sob `color`, atravessado ao andar para dentro em vez de usado com a mão, e inquebrável. A cor multiplica a textura, como um `tintindex` faz, então `#C77DFF` mantém o violeta do Nether e `#4CFFB0` o torna venenoso. Para um portal que não use a textura do vanilla de forma alguma, escreva um bloco `portal` comum seu, com modelo próprio e uma textura desenhada como [mapa de pixels](#texturas-escritas-como-mapas-de-pixels), onde `tint` pode fazer uma transição entre duas cores.

`return` decide o que acontece do outro lado. `built` ergue a mesma moldura, no tamanho que o jogador construiu, e a acende, que é como o vanilla se comporta. `player` não constrói nada, mas permite que a mesma moldura seja acesa lá, então o caminho de casa precisa ser encontrado e feito. `none` recusa acender a moldura naquela dimensão, e a viagem é só de ida.

**Uma moldura, várias dimensões.** O par formado por uma moldura e o item que a acende é o que escolhe a dimensão, então o mesmo `standing_gate` aceso com isqueiro e aceso com um acendedor próprio do pacote abre dois lugares diferentes, cada um com sua cor. Duas dimensões reivindicarem a mesma moldura *e* o mesmo item é um erro do pacote: a segunda é recusada e isso é dito no log, em vez de uma delas vencer em silêncio.

Quebrar qualquer bloco da moldura apaga o portal, como no vanilla.

### Portais

*portais e passagens*

`<namespace>/gates/*.json`

O caminho do arquivo é o nome de registro da passagem, que um portal então cita em `gate`.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve só as de que precisa.

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

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `dimension`       | sim | int                                |                                  | A dimensão que ela guarda |
| `name`            | não | string                             | o nome do arquivo                | Mostrado ao jogador |
| `scope`           | não | `player`, `global`                 | `player`                         | Um jogador por vez, ou o mundo inteiro de uma vez |
| `open`            | não | booleano                           | `false`                          | Se ela começa aberta |
| `unlock`          | não | objeto                             |                                  | O que a abre. Veja abaixo |
| `unlockedMessage` | não | string                             | `%dim% is now open`              | Mostrada quando ela abre |
| `blockedMessage`  | não | string                             | `You need %item% to enter %dim%` | Mostrada quando ela recusa |
| `safeReturn`      | não | booleano                           | `false`                          | Um retorno bloqueado ainda pousa em um lugar seguro em vez de recusar |
| `requires`        | não | lista de ids de mods ou namespaces de pacotes | nenhum                | A passagem é ignorada a menos que todos estejam presentes |
| `portalBlocks`    | não | lista de nomes de blocos           | todo portal                      | Limita a passagem a estes blocos de portal, de modo que uma dimensão pode ter uma porta guardada e uma aberta |

`unlock` aceita `hold` (um item que precisa estar na mão), `consume` com `consumeCount` (`1`), `craft` (um item que precisa ter sido fabricado), `advancement` e `killed` (um nome de entidade; a passagem se abre para quem matar uma, de modo que um chefe pode guardar a chave de um mundo) com `killedCount` (`1`) quando uma não basta, contabilizado por jogador ou para o mundo inteiro conforme o escopo. Adicionar `killedDrops` (um nome de item) faz as mortes contadas largarem esse item aos pés de quem matou em vez de abrir a passagem, e zera a contagem, de modo que uma chave pode ser conquistada de novo e entregue a alguém que nunca lutou por ela; use `hold` ou `consume` do mesmo item como condição para torná-lo a chave. `%item%`, `%mob%` e `%dim%` são preenchidos para você. Uma chave que um mob solta não precisa de nada especial aqui: dê o drop ao mob e use `hold` ou `consume` como condição.

## Mundos Rubic

*o mundo*

`rubicWorld` nas configurações de `terrain` reconstrói o mundo de uma dimensão com cubos de 16×16×16 em vez de colunas de 256 blocos, de modo que seu piso e seu teto podem ficar onde o pacote quiser. A geração de terreno em si não muda — o gerador do vanilla e a worldgen de outros mods rodam como de costume e produzem a mesma terra; simplesmente há mundo acima e abaixo dela.

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

Todas as chaves ficam no grupo `terrain`, no bloco `settings` de um modelo de mundo como as demais:

| Chave | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| `rubicWorld`                       | booleano                         | `false` | Liga os mundos rubic |
| `worldMinHeight`                   | int, múltiplo de 16              | `-64`   | O piso do mundo |
| `worldMaxHeight`                   | int, múltiplo de 16              | `320`   | O teto do mundo |
| `rubicWorldDimensions`             | lista de ints                    | vazia   | Quais dimensões se tornam rubic. Vazia significa todas as dimensões |
| `rubicWorldDimensionsAreBlacklist` | booleano                         | `false` | Trata a lista como as dimensões a deixar em paz |
| `terrainOffset`                    | int, múltiplo não negativo de 16 | `0`     | Desloca toda a janela de terreno do vanilla para cima. Para predefinições em camadas simples: um mundo plano com `272` tem a superfície perto de y 275, acima do teto do vanilla. Decorações e estruturas que uma predefinição pede ainda geram em suas alturas sem deslocamento |

**Alturas.** `worldMinHeight` deve ser menor que `worldMaxHeight`, ambos múltiplos de 16, e ambos dentro do alcance que `rubicHeightLimit` na config permite (`4096` blocos para cada lado por padrão; só na config, nunca uma chave de pacote). Qualquer outra coisa é recusada com uma linha no log e o mundo é feito de `-64` a `320`. A altura custa espaço: cada 16 blocos é mais um cubo em cada coluna, então memória, disco e tempo de pré-geração crescem com ela — o comentário da config sobre `rubicHeightLimit` traz os números.

**A janela de terreno.** O gerador próprio da dimensão mantém sua própria altura, 256 blocos no overworld, e essa janela é o que `terrainOffset` desliza. `worldMinHeight` e `worldMaxHeight` acrescentam espaço ao redor da janela, nunca dentro dela. Subir o teto não ergue a terra, acrescenta céu; baixar o piso não aprofunda as cavernas que o gerador cavou, acrescenta mundo profundo. O nível do mar também fica dentro da janela, então acompanha o `terrainOffset` e vem do tipo de mundo, e não de qualquer chave rubic. Para pôr a superfície mais alto no mundo, aumente `terrainOffset`. Para pôr mais espaço acima ou abaixo dela, mova as alturas. Todo cubo fora da janela ainda é gerado e iluminado em cada coluna, então um teto mais alto custa tempo de pré-geração, quer algo o preencha ou não, e custa memória e disco além disso quando `skyStone` o preenche.

**O que uma janela deslocada faz com outros mods.** A população roda na coluna, então todo gerador que um mod registra ainda roda uma vez por chunk, sem nenhuma coordenada traduzida. O que muda é onde cai a conta do próprio gerador. Um gerador que pergunta ao mundo onde está o chão, pelo bloco sólido mais alto ou pela altura de precipitação, acompanha o terreno deslocado: ambos reconhecem rubic, o que cobre árvores, flores e a maior parte da decoração. Um gerador que calcula uma altura absoluta, entre eles o padrão usual de minérios com y aleatório abaixo de 64, continua escrevendo nessa altura, que depois de um deslocamento é o enchimento ou o mundo profundo bem abaixo da terra. O nível do mar também não é deslocado, então um gerador que o testa lê o número sem deslocamento. Essas escritas também caem fora dos cubos que a população mantém carregados, e puxam cubos próprios enquanto uma coluna é populada. Um `terrainOffset` grande serve a um pacote que descreve a própria geração, não a um empilhado sobre a worldgen de outro pacote. Só por espaço, a profundidade é a direção mais barata: abaixo da janela fica um gerador completo com sua própria pedra, cavernas, veios, aquíferos e masmorras, e ele deixa a superfície nas alturas que todo outro gerador presume, enquanto o espaço acima da janela é cenário que o pacote precisa mobiliar sozinho.

**Decidido por save, uma vez.** Se uma dimensão é rubic e quais são suas alturas é gravado no save dela na primeira vez em que carrega, e vale a partir daí: um mundo rubic continua rubic mesmo com o pacote removido, e suas alturas não podem ser mudadas depois. Dimensões diferentes do overworld adotam as alturas do overworld. Terra Anvil existente não é convertida — o rubic guarda sua terra em arquivos `region2d`/`region3d` próprios, então uma dimensão que já foi gerada como Anvil recomeça seu terreno. Ligue-o para mundos novos.

**Excluindo dimensões.** Uma dimensão deixada de fora de `rubicWorldDimensions` mantém seu mundo Anvil comum, no mesmo save — dimensões rubic e Anvil se misturam livremente. É a escolha certa para dimensões cujos geradores escrevem nos internos do chunk em vez de passar pelo ciclo de população comum. Independentemente da lista, um mundo cujas classes de servidor outro mod substituiu é ignorado, com uma linha no log dizendo isso.

**Espaço fora da janela.** O alcance do próprio gerador mantém sua forma usual, e o espaço que um mundo rubic acrescenta ao redor dele é preenchido com o bloco em que esse alcance termina: pedra sob o overworld, ar sobre ele. Uma dimensão cujo topo é selado com bedrock, o Nether acima de todas, conta como fechada, então o espaço acima dela fica vazio em vez de cheio do netherrack sob o teto. O teto em si não é tocado. `deepStone` nomeia o bloco do espaço abaixo da janela, `skyStone` o bloco do espaço acima dela.

**CubicChunks.** Rodar os dois não é suportado. Com o CubicChunks instalado e um pacote pedindo `rubicWorld`, o carregamento para com uma mensagem: remova o CubicChunks, ou tire `rubicWorld` do pacote e deixe o CubicChunks criar os mundos.

### Movendo um mundo para o CubicChunks e de volta

*mundos Rubic*

**Nomes de arquivos.** Um mundo rubic guarda suas colunas em `region2d/<x>.<z>.2rdr` e seus cubos em `region3d/<x>.<y>.<z>.3rdr`. Uma entrada grande demais para seu arquivo de região vai para uma pasta ao lado, com o nome do arquivo acrescido de `.ext`. Mundos feitos antes de esses nomes existirem usavam `.2dr` e `.3dr`: o mod renomeia esses arquivos e pastas sozinho quando uma dimensão carrega, com uma linha de log por dimensão, então um mundo mais antigo não precisa de nada feito à mão.

**Abrindo um mundo CubicChunks.** Quando um pacote pede `rubicWorld` e o mundo que está sendo aberto é marcado como mundo CubicChunks, o mod pergunta antes de fazer qualquer coisa, da mesma forma que o Forge pergunta sobre entradas de registro ausentes: uma tela de confirmação no modo um jogador, e em um servidor dedicado uma mensagem no console respondida com `/fml confirm` ou `/fml cancel`, ou de antemão com `-Dfml.queryResult=confirm`. Em caso de sim, o backup de mundo do Forge é gravado como um zip na pasta saves e o mundo é convertido para um mundo rubic no próprio lugar, e então o carregamento continua. Em caso de não, o carregamento para e nada no mundo é alterado.

**O conversor.** A mesma conversão, e o caminho de volta, pode ser executada fora do jogo. O repositório traz [`scripts/convert_rubic_world.py`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/scripts), que transforma um mundo rubic em um mundo CubicChunks, ou um mundo CubicChunks em um rubic. Ele precisa de Python 3 e de mais nada. Feche o jogo e faça backup do mundo primeiro, depois olhe a simulação antes de executá-lo de verdade.

```
python3 scripts/convert_rubic_world.py to-cubic "saves/My World" --dry-run
python3 scripts/convert_rubic_world.py to-cubic "saves/My World"
```

| Argumento | O que faz |
| --- | --- |
| `to-cubic`       | Um mundo rubic vira um mundo CubicChunks |
| `to-rubic`       | Um mundo CubicChunks vira um mundo rubic |
| `<world folder>` | A pasta do save, a que contém `level.dat` |
| `--dry-run`      | Mostra cada mudança e não faz nenhuma |

**O que ele altera.** Em todas as dimensões, os arquivos de região e suas pastas `.ext` assumem os nomes do outro lado (`.2rdr` e `.3rdr` para rubic, `.2dr` e `.3dr` para CubicChunks), e `data/rdplRubicData.dat` vira `data/cubicChunksData.dat` ou o contrário, com as alturas mantidas e o formato de armazenamento e o gerador de compatibilidade nomeados como o outro mod os nomeia. Por último, o marcador em `level.dat` e `level.dat_old` é trocado entre `isRubicWorld` e `isCubicWorld`. Os cubos e colunas em si não são reescritos.

**O que ele recusa.** Um mundo cujo marcador em `level.dat` não corresponde à direção pedida, um formato de armazenamento ou gerador de compatibilidade que o outro mod não tem, e uma renomeação cujo destino já existe. Uma recusa não altera nada, e uma execução que foi interrompida pode ser repetida.

**O que não é transportado.** Um mundo convertido contém só o que os dois mods entendem. Blocos e dimensões que um pacote definiu não existem no CubicChunks puro, e cada mod recalcula a luz dos cubos que o outro salvou.

### Streaming de cubos

*mundos Rubic*

**Streaming de cubos.** Quatro chaves de `chunks` decidem como os cubos chegam a um jogador e quando são liberados de novo. Só fazem algo em um mundo rubic, e os padrões são os números em que o subsistema foi ajustado, então um pacote que os deixa em paz não paga nada.

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

| Chave | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| `verticalCubeLoadDistance` | int, cubos        | `8`     | Quantos cubos acima e abaixo de um jogador um ticket de carregamento de chunk mantém. O controle deslizante de mesmo nome nas configurações de vídeo é a distância de visão do próprio cliente, definida por quem joga e não por um pacote |
| `cubesSentPerTick`         | int, cubos        | `649`   | Quantos cubos um jogador pode receber em um tick. Aumentá-lo preenche uma bolha de visão mais rápido e deixa os pacotes de cada tick maiores; um pacote ainda é dividido em 1024 cubos ou 512 KB, o que vier primeiro |
| `cubeGenMillisPerRound`    | int, milissegundos | `50`   | Quanto tempo um tick pode gastar gerando os cubos que os jogadores estão esperando |
| `cubeGCInterval`           | int, ticks        | `200`   | Com que frequência os cubos que ninguém está observando são liberados |

**Cliente.** As configurações de vídeo ganham um controle deslizante de distância de renderização vertical, o análogo vertical da distância de renderização (`verticalCubeLoadDistance` na config, que pertence a quem joga). Todo o resto do grupo `terrain` — pré-geração, física do mundo, spawn, borda — vale para mundos rubic sem mudanças.

## O mundo profundo

*o mundo*

Nove chaves `terrain` a mais preenchem o espaço que um mundo rubic abre ao redor da janela de terreno do vanilla com geração ao estilo moderno. Só fazem algo em um mundo rubic:

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

| Chave | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| `deepStone`    | `namespace:block`, meta como `@meta`      | nenhum    | O bloco de que é feito o mundo abaixo da janela, como o deepslate próprio de um pacote. Ele se mistura à pedra da janela ao longo das oito camadas mais baixas da janela, como as versões modernas misturam o deepslate |
| `skyStone`     | `namespace:block`, meta como `@meta`      | nenhum    | O bloco de que é feito o mundo acima da janela sob sua superfície, moldado em terra flutuante pelo mesmo ruído que escava o mundo profundo abaixo, de modo que o que lá embaixo é caverna aqui em cima é ilha. Vazio deixa o espaço acima da janela vazio, como sempre foi. A terra leva a superfície da própria coluna, o bloco do topo e os três abaixo dele tirados do bioma, então uma ilha do overworld lê-se como grama sobre terra sobre este bloco. Um bioma ou uma região de caverna pode nomear seus próprios `skyStone`, `skyIslands` e `skyThickness`, de modo que uma faixa ou uma região carrega ilhas próprias, resolvido por coluna com a região vencendo a faixa e a faixa vencendo o bioma. As ilhas são decoradas por mérito próprio: cada cubo acima da janela executa as features do próprio bioma contra a superfície dentro desse cubo, então árvores, grama, flores, cogumelos, juncos e manchas caem sobre a ilha em vez de serem espalhados pela coluna como o vanilla os coloca. Uma faixa de bioma lá em cima decora com suas próprias contagens. As adições próprias de um bioma também rodam lá, não só as compartilhadas: poços de deserto, melões de selva, a copa densa e os cogumelos de uma floresta sombria, rochas de taiga, espetos de gelo nos biomas gelados, e as flores e gramas altas que cada bioma coloca. Uma ilha nunca é feita de um bloco que cai: onde a superfície de um bioma seria areia ou cascalho a ilha usa arenito, ou seu próprio `skyStone` para qualquer outra coisa, já que nada sustenta um bloco que cai no ar. Os rebanhos são colocados da mesma forma, por cubo, então os animais começam nas ilhas à medida que a terra é feita. A profundidade da superfície varia de um a quatro blocos de enchimento com o ruído, então a borda de uma ilha não é uma crosta uniforme, e a crosta é medida ao longo da inclinação e não reto para baixo, então uma face íngreme mantém seu solo em vez de afinar até nada. A superfície segue o bioma que o próprio céu informar, a região de caverna primeiro, depois um bioma em faixa de altura, depois a coluna abaixo, então nomear `minecraft:mesa` em uma região do céu dá ilhas de argila em faixas em qualquer altura, as mesmas faixas que o chão tem, e nomear um deserto dá sua areia, transformada em arenito porque nada sustenta um bloco que cai. Os animais se estabelecem nela, o que `skyAnimals` no grupo `spawning` desliga. Quanto do céu vira terra é `skyIslands`, e seu padrão de 0.5 é um arquipélago: em um mundo gerado, deixa cerca de sete em cada oito cubos acima da janela vazios e sua camada mais cheia fica perto de um terço, então o céu é sobrevoado e não percorrido a pé. Baixe-o em direção a 0.2 e a faixa se fecha em um teto ondulado com colinas sobre ele, cerca de quatro quintos sólido no meio, o que é algo sobre o qual construir, mas já não são ilhas. As ilhas param oito blocos antes de `worldMaxHeight`, então um topo nunca é cortado reto contra o teto e árvores e plantas têm espaço acima dele; `caves` preenche até o teto como antes. Toda dimensão rubic tem uma janela própria, então isto preenche o espaço acima de cada uma: no Nether, cuja janela tem 128 de altura, é o espaço acima do teto de bedrock, e uma fenda que abre o teto limpa o próprio teto |
| `skyShape`     | `islands` ou `caves`                      | `islands` | Em que se molda o mundo acima da janela. `islands` é terra flutuante. `caves` é rocha sólida com cavernas escavadas através dela, o tratamento do próprio mundo profundo virado para cima, que sai cerca de 86 por cento sólido, a mesma proporção de rocha para caverna do mundo profundo. Nada inunda em nenhum dos casos, já que nenhum aquífero é consultado acima da janela. Só é lido quando `skyStone` nomeia um bloco |
| `skyIslands`   | número, de `-1` a `1`                     | `0.5`     | Com que facilidade o céu se reúne em ilhas. Menor espalha ilhas por mais do céu e aprofunda a sombra sob elas, maior deixa peças menos numerosas e menores. O padrão deixa cerca de sete em cada oito cubos vazios e tem pico perto de um terço; por volta de `0.2` a faixa se fecha em um teto com colinas, cerca de quatro quintos sólido no meio. Só é lido quando `skyStone` nomeia um bloco e `skyShape` é `islands` |
| `skyThickness` | número, `0` ou mais                       | `2.0`     | Quão sólida é uma ilha. Maior preenche as ilhas, menor as esvazia e afina suas bordas até nada. Só é lido quando `skyStone` nomeia um bloco e `skyShape` é `islands` |
| `skyHeights`   | dois ints, o mais baixo e depois o mais alto | nenhum | O bloco mais baixo e o mais alto que uma ilha pode alcançar, contados a partir do fundo da janela como as alturas de `oreVeins`. Vazio preenche todo o mundo acima da janela, que em um mundo alto é muito céu. Só é lido quando `skyStone` nomeia um bloco |
| `noiseCaves`   | `off`, `deep`, `world`                    | `off`     | Cavernas de ruído ao estilo moderno: cavernões de queijo, túneis de espaguete, bocas de caverna perto da superfície e pilares nas salas grandes. `deep` escava só abaixo da janela, `world` escava o mundo inteiro |
| `deepRavines`  | booleano                                  | `false`   | Corta ravinas ao estilo vanilla pelo mundo abaixo da janela, cânions longos e íngremes. Uma ravina assume os fluidos próprios do mundo profundo por onde passa, enchendo-se de lava abaixo da linha de lava e mantendo a água de um aquífero ou sua parede de pressão acima dela, então nunca drena o que corta. As versões modernas escavam seus cânions só dentro da janela, então o mundo profundo não tem nenhum a menos que isto esteja ligado |
| `oreVeins`     | lista de `ore,extra,filler,lowest,highest` | nenhum   | Grandes veios de minério em faixas, na maior parte o bloco `filler` com o `ore` espalhado nele e uma chance rara do `extra`, que pode ficar vazio. As alturas contam a partir do fundo da janela, então valores negativos alcançam o mundo profundo |

Água e lava se comportam lá embaixo. Lava a granel preenche as camadas mais baixas, e as cavernas acima carregam aquíferos locais — o mesmo esquema de pontos de amostra e pressão que as versões modernas usam, portado da 26.1.2 — então bolsões de água parada ficam em seus próprios níveis, com paredes da pedra profunda moldadas por ruído onde quer que dois níveis se encontrem ou a água encontre a lava. Sob oceanos as cavernas inundam em direção ao nível do mar, como as versões modernas atrelam seus aquíferos à superfície.

**Por dimensão.** `deepStone`, `noiseCaves`, `skyStone`, `skyShape`, `deepRavines` e `oreVeins` aceitam cada um um único valor ou uma lista, e uma entrada de lista escrita como `dimension=value` vale só para aquela dimensão. Onde qualquer entrada nomeia uma dimensão, essas entradas decidem por completo e as sem nome são ignoradas ali, então `"1="` sem nada depois desliga a chave para aquela dimensão. Um valor sem dimensão alcança toda dimensão rubic exceto o End, que continua vazio a menos que um pacote o nomeie: preencher o End seria o fim do End, e um End preenchido também cega a busca do portal do vanilla, que anda de volta a partir de 1024 blocos enquanto continuar encontrando chunks com blocos. Nomeie-o e você recebe o que pediu.

Com `noiseCaves` ligado, o mundo profundo também sorteia salas de monstros ao estilo moderno — cerca de quatro tentativas por coluna de chunk abaixo da janela, nenhuma a menos de seis blocos do piso do mundo — então spawners de masmorra e o loot de seus baús aparecem nas cavernas profundas como nas versões modernas.

O escopo `world` também aposenta dois resquícios do vanilla que brigariam com as cavernas reformuladas. A lava que o vanilla despeja em suas cavernas abaixo de y 10 passa a ser julgada pelo aquífero, então a velha janela de lava some, e os lagos de água enterrados do vanilla — lagoas de superfície incluídas — deixam de gerar, como as versões modernas os abandonaram; as poças próprias do aquífero tomam seu lugar.

O escopo `deep` deixa a faixa do vanilla como está, janela de lava incluída, e apenas sela a emenda onde os dois se encontram. Lava ou água na camada mais baixa da janela com uma caverna profunda aberta logo abaixo vira a pedra profunda, de modo que a janela não pode drenar para as cavernas abaixo.

## Regiões de cavernas

*o mundo*

`<namespace>/caveregions/*.json`

O caminho do arquivo é o nome da região, que uma entrada de worldgen então cita em `caveRegions`. Um nome sem namespace ali assume o namespace da própria entrada.

Pinta regiões nomeadas sobre o subsolo, a contrapartida em pacote dos biomas de caverna modernos. O subsolo é dividido em células arredondadas — `caveRegionCells` blocos de largura e `caveRegionCellsY` de altura, ambas chaves `terrain` — e cada célula sorteia uma região, ou nenhuma, por peso. Tudo o que uma região faz vem de forma determinística da seed, então os chunks concordam entre si sem nunca escrever além de uma borda.

### Arquivos de região

*regiões de cavernas*

Todas as chaves, mostradas de uma vez. Um arquivo real escreve só as de que precisa.

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

| Chave | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| `weight`            | int              | `1`                 | Parcela das células que esta região vence. `0` a desliga |
| `minHeight`         | int              | o piso do mundo     | Fundo da faixa em que a região existe |
| `maxHeight`         | int              | `48`                | Topo dessa faixa. Uma célula cujo centro fica fora da faixa nunca escolhe a região |
| `dimensions`        | lista de ints    | todas               | Em quais dimensões a região aparece |
| `floorCover`        | bloco            | nenhum              | Substitui o bloco do topo dos pisos de caverna dentro da região |
| `floorChance`       | 0.0 a 1.0        | `1.0`               | Quanto do piso é coberto |
| `ceilingCover`      | bloco            | nenhum              | Substitui os blocos do teto da caverna dentro da região |
| `ceilingChance`     | 0.0 a 1.0        | `1.0`               | Quanto do teto |
| `coverReplace`      | lista de blocos  | qualquer coisa parecida com pedra | O que as coberturas podem substituir |
| `waterLevel`        | int              | nenhum              | Fixa o nível de água de todo ponto de amostra de aquífero dentro da região, de modo que suas cavernas inundam até esta altura. As paredes onde a região encontra cavernas secas são moldadas pelo mesmo ruído de pressão dos aquíferos modernos, e a água nunca toca o piso de lava. Precisa de `noiseCaves` ligado |
| `spawns`            | lista            | nenhum              | Mobs que surgem dentro da região, as mesmas entradas que o `spawns` de um bioma aceita: `entity`, `type` (monster, creature, ambient ou water), `weight`, `min` e `max` para o tamanho do grupo. Sob a janela de terreno, um ponto que enxerga o céu fica a cargo do bioma, como as coberturas; acima da janela, onde a única terra é a geração do céu, a lista vale também a céu aberto |
| `keepDefaultSpawns` | booleano         | `false`             | Mantém a lista de spawns própria do bioma junto com a da região. Desligado, a lista da região a substitui por completo dentro da região |
| `structures`        | lista            | nenhum              | Uma estrutura colocada uma vez por célula de região, no coração da célula, encaixada no piso de uma caverna — como as versões modernas dão a um bioma de caverna seu marco. As entradas são modelos `namespace:name`, ou `{ "structure": "...", "weight": 3 }` para escolher entre vários |
| `structureChance`   | 0.0 a 1.0        | `1.0`               | A chance de cada célula da região realmente receber sua estrutura |
| `structureLoot`     | `namespace:path` | nenhum              | A tabela de loot de que todo baú dentro de uma estrutura colocada é preenchido na primeira vez em que é aberto |
| `biome`             | nome de bioma    | nenhum              | O bioma que a região informa dentro de seu volume, gravado no cubo como bioma 3D. Dá à região sua própria folhagem, cores de grama e água, música e sons ambientes, e deixa o peso de spawn do vanilla lê-lo. A superfície acima fica intacta, já que só as células que a região ocupa são gravadas |
| `skyStone`          | bloco            | a configuração do mundo | O bloco de que são feitas as ilhas do céu sob sua superfície dentro desta região, de modo que uma região carrega ilhas próprias |
| `skyIslands`        | `-1` a `1`       | a configuração do mundo | O limiar de ilhas dentro da região. Menor reúne mais terra |
| `skyThickness`      | `0` ou mais      | a configuração do mundo | Quão sólidas são as ilhas da região |
| `ambientSound`      | nome de som      | nenhum              | Um som tocado de vez em quando para um jogador dentro da região, como os biomas modernos adicionam seus próprios sons de caverna. Enviado pelo servidor só a esse jogador |
| `soundChance`       | 0.0 a 1.0        | `0.0111`            | A chance por tick de `ambientSound` tocar |
| `particle`          | nome de partícula | nenhum             | Uma partícula mostrada ao redor de um jogador dentro da região, um dos nomes de partícula do jogo como `dripWater`, `happyVillager` ou `depthsuspend`. Só o ar dentro da região a mostra |
| `particleChance`    | 0.0 a 1.0        | `0.00625`           | Densidade de partículas dos biomas modernos: a cada tick cerca de 667 pontos num raio de 16 blocos são tentados, e cada um mostra a partícula com esta chance |

### Células

*regiões de cavernas*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `caveRegionCells`       | int, blocos | `128`   | Quão larga é uma célula de região |
| `caveRegionCellsY`      | int, blocos | `64`    | Quão alta é uma célula de região |
| `caveRegionPlainWeight` | int         | `4`     | O peso do subsolo simples, sem região, no sorteio de cada célula. Maior deixa mais subsolo sem nenhuma região: com uma única região de peso 1, cerca de um quinto das células a recebem |

Quanto do subsolo continua simples é a chave `terrain` `caveRegionPlainWeight`, padrão `4`: com uma única região de peso 1, cerca de um quinto das células recebem a região. As coberturas se aplicam sob um teto, então uma região que alcança acima do solo nunca aparece na superfície; acima da janela de terreno elas também se aplicam a céu aberto, já que tudo lá em cima é terra que a geração do céu fez. As coberturas funcionam em toda caverna, seja qual for o gerador que a escavou; `waterLevel` é a única chave que precisa das cavernas de ruído, porque a inundação é colocada enquanto elas são escavadas.

### Características em uma região

*regiões de cavernas*

Features se ligam por duas chaves em [entradas de worldgen](#entradas-de-worldgen) comuns. `caveRegions` lista as regiões em que uma entrada pode gerar, verificadas na posição colocada, então cogumelos, cristais ou qualquer outra coisa aparecem só dentro de sua região. `snap` primeiro move cada tentativa na vertical até a superfície de caverna mais próxima: `floor` para o que fica em pé, `ceiling` para o que pende. Uma região ao estilo dripstone não precisa de formas novas:

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

O `replace` de `minecraft:air` importa: aquilo sobre o que uma forma colocada escreve é conferido com `replace`, cujo padrão é pedra, então qualquer coisa construída em espaço de caverna aberto precisa ter o ar listado. A mesma entrada com `"snap": "floor"` e sem `hanging` faz crescer as estalagmites correspondentes. O filtro de região funciona com toda forma colocada; `belt` e `field` colocam por suas próprias regras e o ignoram.

---

# Gerando o mundo

## Entradas de worldgen

*gerando o mundo*

`<namespace>/worldgen/*.json`

O caminho do arquivo nomeia a entrada, e as formas `belt` e `field` semeiam seu ruído a partir dele, então renomear um arquivo muda o que ele gera.

Descreve algo que gera. Toda entrada é uma **forma** colocada por uma **dispersão**, filtrada por onde é permitida.

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

Só `block` é obrigatória; todo o resto pode ser omitido e assume seu padrão. `blocks` substitui `block` quando um só não basta e tem seu próprio exemplo abaixo.

### O que coloca

*entradas de worldgen*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `block`    | sim | nome de bloco                  |                         | O que é colocado |
| `meta`     | não | int                            | `0`                     | Qual variante desse bloco |
| `blocks`   | não | lista de objetos               | nenhum                  | Uma lista ponderada, usada no lugar de um único bloco. Veja abaixo |
| `size`     | não | int ou intervalo               | `8`                     | Quantos blocos uma tentativa coloca, ou quão grande é uma forma com raio |
| `attempts` | não | int ou intervalo               | `8`                     | Quantas vezes por chunk ele tenta |
| `sparse`   | não | booleano                       | `false`                 | Espalha os blocos em vez de agrupá-los |
| `shape`    | não | objeto                         | `{ "type": "cluster" }` | A forma que ele assume. Veja [Formas](#formas) |
| `spread`   | não | objeto                         | `{ "type": "even" }`    | Onde é colocado. Veja [Dispersões](#dispersões) |
| `replace`  | não | lista de nomes de bloco ou objetos | `["minecraft:stone"]` | O que ele pode substituir. Veja abaixo |
| `adjacent` | não | lista de nomes de bloco ou objetos | nenhum              | Só coloca onde um destes estiver entre os 26 blocos que tocam o ponto. As mesmas três formas de `replace` |

### Onde pode gerar

*entradas de worldgen*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `minHeight`              | não | int                      | `0`                   | O menor y em que ele colocará |
| `maxHeight`              | não | int                      | `64`                  | O maior y em que ele colocará |
| `snap`                   | não | `floor` ou `ceiling`     | nenhum                | Move cada tentativa na vertical, primeiro, até o piso ou teto de caverna mais próximo |
| `snapDepth`              | não | int                      | `0`                   | Quão além da superfície `snap` então se move, para baixo a partir de um piso e para cima a partir de um teto. `0` fica no espaço aberto junto à superfície, `1` é o próprio bloco da superfície, `2` o que está atrás dele. O que ele pode sobrescrever continua regido por `replace`, então é assim que um pacote faz uma faixa de um bloco logo sob o chão em vez de sobre ele |
| `dimensions`             | não | lista de ints            | todas as dimensões    | Em quais dimensões ele roda |
| `dimensionsAreBlacklist` | não | booleano                 | `false`               | Transforma essa lista nas dimensões a evitar |
| `biomes`                 | não | lista de nomes de bioma  | todos os biomas       | Em quais biomas ele roda |
| `biomeTypes`             | não | lista de tipos do dicionário | nenhum            | Biomas por tipo, como `FOREST` ou `NETHER` |
| `biomesAreBlacklist`     | não | booleano                 | `false`               | Transforma essas listas nas que devem ser evitadas |
| `minTemperature`         | não | float                    | `-100.0`              | O bioma mais frio em que ele gerará |
| `maxTemperature`         | não | float                    | `100.0`               | O bioma mais quente em que ele gerará |
| `minRainfall`            | não | float                    | `-100.0`              | O bioma mais seco em que ele gerará |
| `maxRainfall`            | não | float                    | `100.0`               | O bioma mais úmido em que ele gerará |
| `minDistanceFromSpawn`   | não | int, blocos              | `0`                   | A que distância do spawn do mundo ele começa |
| `caveRegions`            | não | lista de nomes de região | nenhum                | Só gera dentro destas [regiões de cavernas](#regiões-de-cavernas) |

### Placas de superfície e seguidores

*entradas de worldgen*

| Chave | Obrigatória | Valor | Padrão | O que faz |
| --- | --- | --- | --- | --- |
| `indicators`      | não | lista de `block=weight`          | nenhum             | Blocos deixados espalhados na superfície sobre um veio que gerou, para que o jogador saiba o que há sob o chão; escolha-os de acordo com o conteúdo do veio. `empty=weight` deixa um ponto limpo |
| `indicatorCount`  | não | int ou intervalo                 | `1`                | Quantos pontos de superfície cada veio gerado recebe |
| `indicatorSpread` | não | int, blocos                      | `0`                | Quão além da pegada do veio um indicador pode cair |
| `then`            | não | lista de `name=weight` ou objetos | nenhum            | Entradas de worldgen que crescem a partir desta logo depois que ela gera, ligadas a ela: a origem do seguidor é posta logo fora da borda deste veio, na direção que `thenSpread` e `thenDepth` dão, de modo que os dois se tocam. Uma entrada é `name=weight`, ou um objeto com `name`, `weight` e seus próprios `spread` e `depth` (int ou intervalo) que substituem os do veio só para aquele seguidor, então uma lista pode mandar uma ponta de diamante para baixo e um ramo para o lado. Um nome simples é lido no namespace deste pacote, `empty=weight` não enfileira nada. Um seguidor mantém sua própria forma, blocos, tamanho e `replace`, mas pula suas próprias tentativas, chance, faixa de altura e filtros de bioma, e pode ele mesmo carregar `then`, tão fundo quanto o pacote quiser; uma entrada que já gerou na mesma cadeia a interrompe |
| `thenCount`       | não | int ou intervalo                 | `1`                | Quantos seguidores diferentes são escolhidos dessa lista por veio gerado, cada entrada no máximo uma vez, então uma contagem igual ao tamanho da lista faz crescer todos eles |
| `thenSpread`      | não | int, blocos                      | o raio da forma    | Quanto para os lados a direção em que um seguidor cresce pode se inclinar, sorteado de menos este valor a mais este valor |
| `thenDepth`       | não | int ou intervalo                 | `0`                | Quanto a direção se inclina para baixo (negativo) ou para cima. `0` sem inclinação lateral faz o seguidor pender reto para baixo |
| `prospectAs`      | não | string                           | o nome do arquivo  | Como um item de prospecção nomeia esta entrada em sua leitura, p. ex. `Hematite` |

### Retrogen e requisitos

*entradas de worldgen*

| Chave         | Obrigatório | Valor                                  | Padrão             | O que faz                                              |
| ------------- | ----------- | -------------------------------------- | ------------------ | ------------------------------------------------------ |
| `retrogen`    | não         | booleano                               | `false`            | Também gera em chunks que já existem                   |
| `retrogenKey` | não         | texto                                  | a chave da config  | Substitui a chave de retrogen apenas para esta entrada |
| `requires`    | não         | lista de ids de mods ou namespaces de pacotes | nenhum      | A entrada é ignorada, a menos que todos estejam presentes |

### Blocos ponderados

*entradas de worldgen*

`blocks` substitui `block` quando uma única entrada não basta. Os pesos são relativos, então 80 e 20 equivalem a quatro para um.

```json
{
  "blocks": [
    { "block": "minecraft:wool", "meta": 2, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "lime" } }
  ]
}
```

| Chave        | Obrigatório | Valor                            | Padrão | O que faz                                                                             |
| ------------ | ----------- | -------------------------------- | ------ | ------------------------------------------------------------------------------------- |
| `block`      | sim         | nome do bloco                    |        | O que é colocado                                                                      |
| `meta`       | não         | int                              | `0`    | Qual variante                                                                         |
| `weight`     | não         | int                              | `1`    | Com que frequência este é escolhido em relação aos outros                             |
| `properties` | não         | objeto de propriedade para valor | nenhum | Propriedades do estado do bloco por nome, para estados sem metadados próprios         |

`block` e `meta` continuam obrigatórios no nível superior do arquivo mesmo quando `blocks` é usado; a primeira entrada é um bom valor para colocar ali.

### Alvos de substituição

*entradas de worldgen*

`replace` é uma lista, e cada entrada aceita uma de três formas.

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

| Forma              | Exemplo                                                                   | O que corresponde                |
| ------------------ | ------------------------------------------------------------------------- | -------------------------------- |
| Nome               | `"minecraft:stone"`                                                       | Todos os estados desse bloco     |
| Nome e metadado    | `"minecraft:stone:3"`                                                     | Apenas esse metadado, aqui o diorito |
| Objeto             | `{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }` | Apenas esse estado               |

A forma de objeto também aceita `meta` no lugar de `properties`, o que equivale à forma com dois-pontos. Use `"minecraft:air"` para gerar em espaço aberto.

### Blocos adjacentes

*entradas de worldgen*

`adjacent` aceita as mesmas três formas que `replace` e acrescenta uma segunda condição: o ponto só é usado quando pelo menos um dos 26 blocos que o tocam, faces, arestas e cantos, corresponde à lista. Se omitido, nada é verificado.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

Isso coloca enxofre no arenito apenas onde ele já está aberto para uma caverna ou para a superfície, e deixa em paz o arenito enterrado. Vizinhos em chunks que ainda não existem são tratados como não correspondentes em vez de serem lidos, então a verificação nunca faz um chunk ser gerado.

Toda forma respeita essa condição, já que ela faz parte da decisão de poder ou não usar um único bloco. Um `geode` nomeia separadamente sua crosta e seu preenchimento, e esses dois são colocados sem a verificação.

Uma entrada que nomeia apenas blocos não registrados é ignorada com um erro, em vez de gerar em todo lugar.

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

| Chave    | Obrigatório | Valor                  | Padrão                    | O que faz                                                                                                          |
| -------- | ----------- | ---------------------- | ------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| `name`   | sim         | nome da entrada        |                           | A entrada de worldgen que cresce a partir desta. Um nome simples é lido no namespace deste pacote                  |
| `weight` | não         | int                    | `1`                       | Com que frequência este seguidor é escolhido em relação aos outros da lista                                        |
| `spread` | não         | int, blocos            | o `thenSpread` da entrada | Quanto a direção deste seguidor pode se inclinar para os lados, apenas para esta entrada                           |
| `depth`  | não         | int ou intervalo       | o `thenDepth` da entrada  | Quanto para baixo, negativo, ou para cima a direção deste seguidor se inclina, apenas para esta entrada            |

`name=weight` é a forma curta de um objeto com apenas esses dois, e `empty=weight` não enfileira nada. Como `spread` e `depth` valem por entrada, uma mesma lista pode mandar a ponta de um diamante direto para baixo e um ramo para o lado a partir do mesmo veio.

## Formas

*gerando o mundo*

Um bloco `shape` com um `type`. Chaves não listadas para um tipo são ignoradas por ele.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa. Uma chave marcada para um tipo é lida somente por esse tipo.

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

| Tipo         | O que produz |
| ------------ | ------------ |
| `cluster`    | O aglomerado padrão, um veio de minério. Usa `size` |
| `largevein`  | Um veio longo e sinuoso com ramificações. Usa `size` |
| `plate`      | Um disco plano |
| `geode`      | Uma bolsa oca com uma crosta |
| `decoration` | Dispersão na superfície, como flores ou cogumelos. Usa `size` |
| `tree`       | Uma árvore inteira |
| `vines`      | Trepadeiras sobre o que já existe. Usa `size` |
| `basin`      | Uma bacia que se aprofunda em direção ao meio |
| `spire`      | Uma coluna afilada |
| `nodule`     | Uma bola irregular |
| `vent`       | Uma coluna estreita que para ao encontrar algo |
| `imprint`    | Um dos seus modelos `.nbt`. Um que cabe dentro de um chunk é deslocado para ser colocado inteiro no chunk em construção, em vez de invadir um vizinho que ainda não foi criado, seja qual for o lado para o qual esteja virado; um maior que um chunk só é colocado onde o terreno ao redor já existe |
| `belt`       | Um aglomerado que abrange vários chunks, para regiões de pedra |
| `field`      | Veios calculados para todos os blocos de uma vez, compartilhando sua forma com os grupos de dureza |
| `vein`       | Um depósito calculado como um campo de ruído com semente em torno de uma origem, do jeito que o Immersive Geology faz: cada chunk grava a sua própria fatia de todo veio cujo alcance de 24 blocos o toca, então nada se propaga em cascata, e `/rdplserver vein` consegue dizer onde um veio estará antes de o terreno ser criado. Usa `size`, `attempts`, `rarity` e a faixa de altura; `pattern` escolhe a aparência |
| `spring`     | Um fluido vazando de uma parede de caverna: colocado onde há rocha acima, abaixo e em três lados, com um lado aberto, e posto para fluir |

### Tamanho e forma

*formas*

| Chave           | Usada por                                | Valor                        | Padrão                           | O que faz |
| --------------- | ---------------------------------------- | ---------------------------- | -------------------------------- | --------- |
| `type`          | todos                                    | uma das formas acima         | `cluster`                        | Qual forma |
| `radius`        | plate, geode, basin, spire, nodule, vent | int ou intervalo             | `6`                              | Quão larga é |
| `height`        | plate, geode, basin, spire, vent, tree   | int ou intervalo             | `1`, `8` para geode, `5` para tree | Quão alta ou espessa é |
| `width`         | geode                                    | int ou intervalo             | `12`                             | A extensão total da bolsa |
| `plane`         | plate, basin, spire, vent                | `circle`, `square`           | `circle`                         | Sua base |
| `slim`          | plate, largevein, nodule                 | booleano                     | `false`                          | Plate: uma camada mais fina. Largevein: ramos de um único bloco. Nodule: casca oca |
| `hanging`       | spire, vent                              | booleano                     | `false`                          | Cresce para baixo a partir de um teto em vez de para cima a partir de um chão |
| `taper`         | spire                                    | `straight`, `bell`, `needle` | `straight`                       | Como a largura diminui em direção à ponta. `straight` estreita de modo uniforme, `bell` mantém a largura embaixo e depois cai, `needle` afina de imediato até uma ponta longa |
| `outline`       | geode                                    | nome do bloco                | nenhum                           | O bloco da crosta |
| `fill`          | geode                                    | nome do bloco                | nenhum                           | O que preenche o meio. Se omitido, o meio fica oco |
| `middle`        | geode                                    | nome do bloco                | nenhum                           | Uma casca entre o corpo e `outline`, a calcita de um geodo de ametista moderno |
| `budding`       | geode                                    | nome do bloco                | nenhum                           | Colocado no lugar dos blocos do corpo voltados para o meio oco, como a ametista brotante. Requer `fill` |
| `buddingChance` | geode                                    | 0.0 a 1.0                    | `0.083`                          | Quantos desses blocos do corpo brotam |
| `crystal`       | geode                                    | nome do bloco                | nenhum                           | Crescido no oco ao lado de um bloco `budding`, como um aglomerado de ametista |
| `crystalChance` | geode                                    | 0.0 a 1.0                    | `0.35`                           | Em quantos desses pontos cresce um |
| `crack`         | geode                                    | 0.0 a 1.0                    | `0`                              | A chance de um geodo estar aberto: um tubo do meio para fora, atravessando todas as camadas de um lado, preenchido com `fill`. Os geodos de ametista modernos usam `0.95` |

### Posicionamento

*formas*

| Chave              | Usada por        | Valor                  | Padrão                | O que faz |
| ------------------ | ---------------- | ---------------------- | --------------------- | --------- |
| `surface`          | decoration, tree | lista de nomes de bloco | nenhum               | Sobre o que será colocado |
| `seeSky`           | decoration       | booleano               | `true`                | Só coloca onde o céu é visível |
| `checkStay`        | decoration       | booleano               | `true`                | Só coloca onde o bloco sobreviveria |
| `stackHeight`      | decoration       | int ou intervalo       | `1`                   | Quantos empilhar uns sobre os outros |
| `scatterX`         | decoration, tree | int                    | `8`                   | Quanto se desloca para os lados |
| `scatterY`         | decoration, tree | int                    | `4`                   | Quanto se desloca na vertical |
| `scatterZ`         | decoration, tree | int                    | `8`                   | Quanto se desloca para os lados |
| `rarity`           | qualquer         | int                    | nenhum (`400` para belt) | Uma colocação a cada tantos chunks. Em um belt, isso espaça os belts; em qualquer outra forma, controla a entrada inteira, de modo que apenas um chunk em tantos sorteia seus `attempts`. `field` a ignora |
| `rarityIsPerChunk` | qualquer         | booleano               | `false`               | Transforma `rarity` em quantas colocações cada chunk recebe |

### Árvores

*formas*

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

Uma `tree` sem `log` ou `leaves` não gera nada, e avisa isso no log. Nomear uma `structure`, ou várias em `structures`, planta esse modelo em cada ponto em vez de fazer crescer uma árvore, e então `log` e `leaves` não são necessários; uma árvore baseada em modelo lê `turns`, `mirrors`, `integrity`, `lootTable` e `locateAs` exatamente como um `imprint`.

| Chave    | Usada por | Valor         | Padrão  | O que faz                    |
| -------- | --------- | ------------- | ------- | ---------------------------- |
| `log`    | tree      | nome do bloco | nenhum  | O bloco do tronco            |
| `leaves` | tree      | nome do bloco | nenhum  | O bloco das folhas           |
| `vines`  | tree      | booleano      | `false` | Pendura trepadeiras nas folhas |

### Colocando modelos

*formas*

| Chave        | Usada por     | Valor                | Padrão  | O que faz |
| ------------ | ------------- | -------------------- | ------- | --------- |
| `structure`  | imprint, tree | `namespace:name`     | nenhum  | O modelo a colocar |
| `integrity`  | imprint, tree | 1 a 100              | `100`   | Porcentagem dos blocos do modelo que de fato aparecem |
| `lootTable`  | imprint, tree | `namespace:path`     | nenhum  | A tabela de saque da qual todo baú dentro do modelo colocado é preenchido na primeira vez que é aberto, e qualquer outro contêiner que aceite uma, incluindo uma caixa de shulker ou o engradado de um mod. Cobre `structure` e todas as entradas de `structures`; cada baú sorteia sua própria semente |
| `structures` | imprint, tree | lista                | nenhum  | Vários modelos para escolher, um colocado a cada vez. Cada entrada é `{ "structure": "namespace:name", "weight": 3 }`, ou um nome simples para chances iguais. Substitui `structure` |
| `turns`      | imprint, tree | lista                | qualquer | Em que sentido pode ser colocado: `none`, `quarter`, `half`, `threequarter`. As entradas podem ter um `weight`. Se omitido, os quatro são igualmente prováveis |
| `mirrors`    | imprint, tree | lista                | nenhum  | Espelha também: `none`, `leftright`, `frontback`, com `weight` opcional. Uma entrada que nomeia seu próprio peso é escrita `{ "mirror": "leftright", "weight": 2 }`, e uma entrada de `turns` do mesmo modo com `turn` |
| `at`         | imprint       | dois ints, x e z     | nenhum  | Coloca exatamente uma vez nessas coordenadas de bloco na superfície, quando esse chunk é gerado, em vez de por sorteio. Veja [Estruturas em locais exatos](#estruturas-em-locais-exatos) |
| `locateAs`   | imprint, tree | texto                | nenhum  | Registra toda estrutura que esta entrada coloca com esse nome, de modo que `/locate <name>` encontra a mais próxima. Veja [Encontrando estruturas colocadas](#encontrando-estruturas-colocadas) |

Para uma forma que nenhum tipo integrado cobre, `imprint` é o caminho: construa-a como um modelo `.nbt` e coloque-o, com `structures` para variá-lo, `turns` e `mirrors` para girá-lo e `integrity` para dissolvê-lo em algo mais áspero que o arquivo que você desenhou.

### Estruturas em locais exatos

*formas*

Estruturas vanilla são fixadas em pontos exatos com `structureAt` nas configurações de `terrain`, como entradas `structure=x,z`, uma por linha: `"structureAt": ["villages=1000,-500"]`. **O x e o z são coordenadas de bloco, não de chunk**, e a estrutura é gerada no chunk que contém esse bloco; o poço de uma vila fica nesse próprio bloco, enquanto outras estruturas começam onde o jogo as começaria nesse chunk. Uma entrada por instância desejada. Seu espaçamento, separação, distância mínima de spawn e verificações de terreno plano ficam todos de lado, então o ponto é responsabilidade do pacote, e dois pontos fixados a menos de um chunk de distância colocam duas estruturas no mesmo chunk. Depois de fundada, a estrutura se assenta no chão do seu chunk pelas regras habituais.

| Configuração  | Tipo                    | Padrão | O que faz |
| ------------- | ----------------------- | ------ | --------- |
| `structureAt` | lista de `structure=x,z` | nenhum | Fixa uma estrutura vanilla em um ponto exato, uma entrada por instância desejada. O x e o z são coordenadas de bloco, e a estrutura é gerada no chunk que contém esse bloco; seu espaçamento, separação, distância mínima de spawn e verificações de terreno plano ficam todos de lado |

Uma entrada `imprint` é fixada do mesmo modo com `"at": [x, z]` em sua forma, colocando exatamente uma vez nessas coordenadas na superfície quando esse chunk é gerado, em vez de por sorteio. Ela se combina com `locateAs`, então uma estrutura fixada também pode ser encontrada com /locate.

### Encontrando estruturas colocadas

*formas*

Uma entrada `imprint` com `"locateAs": "Crypt"` registra toda estrutura que coloca sob esse nome, e `/locate Crypt` então aponta para a mais próxima, com o nome oferecido no preenchimento com Tab. Só estruturas que já foram geradas podem ser encontradas, já que as estruturas de pacotes são colocadas por sorteio à medida que os chunks são criados, e não em uma grade que o jogo poderia prever. Os nomes ficam no save do mundo, então sobrevivem a reinicializações e funcionam em servidores. Um nome registrado assim também pode receber sua própria permissão com `gotoPlaceLevels`, de modo que um pacote decide separadamente de quem pode ser levado às suas próprias estruturas, à parte das vanilla.

### Chaves de campos e veios

*formas*

| Chave       | Usada por   | Valor                         | Padrão                  | O que faz |
| ----------- | ----------- | ----------------------------- | ----------------------- | --------- |
| `field`     | field       | objeto                        | `{ "type": "speckle" }` | Como o campo é calculado. Mesmas chaves do `field` de um grupo de dureza, descritas em [O campo](#o-campo): `speckle` com `chances` e `spread`, ou `seeded` com `cell`, `seeds`, `reach`, `arms` e `armReach` |
| `threshold` | field, vein | 0.0 a 1.0                     | `0.5` (`0.4` para vein) | Quão forte o campo precisa ser em um bloco para que ele seja colocado. Menor preenche mais |
| `fade`      | field       | int                           | `0`                     | Esmaece o topo da faixa em vez de terminá-la reta: ao longo deste número de blocos do topo do intervalo de altura, a chance de cada bloco ser colocado diminui passo a passo, o mesmo visual que o motor dá a `deepStone` onde ele encontra o mundo acima |
| `pattern`   | vein        | `default`, `banded` ou `tube` | `default`               | A aparência do depósito: um blob deformado, camadas empilhadas a cada poucos blocos ou tubos ocos serpenteando pela rocha |
| `density`   | vein        | 0.0 a 1.0                     | `1.0`                   | A parcela dos blocos qualificados que são de fato colocados, uma moeda por bloco |
| `rich`      | vein        | nome do bloco                 | nenhum                  | Colocado no quinto superior do intervalo do campo acima de `threshold`, o coração do depósito, no lugar dos blocos da entrada |
| `poor`      | vein        | nome do bloco                 | nenhum                  | Colocado nos dois quintos inferiores desse intervalo, a franja, no lugar dos blocos da entrada; o meio são os blocos da própria entrada. Se uma das faixas for omitida, os blocos da entrada são colocados ali |
| `richAt`    | vein        | 0.0 a 1.0                     | `0.88`                  | Onde a faixa rica começa nesse intervalo: `0.88` limita o bloco rico ao oitavo mais forte do depósito, um número menor engorda o núcleo rico, `1.0` não deixa nenhum bloco rico |
| `poorAt`    | vein        | 0.0 a 1.0                     | `0.4`                   | Onde os blocos da própria entrada começam: abaixo disso o bloco `poor` é colocado, então `0.4` dá uma franja dos dois quintos inferiores e `0.0` não deixa franja pobre. Limitado a `richAt` |

### Cinturões

*formas*

Um `belt` é uma bola muito maior que um chunk, usada para regiões de pedra e não para veios de minério. Seu `radius` é o tamanho da bola, e cada chunk calcula por conta própria onde começam as bolas próximas a ele, a partir da semente do mundo e do nome da própria entrada, de modo que um cinturão sai inteiro seja qual for a ordem de geração dos chunks, e nada é jamais gravado em um chunk vizinho.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

Um belt ignora `attempts` e `spread`, já que é colocado por chunk e não por tentativa. `minHeight` e `maxHeight` são a faixa em que ficam os centros, e a bola se estende por `radius` além dessa faixa. `replace` decide o que ele consome, `biomes` e os limites de temperatura e precipitação são verificados no centro, então um cinturão aparece inteiro ou não aparece, em vez de ser cortado na borda de um bioma.

O custo cresce com o cubo de `radius`, e um `rarity` baixo o multiplica, então comece pelos padrões e aumente o raio devagar.

### Campos

*formas*

Um `field` não coloca nada em um ponto e tudo de uma vez. Em vez de escolher um ponto e construir uma forma em torno dele, ele faz uma pergunta a cada bloco do chunk, entre `minHeight` e `maxHeight`, e coloca onde a resposta é pelo menos `threshold`. A pergunta é a mesma que os grupos de dureza fazem, então os dois descrevem os mesmos veios, e um pacote pode criar um grupo e uma entrada que concordam.

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

| Chave       | Obrigatório | Valor      | Padrão | O que faz |
| ----------- | ----------- | ---------- | ------ | --------- |
| `threshold` | não         | 0.0 a 1.0  | `0.5`  | Quão forte o campo precisa ser antes de um bloco ser colocado |
| `field`     | sim         | objeto     | nenhum | O mesmo objeto que um grupo de dureza aceita, com os mesmos tipos `speckle` e `seeded` |

Um `threshold` baixo toma a maior parte do campo e dá veios largos; um alto toma apenas o miolo de cada aglomerado e dá pequenos bolsões espalhados. Com `speckle` você obtém muitas pintinhas minúsculas; com `seeded` obtém manchas mais redondas ou, com braços, nós com tentáculos se estendendo entre eles.

Como um belt, um field ignora `attempts` e `spread`, já que é consultado por chunk e não por tentativa, e nunca grava em um chunk vizinho. Ele é calculado a partir da semente do mundo e do nome da própria entrada, então a mesma semente sempre dá os mesmos veios, e duas entradas com nomes diferentes nunca se alinham. `replace`, `adjacent`, `biomes` e os limites de clima se aplicam como de costume.

Um veio de `field` é a única forma que você descreve em vez de escolher. Ele roda a mesma malha que os grupos de dureza usam, então `seeded` com alguns braços dá nós com tentáculos se estendendo em direção aos vizinhos, o que é um veio e não um blob, e `threshold` decide quanto dele é sólido o bastante para ser colocado:

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

As chaves vão em um objeto `field` próprio, e não ao lado de `type`, já que `type` na forma já diz `field`.

## Dispersões

*gerando o mundo*

Um bloco `spread` com um `type`.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa. Uma chave marcada para um tipo é lida somente por esse tipo.

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

| Tipo        | Onde coloca as coisas |
| ----------- | --------------------- |
| `even`      | Em qualquer lugar entre as alturas, de modo uniforme. O padrão |
| `centered`  | Ponderado em torno de uma altura, rareando com a distância |
| `sprawl`    | Veios fractais que abrangem uma faixa de altura |
| `terrain`   | Acompanhando a superfície |
| `cavern`    | No piso das cavernas, ou no teto |
| `submerged` | Debaixo d'água ou de outro fluido |

| Chave               | Usada por | Valor                    | Padrão                       | O que faz |
| ------------------- | --------- | ------------------------ | ---------------------------- | --------- |
| `type`              | todos     | uma das dispersões acima | `even`                       | Qual dispersão |
| `center`            | centered  | int                      | ponto médio da faixa de altura | A altura em torno da qual se agrupa |
| `range`             | centered  | int                      | metade da faixa de altura    | Quão longe dessa altura alcança |
| `smoothness`        | centered  | 1 a 8                    | `2`                          | Quantos sorteios são tirados em média. Maior é uma faixa mais estreita |
| `veinHeight`        | sprawl    | int                      | a faixa de altura            | Quão alto é um veio |
| `veinDiameter`      | sprawl    | int                      | `12`                         | Quão largo é um veio |
| `verticalDensity`   | sprawl    | 1 a 100                  | `16`                         | Quão sólido é na vertical |
| `horizontalDensity` | sprawl    | 1 a 100                  | `32`                         | Quão sólido é na horizontal |
| `offsetMin`         | terrain   | int                      | `0`                          | Menor deslocamento a partir da superfície |
| `offsetMax`         | terrain   | int                      | `offsetMin`                  | Maior deslocamento a partir da superfície |
| `ceiling`           | cavern    | booleano                 | `false`                      | Fixa no teto da caverna em vez de no piso |

## Mapas de estruturas

*gerando o mundo*

Um mapa de estruturas compõe modelos em uma única construção nomeada sobre uma grade, muito além do limite de 32 blocos de um único arquivo `.nbt`. Cada camada é desenhada como linhas de caracteres únicos, um caractere por célula, e fica empilhada uma altura de célula acima da camada anterior. No máximo 8 camadas de 8 por 8 células, o que, com a célula padrão de 32, dá 256 blocos de lado, a altura de construção do vanilla.

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

| Configuração | Tipo         | Padrão              | O que faz |
| ------------ | ------------ | ------------------- | --------- |
| `name`       | texto        | o nome do arquivo   | Como o mapa é chamado nos logs |
| `cell`       | número       | `32`                | O passo da grade em blocos, até 48. Um modelo menor que a célula fica no canto da célula, de modo que peças de tamanho total se encaixam sem emendas |
| `ground`     | número       | `0`                 | Qual camada assenta na superfície do terreno. As camadas anteriores a ela escavam para baixo, e é assim que uma construção ganha porões |
| `at`         | dois números | nenhum              | Fixa uma cópia em coordenadas de bloco exatas, do mesmo modo que `structureAt` fixa uma vila |
| `spacing`    | número       | `0`                 | Espalha cópias em uma grade com este tanto de chunks de distância, com variação a partir da semente do mundo. `0` não espalha nenhuma, então um mapa só com `at` constrói exatamente uma vez |
| `chance`     | número       | `100`               | A porcentagem de pontos da grade que constroem uma cópia |
| `dimensions` | lista        | todas               | Ids de dimensão em que o mapa pode construir |
| `layers`     | lista        | nenhum              | As camadas, de baixo para cima, cada uma com um `palette` e um `map` |

Uma paleta nomeia modelos pela chave de registro, a partir de `<namespace>/structures/` de um pacote.

| Valor                                       | O que faz |
| ------------------------------------------- | --------- |
| `"a": "mypack:keep"`                        | Toda célula `a` dessa camada coloca este modelo |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Cada célula `a` sorteia na lista por peso, a partir da semente do mundo e do ponto da célula, então duas cópias da construção diferem, mas o mesmo mundo sempre constrói a mesma |
| `.`                                         | Uma célula vazia, nada é colocado |

Toda cópia sorteia uma das quatro orientações a partir da semente do mundo e a construção inteira gira junto, modelos incluídos, de modo que paredes que se encontram entre células continuam se encontrando. A camada de chão assenta na superfície amostrada do terreno sob o meio da construção. Cada chunk constrói apenas a sua própria fatia da grade, então uma construção que abrange muitos chunks chega sem geração em cascata, seja qual for a ordem em que os chunks carregam. Um [terreno de vila](#terrenos-de-vilas) do tipo `template` também pode nomear um mapa como seu `structure`, o que faz do composto uma construção da vila.

## Terrenos de vilas

*gerando o mundo*

`<namespace>/villages/*.json`

O caminho do arquivo é o nome do terreno, que `villagePieces` pode então nomear para mantê-lo ou descartá-lo.

Um arquivo aqui acrescenta uma peça que as vilas podem construir, junto com as do vanilla. Dois tipos, escolhidos com `type`.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as que precisa. Uma chave marcada para um tipo é lida somente por esse tipo.

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

### Cada terreno

*terrenos de vilas*

| Chave        | Usada por | Valor                                  | Padrão           | O que faz |
| ------------ | --------- | -------------------------------------- | ---------------- | --------- |
| `type`       | todos     | `farm` ou `template`                   | `farm`           | Que tipo de terreno |
| `weight`     | todos     | int                                    | `3`              | Com que frequência este terreno é escolhido em relação aos outros do pacote |
| `leastCount` | todos     | int                                    | `1`              | O mínimo por vila, antes de somar o tamanho da vila |
| `mostCount`  | todos     | int                                    | `4`              | O máximo por vila, antes de somar o tamanho da vila |
| `width`      | todos     | int                                    | `7`              | Tamanho ao longo do caminho |
| `height`     | todos     | int                                    | `4`              | Altura liberada acima do chão |
| `depth`      | todos     | int                                    | `9`              | Tamanho afastando-se do caminho |
| `apron`      | todos     | int                                    | `2`              | Quanto o chão pode diferir do nível da estrada sob o terreno antes de ser recusado ou deslizado ao longo da estrada: esse tanto de blocos de aterro sob ele, ou de corte em uma elevação acima dele, e não mais que isso entre seu canto mais alto e o mais baixo. Um terreno largo em colinas precisa de mais. Se for muito alto, o terreno forma terraços direto em uma encosta, o que, no lugar errado, devora uma montanha |
| `ground`     | todos     | nome do bloco                          | `minecraft:dirt` | O que é compactado por baixo em uma encosta |
| `requires`   | todos     | lista de ids de mods ou namespaces de pacotes | nenhum    | O terreno é deixado de fora, a menos que todos estejam presentes |

Todo terreno de pacote é oferecido às vilas como uma única entrada, então `weight` decide qual dos seus terrenos é escolhido quando uma vila pede um. Qual terreno uma colocação usou é gravado nos dados da própria vila, de modo que ela é reconstruída corretamente ao carregar.

### Fazendas

*terrenos de vilas*

Uma `farm` é o campo do vanilla, descrito em vez de codificado: um terreno do tamanho que você pedir, contornado por um bloco, preenchido com fileiras de solo separadas por canais de água, plantado com uma cultura escolhida por bloco a partir da sua lista.

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

| Chave      | Usada por | Valor                    | Padrão               | O que faz |
| ---------- | --------- | ------------------------ | -------------------- | --------- |
| `crops`    | farm      | lista de nomes de bloco  | trigo                | Plantada uma por bloco, em um estágio de crescimento aleatório |
| `edge`     | farm      | nome do bloco            | `minecraft:log`      | A moldura ao redor do terreno |
| `soil`     | farm      | nome do bloco            | `minecraft:farmland` | Do que as fileiras são feitas |
| `water`    | farm      | booleano                 | `true`               | Coloca um canal de água entre as fileiras |
| `rowWidth` | farm      | int                      | `2`                  | Quão larga é cada fileira de solo |

### Construído a partir de modelos

*terrenos de vilas*

Um `template` coloca, em vez disso, uma das suas estruturas `.nbt`, virada para o caminho da vila.

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

Um `template` cujo `structure` nomeia um dos seus [mapas de estruturas](#mapas-de-estruturas) coloca o composto inteiro como terreno. O tamanho do terreno vem então do mapa, sua área e suas camadas empilhadas vezes a célula, então `width`, `height`, `depth` e `integrity` não são lidos. As camadas anteriores ao `ground` do mapa escavam para baixo como porões, e as células ponderadas da paleta ainda são sorteadas por construção, então duas torres do mesmo mapa podem ser diferentes.

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| Chave            | Usada por | Valor            | Padrão       | O que faz |
| ---------------- | --------- | ---------------- | ------------ | --------- |
| `structure`      | template  | `namespace:name` | nenhum       | O modelo a colocar, ou um dos seus mapas de estruturas, que então define o tamanho do terreno |
| `integrity`      | template  | 1 a 100          | `100`        | Porcentagem dos blocos do modelo que aparecem |
| `lootTable`      | template  | `namespace:path` | nenhum       | A tabela de saque da qual todo baú dentro do modelo colocado é preenchido na primeira vez que é aberto. Um terreno que nomeia um mapa de estruturas é deixado como está |
| `villagers`      | todos     | int              | `0`          | Quantas pessoas o terreno gera |
| `villagerEntity` | todos     | `namespace:name` | um aldeão    | Quem mora ali, como uma variante de entidade sua |
| `villagerX`      | todos     | int              | `1`          | Onde aparecem, ao longo do terreno |
| `villagerY`      | todos     | int              | `1`          | Onde aparecem, acima do piso |
| `villagerZ`      | todos     | int              | `1`          | Onde aparecem, para dentro do terreno |

## Mapas de layout de cidades

*gerando o mundo*

Um mapa de cidade desenha a planta das ruas de uma vila em uma grade, um caractere por célula, e a vila é disposta a partir do desenho em vez de crescer. Ruas, praças e terrenos saem como as mesmas peças que uma vila crescida usa, então toda opção de estrada, ponte, cais, beco sem saída, poste de luz, cul-de-sac e peça central de praça se aplica sem alterações. O modelo de mundo nomeia o mapa em `villageLayout`.

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

| Configuração | Tipo   | Padrão            | O que faz |
| ------------ | ------ | ----------------- | --------- |
| `name`       | texto  | o nome do arquivo | Como o mapa é chamado nos logs |
| `cell`       | número | `48`              | O passo da grade em blocos, de 8 a 128. As estradas correm pelo meio de suas células, na largura de estrada do pacote, e os terrenos ficam centralizados nas suas, então uma célula precisa do maior terreno mais espaço para dar de frente para a rua |
| `palette`    | objeto | nenhum            | O que cada caractere dispõe, listado abaixo |
| `map`        | lista  | nenhum            | As linhas, até 64 por 64 células. Uma linha mais curta que a mais larga fica aberta depois do seu fim |
| `settings`   | objeto | nenhum            | Configurações de vila apenas para este mapa, sob os nomes que um modelo de mundo usa, como `villagePathCenterBlock`. Elas prevalecem sobre as do modelo, e as configurações de vila do próprio bioma ainda prevalecem sobre elas |

| Valor                                                                   | O que faz |
| ----------------------------------------------------------------------- | --------- |
| `"#": "street"`                                                         | Uma sequência de células de rua ao longo de uma linha ou coluna vira uma caixa de estrada na largura de estrada do pacote. Onde uma sequência de linha cruza uma de coluna, o cruzamento é pintado como qualquer outro. Uma célula de rua isolada, sem sequência em nenhum eixo, é disposta como um pequeno toco ao longo da linha |
| `"+": "plaza"`                                                          | Um poço com seu anel de praça. As sequências passam por células de praça, então as ruas se encontram no poço, e uma praça em um cruzamento ergue seu poço, ou sua peça central `villageWellStructure`, no meio do cruzamento como uma rotatória. A primeira praça do arquivo é o poço da própria vila, o que fixa o mapa no ponto onde a vila é fundada; um mapa sem nenhuma fica centralizado ali |
| `"a": "alley"`                                                          | Uma passagem estreita. Construções dão de frente para ela, mas ela não conecta nada, a regra do beco como de costume |
| `"J": "junction"`                                                       | Uma célula de rua disposta nas duas direções, de modo que um cruzamento se ergue ali mesmo onde o desenho passa em apenas um sentido. O braço que a atravessa tem uma célula de comprimento |
| `"b": "bulb"`                                                           | Uma célula de rua que termina em um cul-de-sac. Quando um mapa tem uma célula bulb, apenas pontas de estrada situadas em células bulb recebem um bulb, e todas as que têm espaço para isso recebem; um mapa sem ela mantém três pontas em quatro |
| `"E": { "kind": "elevated", "height": 8 }`                              | Uma célula de rua elevada a um deck `height` blocos, de 2 a 64, acima do terreno mais alto sob seu trecho de células elevadas unidas, com uma rampa de um bloco por linha em cada ponta. Um cruzamento de ruas dentro do trecho sobe com ele. Um trecho cujo deck ou rampas alcançariam uma linha que uma ferrovia ou um poço mantém em seu próprio nível permanece ao nível do chão, com uma linha no log. Qualquer valor pode ser escrito como objeto dessa forma, com `kind` nomeando a palavra |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | Uma rua disposta e pavimentada com suas próprias chaves de estrada, que prevalecem sobre as do mapa e do modelo. Sua largura segue seu próprio `villagePathExtraWidth`, `villagePathSidewalkWidth` e linha, e sua superfície, linhas e calçadas seguem suas próprias chaves de bloco, então uma avenida ou uma viela é desenhada com uma marca própria. Uma sequência usa as chaves de sua primeira célula que define alguma. Por mais larga ou estreita que seja, uma rua desenhada continua sendo uma rua: nunca é tomada por um beco ou um cul-de-sac |
| `"T": "mypack:tower"`                                                   | Uma célula de terreno, disposta a partir dessa definição de terreno, centralizada na célula e voltada para a rua mais próxima |
| `"T": ["mypack:a=3", "mypack:b=1"]`                                     | O mesmo, sorteado por peso a partir da semente do mundo e do ponto da célula, então o mesmo mundo sempre dispõe o mesmo terreno ali |
| `"g": "grow"`                                                           | Deixada para o crescimento. Com `villagePlotsLeast` definido, os distritos crescidos e o preenchimento de ruas ocupam essas células e se espalham para fora a partir do mapa; sem ele, a célula permanece aberta |
| `.`                                                                     | Terreno aberto, nada disposto |

Todo mapa sorteia uma das quatro orientações a partir da semente do mundo e gira inteiro, de modo que uma planta se lê igual de qualquer lado. As estradas são dispostas primeiro, então um terreno que se sobreporia a uma estrada ou a outro terreno é deixado aberto com uma linha no log, e um nome de terreno que nenhum pacote fornece deixa sua célula aberta do mesmo modo. O mapa não muda como as peças são vestidas: as chaves de estrada, `villageBlocks`, os postes de luz e a substituição do poço são todos lidos como em uma vila crescida. Nada cresce a partir de um mapa desenhado: nenhum beco é preenchido ao lado de suas ruas, e as pontas de suas estradas recebem seus bulbs, três em quatro como de costume ou como suas células bulb determinam, mas nenhuma casa ao longo delas.

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

| Configuração          | Tipo     | Padrão  | O que faz |
| --------------------- | -------- | ------- | --------- |
| `retrogen`            | booleano | `false` | Atualiza chunks salvos antes de uma entrada existir, para toda entrada de worldgen marcada com `"retrogen": true`. Desligado, os chunks que já existem são deixados em paz |
| `adoptExistingChunks` | booleano | `false` | O que acontece na primeira vez que um chunk antigo é visto: ligado, ele é marcado como se este pacote já o tivesse gerado e nunca é atualizado; desligado, é atualizado como qualquer outro. Para preencher um mundo existente, ligue `retrogen` e deixe este desligado |

Uma entrada com `"retrogen": true` é gerada em chunks que foram salvos antes de você a adicionar. Cada chunk registra o que já recebeu, então nada é feito duas vezes.

O sinalizador da entrada apenas marca uma entrada como elegível. A atualização é ligada pela configuração `retrogen`, que um pacote pode definir em seu bloco `settings` ou um jogador pode definir na config, e vem desligada por padrão. Junto dela, `adoptExistingChunks` decide o que acontece na primeira vez que um chunk antigo é visto: ligado, o chunk é marcado como se este pacote já o tivesse gerado e nunca é atualizado; desligado, é atualizado como qualquer outro. Ligar `retrogen` enquanto `adoptExistingChunks` também está ligado não faz nada, porque todo chunk antigo é descartado antes de poder entrar na fila. Para preencher um mundo existente, ligue `retrogen` e desligue `adoptExistingChunks` ao mesmo tempo.

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

Mudar `retrogenKey` na config torna todo chunk elegível de novo, o que acrescenta os novos veios por cima dos antigos, então a densidade dobra. Isso é deliberado, e é por isso que a chave é manual.

## Pré-geração

*gerando o mundo*

Criar o terreno de um mundo com antecedência, para que ninguém gere chunks durante o jogo: sem lag de chunks, um tamanho conhecido em disco e uma única espera no início em vez de uma primeira hora engasgada.

Os primeiros 12 chunks ao redor do spawn são sempre assumidos, seja o que for que um pacote ou a config diga, porque o jogo cria exatamente essa quantidade sozinho antes de qualquer um entrar. Deixado em paz, esse terreno chega sem iluminação e é vestido um chunk por vez conforme o jogador o percorre; assumido, ele é finalizado em uma única passada e o jogador pousa em um terreno já pronto. `pregenOnNewWorld` define até onde ir além disso, e o comando executa uma pré-geração manualmente.

`/rdplserver pregen <radius>` cria todo chunk dentro desse número de chunks do ponto onde é executado. `status` diz em que ponto está, `stop` a encerra, e `<radius> relight` executa apenas a passada de iluminação sobre um terreno que já existe, vestindo as junções que a execução não conseguiu alcançar e deixando em paz qualquer coisa nunca criada.

Enquanto uma execução está em andamento, todos ficam retidos: transformados em espectadores, mantidos no lugar, com uma linha pulsante no meio da tela e o mundo pausado ao redor. O modo em que cada jogador chegou é gravado no jogador no momento em que ele é retido, então um save feito no meio da execução, uma falha ou uma reconexão nunca deixa ninguém preso como espectador; o fim da execução devolve exatamente o modo que tirou, ou o `worldGameMode` do pacote quando um está definido. O progresso é anunciado a cada décimo do caminho, cada execução reilumina o seu próprio quadrado ao terminar e, quando tudo está pronto, os jogadores são liberados e recebidos. Até onde cada dimensão foi criada fica salvo no mundo, então um mundo concluído nunca roda de novo, a menos que algum dos arquivos em que o terreno de uma dimensão vive desapareça do disco, o que é percebido e faz essa dimensão ser refeita.

Em um pacote, essas chaves vão no bloco `settings` de um [modelo de mundo](#modelos-de-mundo), como qualquer outra chave de `chunks`. Todas elas mostradas, com `pregenBorderLimit` sendo a única ausente, já que apenas a config a contém:

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

### O que é feito

*pré-geração*

| Chave                         | O que faz | Por que defini-la |
| ----------------------------- | --------- | ----------------- |
| `pregenOnNewWorld`            | Raio em chunks criados ao redor do spawn antes de alguém jogar. 12 é o piso e 0 significa esse piso em vez de nada, já que o jogo cria 12 chunks ao redor do spawn por conta própria de qualquer jeito: a execução assume esse terreno e o ilumina em uma única passada em vez de deixá-lo chegar aos poucos atrás do jogador. Aumente para ir mais longe do que o jogo vai | Define até onde um pacote chega além do terreno que o jogo já cria |
| `pregenDimensions`            | Quais dimensões são criadas, em ordem, cada uma ao redor do seu próprio spawn | Adicione o Nether, o End ou suas próprias dimensões |
| `pregenAllDimensions`         | Toda dimensão registrada em vez de uma lista, começando pelo overworld | Pacotes com muitas dimensões. As dimensões de todos os mods contam, então cuidado com o tamanho |
| `pregenDimensionsWhenEntered` | Estas são criadas na primeira vez que alguém pisa nelas, retendo todos de novo até terminar | Dimensões que a maioria dos jogadores nunca visita; quem nunca vai não paga nada |
| `pregenToBorder`              | Preenche cada dimensão até a sua borda do mundo em vez de um raio | Mundos delimitados |
| `pregenBorderLimit`           | Até onde uma borda pode chegar antes de a execução ser recusada. Apenas config, nunca uma chave de pacote | Uma proteção contra uma execução descontrolada; aumente apenas sabendo o tempo e o disco que ela permite |

### Como uma partida se comporta

*pré-geração*

| Chave                  | O que faz | Por que defini-la |
| ---------------------- | --------- | ----------------- |
| `pregenResume`         | Uma execução parada ou interrompida continua de onde parou. A dimensão, o centro e o raio da execução são gravados no save quando ela começa, então uma falha, uma queda de energia ou uma saída no meio da execução retomam, no próximo carregamento, a cerca de dez segundos de onde pararam. Uma execução parada de propósito, por comando ou pelo watchdog, permanece parada | Execuções longas em servidores; execuções pequenas reiniciam barato sem isso |
| `pregenKeepLoaded`     | Chunks mantidos carregados atrás da execução para que os vizinhos de um chunk estejam à mão quando ele é vestido e iluminado | Aumente se a reiluminação informar muitos chunks deixados para depois; custa memória |
| `pregenPauseAbove`     | A execução descansa quando tantos chunks estão esperando para serem gravados | Diminua para um disco lento |
| `pregenMillisPerRound` | Quanto tempo cada tick pode gastar criando terreno | Aumente em um mundo vazio, diminua em um servidor onde as pessoas estão jogando |

A pré-geração tem seu próprio caminho rápido para a iluminação, e ele se retira quando um motor de luz como Alfheim ou Phosphor está instalado, deixando esse motor fazer o trabalho. De um jeito ou de outro, você acaba com um terreno finalizado e totalmente iluminado.

Execute você mesmo antes de distribuir, no raio que será distribuído, do começo ao fim. Os chunks crescem com o quadrado do raio, 63 em qualquer sentido são dezesseis mil chunks, 500 são mais de um milhão, a cerca de dez quilobytes cada, então a pasta de regiões do seu mundo de teste e o tempo de relógio são os números honestos a colocar diante dos jogadores. Não distribua um raio que nunca foi executado.

### O que os jogadores veem

*pré-geração*

| Chave                                                                               | O que faz | Por que defini-la |
| ----------------------------------------------------------------------------------- | --------- | ----------------- |
| `pregenRunningSays`, `pregenRelightSays`, `pregenFinishedSays`, `pregenStoppedSays` | As mensagens de chat de cada etapa. As duas primeiras podem conter `%d` para a porcentagem e, depois dele, `%s` para o nome da dimensão, ou `%1$d` e `%2$s` para colocá-los em qualquer ordem, e sempre terminam com ` - ETA 00:00:00` para essa passada, que não é uma configuração. Concluída e parada são ditas uma vez, quando tudo o que foi pedido está pronto, terminando com ` - Total time 00:00:00` para o conjunto, que também não é uma configuração | Reescreva-as na voz do seu pacote, nomeie a dimensão quando várias são criadas ou silencie-as |
| `pregenSpectatingSays`                                                              | A linha de retenção no meio da tela enquanto o terreno é criado. Deixada no padrão, ela fala o idioma de cada jogador; vazia, não mostra nada | Mantenha abaixo de cerca de trinta e cinco caracteres, ou janelas pequenas a cortam |
| `pregenLogo`                                                                        | Onde o logo fica quando a pré-geração termina: `left`, `center` ou `right`, acima do texto do meio da tela, exibido por alguns segundos e depois desaparecendo junto com a neblina | É sempre exibido; uma palavra desconhecida é lida como `center` |
| `welcomeSays`                                                                       | A saudação verde, exibida em todo login e após a pré-geração. Uma entrada simples é a linha para todo lugar; uma entrada `dimension=message` a substitui para essa dimensão e também saúda toda chegada ali, por exemplo `"-1=Welcome to the Nether!"`. Uma mensagem vazia depois do `=` silencia essa dimensão; uma lista vazia não mostra nada. Deixada no padrão, ela fala o idioma de cada jogador | Uma linha simples nomeia seu pacote; adicione linhas de dimensão para dar um tema a cada mundo. Mantenha as linhas abaixo de cerca de trinta e cinco caracteres |
| `saysCard`                                                                          | Mostra as linhas que este mod diz, a boas-vindas, o progresso da pré-geração e as linhas de ameaça, como um cartão no canto inferior direito em vez de no chat. O cartão desliza para dentro, permanece oito segundos e esmaece, e aparece também sobre uma tela aberta | Ligue quando o chat estiver movimentado ou as linhas devam parecer parte do mundo e não conversa |
| `saysIcon`                                                                          | Um item desenhado no cartão, por exemplo `minecraft:compass`. Vazio não desenha nenhum | Dê ao cartão o emblema do seu pacote |
| `saysColor`                                                                         | A cor de fundo do cartão em hexadecimal, por exemplo `1E2630`. Vazio usa um cinza-ardósia escuro | Combine com a paleta do seu pacote |
| `saysImage`                                                                         | Um PNG dos assets de cliente do pacote, por exemplo `rubyworld:textures/gui/card.png`, esticado sobre o cartão como fundo e desenhado sobre a cor. Vazio não desenha nenhum | Dê ao cartão um painel pintado; mantenha a imagem larga e baixa, ela é esticada para o que o texto precisar |
| `saysBackground`                                                                    | Desenha o painel do cartão, sua borda e a faixa de cor, e o fundo escuro atrás da boas-vindas e das notas no meio da tela enquanto um jogador está retido. Desligado, sobra apenas o texto, que mantém sua sombra, e `saysImage` se houver um definido | Deixe as linhas flutuarem sobre o mundo, ou deixe um `saysImage` pintado se sustentar sozinho |
| `saysFont`                                                                          | A fonte em que o texto do cartão é desenhado, nomeada como `namespace:name`, por exemplo `rubyworld:runes`. Vazio usa a fonte do RDPL, `resourcedatapackloader:rdpl`. O arquivo que ela nomeia é descrito em Cartas | Dê ao cartão a tipografia própria do seu pacote |
| `toasts`                                                                            | Quais dos toasts do jogo, os pop-ups no canto superior direito, são exibidos. `true` mostra todos e `false` nenhum; uma lista mostra apenas os tipos que nomeia: `advancements`, `recipes` para receitas desbloqueadas, `tutorial` para as dicas de como jogar, `system` para os avisos do próprio jogo e `other` para todo toast que os demais não cobrem, como os de outros mods. O padrão não mostra nenhum. O cliente de um jogador adota o valor quando ele entra | Mantenha `["advancements"]` quando seu pacote guia os jogadores com advancements e o resto atrapalha |

### Backup e redefinição do mapa

*pré-geração*

| Chave                   | O que faz | Por que defini-la |
| ----------------------- | --------- | ----------------- |
| `pregenBackup`          | Copia o mundo para um backup intocado assim que a pré-geração termina, enquanto os jogadores ainda estão retidos. A geração é então paga uma única vez: uma redefinição posterior, ou um mundo novo com o mesmo pacote e semente, restaura a cópia em vez de gerar de novo, o que é muito mais rápido do que pré-gerar duas vezes. A cópia é mantida fora do save, em `rdpl-pristine/<world>` ao lado dele, de modo que os backups de outro mod não a levem junto e ela não apareça em uma pasta que eles gerenciam. Uma cópia cujos pacotes não correspondem mais aos carregados é descartada e refeita a partir do mundo em mãos, então uma mudança de pacote nunca redefine para o mapa de outra pessoa | `false` |
| `pregenBackupSays`      | A linha no meio da tela exibida aos jogadores enquanto essa cópia é feita, com a porcentagem depois dela. Vazio não mostra nada e a cópia é feita em silêncio | `Pack requested world backup` |
| `resetSays`             | A linha no meio da tela exibida aos jogadores enquanto `/rdplserver reset` ou o fim de uma rodada recoloca o mapa no lugar. Vazio redefine em silêncio | `Pack requested map reset` |
| `resetSendsTo`          | Para onde os jogadores são levados por uma redefinição: `spawn`, uma posição como `x,y,z`, ou `dimension:x,y,z` para enviá-los a outro mundo, que é como uma redefinição deixa todos em um lobby em vez de voltar para a arena | `spawn` |
| `resetRuns`             | Uma função executada depois que uma redefinição limpou o mapa, nomeada como `namespace:path`. É isso que reconstrói a arena, já que um pacote que criou seu mapa a partir de uma função pode simplesmente executá-la uma segunda vez. Vazio não executa nada | vazio |
| `resetClearsEntities`   | Remove toda entidade que não seja um jogador. Mobs, itens soltos e experiência somem, o que deixa o mapa como começou | `true` |
| `resetClearsScores`     | Volta a zero todo objetivo que o pacote mantém, para que uma nova partida comece do zero. As próprias equipes são mantidas | `true` |
| `resetClearsInventory`  | Esvazia o inventário de todo jogador, armadura e mão secundária incluídas, para que uma rodada comece com o que o mapa distribui e não com o que a última deixou. O `gives` de um lado é entregue de novo logo depois | `false` |
| `resetClearsExperience` | Volta a experiência de todo jogador ao nível zero | `false` |

---

# Modos de jogo

## Introdução do mundo

*modos de jogo*

`<namespace>/worldintro/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida. Toda introdução que um pacote distribui é executada, na ordem dos pacotes.

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

| Chave      | Obrigatório | Valor                                         | Padrão | O que faz |
| ---------- | ----------- | --------------------------------------------- | ------ | --------- |
| `pages`    | sim         | lista de páginas                              | nenhum | Exibidas em ordem. Um arquivo sem páginas é recusado com um erro |
| `once`     | não         | booleano                                      | `false` | Toca uma vez por jogador por mundo em vez de a cada entrada |
| `music`    | não         | nome de evento de som                         | nenhum | Uma faixa para a sequência inteira, iniciada com a primeira página |
| `requires` | não         | lista de ids de mods ou namespaces de pacotes | nenhum | A introdução é ignorada, a menos que todos estejam presentes |

### Páginas

*introdução do mundo*

Cada entrada em `pages`:

| Chave         | Obrigatório | Valor                    | Padrão                          | O que faz |
| ------------- | ----------- | ------------------------ | ------------------------------- | --------- |
| `mode`        | não         | `scroll` ou `static`     | `scroll`                        | Texto que se move, ou texto que fica parado até o jogador avançar |
| `text`        | não         | caminho para um arquivo `.txt` | nenhum                    | As palavras. Omita para uma página que seja só imagens |
| `background`  | não         | caminho de textura       | o fundo de terra em mosaico     | Um fundo |
| `backgrounds` | não         | lista de caminhos de textura | nenhum                      | Vários, em ciclo. Soma-se a `background` se você der os dois |
| `interval`    | não         | segundos                 | `5.0`                           | Por quanto tempo cada fundo é mantido, quando há mais de um |
| `time`        | não         | segundos                 | calculado a partir do texto     | Quanto tempo uma página rolante leva, do início ao fim. Em uma página parada, ou na última página de qualquer tipo, é quanto tempo até a página avançar sozinha, e sem ele elas esperam o botão |
| `direction`   | não         | `up` ou `down`           | `up`                            | Em que sentido o texto rolante se move |
| `textScale`   | não         | número                   | `1.0`                           | Multiplica o tamanho da fonte. Uma página `static` quebra seu texto na largura da tela, menos uma margem de cada lado, e quando ainda passaria por baixo dos botões o texto é desenhado menor, até a metade, até caber |
| `settle`      | não         | booleano                 | `false`                         | Termina com a última linha centralizada em vez de sair correndo para fora da tela |

### Texto e tempo

*introdução do mundo*

Os arquivos de texto ficam em `<namespace>/texts/*.txt`. É texto simples, um parágrafo por linha, e as linhas em branco permanecem como linhas em branco. Um arquivo `.md` é lido da mesma forma, e os dois tipos aceitam a formatação abaixo. `PLAYERNAME` é trocado pelo nome do jogador, a mesma substituição que o poema final do vanilla usa.

`time` define quanto tempo a página dura, de modo que a mesma página leva o mesmo tempo tenha ela uma linha ou vinte. Ajuste a velocidade de leitura pela quantidade de conteúdo na página. Se você omitir `time`, a página corre na mesma velocidade dos créditos do vanilla, em que mais texto simplesmente leva mais tempo.

### Formatação de texto

*introdução do mundo*

Os textos de introdução aceitam Markdown. Um arquivo sem marcações aparece exatamente como texto simples.

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

| Marca | Escrita como | Aparece como |
| --- | --- | --- |
| Título | `# `, `## `, `### ` no início de uma linha | Negrito e maior: duas vezes, uma vez e meia e uma vez e um quarto o tamanho do texto, alinhado como o texto do corpo |
| Negrito | `**text**` | O estilo negrito da fonte |
| Itálico | `*text*` | O estilo itálico da fonte |
| Negrito itálico | `***text***` | O estilo negrito, inclinado |
| Tachado | `~~text~~` | Riscado |
| Código | `` `text` `` | Tingido de ciano |
| Link | `[text](url)` | Apenas o texto, sublinhado; não é clicável |
| Rúnico | `{runic}text{/runic}` | O texto na cifra de runas, `resourcedatapackloader:rdpl_runic`, enquanto o resto da linha mantém a sua fonte; negrito e itálico dentro dele usam os estilos negrito e itálico da cifra. Funciona em títulos, itens de lista e citações, e um `{runic}` sem fechamento aparece como foi escrito |
| Marcador | `- ` ou `* ` no início de uma linha | Um marcador, com as linhas quebradas recuadas sob o texto; dois espaços antes da marca o aninham em um nível |
| Numerado | `1. ` no início de uma linha | O número como escrito, recuado da mesma forma |
| Citação | `> ` no início de uma linha | Recuada e esmaecida |
| Linha | `---` em uma linha própria | Uma linha horizontal ao longo da largura do texto |
| Imagem | `![alt](namespace:textures/....png)` em uma linha própria | A imagem, reduzida à largura do texto e mantendo a proporção; o texto alternativo aparece se ela não puder ser lida |
| Escape | `\` antes de uma marca, p. ex. `\*` | A marca como um caractere comum |

Tabelas e blocos de código delimitados (entre linhas ```) são desenhados como texto simples, marcas incluídas. O tempo calculado de uma página com rolagem e o ajuste de tamanho de uma página fixa consideram ambos a altura já diagramada, imagens incluídas. Títulos e linhas de cartas, mensagens de Says e as notas de boas-vindas e de espera aceitam as marcas em linha, do negrito ao rúnico, uma linha cada.

### Como se joga

*introdução do mundo*

Uma página com rolagem passa para a próxima quando o seu tempo acaba. A última página nunca avança sozinha, ela espera. Na parte inferior ficam **Próxima página** e **Pular tudo**, ou um único **Continuar para o mundo** na última página. Esc faz o mesmo que Pular tudo. Páginas estáticas centralizam todas as linhas. Páginas com rolagem mantêm uma coluna fixa, como os créditos fazem.

No modo um jogador, o mundo fica pausado atrás da introdução, de modo que nada se aproxima do jogador enquanto ele lê. A única exceção é o terreno que ainda está sendo criado quando a introdução abre: nesse caso a criação continua atrás das páginas, e o jogador fica retido como espectador até continuar para o mundo, mesmo que a execução termine antes. Em um servidor o mundo continua rodando, e um cliente vanilla nunca vê a introdução e entra normalmente. A saudação de boas-vindas espera até que as páginas sejam fechadas, para não se perder atrás delas.

`once` é lembrado nos dados salvos do jogador e sobrevive à morte. `/rdplserver intro` o limpa para quem executa o comando, de modo que a introdução toca de novo na próxima vez que a pessoa entrar. Ela não é reexibida na hora, o que impede que seja um caminho de volta à sequência de entrada no meio de uma partida.

Os fundos são esticados para preencher a janela, então uma imagem 16:9 combina com uma janela 16:9 e uma quadrada fica achatada. Recorte a imagem no formato certo em vez de confiar no ajuste. `music` aceita qualquer evento de som registrado, do vanilla ou um que o seu próprio pacote adicione por meio de `sounds`. Ele não se repete, então uma faixa curta termina e deixa silêncio atrás de si.

Se mais de um pacote incluir uma introdução, as páginas deles correm em sequência na ordem dos pacotes, em vez de uma única vencer. Restrinja-as com `requires` se quiser apenas uma.

## Equipes

*modos de jogo*

`<namespace>/teams/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam. Cada arquivo é um lado.

Um lado é uma equipe real no placar do próprio jogo, então `/scoreboard teams list` a enxerga, ela mantém seus membros após salvar e recarregar, e um cliente sem este mod mostra as cores e as plaquetas de nome como em qualquer equipe vanilla. A filiação é por nome, então qualquer coisa que tenha um nome ou um UUID pode estar em um lado: um jogador, um zumbi, um aldeão, um suporte de armadura.

Um lado só entra em campo onde um pacote pede um: sem nenhuma pasta `teams` em lugar algum, o mod não adiciona equipe, não escuta nada e não oferece o comando. Um operador de servidor que edita um arquivo pode executar `/rdpl reload` para pôr a mudança em campo no mundo em execução sem reiniciar.

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `name` | texto | o nome do arquivo | O nome da equipe no placar, de 1 a 16 caracteres. É o que `/scoreboard` e os outros arquivos usam |
| `displayName` | texto | o nome | O que os jogadores veem no lugar do nome |
| `color` | texto | `white` | Uma das dezesseis cores de texto. Ela tinge a plaqueta de nome e é por ela que os espaços da barra lateral por equipe se guiam |
| `prefix` | texto | vazio | Colocado antes do nome de um membro, depois da cor |
| `suffix` | texto | vazio | Colocado depois do nome de um membro |
| `scoreboard` | booleano | `true` | Se o lado existe como equipe no placar do jogo. Desligado, não cria equipe alguma: os seus mobs levam a cor do lado no nome, nada os impede de lutar entre si, e nenhum ponto é registrado nele, já que a pontuação vai pela equipe |

### Combate e visibilidade

*equipes*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `friendlyFire` | booleano | `false` | Se os membros podem ferir uns aos outros. É também o padrão de `mobFriendlyFire` |
| `mobFriendlyFire` | booleano | `friendlyFire` | Se os mobs de um lado podem ferir o próprio lado com explosões e TNT arremessado, o que o jogo sozinho nunca impede. Desligado poupa o lado; ligado deixa como o jogo o tem |
| `seeFriendlyInvisibles` | booleano | `true` | Se os membros se veem mutuamente enquanto invisíveis |
| `nameTags` | texto | `always` | `always`, `never`, `hideForOtherTeams` ou `hideForOwnTeam` |
| `deathMessages` | texto | `always` | As mesmas quatro palavras, para quem é avisado quando um membro morre |
| `collision` | texto | `always` | `always`, `never`, `pushOtherTeams` ou `pushOwnTeam` |

### Quem entra

*equipes*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `entities` | lista | vazio | IDs de entidades cujos spawns todos entram neste lado, como `minecraft:zombie` ou uma das suas |
| `players` | lista | vazio | Nomes de jogadores que entram neste lado ao fazer login |
| `spawnBox` | lista | nenhum | Seis números inteiros, x y z até x y z. Tudo que nascer lá dentro entra, e os cantos podem ser dados em qualquer ordem |
| `joinable` | booleano | `true` | Se um jogador pode entrar com `/rdpl team join`. Defina como false para um lado que é só de mobs |
| `balance` | booleano | `false` | Se `/rdpl team join` sem nome pode colocar um jogador aqui. Entre os lados que o permitem, é escolhido o que tem menos jogadores |
| `picks` | número | `0` | Quantos membros este lado sorteia. A cada rodada aberta, o lado deixa o último sorteado voltar para onde estava e sorteia de novo entre tudo que `picksFrom` nomeia; entre os sorteios, um login ou um spawn desse conjunto preenche de imediato uma vaga vazia. Um jogador entre todos, em um lado só dele, é para isso que serve |
| `picksFrom` | lista | vazio | De onde o sorteio é feito: `players` para todos os que estão online, e IDs de entidades para todo mob vivo desse tipo |
| `standIn` | objeto | nenhum | Um mob que ocupa o lado enquanto nenhum jogador está nele: `{ "entity": "mypack:herobrine", "at": "23,31,0" }` mantém vivo um exemplar dessa entidade naquele ponto do Overworld, invocando-o quando falta, e o remove no instante em que um jogador entra no lado, de modo que a partida é jogada contra a IA até que um jogador assuma o papel. Verificado a cada cinco segundos; o ponto precisa estar em terreno carregado. Em um jogo com lobby (`opens.by: leader`), um substituto só é invocado enquanto o lobby espera e quando a rodada abre, então um que cai permanece ausente pelo resto da rodada e do seu fim, até que todos voltem ao lobby; sem lobby, um substituto caído não é reposto enquanto roda uma rodada que termina em `ends.lastStanding` |

Há três formas de entrar, e um lado pode usar todas. `entities` nomeia IDs de entidades, e qualquer coisa desse tipo entra ao nascer, que é como um pacote dá lados aos mobs sem mexer neles. `spawnBox` reivindica um canto do mundo, e tudo que nascer lá dentro entra, o que serve a uma arena em que os dois lados usam o mesmo mob. `players` nomeia jogadores diretamente. Além disso, um jogador pode entrar com `/rdpl team join <name>`, a menos que o lado defina `joinable` como false, e sair com `/rdpl team leave`.

### Kit inicial e spawn

*equipes*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `gives` | lista | vazio | Itens colocados no inventário de um jogador ao entrar no lado, um nome de item para um só ou `{ "item", "count", "unbreakable" }` para mais, ou para um que nunca se desgasta, em qualquer espaço livre e largados aos pés dele quando não há nenhum. Entregues de novo após uma redefinição que limpa inventários (`resetClearsInventory`) |
| `spawn` | texto | nenhum | `x,y,z` no Overworld onde os jogadores do lado são postos quando uma rodada abre, para que cada lado comece no seu próprio terreno; sem isso, eles ficam onde a redefinição ou o lobby os deixou |

### O líder

*equipes*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `lead` | texto | `none` | Como o líder do lado é escolhido: `none`, `first` para quem entrou no lado há mais tempo entre os que estão online, de modo que passa pela ordem de entrada enquanto um está ausente e volta com ele; eles são avisados ao chegar, depois da introdução e de qualquer espera, e de novo quando passa para eles, `topScore` para quem está mais alto no objetivo que `leadOn` nomeia, `appointed` para o jogador que `leadIs` nomeia, `vote` para quem os membros votarem, ou `claim` para quem o reivindicar primeiro. Um líder é um rótulo e uma cor e nada mais: não concede poder algum, então um líder que sai não quebra nada |
| `leadOn` | texto | vazio | Com `topScore`, o objetivo pelo qual os membros são classificados. É recalculado a cada vez que é lido, então acompanha a pontuação |
| `leadIs` | texto | vazio | Com `appointed`, o jogador que lidera |
| `leadSays` | texto | `You are the current round leader` | Dito a um jogador quando a liderança chega a ele: ao chegar a um lado que ele lidera, ao reivindicá-la, ou quando uma liderança `first` passa para ele, caso em que informa quem saiu. `{side}` é o nome de exibição do lado; vazio não diz nada |
| `leadRuns` | texto | vazio | Uma função, `namespace:path`, executada uma vez a cada vez que a liderança passa a um jogador: a primeira liderança e cada repasse depois dela. Roda como o líder, na posição dele, com a permissão que uma função recompensada por um avanço tem, de modo que `@s` é o líder. Verificada a cada segundo; para um líder que está offline, roda quando ele estiver online de novo. Uma reinicialização decide a liderança do zero |

## Pontuação

*modos de jogo*

`<namespace>/scoring/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam. Cada arquivo é um objetivo.

Um objetivo é um objetivo real no placar do próprio jogo, então `/scoreboard players list` o lê e ele mantém suas pontuações após salvar. `criterion` é o que o jogo conta por conta própria: `dummy` para uma pontuação que só este pacote move, ou `deathCount`, `playerKillCount`, `totalKillCount`, `health`, ou qualquer nome `stat.` ou `achievement.` que o jogo conheça.

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `name` | texto | o nome do arquivo | O nome do objetivo no placar, de 1 a 16 caracteres |
| `displayName` | texto | o nome | O que os jogadores veem no lugar do nome |
| `criterion` | texto | `dummy` | O que o jogo conta por conta própria. Um desconhecido é recusado com uma linha avisando |
| `display` | texto | vazio | `sidebar`, `list`, `belowName` ou `sidebar.team.<color>`. Vazio não o mostra em lugar nenhum; não há tela de placar para abrir |
| `render` | texto | o próprio do critério | `integer` ou `hearts` |
| `teamTotals` | booleano | `true` | Os pontos vão para uma linha com o nome da equipe do membro |
| `individuals` | booleano | `false` | Os pontos vão também para uma linha do próprio membro |
| `carries` | booleano | `false` | O objetivo sobrevive a uma redefinição do mapa em vez de ser apagado com ele. Um placar de vitórias de rodada de uma partida é um exemplo |
| `awardsTo` | texto | vazio | Outro objetivo ao qual este concede um ponto quando termina, ao lado que liderou. Uma classificação empatada não concede nada |
| `tiebreak` | booleano | `false` | Uma rodada que termina empatada no topo sorteia um dos lados empatados com o acaso do mundo, registra o sorteio e o premia como de costume. Uma partida, um objetivo sem `awardsTo`, sorteia do mesmo jeito e nomeia o lado sorteado no topo dos resultados |

### Pontos

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `points.kill` | objeto | vazio | ID de entidade para pontos, creditados ao lado de quem matou. `minecraft:player` pontua a morte de um jogador |
| `points.death` | int | `0` | Pontos sempre que um membro morre, seja qual for a causa. Pode ser negativo |
| `points.ownKill` | int | `0` | Pontos por uma morte causada a um membro do próprio lado de quem matou, no lugar do valor de `kill`. 0 não pontua nada por ela; um número negativo é uma penalidade |

`points` é o que este mod acrescenta ao que o jogo conta, alimentado no mesmo objetivo para que `/scoreboard` ainda o leia. `kill` vale tantos pontos por ID de entidade morta, creditados ao lado de quem matou; `death` vale tantos sempre que um membro de um lado morre, e pode ser negativo. Com `teamTotals` os pontos vão para uma linha com o nome da equipe, o que permite à barra lateral mostrar quatro lados em vez de uma linha para cada mob. `individuals` acrescenta também uma linha por membro, e vem desligado por padrão porque uma linha por UUID de mob parece ruído.

### Como uma rodada termina

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `ends.atScore` | int | `0` | A partida termina no instante em que um lado atinge este valor. 0 nunca termina por pontuação |
| `ends.afterMinutes` | int | `0` | A partida termina após este número de minutos. 0 nunca termina por tempo |
| `ends.afterRounds` | int | `0` | Para um objetivo ao qual outro faz `awardsTo`: a partida termina quando este número de rodadas tiver sido concedido no total, quem quer que as tenha levado. 0 nunca termina por rodadas |
| `ends.lastStanding` | booleano | `false` | A rodada termina quando resta apenas um lado de pé. Os lados em jogo são os que têm um jogador ou um mob vivo quando a rodada abre, dois no mínimo; um jogador que morre está fora, de volta como espectador até a rodada acabar, e um lado cujos jogadores estão todos fora ou ausentes e cujos mobs estão todos mortos caiu. O lado que resta de pé leva a rodada, e `awardsTo` a registra para esse lado, seja qual for a pontuação. Com `resets` e `opens.by: leader` o jogo então volta ao lobby. O `standIn` de um lado não é invocado de novo enquanto uma rodada assim roda |
| `ends.outSays` | texto | `You are out until the round ends` | O que se diz a um jogador eliminado. Vazio não diz nada |
| `ends.locksTeams` | booleano | `true` | Entrar em um lado enquanto uma rodada está em andamento espera até que a rodada acabe, para que ninguém caia no meio de uma rodada pontuada |

`ends` encerra a partida, seja no instante em que um lado atinge `atScore` ou quando `afterMinutes` tiverem passado. A classificação é então exibida, ordenada pelo próprio jogo: como chat, ou como uma carta se `results` pedir uma. Um jogador sem este mod recebe a mesma classificação como linhas de chat, para que ninguém fique sem resultado. Com `resets`, esse fim é o de uma rodada: a classificação permanece por `intermissionSeconds` enquanto uma contagem regressiva corre na barra de ação, o mapa é redefinido para a boas-vindas, e a próxima rodada abre após uma contagem de cinco segundos. `awardsTo` entrega a rodada ao lado que liderou, em um objetivo que `carries` atravessa a redefinição. Um objetivo carregado pode terminar por conta própria -- `atScore` para uma melhor de N, `afterRounds` para uma contagem fixa -- e a sua classificação é limpa na redefinição seguinte, de modo que uma nova partida abre.

### Entre rodadas

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `ends.resets` | booleano | `false` | Terminar a rodada redefine o mapa, como `resetSays` e as outras configurações de redefinição descrevem, e então uma nova rodada abre |
| `ends.intermissionSeconds` | int | `10` | Quanto tempo a classificação permanece entre o fim e a redefinição |
| `ends.intermissionSays` | texto | `Round cooldown {seconds}` | Exibido na barra de ação a cada segundo do intervalo após o fim de uma rodada, com `{seconds}` em contagem regressiva até a redefinição. Vazio não mostra nada |
| `ends.startsSays` | texto | `Round starting in {seconds}` | Exibido na barra de ação durante a contagem de cinco segundos que abre a próxima rodada após a redefinição, com `{seconds}` em contagem regressiva. Vazio não mostra nada |

### O lobby

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `opens.by` | texto | `auto` | `auto` abre a próxima rodada por conta própria, cinco segundos após a redefinição. `leader` mantém o jogo em um lobby: após a redefinição, e quando o mundo carrega pela primeira vez, nada é pontuado e nenhum relógio corre, os lados podem ser livremente escolhidos e deixados, e a rodada só abre quando o líder de um lado, ou um operador, executa `/rdpl round start`, e não enquanto alguém ainda estiver lendo a introdução do mundo; então corre a contagem de cinco segundos, os sorteios são feitos, e cada lado é posto no seu `spawn`. Enquanto o mundo espera, até o fim da contagem de cinco segundos, os jogadores ficam onde estão e não podem quebrar, colocar, usar, bater nem largar nada e não sofrem dano, vendo a linha de espera quando tentam, e todo outro ser vivo fica parado: sem IA, sem movimento. Os comandos continuam funcionando, então os lados podem ser escolhidos e a rodada iniciada |
| `opens.says` | texto | `Waiting for {leader} to start the round` | Exibido no meio da tela, como a boas-vindas, a cada jogador que não lidera: ao chegar ao lobby depois da introdução, uma vez exibida a boas-vindas; quando o lobby abre de novo após uma rodada; sempre que muda, quando um líder chega ou sai; e quando tentam algo que o lobby recusa. `{leader}` são os líderes de todos os lados, ou `a leader` enquanto ninguém lidera. Vazio não mostra nada |
| `opens.leaderSays` | texto | `Type /rdpl round start` | Exibido da mesma forma e nos mesmos momentos a um jogador que lidera um lado, no lugar de `opens.says`. Vazio não mostra nada |
| `opens.lobby` | texto | nenhum | `x,y,z` no Overworld, ou `dimension:x,y,z` em outro mundo, como `-1:0,64,0`, onde todos esperam enquanto o lobby se mantém: todo jogador, e todo mob vivo de um lado, é posto em um anel ao redor desse ponto, cada um voltado para o centro, de modo que ficam se encarando. A cada um é dado um arco tão largo quanto ele mais dois blocos, para que nenhum se sobreponha a outro, e o anel cresce conforme mais chegam; ele é refeito sempre que alguém entra ou sai. A altura é o piso em que estão, encontrado a até três blocos para cima ou para baixo. Jogadores e mobs passam para esse mundo e voltam diretamente, sem portal construído. Quando a rodada abre, os jogadores vão para o `spawn` do seu lado, e um mob que ainda está de pé é devolvido ao lugar onde estava, no seu próprio mundo |
| `opens.lobbyJoins` | booleano | `false` | Põe um jogador que entra no meio de uma rodada no lobby como espectador até a rodada acabar, em vez de onde ele saiu. Precisa de `opens.lobby` |
| `opens.joinsSays` | texto | `Round is in progress, you can join after it ends` | O que se diz a ele. Vazio não diz nada |

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
| `reset.lead` | texto | `none` | O que `/rdpl round reset` faz pelo líder de um lado enquanto uma rodada roda. `now` encerra a rodada de imediato e redefine o mapa; `vote` convoca uma votação; `none` não dá ao líder voz própria, então o líder convoca uma votação como qualquer outro jogador onde `players` permitir. Um operador sempre redefine de imediato |
| `reset.players` | texto | `none` | `vote` permite que um jogador de qualquer lado convoque uma votação com `/rdpl round reset`. `none` deixa a redefinição para o líder |
| `reset.teams` | lista | vazio | Os lados cujos jogadores podem convocar uma votação. Vazio é todos os lados |
| `reset.passPercent` | int | `51` | A parcela dos votantes, de 1 a 100, que precisa votar sim para a rodada ser redefinida. `51` é mais da metade, `100` é todos |
| `reset.voteSeconds` | int | `30` | Quanto tempo uma votação dura, cinco segundos no mínimo. Ela se encerra antes no instante em que o resultado é certo |
| `reset.cooldownSeconds` | int | `60` | Quanto tempo após uma votação fracassada até que outra possa ser convocada. Um líder com `now` não é contido por isso |
| `reset.leadSays` | texto | `{player} reset the round` | Dito a todos quando a rodada é redefinida de imediato, sendo `{player}` quem a redefiniu. Vazio não diz nada |
| `reset.voteSays` | texto | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | Dito a todos quando uma votação é convocada, sendo `{player}` quem a convocou. Vazio não diz nada |
| `reset.tallySays` | texto | `Reset the round? {yes} yes, {no} no, {seconds}` | Exibido na barra de ação a cada segundo de uma votação, com `{seconds}` em contagem regressiva. Vazio não mostra nada |
| `reset.passSays` | texto | `The vote passed, so the round is reset` | Dito a todos quando uma votação passa. Vazio não diz nada |
| `reset.failSays` | texto | `The vote failed, so the round goes on` | Dito a todos quando uma votação fracassa. Vazio não diz nada |

Uma redefinição interrompe a rodada onde ela está. A classificação é exibida sob `The round was reset`, ninguém recebe a rodada, o intervalo faz a contagem regressiva, e o mapa é redefinido como se a rodada tivesse terminado com `ends.resets`, de volta ao lobby onde `opens.by` é `leader`. Funciona quer a rodada fosse terminar por conta própria ou não, mas não no lobby, durante a contagem que abre uma rodada, nem depois que uma rodada acabou e a sua redefinição está a caminho; uma votação ainda em andamento nesse momento é descartada.

Todo jogador online em um lado vota, seja qual for o lado, com `/rdpl round vote yes` ou `no`, e pode mudar o voto enquanto ela corre. Quem convoca a votação já votou sim, e um jogador que não votou quando o tempo acaba conta como não. Em um pacote com lados, um jogador sem lado nenhum não convoca nem vota; em um pacote sem lados, todo jogador online o faz. Vale o primeiro arquivo de pontuação cujo `reset` permita a alguém redefinir.

### Resultados

*pontuação*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `results.card` | booleano | `false` | Mostra a classificação como uma carta em vez de chat |
| `results.title` | texto | o nome e `results` | O título da carta |
| `results.icon` | texto | vazio | Um item desenhado na carta, p. ex. `minecraft:tnt` |
| `results.image` | texto | vazio | Uma imagem desenhada na carta no lugar de um item |
| `results.background` | texto | uma ardósia escura | A cor de fundo da carta |
| `results.seconds` | int | `8` | Quanto tempo a carta permanece, no mínimo um segundo |

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

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam. Cada arquivo é uma invasão.

Uma invasão é a que o jogo tem a partir da 1.14, construída sobre as vilas que a 1.12.2 já mantém. Ela começa quando um jogador com o efeito `omen` está dentro de uma vila: o efeito é retirado, e uma barra de chefe aparece para todo jogador a até `reach` do centro da vila. Após `waveDelay` ticks a primeira onda chega em um anel ao redor da vila e avança em direção ao centro, atacando jogadores, aldeões e golens de ferro pelo caminho. Os invasores nunca ferem nem miram uns aos outros, então uma flecha ou golpe perdido entre dois deles não faz nada. A barra mostra a vida que a onda ainda tem, e conta os invasores quando restam dois ou menos. Quando uma onda acaba, a próxima espera `waveDelay` ticks. Quando a última onda acaba e nada volta por dois segundos, a invasão está vencida; quando todo aldeão está morto ou a própria vila desapareceu depois que uma onda veio, ela está perdida. De qualquer forma, a barra avisa por trinta segundos, e a função correspondente roda como cada jogador ao alcance.

Uma invasão em andamento é salva com o mundo, e os seus invasores retomam a marcha após um recarregamento. Ela termina sem desfecho em modo pacífico, após `timeout` ticks, ou quando nenhum ponto ao redor da vila comporta uma onda. Uma vila só conta depois que um aldeão encontrou as suas portas, então uma invasão precisa de uma vila que o jogo tenha notado.

Enquanto uma onda está sobre a vila, os seus aldeões correm para dentro até a porta mais próxima que a vila conhece e ficam lá. Os invasores derrubam as portas de madeira no caminho para chegar a eles, doze segundos por porta, nas dificuldades normal e difícil enquanto `mobGriefing` está ligado; portas de ferro resistem. Um bloco do tipo `bell` é um sino de vila onde quer que esteja, e toca como [Sinos](#sinos) descreve; o `bell` da invasão nomeia qualquer outro bloco para tocar como um. Todo sino da vila toca quando uma onda chega, e um bloco nomeado também toca quando um jogador o usa: aldeões a até 48 blocos se escondem por quinze segundos e invasores a até 48 blocos brilham por três. Nada gera um sino. Um pacote que queira um define o bloco, o coloca em uma estrutura NBT e posiciona essa estrutura na vila, como um terreno ou como a substituição do poço, de modo que o sino fique onde os aldeões vivem.

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

### A invasão

*invasões*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `omen` | nome de efeito | nenhum, obrigatório | O efeito que inicia a invasão quando quem o tem está dentro de uma vila. Qualquer efeito registrado serve, inclusive uma poção do próprio pacote |
| `name` | texto | `Raid` | O título da barra de chefe |
| `color` | texto | `red` | A cor da barra: `pink`, `blue`, `red`, `green`, `yellow`, `purple` ou `white` |
| `waves` | lista de ondas | nenhum, obrigatório | Cada onda é uma lista de grupos, e as ondas vêm em ordem |
| `waveDelay` | int | `300` | Ticks antes da primeira onda, e entre o fim de uma onda e a próxima |
| `spawnDistance` | int | `32` | A que distância do centro da vila uma onda chega. As primeiras tentativas são ao dobro disso, depois a esta distância, depois dentro da vila |
| `reach` | int | `96` | Jogadores a até este número de blocos do centro veem a barra, e a função final roda como eles. Um invasor que se afasta dezesseis blocos além disso sai da invasão |
| `timeout` | int | `48000` | Ticks após os quais uma invasão inacabada termina sem desfecho. `0` nunca a encerra |
| `sound` | nome de som | nenhum | Tocado a todo jogador ao alcance, do lado de onde a onda vem, quando cada onda chega |
| `wins` | função | nenhum | Roda como cada jogador ao alcance quando a invasão é vencida |
| `loses` | função | nenhum | Roda como cada jogador ao alcance quando a invasão é perdida |
| `bell` | nome de bloco ou lista | nenhum | Outros blocos que tocam como um sino, quando um jogador os usa e sempre que uma onda chega. Um bloco do tipo `bell` toca sem ser nomeado. Posicione-o na vila por meio de uma estrutura NBT, já que nada o gera |

### Um grupo

*invasões*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `entity` | nome de entidade | nenhum, obrigatório | O que vem. Uma variante de entidade mantém todo o seu comportamento e ganha a marcha |
| `count` | int ou `{ "min", "max" }` | `1` | Quantos vêm |

---

## Cartas

*modos de jogo*

`<namespace>/cards/*.json`

O nome do arquivo é você quem escolhe, apenas a pasta é lida, e vários arquivos se acumulam. Cada arquivo é uma regra, e o seu id é `<namespace>:<file name>`. Uma regra espera um gatilho, verifica o seu `when` e mostra uma carta ao seu público; ela pode também executar uma função. Nada precisa estar no cliente: um jogador sem o mod recebe uma carta de canto como linhas de chat e uma carta central como um título.

Toda mensagem que este mod diz por conta própria é uma regra integrada, listada abaixo. Um pacote muda uma escrevendo um arquivo com esse id, `rdpl/cards/<name>.json`, que não precisa de gatilho: o que ele deixar de fora permanece como é hoje, e `{text}` representa a mensagem que o mod teria dito. Um pacote que não escreve nenhuma delas vê todas as mensagens como antes.

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

O terceiro saúda um jogador na primeira entrada com uma carta central sem painel por trás, apenas o seu texto e a sombra do texto, desenhados na fonte própria do pacote.

### Gatilhos

*cartas*

| Gatilho | Precisa de | Dispara quando |
| --- | --- | --- |
| `command` | nada | `/rdplserver card <rule> [players]` é executado. O comando ignora `when`, `repeat` e `cooldown`, e ainda executa `runs`. Qualquer regra pode ser mostrada assim, seja qual for o seu gatilho |
| `first_join` | nada | Um jogador entra no mundo pela primeira vez |
| `dimension_enter` | `dimension` | Um jogador chega a essa dimensão |
| `biome_enter` | `biomes` | Um jogador entra em um desses biomas vindo de outro lugar |
| `structure_enter` | `structures` | Um jogador entra em uma dessas estruturas vindo de fora dela |
| `advancement` | `advancement` | Um jogador conquista esse avanço |
| `time_of_day` | `time` | O relógio do dia passa por esse tick, `0` a `23999`, enquanto há jogadores na dimensão. Um relógio ajustado por comando ou por uma cama não conta |
| `day` | nada, ou `day` | Um novo dia começa na dimensão; com `day`, apenas esse dia |
| `craft` | `item` | Um jogador fabrica esse item |
| `pickup` | `item` | Um jogador pega esse item |
| `kill` | `entity` | Um jogador mata essa entidade, ou a de número `count` dela |
| `respawn` | nada | Um jogador renasce após morrer |
| `death` | nada | Um jogador morre |
| `y_level` | `below` ou `above` | Um jogador desce abaixo ou sobe acima dessa altura |
| `play_time` | `minutes` | O tempo de um jogador no mundo atinge esse número de minutos, contado a partir do fechamento da introdução do mundo, ou da entrada quando nenhuma introdução é mostrada a ele |
| `score` | `objective` | A pontuação de um jogador nesse objetivo atinge `score` |

Bioma, estrutura, altura, tempo de jogo e pontuação são verificados uma vez por segundo para cada jogador, e disparam na mudança de fora para dentro, nunca na primeira verificação após uma entrada. Uma regra `time_of_day` ou `day` cujo público não seja `player` dispara uma vez para a dimensão, em vez de uma vez para cada jogador nela.

Uma carta que dispara enquanto um jogador ainda tem a introdução do mundo aberta espera e é mostrada quando a introdução fecha, seja qual for o seu gatilho, inclusive o `command`. Ela é descartada se o jogador sair antes disso.

### Configurações de gatilhos

*cartas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `trigger` | texto | nenhum, obrigatório | Um dos gatilhos acima. Uma regra integrada não usa nenhum |
| `dimension` | texto | nenhum | Um id de dimensão como `-1`, ou o seu nome como `the_nether`. Para `dimension_enter` é a dimensão em que se entra; para todos os outros gatilhos limita a regra aos jogadores nessa dimensão |
| `biomes` | lista | nenhum | Nomes de biomas como `minecraft:desert`, ou `#TYPE` para um tipo de bioma do Forge como `#SNOWY` |
| `structures` | lista | nenhum | `Village`, `Temple`, `Mansion`, `Monument`, `Mineshaft`, `Stronghold`, `Fortress` ou `EndCity`, ou o nome de uma estrutura que um pacote posiciona por meio de `structures`, que conta dentro de `radius` de onde foi posicionada |
| `radius` | int | `32` | Que proximidade conta como dentro de uma estrutura do próprio pacote |
| `advancement` | texto | nenhum | O id do avanço |
| `item` | texto | nenhum | O item, escrito como em outros lugares de um pacote, como `minecraft:diamond_sword` |
| `entity` | texto | nenhum | O id da entidade, como `minecraft:zombie` |
| `count` | int | `1` | Para `kill`: quantas mortes são necessárias. A contagem recomeça depois que a regra dispara |
| `below`, `above` | int | nenhum | Para `y_level`: a altura abaixo ou acima da qual ir |
| `time` | int | `0` | Para `time_of_day`: o tick do dia |
| `day` | int | nenhum | Para `day`: o único dia em que disparar. Sem ele, todo dia |
| `minutes` | int | nenhum | Para `play_time` |
| `objective`, `score` | texto, int | nenhum, `1` | Para `score`: o objetivo e o valor a atingir |
| `requires` | lista de ids de mods ou namespaces de pacotes | nenhum | O arquivo é ignorado a menos que todos estejam presentes |

### Quando

*cartas*

`when` contém condições que devem ser todas verdadeiras no momento em que o gatilho dispara.

| Configuração | Tipo | O que verifica |
| --- | --- | --- |
| `biomes` | lista | O jogador está em um destes biomas, escritos como no gatilho |
| `timeFrom`, `timeTo` | int | O relógio do dia está dentro desta janela, que pode passar da meia-noite, como `13000` a `1000` |
| `dayAtLeast` | int | O número do dia é pelo menos este |
| `advancement` | texto | O jogador tem este avanço |
| `gameMode` | texto | O jogador está neste modo de jogo: `survival`, `creative`, `adventure` ou `spectator` |
| `team` | texto | O jogador está nesta equipe do placar |
| `objective`, `scoreAtLeast` | texto, int | A pontuação do jogador no objetivo é pelo menos esta |

### A carta

*cartas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `title` | texto | nenhum | A primeira linha, desenhada maior em uma carta central |
| `lines` | lista | nenhum | Até dezesseis linhas. Uma regra precisa de um título ou de linhas, exceto uma integrada. `{player}`, `{dim}`, `{biome}` e `{day}` são preenchidos; `{text}` é a mensagem integrada e, em uma linha própria, fornece todas as linhas dela |
| `style` | texto | `corner` | `corner` é a carta no canto inferior direito que `saysCard` mostra; `center` é uma carta no meio da tela; `chat` são linhas de chat; `bar` é a barra de ação |
| `icon` | texto | `saysIcon` | Um item desenhado em uma carta de canto. Vazio não desenha nenhum |
| `color` | texto | `saysColor` | A cor de fundo da carta em hexadecimal |
| `image` | texto | `saysImage` | Um PNG dos assets de cliente do pacote, esticado sobre a carta como fundo |
| `background` | booleano | `saysBackground` | `false` elimina o painel, a sua borda e a faixa de cor; o texto mantém a sombra, e uma `image` ainda é desenhada |
| `font` | texto | `saysFont` | A fonte em que o texto da carta é desenhado, como `namespace:name`. Vazio usa a fonte RDPL |
| `ticks` | int | `160` | Quanto tempo a carta permanece, incluindo o esmaecimento |
| `audience` | texto | `player` | Quem a vê: `player`, `everyone`, `dimension` (todos na dimensão do jogador) ou `team` (a equipe do placar do jogador) |
| `repeat` | texto | `always` | `always`, `once_per_player`, `once_per_world` ou `once_per_session` (de novo depois que o jogador entra outra vez) |
| `cooldown` | int | `0` | Segundos antes de a regra disparar de novo para o mesmo jogador |
| `runs` | texto | nenhum | Uma função executada como o jogador quando a regra dispara |

Uma carta de canto vai para o chat quando `saysCard` está desligado. O que um jogador já viu é guardado com o jogador, então sobrevive à morte e à passagem entre dimensões; `once_per_world` é guardado com o mundo.

A fonte RDPL, `resourcedatapackloader:rdpl`, é a padrão para todo texto: cartas, mensagens de Says, as notas de boas-vindas e de espera, a introdução do mundo, e os menus, o chat, o HUD e as dicas do próprio jogo. Seus estilos negrito e itálico são `resourcedatapackloader:rdpl_bold` e `resourcedatapackloader:rdpl_italic`. As letras da mesa de encantamento continuam as do jogo.

O RDPL inclui estas fontes e caracteres. O `font` de uma carta, nota ou introdução pode nomear uma fonte RDPL pelo seu nome curto ou pelo id completo:

| Nome | O que desenha |
| --- | --- |
| `rdpl` (ou `resourcedatapackloader:rdpl`) | A fonte RDPL, com cirílico (U+0400 a U+04FF) e o alfabeto rúnico (U+16A0 a U+16F8) |
| `rdpl_runic` (ou `resourcedatapackloader:rdpl_runic`) | Uma cifra de runas: as letras A a Z e a a z são desenhadas como runas, e todo outro caractere é desenhado na fonte RDPL. Um trecho em negrito é desenhado em `rdpl_runic_bold` e um em itálico em `rdpl_runic_italic` |
| Runas, U+16A0 a U+16F8 | Escritas como os próprios caracteres rúnicos (ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ), em qualquer texto que a fonte RDPL desenhe, chat incluído; trechos em negrito e itálico mantêm o seu estilo |

Uma fonte de carta é um PNG em `assets/<namespace>/textures/font/<name>.png`, uma grade de 16 por 16 glifos disposta como o `ascii.png` do próprio jogo, e a carta é dimensionada pelas larguras de glifo lidas dele. Um `<name>_cyrillic.png` ao lado, a mesma grade contendo Unicode U+0400 a U+04FF, desenha cirílico; sem ele, o cirílico vem das páginas do próprio jogo. Um `<name>_runes.png`, a mesma grade contendo U+1600 a U+16FF, desenha runas da mesma forma. As versões 1.20.1 e 1.21.1 leem em vez disso uma definição de fonte em `assets/<namespace>/font/<name>.json`, cujo provedor `bitmap` pode apontar para o mesmo PNG, de modo que um pacote que inclua os dois arquivos desenha as mesmas letras nas três versões. `minecraft:default` nomeia a fonte do jogo. Uma fonte que nenhum pacote tenha recai na fonte do jogo, com um aviso em `rdpl.log`.

Um pacote muda a fonte RDPL incluindo o seu próprio `assets/resourcedatapackloader/textures/font/rdpl.png` (e `rdpl_bold.png`, `rdpl_italic.png` e as suas folhas `_cyrillic.png` e `_runes.png`), que a substitui em todo lugar, inclusive no texto do jogo. Um pacote que inclua `assets/minecraft/textures/font/ascii.png`, uma cópia da do vanilla por exemplo, dá essa fonte ao texto do próprio jogo, com cirílico e runas das páginas do jogo; o texto do próprio RDPL mantém a fonte RDPL a menos que `saysFont` seja `minecraft:default`.

Títulos e linhas de cartas, mensagens de Says e as notas de boas-vindas e de espera aceitam as marcas em linha da tabela em Introdução do mundo, Formatação de texto: negrito, itálico, negrito itálico, tachado, código, links e trechos rúnicos. Um trecho em negrito é desenhado no estilo `_bold` da fonte e um em itálico no seu estilo `_italic`; para uma fonte sem esse estilo, o trecho recebe o estilo negrito ou itálico do jogo, e a carta é dimensionada pelos trechos como desenhados. Jogadores sem o mod recebem as mesmas marcas como formatação de chat, e um trecho rúnico como as suas letras simples.

### Regras integradas

*cartas*

| Id | A mensagem | De onde vem o texto |
| --- | --- | --- |
| `rdpl:gate_unlocked` | Um portal abre | `unlockedMessage` em [Portais](#portais) |
| `rdpl:gate_blocked` | Um portal fechado faz um jogador voltar, na barra de ação | `blockedMessage` em [Portais](#portais) |
| `rdpl:team_joined` | Um jogador entra em um lado | o `displayName` do lado |
| `rdpl:team_lead` | A liderança de um lado chega a um jogador | `leadSays` em [Equipes](#equipes) |
| `rdpl:team_picked` | Um jogador é sorteado para um lado | o `displayName` do lado |
| `rdpl:team_round_ended` | A rodada terminou, então um jogador é movido para um lado | o `displayName` do lado |
| `rdpl:lobby_joins` | Um jogador que entra no meio de uma rodada é enviado ao lobby | `opens.joinsSays` em [O lobby](#o-lobby) |
| `rdpl:lobby_note` | A linha do lobby no meio da tela | `opens.says`, `opens.leaderSays` em [O lobby](#o-lobby) |
| `rdpl:scoring_results` | A classificação no fim de uma rodada, para cada jogador | `results.card`, `results.title`, `results.icon`, `results.image`, `results.background`, `results.seconds` em [Resultados](#resultados) |
| `rdpl:scoring_out` | Um jogador eliminado | `ends.outSays` em [Como uma rodada termina](#como-uma-rodada-termina) |
| `rdpl:reset_lead` | O líder redefine a rodada | `reset.leadSays` em [Redefinindo uma rodada](#redefinindo-uma-rodada) |
| `rdpl:reset_vote` | Uma votação de redefinição é convocada | `reset.voteSays` |
| `rdpl:reset_pass` | A votação passa | `reset.passSays` |
| `rdpl:reset_fail` | A votação fracassa | `reset.failSays` |
| `rdpl:anvil_waits` | O trabalho de uma bigorna espera por um avanço | [Trabalho na bigorna](#trabalho-na-bigorna) |
| `rdpl:threat` | A faixa de ameaça de um jogador muda | `threatSays` |
| `rdpl:prospect` | Cada linha que uma descoberta de prospecção relata | a descoberta |
| `rdpl:prospect_none` | A prospecção não encontrou nada | o arquivo de idioma |
| `rdpl:board_result` | Uma partida de tabuleiro termina | o arquivo de idioma |
| `rdpl:pregen_ended` | A pré-geração termina ou para | `pregenFinishedSays`, `pregenStoppedSays` em [Pré-geração](#pré-geração) |
| `rdpl:pregen_running` | A linha de progresso que um jogador vê ao entrar durante a pré-geração | `pregenRunningSays` |

`welcomeSays` não é uma regra e mantém o seu logotipo; uma regra `first_join` ou `dimension_enter` se soma a ele. As contagens regressivas e os placares na barra de ação de uma rodada permanecem como as suas configurações os fazem.

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

Tudo que interrompe ou altera a geração é agrupado, e cada grupo tem uma chave na categoria `control` da config com três valores:

| Valor | O que significa |
| --- | --- |
| `default` | O pacote decide. Os valores da config são o recurso reserva |
| `global` | A config vence. As seções do pacote são ignoradas |
| `off` | O grupo é desativado por completo e nenhum pacote pode ativá-lo |

Os grupos são `ores`, `biomes`, `generators`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `entities`, `chunks`, `commands` e `server`.

As configurações se resolvem **bioma → modelo de mundo → config**. O bloco `settings` de um modelo de mundo usa os mesmos nomes de chave da config, então um pacote as define da mesma forma que você faria:

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

Com o controle de um grupo em `default` elas vencem, em `global` são ignoradas, e em `off` o grupo inteiro não faz nada, não importa o que qualquer pacote diga.

## O que cada grupo faz

*controle*

### Minérios

*o que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `blockOres` | booleano | `false` | Impede todo mod e o Minecraft de gerar minério, exceto os mods nomeados em `oreWhitelist`. Só a geração que passa pelo evento de geração de minério do Forge pode ser alcançada, que é o Minecraft e a maioria dos mods, mas não todos |
| `oreWhitelist` | lista de ids de mods | `["minecraft"]` | Os mods ainda autorizados a gerar minério enquanto `blockOres` está ligado |
| `oreTypes` | lista de tipos de minério | nenhum | A quais tipos de minério o bloqueio se aplica, escritos como nomes do Forge, `COAL`, `IRON`. Vazio significa todos os tipos |
| `oreTypesAreBlacklist` | booleano | `true` | Ligado, os tipos em `oreTypes` são os bloqueados. Desligado, só esses tipos são gerados |
| `blockOreDimensions` | lista de ints | nenhum | As dimensões às quais o bloqueio de minério se aplica, vazio significando todas. Uma dimensão fora do escopo não é tocada de forma alguma, então os minérios de outro mod são gerados lá enquanto o Overworld continua bloqueado |
| `blockOreDimensionsAreBlacklist` | booleano | `false` | Ligado, as dimensões listadas são as deixadas em paz |
| `prospectItems` | lista de `item=entries` | nenhum | Itens que prospectam entradas de worldgen em forma de `vein` quando um jogador agachado quebra um bloco com um deles na mão. A forma está no parágrafo abaixo |
| `prospectItemsAreBlacklist` | booleano | `false` | Ligado, a lista de cada item são as entradas que ele não lê |
| `prospectWear` | int | `2` | Quantas vezes o desgaste normal uma quebra de prospecção custa à ferramenta. `2`, o dobro, é o mínimo permitido, e um item sem durabilidade não paga nada |
| `prospectSlow` | int | `2` | Quantas vezes mais tempo um jogador agachado com um item marcado leva para quebrar um bloco. `1` é a velocidade normal |
| `prospectDrops` | booleano | `false` | Ligado, um bloco quebrado no modo de prospecção ainda solta itens e dá experiência. Desligado, a amostra é consumida |

**A leitura.** Uma entrada de `prospectItems` é escrita `item=entry|entry[,radius in chunks]`, ou `item=*[,radius]` para toda entrada de veio, com o raio padrão sendo 8. Um jogador agachado que quebra um bloco com um item assim na mão é informado, para cada entrada que ele lê, `Possible hit on <ore> <direction> of this location, <deeper down | higher up | at about this depth>` — um de oito pontos cardeais do bloco quebrado até o veio semeado mais próximo, e nunca uma posição; `at this location` quando o bloco já está ao alcance do veio, e `No sign of anything here` quando nada está semeado no raio. O minério é nomeado pelo `prospectAs` da entrada, ou então pelo nome do arquivo. A leitura reproduz os mesmos sorteios que a geração faz, então acerta mesmo em terreno ainda não criado, e um item marcado diz o que prospecta na sua dica.

### Biomas

*o que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `blockBiomes` | booleano | `false` | Impede todo bioma de ser gerado, exceto os dos mods em `biomeWhitelist`. Os biomas bloqueados são substituídos no mapa de biomas já pronto, a única forma de alcançar oceanos, ilhas de cogumelo, variantes de mesa, selva, colinas e costas. Bloqueie todos e o Overworld vira um mundo vazio por conta própria |
| `biomeWhitelist` | lista de ids de mods | `minecraft` | Os mods cujos biomas ainda são gerados enquanto `blockBiomes` está ligado. Um bioma de pacote usa o namespace do pacote |
| `biomeNames` | lista de nomes de biomas | nenhum | Biomas aos quais isto se aplica por nome, quem quer que seja o dono e diga o que disser a whitelist. Um nome amigável como `Birch Forest` ou um nome de registro |
| `biomeNamesAreBlacklist` | booleano | `true` | Ligado, os nomes em `biomeNames` são bloqueados. Desligado, só esses nomes são gerados |
| `blockBiomeDimensions` | lista de ints | `0`, o Overworld | As dimensões às quais o bloqueio de biomas se aplica. Vazio significa todas |
| `blockBiomeDimensionsAreBlacklist` | booleano | `false` | Ligado, o bloqueio pula as dimensões listadas. Desligado, aplica-se apenas a elas |

`blockBiomes` e `biomeWhitelist` funcionam por mod, e `biomeNames` com `biomeNamesAreBlacklist` por nome. Os biomas bloqueados são substituídos no mapa de biomas já pronto, que é a única forma de alcançar oceanos, ilhas de cogumelo, variantes de mesa, selva, colinas e costas, que são escolhidos fora das listas que um mod pode editar. Bloqueie todo bioma e o Overworld vira um mundo vazio por conta própria. `blockBiomeDimensions` limita tudo isso a certas dimensões, vazio significando todas, e `blockBiomeDimensionsAreBlacklist` transforma essa lista em uma exclusão.

### Geradores

*O que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `blockWorldGenerators` | booleano | `false` | Impede que outros mods gerem por meio de seus próprios geradores de mundo, que é como os mods adicionam o que os eventos do Forge nunca enxergam: ilhas de slime, cristais de caverna e coisas do tipo. A geração dos pacotes deste mod nunca é bloqueada |
| `generatorWhitelist` | lista de ids de mods | `minecraft` | Os mods que ainda podem executar seus próprios geradores |
| `blockedGenerators` | lista de ids de mods ou trechos de nomes de classe | nenhum | Geradores individuais bloqueados de vez, independentemente da whitelist |
| `blockGeneratorDimensions` | lista de inteiros | `0`, o overworld | As dimensões às quais isso se aplica. Vazio significa todas |
| `blockGeneratorDimensionsAreBlacklist` | booleano | `false` | Ligado, o bloqueio ignora as dimensões listadas. Desligado, ele se aplica somente a elas |
| `generatorTypes` | lista de tipos | nenhum | Bloqueia pelo que um gerador produz em vez de por quem é o dono dele: `ores`, `structures`, `flora`, `lakes`, `terrain`, ou `unknown` para os que nada identificou |
| `generatorTypesAreBlacklist` | booleano | `true` | Ligado, os tipos listados são bloqueados. Desligado, somente esses tipos geram |
| `generatorTypeMap` | lista de `pattern=type` | nenhum | Tipos para geradores que o nome da classe não descreve, sendo o padrão um id de mod ou parte do nome de uma classe de gerador. As entradas mapeadas são verificadas antes das palavras integradas, então também corrigem um gerador que as palavras interpretam errado |
| `logBlockedGenerators` | booleano | `true` | Registra no log cada gerador com o tipo que recebeu na primeira vez em que é bloqueado. `/rdplserver generators` mostra os totais acumulados por mod e tipo |

`blockWorldGenerators` impede que outros mods gerem por meio de seus próprios geradores de mundo, que é como os mods adicionam o que os eventos do Forge nunca enxergam: ilhas de slime, cristais de caverna e coisas do tipo. `generatorWhitelist` mantém os mods citados, `blockedGenerators` nomeia geradores individuais, e a geração dos pacotes deste mod nunca é bloqueada. `blockGeneratorDimensions` limita isso a certas dimensões, com `blockGeneratorDimensionsAreBlacklist` para inverter a lista.

`generatorTypes` bloqueia pelo que um gerador produz em vez de pelo mod que é dono dele: `ores`, `structures`, `flora`, `lakes`, `terrain`, ou `unknown` para os que nada identificou. `generatorTypesAreBlacklist` define o sentido: ligado, os tipos listados são bloqueados; desligado, somente os tipos listados geram. Um tipo bloqueia independentemente da whitelist, da mesma forma que `oreTypes`, então você pode impedir todos os mods de adicionar minérios deixando em paz suas dungeons e árvores.

O tipo vem do nome da classe do gerador, comparado com uma lista integrada de palavras por tipo. Isso identifica corretamente a maioria dos mods, `NetherOreGenerator` é minérios, `SlimeIslandGenerator` é estruturas, mas um gerador batizado sem nada de específico, como o `SimpleGenHandler` do ProjectRed ou o `DEWorldGenHandler` do Draconic Evolution, acaba como `unknown`. `generatorTypeMap` corrige esses casos à mão, um `pattern=type` por linha, em que o padrão é um id de mod ou parte do nome de uma classe de gerador:

```
mrtjpcore=ores
deworldgenhandler=structures
```

As entradas mapeadas são verificadas antes das palavras integradas, então também corrigem um gerador que as palavras interpretam errado. Ative `logBlockedGenerators` e cada gerador é registrado no log com o tipo que recebeu na primeira vez em que é bloqueado, e `/rdplserver generators` mostra os totais acumulados por mod e tipo.

### Substituições

*O que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `blockReplacements` | lista de `block=block` | nenhum | Blocos trocados em chunks que já existem, com um meta opcional de cada lado. Cada chunk é processado uma vez ao carregar e marcado em seus próprios dados, de modo que nunca é processado duas vezes |
| `blockReplacementDimensions` | lista de inteiros | nenhum | As dimensões às quais isso se aplica. Vazio significa todas |
| `blockReplacementDimensionsAreBlacklist` | booleano | `false` | Ligado, a substituição ignora as dimensões listadas. Desligado, ela se aplica somente a elas |
| `blockReplacementMinHeight` | inteiro | `0` | O menor y que ela examina |
| `blockReplacementMaxHeight` | inteiro | `255` | O maior y que ela examina |
| `blockReplacementKey` | string | `0000` | Mude-a e todos os chunks passam pela substituição de novo |

`blockReplacements` troca blocos em chunks que já existem, um `block=block` por linha, com um meta opcional de cada lado:

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

Cada chunk é processado uma vez, quando carrega do disco, e marcado nos dados do próprio chunk para nunca ser processado duas vezes. Um chunk gerado pela primeira vez é limpo na próxima vez que carrega, e não de imediato, porque os chunks vizinhos ainda estão escrevendo nele enquanto ele é gerado. Um chunk na borda da área explorada é limpo, mas não marcado, de modo que é limpo de novo quando o terreno ao redor passa a existir. `blockReplacementDimensions` e `blockReplacementDimensionsAreBlacklist` escolhem onde, `blockReplacementMinHeight` e `blockReplacementMaxHeight` escolhem a faixa do mundo a examinar, e `blockReplacementKey` é uma string que você muda para fazer todos os chunks passarem por isso de novo. Funciona com `retrogen` ligado ou não, já que um mundo que precisa de limpeza costuma ser um mundo ao qual você não quer adicionar veios novos. Ela apenas troca blocos: algo que um mod gerou como estrutura não pode ser removido por esse meio, porque o terreno que ele substituiu nunca foi registrado.

### Vilas

*O que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageBlocks` | lista de `original=replacement` | nenhum | Os blocos com que as peças da vila são construídas, aplicados depois que todos os outros mods já disseram o que tinham a dizer. Um par pode levar uma chance e uma condição e passa então a ser uma regra; os campos estão na tabela abaixo |
| `villagePieces` | lista de nomes de peças | nenhum | Peças de vila do vanilla nomeadas uma por linha: `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1`, `field2`. Um terreno de pacote é nomeado pelo seu próprio modelo, e o mesmo vale para peças que outros mods adicionam |
| `villagePiecesAreBlacklist` | booleano | `true` | Ligado, as peças listadas são bloqueadas. Desligado, somente essas peças geram, e uma whitelist só remove peças do próprio vanilla |
| `villagePlotsLeast` | inteiro | `0` | O mínimo de terrenos construídos com que uma vila se contenta, contando casas, fazendas e terrenos de pacote, mas nunca estradas, tochas ou o poço. Uma vila que sai menor é regenerada algumas vezes e vence o maior layout. `0` mantém o vanilla |
| `villagePlotsBackRow` | booleano | `true` | Depois que a vila cresce, uma segunda passada coloca um terreno diretamente atrás de cada terreno que dá para uma rua, virado para ela, com o mesmo sorteio e o mesmo teste de espaço, de modo que o interior de um quarteirão entre duas ruas seja construído em vez de ficar vazio |
| `villagePlotsMost` | inteiro | `0` | O máximo que ela pode ter; no máximo, ela para de crescer de vez, sem mais construções e sem mais estradas. `0` mantém o vanilla |
| `villageTieStreets` | booleano | `true` | Ligado, um distrito que não consegue estender suas ruas até a vila já estabelecida ganha uma rua de ligação reta até a rua mais próxima com a qual se alinha. Desligado, esse distrito é desfeito |
| `villageBlockSizes` | lista de `size=weight` | nenhum | Qual a profundidade dos quarteirões entre as ruas paralelas de uma cidade, sorteada uma vez por distrito a partir da posição da sua praça. Vazio dimensiona todos os quarteirões pelo maior terreno que o pacote traz |
| `villageLayout` | string | vazio | Nomeia um [mapa de layout de cidade](#mapas-de-layout-de-cidades) que organiza a vila a partir de uma planta de ruas desenhada em vez de fazê-la crescer |

As vilas usam as mesmas listas `structure=value` que toda outra estrutura, sob o nome `villages`, então `structureSpacing`, `structureMinDistanceFromSpawn`, `structureBiomes` e `structureBiomesAreBlacklist` todas as alcançam. Uma lista `structureBiomes` que não seja blacklist também adiciona qualquer bioma citado que a lista própria da estrutura nunca continha, de modo que as vilas podem ser enviadas para as montanhas; nomeie-os pelo nome de registro para isso, já que somente nomes de registro podem adicionar. O espaçamento delas tem um piso de 9, porque o vanilla subtrai 8 dele. `villagePieces` pertence ao mesmo grupo, então um único interruptor cobre tudo sobre onde as vilas vão e do que são construídas, enquanto o grupo `villages` cobre apenas os terrenos que um pacote adiciona.

`villageBlocks` substitui os blocos com que uma vila é construída, como pares `original=replacement`: `minecraft:cobblestone=mypack:ruby_brick`. É aplicado depois que todos os outros mods já disseram o que tinham a dizer, então um pacote sempre vence, até contra mods que trocam os materiais das vilas por bioma. Os dois lados aceitam um nome de bloco simples ou um nome com estados. As estradas são nomeadas separadamente por `villagePathBlock` e seus irmãos.

Um par pode levar uma chance e uma condição depois dele, escritas como campos separados por vírgulas, e então passa a ser uma regra em vez de uma troca simples. `minecraft:cobblestone=minecraft:mossy_cobblestone,20` desgasta um quinto do pedregulho que uma vila assenta; `minecraft:planks=minecraft:sandstone,100,under=minecraft:sand` muda o piso apenas onde uma casa fica sobre areia. Os campos depois do par podem vir em qualquer ordem, e uma entrada que cita um campo que não sabe ler é recusada por inteiro em vez de aplicada pela metade.

| Campo | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| chance | inteiro, 1 a 100 | `100` | Com que frequência a regra pega, em cem |
| `at=` | nome de bloco | nenhum | Somente onde este bloco já está no ponto em que se constrói |
| `under=` | nome de bloco | nenhum | Somente onde este bloco está diretamente abaixo do ponto |

Um par simples é respondido quando uma peça pergunta ao jogo do que deve ser construída, então muda de uma vez todas as paredes daquele bloco. Uma regra é avaliada onde o bloco é de fato assentado, um ponto por vez, o que permite que uma chance e uma condição signifiquem alguma coisa, e ela enxerga o bloco como ele está prestes a ser colocado, depois que qualquer par simples já disse o que tinha a dizer. Em quais pontos uma chance cai é calculado a partir da seed do mundo e do próprio ponto, de modo que o mesmo mundo sempre desgasta os mesmos blocos, não importa quantas vezes seja gerado.

As estradas nunca são regidas por regras, para que os declives, pontes e desenhos de cruzamento ainda reconheçam a estrada que assentaram. Um terreno de modelo assenta o seu próprio arquivo `.nbt` em vez de construir à maneira do jogo, então as regras não alcançam o interior dele; seus blocos são os do próprio arquivo. Pares simples e regras funcionam com `terrainAdaptation` ligado ou não.

`villagePieces` nomeia peças de vila do vanilla, `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1` e `field2`, e `villagePiecesAreBlacklist` define o sentido, de modo que você pode eliminar os campos de trigo do vanilla e manter as casas, ou listar as únicas peças que quer. Um terreno de pacote é nomeado pelo seu próprio modelo: o nome completo, `mypack:big_house`, ou apenas `big_house`, ou o nome do próprio terreno, se preferir. Assim um pacote pode trazer dez terrenos e um modelo de mundo pode descartar um deles sem tocar nos outros nove. O mesmo vale para peças que outros mods adicionam, entre elas as casas do Tektopia ou os terrenos do Recurrent Complex: uma whitelist só remove peças do próprio vanilla, então listar as do vanilla que você quer não apagará discretamente as de outra pessoa. Para descartar uma peça de mod, use uma blacklist e nomeie-a, `tekhouse2` e afins.

`villagePlotsLeast` e `villagePlotsMost` limitam com quantos terrenos uma vila é construída, contando casas, fazendas e terrenos de pacote, nunca estradas, tochas ou o poço. Uma vila que sai abaixo do mínimo é regenerada em tamanho maior, algumas tentativas, e vence o maior layout, de modo que um terreno apertado ainda pode ficar aquém do pedido. No máximo, a vila para de crescer de vez: sem mais construções e sem mais estradas. `0` em qualquer das pontas mantém o comportamento do vanilla ali.

`villageTieStreets`, ligado a menos que se defina o contrário, assenta uma rua de ligação para um distrito que não consegue estender suas ruas até a vila já estabelecida: uma rua reta de largura total, de uma das pontas de rua do distrito até a rua estabelecida mais próxima com a qual se alinha, quando essa linha é mais longa que a largura de uma estrada, não passa de 112 fileiras, está livre de qualquer peça, não fica ao lado de uma rua paralela, não atravessa um túnel e é plana o bastante para andar. Sem ela, esse distrito é desfeito, o que mantém uma cidade em terreno acidentado restrita à sua praça e quatro ruas; com ela, o distrito se junta à vila e continua crescendo. Uma cidade em terreno plano raramente precisa dela, e ela vem ligada por padrão para que um pacote que pede uma cidade grande a receba; desligue-a para manter pequena uma cidade em terreno acidentado.

`villageBlockSizes` define qual a profundidade dos quarteirões entre as ruas paralelas de uma cidade, como entradas ponderadas `size=weight`: `32=3` e `64=1` colocam três distritos em quatro com quarteirões de 32 de profundidade e o restante com 64. Cada distrito sorteia seu tamanho uma vez a partir da posição da sua praça, de modo que o mesmo mundo sempre recebe a mesma mistura. Suas ruas ramificam ruas laterais ao longo do comprimento a cada dois quarteirões mais a largura de uma estrada, então um distrito de 16 é uma malha fina e um de 64 uma malha grossa, e duas ruas paralelas mantêm entre si essa quantidade de blocos mais a do distrito vizinho, de modo que um distrito de 32 ao lado de um de 64 deixa 96 entre elas. Apenas terrenos que cabem na profundidade são construídos ao longo das ruas de um distrito, e entre os que cabem, a chance de um terreno é o seu peso vezes a sua largura, então quarteirões profundos favorecem os edifícios que os preenchem e um terreno de 64 de largura nunca fica de frente para um quarteirão de 32 de profundidade. O comprimento das ruas e o espaçamento das praças ainda seguem o maior terreno que o pacote traz, o que permite que todo distrito alcance a cidade. Vazio dimensiona todos os quarteirões por esse maior terreno e ramifica ruas somente nas pontas, como antes.

`villageLayout` nomeia um [mapa de layout de cidade](#mapas-de-layout-de-cidades) que organiza a vila a partir de uma planta de ruas desenhada em vez de fazê-la crescer; vazio cresce como de costume.

#### Estradas de vilas

*vilas*

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

Tudo o que vem abaixo só tem efeito enquanto `terrainAdaptation` estiver ligado. Todas as configurações são vazias ou zero por padrão, o que deixa as estradas do vanilla exatamente como eram.

**Misturando blocos.** Algumas configurações de bloco aceitam uma mistura em vez de um único bloco: blocos separados por vírgulas, cada um seguido de um espaço e um peso, como em `"minecraft:stonebrick 3, minecraft:cobblestone 1"`. Um bloco sem peso conta uma vez. Cada bloco assentado sorteia a mistura a partir da seed do mundo e do seu ponto, de modo que o mesmo mundo sempre constrói o mesmo padrão. As configurações que aceitam uma mistura são `villagePathVergeBlock`, `villagePathVergeWaterBlock`, `villagePathTunnelBlock`, `villagePathBridgeFrameBlock`, `villagePathBridgeFrameTopBlock`, `villageRailTunnelBlock`, `villageRailDeckBlock`, `villageRailSupportBlock`, `villageRailBarrierBlock`, `villageRailBridgeFrameBlock`, `villageRailBridgeFrameTopBlock`, `villageSubwayTunnelBlock`, `villageSubwayPlatformBlock`, `villageSubwayRailingBlock`, `villageSubwayBenchEndBlock` e `villageSewerMossBlock`. Toda outra configuração de bloco usa o primeiro bloco de uma mistura do início ao fim.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villagePathBlock` | bloco | vazio | A superfície da estrada. Vazio mantém o bloco que o bioma usaria: arenito sobre areia, argila endurecida na mesa, caminho de grama sobre terra |
| `villagePathVergeBlock` | bloco | vazio | O bloco com que se preenche o terreno ao lado de uma estrada e sob um terreno onde a vila precisa criar chão. Vazio segue o terreno, assentando o preenchimento do próprio bioma com grama por cima onde seria terra |
| `villagePathVergeWaterBlock` | bloco | `minecraft:planks` | No que esse preenchimento se transforma onde fica sobre a água, para que um acostamento levado até um lago não seja uma coluna de terra. Ele também reveste um degrau de pedra deixado sobre a água |
| `villagePathCenterBlock` | bloco | vazio | Uma linha central no meio da estrada. Vazio não desenha nenhuma |
| `villagePathCenterDash` | número | `0` | Tracejado dessa linha: N blocos de linha, depois um de estrada. Ancorado nas coordenadas do mundo, de modo que os traços de uma peça de estrada continuam na seguinte. `0` mantém a linha contínua |
| `villagePathLineBlock` | bloco | vazio | Linhas de borda entre a estrada e a calçada. Vazio não desenha nenhuma |
| `villagePathSidewalkBlock` | bloco | vazio | Calçadas, assentadas no mesmo nível da estrada fora das linhas de borda. Vazio não assenta nenhuma |
| `villagePathSidewalkWidth` | número | `2` | A largura de cada calçada, uma vez definido `villagePathSidewalkBlock` |
| `villagePathExtraWidth` | número | `0` | Blocos extras de estrada de cada lado além dos 3 do vanilla. Alarga as próprias peças de estrada, de modo que as casas ficam mais afastadas de uma rua larga |
| `villagePathMinimumWidth` | número | `0` | A estrada mais estreita que vale a pena assentar. Um trecho que não comporta seu acabamento completo cai para uma viela nua de 3 de largura; abaixo dessa largura ele não é assentado e a vila se organiza ao redor. `0` nunca recusa |
| `villagePathAlleyBlock` | bloco | vazio | A superfície de uma viela, uma estrada estreita demais para levar linhas e calçadas. Uma viela corre entre as calçadas das ruas que encontra e não leva nenhuma própria, e nenhuma faixa de pedestres é pintada onde ela encontra uma rua. Vazio assenta as vielas com o bloco da estrada |
| `villagePathAlleyChance` | número | `0` | A chance em porcentagem de uma estrada ser assentada como viela em vez de se alargar até uma rua completa. `0` assenta uma viela apenas onde uma rua completa não cabe, o que na prática é somente o primeiro distrito, apertado. Aumentá-la muda quais estradas são assentadas e, portanto, remodela todo o grafo de ruas; medido em 50, custou sete cruzamentos divididos a mais, então aumente-a e confira o resultado |
| `villagePathFlatRun` | número | `6` | Quantos blocos uma estrada mantém a mesma altura antes de dar um degrau. Ancorado nas coordenadas do mundo para que as peças vizinhas concordem. `0` dá um degrau a cada bloco, como as rampas do vanilla |
| `villagePathIntersects` | lista | nenhum | Desenhos pintados nos cruzamentos, nomeados pela chave de registro a partir de `<namespace>/pathintersects/` de um pacote. Uma entrada pinta todos os cruzamentos do mesmo jeito; várias são escolhidas por cruzamento, por peso |
| `villagePathDeadEnds` | lista | nenhum | Como se fecha uma estrada sem saída, quando ela não desenvolveu um beco circular, listado abaixo. Uma entrada fecha todas as pontas sem saída do mesmo jeito; várias sorteiam uma por ponta a partir da seed do mundo. Vazio deixa as pontas sem saída abertas |
| `villagePathLampBlock` | bloco ou bloco com dados | `minecraft:oak_fence` | O bloco de que se constrói um poste de luz ao longo de uma estrada, empilhado sobre o meio-fio. Vazio não ergue postes |
| `villagePathLampHeight` | número | `3` | Quantos blocos de altura o poste tem antes da cabeça |
| `villagePathLampTopBlock` | bloco ou bloco com dados | `minecraft:wool:15` | A cabeça no topo do poste. Vazio a deixa nua |
| `villagePathLampSideBlock` | bloco ou bloco com dados | `minecraft:torch` | A luz pendurada em cada lado da cabeça, voltada para fora. Vazio não pendura nenhuma |
| `villagePathLampStructure` | texto | vazio | Um arquivo de estrutura colocado como o poste inteiro em vez de empilhar os três blocos do poste, nomeado como `mypack:street_lamp` e lido da pasta `structures` desse pacote. É centralizado no ponto do poste com sua camada mais baixa no meio-fio, e os blocos que ele assenta ficam retidos para que nada mais os sobrescreva. Vazio empilha os blocos |
| `villageWellStructure` | lista | nenhum | Arquivos de estrutura colocados como peça central da praça no lugar do poço, como entradas ponderadas `name=weight`, por exemplo `mypack:plaza_spire=3`, lidos da pasta `structures` desse pacote e sorteados uma vez por poço a partir da sua posição, de modo que o mesmo poço sempre recebe a mesma. Uma entrada `empty=weight` mantém o poço naquela fatia. A escolhida é centralizada na área de seis por seis do poço com sua camada mais baixa no piso da praça, esse piso é pavimentado sob ela, e os blocos que ela assenta ficam retidos para que o acabamento da praça os deixe em paz. Uma estrutura mais larga se estende pelo anel da praça. Sem entradas, constrói o poço |

Uma estrada é revestida de dentro para fora: linha central, depois estrada, depois linhas de borda, depois calçadas. Larguras que não cabem recuam em vez de transbordar, então um trecho estreito perde discretamente a calçada antes de perder a estrada.

`villagePathBlock` e seus irmãos vencem `villageBlocks`. Um bloco de estrada nomeado é usado como está, enquanto o mapa apenas toca no que a estrada teria escolhido por conta própria. Deixe-os vazios e o mapa decide, que é como um pacote mantém a superfície fiel ao bioma e ainda assim a recolore.

**Os blocos de poste carregam dados.** Os três blocos do poste aceitam um nome simples, um nome com metadados ou um nome com dados de block entity entre chaves, `minecraft:skull:1{SkullType:3}`. As chaves são lidas como NBT e aplicadas à block entity depois que o bloco é colocado, que é como um poste de outro mod mantém as configurações de que precisa. NBT inválido é relatado e ignorado em vez de impedir a construção do poste.

**Pontas sem saída.** Uma estrada que termina sem desenvolver um beco circular é fechada por `villagePathDeadEnds`, um estilo sorteado por ponta a partir da seed do mundo. Um estilo cujo bloco não esteja definido sai do sorteio, então `barrier` não fecha nada até que `villagePathBridgeBarrierBlock` nomeie um bloco, e a ponta de uma viela nunca recebe `sidewalk`.

| Valor | O que faz |
| --- | --- |
| `sidewalk` | Pavimenta a fileira da ponta com o bloco da calçada |
| `barrier` | Ergue o bloco de barreira ao longo da fileira da ponta, com a altura de `villagePathBridgeBarrierHeight` |

**Desenhos de cruzamento.** `villagePathIntersects` nomeia arquivos que um pacote traz, cada um um pequeno desenho do que pintar onde duas estradas se encontram, traçado como fileiras de caracteres únicos, um caractere por bloco.

`<namespace>/pathintersects/*.json`

O caminho do arquivo é a chave de registro do desenho, que `villagePathIntersects` então nomeia.

```json
{
  "name": "Crosswalk",
  "weight": 3,
  "legend": { "w": "minecraft:quartz_block", "y": "minecraft:wool@4" },
  "mouth": ["wwww", "....", "wwww"],
  "corner": ["yy.", "y..", "..."]
}
```

| Chave | Valor | Padrão | O que faz |
| --- | --- | --- | --- |
| `name` | string | o nome do arquivo | O nome usado no log |
| `weight` | inteiro, 1 ou mais | `1` | Fatia dos cruzamentos que este desenho ganha quando vários são listados |
| `legend` | objeto de um caractere para um bloco | nenhum | Os caracteres que as fileiras podem usar além dos papéis abaixo. Um caractere que já é um papel é recusado com uma linha no log |
| `mouth` | lista de strings | nenhum | Fileiras pintadas em cada acesso, fora da estrada que cruza. A primeira fileira é a mais próxima do cruzamento e as demais seguem para fora. Os caracteres correm no sentido da largura da estrada e se repetem onde uma fileira é mais curta que a largura da estrada |
| `corner` | lista de strings | nenhum | Fileiras pintadas dentro do próprio cruzamento. A primeira fileira é a mais próxima da borda da estrada que cruza e, dentro de uma fileira, o primeiro caractere é o mais próximo da borda da própria estrada, avançando para dentro. Uma célula que o desenho não alcança é deixada em paz |

Cinco caracteres são papéis e não blocos, então acompanham o que quer que a estrada já tenha como acabamento: `r` é a superfície da estrada, `l` a linha de borda, `s` a calçada, `.` deixa o bloco exatamente como estava, e `c` é reservado e pinta a superfície da estrada. Um papel cujo bloco o pacote nunca definiu recua para a superfície da estrada, e qualquer outro caractere é procurado na `legend`, recuando também para a superfície da estrada.

Qual desenho um cruzamento recebe é calculado a partir da seed do mundo e da posição do próprio cruzamento, de modo que o mesmo mundo sempre pinta os mesmos cruzamentos. Um desenho só é pintado onde três ou mais ruas se encontram, em um cruzamento ou na praça de um poço; duas ruas que se encontram resultam em um cotovelo simples.

#### Pontes e cais de vilas

*vilas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villagePathSupportBlock` | bloco | vazio | A própria superfície onde o terreno é rocha nua, e os pilares e pernas sob uma estrada sobre a água. Vazio mantém o cascalho do vanilla, arenito em vilas de deserto |
| `villagePathBridgeBlock` | bloco | vazio | Com o que uma estrada atravessa a água. Vazio mantém as tábuas do vanilla |
| `villagePathBridgeBarrierBlock` | bloco | vazio | Barreiras empilhadas ao longo das duas bordas do tabuleiro de uma ponte. Vazio não constrói nenhuma |
| `villagePathBridgeBarrierHeight` | número | `1` | Quantos blocos de altura essas barreiras têm |
| `villagePathBridgeSidewalkBlock` | bloco | vazio | Reveste a calçada onde uma estrada cruza a água. Vazio leva o bloco normal da calçada para o outro lado |
| `villagePathBridgeDrop` | número | `0` | A que distância do chão o nível de uma estrada precisa ficar para que o vão sob ela seja coberto por ponte em vez de aterrado. `0` mantém as estradas no chão: elas só fazem ponte sobre a água e nada mais. `3` é a regra que um viaduto ferroviário segue. Isso muda o nível, não apenas o acabamento |
| `villagePathBridgeFrameBlock` | bloco | vazio | Uma estrutura aérea sobre uma ponte longa: um poste de cada lado do tabuleiro e uma viga por cima. Cada estrutura leva um pilar até o chão sob o tabuleiro, e nenhum poste de luz é erguido na fileira em que ela está. Vazio não constrói nenhuma |
| `villagePathBridgeFrameTopBlock` | bloco | vazio | A viga no topo dessa estrutura. Vazio usa `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight` | número | `4` | Quantos blocos de espaço livre a estrutura deixa sobre o tabuleiro, ficando a viga um bloco acima disso |
| `villagePathBridgeFrameRun` | número | `24` | A quantas fileiras de distância ficam as estruturas quando uma ponte é longa o bastante para várias |
| `villagePathBridgeFrameLeast` | número | `24` | O menor trecho em ponte que recebe uma estrutura. Uma ponte mais curta fica lisa |
| `villagePathPiers` | lista | nenhum | Estilos de cais para uma estrada que termina sem saída sobre a água, listados abaixo. O trecho final em ponte vira um cais; várias entradas sorteiam um estilo por cais. Vazio deixa essa ponte como uma ponte comum |
| `villagePathPierCargo` | lista | nenhum | Carga posta ao longo do lado interno das grades de um cais, como entradas ponderadas listadas abaixo. Toda segunda fileira de todo cais sorteia a lista em cada lado, então os pesos decidem o quanto um cais parece cheio. Vazio deixa os cais vazios |
| `villagePathPierLoot` | texto | `resourcedatapackloader:chests/pier_cargo` | A loot table com que a carga que tem inventário é preenchida, sorteada na primeira vez em que é aberta. Vazio deixa essa carga vazia |

**Um tabuleiro nivelado.** Toda ponte fica em uma única altura de ponta a ponta, qualquer que seja a altura das duas margens; a estrada de cada lado sobe em rampa para encontrá-la.

**Uma queda seca.** Uma estrada aterra uma depressão e faz ponte somente sobre a água, a menos que `villagePathBridgeDrop` indique uma altura: uma fileira cujo nível fica a mais desse número de blocos acima do chão é então coberta por um tabuleiro sobre pernas, à maneira de um viaduto ferroviário que cruza uma ravina. Isso muda o nível em vez do acabamento, de modo que uma vila gerada com ele não é igual a uma gerada sem.

**Estruturas aéreas.** Um trecho em ponte de `villagePathBridgeFrameLeast` fileiras ou mais leva estruturas sobre o tabuleiro assim que `villagePathBridgeFrameBlock` nomeia um bloco: um poste de cada lado e uma viga por cima, com `villagePathBridgeFrameHeight` blocos de espaço livre sob ela. Várias ficam em uma ponte longa, a `villagePathBridgeFrameRun` fileiras de distância e distribuídas simetricamente em torno do meio do trecho, de modo que a mesma ponte sempre leva as mesmas estruturas. Uma fileira em que outra estrada cruza a ponte fica aberta, e um cais não leva estrutura alguma — um píer não é uma ponte.

**Cais.** Uma estrada que avança sobre a água e termina no nada vira um cais em vez de uma ponte para lugar nenhum, assim que `villagePathPiers` nomeia pelo menos um estilo. Várias entradas sorteiam um estilo por cais, a partir da seed do mundo e da ponta do cais, de modo que o mesmo mundo sempre constrói o mesmo cais. Todo cais fica sobre estacas do bloco de suporte, cravadas até o leito abaixo nas duas bordas do tabuleiro a cada quarta fileira, qualquer que seja o estilo. O tabuleiro é o bloco de ponte, as grades e postes são o bloco de barreira, e as estacas são o bloco de suporte.

| Valor | O que faz |
| --- | --- |
| `railed` | Mantém o tabuleiro inteiro, liso, sem linhas nem faixa de calçada, e fecha a ponta distante com o bloco de barreira |
| `pilings` | Abre as barreiras laterais em postes a cada quarta fileira, sobre esses mesmos suportes |
| `boardwalk` | Estreita o tabuleiro até a largura do núcleo da estrada |

**Carga do cais.** `villagePathPierCargo` põe carga em um cais. Toda segunda fileira sorteia a lista uma vez em cada lado, uma coluna para dentro das grades, o que deixa o meio do tabuleiro livre para andar, nunca lota a fileira da ponta com grade, e impede que duas cargas fiquem lado a lado, já que dois baús encostados formariam um único baú duplo. Uma pilha só é assentada onde todos os blocos dela cabem, e nomear o mesmo bloco duas vezes em alturas diferentes é como um cais ganha pilhas de tamanhos variados.

| Valor | O que faz |
| --- | --- |
| `<block>=<weight>` | Um bloco e sua fatia dos pontos, posto com um de altura |
| `<block>=<weight>,<height>` | O mesmo, empilhado com essa quantidade de blocos de altura, de 1 a 8 |
| `empty=<weight>` | A fatia do tabuleiro deixada livre |

Um bloco que leva um inventário de saque, um baú entre eles, é preenchido a partir de `villagePathPierLoot`, sorteado na primeira vez em que um jogador o abre, como um baú do vanilla. A tabela integrada é sucata marinha fácil de reunir. Um pacote a substitui trazendo seu próprio `loot_tables/chests/pier_cargo.json` sob o namespace `resourcedatapackloader`, ou nomeando uma tabela própria.

#### Túneis de vilas

*vilas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villagePathTunnelBlock` | bloco | vazio | Reveste uma estrada onde ela atravessa uma colina em vez de abri-la a céu aberto: as paredes de cada lado da perfuração e o teto sobre ela. Vazio não perfura túneis, e a estrada corta a colina como antes |
| `villagePathTunnelDepth` | número | `10` | Quanto terreno precisa ficar sobre a superfície da estrada para que um trecho seja perfurado em vez de cortado. Uma elevação enterrada a essa profundidade por doze fileiras ou mais é mantida nivelada e perfurada, com suas aproximações mais rasas cortadas a céu aberto; uma lombada mais curta é cortada como antes. Só conta quando `villagePathTunnelBlock` nomeia um bloco |
| `villagePathTunnelLightBlock` | bloco | vazio | Uma luz embutida no teto do túnel ao longo da sua linha central. Vazio não ilumina |
| `villagePathTunnelLightRun` | número | `8` | A quantos blocos de distância ficam essas luzes. Ancorado nas coordenadas do mundo, de modo que as luzes de uma peça de estrada continuam na seguinte; um túnel curto demais para alcançar um desses pontos é iluminado uma vez, no meio |

**Túneis.** Sem um bloco de túnel, uma estrada que encontra uma colina sobe por ela, no máximo um bloco por fileira, e não corta mais fundo que dois blocos em uma elevação curta. Assim que `villagePathTunnelBlock` nomeia um bloco, uma elevação que fica `villagePathTunnelDepth` ou mais sobre a estrada por pelo menos doze fileiras é perfurada: a estrada mantém o nível do lado mais alto durante toda a elevação, toda fileira com tanto terreno por cima ganha uma perfuração de quatro blocos de altura com o bloco de revestimento nas paredes e no teto, e as fileiras mais rasas antes dos portais são cortadas a céu aberto como aproximação. Uma estrada que encontra a face de uma montanha, e não uma colina que dá para ver além, também não é escalada: ela mantém o nível em que chega e procura o outro lado, até 98 fileiras além de onde a peça teria terminado. Se o encontra nesse alcance, com o terreno entre os dois livre de outras peças, a peça é alongada para sair no portal oposto, de modo que um túnel sempre atravessa. Se não o encontra, a estrada para ao pé da montanha e nunca entra nela. A rua inteira atravessa, faixas, linhas e calçadas, iluminada pelo teto com `villagePathTunnelLightBlock` a cada `villagePathTunnelLightRun` blocos, enquanto postes de luz e a decoração do acostamento terminam nos portais. Um cruzamento nunca é perfurado, então uma rua transversal sempre encontra a estrada a céu aberto. Nenhum terreno é assentado ao longo de um trecho que a estrada vai perfurar e nenhuma rua se ramifica dele, então uma casa nunca fica de frente para um túnel e nenhum cruzamento é aberto em um; um distrito que não encontra espaço para seus terrenos em outro lugar assenta menos ruas ali.

#### Esgotos de vilas

*vilas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageSewerBlock` | nome de bloco | nenhum | O bloco com que um esgoto é revestido sob as ruas e vielas de uma vila: seu piso, suas duas paredes e seu teto. Vazio não cava esgotos |
| `villageSewerDepth` | número | `8` | A que profundidade sob a superfície da própria rua fica o piso do esgoto. O esgoto acompanha a rua sob a qual corre, então uma rua que sobe leva um esgoto que sobe |
| `villageSewerHeight` | número | `3` | Quantos blocos de espaço livre ficam sobre a passarela |
| `villageSewerWidth` | número | `5` | Qual a largura do esgoto, contada no sentido transversal incluindo suas duas paredes. Um número par é arredondado para cima para que o canal mantenha o meio |
| `villageSewerWaterBlock` | nome de bloco | `minecraft:water` | O que preenche o canal no meio. Vazio deixa o canal seco |
| `villageSewerWalkBlock` | nome de bloco | nenhum | Com que se reveste a superfície das passarelas de cada lado do canal. Vazio anda sobre o bloco de revestimento |
| `villageSewerLightBlock` | nome de bloco | nenhum | O bloco embutido no teto sobre o canal como luz. Vazio não ilumina |
| `villageSewerLightRun` | número | `8` | A quantos blocos de distância ficam essas luzes. Ancorado nas coordenadas do mundo, de modo que as luzes de uma peça de estrada continuam na seguinte |
| `villageSewerLadderBlock` | nome de bloco | nenhum | O bloco pelo qual se sobe um poço de acesso, posto ao longo do poço da rua até a passarela do esgoto. Vazio deixa o poço aberto |
| `villageSewerCoverBlock` | nome de bloco | nenhum | O bloco que cobre um bueiro, posto rente ao chão em uma rua leste-oeste onde quer que uma rua ou viela a encontre, e na praça onde essa rua cruza o circuito do esgoto. Um alçapão de madeira é a escolha usual: um de ferro recebe sinal de redstone e nenhum jogador consegue abri-lo à mão, o que fecha o esgoto para eles. Vazio deixa a boca do poço aberta |
| `villageSewerMossBlock` | nome de bloco | nenhum | Um segundo bloco misturado ao revestimento aqui e ali, por exemplo pedra musgosa entre pedra lisa. Vazio reveste o esgoto com um único bloco do início ao fim |
| `villageSewerMossChance` | 0 a 100 | `25` | Qual porcentagem dos blocos de revestimento sai como esse segundo bloco. Sorteado por posição de bloco a partir da seed do mundo, de modo que o mesmo esgoto sempre sai igual |
| `villageSewerVineBlock` | nome de bloco | nenhum | Um bloco pendurado no lado de dentro das paredes do esgoto aqui e ali, por exemplo trepadeiras. Ele se agarra à parede em que estiver encostado. Vazio não pendura nada |
| `villageSewerVineChance` | 0 a 100 | `20` | Qual porcentagem das células ao lado de uma parede o leva. Sorteado por posição de bloco a partir da seed do mundo, de modo que o mesmo esgoto sempre pendura igual |
| `villageSewerWellEntrance` | booleano | `true` | Um circuito de esgoto sob o anel da praça ao redor do poço, com o esgoto de cada rua passando por ele, e um bueiro na praça descendo até o circuito em cada lado onde uma rua leste-oeste o cruza, de modo que os esgotos formam um sistema conectado com uma entrada no centro da cidade. Desligado, o esgoto de cada rua termina no poço e a praça não tem como descer |

**Esgotos.** Nomear `villageSewerBlock` cava um esgoto sob toda rua e viela, `villageSewerDepth` blocos abaixo da superfície da própria rua. Ele não é uma rede à parte: acompanha as estradas, então onde as ruas vão o esgoto vai, vira onde elas viram, sobe onde elas sobem, e dois esgotos se encontram sob um cruzamento porque as ruas acima deles se encontram; onde uma rua ou viela termina contra outra estrada, o esgoto dela segue por baixo dessa estrada para se juntar ao da outra. Um bulbo de beco circular e uma fileira levada sobre uma ponte não levam esgoto. A seção é um piso revestido, um canal no meio preenchido com `villageSewerWaterBlock`, uma passarela de cada lado revestida com `villageSewerWalkBlock`, `villageSewerHeight` blocos de espaço livre e um teto revestido, com `villageSewerWidth` de largura no sentido transversal incluindo suas duas paredes. `villageSewerLightBlock` embute uma luz no teto sobre o canal a cada `villageSewerLightRun` blocos. Um esgoto nunca sobe o bastante para perturbar a rua sobre ele, e um trecho sem espaço entre a estrada e o piso do mundo é pulado em vez de espremido.

#### Ferrovias de vilas

*vilas*

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

Uma linha ferroviária é um trecho reto de trilhos que cruza a vila inteira em um eixo e segue além da última peça em cada ponta. Ela é assentada antes da primeira rua, então a cidade cresce ao redor dela: nenhuma casa fica sobre a linha, uma rua só pode cruzá-la em linha reta, e nada se ramifica dela. Como as estradas, precisa de `terrainAdaptation`. `villageRailLines` é `0` por padrão, o que não assenta nenhuma e deixa uma vila exatamente como era.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageRailLines` | número | `0` | Quantas linhas passam por cada vila. `0` não assenta nenhuma |
| `villageRailSpacing` | número | `48` | O mínimo de blocos de terreno livre entre o leito de uma linha e o da seguinte da mesma vila. `1` as assenta a um bloco de distância, que é como um pacote constrói um pátio de linhas paralelas |
| `villageRailDirection` | texto | `any` | Em que sentido as linhas correm: `ew` de leste a oeste, `ns` de norte a sul, `any` sorteia por vila. `e`, `w`, `n` e `s` são lidos do mesmo modo |
| `villageRailWidth` | número | `3` | O mínimo que o leito ferroviário tem. `3` leva um trilho pelo meio e `5` leva dois; um leito para o qual se pedem mais trilhos do que isso comporta se alarga para abrigá-los |
| `villageRailTracks` | número | `0` | Quantos trilhos um mesmo leito leva, lado a lado e a `villageRailTrackGap` de distância. **O leito se alarga para abrigar todos**, então três trilhos dividem um leito em vez de virar três linhas. `0` assenta um trilho em um leito com menos de cinco de largura e dois em um mais largo |
| `villageRailTrackGap` | número | `2` | A quantos blocos de distância ficam os trilhos de um leito, de centro a centro. `2`, o mínimo permitido, deixa um bloco de leito entre eles, o que impede que se curvem um para o outro como fazem trilhos encostados |
| `villageRailBlock` | bloco | vazio | O trilho. Vazio assenta trilhos do vanilla, sobre os quais os carrinhos de minas andam; qualquer outro bloco é assentado como está |
| `villageRailTrackSeat` | `auto`, `on` ou `in` | `auto` | Onde o trilho se assenta. `auto` assenta um bloco de trilho sobre o leito e embute qualquer outro bloco rente à superfície do leito; `on` sempre o assenta sobre o leito; `in` sempre o embute no leito. Um trilho embutido no leito é como um pacote dá a um trilho a aparência de blocos de ferro ou lajes em vez de trilhos de carrinho, e uma passagem de nível então corre rente ao pavimento |
| `villageRailBedBlock` | bloco | vazio | O leito sob o trilho. Vazio assenta cascalho |
| `villageRailTieBlock` | bloco | vazio | O dormente assentado através do leito a cada `villageRailTieRun` fileiras. Vazio assenta tábuas |
| `villageRailTieRun` | número | `2` | A quantas fileiras de distância ficam os dormentes |
| `villageRailShoulderBlock` | bloco | vazio | Reveste as colunas mais externas do leito, um caminho de manutenção ao lado do trilho e a resposta da ferrovia à calçada de uma estrada. Vazio não assenta nenhum |
| `villageRailShoulderWidth` | número | `1` | Quantas colunas de largura tem esse acostamento em cada lado, acrescentadas por fora de `villageRailWidth` |
| `villageRailPowerBlock` | bloco | vazio | O trilho energizado embutido na linha a cada `villageRailPowerRun` fileiras. Vazio usa um trilho energizado do vanilla; um bloco que não seja trilho é simplesmente assentado ali |
| `villageRailPowerBase` | bloco | vazio | O que fica sob um trilho energizado para alimentá-lo. Vazio usa um bloco de redstone |
| `villageRailPowerRun` | número | `0` | A cada tantas fileiras, um trilho energizado sobre um bloco de redstone é embutido em uma linha de trilhos do vanilla, para que o carrinho continue rolando. `0` não energiza nenhum, e qualquer trilho que não seja do vanilla o ignora |
| `villageRailSupportBlock` | bloco | vazio | Os postes sob um cavalete. Vazio usa troncos |
| `villageRailDeckBlock` | bloco | vazio | O tabuleiro sobre o qual um cavalete leva o leito. Vazio usa tábuas |
| `villageRailBarrierBlock` | bloco | vazio | Barreiras ao longo das duas bordas do tabuleiro de um cavalete. Vazio não ergue nenhuma |
| `villageRailBridgeFrameBlock` | bloco | vazio | Uma estrutura aérea sobre um cavalete longo: um poste de cada lado do tabuleiro e uma viga por cima. Toda fileira que leva uma também leva seus postes de apoio até o leito. Vazio não constrói nenhuma |
| `villageRailBridgeFrameTopBlock` | bloco | vazio | A viga no topo dessa estrutura. Vazio usa `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight` | número | `4` | Quantos blocos de espaço livre a estrutura deixa sobre o tabuleiro, ficando a viga um bloco acima disso |
| `villageRailBridgeFrameRun` | número | `24` | A quantas fileiras de distância ficam as estruturas quando um cavalete é longo o bastante para várias |
| `villageRailBridgeFrameLeast` | número | `24` | O menor cavalete que recebe uma estrutura. Um cavalete mais curto fica liso |
| `villageRailTunnelBlock` | bloco | vazio | Reveste as paredes e o teto onde a linha atravessa uma colina. Vazio não perfura túneis e corta toda colina a céu aberto |
| `villageRailTunnelDepth` | número | `6` | Quanto terreno precisa ficar sobre o leito para que um trecho seja perfurado em vez de cortado. Precisa de `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock` | bloco | vazio | Uma luz embutida no teto de um túnel ferroviário ao longo da sua linha central. Vazio não ilumina |
| `villageRailTunnelLightRun` | número | `8` | A quantos blocos de distância ficam essas luzes do túnel, ancoradas nas coordenadas do mundo para que as peças concordem |
| `villageRailClimb` | número | `8` | Quantas fileiras a linha corre nivelada para cada bloco que sobe ou desce. `1` a inclina tão íngreme quanto uma estrada |
| `villageRailTail` | número | `48` | Até onde a linha segue além da última peça da vila em cada ponta |

**Por onde passa uma linha.** As linhas correm em paralelo, no eixo que `villageRailDirection` nomeia, e são distribuídas a partir da praça do poço, uma por vez, primeiro de um lado e depois do outro, cada uma mantendo pelo menos `villageRailSpacing` blocos de terreno entre o seu leito e o da linha seguinte. Uma linha nunca passa pela praça nem por um terreno: ela é assentada antes da primeira rua, então toda rua e casa da vila são colocadas ao redor dela, e é aparada até a vila crescida mais `villageRailTail` em cada ponta depois que a vila está organizada.

**Declive.** Uma ferrovia não sobe como uma estrada. Seu leito acompanha o terreno suavizado ao longo de um trecho longo e muda de nível em um bloco no máximo a cada `villageRailClimb` fileiras. Onde o terreno cai mais de três blocos, a linha corre sobre um cavalete, `villageRailDeckBlock` sobre postes de `villageRailSupportBlock` a cada quatro fileiras, tanto sobre a água quanto sobre uma ravina. Onde o terreno sobe, a linha é cortada a céu aberto, ou perfurada com `villageRailTunnelBlock` assim que o terreno sobre o leito atinge `villageRailTunnelDepth` de profundidade por doze fileiras ou mais. Quatro blocos são mantidos livres sobre o leito ao longo de toda a linha. Um cavalete fica em uma única altura de ponta a ponta, e o leito de cada lado sobe em rampa para encontrar essa altura; onde manter um cavalete nivelado e a taxa de subida discordam, o nível vence e a rampa ao lado pode dar degraus antes do que `villageRailClimb` indica. Um cavalete de `villageRailBridgeFrameLeast` fileiras ou mais leva estruturas aéreas assim que `villageRailBridgeFrameBlock` nomeia um bloco, a `villageRailBridgeFrameRun` fileiras de distância e distribuídas simetricamente em torno do meio do cavalete, e toda fileira que leva uma leva junto os seus postes de apoio até o leito. Uma fileira em que uma estrada cruza a linha fica aberta.

**Cruzamentos.** Uma rua cruza uma linha em linha reta e segue livre além das duas bordas do leito por sete blocos ou mais. Uma rua que terminaria na linha ou dentro desses sete blocos é levada adiante através dela quando seu declive permite, e do contrário é interrompida sete blocos antes do leito; uma rua que começaria na linha ou correria ao longo dela é recusada. Em um cruzamento, a rua é nivelada pela linha, nunca o contrário, e sobe em rampa até esse nível com sua própria inclinação transitável de cada lado. O pavimento mantém a superfície e o trilho corre sobre ela um bloco acima, de modo que um carrinho cruza a estrada e um aldeão cruza o trilho. Uma linha perfurada através de uma colina não é cruzada de forma alguma: a rua passa por cima do túnel.

**Degraus de porta.** Com `terrainAdaptation` ligado, nenhuma construção da vila assenta um bloco de escada fora da sua própria caixa: as escadas de degrau que o vanilla coloca diante de uma porta são omitidas, já que a frente da estrada e o avental do terreno levam o chão até a porta por conta própria.

**Trilho.** Com `villageRailBlock` vazio, o trilho é o trilho do vanilla voltado ao longo da linha, e `villageRailPowerRun` coloca um trilho energizado sobre um bloco de redstone a cada tantas fileiras para que um carrinho percorra a linha inteira. Um pacote que quer blocos de ferro, barras ou qualquer outra coisa os nomeia em vez disso, e a linha é revestida com esse bloco como está.

#### Metrôs de vilas

*vilas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageSubwayLines` | número | `0` | Quantas linhas ferroviárias subterrâneas uma vila cava. 0 não cava nenhuma e não sorteia nada, de modo que a vila é organizada exatamente como seria sem elas |
| `villageSubwayDepth` | número | `24` | A que profundidade sob a superfície fica o leito. A linha é nivelada a partir do terreno acima dela, então acompanha o relevo àquela profundidade em vez de correr plana |
| `villageSubwaySpacing` | número | `64` | A que distância umas das outras ficam as linhas de metrô de uma vila |
| `villageSubwayDirection` | string | `any` | Em que sentido as linhas de metrô correm: `ew` de leste a oeste, `ns` de norte a sul, ou `any` para sortear por vila |
| `villageSubwayWidth` | número | `3` | Qual a largura do leito, antes dos acostamentos |
| `villageSubwayTracks` | número | `0` | Quantos trilhos paralelos o leito leva. 0 toma quantos a largura permitir |
| `villageSubwayTrackGap` | número | `2` | A que distância ficam os trilhos paralelos |
| `villageSubwayBlock` | bloco | vazio | O bloco do trilho. Vazio assenta trilhos do vanilla |
| `villageSubwayTrackSeat` | string | `auto` | Se o trilho fica sobre o leito, embutido nele, ou `auto` para deixar o bloco decidir |
| `villageSubwayBedBlock` | bloco | vazio | O bloco de que o leito é feito. Vazio usa cascalho |
| `villageSubwayTieBlock` | bloco | vazio | O bloco assentado através do leito como dormentes. Vazio usa tábuas |
| `villageSubwayTieRun` | número | `2` | A quantos blocos de distância ficam os dormentes |
| `villageSubwayShoulderBlock` | bloco | vazio | O bloco de cada lado do leito. Vazio não deixa acostamento |
| `villageSubwayShoulderWidth` | número | `1` | Qual a largura desse acostamento |
| `villageSubwayPowerBlock` | bloco | vazio | O bloco do trilho energizado. Vazio usa o trilho energizado do vanilla |
| `villageSubwayPowerBase` | bloco | vazio | O bloco posto sob um trilho energizado para acioná-lo. Vazio usa um bloco de redstone |
| `villageSubwayPowerRun` | número | `0` | A quantos blocos de distância ficam os trilhos energizados. 0 não assenta nenhum |
| `villageSubwayTunnelBlock` | bloco | vazio | O bloco com que a perfuração é revestida: as paredes de cada lado e o teto sobre ela. Vazio cava a perfuração e suas estações sem revestimento |
| `villageSubwayTunnelLightBlock` | bloco | vazio | O bloco embutido no teto do túnel como luz. Vazio não ilumina |
| `villageSubwayTunnelLightRun` | número | `8` | A quantos blocos de distância ficam essas luzes, ancoradas nas coordenadas do mundo para que as peças concordem |
| `villageSubwayClimb` | número | `8` | Quantos blocos uma linha corre antes de poder subir ou descer um bloco |
| `villageSubwayTail` | número | `48` | Até onde, além das peças da própria vila, uma linha corre antes de parar |
| `villageSubwaySurfaces` | número | `25` | A chance em cem de uma linha de metrô subir à superfície em uma ponta e seguir dali como uma ferrovia comum, túnel atrás e trilho aberto adiante. `0` mantém todo metrô enterrado em todo o seu comprimento |

**Saindo à superfície.** `villageSubwaySurfaces` é a chance em cem de uma linha, em vez de ficar enterrada de ponta a ponta, subir à superfície em uma ponta e seguir dali como uma ferrovia comum — túnel atrás, trilho aberto adiante. A subida obedece a `villageSubwayClimb`, um bloco por essa quantidade de fileiras, então uma linha de `villageSubwayDepth` de profundidade gasta profundidade vezes subida em fileiras só na rampa e precisa de um bom trecho além dela para valer o nome; uma linha sem espaço para os dois simplesmente fica no subsolo. Em uma linha curta que leva uma estação, aumentar `villageSubwayClimb` é o que abre espaço para os dois.

#### Estações de metrô

*vilas*

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | número | `0` | Quantos blocos de comprimento tem a câmara de uma estação, centralizada na fileira em que a linha passa mais perto do poço. 0 não constrói estação alguma |
| `villageSubwayStationRun` | número | `0` | A quantos blocos de distância ficam as demais estações ao longo da linha, além da que fica no poço. Cada uma desliza um pouco ao longo da linha para achar um terreno que a comporte e é omitida onde nenhum comporta. 0 constrói somente aquela |
| `villageSubwayStationRepeat` | número | `12` | Quantas camadas de uma construção de estação se repetem, de modo que uma construção sirva a qualquer profundidade: o poço cresce por cópias inteiras dessa faixa e o corredor absorve o que sobra. Precisa ser uma volta inteira da escada ou os lances não se encontram. `0` nunca faz a construção crescer |
| `villageSubwayStationFoot` | número | `4` | Quantas camadas na base de uma construção de estação são assentadas uma única vez, antes da parte que se repete. O piso e a porta para a plataforma ficam aqui |
| `villageSubwayPlatformWidth` | número | `3` | O quanto a câmara se abre de cada lado do leito para formar uma plataforma |
| `villageSubwayPlatformBlock` | bloco | vazio | O bloco com que a plataforma é revestida no piso. Vazio reveste o piso com o revestimento do túnel |
| `villageSubwayStation` | texto | vazio | O arquivo de estrutura de que toda estação é construída, nomeado como `mypack:subway_station` e lido da pasta `structures` desse pacote. Seus blocos são assentados como foram construídos, com esponja representando o revestimento do túnel, e suas células de ar são escavadas, de modo que o que existe no subsolo é a construção e não uma descrição dela. Uma linha só recebe estações quando isto nomeia uma construção que carrega: vazio, ou um nome que não pode ser carregado, não constrói estação alguma |
| `villageSubwayRailingBlock` | bloco | `minecraft:iron_bars` | O bloco posto como grade ao redor da cabeceira da escada de uma estação onde ela se abre para a rua, para que ninguém caia no poço. Vazio deixa a cabeceira sem grade |
| `villageSubwayBenchBlock` | bloco | `minecraft:oak_stairs` | O assento dos bancos postos na plataforma de uma estação e ao lado da cabeceira da escada. Um bloco de escada é virado de costas para a linha e se lê como um banco; qualquer bloco serve. Vazio omite os bancos |
| `villageSubwayBenchEndBlock` | bloco | `minecraft:log` | Os braços em cada ponta de um banco de estação. Vazio deixa o assento nu nas duas pontas |
| `villageSubwayBenchLength` | número | `5` | Qual o comprimento de um banco de estação, braços incluídos. `0` omite os bancos |

**Estações.** Uma linha de metrô recebe uma estação onde passa mais perto do poço, assim que `villageSubwayStationLength` é definido e `villageSubwayStation` nomeia uma construção que carrega, e outras a cada `villageSubwayStationRun` blocos ao longo dela. Cada uma desliza alguns blocos para um lado ou outro para achar um ponto que o terreno comporte, mantém distância das estações já reservadas e é simplesmente omitida onde nada viável está por perto, de modo que uma linha nunca leva uma câmara sem acesso. A câmara é o leito aberto `villageSubwayPlatformWidth` de cada lado, com o piso de `villageSubwayPlatformBlock`, paredes e teto no revestimento do túnel, e iluminada pelos próprios `villageSubwayTunnelLightBlock` e `villageSubwayTunnelLightRun` do túnel. Da plataforma, um corredor leva a um poço de escada que sobe até a rua ao lado da estrada, nunca por baixo dela, e nunca pela praça do poço nem por uma casa; onde a subida é longa demais para ir em linha reta, o corredor primeiro volta ao longo da câmara. Uma grade de `villageSubwayRailingBlock` cerca a cabeceira da escada ao nível da rua, com a ponta mais próxima deixada aberta como entrada, e um banco de `villageSubwayBenchBlock` com braços de `villageSubwayBenchEndBlock`, de comprimento `villageSubwayBenchLength`, fica na plataforma e de novo ao lado da cabeceira da escada.

**Construindo a estação à mão.** `villageSubwayStation` nomeia o arquivo de estrutura de que toda estação é construída, e sem ele nenhuma estação é construída. É como um pacote traz uma forma que alguém construiu em vez de uma descrita em configurações. Construa-a em um mundo, marque a estrutura em qualquer bloco, exporte-a e coloque-a no pacote: seus blocos são assentados como foram construídos, com esponja representando o revestimento do túnel, e suas células de ar são escavadas. Uma construção serve a qualquer profundidade porque o meio dela se repete — `villageSubwayStationFoot` camadas são assentadas uma vez na base, levando o piso e a porta para a plataforma, depois cópias inteiras das próximas `villageSubwayStationRepeat` camadas se empilham até a construção chegar à rua. Essa faixa precisa ser uma volta inteira da escada ou os lances não se encontram onde duas cópias se juntam. A construção traz a sua própria abertura para a rua.

#### Ligações ferroviárias

*vilas*

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

As ligações ferroviárias unem vilas vizinhas em uma única rede. As vilas são fundadas uma por célula da grade de vilas (`structureSpacing`), e uma ligação corre ao longo da junção entre duas células: a primeira linha de cada vila segue além da sua cauda como um ramal, reto até a junção, e encontra um tronco assentado ao longo da junção em ângulo reto. O tronco corre de um ramal ao outro e nunca além de nenhum dos dois. Precisa de `villageRailLines`, ou de `villageSubwayLines` em um pacote sem linhas de superfície, e vem desligada por padrão.

| Configuração | Tipo | Padrão | O que faz |
| --- | --- | --- | --- |
| `villageRailLinks` | true/false | `false` | Liga vilas vizinhas cujas primeiras linhas ficam de frente uma para a outra através de uma junção |
| `villageRailLinkLeast` | número | `128` | A menor ligação assentada, ramal mais tronco mais ramal, em blocos |
| `villageRailLinkMost` | número | `1024` | A maior ligação assentada, ramal mais tronco mais ramal, em blocos |
| `villageRailLinkBridgeMost` | número | `96` | A maior ponte de que uma ligação pode precisar. Uma ligação sobre água mais larga ou uma queda mais funda não é assentada |
| `villageRailLinkTunnelMost` | número | `192` | O maior túnel de que uma ligação pode precisar onde `villageRailTunnelBlock` perfura túneis. Uma ligação que perfuraria mais longe não é assentada |
| `villageRailLinkStation` | texto | `both` | A estação em cada ramal logo antes do tronco: `both` assenta uma plataforma de cada lado da linha, `one` uma única plataforma à esquerda de um trem que chega ao tronco, `none` não constrói nenhuma |
| `villageRailLinkStationLength` | número | `16` | Quantas fileiras de comprimento têm as plataformas da estação. `0` não constrói estações |
| `villageRailLinkPlatformWidth` | número | `3` | Quantos blocos de largura tem cada plataforma |
| `villageRailLinkPlatformBlock` | bloco | vazio | O bloco de que as plataformas são construídas. Vazio usa tijolos de pedra |

**Quais vilas se ligam.** Duas vilas só se ligam quando estão em células vizinhas, suas primeiras linhas correm no eixo que cruza a junção entre elas, e a ligação inteira, medida de poço a poço ao longo do trilho, fica entre `villageRailLinkLeast` e `villageRailLinkMost` blocos. Cada parte da decisão é calculada a partir da seed e dos dois locais de vila, de modo que o resultado é o mesmo qualquer que seja a vila ou o chunk gerado primeiro. Uma ligação que não pode ser construída inteira não é assentada de forma alguma, nunca pela metade: uma que precisaria de uma ponte ou túnel mais longo do que as configurações permitem, passaria da borda do mundo, esbarraria em uma mansão da floresta, deixaria duas vilas mais próximas do que `structureSeparation` permite, ou levaria uma junção perto demais de um canto das células. Um tronco só é assentado em direção a uma vila que foi de fato fundada: quando um limite como `structureMost` barra a vizinha, ou ela cresce pequena demais para ser mantida, nem a metade do tronco nem o ramal além da cauda da própria vila são construídos. Vilas fixadas se ligam do mesmo modo, uma por célula; uma célula com duas fixadas não liga nenhuma das duas. As outras vilas mantêm distância do ramal e do tronco de uma ligação à medida que crescem, como mantêm distância umas das outras.

**Declive.** Ramais e troncos são linhas ferroviárias e são nivelados, atravessados por pontes, perfurados por túneis e cruzados exatamente como uma linha de vila, com `villageRailClimb` e as configurações de cavalete e túnel acima. Onde um ramal encontra o tronco, os dois ficam nivelados, e a estação ao lado também.

**O entroncamento.** Um ramal se junta apenas ao trilho próximo do tronco. Esse trilho é interrompido onde o meio do ramal o encontra, o trilho esquerdo do ramal se curva para a esquerda para dentro dele e o direito se curva para a direita, e o trilho distante segue reto. Com dois trilhos, o tronco no alto e o ramal vindo de baixo:

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` é leito ferroviário e `o` é trilho. Onde os dois ramais chegariam a poucos blocos um do outro, a primeira linha da segunda vila se desloca para se alinhar com a primeira, e os dois se encontram em um cruzamento em vez disso: cada ramal se funde apenas ao seu próprio trilho próximo exatamente como acima, os dois trilhos do tronco são interrompidos no centro do ramal, e nenhum trilho cruza outro:

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

Um tronco de trilho único não tem um segundo trilho para dar ao outro ramal, então uma ligação cujos ramais se encontrariam de frente sobre um trilho único não é assentada. Com um trilho único, o trilho do ramal se curva para dentro do trilho do tronco em direção à esquerda, e o trilho do tronco além dessa curva termina contra ela. As curvas são definidas com suas formas fixas, de modo que o trilho do vanilla faz a curva onde o entroncamento é desenhado e em nenhum outro lugar.

**Estações.** As últimas fileiras de um ramal antes do entroncamento são uma estação: plataformas de `villageRailLinkPlatformBlock` niveladas com o trilho, com grade ao longo da borda externa usando `villageSubwayRailingBlock`, e um banco de `villageSubwayBenchBlock` na metade de cada plataforma.

**Metrôs.** Em um pacote apenas com linhas de metrô, a ligação leva a primeira linha de metrô de uma vila. A linha sai do chão em direção ao tronco, com a rampa de `villageSubwayDepth` vezes `villageSubwayClimb` fileiras de comprimento, e chega à estação e ao entroncamento na superfície; uma vila desse tipo se liga por um lado apenas, o da ligação mais curta, e o tronco é uma ferrovia de superfície construída com as configurações `villageRail`. Onde um ramal não tem espaço para essa rampa e sua estação, o tronco desce até o metrô: a ligação inteira, ramais e tronco, fica no subsolo em `villageSubwayDepth`, é construída com as configurações `villageSubway` e se encontra no mesmo entroncamento, sem estação.

#### Decoração de vilas

*vilas*

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
| `villageDecor` | lista de `name=weight` | nenhum | Espalha o worldgen próprio deste pacote ao longo dos acostamentos das estradas da vila. O nome é uma chave de registro de worldgen, o peso é a fatia dos pontos dessa entrada, e `empty=weight` é a fatia dos pontos deixada vazia |

`villageDecor` espalha o worldgen próprio de um pacote ao longo dos acostamentos das estradas da vila, o que impede que uma vila se pareça com casas em meio a grama nua. Cada entrada é `name=weight`: o nome é uma chave de registro de worldgen, `mypack:street_flowers`, e o peso é a fatia dos pontos dessa entrada. O nome `empty` é a fatia dos pontos deixada vazia, e é a que mais importa acertar, porque uma lista sem ela preenche todos os pontos de todos os acostamentos e a vila vira um viveiro em vez de uma rua.

A cada terceiro bloco de cada lado de uma estrada há um ponto, contado a partir das coordenadas do mundo para que o espaçamento passe de uma peça de estrada para a seguinte. Um ponto é ignorado onde cai dentro de qualquer peça da vila, sobre a própria estrada, diante de uma porta, ou onde o chão não é ar livre sobre algo sólido. O que cresce em um ponto é calculado a partir da seed do mundo e do próprio ponto, de modo que o mesmo mundo sempre espalha da mesma maneira.

O nome aponta para uma entrada comum de worldgen de `<namespace>/worldgen/*.json`, então uma `decoration`, uma `tree` ou uma `imprint` servem, e cada uma mantém seus próprios blocos, tamanhos e dispersão. Somente a forma dessa entrada é usada aqui: seus biomas, dimensões, alturas e raridade são como ela se semeia pelo mundo em geral, e a vila não os consulta, então uma entrada pensada para o acostamento é melhor escrita para nada além disso. Um acostamento é ar livre sobre o chão, então essa entrada quer `replace` definido como `minecraft:air`; uma que nunca nomeia `replace` recebe o padrão usual de `minecraft:stone` e discretamente não coloca nada aqui.

Enquanto `terrainAdaptation` estiver ligado, o que quer que um ponto faça crescer fica protegido contra a própria arrumação da vila, de modo que uma árvore em um acostamento não seja derrubada de novo quando o chunk seguinte é arrumado. Com ele desligado não há arrumação contra a qual protegê-lo, e a dispersão é a mesma.

#### Configurações de vilas por bioma

*vilas*

**Um bioma pode construir de forma diferente.** Um objeto `biomes` dentro de `settings` guarda configurações de vila próprias para um bioma nomeado, de modo que uma vila de deserto assenta ruas de arenito onde uma de planícies assenta concreto, sem que cada uma precise ser um pacote separado. Nomeie um bioma pelo seu id, `minecraft:desert`, ou por um tipo de bioma do Forge, `SANDY`, `SNOWY`, `MESA`, `JUNGLE` e os demais; um id exato é examinado antes dos tipos, de modo que uma regra geral pode ser sobrescrita para um bioma. Tudo o que não for nomeado dentro de uma seção recai na configuração simples acima dela.

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

Toda configuração de bloco que uma estrada, uma ponte, uma ferrovia, um metrô, uma estação ou um esgoto aceita responde a isto, e a sintaxe de mistura ponderada funciona dentro de uma seção como fora dela. O bioma é lido à medida que uma peça é construída, e os blocos são tomados de novo onde quer que o terreno mude de bioma, de modo que uma estrada ou uma ferrovia que sai de um deserto muda de material na própria fronteira. Uma linha de log ao carregar o mundo diz quantas seções um pacote trouxe e as nomeia, e com o debug ligado cada bioma diz qual seção tomou, ou que não tomou nenhuma e a que teria respondido.

### Blast Plaster

*o que cada grupo faz*

O que acontece depois de uma explosão, a partir de `<namespace>/blastplaster/*.json`. `default` deixa os pacotes decidirem, `global` ignora os arquivos dos pacotes e mantém os padrões próprios deste mod sobre a configuração do Blast Plaster, e `off` devolve o Blast Plaster inteiramente à sua própria configuração.

### Estruturas

*o que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| ------------------------------- | --------------------------------------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureSpacing`              | lista de `structure=chunks`                    | vanilla | A distância entre os pontos de geração de uma estrutura. Alcança templos, monumentos, mansões, cidades do End e fortalezas; em `mineshafts` o número significa um chunk em tantos, pois é assim que o vanilla as posiciona |
| `structureSeparation`           | lista de `structure=chunks`                    | vanilla | A menor distância permitida entre duas estruturas do mesmo tipo. Alcança monumentos, mansões, cidades do End, fortalezas e vilas, para as quais é o mínimo de chunks entre uma vila e a seguinte, qualquer que seja a grade |
| `structureMinDistanceFromSpawn` | lista de `structure=blocks`                    | vanilla | A que distância do spawn do mundo uma estrutura começa a gerar |
| `structureBiomes`               | lista de `structure=biome,biome`               | vanilla | Em quais biomas uma estrutura gera, por nome de registro ou tipo do dicionário de biomas. Alcança todas as estruturas exceto cidades do End, já que o End é um único bioma nesta versão |
| `structureBiomesAreBlacklist`   | lista de `structure=true` ou `structure=false` | `false` | O sentido da lista de biomas de cada estrutura |
| `structureSpawns`               | lista de `structure=entity:weight:least:most`  | vanilla | Os mobs que uma estrutura gera, qualquer que seja o bioma ao redor. Só templos, monumentos e fortalezas do Nether mantêm essa lista; uma linha vazia após o sinal de igual impede que a estrutura gere qualquer mob próprio |
| `structureSpawners`             | lista de `structure=entity`                    | vanilla | O que o gerador de monstros dentro de uma estrutura vanilla gera, separado por vírgulas para sorteio por gerador. As quatro que colocam um são masmorras, minas abandonadas, fortalezas do Nether e fortalezas |
| `structureAt`                   | lista de `structure=x,z`                       | nenhum  | Fixa uma estrutura em um ponto exato. Veja [Estruturas em locais exatos](#estruturas-em-locais-exatos) |
| `structureMost`                 | lista de `structure=count`                     | nenhum  | O máximo de uma estrutura que uma dimensão pode ter. Só as vilas o leem, e uma fixada com `structureAt` é fundada de qualquer forma |

Estruturas vanilla desativadas por nome, por dimensão. O posicionamento é controlado com quatro listas escritas como `structure=value`, uma por linha: `structureSpacing` para a distância entre os pontos de geração, `structureSeparation` para a menor distância permitida entre duas, `structureMinDistanceFromSpawn` para a que distância começam a surgir, e `structureBiomes` com `structureBiomesAreBlacklist` para onde podem aparecer.

```
temples=24
monuments=40
mineshafts=200
```

```
temples=minecraft:desert,SANDY
monuments=minecraft:deep_ocean
```

Nem toda estrutura entende todas as configurações. O espaçamento alcança templos, monumentos, mansões, cidades do End e fortalezas; em `mineshafts` o número significa um chunk em tantos, e não uma grade, pois é assim que o vanilla as posiciona. A separação alcança monumentos, mansões, cidades do End, fortalezas e vilas, para as quais é o mínimo de chunks entre uma vila e a seguinte, qualquer que seja a grade. `structureMost` limita quantas de uma estrutura uma dimensão pode ter, `villages=100`: depois que essa quantidade foi fundada, nenhuma outra é, onde quer que a grade a colocasse, enquanto uma vila fixada com `structureAt` é fundada de qualquer forma. Só as vilas o leem. Os biomas alcançam todas as estruturas exceto cidades do End, porque o End é um único bioma nesta versão e não há o que escolher. As cidades do End ainda escolhem o próprio ponto dentro da grade: elas só ficam em uma ilha externa cuja superfície chegue a y60, então aumentar o espaçamento as torna mais raras, mas não consegue colocar uma sobre o vazio. As fortalezas do Nether ficam em uma grade fixa que o vanilla não expõe, então só as listas de bioma e de distância do spawn as alcançam. As vilas mantêm seus próprios `villageSpacing`, `villageBiomes` e os demais.

`structureSpawns` substitui os mobs que uma estrutura gera, qualquer que seja o bioma ao redor, escrito como `structure=namespace:entity:weight:least:most`, separado por vírgulas:

```
netherbridges=minecraft:blaze:10:2:3,minecraft:wither_skeleton:8:5:5
temples=minecraft:witch:1:1:1
monuments=
```

Só templos, monumentos e fortalezas do Nether mantêm essa lista nesta versão; as vilas colocam seus aldeões a partir das próprias peças, e minas abandonadas, fortalezas e cidades do End usam geradores e mobs colocados. Deixar a linha vazia após o sinal de igual, como em monuments acima, impede que a estrutura gere qualquer mob próprio.

`structureSpawners` diz o que o gerador de monstros dentro de uma estrutura vanilla gera, escrito como `structure=namespace:entity`, separado por vírgulas para sorteio por gerador:

```
dungeons=minecraft:zombie,minecraft:husk
mineshafts=minecraft:cave_spider
netherbridges=minecraft:wither_skeleton
strongholds=minecraft:silverfish
```

Quatro estruturas vanilla colocam um gerador: a sala da masmorra, o corredor da mina abandonada, o trono da fortaleza do Nether e a sala do portal da fortaleza. Cada uma é alcançada isoladamente, então geradores colocados por outros mods nunca são tocados. As masmorras normalmente sorteiam da lista à qual os mods adicionam via Forge, então nomeá-los aqui assume também essa escolha.

O espaçamento decide onde uma estrutura é semeada, então alterá-lo em um mundo que já existe mantém o que está lá e coloca as novas em uma grade diferente.

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

| Configuração | Tipo | Padrão | O que faz |
| ----------------------------- | -------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `surfaceDayMonsterRate`       | float                      | `1.0`   | Multiplicador do spawn hostil na superfície de dia, sendo `1.0` o vanilla, de modo que o spawn à luz do dia na superfície pode ser desligado sem tocar nas cavernas |
| `surfaceNightMonsterRate`     | float                      | `1.0`   | O mesmo para a superfície à noite |
| `undergroundDayMonsterRate`   | float                      | `1.0`   | O mesmo no subterrâneo de dia |
| `undergroundNightMonsterRate` | float                      | `1.0`   | O mesmo no subterrâneo à noite |
| `monsterCap`                  | inteiro                    | `-1`    | Quantos hostis podem estar carregados ao mesmo tempo. O vanilla usa 70, e `-1` não mexe nisso |
| `creatureCap`                 | inteiro                    | `-1`    | O mesmo para animais passivos. O vanilla usa 10 |
| `ambientCap`                  | inteiro                    | `-1`    | O mesmo para morcegos e semelhantes. O vanilla usa 15 |
| `waterCreatureCap`            | inteiro                    | `-1`    | O mesmo para lulas. O vanilla usa 5 |
| `monsterSpawnLight`           | inteiro                    | `-1`    | O máximo de luz de bloco que o spawn de um hostil tolera, além das verificações do vanilla. `0` é a regra moderna, em que uma tocha protege totalmente uma caverna; `-1` mantém a sorte do vanilla |
| `skyAnimals`                  | booleano                   | `true`  | Se os mobs passivos se estabelecem na terra que um mundo rubic gera acima da sua janela de terreno, sobretudo nas ilhas do céu. Desligado, mantém animais e morcegos no solo abaixo. Geradores de monstros ignoram ambos |
| `threatItems`                 | lista de `item=level,count` | nenhum | Os itens que aumentam a pontuação de ameaça de um portador, com um `,each` ou `,batch` opcional: `each`, o padrão, soma o nível por cada item carregado até `count`; `batch` soma uma vez a cada `count` inteiro carregado |
| `threatLevels`                | lista de inteiros          | nenhum  | As pontuações em que se entra em cada faixa, crescentes, então `10, 25, 50` forma três faixas. Vazio desliga o nível de ameaça |
| `threatMost`                  | inteiro                    | `-1`    | Limita a pontuação. `-1` deixa sem limite |
| `threatSpawnRate`             | float                      | `1.0`   | Escala o spawn hostil a até 128 blocos de um portador na faixa mais alta, além das outras taxas, com as faixas mais baixas recebendo uma parcela proporcional |
| `threatNotice`                | float, blocos              | `0.0`   | Quantos blocos mais longe os mobs hostis, inclusive os vanilla, enxergam um portador na faixa mais alta, de novo repartido entre as faixas mais baixas |
| `threatSays`                  | lista de `band=message`    | nenhum  | As linhas exibidas em amarelo quando a faixa do próprio jogador muda, sendo a faixa `0` a linha para voltar abaixo da primeira faixa |

Taxas e limites de spawn de mobs, por bioma. O spawn hostil é escalado por `surfaceDayMonsterRate`, `surfaceNightMonsterRate`, `undergroundDayMonsterRate` e `undergroundNightMonsterRate`, cada um um multiplicador em que `1.0` é o vanilla, de modo que o spawn à luz do dia na superfície pode ser desligado sem tocar nas cavernas. Os limites são `monsterCap`, `creatureCap` para animais passivos, `ambientCap` para morcegos e semelhantes, e `waterCreatureCap` para lulas; os do vanilla são 70, 10, 15 e 5, e `-1` não mexe em nenhum. `monsterSpawnLight` limita a luz de bloco que o spawn de um hostil tolera, além das verificações do vanilla: `0` é a regra moderna, em que uma tocha protege totalmente uma caverna, e `-1`, o padrão, mantém a sorte do vanilla. `skyAnimals` decide se os mobs passivos se estabelecem na terra que um mundo rubic gera acima da sua janela de terreno, sobretudo nas ilhas do céu: `true`, o padrão, mantém os rebanhos do vanilla onde quer que fique o bloco mais alto, e `false` mantém animais e morcegos no solo abaixo. Geradores de monstros ignoram ambos.

O nível de ameaça pontua o que cada jogador carrega e deixa o mundo responder. `threatItems` lista os itens que contam, como entradas `item=level,count` com um `,each` ou `,batch` opcional no fim: `each`, o padrão, soma o nível por cada item carregado, contando no máximo `count` deles, e `batch` soma o nível uma vez a cada `count` carregados, apenas lotes inteiros. Uma quantidade acima do tamanho da pilha do item é reduzida ao tamanho da pilha, então uma pilha cheia é o máximo que uma entrada pode contar, e o item pode ter metadados, como `minecraft:dye:4`. Toda entidade carregada que segura itens é um portador, não apenas jogadores: o inventário principal, a armadura e a mão secundária de um jogador, uma pilha largada, qualquer coisa com inventário de itens, como uma mula com baú ou um carrinho com baú, e os itens segurados e a armadura de qualquer outro mob, de modo que uma área continua perigosa em torno do que fica, anda ou é cavalgado ali. Jogadores no criativo e no espectador não pontuam nada. `threatLevels` são as pontuações em que se entra em cada faixa, crescentes, então `[10, 25, 50]` forma três faixas, e `threatMost` limita a pontuação, com `-1` deixando-a sem limite. A pontuação é medida a cada cinco segundos. `threatSpawnRate` escala o spawn hostil a até 128 blocos de um portador na faixa mais alta, além das outras taxas, e as faixas mais baixas recebem uma parcela proporcional da mudança: `2.0` o dobra no topo e soma um terço na faixa um de três. `threatNotice` é quantos blocos mais longe os mobs hostis, inclusive os vanilla, enxergam um portador na faixa mais alta, de novo repartido proporcionalmente entre as faixas mais baixas. `threatSays` são as linhas exibidas em amarelo quando a faixa do próprio jogador muda, como entradas `band=message`, sendo a faixa `0` a linha para voltar abaixo da primeira faixa. Uma variante de entidade pode definir `threatLeast` para só gerar naturalmente enquanto um portador a até 128 blocos estiver nessa faixa ou acima. Qualquer uma das listas deixada vazia desliga o nível de ameaça.

### Posicionando estruturas

*o que cada grupo faz*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"]
  }
}
```

| Configuração | Tipo | Padrão | O que faz |
| --------------------- | ------------------------ | --------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureAdaptation` | lista de `structure=mode` | vilas e mansões `beard_thin`, o resto `none` | A quais estruturas o terreno se adapta e como, entre vilas, fortalezas, minas abandonadas, monumentos e mansões. Os modos são `none`, `bury`, `beard_thin`, `beard_box` e `encapsulate` |

`structureAdaptation` decide a quais estruturas o terreno se adapta e como, em entradas `structure=mode`, `"mansions=bury"`, `"monuments=none"`, entre vilas, fortalezas, minas abandonadas, monumentos e mansões, com os cinco modos usados pelas versões modernas: `none`, `bury`, `beard_thin`, `beard_box` e `encapsulate`. Vilas e mansões usam `beard_thin`, a menos que sejam sobrescritas, e todo o resto é `none`, a menos que seja nomeado. Os templos ainda não podem ser nomeados, porque só se posicionam à medida que são construídos, então não há nada a que o terreno se adaptar a tempo.

### Posicionando vilas

*o que cada grupo faz*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "terrainAdaptation": true
  }
}
```

| Configuração | Tipo | Padrão | O que faz |
| ------------------- | ------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `terrainAdaptation` | booleano | `false` | Refaz como as vilas escolhem o terreno e se assentam nele: estradas niveladas, construções assentadas, anéis em talude e tudo o mais que esta seção descreve. O que ela estabelece é permanente |

**O que ela estabelece é permanente.** Ela remodela o terreno à medida que o mundo é criado, então tudo o que coloca em um save permanece lá. Uma vila criada por uma versão mais antiga nunca é revisitada nem reparada por uma mais nova, então dois mundos criados com a mesma seed em duas versões diferentes do mod não serão iguais, e as vilas de um mundo são um retrato do dia em que aqueles chunks foram gerados.

`terrainAdaptation` refaz como as vilas escolhem o terreno e se assentam nele, portado no espírito de como as versões modernas assentam suas estruturas e depois levado mais longe. Uma vila só é fundada em um chunk cujo terreno varie no máximo dez blocos, e nunca a menos de oito chunks de outra vila; regiões que não oferecem tal chunk não fundam nada. O poço se assenta no ponto mais baixo que sua própria área toca, e a vila inteira se desloca com ele, de modo que todo o resto se nivela a partir daí.

As estradas são niveladas à medida que são traçadas: a superfície segue o terreno natural mais baixo ao longo da largura da estrada, os montes são cortados, os buracos são preenchidos, a inclinação nunca passa de um bloco por passo, e pequenos abismos são vencidos com tábuas. A superfície da estrada acompanha o terreno que atravessa: caminhos de grama sobre terra, arenito sobre areia, argila endurecida na mesa, cascalho sobre pedra e cascalho, tábuas sobre a água, de modo que uma vila no deserto ganha ruas de arenito em vez de uma trilha de terra e as estradas não somem mais onde o chão não é grama. Onde duas estradas se cruzam, elas se encontram no mais baixo dos dois níveis, já que um nível que ambas alcançam é o único que não deixa degrau entre elas.

Cada construção se assenta um bloco acima da estrada que ela encara, lido da estrada já traçada ou previsto a partir do terreno em que a estrada vai se nivelar quando ainda não foi construída, de modo que os degraus da entrada repousam sobre a superfície da estrada e a porta fica atrás deles. Uma construção cuja área precisaria de mais de dois blocos de terreno aterrado sob qualquer parte dela não é construída ali: ela desliza até doze blocos ao longo da estrada procurando o assento mais raso, e é descartada por completo se não encontra nenhum, então vilas em terreno acidentado saem mais esparsas em vez de empoleiradas. O anel ao redor de uma construção é elevado no lado em declive e cortado no lado em aclive, um bloco mais raso a cada anel mais afastado.

As fazendas mantêm o nível do terreno do próprio vanilla. Os postes de luz ficam no nível da estrada que iluminam em vez do acostamento ao lado, com terra preenchida sob eles onde a estrada passa acima da borda, e os postes de tocha do próprio vanilla ficam fora do layout, já que estes os substituem. O terreno é preenchido sob cada construção até a superfície de apoio mais próxima, no mesmo material em que ela repousa, paredes e portas são abertas nas encostas, a terra é removida dos telhados, e qualquer árvore dentro de uma estrutura é derrubada inteira, com as folhas indo junto com a madeira, enquanto toda folha que um galho em pé ainda possui é deixada em paz. Mansões e as construções isoladas (templos, cabanas, iglus) seguem o mesmo padrão de terreno plano antes de poderem se posicionar.

Ela remodela o próprio terreno à medida que é criado, então um mundo gerado com ela ligada difere de um gerado sem ela, o mesmo aviso que as versões modernas trazem, e fica desligada a menos que um pacote ou a configuração peça.

### Bedrock

*o que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| ----------------------------------- | ------------------------- | --------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `flatBedrock`                       | booleano                  | `false`                     | Substitui a bedrock irregular do fundo do mundo por camadas planas. Só chunks novos, a menos que `flatBedrockRetrogen` esteja ligado |
| `flatBedrockRetrogen`               | booleano                  | `false`                     | Achata também a bedrock dos chunks que já existem. Cada chunk é feito uma vez e se lembra disso, e não pode ser desfeito: o padrão original não é registrado em lugar nenhum |
| `bedrockLayers`                     | inteiro                   | `1`                         | Quantas camadas de bedrock restam |
| `flatBedrockRoof`                   | booleano                  | `false`                     | Achata também o teto de bedrock, onde a dimensão tem um, como o teto do Nether |
| `flatBedrockFiller`                 | bloco                     | vazio                       | O que substitui a bedrock removida. Vazio escolhe por dimensão: pedra, netherrack, pedra do End |
| `flatBedrockFillers`                | lista de `dimension=block` | os padrões do Nether e do End | Um preenchimento por dimensão, que sobrescreve `flatBedrockFiller` nas dimensões nomeadas |
| `flatBedrockDimensions`             | lista de inteiros         | `0`, o overworld            | As dimensões em que se achata. Vazio significa todas |
| `flatBedrockDimensionsAreBlacklist` | booleano                  | `false`                     | Ligado, o achatamento pula as dimensões listadas. Desligado, aplica-se apenas a elas |
| `flatBedrockBiomes`                 | lista de nomes de bioma   | nenhum                      | Os biomas em que se achata, por nome amigável ou de registro. Vazio significa todos os biomas |
| `flatBedrockBiomeTypes`             | lista de tipos do dicionário | nenhum                   | Tipos do dicionário de biomas em que se achata, junto com `flatBedrockBiomes`. `OCEAN`, `RIVER`, `MOUNTAIN` e os demais |
| `flatBedrockBiomesAreBlacklist`     | booleano                  | `false`                     | Ligado, o achatamento pula os biomas listados. Desligado, aplica-se apenas a eles |

`flatBedrock` substitui a camada irregular por camadas planas, por dimensão e por bioma, com um bloco de preenchimento à sua escolha. `flatBedrockRetrogen` faz isso nos chunks que já existem. Não pode ser desfeito, o padrão original não é registrado em lugar nenhum. `bedrockLayers` define quantas camadas restam, `flatBedrockRoof` faz o teto também onde a dimensão tem um, e `flatBedrockFiller` é o que substitui a bedrock removida, deixado vazio para escolher por dimensão, com `flatBedrockFillers` nomeando um por dimensão em vez disso. Quais dimensões e biomas ele alcança é definido por `flatBedrockDimensions`, `flatBedrockBiomes` e `flatBedrockBiomeTypes`, com `flatBedrockDimensionsAreBlacklist` e `flatBedrockBiomesAreBlacklist` transformando essas listas em exclusões.

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

As entidades custam ao servidor mais do que qualquer outra coisa, e a maioria delas está longe de qualquer jogador. `slowDistantEntities` dá a um chunk sem jogador a até `slowDistance` blocos um tick a cada `slowRate`, de modo que o que está nele ainda se move, flutua, queima e desaparece, só que em ritmo mais lento. Nada nunca fica sem tick.

| Chave | Obrigatório | Valor | Padrão | O que faz |
| --------------------- | -------- | -------------------------------------------- | --------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `slowDistantEntities` | não      | booleano                                     | `true`                | Se algo é desacelerado ou não |
| `slowedKinds`         | não      | lista de `items`, `experience`, `projectiles` | `{items, experience}` | Quais tipos recebem menos ticks. Tudo o que pensa por conta própria é sempre desacelerado de outra forma e não é nomeado aqui. Máquinas nunca são desaceleradas |
| `slowDistance`        | não      | inteiro, 64 ou mais                          | `192`                 | A que distância do jogador mais próximo um chunk passa a ser desacelerado |
| `slowRate`            | não      | inteiro, 1 a 20                              | `4`                   | Um tick a cada tantos é dado a um chunk desacelerado. `1` não desacelera nada |
| `neverSlowed`         | não      | lista de nomes de entidades                  | nenhum                | Deixadas em paz por mais longe que estejam |
| `slowRecheck`         | não      | inteiro, 1 a 100                             | `20`                  | Com que frequência a distância até o jogador mais próximo é calculada de novo |

Tudo o que pensa por conta própria, todo mob, animal, aldeão e golem, de qualquer mod, é tratado de forma diferente do resto e não é nomeado em `slowedKinds`. Ele nunca recebe menos ticks, porque um jogador pode vê-lo andar. Em vez disso, continua com tick a cada tick e é feito pensar com menos frequência: a parte da mente que decide o que fazer em seguida, que também é a parte cara, é consultada uma vez a cada `slowRate` em vez de a cada três ticks. Ele continua se movendo, caindo, se afogando, queimando e se deslocando exatamente como faria, e simplesmente muda de ideia com menos frequência enquanto ninguém está por perto. Não há nada a ver, nem passos entrecortados nem recuperação, e um mob em que um jogador se aproxima volta ao normal antes de entrar em vista. Como não pode ser notado, não é uma escolha: acontece onde quer que a desaceleração esteja ligada.

O que recebe menos ticks continua envelhecendo no ritmo normal. Um item largado e uma esfera de experiência carregam cada um o próprio contador que decide quando somem, e em um tick que um chunk desacelerado não recebe, esse contador é avançado de qualquer forma. Assim, um item ainda fica no chão por cinco minutos em vez de vinte. Só o que ele faz a cada tick é reduzido, nunca quanto tempo dura.

Um chunk que algo mantém carregado de propósito nunca é desacelerado, por mais longe que esteja. São os chunks que um carregador de chunks mantém, e todo o sentido de mantê-los é que o que está neles continue funcionando, de modo que uma fazenda deixada trabalhando enquanto o dono está longe trabalha no ritmo para o qual foi construída. Os chunks ao redor do spawn de um mundo não são desses, já que ninguém os pediu, então são desacelerados como em qualquer outro lugar.

Um chunk inteiro é desacelerado ou não em conjunto, de modo que o que está dentro dele continua se comportando como deveria: os itens caem na mesma pilha, um mob ainda segue o que está ao lado. Cada jogador conta por si, então quem está sozinho em algum lugar ainda tem um espaço tranquilo ao redor, onde quer que esteja. Algo cavalgado, nomeado, domado, preso por guia, brilhando, impedido de desaparecer, sob efeito ou já perseguindo um jogador é deixado em paz por mais longe que esteja, assim como todas as máquinas. Aplica-se a todos os mundos, inclusive os que um mod adiciona.

### Acompanhando o trabalho de chunks

*o que cada grupo faz*

Com `worldgenDebug` ligado, uma linha a cada cem rodadas diz como o mundo está gastando seu trabalho de chunks: quantos chunks foram feitos do zero, quantos tiveram de ser buscados de volta depois de liberados, quantos deles vieram do disco em vez da fila ainda à espera de gravação, quantos arquivos de região foram abertos e com que frequência todos foram fechados de uma vez, e o máximo de chunks retidos e gravações pendentes em qualquer momento. Foi escrita para descobrir se gerar terreno está custando tempo na geração ou em buscar de volta o mesmo chão, então vale a pena ligá-la antes de uma grande pré-geração e desligá-la depois.

Mais três linhas a seguem: uma para gravar chunks de volta no armazenamento, uma para iluminá-los, e uma que divide a criação do terreno em si entre o chão, o acabamento que o jogo coloca sobre ele e o acabamento que cada mod coloca, com os cinco piores nomeados. Um mundo lento pode então ser lido como quatro custos separados em vez de um só, e o mod responsável nomeado em vez de adivinhado.

### Criando terreno com antecedência

*o que cada grupo faz*

Grande o bastante para ter uma seção própria, veja [Pré-geração](#pré-geração).

### Blocos aguardando a vez

*o que cada grupo faz*

Água que se espalha, lava que esfria e plantações que crescem são todos blocos que esperam um tempo antes de fazer algo, e o jogo guarda todos eles em um único monte. Cada vez que um chunk é gravado, ele percorre esse monte inteiro de ponta a ponta atrás dos poucos que lhe pertencem, então quanto mais eles existem em um mundo, mais lenta fica cada gravação, quer o chunk gravado tenha algum ou não. Agora eles são ordenados pelo chunk em que estão, e a ordenação é descartada e refeita no instante em que o monte muda ou a rodada avança, de modo que gravar um chunk olha apenas para o punhado que lhe diz respeito.

### Mais espaço para os blocos em um chunk

*o que cada grupo faz*

Um chunk é guardado em fatias, e cada fatia mantém uma lista dos tipos de bloco que há nela, começando com espaço para dezesseis. Passar de dezesseis significa criar uma lista maior e copiar para ela cada um dos quatro mil blocos da fatia, e de novo aos trinta e dois, e de novo aos sessenta e quatro. Um terreno com alguns tipos de pedra e minério passa por todos esses limites, então isso é feito quatro vezes por um pouco de espaço. Agora ele vai direto ao maior desses tamanhos na primeira vez que o espaço acaba, o que é uma cópia em vez de quatro e custa alguns quilobytes por fatia que de qualquer forma seria usada em instantes.

### Preparando chunks para gravação

*o que cada grupo faz*

Antes que um chunk possa ser gravado, ele é convertido na forma que vai para o disco, o que percorre cada um de seus blocos e procura cada um em uma tabela por nome. O terreno vem em longas sequências da mesma coisa, então a mesma consulta é feita milhares de vezes para a mesma pedra, e a resposta da última é simplesmente guardada e reaproveitada quando o próximo bloco é igual. Não é algo que possa ser desligado, já que não há o que ponderar: a resposta é a mesma de qualquer jeito.

### Gravando chunks

*o que cada grupo faz*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "hurryWritesAbove": 100
  }
}
```

| Configuração | Tipo | Padrão | O que faz |
| ------------------ | ----------- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hurryWritesAbove` | inteiro, chunks | `100` | Quantos chunks concluídos podem esperar para ser gravados antes que o gravador pare de descansar um centésimo de segundo após cada um e simplesmente grave o mais rápido que puder. `0` o deixa sempre descansando, como faz o jogo |

O jogo grava chunks concluídos em uma thread própria, um de cada vez, descansando um centésimo de segundo após cada um. Isso o limita a cerca de cem chunks por segundo, não importa a rapidez do disco, o que é de sobra enquanto alguém joga e está longe de bastar enquanto o terreno é criado em massa, de modo que os chunks não gravados se acumulam na memória. `hurryWritesAbove` diz quantos podem estar esperando antes que ele pare de descansar e simplesmente grave o mais rápido que puder. `100` é o padrão e coincide com o ponto em que o próprio jogo começa a segurar a geração; `0` o deixa sempre descansando, como faz o jogo. Nada muda enquanto o número de chunks em espera é pequeno, o que é todo momento comum de jogo.

Cada vez que a faxina roda, uma linha é escrita para ela na hora, nomeando qual varredor rodou, quanto tempo levou, o que estava retido antes e depois, e quanto espaço o jogo tinha na ocasião. Se esse espaço muda, isso é dito, porque o crescimento do espaço é justamente o que causa as mais longas dessas pausas: um jogo iniciado com menos espaço do que acaba precisando vai parar para aumentá-lo, repetidamente, em momentos que nada têm a ver com o que ele está fazendo. Iniciá-lo com todo o espaço que lhe é permitido evita isso por completo.

Uma última linha diz quanto lixo de trabalho foi descartado desde a última verificação, quanto tempo a limpeza levou e quantas varreduras foram, e quanto do espaço permitido ao jogo está retido no momento. Criar terreno descarta muito por natureza, já que cada chunk é transformado em arrays novos antes de ser gravado, e essa limpeza acontece entre rodadas e não durante elas, então aparece como um engasgo e não como tempo em nenhuma das contagens acima.

### Chunks de spawn

*o que cada grupo faz*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["0=64", "7=0"]
  }
}
```

| Configuração | Tipo | Padrão | O que faz |
| ------------------ | -------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `spawnChunkRadius` | inteiro, blocos            | `128`   | A que distância do ponto de spawn do mundo, em blocos, os chunks são mantidos carregados, haja alguém ali ou não. B blocos mantêm `r = (B + 8) / 16` chunks em cada direção a partir do chunk de spawn, `(2r+1)²` no total, e o mundo prepara `(2r+9)²` chunks ao redor dele ao iniciar. `128` é o que o jogo faz, com 289 mantidos e 625 preparados, e `0` não mantém nem prepara nenhum |
| `spawnChunkRadii`  | lista de `dimension=blocks` | nenhum  | Um raio para uma dimensão de cada vez, que sobrescreve `spawnChunkRadius` nas dimensões nomeadas |

O jogo mantém carregados os chunks ao redor do ponto de spawn de um mundo, haja alguém ali ou não, para que os mods tenham um lugar que sempre recebe ticks. São 128 blocos em todas as direções, cerca de 289 chunks, e não é ajustável no jogo. `spawnChunkRadius` define essa distância. `128` é o que o jogo faz e é o padrão, um número menor mantém uma âncora menor, e `0` não mantém nenhuma, de modo que a área de spawn é descarregada como qualquer outra. `spawnChunkRadii` define um raio para uma dimensão de cada vez, escrito como `dimension=blocks`, um por linha, e sobrescreve `spawnChunkRadius` nas dimensões nomeadas.

Só uma dimensão registrada para manter seu spawn mantém um, o que no próprio jogo é apenas o overworld; o Nether e o End nunca mantiveram, então configurar isso para eles não muda nada. Uma dimensão que um mod adiciona só mantém um se esse mod pediu, e um mod que pediu muitas vezes carrega outros 289 chunks que um pacote nunca quis. Se um mundo permanece carregado ou não é outra questão, que isto não toca: uma dimensão que um mod marcou como permanentemente carregada continua carregada com `0`, apenas deixa de manter chunks. A maioria dos mods que usam o spawn como âncora quer algo ali, e não 289 chunks dele, então um número pequeno costuma mantê-los funcionando enquanto um `0` não.

### Mundo vazio

*o que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| --------------------------------- | ------------ | ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| `voidWorld`                       | booleano     | `false`            | Gera um mundo vazio com uma plataforma no ponto de spawn, e impede mobs, animais, estruturas e tudo o que um mod geraria ali de outra forma |
| `voidPlatformBlock`               | bloco        | `minecraft:stone`  | De que a plataforma é feita |
| `voidPlatformSize`                | inteiro, blocos | `9`             | Qual a largura da plataforma, arredondada para baixo até um número ímpar para que fique centralizada no spawn |
| `voidPlatformHeight`              | inteiro      | `64`               | A que altura acima do fundo do mundo a plataforma fica |
| `voidWorldDimensions`             | lista de inteiros | `0`, o overworld | Quais dimensões são esvaziadas. Só o overworld recebe uma plataforma |
| `voidWorldDimensionsAreBlacklist` | booleano     | `false`            | Ligado, as dimensões listadas são as que ficam intactas |

`voidWorld` gera um mundo vazio com uma plataforma no ponto de spawn, e impede mobs, animais, estruturas e tudo o que um mod geraria ali de outra forma. O bloco, o tamanho e a altura da plataforma são `voidPlatformBlock`, `voidPlatformSize` e `voidPlatformHeight`; o tamanho é arredondado para baixo até um número ímpar de blocos para que a plataforma fique centralizada no spawn. `voidWorldDimensions` escolhe quais mundos são esvaziados, só o overworld por padrão, e `voidWorldDimensionsAreBlacklist` transforma essa lista na dos que ficam intactos. O Nether e o End são esvaziados da mesma forma que o overworld, sejam os que esta versão constrói ou os que um mod colocou no lugar deles. Só o overworld recebe uma plataforma, então um caminho para um Nether ou End esvaziado é algo que o próprio pacote fornece. Um End esvaziado também não tem dragão, cristais nem fonte de bedrock, já que a luta que os constrói não é iniciada.

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
| ------------- | ------- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `dragonFight` | booleano | `true`  | Se tudo isso acontece ou não: o dragão, sua barra, os cristais, a fonte sobre a qual ele fica, e o renascimento que um jogador iniciaria com cristais do End. Pertence ao grupo `structures` |

`dragonFight` pertence ao grupo `structures` e decide se tudo isso acontece ou não: o dragão, sua barra, os cristais, a fonte sobre a qual ele fica, e o renascimento que um jogador iniciaria com cristais do End. Um End esvaziado o deixa de fora a menos que um pacote peça, e um End comum o tem a menos que um pacote diga o contrário, então vale a pena definir `dragonFight` nos dois sentidos.

### Terreno

*o que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| ------------------------------- | ------------------- | -------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldType`                     | string              | vazio                            | O tipo de mundo com que todo mundo novo é criado, qualquer que seja o escolhido na tela em que foi criado: `default`, `largebiomes`, `amplified`, `customized`, ou um que um mod adicione. Um servidor dedicado o grava em `server.properties` como `level-type` antes de os mundos carregarem, a menos que `level-type` já nomeie um dos `worldTypeExceptions`. Vazio deixa a escolha para quem cria o mundo |
| `worldTypeExceptions`           | lista de tipos de mundo | `flat`, `debug_all_block_states` | As escolhas que `worldType` deixa de pé |
| `worldSeed`                     | string              | vazio                            | A seed com que todo mundo novo é criado, escrita como seria digitada: um número é usado como está, e qualquer outra coisa é convertida em um da forma como o jogo faz. Um servidor dedicado a grava em `server.properties` como `level-seed` antes de os mundos carregarem |
| `terrainWorldTypes`             | lista de tipos de mundo | nenhum                       | A quais tipos de mundo as configurações de terreno são aplicadas. Vazio significa todos |
| `terrainWorldTypesAreBlacklist` | booleano            | `false`                          | Ligado, os tipos de mundo listados são os que ficam intactos |

`worldType` decide que tipo de mundo é um mundo novo, qualquer que seja o escolhido na tela em que foi criado, `default`, `largebiomes`, `amplified`, `customized`, ou um que um mod adicione, como `biomesop` ou `realistic`. Um pacote construído em torno de um tipo de mundo o nomeia aqui e todo mundo novo é criado assim. Vazio, o padrão, deixa a escolha para quem está criando o mundo. Um mundo que já existe mantém o tipo com que foi criado, e um nome que nada fornece é registrado no log e ignorado. `worldTypeExceptions` nomeia as escolhas que são deixadas de pé, superplano e o mundo de depuração para começar, já que um pacote que quer um só tipo de mundo raramente pretende tirar o superplano de quem está testando, e quem cria um mundo é avisado no chat, uma vez dentro dele, de que o pacote escolheu o seu tipo. Essa mensagem é decidida pelo arquivo de configuração com `tellWorldType`, e não por um pacote, de modo que quem joga pode desligá-la para si e nenhum pacote pode religá-la. As configurações com que o mundo foi criado são descartadas quando o tipo é alterado, já que foram escritas para o tipo que havia sido escolhido.

`worldSeed` decide a seed com que todo mundo novo é criado, qualquer que seja a digitada na tela em que foi criado. É escrita da mesma forma como seria digitada: um número é usado como está, e qualquer outra coisa é convertida em um número da forma como o jogo converte uma palavra em um, então `Hollow Ridge` e `-4172144997902289642` são ambos permitidos e sempre dão o mesmo mundo. Vazio, o padrão, deixa a escolha para quem está criando o mundo. Um mundo que já existe mantém a seed com que foi criado, então isso só decide o que um novo recebe. Um pacote construído em torno de um mapa nomeia sua seed aqui e todo mundo criado com esse pacote é esse mapa.

`generatorOptions` molda o próprio overworld, nível do mar, oceanos de lava e todo ruído de terreno, no mesmo formato que o tipo de mundo customizado escreve. É aplicado a um mundo quando ele é criado e nunca depois, então um mundo que já existe fica exatamente como estava. Um mundo que já traz opções próprias as mantém, e o log nomeia a string usada. Um servidor dedicado as grava em `server.properties` como `generator-settings` antes de os mundos carregarem.

Um tipo de mundo que traz as próprias configurações e nunca olha as do mundo, como o realistic do Quark, recebe as configurações do pacote mescladas às suas, de modo que a forma para a qual foi construído permanece a menos que um pacote peça outra coisa.

`terrainWorldTypes` nomeia os tipos de mundo a que as configurações são aplicadas, `default`, `customized`, `biomesop`, `realistic` e assim por diante, e `terrainWorldTypesAreBlacklist` transforma isso na lista dos que ficam intactos. Vazio, o padrão, significa todos os tipos de mundo. Um pacote que molda o mundo comum mas quer um tipo de mundo de um mod deixado exatamente como esse mod o fez o nomeia aqui e pronto: nada é mesclado, nada é repassado, e a tela de personalização do próprio mod continua aberta. Os nomes são comparados com o tipo de mundo com que um mundo foi criado, então nomear um que nada aqui fornece simplesmente nunca corresponde e não custa nada.

Tudo abaixo sobre o Biomes O' Plenty só acontece quando esse mod está instalado, já que o trabalho é feito por compatibilidade que só é carregada quando ele está presente. Sem ele não há tipo de mundo `biomesop` para escolher, e um pacote que nomeia um fica com o tipo de mundo com que o mundo foi realmente criado.

Em um mundo do Biomes O' Plenty, as mesmas configurações são convertidas nas palavras que esse mod lê, então um pacote não precisa de uma segunda cópia delas. `biomeSize` vira um de seus cinco tamanhos, as configurações de ruído e escala passam como estão, e tudo o que ele nunca lê é deixado de fora com uma linha no log dizendo isso. Esse mod lê bem menos do que o tipo de mundo customizado, e nunca lê nível do mar, cavernas, lagos nem os interruptores de estruturas de suas configurações, então esses são entregues a ele diretamente, e um pacote os define da mesma forma que faria em qualquer outro mundo.

Duas coisas ele decide por conta própria. Os rios saem de suas próprias camadas e não têm configuração, então `riverSize` não significa nada ali. E onde oceanos, montanhas e regiões de fato ficam também são suas camadas, alcançáveis apenas por `landScheme`, `tempScheme`, `rainScheme` e `biomeSize`, então um pacote molda esse mundo nos termos desse mod e não nos do tipo de mundo customizado. Um mundo de um único bioma ainda é algo que um pacote pode fazer: bloqueie todos os biomas e nomeie o que quiser como o `default` do template, o que funciona no tipo de mundo dele da mesma forma que em qualquer outro.

Tudo o mais que um pacote faz, bloquear biomas e minérios, substituir blocos, bedrock plana, posicionamento de estruturas, a própria geração de mundo, nunca passou por essa string, e funciona da mesma forma em qualquer tipo de mundo.

### Servidor

*o que cada grupo faz*

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

`control.server` decide este grupo: as linhas de `server.properties` que um pacote pode definir, com o modo de jogo, a dificuldade e os comandos em um mundo aberto para a LAN. Em um servidor dedicado, todo valor que um pacote define aqui é gravado em `server.properties` quando o servidor inicia, de modo que o arquivo nomeia o que está em vigor, e os que o servidor já leu também são definidos nele. Um mundo single player recebe o que um servidor integrado tem, como diz cada linha. Vazio, ou `-1` para um número, deixa o valor do próprio servidor, e com `control.server` em `off` toda linha permanece como o servidor a tem.

| Configuração | Tipo | Padrão | O que faz |
| ---------------------- | -------------------------------------------------------------- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldGameMode`        | `survival`, `hardcore`, `creative`, `adventure` ou `spectator` | vazio   | O modo em que todo mundo novo é iniciado, aplicado apenas na criação em single player. Um servidor dedicado define todo mundo com o modo de seu `server.properties` a cada início, então ali o modo do pacote é gravado em `server.properties` (`gamemode` e `hardcore`) antes de os mundos carregarem. `hardcore` é sobrevivência mais o indicador hardcore do save inteiro, e `creative` também ativa os cheats |
| `worldLanCommands`     | booleano                                                       | `true`  | Se um jogador que abre um mundo single player para a LAN pode ativar os comandos para todos que entrarem. `false` deixa cinza o botão Permitir Cheats da tela Abrir para LAN e o mantém desligado, e o mundo é aberto sem comandos seja como for pedido, `/publish` inclusive |
| `worldDifficulty`      | lista                                                          | nenhum  | Trava a dificuldade em `peaceful`, `easy`, `normal` ou `hard`. Uma dificuldade sozinha cobre todas as dimensões, e uma linha `dimension=difficulty` a sobrescreve para aquela. Um servidor dedicado grava a dificuldade do overworld em `server.properties` como `difficulty` |
| `worldForceGameMode`   | booleano                                                       | vazio   | Se um jogador que entra é recolocado no modo de jogo do servidor toda vez, a linha `force-gamemode`. Um mundo single player aberto para a LAN também a recebe |
| `worldPvp`             | booleano                                                       | vazio   | Se os jogadores podem ferir uns aos outros, a linha `pvp`. Um mundo single player também a recebe |
| `worldFlight`          | booleano                                                       | vazio   | Se um jogador voando na sobrevivência é deixado em paz em vez de expulso, a linha `allow-flight`. Um mundo single player também a recebe |
| `worldSpawnProtection` | inteiro, -1 ou mais                                            | `-1`    | Quantos blocos ao redor do ponto de spawn só operadores podem construir, a linha `spawn-protection`, 0 para nenhum. Só um servidor dedicado protege seu spawn |
| `worldNether`          | booleano                                                       | vazio   | Se o Nether pode ser acessado, a linha `allow-nether`. `false` o fecha também em um mundo single player |
| `worldCommandBlocks`   | booleano                                                       | vazio   | Se os blocos de comando funcionam, a linha `enable-command-block`. Um mundo single player já os executa, e `false` os desliga ali também |
| `worldIdleTimeout`     | inteiro, -1 ou mais                                            | `-1`    | Quantos minutos um jogador pode ficar parado antes de ser expulso, a linha `player-idle-timeout`, 0 para nunca. Um mundo single player também a recebe |
| `worldMotd`            | texto                                                          | vazio   | A linha exibida sob o nome do servidor na lista de servidores, a linha `motd`. Um mundo single player aberto para a LAN a exibe no lugar do dono e do nome do mundo |
| `worldMaxSize`         | inteiro, -1 a 29999984                                         | `-1`    | O mais longe, em blocos a partir do centro, que a borda de um mundo pode chegar, a linha `max-world-size`. Um mundo single player também a recebe |
| `worldStructures`      | booleano                                                       | vazio   | Se um mundo novo gera estruturas, a linha `generate-structures` e a opção Gerar Estruturas da tela do mundo. Só aplicada a um mundo quando ele é criado |
| `worldSpawnMonsters`   | booleano                                                       | vazio   | Se os mobs hostis surgem, a linha `spawn-monsters`. `false` os impede também em um mundo single player |
| `worldSpawnAnimals`    | booleano                                                       | vazio   | Se os animais surgem, a linha `spawn-animals`. Um mundo single player também a recebe |
| `worldSpawnNpcs`       | booleano                                                       | vazio   | Se os aldeões surgem, a linha `spawn-npcs`. Um mundo single player também a recebe |
| `worldViewDistance`    | inteiro, -1 a 32                                               | `-1`    | A quantos chunks de distância um servidor dedicado envia o mundo a cada jogador, a linha `view-distance`. Um mundo single player segue a distância de renderização |
| `worldBuildHeight`     | inteiro, -1 a 256                                              | `-1`    | O maior y em que um bloco pode ser colocado, a linha `max-build-height`, arredondado para um múltiplo de 16 entre 64 e 256. Um mundo single player também a recebe |

**`worldGameMode`** (grupo `server`): `survival`, `hardcore`, `creative`, `adventure` ou `spectator`. Aplicado apenas na criação do mundo; mundos existentes não são tocados, e mudar o modo depois é deixado em paz. `hardcore` é sobrevivência mais o indicador hardcore do save inteiro do vanilla; `creative` também ativa os cheats, como a caixa de seleção da tela de criação faria. A tela de criação abre com o modo (e a seed do pacote) já selecionados; um jogador pode alterá-lo ali, mas o pacote o redefine na criação. `adventure` e `spectator` não são oferecidos nessa tela e são aplicados quando o mundo é criado.

**`worldLanCommands`** (grupo `server`): `true` (padrão) deixa a tela Abrir para LAN como o vanilla a tem. `false` deixa cinza o botão Permitir Cheats e o mantém desligado, de modo que um jogador que abre o mundo para a LAN não possa entregar os comandos a todos que entrarem. O mundo é aberto sem comandos também no lado do servidor, seja o que for que os peça, `/publish` inclusive. Governa apenas a abertura para a LAN; um servidor dedicado não é afetado.

**`worldDifficulty`** (grupo `server`): `peaceful`, `easy`, `normal` ou `hard`. Um valor sozinho cobre todas as dimensões; linhas `dimension=difficulty` (`-1=hard`) sobrescrevem por dimensão. A trava vale também contra o menu de pausa. Vazio (padrão) deixa a dificuldade para o jogador.

### Registro de logs

*o que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| ---------------------- | ------- | ------- | --------------------------------------------------------------------------------- |
| `logBlockedOres`       | booleano | `true`  | Registra a primeira vez que cada mod e tipo de minério é recusado |
| `logBlockedBiomes`     | booleano | `true`  | Registra uma contagem por mod de quais biomas foram bloqueados |
| `logBlockedGenerators` | booleano | `true`  | Registra a primeira vez que cada mod e gerador é bloqueado |
| `logBlockedRecipes`    | booleano | `true`  | Registra uma contagem por mod do que foi bloqueado |
| `logBlockReplacements` | booleano | `true`  | Registra a primeira vez que cada substituição é feita, e um total quando um mundo se atualiza |

`logBlockedOres`, `logBlockedBiomes`, `logBlockedRecipes` e `logBlockReplacements` registram cada um a primeira vez que algo é recusado, para que você veja o que uma regra de bloqueio realmente pegou em vez de adivinhar pelo que está faltando. São a primeira coisa a ligar quando uma regra parece não fazer nada, ou fazer demais.

### Receitas

*o que cada grupo faz*

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

| Configuração | Tipo | Padrão | O que faz |
| --------------------- | ---------------------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `blockRecipes`        | booleano                     | `false`     | Remove toda receita de criação exceto as dos mods em `recipeWhitelist`. Nada é isento por padrão, então liste o namespace do seu próprio pacote para manter as receitas dele. Adições do CraftTweaker e do GroovyScript sempre sobrevivem |
| `recipeWhitelist`     | lista de ids de mods         | `minecraft` | Os mods cujas receitas de criação sobrevivem |
| `blockedRecipeMods`   | lista de ids de mods         | nenhum      | Mods cujas receitas de criação são removidas por completo, diga o que disser a whitelist |
| `blockFurnaceRecipes` | booleano                     | `false`     | O mesmo para receitas de fornalha, sendo o mod lido a partir do item produzido |
| `furnaceWhitelist`    | lista de ids de mods         | `minecraft` | Os mods cujas receitas de fornalha sobrevivem |
| `blockedFurnaceMods`  | lista de ids de mods         | nenhum      | Mods cujas receitas de fornalha são removidas por completo |
| `recipeMatch`         | `recipe`, `output` ou `both` | `recipe`    | De onde o id do mod é lido quando as receitas de criação são bloqueadas: o nome da própria receita, o item que ela faz, ou qualquer um dos dois, o que bloqueia quando qualquer um corresponde e poupa quando qualquer um está na whitelist |

`blockRecipes` e `blockFurnaceRecipes` removem tudo exceto os mods de suas whitelists. Nada é isento por padrão, então liste o namespace do seu próprio pacote para manter as receitas dele. Adições do CraftTweaker e do GroovyScript sempre sobrevivem, diga o que disser a whitelist. As whitelists são `recipeWhitelist` e `furnaceWhitelist`; `blockedRecipeMods` e `blockedFurnaceMods` seguem o caminho oposto e removem as receitas de um mod nomeado, diga o que disser a whitelist. `recipeMatch` decide de onde o id do mod é lido quando as receitas de criação são bloqueadas: `recipe`, o padrão, usa o nome da própria receita, `output` usa o item que ela faz, e `both` bloqueia quando qualquer um corresponde e poupa quando qualquer um está na whitelist.

---

# Outros mods

## Universal Tweaks

*outros mods*

O Universal Tweaks se sobrepõe a vários dos ajustes vanilla deste mod. Onde se sobrepõem, este mod recua (registrado a cada vez, nomeando o que foi pulado) em vez de ter dois mods editando o mesmo método.

| O que se sobrepõe | Quando este mod dá lugar |
| -------------------- | ----------------------------------------- |
| `promptLeafDecay`    | O Universal Tweaks está com `Fast Leaf Decay` ligado |
| `lenientPaths`       | O Universal Tweaks está com `Lenient Paths` ligado |
| `cactusMaxHeight`    | O Universal Tweaks está instalado |
| `caneMaxHeight`      | O Universal Tweaks está instalado |
| Retorno do portal do Nether | O Universal Tweaks está instalado |

Os dois primeiros leem os próprios interruptores do Universal Tweaks em `config/Universal Tweaks - Tweaks.cfg`, então desligar um ali devolve essa tarefa a este mod. O par de alturas não tem tal interruptor para ler, apenas `Cactus Size` e `Sugar Cane Size`, então este mod recua sempre que o Universal Tweaks está presente e você define a altura lá.

**Retorno do portal do Nether**: este mod registra onde você entrou no Nether e o devolve ali, em vez da busca pelo portal mais próximo do vanilla. O Universal Tweaks tem seu próprio tratamento, então isso é pulado por completo quando ele está instalado.

**Nada disso toca um pacote.** Tudo acima diz respeito a cactos, cana, folhas, caminhos e portais do próprio Minecraft. Os blocos que seu pacote define carregam o próprio comportamento, e os portais de pacote em `portals/*.json` são um sistema à parte que o Universal Tweaks nunca vê.

## Mo' Villages

*outros mods*

O Mo' Villages adiciona biomas de vilas e troca os materiais das vilas — duas coisas que os pacotes também podem definir. Ao contrário das sobreposições com o Universal Tweaks, aqui o pacote fica com a última palavra.

| O que se sobrepõe | O que acontece |
| ------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureSpacing` para vilas   | O Mo' Villages define seu próprio espaçamento a partir de `villageDistance` depois que este mod já pediu. Se um pacote nomeou um espaçamento, este mod devolve o seu número e avisa uma vez no log |
| `villageBlocks`                 | O Mo' Villages troca os materiais das vilas por bioma e marca a troca como final. O mapa de um pacote é aplicado depois disso, então o pacote vence |
| `structureBiomes` para vilas    | O Mo' Villages adiciona seus biomas à lista do próprio jogo. A whitelist de um pacote ainda decide o que sobrevive |

Nada aqui precisa ser ligado. Se um pacote não indica espaçamento nem mapa de blocos, o Mo' Villages é deixado em paz para fazer como quiser.

Duas coisas que vale saber quando ambos estão instalados. O Mo' Villages também define `minTownSeparation`, que não faz absolutamente nada no 1.12: o campo é escrito uma vez e nunca lido, nem pelo jogo nem por este mod. E os blocos de uma vila são decididos por bioma pelo Mo' Villages antes de `villageBlocks` rodar, então mapear tanto o bloco original quanto o bloco para o qual o Mo' Villages o trocou pega uma vila de qualquer forma, `minecraft:cobblestone=...` e `minecraft:brick_block=...` juntos.

## CoFH World

*outros mods*

Mods que exigem o CoFH World carregam sem ele, pois a exigência é removida automaticamente, exceto os mods que realmente chamam sua API e travariam.

A geração própria deles então não acontece, porque o CoFH World é quem lê seus `assets/<modid>/world/*.json`. Espera-se que um pacote cubra isso.

Na falta disso, `readCofhWorldFiles` lê esses arquivos direto dos jars dos mods e os gera por meio deste mod. Vem desligado por padrão, e recua quando o CoFH World de verdade está instalado, que então gera normalmente. Todo gerador e distribuição do CoFH que produz algo é convertido, mapeado nas formas e distribuições acima. As formas são a geometria própria deste mod, então um lago ou um pináculo não ficará idêntico. Listas ponderadas de estruturas, tabelas de rotação e espelhamento, listas de blocos ignorados e o afunilamento das estalagmites passam todos. O afunilamento é combinado por forma e não por fórmula, então o contorno de um pináculo fica próximo, mas não idêntico.

Traduzir os arquivos para um pacote é o caminho suportado, e a única forma de mudar o que eles geram.

## Lost Cities

*outros mods*

O Lost Cities substitui o gerador do overworld por um próprio, então qualquer coisa ligada ao gerador comum deixaria de funcionar em seus mundos. Uma compatibilidade que só é carregada quando o Lost Cities está instalado leva três coisas adiante:

- `generatorOptions` molda o terreno entre e sob as cidades. O Lost Cities lê apenas as configurações de ruído, então nível do solo, nível da água, cavernas, lagos e os interruptores de estruturas saem de seus próprios perfis, e `seaLevel` não faz nada em seus mundos; o resumo no log diz isso. `terrainWorldTypes` o controla como qualquer outro tipo, comparado como `lostcities`.
- Um mundo vazio funciona, inclusive o que uma lista de biomas totalmente bloqueada traz. Cidades e terreno somem, e a plataforma e o spawn se comportam como em qualquer lugar.
- O `stoneBlock` de um bioma de pacote substitui a pedra sob ele, em todo tipo de paisagem que o Lost Cities tem: normal, flutuante, espaço e caverna.

As cidades em si não cabem a este mod mudar. Quão grandes e comuns são, de que são feitos os edifícios, nível do solo e da água, tudo isso fica nos próprios arquivos de perfil do Lost Cities em `config/lostcities`, e o JSON de seus edifícios passa pela sua própria configuração `assets` no mesmo lugar. Um pacote que distribui um mundo Lost Cities distribui esses arquivos junto, da mesma forma que distribui a configuração de qualquer outro mod.

`worldType` definido como `lostcities` faz de todo mundo novo um mundo Lost Cities, da mesma forma que o faz `biomesop` ou `realistic`. Forçar um tipo descarta as configurações que o mundo traria, então o mundo cai no perfil padrão do Lost Cities, e `defaultProfile` em `config/lostcities/general.cfg` nomeia qual é. No sentido contrário, um pacote que força um tipo diferente tira o Lost Cities de um jogador que o escolheu, então um pacote que pretende deixar essa escolha aberta adiciona `lostcities` a `worldTypeExceptions`.

Todo o resto nunca passou pelo gerador e funciona como em qualquer lugar: geração de mundo do pacote, bloqueio de minérios e biomas, espaçamento de estruturas e geradores de monstros, bedrock plana, retrogen, pré-geração, e suas duas tabelas de saque de baús sobrescrevem e injetam como quaisquer outras.

## Integração com Blast Plaster

*outros mods*

`<namespace>/blastplaster/*.json`

O nome do arquivo é de sua escolha, só a pasta é lida, e vários arquivos se empilham.

O Blast Plaster (uma dependência deste mod) cuida do comportamento pós-explosão: curar crateras bloco a bloco, derrubada de árvores consciente da estrutura, controle de drops. Sozinho, ele lê uma única configuração global. Conduzido por um pacote, responde **por dimensão**, e o pacote entrega a decisão em vez de pedir que os jogadores editem uma configuração. A derrubada de árvores das vilas também reutiliza sua geometria de árvores, e é por isso que uma árvore sobre uma estrada nova cai inteira. Sem arquivos de pacote, o Blast Plaster se comporta exatamente como se estivesse instalado sozinho.

As chaves escritas no topo do arquivo valem em toda parte; um bloco `dimensions` as sobrescreve para uma dimensão por id. Tudo o que um pacote nunca nomeia mantém o que a própria configuração do Blast Plaster diz, então um pacote define o punhado que lhe interessa e deixa o resto em paz.

Todas as chaves, mostradas de uma vez. Um arquivo real escreve apenas as de que precisa.

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

`explosionMode` é o interruptor principal: `HEAL` restaura a cratera com o tempo, `EJECT_DROPS` deixa o buraco e solta cerca de um terço dos blocos (comportamento vanilla), `VISUAL_TOSS` deixa o buraco e não solta nada. Quando conduzido por um pacote, o padrão é `EJECT_DROPS` (e não o `HEAL` do Blast Plaster), então uma instalação sem configuração se comporta como o vanilla.

| Chave | Valor | O que faz |
| ----------------------------------------------------------------------------------------------------------- | ------------------------------------ | -------------------------------------------------------------------------------------------------------- |
| `explosionMode`                                                                                             | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | O que acontece depois do estrondo |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll`                                                 | true ou false                        | Quais explosões são tratadas |
| `processPlayerIgnitedTNT`                                                                                   | true ou false                        | Se o TNT que um jogador acendeu é tratado junto com o resto |
| `customEntitiesToHeal`                                                                                      | lista de nomes de entidades          | Explosões de outros mods, nomeadas como `modid:entity` |
| `healFullTrees`                                                                                             | true ou false                        | Uma árvore atingida por uma explosão é levada ou restaurada inteira, em vez de cortada ao meio |
| `maxTreeSize`                                                                                               | número                               | O máximo de blocos que uma árvore pode reivindicar antes de ser deixada em paz |
| `minimumTicksBeforeHeal`, `randomTickVar`                                                                   | números                              | Quanto tempo antes de a restauração começar, e quão irregular é o seu ritmo |
| `overrideBlocks`                                                                                            | true ou false                        | Se a restauração sobrescreve o que foi construído desde então no buraco |
| `enableFakeTossedBlocks`                                                                                    | true ou false                        | Os destroços que voam da explosão |
| `enableExplosionFlash`                                                                                      | true ou false                        | O clarão brilhante no momento da explosão |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | números                              | Quanto dura o clarão, quão forte ele brilha, quantas partículas lança e quantas vezes pulsa |
| `enableExplosionSmoke`                                                                                      | true ou false                        | A coluna de fumaça depois |
| `explosionSmokeDuration`, `explosionSmokeParticleCount`                                                     | números                              | Quanto tempo a fumaça persiste e quão densa fica |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks`                                                           | true ou false                        | O que o próprio TNT de um jogador deixa para trás |
| `enableDropSuppression`, `dtSpecialDrops`                                                                   | true ou false                        | Drops dentro de uma explosão, e os drops próprios do Dynamic Trees |
| `preventMobDrops`                                                                                           | true ou false                        | Se mobs mortos por uma explosão ainda soltam itens |
| `blockConversions`                                                                                          | lista de regras                      | No que um bloco destruído se transforma em vez de voltar como era, de modo que uma construção se desgasta um passo por explosão |

`blockConversions` decide no que um bloco destruído se transforma em vez de voltar como era. Uma regra se lê `<source>=<result>[@chance]`: a origem é um id de bloco, um id de bloco com meta (`minecraft:log:1`), ou um nome do dicionário de minérios com um `#` na frente; o resultado é um id de bloco, um id de bloco com meta, ou `nothing` para deixar o espaço vazio; a chance vai de 0.0 a 1.0 e o padrão é 1.0. A primeira regra que corresponde vence, então as regras específicas vão acima das amplas, e um bloco que já é o resultado de alguma regra nunca é convertido de novo — uma parede perde um passo por explosão em vez de se desgastar até não sobrar nada.

**Aparência totalmente vanilla:** `EJECT_DROPS` mais `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` e `playerTNTAlwaysDrops`, todos desligados. Cada chave pode ser definida por dimensão.

**Clientes vanilla** não veem nada de incomum. O clarão é o único recurso que coloca um bloco, então com `vanillaClients` definido ele é forçado a desligar; todo o resto são partículas e itens que um cliente comum entende.

Não são chaves de pacote: o log de depuração do Blast Plaster e o seu pareamento tronco-folha (a identificação de árvores deve ter uma única resposta em todo o jogo). Ambos permanecem na própria configuração do Blast Plaster.

## Mods de túmulos

*outros mods*

Nenhuma configuração necessária. Os itens de `player_loot` se juntam aos drops comuns da morte antes que qualquer mod de túmulo os leia, então acabam no túmulo junto com o inventário — funciona com Gravestone, GraveStone Mod, Corail Tombstone e qualquer outro que leia a lista de drops da morte. Por entrada, `dropLoose` contorna a lista de drops, de modo que os itens ficam no chão para quem matou em vez de irem para o túmulo. Chaves e a ressalva de `dropLoose`: [Saque de jogadores](#saque-de-jogadores).

---

# Referência

## Listas de valores

*referência*

Estes são os nomes que o analisador aceita onde quer que as tabelas acima digam "um dos materiais" e assim por diante. Tudo o que não for reconhecido é registrado no log e substituído pelo padrão.

### Nomes aceitos

*listas de valores*

**Materiais de bloco.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`.

**Tipos de som.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Cores de mapa.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Camadas de renderização.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Deixado vazio, o bloco escolhe uma que combine com seu tipo.

**Raridades.** `common`, `uncommon`, `rare`, `epic`.

**Partículas de tocha.** `none`, `flame`, `colored`. `colored` usa `particleColor`.

**Classes de ferramenta.** `pickaxe`, `axe`, `shovel`, `sword`.

**Espaços de armadura.** `head` ou `helmet`, `chest` ou `chestplate`, `legs` ou `leggings`, `feet` ou `boots`.

**Tons.** `biome`, `none`, ou uma cor hexadecimal de seis dígitos. As cores em qualquer lugar de uma definição são hexadecimais, com ou sem um `#` na frente.

**Comportamentos** para `behavesAs`. `till`, `path`, `bush`, `animals`.

**Estruturas** para um template de mundo, e para as listas do próprio grupo `structures`. `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges`, `endcities`, `caves`, `ravines`, e `reccomplex`, que desliga tudo o que o Recurrent Complex gera por conta própria — suas estruturas naturais e seus substitutos de decoração — deixando intocado o que já existe no mundo. Mais oito nomeiam o que a etapa de povoamento coloca e não um gerador de estruturas: `dungeons`, `waterlakes`, `lavalakes`, `netherlava`, `fire`, `glowstone`, `ice` e `animals`.

**Tipos de criatura** para spawns e taxas de bioma. `creature`, `monster`, `ambient`, `water_creature`.

**Papéis** para os `roles` de um template de mundo. `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains`, `water`. Cada um nomeia um bioma que cumpre esse papel depois que o bloqueio removeu os que o teriam cumprido.

**Tipos de minério** para `oreTypes`. `COAL`, `IRON`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `SILVERFISH`, `CUSTOM`.

### Configurações do mundo

*listas de valores*

As chaves de `terrain` abaixo, reunidas no bloco `settings` de um template de mundo:

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

| Configuração | Tipo | Padrão | O que faz |
| ---------------- | --------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `worldName`      | string                | vazio   | Preenche previamente a caixa de nome da tela de criação de mundo, e a pasta do save decorre dela. Só preenche a caixa enquanto ela ainda contém o padrão do jogo, e, ao contrário da seed e do modo de jogo, não é reaplicado depois |
| `worldSpawn`     | `x,z` ou `x,y,z`      | vazio   | Onde todo mundo novo surge, aplicado apenas na criação. Sem y, usa-se a superfície no nível do solo do tipo de mundo |
| `worldBorder`    | inteiro, blocos       | `0`     | O diâmetro da borda dado a todo mundo novo, o valor que `/worldborder set` aceita. `0` deixa a borda em paz |
| `worldTime`      | inteiro, ticks        | `-1`    | A hora do dia em que todo mundo novo começa. `-1` deixa em paz |
| `weatherCeiling` | lista de `dimension=y` | nenhum | O maior y que a chuva e a neve alcançam. Um número sozinho cobre todas as dimensões |
| `cloudHeight`    | lista de `dimension=y` | nenhum | O y em que as nuvens são desenhadas. Um número sozinho cobre todas as dimensões, e vazio mantém a altura do próprio jogo |

**`worldName`** (grupo `terrain`) preenche previamente a caixa de nome da tela de criação de mundo; a pasta do save decorre dela como de costume. Só preenche a caixa enquanto ela ainda contém o padrão do jogo, então um nome digitado pelo jogador nunca é sobrescrito, e, ao contrário da seed e do modo de jogo, não é reaplicado depois — o que estiver na caixa na criação é o nome.

**`worldSpawn`** (grupo `terrain`): `x,z` ou `x,y,z`. Aplicado apenas na criação. Sem y, usa-se a superfície no nível do solo do tipo de mundo. Entradas não inteiras são reportadas e ignoradas. Relevante sobretudo no superplano: a busca de spawn do vanilla procura grama ao nível do mar, nunca a encontra em uma pilha de camadas e pode vagar por centenas de blocos — `worldSpawn` a fixa.

**`worldBorder`** (grupo `terrain`): diâmetro da borda em blocos, o valor que `/worldborder set` aceita. Aplicado na criação; `0` (padrão) deixa a borda em paz; ainda pode ser movida por comando depois. `worldBorderLimit` na configuração limita o que um pacote pode pedir — um pacote que pede mais é recusado e registrado no log, não limitado, de modo que um pacote não pode impor a um servidor uma borda com a qual o operador não concordou.

**`worldTime`** (grupo `terrain`): um valor em ticks como `/time set` aceita (`18000` meia-noite, `6000` meio-dia). Trava o relógio do overworld; tudo o que lê a hora do dia (spawn de mobs, dormir) vê o valor travado. `-1` (padrão) deixa o tempo correr. O análogo no overworld do `fixedTime` de uma dimensão customizada, e independente de `doDaylightCycle`.

**`cloudHeight`** (grupo `terrain`): o y em que as nuvens são desenhadas. Um número sozinho cobre todas as dimensões; linhas `dimension=y` (`0=384`) sobrescrevem por dimensão. É o que um pacote com construções altas define para que o horizonte fique sob as nuvens e não atravesse por elas, e em um mundo rubic é um y absoluto, então um teto elevado é o lugar para colocar as nuvens acima. Vazio (padrão) mantém a altura do próprio jogo, 128 no overworld, deslocada para cima com `terrainOffset` em um mundo rubic.

**`weatherCeiling`** (grupo `terrain`): o maior y que a chuva e a neve alcançam. Um número sozinho cobre todas as dimensões; linhas `dimension=y` (`0=128`) sobrescrevem por dimensão. Acima dele a chuva não cai, a neve não se acumula, caldeirões não enchem, raios não caem e nenhuma precipitação é desenhada; abaixo dele o clima não muda. Vazio (padrão) significa sem teto. O gelo depende da temperatura e não da precipitação, então ainda se forma acima da linha.

### Física do mundo

*listas de valores*

**Física do mundo** — quatro chaves de `terrain`, cada uma um multiplicador do vanilla (`1.0` = inalterado), cada uma aceitando um valor sozinho para todas as dimensões ou sobrescritas `dimension=value`:

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

| Configuração | Escala | Observações |
| ----------------------- | ----------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| `worldGravity`          | A aceleração de queda de jogadores, mobs, itens largados, blocos em queda, flechas, entidades arremessadas, TNT e esferas de experiência | `0.17` é parecido com o da Lua; os arcos de salto e os alcances de projéteis acompanham automaticamente |
| `worldFallDamage`       | Dano de queda | Uma dimensão de baixa gravidade geralmente quer isto ajustado junto |
| `worldJumpStrength`     | Velocidade do salto | Aplicado além da mudança de gravidade |
| `worldTerminalVelocity` | Velocidade máxima de queda, como parcela do limite vanilla | O voo com elytra não é tocado |

Todas as quatro vazias (padrão) mantêm a física vanilla. Nas dimensões do Galacticraft, a chave de gravidade escala a gravidade do próprio Galacticraft.

### Junções do mundo

*listas de valores*

**Junções do mundo** — empilham dimensões na vertical: ao sair de um mundo pelo piso ou pelo teto, a entidade chega à dimensão abaixo ou acima, nas mesmas coordenadas x e z.

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

| Configuração        | Valor                                                      | Padrão  | O que faz                                                                                                 |
| ------------------- | ---------------------------------------------------------- | ------- | --------------------------------------------------------------------------------------------------------- |
| `worldBelow`        | linhas `dimension=target`, ou um id puro para todas as dimensões | nenhum  | Dimensão em que se entra ao cair além do piso do mundo                                              |
| `worldAbove`        | o mesmo                                                    | nenhum  | Dimensão em que se entra ao subir além do topo gerado, ou seja, o teto do Nether, e não o seu limite de construção |
| `worldSeamEntities` | booleano                                                   | `true`  | Se itens, mobs e outras entidades atravessam, ou apenas os jogadores                                      |
| `worldSeamBedrock`  | booleano                                                   | `false` | Mantém a bedrock no limite da junção. Desligado, o limite não gera nenhuma, e a passagem pode ser escavada |

Com as duas listas vazias (o padrão), todos os mundos permanecem fechados. A camada de blocos mais externa de um mundo é a sua porta: entrar na camada inferior leva você para baixo, entrar na camada superior leva você de volta para cima. As chegadas ocorrem longe dela, três camadas para dentro ao descer e uma ao subir, para que nada volte direto. Ao descer, as camadas acima do ponto de chegada também são abertas até a porta, de modo que a entrada continue visível de baixo e sirva de caminho de volta.

Quebre um bloco numa camada de porta e o mundo do outro lado aparece através dele: o céu da dimensão abaixo surge sob o piso, e o céu da dimensão acima surge sobre o teto. Isso é desenhado apenas no cliente, dentro da distância de renderização, e não altera nada no mundo em si. O momento é preservado na travessia.

As travessias de um jogador são lembradas. Descer marca o buraco, e voltar a subir perto dele leva você ao lugar onde esse buraco o deixou da última vez, de modo que um poço usado com frequência sempre devolve você ao mesmo ponto conhecido, em vez de a um lugar novo. O primeiro retorno calcula a chegada: esse ponto, se houver chão sob ele; senão, o espaço de pé mais próximo, avançando para fora a partir da junção uma altura por vez; e, por fim, um nicho aberto na borda ao lado do buraco, já que um poço cavado reto para baixo ainda não tem saliência própria. Subir num lugar sem nenhum buraco seu por perto simplesmente cria uma nova chegada ali. Chegar de baixo sem nenhum ponto onde ficar de pé por perto recorre à superfície daquela coluna. Pés e cabeça são liberados se o ponto estiver dentro de rocha, quebrando esses blocos de forma adequada para que eles caiam, recipientes inclusos.

As cadeias se empilham dando a cada dimensão as suas próprias linhas, e cavaleiros e montarias atravessam separadamente.

Os portais se aplicam a jogadores. Um jogador que não desbloqueou o destino recebe a mensagem de recusa do portal e é recolocado no último chão em que esteve, ou numa saliência perto da junção; as junções não colocam blocos, então um poço trancado não pode ser explorado caindo por ele. Itens e mobs não têm portal próprio: com `worldSeamEntities` ligado eles atravessam independentemente de quem os perdeu, e desligado eles caem por um piso aberto e se perdem como em qualquer buraco. `worldSeamBedrock` sela o piso, e um pacote que mantém sua bedrock fornece a passagem por conta própria, geralmente com uma [substituição de propriedades](#substituições-de-propriedades) que dá a `minecraft:bedrock` uma `hardness` positiva. Os chunks gerados antes da junção mantêm a bedrock que já têm.

**Mundos Rubic** — `rubicWorld`, `worldMinHeight`, `worldMaxHeight`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist` e `terrainOffset` também são chaves de `terrain`: veja [Mundos Rubic](#mundos-rubic).

## Lista de pastas

*referência*

Todas as pastas, com o caminho completo e um link para a seção que as descreve, estão em [Onde os arquivos ficam](#onde-os-arquivos-ficam).

## Comandos

*referência*

### Seus próprios comandos

*comandos*

`/rdpl` roda na sua própria máquina e não exige permissões, porque tudo o que ele toca é seu. Um reload reexamina a pasta que você possui, reaplica as suas [substituições de propriedades](#substituições-de-propriedades) à sua própria cópia dos blocos e itens e atualiza os seus próprios recursos; ele não alcança nenhum servidor, então a cópia do servidor é recarregada com `/rdplserver reload`. No modo single player as duas são a mesma máquina, então `/rdpl reload` também recarrega as loot tables, os avanços e as funções do servidor integrado, como o reload do próprio vanilla. Funciona em qualquer servidor, tenha ele o mod ou não.

| Comando                                                                               | Nível        | O que faz                                                                                                                                                                                                        |
| ------------------------------------------------------------------------------------- | ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdpl list`                                                                          | nenhum       | Todos os pacotes carregados, a prioridade de cada um e o que contêm. Clique num pacote para procurar um arquivo nele                                                                                             |
| `/rdpl which <namespace:path>`                                                        | nenhum       | Qual pacote fornece um determinado arquivo e quais pacotes ele sobrepõe                                                                                                                                          |
| `/rdpl reload`                                                                        | nenhum       | Reexamina a pasta e recarrega tudo                                                                                                                                                                               |
| `/rdpl reload <group>`                                                                | nenhum       | Recarrega apenas um tipo: `textures`, `models`, `languages`, `sounds` ou `shaders`                                                                                                                               |
| `/rdpl unused`                                                                        | nenhum       | Arquivos nos seus pacotes que nada pediu até agora, geralmente um erro de digitação num caminho                                                                                                                  |
| `/rdpl config unused`                                                                 | nenhum       | Arquivos de opções em `rdploader/config` que nenhum pacote instalado define mais                                                                                                                                 |
| `/rdpl config prune`                                                                  | nenhum       | Exclui esses arquivos                                                                                                                                                                                            |
| `/rdpl pixelmap <namespace:path>`                                                     | nenhum       | No que um [mapa de pixels](#texturas-escritas-como-mapas-de-pixels) resultou, caractere por caractere                                                                                                            |
| `/rdpl biome list`                                                                    | nenhum       | Todos os biomas que podem gerar e o id de cada um                                                                                                                                                                |
| `/rdpl biome here`                                                                    | nenhum       | O bioma em que você está                                                                                                                                                                                         |
| `/rdpl biome find <name>`                                                             | o do servidor | Encaminhado. Repassado a `/rdplserver biome find`, o único lado que conhece a seed                                                                                                                              |
| `/rdpl team`                                                                          | nenhum       | As equipes que um pacote formou, cada uma na sua cor, em qual você está e quem lidera cada uma                                                                                                                   |
| `/rdpl team join [name]`                                                              | nenhum       | Entra numa equipe. Só são oferecidas as equipes que um pacote deixa abertas; uma equipe de mobs não é uma em que se possa entrar. Sem o nome, você é colocado na equipe com menos jogadores entre as que aceitam jogadores por `balance` |
| `/rdpl team leave`                                                                    | nenhum       | Sai da equipe em que você está                                                                                                                                                                                   |
| `/rdpl team vote <player>`                                                            | nenhum       | Vota em quem lidera a sua equipe, onde o pacote escolhe o líder por votação. Um empate deixa a equipe sem líder                                                                                                  |
| `/rdpl team claim`                                                                    | nenhum       | Assume a liderança da sua equipe, onde o pacote permite reivindicá-la e ninguém na equipe a detém                                                                                                                |
| `/rdpl round start`                                                                   | nenhum       | Inicia a rodada, onde o pacote a mantém num lobby (`opens.by`). Para o líder de uma equipe ou um operador                                                                                                        |
| `/rdpl round reset`                                                                   | nenhum       | Reinicia a rodada em andamento, ou convoca uma votação para isso, conforme o `reset` do pacote permitir. Para o líder de uma equipe, um jogador de uma equipe que o pacote deixa convocar votações, ou um operador |
| `/rdpl round vote yes`, `no`                                                          | nenhum       | Vota numa votação em andamento para reiniciar a rodada. Para um jogador que esteja numa equipe                                                                                                                   |
| `/rdpl oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein`, `game` | o do servidor | Encaminhado. Repassado palavra por palavra a `/rdplserver`, que decide; veja a tabela abaixo                                                                                                                    |

**Quais subcomandos do servidor são encaminhados, e por que os demais não são.** Um subcomando do servidor recebe repasse exatamente quando o cliente não tem significado próprio para aquele nome: `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein`, `team` e `game` só podem significar o do servidor, então `/rdpl` os repassa. Os seis que o cliente também tem, `reload`, `list`, `which`, `unused`, `config` e `biome`, mantêm o seu próprio sentido de seus pacotes e do seu cliente, e encaminhá-los tiraria isso deles. `biome find` é a única parte de um nome compartilhado que pertence ao servidor de qualquer forma, já que só o servidor conhece a seed do mundo, então só essa forma é repassada, enquanto `biome list` e `biome here` ficam com você. Isso também resolve a permissão: quem decide é a própria verificação de operador do servidor, e um cliente não pode burlá-la nem receber uma resposta forjada.

**`/rdpl` também alcança o comando do servidor.** Tudo o que `/rdpl` não trata por conta própria, `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto`, `vein`, `team` e `game`, é repassado direto a `/rdplserver` e oferecido no preenchimento com Tab, de modo que há um único comando para digitar no single player. Ele é repassado palavra por palavra e o servidor decide como sempre decidiria, permissões inclusas, então nada se abre ao digitar o nome mais curto. Os subcomandos que ambos têm, `reload`, `list`, `which`, `unused`, `biome` e `config`, ficam com `/rdpl` e significam os pacotes do próprio cliente. `biome find` é a única exceção dentro de um nome compartilhado: só o servidor conhece a seed do mundo, então essa forma é repassada, enquanto `biome list` e `biome here` respondem a partir do seu próprio cliente.

**Edição no dia a dia:** `/rdpl reload textures` é muito mais rápido que F3+T num modpack grande. F3+T continua funcionando e recarrega tudo. Use `/rdpl reload` simples quando você *adicionar* ou *excluir* um arquivo, já que isso muda o que a pasta contém.

### Comandos de servidor

*comandos*

Num servidor dedicado, `/rdplserver` faz o mesmo para a cópia da pasta do próprio servidor. A coluna Nível é o nível de permissão de que um remetente precisa: `3` é operador, `2` admite também command blocks, `0` é qualquer jogador e `4` fica acima de operador e não alcança ninguém. Apenas `intro`, `team`, `card` e as três formas de `goto` estão abertas abaixo de operador, e `goto` é a que um pacote pode mover.

#### Pacotes e arquivos

*comandos de servidor*

| Comando                              | Nível | O que faz                                                                    |
| ------------------------------------ | ----- | ---------------------------------------------------------------------------- |
| `/rdplserver reload`                 | 3     | Reexamina a pasta do servidor e recarrega tudo                               |
| `/rdplserver list`                   | 3     | Todos os pacotes que o servidor carregou, a prioridade de cada um e o que contêm |
| `/rdplserver which <namespace:path>` | 3     | Qual pacote fornece um determinado arquivo e quais pacotes ele sobrepõe      |
| `/rdplserver unused`                 | 3     | Arquivos nos pacotes do servidor que nada pediu                              |
| `/rdplserver config unused`          | 3     | Arquivos de opções em `rdploader/config` que nenhum pacote instalado define mais |
| `/rdplserver config prune`           | 3     | Exclui esses arquivos                                                        |

#### Mundo e geração

*comandos de servidor*

| Comando                             | Nível | O que faz                                                                                                                                                                                                                                    |
| ----------------------------------- | ----- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver oregen`                | 3     | Totais acumulados da geração de minérios que foi bloqueada, por mod e tipo                                                                                                                                                                   |
| `/rdplserver generators`            | 3     | Totais acumulados dos geradores de mundo que foram bloqueados, por mod e tipo                                                                                                                                                                |
| `/rdplserver biome`                 | 3     | Todos os biomas que podem gerar no servidor                                                                                                                                                                                                  |
| `/rdplserver biome list [all]`      | 3     | O mesmo com o id de cada bioma, e `all` inclui aqueles que nada pode gerar                                                                                                                                                                   |
| `/rdplserver biome here`            | 3     | O bioma em que você está. O console não está em lugar nenhum, então dali ele pede um jogador                                                                                                                                                 |
| `/rdplserver biome here <player>`   | 3     | O bioma em que esse jogador está, que é a forma que o console e um script querem                                                                                                                                                             |
| `/rdplserver biome find <name>`     | 3     | O lugar mais próximo onde um bioma gera, sem gerar chunks para procurar                                                                                                                                                                      |
| `/rdplserver dimensions`            | 3     | Todas as dimensões, incluindo as que os pacotes adicionaram                                                                                                                                                                                  |
| `/rdplserver vein <entry> [radius]` | 3     | Onde uma entrada de worldgen do tipo `vein` tem as suas veias semeadas dentro desse número de chunks (padrão 8) do lugar onde é executado, as mais próximas primeiro, quer esses chunks já existam ou não. `/rdpl vein` encaminha para ele |

#### Comandos de portais

*comandos de servidor*

| Comando                                   | Nível | O que faz                              |
| ----------------------------------------- | ----- | -------------------------------------- |
| `/rdplserver gate list`                   | 3     | Todos os portais e se estão abertos    |
| `/rdplserver gate check <player>`         | 3     | Quais portais um jogador atravessou    |
| `/rdplserver gate grant <player> <gate>`  | 3     | Abre um portal para um jogador         |
| `/rdplserver gate revoke <player> <gate>` | 3     | Fecha um novamente                     |

#### Comandos de pré-geração

*comandos de servidor*

| Comando                               | Nível | O que faz                                                                                                |
| ------------------------------------- | ----- | -------------------------------------------------------------------------------------------------------- |
| `/rdplserver pregen <radius>`         | 3     | Gera todos os chunks dentro desse número de chunks do lugar onde é executado. Veja [Pré-geração](#pré-geração) |
| `/rdplserver pregen <radius> relight` | 3     | Executa apenas a passada de iluminação sobre terreno que já existe                                       |
| `/rdplserver pregen status`           | 3     | Em que ponto uma execução está                                                                           |
| `/rdplserver pregen stop`             | 3     | Encerra a execução                                                                                       |

#### Jogadores, equipes e rodadas

*comandos de servidor*

| Comando                                                                                  | Nível | O que faz                                                                                                                                                                                                                                                                                                                  |
| ---------------------------------------------------------------------------------------- | ----- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver intro`                                                                      | 0     | Deixa a introdução do mundo tocar de novo na sua próxima entrada. Qualquer jogador pode executá-lo, e ele só limpa a do próprio jogador                                                                                                                                                                                    |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0     | O mesmo que as formas de `/rdpl team` acima, que são repassadas a estas                                                                                                                                                                                                                                                    |
| `/rdplserver round start`                                                                | 0     | O mesmo que `/rdpl round start`, que é repassado a ele                                                                                                                                                                                                                                                                     |
| `/rdplserver round reset`, `round vote yes`, `round vote no`                             | 0     | O mesmo que as formas de `/rdpl round` acima, que são repassadas a estas                                                                                                                                                                                                                                                   |
| `/rdplserver card <rule> [players]`                                                      | 2     | Mostra uma [regra de carta](#cartas) aos jogadores indicados, ou a você mesmo, pelo id ou nome de arquivo. `when`, `repeat` e `cooldown` são ignorados                                                                                                                                                                     |
| `/rdplserver reset`                                                                      | 3     | Devolve o mapa ao estado em que o fim de uma rodada o deixa: todos ficam retidos, as entidades são varridas, as pontuações zeradas, `resetRuns` executado, os jogadores levados a `resetSendsTo` e liberados, e uma rodada se abre com a contagem inicial, como as configurações de reset em [Pré-geração](#pré-geração) descrevem. Não é repassado a partir de `/rdpl` |

#### Indo a lugares

*comandos de servidor*

| Comando                             | Nível                | O que faz                                                                                                                  |
| ----------------------------------- | -------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver goto <structure>`      | `gotoLevel`, `3`     | Leva você à mais próxima que ninguém visitou ainda, procurando sem gerar o terreno pelo caminho                            |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Leva você adiante à mais próxima a que você ainda não foi levado nesta sessão, tenha ela sido visitada antes ou não        |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Leva você à anterior a ela, voltando pelos lugares para onde esta sessão enviou você                                       |

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

**Abrindo o `goto`.** Toda parte de `/rdplserver` exige um operador, nível 3, exceto `intro` e `team`, que são comandos do próprio jogador e sempre de nível 0, e `card`, que é de nível 2 para que um command block possa mostrar uma carta, e `game`, cujas partes têm [níveis próprios](#quem-pode-usar-game). As três formas de `goto` são a única coisa que um pacote decide: cada uma tem um nível de permissão próprio que um pacote ou a configuração pode reduzir, separadamente das outras duas e do restante do comando.

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

| Configuração      | O que controla                      |
| ----------------- | ----------------------------------- |
| `gotoLevel`       | `goto <structure>`                  |
| `gotoNextLevel`   | `goto <structure> next`             |
| `gotoBackLevel`   | `goto <structure> back`             |
| `gotoPlaceLevels` | Um lugar nomeado, nas três formas   |

O valor é o nível de permissão de que um remetente precisa. `3` (operador) é o padrão. `2` admite também command blocks, de modo que um pacote possa pôr um salto num botão ou numa placa de pressão sem expor o restante de `/rdplserver`. `0` o abre a qualquer jogador. As três configurações são independentes: por exemplo, `next` aberto a command blocks para um passeio por vilas enquanto `back` continua só para operadores.

Como `intro` está aberto a todos, qualquer jogador alcança o próprio `/rdplserver`, então todos os outros subcomandos verificam por conta própria se há um operador e recusam com uma mensagem. O preenchimento com Tab acompanha: um não operador recebe `intro`, `team` onde um pacote forma uma equipe e também `goto` quando um nível permite que ele o use.

`gotoPlaceLevels` substitui as três configurações para lugares individuais, como entradas `name=level`, como no exemplo acima. O nome é o que você digitaria depois de `goto`: um do vanilla, como `Village` ou `Mansion`, ou um nome registrado com `locateAs` numa entrada `imprint`. A correspondência ignora maiúsculas e minúsculas. Um nível `4` fica acima de operador e fecha esse lugar para todos — a maneira de esconder um lugar enquanto o resto de `goto` está aberto.

Uma entrada define um nível para as três formas daquele lugar. Um lugar não listado recorre às três configurações acima, e um nome não registrado nunca corresponde.

O preenchimento com Tab segue as mesmas regras, então depois de `goto` o remetente só recebe os lugares para onde ele realmente pode ser levado.

Elas ficam no grupo `commands`, então `control.commands` na configuração decide se um pacote pode defini-las, e `off` ali mantém tudo no nível de operador, não importa o que um pacote peça.

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

- CraftTweaker e GroovyScript rodam depois do RDPL, então as alterações deles ainda prevalecem.
- As receitas só carregam na inicialização, então mudanças em receitas exigem uma reinicialização, e não um reload.
- Funções salvas na própria pasta de dados de um mundo ainda prevalecem sobre uma função de um pacote, e o mesmo vale para os avanços desse mundo.
- Uma estrutura que já foi gerada permanece carregada até você sair do mundo.
- A diferença entre maiúsculas e minúsculas nos nomes de arquivo importa. Se a capitalização do seu arquivo não corresponder ao que o jogo pediu, o RDPL ainda o carrega, mas avisa você, porque no Linux ele não seria encontrado de forma alguma.
- Coloque um `pack.png` em `rdploader` para dar um ícone ao pacote. Sem ele, é mostrado o ícone do RDPL.
- A pasta pode ser movida ou renomeada com a opção `rootDirectory` em `config/mct_resourcedatapackloader_mixin.cfg`. Um caminho absoluto também funciona, e é preciso reiniciar.
- Blockstates que citam um modelo vanilla puro herdam também as texturas do vanilla. Modelos pai como `cube_all` e `cross` pegam suas texturas do blockstate e funcionam bem.
- `forge_marker: 1` não oferece suporte a multipart, então os blockstates de vinhas precisam ser multipart vanilla simples, com as texturas embutidas no modelo.
- A tela de carregamento do Forge é desenhada em cores escuras, com o logotipo deste mod no lugar do do Forge. `darkSplash` na categoria `client` da configuração restaura as cores do próprio Forge; cores definidas à mão em `config/splash.properties` são deixadas como estão de qualquer forma, e é preciso reiniciar.

## Quando algo não funciona

*referência*

**Verifique `logs/rdpl.log` primeiro.** Tudo o que o RDPL faz vai para lá, e não para o log principal. Avanços, loot tables, receitas, funções, estruturas e cada peça de conteúdo são registrados com o pacote de onde vieram, e qualquer coisa malformada é registrada com o motivo.

**Texturas e outros recursos são diferentes.** São pedidos com frequência demais para serem registrados um a um, então `/rdpl unused` lista os arquivos nos seus pacotes que nada pediu. Execute-o depois que o jogo terminar de carregar. Um arquivo com o caminho certo sempre é pedido, então qualquer coisa listada geralmente é um erro de digitação, mas lembre que alguns arquivos só carregam quando são necessários, como idiomas diferentes do que você joga.

**Um zip sem um diretório `assets` dentro dele é ignorado,** e o mesmo vale para qualquer pasta em `rdploader`, e o log informa isso.

**`/rdpl which minecraft:textures/blocks/stone.png`** diz exatamente qual pacote está fornecendo um arquivo e o que ele está sobrepondo.

## Bônus: ajustes do vanilla

*referência*

Pequenas mudanças no comportamento do vanilla, cada uma ativada na categoria de configuração `tweaks`.

| Opção                  | Padrão  | O que faz                                                                                       |
| ---------------------- | ------- | ----------------------------------------------------------------------------------------------- |
| `promptLeafDecay`      | ligado  | Folhas que perdem a sua árvore se decompõem em um segundo, em vez de esperar por random ticks   |
| `lenientPaths`         | ligado  | Caminhos de grama podem ser feitos sob um bloco e permanecem quando um é colocado acima         |
| `unbreakableSpawners`  | desligado | Spawners de mobs não podem ser minerados nem explodidos                                       |
| `modernChestPlacement` | ligado  | Os baús se unem como a partir da 1.13                                                           |

Mais três ficam na categoria `content`, e não em `tweaks`:

| Opção             | Padrão  | O que faz                                                                                             |
| ----------------- | ------- | ----------------------------------------------------------------------------------------------------- |
| `cactusMaxHeight` | `3`     | Qual altura o cacto vanilla atinge                                                                    |
| `caneMaxHeight`   | `3`     | Qual altura a cana-de-açúcar vanilla atinge                                                           |
| `shovelPaths`     | ligado  | Uma pá transforma blocos marcados com `behavesAs` path em um caminho, e agachar reverte um           |

**Estes cedem lugar ao Universal Tweaks**, que altera os mesmos blocos do vanilla. Veja [Universal Tweaks](#universal-tweaks) para saber exatamente quando.

**Nada disto alcança um pacote.** Estas opções só mudam o próprio cacto, a cana, as folhas e os caminhos do Minecraft. Um bloco que o seu pacote define com `"type": "cane"` traz a sua própria seção `growth` e cresce até a altura que você definiu, seja o que for que mais esteja instalado. `lenientPaths` também remove a mesma restrição dos blocos de pacote que usam `behavesAs`, o que o Universal Tweaks não toca, então essa metade permanece ativa de qualquer forma.

### Spawners inquebráveis

*bônus: ajustes do vanilla*

`unbreakableSpawners` dá ao bloco de spawner de mobs os números da bedrock, uma dureza inquebrável e uma resistência a explosões que nada supera. Um jogador não pode minerar um, por melhor que seja a picareta, e nem creepers, TNT ou uma entidade de pacote que `explodes` conseguem destruir um. O modo criativo ainda os remove, exatamente como ainda remove a bedrock, de modo que o autor de um pacote nunca fique trancado fora da própria construção. É preciso reiniciar, já que os valores são definidos uma única vez quando o jogo termina de carregar.

**É o bloco, não o spawner.** Não existe um interruptor por spawner. A opção altera o próprio `minecraft:mob_spawner`, então alcança todos os spawners do mundo de uma vez: as quatro estruturas vanilla que colocam um, qualquer um que um mod coloque e qualquer um que os seus próprios pacotes coloquem.

Este último caso é a resposta para uma estrutura personalizada. Um spawner dentro de um dos seus templates `.nbt`, colocado por uma entrada `imprint`, é um bloco de spawner de mobs comum com a sua própria tile entity, então fica coberto no instante em que a opção é ligada. Construa a estrutura com um spawner dentro da maneira usual, defina o que ele gera nos dados de tile entity do template, ligue `unbreakableSpawners`, e o da sua masmorra fica tão inquebrável quanto o do vanilla. Nada vai no pacote para isso, e não há como proteger apenas os seus deixando os demais do mundo quebráveis.

### Posicionamento de baús

*bônus: ajustes do vanilla*

`modernChestPlacement` coloca baús e baús armadilhados como a 1.13 e posteriores fazem.

- Um baú se une a um baú simples diretamente à sua esquerda ou direita, e somente quando ambos estão voltados para o mesmo lado. Um baú à frente ou atrás de outro nunca se une a ele.
- Agachar mantém o novo baú simples, a menos que se clique no lado de um baú simples: então ele se une a esse baú e se vira para o mesmo lado.
- Um baú pode ficar ao lado de um baú duplo, onde permanece simples, de modo que uma fileira de baús ao longo de uma parede é possível.

Cada baú lembra o seu par, então o que foi colocado continua pareado ou simples depois de um reload, um funil ou cano enche apenas o baú que toca, e quebrar uma metade deixa a outra simples. Baús colocados antes de a opção ser ligada, ou pela geração de mundo e por estruturas, se unem como a 1.12 sempre fez. Um cliente sem RDPL ainda desenha dois baús simples que se tocam como um baú duplo, embora os abra separadamente.

## Bônus: correção de conflito de plugins do JEI

*referência*

Alguns mods consultam o registro de receitas do JEI antes que os mods que o fornecem tenham terminado de inicializar, o que inunda os logs com centenas de erros inofensivos, mas ruidosos, e pode quebrar silenciosamente a integração de um mod com o JEI. O RDPL detecta isso automaticamente e corrige a ordem de notificação. Funciona com o Just Enough Items e com o Had Enough Items. Se nenhum dos dois estiver instalado, nada acontece.

## Bônus: menos erros na inicialização

*referência*

- Receitas que referenciam um item que nenhum mod realmente registrou, geralmente conteúdo desativado na configuração própria de um mod, são ignoradas em vez de lançar um erro de análise. A contagem é registrada uma vez. (`skipMissingItems`)
- Avanços que recompensam uma receita que um script removeu desde então ainda carregam, em vez de falhar. Eles apenas nunca desbloqueiam essa receita, e o conjunto todo é resumido numa única linha. (`tolerateMissingInAdvancements`)

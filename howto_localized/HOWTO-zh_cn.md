# Resource Data Pack Loader

**一个文件夹，即可覆盖 Minecraft 或任何模组提供的内容，用 JSON 定义新内容，并控制世界的生成方式；在每个世界、客户端与服务器上均生效，玩家无需开启任何开关。**

一个可用的示例。直接把它放进 `rdploader`，看看每个文件是怎么写的。

- [RDPLExamplePack.zip](../example/RDPLExamplePack.zip) 用到了加载器能读取的几乎所有文件类型：方块、物品、一种流体、一个创造模式标签页、生物群系、一个世界模板、一个设有传送门限制的维度、世界生成、一种药水及其酿造、一名村民及其交易、配方、战利品、对原版内容的覆盖、一个音效、一个进度和一个函数。其自述文件说明了在游戏中应检查什么。

本指南适用于 1.20.1 和 1.21.1 版本。两者读取相同的资源包；两者不同的少数地方以 **1.20.1** 和 **1.21.1** 标出。

---

## 目录

**快速入门**
- [它是什么](#它是什么)
- [文件放在哪里](#文件放在哪里)
- [如何阅读表格](#如何阅读表格)
- [唯一的规则](#唯一的规则)
- [组织资源包](#组织资源包)
- [资源包：谁优先](#资源包谁优先)

**资源包如何工作**
- [定义如何工作](#定义如何工作)
- [可以覆盖的内容](#可以覆盖的内容)
- [服务端资源包](#服务端资源包)
- [注册表重命名](#注册表重命名)
- [模组 API](#模组-api)
- [为 1.12.2 编写的资源包](#为-1122-编写的资源包)

**方块与物品**
- [方块](#方块)
- [容器](#容器)
- [钟](#钟)
- [模型、方块状态与纹理](#模型方块状态与纹理)
- [让原版正确对待你的方块](#让原版正确对待你的方块)
- [物品](#物品)
- [流体](#流体)
- [材料、标签页、音效、标签](#材料标签页音效标签)
- [属性覆盖](#属性覆盖)
- [硬度分组](#硬度分组)

**合成、战利品与交易**
- [禁用的方块与物品](#禁用的方块与物品)
- [熔炉配方与燃料](#熔炉配方与燃料)
- [药水、药水类型与酿造](#药水药水类型与酿造)
- [铁砧操作](#铁砧操作)
- [方块掉落物](#方块掉落物)
- [玩家战利品](#玩家战利品)
- [村民与交易](#村民与交易)

**生物与危险**
- [实体变种](#实体变种)
- [暴露设置](#暴露设置)

**世界**
- [世界模板](#世界模板)
- [游戏规则](#游戏规则)
- [生物群系](#生物群系)
- [维度](#维度)
- [传送门与门](#传送门与门)
- [深层世界](#深层世界)
- [洞穴区域](#洞穴区域)

**生成世界**
- [世界生成条目](#世界生成条目)
- [形状](#形状)
- [扩散](#扩散)
- [结构地图](#结构地图)
- [村庄地块](#村庄地块)
- [城市布局地图](#城市布局地图)
- [回溯生成](#回溯生成)
- [预生成](#预生成)

**游戏模式**
- [世界介绍](#世界介绍)
- [队伍](#队伍)
- [计分](#计分)
- [袭击](#袭击)
- [卡片](#卡片)

**控制**
- [控制层](#控制层)
- [各分组的作用](#各分组的作用)

**其他模组**
- [Blast Plaster 集成](#blast-plaster-集成)

**参考**
- [值列表](#值列表)
- [文件夹列表](#文件夹列表)
- [命令](#命令)
- [须知](#须知)
- [出现问题时](#出现问题时)
- [额外功能：原版调整](#额外功能原版调整)
- [未延续的键](#未延续的键)

---

# 快速入门

## 它是什么

*快速入门*

Resource Data Pack Loader（RDPL）读取唯一的一个文件夹 `rdploader`，并完成三项工作：

- **覆盖。** 文件夹中的文件会替换游戏或模组本来会加载的那个文件。无需开关，无需按世界单独设置，玩家什么都不用启用。
- **新内容。** 用 JSON 定义注册方块、物品、流体、生物群系、维度、药水和村民。无需 Java，无需 jar。
- **控制。** 阻止矿石、生物群系、结构或配方的生成，把基岩压平，设置生成率，让主世界变成虚空，设置世界默认值。

## 文件放在哪里

*快速入门*

一个资源包有两个根目录，与原版资源包相同。`assets/` 存放客户端绘制和播放的内容：模型、方块状态、纹理、语言文件、音效以及世界介绍的文本。`data/` 存放其余一切：本模组读取的每一种定义，以及资源包所替换的原版数据文件。本指南中的每个路径都从命名空间起写，所以对于命名空间为 `mypack` 的资源包，`<namespace>/blocks/*.json` 在磁盘上就是 `data/mypack/blocks/ruby_ore.json`，`<namespace>/models/` 则是 `assets/mypack/models/`。每一节都会在标题下重复写出自己的路径。

`data/` 之下：

| 路径                                         | 存放内容                                                                                                              |
| -------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| `<namespace>/blocks/*.json`                  | 方块定义。[方块](#方块)                                                                                               |
| `<namespace>/items/*.json`                   | 物品定义。[物品](#物品)                                                                                               |
| `<namespace>/fluids/*.json`                  | 流体，附带一个方块和一个桶。[流体](#流体)                                                                             |
| `<namespace>/materials/*.json`               | 工具与盔甲材料。[材料、标签页、音效、标签](#材料标签页音效标签)                                                       |
| `<namespace>/tabs/*.json`                    | 创造模式标签页。[材料、标签页、音效、标签](#材料标签页音效标签)                                                       |
| `<namespace>/sounds/*.json`                  | 声音事件。[材料、标签页、音效、标签](#材料标签页音效标签)                                                             |
| `<namespace>/biomes/*.json`                  | 生物群系定义。[生物群系](#生物群系)                                                                                   |
| `<namespace>/worldgen/*.json`                | 生成什么，以及在哪里生成。[世界生成条目](#世界生成条目)                                                               |
| `<namespace>/caveregions/*.json`             | 绘制在地下的命名区域。[洞穴区域](#洞穴区域)                                                                           |
| `<namespace>/dimensions/*.json`              | 维度定义。[维度](#维度)                                                                                               |
| `<namespace>/worldtemplates/*.json`          | 把整个世界的设置写在一个文件里。[世界模板](#世界模板)                                                                 |
| `<namespace>/worldintro/*.json`              | 玩家进入世界时显示的页面。[世界介绍](#世界介绍)                                                                       |
| `<namespace>/gates/*.json`                   | 传送门与维度的条件。[传送门与门](#传送门与门)                                                                         |
| `<namespace>/gamerules/*.json`               | 新世界的游戏规则。[游戏规则](#游戏规则)                                                                               |
| `<namespace>/teams/*.json`                   | 原版记分板上的阵营，以及谁会加入。[队伍](#队伍)                                                                       |
| `<namespace>/scoring/*.json`                 | 目标、积分以及一场比赛如何结束。[计分](#计分)                                                                         |
| `<namespace>/raids/*.json`                   | 玩家把不祥之兆带进村庄时，向村庄袭来的波次。[袭击](#袭击)                                                             |
| `<namespace>/entities/*.json`                | 基于已有实体构建的实体变种。[实体变种](#实体变种)                                                                     |
| `<namespace>/hardness/*.json`                | 一组方块的挖掘时间与爆破倍率。[硬度分组](#硬度分组)                                                                   |
| `<namespace>/anvils/*.json`                  | 铁砧附加在指定物品上的附魔、由此获得的进度，以及在此之前的锁定。[铁砧操作](#铁砧操作)                                 |
| `<namespace>/cards/*.json`                   | 由触发器显示的屏幕卡片，以及本模组自己发出的消息。[卡片](#卡片)                                                       |
| `<namespace>/exposures/*.json`               | 使靠近指定方块、携带指定物品或身处指定维度的玩家受到影响的危险。[暴露设置](#暴露设置)                                 |
| `<namespace>/overrides/<target>/<name>.json` | 就地修改已有方块、物品和药水类型的属性。[属性覆盖](#属性覆盖)                                                         |
| `<namespace>/villages/*.json`                | 城市或村庄可以建造的地块。[村庄地块](#村庄地块)                                                                       |
| `<namespace>/pathintersects/*.json`          | 绘制在村庄道路交汇处的图案。[村庄道路](#村庄道路)                                                                     |
| `<namespace>/structuremaps/*.json`           | 在网格上组合成一座大型建筑的模板。[结构地图](#结构地图)                                                               |
| `<namespace>/citymaps/*.json`                | 一张手绘的街道规划图，城市依据它布局，而不是随机生成。[城市布局地图](#城市布局地图)                                   |
| `<namespace>/portalframes/*.json`            | 玩家可以搭建并点燃的框架。[传送门框架](#传送门框架)                                                                   |
| `<namespace>/blastplaster/*.json`            | Blast Plaster 在爆炸之后的行为，按维度区分。[Blast Plaster 集成](#blast-plaster-集成)                                 |
| `<namespace>/structures/*.nbt`               | 模板，用于树苗、`imprint` 和模组覆盖。[可以覆盖的内容](#可以覆盖的内容)                                               |
| `<namespace>/recipes/*.json`                 | 合成配方，新增或替换。[可以覆盖的内容](#可以覆盖的内容)                                                               |
| `<namespace>/recipe_removals/*.json`         | 按名称、命名空间或产物删除的配方。[可以覆盖的内容](#可以覆盖的内容)                                                   |
| `<namespace>/disabled/*.json`                | 被移出游戏的方块和物品。[禁用的方块与物品](#禁用的方块与物品)                                                         |
| `<namespace>/furnace/*.json`                 | 新增和移除的熔炉配方。[熔炉配方与燃料](#熔炉配方与燃料)                                                               |
| `<namespace>/fuels/*.json`                   | 燃烧时间。[熔炉配方与燃料](#熔炉配方与燃料)                                                                           |
| `<namespace>/brewing/*.json`                 | 酿造台配方。[药水、药水类型与酿造](#药水药水类型与酿造)                                                               |
| `<namespace>/potions/*.json`                 | 药水效果。[药水、药水类型与酿造](#药水药水类型与酿造)                                                                 |
| `<namespace>/potion_types/*.json`            | 由这些效果构成的瓶装药水。[药水、药水类型与酿造](#药水药水类型与酿造)                                                 |
| `<namespace>/villagers/*.json`               | 村民职业。[村民与交易](#村民与交易)                                                                                   |
| `<namespace>/trades/*.json`                  | 职业收购与出售的物品。[村民与交易](#村民与交易)                                                                       |
| `<namespace>/loot_tables/*.json`             | 被替换的战利品表。[可以覆盖的内容](#可以覆盖的内容)                                                                   |
| `<namespace>/loot_injections/*.json`         | 加入已有战利品表的一个池。[可以覆盖的内容](#可以覆盖的内容)                                                           |
| `<namespace>/block_drops/*.json`             | 为资源包并不拥有的方块新增或替换的掉落物。[方块掉落物](#方块掉落物)                                                   |
| `<namespace>/player_loot/*.json`             | 玩家死亡时抽取的战利品表。[玩家战利品](#玩家战利品)                                                                   |
| `<namespace>/advancements/*.json`            | 进度。[可以覆盖的内容](#可以覆盖的内容)                                                                               |
| `<namespace>/functions/*.mcfunction`         | 函数文件。[可以覆盖的内容](#可以覆盖的内容)                                                                           |
| `<namespace>/tags/<kind>/*.json`             | 标签，使用游戏自身的格式。[材料、标签页、音效、标签](#材料标签页音效标签)                                             |
| `<namespace>/registry_remap/*.json`          | 把旧名称映射到新名称。[注册表重命名](#注册表重命名)                                                                   |

`assets/` 之下：

| 路径                                                                                            | 存放内容                                                                                      |
| ----------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | 常规的资源文件夹。[模型、方块状态与纹理](#模型方块状态与纹理)                                 |
| `<namespace>/sounds.json`                                                                       | 游戏读取的声音索引，与 `data/` 下的 `sounds/` 定义并列                                        |
| `<namespace>/texts/*.txt`                                                                       | 纯文本文件，供世界介绍使用。[世界介绍](#世界介绍)                                             |

**1.21.1** 以单数形式命名原版数据文件夹：`loot_table/`、`recipe/`、`advancement/`、`function/`、`structure/`、`tags/item/`、`tags/block/`。资源包在此处两种写法都可以；上面的复数名称会按其单数对应项读取，所以同一个资源包可用于两个版本。

## 如何阅读表格

*快速入门*

每个文件都是标准 JSON。一个典型的世界生成条目：

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

本文档中的键表会说明一个键是否必需、它存放什么，以及省略时的默认值。无法识别的值会被记入日志并替换为默认值，不会导致游戏崩溃。全文使用的值类型：

| 表中写作                                            | 你应写成                                                                                                                                                                         |
| --------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| int                                                 | `8`                                                                                                                                                                              |
| int, ticks                                          | `100`（20 刻 = 1 秒）                                                                                                                                                            |
| int or range                                        | `8`，或 `{ "min": 4, "max": 12 }`，在两者之间随机取值                                                                                                                           |
| 0 to 15, 1 to 100 and such                          | 位于该范围内的整数                                                                                                                                                               |
| float                                               | `0.5`                                                                                                                                                                            |
| boolean                                             | `true` 或 `false`                                                                                                                                                                |
| string                                              | `"words in quotes"`                                                                                                                                                              |
| block name, item name                               | `"minecraft:stone"`。方块状态就是名称加上并列的 `properties`：`{ "block": "minecraft:oak_log", "properties": { "axis": "x" } }`                                                  |
| `namespace:name`                                    | `"mypack:ruby_ore"`                                                                                                                                                              |
| biome name, sound name, tab name                    | 同样的带引号的 `namespace:name` 形式                                                                                                                                             |
| dimension id                                        | `"minecraft:overworld"`、`"minecraft:the_nether"`、`"minecraft:the_end"`，或资源包自己的 `"mypack:verdant"`。1.12.2 的数字 `0`、`-1` 和 `1` 仍会被当作这三个维度                  |
| hex color                                           | 六位十六进制数字，`"A0C8FF"`，`#` 可有可无                                                                                                                                       |
| texture path                                        | `"mypack:block/ruby_ore"`                                                                                                                                                        |
| list of ints                                        | `[4, 12]`                                                                                                                                                                        |
| list of block names                                 | `["minecraft:stone", "minecraft:andesite"]`                                                                                                                                      |
| list of biome names                                 | `["minecraft:windswept_hills", "mypack:ruby_hills"]`                                                                                                                             |
| list of biome types                                 | `["mountain", "forest"]`，即[值列表](#值列表)中列出的类型词，每个词代表一个生物群系标签                                                                                          |
| list of mod ids or pack namespaces                  | `["quark", "mypack"]`                                                                                                                                                            |
| list of objects                                     | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`，键以该对象自己的表为准                                                                                                  |
| object                                              | `{ "type": "cluster" }`，键以其自己的表为准                                                                                                                                      |
| object of role to biome, of variant name to variant | 键是前者，值是后者：`{ "ocean": "mypack:ruby_ocean" }`                                                                                                                           |

大多数定义还接受 `requires`，这是一个由模组 id 或资源包命名空间组成的列表，这些都必须存在，否则该文件会被跳过。

## 唯一的规则

*快速入门*

打开 jar，找到你想修改的文件，从 `assets` 或 `data` 起复制它的路径：

```
assets/minecraft/textures/block/iron_ore.png                 (in the Minecraft jar)
rdploader/assets/minecraft/textures/block/iron_ore.png       (your override)

data/minecraft/loot_tables/blocks/iron_ore.json              (in the Minecraft jar)
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    (your override)
```

`assets` 或 `data` 之后的路径始终与 jar 内的路径完全一致。没有任何重命名或移动。

## 组织资源包

*快速入门*

散装文件可放在 `rdploader/assets/<namespace>/` 和 `rdploader/data/<namespace>/` 下。也可以打成 zip 来分组。`rdploader` 中的文件夹永远不会被当作资源包：它会被跳过，并在日志中给出警告，所以请先把资源包压缩成 zip 再放进去。压缩时，请选中其中的内容再压缩，而不是压缩包含它们的文件夹：如果 zip 的顶层是一个包着 `assets` 或 `data` 的文件夹，它会被跳过，日志中会说明。

```
rdploader/assets/minecraft/textures/block/iron_ore.png
rdploader/MyTextures.zip
```

**放错文件夹的资源包。** 启动时，在读取 `rdploader` 之前，RDPL 会检查游戏的 `resourcepacks` 文件夹以及每个世界的 `datapacks` 文件夹（在专用服务器上，是 `level-name` 所指的世界），并把找到的每个 RDPL 资源包 zip 移入 `rdploader`。如果 zip 中包含 RDPL 定义文件，例如 `data/<namespace>/blocks/`，或者对 1.12.2 的资源包而言，`assets/<namespace>/blocks/`，它就是 RDPL 资源包。普通的资源包或数据包保持原位。如果 `rdploader` 中已有同名 zip，则该 zip 留在原处；文件夹形式的 RDPL 资源包也一样；两种情况都会给出警告。每一次移动都会写入 `logs/rdpl.log`。游戏会自行把被移走的资源包从资源包列表或世界的数据包中去掉，此后 RDPL 从 `rdploader` 加载它。

**优先级。** 当两个资源包含有同一个文件时，在名称前加上 `RDPL` 和一个数字；数字越大，加载得越晚，也就越优先：

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

不区分大小写；数字后面的空格、连字符或下划线可有可无；该前缀不会出现在显示名称中。没有前缀的资源包最先加载，并会输给任何带数字的资源包。优先级也决定世界生成条目的顺序，当一个资源包铺设的方块会被另一个资源包替换时，这一点很重要。

**禁用资源包** 的方法是在其名称末尾加上 `.disabled`。

**一个 zip 适用于所有版本。** 一个 zip 可以为它所支持的每个 Minecraft 版本携带一个 `versions/<version>/` 文件夹：`versions/1.12.2/`、`versions/1.20.1/`、`versions/1.21.1/`，在 26.x 上则是其运行的确切版本，`versions/26.1.2/`、`versions/26.2/` 或 `versions/26.3/`。每个文件夹的布局都与该版本资源包的根目录相同，包括 `pack.mcmeta`。当前运行版本的文件夹下的文件，会取代根目录中相同路径的文件被读取；根目录由所有版本共用，其他版本的文件夹则永远不会被读取。把所有版本读取方式相同的内容放在根目录，只把有差异的内容放进版本文件夹，一个 zip 就能在这四个版本上加载。

**资源包在版本之间转换。** 加载为另一条版本线编写的资源包时，会在第一次加载时以同样的方式转换它，并把改动的内容写入它自己的 `versions/<version>/` 文件夹，如上所述；这是加载时自动发生的，包括 `/rdpl reload` 或 `/rdplserver reload` 触发的那次加载，没有专门的命令。每一对版本都能双向转换，所以 1.12.2、1.20.1、1.21.1 或 26.x 的资源包可以在其他任何版本上加载。26.3 的资源包同样可以在每条版本线上加载，在较旧的版本上按 26.2 的格式读取，较旧的资源包也可以在 26.3 上加载。一方有而另一方无法容纳的内容会被丢弃，日志会逐一列出：从 26.3 向下转换时，被丢弃的包括调试用密度函数、资源包自己的含水层排除和地表高度，以及 26.2 没有的命令；向上转换到 26.3 时，包括生成目标的 `depth` 和 `offset`，以及噪声路由器的矿脉键。[未延续的键](#未延续的键)列出了全部内容。

zip 根目录下的 `pack.mcmeta` 受欢迎但并非必需：本模组会把每个资源包以它自己的一个条目呈现给游戏，并采用游戏所期望的资源包格式，所以资源包永远不会因为格式编号而过时。在它旁边放一个 `pack.png`，就能给该文件夹的条目设置图标。没有的话，该条目显示 RDPL 图标。

## 资源包：谁优先

*快速入门*

默认情况下，RDPL 的文件位于玩家所选资源包之上，所以资源包无法覆盖它们。在 `RDPL` 前缀之后加上 `O` 或 `N`，可以按资源包单独决定：

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

没有字母的资源包遵循 `overrideResourcePacks` 配置选项。`/rdpl list` 会标出那些进行覆盖的资源包。该字母必须是前缀的结尾（其后跟空格、连字符、下划线，或什么都没有），所以 `RDPLOverhaul` 是一个名为 `Overhaul` 的资源包，而不是带 `O` 标记的。

同样的层级也适用于数据包。标有 `N` 的资源包位于世界自己 `datapacks` 文件夹中携带的数据包之下，标有 `O` 的则位于其上。

---

# 资源包如何工作

## 定义如何工作

*资源包如何工作*

除了覆盖文件的那些文件夹之外，还有描述新事物的文件夹。一个定义文件把同一类的一个或多个事物归在 `variants` 之下，`variants` 里的每个键都是一个注册名：`data/mypack/blocks/ore.json` 中若有一个名为 `ruby_ore` 的变种，就会注册 `mypack:ruby_ore`。文件本身的名称只是一个分组，别无他意；一个文件可以只放一个方块，也可以放十几个共用设置的方块。

注册发生在加载器所提供的最低优先级上，所以如果某个真正的模组注册了同样的名称，模组胜出，你的文件会被忽略。这里的任何内容都无法取代模组。

**界限在哪里。** 凡是需要自带方块实体、界面、物品栏或自带逐刻逻辑的内容，都需要真正的模组，只有一个例外：[容器](#容器)类型，它自带物品栏和界面。除此之外的一切都可以做。

### 你的命名空间就是你的模组

*定义如何工作*

你选择的命名空间，在一切实际用途上都等同于模组 id。没有任何东西会被当作模组加载，它也不会出现在模组列表中，但所有读取模组 id 的地方都会读取你的命名空间：

- 注册名是 `mypack:ruby_ore`，与模组的注册名完全一样，并且会写入每个包含它们的已保存世界。
- 配置中的矿石、生物群系和配方白名单都会匹配它，所以 `oreWhitelist = mypack` 会保留你的矿石和方块，以及其他所有人的矿石和方块。
- `/rdpl which`、`/rdplserver oregen` 和各种报告都按它分组。
- JEI、标签以及其他模组的查询也以同样的方式看待它。

所以一开始就选定一个名称，永远不要更改。重命名命名空间会让世界中已放置的一切变成孤儿，就像模组更改自己的 id 一样，这正是 `registry_remap` 存在的原因：用来修复这种情况。

这是双向的：`requires` 既接受已安装的模组 id，也同样接受资源包命名空间，所以一个资源包可以依赖另一个，并在对方未安装时被跳过。

`requires` 中点名的模组或资源包如果未安装，该定义会被跳过：`logs/rdpl.log` 中会写入一行，说明缺少了什么，游戏则继续运行。如果你预期的某个方块没有出现在创造模式标签页中，首先应该查看的就是这行日志。

`requires` 只接受纯 id。没有版本范围语法，所以它只能表示某个模组必须存在，而不能指定哪个版本。

本模组自己的 id `resourcedatapackloader` 是保留的。在它之下定义内容会被忽略并记入日志，因为那会把本模组所注册内容的所有权据为己有。覆盖本模组自己的资源仍然可以，只是不能在那里注册内容。

下面的每张表都遵循[如何阅读表格](#如何阅读表格)中的约定。

大多数定义还接受 `requires`，这是一个由模组 id 或资源包命名空间组成的列表，这些都必须存在，否则该文件会被跳过。

### 资源包选项

*定义如何工作*

选项文件接受的每个键：

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

| 键                      | 必需 | 值                    | 默认值  | 作用                                                                                                                                        |
| ----------------------- | ---- | --------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| an option name          | yes  | boolean, or an object |         | `true` 或 `false` 即该选项的默认值。对象则携带下面的三个键                                                                                  |
| `hide` at the top level | no   | boolean               | `false` | 使本资源包的选项不出现在选项界面和生成的文件中，而它们仍按默认值控制内容                                                                    |
| `default`               | yes  | boolean               |         | 在用户更改之前该选项的值。没有布尔值 `default` 的对象会被忽略，并给出警告                                                                   |
| `hide` inside an option | no   | boolean               | `false` | 隐藏这一个选项，使其无法被切换，并保持默认值                                                                                                |
| `description`           | no   | string                | none    | 显示在选项界面中该选项名称的下方                                                                                                            |

资源包可以在 `assets` 和 `data` 旁边带一个 `config` 文件夹，其中是由 JSON 文件组成的、带默认值的真假选项：

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

顶层带有 `"hide": true` 的文件，会使该资源包的选项不出现在选项界面和生成的文件中，而这些选项仍按默认值控制内容。有两种情况需要这样做：尚未准备好发布的内容，以及模板资源包，其中的选项只是把各个定义维系在一起的机制，而不是任何人应该去做的选择。删除该键即可发布它们。同样也可以按单个选项来做：选项对象内的 `"hide": true` 只隐藏那一个选项，所以一个已完成的资源包可以带有控制未完成内容的开关或模板闸门，而它们都不会显示出来：

    { "enablePackB": { "default": false, "hide": true } }

由于隐藏的选项无法被切换，默认值为 true 的隐藏选项实际上是被强制开启的，适用于那些必须通过选项机制保持连接、但并非一种选择的内容。

选项也可以是一个带有说明的对象，说明显示在选项界面中其名称的下方：

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

启动时，资源包的选项文件会合并成一个真正的、归用户所有的配置文件，以资源包命名，即 `rdploader/config/PackA.json`，创建时带有资源包的默认值，并在资源包更新时合并，所以新选项会加进来，而不会触动用户已经设置的内容。更改在下次启动游戏时生效，玩家在世界选择界面和创建世界界面上的“资源包选项”按钮处切换它们。选项只属于有名称的资源包，也就是 zip，因为生成的文件以资源包命名；`rdploader/assets` 和 `rdploader/data` 下的散装文件没有资源包名称，不带任何选项，所以如果散装内容需要开关，请把它们打成一个有名称的 zip 资源包。

之后，任何定义的 `requires` 列表都可以用 `config:` 条目来指定一个选项：`"requires": ["config:enableTestingContent"]` 只在该选项为 true 时才注册该内容，与缺少模组时跳过的方式完全相同。不带资源包名的名称会检查每个资源包的文件，且每个定义了它的资源包必须一致；`"config:PackA:enableTestingContent"` 则指定某一个资源包。没有任何资源包定义的选项按 false 处理，并只警告一次。

如果一个选项控制着世界创建时所用的内容，该世界会记住它，并在每次世界保存时再次记录。更改它后再次打开世界，如果这一更改使世界所持有的内容变为未注册，世界会先被备份到游戏自己的 `backups` 文件夹，与“编辑世界”界面的做法完全相同；如果该备份失败，世界不会被打开。第一个进入主世界的玩家会被告知哪些选项发生了变化，以及（如果做过备份）已经制作了副本。

`file:` 条目以游戏文件夹下某个文件或文件夹是否存在为条件，用于把内容与 RDPL 自己的资源包之外的东西挂钩，例如另一个模组的资源包：`"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` 只在那个确切的文件已安装时才注册该内容。路径相对于游戏文件夹，始终使用正斜杠，且不得包含 `..`。

### 继承定义

*定义如何工作*

方块或物品定义可以用 `"inherits"` 从同类的另一个定义出发，指定任意变种的注册名，然后覆盖不同之处：

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

子定义会复制父定义的文件和所指定变种的每一项数值，文件顺序无关紧要，继承链按先父后子的顺序解析，出现循环或父定义缺失时会记入日志，子定义保持原样。子定义写出的字段会替换继承来的值；嵌套的变种属性逐项覆盖，但像 `requires` 这样的列表则是整体替换，所以请写出你想要的完整列表。方块只能从方块继承，物品只能从物品继承。

### 方块与物品模板

*定义如何工作*

父定义可以是一个永远不会进入游戏的纯模板，因为继承读取的是定义文件本身，而不是已注册的内容。把模板放在一个被强制关闭的隐藏选项之后，它就不会注册任何东西，而它的数值仍可被继承：

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

模板永远不会注册，而 `jacks_ore` 会带着模板的材料、声音、工具、标签页、经验掉落和抗性注册，只覆盖硬度。子定义必须写出自己的 `requires`，这里清空为空列表，否则它会继承父定义的 `requires`，并随之消失。

## 可以覆盖的内容

*资源包如何工作*

- **模组资源文件夹中的任何内容**，包括纹理、模型、方块状态、语言文件、音效、字体、闪烁标语、指南书、手册
- **进度、战利品表、标签和函数**，在服务端生效，所以在专用服务器上也能工作
- **配方**，替换模组的配方或添加你自己的
- **结构模板**，即模组用于生成建筑的 `.nbt` 文件，位于 `<namespace>/structures/` 下
- **注册表重命名**，在模组重命名方块或物品时，让旧世界继续可用
- **配方移除**，按名称、命名空间或产物删除合成配方
- **禁用的方块与物品**，把任何方块或物品移出游戏，参见[禁用的方块与物品](#禁用的方块与物品)
- **战利品注入**，向战利品表添加一个池，而不是替换整张表
- **方块掉落物**，为任何方块在被破坏时的掉落物（包括经验）追加内容或替换
- **玩家战利品**，在玩家死亡时抽取一张战利品表，叠加在其所携带物品之上，或取而代之
- **现有方块、物品和药水的属性**，包括硬度、光照、堆叠上限、任意物品上的食物属性、药水的效果，参见[属性覆盖](#属性覆盖)
- **熔炉配方、燃料燃烧时间、创造模式标签页和声音事件**

在此版本中，方块掉落什么由它的战利品表决定：要改变石头的掉落物，请提供 `data/minecraft/loot_tables/blocks/stone.json`；若要在不替换的前提下追加内容，则使用战利品注入或[方块掉落物](#方块掉落物)规则，后者还可以给予经验。

RDPL 适合替换一两个配方，而你自己内容的配方则应随资源包一并添加。若要在整个整合包范围内完全控制配方，KubeJS 和 CraftTweaker 是更好的选择；而这里的文件仍会完整替换原文件，所以若只想改动一种原料或去掉一个战利品条目，请使用它们。

## 服务端资源包

*资源包如何工作*

资源包可以只放在服务器上，让玩家使用纯原版客户端，但有一个限制：**其中任何内容都不得注册任何东西**。本模组接受任何远端；由资源包来决定。原版客户端使用它自带的注册表，所以向注册表添加内容的资源包必须两端都有。

| 只放服务器即可                                                                                                                                            | 客户端也需要该资源包                                                                                                    |
| --------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `structuremaps`, `citymaps`, `villages`, `pathintersects`, `caveregions`, `biomes`, `dimensions` | `blocks`, `items`, `fluids`, `materials`, `containers`                                                                  |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `anvils`, `tags`, `disabled`                                                                 | `potions`, `potion_types`, `sounds`, `tabs`, `exposures`                                                                |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions`                                                                              | `entities`, `villagers`, `portalframes`                                                                                 |
| `gates`, `cards`, `trades`, `registry_remap`, `teams`, `scoring`, `raids`, `hardness`, `blastplaster`                                                     | `models`, `blockstates`, `textures`, `lang`, `worldintro`, `overrides`（客户端文件夹：没有客户端时，请不要放）           |
| 整个控制层、设置和预生成                                                                                                                                  |                                                                                                                         |

右列是硬性限制：原版客户端没有的方块、物品、实体类型、声音和药水效果，无法向它描述，而维度自己的传送门就是资源包的方块之一；暴露设置只会与那些内容一起加载。左列之所以可行，是因为其中的一切要么完全在服务端运行，要么以原版已能读取的数据包条目的形式传到客户端（生物群系、洞穴区域、维度类型），要么通过原版已经会说的数据包传到客户端（由服务器填充的合成结果槽、普通的进度数据包、状态消息形式的传送门拒绝提示，以及由原版游戏模式/标题/传送数据包构成的预生成等待）。

设置步骤：

1. 在配置中启用 `vanillaClients`（`content` 类别，需要重启）。它会强制执行右列：这些文件夹在加载时被跳过，每个被跳过的文件都会在日志中点名，所以一个误放的方块文件只会变成一行日志，而不是被拒绝的连接。
2. 无论如何都要让定义远离右列的文件夹；被跳过的文件只是累赘。当资源包引用物品时（门的 `hold`、`killedDrops`、配方产物、交易），只写原版或服务器上其他两端都有的模组所提供的物品。一个指明了资源包自己地面方块的生物群系会保留基础生物群系的地面，由自己的传送门开启的维度则需要那个传送门方块，所以请用命令把玩家送过去。
3. 在此版本中，实体变种本身就是独立的实体类型，所以它们属于右列：开启 `vanillaClients` 后它们会被跳过，它们的生成也随之跳过，日志会点名它们。
4. 照常安装在服务器上，并装上 Blast Plaster，本模组要求它，而它同样不注册任何东西。玩家的机器上什么都不用放；对他们来说 `/rdpl` 不会存在。
5. 用一个干净的同版本原版客户端加入一次来测试。失败会很明显：连接在门口就被拒绝，而不是稍后悄悄出问题。
6. 两处可接受的外观缺憾：服务器添加的配方可以合成，但不会出现在配方书中；造陆期间的等待只是普通的旁观者模式等待，进度显示在动作栏上，没有本模组自己的客户端所绘制的雾和徽标。

## 注册表重命名

*资源包如何工作*

`<namespace>/registry_remap/*.json`

文件名由你选择，只读取文件夹，多个文件会叠加。

当模组重命名它的某个方块或物品时，在重命名之前保存的世界会丢失它们。在此处放一个文件，把旧名称映射到新名称：

```json
{
  "registry": "minecraft:item",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

注册表是条目所属的那一个，名称按游戏的叫法书写：`minecraft:item`、`minecraft:block`、`minecraft:entity_type` 等等。重命名可以串联，所以把 A 映射到 B，之后又把 B 映射到 C，就会把 A 直接送到 C。

## 模组 API

*资源包如何工作*

模组可以把 RDPL 内容放在自己的 jar 里，这样就不需要单独的资源包。在 jar 的根目录放一个名为 `rdploader` 的文件夹，并让它的布局与资源包完全相同：

```
thatmod.jar
  META-INF/mods.toml                (1.21.1: META-INF/neoforge.mods.toml)
  rdploader/data/thatmod/blocks/ruby_ore.json
  rdploader/assets/thatmod/textures/block/ruby_ore.png
```

模组提供的是默认值，而不是覆盖。它在资源包文件夹中的所有资源包之下加载，所以资源包作者写的任何内容都优先于它，并且模组只能提供它自己模组文件中声明的命名空间下的文件。其他命名空间下的文件会被忽略并给出警告，命名空间内部嵌套的 `rdploader` 文件夹同样如此，所以模组无法悄悄重新定义另一个模组或资源包作者的内容。

每个附带一个这样文件夹的模组，在第一次被看到时，都会在 `rdploader/config/mods.json` 中获得一个条目：

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| 字段       | 值                | 默认值  | 作用                                                                                                                                        |
| ---------- | ----------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| `enabled`  | `true` 或 `false` | `true`  | 关闭该模组的内容，方式与 `.disabled` 关闭资源包相同                                                                                         |
| `priority` | `-1` or a number  | `-1`    | `-1` 使该模组位于所有资源包之下；其他任何数字则把它放进与带编号资源包并列的普通[优先级](#组织资源包)顺序                                    |

无论 `overrideResourcePacks` 如何设置，模组资源包都不会加入资源包覆盖层级，因为只有资源包作者才能用 `O` 字母提出这一要求。日志会标出模组资源包，并从低到高列出资源包，所以没有任何东西会在不为人知的情况下加载。

## 为 1.12.2 编写的资源包

*资源包如何工作*

为 1.12.2 版本线制作的资源包可以原样加载。加载器通过它的 `pack.mcmeta` 格式、通过 `assets/` 下没有与之并列的 `data/` 的定义文件夹，或通过 `.lang` 文件来识别这样的资源包，并把它向前延续。zip 只会在自身内部转换一次：此版本读取方式不同的每个文件，都会写入 zip 的 `versions/1.20.1/` 文件夹（1.21.1：`versions/1.21.1/`），并已完成下面的所有处理，而根目录中的 1.12.2 文件保持原样，所以同一个 zip 仍然可以在 1.12.2 上加载，如[一个 zip 适用于所有版本](#组织资源包)所述。转换会在它写入的文件夹里放一个 `port.stamp` 文件，记录 RDPL 的版本。已经带有此版本文件夹的 zip 会通过该文件夹读取；当它的 `port.stamp` 指向 RDPL 的另一个版本时，转换会重新写入该文件夹，替换其中的每个文件，并在日志中点名每一个，而没有 `port.stamp` 的文件夹，例如由资源包作者自己写的，则永远不会再被转换。转换所替换的根目录下的 1.12.2 文件，例如 `assets/` 下的定义、`.lang` 文件、1.12.2 的方块状态和模型，以及 `textures/blocks/` 和 `textures/items/` 下的纹理，在此版本上不会被读取；只有转换原样放行的根目录文件，例如音效，仍会被读取。zip 会先写入一个临时文件，在完整之后才替换原文件。`rdploader/assets` 下的散装文件不会被重写；每次扫描文件夹时，它们都会经由同一个转换流程来读取。

- 定义文件夹从 `assets/<namespace>/` 移到 `data/<namespace>/`，原版数据文件夹也随之移动：配方、战利品表、战利品注入、进度、函数和结构，并改为 1.21.1 读取的单数名称（`recipe`、`loot_table`、`advancement`、`function`、`structure`、`tags/item`）。
- `textures/blocks/` 和 `textures/items/` 会作为 `textures/block/` 和 `textures/item/` 提供，在模型中、在像素图中以及在文件本身中均如此。位于 `models/item/<file>/<variant>.json` 的物品模型会作为 `models/item/<variant>.json` 提供。
- 每个带元数据的 id，`minecraft:wool:14` 或 `minecraft:dye:4`，都会交给游戏自己的数据修复器处理，也就是升级 1.12.2 世界所用的同一套代码，所以得出的是它后来变成的那个方块或物品：`minecraft:red_wool`、`minecraft:lapis_lazuli`。在扁平化之后保留为属性的方块状态，`minecraft:log:1` 变为带 `axis=y` 的 `minecraft:oak_log`，会以 `properties` 对象的形式给出。资源包自己的 id 通过它自己的定义来解析：`mypack:materials:5` 变成 `meta` 为 5 的那个变种，而 `mypack:ruby_ore` 变成文件的第一个变种，因为在这里每个变种都是独立的方块。实体和生物群系名称以同样方式修复，维度编号则变成 id。
- `variants` 保留它们的键；`meta` 被丢弃，`oreDict` 通过矿物词典对约定标签的映射变成 `tags`。`oredict/*.json` 文件会按它添加或移除的每个名称变成一个物品标签文件：`-name` 形式的移除会落入该标签的 `remove` 列表，移除 `*` 则替换该标签。单独的 `creativeTab` 会采用资源包的命名空间，而 `misc` 这样的 1.12.2 原版标签页名称会变成最接近的原版标签页。
- `.lang` 文件会作为游戏所读取的 `.json` 提供，`tile.mypack:file.variant.name` 变为 `block.mypack.variant`，`item.` 同理，`itemGroup.x` 变为 `itemGroup.mypack.x`，`fluid.x` 变为两个流体键，其余一切照原样保留。
- 1.12.2 的方块状态完全不会被提供。取而代之的是读取它的纹理，并按生成器所查找的名称提供，即 `textures/block/<variant>.png`，在方块状态中有 `end`、`top` 或 `bottom` 的地方带有 `_top` 和 `_bottom`，所以每个变种的方块状态和模型都会像为此版本编写的资源包那样生成。
- 配方会丢掉它们的 `data` 并获得扁平化的 id，`forge:ore_shaped` 变成 `minecraft:crafting_shaped`，其中的 `ore` 原料变为 `tag`，战利品表同样会丢掉 `set_data`，进度的 `item` 与 `data` 会变成 `items`。进度的 `background` 从 `textures/blocks/` 移到 `textures/block/`，其图标以 `id` 指明物品。
- 配方的 1.12.2 Forge 词汇也会一并转换：`forge:ore_dict` 或 `minecraft:item` 原料类型会被丢弃，`minecraft:item_nbt` 原料变成 `forge:nbt`（1.21.1：`neoforge:components`），其 nbt 经过数据修复器处理，`minecraft:item_exists` 变成 `forge:item_exists`（1.21.1：每个条件都采用其 `neoforge` 名称，位于 `neoforge:conditions` 之下），没有命名空间的物品采用配方的命名空间，`data` 为 32767 的会变成包含每个变种的列表。模组的 `_constants.json` 中的 `#CONSTANT` 无法一并转换，日志会点名它。凡是列出物品的地方，`name:*` 都会变成该物品曾有的每个变种，而单个值则取第一个；这涉及 `recipe_removals` 的产物和 `furnace` 的移除，它们同样会被当作物品来转换。铁砧的 `item` 或 `with` 写成 `name:*` 时，会变成包含每个变种的列表，其中任何一个都可以匹配。
- 自 1.12.2 以来被重命名的战利品表，凡是资源包点名之处都会被重命名：`loot_injections` 的目标、`player_loot` 的表以及 `loot_table` 条目（1.21.1：其 `value`），所以 `minecraft:entities/zombie_pigman` 变成 `minecraft:entities/zombified_piglin`。带 `inverse` 的 `killed_by_player` 变成 `inverted` 条件，带 `on_fire` 的 `entity_properties` 变成 `flags` 谓词，`generic.maxHealth` 这样的 `set_attributes` 名称变成 `generic.max_health`（1.21.1：运算采用新名称，修饰符的 `name` 变成它的 `id`）。
- 对被扁平化拆分的 1.12.2 方块的覆盖，例如 `overrides/minecraft/wool.json`，会被当作对它变成的每个方块的覆盖，即全部十六种羊毛，因为 1.12.2 一次改动了每个变种。
- 游戏规则文件的 `gameLoopFunction` 会变成 `#minecraft:tick` 函数标签，写为 `data/minecraft/tags/function/tick.json`，因为该游戏规则已不存在。资源包自己的维度编号通过它的 `dimensions` 文件读取，所以 `dimensions/verdant.json` 中的 `"id": 7` 会使资源包点名它的每个地方都把 7 当作 `mypack:verdant`。`villageBlocks` 对的两侧都会被修复，概率保持不变，`registry_remap` 文件可以保留 1.12.2 的复数形式 `minecraft:blocks` 和 `minecraft:items`。
- 如果世界模板关闭了 1.12.2 在某个维度中拥有的每一种结构，则只有此版本才有的结构在那里也会被关闭：主世界中的 `ancient_cities`、`buried_treasures`、`ocean_ruins`、`pillager_outposts`、`ruined_portals`、`shipwrecks` 、`trail_ruins` 和 `trial_chambers`，以及下界中的 `nether_fossils`。只要留着一个 1.12.2 的名称开启，它们就不会被动。生成器控制键 `blockWorldGenerators` 及其同类，会被排除在转换后的模板之外，并在日志中留下一行，因为这里没有任何东西读取它们。平坦模板的 `generatorOptions` 图层的方块名称会以同样的方式修复，所以 `minecraft:grass` 变成 `minecraft:grass_block`。
- 函数会被逐行改写成此版本的命令语法。带数据值的 id 会经过同样的数据修复器，所以 `give @p minecraft:wool 1 14` 变成 `give @p minecraft:red_wool 1`，`give @p mypack:materials 1 5` 给出 `meta` 为 5 的那个变种，物品、实体和方块的 nbt 会像世界中的那样被修复（1.21.1：物品的 nbt 变成它的组件）。`testforblock`、`testfor` 和 `scoreboard players test` 变成 `execute if`，`execute <entity> <x> <y> <z> [detect ...]` 变成 `execute as ... at @s [positioned ...] [if block ...] run`，`effect` 采用 `give` 和 `clear`，`blockdata`、`entitydata` 和 `replaceitem` 变成 `data merge` 和 `item replace`，`scoreboard teams` 和 `scoreboard players tag` 变成 `team` 和 `tag`。选择器把 `score_X_min` 和 `score_X` 换成 `scores`，把 `r` 和 `rm` 换成 `distance`，把 `l` 和 `lm` 换成 `level`，把 `m` 换成 `gamemode`，把 `c` 换成 `limit` 和 `sort`，把 `rx` 和 `ry` 换成 `x_rotation` 和 `y_rotation`。附魔和效果的编号、粒子和声音名称、游戏模式和难度的编号、以秒为单位的 `weather` 持续时间，以及对另一个实体的相对 `tp`，同样会被转换。转换无法处理的行会保持原样，日志会点名文件、行以及原因；含有这种行的函数在手动修复之前不会加载。`block_drops` 照原样转换，其 `meta` 折算进方块名称或它的 `properties`。
- 1.12.2 的平坦世界，其地面位于 y 0，而此版本从底部 y -64 起铺设平坦世界，所以转换会把高度相应下移。当世界模板的 `worldType` 为 `flat` 或 `superflat` 时，它的 `worldSpawn` 和 `resetSendsTo` 会下移 64（若它指定了 `worldMinHeight`，则下移到该值），当资源包附带的每个世界模板都是平坦时，队伍的 `spawn`、`standIn` 的 `at` 与 `spawnBox` 的高度，以及计分文件的 `opens.lobby` 也同样下移。带有 `flat` 地形的资源包维度会下移它的 `groundLevel`，以及任何指向该维度的位置也同样处理（若它有 `minHeight`，则按该值）。函数并不说明它们在哪里运行，所以当资源包的主世界是平坦的时，其函数中的每个绝对 y 都会下移 64，包括选择器的 `y`，并且日志会说明一次；`~` 和 `^` 的高度则不动。处于普通地形上的世界保留每一个坐标，因为它的地表仍在海平面。

日志中，每个被转换的资源包有一行汇总，被移动、被排除或无法转换的每个文件各有一行，而此版本不再读取的每个键，仍然由遇到它的解析器点名。转换只是尽力而为，并不是一个成品资源包：请阅读这些日志行，并手动完成它们所点名的工作，首先处理它原样保留的任何命令行，以及它找不到名称的任何纹理。请在资源包根目录或单独的 zip 中进行这些修复，绝不要在转换所写入的 `versions/` 文件夹中进行：RDPL 的另一个版本会重新写入该文件夹。

---

# 方块与物品

## 方块

*方块与物品*

`<namespace>/blocks/*.json`

`variants` 里的每个键都是一个方块，注册在资源包的命名空间之下：一个包含 `ruby_ore` 和 `deep_ruby_ore` 的文件会注册 `mypack:ruby_ore` 和 `mypack:deep_ruby_ore`，共用文件在 `variants` 之外写出的每项设置。文件本身的名称只是一个分组。

一次展示每个键。真正的文件只写它需要的那些。标明为某一类型专用的键，只会被该类型读取。

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

### 类型

*方块*

| 类型         | 你得到什么                                                                                                                                                                                                                                            |
| ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `basic`      | 普通方块。`type` 缺失时使用                                                                                                                                                                                                                           |
| `ore`        | 掉落自身以外的东西，并支持时运和精准采集                                                                                                                                                                                                              |
| `falling`    | 像沙子或沙砾那样下落                                                                                                                                                                                                                                  |
| `slab`       | 下半、上半和双层，手持两个可以合并                                                                                                                                                                                                                    |
| `stairs`     | 转角和斜坡自动处理                                                                                                                                                                                                                                    |
| `fence`      | 与相邻方块连接，也与其他模组的栅栏连接                                                                                                                                                                                                                |
| `pane`       | 像玻璃板那样连接                                                                                                                                                                                                                                      |
| `wall`       | 像圆石墙那样连接，带有墙柱形状                                                                                                                                                                                                                        |
| `door`       | 两格高，可手动开启，并响应红石                                                                                                                                                                                                                        |
| `trapdoor`   | 位于方块顶部或底部的带铰链的活板，可手动或用红石开启                                                                                                                                                                                                  |
| `fence_gate` | 栅栏线中的一扇门，可手动或用红石开启，并在与墙相接处降低                                                                                                                                                                                              |
| `banner`     | 立在柱子上或挂在墙上的旗帜，有十六种站立朝向，带有你自己的图案                                                                                                                                                                                        |
| `ladder`     | 可攀爬，贴着墙放置                                                                                                                                                                                                                                    |
| `torch`      | 墙面和地面放置，带有粒子。按原样发出该变种的 `light`，所以 `0` 的火把不发光                                                                                                                                                                           |
| `bell`       | 与游戏中村庄里的钟一样：侧面使用、被红石触发或被抛射物击中时会响，在框架中摆动，并使附近的袭击者发光。它的方块状态记录朝向以及悬挂方式                                                                                                                 |
| `log`        | 朝向放置时所贴的那一面旋转，并带有 `minecraft:logs` 标签，使砍树和 Blast Plaster 把它当作树干                                                                                                                                                         |
| `leaves`     | 会腐烂、可用剪刀剪取、会着色并掉落树苗，并带有 `minecraft:leaves` 标签。保持 `opaque` 时，它们绘制为实心，如同快速树叶；设置 `"opaque": false` 可以透视它们                                                                                           |
| `sapling`    | 长成一棵树，或长成你的某个结构                                                                                                                                                                                                                        |
| `crop`       | 分阶段生长，掉落种子和产物物品；文件中省略哪一项，就用小麦种子和小麦代替                                                                                                                                                                              |
| `flower`     | 站在土壤上的单格植物                                                                                                                                                                                                                                  |
| `cane`       | 像甘蔗或仙人掌那样向上成列生长                                                                                                                                                                                                                        |
| `vine`       | 攀附并悬挂在方块的侧面。带 `growth` 时，它向下生长直到 `maxHeight`，带 `spread` 时还会向侧面延伸到相邻的墙上；没有 `growth` 时，它保持放置时的样子                                                                                                    |
| `portal`     | 把走进去的东西送往另一个维度                                                                                                                                                                                                                          |
| `container`  | 持有玩家可以打开的物品栏，大小任意，并可在第一次被打开时用战利品表自行填充。绘制为普通方块或箱子，由资源包要求而定。没有 `container` 对象时，它是三行九列 |

### 文件键

*方块*

| 键                           | 必需           | 值                                                | 默认值                                                                                                       | 作用                                                                                                                                                                                                                                                                                                                                                                                                                |
| ---------------------------- | -------------- | ------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `variants`                   | yes            | object of variant name to variant                 |                                                                                                              | 每个条目一个方块。键就是它的注册名，并决定它的方块状态、模型、纹理和语言键的名称                                                                                                                                                                                                                                                                                                                                    |
| `type`                       | no             | one of the types above                            | `basic`                                                                                                      | 方块采用哪种形态                                                                                                                                                                                                                                                                                                                                                                                                    |
| `material`                   | no             | one of the [block materials](#值列表)             | `rock`                                                                                                       | 方块的表现与该材料在 1.12.2 中一致：徒手破坏时是否掉落东西、活塞如何对待它、熔岩是否会将其点燃、流动的液体是否会将其冲走，以及放置的方块是否会替换它。`log` 始终是 `wood`，`leaves` 始终是 `leaves`，`vine` 是 `vine`，`torch` 或 `ladder` 是 `circuits`，`crop` 是 `plants`，而 `stairs` 和 `wall` 的表现与其 `modelBlock` 相同                                                                                   |
| `soundType`                  | no             | one of the [sound types](#值列表)                 | `stone`；`log` 为 `wood`，`leaves` 和 `crop` 为 `plant`，`stairs` 和 `wall` 为其 `modelBlock` 的            | 脚步声、破坏声和放置声                                                                                                                                                                                                                                                                                                                                                                                              |
| `mapColor`                   | no             | one of the [map colors](#值列表)                  | from the material                                                                                            | 它在地图上的外观                                                                                                                                                                                                                                                                                                                                                                                                    |
| `harvestTool`                | no             | `pickaxe`, `axe`, `shovel`, `hoe`, `sword`        | `pickaxe`                                                                                                    | 用哪种工具采集它，系统会替你写入游戏的 `mineable` 标签；`sword` 会写入 `resourcedatapackloader:mineable/sword`，资源包自己的 `sword` 工具可以挖掘它。对于掉落物，只有在需要工具的材料上才有影响。与 1.12.2 一样，镐也能以全速挖掘 `rock`、`iron` 和 `anvil`，斧能以全速挖掘 `wood`、`plants` 和 `vine`。任何其他名称，例如 `shears`，都会被记入日志并被忽略                                                           |
| `harvestToolLevel`           | no             | 0 to 4                                            | `0`                                                                                                          | 0 木、1 石、2 铁、3 钻石、4 下界合金，系统会替你写入 `needs_*_tool` 标签。`basic`、`ore`、`container`、`portal`、`fence`、`pane`、`log`、`falling`、`slab` 或 `wall` 方块会忽略它，并采用变种的 `harvestLevel`，与 1.12.2 一样                                                                                                                                                                                     |
| `silkHarvest`                | no             | boolean                                           | `true`                                                                                                       | 精准采集是否返回方块本身                                                                                                                                                                                                                                                                                                                                                                                            |
| `opensWith`                  | no             | item id                                           | none                                                                                                         | 使该方块成为一个锁箱：破坏它会掉落方块本身，用指定的物品右键会消耗一个，播放该方块的破坏声，发放该变种的 `drops` 列表，并移除该方块。其他任何点击都会在动作栏显示语言文件中的 `block.<pack>.<block>.locked` 一行                                                                                                                                                                                                     |
| `openSound`                  | no             | sound name                                        | the break sound                                                                                              | 锁箱被打开时播放的声音，取代它的破坏声。1.12.2 的名称仍可读取，参见[声音名称](#值列表)                                                                                                                                                                                                                                                                                                                              |
| `expDrop`                    | no             | object with `min` and `max`                       | none                                                                                                         | 玩家破坏该方块、或收集经验的资源包生物挖掘它时掉落的经验；活塞、水和爆炸不会掉落经验。只有在 `silkHarvest` 开启时，精准采集才会使其消失                                                                                                                                                                                                                                                                              |
| `creativeTab`                | no             | tab name                                          | none                                                                                                         | 它出现在哪个标签页中，参见[创造模式标签页](#创造模式标签页)                                                                                                                                                                                                                                                                                                                                                         |
| `renderLayer`                | no             | `solid`, `cutout`, `cutout_mipped`, `translucent` | to suit the type                                                                                             | 它如何绘制                                                                                                                                                                                                                                                                                                                                                                                                          |
| `opaque`                     | no             | boolean                                           | `true`                                                                                                       | 它是否完全阻挡视线和光线                                                                                                                                                                                                                                                                                                                                                                                            |
| `fullCube`                   | no             | boolean                                           | same as `opaque`                                                                                             | 它是否填满整个空间                                                                                                                                                                                                                                                                                                                                                                                                  |
| `lightOpacity`               | no             | 0 to 255                                          | `255` when opaque, else `0`                                                                                  | 它吸收多少光：15 或更高会挡住全部光线，`0` 则让阳光直接穿过。`slab` 保持游戏自己的值，箱子模型的容器则让光线透过                                                                                                                                                                                                                                                                                                    |
| `slipperiness`               | no             | float                                             | `0.6`                                                                                                        | 冰是 `0.98`                                                                                                                                                                                                                                                                                                                                                                                                         |
| `flammability`               | no             | int                                               | `0`                                                                                                          | 火焰烧毁它的难易程度                                                                                                                                                                                                                                                                                                                                                                                                |
| `fireSpread`                 | no             | int                                               | `0`                                                                                                          | 火焰从它蔓延出去的难易程度                                                                                                                                                                                                                                                                                                                                                                                          |
| `explosionResistanceDivisor` | no             | float                                             | `1.0`                                                                                                        | 用来除每个变种的 `resistance`，以得到其抗爆炸能力                                                                                                                                                                                                                                                                                                                                                                   |
| `modelBlock`                 | no             | block name                                        | `minecraft:stone`                                                                                            | 当你的方块没有自带纹理和模型时，借用其模型的方块                                                                                                                                                                                                                                                                                                                                                                    |
| `itemModel`                  | no             | `state`, `item`                                   | `state`                                                                                                      | `state` 跟随方块状态，`item` 查找它自己的文件，即 `models/item/<name>.json`                                                                                                                                                                                                                                                                                                                                         |
| `tint`                       | no             | `biome`, `none`, or a hex color                   | none                                                                                                         | 需要模型中有 `tintindex` 才会显示                                                                                                                                                                                                                                                                                                                                                                                   |
| `plantTypes`                 | no             | list of [plant types](#值列表)                    | none                                                                                                         | 可以种在它上面的东西                                                                                                                                                                                                                                                                                                                                                                                                |
| `behavesAs`                  | no             | list of `till`, `path`, `bush`, `animals`         | none                                                                                                         | 要采用的原版行为                                                                                                                                                                                                                                                                                                                                                                                                    |
| `bounds`                     | no             | list of six numbers, 0 to 1                       | full block                                                                                                   | 碰撞箱，写作 `[x1, y1, z1, x2, y2, z2]`                                                                                                                                                                                                                                                                                                                                                                             |
| `requires`                   | no             | list of mod ids or pack namespaces                | none                                                                                                         | 除非全部存在，否则该文件会被跳过                                                                                                                                                                                                                                                                                                                                                                                    |
| `particle`                   | torch only     | `none`, `flame`, `colored`                        | `flame`                                                                                                      | 火把上方的粒子                                                                                                                                                                                                                                                                                                                                                                                                      |
| `particleColor`              | torch only     | hex color                                         | `FFFFFF`                                                                                                     | 当 `particle` 为 `colored` 时使用                                                                                                                                                                                                                                                                                                                                                                                   |
| `smoke`                      | torch only     | boolean                                           | `true`                                                                                                       | 是否冒烟                                                                                                                                                                                                                                                                                                                                                                                                            |
| `leafSapling`                | leaves only    | block name                                        | none                                                                                                         | 它们掉落的树苗                                                                                                                                                                                                                                                                                                                                                                                                      |
| `leafSaplingChance`          | leaves only    | int                                               | `5`                                                                                                          | 每 N 个树叶方块有一个会掉落                                                                                                                                                                                                                                                                                                                                                                                         |
| `seed`                       | crop only      | item name                                         | `minecraft:wheat_seeds`                                                                                      | 用来种植它的物品，也是未成熟作物掉落的东西                                                                                                                                                                                                                                                                                                                                                                          |
| `produce`                    | crop only      | item name                                         | `minecraft:wheat`                                                                                            | 收获得到的东西                                                                                                                                                                                                                                                                                                                                                                                                      |
| `maxAge`                     | crop only      | int                                               | `7`                                                                                                          | 有多少个生长阶段                                                                                                                                                                                                                                                                                                                                                                                                    |
| `growth`                     | plants only    | object                                            | none                                                                                                         | 参见[生长](#生长)                                                                                                                                                                                                                                                                                                                                                                                                   |
| `sapling`                    | sapling only   | object                                            | none                                                                                                         | 参见[树苗](#树苗)                                                                                                                                                                                                                                                                                                                                                                                                   |
| `portal`                     | portal only    | object                                            | none                                                                                                         | 参见[传送门与门](#传送门与门)                                                                                                                                                                                                                                                                                                                                                                                       |
| `container`                  | container only | object                                            | none                                                                                                         | 参见[容器](#容器)                                                                                                                                                                                                                                                                                                                                                                                                   |
| `bell`                       | bell only      | object                                            | none                                                                                                         | 参见[钟](#钟)                                                                                                                                                                                                                                                                                                                                                                                                       |

### 变种键

*方块*

| 键             | 必需        | 值                                   | 默认值       | 作用                                                                                                                                                                                               |
| -------------- | ----------- | ------------------------------------ | ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hardness`     | no          | float                                | `1.0`        | 破坏它需要多长时间。黑曜石是 `50`，`-1` 表示不可破坏                                                                                                                                               |
| `resistance`   | no          | float                                | `5.0`        | 按 1.12.2 的读法确定的爆炸抗性：方块保留该数值的五分之三，所以 `10` 得到石头的 `6`                                                                                                                 |
| `light`        | no          | 0 to 15                              | `0`          | 发出的光                                                                                                                                                                                           |
| `harvestLevel` | no          | 0 to 4                               | `0`          | `basic`、`ore`、`container`、`portal`、`fence`、`pane`、`log`、`falling`、`slab` 或 `wall` 方块的工具等级，取代文件的 `harvestToolLevel`。其他类型采用文件的值                                     |
| `rarity`       | no          | `common`, `uncommon`, `rare`, `epic` | `common`     | 工具提示中名称的颜色                                                                                                                                                                               |
| `maxSize`      | no          | 1 to 64                              | `64`         | 堆叠上限                                                                                                                                                                                           |
| `tags`         | no          | list of tag ids                      | none         | 该变种被写入的方块和物品标签，例如 1.20.1 上的 `forge:ores/ruby` 或 1.21.1 上的 `c:ores/ruby`。标签文件会替你生成                                                                                  |
| `drops`        | no          | list of drops                        | drops itself | 破坏它得到什么                                                                                                                                                                                     |
| `portal`       | portal only | object                               | the file's   | 该变种自己的传送门，取代文件的传送门，写法见[传送门与门](#传送门与门)。文件本身仍然需要有它自己的一个                                                                                               |

**名称是永久的。** 变种的键会写入每个包含它的已保存世界。之后重命名它，会把已放置的方块变成空气，除非用[注册表重命名](#注册表重命名)把旧名称映射到新名称。一个文件可以包含任意多个变种；每个都是独立的方块，而 1.12.2 资源包中的 `meta` 键会被忽略，并在日志中留下一条说明。

### 掉落物

*方块*

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

| 键            | 必需           | 值                 | 默认值                                 | 作用                                                                       |
| ------------- | -------------- | ------------------ | -------------------------------------- | -------------------------------------------------------------------------- |
| `block`       | one of the two | block or item name |                                        | 掉落什么                                                                   |
| `entity`      | one of the two | entity name        |                                        | 方块被破坏时放出的实体，取代物品                                           |
| `amount`      | no             | int or range       | `1`                                    | 多少个                                                                     |
| `chance`      | no             | 0 to 100           | `100`, or `0` when `guaranteed` is off | 该掉落究竟发生的频率                                                       |
| `weight`      | no             | int                | `0`                                    | 大于零时，该条目加入一个恰好产出一个掉落物的池。见下文                     |
| `bonusChance` | no             | list of ints       | none                                   | 每个时运等级的额外掉落，每个等级一个条目                                   |
| `guaranteed`  | no             | boolean            | `true`                                 | `chance` 的旧式简写。开启为 `100`，关闭为 `0`                              |

每个没有 `weight` 的条目都是独立判定的，所以有三个这样条目的方块，可能三个都掉落，也可能一个都不掉。给条目加上 `weight`，它们就不再独立：它们组成一个池，每次方块被破坏时恰好从中选出一个，几率与权重成比例。上面的例子里，钻石和绿宝石以一比四共用一个池，所以两者之一一定会掉出来，并且五次中有四次是绿宝石，而红宝石和煤炭是各自单独判定的，那只蠹虫则又是另外一回事。物品和实体分别成池，所以带权重的物品和带权重的实体彼此不竞争。

指定了 `entity` 的条目，会在方块所在的位置放出一个，朝向随机，并且生物会按当地难度获得它通常的生成处理，所以它出现时带有它本该有的装备和效果。`amount` 决定数量，`chance` 决定频率，`weight` 把它放进实体池。它发生在方块被破坏的那一刻，无论是怎么破坏的，所以爆炸或活塞放出它们的效果与镐一样。`bonusChance` 和时运对实体毫无意义，会被忽略。

同时指定 `block` 和 `entity` 的掉落会使用实体，并在日志中说明。

这些掉落物会写入一张生成的战利品表，即资源包命名空间下的 `loot_tables/blocks/<name>.json`；如果资源包在该路径下自带了一张，则方块使用资源包的文件，不会读取 `drops`。

### 生长

*方块*

适用于 `crop`、`flower`、`cane` 和 `vine`。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `stages` | 否 | int | `16` | 完全长成前的生长阶段数。藤蔓在每个随机刻内有 1/该数值 的机会尝试生长 |
| `growth` | 否 | int | | 每个随机刻有 1/N 的几率推进一个阶段 |
| `spread` | 否 | int | `0` | 向相邻方块扩散的范围。当两格范围内已有这么多藤蔓时，藤蔓就不再向侧面延伸 |
| `maxHeight` | 否 | int | `3` | 用于 cane 和 vine。柱体能长多高，或藤蔓向下垂多远；`1` 的藤蔓既不生长也不扩散 |
| `soil` | 否 | 方块名称列表 | 该类型的常规值 | 它所立足的方块 |
| `drop` | 否 | 物品名称 | 无 | 用于 cane 和 vine。被破坏时掉落的物品；花朵掉落其自身 |
| `dropCount` | 否 | int | `1` | 用于 cane 和 vine。掉落数量 |
| `needsSky` | 否 | boolean | `false` | 只在能看到天空的地方生长 |
| `needsWater` | 否 | boolean | `false` | 只在水附近生长 |
| `waterRange` | 否 | int | `1` | 水最远可以离多远 |
| `damage` | 否 | boolean | `false` | 伤害接触它的一切 |
| `damageAmount` | 否 | float，半颗心 | `1.0` | 伤害多少 |
| `breaksNeighbors` | 否 | boolean | `false` | 像仙人掌一样，破坏放在它旁边的方块 |

### 树苗

*方块*

一次性展示所有键。实际文件只写需要的键。

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

`structure` 会用你的某个模板取代生成的树，这是构造生成器做不出的东西的方法，方块中其余的内容都无需再写。若改在 `structures` 下列出多个，树苗每次生长时会从中选一个，这样一片树林就不会总是同一棵树：

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `soil` | 否 | 方块名称列表 | 无 | 它能在什么方块上生长 |
| `stages` | 否 | int | `2` | 长成树之前的生长阶段数 |
| `chance` | 否 | int | `7` | 每个随机刻有 1/N 的几率 |
| `light` | 否 | 0 到 15 | `9` | 所需的光照等级 |
| `log` | 否 | 方块名称 | `minecraft:oak_log` | 树干方块 |
| `leaves` | 否 | 方块名称 | `minecraft:oak_leaves` | 树叶方块 |
| `height` | 否 | int | `4` | 树干高度 |
| `vines` | 否 | boolean | `false` | 从树叶上垂下藤蔓 |
| `structure` | 否 | `namespace:name` | 无 | 长成这个模板，而不是生成的树 |
| `structures` | 否 | 列表 | 无 | 多个可长成的模板，每次生长时选其一。每个条目写作 `{ "structure": "namespace:name", "weight": 3 }`，或只写名称表示几率相等。会覆盖 `structure` |

## 容器

*方块与物品*

`<namespace>/blocks/*.json`、`<namespace>/items/*.json`

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `rows` | int | `3` | 槽位的行数，1 到 9 |
| `columns` | int | `9` | 每行的槽位数，1 到 12 |
| `lootTable` | text | 空 | 第一次有任何东西接触其内容物时（玩家打开、漏斗、比较器或将其破坏）会向方块中投放的战利品表，与地牢箱子的填充方式完全一致。玩家自己放置的容器不会触发。留空则保持空容器 |
| `chestModel` | boolean 或 text | `false` | 绘制为带有可开启盖子的箱子，而不是用你自己纹理绘制的普通方块。`true` 使用原版箱子的贴图；像 `mypack:entity/chest/strongbox` 这样的纹理名称则改用你自己的箱子贴图表，放置的方块和物品都是如此。箱子模型方块还会像原版箱子一样将 `opaque` 默认为 `false`，这样光线不会在方块处被截断，箱子也不会被画得发暗 |
| `guiTexture` | text | 空 | 界面所用的你自己的背景图片。留空则按行列所需的尺寸，用原版箱子界面拼出一个 |
| `guiWidth` | int | 无 | 界面的宽度，从该图片的左上角起，按 256 乘 256 的贴图表读取，使用 `guiTexture` 时必填 |
| `guiHeight` | int | 无 | 界面的高度，使用 `guiTexture` 时必填 |
| `curioSlot` | text | 空 | 仅用于物品：它可以佩戴的 Curios 槽位，`back`、`belt`、`body`、`charm`、`head`、`necklace`、`ring` 或其他模组添加的任何槽位。背包通常使用 `back`。未安装 Curios 时会被忽略，物品的其余功能照常工作。1.12.2 的键 `bauble` 会按此键读取，并采用 Baubles 的名称：`amulet` 变为 `necklace`，`ring` 提供两个戒指槽，`belt`、`head`、`body` 和 `charm` 保持原名，`trinket` 适用于以上所有槽位。其他任何名称都会让物品无法佩戴，并输出一条错误日志 |

**九行乘十二列是上限**，也是一个界面能容纳的最大值。资源包要求更大时会被裁到这个上限，并输出一条错误日志说明。关于最高的尺寸有一点提醒：九行的界面高 276 像素，而 1080 显示器在 GUI 缩放为 `auto` 时只有 270，因此顶部和底部各被裁掉三像素；缩放设为 3 时可完整显示。

**界面是绘制出来的，而不是随包附带的。** 九列及以内、六行及以内的容器直接使用原版箱子界面，看起来与该尺寸的箱子完全相同。更大的尺寸则在绘制时由同一张图片拼装而成：顶边、按需重复的一行槽位，以及带有玩家自身物品栏的底部，因此资源包可以要求原版界面未涵盖的尺寸，而无需附带自己的图片。`guiTexture` 会在资源包想要自己外观的地方覆盖以上所有内容，此时 `guiWidth` 和 `guiHeight` 必须说明它有多大，否则会改用绘制出的界面，并输出一条错误日志。

**方块的行为。** 它在存档和重新加载后保留其内容物，破坏时将其掉落，用比较器读取时按装满程度输出信号，并保留创建时的行数和列数，因此之后在资源包中修改它们，不会影响已在世界中的容器。容器物品或容器方块不能放进容器里。`chestModel` 还会赋予它箱子的开启音效和盖子动画；关闭时，方块像其他方块一样用自己的纹理绘制，因此板条箱、木桶或柜子都可以做。

**给箱子上色。** 箱子贴图表是普通纹理，因此像素图无需绘制任何像素就能为原版箱子重新着色：对它 `extends` 并给出 `tint`，再在 `chestModel` 中写这张图的名称。

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

**容器物品是一个小袋**，即类型为 `container` 的物品，携带同样的 `container` 块，含 `rows` 和 `columns`；右键打开，并且在易手时保留其内容物。没有 `container` 对象时，它只有一行九格。给它 `curioSlot`，在安装了 Curios 的情况下，它会放入该槽位，并且可以用一个按键在不取下的情况下打开，默认是 `V`，可在控制设置中的 Resource Data Pack Loader 下重新绑定。已打开一个佩戴的容器时再次按下，会切换到你佩戴的下一个并循环，因此同时佩戴多个时都能打开。该按键只在有 Curios 时出现，而物品的其他一切（右键和它的物品栏）无论有没有 Curios 都能工作。小袋的可佩戴性会自动写入该槽位的 Curios 标签。

**战利品表在首次使用时填充**，而不是在方块放置时，这使它在结构中很有用：第一个打开它的人获得这次投放，而先接触它的漏斗或比较器同样会触发投放。同一张表也可以用于印记形状或村庄地块上的 `lootTable`，因此资源包可以通过世界生成放置这些容器，并以同样的方式为它们补充物品。

## 钟

*方块*

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

以及它的方块状态 `assets/mypack/blockstates/village_bell.json`，以 `attachment` 和 `facing` 为键：

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `swing` | boolean | `true` | 用单独的模型文件绘制摆动的部分，并在钟响时让它摆动。`false` 则完全用方块状态中的模型绘制整只钟，没有任何动画 |
| `sound` | 音效名称 | `minecraft:block.note_block.bell` | 钟响时播放。留空则无声 |
| `resonateSound` | 音效名称 | `minecraft:block.note_block.chime` | 因附近有袭击者而共鸣时播放。留空则共鸣无声 |

**它的悬挂方式与游戏自带的钟相同。** 放在方块顶部时立在地面上，朝向你面对的方向；放在方块下方时挂在天花板上；靠着墙放置时挂在那面墙上，而当对侧也是实心方块时则挂在两面墙之间。支撑它的方块消失时它会掉落，而两面墙之间的钟在其中一面墙消失后会变成单墙钟。四种形态的碰撞箱都遵循原版，因此不读取 `bounds`。

**什么能敲响它。** 使用钟体的侧面，位于横梁下方：地面钟是其横梁所跨的两个面，墙钟是紧贴墙的两个侧面，天花板钟则是任意一侧。顶部、底部和钟体以上的任何位置都没有效果。红石信号在接通的瞬间敲响一次，箭、雪球或任何其他投射物击中手能够碰到的一侧时也会敲响。钟体会从被击中的一侧摆开，持续两秒半；红石则让它沿钟所朝的方向摆动。

**敲钟的效果。** 32 格内的村民听到后会跑回家躲藏十五秒，与游戏自带的钟让它们躲藏的方式一样。当 32 格内有袭击者时，钟会在敲响后四分之一秒共鸣，两秒后 48 格内的每个袭击者会发光三秒，并在钟旁各自所站的一侧产生彩色粒子。袭击者包括 [袭击](#袭击) 派出的一切，以及游戏自带的灾厄村民和女巫。这种类型的钟对每场袭击来说都是村庄钟：每一波到来时它都会响，无需在该袭击的 `bell` 中指名。

**模型。** 钟以 `attachment` 和 `facing` 为键，共十六种状态，`powered` 不在键中。启用 `swing` 时，这些模型只绘制框架，而摆动部分是单独的模型文件 `<namespace>:block/<name>_body`：模组按该路径加载它并由渲染器绘制，因此这个名称不是你能选的，也不会出现在方块状态中。它建模在钟体所处的方块空间中，并绕着向内半格、向上四分之三格的点倾斜，与原版相同。关闭 `swing` 时没有钟体模型，十六个框架模型绘制完整的钟。为手持而写的物品模型把框架和钟体一起绘制，所以钟在手中显示为完整的；若想以别的方式绘制，请附带 `models/item/<name>.json`。

**摆动由客户端绘制。** 敲钟以方块事件的形式传达给玩家，因此专用服务器会让每个装有本模组的玩家看到钟摆动，而没有本模组的玩家只会听到钟声。音效、共鸣和发光都发生在服务端。

## 模型、方块状态与纹理

*方块与物品*

定义一个方块或物品就是注册它。它*看起来*如何则由一组资源文件决定，这些文件放在与游戏相同的文件夹中，格式也与游戏相同，位于你自己的命名空间之下，而在这个版本上，其中大部分都会自动为你写好。

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**附带一张纹理，其余的都会生成。** 对于资源包没有附带方块状态的每个方块，模组会写出该类型所需的方块状态和模型，指向 `textures/block/<name>.png`，其中 `<name>` 是变种的键；对于没有 `models/item/<name>.json` 的每个物品，则生成指向 `textures/item/<name>.png` 的物品模型。既没有纹理也没有自己模型的方块会借用 `modelBlock` 的外观，默认是石头，因此永远不会渲染成紫黑色方块。附带你自己的 `blockstates/<name>.json`，模组就不会为该方块生成任何东西而使用你的；`models/item/<name>.json` 同理。

| 类型 | 它查找的纹理文件 | 生成自 |
| --- | --- | --- |
| `basic`、`ore`、`falling` | `<name>`，资源包附带时，顶面和底面使用 `<name>_top` 和 `<name>_bottom` | `cube_all`，或存在顶部或底部纹理时的 `cube_bottom_top` |
| `flower`、`sapling`、`cane`、`leaves`、不带箱子的 `container` | `<name>` | `cube_all`、`cross` 或 `leaves` |
| `log` | 侧面用 `<name>`，两端用 `<name>_top` | `cube_column` |
| `slab` | `<name>` | `slab`、`slab_top` 和一个 `cube_all` 的双层 |
| `stairs` | `<name>` | `stairs`、`inner_stairs`、`outer_stairs`，全部四十种状态都写出 |
| `fence` | `<name>` | 以 multipart 形式的 `fence_post` 和 `fence_side`，以及手持用的 `fence_inventory` |
| `wall` | `<name>` | 以 multipart 形式的墙柱和墙侧模板，以及手持用的 `wall_inventory` |
| `pane` | 玻璃板用 `<name>`，边缘用 `<name>_top` | 以 multipart 形式的五个玻璃板模板 |
| `door` | `<name>_top` 和 `<name>_bottom`，或用 `<name>` 同时表示两者 | 八个门模型及其三十二种状态 |
| `trapdoor` | `<name>` | 三个可定向的活板门模型 |
| `fence_gate` | `<name>` | 四个栅栏门模型，关闭与开启，贴墙与不贴墙 |
| `ladder`、`vine`、`torch` | `<name>` | 游戏自带的各自模板 |
| `bell` | `<name>` | 游戏自带的钟框架，分别为 `<name>_floor`、`<name>_ceiling`、`<name>_wall` 和 `<name>_between_walls`，模组的 `pack_bell_body` 作为 `<name>_body`，以及覆盖这些模型的十六个 `attachment` 和 `facing` 状态的方块状态 |
| `crop` | `<name>_stage0` 直到 `<name>_stage<maxAge>`，或用 `<name>` 表示全部 | 每个阶段一个 `crop` 模型，`age=0` 到 `7` 映射到它们 |
| `portal` | `<name>`，或下界传送门的纹理 | 对于 `fullCube` 方块（如 1.12.2 中的传送门方块）为 `cube_all`；对于不是的（例如某个维度的框架传送门），则是每个轴一个的三个传送门半砖 |
| `banner` | 它自己的贴图表，见 [旗帜](#旗帜) | 游戏的旗帜模型 |
| 带有 `chestModel` 的 `container` | `chestModel` 中指定的箱子贴图表 | 模组的 `pack_chest` 模型 |

所有纹理都在 `textures/block/` 下查找，名称就是变种的键，因此注册为 `ruby_ore` 的方块需要 `textures/block/ruby_ore.png`，其他什么都不用写。物品以平面形式绘制的方块，即门、梯子、火把、树苗、花、cane、藤蔓或玻璃板，手持时在 `textures/item/<name>.png` 存在时使用它，不存在时使用其方块纹理。

**物品**使用 `textures/item/<name>.png` 和生成的 `item/generated` 模型，工具则使用 `item/handheld`。附带 `models/item/<name>.json` 可以用别的方式绘制。

**流体**完全不需要模型；会根据 `still` 和 `flow` 纹理生成一个。

**有多个变种的方块就是多个方块。** `variants` 下的每个键都单独注册，因此各自有自己的方块状态、模型和纹理，并以该键命名。没有携带 `blocks` 属性的共享方块状态，方块状态中也无需说明它是哪个变种：`blockstates/ruby_ore.json` 属于红宝石矿石，`blockstates/deep_ruby_ore.json` 属于深层的那个。

### 编写你自己的内容

*模型、方块状态与纹理*

所有生成的内容都可以替换。资源包附带的方块状态原样使用，采用游戏自己的格式：以方块属性为键的原版 `variants`，或 `multipart`。属性是游戏对每种类型自带的：原木用 `axis`，半砖用 `type`，楼梯用 `facing`、`half` 和 `shape`，门用 `facing`、`half`、`hinge` 和 `open`，活板门用 `facing`、`half` 和 `open`，栅栏门用 `facing`、`in_wall` 和 `open`，作物和 cane 用 `age`，树苗用 `stage`，栅栏或玻璃板用 `north`、`east`、`south`、`west`，墙和藤蔓则再加 `up`，立式旗帜用 `rotation`，墙上的旗帜用 `facing`，传送门用 `axis`，钟用 `attachment` 和 `facing`，其 `powered` 不在键中。`basic`、`ore`、`falling`、`leaves`、`flower` 或 `container` 方块只有一种状态，键为 `""`。

模型应指向接受纹理的父模型，而不是已完成的原版模型：`cube_all` 接受 `all`；`cube_column` 接受 `end` 和 `side`；`cross` 接受 `cross`；楼梯的父模型接受 `bottom`、`top` 和 `side`；`fence_post` 和 `fence_side` 接受 `texture`；`template_wall_post` 和 `template_wall_side` 接受 `wall`；玻璃板模板接受 `pane` 和 `edge`；门的父模型接受 `top` 和 `bottom`；`template_orientable_trapdoor_*` 和 `template_fence_gate*` 接受 `texture`；`template_torch` 接受 `torch`；`crop` 接受 `crop`；`vine` 和 `ladder` 接受以各自名称命名的纹理。指向已完成的原版模型（例如 `oak_door_bottom_left`）的模型会连带继承原版的纹理，无论方块状态怎么写。

### 旗帜

*模型、方块状态与纹理*

旗帜是唯一一种方块的形状与模型的形状分道扬镳的类型，因此值得完整说明。

**它会注册两个方块。** 一个定义会给你以自己名称命名的立式旗帜，以及名为 `<name>_wall` 的第二个方块用于悬挂的旗帜。两者都需要方块状态；只有立式的会得到物品，由该物品决定放置两者中的哪一个：点击方块顶部时放立式的，点击侧面时放墙式的。你永远不会直接放置墙式方块，它也不需要自己的物品。

**立式旗帜以十六分之一圈转动。** 它的属性是 `rotation`，从 `0` 到 `15`，因为旗帜是按十六分之一圈而不是四分之一圈转动的。方块状态自带的 `y` 只接受 0、90、180 和 270，因此每个 rotation 都指向一个自己的小模型，该模型以你的旗帜模型为父模型，并用 `transform` 转动它：

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

……依此类推直到 `15`，每个都再多转 `-22.5` 度。符号与游戏自带的旗帜一致，它们按 rotation 的负值转动。请把模型建成朝南，因为玩家面朝南放置的旗帜最终就朝向那里。墙式方块是普通的方块状态，有常规的四个 `facing` 条目，分别为 0、90、180 和 270，因为它没有任何小数的部分。转换 1.12.2 资源包时，其 Forge 方块状态正是被转换成这种形式。

**模型几乎有两个方块高。** 旗帜在放置和碰撞上只占一个方块，但绘制时远远超出这个范围，止于自身方块顶部的模型会显得发育不良。原版的比例（以十六分之一方块为单位）值得原样照抄：

| 部分 | 起 | 止 |
| --- | --- | --- |
| 旗杆 | `0` | `28` |
| 横杆 | `28` | `29.33` |
| 布面 | `2.67` | `29.33` |
| 布面宽度 | `1.33` | `14.67` |
| 墙上布面 | `-13` | `13.67` |

因此立式旗帜高达 `29.33`，差不多两个方块，而墙上的旗帜会垂到承托它的方块*下方*十三个十六分之一处。模型元素可以从 `-16` 延伸到 `32`，所以两者都放得下。墙式没有旗杆和横杆，只有布面。

**布面的高度是宽度的两倍，你的纹理也必须如此。** 那个面是 `13.33` 乘 `26.67`。把方形纹理映射上去，图案会被压成一半高度。方块纹理本身不能做成高度为宽度两倍，因为任何非方形的纹理都会被当作动画读取，所以绕开的办法是用一张更大的方形贴图表，把布面放在其中一部分：一个 32×32 的文件，将布面放在一个 16×32 的区域里，以 `"uv": [0, 0, 8, 16]` 寻址，旗杆和横杆的条带放在旁边的空位中。无论文件分辨率如何，UV 坐标始终是 0 到 16，因此同样的数字在任何尺寸下都适用。

**它的物品需要一个自己的模型。** 继承了这么高的模型的物品，在通常的方块缩放下会冲出它的物品栏格子，因此请在 `models/item/<name>.json` 中给出自己的 `display` 块，把缩放调小，并把整体平移回框内。

**没有方块状态时，它从贴图表绘制。** 资源包没有为某个旗帜附带方块状态时，会得到一个生成的方块状态，游戏的旗帜渲染器会按原版旗帜的形状，从 `textures/entity/banner/<name>.png` 的贴图表绘制它，布局与原版旗帜一致。这是此版本新增的功能；立式和墙式方块各自由自己的方块状态决定。

**它上面没有颜色和图案。** 资源包旗帜不像原版旗帜那样带有图层列表。设计就是纹理，就像门的外观就是它的纹理一样，一个定义就是一面旗帜。给它染色并在上面叠加图案，是资源包做不到的。

**它使用你给定的 `material`。** 石质旗帜像它自称的石头一样，用镐开采。

### 以像素图编写的纹理

*模型、方块状态与纹理*

纹理可以是 JSON 文件而不是 PNG。把它放在 PNG 本应在的位置，并在整个名称后加上 `.json`，这样 `textures/block/panel.png.json` 就能响应对 `textures/block/panel.png` 的所有请求。其他一切都不变：模型照旧指向 `mypack:block/panel`，图集、mipmap 和动画 `.mcmeta` 都能工作，因为游戏收到的仍然是 PNG。示例资源包一个 PNG 都没有附带；其中的每张纹理都是一张像素图。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `size` | 是，或继承 | `widthxheight` | | 横向和纵向各有多少像素 |
| `rows` | 是，或继承 | 文本列表 | | 每行像素一个字符串，每个像素一个字符，从上往下 |
| `palette` | 是，或继承 | 对象 | | 字符到颜色的映射，`#RRGGBB` 或 `#AARRGGBB` |
| `extends` | 否 | 另一张像素图 | | 这张图的起点所基于的图 |
| `tint` | 否 | 含 `from` 和 `to` 的对象 | | 沿着两种颜色之间的渐变，为所有继承来的内容重新着色 |
| `notes` | 否 | 对象 | | 字符到一行说明其用途的文字的映射，会被继承，且从不绘制 |

**无需声明名称。** 文件自身的路径就是它的名称，与 PNG 完全一样，所以位于 `assets/mypack/textures/block/panel.png.json` 的图在模型中就是 `mypack:block/panel`，位于 `assets/mypack/textures/item/gem.png.json` 的图在物品模型中就是 `mypack:item/gem`。没有任何东西专门指向像素图；方块或物品照常指定它的纹理，并且永远不知道得到的是哪一种。这也意味着方块和物品文件夹保持分开，与 PNG 一样：`textures/block/gem.png.json` 和 `textures/item/gem.png.json` 是两张不同的纹理，并作为两个不同的文件缓存。

**尺寸随意**，每边最大 4096，且两边不必相等。`16x16` 是普通的方块面，`16x32` 是门的半扇或动画所需的那类高条。尺寸是经过检查而不是猜测的：每行像素给一行，每个像素给一个横向字符，否则这张图会被拒绝，日志会指出是哪一行以及发现了什么。调色板中没有颜色的字符会留作透明，因此 `.` 或空格就是一个洞。

**模板才是关键。** `extends` 指定另一张像素图，写作 `namespace:path` 或同一资源包中的裸路径，继承它的文件会继承它的 `size`、`rows` 和 `palette`。自己写出的任何内容都优先，而且不必全部写出，因此一个完整的变种可以只是几种颜色：

```json
{
  "extends": "mypack:textures/block/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

这就是一张完整的第二纹理：同样的形状，紫珀色的，而且如果模板中的形状日后被重绘，每个变种都会随之变化。变种也可以反过来给出自己的 `rows` 并保留模板的调色板，即相同的颜色配不同的图案。继承最多可达八层，循环会被捕获并报告，指向没有任何资源包提供的模板的图会被报告，而不是画成空白。

**两张纹理中哪张是模板**，取决于哪张包含更多区别，而不是哪张先画的。变种给每个字符一种颜色，因此模板中称为同一个字符的每个像素在变种中都会是同一种颜色。因此，画在石头上的矿石不能继承石头的 `rows`：石头把矿点的位置称为普通石头，而变种所能写的任何内容都无法把一个字符拆成两个。反过来就行得通。让矿石做模板，这样石头的色调和矿石的色调各自拥有自己的字符，于是第二种矿石只需四种颜色：

```json
{
  "extends": "mypack:textures/block/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

真正想要不同图案的变种应像前面那样给出自己的 `rows`，此时只继承调色板。当颜色才是重点而形状无关紧要时，这样做是值得的；当形状才是重点时，把形状放进模板，让变种去指定颜色。

**模板根本不必是纹理。** 只有当路径以 `.png` 结尾时，像素图才会被提供给游戏，因此位于 `textures/block/ore_template.json` 的模板对游戏是不可见的，纯粹为被继承而存在，而位于 `textures/block/ore_template.png.json` 的则还会响应对 `ore_template.png` 的请求。给共享的形状取名时不带 `.png`，就没有任何东西会意外请求到它。

**模板也可以是一张真实的图像而不是图。** 让 `extends` 指向任何资源包或游戏本身提供的 PNG，调色板的含义就变了：键变成该图像中已有的颜色，值是要换上去的颜色。不做描摹，也不写 `rows`，因此资源包可以就地为原版或模组的纹理重新着色：

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

这就是在原版自己的石头里的红宝石矿石：四种矿点色调被换掉，其他每个像素保持原样。图像中不存在的颜色只是永远匹配不上，尺寸取自该图像，除非你另行指定，而指定的尺寸必须与之相符。

`extends` 优先使用像素图：它先在该路径查找像素图，只有在没有资源包提供时才退回到图像。两者都不是的名称会被报告，而不是画成空白。基于图像构建属于客户端的工作，因为读取的是游戏自己的资源，所以专用服务器从不执行这件事。

**模板可以着色而不必重绘。** `tint` 指定两种颜色，并沿它们之间的渐变为这张图继承的一切重新着色。每个继承来的颜色的亮度就是它在渐变上的位置，因此黑色落在 `from`，白色落在 `to`，两者之间的每个色调按比例混合。透明度保持不变。这样，一张灰度模板加两种颜色就是一个完整的变种：

```json
{
  "extends": "mypack:textures/item/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` 可以省略，此时它是黑色，着色就变成普通的相乘，形态与渲染时的 `tintindex` 相同。区别在于这种着色只绘制进 PNG 一次并缓存，因此每帧零开销，并且能作用于没有任何东西为其着色的纹理，但它也无法像 `grass` 或 `foliage` 那样跟随生物群系变化。

模板仍然是一张普通的图：打开它看，它就是它本来的灰色。两种颜色都接受 `#RRGGBB`、`#AARRGGBB` 或以 `0x` 开头的写法，两者都不是的值会让这张图不被绘制，而不是画成错误的颜色。着色与其他内容一样会被继承，继承链上第一个出现的生效，因此变种自己的着色胜过它所继承的。它对图像模板同样有效，此时在调色板的颜色替换之后运行。

**着色是两种颜色之间的渐变**，所以只适合色调落在一条渐变上的纹理。有两个互不相关区域的形状，比如矿石的石头与矿点，就不是这种情况，需要把调色板写全。

**了解模板的字符各是什么意思**是继承模板时的麻烦事，这正是上面的 `notes` 块的用途：字符到一行简短说明的映射，与调色板一样被继承，且从不绘制。给模板的字符加上标注，继承它的人就知道该覆盖哪些。

`/rdpl pixelmap <namespace:path>` 随后会报告一张图实际上生成的结果，这是编写变种时无需逐个打开继承链上的文件的可靠方式：

```
oretest:textures/block/ruby_ore.png is 16x16
  built from oretest:textures/block/ruby_ore.png.json
  built from oretest:textures/block/gem_ore.png.json
  rows come from oretest:textures/block/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

每个字符都会列出它的颜色、覆盖的像素数、继承链中哪个文件设置了它，以及该文件说明它的用途。路径可以写成简短形式 `mypack:block/panel`，也可以写全。显示为 0 像素的字符，是调色板里提到而 rows 从未用到的，这通常是某一行里的笔误。

**绘制出的图像保存在磁盘上**，位于 `rdploader/pixelmap-cache`，每个命名空间一个文件夹，文件以纹理命名，末尾带有其来源的哈希。哈希涵盖整条继承链，即这张图本身及其上方的每个模板，因此编辑模板会改变每个继承它的变种的戳记，它们都会被重新绘制。一张图被重绘时，它较旧的文件会被清除。

每次扫描资源包时也会检查该文件夹，任何其像素图已不被任何资源包提供的图像都会被删除，连同留下的空文件夹。重命名纹理、移除资源包、删除像素图，它缓存的图像也会随之消失，而不是永远留在那里。删除整个文件夹的代价只是重新绘制它们的时间，而且扫描资源包时会跳过它，所以它永远不会被误认为是资源包。

PNG 永远优先。如果 `panel.png` 和 `panel.png.json` 都存在，就提供 PNG，而像素图从不绘制，因此生成的纹理日后可以被手绘的取代，而无需改动任何指向它的内容。

**没有人需要手写这些文件。** 仓库在 `pixelmap/` 中附带了整个往返流程所需的脚本：`png_to_pixelmap.py` 把一张 PNG 转成一张像素图，`convert_pack.py` 对资源包里的每张纹理执行这一操作，`verify_pack.py` 则绘制转换后资源包的像素图并与它们所源自的 PNG 比较，这样在把原件收起之前就可以信任转换结果。

### 值得留意的陷阱

*模型、方块状态与纹理*

**指向已完成原版模型的模型，也会继承原版的纹理。** `torch`、`ladder`、`oak_door_bottom_left` 和 `wheat_stage0` 都自带纹理，因此指向其中之一的模型，无论你在旁边放什么，得到的都是原版的外观。`cube_all`、`cross` 和 `crop` 这类父模型从指定它们的模型中获取纹理，行为正常，门、活板门和栅栏门的模板也是如此。

**名称来自语言文件。** 方块或物品在 `lang/en_us.json` 给出名称之前会显示原始键，而这些键是游戏自己的：方块及放置它的物品用 `block.mypack.ruby_ore`，物品用 `item.mypack.ruby`，创造模式标签页用 `itemGroup.mypack.tab`，流体用 `fluid_type.mypack.molten_ruby` 和 `fluid.mypack.molten_ruby`，药水效果用 `effect.mypack.ruby_sight`，实体变种用 `entity.mypack.angry_cow`，生物群系用 `biome.mypack.ruby_forest`。每个只有一个名称；在这个版本上，没有任何键需要写两遍。

## 让原版正确对待你的方块

*方块与物品*

原版在十几个地方是按身份检查自己的方块的，因此资源包中显然应该起作用的方块往往并不起作用。有两个键可以解决。

```json
{
  "material": "ground",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "hardness": 0.6 } }
}
```

**`plantTypes`** 列出你的方块所支持的植物类型，这样树苗、作物和花就能种在它上面：`plains`、`desert`、`beach`、`cave`、`water`、`nether` 和 `crop`。**1.21.1** 完全没有植物类型；在那里 `behavesAs` 中的 `bush` 起到 `plains` 原来的作用，其余由植物自己的 soil 列表决定，该键会被读取但忽略。

**`behavesAs`** 让原版把你的方块当作它自己的某个方块来对待：

| 值 | 作用 |
| --- | --- |
| `till` | 锄头可将其变为耕地，或变为配置中 `hoeTillsInto` 所指定的方块 |
| `path` | 锹可将其变为土径，或变为 `shovelPathBecomes` 所指定的方块 |
| `bush` | 花、草和树苗可以种在它上面并留在上面，与泥土相同。等同于 `plantTypes` 中的 `plains` |
| `animals` | 动物像在草方块上那样，在有光照时于其上生成 |

## 物品

*方块与物品*

`<namespace>/items/*.json`

`variants` 中的每个键都是一个物品，注册在资源包的命名空间之下，因此包含 `ruby_apple` 和 `dried_ruby_apple` 的文件会注册 `mypack:ruby_apple` 和 `mypack:dried_ruby_apple`；文件自己的名称只是一种分组。除非资源包附带了 `models/item/<name>.json`，否则每个物品的模型都由 `textures/item/<name>.png` 生成。

一次性展示所有键。实际文件只写需要的键。标明某个类型的键只由该类型读取。

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

### 物品类型

*物品*

| 类型 | 你得到什么 |
| --- | --- |
| `basic` | 普通物品。缺少 `type` 时使用 |
| `food` | 可食用，带有饥饿值和饱和度 |
| `drink` | 饮用而不是食用，并返还一个空容器 |
| `tool` | 由材料制成的镐、斧、锹、锄或剑 |
| `armor` | 由材料制成的头盔、胸甲、护腿或靴子 |
| `seed` | 种下你的某种作物 |
| `potion` | 使用时施加你的药水效果 |
| `potion_bottle` | 盛放你的药水类型，并在创造模式标签页中显示它们 |
| `container` | 小袋：手持或佩戴的物品栏，见 [容器](#容器) |

`potion_bottle` 用 `potionTypes` 列出它能盛放的内容，这是一个药水类型名称的数组，如 `["mypack:ruby_tonic"]`。列表为空的物品不会注册任何内容，日志会说明这一点。

### 物品文件键

*物品*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `variants` | 是 | 变种名称到变种的对象 | | 每个条目一个物品。键是它的注册名，并命名它的模型、纹理和语言键 |
| `type` | 否 | 上述类型之一 | `basic` | 物品采用哪种类型 |
| `creativeTab` | 否 | 标签页名称 | 无 | 它出现在哪个标签页，见 [创造模式标签页](#创造模式标签页) |
| `material` | tool、armor | 材料名称 | 无 | 它由你的哪种材料制成 |
| `toolClass` | tool | `pickaxe`、`axe`、`shovel`、`sword` | 无 | 它是哪种工具。`sword` 是 1.12.2 的工具而不是原版的剑：它造成 3 加上材料 `damage` 的伤害，能开采 `harvestTool` 为 `sword` 的方块，可使用挖掘类附魔，既不会横扫也不能砍蜘蛛网 |
| `slot` | armor | `head`、`chest`、`legs`、`feet` | 无 | 穿戴的位置。`helmet`、`chestplate`、`leggings` 和 `boots` 也可使用 |
| `eat` | food | boolean | `false` | 使用食用动画 |
| `alwaysEdible` | food | boolean | `false` | 饥饿值已满时也可食用 |
| `useDuration` | 否 | int，刻 | `32` | 使用它需要多长时间 |
| `attackSpeed` | 否 | float | 视工具类别而定 | 用于 `tool`，即攻击速度属性，例如剑为 `-2.4` |
| `cooldown` | 否 | int，刻 | `0` | 用于 `food`、`drink` 和 `potion`，物品被消耗后多长时间内拒绝再次使用 |
| `container` | drink | 物品名称 | 无 | 留下的东西，例如瓶子。在 `container` 物品上，这个键改为小袋自己的设置，见 [容器](#容器) |
| `crop` | seed | 方块名称 | 无 | 它种下的作物 |
| `soil` | seed | 方块名称 | `minecraft:farmland` | 它可以种在什么方块上 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该文件 |

### 物品变种键

*物品*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `maxSize` | 否 | 1 到 64 | `64` | 堆叠上限 |
| `rarity` | 否 | `common`、`uncommon`、`rare`、`epic` | `common` | 提示框中名称的颜色 |
| `healAmount` | food | int，半个鸡腿 | `0` | 恢复的饥饿值 |
| `saturation` | food | float | `0.0` | 恢复的饱和度 |
| `tags` | 否 | 标签 id 列表 | 无 | 该变种写入的物品标签；标签文件会为你生成 |
| `potion` | food、drink | `potion,duration,amplifier` | 无 | 该变种被食用或饮用时施加的效果。第四个部分 `true` 使其成为环境效果。有益效果在提示框中以绿色显示名称，等级高于 0 时后面跟着罗马数字的等级，并且它所赋予的任何效果都不显示粒子 |

## 流体

*方块与物品*

`<namespace>/fluids/*.json`

文件的路径是流体方块的注册名。`name` 命名流体本身、它的桶及其语言键，除非文件另行设置，否则就是文件的路径。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 否 | string | 文件名 | 流体及其桶（`<name>_bucket`）的注册名；方块保留文件的路径 |
| `still` | 否 | 纹理路径 | 原版静止的水 | 静止流体的纹理 |
| `flow` | 否 | 纹理路径 | 原版流动的水 | 流动流体的纹理 |
| `color` | 否 | 十六进制颜色 | 无 | 施加在这些纹理上的色调。在默认的水纹理上，它会乘以 1.12.2 水的蓝色，因此为 1.12.2 选的颜色在这里看起来一样 |
| `bucket` | 否 | boolean | `true` | 为它注册一个桶 |
| `luminosity` | 否 | 0 到 15 | `0` | 发出的光 |
| `density` | 否 | int | `1000` | 负值会像气体一样向上浮 |
| `temperature` | 否 | int，开尔文 | `300` | 水是 300，熔岩是 1300 |
| `viscosity` | 否 | int | `1000` | 流动得有多慢：流体每隔 viscosity / 200 刻前进一次。水是 1000，熔岩是 6000 |
| `gaseous` | 否 | boolean | `false` | 视为气体 |
| `creativeTab` | 否 | 标签页名称 | 无 | 桶出现的标签页 |
| `block` | 否 | 对象 | | 流体方块。`material`（`water`）：`water` 可以游泳和溺水，能让船漂浮，扑灭着火的生物，使耕地保持湿润，并在下界倒入时蒸发；`lava` 会点燃站在其中的一切，不能游泳或溺水，并使用熔岩桶的音效；任何其他材料都不具备以上这些。`flammability`（`0`）和 `fireSpread`（`0`）：火焰吞噬该方块并从它蔓延的难易程度。`quantaPerBlock`（`0`，按 8 读取）：它从源头流出多远，如 1.12.2 一样比该数字少一格；这里的流体能到达 1、2、3 或 7 格，因此 4 和 5 流 3 格，6 及以上流 7 格。`potions`（无，一个施加给站在其中的一切的效果列表，每个写作 `potion,duration,amplifier`，可加第四个部分 `true` 表示环境效果） |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该文件 |

## 材料、标签页、音效、标签

*方块与物品*

`<namespace>/materials/*.json`

文件的路径就是材料的名称，工具或盔甲物品随后在 `material` 中指定它。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `harvestLevel` | 否 | 0 到 4 | `1` | 工具等级。0 木，1 石，2 铁，3 钻石，4 下界合金 |
| `durability` | 否 | int | `250` | 损坏前的使用次数 |
| `efficiency` | 否 | float | `6.0` | 挖掘速度。钻石是 8 |
| `damage` | 否 | float | `2.0` | 攻击伤害加成 |
| `enchantability` | 否 | int | `14` | 附魔的优质程度。金是 22 |
| `repairItem` | 否 | 物品名称 | 无 | 在铁砧中修复由它制成的工具所用的物品。由它制成的盔甲不通过这种方式修复，与 1.12.2 一致 |
| `reduction` | 否 | 四个 int 的列表 | | 护甲值，顺序为靴子、护腿、胸甲、头盔 |
| `toughness` | 否 | float | `0.0` | 盔甲韧性，如钻石所具有的 |
| `equipSound` | 否 | 音效名称 | `item.armor.equip_iron` | 穿上盔甲时的音效 |
| `armorTexture` | 否 | 纹理前缀 | 文件名 | 穿戴的盔甲纹理，从该命名空间下的 `textures/models/armor/<name>_layer_1.png` 和 `_layer_2.png` 读取 |

### 创造模式标签页

*材料、标签页、音效、标签*

`<namespace>/tabs/*.json`

方块、物品和桶在 `creativeTab` 中指定它们的标签页。完整的 id，如 `mypack:rubypack` 或 `minecraft:combat`，按原样使用。裸名称按 1.12.2 读取标签页名称的方式读取：`buildingBlocks` 对应 `minecraft:building_blocks`，`decorations` 对应 `minecraft:functional_blocks`，`redstone` 对应 `minecraft:redstone_blocks`，`transportation` 和 `tools` 对应 `minecraft:tools_and_utilities`，`misc` 和 `materials` 对应 `minecraft:ingredients`，`food` 和 `brewing` 对应 `minecraft:food_and_drinks`，`combat` 对应 `minecraft:combat`，其他任何裸名称都是指定它的文件所在命名空间中的标签页 `<namespace>:<name>`。没有任何文件声明的标签页会为你创建，标题取自 `itemGroup.<namespace>.<name>`，并显示其中的第一个物品。除非 `label` 覆盖，否则标签页文件的路径就是标签页的名称。

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `label` | 否 | string | 文件名 | 标签页的 id：方块和物品在 `creativeTab` 中指定它，显示的名称来自语言文件中的 `itemGroup.<namespace>.<label>` |
| `icon` | 否 | 物品名称 | 无 | 标签页上显示的物品 |

### 音效

*材料、标签页、音效、标签*

`<namespace>/sounds/*.json`

文件名由你自己选择，只读取文件夹，并且多个文件会叠加。

原版的 `sounds.json` 格式，因此资源包可以附带自己的音频。这里的文件注册音效事件；客户端仍通过资源包自己的 `assets/<namespace>/sounds.json` 读取音频，所以两者都要附带，索引放在 `assets` 下，事件放在 `data` 下。

### 标签

*材料、标签页、音效、标签*

`<namespace>/tags/<kind>/*.json`

标签采用游戏自己的格式，放在游戏自己的文件夹中，资源包像数据包那样附带它们：`tags/items/ores/ruby.json`（1.21.1：`tags/item/`）中写 `{ "values": ["mypack:ruby_ore"] }`，就把这个矿石放进 `mypack:ores/ruby`，而 `data/forge/tags/items/ores/ruby.json`（1.21.1：`data/c/...`）下的文件会添加到每个模组都读取的共享约定标签中。资源包自己的方块和物品改为在变种的 `tags` 中指定它们的标签，文件会为你写好；`harvestTool` 和 `harvestToolLevel` 以同样的方式写出 `mineable` 和 `needs_*_tool` 标签。

1.12.2 的矿物词典已被标签取代。它的名称对应到约定标签：`oreRuby` 在 1.20.1 上是 `forge:ores/ruby`，在 1.21.1 上是 `c:ores/ruby`，`ingotCopper` 是 `ingots/copper`，`gemRuby` 是 `gems/ruby`，`dustX` 是 `dusts/x`，`nuggetX` 是 `nuggets/x`，`blockX` 是 `storage_blocks/x`，而 `logWood`、`plankWood` 和 `stickWood` 是游戏自己的 `minecraft:logs`、`minecraft:planks` 和约定的 `rods/wooden`。标签文件的 `"remove": [...]` 从标签中移除单个条目，`"replace": true` 加上空的 `"values": []` 则清空它，因此来自较低资源包或某个模组的标签可以被削减或清空。若要一次性把物品从所有标签中移除并彻底退出游戏，请使用 [禁用的方块与物品](#禁用的方块与物品)。

## 属性覆盖

*方块与物品*

`<namespace>/overrides/<target>/<name>.json`

路径指明目标：`overrides/` 之后的一切是被修改的方块、物品或药水类型的命名空间和名称。

在其他地方，资源包要么替换一个文件，要么添加一个。覆盖两者都不是：它修改一个已存在的方块、物品或药水类型的属性，无论是原版的还是模组的，而不触碰它的任何文件。路径指明目标，因此 `overrides/minecraft/stone.json` 修改 `minecraft:stone`，`overrides/tconstruct/<name>.json` 以同样的方式修改该模组的方块。

一次性展示所有键。实际文件只写需要的键。

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

### 方块属性

*属性覆盖*

每个键都是可选的，文件只修改它所指名的内容，因此位于 `overrides/minecraft/stone.json` 且只含 `hardness`、`light` 和 `soundType` 的文件，会让石头几乎瞬间挖开、发光，并且听起来像玻璃。一个文件可同时携带方块、物品和药水的键。当目标是方块时，以下这些适用：

| 键 | 值 | 作用 |
| --- | --- | --- |
| `hardness` | float | 挖掘时间，与方块定义所接受的数值相同。若没有 `resistance`，它还会像 1.12.2 那样把爆炸抗性至少提高到同样的数值 |
| `resistance` | float | 按 1.12.2 的读法读取的爆炸抗性：方块保留该数值的五分之三，因此 `10` 得到石头的 `6` |
| `slipperiness` | float | `0.6` 是普通地面，`0.98` 是冰 |
| `light` | `0` 到 `15` | 发出的光 |
| `lightOpacity` | `0` 到 `15` | 方块阻挡多少光 |
| `soundType` | 音效类型之一 | 脚步、放置和破坏的音效 |
| `harvestTool` | `pickaxe`、`axe`、`shovel`、`hoe` 或 `sword` | 用什么能快速挖掘它，写入工具标签；`harvestToolLevel`（默认 `0`）设置等级：1 石，2 铁，3 钻石，4 及以上下界合金。没有合适工具时方块是否掉落，保持方块原有的设定 |
| `flammability` | int | 它烧毁的难易程度；`fireSpread`（默认 `5`）为火焰蔓延到它的难易程度 |

### 物品属性

*属性覆盖*

| 键 | 值 | 作用 |
| --- | --- | --- |
| `maxStackSize` | `1` 到 `64` | 堆叠上限 |
| `maxDamage` | int | 耐久度 |
| `containerItem` | 物品名称 | 留在合成网格中，就像桶那样 |
| `food` | 对象 | 使物品可食用，见下文 |

既是方块又是物品的名称，而每个可放置方块的物品都是如此，可以在一个文件中同时使用这两组键：

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

在 `overrides/minecraft/oak_planks.json` 处，这会让木板的破坏速度与泥土差不多，并且可以被食用。`food` 接受 `heal`（`1`）、`saturation`（`0.6`）、`alwaysEdible`（`false`；`true` 允许在饥饿值已满时进食）和 `effects`，后者的条目写法与药水类型完全相同。本来就是食物的物品接受新的 `heal`、`saturation` 和 `alwaysEdible`；对这类物品使用 `effects` 不受支持，日志会说明。当可食用的物品会放置方块时，要对着天空进食，因为对着方块会把它放下：这是原版的使用顺序，不是缺陷。

### 药水类型效果

*属性覆盖*

文件顶层的 `effects` 会彻底重写药水类型的效果列表：

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

在 `overrides/minecraft/swiftness.json` 处，迅捷药水现在会赋予飘浮。每个条目接受 `potion`（必填）、`duration`（`3600`）、`amplifier`（`0`）、`ambient`（`false`）和 `showParticles`（`true`），与 `potion_types/` 中相同，且列表不可为空。

### 其他模组、重载与限制

*属性覆盖*

由其他模组拥有的目标应在 `requires` 中带上该模组，这样当模组未安装时文件会被静默跳过，而不是被报告为缺少目标：

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

覆盖是实时的。原始值会在第一次修改之前被记住，因此禁用资源包并运行 `/rdplserver reload` 会把一切恢复原状，无需重启；每次进入世界时也会发生同样的事。每个目标一个文件：当两个资源包覆盖同一样东西时，较后的资源包的文件会整体替换较早的那个，日志会说明这一点。

有两个值得了解的限制。自身代码会计算某个属性的方块或物品会忽略其背后的字段，因此覆盖会生效但什么也不会改变；原版只在楼梯的爆炸抗性上这样做，但模组可以在任何地方这样做。另外，使其可食用的物品只对没有自己右键行为的物品有效：使用时已有作用的物品会继续那样做。

覆盖要求客户端和服务器都装有该资源包，因为挖掘速度、光照和进食都发生在玩家的画面上，所以它们不适用于服务端资源包。`content` 配置类别中的 `overrides` 会完全关闭该文件夹。

## 硬度分组

*方块与物品*

`<namespace>/hardness/*.json`

文件的路径在日志中命名该分组，其他地方都不读取它，因此多个文件会叠加。

给一组方块一个挖掘时间倍率，按每个方块位置随机决定。方块本身从不改变：不注册任何东西，不向世界写入任何东西，不带该资源包打开的世界就是普通的原版世界。

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

### 挖掘与爆破

*硬度分组*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `blocks` | 是 | 方块名称或对象的列表 | | 该分组。与世界生成的 `replace` 形式相同 |
| `except` | 否 | 方块名称或对象的列表 | 无 | 从该分组中剔除，无论 `blocks` 怎么写 |
| `miningTime` | 否 | 数字，或含 `min` 和 `max` 的对象 | `1.0` | 方块需要多少倍的时间才能破坏，对玩家和 `digs` 生物同样适用 |
| `blastResistance` | 否 | 数字，或含 `min` 和 `max` 的对象 | `1.0` | 乘以方块的爆炸抗性 |
| `buckets` | 否 | 1 到 256 | `10` | 范围被划分成多少级 |
| `minHeight` | 否 | int | 世界底部 | 低于此高度，随机结果取最硬的一级 |
| `maxHeight` | 否 | int | 世界顶部 | 高于此高度，随机结果取最硬的一级 |
| `field` | 否 | 对象 | 见下文 | 随机结果聚成团的形状 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该文件 |

单个数字让分组中的每个方块得到相同的倍率，不做任何随机。`min` 和 `max` 则按位置随机：矿场为空的地方取 `max`，团块中央取 `min`，两者之间的各级由 `buckets` 决定。

### 冒险挖掘与解锁

*硬度分组*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `keeps` | 否 | boolean | `false` | 方块被挖掉后仍留在原处：掉落物、经验、工具损耗和破坏音效都会发生，而方块仍然在那里可以再次挖掘，因此该分组成为一条无穷无尽的矿脉，节奏由 `miningTime` 决定。创造模式照常将其移除 |
| `adventure` | 否 | 对象 | 无 | 谁可以在冒险模式下破坏该分组，否则在冒险模式下什么都不能破坏。`tools` 列出必须手持其中之一的物品，留空表示手持任何东西均可；`teams`、`players` 和 `entities` 说明是谁，队伍用名称，玩家用名字，生物用其实体 id（用于 `digs` 任务），三者都为空表示任何持有该工具的人。生存模式和创造模式不受影响 |
| `advancement` | 否 | `namespace:path` | 无 | 玩家只有获得该进度后，该分组才对其生效。两个分组可以指定同一个方块，一个带进度，一个不带，已解锁的那个优先；没有该进度的玩家得到普通分组，若没有则是原版。生物没有进度，因此带条件的分组永远不会作用于 `digs` 任务，而不属于任何玩家的爆炸抗性和纹理随机，则来自普通分组 |
| `becomes` | 否 | 对象 | 无 | 一旦任何玩家获得 `advancement`，该分组的方块就在全世界范围内变成另一种方块：`{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`。每个已加载的区块会立刻被清扫，之后加载或生成的区块会在进入时被清扫，因此旧方块就此永远消失。给新方块一个自己的分组，可以改变它的挖掘方式 |

### 矿场

*硬度分组*

每个方块并不是完全独立地掷骰，否则硬与软就会变成毫无形状的纯噪点。`field` 决定它呈现什么形状，`type` 则在两种实现方式之间选择。

```json
{
  "field": { "type": "speckle" }
}
```

| 键     | 必需 | 值                    | 默认值    | 作用                   |
| ------ | ---- | --------------------- | --------- | ---------------------- |
| `type` | 否   | `speckle` 或 `seeded` | `speckle` | 使用下面两种中的哪一种 |

#### speckle

*矿场*

每个方块各自抽取一个档位，相隔一面的方块还可以把一个更弱的档位传给它。这样得到的是密集而细碎的斑点，大多只有单个方块，相遇处偶尔会出现较大的斑块。在这两种方式中，它更接近本模组所借鉴的那个模组里的挖掘手感。

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| 键        | 必需 | 值                 | 默认值                                 | 作用                                                                 |
| --------- | ---- | ------------------ | -------------------------------------- | -------------------------------------------------------------------- |
| `chances` | 否   | 整数列表，单位为千分比 | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | 方块从每个档位起步的频率，最软的档位排在最后。剩余的部分都是最硬的档位 |
| `spread`  | 否   | 0.0 到 1.0         | `0.15`                                 | 一个档位传给相邻方块的频率，传过去的档位会弱一档或三档               |

列表按最软档位在最后的顺序读取，因此最后一项是最软的档位，第一项比最硬档位高一档。按上面的数值，每十个方块中约有七个是最硬档位，其余的散布其中。

#### seeded

*矿场*

种子分布在由世界和位置推算出的晶格上，方块的档位取决于它离最近一颗种子有多近。这样得到的斑块更少、更大、更圆，彼此相连，还能长出分支。

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

| 键         | 必需 | 值             | 默认值 | 作用                     |
| ---------- | ---- | -------------- | ------ | ------------------------ |
| `cell`     | 否   | 整数，单位为方块 | `8`    | 种子之间相隔多远         |
| `seeds`    | 否   | 1 到 4         | `1`    | 每个单元格中的种子数     |
| `reach`    | 否   | 浮点数，单位为方块 | `3.0`  | 一颗种子的影响范围       |
| `arms`     | 否   | 0 到 6         | `0`    | 从每颗种子向外辐射的分支数 |
| `armReach` | 否   | 浮点数，单位为方块 | `0.0`  | 分支延伸的距离           |

不写 `arms` 时，斑块是圆形的。给种子加上分支，它就变成带触须的结，相邻结的分支会彼此伸向对方，形成的是矿脉而不是团块。请让 `reach` 大于 `cell` 的一半，否则斑块无法相接，只会得到彼此之间空无一物的独立圆球。

### 显示方式

*硬度分组*

倍率本身是看不见的。要让玩家看出哪些方块更坚硬，请给该方块写一个方块状态文件，每个桶对应一个变种，权重全部相等，按从最硬到最软的顺序排列：

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

Minecraft 本来就会根据方块的位置挑选变种，而硬度分组把桶交给它来选，因此纹理与倍率始终一致。示例资源包的红宝石石头正是这样做的。

有三件事必须做对，而且做错时都不会有任何提示。

**恰好 `buckets` 个条目，且权重全部相同。** 桶编号被当作列表中的位置使用，所以长度不同的列表，或权重不一致的列表，会悄悄指向错误的纹理。

**模型名前面要加 `block/`。** 在此版本上，方块状态要写出模型文件的完整名称，所以 `"model": "mypack:block/stone_step0"` 读取的是 `models/block/stone_step0.json`；而裸写的 `mypack:stone_step0` 会去找并不存在的 `models/stone_step0.json`，该条目会被默默丢弃。

**键要与游戏查询的键一致。** 只有一种状态的方块，键写作 `""`，原版石头就是这样。带属性的方块，键由它的所有属性组成，所以覆盖原木时需要 `axis=x`、`axis=y` 和 `axis=z`，各自带一个列表。

打开 `worldgenDebug` 后，进入世界时会把每个硬度分组与其烘焙后的模型逐一核对，指出方块状态的名称、保留下来的变种数量、每个变种最终使用的纹理，以及游戏为此合并了哪些资源包。这是排查上面三个问题最快的办法；如果覆盖共享的方块状态改动了该分组从未提及的某个状态，它也会发出警告。

### 它不涉及的范围

*硬度分组*

只有玩家自己的挖掘会被改变。会破坏方块的机器直接读取方块的硬度，不受影响。玩家放置的方块与其他方块一样会被掷骰，因为这次掷骰属于位置而不是方块本身，被带到别处的方块则采用新位置所规定的值。

---

# 合成、战利品与交易

## 禁用的方块与物品

*合成、战利品与交易*

`<namespace>/disabled/*.json`

文件名可以自己定，只有文件夹会被读取，多个文件会叠加。

让方块和物品退出游戏，但不注销它们，因此世界保留原有的 id，删除该文件即可恢复一切。原版、模组和资源包的内容一视同仁，资源包自己的方块和物品也包括在内；被禁用的方块会连带禁用它的物品，被禁用的物品同样会连带禁用它的方块。

```json
{
  "requires": ["thermal"],
  "names": ["thermal:tin_ore", "thermal:deepslate_tin_ore", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "tags": ["forge:ores/tin"]
}
```

| 键           | 必需 | 值                     | 默认值 | 作用                                                                                                     |
| ------------ | ---- | ---------------------- | ------ | -------------------------------------------------------------------------------------------------------- |
| `names`      | 否   | 方块和物品名称的列表   | 无     | 要禁用的内容。以 `*` 结尾的名称匹配所有以其余部分开头的名称                                              |
| `namespaces` | 否   | 模组 id 的列表         | 无     | 该模组的所有方块和物品                                                                                   |
| `tags`       | 否   | 标签名称的列表         | 无     | 该名称的物品标签中的所有物品和方块标签中的所有方块，这两个标签随后都会被清空。允许在前面加 `#`           |
| `requires`   | 否   | 模组 id 的列表         | 无     | 除非列出的模组全部已加载，否则跳过该文件。`config:` 和 `file:` 条目的作用与其他各处相同                  |

被禁用的方块或物品：

- 从所有创造模式标签页和搜索标签页中消失，并在 JEI 中隐藏
- 没有以它为产物或原料的配方：以它为结果的所有类型的配方都会被移除，包括合成、烹饪、切石和锻造，只有它能填入的槽位所在的配方也一并移除。槽位还能放其他东西的配方会保留，而标签槽位则随标签一起失去它
- 从所有物品标签和方块标签中移除，改为列在 `resourcedatapackloader:disabled` 中
- 从所有战利品抽取中剔除，包括箱子、生物和钓鱼，也从方块掉落物以及村民和流浪商人的交易中剔除，掉落出来的该物品堆叠会消失
- 无法放置、使用、挥动或拾取，玩家尝试时，手中的堆叠会被删除
- 无论该物品的堆叠出现在哪里都会被删除：从玩家的物品栏和末影箱中，在登录时以及之后每秒删除一次；从任何容器中，在玩家打开时删除；从箱子和其他物品栏中，在其所在区块加载时删除
- 从放置它的世界中移除：它的每个方块都变成空气，其方块实体也一并消失，在其所在区块加载时发生

若想把已放置的方块换成别的东西而不是直接移除，请在世界模板中给它们写一行 `blockReplacements`，例如 `thermal:tin_ore=minecraft:stone`，参见[替换](#替换)。被替换流程换掉的方块交由该流程处理。其他模组保存在自己机器内部的配方属于那个模组，这里无法触及。从创造模式标签页和 JEI 中隐藏发生在客户端，因此原版客户端仍会列出该物品。配置中的 `content.disabled` 可以关闭这个文件夹。

若要清空一个标签但让其中的物品继续留在游戏中，请改用带 `"replace": true` 且 `"values": []` 为空的标签文件，参见[标签](#标签)。

## 熔炉配方与燃料

*合成、战利品与交易*

`<namespace>/furnace/*.json`

文件名可以自己定，只有文件夹会被读取，多个文件会叠加。

添加和移除熔炉配方。移除时也会一并去掉匹配的高炉、烟熏炉和营火配方，因为 1.12.2 把所有烹饪配方都放在同一个熔炉列表里；添加则只添加熔炉配方。

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

`add` 下的条目：

| 键           | 必需 | 值       | 默认值 | 作用                                                                                                                                  |
| ------------ | ---- | -------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------- |
| `input`      | 是   | 物品名称 | 无     | 放入什么                                                                                                                              |
| `output`     | 是   | 物品名称 | 无     | 产出什么                                                                                                                              |
| `count`      | 否   | 整数     | `1`    | 产出多少个                                                                                                                            |
| `experience` | 否   | 数字     | `0.0`  | 每烧炼一个物品获得的经验，最多一点：1.0 或更高时，每取出一个物品给一点，所以 `count` 为 2 就给两点。铁矿石给 0.7                      |

如果某个输入已经有物品可以烧炼，那么针对它的添加会被忽略，日志会指出挡路的那条配方，这与 1.12.2 一致；要替换它，请在同一文件或更早的文件中先把那条配方移除。

`remove` 下的条目可以是一个单独的物品名称，表示移除所有产出该物品的配方，也可以是一个对象，用 `input`、`result` 或两者共同缩小范围。两者都没写的移除会被跳过，日志会说明。

文件按加载顺序生效，每个文件先执行移除再执行添加。因此后面文件中的移除也会去掉更早文件所做的添加，但永远影响不到排在它之后的添加。一个添加的配方算作其产物所属模组的熔炉配方，所以 `blockFurnaceRecipes` 和 `blockedFurnaceMods` 会像对待其他配方一样屏蔽它。

`<namespace>/fuels/*.json`

文件名可以自己定，只有文件夹会被读取，多个文件会叠加。

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "tag": "forge:gems/ruby", "burnTime": 800 }
  ]
}
```

| 键         | 必需         | 值          | 默认值 | 作用                 |
| ---------- | ------------ | ----------- | ------ | -------------------- |
| `item`     | 两者选其一   | 物品名称    | 无     | 会燃烧的物品         |
| `tag`      | 两者选其一   | 标签 id     | 无     | 该标签中的所有物品都会燃烧 |
| `burnTime` | 是           | 整数，单位为刻 | `0`    | 煤炭是 1600，木板是 300 |

## 药水、药水类型与酿造

*合成、战利品与交易*

`<namespace>/potions/*.json`

文件的路径就是效果的注册名，因此 `mypack/potions/ruby_sight.json` 注册的是 `mypack:ruby_sight`，随后由药水类型引用它。

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

| 键              | 必需 | 值           | 默认值                                  | 作用                                                                                                  |
| --------------- | ---- | ------------ | --------------------------------------- | ----------------------------------------------------------------------------------------------------- |
| `name`          | 否   | 翻译键       | `effect.<namespace>.<name>`             | 玩家看到的名称                                                                                        |
| `color`         | 否   | 十六进制颜色 | `FFFFFF`                                | 粒子颜色                                                                                              |
| `badEffect`     | 否   | 布尔值       | `false`                                 | 视为有害效果，因此发酵蜘蛛眼会将其反转                                                                |
| `beneficial`    | 否   | 布尔值       | `false`                                 | 显示为增益效果                                                                                        |
| `instant`       | 否   | 布尔值       | `false`                                 | 一次性生效，而不是随时间持续                                                                          |
| `effectiveness` | 否   | 浮点数       | `0.5`                                   | 读取它只是为了让 1.12.2 的文件能够加载；1.12.2 和此版本都不会用到它                                   |
| `attributes`    | 否   | 对象列表     | 无                                      | `attribute`（游戏中的 id，例如 `minecraft:generic.movement_speed`）、`uuid`、`amount`（`0.0`）、`operation`（`0`） |
| `icon`          | 否   | 对象         | 无                                      | `x` 和 `y`，即图标在 1.12.2 状态图集中的列和行，不写则各为 `0`。仅在没有 `iconTexture` 时读取         |
| `iconTexture`   | 否   | 纹理路径     | RDPL 图标，设置了 `icon` 时则为无       | 资源包自带的图片，例如 `mypack:textures/effect/rage.png`，整张绘制为图标                              |

效果的图标是纹理 `assets/<namespace>/textures/mob_effect/<name>.png`，与游戏自带的一样是 18 x 18，资源包在该处提供的文件永远优先。否则，`iconTexture` 指向资源包自带的图片，会被整张复制到该处；单独写 `icon` 则按图标在 1.12.2 状态图集中的位置挑选原版效果的图标，`x` 向右、`y` 向下，均从 0 起，例如跳跃提升是 `{ "x": 2, "y": 1 }`；两者都没写的效果显示 RDPL 图标。

### 药水类型

*药水、药水类型与酿造*

`<namespace>/potion_types/*.json`

文件的路径就是药水类型的注册名，随后由 `potion_bottle` 物品在 `potionTypes` 中引用它。

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| 键         | 必需 | 值       | 默认值             | 作用                                                                                                                                                                                                                                       |
| ---------- | ---- | -------- | ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `baseName` | 否   | 字符串   | 命名空间与名称     | 为药水命名：语言键 `item.minecraft.potion.effect.<baseName>`，其他形态则把 `potion` 换成 `splash_potion`、`lingering_potion` 或 `tipped_arrow`。盛放它的 `potion_bottle` 显示相同的名称，1.12.2 资源包的 `potion.effect.<baseName>` 键会被转换 |
| `effects`  | 是   | 对象列表 |                    | 见下文                                                                                                                                                                                                                                     |

每个效果接受 `potion`（必需）、`duration`（`3600`）、`amplifier`（`0`）、`ambient`（`false`）和 `showParticles`（`true`）。

### 酿造

*药水、药水类型与酿造*

`<namespace>/brewing/*.json`

文件名可以自己定，只有文件夹会被读取，多个文件会叠加。

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

每个条目要么是 `input`、`ingredient` 和 `output`，把一种物品酿成另一种物品；要么是 `from`、`ingredient` 和 `to`，把一种药水类型变成另一种。两种写法都必须有 `ingredient`，条目还可以带 `requires`，这样可以只跳过某一个配方而不必跳过整个文件。

## 铁砧操作

*合成、战利品与交易*

`<namespace>/anvils/*.json`

文件名可以自己定，只有文件夹会被读取，多个文件会叠加。每个文件是一项操作。

把指定的物品放进铁砧的左槽，把它的 `with` 物品放进右槽，铁砧就会按所写的等级数，把左边的物品附上所列的附魔后交还，或者交出它的 `result`；除非指定了更多的数量，否则每边各消耗一个，两边堆叠中剩下的仍留在铁砧里。取出时还可以获得一项进度，并且可以让该物品在获得这项进度之前无法使用：比如一把必须经过加工才能挥动的剑。

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

| 键             | 必需 | 值                                                  | 默认值   | 作用                                                                                                                                                                                                                        |
| -------------- | ---- | --------------------------------------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`         | 是   | 物品名称、物品名称列表，或 `{ "item", "count" }`    |          | 放入左槽的内容（列表中任意一种物品均可），以及一项操作消耗多少个，默认一个；堆叠中剩下的留给下一次。`{ "item": "minecraft:coal", "count": 8 }` 配上钻石 `result`，就是八个煤炭换一颗钻石                                      |
| `with`         | 是   | 物品名称、物品名称列表，或 `{ "item", "count" }`    |          | 放入右槽的内容（列表中任意一种物品均可），以及消耗多少个，默认一个：`{ "item": "minecraft:coal", "count": 10 }` 要求至少一组十个并消耗十个。铁砧从不为单独一件物品开口，所以每项操作都是一对                                  |
| `result`       | 否   | 物品名称，或 `{ "item", "count" }`                  | 左侧物品 | 取代左侧物品交出的内容及其数量，默认一个，并保留左侧物品的标签，所以一把不可破坏的铁镐加十个煤炭可以换回一把不可破坏的钻石镐。附魔会加在最终交出的那件物品上                                                                  |
| `levels`       | 否   | 整数                                                | `1`      | 这项操作花费的经验等级，至少为 1                                                                                                                                                                                            |
| `enchantments` | 否   | 附魔名称到等级的对象                                | 无       | 物品交还时带有的附魔。已经有同等或更高等级的附魔会保持不变，没有任何可提升之处时铁砧不会给出结果，除非设置了 `grants`                                                                                                       |
| `grants`       | 否   | `namespace:path`                                    | 无       | 取出成果时获得的进度。请把它放在 `advancements/` 下，并使用 `impossible` 条件，这样其他途径就无法获得它                                                                                                                     |
| `locks`        | 否   | 布尔值                                              | `false`  | 在玩家获得 `grants` 之前，该物品不能用来攻击任何东西、不能使用，也不能用来挖掘；它进入玩家手中时，会告知玩家它在等待什么。仍然允许把它放进铁砧，这正是解锁它的方式                                                           |

铁砧本身的修复与合并不受影响：只有当左槽放着指定物品、右槽放着它的 `with` 时，这里才会作出响应。

带有 `collectsExperience` 的生物也会在这里花费自己的等级。当它主手拿着 `item`、副手拿着 `with`，并且有足够的 `levels` 可付时，它会走向水平 16 格、上下 4 格范围内的铁砧、开裂的铁砧或损坏的铁砧，进入 3 格以内后进行一次操作：等级从它自己身上扣除，与玩家被扣除的方式相同，`with` 被消耗，铁砧像玩家使用时那样磨损，成果最终落在它的主手上。当它走过某个铁砧操作在 `with` 中指明的掉落物时，会把它捡起放入副手。`grants` 和 `locks` 只与玩家有关，所以生物不会从 `grants` 中获得任何东西，也不受任何锁定的限制。

## 方块掉落物

*合成、战利品与交易*

`<namespace>/block_drops/*.json`

文件名可以自己定，只有文件夹会被读取，多个文件会叠加。

方块的战利品表决定它掉落什么，但提供战利品表会整个接管原有的表，而且战利品表无法给出经验。这里的一条规则指定一个方块，以及破坏它时在常规掉落物之外或取而代之的掉落内容，包括经验，同时不触动该方块自己的战利品表。

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

| 键            | 必需 | 值                   | 默认值  | 作用                                                                                                                  |
| ------------- | ---- | -------------------- | ------- | --------------------------------------------------------------------------------------------------------------------- |
| `block`       | 是   | 方块 id              |         | 规则所监视的方块                                                                                                      |
| `properties`  | 否   | 属性到值的对象       | 无      | 只匹配带有这些值的状态，例如原木上的 `{ "axis": "x" }`；不写则匹配所有状态。1.12.2 的 `meta` 不会被读取              |
| `replace`     | 否   | 布尔值               | `false` | 在掷出这些掉落物之前，是否先丢弃常规掉落物                                                                            |
| `advancement` | 否   | `namespace:path`     | 无      | 该规则只对拥有这项进度的玩家生效，因此同一个方块可以在此之前掉落一种东西，在此之后掉落另一种                          |
| `drops`       | 是   | 掉落物列表           |         | 方块被破坏时，每一项各自单独掷骰                                                                                      |

每个掉落物：

| 键           | 必需                | 值                        | 默认值   | 作用                                                                                                                                                                                                                      |
| ------------ | ------------------- | ------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`       | 是，除非有 `experience` | 物品 id                   |          | 掉落什么                                                                                                                                                                                                                  |
| `experience` | 否                  | 数字或 `low-high`         |          | 取代物品，以经验球的形式给出这么多经验，在范围内均匀掷出。`chance` 和 `silkTouch` 的适用方式与物品相同                                                                                                                    |
| `count`      | 否                  | 数字或 `low-high`         | `1`      | 数量多少，在范围内均匀掷出                                                                                                                                                                                                |
| `chance`     | 否                  | 浮点数                    | `1.0`    | 掉落发生的概率，`0.05` 表示每二十次破坏掉一次                                                                                                                                                                             |
| `fortune`    | 否                  | 整数                      | `0`      | 工具上每一级时运，最多额外增加这么多个                                                                                                                                                                                    |
| `silkTouch`  | 否                  | `either`、`only` 或 `never` | `either` | 该掉落物是需要精准采集的收获、拒绝精准采集，还是不在意。当玩家用精准采集工具破坏一个按 1.12.2 的判断方式可被精准采集的方块时，即视为精准采集：没有方块实体的完整方块，或玻璃板、铁栏杆、蜘蛛网和末影箱                      |

规则会看到每一次会掉落方块战利品的破坏，与 1.12.2 相同：玩家的破坏，以及爆炸、活塞、流动的水、生物，还有正在挖穿该方块的资源包生物，这些情况会掷出每一条没有 `advancement` 的规则。当爆炸削减方块自己的掉落物时，这里每个掷出的物品以相同的概率保留；经验不会被削减。同一个方块的多条规则全部生效，其中任何一条的 `replace` 都会先清除常规掉落物。

## 玩家战利品

*合成、战利品与交易*

`<namespace>/player_loot/*.json`

文件名可以自己定，只有文件夹会被读取，多个文件会叠加。

游戏没有给玩家提供专属的战利品表：死亡时只掉落物品栏，也没有资源包可以覆盖的表名。RDPL 新增了一张，在玩家死亡时掷出：

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| 键                    | 必需 | 值                 | 默认值  | 作用                                                                  |
| --------------------- | ---- | ------------------ | ------- | --------------------------------------------------------------------- |
| `table`               | 是   | 表名               |         | 玩家死亡时掷出的战利品表                                              |
| `mode`                | 否   | `add` 或 `replace` | `add`   | 表中的物品是加入物品栏，还是取代物品栏                                |
| `rollOnKeepInventory` | 否   | 布尔值             | `false` | 在保留了物品栏的死亡中，是否仍然掷这张表                              |
| `dropLoose`           | 否   | 布尔值             | `false` | 物品是否直接放在地上，而不是加入死亡掉落物                            |

`add` 会把表中的物品与物品栏一起掉落，适合击杀悬赏。`replace` 会丢弃物品栏，只掉落表中掷出的内容。

`rollOnKeepInventory` 关闭时，在 `keepInventory` 下的死亡（以及旁观者的死亡，旁观者总是保留物品栏）不会掷出任何东西。打开它则能让保留物品栏的世界里死亡依然有代价。

多个文件会叠加，各自按自己的条件求值。只要有任何一个适用的条目是 `replace`，物品栏就会在掷骰前被清空一次，因此与它并存的 `add` 条目仍然会生效。

这张表就是一张按名称查找的普通战利品表：它可以放在资源包的 `loot_tables/entities/player.json`，可以是任何原版或模组的表，也可以由 `loot_injections` 触及。战利品上下文：死亡的玩家是被掠夺的实体，击杀者（如果有）是击杀的玩家，伤害来源也已设置，所以 `killed_by_player`、`entity_properties`、`random_chance_with_looting` 等等都能正常工作。

有一个战利品函数是 RDPL 自己的，可以在任何有被掠夺实体的表中使用：`rdpl:killed_name` 会以受害者的名字为掉落物命名。`format` 决定显示名称的样式（`%s` 代表受害者，默认只显示名字），而 `tag` 则把纯名字写入一个 NBT 字符串键，供自己读取它的物品使用。

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**墓碑模组。** 掷出的物品会在任何墓碑模组读取之前加入普通的死亡掉落物，所以它们会与其他物品一起进入墓碑（`replace` 会让表的内容而不是物品栏进入墓碑）。无需任何设置。

`dropLoose` 完全绕过掉落物列表：物品被直接放进世界，所以墓碑模组永远看不到它们；物品栏进入墓碑，表中的物品则散落在地上留给击杀者。适用于属于击杀者而不是受害者墓碑的战利品。没有墓碑模组时，它几乎没有区别。注意：这些物品在下游任何环节取消掉落之前就已经存在，所以凡是在死亡被取消后不应保留的条目，都应当关闭它。

在 `data` 配置类别中把 `playerLoot` 设为 `false`，即可完全关闭这个文件夹。

## 村民与交易

*合成、战利品与交易*

`<namespace>/villagers/*.json`

文件的路径就是职业的注册名，因此 `mypack/villagers/jeweller.json` 注册的是 `mypack:jeweller`，随后由交易在 `profession` 中引用它。

```json
{
  "jobSite": "mypack:gem_bench",
  "workSound": "minecraft:entity.villager.work_toolsmith"
}
```

| 键          | 必需 | 值       | 默认值 | 作用                                                                                                                                                                                                         |
| ----------- | ---- | -------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `jobSite`   | 否   | 方块名称 | 无     | 村民认领以担任该职业的方块，就像锻造台让村民成为工具匠那样。不写时，没有任何方块会分配这个职业：与 1.12.2 一样，生成或繁殖出的村民会被随机分配到它，一直保持，而且没有可工作的方块，因此永远不会补货 |
| `workSound` | 否   | 音效名称 | 无     | 它在该方块处工作时播放的声音                                                                                                                                                                                 |

职业生涯（career）是 1.12.2 的概念，游戏已不再有：一个职业就是一套交易，所以原先有两个职业生涯的资源包要提供两个村民文件。村民的外观是普通纹理，放在 `assets/<namespace>/textures/entity/villager/profession/<name>.png` 和 `textures/entity/zombie_villager/profession/<name>.png`，与游戏存放自己纹理的位置完全相同。一个交易如果同时写了原版 1.12.2 的职业和它的 `career`，例如 `minecraft:smith` 配 `armor`，就会归入该职业生涯后来变成的职业，这里是 `minecraft:armorer`。1.12.2 文件中的 `texture` 和 `zombieTexture`，即资源包自带的整张皮肤，在资源包于这两个路径上没有内容时会被复制过去，皮肤会绘制在村民自己的外观之上。

### 交易

*村民与交易*

`<namespace>/trades/*.json`

文件名可以自己定，只有文件夹会被读取，多个文件会叠加。

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

| 键           | 必需 | 值       | 默认值 | 作用                                                                     |
| ------------ | ---- | -------- | ------ | ------------------------------------------------------------------------ |
| `profession` | 是   | 职业名称 |        | 这是谁的交易。原版 1.12.2 名称连同它的 `career` 也会被读取，见上文       |
| `level`      | 否   | 整数     | `1`    | 它出现在哪个交易等级，1 到 5。更高的等级并入 5 级，即村民所能达到的最高级 |
| `maxUses`    | 否   | 整数     | `12`   | 锁定之前可以使用的次数                                                   |
| `xp`         | 否   | 整数     | `2`    | 每次交易村民获得的、用于升级的经验                                       |

一个堆叠由 `item` 加上 `min`（`1`）和 `max`（默认为 `min`）构成，所以固定价格只需写 `min`。

---

# 生物与危险

## 实体变种

*生物与危险*

`<namespace>/entities/*.json`

文件的路径就是变种的注册名，因此 `mypack/entities/angry_cow.json` 注册的是 `mypack:angry_cow`，`becomes`、刷怪蛋和世界存档引用的都是它。

这里的一个文件会以一个已有的实体为基础造出新的实体。它是一个真正独立的实体，有自己的注册名、在世界中的自己的名称、自己的刷怪蛋，只要你提供还可以有自己的战利品表，它建立在另一个实体的行为之上，而不是取代它。被复制的那个实体不会有任何改变。

下面一次列出所有键。实际的文件只写需要的那几个。

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

### 标识

*实体变种*

| 键              | 必需 | 值                         | 默认值 | 作用                                                                                                                                                  |
| --------------- | ---- | -------------------------- | ------ | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entity`        | 是   | `namespace:name`           | 无     | 作为基础的实体。任何模组的都可以，只要它使用普通的世界构造函数                                                                                        |
| `name`          | 否   | 字符串                     | 无     | 它在世界中、死亡消息中以及刷怪蛋上显示的名称                                                                                                          |
| `showName`      | 否   | 布尔值                     | `false` | 不必注视它也显示名称                                                                                                                                  |
| `profession`    | 否   | `namespace:name`           | 随机   | 对村民而言，它从事的职业                                                                                                                              |
| `baby`          | 否   | 布尔值或 0.0 到 1.0        | `false` | 生成幼体的频率，且会一直保持幼体。`true` 表示总是，数字则表示其中的比例                                                                               |
| `becomes`       | 否   | 列表                       | 无     | 它在生成时可能变成的其他变种，按权重决定。见下文                                                                                                      |
| `egg`           | 否   | 布尔值或对象               | `true` | 刷怪蛋，颜色与它所复制的实体的刷怪蛋相同。`{ "primary": "AABBCC", "secondary": "112233" }` 可指定自己的颜色，`false` 则不提供刷怪蛋                    |
| `keepsBaseBaby` | 否   | 布尔值                     | `false` | 基础实体自己的幼体判定是否也照常进行。不写时，基于僵尸的变种只按 `baby` 所说生成幼体，不会出现僵尸自身判定产生的小孩，也不会出现鸡骑士                |
| `requires`      | 否   | 模组 id 或资源包命名空间列表 | 无     | 除非全部存在，否则该变种会被省略                                                                                                                      |

变种是一个独立的类，所以包含它的世界依赖于制作它的资源包，就像依赖一个模组那样。把这个文件拿掉，那个世界里的这些生物也会随之消失。

**一个蛋或刷怪笼生成混合内容。** 变种是一个独立的类，所以它单独使用时总是严格按它所写的内容生成。`becomes` 是资源包打破这一点的方式：一个列表，列出这个变种在生成时可能变成的变种，每个都带权重，按每个生物分别决定。

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

写上它自己的名字，就是让它保持原样的方式，权重就是概率。把这段写在 `mypack:walker` 上，一个蛋和一条生成条目就会给出大多数的 walker，偶尔夹杂一个小的，就像僵尸蛋偶尔会给你一个幼年僵尸。它发生在生物进入世界的时候，所以对蛋、`/summon` 和自然生成都成立，而到来的生物是所选变种的真实个体，具备该变种所写的一切。刷怪笼则更严格：它只在与所设变种同一基础生物、同一队伍的变种中掷骰，所以僵尸刷怪笼给出的是该队伍的僵尸及其幼体，永远不会出现其他种类或其他队伍的生物，就像原版的僵尸刷怪笼始终是僵尸刷怪笼。通过这种方式得到的变种不会再次转变，所以两个变种可以互相指名而不会陷入空转。

**`baby` 的位置。** 游戏本身没有幼年僵尸这种东西：只有一种僵尸，在生成时判定自己是否为小孩。`baby` 说明频率，所以 `"baby": 0.05` 就是原版的习惯，`"baby": true` 则是总是。变种不会在此之上再叠加僵尸自己的判定，所以不会出现 `baby` 没有要求的小孩或鸡骑士；`keepsBaseBaby` 会把那次判定还给它。这两者是达到同一目的的两种方式，该用哪一种取决于你想要的差异：单独用 `baby` 得到一个有时是幼体的变种，`becomes` 则得到若干在你喜欢的任何方面各不相同的变种，两者混用也完全可以。

### 外观

*实体变种*

| 键           | 必需 | 值                                     | 默认值     | 作用                                                                                         |
| ------------ | ---- | -------------------------------------- | ---------- | -------------------------------------------------------------------------------------------- |
| `texture`    | 否   | `namespace:textures/entity/<file>.png` | 无         | 自己的皮肤，布局与它所复制的实体相同                                                         |
| `leftHanded` | 否   | 布尔值                                 | `false`    | 用另一只手持武器                                                                             |
| `glowing`    | 否   | 布尔值                                 | `false`    | 隔墙也能看到轮廓                                                                             |
| `invisible`  | 否   | 布尔值                                 | `false`    | 不绘制自身，但它的装备仍会绘制                                                               |
| `scale`      | 否   | 浮点数                                 | `1.0`      | 绘制的大小，以及碰撞箱的大小                                                                 |
| `angryScale` | 否   | 浮点数                                 | `scale`    | 它有攻击目标时膨胀到的大小，失去目标后还会保持三秒                                           |
| `width`      | 否   | 浮点数                                 | 基础实体的 | 碰撞箱的宽度，在应用 `scale` 之前                                                            |
| `height`     | 否   | 浮点数                                 | 基础实体的 | 碰撞箱的高度，在应用 `scale` 之前                                                            |
| `bright`     | 否   | 布尔值                                 | `false`    | 无论站在哪里都以全亮度绘制，如同处在正午的阳光下，因此不会被夜晚、阴影或洞穴压暗             |
| `hideArmor`  | 否   | 布尔值                                 | `false`    | 穿着盔甲但不绘制出来                                                                         |
| `hideHeld`   | 否   | 布尔值                                 | `false`    | 对手持的物品同样如此                                                                         |
| `tint`       | 否   | 十六进制颜色                           | 无         | 在绘制时给实体着色                                                                           |
| `tintParts`  | 否   | `body`、`armor`、`held` 的列表         | `["body"]` | 着色作用于哪些部分                                                                           |

`scale` 在两端都同时改变模型和碰撞箱，所以看到什么就能打中什么。会自行改变大小的生物，比如长大的动物或是小孩的僵尸，会围绕它自己选定的大小进行缩放，所以两者不会互相冲突。`angryScale` 在它有目标时让它膨胀，失去目标时恢复为 `scale`。由于客户端从不被告知生物在追捕什么，疾跑标志承担了这一通知：它只在使用 `angryScale` 的变种上设置，其他地方都不设置，所以读取你的变种疾跑状态的模组会看到它在变化。在低矮的天花板下长大是可能的，与史莱姆长大的情况相同，所以差别请保持适度。

`texture` 会取代实体原本使用的纹理而被绑定，无论它继承了哪个渲染器，所以对模组实体和原版实体同样适用。它必须与所绘制的模型相符，因为模型是基础实体的，这只是一张皮肤，而不是新的形状。各图层保留自己的纹理，所以换了皮肤的僵尸身上，盔甲看起来仍然是盔甲。

盔甲只会绘制在渲染器带有盔甲图层的实体上，也就是类人生物和村民。牛或蜘蛛的变种可以携带盔甲并获得其防护，但不会有东西把它绘制出来，所以对这类生物，用 `attributes` 下的 `armor` 通常是让它变坚韧更整洁的办法。`hideArmor` 针对另一种情况：类人生物想让盔甲留在装备栏里，为了防护或为了某个读取它们的模组，却不想让它被看见。

### 它的音效

*实体变种*

| 键            | 必需 | 值     | 默认值     | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| ------------- | ---- | ------ | ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `sounds`      | 否   | 对象   | 基础实体的 | `ambient`、`hurt` 和 `death`，各为一个已注册的音效事件，1.12.2 的名称仍可读取，参见[音效名称](#值列表)。另有三个是基础实体没有对应声音的：`target` 在它每次获得目标时播放一次，`explode` 是它爆炸时的声音，无论是用 `explodes` 自爆还是用 `throws` 投掷 TNT。`throw` 在它用 `throws` 投掷任何东西时播放，取代雪球投掷声，或 TNT 的引信嘶嘶声。`targetVaries` 让每次 `target` 播放的音高在该半音数范围内随机上下偏移，所以 `3` 会向任一方向游移四分之一个八度；`0` 则原样播放。爆炸声会取代游戏自己的爆炸声 |
| `soundVolume` | 否   | 数字   | `1.0`      | 这些声音有多响                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `soundPitch`  | 否   | 数字   | `1.0`      | 播放的音调有多高。小于 1 更低沉，大于 1 更尖细                                                                                                                                                                                                                                                                                                                                                                                                        |
| `silent`      | 否   | 布尔值 | `false`    | 不发出任何声音                                                                                                                                                                                                                                                                                                                                                                                                                                        |

### 生命值、伤害与效果

*实体变种*

| 键                  | 必需 | 值                                              | 默认值        | 作用                                                                                                                                                                                                    |
| ------------------- | ---- | ----------------------------------------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `immuneTo`          | 否   | 伤害类型列表                                    | 无            | 它不受影响的伤害，用 1.12.2 使用的名称，如 `fall`、`drown`、`explosion`、`explosion.player`、`magic`、`indirectMagic`、`mob`、`player`、`inWall` 等，或用伤害类型 id。参见[伤害类型](#值列表)        |
| `fallDamage`        | 否   | 浮点数                                          | `1.0`         | 摔落造成伤害的倍率。`0` 表示免除摔落伤害                                                                                                                                                                |
| `absorption`        | 否   | 浮点数                                          | `0`           | 在生命值之上额外增加的心数                                                                                                                                                                              |
| `creatureAttribute` | 否   | `undefined`、`undead`、`arthropod` 或 `illager` | 基础实体的    | 它被视为哪一类，所以亡灵杀手和治疗药水会据此对待它                                                                                                                                                      |
| `effects`           | 否   | 对象列表                                        | 无            | 它始终拥有的效果：`{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                  |
| `fireproof`         | 否   | 布尔值                                          | `false`       | 完全不会着火，所以永远不会被火或岩浆伤害，白天也不会燃烧                                                                                                                                                |
| `invulnerable`      | 否   | 布尔值                                          | `false`       | 除了虚空和创造模式之外，不受任何伤害                                                                                                                                                                    |
| `attributes`        | 否   | 对象                                            | 无            | `maxHealth`、`movementSpeed`、`attackDamage`、`attackSpeed`、`knockbackResistance`、`followRange`、`armor`。实体通常没有的属性会被赋予它。对近战生物而言，`attackSpeed` 是每秒攻击次数，游戏中为 `1`，所以 `2` 表示攻击频率翻倍 |
| `hurtResistance`    | 否   | 整数，单位为刻                                  | 游戏的 `20`   | 受到一次攻击后，多久之内不会再次受伤。比它的一半更快的攻击会被忽略，所以攻击速度快的攻击者需要一个更低的目标值                                                                                          |
| `ignoresEffects`    | 否   | 效果名称列表                                    | 无            | 永远不会作用在它身上的效果，无论是谁或什么施加的：一次攻击、一瓶喷溅药水、一个信标、一支箭、`/effect`。`all` 会拒绝所有效果，所以变种从一张白纸开始。它自己的 `effects` 仍会加在它身上                   |

### 移动

*实体变种*

| 键               | 必需 | 值               | 默认值     | 作用                                                                                                                                                                                                                                      |
| ---------------- | ---- | ---------------- | ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `jumpMultiplier` | 否   | 浮点数           | `1.0`      | 它比所复制的实体跳得高多少                                                                                                                                                                                                                |
| `maxFallHeight`  | 否   | 整数             | 基础实体的 | 寻路时它愿意下落多高                                                                                                                                                                                                                      |
| `noAI`           | 否   | 布尔值           | `false`    | 站在被放置的地方，什么也不做                                                                                                                                                                                                              |
| `leashable`      | 否   | 布尔值           | `false`    | 可以被拴绳牵着走，即使它所复制的实体从来不能                                                                                                                                                                                              |
| `steerable`      | 否   | 布尔值           | `false`    | 被骑乘时可以操控                                                                                                                                                                                                                          |
| `pathPriorities` | 否   | 对象             | 无         | 它愿意走过什么，如 `WATER`、`LAVA`、`DANGER_FIRE`、`DOOR_WOOD_CLOSED` 以及游戏路径类型的其余各项，每项是一个数字，负数表示绝不通行。1.12.2 的 `DANGER_CACTUS` 和 `DAMAGE_CACTUS` 会被读作 `DANGER_OTHER` 和 `DAMAGE_OTHER`，此版本把仙人掌与甜浆果丛归在这两项下 |
| `stepHeight`     | 否   | 浮点数，单位为方块 | 基础实体的 | 它不用跳跃就能走上多高的台阶                                                                                                                                                                                                              |
| `climbs`         | 否   | 布尔值           | 基础实体的 | 开启时，它会爬上走到的任何墙壁，像蜘蛛一样，无论基础是什么。关闭时，它什么都不爬，连梯子也不爬，蜘蛛则会留在地面上                                                                                                                          |
| `teleports`      | 否   | 布尔值           | `true`     | 末影人或潜影贝是否可以传送。关闭时，它留在原地，白天和在水中也是如此                                                                                                                                                                      |
| `walks`          | 否   | 布尔值           | `false`    | 兔子像其他动物那样行走，而不是蹦跳着移动。只有兔子会读取它                                                                                                                                                                                |

### 水

*实体变种*

| 键                   | 必需 | 值     | 默认值  | 作用                                                                                                                                                                  |
| -------------------- | ---- | ------ | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `breathesUnderwater` | 否   | 布尔值 | `false` | 永远不会溺水，并且会下沉到水底行走，而不是向水面游去。它在地面上仍能找路，所以它无法走出的深水会困住它                                                                |
| `swims`              | 否   | 布尔值 | `false` | 像鱿鱼或守卫者那样在水中移动，永远不会溺水。它在水中而不是在地面上寻路，所以它属于水中，离开水就会搁浅                                                                |
| `amphibious`         | 否   | 布尔值 | `false` | 在陆地上行走，在水中正常游泳，进出水时会改变寻路的方式。它永远不会溺水。它追逐的目标在水边会被忘记，所以每次穿过水边时它都会犹豫片刻                                  |
| `waterSlowdown`      | 否   | 浮点数 | `0.8`   | 水让它减速多少。越高越快                                                                                                                                              |

### 战斗

*实体变种*

| 键              | 必需 | 值               | 默认值              | 作用                                                                                                                                                                                                                                                           |
| --------------- | ---- | ---------------- | ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hostile`       | 否   | 布尔值           | `false`             | 攻击够得着的东西，受伤时会还击。无论基础是什么，敌对变种在游戏中都算作怪物，所以受怪物上限约束。和平模式只在其基础是怪物时才会清除它；其他基础会保留，在和平模式下无法伤害玩家。它会丢掉基础自带的动物任务：繁殖、被引诱、跟随父母、主人或同类、坐下 |
| `targets`        | 否   | 实体名称列表     | 玩家                | 敌对时它去寻找什么。`minecraft:player` 可被识别，尽管玩家并不是已注册的实体                                                                                                                                                                                    |
| `attackReach`   | 否   | 浮点数，单位为方块 | 它的体型            | 近战攻击能打多远。游戏按宽度的两倍计算，所以放大的生物能从更远处打中；这里则直接设定该值                                                                                                                                                                       |
| `knockback`     | 否   | 浮点数           | 基础实体的，`0.4`   | 它的攻击推得多用力。`0` 表示完全不推                                                                                                                                                                                                                           |
| `hitEffects`    | 否   | 布尔值           | `true`              | 它是否会像所复制的实体那样给被击中的对象施加效果：凋灵骷髅的凋零、洞穴蜘蛛的中毒、尸壳的饥饿。关闭时，它只造成伤害                                                                                                                                             |
| `hitFire`       | 否   | 布尔值           | `true`              | 当所复制的实体会这样做时，它是否会点燃被击中的对象：燃烧的僵尸、烈焰人的火球。关闭时，它的任何行为都不会在目标身上引起火焰                                                                                                                                     |
| `passive`       | 否   | 布尔值           | `false`             | 无论它平时的行为如何，都不再攻击任何东西                                                                                                                                                                                                                       |
| `threatLeast`   | 否   | 整数             | `0`                 | 128 格内的玩家或其他携带者必须处于的最低威胁档位，变种才会自然生成。`0` 表示照常生成                                                                                                                                                                           |
| `threatHostile` | 否   | 整数             | `0`                 | 玩家必须处于的最低威胁档位，变种才会主动攻击他们。低于该档位时，变种对该玩家是温顺的，但被打时仍会还击。`0` 表示照常攻击                                                                                                                                       |

`hostile` 还会去掉让这个生物逃跑的行为：原本会躲避玩家或受伤时惊慌的动物，一旦敌对就两者都不会做了，否则它会逃离本该攻击的对象。它需要一个在地面行走的实体，因为它使用的攻击行为与原版给自己的怪物所用的相同。飞行或游泳的基础会被记入日志并保持原样。`passive` 的适用面更广，但只对按原版方式构建的行为起作用，敌意被写进自己的刻更新或伤害代码里的模组，不是资源包能劝退的。

### 装备、掉落物与经验

*实体变种*

| 键                   | 必需 | 值                          | 默认值     | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| -------------------- | ---- | --------------------------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `lootTable`          | 否   | `namespace:entities/<name>` | 基础实体的 | 它掉落什么。不写时，它掉落所复制的实体掉落的东西                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `experience`         | 否   | 整数                        | 基础实体的 | 它掉落多少经验                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `collectsExperience` | 否   | 布尔值                      | `false`    | 像玩家一样收集经验：八格内的经验球会飘向它并在接触时被吸收，其装备上的经验修补会被优先修复，点数按玩家自己的曲线累积成等级，并通过存档保存在生物身上。它所击杀的对象会像玩家击杀一样掉落经验，它的 `digs` 任务所破坏的方块会掉落该方块自己的经验，`block_drops` 的经验掷骰对它也同样生效。它死亡时每级掉落七点，最多一百点，除非开启了 `keepInventory`。带有 `xp` 或 `level` 条件的计分项会把它的总经验和等级记在以其 UUID 命名的一行上，所以函数可以用 `execute if score` 或 `scores={<objective>=N..}` 选择器读取它们。它像玩家一样把等级花在铁砧操作上，参见[铁砧操作](#铁砧操作) |
| `dropChance`         | 否   | 0 到 1                      | `0`        | 每件装备掉落的可能性                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `picksUpLoot`        | 否   | 布尔值                      | `false`    | 捡起它走过的东西                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `equipment`          | 否   | 对象                        | 无         | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet`，各为一个物品名称                                                                                                                                                                                                                                                                                                                                                                                                                                   |

变种掉落所复制的实体掉落的东西，因为战利品表是固定在那个实体自己的代码里的，而不是按名称查找的。`lootTable` 让它指向你自己的表，你再像其他表一样把它放在 `loot_tables/entities/<name>.json`。

### 特殊行为

*实体变种*

| 键               | 必需 | 值           | 默认值          | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ---------------- | ---- | ------------ | --------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `digs`           | 否   | 布尔值       | `false`         | 用手中的工具挖穿它与目标之间的一切：铲子挖泥土、沙子和沙砾，镐挖石头，斧砍木头，并且只能挖该工具的材质能破坏的东西，所以木镐永远打不开铁矿石，不到钻石就没有什么能打开黑曜石。方块耗费的时间与玩家使用该工具时相同，掉落的东西也一样，并且会磨损工具。用 `equipment` 给它工具；赤手空拳时它什么也挖不了，在 `mobGriefing` 关闭的地方也挖不了。它从不寻找绕路的办法：有目标时它径直走向目标，挖掉挡路的一切，工具打不开的方块前它就站着推。需要 `hostile`。它选取目标时不需要看见目标，因为它朝着挖掘的对象本来就在某物的后面 |
| `throws`         | 否   | 布尔值       | `false`         | 从远处把手里拿着的东西扔向目标，如果是 TNT，它会点燃并后退。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `throwAmmo`      | 否   | 整数         | 无              | 它有多少个可扔。不写则永远不会用完                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `throwReload`    | 否   | 整数，单位为秒 | `explosionFuse` | 它的手空着多久才会再拿一个                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `throwRetreat`   | 否   | 整数，单位为秒 | `explosionFuse` | 投掷之后它保持距离多久才转回头                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `throwPower`     | 否   | 浮点数       | `1.0`           | 它投掷得多用力。翻倍大致使射程翻倍                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `throwArc`       | 否   | 浮点数       | `0.35`          | 它抛得多高。越高滞空越久，接近零是平直的猛掷，小于零则向下投掷                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `throwReturns`   | 否   | 布尔值       | `false`         | 它投掷的东西像三叉戟一样飞行：造成该变种的 `attackDamage` 点伤害，没有该属性的基础则为 8，然后像忠诚让三叉戟飞回那样飞回它的手里。它永远不会用完，瞄准目标的方式与骷髅相同，随 `throwPower` 变快，难度越高散布越小，并且投掷者在它飞行时站定不动，所以 `throwAmmo`、`throwReload`、`throwRetreat` 和 `throwArc` 对它不适用。TNT 仍照常投掷                                                                                                                                                                                                       |
| `explodes`       | 否   | 布尔值       | `false`         | 像苦力怕一样在目标身旁自爆。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `explosionPower` | 否   | 数字         | `3.0`           | 爆炸有多大。苦力怕是 3，TNT 是 4。在苦力怕或恶魂基础上，写下这个键或 `explosionFuse` 还会在没有 `explodes` 的情况下设置基础自己的爆炸：苦力怕的爆炸大小和引信、恶魂的火球，均取整数，所以只给了 `explosionFuse` 的恶魂会以 3 而不是它自己的 1 爆炸                                                                                                                                                                                                                                                                                      |
| `explosionFuse`  | 否   | 整数，单位为刻 | `30`            | 它在引爆前嘶嘶作响多久，同时也是苦力怕基础自己的引信                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `explosionFire`  | 否   | 布尔值       | `false`         | 爆炸后留下火焰                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `charges`        | 否   | 布尔值       | `false`         | 从远处冲向目标，接触时造成强烈的击退，像劫掠兽那样，然后休息一阵再进行下一次冲锋。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `pounces`        | 否   | 布尔值       | `false`         | 先蹲伏，然后划着弧线跃向目标，落地时攻击，像狐狸那样。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `sniffs`         | 否   | 整数，单位为方块 | `0`         | 能听到该格数内移动的玩家，无论隔不隔墙，并走向听到声音的地方；潜行或站着不动的玩家听不到，之后被它看见的玩家会成为它的目标。`0` 表示不监听。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                 |
| `fleesWhenHurt`  | 否   | 0.0 到 1.0   | `0`             | 当生命值低于该比例时，脱离战斗并从对手面前逃走，回升到该比例之上后再回来。`0` 表示从不逃跑。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `sleepsByDay`    | 否   | 布尔值       | `false`         | 白天寻找阴影，在那里一动不动，直到夜晚或被攻击。休息时它侧躺着                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `home`           | 否   | 整数，单位为方块 | `0`         | 活动范围限制在它最初站立处周围这么多格内，在其中游荡，走远了就走回来。`0` 表示自由漫游                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `patrols`        | 否   | 布尔值       | `false`         | 与同类一起，由一个领头者带领，在陆地上分长段巡行，像掠夺者巡逻队那样。一起生成的一群会选出一个领头者；其余的与它保持几格之内，领头者一旦有了目标，它们全都有目标。失去领头者的跟随者会自己接任领头。需要 `hostile`                                                                                                                                                                                                                                                                                                                         |
| `swoops`         | 否   | 布尔值       | `false`         | 在目标上空盘旋并俯冲穿过目标，在掠过时攻击，像幻翼那样。变种会被赋予一个飞行辅助，所以它在狩猎时飞行，闲置时落回地面；它需要一个属于生物类的基础，比如鹦鹉，蝙蝠则不行。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                      |
| `gusts`          | 否   | 布尔值       | `false`         | 蓄力后从远处向目标放出一阵风，把目标附近的一切向后向上掀飞，像旋风人的风弹那样。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `gustPower`      | 否   | 浮点数       | `1.5`           | 一阵风掀得多用力。生物的一次攻击是 0.4，强力的击退附魔约为 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |

**投掷而不是冲锋。** `explodes` 让生物冲上去自爆。`throws` 则是另一种性情：它保持距离，把主手里的东西扔向正在战斗的对象，如果恰好是 TNT，它会点燃、投出，并在 TNT 燃烧时后退。

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

投掷会让它的手空下来，因为它把东西扔出去了。随后它在 `throwRetreat` 内保持距离，在 `throwReload` 之后再拿一个，并转回头面对目标：这是一个投掷、后退、装填、逼近的循环。给它设一个 `throwAmmo`，这个循环就会在数量用完时结束，它的手永远空着，由普通攻击接手。不写 `throwAmmo` 则永远不会用完。

这个数量会写入生物本身，所以不会因为区块卸载后又重新加载而重新补满。任何不是 TNT 的东西都会作为物品飞出并落地，这让投掷石头或腐肉的工兵与投掷炸药的工兵一样容易实现。

`explosionFuse` 仍然是被投出的 TNT 的引信，并且会顶替你没写的任何一个计时器，所以在这些键出现之前写的变种，行为与以前完全一样。

投掷本身如何飞行由 `throwPower` 和 `throwArc` 决定。前者是对推力的倍率，而由于推力本来就随距离增长，提高它会延长射程，却不改变投掷在空中滞留的时间。后者是抬升量，它改变轨迹的形状：高了就越过墙壁慢慢落下，接近零就平直猛掷、几乎立刻落地，小于零则朝下方的目标投掷。两者都不动引信，所以抛高的炸药和平飞的炸药，在离手后引爆的秒数相同，这决定了它是在头顶爆炸，还是先落地再等待。它能从多远处投掷取决于它的 `followRange`，而当你靠近到三格以内时它照常逼近，所以它在远处很危险，贴脸时则很普通。

### 任务

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `tasks` | 否 | 列表 | 无 | 游戏拥有的任何任务，按名称并以你选择的优先级添加到变种上，或从其基础实体自带的任务中移除。列表见下 |

**游戏拥有的任何任务。** 上面的键是 RDPL 自己的行为。`tasks` 越过它们，触及原版自身使用的每一个任务，适用于任何基础实体：条目是一个对象，写明 `task` 及其 `priority`，再加上该任务要读取的内容；以 `-` 开头的名称会移除基础实体自带的所有该类任务。优先级从 0 开始最先运行，原版自己的任务位于 1 到 8 之间，因此优先级为 0 的任务胜过基础实体的一切行为，而优先级为 9 的任务只在没有其他任务想运行时才会运行。

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

该列表在 `hostile`、`passive` 和上述各行为生效之后才应用，因此它拥有最终决定权。移动身体的任务会相互排斥：只有当优先级更靠前的任务都没有在移动生物时，它才会运行；而怪物自带的攻击位于优先级 2，所以僵尸上的跳扑或逃跑需要优先级 1，否则永远轮不到它；蜘蛛和狼出于同样的原因，把跳扑排在攻击之前。基础实体已经在运行的任务会被再添加一次，而不是被替换；请先移除旧的。有些任务只有在基础实体具备其所驱动的东西时才有意义：弓箭战斗需要会射击的基础实体，坐下需要可驯服的基础实体，交易需要村民。在无法承载该任务的基础实体上请求它时，日志会说明它需要哪种基础实体，而变种会在没有它的情况下运行。此版本中的村民靠游戏的大脑而不是任务运行，所以下面的村民行只涉及大脑留给任务的部分。

| 键 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `priority` | int | 必填 | 它在基础实体任务中的位置。越小越先运行 |
| `speed` | 数值 | 该任务通常的值 | 任务运行时的移动速度，为步行速度的倍数 |
| `nearSpeed` | 数值 | `1.2` | `avoidEntity`：所躲避的对象靠近后的倍数 |
| `distance` | 数值，格 | 该任务通常的值 | 它观察、跟随、射击或保持距离的远近 |
| `near` | 数值，格 | 该任务通常的值 | `follow`、`followOwner`、`followOwnerFlying`：靠近到多近后停下 |
| `chance` | 数值 | 该任务通常的值 | `wander`：每隔这么多刻掷一次；`wanderAvoidWater`：离开掩护的几率，0 到 1；`watchClosest`、`watchClosest2`：每刻注视的几率，0 到 1 |
| `leap` | 数值 | `0.4` | `leapAtTarget`：跳得多高 |
| `cooldown` | int，刻 | `20` | `attackRanged`、`attackRangedBow`：两次射击之间的刻数 |
| `entity` | 实体名称 | 无 | 任务所寻找、躲避、注视或繁殖的实体。可识别 `minecraft:player` |
| `items` | 物品名称列表 | 无 | `tempt`：玩家手持的诱饵 |
| `sight` | 布尔值 | `true` | `nearestAttackableTarget`、`targetNonTamed`：只针对它看得见的目标 |
| `nearby` | 布尔值 | `false` | `nearestAttackableTarget`：只针对在其自身跟随范围内的目标 |
| `help` | 布尔值 | `false` | `hurtByTarget`：附近的同类一起加入 |
| `memory` | 布尔值 | `false` | `attackMelee`、`zombieAttack`：继续追击已经丢失视线的目标 |
| `close` | 布尔值 | `false` | `openDoor`：通过后把身后的门关上 |
| `nocturnal` | 布尔值 | `false` | `moveThroughVillage`：只在夜间 |
| `scared` | 布尔值 | `false` | `tempt`：玩家移动过快会打破诱惑 |

`List` 列表示该任务所在的列表。`tasks` 是生物所做的事；`targets` 是它挑选攻击对象的方式，而没有对应攻击的目标任务本身不起任何作用。

| 任务 | 需要 | 列表 | 读取 | 作用 |
| --- | --- | --- | --- | --- |
| `attackMelee` | 行走的生物 | `tasks` | `speed`, `memory` | 走向目标并攻击它 |
| `attackRanged` | 会射击的基础实体 | `tasks` | `speed`, `cooldown`, `distance` | 保持距离，并射出其基础实体所射的东西 |
| `attackRangedBow` | 会射击的怪物 | `tasks` | `speed`, `cooldown`, `distance` | 骷髅的弓箭战斗：横向移动、拉弓、放箭 |
| `avoidEntity` | 行走的生物 | `tasks` | `entity`, `distance`, `speed`, `nearSpeed` | 指定实体进入 `distance` 范围内时逃离它 |
| `beg` | 狼 | `tasks` | `distance` | 向手持食物的玩家乞食 |
| `breakDoor` | 任何基础实体 | `tasks` | | 在困难难度下破坏挡路的木门 |
| `creeperSwell` | 苦力怕 | `tasks` | | 在目标旁发出嘶嘶声并爆炸 |
| `defendVillage` | 铁傀儡 | `targets` | | 攻击任何袭击过村民的对象 |
| `eatGrass` | 任何基础实体 | `tasks` | | 像绵羊一样吃草 |
| `findEntityNearest` | 任何基础实体 | `targets` | `entity` | 以最近的指定实体为目标，如同史莱姆或恶魂选定目标的方式 |
| `findEntityNearestPlayer` | 任何基础实体 | `targets` | | 以它能够到的最近玩家为目标 |
| `fleeSun` | 行走的生物 | `tasks` | `speed` | 被阳光照到时寻找阴凉处 |
| `follow` | 任何基础实体 | `tasks` | `speed`, `near`, `distance` | 跟随同类 |
| `followGolem` | 村民 | `tasks` | | 跟随手持虞美人的铁傀儡 |
| `followOwner` | 可驯服的基础实体 | `tasks` | `speed`, `near`, `distance` | 跟随主人，落后太远时传送到主人身边 |
| `followOwnerFlying` | 可驯服的基础实体 | `tasks` | `speed`, `near`, `distance` | 同上，但以飞行方式 |
| `followParent` | 动物 | `tasks` | `speed` | 幼崽紧跟同类的成年个体 |
| `harvestFarmland` | 村民 | `tasks` | `speed` | 收获成熟的作物并重新种下 |
| `hurtByTarget` | 行走的生物 | `targets` | `help` | 反击任何打了它的对象 |
| `landOnOwnersShoulder` | 鹦鹉 | `tasks` | | 停在主人的肩膀上 |
| `leapAtTarget` | 任何基础实体 | `tasks` | `leap` | 从近处向目标跳扑 |
| `llamaFollowCaravan` | 羊驼 | `tasks` | `speed` | 排在被牵着的羊驼后面 |
| `lookAtTradePlayer` | 村民 | `tasks` | | 面向正在与它交易的玩家 |
| `lookAtVillager` | 铁傀儡 | `tasks` | | 时不时向村民递出虞美人并看着它 |
| `lookIdle` | 任何基础实体 | `tasks` | | 时不时四处张望 |
| `mate` | 动物 | `tasks` | `speed`, `entity` | 处于求爱状态时繁殖，对象是同类或所指定的 `entity` |
| `moveIndoors` | 行走的生物 | `tasks` | | 夜幕降临时进入村庄的房屋 |
| `moveThroughVillage` | 行走的生物 | `tasks` | `speed`, `nocturnal` | 沿村庄的道路从一扇门走到另一扇门 |
| `moveTowardsRestriction` | 行走的生物 | `tasks` | `speed` | 走远后走回其家的位置 |
| `moveTowardsTarget` | 行走的生物 | `tasks` | `speed`, `distance` | 向远处的目标逼近 |
| `nearestAttackableTarget` | 行走的生物 | `targets` | `entity`, `sight`, `nearby` | 以最近的指定实体为目标 |
| `ocelotAttack` | 任何基础实体 | `tasks` | | 猫的潜行与扑击 |
| `ocelotSit` | 猫 | `tasks` | `speed` | 坐在箱子、床和点燃的熔炉上。驯服后的豹猫变成了猫，所以这需要猫作为基础实体 |
| `openDoor` | 任何基础实体 | `tasks` | `close` | 打开它经过的木门 |
| `ownerHurtByTarget` | 可驯服的基础实体 | `targets` | | 攻击任何打了其主人的对象 |
| `ownerHurtTarget` | 可驯服的基础实体 | `targets` | | 攻击其主人所打的对象 |
| `panic` | 行走的生物 | `tasks` | `speed` | 受伤或着火时奔逃 |
| `play` | 村民 | `tasks` | `speed` | 孩子们互相玩追逐游戏 |
| `restrictOpenDoor` | 行走的生物 | `tasks` | | 夜间留在村庄的门内 |
| `restrictSun` | 行走的生物 | `tasks` | | 白天待在阴影里 |
| `runAroundLikeCrazy` | 马、驴、骡或羊驼 | `tasks` | `speed` | 甩掉尚未信任的骑乘者 |
| `sit` | 可驯服的基础实体 | `tasks` | | 被命令时坐下 |
| `skeletonRiders` | 骷髅马 | `tasks` | | 玩家靠近时召来骷髅骑手，即陷阱马 |
| `swimming` | 任何基础实体 | `tasks` | | 让头保持在水面以上 |
| `targetNonTamed` | 可驯服的基础实体 | `targets` | `entity`, `sight` | 在尚未被驯服时以指定实体为目标 |
| `tempt` | 行走的生物 | `tasks` | `items`, `speed`, `scared` | 跟随手持 `items` 之一的玩家 |
| `tradePlayer` | 村民 | `tasks` | | 交易时站着不动 |
| `villagerInteract` | 村民 | `tasks` | | 与其他村民聊天 |
| `villagerMate` | 村民 | `tasks` | | 村庄有空位时繁殖 |
| `wander` | 行走的生物 | `tasks` | `speed`, `chance` | 四处闲逛 |
| `wanderAvoidWater` | 行走的生物 | `tasks` | `speed`, `chance` | 闲逛，避开水 |
| `wanderAvoidWaterFlying` | 行走的生物 | `tasks` | `speed` | 在空中闲逛并栖息在树上 |
| `watchClosest` | 任何基础实体 | `tasks` | `entity`, `distance`, `chance` | 注视最近的指定实体，未指定则注视玩家 |
| `watchClosest2` | 任何基础实体 | `tasks` | `entity`, `distance`, `chance` | 同上，在另一个任务运行时仍保持 |
| `zombieAttack` | 僵尸 | `tasks` | `speed`, `memory` | 僵尸的攻击，双臂抬起 |

### 生成与消失

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `despawns` | 否 | 布尔值 | `true` | 关闭后，即使通常会被清除，它也会留下 |
| `despawnAfter` | 否 | int，秒 | 无 | 它在世界中存在这么久之后会悄然消失，无论周围有多远没有人 |
| `persistent` | 否 | 布尔值 | `false` | 永不消失 |
| `ignoresSpawnRules` | 否 | 布尔值 | `false` | 被放在哪里就在哪里生成，忽略它继承来的规则 |
| `spawns` | 否 | 对象列表 | 无 | `creatureType`、`weight`、`min` 和 `max`，与生物群系使用的结构相同。`creatureType` 是[生物类型](#值列表)之一，省略时为 `creature`，并决定该条目加入哪个生成列表；类型为游戏所不认识的条目不会添加任何内容 |
| `biomes` | 否 | 生物群系名称列表 | 每个生物群系 | 这些生成被添加到哪里，可用生物群系 id，或 1.12.2 中对原版生物群系显示的名称，如 `Extreme Hills`。如果既没有这个也没有 `biomeTypes`，每个生物群系都会采用，包括下界和末地，而被两个列表都匹配到的生物群系，每个生成只采用一次 |
| `biomeTypes` | 否 | 生物群系类型列表 | 无 | 同上，但按类型词 |

**有保质期的生物。** `despawnAfter` 以秒为单位，从生物首次进入世界的那一刻起计时，时间一到就悄然将其带走：没有死亡，没有掉落物，没有声音，就像它自己走开并被清除了一样。计时被写入生物自身，因此在存档和重新载入之间会继续运行，而不是每次区块回来时重新开始。

它自成一体，并不是对 `despawns` 和 `persistent` 所管辖规则的微调。后两者决定游戏能否因为生物远离所有人而清除它；这一项则是承诺它无论如何都会在设定的时间离开。生物可以既是 `persistent` 又有保质期，这正适合为一场战斗或活动而召唤、不应比它们存活更久的东西。

计时依据世界时间运行，因此无人游玩时会暂停，也不会计入区块处于未加载状态的那些分钟。

### 网络

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `trackingRange` | 否 | int | `80` | 客户端在多远处会被告知它的存在 |
| `trackVelocity` | 否 | 布尔值 | `true` | 除位置外还发送它的速度。关闭可为几乎不动的东西节省流量 |
| `trackingFrequency` | 否 | int | `3` | 多久一次，单位为刻 |

### 存储

*实体变种*

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

`storage` 为任何实体的变种提供物品槽位、流体储罐和能量缓冲，每一项只有在写了其对象时才会提供。每一项都作为实体的物品、流体或能量能力对外提供，因此任何向实体输送物品、流体或能量的东西都能够到它。如果基础实体自己就响应该能力，比如生物响应其双手和护甲，马或箱子矿车响应其物品栏，则由资源包的存储取而代之作出响应，对每一侧都是如此。玩家潜行并右键点击实体即可打开界面。内容物会随实体一起保存。

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `items` | 否 | 对象 | 无 | 为实体提供物品槽位。区域为 3 行 9 列：每个流体条或能量条占一行，槽位占用其余部分，因此两条都有时为 1x9，有一条时为 2x9，都没有时为 3x9。没有 `items` 时只显示条 |
| `fluid` | 否 | 对象 | 无 | 一个流体储罐 |
| `energy` | 否 | 对象 | 无 | 一个 Forge Energy 缓冲 |
| `dropsOnDeath` | 否 | 布尔值 | `true` | 实体死亡处，存储的物品会作为散落的物品掉出。`false` 则丢失它们。流体和能量无论如何都会丢失 |
| `runsDry` | 否 | `stops`、`slows` 或 `hurts` | `stops` | 当它无法支付整整一秒的 `use` 时会发生什么。`stops`：它不再思考，原地站着，其任务、目标和行为全部闲置，直到被重新填满，不过它仍会下落，也仍能被推动。`slows`：它以一半速度移动。`hurts`：它每秒受到 1 点伤害，如同饥饿所致，因此 `immuneTo` 中写入 `starve` 可使其免疫 |

**靠随身携带的东西运转。** 储罐或缓冲上的 `use` 是一项运行开销：实体每秒从中取走这么多，无视 `transfer`、`buckets` 和各个过滤器。当任何一个所存的量不足整整一秒的 `use` 时，实体就耗尽了：不再取走任何东西，由 `runsDry` 决定会发生什么，而一旦重新填满，它就立刻恢复正常。只有生物才会消耗；在并非活物的基础实体上，如矿车，`use` 不起作用。

`items`：

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `filter` | 否 | 过滤条目列表 | 无 | 槽位接受什么。没有它时，槽位接受任何东西 |

`fluid`：

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `capacity` | 是 | int，mB | 无 | 储罐容量 |
| `filter` | 否 | 过滤条目列表 | 无 | 储罐接受哪些流体。没有它时，接受任何流体 |
| `buckets` | 否 | 布尔值 | `false` | 不潜行时用桶或其他流体容器右键点击，会把其中的流体倒入储罐，或从储罐中灌满。没有移动任何流体的点击则留给实体处理 |
| `use` | 否 | int，每秒 mB | `0` | 实体在存活的每一秒从储罐中消耗多少。`0` 表示无消耗 |

`energy`：

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `capacity` | 是 | int，FE | 无 | 能存多少能量 |
| `transfer` | 否 | int，FE | 无限制 | 一次操作中输入或输出的最大能量 |
| `use` | 否 | int，每秒 FE | `0` | 实体在存活的每一秒消耗多少能量。`0` 表示无消耗 |

过滤条目。第一个匹配的条目起决定作用，没有任何条目匹配的东西都会被拒绝：

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `item` | 三者之一 | 物品 id | 无 | 一个物品，写作 `namespace:name` |
| `tag` | 三者之一 | 物品标签 id | 无 | 该标签下的每个物品，如 `c:ingots/iron` |
| `fluid` | 三者之一 | 流体 id | 无 | 以 id 指定的流体，如 `minecraft:water`。只由流体过滤器读取 |
| `max` | 否 | int | `0` | 一次最多持有的数量，按所有槽位合计，流体则以 mB 计。`0` 表示无限制 |

## 暴露设置

*生物与危险*

`<namespace>/exposures/*.json`

文件的路径就是危险的名称，其死亡消息来自语言键 `death.attack.rdpl.<file name>`。暴露设置只在 `load` 开启且 `vanillaClients` 关闭时加载。

资源包自定义的危险：指定的方块、物品和维度，会让站在这些方块附近、携带这些物品或停留在这些维度中的玩家受到暴露，分为若干等级，每个等级施加效果和周期性伤害。危险也可以从附近的生物和玩家那里染上，或随雨落下（[传染与天气](#传染与天气)）。一个文件定义一种危险；多个文件并行运行。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `blocks` | 五者之一 | `block` 或 `block=level` 的列表 | | 会让站在附近的玩家受到暴露的方块。不写等级则为 1 |
| `items` | 五者之一 | `item` 或 `item=level` 的列表 | | 会让携带或穿戴它们的玩家受到暴露的物品 |
| `dimensions` | 五者之一 | `dim` 或 `dim=level` 的列表 | | 会让其中任何玩家受到暴露的维度 id |
| `levels` | 是 | 等级列表 | | 严重程度的阶梯，第一项是 1 级。玩家获得任何来源所达到的最高等级 |
| `immunity` | 否 | 药水名称 | 无 | 带有该效果的人完全不会受到暴露 |
| `scanInterval` | 否 | 刻 | `20` | 多久检查一次周围环境和物品栏 |
| `range` | 否 | 格 | `10` | 方块的暴露范围有多远，为球形 |
| `sourcesForNextLevel` | 否 | int | `0` | 附近有这么多个同一等级的来源，就会把该等级再推高一级。`0` 表示关闭此功能 |
| `skipsCreative` | 否 | 布尔值 | `true` | 创造模式和旁观模式的玩家不受影响 |

### 等级

*暴露设置*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `effect` | 是 | 药水名称 | | 在玩家身上标记该等级的效果。伤害由它的存在驱动，因此它应当是资源包为此定义的效果 |
| `damage` | 否 | 半颗心 | `0` | 该等级持续期间，每隔 `damageInterval` 刻造成的伤害。它无视护甲 |
| `damageInterval` | 否 | 刻 | `160` | 这种伤害多久落下一次 |
| `effects` | 否 | 效果列表 | 无 | 一并施加的额外效果，结构与药水类型所用的相同。不写 `duration` 时，它们跟随扫描窗口 |

等级效果会比下一次扫描稍晚结束，因此走开后它们会自行失效。因暴露伤害而死亡时，其消息读取自 `death.attack.rdpl.<file name>`，由资源包的语言文件提供。

### 传染与天气

*暴露设置*

另外两种来源，写在同一个文件中。携带者和受到暴露的携带体会把危险传给周围的易感者，而雨或雷暴会让被其淋到的玩家受到暴露。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `carriers` | 五者之一 | `entity` 或 `entity=level` 的列表 | | 总会以该等级传播危险的生物，或 `minecraft:player`。不写等级则为 1 |
| `contagious` | 否 | 布尔值 | `false` | 任何受到暴露的对象都会以其所处的等级传播危险 |
| `catchers` | 否 | 实体名称列表 | `minecraft:player` | 谁会染上。生物只会从携带者和携带体身上染上，绝不会从方块、物品或天气染上 |
| `contagionRange` | 否 | 格 | `4` | 携带者或携带体的传播范围有多远，为球形 |
| `contagionChance` | 否 | `0` 到 `1` | `0.1` | 每次扫描携带者或携带体时，范围内每个易感者染上的几率 |
| `contagionDuration` | 否 | 刻 | `1200` | 染上的危险保持所染等级的时长。再次染上会重新计时 |
| `weather` | 五者之一 | `kind` 或 `kind=level` 的列表 | | `rain` 让被雨淋到的玩家受到暴露：头顶是开阔天空，且位于会下雨的生物群系。`thunder` 在风暴期间计入 |
| `weatherDimensions` | 否 | `dim` 列表 | 每个维度 | 天气会造成暴露的维度 id |

染上的危险在扫描中算作又一个来源，与任何其他来源一样取最高等级，`immunity` 同样能防护它。除非 `contagionRange` 和 `contagionChance` 都大于 `0`，并且文件写明了 `carriers` 或设置了 `contagious`，否则什么都不会传播，而且只有当某个文件这样做时，才会检查生物。

---

# 世界

## 世界模板

*世界*

`<namespace>/worldtemplates/*.json`

文件的路径就是模板的名称，配置选项 `worldTemplate` 可以指名它以直接选用。

把世界的形态汇集到一个文件中，这样资源包就能一次性提供整个世界，而不必让玩家去设置十几个配置选项。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 否 | 字符串 | 文件名 | 显示在日志和报告中 |
| `default` | 否 | 生物群系名称或 `void` | `void` | 填充被屏蔽移除的生物群系的内容。`void` 会留下虚空生物群系。未注册的生物群系会被记入日志，并改用虚空。`fallback` 是同一个键的另一个名字 |
| `roles` | 否 | 角色到生物群系的对象 | 无 | 充当特定角色的生物群系：`ocean`、`river`、`beach`、`mushroom`、`swamp`、`hills`、`mountain`、`jungle`、`forest`、`savanna`、`sandy`、`mesa`、`snowy`、`wasteland`、`plains` 和 `water`，无论文件中按什么顺序书写，都按此顺序检查，因此一个既是海洋又是雪地的被屏蔽生物群系会采用海洋角色。指名 `void` 或未注册生物群系的角色会落到下一个。角色只在模板的 `dimensions` 中生效；在其他地方，被屏蔽的生物群系会变成虚空 |
| `structures` | 否 | [结构名称](#值列表)到布尔值的对象 | 无 | 开启或关闭的原版结构 |
| `settings` | 否 | 对象 | 无 | 模板所设置的配置值 |
| `dimensions` | 否 | 维度 id 列表 | 每个维度 | 它适用于哪些维度 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该模板 |

`settings` 使用与配置相同的键名，因此没有需要学习的对照表。

哪个模板生效由 `worldTemplate` 配置选项决定。保持 `auto` 时，提供模板的最高优先级资源包胜出，遵循与其他一切相同的顺序；当不止一个资源包提供模板时，日志会列出它们全部以及生效的那一个，因为其余的都不起作用，连其设置也一样。在那里指名一个模板即可直接选用。内置了五个，可以这样指名：`void`、`vanilla`（海洋、河流、海滩、蘑菇岛、沼泽和丘陵保持为游戏自己的，其他各处为平原）、`ocean`（保留河流和海滩，其他各处为海洋）、`plains` 和 `desert`。`auto` 绝不会选用内置模板。

**同一个生物群系可以有不同的建法。** `settings` 内的 `biomes` 对象为指定的生物群系保存它自己的村庄设置，因此沙漠村庄铺砂岩街道，而平原村庄铺混凝土，两者无需分成不同的资源包。可以用生物群系 id 指名，如 `minecraft:desert`，也可以用本模组映射到生物群系标签的类型词之一（`sandy`、`snowy`、`desert`、`forest`、`jungle`、`mountain`、`ocean`、`swamp`、`hot`、`cold` 等等），或写出完整的标签，`#minecraft:is_forest`；精确 id 先于类型被检查，因此可以为某一个生物群系覆盖一条通用规则。凡是某一节中未指名的内容，都回落到其上方的普通设置。

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

道路、桥梁、铁路、地铁、车站或下水道所用的每一个方块设置都响应这一点，加权混合的语法在节内的使用方式与在节外相同。生物群系在建造每个部件时读取，而且地面每次变换生物群系的地方，方块都会重新取用，因此驶出沙漠的道路或铁路会在边界处当场更换材料。世界加载时的一行日志会说明资源包提供了多少个节并列出其名称，开启调试后，每个生物群系还会说明它采用了哪一节，或者说它没有采用任何一节以及它本会响应什么。

## 游戏规则

*世界*

`<namespace>/gamerules/*.json`

文件名由你自己选择，只读取该文件夹，多个文件会叠加。

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

每个键是这些规则所属世界的 id，`minecraft:overworld`、`minecraft:the_nether`、`minecraft:the_end`，资源包自己的，或者某个模组使用的；1.12.2 的数字 `0`、`-1` 和 `1` 仍被当作原版的这三个。值是字符串，与 `/gamerule` 命令中一样，所以写 `"false"` 而不是 `false`。这些规则应用于新世界。文件中省略的规则在该世界中使用游戏自己的默认值，而不是存档其余部分所用的值，带有该资源包的客户端读取同样的规则。维度文件则在 `gameRules` 块中携带同样的规则，那只会应用于该世界。

## 生物群系

*世界*

`<namespace>/biomes/*.json`

文件的路径就是生物群系的注册名，因此 `mypack/biomes/ruby_forest.json` 注册的是 `mypack:ruby_forest`。`name` 只是向玩家显示的内容，而语言文件中的 `biome.mypack.ruby_forest` 则以每种语言说出它。

一次展示所有的键。实际的文件只写需要的那些。

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

### 生物群系

*生物群系*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 否 | 字符串 | 文件名 | 向玩家显示的名称 |
| `types` | 否 | 生物群系类型列表 | 推断 | 把该生物群系写入这些类型词所代表的标签，如 `forest`、`cold`、`wet` 或 `nether`，以便其他模组找到它。省略时，类型按游戏当年推断的方式从生物群系推断：三棵树或更多则为 `forest` 或 `jungle`，否则为 `plains`，由温度和降水量得出 `hot`、`cold`、`wet` 和 `dry`，由树木数量得出 `sparse` 或 `dense`，由 `snow` 得出 `snowy`，由沙子、菌丝或陶瓦地面得出 `sandy`、`mushroom` 或 `mesa` |
| `baseBiome` | 否 | 生物群系名称 | `minecraft:plains` | 用于复制设置的现有生物群系。不是游戏或某个模组所提供的生物群系时会被记入日志，并改用平原 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该文件 |

在此版本中，生物群系是数据包条目，会替你写入 `worldgen/biome/` 下，而其下的地形属于噪声设置而非生物群系，这就是为什么没有 `baseHeight` 或 `heightVariation`：陆地的形状取决于气候把该生物群系放在何处，与游戏自己的生物群系一样。1.12.2 的 `id` 会被读取并忽略。

### 气候

*生物群系*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `temperature` | 否 | 浮点数 | `0.5` | 低于 0.15 下雪，高于 1.0 为沙漠般的炎热 |
| `rainfall` | 否 | 浮点数，0 到 1 | `0.5` | 有多潮湿 |
| `rain` | 否 | 布尔值 | `true` | 是否会出现天气 |
| `snow` | 否 | 布尔值 | `false` | 雨是否以雪的形式落下。只有温度低于 0.15 的地方才会下雪，而这一项不会改变温度，因此较温暖的生物群系仍会下雨 |

### 地面与颜色

*生物群系*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `topBlock` | 否 | 方块名称 | 草方块 | 地表方块 |
| `fillerBlock` | 否 | 方块名称 | 泥土 | 地表正下方 |
| `stoneBlock` | 否 | 方块名称 | 石头 | 地面的主体 |
| `waterColor` | 否 | 十六进制颜色 | `FFFFFF` | 水的色调 |
| `grassColor` | 否 | 十六进制颜色 | 取自气候 | 草的色调，取代温度和降水量所给出的颜色 |
| `foliageColor` | 否 | 十六进制颜色 | 取自气候 | 树叶的色调，方式相同 |

### 装饰与生成

*生物群系*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `decoration` | 否 | 对象 | 基础生物群系的值 | 每区块的数量，改变基础生物群系已经放置的内容。它读取的名称有 `trees`、`flowers`、`grass`、`deadbush`、`mushrooms`、`bigmushrooms`、`reeds`、`cacti`、`sand`、`gravel`、`clay` 和 `waterlily`，以及开关 `falls`（湖泊和泉水）、`pumpkins`、`desertwells`、`ice`（冰刺和冰块地带）、`fossils` 和 `rocks`（森林巨石），其中任何大于零的值都保持基础生物群系自己的比率，零或更小则移除该特征，还有 `extratreechance`，即多一棵树的百分比几率，其中 `0` 也会移除基础生物群系所掷出的额外那棵树。对基础生物群系不放置的种类写数量不会添加任何内容；为此请写一个世界生成条目。其他任何名称都会被记入日志并忽略 |
| `spawns` | 否 | 对象列表 | 原版列表 | 见下 |
| `keepDefaultSpawns` | 否 | 布尔值 | `false` | 在你的列表之外保留原版的列表 |
| `spawnChance` | 否 | 浮点数，小于 1 | `0.1` | 陆地首次生成时，再放置另一群的可能性。只要成功，游戏就会继续掷骰，因此 1 永不停止，会一直填满世界直到没有空间。任何大于等于 0.99 的值都会被拒绝，改用 0.99 |
| `spawnRates` | 否 | 由 `surfaceDay`、`surfaceNight`、`undergroundDay`、`undergroundNight` 到倍数的对象 | 无 | 敌对生物在此处多久生成一次，取代全局设置。见下 |

生成条目接受 `entity`（必填）、`type`（`creature`，为 `monster`、`creature`、`ambient` 或 `water` 之一，`water_creature` 这类名称中的下划线可有可无）、`weight`（`10`）、`min`（`1`）和 `max`（`min`）。

`spawnRates` 只涉及敌对生物，别无其他。它只接受四个键，不接受其他键：`surfaceDay` 和 `surfaceNight` 用于看得见天空的地方，`undergroundDay` 和 `undergroundNight` 用于看不见的地方。每一项都是敌对生物获准出现的频率的倍数，`1` 是普通比率，`0` 完全阻止它们，小于 1 会降低部分尝试，而大于 1 会放行游戏本会拒绝的尝试，所以 `2` 是两倍之多。省略某个键表示该生物群系不作决定，转而使用该时间和地点的全局设置。写在这里的其他任何内容都不是键，会被忽略，因此以生物类型命名的比率完全不起作用。

### 生成位置

*生物群系*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `placement` | 否 | 对象 | 无 | 它在何处生成。见下 |
| `villageType` | 否 | `oak`、`sandstone`、`acacia` 或 `spruce` | 无 | 站在此处的村庄用什么建造：平原、沙漠、热带草原或针叶林村庄。留空则建造平原村庄，与没有这个键时一样 |

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `climate` | 否 | `icy`、`cool`、`medium`、`warm` 或 `desert` | 无 | 它加入哪个气候带，即主世界自己的生物群系所依据分配的那五个。省略、`weight` 为 0 或写了此处未列出的气候时，该生物群系会被注册但永远不会被放置，除非模板的 `roles`、维度的 `biome` 或高度带要求它 |
| `weight` | 否 | int | `10` | 在该气候带中与相邻生物群系相比被选中的频率 |
| `villages` | 否 | 布尔值 | `false` | 允许生成村庄 |
| `strongholds` | 否 | 布尔值 | `false` | 允许生成要塞 |
| `playerSpawn` | 否 | 布尔值 | `false` | 世界出生点可以放在这里 |

### 高度带

*生物群系*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `minHeight` | 否 | int | 无 | 该生物群系作为 3D 生物群系接管的最低 y。设置任一高度都会把该生物群系变成一个带：在带外，该列保持自己的生物群系，而在带内，世界的每个 4×4×4 单元格都报告为这一个 |
| `maxHeight` | 否 | int | 无 | 该带的最高 y |
| `replaces` | 否 | 生物群系名称列表 | 每个生物群系 | 把该带限制在自身生物群系在此列出的那些列，因此高山带可以覆盖在山地之上而不触及其他地方。该列自身的生物群系是其地表处的那一个。没有 `minHeight` 或 `maxHeight` 时它不起作用 |

### 随高度变化的温度

*生物群系*

**随高度变化的温度。** 生物群系随海拔升高而变冷，这就是山顶有雪、某条线以上不下雨的原因。三个 `terrain` 键可以移动这条曲线，这对于地面远高于或远低于游戏所假定高度的维度很重要。不设置时，游戏自己的曲线保持不变，因此不去动它们的资源包什么都不会改变。

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

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | int | `80` | 曲线的度量起点高度。在此高度或以下，生物群系报告其自身未经改动的 `temperature` |
| `biomeTemperatureHeightFactor` | 浮点数 | `-0.00125` | 高于该高度后每格温度变化多少，即游戏自己的每 40 格 0.05。负值随海拔变冷，正值变暖 |
| `biomeTemperatureScaleMaxY` | int | 无 | 曲线停止的高度，这样比游戏自身更高的世界就不会一路冷却到顶部。不设置时，曲线延伸到世界顶部 |

## 维度

*世界*

`<namespace>/dimensions/*.json`

文件的路径就是维度的 id，因此 `mypack/dimensions/verdant.json` 就是 `mypack:verdant`，传送门、门、游戏规则文件和 `/execute in` 所指的都是它。此版本没有数字 id，1.12.2 的 `id` 或 `suffix` 会被读取并忽略。

一次展示所有的键。实际的文件只写需要的那些。

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
    "rain": { "particle": "minecraft:rain", "sound": "minecraft:weather.rain", "volume": 0.2, "interval": 3, "color": "#88AAFF", "snowColor": "#FFFFFF", "angle": 30, "heading": 90 }
  },
  "gameRules": { "doMobSpawning": "false" }
}
```

### 顶层

*维度*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `gameRules` | 否 | 对象 | 无 | 只在此处适用的规则 |
| `portal` | 否 | 对象 | 无 | 开启此维度的框架。见[用框架开启维度](#用框架开启维度) |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该文件 |

在此版本中，维度是数据包条目：维度类型和噪声设置会替你写入资源包的命名空间下，因此原版客户端在加入时会被告知它的存在，并像前往其他任何维度一样前往那里。维度在世界下保有它自己的存档文件夹，以其 id 命名，在有人处于其中，或有 `forceload` 保持着某个区块时加载。

### `terrain` 块

*维度*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `type` | 否 | `overworld`、`flat`、`void`、`nether`、`end` | `overworld` | 由游戏的哪个生成器来构建它，其噪声设置会被复制并由下面的键修改 |
| `minHeight` | 否 | int，16 的倍数 | 该类型自身的值 | 维度的底部。低于该类型自身的值时，会在地形之下形成深层世界，见[深层世界](#深层世界) |
| `maxHeight` | 否 | int，16 的倍数 | 该类型自身的值 | 其顶部之上的那一格方块 |
| `generatorOptions` | 否 | 对象、文本或列表 | 无 | 对于 `overworld` 和其他类型，是一个对象，或 1.12.2 所写的该对象的文本，包含 `seaLevel`、`useLavaOceans`，以及设为 false 以在此维度中省略相应内容的 `useCaves`、`useRavines`、`useDungeons`、`useLavaLakes`、`useStrongholds`、`useVillages`、`useMineShafts`、`useTemples`、`useMonuments` 和 `useMansions`。对于 `flat`，是自下而上的各层，如 `"minecraft:bedrock"`、`"59*minecraft:stone"`、`"3*minecraft:dirt"`、`"minecraft:grass_block"`，这也是默认的地面，或者 1.12.2 的超平坦文本，其生物群系编号设定生物群系，其 `decoration`、`lava_lake` 和结构名称的读取方式与主世界的 `generatorOptions` 相同 |
| `structures` | 否 | 布尔值 | `true` | 原版结构是否生成 |

### `biomes` 块

*维度*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `source` | 否 | `inherit`、`single` | `inherit` | `inherit` 无论地形类型如何都使用主世界自己的生物群系图，因此 `nether` 或 `end` 维度会在自己的地面上得到主世界的生物群系；`single` 在各处使用同一个生物群系。`flat` 维度无论哪种都只有一个生物群系，即 `single` 指定的那个或其超平坦文本所指的那个 |
| `biome` | 使用 `single` 时 | 生物群系名称 | `minecraft:plains` | 是哪个生物群系 |

### `sky` 块

*维度*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | 否 | 布尔值 | `true` | 日光是否能到达这里 |
| `surfaceWorld` | 否 | 布尔值 | `true` | 地图和指南针的行为是否与主世界相同 |
| `respawn` | 否 | 布尔值 | `true` | 玩家是否在此重生 |
| `respawnDimension` | 否 | 维度 id | 无 | 他们改为在哪里重生 |
| `spawning` | 否 | 布尔值 | `true` | 生物是否生成。关闭会阻止一切生成，包括刷怪笼，无论 `spawning` 分组怎么说 |
| `nether` | 否 | 布尔值 | `false` | 在传送门和天花板方面被视为下界 |
| `beds` | 否 | 布尔值 | `true` | 关闭后，床会爆炸 |
| `waterVaporizes` | 否 | 布尔值 | `false` | 水会蒸发 |
| `cloudHeight` | 否 | int | `128` | 云所在的高度。指名此维度的 `cloudHeight` 设置，或不带维度的设置，会胜过它 |
| `cloudColor` | 否 | 十六进制颜色 | 无 | 云的色调 |
| `cloudSpeed` | 否 | 浮点数 | `1.0` | 云飘动的速度。`0` 使其静止，负值使其反向 |
| `cloudLayers` | 否 | 对象列表 | 无 | 多个云层。见[雾、光照、云与热浪](#雾光照云与热浪) |
| `groundLevel` | 否 | int | `63` | 海平面，用于地平线、出生点搜索，以及传送门到达或在虚空上方坠落后落地的位置 |
| `movementFactor` | 否 | 浮点数 | `1.0` | 与主世界的距离比。下界使用 8 |
| `fogColor` | 否 | 十六进制颜色或 `sample` | 无 | 正午的雾色调。夜间会像原版的雾一样变暗。`sample` 把天空与玩家周围的地面混合 |
| `showFog` | 否 | 布尔值 | `false` | 浓雾，如同在下界 |
| `fogDensity` | 否 | 浮点数，0 到 1 | `0.0` | 雾有多浓。`0` 保持原版距离，`1` 把它收紧到 8 格 |
| `fogGroundWeight` | 否 | 浮点数，0 到 1 | `0.5` | 在 `fogColor: sample` 下，地面相对于天空占多大比重 |
| `skyColor` | 否 | 十六进制颜色 | 无 | 正午的天空色调。夜间会变暗，雨天和雷暴时会像原版天空一样发灰 |
| `fixedTime` | 否 | int，刻 | 无 | 锁定一天中的时间 |
| `sunriseColors` | 否 | 布尔值 | `true` | 日出和日落是否带有色调 |
| `ambientLight` | 否 | 浮点数，0 到 1 | `0.0` | 各处的最低光照 |
| `lightSkyColor` | 否 | 十六进制颜色 | 无 | 日光照在方块和生物上的色调 |
| `lightBlockColor` | 否 | 十六进制颜色 | 无 | 火把光和其他方块光的色调 |
| `skyFactor` | 否 | 浮点数，0 到 1 | `1.0` | 日光看起来有多亮。只在客户端绘制，因此生物生成不会改变 |
| `starBrightness` | 否 | 浮点数，0 到 1 | 无 | 星星有多亮 |
| `sunBrightness` | 否 | 浮点数，0 到 1 | `1.0` | 太阳绘制得有多亮 |
| `moonBrightness` | 否 | 浮点数，0 到 1 | `1.0` | 月亮绘制得有多亮，配合 `bodies` 时则为除太阳外的每个天体 |
| `heat` | 否 | 对象 | 无 | 覆盖在视野上的热浪闪烁。见[雾、光照、云与热浪](#雾光照云与热浪) |
| `renderSky` | 否 | 布尔值 | `true` | 关闭后，不绘制天空、太阳、月亮或星星，只留下雾色 |
| `renderClouds` | 否 | 布尔值 | `true` | 关闭后，不绘制云 |
| `renderWeather` | 否 | 布尔值 | `true` | 关闭后，不绘制雨或雪 |
| `sun` | 否 | 对象 | 无 | 你自己的太阳。见[天空渲染器](#天空渲染器) |
| `bodies` | 否 | 对象列表 | 无 | 挂在天空中的行星和卫星。见[天空渲染器](#天空渲染器) |
| `stars` | 否 | 对象 | 无 | 你自己的星空。见[天空渲染器](#天空渲染器) |

### 天空渲染器

*维度*

设置 `sun`、`bodies` 或 `stars` 中的任何一个，都会把原版天空换成 RDPL 自己的，它绘制与原版相同的穹顶、日出光辉和虚空，但太阳、其他天体和星星取自资源包。它只在客户端绘制，专用服务器绝不会加载它。`renderSky: false` 仍然优先，什么都不绘制，而 `renderClouds: false` 是制作无云天空的方法。

没有 `bodies` 时，原版的月亮及其月相保持不变。有了 `bodies`，该列表就是太阳之外的一切，因此空列表就是没有月亮的天空。

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `sun.texture` | 否 | 纹理路径 | 原版太阳 | 太阳的图片 |
| `sun.size` | 否 | 浮点数 | `30` | 在距离 100 处太阳宽度的一半。`0` 将其隐藏 |
| `bodies[].texture` | 是 | 纹理路径 | | 天体的图片 |
| `bodies[].size` | 否 | 浮点数 | `20` | 在距离 100 处其宽度的一半。原版月亮为 `20` |
| `bodies[].angle` | 否 | 浮点数，度 | `180` | 它在太阳轨迹上落后于太阳多远。`180` 是原版月亮所在的位置。关闭 `followsTime` 时，从正上方起量，因此 `0` 是天顶，`90` 是地平线 |
| `bodies[].tilt` | 否 | 浮点数，度 | `0` | 它偏离太阳轨迹多远，向北或向南 |
| `bodies[].followsTime` | 否 | 布尔值 | `true` | 关闭后，它静止悬挂在天空中，而不随太阳一同转动 |
| `stars.count` | 否 | int | `1500` | 有多少颗星星 |
| `stars.size` | 否 | 浮点数 | `0.15` | 最小的星星；最大的再大出三分之二 |

### 雾、光照、云与热浪

*维度*

这些键位于 `sky` 块中，与较早的键并列，较早的键照常工作。它们全都只在客户端绘制，因此专用服务器会忽略它们，任何存档也不会改变。

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

`fogColor: "sample"` 每秒一次读取玩家周围 33×33 格范围内最上层的方块，按一天中的时间为其地图颜色打光，再与天空颜色混合。雾会逐渐过渡到每个新的采样。`fogDensity` 可与采样得到的雾色、设定的雾色或没有雾色搭配使用。在水下、岩浆中和失明时，原版的雾保持不变。

`lightSkyColor` 和 `lightBlockColor` 为光照图着色，因此每个被照亮的方块和生物都带上这种色调。`skyFactor` 缩放日光看起来的亮度，而服务器为生物生成和作物所计算的光照等级保持不变。

没有 `cloudLayers` 时，`cloudSpeed` 改变位于 `cloudHeight` 的那一层原版云的速度。有了 `cloudLayers`，每个条目是自己的一层，而 `cloudHeight`、`cloudSpeed` 和 `cloudColor` 会补上条目所省略的内容。`renderClouds: false` 仍然一层也不绘制。

`sunBrightness` 和 `moonBrightness` 在原版的雨天淡化之外，再让太阳和月亮淡出，对原版天空和你自己的[天空渲染器](#天空渲染器)均有效。

`heat` 在玩家所处生物群系至少与 `minTemperature` 一样温暖时，在视野上覆盖一层波动的闪烁。沙漠是 2.0，平原是 0.8。闪烁在几秒内淡入淡出，在水下保持关闭。它需要显卡的着色器支持，并在另一个全屏着色器（如旁观视角）开启时保持关闭。

`mode` 决定闪烁落在哪里。`screen` 在屏幕下部扭曲一条固定的带状区域，无论玩家看向哪里。`world` 则跟随地形：距离小于 `startDistance` 格的地形保持清晰，闪烁向渲染距离的远端逐渐增强，也就是雾气收拢的地方，天空永远不会闪烁，无论玩家是向下、平视还是向上看。

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `cloudLayers[].height` | 否 | 浮点数 | `cloudHeight` | 该层所在的位置 |
| `cloudLayers[].speed` | 否 | 浮点数 | `cloudSpeed` | 它飘动的速度。`0` 使其静止，负值使其反向 |
| `cloudLayers[].color` | 否 | 十六进制颜色 | `cloudColor` | 它的色调 |
| `heat.strength` | 否 | 浮点数，0 到 1 | `0.1` | 闪烁有多强 |
| `heat.minTemperature` | 否 | 浮点数 | `1.5` | 会出现闪烁的最低生物群系温度 |
| `heat.dayOnly` | 否 | 布尔值 | `true` | 开启时，闪烁随日光淡去，夜间消失 |
| `heat.mode` | 否 | 字符串 | `screen` | 闪烁落在哪里，`screen` 或 `world` |
| `heat.startDistance` | 否 | 浮点数 | `32` | 在 `world` 模式下，闪烁从多少格外开始 |

### `physics` 块

*维度*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `gravity` | 否 | 浮点数，大于 0 | `1.0` | 此处的下落加速度，为原版的倍数。`0.17` 近似月球 |
| `fallDamage` | 否 | 浮点数，大于 0 | `1.0` | 此处的摔落伤害，为倍数 |
| `arrowGravity` | 否 | 浮点数，大于 0 | 跟随 `gravity` | 箭在此处下坠的快慢，为倍数 |

这些与世界模板键 `worldGravity` 和 `worldFallDamage` 是相同的倍数，只是设置在维度上。世界模板中针对此维度的 `dimension=value` 一行仍然优先；不带维度的世界模板值只覆盖那些自己什么都没设置的维度。

### `time` 块

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `dayLength` | 否 | int，单位刻 | `24000` | 此处一个昼夜的时长。月相仍然每 24000 刻轮转一次 |

### `weather` 块

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `precipitation` | 否 | boolean | `true` | 关闭后，此处永远不会下雨、下雪或出现雷暴 |
| `lightning` | 否 | boolean | `true` | 关闭后，降雨和雷暴不会伴有闪电 |
| `snow` | 否 | boolean | `true` | 关闭后，雪不会积起来 |
| `freeze` | 否 | boolean | `true` | 关闭后，水永远不会结冰 |
| `cycle.rainTicks` | 否 | int 或 `[min, max]` | `[1000, 4600]` | 一场阵雨持续多久 |
| `cycle.clearTicks` | 否 | int 或 `[min, max]` | `[1000, 3000]` | 两场阵雨之间的晴朗间隔持续多久 |
| `cycle.maxStrength` | 否 | float，大于 0 且至多为 1 | `0.6` | 阵雨最大能达到的强度。每场阵雨的强度在此值的四分之一到全部之间浮动 |
| `cycle.thunderTicks` | 否 | int 或 `[min, max]` | 无 | 一场雷暴持续多久。不写则该循环永远不会出现雷暴 |
| `cycle.calmTicks` | 否 | int 或 `[min, max]` | `[12000, 180000]` | 两场雷暴之间的平静期持续多久 |
| `cycle.thunderStrength` | 否 | float，大于 0 且至多为 1 | `1` | 雷暴的天色能暗到什么程度。只有高于 `0.9` 时才会落雷 |
| `rain.particle` | 否 | 粒子 id | `minecraft:rain` | 雨滴落地处溅起的粒子 |
| `rain.sound` | 否 | 音效名称 | `minecraft:weather.rain` | 雨声 |
| `rain.volume` | 否 | float | `0.2` | 雨声的音量，雨从你头顶落下时减半 |
| `rain.interval` | 否 | int | `3` | 雨声播放得有多稀疏；越大越稀疏，`0` 表示每次机会都播放 |
| `rain.color` | 否 | 十六进制颜色 | `#FFFFFF` | 落下的雨的色调 |
| `rain.snowColor` | 否 | 十六进制颜色 | `#FFFFFF` | 落下的雪的色调 |
| `rain.angle` | 否 | float，0 到 180 | `0` | 偏离正下方的角度：`90` 为横向吹，`180` 为笔直向上飘。绘制时最多倾斜 75 度 |
| `rain.heading` | 否 | float，单位度 | `0` | 吹向哪个方向：`0` 为南，`90` 为西，`180` 为北，`270` 为东 |

其他维度共用主世界的降雨。`cycle` 会让此维度拥有自己的天气：阵雨按上述时间来去，无论主世界是什么天气。写了 `thunderTicks` 后，此处也会有雷暴，按它自己的时间出现；雷暴遇上阵雨时，会让阵雨达到最大强度、使天空变暗，并且在 `lightning` 开启时带来闪电。[世界模板](#世界模板)中的 `weatherCeiling` 仍然限制雨能到达的最大高度。

`rain` 块会改变此处雨雪的外观和声音，无论有没有 `cycle`；没有 `cycle` 时，雨雪的外观和声音与原版一致。

## 传送门与门

*世界*

`<namespace>/blocks/*.json`

传送门就是一个普通的方块定义，所以路径规则相同，每个变种都是一个传送门方块。

`portal` 方块带有一个 `portal` 部分：

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `dimension` | 是 | 维度 id | | 它把你送到哪里 |
| `returnDimension` | 否 | 维度 id | `minecraft:overworld` | 它把你送回哪里 |
| `gate` | 否 | 传送门名称 | 无 | 必须处于开启状态才能通过的传送门 |
| `cooldown` | 否 | int，单位刻 | `60` | 同一名玩家再次使用前的等待时间 |
| `platform` | 否 | boolean | `true` | 抵达时建造一个落脚平台 |
| `platformBlock` | 否 | 方块名称 | 传送门自身的框架 | 该平台由什么方块构成 |
| `sound` | 否 | 音效名称 | 无 | 通过时播放。参见[音效名称](#值列表) |
| `owned` | 否 | boolean | `true` | 只有建造者及其允许的人可以使用。有主的传送门同时免疫爆炸 |
| `walkIn` | 否 | boolean | `false` | 走进方块即可传送，就像下界传送门那样。关闭时则需用手使用 |

### 传送门框架

*传送门与门*

`<namespace>/portalframes/*.json`

文件路径就是框架的注册名，维度随后在 `frames` 中引用它。

框架只是玩家需要搭建之物的一幅图样，仅此而已：它说明哪些方块构成边框、洞口在哪里，而不说明传送门通向何处。这是有意为之，因为维度是认领框架而不是拥有框架，两个维度可以认领同一个框架。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 否 | string | 文件名 | 日志中使用的名称 |
| `axis` | 否 | `vertical`、`horizontal` 或 `both` | `vertical` | 是像下界传送门那样竖立、像末地传送门那样平躺，还是两者皆可 |
| `legend` | 是 | 由单个字符映射到方块的 object | 无 | 各行可以使用的方块。带状态的方块名称的读取方式与其他地方相同 |
| `rows` | 是 | string 列表 | 无 | 图样，最上面一行写在最前 |
| `maxWidth` | 否 | int | `21` | `*` 最多能拉伸到的洞口宽度 |
| `maxHeight` | 否 | int | `21` | `*` 最多能拉伸到的洞口高度 |

有三个字符不代表方块。`.` 是传送门所在的洞口，没有洞口的框架会被拒绝。空格表示框架不关心的格子，因此 L 形的边框可以通过把角落留空来绘制。`*` 表示重复：整行只有 `*` 的行会按玩家搭建的次数重复其上一行，行内的 `*` 则以同样的方式重复它前面的字符。它可以一次也不重复，所以把图样中每个 `*` 都划掉后读出的就是能点亮的最小形态，而下文的最大值则是最大形态。不含 `*` 的图样是精确的，玩家必须原样搭建，不能多也不能少。

竖立的框架可在任一水平轴上、以任一朝向被识别，因此建造者面朝哪个方向都无关紧要。平放的框架则会在四种旋转下都被识别。

**能有多大由资源包决定。** `maxWidth` 和 `maxHeight` 是 `*` 最多能拉伸到的洞口大小，下限以上任何更小的尺寸都会被接受，所以资源包可以决定它的传送门上限是原版的 21 还是 4。下限取决于玩家：竖立的框架要求洞口至少能达到宽 1、高 2，平放的至少 1 乘 1，永远达不到这一点的图样会在加载时被拒绝并在日志中留下一行，而不会变成一个谁也走不进去的框架。

**框架能拉伸得越多，查找它的代价就越高。** 行 `*` 和列 `*` 同时存在，意味着要尝试直到两个最大值的每一种组合，因此在两个方向上都能拉伸到 21 的框架就是 441 种图样。搜索会在耗时过长时放弃而不是卡死，并在日志中说明，这就是该调低最大值或去掉一处拉伸的信号。

**没有什么能阻止框架是用打火石点燃的黑曜石，但它具有优先权。** 框架会在物品执行自己的功能之前被查找，因此这样的框架会在本应出现下界传送门的地方开启资源包的维度。若要保留原版传送门，请换一种方块或另一种点火物。

### 用框架开启维度

*传送门与门*

`<namespace>/dimensions/*.json`

维度通过携带一个 `portal` 部分来经由框架开启。框架与点燃它的物品一起决定了是哪个维度，因此同一种框架形状可以根据点燃它所用的物品通向多个不同的地方。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `frames` | 是 | 框架名称列表 | 无 | 能开启此维度的框架 |
| `ignitedBy` | 否 | 物品名称 | `minecraft:flint_and_steel` | 玩家手持什么来点燃它 |
| `color` | 否 | 十六进制颜色 | 白色 | 传送门绘制所用的颜色 |
| `return` | 否 | `built`、`player` 或 `none` | `built` | 是否提供返回的路：自动建好、由玩家建造，或完全不提供 |
| `gate` | 否 | 传送门名称 | 无 | 必须处于开启状态才能通过的传送门 |
| `cooldown` | 否 | int，单位刻 | `60` | 同一名玩家再次通过前的等待时间 |
| `platform` | 否 | boolean | `true` | 抵达时建造一个落脚平台 |
| `platformBlock` | 否 | 方块名称 | 石头 | 该平台由什么方块构成 |
| `sound` | 否 | 音效名称 | 无 | 通过时播放。参见[音效名称](#值列表) |
| `owned` | 否 | boolean | `false` | 只有点燃者及其允许的人可以使用 |

站在洞口里的方块不由资源包编写。带有 `portal` 部分的维度会得到一个属于自己的方块，以游戏自带的传送门纹理按 `color` 绘制，走进去即可传送而无需用手使用，并且不可破坏。颜色会与纹理相乘，方式和 `tintindex` 一样，所以 `#C77DFF` 保留下界的紫色，而 `#4CFFB0` 会把它变成毒绿色。若想要完全不是原版纹理的传送门，请编写你自己的普通 `portal` 方块并配上自己的纹理，需要的话可以画成[像素图](#以像素图编写的纹理)，其中 `tint` 可以在两种颜色之间渐变。

`return` 决定另一侧会发生什么。`built` 会按玩家搭建的尺寸建起同样的框架并点燃它，这与原版的行为一致。`player` 不建造任何东西，但允许在那边点燃同样的框架，所以回家的路需要自己去找并搭建。`none` 则完全拒绝在该维度点燃这个框架，这趟旅程是单程的。

**一个框架，多个维度。** 框架与点燃它的物品这一对组合决定了维度，所以同一个 `standing_gate`，用打火石点燃和用资源包自己的点火物点燃，会开启两个不同的地方，各有自己的颜色。两个维度认领同一个框架*并且*同一个物品是资源包的错误：第二个会被拒绝并在日志中说明，而不是让其中一个悄悄胜出。

破坏框架的任何一个方块都会使传送门熄灭，与原版一样。

### 传送门

*传送门与门*

`<namespace>/gates/*.json`

文件路径就是传送门的注册名，传送门方块随后在 `gate` 中引用它。

下面一次列出了所有键。实际的文件只写需要的那些。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `dimension` | 是 | 维度 id | | 它守卫的维度 |
| `name` | 否 | string | 文件名 | 显示给玩家 |
| `scope` | 否 | `player`、`global` | `player` | 一次针对一名玩家，还是针对整个世界 |
| `open` | 否 | boolean | `false` | 是否一开始就处于开启状态 |
| `unlock` | 否 | object | | 什么能开启它。见下文 |
| `unlockedMessage` | 否 | string | `%dim% is now open` | 开启时显示 |
| `blockedMessage` | 否 | string | `You need %item% to enter %dim%` | 拒绝时显示 |
| `safeReturn` | 否 | boolean | `false` | 被拦回的玩家会被安置在他们试图离开的那个世界中的安全位置：若床或已充能的重生锚仍在，就放在其旁边，否则放在该世界的出生点 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该传送门 |
| `portalBlocks` | 否 | 方块名称列表 | 每个传送门 | 将该传送门限定于这些传送门方块，这样一个维度就可以同时拥有一扇受守卫的门和一扇敞开的门 |

`unlock` 接受 `hold`（必须手持的物品）、`consume` 及 `consumeCount`（`1`）、`craft`（必须已合成过的物品）、`advancement`，以及 `killed`（一个实体名称，谁击杀了一个，传送门就为谁开启，因此可以让某个 Boss 持有通往一个世界的钥匙），当一个不够时可配合 `killedCount`（`1`），按 scope 的设定逐玩家或针对整个世界统计。加上 `killedDrops`（一个物品名称）后，被计入的击杀会把该物品放到击杀者脚边而不是开启传送门，并让计数从头开始，因此钥匙可以再次获得，并交给从未战斗过的人；对同一物品使用 `hold` 或 `consume` 来设门槛，即可使它成为钥匙。`%item%`、`%mob%` 和 `%dim%` 会自动填入。怪物掉落的钥匙在这里不需要任何特殊处理：给怪物设置掉落物，再用 `hold` 或 `consume` 设门槛即可。

传送门也守卫游戏自带的维度：`dimension` 为 `minecraft:the_nether` 的传送门会挡在每一个下界传送门前面。

## 深层世界

*世界*

主世界可以比游戏默认的更高或更深，而地形下方开辟出的空间会由它自己的生成来填充。四个 `terrain` 键可以做到这一点，和其余的键一样写在世界模板的 `settings` 块中；资源包维度则在它自己的 `terrain` 中用 `minHeight` 和 `maxHeight` 来实现同样的效果。

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

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `worldMinHeight` | int，16 的倍数，最低到 -2032 | `-64` | 主世界最低的方块。游戏自带的底部是 -64；更低就会在原版地形之下形成一个深层世界，那里是实心的石头，直到世界生成层把它挖开，或者 `noiseCaves` 把游戏的洞穴延伸下去。只通过生成的预设应用，所以在资源包之前创建的世界会保持原有高度 |
| `worldMaxHeight` | int，16 的倍数，最高到 2032，且至多比底部高 4064 | `320` | 主世界顶部之上的那个方块。游戏自带的顶部是 320；更高会在原版地形之上留出开阔的天空 |
| `deepStone` | 方块名称 | 无 | 当底部低于 -64 时，原版地形之下的世界所用的方块，例如资源包自己的深板岩。它会在 -64 以下的八层中与深板岩渐变融合，就像深板岩与石头融合那样。留空则保持石头 |
| `noiseCaves` | `off`、`deep` 或 `world` | `off` | 当底部低于 -64 时，游戏的洞穴、隧道、面条洞穴和含水层在何处延续：`off` 让原版地形之下保持为实心深层石头，交给世界生成层去挖掘；`deep` 让它们一直延伸到底部，并把熔岩湖移到最底下的十层；`world` 在此版本上意思相同，因为原版地形本来就有它们 |

深层世界是资源包自己的世界生成条目、洞穴区域和硬度分组发挥作用的地方：条目上的 `minHeight` 和 `maxHeight` 可以一直延伸到底部。游戏自带的放置特征仍然局限于原版地形：高度从世界底部算起的特征，包括游戏的钻石和低处的红石，仍从 -64 算起，而会落到 -64 以下的放置会被省略，就像底部为 -64 的世界里那样。1.12.2 的天空键、`deepRavines`、`oreVeins`、`terrainOffset` 以及 rubic 世界本身在这里都没有对应项，因为此引擎自带的生成本来就能从底部延伸到顶部。

## 洞穴区域

*世界*

`<namespace>/caveregions/*.json`

文件路径就是区域的名称，世界生成条目随后在 `caveRegions` 中引用它。其中的裸名称取该条目自己的命名空间。

在地下绘制具名区域，是游戏洞穴生物群系在资源包中的对应物。地下被划分为圆角的单元格，宽 `caveRegionCells` 个方块，高 `caveRegionCellsY` 个方块，二者都是 `terrain` 键，每个单元格按权重抽取一个区域，也可能一个都不抽中。区域所做的一切都由种子确定性地得出，所以区块之间互相一致，而无需跨边界写入。

### 区域文件

*洞穴区域*

下面一次列出了所有键。实际的文件只写需要的那些。

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

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `weight` | int | `1` | 此区域赢得的单元格份额。`0` 将其关闭 |
| `minHeight` | int | 世界底部 | 区域存在的高度带的底部 |
| `maxHeight` | int | `48` | 该高度带的顶部。中心位于高度带之外的单元格永远不会选中此区域 |
| `waterLevel` | int | 无 | 把区域内的地下水位固定在这个高度，取代那里的含水层。它保持在海平面以下，并且至少比深层熔岩高两个方块 |
| `dimensions` | 维度 id 列表 | 全部 | 区域出现在哪些维度中，包括资源包自己的维度。带 `biome` 的区域只会在生物群系按气候放置的地方显示它：主世界、下界，以及继承主世界生物群系的资源包维度 |
| `floorCover` | 方块 | 无 | 替换区域内洞穴地面的顶层方块 |
| `floorChance` | 0.0 到 1.0 | `1.0` | 地面有多少会被覆盖 |
| `ceilingCover` | 方块 | 无 | 替换区域内洞穴顶部的方块 |
| `ceilingChance` | 0.0 到 1.0 | `1.0` | 顶部有多少会被覆盖 |
| `coverReplace` | 方块列表 | 游戏的基础石头、矿石、圆石、砂岩、陶瓦、末地石和黑曜石方块 | 覆盖层可以替换什么。列出的方块只匹配其自身：`minecraft:stone` 不会同时覆盖安山岩、深板岩或凝灰岩，所以要把地面可能出现的每一种石头都列出来 |
| `spawns` | 列表 | 无 | 在区域内生成的生物，与生物群系的 `spawns` 接受的条目相同：`entity`、`type`（monster、creature、ambient 或 water）、`weight`（`8`），以及表示群体规模的 `min`（`1`）和 `max`（`4`）。能看到天空的位置交给生物群系处理，覆盖层也是如此 |
| `keepDefaultSpawns` | boolean | `false` | 在区域自己的列表之外保留生物群系原有的生成列表。关闭时，区域的列表在区域内完全取代它 |
| `structures` | 列表 | 无 | 每个区域单元格放置一次的结构，位于单元格的中心，并贴合到洞穴地面，就像游戏为洞穴生物群系提供地标那样。条目是 `namespace:name` 模板，或者用 `{ "structure": "...", "weight": 3 }` 在多个之间选择 |
| `structureChance` | 0.0 到 1.0 | `1.0` | 区域的每个单元格实际得到其结构的几率 |
| `structureLoot` | `namespace:path` | 无 | 已放置结构内的每个箱子首次被打开时所用的战利品表 |
| `biome` | 生物群系名称 | 无 | 区域在其体积内上报的生物群系，以 3D 生物群系的形式写入。让区域拥有自己的植被、草地和水的颜色、音乐与环境音，并让原版的生成权重读取它。上方的地表不受影响，因为只写入区域所占据的单元格。省略时，区域保留周围原有的生物群系，仍会铺设它的覆盖层、结构和生成 |
| `requires` | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该区域 |
| `ambientSound` | 音效名称 | 无 | 不时向站在区域内的玩家播放的音效，就像游戏的生物群系添加自己的洞穴音效那样。由服务器仅发送给该玩家 |
| `soundChance` | 0.0 到 1.0 | `0.0111` | `ambientSound` 每刻播放的几率 |
| `particle` | 粒子名称 | 无 | 在区域内玩家周围显示的粒子，是游戏中自身不带设置的粒子之一，例如 `minecraft:dripping_water`、`minecraft:happy_villager` 或 `minecraft:underwater`。`dripWater` 这样的 1.12.2 名称会随其资源包一并转换。只有区域内的空气方块才会显示它 |
| `particleChance` | 0.0 到 1.0 | `0.00625` | 生物群系自身的粒子密度：每刻会尝试玩家 16 个方块范围内约 667 个位置，每个位置以此几率显示粒子 |

### 单元格

*洞穴区域*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `caveRegionCells` | int，方块 | `128` | 一个区域单元格有多宽 |
| `caveRegionCellsY` | int，方块 | `64` | 一个区域单元格有多高 |
| `caveRegionPlainWeight` | int | `4` | 每个单元格抽取时，普通、无区域的地下所占的权重。越高，地下没有任何区域的部分越多：只有一个权重为 1 的区域时，约五分之一的单元格会得到它 |

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

地下有多少保持普通，由 `caveRegionPlainWeight` 这个 `terrain` 键决定，默认 `4`：只有一个权重为 1 的区域时，约五分之一的单元格会得到该区域。覆盖层作用于顶盖之下，所以延伸到地面之上的区域永远不会在地表显示。覆盖层在每个洞穴中都有效，无论是哪种生成器挖出的。

### 区域内的特征

*洞穴区域*

特征通过普通[世界生成条目](#世界生成条目)上的两个键与区域联系起来。`caveRegions` 列出某个条目可以在哪些区域中生成，在放置位置处检查，所以蘑菇、晶体或其他任何东西都只会出现在它们的区域内。`snap` 会先把每次尝试竖直移动到最近的洞穴表面：`floor` 用于立着的东西，`ceiling` 用于悬挂的东西。类似滴水石的区域不需要任何新形状：

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

`minecraft:air` 的 `replace` 很重要：已放置形状所覆盖的内容会按 `replace` 检查，其默认值是石头，所以任何建在开阔洞穴空间中的东西都需要列出空气。同一个条目改为 `"snap": "floor"` 且不带 `hanging`，就会长出与之对应的石笋。区域过滤对每一种放置形状都有效；`belt` 和 `field` 按各自的规则放置，会忽略它。

---

# 生成世界

## 世界生成条目

*生成世界*

`<namespace>/worldgen/*.json`

文件路径决定条目的名称，而 `belt`、`field` 和 `vein` 形状会以它为种子生成噪声，所以重命名文件会改变它生成的内容。

描述会生成的东西。每个条目都是由某种**扩散**放置的某种**形状**，并受允许位置的过滤。

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

只有 `block` 是必需的；其余的都可以省略，并采用默认值。当一个方块不够用时，`blocks` 取代 `block`，下文有它自己的示例。

### 它放置什么

*世界生成条目*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `block` | 是 | 方块名称 | | 放置的内容 |
| `blocks` | 否 | object 列表 | 无 | 加权列表，代替单个方块使用。见下文 |
| `size` | 否 | int 或范围 | `8` | 一次尝试放置多少个方块，或带半径的形状有多大 |
| `attempts` | 否 | int 或范围 | `8` | 每个区块尝试多少次 |
| `replace` | 否 | 方块名称或 object 的列表 | `["minecraft:stone"]` | 它可以替换什么。见下文 |
| `adjacent` | 否 | 方块名称或 object 的列表 | 无 | 只有当这些之一位于紧邻该位置的 26 个方块之中时才放置。形式与 `replace` 相同 |
| `sparse` | 否 | boolean | `false` | 把方块分散开，而不是紧密堆在一起 |
| `shape` | 否 | object | `{ "type": "cluster" }` | 它呈现的形态。参见[形状](#形状) |
| `spread` | 否 | object | `{ "type": "even" }` | 它被放在哪里。参见[扩散](#扩散) |

### 可生成的位置

*世界生成条目*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `minHeight` | 否 | int | `0` | 它会放置的最低 y 坐标 |
| `maxHeight` | 否 | int | `64` | 它会放置的最高 y 坐标 |
| `dimensions` | 否 | 维度 id 列表 | 每个维度 | 它在哪些维度中运行 |
| `dimensionsAreBlacklist` | 否 | boolean | `false` | 把该列表变成需要避开的维度 |
| `biomes` | 否 | 生物群系名称列表 | 每个生物群系 | 它在哪些生物群系中运行 |
| `biomeTypes` | 否 | 生物群系类型列表 | 无 | 按类型词指定生物群系，例如 `forest` 或 `nether` |
| `biomesAreBlacklist` | 否 | boolean | `false` | 把这些列表变成需要避开的 |
| `minTemperature` | 否 | float | `-100.0` | 它会生成的最冷生物群系 |
| `maxTemperature` | 否 | float | `100.0` | 它会生成的最热生物群系 |
| `minRainfall` | 否 | float | `-100.0` | 它会生成的最干燥生物群系 |
| `maxRainfall` | 否 | float | `100.0` | 它会生成的最湿润生物群系 |
| `minDistanceFromSpawn` | 否 | int，方块 | `0` | 距世界出生点多远才开始生成 |
| `caveRegions` | 否 | 区域名称列表 | 无 | 只在这些[洞穴区域](#洞穴区域)内生成 |
| `snap` | 否 | `floor` 或 `ceiling` | 无 | 先把每次尝试竖直移动到最近的洞穴地面或顶部 |
| `snapDepth` | 否 | int | `0` | `snap` 随后越过表面多远，从地面向下、从顶部向上。`0` 停留在紧贴表面的开阔空间中，`1` 是表面方块本身，`2` 是它后面的那个。它可以覆盖什么仍由 `replace` 决定，所以资源包可以借此把一种方块分层放在地面之下而不是地面之上 |

### 地表标志与跟随者

*世界生成条目*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `indicators` | 否 | `block=weight` 列表 | 无 | 在已生成的矿脉上方的地表散落的方块，让玩家能知道地下有什么；按矿脉的内容来挑选。`empty=weight` 让一个位置保持空白。没有权重或权重小于 1 的条目会被记入日志并忽略，并且不会在村庄或城市的街道和建筑上留下任何指示物 |
| `indicatorCount` | 否 | int 或范围 | `1` | 每条生成的矿脉得到多少个地表位置 |
| `indicatorSpread` | 否 | int，方块 | `0` | 指示物可以落在超出矿脉占地范围多远的地方 |
| `then` | 否 | `name=weight` 或 object 的列表 | 无 | 在本条目生成之后立刻从它长出来、并依附于它的世界生成条目：跟随者的原点设在这条矿脉边缘的外侧，方向由 `thenSpread` 和 `thenDepth` 给出，使两者相接。条目可以是 `name=weight`，也可以是带有 `name`、`weight` 以及自己的 `spread` 和 `depth`（int 或范围）的 object，这些仅对该跟随者覆盖矿脉的设置，因此同一个列表可以让钻石尖端朝下、让分支朝侧面。裸名称在本资源包的命名空间中读取，`empty=weight` 不排入任何内容。跟随者保留自己的形状、方块、大小和 `replace`，但跳过自己的尝试次数、几率、高度带和生物群系限制，并且自己也可以带有 `then`，资源包想嵌套多深都可以；已经在同一条链中生成过的条目会使该链停止 |
| `thenCount` | 否 | int 或范围 | `1` | 每条生成的矿脉从该列表中选出多少个不同的跟随者，每个条目至多一次，所以数量等于列表长度时，每一个都会长出来 |
| `thenSpread` | 否 | int，方块 | 形状的半径 | 跟随者生长方向可以向侧面偏多远，在负的该值到正的该值之间随机取值 |
| `thenDepth` | 否 | int 或范围 | `0` | 方向向下（负）或向上偏多远。`0` 且没有侧向偏移时，跟随者笔直向下悬挂 |
| `prospectAs` | 否 | string | 文件名 | 勘探物品在其读数中如何称呼此条目，例如 `Hematite` |

### 回溯生成与要求

*世界生成条目*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `retrogen` | 否 | boolean | `false` | 同时生成到已经存在的区块中 |
| `retrogenKey` | 否 | string | 配置中的键 | 仅为此条目覆盖回溯生成键 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该条目 |

### 加权方块

*世界生成条目*

当一个条目不够用时，`blocks` 取代 `block`。权重是相对的，所以 80 和 20 就是四比一。

```json
{
  "blocks": [
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "minecraft:oak_log", "weight": 20, "properties": { "axis": "x" } }
  ]
}
```

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `block` | 是 | 方块名称 | | 放置的内容 |
| `weight` | 否 | int | `1` | 这一个相对于其他方块被选中的频率 |
| `properties` | 否 | 由属性映射到值的 object | 无 | 按名称指定的方块状态属性，用于方块默认状态之外的状态 |

即使使用了 `blocks`，文件顶层仍然需要 `block`；把第一个条目放在那里就很合适。

### 替换目标

*世界生成条目*

`replace` 是一个列表，每个条目采用两种形式之一。

```json
{
  "replace": [
    "minecraft:stone",
    { "block": "minecraft:oak_log", "properties": { "axis": "y" } }
  ]
}
```

| 形式 | 示例 | 它匹配什么 |
| --- | --- | --- |
| 名称 | `"minecraft:stone"` | 该方块的每一种状态 |
| Object | `{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }` | 仅该状态 |

末尾带元数据的 1.12.2 名称 `minecraft:stone:3` 会匹配该方块的每一种状态，并在日志中说明，因为带元数据的那些方块现在已是各自独立的方块：请写 `minecraft:diorite`。使用 `"minecraft:air"` 可在开阔空间中生成。

### 相邻方块

*世界生成条目*

`adjacent` 接受与 `replace` 相同的形式，并在其基础上增加第二个条件：只有当紧邻该位置的 26 个方块（面、棱和角）中至少有一个匹配该列表时，才会使用这个位置。省略时不做任何检查。

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

这样只会在砂岩已经向洞穴或地表敞开的地方放置硫，而不会动被埋住的砂岩。尚不存在的区块中的相邻方块会被视为不匹配，而不是被读取，因此这个检查绝不会导致区块被生成。

每一种形状都遵守它，因为它是判断能否取用单个方块的一部分。`geode` 分别指定其外壳和填充物，这两者的放置不做该检查。

只指明了未注册方块的条目会被跳过并报错，而不是在各处生成。

### 跟随者条目

*世界生成条目*

世界生成条目的 `then` 列表中的一个条目，是带权重的名称，或者当该跟随者需要自己的方向时使用 object。

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 是 | 条目名称 | | 从本条目长出来的世界生成条目。裸名称在本资源包的命名空间中读取 |
| `weight` | 否 | int | `1` | 该跟随者相对于列表中其他跟随者被选中的频率 |
| `spread` | 否 | int，方块 | 条目的 `thenSpread` | 该跟随者的方向可以向侧面偏多远，仅对此条目生效 |
| `depth` | 否 | int 或范围 | 条目的 `thenDepth` | 该跟随者的方向向下（负）或向上偏多远，仅对此条目生效 |

`name=weight` 是只含这两项的 object 的简写，而 `empty=weight` 不排入任何内容。由于 `spread` 和 `depth` 是逐条目设置的，同一个列表可以让同一条矿脉上的钻石尖端笔直向下，同时让一个分支向侧面延伸。

## 形状

*生成世界*

带有 `type` 的 `shape` 块。某个类型未列出的键会被它忽略。

下面一次列出了所有键。实际的文件只写需要的那些。标明属于某一类型的键只会被该类型读取。

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

| 类型 | 它生成什么 |
| --- | --- |
| `cluster` | 默认的团块，即矿脉。使用 `size` |
| `largevein` | 带分支的长条蜿蜒矿脉。使用 `size` |
| `plate` | 一个扁平的圆盘 |
| `geode` | 带外壳的中空腔体 |
| `decoration` | 地表散布物，例如花或蘑菇。使用 `size` |
| `tree` | 一整棵树 |
| `vines` | 在已有方块上生长的藤蔓。使用 `size` |
| `basin` | 向中间逐渐加深的碗形 |
| `spire` | 逐渐变细的柱体 |
| `nodule` | 粗糙的球体 |
| `vent` | 遇到东西就停下的细长柱体 |
| `imprint` | 你的某个 `.nbt` 模板。能放进一个区块内的模板会被微调，使它无论朝向哪边都完整地落在正在构建的区块中，而不会伸进尚未生成的相邻区块；比区块大的模板只会放置在其周围地面已经存在的地方 |
| `belt` | 跨越多个区块的团块，用于石头区域 |
| `field` | 一次性为每个方块算出的矿脉，与硬度分组共用其形状 |
| `vein` | 以带种子的噪声场围绕原点算出的矿床，做法与 Immersive Geology 相同：每个区块为每条其 24 个方块的影响范围触及它的矿脉写出各自的那一片，因此不会产生连锁反应，并且 `/rdplserver vein` 可以在陆地生成之前就告诉你矿脉将位于何处。使用 `size`、`attempts`、`rarity` 和高度带；`pattern` 决定外观 |
| `spring` | 从洞穴壁渗出的流体：放置在上方、下方和三侧都是岩石而一侧敞开的位置，并设为流动状态 |

### 大小与形态

*形状*

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `type` | 全部 | 上述形状之一 | `cluster` | 哪种形状 |
| `radius` | plate、geode、basin、spire、nodule、vent | int 或范围 | `6` | 它有多宽 |
| `height` | plate、geode、basin、spire、vent、tree | int 或范围 | `1`，geode 为 `8`，tree 为 `5` | 它有多高或多厚 |
| `width` | geode | int 或范围 | `12` | 腔体的整体跨度 |
| `plane` | plate、basin、spire、vent | `circle`、`square` | `circle` | 它的占地形状 |
| `slim` | plate、largevein、nodule | boolean | `false` | plate：薄一层。largevein：单方块分支。nodule：空心外壳 |
| `hanging` | spire、vent | boolean | `false` | 从顶部向下生长，而不是从地面向上生长 |
| `taper` | spire | `straight`、`bell`、`needle` | `straight` | 宽度向尖端如何收窄。`straight` 均匀收窄，`bell` 下方保持宽度然后骤降，`needle` 立刻变细成一根长长的尖 |
| `outline` | geode | 方块名称 | 无 | 外壳方块 |
| `fill` | geode | 方块名称 | 无 | 中间填充什么。省略时，中间是空心的 |
| `middle` | geode | 方块名称 | 无 | 位于主体与 `outline` 之间的一层壳，相当于游戏紫水晶晶洞的方解石 |
| `budding` | geode | 方块名称 | 无 | 替换面向中空部分的主体方块，就像紫水晶母岩那样。需要 `fill` |
| `buddingChance` | geode | 0.0 到 1.0 | `0.083` | 这些主体方块中有多少会成为母岩 |
| `crystal` | geode | 方块名称 | 无 | 长在 `budding` 方块旁边的空腔中，就像紫水晶簇那样 |
| `crystalChance` | geode | 0.0 到 1.0 | `0.35` | 这些位置中有多少会长出晶体 |
| `crack` | geode | 0.0 到 1.0 | `0` | 晶洞被裂开的几率：从中间向外穿过每一层、位于一侧的一条管道，用 `fill` 填充。游戏的紫水晶晶洞使用 `0.95` |

### 放置

*形状*

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `surface` | decoration、tree | 方块名称列表 | 无 | 它会放在什么之上 |
| `seeSky` | decoration | boolean | `true` | 只在能看到天空的地方放置 |
| `checkStay` | decoration | boolean | `true` | 只在该方块能存活的地方放置 |
| `stackHeight` | decoration | int 或范围 | `1` | 向上叠放多少个 |
| `scatterX` | decoration、tree | int | `8` | 它向侧面游移多远 |
| `scatterY` | decoration、tree | int | `4` | 它在竖直方向游移多远 |
| `scatterZ` | decoration、tree | int | `8` | 它向侧面游移多远 |
| `rarity` | 任意 | int | 无（belt 为 `400`） | 每隔这么多个区块放置一次。对 belt 而言，它拉开各个带之间的间距；对其他形状而言，它为整个条目设门槛，只有这么多个区块中的一个才会执行它的 `attempts`。`field` 忽略它 |
| `rarityIsPerChunk` | 任意 | boolean | `false` | 把 `rarity` 变成每个区块得到多少次放置 |

### 树木

*形状*

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `log` | tree | 方块名称 | 无 | 树干方块 |
| `leaves` | tree | 方块名称 | 无 | 树叶方块 |
| `vines` | tree | boolean | `false` | 从树叶上垂下藤蔓 |

没有 `log` 或 `leaves` 的 `tree` 什么也不会生成，并会在日志中说明。指明一个 `structure`，或在 `structures` 下指明多个，就会在每个位置种下该模板而不是长出一棵树，此时不再需要 `log` 或 `leaves`；使用模板的树读取 `turns`、`mirrors`、`integrity`、`lootTable` 和 `locateAs` 的方式与 `imprint` 完全相同。

### 放置模板

*形状*

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `structure` | imprint、tree | `namespace:name` | 无 | 要放置的模板 |
| `integrity` | imprint、tree | 1 到 100 | `100` | 模板方块中实际出现的百分比 |
| `lootTable` | imprint、tree | `namespace:path` | 无 | 已放置模板内的每个箱子，以及任何其他接受战利品表的容器（包括潜影盒或模组的板条箱），首次被打开时所用的战利品表。涵盖 `structure` 和 `structures` 的每个条目；每个箱子各自掷出自己的种子 |
| `structures` | imprint、tree | 列表 | 无 | 在多个模板之间选择，每次放置一个。每个条目是 `{ "structure": "namespace:name", "weight": 3 }`，或者用裸名称表示机会均等。会覆盖 `structure` |
| `turns` | imprint、tree | 列表 | 任意 | 它可以朝哪个方向放置：`none`、`quarter`、`half`、`threequarter`。条目可以带 `weight`。省略时，四种方向的可能性相同 |
| `mirrors` | imprint、tree | 列表 | 无 | 同时翻转：`none`、`leftright`、`frontback`，可带 `weight`。要为条目指定自己的权重，写作 `{ "mirror": "leftright", "weight": 2 }`，`turns` 的条目同理，只是用 `turn` |
| `at` | imprint | 两个 int，x 和 z | 无 | 在该区块生成时，在地表这些方块坐标处精确放置一次，而不是按几率放置。参见[位于精确位置的结构](#位于精确位置的结构) |
| `locateAs` | imprint、tree | string | 无 | 把此条目放置的每个结构以该名称登记，使 `/rdplserver locate <name>` 能找到最近的一个。参见[查找已放置的结构](#查找已放置的结构) |

对于没有内置类型能涵盖的形状，用 `imprint`：把它建成 `.nbt` 模板再放置它，用 `structures` 来变化，用 `turns` 和 `mirrors` 来转动，用 `integrity` 把它消解成比你绘制的文件更粗糙的样子。

### 位于精确位置的结构

*形状*

原版结构通过 `terrain` 设置中的 `structureAt` 固定到精确位置，写成 `structure=x,z` 条目，每行一个：`"structureAt": ["villages=1000,-500"]`。**其中的 x 和 z 是方块坐标，不是区块坐标**，结构会在包含该方块的区块中生成。在 `terrainAdaptation` 把村庄铺成城市街道的情况下，被固定的村庄的水井就位于该方块本身；当该方块离街区边缘只有几个方块远时，则位于街区允许的最近处；其他结构，以及没有启用它而铺设的村庄，则从游戏在该区块中本会让它们开始的地方开始。每个想要的实例写一个条目。它的间距、间隔、最小出生点距离和平地检查全部不再起作用，所以位置由资源包自己负责，两个相距不到一个区块的固定位置会在同一个区块中放入两个结构。结构一旦建立，就按通常的规则贴合其区块处的地面。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `structureAt` | `structure=x,z` 列表 | 无 | 把原版结构固定到精确位置，每个想要的实例一个条目。其中的 x 和 z 是方块坐标，结构会在包含该方块的区块中生成；它的间距、间隔、最小出生点距离和平地检查全部不再起作用 |

`imprint` 条目以同样的方式固定，在其形状中写 `"at": [x, z]`，在该区块生成时于地表这些坐标处精确放置一次，而不是按几率放置。它可以与 `locateAs` 组合，所以被固定的结构也能被找到。

### 查找已放置的结构

*形状*

带有 `"locateAs": "Crypt"` 的 `imprint` 条目会把它放置的每个结构以该名称登记，之后 `/rdplserver locate Crypt` 会指向最近的一个，并且该名称会出现在 Tab 补全中；`/rdplserver goto Crypt` 会把你带到那里。只有已经生成的结构才能被找到，因为资源包结构是在区块生成时按几率放置的，而不是按游戏可以预测的网格。这些名称保存在世界存档中，所以重启后仍然有效，在服务器上也能用。以这种方式登记的名称还可以用 `gotoPlaceLevels` 赋予它自己的权限，所以资源包可以把谁能被传送到它自己的结构，与原版结构分开来决定。

### 矿场与矿脉键

*形状*

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `field` | field | object | `{ "type": "speckle" }` | 矿场如何算出。键与硬度分组的 `field` 相同，在[矿场](#矿场)中说明：带 `chances` 和 `spread` 的 `speckle`，或带 `cell`、`seeds`、`reach`、`arms` 和 `armReach` 的 `seeded` |
| `threshold` | field、vein | 0.0 到 1.0 | `0.5`（vein 为 `0.4`） | 某个方块处的场强必须达到多少才放置。越低，填充越多 |
| `fade` | field | int | `0` | 让高度带的顶部呈斑点状消散，而不是平齐地结束：在高度范围的顶部这么多个方块内，每个方块被放置的几率逐级降低，与引擎在 `deepStone` 与上方世界相接处所呈现的效果相同 |
| `pattern` | vein | `default`、`banded` 或 `tube` | `default` | 矿床的外观：扭曲的团块、每隔几个方块叠起的层，或在岩石中蜿蜒的空心管道 |
| `density` | vein | 0.0 到 1.0 | `1.0` | 符合条件的方块中实际被放置的比例，逐方块掷硬币 |
| `rich` | vein | 方块名称 | 无 | 从 `richAt` 起，在场强高于 `threshold` 的范围内放置，即矿床的核心，取代条目的方块 |
| `poor` | vein | 方块名称 | 无 | 放置在该范围底部的五分之二，即边缘，取代条目的方块；中间部分则是条目自己的方块。省略任一层级，则该处放置条目的方块 |
| `richAt` | vein | 0.0 到 1.0 | `0.88` | 富集层在该范围中从哪里开始：`0.88` 把富集方块限制在矿床最强的八分之一，数值更低会使富集核心更粗，`1.0` 则完全没有富集方块 |
| `poorAt` | vein | 0.0 到 1.0 | `0.4` | 条目自己的方块从哪里开始：低于此处放置 `poor` 方块，所以 `0.4` 给出底部五分之二的边缘，`0.0` 则没有贫瘠边缘。会被限制在 `richAt` 以内 |

`field` 矿脉是唯一一种你描述而不是挑选的形状。它运行硬度分组所用的同一套格点，所以带有若干条臂的 `seeded` 会给出向邻近节点伸出卷须的结节，这是矿脉而不是团块，而 `threshold` 决定其中有多少足够坚实可以放置：

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

这些键写在它自己的 `field` object 中，而不是与 `type` 并列，因为形状上的 `type` 已经写了 `field`。

### 带状区域

*形状*

`belt` 是一个比单个区块大得多的球体，用于石头区域而不是矿脉。它的 `radius` 是球体的大小，每个区块都根据世界种子和条目自己的名称，各自算出附近的球体从哪里开始，所以无论区块以何种方式生成，带状区域都会完整出现，并且绝不会向相邻区块写入任何内容。

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

带状区域会忽略 `attempts` 和 `spread`，因为它是按区块而不是按尝试放置的。`minHeight` 和 `maxHeight` 是球心所在的高度带，球体在该高度带之外再延伸 `radius`。`replace` 决定它吞掉什么，`biomes` 以及温度和降雨限制在球心处检查，所以带状区域要么完整出现，要么完全不出现，而不会在生物群系边缘被截断。

开销随 `radius` 的立方增长，而较低的 `rarity` 会使它成倍增加，所以请从默认值开始，缓慢地提高半径。

### 矿场

*形状*

`field`（矿场）不在某一点放置任何东西，而是一次性覆盖全部范围。它不会先选定一个位置再围绕它构建形状，而是在 `minHeight` 与 `maxHeight` 之间，对区块中的每个方块提出一个问题，并在答案不低于 `threshold` 的位置放置方块。这个问题与硬度分组所问的完全相同，因此两者描述的是同一批矿脉，资源包可以让分组与条目彼此吻合。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `threshold` | 否 | 0.0 到 1.0 | `0.5` | 场的强度达到多少才会放置方块 |
| `field` | 是 | 对象 | 无 | 与硬度分组所接受的对象相同，同样支持 `speckle` 和 `seeded` 类型 |

`threshold` 较低时会取走大部分场，形成宽阔的矿层；较高时只取每团的中心部分，形成零星分散的小矿袋。使用 `speckle` 会得到许多细小的斑点，使用 `seeded` 会得到更圆的矿块，若再加上分支，则会得到带有卷须、彼此相连的结点。

与带状区域一样，矿场会忽略 `attempts` 和 `spread`，因为它是按区块而不是按尝试次数来求值的，并且绝不会写入相邻区块。它由世界种子和条目自身的名称推算而来，因此同一种子总是得到相同的矿脉，而名称不同的两个条目绝不会重合。`replace`、`adjacent`、`biomes` 以及气候限制均照常生效。

## 扩散

*生成世界*

一个带有 `type` 的 `spread` 块。

此处一次性列出所有键。实际文件只写需要的键。标注为某一类型专用的键只会被该类型读取。

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

| 类型 | 放置位置 |
| --- | --- |
| `even` | 在各高度之间均匀分布，任意位置皆可。默认值 |
| `centered` | 偏向某一高度，距离越远越稀疏 |
| `sprawl` | 跨越一段高度范围的分形矿脉 |
| `terrain` | 沿地表分布 |
| `cavern` | 位于洞穴地面或洞顶 |
| `submerged` | 位于水下或其他流体之下 |

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `type` | 全部 | 上述扩散类型之一 | `even` | 使用哪种扩散 |
| `center` | centered | 整数 | 高度范围的中点 | 聚集所围绕的高度 |
| `range` | centered | 整数 | 高度范围的一半 | 距该高度可延伸多远 |
| `smoothness` | centered | 1 到 8 | `2` | 对多少次随机取平均。越高，分布带越集中 |
| `veinHeight` | sprawl | 整数 | 高度范围 | 单条矿脉有多高 |
| `veinDiameter` | sprawl | 整数 | `12` | 单条矿脉有多宽 |
| `verticalDensity` | sprawl | 1 到 100 | `16` | 垂直方向上有多密实 |
| `horizontalDensity` | sprawl | 1 到 100 | `32` | 水平方向上有多密实 |
| `offsetMin` | terrain | 整数 | `0` | 距地表的最低偏移 |
| `offsetMax` | terrain | 整数 | `offsetMin` | 距地表的最高偏移 |
| `ceiling` | cavern | 布尔值 | `false` | 附着在洞顶而不是地面 |

## 结构地图

*生成世界*

结构地图把多个模板在网格上组合成一座有名称的完整建筑，远远超出单个 `.nbt` 文件 48 格方块的上限。每一层都以若干行单字符绘制，一个字符对应一个单元格，并且每一层叠放在前一层之上，高度为一个单元格。最多 8 层，每层 8 乘 8 个单元格；在默认单元格大小 32 下，每边为 256 格方块。

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `cell` | 数字 | `32` | 网格间距，以方块计，最大 48。小于单元格的模板放在单元格的角落，因此满尺寸的构件可以无缝拼接 |
| `ground` | 数字 | `0` | 哪一层以地表为底。位于其前的层向下挖掘，建筑的地下室就是这样来的 |
| `at` | 两个数字 | 无 | 将一份副本固定在精确的方块坐标上，方式与 `structureAt` 固定村庄相同 |
| `spacing` | 数字 | `0` | 在相隔这么多区块的网格上散布副本，并依据世界种子加以抖动。`0` 表示不散布，因此只有 `at` 的地图恰好只会建造一次 |
| `chance` | 数字 | `100` | 网格点中会建造副本的百分比 |
| `dimensions` | 维度 ID 列表 | 全部 | 地图可在哪些维度建造，包括资源包自己的维度 |
| `layers` | 列表 | 无 | 各层，自下而上，每层由一个 `palette` 和一个 `map` 组成 |

调色板按注册表键引用资源包 `<namespace>/structures/` 中的模板。

| 值 | 作用 |
| --- | --- |
| `"a": "mypack:keep"` | 该层的每个 `a` 单元格都放置这个模板 |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | 每个 `a` 单元格依据世界种子和该单元格的位置，按权重从列表中抽取，因此同一建筑的两份副本各不相同，但同一个世界总是建造出相同的那一座 |
| `.` | 空单元格，不放置任何东西 |

每份副本会依据世界种子从四个朝向中抽取一个，整座建筑连同其中的模板一起转动，因此跨单元格相接的墙体依然相接；地图只会旋转，绝不会镜像。地面层以建筑中央下方采样到的地表为底，整张地图共用这一个高度。对游戏而言，散布的地图本身就是一个结构，通过为你写好的结构集放置，因此每个区块只建造自己所占的那一片网格，跨越许多区块的建筑无需级联生成即可出现，与区块的加载顺序无关。类型为 `template` 的[村庄地块](#村庄地块)也可以把地图指定为它的 `structure`，这样复合结构就成为一座城市建筑。

## 村庄地块

*生成世界*

`<namespace>/villages/*.json`

文件的路径就是地块的名称，`villagePieces` 可以通过它来保留或舍弃该地块。

此处的文件会添加一种资源包的城市和村庄可以建造的构件。通过 `type` 选择，共有两种。

此处一次性列出所有键。实际文件只写需要的键。标注为某一类型专用的键只会被该类型读取。

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

### 每个地块

*村庄地块*

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `type` | 全部 | `farm` 或 `template` | `farm` | 地块的种类 |
| `weight` | 全部 | 整数 | `3` | 与资源包中其他地块相比，这个地块被选中的频率 |
| `leastCount` | 全部 | 整数 | `1` | 每个街区的最少数量：街区沿街道安置地块，数量上限在其可建造地块中最低的 `leastCount` 与最高的 `mostCount` 之间随机抽取，二者在城市生长期间均加上 16，或加上 `villagePlotsLeast` 的三十二分之一（若该值更大）。位于其他地块后方的地块不计入 |
| `mostCount` | 全部 | 整数 | `4` | 该随机范围的上限 |
| `width` | 全部 | 整数 | `7` | 沿街道方向的宽度 |
| `height` | 全部 | 整数 | `4` | 在地面以上清出的高度 |
| `depth` | 全部 | 整数 | `9` | 远离街道方向的进深 |
| `apron` | 全部 | 整数 | `2` | 地块下方的地面与街道标高相差多少仍可接受，超出则被拒绝或沿街道滑动：即其下的填土或其上隆起处的削平的方块数，且最高角与最低角之间的差也不超过此值。丘陵中的宽地块需要更大的值。设得过高，地块会径直在斜坡上形成梯田，在不合适的地方会把一座山削平 |
| `ground` | 全部 | 方块名称 | `minecraft:dirt` | 在斜坡上垫在下方的材料 |
| `requires` | 全部 | 模组 ID 或资源包命名空间的列表 | 无 | 除非全部存在，否则该地块会被排除 |

地块是资源包自己的城市沿街道建造的内容，并且每个模板地块还会作为房屋之一加入游戏自带的村庄，入口位于其正面中央。若完全没有地块文件，城市会按各街区所在生物群系的村庄类型，建造游戏自带的村庄房屋。一旦某条街道要求一个地块，由 `weight` 决定选中你的哪一个；`villages` 设置中的 `villagePieces` 则以 `mypack:smithy`、`smithy` 或该地块所建造的结构来指明模板保留哪些地块。街道本身如何铺设、装饰、架桥、开凿隧道和铺设铁轨，由[各分组的作用](#各分组的作用)下的 `village*` 设置决定。

### 农场

*村庄地块*

`farm` 是一块用描述而非代码定义的田地：一个按你要求的大小划出的地块，以某种方块作边框，内部填满以水渠隔开的土壤行，并按方块从你的列表中挑选作物种下。

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

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `crops` | farm | 方块名称列表 | 小麦 | 每个方块种一株，处于随机的生长阶段 |
| `edge` | farm | 方块名称 | `minecraft:oak_log` | 地块四周的边框 |
| `soil` | farm | 方块名称 | `minecraft:farmland` | 各行所用的材料 |
| `water` | farm | 布尔值 | `true` | 在各行之间放置一条水渠 |
| `rowWidth` | farm | 整数 | `2` | 每行土壤有多宽 |

### 由模板构建

*村庄地块*

`template` 则改为放置你的一个 `.nbt` 结构，并转向面对街道。

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

若 `template` 的 `structure` 指向你的某张[结构地图](#结构地图)，则整个复合结构会作为这个地块放置。此时地块的大小来自地图，即其占地范围与叠放的层数乘以单元格大小，因此 `width`、`height`、`depth` 和 `integrity` 都不会被读取。位于地图 `ground` 之前的层会作为地下室向下挖掘，带权重的调色板单元格仍会按每座建筑分别抽取，因此同一张地图建出的两座塔可以各不相同。

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| 键 | 使用者 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `structure` | template | `namespace:name` | 无 | 要放置的模板，或你的某张结构地图，此时由地图决定地块的大小 |
| `integrity` | template | 1 到 100 | `100` | 模板方块中会出现的百分比 |
| `lootTable` | template | `namespace:path` | 无 | 放置后的模板内每个箱子在首次打开时所用的战利品表。指定了结构地图的地块不受影响 |
| `villagers` | 全部 | 整数 | `0` | 地块生成多少人 |
| `villagerEntity` | 全部 | `namespace:name` | 村民 | 住在这里的是谁，例如你自己的实体变种 |
| `villagerX` | 全部 | 整数 | `1` | 他们出现的位置，沿地块宽度方向 |
| `villagerY` | 全部 | 整数 | `1` | 他们出现的位置，位于地板之上的高度 |
| `villagerZ` | 全部 | 整数 | `1` | 他们出现的位置，向地块内部的进深 |

## 城市布局地图

*生成世界*

城市地图在网格上绘制一座城市的街道规划，一个字符对应一个单元格，城市依据这幅绘图布局，而不是随机生成。街道、广场和地块与随机城市所用的构件完全相同，因此所有街道选项、桥梁、隧道、地铁、下水道、路灯和广场中心装饰物都原样适用。世界模板在 `villageLayout` 中指定地图。

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `cell` | 数字 | `48` | 网格间距，以方块计，8 到 128。街道沿单元格中线延伸，宽度为资源包的街道宽度，地块则位于各自单元格的中央，因此单元格需要容纳最宽的地块，并留出临街的余地 |
| `palette` | 对象 | 无 | 每个字符铺设什么，见下表 |
| `map` | 列表 | 无 | 各行，最多 64 乘 64 个单元格。比最宽的行短的行，在其末尾之后视为开放 |
| `settings` | 对象 | 无 | 仅用于这张地图的城市设置，沿用世界模板所用的名称，例如 `villagePathCenterBlock`。它们优先于模板中的设置，而生物群系自己的设置仍优先于它们 |

| 值 | 作用 |
| --- | --- |
| `"#": "street"` | 沿某一行或某一列连续的街道单元格会成为一条符合资源包宽度的街道。行方向与列方向的街道相交处，路口与其他路口一样铺设。在两个方向上都没有连续段的孤立街道单元格，会沿行方向铺成一小段短支路 |
| `"+": "plaza"` | 带有中心装饰物的广场。连续段会穿过广场单元格，因此街道在广场处相会；位于交叉口的广场会像环岛一样，在十字路口中央立起其 `villageWellStructure` 中心装饰物。文件中的第一个广场就是城市自己的中心，它把地图固定到城市奠基的位置；没有广场的地图则以该处为中心 |
| `"a": "alley"` | 一条狭窄的通道。建筑面向它，但它不连接任何东西，仍按小巷规则处理 |
| `"J": "junction"` | 向两个方向都铺设的街道单元格，因此即使绘图中只有一个方向穿过，那里也会出现一个十字路口。穿过它的支臂只有一个单元格长 |
| `"b": "bulb"` | 以环形尽端路收尾的街道单元格。地图中一旦有了环形单元格，只有位于环形单元格内的街道末端才会得到环形尽端路；没有任何环形单元格的地图，则依据世界种子，让四个死胡同中的三个成为环形尽端路。环形尽端路只会安置在其范围内没有地块、其他街道、铁路或广场中心装饰物的位置：它会缩小以适应空间，最小可至比街道略宽一点，若在任何尺寸下都没有空间，该末端就保持为普通的尽头 |
| `"E": { "kind": "elevated", "height": 8 }` | 抬升到桥面上的街道单元格，桥面在其相连的抬升单元格路段下方最高地面之上 `height` 格方块处，范围 2 到 64，两端各有一条每行升高一格的坡道。路段内部的街道交叉口随之升起，沿途的地块仍留在地面上。若桥面或坡道会触及铁路或广场中心装饰物，该路段保持在地面高度，并在日志中留下一行记录。任何值都可以像这样写成对象，由 `kind` 指明是哪个词 |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | 使用自己的一组街道键铺设和铺面的街道，这些键优先于地图和模板的设置。它的宽度取决于自己的 `villagePathExtraWidth`、`villagePathSidewalkWidth` 和线条，其路面、线条和人行道取决于自己的方块键，因此大道或小巷都可以用专属的标记绘制。一段连续街道采用其中第一个设置了任何键的单元格的键。无论多宽多窄，绘制出的街道始终是街道：绝不会被当作小巷 |
| `"T": "mypack:tower"` | 一个地块单元格，按该地块定义铺设，位于单元格中央并面向最近的街道 |
| `"T": ["mypack:a=3", "mypack:b=1"]` | 同上，但依据世界种子和单元格的位置按权重抽取，因此同一个世界在那里总是铺设相同的地块 |
| `"g": "grow"` | 交给随机生成的布局处理，它会填充这类单元格，并从地图向外扩展 |
| `.` 或 `open` | 开放的地面，不铺设任何东西 |

每张地图会依据世界种子从四个朝向中抽取一个并整体转动，因此规划图从任何一侧看都一样。街道最先铺设，所以若地块会与街道或另一个地块重叠，该地块会保持开放并在日志中留下一行记录；若某个地块名称没有任何资源包提供，其单元格同样保持开放。地图不会改变构件的装饰方式：街道键、`villageBlocks`、路灯和广场中心装饰物，其表现都与随机城市一致。

## 回溯生成

*生成世界*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "retrogen": true,
    "adoptExistingChunks": false
  }
}
```

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `retrogen` | 布尔值 | `false` | 为每个标记了 `"retrogen": true` 的世界生成条目，补上在该条目出现之前保存的区块。关闭时，已存在的区块保持不变。无论开关与否，区块在生成时都会被标记，因此之后再开启时只会处理比资源包更旧的区块 |
| `adoptExistingChunks` | 布尔值 | `false` | 首次遇到旧区块时的处理方式：开启时，该区块会被盖章，视作已由这个资源包生成过，永远不会补生成；关闭时，它与其他区块一样被补生成。要填充一个已有的世界，请开启 `retrogen` 并关闭此项 |

带有 `"retrogen": true` 的条目会生成到你添加它之前就已保存的区块中。每个区块都会记录它已经历过什么，因此不会重复处理。

条目上的标志只表示该条目有资格参与。补生成由 `retrogen` 设置开启，资源包可以在其 `settings` 块中设置，玩家也可以在配置中设置，默认关闭。与之配合，`adoptExistingChunks` 决定首次遇到旧区块时发生什么：开启时，该区块会被盖章，视作已由这个资源包生成过，永远不会补生成；关闭时，它与其他区块一样被补生成。在 `adoptExistingChunks` 同样开启时开启 `retrogen` 不会有任何效果，因为每个旧区块在排入队列之前就已被注销。要填充一个已有的世界，请同时开启 `retrogen` 并关闭 `adoptExistingChunks`。配置中的 `retrogenChunksPerTick` 默认为 `2`，表示每刻补生成多少个旧区块。

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

在配置中更改 `retrogenKey` 会让每个区块重新符合条件，从而在旧矿脉之上再添加新矿脉，使密度翻倍。这是有意为之，也是该键需要手动设置的原因。

## 预生成

*生成世界*

提前生成世界的地形，使玩家游玩时无需再生成区块：没有区块卡顿，磁盘占用大小确定，只需在一开始等待一次，而不是在头一个小时里不断卡顿。

出生点周围的前 12 个区块总会被处理，无论资源包或配置如何设置，因为在任何人加入之前，游戏自己恰好就会生成这么多。`pregenOnNewWorld` 设置再向外延伸多远，而命令则可以手动运行一次。

`/rdplserver pregen <radius>` 会生成运行位置周围该区块半径内的每一个区块。`status` 会报告进度，`stop` 会终止它。地形由游戏自己的区块系统生成，每次同时处理 `pregenChunksInFlight` 个区块，每次一个由 32 乘 32 个区块组成的区域文件，各区域从中心起按环依次处理，同一区域内的区块沿希尔伯特曲线排列，且每个区域完成后才开始下一个，生成的区块已完成光照与收尾，因此之后无需再运行光照处理。

运行期间所有人都会被固定：变为旁观者，原地不动，屏幕中央显示一条脉动的线，进度显示在动作栏上，周围的天空保持静止，每个维度中的所有生物和机器都被冻结，正在生成的那个维度的时间与天气也停在原处。每位玩家抵达时的模式会在被固定时写入该玩家，因此运行途中保存的存档、崩溃或重新加入都不会让任何人滞留在旁观者模式；运行结束时会原样归还所取走的模式，若资源包设置了 `worldGameMode` 则归还该模式，`hardcore` 则归还生存模式。装有该模组的客户端在被固定时会看到视野起雾，之后看到徽标淡入；原版客户端则只看到普通的固定画面。每个维度已生成到什么程度会保存在世界中，因此已完成的世界不会再次运行。结束的消息会在世界备份之前通知所有人，由控制台或命令方块启动的运行会把计数报告回那里。

在资源包中，这些键放在[世界模板](#世界模板)的 `settings` 块中，与其他所有 `chunks` 键一样。此处展示全部键，唯有 `pregenBorderLimit` 例外，因为它只存在于配置中：

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

### 生成的内容

*预生成*

| 键 | 作用 | 设置它的原因 |
| --- | --- | --- |
| `pregenOnNewWorld` | 在任何人游玩之前，围绕出生点生成的区块半径。12 是下限，0 表示取这个下限而不是什么都不生成，因为游戏本来就会自行在出生点周围生成 12 个区块。调高它可以延伸得比游戏更远 | 设定资源包在游戏已生成的地形之外延伸多远 |
| `pregenDimensions` | 要生成哪些维度，按 ID 依次列出，每个维度围绕自己的出生点 | 添加下界、末地或你自己的维度 |
| `pregenAllDimensions` | 服务器持有的每一个维度，而不是一份列表，主世界在前，其余按 ID 顺序 | 维度很多的资源包。每个模组的维度都算在内，所以要留意体积 |
| `pregenDimensionsWhenEntered` | 这些维度在有人首次踏入时才生成，并再次固定所有人直到完成 | 大多数玩家不会去的维度；从不前往的玩家无需付出任何代价 |
| `pregenToBorder` | 将每个维度填充到其世界边界，而不是按半径，并以边界为中心 | 有边界的世界 |
| `pregenBorderLimit` | 边界最多可延伸多远，以区块计，向任一方向，超出则拒绝运行。仅限配置，绝不是资源包的键 | 防止失控的运行；只有在清楚它所需的时间和磁盘空间时才调高 |

请在发布之前，按要发布的半径亲自从头到尾运行一次。区块数随半径的平方增长，每个方向 63 就是一万六千个区块，500 则超过一百万，所以你测试世界的区域文件夹大小和实际耗时，才是摆在玩家面前的真实数字。不要发布从未运行过的半径。

### 一次运行的行为

*预生成*

| 键 | 作用 | 设置它的原因 |
| --- | --- | --- |
| `pregenResume` | 被停止或中断的运行从中断处继续。运行开始时，其维度、中心和半径会写入存档，已完成的计数每十秒写入一次，因此崩溃、断电或中途退出，在下次加载时都会从中断处约十秒之内恢复。主动停止的运行，无论是通过命令还是停滞监视器，都保持停止状态 | 服务器上的长时间运行；小规模运行即使没有它也能轻松重启 |
| `pregenChunksInFlight` | 运行一次向游戏请求多少个区块。更多会让生成线程更忙碌，而让服务器对被固定围观的人响应更慢 | 在空服务器上调高，在有人游玩的服务器上调低 |

### 向玩家显示什么

*预生成*

| 键 | 作用 | 设置它的原因 |
| --- | --- | --- |
| `pregenRunningSays`、`pregenFinishedSays`、`pregenStoppedSays` | 各阶段的消息。第一条可以包含表示百分比的 `%d`，其后可以包含表示维度名称的 `%s`，或用 `%1$d` 和 `%2$s` 以任意顺序放置它们。保持默认值时，它们会使用每位玩家各自的语言 | 以资源包自己的口吻改写，在生成多个维度时写出维度名称，或将其静默 |
| `pregenSpectatingSays` | 生成地形期间屏幕中央的固定提示行。保持默认值时，它会使用每位玩家各自的语言；留空则不显示任何内容 | 请保持在约三十五个字符以内，否则小窗口会将其截断 |
| `pregenLogo` | 预生成完成时徽标所在的位置：`left`、`center` 或 `right`，位于屏幕中央文字之上，显示几秒钟，然后随雾一同淡出 | 它总会显示；未知的词会被当作 `center` |
| `welcomeSays` | 绿色的问候语，每次登录以及预生成之后都会显示。单独的一项是适用于所有地方的那一行；`dimension=message` 形式的项会为该维度覆盖它，并向每个抵达该维度的人问候，例如 `"minecraft:the_nether=Welcome to the Nether!"`。维度也可以写成 1.12.2 的 `0`、`-1` 或 `1`，在地形生成期间抵达的人不会收到问候。`=` 之后的空消息会让该维度静默；空列表则不显示任何内容。保持默认值时，它会使用每位玩家各自的语言 | 用一条单独的行写出你资源包的名称；再添加各维度的行，为每个世界设定主题。请将每行保持在约三十五个字符以内 |
| `saysCard` | 将这个模组发出的各行文字，即欢迎语、中途加入的玩家收到的造陆提示、运行的结束（运行进度仍显示在动作栏上），以及威胁提示行，以卡片形式显示在右下角，而不是显示在聊天中。卡片滑入，停留八秒后淡出，即使打开了界面也会显示在其上方 | 当聊天栏很忙，或这些文字应当读起来像是世界的一部分而不是闲聊时，可将其开启 |
| `saysIcon` | 绘制在卡片上的物品，例如 `minecraft:compass`。留空则不绘制 | 为卡片配上资源包的标志 |
| `saysColor` | 卡片的背景颜色，十六进制，例如 `1E2630`。留空则使用深石板色 | 与资源包的配色相配 |
| `saysImage` | 来自资源包客户端资源的 PNG，例如 `rubyworld:textures/gui/card.png`，拉伸后铺满卡片作为背景，绘制在颜色之上。留空则不绘制 | 为卡片配上一块绘制的面板；图片请保持宽而矮，它会被拉伸到文字所需的大小 |
| `saysBackground` | 绘制卡片的面板、边框和颜色条，以及玩家被固定时屏幕中央欢迎语和提示后方的深色背景。关闭时只剩下文字（保留其阴影），以及设置了的话 `saysImage` | 让文字漂浮在世界之上，或者让绘制的 `saysImage` 独立呈现 |
| `saysFont` | 卡片文字所用的字体，以 `namespace:name` 命名，例如 `rubyworld:runes`。留空则使用 RDPL 字体 `resourcedatapackloader:rdpl`。它所指向的文件在“卡片”一节中说明 | 为卡片配上资源包自己的字体 |
| `toasts` | 显示游戏的哪些提示框，即右上角的弹出通知。`true` 显示全部，`false` 全部不显示；列表则只显示其中列出的种类：`advancements`，`recipes` 表示已解锁的配方，`tutorial` 表示操作指引提示，`system` 表示游戏自己的通知，`other` 表示其余各项未涵盖的所有提示框，例如其他模组的。默认不显示任何一种。玩家的客户端会在加入时采用该值 | 当资源包用进度来引导玩家，而其余提示只会碍事时，请保留 `["advancements"]` |

### 备份与地图重置

*预生成*

| 键 | 作用 | 设置它的原因 |
| --- | --- | --- |
| `pregenBackup` | 预生成完成后，在玩家仍被固定时，将世界复制为一份原始备份。这样生成只需付出一次代价：之后的重置，或使用同一资源包和种子的新世界，会还原这份副本而不是再次生成，这比预生成两次快得多。副本保存在存档之外，位于它旁边的 `rdpl-pristine/<world>`，因此其他模组的备份不会将它一并清扫，它也不会出现在它们管理的文件夹中。若副本所对应的资源包与当前加载的不再一致，它会被丢弃，并根据手头的世界重新保存，因此更换资源包绝不会重置成别人的地图 | `false` |
| `pregenBackupSays` | 制作该副本期间向玩家显示的屏幕中央提示行，其后附有百分比。留空则不显示任何内容，副本会静默制作 | `Pack requested world backup` |
| `resetSays` | `/rdplserver reset` 或一轮回合结束将地图复原期间，向玩家显示的屏幕中央提示行。留空则静默重置 | `Pack requested map reset` |
| `resetSendsTo` | 重置时把玩家放在哪里：`spawn`，形如 `x,y,z` 或 `x,z` 的坐标（其中高度为海平面之上一格），或在任一者前加上 `dimension:` 以把他们送入另一个世界，维度可写 ID，也可写 1.12.2 的 `0`、`-1` 或 `1`，重置就是靠它把所有人放进大厅，而不是放回竞技场 | `spawn` |
| `resetRuns` | 重置清除地图之后运行的函数，以 `namespace:path` 命名。它负责重新建造竞技场，因为用函数制作地图的资源包只需再运行一次即可。留空则不运行任何内容 | 空 |
| `resetClearsEntities` | 移除所有不是玩家的实体。生物、掉落的物品和经验全部消失，这样才能让地图恢复到起始状态 | `true` |
| `resetClearsScores` | 将资源包维护的每个目标重置为零，使新的比赛从零开始。队伍本身会被保留 | `true` |
| `resetClearsInventory` | 清空每位玩家的物品栏，包括盔甲和副手，使一轮回合从地图发放的物品开始，而不是从上一轮留下的物品开始。阵营的 `gives` 随后立即再次发放 | `false` |
| `resetClearsExperience` | 将每位玩家的经验重置为 0 级 | `false` |
| `spawnChunkRadius` | 距出生点多远（以方块计）的区块无论是否有玩家在场都保持加载，取整到整个区块：每个方向 `(blocks + 8) / 16`，因此默认的 `128` 保持 8。世界启动时，在服务器就绪之前，主世界会在此基础上每个方向再多准备 4 个区块的正方形范围。`0` 表示既不准备也不保持任何区块。RDPL 用自己的区块票据来保持它们，因此在 1.21.1 上，只要此键生效，`spawnChunkRadius` 游戏规则就不起作用 | 让出生点的机器或农场持续运行，或用 `0` 关闭出生点区块 |
| `spawnChunkRadii` | 以 `dimension=blocks` 形式书写的主世界半径，如 `minecraft:overworld=64`，它会覆盖 `spawnChunkRadius`。只有主世界有出生点区块，因此其他维度的条目不会改变任何东西 | 在按维度设置半径的资源包中，设定出生区域的大小 |

---

# 游戏模式

## 世界介绍

*游戏模式*

`<namespace>/worldintro/*.json`

文件名由你自己决定，只有文件夹会被读取。资源包提供的每个介绍都会运行，按资源包顺序。

在玩家进入世界、获得控制权之前，显示一系列页面。可以是叠在图片上的滚动文字、标题卡、幻灯片，或三者依次出现。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `pages` | 是 | 页面列表 | 无 | 按顺序显示。没有页面的文件会被拒绝并报错 |
| `once` | 否 | 布尔值 | `false` | 每位玩家在每个世界中只播放一次，而不是每次加入都播放 |
| `music` | 否 | 声音事件名称 | 无 | 整个流程使用一首曲目，随第一页开始播放 |
| `requires` | 否 | 模组 ID 或资源包命名空间的列表 | 无 | 除非全部存在，否则跳过该介绍 |

### 页面

*世界介绍*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `mode` | 否 | `scroll` 或 `static` | `scroll` | 文字移动，或文字静止不动直到玩家继续 |
| `text` | 否 | `.txt` 文件的路径 | 无 | 文字内容。只有图片的页面可以省略它 |
| `background` | 否 | 纹理路径 | 平铺的泥土背景 | 单个背景 |
| `backgrounds` | 否 | 纹理路径列表 | 无 | 多个背景，循环切换。若两者都给出，则叠加在 `background` 之上 |
| `interval` | 否 | 秒 | `5.0` | 存在多个背景时，每个背景停留多久 |
| `time` | 否 | 秒 | 根据文字推算 | 滚动页面从头到尾需要多久。对于静止页面，或任何类型的最后一页，它表示多久之后页面自行翻过，若不设置，它们会等待按钮 |
| `direction` | 否 | `up` 或 `down` | `up` | 滚动文字朝哪个方向移动 |
| `textScale` | 否 | 数字 | `1.0` | 对字体大小的倍数。`static` 页面会按屏幕宽度（减去两侧的页边距）折行，若文字仍会延伸到按钮下方，则缩小绘制，最多缩小到一半，直到放得下 |
| `settle` | 否 | 布尔值 | `false` | 以最后一行居中收尾，而不是完全滚出屏幕 |

### 文本与时间

*世界介绍*

文本文件放在 `assets/<namespace>/texts/*.txt`。纯文本，一行一个段落，空行保持为空行。`.md` 文件以相同方式读取，两种文件都支持下面的格式。`PLAYERNAME` 会被替换为玩家的名字，与原版终末之诗所用的替换相同。

`time` 设定页面持续多久，因此无论页面只有一行还是二十行，用时都相同。请通过页面上放多少内容来调整阅读速度。若省略 `time`，页面会以与原版制作人员名单相同的速度运行，此时文字越多，用时越长。

### 文本格式

*世界介绍*

介绍文本支持 Markdown。不含任何标记的文件，显示效果与纯文本完全一致。

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

| 标记 | 写法 | 显示效果 |
| --- | --- | --- |
| 标题 | 行首的 `# `、`## `、`### ` | 加粗并放大：分别为正文大小的两倍、一点五倍和一点二五倍，对齐方式与正文相同 |
| 粗体 | `**text**` | 字体的粗体字形 |
| 斜体 | `*text*` | 字体的斜体字形 |
| 粗斜体 | `***text***` | 倾斜的粗体字形 |
| 删除线 | `~~text~~` | 带删除线 |
| 代码 | `` `text` `` | 染成青色 |
| 链接 | `[text](url)` | 仅显示文字，带下划线；不可点击 |
| 符文 | `{runic}text{/runic}` | 文字以符文密码字体 `resourcedatapackloader:rdpl_runic` 显示，同一行的其余部分保持原字体；其中的粗体和斜体采用该密码字体的粗体和斜体字形。它可用于标题、列表项和引用，未闭合的 `{runic}` 会按原样显示 |
| 项目符号 | 行首的 `- ` 或 `* ` | 一个圆点，折行的部分缩进到文字下方；标记前加两个空格则嵌套一级 |
| 编号 | 行首的 `1. ` | 按所写的数字显示，缩进方式相同 |
| 引用 | 行首的 `> ` | 缩进并变暗 |
| 分隔线 | 单独一行的 `---` | 横跨文字宽度的一条水平线 |
| 图片 | 单独一行的 `![alt](namespace:textures/....png)` | 图片，缩小到文字宽度并保持其形状；若无法读取，则显示替代文字 |
| 转义 | 标记前加 `\`，例如 `\*` | 该标记作为普通字符 |

表格和围栏代码块（位于 ``` 行之间）会按纯文本绘制，标记也原样保留。滚动页面推算出的时间和静止页面的缩小以适应，都会计入排版后的高度，包括图片。卡片标题与文字行、Says 消息以及欢迎语和固定提示，支持从粗体到符文的行内标记，每次一行。

### 玩法

*世界介绍*

滚动页面在时间用尽时转到下一页。最后一页绝不会自行前进，它会等待。底部有 **Next Page** 和 **Skip All**，最后一页则只有一个 **Continue to World**。Esc 键的作用与 Skip All 相同。静止页面的每一行都居中。滚动页面则保持固定的列宽，如同制作人员名单。

在单人游戏中，世界会在介绍后面暂停，因此玩家阅读时不会有东西悄悄逼近。唯一的例外是介绍打开时仍在生成地形：此时生成会在页面后面继续进行，玩家保持旁观者的固定状态，直到他们继续进入世界，即使运行先行结束也是如此。在服务器上，世界会继续运行，而原版客户端根本不会看到介绍，照常加入。欢迎问候会等到页面关闭之后才出现，因此不会被页面遮住而错过。

`once` 记录在玩家的存档数据中，并在死亡后依然保留。`/rdplserver intro` 会为运行它的人清除该记录，因此介绍会在他们下次加入时再次播放。它不会当场重播，这样就避免它成为在游戏进行中重返开场流程的途径。

背景会被拉伸以填满窗口，因此 16:9 的图片适合 16:9 的窗口，而正方形的图片看起来会被压扁。请把图片裁剪成合适的形状，而不要依赖适配。`music` 接受任何已注册的声音事件，无论是原版的，还是你自己的资源包通过 `sounds` 添加的。它不会循环，因此较短的曲目播完后会留下一片寂静。

如果有多个资源包提供了介绍，它们的页面会按资源包顺序首尾相接地运行，而不是只有一个胜出。若你只想要其中一个，请用 `requires` 加以限定。

## 队伍

*游戏模式*

`<namespace>/teams/*.json`

文件名由你自己决定，只有文件夹会被读取，多个文件会叠加。每个文件是一个阵营。

阵营是游戏自己记分板上的一支真正的队伍，因此 `/team list` 能看到它，它的成员在存档和重新加载后依然保留，没有这个模组的客户端显示的颜色和名牌，也与任何原版队伍一样。成员关系按名称确定，因此任何有名称或 UUID 的东西都可以加入阵营：玩家、僵尸、村民、盔甲架。

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

### 阵营

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `name` | 文本 | 文件名 | 队伍在记分板上的名称，1 到 16 个字符。`/team` 和其他文件使用的就是它 |
| `displayName` | 文本 | 名称 | 向玩家显示的名称，用以代替 name |
| `color` | 文本 | `white` | 十六种文字颜色之一。它为名牌着色，并且是各队伍侧边栏栏位所依据的键 |
| `prefix` | 文本 | 空 | 放在成员名字之前，位于颜色之后 |
| `suffix` | 文本 | 空 | 放在成员名字之后 |
| `scoreboard` | 布尔值 | `true` | 该阵营是否作为游戏记分板上的一支队伍存在。关闭则完全不设队伍：它的生物改为在名称中带上阵营的颜色，没有任何东西阻止它们互相战斗，也不会有积分记到它头上，因为计分是按队伍进行的 |

只有在资源包要求时才会设立阵营：若各处都没有 `teams` 文件夹，这个模组不会添加任何队伍，不监听任何东西，也不提供该命令。编辑了文件的服务器管理员可以运行 `/rdplserver reload`，在不重启的情况下把更改应用到正在运行的世界。

### 战斗与可见度

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `friendlyFire` | 布尔值 | `false` | 成员是否可以互相伤害。也是 `mobFriendlyFire` 的默认值 |
| `mobFriendlyFire` | 布尔值 | `friendlyFire` | 阵营的生物是否会用爆炸和投掷的 TNT 伤害自己一方，这是游戏本身从不阻止的。关闭则保护己方；开启则保持游戏原有的行为 |
| `seeFriendlyInvisibles` | 布尔值 | `true` | 成员在隐身时是否能互相看见 |
| `nameTags` | 文本 | `always` | `always`、`never`、`hideForOtherTeams` 或 `hideForOwnTeam`，不区分大小写 |
| `deathMessages` | 文本 | `always` | 同样的四个词，决定成员死亡时通知谁 |
| `collision` | 文本 | `always` | `always`、`never`、`pushOtherTeams` 或 `pushOwnTeam`，不区分大小写 |

### 谁会加入

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `entities` | 列表 | 空 | 实体 ID，其每一次生成都加入该阵营，例如 `minecraft:zombie` 或你自己的某个实体 |
| `players` | 列表 | 空 | 在登录时加入该阵营的玩家名称 |
| `spawnBox` | 列表 | 无 | 六个整数，x y z 到 x y z。在其内部生成的任何东西都会加入，两个角点的先后顺序可以任意 |
| `joinable` | 布尔值 | `true` | 玩家是否可以用 `/rdplserver team join` 加入。对只供生物使用的阵营请设为 false |
| `balance` | 布尔值 | `false` | 不带名称的 `/rdplserver team join` 是否可以把玩家放到这里。在允许它的阵营中，会选择玩家最少的那一个 |
| `picks` | 数字 | `0` | 该阵营随机抽取多少名成员。每次回合开启时，阵营会让上一轮抽到的人回到他们原来站的位置，并从 `picksFrom` 所指的全部对象中重新抽取；在两次抽取之间，来自该池的登录或生成会立即填补空缺的席位。它的用途是：在所有人中抽出一名玩家，单独成为一个阵营 |
| `picksFrom` | 列表 | 空 | 抽取的来源：`players` 表示所有在线玩家，实体 ID 表示该种类的每一只存活生物 |
| `standIn` | 对象 | 无 | 在没有玩家加入该阵营期间，由一只生物替它守住阵营：`{ "entity": "mypack:herobrine", "at": "23,31,0" }` 会让该实体的一只在主世界的那个位置保持存活，缺失时将其召唤，并在有玩家加入阵营的那一刻将其移除，因此游戏会在玩家接手这个角色之前与 AI 对战。每五秒检查一次；该位置必须处于已加载的地面。在有大厅的游戏中（`opens.by: leader`），替身只会在大厅等待期间以及回合开启时召唤，因此倒下的替身会在回合的其余时间及其结束阶段保持缺席，直到所有人回到大厅；没有大厅时，在以 `ends.lastStanding` 结束的回合进行期间，倒下的替身不会被补充 |

有三种加入方式，一个阵营可以同时使用。`entities` 指明实体 ID，该类型的任何东西在生成时加入，资源包就是这样在不改动生物本身的情况下给它们分配阵营的。`spawnBox` 划定世界的一角，在其中生成的任何东西都会加入，适合双方使用同一种生物的竞技场。`players` 则直接指名玩家。除此之外，玩家可以用 `/rdplserver team join <name>` 加入，除非该阵营把 `joinable` 设为 false，并可用 `/rdplserver team leave` 离开。

### 初始装备与出生点

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `gives` | 列表 | 空 | 玩家加入阵营时放入其物品栏的物品，单个物品写物品名称，多个或不会磨损的物品写 `{ "item", "count", "unbreakable" }`，放入任意空闲栏位，没有空位时则丢在其脚边。在清空物品栏的重置（`resetClearsInventory`）之后会再次发放 |
| `spawn` | 文本 | 无 | 主世界中的 `x,y,z`，回合开启时阵营的玩家被放置在这里，使每个阵营从自己的地盘开始；没有它时，他们留在重置或大厅让他们所在的位置 |

### 主导者

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `lead` | 文本 | `none` | 如何选出阵营的主导者：`none`；`first` 表示在线者中最早加入阵营的人，因此在有人离开时它会沿加入顺序向下传递，并在其返回时回到他们手中；他们会在抵达时（越过介绍和任何固定）以及它传给他们时收到通知；`topScore` 表示 `leadOn` 所指目标上得分最高的人；`appointed` 表示 `leadIs` 所指的玩家；`vote` 表示成员投票选出的人；`claim` 表示最先认领的人。主导者只是一个标签和一种颜色，别无其他：它不授予任何权力，因此主导者下线不会破坏任何东西 |
| `leadOn` | 文本 | 空 | 与 `topScore` 一起使用，成员据以排名的目标。每次读取时都会重新计算，因此它会跟随分数变化 |
| `leadIs` | 文本 | 空 | 与 `appointed` 一起使用，担任主导者的玩家 |
| `leadSays` | 文本 | `You are the current round leader` | 在主导者落到某位玩家身上时告知他：抵达他们所主导的阵营时、认领时，或 `first` 主导者传给他们时（此时会附带离开的是谁）。`{side}` 是该阵营的显示名称；留空则不告知任何内容 |
| `leadRuns` | 文本 | 空 | 一个函数，`namespace:path`，每当主导者传给一位玩家时运行一次：第一位主导者，以及此后的每一次交接。它以主导者的身份、在其位置运行，权限与进度奖励的函数相同，因此 `@s` 就是主导者。每秒检查一次；离线的主导者会在其下次在线时补运行。重启会重新决定主导者 |

## 计分

*游戏模式*

`<namespace>/scoring/*.json`

文件名由你自己决定，只有文件夹会被读取，多个文件会叠加。每个文件是一个目标。

目标是游戏自己记分板上的一个真正的目标，因此 `/scoreboard players list` 能读到它，它的分数在存档后依然保留。`criterion` 是游戏自行统计的内容：`dummy` 表示只有这个资源包才会改动的分数，或者 `deathCount`、`playerKillCount`、`totalKillCount`、`health`、`air`、`armor`、`food`、`level`、`xp`、`trigger`，以及任何以 `/scoreboard` 所接受的写法书写的统计，例如 `minecraft.custom:minecraft.jump`。1.12.2 的统计如 `stat.jump` 会被读作它后来变成的那一项。

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

### 目标

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `name` | 文本 | 文件名 | 目标在记分板上的名称，1 到 16 个字符 |
| `displayName` | 文本 | 名称 | 向玩家显示的名称，用以代替 name |
| `criterion` | 文本 | `dummy` | 游戏自行统计的内容。未知的准则会被拒绝，并输出一行说明 |
| `display` | 文本 | 空 | `sidebar`、`list`、`belowName`（也接受 `below_name`）或 `sidebar.team.<color>`。留空则不在任何地方显示；没有可供打开的记分板界面 |
| `render` | 文本 | 准则自带的 | `integer` 或 `hearts` |
| `teamTotals` | 布尔值 | `true` | 积分记到以成员所属队伍命名的行上 |
| `individuals` | 布尔值 | `false` | 积分同时也记到成员自己的一行上 |
| `carries` | 布尔值 | `false` | 目标在地图重置时保留，而不是随之清空。回合胜场的比赛累计就是一例 |
| `awardsTo` | 文本 | 空 | 该目标结束时向其授予一分的另一个目标，授予领先的阵营。等级排名不授予任何东西 |

### 积分

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `points.kill` | 对象 | 空 | 实体 id 对应的积分，记在击杀者所属阵营名下。`minecraft:player` 为击杀玩家计分 |
| `points.death` | 整数 | `0` | 成员死亡时得到的积分，无论死因。可以为负数 |
| `points.ownKill` | 整数 | `0` | 击杀者所属阵营自己人时的积分，取代 `kill` 的值。0 表示不计分；负数表示扣分 |

`points` 是本模组在游戏自身计数之上额外增加的积分，会写入同一个计分项，因此 `/scoreboard` 仍然可以读取。`kill` 按被击杀的实体 id 给出相应积分，记在击杀者所属阵营名下；`death` 在某个阵营的成员死亡时给出相应积分，可以为负数。启用 `teamTotals` 后，积分会记在以队伍命名的行上，侧边栏因此只显示四个阵营，而不是为每只生物各占一行。`individuals` 会额外为每个成员加一行，默认关闭，因为每个生物 UUID 一行读起来只是噪音。

### 回合如何结束

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `ends.atScore` | 整数 | `0` | 某个阵营达到该分数的瞬间比赛结束。0 表示永不因分数结束 |
| `ends.afterMinutes` | 整数 | `0` | 经过这么多分钟后比赛结束。0 表示永不因时间结束 |
| `ends.afterRounds` | 整数 | `0` | 用于被另一个目标 `awardsTo` 的目标：累计颁出这么多回合后比赛结束，无论由谁获得。0 表示永不因回合数结束 |
| `ends.lastStanding` | 布尔值 | `false` | 只剩一个阵营屹立不倒时，回合结束。参与的阵营是回合开始时拥有玩家或存活生物的阵营，至少两个；死亡的玩家出局，以旁观者身份留到回合结束，而玩家全部出局或消失、生物全部死亡的阵营即告败亡。最后屹立的阵营赢得该回合，`awardsTo` 无论分数如何都会为该阵营记录。配合 `resets` 和 `opens.by: leader`，游戏随后回到大厅。此类回合进行期间，不会再次召唤阵营的 `standIn` |
| `ends.outSays` | 文本 | `You are out until the round ends` | 对被淘汰的玩家显示的话。留空则不说 |
| `ends.locksTeams` | 布尔值 | `true` | 回合进行期间加入阵营要等到回合结束，这样就没有人会中途闯入一个正在计分的回合 |

`ends` 负责结束比赛，要么在某个阵营达到 `atScore` 的瞬间，要么在经过 `afterMinutes` 之后。随后会显示排名，由游戏自身排序：以聊天形式显示，若 `results` 要求则以卡片形式显示。没有安装本模组的玩家会以聊天行的形式收到同样的排名，因此没有人会得不到结果。配合 `resets`，这次结束就是一个回合的结束：排名会停留 `intermissionSeconds` 秒，期间动作栏上倒数冷却时间，地图重置为欢迎时的状态，随后经过五秒倒计时开启下一回合。`awardsTo` 把回合交给领先的阵营，所用目标会在重置之后 `carries`（延续）。被延续的目标可以自行结束——三局两胜用 `atScore`，固定局数用 `afterRounds`——并且它的积分会在之后的那次重置中清除，从而开启全新的比赛。

### 回合之间

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `ends.resets` | 布尔值 | `false` | 回合结束时重置地图，具体如 [世界模板](#世界模板) 下的 `resetSays` 及其他重置设置所述，然后开启新的回合 |
| `ends.intermissionSeconds` | 整数 | `10` | 从结束到重置之间，排名停留多久 |
| `ends.intermissionSays` | 文本 | `Round cooldown {seconds}` | 回合结束后的休整期间，每秒显示在动作栏上，`{seconds}` 倒数至重置。留空则不显示 |
| `ends.startsSays` | 文本 | `Round starting in {seconds}` | 重置后开启下一回合的五秒倒计时期间显示在动作栏上，`{seconds}` 倒数。留空则不显示 |

### 大厅

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `opens.by` | 文本 | `auto` | `auto` 在重置五秒后自动开启下一回合。`leader` 则让游戏停在大厅：重置之后以及世界首次加载时，不计任何分，也没有计时，阵营可以自由加入和离开，只有当某个阵营的主导者或管理员运行 `/rdplserver round start` 时回合才会开启，且在仍有人在阅读世界介绍时不会开启；随后进行五秒倒计时，抽签完成，每个阵营被送到各自的 `spawn`。在世界等待期间，直到五秒倒计时结束，玩家留在原地，不能破坏、放置、使用、攻击或丢弃任何东西，也不会受到伤害，尝试时会看到等待提示，其他所有生物也都静止不动：没有 AI，没有移动。命令仍然可用，因此可以加入阵营并开始回合 |
| `opens.says` | 文本 | `Waiting for {leader} to start the round` | 像欢迎语一样显示在屏幕中央，面向每个不是主导者的玩家：当他们越过介绍进入大厅、欢迎语显示过之后；回合结束后大厅再次开启时；主导者来去而内容变化时；以及他们尝试大厅所禁止的事情时。`{leader}` 是所有阵营的主导者，没有主导者时则为 `a leader`。留空则不显示 |
| `opens.leaderSays` | 文本 | `Type /rdpl round start` | 以相同方式、在相同时刻显示给领导某个阵营的玩家，取代 `opens.says`。留空则不显示 |
| `opens.lobby` | 文本 | 无 | overworld 中写作 `x,y,z`，其他世界中写作 `dimension:x,y,z`，例如 `minecraft:the_nether:0,64,0`，大厅持续期间所有人在这里等待：每个玩家以及每个属于某阵营的存活生物都被安置在该点周围的一个圆环上，各自面朝圆心，于是彼此怒目相对。每个单位分得的弧长为其自身宽度加两格，因此互不重叠，人数增多时圆环也随之扩大；每当有人加入或离开，圆环会重新布局。高度取他们所站立的地面，在上下三格范围内查找。玩家和生物可以直接进入那个世界并返回，不会建造传送门。回合开启时玩家前往所属阵营的 `spawn`，仍站着的生物则被放回它原来所在世界中的原位 |
| `opens.lobbyJoins` | 布尔值 | `false` | 回合中途登录的玩家，会被放进大厅以旁观者身份等待回合结束，而不是出现在他们登出时的位置。需要 `opens.lobby` |
| `opens.joinsSays` | 文本 | `Round is in progress, you can join after it ends` | 对他们显示的话。留空则不说 |

### 重置回合

*计分*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `reset.lead` | 文本 | `none` | 回合进行时，`/rdplserver round reset` 对阵营主导者的作用。`now` 立即结束回合并重置地图；`vote` 改为发起投票；`none` 不给主导者任何特权，因此在 `players` 允许的情况下，主导者像其他玩家一样发起投票。管理员总是立即重置 |
| `reset.players` | 文本 | `none` | `vote` 允许任何阵营的玩家用 `/rdplserver round reset` 发起投票。`none` 则把重置留给主导者 |
| `reset.teams` | 列表 | 空 | 其玩家可以发起投票的阵营。留空表示所有阵营 |
| `reset.passPercent` | 整数 | `51` | 回合要被重置，投赞成票的投票者所占的比例，1 到 100。`51` 即过半，`100` 即全体 |
| `reset.voteSeconds` | 整数 | `30` | 一次投票持续多久，最少五秒。结果一旦确定就会提前结束 |
| `reset.cooldownSeconds` | 整数 | `60` | 投票失败后，要隔多久才能再次发起。使用 `now` 的主导者不受其限制 |
| `reset.leadSays` | 文本 | `{player} reset the round` | 回合被立即重置时告知所有人，`{player}` 为执行重置者。留空则不说 |
| `reset.voteSays` | 文本 | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | 发起投票时告知所有人，`{player}` 为发起者。留空则不说 |
| `reset.tallySays` | 文本 | `Reset the round? {yes} yes, {no} no, {seconds}` | 投票期间每秒显示在动作栏上，`{seconds}` 倒数。留空则不显示 |
| `reset.passSays` | 文本 | `The vote passed, so the round is reset` | 投票通过时告知所有人。留空则不说 |
| `reset.failSays` | 文本 | `The vote failed, so the round goes on` | 投票未通过时告知所有人。留空则不说 |

重置会让回合当场中止。排名以 `The round was reset` 为标题显示，无人获得该回合，休整倒计时开始，地图的重置如同回合以 `ends.resets` 结束一样，在 `opens.by` 为 `leader` 时回到大厅。无论该回合是否本来会自行结束，重置都有效，但在大厅中、开启回合的倒计时期间，或回合已结束且其重置即将进行时无效；此时仍在进行的投票会被丢弃。

每位在线且属于某个阵营的玩家都可以投票，无论所属哪个阵营，用 `/rdplserver round vote yes` 或 `no`，并且可以在投票进行期间更改投票。发起投票的人视为已投赞成票，时间结束时尚未投票的玩家按反对计。在设有阵营的资源包中，不属于任何阵营的玩家既不能发起也不能参与投票；在没有阵营的资源包中，每位在线玩家都可以。使用的是第一个其 `reset` 允许任何人重置的计分文件。

### 结果

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `results.card` | 布尔值 | `false` | 以卡片而不是聊天的形式显示排名 |
| `results.title` | 文本 | 名称和 `results` | 卡片的标题 |
| `results.icon` | 文本 | 空 | 绘制在卡片上的物品，例如 `minecraft:tnt` |
| `results.image` | 文本 | 空 | 取代物品绘制在卡片上的图像 |
| `results.background` | 文本 | 深石板色 | 卡片的背景色 |
| `results.seconds` | 整数 | `8` | 卡片停留多久，至少一秒 |

## 袭击

*游戏模式*

`<namespace>/raids/*.json`

文件名由你自己决定，只有文件夹会被读取，多个文件可以叠加。每个文件是一场袭击。

袭击是游戏自 1.14 起就有的那种，在游戏自带的某个村庄上展开。当带有 `omen` 效果的玩家进入村庄时袭击开始：该效果被移除，村庄中心 `reach` 范围内的每个玩家都会看到一个 Boss 栏。经过 `waveDelay` 刻后，第一波在村庄周围的圆环上出现，向中心进发，沿途攻击玩家、村民和铁傀儡。袭击者之间不会互相伤害或以彼此为目标，因此它们之间的流矢或误击不会造成任何影响。Boss 栏显示该波剩余的生命值，并在剩下两个或更少的袭击者时开始统计袭击者数量。一波消灭后，下一波等待 `waveDelay` 刻。最后一波消灭且两秒内没有新的袭击者出现时，袭击胜利；在已有一波到来之后，若所有村民死亡或村庄本身消失，则袭击失败。无论哪种结果，Boss 栏都会显示三十秒，并且对范围内的每个玩家以其身份运行相应的函数。

进行中的袭击会随世界一起保存，其袭击者在重新载入后继续行军。袭击在和平难度下、经过 `timeout` 刻后，或村庄周围没有任何地点能容纳一波时，会无结局地停止。村庄是游戏记录其村庄点的地方，即村民占用的床、工作方块和钟：其中心是这些点的中央，范围至少向外延伸 32 格，其村民是在该范围内、且与中心高度相差在四格以内的村民。游戏自身的 `minecraft:bad_omen` 会先触发游戏自身的袭击，因此袭击要使用其资源包自己的效果。

一波袭击来到村庄期间，村民会跑回家并留在那里，就像游戏中的钟敲响时一样。袭击者会拆毁挡路的木门以抵达他们，每扇门十二秒，在普通和困难难度下且 `mobGriefing` 开启时生效；铁门挡得住。类型为 `bell` 的方块无论位于何处都是村庄的钟，并按 [钟](#钟) 所述敲响；袭击的 `bell` 可以指定其他任何方块当作钟来敲响。一波到来时，村庄中的每一口钟都会敲响，被指定的方块在玩家使用时也会敲响：48 格内的村民躲藏十五秒，48 格内的袭击者发光三秒。`minecraft:bell` 本来就存在于游戏的村庄中，可以被指定，而且它仍会按游戏自己的方式敲响；资源包自己的钟方块则通过 NBT 结构放置到村庄中，作为地块或广场的中心装饰。

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

### 袭击

*袭击*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `omen` | 效果名 | 无，必填 | 持有者在村庄内时触发袭击的效果。任何已注册的效果都可以，包括资源包自己的药水 |
| `name` | 文本 | `Raid` | Boss 栏的标题 |
| `color` | 文本 | `red` | 栏的颜色：`pink`、`blue`、`red`、`green`、`yellow`、`purple` 或 `white` |
| `waves` | 波次列表 | 无，必填 | 每一波是一个分组的列表，各波按顺序到来 |
| `waveDelay` | 整数 | `300` | 第一波之前、以及一波结束到下一波之间的刻数 |
| `spawnDistance` | 整数 | `32` | 一波到来时距村庄中心多远。最先尝试此距离的两倍处，然后是此距离处，再然后是村庄内部 |
| `reach` | 整数 | `96` | 中心周围这么多格内的玩家会看到 Boss 栏，结束函数也会以他们的身份运行。越过该范围十六格的袭击者会脱离袭击 |
| `timeout` | 整数 | `48000` | 未完成的袭击经过这么多刻后无结局地停止。`0` 表示永不停止 |
| `sound` | 音效名 | 无 | 每波到来时，从该波来的方向向范围内的每个玩家播放 |
| `wins` | 函数 | 无 | 袭击胜利时，以范围内每个玩家的身份运行 |
| `loses` | 函数 | 无 | 袭击失败时，以范围内每个玩家的身份运行 |
| `bell` | 方块名或列表 | 无 | 在玩家使用时以及每波到来时，像钟一样敲响的其他方块。类型为 `bell` 的方块无需指定也会敲响 |

### 一个分组

*袭击*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `entity` | 实体名 | 无，必填 | 来的是什么。实体变种保留自己的全部行为，并获得行军能力 |
| `count` | 整数或 `{ "min", "max" }` | `1` | 来多少个 |

---

## 卡片

*游戏模式*

`<namespace>/cards/*.json`

文件名由你自己决定，只有文件夹会被读取，多个文件可以叠加。每个文件是一条规则，其 id 为 `<namespace>:<file name>`。规则等待一个触发器，检查其 `when`，然后向其受众显示一张卡片；它还可以运行一个函数。客户端不需要安装任何东西：没有安装本模组的玩家，角落卡片会以聊天行显示，中央卡片会以标题显示。

本模组自己说的每一条消息都是一条内置规则，列在下面。资源包可以通过写一个具有该 id 的文件 `rdpl/cards/<name>.json` 来修改其中一条，该文件不需要触发器：它省略的内容保持现状，`{text}` 代表本模组原本会说的那条消息。没有写任何这类文件的资源包，所有消息都和以前一样。

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

第二个文件保存为 `rdpl/cards/gate_blocked.json`，它把关闭的传送门所显示的红色动作栏文字变成一张带图标和第二行文字的卡片，并且每三十秒最多显示一次。

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

第三个在玩家首次加入时用一张中央卡片迎接他，卡片背后没有面板，只有文字及其阴影，以资源包自己的字体绘制。

### 触发器

*卡片*

| 触发器 | 需要 | 触发时机 |
| --- | --- | --- |
| `command` | 无 | 运行 `/rdplserver card <rule> [players]` 时。该命令会跳过 `when`、`repeat` 和 `cooldown`，但仍会运行 `runs`。任何规则都可以这样显示，无论它的触发器是什么 |
| `first_join` | 无 | 玩家首次加入世界时 |
| `dimension_enter` | `dimension` | 玩家抵达该维度时 |
| `biome_enter` | `biomes` | 玩家从别处走入这些生物群系之一时 |
| `structure_enter` | `structures` | 玩家从外面走入这些结构之一时 |
| `advancement` | `advancement` | 玩家获得该进度时 |
| `time_of_day` | `time` | 当玩家处于该维度时，昼夜时钟经过该刻，`0` 到 `23999`。通过命令或床设置的时钟不计入 |
| `day` | 无，或 `day` | 该维度新的一天开始时；带有 `day` 时，只在那一天触发 |
| `craft` | `item` | 玩家合成该物品时 |
| `pickup` | `item` | 玩家拾取该物品时 |
| `kill` | `entity` | 玩家击杀该实体，或击杀其第 `count` 个时 |
| `respawn` | 无 | 玩家死亡后重生时 |
| `death` | 无 | 玩家死亡时 |
| `y_level` | `below` 或 `above` | 玩家降到该高度以下或升到该高度以上时 |
| `play_time` | `minutes` | 玩家在世界中的时间达到这么多分钟时，从世界介绍关闭时开始计，若没有向他显示介绍则从加入时开始计 |
| `score` | `objective` | 玩家在该计分项中的分数达到 `score` 时 |

生物群系、结构、高度、游戏时长和分数每秒为每个玩家检查一次，在从外到内的变化时触发，加入后的第一次检查绝不触发。受众不是 `player` 的 `time_of_day` 或 `day` 规则，对该维度只触发一次，而不是对其中每个玩家各触发一次。

在玩家仍开着世界介绍时触发的卡片，会等到介绍关闭时再显示，无论其触发器是什么，包括 `command` 触发的。如果玩家在那之前离开，它就会被丢弃。

### 触发器设置

*卡片*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `trigger` | 文本 | 无，必填 | 上面的触发器之一。内置规则不用写 |
| `dimension` | 文本 | 无 | 维度 id，例如 `minecraft:the_nether`；没有命名空间的 id 按 `minecraft:` 读取。对于 `dimension_enter`，它是进入的那个维度；对于其他所有触发器，它把规则限制在该维度中的玩家 |
| `biomes` | 列表 | 无 | 生物群系 id，例如 `minecraft:desert`，或用 `#tag` 表示生物群系标签，例如 `#minecraft:is_ocean` |
| `structures` | 列表 | 无 | 结构 id，例如 `minecraft:village_plains`，或用 `#tag` 表示结构标签，例如 `#minecraft:village`，在玩家位于其中某个部件内时计入；也可以是资源包通过 `structures` 放置的结构的名称，在距其放置位置 `radius` 范围内计入 |
| `radius` | 整数 | `32` | 多近才算位于资源包自己的结构之内 |
| `advancement` | 文本 | 无 | 进度 id |
| `item` | 文本 | 无 | 物品，写法与资源包中其他地方相同，例如 `minecraft:diamond_sword` |
| `entity` | 文本 | 无 | 实体 id，例如 `minecraft:zombie` |
| `count` | 整数 | `1` | 用于 `kill`：需要击杀多少次。规则触发后重新开始计数 |
| `below`、`above` | 整数 | 无 | 用于 `y_level`：要降到其下或升到其上的高度 |
| `time` | 整数 | `0` | 用于 `time_of_day`：一天中的刻 |
| `day` | 整数 | 无 | 用于 `day`：唯一触发的那一天。不写则每天都触发 |
| `minutes` | 整数 | 无 | 用于 `play_time` |
| `objective`、`score` | 文本、整数 | 无、`1` | 用于 `score`：计分项和要达到的值 |
| `requires` | 模组 id 或资源包命名空间的列表 | 无 | 除非全部存在，否则跳过该文件 |

### 时机

*卡片*

`when` 包含的条件必须在触发器触发的那一刻全部成立。

| 设置 | 类型 | 检查内容 |
| --- | --- | --- |
| `biomes` | 列表 | 玩家站在这些生物群系之一中，写法与触发器相同 |
| `timeFrom`、`timeTo` | 整数 | 昼夜时钟位于此时间窗口内，窗口可以跨过午夜，例如 `13000` 到 `1000` |
| `dayAtLeast` | 整数 | 天数至少为此值 |
| `advancement` | 文本 | 玩家拥有该进度 |
| `gameMode` | 文本 | 玩家处于该游戏模式：`survival`、`creative`、`adventure` 或 `spectator` |
| `team` | 文本 | 玩家在该计分板队伍中 |
| `objective`、`scoreAtLeast` | 文本、整数 | 玩家在该计分项中的分数至少为此值 |

### 卡片

*卡片*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `title` | 文本 | 无 | 第一行，在中央卡片上绘制得更大 |
| `lines` | 列表 | 无 | 最多十六行。规则需要标题或行，内置规则除外。`{player}`、`{dim}`、`{biome}` 和 `{day}` 会被填入；`{text}` 是内置消息，单独占一行时会给出它的所有行 |
| `style` | 文本 | `corner` | `corner` 是 `saysCard` 所显示的右下角卡片；`center` 是屏幕中央的卡片；`chat` 是聊天行；`bar` 是动作栏 |
| `icon` | 文本 | `saysIcon` | 绘制在角落卡片上的物品。留空则不绘制 |
| `color` | 文本 | `saysColor` | 卡片的背景色，十六进制 |
| `image` | 文本 | `saysImage` | 来自资源包客户端资源的 PNG，拉伸铺满卡片作为背景 |
| `background` | 布尔值 | `saysBackground` | `false` 去掉面板、边框和彩色条纹；文字保留阴影，`image` 仍会绘制 |
| `font` | 文本 | `saysFont` | 卡片文字所用的字体，写作 `namespace:name`。留空则使用 RDPL 字体 |
| `ticks` | 整数 | `160` | 卡片停留多久，包括淡出 |
| `audience` | 文本 | `player` | 谁能看到：`player`、`everyone`、`dimension`（玩家所在维度中的所有人）或 `team`（玩家的计分板队伍） |
| `repeat` | 文本 | `always` | `always`、`once_per_player`、`once_per_world` 或 `once_per_session`（玩家重新登录后再次显示） |
| `cooldown` | 整数 | `0` | 同一玩家的该规则再次触发前的秒数 |
| `runs` | 文本 | 无 | 规则触发时以该玩家身份运行的函数 |

`saysCard` 关闭时，角落卡片会改走聊天。玩家已被展示过的内容保存在玩家身上，因此死亡和跨维度移动后依然有效；`once_per_world` 则保存在世界中。

RDPL 字体 `resourcedatapackloader:rdpl` 是所有文字的默认字体：卡片、Says 消息、欢迎与停留提示、世界介绍，以及游戏自身的菜单、聊天、HUD 和工具提示。它的粗体和斜体字形分别是 `resourcedatapackloader:rdpl_bold` 和 `resourcedatapackloader:rdpl_italic`。附魔台的文字仍然使用游戏自己的。

RDPL 附带以下字体和字符。卡片、提示或介绍的 `font` 可以用短名称，也可以用完整 id 来指定一个 RDPL 字体：

| 名称 | 绘制内容 |
| --- | --- |
| `rdpl`（或 `resourcedatapackloader:rdpl`） | RDPL 字体，含西里尔字母（U+0400 至 U+04FF）和卢恩字母（U+16A0 至 U+16F8） |
| `rdpl_runic`（或 `resourcedatapackloader:rdpl_runic`） | 一种卢恩密码：字母 A 到 Z 和 a 到 z 绘制为卢恩字符，其他所有字符以 RDPL 字体绘制。粗体片段以 `rdpl_runic_bold` 绘制，斜体片段以 `rdpl_runic_italic` 绘制 |
| 卢恩字符，U+16A0 至 U+16F8 | 直接写成卢恩字符本身（ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ），在 RDPL 字体绘制的任何文字中都可使用，包括聊天；粗体和斜体片段保留其字形 |

卡片字体是位于 `assets/<namespace>/font/<name>.json` 的字体定义，格式与游戏自带的字体相同，卡片的尺寸按该字体的字宽确定。`bitmap` 提供器若其 `file` 为 `<namespace>:font/<name>.png`、其 `chars` 为游戏 `ascii.png` 的十六行，则读取的 PNG 与 1.12.2 版本在 `assets/<namespace>/textures/font/<name>.png` 使用的是同一张，因此同一个资源包能在三个版本上绘制出相同的字样。`minecraft:default` 指代游戏的字体。没有任何资源包提供的字体会回退为游戏的字体，并在 `rdpl.log` 中记录一条警告。

资源包可以通过提供自己的 `assets/resourcedatapackloader/font/rdpl.json`，或其所用的 `assets/resourcedatapackloader/textures/font/` 下的 PNG，来更改 RDPL 字体；两者任一都会在所有地方替换它，包括游戏自身的文字。游戏自身的文字使用 RDPL 字体，是因为本模组附带了 `assets/minecraft/font/default.json`，其中 RDPL 字体排在最前，其余所有字符则落到游戏自己的字体上。资源包的 `assets/minecraft/font/default.json` 先于本模组的被读取，因此原版那份的副本会让游戏的文字恢复其自己的字体；RDPL 自己的文字仍使用 RDPL 字体，除非 `saysFont` 为 `minecraft:default`。

卡片标题和行、Says 消息以及欢迎与停留提示，都支持“世界介绍”中“文本格式”一节表格里的行内标记：粗体、斜体、粗斜体、删除线、代码、链接和卢恩片段。粗体片段以字体的 `_bold` 字形绘制，斜体片段以其 `_italic` 字形绘制；对没有该字形的字体，该片段采用游戏的粗体或斜体样式，卡片按实际绘制的片段确定尺寸。没有安装本模组的玩家会以聊天格式收到相同的标记，卢恩片段则显示为普通字母。

### 内置规则

*卡片*

| Id | 消息 | 其文本来自 |
| --- | --- | --- |
| `rdpl:gate_unlocked` | 传送门开启 | [传送门](#传送门) 中的 `unlockedMessage` |
| `rdpl:gate_blocked` | 关闭的传送门把玩家拦回，显示在动作栏上 | [传送门](#传送门) 中的 `blockedMessage` |
| `rdpl:team_joined` | 玩家加入一个阵营 | 该阵营的 `displayName` |
| `rdpl:team_lead` | 某个阵营的主导权落到一名玩家手中 | [队伍](#队伍) 中的 `leadSays` |
| `rdpl:team_picked` | 玩家被选入一个阵营 | 该阵营的 `displayName` |
| `rdpl:team_round_ended` | 回合结束，玩家被移入一个阵营 | 该阵营的 `displayName` |
| `rdpl:lobby_joins` | 回合中途登录的玩家被送往大厅 | [大厅](#大厅) 中的 `opens.joinsSays` |
| `rdpl:lobby_note` | 屏幕中央的大厅提示行 | [大厅](#大厅) 中的 `opens.says`、`opens.leaderSays` |
| `rdpl:scoring_results` | 回合结束时发给每位玩家的排名 | [结果](#结果) 中的 `results.card`、`results.title`、`results.icon`、`results.image`、`results.background`、`results.seconds` |
| `rdpl:scoring_out` | 被淘汰的玩家 | [回合如何结束](#回合如何结束) 中的 `ends.outSays` |
| `rdpl:reset_lead` | 主导者重置回合 | [重置回合](#重置回合) 中的 `reset.leadSays` |
| `rdpl:reset_vote` | 发起重置投票 | `reset.voteSays` |
| `rdpl:reset_pass` | 投票通过 | `reset.passSays` |
| `rdpl:reset_fail` | 投票未通过 | `reset.failSays` |
| `rdpl:anvil_waits` | 铁砧的操作在等待某个进度 | [铁砧操作](#铁砧操作) |
| `rdpl:threat` | 玩家的威胁等级改变 | `threatSays` |
| `rdpl:prospect` | 勘探发现所报告的每一行 | 该发现 |
| `rdpl:prospect_none` | 勘探一无所获 | 语言文件 |
| `rdpl:pregen_ended` | 预生成完成或停止 | [预生成](#预生成) 中的 `pregenFinishedSays`、`pregenStoppedSays` |
| `rdpl:pregen_running` | 预生成期间加入的玩家看到的进度行 | `pregenRunningSays` |

`welcomeSays` 不是规则，保留其徽标；`first_join` 或 `dimension_enter` 规则是在它的基础上追加。回合中动作栏上的倒计时和统计，仍由其设置决定。

---

# 控制

## 控制层

*控制*

所有会阻止或改变生成的内容都被分成组，每组在配置的 `control` 类别中有一个键，可取三个值：

| 值 | 含义 |
| --- | --- |
| `default` | 由资源包决定。配置值作为后备 |
| `global` | 以配置为准。资源包中的相应部分被忽略 |
| `off` | 该组被完全禁用，任何资源包都无法启用它 |

这些组是 `ores`、`biomes`、`structures`、`spawning`、`bedrock`、`voidWorld`、`recipes`、`terrain`、`replacements`、`villages`、`entities`、`chunks`、`blastPlaster`、`commands` 和 `server`。

设置按 **生物群系部分 → 世界模板 → 配置** 的顺序解析。世界模板的 `settings` 块使用与配置相同的键名，因此资源包设置它们的方式与你自己设置的相同：

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

某组的 control 为 `default` 时，这些设置生效；为 `global` 时被忽略；为 `off` 时，无论任何资源包怎么写，整个组都不起作用。模板中写了但没有任何东西读取的键，会在日志中警告一次，`biomes` 部分中不属于村庄设置的键也一样。

配置文件是 `config/resourcedatapackloader-common.toml`。下面的每个键在其中都使用相同的名称，位于各自的类别下，列表写成游戏配置格式所用的 TOML 列表。

## 各分组的作用

*控制*

下面的每项设置都通过其所在的组读取，因此该组的 `control` 键决定由资源包还是配置拥有最终决定权。资源包可以设置的项，会以相同的名称出现在世界模板的 `settings` 块中；标注为 **仅限配置** 的项只从配置中读取，资源包写了会被警告并忽略。默认值是配置中的默认值。

### 矿石

*各分组的作用*

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

`control.ores` 决定这一组。按模组和矿石类型阻止矿石生成。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `blockOres` | 布尔值 | `false` | 阻止所有模组以及 Minecraft 本身生成矿石。只有 oreWhitelist 中的模组仍会生成。矿石是指基于游戏的矿石或散布矿石特征构建的放置特征，也就是 1.12.2 为其触发矿石事件的那些，涵盖 Minecraft 和大多数模组的矿石、泥土、沙砾和各种石头。资源包自己的世界生成条目绝不会被阻止，无论是被它还是被 oreTypes |
| `logBlockedOres` | 布尔值 | `true` | 每个模组和矿石类型第一次被拒之门外时记入日志 |
| `oreWhitelist` | 模组 id 列表 | `["minecraft"]` | `blockOres` 开启时仍允许生成矿石的模组 |
| `prospectItems` | 列表 | 空 | 潜行的玩家用其破坏方块时，用来勘探矿脉形状的世界生成条目的物品，写作 item=entry\|entry[,以区块计的半径] 或 item=*[,半径]，例如 minecraft:compass=iron_vein\|coal_seam 或 mypack:rod=*,12。读数会给出矿石名称和一个罗盘方位 |
| `prospectItemsAreBlacklist` | 布尔值 | `false` | 开启时，每个物品的列表是它不读取的条目 |
| `prospectDrops` | 布尔值 | `false` | 开启时，在勘探模式下破坏的方块仍会掉落并给予经验。关闭时，样本被消耗掉 |
| `prospectSlow` | 整数，1 到 100 | `2` | 手持带标记物品的潜行玩家破坏一个方块要花多少倍的时间。`1` 为正常速度 |
| `prospectWear` | 整数，2 到 1000 | `2` | 一次勘探破坏对工具造成正常磨损的多少倍。`2`，即两倍，是允许的最小值，没有耐久度的物品不付出代价 |
| `oreTypes` | 列表 | 空 | 本设置适用的矿石类型，无论由谁生成、白名单如何规定。已知类型：COAL、IRON、COPPER、GOLD、REDSTONE、DIAMOND、LAPIS、EMERALD、QUARTZ、DIRT、GRAVEL、DIORITE、GRANITE、ANDESITE、TUFF、CLAY、SILVERFISH，其他任何矿石用 CUSTOM |
| `oreTypesAreBlacklist` | 布尔值 | `true` | 开启时，`oreTypes` 中的类型被阻止。关闭时，只有这些类型会生成 |
| `blockOreDimensions` | 列表 | 空 | 矿石阻止适用的维度，留空表示每一个。范围之外的维度完全不受影响，因此主世界仍被阻止时，其他模组的矿石仍会在那里生成 |
| `blockOreDimensionsAreBlacklist` | 布尔值 | `false` | 开启时，所列维度是被放过的那些 |

### 生物群系

*各分组的作用*

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

`control.biomes` 决定这一组。按模组和名称阻止生物群系，以及用什么来替代它们。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `logBlockedBiomes` | 布尔值 | `true` | 按模组统计并记录哪些生物群系被阻止 |
| `blockBiomes` | 布尔值 | `false` | 阻止除 biomeWhitelist 中的模组之外的所有生物群系生成。被阻止的生物群系变成虚空生物群系，或世界模板的 roles 和 fallback 所指定的生物群系。当世界模板为虚空、没有 roles 且默认为虚空时，阻止所有生物群系会使 voidWorldDimensions 成为虚空世界 |
| `biomeWhitelist` | 模组 id 列表 | `["minecraft"]` | `blockBiomes` 开启时，其生物群系仍会生成的模组。资源包的生物群系使用该资源包的命名空间 |
| `biomeNames` | 列表 | 空 | 本设置适用的生物群系，按 id（如 minecraft:birch_forest）或按游戏显示的名称（如 Birch Forest）。作为黑名单时，无论归谁所有都会被阻止。作为白名单时，所列生物群系在 blockBiomes 开启时仍需要它的模组位于 biomeWhitelist 中 |
| `biomeNamesAreBlacklist` | 布尔值 | `true` | 开启时，`biomeNames` 中的名称被阻止。关闭时，只有这些名称会生成 |
| `blockBiomeDimensions` | 列表 | `["minecraft:overworld"]` | 生物群系阻止适用的维度。留空表示每一个 |
| `blockBiomeDimensionsAreBlacklist` | 布尔值 | `false` | 开启时，阻止会跳过所列维度。关闭时，只对它们生效 |

### 生成器

*各分组的作用*

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

`control.generators` 决定这一组。按模组及其生成的内容，阻止其他模组的世界生成。生成器是一个放置特征，归其 id 的命名空间所有；Minecraft 自己的特征、本模组的特征以及资源包的世界生成条目绝不会被阻止。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `blockWorldGenerators` | 布尔值 | `false` | 阻止所有模组通过其自己的放置特征进行生成，模组就是这样添加史莱姆岛、洞穴水晶之类的东西。只有 generatorWhitelist 中的模组仍会生成 |
| `generatorWhitelist` | 模组 id 列表 | `["minecraft"]` | `blockWorldGenerators` 开启时仍允许生成的模组 |
| `blockedGenerators` | 模组 id 或 id 片段的列表 | 空 | 直接被阻止的个别生成器，无论白名单如何规定，按模组 id 或放置特征 id 的一部分指定 |
| `blockGeneratorDimensions` | 列表 | `["minecraft:overworld"]` | 生成器阻止适用的维度。留空表示每一个 |
| `blockGeneratorDimensionsAreBlacklist` | 布尔值 | `false` | 开启时，阻止会跳过所列维度。关闭时，只对它们生效 |
| `generatorTypes` | 列表 | 空 | 本设置适用的类型，无论生成器归谁所有、白名单如何规定：`ores`、`structures`、`flora`、`lakes`、`terrain`，或 `unknown` 表示都没有匹配的那些。类型取自特征 id 中的词，因此 `crystal_ore` 是 ores，`slime_island` 是 structures |
| `generatorTypesAreBlacklist` | 布尔值 | `true` | 开启时，`generatorTypes` 中的类型被阻止。关闭时，只有这些类型会生成 |
| `generatorTypeMap` | `pattern=type` 的列表 | 空 | 为 id 无法说明其类型的生成器指定类型，pattern 是模组 id 或特征 id 的一部分，例如 mymod=ores。映射条目先于内置的词检查，因此它们也能纠正词语读错了的那些 |
| `logBlockedGenerators` | 布尔值 | `true` | 每个生成器第一次被阻止时，连同赋予它的类型一起记入日志。`/rdplserver generators` 按模组和类型显示累计总数 |

### 替换

*各分组的作用*

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

`control.replacements` 决定这一组。对已经存在的区块进行方块替换。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `blockReplacements` | 列表 | 空 | 区块加载时被换出的方块，写作 block=block，两侧都可带可选的状态，例如 minecraft:andesite=minecraft:stone 或 minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]。每个区块只处理一次，新区块也包括在内 |
| `blockReplacementDimensions` | 列表 | 空 | 本设置适用的维度。留空表示每一个 |
| `blockReplacementDimensionsAreBlacklist` | 布尔值 | `false` | 开启时，替换会跳过所列维度。关闭时，只对它们生效 |
| `blockReplacementMinHeight` | 整数，-2032 到 2031 | `-64` | 查找的最低 y |
| `blockReplacementMaxHeight` | 整数，-2032 到 2031 | `319` | 查找的最高 y |
| `blockReplacementKey` | 字符串 | `0000` | 更改它，每个区块都会重新经历一次替换 |
| `logBlockReplacements` | 布尔值 | `true` | 每次替换第一次发生时记入日志，世界追赶完成时再记一个总数 |

### 村庄与城市

*各分组的作用*

`control.villages` 决定这一组。资源包所铺设的城市和村庄街道：它们的形状、装饰、桥梁、隧道、铁轨、地块和广场。

#### 村庄道路

*村庄与城市*

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

**混合方块。** 有些方块设置接受一个混合，而不是单个方块：方块之间用逗号隔开，每个方块后面跟一个空格和一个权重，如 `"minecraft:stone_bricks 3, minecraft:cobblestone 1"`。没有权重的方块计一次。每放置一个方块，都会根据世界种子和它所在的位置对混合进行抽取，因此同一个世界总会建出相同的图案。接受混合的设置有 `villagePathVergeBlock`、`villagePathVergeWaterBlock`、`villagePathTunnelBlock`、`villagePathBridgeFrameBlock`、`villagePathBridgeFrameTopBlock`、`villageRailTunnelBlock`、`villageRailDeckBlock`、`villageRailSupportBlock`、`villageRailBarrierBlock`、`villageRailBridgeFrameBlock`、`villageRailBridgeFrameTopBlock`、`villageSubwayTunnelBlock`、`villageSubwayPlatformBlock`、`villageSubwayRailingBlock`、`villageSubwayBenchEndBlock` 和 `villageSewerMossBlock`。其他所有方块设置在各处都只使用混合中的第一个方块。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villagePathBlock` | 文本 | 空 | terrainAdaptation 铺设城市道路时的路面。留空则保留生物群系本来会使用的方块：沙子上用砂岩，恶地上用陶瓦，泥土上用土径 |
| `villagePathExtraWidth` | 整数，0 或更大 | `0` | terrainAdaptation 铺设道路时，每侧在通常的 3 格之外额外增加的道路宽度。这会加宽街道本身，因此街道之间的街区会从宽路旁退后 |
| `villageBlockSizes` | `size=weight` 的列表 | 空 | 城市平行街道之间的街区有多深，根据广场位置为每个街区群抽取一次。留空则把每个街区都设为资源包所带最大地块的大小 |
| `villageCitySpacing` | 整数，0 到 256 | `16` | 城市街区群之间的播种间隔，以街区群为单位，街区群的大小由地块决定（最大地块的两倍，加上一个广场和两侧各一条街道，向上取整到 16 格，至少 96）：每个这么多街区群组成的方格中有一个携带城市，为 1 时每个街区群都是城市，即一个中心有井的广场，以及从中伸出、与下一个街区群的街道相接的街道。0 表示不播种任何城市。资源包没有设置它时，由 `structureSpacing` 的 villages=chunks 来设置，至少 9 个区块。每个方格把城市建在它最平坦的街区群上，高低差不超过 10 格，位于村庄生物群系中（由 `structureBiomes` 的 villages= 选择），且不在林地府邸可能起始之处；`structureSeparation`、`structureMinDistanceFromSpawn` 和 `structureMost` 的 villages= 让城市彼此保持距离，就像它们对村庄所做的那样，`structureAt` 的 villages=x,z 则只在它所固定的位置建立城市。城市只会向其第一口井附近范围内、地面起伏不超过 6 格且高于水位线的街区群扩展，地块和井合计不足或仅有两个的城市不会被建造 |
| `villagePathAlleyBlock` | 方块 | 空 | 小巷的路面，小巷是窄到无法容纳线条和人行道的道路。小巷穿行于其所连接街道的人行道之间，自身没有人行道，且在与街道相接处不会画出人行横道。留空则用道路方块铺设小巷 |
| `villagePathAlleyChance` | 整数，0 或更大 | `0` | 一条街道被铺成小巷而不是全宽的百分比概率。0 表示不铺设小巷 |
| `villagePathMinimumWidth` | 整数，0 或更大 | `0` | 允许的最窄街道。会被铺得比这更窄的街道根本不铺，街区群围绕这个缺口布局。0 表示从不拒绝 |
| `villagePathFlatRun` | 整数，0 或更大 | `6` | 街道每保持一个坡度至少这么多格才升降一级，以世界坐标为锚，使各段之间保持一致。0 或 1 允许街道每格升降一次 |
| `villagePlotsLeast` | 整数，0 或更大 | `0` | 城市要扩展到多少个地块：围绕其中心一圈一圈地添加街区群，直到它们至少容纳这么多个，但绝不超过 villagePlotsMost。0 表示只铺设中心街区群 |
| `villagePlotsMost` | 整数，0 或更大 | `0` | 城市最多可以容纳多少个地块：扩展会在将超过它的那个街区群之前停止，任何街区群都不会再安置更多，而达到该数目的街区群会省去没有地块正对着的小巷。0 表示不设上限 |
| `villagePlotsBackRow` | 布尔值 | `true` | 村庄扩展完成后，第二轮会在每个正对着街道的地块正后方安置一个地块，朝向它，使用相同的抽取和相同的空间测试，这样两条街道之间街区的内部会被建满，而不是空着 |
| `villageTieStreets` | 布尔值 | `true` | 开启时，无法把街道扩展到既有村庄的街区群，会铺设一条笔直的连接街道，通向与它对齐的最近的街道。关闭时，这样的街区群会被拆回 |
| `villageLayout` | 文本 | 空 | 取代街区群规划而铺设的城市地图，命名形如 mypack:downtown，从该资源包的 citymaps 文件夹读取。留空则照常规划街区群 |
| `villagePathCenterBlock` | 方块 | 空 | 道路正中的一条中线。留空则不画 |
| `villagePathCenterDash` | 整数，0 或更大 | `0` | 让该线呈虚线：N 格线，然后 1 格路面。以世界坐标为锚，因此一段道路的虚线会延续到下一段。`0` 表示保持实线 |
| `villagePathLineBlock` | 方块 | 空 | 道路与人行道之间的边线。留空则不画 |
| `villagePathSidewalkBlock` | 方块 | 空 | 人行道，与道路齐平，铺在边线之外。留空则不铺 |
| `villagePathSidewalkWidth` | 整数，0 或更大 | `2` | 每条人行道多宽，在设置了 `villagePathSidewalkBlock` 之后生效 |
| `villagePathLampBlock` | 文本 | `minecraft:oak_fence` | 街边灯柱所用的方块，在路缘上堆叠 villagePathLampHeight 高。街道或小巷的每一端各立一根，另一条街道与之相接处立一根，其间每隔 7 到 12 格立一根，立在较低的一侧，只有较低一侧没有空间时才立在另一侧，死胡同的边缘则围成一圈。桥上、隧道中以及距门两格以内都不立。留空则不立灯柱 |
| `villagePathLampHeight` | 整数，1 或更大 | `3` | 灯柱在灯头之前有几格高 |
| `villagePathLampTopBlock` | 方块 | `minecraft:black_wool` | 柱顶的灯头。留空则让它光秃秃的 |
| `villagePathLampSideBlock` | 方块 | `minecraft:torch` | 挂在灯头两侧、朝外的光源。留空则不挂 |
| `villagePathLampStructure` | 文本 | 空 | 作为整根灯柱放置的结构文件，取代堆叠那三种灯柱方块，命名形如 `mypack:street_lamp`，从该资源包的 `structures` 文件夹读取。它以灯位为中心，最低一层位于路缘上，它所放置的方块被锁定，不会被其他东西覆盖。留空则堆叠方块 |
| `villageWellStructure` | 列表 | 空 | 作为每个广场中心装饰放置的结构文件，每行一个带权重的条目，写作 name=weight，如 mypack:plaza_spire=3，每个广场抽取一次。它以一个六格见方、清理并以 villagePathBlock 铺平的区域为中心，其最低一层位于该地面上。留空、空份额，或结构无法加载时，会在那里建造游戏自己的井。不是 name=weight 写法的条目会被忽略 |
| `villagePathDeadEnds` | 列表 | 空 | 断头街道如何封口，每行一个条目，按每个尽头分别抽取：sidewalk 用人行道方块铺满尽头那一排，barrier 沿它立起 villagePathBridgeBarrierBlock，高 villagePathBridgeBarrierHeight；其他任何条目都被忽略。它们只封没有长出死胡同的尽头，没有设置方块的样式会退出抽取，小巷的尽头只采用 barrier。留空则让这类尽头敞开 |
| `villagePathIntersects` | 列表 | 空 | 画在路口的设计，按注册表键命名，取自资源包的 `<namespace>/pathintersects/`。一个条目让每个路口都画得一样；多个条目则按权重为每个路口挑选 |

#### 村庄桥梁与码头

*村庄与城市*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villagePathSupportBlock` | 文本 | 空 | 地面为裸露岩石之处的路面本身，以及水上街道之下的桥墩和桥腿。留空则保留原版的沙砾，沙漠城市用砂岩 |
| `villagePathBridgeBlock` | 文本 | 空 | 街道或码头跨水所用的方块。留空则用村庄木材的木板铺面：热带草原村庄用金合欢，针叶林村庄用云杉，其他地方用橡木 |
| `villagePathBridgeSidewalkBlock` | 方块 | 空 | 道路跨水处人行道的铺面。留空则让正常的人行道方块延伸过去 |
| `villagePathBridgeBarrierBlock` | 方块 | 空 | 沿桥面两侧边缘堆叠的护栏。桥面落在地面上之处不设。留空则不建 |
| `villagePathBridgeBarrierHeight` | 整数，1 或更大 | `1` | 这些护栏有几格高 |
| `villagePathBridgeDrop` | 整数，0 或更大 | `0` | 道路的坡面必须离地多高，其下的落差才会被架桥跨过，而不是被填实。`0` 让道路贴着地面：它们只跨水，别的不跨。`3` 是铁路栈桥所遵循的规则。这会改变坡面，而不只是装饰 |
| `villagePathVergeBlock` | 文本 | 空 | 城市必须造地之处，街道旁和地块下的地面所填的方块：地块之间的沟槽，以及跨越缺口通向街道的填土，该处的街道若是桥，则改用 villagePathBridgeBlock 铺面。留空则跟随其所在的地面：沙子、陶瓦、沙砾，或在本来会是泥土之处用顶部带草的泥土 |
| `villagePathVergeWaterBlock` | 方块 | `minecraft:oak_planks` | 该填土位于水上时变成什么，这样延伸到湖上的路肩就不会是一根泥土柱。它也会装饰留在水上的石质门阶 |
| `villagePathBridgeFrameBlock` | 方块 | 空 | 长桥上方的框架：桥面每侧一根立柱，顶上一根横梁。每个框架都带有一个向下通到桥面下地面的桥墩，其所在那一排不竖灯柱。留空则不建 |
| `villagePathBridgeFrameTopBlock` | 方块 | 空 | 该框架顶上的横梁。留空则使用 `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight` | 整数，1 或更大 | `4` | 框架在桥面上方留出多少格净空，横梁位于其上一格 |
| `villagePathBridgeFrameRun` | 整数，1 或更大 | `24` | 桥长到足以容纳多个框架时，框架间隔多少排。它们围绕桥跨段的中点对称分布 |
| `villagePathBridgeFrameLeast` | 整数，1 或更大 | `24` | 能得到框架的最短桥跨段。更短的桥保持朴素 |
| `villagePathPiers` | 列表 | 空 | 在水上断头的街道所用的码头样式：架桥的尾段变成码头，而不是一座通向虚无的桥。样式有 railed、pilings 和 boardwalk；多个条目则每个码头抽取一个。留空则让这样的尾段保持为普通桥 |
| `villagePathPierCargo` | 列表 | 空 | 摆在码头栏杆内侧的货物，写作 block=weight 条目，block=weight,height 表示堆叠，或 empty=weight 表示留空的份额。方块可以在方括号中带上状态，带朝向的方块会转向码头的中间。不在 1 到 8 之间的高度按一格堆放。其余每一排在每一侧都对列表进行抽取。留空则让码头光秃秃的 |
| `villagePathPierLoot` | 文本 | `resourcedatapackloader:chests/pier_cargo` | 有物品栏的货物方块用来填充的战利品表，在第一次被打开时抽取。资源包可以在该名称下提供自己的战利品表来替换内置的那张。留空则让它们保持为空 |

#### 村庄隧道

*村庄与城市*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villagePathTunnelBlock` | 文本 | 空 | 街道穿山而过、而不是把山劈开之处，用来衬砌街道的方块：隧道两侧的墙以及其上的顶。留空则不凿隧道，让街道爬上山去 |
| `villagePathTunnelDepth` | 整数，1 或更大 | `10` | 路面之上要有多厚的土层，一段路才会被凿穿而不是劈开。埋得这么深、持续十二排或更多的隆起会被保持水平并凿穿，其较浅的引道则被劈开；较短的土包仍像以前一样被劈开。只有在 `villagePathTunnelBlock` 指定了方块之后才起作用 |
| `villagePathTunnelLightBlock` | 方块 | 空 | 沿隧道顶部中线嵌入的光源。留空则不设光 |
| `villagePathTunnelLightRun` | 整数，1 或更大 | `8` | 这些光源相隔多少格。以世界坐标为锚，因此一段道路的光源会延续到下一段；短得够不到其中某个位置的隧道，只在正中点亮一次 |

#### 村庄下水道

*村庄与城市*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageSewerBlock` | 方块名 | 空 | 村庄街道和小巷之下的下水道所用的衬砌方块：它的地面、两侧的墙和顶。留空则不挖下水道 |
| `villageSewerDepth` | 整数，4 或更大 | `8` | 下水道地面位于街道自身路面之下多深。下水道跟随它所在的街道，因此爬升的街道带着爬升的下水道。需要 `villageSewerBlock` |
| `villageSewerHeight` | 整数，2 或更大 | `3` | 步道上方有多少格净空 |
| `villageSewerWidth` | 整数，3 或更大 | `5` | 下水道有多宽，横向计算，包括两侧的墙。偶数会被向上取整，使水道保持居中 |
| `villageSewerWaterBlock` | 方块名 | `minecraft:water` | 填满中间水道的东西。留空则让水道保持干燥 |
| `villageSewerWalkBlock` | 方块名 | 空 | 水道两侧步道的铺面。留空则在衬砌方块上行走 |
| `villageSewerLightBlock` | 方块名 | 空 | 作为光源嵌入水道上方顶部的方块。留空则不设光 |
| `villageSewerLightRun` | 整数，1 或更大 | `8` | 这些光源相隔多少格。以世界坐标为锚，因此一段道路的光源会延续到下一段 |
| `villageSewerLadderBlock` | 文本 | 空 | 检修井竖井用来攀爬的方块，从街道沿竖井一路设置到下水道顶部。留空则让竖井敞开 |
| `villageSewerCoverBlock` | 方块名 | 空 | 盖住检修井的方块，与东西向街道的路面齐平设置，设在街道或小巷与之相接之处，以及该街道穿过下水道环路的广场上。通常选用木活板门：铁活板门接受红石信号而玩家无法徒手打开，这会把下水道对他们关闭。留空则让井口敞开 |
| `villageSewerMossBlock` | 方块名 | 空 | 在衬砌中这里那里混入的第二种方块，例如在普通石头中混入苔石。留空则全程只用一种方块衬砌下水道 |
| `villageSewerMossChance` | 0 到 100 | `25` | 衬砌方块中有百分之几会变成那第二种方块。根据世界种子对每个方块位置抽取，因此同一条下水道总会得到相同的结果 |
| `villageSewerVineBlock` | 方块名 | 空 | 这里那里挂在下水道墙壁内侧的方块，例如藤蔓。它会附着在它所靠的那面墙上。留空则什么也不挂 |
| `villageSewerVineChance` | 0 到 100 | `20` | 墙边的格子中有百分之几挂有它。根据世界种子对每个方块位置抽取，因此同一条下水道总会挂出相同的样子 |
| `villageSewerWellEntrance` | 布尔值 | `true` | 在井周围的广场环下方建一圈下水道环路，每条街道的下水道都穿过它，东西向街道穿过它的每一侧，在广场上设一个检修井通向环路，这样各条下水道便成为一个相连的系统，并在城镇中心有一个入口。关闭时，每条街道的下水道止于井处，广场上没有下去的通道 |

**下水道。** 指定 `villageSewerBlock` 后，会在每条街道和小巷之下挖一条下水道，位于该街道自身路面之下 `villageSewerDepth` 格。它不是独立的网络：它跟随街道，街道通向哪里它就通向哪里，街道爬升它也爬升，十字路口下的两条下水道相遇，因为它们上方的街道相遇；街道或小巷在另一条街道处终止时，它的下水道会继续延伸到那条街道之下与之相接。死胡同和架在桥上的路段没有下水道。其截面为衬砌的地面，中间一条填满 `villageSewerWaterBlock` 的水道，两侧各一条以 `villageSewerWalkBlock` 铺面的步道，`villageSewerHeight` 格的净空和衬砌的顶，宽 `villageSewerWidth`（包括两侧的墙），并且 `villageSewerLightBlock` 每隔 `villageSewerLightRun` 格在水道上方的顶部嵌入一个光源。地铁隧道从下水道的深度范围穿过时，无论在街道之下还是它旁边，下水道会在那里整面封死，那里的任何轨道则保持不动。`villageSewerWellEntrance` 开启时，街道的下水道止于井周围的环路，关闭时则止于井本身。下水道绝不会上升到干扰其上街道的程度，街道与世界底部之间没有空间的路段会被跳过，而不是被硬挤进去。

#### 村庄铁路

*村庄与城市*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageRailLines` | 整数，0 或更大 | `0` | 一座城市有多少条铁路线穿过，在任何街道之前铺设，让城镇围绕它们生长，每条都贯穿整座城市的长度。villageCitySpacing 为 1 时每个街区群都是一座城市，各自带有自己的铁路线。0 表示不铺设 |
| `villageRailSpacing` | 整数，1 或更大 | `48` | 同一座城市中，一条铁路线的路基与下一条之间至少要有多少格空地。1 让它们相隔一格，资源包就是这样建出平行线路的编组场的 |
| `villageRailDirection` | 文本 | `any` | 线路的走向：`ew` 为东西向，`ns` 为南北向，`any` 为每座城市随机抽取。`e`、`w`、`n` 和 `s` 的读法相同 |
| `villageRailWidth` | 整数，3 或更大 | `3` | 路基的最小宽度。`3` 在正中承载一条轨道，`5` 承载两条；要求的轨道数超过其容量的路基，会加宽以容纳它们 |
| `villageRailBlock` | 方块 | 空 | 轨道。留空则铺设原版铁轨，矿车可以在上面行驶；其他任何方块都按原样铺设 |
| `villageRailTrackSeat` | `auto`、`on` 或 `in` | `auto` | 轨道所处的位置。`auto` 把铁轨方块放在路基上，把任何其他方块与路基表面齐平嵌入；`on` 总是把它铺在路基上；`in` 总是把它嵌入路基。嵌入路基的轨道，是资源包用铁块或台阶而非矿车铁轨铺出铁轨外观的方式，此时平交道口会齐平地穿过路面 |
| `villageRailBedBlock` | 方块 | 空 | 轨道下的路基。留空则铺沙砾 |
| `villageRailTieBlock` | 文本 | 空 | 每隔 villageRailTieRun 排横铺在路基上的枕木。留空则铺橡木木板 |
| `villageRailTieRun` | 整数，1 或更大 | `2` | 枕木相隔多少排 |
| `villageRailTracks` | 整数，0 或更大 | `0` | 同一个路基承载多少条轨道，并排且相距 `villageRailTrackGap`。**路基会加宽以容纳它们全部**，因此三条轨道共用一个路基，而不是变成三条线路。`0` 在不足五格宽的路基上铺一条轨道，在更宽的路基上铺两条 |
| `villageRailTrackGap` | 整数，2 或更大 | `2` | 一个路基上的轨道相隔多少格，中心到中心。`2` 是允许的最小值，在它们之间留一格路基，这能防止它们像相邻铁轨那样彼此弯向对方 |
| `villageRailShoulderBlock` | 方块 | 空 | 装饰路基最外侧的几列，即轨道旁的一条养护小路，相当于铁路对道路人行道的回应。留空则不铺 |
| `villageRailShoulderWidth` | 整数，0 或更大 | `1` | 该路肩每侧有多少列宽，加在 `villageRailWidth` 之外。需要 `villageRailShoulderBlock` |
| `villageRailPowerBlock` | 方块 | 空 | 每隔 `villageRailPowerRun` 排嵌入线路的动力轨道。留空则使用原版充能铁轨；不是铁轨的方块只是被直接铺在那里 |
| `villageRailPowerBase` | 方块 | 空 | 动力轨道下用来给它供能的东西。留空则使用红石块 |
| `villageRailPowerRun` | 整数，0 或更大 | `0` | 每隔这么多排，在原版铁轨线路中嵌入一根位于红石块之上的充能铁轨，让矿车保持滚动。`0` 表示不供能，除原版铁轨之外的任何轨道都会忽略它 |
| `villageRailClimb` | 整数，1 或更大 | `8` | 线路每爬升或下降一格，要保持水平行进多少排。`1` 让它的坡度和道路一样陡 |
| `villageRailTail` | 整数，0 或更大 | `48` | 铁路线越过城市最后一个街区群，向两端继续延伸多远 |
| `villageRailSupportBlock` | 文本 | 空 | 线路跨越水面或落差处，栈桥下的立柱方块。留空则使用橡木原木 |
| `villageRailDeckBlock` | 文本 | 空 | 栈桥承载路基的桥面。留空则使用橡木木板 |
| `villageRailBarrierBlock` | 方块 | 空 | 沿栈桥桥面两侧边缘的护栏。留空则不设 |
| `villageRailBridgeFrameBlock` | 方块 | 空 | 长栈桥上方的框架：桥面每侧一根立柱，顶上一根横梁。每个带有框架的那一排，也把它的支撑立柱一直向下立到路基。留空则不建 |
| `villageRailBridgeFrameTopBlock` | 方块 | 空 | 该框架顶上的横梁。留空则使用 `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight` | 整数，2 或更大 | `4` | 框架在桥面上方留出多少格净空，横梁位于其上一格 |
| `villageRailBridgeFrameRun` | 整数，2 或更大 | `24` | 栈桥长到足以容纳多个框架时，框架间隔多少排 |
| `villageRailBridgeFrameLeast` | 整数，2 或更大 | `24` | 能得到框架的最短栈桥。更短的栈桥保持朴素 |
| `villageRailTunnelBlock` | 文本 | 空 | 铁路线穿山而过、而不是爬上山去之处，用来衬砌线路的方块。留空则不凿隧道 |
| `villageRailTunnelDepth` | 整数，1 或更大 | `6` | 路基之上要有多厚的土层，一段路才会被凿穿而不是劈开。需要 `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock` | 方块 | 空 | 沿铁路隧道顶部中线嵌入的光源。留空则不设光 |
| `villageRailTunnelLightRun` | 整数，1 或更大 | `8` | 这些隧道光源相隔多少格，以世界坐标为锚，使各段保持一致 |

**线路的走向。** 线路相互平行，沿 `villageRailDirection` 指定的轴，从城市的第一口井开始依次向外分布，先一侧再另一侧，每条的路基与下一条之间至少保留 `villageRailSpacing` 格的地面。铁路起点避开广场及其周围的地块，凡是会顺着街道铺设之处就滑向一旁；地铁则从井所在的那一排起步，滑向 `villageSubwaySpacing` 之内最近的街道，因此它从道路之下穿行。线路在地块之前铺设，因此没有地块立在露天轨道上，它贯穿整座城市，并在两端越过最后一个街区群再延伸 `villageRailTail`，在距离挡路的其他任何城市还有七格处停下。它所经过的每个街区群都铺设自己的那一段，无论城市是否在那里生长。

**坡度。** 铁路不像街道那样爬坡。它的路基跟随被长距离抹平的地面，且每隔至少 `villageRailClimb` 排才变化一格高度；地铁跟随其下方 `villageSubwayDepth` 处的地面，若某条线路沿线任何地方都无法保持这个深度，在其衬砌所需的、位于世界底部之上的六格空间之内，则根本不铺设。地面落差超过三格或被水覆盖之处，线路走在栈桥上：一个以 `villageRailDeckBlock` 铺成的桥面，轨道在其上或嵌入其中，没有枕木或路肩，桥面两侧边缘之下每四排立 `villageRailSupportBlock` 立柱，每根立柱向下抵达坚实地面，最多 24 格。地面抬升之处，线路被劈开，或者一旦路基之上的土层连续十二排或更多达到 `villageRailTunnelDepth` 深，就用 `villageRailTunnelBlock` 凿穿；只要仍有一格土层作顶，凿穿就继续进行。水在三格之内的露天劈开路段，会用隧道衬砌砌墙，一直砌到水面，路基旁边的地面则在塌落处被垫起。整条线路沿线在路基之上保持四格净空。栈桥从头到尾处于同一高度，其两侧的路基以坡道与该高度相接；在保持栈桥水平与爬升速率相冲突之处，以水平为准，其旁边的坡道可能比 `villageRailClimb` 所说的更早一级一级变化。长度达到 `villageRailBridgeFrameLeast` 排或更多的栈桥，一旦 `villageRailBridgeFrameBlock` 指定了方块，就带有顶部框架，相隔 `villageRailBridgeFrameRun` 排，围绕栈桥中点对称分布，每个带有框架的那一排也带有其支撑立柱。街道与线路交叉的那一排不设框架。

**交叉口。** 街道笔直穿过线路。在交叉口，线路在街道上以及两侧各多一排的范围内保持水平，街道向线路看齐地调整坡度，绝不会反过来，并以其自身的坡度升降到那个高度。路面保持原貌，轨道则高出一格横穿过去，或者在 `villageRailTrackSeat` 把轨道设在路基里时与路面齐平，这样矿车可以穿过街道，村民可以穿过轨道。在高出六格或更多的街道之下凿穿的线路根本不交叉：街道保持它自己的坡度，从隧道上方经过。该高度是依据街道的地面读取的，这个地面被抹平成每排最多爬升一格，先于任何交叉口、井或铁路对它的约束。

**门阶。** 地面塌落处，每个地块的每扇门前的台阶会被用土垫起，而留在水上的石质台阶则用 `villagePathVergeWaterBlock` 装饰。

**轨道。** `villageRailBlock` 为空时，轨道是顺着线路方向转好的原版铁轨，`villageRailPowerRun` 每隔若干排在一个红石块之上设置一根已通电的充能铁轨，让矿车能骑行整条线路；嵌入路基的轨道不带充能铁轨。想要铁块、铁栏杆或其他任何东西的资源包，直接指定它们即可：带轴的方块，例如原木，会顺着线路方向转向，其他方块则按原样铺设。每个铁路、地铁、车站和下水道方块都可以在方括号中携带其状态，上面的“混合方块”列出了按方块逐个抽取加权混合的那些设置。

#### 村庄地铁

*村庄与城市*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageSubwayLines` | 整数，0 或更大 | `0` | 一座城市挖多少条地下铁路线。0 表示不挖，也不抽取任何东西，因此城市的布局与没有它们时完全一样 |
| `villageSubwayDepth` | 整数，6 或更大 | `24` | 路基位于地表之下多深。线路依据其上方的地面确定坡度，因此它在该深度跟随地势起伏，而不是水平延伸 |
| `villageSubwaySpacing` | 整数，1 或更大 | `64` | 一座城市的地铁线路彼此保持多远 |
| `villageSubwayDirection` | 文本 | `any` | 地铁线路的走向：ew 为东西向，ns 为南北向，any 为每座城市随机抽取 |
| `villageSubwayWidth` | 整数，3 或更大 | `3` | 路基有多宽，不含路肩 |
| `villageSubwayBlock` | 方块 | 空 | 轨道方块。留空则铺原版铁轨 |
| `villageSubwayTrackSeat` | 字符串 | `auto` | 轨道是放在路基上、嵌入路基中，还是用 `auto` 由方块自己决定 |
| `villageSubwayBedBlock` | 方块 | 空 | 路基所用的方块。留空则使用沙砾 |
| `villageSubwayTieBlock` | 方块 | 空 | 横铺在路基上作为枕木的方块。留空则使用木板 |
| `villageSubwayTieRun` | 整数，1 或更大 | `2` | 枕木相隔多少格 |
| `villageSubwayTracks` | 整数，0 或更大 | `0` | 路基承载多少条平行轨道。0 表示在宽度允许的范围内尽可能多 |
| `villageSubwayTrackGap` | 整数，2 或更大 | `2` | 平行轨道相距多远 |
| `villageSubwayShoulderBlock` | 方块 | 空 | 路基两侧的方块。留空则没有路肩 |
| `villageSubwayShoulderWidth` | 整数，0 或更大 | `1` | 该路肩有多宽 |
| `villageSubwayPowerBlock` | 方块 | 空 | 动力轨道方块。留空则使用原版充能铁轨 |
| `villageSubwayPowerBase` | 方块 | 空 | 放在动力轨道下用来驱动它的方块。留空则使用红石块 |
| `villageSubwayPowerRun` | 整数，0 或更大 | `0` | 动力轨道相隔多少格。0 表示不铺 |
| `villageSubwayTunnelBlock` | 文本 | 空 | 衬砌隧道的方块：两侧的墙和其上的顶。留空则挖出隧道及其车站而不衬砌 |
| `villageSubwayTunnelLightBlock` | 方块 | 空 | 作为光源嵌入隧道顶部的方块。留空则不设光 |
| `villageSubwayTunnelLightRun` | 整数，1 或更大 | `8` | 这些光源相隔多少格，以世界坐标为锚，使各段保持一致 |
| `villageSubwayClimb` | 整数，1 或更大 | `8` | 线路行进多少格后才可以升降一格 |
| `villageSubwayTail` | 整数，0 或更大 | `48` | 线路越过城市自身的部件后再延伸多远才停止 |
| `villageSubwaySurfaces` | 整数，0 到 100 | `25` | 一条地铁线路在某一端爬升到地表，并从那里作为普通铁路继续延伸的百分之几概率，身后是隧道，前方是露天轨道。爬升每升一格需要 villageSubwayClimb 排，所以深处的线路要花很长一段才能升上来。0 让每条地铁在整个长度上都埋在地下 |

**爬出地面。** `villageSubwaySurfaces` 是一条线路不从头到尾保持埋于地下，而是在某一端爬升到地表并从那里作为普通铁路继续延伸的百分之几概率：身后是隧道，前方是露天轨道。爬升遵循 `villageSubwayClimb`，每隔那么多排升一格，因此深 `villageSubwayDepth` 的线路仅坡道就要花 depth 乘以 climb 排，且在它之外还需要相当长的一段，才配得上这个名称；没有空间容纳两者的线路则只是留在地下。爬升的起点不会比线路所穿行街道的远端更近，因此它是从城市街道的另一头冒出来，而不是从街道中间穿出，在升起的那一段上的地块会为它让路。车站在爬升确定之后才被占用，并避开坡道。`villageSubwayTunnelBlock` 为空时，隧道及其车站和楼梯都不衬砌地开挖。

#### 地铁站

*村庄与城市*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | 整数，0 或更大 | `0` | 车站站厅有多少格长，以线路最靠近井的那一排为中心。0 表示完全不建车站 |
| `villageSubwayStationRun` | 整数，0 或更大 | `0` | 沿线路，除了最靠近城市第一口井的那座之外，其余车站相隔多少格。0 表示只在井处建那一座 |
| `villageSubwayPlatformWidth` | 整数，0 或更大 | `3` | 站厅在路基两侧各向外扩开多远，用来做站台 |
| `villageSubwayPlatformBlock` | 方块 | 空 | 站台所铺的地面方块。留空则用隧道衬砌铺地 |
| `villageSubwayRailingBlock` | 方块 | `minecraft:iron_bars` | 围在车站楼梯口、通向街道处的栏杆方块，免得有人走进井里。留空则让楼梯口不设栏杆 |
| `villageSubwayBenchBlock` | 方块 | `minecraft:oak_stairs` | 设在车站站台上和楼梯口旁的长椅座面。楼梯方块转向背离线路，看上去就是长椅；任何方块都行。留空则不设长椅 |
| `villageSubwayBenchEndBlock` | 方块 | `minecraft:oak_log` | 车站长椅两端的扶手。留空则让座面两头光秃秃的 |
| `villageSubwayBenchLength` | 整数，0 到 32 | `5` | 车站长椅有多长，包括扶手。`0` 表示不设长椅 |
| `villageSubwayStation` | 文本 | 空 | 取自资源包 structures 文件夹、用作车站的结构：它的竖井、楼梯以及从街道进入的通道。用 #scripts/rdpl-grab-template.py 从手工建造的世界中取出一个：其实心格会被铺设，其空气格会被挖空，因此形状就是建筑本身，而不是对它的描述。留空则完全不建车站，无法加载的名称会记录一条错误并且不建任何车站 |
| `villageSubwayStationFoot` | 整数，0 到 64 | `4` | 车站建筑底部有多少层只铺一次，在重复的部分之前。地面和通向站台的门洞位于这里 |
| `villageSubwayStationRepeat` | 整数，0 到 64 | `12` | 车站建筑有多少层会重复，这样一座建筑就能适用于任何深度：竖井以这一段的整份副本增长，走廊吸收剩下的部分。它必须恰好是楼梯的整整一圈，否则各段楼梯无法衔接。`0` 表示建筑绝不增长 |

**车站。** 只有当 `villageSubwayStation` 指定了一个能加载的建筑时，地铁线路才会有车站：留空时没有站厅、没有楼梯，也没有入口，无法加载的名称会记录一条错误并且不建任何车站。指定了建筑之后，一旦设置了 `villageSubwayStationLength` 和 `villageSubwayPlatformWidth`，线路会在城市第一口井所在的那一排设一座车站，并沿线每隔 `villageSubwayStationRun` 格再设一座。每座车站最多向两侧各滑动 48 格，为其建筑寻找一处位置：紧邻一条在该位置的整个长度上都与线路并行的街道，避开所有街道、井和广场，且与已占用的车站至少相距车站长度再加七格；站在该位置上的地块会让路，找不到这样位置的车站会被舍去。不设车站的线路不开站厅，因此绝不会有一个没有入口的站厅。站厅沿其长度保持水平：路基向两侧各扩开 `villageSubwayPlatformWidth`，以 `villageSubwayPlatformBlock` 铺地，以隧道衬砌砌墙并加顶，由隧道自己的 `villageSubwayTunnelLightBlock` 和 `villageSubwayTunnelLightRun` 照明，并在两端把隧道横向封死。从站台起有一条走廊通向车站建筑，建筑爬升到道路旁边的街道，绝不在它下面；建筑升到八格内最近街道的坡度上，也就是该街道在地面上保持的坡度，而不是架在其上的任何桥面或坡道，在没有街道的地方则升到地面的高度，当城市建造时该高度比站台高出不到三格、建筑即使增长也无法完成爬升、或通往站台的走廊会延伸超过 32 格时，它被舍去；日志会说明原因。那条街道与建筑之间的地面被调整到同一高度，塌落处被填平，头顶被清空，这样人们就能从道路走进车站。一张以 `villageSubwayBenchEndBlock` 为扶手、长 `villageSubwayBenchLength` 的 `villageSubwayBenchBlock` 长椅立在站台上。

**手工建造车站。** `villageSubwayStation` 指定一个用作车站的结构文件，资源包就是这样提供某人亲手建造的形状，而不是在设置中描述出来的形状。在世界中建好它，用 #scripts/rdpl-grab-template.py 取出，并随资源包放置：其方块按所建的样子铺设，海绵格变成隧道衬砌，其空气格被挖空，站在其中的任何东西，如矿车或盔甲架，也会随之而来。它从车站所占位置的角上安置。一座建筑能适用于任何深度，是因为它的中间部分会重复：底部先铺一次 `villageSubwayStationFoot` 层，带有地面和通向站台的门洞，然后接下来 `villageSubwayStationRepeat` 层的整份副本向上堆叠，直到建筑到达街道。那一段必须恰好是楼梯的整整一圈，否则两份副本相接之处，各段楼梯无法衔接。一条两格高的走廊从门洞通向站台，在落差太长、无法直行之处沿站厅折返；建筑头顶的地面向上清空八格，街道层的开口周围围一圈 `villageSubwayRailingBlock` 的栏杆，旁边立一张长椅。无法加载的建筑会让任何地方都不建车站并记录一条错误；其建筑无法升到街道的车站，在城市规划时被舍去，没有站厅，并记录原因。建筑自带通向街道的出口。

#### 铁路连接

*村庄与城市*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageRailLinks` | true/false | `false` | 连接相邻的城市，这些城市的第一条线路隔着一道接缝彼此相对。需要 `villageRailLines`，或在没有地面线路的资源包中需要 `villageSubwayLines` |
| `villageRailLinkLeast` | 整数，0 或更大 | `128` | 铺设的最短连接，支线加干线再加支线，以格计 |
| `villageRailLinkMost` | 整数，0 或更大 | `1024` | 铺设的最长连接，支线加干线再加支线，以格计 |
| `villageRailLinkBridgeMost` | 整数，0 或更大 | `96` | 一条连接可能需要的最长桥梁。需要跨越更宽的水面或更深的落差的连接不会被铺设 |
| `villageRailLinkTunnelMost` | 整数，0 或更大 | `192` | 在 `villageRailTunnelBlock` 凿隧道之处，一条连接可能需要的最长隧道。需要凿得更长的连接不会被铺设 |
| `villageRailLinkStation` | 文本 | `both` | 每条支线在接入干线之前的车站：`both` 在线路两侧各铺一个站台，`one` 在驶向干线的列车左侧铺单个站台，`none` 则不建 |
| `villageRailLinkStationLength` | 整数，0 或更大 | `16` | 车站站台有多少排长。`0` 表示不建车站 |
| `villageRailLinkPlatformWidth` | 整数，0 或更大 | `3` | 每个站台有多少格宽 |
| `villageRailLinkPlatformBlock` | 方块 | 空 | 站台所用的建材方块。留空则使用石砖 |

**什么是连接。** 铁路连接把相邻的城市连成一个网络。城市按城市网格（`villageCitySpacing`）每格一座地建立，连接沿着两个格子之间的接缝延伸：每座城市的第一条线路越过其尾段，作为支线笔直伸向接缝，并与沿接缝垂直铺设的干线相接。干线从一条支线延伸到另一条，绝不越过任何一条。它需要 `villageRailLines`，或在没有地面线路的资源包中需要 `villageSubwayLines`，并且默认关闭。

**哪些城市会连接。** 两座城市只有在位于相邻的格子中、其第一条线路沿跨越它们之间接缝的轴线延伸，且整条连接沿轨道从井到井测得的长度介于 `villageRailLinkLeast` 和 `villageRailLinkMost` 格之间时才会连接。决定的每个环节都由种子和两座城市的选址算出，因此无论先生成哪座城市或哪个区块，结果都相同。无法完整建成的连接根本不铺设，绝不会只建一半：包括需要比设置所允许的更长的桥或隧道、越过世界边界、撞上林地府邸、让两座城市靠得比 `structureSeparation` 所允许的更近，或使接合处离格子的角太近的那些。干线只会朝着真正建立起来的城市铺设：当 `structureMost` 之类的上限拦住了邻居，或它长得太小而保不住时，干线的两半和越过该城市自身尾段的支线都不会建造。固定的城市以同样的方式连接，每格一座；含有两个固定点的格子则两边都不连。其他城市在生长时会避开连接的支线和干线，就像它们彼此避开一样。

**坡度。** 支线和干线都是铁路线，其坡度、架桥、凿隧道和交叉的处理与城市线路完全相同，用的是 `villageRailClimb` 以及上面的栈桥和隧道设置。支线与干线相接之处，两者都保持水平，旁边的车站也是如此。

**路口。** 支线只接入干线的近侧轨道。该轨道在支线中线与之相遇之处被截断，支线的左轨向左弯入其中，右轨向右弯入，远侧的轨道则笔直穿过。以两条轨道为例，干线在上方，支线从下方而来：

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` 是路基，`o` 是轨道。两条支线会在几格之内相继到达之处，第二座城市的第一条线路会移过去与第一条对齐，两者改在一个十字路口相会：每条支线只汇入它自己的近侧轨道，与上述完全相同，干线的两条轨道都在支线中心被截断，且没有任何铁轨穿越另一条：

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

单轨干线没有第二条轨道可以交给另一条支线，因此两条支线会在单轨上迎头相遇的连接不会被铺设。在单轨的情况下，支线的轨道向左弯入干线轨道，该弯道之外的干线轨道则止于它。这些弯道的形状是固定设置的，因此原版铁轨只在路口所画之处转弯，别处不转。

**车站。** 支线在路口之前的最后几排是一座车站：由 `villageRailLinkPlatformBlock` 铺成、与铁轨齐平的站台，外侧边缘以 `villageSubwayRailingBlock` 设栏杆，每个站台的中途有一张 `villageSubwayBenchBlock` 的长椅。

**地铁。** 在只有地铁线路的资源包中，连接承载的是城市的第一条地铁线路。线路从地下爬向干线，坡道长 `villageSubwayDepth` 乘以 `villageSubwayClimb` 排，在地表到达车站和路口；这种城市只在一侧连接，即连接较短的那一侧，干线是依据 `villageRail` 设置建成的地面铁路。支线没有空间容纳那段坡道及其车站之处，干线改为下到地铁：整条连接，支线和干线，都留在 `villageSubwayDepth` 的地下，依据 `villageSubway` 设置建成，并在同样的路口相会，不设车站。

#### 村庄装饰

*村庄与城市*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageDecor` | 列表 | 空 | 散布在城市街道旁的装饰，写作 name=weight 对，名称指向资源包提供的世界生成，如 mypack:street_flowers=2。名称 empty 表示留空的位置所占份额，不是 name=weight 写法的条目会被忽略。街道每侧路肩上每隔三格就对列表抽取一次，落在那里的地面上，无论它多高，但不在隧道中、地块下、广场上，或距门两格以内。留空则什么也不散布 |

### 结构

*各分组的作用*

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

`control.structures` 决定此分组是否生效。包括被关闭的原版结构，以及它们的间距、间隔、出生点距离、生物群系、生成物、固定位置和地形适配。

| 设置项 | 类型 | 默认值 | 作用 |
| ------------------------------- | --------------------------------------------- | ------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureSpacing`              | 列表                                          | 空                                                                     | 原版结构的播种间距（以区块为单位），写成 structure=chunks 条目：可用 1.12.2 的名称 temples、monuments、mansions、mineshafts、strongholds、netherbridges、endcities 和 villages，也可用任意结构集 id，例如 pillager_outposts。对于 mineshafts，该数值表示每这么多个区块出现一个；对于 strongholds，则是环的间距。下界要塞沿用自己的网格，netherbridges 间距对其无效 |
| `structureSeparation`           | 列表                                          | 空                                                                     | 同一种结构两个实例之间允许的最小距离（以区块为单位），写成 structure=chunks 条目；对于 strongholds，则是环的分布范围。Temples、mineshafts 和 netherbridges 沿用自己的间隔，此项对其无效。对于 monuments，若间隔大到等于间距，会被降为间距减一，日志中会注明 |
| `structureMost`                 | 列表                                          | 空                                                                     | 一个维度最多可容纳的村庄数量，写成 villages=count（例如 villages=100）；其他结构不设上限：一旦已建立这么多个，就不会再有区块建立新的，用 structureAt 固定的区块除外。0 或没有该条目表示不设上限 |
| `structureSpawners`             | `structure=entity` 列表                       | 空                                                                     | 原版结构内部的刷怪笼生成什么，用逗号分隔则每个刷怪笼随机选一个。会放置刷怪笼的有四种：dungeons、mineshafts、下界要塞和 strongholds |
| `structureMinDistanceFromSpawn` | 列表                                          | 空                                                                     | 结构的起点距世界出生点至少多远（以方块为单位），写成 structure=blocks 条目。从世界出生点测量；新世界仍在选择出生点时，若资源包设置了 worldSpawn 则从它测量，否则从世界原点测量 |
| `structureBiomes`               | 列表                                          | 空                                                                     | 结构可以在哪里生成，写成 structure=biome,biome 条目，可填生物群系 id、游戏中显示的名称（如 Birch Forest）、不带命名空间的原版名称（如 desert），或生物群系类型（如 SANDY） |
| `structureBiomesAreBlacklist`   | `structure=true` 或 `structure=false` 列表    | 空                                                                     | 每个结构的生物群系列表的方向 |
| `structureSpawns`               | 列表                                          | 空                                                                     | 无论生物群系如何规定，结构都会生成的生物，写成 structure=namespace:entity:weight:least:most 条目，用逗号分隔。该列表会整体替换结构自带的生物列表，不论其中每种生物属于哪一类；= 后面为空列表则什么都不生成 |
| `structureAt`                   | `structure=x,z` 列表                          | 空                                                                     | 将结构固定在精确的位置。参见[位于精确位置的结构](#位于精确位置的结构) |
| `structureAdaptation`           | 列表                                          | mansions 为 `beard_thin`；其他结构保持其原版适配 | 地形如何适配结构，写成 structure=mode 条目，模式有 none、bury、beard_thin、beard_box 和 encapsulate |
| `terrainAdaptation`             | 布尔值                                        | `false`                                                                   | 铺设 RDPL 自己的城市街道，使其嵌入地形，而不是在每处凹陷上方架在支柱上，并同时读取 villagePath 和 villageRail 选项。这会改变地形，因此开启后创建的世界与未开启时不同。城市按 villageCitySpacing 的设定播种，设为 0 则没有城市 |
| `villagePieces`                 | 列表                                          | 空                                                                     | 在此处指定的村庄地块，每行一个，可写 villages 文件的完整 id（如 mypack:smithy）、其简名，或模板地块所建造的结构的名称。当 villagePiecesAreBlacklist 开启时，此处列出的结构在游戏加载它的任何地方也会被留空，包括游戏自带的村庄房屋，例如 minecraft:village/plains/houses/plains_small_house_1 |
| `villagePiecesAreBlacklist`     | 布尔值                                        | `true`                                                                    | 开启时，villagePieces 中的地块被禁止建造。关闭时，只建造这些地块 |
| `villageBlocks`                 | 列表                                          | 空                                                                     | 建造村庄地块所用的方块，写成 original=replacement 对，如 minecraft:cobblestone=mypack:ruby_brick。两侧都可以带方括号中的状态，此时原方块必须与之完全匹配。一对还可以添加百分之几的几率，如 minecraft:cobblestone=minecraft:mossy_cobblestone,20，几率在放置该方块处依据世界种子计算；at=block 表示仅在该方块所在处生效，under=block 表示仅在其上方生效。没有几率或条件的对最先应用，因此带条件的对可以对它们的结果做风化处理。它作用于农田和城市所建造的游戏自带村庄房屋；街道、水井、灯和你的模板地块中的结构永远不受影响。留空则每个方块保持放置时的样子 |

### 生成

*各分组的作用*

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

`control.spawning` 决定此分组是否生效。包括生物生成上限、敌对生物的生成速率和光照上限。

| 设置项 | 类型 | 默认值 | 作用 |
| ----------------------------- | ---------------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `surfaceDayMonsterRate`       | 数值，0.0 至 4.0       | `1.0`   | 白天地表敌对生物生成的倍率，`1.0` 即原版，因此可以关闭白天地表的生成而不影响洞穴 |
| `surfaceNightMonsterRate`     | 数值，0.0 至 4.0       | `1.0`   | 同上，针对夜晚的地表 |
| `undergroundDayMonsterRate`   | 数值，0.0 至 4.0       | `1.0`   | 同上，针对白天的地下 |
| `undergroundNightMonsterRate` | 数值，0.0 至 4.0       | `1.0`   | 同上，针对夜晚的地下 |
| `monsterCap`                  | 整数，-1 至 1000       | `-1`    | 同时可加载的敌对生物数量。原版为 70，`-1` 表示不改动 |
| `creatureCap`                 | 整数，-1 至 1000       | `-1`    | 同上，针对被动动物。原版为 10 |
| `ambientCap`                  | 整数，-1 至 1000       | `-1`    | 同上，针对蝙蝠之类。原版为 15 |
| `waterCreatureCap`            | 整数，-1 至 1000       | `-1`    | 同上，针对鱿鱼。原版为 5 |
| `monsterSpawnLight`           | 整数，-1 至 15         | `-1`    | 在原版检查之外，敌对生物仍可生成的最高方块亮度。-1 表示只保留原版规则。刷怪笼不受影响 |
| `threatItems`                 | 列表                   | 空      | 会提高玩家威胁等级的物品，写成 item=level,count 条目，末尾可选加 ,each 或 ,batch，例如 minecraft:diamond_sword=5,1 或 minecraft:diamond=1,16,batch。默认的 each 对持有的每一个都加上该等级，最多计入 count 个；batch 则每持有 count 个加一次该等级。超过物品堆叠上限的 count 会被截为堆叠上限。每个持有物品的已加载实体都是携带者：玩家的主物品栏、盔甲和副手，掉落的物品堆，任何带有物品栏的实体（如驮箱骡或运输箱矿车），以及其他生物手持的物品和穿戴的盔甲。留空则关闭威胁等级。即使 `control.spawning` 为 `off`，配置自身的威胁设置依然生效 |
| `threatLevels`                | 列表                   | 空      | 进入各个等级带所需的分数，递增排列，因此 `10, 25, 50` 构成三个等级带。留空则关闭威胁等级 |
| `threatMost`                  | 整数，-1 至 100000     | `-1`    | 为分数设上限。`-1` 表示不设上限 |
| `threatSpawnRate`             | 数值，0.0 至 8.0       | `1.0`   | 在处于最高等级带的携带者 128 格范围内，在其他倍率之外再缩放敌对生物的生成，较低的等级带按比例获得一部分 |
| `threatNotice`                | 数值，0.0 至 64.0      | `0.0`   | 敌对生物（包括原版生物）能在多远的额外距离外发现处于最高等级带的携带者，同样按比例分配给较低的等级带 |
| `threatSays`                  | `band=message` 列表    | 空      | 当玩家自己所在的等级带变化时以黄色显示的文字，等级带 `0` 是回落到第一个等级带以下时的文字 |

### 基岩

*各分组的作用*

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

`control.bedrock` 决定此分组是否生效。包括平坦基岩及其维度和生物群系列表。

| 设置项 | 类型 | 默认值 | 作用 |
| ----------------------------------- | ------------------------- | ---------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `flatBedrock`                       | 布尔值                    | `false`                                                                                  | 将世界底部参差不齐的基岩替换为平坦的层。仅对新区块生效，除非开启 `flatBedrockRetrogen` |
| `flatBedrockDimensions`             | 列表                      | `["minecraft:overworld"]`                                                                | 要平整的维度。留空表示所有维度 |
| `flatBedrockDimensionsAreBlacklist` | 布尔值                    | `false`                                                                                  | 开启时，平整会跳过列出的维度。关闭时，只对它们生效 |
| `bedrockLayers`                     | 整数，1 至 5              | `1`                                                                                      | 保留多少层基岩 |
| `flatBedrockBiomes`                 | 生物群系名称列表          | 空                                                                                    | 要平整的生物群系，可用显示名称或注册名称。留空表示所有生物群系 |
| `flatBedrockBiomesAreBlacklist`     | 布尔值                    | `false`                                                                                  | 开启时，平整会跳过列出的生物群系。关闭时，只对它们生效 |
| `flatBedrockRoof`                   | 布尔值                    | `false`                                                                                  | 在维度有基岩顶盖的地方（如下界顶部）也将其平整 |
| `flatBedrockFiller`                 | 方块                      | 空                                                                                    | 替换被移除基岩的方块。留空则按维度选择：石头、下界岩、末地石 |
| `flatBedrockFillers`                | `dimension=block` 列表    | `["minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"]` | 按维度指定的填充方块，对所列维度覆盖 `flatBedrockFiller` |
| `flatBedrockBiomeTypes`             | 列表                      | 空                                                                                    | 要平整基岩的生物群系类型，与 flatBedrockBiomes 并用，可用生物群系标签（如 minecraft:is_ocean）或 1.12.2 的类型名称（如 OCEAN）。flatBedrockBiomesAreBlacklist 同样适用于它们 |
| `flatBedrockRetrogen`               | 布尔值                    | `false`                                                                                  | 也平整已存在区块中的基岩。每个区块只处理一次并记住这一点，且无法撤销：原来的形态没有记录在任何地方 |
| `flatBedrockRetrogenKey`            | 文本                      | `0000`                                                                                   | 修改它可使每个区块再次有资格进行基岩平整 |

### 远处缓慢刻更新

*各分组的作用*

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

`control.entities` 决定此分组是否生效。远离所有玩家的实体以较慢的节奏更新。

| 设置项 | 类型 | 默认值 | 作用 |
| --------------------- | --------------- | ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `slowDistantEntities` | 布尔值          | `true`                    | 对远离所有玩家的实体减少刻更新的频率。任何东西都不会完全停止更新，只是节奏变慢 |
| `slowedKinds`         | 列表            | `["items", "experience"]` | 哪些种类会被减少刻更新：items、experience、projectiles，最后一项指箭、三叉戟、投掷的雪球、鸡蛋、药水、附魔之瓶和末影珍珠，以及羊驼的唾沫。凡是能自行思考的实体总是会被减慢，无需在此列出：它们选择下一步行动的频率降低，但仍然每刻移动。机器永远不会被减慢 |
| `slowDistance`        | 整数，64 至 4096 | `192`                    | 区块距最近玩家多远（以方块为单位）才会被减慢。游戏在 64 格以外就不再向玩家通报大多数实体，因此不要低于这个值 |
| `slowRate`            | 整数，1 至 20   | `4`                       | 被减慢的区块每这么多刻获得一刻更新。1 表示完全不减慢，20 表示每秒一次 |
| `neverSlowed`         | 列表            | 空                     | 无论多远都不受影响的实体，写成 namespace:name |
| `slowRecheck`         | 整数，1 至 100  | `20`                      | 每隔多少刻重新计算一次到最近玩家的距离。每位玩家各自独立计算，因此独自待在远处的人周围依然有属于自己的安静空间 |

### 陆地、据点与模组的提示

*各分组的作用*

`control.chunks` 决定此分组是否生效。包括出生点区块半径、预生成、回溯生成与重置、欢迎文字、提示卡片以及游戏的 toast 通知。

| 设置项 | 类型 | 默认值 | 作用 |
| ----------------------------- | ----------------- | ---------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `retrogen`                    | 布尔值            | `false`                                  | 让已存在的区块补上带有 \"retrogen\": true 的世界生成条目。关闭时，已存在的区块保持不变。无论开关，区块在生成时都会被标记，因此之后再开启只会影响比资源包更早的区块 |
| `adoptExistingChunks`         | 布尔值            | `false`                                  | 将已存在的区块视作由此资源包生成，对其做标记，而不是留给回溯生成处理。替换一个已生成同样矿石的模组时开启它，这样回溯生成就不会让矿石翻倍。之后新增的世界生成条目仍会回溯生成到这些区块中 |
| `saysCard`                    | 布尔值            | `false`                                  | 将此模组说的话，即欢迎语、中途加入的玩家收到的造陆说明、运行结束的提示（运行进度仍显示在动作栏），以及威胁提示，显示为右下角的卡片，而不是聊天消息。卡片滑入，停留八秒后淡出，打开界面时也会显示在其上方 |
| `saysIcon`                    | 文本              | 空                                    | 绘制在卡片上的物品，例如 minecraft:compass。留空则不绘制 |
| `saysColor`                   | 文本              | 空                                    | 卡片的背景颜色，十六进制，例如 1E2630。留空则使用深石板色 |
| `saysImage`                   | 文本              | 空                                    | 来自资源包客户端资源的 PNG，拉伸后作为卡片背景，例如 rubyworld:textures/gui/card.png，绘制在颜色之上。留空则不绘制 |
| `saysBackground`              | 布尔值            | `true`                                   | 绘制卡片的面板、边框和彩色条纹，以及欢迎语和据点提示后方的深色背景。关闭后只剩文字（保留其阴影），若设置了 saysImage 则还有该图像 |
| `saysFont`                    | 文本              | 空                                    | 卡片文字所用的字体，写成 namespace:name，例如 rubyworld:runes 对应资源包的 assets/rubyworld/font/runes.json。留空则使用 RDPL 字体 |
| `toasts`                      | 布尔值            | `false`                                  | 显示游戏的 toast 通知，即右上角用于进度、解锁配方、教程提示和系统通知的弹窗，其他模组的 toast 也包括在内。关闭则一律不显示。在下一次加入世界或服务器时生效。世界模板可以改为列出要显示的种类 |
| `pregenOnNewWorld`            | 整数，0 至 8192   | `0`                                      | 在任何人开始游玩之前，新世界在出生点周围多大范围内（以区块为单位）完成造陆。游戏自己会在出生点周围生成 12 个区块，因此 12 是下限，0 表示这个下限而不是什么都不做：游戏本来就要生成的地面会在一次有组织的流程中被接管并完成光照，而不是零零散散地陆续出现。调高它可以比游戏覆盖得更远 |
| `pregenToBorder`              | 布尔值            | `false`                                  | 新世界是否将陆地一直生成到世界边界，而不是固定的区块数，并以边界为中心而不是出生点。边界从未向内收缩过的世界没有可到达的边界，会被跳过 |
| `pregenAllDimensions`         | 布尔值            | `false`                                  | 为服务器拥有的每个维度（包括模组维度）造陆，先主世界，其余按 id 顺序，而不是只处理 pregenDimensions 中的维度。在 pregenDimensionsWhenEntered 中列出的维度仍留给第一位到访者 |
| `pregenResume`                | 布尔值            | `false`                                  | 被停止或中途中断的运行，下次加载世界时是否从中断处继续，而不是重新开始 |
| `pregenChunksInFlight`        | 整数，1 至 512    | `32`                                     | 一次造陆运行同时向游戏请求多少个区块。更多会让生成线程更忙，而服务器对被留在原地观看的人响应更慢 |
| `pregenBackup`                | 布尔值            | `false`                                  | 预生成完成后、玩家仍被留在原地时，将世界复制为一份原始备份。重置时恢复的就是这份副本；资源包已不匹配的副本会被丢弃并重新保留 |
| `resetClearsEntities`         | 布尔值            | `true`                                   | 地图重置时移除所有非玩家的实体 |
| `resetClearsScores`           | 布尔值            | `true`                                   | 地图重置时将资源包保存的每个目标归零，让新的一场比赛从零开始。队伍本身会保留 |
| `resetClearsInventory`        | 布尔值            | `false`                                  | 地图重置时清空每位玩家的物品栏，包括盔甲和副手 |
| `resetClearsExperience`       | 布尔值            | `false`                                  | 地图重置时将每位玩家的经验设回零级 |
| `spawnChunkRadius`            | 整数，0 至 1024   | `128`                                    | 距出生点多远（以方块为单位）的区块会一直保持加载，无论玩家是否在那里，按每个方向 (方块数 + 8) / 16 取整到整区块，因此 128 表示每个方向保持 8 个区块。世界启动时，主世界会在服务器就绪之前向每个方向多准备 4 个区块的方形范围，128 时即 25 乘 25 个区块。0 表示不准备也不保持任何区块，出生点区域会像其他地方一样卸载。RDPL 用自己的票据保持这些区块，因此在 1.21.1 上，只要此键生效，spawnChunkRadius 游戏规则就不起作用 |
| `spawnChunkRadii`             | 列表              | 空                                    | 为主世界写成 dimension=blocks 的半径，如 minecraft:overworld=64，覆盖 spawnChunkRadius。只有主世界有出生点区块，因此其他维度的条目不会改变任何东西 |
| `welcomeSays`                 | 列表              | `[WELCOME]`                              | 欢迎语，每次登录和预生成之后以绿色显示。单独写的条目是适用于所有地方的文字；dimension=message 条目会为该维度覆盖它，并且在每次抵达该维度时也问候玩家，例如 minecraft:the_nether=Welcome to the Nether!。= 后面的消息为空会让该维度静音；空列表则什么都不显示。保持此默认值时，会使用每位玩家各自的语言 |
| `pregenBorderLimit`           | 整数，1 至 1875000 | `8192`                                  | 在拒绝向边界造陆之前，边界允许延伸的最远距离，每个方向以区块计。它的作用是防止一个失误运行数周，而不是用来调高的，资源包也无法设置它。边长 8192 的正方形含有 2.68 亿个区块 **仅限配置。** |
| `pregenDimensions`            | 列表              | `["minecraft:overworld"]`                | 新世界在哪些维度中造陆，按 id，依给定顺序逐个进行 |
| `pregenDimensionsWhenEntered` | 列表              | 空                                    | 不在一开始、而是在有人首次踏入时才造陆的维度，范围相同，同样会留住所有人直到完成。同时列在这里和 pregenDimensions 中的维度，直接在一开始完成 |
| `pregenRunningSays`           | 文本              | `World pregeneration running, %d%% done` | 世界生成期间玩家看到的进度消息，其中 %d 是百分比，第二个 %s 是维度。留空则不告知玩家任何内容。保持此默认值时，会使用每位玩家各自的语言 |
| `pregenFinishedSays`          | 文本              | `World pregeneration finished`           | 生成完成时玩家看到的消息。留空则不告知玩家任何内容。保持此默认值时，会使用每位玩家各自的语言 |
| `pregenStoppedSays`           | 文本              | `World pregeneration stopped`            | 生成被提前停止时玩家看到的消息。留空则不告知玩家任何内容。保持此默认值时，会使用每位玩家各自的语言 |
| `pregenSpectatingSays`        | 文本              | `Spectating until the world is ready`    | 世界生成期间玩家被留在旁观模式时看到的屏幕中央消息。留空则不显示。保持此默认值时，会使用每位玩家各自的语言 |
| `pregenLogo`                  | 文本              | `center`                                 | 预生成完成时徽标所在的位置：left、center 或 right，位于屏幕中央文字上方。它总是会显示；未知的词按 center 处理 |
| `pregenBackupSays`            | 文本              | `Pack requested world backup`            | 复制该备份期间玩家看到的屏幕中央消息。留空则不显示 |
| `resetSays`                   | 文本              | `Pack requested map reset`               | /rdpl reset 还原地图期间向玩家显示的屏幕中央文字。留空则静默重置 |
| `resetSendsTo`                | 文本              | `spawn`                                  | 重置后玩家被送到哪里：spawn、形如 x,y,z 的坐标，或用 dimension:x,y,z 将他们送入另一个世界 |
| `resetRuns`                   | 文本              | 空                                    | 重置清空地图之后运行的函数，写成 namespace:path。留空则不运行任何内容 |

### 虚空世界

*各分组的作用*

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

`control.voidWorld` 决定此分组是否生效。包括虚空世界的生成及其平台。

| 设置项 | 类型 | 默认值 | 作用 |
| --------------------------------- | ------------------ | ------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `voidWorld`                       | 布尔值             | `false`                   | 将列出的维度生成为空无一物的空间，出生点处有一个平台，且没有任何生物，原版三个维度通过生成的预设实现，资源包自己的维度文件则通过其文件实现；列出的任何其他维度会在造陆时被清空。该选择在世界创建时随世界保存，因此之后开启或关闭都不会改变已有的世界 |
| `voidWorldDimensions`             | 列表               | `["minecraft:overworld"]` | 哪些维度变为虚空，按 id。留空表示没有，或者在 voidWorldDimensionsAreBlacklist 开启时表示所有维度 |
| `voidWorldDimensionsAreBlacklist` | 布尔值             | `false`                   | 开启时，列出的维度是不受影响的那些 |
| `voidPlatformBlock`               | 方块               | `minecraft:stone`         | 平台所用的材料 |
| `voidPlatformHeight`              | 整数，-2032 至 2031 | `64`                     | 虚空世界平台所在的 y 坐标 |
| `voidPlatformSize`                | 整数，1 或更大     | `9`                       | 平台的宽度，向下取整为奇数，以便居中位于出生点 |
| `voidWorld`                       | 文本               | `default`                 | 虚空世界的生成及其平台 [default\|global\|off] |

### 龙

*各分组的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

| 设置项 | 类型 | 默认值 | 作用 |
| ------------- | ------- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `dragonFight` | 布尔值  | `true`  | 整场战斗是否发生：龙、它的血条、水晶、它所站立的喷泉，以及玩家用末影水晶发起的重生。属于 `structures` 分组 |

`dragonFight` 属于 `structures` 分组，决定整场战斗是否发生：龙、它的血条、水晶、它所站立的喷泉，以及玩家用末影水晶发起的重生。被清空的末地默认不含它，除非资源包要求；普通的末地默认有它，除非资源包另有设定，因此无论哪种情况，都值得设置 `dragonFight`。

### 地形

*各分组的作用*

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

`control.terrain` 决定此分组是否生效。包括创建时的世界名称和种子、generatorOptions、洞穴区域、云层高度以及世界之间的接缝。

| 设置项 | 类型 | 默认值 | 作用 |
| ----------------------- | ------------------ | ------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldSeed`             | 字符串             | 空                                | 每个新世界所用的种子，按键入时的写法书写：数字直接使用，其他内容则像游戏那样转换成数字。专用服务器也用它创建世界，并将其作为 `level-seed` 写入 `server.properties`。留空则不干预选择 |
| `worldName`             | 文本               | 空                                | 打开创建世界的界面时新世界的名称。留空则保持游戏的默认命名 |
| `worldType`             | 文本               | 空                                | 塑形后的世界所基于的世界类型，可选 default、largebiomes、amplified 或 flat，1.12.2 的名称 customized 和 default_1_1 按 default 读取；flat 是由 generatorOptions 的层构建的超平坦主世界，其上带有资源包的城市。下面的形态（高度、深层石头、海平面、基岩、虚空）会被生成为一个独立的世界预设，列在世界界面的“世界类型”下，并且无论之前选了什么，都会在那里被选中。专用服务器会将其作为 `level-type` 写入 `server.properties`，指向该预设；若没有任何塑形，则指向游戏自带的预设，除非 `level-type` 已经指向 worldTypeExceptions 中的某一项。留空则基于 default 构建 |
| `worldTypeExceptions`   | 列表               | `["flat", "debug_all_block_states"]` | 玩家选择后，生成的预设不会去动的世界类型，例如 flat 或 debug_all_block_states。留空表示任何选择都会被替换 |
| `generatorOptions`      | 文本               | 空                                | 主世界的地形设置，写成 JSON 对象，键为 1.12.2 的 customized 世界类型所写入的那些。此处读取：seaLevel、useLavaOceans、fixedBiome，以及设为 false 的 useCaves、useRavines、useDungeons、useLavaLakes、useStrongholds、useVillages、useMineShafts、useTemples、useMonuments 和 useMansions。在 worldType 为 flat 时，它改为表示各层，自下而上，写成 1.12.2 超平坦文本 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village 或一个层的列表；层之后的数字是生物群系，其后的 village、biome_1、mineshaft、stronghold、oceanmonument、lava_lake 和 decoration 会开启相应内容。仅在世界创建时应用。专用服务器会将其作为 `generator-settings` 写入 `server.properties`，平坦层则写成游戏自带的平坦 JSON，除非 `level-type` 已经指向 worldTypeExceptions 中的某一项。留空则保持世界类型生成的地形 |
| `worldMinHeight`        | 整数，-2032 至 2016 | `-64`                               | 主世界的最低方块，为 16 的倍数，最低到 -2032。游戏自己的底部是 -64；更低则会在原版地形之下形成深层世界，一直是实心石头，直到世界生成层将其雕刻，或 noiseCaves 把游戏的洞穴延伸下去。仅通过生成的预设应用 |
| `worldMaxHeight`        | 整数，-2016 至 2032 | `320`                               | 主世界顶部之上的那个方块，为 16 的倍数，最高到 2032，比 worldMinHeight 至多高 4064。游戏自己的顶部是 320；更高则在原版地形之上留出开阔的天空 |
| `deepStone`             | 文本               | 空                                | 当 worldMinHeight 低于 -64 时，原版地形之下的世界所用的方块，例如资源包自己的深板岩。它在 -64 以下的八层中渗入深板岩，就像深板岩渗入石头那样。留空则保持石头 |
| `noiseCaves`            | 文本               | `off`                                | 当 worldMinHeight 低于 -64 时，游戏的洞穴、隧道、面条洞穴和含水层在哪里延续：off 让原版地形之下的世界保持为实心深层石头，留给世界生成层去雕刻；deep 把它们一直延伸到底层，岩浆湖移到其最底部的十层；world 在此版本上含义相同，因为原版地形本来就有它们 |
| `worldSpawn`            | 文本               | 空                                | 每个新世界的出生位置，写成 x,z 或 x,y,z。不带 y 时使用世界的平均地面高度，即海平面上一格，在超平坦世界中则是各层的顶部，之后游戏会像对待任何出生点那样在那里寻找安全的落脚处。仅在世界创建时应用。留空则交由游戏决定 |
| `worldBorder`           | 整数，0 至 60000000 | `0`                                 | 每个新世界中世界边界的宽度（以方块为单位）。仅在世界创建时应用。0 表示让边界保持在游戏设定的位置 |
| `worldTime`             | 整数，-1 至 23999  | `-1`                                 | 锁定主世界的一天中的时间，以刻为单位，与 /time set 接受的数值相同，因此 18000 是午夜。时钟停止且永不移动：/time set 无法推动它，天数仍在后台计数，移除此设置后就会恢复到应有的时间。-1 表示让时间照常流逝 |
| `caveRegionPlainWeight` | 整数，0 或更大     | `4`                                  | 普通的、无区域的地下相对于洞穴区域各自权重的权重。越高，地下没有任何区域的部分越多 |
| `caveRegionCells`       | 整数，16 或更大    | `128`                                | 洞穴区域单元格的宽度，以方块计。资源包的洞穴区域以大约这个大小的单元格涂绘在地下 |
| `caveRegionCellsY`      | 整数，16 或更大    | `64`                                 | 洞穴区域单元格的高度，以方块计 |
| `worldGravity`          | 列表               | 空                                | 缩放重力，为原版的倍数，1.0 表示不变，0.17 近似月球，对所有实体生效。单独的数值适用于所有维度，写成 dimension=value 的条目只适用于该维度，并优先于单独的数值。留空则不改动重力 |
| `worldFallDamage`       | 列表               | 空                                | 以同样的方式缩放摔落伤害，0.5 为减半，2.0 为加倍 |
| `worldJumpStrength`     | 列表               | 空                                | 以同样的方式缩放跳跃力度，1.5 表示跳得高出一半 |
| `worldTerminalVelocity` | 列表               | 空                                | 以同样的方式缩放生物或玩家下落的最快速度，0.5 表示以原版最高速度的一半下落 |
| `weatherCeiling`        | 列表               | 空                                | 雨和雪所能到达的最高 y 坐标，写成 dimension=y 条目。单独的数字适用于所有维度。高于此高度不会下雨，雪不会堆积，炼药锅不会注水，闪电不会落下，也不会绘制任何降水；低于此高度天气不变。冰取决于温度而非降水，因此在此线之上仍会形成。留空表示没有上限 |
| `cloudHeight`           | 列表               | 空                                | 云绘制所在的 y 坐标，写成 dimension=y 条目。单独的数字适用于所有维度。它优先于资源包维度自己的 `cloudHeight`。留空则保持游戏自己的云层高度，主世界为 192 |
| `worldBelow`            | 列表               | 空                                | 将另一个维度叠放在此维度之下：从世界底部掉出去会把你带入指定的维度，在相同的 x 和 z 处从其顶盖之下抵达，仍在下落。条目写成 dimension=target，例如 minecraft:overworld=minecraft:the_nether 让下界悬挂在主世界之下；单独的 id 适用于所有维度。向下挖穿需要不保留底部的基岩，这由 worldSeamBedrock 决定。留空表示地板仍是地板 |
| `worldAbove`            | 列表               | 空                                | 对顶盖同理：升过世界顶部会把你带入指定的维度，在其地板之上抵达。写法与 worldBelow 相同 |
| `worldSeamEntities`     | 布尔值             | `true`                               | 掉落物、生物和其他实体是否也能通过世界接缝，还是只有玩家可以。骑乘者和坐骑一次只能通过一个 |
| `worldSeamBedrock`      | 布尔值             | `false`                              | 无论如何都在接缝边界保留基岩。关闭时，地板或顶盖带有 worldBelow 或 worldAbove 接缝的维度不会在那里生成基岩，因此可以挖出通路。已生成的区块保持原样 |

### 服务器

*各分组的作用*

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

`control.server` 决定本分组：资源包可以设置的 `server.properties` 中的各行，以及游戏模式、难度和对局域网开放的世界上的命令。在专用服务器上，资源包在此设置的每个值都会在服务器启动时写入 `server.properties`，因此该文件会写明当前生效的内容，服务器已经读取过的那些值也会同步设置到服务器上。单人世界采用集成服务器所具有的设置，各行已分别说明。留空，或数值为 `-1`，则保留服务器自身的值；`control.server` 为 `off` 时，每一行都保持服务器原样。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `worldGameMode` | 文本 | 空 | 每个新世界以哪种模式开始，可选生存、极限、创造、冒险或旁观。极限模式即死亡后世界终结的生存模式，对整个存档生效，与世界选择界面上的选项相同。留空则保持创建世界者的选择。世界选择界面只提供生存、极限和创造，因此冒险和旁观只能在创建世界时设置。专用服务器在每次启动时都会把所有世界设为 server.properties 中的模式，所以在那里资源包的模式会在世界加载之前写入 server.properties（gamemode 和 hardcore） |
| `privacy` | 布尔值 | `true` | 是否关闭游戏的遥测和聊天举报：不发送任何遥测事件，客户端不为聊天消息签名，服务器不保留聊天会话也不要求会话，因此任何消息都无法被举报。资源包省略该项时，采用 `tweaks` 类别中的 `privacy` 配置选项。在下一次加入世界或服务器时生效 |
| `worldLanCommands` | 布尔值 | `true` | 将单人世界对局域网开放的玩家，是否可以为所有加入者开启命令。`false` 会使“对局域网开放”界面的“允许命令”按钮变灰并固定为关闭，无论以何种方式请求（包括 `/publish`），世界都不带命令开放 |
| `worldDifficulty` | 列表 | 空 | 锁定难度，可选和平、简单、普通或困难。单独写的难度适用于所有维度；写成 维度=难度 的条目（例如 minecraft:the_nether=hard）只适用于该维度，并优先于单独写的难度。世界自身的设置保持原样，移除该条目后即恢复。专用服务器会把主世界的难度作为 `difficulty` 写入 `server.properties`。留空则保持所选难度 |
| `worldForceGameMode` | 布尔值 | 空 | 加入的玩家是否每次都被设回服务器的游戏模式，即 `force-gamemode` 一行。对局域网开放的世界本来就会这样，`false` 在那里同样会使其停止 |
| `worldPvp` | 布尔值 | 空 | 玩家之间是否可以互相伤害，即 `pvp` 一行。单人世界同样采用 |
| `worldFlight` | 布尔值 | 空 | 在生存模式中飞行的玩家是否不被踢出，即 `allow-flight` 一行。单人世界同样采用 |
| `worldSpawnProtection` | 整数，-1 或更大 | `-1` | 出生点周围多少格内只有管理员可以建造，即 `spawn-protection` 一行，0 表示不保护。只有专用服务器会保护出生点 |
| `worldNether` | 布尔值 | 空 | 是否可以进入下界，即 `allow-nether` 一行。`false` 在单人世界中同样会将其关闭 |
| `worldCommandBlocks` | 布尔值 | 空 | 命令方块是否运行，即 `enable-command-block` 一行。单人世界本来就会运行，`false` 在那里同样会将其关闭 |
| `worldIdleTimeout` | 整数，-1 或更大 | `-1` | 玩家挂机多少分钟后被踢出，即 `player-idle-timeout` 一行，0 表示永不。单人世界同样采用 |
| `worldMotd` | 文本 | 空 | 在服务器列表中显示于服务器名称下方的那一行，即 `motd` 一行。对局域网开放的单人世界会用它取代房主和世界名称来显示 |
| `worldMaxSize` | 整数，-1 至 29999984 | `-1` | 世界边界最远可以扩展到离中心多少格，即 `max-world-size` 一行。单人世界同样采用 |
| `worldStructures` | 布尔值 | 空 | 新世界是否生成结构，即 `generate-structures` 一行，也是世界选择界面上的“生成结构”选项。仅在创建世界时应用于该世界 |
| `worldSpawnMonsters` | 布尔值 | 空 | 敌对生物是否生成，即 `spawn-monsters` 一行。`false` 在单人世界中同样会阻止其生成 |
| `worldSpawnAnimals` | 布尔值 | 空 | 动物是否生成，即 `spawn-animals` 一行。`false` 在单人世界中同样会阻止其生成 |
| `worldSpawnNpcs` | 布尔值 | 空 | 村民是否生成，即 `spawn-npcs` 一行。`false` 在单人世界中同样会阻止其生成 |
| `worldViewDistance` | 整数，-1 至 32 | `-1` | 专用服务器向每位玩家发送多少区块范围内的世界，即 `view-distance` 一行。单人世界则遵循渲染距离 |
| `worldSimulationDistance` | 整数，-1 至 32 | `-1` | 专用服务器让每位玩家周围多少区块范围内的世界保持刻更新，即 `simulation-distance` 一行。单人世界则遵循其自身的设置 |

### 配方

*各分组的作用*

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

`control.recipes` 决定本分组：合成配方和熔炉配方的屏蔽及其白名单。屏蔽熔炉配方时，高炉、烟熏炉和营火的配方也一并屏蔽，因为 1.12.2 把所有烹饪配方都放在同一个熔炉列表里。切石机配方和锻造台配方永远不会被屏蔽，因为 1.12.2 中没有这些配方。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `blockRecipes` | 布尔值 | `false` | 移除所有合成配方，但 `recipeWhitelist` 中模组的配方除外。默认没有任何豁免，所以请列出你自己资源包的命名空间以保留其配方 |
| `recipeWhitelist` | 模组 id 列表 | `["minecraft"]` | 其合成配方得以保留的模组 |
| `blockedRecipeMods` | 模组 id 列表 | 空 | 其合成配方被彻底移除的模组，无论白名单如何设置 |
| `recipeMatch` | `recipe`、`output` 或 `both` | `recipe` | 屏蔽合成配方时从何处读取模组 id：配方自身的名称、其产出的物品，或二者之一（任一匹配即屏蔽，任一在白名单中即豁免） |
| `blockFurnaceRecipes` | 布尔值 | `false` | 对熔炉配方同样处理，模组按产出的物品读取 |
| `furnaceWhitelist` | 模组 id 列表 | `["minecraft"]` | 其熔炉配方得以保留的模组 |
| `blockedFurnaceMods` | 模组 id 列表 | 空 | 其熔炉配方被彻底移除的模组 |
| `logBlockedRecipes` | 布尔值 | `true` | 按模组记录被屏蔽数量的日志 |
| `furnace` | 布尔值 | `true` | 应用 furnace/*.json 文件，用于添加和移除熔炉烧炼配方 **仅限配置。** |
| `removals` | 布尔值 | `true` | 应用 recipe_removals/*.json 文件，按名称、命名空间或产出删除合成配方 **仅限配置。** |
| `skipMissingItems` | 布尔值 | `true` | 跳过使用了未注册物品的配方，而不是让它们失败。数量只记录一次 **仅限配置。** |

### 命令

*各分组的作用*

`control.commands` 决定本分组：谁可以运行本模组自己的命令，即 goto 的权限等级。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `gotoLevel` | 整数，0 至 4 | `3` | 运行 /rdplserver goto <name> 所需的权限等级，该命令会把发送者带到最近的一处。3 为管理员，命令其余各部分也处于这一级别。2 还允许命令方块运行它，因此资源包可以把这次跳转放在按钮或压力板上，而不必把命令的其余部分交给任何人。0 允许任何玩家输入。无论此项如何设置，/rdplserver 的其他部分都保持在 3 |
| `gotoNextLevel` | 整数，0 至 4 | `3` | 运行 /rdplserver goto <name> next 的权限等级，该命令会跳过上一次带发送者去的那一处，另找一处。等级划分与 gotoLevel 相同 |
| `gotoBackLevel` | 整数，0 至 4 | `3` | 运行 /rdplserver goto <name> back 的权限等级，该命令会把发送者送回上一处。等级划分与 gotoLevel 相同 |
| `gotoPlaceLevels` | 列表 | 空 | 针对单个地点的权限等级，写成 name=level 条目，每行一条，仅对该地点覆盖上面三项设置，且对其全部三种形式生效。name 就是你在 goto 之后输入的内容，即 Village 或 Mansion 这类原版名称，或资源包用 locateAs 为自己的结构注册的名称。等级划分相同：3 为管理员，2 还包括命令方块，0 为任何人。这样，资源包可以对自己的遗迹开放通路，而所有原版结构保持关闭，反之亦然。未注册任何内容的名称会被忽略，并在日志中留下一条说明 |

### 世界生成，仅限配置

*各分组的作用*

这些 `worldgen` 键不属于任何分组，仅由配置决定。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `worldgenDebug` | 布尔值 | `false` | 把其他消息所指向的调试行写入 logs/rdpl.log，例如由哪个资源包提供了某个文件、每条命令做了什么。输出非常冗长 |
| `worldTemplate` | 文本 | `auto` | 应用哪个世界模板的设置。资源包在 worldtemplates/*.json 中添加模板，你在此以 namespace:name 的形式指定。“auto”会从优先级最高的资源包中选取模板。留空则不使用 |
| `tellWorldType` | 布尔值 | `true` | 玩家加入以生成的预设创建的世界时，在聊天中告知是哪个模板塑造了它。资源包无法设置此项 |
| `worldBorderLimit` | 整数，1 至 60000000 | `60000000` | 资源包通过 worldBorder 可以请求的最宽边界。请求更大的资源包会被拒绝，边界保持游戏默认的位置。资源包无法设置此项 |
| `retrogenKey` | 文本 | `0000` | 修改它可使每个区块重新符合回溯生成的条件，对所有世界生成条目生效。新矿脉会叠加在已有内容之上 |
| `retrogenChunksPerTick` | 整数，1 或更大 | `2` | 每刻补做多少个已生成的区块。越高越快，但卡顿越明显 |

### `packs` 类别

*各分组的作用*

如何查找并提供资源包文件夹。仅限配置。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `rootDirectory` | 文本 | `rdploader` | 加载资源包的文件夹，相对于 .minecraft 目录。也可以使用绝对路径。需要重启 |
| `overrideResourcePacks` | 布尔值 | `true` | 把资源包插入到玩家所选资源包和世界自带数据包之上。名为 RDPLO... 的资源包总是覆盖，RDPLN... 的则从不覆盖 |
| `warnOnCaseMismatch` | 布尔值 | `true` | 当某个文件仅因文件系统不区分大小写才匹配成功时发出警告。这样的资源包在 Linux 上会失效 |
| `logContents` | 布尔值 | `false` | 记录找到的每个资源包及其提供的文件数量 |
| `traceUnresolvedVariables` | 布尔值 | `false` | 第一次请求文件名中带有“#”的文件时记录堆栈跟踪，指出是谁请求了它 |

### `content` 类别

*各分组的作用*

方块、物品、流体以及资源包定义的其他一切内容。仅限配置。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `load` | 布尔值 | `true` | 注册资源包定义的方块、物品、流体、材料和创造模式标签页，并加载它们的暴露设置。需要重启 |
| `vanillaClients` | 布尔值 | `false` | 服务于纯原版客户端：不注册任何资源包的内容，包括方块、物品、流体和创造模式标签页，也不加载任何暴露设置，因此没有安装本模组的客户端也可以加入。仅存在于服务端的一切内容仍然生效。需要重启 |
| `sounds` | 布尔值 | `true` | 注册 sounds/*.json 所指定的音效事件，使资源包可以附带自己的音频 |
| `fuels` | 布尔值 | `true` | 应用 fuels/*.json 文件，为物品设定熔炉燃烧时间 |
| `potions` | 布尔值 | `true` | 注册资源包中 potions/*.json 和 potion_types/*.json 所描述的药水效果和药水类型。需要重启 |
| `brewing` | 布尔值 | `true` | 应用 brewing/*.json 文件，用于添加酿造台配方 |
| `villagers` | 布尔值 | `true` | 注册 villagers/*.json 所描述的村民职业，并应用 trades/*.json 中的交易。需要重启 |
| `entities` | 布尔值 | `true` | 注册资源包中 entities/*.json 所描述的实体变种。需要重启 |
| `overrides` | 布尔值 | `true` | 应用 overrides/<namespace>/<name>.json 文件，用于更改已存在的方块、物品和药水类型的属性，无论是原版的还是模组的 |
| `disabled` | 布尔值 | `true` | 应用 disabled/*.json 文件，使方块和物品退出游戏：没有创造模式标签页、JEI 条目、配方、战利品、交易、标签，无法放置、使用或拾取，已有的堆叠也会被删除 |
| `hardness` | 布尔值 | `true` | 应用 hardness/*.json 文件，为一组方块设定挖掘时间和爆炸抗性倍率，按方块位置随机生成 |
| `shovelPaths` | 布尔值 | `true` | 允许铲子把标记为 behavesAs path 的方块变成土径，并在潜行时将土径还原 |
| `shovelPathBecomes` | 文本 | 空 | 铲子把这些方块变成什么。留空则使用土径 |
| `shovelPathReverts` | 文本 | 空 | 持铲潜行时把土径变回什么。留空则使用泥土 |
| `hoeTilling` | 布尔值 | `true` | 允许锄头耕地标记为 behavesAs till 的方块 |
| `hoeTillsInto` | 文本 | 空 | 锄头把这些方块变成什么。留空则使用耕地 |
| `caneMaxHeight` | 整数，1 至 255 | `3` | 原版甘蔗能长多高。原版为 3。资源包定义的甘蔗方块使用它们自己的生长部分，忽略此项 |
| `cactusMaxHeight` | 整数，1 至 255 | `3` | 原版仙人掌的同样设置 |

### `data` 类别

*各分组的作用*

战利品、函数和注册表名称。仅限配置。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `lootInjections` | 布尔值 | `true` | 应用 loot_injections/*.json 文件，向已存在的战利品表添加战利品池，而不是替换整张表 |
| `playerLoot` | 布尔值 | `true` | 应用 player_loot/*.json 文件，在玩家死亡时抽取战利品表并掉落其结果，可与物品栏内容叠加或取而代之 |
| `registryRemaps` | 布尔值 | `true` | 应用 registry_remap 文件，重命名注册表条目，使重命名之前保存的世界保留其方块和物品，而不会丢失 |
| `anvils` | 布尔值 | `true` | 应用 anvils/*.json 文件，使铁砧可以按等级花费为物品附上指定的附魔，在取出时获得一项进度，并在此之前禁止使用该物品 |
| `blockDrops` | 布尔值 | `true` | 应用 block_drops/*.json 文件，在方块被破坏时补充或替换其掉落物（包括经验），适用于资源包并不拥有的方块 |
| `functions` | 布尔值 | `true` | 从资源包中加载 .mcfunction 文件，使其在每个世界中都能使用 |

### `tweaks` 类别

*各分组的作用*

对原版行为的小幅调整。仅限配置，但 `privacy` 除外，资源包也可以设置它；参见 [额外功能：原版调整](#额外功能原版调整)。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `promptLeafDecay` | 布尔值 | `true` | 失去树木的树叶会在一秒内凋落，而不是等待随机刻 |
| `lenientPaths` | 布尔值 | `true` | 可以在方块下方制作土径，并且在其上方放置方块时土径仍会保留 |
| `unbreakableSpawners` | 布尔值 | `false` | 刷怪笼无法被挖掘或炸毁。创造模式仍可将其移除。需要重启 |
| `experimentalWarning` | 布尔值 | `false` | 在创建或打开世界时显示游戏的实验性设置警告。关闭时会视作你已点击继续 |
| `privacy` | 布尔值 | `true` | 关闭游戏的遥测和聊天举报：不发送也不记录任何遥测事件，客户端不为聊天消息签名，服务器不保留聊天会话也不要求会话，因此任何人发送的消息都无法被举报，客户端也不会显示服务器未强制安全聊天的警告提示。资源包可以在世界模板 `settings` 中以 `privacy` 设置它，归属于 [服务器](#服务器) 分组。在下一次加入世界或服务器时生效 |
| `darkSplash` | 布尔值 | `true` | 以深色绘制加载界面，并用资源包加载器的徽标取代游戏自己的徽标：徽标在界面创建时被替换，并且当游戏自带的“单色徽标”选项仍处于关闭时会将其开启，该选项在下次启动时生效。关闭则保持该选项原样 |

---

# 其他模组

## Blast Plaster 集成

*其他模组*

`<namespace>/blastplaster/*.json`

文件名由你自定，只读取该文件夹，多个文件会叠加。

Blast Plaster 负责处理爆炸之后的行为：逐方块修复弹坑、感知树木的整棵砍伐、掉落物控制。它本身只读取一份全局配置。由资源包驱动时，它会**按维度**响应，由资源包给出决定，而不是让玩家去编辑配置。没有资源包文件，或者没有安装 Blast Plaster，这里的内容都不起任何作用，该文件夹会被跳过并在日志中留下一行。

写在文件顶层的键适用于所有地方；`dimensions` 块则按 id 为某个维度覆盖它们。资源包从未提及的任何键都保持 Blast Plaster 自身配置所写的值，因此资源包只需设置自己关心的几项，其余保持不动。

下面一次性展示每个键。实际文件只写需要的那些。

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

`explosionMode` 是主开关：`HEAL` 会随时间修复弹坑，`EJECT_DROPS` 保留坑洞并掉落大约三分之一的方块（原版行为），`VISUAL_TOSS` 保留坑洞且不掉落任何东西。由资源包驱动时，默认值为 `EJECT_DROPS`（而不是 Blast Plaster 的 `HEAL`），因此未配置的安装表现得与原版一致。

| 键 | 值 | 作用 |
| --- | --- | --- |
| `explosionMode` | `HEAL`、`EJECT_DROPS`、`VISUAL_TOSS` | 爆炸之后会发生什么 |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true 或 false | 哪些爆炸会被处理 |
| `processPlayerIgnitedTNT` | true 或 false | 玩家点燃的 TNT 是否与其他爆炸一同处理 |
| `customEntitiesToHeal` | 实体名称列表 | 其他模组造成的爆炸，以 `modid:entity` 命名 |
| `healFullTrees` | true 或 false | 被爆炸削去一角的树会被整棵取走或整棵恢复，而不是被拦腰截断 |
| `maxTreeSize` | 数字 | 一棵树最多可以占用多少方块，超过则不予处理 |
| `minimumTicksBeforeHeal`, `randomTickVar` | 数字 | 多久之后开始修复，以及修复节奏有多参差不齐 |
| `overrideBlocks` | true 或 false | 修复时是否覆盖此后在坑洞中建造的内容 |
| `enableFakeTossedBlocks` | true 或 false | 从爆炸中飞出的碎片 |
| `enableExplosionFlash` | true 或 false | 爆炸瞬间的明亮闪光 |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | 数字 | 闪光持续多久、有多亮、喷出多少粒子，以及脉动多少次 |
| `enableExplosionSmoke` | true 或 false | 事后升起的烟柱 |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | 数字 | 烟雾持续多久，以及有多浓 |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true 或 false | 玩家自己的 TNT 会留下什么 |
| `enableDropSuppression`, `dtSpecialDrops` | true 或 false | 爆炸范围内的掉落物，以及 Dynamic Trees 自己的掉落物 |
| `preventMobDrops` | true 或 false | 被爆炸杀死的生物是否仍会掉落物品 |
| `blockConversions` | 规则列表 | 被炸的方块变成什么而不是原样恢复，使建筑每次爆炸磨损一级 |

`blockConversions` 决定被炸的方块变成什么，而不是原样恢复。一条规则写作 `<source>=<result>[@chance]`：source 是方块 id，或以 `#` 开头的方块标签；result 是方块 id，或用 `nothing` 表示留空；chance 取值 0.0 至 1.0，默认为 1.0。第一条匹配的规则生效，所以具体的规则要放在宽泛的规则之上，并且已经是某条规则结果的方块不会再被转换，因此一堵墙每次爆炸只会退化一级，而不会被磨损殆尽。

**完全原版的外观：** `EJECT_DROPS`，并将 `healFullTrees`、`enableFakeTossedBlocks`、`enableExplosionFlash`、`enableExplosionSmoke`、`preventMobDrops` 和 `playerTNTAlwaysDrops` 全部关闭。每个键都支持按维度设置。

**原版客户端**看不到任何异常。闪光是唯一会放置方块的功能，因此设置了 `vanillaClients` 时它会被强制关闭；其余一切都是纯客户端也能理解的粒子和物品。

不属于资源包键的内容：Blast Plaster 的调试日志，以及它的原木与树叶配对（树木的识别必须在整个游戏中只有一个答案）。这两项都保留在 Blast Plaster 自己的配置中。

---

# 参考

## 值列表

*参考*

### 接受的名称

*值列表*

上面各表中凡是写到“材料之一”等处，解析器接受的就是这些名称。无法识别的内容会被记录到日志，并替换为默认值。

**方块材料。** `air`、`grass`、`ground`、`wood`、`rock`、`iron`、`anvil`、`water`、`lava`、`leaves`、`plants`、`vine`、`sponge`、`cloth`、`fire`、`sand`、`circuits`、`carpet`、`glass`、`redstone_light`、`tnt`、`coral`、`ice`、`packed_ice`、`snow`、`crafted_snow`、`cactus`、`clay`、`gourd`、`dragon_egg`、`portal`、`cake`、`web`、`piston`、`barrier`、`structure_void`。游戏本身已不再有材料；每个名称的作用都与 1.12.2 中该材料相同：它决定地图颜色、方块是否需要工具才能掉落物品、活塞如何对待它、熔岩是否会将其点燃、流动的液体是否会将其冲走，以及放置的方块是否会取代它。

**音效类型。** `wood`、`ground`、`plant`、`stone`、`metal`、`glass`、`cloth`、`sand`、`snow`、`ladder`、`anvil`、`slime`。

**地图颜色。** `air`、`grass`、`sand`、`cloth`、`tnt`、`ice`、`iron`、`foliage`、`snow`、`clay`、`dirt`、`stone`、`water`、`wood`、`quartz`、`adobe`、`magenta`、`light_blue`、`yellow`、`lime`、`pink`、`gray`、`silver`、`cyan`、`purple`、`blue`、`brown`、`green`、`red`、`black`、`gold`、`diamond`、`lapis`、`emerald`、`obsidian`、`netherrack`。

**渲染层。** `solid`、`cutout`、`cutout_mipped`、`translucent`。留空时，方块会按其类型自行选择。

**稀有度。** `common`、`uncommon`、`rare`、`epic`。

**火把粒子。** `none`、`flame`、`colored`。`colored` 使用 `particleColor`。

**工具类别。** `pickaxe`、`axe`、`shovel`、`hoe`、`sword`。

**盔甲槽位。** `head` 或 `helmet`，`chest` 或 `chestplate`，`legs` 或 `leggings`，`feet` 或 `boots`。

**染色。** `biome`、`none`，或六位十六进制颜色。定义中任何位置的颜色都是十六进制，开头的 `#` 可有可无。

用于 `behavesAs` 的**行为**。`till`、`path`、`bush`、`animals`。

用于 `plantTypes` 的**植物类型**。`plains`、`desert`、`beach`、`cave`、`water`、`nether`、`crop`。仅限 1.20.1；1.21.1 会读取该键但忽略它。

**生物群系类型**，即在表格写到“生物群系类型列表”的各处代表生物群系标签的词，用于 `biomeTypes`、生物群系的 `types`、模板的 `roles` 以及 `biomes` 部分：`ocean`、`deepocean`、`beach`、`river`、`mountain`、`mesa`、`hills`、`coniferous`、`jungle`、`forest`、`savanna`、`overworld`、`nether`、`end`、`hot`、`cold`、`sparse`、`dense`、`wet`、`dry`、`spooky`、`dead`、`lush`、`mushroom`、`magical`、`rare`、`plateau`、`modified`、`water`、`desert`、`plains`、`swamp`、`sandy`、`snowy`、`wasteland`、`void`。原版的词对应 `minecraft:is_*` 标签，其余的对应约定标签，在 1.20.1 上是 `forge:is_*`，在 1.21.1 上是 `c:is_*`。写全的标签，如 `minecraft:is_forest` 或 `#minecraft:is_forest`，则按原样采用。大小写无关，因此 1.12.2 的 `FOREST` 仍然可以读取。

世界模板 `roles` 的**角色**。上面的任一生物群系类型词：每个词指定一个生物群系，在屏蔽移除了带有该标签的生物群系之后，由它来填补，因此 `"ocean": "mypack:ruby_ocean"` 会让红宝石海洋出现在原本海洋被屏蔽的任何地方。

世界模板 `structures` 以及 `structures` 分组自身列表中的**结构**：1.12.2 的名称 `villages`、`mineshafts`、`strongholds`、`temples`、`monuments`、`mansions`、`netherbridges` 和 `endcities`，或者游戏或模组提供的任何结构集，如 `pillager_outposts`、`ancient_cities`、`trail_ruins`、`shipwrecks`、`ocean_ruins`、`ruined_portals`、`nether_fossils`、`buried_treasures`、`desert_pyramids`、`jungle_temples`、`igloos`、`swamp_huts`、`woodland_mansions`、`ocean_monuments`、`nether_complexes`、`end_cities`。1.12.2 的名称会被读作它所代表的那些结构集，因此 `temples` 即沙漠神殿、丛林神庙、雪屋和沼泽小屋合在一起。1.12.2 的填充名称也会被读取，对应于它们在本版本世界中所代表的部分：`caves` 为洞穴雕刻器（噪声洞穴是 `noiseCaves`），`ravines` 为峡谷，`dungeons` 为刷怪房，`lavalakes` 为熔岩湖，`netherlava` 为下界敞开的熔岩泉，`fire` 为下界的火焰斑块，`glowstone` 为其荧石，`ice` 为冰冻的顶层，`animals` 为生成区块时放置的动物。`waterlakes` 可以接受但不起作用，因为本版本没有水湖。

用于生物群系生成和比率的**生物类型**。`creature`、`monster`、`ambient`、`water`。实体变种的生成还可以按名称使用本版本的其他列表，例如 `water_ambient` 或 `underground_water_creature`。

用于 `oreTypes` 的**矿石类型**。`COAL`、`IRON`、`COPPER`、`GOLD`、`REDSTONE`、`DIAMOND`、`LAPIS`、`EMERALD`、`QUARTZ`、`DIRT`、`GRAVEL`、`DIORITE`、`GRANITE`、`ANDESITE`、`TUFF`、`CLAY`、`SILVERFISH`，`CUSTOM` 用于任何其他矿石。

实体变种 `immuneTo` 的**伤害类型**，不论大小写，带不带下划线均可。1.12.2 的名称，各自涵盖当时所涵盖的内容：`inFire`（营火也算）、`onFire`（无人发射的火球也算）、`lava`、`hotFloor`、`inWall`（世界边界也算）、`cramming`、`drown`、`starve`、`cactus`、`fall`、`flyIntoWall`、`outOfWorld`（`/kill` 也算）、`generic`、`magic`、`indirectMagic`、`wither`、`anvil`、`fallingBlock`、`dragonBreath`、`fireworks`、`lightningBolt`、`thorns`、`arrow`、`fireball`、`thrown`，`mob` 用于生物的攻击、羊驼的唾沫、潜影贝的子弹或凋灵的头颅，`player` 用于玩家的攻击，`explosion` 用于无人引爆的爆炸，例如床，`explosion.player` 用于由生物或玩家引爆的爆炸，如苦力怕或点燃的 TNT。本版本自己的伤害也有名称：`fire` 指两种燃烧中的任一种，另有 `lightning`、`void`、`freeze`、`dryOut` 和 `sweetBerry`。其他任何内容都会被读作伤害类型 id，如 `minecraft:sonic_boom` 或某个模组自己的类型。

实体变种的 `sounds`、上锁箱的 `openSound` 和传送门的 `sound` 所用的**音效名称**：任何已注册的音效事件，无论是游戏的、模组的，还是资源包通过 `sounds` 添加的。1.12.2 的名称会被读作该音效现在的名称，因此 `entity.endermen.scream` 会播放 `entity.enderman.scream`，`block.cloth.step` 会播放 `block.wool.step`，`entity.small_slime.squish` 会播放 `entity.slime.squish_small`，`record.cat` 会播放 `music_disc.cat`。本版本取消的四种鹦鹉模仿音，即末影人、北极熊、狼和僵尸猪人，不会播放任何声音。

## 文件夹列表

*参考*

每个文件夹的完整路径及其所述章节的链接，都在 [文件放在哪里](#文件放在哪里) 中。

## 命令

*参考*

### 你自己的命令

*命令*

`/rdpl` 在你自己的机器上运行，不需要任何权限，因为它所触及的一切都归你所有。重载会重新扫描你拥有的文件夹并刷新你自己的资源；它不会触及任何服务器，因此服务器上的副本要改用 `/rdplserver reload` 来重载。在单人游戏中，两者是同一台机器，所以 `/rdpl reload` 也会重新应用你的 [属性覆盖](#属性覆盖) 并重新编排你的队伍。

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdpl list` | 无 | 每个已加载的资源包、其优先级及其内容。点击资源包可查找其中的文件 |
| `/rdpl which <namespace:path>` | 无 | 由哪个资源包提供指定文件，以及它遮蔽了哪些资源包 |
| `/rdpl reload` | 无 | 重新扫描文件夹并重载一切，包括游戏自身的资源重载 |
| `/rdpl unused` | 无 | 你的资源包中尚无任何东西请求过的文件，通常是路径拼写错误 |
| `/rdpl config unused` | 无 | `rdploader/config` 中已没有任何已安装资源包定义的选项文件 |
| `/rdpl config prune` | 无 | 删除这些文件 |
| `/rdpl pixelmap <namespace:path>` | 无 | [像素图](#以像素图编写的纹理) 的最终结果，逐字符显示 |
| `/rdpl biome`, `biome list [all]` | 无 | 每个可以生成的生物群系及其 id；`all` 包括那些无法生成的 |
| `/rdpl biome here` | 无 | 你所在的生物群系：其名称、id 和编号 |
| `/rdpl biome find <name>` | 服务器的 | 已关联。原样传给 `/rdplserver`，由它来决定，参见下表 |
| `/rdpl locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen` | 服务器的 | 已关联。原样传给 `/rdplserver`，由它来决定，参见下表 |

**哪些服务器子命令已关联，其余为何没有。** `locate`、`goto`、`vein`、`gate`、`pregen`、`intro`、`team`、`round`、`dimensions` 和 `oregen` 只可能指服务器的，因为只有服务器了解世界、其中的玩家和回合，所以 `/rdpl` 把它们移交出去。在单人游戏中，这些命令之后的 Tab 补全提供的是 `/rdplserver` 会提供的内容；在服务器上，`goto` 提供原版结构名称。其余的 `reload`、`list`、`which`、`unused`、`config`、`pixelmap` 和 `biome` 保持各自的含义，即针对你的资源包和你的客户端。已关联的命令由服务器自己的权限检查来裁决，因此客户端既无法作弊，也不会被告知伪造的答案。

**日常编辑：** F3+T 重载纹理、模型和语言文件，`/reload` 重载服务器的数据。当你*添加*或*删除*文件时请使用 `/rdpl reload`，因为这会改变文件夹所包含的内容。

### 服务器命令

*命令*

在专用服务器上，`/rdplserver` 对服务器自己的文件夹副本执行同样的操作。“等级”一列是发送者所需的权限等级：`3` 为管理员，`2` 还允许命令方块，`0` 为任何玩家，`4` 高于管理员，没有人能达到。

#### 资源包与文件

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver reload` | 3 | 重新扫描服务器的文件夹并重载一切，然后再次编排队伍和目标 |
| `/rdplserver list` | 3 | 服务器加载的每个资源包、其优先级及其内容 |
| `/rdplserver which <namespace:path>` | 3 | 由哪个资源包提供指定文件，以及它遮蔽了哪些资源包 |
| `/rdplserver unused` | 3 | 服务器资源包中尚无任何东西请求过的文件 |
| `/rdplserver config unused` | 3 | `rdploader/config` 中已没有任何已安装资源包定义的选项文件 |
| `/rdplserver config prune` | 3 | 删除这些文件 |
| `/rdplserver pixelmap <namespace:path>` | 3 | 像素图的最终结果 |

#### 世界与生成

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver oregen` | 3 | 被屏蔽的矿石生成的累计总数，按模组和类型统计 |
| `/rdplserver generators` | 3 | 被屏蔽的世界生成器的累计总数，按模组和类型统计 |
| `/rdplserver biome list [all]` | 3 | 服务器上每个可以生成的生物群系，带其编号、id 和名称；`all` 包括那些无法生成的 |
| `/rdplserver biome` | 3 | 你所在的生物群系以及资源包对它的处理：其 id、编号和名称，`blockBiomes` 是否开启、当前启用的是哪个世界模板，以及地面、其下的方块和 y 40 处的石头 |
| `/rdplserver biome here [player]` | 3 | 你或所指定玩家所在的生物群系：其名称、id 和编号。控制台需要指明玩家 |
| `/rdplserver biome find <name>` | 3 | 6400 格范围内最近的、生成与该 id 或显示名称相匹配的生物群系的地点：其坐标以及与运行命令处的距离。没有那么近的，或名称不匹配任何生物群系时会如实告知。`/rdpl biome find` 会转发给它 |
| `/rdplserver dimensions` | 3 | 每个维度，包括资源包添加的 |
| `/rdplserver vein <entry> [radius]` | 3 | `vein` 形态的世界生成条目在运行命令处周围该区块数（默认 8）范围内播下矿脉的位置，由近及远，无论这些区块是否已经存在。`/rdpl vein` 会转发给它 |

#### 传送门命令

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver gate`, `gate list` | 3 | 每个传送门、其维度、其范围，以及是否开启 |
| `/rdplserver gate check <player>` | 3 | 玩家通过了哪些传送门 |
| `/rdplserver gate grant <player> <gate>` | 3 | 为玩家开启一个传送门 |
| `/rdplserver gate revoke <player> <gate>` | 3 | 再把它关上 |

#### 预生成命令

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver pregen <radius>` | 3 | 生成运行命令处周围该区块数范围内的每个区块。参见 [预生成](#预生成) |
| `/rdplserver pregen status` | 3 | 一次运行进行到了哪里 |
| `/rdplserver pregen stop` | 3 | 结束它 |

#### 玩家、队伍与回合

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver intro` | 0 | 让世界介绍在你下次加入时再次播放。任何玩家都可以运行，且只会清除自己的记录；没有资源包设置介绍时会被拒绝 |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0 | 资源包编排的阵营以及加入和离开它们的方式，仅在资源包编排阵营时提供，参见 [队伍](#队伍) |
| `/rdplserver round start`, `round reset`, `round vote yes`, `round vote no` | 0 | 开始资源包在大厅中等候的回合，重置正在进行的回合或发起投票，并在其中投票，具体按资源包的计分规则而定，且只有玩家才能开始回合；仅在资源包记分时才向玩家提供，参见 [计分](#计分) |
| `/rdplserver card <rule> [players]` | 2 | 向指定的玩家或你自己显示一条 [卡片规则](#卡片)，通过其 id 或文件名指定。`when`、`repeat` 和 `cooldown` 会被跳过 |
| `/rdplserver reset` | 3 | 像回合结束时那样把地图恢复原状：所有人被扣留，实体被清扫，分数被清除，运行 `resetRuns`，玩家被送到 `resetSendsTo` 后释放，并以起始人数开启一个回合，如 [预生成](#预生成) 下的重置设置所述。不会从 `/rdpl` 转发 |

#### 前往各处

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver locate <name>` | 3 | 资源包以该 `locateAs` 名称放置的最近的结构 |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | 带你前往最近的一处尚无人去过的地方，沿途查找而不生成陆地。资源包用 `locateAs` 注册的地点则是放置的最近一处，无论是否有人去过。`temple` 指所有零散的特征结构：沙漠神殿、丛林神庙、女巫小屋和雪屋。在造陆期间会被拒绝 |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | 带你继续前往本次会话中尚未带你去过的最近一处，无论以前是否有人去过。离你八个区块以内的会被跳过；对于资源包的地点，则是 128 格以外最近的一处 |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | 带你前往上一处，沿着本次会话把你送去过的地点向后退 |

### 谁可以使用 goto

*命令*

**开放 `goto`。** `/rdplserver` 的每个部分都需要管理员，即等级 3，唯独 `intro`、`team` 和 `round` 例外，任何玩家都可以运行它们，与 1.12.2 一致。三种 `goto` 形式是资源包可以决定的唯一一项：每一种都带有自己的权限等级，资源包或配置可以将其调低，与另外两种以及命令的其余部分互不影响。想让玩家使用 `reset` 的资源包，可以把它放在命令方块或函数里，那样它以等级 3 运行。

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

| 设置 | 它管辖什么 |
| --- | --- |
| `gotoLevel` | `goto <structure>` |
| `gotoNextLevel` | `goto <structure> next` |
| `gotoBackLevel` | `goto <structure> back` |
| `gotoPlaceLevels` | 某一个指名的地点，三种形式全部适用 |

该值是发送者所需的权限等级。默认为 `3`（管理员）。`2` 还允许命令方块，因此资源包可以把跳转放在按钮或压力板上，而不必暴露 `/rdplserver` 的其余部分。`0` 向任何玩家开放。这三项设置彼此独立：例如，让 `next` 对命令方块开放以便进行村庄游览，而 `back` 仍仅限管理员。低于 0 的值按 0 计，高于 4 的按 4 计，并且无论设置如何，管理员总会被提供 `goto` 本身。

`gotoPlaceLevels` 针对单个地点覆盖这三项设置，写成 `name=level` 条目，如上面的示例。name 就是你在 `goto` 之后输入的内容：`village` 或 `mansion` 这类原版名称，或在 `imprint` 条目上用 `locateAs` 注册的名称。匹配时忽略大小写。等级 `4` 高于管理员，会对所有人关闭该地点，这是在 `goto` 其余部分开放的同时隐藏某一个地点的办法。

一个条目为该地点的全部三种形式设定同一个等级。未列出的地点回退到上面的三项设置，而未注册的名称永远不会匹配。Tab 补全遵循同样的规则，因此在 `goto` 之后，发送者只会看到自己实际可以被带去的地点。

这些设置位于 `commands` 分组中，因此配置中的 `control.commands` 决定资源包是否可以设置它们，那里设为 `off` 则无论资源包如何请求，一切都保持为管理员级别。

## 须知

*参考*

- KubeJS 和 CraftTweaker 在 RDPL 之后运行，因此它们的改动仍然优先。
- 配方、战利品表、进度和函数在这里都是游戏自己的数据文件，所以 `/reload` 会采纳对其的编辑，而 `/rdpl reload` 会采纳新文件。
- 已经生成的结构会一直保持加载，直到你离开世界。
- 文件名的大小写很重要。如果你的文件大小写与游戏所请求的不一致，RDPL 仍会加载它，但会发出警告，因为在 Linux 上它根本找不到。
- 在 `rdploader` 中放入 `pack.png` 可为资源包设置图标。没有它则显示 RDPL 的图标。
- 可以用 `config/resourcedatapackloader-common.toml` 中的 `rootDirectory` 选项移动或重命名该文件夹。绝对路径同样可用，且需要重启。
- 指向某个完整原版模型的模型也会继承原版的纹理。`cube_all` 和 `cross` 这类父模型从指向它们的模型获取纹理，没有问题。
- 只要 `tweaks` 类别中的 `privacy` 开启（默认即开启），游戏的遥测和聊天举报就是关闭的：不发送任何内容，不为聊天消息签名，运行本模组的服务器不为任何人保留聊天会话，加入未强制安全聊天的服务器也不会显示警告提示。
- 更改后的资源包选项由其所更改的世界记住；当更改使世界所持有的内容变为未注册时，世界会在再次打开之前备份到游戏的 `backups` 文件夹，若备份失败则保持关闭。

## 出现问题时

*参考*

**请先查看 `logs/rdpl.log`。** RDPL 所做的一切都记录在那里，而不是主日志。进度、战利品表、配方、函数、结构和每一项内容都会连同其来源资源包一并记录，任何格式错误的内容都会连同原因一起记录。

**纹理和其他资源则不同。** 它们被请求得太频繁，无法逐个记录，因此改由 `/rdpl unused` 列出你的资源包中尚无任何东西请求过的文件。请在游戏加载完毕后运行它。路径正确的文件一定会被请求，所以列出的内容通常是拼写错误，但请记住有些文件只在需要时才加载，例如你所玩语言之外的其他语言。

**内部没有 `assets` 或 `data` 目录的 zip 会被跳过，** `rdploader` 中的任何文件夹也一样，日志会说明这一点。顶层是一个把它们包起来的文件夹的 zip，同样会被跳过。

**`/rdpl which minecraft:textures/block/stone.png`** 会准确告诉你是哪个资源包在提供某个文件，以及它遮蔽了什么。

**为 1.12.2 编写的资源包会通过前向移植来读取。** 参见 [为 1.12.2 编写的资源包](#为-1122-编写的资源包)；日志会指明它移动、略去或无法延续的每一个文件。

## 额外功能：原版调整

*参考*

对原版行为的小幅调整，各自在 `tweaks` 配置类别中开关。

| 选项 | 默认值 | 作用 |
| --- | --- | --- |
| `promptLeafDecay` | 开 | 失去树木的树叶会在一秒内凋落，而不是等待随机刻 |
| `lenientPaths` | 开 | 可以在方块下方制作土径，并且在其上方放置方块时土径仍会保留 |
| `unbreakableSpawners` | 关 | 刷怪笼无法被挖掘或炸毁。创造模式仍可将其移除。需要重启 |
| `experimentalWarning` | 关 | 在创建或打开世界时显示游戏的实验性设置警告。关闭时会视作你已点击继续 |
| `privacy` | 开 | 关闭游戏的遥测和聊天举报；参见 [须知](#须知) |
| `darkSplash` | 开 | 带有资源包加载器徽标的深色加载界面；游戏的“单色徽标”选项会在下次启动时开启 |

另有四项位于 `content` 类别而不是 `tweaks`：

| 选项 | 默认值 | 作用 |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | 原版仙人掌能长多高 |
| `caneMaxHeight` | `3` | 原版甘蔗能长多高 |
| `shovelPaths` | 开 | 铲子把标记为 `behavesAs` path 的方块变成土径，潜行则将其还原 |
| `hoeTilling` | 开 | 锄头耕地标记为 `behavesAs` till 的方块 |

**这些都不会影响资源包。** 这些选项只改变 Minecraft 自己的仙人掌、甘蔗、树叶和土径。你的资源包以 `"type": "cane"` 定义的方块带有自己的 `growth` 部分，会长到你给定的任何高度，无论还安装了什么。

### 不可破坏的刷怪笼

*额外功能：原版调整*

`unbreakableSpawners` 让刷怪笼方块拥有基岩的数值：不可破坏的硬度和无物可存的爆炸抗性。无论镐子多好，玩家都无法挖掘它，苦力怕、TNT，以及会 `explodes` 的资源包实体也都无法将其摧毁。创造模式仍可将其移除，就像仍可移除基岩一样，因此资源包作者永远不会被锁在自己的建筑之外。它需要重启，因为方块的数值是在其注册时设定的。

**它针对的是方块，而不是刷怪笼。** 没有针对单个刷怪笼的开关。该选项更改的是 `minecraft:spawner` 本身，因此会一次性影响世界中的每一个刷怪笼：放置刷怪笼的原版结构、模组放置的任何一个，以及你自己的资源包放置的任何一个。你的某个 `.nbt` 模板中的刷怪笼，由 `imprint` 条目放置，就是一个自带方块实体的普通刷怪笼方块，因此该选项一开启它就受到保护。

## 未延续的键

*参考*

1.12.2 资源包可以写、但本版本不读取的内容，以及原因。写了这些键的资源包仍会加载；这些键不起作用。标有 *尚未延续* 的行是在该部分移植之后才加入 1.12.2 的，仍有待延续。其余各行在此引擎上都无法或无须延续。标有 *自 26.3 起* 的行是 26.3 资源包可以写、但本版本无法容纳的内容；日志会在每一项被丢弃时指明。本表会随各条线的推进而保持更新。

| 键 | 位置 | 原因 |
| --- | --- | --- |
| `meta` | 方块、物品、世界生成、方块掉落物 | 自“扁平化”以来 id 不再带有元数据。移植时会让每个 `name:meta` 经过游戏的数据修复器，并丢弃该键 |
| `oreDict` | 方块、物品、熔炉、燃料、实体存储过滤器 | 矿物词典已不复存在。移植时会把它转换为约定标签上的 `tags`，燃料或存储过滤器条目中的则转换为 `tag` |
| `galacticraft` | 实体变种 | 本版本没有 Galacticraft，因此变种没有火箭等级、燃料箱、货物或有效载荷可供设置 |
| `oreDictionary` | 设置 | 矿物词典已不复存在，因此没有可关闭的矿物词典文件。标签承担了它的工作，移植时会根据资源包的 `oreDict` 写出这些标签 |
| `modelMeta` | 方块 | 模型按变种生成，因此没有可用于映射它们的元数据 |
| `disableOverrides`, `tolerateMissingInAdvancements` | 设置 | 数据包通过在同一名称下提供一个配方或进度来替换原版的配方或进度 |
| `#CONSTANT` 物品名称 | 配方 | 配方常量存在于 1.12.2 模组的 `_constants.json` 中，没有资源包携带它，本版本也没有任何模组带有它。请写出该常量所代表的物品或标签 |
| `harvestTool` 为 `pickaxe`、`axe`、`shovel`、`hoe` 和 `sword` 之外的值 | 属性覆盖 | 这里的工具通过方块标签挖掘，而只有这五种有标签。1.12.2 模组自创的工具类别没有标签可写，因此该方块的工具保持不变，并且日志会说明 |
| `careers` | 村民 | 自 1.14 起就没有职业分支了，因此每个职业分支都变成一个独立的村民文件 |
| `career` | 交易、实体 | 没有职业分支；请直接写出职业本身。指明了原版 1.12.2 职业及其职业分支的交易，会转到该职业分支所变成的职业 |
| `gameLoopFunction` | 游戏规则 | 该游戏规则已不存在；`#minecraft:tick` 函数标签会每刻运行一个函数，移植时会写出一个 |
| `id` | 生物群系、维度 | 生物群系和维度以其资源位置识别，绝不用数字 |
| `suffix`, `keepLoaded` | 维度 | 存档文件夹跟随维度的名称。只有主世界有出生点区块，因此必须保持加载的维度要使用 `forceload` |
| `baseHeight`, `heightVariation` | 生物群系 | 地形高度属于噪声设置，而不属于生物群系 |
| `placement.villageSpawn` | 生物群系 | 村民随村庄结构本身一同生成 |
| `rubicWorld`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist`, `verticalCubeLoadDistance`, `cubeGCInterval`, `cubeGenMillisPerRound`, `cubesSentPerTick` | Rubic 世界 | Rubic 世界是 1.12.2 中突破 256 格世界的办法。在这里，维度的 `minHeight` 和 `maxHeight` 决定其大小，由区块系统对其进行流式加载 |
| `rubicHeightLimit` | Rubic 世界 | Rubic 世界的高度上限随之而去；维度的 `minHeight` 和 `maxHeight` 改为决定其大小，没有单独的上限可供提高 |
| `regionCacheLimit` | Rubic 世界 | 它对 Rubic 世界自身已打开的区域文件所做的缓存，随 Rubic 世界一同消失；这里的区块系统自行管理其文件 |
| `skyStone`, `skyShape`, `skyIslands`, `skyThickness`, `skyHeights`, `skyAnimals` | 深层世界、生物群系、洞穴区域 | 空岛位于 Rubic 世界的地形窗口之上，而现在没有 Rubic 世界来容纳它 |
| `deepRavines`, `oreVeins` | 深层世界 | 引擎自身的地形已经延伸到 y 0 以下，并带有自己的峡谷和大型矿脉 |
| `terrainOffset` | 设置 | 没有固定的原版窗口可供平移；维度的 `minHeight` 和 `maxHeight` 设定其底部和顶部 |
| `terrainWorldTypes`, `terrainWorldTypesAreBlacklist` | 设置 | 世界类型在这里就是世界预设，而世界模板会指定它自己的 |
| `biomeSize`, `riverSize`, `dungeonChance`, `waterLakeChance`, `lavaLakeChance`、矿石大小、数量和高度键，以及自定义 `generatorOptions` 的噪声尺度键 | 设置、维度 | 生物群系大小和陆地形状属于噪声设置，特征或矿石的放置频率则属于其自身的放置特征，因此没有哪一个数字能影响它们。编号大于 39 的 `fixedBiome` 所指的生物群系，本版本无法映射 |
| `useWaterLakes`，以及超平坦文本中的 `lake` 和 `dungeon` | 设置、维度 | 自 1.18 起游戏就没有水湖了。平坦世界上的刷怪房随 `decoration` 而来，而不会单独出现 |
| 作为 `flat` 或 `void` 维度 `biomes` 来源的 `inherit` | 维度 | 平坦生成器只容纳一个生物群系，因此这样的维度采用其超平坦文本所指明的那一个，或平原 |
| `flatBedrockDimensions` 或 `voidWorldDimensions` 中另一个模组的维度 | 设置 | 基岩和虚空是写入生成的世界预设和资源包自己的维度文件的；其他模组创建的维度由它自己的文件构建 |
| 作为 `worldType` 的模组世界类型，或 `debug_all_block_states` | 设置 | 世界类型现在是世界预设，而塑造出的世界建立在游戏自己的默认、大型生物群系、放大或平坦噪声之上。调试世界没有可供塑造的地形 |
| `pregenKeepLoaded`, `pregenPauseAbove`, `pregenMillisPerRound`, `pregenRelightSays`, `hurryWritesAbove` | 预生成 | 它们用于调节 1.12.2 的区块写入器和重新照明流程。这里的区块系统在造陆的同时完成光照，并按自己的节奏写入 |
| `readCofhWorldFiles` | 设置 | CoFH World 及其自有的文件格式在此引擎上并不存在。像 1.12.2 已经推荐的那样，把这些文件转译成资源包，仍然是通行之道 |
| `villagePathLamp*` 中的 `name:meta` | 村庄 | id 不带元数据，因此灯方块要用方括号写出其状态，花括号中的方块数据仍可读取 |
| 指明 `shears` 或模组工具类别的 `harvestTool` | 方块 | 这里的工具读取的是方块标签而不是类别名称，因此没有任何东西响应类别名称。请在变种的 `tags` 下写出该模组工具自己的方块标签 |
| `creativeTab` 中另一个模组的标签页名称 | 方块、物品、流体 | 标签页现在由其 id 识别，因此单纯的名称会被读作资源包自己的标签页。请用 id 指明该模组的标签页，例如 `modid:main` |
| `/rdpl reload <group>` | 命令 | 游戏一次性重载所有资源，因此纹理、模型、语言、音效和着色器无法单独重载。`/rdpl reload` 或 F3+T 会把它们全部重载 |
| `modernChestPlacement` | 原版调整 | 自 1.13 起游戏就是这样合并箱子的：仅当两个箱子朝向相同时，箱子才会与旁边的单个箱子相连，潜行则使其保持单个 |
| `loadingScreenPercent` | 设置 | 游戏自己的世界加载界面已经显示出生点区域准备好了多少 |
| `disableOptimizations` | 设置 | 它用于停用 1.12.2 的预生成和生成优化，这些优化是为那个引擎编写的，在这里没有对应之物 |
| `fixTinkersModelErrors` | 设置 | 它用于压制 1.12.2 版本的 Tinkers' Construct 和 Construct's Armory 为每件工具、部件和盔甲所记录的模型错误。该修复深入到那些版本之中，因此在这里无可作用 |
| `achievement.` 条件 | 计分、函数 | 成就变成了进度，而进度不保留可供计数的分数。`stat.` 条件会被读作它所变成的统计信息 |
| `toggledownfall`, `stats` | 函数 | 这些命令已不存在。`weather` 指明要设置的天气，`execute store` 保存命令的结果 |
| `gamerule gameLoopFunction`，以及资源包自创的游戏规则 | 函数 | 存在哪些游戏规则由游戏决定。移植时会把游戏规则文件的 `gameLoopFunction` 转换为 `#minecraft:tick` 标签，但设置它的命令行会保留，留待你来移除 |
| 数据值为 `-1` 或 `*`、现在变成了多个方块或物品的名称 | 函数：`clear`、`testforblock`、`execute ... detect`、`fill ... replace`、`clone ... filtered` | 现在一次检测只指向一个方块或物品。请写出所指的那一个，或使用 `#minecraft:wool` 之类的标签 |
| 写成 `name=value` 对、但并非该方块完整 1.12.2 状态的方块状态，以及另一个模组的方块或物品上的数据值 | 函数 | 数据修复器只认识完整的 1.12.2 状态，而另一个模组的元数据没有可对应的扁平化名称 |
| `footstep` 和 `take` 粒子、`locate Temple`、带多个目标的 `spreadplayers` | 函数 | 这些粒子已不存在，神殿现在是四种结构，而 `spreadplayers` 只接受一个目标参数 |
| `debug_functions` | 噪声设置，*自 26.3 起* | 26.2 及更早版本没有调试密度函数，因此该列表会被丢弃 |
| `aquifers` 中的 `exclusion` 和 `surface_level` | 噪声设置，*自 26.3 起* | 26.2 及更早版本把两者都内置了，因此资源包自己的会被丢弃 |
| 指明了 `noise_router` 气候轴之外任何内容的 `spawn_target` 条目 | 噪声设置，*自 26.3 起* | 较早的出生目标只涵盖五个气候轴，因此该条目会被丢弃 |
| 通过 `material_rule` 放置的矿脉 | 噪声设置，*自 26.3 起* | 26.2 无法通过材质规则放置矿脉，因此它们会被丢弃 |
| 洞穴雕刻器自己的 `count`、`thickness`、`weird_thickness_bias` 或 `start_vertical_radius_multiplier` | 雕刻器，*自 26.3 起* | 26.2 无法设置它们，因此使用原版主世界或下界的洞穴形状 |
| 作为修饰器的 `creature_world_gen_spawn_probability`，以及追加到其维度值上的属性 | 生物群系，*自 26.3 起* | 26.2 只接受普通数值：保留默认生成几率，并且生物群系自己的值会取代维度的值 |
| 不相同的 x 和 z 偏移，以及设为 false 的 `normalize` | 放置特征、噪声，*自 26.3 起* | 26.2 以同一个量扩散两个轴，因此两者都使用 x 偏移，并且总是对噪声进行归一化 |
| 26.2 没有的特征类型，或由 26.2 没有的方块构建的 26.3 特征 | 特征、生物群系，*自 26.3 起* | 这样的特征不放置任何东西，而生物群系会略去没有 26.2 对应物的 26.3 放置特征 |
| `straw_bed_rule`，以及床规则中的 `destroy_on_leave` | 环境属性，*自 26.3 起* | 26.2 两种规则都没有，因此两者都会被丢弃 |
| 陶罐装饰物中带数量或组件的物品 | 数据文件、函数，*自 26.3 起* | 26.2 读取的是普通物品，因此数量和组件会被丢弃 |
| `compute`、`posteffect`、`item fill`、`item override` 和 `execute if slots`，以及 `swing` 的动画 | 函数，*自 26.3 起* | 26.2 没有这些命令，因此该行会被丢弃；`swing` 行会保留并作普通挥动 |
| 不是 `up` 的 `shade_direction_override`，以及 `trim_overrides` | 模型、装备资产，*自 26.3 起* | 26.2 对这样的元素按常规着色，并从纹饰材料的 `override_armor_assets` 读取各装备的纹饰调色板 |
| 酿造出的物品上的组件，以及用于酿造的物品标签或列表 | 酿造配方，*自 26.3 起* | 26.2 的酿造配方两者都无法指明：组件会被丢弃，而从标签或列表酿造的配方则整条丢弃 |
| 26.2 没有对应形式的数值提供器，以及不为 0 的回退值 | 战利品表、谓词、物品修饰器，*自 26.3 起* | 该提供器变为 0，缺失的分数或存储的数字回退为 0 |
| 除 `blocks` 和 `state` 之外的方块条件测试、26.2 只读取一个的 id 集合、谓词或物品修饰器的标签、指向具名结构的探险家地图 | 战利品表、谓词、物品修饰器，*自 26.3 起* | 该测试被丢弃，只保留第一个 id，标签被读作它所指的那一个 id，并且地图指向宝藏结构 |

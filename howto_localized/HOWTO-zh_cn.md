# Resource Data Pack Loader

**一个文件夹，即可覆盖 Minecraft 或任何模组提供的内容，用 JSON 定义新内容，并控制世界的生成方式；在每个世界、客户端与服务器上均生效，无需玩家开启任何开关。**

十四个可直接运行的示例。把其中任意一个直接放进 `rdploader`，看看每个文件是怎么写的。

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) 涵盖大部分功能：方块、物品、生物群系、一个维度、一个世界模板以及所有世界生成形状。
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) 把主世界变成一片空无一物的虚空，世界生成的内容悬浮在半空中，每个高度带一种形状，方便单独观察每一种。
- [RDPLExampleVeinShapes.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapes.zip) 在普通主世界中放入三种矿脉，每种对应一种矿脉样式（普通、条带和管状），各有富矿、普通和贫矿三个等级，并在其上方的地表放置了几个标记方块，供你据此勘探。
- [RDPLExampleVeinShapesVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapesVoid.zip) 把同样的三种矿脉样式悬浮在空无一物的虚空中，让每种形状都能完整地看清。
- [RDPLExampleDeepWorld.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleDeepWorld.zip) 把主世界变成一个 rubic 世界，在原版世界之下生成 256 格、之上生成 128 格：包括深层石头混合、现代噪声洞穴、峡谷、条带状矿脉、三个可向下探索的洞穴区域，以及由同一套噪声切割而成、悬在头顶的浮空岛。
- [RDPLExampleContainers.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleContainers.zip) 添加了带有物品栏的方块与手持物品，格数从三格到允许的最大值各种尺寸齐备，配有战利品表、从原版贴图集中染色而来的箱子模型、全部以像素图绘制的纹理，以及两个可在 Baubles 中佩戴的小袋。
- [RDPLExampleMegaCity32.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity32.zip) 生成一个超平坦世界，其中有一座刻意造得极其庞大的村庄，扩展到一千个地块并固定在原点；街道装饰成混凝土道路、人行道、虚线中心线和路灯，街道之下有下水道，其下是两条带车站的地铁线，另有一条穿过城镇的铁路，建筑则由四种尺寸、三种外立面的结构地图组合而成，而不是原版房屋。
- [RDPLExampleMegaCity64.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity64.zip) 是同一座城市，建在一个顶部高度为 512、云层抬升到 384 的 rubic 世界上，因此高塔比街道高出 256 格，并且每个街区随机取 16、32 或 64 的方块深度，使粗网格与细网格混合在一起。这座城市没有边界：世界上的每个街区都是一座独立的城镇，无论走到哪里，街道都会一直延伸下去。
- [RDPLExampleCityCustomMap.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleCityCustomMap.zip) 用一张城市地图来绘制同一座城市，而不是随机生成：一张每格 48 个方块的字符网格，配合调色板指明街道、广场、小巷以及按权重选取的建筑，因此街区布局由手工排定。
- [MCTKamikazeDemo.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/MCTKamikazeDemo.zip) 让四个阵营在永夜笼罩的基岩竞技场中互相对抗：每一方都是真正的原版计分板队伍，其生物在生成时加入；每击杀一只其他阵营的生物，本方就得分；一个回合在两分钟后以一张卡片结束，三个回合构成一场比赛。
- [RDPLExampleRaid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleRaid.zip) 把一个平坦世界布置在一座村庄周围，村庄的水井是一座钟亭，玩家加入时会获得不祥之兆：共五波敌人，包括原版的灾厄村民、一名女巫，以及资源包自带的掠夺者、袭击队长、掷斧者和劫掠兽；不祥之兆与村庄英雄是资源包自带的效果并配有像素图图标，另有一种能让不祥之兆重新出现的饮品，以及结束袭击的函数。
- [RDPLExampleGalacticraft.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGalacticraft.zip) 需要 Galacticraft，在星图上加入一个自己的星系，其中有一颗乘坐二级火箭即可抵达的行星，拥有自己的天空、重力、昼长、天气和大气；还有一颗只能观赏、无法降落的行星，并且在搭配 GalaxySpace 时，在天仓五周围多出一颗冰冻行星。
- [RDPLExampleGameHall.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGameHall.zip) 把一个平坦世界变成游戏大厅：资源包自带的加权骰子、一副会自动重洗的生物牌和一副在洗牌前一直为空的命运牌、一个右键即可掷 2d6 的骰盅、与庄家轮流掷骰、以抽签决出平局的骰子对决，以及用生物作棋子、与电脑对弈的国际象棋和跳棋。
- [RDPLExampleColony.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleColony.zip) 在关闭介绍后自动建起一座工作院落，让村民工人承担每一种工作订单：随镐子提升作业范围的矿工、把砍下的木头留在身上的伐木工、始终保持固定小麦库存的农夫，以及搬运圆石的搬运工；每个订单都可以通过告示牌、工具、雇佣或库存箱来开启。
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
- [为 1.20.1、1.21.1 和 26.x 编写的资源包](#为-12011211-和-26x-编写的资源包)

**方块与物品**
- [方块](#方块)
- [容器](#容器)
- [模型、方块状态与纹理](#模型方块状态与纹理)
- [按类型划分的方块状态](#按类型划分的方块状态)
- [让原版正确对待你的方块](#让原版正确对待你的方块)
- [物品](#物品)
- [流体](#流体)
- [材料、标签页、音效、矿物词典](#材料标签页音效矿物词典)
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
- [工作订单](#工作订单)
- [暴露设置](#暴露设置)

**世界**
- [世界模板](#世界模板)
- [游戏规则](#游戏规则)
- [生物群系](#生物群系)
- [维度](#维度)
- [Galacticraft 天体](#galacticraft-天体)
- [传送门与门](#传送门与门)
- [Rubic 世界](#rubic-世界)
- [深层世界](#深层世界)
- [洞穴区域](#洞穴区域)

**生成世界**
- [世界生成条目](#世界生成条目)
- [形状](#形状)
- [扩散](#扩散)
- [结构地图](#结构地图)
- [村庄地块](#村庄地块)
- [城市布局地图](#城市布局地图)
- [连绵城市](#连绵城市)
- [回溯生成](#回溯生成)
- [预生成](#预生成)

**游戏模式**
- [世界介绍](#世界介绍)
- [队伍](#队伍)
- [计分](#计分)
- [袭击](#袭击)
- [卡片](#卡片)
- [骰子与牌堆](#骰子与牌堆)

**控制**
- [控制层](#控制层)
- [各分组的作用](#各分组的作用)

**其他模组**
- [Universal Tweaks](#universal-tweaks)
- [Mo' Villages](#mo-villages)
- [CoFH World](#cofh-world)
- [Lost Cities](#lost-cities)
- [Blast Plaster 集成](#blast-plaster-集成)
- [墓碑模组](#墓碑模组)

**参考**
- [值列表](#值列表)
- [文件夹列表](#文件夹列表)
- [命令](#命令)
- [须知](#须知)
- [出现问题时](#出现问题时)
- [额外功能：原版调整](#额外功能原版调整)
- [额外功能：修复 JEI 插件冲突](#额外功能修复-jei-插件冲突)
- [额外功能：减少启动错误](#额外功能减少启动错误)

---

# 快速入门

## 它是什么

*快速入门*

Resource Data Pack Loader（RDPL）读取唯一的一个文件夹 `rdploader`，并完成三项工作：

- **覆盖。** 文件夹中的文件会替换游戏或模组本来会加载的那个文件。没有开关，无需逐个世界设置，也无需玩家启用任何东西。
- **新内容。** 通过 JSON 定义注册方块、物品、流体、生物群系、维度、药水和村民。无需 Java，无需 jar。
- **控制。** 屏蔽矿石、生物群系、结构或配方的生成，压平基岩，设置生成率，把主世界变成虚空，设定世界默认值。

## 文件放在哪里

*快速入门*

本指南中的每个路径都是从 `assets/` 起写的，因此对于命名空间为 `mypack` 的资源包，`<namespace>/blocks/*.json` 在磁盘上就是 `assets/mypack/blocks/ruby_ore.json`。每一节都会在标题下重复写出自己的路径，并说明该路径会变成什么。

| 路径 | 内容 |
| --- | --- |
| `<namespace>/blocks/*.json` | 方块定义。[方块](#方块) |
| `<namespace>/items/*.json` | 物品定义。[物品](#物品) |
| `<namespace>/fluids/*.json` | 流体，附带方块和桶。[流体](#流体) |
| `<namespace>/materials/*.json` | 工具与盔甲材料。[材料、标签页、音效、矿物词典](#材料标签页音效矿物词典) |
| `<namespace>/tabs/*.json` | 创造模式标签页。[材料、标签页、音效、矿物词典](#材料标签页音效矿物词典) |
| `<namespace>/sounds/*.json` | 音效事件。[材料、标签页、音效、矿物词典](#材料标签页音效矿物词典) |
| `<namespace>/oredict/*.json` | 矿物词典名称。[材料、标签页、音效、矿物词典](#材料标签页音效矿物词典) |
| `<namespace>/biomes/*.json` | 生物群系定义。[生物群系](#生物群系) |
| `<namespace>/worldgen/*.json` | 生成什么，以及在哪里生成。[世界生成条目](#世界生成条目) |
| `<namespace>/caveregions/*.json` | 绘制在地下的命名区域。[洞穴区域](#洞穴区域) |
| `<namespace>/dimensions/*.json` | 维度定义。[维度](#维度) |
| `<namespace>/celestial/*.json` | Galacticraft 星图用的星系与天体。[Galacticraft 天体](#galacticraft-天体) |
| `<namespace>/worldtemplates/*.json` | 把整个世界的设置写在一个文件里。[世界模板](#世界模板) |
| `<namespace>/worldintro/*.json` | 玩家进入世界时显示的页面。[世界介绍](#世界介绍) |
| `<namespace>/gates/*.json` | 传送门与维度的条件。[传送门与门](#传送门与门) |
| `<namespace>/gamerules/*.json` | 新世界的游戏规则。[游戏规则](#游戏规则) |
| `<namespace>/teams/*.json` | 原版计分板上的阵营，以及加入阵营的对象。[队伍](#队伍) |
| `<namespace>/scoring/*.json` | 目标、积分以及比赛如何结束。[计分](#计分) |
| `<namespace>/raids/*.json` | 玩家把不祥之兆带入村庄时来袭的各波敌人。[袭击](#袭击) |
| `<namespace>/entities/*.json` | 基于已有实体构建的实体变种。[实体变种](#实体变种) |
| `<namespace>/hardness/*.json` | 针对方块分组的挖掘时间与爆炸倍率。[硬度分组](#硬度分组) |
| `<namespace>/exposures/*.json` | 对靠近指定方块、携带指定物品或处于指定维度的玩家造成暴露的危险。[暴露设置](#暴露设置) |
| `<namespace>/orders/*.json` | 实体变种在箱子处为玩家完成的工作。[工作订单](#工作订单) |
| `<namespace>/overrides/<target>/<name>.json` | 就地修改现有方块、物品和药水类型的属性。[属性覆盖](#属性覆盖) |
| `<namespace>/villages/*.json` | 村庄可以建造的地块。[村庄地块](#村庄地块) |
| `<namespace>/pathintersects/*.json` | 绘制在村庄道路交汇处的图案。[村庄道路](#村庄道路) |
| `<namespace>/structuremaps/*.json` | 在网格上组合成一座大型建筑的模板。[结构地图](#结构地图) |
| `<namespace>/citymaps/*.json` | 手绘的街道规划图，村庄据此布局而不是自行生长。[城市布局地图](#城市布局地图) |
| `<namespace>/portalframes/*.json` | 玩家可以搭建并点燃的框架。[传送门框架](#传送门框架) |
| `<namespace>/blastplaster/*.json` | 爆炸之后 Blast Plaster 按维度所做的事。[Blast Plaster 集成](#blast-plaster-集成) |
| `<namespace>/structures/*.nbt` | 模板，用于树苗、`imprint` 和模组覆盖。[可以覆盖的内容](#可以覆盖的内容) |
| `<namespace>/recipes/*.json` | 合成配方，新增或替换。[可以覆盖的内容](#可以覆盖的内容) |
| `<namespace>/recipe_removals/*.json` | 按名称、命名空间或产物删除的配方。[可以覆盖的内容](#可以覆盖的内容) |
| `<namespace>/disabled/*.json` | 被移出游戏的方块与物品。[禁用的方块与物品](#禁用的方块与物品) |
| `<namespace>/furnace/*.json` | 新增与移除的熔炉配方。[熔炉配方与燃料](#熔炉配方与燃料) |
| `<namespace>/fuels/*.json` | 燃烧时间。[熔炉配方与燃料](#熔炉配方与燃料) |
| `<namespace>/brewing/*.json` | 酿造台配方。[药水、药水类型与酿造](#药水药水类型与酿造) |
| `<namespace>/potions/*.json` | 药水效果。[药水、药水类型与酿造](#药水药水类型与酿造) |
| `<namespace>/potion_types/*.json` | 由这些效果构成的瓶装药水。[药水、药水类型与酿造](#药水药水类型与酿造) |
| `<namespace>/villagers/*.json` | 村民职业。[村民与交易](#村民与交易) |
| `<namespace>/trades/*.json` | 各职业收购与出售的物品。[村民与交易](#村民与交易) |
| `<namespace>/loot_tables/*.json` | 战利品表，被替换。[可以覆盖的内容](#可以覆盖的内容) |
| `<namespace>/loot_injections/*.json` | 添加到已有战利品表中的一个池。[可以覆盖的内容](#可以覆盖的内容) |
| `<namespace>/block_drops/*.json` | 资源包并不拥有的方块的额外或替换掉落物。[方块掉落物](#方块掉落物) |
| `<namespace>/anvils/*.json` | 铁砧为指定物品附加的附魔、由此获得的进度，以及在此之前的锁定。[铁砧操作](#铁砧操作) |
| `<namespace>/cards/*.json` | 由触发器显示的屏幕卡片，以及本模组自己发出的消息。[卡片](#卡片) |
| `<namespace>/dice/*.json` | 资源包的带权重骰子、卡牌牌堆、谁能听到掷骰，以及结果的文字。[骰子与牌堆](#骰子与牌堆) |
| `<namespace>/games/*.json` | 以生物为棋子的棋盘游戏：棋盘、棋子及其走法，以及结果的奖励。[棋盘游戏](#棋盘游戏) |
| `<namespace>/player_loot/*.json` | 玩家死亡时抽取的战利品表。[玩家战利品](#玩家战利品) |
| `<namespace>/advancements/*.json` | 进度。[可以覆盖的内容](#可以覆盖的内容) |
| `<namespace>/functions/*.mcfunction` | 函数文件。[可以覆盖的内容](#可以覆盖的内容) |
| `<namespace>/registry_remap/*.json` | 旧名称到新名称的映射。[注册表重命名](#注册表重命名) |
| `<namespace>/texts/*.txt` | 纯文本文件，供世界介绍使用。[世界介绍](#世界介绍) |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | 常规的资源文件夹。[模型、方块状态与纹理](#模型方块状态与纹理) |

## 如何阅读表格

*快速入门*

每个文件都是标准 JSON。一个有代表性的世界生成条目：

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

本文档中的键表会说明一个键是否必填、它存放什么，以及省略时的默认值。无法识别的值会被记入日志并换成默认值，不会使游戏崩溃。全文使用的值类型：

| 当表格写的是 | 你要写 |
| --- | --- |
| int | `8` |
| int, ticks | `100`（20 刻 = 1 秒） |
| int or range | `8`，或用 `{ "min": 4, "max": 12 }` 在两者之间随机取值 |
| 0 to 15, 1 to 100 and such | 落在该范围内的整数 |
| float | `0.5` |
| boolean | `true` 或 `false` |
| string | `"words in quotes"` |
| block name, item name | `"minecraft:stone"`，元数据作为第三部分：`"minecraft:stone:3"` |
| `namespace:name` | `"mypack:ruby_ore"` |
| biome name, sound name, tab name | 同样带引号的 `namespace:name` 形式 |
| hex color | 六位十六进制数字，`"A0C8FF"`，`#` 可有可无 |
| texture path | `"mypack:blocks/ruby_ore"` |
| list of ints | `[0, -1]` |
| list of block names | `["minecraft:stone", "minecraft:andesite"]` |
| list of biome names | `["minecraft:extreme_hills", "mypack:ruby_hills"]` |
| list of dictionary types | `["MOUNTAIN", "FOREST"]` |
| list of mod ids or pack namespaces | `["quark", "mypack"]` |
| list of objects | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`，各键见该对象自己的表 |
| object | `{ "type": "cluster" }`，各键见其自己的表 |
| object of role to biome, of variant name to variant | 键是前者，值是后者：`{ "ocean": "mypack:ruby_ocean" }` |

大多数定义还接受 `requires`，这是一个由模组 id 或资源包命名空间组成的列表，列出的内容必须存在，否则该文件会被跳过。

## 唯一的规则

*快速入门*

打开 jar，找到你想修改的文件，从 `assets` 起复制它的路径：

```
assets/minecraft/textures/blocks/iron_ore.png        (in the Minecraft jar)
rdploader/assets/minecraft/textures/blocks/iron_ore.png    (your override)
```

`assets` 之后的路径始终与 jar 内的路径完全一致。没有任何东西被重命名或移动。

## 组织资源包

*快速入门*

散装文件可放在 `rdploader/assets/<namespace>/` 下。也可以用 zip 来分组。`rdploader` 中的文件夹永远不会被当作资源包：它会被跳过并在日志中给出警告，因此请先把资源包打成 zip 再放进去。

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png
rdploader/MyTextures.zip
```

**放错文件夹的资源包。** 启动时，在读取 `rdploader` 之前，RDPL 会检查游戏的 `resourcepacks` 文件夹，并把找到的每个 RDPL 资源包 zip 移入 `rdploader`。当 zip 中包含 RDPL 定义文件时，它就是 RDPL 资源包，例如 `assets/<namespace>/blocks/`，或者对于现代资源包，`data/<namespace>/blocks/`。普通资源包留在原处。`rdploader` 中已有同名 zip 时，该 zip 会留在原处，位于文件夹中的 RDPL 资源包也一样；两种情况都会给出警告。每一次移动都会写入 `logs/rdpl.log`。被移走的资源包不再出现在资源包列表中，此后 RDPL 会从 `rdploader` 加载它。

**优先级。** 当两个资源包含有同一个文件时，在名称前加上 `RDPL` 和一个数字作为前缀；数字越大加载越晚，也越优先：

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

不区分大小写；数字后面的空格、连字符或下划线可有可无；前缀不会出现在显示名称中。没有前缀的资源包最先加载，并输给任何带编号的资源包。优先级同样决定世界生成条目的顺序，当一个资源包铺设的方块会被另一个资源包替换时，这一点很重要。

**禁用资源包**，只需在其名称后追加 `.disabled`。

**一个 zip 适用于所有版本。** zip 中可以为它所支持的每个 Minecraft 版本各带一个 `versions/<version>/` 文件夹：`versions/1.12.2/`、`versions/1.20.1/`、`versions/1.21.1/`，在 26.x 上则是它所运行的确切版本，`versions/26.1.2/`、`versions/26.2/` 或 `versions/26.3/`。每个文件夹的布局都与该版本资源包的根目录相同，包括 `pack.mcmeta`。当前运行版本的文件夹下的文件，会代替根目录中相同路径的文件被读取；根目录由所有版本共享，其他版本的文件夹永远不会被读取。把所有版本读取方式相同的内容放在根目录，只把有差异的部分放进版本文件夹，一个 zip 就能在全部四条线上加载。

**资源包可在版本之间转换。** 加载为另一条版本线编写的资源包时，会在首次加载时以同样的方式对其转换，并把变更的内容写入它自己的 `versions/<version>/` 文件夹，如上所述；这在加载时自动发生，包括 `/rdpl reload` 或 `/rdplserver reload` 触发的那次加载，没有单独的命令。任意两个版本之间都可以双向转换，因此 1.12.2、1.20.1、1.21.1 或 26.x 的资源包可以在其他任何一条线上加载。26.3 的资源包同样能在每条线上加载，在较旧的线上通过 26.2 格式读取，而较旧的资源包也能在 26.3 上加载。一方有而另一方容纳不了的内容会被丢弃，日志会逐一指明：从 26.3 向下转换时，被丢弃的包括调试密度函数、资源包自己的含水层排除和地表高度，以及 26.2 没有的命令；向上转换到 26.3 时，被丢弃的包括生成目标的 `depth` 和 `offset`，以及噪声路由器的矿脉键。在 1.12.2 上，现代资源包的原版世界生成内容（如其生物群系、特征和噪声设置）以及其 `neoforge` 文件夹也会被略去，因为 1.12.2 对这两者都没有对应物。

## 资源包：谁优先

*快速入门*

默认情况下，RDPL 文件位于玩家所选资源包之上，因此资源包无法覆盖它们。在 `RDPL` 前缀之后加上 `O` 或 `N`，即可逐个资源包决定：

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

没有字母的资源包遵循配置选项 `overrideResourcePacks`。`/rdpl list` 会标出具有覆盖权的资源包。字母必须位于前缀末尾（其后跟空格、连字符、下划线或什么都没有），因此 `RDPLOverhaul` 是一个名为 `Overhaul` 的资源包，而不是带 `O` 标志。

---

# 资源包如何工作

## 定义如何工作

*资源包如何工作*

资源包的文件夹有两种用途：一些用来描述新事物，其余的用来替换游戏或模组已有的文件，后者见[可以覆盖的内容](#可以覆盖的内容)。对于第一种，路径就是标识：位于 `assets/mypack/blocks/ruby_ore.json` 的文件会注册一个名为 `mypack:ruby_ore` 的方块。

注册发生在 Forge 提供的最低优先级，所以如果真正的模组注册了同名内容，模组获胜，你的文件会被忽略。这里没有任何东西可以取代模组。

**界线在哪里。** 凡是需要自己的方块实体、GUI、物品栏或逐刻逻辑的东西，都需要真正的模组。除此之外的一切都可以做。

### 你的命名空间就是你的模组

*定义如何工作*

你选择的命名空间，在一切实际用途上都相当于一个模组 id。它不会作为模组被加载，也永远不会出现在模组列表中，但所有读取模组 id 的地方都会读取你的命名空间：

- 注册名形如 `mypack:ruby_ore`，与模组的完全一样，并且会写入每一个包含它们的存档。
- 配置中的矿石、生物群系、生成器和配方白名单都会匹配它，因此 `oreWhitelist = mypack` 会保留你的矿石和其他所有人的方块。
- `/rdpl which`、`/rdplserver oregen` 和各类报告都按它分组。
- JEI、矿物词典和其他模组的查询以同样的方式看待它。

所以在一开始选定一个名称，之后永远不要更改。重命名命名空间会使世界中已经放置的一切变成孤儿，就像模组更改自己的 id 一样，这正是 `registry_remap` 用来修复的问题。

这是双向的：`requires` 既接受已安装的模组 id，也同样接受资源包命名空间，因此一个资源包可以依赖另一个，并在后者未安装时被跳过。

**缺少模组会让游戏停止，就像模组自己的依赖一样。** 你的资源包中任何位置的 `requires` 所列出的每个模组 id，都会在任何内容加载之前，作为本模组的依赖交给 Forge。如果有一个没有安装，你会在客户端或专用服务器上看到标准的“缺少模组”界面，列出所需的模组，期间不会生成或注册任何东西。

缺少的*资源包*则不同。资源包命名空间不是模组，因此永远不会触发那项检查，该定义会被跳过，`logs/rdpl.log` 会写入一行说明缺少了什么，游戏继续运行。如果你期望的某个方块没有出现在创造模式标签页中，首先应该查看的就是那行日志。

`requires` 只接受裸 id。没有版本范围语法，所以它只能说明某个模组必须存在，而不能说明是哪个版本。

本模组自己的两个 id，`resourcedatapackloader` 和 `resourcedatapackloader_mixin`，是保留的。在它们之下定义内容会被忽略并记入日志，因为那会宣称拥有本模组所注册的东西。覆盖本模组自己的资源仍然没有问题，只是不能在那里注册内容。

下面的每个表都遵循[如何阅读表格](#如何阅读表格)中的约定。

大多数定义还接受 `requires`，这是一个由模组 id 或资源包命名空间组成的列表，列出的内容必须存在，否则该文件会被跳过。

### 资源包选项

*定义如何工作*

资源包可以在 `assets` 旁边带一个 `config` 文件夹，其中存放由真/假选项及其默认值构成的 JSON 文件：

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

顶层带有 `"hide": true` 的文件，会让该资源包的选项完全不出现在选项界面和生成的文件中，而这些选项仍会按默认值控制内容。有两种情况需要这样做：尚未准备好发布的内容，以及模板资源包，其选项只是把各定义串联起来的机制，而不是任何人应该去做的选择。去掉该键即可发布它们。这也可以逐个选项使用：选项对象内部的 `"hide": true` 只隐藏那一个选项，这样已完成的资源包可以带有一个控制未完成内容的开关，或模板闸门，而两者都不会显示出来：

    { "enablePackB": { "default": false, "hide": true } }

由于隐藏的选项无法被切换，默认值为 true 的隐藏选项实际上是被强制开启的，适用于必须通过选项机制接入、但并不是一项选择的内容。

选项也可以是带有描述的对象，描述会显示在选项界面中该名称的下方：

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| 选项名称 | 是 | 布尔值，或对象 | | `true` 或 `false` 是该选项的默认值。对象则带有下面三个键 |
| 顶层的 `hide` | 否 | 布尔值 | `false` | 让该资源包的选项完全不出现在选项界面和生成的文件中，而它们仍按默认值控制内容 |
| `default` | 是 | 布尔值 | | 用户更改之前该选项的值。没有布尔值 `default` 的对象会被忽略，并给出警告 |
| 选项内部的 `hide` | 否 | 布尔值 | `false` | 隐藏这一个选项，因此它无法被切换，保持其默认值 |
| `description` | 否 | 字符串 | 无 | 显示在选项界面中该选项名称的下方 |

启动时，资源包的选项文件会合并成一个以资源包命名、归用户所有的真实配置文件，即 `rdploader/config/PackA.json`，它按资源包的默认值创建，并在资源包更新时合并，因此新增选项会加入，而不会触及用户已经设置的内容。更改在下次启动游戏时生效。选项只属于有名称的资源包，也就是 zip，因为生成的文件以资源包命名；`rdploader/assets` 下的散装文件没有资源包名称，也不带选项，所以如果散装内容需要开关，请把它打包进一个有名称的资源包 zip。

任何定义的 `requires` 列表都可以用 `config:` 条目指定一个选项：`"requires": ["config:enableTestingContent"]` 仅在该选项为 true 时才注册该内容，效果与缺少模组而跳过它完全相同。不带资源包名的写法会检查每个资源包的文件，并且所有定义了它的资源包必须一致；`"config:PackA:enableTestingContent"` 则指定某一个资源包。没有任何资源包定义的选项视为 false，并且只警告一次。

`file:` 条目以游戏文件夹下某个文件或文件夹是否存在作为条件，用于把内容与 RDPL 自身资源包之外的东西挂钩，比如另一个模组的资源包：`"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` 仅在恰好安装了该文件时才注册内容。路径相对于游戏文件夹，始终使用正斜杠，且不能包含 `..`。

### 继承定义

*定义如何工作*

方块或物品定义可以用 `"inherits"` 从同类的另一个定义出发，指定任意变种的注册名，然后覆盖不同的部分：

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "meta": 0, "hardness": 4.0 } } }

子定义会复制父级文件和所指定变种的每一项数值，文件顺序无关紧要，链条按先父后子的顺序解析，出现循环或缺少父级时会记入日志，并保持子定义原样。子定义写出的字段会替换继承来的值；嵌套的变种属性逐项覆盖，但像 `requires` 这样的列表是整体替换，所以要写出完整想要的列表。方块只能继承方块，物品只能继承物品。

### 方块与物品模板

*定义如何工作*

父级可以是一个永远不会进入游戏的纯模板，因为继承读取的是定义文件本身，而不是已注册的内容。把模板放在一个被强制关闭的隐藏选项之后，它就不会注册任何东西，而它的数值仍可被继承：

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

模板永远不会注册，而 `jacks_ore` 会带着模板的材料、音效、工具、标签页、经验掉落和抗性注册，只覆盖硬度。子定义必须写出自己的 `requires`，这里清空为空列表，否则它会继承父级的 `requires`，并随父级一同消失。

## 可以覆盖的内容

*资源包如何工作*

- **模组 assets 文件夹中的任何内容**，包括纹理、模型、方块状态、语言文件、音效、字体、闪烁标语、指南书、手册
- **进度与战利品表**，在服务端处理，因此在专用服务器上同样有效
- **配方**，替换模组的配方或添加你自己的
- **结构模板**，即模组用于生成建筑的 `.nbt` 文件，位于 `<namespace>/structures/` 下
- **函数**，即位于 `<namespace>/functions/` 下的 `.mcfunction` 文件
- **注册表重命名**，在模组重命名方块或物品时让旧世界继续可用
- **配方移除**，按名称、命名空间或产物删除合成配方
- **禁用的方块与物品**，把任何方块或物品移出游戏，见[禁用的方块与物品](#禁用的方块与物品)
- **战利品注入**，向战利品表添加一个池，而不是替换整张表
- **玩家战利品**，在玩家死亡时抽取一张战利品表，叠加在其携带的物品之上，或取而代之
- **方块掉落物**，增加或替换任何方块在被玩家破坏时的掉落物
- **现有方块、物品和药水的属性**，包括硬度、光照、堆叠上限、任意物品的食物属性、药水的效果，见[属性覆盖](#属性覆盖)
- **矿物词典名称、熔炉配方、燃料燃烧时间、创造模式标签页和音效事件**

RDPL 适合替换一两个配方，而你自己内容的配方应当添加在随附的资源包中。若要对整个整合包的配方进行全面控制，CraftTweaker 和 GroovyScript 是更好的选择；这里的文件仍会完全替换原文件，所以如果只想更改一种原料或去掉一条战利品条目，请使用它们。

## 服务端资源包

*资源包如何工作*

资源包可以只放在服务器上，让玩家使用纯原版客户端，但有一个约束：**其中任何内容都不能注册任何东西**。两个模组 id 都接受任何远端；由资源包自己决定。原版客户端使用它自带的注册表进行游戏，所以向注册表添加内容的资源包必须两端都有。

| 只放服务端就够 | 客户端也需要该资源包 |
| --- | --- |
| `worldgen`、`worldtemplates`、`gamerules`、`structures`、`caveregions` | `blocks`、`items`、`fluids`、`materials` |
| `villages`、`pathintersects`、`structuremaps`、`citymaps` | `potions`、`potion_types`、`sounds`、`tabs` |
| `recipes`、`recipe_removals`、`furnace`、`fuels`、`brewing`、`oredict`、`disabled` | `biomes`、`dimensions`、`portalframes` |
| `loot_tables`、`loot_injections`、`block_drops`、`anvils`、`player_loot`、`advancements`、`functions` | `villagers` |
| `gates`、`cards`、`registry_remap`、`exposures`、`hardness`、`overrides`、`trades`（用于客户端已知的职业：原版的，或两端都有的模组的） | `entities`、`worldintro`、`texts`（介绍永远不会显示给原版客户端） |
| `teams`、`scoring` | `models`、`blockstates`、`textures`、`lang`（客户端文件夹，没有客户端时请不要放） |
| 整个控制层、设置和预生成 | |

右栏是硬性限制：原版客户端被送往未知维度时会断开连接，而未知方块也无法向它描述。左栏之所以可行，是因为其中的一切要么完全在服务端运行，要么通过原版本就能理解的数据包传到客户端（由服务端填充的合成结果槽、普通的进度数据包、状态消息形式的传送门拒绝提示，以及由原版游戏模式/标题/传送数据包构成的预生成等候）。

`worldtemplates` 属于服务端，只有一个例外：**`rubicWorld` 不能与 `vanillaClients` 一起使用**。rubic 世界由立方体构成，没有该模组的客户端无法接收它们，因此它会在登录时被拒之门外，或什么也看不到。两者同时设置时，新世界会按普通世界而非 rubic 世界创建，并且日志会说明原因，而不是留下一个把每位玩家都拒之门外的服务器。对一个*已经*作为 rubic 世界创建的世界开启 `vanillaClients`，是唯一会让游戏直接停止的情况：把这样的存档按普通世界加载会毁掉它，所以它会原封不动，留给你决定。

设置步骤：

1. 在配置中启用 `vanillaClients`（`content` 类别，需要重启）。它会强制执行右栏的限制：这些文件夹在加载时被跳过，每个被跳过的文件都会在日志中点名，因此一个误放的方块文件会变成一行日志，而不是一次被拒绝的连接。
2. 无论如何都要让定义远离右栏的文件夹；被跳过的文件只是累赘。当资源包引用物品时（闸门的 `hold`、`killedDrops`、配方产物、交易），只写原版或服务器上其他两端都有的模组所提供的物品。
3. 不要加入实体变种。每一个都会注册成独立的实体，而原版客户端没有办法生成它，所以 `vanillaClients` 会像跳过方块一样跳过它们，并在日志中点名；某个阵营的 `standIn`，或指定了其中之一的生成，随后就没有东西可生成了。
4. 照常安装在服务器上。玩家的机器上不需要放任何东西；他们没有 `/rdpl`，所以改用 `/rdplserver` 形式：`/rdplserver team join`、`/rdplserver round start`、`round reset`、`round vote yes`。`opens.leaderSays` 和 `reset.voteSays` 默认写的是 `/rdpl`，因此在供原版客户端使用的资源包中，请改用 `/rdplserver` 来措辞。
5. 用同一版本的一个干净原版客户端加入进行测试。失败是明显的：连接会在门口就被拒绝，而不是之后悄悄出问题。
6. 可接受的缺憾：
   - 服务端添加的配方可以合成，但不会出现在配方书中。
   - 世界介绍不会显示。原版客户端也不会被等待：它会立即受到欢迎，与其他所有人一起从预生成等候中放行，并且永远不会拖延 `/rdpl round start`。
   - 本模组显示为卡片的任何内容，例如结果、主导者通知或 `saysCard` 的行，会以聊天行的形式送达，而像大厅那样的屏幕中央提示则以标题形式送达。
   - 硬度分组设置服务器破坏方块所需的时间，但客户端的裂纹动画仍按该方块通常的速度进行。对于客户端也会读取的数值（如堆叠上限、耐久度、硬度或光照）的覆盖，客户端那边仍会显示旧值。
   - 硬度分组的 `adventure` 挖掘不起作用：除非手持物品在自己的 `CanDestroy` 标签中写明了该方块，否则处于冒险模式的原版客户端永远不会开始挖掘。
   - 被禁用的方块或物品仍会出现在原版客户端的创造模式标签页中，因为这是客户端自己构建的；禁用的其他一切都可以通过服务器实现。

## 注册表重命名

*资源包如何工作*

`<namespace>/registry_remap/*.json`

文件名由你自己选择，只有文件夹会被读取，并且多个文件可以叠加。

当模组重命名它的某个方块或物品时，在重命名之前保存的世界会丢失它们。在这里放一个文件，把旧名称映射到新名称：

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

注册表指该条目所属的那一个，通常是 `minecraft:items` 或 `minecraft:blocks`。重命名可以串联，所以先把 A 映射到 B、之后又把 B 映射到 C，会让 A 直接指向 C。

## 模组 API

*资源包如何工作*

模组可以在自己的 jar 内附带 RDPL 内容，因此不需要单独的资源包。在 jar 的根目录放一个名为 `rdploader` 的文件夹，布局与资源包完全相同：

```
thatmod.jar
  mcmod.info
  rdploader/assets/thatmod/blocks/ruby_ore.json
```

模组附带的是默认值，而不是覆盖。它的加载优先级低于资源包文件夹中的每一个资源包，因此资源包作者写的任何内容都会胜过它，并且模组只能提供它在自己的 `mcmod.info` 中声明的命名空间下的文件。其他命名空间下的文件会被忽略并给出警告，命名空间内部嵌套的 `rdploader` 文件夹也一样，因此模组无法悄悄重新定义另一个模组或资源包作者的内容。

每个附带此类内容的模组，在首次被发现时都会在 `rdploader/config/mods.json` 中得到一个条目：

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| 字段 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `enabled` | `true` 或 `false` | `true` | 关闭该模组的内容，方式与 `.disabled` 关闭资源包相同 |
| `priority` | `-1` 或一个数字 | `-1` | `-1` 使该模组位于所有资源包之下；任何其他数字会让它进入普通的[优先级](#组织资源包)顺序，与带编号的资源包并列 |

无论 `overrideResourcePacks` 如何设置，模组资源包都永远不会进入资源包覆盖层，因为只有资源包作者才能用 `O` 字母提出这一要求。日志会标出模组资源包，并按从低到高的顺序列出资源包，因此没有任何东西会在不被看见的情况下加载。

## 为 1.20.1、1.21.1 和 26.x 编写的资源包

*资源包如何工作*

为本模组的 1.20.1、1.21.1 或 26.x 线制作的资源包也可以在这里加载。加载器通过以下方式识别它们：`pack.mcmeta` 的格式高于 3，`assets/` 旁边有 `data/` 文件夹，或者存在没有 1.12.2 对应物的 `.json` 语言文件和 `textures/block/`；无论它是为哪条线编写的，都以同样的方式向回转换。zip 只会在自身内部转换一次：每个 1.12.2 读取方式不同的文件，都会写入 zip 的 `versions/1.12.2/` 文件夹，而根目录中的现代文件保持原样，所以同一个 zip 仍可在 1.20.1、1.21.1 和 26.x 上加载，正如[一个 zip 适用于所有版本](#组织资源包)所述。转换会在它所写入的文件夹中放一个 `port.stamp` 文件作为标记，其中写有 RDPL 的版本。已经带有 `versions/1.12.2/` 文件夹的 zip 会通过它来读取；当其 `port.stamp` 指明的是另一个 RDPL 版本时，转换会重新写入该文件夹，替换其中每个文件并在日志中逐一点名，而没有 `port.stamp` 的文件夹（例如由资源包作者自己编写的）则永远不会再被转换。根目录的文件中，只有转换原样通过的那些仍会被读取，例如音效以及 `textures/block/` 和 `textures/item/` 之外的纹理。zip 会先写入一个临时文件，完成之后才替换原文件。`rdploader/assets` 和 `rdploader/data` 下的散装文件不会被改写；每次扫描该文件夹时，它们都会经过同一套转换来读取。

现代的方块文件转回来后，每个变种都会带有一个 `meta`，顺序即变种的书写顺序，其标签则变成矿物词典名称：

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

| 现代文件 | 1.12.2 文件 |
| --- | --- |
| 每个定义文件夹对应 `data/<ns>/<folder>/` | `assets/<ns>/<folder>/` |
| `recipe/`、`loot_table/`、`advancement/`、`function/`、`structure/`（1.21.1） | `recipes/`、`loot_tables/`、`advancements/`、`functions/`、`structures/` |
| `data/*/tags/items/`（1.21.1：`tags/item/`） | `assets/<ns>/oredict/converted_tags.json` |
| `data/minecraft/tags/functions/tick.json` | `assets/<ns>/gamerules/converted_tick.json`，即主世界的 `gameLoopFunction` |
| `minecraft:smelting` 配方 | `assets/<ns>/furnace/converted_smelting.json` |
| `assets/<ns>/lang/<lang>.json` | `assets/<ns>/lang/<lang>.lang` |
| `textures/block/`、`textures/item/` | `textures/blocks/`、`textures/items/` |
| `models/item/<variant>.json` | `models/item/<file>/<variant>.json` |
| 没有方块状态（在现代线上自动生成） | 为每个方块文件生成一个 Forge 方块状态，并附带其类型所需的模型 |

- 每个现代原版 id 都会通过 jar 中附带的扁平化表向回转换，该表由游戏自己的数据修复器构建，因此它会变成带有元数据的 1.12.2 方块或物品：`minecraft:red_wool` 变成 `minecraft:wool:14`，带有 `axis=x` 的 `minecraft:oak_log` 变成 `minecraft:log:4`。当某个键把方块和元数据分开存放时，例如世界生成中的 `block` 和 `modelBlock`，元数据会放入 `meta` 或 `modelMeta`；`soil` 则保留整个方块。资源包自己的 id 通过它自己的文件来解析：`mypack:worm` 变成 `mypack:test_ore:1`。实体、生物群系、战利品表、音效、粒子和属性的名称以同样的方式向回转换，而被命名的维度会变成一个数字：资源包自己的维度取其 `id`，没有 `id` 时则取从 1000 起的一个稳定数字，日志会指明。
- 标签通过约定映射的逆向变成矿物词典名称：`forge:gems/testium` 和 `c:gems/testium` 变成 `gemTestium`，`minecraft:logs` 变成 `logWood`。配方原料的 `tag` 变成 `forge:ore_dict` 原料，配方则变成 `forge:ore_shaped` 或 `forge:ore_shapeless`；燃料的 `tag` 变成 `oreDict`。每个配方物品都会得到一个 `data`，因为 1.12.2 拒绝没有它的带子类型物品。
- 语言文件中的 `block.mypack.worm` 变成 `tile.mypack:test_ore.worm.name`，`item.` 在 `item.` 之下以同样方式处理，`itemGroup.mypack.tab` 变成 `itemGroup.tab`，`fluid.mypack.x` 则同时变成两个 1.12.2 流体键。
- 战利品表会丢失 1.12.2 无法读取的部分：物品的变种变成 `set_data`，数值提供器变成 `min` 和 `max`，池会得到一个 `name`，`alternatives` 和 `group` 条目被展平，而 1.12.2 没有的函数、条件或条目类型会被略去，并在日志中留下一行。进度的 `items` 变成 `item` 和 `data`，`tag` 变成 `forge:ore_dict`。
- 函数会被逐行改写成 1.12.2 的语法：`execute as ... at @s run` 变成 `execute <entity> ~ ~ ~`，`execute if block` 变成 `detect`，`tag` 和 `team` 变成 `scoreboard players tag` 和 `scoreboard teams`，`data merge` 变成 `blockdata` 和 `entitydata`，选择器则把 `distance`、`scores`、`limit` 和 `gamemode` 换成 `r`、`score_X_min`、`c` 和 `m`。刷怪笼的 `SpawnData` 会失去它的 `entity` 包装。转换无法携带的行，例如 `bossbar` 或宏行，会变成注释，日志会指出文件、行以及原因，因此函数仍能加载。
- 数据版本高于 1343 的结构 `.nbt` 会把它的调色板、刷怪笼、物品堆叠和实体 id 向回转换；没有 1.12.2 对应物的方块保持原样，并放置空气。
- 现代平坦世界的地面位于世界底部，而 1.12.2 的平坦世界从 y 0 开始铺设，所以转换会把高度上移 64（当模板写明 `worldMinHeight` 时，则按它上移）：平坦模板的 `worldSpawn` 和 `resetSendsTo`、队伍的出生高度、平坦维度的 `groundLevel`，以及当资源包的主世界是平坦世界时函数中的每一个绝对 y。
- 被略去的内容，每一项都会在日志中留下一行：原版的数据驱动世界生成、维度类型、伤害类型、附魔以及 1.12.2 没有的其他注册表；方块、实体和流体标签；除 `tick` 之外的函数标签；切石机、锻造和 1.12.2 没有的其他配方类型；物品组件；只有现代世界才会生成的结构键；除 `till`、`path`、`bush` 和 `animals` 之外的 `behavesAs` 名称；`jobSite`，以及没有 `careers` 的职业会得到一个以其文件命名的职业。

日志会为每个转换的资源包写一行摘要，并为每个被移动、转换、略去或无法携带的文件写一行，而 1.12.2 不读取的每个键，仍会由遇到它的解析器点名。这次转换只是尽力而为，并不是一个完成的资源包：请阅读这些行，并手动完成它们所指出的事项，首先处理那些找不到纹理的自动生成方块状态。请在资源包根目录或单独的 zip 中进行这些修复，不要在转换所写入的 `versions/1.12.2/` 文件夹中修复：另一个版本的 RDPL 会重新写入该文件夹。

---

# 方块与物品

## 方块

*方块与物品*

`<namespace>/blocks/*.json`

文件的路径就是方块的注册名，所以 `mypack/blocks/ruby_ore.json` 注册的是 `mypack:ruby_ore`。`variants` 内部的键命名的是该方块的各个元数据值；它们本身并不是方块。

下面一次性展示每一个键。真实的文件只写它需要的那些。标明适用于某一类型的键，只由该类型读取。

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

### 类型

*方块*

| 类型 | 你会得到什么 |
| --- | --- |
| `basic` | 普通方块。缺少 `type` 时使用 |
| `ore` | 掉落自身以外的东西，受时运和精准采集影响 |
| `falling` | 像沙子或沙砾那样下落 |
| `slab` | 下半、上半和双层，两个可以在手中合并 |
| `stairs` | 转角和斜坡由系统为你处理 |
| `fence` | 与相邻方块连接，也与其他模组的栅栏连接 |
| `pane` | 像玻璃板那样连接 |
| `wall` | 像圆石墙那样连接，带有立柱形状 |
| `door` | 两格高，可用手开关，也响应红石。只使用一个变种，因为其余元数据承载铰链、朝向以及是否打开 |
| `trapdoor` | 位于方块顶部或底部的铰链翻板，可用手或红石开关。一个变种，元数据承载朝向、上下半以及是否打开 |
| `fence_gate` | 栅栏线中的一扇门，可用手或红石开关，遇到墙时会降低。一个变种 |
| `banner` | 立在柱子上或靠在墙上的旗帜，有十六种站立朝向，承载你自己的图案。会再注册一个名为 `<name>_wall` 的方块，用于悬挂的那种 |
| `ladder` | 可攀爬，靠墙放置 |
| `torch` | 墙上与地面放置，带有粒子 |
| `bell` | 像村庄自 1.14 起拥有的那种钟：从侧面使用时、被红石触发时或被投射物击中时会响，在框架中摆动，并让附近的袭击者发光。一个变种，元数据承载朝向以及悬挂方式 |
| `log` | 朝向你所放置的那一面旋转，并在矿物词典中注册为 `logWood`，使砍树和 Blast Plaster 把它视为树干 |
| `leaves` | 会凋零，可用剪刀采集，可染色，并掉落树苗，且在矿物词典中注册为 `treeLeaves` |
| `sapling` | 长成一棵树，或长成你的某个结构 |
| `crop` | 经历多个阶段生长，掉落种子和产物物品 |
| `flower` | 站在土壤上的单格植物 |
| `cane` | 像甘蔗或仙人掌那样向上成柱生长 |
| `vine` | 攀爬并悬挂在方块的侧面 |
| `portal` | 把走进去的一切送往另一个维度 |
| `container` | 持有一个玩家可以打开的物品栏，大小不限，并能在首次打开时用战利品表填充自己。绘制成普通方块或箱子，取决于资源包的要求 |

### 文件键

*方块*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `variants` | 是 | 变种名称到变种的对象 | | 每个元数据值一个条目。键在方块状态、模型路径和语言键中命名该值。注册名来自文件自己的路径 |
| `type` | 否 | 上述类型之一 | `basic` | 方块采取哪种形态 |
| `material` | 否 | [方块材料](#值列表)之一 | `rock` | 挖掘行为、活塞、火和液体 |
| `soundType` | 否 | [音效类型](#值列表)之一 | `stone`；`log` 为 `wood`，`leaves` 和 `crop` 为 `plant`，`stairs` 和 `wall` 取 `modelBlock` 的 | 脚步声、破坏和放置 |
| `mapColor` | 否 | [地图颜色](#值列表)之一 | 取自材料 | 它在地图上的样子 |
| `harvestTool` | 否 | `pickaxe`、`axe`、`shovel` | `pickaxe` | 用哪种工具采集 |
| `harvestToolLevel` | 否 | 0 到 3 | `0` | 0 木，1 石，2 铁，3 钻石 |
| `silkHarvest` | 否 | 布尔值 | `true` | 精准采集是否返回方块本身 |
| `opensWith` | 否 | 物品 id | 无 | 使该方块成为一个锁箱：破坏它会掉落方块本身，而用指定物品右键会消耗一个、播放该方块的破坏音效、发放变种的 `drops` 列表并移除该方块。其他任何点击都会显示来自语言文件的动作栏文字 `tile.<pack>:<block>.<variant>.locked` |
| `openSound` | 否 | 音效名称 | 破坏音效 | 锁箱打开时播放的、代替其破坏音效的声音 |
| `expDrop` | 否 | 带有 `min` 和 `max` 的对象 | 无 | 不用精准采集破坏时掉落的经验 |
| `creativeTab` | 否 | 标签页名称 | 无 | 它出现在哪个标签页中 |
| `renderLayer` | 否 | `solid`、`cutout`、`cutout_mipped`、`translucent` | 视类型而定 | 如何绘制 |
| `opaque` | 否 | 布尔值 | `true` | 是否完全遮挡视线和光线 |
| `fullCube` | 否 | 布尔值 | 与 `opaque` 相同 | 是否填满它的整个空间 |
| `lightOpacity` | 否 | 0 到 255 | 不透明时为 `255`，否则为 `0` | 它吸收多少光 |
| `slipperiness` | 否 | float | `0.6` | 冰是 `0.98` |
| `flammability` | 否 | int | `0` | 火烧毁它的难易程度 |
| `fireSpread` | 否 | int | `0` | 火从它蔓延的难易程度 |
| `explosionResistanceDivisor` | 否 | float | `1.0` | 在爆炸面前对每个变种的 `resistance` 做除法 |
| `modelBlock` | 否 | 方块名称 | `minecraft:stone` | 当你的方块没有模型时借用其模型的方块 |
| `modelMeta` | 否 | int | `0` | 该模型的哪个变种 |
| `itemModel` | 否 | `state`、`item` | `state` | `state` 跟随方块状态，`item` 寻找它自己的文件 |
| `tint` | 否 | `biome`、`none`，或十六进制颜色 | 无 | 需要模型中有 `tintindex` 才会显示 |
| `plantTypes` | 否 | [植物类型](#值列表)的列表 | 无 | 什么可以种在它上面 |
| `behavesAs` | 否 | `till`、`path`、`bush`、`animals` 的列表 | 无 | 要采用的原版行为 |
| `bounds` | 否 | 六个数字的列表，0 到 1 | 完整方块 | 碰撞箱，写作 `[x1, y1, z1, x2, y2, z2]` |
| `requires` | 否 | 模组 id 或资源包命名空间的列表 | 无 | 除非全部存在，否则该文件会被跳过 |
| `particle` | 仅限 torch | `none`、`flame`、`colored` | `flame` | 火把上方的粒子 |
| `particleColor` | 仅限 torch | 十六进制颜色 | `FFFFFF` | 当 `particle` 为 `colored` 时使用 |
| `smoke` | 仅限 torch | 布尔值 | `true` | 是否冒烟 |
| `leafSapling` | 仅限 leaves | 方块名称 | 无 | 它们掉落的树苗 |
| `leafSaplingChance` | 仅限 leaves | int | `5` | 每 N 个树叶中有一个会掉落 |
| `seed` | 仅限 crop | 物品名称 | `minecraft:wheat_seeds` | 用于种植它的物品，以及未成熟作物掉落的东西 |
| `produce` | 仅限 crop | 物品名称 | `minecraft:wheat` | 收获得到什么 |
| `maxAge` | 仅限 crop | int | `7` | 有多少个生长阶段 |
| `growth` | 仅限植物 | 对象 | 无 | 见[生长](#生长) |
| `sapling` | 仅限 sapling | 对象 | 无 | 见[树苗](#树苗) |
| `portal` | 仅限 portal | 对象 | 无 | 见[传送门与门](#传送门与门) |
| `container` | 仅限 container | 对象 | 无 | 见[容器](#容器) |
| `bell` | 仅限 bell | 对象 | 无 | 见[钟](#钟) |

### 变种键

*方块*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `meta` | 是 | 0 到 15 | | 该变种所占用的元数据值 |
| `hardness` | 否 | float | `1.0` | 破坏它需要多久。黑曜石是 `50`，`-1` 表示不可破坏 |
| `resistance` | 否 | float | `5.0` | 爆炸抗性 |
| `light` | 否 | 0 到 15 | `0` | 发出的光 |
| `harvestLevel` | 否 | 0 到 3 | `0` | 为该变种覆盖工具等级 |
| `rarity` | 否 | `common`、`uncommon`、`rare`、`epic` | `common` | 提示框中的名称颜色 |
| `maxSize` | 否 | 1 到 64 | `64` | 堆叠上限 |
| `oreDict` | 否 | 矿物词典名称的列表 | 无 | 该变种所注册的矿物词典名称 |
| `drops` | 否 | 掉落物列表 | 掉落自身 | 破坏它会得到什么 |

**元数据是永久的。** 变种所占用的数字会写入每一个包含它的存档。之后重新编号或重新排序变种，会把已放置的方块变成别的东西。请把新变种添加在末尾，并且永远不要重用数字。

`basic` 方块可以容纳十六个变种；`slab` 八个；`log` 和 `leaves` 四个，因为轴向和凋零标志需要各自的位；单状态类型只容纳一个。

### 掉落物

*方块*

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `block` | 二选一 | 方块或物品名称 | | 掉落什么 |
| `entity` | 二选一 | 实体名称 | | 方块破坏时放出的实体，代替物品 |
| `meta` | 否 | int | `0` | 它的哪个变种 |
| `amount` | 否 | int 或范围 | `1` | 数量 |
| `chance` | 否 | 0 到 100 | `100`，当 `guaranteed` 关闭时为 `0` | 该掉落究竟多常发生 |
| `weight` | 否 | int | `0` | 大于零时，该条目加入一个恰好产出一个掉落物的池。见下文 |
| `bonusChance` | 否 | int 列表 | 无 | 每个时运等级额外的掉落，每级一个条目 |
| `guaranteed` | 否 | 布尔值 | `true` | `chance` 的旧式简写。开启为 `100`，关闭为 `0` |

每个没有 `weight` 的条目都独立判定，所以有三个这样条目的方块可能三个全掉，也可能一个都不掉。给条目加上 `weight`，它们就不再独立：它们组成一个池，每次方块破坏时恰好选中其中一个，几率与权重成比例。上面的例子中，钻石和绿宝石以一比四共用一个池，所以两者之一一定会掉出，其中绿宝石占五分之四，而红宝石和煤炭各自单独判定，银鱼又是另一回事。物品和实体分别入池，因此带权重的物品与带权重的实体不会互相竞争。

指定了 `entity` 的条目会在方块所在的位置放出一个实体，朝向随机，并且生物会按当地难度获得其通常的生成处理，所以它出现时带有它本来会有的装备和效果。`amount` 决定数量，`chance` 决定频率，`weight` 把它放入实体池。它在方块破坏时发生，无论是怎么破坏的，所以爆炸或活塞放出它们的效果与镐子无异。`meta`、`bonusChance` 和时运对实体没有意义，会被忽略。

同时指定了 `block` 和 `entity` 的掉落使用实体，并在日志中说明。

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
| `stages` | 否 | int | `16` | 完成之前的生长阶段数 |
| `growth` | 否 | int | | 每个随机刻有 N 分之一的几率推进一步 |
| `spread` | 否 | int | `0` | 向相邻方块蔓延多远 |
| `maxHeight` | 否 | int | `3` | 仅限 cane。柱子能长多高 |
| `drop` | 否 | 物品名称 | 无 | 破坏时掉落什么 |
| `dropCount` | 否 | int | `1` | 数量 |
| `needsSky` | 否 | 布尔值 | `false` | 只在能看见天空的地方生长 |
| `needsWater` | 否 | 布尔值 | `false` | 只在水附近生长 |
| `waterRange` | 否 | int | `1` | 那片水可以有多远 |
| `damage` | 否 | 布尔值 | `false` | 伤害接触它的一切 |
| `damageAmount` | 否 | float，半颗心 | `1.0` | 伤害有多大 |
| `breaksNeighbors` | 否 | 布尔值 | `false` | 破坏放在它旁边的方块，像仙人掌那样 |

### 树苗

*方块*

下面一次性展示每一个键。真实的文件只写它需要的那些。

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

`structure` 会用你的某个模板取代生成的树，这是建造生成器做不到的东西的办法，而方块中的其他部分都不需要再写。改在 `structures` 下列出多个，树苗每次生长时就会从中选一个，这样一片树林就不会总是同一棵树：

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `soil` | 否 | 方块名称列表 | 无 | 它能在什么上生长 |
| `stages` | 否 | int | `2` | 变成树之前的生长阶段数 |
| `chance` | 否 | int | `7` | 每个随机刻 N 分之一 |
| `light` | 否 | 0 到 15 | `9` | 所需的光照等级 |
| `log` | 否 | 方块名称 | `minecraft:log` | 树干方块 |
| `leaves` | 否 | 方块名称 | `minecraft:leaves` | 树叶方块 |
| `height` | 否 | int | `4` | 树干高度 |
| `vines` | 否 | 布尔值 | `false` | 从树叶上垂下藤蔓 |
| `structure` | 否 | `namespace:name` | 无 | 长成这个模板，而不是生成的树 |
| `structures` | 否 | 列表 | 无 | 可长成的多个模板，每次生长时选择一个。每个条目写作 `{ "structure": "namespace:name", "weight": 3 }`，或写裸名称表示几率均等。会覆盖 `structure` |

## 容器

*方块与物品*

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

| 设置 | 类型 | 默认值 | 作用 |
| ------------ | --------------- | ------- | ---- |
| `rows`       | 整数             | `3`     | 槽位的行数，1 到 9 |
| `columns`    | 整数             | `9`     | 每行的槽位数，1 到 12 |
| `lootTable`  | 文本            | 空   | 玩家第一次打开该方块时，向其中掷入的战利品表，与地牢箱子的填充方式完全相同。留空则初始为空 |
| `chestModel` | 布尔值或文本 | `false` | 渲染为带可开合箱盖的箱子，而不是使用你自己模型的普通方块。`true` 使用原版箱子贴图；形如 `mypack:blocks/strongbox_chest` 的纹理名则改用你自己的箱子贴图，放置的方块和物品都适用。请给方块状态指定模型 `resourcedatapackloader:pack_chest`，并在 `texture` 下写上同一个名称，这样你手中的物品也是箱子的形状。带箱子模型的方块还会像原版箱子一样把 `opaque` 默认设为 `false`，因此光线不会在该方块处被截断，箱子也不会被画得发暗。 |
| `guiTexture` | 文本            | 空   | 界面使用你自己的背景图片。留空则根据行数和列数所需的大小，用原版箱子界面拼出一张 |
| `guiWidth`   | 整数             | 无    | 该图片的宽度，使用 `guiTexture` 时必填 |
| `guiHeight`  | 整数             | 无    | 该图片的高度，使用 `guiTexture` 时必填 |
| `bauble`     | 文本            | 空   | 仅限物品：可佩戴它的 Baubles 槽位，可选 `amulet`、`ring`、`belt`、`trinket`、`head`、`body` 或 `charm`。背包通常使用 `body` 或 `charm`。未安装 Baubles 时此项被忽略，物品的其余功能照常工作。每个名称对应 Baubles 标签页中的一个格子，所以要求 `body` 的物品只适合那个格子；`ring` 是两个戒指格子，`trinket` 则适合所有格子。Baubles 是软依赖：它存在时本模组在其之后加载，不存在时照常运行，因此指定槽位的资源包在从未听说过 Baubles 的服务器上也是安全的。 |

**九行乘十二列是上限**，这是 Iron Chest 提供的最大尺寸，也是一个界面所能容纳的最大值。要求更大的资源包会被截到该上限，并输出一行错误说明。关于最高的情况有一点警告：九行的界面高 276 像素，而 1080 显示器在 GUI 缩放 `auto` 下只有 270，因此顶部和底部各被裁掉三像素——缩放设为 3 即可完整显示。Iron Chest 能放下九行，是因为它自带更紧凑的贴图；想要同样效果的资源包可以设置 `guiTexture` 并自己绘制。

**界面是绘制出来的，而不是随包提供的。** 九列及以下、六行及以下的容器直接使用原版箱子界面，看起来与该尺寸的箱子完全一样。更大的则在绘制时由同一张图片拼装而成——顶边、按需重复的一行槽位，以及带有玩家自身物品栏的底部——因此资源包可以要求任何原版界面都不涵盖的尺寸，而无需自带图片。`guiTexture` 会在资源包想要自己外观的地方覆盖以上所有内容，此时 `guiWidth` 和 `guiHeight` 必须给出其大小，否则会改用绘制的版本，并输出一行错误说明。

**方块的行为。** 它在存档和重新加载后保留内容，被破坏时掉落内容，通过红石比较器反映其装满程度，并且可以像箱子一样在铁砧中改名。`chestModel` 还会赋予它箱子的开启音效和箱盖动画；关闭时，方块使用你自己的 `modelBlock` 所指定的模型来绘制，因此板条箱、木桶或柜子都可以。

**给箱子上色。** 箱子贴图就是一张普通纹理，所以像素图无需画一个像素就能给原版贴图重新着色：`extends` 它并给出一个 `tint`，然后在 `chestModel` 中以及模型的 `texture` 中写上该像素图的名称。

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

放置的方块和你手中的物品读取的是同一个名称，所以两者一致。只在其中一处写上这个名称，另一处就仍是原版的棕色。

**容器物品可以佩戴。** 给它 `bauble`，在安装了 Baubles 的情况下，它会进入对应的槽位，并且无需摘下就能用按键打开——默认是 `V`，可在控制设置的 Resource Data Pack Loader 下重新绑定。`B` 是 Baubles 为自己的标签页绑定的键，所以两者不共用按键。在已打开一个佩戴的容器时再次按下，会切换到你佩戴的下一个并循环，因此同时佩戴多个时都能打开。该按键只在存在 Baubles 时才出现，而物品的其他一切，包括右键和它的物品栏，无论有没有 Baubles 都能工作。Baubles 没有自己的背包槽位；`body` 和 `charm` 是背包通常使用的两个。

**战利品表在第一次打开时填充**，而不是在方块被放置时，这正是它在结构中有用的原因：第一个打开的人得到那次掷取。同一张战利品表也可以被印记形状或村庄地块上的 `lootTable` 使用，因此资源包可以通过世界生成放置这些容器并以同样的方式补给它们。

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
    "sound": "minecraft:block.note.bell",
    "resonateSound": "minecraft:block.note.chime"
  },
  "variants": { "village_bell": { "meta": 0, "hardness": 5.0, "resistance": 30 } }
}
```

以及它的方块状态，`assets/mypack/blockstates/village_bell.json`：

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

| 设置 | 类型 | 默认值 | 作用 |
| --------------- | ---------- | ---------------------------- | ---- |
| `swing`         | 布尔值    | `true`                       | 用自己的模型绘制钟身，并在钟响时让它摆动。`false` 则把整个钟画成一个静止的方块，没有任何动画 |
| `sound`         | 音效名 | `minecraft:block.note.bell`  | 钟响时播放。留空则无声 |
| `resonateSound` | 音效名 | `minecraft:block.note.chime` | 因附近有袭击者而使钟产生共鸣时播放。留空则共鸣无声 |

**它的悬挂方式与游戏自 1.14 起的钟相同。** 放在方块顶部时立在地面上，朝向你面对的方向；放在方块下方时从天花板悬挂；靠在墙上时挂在那面墙上，若对侧也是实心方块则挂在两面墙之间。支撑它的东西消失时它会掉落，而两墙之间的钟在其中一面墙消失时会变成单墙钟。这四种情况下它的碰撞箱都与原版一致，因此不读取 `bounds`。

**什么会敲响它。** 使用钟身的侧面、横梁以下的部分：地面钟是横梁所跨越的两个面，墙上的钟是紧邻墙的两个面，天花板钟则是任意一侧。顶部、底部以及钟身以上的任何位置都没有作用。红石信号在接通时敲响一次，箭、雪球或任何其他弹射物击中手能够触及的一侧时也会敲响它。钟身会向远离被击中一侧的方向摆动两秒半；红石则让它沿钟所面朝的方向摆动。

**敲响之后会发生什么。** 32 格以内的村民听到后会进入室内十五秒，前往其村庄所知的最近的门。当 32 格内有袭击者时，钟在敲响后四分之一秒产生共鸣，两秒后，48 格内的每个袭击者都会发光三秒，并在钟旁其所在的一侧出现彩色粒子。袭击者指的是[袭击](#袭击)派出的任何生物，以及游戏自带的灾厄村民和女巫。这种类型的钟对每一场袭击来说都是村庄之钟：每一波到来时它都会响，无需在该袭击的 `bell` 中指名。

**模型。** 钟没有 `blocks` 属性，因此它的方块状态以 `facing` 和 `attachment` 为键。开启 `swing` 时，这些模型只绘制框架，而摆动的部分是另一个名为 `body` 的条目，在其静止位置的方块空间中建模；它绕着向内半格、向上四分之三格的那个点倾斜，与原版相同。`inventory` 条目是物品，框架和钟身合在一起。关闭 `swing` 时没有 `body` 条目，四个框架模型也会把钟绘制出来。

**摆动由客户端绘制。** 钟响会作为方块事件传达给玩家，因此专用服务器会让所有装有本模组的玩家看到钟在摆动，而没有本模组的玩家只能听到钟声。音效、共鸣和发光都在服务器上发生。

## 模型、方块状态与纹理

*方块与物品*

定义一个方块或物品就会注册它。它*看起来*如何，仍然是一组普通的资源文件，位于 Minecraft 已在使用的相同文件夹中、采用相同的格式，只是放在你自己的命名空间下。

```
assets/mypack/blockstates/ruby_ore.json
assets/mypack/models/block/ruby_ore.json
assets/mypack/models/item/ruby/ruby.json
assets/mypack/textures/blocks/ruby_ore.png
assets/mypack/lang/en_us.lang
```

### 为变种命名

*模型、方块状态与纹理*

每个拥有多个变种的方块都会获得一个名为 `blocks` 的属性，其取值就是定义中的变种名称。因此注册 `ruby_ore` 和 `deep_ruby_ore` 的方块文件需要一个包含这两个变种的方块状态：

```json
{
  "variants": {
    "blocks=ruby_ore": { "model": "mypack:ruby_ore" },
    "blocks=deep_ruby_ore": { "model": "mypack:deep_ruby_ore" }
  }
}
```

只有一个变种的方块同样保留 `blocks` 属性，所以它的键仍然是 `blocks=<name>`，但这只限于确实带有该属性的类型。有十二种类型把全部元数据都用在了形状上，只有一个变种，也不带 `blocks` 属性，因此它们只以自己的属性为键。[按类型划分的方块状态](#按类型划分的方块状态)说明了哪些是哪些。

方块若有自己的属性，则按状态列出的顺序用逗号连接，例如 `blocks=ruby_log,axis=y`、`blocks=ruby_slab,half=bottom`、`blocks=ruby_wall,up=true,north=true`。楼梯方块没有 `blocks` 属性，所以它只以 `facing=east,half=bottom,shape=straight` 为键。有两个属性是特意省略的：墙自己的变种属性，以及树叶方块的 `check_decay` 和 `decayable`，所以树叶只需要 `blocks=ruby_leaves`。旗帜完全没有变种属性，站立时以 `rotation=0` 到 `15` 为键，挂在墙上时以 `facing=north` 为键，详见[旗帜](#旗帜)。

### 按类型划分的方块状态

*模型、方块状态与纹理*

方块状态文件必须包含什么，由两件事决定：该类型是否带有 `blocks` 属性，以及它自己有哪些属性。

| 类型 | 注册内容 | 方块状态属性 | 变种数 |
| ------------------------- | --------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- | -------- |
| `basic`、`ore`、`falling` | 一个方块                         | `blocks`                                                                                                                          | 16       |
| `flower`                  | 一个方块                         | `blocks`                                                                                                                          | 16       |
| `portal`                  | 一个方块                         | `blocks`                                                                                                                          | 16       |
| `fence`、`pane`           | 一个方块                         | `blocks`、`north`、`east`、`south`、`west`                                                                                        | 16       |
| `wall`                    | 一个方块                         | `blocks`、`up`、`north`、`east`、`south`、`west`                                                                                  | 16       |
| `slab`                    | 两个，`<name>` 和 `<name>_double` | 半砖是 `blocks` 和 `half`；双层砖只有 `blocks`                                                                      | 8        |
| `log`                     | 一个方块                         | `blocks`、`axis`，取值为 `x`、`y`、`z` 或 `none`                                                                                | 4        |
| `leaves`                  | 一个方块                         | `blocks`                                                                                                                          | 4        |
| `stairs`                  | 一个方块                         | `facing`、`half`、`shape`                                                                                                         | 1        |
| `door`                    | 一个方块                         | `facing`、`half`、`hinge`、`open`                                                                                                 | 1        |
| `trapdoor`                | 一个方块                         | `facing`、`half`、`open`                                                                                                          | 1        |
| `fence_gate`              | 一个方块                         | `facing`、`in_wall`、`open`                                                                                                       | 1        |
| `banner`                  | 两个，`<name>` 和 `<name>_wall`   | 站立的是 `rotation`，`0` 到 `15`；墙上的是 `facing`                                                                           | 1        |
| `ladder`、`torch`         | 一个方块                         | `facing`，火把在四面墙之外再加上 `up`                                                                                           | 1        |
| `bell`                    | 一个方块                         | `facing` 和 `attachment`（取值为 `floor`、`ceiling`、`single_wall` 或 `double_wall`），外加摆动部分的 `body` 条目 | 1        |
| `crop`                    | 一个方块                         | `age`，始终是 `0` 到 `7`，无论 `maxAge` 是多少                                                                                  | 1        |
| `cane`                    | 一个方块                         | `age`，`0` 到 `15`                                                                                                                | 1        |
| `sapling`                 | 一个方块                         | `stage`，`0` 到比 `stages` 小一                                                                                                   | 1        |
| `vine`                    | 一个方块                         | `up`、`north`、`east`、`south`、`west`，并且只能使用多部件                                                                      | 1        |

有四个属性会替你去掉，所以书写键时不要带它们：门和栅栏门上的 `powered`、墙上的 `variant`，以及树叶上的 `check_decay` 和 `decayable`。

无论 `maxAge` 是多少，作物都保留原版的八个 `age` 值，因为 `maxAge` 只决定它能长到多大，所以它的方块状态总是写 `age=0` 到 `age=7`。

有两种类型会注册第二个方块。半砖的 `<name>_double` 需要自己的方块状态，以 `blocks` 为键、不带 `half`，并且它永远不会有自己的物品。旗帜的 `<name>_wall` 见[旗帜](#旗帜)。

**藤蔓是唯一不能使用 Forge 格式的类型**，因为 `forge_marker` 不支持多部件，所以它的方块状态是普通的原版 `multipart` 列表，纹理直接写进模型里。

**Forge 格式更短，示例资源包用的就是它。** 原版方块状态把每一种组合都写成独立的键，楼梯就有四十个。有了 `"forge_marker": 1`，文件只需把每个属性列出一次，由游戏自行组合，于是同样的四十个状态只需十一个条目：

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

`defaults` 会合并进每个条目，像 `stairs` 这样的裸模型名表示 `minecraft:block/stairs`，而 `inventory` 是你手中物品所用的模型。三个楼梯父模型 `stairs`、`inner_stairs` 和 `outer_stairs` 接受 `bottom`、`top` 和 `side` 纹理。

**连接型的类型为每个方向添加一个子模型。** 栅栏、玻璃板或墙的每个方向都有一个布尔值，`true` 会把另一个模型粘到立柱上，而不是替换它：

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

父模型有：带 `texture` 的 `fence_post` 和 `fence_side`；带 `wall` 的 `wall_post` 和 `wall_side`，无立柱的情形再加 `block`；以及带 `pane` 和 `edge` 的 `pane_post`、`pane_side`、`pane_side_alt`、`pane_noside` 和 `pane_noside_alt`。它们每一个都需要 `"uvlock": true`。

其余类型只用一个父模型。`cube_all` 接受一个 `all`，是 `basic`、`ore`、`falling` 或 `leaves` 方块所需要的。`cube_column` 接受 `end` 和 `side`，用于 `log`，并按 `axis` 旋转。`cross` 接受一个 `cross`，是 `flower`、`cane` 或 `sapling` 所需要的；`crop` 则使用它自己的各阶段模型。半砖需要两个自己的模型，下半和上半，因为它是作为形状而不是立方体绘制的。

**示例资源包是实际可用的参考。** [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) 为上表中的每种类型都附带了定义、方块状态和模型，原版格式和 Forge 格式各一份，所以遇到不明显的形状，直接复制比自己琢磨更快。

### 物品模型

*模型、方块状态与纹理*

默认情况下，物品使用方块状态为该变种给出的模型，因此无需再做任何事。在方块上设置 `"itemModel": "item"` 会让它转而查找自己的文件，位于 `models/item/<block>/<variant>.json`。

物品则总是采用后一种方式，因为每个资源包物品都有子类型：

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

路径是物品的注册名，后面跟着变种名称。

流体完全不需要模型，会根据 `still` 和 `flow` 纹理自动生成一个。

### 门、活板门与栅栏门

*模型、方块状态与纹理*

这三者都把全部元数据用在它们所取的形状上，因此各自都是单一变种，在你编写文件之前有几点值得了解。

**它们不带 `blocks` 属性**，所以它们的方块状态只以形状为键：门是 `facing=east,half=lower,hinge=left,open=false`，活板门是 `facing=north,half=bottom,open=false`，栅栏门是 `facing=south,in_wall=false,open=false`。这样一共是 32 个键，16 个加 16 个。

**门和栅栏门省略了 `powered`。** 两者实际上都有这个属性，否则它们的方块状态会因为一个看不出任何变化的轴而翻倍。它会替你去掉，就像游戏对自己的门和栅栏门所做的那样，所以书写键时不要带它。活板门从来没有这个属性。

**让模型指向接受纹理的父模型**，而不是已完成的原版模型：

| 类型 | 父模型 |
| ------------ | ---- |
| `door`       | `block/door_bottom`、`block/door_bottom_rh`、`block/door_top`、`block/door_top_rh`                   |
| `trapdoor`   | `block/trapdoor_bottom`、`block/trapdoor_top`、`block/trapdoor_open`                                 |
| `fence_gate` | `block/fence_gate_closed`、`block/fence_gate_open`、`block/wall_gate_closed`、`block/wall_gate_open` |

门接受两张纹理，`bottom` 和 `top`；另外两种接受一张，`texture`。两个门上半部分的模型都会用 `bottom` 来贴上边缘，所以要在全部四个文件里都声明这两张，尽管上半部分的看起来只需要一张。栅栏门的变种需要 `"uvlock": true`，与游戏自己的一样。

**它们的纹理会用到每一个像素，这一点最容易让人栽跟头。** 门的宽面映射为 `[0, 0, 16, 16]`，即整张图片，而它的窄边以及顶面和底面取自同一个正方形：侧面取第 0 到 3 列，顶面和底面取第 13 到 16 列。活板门也一样，平面取整张图片，四条边缘取自第 13 到 16 行。

所以不要留下空白边距。若你以为形状比文件窄而清空某一边的几列，就会在面的正中间切出一条缝，并且完全丢失顶面和底面。应当把门框或门梃画进这些边缘像素里，它们看起来就像方块自身边缘上的饰边。

**它们的物品因类型而异。** 门的物品是平面精灵图，即 `item/generated` 覆在它自己的 `textures/items/<name>.png` 之上，因为手中的门是作为图片而不是形状绘制的。活板门和栅栏门的物品则以方块模型为父模型，分别是下半部分和关闭的门，这也是游戏对自己的同类物品的做法。

三者都接受你给出的任何 `material`。栅栏门建立在一个会把自身固定为木质的方块之上，所以本模组在注册时把材质设回你指定的，这样石质栅栏门就像它所声称的石头那样用镐挖掘。

### 旗帜

*模型、方块状态与纹理*

旗帜是唯一一种方块形状与模型部件形状各行其道的类型，因此值得完整地讲清楚。

**它会注册两个方块。** 一个定义会给你以自己名称命名的站立旗帜，以及另一个名为 `<name>_wall` 的方块，用于悬挂的旗帜。两者都需要方块状态；只有站立的那个有物品，由该物品决定放置哪一个：点击方块顶部时放置站立的，点击侧面时放置墙上的。你永远不会直接放置墙上的方块，它也不需要自己的物品。

**站立的那个需要 Forge 方块状态。** 它的属性是 `rotation`，取值 `0` 到 `15`，因为旗帜是以十六分之一而不是四分之一圈来转动的。原版方块状态无法表达这一点：它的 `y` 要经过 `ModelRotation`，而后者只接受 0、90、180 和 270，遇到其他值就会抛出异常。Forge 的格式接受任意角度，所以这十六个条目写成变换：

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

……依此类推直到 `15`，每一项再多转 `-22.5` 度。符号与游戏自己的旗帜一致，它们按旋转值的负数转动。请把模型建成朝南，因为玩家面朝南放置旗帜时，旗帜最终指向的就是那里。墙上的方块是普通的原版方块状态，带有常规的四个 `facing` 条目，分别为 0、90、180 和 270，因为它没有任何小数角度。

**模型几乎有两个方块高。** 旗帜在放置和碰撞上只占一个方块，但它的绘制范围远远超出这个方块，止于自身方块顶部的模型看起来会显得矮小。原版的比例以十六分之一方块为单位，值得原样照搬：

| 部件 | 起点 | 终点 |
| ----------- | ------ | ------- |
| 旗杆        | `0`    | `28`    |
| 横杆    | `28`   | `29.33` |
| 旗布       | `2.67` | `29.33` |
| 旗布宽度 | `1.33` | `14.67` |
| 墙上旗布  | `-13`  | `13.67` |

所以站立的旗帜一直延伸到 `29.33`，接近两个方块，而墙上的旗帜则垂在承载它的方块*下方*十三个十六分之一处。模型元素的范围可以从 `-16` 到 `32`，所以两者都放得下。墙上的形式没有旗杆或横杆，只有旗布。

**旗布的高度是宽度的两倍，你的纹理也必须如此。** 该面是 `13.33` 乘 `26.67`。把正方形纹理映射上去，图案就会被压缩到一半高度。方块纹理本身不能是宽度两倍的高度，因为任何非正方形的纹理都会被当作动画读取，所以变通的办法是使用更大的正方形图集，把旗布放在其中一部分：一个 32×32 的文件，把旗布放在 16×32 的区域里，以 `"uv": [0, 0, 8, 16]` 寻址，旁边的空间放旗杆和横杆的条带。UV 坐标无论文件分辨率如何，始终从 0 到 16，所以同样的数字在任何尺寸下都适用。

**它的物品需要自己的模型。** 继承了这么高的模型的物品，在通常的方块缩放下会冲出它的格子，所以请给 `models/item/<name>.json` 一个自己的 `display` 块，把缩放调小，并把整体平移回画面之内。

**它没有颜色或图案。** 资源包旗帜没有方块实体，因此没有任何东西承载原版旗帜保存在方块实体中的图层列表。图案就是纹理，就像门的外观就是它的纹理一样，一个定义就是一面旗帜。给它染色并叠加图案，是资源包无法做到的。

**它采用你给出的 `material`。** 它所建立的方块会把自身固定为木质，所以本模组在注册时把材质设回你指定的，这样石质旗帜就像它所声称的石头那样用镐挖掘。

### 以像素图编写的纹理

*模型、方块状态与纹理*

纹理可以是 JSON 文件而不是 PNG。把它放在 PNG 本应在的位置，并在整个名称后加上 `.json`，这样 `textures/blocks/panel.png.json` 就能响应所有对 `textures/blocks/panel.png` 的请求。其他一切都不变：模型仍然像以往一样指向 `mypack:blocks/panel`，图集、mipmap 和动画 `.mcmeta` 全都有效，因为游戏收到的仍然是 PNG。

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

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| --------- | ----------------- | --------------------------- | ------- | ---- |
| `size`    | 是，或继承 | `widthxheight`              |         | 横向和纵向各有多少像素 |
| `rows`    | 是，或继承 | 文本列表                | | 每行像素一个字符串，每个像素一个字符，从上往下 |
| `palette` | 是，或继承 | 对象                      |         | 字符到颜色的映射，`#RRGGBB` 或 `#AARRGGBB` |
| `extends` | 否                | 另一个像素图           |         | 本像素图所基于的那一张 |
| `tint`    | 否                | 含 `from` 和 `to` 的对象 |         | 沿两种颜色之间的渐变，给所有继承来的内容重新着色 |
| `notes`   | 否                | 对象                      |         | 字符到说明其用途的一行文字的映射，会被继承，绝不绘制 |

**无需声明名称。** 文件自身的路径就是它的名称，与 PNG 完全一样，所以位于 `assets/mypack/textures/blocks/panel.png.json` 的像素图在模型中就是 `mypack:blocks/panel`，位于 `assets/mypack/textures/items/gem.png.json` 的像素图在物品模型中就是 `mypack:items/gem`。没有什么东西会专门指向像素图；方块或物品像往常一样指定它的纹理，永远不会知道自己得到的是两者中的哪一种。这也意味着方块和物品文件夹仍然各自独立，就像 PNG 那样：`textures/blocks/gem.png.json` 和 `textures/items/gem.png.json` 是两张不同的纹理，并作为两个不同的文件缓存。

**尺寸随你定**，每边最多 4096，两边不必相等。`16x16` 是普通的方块面，`16x32` 则是门的一半或动画所需要的那种高条带。尺寸是经过检查而不是猜测的：每行像素给一行、横向每个像素给一个字符，否则该像素图会被拒绝，日志会指出是哪一行以及发现了什么。调色板中没有颜色的字符会保持透明，所以 `.` 或空格就是一个洞。

**模板才是关键。** `extends` 指定另一个像素图，写成 `namespace:path`，或同一资源包内的裸路径，继承它的文件会继承其 `size`、`rows` 和 `palette`。它自己写出的内容优先，而且不必全部写出，所以一个完整的变种可以只是寥寥几种颜色：

```json
{
  "extends": "mypack:textures/blocks/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

这就是一张完整的第二纹理：同样的形状，换成紫珀色，并且如果模板中的形状日后被重绘，所有变种都会随之更新。变种也可以改为给出自己的 `rows` 并保留模板的调色板，这是相反的做法，即相同的颜色配上不同的图案。继承最多嵌套八层，循环会被检测并报告，而指定了无人提供的模板的像素图会被报告，而不是画成空白。

**两张纹理中哪一张是模板**，取决于哪一张包含更多的区分，而不是哪一张先画出来。变种为每个字符给出一种颜色，所以模板中称为同一字符的每个像素在变种中都是同一种颜色。因此，画在石头上的矿石不能继承石头的 `rows`：石头把斑点位置都称为普通石头，而变种无论怎么写都无法把一个字符拆成两个。反过来就行得通。让矿石做模板，石头的色调和矿石的色调就各自有自己的字符，第二种矿石只需四种颜色：

```json
{
  "extends": "mypack:textures/blocks/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

确实想要不同图案的变种会像上面那样给出自己的 `rows`，然后只继承调色板。当颜色才是重点而形状无关紧要时，这样做是值得的；当形状才是重点时，就把形状放进模板，让变种去指定颜色。

**模板完全不必是一张纹理。** 只有当路径以 `.png` 结尾时，像素图才会提供给游戏，所以位于 `textures/blocks/ore_template.json` 的模板对游戏是不可见的，只为被继承而存在，而位于 `textures/blocks/ore_template.png.json` 的则还会响应对 `ore_template.png` 的请求。给共享的形状起名时不带 `.png`，就不会有东西意外地请求它。

**模板也可以是真实的图片而不是像素图。** 把 `extends` 指向任何资源包或游戏本身提供的 PNG，调色板的含义就变了：键成为该图片中已有的颜色，值是要替换进去的颜色。不做任何描摹，也不写 `rows`，所以资源包可以就地为原版或模组纹理重新着色：

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

这就是用原版自己的石头做成的红宝石矿石：四种斑点色调被替换，其他每个像素保持原样。图片中不存在的颜色只会永远匹配不上，尺寸来自图片，除非你自己指定，而指定的尺寸必须与之一致。

`extends` 优先使用像素图：它先在该路径查找像素图，只有在没有资源包提供时才退而使用图片。既不是像素图也不是图片的名称会被报告，而不是画成空白。基于图片的构建是客户端的工作，因为读取的是游戏自己的资源，所以专用服务器从不执行。

**模板可以着色而不必重绘。** `tint` 给出两种颜色，并沿它们之间的渐变给地图所继承的一切重新着色。每个继承来的颜色，其亮度就是它在该渐变上的位置，因此黑色落在 `from`，白色落在 `to`，两者之间的每一种色调按比例混合。透明度保持不变。这样，一张灰度模板加两种颜色就是一个完整的变种：

```json
{
  "extends": "mypack:textures/items/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` 可以省略，此时它是黑色，着色就变成普通的相乘，与渲染时的 `tintindex` 形式相同。区别在于，这种着色是一次性画进 PNG 并缓存的，所以每帧零开销，并且能作用于没有任何东西着色的纹理，但它也无法像 `grass` 或 `foliage` 那样跟随生物群系变化。

模板仍然是一张普通的像素图：打开看看，它画出来就是它本来的灰色。两种颜色都接受 `#RRGGBB`、`#AARRGGBB` 或以 `0x` 开头的形式，两者都不是的值会让该像素图不被绘制，而不是画成错误的颜色。着色与其他内容一样会被继承，沿继承链向下第一个出现的生效，所以变种自己的着色优先于它所继承的那个。它也适用于图片模板，此时在调色板的颜色替换之后运行。

**着色是两种颜色之间的渐变**，所以只适合色调落在同一条渐变上的纹理。拥有两个无关区域的形状，比如矿石的石头与斑点，就不属于此类，需要把调色板完整写出来。

**弄清模板中各字符的含义**是继承模板时比较棘手的部分，这正是上面 `notes` 块的用途：字符到一行简短说明的映射，与调色板一样被继承，绝不绘制。给模板的字符加上标注，继承它的人就知道该覆盖哪些。

`/rdpl pixelmap <namespace:path>` 随后会报告一张像素图实际生成的结果，这是编写变种而不必打开继承链上每个文件的可靠办法：

```
oretest:textures/blocks/ruby_ore.png is 16x16
  built from oretest:textures/blocks/ruby_ore.png.json
  built from oretest:textures/blocks/gem_ore.png.json
  rows come from oretest:textures/blocks/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

每个字符都会列出其颜色、覆盖多少像素、链上哪个文件设定了它，以及该文件说它的用途是什么。路径可以用简短写法 `mypack:blocks/panel`，也可以写全。显示为 0 像素的字符，是调色板中给出了名字而 `rows` 从未使用的字符，这通常是某一行里的拼写错误。

**绘制出的图片保存在磁盘上**，位于 `rdploader/pixelmap-cache`，每个命名空间一个文件夹，文件以纹理名称命名，末尾带有其来源的哈希值。该哈希涵盖整条继承链，即像素图本身和它之上的每一个模板，所以编辑一个模板会改变继承自它的每个变种的标记，它们都会被重绘。一张像素图被重绘时，它较旧的文件会被清除。

每次扫描资源包时，该文件夹也会被检查一遍，凡是不再有任何资源包提供其像素图的图片都会被删除，留下的空文件夹也一并删除。重命名纹理、移除资源包、删除像素图，它缓存的图片都会随之消失，而不是永远留在那里。删除整个文件夹只会损失重新绘制它们的时间，并且扫描资源包时会跳过它，所以它绝不会被误认为资源包。

PNG 永远优先。如果 `panel.png` 和 `panel.png.json` 同时存在，就使用 PNG，像素图永远不会被绘制，所以生成的纹理日后可以被手绘的替换，而无需更改任何指向它的内容。

**没有人需要手写这些文件。** 仓库在 [`pixelmap/`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/pixelmap) 中提供了整个往返流程的脚本：`png_to_pixelmap.py` 把一张 PNG 转为像素图，`convert_pack.py` 对资源包内的每张纹理执行此操作，而 `verify_pack.py` 绘制已转换资源包的像素图并与它们的来源 PNG 比较，这样在把原件收起之前就能确信转换是可靠的。

### 值得留意的陷阱

*模型、方块状态与纹理*

**指向裸原版模型的方块状态也会继承原版的纹理。** `normal_torch`、`ladder`、`wooden_door_*` 和 `wheat_stage*` 都自带纹理，所以指向它们的方块无论你在方块状态里写什么，得到的都是原版的外观。`cube_all`、`cross` 和 `block/crop` 这样的父模型则从方块状态中获取纹理，表现正常，[门、活板门与栅栏门](#门活板门与栅栏门)下列出的门、活板门和栅栏门的父模型也是如此。

**`forge_marker: 1` 不支持多部件。** 藤蔓的方块状态必须是普通的原版多部件，纹理直接写进模型，而不是从外部传入。

**名称来自语言文件，而且一个方块需要两个名称。** 在 `lang/en_us.lang` 给出名称之前，方块或物品显示的都是原始键。你手持并放置的物品，其键是方块的注册名后面跟着变种名，`tile.mypack:ruby_ore.ruby_ore.name=Ruby Ore`，大多数资源包记得的就是这个。方块本身的键只是注册名，`tile.mypack:ruby_ore.name=Ruby Ore`，任何向已放置方块询问名称的地方读取的都是它——容器界面的标题栏就是其中之一。两个都要写，否则物品在手中显示正常，而它所打开的界面却是空白标题。

**单变种类型的名称要写两次。** 可以容纳多个变种的方块，其键如上所述只是注册名。而把全部元数据用于形状的方块，则在后面再加上变种名称，所以在 `blocks/my_door.json` 中定义、只有一个名为 `my_door` 的变种的门，其键是 `tile.mypack:my_door.my_door.name=My Door`。这涵盖了 `door`、`trapdoor`、`fence_gate`、`banner`、`stairs`、`ladder`、`torch`、`crop`、`cane`、`sapling` 和 `vine`。这类类型若有自己的物品，比如门和旗帜，则同样的键还要在 `item.` 而不是 `tile.` 之下再写一遍。

## 让原版正确对待你的方块

*方块与物品*

原版在十几个地方是按身份来检查自己的方块的，所以显然应该能用的资源包方块往往不起作用。有两个键可以解决这个问题。

```json
{
  "material": "ground",
  "plantTypes": ["Plains", "Crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "meta": 0, "hardness": 0.6 } }
}
```

**`plantTypes`** 列出你的方块所支持的 Forge 植物类型，这样树苗、作物和花就可以种在上面。

**`behavesAs`** 让原版把你的方块当作它自己的某种方块来对待：

| 值 | 作用 |
| --------- | ---- |
| `till`    | 锄头可以把它变成耕地 |
| `path`    | 锹可以把它变成草径 |
| `bush`    | 花、草和树苗可以种在上面并留在上面，与泥土相同。等同于 `plantTypes` 中的 `plains` |
| `animals` | 动物在有光照时会在上面生成，与在草方块上一样 |

## 物品

*方块与物品*

`<namespace>/items/*.json`

文件的路径就是物品的注册名，所以 `mypack/items/ruby.json` 会注册 `mypack:ruby`。`variants` 内的键为该物品的各个元数据值命名，每个键对应的模型位于 `models/item/ruby/<key>.json`。

下面一次性展示了所有的键。实际的文件只写需要的那些。标明了适用于某一类型的键，只有该类型才会读取。

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

### 物品类型

*物品*

| 类型 | 得到什么 |
| --------------- | -------- |
| `basic`         | 普通物品。缺少 `type` 时使用 |
| `food`          | 食用，带有饥饿值和饱和度 |
| `drink`         | 饮用而不是食用，返还一个空容器 |
| `tool`          | 由材料制成的镐、斧、锹或剑 |
| `armor`         | 由材料制成的头盔、胸甲、护腿或靴子 |
| `seed`          | 种下你的某种作物 |
| `potion`        | 使用时施加你的药水效果 |
| `potion_bottle` | 容纳你的药水类型，并在创造模式标签页中显示它们 |
| `rocket`        | 在发射台上放置你的某个 Galacticraft 火箭变种 |

`potion_bottle` 用 `potionTypes` 列出它能容纳的内容，这是一个药水类型名称数组，如 `["mypack:ruby_tonic"]`。列表为空的物品不会注册任何东西，日志会说明这一点。

`rocket` 用 `rocket` 指定它所放置的实体变种。它需要 Galacticraft，没有 Galacticraft 或没有 `rocket` 时不会注册任何东西，日志会说明这一点。参见实体变种中的 Galacticraft 火箭。

### 物品文件键

*物品*

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| -------------- | ----------- | ----------------------------------- | ---------------------- | ---- |
| `variants`     | 是         | 变种名称到变种的对象   |                        | 每个元数据值一个条目。键为该值在方块状态、模型路径和语言键中命名。注册名来自文件自身的路径 |
| `type`         | 否          | 上述类型之一              | `basic`                | 物品采用的类型 |
| `creativeTab`  | 否          | 标签页名称                            | 无                   | 它出现在哪个标签页中 |
| `material`     | tool、armor | 材料名称                       | 无                   | 它由你的哪种材料制成 |
| `toolClass`    | tool        | `pickaxe`、`axe`、`shovel`、`sword` | 无                   | 它是哪种工具 |
| `slot`         | armor       | `head`、`chest`、`legs`、`feet`     | 无                   | 穿戴的位置。`helmet`、`chestplate`、`leggings` 和 `boots` 也可以 |
| `eat`          | food        | 布尔值                             | `false`                | 使用进食动画 |
| `alwaysEdible` | food        | 布尔值                             | `false`                | 饥饿条已满时也可以食用 |
| `useDuration`  | 否          | 整数，刻                          | `32`                   | 使用它需要多长时间 |
| `attackSpeed`  | 否          | 浮点数                               | 视工具类别而定 | 对 `tool` 而言，是攻击速度属性，剑的值是 `-2.4` |
| `cooldown`     | 否          | 整数，刻                          | `0`                    | 对 `food`、`drink` 和 `potion` 而言，物品被消耗后多长时间内拒绝再次使用；对带 `rolls` 的物品而言，两次掷骰之间的间隔 |
| `container`    | drink       | 物品名称                           | 无                   | 留下什么，比如一个瓶子 |
| `crop`         | seed        | 方块名称                          | 无                   | 它种下的作物 |
| `soil`         | seed        | 方块名称                          | `minecraft:farmland`   | 它可以种在什么上面 |
| `rocket`       | rocket      | `namespace:name`                    | 无                   | 它放置的实体变种 |
| `rolls` | 否 | `coin`、`d6`、`2d6+1`、一个骰子或一个牌堆 | 无 | 在普通物品上，右键会像 `/rdplserver game` 那样掷骰，并告知资源包的默认听众。参见[骰子与牌堆](#骰子与牌堆) |
| `passesTurn` | 否 | 布尔值 | `false` | 在普通物品上，右键会像 `/rdplserver game pass` 那样结束持有者阵营的行动。参见[轮流行动](#轮流行动) |
| `requires`     | 否          | 模组 ID 或资源包命名空间的列表  | 无                   | 除非全部存在，否则跳过该文件 |

### 物品变种键

*物品*

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ------------ | ----------- | ------------------------------------ | -------- | ---- |
| `meta`       | 是         | 0 到 15                              |          | 该变种占用的元数据值 |
| `maxSize`    | 否          | 1 到 64                              | `64`     | 堆叠上限 |
| `rarity`     | 否          | `common`、`uncommon`、`rare`、`epic` | `common` | 提示框中的名称颜色 |
| `healAmount` | food        | 整数，半个鸡腿                 | `0`      | 恢复的饥饿值 |
| `saturation` | food        | 浮点数                                | `0.0`    | 恢复的饱和度 |
| `oreDict`    | 否          | 矿物词典名称列表         | 无     | 该变种注册所使用的矿物词典名称 |
| `potion`     | food、drink | `potion,duration,amplifier`          | 无     | 该变种被食用或饮用时施加的效果。第四部分 `true` 使其变为环境效果。有益效果会在提示框中注明 |

## 流体

*方块与物品*

`<namespace>/fluids/*.json`

文件的路径就是流体的注册名，除非 `name` 覆盖了它。

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

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ------------- | -------- | ---------------------------------- | --------------------- | ---- |
| `name`        | 否       | 字符串                             | 文件名         | 流体的注册名 |
| `still`       | 否       | 纹理路径                       | 原版静止的水   | 静止流体的纹理 |
| `flow`        | 否       | 纹理路径                       | 原版流动的水 | 流动流体的纹理 |
| `color`       | 否       | 十六进制颜色                          | 无                  | 应用于这些纹理的着色 |
| `bucket`      | 否       | 布尔值                            | `true`                | 为它注册一个桶 |
| `luminosity`  | 否       | 0 到 15                            | `0`                   | 发出的光 |
| `density`     | 否       | 整数                                | `1000`                | 负值会像气体一样向上浮 |
| `temperature` | 否       | 整数，开尔文                        | `300`                 | 水是 300，岩浆是 1300 |
| `viscosity`   | 否       | 整数                                | `1000`                | 流动得有多慢。水是 1000，岩浆是 6000 |
| `gaseous`     | 否       | 布尔值                            | `false`               | 当作气体处理 |
| `creativeTab` | 否       | 标签页名称                           | 无                  | 桶出现在哪个标签页中 |
| `block`       | 否       | 对象                             |                       | 流体方块。`material`（`water`）、`flammability`（`0`）、`fireSpread`（`0`）、`quantaPerBlock`（`0`）、`potions`（无，一个效果列表，施加给站在其中的任何事物，每个写作 `potion,duration,amplifier`，可选的第四部分 `true` 表示环境效果） |
| `requires`    | 否       | 模组 ID 或资源包命名空间的列表 | 无                  | 除非全部存在，否则跳过该文件 |

## 材料、标签页、音效、矿物词典

*方块与物品*

`<namespace>/materials/*.json`

文件的路径就是材料的名称，工具或盔甲物品随后在 `material` 中引用它。

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

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ---------------- | -------- | ----------------- | ----------------------- | ---- |
| `harvestLevel`   | 否       | 0 到 3            | `1`                     | 工具等级。0 木、1 石、2 铁、3 钻石 |
| `durability`     | 否       | 整数               | `250`                   | 损坏前的使用次数 |
| `efficiency`     | 否       | 浮点数             | `6.0`                   | 挖掘速度。钻石是 8 |
| `damage`         | 否       | 浮点数             | `2.0`                   | 攻击伤害加成 |
| `enchantability` | 否       | 整数               | `14`                    | 附魔的优劣程度。金是 22 |
| `repairItem`     | 否       | 物品名称         | 无                    | 在铁砧中用什么修复它 |
| `reduction`      | 否       | 四个整数的列表 |                         | 护甲值，顺序为靴子、护腿、胸甲、头盔 |
| `toughness`      | 否       | 浮点数             | `0.0`                   | 盔甲韧性，与钻石相同 |
| `equipSound`     | 否       | 音效名         | `item.armor.equip_iron` | 穿上盔甲时的音效 |
| `armorTexture`   | 否       | 纹理前缀    | 文件名           | 穿戴时的盔甲纹理 |

### 创造模式标签页

*材料、标签页、音效、矿物词典*

`<namespace>/tabs/*.json`

文件的路径就是标签页的名称，除非 `label` 覆盖了它，方块和物品在 `creativeTab` 中引用它。

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ------- | -------- | --------- | ------------- | ---- |
| `label` | 否       | 字符串    | 文件名 | 标签页的 ID：方块和物品在 `creativeTab` 中引用它，显示的名称来自语言文件中的 `itemGroup.<label>` |
| `icon`  | 否       | 物品名称 | 无          | 标签页上显示的物品 |

### 音效

*材料、标签页、音效、矿物词典*

`<namespace>/sounds/*.json`

文件名由你自己选择，只读取文件夹，多个文件会叠加。

即原版的 `sounds.json` 格式，因此资源包可以附带自己的音频。

### 矿物词典

*材料、标签页、音效、矿物词典*

`<namespace>/oredict/*.json`

文件名由你自己选择，只读取文件夹，多个文件会叠加。

为已经存在的物品添加矿物词典名称。每个键是一个矿物词典名称，其值是注册在该名称下的物品，因此文件本身没有固定的键。资源包自己的方块和物品则改在变种的 `oreDict` 中指定它们的名称。

以 `-` 开头的键表示移除：`"-ingotCopper": ["thermalfoundation:material:128"]` 会把该物品从这个名称上取下，而 `["*"]` 会清空这个名称。使用该名称的配方会立即停止匹配该物品，这正是目的所在。没有任何东西注册的名称会被拒绝并报错，名称并不包含的物品也一样。对所有元数据都注册的条目，如原版注册 `plankWood` 的方式，无论你指定哪个元数据，都会被整体移除，日志会说明这一点；用 `:*` 来指定就是直截了当地表达同样的意思。

```json
{
  "_note": "ruby equivalents",
  "gemRuby": ["mypack:ruby", "mypack:polished_ruby:1"],
  "oreRuby": ["mypack:ruby_ore", "minecraft:redstone_ore"]
}
```

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ------------------------ | -------- | ------------------ | ------- | ---- |
| 矿物词典名称   | 是      | 物品名称列表 |         | 注册在该名称下的物品。元数据作为第三部分，`"mypack:ruby:1"` |
| 以 `_` 开头的名称 | 否       | 任意内容           |         | 被跳过，所以文件可以给自己留一条备注 |

## 属性覆盖

*方块与物品*

`<namespace>/overrides/<target>/<name>.json`

路径指明目标：`overrides/` 之后的所有内容就是被更改的方块、物品或药水类型的命名空间和名称。

在其他任何地方，资源包要么替换一个文件，要么添加一个。覆盖两者都不是：它更改一个已经存在的方块、物品或药水类型的属性，无论是原版还是模组的，而不触碰它的任何文件。路径指明目标，所以 `overrides/minecraft/stone.json` 更改 `minecraft:stone`，`overrides/tconstruct/<name>.json` 以同样的方式更改该模组的方块。

下面一次性展示了所有的键。实际的文件只写需要的那些。

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

每个键都是可选的，文件只更改它所指名的内容，所以位于 `overrides/minecraft/stone.json` 的文件只包含 `hardness`、`light` 和 `soundType`，就能让石头几乎瞬间挖开、发光，并且听起来像玻璃。一个文件可以同时带有方块、物品和药水的键。当目标是方块时，这些键生效：

| 键 | 值 | 作用 |
| -------------- | ---------------------- | ---- |
| `hardness`     | 浮点数                  | 挖掘时间，与方块定义接受的数值相同 |
| `resistance`   | 浮点数                  | 爆炸抗性 |
| `slipperiness` | 浮点数                  | `0.6` 是普通地面，`0.98` 是冰 |
| `light`        | `0` 到 `15`            | 发出的光 |
| `lightOpacity` | `0` 到 `255`           | 方块阻挡多少光 |
| `soundType`    | 音效类型之一 | 脚步、放置和破坏的音效 |
| `harvestTool`  | 工具类别             | 用什么挖掘它；`harvestToolLevel`，默认 `0`，设定等级 |
| `flammability` | 整数                    | 它多容易被烧掉；`fireSpread`，默认 `5`，火多容易蔓延到它 |

### 物品属性

*属性覆盖*

当目标是物品时，这些键生效：

| 键 | 值 | 作用 |
| --------------- | ----------- | ---- |
| `maxStackSize`  | `1` 到 `64` | 堆叠上限 |
| `maxDamage`     | 整数         | 耐久度 |
| `containerItem` | 物品名称   | 留在合成网格中，就像桶那样 |
| `food`          | 对象      | 使物品可食用，见下文 |

既是方块又是物品的名称，而每个可放置方块的物品都是这样，可以从一个文件中同时接受两组键：

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

放在 `overrides/minecraft/planks.json`，这会让木板的破坏速度和泥土差不多，并且可以被吃掉。`food` 接受 `heal`（`1`）、`saturation`（`0.6`）、`alwaysEdible`（`false`；`true` 允许在饥饿条已满时进食）和 `effects`，其条目的写法与药水类型完全相同。已经是食物的物品会接受新的 `heal`、`saturation` 和 `alwaysEdible`；对这类物品使用 `effects` 不受支持，日志会说明这一点。当可食用的物品会放置方块时，要对着天空进食，因为对着方块会把它放下：这是原版的使用顺序，不是缺陷。

### 药水类型效果

*属性覆盖*

文件顶层的 `effects` 会直接重写药水类型的效果列表：

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

放在 `overrides/minecraft/swiftness.json`，迅捷药水现在会赋予飘浮效果。每个条目接受 `potion`（必填）、`duration`（`3600`）、`amplifier`（`0`）、`ambient`（`false`）和 `showParticles`（`true`），与 `potion_types/` 中相同，并且列表不能为空。

### 其他模组、重载与限制

*属性覆盖*

由其他模组拥有的目标应当在 `requires` 中带上该模组，这样当该模组未安装时，文件会被悄悄跳过，而不是被报告为缺失目标：

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

覆盖是实时的。原始值会在第一次更改之前被记住，所以禁用资源包并运行 `/rdpl reload` 就能让一切恢复原样，无需重启；每次进入世界时也会如此。每个目标一个文件：当两个资源包覆盖同一样东西时，较晚的资源包的文件会整个替换较早的那个，日志会说明这一点。

有两个值得了解的限制。自身代码会计算某个属性的方块或物品会忽略背后的字段，所以覆盖虽然生效，却什么也不会改变；原版只在楼梯的爆炸抗性上这样做，但模组可以在任何地方这样做。而且被改为可食用的物品只对没有自己右键行为的物品有效：使用时本来就会做某件事的物品会继续做那件事。

覆盖需要资源包同时存在于客户端和服务器上，因为挖掘速度、光照和进食都发生在玩家的画面上，所以它们不适用于服务端资源包。`content` 配置类别中的 `overrides` 会完全关闭该文件夹。

## 硬度分组

*方块与物品*

`<namespace>/hardness/*.json`

文件的路径只在日志中为分组命名，没有其他东西读取它，所以多个文件会叠加。

给一组方块一个挖掘时间倍率，按每个方块位置掷取。方块本身永远不会被更改：不注册任何东西，不向世界写入任何东西，而不带该资源包打开的世界就是普通的原版。

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

### 挖掘与爆破

*硬度分组*

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ----------------- | -------- | -------------------------------------- | --------- | ---- |
| `blocks`          | 是      | 方块名称或对象的列表         |           | 该分组。与世界生成的 `replace` 相同的三种形式 |
| `except`          | 否       | 方块名称或对象的列表         | 无      | 从分组中取出，无论 `blocks` 怎么写 |
| `miningTime`      | 否       | 数字，或含 `min` 和 `max` 的对象 | `1.0`     | 方块需要多少倍的时间才能破坏，对玩家和 `digs` 生物都一样 |
| `blastResistance` | 否       | 数字，或含 `min` 和 `max` 的对象 | `1.0`     | 乘以方块的爆炸抗性 |
| `buckets`         | 否       | 1 到 256                               | `10`      | 范围被划分成多少级 |
| `minHeight`       | 否       | 整数                                    | `0`       | 低于此高度，掷取结果为最硬的一级 |
| `maxHeight`       | 否       | 整数                                    | `255`     | 高于此高度，掷取结果为最硬的一级 |
| `field`           | 否       | 对象                                 | 见下文 | 掷取结果聚集成的形状 |
| `requires`        | 否       | 模组 ID 或资源包命名空间的列表     | 无      | 除非全部存在，否则跳过该文件 |

单个数字会让分组中的每个方块都得到相同的倍率，不做任何掷取。`min` 和 `max` 则按位置掷取：矿场为空的地方取 `max`，团块中心取 `min`，两者之间的级数由 `buckets` 决定。

### 冒险挖掘与解锁

*硬度分组*

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ------------- | -------- | ---------------- | ------- | ---- |
| `keeps`       | 否       | 布尔值          | `false` | 方块被挖掉后仍留在原处：掉落物、经验、工具磨损和破坏音效都照常发生，而方块仍在那里可以再次挖掘，所以该分组就是一条无尽的矿脉，节奏由 `miningTime` 决定。创造模式照常移除它 |
| `adventure`   | 否       | 对象           | 无    | 谁可以在冒险模式下破坏该分组，否则冒险模式下什么都破坏不了。`tools` 列出手中必须持有其一的物品，留空表示持有任何东西均可；`teams`、`players` 和 `entities` 指明是谁，队伍用名称，玩家用名字，生物用其实体 ID（用于 `digs` 任务），三者都为空则表示任何持有该工具的人。生存和创造模式不受影响 |
| `advancement` | 否       | `namespace:path` | 无    | 只有玩家获得该进度之后，该分组才对其生效。两个分组可以指定同一个方块，一个带进度，一个不带，已解锁的那个优先；没有该进度的玩家得到普通分组，若没有普通分组则是原版。生物不持有进度，所以带进度限制的分组永远不会作用于 `digs` 任务，而爆炸抗性和纹理掷取不属于任何玩家，来自普通分组 |
| `becomes`     | 否       | 对象           | 无    | 任何玩家一旦获得 `advancement`，该分组的方块就会在全世界范围内变成另一种方块：`{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`。每个已加载的区块会立即被扫描，之后加载的区块会在载入时被扫描，之后生成的区块会在其矿石放置之后立即被扫描，所以旧方块会彻底消失。给新方块一个自己的分组，可以改变它的挖掘方式 |

### 矿场

*硬度分组*

掷取并不是对每个方块完全独立进行的，否则硬与软就成了纯粹的杂点，没有任何形状可言。`field` 决定它采取什么形状，而 `type` 在达成这一点的两种方式中择一。

```json
{
  "field": { "type": "speckle" }
}
```

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ------ | -------- | --------------------- | --------- | ---- |
| `type` | 否       | `speckle` 或 `seeded` | `speckle` | 使用下面两种中的哪一种 |

#### speckle

*矿场*

每个方块各自抽取自己的级数，相隔一面的方块可以把较弱的一级传给它。这样得到密集、细粒的斑点，大多数只有一个方块，偶尔在它们相接处出现较大的一片。在这一点上，它更接近本模组所借鉴的那个模组中的挖掘手感。

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| --------- | -------- | -------------------------- | -------------------------------------- | ---- |
| `chances` | 否       | 整数列表，每千分之几 | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | 方块从每一级起步的频率，最软的在最后。剩下的部分就是最硬的一级 |
| `spread`  | 否       | 0.0 到 1.0                 | `0.15`                                 | 一级传给相邻方块的频率，弱一级或弱三级 |

该列表按最软的在最后来读取，所以最后一项是最软的一级，第一项比最硬的高一级。按上面的数字，大约十个方块中有七个是最硬的一级，其余的散布其中。

#### seeded

*矿场*

种子位于由世界和位置算出的格点上，方块的级数取决于它离最近的一个种子有多近。这样得到数量更少、更大、更圆并且相互连接的斑块，并且它可以长出臂。

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

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ---------- | -------- | ------------- | ------- | ---- |
| `cell`     | 否       | 整数，方块   | `8`     | 种子之间相距多远 |
| `seeds`    | 否       | 1 到 4        | `1`     | 每个单元中的种子数 |
| `reach`    | 否       | 浮点数，方块 | `3.0`   | 一个种子的影响延伸多远 |
| `arms`     | 否       | 0 到 6        | `0`     | 从每个种子向外辐射的臂数 |
| `armReach` | 否       | 浮点数，方块 | `0.0`   | 臂延伸多远 |

不写 `arms` 时斑块是圆的。给种子加上臂，就把它变成带卷须的结，而相邻结的臂会相互伸向对方，这就成了矿脉而不是团块。让 `reach` 保持在 `cell` 的一半以上，否则斑块无法相接，你得到的就是彼此之间空无一物的独立圆球。

### 显示方式

*硬度分组*

倍率本身是看不见的。想让玩家看出哪些方块坚硬，就给方块一个方块状态，每一级一个变种，权重全部相等，从最硬的开始列出：

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

Minecraft 本来就会根据方块的位置挑选变种，而硬度分组把级数交给它，这样纹理和倍率始终一致。

有三件事必须做对，而且出错时没有一件会自己提示。

**恰好 `buckets` 个条目，权重全部相同。** 级数被当作列表中的位置使用，所以长度不同的列表，或权重不同的列表，会悄悄指向错误的纹理。

**模型名称前不要带 `block/`。** 方块状态会自己加上 `block/`，所以 `"model": "mypack:step_stone"` 读取的是 `models/block/step_stone.json` 处的文件。写成 `mypack:block/step_stone` 则会去找 `models/block/block/step_stone.json`，而那里什么都没有，该条目会被悄无声息地丢弃。

**与游戏所请求的键相同。** 并不是每个方块都按其属性的读法来设键。原版石头把一切都放在 `normal` 之下，而不是 `variant=stone`，所以只写 `variant=stone` 的覆盖会被合并进去，然后再也没有人看它。两个键都写是安全的，因为合并是按键进行的，而且资源包的优先级高于之前的内容。

开启 `worldgenDebug` 后，每个硬度分组在进入世界时都会与其烘焙好的模型核对，指出方块状态的名称、剩下多少个变种、每个变种最终得到什么纹理，以及游戏为此合并了哪些资源包。这是找出上述三个问题中任何一个的最快办法，当覆盖某个共享方块状态改变了该分组从未提及的某个状态时，它也会发出警告。

### 它不涉及的范围

*硬度分组*

只有玩家自己的挖掘会被更改。破坏方块的机器直接读取方块的硬度，不受影响。玩家放置的方块与其他任何方块一样被掷取，因为掷取属于位置而不属于方块，而被带到别处的方块则采用新位置所给出的值。

---

# 合成、战利品与交易

## 禁用的方块与物品

*合成、战利品与交易*

`<namespace>/disabled/*.json`

文件名由你自己选择，只读取文件夹，多个文件会叠加。

让方块和物品退出游戏，但不注销它们，所以世界保留其 ID，删除该文件就能让一切恢复。原版、模组和资源包的内容一视同仁，资源包自己的方块和物品也包括在内，被禁用的方块会禁用它的物品，被禁用的物品也会禁用它的方块。

```json
{
  "requires": ["thermalfoundation"],
  "names": ["thermalfoundation:ore", "thermalfoundation:material:128", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "oreDict": ["oreTin"]
}
```

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ------------ | -------- | ---------------------------- | ------- | ---- |
| `names`      | 否       | 方块和物品名称的列表 | 无    | 被禁用的内容。元数据作为第三部分，`"thermalfoundation:material:128"`，禁用的是那一个物品，`:*` 则是所有元数据。以 `*` 结尾的名称匹配所有以其余部分开头的名称 |
| `namespaces` | 否       | 模组 ID 列表              | 无    | 该模组的每个方块和物品 |
| `oreDict`    | 否       | 矿物词典名称列表 | 无    | 注册在该名称下的每个物品，并且该名称会被清空 |
| `requires`   | 否       | 模组 ID 列表              | 无    | 除非每一个都已加载，否则跳过该文件。`config:` 和 `file:` 条目的作用与其他任何地方相同 |

被禁用的方块或物品：

- 从每个创造模式标签页和搜索标签页中消失，并在 JEI 和 HEI 中被隐藏
- 没有制作它或使用它的配方：以它为产物的每个合成配方都会消失，每个有一个只有它才能填入的槽位的合成配方也一样，还有每个熔炼它或熔炼出它的熔炉配方。如果槽位还接受别的东西，则保留该配方，而矿物词典槽位则随该名称一起失去这个配方
- 被从每个矿物词典名称中移除
- 从每一次战利品掷取中剔除，包括箱子、生物和钓鱼，也包括方块掉落物和村民交易，掉落的该物品堆叠会消失
- 不能被放置、使用、挥动或拾取，玩家尝试时手中的堆叠会被删除
- 无论该物品的堆叠出现在哪里都会被删除：登录时以及此后每秒从玩家的物品栏和末影箱中删除，玩家打开任何容器时从该容器中删除，并在其区块加载时从箱子和其他物品栏中删除
- 从被放置的世界中移除：随着其区块加载，它的每个方块都会变成空气，其方块实体也一并消失

若要把已放置的方块替换成别的东西而不是移除，请在世界模板中给它们一行 `blockReplacements`，例如 `thermalfoundation:ore=minecraft:stone`，参见[替换](#替换)。替换过程所交换的方块由它负责处理。其他模组保留在自己机器内部的配方属于该模组，不会被涉及。这些文件在启动时只读取一次，配置中的 `content.disabled` 会关闭该文件夹，这需要重启。

若要清空某个矿物词典名称而让它的物品仍然留在游戏中，请改在[矿物词典](#矿物词典)文件中使用 `"-name": ["*"]`。

## 熔炉配方与燃料

*合成、战利品与交易*

`<namespace>/furnace/*.json`

文件名由你自己选择，只读取文件夹，多个文件会叠加。

添加和移除熔炼配方。

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

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ------------ | -------- | --------- | ------- | ---- |
| `input`      | 是      | 物品名称 | 无    | 放入什么 |
| `output`     | 是      | 物品名称 | 无    | 产出什么 |
| `count`      | 否       | 整数       | `1`     | 产出多少个 |
| `experience` | 否       | 数字    | `0.0`   | 每次熔炼的经验。铁矿石给 0.7 |

如果某个添加项的输入已经能被熔炼，则会被忽略，日志会指出该输入现在熔炼成什么；要替换它，请在同一个文件中移除那个配方。

`remove` 下的条目可以是一个裸物品名称，这会移除所有产出它的配方，也可以是一个指明 `input`、`result` 或两者以缩小范围的对象。两者都没有指明的移除项会被跳过，日志会说明这一点。

`<namespace>/fuels/*.json`

文件名由你自己选择，只读取文件夹，多个文件会叠加。

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "oreDict": "gemRuby", "burnTime": 800 }
  ]
}
```

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ---------- | -------------- | ------------------- | ------- | ---- |
| `item`     | 二选一 | 物品名称           | 无    | 会燃烧的物品 |
| `oreDict`  | 二选一 | 矿物词典名称 | 无    | 该名称下的一切都会燃烧 |
| `burnTime` | 是            | 整数，刻          | `0`     | 煤是 1600，木板是 300 |

## 药水、药水类型与酿造

*合成、战利品与交易*

`<namespace>/potions/*.json`

文件的路径就是效果的注册名，所以 `mypack/potions/ruby_sight.json` 会注册 `mypack:ruby_sight`，药水类型随后引用它。

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

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| --------------- | -------- | ----------------------- | ------------------------------------------------------ | ---- |
| `name`          | 否       | 翻译键                 | `effect.<namespace>.<name>`                            | 玩家看到的内容 |
| `color`         | 否       | 十六进制颜色               | `FFFFFF`                                               | 粒子颜色 |
| `badEffect`     | 否       | 布尔值                 | `false`                                                | 算作有害效果，所以发酵蛛眼会将其反转 |
| `beneficial`    | 否       | 布尔值                 | `false`                                                | 显示为增益效果 |
| `instant`       | 否       | 布尔值                 | `false`                                                | 一次性生效而不是持续生效 |
| `effectiveness` | 否       | 浮点数                   | `0.5`                                                  | 生物 AI 对它的重视程度 |
| `icon`          | 否       | 含 `x` 和 `y` 的对象 | `0`、`0`                                               | 图标在图集中的位置 |
| `iconTexture`   | 否       | 纹理路径            | RDPL 图标，设置了 `icon` 时则是原版图集 | 你自己的 18×18 图标 |
| `attributes`    | 否       | 对象列表         | 无                                                   | `attribute`、`uuid`、`amount`（`0.0`）、`operation`（`0`） |

### 药水类型

*药水、药水类型与酿造*

`<namespace>/potion_types/*.json`

文件的路径就是药水类型的注册名，`potion_bottle` 物品随后在 `potionTypes` 中引用它。

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| 键 | 是否必填 | 值 | 默认值 | 作用 |
| ---------- | -------- | --------------- | ---------------------- | ---- |
| `baseName` | 否       | 字符串          | 命名空间和名称 | 药水瓶据以构建的名称 |
| `effects`  | 是      | 对象列表 |                        | 见下文 |

每个效果接受 `potion`（必填）、`duration`（`3600`）、`amplifier`（`0`）、`ambient`（`false`）和 `showParticles`（`true`）。

### 酿造

*药水、药水类型与酿造*

`<namespace>/brewing/*.json`

文件名由你自己选择，只读取文件夹，多个文件会叠加。

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

每个条目要么是 `input`、`ingredient` 和 `output`，把一种物品酿成另一种；要么是 `from`、`ingredient` 和 `to`，把一种药水类型变成另一种。无论哪种，`ingredient` 都是必填的，条目还接受 `requires`，这样可以跳过某一个配方而不必跳过整个文件。

## 铁砧操作

*合成、战利品与交易*

`<namespace>/anvils/*.json`

文件名可自行决定，只读取文件夹，多个文件会叠加。每个文件对应一项操作。

将指定物品放入铁砧左槽，并把它的 `with` 物品放入右槽，铁砧便会按所列的等级消耗，交还附有所列附魔的左侧物品，或交还 `result`；除非设置了数量，否则两边各消耗一个，任一堆叠中剩余的部分仍留在铁砧中。取出成品时还可以获得一项进度，并且在获得该进度之前，该物品可以被限制使用：一把只有经过加工才能挥动的剑。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| -------------- | -------- | ----------------------------------- | ------------- | ----- |
| `item`         | 是      | 物品名，或 `{ "item", "count" }` |               | 放入左槽的物品，以及一次操作需要多少个，默认为一个；其余留给下一次。`{ "item": "minecraft:coal", "count": 8 }` 搭配钻石 `result`，即八个煤炭换一颗钻石。元数据写作 `minecraft:dye:4` |
| `with`         | 是      | 物品名，或 `{ "item", "count" }` |               | 放入右槽的物品，以及消耗多少个，默认为一个：`{ "item": "minecraft:coal", "count": 10 }` 要求至少十个一堆，并消耗十个。铁砧不会对单独一件物品作出回应，因此每项操作都是一对物品                |
| `result`       | 否       | 物品名，或 `{ "item", "count" }` | 左侧物品 | 取代左侧物品产出的物品及其数量，默认为一个，并保留左侧物品的标签，因此一把无法破坏的铁镐加十个煤炭，可以换回一把无法破坏的钻石镐。附魔会附加在最终产出的物品上                         |
| `levels`       | 否       | int                                 | `1`           | 该操作消耗的经验等级，最少为 1                                                                                                                                                                                                             |
| `enchantments` | 否       | 附魔名到等级的对象 | 无          | 物品返还时带有的附魔。已有同等或更高等级的附魔会保持不变，若没有可提升的内容，铁砧不会给出任何结果，除非设置了 `grants`                                                                                          |
| `grants`       | 否       | `namespace:path`                    | 无          | 取出成品时获得的进度。请将其放在 `advancements/` 下，并使用 `impossible` 条件，这样其他途径就无法获得它                                                                                                                                                           |
| `locks`        | 否       | boolean                             | `false`       | 在玩家获得 `grants` 之前，该物品无法用来攻击、使用或挖掘；它进入玩家手中时，会告知玩家它在等待什么。仍然允许把它放进铁砧，解锁正是靠这种方式                                      |

铁砧自身的修复与合并不受影响：只有当左侧是指定物品、右侧是其 `with` 时，这项规则才会响应。

带有 `collectsExperience` 的生物也会在此消耗自己的等级。当它主手持有 `item`、副手持有 `with`，且有足够的 `levels` 可付时，它会走向 16 格内的铁砧，并在 3 格内进行操作：等级从它自身扣除，就像从玩家身上扣除一样，`with` 被用掉，铁砧像在玩家手下一样磨损，成品最终出现在它的主手中。当它走过某项铁砧操作在 `with` 中指定的掉落物时，会把它捡起放入副手。`grants` 和 `locks` 只与玩家有关，因此生物不会从 `grants` 获得任何东西，也不受任何锁定限制。

## 方块掉落物

*合成、战利品与交易*

`<namespace>/block_drops/*.json`

文件名可自行决定，只读取文件夹，多个文件会叠加。

原版 1.12 的方块没有战利品表，因此资源包可以为自己的方块增加掉落，却无法改动石头、矿石或其他模组的方块。这项功能可以做到：一条规则指定一个方块，以及玩家采集它时在常规掉落之外、或取代常规掉落的内容。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| ------------- | -------- | ---------------- | ------- | ----- |
| `block`       | 是      | 方块 id         |         | 规则所监视的方块                                                                                                |
| `meta`        | 否       | int              | `-1`    | 仅限该方块的这一元数据；`-1` 表示所有状态                                                                      |
| `replace`     | 否       | boolean          | `false` | 是否在掷出这些掉落物之前丢弃常规掉落物                                                                         |
| `advancement` | 否       | `namespace:path` | 无    | 规则只对拥有该进度的玩家生效，因此同一个方块在此之前和之后可以掉落不同的东西 |
| `drops`       | 是      | 掉落物列表    |         | 玩家破坏方块时，每一项各自独立掷出                                                                              |

每一项掉落物：

| 键 | 必需 | 值 | 默认值 | 作用 |
| ------------ | ------------------------ | --------------------------- | -------- | ----- |
| `item`       | 是，除非有 `experience` | 物品 id                     |          | 掉落什么，元数据写作 `minecraft:dye:4`                                                                                  |
| `experience` | 否                       | 数字或 `low-high`        |          | 取代物品，以经验球形式掉落相应经验，在范围内均匀掷出。`chance` 和 `silkTouch` 的适用方式与物品相同 |
| `count`      | 否                       | 数字或 `low-high`        | `1`      | 掉落多少，在范围内均匀掷出                                                                                        |
| `chance`     | 否                       | float                       | `1.0`    | 掉落发生的概率，`0.05` 即二十次破坏出一次                                                              |
| `fortune`    | 否                       | int                         | `0`      | 工具上每一级时运，最多额外增加这么多                                                                          |
| `silkTouch`  | 否                       | `either`、`only` 或 `never` | `either` | 掉落是否需要精准采集工具、拒绝精准采集工具，或不在乎                                                         |

规则只处理玩家的采集；爆炸、活塞和生物破坏不会掷出任何东西。同一方块的多条规则会全部生效，其中任何一条的 `replace` 都会先清除常规掉落物。

## 玩家战利品

*合成、战利品与交易*

`<namespace>/player_loot/*.json`

文件名可自行决定，只读取文件夹，多个文件会叠加。

原版 1.12 没有为玩家提供战利品表，死亡时只掉落物品栏，也没有资源包可以覆盖的表名。RDPL 添加了一个，在玩家死亡时掷出：

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| 键 | 必需 | 值 | 默认值 | 作用 |
| --------------------- | -------- | ------------------ | ------- | ----- |
| `table`               | 是      | 表名         |         | 玩家死亡时掷出的战利品表                                                                        |
| `mode`                | 否       | `add` 或 `replace` | `add`   | 该表的物品是加入物品栏，还是取代物品栏                                                        |
| `rollOnKeepInventory` | 否       | boolean            | `false` | 在保留物品栏的死亡中，是否仍然掷出该表                                                     |
| `dropLoose`           | 否       | boolean            | `false` | 物品是否直接放在地面上，而不是加入死亡掉落物 |

`add` 会在物品栏之外另外掉落该表的物品，适用于击杀赏金。`replace` 会丢弃物品栏，只掉落该表掷出的内容。

关闭 `rollOnKeepInventory` 时，`keepInventory` 下的死亡（以及始终保留物品栏的旁观者死亡）不会掷出任何东西。开启它则能让保留物品栏的世界中死亡依然有代价。

多个文件会叠加，各自独立判定。只要有任何一个适用条目是 `replace`，物品栏就会在掷出之前被清空一次，因此与之并存的 `add` 条目仍然会生效。

该表是按名称查找的普通战利品表：它可以放在资源包的 `loot_tables/entities/player.json`，可以是任何原版或模组的表，也可以由 `loot_injections` 触及。战利品上下文：死亡的玩家是被掠夺的实体，击杀者（如有）是击杀玩家，伤害来源已设置——`killed_by_player`、`entity_properties`、`random_chance_with_looting`、`looting_enchant` 和 `quality` 都正常工作。

有一个战利品函数是 RDPL 自有的，可用于任何带有被掠夺实体的表：`rdpl:killed_name` 会以受害者的名字为掉落物命名。`format` 决定显示名称的形式（`%s` 代表受害者，默认只显示名字），而 `tag` 则会把纯名字写入一个 NBT 字符串键，供自己读取它的物品使用。

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**墓碑模组。** 掷出的物品会在任何墓碑模组读取之前加入普通死亡掉落物，因此它们会与其他物品一同进入墓碑（`replace` 则把该表的内容而不是物品栏放入墓碑）。对 Gravestone、GraveStone Mod、Corail Tombstone 以及任何依据死亡掉落列表工作的模组均适用。无需任何设置。

`dropLoose` 完全绕过掉落列表：物品直接放置在世界中，因此墓碑模组看不到它们——物品栏进入墓碑，该表的物品则散落在地上，留给击杀者。适用于属于击杀者而非受害者墓碑的战利品。没有墓碑模组时它几乎没有区别。注意：这些物品在任何下游环节取消掉落之前就已经存在，因此那些在死亡被取消后不应保留的条目，应当关闭它。

在 `data` 配置类别中将 `playerLoot` 设为 `false`，即可完全关闭该文件夹。

## 村民与交易

*合成、战利品与交易*

`<namespace>/villagers/*.json`

文件路径即职业的注册名，因此 `mypack/villagers/jeweller.json` 会注册 `mypack:jeweller`，随后交易在 `profession` 中引用它。

```json
{
  "careers": ["gem_cutter", "appraiser"],
  "texture": "mypack:textures/entity/villager/jeweller.png",
  "zombieTexture": "mypack:textures/entity/zombie_villager/jeweller.png"
}
```

| 键 | 必需 | 值 | 默认值 | 作用 |
| --------------- | -------- | ------------- | --------------------------- | ----- |
| `careers`       | 是      | 名称列表 | 无                        | 该职业提供的专业。没有任何专业的职业会被拒绝 |
| `texture`       | 否       | 纹理路径  | 原版村民        | 村民的外观                                                          |
| `zombieTexture` | 否       | 纹理路径  | 原版僵尸村民 | 僵尸化之后的外观                                           |

### 交易

*村民与交易*

`<namespace>/trades/*.json`

文件名可自行决定，只读取文件夹，多个文件会叠加。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| ------------ | -------- | --------------- | ------- | ----- |
| `profession` | 是      | 职业名 |         | 这是谁的交易                 |
| `career`     | 是      | 专业名     |         | 其中的哪个专业              |
| `level`      | 否       | int             | `1`     | 出现在哪个交易等级      |
| `maxUses`    | 否       | int             | `12`    | 锁定前可以使用的次数 |

一组物品由 `item` 加上 `min`（`1`）和 `max`（默认为 `min`）构成，因此固定价格只需写 `min`。

---

# 生物与危险

## 实体变种

*生物与危险*

`<namespace>/entities/*.json`

文件路径即变种的注册名，因此 `mypack/entities/angry_cow.json` 会注册 `mypack:angry_cow`，`becomes`、刷怪蛋和世界存档引用的都是它。

此处的文件会基于一个已存在的实体制作出一个新实体。它是一个真正独立的实体，拥有自己的注册名、在世界中的名称、刷怪蛋，若你提供，还有自己的战利品表，它建立在另一个实体的行为之上，而不是取代它。被复制的实体不会有任何改变。

此处一次性列出所有键。实际文件只需写用到的那些。

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

### 标识

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --------------- | -------- | ---------------------------------- | ------- | ----- |
| `entity`        | 是      | `namespace:name`                   | 无    | 作为基础的实体。任何模组的实体均可，只要它接受普通的世界构造函数                                                                                              |
| `name`          | 否       | string                             | 无    | 它在世界中、死亡信息里和刷怪蛋上使用的名称                                                                                                            |
| `showName`      | 否       | boolean                            | `false` | 无需注视即可显示名称                                                                                                                                           |
| `egg`           | 否       | boolean 或对象                  | `true`  | 一枚刷怪蛋，颜色与被复制实体的蛋相同。`{ "primary": "AABBCC", "secondary": "112233" }` 可指定你自己的颜色，`false` 则不生成刷怪蛋                 |
| `becomes`       | 否       | 列表                               | 无    | 该变种在生成时可能转变成的其他变种，按权重决定。见下文                                                                                                      |
| `baby`          | 否       | boolean 或 0.0 到 1.0              | `false` | 以幼体生成的频率，并且会一直保持幼体。`true` 为总是，数字则为这一比例的个体                                                                           |
| `keepsBaseBaby` | 否       | boolean                            | `false` | 是否同时保留基础实体自身的幼体判定。不开启时，基于僵尸的变种只会按 `baby` 所说生成幼体，不会有 Forge 的 `zombieBabyChance` 幼体，也不会有鸡骑士 |
| `profession`    | 否       | `namespace:name`                   | 随机  | 对于村民，所从事的职业                                                                                                                                        |
| `career`        | 否       | int                                | 随机  | 该职业中的哪个专业，从 1 起算                                                                                                                           |
| `requires`      | 否       | 模组 id 或资源包命名空间的列表 | 无    | 除非全部存在，否则该变种会被略去                                                                                                        |

变种是独立的一个类，因此包含它的世界会依赖制作它的资源包，就像依赖模组一样。移走该文件，世界中的这些生物也会随之消失。

**用一枚蛋或一个刷怪笼产出混合。** 变种是独立的一个类，所以它自己总是恰好生成它所写的内容。`becomes` 是资源包打破这一点的方式：一个该变种在生成时可能转变成的变种列表，每个带有权重，按每只生物分别决定。

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

写上自己的名字，就是保持原样的方式，而权重就是概率。把它放在 `mypack:walker` 上，一枚蛋和一条生成条目便会产出大多数行尸，偶尔夹杂一个小的，就像僵尸蛋偶尔会给你一个幼年僵尸。它发生在生物进入世界的时刻，因此对刷怪蛋、`/summon` 和自然生成一律有效，到来的生物是所选变种的真正个体，具备该变种所写的一切。刷怪笼则更严格：它只会在与其所设变种具有相同基础生物和相同队伍的变种之间掷选，因此僵尸刷怪笼只会产出该队伍的僵尸及其幼体，绝不会出现其他种类或其他队伍的生物，就像原版僵尸刷怪笼始终是僵尸刷怪笼一样。通过这种方式得到的变种不会再次转变，因此两个变种可以互相指名而不会陷入循环。

**`baby` 的用武之地。** 游戏本身没有幼年僵尸：只有一种僵尸，在生成时判定自己是否为幼体。`baby` 决定频率，因此 `"baby": 0.05` 是原版的习惯，`"baby": true` 则是总是。变种不会在此之上再套用僵尸自身的判定，所以不会出现 `baby` 没有要求的幼体或鸡骑士；`keepsBaseBaby` 会把这一判定还回来。二者其实是达成同一目的的两种方式，该选哪个取决于你想要的差异：单用 `baby` 得到一个有时是幼体的变种，`becomes` 则得到若干在你喜欢的任何方面不同的变种，二者混用也无妨。

### 外观

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| ------------ | -------- | -------------------------------------- | ---------- | ----- |
| `texture`    | 否       | `namespace:textures/entity/<file>.png` | 无       | 专属的皮肤，布局与被复制实体的相同                                           |
| `tint`       | 否       | 十六进制颜色                              | 无       | 在绘制时为实体着色                                                                           |
| `tintParts`  | 否       | `body`、`armor`、`held` 的列表        | `["body"]` | 着色涉及哪些部位                                                                               |
| `scale`      | 否       | float                                  | `1.0`      | 绘制的大小，以及碰撞箱的大小                                                                   |
| `angryScale` | 否       | float                                  | `scale`    | 有攻击目标时它膨胀到的大小，以及失去目标后三秒内的大小           |
| `width`      | 否       | float                                  | 基础实体的 | 碰撞箱的宽度，在应用 `scale` 之前                                                              |
| `height`     | 否       | float                                  | 基础实体的 | 碰撞箱的高度，在应用 `scale` 之前                                                              |
| `glowing`    | 否       | boolean                                | `false`    | 隔墙也能看到轮廓                                                                               |
| `bright`     | 否       | boolean                                | `false`    | 无论站在何处都以最大亮度绘制，如同正午阳光下，因此不会被夜晚、阴影或洞穴压暗 |
| `invisible`  | 否       | boolean                                | `false`    | 不绘制本体，但其装备仍会绘制                                                                   |
| `hideArmor`  | 否       | boolean                                | `false`    | 穿着盔甲但不绘制出来                                                                           |
| `hideHeld`   | 否       | boolean                                | `false`    | 对手中所持之物同样如此                                                                         |
| `leftHanded` | 否       | boolean                                | `false`    | 用另一只手持武器                                                                               |

`scale` 在两端同时改变模型和碰撞箱，因此所见即所击。自行改变大小的生物，如长大中的动物或幼年僵尸，会围绕它选定的大小进行缩放，因此二者不会冲突。`angryScale` 在它有目标时使其膨胀，失去目标时恢复到 `scale`。由于客户端从不被告知生物在追猎什么，奔跑标志承担了这一信息的传递，它只在使用 `angryScale` 的变种上设置，别处不会设置，所以读取你的变种奔跑状态的模组会看到它发生变化。在低矮的天花板下长大是可能的，与史莱姆长大的情形相同，因此差值应保持适度。

`texture` 会取代实体原本使用的那张被绑定，无论它继承的是哪种渲染器，因此对模组实体和原版实体都有效。它必须与绘制所用的模型相匹配，因为模型是基础实体的，这只是一张皮肤，而不是一个新形状。各层保留它们自己的纹理，所以重新换肤的僵尸上盔甲看起来仍像盔甲。

盔甲只会绘制在渲染器带有盔甲层的实体上，在此版本中指人形生物和村民。牛或蜘蛛的变种可以携带盔甲并获得其防护，但不会绘制出来，因此通常用 `attributes` 下的 `armor` 来让这类生物变得结实更为整洁。`hideArmor` 针对另一种情形：人形生物应当保留装备栏中的盔甲，以获得防护或供读取它们的模组使用，但不让人看见。

### 它的音效

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| ------------- | -------- | ------- | ---------- | ----- |
| `sounds`      | 否       | 对象  | 基础实体的 | `ambient`、`hurt` 和 `death`，各为一个已注册的声音事件。另有三个基础实体没有对应音效的：`target` 在它每次选定目标时播放一次，`explode` 取代游戏自带的声音作为其爆炸的声音，无论它是用 `explodes` 自爆还是用 `throws` 投掷 TNT。`throw` 在它用 `throws` 投掷任何东西时播放，取代雪球投掷声，或 TNT 的引信嘶嘶声。`targetVaries` 让每次 `target` 播放的音高在该数值的半音范围内随机上下偏移，因此 `3` 即上下各约四分之一个八度；`0` 则原样播放 |
| `soundVolume` | 否       | 数字  | `1.0`      | 这些声音有多响                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `soundPitch`  | 否       | 数字  | `1.0`      | 播放的音高。小于 1 更低沉，大于 1 更尖细                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `silent`      | 否       | boolean | `false`    | 不发出任何声音                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |

### 生命值、伤害与效果

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| ------------------- | -------- | ----------------------------------------------- | ---------------- | ----- |
| `attributes`        | 否       | 对象                                          | 无             | `maxHealth`、`movementSpeed`、`attackDamage`、`attackSpeed`、`knockbackResistance`、`followRange`、`armor`。实体通常没有的属性会被赋予它。对近战生物，`attackSpeed` 是每秒的攻击次数，游戏中为 `1`，因此 `2` 意味着攻击频率翻倍。基础实体已有的任何名称同样可用，如 `zombie.spawnReinforcements`、`horse.jumpStrength`。在会射击的基础实体上，`attackDamage` 是其箭矢造成的伤害 |
| `absorption`        | 否       | float                                           | `0`              | 在生命值之上额外增加的心                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `invulnerable`      | 否       | boolean                                         | `false`          | 除虚空和创造模式外不受任何伤害                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `fireproof`         | 否       | boolean                                         | `false`          | 完全不会着火，因此永远不会被火或岩浆伤害，也不会在白天燃烧                                                                                                                                                                                                                                                                                                                                                                                           |
| `immuneTo`          | 否       | 伤害类型列表                              | 无             | 它能无视的伤害：`fall`、`drown`、`explosion`、`magic`、`cactus`、`lava`、`wither`、`starve`、`anvil`、`inWall` 等                                                                                                                                                                                                                                                                                                                                       |
| `fallDamage`        | 否       | float                                           | `1.0`            | 乘以坠落造成的伤害。`0` 则免除坠落伤害                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `hurtResistance`    | 否       | int，刻                                      | 基础实体的，`20` | 受击后多久之内无法再次受伤。快于其一半的攻击会被丢弃，因此攻击速度快的攻击者需要一个数值更小的目标                                                                                                                                                                                                                                                                                                                                                   |
| `effects`           | 否       | 对象列表                                 | 无             | 它始终拥有的效果：`{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                                                                                                                                                                                                                                                             |
| `ignoresEffects`    | 否       | 药水 id 列表，或 `all`                    | 无             | 永远不会作用于它的效果，无论是谁或什么施加：一次命中、一瓶喷溅药水、一座信标、一支箭、`/effect`。`all` 会拒绝所有效果，因此变种起初是一张白纸。它自己的 `effects` 仍会加到它身上                                                                                                                                                                                                                                                                |
| `creatureAttribute` | 否       | `undefined`、`undead`、`arthropod` 或 `illager` | 基础实体的       | 它被视作什么，因此节肢杀手、亡灵杀手和治疗药水会相应对待它                                                                                                                                                                                                                                                                                                                                                                                      |

### 移动

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| ---------------- | -------- | ------------- | ---------- | ----- |
| `jumpMultiplier` | 否       | float         | `1.0`      | 比被复制的实体跳得高多少                                                                                        |
| `stepHeight`     | 否       | float，格 | 基础实体的 | 不跳跃就能走上多高的台阶。大多数生物为 `0.6`，僵尸为 `1.0`                                                   |
| `maxFallHeight`  | 否       | int           | 基础实体的 | 寻路时愿意下落多远                                                                                        |
| `climbs`         | 否       | boolean       | 基础实体的 | 像蜘蛛一样爬墙，并在墙上寻路；`false` 会让蜘蛛无法爬墙                                                       |
| `teleports`      | 否       | boolean       | `true`     | 末影人或潜影贝是否可以传送。关闭后，它留在原地，白天和在水中也是如此                                |
| `walks`          | 否       | boolean       | `false`    | 兔子像其他动物一样行走，而不是蹦跳着移动。只有兔子会读取它                                                 |
| `pathPriorities` | 否       | 对象        | 无       | 它愿意穿过什么，如 `WATER`、`LAVA`、`DANGER_FIRE`、`DOOR_WOOD_CLOSED` 等，各为一个数字，负数表示绝不 |
| `leashable`      | 否       | boolean       | `false`    | 可以被拴绳牵引，即使被复制的实体从来不能                                                                    |
| `steerable`      | 否       | boolean       | `false`    | 被骑乘时可以操控                                                                                           |
| `noAI`           | 否       | boolean       | `false`    | 放在哪里就站在哪里，什么也不做                                                                             |

### 水

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| -------------------- | -------- | ------- | ------- | ----- |
| `breathesUnderwater` | 否       | boolean | `false` | 永远不会溺水，并且会下沉到水底行走，而不是游向水面。它在地面上仍然能找到路，因此无法走出的深水会困住它                                                            |
| `swims`              | 否       | boolean | `false` | 像鱿鱼或守卫者一样在水中移动，并且永远不会溺水。它在水中而不是地面上寻路，因此它属于水中，离水就会搁浅                                                       |
| `amphibious`         | 否       | boolean | `false` | 在陆地上行走，在水中则能真正游泳，进出水时会改变寻路方式。它永远不会溺水。它追逐的对象会在水边被遗忘，因此每次过水时都会迟疑片刻 |
| `waterSlowdown`      | 否       | float   | `0.8`   | 水使它减慢多少。数值越高越快                                                                                                                                                                                                |

### 战斗

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --------------- | -------- | -------------------- | ----------------- | ----- |
| `hostile`       | 否       | boolean              | `false`           | 攻击够得着的目标，受伤时会还击。无论其基础实体是什么，敌对变种在游戏看来都算怪物，因此受怪物上限约束，和平模式会清除它，并且它会丢弃基础实体自带的动物任务：繁殖、被引诱、跟随父母、主人或同类、坐下 |
| `passive`       | 否       | boolean              | `false`           | 使它不再攻击任何东西，无论它平时如何表现                                                                                                                                                                                                                                                                                        |
| `targets`       | 否       | 实体名列表 | 玩家        | 敌对时它去寻找什么。`minecraft:player` 可被识别，尽管玩家并不是已注册的实体                                                                                                                                                                                                                                                                                      |
| `attackReach`   | 否       | float，格        | 它的体型          | 近战攻击能够到多远。游戏的触及距离是宽度的两倍，这就是放大的生物能从更远处命中的原因；此键则直接设定它                                                                                                                                                                                                                                 |
| `knockback`     | 否       | float                | 基础实体的，`0.4` | 它的攻击击退得有多猛。`0` 则完全不击退                                                                                                                                                                                                                                                                 |
| `hitEffects`    | 否       | boolean              | `true`            | 是否对被它击中的目标施加被复制实体会施加的效果：凋灵骷髅的凋零、洞穴蜘蛛的中毒、尸壳的饥饿。关闭后，它只造成伤害                                                                                                                                                                                                    |
| `hitFire`       | 否       | boolean              | `true`            | 在被复制实体会点燃目标时，它是否也点燃被击中的目标：燃烧的僵尸、烈焰人的火球。关闭后，它的任何行为都不会在目标身上引发火焰                                                                                                                                                                                                                                                 |
| `threatLeast`   | 否       | int                  | `0`               | 128 格内的玩家或其他携带者必须处于的最低威胁等级，变种才会自然生成。`0` 照常生成                                                                                                                                                                                                       |
| `threatHostile` | 否       | int                  | `0`               | 玩家必须处于的最低威胁等级，变种才会主动攻击他们。低于该等级时，变种对该玩家是温和的，不过被击中时仍会还击。`0` 照常攻击                                                                                                      |

`hostile` 还会去掉让生物逃跑的行为：原本会躲避玩家或受伤时惊慌的动物，一旦敌对就都不会了，否则它会逃离自己本应攻击的对象。它需要一个在地面行走的实体，因为它使用的是原版赋予自己怪物的同一套攻击行为。飞行或游泳的基础实体会被记录到日志并保持原样。`passive` 的适用范围更广，但只能触及按原版方式构建的行为，敌意写在自身刻更新或伤害代码中的模组，不是资源包能劝退的。

### 装备、掉落物与经验

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| -------------------- | -------- | --------------------------- | ---------- | ----- |
| `equipment`          | 否       | 对象                      | 无       | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet`，各为一个物品名                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `dropChance`         | 否       | 0 到 1                      | `0`        | 每件装备掉落的可能性                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `picksUpLoot`        | 否       | boolean                     | `false`    | 捡起它走过的物品                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `lootTable`          | 否       | `namespace:entities/<name>` | 基础实体的 | 它掉落什么。没有此键时，它掉落被复制实体所掉落的东西                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `experience`         | 否       | int                         | 基础实体的 | 它掉落多少经验                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `collectsExperience` | 否       | boolean                     | `false`    | 像玩家一样收集经验：八格内的经验球会飘向它并在接触时被吸收，其装备上的经验修补会优先修复，经验点数按玩家自己的曲线累积成等级，并通过存档保存在生物身上。它击杀的目标会像玩家击杀一样掉落经验，它的 `digs` 任务破坏的方块会掉落该方块自身的经验，`block_drops` 的经验掷出也会落到它身上。死亡时，除非开启了 `keepInventory`，它每级掉落七点，最多一百点。带有 `xp` 或 `level` 条件的计分项会将它的总经验和等级记录在以其 UUID 命名的行上，因此函数可以通过 `score_<objective>_min` 读取。它像玩家一样把等级花在铁砧操作上，见[铁砧操作](#铁砧操作) |

变种掉落被复制实体所掉落的东西，因为战利品表是固定在该实体自己的代码中的，而不是按名称查找的。`lootTable` 会把它指向你自己的表，然后你像其他表一样把它提供在 `loot_tables/entities/<name>.json`。

### 特殊行为

*实体变种*

| 键 | 必需 | 值 | 默认值 | 作用 |
| ---------------- | -------- | ------------ | --------------- | ----- |
| `throws`         | 否       | boolean      | `false`         | 从远处把手中所持之物投向目标，若是 TNT，则点燃它并后撤。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `throwAmmo`      | 否       | int          | 无            | 它有多少可以投掷。不写则永远不会用完                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `throwReload`    | 否       | int，秒 | `explosionFuse` | 手保持空着多久才再拿出一个                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `throwRetreat`   | 否       | int，秒 | `explosionFuse` | 投掷之后躲开多久才转身回来                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `throwPower`     | 否       | float        | `1.0`           | 投掷的力度。翻倍大致使射程翻倍                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `throwArc`       | 否       | float        | `0.35`          | 抛得有多高。越高滞空越久，接近零是平直投掷，低于零则向下投掷                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `throwReturns`   | 否       | boolean      | `false`         | 投出之物像三叉戟一样飞行：命中时造成变种的 `attackDamage` 点伤害，基础实体没有则为 8，然后像忠诚附魔召回三叉戟那样飞回它手中。它永远不会用完，瞄准目标的方式与骷髅相同，`throwPower` 越高越快，难度越高散布越小，并且投掷者在物体飞行期间原地不动，因此 `throwAmmo`、`throwReload`、`throwRetreat` 和 `throwArc` 对它不适用。TNT 仍照常投掷                                                                                                                                                                                                                                                                                                                                                                     |
| `explodes`       | 否       | boolean      | `false`         | 像苦力怕一样在目标身旁自爆。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `explosionPower` | 否       | number       | `3.0`           | 爆炸的大小。苦力怕为 3，TNT 为 4。在苦力怕基础上，它同时也是苦力怕自身的爆炸，在恶魂上则是火球的爆炸                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `explosionFuse`  | 否       | int，刻   | `30`            | 引爆前嘶嘶作响多久。在苦力怕基础上，它同时也是苦力怕自身的引信                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `explosionFire`  | 否       | boolean      | `false`         | 留下火焰                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `charges`        | 否       | boolean      | `false`         | 从远处冲向目标，接触时造成强力击退，就像劫掠兽一样，然后歇息一阵再进行下一次冲锋。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `pounces`        | 否       | boolean      | `false`         | 蹲伏，然后呈弧线跃向目标并在落地时攻击，就像狐狸一样。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `sniffs`         | 否       | int，格  | `0`             | 能听到该格数内玩家的移动声，无论有没有墙，并走向听到声音的地点；潜行或站立不动的玩家听不到，而它随后看到的玩家会成为目标。`0` 表示不监听。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `fleesWhenHurt`  | 否       | 0.0 到 1.0   | `0`             | 当生命值低于该比例时，脱离战斗并从对手面前逃开，回升到该比例以上后再回来。`0` 表示从不逃跑。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `sleepsByDay`    | 否       | boolean      | `false`         | 白天寻找阴影，并在那里站着不动，直到夜晚或被攻击。休息时侧躺着                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `home`           | 否       | int，格  | `0`             | 活动范围限制在它最初所站位置周围该格数以内，在范围内游荡，走出去就走回来。`0` 表示自由漫游                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `patrols`        | 否       | boolean      | `false`         | 与同类一起跟随领队，分成长段在陆地上行走，就像掠夺者巡逻队一样。一起生成的一组会选出一个领队；其余的保持在它几格范围内，领队选定目标时它们全都跟着选定。失去领队的跟随者会自己成为领队。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `swoops`         | 否       | boolean      | `false`         | 在目标上空盘旋并俯冲穿过它，在掠过时攻击，就像幻翼一样。变种会得到一个飞行辅助，因此狩猎时飞行，空闲时落到地面；它需要一个属于生物的基础实体，例如鹦鹉，蝙蝠则不行。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `gusts`          | 否       | boolean      | `false`         | 蓄力并从远处朝目标放出一阵风，把目标附近的一切向后向上掷出，就像微风人的风弹一样。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `gustPower`      | 否       | float        | `1.5`           | 一阵风掷出的力度。生物的一次命中为 0.4，强力击退附魔约为 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `digs`           | 否       | boolean      | `false`         | 用手中的工具挖穿它与目标之间的一切：铲子挖泥土、沙子和沙砾，镐挖石头，斧砍木头，并且只能挖开该工具材质能破坏的方块，因此木镐永远挖不开铁矿石，不到钻石级别就挖不开黑曜石。一个方块所需的时间与玩家用该工具相同，掉落相同的东西，并磨损工具。用 `equipment` 给它工具；空手则什么也挖不了，而在 `mobGriefing` 关闭的地方它也什么都不挖。它从不寻找绕路：有目标时径直走向目标，挖掉挡路的一切，工具无法打开方块时就站着推。需要 `hostile`。它无需看见目标就能选定目标，因为它所朝向挖掘的东西本来就在某物之后 |

**投掷而不是冲锋。** `explodes` 让生物冲上去自爆。`throws` 则是另一种性情：它保持距离，把主手中的任何东西投向正在战斗的对象，若恰好是 TNT，就点燃它、投出，并在它燃烧时后退。

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

投掷会清空它的手，因为它把东西扔了出去。随后它保持 `throwRetreat` 的时间躲开，经过 `throwReload` 后再拿出一个，并转回目标：投掷、后撤、装填、逼近，如此循环。给它设置 `throwAmmo`，这个循环会在数量用完时结束，它的手永久空着，改由普通攻击接手。不写 `throwAmmo`，它就永远不会用完。

这个数量被写入生物本身，因此不会因为区块被卸载再加载而补满。任何不是 TNT 的东西都以物品形式飞出并落地，这使得投掷石头或腐肉的工兵与投掷炸药的一样容易制作。

`explosionFuse` 仍是被投出的 TNT 的引信，并且会代替你省略的任一计时器，因此在这些键出现之前写的变种，行为与以前完全一致。

投掷本身如何飞行由 `throwPower` 和 `throwArc` 决定。前者是对推力的乘数，而推力本来就随距离增长，因此提高它会加长射程，而不改变投掷在空中滞留的时间。后者是升力，它改变轨迹的形状：高则越过墙壁抛射、耗时较长，接近零则平直掷出、几乎立刻落地，低于零则向下投向下方的目标。两者都不影响引信，因此抛射的炸药和平直的炸药，在离手后爆炸的秒数相同，这决定了它是在头顶爆开，还是先落地再等待。它从多远处投掷由它的 `followRange` 决定，你靠近到三格以内时它照常逼近，因此远距离危险，贴脸时则平平无奇。

### 任务

*实体变种*

**游戏拥有的任何任务。** 上面的键是 RDPL 自己的行为。`tasks` 则能触及原版自身使用的每一种任务，适用于任何基础实体：一个条目是一个对象，写明 `task` 及其 `priority`，外加该任务所读取的内容；以 `-` 开头的名称会去掉基础实体自带的该种任务。优先级从 0 起先执行，原版自己的优先级在 1 到 8 之间，因此 0 的任务胜过基础实体的一切行为，而 9 的任务只在没有其他事可做时才运行。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| ------- | -------- | ----- | ------- | ----- |
| `tasks` | 否       | 列表  | 无    | 游戏拥有的任何任务，按名称以你选择的优先级添加到变种上，或从其基础实体自带的任务中去除。列表见下 |

该列表在 `hostile`、`passive` 和上述行为完成各自的工作之后应用，因此它拥有最终决定权。驱动身体移动的任务会互相排斥：一个任务只在优先级更靠前的任务都没有在移动生物时才运行，而怪物自带的攻击位于 2，所以僵尸上的跳扑或逃跑需要优先级 1，否则永远轮不到它；蜘蛛和狼出于同样的原因，把跳扑排在攻击之前。基础实体已在运行的任务会被再添加一次，而不是被替换；请先去掉旧的。有些任务只对具备其所驱动之物的基础实体才有意义：弓箭战斗需要会射击的基础实体，坐下需要可驯服的基础实体，交易需要村民。在无法承载某个任务的基础实体上请求它，日志会说明它需要哪种基础实体，而变种则会放弃该任务。

| 键 | 类型 | 默认值 | 作用 |
| ----------- | ------------------ | ---------------- | ----- |
| `priority`  | int                | 必需         | 它在基础实体各任务中的位置。越小越先运行                                                                                                                 |
| `speed`     | number             | 该任务的通常值 | 任务运行时它移动的速度，作为行走速度的乘数                                                                                           |
| `nearSpeed` | number             | `1.2`            | `avoidEntity`：它所躲避之物靠近时的乘数                                                                                                        |
| `distance`  | number，格     | 该任务的通常值 | 它能看多远、跟随多远、射击多远或保持多远                                                                                                                        |
| `near`      | number，格     | 该任务的通常值 | `follow`、`followOwner`、`followOwnerFlying`：它走近到多近才停下                                                                                       |
| `chance`    | number             | 该任务的通常值 | `wander`：每隔这么多刻掷一次；`wanderAvoidWater`：离开掩体的概率，0 到 1；`watchClosest`、`watchClosest2`：每刻注视的概率，0 到 1 |
| `leap`      | number             | `0.4`            | `leapAtTarget`：跳得多高                                                                                                                 |
| `cooldown`  | int，刻         | `20`             | `attackRanged`、`attackRangedBow`：两次射击之间的刻数                                                                                                                |
| `entity`    | 实体名        | 无             | 任务寻找、躲避、注视或与之繁殖的实体。`minecraft:player` 可被识别                                                                      |
| `items`     | 物品名列表 | 无             | `tempt`：玩家举着的什么                                                                                                                                       |
| `sight`     | boolean            | `true`           | `nearestAttackableTarget`、`targetNonTamed`：只针对它看得见的                                                                                                    |
| `nearby`    | boolean            | `false`          | `nearestAttackableTarget`：只针对在它自身跟随范围内的                                                                                                      |
| `help`      | boolean            | `false`          | `hurtByTarget`：附近的同类也加入战斗                                                                                                                      |
| `memory`    | boolean            | `false`          | `attackMelee`、`zombieAttack`：对失去视线的目标继续追击                                                                                                   |
| `close`     | boolean            | `false`          | `openDoor`：在身后关上门                                                                                                                                  |
| `nocturnal` | boolean            | `false`          | `moveThroughVillage`：只在夜间                                                                                                                                    |
| `scared`    | boolean            | `false`          | `tempt`：玩家移动过快会打破魔咒                                                                                                                     |

`List` 列说明任务位于何处。`tasks` 是生物所做的事；`targets` 是它如何挑选追击对象，而没有匹配攻击的目标任务本身不起任何作用。

| 任务 | 需要 | List | 读取 | 作用 |
| ------------------------- | ------------------------------ | --------- | ------------------------------------------ | ----- |
| `attackMelee`             | 行走的生物                     | `tasks`   | `speed`、`memory`                          | 走到目标面前并攻击它                                                      |
| `attackRanged`            | 会射击的基础实体               | `tasks`   | `speed`、`cooldown`、`distance`            | 保持距离，射击其基础实体所射出的东西                                      |
| `attackRangedBow`         | 会射击的怪物                   | `tasks`   | `speed`、`cooldown`、`distance`            | 骷髅的弓箭战斗：横移、拉弓、放箭                                          |
| `avoidEntity`             | 行走的生物                     | `tasks`   | `entity`、`distance`、`speed`、`nearSpeed` | 指名的实体进入 `distance` 内时逃开                                        |
| `beg`                     | 狼                             | `tasks`   | `distance`                                 | 向举着食物的玩家乞食                                                      |
| `breakDoor`               | 任何基础实体                   | `tasks`   |                                            | 在困难难度下破坏挡路的木门                                                |
| `creeperSwell`            | 苦力怕                         | `tasks`   |                                            | 嘶嘶作响并在目标身旁爆炸                                                  |
| `defendVillage`           | 铁傀儡                         | `targets` |                                            | 攻击袭击过村民的人                                                        |
| `eatGrass`                | 任何基础实体                   | `tasks`   |                                            | 像绵羊一样吃草                                                            |
| `findEntityNearest`       | 任何基础实体                   | `targets` | `entity`                                   | 以最近的指名实体为目标，如史莱姆或恶魂选定目标的方式                      |
| `findEntityNearestPlayer` | 任何基础实体                   | `targets` |                                            | 以够得着的最近玩家为目标                                                  |
| `fleeSun`                 | 行走的生物                     | `tasks`   | `speed`                                    | 被阳光照到时寻找阴影                                                      |
| `follow`                  | 任何基础实体                   | `tasks`   | `speed`、`near`、`distance`                | 跟随同类                                                                  |
| `followGolem`             | 村民                           | `tasks`   |                                            | 跟随举着虞美人的铁傀儡                                                    |
| `followOwner`             | 可驯服的基础实体               | `tasks`   | `speed`、`near`、`distance`                | 跟随主人，落后太远时传送到主人身边                                        |
| `followOwnerFlying`       | 可驯服的基础实体               | `tasks`   | `speed`、`near`、`distance`                | 同上，以飞行方式                                                          |
| `followParent`            | 动物                           | `tasks`   | `speed`                                    | 幼体紧跟同类的成体                                                        |
| `harvestFarmland`         | 村民                           | `tasks`   | `speed`                                    | 收割成熟的作物并重新种上                                                  |
| `hurtByTarget`            | 行走的生物                     | `targets` | `help`                                     | 向打它的东西还击                                                          |
| `landOnOwnersShoulder`    | 鹦鹉                           | `tasks`   |                                            | 停在主人的肩膀上                                                          |
| `leapAtTarget`            | 任何基础实体                   | `tasks`   | `leap`                                     | 从近处向目标跳扑                                                          |
| `llamaFollowCaravan`      | 羊驼                           | `tasks`   | `speed`                                    | 排在被牵着的羊驼后面                                                      |
| `lookAtTradePlayer`       | 村民                           | `tasks`   |                                            | 面向正在与它交易的玩家                                                    |
| `lookAtVillager`          | 铁傀儡                         | `tasks`   |                                            | 注视村民                                                                  |
| `lookIdle`                | 任何基础实体                   | `tasks`   |                                            | 时不时四处张望                                                            |
| `mate`                    | 动物                           | `tasks`   | `speed`、`entity`                          | 进入求爱状态时与同类或指名的 `entity` 繁殖                                |
| `moveIndoors`             | 行走的生物                     | `tasks`   |                                            | 夜幕降临时进入村庄的房屋                                                  |
| `moveThroughVillage`      | 行走的生物                     | `tasks`   | `speed`、`nocturnal`                       | 沿村庄的道路在各家门之间行走                                              |
| `moveTowardsRestriction`  | 行走的生物                     | `tasks`   | `speed`                                    | 走远时朝它的家园位置走回                                                  |
| `moveTowardsTarget`       | 行走的生物                     | `tasks`   | `speed`、`distance`                        | 逼近远处的目标                                                            |
| `nearestAttackableTarget` | 行走的生物                     | `targets` | `entity`、`sight`、`nearby`                | 以最近的指名实体为目标                                                    |
| `ocelotAttack`            | 任何基础实体                   | `tasks`   |                                            | 猫的潜行与扑击                                                            |
| `ocelotSit`               | 豹猫                           | `tasks`   | `speed`                                    | 坐在箱子、床和点燃的熔炉上                                                |
| `openDoor`                | 任何基础实体                   | `tasks`   | `close`                                    | 打开它走过的木门                                                          |
| `ownerHurtByTarget`       | 可驯服的基础实体               | `targets` |                                            | 攻击打了它主人的东西                                                      |
| `ownerHurtTarget`         | 可驯服的基础实体               | `targets` |                                            | 攻击它主人所打的东西                                                      |
| `panic`                   | 行走的生物                     | `tasks`   | `speed`                                    | 受伤或着火时奔逃                                                          |
| `play`                    | 村民                           | `tasks`   | `speed`                                    | 孩子们互相玩捉人游戏                                                      |
| `restrictOpenDoor`        | 行走的生物                     | `tasks`   |                                            | 夜间留在村庄的门内                                                        |
| `restrictSun`             | 行走的生物                     | `tasks`   |                                            | 白天待在阴影里                                                            |
| `runAroundLikeCrazy`      | 马、驴、骡或羊驼               | `tasks`   | `speed`                                    | 甩掉尚未信任的骑乘者                                                      |
| `sit`                     | 可驯服的基础实体               | `tasks`   |                                            | 被命令时坐下                                                              |
| `skeletonRiders`          | 骷髅马                         | `tasks`   |                                            | 玩家靠近时召来骷髅骑手，即陷阱马                                          |
| `swimming`                | 任何基础实体                   | `tasks`   |                                            | 让头保持在水面之上                                                        |
| `targetNonTamed`          | 可驯服的基础实体               | `targets` | `entity`、`sight`                          | 在尚未被驯服时以指名的实体为目标                                          |
| `tempt`                   | 行走的生物                     | `tasks`   | `items`、`speed`、`scared`                 | 跟随举着某件 `items` 的玩家                                               |
| `tradePlayer`             | 村民                           | `tasks`   |                                            | 交易时站着不动                                                            |
| `villagerInteract`        | 村民                           | `tasks`   |                                            | 与其他村民交谈                                                            |
| `villagerMate`            | 村民                           | `tasks`   |                                            | 村庄有空位时繁殖                                                          |
| `wander`                  | 行走的生物                     | `tasks`   | `speed`、`chance`                          | 四处游荡                                                                  |
| `wanderAvoidWater`        | 行走的生物                     | `tasks`   | `speed`、`chance`                          | 游荡，避开水                                                              |
| `wanderAvoidWaterFlying`  | 行走的生物                     | `tasks`   | `speed`                                    | 在空中游荡并栖息在树上                                                    |
| `watchClosest`            | 任何基础实体                   | `tasks`   | `entity`、`distance`、`chance`             | 注视最近的指名实体，未指名则为玩家                                        |
| `watchClosest2`           | 任何基础实体                   | `tasks`   | `entity`、`distance`、`chance`             | 同上，在另一个任务运行时持续保持                                          |
| `zombieAttack`            | 僵尸                           | `tasks`   | `speed`、`memory`                          | 僵尸的攻击，双臂抬起                                                      |

### 生成与消失

*实体变种*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `spawns` | 否 | 对象列表 | 无 | `creatureType`、`weight`、`min` 和 `max`，与生物群系使用的结构相同 |
| `biomes` | 否 | 生物群系名称列表 | 所有生物群系 | 在哪些生物群系中加入这些生成项 |
| `biomeTypes` | 否 | 字典类型列表 | 无 | 同上，但按类型指定 |
| `ignoresSpawnRules` | 否 | 布尔值 | `false` | 放在哪里就在哪里生成，忽略它继承而来的规则 |
| `despawns` | 否 | 布尔值 | `true` | 关闭后，即使通常会被清除，它也会留在世界中 |
| `despawnAfter` | 否 | 整数，秒 | 无 | 在世界中存在这么久之后悄然消失，无论周围有没有玩家 |
| `persistent` | 否 | 布尔值 | `false` | 永不消失 |

**有保质期的生物。** `despawnAfter` 从生物首次进入世界的那一刻起以秒计时，时间一到就悄然将其移除：没有死亡，没有掉落物，没有声音，就像它自己走开后被清除了一样。计时信息写入生物本身，所以存档并重新载入后仍会继续计时，而不是每次区块重新加载时从头开始。

它是独立的机制，不受 `despawns` 和 `persistent` 所管规则的影响。后两者决定游戏是否可以因为生物远离所有玩家而将其清除；这一项则承诺它会在设定的时间消失，不论其他条件。一个生物可以同时 `persistent` 又有保质期，这正适合为一场战斗或活动召唤出来、不应比活动存在更久的生物。

计时依据世界时间，因此无人游玩时会暂停，区块未加载的那段时间也不计入。

### 网络

*实体变种*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `trackingRange` | 否 | 整数 | `80` | 在多远的距离内向客户端通知它的存在 |
| `trackVelocity` | 否 | 布尔值 | `true` | 除位置外同时发送它的速度。关闭可为几乎不动的实体节省流量 |
| `trackingFrequency` | 否 | 整数 | `3` | 更新频率，以刻为单位 |

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

`storage` 可以为任意实体的变种提供物品栏位、流体储罐和能量缓冲，三者各自只在写出对应对象时才会存在。每一项都作为该实体的 Forge 物品、流体或能量能力对外提供，因此任何向实体输送物品、流体或能量的设备都能连接到它。如果原实体自身已经响应该能力，例如生物对手部和护甲、马或运输箱矿车对其物品栏的响应，那么资源包的存储会在每一面取而代之。玩家潜行并右键实体即可打开界面。内容会随实体一同保存。

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `items` | 否 | 对象 | 无 | 为实体提供物品栏位。区域为三行，每行 9 格：每个流体条或能量条占一行，其余行给栏位，因此两个条都有时为 1x9，有一个时为 2x9，都没有时为 3x9。没有 `items` 时只显示条 |
| `fluid` | 否 | 对象 | 无 | 一个流体储罐 |
| `energy` | 否 | 对象 | 无 | 一个 Forge Energy 缓冲 |
| `dropsOnDeath` | 否 | 布尔值 | `true` | 实体死亡时，存储的物品会作为掉落物散落在原地。`false` 则丢失。流体和能量无论如何都会丢失 |
| `runsDry` | 否 | `stops`、`slows` 或 `hurts` | `stops` | 当它付不起整整一秒的 `use` 时会发生什么。`stops`：它不再思考，停在原地，其任务、目标和行为全部闲置，直到重新补充，不过它仍会下落，也能被推动。`slows`：它以一半速度移动。`hurts`：它每秒受到 1 点伤害，如同饥饿，所以用 `immuneTo` 加 `starve` 可以让它免于伤害 |

**靠自身携带的资源运转。** 储罐或缓冲上的 `use` 是一项持续消耗：实体每秒从中取走这么多，不受 `transfer`、`buckets` 和过滤器的限制。当任何一个所存的量不足一整秒的 `use` 时，实体即为耗尽：不再取走任何东西，由 `runsDry` 决定后果，而一旦重新补满就立刻恢复正常。只有生物才会消耗；对于不是生物的原实体，例如矿车，`use` 不起作用。

`items`：

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `filter` | 否 | 过滤条目列表 | 无 | 栏位接受什么。没有它时栏位接受任何东西 |

`fluid`：

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `capacity` | 是 | 整数，mB | 无 | 储罐的容量 |
| `filter` | 否 | 过滤条目列表 | 无 | 储罐接受哪些流体。没有它时接受任何流体 |
| `buckets` | 否 | 布尔值 | `false` | 不潜行时用桶或其他流体容器右键，可将流体倒入储罐或从储罐中取出。没有移动任何流体的点击则交给实体自己处理 |
| `use` | 否 | 整数，每秒 mB | `0` | 实体存活期间每秒从储罐中消耗多少。`0` 表示没有消耗 |

`energy`：

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `capacity` | 是 | 整数，FE | 无 | 能存多少能量 |
| `transfer` | 否 | 整数，FE | 无限制 | 单次操作中最多输入或输出的能量 |
| `use` | 否 | 整数，每秒 FE | `0` | 实体存活期间每秒消耗多少能量。`0` 表示没有消耗 |

过滤条目。第一个匹配的条目起决定作用，没有任何条目匹配的东西会被拒绝：

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `item` | 三选一 | 物品名称 | 无 | 一种物品，写作 `namespace:name` 或 `namespace:name:meta` |
| `oreDict` | 三选一 | 矿物名称 | 无 | 该矿物词典名称下的所有物品 |
| `fluid` | 三选一 | 流体名称 | 无 | 以注册名指定的流体，例如 `water`。仅由流体过滤器读取 |
| `max` | 否 | 整数 | `0` | 同时最多存放多少，按所有栏位合计，流体则以 mB 计。`0` 表示没有上限 |

### Galacticraft 火箭

*实体变种*

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

放置它的物品，位于 `<namespace>/items/supply_rocket.json`：

```json
{
  "type": "rocket",
  "rocket": "mypack:supply_rocket",
  "variants": {
    "supply_rocket": { "meta": 0 }
  }
}
```

Galacticraft 火箭（`galacticraftcore:rocket_t1`、`galacticraftplanets:rocket_t2`、`galacticraftplanets:rocket_t3`）的变种会读取 `galacticraft` 对象。对其他任何实体，该对象会被忽略，日志中会有说明。火箭通过 `rocket` 类型的物品放置在发射台上。破坏火箭，或带着它降落到另一个天体上，会归还该物品，货物和燃料都在其中。

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `tier` | 否 | 整数 | 原火箭的等级 | 火箭等级，决定它能到达哪些天体 |
| `fuelTank` | 否 | 整数 | 原火箭的容量 | 燃料箱大小。1 级火箭为 `1000` |
| `cargoSlots` | 否 | `0`、`27`、`36`、`54` | `0` | Galacticraft 的货舱规格。`27` 提供 18 个栏位，与 Galacticraft 自带火箭相同 |
| `cargo` | 否 | 过滤条目列表 | 无 | 货舱栏位和货物装载器可以放入什么。没有它时接受任何东西。条目与 `storage` 的相同，使用 `item` 或 `oreDict` |
| `requiredPayload` | 否 | 列表 | 无 | 火箭发射前货舱中必须装有什么。每个条目是 `item` 或 `oreDict` 加上 `count`，默认为 `1` |
| `payload` | 否 | 列表 | 无 | 新造出的火箭物品携带什么，在首次放置时装入货舱。每个条目是 `item` 加上 `count`，默认为 `1` |

## 工作订单

*生物与危险*

`<namespace>/orders/*.json`

文件名随意，只读取文件夹，多个文件会叠加。一个文件包含一个 `orders` 列表，旁边还有三个作用于整个包的设置；由第一个设置它的文件决定。

订单是一项工作，由列为其接单者的实体变种为玩家完成：它们在订单范围内挖掘指定的方块，或从其他箱子里搬来指定的物品，再把所得放进订单的箱子。每种开启订单的方式都能在原版客户端上使用：

- **告示牌。** 在箱子上或箱子旁放一块告示牌，在第一行写上订单的 `sign` 词。订单就在该箱子处开启，告示牌第二行显示还需要多少物品。破坏告示牌或箱子会取消订单。
- **工具。** 不潜行时用订单 `tool` 种类的工具右键点击工人。工人收下工具，它原本拿着的工具会还给你，然后它在自身范围内最近的箱子处执行该订单。
- **库存箱。** 在铁砧中把箱子改名为 `stockName` 并放下。它第一行的每个物品都会补满一组：工人从包中第一个 `haul` 订单范围内的其他箱子里取来，沿用该订单的接单者、优先级和范围。

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

`orders` 旁边的设置：

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `stockName` | 否 | 字符串 | `Stock` | 让放下的箱子成为库存箱的名称，不区分大小写。留空则关闭库存箱 |
| `hire` | 否 | 物品名 | 无 | 雇用空闲工人：不潜行时手持此物品右键点击它，它就归你，物品被消耗。空闲工人不属于任何玩家或队伍。元数据写作 `minecraft:dye:4` |
| `spawnCap` | 否 | int | `1` | 没有工人前来时，一个订单最多可在其箱子处生成多少工人。`0` 不生成 |

订单键：

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `job` | 是 | `gather`、`mine`、`farm` 或 `haul` | | `gather` 和 `mine` 挖掘 `blocks` 指定的方块（以方块本身或其掉落物匹配），只要有一面暴露在空气或非完整方块旁。`farm` 收获成熟作物，并用其自身的种子补种。`haul` 把 `blocks` 指定的物品从其他箱子和容器搬进订单的箱子 |
| `blocks` | 是，`farm` 除外 | 矿物词典名 | 无 | 订单想要的东西，例如 `oreIron`、`logWood` 或 `cropWheat`，不区分大小写。在 `farm` 订单上，它只收获会掉落该物品的作物 |
| `area` | 否 | int | `16` | 工作从箱子起延伸多远，按每条轴的方块数计 |
| `areaByTier` | 否 | int 列表 | 无 | 按工人工具的挖掘等级决定范围：第一项用于无工具或等级 0，之后每级一项，超出列表的等级都用最后一项。会覆盖 `area` |
| `deliver` | 否 | `chest` 或 `self` | `chest` | `chest` 把收集到的东西送进订单的箱子。`self` 把它留在工人自己的存储里，订单在收集时计数 |
| `limit` | 否 | int | `64` | 订单关闭前需要多少物品 |
| `standing` | 否 | int | `0` | 大于 `0` 时订单永不关闭：它让箱子里保持这么多物品，少了就工作。此时不读取 `limit` |
| `workers` | 否 | int | `1` | 同时处理该订单的最多工人数 |
| `priority` | 否 | int | `0` | 空闲工人先接优先级最高的开放订单，其次是最近的 |
| `takers` | 是 | 列表 | | 谁可以处理它：变种名、`team:<team>` 表示计分板队伍的任意成员，或 `tag:<tag>` 表示带有该标签的任意实体 |
| `sign` | 否 | 字符串 | 无 | 告示牌第一行上开启该订单的词，不区分大小写 |
| `speed` | 否 | 数字 | `1` | 乘以工人的挖掘速度。`2` 比拿同样工具的玩家快一倍 |
| `tool` | 否 | 工具种类 | 无 | 通过右键把此订单交给工人的工具种类，例如 `pickaxe`、`axe`、`shovel` 或 `hoe`。工具无法采集某方块的工人会回箱子取一把这种工具 |

**工人如何工作。** 被接单者点名的变种，或在任一接单者是队伍或标签时的所有变种，空闲时每秒寻找一次工作。它接下箱子位于自身范围外加 32 格以内的最佳开放订单，以租约持有，停止工作时租约失效，保存和重新载入后租约仍然保留。挖掘耗时与手持工人工具的玩家相同，计入效率，时运影响掉落；精准采集不计入。挖到的东西在变种有 `storage` 物品栏时放进其中，否则放进副手，绝不掉在地上。拿不下时，它走到箱子把所有东西放进去。每挖一个方块都会磨损工具，工具损坏的工人会回箱子取一把同种类的新工具，没有则休息一分钟。工人死亡时会掉落工具和手中的物品，其存储像往常一样只洒出一次。`mobGriefing` 不会阻止它，因为这项工作是玩家要求的。

**谁为谁工作。** 开启订单的玩家在计分板队伍中时，只有该队伍的工人会接单。否则订单归该玩家所有，而工人归第一个给它工作的玩家——通过工具、通过 `hire`，或接下该玩家的订单；此后它只处理该玩家的订单。被队伍中的玩家雇用或递给工具的空闲工人会加入该队伍。

**在箱子处生成。** 一个订单十秒没有工人时，会在其箱子旁生成一个它的变种接单者，每个订单最多 `spawnCap` 个。生成的工人属于订单的队伍或玩家。

## 暴露设置

*生物与危险*

`<namespace>/exposures/*.json`

文件路径就是该危险源的名称，其死亡消息来自语言键 `death.attack.rdpl.<文件名>`。

由资源包定义的危险源：指定的方块、物品和维度会让站在这些方块附近、携带这些物品或身处这些维度的玩家受到暴露，分为若干等级，每个等级施加效果和周期性伤害。危险源也可以从附近的生物和玩家身上传染，或随雨水落下（[传染与天气](#传染与天气)）。一个文件定义一个危险源；多个危险源并行运作。各个键的默认值取自 Immersive World 的辐射所用的数值。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `blocks` | 五选一 | `block` 或 `block=level` 的列表 | | 会让站在附近的玩家受到暴露的方块。不写等级则为 1 |
| `items` | 五选一 | `item` 或 `item=level` 的列表 | | 会让携带或穿戴它们的玩家受到暴露的物品 |
| `dimensions` | 五选一 | `dim` 或 `dim=level` 的列表 | | 会让身处其中的任何玩家受到暴露的数字维度 id |
| `levels` | 是 | 等级列表 | | 严重程度的阶梯，第一项为 1 级。玩家获得任一来源所达到的最高等级 |
| `immunity` | 否 | 药水名称 | 无 | 持有该效果的玩家完全不受暴露 |
| `scanInterval` | 否 | 刻 | `20` | 多久检查一次周围环境和物品栏 |
| `range` | 否 | 方块 | `10` | 方块的暴露范围有多远，按球形计算 |
| `sourcesForNextLevel` | 否 | 整数 | `0` | 附近有这么多个同一等级的来源时，会将该等级再提高一级。`0` 表示关闭此功能 |
| `skipsCreative` | 否 | 布尔值 | `true` | 创造模式和旁观模式的玩家不受影响 |

### 等级

*暴露设置*

每个等级：

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `effect` | 是 | 药水名称 | | 在玩家身上标记该等级的效果。伤害由它是否存在来驱动，所以它应当是资源包为此专门定义的效果 |
| `damage` | 否 | 半颗心 | `0` | 该等级持续期间，每隔 `damageInterval` 刻造成一次的伤害。无视护甲 |
| `damageInterval` | 否 | 刻 | `160` | 该伤害多久生效一次 |
| `effects` | 否 | 效果列表 | 无 | 同时附加的额外效果，结构与药水类型所用的相同。没有 `duration` 时，它们跟随扫描窗口 |

等级效果的持续时间略长于下一次扫描，所以玩家走开后它们会自行消退。因暴露伤害而死亡时，死亡消息读取自 `death.attack.rdpl.<文件名>`，由资源包的语言文件提供。

### 传染与天气

*暴露设置*

在同一个文件中还可以写两种来源。携带者和受暴露的持有者会把危险源传给周围的感染对象，而雨或雷暴会让被淋到的玩家受到暴露。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `carriers` | 五选一 | `entity` 或 `entity=level` 的列表 | | 始终以该等级传播危险源的生物或 `minecraft:player`。不写等级则为 1 |
| `contagious` | 否 | 布尔值 | `false` | 任何受到暴露的对象都会以其所持等级传播危险源 |
| `catchers` | 否 | 实体名称列表 | `minecraft:player` | 谁会被感染。生物只会从携带者和持有者身上感染，绝不会从方块、物品或天气感染 |
| `contagionRange` | 否 | 方块 | `4` | 携带者或持有者的传播范围有多远，按球形计算 |
| `contagionChance` | 否 | `0` 到 `1` | `0.1` | 每次扫描携带者或持有者时，范围内每个感染对象被感染的概率 |
| `contagionDuration` | 否 | 刻 | `1200` | 被感染的危险源会以所感染的等级持续多久。再次感染会让时间重新开始 |
| `weather` | 五选一 | `kind` 或 `kind=level` 的列表 | | `rain` 会让被雨淋到的玩家受到暴露：头顶是开阔天空，且所在生物群系会下雨。`thunder` 在风暴期间生效 |
| `weatherDimensions` | 否 | `dim` 列表 | 所有维度 | 天气会造成暴露的数字维度 id |

被感染的危险源在扫描中算作又一个来源，与其他来源一样取最高等级，`immunity` 同样可以抵御它。只有当 `contagionRange` 和 `contagionChance` 都大于 `0`，并且文件写了 `carriers` 或设置了 `contagious` 时才会传播，而且只有存在这样的文件时才会检查生物。

---

# 世界

## 世界模板

*世界*

`<namespace>/worldtemplates/*.json`

文件路径就是模板的名称，配置选项 `worldTemplate` 可以通过该名称直接选中它。

将一个世界的形态汇集到一个文件中，这样资源包可以一次性提供整个世界，而不必让玩家去设置十几个配置选项。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 否 | 字符串 | 文件名 | 显示在日志和报告中 |
| `default` | 否 | 生物群系名称或 `void` | `void` | 填充被屏蔽移除的生物群系 |
| `roles` | 否 | 角色到生物群系的对象 | 无 | 承担特定角色的生物群系，例如海洋或河流 |
| `structures` | 否 | [结构名称](#值列表)到布尔值的对象 | 无 | 开启或关闭的原版结构 |
| `settings` | 否 | 对象 | 无 | 模板设置的配置值 |
| `dimensions` | 否 | 整数列表 | 所有维度 | 适用于哪些维度 |

`settings` 使用与配置相同的键名，所以不需要学习任何对照表。

当前启用哪个模板由配置选项 `worldTemplate` 决定。保持为 `auto` 时，提供模板的最高优先级资源包胜出，遵循与其他一切相同的顺序。在那里写出模板名称则直接选中它。

## 游戏规则

*世界*

`<namespace>/gamerules/*.json`

文件名由你自己决定，只有文件夹会被读取，多个文件会叠加。

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

每个键是规则所属世界的 id：`0` 为主世界，`-1` 为下界，`1` 为末地，模组为自己的维度使用什么就是什么。值是字符串，与 `/gamerule` 命令中一样，所以写 `"false"` 而不是 `false`。这些规则应用于新世界。维度文件可以在 `gameRules` 块中携带同样的规则，那只会应用于该世界。

## 生物群系

*世界*

`<namespace>/biomes/*.json`

文件路径就是生物群系的注册名，所以 `mypack/biomes/ruby_forest.json` 注册为 `mypack:ruby_forest`。`name` 只是展示给玩家看的名字。

下面一次性展示了所有键。真实的文件只写需要的那些。

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

### 生物群系

*生物群系*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 否 | 字符串 | 文件名 | 显示给玩家的名称 |
| `id` | 否 | 整数 | 自动分配 | 固定的生物群系 id。只有需要它保持稳定时才设置 |
| `types` | 否 | 字典类型列表 | 推测得出 | 将该生物群系注册到这些类型之下，例如 `FOREST`、`COLD`、`WET` 或 `NETHER`，以便其他模组能找到它。省略时，Forge 会根据该生物群系的树木数量、高度、温度、降水量和地面方块做出最佳推测 |
| `baseBiome` | 否 | 生物群系名称 | 无 | 复制其设置的现有生物群系 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该文件 |

### 气候

*生物群系*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `temperature` | 否 | 浮点数 | `0.5` | 低于 0.15 会下雪，高于 1.0 则如沙漠般炎热 |
| `rainfall` | 否 | 浮点数，0 到 1 | `0.5` | 有多湿润 |
| `rain` | 否 | 布尔值 | `true` | 是否会出现天气变化 |
| `snow` | 否 | 布尔值 | `false` | 降水是否以雪的形式落下 |

### 地面与颜色

*生物群系*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `baseHeight` | 否 | 浮点数 | `0.1` | 地形高度。海平面为 0，平原为 0.125 |
| `heightVariation` | 否 | 浮点数 | `0.2` | 起伏有多大 |
| `topBlock` | 否 | 方块名称 | 草方块 | 表层方块 |
| `fillerBlock` | 否 | 方块名称 | 泥土 | 表层正下方的方块 |
| `stoneBlock` | 否 | 方块名称 | 石头 | 地面的主体 |
| `waterColor` | 否 | 十六进制颜色 | `FFFFFF` | 水的色调 |
| `grassColor` | 否 | 十六进制颜色 | 取自气候 | 草的色调，取代由温度和降水量得出的颜色 |
| `foliageColor` | 否 | 十六进制颜色 | 取自气候 | 树叶的色调，方式相同 |
| `snowColor` | 否 | 十六进制颜色 | 取自维度 | 地面积雪的色调，优先于维度的 `snowColor` |

### 装饰与生成

*生物群系*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `decoration` | 否 | 对象 | 原版数量 | 每个区块的数量。读取的名称有 `trees`、`flowers`、`grass`、`deadbush`、`mushrooms`、`bigmushrooms`、`reeds`、`cacti`、`sand`、`gravel`、`clay` 和 `waterlily`，另外还有 `falls`（大于零表示会生成湖泊和泉水）以及 `extratreechance`（多生成一棵树的百分比概率）。其他任何名称都会被记入日志并忽略 |
| `spawns` | 否 | 对象列表 | 原版列表 | 见下文 |
| `keepDefaultSpawns` | 否 | 布尔值 | `false` | 在你的列表之外保留原版的列表 |
| `spawnChance` | 否 | 浮点数，小于 1 | `0.1` | 土地初次生成时再放置一群生物的可能性。只要成功，游戏就会一直继续判定，所以 1 永远不会停止，会一直生成直到没有空间。大于或等于 0.99 的值会被拒绝，改用 0.99 |
| `spawnRates` | 否 | 由 `surfaceDay`、`surfaceNight`、`undergroundDay`、`undergroundNight` 到倍率组成的对象 | 无 | 此处敌对生物的生成频率，取代全局设置。见下文 |

生成条目接受 `entity`（必填）、`type`（`creature`，即某个[生物类型](#值列表)）、`weight`（`10`）、`min`（`1`）和 `max`（`min`）。

`spawnRates` 只针对敌对生物，别无其他。它只接受四个键，没有别的：`surfaceDay` 和 `surfaceNight` 用于能看见天空的地方，`undergroundDay` 和 `undergroundNight` 用于看不见天空的地方。每一项都是敌对生物获准出现频率的倍率，`1` 为正常频率，`0` 完全阻止它们，小于 1 会让一部分尝试失效，大于 1 则会放行游戏本来会拒绝的尝试，所以 `2` 就是两倍的数量。省略某个键表示该生物群系不做决定，改用对应时间和地点的全局设置。写在这里的其他任何内容都不是键，会被忽略，所以以生物类型命名的频率完全不起作用。

### 生成位置

*生物群系*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `placement` | 否 | 对象 | 无 | 它在哪里生成。见下文 |
| `villageType` | 否 | `oak`、`sandstone`、`acacia` 或 `spruce` | 无 | 建在这里的村庄用什么材料建造。留空则用橡木建造，与没有此键时相同 |

`placement`：

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `climate` | 否 | 字符串 | 无 | 加入哪个原版气候分组 |
| `weight` | 否 | 整数 | `10` | 相对于相邻生物群系被选中的频率 |
| `villages` | 否 | 布尔值 | `false` | 可以生成村庄 |
| `villageSpawn` | 否 | 布尔值 | `true` | 村民可以在其中生成 |
| `strongholds` | 否 | 布尔值 | `false` | 可以生成要塞 |
| `playerSpawn` | 否 | 布尔值 | `false` | 世界出生点可以设在这里 |

### 高度带与空岛

*生物群系*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `minHeight` | 否 | 整数 | 无 | 该生物群系作为 3D 生物群系接管的最低 y 值。设置任一高度都会把该生物群系变成一个高度带：此范围之外的柱体保留其自身的生物群系，范围之内世界的每个 4×4×4 单元都会报告为这个生物群系。仅限 Rubic 世界，并且在土地生成时应用，所以已有的土地保持原样 |
| `maxHeight` | 否 | 整数 | 无 | 该高度带的最高 y 值 |
| `replaces` | 否 | 生物群系名称列表 | 所有生物群系 | 将高度带限制在自身生物群系在此列出的柱体上，这样高山带可以只覆盖山地而不涉及其他地方 |
| `skyStone` | 否 | 方块名称 | 世界设置 | 在该生物群系适用的位置，空岛表层之下所用的方块。在高度带上，`topBlock` 和 `fillerBlock` 会用它来铺设岛屿表面，所以高度带是让某一段天空拥有专属空岛的方法 |
| `skyIslands` | 否 | 浮点数，`-1` 到 `1` | 世界设置 | 该生物群系适用的位置的岛屿阈值。越低，聚集的陆地越多 |
| `skyThickness` | 否 | 浮点数，`0` 或更大 | 世界设置 | 该生物群系适用的位置岛屿的坚实程度 |

### 随高度变化的温度

*生物群系*

**随高度变化的温度。** 生物群系随海拔升高而变冷，这正是山顶有雪、某条线以上不再下雨的原因。三个 `terrain` 键可以移动这条曲线，这在 Rubic 世界中很重要，因为那里的地面可能远高于或远低于游戏所假定的高度。默认值就是游戏本身的做法，所以不改动它们的资源包不会产生任何变化。

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

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | 整数 | `64` | 曲线的起算高度。在此高度或更低处，生物群系报告其自身未经改动的 `temperature` |
| `biomeTemperatureHeightFactor` | 浮点数 | `-0.001667` | 在该高度之上每升高一格，温度变化多少，即游戏自身的每 30 格 0.05。负值随海拔变冷，正值变暖 |
| `biomeTemperatureScaleMaxY` | 整数 | `256` | 曲线终止的高度，这样比游戏本身更高的世界不会一直冷到顶部 |

## 维度

*世界*

`<namespace>/dimensions/*.json`

文件路径为 `suffix` 命名该维度，其默认值是 `DIM_<名称>`。维度本身通过其 `id` 来识别，所以其他一切引用的都是那个数字。

下面一次性展示了所有键。真实的文件只写需要的那些。

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

### 顶层

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `id` | 是 | 整数 | | 维度 id。不得与其他模组冲突 |
| `suffix` | 否 | 字符串 | `DIM_<名称>` | 存档文件夹 |
| `keepLoaded` | 否 | 布尔值 | `false` | 无人在其中时仍保持加载 |
| `gameRules` | 否 | 对象 | 无 | 仅在此处适用的规则 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该文件 |

### `terrain` 块

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `type` | 否 | `overworld`、`flat`、`void`、`nether`、`end` | `overworld` | 由哪种生成器构建 |
| `generatorOptions` | 否 | 字符串 | 无 | 生成器字符串，与超平坦预设所用的相同 |
| `structures` | 否 | 布尔值 | `true` | 是否生成原版结构 |

### `biomes` 块

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `source` | 否 | `inherit`、`single` | `inherit` | `inherit` 使用常规生物群系地图，`single` 在所有地方使用同一个生物群系 |
| `biome` | 当为 `single` 时 | 生物群系名称 | `minecraft:plains` | 具体是哪个生物群系 |

### `sky` 块

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | 否 | 布尔值 | `true` | 日光是否能照进来 |
| `surfaceWorld` | 否 | 布尔值 | `true` | 地图和指南针的行为是否与主世界相同 |
| `respawn` | 否 | 布尔值 | `true` | 玩家是否在此重生 |
| `respawnDimension` | 否 | 整数 | 无 | 玩家改为在哪里重生 |
| `spawning` | 否 | 布尔值 | `true` | 生物是否会生成 |
| `nether` | 否 | 布尔值 | `false` | 在传送门和顶部屏障方面按下界处理 |
| `beds` | 否 | 布尔值 | `true` | 关闭后，床会爆炸 |
| `waterVaporizes` | 否 | 布尔值 | `false` | 水会蒸发 |
| `cloudHeight` | 否 | 整数 | `128` | 云所在的高度 |
| `cloudColor` | 否 | 十六进制颜色 | 无 | 云的色调 |
| `cloudSpeed` | 否 | 浮点数 | `1.0` | 云飘动的速度。`0` 使其静止，负值使其反向 |
| `cloudLayers` | 否 | 对象列表 | 无 | 多个云层。见[雾、光照、云与热浪](#雾光照云与热浪) |
| `groundLevel` | 否 | 整数 | `63` | 海平面，用于地平线和出生点搜索 |
| `movementFactor` | 否 | 浮点数 | `1.0` | 与主世界的距离比例。下界使用 8 |
| `fogColor` | 否 | 十六进制颜色或 `sample` | 无 | 正午的雾色调。夜间会像原版的雾一样变暗。`sample` 会将天空与玩家周围的地面混合 |
| `showFog` | 否 | 布尔值 | `false` | 浓雾，如同下界 |
| `fogDensity` | 否 | 浮点数，0 到 1 | `0.0` | 雾有多浓。`0` 保持原版的距离，`1` 将其收拢到 8 格 |
| `fogGroundWeight` | 否 | 浮点数，0 到 1 | `0.5` | 当 `fogColor: sample` 时，地面相对于天空占多大比重 |
| `skyColor` | 否 | 十六进制颜色 | 无 | 正午的天空色调。夜间会变暗，雨天和雷暴时会像原版的天空一样变灰 |
| `fixedTime` | 否 | 整数，刻 | 无 | 锁定一天中的时间 |
| `sunriseColors` | 否 | 布尔值 | `true` | 日出和日落是否带有色调 |
| `ambientLight` | 否 | 浮点数，0 到 1 | `0.0` | 各处的最低亮度 |
| `lightSkyColor` | 否 | 十六进制颜色 | 无 | 方块和生物上日光的色调 |
| `lightBlockColor` | 否 | 十六进制颜色 | 无 | 火把光和其他方块光的色调 |
| `skyFactor` | 否 | 浮点数，0 到 1 | `1.0` | 日光看起来有多亮。仅在客户端绘制，所以生物生成不会改变 |
| `starBrightness` | 否 | 浮点数，0 到 1 | 无 | 星星有多亮 |
| `sunBrightness` | 否 | 浮点数，0 到 1 | `1.0` | 太阳绘制得有多亮 |
| `moonBrightness` | 否 | 浮点数，0 到 1 | `1.0` | 月亮绘制得有多亮，配合 `bodies` 时则是除太阳外的每个天体 |
| `heat` | 否 | 对象 | 无 | 视野上的热浪扭曲。见[雾、光照、云与热浪](#雾光照云与热浪) |
| `renderSky` | 否 | 布尔值 | `true` | 关闭后，不绘制天空、太阳、月亮或星星，只留下雾的颜色 |
| `renderClouds` | 否 | 布尔值 | `true` | 关闭后，不绘制云 |
| `renderWeather` | 否 | 布尔值 | `true` | 关闭后，不绘制雨或雪 |
| `sun` | 否 | 对象 | 无 | 你自己的太阳。见[天空渲染器](#天空渲染器) |
| `bodies` | 否 | 对象列表 | 无 | 悬挂在天空中的行星和卫星。见[天空渲染器](#天空渲染器) |
| `stars` | 否 | 对象 | 无 | 你自己的星空。见[天空渲染器](#天空渲染器) |

### 天空渲染器

*维度*

只要设置了 `sun`、`bodies` 或 `stars` 中的任何一个，就会用 RDPL 自己的天空取代原版天空。它绘制与原版相同的天穹、日出光辉和虚空，但太阳、其他天体和星星取自资源包。它只在客户端绘制，专用服务器从不加载它。`renderSky: false` 依然优先，什么都不绘制，而 `renderClouds: false` 则是做出无云天空的办法。

没有 `bodies` 时，保留原版的月亮及其月相。有了 `bodies`，该列表就是太阳之外的一切，所以空列表就是没有月亮的天空。

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `sun.texture` | 否 | 纹理路径 | 原版太阳 | 太阳的图像 |
| `sun.size` | 否 | 浮点数 | `30` | 在距离 100 处太阳宽度的一半。`0` 将其隐藏 |
| `bodies[].texture` | 是 | 纹理路径 | | 该天体的图像 |
| `bodies[].size` | 否 | 浮点数 | `20` | 在距离 100 处其宽度的一半。原版月亮为 `20` |
| `bodies[].angle` | 否 | 浮点数，度 | `180` | 它在太阳之后沿太阳轨迹落后多远。`180` 是原版月亮所在的位置。关闭 `followsTime` 时，从正上方起算，所以 `0` 是天顶，`90` 是地平线 |
| `bodies[].tilt` | 否 | 浮点数，度 | `0` | 它偏离太阳轨迹多远，向北或向南 |
| `bodies[].followsTime` | 否 | 布尔值 | `true` | 关闭后，它静止悬挂在天空中，而不是随太阳转动 |
| `stars.count` | 否 | 整数 | `1500` | 星星的数量 |
| `stars.size` | 否 | 浮点数 | `0.15` | 最小的星星；最大的星星比它再大三分之二 |

### 雾、光照、云与热浪

*维度*

这些键与较早的键一起位于 `sky` 块中，较早的键仍照常工作。它们全部只在客户端绘制，所以专用服务器会忽略它们，也不会改变任何存档。

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

`fogColor: "sample"` 每秒一次读取玩家周围 33×33 格范围内的顶层方块，根据一天中的时间为它们的地图颜色打光，并与天空颜色混合。雾会逐渐过渡到每次新的采样。`fogDensity` 可以配合采样得到的雾色、设定的雾色或没有雾色使用。在水下、岩浆中以及失明时，保持原版的雾。

`lightSkyColor` 和 `lightBlockColor` 为光照贴图着色，所以每个被照亮的方块和生物都会带上这种色调。`skyFactor` 缩放日光看起来的亮度，而服务器用于生物生成和作物的光照等级保持不变。

没有 `cloudLayers` 时，`cloudSpeed` 改变位于 `cloudHeight` 处那一层原版云的速度。有了 `cloudLayers`，每个条目就是独立的一层，条目省略的内容由 `cloudHeight`、`cloudSpeed` 和 `cloudColor` 补上。`renderClouds: false` 仍然不绘制任何云。

`sunBrightness` 和 `moonBrightness` 在原版雨天淡化效果之上，让太阳和月亮淡出，对原版天空和你自己的天空（来自[天空渲染器](#天空渲染器)）都有效。

`heat` 在玩家所处生物群系的温度不低于 `minTemperature` 时，在视野上叠加波浪状的热浪扭曲。沙漠为 2.0，平原为 0.8。扭曲会在几秒内淡入淡出，在水下保持关闭。它需要显卡支持着色器，并且在另一个全屏着色器（例如旁观者视角）开启时保持关闭。

`mode` 决定热浪扭曲落在哪里。`screen` 在屏幕下部扭曲一条固定的带状区域，无论玩家看向哪里。`world` 则跟随地形：距离小于 `startDistance` 格的地形保持清晰，热浪扭曲向渲染距离的远端逐渐增强，也就是雾气收拢的地方，天空永远不会出现热浪扭曲，无论玩家是向下、平视还是向上看。

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `cloudLayers[].height` | 否 | 浮点数 | `cloudHeight` | 该层所在的高度 |
| `cloudLayers[].speed` | 否 | 浮点数 | `cloudSpeed` | 它飘动的速度。`0` 使其静止，负值使其反向 |
| `cloudLayers[].color` | 否 | 十六进制颜色 | `cloudColor` | 它的色调 |
| `heat.strength` | 否 | 浮点数，0 到 1 | `0.1` | 热浪扭曲有多强 |
| `heat.minTemperature` | 否 | 浮点数 | `1.5` | 会产生热浪的最低生物群系温度 |
| `heat.dayOnly` | 否 | 布尔值 | `true` | 开启时，热浪随日光淡出，夜间消失 |
| `heat.mode` | 否 | 字符串 | `screen` | 热浪扭曲落在哪里，`screen` 或 `world` |
| `heat.startDistance` | 否 | 浮点数 | `32` | 在 `world` 模式下，热浪扭曲从多少格外开始 |

### 雪、流体、星星与闪电

*维度*

这些键同样位于 `sky` 块中，同样只在客户端绘制。

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

`snowColor` 为地面上的雪层和雪块着色。生物群系自己的 `snowColor` 优先于维度的设置，颜色也会像草一样在生物群系交界处过渡混合。

`waterFogColor` 和 `lavaFogColor` 替换镜头在水下或岩浆中看到的雾色。夜晚、深度和夜视仍会像对原版颜色那样让它变暗或变亮。

`starColor` 为星星着色，对原版天空和你自己的[天空渲染器](#天空渲染器)均有效。`starTwinkle` 让星星闪烁：星星分为八组，每组按自己的节奏变暗又变亮，该值决定它们暗下去的程度；为 `1` 时，一组星星在最暗时会完全消失。

`lightningColor` 为闪电着色。

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `snowColor` | 否 | 十六进制颜色 | 白色 | 雪层和雪块的色调 |
| `waterFogColor` | 否 | 十六进制颜色 | `050533` | 水下的雾色 |
| `lavaFogColor` | 否 | 十六进制颜色 | `991A00` | 岩浆中的雾色 |
| `starColor` | 否 | 十六进制颜色 | 白色 | 星星的色调 |
| `starTwinkle` | 否 | 浮点数，0 到 1 | `0.0` | 星星闪烁时变暗的程度。`0` 表示不闪烁 |
| `lightningColor` | 否 | 十六进制颜色 | `737380` | 闪电的色调 |

### 天空盒、极光与彩虹

*dimensions*

这些键同样位于 `sky` 块中，也同样只在客户端绘制。

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

`skybox` 把你自己的图片画在天空上，位于日出霞光、太阳、月亮和星星之后。以完整的纹理路径给出立方体的全部六个面，按立方体展开图排列：`up` 接在 `north` 的上边缘，`down` 接在它的下边缘，`west` 在它左边，`east` 在它右边，`south` 接在 `east` 之后。也可以只给出 `panorama`，用一张 2:1 的图片包住整个天空：图片左边缘朝北，沿顺时针方向经过东、南、西，最上一行在正上方，最下一行在正下方。缺少某个面又没有全景图的天空盒会被跳过，并在日志中记录一条错误。

`aurora` 在夜晚的北方低空挂起发光的光幕。光幕缓缓飘动，在日落时出现，白天和下雨时消失。`color` 是光幕底部的颜色，`topColor` 是它们在顶部淡出时的颜色。

`rainbow` 在白天雨停后于太阳对面显示一道彩虹。彩虹会在雨停后的两分钟内逐渐淡去，再次下雨会让它消失。

原版没有任何键能给维度设置自己的天空图片、极光或彩虹，因此这些键在所有版本中的效果都相同。

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `skybox.<face>` | 否 | 纹理路径 | 无 | 立方体的一个面：`up`、`down`、`north`、`east`、`south` 或 `west`。六个面都要给出 |
| `skybox.panorama` | 否 | 纹理路径 | 无 | 一张包住整个天空的图片，代替各个面 |
| `aurora.color` | 否 | 十六进制颜色 | `40FF90` | 光幕底部的颜色 |
| `aurora.topColor` | 否 | 十六进制颜色 | `8040FF` | 光幕顶部淡出处的颜色 |
| `rainbow` | 否 | 布尔值 | `false` | 雨后显示彩虹 |

### `physics` 块

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `gravity` | 否 | 浮点数，大于 0 | `1.0` | 这里的下落加速度，作为原版的倍数。`0.17` 接近月球 |
| `fallDamage` | 否 | 浮点数，大于 0 | `1.0` | 这里的摔落伤害，作为倍数 |
| `arrowGravity` | 否 | 浮点数，大于 0 | 跟随 `gravity` | 箭在这里下坠的速度，作为倍数 |

这些与[世界物理](#世界物理)中的倍数相同，只是设置在维度上。世界模板中针对该维度的 `dimension=value` 行仍然优先；不带维度的世界模板值只涵盖自己没有任何设置的维度。在 [Galacticraft 天体](#galacticraft-天体)上，由 Galacticraft 应用它们。

### `time` 块

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `dayLength` | 否 | 整数，刻 | `24000` | 这里一个昼夜持续多久。月相仍然每 24000 刻转一圈 |

### `weather` 块

*维度*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `precipitation` | 否 | 布尔值 | `true` | 关闭后，这里永远不会下雨、下雪或有风暴 |
| `lightning` | 否 | 布尔值 | `true` | 关闭后，雨和风暴会来，但没有闪电 |
| `snow` | 否 | 布尔值 | `true` | 关闭后，雪永远不会积起来 |
| `freeze` | 否 | 布尔值 | `true` | 关闭后，水永远不会结冰 |
| `cycle.rainTicks` | 否 | 整数或 `[min, max]` | `[1000, 4600]` | 一场阵雨持续多久 |
| `cycle.clearTicks` | 否 | 整数或 `[min, max]` | `[1000, 3000]` | 两场阵雨之间的晴天持续多久 |
| `cycle.maxStrength` | 否 | 浮点数，大于 0 至 1 | `0.6` | 阵雨最强能到多大。每场阵雨的强度在此值的四分之一到全部之间浮动 |
| `cycle.thunderTicks` | 否 | 整数或 `[min, max]` | 无 | 一场雷暴持续多久。没有它，该周期永远不会出现雷暴 |
| `cycle.calmTicks` | 否 | 整数或 `[min, max]` | `[12000, 180000]` | 两场雷暴之间的平静期持续多久 |
| `cycle.thunderStrength` | 否 | 浮点数，大于 0 至 1 | `1` | 雷暴会变得多暗。只有高于 `0.9` 时才会落雷 |
| `rain.particle` | 否 | 粒子名称 | `droplet` | 雨落地处溅起什么 |
| `rain.sound` | 否 | 音效名称 | `minecraft:weather.rain` | 雨的声音 |
| `rain.volume` | 否 | 浮点数 | `0.2` | 它的音量，雨落在你头顶上方时减半 |
| `rain.interval` | 否 | 整数 | `3` | 声音播放得有多稀疏；越大越稀疏，`0` 则每次机会都播放 |
| `rain.color` | 否 | 十六进制颜色 | `#FFFFFF` | 落下的雨的色调 |
| `rain.snowColor` | 否 | 十六进制颜色 | `#FFFFFF` | 落下的雪的色调 |
| `rain.angle` | 否 | 浮点数，0 到 180 | `0` | 与竖直向下的夹角度数：`90` 为横向吹，`180` 为笔直向上。绘制时最多倾斜 75 度 |
| `rain.heading` | 否 | 浮点数，度 | `0` | 风吹的方向：`0` 为南，`90` 为西，`180` 为北，`270` 为东 |
| `rain.splashUpward` | 否 | 布尔值 | `false` | 开启后，向上飘的雨（`angle` 大于 `90`）仍会在地面溅起水花并发出声音 |
| `wind.gust` | 否 | 浮点数，0 到 90 | `15` | 阵风在最强时给 `angle` 增加的度数，最多到水平 |
| `wind.every` | 否 | 整数或 `[min, max]` | `[200, 600]` | 两阵风之间相隔的刻数 |
| `wind.swing` | 否 | 浮点数，0 到 180 | `30` | 阵风让 `heading` 偏向一侧的度数 |

`wind` 块让雨一阵一阵地刮。每隔一段时间，一阵风会让雨再多倾斜最多 `gust` 度，并把方向向一侧偏转最多 `swing` 度；风势在四秒内起落，阵风每隔 `every` 刻到来。雨和雪随风倾斜，维度的环境粒子也朝雨倾斜的方向飘，无论有没有阵风。只有 `wind` 块而没有 `rain` 块时，雨使用默认值。

其他维度共用主世界的雨。`cycle` 为这个维度提供它自己的天气：阵雨按上述时间来去，不管主世界在做什么。有了 `thunderTicks`，它也会出现雷暴，按它自己的时间；雷暴遇上阵雨时，会使阵雨达到最大强度、让天空变暗，并在 `lightning` 开启时带来闪电。[世界模板](#世界模板)中的 `weatherCeiling` 仍然限制雨能到达的高度。

`rain` 块改变这里雨和雪的外观与声音，无论有没有 `cycle`；没有时它们的外观和声音与原版相同。它在 Galacticraft 的行星或卫星上的作用方式相同。

### `ambience` 块

*维度*

| 键                | 必填  | 值                | 默认值              | 作用                                                                                |
| ---------------- | --- | ---------------- | ---------------- | --------------------------------------------------------------------------------- |
| `music`          | 否   | 音效名称             | 无                | 在这里代替平常曲目播放的音乐，创造模式下也一样。进入时会切断正在播放的曲目                                             |
| `musicDelay`     | 否   | 整数或 `[min, max]` | `[12000, 24000]` | 两首曲目之间安静的刻数                                                                       |
| `loopSound`      | 否   | 音效名称             | 无                | 只要你在这里就循环播放的音效，进入时淡入，离开时淡出                                                        |
| `ambientSound`   | 否   | 音效名称             | 无                | 不时播放的音效，就像现代生物群系添加自己的音效那样。由服务器只发送给那名玩家                                            |
| `soundChance`    | 否   | 0.0 到 1.0        | `0.0111`         | 每刻播放 `ambientSound` 的几率                                                           |
| `particle`       | 否   | 粒子名称             | 无                | 在你周围空气中飘浮的粒子，游戏的粒子名称之一，例如 `depthsuspend`、`townaura`、`reddust` 或 `mobSpellAmbient` |
| `particleChance` | 否   | 0.0 到 1.0        | `0.00625`        | 它的密度，按现代生物群系的算法：每刻在 16 格范围内尝试约 667 个位置，在 32 格范围内再尝试 667 个，每个不是完整方块的位置以此几率显示粒子     |
| `particleColor`  | 否   | 十六进制颜色           | 无                | 可着色粒子的颜色：`reddust`、`mobSpell` 和 `mobSpellAmbient`                                 |

`ambience` 块为维度提供自己的音乐、音效和飘浮粒子，就像现代生物群系那样。视频设置中的“粒子”选项会像减少原版粒子一样减少它们，游戏的洞穴音效照常播放。

## Galacticraft 天体

*世界*

`<namespace>/celestial/*.json` 以及 `<namespace>/dimensions/*.json` 的 `galacticraft` 块

安装了 Galacticraft 后，资源包可以把自己的星系、行星、卫星、小行星带和空间站放到 Galacticraft 的星图上，并让资源包维度成为火箭可以飞往的地点。没有 Galacticraft 时这一切都会被跳过：`celestial/` 文件被忽略，带有 `galacticraft` 块的维度不会被注册，日志中会有说明。

天体在地图上的名称来自资源包的语言文件，使用 Galacticraft 所用的键：`solarsystem.<名称>`、`star.<名称>`、`planet.<名称>`、`moon.<名称>`，空间站则是 `satellite.<名称>`。围绕恒星的小行星带使用 `planet.<名称>`，围绕行星的则使用 `moon.<名称>`。

### 星系与仅地图天体

*Galacticraft 天体*

`<namespace>/celestial/*.json`

这里的文件要么是一个星系，要么是一颗位于地图上却无处可降落的行星或卫星。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `kind` | 是 | `system`、`planet`、`moon` | | 该文件创建什么 |
| `galaxy` | 否 | 字符串 | `milky_way` | 星系所属的星系团 |
| `mapPosition` | 星系必填 | `[x, y]` 或 `[x, y, z]` | | 该星系在银河系地图上的位置 |
| `star` | 否 | [地图键](#地图键)对象 | | 星系的恒星，绘制在中心 |
| `requires` | 否 | 模组 id 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该文件 |

这里的行星或卫星接受[地图键](#地图键)，其 `tier` 默认值为 `0`。行星和卫星在所有星系之后才放到地图上，所以行星可以围绕同一资源包中的星系运行。

### 地图键

*Galacticraft 天体*

每个天体，无论是 `celestial/` 文件、星系的 `star` 还是维度的 `galacticraft` 块，都用这些键把自己放到地图上。

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 否 | 字符串 | 文件名 | 天体的名称，其语言键和每个 `parent` 都使用它 |
| `parent` | 卫星或空间站必填 | 名称 | 行星或带为 `sol` | 行星所围绕的星系、卫星所围绕的行星、空间站所围绕的行星或卫星，或小行星带所围绕的星系或行星 |
| `icon` | 否 | 纹理路径 | Galacticraft 的火星、月球、太阳、小行星或空间站图标 | 地图上的图像 |
| `relativeSize` | 否 | 浮点数 | `1.0`，卫星或空间站为 `0.2667` | 它在地图上的大小 |
| `distance` | 否 | 浮点数 | `1.0`，卫星为 `13`，空间站为 `9` | 它的环绕距离有多远 |
| `scaledDistance` | 否 | 浮点数 | `distance` | 放大地图上使用的距离 |
| `orbitTime` | 否 | 浮点数，年 | `1.0`，卫星为 `100`，空间站为 `20` | 在地图上绕轨道一圈需要多久。负值为逆向运行 |
| `phaseShift` | 否 | 浮点数，弧度 | `0`，空间站错开分布 | 它在轨道上的起点。未设置的空间站，起点比围绕同一行星的上一个空间站（包括其卫星的空间站）晚 2.4 弧度，这样没有两个空间站会占同一位置 |
| `ringColor` | 否 | 十六进制颜色 | `19E599` | 地图上绘制轨道线时使用的颜色 |
| `tier` | 否 | 整数 | 维度为 `1`，空间站取其父天体的等级，其他为 `0` | 地图显示它所需的火箭等级。安装 GalaxySpace 时，AsmodeusCore 的地图改为根据距离推算等级，围绕另一颗恒星的天体需要最高等级，除非 `config/AsmodeusCore/core.conf` 中关闭了 `enableNewTierSystem` |

### `galacticraft` 块

*Galacticraft 天体*

`<namespace>/dimensions/*.json`

维度文件中的 `galacticraft` 块会让该维度成为自己的行星或卫星，或者成为[小行星带](#小行星带)或[空间站](#空间站)。块之外的一切，即天空、物理、时间和天气，仍属于该维度，不论有没有 Galacticraft 都以同样方式工作；该块只存放 Galacticraft 读取的内容。

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

该块接受[地图键](#地图键)，另外还有：

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `kind` | 否 | `planet`、`moon`、`asteroids`、`station` | `planet` | 该维度是什么 |
| `reachable` | 否 | 布尔值 | `true` | 关闭后，它会出现在地图上，但没有火箭能去那里 |
| `minTier` | 否 | 整数 | `tier` | 允许降落的最低火箭等级 |
| `landing` | 否 | `lander`、`parachute`、`balloons` | `lander` | 玩家如何降落。`balloons` 需要 Galacticraft Planets，没有它时就是着陆器。Galacticraft 的 `disableLander` 会强制使用 `parachute` |
| `landingHeight` | 否 | 浮点数 | `900`，降落伞为 `250` | 玩家抵达时所处的高度 |
| `arrival` | 否 | `departure`、`spawn` | `departure` | 降落在火箭起飞位置的上方，还是该维度出生点的上方 |
| `exitHeight` | 否 | 浮点数 | `1200` | 离开该维度的火箭在多高处离开此维度 |
| `rocketGui` | 否 | 纹理路径 | Galacticraft 的主世界界面 | 飞行界面 |
| `checklist` | 否 | 字符串列表 | 无 | 发射前显示的 Galacticraft 检查清单键 |
| `meteorFrequency` | 否 | 浮点数 | 取自 `density` | 流星坠落得有多稀少，每名玩家附近大约每隔此数乘以 750 刻一次。`0` 使其停止 |
| `fuelMultiplier` | 否 | 浮点数 | `1.0` | 从这里出发的火箭消耗的燃料 |
| `soundReduction` | 否 | 浮点数 | 取自 `density` | 这里的空气中声音减弱多少 |
| `solarEnergy` | 否 | 浮点数 | `1.0` | 太阳能板在这里的输出 |
| `netherPortals` | 否 | 布尔值 | `false` | 下界传送门能否在这里点燃 |

| `atmosphere` 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `gases` | 否 | `NITROGEN`、`OXYGEN`、`CO2`、`WATER`、`METHANE`、`HYDROGEN`、`HELIUM`、`ARGON` 的列表 | 无 | 空气成分。没有则为真空，而火焰需要 `OXYGEN` |
| `breathable` | 否 | 布尔值 | 有氧气且无 CO2 | 玩家不穿太空服能否呼吸 |
| `corrosive` | 否 | 布尔值 | `false` | 没有护盾控制器时会腐蚀护甲 |
| `temperature` | 否 | 浮点数 | `0` | Galacticraft 的热等级。低于 0 为寒冷，高于 0 为炎热；由保温衬垫来应对 |
| `wind` | 否 | 浮点数 | 有气体时为 `1.0`，没有时为 `0` | 使旗帜飘动并驱动风力发电 |
| `density` | 否 | 浮点数 | `1.0` | 空气有多稠密。它决定流星和声音的默认值 |

| `dungeon` 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `spacing` | 否 | 整数，方块 | `0` | Galacticraft 地牢之间的距离。`0` 表示没有 |
| `chest` | 否 | 战利品表 | 无 | 它们箱子中的战利品 |

Galacticraft 只在它自己的地形中建造地牢，所以 `dungeon` 只在有东西运行 Galacticraft 的地牢生成器的地方才有意义；RDPL 地形不会。

行星上的雨看起来和听起来如何，由该维度自己的 [`weather.rain`](#weather-块) 块决定，如上面的示例所示。

**由谁注册维度。** 可到达天体的维度由 Galacticraft 注册，所以火箭和多人游戏客户端能看到它；带有 `reachable: false` 的则由 RDPL 注册。如果天体无法被放置，原因是其父天体未知或名称已被占用，则该维度不会被注册，日志中会说明原因。

### 小行星带

*Galacticraft 天体*

`<namespace>/dimensions/*.json`

`galacticraft` 块带有 `kind: "asteroids"` 的维度是小行星带：即 Galacticraft 自己的小行星场，生成在空旷的虚空中。它需要 Galacticraft Planets，以及类型为 `void` 的 `terrain`；缺少任何一项，该维度都不会被注册，日志中会说明原因。

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

小行星带的 `parent` 是一个星系或一颗行星。围绕星系时，它在地图上与行星一样，围绕行星时则与卫星一样。它接受 [`galacticraft` 块](#galacticraft-块)的所有键，下面这些除外，小行星带会忽略它们：

| 键 | 在小行星带中 |
| --- | --- |
| `landing` | 玩家在最近的小行星上的进入舱中抵达，与 Galacticraft 自己的小行星带相同 |
| `landingHeight` | 进入舱自行决定高度 |
| `arrival` | 进入舱挑选小行星 |
| `dungeon` | 资源包的小行星带没有废弃基地 |

天空、重力、时间和天气是该维度自己的键，与任何资源包行星一样。其中五项在小行星带中的默认值不同，以与 Galacticraft 自己的保持一致：

| 键 | 未设置时 |
| --- | --- |
| `sky.fogColor` | `000000`，所以雾和地平线是黑色的 |
| `sky.renderClouds` | `false` |
| `sky.sunriseColors` | `false`，小行星带没有日落 |
| `sky.sun`、`sky.bodies`、`sky.stars` | 绘制 Galacticraft 自己的小行星天空：一颗小小的白色太阳，没有月亮，星空密集 |
| `time.dayLength` | 没有白天：太阳静止在地平线上，永远是白天 |

设置 `sun`、`bodies` 或 `stars` 中的任何一个，就会改为绘制资源包的天空，而 `sky.renderSky` 关闭时仍然什么都不绘制。设置 `time.dayLength`（即使设为 `24000`）或 `sky.fixedTime`，会让小行星带拥有自己的昼夜。

### 空间站

*Galacticraft 天体*

`<namespace>/dimensions/*.json`

`galacticraft` 块带有 `kind: "station"` 的维度文件，允许玩家通过 Galacticraft 自己的地图按钮和它自己的轨道维度，在资源包行星或卫星的轨道上建造空间站。该文件是空间站的种类，而不是某一个空间站：玩家建造的每个空间站都是一个独立的维度，由 Galacticraft 创建并保管。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `kind` | 是 | `station` | | 使该文件成为空间站 |
| `parent` | 是 | 名称 | | 它所围绕的行星或卫星：必须是资源包维度创建的可到达天体。卫星的空间站在地图上位于该卫星旁边，围绕卫星所属的行星 |
| `tier` | 否 | 整数 | 父天体的等级 | 能到达该空间站的火箭等级 |
| `showName` | 否 | 布尔值 | `false` | 开启时，地图在 Galacticraft 写 `Station: <owner>` 的地方，把每个空间站列在该文件的 `name` 之下；所有者那一行保留。被所有者重命名的空间站保留所有者的名称 |
| `recipe` | 否 | 材料到数量的对象 | Galacticraft 自己的空间站配方 | 建造一个需要什么。材料是矿物词典名称、`modid:item` 或 `modid:item:meta` |
| `checklist` | 否 | 字符串列表 | 无 | 发射前显示的 Galacticraft 检查清单键 |

其他[地图键](#地图键)用于把空间站放到地图上。`galacticraft` 块中的其他内容都不适用：空间站使用 Galacticraft 自己的空间站空气、重力和抵达方式。

在块之外，空间站只读取这些维度键，别无其他：

| 键 | 未设置时 |
| --- | --- |
| `id` | 必填。空间站占用这个 id 和下一个 id |
| `sky.skyColor`、`sky.fogColor` | Galacticraft 的轨道颜色 |
| `sky.starBrightness` | Galacticraft 的轨道星星 |
| `sky.sun`、`sky.bodies`、`sky.stars` | Galacticraft 的轨道天空，下方是它的父天体 |
| `sky.renderSky`、`sky.renderClouds`、`sky.renderWeather` | `true` |
| `time.dayLength` | `24000` |

**Id。** `id` 和 `id + 1` 是维度类型 id，而不是空间站的维度；两者都必须空闲。每个建成的空间站在建造的那一刻获得下一个空闲的维度 id，Galacticraft 会把该 id、所有者和空间站的名称保存在世界存档中，所以重启后空间站仍在同一个 id 上。它的存档文件夹由 Galacticraft 决定，为 `DIM_SPACESTATION<id>`。

**每个天体一种空间站。** 已经有空间站的行星或卫星，无论来自另一个资源包还是另一个模组，都保留原来的空间站：第二个不会被注册，日志中会有说明。

### GalaxySpace 与 ExtraPlanets

*Galacticraft 天体*

`<namespace>/celestial/*.json` 以及 `<namespace>/dimensions/*.json` 的 `galacticraft` 块

两个 Galacticraft 附属模组会读取比 Galacticraft 更多的天体信息。`galaxyspace` 对象可以写在 `celestial/` 的行星或卫星、星系的 `star` 或维度的 `galacticraft` 块中，安装了 GalaxySpace 时生效。`extraplanets` 对象只能写在维度的 `galacticraft` 块中，安装了 ExtraPlanets 时生效。没有对应的附属模组时，其对象不起作用，日志会为该天体说明一次，而天体仍作为普通的 Galacticraft 天体创建。行星可以围绕 GalaxySpace 的星系运行，例如 `tauceti`、`barnards`、`acentauri` 或 `proxima`，无论有没有这些对象。

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

维度的 `gravity` 和 `dayLength` 同时也是 GalaxySpace 为该天体显示和使用的值；它们不需要专门的键。

| `galaxyspace` 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `pressure` | 否 | 浮点数 | `0` | 气压。高于 `10` 会导致反胃，高于 `25` 会导致缓慢，高于 `35` 会导致失明，高于 `45` 会造成伤害，除非 GalaxySpace 的护甲或其配置阻止了这些效果 |
| `radiation` | 否 | 布尔值 | `false` | 太阳辐射，在开阔天空下日光中的玩家身上累积，除非其护甲能屏蔽 |
| `class` | 否 | `selena`、`desert`、`terra`、`oceanide`、`gasgiant`、`icegiant`、`asteroid`、`titan`、`iceworld` | 无 | GalaxySpace 地图和指南书中显示的行星类别 |
| `orbitEccentricity` | 否 | `[x, y]` | `[0, 0]` | 沿每个轴拉伸 GalaxySpace 地图绘制的轨道。`0` 或更小则使该轴保持圆形 |
| `orbitOffset` | 否 | `[x, y]` | `[0, 0]` | 移动该轨道的中心 |
| `freezeBlocks` | 否 | 布尔值 | `true` | 放置在密封空气之外的 GalaxySpace 液态甲烷和氦氢，在强热下是否会变成火，或在强寒下是否会消失；水从不改变 |
| `thermalVariation` | 否 | 浮点数 | `0` | 热等级在正午与午夜之间摆动多大，按 `atmosphere.temperature` 的比例计算，当它为 `0` 时则直接摆动这个数值。需要 GalaxySpace 的高级热系统 |
| `solarWind` | 否 | 浮点数 | 恒星大小的平方 | GalaxySpace 太阳风板在这里的输出 |
| `weather` | 否 | `dust_storm`、`frozen_storm`、`lightning_storm`、`meteoric_rain` | 无 | 时来时去的风暴。沙尘暴会伤害站在开阔天空下的任何人，流星雨会落下流星，雷暴会落雷 |
| `weatherFrequency` | 否 | 浮点数 | `1` | 雷暴落雷的频率 |
| `starType` | 否 | `subdwarf`、`dwarf`、`subgiant`、`giant`、`supergiant`、`hypergiant`、`blackhole` | 无 | 恒星的类型，显示在 GalaxySpace 地图上 |
| `starColor` | 否 | `brown`、`red`、`orange`、`yellow`、`white`、`lightblue`、`blue`，或 `M1` 到 `O3` 的类别 | 无 | 恒星的颜色类别，显示在 GalaxySpace 地图上 |
| `habitableZone` | 否 | `[distance, width]` | `[0, 0]` | GalaxySpace 地图标记为宜居的恒星周围区带 |

`starType`、`starColor` 和 `habitableZone` 只在星系的 `star` 中读取；其余的只在行星或卫星中读取。GalaxySpace 按行星绘制它的风暴，所以资源包的风暴会起作用，但不会显示它自己的天空。

| `extraplanets` 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `pressure` | 否 | 整数，`0` 到 `100` | 无 | ExtraPlanets 的气压。大于 `0` 时，会伤害没有 ExtraPlanets 太空服的玩家，并显示在其 HUD 上 |
| `radiation` | 否 | 整数，`0` 到 `100` | ExtraPlanets 对其他附属模组的默认值 | ExtraPlanets 的辐射，在其太空服等级无法防护的玩家身上累积 |
| `temperatureDay` | 否 | 浮点数 | `atmosphere.temperature` | 白天的热等级，采用 ExtraPlanets 的刻度，大约从 `-140` 到 `100`。需要 ExtraPlanets 的 3 级和 4 级保温衬垫选项 |
| `temperatureNight` | 否 | 浮点数 | `temperatureDay` | 夜间的热等级 |
| `lander` | 否 | `general`、`jupiter`、`saturn`、`mercury`、`neptune`、`uranus` | Galacticraft 的着陆器 | 在 `landing` 为 `lander` 时，玩家乘坐降落的 ExtraPlanets 着陆器 |

## 传送门与门

*世界*

`<namespace>/blocks/*.json`

传送门就是一个普通的方块定义，因此适用同样的路径规则：文件路径即方块的注册名。

`portal` 方块带有一个 `portal` 部分：

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `dimension` | 是 | int | | 传送到的维度 |
| `returnDimension` | 否 | int | `0` | 返回时传送到的维度 |
| `gate` | 否 | 门名称 | 无 | 必须处于开启状态才能通过的门 |
| `cooldown` | 否 | int，刻 | `60` | 同一玩家再次使用前的冷却时间 |
| `platform` | 否 | boolean | `true` | 到达时是否建造着陆平台 |
| `platformBlock` | 否 | 方块名称 | 传送门自身的框架 | 该平台的组成方块 |
| `sound` | 否 | 音效名称 | 无 | 通过时播放 |
| `owned` | 否 | boolean | `true` | 只有建造者及其允许的人可以使用。有主的传送门同时免疫爆炸 |
| `walkIn` | 否 | boolean | `false` | 走进方块即可传送，就像下界传送门那样。关闭时需要手动使用 |

### 传送门框架

*传送门与门*

`<namespace>/portalframes/*.json`

文件路径即框架的注册名，维度随后在 `frames` 中引用它。

框架只是玩家需要搭建的图样，仅此而已：它说明哪些方块构成边缘、洞口在哪里，却不说明传送门通向何处。这是有意为之，因为维度是认领框架而不是拥有框架，两个维度可以认领同一个框架。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `name` | 否 | string | 文件名 | 日志中使用的名称 |
| `axis` | 否 | `vertical`、`horizontal` 或 `both` | `vertical` | 是像下界传送门那样竖立、像末地传送门那样平放，还是两者皆可 |
| `legend` | 是 | 单个字符到方块的对象 | 无 | 各行可以使用的方块。带状态的方块名称与其他地方的读取方式相同 |
| `rows` | 是 | 字符串列表 | 无 | 图样，从最上面一行开始绘制 |
| `maxWidth` | 否 | int | `21` | `*` 最宽可拉伸出的洞口宽度 |
| `maxHeight` | 否 | int | `21` | `*` 最高可拉伸出的洞口高度 |

有三个字符不是方块。`.` 是传送门所在的洞口，没有洞口的框架会被拒绝。空格表示框架不关心的格子，因此要画 L 形的边框，把角落留空即可。`*` 表示重复：只有 `*` 的一行会按玩家实际搭建的数量重复其上一行，行内的 `*` 则以同样方式重复它前面的字符。它也可以重复零次，所以把所有 `*` 划掉后读到的图样就是能够点亮的最小形态，而下文的最大值则是最大形态。不含 `*` 的图样是精确的，玩家必须原样搭建，不能多也不能少。

竖立的框架可以在任一水平轴上、以任一朝向被找到，所以建造者面朝哪个方向都无所谓。平放的框架则可在四个旋转方向上被找到。

**框架能有多大由资源包决定。** `maxWidth` 和 `maxHeight` 是 `*` 能拉伸到的最大洞口，小于它、直到下限之间的任何尺寸都会被接受，因此资源包可以决定它的门最大是原版的 21，还是只有 4。下限由玩家决定：竖立的框架要求洞口至少宽 1、高 2，平放的至少 1 乘 1，永远达不到这个尺寸的图样会在加载时被拒绝并在日志中留下一行，而不会变成一个没人能走过去的框架。

**框架能拉伸得越多，查找的代价就越高。** 行 `*` 与列 `*` 同时存在，意味着要尝试两个最大值之内的所有组合，所以两个方向都能拉伸到 21 的框架就是 441 种图样。搜索会放弃而不是卡死，并在日志中说明，这就是该降低某个最大值或去掉一处拉伸的信号。

**没有什么能阻止框架使用由打火石点燃的黑曜石，但它优先生效。** 框架的查找发生在物品执行自身作用之前，所以这样的框架会在本应出现下界传送门的位置开启资源包的维度。若想保留原版传送门，请换一种方块或另一种点火器。

### 用框架开启维度

*传送门与门*

`<namespace>/dimensions/*.json`

维度通过携带 `portal` 部分来经由框架开启。框架与点燃它的物品共同决定维度，所以同一种框架形状会因点燃方式不同而通向不同的地方。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `frames` | 是 | 框架名称列表 | 无 | 开启该维度的框架 |
| `ignitedBy` | 否 | 物品名称 | `minecraft:flint_and_steel` | 玩家手持何物来点燃框架 |
| `color` | 否 | 十六进制颜色 | 白色 | 传送门绘制所用的颜色 |
| `return` | 否 | `built`、`player` 或 `none` | `built` | 是否提供返程：自动建造、由玩家建造，或完全不提供 |
| `gate` | 否 | 门名称 | 无 | 必须处于开启状态才能通过的门 |
| `cooldown` | 否 | int，刻 | `60` | 同一玩家再次通过前的冷却时间 |
| `platform` | 否 | boolean | `true` | 到达时是否建造着陆平台 |
| `platformBlock` | 否 | 方块名称 | 石头 | 该平台的组成方块 |
| `sound` | 否 | 音效名称 | 无 | 通过时播放 |
| `owned` | 否 | boolean | `false` | 只有点燃者及其允许的人可以使用 |

洞口中的方块不需要资源包来编写。带有 `portal` 部分的维度会获得一个专属方块，名为 `<namespace>:portal_<dimension>`，使用游戏自带的传送门纹理并按 `color` 着色，走进去即可传送而无需手动使用，且不可破坏。颜色会与纹理相乘，原理与 `tintindex` 相同，所以 `#C77DFF` 保留了下界的紫色，而 `#4CFFB0` 会让它变成毒绿色。若想要完全不是原版纹理的传送门，请自行编写一个普通的 `portal` 方块，配上自己的模型，以及用[像素图](#以像素图编写的纹理)绘制的纹理，其中 `tint` 可以在两种颜色之间渐变。

`return` 决定另一侧会发生什么。`built` 会按玩家搭建的尺寸立起同样的框架并将其点亮，这与原版的行为一致。`player` 不会建造任何东西，但允许在那边点亮同样的框架，因此回家的路需要自己去找并自己搭。`none` 则完全拒绝在该维度中点亮该框架，这趟旅程只能单程。

**一个框架，多个维度。** 框架与点燃它的物品这一组合决定了维度，所以同一个 `standing_gate`，用打火石点燃与用资源包自己的点火器点燃，会开启两个不同的地方，各有各的颜色。两个维度认领同一个框架*且*同一个物品是资源包的错误：第二个会被拒绝并在日志中说明，而不是悄悄让其中一个胜出。

破坏框架的任何一个方块都会使传送门熄灭，与原版一样。

### 传送门

*传送门与门*

`<namespace>/gates/*.json`

文件路径即门的注册名，传送门随后在 `gate` 中引用它。

此处一次性展示所有键。实际文件只写需要的键。

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

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `dimension` | 是 | int | | 它守卫的维度 |
| `name` | 否 | string | 文件名 | 显示给玩家 |
| `scope` | 否 | `player`、`global` | `player` | 一次针对一名玩家，还是针对整个世界 |
| `open` | 否 | boolean | `false` | 是否一开始就处于开启状态 |
| `unlock` | 否 | object | | 开启它的条件。见下文 |
| `unlockedMessage` | 否 | string | `%dim% is now open` | 开启时显示 |
| `blockedMessage` | 否 | string | `You need %item% to enter %dim%` | 被拒绝时显示 |
| `safeReturn` | 否 | boolean | `false` | 被封锁的返程仍会降落在安全的地方，而不是被拒绝 |
| `requires` | 否 | 模组 id 或资源包命名空间的列表 | 无 | 除非全部存在，否则跳过该门 |
| `portalBlocks` | 否 | 方块名称列表 | 所有传送门 | 将门限制在这些传送门方块上，因此同一个维度可以既有受守卫的门，也有敞开的门 |

`unlock` 接受 `hold`（必须手持的物品）、`consume` 及其 `consumeCount`（`1`）、`craft`（必须合成过的物品）、`advancement`，以及 `killed`（一个实体名称，谁击杀一只，门就为谁开启，因此一个首领可以掌管通往一个世界的钥匙）及其 `killedCount`（`1`，一只不够时使用），按作用域所说的，按玩家或按整个世界统计。再加上 `killedDrops`（一个物品名称），被计入的击杀就会改为在击杀者脚边放下该物品而不是开启门，并重新开始计数，这样钥匙可以反复获得，再交给从未参与战斗的人；对同一物品使用 `hold` 或 `consume` 来设门，即可让它成为钥匙。`%item%`、`%mob%` 和 `%dim%` 会自动填入。怪物掉落的钥匙在这里无需任何特殊处理：给怪物设置掉落物，再用 `hold` 或 `consume` 设门即可。

## Rubic 世界

*世界*

`terrain` 设置中的 `rubicWorld` 会把维度的世界重建为 16×16×16 的立方体，而不是 256 格高的区块柱，因此它的地板和天花板可以位于资源包指定的任何位置。地形生成本身没有改变：原版的生成器和其他模组的世界生成照常运行，产生相同的陆地，只是在它的上方和下方多了世界。

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

所有键都位于 `terrain` 分组中，与其他键一样写在世界模板的 `settings` 块里：

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `rubicWorld` | boolean | `false` | 启用 Rubic 世界 |
| `worldMinHeight` | int，16 的倍数 | `-64` | 世界地板 |
| `worldMaxHeight` | int，16 的倍数 | `320` | 世界天花板 |
| `rubicWorldDimensions` | int 列表 | 空 | 哪些维度变为 Rubic。为空表示每个维度 |
| `rubicWorldDimensionsAreBlacklist` | boolean | `false` | 将该列表视为需要排除的维度 |
| `terrainOffset` | int，非负的 16 的倍数 | `0` | 将整个原版地形窗口向上平移。适用于普通的分层预设：设为 `272` 的平坦世界，其地表约在 y 275，高于原版的天花板。预设要求的装饰和结构仍然在其未平移的高度生成 |

**高度。** `worldMinHeight` 必须低于 `worldMaxHeight`，两者都必须是 16 的倍数，并且都要在配置中 `rubicHeightLimit` 允许的范围内（默认上下各 `4096` 格；仅限配置，绝不是资源包的键）。否则会被拒绝并在日志中留下一行，世界将以 `-64` 到 `320` 创建。高度是有代价的：每 16 格就是每根区块柱中多出的一个立方体，所以内存、磁盘和预生成时间都随之增长，`rubicHeightLimit` 的配置注释中给出了具体数字。

**地形窗口。** 维度自身的生成器保持自己的高度，主世界为 256 格，而 `terrainOffset` 平移的正是这个窗口。`worldMinHeight` 和 `worldMaxHeight` 只会在窗口的周围增加空间，绝不会在窗口内部。抬高天花板并不会抬高陆地，只是增加天空；降低地板也不会加深生成器挖出的洞穴，只是增加深层世界。海平面同样位于窗口之内，所以它会随 `terrainOffset` 一起移动，并且由世界类型决定，而不是由任何 Rubic 键决定。要让地表在世界中更高，请提高 `terrainOffset`。要在其上方或下方留出更多空间，请调整高度。窗口之外的每个立方体仍然会在每根区块柱中生成并计算光照，所以更高的天花板无论上面有没有东西填充，都要花费预生成时间，而一旦 `skyStone` 填充了它，还要额外占用内存和磁盘。

**平移后的窗口对其他模组的影响。** 填充（population）在区块柱上运行，所以其他模组注册的每个生成器仍然在每个区块中运行一次，坐标不做任何换算。改变的是它自己的计算落在哪里。通过最高固体方块或降水高度向世界询问地面位置的生成器会跟随平移后的地形：这两者都能识别 Rubic，涵盖了树木、花朵和大多数装饰。计算绝对高度的生成器，包括常见的 y 低于 64 的随机矿石模式，仍会在那个高度写入，而平移之后那里是远在陆地之下的填充物或深层世界。海平面同样不会被平移，所以拿它做判断的生成器读到的是未平移的数值。这些写入还会落在填充所保持加载的立方体之外，并在区块柱填充期间引入它们自己的立方体。较大的 `terrainOffset` 适合自己描述生成过程的资源包，而不适合叠加在另一个资源包的世界生成之上。若只是想要空间，向下更划算：窗口下方是一个完整的生成器，有它自己的石头、洞穴、矿脉、含水层和地牢，并且地表仍保持在其他所有生成器所假定的高度；而窗口上方的空间是需要资源包自己去布置的景观。

**每个存档决定一次。** 维度是否为 Rubic 以及它的高度是多少，会在它第一次加载时写入存档，此后固定不变：即使移除了资源包，Rubic 世界仍然是 Rubic，并且它的高度之后无法更改。主世界以外的维度采用主世界的高度。已有的 Anvil 陆地不会被转换：Rubic 把陆地保存在它自己的 `region2d`/`region3d` 文件中，所以已经以 Anvil 形式生成过的维度会重新开始它的地形。请只为新世界启用它。

**排除维度。** 未列入 `rubicWorldDimensions` 的维度在同一个存档中保留它普通的 Anvil 世界，Rubic 与 Anvil 维度可以自由混用。对于那些其生成器写入区块内部结构而不是走普通填充流程的维度，这是正确的做法。与该列表无关，如果某个世界的服务端类被另一个模组替换了，它会被跳过，并在日志中留下一行说明。

**窗口之外的空间。** 生成器自身的范围保持其通常的形态，Rubic 世界在其周围增加的空间，会用该范围终止处的方块填充：主世界下方是石头，上方是空气。顶部以基岩封住的维度，尤其是下界，被视为封闭的，所以它上方的空间保持为空，而不是塞满屋顶之下的下界岩。屋顶本身不受影响。`deepStone` 指定窗口下方空间的方块，`skyStone` 指定窗口上方空间的方块。

**CubicChunks。** 不支持同时运行两者。安装了 CubicChunks 而资源包要求 `rubicWorld` 时，加载会停止并给出提示：请移除 CubicChunks，或者从资源包中去掉 `rubicWorld`，让 CubicChunks 来创建世界。

### 将世界迁移到 CubicChunks 及迁回

*Rubic 世界*

**文件名。** Rubic 世界把它的区块柱保存在 `region2d/<x>.<z>.2rdr` 中，把它的立方体保存在 `region3d/<x>.<y>.<z>.3rdr` 中。对于其区域文件放不下的条目，会放入它旁边的一个文件夹，该文件夹以文件名加上 `.ext` 命名。在这些名称出现之前创建的世界使用 `.2dr` 和 `.3dr`：模组会在维度加载时自行重命名这些文件和文件夹，每个维度在日志中留下一行，所以旧世界无需手动处理。

**打开 CubicChunks 世界。** 当资源包要求 `rubicWorld`，而正在打开的世界被标记为 CubicChunks 世界时，模组会在做任何事之前先询问，方式与 Forge 询问缺失的注册表条目相同：单人游戏中是一个确认界面，专用服务器上则是一条控制台消息，用 `/fml confirm` 或 `/fml cancel` 回答，或者提前用 `-Dfml.queryResult=confirm` 回答。选择确认后，Forge 的世界备份会以 zip 形式写入 saves 文件夹，世界就地转换为 Rubic 世界，然后继续加载。选择拒绝后，加载停止，世界不会有任何改动。

**转换器。** 同样的转换及其逆向转换，也可以在游戏之外运行。仓库附带了 [`scripts/convert_rubic_world.py`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/scripts)，它可以把 Rubic 世界转换为 CubicChunks 世界，或把 CubicChunks 世界转换为 Rubic 世界。它只需要 Python 3，别无其他。请先关闭游戏并备份世界，然后先看一遍试运行的结果，再正式运行。

```
python3 scripts/convert_rubic_world.py to-cubic "saves/My World" --dry-run
python3 scripts/convert_rubic_world.py to-cubic "saves/My World"
```

| 参数 | 作用 |
| --- | --- |
| `to-cubic` | Rubic 世界变为 CubicChunks 世界 |
| `to-rubic` | CubicChunks 世界变为 Rubic 世界 |
| `<world folder>` | 存档文件夹，即包含 `level.dat` 的那个 |
| `--dry-run` | 打印每一处改动，但不做任何改动 |

**它会改动什么。** 在每个维度中，区域文件及其 `.ext` 文件夹采用另一方的名称（Rubic 为 `.2rdr` 和 `.3rdr`，CubicChunks 为 `.2dr` 和 `.3dr`），`data/rdplRubicData.dat` 变为 `data/cubicChunksData.dat`，或者反过来，高度保持不变，存储格式和兼容生成器按另一个模组的命名方式命名。最后，`level.dat` 和 `level.dat_old` 中的标记在 `isRubicWorld` 与 `isCubicWorld` 之间互换。立方体和区块柱本身不会被重写。

**它会拒绝什么。** `level.dat` 标记与所要求方向不符的世界、另一个模组所没有的存储格式或兼容生成器，以及目标已存在的重命名。拒绝不会改变任何东西，被中断的运行可以重复执行。

**不会延续的内容。** 转换后的世界只保留两个模组都能理解的内容。资源包定义的方块和维度在纯 CubicChunks 下并不存在，并且每个模组都会为对方保存的立方体重新计算光照。

### 立方体流式加载

*Rubic 世界*

**立方体流式加载。** 四个 `chunks` 键决定立方体如何送达玩家，以及何时再次释放。它们只在 Rubic 世界中起作用，默认值就是该子系统调校时所用的数值，所以不去动它们的资源包不会付出任何代价。

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

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `verticalCubeLoadDistance` | int，立方体 | `8` | 区块加载票据在玩家上方和下方各保持多少个立方体。同名的视频设置滑块是客户端自己的视距，由游玩的人而不是资源包设定 |
| `cubesSentPerTick` | int，立方体 | `649` | 一刻内最多可向玩家发送多少个立方体。调高它会更快填满视野范围，也会让每刻的数据包更大；数据包仍会在 1024 个立方体或 512 KB 处拆分，以先到者为准 |
| `cubeGenMillisPerRound` | int，毫秒 | `50` | 一刻最多可以花多长时间生成玩家正在等待的立方体 |
| `cubeGCInterval` | int，刻 | `200` | 无人注视的立方体多久被释放一次 |

**客户端。** 视频设置中新增了一个垂直渲染距离滑块，是渲染距离在垂直方向上的对应项（即配置中的 `verticalCubeLoadDistance`，由游玩的人决定）。`terrain` 分组中的其余所有内容，包括预生成、世界物理、出生点和边界，对 Rubic 世界依然照常适用。

## 深层世界

*世界*

另外九个 `terrain` 键会用现代风格的生成来填充 Rubic 世界在原版地形窗口周围开辟出的空间。它们只在 Rubic 世界中起作用：

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

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `deepStone` | `namespace:block`，元数据写作 `@meta` | 无 | 窗口下方世界所用的方块，例如资源包自己的深板岩。它在窗口最低的八层中与窗口的石头渐变过渡，就像现代版本混合深板岩那样 |
| `skyStone` | `namespace:block`，元数据写作 `@meta` | 无 | 窗口上方世界在其表面之下所用的方块，由雕刻下方深层世界的同一种噪声塑造成漂浮的陆地，所以那里是洞穴的地方，这里就是岛屿。留空则窗口上方的空间保持为空，一如既往。陆地带有该区块柱自己的地表，即取自生物群系的顶层方块及其下的三层，所以主世界的岛屿读起来是草方块覆盖泥土再覆盖这种方块。生物群系或洞穴区域可以指定自己的 `skyStone`、`skyIslands` 和 `skyThickness`，这样一个高度带或一个区域就带有自己的岛屿，按区块柱解析，区域优先于高度带，高度带优先于生物群系。这些岛屿本身也会被装饰：窗口上方的每个立方体都会针对该立方体内的表面运行生物群系自己的特征，所以树木、草、花、蘑菇、芦苇和各类斑块会落在岛上，而不是像原版那样沿区块柱向下散布；而那里的生物群系高度带会按自己的数量进行装饰。生物群系自己的附加内容也会在那里运行，而不只是共用的那些：沙漠水井、丛林西瓜、黑森林的茂密树冠和蘑菇、针叶林巨石、冰刺平原生物群系的冰刺，以及各生物群系放置的高大花草。岛屿绝不会由会掉落的方块构成：生物群系的表面本应是沙子或沙砾的地方，岛屿使用砂岩，其他情况则使用它自己的 `skyStone`，因为没有什么能在半空中托住一个会掉落的方块。兽群以同样的方式按立方体放置，所以动物在陆地生成的同时就出现在岛上。表层深度随噪声在一到四格填充方块之间变化，所以岛屿的边缘不是均匀的一层壳，并且这层壳是沿坡面测量而不是笔直向下，所以陡峭的坡面会保留它的土壤，而不会薄到消失。表面遵循天空自身报告的任何生物群系，依次是洞穴区域、按高度分带的生物群系，然后是下方的区块柱，所以在天空区域中指定 `minecraft:mesa`，就能在任何高度得到由带状黏土构成的岛屿，与地面上的条带相同；指定沙漠则得到它的沙子，并因为没有什么能在半空中托住会掉落的方块而变为砂岩。动物会在其上定居，这可以用 `spawning` 分组中的 `skyAnimals` 关闭。天空中有多少会变成陆地由 `skyIslands` 决定，其默认值 0.5 是一片群岛：在生成的世界上，窗口上方大约八分之七的立方体是空的，最满的一层接近三分之一，所以天空是用来飞越的，而不是用来行走的。把它降到 0.2 附近，这个高度带就会闭合成起伏的天花板，上面立着丘陵，中间约五分之四是实心的，这可以用来建造，但已经不再是岛屿。岛屿在 `worldMaxHeight` 之下八格处停止，所以顶部不会被平切在天花板上，树木和植物上方也有空间；`caves` 则和以前一样填满到天花板。每个 Rubic 维度都有自己的窗口，所以这会填充每个窗口的上方：在窗口高 128 的下界，那就是基岩屋顶之上的空间，而打开天花板的接缝会清除屋顶本身 |
| `skyShape` | `islands` 或 `caves` | `islands` | 窗口上方的世界被塑造成什么形态。`islands` 是漂浮的陆地。`caves` 是被洞穴贯穿的实心岩石，即把深层世界自己的处理方式翻转向上，实心比例约为 86%，与深层世界的岩石与洞穴之比相同。两种情况下都不会有任何东西被淹没，因为窗口之上不会查询含水层。仅当 `skyStone` 指定了方块时才会读取 |
| `skyIslands` | 数字，`-1` 到 `1` | `0.5` | 天空聚集成岛屿的难易程度。越低，岛屿铺开在越多的天空中，其下的阴影也越深；越高，则岛屿越少越小。默认值使得大约八分之七的立方体为空，峰值接近三分之一；在 `0.2` 附近，这个带会闭合成带丘陵的天花板，中间约五分之四是实心的。仅当 `skyStone` 指定了方块且 `skyShape` 为 `islands` 时才会读取 |
| `skyThickness` | 数字，`0` 或更大 | `2.0` | 岛屿有多厚实。越高，岛屿越饱满；越低，岛屿越空心，边缘也薄到消失。仅当 `skyStone` 指定了方块且 `skyShape` 为 `islands` 时才会读取 |
| `skyHeights` | 两个 int，先最低后最高 | 无 | 岛屿所能达到的最低和最高方块，从窗口底部起算，与 `oreVeins` 的高度计算方式相同。留空则填满窗口上方的整个世界，在很高的世界中，这是一大片天空。仅当 `skyStone` 指定了方块时才会读取 |
| `noiseCaves` | `off`、`deep`、`world` | `off` | 现代风格的噪声洞穴：奶酪洞穴、意面隧道、靠近地表的洞口，以及大型洞室中的石柱。`deep` 只在窗口下方雕刻，`world` 则雕刻整个世界 |
| `deepRavines` | boolean | `false` | 在窗口下方的世界中切出原版风格的峡谷，即又长又陡的深谷。峡谷穿过深层世界自己的流体时会采用它们：在熔岩线以下充满熔岩，在其上方则保留含水层的水或其压力墙，所以它绝不会把切开的地方排干。现代版本只在窗口内部雕刻峡谷，所以除非开启此项，深层世界没有峡谷 |
| `oreVeins` | `ore,extra,filler,lowest,highest` 的列表 | 无 | 大型带状矿脉，主要是 `filler` 方块，其中散布着 `ore`，并有很小的几率出现 `extra`，后者可以留空。高度从窗口底部起算，所以负数会到达深层世界 |

那里的水和熔岩有它们的行为。大量熔岩填满最低的几层，其上的洞穴带有局部含水层，采用现代版本使用的同一套采样点与压力机制，移植自 26.1.2，所以一潭潭静水位于各自的水位，凡两个水位相遇或水与熔岩相遇之处，都有由噪声塑造的深层石头墙。在海洋之下，洞穴会向海平面方向淹没，就像现代版本把含水层与地表联系起来那样。

**按维度设置。** `deepStone`、`noiseCaves`、`skyStone`、`skyShape`、`deepRavines` 和 `oreVeins` 各自既可以接受单个值也可以接受列表，写作 `dimension=value` 的列表条目只对该维度生效。只要有任何条目指明了某个维度，这些条目就完全决定该维度的取值，未指明维度的条目在那里被忽略，所以只写 `"1="` 而后面什么都没有，就是为该维度关闭这个键。没有写维度的值会作用于除末地以外的每个 Rubic 维度，末地保持虚空，除非资源包点名指定：填满末地就等于终结末地，而且被填满的末地还会使原版的传送门寻找失效，因为它会从 1024 格处向回走，只要还不断遇到含方块的区块就会一直走下去。点名指定它，你就会得到你所要求的。

开启 `noiseCaves` 后，深层世界还会生成现代风格的怪物房间，窗口下方每根区块柱约尝试四次，距离世界地板不到六格的地方不会生成，所以地牢刷怪笼及其箱子战利品会像现代版本那样出现在深层洞穴中。

`world` 范围还会废除两项会与重做后的洞穴冲突的原版遗留行为。原版在 y 10 以下的洞穴中倾倒的熔岩改由含水层判定，所以旧的熔岩窗口消失了；而原版的地下水湖，包括地表池塘，也不再生成，就像现代版本取消了它们一样；含水层自己的水池取而代之。

`deep` 范围保持原版的带不变，包括熔岩窗口，只封住两者相接的接缝。窗口最低一层上的熔岩或水，如果正下方有一个敞开的深层洞穴，就会变成深层石头，这样窗口就不会向下方的洞穴漏光。

## 洞穴区域

*世界*

`<namespace>/caveregions/*.json`

文件路径即区域的名称，世界生成条目随后在 `caveRegions` 中引用它。其中的裸名称取该条目自己的命名空间。

在地下绘制具名区域，是现代洞穴生物群系在资源包中的对应物。地下被划分为圆角的单元格，宽 `caveRegionCells` 格、高 `caveRegionCellsY` 格，两者都是 `terrain` 键，每个单元格按权重掷出一个区域，或者不掷出区域。区域所做的一切都确定性地来自种子，所以区块之间彼此一致，而无需跨边界写入。

### 区域文件

*洞穴区域*

此处一次性展示所有键。实际文件只写需要的键。

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

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `weight` | int | `1` | 该区域赢得的单元格份额。`0` 表示关闭它 |
| `minHeight` | int | 世界地板 | 区域所存在的高度带的底部 |
| `maxHeight` | int | `48` | 该高度带的顶部。中心位于高度带之外的单元格绝不会选中该区域 |
| `dimensions` | int 列表 | 全部 | 区域出现在哪些维度中 |
| `floorCover` | 方块 | 无 | 替换区域内洞穴地面的顶层方块 |
| `floorChance` | 0.0 到 1.0 | `1.0` | 地面有多大比例被覆盖 |
| `ceilingCover` | 方块 | 无 | 替换区域内的洞穴天花板方块 |
| `ceilingChance` | 0.0 到 1.0 | `1.0` | 天花板有多大比例被覆盖 |
| `coverReplace` | 方块列表 | 任何类石头的方块 | 覆盖物可以替换什么 |
| `waterLevel` | int | 无 | 固定区域内每个含水层采样点的水位，使其洞穴淹没到这个高度。区域与干燥洞穴相接处的墙，由与现代含水层相同的压力噪声塑造，并且水绝不会碰到熔岩地面。需要开启 `noiseCaves` |
| `spawns` | 列表 | 无 | 在区域内生成的生物，条目与生物群系的 `spawns` 所接受的相同：`entity`、`type`（monster、creature、ambient 或 water）、`weight`，以及表示群体大小的 `min` 和 `max`。在地形窗口之下，能看见天空的位置交给生物群系处理，覆盖物也是如此；在窗口之上，唯一的陆地就是天空生成的陆地，该列表也适用于露天处 |
| `keepDefaultSpawns` | boolean | `false` | 在区域自己的列表之外，同时保留生物群系自己的生成列表。关闭时，区域的列表在区域内完全取代它 |
| `structures` | 列表 | 无 | 每个区域单元格放置一次的结构，位于单元格的中心，并吸附到洞穴地面，就像现代版本给洞穴生物群系配上地标那样。条目是 `namespace:name` 模板，或用 `{ "structure": "...", "weight": 3 }` 在多个之间选择 |
| `structureChance` | 0.0 到 1.0 | `1.0` | 区域中每个单元格实际得到其结构的几率 |
| `structureLoot` | `namespace:path` | 无 | 已放置结构内的每个箱子在第一次被打开时所用的战利品表 |
| `biome` | 生物群系名称 | 无 | 区域在其体积内报告的生物群系，以 3D 生物群系的形式写入立方体。赋予区域自己的树叶、草和水的颜色、音乐和环境音效，并让原版的生成权重读取它。其上方的地表不受影响，因为只有区域所占据的单元格会被写入 |
| `skyStone` | 方块 | 世界设置 | 此区域内天空岛屿在其表面之下所用的方块，所以一个区域可以带有自己的岛屿 |
| `skyIslands` | `-1` 到 `1` | 世界设置 | 区域内的岛屿阈值。越低，聚集的陆地越多 |
| `skyThickness` | `0` 或更大 | 世界设置 | 区域岛屿有多厚实 |
| `ambientSound` | 音效名称 | 无 | 不时向站在区域内的玩家播放的音效，就像现代生物群系添加它们自己的洞穴音效那样。由服务器只发送给那名玩家 |
| `soundChance` | 0.0 到 1.0 | `0.0111` | `ambientSound` 每刻播放的几率 |
| `particle` | 粒子名称 | 无 | 在区域内的玩家周围显示的粒子，是游戏的粒子名称之一，例如 `dripWater`、`happyVillager` 或 `depthsuspend`。只有区域内的空气会显示它 |
| `particleChance` | 0.0 到 1.0 | `0.00625` | 现代生物群系的粒子密度：每刻在 16 格范围内尝试约 667 个位置，每个位置以此几率显示粒子 |

### 单元格

*洞穴区域*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `caveRegionCells` | int，格 | `128` | 区域单元格有多宽 |
| `caveRegionCellsY` | int，格 | `64` | 区域单元格有多高 |
| `caveRegionPlainWeight` | int | `4` | 每个单元格掷骰时，普通的、无区域的地下所占的权重。越高，没有任何区域的地下就越多：在只有一个权重为 1 的区域时，约五分之一的单元格会得到它 |

地下有多少保持普通，由 `terrain` 键 `caveRegionPlainWeight` 决定，默认 `4`：在只有一个权重为 1 的区域时，约五分之一的单元格会得到该区域。覆盖物在顶部有遮盖时才生效，所以延伸到地面以上的区域绝不会出现在地表；在地形窗口之上，它们也适用于露天处，因为那里的一切都是天空生成所造的陆地。覆盖物适用于每个洞穴，无论是哪个生成器雕刻的；`waterLevel` 是唯一需要噪声洞穴的键，因为洪水是在雕刻洞穴时放置的。

### 区域内的特征

*洞穴区域*

特征通过普通[世界生成条目](#世界生成条目)上的两个键与之关联。`caveRegions` 列出条目可以在哪些区域中生成，在放置的位置上检查，所以蘑菇、水晶或其他任何东西只会出现在它们的区域内。`snap` 会先把每次尝试在垂直方向上移到最近的洞穴表面：站立的东西用 `floor`，悬挂的东西用 `ceiling`。类似滴水石的区域不需要新的形状：

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

`replace` 中的 `minecraft:air` 很重要：已放置的形状所覆盖的内容会对照 `replace` 检查，其默认值是石头，所以任何建在开阔洞穴空间里的东西都需要列出空气。同样的条目改用 `"snap": "floor"` 且不带 `hanging`，就会长出与之对应的石笋。区域过滤适用于每一种放置的形状；`belt` 和 `field` 按它们自己的规则放置，会忽略它。

---

# 生成世界

## 世界生成条目

*生成世界*

`<namespace>/worldgen/*.json`

文件路径即条目的名称，`belt` 和 `field` 形状会以它为种子生成噪声，所以重命名文件会改变它所生成的内容。

描述会生成的东西。每个条目都是一个由**扩散**放置的**形状**，并按允许生成的位置进行过滤。

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

只有 `block` 是必需的；其他一切都可以省略并采用默认值。当一种方块不够用时，`blocks` 会取代 `block`，它在下文有自己的示例。

### 它放置什么

*世界生成条目*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `block` | 是 | 方块名称 | | 放置什么 |
| `meta` | 否 | int | `0` | 该方块的哪个变种 |
| `blocks` | 否 | 对象列表 | 无 | 加权列表，用来取代单个方块。见下文 |
| `size` | 否 | int 或范围 | `8` | 一次尝试放置多少方块，或带半径的形状有多大 |
| `attempts` | 否 | int 或范围 | `8` | 每个区块尝试多少次 |
| `sparse` | 否 | boolean | `false` | 把方块分散开而不是紧挨在一起 |
| `shape` | 否 | object | `{ "type": "cluster" }` | 它所采用的形态。见[形状](#形状) |
| `spread` | 否 | object | `{ "type": "even" }` | 它被放在哪里。见[扩散](#扩散) |
| `replace` | 否 | 方块名称或对象的列表 | `["minecraft:stone"]` | 它可以替换什么。见下文 |
| `adjacent` | 否 | 方块名称或对象的列表 | 无 | 仅当与该位置相接的 26 个方块中有其中之一时才放置。与 `replace` 的三种写法相同 |

### 可生成的位置

*世界生成条目*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `minHeight` | 否 | int | `0` | 它放置的最低 y |
| `maxHeight` | 否 | int | `64` | 它放置的最高 y |
| `snap` | 否 | `floor` 或 `ceiling` | 无 | 先把每次尝试在垂直方向上移到最近的洞穴地面或天花板 |
| `snapDepth` | 否 | int | `0` | `snap` 随后越过表面再移动多远，从地面向下、从天花板向上。`0` 停留在紧贴表面的开阔空间中，`1` 是表面方块本身，`2` 是它后面的那个。它可以覆盖什么仍然由 `replace` 决定，所以资源包就是这样把方块分带放置在地面之下而不是地面之上 |
| `dimensions` | 否 | int 列表 | 每个维度 | 它在哪些维度中运行 |
| `dimensionsAreBlacklist` | 否 | boolean | `false` | 把该列表变为需要避开的维度 |
| `biomes` | 否 | 生物群系名称列表 | 每个生物群系 | 它在哪些生物群系中运行 |
| `biomeTypes` | 否 | 字典类型列表 | 无 | 按类型指定生物群系，例如 `FOREST` 或 `NETHER` |
| `biomesAreBlacklist` | 否 | boolean | `false` | 把这些列表变为需要避开的 |
| `minTemperature` | 否 | float | `-100.0` | 它会生成的最冷生物群系 |
| `maxTemperature` | 否 | float | `100.0` | 它会生成的最热生物群系 |
| `minRainfall` | 否 | float | `-100.0` | 它会生成的最干燥生物群系 |
| `maxRainfall` | 否 | float | `100.0` | 它会生成的最湿润生物群系 |
| `minDistanceFromSpawn` | 否 | int，格 | `0` | 距离世界出生点多远才开始生成 |
| `caveRegions` | 否 | 区域名称列表 | 无 | 只在这些[洞穴区域](#洞穴区域)内生成 |

### 地表标志与跟随者

*世界生成条目*

| 键 | 必需 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `indicators` | 否 | `block=weight` 列表 | 无 | 在已生成的矿脉上方的地表散落的方块，让玩家能知道地下有什么；请按矿脉的内容来选择它们。`empty=weight` 表示该处留空 |
| `indicatorCount` | 否 | int 或范围 | `1` | 每条已生成的矿脉得到多少个地表位置 |
| `indicatorSpread` | 否 | int，格 | `0` | 标志物可以落在超出矿脉占地范围多远的地方 |
| `then` | 否 | `name=weight` 或对象的列表 | 无 | 在本条目生成之后紧接着从它身上长出来的世界生成条目，并附属于它：跟随者的原点设在这条矿脉边缘的外侧，方向由 `thenSpread` 和 `thenDepth` 给出，所以两者相接。条目是 `name=weight`，或带有 `name`、`weight` 以及自己的 `spread` 和 `depth`（int 或范围）的对象，这些会仅对该跟随者覆盖矿脉的设置，所以同一个列表可以让一个钻石尖端向下、一个分支向侧面。裸名称按本资源包的命名空间读取，`empty=weight` 不排入任何内容。跟随者保留自己的形状、方块、大小和 `replace`，但跳过它自己的尝试次数、几率、高度带和生物群系限制，并且自己也可以带 `then`，层数想多深就多深；已经在同一条链中生成过的条目会使链终止 |
| `thenCount` | 否 | int 或范围 | `1` | 每条已生成的矿脉从该列表中挑选多少个不同的跟随者，每个条目至多一次，所以等于列表长度的数量会让每一个都生长出来 |
| `thenSpread` | 否 | int，格 | 形状的半径 | 跟随者生长的方向可以向侧面偏多远，从负的该值到正的该值之间随机 |
| `thenDepth` | 否 | int 或范围 | `0` | 方向向下（负）或向上偏多远。`0` 且没有侧向偏移时，跟随者笔直向下悬挂 |
| `prospectAs` | 否 | string | 文件名 | 勘探物品在其读数中如何称呼这个条目，例如 `Hematite` |

### 回溯生成与要求

*世界生成条目*

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `retrogen`    | 否 | 布尔值 | `false` | 同时在已存在的区块中生成 |
| `retrogenKey` | 否 | 字符串 | 配置中的键 | 仅为该条目覆盖回溯生成键 |
| `requires`    | 否 | 模组 ID 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该条目 |

### 加权方块

*世界生成条目*

当单个条目不够用时，`blocks` 可替代 `block`。权重是相对值，因此 80 和 20 即四比一。

```json
{
  "blocks": [
    { "block": "minecraft:wool", "meta": 2, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "lime" } }
  ]
}
```

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `block`      | 是 | 方块名称 |    | 要放置的内容 |
| `meta`       | 否 | 整数 | `0` | 使用哪个变种 |
| `weight`     | 否 | 整数 | `1` | 该项相对于其他项被选中的频率 |
| `properties` | 否 | 属性到值的对象 | 无 | 按名称指定的方块状态属性，用于没有自身元数据的状态 |

即使使用了 `blocks`，文件顶层仍然必须有 `block` 和 `meta`，建议把第一个条目填在这里。

### 替换目标

*世界生成条目*

`replace` 是一个列表，每个条目可采用三种形式之一。

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

| 形式 | 示例 | 匹配内容 |
| --- | --- | --- |
| 名称 | `"minecraft:stone"` | 该方块的所有状态 |
| 名称加元数据 | `"minecraft:stone:3"` | 仅该元数据，这里是闪长岩 |
| 对象 | `{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }` | 仅该状态 |

对象形式也可以用 `meta` 代替 `properties`，效果与冒号形式相同。使用 `"minecraft:air"` 可在开阔空间中生成。

### 相邻方块

*世界生成条目*

`adjacent` 与 `replace` 接受相同的三种形式，并在其基础上增加第二个条件：只有当与该位置接触的 26 个方块（包括面、棱和角）中至少有一个匹配该列表时，才会使用该位置。省略则不做检查。

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

这会让硫矿只生成在已经通向洞穴或地表的砂岩中，而埋在地下的砂岩则不受影响。尚不存在的区块中的相邻方块一律视为不匹配，而不会去读取，因此该检查绝不会导致区块被生成。

所有形状都遵循该条件，因为它是判断单个方块能否被取用的一部分。`geode` 的外壳与填充物是分别指定的，这两者的放置不做此检查。

如果某个条目只列出了未注册的方块，它会被跳过并报错，而不是到处生成。

### 跟随者条目

*世界生成条目*

世界生成条目的 `then` 列表中的每一项，可以是带权重的名称；当某个跟随者需要自己的方向时，也可以是对象。

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
| `name`   | 是 | 条目名称 |                  | 从该条目生长出来的世界生成条目。裸名称按本资源包的命名空间解析 |
| `weight` | 否 | 整数 | `1` | 该跟随者相对于列表中其他项被选中的频率 |
| `spread` | 否 | 整数，方块数 | 该条目的 `thenSpread` | 该跟随者的方向可向侧面偏斜多远，仅对本条目有效 |
| `depth`  | 否 | 整数或范围 | 该条目的 `thenDepth` | 该跟随者的方向向下（负值）或向上偏斜多远，仅对本条目有效 |

`name=weight` 是只含这两项的对象的简写，`empty=weight` 表示不排入任何内容。由于 `spread` 和 `depth` 是逐条目设置的，同一个列表可以让钻石矿脉的尖端笔直向下，同时从同一条矿脉向侧面分出一条支脉。

## 形状

*生成世界*

`shape` 块带有一个 `type`。某个类型未列出的键会被它忽略。

下面一次性展示所有键。实际文件只写需要的键。标明属于某个类型的键只会被该类型读取。

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

| 类型 | 生成的内容 |
| --- | --- |
| `cluster`    | 默认的团块，即矿脉。使用 `size` |
| `largevein`  | 带分支的长条蜿蜒矿脉。使用 `size` |
| `plate`      | 一个扁平的圆盘 |
| `geode`      | 带外壳的中空腔体 |
| `decoration` | 地表散布物，例如花或蘑菇。使用 `size` |
| `tree`       | 一整棵树 |
| `vines`      | 在已有方块上生成藤蔓。使用 `size` |
| `basin`      | 向中心逐渐加深的碗形 |
| `spire`      | 逐渐收窄的柱体 |
| `nodule`     | 粗糙的球体 |
| `vent`       | 遇到障碍就停止的细柱 |
| `imprint`    | 你的一个 `.nbt` 模板。能放进一个区块内的模板会被微调位置，使其无论朝向如何，都完整落在正在生成的区块里，而不会伸进尚未生成的相邻区块；比区块大的模板只会放置在周围地面已经存在的位置 |
| `belt`       | 跨越多个区块的团块，用于石质区域 |
| `field`      | 一次性为每个方块计算矿脉，与硬度分组共享矿脉形态 |
| `vein`       | 以原点为中心、由带种子的噪声场计算出的矿床，做法与 Immersive Geology 相同：每个区块只写入自己所在的那一片，涉及每条其 24 格范围触及该区块的矿脉，因此不会产生连锁生成，并且 `/rdplserver vein` 可以在陆地生成之前告诉你矿脉会在哪里。使用 `size`、`attempts`、`rarity` 和高度带；`pattern` 决定外观 |
| `spring`     | 从洞穴墙壁渗出的流体：放置在上方、下方和三个侧面都是岩石、另一侧敞开的位置，并设为流动状态 |

### 大小与形态

*形状*

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `type`          | 全部 | 上述形状之一 | `cluster` | 使用哪种形状 |
| `radius`        | plate、geode、basin、spire、nodule、vent | 整数或范围 | `6` | 宽度 |
| `height`        | plate、geode、basin、spire、vent、tree | 整数或范围 | `1`，geode 为 `8`，tree 为 `5` | 高度或厚度 |
| `width`         | geode | 整数或范围 | `12` | 腔体的整体跨度 |
| `plane`         | plate、basin、spire、vent | `circle`、`square` | `circle` | 底面轮廓 |
| `slim`          | plate、largevein、nodule | 布尔值 | `false` | plate：薄一层。largevein：单方块分支。nodule：空心外壳 |
| `hanging`       | spire、vent | 布尔值 | `false` | 从天花板向下生长，而不是从地面向上生长 |
| `taper`         | spire | `straight`、`bell`、`needle` | `straight` | 宽度向尖端收窄的方式。`straight` 均匀收窄，`bell` 下部保持宽度然后骤降，`needle` 立即变细成一根长尖 |
| `outline`       | geode | 方块名称 | 无 | 外壳方块 |
| `fill`          | geode | 方块名称 | 无 | 中间的填充物。省略则中间为空心 |
| `middle`        | geode | 方块名称 | 无 | 位于主体与 `outline` 之间的一层壳，相当于现代紫水晶晶洞中的方解石 |
| `budding`       | geode | 方块名称 | 无 | 替换朝向中空内部的主体方块，如同紫水晶母岩。需要 `fill` |
| `buddingChance` | geode | 0.0 至 1.0 | `0.083` | 这些主体方块中有多少会变成母岩 |
| `crystal`       | geode | 方块名称 | 无 | 在 `budding` 方块旁的空腔中生长，如同紫水晶簇 |
| `crystalChance` | geode | 0.0 至 1.0 | `0.35` | 这些位置中有多少会长出晶体 |
| `crack`         | geode | 0.0 至 1.0 | `0` | 晶洞被裂开的概率：从中心穿过每一层通向一侧的管道，内部用 `fill` 填充。现代紫水晶晶洞使用 `0.95` |

### 放置

*形状*

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `surface`          | decoration、tree | 方块名称列表 | 无 | 它将生长于哪些方块之上 |
| `seeSky`           | decoration | 布尔值 | `true` | 仅在能看到天空的位置放置 |
| `checkStay`        | decoration | 布尔值 | `true` | 仅在方块能够存续的位置放置 |
| `stackHeight`      | decoration | 整数或范围 | `1` | 向上堆叠多少个 |
| `scatterX`         | decoration、tree | 整数 | `8` | 向侧面游走多远 |
| `scatterY`         | decoration、tree | 整数 | `4` | 在垂直方向游走多远 |
| `scatterZ`         | decoration、tree | 整数 | `8` | 向侧面游走多远 |
| `rarity`           | 任意 | 整数 | 无（belt 为 `400`） | 每隔这么多个区块放置一次。对 belt 来说，它决定各个 belt 的间距；对其他任何形状来说，它控制整个条目，使得每这么多个区块中只有一个会尝试其 `attempts`。`field` 忽略它 |
| `rarityIsPerChunk` | 任意 | 布尔值 | `false` | 将 `rarity` 改为表示每个区块放置多少次 |

### 树木

*形状*

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

没有 `log` 或 `leaves` 的 `tree` 不会生成任何内容，并会在日志中说明。如果指定了 `structure`，或在 `structures` 下指定多个，则会在每个位置种下该模板而不是生长一棵树，此时不再需要 `log` 或 `leaves`；使用模板的树对 `turns`、`mirrors`、`integrity`、`lootTable` 和 `locateAs` 的读取方式与 `imprint` 完全相同。

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `log`    | tree | 方块名称 | 无 | 树干方块 |
| `leaves` | tree | 方块名称 | 无 | 树叶方块 |
| `vines`  | tree | 布尔值 | `false` | 从树叶上垂下藤蔓 |

### 放置模板

*形状*

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `structure`  | imprint、tree | `namespace:name` | 无 | 要放置的模板 |
| `integrity`  | imprint、tree | 1 至 100 | `100` | 模板中实际出现的方块所占的百分比 |
| `lootTable`  | imprint、tree | `namespace:path` | 无 | 已放置模板内的每个箱子，以及其他任何可接受战利品表的容器（包括潜影盒或模组的板条箱），在第一次被打开时都会用该战利品表填充。适用于 `structure` 以及 `structures` 的每个条目；每个箱子各自使用独立的种子 |
| `structures` | imprint、tree | 列表 | 无 | 可供选择的多个模板，每次放置其中一个。每个条目写作 `{ "structure": "namespace:name", "weight": 3 }`，或写成裸名称以表示等概率。会覆盖 `structure` |
| `turns`      | imprint、tree | 列表 | 任意 | 允许放置时采用的朝向：`none`、`quarter`、`half`、`threequarter`。条目可以带 `weight`。省略则四种朝向概率相等 |
| `mirrors`    | imprint、tree | 列表 | 无 | 同时进行翻转：`none`、`leftright`、`frontback`，可带可选的 `weight`。自带权重的条目写作 `{ "mirror": "leftright", "weight": 2 }`，`turns` 条目同样写法，只是键名为 `turn` |
| `at`         | imprint | 两个整数，x 和 z | 无 | 在该方块坐标的地表上精确放置一次，在该区块生成时进行，而不是按概率放置。参见 [位于精确位置的结构](#位于精确位置的结构) |
| `locateAs`   | imprint、tree | 字符串 | 无 | 将该条目放置的每个结构登记为该名称，这样 `/locate <name>` 就能找到最近的一个。参见 [查找已放置的结构](#查找已放置的结构) |

对于内置类型都无法涵盖的形状，请使用 `imprint`：把它做成 `.nbt` 模板再放置，用 `structures` 增加变化，用 `turns` 和 `mirrors` 旋转与翻转，用 `integrity` 把它打散成比你绘制的文件更粗糙的样子。

### 位于精确位置的结构

*形状*

原版结构可以通过 `terrain` 设置中的 `structureAt` 固定到精确的位置，写成 `structure=x,z` 的条目，每行一个：`"structureAt": ["villages=1000,-500"]`。**x 和 z 是方块坐标，而不是区块坐标**，结构会在包含该方块的区块中生成；村庄的水井就立在该方块上，而其他结构则从游戏在该区块中本来会开始生成的位置开始。每个想要的实例写一个条目。它的间距、间隔、最小出生距离和平地检查全部不再生效，因此该位置是否合适由资源包自行负责，两个相距不到一个区块的固定点会在同一个区块中放入两个结构。结构一旦建立，就按通常的规则贴合其所在区块的地面。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `structureAt` | `structure=x,z` 列表 | 无 | 将原版结构固定到精确位置，每个想要的实例写一个条目。x 和 z 是方块坐标，结构会在包含该方块的区块中生成；它的间距、间隔、最小出生距离和平地检查全部不再生效 |

`imprint` 条目可以在其形状中用 `"at": [x, z]` 以同样的方式固定位置，在该区块生成时于这些坐标的地表上精确放置一次，而不是按概率放置。它可以与 `locateAs` 组合使用，因此被固定的结构也可以用 /locate 找到。

### 查找已放置的结构

*形状*

带有 `"locateAs": "Crypt"` 的 `imprint` 条目会将它放置的每个结构登记为该名称，之后 `/locate Crypt` 会指向最近的一个，且该名称会出现在 Tab 补全中。只有已经生成的结构才能被找到，因为资源包结构是在生成区块时按概率放置的，而不是放在游戏可以预测的网格上。这些名称保存在世界存档中，因此重启后依然有效，在服务器上也能使用。以这种方式登记的名称还可以用 `gotoPlaceLevels` 赋予独立的权限，这样资源包就能把谁可以被传送到它自己的结构，与原版结构分开决定。

### 矿场与矿脉键

*形状*

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `field`     | field | 对象 | `{ "type": "speckle" }` | 矿场的计算方式。键与硬度分组的 `field` 相同，详见 [矿场](#矿场)：`speckle` 搭配 `chances` 和 `spread`，或 `seeded` 搭配 `cell`、`seeds`、`reach`、`arms` 和 `armReach` |
| `threshold` | field、vein | 0.0 至 1.0 | `0.5`（vein 为 `0.4`） | 某个方块处的矿场强度必须达到多少才会放置。越低填充越多 |
| `fade`      | field | 整数 | `0` | 让高度带的顶部逐渐稀疏，而不是平直地截止：在高度范围最上面的这么多格内，每个方块被放置的几率逐级下降，外观与引擎在 `deepStone` 与上方世界交界处所用的效果相同 |
| `pattern`   | vein | `default`、`banded` 或 `tube` | `default` | 矿床的外观：扭曲的团块、每隔几格叠起的层状，或在岩石中蜿蜒的空心管道 |
| `density`   | vein | 0.0 至 1.0 | `1.0` | 符合条件的方块中实际被放置的比例，相当于对每个方块抛一次硬币 |
| `rich`      | vein | 方块名称 | 无 | 放置在场强范围中高于 `threshold` 的最上面五分之一，即矿床的核心，取代该条目自己的方块 |
| `poor`      | vein | 方块名称 | 无 | 放置在该范围的最下面五分之二，即边缘，取代该条目自己的方块；中间部分则是该条目自己的方块。任何一层省略，则该处放置该条目自己的方块 |
| `richAt`    | vein | 0.0 至 1.0 | `0.88` | 富矿层在该范围内的起点：`0.88` 使富矿方块只出现在矿床最强的八分之一，数值越低富矿核心越粗，`1.0` 则完全没有富矿方块 |
| `poorAt`    | vein | 0.0 至 1.0 | `0.4` | 该条目自己的方块从哪里开始：低于此值则放置 `poor` 方块，因此 `0.4` 会得到最下面五分之二的边缘，`0.0` 则没有贫矿边缘。会被限制在 `richAt` 以内 |

### 带状区域

*形状*

`belt` 是一个远大于单个区块的球体，用于石质区域而不是矿脉。它的 `radius` 就是球体的大小，每个区块根据世界种子和条目自己的名称，自行算出附近各球体的起点，因此无论区块以什么方式生成，belt 都会完整呈现，并且绝不会向相邻区块写入任何内容。

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

belt 忽略 `attempts` 和 `spread`，因为它是按区块而不是按尝试次数放置的。`minHeight` 和 `maxHeight` 是球心所在的高度带，球体会在该高度带之外再延伸 `radius`。`replace` 决定它吞噬什么，`biomes` 以及温度和降雨限制是在球心处检查的，因此 belt 要么完整出现，要么完全不出现，而不会在生物群系边缘被截断。

开销随 `radius` 的立方增长，而较低的 `rarity` 会使其成倍增加，所以请从默认值开始，缓慢地提高半径。

### 矿场

*形状*

`field` 不是在某一点放置内容，而是一次性放置一切。它不是先挑一个位置再围绕它构建形状，而是对区块内 `minHeight` 与 `maxHeight` 之间的每个方块提出一个问题，并在答案不低于 `threshold` 的地方放置。这个问题与硬度分组所问的相同，所以两者描述的是同样的矿脉，资源包可以让一个分组和一个条目保持一致。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `threshold` | 否 | 0.0 至 1.0 | `0.5` | 矿场强度必须达到多少才会放置方块 |
| `field`     | 是 | 对象 | 无 | 与硬度分组所接受的对象相同，同样有 `speckle` 和 `seeded` 两种类型 |

较低的 `threshold` 会取用大部分矿场，得到宽阔的矿层；较高的则只取用每个团块的中心，得到零散的小矿囊。使用 `speckle` 会得到许多细小的斑点，使用 `seeded` 则得到较圆的块状，一旦带有分支，就成了节点之间有触须相连的结。

与 belt 一样，field 忽略 `attempts` 和 `spread`，因为它是按区块而不是按尝试次数求值的，并且绝不会向相邻区块写入。它由世界种子和条目自己的名称计算得出，所以同一个种子总是得到同样的矿脉，而名称不同的两个条目绝不会重合。`replace`、`adjacent`、`biomes` 和气候限制都照常生效。

`field` 矿脉是唯一一种需要你来描述而不是挑选的形状。它运行的是硬度分组所用的同一套格点，所以带有几条分支的 `seeded` 会得到向邻近节点伸出触须的结，这是矿脉而不是团块，而 `threshold` 决定其中有多少足够坚实可以放置：

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

这些键要放在单独的 `field` 对象中，而不是与 `type` 并列，因为形状上的 `type` 已经写明是 `field`。

## 扩散

*生成世界*

`spread` 块带有一个 `type`。

下面一次性展示所有键。实际文件只写需要的键。标明属于某个类型的键只会被该类型读取。

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
| `even`      | 在高度之间的任意位置，均匀分布。默认值 |
| `centered`  | 偏向某一高度，随距离增大而变稀疏 |
| `sprawl`    | 跨越一段高度范围的分形矿脉 |
| `terrain`   | 沿着地表 |
| `cavern`    | 在洞穴的地面或顶部 |
| `submerged` | 在水下或其他流体之下 |

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `type`              | 全部 | 上述扩散之一 | `even` | 使用哪种扩散 |
| `center`            | centered | 整数 | 高度范围的中点 | 聚集所围绕的高度 |
| `range`             | centered | 整数 | 高度范围的一半 | 距该高度可延伸多远 |
| `smoothness`        | centered | 1 至 8 | `2` | 对多少次随机取平均。越高，范围越集中 |
| `veinHeight`        | sprawl | 整数 | 高度范围 | 一条矿脉有多高 |
| `veinDiameter`      | sprawl | 整数 | `12` | 一条矿脉有多宽 |
| `verticalDensity`   | sprawl | 1 至 100 | `16` | 垂直方向上的密实程度 |
| `horizontalDensity` | sprawl | 1 至 100 | `32` | 水平方向上的密实程度 |
| `offsetMin`         | terrain | 整数 | `0` | 距地表的最低偏移 |
| `offsetMax`         | terrain | 整数 | `offsetMin` | 距地表的最高偏移 |
| `ceiling`           | cavern | 布尔值 | `false` | 附着在洞穴顶部而不是地面 |

## 结构地图

*生成世界*

结构地图把多个模板组合成网格上的一座命名建筑，远远超出单个 `.nbt` 文件 32 格的限制。每一层用若干行单个字符绘制，一个字符对应一个单元格，并叠在前一层之上，高度为一个单元格。最多 8 层，每层 8 乘 8 个单元格；按默认的单元格 32 计算，每边为 256 格，即原版的建筑高度。

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `name`       | 文本 | 文件名 | 该地图在日志中的名称 |
| `cell`       | 数字 | `32` | 网格间距，单位为方块，最大 48。比单元格小的模板位于单元格的角落，因此满尺寸的部件可以无缝拼接 |
| `ground`     | 数字 | `0` | 哪一层作为地面贴合地形表面。在它之前的层向下挖掘，建筑的地下室就是这样来的 |
| `at`         | 两个数字 | 无 | 将一份副本固定在精确的方块坐标，方式与 `structureAt` 固定村庄相同 |
| `spacing`    | 数字 | `0` | 按相隔这么多个区块的网格散布副本，位置由世界种子加以抖动。`0` 表示不散布，所以只有 `at` 的地图恰好只建造一次 |
| `chance`     | 数字 | `100` | 网格点中会建造副本的百分比 |
| `dimensions` | 列表 | 全部 | 该地图可以在其中建造的维度 ID |
| `layers`     | 列表 | 无 | 各层，自下而上，每层包含一个 `palette` 和一个 `map` |

调色板通过注册表键，引用资源包 `<namespace>/structures/` 中的模板。

| 值 | 作用 |
| --- | --- |
| `"a": "mypack:keep"` | 该层的每个 `a` 单元格都放置这个模板 |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | 每个 `a` 单元格按权重从列表中抽取，依据世界种子和该单元格的位置，因此同一建筑的两份副本会有所不同，但同一个世界总会建出同样的结果 |
| `.` | 空单元格，不放置任何内容 |

每份副本都会根据世界种子抽取四个朝向之一，整座建筑连同其中的模板一起旋转，所以跨单元格相接的墙壁依然相接。地面层贴合建筑中心下方采样到的地形表面。每个区块只建造网格中属于自己的那一片，因此无论区块以什么顺序加载，横跨许多区块的建筑都不会引发连锁生成。类型为 `template` 的 [村庄地块](#村庄地块) 也可以把某张地图指定为它的 `structure`，这样组合体就成为一座村庄建筑。

## 村庄地块

*生成世界*

`<namespace>/villages/*.json`

文件的路径就是该地块的名称，`villagePieces` 随后可以按这个名称来保留或舍弃它。

这里的文件会添加一个村庄可以建造的部件，与原版的部件并存。有两种类型，由 `type` 选择。

下面一次性展示所有键。实际文件只写需要的键。标明属于某个类型的键只会被该类型读取。

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

### 每个地块

*村庄地块*

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `type`       | 全部 | `farm` 或 `template` | `farm` | 地块的类型 |
| `weight`     | 全部 | 整数 | `3` | 该地块相对于资源包中其他地块被选中的频率 |
| `leastCount` | 全部 | 整数 | `1` | 每个村庄最少数量，在加上村庄规模之前 |
| `mostCount`  | 全部 | 整数 | `4` | 每个村庄最多数量，在加上村庄规模之前 |
| `width`      | 全部 | 整数 | `7` | 沿道路方向的尺寸 |
| `height`     | 全部 | 整数 | `4` | 地面以上被清空的高度 |
| `depth`      | 全部 | 整数 | `9` | 远离道路方向的尺寸 |
| `apron`      | 全部 | 整数 | `2` | 地块下方的地面与道路高度相差多少以内才不会被拒绝或沿道路滑动调整：其下最多填充这么多格，或在其上方的隆起中最多切削这么多格，并且最高角与最低角之间的差不超过这个数。丘陵中的宽地块需要更大的值。设得很高时，地块会直接在斜坡上形成梯田，在不合适的地方这会吃掉一座山 |
| `ground`     | 全部 | 方块名称 | `minecraft:dirt` | 在斜坡上垫在下面的内容 |
| `requires`   | 全部 | 模组 ID 或资源包命名空间列表 | 无 | 除非全部存在，否则该地块被舍弃 |

每个资源包地块都会作为一个条目提供给村庄，所以一旦村庄需要一个地块，由 `weight` 决定选中你的哪一个。某次放置使用了哪个地块会写入村庄自己的数据，因此加载时能够正确重建。

### 农场

*村庄地块*

`farm` 是原版的农田，只是改为描述而不是编码：一个你指定大小的地块，用某种方块围边，里面是由水渠隔开的一行行土壤，并从你的列表中为每个方块挑选一种作物种下。

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

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `crops`    | farm | 方块名称列表 | 小麦 | 每个方块种一株，处于随机的生长阶段 |
| `edge`     | farm | 方块名称 | `minecraft:log` | 地块周围的边框 |
| `soil`     | farm | 方块名称 | `minecraft:farmland` | 各行所用的材料 |
| `water`    | farm | 布尔值 | `true` | 在各行之间放置一条水渠 |
| `rowWidth` | farm | 整数 | `2` | 每行土壤的宽度 |

### 由模板构建

*村庄地块*

`template` 改为放置你的一个 `.nbt` 结构，并使其转向面对村庄道路。

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

如果 `template` 的 `structure` 指向你的某个 [结构地图](#结构地图)，则整个组合体都会作为该地块放置。此时地块的大小由地图决定，即其占地范围和叠起的层数乘以单元格，因此 `width`、`height`、`depth` 和 `integrity` 不会被读取。地图 `ground` 之前的层会向下挖成地下室，带权重的调色板单元格仍然会按每座建筑各自抽取，所以同一张地图建出的两座塔可以不同。

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| 键 | 适用类型 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `structure`      | template | `namespace:name` | 无 | 要放置的模板，或你的某个结构地图，此时由它决定地块的大小 |
| `integrity`      | template | 1 至 100 | `100` | 模板中出现的方块所占的百分比 |
| `lootTable`      | template | `namespace:path` | 无 | 已放置模板内的每个箱子在第一次被打开时用该战利品表填充。指定了结构地图的地块不受影响 |
| `villagers`      | 全部 | 整数 | `0` | 该地块生成多少人 |
| `villagerEntity` | 全部 | `namespace:name` | 村民 | 谁住在那里，例如你自己的实体变种 |
| `villagerX`      | 全部 | 整数 | `1` | 他们出现的位置，沿地块宽度方向 |
| `villagerY`      | 全部 | 整数 | `1` | 他们出现的位置，高于地板多少 |
| `villagerZ`      | 全部 | 整数 | `1` | 他们出现的位置，进入地块的深度 |

## 城市布局地图

*生成世界*

城市地图在网格上绘制村庄的街道平面图，一个字符对应一个单元格，村庄按这幅图来布置，而不是自行生长。街道、广场和地块生成的部件与自然生长的村庄所用的相同，因此所有道路选项、桥梁、码头、断头路、灯柱、尽头环岛和广场中心装饰都原样适用。世界模板在 `villageLayout` 中指定地图名称。

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `name`     | 文本 | 文件名 | 该地图在日志中的名称 |
| `cell`     | 数字 | `48` | 网格间距，单位为方块，8 至 128。道路按资源包的道路宽度沿单元格中线延伸，地块居中放在各自的单元格里，所以一个单元格需要容纳最宽的地块，外加临街所需的空间 |
| `palette`  | 对象 | 无 | 每个字符铺设什么，见下表 |
| `map`      | 列表 | 无 | 各行，最多 64 乘 64 个单元格。比最宽一行短的行，在其末端之后视为开阔 |
| `settings` | 对象 | 无 | 仅用于这张地图的村庄设置，使用世界模板所用的名称，例如 `villagePathCenterBlock`。它们优先于模板的设置，而生物群系自己的村庄设置又优先于它们 |

| 值 | 作用 |
| --- | --- |
| `"#": "street"` | 沿某一行或某一列连续排列的街道单元格会成为一个道路盒，宽度为资源包的道路宽度。行方向与列方向的连续段相交处，路口与其他位置一样照常绘制。在两个方向上都没有连续段的孤立街道单元格，会沿行方向铺成一小段短道 |
| `"+": "plaza"` | 一口水井及其广场环。连续段会穿过广场单元格，所以街道在水井处交汇，而位于十字路口的广场会像环岛一样，把它的水井或其 `villageWellStructure` 中心装饰立在路口中央。文件中的第一个广场就是村庄自己的水井，它把地图固定在村庄建立的位置；没有广场的地图则以该位置为中心 |
| `"a": "alley"` | 一条窄道。建筑朝向它，但它不连通任何地方，仍按常规的小巷规则处理 |
| `"J": "junction"` | 向两个方向都铺设的街道单元格，因此即使图中只有一个方向穿过它，那里也会有一个路口。穿过它的那条支路长一个单元格 |
| `"b": "bulb"` | 以尽头环岛结束的街道单元格。地图一旦有 bulb 单元格，就只有位于 bulb 单元格内的道路末端才会得到 bulb，且每个有空间的末端都会有；没有 bulb 单元格的地图则保留四分之三的末端 |
| `"E": { "kind": "elevated", "height": 8 }` | 抬升到桥面上的街道单元格，桥面比其所在连续高架单元格范围下方的最高地面高出 `height` 格，取值 2 至 64，两端各有一段每行升一格的坡道。该范围内相交的街道随之抬升。如果某段高架的桥面或坡道会触及被铁路或水井按自身高度占用的行，则它保持在地面高度，并在日志中留下一行记录。任何值都可以这样写成对象，由 `kind` 指明类别 |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | 使用自己的道路键铺设的街道，这些键优先于地图和模板的设置。它的宽度取决于它自己的 `villagePathExtraWidth`、`villagePathSidewalkWidth` 和标线，路面、标线和人行道取决于它自己的方块键，所以大道或小巷都可以画上自己专属的标记。一段连续的街道采用其第一个设置了任何键的单元格的键。无论多宽或多窄，画出来的街道始终是街道：它绝不会被当成小巷或尽头环岛 |
| `"T": "mypack:tower"` | 一个地块单元格，按该地块定义铺设，居中放在单元格内，并朝向最近的街道 |
| `"T": ["mypack:a=3", "mypack:b=1"]` | 同上，但按权重根据世界种子和该单元格的位置抽取，所以同一个世界总会在那里铺设同样的地块 |
| `"g": "grow"` | 交给自然生长。设置了 `villagePlotsLeast` 时，生长出的街区和街道填充会填满这类单元格并从地图向外扩展；没有设置时，该单元格保持开阔 |
| `.` | 开阔的地面，不铺设任何内容 |

每张地图都会根据世界种子抽取四个朝向之一并整体旋转，所以平面图从任何一侧看都一样。道路最先铺设，所以会与道路或其他地块重叠的地块会保持开阔并在日志中留下一行记录，没有任何资源包提供的地块名称同样会使其单元格保持开阔。地图不会改变各部件的装饰方式：道路键、`villageBlocks`、灯和水井替换都与自然生长的村庄一样读取。绘制出的地图不会向外生长：街道旁不会填补小巷，其道路末端会得到 bulb（照常是四分之三，或按其 bulb 单元格所指定的），但沿途不会有房屋。

## 连绵城市

*生成世界*

连绵城市没有边界。`villageCitySpacing` 设为 `1` 时，世界中的每个街区群都是一座独立的城市：一个中心有井的广场，以及从中伸出、与周围街区群的街道相接的街道。玩家走到哪里，城市就生成到哪里，城市之间没有空旷的郊野。

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `terrainAdaptation` | 布尔值 | `false` | 铺设 RDPL 自己的城市街道。连绵城市需要开启它 |
| `villageCitySpacing` | 整数，0 到 256 | `0` | `1` 让每个街区群都成为一座城市，城市正是因此而连绵不断。在 1.12.2 上只有 `1` 会带来变化：`0` 和其他任何数值都照旧按 `structureSpacing` 播种村庄 |
| `villageBlockSizes` | `size=weight` 的列表 | 空 | 平行街道之间的街区有多深，每个街区群抽取一次，因此城市会混合细密与稀疏的街网 |
| `villagePlotsMost` | 整数，0 或更大 | `0` | 一个街区群最多安置多少个地块。0 表示不设上限 |
| `villagePlotsBackRow` | 布尔值 | `true` | 在每个正对街道的地块后方再安置一个地块，使每个街区的内部也被建满 |

街区群是世界上的一个正方形，宽度为最大地块的两倍，再加上一个广场和每侧一条街道，向上取整到 16 格，且不小于 96 格。其他所有街道设置，从路面方块到路灯、桥梁、隧道和下水道，装点连绵城市的方式与其他城市相同。`villagePlotsLeast` 不起作用，因为每个街区群本身已是一座完整的城市。规划中地块和井合计不超过两个的街区群会留空，与任何这么小的城市一样。

**街道如何相接。** 每个街区群都把井建在街区群内的同一个区块中，并把它的主十字路口，即在井处交叉的两条街道，一直铺到街区群的边缘。因此相邻街区群的十字街道会连成笔直的大道，贯穿整个世界，并在两个街区群之间的接缝处相接，双方都无需参照对方。街区群铺设的任何东西都不会越过它的边缘，所以无论哪个邻居先生成，街区群生成的结果都相同，它的井也不会移动。

**代价。** 连绵城市的每个区块都是建成区，因此新地形在任何地方的开销都与大城市中心相当：放置建筑并为它们计算光照。快速飞行的玩家会超过生成速度，在新街区群生成期间刻速率会下降。用 `pregenOnNewWorld` 或 `/rdplserver pregen` 预生成玩家起步的区域，并保持适中的视距，可以控制住这一点，但在玩家探索新地形期间，TPS 仍会低于普通世界。存档会随探索过的地形增长，与任何建成区一样。游戏只在内存中保留距玩家或出生点 96 个区块以内的村庄，其余的随存档的村庄记录收起，当附近的地形再次生成时，再把它原样读回。

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
| `retrogen`            | 布尔值 | `false` | 为每个标有 `"retrogen": true` 的世界生成条目，补上在该条目出现之前保存的区块。关闭时，已存在的区块不受影响 |
| `adoptExistingChunks` | 布尔值 | `false` | 第一次见到旧区块时会发生什么：开启时，它被标记为仿佛该资源包已经生成过它，永远不会补生成；关闭时，它与其他区块一样被补生成。要填充已有的世界，请开启 `retrogen` 并关闭此项 |

带有 `"retrogen": true` 的条目会生成到你添加它之前就已保存的区块中。每个区块都会记录它已经经历过什么，所以不会重复做任何事。

条目上的标记只是把条目标为符合条件。补生成是否启用由 `retrogen` 设置决定，资源包可以在其 `settings` 块中设置它，玩家也可以在配置中设置，默认关闭。与之配合的 `adoptExistingChunks` 决定第一次见到旧区块时的处理：开启时，该区块被标记为仿佛该资源包已经生成过它，永远不会补生成；关闭时，它与其他区块一样被补生成。在 `adoptExistingChunks` 同时开启的情况下开启 `retrogen` 不会起任何作用，因为每个旧区块在排入队列之前就已被注销。要填充已有的世界，请同时开启 `retrogen` 并关闭 `adoptExistingChunks`。

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

在配置中更改 `retrogenKey` 会使每个区块再次符合条件，这会在旧矿脉之上再添加新矿脉，使密度翻倍。这是有意为之，也是该键需要手动更改的原因。

## 预生成

*生成世界*

提前生成世界的陆地，这样游玩时就没人需要生成区块：没有区块卡顿，磁盘占用大小可知，只需在一开始等待一次，而不是在头一个小时里不断卡顿。

出生点周围的 12 个区块总是会被接管，无论资源包或配置怎么说，因为在任何人加入之前游戏自己就恰好会生成这么多。如果不管它，这片地面会在未打光的状态下出现，并在玩家走过时一个区块一个区块地被修整；接管之后，它会一次性完成，玩家落脚的就是已经处理好的地面。`pregenOnNewWorld` 设定要再向外延伸多远，而命令则手动运行一次。

`/rdplserver pregen <radius>` 会生成运行该命令的位置周围这么多个区块范围内的所有区块。`status` 会说明进度，`stop` 会结束它，`<radius> relight` 则只对已经存在的陆地运行光照处理，修整运行时无法触及的接缝，而从未生成的部分则不予理会。

运行期间所有人都会被控制住：变为旁观者、固定在原地、屏幕中央显示一行脉动的文字，周围的世界暂停。每位玩家进入的游戏模式会在被控制时写入玩家数据，所以运行中途保存的存档、崩溃或重新加入，都不会让任何人一直停留在旁观者模式；运行结束时会准确恢复所取走的模式，或在设置了 `worldGameMode` 时恢复为资源包指定的模式。进度每完成十分之一就会播报，每次运行结束时会对自己的那一方块区域重新打光，全部完成后玩家被释放并收到问候。每个维度生成到了哪里会保存在世界中，所以已完成的世界不会再次运行，除非某个维度的陆地所在的任何文件从磁盘上丢失，这会被察觉并使该维度重新来过。

在资源包中，这些键放在 [世界模板](#世界模板) 的 `settings` 块中，与其他每个 `chunks` 键一样。下面展示其中的每一项，只有 `pregenBorderLimit` 不在其中，因为它只存在于配置中：

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

### 生成的内容

*预生成*

| 键 | 作用 | 设置它的原因 |
| --- | --- | --- |
| `pregenOnNewWorld`            | 在任何人游玩之前，围绕出生点生成的区块半径。12 是下限，0 表示取这个下限而不是什么都不做，因为游戏自己本来就会在出生点周围生成 12 个区块：运行会接管这片地面并一次性为其打光，而不是任由它跟在玩家身后慢慢出现。调高它可以比游戏本身延伸得更远 | 设定资源包在游戏本来就会生成的地面之外再延伸多远 |
| `pregenDimensions`            | 要生成哪些维度，按顺序，每个维度围绕它自己的出生点 | 添加下界、末地或你自己的维度 |
| `pregenAllDimensions`         | 生成所有已注册的维度而不是一个列表，主世界优先 | 适用于维度很多的资源包。每个模组的维度都会算在内，所以要留意大小 |
| `pregenDimensionsWhenEntered` | 这些维度在有人第一次踏入时才生成，并再次控制住所有人直到完成 | 大多数玩家从不前往的维度；从不去的玩家不用付出任何代价 |
| `pregenToBorder`              | 把每个维度一直填充到其世界边界，而不是按半径 | 有边界的世界 |
| `pregenBorderLimit`           | 边界最多可以延伸多远，超过则拒绝运行。仅限配置，不是资源包的键 | 防止失控运行的保护措施；只有在清楚它所需的时间和磁盘空间时才调高 |

### 一次运行的行为

*预生成*

| 键 | 作用 | 设置它的原因 |
| --- | --- | --- |
| `pregenResume`         | 被停止或中断的运行会从中断处继续。运行开始时，其维度、中心和半径会写入存档，因此崩溃、断电或中途退出，在下次加载时都会在中断位置后约十秒内恢复。出于有意而停止的运行（通过命令或看门狗）会保持停止 | 服务器上的长时间运行；小型运行即使不用它，重新开始的代价也很低 |
| `pregenKeepLoaded`     | 在运行后方保持加载的区块数，使得一个区块在被修整和打光时，它的相邻区块都已就绪 | 如果重新打光报告说有很多区块被留到以后处理，就调高它；会占用内存 |
| `pregenPauseAbove`     | 当等待写入的区块达到这么多时，运行会暂时休息 | 磁盘较慢时调低它 |
| `pregenMillisPerRound` | 每个刻最多可以花多长时间生成陆地 | 在空世界中调高，在有人游玩的服务器上调低 |

预生成有自己的快速光照路径，当安装了 Alfheim 或 Phosphor 之类的光照引擎时，它会退让，让该引擎来完成工作。无论哪种方式，最终得到的都是已完成且光照完整的陆地。

发布前请自己运行一遍，使用准备发布的半径，从头到尾。区块数随半径的平方增长，两个方向各 63 就是一万六千个区块，500 则超过一百万，每个大约十千字节，所以测试世界的 region 文件夹和实际耗时才是应当摆在玩家面前的真实数字。不要发布从未运行过的半径。

### 向玩家显示什么

*预生成*

| 键 | 作用 | 设置它的原因 |
| --- | --- | --- |
| `pregenRunningSays`、`pregenRelightSays`、`pregenFinishedSays`、`pregenStoppedSays` | 各阶段的聊天消息。前两个可以包含用于百分比的 `%d`，其后可跟用于维度名称的 `%s`，或用 `%1$d` 和 `%2$s` 以任意顺序放置它们，并且总是以该阶段的 ` - ETA 00:00:00` 结尾，这不是一项设置。完成和停止的消息在所有要求的内容都做完时说一次，以整个过程的 ` - Total time 00:00:00` 结尾，这同样不是一项设置 | 用你的资源包的口吻改写它们，在生成多个维度时写上维度名称，或让它们静默 |
| `pregenSpectatingSays`                                                              | 生成陆地期间屏幕中央的控制提示行。保持默认时，它会使用每位玩家的语言；留空则不显示 | 请保持在大约三十五个字符以内，否则小窗口会将其截断 |
| `pregenLogo`                                                                        | 预生成结束时徽标所在的位置：`left`、`center` 或 `right`，位于屏幕中央文字上方，显示几秒钟，然后随着雾一起淡出 | 它总是会显示；无法识别的词按 `center` 处理 |
| `welcomeSays`                                                                       | 绿色的问候语，在每次登录时以及预生成之后显示。裸条目是适用于所有地方的那一行；`dimension=message` 条目会为该维度覆盖它，并且也会问候每位到达该维度的玩家，例如 `"-1=Welcome to the Nether!"`。`=` 之后留空的消息会使该维度静音；空列表则什么也不显示。保持默认时，它会使用每位玩家的语言 | 一条裸行写上你的资源包名称；再添加维度行，为每个世界设定主题。每行请保持在大约三十五个字符以内 |
| `saysCard`                                                                          | 把本模组所说的内容，即欢迎语、预生成进度和威胁提示行，显示为右下角的一张卡片，而不是聊天消息。卡片滑入，停留八秒后淡出，在打开的界面之上也会显示 | 当聊天很繁忙，或者希望这些内容读起来像是世界的一部分而不是闲聊时，请开启它 |
| `saysIcon`                                                                          | 绘制在卡片上的一个物品，例如 `minecraft:compass`。留空则不绘制 | 为卡片配上你的资源包的徽记 |
| `saysColor`                                                                         | 卡片的背景颜色，十六进制，例如 `1E2630`。留空则使用深石板色 | 与你的资源包的配色相匹配 |
| `saysImage`                                                                         | 来自资源包客户端资源的一张 PNG，例如 `rubyworld:textures/gui/card.png`，拉伸后铺满卡片作为其背景，绘制在颜色之上。留空则不绘制 | 为卡片配上一块绘制的面板；请让图像宽而矮，它会被拉伸到文字所需的任何大小 |
| `saysBackground`                                                                    | 绘制卡片的面板、边框和颜色条，以及玩家被控制期间屏幕中央欢迎语和提示后面的深色背景。关闭后只剩下文字（保留其阴影），如果设置了 `saysImage` 则还有它 | 让文字漂浮在世界之上，或者让绘制的 `saysImage` 独立呈现 |
| `saysFont`                                                                          | 绘制卡片文字所用的字体，以 `namespace:name` 命名，例如 `rubyworld:runes`。留空则使用 RDPL 字体 `resourcedatapackloader:rdpl`。它所指的文件见“卡片”一节 | 为卡片配上你的资源包专属的字体 |
| `toasts`                                                                            | 游戏的哪些提示（右上角的弹出框）会显示。`true` 全部显示，`false` 全部不显示；列表则只显示其中列出的种类：`advancements`，`recipes` 表示已解锁的配方，`tutorial` 表示操作教程提示，`system` 表示游戏自身的通知，`other` 表示其余各项未涵盖的所有提示，例如其他模组的。默认不显示任何提示。玩家的客户端会在加入时采用该值 | 当你的资源包用进度来引导玩家，而其余提示只会碍事时，保留 `["advancements"]` |

### 备份与地图重置

*预生成*

| 键 | 作用 | 设置它的原因 |
| --- | --- | --- |
| `pregenBackup`          | 预生成完成后，在玩家仍被控制期间，将世界复制为一份原始备份。这样生成只需付出一次代价：之后的重置，或使用同一资源包和种子的新世界，会恢复这份副本而不是再次生成，这比预生成两次快得多。该副本保存在存档之外，位于存档旁边的 `rdpl-pristine/<world>`，因此其他模组的备份不会把它一并清掉，它也不会出现在它们管理的文件夹中。如果某份副本所对应的资源包与当前加载的不再匹配，它会被丢弃，并根据手头的世界重新保存，所以更换资源包绝不会重置成别人的地图 | `false` |
| `pregenBackupSays`      | 制作该副本期间向玩家显示的屏幕中央提示行，后面带有百分比。留空则不显示，副本在静默中制作 | `Pack requested world backup` |
| `resetSays`             | `/rdplserver reset` 或一轮游戏结束把地图恢复原样期间向玩家显示的屏幕中央提示行。留空则静默重置 | `Pack requested map reset` |
| `resetSendsTo`          | 重置后把玩家送到哪里：`spawn`、形如 `x,y,z` 的位置，或用 `dimension:x,y,z` 把他们送进另一个世界，重置时把所有人放到大厅而不是回到竞技场，就是这样做到的 | `spawn` |
| `resetRuns`             | 在重置清空地图之后运行的一个函数，以 `namespace:path` 命名。它负责重新建造竞技场，因为用函数制作地图的资源包可以直接再运行一次。留空则不运行任何内容 | 空 |
| `resetClearsEntities`   | 移除所有非玩家实体。生物、掉落物和经验球全部消失，这样地图就回到了开始时的样子 | `true` |
| `resetClearsScores`     | 把资源包保存的每个计分项重置为零，使新一场比赛从零开始。队伍本身会保留 | `true` |
| `resetClearsInventory`  | 清空每位玩家的物品栏，包括盔甲和副手，使一轮游戏从地图发放的物品开始，而不是上一轮留下的。随后会立刻再次发放阵营的 `gives` | `false` |
| `resetClearsExperience` | 把每位玩家的经验重置为 0 级 | `false` |

---

# 游戏模式

## 世界介绍

*游戏模式*

`<namespace>/worldintro/*.json`

文件名可以自己选，只读取文件夹。资源包提供的每个介绍都会运行，按资源包顺序。

在玩家进入世界、获得控制权之前，显示一系列页面。可以是图片上滚动的文字、标题卡、幻灯片，或依次出现的三者。

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

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `pages`    | 是 | 页面列表 | 无 | 按顺序显示。没有页面的文件会被拒绝并报错 |
| `once`     | 否 | 布尔值 | `false` | 每位玩家在每个世界中只播放一次，而不是每次加入都播放 |
| `music`    | 否 | 音效事件名称 | 无 | 整个流程使用的一首曲目，随第一页开始播放 |
| `requires` | 否 | 模组 ID 或资源包命名空间列表 | 无 | 除非全部存在，否则跳过该介绍 |

### 页面

*世界介绍*

`pages` 中的每个条目：

| 键 | 必填 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- | --- |
| `mode`        | 否 | `scroll` 或 `static` | `scroll` | 文字滚动，或文字静止不动直到玩家继续 |
| `text`        | 否 | `.txt` 文件的路径 | 无 | 文字内容。对于只有图片的页面则省略 |
| `background`  | 否 | 纹理路径 | 平铺的泥土背景 | 一个背景 |
| `backgrounds` | 否 | 纹理路径列表 | 无 | 多个，循环播放。如果两者都给出，则叠加到 `background` 上 |
| `interval`    | 否 | 秒 | `5.0` | 当背景多于一个时，每个背景停留多久 |
| `time`        | 否 | 秒 | 根据文字计算 | 滚动页面从头到尾需要多久。对于静止页面，或任何类型的最后一页，它表示多久之后页面自动翻过，如果没有它则会等待按钮 |
| `direction`   | 否 | `up` 或 `down` | `up` | 滚动文字的移动方向 |
| `textScale`   | 否 | 数字 | `1.0` | 乘以字体大小。`static` 页面会把文字换行到屏幕宽度减去两侧边距，当文字仍会压到按钮下方时，会缩小绘制，最多缩小到一半，直到放得下 |
| `settle`      | 否 | 布尔值 | `false` | 以最后一行居中作为结束，而不是一直滚出屏幕 |

### 文本与时间

*世界介绍*

文本文件放在 `<namespace>/texts/*.txt`。纯文本，每个段落占一行，空行会保留为空行。`.md` 文件的读取方式相同，两种文件都支持下文的格式。`PLAYERNAME` 会被替换为玩家的名字，与原版终末之诗的替换方式相同。

`time` 设定一个页面持续多久，因此无论页面上只有一行还是二十行，同一页占用的时间都相同。请通过页面上放多少内容来调节阅读速度。省略 `time` 时，页面的速度与原版制作人员名单相同，文字越多，耗时自然越长。

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
| 标题 | 行首的 `# `、`## `、`### ` | 加粗并放大：分别为文字大小的两倍、一点五倍和一点二五倍，对齐方式与正文相同 |
| 粗体 | `**text**` | 字体的粗体字形 |
| 斜体 | `*text*` | 字体的斜体字形 |
| 粗斜体 | `***text***` | 倾斜的粗体字形 |
| 删除线 | `~~text~~` | 带删除线 |
| 代码 | `` `text` `` | 染成青色 |
| 链接 | `[text](url)` | 只显示文字，带下划线；不可点击 |
| 符文 | `{runic}text{/runic}` | 文字以符文密码 `resourcedatapackloader:rdpl_runic` 显示，该行其余部分保持原字体；其中的粗体和斜体采用该密码字体的粗体和斜体字形。标题、列表项和引用中均可使用，未闭合的 `{runic}` 按原样显示 |
| 项目符号 | 行首的 `- ` 或 `* ` | 显示为圆点，换行后的文字缩进对齐到文本下方；标记前加两个空格即嵌套一级 |
| 编号 | 行首的 `1. ` | 按所写的数字显示，缩进方式相同 |
| 引用 | 行首的 `> ` | 缩进并变暗 |
| 分隔线 | 单独一行的 `---` | 横跨文本宽度的水平线 |
| 图片 | 单独一行的 `![alt](namespace:textures/....png)` | 显示图片，缩小到文本宽度并保持比例；若无法读取则显示替代文字 |
| 转义 | 标记前加 `\`，例如 `\*` | 该标记作为普通字符显示 |

表格和围栏代码块（位于 ``` 行之间）按纯文本绘制，标记原样保留。滚动页面的时间推算与静止页面的缩放适配，都以排版后的高度为准，图片也计算在内。卡片的标题与各行、Says 消息以及欢迎与保持提示，支持从粗体到符文的行内标记，每条一行。

### 玩法

*世界介绍*

滚动页面在时间到后切换到下一页。最后一页不会自动前进，而是停下等待。底部有 **Next Page** 和 **Skip All**，最后一页则只有一个 **Continue to World**。按 Esc 与 Skip All 的效果相同。静态页面的每一行都居中。滚动页面则像制作人员名单一样保持固定的列宽。

在单人游戏中，世界会在介绍背后暂停，因此玩家阅读时不会有东西悄悄逼近。唯一的例外是介绍打开时仍在生成的陆地：此时生成会在页面背后继续进行，玩家保持旁观者状态，直到继续进入世界，即使生成已先行完成也是如此。在服务器上，世界继续运行，而原版客户端根本不会看到介绍，照常加入。欢迎问候会等到页面关闭后才出现，因此不会被页面遮住而错过。

`once` 记录在玩家的存档数据中，死亡后依然有效。`/rdplserver intro` 会为执行者清除该记录，因此他们下次加入时介绍会再次播放。它不会当场重放，以免成为在游戏进行中回到入场流程的途径。

背景会拉伸以填满窗口，因此 16:9 的图片适合 16:9 的窗口，而正方形图片看起来会被压扁。请按所需形状裁剪图片，而不要依赖自动适配。`music` 接受任何已注册的音效事件，无论是原版的，还是你自己的资源包通过 `sounds` 添加的。它不会循环，所以较短的曲目播完后就是一片寂静。

如果有多个资源包都提供了介绍，它们的页面会按资源包顺序首尾相接地播放，而不是只让其中一个生效。如果只想要一个，请用 `requires` 加以限制。

## 队伍

*游戏模式*

`<namespace>/teams/*.json`

文件名由你自行决定，只读取该文件夹，多个文件会叠加。每个文件是一个阵营。

一个阵营就是游戏自带计分板上的真实队伍，因此 `/scoreboard teams list` 能看到它，它的成员在存档和重新加载后依然保留，而没有安装本模组的客户端也会像对待任何原版队伍一样显示颜色和名牌。成员资格按名称计算，因此任何有名称或 UUID 的对象都可以加入阵营：玩家、僵尸、村民、盔甲架。

只有资源包要求时才会设立阵营：任何位置都没有 `teams` 文件夹时，本模组不会添加队伍，不监听任何事件，也不提供该命令。编辑了文件的服务器管理员可以运行 `/rdpl reload`，在不重启的情况下把改动应用到正在运行的世界。

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
| `name` | 文本 | 文件名 | 队伍在计分板上的名称，1 到 16 个字符。`/scoreboard` 和其他文件使用的就是它 |
| `displayName` | 文本 | 名称 | 向玩家显示的内容，用以代替名称 |
| `color` | 文本 | `white` | 十六种文本颜色之一。它为名牌着色，也是每队侧边栏栏位所依据的键 |
| `prefix` | 文本 | 空 | 放在成员名字前面，位于颜色之后 |
| `suffix` | 文本 | 空 | 放在成员名字后面 |
| `scoreboard` | 布尔值 | `true` | 该阵营是否作为游戏计分板上的队伍存在。关闭则完全不设立队伍：其生物改为在名字中带上阵营的颜色，没有任何机制阻止它们互相战斗，也不会有积分记到它头上，因为计分是按队伍进行的 |

### 战斗与可见度

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `friendlyFire` | 布尔值 | `false` | 成员之间是否可以互相伤害。同时也是 `mobFriendlyFire` 的默认值 |
| `mobFriendlyFire` | 布尔值 | `friendlyFire` | 阵营的生物是否会被己方的爆炸和投掷的 TNT 伤到，单靠游戏本身永远不会阻止这种情况。关闭则保护己方；开启则保持游戏原有的行为 |
| `seeFriendlyInvisibles` | 布尔值 | `true` | 成员隐身时彼此是否能看见 |
| `nameTags` | 文本 | `always` | `always`、`never`、`hideForOtherTeams` 或 `hideForOwnTeam` |
| `deathMessages` | 文本 | `always` | 同样的四个词，决定成员死亡时通知哪些人 |
| `collision` | 文本 | `always` | `always`、`never`、`pushOtherTeams` 或 `pushOwnTeam` |

### 谁会加入

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `entities` | 列表 | 空 | 实体 ID，其每一次生成都会加入该阵营，例如 `minecraft:zombie` 或你自己的实体 |
| `players` | 列表 | 空 | 玩家名称，登录时加入该阵营 |
| `spawnBox` | 列表 | 无 | 六个整数，x y z 到 x y z。在其内部生成的对象都会加入，两个角点的先后顺序可以任意 |
| `joinable` | 布尔值 | `true` | 玩家是否可以用 `/rdpl team join` 加入。对于只供生物使用的阵营，请设为 false |
| `balance` | 布尔值 | `false` | 不带名称的 `/rdpl team join` 是否可以把玩家放到这里。在允许的阵营中，会选择玩家最少的那个 |
| `picks` | 数字 | `0` | 该阵营随机抽取多少名成员。每当一个回合开启时，阵营会让上一轮抽到的成员回到原来的位置，并从 `picksFrom` 所列的全部对象中重新抽取；两次抽取之间，来自该范围的登录或生成会立即填补空缺的席位。它的用途是：从所有人中选出一名玩家，单独成为一方 |
| `picksFrom` | 列表 | 空 | 抽取的范围：`players` 代表所有在线玩家，实体 ID 代表该种类的每一只存活生物 |
| `standIn` | 对象 | 无 | 在阵营中没有玩家时代为占位的生物：`{ "entity": "mypack:herobrine", "at": "23,31,0" }` 会在主世界的该位置保持一个该实体存活，缺失时重新召唤，并在有玩家加入该阵营的瞬间将其移除，因此在玩家接手这个角色之前，游戏是与 AI 对战的。每五秒检查一次；该位置必须位于已加载的地面。在带有大厅（`opens.by: leader`）的游戏中，替身只会在大厅等待期间以及回合开启时召唤，因此倒下的替身在本回合余下的时间及回合结束阶段都不会再出现，直到所有人回到大厅；没有大厅时，在以 `ends.lastStanding` 结束的回合进行期间，倒下的替身不会被补充 |

加入的方式有三种，一个阵营可以同时使用。`entities` 指定实体 ID，该类型的任何对象在生成时就会加入，资源包无需改动生物本身就能让它们分属各方。`spawnBox` 划出世界的一角，在其中生成的任何对象都会加入，适合双方使用同一种生物的竞技场。`players` 直接指定玩家。除此之外，除非阵营把 `joinable` 设为 false，玩家都可以用 `/rdpl team join <name>` 加入，并用 `/rdpl team leave` 离开。

### 初始装备与出生点

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `gives` | 列表 | 空 | 玩家加入阵营时放入其物品栏的物品，单个物品写物品名称，需要更多选项时写 `{ "item", "count", "unbreakable" }`，其中 `unbreakable` 用于永不磨损的物品；放入任意空闲栏位，没有空位时掉落在其脚边。在清空物品栏的重置（`resetClearsInventory`）之后会再次发放 |
| `spawn` | 文本 | 无 | 主世界中的 `x,y,z`，回合开启时阵营的玩家会被放到这里，使各阵营从各自的地盘出发；没有它，玩家会留在重置或大厅让他们所在的位置 |

### 主导者

*队伍*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `lead` | 文本 | `none` | 阵营主导者的选定方式：`none`；`first` 指在线成员中最早加入阵营的人，因此在有人离开时它会按加入顺序依次传递，原来的人回来后会再回到他手中；他们到场时会收到通知，过了介绍和任何保持之后，以及主导权传给他们时也会通知；`topScore` 指在 `leadOn` 所指目标上得分最高的人；`appointed` 指 `leadIs` 所指的玩家；`vote` 指由成员投票选出的人；`claim` 指最先认领的人。主导者只是一个标签和一种颜色，除此之外别无其他：它不赋予任何权力，因此主导者下线不会造成任何问题 |
| `leadOn` | 文本 | 空 | 与 `topScore` 配合使用，成员按其排名的目标。每次读取时都会重新计算，因此会跟随分数变化 |
| `leadIs` | 文本 | 空 | 与 `appointed` 配合使用，担任主导者的玩家 |
| `leadSays` | 文本 | `You are the current round leader` | 主导权来到某个玩家身上时告知他：在他到达自己所主导的阵营时、在他认领时，或在 `first` 主导权传给他时，此时它会带上离开者的信息。`{side}` 是阵营的显示名称；留空则不通知 |
| `leadRuns` | 文本 | 空 | 一个函数，`namespace:path`，每当主导权传给某个玩家时运行一次：首任主导者，以及此后每一次交接。它以主导者的身份、在其位置上运行，权限与进度奖励的函数相同，因此 `@s` 就是主导者。每秒检查一次；下线的主导者会在他下次上线时补运行。重启后会重新决定主导者 |

## 计分

*游戏模式*

`<namespace>/scoring/*.json`

文件名由你自行决定，只读取该文件夹，多个文件会叠加。每个文件是一个目标。

一个目标就是游戏自带计分板上的真实目标，因此 `/scoreboard players list` 能读取它，它的分数在存档后依然保留。`criterion` 是游戏自身统计的依据：`dummy` 表示只有本资源包才会改动的分数，也可以是 `deathCount`、`playerKillCount`、`totalKillCount`、`health`，或游戏所认识的任何 `stat.` 或 `achievement.` 名称。

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

### 目标

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `name` | 文本 | 文件名 | 目标在计分板上的名称，1 到 16 个字符 |
| `displayName` | 文本 | 名称 | 向玩家显示的内容，用以代替名称 |
| `criterion` | 文本 | `dummy` | 游戏自身统计的依据。未知的依据会被拒绝，并输出一行说明 |
| `display` | 文本 | 空 | `sidebar`、`list`、`belowName` 或 `sidebar.team.<color>`。留空则不在任何位置显示；没有可打开的计分板界面 |
| `render` | 文本 | 依据本身的设置 | `integer` 或 `hearts` |
| `teamTotals` | 布尔值 | `true` | 积分记到以成员所在队伍命名的一行上 |
| `individuals` | 布尔值 | `false` | 积分同时也记到成员本人的一行上 |
| `carries` | 布尔值 | `false` | 该目标在地图重置后保留，而不是随之清除。记录回合胜场的比赛总分就是一例 |
| `awardsTo` | 文本 | 空 | 另一个目标，本目标结束时向它给领先的一方记一分。平局的战况不会给任何一方记分 |
| `tiebreak` | 布尔值 | `false` | 以并列第一结束的回合会用世界的随机数从并列各方中抽出一方，把抽签写入日志，并照常给它记分。比赛（没有 `awardsTo` 的目标）也同样抽签，并在结果顶部写出抽中的一方 |

### 积分

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `points.kill` | 对象 | 空 | 实体 ID 对应积分，记到击杀者所在的阵营。`minecraft:player` 对应击杀玩家的得分 |
| `points.death` | 整数 | `0` | 成员每次死亡时的积分，无论死因如何。可以为负数 |
| `points.ownKill` | 整数 | `0` | 击杀者击杀自己阵营成员的积分，取代 `kill` 的值。0 表示此类击杀不计分；负数则是惩罚 |

`points` 是本模组在游戏统计之上添加的部分，它被送入同一个目标，因此 `/scoreboard` 依然能读取。`kill` 表示击杀每个实体 ID 值多少分，记到击杀者的阵营；`death` 表示阵营成员每次死亡值多少分，可以为负数。启用 `teamTotals` 时，积分记到以队伍命名的一行上，这使侧边栏能显示四个阵营，而不是为每只生物各占一行。`individuals` 还会为每个成员添加一行，默认关闭，因为每个生物 UUID 一行读起来只是杂音。

### 回合如何结束

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `ends.atScore` | 整数 | `0` | 某一阵营达到此分数的瞬间，比赛结束。0 表示永远不会因得分而结束 |
| `ends.afterMinutes` | 整数 | `0` | 经过这么多分钟后比赛结束。0 表示永远不会因时间而结束 |
| `ends.afterRounds` | 整数 | `0` | 用于被另一个目标 `awardsTo` 的目标：累计颁出这么多个回合后比赛结束，无论是谁赢得的。0 表示永远不会因回合数而结束 |
| `ends.lastStanding` | 布尔值 | `false` | 当只剩下一个阵营仍然屹立时，回合结束。参与的阵营是回合开启时有玩家或存活生物的那些，至少两个；死亡的玩家出局，在回合结束前作为旁观者，而玩家全部出局或离开、生物全部死亡的阵营即告覆灭。最后屹立的阵营赢得该回合，`awardsTo` 无论分数如何都会为该阵营记录。在配合 `resets` 和 `opens.by: leader` 时，游戏随后回到大厅。此类回合进行期间，阵营的 `standIn` 不会再次召唤 |
| `ends.outSays` | 文本 | `You are out until the round ends` | 告知被淘汰的玩家的内容。留空则不通知 |
| `ends.locksTeams` | 布尔值 | `true` | 回合进行期间加入阵营要等到回合结束，因此没有人能在计分回合中途闯入 |

`ends` 结束比赛，要么在某个阵营达到 `atScore` 的瞬间，要么在经过 `afterMinutes` 之后。随后会显示战况，由游戏自身排名：以聊天文字显示，若 `results` 要求则以卡片显示。没有安装本模组的玩家会以聊天行的形式收到同样的战况，因此没有人会得不到结果。配合 `resets` 时，这一结束就是一个回合的结束：战况停留 `intermissionSeconds` 秒，同时动作栏上倒数冷却时间，地图重置为欢迎状态，之后经过五秒倒数，下一个回合开启。`awardsTo` 把该回合判给领先的一方，记在跨重置 `carries` 的目标上。被保留的目标可以自行结束，用 `atScore` 实现多局决胜，用 `afterRounds` 实现固定回合数，其战况会在之后的重置时清除，于是新的比赛开始。

### 回合之间

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `ends.resets` | 布尔值 | `false` | 回合结束时重置地图，如 `resetSays` 和其他重置设置所述，然后开启新的回合 |
| `ends.intermissionSeconds` | 整数 | `10` | 从结束到重置之间，战况停留多久 |
| `ends.intermissionSays` | 文本 | `Round cooldown {seconds}` | 回合结束后的休整期间，每秒显示在动作栏上，`{seconds}` 倒数至重置。留空则不显示 |
| `ends.startsSays` | 文本 | `Round starting in {seconds}` | 重置后开启下一回合的五秒倒数期间显示在动作栏上，`{seconds}` 倒数。留空则不显示 |

### 大厅

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `opens.by` | 文本 | `auto` | `auto` 在重置五秒后自动开启下一个回合。`leader` 则改为让游戏停在大厅：重置之后以及世界首次加载时，不计任何分，不运行任何计时，可以自由加入和离开阵营，只有当某个阵营的主导者或管理员运行 `/rdpl round start` 时回合才会开启，且在还有人正在阅读世界介绍时不会开启；随后五秒倒数开始，进行抽取，每个阵营被放到它的 `spawn`。在世界等待期间，直到五秒倒数结束，玩家留在原地，不能破坏、放置、使用、攻击或丢弃任何东西，也不受伤害，尝试时会看到等待提示，其他所有生物也都静止不动：没有 AI，没有移动。命令仍然有效，因此仍可加入阵营并开始回合 |
| `opens.says` | 文本 | `Waiting for {leader} to start the round` | 像欢迎语一样闪现在屏幕中央，发给每个不是主导者的玩家：他们越过介绍到达大厅、且欢迎语已显示之后；回合结束后大厅再次开启时；每当主导者的到来或离开使其变化时；以及他们尝试大厅所拒绝的操作时。`{leader}` 是各阵营的主导者，没有人主导时则是 `a leader`。留空则不显示 |
| `opens.leaderSays` | 文本 | `Type /rdpl round start` | 以同样的方式、在同样的时刻闪现给主导某个阵营的玩家，取代 `opens.says`。留空则不显示 |
| `opens.lobby` | 文本 | 无 | 主世界中的 `x,y,z`，或另一个世界中的 `dimension:x,y,z`，例如 `-1:0,64,0`，在大厅保持期间所有人在此等待：每个玩家以及阵营中每只存活的生物，都被安排站在该点周围的一个圆环上，各自面朝圆心，于是它们站着彼此对视。每个对象分得的弧长等于其自身宽度加两格，因此互不重叠，随着更多对象到来，圆环会扩大；每当有人加入或离开，圆环会重新排布。高度取其所站的地面，在上下三格范围内查找。玩家和生物直接进入该世界并返回，无需建造传送门。回合开启时玩家前往其阵营的 `spawn`，仍然站着的生物则被放回原位，回到它自己的世界 |
| `opens.lobbyJoins` | 布尔值 | `false` | 回合中途登录的玩家，不放在其登出的位置，而是放到大厅作为旁观者，直到回合结束。需要 `opens.lobby` |
| `opens.joinsSays` | 文本 | `Round is in progress, you can join after it ends` | 告知他们的内容。留空则不通知 |

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
| `reset.lead` | 文本 | `none` | 回合进行期间，`/rdpl round reset` 对阵营主导者的作用。`now` 立即结束回合并重置地图；`vote` 改为发起投票；`none` 不赋予主导者任何单独的决定权，因此在 `players` 允许的情况下，主导者和其他玩家一样发起投票。管理员始终立即重置 |
| `reset.players` | 文本 | `none` | `vote` 允许任何阵营的玩家用 `/rdpl round reset` 发起投票。`none` 则把重置留给主导者 |
| `reset.teams` | 列表 | 空 | 其玩家可以发起投票的阵营。留空表示所有阵营 |
| `reset.passPercent` | 整数 | `51` | 必须投赞成票才能重置回合的投票者比例，1 到 100。`51` 是过半数，`100` 是所有人 |
| `reset.voteSeconds` | 整数 | `30` | 投票持续多久，最少五秒。结果一旦确定就会提前结束 |
| `reset.cooldownSeconds` | 整数 | `60` | 投票失败后，要隔多久才能再次发起。使用 `now` 的主导者不受其限制 |
| `reset.leadSays` | 文本 | `{player} reset the round` | 回合被立即重置时告知所有人，`{player}` 是执行重置的人。留空则不通知 |
| `reset.voteSays` | 文本 | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | 发起投票时告知所有人，`{player}` 是发起的人。留空则不通知 |
| `reset.tallySays` | 文本 | `Reset the round? {yes} yes, {no} no, {seconds}` | 投票期间每秒显示在动作栏上，`{seconds}` 倒数。留空则不显示 |
| `reset.passSays` | 文本 | `The vote passed, so the round is reset` | 投票通过时告知所有人。留空则不通知 |
| `reset.failSays` | 文本 | `The vote failed, so the round goes on` | 投票失败时告知所有人。留空则不通知 |

重置会在回合进行到的位置将其截断。战况会显示在 `The round was reset` 之下，没有人获得该回合，休整期开始倒数，地图重置的方式就如同回合以 `ends.resets` 结束一样，在 `opens.by` 为 `leader` 的情况下回到大厅。无论回合自己是否会结束，重置都能生效，但在大厅中、开启回合的倒数期间，或回合已结束且其重置正在进行时则不行；此时仍在进行的投票会被丢弃。

每个在阵营中的在线玩家，无论属于哪个阵营，都可以用 `/rdpl round vote yes` 或 `no` 投票，并可以在投票进行期间更改选择。发起投票的人视为已投赞成票，时间到时尚未投票的玩家按反对计。在设有阵营的资源包中，不属于任何阵营的玩家既不能发起也不能投票；在没有阵营的资源包中，每个在线玩家都可以。使用的是第一个其 `reset` 允许任何人重置的计分文件。

### 结果

*计分*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `results.card` | 布尔值 | `false` | 以卡片而不是聊天文字显示战况 |
| `results.title` | 文本 | 名称加 `results` | 卡片的标题 |
| `results.icon` | 文本 | 空 | 绘制在卡片上的物品，例如 `minecraft:tnt` |
| `results.image` | 文本 | 空 | 绘制在卡片上的图片，取代物品 |
| `results.background` | 文本 | 深石板色 | 卡片的背景颜色 |
| `results.seconds` | 整数 | `8` | 卡片停留多久，至少一秒 |

### 轮流行动

*计分*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `turns.order` | 文本 | `fixed` | 谁在何时行动，在每一圈开始时决定。`fixed` 保持各阵营首次出现的顺序，`random` 每一圈重新打乱，`lowestFirst` 从此目标上分数最低的阵营开始，`lastWinner` 从赢得上一回合的阵营开始，其余按固定顺序 |
| `turns.seconds` | 整数 | `60` | 每次行动持续多久，至少一秒 |
| `turns.gapSeconds` | 整数 | `3` | 两次行动之间的停顿，期间所有阵营都在等待。0 表示直接继续 |
| `turns.held` | 文本 | `frozen` | 玩家如何等待轮到自己：`frozen` 让其站着不动，并像大厅那样不让其攻击、挖掘、建造、使用或丢弃物品；`spectator` 或 `adventure` 把其切换到该游戏模式，轮到自己时再换回原来的模式 |
| `turns.endsAtScore` | 整数 | `0` | 一旦有阵营在此目标上达到这个分数，轮流行动就结束，这一回合也随之结束。0 表示从不因分数结束 |
| `turns.cycles` | 整数 | `0` | 这么多圈之后轮流行动结束，这一回合也随之结束；一圈就是在场的每个阵营各行动一次。0 表示一直进行到别的条件结束这一回合 |
| `turns.mobTags` | 列表 | 空 | 让不在队伍中的生物加入轮流行动的计分板标签，每个标签一组 |
| `turns.mobTypes` | 列表 | 空 | 让不在队伍中的生物加入轮流行动的实体 ID，每个 ID 一组 |

`turns` 让各阵营在一回合进行时轮流行动。每个队伍是一个阵营；没有队伍时每个玩家各自为一个阵营，队伍中的生物随队伍行动。一个阵营行动时，其他阵营都要等待：玩家按 `held` 的设定等待，生物则像在大厅里那样被定住。没有人在场、或所有玩家都已出局的阵营会被跳过。

每次行动都会在聊天栏中宣布，计时在动作栏上倒数。正在行动的阵营会在剩 10 秒时在聊天栏中收到带提示音的警告，剩 3 秒时再提醒一次。`/rdplserver game pass`，或用带 `passesTurn` 的物品右键，会提前结束行动：玩家只能结束自己阵营的行动，命令方块或控制台则结束当前正在行动的阵营。

因 `endsAtScore` 或 `cycles` 结束时，这一回合会像其他结束方式一样结束：显示战况，`awardsTo` 和 `tiebreak` 生效，`ends.resets` 会重置地图。只有第一个带 `turns` 的计分文件会轮流行动。相关文字是 `turn`、`turnclock`、`turnwarn`、`turnout`、`turnpass`、`turngap`、`notturn` 和 `noturns` 这些键，骰子文件的 `says` 可以修改它们。

## 袭击

*游戏模式*

`<namespace>/raids/*.json`

文件名由你自行决定，只读取该文件夹，多个文件会叠加。每个文件是一场袭击。

这里的袭击就是游戏从 1.14 起拥有的那种，建立在 1.12.2 已经维护的村庄之上。当携带 `omen` 效果的玩家位于村庄内时，袭击开始：该效果被移除，并为村庄中心 `reach` 范围内的每个玩家显示一条 Boss 血条。经过 `waveDelay` 刻后，第一波在村庄周围的一个圆环上出现，朝中心走去，沿途攻击玩家、村民和铁傀儡。袭击者之间绝不会互相伤害或以彼此为目标，因此它们之间的流矢或打击不起作用。血条显示该波剩余的生命值，并在剩下两个或更少的袭击者时开始计数。一波结束后，下一波等待 `waveDelay` 刻。当最后一波结束，且两秒内没有新的袭击者出现时，袭击获胜；当一波到来之后所有村民都已死亡，或村庄本身消失，则袭击失败。无论哪种结果，血条都会显示结果三十秒，并以范围内的每个玩家的身份运行对应的函数。

进行中的袭击会随世界一起保存，重新加载后其袭击者会继续行进。在和平难度下、经过 `timeout` 刻后，或村庄周围没有任何位置能容纳一波袭击者时，袭击会不带结局地停止。村庄只有在有村民找到它的门之后才算数，因此袭击需要一座游戏已经注意到的村庄。

一波袭击进行期间，村庄的村民会跑进室内，前往村庄已知的最近的门并留在那里。袭击者会拆毁沿途的木门来接近他们，每扇门需要十二秒，仅在普通和困难难度下且 `mobGriefing` 开启时才会这样；铁门则能挡住。类型为 `bell` 的方块无论位于何处都是村庄的钟，其鸣响方式见 [钟](#钟)；袭击的 `bell` 可指定任何其他方块也像钟一样鸣响。一波到来时，村庄中的每一口钟都会响起，而指定的方块在玩家使用时也会响：48 格内的村民躲藏十五秒，48 格内的袭击者发光三秒。不会自动生成任何钟。想要钟的资源包需要自己定义该方块，把它放进 NBT 结构，并把该结构放到村庄中，作为地块或水井的替代物，这样钟就立在村民居住的地方。

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

### 袭击

*袭击*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `omen` | 效果名称 | 无，必填 | 当持有者位于村庄内时触发袭击的效果。任何已注册的效果都可以，包括资源包自己的药水 |
| `name` | 文本 | `Raid` | Boss 血条的标题 |
| `color` | 文本 | `red` | 血条的颜色：`pink`、`blue`、`red`、`green`、`yellow`、`purple` 或 `white` |
| `waves` | 波次列表 | 无，必填 | 每一波是一个分组列表，各波按顺序出现 |
| `waveDelay` | 整数 | `300` | 第一波之前，以及一波结束到下一波之间的刻数 |
| `spawnDistance` | 整数 | `32` | 一波在距村庄中心多远处出现。最初的尝试位于此距离的两倍处，其次是此距离处，最后是村庄内部 |
| `reach` | 整数 | `96` | 中心此格数范围内的玩家能看到血条，结局函数以他们的身份运行。游荡到超出该范围十六格的袭击者会脱离袭击 |
| `timeout` | 整数 | `48000` | 未完成的袭击在经过这么多刻后不带结局地停止。`0` 表示永不停止 |
| `sound` | 音效名称 | 无 | 每一波到来时，从该波来袭的方向播放给范围内的每个玩家 |
| `wins` | 函数 | 无 | 袭击获胜时，以范围内的每个玩家的身份运行 |
| `loses` | 函数 | 无 | 袭击失败时，以范围内的每个玩家的身份运行 |
| `bell` | 方块名称或列表 | 无 | 其他像钟一样鸣响的方块，在玩家使用它们以及每一波到来时都会响。类型为 `bell` 的方块无需指定也会响。请通过 NBT 结构把它放进村庄，因为不会自动生成 |

### 分组

*袭击*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `entity` | 实体名称 | 无，必填 | 来袭的对象。实体变种保留其自身的所有行为，并获得行进能力 |
| `count` | 整数或 `{ "min", "max" }` | `1` | 来多少个 |

---

## 卡片

*游戏模式*

`<namespace>/cards/*.json`

文件名由你自行决定，只读取该文件夹，多个文件会叠加。每个文件是一条规则，其 ID 为 `<namespace>:<file name>`。规则等待触发器，检查其 `when`，并向其受众显示一张卡片；它还可以运行一个函数。客户端无需安装任何东西：没有安装本模组的玩家，角落卡片会收到聊天行，居中卡片会收到标题。

本模组自己说出的每条消息都是一条内置规则，列在下文。资源包可以通过编写具有该 ID 的文件 `rdpl/cards/<name>.json` 来修改其中一条，该文件不需要触发器：它省略的内容保持现状，而 `{text}` 代表本模组原本会说的那条消息。没有编写其中任何一条的资源包，所有消息都和以前一样。

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

第二个文件保存为 `rdpl/cards/gate_blocked.json`，它把关闭的传送门显示的红色动作栏文字变成带图标和第二行的卡片，并且最多每三十秒显示一次。

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

第三个在玩家首次加入时，以一张居中卡片问候他，这张卡片背后没有面板，只有文字及其阴影，用资源包自己的字体绘制。

### 触发器

*卡片*

| 触发器 | 需要 | 触发时机 |
| --- | --- | --- |
| `command` | 无 | 运行 `/rdplserver card <rule> [players]` 时。该命令会跳过 `when`、`repeat` 和 `cooldown`，但仍会运行 `runs`。任何规则都可以这样显示，无论其触发器是什么 |
| `first_join` | 无 | 玩家第一次加入世界时 |
| `dimension_enter` | `dimension` | 玩家到达该维度时 |
| `biome_enter` | `biomes` | 玩家从别处走进其中一个生物群系时 |
| `structure_enter` | `structures` | 玩家从外面走进其中一个结构时 |
| `advancement` | `advancement` | 玩家获得该进度时 |
| `time_of_day` | `time` | 当玩家处于该维度时，日间时钟经过该刻，`0` 到 `23999`。通过命令或床设置的时钟不计入 |
| `day` | 无，或 `day` | 该维度中新的一天开始时；带 `day` 时，仅限那一天 |
| `craft` | `item` | 玩家合成该物品时 |
| `pickup` | `item` | 玩家拾取该物品时 |
| `kill` | `entity` | 玩家击杀该实体，或击杀其中第 `count` 个时 |
| `respawn` | 无 | 玩家死亡后重生时 |
| `death` | 无 | 玩家死亡时 |
| `y_level` | `below` 或 `above` | 玩家低于或高于该高度时 |
| `play_time` | `minutes` | 玩家在世界中的时间达到这么多分钟时，从世界介绍关闭起计算；若没有向其显示介绍，则从加入时起计算 |
| `score` | `objective` | 玩家在该目标中的分数达到 `score` 时 |

生物群系、结构、高度、游戏时间和分数每秒为每个玩家检查一次，并在从外部变为内部时触发，加入后的第一次检查绝不会触发。受众不是 `player` 的 `time_of_day` 或 `day` 规则，会为该维度触发一次，而不是为其中的每个玩家各触发一次。

如果卡片在玩家仍开着世界介绍时触发，它会等待，并在介绍关闭时显示，无论其触发器是什么，包括 `command` 触发器。如果玩家在那之前离开，它会被丢弃。

### 触发器设置

*卡片*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `trigger` | 文本 | 无，必填 | 上述触发器之一。内置规则不需要 |
| `dimension` | 文本 | 无 | 维度 ID，例如 `-1`，或其名称，例如 `the_nether`。对 `dimension_enter` 来说它是进入的那个维度；对其他所有触发器来说，它把规则限制在该维度中的玩家 |
| `biomes` | 列表 | 无 | 生物群系名称，例如 `minecraft:desert`，或 `#TYPE` 表示 Forge 生物群系类型，例如 `#SNOWY` |
| `structures` | 列表 | 无 | `Village`、`Temple`、`Mansion`、`Monument`、`Mineshaft`、`Stronghold`、`Fortress` 或 `EndCity`，或资源包通过 `structures` 放置的某个结构的名称，在其放置位置的 `radius` 范围内都算在内 |
| `radius` | 整数 | `32` | 离资源包自己的结构多近才算在内部 |
| `advancement` | 文本 | 无 | 进度 ID |
| `item` | 文本 | 无 | 物品，写法与资源包中其他位置相同，例如 `minecraft:diamond_sword` |
| `entity` | 文本 | 无 | 实体 ID，例如 `minecraft:zombie` |
| `count` | 整数 | `1` | 用于 `kill`：需要击杀多少次。规则触发后计数重新开始 |
| `below`、`above` | 整数 | 无 | 用于 `y_level`：要低于或高于的高度 |
| `time` | 整数 | `0` | 用于 `time_of_day`：一天中的刻 |
| `day` | 整数 | 无 | 用于 `day`：触发的那一天。没有它则每天都触发 |
| `minutes` | 整数 | 无 | 用于 `play_time` |
| `objective`、`score` | 文本、整数 | 无、`1` | 用于 `score`：目标和要达到的值 |
| `requires` | 模组 ID 或资源包命名空间的列表 | 无 | 除非全部存在，否则跳过该文件 |

### 时机

*卡片*

`when` 包含的条件必须在触发器触发的那一刻全部成立。

| 设置 | 类型 | 检查内容 |
| --- | --- | --- |
| `biomes` | 列表 | 玩家位于其中一个生物群系，写法与触发器相同 |
| `timeFrom`、`timeTo` | 整数 | 日间时钟处于此时间窗口内，窗口可以跨过午夜，例如 `13000` 到 `1000` |
| `dayAtLeast` | 整数 | 天数至少为此值 |
| `advancement` | 文本 | 玩家已获得此进度 |
| `gameMode` | 文本 | 玩家处于此游戏模式：`survival`、`creative`、`adventure` 或 `spectator` |
| `team` | 文本 | 玩家在此计分板队伍中 |
| `objective`、`scoreAtLeast` | 文本、整数 | 玩家在该目标中的分数至少为此值 |

### 卡片

*卡片*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `title` | 文本 | 无 | 第一行，在居中卡片上绘制得更大 |
| `lines` | 列表 | 无 | 最多十六行。除内置规则外，规则需要标题或行。`{player}`、`{dim}`、`{biome}` 和 `{day}` 会被填入；`{text}` 是内置消息，单独占一行时会给出它的所有行 |
| `style` | 文本 | `corner` | `corner` 是 `saysCard` 所显示的右下角卡片；`center` 是屏幕中央的卡片；`chat` 是聊天行；`bar` 是动作栏 |
| `icon` | 文本 | `saysIcon` | 绘制在角落卡片上的物品。留空则不绘制 |
| `color` | 文本 | `saysColor` | 卡片的背景颜色，十六进制 |
| `image` | 文本 | `saysImage` | 来自资源包客户端资源的 PNG，拉伸铺满卡片作为其背景 |
| `background` | 布尔值 | `saysBackground` | `false` 去掉面板、边框和彩色条纹；文字保留阴影，`image` 仍会绘制 |
| `font` | 文本 | `saysFont` | 卡片文字所用的字体，写作 `namespace:name`。留空则使用 RDPL 字体 |
| `ticks` | 整数 | `160` | 卡片停留多久，包括淡出 |
| `audience` | 文本 | `player` | 谁能看到：`player`、`everyone`、`dimension`（玩家所在维度中的所有人）或 `team`（玩家所在的计分板队伍） |
| `repeat` | 文本 | `always` | `always`、`once_per_player`、`once_per_world` 或 `once_per_session`（玩家重新登录后再次显示） |
| `cooldown` | 整数 | `0` | 同一玩家再次触发该规则前的秒数 |
| `runs` | 文本 | 无 | 规则触发时以该玩家身份运行的函数 |

当 `saysCard` 关闭时，角落卡片会转为聊天文字。玩家已看过的内容随玩家保存，因此死亡和切换维度后依然有效；`once_per_world` 则随世界保存。

RDPL 字体 `resourcedatapackloader:rdpl` 是所有文本的默认字体：卡片、Says 消息、欢迎与保持提示、世界介绍，以及游戏自身的菜单、聊天、HUD 和工具提示。它的粗体和斜体字形是 `resourcedatapackloader:rdpl_bold` 和 `resourcedatapackloader:rdpl_italic`。附魔台的文字保持游戏原样。

RDPL 附带下列字体和字符。卡片、提示或介绍的 `font` 可以用简称或完整 ID 指定 RDPL 字体：

| 名称 | 绘制内容 |
| --- | --- |
| `rdpl`（或 `resourcedatapackloader:rdpl`） | RDPL 字体，包含西里尔字母（U+0400 至 U+04FF）和卢恩字母表（U+16A0 至 U+16F8） |
| `rdpl_runic`（或 `resourcedatapackloader:rdpl_runic`） | 一种符文密码：字母 A 到 Z 和 a 到 z 绘制为符文，其他所有字符以 RDPL 字体绘制。粗体部分以 `rdpl_runic_bold` 绘制，斜体部分以 `rdpl_runic_italic` 绘制 |
| 符文，U+16A0 至 U+16F8 | 直接写作符文字符本身（ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ），可用于 RDPL 字体所绘制的任何文本，包括聊天；粗体和斜体部分保留其字形 |

卡片字体是位于 `assets/<namespace>/textures/font/<name>.png` 的 PNG，是一个 16 乘 16 字形的网格，排布方式与游戏自己的 `ascii.png` 相同，卡片的大小按从中读取的字形宽度确定。旁边的 `<name>_cyrillic.png` 是同样的网格，存放 Unicode U+0400 至 U+04FF，用于绘制西里尔字母；没有它时，西里尔字母来自游戏自己的字体页。`<name>_runes.png` 是同样的网格，存放 U+1600 至 U+16FF，以同样的方式绘制符文。1.20.1 和 1.21.1 版本则改为读取位于 `assets/<namespace>/font/<name>.json` 的字体定义，其 `bitmap` 提供器可以指向同一个 PNG，因此同时提供这两个文件的资源包在三个版本上绘制出相同的字样。`minecraft:default` 指游戏的字体。没有任何资源包包含的字体会回退到游戏的字体，并在 `rdpl.log` 中记录一条警告。

资源包可以通过提供自己的 `assets/resourcedatapackloader/textures/font/rdpl.png`（以及 `rdpl_bold.png`、`rdpl_italic.png` 及其 `_cyrillic.png` 和 `_runes.png` 图集）来更改 RDPL 字体，它会在所有位置取而代之，包括游戏自身的文字。提供 `assets/minecraft/textures/font/ascii.png` 的资源包，例如原版文件的副本，会让游戏自己的文字改用该字体，西里尔字母和符文则来自游戏的字体页；RDPL 自己的文字仍保持 RDPL 字体，除非 `saysFont` 为 `minecraft:default`。

卡片的标题与各行、Says 消息以及欢迎与保持提示，支持世界介绍的文本格式一节下表中的行内标记：粗体、斜体、粗斜体、删除线、代码、链接和符文片段。粗体部分以字体的 `_bold` 字形绘制，斜体部分以其 `_italic` 字形绘制；对于没有该字形的字体，该部分采用游戏的粗体或斜体样式，卡片的大小按实际绘制的内容确定。没有安装本模组的玩家会得到以聊天格式显示的同样标记，符文片段则显示为其原本的字母。

### 内置规则

*卡片*

| ID | 消息 | 其文本来源 |
| --- | --- | --- |
| `rdpl:gate_unlocked` | 传送门开启 | [传送门](#传送门) 中的 `unlockedMessage` |
| `rdpl:gate_blocked` | 关闭的传送门把玩家挡回，显示在动作栏上 | [传送门](#传送门) 中的 `blockedMessage` |
| `rdpl:team_joined` | 玩家加入一个阵营 | 该阵营的 `displayName` |
| `rdpl:team_lead` | 阵营的主导权来到某个玩家身上 | [队伍](#队伍) 中的 `leadSays` |
| `rdpl:team_picked` | 玩家被选入一个阵营 | 该阵营的 `displayName` |
| `rdpl:team_round_ended` | 回合结束，玩家被转移到一个阵营 | 该阵营的 `displayName` |
| `rdpl:lobby_joins` | 回合中途登录的玩家被送往大厅 | [大厅](#大厅) 中的 `opens.joinsSays` |
| `rdpl:lobby_note` | 屏幕中央的大厅提示行 | [大厅](#大厅) 中的 `opens.says`、`opens.leaderSays` |
| `rdpl:scoring_results` | 回合结束时发给每个玩家的战况 | [结果](#结果) 中的 `results.card`、`results.title`、`results.icon`、`results.image`、`results.background`、`results.seconds` |
| `rdpl:scoring_out` | 被淘汰的玩家 | [回合如何结束](#回合如何结束) 中的 `ends.outSays` |
| `rdpl:reset_lead` | 主导者重置回合 | [重置回合](#重置回合) 中的 `reset.leadSays` |
| `rdpl:reset_vote` | 发起重置投票 | `reset.voteSays` |
| `rdpl:reset_pass` | 投票通过 | `reset.passSays` |
| `rdpl:reset_fail` | 投票失败 | `reset.failSays` |
| `rdpl:anvil_waits` | 铁砧的操作在等待某个进度 | [铁砧操作](#铁砧操作) |
| `rdpl:threat` | 玩家的威胁等级发生变化 | `threatSays` |
| `rdpl:prospect` | 勘探发现所报告的每一行 | 发现的内容 |
| `rdpl:prospect_none` | 勘探一无所获 | 语言文件 |
| `rdpl:board_result` | 一局棋盘游戏结束 | 语言文件 |
| `rdpl:pregen_ended` | 预生成完成或停止 | [预生成](#预生成) 中的 `pregenFinishedSays`、`pregenStoppedSays` |
| `rdpl:pregen_running` | 玩家在预生成期间加入时看到的进度行 | `pregenRunningSays` |

`welcomeSays` 不是规则，保留其徽标；`first_join` 或 `dimension_enter` 规则是对它的补充。回合的动作栏倒计时和计分表保持各自设置所定的样子。

## 骰子与牌堆

*游戏模式*

`<namespace>/dice/*.json`

文件名由你决定，多个文件会合并。一个文件可以定义各面带权重的骰子、抽出后不放回的卡牌牌堆、默认谁能听到掷骰，以及结果的文字。掷骰用 [`/rdplserver game`](#游戏)，也可以用任何带 [`rolls`](#物品文件键) 的物品。

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

| 键 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `audience` | 文本 | `all` | 没有指定听众的掷骰由谁听到：`self`、`team`、`all`、`radius <格数>` 或 `silent`。以第一个设置它的资源包为准；之后的会写入日志 |
| `dice` | 对象 | 空 | 骰子名对应一个由面和权重组成的对象。权重为 2 的面出现的次数是权重为 1 的两倍。权重是从 1 起的整数 |
| `decks` | 对象 | 空 | 牌堆名对应它的卡牌列表，或对应对象 `{ "cards": [...], "reshuffle": false }`。`reshuffle` 不写时为 `true`：从空牌堆抽牌会把所有牌重新洗好再抽。设为 `false` 时，牌堆保持为空，直到执行 `game deck shuffle` |
| `says` | 对象 | 空 | 文字键对应在所有语言中替换本模组自带文字的文本。键及其占位符见下 |

骰子或牌堆的名字属于第一个加载它的资源包。另一个同名的资源包，或名为 `coin` 的，会被跳过并在日志中记一条错误。资源包骰子用 `game die <name>` 掷出并显示其面；存为分数时，按该面在文件中的位置计数，从 1 开始。

牌堆是一叠越抽越少的牌。`game deck draw <name>` 从剩余的牌中随机抽一张，在空牌堆于下一次抽牌时自动重新洗牌、或 `game deck shuffle <name>` 把所有牌放回之前，抽出的牌不会回来。牌堆随世界一起保存，所以重启不会洗牌。

每次掷骰都使用世界自身的随机数，并连同掷骰者、掷了什么和结果一起写入日志。`game last` 显示最近的几次。结果以服务器拼好的普通聊天行发出，所以没有装本模组的玩家也能看到。骰子记法由个数、`d`、面数和可选的修正值组成：`3d8-2` 表示三个八面骰子相加再减 2，`d20` 表示一个二十面骰子。聊天和日志会把掷骰写成文字，例如“Boss 掷了 3 个八面骰子，减 2：[2, 6, 7] = 13”，`{dice}` 就是这段文字。

| 文字键 | 占位符 |
| --- | --- |
| `coin`、`pickplayer`、`pickteam` | `{player}`、`{result}` |
| `heads`、`tails`、`lastnone`、`nobody`、`notallowed`、`usage` | 无 |
| `die` | `{player}`、`{dice}`、`{sides}`、`{result}` |
| `packdie` | `{player}`、`{die}`、`{result}` |
| `dice` | `{player}`、`{dice}`、`{rolls}`、`{result}` |
| `advantage`、`disadvantage` | `{player}`、`{dice}`、`{first}`、`{second}`、`{result}` |
| `pickmember` | `{player}`、`{team}`、`{result}` |
| `draw`, `reshuffled` | `{player}`、`{deck}`、`{result}`、`{left}` |
| `shuffle` | `{player}`、`{deck}`、`{left}` |
| `left`、`empty`、`nodeck` | `{deck}`，`left` 另有 `{left}` |
| `teamroll` | `{member}`、`{dice}`、`{result}` |
| `teamrollwin` | `{result}`、`{score}` |
| `tiebreak` | `{objective}`、`{sides}`、`{result}` |
| `notie`、`noobjective` | `{objective}` |
| `turn`、`turnclock`、`turnwarn` | `{group}`、`{seconds}` |
| `turnout`、`turnpass` | `{group}` |
| `turngap` | `{seconds}` |
| `notturn`、`noturns` | 无 |
| `badsides`、`badroll`、`badaudience`、`nodie`、`noteam` | 依次为 `{sides}`、`{roll}`、`{audience}`、`{name}`、`{team}` |

本模组自带的文字位于其语言文件中，键为 `rdpl.game.<key>`，所以资源包也可以按语言逐一更改它。

## 棋盘游戏

*游戏模式*

`<namespace>/games/*.json`

一个文件就是一种棋盘游戏，以文件名命名。它设定棋盘、双方、棋子及其走法、开局局面，以及结果的奖励。`game board start <game> <board>` 在发送者所站的位置或给定的位置摆出一副棋盘：格子铺在下方一格，列向东延伸，行向南延伸，每个棋子都是资源包选定的生物，静止、无声、无敌，并以所属一方命名。被吃掉的棋子站在棋盘旁边。

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

| 键 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `name` | 文本 | 文件名 | 在聊天和结果中显示的名称 |
| `board` | 对象 | 8 × 8 | `files` 和 `ranks`，各 2 到 16，以及铺在格子下面的方块状态 `light` 和 `dark`。不写时地面保持原样 |
| `sides` | 列表 | `White`、`Black` | 两个由 `name` 和 `color`（聊天颜色）组成的对象。第一方先走，用大写字母摆子 |
| `pieces` | 对象 | 无 | 棋子名称对应一个由下方棋子键组成的对象 |
| `setup` | 列表 | 无 | 每行一段文本，第 1 行在前。棋子的字母放置该棋子，第一方用大写，第二方用小写，`.` 留空该格 |
| `rules` | 对象 | 无 | `mustCapture`、`chainCaptures`、`quietDraw` 和 `repeatDraw`，见下 |
| `clock` | 对象 | 无 | 每方的 `minutes`，以及每步后加上的 `addSeconds`。用完时间的一方判负 |
| `ai` | 数字 | `2` | 电脑的等级，1 到 4，用于无人执掌的一方 |
| `result` | 对象 | 无 | 对局结束时发放的 `objective`，以及 `win`、`draw`、`loss` 分数，不写时为 1、0、0 |
| `quiet` | 真或假 | `false` | 不在聊天中发送走棋、选子和将军的消息。拒绝提示和对局结果照常发送，每一步也照常写入 `logs/rdpl.log` |

| 棋子键 | 类型 | 作用 |
| --- | --- | --- |
| `letter` | 文本 | 在 `setup` 和 `game board show` 中使用的字母 |
| `value` | 数字 | 它对电脑而言的价值 |
| `mobs` | 列表 | 每一方的生物，第一方在前。`mob` 为双方给出同一个 |
| `moves` | 列表 | 以对象表示的步法：`steps`，从本方一端看的 `[file, rank]` 偏移列表；`slides`，一直走到被挡住为止；`captures`，`both`（默认）、`never`、`only` 或 `hop`，后者跳过对方的一枚棋子落到其后的空格并吃掉它；以及 `firstRange`，首步可以走几步 |
| `royal` | 真或假 | 这枚棋子被将死时该方判负，且任何一步都不能让它处于被攻击状态。没有王棋时，无棋可走的一方判负 |
| `enPassant` | 真或假 | 可以吃掉刚从它旁边走过两格的棋子 |
| `castles` | 文本 | 一个搭档棋子：这枚棋子朝未动过的搭档走两格，搭档跳过它 |
| `promotes` | 列表 | 到达底线时可以变成什么。除非着法指定其他，否则用第一个 |

| 规则 | 作用 |
| --- | --- |
| `mustCapture` | 能吃子的一方必须吃子 |
| `chainCaptures` | 吃子的跳跃之后，同一枚棋子只要还能吃就继续吃 |
| `quietDraw` | 双方合计连续多少步没有吃子、也没有可升变棋子走动之后，对局判和。0 表示永不判和 |
| `repeatDraw` | 同一局面在同一方走棋时出现这么多次后，对局判和；3 即国际象棋的三次重复局面。只计算上次吃子或可升变棋子走动之后的局面。0 表示永不判和 |

一方属于第一个右键点击其棋子的玩家；该玩家若在计分板队伍中，则属于该队伍，之后任何成员都可以走这一方。无人执掌的一方在另一方被占后由电脑来走，或在 `game board ai` 为其设定等级时立即由电脑来走；电脑在服务器线程之外思考，着法在服务器线程上执行。右键点击一枚棋子会显示它能去哪里，再右键点击一个格子或对方的一枚棋子即可走过去。`game board move` 用格子名称做同样的事，例如 `e2 e4`，升变时在末尾加上棋子名称。当一步棋造成升变且 `promotes` 列出不止一种棋子时，玩家在聊天栏中点击选项来选择；10 秒内未选择，或玩家改为点击棋盘，则采用第一项。电脑会选择它认为最好的一项。吃子的棋子会先击打被吃的棋子，带有挥击、受伤闪红和击中音效，但不造成伤害，随后被吃的棋子移到棋盘旁属于它的一列。

`game board resign` 认输。`game board draw` 提出和棋，对方用同一命令接受；电脑在局面占优时拒绝。`game board takeback` 请求撤回请求者的上一步，对方用同一命令同意；对电脑时立即撤回。

棋盘以着法列表的形式保存在世界的存档数据中。世界加载或资源包重新加载时，局面会按这个列表重放，棋子会按它重新摆好，棋盘上任何旧的棋子都会被移除。

结束时，`result` 向每一方的执掌者在其目标中发放分数，作为玩家行或队伍行。结果会发给棋盘附近的玩家和执掌一方的玩家：若卡片文件设置了 `rdpl:board_result` 这个 id，则以该卡片显示，否则以一行聊天显示。文字位于本模组的语言文件中，键为 `rdpl.game.board.<key>`。

---

# 控制

## 控制层

*控制*

所有会阻止或改变生成的内容都被分组，每个分组在配置的 `control` 类别中有一个键，有三个取值：

| 取值 | 含义 |
| --- | --- |
| `default` | 由资源包决定。配置值作为后备 |
| `global` | 以配置为准。资源包的相应部分被忽略 |
| `off` | 该分组被完全禁用，任何资源包都无法启用 |

这些分组是 `ores`、`biomes`、`generators`、`structures`、`spawning`、`bedrock`、`voidWorld`、`recipes`、`terrain`、`entities`、`chunks`、`commands` 和 `server`。

设置按 **生物群系 → 世界模板 → 配置** 的顺序解析。世界模板的 `settings` 块使用与配置相同的键名，因此资源包设置它们的方式与你自己设置的一样：

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

当分组的控制为 `default` 时，这些设置优先；为 `global` 时它们被忽略；为 `off` 时，无论任何资源包怎么说，整个分组都不起作用。

## 各分组的作用

*控制*

### 矿石

*各分组的作用*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `blockOres` | 布尔值 | `false` | 阻止所有模组和 Minecraft 生成矿石，`oreWhitelist` 中列出的模组除外。只有经由 Forge 矿石生成事件的生成才能被干预，这包括 Minecraft 和大多数模组，但不是全部 |
| `oreWhitelist` | 模组 ID 列表 | `["minecraft"]` | 在 `blockOres` 开启期间仍被允许生成矿石的模组 |
| `oreTypes` | 矿石类型列表 | 无 | 阻止所适用的矿石类型，写作 Forge 名称，`COAL`、`IRON`。留空表示所有类型 |
| `oreTypesAreBlacklist` | 布尔值 | `true` | 开启时，`oreTypes` 中的类型就是被阻止的类型。关闭时，只有这些类型会生成 |
| `blockOreDimensions` | 整数列表 | 无 | 矿石阻止所适用的维度，留空表示所有维度。范围之外的维度完全不受影响，因此主世界保持被阻止的同时，其他模组的矿石会在那里生成 |
| `blockOreDimensionsAreBlacklist` | 布尔值 | `false` | 开启时，所列的维度是被放过的维度 |
| `prospectItems` | `item=entries` 列表 | 无 | 当潜行的玩家手持其中一件物品破坏方块时，用来勘探 `vein` 形态的世界生成条目的物品。格式见下面的段落 |
| `prospectItemsAreBlacklist` | 布尔值 | `false` | 开启时，每件物品的列表就是它不读取的条目 |
| `prospectWear` | 整数 | `2` | 一次勘探性破坏给工具造成的磨损是正常磨损的多少倍。`2`，即两倍，是允许的最小值，没有耐久度的物品不付出任何代价 |
| `prospectSlow` | 整数 | `2` | 手持带标签物品的潜行玩家破坏方块所需的时间是正常的多少倍。`1` 是正常速度 |
| `prospectDrops` | 布尔值 | `false` | 开启时，在勘探模式下破坏的方块仍会掉落物品并给予经验。关闭时，样本被消耗掉 |

**读数。** `prospectItems` 条目写作 `item=entry|entry[,radius in chunks]`，或用 `item=*[,radius]` 表示所有矿脉条目，半径默认为 8。潜行的玩家手持这样的物品破坏方块时，会针对它读取的每个条目被告知 `Possible hit on <ore> <direction> of this location, <deeper down | higher up | at about this depth>`，其中方向是从被破坏的方块指向最近的已播种矿脉的八个罗盘方位之一，绝不会给出具体坐标；当该方块已位于矿脉的范围之内时是 `at this location`，半径内没有任何已播种的矿脉时是 `No sign of anything here`。矿石的名称取自条目的 `prospectAs`，否则取自其文件名。读数会重放生成时所做的同样的随机判定，因此对尚未生成的土地也是准确的，带标签的物品会在其工具提示中说明它勘探的是什么。

### 生物群系

*各分组的作用*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `blockBiomes` | 布尔值 | `false` | 阻止所有生物群系生成，`biomeWhitelist` 中模组的生物群系除外。被阻止的生物群系会在完成的生物群系地图上被替换，这是干预海洋、蘑菇岛、恶地变种、丛林、丘陵和海岸的唯一办法。把它们全部阻止，主世界就会自行变成虚空世界 |
| `biomeWhitelist` | 模组 ID 列表 | `minecraft` | 在 `blockBiomes` 开启期间，其生物群系仍会生成的模组。资源包的生物群系使用该资源包的命名空间 |
| `biomeNames` | 生物群系名称列表 | 无 | 按名称适用的生物群系，无论归谁所有，也无论白名单怎么说。可以是 `Birch Forest` 这样的通俗名称，或注册名 |
| `biomeNamesAreBlacklist` | 布尔值 | `true` | 开启时，`biomeNames` 中的名称被阻止。关闭时，只有这些名称会生成 |
| `blockBiomeDimensions` | 整数列表 | `0`，即主世界 | 生物群系阻止所适用的维度。留空表示所有维度 |
| `blockBiomeDimensionsAreBlacklist` | 布尔值 | `false` | 开启时，阻止会跳过所列的维度。关闭时，只对它们适用 |

`blockBiomes` 和 `biomeWhitelist` 按模组起作用，`biomeNames` 与 `biomeNamesAreBlacklist` 则按名称起作用。被阻止的生物群系会在完成的生物群系地图上被替换，这是干预海洋、蘑菇岛、恶地变种、丛林、丘陵和海岸的唯一办法，它们是在模组可以编辑的列表之外选定的。把所有生物群系都阻止，主世界就会自行变成虚空世界。`blockBiomeDimensions` 把这一切限制在特定维度，留空表示所有维度，而 `blockBiomeDimensionsAreBlacklist` 把该列表变成排除项。

### 生成器

*各分组的作用*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `blockWorldGenerators` | 布尔值 | `false` | 阻止其他模组通过它们自己的世界生成器生成内容。模组正是借此添加 Forge 事件看不到的东西，例如史莱姆岛、洞穴水晶之类。本模组自身的资源包生成永远不会被阻止 |
| `generatorWhitelist` | 模组 ID 列表 | `minecraft` | 仍允许运行自身生成器的模组 |
| `blockedGenerators` | 模组 ID 或类名片段列表 | 无 | 被直接阻止的单个生成器，无论白名单如何设置 |
| `blockGeneratorDimensions` | 整数列表 | `0`，即主世界 | 此项适用的维度。留空表示所有维度 |
| `blockGeneratorDimensionsAreBlacklist` | 布尔值 | `false` | 开启时，阻止会跳过所列维度；关闭时，仅对所列维度生效 |
| `generatorTypes` | 类型列表 | 无 | 按生成器产出的内容而非所属者来阻止：`ores`、`structures`、`flora`、`lakes`、`terrain`，或用于没有任何规则匹配者的 `unknown` |
| `generatorTypesAreBlacklist` | 布尔值 | `true` | 开启时，所列类型被阻止；关闭时，只有所列类型会生成 |
| `generatorTypeMap` | `pattern=type` 列表 | 无 | 为类名无法说明用途的生成器指定类型，其中 pattern 是模组 ID 或生成器类名的一部分。映射条目先于内置关键词检查，因此也能纠正关键词判断错误的生成器 |
| `logBlockedGenerators` | 布尔值 | `true` | 在每个生成器首次被阻止时，记录该生成器及其被判定的类型。`/rdplserver generators` 按模组和类型显示累计总数 |

`blockWorldGenerators` 会阻止其他模组通过它们自己的世界生成器生成内容，模组正是借此添加 Forge 事件看不到的东西，例如史莱姆岛、洞穴水晶之类。`generatorWhitelist` 保留指定的模组，`blockedGenerators` 指定单个生成器，而本模组自身的资源包生成永远不会被阻止。`blockGeneratorDimensions` 将范围限制在特定维度，并可用 `blockGeneratorDimensionsAreBlacklist` 反转该列表。

`generatorTypes` 按生成器产出的内容而非所属模组来阻止：`ores`、`structures`、`flora`、`lakes`、`terrain`，或用于没有任何规则匹配者的 `unknown`。`generatorTypesAreBlacklist` 决定方向：开启时，所列类型被阻止；关闭时，只有所列类型会生成。无论白名单如何设置，类型都会生效，这与 `oreTypes` 的做法相同，因此你可以让所有模组都不再添加矿石，同时保留它们的地牢和树木。

类型取自生成器的类名，并与每种类型的内置关键词列表匹配。这样能正确识别大多数模组，`NetherOreGenerator` 是 ores，`SlimeIslandGenerator` 是 structures，但名称与用途毫无关联的生成器，例如 ProjectRed 的 `SimpleGenHandler` 或 Draconic Evolution 的 `DEWorldGenHandler`，会被判为 `unknown`。`generatorTypeMap` 用于手动修正这些情况，每行一个 `pattern=type`，其中 pattern 是模组 ID 或生成器类名的一部分：

```
mrtjpcore=ores
deworldgenhandler=structures
```

映射条目先于内置关键词检查，因此也能纠正关键词判断错误的生成器。开启 `logBlockedGenerators` 后，每个生成器在首次被阻止时都会连同被判定的类型一起记录，而 `/rdplserver generators` 会按模组和类型显示累计总数。

### 替换

*各分组的作用*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `blockReplacements` | `block=block` 列表 | 无 | 将已存在区块中的方块换掉，两侧都可附带可选的 meta。每个区块在加载时处理一次，并在自身数据中标记，因此不会重复处理 |
| `blockReplacementDimensions` | 整数列表 | 无 | 此项适用的维度。留空表示所有维度 |
| `blockReplacementDimensionsAreBlacklist` | 布尔值 | `false` | 开启时，替换会跳过所列维度；关闭时，仅对所列维度生效 |
| `blockReplacementMinHeight` | 整数 | `0` | 检查的最低 y 坐标 |
| `blockReplacementMaxHeight` | 整数 | `255` | 检查的最高 y 坐标 |
| `blockReplacementKey` | 字符串 | `0000` | 修改它后，每个区块都会重新经过一次替换 |

`blockReplacements` 将已存在区块中的方块换掉，每行一个 `block=block`，两侧都可附带可选的 meta：

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

每个区块在从磁盘加载时处理一次，并在区块自身的数据中标记，因此不会重复处理。首次生成的区块不会立即清理，而是在下次加载时清理，因为生成期间相邻区块仍在向其中写入内容。位于已探索区域边缘的区块会被清理但不会被标记，因此待周围的区域生成后会再次清理。`blockReplacementDimensions` 与 `blockReplacementDimensionsAreBlacklist` 决定在何处生效，`blockReplacementMinHeight` 与 `blockReplacementMaxHeight` 决定检查世界中的哪一段高度，而 `blockReplacementKey` 是一个字符串，修改它即可让每个区块重新处理一遍。无论 `retrogen` 是否开启，它都会运行，因为需要清理的世界通常不希望再添加新的矿脉。它只替换方块：模组以结构形式生成的内容无法通过这种方式去除，因为被它取代的原有地形从未被记录下来。

### 村庄

*各分组的作用*

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

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageBlocks` | `original=replacement` 列表 | 无 | 村庄建筑所用的方块，在所有其他模组处理完之后才应用。一对方块后可附带几率和条件，此时它就成为一条规则；各字段见下方的表格 |
| `villagePieces` | 建筑片段名称列表 | 无 | 每行一个原版村庄建筑片段名称：`house1`、`house2`、`house3`、`house4garden`、`church`、`woodhut`、`hall`、`field1`、`field2`。资源包的地块以其自身模板命名，其他模组添加的片段同理 |
| `villagePiecesAreBlacklist` | 布尔值 | `true` | 开启时，所列片段被阻止；关闭时，只有所列片段会生成，且白名单只会移除原版自身的片段 |
| `villagePlotsLeast` | 整数 | `0` | 村庄至少接受的已建地块数，计入房屋、农田和资源包地块，但不计道路、火把或水井。布局小于此数的村庄会重新生长几次，并取最大的布局。`0` 保持原版行为 |
| `villagePlotsBackRow` | 布尔值 | `true` | 村庄生长完成后，再进行第二轮，在每个临街地块的正后方安置一个地块并使其朝向街道，使用相同的随机判定和相同的空间检测，这样两条街之间的街区内部会被建满而不是留空 |
| `villagePlotsMost` | 整数 | `0` | 最多允许的数量；达到上限时村庄直接停止生长，不再增加建筑和道路。`0` 保持原版行为 |
| `villageTieStreets` | 布尔值 | `true` | 开启时，若某个街区无法将其街道延伸并接入已有的村庄，则会铺设一条笔直的连接街道，通向与它对齐的最近街道。关闭时，这样的街区会被拆除 |
| `villageCitySpacing` | 整数，0 到 256 | `0` | 开启 `terrainAdaptation` 时，`1` 让世界中的每个街区群都成为一座城市，即连绵城市。在 1.12.2 上只有 `1` 会带来变化：`0` 和其他任何数值都按 `structureSpacing` 播种村庄 |
| `villageBlockSizes` | `size=weight` 列表 | 无 | 城市中平行街道之间的街区有多深，按广场位置为每个街区掷定一次。留空则让每个街区的大小都取资源包所带的最大地块 |
| `villageLayout` | 字符串 | 空 | 指定一张[城市布局地图](#城市布局地图)，按绘制好的街道规划来布置村庄，而不是让它自行生长 |

村庄与其他所有结构一样，使用同样的 `structure=value` 列表，名称为 `villages`，因此 `structureSpacing`、`structureMinDistanceFromSpawn`、`structureBiomes` 和 `structureBiomesAreBlacklist` 都对它们有效。非黑名单的 `structureBiomes` 列表还会加入该结构自身列表中从未包含的任何已命名生物群系，因此可以让村庄出现在山地中，为此请使用注册名称来指定，因为只有注册名称才能添加。其间距有 9 的下限，因为原版会从中减去 8。`villagePieces` 属于同一分组，所以一个开关就涵盖了村庄位于何处以及用什么建造的全部内容，而 `villages` 分组只涵盖资源包添加的地块。

`villageBlocks` 以 `original=replacement` 对的形式替换村庄所用的建筑方块：`minecraft:cobblestone=mypack:ruby_brick`。它在所有其他模组处理完之后才应用，因此资源包始终优先，即使面对那些按生物群系替换村庄材料的模组也是如此。两侧都接受纯方块名称或带状态的名称。道路由 `villagePathBlock` 及其同类设置单独指定。

一对方块之后可以附带几率和条件，以逗号分隔的字段写出，此时它就是一条规则而不是简单的替换。`minecraft:cobblestone=minecraft:mossy_cobblestone,20` 会让村庄铺设的圆石中有五分之一风化；`minecraft:planks=minecraft:sandstone,100,under=minecraft:sand` 只会在房屋建在沙子上的地方更换地板。这一对之后的字段可以按任意顺序给出，若某个条目包含无法识别的字段，则整条被拒绝，而不是只应用一半。

| 字段 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| chance | 整数，1 到 100 | `100` | 规则生效的频率，以百分之几计 |
| `at=` | 方块名称 | 无 | 仅当正在建造的位置上原本就有此方块时 |
| `under=` | 方块名称 | 无 | 仅当此方块正好位于该位置的正下方时 |

简单的对在建筑片段询问游戏应该用什么来建造时作答，因此会一次性改变该方块的所有墙体。规则则在方块实际被放置的地方逐个位置判定，这正是几率和条件得以生效的原因，并且它看到的是即将被放置的方块，即简单的对已经处理过之后的结果。几率落在哪些位置，由世界种子和该位置本身算出，因此无论生成多少次，同一个世界总是风化同样的方块。

道路从不适用规则，因此坡度、桥梁和路口设计仍然读取它们所铺设的道路。模板地块铺设的是它自己的 `.nbt` 文件，而不是按游戏的方式建造，所以规则不会深入其中；其方块完全由该文件决定。无论 `terrainAdaptation` 是否开启，简单的对和规则都有效。

`villagePieces` 指定原版村庄建筑片段，`house1`、`house2`、`house3`、`house4garden`、`church`、`woodhut`、`hall`、`field1` 和 `field2`，而 `villagePiecesAreBlacklist` 决定方向，因此你可以去掉原版的小麦农田而保留房屋，也可以只列出你想要的片段。资源包地块以其自身模板命名：可以写完整名称 `mypack:big_house`，也可以只写 `big_house`，或者按你的喜好使用地块自己的名称。因此资源包可以附带十个地块，而世界模板可以去掉其中一个，不影响其余九个。其他模组添加的片段同理，包括 Tektopia 的房屋或 Recurrent Complex 的地块：白名单只会移除原版自身的片段，所以列出你想要的原版片段不会悄悄删掉别人的片段。若要去掉某个模组片段，请使用黑名单并写出其名称，例如 `tekhouse2` 之类。

`villagePlotsLeast` 和 `villagePlotsMost` 限定一个村庄建造多少个地块，计入房屋、农田和资源包地块，不计道路、火把或水井。布局低于下限的村庄会以更大的规模重新生长，尝试几次后取最大的布局，因此在局促的地形上仍可能达不到要求。达到上限时，村庄直接停止生长：不再增加建筑，也不再增加道路。任一端设为 `0`，该端就保持原版行为。

`villageTieStreets` 在未设置时默认开启，它会为无法将街道延伸并接入已有村庄的街区铺设一条连接街道：一条完整宽度的笔直街道，从街区的某个街道末端通向与之对齐的最近的已有街道，条件是该线路比道路宽度更长，不超过 112 行，不穿过任何建筑片段，不紧邻平行街道，不穿过隧道，并且足够平坦可以行走。没有它时，这样的街区会被拆除，这使得建在崎岖地面上的城市只剩下广场和四条街道；有了它，街区就会并入并继续生长。建在平地上的城市很少需要它，而它默认开启，是为了让要求大型城市的资源包能得到大型城市；若要让建在崎岖地面上的城市保持较小，可将其关闭。

`villageBlockSizes` 以加权的 `size=weight` 条目设定城市中平行街道之间的街区有多深：`32=3` 和 `64=1` 会让四个街区中有三个采用 32 深的街区，其余采用 64。每个街区根据其广场位置掷定一次大小，因此同一个世界总是得到同样的组合。其街道沿长度方向每隔两个街区加一个道路宽度就分出支路，因此 16 的街区是细密的网格，64 的街区则是粗疏的网格；两条平行街道之间会保留相应数量的方块，再加上相邻街区的深度，所以 32 的街区紧邻 64 的街区时，二者之间相隔 96。只有深度合适的地块才会建在街区的街道旁，而在合适的地块中，一个地块的几率等于其权重乘以其宽度，因此深街区偏向能填满它们的建筑，而 64 宽的地块永远不会面向 32 深的街区。街道长度和广场间距仍取决于资源包所带的最大地块，这正是每个街区都能连通城市的原因。留空则让每个街区都按那个最大地块的大小，且街道只在末端分支，与以前一样。

`villageLayout` 指定一张[城市布局地图](#城市布局地图)，按绘制好的街道规划来布置村庄，而不是让它自行生长；留空则照常生长。

#### 村庄道路

*村庄*

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

以下所有内容仅在 `terrainAdaptation` 开启时才起作用。它们默认全部为空或为零，此时原版的道路保持原样。

**混合方块。** 有些方块设置接受混合方案而不是单个方块：以逗号分隔的若干方块，每个方块后跟一个空格和一个权重，例如 `"minecraft:stonebrick 3, minecraft:cobblestone 1"`。没有权重的方块计为一份。每个被放置的方块都会根据世界种子及其位置从混合方案中掷定，因此同一个世界总是建出同样的图案。接受混合方案的设置有 `villagePathVergeBlock`、`villagePathVergeWaterBlock`、`villagePathTunnelBlock`、`villagePathBridgeFrameBlock`、`villagePathBridgeFrameTopBlock`、`villageRailTunnelBlock`、`villageRailDeckBlock`、`villageRailSupportBlock`、`villageRailBarrierBlock`、`villageRailBridgeFrameBlock`、`villageRailBridgeFrameTopBlock`、`villageSubwayTunnelBlock`、`villageSubwayPlatformBlock`、`villageSubwayRailingBlock`、`villageSubwayBenchEndBlock` 和 `villageSewerMossBlock`。其他所有方块设置都始终使用混合方案中的第一个方块。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villagePathBlock` | 方块 | 空 | 路面。留空则沿用该生物群系会使用的方块：沙地上用砂岩，恶地上用硬化粘土，泥土上用草径 |
| `villagePathVergeBlock` | 方块 | 空 | 当村庄必须造地时，道路两侧以及地块之下所填充的方块。留空则顺应地形，铺设该生物群系自身的填充物，并在原本会是泥土的地方在顶部铺上草 |
| `villagePathVergeWaterBlock` | 方块 | `minecraft:planks` | 该填充物位于水面之上时变成什么，这样延伸到湖面上的路肩就不会是一根泥土柱。它也用来装饰留在水面上的石质门阶 |
| `villagePathCenterBlock` | 方块 | 空 | 道路正中的中心线。留空则不画 |
| `villagePathCenterDash` | 数字 | `0` | 让该线呈虚线：N 格线，然后 1 格路面。以世界坐标为基准，因此一个道路片段的虚线会延续到下一个片段。`0` 保持实线 |
| `villagePathLineBlock` | 方块 | 空 | 道路与人行道之间的边线。留空则不画 |
| `villagePathSidewalkBlock` | 方块 | 空 | 人行道，铺在边线之外，与路面齐平。留空则不铺 |
| `villagePathSidewalkWidth` | 数字 | `2` | 设置了 `villagePathSidewalkBlock` 之后，每条人行道的宽度 |
| `villagePathExtraWidth` | 数字 | `0` | 在原版的 3 格之外，每侧额外增加的道路格数。它会加宽道路片段本身，因此房屋会远离宽阔的街道 |
| `villagePathMinimumWidth` | 数字 | `0` | 值得铺设的最窄道路。无法容纳完整装饰的路段会降为光秃秃的 3 格宽小巷；窄于此宽度则完全不铺，村庄会绕开它进行布局。`0` 表示从不拒绝 |
| `villagePathAlleyBlock` | 方块 | 空 | 小巷的路面，小巷是太窄而无法容纳线条和人行道的道路。小巷穿行于与其相交的街道的人行道之间，自己不带人行道，并且在小巷与街道相接处不画人行横道。留空则用道路方块铺设小巷 |
| `villagePathAlleyChance` | 数字 | `0` | 一条道路被铺成小巷而不是拓宽为完整街道的百分比几率。`0` 表示只在放不下完整街道的地方铺小巷，实际上只出现在拥挤的第一个街区。调高它会改变铺设哪些道路，从而重塑整个街道图；在 50 时实测多出了七个分叉路口，因此调高后请检查结果 |
| `villagePathFlatRun` | 数字 | `6` | 一条道路在升降一格之前保持同一高度的格数。以世界坐标为基准，因此相邻片段保持一致。`0` 表示每格都升降，就像原版的坡道那样 |
| `villagePathIntersects` | 列表 | 无 | 画在路口上的设计，以资源包 `<namespace>/pathintersects/` 中的注册键命名。只写一项则所有路口都画成一样；写多项则按权重为每个路口挑选一项 |
| `villagePathDeadEnds` | 列表 | 无 | 一条没有长出回车场的断头路如何封口，选项见下文。只写一项则所有断头路封口方式相同；写多项则根据世界种子为每个末端掷定一项。留空则断头路保持敞开 |
| `villagePathLampBlock` | 方块或带数据的方块 | `minecraft:oak_fence` | 沿路灯柱所用的方块，堆叠在路缘上。留空则不立灯柱 |
| `villagePathLampHeight` | 数字 | `3` | 灯柱在灯头之前有几格高 |
| `villagePathLampTopBlock` | 方块或带数据的方块 | `minecraft:wool:15` | 灯柱顶端的灯头。留空则保持光秃 |
| `villagePathLampSideBlock` | 方块或带数据的方块 | `minecraft:torch` | 挂在灯头两侧、朝外的光源。留空则不挂 |
| `villagePathLampStructure` | 文本 | 空 | 作为整盏灯放置的结构文件，用来代替堆叠三个灯方块，写作 `mypack:street_lamp`，并从该资源包的 `structures` 文件夹读取。它以灯的位置为中心，最底层位于路缘上，其放置的方块会被固定，不会被其他内容覆盖。留空则堆叠方块 |
| `villageWellStructure` | 列表 | 无 | 作为广场中心装饰物放置的结构文件，用来代替水井，写成加权的 `name=weight` 条目，如 `mypack:plaza_spire=3`，从该资源包的 `structures` 文件夹读取，并根据每口井的位置掷定一次，因此同一口井总是得到同一个结构。`empty=weight` 条目表示保留水井所占的份额。所选结构以水井 6×6 的占地为中心，最底层位于广场地面上，其下方的地面会被铺平，并且它放置的方块会被固定，使广场装饰不会动它们。更宽的结构会向广场环带扩展。没有条目则建造水井 |

一条道路从中间向外装饰：先是中心线，然后是路面，再是边线，最后是人行道。放不下的宽度会回退而不是溢出，因此狭窄的路段会先悄悄失去人行道，然后才失去路面。

`villagePathBlock` 及其同类设置优先于 `villageBlocks`。被指定的道路方块按原样使用，而映射只改动道路本来会自行选择的内容。将它们留空，由映射来决定，资源包就能借此保留与生物群系相符的路面，同时又为其重新着色。

**灯方块带有数据。** 三个灯方块接受纯名称、带元数据的名称，或在花括号中带方块实体数据的名称，例如 `minecraft:skull:1{SkullType:3}`。花括号中的内容按 NBT 读取，并在方块放置之后应用到方块实体上，其他模组的灯就是借此保留它所需的设置。错误的 NBT 会被报告并忽略，而不会妨碍灯的建造。

**断头路。** 一条没有长出回车场的断头路由 `villagePathDeadEnds` 封口，根据世界种子为每个末端掷定一种样式。未设置其方块的样式会退出掷定，因此在 `villagePathBridgeBarrierBlock` 指定方块之前，`barrier` 不会封住任何东西，而小巷的末端永远不会采用 `sidewalk`。

| 值 | 作用 |
| --- | --- |
| `sidewalk` | 用人行道方块铺满末端那一行 |
| `barrier` | 沿末端那一行立起栅栏方块，高度为 `villagePathBridgeBarrierHeight` |

**路口设计。** `villagePathIntersects` 指定资源包附带的文件，每个文件都是一幅小图，描绘两条道路相交处要画什么，以单个字符组成的行来绘制，一个字符对应一个方块。

`<namespace>/pathintersects/*.json`

文件的路径就是该设计的注册键，`villagePathIntersects` 随后用它来指定设计。

```json
{
  "name": "Crosswalk",
  "weight": 3,
  "legend": { "w": "minecraft:quartz_block", "y": "minecraft:wool@4" },
  "mouth": ["wwww", "....", "wwww"],
  "corner": ["yy.", "y..", "..."]
}
```

| 键 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `name` | 字符串 | 文件名 | 日志中使用的名称 |
| `weight` | 整数，1 及以上 | `1` | 列出多个设计时，该设计在路口中所占的份额 |
| `legend` | 单个字符到方块的对象 | 无 | 行中除下列角色字符之外还可使用的字符。已经是角色字符的字符会被拒绝，并记入一行日志 |
| `mouth` | 字符串列表 | 无 | 画在每个入口上的行，位于相交道路之外。第一行最靠近路口，其余的向外延伸。字符沿道路横向排列，若某行比道路宽度短则重复 |
| `corner` | 字符串列表 | 无 | 画在路口内部的行。第一行最靠近相交道路的边缘，而在一行之中，第一个字符最靠近道路自身的边缘，向内依次排列。图案未覆盖的格子保持不变 |

有五个字符是角色而不是方块，因此它们会跟随道路已有的装饰：`r` 是路面，`l` 是边线，`s` 是人行道，`.` 让方块完全保持原样，`c` 为保留字符，画成路面。资源包从未设置其方块的角色会回退为路面，而任何其他字符都会在 `legend` 中查找，同样回退为路面。

路口采用哪个设计，由世界种子和路口自身的位置算出，因此同一个世界总是画出同样的路口。设计只在三条或更多街道相交处绘制，即路口或水井广场；两条街道相交只会呈现为一个普通的弯角。

#### 村庄桥梁与码头

*村庄*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villagePathSupportBlock` | 方块 | 空 | 地面为裸露岩石处的路面本身，以及水上道路之下的桥墩和桥腿。留空则沿用原版的砾石，沙漠村庄则用砂岩 |
| `villagePathBridgeBlock` | 方块 | 空 | 道路跨越水面所用的方块。留空则沿用原版的木板 |
| `villagePathBridgeBarrierBlock` | 方块 | 空 | 沿桥面两侧边缘堆叠的栏杆。留空则不建 |
| `villagePathBridgeBarrierHeight` | 数字 | `1` | 这些栏杆有几格高 |
| `villagePathBridgeSidewalkBlock` | 方块 | 空 | 道路跨水处人行道的桥面铺装。留空则沿用普通人行道方块过桥 |
| `villagePathBridgeDrop` | 数字 | `0` | 道路的坡度必须高出地面多少格，其下的落差才会架桥而不是填实。`0` 让道路留在地面上：它们只在水上架桥，别处不架。`3` 是铁路栈桥所遵循的规则。这会改变坡度，而不仅仅是装饰 |
| `villagePathBridgeFrameBlock` | 方块 | 空 | 长桥上方的框架：桥面两侧各立一根柱子，顶部横一根梁。每个框架都有一根桥墩一直通到桥面下的地面，且在它所在的那一行不会立灯柱。留空则不建 |
| `villagePathBridgeFrameTopBlock` | 方块 | 空 | 该框架顶部的横梁。留空则使用 `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight` | 数字 | `4` | 框架在桥面上方留出多少格净空，横梁位于其上一格 |
| `villagePathBridgeFrameRun` | 数字 | `24` | 当桥长到足以容纳多个框架时，相邻框架相隔多少行 |
| `villagePathBridgeFrameLeast` | 数字 | `24` | 能得到框架的最短桥段。更短的桥保持朴素 |
| `villagePathPiers` | 列表 | 无 | 在水上终止的道路所用的码头样式，选项见下文。架桥的末段会变成码头；写多项则为每个码头掷定一种样式。留空则这样的桥仍是普通的桥 |
| `villagePathPierCargo` | 列表 | 无 | 摆放在码头栏杆内侧的货物，写成加权条目，选项见下文。每个码头每隔一行就在两侧各掷定一次该列表，因此权重决定码头显得多拥挤。留空则码头上空无一物 |
| `villagePathPierLoot` | 文本 | `resourcedatapackloader:chests/pier_cargo` | 带物品栏的货物所填充的战利品表，在首次被打开时掷定。留空则这类货物是空的 |

**平整的桥面。** 每座桥从头到尾都位于同一高度，无论两岸高低如何；两侧的道路会通过坡道与之衔接。

**干燥的落差。** 道路会把洼地填实，只在水上架桥，除非 `villagePathBridgeDrop` 指定了一个高度：坡度高出地面超过该格数的那一行，会改为在桥腿上铺设桥面，就像铁路栈桥跨越沟壑那样。它改变的是坡度而不是装饰，因此启用它所布置的村庄与未启用的不一样。

**桥上框架。** 一段至少有 `villagePathBridgeFrameLeast` 行的架桥路段，在 `villagePathBridgeFrameBlock` 指定了方块之后，会在桥面上方设置框架：每侧一根柱子，顶部一根横梁，其下有 `villagePathBridgeFrameHeight` 格净空。长桥上会立多个框架，相隔 `villagePathBridgeFrameRun` 行，并以该路段的中点为中心对称分布，因此同一座桥总是带着同样的框架。另一条道路穿过该桥的那一行保持敞开，而码头完全不带框架，栈道不是桥。

**码头。** 一条延伸到水面上并止于空处的道路，在 `villagePathPiers` 至少指定一种样式之后，会成为码头而不是通向无处的桥。写多项则根据世界种子和码头的末端为每个码头掷定一种样式，因此同一个世界总是建出同样的码头。无论样式如何，每个码头都立在支撑方块制成的桩上，桩每隔四行打到桥面两侧的河床。桥面是桥方块，栏杆和立柱是栏杆方块，桩是支撑方块。

| 值 | 作用 |
| --- | --- |
| `railed` | 保留完整的桥面，朴素无线条也无人行道带，并用栏杆方块封住远端 |
| `pilings` | 每隔四行把两侧的栏杆改为立柱，立在同样的支撑之上 |
| `boardwalk` | 把桥面收窄到道路的核心宽度 |

**码头货物。** `villagePathPierCargo` 在码头上摆放货物。每隔一行在两侧各掷定一次该列表，位置在栏杆内侧一列，这样桥面中央留出可供行走的空地，从不挤占带栏杆的末端行，也不会让两件货物并排摆放，因为两个相邻的箱子会合并成一个大箱子。只有一叠中的每个方块都放得下时才会摆放，而在不同高度写两次同一个方块，就是让码头上出现大小混杂的货堆的办法。

| 值 | 作用 |
| --- | --- |
| `<block>=<weight>` | 一个方块及其所占位置的份额，摆放一格高 |
| `<block>=<weight>,<height>` | 同上，但堆叠指定的格数，范围 1 到 8 |
| `empty=<weight>` | 桥面上保持空着的份额 |

带有战利品物品栏的方块，包括箱子，会从 `villagePathPierLoot` 填充，在玩家首次打开时掷定，方式与原版箱子相同。内置的战利品表是容易获得的海上打捞物。资源包可以在 `resourcedatapackloader` 命名空间下附带自己的 `loot_tables/chests/pier_cargo.json` 来替换它，也可以指定自己的战利品表。

#### 村庄隧道

*村庄*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villagePathTunnelBlock` | 方块 | 空 | 道路穿过山丘时用来衬砌的方块，而不是把山丘切开：钻孔两侧的墙壁及其上方的顶板。留空则不挖隧道，道路像以前一样切开山丘 |
| `villagePathTunnelDepth` | 数字 | `10` | 路面之上必须有多少地面，一段路才会被钻通而不是切开。埋得这么深的隆起如果连续十二行或更多，就会保持水平并钻通，其较浅的引道则被切开；更短的土包像以前一样被切开。只有在 `villagePathTunnelBlock` 指定了方块之后才会计入 |
| `villagePathTunnelLightBlock` | 方块 | 空 | 沿隧道顶板中心线嵌入的光源。留空则不设灯 |
| `villagePathTunnelLightRun` | 数字 | `8` | 这些灯之间相隔多少格。以世界坐标为基准，因此一个道路片段的灯会延续到下一个片段；太短而够不到这些位置的隧道，会在正中间点一盏灯 |

**隧道。** 没有隧道方块时，遇到山丘的道路会爬上去，每行最多升一格，并且在短隆起上最深只切入两格。一旦 `villagePathTunnelBlock` 指定了方块，高出道路 `villagePathTunnelDepth` 或更多且至少持续十二行的隆起就会被钻通：道路在整个隆起处保持较高一侧的高度，每一行只要其上有那么多地面，就会被钻出四格高的孔洞，墙壁和顶板使用衬砌方块，而洞口之前较浅的几行则作为引道被切开。遇到的是一面无法看到另一侧的山壁而不是山丘时，道路同样不会爬升：它保持抵达时的高度并寻找山的另一侧，最远搜寻到该片段原本终点之外 98 行。若在这个范围内找到，且其间的地面没有其他片段，该片段就会被延长，从对侧洞口出来，因此隧道总是能穿通。若找不到，道路就停在山脚，永不进入山中。整条街道穿行而过，车道、线条和人行道一概如此，并由 `villagePathTunnelLightBlock` 每隔 `villagePathTunnelLightRun` 格从顶板照明，而灯柱和路肩装饰则止于洞口。路口永远不会被钻通，因此相交的街道总是在露天与这条路相接。道路将要钻通的路段旁不安置任何地块，也没有街道从中分出，因此房屋永远不会面向隧道，也不会有路口切入隧道；找不到其他空间安置地块的街区，会在那里少铺几条街道。

#### 村庄下水道

*村庄*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageSewerBlock` | 方块名称 | 无 | 村庄街道和小巷之下的下水道所用的衬砌方块：地板、两侧墙壁和顶板。留空则不挖下水道 |
| `villageSewerDepth` | 数字 | `8` | 下水道地板位于街道自身路面之下多深。下水道跟随其所在的街道，因此上坡的街道下面是上坡的下水道 |
| `villageSewerHeight` | 数字 | `3` | 人行通道上方有几格净空 |
| `villageSewerWidth` | 数字 | `5` | 下水道横向的宽度，含两侧的墙壁在内。偶数会被向上取整，以便水渠保持在正中 |
| `villageSewerWaterBlock` | 方块名称 | `minecraft:water` | 填充中间水渠的内容。留空则水渠是干的 |
| `villageSewerWalkBlock` | 方块名称 | 无 | 水渠两侧人行道的铺面。留空则踩在衬砌方块上 |
| `villageSewerLightBlock` | 方块名称 | 无 | 嵌入水渠上方顶板作为光源的方块。留空则不设灯 |
| `villageSewerLightRun` | 数字 | `8` | 这些灯之间相隔多少格。以世界坐标为基准，因此一个道路片段的灯会延续到下一个片段 |
| `villageSewerLadderBlock` | 方块名称 | 无 | 用来攀爬检修井竖井的方块，沿竖井从街道设置到下水道人行道。留空则竖井保持敞开 |
| `villageSewerCoverBlock` | 方块名称 | 无 | 盖住检修井的方块，与路面齐平地嵌在东西向街道上，位于每个街道或小巷与它相接之处，以及该街道穿过下水道环路的广场上。通常的选择是木质活板门：铁质的会接收红石信号，玩家无法徒手打开，等于把下水道对他们关闭。留空则井口保持敞开 |
| `villageSewerMossBlock` | 方块名称 | 无 | 零星混入衬砌的第二种方块，例如在普通石头中混入苔石。留空则整个下水道只用一种方块衬砌 |
| `villageSewerMossChance` | 0 到 100 | `25` | 衬砌方块中有百分之几会变成第二种方块。按方块位置根据世界种子掷定，因此同一条下水道总是呈现相同的样子 |
| `villageSewerVineBlock` | 方块名称 | 无 | 零星挂在下水道墙壁内侧的方块，例如藤蔓。它会攀附其所靠的那面墙。留空则什么也不挂 |
| `villageSewerVineChance` | 0 到 100 | `20` | 墙边的格子中有百分之几带有它。按方块位置根据世界种子掷定，因此同一条下水道总是挂着同样的东西 |
| `villageSewerWellEntrance` | 布尔值 | `true` | 在水井周围的广场环带之下设一圈下水道环路，每条街道的下水道都穿过它，并在每个东西向街道穿过它的两侧，于广场上设一个通往环路的检修井，使这些下水道成为一个相互连通的系统，并在镇中心留有入口。关闭时，每条街道的下水道止于水井，广场没有下去的通道 |

**下水道。** 指定 `villageSewerBlock` 后，会在每条街道和小巷之下、该街道自身路面之下 `villageSewerDepth` 格处挖一条下水道。它不是自成一体的网络：它跟随道路，因此街道通向哪里，下水道就通向哪里，街道转弯它也转弯，街道爬升它也爬升，两条下水道在十字路口之下相遇，因为其上方的街道相遇；街道或小巷止于另一条道路处，它的下水道会继续穿到那条路之下与另一条相接。回车场的圆端和架在桥上的那一行都不带下水道。其截面是一层衬砌的地板，中间是一条填充了 `villageSewerWaterBlock` 的水渠，两侧是铺有 `villageSewerWalkBlock` 的人行道，上方有 `villageSewerHeight` 格净空和一层衬砌的顶板，横向宽 `villageSewerWidth`，含两侧的墙壁。`villageSewerLightBlock` 每隔 `villageSewerLightRun` 格在水渠上方的顶板中嵌入一盏灯。下水道从不会升得高到干扰其上方的街道，而道路与世界底部之间没有空间的路段会被跳过，而不是硬挤进去。

#### 村庄铁路

*村庄*

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

一条铁路线是一段笔直的轨道，沿一条轴线横贯整个村庄，并在两端越过最后一个建筑片段继续延伸。它在第一条街道之前铺设，因此城镇围绕它生长：线路上不会有房屋，街道只能笔直穿过它，并且没有任何东西从它分出。与道路一样，它需要 `terrainAdaptation`。`villageRailLines` 默认为 `0`，即不铺设，村庄保持原样。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageRailLines` | 数字 | `0` | 每个村庄有多少条线路穿过。`0` 表示不铺 |
| `villageRailSpacing` | 数字 | `48` | 同一村庄中，一条线路的路基与下一条之间至少相隔多少格空地。`1` 让它们仅隔一格，资源包就是这样建出平行线路的车场的 |
| `villageRailDirection` | 文本 | `any` | 线路的走向：`ew` 为东西向，`ns` 为南北向，`any` 为每个村庄随机掷定。`e`、`w`、`n` 和 `s` 的含义与之相同 |
| `villageRailWidth` | 数字 | `3` | 路基的最小宽度。`3` 在正中承载一条轨道，`5` 承载两条；要求的轨道数超出该宽度所能容纳时，路基会加宽以容纳它们 |
| `villageRailTracks` | 数字 | `0` | 同一路基承载多少条轨道，并排铺设，彼此相隔 `villageRailTrackGap`。**路基会加宽以容纳全部轨道**，所以三条轨道共用一个路基，而不会变成三条线路。`0` 表示宽度不足五格的路基铺一条轨道，更宽的铺两条 |
| `villageRailTrackGap` | 数字 | `2` | 路基上的轨道彼此相隔多少格，以中心到中心计。允许的最小值 `2` 会在它们之间留一格路基，这样可以避免它们像相邻的铁轨那样互相弯向对方 |
| `villageRailBlock` | 方块 | 空 | 轨道。留空则铺设原版铁轨，矿车可在其上行驶；任何其他方块都按原样铺设 |
| `villageRailTrackSeat` | `auto`、`on` 或 `in` | `auto` | 轨道的位置。`auto` 把铁轨方块放在路基上，而把任何其他方块与路基表面齐平地嵌入；`on` 始终铺在路基上；`in` 始终嵌入路基。资源包若要用铁块或台阶来做出铁轨的样子，而不是矿车铁轨，就把轨道嵌入路基，这样平交道口会与路面齐平地穿过路面 |
| `villageRailBedBlock` | 方块 | 空 | 轨道之下的路基。留空则铺砾石 |
| `villageRailTieBlock` | 方块 | 空 | 每隔 `villageRailTieRun` 行横铺在路基上的枕木。留空则铺木板 |
| `villageRailTieRun` | 数字 | `2` | 枕木相隔多少行 |
| `villageRailShoulderBlock` | 方块 | 空 | 装饰路基最外侧的几列，是轨道旁的维护小路，相当于铁路对应道路人行道的设施。留空则不铺 |
| `villageRailShoulderWidth` | 数字 | `1` | 该路肩每侧有几列宽，加在 `villageRailWidth` 之外 |
| `villageRailPowerBlock` | 方块 | 空 | 每隔 `villageRailPowerRun` 行嵌入线路的动力轨道。留空则使用原版动力铁轨；不是铁轨的方块则直接铺在那里 |
| `villageRailPowerBase` | 方块 | 空 | 动力轨道之下用来给它供能的方块。留空则使用红石块 |
| `villageRailPowerRun` | 数字 | `0` | 每隔这么多行，在原版铁轨轨道中嵌入一根位于红石块上的动力铁轨，让矿车持续行驶。`0` 表示不设置动力轨道，且任何非原版铁轨的轨道都会忽略它 |
| `villageRailSupportBlock` | 方块 | 空 | 栈桥之下的立柱。留空则使用原木 |
| `villageRailDeckBlock` | 方块 | 空 | 栈桥承载路基的桥面。留空则使用木板 |
| `villageRailBarrierBlock` | 方块 | 空 | 沿栈桥桥面两侧边缘的栏杆。留空则不立 |
| `villageRailBridgeFrameBlock` | 方块 | 空 | 长栈桥上方的框架：桥面两侧各立一根柱子，顶部横一根梁。每个带框架的行，其支撑柱也一直通到路基。留空则不建 |
| `villageRailBridgeFrameTopBlock` | 方块 | 空 | 该框架顶部的横梁。留空则使用 `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight` | 数字 | `4` | 框架在桥面上方留出多少格净空，横梁位于其上一格 |
| `villageRailBridgeFrameRun` | 数字 | `24` | 当栈桥长到足以容纳多个框架时，相邻框架相隔多少行 |
| `villageRailBridgeFrameLeast` | 数字 | `24` | 能得到框架的最短栈桥。更短的栈桥保持朴素 |
| `villageRailTunnelBlock` | 方块 | 空 | 线路穿过山丘时衬砌墙壁和顶板的方块。留空则不挖隧道，所有山丘都被切开 |
| `villageRailTunnelDepth` | 数字 | `6` | 路基之上必须有多少地面，一段路才会被钻通而不是切开。需要 `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock` | 方块 | 空 | 沿铁路隧道顶板中心线嵌入的光源。留空则不设灯 |
| `villageRailTunnelLightRun` | 数字 | `8` | 这些隧道灯之间相隔多少格，以世界坐标为基准，使各片段保持一致 |
| `villageRailClimb` | 数字 | `8` | 线路每升降一格，要先水平延伸多少行。`1` 表示与道路一样陡 |
| `villageRailTail` | 数字 | `48` | 线路在两端越过村庄最后一个建筑片段后继续延伸多远 |

**线路走向。** 各线路平行，沿 `villageRailDirection` 指定的轴线延伸，并依次从水井广场向外隔开，先一侧再另一侧，每条线路的路基与下一条的路基之间至少保留 `villageRailSpacing` 格地面。线路从不穿过广场或地块：它在第一条街道之前铺设，因此村庄的每条街道和每座房屋都围绕它布置，待村庄布局完成后，它会被修剪为已生长的村庄加上两端各 `villageRailTail` 的长度。

**坡度。** 铁路不像道路那样爬升。它的路基跟随经过长距离平滑后的地面，最多每隔 `villageRailClimb` 行变化一格高度。地面下陷超过三格的地方，线路改走栈桥，在每四行一根的 `villageRailSupportBlock` 立柱上铺 `villageRailDeckBlock`，无论是跨水还是跨沟壑都是如此。地面升高的地方，线路被切开，或者在路基之上的地面达到 `villageRailTunnelDepth` 深且持续十二行或更多时，用 `villageRailTunnelBlock` 钻通。整条线路沿线在路基之上保留四格净空。栈桥从头到尾位于同一高度，其两侧的路基通过坡道与该高度衔接；当保持栈桥水平与爬升速率相冲突时，以水平为准，其旁的坡道可能比 `villageRailClimb` 所说的更早升降。一段至少有 `villageRailBridgeFrameLeast` 行的栈桥，在 `villageRailBridgeFrameBlock` 指定了方块之后会设置桥上框架，相隔 `villageRailBridgeFrameRun` 行，并以栈桥中点为中心对称分布，每个带框架的行，其支撑柱也随之一直通到路基。道路穿过线路的那一行保持敞开。

**交叉口。** 街道笔直地穿过线路，并在路基两侧各延伸出七格或更多。将止于线路上或这七格之内的街道，在坡度允许时会继续穿越过去，否则就在距路基七格处停下；将起始于线路上或沿着线路延伸的街道则被拒绝。在交叉口，是街道迁就线路的坡度，而不是反过来，并在两侧以其自身可行走的坡度坡道升降到该高度。路面保持原样，轨道在其上高出一格穿过，因此矿车可以穿过道路，村民也可以穿过轨道。钻通山丘的线路根本不会被穿越：街道从隧道上方经过。

**门阶。** 在 `terrainAdaptation` 开启时，村庄建筑不会在自身范围之外放置楼梯方块：原版在门前放置的门阶楼梯会被省去，因为道路临街面和地块围裙自会把地面延伸到门口。

**轨道。** `villageRailBlock` 为空时，轨道是沿线路方向的原版铁轨，而 `villageRailPowerRun` 每隔若干行就在红石块上设一根动力铁轨，让矿车能行驶整条线路。资源包若想用铁块、栏杆或其他任何东西，只需指定它们，线路就会按原样用该方块来装饰。

#### 村庄地铁

*村庄*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageSubwayLines` | 数字 | `0` | 一个村庄挖多少条地下铁路线。0 表示不挖，也不进行任何随机判定，因此村庄的布局与没有地铁时完全相同 |
| `villageSubwayDepth` | 数字 | `24` | 路基位于地表之下多深。线路的坡度依据其上方的地面来定，因此它在该深度跟随地势起伏，而不是水平延伸 |
| `villageSubwaySpacing` | 数字 | `64` | 一个村庄的各条地铁线路彼此保持多远的间距 |
| `villageSubwayDirection` | 字符串 | `any` | 地铁线路的走向：`ew` 为东西向，`ns` 为南北向，`any` 为每个村庄随机掷定 |
| `villageSubwayWidth` | 数字 | `3` | 路基有多宽，不含路肩 |
| `villageSubwayTracks` | 数字 | `0` | 路基承载多少条平行轨道。0 表示宽度允许多少就铺多少 |
| `villageSubwayTrackGap` | 数字 | `2` | 平行轨道彼此相隔多远 |
| `villageSubwayBlock` | 方块 | 空 | 轨道方块。留空则铺设原版铁轨 |
| `villageSubwayTrackSeat` | 字符串 | `auto` | 轨道是放在路基上、嵌入路基，还是用 `auto` 由方块自行决定 |
| `villageSubwayBedBlock` | 方块 | 空 | 构成路基的方块。留空则使用砾石 |
| `villageSubwayTieBlock` | 方块 | 空 | 横铺在路基上作为枕木的方块。留空则使用木板 |
| `villageSubwayTieRun` | 数字 | `2` | 枕木相隔多少格 |
| `villageSubwayShoulderBlock` | 方块 | 空 | 路基两侧的方块。留空则不设路肩 |
| `villageSubwayShoulderWidth` | 数字 | `1` | 该路肩有多宽 |
| `villageSubwayPowerBlock` | 方块 | 空 | 动力轨道方块。留空则使用原版动力铁轨 |
| `villageSubwayPowerBase` | 方块 | 空 | 设在动力轨道之下用来驱动它的方块。留空则使用红石块 |
| `villageSubwayPowerRun` | 数字 | `0` | 动力轨道相隔多少格。0 表示不设置 |
| `villageSubwayTunnelBlock` | 方块 | 空 | 衬砌钻孔的方块：两侧的墙壁及其上方的顶板。留空则钻孔及其车站都不加衬砌 |
| `villageSubwayTunnelLightBlock` | 方块 | 空 | 嵌入隧道顶板作为光源的方块。留空则不设灯 |
| `villageSubwayTunnelLightRun` | 数字 | `8` | 这些灯之间相隔多少格，以世界坐标为基准，使各片段保持一致 |
| `villageSubwayClimb` | 数字 | `8` | 一条线路要延伸多少格，才可以升降一格 |
| `villageSubwayTail` | 数字 | `48` | 线路在越过村庄自身的建筑片段之后，延伸多远才停止 |
| `villageSubwaySurfaces` | 数字 | `25` | 一条地铁线路在一端爬升到地表，并从那里作为普通铁路继续延伸的百分比几率，身后是隧道，前方是露天轨道。`0` 让每条地铁在全长上都埋在地下 |

**爬出地面。** `villageSubwaySurfaces` 是一条线路不再从头到尾埋在地下，而是在一端爬升到地表，并从那里作为普通铁路继续延伸的百分比几率，身后是隧道，前方是露天轨道。爬升遵循 `villageSubwayClimb`，即每经过那么多行升一格，因此深度为 `villageSubwayDepth` 的线路仅坡道就要花去深度乘以爬升率那么多行，并且在其后还需要相当长的一段距离才名副其实；两者都没有空间的线路就只会留在地下。在带有车站的短线路上，调高 `villageSubwayClimb` 才能为两者腾出空间。

#### 地铁站

*村庄*

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | 数字 | `0` | 车站大厅有多少格长，以线路最靠近水井的那一行为中心。0 表示完全不建车站 |
| `villageSubwayStationRun` | 数字 | `0` | 沿线路在水井处的车站之后，其余车站相隔多少格。每个车站都会沿线稍微滑动，寻找能容纳它的地面，找不到的地方就不建。0 表示只建那一个 |
| `villageSubwayStationRepeat` | 数字 | `12` | 车站建筑中有多少层重复，使一份建筑适用于任何深度：竖井以这一段的整份副本增长，而走廊吸收多出的部分。它必须是楼梯的整数圈，否则各段楼梯无法衔接。`0` 表示从不扩展建筑 |
| `villageSubwayStationFoot` | 数字 | `4` | 车站建筑底部有多少层只铺设一次，位于重复部分之前。地板和通向站台的门洞就在这里 |
| `villageSubwayPlatformWidth` | 数字 | `3` | 大厅在路基两侧各向外拓宽多远以构成站台 |
| `villageSubwayPlatformBlock` | 方块 | 空 | 站台地板所用的方块。留空则用隧道衬砌铺地板 |
| `villageSubwayStation` | 文本 | 空 | 每个车站所依据的结构文件，写作 `mypack:subway_station`，并从该资源包的 `structures` 文件夹读取。其方块按建造时的样子铺设，以海绵代表隧道衬砌，其空气格会被挖空，因此地下所立的就是这份建筑本身，而不是对它的描述。只有当此项指定了能加载的建筑时，线路才会有车站：留空，或名称无法加载，则完全不建车站 |
| `villageSubwayRailingBlock` | 方块 | `minecraft:iron_bars` | 围在车站楼梯顶部、与街道相通处的方块，防止有人走进井里。留空则楼梯顶部不设栏杆 |
| `villageSubwayBenchBlock` | 方块 | `minecraft:oak_stairs` | 设在车站站台上以及楼梯顶部旁的长椅的座位。楼梯方块会被转向背离线路，看起来就是长椅；任何方块都可以。留空则不设长椅 |
| `villageSubwayBenchEndBlock` | 方块 | `minecraft:log` | 车站长椅两端的扶手。留空则座位两端光秃 |
| `villageSubwayBenchLength` | 数字 | `5` | 车站长椅有多长，含扶手在内。`0` 表示不设长椅 |

**车站。** 在设置了 `villageSubwayStationLength` 且 `villageSubwayStation` 指定了能加载的建筑之后，地铁线路会在最靠近水井之处得到一个车站，并沿线每隔 `villageSubwayStationRun` 格再有一个。其中每一个都会向两侧滑动几格，寻找地面能容纳的位置，避开已被占用的车站，附近找不到可行位置时就直接不建，因此线路上永远不会有没有出入口的大厅。大厅是向两侧各拓宽 `villageSubwayPlatformWidth` 的路基，地板铺 `villageSubwayPlatformBlock`，墙壁和顶板采用隧道衬砌，并由隧道自身的 `villageSubwayTunnelLightBlock` 和 `villageSubwayTunnelLightRun` 照明。从站台出发，一条走廊通向一个楼梯井，楼梯井爬升到道路旁边的街道上，绝不在道路之下，也绝不穿过水井广场或房屋；爬升过长而无法直行时，走廊会先沿大厅折返。街道层的楼梯顶部围着一圈 `villageSubwayRailingBlock` 栏杆，靠近的一端留作入口敞开，而站台上以及楼梯顶部旁各有一张长椅，座位为 `villageSubwayBenchBlock`，扶手为 `villageSubwayBenchEndBlock`，长度为 `villageSubwayBenchLength`。

**手工建造车站。** `villageSubwayStation` 指定每个车站所依据的结构文件，没有它就不建任何车站。资源包借此附带某人亲手建造的造型，而不是靠设置描述出来的造型。在世界中把它建好，用任意方块标记该结构，导出，然后随资源包一起放置：其方块按建造时的样子铺设，以海绵代表隧道衬砌，其空气格会被挖空。一份建筑可适用于任何深度，因为它的中间部分会重复——`villageSubwayStationFoot` 层在底部只铺设一次，包含地板和通向站台的门洞，然后把接下来的 `villageSubwayStationRepeat` 层整份堆叠上去，直到建筑到达街道。该段必须是楼梯的整数圈，否则两份副本衔接处的各段楼梯无法对接。建筑自带通向街道的出口。

#### 铁路连接

*村庄*

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

铁路连接把相邻的村庄连成一个网络。村庄在村庄网格（`structureSpacing`）中每个单元格建立一个，而连接沿着两个单元格之间的接缝铺设：每个村庄的第一条线路越过其尾端继续延伸成为支线，笔直地通向接缝，并与沿接缝铺设的干线成直角相接。干线从一条支线延伸到另一条，从不越过任何一端。它需要 `villageRailLines`，在没有地面线路的资源包中则需要 `villageSubwayLines`，并且默认关闭。

| 设置 | 类型 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `villageRailLinks` | true/false | `false` | 连接第一条线路隔着接缝相对的相邻村庄 |
| `villageRailLinkLeast` | 数字 | `128` | 所铺连接的最短长度，支线加干线再加支线，以格计 |
| `villageRailLinkMost` | 数字 | `1024` | 所铺连接的最长长度，支线加干线再加支线，以格计 |
| `villageRailLinkBridgeMost` | 数字 | `96` | 一条连接可能需要的最长桥梁。需要跨越更宽的水面或更深的落差的连接不会铺设 |
| `villageRailLinkTunnelMost` | 数字 | `192` | 在 `villageRailTunnelBlock` 会钻隧道的情况下，一条连接可能需要的最长隧道。需要钻得更远的连接不会铺设 |
| `villageRailLinkStation` | 文本 | `both` | 每条支线在干线之前的车站：`both` 在线路两侧各铺一个站台，`one` 在驶向干线的列车左侧铺一个站台，`none` 则不建 |
| `villageRailLinkStationLength` | 数字 | `16` | 车站站台有多少行长。`0` 表示不建车站 |
| `villageRailLinkPlatformWidth` | 数字 | `3` | 每个站台有多少格宽 |
| `villageRailLinkPlatformBlock` | 方块 | 空 | 建造站台所用的方块。留空则使用石砖 |

**哪些村庄会连接。** 只有当两个村庄位于相邻的单元格中，它们的第一条线路沿着穿过其间接缝的轴线延伸，并且整条连接沿轨道从井到井测得的长度介于 `villageRailLinkLeast` 与 `villageRailLinkMost` 格之间时，两个村庄才会连接。决定的每一部分都由种子和这两个村庄的位置算出，因此无论先生成哪个村庄或区块，结果都相同。无法完整建成的连接根本不会铺设，绝不会只建一半：需要比设置所允许更长的桥或隧道、越过世界边界、撞上林地府邸、让两个村庄比 `structureSeparation` 所允许的更近，或使交汇处太靠近单元格的角落，都属于这种情况。干线只会铺向确实已建立的村庄：当 `structureMost` 之类的上限阻止了邻村，或邻村长得太小而无法保留时，干线的两半都不会建，越过该村庄自身尾端的支线也不会建。固定位置的村庄以同样的方式连接，每个单元格一个；含有两个固定村庄的单元格，两者都不连接。其他村庄在生长时会避开连接的支线和干线，就像它们彼此避让那样。

**坡度。** 支线和干线都是铁路线，其坡度、架桥、挖隧道和交叉的处理与村庄线路完全相同，使用上文的 `villageRailClimb` 以及栈桥和隧道设置。支线与干线相接处，两者都保持水平，其旁的车站也是如此。

**交汇处。** 支线只与干线靠近的那条轨道相接。该轨道在支线中线与之相遇处断开，支线的左轨道向左弯入，右轨道向右弯入，而远侧的轨道笔直穿过。以两条轨道、干线在上方、支线从下方而来为例：

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` 是路基，`o` 是轨道。当两条支线会在相距几格之内抵达时，第二个村庄的第一条线路会移过去与第一条对齐，两者改在十字路口相接：每条支线只按上述方式并入各自靠近的轨道，干线的两条轨道都在支线中心处断开，并且没有任何铁轨与另一条交叉：

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

单轨干线没有第二条轨道可以给另一条支线，因此支线会在单轨上迎头相遇的连接不会铺设。在单轨的情况下，支线的轨道向左弯入干线轨道，而弯道之外的干线轨道则止于其上。这些弯道的形状是固定设置的，因此原版铁轨只在绘制的交汇处转弯，别处不会。

**车站。** 支线在交汇处之前的最后几行是车站：由 `villageRailLinkPlatformBlock` 制成、与铁轨齐平的站台，外侧边缘围以 `villageSubwayRailingBlock` 栏杆，每个站台的中段设一张 `villageSubwayBenchBlock` 长椅。

**地铁。** 在只有地铁线路的资源包中，连接承载村庄的第一条地铁线路。该线路从地下爬升向干线，坡道长 `villageSubwayDepth` 乘以 `villageSubwayClimb` 行，并在地表到达车站和交汇处；这类村庄只在一侧连接，即连接较短的那一侧，而干线是依据 `villageRail` 设置建造的地面铁路。当某条支线没有空间容纳该坡道及其车站时，干线则改为下沉到地铁：整条连接，包括支线和干线，都留在 `villageSubwayDepth` 的地下，依据 `villageSubway` 设置建造，并在没有车站的情况下于同样的交汇处相接。

#### 村庄装饰

*村庄*

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
| `villageDecor` | `name=weight` 列表 | 无 | 沿村庄道路的路肩散布本资源包自己的世界生成内容。名称是世界生成注册键，权重是该条目在各位置中所占的份额，而 `empty=weight` 是保持空白的位置所占的份额 |

`villageDecor` 沿村庄道路的路肩散布资源包自己的世界生成内容，这能避免村庄看起来只是几座房屋立在光秃秃的草地上。每个条目写作 `name=weight`：名称是世界生成注册键，例如 `mypack:street_flowers`，权重是该条目在各位置中所占的份额。名称 `empty` 表示保持空白的位置所占的份额，这一项最需要设对，因为没有它的列表会填满每个路肩上的每个位置，村庄就会变成一座苗圃而不是一条街道。

道路每一侧每隔第三个方块就是一个位置，按世界坐标计数，因此间距会从一个道路片段延续到下一个片段。位于村庄任何片段之内、道路本身之上、门前，或者地面不是立在坚实之物上的开阔空气处的位置，都会被跳过。每个位置上长出什么，由世界种子和该位置本身算出，因此同一个世界总是以同样的方式散布。

该名称指向 `<namespace>/worldgen/*.json` 中一个普通的世界生成条目，因此 `decoration`、`tree` 或 `imprint` 都可以，且各自保留自己的方块、大小和散布方式。这里只用到该条目的形态：它的生物群系、维度、高度和稀有度是它在整个世界中自行播撒的方式，而村庄并不参考它们，所以专为路肩编写的条目最好不要用于别处。路肩是立在地面上的开阔空气，因此这样的条目需要把 `replace` 设为 `minecraft:air`；从未写出 `replace` 的条目会被赋予通常的默认值 `minecraft:stone`，在这里悄悄地什么也不放。

在 `terrainAdaptation` 开启期间，一个位置上长出的任何东西都会被固定下来，免受村庄自身整理的影响，因此立在路肩上的树不会在下一个区块被装饰时再被砍掉。关闭它时，没有整理需要抵御，散布的效果相同。

#### 按生物群系划分的村庄设置

*村庄*

**一个生物群系可以有不同的建法。** `settings` 中的 `biomes` 对象为某个指定的生物群系保存它自己的村庄设置，因此沙漠村庄可以铺砂岩街道，而平原村庄铺混凝土，二者无需成为不同的资源包。生物群系可以用 ID 指定，例如 `minecraft:desert`，也可以用 Forge 生物群系类型指定，例如 `SANDY`、`SNOWY`、`MESA`、`JUNGLE` 等；精确的 ID 先于类型被查看，因此可以为某一个生物群系覆盖一条通用规则。某个分节中未提及的所有内容，都回退为其上方的普通设置。

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

道路、桥梁、铁路、地铁、车站或下水道所接受的每个方块设置都适用于此，并且加权混合语法在分节内部与在外部同样有效。生物群系是在建造每个片段时读取的，而在地面生物群系发生变化的地方，方块会重新选取，因此跨出沙漠的道路或铁路会在边界处当场更换材料。世界加载时的一行日志会说明资源包附带了多少个分节并列出它们的名称，开启调试后，每个生物群系还会说明它采用了哪个分节，或者说明它没有采用任何分节以及它本会对应哪个分节。

### Blast Plaster

*各分组的作用*

爆炸之后会发生什么，由 `<namespace>/blastplaster/*.json` 决定。`default` 交由资源包决定；`global` 忽略资源包文件，让本模组自身的默认值覆盖 Blast Plaster 的配置；`off` 则把控制权完全交还给 Blast Plaster 自己的配置。

### 结构

*各分组的作用*

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

| 设置                            | 类型                                          | 默认值  | 作用                                                                                                                                                                                                                 |
| ------------------------------- | --------------------------------------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureSpacing`              | `structure=chunks` 的列表                     | 原版    | 结构按多大间距播种。适用于神殿、海底神殿、林地府邸、末地城和要塞；对 `mineshafts` 而言，该数字表示每这么多个区块出现一个，因为原版就是这样放置它们的                                                                  |
| `structureSeparation`           | `structure=chunks` 的列表                     | 原版    | 同类结构之间最近允许多近。适用于海底神殿、林地府邸、末地城、要塞和村庄；对村庄而言，它是相邻两个村庄之间至少相隔的区块数，无论网格本来允许多近                                                                        |
| `structureMinDistanceFromSpawn` | `structure=blocks` 的列表                     | 原版    | 结构距离世界出生点多远才开始生成                                                                                                                                                                                     |
| `structureBiomes`               | `structure=biome,biome` 的列表                | 原版    | 结构在哪些生物群系中生成，可用注册名或生物群系字典类型指定。适用于除末地城以外的所有结构，因为在此版本中末地只有一个生物群系                                                                                          |
| `structureBiomesAreBlacklist`   | `structure=true` 或 `structure=false` 的列表  | `false` | 每个结构的生物群系列表的方向                                                                                                                                                                                         |
| `structureSpawns`               | `structure=entity:weight:least:most` 的列表   | 原版    | 无论周围生物群系如何规定，结构都会生成的生物。只有神殿、海底神殿和下界要塞设有这样的列表；等号后留空一行会让该结构不再生成任何自身的生物                                                                              |
| `structureSpawners`             | `structure=entity` 的列表                     | 原版    | 原版结构内的刷怪笼生成什么，用逗号分隔则每个刷怪笼随机选一个。会放置刷怪笼的有四种：地牢、废弃矿井、下界要塞和要塞                                                                                                    |
| `structureAt`                   | `structure=x,z` 的列表                        | 无      | 把结构固定在精确的位置。参见[位于精确位置的结构](#位于精确位置的结构)                                                                                                                                                |
| `structureMost`                 | `structure=count` 的列表                      | 无      | 一个维度最多可容纳多少个某种结构。只有村庄读取它，且用 `structureAt` 固定的村庄无论如何都会建立                                                                                                                      |

按名称关闭原版结构，按维度分别设置。放置由四个写成 `structure=value` 的列表控制，每行一条：`structureSpacing` 决定播种间距，`structureSeparation` 决定同类结构最近能多近，`structureMinDistanceFromSpawn` 决定从多远开始出现，`structureBiomes` 与 `structureBiomesAreBlacklist` 决定允许出现在哪里。

```
temples=24
monuments=40
mineshafts=200
```

```
temples=minecraft:desert,SANDY
monuments=minecraft:deep_ocean
```

并非每种结构都理解每项设置。间距适用于神殿、海底神殿、林地府邸、末地城和要塞；对 `mineshafts` 而言，该数字表示每这么多个区块出现一个而非网格，因为原版就是这样放置它们的。间隔适用于海底神殿、林地府邸、末地城、要塞和村庄；对村庄而言，它是相邻两个村庄之间至少相隔的区块数，无论网格本来允许多近。`structureMost` 限制一个维度最多可容纳多少个某种结构，例如 `villages=100`：一旦建立了这么多个，无论网格会把下一个放在哪里，都不再建立新的，而用 `structureAt` 固定的村庄则无论如何都会建立。只有村庄读取它。生物群系设置适用于除末地城以外的所有结构，因为在此版本中末地只有一个生物群系，无从选择。末地城仍然在网格内自行挑选位置：它们只会坐落在地表达到 y60 的外岛上，所以提高间距能让它们变稀疏，却无法把一座末地城放到虚空之上。下界要塞位于原版没有公开的固定网格上，因此只有生物群系列表和出生点距离列表对它们有效。村庄保留自己的 `villageSpacing`、`villageBiomes` 等设置。

`structureSpawns` 替换结构所生成的生物，无论周围生物群系如何规定，写成 `structure=namespace:entity:weight:least:most`，以逗号分隔：

```
netherbridges=minecraft:blaze:10:2:3,minecraft:wither_skeleton:8:5:5
temples=minecraft:witch:1:1:1
monuments=
```

在此版本中，只有神殿、海底神殿和下界要塞设有这样的列表；村庄的村民由建筑部件自身放置，而废弃矿井、要塞和末地城则改用刷怪笼和已放置的生物。像上面的 monuments 那样，等号后留空，会让该结构不再生成任何自身的生物。

`structureSpawners` 指定原版结构内的刷怪笼生成什么，写成 `structure=namespace:entity`，以逗号分隔则每个刷怪笼随机选一个：

```
dungeons=minecraft:zombie,minecraft:husk
mineshafts=minecraft:cave_spider
netherbridges=minecraft:wither_skeleton
strongholds=minecraft:silverfish
```

有四种原版结构会放置刷怪笼：地牢房间、废弃矿井走廊、下界要塞王座和要塞传送门房间。每一种都单独生效，所以其他模组放置的刷怪笼不会被触及。地牢通常从各模组通过 Forge 添加的列表中挑选，因此在此处指定它们也会接管这一选择。

间距决定结构在何处播种，所以在已存在的世界里修改它，已有的结构保持原样，新的结构则会放在另一套网格上。

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

| 设置                          | 类型                       | 默认值  | 作用                                                                                                                                                                                                   |
| ----------------------------- | -------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `surfaceDayMonsterRate`       | 浮点数                     | `1.0`   | 白天地表敌对生物生成的倍率，`1.0` 即原版，因此可以关闭白天地表的生成而不影响洞穴                                                                                                                       |
| `surfaceNightMonsterRate`     | 浮点数                     | `1.0`   | 夜间地表的同类倍率                                                                                                                                                                                     |
| `undergroundDayMonsterRate`   | 浮点数                     | `1.0`   | 白天地下的同类倍率                                                                                                                                                                                     |
| `undergroundNightMonsterRate` | 浮点数                     | `1.0`   | 夜间地下的同类倍率                                                                                                                                                                                     |
| `monsterCap`                  | 整数                       | `-1`    | 同时可加载多少敌对生物。原版为 70，`-1` 表示保持不变                                                                                                                                                   |
| `creatureCap`                 | 整数                       | `-1`    | 被动动物的同类上限。原版为 10                                                                                                                                                                          |
| `ambientCap`                  | 整数                       | `-1`    | 蝙蝠之类生物的同类上限。原版为 15                                                                                                                                                                      |
| `waterCreatureCap`            | 整数                       | `-1`    | 鱿鱼的同类上限。原版为 5                                                                                                                                                                               |
| `monsterSpawnLight`           | 整数                       | `-1`    | 在原版检查之外，敌对生物生成所能容忍的最高方块光照。`0` 是现代版本的规则，火把可以完全保护一个洞穴；`-1` 保留原版的随机判定                                                                             |
| `skyAnimals`                  | 布尔值                     | `true`  | 被动生物是否会在 rubic 世界于其地形窗口之上生成的陆地上定居，尤其是上方的空岛。关闭后动物和蝙蝠只留在下方的地面上。刷怪笼不受这两项影响                                                                  |
| `threatItems`             | `item=level,count` 的列表  | 无      | 会提高携带者威胁分数的物品，末尾可带可选的 `,each` 或 `,batch`：默认的 `each` 对持有的每一个都加上该等级，最多计到 `count` 个；`batch` 则每持有整整 `count` 个加一次                                     |
| `threatLevels`                | 整数列表                   | 无      | 进入各个档位的分数，递增，所以 `10, 25, 50` 构成三个档位。留空则关闭威胁等级                                                                                                                           |
| `threatMost`                  | 整数                       | `-1`    | 分数上限。`-1` 表示不设上限                                                                                                                                                                            |
| `threatSpawnRate`             | 浮点数                     | `1.0`   | 在最高档位携带者 128 格范围内，在其他倍率之上缩放敌对生物的生成，较低档位按比例分得一部分                                                                                                              |
| `threatNotice`                | 浮点数，格                 | `0.0`   | 敌对生物（包括原版的）能在多远的额外距离外发现最高档位的携带者，同样按比例分摊给较低档位                                                                                                               |
| `threatSays`                  | `band=message` 的列表      | 无      | 玩家自身档位变化时以黄色显示的话，档位 `0` 是降回第一档以下时的那一句                                                                                                                                  |

各生物群系的生物生成倍率与上限。敌对生物的生成由 `surfaceDayMonsterRate`、`surfaceNightMonsterRate`、`undergroundDayMonsterRate` 和 `undergroundNightMonsterRate` 缩放，每个都是倍率，`1.0` 即原版，因此可以关闭白天地表的生成而不影响洞穴。上限包括 `monsterCap`、被动动物的 `creatureCap`、蝙蝠之类的 `ambientCap` 以及鱿鱼的 `waterCreatureCap`；原版分别为 70、10、15 和 5，`-1` 表示保持不变。`monsterSpawnLight` 在原版检查之外限制敌对生物生成所能容忍的方块光照：`0` 是现代版本的规则，火把可以完全保护一个洞穴，默认的 `-1` 保留原版的随机判定。`skyAnimals` 决定被动生物是否会在 rubic 世界于其地形窗口之上生成的陆地上定居，尤其是上方的空岛：默认的 `true` 让原版的畜群出现在顶层方块所在之处，`false` 则让动物和蝙蝠只留在下方的地面上。刷怪笼不受这两项影响。

威胁等级会给每位玩家所携带的东西打分，并让世界做出回应。`threatItems` 列出计分的物品，写成 `item=level,count` 条目，末尾可带可选的 `,each` 或 `,batch`：默认的 `each` 对持有的每一个都加上该等级，最多计到 `count` 个；`batch` 则每持有整整 `count` 个加一次该等级，只算完整的批次。超过物品堆叠上限的 count 会被截到堆叠上限，所以一整组就是一条条目所能计的最大数量，物品还可以带元数据，例如 `minecraft:dye:4`。每个持有物品的已加载实体都是携带者，不只是玩家：玩家的主物品栏、盔甲和副手、掉落的物品堆、任何带物品栏的实体（如驮运箱子的骡子或运输矿车），以及其他任何生物手持和穿戴的物品，因此一片区域会在躺在、骑行在或走动在那里的东西周围保持危险。创造模式和旁观模式的玩家得分为零。`threatLevels` 是进入各个档位的分数，递增，所以 `[10, 25, 50]` 构成三个档位，`threatMost` 限制分数上限，`-1` 表示不设上限。分数每五秒计算一次。`threatSpawnRate` 在最高档位携带者 128 格范围内，在其他倍率之上缩放敌对生物的生成，较低档位按比例分得变化量的一部分：`2.0` 在最高档翻倍，在三档中的第一档则增加三分之一。`threatNotice` 是敌对生物（包括原版的）能在多远的额外距离外发现最高档位的携带者，同样按比例分摊给较低档位。`threatSays` 是玩家自身档位变化时以黄色显示的话，写成 `band=message` 条目，档位 `0` 是降回第一档以下时的那一句。实体变种可以设置 `threatLeast`，使其只在 128 格内有携带者处于该档位或更高档位时才自然生成。两个列表中任何一个留空都会关闭威胁等级。

### 安置结构

*各分组的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"]
  }
}
```

| 设置                  | 类型                     | 默认值                                              | 作用                                                                                                                                                                                      |
| --------------------- | ------------------------ | --------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureAdaptation` | `structure=mode` 的列表  | 村庄和林地府邸为 `beard_thin`，其余为 `none`        | 地形对哪些结构进行适应以及如何适应，涵盖村庄、要塞、废弃矿井、海底神殿和林地府邸。模式有 `none`、`bury`、`beard_thin`、`beard_box` 和 `encapsulate`                                          |

`structureAdaptation` 决定地形对哪些结构进行适应以及如何适应，写成 `structure=mode` 条目，如 `"mansions=bury"`、`"monuments=none"`，涵盖村庄、要塞、废弃矿井、海底神殿和林地府邸，使用现代版本采用的五种模式：`none`、`bury`、`beard_thin`、`beard_box` 和 `encapsulate`。除非另行指定，村庄和林地府邸为 `beard_thin`，其余一律为 `none`。神殿目前不能指定，因为它们只在建造时才放置自身，地形来不及对其做出适应。

### 安置村庄

*各分组的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "terrainAdaptation": true
  }
}
```

| 设置                | 类型   | 默认值  | 作用                                                                                                                                                                            |
| ------------------- | ------ | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `terrainAdaptation` | 布尔值 | `false` | 重新设计村庄如何选择地面并坐落其上：平整的道路、安置好的建筑、堆起的环形土台，以及本节描述的其他一切。它所铺设的内容是永久的 |

**它所铺设的内容是永久的。** 它在世界生成时就重塑地形，所以它写入存档的一切都会留在那里。旧版本生成的村庄不会被新版本重新访问或修复，因此用同一个种子在两个不同版本的模组上生成的两个世界并不会一致，世界里的村庄只是那些区块生成当天的一份快照。

`terrainAdaptation` 重新设计村庄如何选择地面并坐落其上，其思路移植自现代版本安置结构的方式，并在此基础上更进一步。村庄只会建立在地面起伏不超过十格的区块上，且绝不会距离另一座村庄不到八个区块；没有这种区块的区域则完全不会建立村庄。水井坐落在其占地所接触到的最低地面上，整座村庄随之移动，其余一切都以此为基准找平。

道路在铺设时就被整平：路面跟随道路宽度范围内最低的天然地面，凸起被削平，凹陷被填上，坡度每级不超过一格，较短的沟壑用木板桥跨过。路面材质跟随其所经过的地面：泥土上是草径，沙子上是砂岩，恶地上是硬化黏土，石头和沙砾上是沙砾，水面上是木板，所以沙漠村庄得到的是砂岩街道而非土路，道路也不再在地面不是草地的地方消失。两条道路相交时，它们在两者中较低的坡度处会合，因为只有双方都能到达的高度，才不会在二者之间留下台阶。

每座建筑坐落在其所临道路之上一格处，该高度取自已铺好的道路，或在道路尚未建造时根据道路将要整平到的地面预测，因此它的门前台阶落在路面上，门则位于台阶之后。如果某座建筑占地的任何一部分之下需要超过两格的人造地面，它就不会建在那里：它会沿着自己的道路滑动最多十二格，寻找最浅的落脚处，如果找不到就整个舍弃，所以建在崎岖地面上的村庄会变得更稀疏，而不是悬空架在那里。建筑周围的一圈在下坡一侧被垫高，在上坡一侧被削低，再往外一圈则又浅一格。

农田保持原版自己的地面高度。路灯立柱立在它们所照亮的道路的坡度上，而不是路旁的路肩上，在道路高出路边之处，其下会填上地面；原版自己的火把柱不再出现在布局中，因为这些路灯取而代之。每座建筑下方的地面会向下填到最近的承托面，使用与其所在之处相同的材质，墙壁和门洞从山坡中开凿出来，屋顶上的泥土被清走，结构中任何一棵树都会被整棵砍倒，树叶随木头一同消失，而仍属于某根留存树枝的每一片树叶则保持不动。林地府邸和零散的特征结构（神殿、小屋、冰屋）在放置之前也要满足同样的平地标准。

它在地形生成时就重塑地形本身，因此开启它生成的世界与不开启时生成的不同，现代版本也带有同样的警告，而且除非资源包或配置要求，否则它是关闭的。

### 基岩

*各分组的作用*

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

| 设置                                | 类型                      | 默认值                      | 作用                                                                                                                                                                    |
| ----------------------------------- | ------------------------- | --------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `flatBedrock`                       | 布尔值                    | `false`                     | 用平整的层替换世界底部参差不齐的基岩。除非开启 `flatBedrockRetrogen`，否则只对新区块生效                                                                                |
| `flatBedrockRetrogen`               | 布尔值                    | `false`                     | 同时也把已存在区块中的基岩铲平。每个区块只处理一次并记住，且无法撤销：原来的图案没有在任何地方记录                                                                       |
| `bedrockLayers`                     | 整数                      | `1`                         | 保留多少层基岩                                                                                                                                                          |
| `flatBedrockRoof`                   | 布尔值                    | `false`                     | 如果维度有基岩顶（例如下界顶部），也把它铲平                                                                                                                            |
| `flatBedrockFiller`                 | 方块                      | 空                          | 用什么替换被移除的基岩。留空则按维度选择：石头、下界岩、末地石                                                                                                          |
| `flatBedrockFillers`                | `dimension=block` 的列表  | 下界和末地的默认值          | 按维度指定的填充物，对所列维度覆盖 `flatBedrockFiller`                                                                                                                  |
| `flatBedrockDimensions`             | 整数列表                  | `0`，主世界                 | 在哪些维度中铲平。留空表示所有维度                                                                                                                                      |
| `flatBedrockDimensionsAreBlacklist` | 布尔值                    | `false`                     | 开启时，铲平会跳过所列维度。关闭时，只对它们生效                                                                                                                        |
| `flatBedrockBiomes`                 | 生物群系名称列表          | 无                          | 在哪些生物群系中铲平，可用通用名称或注册名。留空表示所有生物群系                                                                                                        |
| `flatBedrockBiomeTypes`             | 字典类型列表              | 无                          | 与 `flatBedrockBiomes` 并用的、需要铲平的生物群系字典类型。`OCEAN`、`RIVER`、`MOUNTAIN` 等等                                                                             |
| `flatBedrockBiomesAreBlacklist`     | 布尔值                    | `false`                     | 开启时，铲平会跳过所列生物群系。关闭时，只对它们生效                                                                                                                    |

`flatBedrock` 用平整的层替换参差不齐的那一层，可按维度和生物群系分别设置，填充方块由你选择。`flatBedrockRetrogen` 对已存在的区块也这样做。它无法撤销，原来的图案没有在任何地方记录。`bedrockLayers` 设定保留多少层，`flatBedrockRoof` 在维度有基岩顶时也处理顶部，`flatBedrockFiller` 是替换被移除基岩的方块，留空则按维度选择，也可以改用 `flatBedrockFillers` 为每个维度指定一种。作用于哪些维度和生物群系由 `flatBedrockDimensions`、`flatBedrockBiomes` 和 `flatBedrockBiomeTypes` 决定，`flatBedrockDimensionsAreBlacklist` 和 `flatBedrockBiomesAreBlacklist` 则把这些列表变成排除列表。

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

实体是服务器最大的开销，而其中大多数离任何玩家都很远。`slowDistantEntities` 让 `slowDistance` 格内没有玩家的区块每 `slowRate` 个刻只更新一刻，所以其中的东西仍然会移动、漂浮、燃烧和消失，只是节奏更慢。没有任何东西会被完全搁置不更新。

| 键                    | 必需 | 值                                           | 默认值                | 作用                                                                                                                                          |
| --------------------- | ---- | -------------------------------------------- | --------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `slowDistantEntities` | 否   | 布尔值                                       | `true`                | 是否减慢任何东西                                                                                                                              |
| `slowedKinds`         | 否   | `items`、`experience`、`projectiles` 的列表  | `{items, experience}` | 哪些种类被减少刻更新。任何能自行思考的东西总是改为被减慢，且不在此处列出。机器永远不会被减慢                                                  |
| `slowDistance`        | 否   | 整数，64 及以上                              | `192`                 | 距离最近的玩家多远，区块才会被减慢                                                                                                            |
| `slowRate`            | 否   | 整数，1 到 20                                | `4`                   | 被减慢的区块每这么多刻获得一刻更新。`1` 表示不减慢任何东西                                                                                    |
| `neverSlowed`         | 否   | 实体名称列表                                 | 无                    | 无论离得多远都不受影响                                                                                                                        |
| `slowRecheck`         | 否   | 整数，1 到 100                               | `20`                  | 多久重新计算一次到最近玩家的距离                                                                                                              |

任何能自行思考的东西，每一只生物、动物、村民和傀儡，无论来自哪个模组，都与其余的区别对待，且完全不在 `slowedKinds` 中列出。它们永远不会被减少刻更新，因为玩家可以看着它们行走。取而代之的是，它们每一刻都照常更新，但思考的频率降低：负责决定下一步做什么的那部分思维（这也是它最耗费资源的部分）每 `slowRate` 刻才询问一次，而不是每三刻一次。它照样移动、下落、溺水、燃烧和寻路，与原本完全一样，只是在没有人靠近时更少改变主意。没有什么可看到的，没有卡顿，也没有追赶，而且玩家走近时，它在进入视野之前就已恢复到平常的状态。因为无法被察觉，所以这不是一个可选项：只要减慢功能开启，它就会发生。

被减少刻更新的东西仍以正常的速度老化。掉落物和经验球各自带有一个决定何时消失的计数器，在被减慢的区块不处理的那一刻，该计数器照样向前推进。所以掉落物仍然在地上停留五分钟而不是二十分钟。减少的只是它每刻所做的事，绝不是它能存在多久。

被某种东西刻意保持加载的区块永远不会被减慢，无论离得多远。那些就是区块加载器所维持的区块，而维持它们的全部意义在于其中的东西继续运转，所以主人不在时仍在运转的农场会按它建造时的速度工作。世界出生点周围的区块不属于此类，因为没有任何东西要求保留它们，所以它们和其他地方一样被减慢。

整个区块要么一起被减慢，要么一起不被减慢，所以其中的东西仍然表现得合乎情理：掉落物落在同一堆里，生物仍然跟随身旁的那一只。每位玩家都只为自己计算，所以独自在外的人无论身在何处，周围都有安静的空间。被骑乘、被命名、被驯服、被拴住、发光、被阻止消失、带有效果，或已在追逐玩家的东西，无论离得多远都不受影响，所有机器也一样。它适用于每个世界，包括模组添加的世界。

### 观察区块处理

*各分组的作用*

开启 `worldgenDebug` 后，每一百轮会输出一行，说明世界如何花费区块处理的工作量：新生成了多少区块，有多少区块在被放走之后又要取回，其中多少是从磁盘读取而不是从仍在等待写入的队列中取出，打开了多少区域文件以及它们被同时全部关闭了多少次，以及任一时刻持有区块数和未完成写入数的最大值。它是为判断生成陆地的时间是花在生成上，还是花在把同一块地面取回上而写的，所以值得在大规模预生成之前开启，之后关闭。

随后还有三行：一行是把区块写回存储，一行是区块的光照计算，一行把陆地本身的生成拆分为地面、游戏附加其上的装饰，以及各个模组附加其上的装饰，并列出最慢的五个名字。这样，一个卡顿的世界就可以被解读为四项独立的开销，而不是一项，并且能指出具体是哪个模组，而不必靠猜。

### 提前造陆

*各分组的作用*

篇幅大到有自己独立的一节，参见[预生成](#预生成)。

### 等待轮到的方块

*各分组的作用*

水的蔓延、熔岩的冷却和作物的生长，都是方块等待一段时间后才做某件事，而游戏把它们全都放在同一个堆里。每次写出一个区块时，它都要从头到尾遍历整个堆，寻找属于该区块的那几个，所以世界里这类方块越多，每次写入就越慢，无论正在写入的区块本身有没有这样的方块。现在它们按所在区块排序，而一旦堆发生变化或回合推进，这个排序就被丢弃并重做，所以写出一个区块时只需查看与它有关的那一小撮。

### 为区块内的方块扩充空间

*各分组的作用*

区块以切片形式保存，每个切片都持有其中方块种类的一个列表，起初有容纳十六种的空间。超过十六种就意味着要建一个更大的列表，并把切片中四千个方块逐一复制过去，到三十二种时再来一次，到六十四种时又来一次。含有几种石头和矿石的地面会越过所有这些门槛，所以为了一点点空间要做四遍。现在它在第一次空间用尽时就直接跳到这些尺寸中最大的一个，也就是一次复制取代四次，代价是每个切片多出几千字节，而这些空间转眼就会被用上。

### 准备待写入的区块

*各分组的作用*

区块在写出之前，要先转换成写到磁盘上的形式，这需要遍历它的每一个方块，并按名称在一张表里逐个查找。地面是由大段相同的东西构成的，所以同一块石头的同一次查找要做上成千上万遍，现在只需把上一次的答案保留下来，在下一个方块相同时直接复用。这不是一个可以关闭的功能，因为没有什么需要权衡的：无论怎样，答案都是一样的。

### 写出区块

*各分组的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "hurryWritesAbove": 100
  }
}
```

| 设置               | 类型        | 默认值  | 作用                                                                                                                                                                                                             |
| ------------------ | ----------- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hurryWritesAbove` | 整数，区块  | `100`   | 等待写入的已完成区块达到多少时，写入线程不再在每写完一个后休息百分之一秒，而是尽可能快地写。`0` 让它始终保持休息，与游戏本身一致                                                                                  |

游戏在自己的线程上一次写出一个已完成的区块，每写完一个就休息百分之一秒。这使它无论磁盘多快都被限制在每秒约一百个区块，在有人游玩时绰绰有余，但在批量造陆时远远不够，于是未写入的区块就堆积在内存里。`hurryWritesAbove` 规定等待的区块数达到多少时，它不再休息，而是尽可能快地写。`100` 是默认值，与游戏本身开始压住生成的那个临界点一致；`0` 让它始终保持休息，与游戏本身一致。在等待数量较少时什么都不会改变，而这正是游玩中的每一个寻常时刻。

每次清理运行时，都会即时为它写一行，指明运行的是哪个清扫器、耗时多久、清理前后持有多少，以及当时游戏拥有多少空间。如果这个空间发生变化，会特别说明，因为空间的增长本身就是这类停顿中最长的那些的成因：启动时空间少于最终所需的游戏，会在与它正在做的事毫无关系的时刻反复停下来扩充。启动时就给它允许的最大空间，可以完全避免这一点。

最后一行说明自上次查看以来丢弃了多少工作废料、清理它花了多长时间、共清扫了多少次，以及游戏当前持有它被允许的空间中的多少。造陆本身就会丢弃大量东西，因为每个区块在写出之前都被转换成全新的数组，而这种清理发生在回合之间而不是回合之中，所以它表现为一次卡顿，而不是体现在上面任何一项计数的耗时里。

### 出生点区块

*各分组的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["0=64", "7=0"]
  }
}
```

| 设置               | 类型                       | 默认值  | 作用                                                                                                                                                                                                                                                                                                                                                        |
| ------------------ | -------------------------- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `spawnChunkRadius` | 整数，格                   | `128`   | 距离世界出生点多远（以格计）的区块无论是否有人在场都保持加载。B 格会从出生点区块向每个方向保持 `r = (B + 8) / 16` 个区块，共 `(2r+1)²` 个，世界在启动时会在其周围准备 `(2r+9)²` 个区块。`128` 就是游戏的做法，保持 289 个、准备 625 个，`0` 则不保持也不准备任何区块                                                                                         |
| `spawnChunkRadii`  | `dimension=blocks` 的列表  | 无      | 一次为一个维度设定半径，对所列维度覆盖 `spawnChunkRadius`                                                                                                                                                                                                                                                                                                   |

游戏会让世界出生点周围的区块无论是否有人在场都保持加载，好让各模组有一个始终在更新的地方。它在每个方向上是 128 格，约 289 个区块，而且在游戏中无法调整。`spawnChunkRadius` 设定这个距离。`128` 就是游戏的做法，也是默认值，较小的数字保持较小的锚点，`0` 则完全不保持，于是出生点区域和其他地方一样被卸载。`spawnChunkRadii` 一次为一个维度设定半径，写成 `dimension=blocks`，每行一条，对所列维度覆盖 `spawnChunkRadius`。

只有被注册为保持其出生点的维度才会保持，在游戏本身中那只有主世界，下界和末地从来不保持，所以对它们设置此项不会有任何改变。模组添加的维度只有在该模组提出要求时才保持，而这样做的模组往往带着一份资源包根本不想要的、多出来的 289 个区块。世界是否保持加载是另一回事，此项不会触及：被模组标记为保持加载的维度在 `0` 时仍然保持加载，只是不再保持区块。大多数把出生点当作锚点的模组想要的是那里有点什么，而不是 289 个区块的量，所以一个小数字通常能让它们继续工作，而 `0` 则不行。

### 虚空世界

*各分组的作用*

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

| 设置                              | 类型         | 默认值             | 作用                                                                                                                                                 |
| --------------------------------- | ------------ | ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| `voidWorld`                       | 布尔值       | `false`            | 生成一个空世界，在出生点放一个平台，并阻止生物、动物、结构以及模组本来会在那里生成的一切                                                             |
| `voidPlatformBlock`               | 方块         | `minecraft:stone`  | 平台由什么构成                                                                                                                                       |
| `voidPlatformSize`                | 整数，格     | `9`                | 平台有多宽，向下取整为奇数，使它以出生点为中心                                                                                                       |
| `voidPlatformHeight`              | 整数         | `64`               | 平台位于世界底部之上多高的位置                                                                                                                       |
| `voidWorldDimensions`             | 整数列表     | `0`，主世界        | 哪些维度被清空。只有主世界会得到平台                                                                                                                 |
| `voidWorldDimensionsAreBlacklist` | 布尔值       | `false`            | 开启时，所列维度是被保持原样的那些                                                                                                                   |

`voidWorld` 生成一个空世界，在出生点放一个平台，并阻止生物、动物、结构以及模组本来会在那里生成的一切。平台的方块、大小和高度是 `voidPlatformBlock`、`voidPlatformSize` 和 `voidPlatformHeight`；大小向下取整为奇数格，使平台以出生点为中心。`voidWorldDimensions` 选择清空哪些世界，默认只有主世界，`voidWorldDimensionsAreBlacklist` 则把该列表变成要保持原样的那些。下界和末地的清空方式与主世界相同，无论它们是此版本自己构建的，还是被某个模组替换后的版本。只有主世界会得到平台，所以通往被清空的下界或末地的通道，需要资源包自己提供。被清空的末地也没有末影龙、末影水晶和基岩喷泉，因为构建它们的战斗根本没有开始。

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

| 设置          | 类型   | 默认值  | 作用                                                                                                                                                                                            |
| ------------- | ------ | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `dragonFight` | 布尔值 | `true`  | 整件事是否发生：末影龙、它的血条、末影水晶、它所站立的喷泉，以及玩家用末地水晶发起的重生。属于 `structures` 分组 |

`dragonFight` 属于 `structures` 分组，决定整件事是否发生：末影龙、它的血条、末影水晶、它所站立的喷泉，以及玩家用末地水晶发起的重生。被清空的末地除非资源包要求，否则不含这些，而普通的末地除非资源包另有规定，否则就有这些，所以无论哪种情况，`dragonFight` 都值得设置。

### 地形

*各分组的作用*

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

| 设置                            | 类型                | 默认值                           | 作用                                                                                                                                                                                                                                                                                                                                                                                           |
| ------------------------------- | ------------------- | -------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldType`                     | 字符串              | 空                               | 每个新世界所采用的世界类型，无论在创建它的界面上选了什么：`default`、`largebiomes`、`amplified`、`customized`，或模组添加的某个类型。专用服务器会在世界加载之前把它作为 `level-type` 写入 `server.properties`，除非 `level-type` 已经指定了某个 `worldTypeExceptions` 中的类型。留空则把选择权交给创建世界的人 |
| `worldTypeExceptions`           | 世界类型列表        | `flat`、`debug_all_block_states` | `worldType` 放行的那些选项                                                                                                                                                                                                                                                                                                                                                                     |
| `worldSeed`                     | 字符串              | 空                               | 每个新世界所采用的种子，按键入时的写法书写：数字原样使用，其他内容则按游戏的方式转换成数字。专用服务器会在世界加载之前把它作为 `level-seed` 写入 `server.properties`                                                                                                                                                                                                                            |
| `terrainWorldTypes`             | 世界类型列表        | 无                               | 地形设置究竟应用于哪些世界类型。留空表示所有类型                                                                                                                                                                                                                                                                                                                                               |
| `terrainWorldTypesAreBlacklist` | 布尔值              | `false`                          | 开启时，所列的世界类型是被保持原样的那些                                                                                                                                                                                                                                                                                                                                                       |

`worldType` 决定新世界属于哪种世界，无论在创建它的界面上选了什么，`default`、`largebiomes`、`amplified`、`customized`，或模组添加的某个类型，例如 `biomesop` 或 `realistic`。围绕某一种世界类型构建的资源包在此指定它，每个新世界就都以这种方式创建。留空（默认）则把选择权交给创建世界的人。已存在的世界保持它被创建时的类型，而没有任何东西提供的名称会被记入日志并忽略。`worldTypeExceptions` 指定被放行的选项，起初是超平坦和调试世界，因为想要统一世界类型的资源包很少打算夺走正在测试的人手中的超平坦，并且创建世界的人在进入世界后会在聊天中被告知一次：该资源包选择了其类型。这条消息由配置文件通过 `tellWorldType` 决定，而不是由资源包决定，所以游玩的人可以为自己关闭它，任何资源包都无法把它重新打开。更改类型时，创建世界时所用的设置会被丢弃，因为它们是为之前选择的类型写的。

`worldSeed` 决定每个新世界所采用的种子，无论在创建它的界面上键入了什么。它按键入时的写法书写：数字原样使用，其他内容则按游戏把一个词转换成数字的方式转换，所以 `Hollow Ridge` 和 `-4172144997902289642` 都可以，且总是得到同一个世界。留空（默认）则把选择权交给创建世界的人。已存在的世界保持它被创建时的种子，所以这只决定新世界得到什么。围绕某一张地图构建的资源包在此指定它的种子，用该资源包创建的每个世界就都是那张地图。

`generatorOptions` 塑造主世界本身，包括海平面、熔岩海洋和每一种地形噪声，格式与自定义世界类型所写的相同。它在世界创建时应用，此后不再应用，所以已存在的世界保持原样。已经带有自身选项的世界保留这些选项，日志会指出所使用的字符串。专用服务器会在世界加载之前把它们作为 `generator-settings` 写入 `server.properties`。

自带设置而从不查看世界设置的世界类型，例如 Quark 的 realistic，会得到合并进它自己设置中的资源包设置，所以除非资源包要求别的，它原本被构建出的形态得以保留。

`terrainWorldTypes` 指定设置究竟应用于哪些世界类型，`default`、`customized`、`biomesop`、`realistic` 等等，`terrainWorldTypesAreBlacklist` 则把它变成要保持原样的列表。留空（默认）表示所有世界类型。塑造普通世界、却想让某个模组的世界类型保持该模组原样的资源包，在此指定它即可：不合并任何内容，不交出任何东西，该模组自己的自定义界面也保持开启。名称与世界创建时所用的世界类型进行匹配，所以指定一个此处没有提供的名称，只是永远不会匹配，不会产生任何代价。

下面关于 Biomes O' Plenty 的一切，只有在安装了该模组时才会发生，因为这项工作由仅在其存在时才加载的兼容代码完成。没有它就没有 `biomesop` 世界类型可选，指定了它的资源包会得到世界实际创建时所用的世界类型。

在 Biomes O' Plenty 世界上，同样的设置会被转换成该模组所读取的措辞，所以资源包不需要另备一份。`biomeSize` 变成它的五种大小之一，噪声和缩放设置原样传递，凡是它从不读取的内容都会被省略，并在日志中留一行说明。该模组读取的内容远少于自定义世界类型，并且完全不从它的设置中读取海平面、洞穴、湖泊或结构开关，所以这些会被直接交给它，资源包设置它们的方式与任何其他世界相同。

有两件事它自行决定。河流出自它自己的图层，没有设置，所以 `riverSize` 在那里毫无意义。而海洋、山脉和区域究竟位于何处，也出自它的图层，只能通过 `landScheme`、`tempScheme`、`rainScheme` 和 `biomeSize` 影响，所以资源包是以该模组的方式而不是自定义世界类型的方式塑造那个世界。单一生物群系的世界仍然可以由资源包制作：屏蔽所有生物群系，并把你想要的那个设为模板的 `default`，这在它的世界类型上与在任何其他世界类型上的效果相同。

资源包所做的其他一切，包括屏蔽生物群系和矿石、替换方块、平整基岩、结构放置、它自己的世界生成，从来都不经过那个字符串，在任何世界类型上的效果都相同。

### 服务器

*各分组的作用*

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

`control.server` 决定这个分组：资源包可以设置的 `server.properties` 中的行，以及游戏模式、难度和对局域网开放的世界上的命令。在专用服务器上，资源包在此设置的每个值都会在服务器启动时写入 `server.properties`，所以该文件会写明当前生效的内容，服务器已经读取过的那些也会同时被设置。单人世界采用集成服务器所拥有的值，各行说明如下。留空，或数字取 `-1`，则保留服务器自身的值，而当 `control.server` 为 `off` 时，每一行都保持服务器原有的样子。

| 设置                   | 类型                                                           | 默认值  | 作用                                                                                                                                                                                                                                                                                                                                                                               |
| ---------------------- | -------------------------------------------------------------- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldGameMode`        | `survival`、`hardcore`、`creative`、`adventure` 或 `spectator` | 空      | 每个新世界开始时所处的模式，仅在单人游戏中于创建时应用。专用服务器每次启动时都会把每个世界设为其 `server.properties` 中的模式，所以在那里，资源包的模式会在世界加载之前写入 `server.properties`（`gamemode` 和 `hardcore`）。`hardcore` 是生存模式加上全存档范围的极限模式标志，而 `creative` 同时会启用作弊 |
| `worldLanCommands`     | 布尔值                                                         | `true`  | 对局域网开放单人世界的玩家，是否可以为所有加入的人开启命令。`false` 会让“对局域网开放”界面的“允许作弊”按钮变灰并保持关闭，无论以何种方式请求（包括 `/publish`），世界都不带命令地开放                                                                                                                                                                                                       |
| `worldDifficulty`      | 列表                                                           | 无      | 把难度锁定为 `peaceful`、`easy`、`normal` 或 `hard`。单独写一个难度适用于每个维度，而 `dimension=difficulty` 行则为那一个维度覆盖它。专用服务器会把主世界的难度作为 `difficulty` 写入 `server.properties`                                                                                                                                                                         |
| `worldForceGameMode`   | 布尔值                                                         | 空      | 加入的玩家是否每次都被放回服务器的游戏模式，即 `force-gamemode` 行。对局域网开放的单人世界也采用它                                                                                                                                                                                                                                                                                 |
| `worldPvp`             | 布尔值                                                         | 空      | 玩家之间能否互相伤害，即 `pvp` 行。单人世界也采用它                                                                                                                                                                                                                                                                                                                                |
| `worldFlight`          | 布尔值                                                         | 空      | 在生存模式中飞行的玩家是否被放过而不是被踢出，即 `allow-flight` 行。单人世界也采用它                                                                                                                                                                                                                                                                                               |
| `worldSpawnProtection` | 整数，-1 或更大                                                | `-1`    | 出生点周围多少格内只有管理员可以建造，即 `spawn-protection` 行，0 表示没有。只有专用服务器会保护其出生点                                                                                                                                                                                                                                                                           |
| `worldNether`          | 布尔值                                                         | 空      | 下界能否进入，即 `allow-nether` 行。`false` 在单人世界中也会将其关闭                                                                                                                                                                                                                                                                                                               |
| `worldCommandBlocks`   | 布尔值                                                         | 空      | 命令方块是否运行，即 `enable-command-block` 行。单人世界本来就会运行它们，`false` 在那里也会将其关闭                                                                                                                                                                                                                                                                               |
| `worldIdleTimeout`     | 整数，-1 或更大                                                | `-1`    | 玩家闲置多少分钟后被踢出，即 `player-idle-timeout` 行，0 表示永不。单人世界也采用它                                                                                                                                                                                                                                                                                                |
| `worldMotd`            | 文本                                                           | 空      | 服务器列表中显示在服务器名称下方的那一行，即 `motd` 行。对局域网开放的单人世界会用它取代房主和世界名称显示                                                                                                                                                                                                                                                                         |
| `worldMaxSize`         | 整数，-1 到 29999984                                           | `-1`    | 世界边界最远可以延伸到距中心多少格，即 `max-world-size` 行。单人世界也采用它                                                                                                                                                                                                                                                                                                       |
| `worldStructures`      | 布尔值                                                         | 空      | 新世界是否生成结构，即 `generate-structures` 行以及世界界面上的“生成结构”选项。仅在世界创建时应用                                                                                                                                                                                                                                                                                   |
| `worldSpawnMonsters`   | 布尔值                                                         | 空      | 敌对生物是否生成，即 `spawn-monsters` 行。`false` 在单人世界中也会阻止它们                                                                                                                                                                                                                                                                                                         |
| `worldSpawnAnimals`    | 布尔值                                                         | 空      | 动物是否生成，即 `spawn-animals` 行。单人世界也采用它                                                                                                                                                                                                                                                                                                                              |
| `worldSpawnNpcs`       | 布尔值                                                         | 空      | 村民是否生成，即 `spawn-npcs` 行。单人世界也采用它                                                                                                                                                                                                                                                                                                                                 |
| `worldViewDistance`    | 整数，-1 到 32                                                 | `-1`    | 专用服务器向每位玩家发送多少个区块远的世界，即 `view-distance` 行。单人世界则遵循渲染距离                                                                                                                                                                                                                                                                                          |
| `worldBuildHeight`     | 整数，-1 到 256                                                | `-1`    | 方块可以放置的最高 y，即 `max-build-height` 行，取整为 64 到 256 之间 16 的倍数。单人世界也采用它                                                                                                                                                                                                                                                                                  |

**`worldGameMode`**（`server` 分组）：`survival`、`hardcore`、`creative`、`adventure` 或 `spectator`。仅在创建世界时应用；已存在的世界不受影响，之后更改模式也不会被干预。`hardcore` 是生存模式加上原版的全存档极限模式标志；`creative` 同时会启用作弊，就像创建界面的复选框那样。创建界面打开时已预选该模式（以及资源包的种子）；玩家可以在那里更改，但资源包会在创建时把它设回去。`adventure` 和 `spectator` 在该界面上不提供，而是在世界创建时直接应用。

**`worldLanCommands`**（`server` 分组）：`true`（默认）让“对局域网开放”界面保持原版的样子。`false` 会让它的“允许作弊”按钮变灰并保持关闭，所以对局域网开放世界的玩家无法把命令交给所有加入的人。无论是谁请求，包括 `/publish`，世界在服务端也都不带命令地开放。它只管辖对局域网开放；专用服务器不受影响。

**`worldDifficulty`**（`server` 分组）：`peaceful`、`easy`、`normal` 或 `hard`。单独写一个值适用于每个维度；`dimension=difficulty` 行（`-1=hard`）按维度覆盖。该锁定对暂停菜单同样有效。留空（默认）则把难度留给玩家。

### 日志记录

*各分组的作用*

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

| 设置                   | 类型   | 默认值  | 作用                                                                  |
| ---------------------- | ------ | ------- | --------------------------------------------------------------------- |
| `logBlockedOres`       | 布尔值 | `true`  | 记录每个模组和每种矿石类型第一次被拒绝的情况                          |
| `logBlockedBiomes`     | 布尔值 | `true`  | 按模组记录有多少生物群系被屏蔽                                        |
| `logBlockedGenerators` | 布尔值 | `true`  | 记录每个模组和每个生成器第一次被屏蔽的情况                            |
| `logBlockedRecipes`    | 布尔值 | `true`  | 按模组记录有多少内容被屏蔽                                            |
| `logBlockReplacements` | 布尔值 | `true`  | 记录每项替换第一次发生的情况，并在世界追赶完成时记录总数              |

`logBlockedOres`、`logBlockedBiomes`、`logBlockedRecipes` 和 `logBlockReplacements` 各自记录某样东西第一次被拒绝的情况，这样你就能看到屏蔽规则究竟拦下了什么，而不必从缺少了什么去猜。当某条规则似乎毫无作用或作用过头时，它们是最先该开启的东西。

### 配方

*各分组的作用*

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

| 设置                  | 类型                         | 默认值      | 作用                                                                                                                                                                                                                     |
| --------------------- | ---------------------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `blockRecipes`        | 布尔值                       | `false`     | 移除除 `recipeWhitelist` 中模组之外的所有合成配方。默认没有任何豁免，所以请列出你自己资源包的命名空间以保留其配方。CraftTweaker 和 GroovyScript 添加的配方始终保留 |
| `recipeWhitelist`     | 模组 id 列表                 | `minecraft` | 合成配方得以保留的模组                                                                                                                                                                                                   |
| `blockedRecipeMods`   | 模组 id 列表                 | 无          | 其合成配方被彻底移除的模组，无论白名单如何规定                                                                                                                                                                           |
| `blockFurnaceRecipes` | 布尔值                       | `false`     | 熔炉配方的同类设置，模组按产出的物品判定                                                                                                                                                                                 |
| `furnaceWhitelist`    | 模组 id 列表                 | `minecraft` | 熔炉配方得以保留的模组                                                                                                                                                                                                   |
| `blockedFurnaceMods`  | 模组 id 列表                 | 无          | 其熔炉配方被彻底移除的模组                                                                                                                                                                                               |
| `recipeMatch`         | `recipe`、`output` 或 `both` | `recipe`    | 屏蔽合成配方时从哪里读取模组 id：配方自身的名称、它所产出的物品，或二者皆可，二者皆可时任一匹配就屏蔽，任一在白名单中就放过                                                                                              |

`blockRecipes` 和 `blockFurnaceRecipes` 移除除各自白名单中的模组之外的一切。默认没有任何豁免，所以请列出你自己资源包的命名空间以保留其配方。CraftTweaker 和 GroovyScript 添加的配方始终保留，无论白名单如何规定。白名单是 `recipeWhitelist` 和 `furnaceWhitelist`；`blockedRecipeMods` 和 `blockedFurnaceMods` 则反其道而行，无论白名单如何规定，都移除所指定模组的配方。`recipeMatch` 决定屏蔽合成配方时从哪里读取模组 id：默认的 `recipe` 使用配方自身的名称，`output` 使用它所产出的物品，`both` 则在任一匹配时屏蔽，在任一位于白名单中时放过。

---

# 其他模组

## Universal Tweaks

*其他模组*

Universal Tweaks 与本模组的若干原版调整有重叠。在重叠之处，本模组会退让（每次都记入日志，指明跳过了什么），而不是让两个模组去编辑同一个方法。

| 重叠的内容           | 本模组何时退让                            |
| -------------------- | ----------------------------------------- |
| `promptLeafDecay`    | Universal Tweaks 开启了 `Fast Leaf Decay` |
| `lenientPaths`       | Universal Tweaks 开启了 `Lenient Paths`   |
| `cactusMaxHeight`    | 安装了 Universal Tweaks                   |
| `caneMaxHeight`      | 安装了 Universal Tweaks                   |
| 下界传送门返回       | 安装了 Universal Tweaks                   |

前两项从 `config/Universal Tweaks - Tweaks.cfg` 中读取 Universal Tweaks 自己的开关，所以在那里关闭其中一项，就把这项工作交还给本模组。高度这一对没有可读取的开关，只有 `Cactus Size` 和 `Sugar Cane Size`，所以只要存在 Universal Tweaks，本模组就一律退让，你需要改在那里设置高度。

**下界传送门返回**：本模组会记录你进入下界的位置并把你送回那里，而不是采用原版对最近传送门的搜索。Universal Tweaks 有自己的处理方式，所以安装它时这一项会被完全跳过。

**以上内容都不会影响资源包。** 上面所说的一切都是关于 Minecraft 自己的仙人掌、甘蔗、树叶、小径和传送门。你的资源包所定义的方块自带它们自己的行为，而 `portals/*.json` 下的资源包传送门是 Universal Tweaks 从未见过的独立系统。

## Mo' Villages

*其他模组*

Mo' Villages 添加村庄生物群系并替换村庄材料，这两件事资源包同样可以设置。与 Universal Tweaks 的重叠不同，这里由资源包保留最终决定权。

| 重叠的内容                      | 会发生什么                                                                                                                                                              |
| ------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 村庄的 `structureSpacing`       | Mo' Villages 在本模组询问之后，根据 `villageDistance` 设定自己的间距。如果资源包指定了间距，本模组会把它的数字改回去，并在日志中说明一次                                 |
| `villageBlocks`                 | Mo' Villages 按生物群系替换村庄材料，并把这次替换标记为最终。资源包的映射在那之后才应用，所以资源包胜出                                                                 |
| 村庄的 `structureBiomes`        | Mo' Villages 把它的生物群系加入游戏自己的列表。资源包的白名单仍然决定哪些得以保留                                                                                       |

这里没有任何东西需要开启。如果资源包既没有指定间距也没有指定方块映射，Mo' Villages 就任其随意行事。

两个模组都安装时，有两点值得知道。Mo' Villages 也会设置 `minTownSeparation`，这在 1.12 中完全不起作用：该字段只被写入一次，游戏和本模组都从不读取。另外，村庄的方块在 `villageBlocks` 运行之前就已由 Mo' Villages 按生物群系决定，所以同时映射原方块和 Mo' Villages 替换成的方块，就能无论哪种情况都捕获到村庄，即同时写 `minecraft:cobblestone=...` 和 `minecraft:brick_block=...`。

## CoFH World

*其他模组*

要求 CoFH World 的模组可以在没有它的情况下加载，这项要求会被自动移除，但真正调用其 API 而会崩溃的模组除外。

这样一来它们自己的生成不会发生，因为读取它们 `assets/<modid>/world/*.json` 的正是 CoFH World。资源包需要自行弥补这一点。

否则，`readCofhWorldFiles` 会直接从模组 jar 中读取这些文件，并通过本模组生成它们。它默认关闭，并在安装了真正的 CoFH World 时退让，那时生成照常进行。每一个会产出东西的 CoFH 生成器和分布都会被转换，映射到上面的形状和扩散上。这些形状是本模组自己的几何，所以湖泊或尖塔看起来不会完全相同。加权结构列表、旋转与镜像表、忽略方块列表以及石笋的收束都会一并沿用。收束是按形状而不是按公式匹配的，所以尖塔的轮廓接近但不完全相同。

把这些文件翻译成资源包是受支持的途径，也是改变它们所生成内容的唯一方式。

## Lost Cities

*其他模组*

Lost Cities 用自己的生成器取代了主世界生成器，所以任何接入普通生成器的东西在它的世界上都会失效。仅在安装了 Lost Cities 时才加载的兼容代码带来了三样东西：

- `generatorOptions` 塑造城市之间和城市之下的陆地。Lost Cities 只读取噪声设置，所以地面高度、水位、洞穴、湖泊和结构开关都出自它自己的配置档案，`seaLevel` 在它的世界上不起作用；日志中的摘要会这样说明。`terrainWorldTypes` 像对待其他类型一样对它进行限制，以 `lostcities` 匹配。
- 虚空世界可以工作，包括由完全屏蔽的生物群系列表所带来的那种。城市和陆地都消失了，平台和出生点的表现与其他任何地方相同。
- 资源包生物群系的 `stoneBlock` 会替换其下方的石头，适用于 Lost Cities 拥有的每一种地貌类型：普通、浮空、太空和洞穴。

城市本身不归本模组改动。它们有多大、多常见，建筑由什么构成，地面和水位，这一切都在 Lost Cities 自己位于 `config/lostcities` 下的配置档案文件中，它的建筑 JSON 也通过同一处自己的 `assets` 设置加载。携带 Lost Cities 世界的资源包要把这些文件一并带上，就像携带任何其他模组的配置一样。

把 `worldType` 设为 `lostcities`，会让每个新世界都成为 Lost Cities 世界，与让它成为 `biomesop` 或 `realistic` 的方式相同。强制指定类型会丢弃世界原本会带有的设置，所以世界会落在 Lost Cities 的默认配置档案上，而 `config/lostcities/general.cfg` 中的 `defaultProfile` 指明那是哪一个。反过来，强制指定另一种类型的资源包，会把 Lost Cities 从选了它的玩家那里夺走，所以打算把这一选择留给玩家的资源包，应把 `lostcities` 加入 `worldTypeExceptions`。

其余一切从一开始就没有经过生成器，与其他地方的工作方式相同：资源包的世界生成、矿石和生物群系屏蔽、结构间距和刷怪笼、平整基岩、回溯生成、预生成，以及它的两张箱子战利品表，都像其他战利品表一样可以覆盖和注入。

## Blast Plaster 集成

*其他模组*

`<namespace>/blastplaster/*.json`

文件名由你自己选择，只读取这个文件夹，多个文件会叠加。

Blast Plaster（本模组的依赖）负责爆炸后的行为：逐方块修复弹坑、感知树木的砍伐、掉落物控制。它自身只读取一份全局配置。由资源包驱动时，它按**维度**作答，由资源包提供决定，而不是要求玩家去编辑配置。村庄的树木砍伐也复用它的树木几何，这就是为什么新道路上方的树会整棵倒下。没有资源包文件时，Blast Plaster 的行为与单独安装时完全一样。

写在文件顶层的键适用于所有地方；`dimensions` 块按维度 id 为某一个维度覆盖它们。资源包从未提及的任何内容都保持 Blast Plaster 自己配置所说的样子，所以资源包只需设置它在意的那几项，其余不动。

每个键一次全部展示。真实的文件只写其中需要的那些。

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

`explosionMode` 是首要开关：`HEAL` 随时间推移恢复弹坑，`EJECT_DROPS` 留下坑洞并掉落大约三分之一的方块（原版行为），`VISUAL_TOSS` 留下坑洞且不掉落任何东西。由资源包驱动时，默认值是 `EJECT_DROPS`（而不是 Blast Plaster 的 `HEAL`），所以未经配置的安装表现得与原版一样。

| 键                                                                                                          | 值                                   | 作用                                                                                                     |
| ----------------------------------------------------------------------------------------------------------- | ------------------------------------ | -------------------------------------------------------------------------------------------------------- |
| `explosionMode`                                                                                             | `HEAL`、`EJECT_DROPS`、`VISUAL_TOSS` | 爆炸之后会发生什么                                                                                       |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll`                                                 | true 或 false                        | 究竟处理哪些爆炸                                                                                         |
| `processPlayerIgnitedTNT`                                                                                   | true 或 false                        | 玩家点燃的 TNT 是否与其余的一并处理                                                                      |
| `customEntitiesToHeal`                                                                                      | 实体名称列表                         | 来自其他模组的爆炸，写成 `modid:entity`                                                                  |
| `healFullTrees`                                                                                             | true 或 false                        | 被爆炸削到的树会被整棵取走或整棵恢复，而不是被切断一半                                                   |
| `maxTreeSize`                                                                                               | 数字                                 | 一棵树最多可以占用多少方块，超过则放过它                                                                 |
| `minimumTicksBeforeHeal`, `randomTickVar`                                                                   | 数字                                 | 多久之后才开始修复，以及它的节奏有多参差不齐                                                             |
| `overrideBlocks`                                                                                            | true 或 false                        | 修复是否会覆盖此后在坑洞中建造的东西                                                                     |
| `enableFakeTossedBlocks`                                                                                    | true 或 false                        | 从爆炸中飞出的碎片                                                                                       |
| `enableExplosionFlash`                                                                                      | true 或 false                        | 爆炸瞬间的明亮闪光                                                                                       |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | 数字                                 | 闪光持续多久、燃烧得多亮、抛出多少粒子，以及脉冲多少次                                                   |
| `enableExplosionSmoke`                                                                                      | true 或 false                        | 事后升起的烟柱                                                                                           |
| `explosionSmokeDuration`, `explosionSmokeParticleCount`                                                     | 数字                                 | 烟持续多久，以及它有多浓                                                                                 |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks`                                                           | true 或 false                        | 玩家自己的 TNT 会留下什么                                                                                |
| `enableDropSuppression`, `dtSpecialDrops`                                                                   | true 或 false                        | 爆炸中的掉落物，以及 Dynamic Trees 自己的掉落物                                                          |
| `preventMobDrops`                                                                                           | true 或 false                        | 被爆炸杀死的生物是否仍会掉落                                                                             |
| `blockConversions`                                                                                          | 规则列表                             | 被炸的方块变成什么而不是原样回来，使建筑每炸一次就磨损一级                                               |

`blockConversions` 决定被炸的方块变成什么，而不是原样回来。一条规则写成 `<source>=<result>[@chance]`：来源是方块 id、带元数据的方块 id（`minecraft:log:1`），或以 `#` 开头的矿物词典名称；结果是方块 id、带元数据的方块 id，或表示让空间留空的 `nothing`；几率从 0.0 到 1.0，默认为 1.0。第一条匹配的规则胜出，所以具体的规则要写在宽泛的规则之上，而已经是某条规则结果的方块不会再被转换，所以一堵墙每炸一次只退一级，而不会被磨得什么也不剩。

**完全原版的外观：** `EJECT_DROPS`，并且 `healFullTrees`、`enableFakeTossedBlocks`、`enableExplosionFlash`、`enableExplosionSmoke`、`preventMobDrops` 和 `playerTNTAlwaysDrops` 全部关闭。每个键都可以按维度设置。

**原版客户端**看不到任何异常。闪光是唯一会放置方块的功能，所以设置了 `vanillaClients` 时它会被强制关闭；其余一切都是普通客户端能理解的粒子和物品。

不属于资源包键的：Blast Plaster 的调试日志，以及它的原木与树叶配对（树木的识别必须在整个游戏中只有一个答案）。这两项都保留在 Blast Plaster 自己的配置中。

## 墓碑模组

*其他模组*

无需任何设置。`player_loot` 物品会在任何墓碑模组读取之前加入普通的死亡掉落物，所以它们会与物品栏一同进入墓碑，适用于 Gravestone、GraveStone Mod、Corail Tombstone 以及任何读取死亡掉落列表的模组。逐条而言，`dropLoose` 会绕过掉落列表，使物品散落在地上留给击杀者，而不是进入墓碑。键以及 `dropLoose` 的注意事项：[玩家战利品](#玩家战利品)。

---

# 参考

## 值列表

*参考*

这些是解析器接受的名称，凡是上面的表格写着“材料之一”等字样的地方都适用。任何无法识别的内容都会被记入日志并替换为默认值。

### 接受的名称

*值列表*

**方块材料。** `air`、`grass`、`ground`、`wood`、`rock`、`iron`、`anvil`、`water`、`lava`、`leaves`、`plants`、`vine`、`sponge`、`cloth`、`fire`、`sand`、`circuits`、`carpet`、`glass`、`redstone_light`、`tnt`、`coral`、`ice`、`packed_ice`、`snow`、`crafted_snow`、`cactus`、`clay`、`gourd`、`dragon_egg`、`portal`、`cake`、`web`、`piston`、`barrier`、`structure_void`。

**音效类型。** `wood`、`ground`、`plant`、`stone`、`metal`、`glass`、`cloth`、`sand`、`snow`、`ladder`、`anvil`、`slime`。

**地图颜色。** `air`、`grass`、`sand`、`cloth`、`tnt`、`ice`、`iron`、`foliage`、`snow`、`clay`、`dirt`、`stone`、`water`、`wood`、`quartz`、`adobe`、`magenta`、`light_blue`、`yellow`、`lime`、`pink`、`gray`、`silver`、`cyan`、`purple`、`blue`、`brown`、`green`、`red`、`black`、`gold`、`diamond`、`lapis`、`emerald`、`obsidian`、`netherrack`。

**渲染层。** `solid`、`cutout`、`cutout_mipped`、`translucent`。留空时，方块会按其类型选择合适的一种。

**稀有度。** `common`、`uncommon`、`rare`、`epic`。

**火把粒子。** `none`、`flame`、`colored`。`colored` 使用 `particleColor`。

**工具类别。** `pickaxe`、`axe`、`shovel`、`sword`。

**盔甲栏位。** `head` 或 `helmet`，`chest` 或 `chestplate`，`legs` 或 `leggings`，`feet` 或 `boots`。

**着色。** `biome`、`none`，或六位十六进制颜色。定义中任何地方的颜色都是十六进制，开头的 `#` 可有可无。

**行为**，用于 `behavesAs`。`till`、`path`、`bush`、`animals`。

**结构**，用于世界模板，以及 `structures` 分组自己的列表。`villages`、`mineshafts`、`strongholds`、`temples`、`monuments`、`mansions`、`netherbridges`、`endcities`、`caves`、`ravines`，以及 `reccomplex`，它会关闭 Recurrent Complex 自行生成的一切（其天然结构及其装饰替代物），而世界中已经存在的东西不受触动。另有八个名称指的是填充步骤所放置的内容，而不是某个结构生成器：`dungeons`、`waterlakes`、`lavalakes`、`netherlava`、`fire`、`glowstone`、`ice` 和 `animals`。

**生物类型**，用于生物群系的生成与倍率。`creature`、`monster`、`ambient`、`water_creature`。

**角色**，用于世界模板的 `roles`。`ocean`、`river`、`beach`、`mushroom`、`swamp`、`hills`、`mountain`、`jungle`、`forest`、`savanna`、`sandy`、`mesa`、`snowy`、`wasteland`、`plains`、`water`。每个名称指的是在屏蔽移除了本来会填补该角色的生物群系之后，仍然填补该角色的一个生物群系。

**矿石类型**，用于 `oreTypes`。`COAL`、`IRON`、`GOLD`、`REDSTONE`、`DIAMOND`、`LAPIS`、`EMERALD`、`QUARTZ`、`DIRT`、`GRAVEL`、`DIORITE`、`GRANITE`、`ANDESITE`、`SILVERFISH`、`CUSTOM`。

### 世界设置

*值列表*

下面这些 `terrain` 键，一同写在世界模板的 `settings` 块中：

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

| 设置             | 类型                  | 默认值  | 作用                                                                                                                                                                                                                  |
| ---------------- | --------------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldName`      | 字符串                | 空      | 预填创建世界界面的名称框，存档文件夹随之而定。它只在名称框仍保持游戏默认值时才填入，并且与种子和游戏模式不同，之后不会再次应用                                                                                          |
| `worldSpawn`     | `x,z` 或 `x,y,z`      | 空      | 每个新世界的出生位置，仅在创建时应用。不带 y 时，采用该世界类型地面高度处的地表                                                                                                                                       |
| `worldBorder`    | 整数，格              | `0`     | 每个新世界所得到的边界直径，即 `/worldborder set` 所接受的数值。`0` 表示不动边界                                                                                                                                      |
| `worldTime`      | 整数，刻              | `-1`    | 每个新世界开始时的时间。`-1` 表示不动                                                                                                                                                                                 |
| `weatherCeiling` | `dimension=y` 的列表  | 无      | 降雨和降雪所能到达的最高 y。单独写一个数字适用于每个维度                                                                                                                                                              |
| `cloudHeight`    | `dimension=y` 的列表  | 无      | 云绘制所在的 y。单独写一个数字适用于每个维度，留空则保持游戏自己的高度                                                                                                                                                |

**`worldName`**（`terrain` 分组）预填创建世界界面的名称框；存档文件夹照常随之而定。它只在名称框仍保持游戏默认值时才填入，所以玩家键入的名称永远不会被覆盖，并且与种子和游戏模式不同，之后不会再次应用，创建时框里是什么，名称就是什么。

**`worldSpawn`**（`terrain` 分组）：`x,z` 或 `x,y,z`。仅在创建时应用。不带 y 时，采用该世界类型地面高度处的地表。非整数条目会被报告并忽略。这在超平坦世界上尤其相关：原版的出生点搜索在海平面寻找草方块，在层叠结构上永远找不到，可能会游荡数百格，而 `worldSpawn` 把它固定下来。

**`worldBorder`**（`terrain` 分组）：以格为单位的边界直径，即 `/worldborder set` 所接受的数值。在创建时应用；`0`（默认）表示不动边界；之后仍可通过命令移动。配置中的 `worldBorderLimit` 限制资源包可以请求的范围，请求更大的会被拒绝并记入日志，而不是被截断，所以资源包无法给服务器塞一个管理员并未同意的边界。

**`worldTime`**（`terrain` 分组）：`/time set` 所接受的刻数值（`18000` 为午夜，`6000` 为正午）。锁定主世界的时钟；一切读取时间的东西（生物生成、睡觉）看到的都是被锁定的值。`-1`（默认）让时间照常流逝。它是自定义维度 `fixedTime` 在主世界上的对应项，且独立于 `doDaylightCycle`。

**`cloudHeight`**（`terrain` 分组）：云绘制所在的 y。单独写一个数字适用于每个维度；`dimension=y` 行（`0=384`）按维度覆盖。拥有高大建筑的资源包会设置它，使天际线立于云层之下，而不是穿过云层，并且在 rubic 世界上它是绝对的 y，所以被抬高的上限正是放置云层之处的上方。留空（默认）则保持游戏自己的高度，主世界为 128，在 rubic 世界上随 `terrainOffset` 上移。

**`weatherCeiling`**（`terrain` 分组）：降雨和降雪所能到达的最高 y。单独写一个数字适用于每个维度；`dimension=y` 行（`0=128`）按维度覆盖。在它之上，雨不下落，雪不堆积，炼药锅不会被灌满，闪电不会落下，也不绘制任何降水；在它之下，天气不变。留空（默认）表示没有上限。冰取决于温度而不是降水，所以它在该线之上仍会形成。

### 世界物理

*值列表*

**世界物理**：四个 `terrain` 键，每个都是原版的倍率（`1.0` = 不变），每个都可以只写一个值适用于所有维度，或用 `dimension=value` 覆盖：

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

| 设置                    | 缩放对象                                                                                                    | 备注                                                                      |
| ----------------------- | ----------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| `worldGravity`          | 玩家、生物、掉落物、下落的方块、箭、投掷物、TNT 和经验球的下落加速度                                        | `0.17` 近似月球；跳跃弧线和弹射物射程会自动随之变化                       |
| `worldFallDamage`       | 摔落伤害                                                                                                    | 低重力维度通常需要把它调到匹配                                            |
| `worldJumpStrength`     | 跳跃速度                                                                                                    | 在重力变化之上应用                                                        |
| `worldTerminalVelocity` | 最大下落速度，以原版上限的比例表示                                                                          | 鞘翅飞行不受影响                                                          |

四项全部留空（默认）则保持原版物理。在 Galacticraft 维度上，重力键会缩放 Galacticraft 自己的重力。

### 世界接缝

*值列表*

**世界接缝**——将维度纵向堆叠：实体从世界底部或顶部离开时，会在相同的 x 和 z 坐标处进入下方或上方的维度。

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

| 设置 | 值 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `worldBelow` | `dimension=target` 行，或对所有维度生效的单个 id | 无 | 掉出世界底部后进入的维度 |
| `worldAbove` | 同上 | 无 | 升出已生成的顶部后进入的维度，指的是下界的顶层岩石，而不是其建筑高度上限 |
| `worldSeamEntities` | 布尔值 | `true` | 物品、生物和其他实体是否也能穿越，还是仅限玩家 |
| `worldSeamBedrock` | 布尔值 | `false` | 是否在接缝边界保留基岩。关闭时，边界处不会生成基岩，因此可以挖出通路 |

两个列表都为空（默认）时，所有世界保持封闭。世界最外层的方块层就是它的入口：进入最底层会将你送往下方，进入最顶层则会将你送回上方。到达位置会避开入口层，向下穿越时落在入口内侧三层处，向上穿越时落在一层处，因此不会被直接弹回。向下进入时，还会把到达点上方直至入口的各层全部打通，使入口从下方始终可见，也可作为返回的通路。

破坏入口层中的方块后，另一侧的世界会透过缺口显现：地板下方会出现下方维度的天空，天花板上方会出现上方维度的天空。这仅在客户端、渲染距离内绘制，对世界本身不做任何改动。动量会被保留。

玩家的每次穿越都会被记住。向下穿越会标记该洞口，之后在其附近向上返回时，会落在上次该洞口让你落脚的位置，因此你经常使用的竖井总是把你送回同一个已知地点，而不是新的地方。首次返回时会确定落点：若该处下方有地面则落在该处，否则从接缝处起逐层向外寻找最近的可站立空间，再不然就在洞口旁边的边缘上凿出一个小空腔，因为笔直向下挖出的竖井本身还没有落脚的平台。在附近没有你自己的洞口处向上穿越，只会在那里生成一个新的落点。从下方到达时若附近任何地方都找不到可站立的位置，则退回到该列的地表。若该处位于岩石内部，脚部和头部的空间会被清出，并以正确的方式破坏这些方块，使其掉落，容器也包括在内。

通过为每个维度单独设置各自的行，可以将多个维度串联堆叠，骑乘者与坐骑则分别穿越。

关卡限制对玩家生效。尚未解锁目标维度的玩家会收到该关卡的拒绝消息，并被送回最后站立过的地面，或接缝附近的一处平台；接缝不会放置任何方块，因此无法通过向下掉落来刷取被锁定的竖井。物品和生物自身不带关卡限制：开启 `worldSeamEntities` 时，无论是谁丢下的它们都能穿越，关闭时它们会从敞开的地板掉落并消失，与任何洞口一样。`worldSeamBedrock` 则改为封住地板，而保留基岩的资源包需要自行提供通路，通常是通过[属性覆盖](#属性覆盖)，给 `minecraft:bedrock` 设置一个正的 `hardness`。在接缝设置之前已生成的区块会保留其已有的基岩。

**Rubic 世界**——`rubicWorld`、`worldMinHeight`、`worldMaxHeight`、`rubicWorldDimensions`、`rubicWorldDimensionsAreBlacklist` 和 `terrainOffset` 同样是 `terrain` 键：参见 [Rubic 世界](#rubic-世界)。

## 文件夹列表

*参考*

每个文件夹的完整路径及其说明章节的链接，都在[文件放在哪里](#文件放在哪里)中。

## 命令

*参考*

### 你自己的命令

*命令*

`/rdpl` 在你自己的机器上运行，不需要任何权限，因为它所触及的一切都属于你。重载会重新扫描你所拥有的文件夹，将你的[属性覆盖](#属性覆盖)重新应用到你自己的方块和物品副本上，并刷新你自己的资源；它不会触及任何服务器，因此服务器上的副本要用 `/rdplserver reload` 来重载。在单人游戏中两者是同一台机器，所以 `/rdpl reload` 也会重载集成服务器的战利品表、进度和函数，与原版自己的重载相同。它可以在任何服务器上使用，无论服务器是否安装了本模组。

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdpl list` | 无 | 列出每个已加载的资源包、其优先级及其包含的内容。点击某个资源包可查找其中的文件 |
| `/rdpl which <namespace:path>` | 无 | 显示某个文件由哪个资源包提供，以及它覆盖了哪些资源包 |
| `/rdpl reload` | 无 | 重新扫描文件夹并重载所有内容 |
| `/rdpl reload <group>` | 无 | 只重载一类内容：`textures`、`models`、`languages`、`sounds` 或 `shaders` |
| `/rdpl unused` | 无 | 你的资源包中目前尚无任何内容请求过的文件，通常是路径写错了 |
| `/rdpl config unused` | 无 | `rdploader/config` 中已没有任何已安装资源包定义的选项文件 |
| `/rdpl config prune` | 无 | 删除这些文件 |
| `/rdpl pixelmap <namespace:path>` | 无 | 一个[像素图](#以像素图编写的纹理)最终解析成了什么，逐字符显示 |
| `/rdpl biome list` | 无 | 所有可生成的生物群系及其 id |
| `/rdpl biome here` | 无 | 你当前所在的生物群系 |
| `/rdpl biome find <name>` | 服务器的 | 已链接。转交给 `/rdplserver biome find`，因为只有服务器一侧知道种子 |
| `/rdpl team` | 无 | 资源包设置的各个阵营，各以自己的颜色显示，你所在的阵营，以及每个阵营的领队 |
| `/rdpl team join [name]` | 无 | 加入一个阵营。只提供资源包保持开放的阵营；由生物组成的阵营不能加入。省略名称时，会被分配到按 `balance` 接纳玩家的阵营中人数最少的一方 |
| `/rdpl team leave` | 无 | 离开你所在的阵营 |
| `/rdpl team vote <player>` | 无 | 在资源包以投票选出领队时，为你所在阵营的领队投票。平票则无人担任领队 |
| `/rdpl team claim` | 无 | 在资源包允许认领、且阵营中无人担任领队时，成为你所在阵营的领队 |
| `/rdpl round start` | 无 | 开始回合，适用于资源包将其保持在大厅中的情况（`opens.by`）。适用于阵营领队或管理员 |
| `/rdpl round reset` | 无 | 重置正在进行的回合，或按资源包的 `reset` 允许的方式发起重置投票。适用于阵营领队、资源包允许其发起投票的阵营中的玩家，或管理员 |
| `/rdpl round vote yes`、`no` | 无 | 在进行中的回合重置投票里投票。适用于已加入阵营的玩家 |
| `/rdpl oregen`、`generators`、`gate`、`dimensions`、`pregen`、`intro`、`goto`、`vein`、`game` | 服务器的 | 已链接。原样转交给 `/rdplserver`，由其决定，因此请参见下表 |

**哪些服务器子命令被链接，其余的为何没有。** 只有当客户端对某个名称没有自己的含义时，服务器子命令才会获得转发：`oregen`、`generators`、`gate`、`dimensions`、`pregen`、`intro`、`goto`、`vein`、`team` 和 `game` 只可能指服务器的命令，所以 `/rdpl` 将它们转交过去。客户端同样拥有的六个——`reload`、`list`、`which`、`unused`、`config` 和 `biome`——保留其针对你的资源包和客户端的含义，转发它们会使这些含义丧失。`biome find` 是共用名称中唯一本就属于服务器的部分，因为只有服务器知道世界种子，所以只转发这一种形式，而 `biome list` 和 `biome here` 仍留在你这边。这也决定了权限：由服务器自己的管理员检查来裁定，客户端既无法绕过，也无法被告知伪造的答案。

**`/rdpl` 也能通向服务器命令。** `/rdpl` 自己不处理的任何内容，即 `oregen`、`generators`、`gate`、`dimensions`、`pregen`、`intro`、`goto`、`vein`、`team` 和 `game`，都会被直接转交给 `/rdplserver`，并出现在 Tab 补全中，因此在单人游戏中只需输入一个命令。它们被原样转交，服务器照常裁定，权限也一并适用，所以输入较短的名称不会开放任何额外权限。两者都有的子命令 `reload`、`list`、`which`、`unused`、`biome` 和 `config` 仍归 `/rdpl`，指的是客户端自己的资源包。`biome find` 是共用名称中的唯一例外：只有服务器知道世界种子，所以这种形式会被转交，而 `biome list` 和 `biome here` 由你自己的客户端回答。

**日常编辑：** 在大型整合包中，`/rdpl reload textures` 比 F3+T 快得多。F3+T 仍然可用，并会重载所有内容。当你*添加*或*删除*文件时请使用普通的 `/rdpl reload`，因为这会改变文件夹所包含的内容。

### 服务器命令

*命令*

在专用服务器上，`/rdplserver` 对服务器自己的文件夹副本执行同样的操作。“等级”一列是发送者所需的权限等级：`3` 为管理员，`2` 还允许命令方块，`0` 为任何玩家，`4` 高于管理员，任何人都无法使用。只有 `intro`、`team`、`card` 和三种 `goto` 形式对管理员以下的发送者开放，而 `goto` 是资源包可以调整的那一个。

#### 资源包与文件

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver reload` | 3 | 重新扫描服务器的文件夹并重载所有内容 |
| `/rdplserver list` | 3 | 列出服务器加载的每个资源包、其优先级及其包含的内容 |
| `/rdplserver which <namespace:path>` | 3 | 显示某个文件由哪个资源包提供，以及它覆盖了哪些资源包 |
| `/rdplserver unused` | 3 | 服务器的资源包中尚无任何内容请求过的文件 |
| `/rdplserver config unused` | 3 | `rdploader/config` 中已没有任何已安装资源包定义的选项文件 |
| `/rdplserver config prune` | 3 | 删除这些文件 |

#### 世界与生成

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver oregen` | 3 | 被阻止的矿石生成的累计总数，按模组和类型统计 |
| `/rdplserver generators` | 3 | 被阻止的世界生成器的累计总数，按模组和类型统计 |
| `/rdplserver biome` | 3 | 服务器上所有可生成的生物群系 |
| `/rdplserver biome list [all]` | 3 | 同上，并附带每个生物群系的 id；`all` 还包括无法生成的那些 |
| `/rdplserver biome here` | 3 | 你当前所在的生物群系。控制台不站在任何地方，因此在控制台中会要求提供玩家 |
| `/rdplserver biome here <player>` | 3 | 该玩家当前所在的生物群系，这是控制台和脚本所需要的形式 |
| `/rdplserver biome find <name>` | 3 | 某个生物群系最近的生成位置，查找时不会生成区块 |
| `/rdplserver dimensions` | 3 | 所有维度，包括资源包添加的维度 |
| `/rdplserver vein <entry> [radius]` | 3 | 在执行命令处周围该区块数（默认 8）范围内，某个 `vein` 形式的世界生成条目的矿脉种子所在位置，由近到远，无论这些区块是否已存在。`/rdpl vein` 会转交给它 |

#### 传送门命令

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver gate list` | 3 | 所有传送门及其是否已开启 |
| `/rdplserver gate check <player>` | 3 | 某玩家已通过哪些传送门 |
| `/rdplserver gate grant <player> <gate>` | 3 | 为某玩家开启一个传送门 |
| `/rdplserver gate revoke <player> <gate>` | 3 | 再将其关闭 |

#### 预生成命令

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver pregen <radius>` | 3 | 生成执行命令处周围该区块数范围内的每个区块。参见[预生成](#预生成) |
| `/rdplserver pregen <radius> relight` | 3 | 仅对已存在的陆地运行光照处理 |
| `/rdplserver pregen status` | 3 | 一次运行的进度 |
| `/rdplserver pregen stop` | 3 | 结束它 |

#### 玩家、队伍与回合

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver intro` | 0 | 让世界开场动画在你下次加入时再次播放。任何玩家都可以运行，且只会清除他自己的记录 |
| `/rdplserver team`、`team join [name]`、`team leave`、`team vote <player>`、`team claim` | 0 | 与上文的 `/rdpl team` 各形式相同，那些命令会转交到这里 |
| `/rdplserver round start` | 0 | 与 `/rdpl round start` 相同，该命令会转交到这里 |
| `/rdplserver round reset`、`round vote yes`、`round vote no` | 0 | 与上文的 `/rdpl round` 各形式相同，那些命令会转交到这里 |
| `/rdplserver card <rule> [players]` | 2 | 按 id 或文件名，向指定的玩家或你自己显示一条[卡片规则](#卡片)。`when`、`repeat` 和 `cooldown` 会被跳过 |
| `/rdplserver reset` | 3 | 让地图恢复到一个回合结束时的样子：所有人被拦住，实体被清扫，分数被清零，运行 `resetRuns`，玩家被送到 `resetSendsTo` 并被释放，并以起始计数开启一个回合，如[预生成](#预生成)下的重置设置所述。不会从 `/rdpl` 转交 |

#### 前往各处

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver goto <structure>` | `gotoLevel`、`3` | 带你前往最近一个还没有人去过的该结构，查找时不会在途中生成陆地 |
| `/rdplserver goto <structure> next` | `gotoNextLevel`、`3` | 带你继续前往你在本次会话中尚未被带去过的最近一个，无论它之前是否有人去过 |
| `/rdplserver goto <structure> back` | `gotoBackLevel`、`3` | 带你前往它之前的那一个，沿着本次会话把你送去过的地方向回退 |
| `/rdplserver goto <biome>` | `gotoLevel`、`3` | 带你前往该生物群系最近的地点，以 ID 指定，例如 `minecraft:river`，并落在其地表；`next` 与 `back` 的用法与结构相同。函数或命令方块以 2 级权限即可运行任何 `goto` |

#### 游戏

*服务器命令*

| 命令 | 等级 | 作用 |
| --- | --- | --- |
| `/rdplserver game coin` | 0 | 抛一枚硬币。正面计为 1，反面计为 0 |
| `/rdplserver game die <sides>` | 0 | 掷一个 2 到 1000 面的骰子 |
| `/rdplserver game die <name>` | 0 | 按权重掷一个[资源包骰子](#骰子与牌堆) |
| `/rdplserver game dice <roll>` | 0 | 掷最多 100 个骰子并求和，如 `2d6`、`d20` 或 `3d8-2`。每个骰子都会显示 |
| `/rdplserver game advantage [roll]`、`disadvantage [roll]` | 0 | 掷两次，取较大的总数或较小的总数。未指定时为 `1d20` |
| `/rdplserver game pick player` | 0 | 随机选一名在线玩家 |
| `/rdplserver game pick team [team]` | 0 | 随机选一支计分板队伍，或所指定队伍的一名在线成员 |
| `/rdplserver game deck draw <name>` | 0 | 从资源包牌堆的剩余牌中抽一张 |
| `/rdplserver game deck left <name>` | 0 | 牌堆还剩多少张牌 |
| `/rdplserver game deck shuffle <name>` | 2 | 把所有牌放回 |
| `/rdplserver game teamroll [roll]` | 0 | 发送者一方的每个人各掷一次，最高者胜，平局抽签决定。没有队伍时只有发送者自己掷 |
| `/rdplserver game tiebreak [objective]` | 2 | 从某个目标并列第一的各方中抽出一方：指定的目标，否则是第一个带 `tiebreak` 的计分目标，再否则是第一个 |
| `/rdplserver game board list` | 0 | 世界中的每副棋盘，及其游戏、位置和状态 |
| `/rdplserver game board start <game> <board> [x y z]` | 2 | 在发送者所站的位置或给定的位置摆出一副棋盘，并摆好棋子 |
| `/rdplserver game board end <board>` | 2 | 移除一副棋盘及其棋子 |
| `/rdplserver game board show <board>` | 0 | 以字母逐行显示局面，以及每一方的执掌者、其用时和轮到谁走 |
| `/rdplserver game board move <board> <from> <to> [piece]` | 0 | 按格子名称走子，例如 `e2 e4`，并给出升变成的棋子 |
| `/rdplserver game board resign <board>` | 0 | 认输 |
| `/rdplserver game board draw <board>` | 0 | 提出和棋，或接受对方的提议 |
| `/rdplserver game board takeback <board>` | 0 | 请求撤回上一步，或同意对方的请求 |
| `/rdplserver game board ai <board> <side> <level>` | 2 | 让电脑以 1 到 4 级执掌一方，或用 0 交还 |
| `/rdplserver game last [count]` | 0 | 最近的掷骰，最新的在前：10 次，或给定的次数，最多 50 |
| `/rdplserver game pass` | 0 | 当计分文件设置了轮流行动时，提前结束发送者所在阵营的行动。从命令方块或控制台执行时，结束当前正在行动的阵营 |

任何掷骰都可以以 `store <objective>` 结尾，把结果数字写入发送者自己在该目标中的分数；也可以以 `audience <谁>` 结尾，取代资源包的默认值：`self`、`team`（发送者一方，没有队伍时只有发送者）、`all`、`radius <格数>`（同一世界中该距离内的玩家）或 `silent`，后者只写日志。`/rdpl game` 会转交给它。

### 谁可以使用 goto

*命令*

**开放 `goto`。** `/rdplserver` 的每一部分都需要管理员，即 3 级，只有 `intro` 和 `team` 除外，它们是玩家自己的命令，始终为 0 级；还有 `card`，它是 2 级，这样命令方块就能显示卡片；`game` 的各部分则有[各自的等级](#谁可以使用-game)。三种 `goto` 形式是资源包唯一可以决定的部分：每种都带有自己的权限等级，资源包或配置可以将其调低，彼此之间以及与命令的其余部分之间互不影响。

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

| 设置 | 所控制的内容 |
| --- | --- |
| `gotoLevel` | `goto <structure>` |
| `gotoNextLevel` | `goto <structure> next` |
| `gotoBackLevel` | `goto <structure> back` |
| `gotoPlaceLevels` | 某一个指定地点，对三种形式都生效 |

该值是发送者所需的权限等级。默认为 `3`（管理员）。`2` 还允许命令方块，因此资源包可以把传送放在按钮或压力板上，而不会暴露 `/rdplserver` 的其余部分。`0` 则对任何玩家开放。这三项设置相互独立：例如，可以让 `next` 对命令方块开放以便进行村庄巡游，而 `back` 仍仅限管理员。

由于 `intro` 对所有人开放，任何玩家都能触及 `/rdplserver` 本身，所以其他每个子命令都会自行检查是否为管理员，并以消息拒绝。Tab 补全与此一致：非管理员会看到 `intro`，在资源包设置了阵营时看到 `team`，一旦某个等级允许他们使用，还会看到 `goto`。

`gotoPlaceLevels` 以 `name=level` 条目为单个地点覆盖这三项设置，如上例所示。名称就是你在 `goto` 之后输入的内容：可以是原版的名称，如 `Village` 或 `Mansion`，也可以是在 `imprint` 条目上以 `locateAs` 注册的名称。匹配时忽略大小写。等级 `4` 高于管理员，会对所有人关闭该地点——这是在其余 `goto` 开放的同时隐藏某一个地点的办法。

一个条目为该地点的三种形式设置同一个等级。未列出的地点回落到上面的三项设置，未注册的名称则永远不会匹配。

Tab 补全遵循相同的规则，因此在 `goto` 之后，发送者只会看到他们实际可以被带去的地点。

这些设置位于 `commands` 组中，所以配置中的 `control.commands` 决定了资源包是否能设置它们，而该项为 `off` 时，无论资源包要求什么，一切都保持为管理员等级。

### 谁可以使用 game

*命令*

`game` 的每个部分都有自己的等级：每种掷骰为 0，`deck shuffle`、`tiebreak`、`board start`、`board end` 和 `board ai` 为 2。`gameLevels` 以 `部分=等级` 条目更改其中任意一个，部分即 `game` 之后的内容。

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "gameLevels": ["coin=0", "deck draw=0", "deck shuffle=3", "tiebreak=4"]
  }
}
```

| 设置 | 管辖什么 |
| --- | --- |
| `gameLevels` | `game` 的一个部分：`coin`、`die`、`dice`、`advantage`、`disadvantage`、`pick`、`deck draw`、`deck shuffle`、`deck left`、`teamroll`、`tiebreak`、`last`、`pass`，或 `board` 加其动作，例如 `board move` |

等级刻度与 `goto` 相同，`4` 会对所有人关闭某个部分。Tab 补全只提供发送者可以使用的部分。`gameLevels` 与 `goto` 的设置一起位于 `commands` 组中。

## 须知

*参考*

- CraftTweaker 和 GroovyScript 在 RDPL 之后运行，因此它们的改动仍然优先。
- 配方只在启动时加载，所以修改配方需要重启而不是重载。
- 保存在世界自身数据文件夹中的函数仍然优先于资源包中的函数，该世界自己的进度也是如此。
- 已经生成的结构会一直保持加载，直到你离开该世界。
- 文件名区分大小写。如果你文件的大小写与游戏请求的不一致，RDPL 仍会加载它，但会发出警告，因为在 Linux 上它根本找不到。
- 在 `rdploader` 中放入一个 `pack.png`，即可为资源包设置图标。没有的话会显示 RDPL 的图标。
- 可以通过 `config/mct_resourcedatapackloader_mixin.cfg` 中的 `rootDirectory` 选项移动或重命名该文件夹。也可以使用绝对路径，需要重启。
- 引用裸原版模型的方块状态也会继承原版的纹理。`cube_all` 和 `cross` 这类父模型从方块状态获取纹理，没有问题。
- `forge_marker: 1` 不支持 multipart，所以藤蔓的方块状态必须是普通的原版 multipart，并把纹理写在模型里。
- Forge 加载画面以深色绘制，并以本模组的标志取代 Forge 的标志。`client` 配置类别中的 `darkSplash` 可恢复 Forge 自己的颜色；无论如何，在 `config/splash.properties` 中手动设置的颜色都不会被改动，且需要重启。

## 出现问题时

*参考*

**首先检查 `logs/rdpl.log`。** RDPL 所做的一切都记录在那里，而不是主日志中。进度、战利品表、配方、函数、结构以及每一项内容都会连同其来源的资源包一并记录，任何格式有误的内容都会连同原因一起记录。

**纹理和其他资源则不同。** 它们被请求得太频繁，无法逐一记录，因此改由 `/rdpl unused` 列出你的资源包中尚无任何内容请求过的文件。请在游戏加载完毕后运行。路径正确的文件一定会被请求，所以列出的任何文件通常都是拼写错误，但要记住有些文件只在需要时才加载，例如你所玩语言之外的其他语言。

**内部没有 `assets` 目录的 zip 会被跳过，** `rdploader` 中的任何文件夹也一样，日志会对此作出说明。

**`/rdpl which minecraft:textures/blocks/stone.png`** 会准确告诉你是哪个资源包在提供某个文件，以及它覆盖了什么。

## 额外功能：原版调整

*参考*

对原版行为的小改动，每一项都在 `tweaks` 配置类别中开关。

| 选项 | 默认值 | 作用 |
| --- | --- | --- |
| `promptLeafDecay` | 开 | 失去树干的树叶会在一秒内凋落，而不是等待随机刻 |
| `lenientPaths` | 开 | 可以在方块下方制作草径，并且在其上方放置方块时草径仍会保留 |
| `unbreakableSpawners` | 关 | 刷怪笼无法被挖掘或炸毁 |
| `modernChestPlacement` | 开 | 箱子的并排方式与 1.13 及之后相同 |

另外三项位于 `content` 类别而不是 `tweaks`：

| 选项 | 默认值 | 作用 |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | 原版仙人掌最高能长多高 |
| `caneMaxHeight` | `3` | 原版甘蔗最高能长多高 |
| `shovelPaths` | 开 | 铲子可将标记为 `behavesAs` path 的方块变成草径，潜行时可还原 |

**这些会让位于 Universal Tweaks**，因为它修改的是相同的原版方块。具体何时让位，参见 [Universal Tweaks](#universal-tweaks)。

**这一切都不会影响资源包。** 这些选项只改变 Minecraft 自己的仙人掌、甘蔗、树叶和草径。你的资源包以 `"type": "cane"` 定义的方块带有自己的 `growth` 部分，无论安装了什么其他内容，它都会长到你设定的高度。`lenientPaths` 还会为使用 `behavesAs` 的资源包方块解除同样的限制，Universal Tweaks 不会触及这一点，因此这一半无论如何都保持开启。

### 不可破坏的刷怪笼

*额外功能：原版调整*

`unbreakableSpawners` 让刷怪笼方块拥有基岩的数值：不可破坏的硬度，以及无物能够幸免的爆炸抗性。无论镐子多好，玩家都无法挖掘它，苦力怕、TNT 或会 `explodes` 的资源包实体也无法将其炸毁。创造模式仍然可以移除它们，正如仍可移除基岩一样，因此资源包作者永远不会被锁在自己的建筑之外。它需要重启，因为这些数值是在游戏完成加载时一次性设定的。

**它针对的是方块，而不是刷怪笼本身。** 没有针对单个刷怪笼的开关。该选项改变的是 `minecraft:mob_spawner` 本身，因此会一次性影响世界中的每一个刷怪笼：放置刷怪笼的四种原版结构、任何模组放置的刷怪笼，以及你自己的资源包放置的刷怪笼。

最后一点就是自定义结构的答案。位于你某个 `.nbt` 模板中、由 `imprint` 条目放置的刷怪笼，是带有自己的方块实体的普通刷怪笼方块，所以选项一开启它就被涵盖了。照常在结构里放置一个刷怪笼，在模板的方块实体数据中设定它刷出什么，再开启 `unbreakableSpawners`，你地牢里的那一个就和原版的一样不可破坏。资源包里无需为此添加任何内容，也没有办法只保护你自己的而让世界中其余的保持可破坏。

### 箱子放置

*额外功能：原版调整*

`modernChestPlacement` 让箱子和陷阱箱的放置方式与 1.13 及之后相同。

- 箱子只会与其正左侧或正右侧的单个箱子相连，并且只有两者朝向相同时才会如此。位于另一个箱子前方或后方的箱子绝不会与其相连。
- 潜行可使新箱子保持单个，除非点击的是单个箱子的侧面：这时它会与该箱子相连，并转向与其相同的朝向。
- 箱子可以紧挨着大箱子放置，并保持为单个，因此可以沿墙摆出一排箱子。

每个箱子都会记住它的搭档，所以已放置的箱子在重载后仍保持成对或单个，漏斗或管道只会填充它所接触的那个箱子，破坏其中一半会使另一半变为单个。在该选项开启之前放置的箱子，或由世界生成和结构放置的箱子，仍像 1.12 一直以来那样配对。没有安装 RDPL 的客户端仍会把两个相接的单箱绘制成一个大箱子，不过打开时它们是分开的。

## 额外功能：修复 JEI 插件冲突

*参考*

有些模组会在提供配方的模组完成初始化之前就查询 JEI 的配方注册表，这会使日志里充斥着数以百计无害但烦人的错误，并可能悄悄破坏某个模组的 JEI 集成。RDPL 会自动检测到这种情况并纠正通知顺序。它适用于 Just Enough Items 和 Had Enough Items。如果两者都没有安装，则什么也不会发生。

## 额外功能：减少启动错误

*参考*

- 引用了没有任何模组实际注册过的物品的配方，通常是在模组自己的配置中被禁用的内容，会被跳过，而不是抛出解析错误。数量只会记录一次。（`skipMissingItems`）
- 奖励某个后来被脚本移除的配方的进度仍会加载，而不是失败。它们只是永远不会解锁该配方，整组情况会汇总成一行。（`tolerateMissingInAdvancements`）

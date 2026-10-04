Resource Data Pack Loader
=========================

放进这个文件夹的任何内容，都会替换模组或 Minecraft 本身提供的内容。
它对每个世界生效，单人游戏和服务器皆然，
不需要任何开关。


不只是覆盖
----------

这里的资源包还可以用 JSON 文件定义新的方块、物品、生物群系乃至
整个维度，决定生成什么、在哪里生成，用钥匙或必须击杀的
生物给维度上锁，还能提前生成世界的陆地，让没人需要
等待区块加载。随模组一起发布的 HOWTO.md 涵盖了这一切。


如何添加文件
------------

打开模组的 jar，找到你想修改的文件，从
“assets”或“data”开始复制它的路径。

要替换铁矿石的纹理，Minecraft jar 里的文件是：

    assets/minecraft/textures/block/iron_ore.png

那么你的版本放在这里：

    rdploader/assets/minecraft/textures/block/iron_ore.png

战利品表放在 data 下，做法相同：

    data/minecraft/loot_tables/blocks/iron_ore.json
    rdploader/data/minecraft/loot_tables/blocks/iron_ore.json

这就是全部规则。“assets”或“data”之后的路径始终与
jar 内的路径相同，所以不需要重命名或移动任何东西。


保持整洁
--------

你也可以把文件归入一个命名的资源包，用 zip 打包：

    rdploader/MyTextures.zip        （zip 的顶层要直接是“assets”或“data”）

打包时，请选中里面的内容再压缩，不要压缩外面的文件夹。
如果 zip 的顶层是一个包着“assets”或“data”的单一文件夹，
它会被跳过，日志里会说明。

rdploader 里的文件夹不算资源包，会被跳过，日志里会说明。
零散文件请放在 assets 或 data 下，资源包请先压缩成 zip 再放进来。

同一个文件出现在两个地方时，命名的资源包优先于零散文件，
/rdpl which 会告诉你哪一个胜出。


资源包优先级
------------

如果两个命名的资源包含有同一个文件，可以在 zip 名称前加上
RDPL 和一个数字来控制谁胜出。RDPL0 最先加载，数字越大
加载越晚，最后加载的资源包胜出：

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

大小写均可，数字后面的空格、短横线或下划线可有可无。
前缀在日志和 /rdpl list 里会被去掉，所以
RDPL1 SeasonalTextures 显示为 SeasonalTextures。


模组 API
--------

模组可以在自己的 jar 里携带 RDPL 内容，放在名为 rdploader 的
文件夹中，布局与资源包完全一致：

    thatmod.jar
      META-INF/mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

这些是默认值，不是覆盖。模组的资源包加载在这个文件夹里的所有
资源包之下，所以你放在这里的内容会胜出，而模组只能在它自己的
mods.toml 里声明过的命名空间下提供文件。其他内容会被
忽略并给出警告，因此没有模组能悄悄重新定义别的模组或你的内容。

每个带有这种内容的模组，在第一次被发现时都会列入
config/mods.json：

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

把 enabled 设为 false 即可关闭该模组的内容。priority 保持 -1 则
始终垫在最底层，也可以给一个数字，让它按上面的顺序与
编号资源包并列。日志里按从低到高列出资源包，模组自带的会有
标记，所以不会有你看不到的东西被加载。


玩家的资源包
------------

默认情况下，这里的文件位于玩家在选项界面选择的资源包之上，
所以资源包无法覆盖它们。这对整合包徽标之类的东西是对的，
对希望玩家能换皮肤的纹理则不对。

在 RDPL 前缀后加上 O 或 N，可以逐个资源包决定：

    rdploader/RDPLO Branding          始终胜出，资源包无法改动
    rdploader/RDPLN BaseTextures      资源包可以覆盖它
    rdploader/RDPL1O Seasonal         有优先级，并且始终胜出，两者兼备

没有字母的资源包遵循配置中的 overrideResourcePacks 选项，
/rdpl list 会标出会覆盖的那些。

同样的规则也适用于数据包。标为 N 的资源包位于世界自己 datapacks
文件夹里的数据包之下，标为 O 的则位于它们之上。

没有前缀的资源包先于所有编号资源包加载，按字母顺序排列，
所以编号资源包始终胜过没有编号的。

想关闭某个资源包又不想删除它，在名称末尾加上 .disabled：

    rdploader/RDPL1 SeasonalTextures.zip.disabled

该资源包会被跳过，日志里会说明。去掉后缀即可重新启用。


你可以修改什么
--------------

纹理、模型、方块状态、语言文件、声音、字体、闪烁标语，以及模组在
assets 文件夹里存放的其他一切，比如指南书或手册。

进度、战利品表、配方、标签、函数、结构模板，以及模组在
data 文件夹里存放的其他一切。它们属于服务端，所以在专用服务器上
同样有效，修改后用 /reload 生效。

也可以把东西拿掉。recipe_removals 里的文件按名称、命名空间或产物删除
配方，disabled 里的文件把方块和物品移出游戏而不注销它们，
所以世界保留原有的 id，删除该文件就能让一切恢复。战利品注入
会向已有的战利品表里加一个池，而不是替换它，block_drops 则为
不属于你的方块新增或替换掉落物。熔炉配方、燃料燃烧时间、创造模式
标签页和音效事件也各有自己的文件。

铁砧也可以学会新的活儿。anvils 里的文件指明一个物品、与它搭配放在
右槽的物品，以及铁砧在指定等级下提供的附魔或结果。取出成果
可以获得一项进度，物品也可以在获得这项进度之前一直无法使用。

hardness 文件为一组方块设定挖掘时间和爆炸抗性，exposures 文件
则定义辐射之类的危险：它作用于靠近指定方块、携带指定物品或
处于指定维度的玩家，分为若干等级，每级施加效果和伤害，
也可以从附近的生物和玩家身上染上，或随雨水降下。


添加新内容
----------

资源包还可以添加自己的方块、物品和流体，用 JSON 描述。你
不需要编写或构建模组。

定义放在 data 下，每种类型各占一个文件夹。“variants”里的每个键
都是一个名称，所以位于

    rdploader/data/mypack/blocks/ores.json

的文件如果包含名为 ruby_ore 的变种，就会注册为 mypack:ruby_ore。
文件本身的名称只用于分组。如果某个真正的模组已经注册了该名称，
模组胜出，你的变种会被跳过。

最简单的方块只要几行：

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

模型、方块状态、纹理和语言条目仍需放在 assets 下，
与这个文件夹里的其他文件方式相同。

下面每一项都是 data/<yourpack> 下的一个文件夹：

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

方块有以下几种形态，由“type”字段决定：

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

物品有以下几种：

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

药水类型的名称取自语言键 item.minecraft.potion.effect.<baseName>，
其他形态则把 potion 换成 splash_potion、lingering_potion 或
tipped_arrow。potion_bottle 物品是你自己的容器：它和其他物品一样
带有 creativeTab，并装着你在 potionTypes 里列出的药水类型。

villagers/<name>.json 文件定义一种职业。trades/*.json 文件为任何职业
添加交易，无论是你的还是 Minecraft 的，并指明职业和交易出现的
等级。

entities/<name>.json 文件用已有的实体做出一个新实体。它指明所基于的
实体，以及不同之处：名称、外观、生命值和伤害、移动方式、战斗方式
以及掉落物。它是一个独立的实体，有自己的刷怪蛋和战利品表，
被它基于的那个实体不受影响。它还可以带有自己的存储：物品槽、
流体罐和能量缓冲，管道和线缆都能接入，玩家潜行并右键点击它即可
打开。

villages/<name>.json 文件添加一个城市或村庄可以用你的 .nbt 模板
建造的地块，raids/<name>.json 文件则在玩家把不祥之兆带进村庄时，
向村庄派出一波波敌人。

cards/<name>.json 文件在发生某件事时（比如玩家进入某个生物群系）
在屏幕上显示一张卡片，同一文件夹里以本模组自带消息命名的文件，
可以改变那条消息的内容。

worldintro/<name>.json 文件在有人进入世界时、取得控制权之前，
播放一连串页面。文字是 assets/<yourpack>/texts 下的普通 .txt 文件。
可以每位玩家播放一次，也可以每次加入都播放。

teams/<name>.json 文件在游戏的计分板上设立一个阵营并规定谁能加入，
scoring/<name>.json 文件则是一个为这些阵营计分、并决定比赛
如何结束的目标。


整个世界
--------

资源包不限于单个事物。dimensions/<name>.json 注册一个拥有自己的
地形、生物群系和天空的维度。gates/<name>.json 为抵达维度设定
条件，比如持有或消耗某个物品。portal 类型的方块会把走进去的人
送往另一个维度，portalframes 则让玩家自己搭建并点燃框架。

worldtemplates/<name>.json 把一个世界的设置收进一个文件，
这样资源包可以一次性提供整个世界的形态，而不必要求
改十几处配置。它也可以塑造主世界本身，比如海平面，
以及海洋是否为岩浆。

维度还可以设定它头顶的外观和行为。它的雾、光照色调、太阳和月亮的
变暗、云层以及热浪都绘制在你自己的屏幕上，天气则决定是否下雨、
下雪或雷暴、阵雨持续多久，以及雨的颜色、粒子、声音和角度。

worldgen 不只是矿石。一个条目放置一种形状，从你的方块的小团块到
你自己的 .nbt 模板，并决定出现的频率、高度和所在的
生物群系。

biomes/<name>.json 文件定义生物群系：它的气候和颜色、
构成它的方块、装饰它的东西、在其中生成的生物，以及它在哪里生成。


这里为止
--------

这里描述的是事物是什么，而不是它随时间做什么。任何需要
方块实体、界面或每刻都运行的代码的东西，仍然需要真正的模组，
但有两个例外：container 类型的方块拥有带自己界面的物品栏，
实体变种可以带有存储。机器是够不到的；矿石、栅栏、食物
或流体则可以。


来自其他版本的资源包
--------------------

为本模组 1.12.2 版本制作的资源包可以原样加载。zip 会在自身内部被
转换一次，写入一个对应本版本的 versions 文件夹，1.12.2 的文件
保持原样，所以同一个 zip 在每个版本上都能继续使用。


查看你的更改
------------

按 F3+T 重新加载纹理、模型、语言文件和 assets 下的其他一切。
在服务器上，或对于 data 下的任何内容，输入 /reload。

如果你添加或删除了文件，请改用 /rdpl reload。编辑一个
原本就存在的文件，只需 F3+T 或 /reload。

/rdpl list 显示加载的每个资源包及其内容。把鼠标悬停在
资源包上即可查看。

/rdpl which minecraft:textures/block/stone.png 显示是哪个资源包
提供了该文件，以及它之下被遮盖的资源包。

/rdpl config unused 列出 rdploader/config 里不再被任何已安装
资源包定义的选项文件，/rdpl config prune 会删除它们。
/rdpl pixelmap 显示像素图最终的样子，/rdpl biome list 和 here
则告诉你生物群系的信息。

这些命令无需管理员权限，因为它们只读取你自己电脑上的文件。
在专用服务器上，/rdplserver reload 重新扫描服务器的副本，
/rdplserver list、which 和 unused 则针对服务器作答。


出问题时
--------

先看日志。logs/rdpl.log 列出每个已加载的资源包和每个被跳过的
资源包及原因，任何出错的地方都会记为警告并说明原因。

/rdpl unused 列出你的资源包中尚未被任何东西请求过的文件，
这通常意味着路径有拼写错误。请在游戏加载完成后运行，
并注意有些文件只在需要时才会加载，比如你所用语言之外的
其他语言。

大小写很重要。如果你的文件是 Stone.png 而游戏请求的是
stone.png，它仍会加载，但警告会让你重命名。请务必重命名，
因为在这个模组之外，这个文件根本找不到。
语言文件最容易出错：它们是 en_us.json，不是 en_US.json。

检查你的文件是否位于“assets”或“data”文件夹内。两者都没有的
zip 会被跳过，日志里会说明。


进度与配方
----------

脚本添加或替换的配方，以脚本给它的名称为名。
要让某个进度解锁它，请在这里放一个指明该配方的进度文件，
这样进度就能重新完整运作。

请在脚本里给这样的配方一个固定的名称。自动生成的名称
可能在你一编辑配方时就变了，所以不能安全地让进度指向它。


rdploader 文件夹本身可以用 config/resourcedatapackloader-common.toml 中的
rootDirectory 选项移动或重命名。也可以用绝对路径，
需要重启。

在这个文件旁边放一个 pack.png，可以给资源包设置图标。

这个文件由模组写入，并在内容变化时更新，
所以你在里面输入的任何内容，在下次启动游戏时都会被替换。

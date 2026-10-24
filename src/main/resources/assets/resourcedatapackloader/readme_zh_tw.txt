Resource Data Pack Loader
=========================

放進這個資料夾的任何內容，都會取代模組或 Minecraft 本身提供的內容。
它對每個世界生效，單人遊戲和伺服器皆然，
不需要任何開關。


不只是覆寫
----------

這裡的資源包還可以用 JSON 檔案定義新的方塊、物品、生態域乃至
整個維度，決定生成什麼、在哪裡生成，用鑰匙或必須擊殺的
生物替維度上鎖，還能事先生成世界的陸地，讓沒有人需要
等待區塊載入。隨模組一起發布的 HOWTO.md 涵蓋了這一切。


如何新增檔案
------------

打開模組的 jar，找到你想修改的檔案，從
「assets」或「data」開始複製它的路徑。

要取代鐵礦石的材質，Minecraft jar 裡的檔案是：

    assets/minecraft/textures/block/iron_ore.png

那麼你的版本放在這裡：

    rdploader/assets/minecraft/textures/block/iron_ore.png

戰利品表放在 data 底下，做法相同：

    data/minecraft/loot_table/blocks/iron_ore.json
    rdploader/data/minecraft/loot_table/blocks/iron_ore.json

這就是全部的規則。「assets」或「data」之後的路徑一律與
jar 內的路徑相同，所以不需要重新命名或移動任何東西。


保持整齊
--------

你也可以把檔案歸入一個具名的資源包，用 zip 打包：

    rdploader/MyTextures.zip        （zip 的最上層要直接是「assets」或「data」）

打包時，請選取裡面的內容再壓縮，不要壓縮外面的資料夾。
如果 zip 的最上層是一個包著「assets」或「data」的單一資料夾，
它會被略過，記錄檔裡會說明。

rdploader 裡的資料夾不算資源包，會被略過，記錄檔裡會說明。
零散的檔案請放在 assets 或 data 底下，資源包請先壓縮成 zip
再放進來。

同一個檔案出現在兩個地方時，具名的資源包優先於零散的檔案，
/rdpl which 會告訴你哪一個勝出。


資源包優先順序
--------------

如果兩個具名的資源包含有同一個檔案，可以在 zip 名稱前加上
RDPL 和一個數字來控制由誰勝出。RDPL0 最先載入，數字越大
載入越晚，最後載入的資源包勝出：

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

大小寫均可，數字後面的空格、連字號或底線可有可無。
前綴在記錄檔和 /rdpl list 裡會被去掉，所以
RDPL1 SeasonalTextures 會顯示為 SeasonalTextures。


模組 API
--------

模組可以在自己的 jar 裡攜帶 RDPL 內容，放在名為 rdploader 的
資料夾中，版面配置與資源包完全一致：

    thatmod.jar
      META-INF/neoforge.mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

這些是預設值，不是覆寫。模組的資源包載入在這個資料夾裡所有
資源包之下，所以你放在這裡的內容會勝出，而模組只能在它自己的
neoforge.mods.toml 裡宣告過的命名空間下提供檔案。其他內容會被
忽略並給出警告，因此沒有模組能悄悄重新定義別的模組或你的內容。

每個帶有這種內容的模組，在第一次被發現時都會列入
config/mods.json：

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

把 enabled 設為 false 即可關閉該模組的內容。priority 保持 -1 則
一律墊在最底層，也可以給一個數字，讓它依上面的順序與
編號資源包並列。記錄檔裡依由低到高列出資源包，模組自帶的會有
標記，所以不會有你看不到的東西被載入。


玩家的資源包
------------

預設情況下，這裡的檔案位於玩家在選項畫面選擇的資源包之上，
所以資源包無法覆寫它們。這對模組包標誌之類的東西是對的，
對希望玩家能更換外觀的材質則不對。

在 RDPL 前綴後加上 O 或 N，可以逐個 RDPL 包決定：

    rdploader/RDPLO Branding          一律勝出，資源包無法更動
    rdploader/RDPLN BaseTextures      資源包可以覆寫它
    rdploader/RDPL1O Seasonal         有優先順序，並且一律勝出，兩者兼備

沒有字母的 RDPL 包遵循設定中的 overrideResourcePacks 選項，
/rdpl list 會標出會覆寫的那些。

同樣的規則也適用於資料包。標為 N 的 RDPL 包位於世界自己 datapacks
資料夾裡的資料包之下，標為 O 的則位於它們之上。

沒有前綴的 RDPL 包先於所有編號 RDPL 包載入，並依字母順序排列，
所以編號 RDPL 包一律勝過沒有編號的。

想停用某個 RDPL 包又不想刪除它，在名稱末尾加上 .disabled：

    rdploader/RDPL1 SeasonalTextures.zip.disabled

該 RDPL 包會被略過，記錄檔裡會說明。去掉後綴即可重新啟用。


你可以修改什麼
--------------

材質、模型、方塊狀態、語言檔案、音效、字型、閃爍標語，以及模組在
assets 資料夾裡存放的其他一切，例如指南書或手冊。

進度、戰利品表、配方、標籤、函數、結構範本，以及模組在
data 資料夾裡存放的其他一切。它們屬於伺服器端，所以在專用伺服器上
同樣有效，修改後用 /reload 生效。

也可以把東西拿掉。recipe_removals 裡的檔案依名稱、命名空間或產物刪除
配方，disabled 裡的檔案把方塊和物品移出遊戲而不取消註冊，
所以世界保留原有的 id，刪除該檔案就能讓一切恢復。戰利品注入
會向既有的戰利品表加入一個池，而不是取代它，block_drops 則為
不屬於你的方塊新增或取代掉落物。熔爐配方、燃料燃燒時間、創造模式
分頁和音效事件也各有自己的檔案。

鐵砧也可以學會新的操作。anvils 裡的檔案指明一個物品、與它搭配放在
右側欄位的物品，以及鐵砧在指定等級下提供的附魔或結果。取出成品
可以獲得一項進度，物品也可以在獲得這項進度之前一直無法使用。

tooltips 裡的檔案會為物品和方塊加上灰色的提示行，並在附魔名稱下方
加上一段說明。

hardness 檔案為一組方塊設定挖掘時間和爆炸抗性，exposures 檔案
則定義輻射之類的危險：它作用於靠近指定方塊、攜帶指定物品或
處於指定維度的玩家，分為若干等級，每級施加效果和傷害，
也可以從附近的生物和玩家身上染上，或隨雨水降下。


新增內容
--------

資源包還可以新增自己的方塊、物品和流體，用 JSON 描述。你
不需要編寫或建構模組。

定義放在 data 底下，每種類型各占一個資料夾。「variants」裡的每個鍵
都是一個名稱，所以位於

    rdploader/data/mypack/blocks/ores.json

的檔案如果包含名為 ruby_ore 的變體，就會註冊為 mypack:ruby_ore。
檔案本身的名稱只用於分組。如果某個真正的模組已經註冊了該名稱，
模組勝出，你的變體會被略過。

最簡單的方塊只要幾行：

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

模型、方塊狀態、材質和語言項目仍需放在 assets 底下，
與這個資料夾裡的其他檔案做法相同。

下面每一項都是 data/<yourpack> 底下的一個資料夾：

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

方塊有以下幾種形態，由「type」欄位決定：

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

物品有以下幾種：

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

藥水類型的名稱取自語言鍵 item.minecraft.potion.effect.<baseName>，
其他形態則把 potion 換成 splash_potion、lingering_potion 或
tipped_arrow。potion_bottle 物品是你自己的容器：它和其他物品一樣
帶有 creativeTab，並裝著你在 potionTypes 裡列出的藥水類型。

villagers/<name>.json 檔案定義一種職業。trades/*.json 檔案為任何職業
新增交易，無論是你的還是 Minecraft 的，並指明職業和交易出現的
等級。

entities/<name>.json 檔案用既有的實體做出一個新實體。它指明所根據的
實體，以及不同之處：名稱、外觀、生命值和傷害、移動方式、戰鬥方式
以及掉落物。它是一個獨立的實體，有自己的生怪蛋和戰利品表，
被它根據的那個實體不受影響。它還可以帶有自己的儲存空間：物品欄位、
流體槽和能量緩衝，管線和纜線都能接入，玩家潛行並對它按右鍵即可
開啟。

villages/<name>.json 檔案新增一個城市或村莊可以用你的 .nbt 範本
建造的地塊，raids/<name>.json 檔案則在玩家把不祥預兆帶進村莊時，
向村莊派出一波波敵人。

cards/<name>.json 檔案在發生某件事時（例如玩家進入某個生態域）
在畫面上顯示一張卡片，同一個資料夾裡以本模組自帶訊息命名的檔案，
可以改變那則訊息的內容。

worldintro/<name>.json 檔案在有人進入世界時、取得控制權之前，
播放一連串頁面。文字是 assets/<yourpack>/texts 底下的純 .txt 檔案。
可以每位玩家播放一次，也可以每次加入都播放。

teams/<name>.json 檔案在遊戲的計分板上設立一個陣營並規定誰能加入，
scoring/<name>.json 檔案則是一個替這些陣營計分、並決定比賽
如何結束的目標。


整個世界
--------

資源包不限於單一事物。dimensions/<name>.json 註冊一個擁有自己的
地形、生態域和天空的維度。gates/<name>.json 為抵達維度設定
條件，例如持有或消耗某個物品。portal 類型的方塊會把走進去的人
送往另一個維度，portalframes 則讓玩家自己搭建並點燃框架。

worldtemplates/<name>.json 把一個世界的設定收進一個檔案，
這樣資源包可以一次提供整個世界的形態，而不必要求
修改十幾處設定。它也可以塑造主世界本身，例如海平面，
以及海洋是否為熔岩。

維度還可以設定它頭頂上的外觀和行為。它的霧、光照色調、太陽和月亮的
變暗、雲層以及熱浪都繪製在你自己的畫面上，天氣則決定是否下雨、
下雪或雷暴、陣雨持續多久，以及雨的顏色、粒子、音效和角度。

worldgen 不只是礦石。一個項目放置一種形狀，從你的方塊所組成的小團塊到
你自己的 .nbt 範本，並決定出現的頻率、高度和所在的
生態域。

biomes/<name>.json 檔案定義生態域：它的氣候和顏色、
構成它的方塊、裝飾它的東西、在其中生成的生物，以及它在哪裡生成。


到此為止
--------

這裡描述的是事物是什麼，而不是它隨時間做什麼。任何需要
方塊實體、畫面或每刻都執行的程式碼的東西，仍然需要真正的模組，
但有兩個例外：container 類型的方塊擁有帶自己畫面的物品欄，
實體變體可以帶有儲存空間。機器是做不到的；礦石、柵欄、食物
或流體則可以。


來自其他版本的資源包
--------------------

為本模組 1.12.2 版本製作的資源包可以原樣載入。zip 會在自身內部被
轉換一次，寫入一個對應本版本的 versions 資料夾，1.12.2 的檔案
保持原樣，所以同一個 zip 在每個版本上都能繼續使用。


查看你的變更
------------

按 F3+T 重新載入材質、模型、語言檔案和 assets 底下的其他一切。
在伺服器上，或對於 data 底下的任何內容，輸入 /reload。

如果你新增或刪除了檔案，請改用 /rdpl reload。編輯一個
原本就存在的檔案，只需 F3+T 或 /reload。

/rdpl list 顯示載入的每個資源包及其內容。把游標停在
資源包上即可查看。

/rdpl which minecraft:textures/block/stone.png 顯示是哪個資源包
提供了該檔案，以及它之下被遮蔽的資源包。

/rdpl config unused 列出 rdploader/config 裡不再被任何已安裝
資源包定義的選項檔案，/rdpl config prune 會刪除它們。
/rdpl pixelmap 顯示像素圖最終的樣子，/rdpl biome list 和 here
則告訴你生態域的資訊。

這些指令無需管理員權限，因為它們只讀取你自己電腦上的檔案。
在專用伺服器上，/rdplserver reload 重新掃描伺服器的副本，
/rdplserver list、which 和 unused 則針對伺服器作答。


出問題時
--------

先看記錄檔。logs/rdpl.log 列出每個已載入的資源包和每個被略過的
資源包及原因，任何出錯的地方都會記為警告並說明原因。

/rdpl unused 列出你的資源包中尚未被任何東西請求過的檔案，
這通常意味著路徑有拼字錯誤。請在遊戲載入完成後執行，
並注意有些檔案只在需要時才會載入，例如你所用語言之外的
其他語言。

大小寫很重要。如果你的檔案是 Stone.png 而遊戲請求的是
stone.png，它仍會載入，但警告會要你重新命名。請務必重新命名，
因為在這個模組之外，這個檔案根本找不到。
語言檔案最容易出錯：它們是 en_us.json，不是 en_US.json。

檢查你的檔案是否位於「assets」或「data」資料夾內。兩者都沒有的
zip 會被略過，記錄檔裡會說明。


進度與配方
----------

腳本新增或取代的配方，以腳本給它的名稱為名。
要讓某個進度解鎖它，請在這裡放一個指明該配方的進度檔案，
這樣進度就能重新完整運作。

請在腳本裡給這樣的配方一個固定的名稱。自動產生的名稱
可能在你一編輯配方時就變了，所以不能安全地讓進度指向它。


rdploader 資料夾本身可以用 config/resourcedatapackloader-common.toml 中的
rootDirectory 選項移動或重新命名。也可以用絕對路徑，
需要重新啟動。

在這個檔案旁邊放一個 pack.png，可以給資源包設定圖示。

這個檔案由模組寫入，並在內容變化時更新，
所以你在裡面輸入的任何內容，在下次啟動遊戲時都會被取代。

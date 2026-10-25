# Resource Data Pack Loader

**一個資料夾，即可覆寫 Minecraft 或任何模組提供的內容，以 JSON 定義新內容，並控制世界的生成方式；在每個世界、用戶端與伺服器上皆能生效，不需要玩家開啟任何開關。**

十四個可直接運作的範例。將其中任何一個直接放進 `rdploader`，看看每個檔案是怎麼寫的。

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) 涵蓋大部分功能：方塊、物品、生態域、一個維度、一個世界範本，以及所有世界生成形狀。
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) 將主世界變成一片空無一物的虛空，世界生成的內容懸浮在半空中，每個高度帶各有一種形狀，方便單獨觀察每一種。
- [RDPLExampleVeinShapes.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapes.zip) 在一般的主世界中放入三種礦脈，每種對應一種礦脈樣式（單純、條帶狀與管狀），各有富礦、普通與貧礦三個等級，並在其上方的地表放置幾個標記方塊，供你據此探勘。
- [RDPLExampleVeinShapesVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleVeinShapesVoid.zip) 將同樣的三種礦脈樣式懸浮在空無一物的虛空中，讓每種形狀都能完整看清。
- [RDPLExampleDeepWorld.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleDeepWorld.zip) 將主世界變成 rubic 世界，在原版世界之下生成 256 格方塊的世界、之上生成 128 格：包括深層石頭混合、現代雜訊洞穴、峽谷、條帶狀礦脈、三個可向下探索的洞穴區域，以及由同一套雜訊切割而成、懸在頭頂的浮空島。
- [RDPLExampleContainers.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleContainers.zip) 新增附有物品欄的方塊與隨身物品，格數從三格到允許的最大值各種尺寸齊備，並附有戰利品表、以原版材質圖染色而成的箱子模型、全部以像素圖繪製的材質，以及兩個可在 Baubles 中配戴的小袋。
- [RDPLExampleMegaCity32.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity32.zip) 產生一個超平坦世界，其中有一座刻意打造得極其龐大的村莊，擴展到一千個地塊並固定在原點；街道裝飾成混凝土道路、人行道、虛線中線與路燈，街道下方有下水道，更下方是兩條附車站的地鐵線，另有一條穿過城鎮的鐵路，建築則由四種尺寸、三種外觀的結構地圖組合而成，而不是原版房屋。
- [RDPLExampleMegaCity64.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleMegaCity64.zip) 是同一座城市，建在一個頂部高度為 512、雲層抬升到 384 的 rubic 世界上，因此高塔比街道高出 256 格，而且每個街區會隨機取 16、32 或 64 的方塊深度，讓粗網格與細網格混合在一起。這座城市沒有邊界：世界中的每個街區都是一座獨立的城鎮，無論走到哪裡，街道都會一路延伸下去。
- [RDPLExampleCityCustomMap.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleCityCustomMap.zip) 以一張城市地圖來繪製同一座城市，而不是隨機生成：一張每格 48 個方塊的字元網格，搭配調色盤指明街道、廣場、小巷與依權重挑選的建築，因此街區配置由手工排定。
- [MCTKamikazeDemo.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/MCTKamikazeDemo.zip) 讓四個陣營在永夜籠罩的基岩競技場中互相對抗：每一方都是真正的原版計分板隊伍，其生物在生成時加入；每擊殺一隻其他陣營的生物，本方就得分；每回合兩分鐘，結束時顯示結算畫面，三個回合構成一場比賽。
- [RDPLExampleRaid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleRaid.zip) 將一個平坦世界布置在一座村莊周圍，村莊的水井是一座鐘亭，玩家加入時會獲得不祥之兆：共五波敵人，包括原版的災厄村民、一名女巫，以及資源包自帶的掠奪者、突襲隊長、擲斧者與劫毀獸；不祥之兆與村莊英雄是資源包自帶的效果，並配有像素圖圖示，另有一種能讓不祥之兆重新出現的飲品，以及結束突襲的函數。
- [RDPLExampleGalacticraft.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGalacticraft.zip) 需要 Galacticraft，會在星圖上加入一個自己的星系，其中有一顆搭乘二級火箭即可抵達的行星，擁有自己的天空、重力、晝長、天氣與大氣；還有一顆只能觀賞、無法降落的行星，並且在搭配 GalaxySpace 時，於天倉五周圍多出一顆冰凍行星。
- [RDPLExampleGameHall.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleGameHall.zip) 將一個平坦世界變成遊戲大廳：資源包自帶的加權骰子、一副會自動重新洗牌的生物牌，以及一副在洗牌前一直為空的命運牌、一個右鍵即可擲出 2d6 的骰盅、與莊家輪流擲骰、平手時以抽牌決勝的骰子對決，以及以生物作棋子、與電腦對弈的西洋棋和跳棋。
- [RDPLExampleColony.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleColony.zip) 會在關閉介紹後自動建起一座工作場地，讓村民工人承擔每一種工作訂單：作業範圍隨鎬子提升的礦工、把砍下的木頭留在身上的伐木工、始終保持固定小麥庫存的農夫，以及搬運鵝卵石的搬運工；每個訂單都可透過告示牌、工具、雇用或庫存箱來開啟。
---

## 目錄

**開始使用**
- [它是什麼](#它是什麼)
- [檔案放在哪裡](#檔案放在哪裡)
- [如何閱讀表格](#如何閱讀表格)
- [唯一的規則](#唯一的規則)
- [整理資源包](#整理資源包)
- [資源包：誰優先](#資源包誰優先)

**資源包如何運作**
- [定義如何運作](#定義如何運作)
- [可以覆寫的內容](#可以覆寫的內容)
- [伺服端資源包](#伺服端資源包)
- [登錄表重新命名](#登錄表重新命名)
- [模組 API](#模組-api)
- [為 1.20.1、1.21.1 和 26.x 編寫的資源包](#為-12011211-和-26x-編寫的資源包)

**方塊與物品**
- [方塊](#方塊)
- [容器](#容器)
- [模型、方塊狀態與材質](#模型方塊狀態與材質)
- [依類型區分的方塊狀態](#依類型區分的方塊狀態)
- [讓原版正確對待你的方塊](#讓原版正確對待你的方塊)
- [物品](#物品)
- [流體](#流體)
- [材料、分頁、音效、礦物辭典](#材料分頁音效礦物辭典)
- [屬性覆寫](#屬性覆寫)
- [硬度群組](#硬度群組)

**合成、戰利品與交易**
- [停用的方塊與物品](#停用的方塊與物品)
- [熔爐配方與燃料](#熔爐配方與燃料)
- [配方鎖](#配方鎖)
- [工具提示](#工具提示)
- [語言閘門](#語言閘門)
- [藥水、藥水類型與釀造](#藥水藥水類型與釀造)
- [附魔](#附魔)
- [鐵砧操作](#鐵砧操作)
- [詞綴與插槽](#詞綴與插槽)
- [附魔台](#附魔台)
- [方塊掉落物](#方塊掉落物)
- [右鍵收穫](#右鍵收穫)
- [礦脈連鎖挖掘](#礦脈連鎖挖掘)
- [搬運](#搬運)
- [玩家戰利品](#玩家戰利品)
- [飲食多樣性](#飲食多樣性)
- [村民與交易](#村民與交易)

**生物與危險**
- [實體變體](#實體變體)
- [生物等級](#生物等級)
- [最大生命值獎勵](#最大生命值獎勵)
- [工作訂單](#工作訂單)
- [曝露設定](#曝露設定)

**世界**
- [世界範本](#世界範本)
- [遊戲規則](#遊戲規則)
- [生態域](#生態域)
- [維度](#維度)
- [Galacticraft 天體](#galacticraft-天體)
- [傳送門與閘門](#傳送門與閘門)
- [Rubic 世界](#rubic-世界)
- [深層世界](#深層世界)
- [洞穴區域](#洞穴區域)

**生成世界**
- [世界生成項目](#世界生成項目)
- [形狀](#形狀)
- [擴散](#擴散)
- [結構地圖](#結構地圖)
- [村莊地塊](#村莊地塊)
- [城市配置地圖](#城市配置地圖)
- [連綿城市](#連綿城市)
- [回溯生成](#回溯生成)
- [預先生成](#預先生成)

**遊戲模式**
- [世界介紹](#世界介紹)
- [隊伍](#隊伍)
- [計分](#計分)
- [突襲](#突襲)
- [卡片](#卡片)
- [對話](#對話)
- [骰子與牌組](#骰子與牌組)

**控制**
- [控制層](#控制層)
- [各群組的作用](#各群組的作用)

**其他模組**
- [Universal Tweaks](#universal-tweaks)
- [Mo' Villages](#mo-villages)
- [CoFH World](#cofh-world)
- [Lost Cities](#lost-cities)
- [Blast Plaster 整合](#blast-plaster-整合)
- [墓碑模組](#墓碑模組)

**參考資料**
- [值清單](#值清單)
- [資料夾清單](#資料夾清單)
- [指令](#指令)
- [須知](#須知)
- [遇到問題時](#遇到問題時)
- [額外功能：原版調整](#額外功能原版調整)
- [額外功能：修正 JEI 外掛程式衝突](#額外功能修正-jei-外掛程式衝突)
- [額外功能：減少啟動錯誤](#額外功能減少啟動錯誤)

---

# 開始使用

## 它是什麼

*開始使用*

Resource Data Pack Loader（RDPL）會讀取單一資料夾 `rdploader`，並負責三項工作：

- **覆寫。** 資料夾中的檔案會取代遊戲或模組原本會載入的檔案。不需要切換開關，不需要逐個世界設定，也不需要玩家啟用任何東西。
- **新內容。** 以 JSON 定義來註冊方塊、物品、流體、生態域、維度、藥水與村民。不需要 Java，也不需要 jar。
- **控制。** 可阻止礦石、生態域、結構或配方生成，壓平基岩，設定生成率，讓主世界變成虛空，並設定世界預設值。

## 檔案放在哪裡

*開始使用*

本指南中的每個路徑都從 `assets/` 起算，因此對於命名空間為 `mypack` 的資源包，`<namespace>/blocks/*.json` 在磁碟上就是 `assets/mypack/blocks/ruby_ore.json`。每個章節都會在標題下方重複列出自己的路徑，並說明該路徑對應到什麼。

| 路徑                                                                                            | 內容                                                                                                                |
| ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| `<namespace>/blocks/*.json`                                                                     | 方塊定義。[方塊](#方塊)                                                                                             |
| `<namespace>/items/*.json`                                                                      | 物品定義。[物品](#物品)                                                                                             |
| `<namespace>/fluids/*.json`                                                                     | 附帶方塊與桶的流體。[流體](#流體)                                                                                   |
| `<namespace>/materials/*.json`                                                                  | 工具與盔甲的材料。[材料、分頁、音效、礦物辭典](#材料分頁音效礦物辭典)                                               |
| `<namespace>/tabs/*.json`                                                                       | 創造模式分頁。[材料、分頁、音效、礦物辭典](#材料分頁音效礦物辭典)                                                   |
| `<namespace>/sounds/*.json`                                                                     | 音效事件。[材料、分頁、音效、礦物辭典](#材料分頁音效礦物辭典)                                                       |
| `<namespace>/oredict/*.json`                                                                    | 礦物辭典名稱。[材料、分頁、音效、礦物辭典](#材料分頁音效礦物辭典)                                                   |
| `<namespace>/biomes/*.json`                                                                     | 生態域定義。[生態域](#生態域)                                                                                       |
| `<namespace>/worldgen/*.json`                                                                   | 生成什麼，以及在哪裡生成。[世界生成項目](#世界生成項目)                                                             |
| `<namespace>/caveregions/*.json`                                                                | 繪製在地下的具名區域。[洞穴區域](#洞穴區域)                                                                         |
| `<namespace>/dimensions/*.json`                                                                 | 維度定義。[維度](#維度)                                                                                             |
| `<namespace>/celestial/*.json`                                                                  | Galacticraft 星圖上的星系與天體。[Galacticraft 天體](#galacticraft-天體)                                            |
| `<namespace>/worldtemplates/*.json`                                                             | 將整個世界的設定放進一個檔案。[世界範本](#世界範本)                                                                 |
| `<namespace>/worldintro/*.json`                                                                 | 玩家進入世界時顯示的頁面。[世界介紹](#世界介紹)                                                                     |
| `<namespace>/gates/*.json`                                                                      | 傳送門與維度的條件。[傳送門與閘門](#傳送門與閘門)                                                                   |
| `<namespace>/gamerules/*.json`                                                                  | 新世界的遊戲規則。[遊戲規則](#遊戲規則)                                                                             |
| `<namespace>/teams/*.json`                                                                      | 原版計分板上的陣營，以及加入陣營的對象。[隊伍](#隊伍)                                                               |
| `<namespace>/scoring/*.json`                                                                    | 目標、積分，以及比賽如何結束。[計分](#計分)                                                                         |
| `<namespace>/raids/*.json`                                                                      | 玩家把不祥之兆帶進村莊時，前來突襲村莊的各波敵人。[突襲](#突襲)                                                     |
| `<namespace>/entities/*.json`                                                                   | 以既有實體為基礎建立的實體變體。[實體變體](#實體變體)                                                               |
| `<namespace>/hardness/*.json`                                                                   | 針對一組方塊的挖掘時間與爆炸倍率。[硬度群組](#硬度群組)                                                             |
| `<namespace>/ranks/*.json`                                                                      | 生物生成時可以抽取的隨機層級：額外生命值、傷害、效果、掉落物和名稱標籤。[生物等級](#生物等級)                       |
| `<namespace>/hearts/*.json`                                                                     | 玩家透過進度、多樣的飲食或隊伍分數獲得的額外心數，隨玩家一起儲存。[最大生命值獎勵](#最大生命值獎勵)                 |
| `<namespace>/orders/*.json`                                                                     | 實體變體在箱子旁為玩家完成的工作。[工作訂單](#工作訂單)                                                             |
| `<namespace>/exposures/*.json`                                                                  | 讓靠近指定方塊、攜帶指定物品或位於指定維度、生態域、結構、洞穴區域或高度的玩家受到曝露的危險。[曝露設定](#曝露設定) |
| `<namespace>/enchanting/*.json`                                                                 | 附魔台提供什麼、要求什麼、拿走什麼：依附魔、依物品以及整體。 [附魔台](#附魔台)                                      |
| `<namespace>/overrides/<target>/<name>.json`                                                    | 就地修改既有方塊、物品與藥水類型的屬性。[屬性覆寫](#屬性覆寫)                                                       |
| `<namespace>/villages/*.json`                                                                   | 村莊可以興建的地塊。[村莊地塊](#村莊地塊)                                                                           |
| `<namespace>/pathintersects/*.json`                                                             | 繪製在村莊道路交會處的圖樣。[村莊道路](#村莊道路)                                                                   |
| `<namespace>/structuremaps/*.json`                                                              | 在網格上組合成一棟大型建築的範本。[結構地圖](#結構地圖)                                                             |
| `<namespace>/citymaps/*.json`                                                                   | 手繪的街道配置，村莊依此規劃而非自行生長。[城市配置地圖](#城市配置地圖)                                             |
| `<namespace>/portalframes/*.json`                                                               | 玩家可以建造並點燃的框架。[傳送門框架](#傳送門框架)                                                                 |
| `<namespace>/blastplaster/*.json`                                                               | Blast Plaster 在爆炸後依維度執行的動作。[Blast Plaster 整合](#blast-plaster-整合)                                   |
| `<namespace>/structures/*.nbt`                                                                  | 供樹苗、`imprint` 與模組覆寫使用的範本。[可以覆寫的內容](#可以覆寫的內容)                                           |
| `<namespace>/recipes/*.json`                                                                    | 合成配方，新增或取代。[可以覆寫的內容](#可以覆寫的內容)                                                             |
| `<namespace>/recipe_removals/*.json`                                                            | 依名稱、命名空間或產出刪除的配方。[可以覆寫的內容](#可以覆寫的內容)                                                 |
| `<namespace>/recipe_locks/*.json`                                                               | 玩家取得進度之前保持鎖定的配方。[配方鎖](#配方鎖)                                                                   |
| `<namespace>/spawn_locks/*.json`                                                                | 條件滿足之前不會生成的生物。[生成鎖](#生成鎖)                                                                       |
| `<namespace>/disabled/*.json`                                                                   | 退出遊戲的方塊與物品。[停用的方塊與物品](#停用的方塊與物品)                                                         |
| `<namespace>/furnace/*.json`                                                                    | 新增與移除的熔爐配方。[熔爐配方與燃料](#熔爐配方與燃料)                                                             |
| `<namespace>/tooltips/*.json`                                                                   | 額外的工具提示行。[工具提示](#工具提示)                                                                             |
| `<namespace>/fuels/*.json`                                                                      | 燃燒時間。[熔爐配方與燃料](#熔爐配方與燃料)                                                                         |
| `<namespace>/brewing/*.json`                                                                    | 釀造台配方。[藥水、藥水類型與釀造](#藥水藥水類型與釀造)                                                             |
| `<namespace>/potions/*.json`                                                                    | 藥水效果。[藥水、藥水類型與釀造](#藥水藥水類型與釀造)                                                               |
| `<namespace>/potion_types/*.json`                                                               | 以這些效果組成的瓶裝藥水。[藥水、藥水類型與釀造](#藥水藥水類型與釀造)                                               |
| `<namespace>/enchantments/*.json`                                                               | 自訂附魔。[附魔](#附魔)                                                                                             |
| `<namespace>/villagers/*.json`                                                                  | 村民職業。[村民與交易](#村民與交易)                                                                                 |
| `<namespace>/trades/*.json`                                                                     | 各職業買進與賣出的東西。[村民與交易](#村民與交易)                                                                   |
| `<namespace>/loot_tables/*.json`                                                                | 戰利品表，取代原有的。[可以覆寫的內容](#可以覆寫的內容)                                                             |
| `<namespace>/loot_injections/*.json`                                                            | 加入既有戰利品表的一個池。[可以覆寫的內容](#可以覆寫的內容)                                                         |
| `<namespace>/block_drops/*.json`                                                                | 資源包並不擁有的方塊所額外增加或取代的掉落物。[方塊掉落物](#方塊掉落物)                                             |
| `<namespace>/anvils/*.json`                                                                     | 鐵砧為指定物品附加的附魔、由此獲得的進度，以及在此之前的鎖定。[鐵砧操作](#鐵砧操作)                                 |
| `<namespace>/affixes/*.json`                                                                    | 物品在玩家第一次持有時隨機出的屬性加成與插槽。[詞綴與插槽](#詞綴與插槽)                                             |
| `<namespace>/gems/*.json`                                                                       | 鐵砧為換取屬性加成而嵌入插槽的物品。[詞綴與插槽](#詞綴與插槽)                                                       |
| `<namespace>/cards/*.json`                                                                      | 由觸發器顯示的螢幕卡片，以及本模組自己說出的訊息。[卡片](#卡片)                                                     |
| `<namespace>/dialogs/*.json`                                                                    | 與實體變體配合的分支對話：文字節點，以及前進、執行函式或顯示卡片的回覆。[對話](#對話)                               |
| `<namespace>/dice/*.json`                                                                       | 帶有加權面的資源包骰子、牌組、誰能聽到擲骰結果，以及結果的措辭。[骰子與牌組](#骰子與牌組)                           |
| `<namespace>/games/*.json`                                                                      | 以生物作棋子進行的桌上遊戲：棋盤、棋子與其走法，以及結果的獎勵。[桌上遊戲](#桌上遊戲)                               |
| `<namespace>/veins/*.json`                                                                      | 工具一次挖完的方塊組，可設上限和飢餓消耗。[礦脈連鎖挖掘](#礦脈連鎖挖掘)                                             |
| `<namespace>/harvests/*.json`                                                                   | 右鍵即可收穫並補種的作物。[右鍵收穫](#右鍵收穫)                                                                     |
| `<namespace>/carries/*.json`                                                                    | 玩家潛行時可以拿起並放回的方塊和生物。[搬運](#搬運)                                                                 |
| `<namespace>/player_loot/*.json`                                                                | 玩家死亡時擲出的戰利品表。[玩家戰利品](#玩家戰利品)                                                                 |
| `<namespace>/diets/*.json`                                                                      | 讓重複吃同一種食物飽足較少、飲食多樣則飽足較多的規則。 [飲食多樣性](#飲食多樣性)                                    |
| `<namespace>/advancements/*.json`                                                               | 進度。[可以覆寫的內容](#可以覆寫的內容)                                                                             |
| `<namespace>/functions/*.mcfunction`                                                            | 函數檔案。[可以覆寫的內容](#可以覆寫的內容)                                                                         |
| `<namespace>/registry_remap/*.json`                                                             | 將舊名稱對應到新名稱。[登錄表重新命名](#登錄表重新命名)                                                             |
| `<namespace>/texts/*.txt`                                                                       | 純文字檔案，供世界介紹使用。[世界介紹](#世界介紹)                                                                   |
| `<namespace>/langgates/<name>/gate.json`                                                        | 僅在資源包的條件成立時才載入的語言條目。[語言閘門](#語言閘門)                                                       |
| `<namespace>/models/`、`<namespace>/blockstates/`、`<namespace>/textures/`、`<namespace>/lang/` | 一般的資產資料夾。[模型、方塊狀態與材質](#模型方塊狀態與材質)                                                       |

## 如何閱讀表格

*開始使用*

每個檔案都是標準 JSON。以下是一個具代表性的世界生成項目：

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

本指南中的鍵表會說明某個鍵是否必填、它存放什麼，以及省略時的預設值。無法辨識的值會被記入日誌並以預設值取代，不會使遊戲當機。全文使用的值類型如下：

| 當表格寫的是                                           | 你要寫的是                                                                 |
| ------------------------------------------------------ | -------------------------------------------------------------------------- |
| 整數                                                   | `8`                                                                        |
| 整數，刻                                               | `100`（20 刻 = 1 秒）                                                      |
| 整數或範圍                                             | `8`，或以 `{ "min": 4, "max": 12 }` 在兩者之間隨機取值                     |
| 0 到 15、1 到 100 等                                   | 落在該範圍內的整數                                                         |
| 小數                                                   | `0.5`                                                                      |
| 布林值                                                 | `true` 或 `false`                                                          |
| 字串                                                   | `"words in quotes"`                                                        |
| 方塊名稱、物品名稱                                     | `"minecraft:stone"`，中繼資料作為第三段：`"minecraft:stone:3"`             |
| `namespace:name`                                       | `"mypack:ruby_ore"`                                                        |
| 生態域名稱、音效名稱、分頁名稱                         | 同樣是加引號的 `namespace:name` 形式                                       |
| 十六進位顏色                                           | 六位十六進位數字，`"A0C8FF"`，`#` 可有可無                                 |
| 材質路徑                                               | `"mypack:blocks/ruby_ore"`                                                 |
| 整數清單                                               | `[0, -1]`                                                                  |
| 方塊名稱清單                                           | `["minecraft:stone", "minecraft:andesite"]`                                |
| 生態域名稱清單                                         | `["minecraft:extreme_hills", "mypack:ruby_hills"]`                         |
| 辭典類型清單                                           | `["MOUNTAIN", "FOREST"]`                                                   |
| 模組 id 或資源包命名空間清單                           | `["quark", "mypack"]`                                                      |
| 物件清單                                               | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`，鍵依該物件自己的表 |
| 物件                                                   | `{ "type": "cluster" }`，鍵依其自己的表                                    |
| 由角色對應到生態域的物件，或由變體名稱對應到變體的物件 | 鍵是前者，值是後者：`{ "ocean": "mypack:ruby_ocean" }`                     |

大多數定義也接受 `requires`，這是一份由模組 id 或資源包命名空間組成的清單，列出的項目必須存在，否則該檔案會被跳過。

## 唯一的規則

*開始使用*

打開 jar，找到你想修改的檔案，並從 `assets` 起複製它的路徑：

```
assets/minecraft/textures/blocks/iron_ore.png        (in the Minecraft jar)
rdploader/assets/minecraft/textures/blocks/iron_ore.png    (your override)
```

`assets` 之後的路徑一律與 jar 內的路徑完全相同。沒有任何東西會被改名或移動。

## 整理資源包

*開始使用*

散裝檔案可在 `rdploader/assets/<namespace>/` 下運作。也可以把檔案分組，做成 zip。`rdploader` 中的資料夾永遠不會被視為資源包：它會被跳過，並在日誌中留下警告，因此請先把資源包壓縮成 zip 再放進去。

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png
rdploader/MyTextures.zip
```

**放錯資料夾的資源包。** 啟動時，在讀取 `rdploader` 之前，RDPL 會檢查遊戲的 `resourcepacks` 資料夾，並把找到的每個 RDPL 資源包 zip 移進 `rdploader`。當 zip 內含 RDPL 定義檔案時，它就是 RDPL 資源包，例如 `assets/<namespace>/blocks/`，或者對於現代資源包而言是 `data/<namespace>/blocks/`。一般的資源包會留在原處。若 `rdploader` 已有同名的 zip，該 zip 會留在原處，位於資料夾中的 RDPL 資源包也一樣；兩者都會產生警告。每一次移動都會寫入 `logs/rdpl.log`。被移動的資源包不再顯示在資源包清單中，之後 RDPL 會從 `rdploader` 載入它。

**優先順序。** 當兩個資源包含有相同的檔案時，請在名稱前加上 `RDPL` 與一個數字作為前綴；數字越大載入得越晚，也就越優先：

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

不分大小寫；數字之後的空格、連字號或底線可有可無；前綴不會顯示在顯示名稱中。沒有前綴的資源包最先載入，並且會輸給任何有編號的資源包。優先順序也會決定世界生成項目的順序，當一個資源包鋪下的方塊被另一個取代時，這一點就很重要。

**同一個資源包內部。** 命名空間依字母順序載入；每個命名空間內，先載入版本資料夾中的檔案，再載入資源包其餘部分的檔案，各自依路徑排序。兩個檔案不會有相同的路徑，所以大多數內容在同一個資源包內不會衝突。檔案內部命名的內容則可能衝突：隊伍、計分目標與骰子保留第一個定義並記錄一則錯誤，而遊戲規則、創造模式分頁、收穫規則、Blast Plaster 設定與登錄表重新命名則保留最後一個。遊戲僅以路徑命名，因此同一路徑出現在兩個命名空間中時，保留第一個。

**停用資源包**的方法是在其名稱後加上 `.disabled`。

**一個 zip 適用於所有版本。** zip 可以為其支援的每個 Minecraft 版本各帶一個 `versions/<version>/` 資料夾：`versions/1.12.2/`、`versions/1.20.1/`、`versions/1.21.1/`，在 26.x 上則是它所執行的確切版本，`versions/26.1.2/`、`versions/26.2/` 或 `versions/26.3/`。每個資料夾的配置都與該版本資源包的根目錄相同，包括 `pack.mcmeta`。執行中版本的資料夾下的檔案，會取代它所對應的根目錄檔案，並依本版本讀取的路徑讀入：在 1.20.1 上，1.12.2 根目錄中的 `assets/mypack/blocks/ruby_ore.json` 會被 `versions/1.20.1/data/mypack/blocks/ruby_ore.json` 取代，`assets/mypack/textures/blocks/ruby_ore.png` 會被 `versions/1.20.1/assets/mypack/textures/block/ruby_ore.png` 取代。在 1.21.1 和 26.x 上，原版資料夾改用單數名稱，因此根目錄的 `recipes/` 檔案會被 `recipe/` 底下的檔案取代。根目錄由所有版本共用，其他版本的資料夾永遠不會被讀取。執行中版本的資料夾一旦存在，不論是轉換寫入還是資源包作者放入，根目錄就不再被轉換：本版本原樣讀取的根目錄檔案（如音效）仍會載入，而為其他版本撰寫、需要移動或重寫的根目錄檔案則全部略過，因此取代它們的內容應放進版本資料夾。把所有版本讀取方式相同的內容放在根目錄，只把有差異的部分放進版本資料夾，一個 zip 就能在全部四條線上載入。

**資源包可在版本之間轉換。** 載入為另一條版本線編寫的資源包時，會在第一次載入時以同樣的方式轉換，並將變更的內容寫入它自己的 `versions/<version>/` 資料夾，如上所述；這會在載入時自動發生，包括由 `/rdpl reload` 或 `/rdplserver reload` 觸發的那次載入，不需要另外的指令。任意兩個版本之間都可以雙向轉換，因此 1.12.2、1.20.1、1.21.1 或 26.x 的資源包都能在其他任何一條線上載入。26.3 的資源包同樣能在每條線上載入，在較舊的線上透過 26.2 格式讀取，而較舊的資源包也能在 26.3 上載入。一方有而另一方容納不了的內容會被捨棄，日誌會逐一指明：從 26.3 向下轉換時，被捨棄的包括除錯密度函數、資源包自己的含水層排除與地表高度，以及 26.2 沒有的指令；向上轉換到 26.3 時，被捨棄的包括生成目標的 `depth` 與 `offset`，以及雜訊路由器的礦脈鍵。在 1.12.2 上，現代資源包的原版世界生成內容（例如其生態域、地物與雜訊設定）以及其 `neoforge` 資料夾也會被略去，因為 1.12.2 對這兩者都沒有對應物。

## 資源包：誰優先

*開始使用*

預設情況下，RDPL 檔案位於玩家所選資源包之上，因此資源包無法覆寫它們。在 `RDPL` 前綴之後加上 `O` 或 `N`，即可逐個 RDPL 包決定：

```
rdploader/RDPLO Branding        永遠優先；資源包無法更動它
rdploader/RDPLN BaseTextures    資源包可以覆寫它
rdploader/RDPL1O Seasonal       優先順序與覆寫合併使用
```

沒有字母的 RDPL 包遵循設定選項 `overrideResourcePacks`。`/rdpl list` 會標出具有覆寫權的 RDPL 包。字母必須位於前綴末尾（其後接空格、連字號、底線或什麼都沒有），因此 `RDPLOverhaul` 是一個名為 `Overhaul` 的 RDPL 包，而不是帶有 `O` 旗標。

---

# 資源包如何運作

## 定義如何運作

*資源包如何運作*

資源包的資料夾有兩種用途：一些用來描述新事物，其餘的用來取代遊戲或模組已有的檔案，後者見[可以覆寫的內容](#可以覆寫的內容)。對於第一種，路徑就是識別：位於 `assets/mypack/blocks/ruby_ore.json` 的檔案會註冊一個名為 `mypack:ruby_ore` 的方塊。

註冊發生在 Forge 提供的最低優先順序，所以如果真正的模組註冊了同名內容，就由模組勝出，你的檔案會被忽略。這裡沒有任何東西可以取代模組。

**界線在哪裡。** 凡是需要自己的方塊實體、GUI、物品欄或逐刻邏輯的東西，都需要真正的模組。除此之外的一切都可以做。

### 你的命名空間就是你的模組

*定義如何運作*

你選擇的命名空間，在一切實際用途上都相當於一個模組 id。它不會作為模組被載入，也永遠不會出現在模組清單中，但所有讀取模組 id 的地方都會讀取你的命名空間：

- 登錄名稱形如 `mypack:ruby_ore`，與模組的完全一樣，並且會寫入每一個包含它們的存檔。
- 設定中的礦石、生態域、生成器與配方白名單都會比對它，因此 `oreWhitelist = mypack` 會保留你的礦石，並擋掉其他所有人的礦石。
- `/rdpl which`、`/rdplserver oregen` 與各類報告都依它分組。
- JEI、礦物辭典與其他模組的查詢，都以同樣的方式看待它。

所以請在一開始選定一個名稱，之後永遠不要更改。重新命名命名空間會讓世界中已經放置的一切變成孤兒，就像模組更改自己的 id 一樣，而 `registry_remap` 正是用來修復這個問題的。

這是雙向的：`requires` 既接受已安裝的模組 id，也同樣接受資源包命名空間，因此一個資源包可以依賴另一個，並在後者未安裝時被跳過。

**缺少模組會讓遊戲停止，就像模組自己的依賴一樣。** 你的資源包中任何位置的 `requires` 所列出的每個模組 id，都會在任何內容載入之前，作為本模組的依賴交給 Forge。如果有一個沒有安裝，你會在用戶端或專用伺服器上看到標準的「缺少模組」畫面，列出所需的模組，期間不會生成或註冊任何東西。

缺少的*資源包*則不同。資源包命名空間不是模組，因此永遠不會觸發那項檢查，該定義會被跳過，`logs/rdpl.log` 會寫入一行說明缺少了什麼，遊戲則繼續執行。如果你預期的某個方塊沒有出現在創造模式分頁中，首先應該查看的就是那行日誌。

`requires` 只接受單純的 id。沒有版本範圍語法，所以它只能表明某個模組必須存在，而不能指定是哪個版本。

本模組自己的兩個 id，`resourcedatapackloader` 與 `resourcedatapackloader_mixin`，是保留的。在它們之下定義內容會被忽略並記入日誌，因為那等於宣稱擁有本模組所註冊的東西。覆寫本模組自己的資產仍然沒有問題，只是不能在那裡註冊內容。

下面的每個表都遵循[如何閱讀表格](#如何閱讀表格)中的慣例。

大多數定義也接受 `requires`，這是一份由模組 id 或資源包命名空間組成的清單，列出的項目必須存在，否則該檔案會被跳過。

### 資源包選項

*定義如何運作*

資源包可以在 `assets` 旁邊帶一個 `config` 資料夾，其中存放由真假選項及其預設值構成的 JSON 檔案：

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

頂層帶有 `"hide": true` 的檔案，會讓該資源包的選項完全不出現在選項畫面與產生的檔案中，而這些選項仍會依預設值控制內容。有兩種情況需要這樣做：尚未準備好發布的內容，以及範本資源包，其選項只是把各定義串聯起來的機制，而不是任何人應該去做的選擇。移除該鍵即可發布它們。這也可以逐個選項使用：選項物件內部的 `"hide": true` 只會隱藏那一個選項，這樣已完成的資源包就能帶有控制未完成內容的開關，或範本閘門，而兩者都不會顯示出來：

    { "enablePackB": { "default": false, "hide": true } }

由於被隱藏的選項無法切換，預設為 true 而被隱藏的選項實際上等於強制開啟，適用於必須透過選項機制保持串接、但不屬於任何人選擇的內容。

選項也可以是一個帶有說明的物件，說明會顯示在選項畫面中該選項名稱的下方：

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

選項檔案接受的所有鍵：

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

| 鍵              | 必填 | 值             | 預設    | 作用                                                                           |
| --------------- | ---- | -------------- | ------- | ------------------------------------------------------------------------------ |
| 選項名稱        | 是   | 布林值，或物件 |         | `true` 或 `false` 即為該選項的預設值。物件則帶有下面三個鍵                     |
| 頂層的 `hide`   | 否   | 布林值         | `false` | 讓此資源包的選項完全不出現在選項畫面與產生的檔案中，而它們仍會依預設值控制內容 |
| `default`       | 是   | 布林值         |         | 使用者變更之前的選項值。沒有布林值 `default` 的物件會被忽略，並產生警告        |
| 選項內的 `hide` | 否   | 布林值         | `false` | 隱藏那一個選項，使其無法切換並保持預設值                                       |
| `description`   | 否   | 字串           | 無      | 顯示在選項畫面中該選項名稱的下方                                               |

啟動時，資源包的選項檔案會變成一個歸使用者所有的真正設定檔，以資源包命名，即 `rdploader/config/PackA.json`，建立時帶有資源包的預設值，並在資源包更新時合併，因此新選項會加入，而不會動到使用者已設定的內容。變更會在下次遊戲啟動時生效，只有語言閘門讀取的選項例外，按下「完成」後立即生效。選項只屬於具名的資源包，也就是 zip，因為產生的檔案是以資源包命名；`rdploader/assets` 下的散裝檔案沒有資源包名稱，也不帶選項，所以如果需要開關，請把散裝內容壓縮成具名的資源包。

任何定義的 `requires` 清單都可以用 `config:` 項目指定一個選項：`"requires": ["config:enableTestingContent"]` 只會在該選項為 true 時註冊該內容，就如同缺少模組時會跳過一樣。只寫名稱時會檢查每個資源包的檔案，且所有定義了該名稱的資源包必須一致；`"config:PackA:enableTestingContent"` 則指定單一資源包。沒有任何資源包定義的選項視為 false，並只警告一次。

`file:` 項目則以遊戲資料夾下是否存在某個檔案或資料夾作為條件，用來把內容與 RDPL 自己的資源包以外的東西連動，例如另一個模組的資源包：`"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` 只會在安裝了那個確切的檔案時才註冊該內容。路徑是相對於遊戲資料夾，一律使用正斜線，且不得含有 `..`。

### 繼承定義

*定義如何運作*

方塊或物品定義可以用 `"inherits"` 從同一類型的另一個定義開始，指定任一變體的登錄名稱，然後覆寫有差異的部分：

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "meta": 0, "hardness": 4.0 } } }

子定義會複製父定義檔案與所指定變體的每一項數值，檔案順序無關緊要，繼承鏈從父層開始解析，而循環或缺少父層的情況會被記入日誌，子定義則保持原樣。子定義所寫的欄位會取代繼承而來的值；巢狀的變體屬性會逐項覆寫，但 `requires` 這類清單則是整份取代，因此請寫出想要的完整清單。方塊只能繼承方塊，物品只能繼承物品。

### 方塊與物品範本

*定義如何運作*

父定義可以是一個永遠不會進入遊戲的純範本，因為繼承讀取的是定義檔案本身，而不是已註冊的內容。把範本放在一個被強制關閉的隱藏選項之後，它就不會註冊任何東西，而它的數值仍可被繼承：

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

範本永遠不會註冊，而 `jacks_ore` 會帶著範本的材料、音效、工具、分頁、經驗值掉落與抗性註冊，只覆寫硬度。子定義必須寫出自己的 `requires`，這裡清為空清單，因為否則它會繼承父定義的 `requires`，並隨之消失。

## 可以覆寫的內容

*資源包如何運作*

- **模組資產資料夾中的任何東西**，包括材質、模型、方塊狀態、語言檔案、音效、字型、啟動畫面標語、指南書與手冊
- **進度與戰利品表**，在伺服端處理，因此在專用伺服器上也能運作
- **配方**，取代模組的配方，或新增你自己的
- **結構範本**，即模組用於生成建築的 `.nbt` 檔案，位於 `<namespace>/structures/` 下
- **函數**，即位於 `<namespace>/functions/` 下的 `.mcfunction` 檔案
- **登錄表重新命名**，在模組更改方塊或物品名稱時，讓舊世界仍能運作
- **配方移除**，依名稱、命名空間或產出刪除合成配方
- **停用的方塊與物品**，讓任何方塊或物品退出遊戲，見[停用的方塊與物品](#停用的方塊與物品)
- **戰利品注入**，在戰利品表中加入一個池，而不是取代整張表
- **玩家戰利品**，在玩家死亡時擲出戰利品表，可疊加在其所攜帶的物品之上，也可取而代之
- **方塊掉落物**，在玩家破壞任何方塊時，為其增加或取代掉落的東西
- **既有方塊、物品與藥水的屬性**，包括硬度、光照、堆疊數量、任何東西上的食物，以及藥水的效果，見[屬性覆寫](#屬性覆寫)
- **礦物辭典名稱、熔爐配方、燃料燃燒時間、創造模式分頁與音效事件**

這些資料夾在設定中各有一個開關，因此模組包可以不動資源包就關閉其中一項：

| 選項               | 類別       | 預設 | 作用                                                                                                          |
| ------------------ | ---------- | ---- | ------------------------------------------------------------------------------------------------------------- |
| `load`             | `content`  | 開   | 註冊 `blocks/*.json` 與 `items/*.json` 中的方塊和物品。關閉後，包含它們的世界會出現缺失的方塊，且需要重新啟動 |
| `oreDictionary`    | `content`  | 開   | 套用 `oredict/*.json`                                                                                         |
| `removals`         | `recipes`  | 開   | 套用 `recipe_removals/*.json`                                                                                 |
| `disableOverrides` | `recipes`  | 關   | 阻止本模組取代或新增配方。只有在另一個 coremod 與配方載入發生衝突時才需要                                     |
| `lootInjections`   | `data`     | 開   | 套用 `loot_injections/*.json`                                                                                 |
| `enchanting`       | `data`     | 開   | 套用 `enchanting/*.json`                                                                                      |
| `affixes`          | `data`     | 開   | 套用 `affixes/*.json` 和 `gems/*.json`                                                                        |
| `blockDrops`       | `data`     | 開   | 套用 `block_drops/*.json`                                                                                     |
| `registryRemaps`   | `data`     | 開   | 套用 `registry_remap` 檔案，讓重新命名之前儲存的世界保有其方塊與物品                                          |
| `load`             | `worldgen` | 開   | 在新區塊中依 `worldgen/*.json` 生成礦脈。既有的區塊不會改變                                                   |

RDPL 適合用來取代一兩個配方，而你自己內容的配方則應該加在與它搭配的資源包中。若要在整個模組包中完整控制配方，CraftTweaker 與 GroovyScript 是更好的選擇，而這裡的檔案仍會完全取代原本的配方，所以如果要更改某一項原料或移除某一個戰利品條目，請使用它們。

## 伺服端資源包

*資源包如何運作*

資源包可以只存在於伺服器上，讓玩家使用純原版用戶端，但有一項限制：**資源包中不得註冊任何東西**。兩個模組 id 都接受任何遠端；由資源包自行決定。原版用戶端使用它隨附的登錄表來遊玩，因此對登錄表有所新增的資源包必須兩端都安裝。

| 只裝在伺服器就夠了                                                                                                                              | 用戶端也需要資源包                                                                                                     |
| ----------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| `worldgen`、`worldtemplates`、`gamerules`、`structures`、`caveregions`                                                                          | `blocks`、`items`、`fluids`、`materials`                                                                               |
| `villages`、`pathintersects`、`structuremaps`、`citymaps`                                                                                       | `potions`、`potion_types`、`enchantments`、`sounds`、`tabs`、`tooltips` （物品和附魔的提示文字行，詞綴和寶石槽的名稱） |
| `recipes`、`recipe_removals`、`furnace`、`fuels`、`brewing`、`oredict`、`disabled`                                                              | `biomes`、`dimensions`、`portalframes`                                                                                 |
| `loot_tables`、`loot_injections`、`block_drops`、`anvils`、`affixes`、`gems`、`enchanting`、`player_loot`、`advancements`、`functions`、`veins` | `villagers`                                                                                                            |
| `gates`、`cards`、`registry_remap`、`exposures`、`hardness`、`overrides`、`trades`（限用戶端認得的職業：原版的，或兩端都有的模組的）            | `entities`、`worldintro`、`texts`（介紹永遠不會顯示給原版用戶端）                                                      |
| `teams`、`scoring`、`ranks`                                                                                                                     | `models`、`blockstates`、`textures`、`lang`（用戶端資料夾，沒有用戶端時請省略）                                        |
| 整個控制層、設定與預先生成                                                                                                                      |                                                                                                                        |

右側欄是硬性限制：原版用戶端被送往未知的維度時會斷線，而未知的方塊也無法描述給它。左側欄之所以可行，是因為其中的一切不是完全在伺服端執行，就是透過原版已經支援的封包傳到用戶端（由伺服器填入的合成結果欄位、一般的進度封包、狀態訊息形式的閘門拒絕提示，以及由原版遊戲模式、標題與傳送封包組成的預先生成停留）。

這些內容保留在伺服端，只由模組自己的用戶端繪製：Boss 血條的 `background`、心形檔案中的 `heartColors`、鐵砧檔案中的 `tooExpensive`、附魔檔案中的 `enchantCosts` 和 `lapisCosts`，以及詞綴和寶石槽的名稱。開啟 `vanillaClients` 後，資源包設定的每一項都會在日誌中列出名稱，該檔案的其餘部分仍然生效。

`worldtemplates` 屬於伺服端，但有一個例外：**`rubicWorld` 不能與 `vanillaClients` 並用**。Rubic 世界由立方體構成，沒有安裝本模組的用戶端無法收到這些立方體，因此會在登入時被拒絕，或什麼都看不到。兩者同時設定時，新世界會建成一般世界而不是 rubic 世界，日誌會說明原因，免得伺服器把每位玩家都拒於門外。對於一個*已經*建成 rubic 世界的世界開啟 `vanillaClients`，是唯一會讓遊戲直接停止的情況：把這樣的存檔當成一般世界載入會毀掉它，因此它會保持原狀，交由你決定。

設定步驟：

1. 在設定中啟用 `vanillaClients`（`content` 類別，需要重新啟動）。它會強制執行右側欄：這些資料夾在載入時會被跳過，每個被跳過的檔案都會在日誌中列出，因此誤放的方塊檔案只會變成一行日誌，而不會造成連線被拒。
2. 即使如此，也請讓定義遠離右側的資料夾；被跳過的檔案只是多餘的負擔。當資源包引用物品時（閘門的 `hold`、`killedDrops`、配方產出、交易），請只使用原版或伺服器上其他兩端皆有的模組所提供的物品。
3. 不要使用實體變體。每一個實體變體都會註冊成獨立的實體，而原版用戶端無法生成它，因此 `vanillaClients` 會像跳過方塊一樣跳過它們，並在日誌中列出名稱；陣營的 `standIn`，或指名其中之一的生成，就因此沒有東西可做。
4. 照常安裝在伺服器上。玩家的電腦上不需要放任何東西；`/rdpl` 對他們並不存在，所以他們改用 `/rdplserver` 的形式：`/rdplserver team join`、`/rdplserver round start`、`round reset`、`round vote yes`。`opens.leaderSays` 與 `reset.voteSays` 預設指名的是 `/rdpl`，因此在提供給原版用戶端的資源包中，請改用 `/rdplserver` 來撰寫。
5. 以同一版本的乾淨原版用戶端加入一次來測試。失敗會非常明顯：連線在門口就被拒絕，而不是之後悄悄出錯。
6. 可接受的缺口：
   - 伺服器新增的配方可以合成，但不會出現在配方書中。
   - 不會顯示世界介紹。原版用戶端也不會被等待：它會立刻受到歡迎，與其他所有人一起從預先生成停留中被釋放，而且絕不會拖住 `/rdpl round start`。
   - 本模組以卡片顯示的任何內容，例如結果、領先通知或 `saysCard` 的行，都會以聊天列的形式送達，而大廳這類畫面中央的提示則以標題形式送達。
   - 村民的對話以聊天列的形式送達，每則回覆都是一行帶編號的文字，點擊即執行 `/rdplserver talk <number>`。
   - 硬度群組設定的是伺服器破壞方塊所需的時間，但用戶端的裂痕動畫仍以該方塊平常的速度進行。對用戶端也會讀取的數值進行覆寫，例如堆疊數量、耐久度、硬度或光照，在用戶端仍會顯示舊值。
   - 硬度群組的 `adventure` 挖掘無法運作：除非手持物品在自己的 `CanDestroy` 標籤中指明該方塊，否則處於冒險模式的原版用戶端永遠不會開始挖掘。
   - 被停用的方塊或物品仍會顯示在原版用戶端的創造模式分頁中，因為該分頁由用戶端自行建立；停用的其他一切都能從伺服器端運作。

## 登錄表重新命名

*資源包如何運作*

`<namespace>/registry_remap/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

當模組為其方塊或物品改名時，在改名之前儲存的世界會失去它們。在此放入一個檔案，即可將舊名稱對應到新名稱：

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

| 鍵         | 必填 | 值         | 預設 | 作用                                                                              |
| ---------- | ---- | ---------- | ---- | --------------------------------------------------------------------------------- |
| `registry` | 是   | 登錄表名稱 |      | 這些項目所屬的登錄表，通常是 `minecraft:items` 或 `minecraft:blocks`              |
| `mapping`  | 是   | 物件       |      | 舊名稱對應到新名稱，每個被改名的項目寫一組 `"oldmod:old_name": "newmod:new_name"` |

重新命名可以串接，因此先將 A 對應到 B，之後再將 B 對應到 C，就會讓 A 直接變成 C。

## 模組 API

*資源包如何運作*

模組可以在自己的 jar 內附帶 RDPL 內容，因此不需要另外的資源包。在 jar 的根目錄放一個名為 `rdploader` 的資料夾，並完全照資源包的方式配置它：

```
thatmod.jar
  mcmod.info
  rdploader/assets/thatmod/blocks/ruby_ore.json
```

模組附帶的是預設值，而不是覆寫。它的載入順序低於資源包資料夾中的每個資源包，因此資源包作者所寫的任何內容都會勝過它，而且模組只能在它自己的 `mcmod.info` 中宣告的命名空間下提供檔案，該檔案可以是單純的模組清單，也可以是帶有 `modList` 清單的物件，其中每個項目都帶有一個 `modid`。其他任何命名空間下的檔案，以及命名空間內巢狀的 `rdploader` 資料夾，都會被忽略並產生警告，因此模組無法悄悄重新定義另一個模組或資源包作者的內容。

每個附帶此資料夾的模組，在第一次被看到時，都會在 `rdploader/config/mods.json` 中獲得一個項目：

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| 欄位       | 值                | 預設   | 作用                                                                                                           |
| ---------- | ----------------- | ------ | -------------------------------------------------------------------------------------------------------------- |
| `enabled`  | `true` 或 `false` | `true` | 關閉該模組的內容，就像 `.disabled` 會關閉資源包一樣                                                            |
| `priority` | `-1` 或一個數字   | `-1`   | `-1` 讓該模組位於所有資源包之下；任何其他數字則讓它進入一般的[優先順序](#整理資源包)排序，與有編號的資源包並列 |

無論 `overrideResourcePacks` 設定為何，模組資源包都不會進入資源包覆寫層級，因為只有資源包作者才能以 `O` 字母提出這項要求。日誌會標出模組資源包，並由低到高列出各資源包，因此不會有任何東西在看不見的情況下載入。

## 為 1.20.1、1.21.1 和 26.x 編寫的資源包

*資源包如何運作*

為本模組的 1.20.1、1.21.1 或 26.x 版本線製作的資源包，在這裡也能載入。載入器會依據以下特徵辨認：`pack.mcmeta` 的格式高於 3、`assets/` 旁有 `data/` 資料夾，或是有 `.json` 語言檔案與 `textures/block/` 而在 1.12.2 中沒有對應物；無論它是為哪一條版本線編寫的，都以同樣的方式轉換回來。zip 只會在自身內部轉換一次：每個 1.12.2 讀取方式不同的檔案，都會寫入 zip 的 `versions/1.12.2/` 資料夾，而根目錄的現代檔案維持原樣，因此同一個 zip 仍能在 1.20.1、1.21.1 與 26.x 上載入，如[一個 zip 適用於所有版本](#整理資源包)所述。轉換會用一個存有 RDPL 版本的 `port.stamp` 檔案標記它所寫入的資料夾。已經有 `versions/1.12.2/` 資料夾的 zip 會透過它來讀取；當它的 `port.stamp` 指名的是另一個 RDPL 版本時，轉換會重新寫入該資料夾，取代其中的每個檔案並在日誌中列出每一個，而沒有 `port.stamp` 的資料夾，例如資源包作者自己撰寫的，則永遠不會再被轉換。根目錄的檔案中，只有轉換原封不動帶過去的那些仍會被讀取，例如音效，以及 `textures/block/` 與 `textures/item/` 之外的材質。zip 會先寫入暫存檔，等完整後才取代原檔。`rdploader/assets` 與 `rdploader/data` 下的散裝檔案不會被改寫；每次掃描資料夾時，它們都會經過同樣的轉換來讀取。

現代方塊檔案會轉換回來，每個變體都帶有一個 `meta`，順序依變體的撰寫順序，而它的標籤則變成礦物辭典名稱：

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

| 現代檔案                                                                      | 1.12.2 檔案                                                                |
| ----------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| 每個定義資料夾的 `data/<ns>/<folder>/`                                        | `assets/<ns>/<folder>/`                                                    |
| `recipe/`、`loot_table/`、`advancement/`、`function/`、`structure/`（1.21.1） | `recipes/`、`loot_tables/`、`advancements/`、`functions/`、`structures/`   |
| `data/*/tags/items/`（1.21.1：`tags/item/`）                                  | `assets/<ns>/oredict/converted_tags.json`                                  |
| `data/minecraft/tags/functions/tick.json`                                     | `assets/<ns>/gamerules/converted_tick.json`，即主世界的 `gameLoopFunction` |
| `minecraft:smelting` 配方                                                     | `assets/<ns>/furnace/converted_smelting.json`                              |
| `assets/<ns>/lang/<lang>.json`                                                | `assets/<ns>/lang/<lang>.lang`                                             |
| `assets/<ns>/langgates/<name>/<lang>.json`                                    | `assets/<ns>/langgates/<name>/<lang>.lang`，`gate.json` 維持原樣           |
| `textures/block/`、`textures/item/`                                           | `textures/blocks/`、`textures/items/`                                      |
| `models/item/<variant>.json`                                                  | `models/item/<file>/<variant>.json`                                        |
| 沒有方塊狀態（現代版本線會自動產生）                                          | 每個方塊檔案各有一個自動產生的 Forge 方塊狀態，附帶其類型所需的模型        |

- 每個現代原版 id 都會透過 jar 中附帶的扁平化表轉換回來，該表由遊戲自己的資料修復器建立，因此它會變成帶有中繼資料的 1.12.2 方塊或物品：`minecraft:red_wool` 變成 `minecraft:wool:14`，帶有 `axis=x` 的 `minecraft:oak_log` 變成 `minecraft:log:4`。當某個鍵把方塊和中繼資料分開存放時，例如世界生成中的 `block` 與 `modelBlock`，中繼資料會放入 `meta` 或 `modelMeta`；`soil` 則保留整個方塊。資源包自己的 id 透過它自己的檔案來解析：`mypack:worm` 變成 `mypack:test_ore:1`。實體、生態域、戰利品表、音效、粒子與屬性的名稱以同樣的方式轉換回來，而具名的維度會變成一個數字：資源包自己的維度取其 `id`，沒有 `id` 時則取從 1000 起的一個穩定數字，日誌會指明。
- 標籤透過慣例對應的逆向變成礦物辭典名稱：`forge:gems/testium` 與 `c:gems/testium` 變成 `gemTestium`，`minecraft:logs` 變成 `logWood`。配方原料的 `tag` 變成 `forge:ore_dict` 原料，配方則變成 `forge:ore_shaped` 或 `forge:ore_shapeless`；燃料的 `tag` 變成 `oreDict`。每個配方物品都會得到一個 `data`，因為 1.12.2 會拒絕沒有它的帶子類型物品。
- 語言檔案中的 `block.mypack.worm` 變成 `tile.mypack:test_ore.worm.name`，`item.` 在 `item.` 之下以同樣方式處理，`itemGroup.mypack.tab` 變成 `itemGroup.tab`，`fluid.mypack.x` 則同時變成兩個 1.12.2 流體鍵。
- 戰利品表會失去 1.12.2 無法讀取的部分：物品的變體變成 `set_data`，數值提供器變成 `min` 與 `max`，池會得到一個 `name`，`alternatives` 與 `group` 項目會被展平，而 1.12.2 沒有的函數、條件或項目類型會被略去，並在日誌中留下一行。進度的 `items` 變成 `item` 與 `data`，`tag` 則變成 `forge:ore_dict`。
- 函數會被逐行改寫成 1.12.2 的語法：`execute as ... at @s run` 變成 `execute <entity> ~ ~ ~`，`execute if block` 變成 `detect`，`tag` 與 `team` 變成 `scoreboard players tag` 與 `scoreboard teams`，`data merge` 變成 `blockdata` 與 `entitydata`，選擇器則把 `distance`、`scores`、`limit` 與 `gamemode` 換成 `r`、`score_X_min`、`c` 與 `m`。生怪磚的 `SpawnData` 會失去它的 `entity` 包裝。轉換無法帶過去的行，例如 `bossbar` 或巨集行，會變成註解，日誌會指出檔案、行號以及原因，因此函數仍能載入。
- 資料版本高於 1343 的結構 `.nbt`，其調色盤、生怪磚、物品堆疊與實體 id 都會轉換回來；沒有 1.12.2 對應物的方塊會保持原樣，並放置空氣。
- 現代平坦世界的地面位於世界底部，而 1.12.2 的平坦世界從 y 0 開始鋪設，所以轉換會把高度上移 64（當範本寫明 `worldMinHeight` 時，則依它上移）：平坦範本的 `worldSpawn` 與 `resetSendsTo`、隊伍的出生高度、平坦維度的 `groundLevel`，以及當資源包的主世界是平坦世界時函數中的每一個絕對 y。
- 被略去的內容，每一項都會在日誌中留下一行：原版的資料驅動世界生成、維度類型、傷害類型、附魔，以及 1.12.2 沒有的其他登錄表；方塊、實體與流體標籤；除 `tick` 之外的函數標籤；切石機、鍛造與 1.12.2 沒有的其他配方類型；物品元件；只有現代世界才會生成的結構鍵；除 `till`、`path`、`bush` 與 `animals` 之外的 `behavesAs` 名稱；`jobSite`，而沒有 `careers` 的職業會得到一個以其檔案命名的 career。

日誌會為每個轉換的資源包寫一行摘要，並為每個被移動、轉換、略去或無法帶過去的檔案各寫一行，而 1.12.2 不讀取的每個鍵，仍會由遇到它的解析器指名。這次轉換只是盡力而為，並不是完成的資源包：請閱讀這些行，並手動完成它們所指出的事項，首先處理那些找不到材質的自動產生方塊狀態。請在資源包根目錄或單獨的 zip 中進行這些修正，不要在轉換所寫入的 `versions/1.12.2/` 資料夾中修正：另一個版本的 RDPL 會重新寫入該資料夾。

---

# 方塊與物品

## 方塊

*方塊與物品*

`<namespace>/blocks/*.json`

檔案的路徑就是方塊的登錄名稱，因此 `mypack/blocks/ruby_ore.json` 會註冊 `mypack:ruby_ore`。`variants` 內的鍵是為該方塊的中繼資料值命名；它們本身並不是方塊。

所有的鍵一次全部列出。實際的檔案只需寫出需要的那些。標示為適用於某一類型的鍵，只有該類型才會讀取。

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
  "growth": { "stages": 8 },
  "sapling": { "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves" },
  "portal": { "dimension": 12 },
  "needsOxygen": true,
  "container": { "rows": 3, "columns": 9 },
  "bell": { "swing": true, "sound": "minecraft:block.note.bell", "resonateSound": "minecraft:block.note.chime" },
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

### 類型

*方塊*

| 類型         | 你會得到什麼                                                                                                                                       |
| ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| `basic`      | 一般的方塊。`type` 缺少時即使用此類型                                                                                                              |
| `ore`        | 掉落自身以外的東西，並支援幸運與絲綢之觸                                                                                                           |
| `falling`    | 像沙子或礫石一樣掉落                                                                                                                               |
| `slab`       | 下半、上半與雙層，兩個可在手中合併                                                                                                                 |
| `stairs`     | 自動處理轉角與斜坡                                                                                                                                 |
| `fence`      | 與鄰近方塊相連，也會與其他模組的柵欄相連                                                                                                           |
| `pane`       | 像玻璃片一樣相連                                                                                                                                   |
| `wall`       | 像鵝卵石牆一樣相連，並帶有牆柱形狀                                                                                                                 |
| `door`       | 兩格高，可用手開啟，也能對紅石作出反應。使用一個變體，因為其餘的中繼資料負責存放鉸鏈、朝向以及是否開啟                                             |
| `trapdoor`   | 位於方塊上方或下方的鉸接活板，可用手或紅石開啟。一個變體，中繼資料存放朝向、上下半部以及是否開啟                                                   |
| `fence_gate` | 柵欄線上的一道門，可用手或紅石開啟，遇到牆時會降低。一個變體                                                                                       |
| `banner`     | 立在柱子上或靠牆的旗幟，有十六種站立旋轉角度，可帶有你自己的圖案。會另外註冊一個名為 `<name>_wall` 的方塊，作為懸掛的那一種                        |
| `ladder`     | 可攀爬，靠牆放置                                                                                                                                   |
| `torch`      | 可放在牆上與地面，並帶有粒子                                                                                                                       |
| `portway`    | 會把自己加入使用者的站點清單，並開啟一個選單，可前往其中任一站點，包括其他維度。方塊實體只儲存名稱                                                 |
| `bell`       | 一座像 1.14 起村莊所擁有的鐘：從側面使用、以紅石或被拋射物擊中時會鳴響，會在框架中擺盪，並使附近的突襲者發光。一個變體，中繼資料存放朝向與懸掛方式 |
| `log`        | 會旋轉到你放置時所靠的那一面，並在礦物辭典中註冊為 `logWood`，因此伐木與 Blast Plaster 會將其視為樹幹                                              |
| `leaves`     | 會腐朽、可用剪刀剪取、可染色並掉落樹苗，且在礦物辭典中註冊為 `treeLeaves`                                                                          |
| `sapling`    | 長成一棵樹，或長成你自己的某個結構                                                                                                                 |
| `crop`       | 經過多個階段生長，掉落種子與收成物品                                                                                                               |
| `flower`     | 站在土壤上的單格植物                                                                                                                               |
| `cane`       | 像甘蔗或仙人掌一樣向上成柱生長                                                                                                                     |
| `vine`       | 攀附並垂掛在方塊的側面                                                                                                                             |
| `portal`     | 將走進去的任何東西送往另一個維度                                                                                                                   |
| `container`  | 存放玩家可打開的物品欄，大小不限，且第一次被打開時可從戰利品表自行填充。可繪製成一般方塊或箱子，依資源包的要求而定                                 |

### 檔案鍵

*方塊*

| 鍵                           | 必填                 | 值                                                | 預設                                                                                             | 作用                                                                                                                                                                                                                                                                            |
| ---------------------------- | -------------------- | ------------------------------------------------- | ------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `variants`                   | 是                   | 由變體名稱對應到變體的物件                        |                                                                                                  | 每個中繼資料值一個項目。鍵在方塊狀態、模型路徑與語言鍵中為該值命名。登錄名稱則來自檔案自己的路徑                                                                                                                                                                                |
| `type`                       | 否                   | 上述其中一種類型                                  | `basic`                                                                                          | 方塊採用哪種形態                                                                                                                                                                                                                                                                |
| `material`                   | 否                   | [方塊材料](#值清單)之一                           | `rock`                                                                                           | 挖掘行為、活塞、火與液體                                                                                                                                                                                                                                                        |
| `soundType`                  | 否                   | [音效類型](#值清單)之一                           | `stone`；`log` 為 `wood`，`leaves` 與 `crop` 為 `plant`，`stairs` 與 `wall` 則取 `modelBlock` 的 | 腳步聲、破壞與放置                                                                                                                                                                                                                                                              |
| `mapColor`                   | 否                   | [地圖顏色](#值清單)之一                           | 取自材料                                                                                         | 在地圖上的外觀                                                                                                                                                                                                                                                                  |
| `harvestTool`                | 否                   | `pickaxe`、`axe`、`shovel`                        | `pickaxe`                                                                                        | 以哪種工具採集                                                                                                                                                                                                                                                                  |
| `harvestToolLevel`           | 否                   | 0 到 3                                            | `0`                                                                                              | 0 木、1 石、2 鐵、3 鑽石                                                                                                                                                                                                                                                        |
| `silkHarvest`                | 否                   | 布林值                                            | `true`                                                                                           | 絲綢之觸是否會取回方塊本身                                                                                                                                                                                                                                                      |
| `opensWith`                  | 否                   | 物品 id                                           | 無                                                                                               | 讓方塊成為上鎖箱：破壞它會掉落方塊本身，以指定物品右鍵點擊則會消耗一個、播放方塊的破壞音效、發出該變體的 `drops` 清單並移除方塊。任何其他點擊都會顯示來自語言檔案的動作列文字 `tile.<pack>:<block>.<variant>.locked`                                                            |
| `openSound`                  | 否                   | 音效名稱                                          | 破壞音效                                                                                         | 上鎖箱開啟時播放的聲音，取代其破壞音效                                                                                                                                                                                                                                          |
| `expDrop`                    | 否                   | 帶有 `min` 與 `max` 的物件                        | 無                                                                                               | 不使用絲綢之觸破壞時掉落的經驗值                                                                                                                                                                                                                                                |
| `creativeTab`                | 否                   | 分頁名稱                                          | 無                                                                                               | 它出現的分頁                                                                                                                                                                                                                                                                    |
| `renderLayer`                | 否                   | `solid`、`cutout`、`cutout_mipped`、`translucent` | 依類型而定                                                                                       | 繪製方式。預設值：`pane` 以及 `leaves` 為 `cutout_mipped`，`sapling`、`flower`、`cane`、`vine`、`ladder`、`torch`、`door`、`trapdoor`、`bell` 為 `cutout`，`portal` 為 `translucent`，其餘為 `solid`（`opaque` 為 false 時為 `cutout`）。自行提供的模型同樣適用；你設定的值優先 |
| `opaque`                     | 否                   | 布林值                                            | `true`                                                                                           | 是否完全阻擋視線與光線；`sapling`、`flower`、`crop`、`cane`、`vine` 預設為 `false`                                                                                                                                                                                              |
| `fullCube`                   | 否                   | 布林值                                            | 與 `opaque` 相同                                                                                 | 是否填滿它的整個空間                                                                                                                                                                                                                                                            |
| `lightOpacity`               | 否                   | 0 到 255                                          | 不透明時為 `255`，否則為 `0`                                                                     | 吸收多少光線                                                                                                                                                                                                                                                                    |
| `slipperiness`               | 否                   | 小數                                              | `0.6`                                                                                            | 冰是 `0.98`                                                                                                                                                                                                                                                                     |
| `flammability`               | 否                   | 整數                                              | `0`                                                                                              | 火焰燒毀它的容易程度                                                                                                                                                                                                                                                            |
| `fireSpread`                 | 否                   | 整數                                              | `0`                                                                                              | 火焰從它蔓延的容易程度                                                                                                                                                                                                                                                          |
| `explosionResistanceDivisor` | 否                   | 小數                                              | `1.0`                                                                                            | 用來除每個變體對爆炸的 `resistance`                                                                                                                                                                                                                                             |
| `modelBlock`                 | 否                   | 方塊名稱                                          | `minecraft:stone`                                                                                | 當你的方塊沒有模型時，借用其模型的方塊                                                                                                                                                                                                                                          |
| `modelMeta`                  | 否                   | 整數                                              | `0`                                                                                              | 該模型的哪一個變體                                                                                                                                                                                                                                                              |
| `itemModel`                  | 否                   | `state`、`item`                                   | `state`                                                                                          | `state` 跟隨方塊狀態，`item` 則尋找自己的檔案                                                                                                                                                                                                                                   |
| `tint`                       | 否                   | `biome`、`none` 或十六進位顏色                    | 無                                                                                               | 模型中需要有 `tintindex` 才會顯示                                                                                                                                                                                                                                               |
| `plantTypes`                 | 否                   | [植物類型](#值清單)清單                           | 無                                                                                               | 可以種植在它上面的東西                                                                                                                                                                                                                                                          |
| `behavesAs`                  | 否                   | `till`、`path`、`bush`、`animals` 的清單          | 無                                                                                               | 要沿用的原版行為                                                                                                                                                                                                                                                                |
| `bounds`                     | 否                   | 六個數字的清單，0 到 1                            | 完整方塊                                                                                         | 碰撞箱，寫成 `[x1, y1, z1, x2, y2, z2]`                                                                                                                                                                                                                                         |
| `requires`                   | 否                   | 模組 id 或資源包命名空間清單                      | 無                                                                                               | 除非全部存在，否則該檔案會被跳過                                                                                                                                                                                                                                                |
| `particle`                   | 僅限火把             | `none`、`flame`、`colored`                        | `flame`                                                                                          | 火把上方的粒子                                                                                                                                                                                                                                                                  |
| `particleColor`              | 僅限火把             | 十六進位顏色                                      | `FFFFFF`                                                                                         | 當 `particle` 為 `colored` 時使用                                                                                                                                                                                                                                               |
| `smoke`                      | 僅限火把             | 布林值                                            | `true`                                                                                           | 是否冒煙                                                                                                                                                                                                                                                                        |
| `leafSapling`                | 僅限樹葉             | 方塊名稱                                          | 無                                                                                               | 它們掉落的樹苗                                                                                                                                                                                                                                                                  |
| `leafSaplingChance`          | 僅限樹葉             | 整數                                              | `5`                                                                                              | 被破壞的樹葉掉落樹苗的百分比機率，0 到 100。越高掉落得越頻繁，每一級幸運再增加 2                                                                                                                                                                                                |
| `seed`                       | 僅限作物             | 物品名稱                                          | `minecraft:wheat_seeds`                                                                          | 種植它的物品，以及未成熟作物掉落的東西                                                                                                                                                                                                                                          |
| `produce`                    | 僅限作物             | 物品名稱                                          | `minecraft:wheat`                                                                                | 收成時得到的東西                                                                                                                                                                                                                                                                |
| `maxAge`                     | 僅限作物             | 整數                                              | `7`                                                                                              | 有多少個生長階段                                                                                                                                                                                                                                                                |
| `growth`                     | 僅限植物             | 物件                                              | 無                                                                                               | 見[生長](#生長)                                                                                                                                                                                                                                                                 |
| `sapling`                    | 僅限樹苗             | 物件                                              | 無                                                                                               | 見[樹苗](#樹苗)                                                                                                                                                                                                                                                                 |
| `needsOxygen`                | 作物、花、樹苗、樹葉 | 布林值                                            | `true`                                                                                           | 使用 GalaxySpace 時，`crop`、`flower`、`sapling` 或 `leaves` 在沒有氧氣的地方是否會枯萎。見[氧氣](#氧氣)                                                                                                                                                                        |
| `portal`                     | 僅限傳送門           | 物件                                              | 無                                                                                               | 見[傳送門與閘門](#傳送門與閘門)                                                                                                                                                                                                                                                 |
| `container`                  | 僅限容器             | 物件                                              | 無                                                                                               | 見[容器](#容器)                                                                                                                                                                                                                                                                 |
| `portway`                    | 僅限傳送通道         | 物件                                              | 無                                                                                               | 見[傳送通道](#傳送通道)                                                                                                                                                                                                                                                         |
| `bell`                       | 僅限鐘               | 物件                                              | 無                                                                                               | 見[鐘](#鐘)                                                                                                                                                                                                                                                                     |

### 變體鍵

*方塊*

| 鍵             | 必填 | 值                                   | 預設     | 作用                                             |
| -------------- | ---- | ------------------------------------ | -------- | ------------------------------------------------ |
| `meta`         | 是   | 0 到 15                              |          | 此變體佔用的中繼資料值                           |
| `hardness`     | 否   | 浮點數                               | `1.0`    | 破壞所需的時間。黑曜石為 `50`，`-1` 表示無法破壞 |
| `resistance`   | 否   | 浮點數                               | `5.0`    | 爆炸抗性                                         |
| `light`        | 否   | 0 到 15                              | `0`      | 發出的光                                         |
| `harvestLevel` | 否   | 0 到 3                               | `0`      | 覆寫此變體的工具等級                             |
| `rarity`       | 否   | `common`、`uncommon`、`rare`、`epic` | `common` | 提示框中的名稱顏色                               |
| `maxSize`      | 否   | 1 到 64                              | `64`     | 堆疊上限                                         |
| `oreDict`      | 否   | 礦物辭典名稱清單                     | 無       | 此變體登錄的礦物辭典名稱                         |
| `drops`        | 否   | 掉落物清單                           | 掉落自身 | 破壞後得到的東西                                 |

**中繼資料是永久的。** 變體所佔用的數字會寫入每個包含它的存檔世界。之後重新編號或重新排序變體，會讓已放置的方塊變成別的東西。請把新變體加在最後，而且絕不重複使用數字。

`basic` 方塊可容納十六個變體；`slab` 八個；`log` 與 `leaves` 四個，因為軸向與腐朽旗標需要各自的位元；單一狀態的類型只有一個。

### 掉落物

*方塊*

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

| 鍵            | 必填   | 值             | 預設                             | 作用                                                                                                                                                                               |
| ------------- | ------ | -------------- | -------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `block`       | 二選一 | 方塊或物品名稱 |                                  | 掉落的內容                                                                                                                                                                         |
| `entity`      | 二選一 | 實體名稱       |                                  | 方塊破壞時放出的實體，用來取代物品                                                                                                                                                 |
| `meta`        | 否     | 整數           | `0`                              | 它的哪個變體                                                                                                                                                                       |
| `amount`      | 否     | 整數或範圍     | `1`                              | 數量                                                                                                                                                                               |
| `chance`      | 否     | 0 到 100       | `100`，`guaranteed` 關閉時為 `0` | 掉落到底會不會發生的機率                                                                                                                                                           |
| `weight`      | 否     | 整數           | `0`                              | 大於零時，此項目加入一個只會掉出一個東西的池。見下文                                                                                                                               |
| `bonusChance` | 否     | 整數清單       | 無                               | 百分比機率清單，每個幸運等級一個，從沒有幸運開始。每個值是該掉落物再出現一次（同樣數量）的機率，與 `chance` 用同一次判定，所以最多只會多出一份。最後一個項目也適用於更高的幸運等級 |
| `guaranteed`  | 否     | 布林值         | `true`                           | `chance` 的舊式簡寫。開啟為 `100`，關閉為 `0`                                                                                                                                      |

每個沒有 `weight` 的項目都是各自獨立判定，所以有三個這類項目的方塊可能三個全掉，也可能一個都不掉。給項目加上 `weight` 後，它們就不再獨立：會組成一個池，方塊每次破壞時恰好從中選出一個，機率與權重成正比。上例中，鑽石與綠寶石以一比四共用一個池，因此兩者之一必定掉出，其中綠寶石佔五分之四；紅寶石與煤炭則各自單獨判定，蠹魚又是另一回事。物品與實體分開入池，所以有權重的物品和有權重的實體不會互相競爭。

指定 `entity` 的項目會在方塊原本的位置放出一隻實體，朝向隨機；生物會依當地難度套用一般的生成處理，因此出現時會帶著它原本該有的裝備與效果。`amount` 決定數量，`chance` 決定頻率，`weight` 則讓它進入實體池。它在方塊破壞時發生，不論是怎麼破壞的，所以爆炸或活塞放出它們的效果與鎬子相同。`meta`、`bonusChance` 與幸運對實體沒有意義，會被忽略。

同時指定 `block` 與 `entity` 的掉落項目會使用實體，並在日誌中說明。

### 生長

*方塊*

適用於 `crop`、`flower`、`cane` 與 `vine`。

```json
{
  "growth": {
    "stages": 8,
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

| 鍵                | 必填 | 值                   | 預設    | 作用                                                                                                                                                                                                             |
| ----------------- | ---- | -------------------- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `stages`          | 否   | 整數                 | `16`    | 完成前的生長階段數                                                                                                                                                                                               |
| `spread`          | 否   | 整數                 | `0`     | 僅限 `vine`。`0` 或 `1` 會關閉向側面的蔓延。高於此值時，只有當側面兩格、上下一格範圍內（含它自己）同種藤蔓的數量少於這個數，藤蔓才會向側面蔓延一格，所以數字越大，它長得越密。它永遠不會蔓延到比下一格更遠的地方 |
| `maxHeight`       | 否   | 整數                 | `3`     | 僅限 cane。柱體長到多高                                                                                                                                                                                          |
| `drop`            | 否   | 物品名稱             | 無      | 破壞時掉落的東西                                                                                                                                                                                                 |
| `dropCount`       | 否   | 整數                 | `1`     | 數量                                                                                                                                                                                                             |
| `needsSky`        | 否   | 布林值               | `false` | 只在看得到天空的地方生長                                                                                                                                                                                         |
| `needsWater`      | 否   | 布林值               | `false` | 只在水附近生長                                                                                                                                                                                                   |
| `waterRange`      | 否   | 整數                 | `1`     | 那片水可以離多遠                                                                                                                                                                                                 |
| `damage`          | 否   | 布林值               | `false` | 會傷害碰到它的任何東西                                                                                                                                                                                           |
| `damageAmount`    | 否   | 浮點數，單位為半顆心 | `1.0`   | 傷害多少                                                                                                                                                                                                         |
| `breaksNeighbors` | 否   | 布林值               | `false` | 會破壞放在它旁邊的方塊，像仙人掌一樣                                                                                                                                                                             |

### 蔓延

*方塊*

適用於 `basic` 與 `ore` 方塊。指定了可蔓延到哪些方塊的方塊，行為就像草蔓延到泥土上。

```json
{
  "spreadsOnto": ["minecraft:dirt", "minecraft:dirt:1"]
}
```

| 鍵            | 必填 | 值           | 預設 | 作用                                   |
| ------------- | ---- | ------------ | ---- | -------------------------------------- |
| `spreadsOnto` | 否   | 方塊名稱清單 | 無   | 它蔓延到的方塊。第一個是它變回去的方塊 |

此方塊會接收隨機刻，行為與原版的草相同。上方光照為 9 以上時，它每刻會嘗試四次，位置隨機選在左右各一格內、下方三格到上方一格之間。若該位置是列出的方塊、該位置上方光照為 4 以上，且其上方方塊遮擋的光不超過 2，該位置就會變成此方塊。上方光照低於 4 且上方方塊遮擋的光超過 2 時，它會變回列出的第一個方塊。`minecraft:dirt` 僅指一般泥土；中繼資料寫在第三段，所以 `minecraft:dirt:1` 是粗泥土。

### 樹苗

*方塊*

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

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
    "structure": "mypack:ruby_tree"
  }
}
```

`structure` 會用你的某個範本取代生成的樹，這是建造生成器做不到之物的方法，方塊中其餘部分都不必再寫。改在 `structures` 下列出多個，樹苗每次長大時會從中挑一個，這樣一片樹林就不會一再是同一棵樹：

```json
{
  "sapling": {
    "structures": [
      { "structure": "mypack:ruby_tree", "weight": 3 },
      "mypack:tall_ruby_tree"
    ]
  }
}
```

| 鍵           | 必填 | 值               | 預設               | 作用                                                                                                                                          |
| ------------ | ---- | ---------------- | ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `soil`       | 否   | 方塊名稱清單     | 無                 | 它能生長在什麼之上                                                                                                                            |
| `stages`     | 否   | 整數             | `2`                | 長成樹之前的生長階段數                                                                                                                        |
| `chance`     | 否   | 整數             | `7`                | 每個隨機刻 N 分之一                                                                                                                           |
| `light`      | 否   | 0 到 15          | `9`                | 所需的光照等級                                                                                                                                |
| `log`        | 否   | 方塊名稱         | `minecraft:log`    | 樹幹方塊                                                                                                                                      |
| `leaves`     | 否   | 方塊名稱         | `minecraft:leaves` | 樹葉方塊                                                                                                                                      |
| `height`     | 否   | 整數             | `4`                | 樹幹高度                                                                                                                                      |
| `structure`  | 否   | `namespace:name` | 無                 | 長成此範本，而不是生成的樹                                                                                                                    |
| `structures` | 否   | 清單             | 無                 | 可長成的多個範本，每次生長時挑選其一。每個項目寫成 `{ "structure": "namespace:name", "weight": 3 }`，或只寫名稱，機率均等。會覆寫 `structure` |

### 氧氣

*方塊*

```json
{
  "type": "flower",
  "needsOxygen": false
}
```

啟用 GalaxySpace 的 `enableOxygenForPlantsAndFoods` 後，在沒有氧氣的世界裡，`crop`、`flower` 或 `sapling` 會枯萎成枯死的灌木或消失，`leaves` 則會乾枯。凡是周圍沒有密封房間或氧氣泡維持空氣的地方都會如此，在極熱或極冷的世界裡，沒有溫控的地方也一樣；`"needsOxygen": false` 會讓該方塊不受影響，使它在那裡像在主世界一樣存活。

## 容器

*方塊與物品*

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
    "chestModel": true,
    "guiTexture": "mypack:textures/gui/crate.png",
    "guiWidth": 176,
    "guiHeight": 222,
    "bauble": "body"
  },
  "variants": [ { "name": "crate", "hardness": 2.5 } ]
}
```

| 設定         | 類型         | 預設    | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| ------------ | ------------ | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `rows`       | 整數         | `3`     | 槽位有幾列，1 到 9                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `columns`    | 整數         | `9`     | 每列有幾個槽位，1 到 12                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `lootTable`  | 文字         | 空      | 第一次有任何東西接觸其內容物時，包含玩家開啟、漏斗、比較器或將其破壞，就會對方塊擲出一張戰利品表，與地城箱子的填入方式完全相同。玩家放置的容器不會擲出。留空則一開始是空的                                                                                                                                                                                                                                                                           |
| `chestModel` | 布林值或文字 | `false` | 畫成蓋子可開啟的箱子，而不是用你自己模型畫成的一般方塊。`true` 使用原版箱子的圖樣；材質名稱如 `mypack:blocks/strongbox_chest` 則改用你自己的箱子材質圖，放置的方塊與物品都適用。請將方塊狀態的模型設為 `resourcedatapackloader:pack_chest`，並在 `texture` 下寫相同的名稱，這樣你手中的物品也會是箱子的形狀。使用箱子模型的方塊也會像原版箱子一樣把 `opaque` 預設為 `false`，因此光線不會在方塊處被截斷，箱子也不會被畫得偏暗。                      |
| `guiTexture` | 文字         | 空      | 介面使用你自己的背景圖片。留空則依列數與欄數所需的大小，以原版箱子介面繪製一張                                                                                                                                                                                                                                                                                                                                                                       |
| `guiWidth`   | 整數         | 無      | 該圖片的寬度，使用 `guiTexture` 時必填                                                                                                                                                                                                                                                                                                                                                                                                               |
| `guiHeight`  | 整數         | 無      | 該圖片的高度，使用 `guiTexture` 時必填                                                                                                                                                                                                                                                                                                                                                                                                               |
| `bauble`     | 文字         | 空      | 僅限物品：可佩戴它的 Baubles 槽位，為 `amulet`、`ring`、`belt`、`trinket`、`head`、`body` 或 `charm`。背包通常使用 `body` 或 `charm`。未安裝 Baubles 時會被忽略，物品的其他功能仍正常運作。每個名稱對應 Baubles 分頁中的一個格子，所以要求 `body` 的物品只適合那個格子；`ring` 是兩個戒指格，`trinket` 則適合所有格子。Baubles 是軟相依：它存在時本模組會在其之後載入，不存在時照常執行，因此指定槽位的資源包在從未聽過 Baubles 的伺服器上也很安全。 |

**九列乘十二欄是上限**，這是 Iron Chest 提供的最大尺寸，也是一個畫面所能容納的最多。要求更多的資源包會被截到此上限，並輸出一行說明的錯誤。關於最高的尺寸有一點要注意：九列的畫面高 276 像素，而 1080 顯示器在 GUI 縮放 `auto` 下只有 270，所以上下各被裁掉三像素，縮放設為 3 就能完整顯示。Iron Chest 能放下九列，是因為它自帶更緊湊的圖樣；想要相同效果的資源包可以設定 `guiTexture` 並自行繪製。

**介面是繪製出來的，不是隨附的。** 九欄以內、六列以內的容器直接使用原版箱子介面，看起來與該大小的箱子完全相同。更大的則在繪製時以同一張圖組合而成：頂端邊緣、依需要重複的一列槽位，以及帶有玩家自己物品欄的底部，所以資源包可以要求原版介面沒有涵蓋的大小，而不必自帶圖片。`guiTexture` 會在資源包想要自己外觀時覆寫以上全部，此時 `guiWidth` 與 `guiHeight` 必須指出圖片大小，否則會改用繪製的版本，並輸出一行說明的錯誤。

**方塊的行為。** 它在存檔與重新載入後會保留內容，破壞時會掉出內容，會依裝滿程度回應比較器，也可以像箱子一樣在鐵砧中改名。`chestModel` 還會賦予它箱子的開啟音效與蓋子動畫；關閉時，方塊會依你自己的 `modelBlock` 所指定的模型繪製，因此板條箱、木桶或櫃子都可以。

**為箱子上色。** 箱子材質圖是一般的材質，所以像素圖不必畫任何像素就能替原版的重新上色：`extends` 它並給一個 `tint`，然後把該像素圖的名稱寫在 `chestModel` 以及模型的 `texture` 中。

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

放置的方塊與你手中的物品讀取的是同一個名稱，所以兩者一致。只在其中一處寫上，另一處就仍是原版的棕色。

**容器物品可以佩戴。** 給它 `bauble`，在安裝了 Baubles 的情況下，它會放進該槽位，並且不必脫下就能按鍵開啟：預設是 `V`，可在控制設定的 Resource Data Pack Loader 下重新綁定。`B` 是 Baubles 為自己的分頁所綁定的按鍵，因此兩者不共用按鍵。已開啟一個佩戴中的容器時再按一次，會移到下一個你佩戴的容器並循環，所以同時佩戴多個時都能開啟。此按鍵只在有 Baubles 時才出現，而物品的其他部分，包括右鍵與它的物品欄，不論有沒有 Baubles 都能運作。Baubles 沒有專屬的背包槽位；`body` 與 `charm` 是背包通常使用的兩個。

**戰利品表在第一次使用時填入**，不是在放置方塊時，這正是它在結構中好用的原因：誰先開啟誰就得到那次抽取的結果，而漏斗或比較器先接觸到它時同樣會抽取。同一張表也可用於印記形狀或村莊地塊上的 `lootTable`，因此資源包可以透過世界生成放置這些容器，並以同樣方式補充物品。

**任何物品都可以配戴。** `bauble` 不只是容器的鍵，也是物品的鍵，所以戒指、護身符或掛飾不需要容器。物品會放進該 Baubles 欄位，在欄位中的期間，`wornEffects` 與 `wornAttributes` 會作用於配戴者。

```json
{
  "bauble": "ring",
  "wornEffects": [
    { "potion": "minecraft:speed", "amplifier": 1, "ambient": true }
  ],
  "wornAttributes": [
    { "attribute": "maxHealth", "amount": 10.0, "operation": 0 }
  ],
  "variants": { "ring": { "meta": 0, "maxSize": 1 } }
}
```

`wornEffects` 的每一項接受 `potion`、`amplifier`、`ambient` 與 `showParticles`，其中 `showParticles` 在這裡預設為關閉。它沒有 `duration`：效果會在物品配戴期間持續維持，摘下後 1 到 2 秒內消失。`wornAttributes` 的每一項接受 `attribute`、`amount` 與 `operation`，其中 `0` 為相加，`1` 為乘以基礎值，`2` 為乘以總值，修改會隨物品摘下而移除。沒有 `uuid`：修改依物品命名，所以同時配戴兩件相同的物品時共用同一個修改，不會疊加。兩者每秒讀取一次，不會隨玩家儲存。摘下生命值加成後，配戴者的生命值會降到新的上限。現代版資源包可以加入的 Curios 欄位類型在這裡沒有對應項，因為 Baubles 的欄位是固定的，所以這個版本不會讀取 `curioslots` 資料夾。

## 鐘

*方塊*

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

以及它的方塊狀態，`assets/mypack/blockstates/village_bell.json`：

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

| 設定            | 類型     | 預設                         | 作用                                                                                         |
| --------------- | -------- | ---------------------------- | -------------------------------------------------------------------------------------------- |
| `swing`         | 布林值   | `true`                       | 用自己的模型繪製鐘身，並在鐘響時讓它擺動。`false` 則把整個鐘畫成一個靜止的方塊，沒有任何動畫 |
| `sound`         | 音效名稱 | `minecraft:block.note.bell`  | 鐘響時播放。留空則無聲                                                                       |
| `resonateSound` | 音效名稱 | `minecraft:block.note.chime` | 因附近有突襲者而使鐘共鳴時播放。留空則共鳴時無聲                                             |

**它的懸掛方式與遊戲自 1.14 起的鐘相同。** 放在方塊頂部時立在地面上，朝向你面對的方向；放在方塊下方時從天花板懸掛；靠著牆時掛在那面牆上，若另一側也是實心方塊則掛在兩面牆之間。支撐它的東西消失時它會掉落，而兩牆之間的鐘在其中一面牆消失時會變成單牆鐘。四種情況的碰撞箱都與原版相同，因此不會讀取 `bounds`。

**什麼會敲響它。** 對鐘身的側面使用，位置在橫樑以下：地面鐘是橫樑橫跨的兩個面，牆上的鐘是緊鄰牆的兩個面，天花板鐘則是任何一側。頂部、底部以及鐘身以上的任何位置都沒有作用。紅石訊號在接通時敲響一次，箭、雪球或任何其他拋射物擊中手能碰到的一側時也會敲響。鐘身會朝遠離被擊中那一側的方向擺動兩秒半；紅石則讓它沿鐘所面對的方向擺動。

**敲響之後會發生什麼。** 32 格內的村民聽到後會進入室內十五秒，前往其村莊所知最近的門。32 格內有突襲者時，鐘會在敲響後四分之一秒產生共鳴，兩秒後 48 格內的每個突襲者都會發光三秒，並在鐘旁各自所在的一側出現彩色粒子。突襲者指的是[突襲](#突襲)派出的任何生物，以及遊戲本身的災厄村民與女巫。這種類型的鐘對每場突襲來說都是村莊之鐘：每一波到來時它都會響，不必在該突襲的 `bell` 中指定。

**模型。** 鐘沒有 `blocks` 屬性，所以它的方塊狀態以 `facing` 與 `attachment` 為鍵。開啟 `swing` 時，這些模型只繪製框架，擺動的部分則是另一個名為 `body` 的項目，在其靜止位置的方塊空間中建模；它繞著向內半格、向上四分之三格的那一點傾斜，與原版相同。`inventory` 項目是物品，框架與鐘身合在一起。關閉 `swing` 時沒有 `body` 項目，四個框架模型也會把鐘畫出來。

**擺動由用戶端繪製。** 鐘響會以方塊事件傳達給玩家，因此專用伺服器會讓所有安裝本模組的人看到鐘在擺動，沒有本模組的玩家則只能聽到鐘聲。音效、共鳴與發光都在伺服器上發生。

## 傳送通道

*方塊*

`<namespace>/blocks/*.json`

```json
{
  "type": "portway",
  "material": "rock",
  "soundType": "stone",
  "creativeTab": "decorations",
  "portway": {
    "activateSound": "minecraft:block.beacon.activate",
    "travelSound": "minecraft:block.portal.travel",
    "names": ["北門", "舊磨坊", "蕨谷"],
    "allow": [],
    "deny": [-1],
    "crossDimension": true,
    "gate": "",
    "network": "",
    "global": false,
    "tall": false,
    "facing": false,
    "activateParticle": "minecraft:portal",
    "travelParticle": "minecraft:portal",
    "carry": { "mount": false, "leashed": false, "pets": false, "crossDimension": true },
    "cost": {
      "xp": { "amount": 0, "perBlocks": 500, "dimension": 3, "max": 3, "points": false },
      "item": { "id": "minecraft:emerald", "amount": 1, "perBlocks": 0, "dimension": 0, "max": 0 },
      "health": { "amount": 0, "perBlocks": 0, "dimension": 0, "max": 0 },
      "hunger": { "amount": 0, "perBlocks": 0, "dimension": 0, "max": 0 },
      "cooldown": 30
    },
    "menu": { "order": "nearest", "previous": "minecraft:arrow", "next": "minecraft:arrow", "page": "minecraft:paper", "sort": "minecraft:compass", "locked": "minecraft:barrier" }
  },
  "variants": { "portway": { "meta": 0, "hardness": 5.0, "resistance": 30 } }
}
```

| 設定               | 類型         | 預設                              | 作用                                                                                                                                                                                                                                                                                                                                                                                               |
| ------------------ | ------------ | --------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `activateSound`    | 音效名稱     | `minecraft:block.beacon.activate` | 玩家把它加入清單時，在傳送通道處播放。留空則不播放                                                                                                                                                                                                                                                                                                                                                 |
| `travelSound`      | 音效名稱     | `minecraft:block.portal.travel`   | 玩家傳送時，在兩端播放。留空則不播放                                                                                                                                                                                                                                                                                                                                                               |
| `names`            | 文字清單     | 空                                | 已放置傳送通道的名稱。每個傳送通道依自己的位置從清單中取一個名稱，每次都相同，除非它以別的方式命名。清單只有一項時，所有傳送通道同名                                                                                                                                                                                                                                                               |
| `allow`            | 維度 ID 清單 | 空                                | 此傳送通道僅在這些維度中生效，並只列出這些維度的傳送通道。留空表示允許所有維度                                                                                                                                                                                                                                                                                                                     |
| `deny`             | 維度 ID 清單 | 空                                | 此傳送通道在這些維度中不生效，也不列出這些維度的傳送通道。優先於 `allow`                                                                                                                                                                                                                                                                                                                           |
| `crossDimension`   | 真或假       | `true`                            | 設為 `false` 時，選單只列出同一維度的傳送通道                                                                                                                                                                                                                                                                                                                                                      |
| `gate`             | 閘門名稱     | 空                                | 玩家啟動此傳送通道、開啟它的選單並前往它，需要先開啟的[閘門](#閘門)。閘門未開啟時，站點顯示為已鎖定。為同一網路的所有傳送通道指定同一個閘門，就能把整個網路一起鎖上。留空表示不需要                                                                                                                                                                                                                |
| `network`          | 文字         | 空                                | 此傳送通道所屬的群組。傳送通道只列出並只能前往自己網路中的傳送通道，其餘的選單不會列出。留空表示共用的預設網路                                                                                                                                                                                                                                                                                     |
| `global`           | 真或假       | `false`                           | 設為 `true` 時，每位玩家不必啟動就能在選單中看到此傳送通道。否則玩家只能看到自己啟動過的                                                                                                                                                                                                                                                                                                           |
| `scope`            | 文字         | `player`                          | 決定誰共享站點：`player` 每位玩家各有一份清單，`team` 讓隊伍全員共享任一成員啟動的站點（隊伍指資料包 `<namespace>/teams` 資料夾中的一方；玩家可透過列在該方的 `players` 中、使用 `/rdpl team join <名稱>`（該方將 `joinable` 設為 false 時除外）、單獨使用 `/rdpl team join` 加入 `balance` 中人數最少的一方，或經 `picks` 抽取加入，見 [隊伍](#誰會加入)），`global` 同上。沒有隊伍時使用個人清單 |
| `tall`             | 真或假       | `false`                           | 像門一樣高兩格。見[高度與朝向](#高度與朝向)                                                                                                                                                                                                                                                                                                                                                        |
| `facing`           | 真或假       | `false`                           | 朝向放置它的玩家，抵達的旅行者出現在它朝向的那一側。見[高度與朝向](#高度與朝向)                                                                                                                                                                                                                                                                                                                    |
| `activateParticle` | 粒子名稱     | 空                                | 玩家把它加入清單時，在傳送通道處顯示的粒子，例如 `minecraft:portal`。留空則不顯示                                                                                                                                                                                                                                                                                                                  |
| `travelParticle`   | 粒子名稱     | 空                                | 在玩家出發處與抵達處顯示的粒子。留空則不顯示                                                                                                                                                                                                                                                                                                                                                       |
| `carry`            | 物件         | 無                                | 隨玩家一同前往的東西。見[隨行](#隨行)                                                                                                                                                                                                                                                                                                                                                              |
| `cost`             | 物件         | 免費                              | 一次傳送的費用，見下方表格                                                                                                                                                                                                                                                                                                                                                                         |
| `menu`             | 物件         | 見[選單](#選單)                   | 傳送選單的初始排序方式和按鈕圖示，見下方的選單表格                                                                                                                                                                                                                                                                                                                                                 |

### 費用

```json
{
  "cost": {
    "xp": { "amount": 1, "perBlocks": 250, "dimension": 3, "max": 5 },
    "cooldown": 30
  }
}
```

`cost` 物件為每種費用設一個項目，全部可省略。全部省略則傳送免費。

| 部分       | 扣除內容                                       | 玩家不足時             |
| ---------- | ---------------------------------------------- | ---------------------- |
| `xp`       | 經驗等級，設了 `points` 則為經驗點數           | 拒絕傳送               |
| `item`     | 由 `id` 指定的物品，取自主物品欄               | 拒絕傳送               |
| `health`   | 半顆心，不會讓玩家的生命低於 1                 | 扣除剩餘部分，從不拒絕 |
| `hunger`   | 飽食度點數                                     | 扣除剩餘部分，從不拒絕 |
| `cooldown` | 兩次傳送之間的秒數，直接填數字，依玩家分別計算 | 時間未到之前拒絕傳送   |

| 鍵          | 類型     | 預設    | 作用                                                                              |
| ----------- | -------- | ------- | --------------------------------------------------------------------------------- |
| `amount`    | 整數     | `0`     | 每次傳送的固定費用                                                                |
| `perBlocks` | 整數     | `0`     | 距離每滿（含不足）`perBlocks` 格就加 1。`0` 表示不加。跨維度傳送的距離以 `0` 計算 |
| `dimension` | 整數     | `0`     | 傳送跨越維度時加上                                                                |
| `max`       | 整數     | `0`     | 該部分的最高費用。`0` 表示不設上限                                                |
| `points`    | 真或假   | `false` | 僅用於 `xp`。以經驗點數而不是等級計價                                             |
| `id`        | 物品名稱 | 空      | 僅用於 `item`。用來支付的物品的註冊名稱                                           |

**冷卻。** 冷卻從玩家抵達時開始。它以允許下一次傳送的遊戲時刻儲存在玩家身上，因此重新啟動後依然有效，每個站點的提示框會顯示剩餘秒數。`/rdplserver portway cooldown` 可以設定或清除它。

### 使用傳送通道

**使用。** 對傳送通道按右鍵，會把這個站點（維度與位置）加入該玩家自己的清單。清單儲存在玩家身上。已經記錄過的站點會有提示。接著開啟傳送選單。尚未開啟該傳送通道 `gate` 的玩家，或身處傳送通道所排除維度中的玩家，會收到提示，選單不會開啟。

### 站點名稱

**名稱。** 傳送通道會顯示它擁有的第一項：自己的名稱、`names` 中的名稱，或者它的維度與位置。它在放置前物品曾在鐵砧上改名，或者結構範本或 `/setblock` 給它的方塊實體寫入了字串 `Name` 時，才有自己的名稱。取自 `names` 的名稱從不儲存，所以修改清單會讓已放置的傳送通道改名。沒有改名介面。

**佔位符與同名站點。** `names` 中的名稱可以包含 `{biome}`，它會變成傳送通道所在生態域的名稱，例如 `Dark Forest`。玩家選單中兩個站點同名時，每個名稱後面會附上各自的位置，例如 `北門 (12, 64, -30)`，維度不同時還會附上維度。

### 選單

**選單。** 列出該玩家的站點：自己的、全域的和團隊的站點合併為一個列表，依玩家用排序按鈕選定的順序排列：依距離（目前維度在前，由近到遠，然後其他維度依 ID）、依名稱從 A 到 Z（沒有名稱的站點排在最後，依位置）或依最新發現（在此版本之前發現的站點排在最後）。被 `allow`、`deny` 或 `crossDimension` 排除的站點不會出現，屬於其他 `network` 的站點也不會出現。其 `gate` 或其維度被玩家尚未開啟的[閘門](#閘門)封住的站點，會顯示為標有已鎖定的屏障，選擇它會給出拒絕提示。每個站點的提示框會列出這次傳送對該玩家的費用和剩餘的冷卻秒數。

**分頁與遺忘。** 每頁顯示 45 個站點。最下一列左邊是上一頁箭頭，接著是排序按鈕，中間是頁碼，右邊是下一頁箭頭。點擊排序按鈕會切換到下一種排序；它的提示框顯示目前的排序，以及點擊後會選到的排序。玩家的選擇儲存在玩家身上，重新登入、死亡和切換維度後仍然保留；在他選擇之前，由選單的 `order` 決定。按住 Shift 點擊玩家自己的站點，玩家就會忘掉它。全域站點和玩家目前所在的傳送通道不受影響。站點數量沒有上限，選單會依需要增加頁數。

**搜尋。** 選單右上角的輸入框會隨玩家輸入過濾列表，依站點名稱；沒有名稱的站點則依維度和位置，不區分大小寫。輸入框為空時顯示所有站點。新的搜尋會回到第一頁，並保留所選排序。右鍵點擊輸入框可清空。搜尋內容不會被儲存。

```json
{ "menu": { "order": "name", "previous": "minecraft:spectral_arrow", "next": "minecraft:spectral_arrow", "page": "minecraft:book", "sort": "minecraft:clock", "locked": "mypack:sealed_rune" } }
```

| 鍵       | 類型     | 預設                | 作用                                                      |
| -------- | -------- | ------------------- | --------------------------------------------------------- |
| order    | 文字     | `nearest`           | 玩家選定之前看到的排序方式：`nearest`、`name` 或 `newest` |
| previous | 物品名稱 | `minecraft:arrow`   | 上一頁按鈕的圖示                                          |
| next     | 物品名稱 | `minecraft:arrow`   | 下一頁按鈕的圖示                                          |
| page     | 物品名稱 | `minecraft:paper`   | 頁碼的圖示                                                |
| sort     | 物品名稱 | `minecraft:compass` | 排序按鈕的圖示                                            |
| locked   | 物品名稱 | `minecraft:barrier` | 玩家尚未開啟的站點的圖示                                  |

任何物品都可以，包括包內自帶的物品。不是物品的 ID 只會記錄一次日誌，並顯示預設值。選單會從開啟它的傳送通道或移動物品取得這些值。

**傳送。** 選擇一個站點後，玩家會被傳送到目標傳送通道旁一處兩格高的空位：如果設了 `facing`，先試它朝向的那一側，再試其他側面，然後是頂部（`tall` 時在兩格以上），最後取最近的空位。騎乘者會下坐騎，玩家抵達時沒有累積的墜落距離與動量。出發與抵達兩處都會播放傳送音效並顯示 `travelParticle`。費用在傳送前檢查，玩家抵達後才會扣除。創造模式與旁觀模式的玩家不付費，也不必等待。如果目標方塊已不存在、不再是傳送通道，或其所在維度已不存在，該站點會從清單中移除，並告知玩家。

**在結構中。** 傳送通道的方塊實體只儲存名稱，因此儲存在結構 `.nbt` 範本中的傳送通道（結構地圖、村莊部件、已放置的結構）與玩家放置的傳送通道運作方式相同。全域傳送通道會在載入或被啟動時加入共用清單，被破壞時移出。

### 隨行

```json
{ "carry": { "mount": true, "leashed": true, "pets": true, "crossDimension": false } }
```

| 鍵               | 類型   | 預設    | 作用                                              |
| ---------------- | ------ | ------- | ------------------------------------------------- |
| `mount`          | 真或假 | `false` | 玩家操控的坐騎一同前往，玩家會重新騎上它          |
| `leashed`        | 真或假 | `false` | 10 格內被拴在玩家身上的生物一同前往，並重新被拴住 |
| `pets`           | 真或假 | `false` | 玩家馴服的、沒有坐下也沒有被拴住的寵物一同前往    |
| `crossDimension` | 真或假 | `true`  | 設為 `false` 時，跨維度不帶任何東西               |

每次傳送都會讓玩家下坐騎，騎在玩家身上的其他東西則會留下。

### 高度與朝向

```json
{
  "type": "portway",
  "portway": { "tall": true, "facing": true },
  "variants": { "portway": { "meta": 0 } }
}
```

`tall` 會讓傳送通道像門一樣高兩格。放置時上方需要有空間，上下兩半會一起放下，破壞任意一半都會同時移除兩半，並只掉落一個傳送通道。站點是下半部分，使用上半部分與使用下半部分相同。`facing` 會讓它朝向放置它的玩家，隨結構範本旋轉與鏡像，抵達的旅行者出現在它朝向的那一側，並面朝同一個方向。

方塊把朝向和上下半部分存在中繼資料裡，所以只要設定了其中任何一個，它就只用一個變體，`variants` 中的其他變體會被忽略。它的方塊狀態照常由 `blocks` 指定，有朝向時再加上 `facing`（`north`、`east`、`south`、`west`），是高方塊時再加上 `half`（`lower`、`upper`）。結構範本會像儲存門一樣儲存高傳送通道的兩半。如果資源包為高傳送通道自帶方塊狀態，就還要在其中為物品加上變體 `inventory`，因為物品使用的正是這個變體，而 RDPL 不會從資源包的方塊狀態推導物品模型。

### 傳送物品

*items*

`<namespace>/items/*.json`

```json
{
  "type": "portway",
  "creativeTab": "tools",
  "portway": {
    "network": "",
    "allow": [],
    "deny": [],
    "crossDimension": true,
    "cost": { "cooldown": 60 },
    "carry": { "pets": true },
    "travelSound": "minecraft:block.portal.travel",
    "travelParticle": "minecraft:portal",
    "consume": true,
    "bind": true
  }
}
```

| 鍵        | 類型   | 預設    | 作用                                       |
| --------- | ------ | ------- | ------------------------------------------ |
| `consume` | 真或假 | `false` | 玩家抵達後消耗一個該物品。創造模式不會消耗 |
| `bind`    | 真或假 | `false` | 物品綁定到某一個傳送通道，而不是開啟選單   |

`portway` 物件也接受傳送通道方塊中適用於一次傳送的鍵：`network`、`crossDimension`、`allow`、`deny`、`gate`、`cost`、`menu`、`carry`、`travelSound` 和 `travelParticle`。沒有 `bind` 時，使用該物品會開啟列出其網路所有站點的選單。由於玩家不在任何傳送通道旁，不會有「此處」的標記，距離從玩家算起。物品離開那隻手時選單會關閉。有 `bind` 時，潛行使用該物品點擊其網路中玩家已知的傳送通道，或點擊玩家已開啟其 `gate` 的全域傳送通道，就會綁定，提示框會顯示它的名稱。之後在空中使用該物品，會經過選單所做的全部檢查，然後前往已綁定的傳送通道。未綁定的物品會提示這一點。如果已綁定的傳送通道不存在了，綁定會被清除，該站點也會被遺忘。

## 模型、方塊狀態與材質

*方塊與物品*

定義方塊或物品就會登錄它。它*看起來*如何，仍然是一組一般的資源檔案，放在 Minecraft 已在使用的相同資料夾、相同格式中，位於你自己的命名空間之下。

```
assets/mypack/blockstates/ruby_ore.json
assets/mypack/models/block/ruby_ore.json
assets/mypack/models/item/ruby/ruby.json
assets/mypack/textures/blocks/ruby_ore.png
assets/mypack/lang/en_us.lang
```

### 為變體命名

*模型、方塊狀態與材質*

每個擁有多個變體的方塊都會取得一個名為 `blocks` 的屬性，其值就是定義中的變體名稱。因此登錄 `ruby_ore` 與 `deep_ruby_ore` 的方塊檔案，需要有這兩個變體的方塊狀態：

```json
{
  "variants": {
    "blocks=ruby_ore": { "model": "mypack:ruby_ore" },
    "blocks=deep_ruby_ore": { "model": "mypack:deep_ruby_ore" }
  }
}
```

只有單一變體的方塊同樣保有 `blocks` 屬性，所以它的鍵仍是 `blocks=<name>`，但僅限確實有該屬性的類型。有十二種類型把全部中繼資料都用在形狀上，只有一個變體，也沒有 `blocks` 屬性，因此只以自己的屬性為鍵。[依類型區分的方塊狀態](#依類型區分的方塊狀態)說明了哪些是哪些。

方塊若有自己的屬性，會依狀態列出的順序以逗號連接，例如 `blocks=ruby_log,axis=y`、`blocks=ruby_slab,half=bottom`、`blocks=ruby_wall,up=true,north=true`。階梯方塊沒有 `blocks` 屬性，所以只以 `facing=east,half=bottom,shape=straight` 為鍵。有兩個屬性是刻意省略的：牆自己的變體屬性，以及樹葉方塊的 `check_decay` 與 `decayable`，所以樹葉只需要 `blocks=ruby_leaves`。旗幟完全沒有變體屬性，站立時以 `rotation=0` 到 `15` 為鍵，掛在牆上時以 `facing=north` 為鍵，詳見[旗幟](#旗幟)。

### 依類型區分的方塊狀態

*模型、方塊狀態與材質*

方塊狀態檔案必須包含什麼，取決於兩件事：該類型是否帶有 `blocks` 屬性，以及它自己有哪些屬性。

| 類型                      | 登錄內容                          | 方塊狀態屬性                                                                                                    | 變體數 |
| ------------------------- | --------------------------------- | --------------------------------------------------------------------------------------------------------------- | ------ |
| `basic`、`ore`、`falling` | 一個方塊                          | `blocks`                                                                                                        | 16     |
| `flower`                  | 一個方塊                          | `blocks`                                                                                                        | 16     |
| `portal`                  | 一個方塊                          | `blocks`                                                                                                        | 16     |
| `fence`、`pane`           | 一個方塊                          | `blocks`、`north`、`east`、`south`、`west`                                                                      | 16     |
| `wall`                    | 一個方塊                          | `blocks`、`up`、`north`、`east`、`south`、`west`                                                                | 16     |
| `slab`                    | 兩個，`<name>` 與 `<name>_double` | 半磚是 `blocks` 與 `half`；雙層磚只有 `blocks`                                                                  | 8      |
| `log`                     | 一個方塊                          | `blocks`、`axis`，其值為 `x`、`y`、`z` 或 `none`                                                                | 4      |
| `leaves`                  | 一個方塊                          | `blocks`                                                                                                        | 4      |
| `stairs`                  | 一個方塊                          | `facing`、`half`、`shape`                                                                                       | 1      |
| `door`                    | 一個方塊                          | `facing`、`half`、`hinge`、`open`                                                                               | 1      |
| `trapdoor`                | 一個方塊                          | `facing`、`half`、`open`                                                                                        | 1      |
| `fence_gate`              | 一個方塊                          | `facing`、`in_wall`、`open`                                                                                     | 1      |
| `banner`                  | 兩個，`<name>` 與 `<name>_wall`   | 站立的是 `rotation`，`0` 到 `15`；牆上的是 `facing`                                                             | 1      |
| `ladder`、`torch`         | 一個方塊                          | `facing`，火把另外為四面牆加上 `up`                                                                             | 1      |
| `bell`                    | 一個方塊                          | `facing` 與 `attachment`，其值為 `floor`、`ceiling`、`single_wall` 或 `double_wall`，另加擺動部分的 `body` 項目 | 1      |
| `crop`                    | 一個方塊                          | `age`，一律為 `0` 到 `7`，不論 `maxAge` 為何                                                                    | 1      |
| `cane`                    | 一個方塊                          | `age`，`0` 到 `15`                                                                                              | 1      |
| `sapling`                 | 一個方塊                          | `stage`，`0` 到比 `stages` 少一                                                                                 | 1      |
| `vine`                    | 一個方塊                          | `up`、`north`、`east`、`south`、`west`，且僅限 multipart                                                        | 1      |

有四個屬性會替你去掉，所以寫鍵時不要加上它們：門與柵欄門上的 `powered`、牆上的 `variant`，以及樹葉上的 `check_decay` 與 `decayable`。

不論 `maxAge` 為何，農作物都保有原版的八個 `age` 值，因為 `maxAge` 只決定它能長到多大，所以它的方塊狀態一律寫 `age=0` 到 `age=7`。

有兩種類型會登錄第二個方塊。半磚的 `<name>_double` 需要自己的方塊狀態，以 `blocks` 為鍵、不帶 `half`，而且永遠不會有自己的物品。旗幟的 `<name>_wall` 見[旗幟](#旗幟)。

**藤蔓是唯一不能使用 Forge 格式的類型**，因為 `forge_marker` 不支援 multipart，所以它的方塊狀態是一般的原版 `multipart` 清單，材質直接寫進模型裡。

**Forge 格式比較短，範例資源包用的就是它。** 原版方塊狀態把每種組合都寫成各自的鍵，階梯就有四十個。使用 `"forge_marker": 1` 時，檔案只需把每個屬性列出一次，由遊戲自行組合，因此同樣的四十個狀態只要十一個項目：

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

`defaults` 會合併進每個項目，像 `stairs` 這樣的單純模型名稱表示 `minecraft:block/stairs`，而 `inventory` 是你手中物品所用的模型。三個階梯父模型 `stairs`、`inner_stairs` 與 `outer_stairs` 接受 `bottom`、`top` 與 `side` 材質。

**連接型的類型為每一側加上一個子模型。** 柵欄、玻璃片或牆的每個方向都有一個布林值，`true` 會把另一個模型接在立柱上，而不是取代它：

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

父模型有：帶 `texture` 的 `fence_post` 與 `fence_side`；帶 `wall` 的 `wall_post` 與 `wall_side`，無立柱的情況再加 `block`；以及帶 `pane` 與 `edge` 的 `pane_post`、`pane_side`、`pane_side_alt`、`pane_noside` 與 `pane_noside_alt`。每一個都需要 `"uvlock": true`。

其餘類型只用一個父模型。`cube_all` 接受 `all`，是 `basic`、`ore`、`falling` 或 `leaves` 方塊所需要的。`cube_column` 接受 `end` 與 `side`，用於 `log`，並依 `axis` 旋轉。`cross` 接受 `cross`，是 `flower`、`cane` 或 `sapling` 所需要的；`crop` 則使用它自己各階段的模型。半磚需要兩個自己的模型，下半與上半，因為它是畫成形狀而不是立方體。

**範例資源包就是實際可用的參考。** [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) 為上表中的每種類型都附帶定義、方塊狀態與模型，原版格式與 Forge 格式各一份，所以遇到不明顯的形狀，直接複製比自己摸索更快。

### 物品模型

*模型、方塊狀態與材質*

預設情況下，物品使用方塊狀態為該變體提供的模型，因此不需要再做什麼。在方塊上設定 `"itemModel": "item"`，會讓它改為尋找自己的檔案，位於 `models/item/<block>/<variant>.json`。

物品則一律採用後一種方式，因為每個資源包物品都有子類型：

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

路徑是物品的登錄名稱，後面接變體名稱。

流體完全不需要模型，會依 `still` 與 `flow` 材質自動產生一個。

### 門、地板門與柵欄門

*模型、方塊狀態與材質*

這三種都把全部中繼資料用在它們採用的形狀上，所以各自都是單一變體，在寫檔案之前有幾件事值得先知道。

**它們沒有 `blocks` 屬性**，所以它們的方塊狀態只以形狀為鍵：門是 `facing=east,half=lower,hinge=left,open=false`，地板門是 `facing=north,half=bottom,open=false`，柵欄門是 `facing=south,in_wall=false,open=false`。合計 32 個鍵，16 個加 16 個。

**門與柵欄門省略了 `powered`。** 兩者其實都有這個屬性，否則它們的方塊狀態會因為一個看不出任何變化的軸而加倍。它會替你去掉，就像遊戲對自己的門和柵欄門所做的那樣，所以寫鍵時不要加上。地板門從來沒有這個屬性。

**讓模型指向會接受材質的父模型**，而不是已完成的原版模型：

| 類型         | 父模型                                                                                               |
| ------------ | ---------------------------------------------------------------------------------------------------- |
| `door`       | `block/door_bottom`、`block/door_bottom_rh`、`block/door_top`、`block/door_top_rh`                   |
| `trapdoor`   | `block/trapdoor_bottom`、`block/trapdoor_top`、`block/trapdoor_open`                                 |
| `fence_gate` | `block/fence_gate_closed`、`block/fence_gate_open`、`block/wall_gate_closed`、`block/wall_gate_open` |

門接受兩種材質，`bottom` 與 `top`；另外兩種接受一種，`texture`。兩個門上半部的模型都會用 `bottom` 貼上它們的上緣，所以四個檔案都要宣告兩者，即使上半部的看起來只需要一個。柵欄門的變體需要 `"uvlock": true`，與遊戲自己的一樣。

**它們的材質會用到每一個像素，這一點最容易讓人踩到。** 門的寬面對應 `[0, 0, 16, 16]`，也就是整張圖，而窄邊與頂面、底面取自同一個正方形：側面取第 0 到 3 欄，頂面與底面取第 13 到 16 欄。地板門也一樣，平面取整張圖，四個邊框取自第 13 到 16 列。

所以不要留空白邊。如果以為形狀比檔案窄，就清掉一邊的幾欄，會在面的正中間切出一道縫，並完全失去頂面與底面。應該把門框或門梃畫進這些邊緣像素，它們看起來就像方塊自身邊緣的飾邊。

**它們的物品依類型而不同。** 門的物品是平面精靈圖，即 `item/generated` 套在它自己的 `textures/items/<name>.png` 上，因為手中的門是畫成圖片而不是形狀。地板門與柵欄門的物品則以方塊模型為父模型，分別是下半部與關閉的門，這也是遊戲對自己的同類物品的做法。

三者都接受你給的任何 `material`。柵欄門建立在一個會把自己固定為木質的方塊上，所以本模組在登錄時把材料設回你指定的，這樣石製柵欄門就會像它自稱的石頭一樣用鎬挖掘。

### 旗幟

*模型、方塊狀態與材質*

旗幟是唯一一種方塊的形狀與模型部件的形狀各走各路的類型，所以值得完整說明。

**它會登錄兩個方塊。** 一個定義會給你以自己名稱命名的站立旗幟，以及另一個名為 `<name>_wall` 的方塊，用於懸掛的旗幟。兩者都需要方塊狀態；只有站立的那個有物品，由該物品決定放置哪一個：點擊方塊頂部時放站立的，點擊側面時放牆上的。你永遠不會直接放置牆上的方塊，它也不需要自己的物品。

**站立的需要 Forge 方塊狀態。** 它的屬性是 `rotation`，範圍 `0` 到 `15`，因為旗幟是以十六分之一而不是四分之一圈轉動。原版方塊狀態無法表達：它的 `y` 會經過 `ModelRotation`，而後者只接受 0、90、180 與 270，遇到其他值就會擲出例外。Forge 的格式接受任何角度，所以這十六個項目寫成變換：

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

……依此類推到 `15`，每一個再多轉 `-22.5` 度。正負號與遊戲自己的旗幟一致，它們依旋轉值的負數轉動。請把模型做成朝南，因為玩家面朝南放置旗幟時，旗幟最後指向的就是那裡。牆上的方塊是一般的原版方塊狀態，有常規的四個 `facing` 項目，分別為 0、90、180 與 270，因為它沒有任何小數角度。

**模型幾乎有兩個方塊高。** 旗幟在放置與碰撞上只佔一個方塊，但繪製範圍遠超出它，止於自身方塊頂部的模型看起來會矮小。原版的比例以十六分之一方塊為單位，值得原樣照抄：

| 部件     | 起點   | 終點    |
| -------- | ------ | ------- |
| 旗桿     | `0`    | `28`    |
| 橫桿     | `28`   | `29.33` |
| 旗布     | `2.67` | `29.33` |
| 旗布寬度 | `1.33` | `14.67` |
| 牆上旗布 | `-13`  | `13.67` |

所以站立的旗幟一路延伸到 `29.33`，接近兩個方塊，而牆上的旗幟則垂在承載它的方塊*下方*十三個十六分之一處。模型元素的範圍可從 `-16` 到 `32`，所以兩者都放得下。牆上的形式沒有旗桿或橫桿，只有旗布。

**旗布的高度是寬度的兩倍，你的材質也必須如此。** 該面是 `13.33` 乘 `26.67`。把正方形材質貼上去，圖案會被壓成一半高。方塊材質本身不能是寬度兩倍高，因為任何非正方形都會被當成動畫讀取，所以變通的辦法是使用較大的正方形材質圖，把旗布放在其中一部分：一個 32×32 的檔案，把旗布放在 16×32 的區域，以 `"uv": [0, 0, 8, 16]` 定址，旁邊的空間放旗桿與橫桿的長條。UV 座標不論檔案解析度為何都是 0 到 16，所以同樣的數字在任何大小都適用。

**它的物品需要自己的模型。** 繼承這麼高的模型的物品，在一般的方塊縮放下會衝出它的格子，所以請在 `models/item/<name>.json` 加上自己的 `display` 區段，把縮放調小，並把整體平移回畫面內。

**它沒有顏色或圖案。** 資源包旗幟沒有方塊實體，所以沒有任何東西承載原版旗幟存放在方塊實體中的圖層清單。圖案就是材質，就像門的外觀就是它的材質，一個定義就是一面旗幟。替它染色並疊上圖案，是資源包做不到的。

**它採用你給的 `material`。** 它所建立的方塊會把自己固定為木質，所以本模組在登錄時把材料設回你指定的，這樣石製旗幟就會像它自稱的石頭一樣用鎬挖掘。

### 以像素圖編寫的材質

*模型、方塊狀態與材質*

材質可以是 JSON 檔案而不是 PNG。把它放在 PNG 原本該在的位置，並在整個名稱後面加上 `.json`，這樣 `textures/blocks/panel.png.json` 就會回應所有對 `textures/blocks/panel.png` 的請求。其他都不變：模型仍像往常一樣指向 `mypack:blocks/panel`，材質圖集、mipmap 與動畫 `.mcmeta` 都能運作，因為遊戲收到的仍是 PNG。

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

| 鍵        | 必填       | 值                       | 預設 | 作用                                                 |
| --------- | ---------- | ------------------------ | ---- | ---------------------------------------------------- |
| `size`    | 是，或繼承 | `widthxheight`           |      | 橫向與縱向各有多少像素                               |
| `rows`    | 是，或繼承 | 文字清單                 |      | 每列像素一個字串，每個像素一個字元，由上往下         |
| `palette` | 是，或繼承 | 物件                     |      | 字元對應到顏色，`#RRGGBB` 或 `#AARRGGBB`             |
| `extends` | 否         | 另一個像素圖             |      | 本像素圖所延伸的起點                                 |
| `tint`    | 否         | 含 `from` 與 `to` 的物件 |      | 沿兩種顏色之間的色階，為所有繼承來的內容重新上色     |
| `notes`   | 否         | 物件                     |      | 字元對應到說明其用途的一行文字，會被繼承，永遠不繪製 |

**不需要宣告名稱。** 檔案自己的路徑就是它的名稱，與 PNG 完全一樣，所以位於 `assets/mypack/textures/blocks/panel.png.json` 的像素圖在模型中是 `mypack:blocks/panel`，位於 `assets/mypack/textures/items/gem.png.json` 的像素圖在物品模型中是 `mypack:items/gem`。沒有任何東西特別指向像素圖；方塊或物品照常指定它的材質，永遠不會知道拿到的是兩者中的哪一種。這也表示方塊與物品資料夾仍然分開，就像 PNG 一樣：`textures/blocks/gem.png.json` 與 `textures/items/gem.png.json` 是兩張不同的材質，並快取成兩個不同的檔案。

**大小隨你決定**，每邊最大 4096，兩邊不必相同。`16x16` 是一般的方塊面，`16x32` 則是門的一半或動畫會用到的那種高長條。大小是經過檢查而不是猜的：每列像素給一行、每個像素給一個字元，否則該像素圖會被拒絕，日誌會指出是哪一列以及發現了什麼。調色盤中沒有顏色的字元會保持透明，所以 `.` 或空格就是一個洞。

**範本才是重點。** `extends` 指定另一個像素圖，寫成 `namespace:path`，或同一資源包內的單純路徑，延伸它的檔案會繼承其 `size`、`rows` 與 `palette`。它自己寫出的內容優先，而且不必全部寫出，所以一個完整的變體可以只是幾種顏色：

```json
{
  "extends": "mypack:textures/blocks/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

這就是一張完整的第二種材質：同樣的形狀，換成紫珀色，而且如果範本中的形狀日後被重畫，所有變體都會跟著更新。變體也可以改為給出自己的 `rows` 並保留範本的調色盤，這是反過來的做法，即相同的顏色配上不同的圖案。繼承最多八層，迴圈會被偵測並回報，而指定了無人提供的範本的像素圖會被回報，而不是畫成空白。

**兩張材質中哪一張是範本**，取決於哪一張包含更多區別，而不是哪一張先畫。變體為每個字元給一種顏色，所以範本中稱為同一字元的每個像素，在變體中都是同一種顏色。因此畫在石頭上的礦石無法繼承石頭的 `rows`：石頭把斑點位置都稱為一般石頭，而變體無論怎麼寫都無法把一個字元拆成兩個。反過來就行得通。讓礦石當範本，石頭色調與礦石色調就各自有自己的字元，第二種礦石只要四種顏色：

```json
{
  "extends": "mypack:textures/blocks/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

真的想要不同圖案的變體，會像上面那樣給出自己的 `rows`，然後只繼承調色盤。當顏色才是重點、形狀無關緊要時，這樣做值得；當形狀才是重點時，就把形狀放進範本，讓變體去指定顏色。

**範本完全不必是一張材質。** 像素圖只有在路徑以 `.png` 結尾時才會提供給遊戲，所以位於 `textures/blocks/ore_template.json` 的範本對遊戲是看不到的，只為被延伸而存在，而位於 `textures/blocks/ore_template.png.json` 的則也會回應對 `ore_template.png` 的請求。共用的形狀命名時不要加 `.png`，就不會有東西意外地請求它。

**範本也可以是真正的圖片而不是像素圖。** 把 `extends` 指向任何資源包或遊戲本身提供的 PNG，調色盤的意義就變了：鍵變成該圖片中已有的顏色，值則是要換上的顏色。不做任何描繪，也不寫 `rows`，所以資源包可以就地替原版或模組的材質重新上色：

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

這就是用原版自己的石頭做成的紅寶石礦：四種斑點色調被換掉，其他每個像素維持原樣。圖片中不存在的顏色只是永遠比對不到，大小取自圖片，除非你自己指定，而指定的大小必須與之相符。

`extends` 優先採用像素圖：它先在該路徑找像素圖，只有在沒有資源包提供時才退而使用圖片。兩者都不是的名稱會被回報，而不是畫成空白。以圖片為基礎的建構是用戶端的工作，因為讀取的是遊戲自己的資源，所以專用伺服器從不執行。

**範本可以上色而不必重畫。** `tint` 給出兩種顏色，並沿兩者之間的色階為像素圖所繼承的一切重新上色。每個繼承來的顏色，其亮度就是它在該色階上的位置，所以黑色落在 `from`，白色落在 `to`，兩者之間的每個色調依比例混合。透明度維持不變。因此一張灰階範本加兩種顏色就是一個完整的變體：

```json
{
  "extends": "mypack:textures/items/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` 可以省略，此時它是黑色，上色就變成一般的相乘，與繪製時的 `tintindex` 形式相同。差別在於這種上色是一次畫進 PNG 並快取的，所以每幀零成本，而且能作用在沒有任何東西上色的材質上，但它也無法像 `grass` 或 `foliage` 那樣隨生態域變化。

範本仍是一張一般的像素圖：打開來看，它畫出來就是它本來的灰色。兩種顏色都接受 `#RRGGBB`、`#AARRGGBB` 或開頭為 `0x` 的形式，兩者都不是的值會讓該像素圖不被繪製，而不是畫成錯誤的顏色。上色與其他內容一樣會被繼承，沿繼承鏈往下第一個出現的生效，所以變體自己的上色優先於它所延伸的那個。它也適用於圖片範本，此時在調色盤的顏色替換之後執行。

**上色是兩種顏色之間的色階**，所以只適合色調落在同一條色階上的材質。有兩個不相關區域的形狀，例如礦石的石頭與斑點，就不屬於此類，需要把調色盤完整寫出來。

**知道範本的字元代表什麼**是延伸範本時最麻煩的部分，這就是上面 `notes` 區段的用途：字元對應到一行簡短說明，與調色盤一樣被繼承，且永遠不繪製。替範本的字元加上標籤，延伸它的人就知道該覆寫哪些。

`/rdpl pixelmap <namespace:path>` 會回報一個像素圖實際的結果，這是不必打開鏈上每個檔案就能寫出變體的可靠方法：

```
oretest:textures/blocks/ruby_ore.png is 16x16
  built from oretest:textures/blocks/ruby_ore.png.json
  built from oretest:textures/blocks/gem_ore.png.json
  rows come from oretest:textures/blocks/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  礦石本體，每個礦塊的大部分
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  礦石陰影，最暗的色調
  a  #747474  86 pixel(s)  set by gem_ore.png.json   石頭，最常見的色調
```

每個字元都會列出它的顏色、涵蓋多少像素、鏈上哪個檔案設定了它，以及該檔案說明它的用途。路徑可以用簡短的寫法 `mypack:blocks/panel`，也可以寫完整。顯示 0 個像素的字元，是調色盤中有列出但 `rows` 從未使用的，通常是某一列打錯字所致。

**繪製好的圖片會存在磁碟上**，位於 `rdploader/pixelmap-cache`，每個命名空間一個資料夾，檔名為材質名稱後面加上其來源的雜湊值。雜湊涵蓋整條鏈，包括像素圖本身與它上面的每個範本，所以編輯範本會改變每個繼承它的變體的戳記，它們都會被重畫。像素圖重畫時，舊的檔案會被清掉。

每次掃描資源包時也會巡視該資料夾，凡是沒有任何資源包再提供其像素圖的圖片都會被刪除，連同留下的空資料夾。重新命名材質、移除資源包、刪除像素圖，其快取的圖片也會跟著消失，而不是永遠留在那裡。刪除整個資料夾只會多花重畫它們的時間，而且掃描資源包時會略過它，所以它絕不會被誤認為資源包。

PNG 永遠優先。如果 `panel.png` 與 `panel.png.json` 同時存在，就會提供 PNG，像素圖不會被繪製，所以日後可以用手繪的材質取代產生的材質，而不必更動任何指向它的東西。

**沒有人必須手寫這些檔案。** 儲存庫附有完整往返流程的指令碼，位於 [`pixelmap/`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/pixelmap)：`png_to_pixelmap.py` 把一個 PNG 轉成像素圖，`convert_pack.py` 對資源包內的每個材質執行轉換，`verify_pack.py` 則繪製已轉換資源包的像素圖並與其來源 PNG 比較，因此可以在把原檔收起來之前信任這次轉換。

### 值得留意的陷阱

*模型、方塊狀態與材質*

**指向單純原版模型的方塊狀態也會繼承原版的材質。** `normal_torch`、`ladder`、`wooden_door_*` 與 `wheat_stage*` 都帶有自己的材質，所以指向它們的方塊不論你在方塊狀態中寫什麼，都會是原版的外觀。`cube_all`、`cross` 與 `block/crop` 這類父模型的材質取自方塊狀態，行為正常，[門、地板門與柵欄門](#門地板門與柵欄門)下列出的門、地板門與柵欄門父模型也是如此。

**`forge_marker: 1` 不支援 multipart。** 藤蔓的方塊狀態必須是單純的原版 multipart，材質直接寫進模型，而不是傳入。

**名稱來自語言檔案，而且一個方塊需要兩個。** 方塊或物品會顯示原始鍵，直到 `lang/en_us.lang` 為它提供名稱。你手持並放置的物品，其鍵是方塊的登錄名稱後面接變體，`tile.mypack:ruby_ore.ruby_ore.name=Ruby Ore`，這是多數資源包記得寫的那個。方塊本身的鍵只有登錄名稱，`tile.mypack:ruby_ore.name=Ruby Ore`，任何向已放置的方塊詢問名稱的東西都讀取這個，包括容器畫面的標題列。兩個都要寫，否則物品在你手中顯示正確，而它開啟的畫面標題卻是空白。

**單一變體的類型名稱要寫兩次。** 可容納多個變體的方塊，鍵只有登錄名稱，如上所述。把全部中繼資料都用在形狀上的方塊，則在後面再加上變體名稱，所以定義在 `blocks/my_door.json`、只有一個名為 `my_door` 的變體的門，鍵是 `tile.mypack:my_door.my_door.name=My Door`。這涵蓋 `door`、`trapdoor`、`fence_gate`、`banner`、`stairs`、`ladder`、`torch`、`crop`、`cane`、`sapling` 與 `vine`。這類類型若有自己的物品，如門與旗幟，同樣的鍵要在 `item.` 而不是 `tile.` 之下再寫一次。

## 讓原版正確對待你的方塊

*方塊與物品*

原版在十幾個地方是以身分檢查自己的方塊，所以明明該能運作的資源包方塊常常不行。兩個鍵可以解決。

```json
{
  "material": "ground",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "meta": 0, "hardness": 0.6 } }
}
```

**`plantTypes`** 列出你的方塊支援的 Forge 植物類型，讓樹苗、農作物與花可以種在上面。

**`behavesAs`** 讓原版把你的方塊當成它自己的某種方塊：

| 值        | 作用                                                                                |
| --------- | ----------------------------------------------------------------------------------- |
| `till`    | 鋤頭可以把它變成耕地                                                                |
| `path`    | 鏟子可以把它變成土徑                                                                |
| `bush`    | 花、草與樹苗可以種在上面並留在上面，就像泥土一樣。等同於 `plantTypes` 中的 `plains` |
| `animals` | 動物在光照下會在上面生成，就像在草方塊上一樣                                        |

## 物品

*方塊與物品*

`<namespace>/items/*.json`

檔案的路徑就是物品的登錄名稱，所以 `mypack/items/ruby.json` 會登錄 `mypack:ruby`。`variants` 內的鍵為該物品的中繼資料值命名，每一個的模型放在 `models/item/ruby/<key>.json`。

這裡一次列出所有鍵。實際的檔案只寫需要的那些。標示為某個類型的鍵只會被該類型讀取。

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
  "sleepingBag": true,
  "container": "minecraft:glass_bottle",
  "crop": "mypack:ruby_crop",
  "soil": "minecraft:farmland",
  "potionTypes": ["mypack:ruby_tonic"],
  "rocket": "mypack:supply_rocket",
  "attackReach": 5.0,
  "sweep": { "shape": "arc", "reach": 4.0, "angle": 90.0, "damage": 0.5 },
  "combo": { "window": 40, "steps": [{ "damage": 1.0 }, { "damage": 1.5 }] },
  "rollStore": "dice_total",
  "locates": "Village",
  "passesTurn": false,
  "bauble": "ring",
  "wornEffects": [{ "potion": "minecraft:night_vision", "amplifier": 0, "ambient": false, "showParticles": false }],
  "wornAttributes": [{ "attribute": "generic.maxHealth", "amount": 2.0, "operation": 0 }],
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

### 物品類型

*物品*

| 類型            | 取得的內容                                                                        |
| --------------- | --------------------------------------------------------------------------------- |
| `basic`         | 一般物品。`type` 缺少時使用                                                       |
| `food`          | 食用，帶有飢餓值與飽和度                                                          |
| `drink`         | 飲用而非食用，會歸還空容器                                                        |
| `tool`          | 由材料製成的鎬、斧、鏟或劍                                                        |
| `armor`         | 由材料製成的頭盔、胸甲、護腿或靴子                                                |
| `seed`          | 種下你的某種農作物                                                                |
| `potion`        | 使用時施加你的藥水效果                                                            |
| `portway`       | 前往傳送通道：開啟列出所有站點的選單，或前往已綁定的那個。見[傳送通道](#傳送通道) |
| `potion_bottle` | 存放你的藥水類型，並顯示在創造模式分頁中                                          |
| `rocket`        | 在發射台上放置你的 Galacticraft 火箭變體                                          |

`potion_bottle` 以 `potionTypes` 列出它能存放的內容，這是藥水類型名稱的陣列，例如 `["mypack:ruby_tonic"]`。清單為空的會什麼都不登錄，日誌會說明。

`rocket` 以 `rocket` 指定它放置的實體變體。它需要 Galacticraft，沒有它或沒有 `rocket` 時什麼都不登錄，日誌會說明。見實體變體下的 Galacticraft 火箭。

帶有 `locates` 的一般物品就是定位指南針。右鍵會尋找該名稱最近的地點並讓物品指向那裡，持有者會得知指針停在了哪裡。名稱可以是像 `Village` 這樣的原版結構、像 `minecraft:river` 這樣的生物群系 ID，或用 `locateAs` 註冊的名稱。該物品的堆疊上限為 1，`cooldown` 決定它在多長時間內拒絕再次搜尋。

`<namespace>/items/*.json`

```json
{
  "locates": "Village",
  "cooldown": 100,
  "variants": {
    "seeker": {
      "meta": 0,
      "maxSize": 1
    }
  }
}
```

目標儲存在物品堆中，因此物品會一直指向它，直到下一次右鍵，在其他維度則會旋轉。指針由物品自己的模型繪製，使用與原版指南針相同的 `angle` 覆寫，每幀一項：

`assets/mypack/models/item/seeker/seeker.json`

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "mypack:items/seeker_16"
  },
  "overrides": [
    { "predicate": { "angle": 0.0 }, "model": "mypack:item/seeker_16" },
    { "predicate": { "angle": 0.03125 }, "model": "mypack:item/seeker_17" },
    { "predicate": { "angle": 0.0625 }, "model": "mypack:item/seeker_18" }
  ]
}
```

帶有 `sleepingBag` 的一般物品就是睡袋。夜裡對實心方塊的頂面按右鍵，持有者會躺在上方的方塊上。使用條件與床相同：必須是夜晚、所在維度允許睡覺，且八格內沒有敵對生物。所有玩家都入睡時會跳過夜晚，與床一樣。醒來不會改變重生點，睡袋也會留在物品欄中。`cooldown` 可避免它被連續使用。

### 物品檔案鍵

*物品*

| 鍵               | 必填          | 值                                                           | 預設                 | 作用                                                                                                                                                                                       |
| ---------------- | ------------- | ------------------------------------------------------------ | -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `variants`       | 是            | 變體名稱對應變體的物件                                       |                      | 每個中繼資料值一個項目。鍵在方塊狀態、模型路徑與語言鍵中為該值命名。登錄名稱來自檔案自己的路徑                                                                                             |
| `type`           | 否            | 上述類型之一                                                 | `basic`              | 物品採用的類型                                                                                                                                                                             |
| `creativeTab`    | 否            | 分頁名稱                                                     | 無                   | 它出現的分頁                                                                                                                                                                               |
| `material`       | tool、armor   | 材料名稱                                                     | 無                   | 它由你的哪種材料製成                                                                                                                                                                       |
| `toolClass`      | tool          | `pickaxe`、`axe`、`shovel`、`sword`                          | 無                   | 是哪種工具                                                                                                                                                                                 |
| `slot`           | armor         | `head`、`chest`、`legs`、`feet`                              | 無                   | 穿戴的位置。`helmet`、`chestplate`、`leggings` 與 `boots` 也可用                                                                                                                           |
| `eat`            | food          | 布林值                                                       | `false`              | 使用食用動畫                                                                                                                                                                               |
| `alwaysEdible`   | food          | 布林值                                                       | `false`              | 飢餓條全滿時也能食用                                                                                                                                                                       |
| `useDuration`    | drink、potion | 整數，單位為刻                                               | `32`                 | 對 `drink` 與 `potion` 而言，是喝下它所需的時間，單位為刻（20 刻 = 1 秒）。其他類型會忽略它                                                                                                |
| `attackReach`    | 否            | 浮點數，格                                                   | `3.0`                | 用主手物品攻擊時能打到的距離。對生物按右鍵時同樣適用。請參閱[武器](#武器)                                                                                                                  |
| `sweep`          | 否            | 物件                                                         | 無                   | 同時傷害滿力度一擊周圍的其他生物。請參閱[武器](#武器)                                                                                                                                      |
| `combo`          | 否            | 物件                                                         | 無                   | 間隔不超過 `window` 刻的連續攻擊依序走過 `steps`。請參閱[武器](#武器)                                                                                                                      |
| `attackSpeed`    | 否            | 浮點數                                                       | 依工具類別而定       | 對 `tool` 而言是攻擊速度屬性，劍為 `-2.4`                                                                                                                                                  |
| `cooldown`       | 否            | 整數，單位為刻                                               | `0`                  | 對 `food`、`drink` 與 `potion` 而言，是物品被消耗後拒絕再次使用的時間；對有 `rolls` 的物品而言，是兩次擲出之間的間隔                                                                       |
| `container`      | drink         | 物品名稱                                                     | 無                   | 留下的東西，例如瓶子                                                                                                                                                                       |
| `crop`           | seed          | 方塊名稱                                                     | 無                   | 它種下的農作物                                                                                                                                                                             |
| `soil`           | seed          | 方塊名稱                                                     | `minecraft:farmland` | 可以種在什麼上面                                                                                                                                                                           |
| `portway`        | portway       | 物件                                                         | 無                   | 傳送物品的設定：適用於一次傳送的[傳送通道鍵](#傳送通道)，加上 `consume` 與 `bind`。見[傳送物品](#傳送物品)                                                                                 |
| `rocket`         | rocket        | `namespace:name`                                             | 無                   | 它放置的實體變體                                                                                                                                                                           |
| `rolls`          | 否            | `coin`、`d6`、`2d6+1`、骰子或牌組                            | 無                   | 對一般物品而言，右鍵會像 `/rdplserver game` 一樣擲出，並通知資源包的預設對象。見[骰子與牌組](#骰子與牌組)                                                                                  |
| `rollStore`      | 否            | 目標名稱                                                     | 無                   | 用於帶 `rolls` 的物品：每次使用時，把擲骰總和作為持有者的分數記入這個計分板目標。目標不存在時，擲骰會被拒絕並在聊天中提示一行                                                              |
| `bauble`         | 否            | `amulet`, `ring`, `belt`, `trinket`, `head`, `body`, `charm` | 無                   | 物品可配戴的 Baubles 欄位，適用於任何類型的物品。未安裝 Baubles 時會被忽略                                                                                                                 |
| `wornEffects`    | 否            | 效果清單                                                     | 無                   | 物品位於其 Baubles 欄位期間持續作用於配戴者的藥水效果，寫作 `potion`、`amplifier`、`ambient` 與 `showParticles`                                                                            |
| `wornAttributes` | 否            | 屬性修改清單                                                 | 無                   | 物品位於其 Baubles 欄位期間持續作用於配戴者的屬性修改，寫作 `attribute`、`amount` 與 `operation`                                                                                           |
| `locates`        | 否            | `Village`, `minecraft:river`                                 | 無                   | 對一般物品而言，右鍵會尋找該名稱最近的地點，並像指南針一樣讓物品指向那裡。名稱可以是原版結構或生物群系，也可以是用 `locateAs` 註冊的名稱。該物品的堆疊上限為 1。請參閱上方的定位指南針段落 |
| `passesTurn`     | 否            | 布林值                                                       | `false`              | 對一般物品而言，右鍵會像 `/rdplserver game pass` 一樣跳過持有者的回合。見[輪流行動](#輪流行動)                                                                                             |
| `sleepingBag`    | 否            | 布林值                                                       | `false`              | 對一般物品而言，右鍵方塊頂面會讓持有者躺下睡覺，並像床一樣跳過夜晚。不會改變重生點，也不會被消耗。僅在允許睡覺的地方有效。若同時有 `locates`、`rolls` 或 `passesTurn` 則忽略               |
| `requires`       | 否            | 模組 id 或資源包命名空間清單                                 | 無                   | 除非全部存在，否則略過此檔案                                                                                                                                                               |

### 物品變體鍵

*物品*

| 鍵           | 必填        | 值                                   | 預設     | 作用                                                                                           |
| ------------ | ----------- | ------------------------------------ | -------- | ---------------------------------------------------------------------------------------------- |
| `meta`       | 是          | 0 到 15                              |          | 此變體佔用的中繼資料值                                                                         |
| `maxSize`    | 否          | 1 到 64                              | `64`     | 堆疊上限                                                                                       |
| `rarity`     | 否          | `common`、`uncommon`、`rare`、`epic` | `common` | 提示框中的名稱顏色                                                                             |
| `healAmount` | food        | 整數，單位為半根雞腿                 | `0`      | 恢復的飢餓值                                                                                   |
| `saturation` | food        | 浮點數                               | `0.0`    | 恢復的飽和度                                                                                   |
| `oreDict`    | 否          | 礦物辭典名稱清單                     | 無       | 此變體登錄的礦物辭典名稱                                                                       |
| `potion`     | food、drink | `potion,duration,amplifier`          | 無       | 變體被食用或飲用時施加的效果。第四段 `true` 會讓它成為環境效果。有益的效果會在提示框中列出名稱 |

### 武器

*物品*

物品可以比遊戲預設的 `3.0` 格打得更遠，也可以傷害目標周圍的生物，或者依序走完一套連擊。這些鍵寫在物品檔案裡，放在 `attackSpeed` 旁邊。範例省略了其餘的鍵。

```json
{
  "attackReach": 8.0,
  "sweep": { "shape": "arc", "reach": 4.0, "angle": 90.0, "damage": 0.5 },
  "combo": {
    "window": 40,
    "steps": [
      { "damage": 1.0 },
      { "damage": 1.5 },
      { "damage": 3.0, "sweep": { "shape": "circle", "reach": 3.0 } },
      { "damage": 2.0, "sweep": { "shape": "line", "reach": 6.0, "width": 1.5 } }
    ]
  }
}
```

`attackReach` 是用主手物品攻擊時能打到的距離，單位為格，必須大於 `0` 且不超過 `64`。對生物按右鍵時同樣適用。創造模式額外的觸及距離會疊加在其上，而方塊仍然只能觸及遊戲允許的距離。

當一擊以滿力度命中，也就是超過攻擊冷卻的 90% 時，`sweep` 會傷害目標周圍的其他生物。它有以下鍵：

| 鍵       | 必填 | 值                             | 預設   | 作用                                 |
| -------- | ---- | ------------------------------ | ------ | ------------------------------------ |
| `shape`  | 否   | `box`, `arc`, `line`, `circle` | `box`  | 選取生物的形狀，見下文               |
| `reach`  | 否   | 浮點數，0.1 到 64              | `3.0`  | 形狀延伸多遠，單位為格               |
| `angle`  | 否   | 浮點數，1 到 360               | `90.0` | 對 `arc` 而言，扇形的張角，單位為度  |
| `width`  | 否   | 浮點數，0.1 到 64              | `1.0`  | 對 `line` 而言，條帶的寬度，單位為格 |
| `damage` | 否   | 浮點數，0 到 10                | `0.5`  | 每個生物承受的那一擊傷害的比例       |

- `box` 是遊戲本身的橫掃：目標旁邊、與玩家相距不超過物品 `attackReach` 的生物（沒有則為 `3.0`）。其餘的鍵會被忽略。
- `arc` 是玩家前方的扇形，張角為 `angle` 度，長 `reach` 格，針對與玩家大致等高的生物。
- `line` 是沿玩家視線延伸的條帶，寬 `width` 格，長 `reach` 格。
- `circle` 是與玩家相距不超過 `reach` 格、大致等高的所有生物，無論在前方還是後方。

`combo` 讓間隔不超過 `window` 刻的連續攻擊依序走過 `steps`，每擊一步，走完最後一步後重新開始：

| 鍵       | 必填 | 值                       | 預設 | 作用                               |
| -------- | ---- | ------------------------ | ---- | ---------------------------------- |
| `window` | 否   | 整數，單位為刻，至少為 1 | `30` | 仍算作連擊的兩次攻擊之間的最長間隔 |
| `steps`  | 是   | 步驟清單                 |      | 每擊一步。至少一步                 |

每一步有以下鍵：

| 鍵       | 必填 | 值               | 預設           | 作用                                       |
| -------- | ---- | ---------------- | -------------- | ------------------------------------------ |
| `damage` | 否   | 浮點數，0 到 100 | `1.0`          | 在計算護甲之前，對這一擊的傷害相乘         |
| `sweep`  | 否   | 物件             | 物品的 `sweep` | 僅在這一擊取代物品的 `sweep`，鍵與上文相同 |

只有滿力度的一擊才會推進連擊並觸發橫掃。力度較弱的一擊會重置連擊，既得不到該步的傷害，也不會橫掃。進度依玩家保存在記憶體中，不會寫入存檔。登出、換用其他物品或超過 `window` 都會將其重置。橫掃不會傷害玩家、目標、盟友、旁觀者和標記盔甲座，每個生物承受的是經過該步倍率之後的那一擊乘以 `damage`。基礎傷害不高於目標上一次所受傷害的一擊，會在該步倍率生效之前被傷害冷卻丟棄，因此某一步無法將其提高；這只在攻擊速度為 2 或更高時才有影響。

## 流體

*方塊與物品*

`<namespace>/fluids/*.json`

檔案的路徑就是流體的登錄名稱，除非 `name` 另行指定。

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

| 鍵            | 必填 | 值                           | 預設         | 作用                                                                                                                                                                                                                            |
| ------------- | ---- | ---------------------------- | ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | 否   | 字串                         | 檔案名稱     | 流體的登錄名稱                                                                                                                                                                                                                  |
| `still`       | 否   | 材質路徑                     | 原版靜止的水 | 靜止流體的材質                                                                                                                                                                                                                  |
| `flow`        | 否   | 材質路徑                     | 原版流動的水 | 流動流體的材質                                                                                                                                                                                                                  |
| `color`       | 否   | 十六進位色碼                 | 無           | 套用在這些材質上的色調                                                                                                                                                                                                          |
| `bucket`      | 否   | 布林值                       | `true`       | 為它登錄一個桶                                                                                                                                                                                                                  |
| `luminosity`  | 否   | 0 到 15                      | `0`          | 發出的光                                                                                                                                                                                                                        |
| `density`     | 否   | 整數                         | `1000`       | 負值會像氣體一樣向上浮                                                                                                                                                                                                          |
| `temperature` | 否   | 整數，單位為克耳文           | `300`        | 水為 300，熔岩為 1300                                                                                                                                                                                                           |
| `viscosity`   | 否   | 整數                         | `1000`       | 流動有多慢。水為 1000，熔岩為 6000                                                                                                                                                                                              |
| `gaseous`     | 否   | 布林值                       | `false`      | 視為氣體                                                                                                                                                                                                                        |
| `creativeTab` | 否   | 分頁名稱                     | 無           | 桶出現的分頁                                                                                                                                                                                                                    |
| `block`       | 否   | 物件                         |              | 流體方塊。`material`（`water`）、`flammability`（`0`）、`fireSpread`（`0`）、`quantaPerBlock`（`0`）、`potions`（無，給予站在其中的任何東西的效果清單，每個寫成 `potion,duration,amplifier`，可選的第四段 `true` 代表環境效果） |
| `requires`    | 否   | 模組 id 或資源包命名空間清單 | 無           | 除非全部存在，否則略過此檔案                                                                                                                                                                                                    |

## 材料、分頁、音效、礦物辭典

*方塊與物品*

`<namespace>/materials/*.json`

檔案的路徑就是材料的名稱，工具或盔甲物品再於 `material` 中指定它。

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

| 鍵               | 必填 | 值             | 預設                    | 作用                                 |
| ---------------- | ---- | -------------- | ----------------------- | ------------------------------------ |
| `harvestLevel`   | 否   | 0 到 3         | `1`                     | 工具等級。0 木、1 石、2 鐵、3 鑽石   |
| `durability`     | 否   | 整數           | `250`                   | 損壞前可使用的次數                   |
| `efficiency`     | 否   | 浮點數         | `6.0`                   | 挖掘速度。鑽石為 8                   |
| `damage`         | 否   | 浮點數         | `2.0`                   | 攻擊傷害加成                         |
| `enchantability` | 否   | 整數           | `14`                    | 附魔的品質。金為 22                  |
| `repairItem`     | 否   | 物品名稱       | 無                      | 在鐵砧中用來修復它的東西             |
| `reduction`      | 否   | 四個整數的清單 |                         | 護甲值，依序為靴子、護腿、胸甲、頭盔 |
| `toughness`      | 否   | 浮點數         | `0.0`                   | 護甲韌性，如同鑽石                   |
| `equipSound`     | 否   | 音效名稱       | `item.armor.equip_iron` | 穿上盔甲時的音效                     |
| `armorTexture`   | 否   | 材質前綴       | 檔案名稱                | 穿戴時的盔甲材質                     |

### 創造模式分頁

*材料、分頁、音效、礦物辭典*

`<namespace>/tabs/*.json`

檔案的路徑就是分頁的名稱，除非 `label` 另行指定，方塊與物品在 `creativeTab` 中指定它。

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| 鍵      | 必填 | 值       | 預設     | 作用                                                                                           |
| ------- | ---- | -------- | -------- | ---------------------------------------------------------------------------------------------- |
| `label` | 否   | 字串     | 檔案名稱 | 分頁的 id：方塊與物品在 `creativeTab` 中指定它，顯示的名稱則來自語言檔中的 `itemGroup.<label>` |
| `icon`  | 否   | 物品名稱 | 無       | 顯示在分頁上的物品                                                                             |

### 音效

*材料、分頁、音效、礦物辭典*

`<namespace>/sounds/*.json`

檔案名稱由你決定，只讀取資料夾，而且多個檔案會疊加。

原版的 `sounds.json` 格式，所以資源包可以自帶音訊。

### 礦物辭典

*材料、分頁、音效、礦物辭典*

`<namespace>/oredict/*.json`

檔案名稱由你決定，只讀取資料夾，而且多個檔案會疊加。

為已存在的物品加上礦物辭典名稱。每個鍵是一個礦物辭典名稱，其值是登錄在該名稱下的物品，因此檔案沒有自己固定的鍵。資源包自己的方塊與物品則改在變體的 `oreDict` 中指定它們的名稱。

以 `-` 開頭的鍵表示移除：`"-ingotCopper": ["thermalfoundation:material:128"]` 會把該物品從該名稱上拿掉，而 `["*"]` 會清空該名稱。使用該名稱的配方會立刻不再比對到該物品，這正是目的所在。沒有任何東西登錄的名稱會被拒絕並回報錯誤，名稱底下沒有的物品也一樣。以所有中繼資料登錄的項目（原版對 `plankWood` 就是這樣登錄）會整個被移除，不論你指定哪個中繼資料，日誌會說明；用 `:*` 指定就是直白地表達同一件事。

```json
{
  "_note": "ruby equivalents",
  "gemRuby": ["mypack:ruby", "mypack:polished_ruby:1"],
  "oreRuby": ["mypack:ruby_ore", "minecraft:redstone_ore"]
}
```

| 鍵                | 必填 | 值           | 預設 | 作用                                                        |
| ----------------- | ---- | ------------ | ---- | ----------------------------------------------------------- |
| 礦物辭典名稱      | 是   | 物品名稱清單 |      | 登錄在該名稱下的物品。中繼資料寫成第三段，`"mypack:ruby:1"` |
| 以 `_` 開頭的名稱 | 否   | 任意值       |      | 會被略過，所以檔案可以給自己留備註                          |

## 屬性覆寫

*方塊與物品*

`<namespace>/overrides/<target>/<name>.json`

路徑指定目標：`overrides/` 之後的一切，就是被更改的方塊、物品或藥水類型的命名空間與名稱。

其他地方，資源包不是取代檔案就是新增檔案。覆寫兩者皆非：它會更改已存在的方塊、物品或藥水類型的屬性，不論是原版還是模組的，而不碰它的任何檔案。路徑指定目標，所以 `overrides/minecraft/stone.json` 會更改 `minecraft:stone`，`overrides/tconstruct/<name>.json` 則以同樣方式更改該模組的方塊。

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

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

### 方塊屬性

*屬性覆寫*

每個鍵都是選填，檔案只更改它所指定的，所以位於 `overrides/minecraft/stone.json`、只含 `hardness`、`light` 與 `soundType` 的檔案，會讓石頭幾乎瞬間挖完、會發光，而且聽起來像玻璃。一個檔案可以同時帶有方塊、物品與藥水的鍵。目標是方塊時，以下鍵適用：

| 鍵             | 值           | 作用                                                     |
| -------------- | ------------ | -------------------------------------------------------- |
| `hardness`     | 浮點數       | 挖掘時間，與方塊定義所用的數值相同                       |
| `resistance`   | 浮點數       | 爆炸抗性                                                 |
| `slipperiness` | 浮點數       | `0.6` 是一般地面，`0.98` 是冰                            |
| `light`        | `0` 到 `15`  | 發出的光                                                 |
| `lightOpacity` | `0` 到 `255` | 方塊擋住多少光                                           |
| `soundType`    | 音效類型之一 | 踩踏、放置與破壞的音效                                   |
| `harvestTool`  | 工具類別     | 用什麼挖掘；`harvestToolLevel`（預設 `0`）設定等級       |
| `flammability` | 整數         | 多容易燒盡；`fireSpread`（預設 `5`）則是火多容易蔓延到它 |

### 物品屬性

*屬性覆寫*

目標是物品時，則適用以下：

| 鍵              | 值          | 作用                     |
| --------------- | ----------- | ------------------------ |
| `maxStackSize`  | `1` 到 `64` | 堆疊上限                 |
| `maxDamage`     | 整數        | 耐久度                   |
| `containerItem` | 物品名稱    | 留在合成格中，就像桶一樣 |
| `food`          | 物件        | 讓物品可食用，見下文     |

同時是方塊與物品的名稱（每個可放置方塊的物品都是），可以在一個檔案中同時使用兩組：

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

在 `overrides/minecraft/planks.json` 中，這會讓木板的破壞速度大約跟泥土一樣快，並讓它們可以食用。`food` 接受 `heal`（`1`）、`saturation`（`0.6`）、`alwaysEdible`（`false`；`true` 允許在飢餓條全滿時食用）與 `effects`，其項目的寫法與藥水類型完全相同。已經是食物的物品會採用新的 `heal`、`saturation` 與 `alwaysEdible`；其上的 `effects` 不受支援，日誌會說明。可食用的物品若會放置方塊，要朝天空瞄準才能食用，因為瞄準方塊會放置它：這是原版的使用順序，不是錯誤。

### 藥水類型效果

*屬性覆寫*

檔案頂層的 `effects` 會整個重寫藥水類型的效果清單：

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

在 `overrides/minecraft/swiftness.json` 中，敏捷藥水現在會給予飄浮。每個項目接受 `potion`（必填）、`duration`（`3600`）、`amplifier`（`0`）、`ambient`（`false`）與 `showParticles`（`true`），與 `potion_types/` 中相同，而且清單不可為空。

### 其他模組、重新載入與限制

*屬性覆寫*

另一個模組擁有的目標，應在 `requires` 中帶上該模組，這樣該模組未安裝時檔案會被靜默略過，而不是被回報為目標不存在：

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

覆寫是即時的。原始值會在第一次更改之前被記住，所以停用資源包並執行 `/rdpl reload` 會讓一切恢復原狀，不必重新啟動；每次進入世界時也一樣。每個目標一個檔案：兩個資源包覆寫同一個東西時，後面那個資源包的檔案會整個取代前面的，日誌會說明。

兩個值得知道的限制。自己的程式碼會計算某個屬性的方塊或物品會忽略其背後的欄位，所以覆寫雖然套用，卻不會改變任何東西；原版只有階梯的爆炸抗性這樣做，但模組可以在任何地方這樣做。此外，改為可食用的物品只對沒有自己右鍵行為的物品有效：已經在使用時會做某件事的物品，會繼續做那件事。

覆寫需要資源包同時放在用戶端與伺服器上，因為挖掘速度、光與食用都發生在玩家的畫面上，所以不適用於伺服端資源包。`content` 設定類別中的 `overrides` 可以完全關閉該資料夾。

## 硬度群組

*方塊與物品*

`<namespace>/hardness/*.json`

檔案的路徑只用來在日誌中稱呼該群組，沒有其他東西會讀取，所以多個檔案可以疊加。

為一組方塊提供挖掘時間倍率，依每個方塊的位置隨機取值。方塊本身不會被更改：不登錄任何東西，不往世界寫入任何東西，沒有資源包就開啟的世界就是一般的原版。

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

### 挖掘與爆破

*硬度群組*

| 鍵                | 必填 | 值                               | 預設   | 作用                                                   |
| ----------------- | ---- | -------------------------------- | ------ | ------------------------------------------------------ |
| `blocks`          | 是   | 方塊名稱或物件的清單             |        | 該群組。與世界生成 `replace` 的三種形式相同            |
| `except`          | 否   | 方塊名稱或物件的清單             | 無     | 從群組中取回，不論 `blocks` 怎麼寫                     |
| `miningTime`      | 否   | 數字，或含 `min` 與 `max` 的物件 | `1.0`  | 方塊破壞所需的時間是幾倍，對玩家與對 `digs` 生物都一樣 |
| `blastResistance` | 否   | 數字，或含 `min` 與 `max` 的物件 | `1.0`  | 乘上方塊的爆炸抗性                                     |
| `buckets`         | 否   | 1 到 256                         | `10`   | 範圍被分成多少步                                       |
| `minHeight`       | 否   | 整數                             | `0`    | 低於此高度時，隨機值固定為最難的那一級                 |
| `maxHeight`       | 否   | 整數                             | `255`  | 高於此高度時，隨機值固定為最難的那一級                 |
| `field`           | 否   | 物件                             | 見下文 | 隨機結果聚成的形狀                                     |
| `requires`        | 否   | 模組 id 或資源包命名空間清單     | 無     | 除非全部存在，否則略過此檔案                           |

單一數字會讓群組中的每個方塊有相同倍率，不做任何隨機取值。`min` 與 `max` 則依位置隨機取值：分佈場為空處是 `max`，團塊中央是 `min`，兩者之間的步數由 `buckets` 決定。

### 冒險挖掘與解鎖

*硬度群組*

| 鍵            | 必填 | 值               | 預設    | 作用                                                                                                                                                                                                                                                                                                                 |
| ------------- | ---- | ---------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `keeps`       | 否   | 布林值           | `false` | 挖掉後方塊仍留在原處：掉落物、經驗值、工具磨損與破壞音效都照常發生，方塊仍在原地可以再次挖掘，因此該群組是一條不會枯竭的礦脈，速度由 `miningTime` 決定。創造模式照常能移除它                                                                                                                                         |
| `adventure`   | 否   | 物件             | 無      | 誰可以在冒險模式下破壞該群組，那裡原本什麼都無法破壞。`tools` 列出手上必須持有其中之一的物品，留空則持有任何東西都可以；`teams`、`players` 與 `entities` 指定誰，隊伍用名稱、玩家用名字、生物則用實體 id（供 `digs` 任務使用），三者都留空表示持有該工具的任何人。生存與創造模式不受影響                             |
| `advancement` | 否   | `namespace:path` | 無      | 玩家必須先取得該進度，此群組才對其生效。兩個群組可以指定同一個方塊，一個有進度條件、一個沒有，已解鎖的那個優先；沒有該進度的玩家會得到一般群組，若沒有則是原版。生物沒有進度，所以有進度條件的群組永遠不會用於 `digs` 任務，而爆炸抗性與材質的隨機結果不屬於任何玩家，取自一般群組                                   |
| `becomes`     | 否   | 物件             | 無      | 只要有任何玩家取得 `advancement`，該群組的方塊就會在全世界變成另一種方塊：`{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`。所有已載入的區塊會立刻被掃過，之後載入的區塊會在載入時被掃過，之後生成的區塊則在其礦石放置後立刻被掃過，所以舊方塊會徹底消失。給新方塊自己的群組可以改變它的挖掘方式 |

### 分佈場

*硬度群組*

隨機取值並不是完全獨立地對每個方塊進行，否則硬與軟會變成純粹的雜訊，沒有任何形狀。`field` 決定它採取什麼形狀，而 `type` 在兩種達成方式之間選擇。

```json
{
  "field": { "type": "speckle" }
}
```

| 鍵     | 必填 | 值                    | 預設      | 作用                   |
| ------ | ---- | --------------------- | --------- | ---------------------- |
| `type` | 否   | `speckle` 或 `seeded` | `speckle` | 使用下面兩者中的哪一種 |

#### speckle

*分佈場*

每個方塊各自抽取自己的步數，相隔一個面的方塊可以把較弱的一步傳給它。這會產生密集細碎的斑點，大多是單一方塊，在它們相接處偶爾有較大的一片。它是兩者中比較接近它所借鏡的那個模組中挖掘手感的。

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| 鍵        | 必填 | 值                     | 預設                                   | 作用                                                       |
| --------- | ---- | ---------------------- | -------------------------------------- | ---------------------------------------------------------- |
| `chances` | 否   | 整數清單，單位為千分率 | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | 方塊從每一步開始的頻率，最軟的排最後。剩下的都是最硬的一步 |
| `spread`  | 否   | 0.0 到 1.0             | `0.15`                                 | 一步傳給相鄰方塊的頻率，弱一步或三步                       |

清單是由最軟往前讀，所以最後一項是最軟的一步，第一項是比最硬高一級的。上面的數字加起來是千分之 190，所以大約十個方塊中有八個不會抽到自己的步數，而從最硬的一步開始。`spread` 接著會抬高其中一些，這樣留在最硬一步的略多於十分之七，其餘分散在較軟的各步之中。

#### seeded

*分佈場*

種子位於由世界與位置算出的格點上，方塊的步數取決於它離最近的種子有多近。這會產生較少、較大、較圓的團塊，彼此相連，還能長出分支。

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

| 鍵         | 必填 | 值               | 預設  | 作用                       |
| ---------- | ---- | ---------------- | ----- | -------------------------- |
| `cell`     | 否   | 整數，單位為格   | `8`   | 種子相隔多遠               |
| `seeds`    | 否   | 1 到 4           | `1`   | 每個儲存格中的種子數       |
| `reach`    | 否   | 浮點數，單位為格 | `3.0` | 種子的影響範圍能延伸多遠   |
| `arms`     | 否   | 0 到 6           | `0`   | 從每個種子向外放射的分支數 |
| `armReach` | 否   | 浮點數，單位為格 | `0.0` | 分支能延伸多遠             |

省略 `arms` 時團塊是圓的。給種子加上分支會讓它變成帶有觸鬚的節，相鄰節的分支會互相伸向對方，那就成了礦脈而不是團塊。請讓 `reach` 大於 `cell` 的一半，否則團塊無法相接，你只會得到彼此分開、中間什麼也沒有的球。

### 顯示方式

*硬度群組*

倍率本身是看不見的。要讓玩家看出哪些方塊很硬，請給方塊一個方塊狀態，每個分段一個變體，權重都相同，由最硬的開始列出：

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

Minecraft 本來就會依方塊的位置挑選變體，硬度群組則改為把分段交給它，所以材質與倍率永遠一致。

有三件事必須正確，而且錯的時候沒有一件會自己出聲。

**剛好 `buckets` 個項目，權重全都相同。** 分段被當作清單中的位置使用，所以長度不同的清單，或權重不同的清單，會悄悄指向錯誤的材質。

**模型名稱前面不要加 `block/`。** 方塊狀態會自己加上 `block/`，所以 `"model": "mypack:step_stone"` 讀取的是 `models/block/step_stone.json`。寫成 `mypack:block/step_stone` 會去找 `models/block/block/step_stone.json`，那個檔案不存在，該項目就會一聲不響地被丟棄。

**與遊戲查詢相同的鍵。** 方塊狀態檔裡的鍵不一定和方塊屬性的寫法一致。原版石頭把一切都放在 `normal` 之下，而不是 `variant=stone`，所以只寫 `variant=stone` 的覆寫會被合併進去，之後卻從來不會被查看。兩個鍵都寫是安全的，因為合併是逐鍵進行，而且資源包的優先順序高於先前的內容。

開啟 `worldgenDebug` 後，進入世界時會對照烘焙後的模型檢查每個硬度群組，並指出方塊狀態、有多少變體留下、每個變體最後採用什麼材質，以及遊戲為此合併了哪些資源包。這是找出以上三種問題最快的方法，當覆寫共用方塊狀態改變了群組從未指定的狀態時，它也會發出警告。

### 它不涉及的範圍

*硬度群組*

只有玩家自己的挖掘會被改變。破壞方塊的機器直接讀取方塊的硬度，不受影響。玩家放置的方塊和其他方塊一樣隨機取值，因為隨機結果綁定的是位置而不是方塊，被搬到別處的方塊會改用新位置所決定的數值。

---

# 合成、戰利品與交易

## 停用的方塊與物品

*合成、戰利品與交易*

`<namespace>/disabled/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

讓方塊和物品退出遊戲，但不取消註冊，所以世界會保留它們的 id，刪除該檔案就能讓一切恢復。原版、模組與資源包的內容一視同仁，資源包自己的方塊和物品也包括在內，被停用的方塊會連帶停用它的物品，被停用的物品也會連帶停用它的方塊。

```json
{
  "requires": ["thermalfoundation"],
  "names": ["thermalfoundation:ore", "thermalfoundation:material:128", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "oreDict": ["oreTin"]
}
```

| 鍵           | 必填 | 值                   | 預設 | 作用                                                                                                                                                              |
| ------------ | ---- | -------------------- | ---- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `names`      | 否   | 方塊與物品名稱的清單 | 無   | 要停用的內容。中繼資料作為第三部分，`"thermalfoundation:material:128"` 只停用那一個物品，`:*` 則涵蓋所有中繼資料。以 `*` 結尾的名稱會比對所有以其餘部分開頭的名稱 |
| `namespaces` | 否   | 模組 id 的清單       | 無   | 該模組的每個方塊和物品                                                                                                                                            |
| `oreDict`    | 否   | 礦物辭典名稱的清單   | 無   | 登錄在該名稱下的每個物品，且該名稱會被清空                                                                                                                        |
| `requires`   | 否   | 模組 id 的清單       | 無   | 除非每一個模組都已載入，否則會略過該檔案。`config:` 和 `file:` 項目的作用與其他地方相同                                                                           |

被停用的方塊或物品：

- 會從所有創造模式分頁和搜尋分頁中消失，並在 JEI 和 HEI 中隱藏
- 沒有製作它或使用它的配方：以它為產物的每個合成配方都會移除，有欄位只能放入它的合成配方也一樣，還有每個熔煉它或熔煉出它的熔爐配方。如果欄位也接受其他東西，則保留該配方，而礦物辭典欄位則會隨該名稱一併失去這個配方
- 會從每個礦物辭典名稱中移除
- 會從每一次戰利品抽取中剔除，箱子、生物和釣魚都包括在內，方塊掉落物與村民交易也一樣，掉落的該物品堆疊會消失
- 無法放置、使用、揮動或撿起，玩家嘗試時手中的堆疊會被刪除
- 無論該物品的堆疊出現在哪裡都會被刪除：玩家的物品欄和終界箱在登入時以及之後每秒刪除，玩家打開任何容器時從該容器刪除，箱子和其他物品欄則在其區塊載入時刪除
- 會從已放置的世界中移除：隨著區塊載入，它的每個方塊都會變成空氣，其方塊實體也一併消失

若要把已放置的方塊改換成其他東西而非直接移除，請在世界範本中為它們加上一行 `blockReplacements`，例如 `thermalfoundation:ore=minecraft:stone`，參見[取代](#取代)。取代程序所替換的方塊由它負責處理。其他模組保留在自己機器內部的配方屬於該模組，不會受到影響。這些檔案只會在啟動時讀取一次，設定中的 `content.disabled` 會關閉該資料夾，這需要重新啟動。

若要清空某個礦物辭典名稱，但讓它的物品仍留在遊戲中，請改在[礦物辭典](#礦物辭典)檔案中使用 `"-name": ["*"]`。

## 熔爐配方與燃料

*合成、戰利品與交易*

`<namespace>/furnace/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

新增和移除熔煉配方。

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

`add` 底下的項目：

| 鍵           | 必填 | 值       | 預設  | 作用                           |
| ------------ | ---- | -------- | ----- | ------------------------------ |
| `input`      | 是   | 物品名稱 | 無    | 放入什麼                       |
| `output`     | 是   | 物品名稱 | 無    | 產出什麼                       |
| `count`      | 否   | 整數     | `1`   | 產出多少個                     |
| `experience` | 否   | 數字     | `0.0` | 每次熔煉的經驗值。鐵礦石給 0.7 |

如果某個新增項目的輸入已經能被熔煉，該項目會被忽略，日誌會指出該輸入目前熔煉成什麼；要取代它，請在同一個檔案中移除那個配方。

`remove` 底下的項目可以是單純的物品名稱，這會移除所有產出它的配方，也可以是指明 `input`、`result` 或兩者以縮小範圍的物件。兩者都沒有指明的移除項會被略過，日誌會說明這一點。

`<namespace>/fuels/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "oreDict": "gemRuby", "burnTime": 800 }
  ]
}
```

| 鍵         | 必填   | 值             | 預設 | 作用                    |
| ---------- | ------ | -------------- | ---- | ----------------------- |
| `item`     | 二擇一 | 物品名稱       | 無   | 會燃燒的物品            |
| `oreDict`  | 二擇一 | 礦物辭典名稱   | 無   | 該名稱下的一切都會燃燒  |
| `burnTime` | 是     | 整數，單位為刻 | `0`  | 煤炭是 1600，木板是 300 |

## 配方鎖

*合成、戰利品與交易*

`<namespace>/recipe_locks/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。在玩家取得進度之前，讓合成配方保持鎖定。

```json
{
  "advancement": "mypack:deep_miner",
  "team": "miners",
  "objective": "kills",
  "scoreAtLeast": 5,
  "names": ["minecraft:diamond_pickaxe", "thermalfoundation:*"],
  "namespaces": ["tconstruct"],
  "outputs": ["minecraft:diamond_sword"]
}
```

| 鍵                          | 必填 | 值               | 預設 | 作用                                                                                                                                                                     |
| --------------------------- | ---- | ---------------- | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `advancement`               | 否   | `namespace:path` | 無   | 玩家合成這些配方所需的進度。一個鎖需要它或一項身分條件（`team`，或 `objective` 加 `scoreAtLeast`）；兩者都設定時，玩家需要同時滿足。兩者都沒有的檔案會被略過，日誌會註明 |
| `names`                     | 否   | 清單             | 無   | 要攔下的配方名稱。結尾的 `*` 會比對所有以該文字開頭的名稱，例如 `thermalfoundation:*`                                                                                    |
| `namespaces`                | 否   | 清單             | 無   | 這些命名空間的所有配方                                                                                                                                                   |
| `outputs`                   | 否   | 物品名稱清單     | 無   | 合成其中任一物品的所有配方                                                                                                                                               |
| `team`                      | 否   | 文字             | 無   | 玩家必須所在的計分板隊伍                                                                                                                                                 |
| `objective`、`scoreAtLeast` | 否   | 文字，整數       | 無   | 玩家在該計分項上的分數至少要達到此值。兩者都需要                                                                                                                         |

沒有該進度的玩家，在符合的配方上會看到空的結果格，工作台、物品欄的 2x2 合成格以及配方書填入合成格時都是如此；擁有該進度的玩家照常合成。熔爐、鐵砧和自動合成器不受影響。多個檔案會疊加，同時符合兩個檔案的配方需要兩個進度都具備。沒有選中任何配方的檔案會被略過，日誌會註明。

## 工具提示

*物品、方塊與附魔*

`<namespace>/tooltips/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。如果已載入的語言檔中有該鍵，每一行就是語言鍵，否則視為純文字。物品的行加在最後，顯示為灰色；附魔的行緊接在附魔名稱下方，附魔書和附魔物品都一樣。設定中的 `content.tooltips` 可關閉這個資料夾。

```json
{
  "items": [
    { "item": "mypack:ruby:1", "lines": ["tooltip.mypack.ruby", "Found deep underground."] }
  ],
  "enchantments": [
    { "enchantment": "minecraft:sharpness", "lines": ["Extra damage to every mob."] }
  ]
}
```

| 鍵             | 必填 | 值       | 預設 | 作用                 |
| -------------- | ---- | -------- | ---- | -------------------- |
| `items`        | 否   | 物件清單 | 無   | 加到物品和方塊的行   |
| `enchantments` | 否   | 物件清單 | 無   | 加在附魔名稱下方的行 |

`items` 或 `enchantments` 中的每個項目接受以下鍵。

| 鍵            | 必填 | 值       | 預設 | 作用                                                                 |
| ------------- | ---- | -------- | ---- | -------------------------------------------------------------------- |
| `item`        | 是   | 物品名稱 | 無   | 物品，或方塊對應的物品。名稱後加 `:meta` 選擇一個變體，`:*` 選擇全部 |
| `enchantment` | 是   | 附魔名稱 | 無   | 獲得這些行的附魔                                                     |
| `lines`       | 是   | 文字清單 | 無   | 語言鍵或純文字，依序顯示                                             |

## 語言閘門

*取決於條件的語言條目*

`<namespace>/langgates/<name>/gate.json`

語言閘門是一組語言條目，只有在其 `requires` 成立期間，資源包才會載入它們。資源包可以藉此提供替代名稱，條件可以是資源包選項、已安裝的模組或某個檔案。條目放在 `<namespace>/langgates/<name>/<lang>.lang`，寫法與 `lang/` 中的檔案相同，只寫需要變動的條目；同一資料夾裡的 `gate.json` 決定它們何時生效。資料夾名稱隨你取。

假設某個資源包有選項 `stupidNames`，希望它開啟時把 `Block A` 顯示為 `Stupid Block!`：

    mypack/langgates/stupid_names/gate.json
    mypack/langgates/stupid_names/en_us.lang

```json
{
  "requires": ["config:stupidNames"]
}
```

該資料夾裡的 `en_us.lang` 寫入變動後的條目：

    tile.mypack:block_a.name=Stupid Block!

| 鍵         | 必填 | 值       | 預設     | 作用                                                                                                   |
| ---------- | ---- | -------- | -------- | ------------------------------------------------------------------------------------------------------ |
| `requires` | 否   | 文字清單 | 一律生效 | 閘門要生效，每一項都必須成立。寫法與任何定義裡的相同：模組 id 或資源包的命名空間、`config:` 和 `file:` |

- 檔名就是語言代碼，所以 `de_de.lang` 只會變動德語。只有語言檔而沒有 `gate.json` 的資料夾會被忽略，並顯示警告。
- 閘門成立期間，它的條目會疊在同一命名空間的 `lang/<lang>.lang` 之上，同一個鍵以閘門的條目為準。對於資源包本身沒有檔案的語言，閘門成立期間會補上該語言的條目。閘門不成立時，基礎檔案照原樣讀取。
- 同一個閘門的 `requires` 裡每一項都必須成立。想表達「或者」，就寫兩個資料夾。
- 閘門依資源包的順序生效，再依資料夾名稱排序，後面的會蓋過同一個鍵。不論基礎檔案在哪個資源包裡，閘門都會變動它；但帶 `O` 標記的資源包裡的語言檔，仍然優先於一般資源包裡的閘門，請見[資源包：誰優先](#資源包誰優先)。
- 原版或其他模組的鍵放在該命名空間下的閘門裡，例如 `minecraft/langgates/<name>/en_us.lang`。它位於模組自帶檔案之上，因此也會變動模組的名稱。
- `requires` 裡的模組 id 在這裡和別處一樣是硬性相依。如果文字只應在安裝了某樣東西時出現，請改用 `file:` 條目或資源包的命名空間。
- 語言檔在載入資源時讀取，所以依賴模組或檔案的閘門在那時判定，按 F3+T 會重新讀取。對於 `config:` 選項，在資源包選項畫面按下「完成」後，語言檔會立即重新載入，不會出現重新啟動的提示。
- 語言只在用戶端讀取，因此專用伺服器會忽略這個資料夾。為 1.20.1、1.21.1 或 26.x 撰寫的資源包中的 `<lang>.json` 會像 `lang/` 裡的檔案一樣轉回 `.lang`；為 1.12.2 撰寫的資源包只使用 `.lang`。

## 藥水、藥水類型與釀造

*合成、戰利品與交易*

`<namespace>/potions/*.json`

檔案的路徑就是效果的註冊名稱，所以 `mypack/potions/ruby_sight.json` 會註冊 `mypack:ruby_sight`，藥水類型隨後會引用它。

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

| 鍵              | 必填 | 值                   | 預設                                    | 作用                                                       |
| --------------- | ---- | -------------------- | --------------------------------------- | ---------------------------------------------------------- |
| `name`          | 否   | 翻譯鍵               | `effect.<namespace>.<name>`             | 玩家看到的內容                                             |
| `color`         | 否   | 十六進位顏色         | `FFFFFF`                                | 粒子顏色                                                   |
| `badEffect`     | 否   | 布林值               | `false`                                 | 算作有害效果，所以發酵蜘蛛眼會將其反轉                     |
| `beneficial`    | 否   | 布林值               | `false`                                 | 顯示為增益效果                                             |
| `instant`       | 否   | 布林值               | `false`                                 | 一次性生效，而非持續生效                                   |
| `effectiveness` | 否   | 浮點數               | `0.5`                                   | 會被讀取並儲存，但沒有任何作用：原版從不讀取它             |
| `icon`          | 否   | 含 `x` 和 `y` 的物件 | `0`、`0`                                | 圖示在圖集中的位置                                         |
| `iconTexture`   | 否   | 材質路徑             | RDPL 圖示，設定了 `icon` 時則是原版圖集 | 你自己的 18 乘 18 圖示                                     |
| `attributes`    | 否   | 物件清單             | 無                                      | `attribute`、`uuid`、`amount`（`0.0`）、`operation`（`0`） |

### 藥水類型

*藥水、藥水類型與釀造*

`<namespace>/potion_types/*.json`

檔案的路徑就是藥水類型的註冊名稱，`potion_bottle` 物品隨後會在 `potionTypes` 中引用它。

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| 鍵         | 必填 | 值       | 預設           | 作用                 |
| ---------- | ---- | -------- | -------------- | -------------------- |
| `baseName` | 否   | 字串     | 命名空間和名稱 | 藥水瓶據以建立的名稱 |
| `effects`  | 是   | 物件清單 |                | 見下文               |

每個效果接受 `potion`（必填）、`duration`（`3600`）、`amplifier`（`0`）、`ambient`（`false`）和 `showParticles`（`true`）。

### 釀造

*藥水、藥水類型與釀造*

`<namespace>/brewing/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

每個項目要麼是 `input`、`ingredient` 和 `output`，把一種物品釀成另一種；要麼是 `from`、`ingredient` 和 `to`，把一種藥水類型變成另一種。無論哪一種，`ingredient` 都是必填，項目也接受 `requires`，這樣就能略過某一個配方，而不必略過整個檔案。

## 附魔

*合成、戰利品與交易*

`<namespace>/enchantments/*.json`

檔案的路徑就是附魔的註冊名稱，所以 `mypack/enchantments/venom_edge.json` 會註冊 `mypack:venom_edge`。它可以附在 `category` 所屬的物品上，以及 `items` 中列出的每個物品上；和其他附魔一樣，附魔台會提供它，戰利品會帶有它，村民也會拿它交易。

```json
{
  "name": "enchantment.mypack.venom_edge",
  "maxLevel": 3,
  "rarity": "uncommon",
  "slots": ["mainhand"],
  "category": "weapon",
  "items": ["mypack:ruby_blade"],
  "treasure": false,
  "minCost": { "base": 10, "perLevel": 10 },
  "maxCost": { "base": 60, "perLevel": 10 },
  "effects": [
    { "type": "apply_effect", "on": "attack", "potion": "minecraft:wither", "duration": { "base": 100, "perLevel": 60 }, "amplifier": 0 },
    { "type": "damage", "mob": "undead", "amount": { "base": 1.0, "perLevel": 0.5 } }
  ],
  "requires": ["mypack"]
}
```

| 鍵         | 必填 | 值                                                                                                                                  | 預設                                          | 作用                                                       |
| ---------- | ---- | ----------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------- | ---------------------------------------------------------- |
| `name`     | 否   | 翻譯鍵                                                                                                                              | `enchantment.<namespace>.<name>`              | 玩家看到的名稱                                             |
| `maxLevel` | 否   | 1 到 255 的整數                                                                                                                     | `1`                                           | 最高等級。超出範圍的值會被限制在範圍內，並輸出一行錯誤     |
| `rarity`   | 否   | `common`、`uncommon`、`rare`、`very_rare`                                                                                           | `common`                                      | 附魔台和戰利品選中它的頻率：權重為 10、5、2 或 1           |
| `slots`    | 否   | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet` 的清單                                                                       | `["mainhand"]`                                | 物品必須手持或穿戴在哪個位置，附魔才會生效                 |
| `category` | 否   | `armor`、`armor_head`、`armor_chest`、`armor_legs`、`armor_feet`、`weapon`、`digger`、`fishing_rod`、`breakable`、`bow`、`wearable` | 無                                            | 可以附上它的物品種類。`weapon` 是劍，`digger` 是斧、鎬和鏟 |
| `items`    | 否   | 物品 ID 的清單                                                                                                                      | 無                                            | 另外可以附上它的物品；沒有 `category` 時，只有這些物品可以 |
| `treasure` | 否   | 布林值                                                                                                                              | `false`                                       | 只能在戰利品或交易中找到，附魔台從不提供                   |
| `minCost`  | 否   | 含 `base` 和 `perLevel` 的物件                                                                                                      | `11` 和 `10`                                  | 該等級所需的附魔強度                                       |
| `maxCost`  | 否   | 含 `base` 和 `perLevel` 的物件                                                                                                      | `minCost` 的 `base` 加 5，以及它的 `perLevel` | 該等級仍會出現的最大附魔強度                               |
| `effects`  | 否   | 物件清單                                                                                                                            | 無                                            | 附魔的作用，見下文                                         |
| `requires` | 否   | 模組 ID 的清單                                                                                                                      | 無                                            | 列出的模組缺少任何一個時，跳過該檔案                       |

既沒有 `category` 也沒有 `items` 的檔案會被跳過，並在日誌中輸出一行錯誤；沒有可用欄位的檔案同樣如此。`items` 中的 ID 即使沒有任何已安裝模組提供，也沒有影響。費用依 `base + perLevel * (level - 1)` 計算。

`effects` 的每一項都有一個 `type`：

| 類型           | 鍵                                      | 作用                                                                                                                                                                           |
| -------------- | --------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `protection`   | `from`、`amount`                        | 和原版的保護類附魔一樣，依穿戴護甲的保護點數計算。`from` 列出 `all`、`fire`、`fall`、`explosion` 和 `projectile`（省略時為 `all`，不包含無視保護的傷害）                       |
| `damage`       | `mob`、`amount`                         | 武器造成的額外傷害，針對 `any`（預設）、`undead`、`arthropod` 或 `illager` 生物                                                                                                |
| `apply_effect` | `on`、`potion`、`duration`、`amplifier` | 給予藥水效果。`on` 為 `attack`（被擊中的生物獲得，預設）或 `hurt`（穿戴者被擊中時，攻擊者獲得）。`potion` 必填，`duration` 以刻為單位（`100`），`amplifier` 為 0 到 255（`0`） |

`amount` 和 `duration` 使用與費用相同的 `base` 和 `perLevel` 組合，`protection` 會把兩者無條件捨去為整數點數。位於非護甲欄位的 `protection` 效果，以及指向未知藥水的 `apply_effect`，都會被跳過並輸出一行錯誤。

附魔註冊表會隨世界一起儲存，和自訂藥水一樣。移除資源包後，伺服器會經過 Forge 的缺失項目提示：它要求確認，並先替世界做一份備份；專用伺服器會一直等待，除非用 `-Dfml.queryResult=confirm` 啟動。ID 會保持預留，不會被其他附魔佔用，物品會在標籤中保留它，資源包缺失期間原版會忽略它，把資源包放回後它就恢復了。

## 鐵砧操作

*合成、戰利品與交易*

`<namespace>/anvils/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。每個檔案對應一項操作。

將指定物品放入鐵砧左側欄位，並把它的 `with` 物品放入右側欄位，鐵砧便會交還附有所列附魔的左側物品，或交還其 `result`，所需等級即所列等級；除非數量要求更多，否則兩邊各消耗一個，任一堆疊中剩餘的部分仍留在鐵砧中。取出成品時還可以獲得一項進度，並且在獲得該進度之前，該物品可以被限制使用：一把必須經過加工才能揮動的劍。

```json
{
  "item": "minecraft:iron_sword",
  "with": "minecraft:wooden_sword",
  "levels": 3,
  "enchantments": { "minecraft:sharpness": 2 },
  "result": { "item": "minecraft:diamond_sword", "count": 1 },
  "grants": "mypack:sword_rite",
  "locks": true
}
```

| 鍵             | 必填 | 值                                 | 預設     | 作用                                                                                                                                                                                         |
| -------------- | ---- | ---------------------------------- | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`         | 是   | 物品名稱，或 `{ "item", "count" }` |          | 放入左側欄位的物品，以及一次操作需要多少個，預設為一個；其餘留給下一次。`{ "item": "minecraft:coal", "count": 8 }` 搭配鑽石 `result`，就是八個煤炭換一顆鑽石。中繼資料寫作 `minecraft:dye:4` |
| `with`         | 是   | 物品名稱，或 `{ "item", "count" }` |          | 放入右側欄位的物品，以及消耗多少個，預設為一個：`{ "item": "minecraft:coal", "count": 10 }` 要求至少十個一疊，並消耗十個。鐵砧不會對單獨一件物品作出回應，因此每項操作都是一對物品           |
| `result`       | 否   | 物品名稱，或 `{ "item", "count" }` | 左側物品 | 取代左側物品產出的物品及其數量，預設為一個，並保留左側物品的標籤，因此一把不會損壞的鐵鎬加十個煤炭，可以換回一把不會損壞的鑽石鎬。附魔會加在最終產出的物品上                                 |
| `levels`       | 否   | 整數                               | `1`      | 該操作消耗的經驗等級，最少為 1                                                                                                                                                               |
| `enchantments` | 否   | 附魔名稱對應等級的物件             | 無       | 物品交還時帶有的附魔。已有同等或更高等級的附魔會維持不變，若沒有可提升的內容，鐵砧不會給出任何結果，除非設定了 `grants`                                                                      |
| `grants`       | 否   | `namespace:path`                   | 無       | 取出成品時獲得的進度。請將其放在 `advancements/` 底下，並使用 `impossible` 條件，這樣其他途徑就無法獲得它                                                                                    |
| `locks`        | 否   | 布林值                             | `false`  | 在玩家獲得 `grants` 之前，該物品無法用來攻擊、使用或挖掘；它進入玩家手中時，會告知玩家它在等待什麼。仍然允許把它放進鐵砧，解鎖正是靠這種方式                                                 |

鐵砧本身的修復與合併不受影響：只有當左側是指定物品、右側是其 `with` 時，這項規則才會回應。

帶有 `collectsExperience` 的生物也會在此消耗自己的等級。當它主手持有 `item`、副手持有 `with`，且有足夠的 `levels` 可支付時，它會走向 16 格內的鐵砧，並在 3 格內進行操作：等級會從它自身扣除，就像從玩家身上扣除一樣，`with` 被用掉，鐵砧的磨損方式與玩家使用時相同，成品最終出現在它的主手中。當它走過某項鐵砧操作在 `with` 中指定的掉落物時，會把它撿起放入副手。`grants` 和 `locks` 只與玩家有關，因此生物不會從 `grants` 獲得任何東西，也不受任何鎖定限制。

### 過於昂貴

含有 `tooExpensive` 的檔案會挪動鐵砧放棄的那個點。花費達到這麼多等級或更多的操作會顯示「過於昂貴！」，除創造模式外無法取出；單純改名的花費仍最多比它少一級。它可以單獨放在一個檔案裡，也可以和某項操作放在一起，最後載入的檔案生效。

```json
{
  "tooExpensive": 60
}
```

| 鍵             | 必填 | 值               | 預設 | 作用                             |
| -------------- | ---- | ---------------- | ---- | -------------------------------- |
| `tooExpensive` | 是   | 整數，1 到 32767 | `40` | 鐵砧操作變得過於昂貴時的等級花費 |

### 鐵砧規則

含有 `match` 的檔案會改變鐵砧修復與合併所指物品的方式。省略的鍵維持原版做法，所以只含 `match` 的檔案與原版完全一樣。改名從不受影響：它的一級花費以及物品攜帶的累積懲罰，仍會加在這裡設定的任何花費之上。

```json
{
  "match": "minecraft:iron_sword",
  "repairWith": "minecraft:iron_ingot",
  "repairPerUnit": 0.5,
  "repairCost": 2,
  "combineCost": { "multiplier": 0.5 },
  "penalty": "none"
}
```

| 鍵              | 必填 | 值                                          | 預設               | 作用                                                                                             |
| --------------- | ---- | ------------------------------------------- | ------------------ | ------------------------------------------------------------------------------------------------ |
| `match`         | 是   | 物品名稱、`#` 加礦物辭典名，或 `*`          |                    | 規則針對的左欄物品。物品名稱優先於礦物辭典名，礦物辭典名優先於 `*`；同類之間以最後載入的檔案為準 |
| `repairWith`    | 否   | 物品名稱、`#` 加礦物辭典名，或 `*`          | 物品自身的修復材料 | 從右欄修復它的東西                                                                               |
| `repairPerUnit` | 否   | 大於 0 且不超過 1 的數                      | `0.25`             | 每個修復物品恢復的滿耐久比例                                                                     |
| `repairCost`    | 否   | 1 到 32767 的整數，或 `{ "multiplier": n }` | 原版               | 修復花費的等級。數字取代原版價格，大於 0 且不超過 100 的倍率按比例縮放它，四捨五入且至少為 1     |
| `combineCost`   | 否   | 同 `repairCost`                             | 原版               | 合併兩件該物品時的同類設定                                                                       |
| `bookCost`      | 否   | 同 `repairCost`                             | 原版               | 右欄為附魔書時的同類設定                                                                         |
| `penalty`       | 否   | `"vanilla"`、`"none"` 或 `{ "factor": n }`  | `"vanilla"`        | 累積懲罰每次使用後如何增長。原版將其加倍再加一，`none` 維持不變，0 到 16 的係數將其相乘再加一    |
| `repair`        | 否   | 布林值                                      | `true`             | 為 `false` 時鐵砧不修復它                                                                        |
| `combine`       | 否   | 布林值                                      | `true`             | 為 `false` 時鐵砧不合併兩件它；附魔書仍可使用                                                    |

## 詞綴與插槽

*合成、戰利品與交易*

`<namespace>/affixes/*.json`、`<namespace>/gems/*.json`

檔案名稱由你自行決定，只會讀取這些資料夾，多個檔案會疊加。詞綴檔案指定一個物品及其可隨機獲得的加成，寶石檔案指定一個可嵌入插槽的物品。

符合的物品會在玩家第一次持有時隨機一次，並保留隨機到的結果。寶石在鐵砧中按寶石的等級花費嵌入其插槽。物品不能堆疊。移除資料包後，已隨機出的數值仍留在物品堆上。

將帶有空插槽的已隨機物品放在鐵砧左格、寶石放在右格時，鐵砧會交還物品，寶石嵌入第一個空插槽並加上寶石的加成，同時消耗一顆寶石。沒有空插槽的物品不會得到任何結果，寶石也不會再被取出。物品的提示框按 `name` 顯示每個詞綴，接著顯示每個插槽是空的還是嵌有寶石。

```json
{
  "item": "minecraft:diamond_sword",
  "rolls": { "min": 1, "max": 2 },
  "sockets": { "min": 0, "max": 3 },
  "affixes": [
    { "name": "Keen", "attribute": "generic.attackDamage", "min": 1.0, "max": 3.0, "operation": 0, "slot": "mainhand", "weight": 3 }
  ]
}
```

| 鍵        | 必填 | 值                     | 預設 | 作用                                                             |
| --------- | ---- | ---------------------- | ---- | ---------------------------------------------------------------- |
| `item`    | 是   | 物品名稱，或 `id:meta` |      | 進行隨機的物品。它不能堆疊：可堆疊的物品屬於錯誤，該檔案會被跳過 |
| `rolls`   | 否   | `{ "min", "max" }`     | `1`  | 隨機出多少個不同的詞綴                                           |
| `sockets` | 否   | `{ "min", "max" }`     | `0`  | 隨機出多少個寶石插槽                                             |
| `affixes` | 是   | 物件清單               |      | 可隨機出的內容，每個項目如下。屬性未知的項目會被跳過             |

`affixes` 的每個項目接受：

| 鍵          | 必填 | 值                                                              | 預設  | 作用                                                             |
| ----------- | ---- | --------------------------------------------------------------- | ----- | ---------------------------------------------------------------- |
| `name`      | 否   | 文字，或語言鍵                                                  | 無    | 物品提示框中為它顯示的一行                                       |
| `attribute` | 是   | 屬性名稱                                                        |       | 它改變的內容，例如 `generic.attackDamage` 或 `generic.maxHealth` |
| `min`       | 否   | 數字                                                            | `0`   | 它增加的最小值                                                   |
| `max`       | 否   | 數字                                                            | `min` | 它增加的最大值。兩值相同則得到精確加成，否則在兩者之間隨機       |
| `operation` | 否   | `0`、`1` 或 `2`                                                 | `0`   | `0` 加到屬性上，`1` 乘以其基礎值，`2` 乘以總值                   |
| `slot`      | 否   | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet` 或 `any` | `any` | 物品必須處於哪個欄位，加成才生效                                 |
| `weight`    | 否   | int                                                             | `1`   | 與其他項目相比的機率。同一物品的詞綴互不相同，按權重抽取         |

```json
{
  "item": "minecraft:emerald",
  "name": "Emerald",
  "attribute": "generic.maxHealth",
  "amount": 2.0,
  "operation": 0,
  "slot": "any",
  "levels": 1
}
```

| 鍵          | 必填 | 值                                                              | 預設   | 作用                                                               |
| ----------- | ---- | --------------------------------------------------------------- | ------ | ------------------------------------------------------------------ |
| `item`      | 是   | 物品名稱，或 `id:meta`                                          |        | 寶石，放在鐵砧的右格                                               |
| `name`      | 否   | 文字，或語言鍵                                                  | 該物品 | 寶石嵌入插槽後提示框所顯示的內容                                   |
| `attribute` | 是   | 屬性名稱                                                        |        | 寶石改變的內容                                                     |
| `amount`    | 否   | 數字                                                            | `0`    | 它增加的量                                                         |
| `operation` | 否   | `0`、`1` 或 `2`                                                 | `0`    | 與詞綴相同                                                         |
| `slot`      | 否   | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet` 或 `any` | `any`  | 與詞綴相同                                                         |
| `levels`    | 否   | int                                                             | `1`    | 嵌入寶石花費的經驗等級，最少為 1，且始終低於鐵砧的「過於昂貴」界線 |

## 附魔台

*合成、戰利品與交易*

`<namespace>/enchanting/*.json`

檔名隨你定，只讀取資料夾，多個檔案會疊加。含有 `enchantment` 的檔案塑造一個附魔，含有 `match` 的檔案塑造一種物品，其他檔案則設定整張附魔台。省略的鍵維持原版做法，所以只含 `enchantment` 或 `match` 的檔案什麼也不改變。設定中 `data` 類別的 `enchanting` 會關閉整個資料夾。

### 每個附魔

```json
{
  "enchantment": "minecraft:sharpness",
  "weight": 30,
  "minLevel": 2,
  "maxLevel": 4
}
```

| 鍵            | 必填 | 值             | 預設     | 作用                                                       |
| ------------- | ---- | -------------- | -------- | ---------------------------------------------------------- |
| `enchantment` | 是   | 附魔名稱       |          | 檔案針對哪個附魔                                           |
| `off`         | 否   | 布林值         | `false`  | 為 `true` 時附魔台永遠不提供它；戰利品、交易與鐵砧仍然可以 |
| `weight`      | 否   | 整數，1 或以上 | 自身的值 | 相對其他附魔被選中的頻率；原版的從最稀有的 1 到 10         |
| `minLevel`    | 否   | 整數，1 或以上 | 自身的值 | 附魔台提供的最低等級                                       |
| `maxLevel`    | 否   | 整數，1 或以上 | 自身的值 | 附魔台提供的最高等級，可以超過該附魔自身的最高等級         |

### 每種物品

```json
{
  "match": "minecraft:shears",
  "enchantable": true,
  "enchantability": 10,
  "enchantments": ["minecraft:efficiency", "minecraft:unbreaking"]
}
```

| 鍵               | 必填 | 值                                 | 預設             | 作用                                                                                             |
| ---------------- | ---- | ---------------------------------- | ---------------- | ------------------------------------------------------------------------------------------------ |
| `match`          | 是   | 物品名稱、`#` 加礦物辭典名，或 `*` |                  | 放在附魔台上的物品。物品名稱優先於礦物辭典名，礦物辭典名優先於 `*`；同類之間以最後載入的檔案為準 |
| `enchantable`    | 否   | 布林值                             | 原版             | `true` 讓附魔台接受原版拒絕的物品，`false` 則拒絕原版會接受的物品                                |
| `enchantability` | 否   | 整數，1 或以上                     | 物品自身的值     | 它接受附魔的能力：越高，抽到的附魔越強越多。原版的範圍是 1 到 25；鑽石工具為 10，金工具為 22     |
| `enchantments`   | 否   | 附魔名稱清單                       | 適合該物品的附魔 | 附魔台可以為它抽取的僅有附魔，不論它們原本是否適合該物品                                         |

被設為可附魔卻沒有自身附魔能力的物品，還需要 `enchantability`，否則永遠抽不到任何附魔。

### 整張附魔台

```json
{
  "enchantLevels": [5, 15, 30],
  "enchantCosts": [1, 2, 3],
  "lapisCosts": [1, 2, 3],
  "shelfCap": 20,
  "shelfPower": 1.5
}
```

| 鍵              | 必填 | 值                       | 預設        | 作用                                                                   |
| --------------- | ---- | ------------------------ | ----------- | ---------------------------------------------------------------------- |
| `enchantLevels` | 否   | 3 個整數的清單，1 或以上 | 隨機值      | 每個欄位由上到下所要求的等級，取代隨機得出的等級；其附魔也依該等級抽取 |
| `enchantCosts`  | 否   | 3 個整數的清單，0 或以上 | `[1, 2, 3]` | 每個欄位被選中後拿走的等級                                             |
| `lapisCosts`    | 否   | 3 個整數的清單，1 或以上 | `[1, 2, 3]` | 每個欄位被選中後拿走的青金石，必須放在附魔台中                         |
| `shelfCap`      | 否   | 整數，0 或以上           | `15`        | 最多計入多少個書架                                                     |
| `shelfPower`    | 否   | 0 到 16 的數             | `1`         | 每個書架算作多少，對總數四捨五入                                       |

介面仍在欄位旁及其提示中繪製原版的一、二、三；附魔台檢查與拿走的數量以檔案為準。

## 方塊掉落物

*合成、戰利品與交易*

`<namespace>/block_drops/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

原版 1.12 的方塊沒有戰利品表，因此資源包可以為自己的方塊增加掉落物，卻無法更動石頭、礦石或其他模組的方塊。這項功能可以做到：一條規則指定一個方塊，以及玩家採集它時，在一般掉落物之外或取代一般掉落物的內容。

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

| 鍵            | 必填 | 值               | 預設    | 作用                                                                               |
| ------------- | ---- | ---------------- | ------- | ---------------------------------------------------------------------------------- |
| `block`       | 是   | 方塊 id          |         | 規則所監看的方塊                                                                   |
| `meta`        | 否   | 整數             | `-1`    | 僅限該方塊的這個中繼資料；`-1` 表示所有狀態                                        |
| `replace`     | 否   | 布林值           | `false` | 是否在擲出這些掉落物之前，先捨棄一般掉落物                                         |
| `advancement` | 否   | `namespace:path` | 無      | 規則只對擁有該進度的玩家生效，因此同一個方塊在取得進度之前和之後可以掉落不同的東西 |
| `drops`       | 是   | 掉落物清單       |         | 玩家破壞方塊時，每一項各自獨立擲出                                                 |

每一項掉落物：

| 鍵           | 必填                    | 值                          | 預設     | 作用                                                                                                     |
| ------------ | ----------------------- | --------------------------- | -------- | -------------------------------------------------------------------------------------------------------- |
| `item`       | 是，除非有 `experience` | 物品 id                     |          | 掉落什麼，中繼資料寫作 `minecraft:dye:4`                                                                 |
| `experience` | 否                      | 數字或 `low-high`           |          | 取代物品，以經驗球的形式掉落相應的經驗值，在範圍內均勻擲出。`chance` 和 `silkTouch` 的適用方式與物品相同 |
| `count`      | 否                      | 數字或 `low-high`           | `1`      | 掉落多少，在範圍內均勻擲出                                                                               |
| `chance`     | 否                      | 浮點數                      | `1.0`    | 掉落發生的機率，`0.05` 即破壞二十次出現一次                                                              |
| `fortune`    | 否                      | 整數                        | `0`      | 工具上每一級幸運，最多額外增加這麼多                                                                     |
| `silkTouch`  | 否                      | `either`、`only` 或 `never` | `either` | 掉落是否需要絲綢之觸工具、是否拒絕這類工具，或是不在意                                                   |

規則只處理玩家的採集；爆炸、活塞和生物破壞不會擲出任何東西。同一方塊的多條規則會全部生效，其中任何一條的 `replace` 都會先清除一般掉落物。

## 右鍵收穫

*合成、戰利品與交易*

`<namespace>/harvests/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

右鍵點擊成熟的作物通常沒有任何效果。這裡的檔案會讓所列作物在成熟時可由玩家右鍵收穫：作物掉落平常的戰利品，並立即在原地以年齡 0 重新種下，一次點擊就取代了破壞方塊再補種的兩個步驟。沒有檔案時一切照舊；想把選擇權交給玩家的資源包，可以在 `requires` 中加入 `config:` 項目，做法與「資源包選項」一節相同。

```json
{
  "requires": ["config:enableRightClickHarvest"],
  "crops": ["minecraft:wheat", "minecraft:carrots", "minecraft:potatoes", "minecraft:beetroots", "minecraft:nether_wart"],
  "replant": true,
  "age": 7
}
```

| 鍵         | 必填 | 值           | 預設                   | 作用                                                                                 |
| ---------- | ---- | ------------ | ---------------------- | ------------------------------------------------------------------------------------ |
| `crops`    | 是   | 方塊 ID 清單 |                        | 右鍵即可收穫的作物方塊，原版或資源包的皆可。沒有任何東西註冊的方塊會被略過並寫入日誌 |
| `age`      | 否   | int          | 該方塊允許的最大 `age` | 檔案內所有作物視為成熟的年齡                                                         |
| `replant`  | 否   | boolean      | `true`                 | 作物是原地回到年齡 0，還是掉落後被移除                                               |
| `requires` | 否   | 字串清單     | 無                     | 必須存在的模組 ID、資源包命名空間或 `config:` 選項，與所有定義相同                   |

任何帶有整數屬性 `age` 的方塊都可以列入。未成熟的作物、潛行的玩家和旁觀者仍依一般右鍵處理。掉落與破壞該作物時相同，會計入手持工具的幸運以及「方塊掉落」一節的所有規則；重新種下時，若掉落物中有一顆該作物的種子，就會從中扣除一顆。村民的 `harvestFarmland` 任務是獨立的，不受影響。

## 礦脈連鎖挖掘

*合成、戰利品與交易*

`<namespace>/veins/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

用鎬子挖礦石，通常只會破壞那一個方塊。這裡的檔案會指定一組方塊和能一次挖完它們的工具：生存模式下的玩家用主手中的某個工具破壞組內方塊時，與它相鄰的同類方塊也會由近到遠一併破壞，直到達到上限。沒有檔案時一切照舊；想把選擇權交給玩家的資源包，可以在 `requires` 中加入 `config:` 項目，做法與「資源包選項」一節相同。

```json
{
  "requires": [],
  "blocks": ["minecraft:coal_ore", { "block": "minecraft:log", "meta": 0 }],
  "tools": ["minecraft:iron_pickaxe", "minecraft:diamond_pickaxe"],
  "limit": 16,
  "hunger": 0.5,
  "advancement": "mypack:found_ore"
}
```

| 鍵            | 必填 | 值                                                                               | 預設  | 作用                                                                              |
| ------------- | ---- | -------------------------------------------------------------------------------- | ----- | --------------------------------------------------------------------------------- |
| `blocks`      | 是   | 方塊 ID 清單，寫法與硬度群組相同：1.12.2 用 `meta`，1.20.1 及以後用 `properties` |       | 方塊組。同一條礦脈只含同一種方塊，組內兩種不同的方塊不會連在一起                  |
| `tools`       | 是   | 物品 ID 清單，1.12.2 中為 `modid:item:meta`                                      |       | 第一個方塊被破壞時持在主手中的工具。不存在的物品會被略過並寫入日誌                |
| `limit`       | 否   | int，1 到 256                                                                    | `16`  | 除第一個方塊外最多破壞的方塊數                                                    |
| `hunger`      | 否   | float，0 或更大                                                                  | `0.1` | 每多破壞一個方塊消耗的飢餓點數。1 點相當於 4 點消耗度。第一個方塊照常消耗原版的量 |
| `advancement` | 否   | 文字                                                                             | 無    | 玩家必須擁有的進度，該組才對其生效。不填則對所有人生效                            |
| `requires`    | 否   | 字串清單                                                                         | 無    | 必須存在的模組 ID、資源包命名空間或 `config:` 選項，與所有定義相同                |

相鄰指面、稜或角相接，所以一個方塊有 26 個鄰居。只在生存模式下生效：創造和旁觀模式的玩家仍只破壞一個方塊。工具損壞或玩家飽食度降到 0 時停止。每個額外方塊都會掉落平常的戰利品，會計入手持工具的幸運以及「方塊掉落」一節的所有規則，並讓工具磨損一次。對硬度群組保留的方塊不會生效。這只是伺服器端的資源包資料，客戶端無需任何東西。沒有任何有效方塊或工具的檔案會被忽略並寫入一行錯誤日誌。

## 搬運

*合成、戰利品與交易*

`<namespace>/carries/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

潛行並以空的主手右鍵，會拿起清單中的方塊或生物；潛行時右鍵方塊，會把它放回被點擊的那一面。搬運中的方塊保留其方塊實體資料，搬運中的生物保留其全部實體資料。搬運者以一半速度移動，動作列會顯示正在搬運的東西，所搬之物在死亡和重新連線後仍保存在玩家資料裡。

```json
{
  "requires": ["config:enableCarrying"],
  "blocks": ["minecraft:chest", "minecraft:furnace", { "block": "mypack:crate", "meta": 2 }],
  "mobs": ["minecraft:cow", "minecraft:sheep"]
}
```

| 鍵         | 必填 | 值                       | 預設 | 作用                                                                                                                                                                                       |
| ---------- | ---- | ------------------------ | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `blocks`   | 否   | 方塊 ID 或比對物件的列表 | 無   | 可以搬運的方塊，來自原版或資料包。每個條目的寫法與礦脈連鎖挖掘的 `blocks` 條目相同：一個 ID、`modid:block:meta`，或帶 `block` 和 `meta` 的物件。沒有任何東西註冊的方塊會被跳過並記一行日誌 |
| `mobs`     | 否   | 實體 ID 清單             | 無   | 可以搬運的生物，依實體 ID 指定，原版或資源包中的都可以。沒有任何東西註冊的 ID 會被略過，並在記錄檔中記一行                                                                                 |
| `requires` | 否   | 字串清單                 | 無   | 必須存在的模組 ID、資源包命名空間或 `config:` 選項，與所有定義相同                                                                                                                         |

只能寫 ID，不能用標籤。門、高大的植物和床這類兩格結構無法搬運，受保護的方塊和硬度低於 0 的方塊也不能。拿起生物時，騎乘者會被甩下，拴繩不作特殊處理，搬運者身上不會繪製任何東西。搬運的資料在遊戲升級時不會經過資料修復，方塊或生物已無法解析的搬運狀態會被清除，並在記錄檔中記一行錯誤。帶有儲存介面的資源包生物，在潛行時會被拿起，而不是開啟介面。

## 玩家戰利品

*合成、戰利品與交易*

`<namespace>/player_loot/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

原版 1.12 沒有為玩家提供戰利品表，死亡時只會掉落物品欄，也沒有資源包可以覆寫的表名稱。RDPL 新增了一個，在玩家死亡時擲出：

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| 鍵                    | 必填 | 值                 | 預設    | 作用                                         |
| --------------------- | ---- | ------------------ | ------- | -------------------------------------------- |
| `table`               | 是   | 表名稱             |         | 玩家死亡時擲出的戰利品表                     |
| `mode`                | 否   | `add` 或 `replace` | `add`   | 該表的物品是加入物品欄，還是取代物品欄       |
| `rollOnKeepInventory` | 否   | 布林值             | `false` | 在保留物品欄的死亡中，是否仍要擲出該表       |
| `dropLoose`           | 否   | 布林值             | `false` | 物品是否直接放在地面上，而不是加入死亡掉落物 |

`add` 會在物品欄之外另外掉落該表的物品，適用於擊殺賞金。`replace` 會捨棄物品欄，只掉落該表擲出的內容。

關閉 `rollOnKeepInventory` 時，`keepInventory` 下的死亡（以及一律保留物品欄的旁觀者死亡）不會擲出任何東西。開啟它則能讓保留物品欄的世界中，死亡依然有代價。

多個檔案會疊加，各自獨立判定。只要有任何一個適用的項目是 `replace`，物品欄就會在擲出之前被清空一次，因此與之並存的 `add` 項目仍然會生效。

該表是依名稱查找的一般戰利品表：它可以放在資源包的 `loot_tables/entities/player.json`，可以是任何原版或模組的表，也可以由 `loot_injections` 觸及。戰利品情境：死亡的玩家是被掠奪的實體，擊殺者（如有）是擊殺玩家，傷害來源已設定。`killed_by_player`、`entity_properties`、`random_chance_with_looting`、`looting_enchant` 和 `quality` 都正常運作。

有一個戰利品函式是 RDPL 自有的，可用於任何帶有被掠奪實體的表：`rdpl:killed_name` 會以受害者的名稱為掉落物命名。`format` 決定顯示名稱的形式（`%s` 代表受害者，預設只顯示名稱），而 `tag` 則會把純名稱寫入一個 NBT 字串鍵，供自行讀取它的物品使用。

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**墓碑模組。** 擲出的物品會在任何墓碑模組讀取之前，先加入一般死亡掉落物，因此它們會與其他物品一同進入墓碑（`replace` 則會把該表的內容而非物品欄放入墓碑）。適用於 Gravestone、GraveStone Mod、Corail Tombstone，以及任何依據死亡掉落清單運作的模組。無須任何設定。

`dropLoose` 完全繞過掉落清單：物品直接放置在世界中，因此墓碑模組看不到它們，物品欄進入墓碑，該表的物品則散落在地上，留給擊殺者。適用於屬於擊殺者而非受害者墓碑的戰利品。沒有墓碑模組時，它幾乎沒有差別。注意：這些物品在任何下游環節取消掉落之前就已經存在，因此那些在死亡被取消後不應保留的項目，應當關閉它。

在 `data` 設定類別中將 `playerLoot` 設為 `false`，即可完全關閉該資料夾。

## 飲食多樣性

*合成、戰利品與交易*

`<namespace>/diets/*.json`

檔名隨意，只讀取資料夾，而且只有一個檔案生效。如果有多個，使用最先載入的那個，其餘的會寫入日誌並被忽略。

通常一種食物無論吃多少次，飽足效果都一樣。在這裡放一個檔案後，玩家最近吃過的食物中已有的食物飽足較少，而多樣的飲食飽足較多。沒有這個檔案時，什麼都不會改變，也不會記錄任何內容。

```json
{
  "requires": [],
  "memory": 8,
  "repeat": [1.0, 0.5, 0.25],
  "exempt": ["minecraft:rotten_flesh", "minecraft:spider_eye", "#listAllmeatraw"],
  "variety": [ { "distinct": 4, "multiplier": 1.25, "effects": [ { "potion": "minecraft:regeneration", "duration": 200 } ] },
               { "distinct": 7, "multiplier": 1.5, "effects": [ { "potion": "minecraft:resistance" } ], "runs": "mypack:diet/well_fed" } ]
}
```

| 鍵         | 必填 | 值               | 預設    | 作用                                                                                                                                                                                                |
| ---------- | ---- | ---------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `memory`   | 否   | int ≥ 1          | `10`    | 每位玩家記住的餐數，最早的一餐最先被忘掉                                                                                                                                                            |
| `repeat`   | 否   | float ≥ 0 的清單 | `[1.0]` | 同一種食物第 1 次、第 2 次、第 3 次出現在記住的餐食中時的倍率，最後一項會一直重複                                                                                                                   |
| `variety`  | 否   | 物件清單         | 無      | 每個物件含有 `distinct`（int ≥ 1）和 `multiplier`（float ≥ 0）：記住的餐食中達到這麼多種不同的食物後，恢復量乘以 `multiplier`                                                                       |
| `groups`   | 否   | 字串清單         | 無      | 物品 ID 或 `#ore` 名稱；符合的食物按該項目計算，取第一個符合項                                                                                                                                      |
| `exempt`   | 否   | 文字列表         | 無      | 物品 ID 或 `#ore` 名稱；與某一項相符的食物不受影響：不會被記住，不計入種類，其回復量也不會改變。優先於 `groups`                                                                                     |
| `effects`  | 否   | 物件列表         | 無      | 寫在 `variety` 物件內：只要該檔位是玩家達到的最高檔位，每次用餐後給予的藥水效果。每項含 `potion`、`duration`（單位為 tick，預設 `1200`）、`amplifier`、`ambient` 和 `showParticles`（預設 `false`） |
| `runs`     | 否   | 函數 ID          | 無      | 寫在 `variety` 物件內：當一餐把玩家從下方提升到該檔位時，以該玩家身分執行的函數，每次越過一次                                                                                                       |
| `messages` | 否   | 物件             | —       | 用餐後動作列文字：重複倍率低於 1 顯示 `repeat`，高於 1 顯示 `variety`；文字、語言鍵或 `""`；預設 `rdpl.diet.*`                                                                                      |
| `requires` | 否   | 字串清單         | 無      | 必須存在的模組 ID、資源包命名空間或 `config:` 選項，與所有定義相同                                                                                                                                  |

出現無效值時，日誌中會記錄一條帶有檔名的錯誤，並跳過該檔案。不存在的藥水在給予效果時會被略過。如果餐食從記憶中移出，玩家跌到該檔位以下後又升回，`runs` 會再次執行。`exempt` 中沒有相符任何物品的項目會在日誌中記錄一條帶有檔名和該項目的警告，只略過這一項，檔案的其餘部分照常載入。

凡是讀取隊伍或分數條件的地方，例如卡片的 `when`、閘門或獲得紅心的獎勵，旁邊都可以寫 `dietAtLeast`（int，至少為 1）：只要玩家記住的餐食中至少有這麼多種不同的食物（已套用 `groups`），它就成立。沒有飲食多樣性檔案時它永遠不成立，日誌會記錄一則錯誤。

玩家吃完後，食物實際恢復的飢餓值和飽和度（已計入原版自身的上限）會乘以同一個倍率。這個倍率是 `repeat` 中與該食物已在記住的餐食裡出現的次數對應的一項（一次也沒有時取第 1 項），乘以已達到的最大 `distinct` 所對應的 `variety` 檔位，檔位在檔案中的位置不限。`distinct` 是把清單截到 `memory` 之後，記住的餐食中（包括這一餐）不同食物的種類數。飢餓值的增量按半格四捨五入取整，新的飢餓值保持在 0 到 20 之間。飽和度按同一倍率變化，但不會超過新的飢餓值。在上面的範例中，連續吃的第二個蘋果只恢復一半，記住的第四種不同食物則恢復 1.25 倍。

原版、模組和資料包的所有食物都適用，包括在 `overrides` 中定義的食物。沒有恢復任何數值的食物不會被記住。像蛋糕這樣不是透過使用物品來吃的不在此列。進食記錄按玩家儲存，死亡和重新啟動後仍然保留。

金蘋果這樣的變體物品，每種變體算作不同的食物。

## 村民與交易

*合成、戰利品與交易*

`<namespace>/villagers/*.json`

檔案的路徑就是職業的註冊名稱，因此 `mypack/villagers/jeweller.json` 會註冊 `mypack:jeweller`，隨後交易會在 `profession` 中引用它。

```json
{
  "careers": ["gem_cutter", "appraiser"],
  "texture": "mypack:textures/entity/villager/jeweller.png",
  "zombieTexture": "mypack:textures/entity/zombie_villager/jeweller.png"
}
```

| 鍵              | 必填 | 值       | 預設         | 作用                                         |
| --------------- | ---- | -------- | ------------ | -------------------------------------------- |
| `careers`       | 是   | 名稱清單 | 無           | 該職業提供的專業。沒有任何專業的職業會被拒絕 |
| `texture`       | 否   | 材質路徑 | 原版村民     | 村民的外觀                                   |
| `zombieTexture` | 否   | 材質路徑 | 原版殭屍村民 | 殭屍化之後的外觀                             |

### 交易

*村民與交易*

`<namespace>/trades/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

```json
{
  "trades": [
    {
      "profession": "mypack:jeweller",
      "career": "gem_cutter",
      "level": 1,
      "maxUses": 12,
      "buy": { "item": "minecraft:emerald", "min": 2, "max": 4 },
      "buySecondary": { "item": "minecraft:gold_nugget", "min": 1, "max": 2 },
      "sell": { "item": "mypack:ruby", "min": 1 }
    }
  ]
}
```

| 鍵             | 必填 | 值       | 預設 | 作用                              |
| -------------- | ---- | -------- | ---- | --------------------------------- |
| `profession`   | 是   | 職業名稱 |      | 這是誰的交易                      |
| `career`       | 是   | 專業名稱 |      | 其中的哪個專業                    |
| `level`        | 否   | 整數     | `1`  | 出現在哪個交易等級                |
| `maxUses`      | 否   | 整數     | `12` | 鎖定前可以使用的次數              |
| `buy`          | 是   | 物品堆疊 |      | 村民要求的物品                    |
| `buySecondary` | 否   | 物品堆疊 | 無   | 在 `buy` 之外額外要求的第二件物品 |
| `sell`         | 是   | 物品堆疊 |      | 村民給出的物品                    |

物品堆疊由 `item` 加上 `min`（`1`）和 `max`（預設為 `min`）構成，因此固定價格只需寫 `min`。

交易也可以取決於開啟視窗的玩家。不符合條件的玩家看不到該交易，其他玩家不受影響。多個條件必須同時成立，而且每次開啟視窗時都會檢查。

```json
{
  "trades": [
    {
      "profession": "minecraft:farmer",
      "career": "farmer",
      "level": 1,
      "advancement": "mypack:ready",
      "team": "red",
      "objective": "rep",
      "scoreAtLeast": 5,
      "priceScale": { "objective": "rep", "perPoint": -0.1, "min": 0.5, "max": 2.0 },
      "buy": { "item": "minecraft:clay_ball", "min": 10 },
      "sell": { "item": "minecraft:emerald" }
    }
  ]
}
```

| 鍵                          | 必填 | 值               | 預設值 | 作用                                                                                                                                                                                               |
| --------------------------- | ---- | ---------------- | ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `advancement`               | 否   | `namespace:path` | 無     | 只有獲得此進度的玩家能看到該交易                                                                                                                                                                   |
| `team`                      | 否   | 隊伍名稱         | 無     | 只有此計分板隊伍的成員能看到該交易                                                                                                                                                                 |
| `objective`, `scoreAtLeast` | 否   | 文字，整數       | 無     | 只有在該目標中的分數不低於此值的玩家才能看到該交易。兩者都需要，沒有分數的玩家不符合條件                                                                                                           |
| `priceScale`                | 否   | 物件             | 無     | 將 `buy` 的數量乘以 `clamp(1 + score * perPoint, min, max)` 並四捨五入，最小為 1，最大為堆疊上限。`objective` 和 `perPoint` 必填，`min` 預設為 `0.0`，`max` 預設為 `10.0`，沒有分數的玩家按 0 計算 |

`priceScale` 只改變 `buy`，絕不改變 `buySecondary`。遊戲透過職業和物品（`buy`、`buySecondary` 和 `sell`，包括其中繼資料）把條件交易與村民的其他交易區分開，所以請為它指定該職業其他交易都沒有用過的物品。兩個資料包交易使用相同的物品時，後一個會被跳過並寫入日誌。已經生成交易的村民會保留原有交易。

---

# 生物與危險

## 實體變體

*生物與危險*

`<namespace>/entities/*.json`

檔案的路徑就是變體的註冊名稱，因此 `mypack/entities/angry_cow.json` 會註冊 `mypack:angry_cow`，`becomes`、生怪蛋和世界存檔所引用的都是它。

這裡的檔案會以一個已存在的實體為基礎，製作出一個新實體。它是真正獨立的實體，有自己的註冊名稱、在世界中的名稱和生怪蛋，若你有提供，也會有自己的戰利品表；它沿用另一個實體的行為，而不是取代它。被複製的實體不會有任何改變。

這裡一次列出所有鍵。實際的檔案只需寫用到的那些。

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
  "keepsBaseBaby": false,
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
  "threatLeast": 1,
  "threatHostile": 2,
  "attackReach": 2.0,
  "hurtResistance": 20,
  "stepHeight": 0.6,
  "knockback": 0.4,
  "climbs": false,
  "teleports": true,
  "walks": false,
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
  "throwReturns": false,
  "charges": false,
  "pounces": false,
  "sniffs": 16,
  "fleesWhenHurt": 0.25,
  "sleepsByDay": false,
  "home": 32,
  "patrols": false,
  "swoops": false,
  "gusts": false,
  "gustPower": 1.5,
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

### 識別

*實體變體*

| 鍵              | 必填 | 值                             | 預設    | 作用                                                                                                                                                                          |
| --------------- | ---- | ------------------------------ | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entity`        | 是   | `namespace:name`               | 無      | 作為基礎的實體。任何模組的實體都可以，只要它接受一般的世界建構子                                                                                                              |
| `name`          | 否   | 字串                           | 無      | 它在世界中、死亡訊息裡和生怪蛋上使用的名稱                                                                                                                                    |
| `showName`      | 否   | 布林值                         | `false` | 不必注視就會顯示名稱                                                                                                                                                          |
| `egg`           | 否   | 布林值或物件                   | `true`  | 一顆生怪蛋，顏色與被複製實體的蛋相同。含有 `primary` 和 `secondary` 的物件，例如 `{ "primary": "AABBCC", "secondary": "112233" }`，可指定你自己的顏色，`false` 則不產生生怪蛋 |
| `becomes`       | 否   | 清單                           | 無      | 該變體在生成時可能轉變成的其他變體，依權重決定。見下文                                                                                                                        |
| `dialog`        | 否   | 字串                           | 無      | 以 `namespace:name` 指定對話，對該實體按右鍵時開啟。[對話](#對話)                                                                                                             |
| `baby`          | 否   | 布林值或 0.0 到 1.0            | `false` | 以幼體生成的頻率，且會一直維持幼體。`true` 為一律幼體，數字則為該比例的個體                                                                                                   |
| `keepsBaseBaby` | 否   | 布林值                         | `false` | 是否同時保留基礎實體自身的幼體判定。不啟用時，以殭屍為基礎的變體只會依 `baby` 所述生成幼體，不會有 Forge 的 `zombieBabyChance` 幼體，也不會有小雞騎士                         |
| `profession`    | 否   | `namespace:name`               | 隨機    | 對村民而言，所從事的職業                                                                                                                                                      |
| `career`        | 否   | 整數                           | 隨機    | 該職業中的哪個專業，從 1 起算                                                                                                                                                 |
| `requires`      | 否   | 模組 id 或資源包命名空間的清單 | 無      | 除非全部存在，否則該變體會被略過                                                                                                                                              |

變體是獨立的一個類別，因此包含它的世界會依賴製作它的資源包，就像依賴模組一樣。移除該檔案，世界中的這些生物也會隨之消失。

**用一顆蛋或一個生怪磚產出混合。** 變體是獨立的一個類別，所以它自己一律會生成它所寫的內容。`becomes` 是資源包打破這一點的方式：一份該變體在生成時可能轉變成的變體清單，每個都帶有權重，並針對每隻生物分別決定。

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

寫上自己的名稱，就是維持原樣的方式，而權重就是機率。把它放在 `mypack:walker` 上，一顆蛋和一筆生成項目便會產出大多數的行屍，偶爾夾雜一隻小的，就像殭屍蛋偶爾會給你一隻殭屍寶寶。它發生在生物進入世界的時刻，因此對生怪蛋、`/summon` 和自然生成一律有效，到來的生物是所選變體的真正個體，具備該變體所寫的一切。生怪磚則更嚴格：它只會在與其所設變體具有相同基礎生物和相同隊伍的變體之間擲選，因此殭屍生怪磚只會產出該隊伍的殭屍及其幼體，絕不會出現其他種類或其他隊伍的生物，就像原版的殭屍生怪磚始終是殭屍生怪磚一樣。透過這種方式得到的變體不會再次轉變，因此兩個變體可以互相指名而不會陷入循環。

**`baby` 的用武之地。** 遊戲本身沒有殭屍寶寶這種獨立的生物：只有一種殭屍，在生成時判定自己是否為幼體。`baby` 決定頻率，因此 `"baby": 0.05` 是原版的習慣，`"baby": true` 則是一律幼體。變體不會在此之上再套用殭屍自身的判定，所以不會出現 `baby` 沒有要求的幼體或小雞騎士；`keepsBaseBaby` 會把這項判定還回來。兩者其實是達成同一目的的兩種方式，該選哪一個取決於你想要的差異：單用 `baby` 會得到一個有時是幼體的變體，`becomes` 則會得到幾個在你喜歡的任何方面不同的變體，兩者混用也無妨。

### 外觀

*實體變體*

| 鍵           | 必填 | 值                                     | 預設       | 作用                                                                         |
| ------------ | ---- | -------------------------------------- | ---------- | ---------------------------------------------------------------------------- |
| `texture`    | 否   | `namespace:textures/entity/<file>.png` | 無         | 專屬的外觀貼圖，配置方式與被複製的實體相同                                   |
| `tint`       | 否   | 十六進位顏色                           | 無         | 在繪製時為實體著色                                                           |
| `tintParts`  | 否   | `body`、`armor`、`held` 的清單         | `["body"]` | 著色涉及哪些部位                                                             |
| `scale`      | 否   | 浮點數                                 | `1.0`      | 繪製的大小，以及碰撞箱的大小                                                 |
| `angryScale` | 否   | 浮點數                                 | `scale`    | 有攻擊目標時它膨脹到的大小，以及失去目標後三秒內的大小                       |
| `width`      | 否   | 浮點數                                 | 基礎實體的 | 碰撞箱的寬度，在套用 `scale` 之前                                            |
| `height`     | 否   | 浮點數                                 | 基礎實體的 | 碰撞箱的高度，在套用 `scale` 之前                                            |
| `glowing`    | 否   | 布林值                                 | `false`    | 隔著牆也能看到輪廓                                                           |
| `bright`     | 否   | 布林值                                 | `false`    | 無論站在何處都以最大亮度繪製，如同正午陽光下，因此不會被夜晚、陰影或洞穴壓暗 |
| `invisible`  | 否   | 布林值                                 | `false`    | 不繪製本體，但其裝備仍會繪製                                                 |
| `hideArmor`  | 否   | 布林值                                 | `false`    | 穿著盔甲，但不繪製出來                                                       |
| `hideHeld`   | 否   | 布林值                                 | `false`    | 對手中所持之物同樣如此                                                       |
| `leftHanded` | 否   | 布林值                                 | `false`    | 用另一隻手持武器                                                             |

`scale` 會同時改變兩端的模型和碰撞箱，因此所見即所擊。自行改變大小的生物，例如長大中的動物或殭屍寶寶，會以它選定的大小為基準進行縮放，所以兩者不會衝突。`angryScale` 在它有目標時使其膨脹，失去目標時恢復到 `scale`。由於用戶端從不被告知生物正在追獵什麼，疾跑旗標便負責傳遞這項資訊，它只會設定在使用 `angryScale` 的變體上，其他地方都不會設定，所以讀取你的變體疾跑狀態的模組會看到它發生變化。在低矮的天花板下長大是可能的，與史萊姆長大的情形相同，因此差值應保持適度。

`texture` 會被綁定，取代實體原本使用的那張材質，無論它繼承的是哪種算繪器，因此對模組實體和原版實體都有效。它必須與繪製所用的模型相符，因為模型是基礎實體的，這只是一張外觀貼圖，而不是新的形狀。各圖層保有它們自己的材質，所以換過外觀的殭屍身上，盔甲看起來仍像盔甲。

盔甲只會繪製在算繪器帶有盔甲圖層的實體上，在此版本中指人形生物和村民。牛或蜘蛛的變體可以攜帶盔甲並獲得其防護，但不會繪製出來，因此通常更整潔的做法是用 `attributes` 底下的 `armor` 讓這類生物變得耐打。`hideArmor` 針對另一種情形：人形生物應當保留裝備欄中的盔甲，以取得防護或供讀取它們的模組使用，但不讓人看見。

### 它的音效

*實體變體*

| 鍵            | 必填 | 值     | 預設       | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| ------------- | ---- | ------ | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `sounds`      | 否   | 物件   | 基礎實體的 | `ambient`、`hurt` 和 `death`，各為一個已註冊的音效事件。另有三個基礎實體沒有對應音效的：`target` 在它每次選定目標時播放一次，`explode` 取代遊戲自帶的聲音作為其爆炸的聲音，無論它是用 `explodes` 自爆，還是用 `throws` 投擲 TNT。`throw` 在它用 `throws` 投擲任何東西時播放，取代雪球的投擲聲，或 TNT 的引信嘶嘶聲。`targetVaries` 會讓每次 `target` 播放的音高，在該數值的半音範圍內隨機上下偏移，因此 `3` 即上下各約四分之一個八度；`0` 則原樣播放 |
| `soundVolume` | 否   | 數字   | `1.0`      | 這些聲音有多響                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `soundPitch`  | 否   | 數字   | `1.0`      | 播放的音高。小於 1 更低沉，大於 1 更尖細                                                                                                                                                                                                                                                                                                                                                                                                             |
| `silent`      | 否   | 布林值 | `false`    | 不發出任何聲音                                                                                                                                                                                                                                                                                                                                                                                                                                       |

### 生命值、傷害與效果

*實體變體*

| 鍵                  | 必填 | 值                                              | 預設             | 作用                                                                                                                                                                                                                                                                                                                                                                                |
| ------------------- | ---- | ----------------------------------------------- | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `attributes`        | 否   | 物件                                            | 無               | `maxHealth`、`movementSpeed`、`attackDamage`、`attackSpeed`、`knockbackResistance`、`followRange`、`armor`。實體通常沒有的屬性會被賦予它。對近戰生物而言，`attackSpeed` 是每秒的攻擊次數，遊戲中為 `1`，因此 `2` 代表攻擊頻率加倍。基礎實體已有的任何名稱同樣可用，例如 `zombie.spawnReinforcements`、`horse.jumpStrength`。在會射擊的基礎實體上，`attackDamage` 是其箭矢造成的傷害 |
| `absorption`        | 否   | 浮點數                                          | `0`              | 在生命值之上額外增加的心                                                                                                                                                                                                                                                                                                                                                            |
| `invulnerable`      | 否   | 布林值                                          | `false`          | 除虛空和創造模式外，不受任何傷害                                                                                                                                                                                                                                                                                                                                                    |
| `fireproof`         | 否   | 布林值                                          | `false`          | 完全不會著火，因此永遠不會被火或熔岩傷害，也不會在白天燃燒                                                                                                                                                                                                                                                                                                                          |
| `immuneTo`          | 否   | 傷害類型清單                                    | 無               | 它能無視的傷害：`fall`、`drown`、`explosion`、`magic`、`cactus`、`lava`、`wither`、`starve`、`anvil`、`inWall` 等                                                                                                                                                                                                                                                                   |
| `fallDamage`        | 否   | 浮點數                                          | `1.0`            | 乘上墜落造成的傷害。`0` 則免除墜落傷害                                                                                                                                                                                                                                                                                                                                              |
| `hurtResistance`    | 否   | 整數，單位為刻                                  | 基礎實體的，`20` | 受擊後多久之內無法再次受傷。比這段時間的一半更快的攻擊會被捨棄，因此攻擊速度快的攻擊者需要一個數值更小的目標                                                                                                                                                                                                                                                                        |
| `effects`           | 否   | 物件清單                                        | 無               | 它始終擁有的效果：`{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                                                                                                                                                                                              |
| `ignoresEffects`    | 否   | 藥水 id 清單，或 `all`                          | 無               | 永遠不會作用於它的效果，無論是誰或什麼施加：一次命中、一瓶噴濺藥水、一座信標、一支箭、`/effect`。`all` 會拒絕所有效果，因此變體起初是一張白紙。它自己的 `effects` 仍會加到它身上                                                                                                                                                                                                    |
| `creatureAttribute` | 否   | `undefined`、`undead`、`arthropod` 或 `illager` | 基礎實體的       | 它被視為什麼，因此不死剋星、節肢剋星和治療藥水會據此對待它                                                                                                                                                                                                                                                                                                                          |

### 移動

*實體變體*

| 鍵               | 必填 | 值               | 預設       | 作用                                                                                                   |
| ---------------- | ---- | ---------------- | ---------- | ------------------------------------------------------------------------------------------------------ |
| `jumpMultiplier` | 否   | 浮點數           | `1.0`      | 乘以它的跳躍速度。高度的增長比數值更快，所以 `2.0` 跳得約為被複製實體的三倍半高，`0.5` 約為三分之一高  |
| `stepHeight`     | 否   | 浮點數，單位為格 | 基礎實體的 | 不必跳躍就能走上多高的台階。大多數生物為 `0.6`，殭屍為 `1.0`                                           |
| `maxFallHeight`  | 否   | 整數             | 基礎實體的 | 尋路時願意向下掉落多遠                                                                                 |
| `climbs`         | 否   | 布林值           | 基礎實體的 | 像蜘蛛一樣爬牆，並在牆上尋路；`false` 會讓蜘蛛無法爬牆                                                 |
| `teleports`      | 否   | 布林值           | `true`     | 終界使者或界伏蚌是否可以傳送。關閉後，它會留在原地，白天和在水中也是如此                               |
| `walks`          | 否   | 布林值           | `false`    | 兔子像其他動物一樣行走，而不是以跳躍的方式移動。只有兔子會讀取它                                       |
| `pathPriorities` | 否   | 物件             | 無         | 它願意穿過什麼，例如 `WATER`、`LAVA`、`DANGER_FIRE`、`DOOR_WOOD_CLOSED` 等，各為一個數字，負數表示絕不 |
| `leashable`      | 否   | 布林值           | `false`    | 可以用拉繩牽引，即使被複製的實體原本不行                                                               |
| `steerable`      | 否   | 布林值           | `false`    | 被騎乘時可以操控                                                                                       |
| `noAI`           | 否   | 布林值           | `false`    | 放在哪裡就站在哪裡，什麼也不做                                                                         |

### 水

*實體變體*

| 鍵                   | 必填 | 值     | 預設    | 作用                                                                                                                             |
| -------------------- | ---- | ------ | ------- | -------------------------------------------------------------------------------------------------------------------------------- |
| `breathesUnderwater` | 否   | 布林值 | `false` | 永遠不會溺水，並且會下沉到水底行走，而不是游向水面。它在地面上仍然能找到路，因此無法走出的深水會困住它                           |
| `swims`              | 否   | 布林值 | `false` | 像魷魚或深海守衛一樣在水中移動，並且永遠不會溺水。它在水中而不是地面上尋路，因此它屬於水中，離水就會擱淺                         |
| `amphibious`         | 否   | 布林值 | `false` | 在陸地上行走，在水中則能真正游泳，進出水時會改變尋路方式。它永遠不會溺水。它追逐的對象會在水邊被遺忘，因此每次過水時都會遲疑片刻 |
| `waterSlowdown`      | 否   | 浮點數 | `0.8`   | 水使它減慢多少。數值越高越快                                                                                                     |

### 戰鬥

*實體變體*

| 鍵              | 必填 | 值               | 預設              | 作用                                                                                                                                                                                                 |
| --------------- | ---- | ---------------- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hostile`       | 否   | 布林值           | `false`           | 攻擊搆得到的目標，受傷時會還擊。無論其基礎實體是什麼，敵對變體在遊戲看來都算怪物，因此受怪物上限約束，和平模式會清除它，並且它會捨棄基礎實體自帶的動物任務：繁殖、被引誘、跟隨父母、主人或同類、坐下 |
| `passive`       | 否   | 布林值           | `false`           | 使它不再攻擊任何東西，無論它平時如何表現                                                                                                                                                             |
| `targets`       | 否   | 實體名稱清單     | 玩家              | 敵對時它去尋找什麼。`minecraft:player` 可被辨識，儘管玩家並不是已註冊的實體                                                                                                                          |
| `attackReach`   | 否   | 浮點數，單位為格 | 它的體型          | 近戰攻擊能搆到多遠。遊戲的觸及距離是寬度的兩倍，這就是放大的生物能從更遠處命中的原因；此鍵則直接設定它                                                                                               |
| `knockback`     | 否   | 浮點數           | 基礎實體的，`0.4` | 它的攻擊擊退得有多猛。`0` 則完全不擊退                                                                                                                                                               |
| `hitEffects`    | 否   | 布林值           | `true`            | 是否對被它擊中的目標施加被複製實體會施加的效果：凋零骷髏的凋零、洞穴蜘蛛的劇毒、屍殼的飢餓。關閉後，它只造成傷害                                                                                     |
| `hitFire`       | 否   | 布林值           | `true`            | 在被複製實體會點燃目標時，它是否也點燃被擊中的目標：燃燒的殭屍、烈焰使者的火球。關閉後，它的任何行為都不會在目標身上引發火焰                                                                         |
| `threatLeast`   | 否   | 整數             | `0`               | 128 格內的玩家或其他攜帶者必須處於的最低威脅等級，變體才會自然生成。`0` 照常生成                                                                                                                     |
| `threatHostile` | 否   | 整數             | `0`               | 玩家必須處於的最低威脅等級，變體才會主動攻擊他們。低於該等級時，變體對該玩家是溫和的，不過被擊中時仍會還擊。`0` 照常攻擊                                                                             |

`hostile` 還會去掉讓生物逃跑的行為：原本會躲避玩家或受傷時驚慌的動物，一旦敵對就都不會了，否則它會逃離自己本應攻擊的對象。它需要一個在地面行走的實體，因為它使用的是原版賦予自家怪物的同一套攻擊行為。飛行或游泳的基礎實體會被記錄到日誌並保持原樣。`passive` 的適用範圍更廣，但只能觸及按原版方式建構的行為，敵意寫在自身刻更新或傷害程式碼中的模組，不是資源包能勸退的。

### 裝備、掉落物與經驗值

*實體變體*

| 鍵                   | 必填 | 值                          | 預設       | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| -------------------- | ---- | --------------------------- | ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `equipment`          | 否   | 物件                        | 無         | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet`，各為一個物品名稱                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `dropChance`         | 否   | 0 到 1                      | `0`        | 每件裝備掉落的可能性                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `picksUpLoot`        | 否   | 布林值                      | `false`    | 撿起它走過的物品                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `lootTable`          | 否   | `namespace:entities/<name>` | 基礎實體的 | 它掉落什麼。沒有此鍵時，它會掉落被複製實體所掉落的東西                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `experience`         | 否   | 整數                        | 基礎實體的 | 它掉落多少經驗值                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `collectsExperience` | 否   | 布林值                      | `false`    | 像玩家一樣收集經驗值：八格內的經驗球會飄向它並在接觸時被吸收，其裝備上的修補會優先修復，經驗點數依玩家自己的曲線累積成等級，並透過存檔保存在生物身上。它擊殺的目標會像玩家擊殺一樣掉落經驗值，它的 `digs` 任務破壞的方塊會掉落該方塊自身的經驗值，`block_drops` 的經驗值擲出也會落到它身上。死亡時，除非啟用了 `keepInventory`，它每級掉落七點，最多一百點。帶有 `xp` 或 `level` 條件的計分項會將它的總經驗值和等級記錄在以其 UUID 命名的列上，因此函式可以透過 `score_<objective>_min` 讀取。它像玩家一樣把等級花在鐵砧操作上，見[鐵砧操作](#鐵砧操作) |

變體會掉落被複製實體所掉落的東西，因為戰利品表是固定在該實體自己的程式碼中，而不是依名稱查找的。`lootTable` 會把它指向你自己的表，然後你像其他表一樣，把它提供在 `loot_tables/entities/<name>.json`。

### 特殊行為

*實體變體*

| 鍵               | 必填 | 值             | 預設               | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ---------------- | ---- | -------------- | ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `throws`         | 否   | 布林值         | `false`            | 從遠處把手中所持之物投向目標，若是 TNT，則點燃它並後撤。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `throwAmmo`      | 否   | 整數           | 無                 | 它有多少可以投擲。不寫則永遠不會用完                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `throwReload`    | 否   | 整數，單位為秒 | `explosionFuse` 刻 | 手保持空著多久才再拿出一個，單位為秒。未設定時，等待的刻數與 `explosionFuse` 相同（`explosionFuse` 未設定時為 30 刻，即 1.5 秒）                                                                                                                                                                                                                                                                                                                                                                           |
| `throwRetreat`   | 否   | 整數，單位為秒 | `explosionFuse` 刻 | 投擲之後躲開多久才轉身回來，單位為秒。未設定時，躲開的刻數與 `explosionFuse` 相同（`explosionFuse` 未設定時為 30 刻，即 1.5 秒）                                                                                                                                                                                                                                                                                                                                                                           |
| `throwPower`     | 否   | 浮點數         | `1.0`              | 投擲的力度。加倍大致會使射程加倍                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `throwArc`       | 否   | 浮點數         | `0.35`             | 拋得有多高。越高滯空越久，接近零是平直投擲，低於零則向下投擲                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `throwReturns`   | 否   | 布林值         | `false`            | 投出之物像三叉戟一樣飛行：命中時造成變體的 `attackDamage` 點傷害，基礎實體沒有則為 8，然後像忠誠附魔召回三叉戟那樣飛回它手中。它永遠不會用完，瞄準目標的方式與骷髏相同，`throwPower` 越高越快，難度越高散布越小，並且投擲者在物體飛行期間原地不動，因此 `throwAmmo`、`throwReload`、`throwRetreat` 和 `throwArc` 對它不適用。TNT 仍照常投擲                                                                                                                                                                |
| `explodes`       | 否   | 布林值         | `false`            | 像苦力怕一樣在目標身旁自爆。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `explosionPower` | 否   | 數字           | `3.0`              | 爆炸的大小。苦力怕為 3，TNT 為 4。在苦力怕基礎上，它同時也是苦力怕自身的爆炸，在地獄幽靈上則是火球的爆炸                                                                                                                                                                                                                                                                                                                                                                                                   |
| `explosionFuse`  | 否   | 整數，單位為刻 | `30`               | 引爆前嘶嘶作響多久。在苦力怕基礎上，它同時也是苦力怕自身的引信                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `explosionFire`  | 否   | 布林值         | `false`            | 留下火焰                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `charges`        | 否   | 布林值         | `false`            | 從遠處衝向目標，接觸時造成強力擊退，就像劫毀獸一樣，然後歇息一陣再進行下一次衝鋒。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                           |
| `pounces`        | 否   | 布林值         | `false`            | 蹲伏，然後呈弧線躍向目標並在落地時攻擊，就像狐狸一樣。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `sniffs`         | 否   | 整數，單位為格 | `0`                | 能聽到該格數內玩家的移動聲，無論有沒有牆，並走向聽到聲音的地點；潛行或站立不動的玩家聽不到，而它隨後看到的玩家會成為目標。`0` 表示不監聽。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                   |
| `fleesWhenHurt`  | 否   | 0.0 到 1.0     | `0`                | 當生命值低於該比例時，脫離戰鬥並從對手面前逃開，回升到該比例以上後再回來。`0` 表示從不逃跑。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                 |
| `sleepsByDay`    | 否   | 布林值         | `false`            | 白天尋找陰影，並在那裡站著不動，直到夜晚或被攻擊。休息時側躺著                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `home`           | 否   | 整數，單位為格 | `0`                | 活動範圍限制在它最初所站位置周圍該格數以內，在範圍內遊蕩，走出去就走回來。`0` 表示自由漫遊                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `patrols`        | 否   | 布林值         | `false`            | 與同類一起跟隨領隊，分成長段在陸地上行走，就像掠奪者巡邏隊一樣。一起生成的一組會選出一個領隊；其餘的保持在它幾格範圍內，領隊選定目標時它們全都跟著選定。失去領隊的跟隨者會自己成為領隊。需要 `hostile`                                                                                                                                                                                                                                                                                                     |
| `swoops`         | 否   | 布林值         | `false`            | 在目標上空盤旋並俯衝穿過它，在掠過時攻擊，就像夜魅一樣。變體會得到一個飛行輔助，因此狩獵時飛行，閒置時落到地面；它需要一個屬於生物的基礎實體，例如鸚鵡，蝙蝠則不行。需要 `hostile`                                                                                                                                                                                                                                                                                                                         |
| `gusts`          | 否   | 布林值         | `false`            | 蓄力並從遠處朝目標放出一陣風，把目標附近的一切向後向上擲出，就像旋風人的風彈一樣。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                           |
| `gustPower`      | 否   | 浮點數         | `1.5`              | 一陣風擲出的力度。生物的一次命中為 0.4，強力擊退附魔約為 1                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `digs`           | 否   | 布林值         | `false`            | 用手中的工具挖穿它與目標之間的一切：鏟子挖泥土、沙子和礫石，鎬挖石頭，斧砍木頭，並且只能挖開該工具材質能破壞的方塊，因此木鎬永遠挖不開鐵礦石，不到鑽石級別就挖不開黑曜石。一個方塊所需的時間與玩家用該工具相同，掉落相同的東西，並會磨損工具。用 `equipment` 給它工具；空手則什麼也挖不了，而在 `mobGriefing` 關閉的地方它也什麼都不挖。它從不尋找繞路：有目標時徑直走向目標，挖掉擋路的一切，工具無法打開方塊時就站著推。需要 `hostile`。它無須看見目標就能選定目標，因為它朝向挖掘的東西本來就在某物之後 |

**投擲而不是衝鋒。** `explodes` 讓生物衝上去自爆。`throws` 則是另一種性情：它保持距離，把主手中的任何東西投向正在戰鬥的對象，若恰好是 TNT，就點燃它、投出，並在它燃燒時後退。

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

投擲會清空它的手，因為它把東西扔了出去。隨後它會維持 `throwRetreat` 的時間躲開，經過 `throwReload` 後再拿出一個，並轉回目標：投擲、後撤、裝填、逼近，如此循環。替它設定 `throwAmmo`，這個循環會在數量用完時結束，它的手永久空著，改由一般攻擊接手。不寫 `throwAmmo`，它就永遠不會用完。

這個數量會寫入生物本身，因此不會因為區塊被卸載再載入而補滿。任何不是 TNT 的東西都會以物品形式飛出並落地，這使得投擲石頭或腐肉的工兵，與投擲炸藥的工兵一樣容易製作。

`explosionFuse` 仍是被投出的 TNT 的引信，並且會代替你省略的任一計時器，因此在這些鍵出現之前寫的變體，行為與以前完全一致。

投擲本身如何飛行，由 `throwPower` 和 `throwArc` 決定。前者是對推力的乘數，而推力本來就會隨距離增加，因此提高它會加長射程，而不改變投擲物在空中停留的時間。後者是升力，它改變軌跡的形狀：高則越過牆壁拋射、耗時較長，接近零則平直擲出、幾乎立刻落地，低於零則向下投向下方的目標。兩者都不影響引信，因此拋射的炸藥和平直的炸藥，在離手後爆炸的秒數相同，這決定了它是在頭頂爆開，還是先落地再等待。它從多遠處投擲，由它的 `followRange` 決定，你靠近到三格以內時它照常逼近，因此遠距離危險，貼臉時則平平無奇。

### 任務

*實體變體*

**遊戲擁有的任何任務。** 上面的鍵是 RDPL 自己的行為。`tasks` 則能觸及原版自身使用的每一種任務，適用於任何基礎實體：一個項目是一個物件，寫明 `task` 及其 `priority`，外加該任務所讀取的內容；以 `-` 開頭的名稱會去掉基礎實體自帶的該種任務。優先順序從 0 起先執行，原版自己的優先順序在 1 到 8 之間，因此 0 的任務勝過基礎實體的一切行為，而 9 的任務只在沒有其他事可做時才會執行。

```json
{
  "entity": "minecraft:cow",
  "tasks": [
    "-wander",
    { "task": "avoidEntity", "priority": 3, "entity": "minecraft:player", "distance": 8, "speed": 1.0, "nearSpeed": 1.4 },
    { "task": "watchClosest", "priority": 6, "entity": "minecraft:wolf", "distance": 12 },
    { "task": "wanderAvoidWater", "priority": 7, "speed": 0.8, "chance": 0.01 },
    { "task": "wander", "priority": 8, "speed": 0.8, "chance": 60 },
    { "task": "follow", "priority": 5, "distance": 10, "near": 3 },
    { "task": "leapAtTarget", "priority": 3, "leap": 0.6 },
    { "task": "attackRanged", "priority": 4, "cooldown": 30, "distance": 15 },
    { "task": "attackMelee", "priority": 2, "memory": true },
    { "task": "openDoor", "priority": 9, "close": true },
    { "task": "moveThroughVillage", "priority": 5, "nocturnal": true },
    { "task": "tempt", "priority": 4, "items": ["minecraft:wheat"], "scared": true },
    { "task": "nearestAttackableTarget", "priority": 2, "entity": "minecraft:player", "sight": true, "nearby": true },
    { "task": "hurtByTarget", "priority": 1, "help": true }
  ]
}
```

| 鍵      | 必填 | 值   | 預設 | 作用                                                                                           |
| ------- | ---- | ---- | ---- | ---------------------------------------------------------------------------------------------- |
| `tasks` | 否   | 清單 | 無   | 遊戲擁有的任何任務，依名稱以你選擇的優先順序加入變體，或從其基礎實體自帶的任務中去除。清單見下 |

該清單會在 `hostile`、`passive` 和上述行為完成各自的工作之後套用，因此它擁有最終決定權。驅動身體移動的任務會互相排斥：一個任務只在優先順序更前面的任務都沒有在移動生物時才會執行，而怪物自帶的攻擊位於 2，所以殭屍上的撲躍或逃跑需要優先順序 1，否則永遠輪不到它；蜘蛛和狼出於同樣的原因，把撲躍排在攻擊之前。基礎實體已在執行的任務會被再加入一次，而不是被取代；請先去掉舊的。有些任務只對具備其所驅動之物的基礎實體才有意義：弓箭戰鬥需要會射擊的基礎實體，坐下需要可馴服的基礎實體，交易需要村民。在無法承載某個任務的基礎實體上要求它，日誌會說明它需要哪種基礎實體，而變體則會放棄該任務。

| 鍵          | 類型           | 預設           | 作用                                                                                                                                                                                              |
| ----------- | -------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `priority`  | 整數           | 必填           | 它在基礎實體各任務中的位置。越小越先執行                                                                                                                                                          |
| `speed`     | 數字           | 該任務的一般值 | 任務執行時它移動的速度，作為行走速度的乘數                                                                                                                                                        |
| `nearSpeed` | 數字           | `1.2`          | `avoidEntity`：它所躲避之物靠近時的乘數                                                                                                                                                           |
| `distance`  | 數字，單位為格 | 該任務的一般值 | 它能看多遠、跟隨多遠、射擊多遠或保持多遠                                                                                                                                                          |
| `near`      | 數字，單位為格 | 該任務的一般值 | `follow`、`followOwner`、`followOwnerFlying`：它走近到多近才停下                                                                                                                                  |
| `chance`    | 數字           | 該任務的一般值 | `wander`：每隔這麼多刻擲一次；`wanderAvoidWater`：遊蕩的一步在陸地上無視水的規則、並可能選中水中位置的機率，0 到 1（未設定時為 `0.001`）；`watchClosest`、`watchClosest2`：每刻注視的機率，0 到 1 |
| `leap`      | 數字           | `0.4`          | `leapAtTarget`：跳得多高                                                                                                                                                                          |
| `cooldown`  | 整數，單位為刻 | `20`           | `attackRanged`、`attackRangedBow`：兩次射擊之間的刻數                                                                                                                                             |
| `entity`    | 實體名稱       | 無             | 任務尋找、躲避、注視或與之繁殖的實體。`minecraft:player` 可被辨識                                                                                                                                 |
| `items`     | 物品名稱清單   | 無             | `tempt`：玩家手中舉著的物品                                                                                                                                                                       |
| `sight`     | 布林值         | `true`         | `nearestAttackableTarget`、`targetNonTamed`：只針對它看得見的                                                                                                                                     |
| `nearby`    | 布林值         | `false`        | `nearestAttackableTarget`：只針對在它自身跟隨範圍內的                                                                                                                                             |
| `help`      | 布林值         | `false`        | `hurtByTarget`：附近的同類也加入戰鬥                                                                                                                                                              |
| `memory`    | 布林值         | `false`        | `attackMelee`、`zombieAttack`：對失去視線的目標繼續追擊                                                                                                                                           |
| `close`     | 布林值         | `false`        | `openDoor`：在身後關上門                                                                                                                                                                          |
| `nocturnal` | 布林值         | `false`        | `moveThroughVillage`：只在夜間                                                                                                                                                                    |
| `scared`    | 布林值         | `false`        | `tempt`：玩家移動過快會打破魔咒                                                                                                                                                                   |

`List` 欄說明任務位於何處。`tasks` 是生物所做的事；`targets` 是它如何挑選追擊對象，而沒有對應攻擊的目標任務本身不起任何作用。

| 任務                      | 需要             | 清單      | 讀取                                       | 作用                                                     |
| ------------------------- | ---------------- | --------- | ------------------------------------------ | -------------------------------------------------------- |
| `attackMelee`             | 行走的生物       | `tasks`   | `speed`、`memory`                          | 走到目標面前並攻擊它                                     |
| `attackRanged`            | 會射擊的基礎實體 | `tasks`   | `speed`、`cooldown`、`distance`            | 保持距離，射擊其基礎實體所射出的東西                     |
| `attackRangedBow`         | 會射擊的怪物     | `tasks`   | `speed`、`cooldown`、`distance`            | 骷髏的弓箭戰鬥：橫移、拉弓、放箭                         |
| `avoidEntity`             | 行走的生物       | `tasks`   | `entity`、`distance`、`speed`、`nearSpeed` | 指名的實體進入 `distance` 內時逃開                       |
| `beg`                     | 狼               | `tasks`   | `distance`                                 | 向舉著食物的玩家乞食                                     |
| `breakDoor`               | 任何基礎實體     | `tasks`   |                                            | 在困難難度下破壞擋路的木門                               |
| `creeperSwell`            | 苦力怕           | `tasks`   |                                            | 嘶嘶作響並在目標身旁爆炸                                 |
| `defendVillage`           | 鐵魔像           | `targets` |                                            | 攻擊襲擊過村民的人                                       |
| `eatGrass`                | 任何基礎實體     | `tasks`   |                                            | 像綿羊一樣吃草                                           |
| `findEntityNearest`       | 任何基礎實體     | `targets` | `entity`                                   | 以最近的指名實體為目標，如史萊姆或地獄幽靈選定目標的方式 |
| `findEntityNearestPlayer` | 任何基礎實體     | `targets` |                                            | 以搆得到的最近玩家為目標                                 |
| `fleeSun`                 | 行走的生物       | `tasks`   | `speed`                                    | 被陽光照到時尋找陰影                                     |
| `follow`                  | 任何基礎實體     | `tasks`   | `speed`、`near`、`distance`                | 跟隨同類                                                 |
| `followGolem`             | 村民             | `tasks`   |                                            | 跟隨舉著罌粟花的鐵魔像                                   |
| `followOwner`             | 可馴服的基礎實體 | `tasks`   | `speed`、`near`、`distance`                | 跟隨主人，落後太遠時傳送到主人身邊                       |
| `followOwnerFlying`       | 可馴服的基礎實體 | `tasks`   | `speed`、`near`、`distance`                | 同上，以飛行方式                                         |
| `followParent`            | 動物             | `tasks`   | `speed`                                    | 幼體緊跟同類的成體                                       |
| `harvestFarmland`         | 村民             | `tasks`   | `speed`                                    | 收割成熟的作物並重新種上                                 |
| `hurtByTarget`            | 行走的生物       | `targets` | `help`                                     | 向打它的東西還擊                                         |
| `landOnOwnersShoulder`    | 鸚鵡             | `tasks`   |                                            | 停在主人的肩膀上                                         |
| `leapAtTarget`            | 任何基礎實體     | `tasks`   | `leap`                                     | 從近處向目標撲躍                                         |
| `llamaFollowCaravan`      | 駱馬             | `tasks`   | `speed`                                    | 排在被牽著的駱馬後面                                     |
| `lookAtTradePlayer`       | 村民             | `tasks`   |                                            | 面向正在與它交易的玩家                                   |
| `lookAtVillager`          | 鐵魔像           | `tasks`   |                                            | 注視村民                                                 |
| `lookIdle`                | 任何基礎實體     | `tasks`   |                                            | 不時四處張望                                             |
| `mate`                    | 動物             | `tasks`   | `speed`、`entity`                          | 進入戀愛狀態時，與同類或指名的 `entity` 繁殖             |
| `moveIndoors`             | 行走的生物       | `tasks`   |                                            | 夜幕降臨時進入村莊的房屋                                 |
| `moveThroughVillage`      | 行走的生物       | `tasks`   | `speed`、`nocturnal`                       | 沿村莊的道路在各家門之間行走                             |
| `moveTowardsRestriction`  | 行走的生物       | `tasks`   | `speed`                                    | 走遠時朝它的家園位置走回                                 |
| `moveTowardsTarget`       | 行走的生物       | `tasks`   | `speed`、`distance`                        | 逼近遠處的目標                                           |
| `nearestAttackableTarget` | 行走的生物       | `targets` | `entity`、`sight`、`nearby`                | 以最近的指名實體為目標                                   |
| `ocelotAttack`            | 任何基礎實體     | `tasks`   |                                            | 貓的潛行與撲擊                                           |
| `ocelotSit`               | 山貓             | `tasks`   | `speed`                                    | 坐在箱子、床和點燃的熔爐上                               |
| `openDoor`                | 任何基礎實體     | `tasks`   | `close`                                    | 打開它走過的木門                                         |
| `ownerHurtByTarget`       | 可馴服的基礎實體 | `targets` |                                            | 攻擊打了它主人的東西                                     |
| `ownerHurtTarget`         | 可馴服的基礎實體 | `targets` |                                            | 攻擊它主人所打的東西                                     |
| `panic`                   | 行走的生物       | `tasks`   | `speed`                                    | 受傷或著火時奔逃                                         |
| `play`                    | 村民             | `tasks`   | `speed`                                    | 孩子們互相玩捉人遊戲                                     |
| `restrictOpenDoor`        | 行走的生物       | `tasks`   |                                            | 夜間留在村莊的門內                                       |
| `restrictSun`             | 行走的生物       | `tasks`   |                                            | 白天待在陰影裡                                           |
| `runAroundLikeCrazy`      | 馬、驢、騾或駱馬 | `tasks`   | `speed`                                    | 甩掉尚未信任的騎乘者                                     |
| `sit`                     | 可馴服的基礎實體 | `tasks`   |                                            | 被命令時坐下                                             |
| `skeletonRiders`          | 骷髏馬           | `tasks`   |                                            | 玩家靠近時召來骷髏騎士，即陷阱馬                         |
| `swimming`                | 任何基礎實體     | `tasks`   |                                            | 讓頭保持在水面之上                                       |
| `targetNonTamed`          | 可馴服的基礎實體 | `targets` | `entity`、`sight`                          | 在尚未被馴服時，以指名的實體為目標                       |
| `tempt`                   | 行走的生物       | `tasks`   | `items`、`speed`、`scared`                 | 跟隨舉著某件 `items` 的玩家                              |
| `tradePlayer`             | 村民             | `tasks`   |                                            | 交易時站著不動                                           |
| `villagerInteract`        | 村民             | `tasks`   |                                            | 與其他村民交談                                           |
| `villagerMate`            | 村民             | `tasks`   |                                            | 村莊有空位時繁殖                                         |
| `wander`                  | 行走的生物       | `tasks`   | `speed`、`chance`                          | 四處遊蕩                                                 |
| `wanderAvoidWater`        | 行走的生物       | `tasks`   | `speed`、`chance`                          | 遊蕩，避開水                                             |
| `wanderAvoidWaterFlying`  | 行走的生物       | `tasks`   | `speed`                                    | 在空中遊蕩並棲息在樹上                                   |
| `watchClosest`            | 任何基礎實體     | `tasks`   | `entity`、`distance`、`chance`             | 注視最近的指名實體，未指名則為玩家                       |
| `watchClosest2`           | 任何基礎實體     | `tasks`   | `entity`、`distance`、`chance`             | 同上，在另一個任務執行時持續保持                         |
| `zombieAttack`            | 殭屍             | `tasks`   | `speed`、`memory`                          | 殭屍的攻擊，雙臂抬起                                     |

### 生成與消失

*實體變體*

| 鍵                  | 必填 | 值             | 預設       | 作用                                                             |
| ------------------- | ---- | -------------- | ---------- | ---------------------------------------------------------------- |
| `spawns`            | 否   | 物件清單       | 無         | `creatureType`、`weight`、`min` 和 `max`，與生態域使用的結構相同 |
| `biomes`            | 否   | 生態域名稱清單 | 所有生態域 | 在哪些生態域中加入這些生成項目                                   |
| `biomeTypes`        | 否   | 辭典類型清單   | 無         | 同上，但依類型指定                                               |
| `ignoresSpawnRules` | 否   | 布林值         | `false`    | 放在哪裡就在哪裡生成，忽略它繼承而來的規則                       |
| `despawns`          | 否   | 布林值         | `true`     | 關閉後，即使通常會被清除，它也會留在世界中                       |
| `despawnAfter`      | 否   | 整數，單位為秒 | 無         | 在世界中存在這麼久之後悄然消失，無論周圍有沒有玩家               |
| `persistent`        | 否   | 布林值         | `false`    | 永不消失                                                         |

**有保存期限的生物。** `despawnAfter` 從生物首次進入世界的那一刻起以秒計時，時間一到就悄然將其移除：沒有死亡，沒有掉落物，沒有聲音，就像它自己走開後被清除了一樣。計時資訊會寫入生物本身，所以存檔並重新載入後仍會繼續計時，而不是每次區塊重新載入時從頭開始。

它是獨立的機制，不受 `despawns` 和 `persistent` 所管規則的影響。後兩者決定遊戲是否可以因為生物遠離所有玩家而將其清除；這一項則承諾它會在設定的時間消失，不論其他條件。一隻生物可以同時 `persistent` 又有保存期限，這正適合為一場戰鬥或活動召喚出來、不應比活動存在更久的生物。

計時依據世界時間，因此無人遊玩時會暫停，區塊未載入的那段時間也不計入。

### 生成條件

*實體變體*

```json
{
  "dimensions": [-1],
  "structures": ["Fortress"],
  "radius": 48,
  "timeFrom": 13000,
  "timeTo": 23000,
  "dayAtLeast": 3,
  "advancement": "minecraft:story/mine_stone",
  "team": "miners",
  "objective": "kills",
  "scoreAtLeast": 5
}
```

| 鍵                          | 必填 | 值                 | 預設     | 作用                                                                                                                                                                                          |
| --------------------------- | ---- | ------------------ | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `dimensions`                | 否   | 維度 ID 或名稱清單 | 所有維度 | 可以自然生成的維度，以 ID 或名稱填寫，例如 `-1` 或 `the_nether`                                                                                                                               |
| `structures`                | 否   | 清單               | 無       | 可以自然生成的位置：`Village`、`Temple`、`Mansion`、`Monument`、`Mineshaft`、`Stronghold`、`Fortress` 或 `EndCity` 內部，或填寫資料包透過 `structures` 放置的結構名稱，則在其 `radius` 範圍內 |
| `radius`                    | 否   | 整數               | `32`     | 距離資料包自帶結構多遠仍算在結構內                                                                                                                                                            |
| `timeFrom`、`timeTo`        | 否   | 整數，刻           | 無       | 可以生成的時段，可以跨過午夜，例如從 `13000` 到 `1000`。兩者都需要                                                                                                                            |
| `dayAtLeast`                | 否   | 整數               | 無       | 從第幾天起可以生成                                                                                                                                                                            |
| `advancement`               | 否   | `namespace:path`   | 無       | 僅當 128 格內有玩家擁有此進度時才生成                                                                                                                                                         |
| `team`                      | 否   | 字串               | 無       | 僅當 128 格內有玩家屬於此計分板隊伍時才生成                                                                                                                                                   |
| `objective`、`scoreAtLeast` | 否   | 字串，整數         | 無       | 僅當 128 格內有玩家在此計分項上至少有該分數時才生成。兩者都需要                                                                                                                               |

沒有設定任何這些條件的變體照常生成；設定了多個條件的變體，只在全部滿足的地點和時間生成。它們限制自然生成，包括區塊生成時的生成；生怪蛋、`/summon`、生怪磚和 `becomes` 不受限制。`advancement`、`team`、`objective` 和 `scoreAtLeast` 會對生成點 128 格內的每名玩家（不含旁觀者）一起判定：必須由同一名玩家全部滿足，附近沒有玩家時，設定了其中任何一項的變體不會生成。

### 生成鎖

*實體變體*

`<namespace>/spawn_locks/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。在與變體相同類型的條件成立之前，攔下所列生物的自然生成，因此不需變體檔案也能限制任何生物。

```json
{
  "entities": ["minecraft:blaze", "mymod:thing"],
  "dimensions": [-1],
  "advancement": "minecraft:story/enter_the_nether"
}
```

| 鍵                                                                                                                           | 必填 | 值                 | 預設 | 作用                                                                                                                                               |
| ---------------------------------------------------------------------------------------------------------------------------- | ---- | ------------------ | ---- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entities`                                                                                                                   | 是   | 實體名稱或類別清單 | 無   | 要攔下的生物：如 `minecraft:blaze` 的 ID，或 `#monster`、`#creature`、`#ambient`、`#water_creature` 這樣的類別。沒有此鍵的檔案會被略過，日誌會註明 |
| `dimensions`, `structures`, `radius`, `timeFrom`, `timeTo`, `dayAtLeast`, `advancement`, `team`, `objective`, `scoreAtLeast` | 否   | 與生成條件相同     | 無   | 與生成條件中相同的鍵，意義也相同。一個都沒設定的檔案會被略過，日誌會註明                                                                           |

同時出現在多個檔案、或還出現在變體中的生物，只會在所有條件都成立的地點與時間生成。被攔下的是自然生成，包括區塊生成時產生的；生怪蛋、`/summon` 和生怪磚不受影響。

### 網路

*實體變體*

| 鍵                  | 必填 | 值     | 預設   | 作用                                                     |
| ------------------- | ---- | ------ | ------ | -------------------------------------------------------- |
| `trackingRange`     | 否   | 整數   | `80`   | 在多遠的距離內向用戶端通知它的存在                       |
| `trackVelocity`     | 否   | 布林值 | `true` | 除位置外同時傳送它的速度。關閉可為幾乎不動的實體節省流量 |
| `trackingFrequency` | 否   | 整數   | `3`    | 更新頻率，以刻為單位                                     |

### Boss 血條

*實體變體*

```json
{
  "entity": "minecraft:zombie",
  "name": "Bar Brute",
  "bossBar": {
    "name": "Bar Brute",
    "color": "red",
    "style": "notched_10",
    "background": { "texture": "mymod:textures/gui/boss_back.png", "width": 182, "height": 19, "x": 0, "y": 0 }
  }
}
```

| 鍵                   | 必填 | 值   | 預設       | 作用                                                                                                                                                                        |
| -------------------- | ---- | ---- | ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `bossBar`            | 否   | 物件 | 無         | 生物存活期間，向附近的玩家顯示 Boss 血條                                                                                                                                    |
| `bossBar.name`       | 否   | 文字 | 生物的名稱 | 血條的標題                                                                                                                                                                  |
| `bossBar.color`      | 否   | 文字 | `purple`   | `pink`、`blue`、`red`、`green`、`yellow`、`purple` 或 `white`                                                                                                               |
| `bossBar.style`      | 否   | 文字 | `progress` | `progress`、`notched_6`、`notched_10`、`notched_12` 或 `notched_20`                                                                                                         |
| `bossBar.background` | 否   | 物件 | 無         | 繪製在血條及其標題後方的紋理：`texture`（`namespace:路徑.png`，必填），`width` 和 `height`（預設為 182 和血條所在列的高度），`x` 和 `y`（相對標題列左上角的偏移，預設為 0） |

每個生物有自己的血條，其 `trackingRange` 範圍內的所有玩家都能看到。血條跟隨生物的生命值，受傷時會變短；生物死亡、被移除或所在區塊卸載時血條消失。旁觀者看不到它。未知的 `color` 或 `style` 會記入日誌，並使用預設值。沒有 `bossBar` 時一切照舊。

### 階段

*實體變體*

```json
{
  "entity": "minecraft:zombie",
  "hostile": true,
  "attributes": { "maxHealth": 200, "movementSpeed": 0.23, "attackDamage": 6 },
  "bossBar": { "name": "Warden", "color": "purple" },
  "phases": [
    {
      "below": 0.66,
      "attributes": { "movementSpeed": 0.30, "attackDamage": 9 },
      "charges": true,
      "tasks": [ { "task": "leapAtTarget", "priority": 3, "leap": 0.5 }, "-wanderAvoidWater" ],
      "bossBar": { "name": "Warden Enraged", "color": "red", "style": "notched_6" }
    },
    {
      "below": 0.33,
      "attributes": { "movementSpeed": 0.36, "attackDamage": 12 },
      "pounces": true,
      "gusts": true,
      "gustPower": 2.0
    }
  ]
}
```

| 鍵                                                                             | 必填 | 值                     | 預設       | 作用                                                                                                                         |
| ------------------------------------------------------------------------------ | ---- | ---------------------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------- |
| `phases`                                                                       | 否   | 清單                   | 無         | 生物的生命階段。依 `below` 排序；生物所處的是 `below` 大於其生命比例的最深一個階段                                           |
| `phases[].below`                                                               | 是   | 數字（大於 0，小於 1） | 無         | 階段開始時的生命比例。一次攻擊跨過多個門檻時，只會進入最深的階段。兩個階段的 `below` 相同時，後者會記入日誌並被捨棄          |
| `phases[].attributes`                                                          | 否   | 物件                   | 無         | 名稱與 `attributes` 相同。階段持續期間，這些值會取代生物自身的值。`maxHealth` 會被拒絕並在日誌中報錯，因為它會移動基準線本身 |
| `phases[].tasks`                                                               | 否   | 清單                   | 無         | 語法與 `tasks` 相同。進入階段時新增；階段新增或移除的內容在離開階段時還原                                                    |
| `phases[].charges`、`phases[].pounces`、`phases[].gusts`、`phases[].gustPower` | 否   | 與 `phases` 之外相同   | 關閉       | 僅在階段持續期間生效的攻擊行為。與 `phases` 之外的同名鍵一樣，要求生物帶有 `hostile`。生物自身已有的行為不會被重複新增       |
| `phases[].bossBar`                                                             | 否   | 物件（同 `bossBar`）   | 生物自身的 | 階段持續期間取代血條的名稱、顏色、樣式與背景。生物必須有自己的 `bossBar`；沒有時該鍵會記入日誌並被捨棄                       |

階段會隨生命值雙向變化：回復到門檻以上的首領會回到上一個階段或基礎狀態。每個階段相互獨立：從一個階段轉入另一個階段時，會先還原前一個階段，所以範例中的第二階段不會繼承第一階段的衝鋒或額外任務。存檔中不會記錄任何內容；生物載入時，階段會依生命值重新計算。階段內的其他鍵（`swoops`、`digs`、`throws`，它們需要在生物出現時做準備）會記入日誌並被忽略。

### 儲存

*實體變體*

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

`storage` 可以為任意實體的變體提供物品欄位、流體儲罐和能量緩衝，三者各自只在寫出對應物件時才會存在。每一項都作為該實體的 Forge 物品、流體或能量能力對外提供，因此任何向實體輸送物品、流體或能量的裝置都能連接到它。如果原實體自身已經回應該能力，例如生物對手部和盔甲、馬或運輸礦車對其物品欄的回應，那麼資源包的儲存會在每一面取而代之。玩家潛行並對實體按右鍵即可打開介面。內容會隨實體一同儲存。

| 鍵             | 必填 | 值                          | 預設    | 作用                                                                                                                                                                                                                                                            |
| -------------- | ---- | --------------------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `items`        | 否   | 物件                        | 無      | 為實體提供物品欄位。區域為三列，每列 9 格：每個流體條或能量條占一列，其餘列給欄位，因此兩個條都有時為 1x9，有一個時為 2x9，都沒有時為 3x9。沒有 `items` 時只顯示條                                                                                              |
| `fluid`        | 否   | 物件                        | 無      | 一個流體儲罐                                                                                                                                                                                                                                                    |
| `energy`       | 否   | 物件                        | 無      | 一個 Forge Energy 緩衝                                                                                                                                                                                                                                          |
| `dropsOnDeath` | 否   | 布林值                      | `true`  | 實體死亡時，儲存的物品會作為掉落物散落在原地。`false` 則遺失。流體和能量無論如何都會遺失                                                                                                                                                                        |
| `runsDry`      | 否   | `stops`、`slows` 或 `hurts` | `stops` | 當它付不起整整一秒的 `use` 時會發生什麼。`stops`：它不再思考，停在原地，其任務、目標和行為全部閒置，直到重新補充，不過它仍會下落，也能被推動。`slows`：它以一半速度移動。`hurts`：它每秒受到 1 點傷害，如同飢餓，所以用 `immuneTo` 加 `starve` 可以讓它免於傷害 |

**靠自身攜帶的資源運轉。** 儲罐或緩衝上的 `use` 是一項持續消耗：實體每秒從中取走這麼多，不受 `transfer`、`buckets` 和過濾器的限制。當任何一個所存的量不足一整秒的 `use` 時，實體即為耗盡：不再取走任何東西，由 `runsDry` 決定後果，而一旦重新補滿就立刻恢復正常。只有生物才會消耗；對於不是生物的原實體，例如礦車，`use` 不起作用。

`items`：

| 鍵       | 必填 | 值           | 預設 | 作用                                   |
| -------- | ---- | ------------ | ---- | -------------------------------------- |
| `filter` | 否   | 過濾項目清單 | 無   | 欄位接受什麼。沒有它時欄位接受任何東西 |

`fluid`：

| 鍵         | 必填 | 值                  | 預設    | 作用                                                                                                         |
| ---------- | ---- | ------------------- | ------- | ------------------------------------------------------------------------------------------------------------ |
| `capacity` | 是   | 整數，單位為 mB     | 無      | 儲罐的容量                                                                                                   |
| `filter`   | 否   | 過濾項目清單        | 無      | 儲罐接受哪些流體。沒有它時接受任何流體                                                                       |
| `buckets`  | 否   | 布林值              | `false` | 不潛行時用桶子或其他流體容器按右鍵，可將流體倒入儲罐或從儲罐中取出。沒有移動任何流體的點擊則交給實體自己處理 |
| `use`      | 否   | 整數，單位為每秒 mB | `0`     | 實體存活期間每秒從儲罐中消耗多少。`0` 表示沒有消耗                                                           |

`energy`：

| 鍵         | 必填 | 值                  | 預設   | 作用                                           |
| ---------- | ---- | ------------------- | ------ | ---------------------------------------------- |
| `capacity` | 是   | 整數，單位為 FE     | 無     | 能儲存多少能量                                 |
| `transfer` | 否   | 整數，單位為 FE     | 無限制 | 單次操作中最多輸入或輸出的能量                 |
| `use`      | 否   | 整數，單位為每秒 FE | `0`    | 實體存活期間每秒消耗多少能量。`0` 表示沒有消耗 |

過濾項目。第一個符合的項目起決定作用，沒有任何項目符合的東西會被拒絕：

| 鍵        | 必填   | 值       | 預設 | 作用                                                               |
| --------- | ------ | -------- | ---- | ------------------------------------------------------------------ |
| `item`    | 三擇一 | 物品名稱 | 無   | 一種物品，寫作 `namespace:name` 或 `namespace:name:meta`           |
| `oreDict` | 三擇一 | 礦石名稱 | 無   | 該礦物辭典名稱下的所有物品                                         |
| `fluid`   | 三擇一 | 流體名稱 | 無   | 以註冊名稱指定的流體，例如 `water`。僅由流體過濾器讀取             |
| `max`     | 否     | 整數     | `0`  | 同時最多存放多少，按所有欄位合計，流體則以 mB 計。`0` 表示沒有上限 |

### Galacticraft 火箭

*實體變體*

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

放置它的物品，位於 `<namespace>/items/supply_rocket.json`：

```json
{
  "type": "rocket",
  "rocket": "mypack:supply_rocket",
  "variants": {
    "supply_rocket": { "meta": 0 }
  }
}
```

任何基於 Galacticraft 的 `EntitySpaceshipBase` 或 ExtraPlanets 的 `EntityElectricSpaceshipBase` 的火箭，其變體都會讀取 `galacticraft` 物件，因此其他 Galacticraft 附加模組中基於這兩者之一的火箭也同樣適用。可作為基礎的火箭，依模組列出：

- Galacticraft：`galacticraftcore:rocket_t1`、`galacticraftplanets:rocket_t2`、`galacticraftplanets:rocket_t3`，以及貨運火箭 `galacticraftplanets:rocket_cargo`
- GalaxySpace：`galacticraftcore:rocket_tier_4`、`galacticraftcore:rocket_tier_5`、`galacticraftcore:rocket_tier_6`、`galacticraftcore:rocket_multi_seat`，以及流體貨運火箭 `galacticraftcore:rocket_fluid_cargo`。該火箭只運載流體，因此其上的 `cargoSlots`、`cargo`、`requiredPayload` 和 `payload` 會被忽略；`tier` 和 `fuelTank` 仍然生效
- ExtraPlanets：從 `extraplanets:extraplanets.entitytier4rocket` 到 `extraplanets:extraplanets.entitytier10rocket`，以及電力火箭 `extraplanets:extraplanets.entitytier10electricrocket`

對其他任何實體，該物件會被忽略，日誌中會有說明。火箭透過 `rocket` 類型的物品放置：燃料火箭放在 Galacticraft 的發射台上，電力火箭放在 ExtraPlanets 的電力火箭發射平台上。對電力火箭而言，`fuelTank` 決定它能儲存多少電量，電量取代燃料。貨運火箭不載乘客，由發射控制器送往降落平台。破壞火箭，或帶著它降落到另一個天體上，會歸還該物品，貨物和燃料或電量都在其中。

| 鍵                | 必填 | 值                    | 預設         | 作用                                                                                                      |
| ----------------- | ---- | --------------------- | ------------ | --------------------------------------------------------------------------------------------------------- |
| `tier`            | 否   | 整數                  | 原火箭的等級 | 火箭等級，決定它能到達哪些天體                                                                            |
| `fuelTank`        | 否   | 整數                  | 原火箭的容量 | 燃料箱大小。1 級火箭為 `1000`                                                                             |
| `cargoSlots`      | 否   | `0`、`27`、`36`、`54` | `0`          | Galacticraft 的貨艙規格。`27` 提供 18 個欄位，與 Galacticraft 自帶火箭相同                                |
| `cargo`           | 否   | 過濾項目清單          | 無           | 貨艙欄位和貨物裝載器可以放入什麼。沒有它時接受任何東西。項目與 `storage` 的相同，使用 `item` 或 `oreDict` |
| `requiredPayload` | 否   | 清單                  | 無           | 火箭發射前貨艙中必須裝有什麼。每個項目是 `item` 或 `oreDict` 加上 `count`，預設為 `1`                     |
| `payload`         | 否   | 清單                  | 無           | 新製造出的火箭物品攜帶什麼，在首次放置時裝入貨艙。每個項目是 `item` 加上 `count`，預設為 `1`              |

## 生物等級

*生物與危險*

`<namespace>/ranks/*.json`

檔案路徑就是等級的名稱，多個檔案會疊加。等級是生成的生物可能獲得的一種層級。生物第一次進入世界時，遊戲會依 `weight` 從所有指明該生物的等級中抽出一個，並永久套用：等級會隨生物一起儲存，不會再次抽取。只有 `entities` 和 `weight` 的等級就是普通層級，可以用它來決定普通生物維持多大的出現比例。等級適用於任何生物，包括原版生物和實體變體，變體會保留自己的名稱。等級會與環境的 `mobs` 區塊疊加：兩者都會乘上生物的屬性。

```json
{
  "entities": ["minecraft:zombie", "#monster"],
  "dimensions": [-1],
  "weight": 10,
  "name": "Brute %s",
  "health": 2.0,
  "damage": 1.5,
  "effects": [{"potion": "minecraft:strength", "amplifier": 1}],
  "loot": 2.0
}
```

| 鍵           | 必填 | 值                      | 預設           | 作用                                                                                                                                       |
| ------------ | ---- | ----------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| `entities`   | 否   | 實體 ID 或類別的清單    | `["#monster"]` | 等級可以落在哪些生物上：如 `minecraft:zombie` 的 ID，或 `#monster`、`#creature`、`#ambient`、`#water_creature`。實體變體用它自己的 ID 指定 |
| `weight`     | 否   | 1 或更大的整數          | `1`            | 該等級相對於指明同一生物的其他等級的機率：它的權重除以總和                                                                                 |
| `name`       | 否   | 字串                    | 無             | 顯示在生物上方的名稱。`%s` 會變成生物自己的名稱。已有自訂名稱的生物會保留它                                                                |
| `health`     | 否   | 大於 0 的數             | `1`            | 乘以生物的最大生命值。生物以滿生命值生成                                                                                                   |
| `damage`     | 否   | 0 或更大的數            | `1`            | 乘以生物的近戰攻擊傷害                                                                                                                     |
| `effects`    | 否   | 清單                    | 無             | 永久效果，每項是帶有 `potion` ID 和 `amplifier`（預設 `0`）的物件。粒子會被隱藏                                                            |
| `loot`       | 否   | 1 或更大的數            | `1`            | 乘以生物掉落的每種可堆疊物品。小數部分是每組多掉一個的機率                                                                                 |
| `dimensions` | 否   | 維度 ID、名稱或數字清單 | 所有維度       | 等級可以抽到的維度，以 ID 或名稱填寫，例如 `-1` 或 `the_nether`。留空表示任何維度                                                          |

工具、盔甲和生物攜帶的裝備不會被乘算，帶有物品欄的生物（例如馱運動物）會被跳過。等級檔案中的錯誤（例如未知的鍵、不存在的實體 ID 或超出範圍的值）會寫入記錄檔，整個檔案會被跳過。刪除等級檔案後，已經抽到該等級的生物維持原樣，沒有任何等級指明的生物不受影響。只有伺服器會讀取等級，所以原版用戶端也可以加入。

## 最大生命值獎勵

*生物與危險*

`<namespace>/hearts/*.json`

在這裡放一個檔案後，玩家可以從你指定的來源獲得額外的最大生命值：某項進度、來自[飲食多樣性](#飲食多樣性)的多樣飲食、吃過的食物清單，或隊伍分數。玩家獲得的部分隨玩家一起儲存。沒有檔案時，什麼都不會改變。

檔名隨意，只讀取資料夾。所有檔案中的 `rewards` 會合併使用。對於每個頂層鍵，使用最先設定它的那個檔案，其餘的會寫入日誌並被忽略。

```json
{
  "startHearts": 10,
  "cap": 20,
  "keepOnDeath": true,
  "style": "bar",
  "sound": "minecraft:entity.player.levelup",
  "particle": "heart",
  "heartColors": ["#E8C33A", "#3AA8E8", "#5FD35F", "#B05FE8"],
  "rewards": [
    { "id": "nether", "advancement": "minecraft:nether/root", "hearts": 1 },
    { "id": "varied", "diet": 4, "hearts": 1, "repeat": "always", "times": 3 },
    { "id": "fish", "foods": ["minecraft:fish", "minecraft:cooked_fish", "#listAllfishraw", "minecraft:bread"], "count": 3, "hearts": 1 },
    { "id": "champ", "team": "red", "objective": "wins", "scoreAtLeast": 5, "hearts": 2, "requires": ["somepack"], "message": "mypack.hearts.champ" }
  ]
}
```

| 鍵            | 必填 | 值                         | 預設                                           | 作用                                                                       |
| ------------- | ---- | -------------------------- | ---------------------------------------------- | -------------------------------------------------------------------------- |
| `startHearts` | 否   | 數字，0.5 或更大，步長 0.5 | `10`                                           | 玩家在獲得任何獎勵之前擁有的心數；`10` 即原版                              |
| `cap`         | 否   | 數字，`startHearts` 或更大 | 無                                             | 心數的總上限，包括初始的心                                                 |
| `keepOnDeath` | 否   | true 或 false              | `true`                                         | `false`：死亡會把所有已獲得的計數歸零                                      |
| `style`       | 否   | `bar` 或 `chat`            | `bar`                                          | 發放獎勵時訊息顯示的位置：動作列或聊天欄                                   |
| `sound`       | 否   | 音效 ID 或 `""`            | `""`                                           | 發放獎勵時在玩家處播放                                                     |
| `particle`    | 否   | 粒子名稱或 `""`            | `""`                                           | 發放獎勵時在玩家處產生；僅限簡單粒子                                       |
| `heartColors` | 否   | `"#RRGGBB"` 的清單         | `["#E8C33A", "#3AA8E8", "#5FD35F", "#B05FE8"]` | 第 2 層、第 3 層及之後各層心形的著色，最後一種顏色會一直重複（見螢幕顯示） |
| `rewards`     | 否   | 物件清單                   | 無                                             | 獎勵，鍵見下表                                                             |

`rewards` 中的每一項：

| 鍵                          | 必填 | 值                            | 預設                 | 作用                                                                                                                     |
| --------------------------- | ---- | ----------------------------- | -------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| `id`                        | 是   | 文字，在檔案內唯一            | 無                   | 在存檔中為獎勵命名，所以修改獎勵時請保留它                                                                               |
| `advancement`               | 否   | 進度 ID                       | 無                   | 觸發條件：獲得該進度                                                                                                     |
| `diet`                      | 否   | int                           | 無                   | 觸發條件：一餐把玩家提升到[飲食多樣性](#飲食多樣性)中某個 `variety` 檔位的這個 `distinct` 數                             |
| `foods`                     | 否   | 物品 ID 或 `#ore` 名稱的清單  | 無                   | 觸發條件：要吃的食物。每一項在第一次吃到與之符合的任何食物時計一次；`#ore` 項算一種食物                                  |
| `count`                     | 否   | int，從 1 到 `foods` 的長度   | `foods` 的長度       | 與 `foods` 一起使用：需要吃過多少項                                                                                      |
| `team`                      | 否   | 隊伍名稱                      | 無                   | 觸發條件：玩家在這支隊伍中                                                                                               |
| `objective`, `scoreAtLeast` | 否   | 文字，int                     | 無                   | 觸發條件：玩家在該目標上的分數至少為此值。兩者都需要；與 `team` 一起使用時必須同時成立                                   |
| `dietAtLeast`               | 否   | int                           | 無                   | 觸發條件：玩家記住的餐食中至少有這麼多種不同的食物（見飲食多樣性）。需要飲食多樣性檔案；與 `team` 一起使用時必須同時成立 |
| `hearts`                    | 是   | 大於 0 的數字，步長 0.5       | 無                   | 一次發放給的心數                                                                                                         |
| `repeat`                    | 否   | `once_per_player` 或 `always` | `once_per_player`    | `always`：每次新的觸發都會再次發放                                                                                       |
| `times`                     | 否   | int，1 或更大                 | `1`，`always` 時不限 | 一名玩家最多能從這項獎勵獲得的次數                                                                                       |
| `requires`                  | 否   | 模組 id 或資源包命名空間清單  | 無                   | 除非全部存在，否則該獎勵會被略過                                                                                         |
| `message`                   | 否   | 語言鍵或 `""`                 | `rdpl.hearts.gained` | 發放時顯示；`""` 則什麼都不顯示                                                                                          |

**每項獎勵只有一個觸發條件。** 在 `advancement`、`diet`、`foods`，或條件鍵 `team`、`objective`、`scoreAtLeast` 與 `dietAtLeast` 中恰好設定一個。否則日誌會指出檔案和 `id`，該獎勵被略過。`advancement` 在獲得該進度時發放。`diet` 在一餐把玩家從低於該 `distinct` 數提升到該數或更高時發放；它需要一個含有該 `distinct` 的 `variety` 檔位的飲食多樣性檔案，沒有就永遠不會發放。`foods` 在吃過的項數達到 `count` 時只發放一次；`repeat` 和 `times` 對它不適用。隊伍、分數或飲食條件在由假變真時發放，只要存在這樣的獎勵，就每 5 秒檢查一次。

**隨玩家一起儲存。** 發放次數按獎勵記錄在玩家自己的存檔資料中，所以獲得的心在死亡、登出和切換維度後都會保留。額外的最大生命值也隨玩家一起儲存，所以最大生命值為 24 的玩家在 23 生命值時登出，回來時仍是 23。最大值是 `startHearts` 加上每次發放的心數，不會超過 `cap`；原版的十顆心就是 `startHearts: 10`。

**生命值。** 最大值上升時，目前生命值按相同比例上升。下降時，生命值被削減到新的最大值。死亡後重生會把生命值補滿。

**死亡。** `keepOnDeath` 為 `true` 時，獲得的心會保留。為 `false` 時，死亡會把所有計數歸零並忘記吃過的食物，獎勵只能透過新的觸發再次獲得。

**補發。** 登入時和 `/rdpl reload` 之後，每名在線玩家的心數會根據儲存的計數和目前規則重新計算。玩家還沒有計數、而條件已經滿足的獎勵（進度已完成、飲食檔位已達到、吃過的食物達到 `count`、隊伍或分數條件成立）會在那時補發，不顯示訊息。因死亡被歸零的獎勵不會再次補發。從檔案中移除的獎勵會在存檔中保留計數，但缺失期間不給任何東西，回來後會重新計算。

**螢幕顯示。** 生命值超過 20 不會產生新的心形列。每多 10 顆心就是一層，畫在同一個第一列之上：第二層用 `heartColors` 的第一種顏色，第三層用第二種，依此類推。處於狀態外觀（中毒、凋零、冰凍）的層會原樣顯示該原版外觀，不著色，傷害吸收則像原版一樣位於這一列之上。顏色來自伺服器，在登入時和重新載入後傳送；沒有收到的客戶端使用預設清單。

## 工作訂單

*生物與危險*

`<namespace>/orders/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。一個檔案包含一份 `orders` 清單，旁邊還有三項作用於整個資源包的設定；由第一個設定它的檔案決定。

訂單是一項工作，由列為其接單者的實體變體為玩家完成：它們在訂單範圍內挖掘指定的方塊，或從其他箱子裡搬來指定的物品，再把所得放進訂單的箱子。每種開啟訂單的方式都能在原版用戶端上使用：

- **告示牌。** 在箱子上或箱子旁放一塊告示牌，在第一行寫上訂單的 `sign` 字詞。訂單就在該箱子處開啟，告示牌第二行顯示還需要多少物品。破壞告示牌或箱子會取消訂單。
- **工具。** 不潛行時，用訂單 `tool` 種類的工具對工人按右鍵。工人會收下工具，它原本拿著的工具會還給你，然後它會在自身範圍內最近的箱子處執行該訂單。
- **庫存箱。** 在鐵砧中把箱子改名為 `stockName` 並放下。它第一列的每個物品都會補滿一疊：工人從資源包中第一個 `haul` 訂單範圍內的其他箱子裡取來，沿用該訂單的接單者、優先順序和範圍。
- **村莊地塊。** 指定了 `orderSign` 的地塊會自己立起告示牌。箱子必須在告示牌正下方或旁邊，而且地塊的模板必須包含它。訂單 `takers` 所指的任何空閒工人（沒有被任何玩家僱用、也不在任何隊伍裡的工人）都可以接手，訂單也會像其他訂單一樣產生空閒工人。因為沒有玩家開啟它，所以不發放積分、戰利品或函數；設定 `standing` 可讓它保持開啟。字詞與任何訂單都不相符的告示牌，會在其區塊載入時記錄一則錯誤。在旋轉後的結構地圖地塊上，告示牌相對於整張地圖的佔地範圍放置，因此若部件小於其單元格，告示牌會偏移單元格大小減去部件寬度的距離。

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
      "wins": "mypack:ore_done",
      "points": 5,
      "loot": "mypack:miner_reward",
      "takers": ["mypack:miner", "team:Red", "tag:digger"]
    },
    {
      "job": "farm",
      "blocks": "cropWheat",
      "sign": "Farm",
      "tool": "hoe",
      "standing": 64,
      "area": 12,
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

`orders` 旁邊的設定：

| 鍵          | 必填 | 值       | 預設    | 作用                                                                                                                             |
| ----------- | ---- | -------- | ------- | -------------------------------------------------------------------------------------------------------------------------------- |
| `stockName` | 否   | 字串     | `Stock` | 讓放下的箱子成為庫存箱的名稱，不分大小寫。留空則關閉庫存箱                                                                       |
| `hire`      | 否   | 物品名稱 | 無      | 雇用閒置工人：不潛行時手持此物品對它按右鍵，它就歸你，物品會被消耗。閒置工人不屬於任何玩家或隊伍。中繼資料寫作 `minecraft:dye:4` |
| `spawnCap`  | 否   | 整數     | `1`     | 沒有工人前來時，一個訂單最多可在其箱子處生成多少工人。`0` 表示不生成                                                             |

訂單的鍵：

| 鍵           | 必填            | 值                                 | 預設    | 作用                                                                                                                                                                                                                |
| ------------ | --------------- | ---------------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `job`        | 是              | `gather`、`mine`、`farm` 或 `haul` |         | `gather` 和 `mine` 挖掘 `blocks` 指定的方塊（以方塊本身或其掉落物比對），只要有一面暴露在空氣或非完整方塊旁。`farm` 收穫成熟作物，並用其自身的種子補種。`haul` 把 `blocks` 指定的物品從其他箱子和容器搬進訂單的箱子 |
| `blocks`     | 是，`farm` 除外 | 礦物辭典名稱                       | 無      | 訂單想要的東西，例如 `oreIron`、`logWood` 或 `cropWheat`，不分大小寫。在 `farm` 訂單上，它只收穫會掉落該物品的作物                                                                                                  |
| `area`       | 否              | 整數                               | `16`    | 工作從箱子起延伸多遠，按每條軸的方塊數計                                                                                                                                                                            |
| `areaByTier` | 否              | 整數清單                           | 無      | 依工人工具的挖掘等級決定範圍：第一項用於無工具或等級 0，之後每級一項，超出清單的等級都用最後一項。會覆寫 `area`                                                                                                     |
| `deliver`    | 否              | `chest` 或 `self`                  | `chest` | `chest` 把收集到的東西送進訂單的箱子。`self` 把它留在工人自己的儲存空間裡，訂單在收集時計數                                                                                                                         |
| `limit`      | 否              | 整數                               | `64`    | 訂單關閉前需要多少物品                                                                                                                                                                                              |
| `standing`   | 否              | 整數                               | `0`     | 大於 `0` 時訂單永不關閉：它讓箱子裡保持這麼多物品，少了就工作。此時不會讀取 `limit`                                                                                                                                 |
| `wins`       | 否              | 函式                               | 無      | 訂單完成時，以每位獲得獎勵的玩家身分執行：開啟訂單的玩家，或為隊伍開啟時該隊伍的每位線上成員                                                                                                                        |
| `points`     | 否              | 整數                               | `0`     | 訂單完成時記給每位獲得獎勵的玩家的點數，計入每個開啟了 `points.order` 的計分項。永不完成的訂單不支付任何點數                                                                                                        |
| `loot`       | 否              | 戰利品表                           | 無      | 訂單完成時，為每位獲得獎勵的玩家各抽取一次的戰利品表。物品進入其物品欄，物品欄已滿時掉落在其腳下                                                                                                                    |
| `workers`    | 否              | 整數                               | `1`     | 同時處理該訂單的最多工人數                                                                                                                                                                                          |
| `priority`   | 否              | 整數                               | `0`     | 閒置工人先接優先順序最高的開放訂單，其次是最近的                                                                                                                                                                    |
| `takers`     | 是              | 清單                               |         | 誰可以處理它：變體名稱、`team:<team>` 表示計分板隊伍的任一成員，或 `tag:<tag>` 表示帶有該標籤的任一實體                                                                                                             |
| `sign`       | 否              | 字串                               | 無      | 告示牌第一行上開啟該訂單的字詞，不分大小寫                                                                                                                                                                          |
| `speed`      | 否              | 數字                               | `1`     | 乘上工人的挖掘速度。`2` 比拿同樣工具的玩家快一倍                                                                                                                                                                    |
| `tool`       | 否              | 工具種類                           | 無      | 透過按右鍵把此訂單交給工人的工具種類，例如 `pickaxe`、`axe`、`shovel` 或 `hoe`。工具無法採集某方塊的工人會回箱子取一把這種工具                                                                                      |

**工人如何工作。** 被接單者點名的變體，或在任一接單者是隊伍或標籤時的所有變體，閒置時每秒尋找一次工作。它會接下箱子位於自身範圍外加 32 格以內的最佳開放訂單，以租約持有，停止工作時租約失效，儲存和重新載入後租約仍然保留。挖掘耗時與手持工人工具的玩家相同，效率計入在內，幸運影響掉落；絲綢之觸則不計入。挖到的東西在變體有 `storage` 物品時放進其中，否則放進副手，絕不掉在地上。拿不下時，它會走到箱子把所有東西放進去。每挖一個方塊都會磨損工具，工具損壞的工人會回箱子取一把同種類的新工具，沒有則休息一分鐘。工人死亡時會掉落工具和手中的物品，其儲存內容像往常一樣只會灑出一次。`mobGriefing` 不會阻止它，因為這項工作是玩家要求的。

**誰為誰工作。** 開啟訂單的玩家在計分板隊伍中時，只有該隊伍的工人會接單。否則訂單歸該玩家所有，而工人歸第一個給它工作的玩家，無論是透過工具、透過 `hire`，或接下該玩家的訂單；此後它只處理該玩家的訂單。被隊伍中的玩家雇用或遞給工具的閒置工人會加入該隊伍。

**在箱子處生成。** 一個訂單十秒沒有工人時，會在其箱子旁生成一個它的變體接單者，每個訂單最多 `spawnCap` 個。生成的工人屬於訂單的隊伍或玩家。

## 曝露設定

*生物與危險*

`<namespace>/exposures/*.json`

檔案路徑即為危害的名稱，其死亡訊息取自語言鍵 `death.attack.rdpl.<file name>`。

由資源包定義的危害：指定的方塊、物品、維度、生態域、結構、洞穴區域與高度，會讓站在這些方塊附近、攜帶這些物品或身處這些維度的玩家受到曝露，曝露分為多個等級，每個等級會施加效果與週期性傷害。危害也可以從附近的生物與玩家身上感染，或隨雨水降下（[傳染與天氣](#傳染與天氣)）。一個檔案定義一種危害，多種危害可同時運作。各鍵的預設值是 Immersive World 輻射所用的數值。

```json
{
  "blocks": [ "mypack:nuclear_waste=2", "mypack:uranium_ore" ],
  "items": [ "mypack:nuclear_waste" ],
  "dimensions": [ "-1" ],
  "biomes": [ "minecraft:desert=2", "#SANDY" ],
  "structures": [ "Village", "mypack:ruin=2" ],
  "caveRegions": [ "mypack:glowcave=2" ],
  "heights": [ "-64:0=2", "200:=1" ],
  "immunity": "mypack:antirad",
  "immuneItems": [ "mypack:hazmat_helmet", "mypack:hazmat_chest" ],
  "immuneTeams": [ "scientists" ],
  "onClear": "mypack:rad/clear",
  "scanInterval": 20,
  "range": 10,
  "sourcesForNextLevel": 4,
  "skipsCreative": true,
  "levels": [
    { "effect": "mypack:radiation_1", "onEnter": "mypack:rad/low", "damage": 4.0, "damageInterval": 160,
      "effects": [ { "potion": "minecraft:nausea", "duration": 0, "amplifier": 0, "ambient": false, "showParticles": false },
                   { "potion": "minecraft:hunger" } ] },
    { "effect": "mypack:radiation_2", "onEnter": "mypack:rad/high", "damage": 8.0, "damageInterval": 120,
      "effects": [ { "potion": "minecraft:nausea", "amplifier": 1 }, { "potion": "minecraft:hunger", "amplifier": 1 } ] }
  ]
}
```

| 鍵                    | 必填   | 值                                                | 預設   | 作用                                                                                                                                                             |
| --------------------- | ------ | ------------------------------------------------- | ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blocks`              | 十選一 | 清單，內容為 `block` 或 `block=level`             |        | 讓站在附近的玩家受到曝露的方塊。未指定等級則為 1                                                                                                                 |
| `items`               | 十選一 | 清單，內容為 `item` 或 `item=level`               |        | 讓攜帶或穿戴的玩家受到曝露的物品                                                                                                                                 |
| `dimensions`          | 十選一 | 清單，內容為 `dim` 或 `dim=level`                 |        | 讓身處其中的任何玩家受到曝露的數字維度 id                                                                                                                        |
| `biomes`              | 十選一 | 清單，內容為 `biome` 或 `biome=level`             |        | 讓站在其中的任何玩家受到曝露的生態域。`#` 表示 BiomeDictionary 類型                                                                                              |
| `environments`        | 十選一 | 清單，內容為 `environment` 或 `environment=level` |        | 啟用期間讓該維度中任何玩家受到曝露的環境，名稱與維度檔案中的相同。`default` 也算。不寫等級時為 1                                                                 |
| `structures`          | 十選一 | 清單，內容為 `structure` 或 `structure=level`     |        | 讓身處其內部的玩家受到曝露的結構。資源包結構在 `range` 個方塊範圍內生效                                                                                          |
| `caveRegions`         | 十選一 | 清單，內容為 `region` 或 `region=level`           |        | 讓身處其中的玩家受到曝露的洞穴區域。只寫名稱時使用本檔案的命名空間                                                                                               |
| `heights`             | 十選一 | 清單，內容為 `from:to=level`                      |        | 讓身處其中的玩家受到曝露的 Y 軸區間。某一側留空表示沒有限制                                                                                                      |
| `levels`              | 是     | 清單，內容為等級                                  |        | 嚴重程度的階梯，第一項為等級 1。玩家取得任一來源所達到的最高等級                                                                                                 |
| `immunity`            | 否     | 藥水名稱                                          | 無     | 帶有此效果的對象完全不會受到曝露                                                                                                                                 |
| `immuneItems`         | 否     | 物品名稱清單                                      | 無     | 穿戴或手持其中任一物品（盔甲欄與雙手）的實體完全不會受到曝露，與 `immunity` 相同。每個護甲部位指定一件即可，任一件都足夠。無法辨識的物品名稱會記錄為錯誤並略過。 |
| `immuneTeams`         | 否     | 計分板隊伍名稱清單                                | 無     | 這些隊伍的成員（套件中的隊伍檔案或 `/scoreboard teams`）不會受到曝露。無法辨識的隊伍名稱永遠不會符合。                                                           |
| `onClear`             | 否     | 函式名稱                                          | 無     | 玩家的等級降到 0 時以該玩家身分執行，包括切換到創造模式時。                                                                                                      |
| `scanInterval`        | 否     | 刻                                                | `20`   | 檢查周圍環境與物品欄的頻率                                                                                                                                       |
| `range`               | 否     | 方塊                                              | `10`   | 方塊曝露的影響範圍，球形                                                                                                                                         |
| `sourcesForNextLevel` | 否     | 整數                                              | `0`    | 附近同一等級的來源達到此數量，就會使該等級再升高一級。`0` 表示關閉此功能                                                                                         |
| `skipsCreative`       | 否     | 布林值                                            | `true` | 創造與旁觀模式的玩家不受影響                                                                                                                                     |

### 等級

*曝露設定*

每個等級：

| 鍵               | 必填 | 值               | 預設  | 作用                                                                             |
| ---------------- | ---- | ---------------- | ----- | -------------------------------------------------------------------------------- |
| `effect`         | 是   | 藥水名稱         |       | 標記玩家處於該等級的效果。此效果的存在會觸發傷害，因此應使用資源包為此定義的效果 |
| `damage`         | 否   | 半顆心           | `0`   | 該等級持續期間，每隔 `damageInterval` 刻造成一次傷害。無視護甲                   |
| `damageInterval` | 否   | 刻               | `160` | 該傷害的施加頻率                                                                 |
| `effects`        | 否   | 清單，內容為效果 | 無    | 一併施加的額外效果，格式與藥水類型相同。未設定 `duration` 時，持續時間隨掃描視窗 |
| `onEnter`        | 否   | 函式名稱         | 無    | 每當等級變為此等級時以該玩家身分執行，無論等級升高或降低。                       |

免疫有三種形式，任一種都會讓攜帶者不受曝露：`immunity` 藥水、穿戴或手持的 `immuneItems` 之一，或屬於 `immuneTeams` 中的某個隊伍。等級變化時會執行新等級 `onEnter` 指定的函式；等級降到 0 時則執行 `onClear`。函式以權限等級 2 執行，@s 為該玩家，並且在該等級的標記效果套用之後執行。加入後的第一次檢查只設定起始等級而不觸發，因此進入時已處於曝露狀態的玩家在等級變化前不會執行任何內容；重生會保留等級。同一變化也會觸發 `exposure_level` 卡牌觸發器。

等級效果會比下一次掃描略晚結束，因此玩家離開後效果會自行消退。因曝露傷害而死亡時，訊息取自 `death.attack.rdpl.<file name>`，由資源包的語言檔案提供。

在玩家腳下成立的來源，每次掃描都會重新檢查，因此玩家一離開，曝露就立即結束。

### 傳染與天氣

*曝露設定*

另外兩種來源，寫在同一個檔案中。帶原者與受到曝露的帶有者會把危害傳給周圍的感染對象，而雨或雷雨會讓被淋到的玩家受到曝露。

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

| 鍵                  | 必填   | 值                                      | 預設               | 作用                                                                                              |
| ------------------- | ------ | --------------------------------------- | ------------------ | ------------------------------------------------------------------------------------------------- |
| `carriers`          | 十選一 | 清單，內容為 `entity` 或 `entity=level` |                    | 一律以該等級傳播危害的生物，或 `minecraft:player`。未指定等級則為 1                               |
| `contagious`        | 否     | 布林值                                  | `false`            | 任何受到曝露的對象都會以其所處等級傳播危害                                                        |
| `catchers`          | 否     | 清單，內容為實體名稱                    | `minecraft:player` | 可被感染的對象。生物只會從帶原者與帶有者身上感染，不會從方塊、物品或天氣感染                      |
| `contagionRange`    | 否     | 方塊                                    | `4`                | 帶原者或帶有者的影響範圍，球形                                                                    |
| `contagionChance`   | 否     | `0` 到 `1`                              | `0.1`              | 每次掃描帶原者或帶有者時，範圍內每個感染對象被感染的機率                                          |
| `contagionDuration` | 否     | 刻                                      | `1200`             | 被感染的危害維持所感染等級的時間。再次感染會重新計時                                              |
| `weather`           | 十選一 | 清單，內容為 `kind` 或 `kind=level`     |                    | `rain` 讓被雨淋到的玩家受到曝露：頭頂為開闊天空，且位於會下雨的生態域。`thunder` 在暴風雨期間計算 |
| `weatherDimensions` | 否     | 清單，內容為 `dim`                      | 所有維度           | 天氣會造成曝露的數字維度 id                                                                       |

被感染的危害在掃描中算作多一個來源，與其他來源一樣取最高等級，`immunity` 同樣能防護。除非 `contagionRange` 與 `contagionChance` 都大於 `0`，且檔案列出 `carriers` 或設定 `contagious`，否則不會有任何傳播；而且只有在某個檔案這樣設定時，才會去檢查生物。

---

# 世界

## 世界範本

*世界*

`<namespace>/worldtemplates/*.json`

檔案路徑即為範本名稱，設定選項 `worldTemplate` 可以直接指定此名稱來選用它。

把世界的形態集中在一個檔案裡，讓資源包一次提供完整的世界，而不必要求玩家調整十幾個設定選項。

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

| 鍵           | 必填 | 值                                  | 預設     | 作用                                 |
| ------------ | ---- | ----------------------------------- | -------- | ------------------------------------ |
| `name`       | 否   | 字串                                | 檔案名稱 | 顯示在日誌與報告中                   |
| `default`    | 否   | 生態域名稱或 `void`                 | `void`   | 填補被封鎖移除之生態域的內容         |
| `roles`      | 否   | 角色對應生態域的物件                | 無       | 擔任特定角色的生態域，例如海洋或河流 |
| `structures` | 否   | [結構名稱](#值清單)對應布林值的物件 | 無       | 開啟或關閉的原版結構                 |
| `settings`   | 否   | 物件                                | 無       | 範本所設定的設定值                   |
| `dimensions` | 否   | 清單，內容為整數                    | 所有維度 | 適用的維度                           |

`settings` 使用與設定檔相同的鍵名，因此不需要另外學習對照表。

目前啟用哪個範本，由設定選項 `worldTemplate` 決定。保持 `auto` 時，由提供範本的最高優先順序資源包勝出，其順序與其他所有內容相同。在該選項指定範本，則直接選用它。

## 遊戲規則

*世界*

`<namespace>/gamerules/*.json`

檔案名稱由你決定，只會讀取資料夾，多個檔案會疊加。

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

每個鍵是規則所屬世界的 id：`0` 為主世界，`-1` 為地獄，`1` 為終界，其他則是模組為自己的維度所用的 id。值為字串，與 `/gamerule` 指令相同，因此寫成 `"false"` 而不是 `false`。這些規則會套用到新世界。維度檔案則改以 `gameRules` 區段攜帶相同的規則，且只會套用於該世界。

## 生態域

*世界*

`<namespace>/biomes/*.json`

檔案路徑即為生態域的登錄名稱，因此 `mypack/biomes/ruby_forest.json` 會登錄 `mypack:ruby_forest`。`name` 只是顯示給玩家看的名稱。

一次列出所有鍵。實際的檔案只需寫入用得到的鍵。

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
  "craters": { "chance": 0.1, "minRadius": 8, "maxRadius": 16, "depth": 6 },
  "requires": ["mypack"]
}
```

### 該生態域

*生態域*

| 鍵          | 必填 | 值                                   | 預設     | 作用                                                                                                                                                         |
| ----------- | ---- | ------------------------------------ | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`      | 否   | 字串                                 | 檔案名稱 | 顯示給玩家的名稱                                                                                                                                             |
| `id`        | 否   | 整數                                 | 自動指派 | 固定的生態域 id。只有需要它保持穩定時才設定                                                                                                                  |
| `types`     | 否   | 清單，內容為字典類型                 | 推測     | 以這些類型登錄生態域，例如 `FOREST`、`COLD`、`WET` 或 `NETHER`，讓其他模組能找到它。省略時，Forge 會依生態域的樹木數量、高度、溫度、降雨量與地面方塊盡量推測 |
| `baseBiome` | 否   | 生態域名稱                           | 無       | 要複製設定的既有生態域                                                                                                                                       |
| `requires`  | 否   | 清單，內容為模組 id 或資源包命名空間 | 無       | 除非全部都存在，否則會跳過此檔案                                                                                                                             |

### 氣候

*生態域*

| 鍵            | 必填 | 值             | 預設    | 作用                                      |
| ------------- | ---- | -------------- | ------- | ----------------------------------------- |
| `temperature` | 否   | 浮點數         | `0.5`   | 低於 0.15 會下雪，高於 1.0 則如沙漠般炎熱 |
| `rainfall`    | 否   | 浮點數，0 到 1 | `0.5`   | 潮濕程度                                  |
| `rain`        | 否   | 布林值         | `true`  | 是否會有天氣變化                          |
| `snow`        | 否   | 布林值         | `false` | 降雨時是否以雪的形式落下                  |

### 地面與顏色

*生態域*

| 鍵                | 必填 | 值           | 預設       | 作用                                     |
| ----------------- | ---- | ------------ | ---------- | ---------------------------------------- |
| `baseHeight`      | 否   | 浮點數       | `0.1`      | 地形高度。海平面為 0，平原為 0.125       |
| `heightVariation` | 否   | 浮點數       | `0.2`      | 起伏程度                                 |
| `topBlock`        | 否   | 方塊名稱     | 草方塊     | 表面方塊                                 |
| `fillerBlock`     | 否   | 方塊名稱     | 泥土       | 表面正下方的方塊                         |
| `stoneBlock`      | 否   | 方塊名稱     | 石頭       | 地面的主體                               |
| `waterColor`      | 否   | 十六進位顏色 | `FFFFFF`   | 水的色調                                 |
| `grassColor`      | 否   | 十六進位顏色 | 依氣候     | 草的色調，取代由溫度與降雨量得出的顏色   |
| `foliageColor`    | 否   | 十六進位顏色 | 依氣候     | 樹葉的色調，作法相同                     |
| `snowColor`       | 否   | 十六進位顏色 | 維度的設定 | 地面積雪的色調，優先於維度的 `snowColor` |

### 裝飾與生成

*生態域*

| 鍵                  | 必填 | 值                                                                                     | 預設     | 作用                                                                                                                                                                                                                                                                                                 |
| ------------------- | ---- | -------------------------------------------------------------------------------------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `decoration`        | 否   | 物件                                                                                   | 原版數量 | 每個區塊的數量。可讀取的名稱有 `trees`、`flowers`、`grass`、`deadbush`、`mushrooms`、`bigmushrooms`、`reeds`、`cacti`、`sand`、`gravel`、`clay` 與 `waterlily`，另外還有 `falls`（大於零表示會生成湖泊與泉水），以及 `extratreechance`（多生成一棵樹的百分比機率）。其他任何名稱都會記錄到日誌並忽略 |
| `spawns`            | 否   | 清單，內容為物件                                                                       | 原版清單 | 見下文                                                                                                                                                                                                                                                                                               |
| `keepDefaultSpawns` | 否   | 布林值                                                                                 | `false`  | 保留原版的清單，與你的並用                                                                                                                                                                                                                                                                           |
| `spawnChance`       | 否   | 浮點數，小於 1                                                                         | `0.1`    | 土地初次生成時，再放置另一群生物的機率。只要成功，遊戲就會持續擲骰，因此 1 永遠不會停止，會一直填到世界沒有空間為止。大於或等於 0.99 的值會被拒絕，改用 0.99                                                                                                                                         |
| `spawnRates`        | 否   | 物件，將 `surfaceDay`、`surfaceNight`、`undergroundDay`、`undergroundNight` 對應到倍率 | 無       | 此處敵對生物的生成頻率，取代全域設定。見下文                                                                                                                                                                                                                                                         |

生成項目包含 `entity`（必填）、`type`（`creature`，為[生物類型](#值清單)之一）、`weight`（`10`）、`min`（`1`）與 `max`（`min`）。

`spawnRates` 只針對敵對生物，不涉及其他任何東西。它只接受四個鍵，沒有其他鍵：`surfaceDay` 與 `surfaceNight` 用於看得見天空的地方，`undergroundDay` 與 `undergroundNight` 用於看不見天空的地方。每個值都是敵對生物允許出現頻率的倍率，`1` 為一般頻率，`0` 完全阻止生成，小於 1 會減少部分嘗試，大於 1 則放行遊戲原本會拒絕的嘗試，因此 `2` 表示兩倍。省略某個鍵，表示該生態域不做決定，改用該時間與地點的全域設定。此處寫入的其他任何內容都不是鍵，會被忽略，因此以生物類型命名的頻率完全不會有作用。

### 生成位置

*生態域*

| 鍵            | 必填 | 值                                       | 預設 | 作用                                                           |
| ------------- | ---- | ---------------------------------------- | ---- | -------------------------------------------------------------- |
| `placement`   | 否   | 物件                                     | 無   | 生成的位置。見下文                                             |
| `villageType` | 否   | `oak`、`sandstone`、`acacia` 或 `spruce` | 無   | 位於此處的村莊使用的建材。留空則使用橡木建造，與沒有此鍵時相同 |

`placement`：

| 鍵             | 必填 | 值     | 預設    | 作用                         |
| -------------- | ---- | ------ | ------- | ---------------------------- |
| `climate`      | 否   | 字串   | 無      | 它所加入的原版氣候群組       |
| `weight`       | 否   | 整數   | `10`    | 與相鄰生態域相比被選中的頻率 |
| `villages`     | 否   | 布林值 | `false` | 可以生成村莊                 |
| `villageSpawn` | 否   | 布林值 | `true`  | 村民可以在其中生成           |
| `strongholds`  | 否   | 布林值 | `false` | 可以生成要塞                 |
| `playerSpawn`  | 否   | 布林值 | `false` | 世界出生點可以設在這裡       |

### 高度帶與空島

*生態域*

| 鍵             | 必填 | 值                     | 預設       | 作用                                                                                                                                                                                                                    |
| -------------- | ---- | ---------------------- | ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `minHeight`    | 否   | 整數                   | 無         | 此生態域作為 3D 生態域接管的最低 y 值。設定任一高度，就會把生態域變成一個高度帶：帶外的直行保有自己的生態域，帶內世界中每個 4 乘 4 乘 4 的單元都會回報此生態域。僅限 Rubic 世界，且在造陸時套用，因此既有的土地保持原狀 |
| `maxHeight`    | 否   | 整數                   | 無         | 該高度帶的最高 y 值                                                                                                                                                                                                     |
| `replaces`     | 否   | 清單，內容為生態域名稱 | 所有生態域 | 把高度帶限制在其自身生態域在此列出的直行，因此高山帶可以只覆蓋山地，不影響其他地方                                                                                                                                      |
| `skyStone`     | 否   | 方塊名稱               | 世界設定   | 此生態域適用處，空島表面之下所用的方塊。在高度帶上，`topBlock` 與 `fillerBlock` 會用它塗出島嶼表面，因此高度帶是讓一段天空擁有自己島嶼的方法                                                                            |
| `skyIslands`   | 否   | 浮點數，`-1` 到 `1`    | 世界設定   | 此生態域適用處的島嶼門檻。越低則聚集越多陸地                                                                                                                                                                            |
| `skyThickness` | 否   | 浮點數，`0` 到 `8`     | 世界設定   | 此生態域適用處島嶼的堅實程度                                                                                                                                                                                            |

### 隕石坑

*生態域*

```json
{
  "craters": { "chance": 0.1, "minRadius": 8, "maxRadius": 16, "depth": 6 }
}
```

`craters` 物件會在該生態域所在之處的地面壓出圓形的坑，就像月球布滿隕石坑一樣。沒有它，該生態域就沒有隕石坑。隕石坑是地形首次生成時的一部分，依據世界種子放置，已經存在的地形維持原樣。隕石坑要麼完整，要麼不存在，絕不會被區塊邊界截斷：村莊附近是否形成隕石坑，在其任一區塊首次生成時就已決定，並儲存在存檔裡，因此之後的每個區塊都與之一致，重新啟動後也一樣。它們在該生態域出現的每個維度中都有效，無論是主世界、地獄、終界還是平坦世界，也無論是資源包自有的維度還是原版維度，而且不需要其他模組。

隕石坑中心所在的生態域決定它是否形成以及有多大，所以靠近生態域邊緣的隕石坑會稍微伸進相鄰的生態域。坑從地面向下挖，中心最深，向邊緣陡峭地升起，隨後地形自身的地表處理會從坑底上方走過。當該處理像主世界那樣鋪設生態域的地面時，由該生態域的 `topBlock` 和 `fillerBlock` 覆蓋坑底；地獄、終界和平坦維度不鋪設地面，所以那裡的坑底就是下面的石頭或地層。在水下或熔岩中，坑會被該液體填滿。隕石坑絕不會挖穿一列中最後一個固體方塊。

隕石坑絕不會出現在村莊、其道路、城市街道和鐵路連接（包括尚待鋪設的）或[結構地圖](#結構地圖)周圍 12 格以內，這樣它們就能立在為其規劃的地面上。這適用於原版、Biomes O' Plenty 和 Realistic Terrain Generation 生成器的村莊。

| 鍵          | 必填 | 值                 | 預設  | 作用                                                               |
| ----------- | ---- | ------------------ | ----- | ------------------------------------------------------------------ |
| `chance`    | 否   | 浮點數，`0` 到 `1` | `0.1` | 每個區塊包含隕石坑中心的可能性。`0` 會關閉隕石坑                   |
| `minRadius` | 否   | 整數，`2` 到 `64`  | `8`   | 隕石坑的最小半徑，單位為方塊                                       |
| `maxRadius` | 否   | 整數，`2` 到 `64`  | `16`  | 最大半徑，絕不低於 `minRadius`。每個隕石坑在兩者之間均勻地選取半徑 |
| `depth`     | 否   | 整數，`1` 到 `32`  | `6`   | 坑中心有多少個方塊深。越靠近邊緣越淺                               |

### 隨高度變化的溫度

*生態域*

**隨高度變化的溫度。** 生態域越高越冷，這讓山頂積雪，並在某條線以上不再下雨。三個 `terrain` 鍵可以移動這條曲線，這在 Rubic 世界很重要，因為那裡的地面可能遠高於或遠低於遊戲預設的高度。預設值就是遊戲原本的行為，因此資源包若不去動它們，就不會有任何改變。

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

| 鍵                             | 值     | 預設        | 作用                                                                                                           |
| ------------------------------ | ------ | ----------- | -------------------------------------------------------------------------------------------------------------- |
| `biomeTemperatureCenterY`      | 整數   | `64`        | 曲線的高度基準。等於或低於此高度時，生態域直接回報自己的 `temperature`                                         |
| `biomeTemperatureHeightFactor` | 浮點數 | `-0.001667` | 在該高度以上每增加一格方塊，溫度的變化量，即遊戲本身每 30 格方塊 0.05 的比率。負值使溫度隨海拔降低，正值則升高 |
| `biomeTemperatureScaleMaxY`    | 整數   | `256`       | 曲線停止的高度，因此比遊戲本身更高的世界不會一路降溫到頂端                                                     |

## 維度

*世界*

`<namespace>/dimensions/*.json`

檔案路徑為維度的 `suffix` 命名，其預設為 `DIM_<name>`。維度本身是依其 `id` 找到的，所以其他所有地方引用的都是這個數字。

一次列出所有鍵。實際的檔案只需寫入用得到的鍵。

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
    "biome": "mypack:ruby_forest",
    "biomeList": ["minecraft:plains", "minecraft:forest", "mypack:ruby_forest"]
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
    "moon": { "texture": "mypack:textures/environment/pink_moon_phases.png", "size": 20 },
    "bodies": [
      { "texture": "mypack:textures/environment/twin_moon.png", "size": 12, "angle": 150, "tilt": 20, "body": "ash" },
      { "texture": "mypack:textures/environment/home.png", "size": 6, "angle": 20, "tilt": 40, "followsTime": false }
    ],
    "stars": { "count": 6000, "size": 0.12 },
    "cloudSpeed": 2.0,
    "cloudLayers": [
      { "height": 140, "speed": 1.0, "color": "#FFFFFF" },
      { "height": 220, "speed": -3.0, "color": "#C0A0FF" }
    ],
    "fogDensity": 0.4,
    "fogGroundWeight": 0.6,
    "lightSkyColor": "#FFD8A0",
    "lightBlockColor": "#A0C0FF",
    "skyFactor": 0.7,
    "sunBrightness": 0.5,
    "moonBrightness": 0.3,
    "heat": { "strength": 0.1, "minTemperature": 1.5, "dayOnly": true, "mode": "world", "startDistance": 32 }
  },
  "physics": { "gravity": 0.4, "fallDamage": 0.5, "arrowGravity": 0.3 },
  "time": { "dayLength": 36000 },
  "weather": {
    "precipitation": true,
    "lightning": true,
    "snow": false,
    "freeze": false,
    "alwaysRain": false,
    "cycle": { "rainTicks": [1000, 4600], "clearTicks": [1000, 3000], "maxStrength": 0.6, "thunderTicks": [3600, 15600], "calmTicks": [12000, 60000], "thunderStrength": 1.0 },
    "rain": { "particle": "droplet", "sound": "minecraft:weather.rain", "volume": 0.2, "interval": 3, "color": "#88AAFF", "colorMode": "solid", "snowColor": "#FFFFFF", "angle": 30, "heading": 90, "splashUpward": false },
    "wind": { "gust": 15, "every": [200, 600], "swing": 30 }
  },
  "ambience": { "music": "mypack:music.ruby", "musicDelay": [1200, 3600], "loopSound": "mypack:ambient.ruby_wind", "ambientSound": "minecraft:ambient.cave", "soundChance": 0.0111, "particle": "reddust", "particleChance": 0.00625, "particleColor": "#FF4060" },
  "gameRules": { "doMobSpawning": "false" }
}
```

### 頂層

*維度*

| 鍵           | 必填 | 值                                   | 預設         | 作用                             |
| ------------ | ---- | ------------------------------------ | ------------ | -------------------------------- |
| `id`         | 是   | 整數                                 |              | 維度 id。不可與其他模組衝突      |
| `suffix`     | 否   | 字串                                 | `DIM_<name>` | 存檔資料夾                       |
| `keepLoaded` | 否   | 布林值                               | `false`      | 無人在其中時仍保持載入           |
| `gameRules`  | 否   | 物件                                 | 無           | 只在此處適用的規則               |
| `requires`   | 否   | 清單，內容為模組 id 或資源包命名空間 | 無           | 除非全部都存在，否則會跳過此檔案 |

### 既有維度

*維度*

```json
{
  "id": 0,
  "sky": { "moon": { "texture": "mypack:textures/environment/pink_moon_phases.png" } },
  "weather": { "alwaysRain": true }
}
```

`id` 為已存在之維度的檔案，例如主世界的 `0`、地獄的 `-1`、終界的 `1`，或其他模組登錄的維度，會修改該維度而不是新增維度。檔案寫入的每個鍵都會套用在該維度上，省略的每個鍵則保留該維度自己的行為，因此其層級資料與區塊維持原樣，既有存檔也能繼續載入。

載入 Galacticraft 時，它及其附加模組加入地圖的行星與衛星也算作既有，儘管 Galacticraft 只在世界啟動時才登錄它們，所以 `-28` 會改變月球。太空站的維度類型只會改變該類型的每個太空站，不影響其他站點：`-27` 涵蓋主世界的太空站，保持載入的太空站則是 `-26`，需要自己的檔案。單一太空站自己的 id 是依世界分配的，因此無法指定，而太空站類型檔案中的 `keepLoaded` 不會有任何作用。

`terrain.type` 會替換生成器，因此之後生成的區塊都來自它，`generatorOptions` 與 `structures` 也隨之套用（對 `overworld` 而言，`generatorOptions` 是 [generatorOptions 的鍵](#generatoroptions-的鍵)的 JSON 物件；超平坦預設組合維持自己的格式）；沒有 `terrain.type` 時，這兩者會被略過，日誌會說明。`biomes` 區段會改變之後生成的區塊的生態域。`suffix` 會被忽略，因為存檔資料夾仍是該維度自己的。摘要的 `dimensions.existing` 行會列出以此方式套用的每個檔案。

只設定 `texture` 的 `sun` 或 `moon`，會替換該維度原本天空中的圖片，月亮則保留其月相。設定 `bodies`、`stars`、`sun.size` 或 `moon.size` 會把整個天空換成 [RDPL 自己的天空](#天空渲染器)。

### `terrain` 區段

*維度*

| 鍵                 | 必填 | 值                                           | 預設        | 作用                                                                                         |
| ------------------ | ---- | -------------------------------------------- | ----------- | -------------------------------------------------------------------------------------------- |
| `type`             | 否   | `overworld`、`flat`、`void`、`nether`、`end` | `overworld` | 由哪個生成器建造                                                                             |
| `generatorOptions` | 否   | 字串                                         | 無          | 生成器字串：`overworld` 為[這些鍵](#generatoroptions-的鍵)的 JSON，`flat` 則為超平坦預設組合 |
| `structures`       | 否   | 布林值                                       | `true`      | 是否生成原版結構                                                                             |

### `biomes` 區段

*維度*

| 鍵          | 必填             | 值                          | 預設               | 作用                                                                                                                                    |
| ----------- | ---------------- | --------------------------- | ------------------ | --------------------------------------------------------------------------------------------------------------------------------------- |
| `source`    | 否               | `inherit`、`single`、`list` | `inherit`          | `inherit` 使用一般的生態域地圖，`single` 在各處使用同一個生態域，`list` 則像一般生態域地圖那樣，把 `biomeList` 中的生態域分佈到整個維度 |
| `biome`     | 當為 `single` 時 | 生態域名稱                  | `minecraft:plains` | 是哪個生態域                                                                                                                            |
| `biomeList` | 當為 `list` 時   | 清單，內容為生態域名稱      | 無                 | `list` 唯一使用的生態域，其區域的大小與多樣性與一般地圖相當                                                                             |

### `sky` 區段

*維度*

| 鍵                 | 必填 | 值                      | 預設    | 作用                                                                                                                                       |
| ------------------ | ---- | ----------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| `hasSkyLight`      | 否   | 布林值                  | `true`  | 日光是否能照入                                                                                                                             |
| `surfaceWorld`     | 否   | 布林值                  | `true`  | 地圖與指南針是否如主世界般運作                                                                                                             |
| `respawn`          | 否   | 布林值                  | `true`  | 玩家是否在此重生                                                                                                                           |
| `respawnDimension` | 否   | 整數                    | 無      | 改為在哪裡重生                                                                                                                             |
| `spawning`         | 否   | 布林值                  | `true`  | 生物是否生成                                                                                                                               |
| `nether`           | 否   | 布林值                  | `false` | 在傳送門與天花板方面視為地獄                                                                                                               |
| `beds`             | 否   | 布林值                  | `true`  | 關閉時，床會爆炸                                                                                                                           |
| `waterVaporizes`   | 否   | 布林值                  | `false` | 放置的水會蒸發：水桶與發射器產生蒸氣而不是水，破壞或融化的冰不留下任何東西，熔岩的流動方式與地獄相同。世界中既有的水或由地形產生的水則保留 |
| `cloudHeight`      | 否   | 整數                    | `128`   | 雲所在的高度                                                                                                                               |
| `cloudColor`       | 否   | 十六進位顏色            | 無      | 雲的色調                                                                                                                                   |
| `cloudSpeed`       | 否   | 浮點數                  | `1.0`   | 雲飄移的速度。`0` 使其靜止，負值則反向                                                                                                     |
| `cloudLayers`      | 否   | 清單，內容為物件        | 無      | 多個雲層。見[霧、光照、雲與熱浪](#霧光照雲與熱浪)                                                                                          |
| `groundLevel`      | 否   | 整數                    | `63`    | 海平面，用於地平線與出生點搜尋                                                                                                             |
| `movementFactor`   | 否   | 浮點數                  | `1.0`   | 與主世界的距離比例。地獄使用 8                                                                                                             |
| `fogColor`         | 否   | 十六進位顏色或 `sample` | 無      | 正午的霧色調。夜間會像原版的霧一樣變暗。`sample` 會把天空與玩家周圍的地面混合                                                              |
| `showFog`          | 否   | 布林值                  | `false` | 濃霧，如同在地獄                                                                                                                           |
| `fogDensity`       | 否   | 浮點數，0 到 1          | `0.0`   | 霧的濃度。`0` 維持原版距離，`1` 會收縮到 8 格方塊                                                                                          |
| `fogGroundWeight`  | 否   | 浮點數，0 到 1          | `0.5`   | 搭配 `fogColor: sample` 時，地面相對於天空所占的比重                                                                                       |
| `skyColor`         | 否   | 十六進位顏色            | 無      | 正午的天空色調。夜間會變暗，雨天與雷雨時會像原版天空一樣變灰                                                                               |
| `fixedTime`        | 否   | 整數，刻                | 無      | 鎖定一天中的時間                                                                                                                           |
| `sunriseColors`    | 否   | 布林值                  | `true`  | 日出與日落是否染色                                                                                                                         |
| `ambientLight`     | 否   | 浮點數，0 到 1          | `0.0`   | 各處的最低光照                                                                                                                             |
| `lightSkyColor`    | 否   | 十六進位顏色            | 無      | 方塊與生物上日光的色調                                                                                                                     |
| `lightBlockColor`  | 否   | 十六進位顏色            | 無      | 火把光與其他方塊光源的色調                                                                                                                 |
| `skyFactor`        | 否   | 浮點數，0 到 1          | `1.0`   | 日光看起來的亮度。僅在用戶端繪製，因此不會改變生物生成                                                                                     |
| `starBrightness`   | 否   | 浮點數，0 到 1          | 無      | 星星的亮度                                                                                                                                 |
| `sunBrightness`    | 否   | 浮點數，0 到 1          | `1.0`   | 繪製出的太陽亮度                                                                                                                           |
| `moonBrightness`   | 否   | 浮點數，0 到 1          | `1.0`   | 繪製出的月亮亮度，搭配 `bodies` 時，則是除太陽外所有天體的亮度                                                                             |
| `heat`             | 否   | 物件                    | 無      | 覆蓋在畫面上的熱浪扭曲。見[霧、光照、雲與熱浪](#霧光照雲與熱浪)                                                                            |
| `renderSky`        | 否   | 布林值                  | `true`  | 關閉時，不繪製天空、太陽、月亮與星星，只留下霧色                                                                                           |
| `renderClouds`     | 否   | 布林值                  | `true`  | 關閉時，不繪製雲                                                                                                                           |
| `renderWeather`    | 否   | 布林值                  | `true`  | 關閉時，不繪製雨或雪                                                                                                                       |
| `sun`              | 否   | 物件                    | 無      | 你自己的太陽。見[天空渲染器](#天空渲染器)                                                                                                  |
| `bodies`           | 否   | 清單，內容為物件        | 無      | 懸掛在天空中的行星與衛星。見[天空渲染器](#天空渲染器)                                                                                      |
| `stars`            | 否   | 物件                    | 無      | 你自己的星空。見[天空渲染器](#天空渲染器)                                                                                                  |

### 天空渲染器

*維度*

設定 `sun`、`moon`、`bodies` 或 `stars` 中的任何一個，就會把原版天空換成 RDPL 自己的天空；它繪製的穹頂、日出光暈與虛空和原版相同，但太陽、其他天體與星星取自資源包。它只在用戶端繪製，專用伺服器永遠不會載入它。`renderSky: false` 仍然優先，什麼都不繪製，而 `renderClouds: false` 就是製作沒有雲的天空的方法。

沒有 `bodies` 時，保留原版的月亮及其月相。有 `bodies` 時，清單就是太陽以外的所有天體，因此空清單就是沒有月亮的天空。

```json
{
  "sky": {
    "sun": { "color": "#FFB070", "auraColor": "#FF7A30" }
  }
}
```

`sun.color` 為太陽上色，`sun.auraColor` 則在太陽周圍鋪上一層柔和的光暈，也就是太陽霧靄。兩者都會像太陽一樣隨雨和 `sunBrightness` 淡去。它們在[既有維度](#既有維度)的原版天空、AsmodeusCore 的主世界天空和 [Galacticraft 天體](#galacticraft-天體)上都有效，在 RDPL 自己的天空中也一樣，而且[時間視窗](#time-區段)可以在一天中的某段時間改變它們。

| 鍵                     | 必填 | 值           | 預設     | 作用                                                                                                                           |
| ---------------------- | ---- | ------------ | -------- | ------------------------------------------------------------------------------------------------------------------------------ |
| `sun.texture`          | 否   | 材質路徑     | 原版太陽 | 太陽的圖片                                                                                                                     |
| `sun.size`             | 否   | 浮點數       | `30`     | 太陽在距離 100 處寬度的一半。`0` 會隱藏它                                                                                      |
| `sun.color`            | 否   | 十六進位顏色 | 白色     | 太陽的色調                                                                                                                     |
| `sun.auraColor`        | 否   | 十六進位顏色 | 無       | 太陽周圍光暈的顏色。不設定就沒有光暈                                                                                           |
| `moon.texture`         | 否   | 材質路徑     | 原版月亮 | 月亮的圖片：包含八個月相的圖集，橫四格、縱兩格，如同原版的 `moon_phases.png`                                                   |
| `moon.size`            | 否   | 浮點數       | `20`     | 月亮在距離 100 處寬度的一半。`0` 會隱藏它。與 `bodies` 並用時省略                                                              |
| `bodies[].texture`     | 是   | 材質路徑     |          | 天體的圖片                                                                                                                     |
| `bodies[].size`        | 否   | 浮點數       | `20`     | 在距離 100 處寬度的一半。原版月亮為 `20`                                                                                       |
| `bodies[].angle`       | 否   | 浮點數，度   | `180`    | 它在太陽軌道上位於太陽後方多遠。`180` 是原版月亮所在的位置。`followsTime` 關閉時，從正上方起算，因此 `0` 是天頂，`90` 是地平線 |
| `bodies[].tilt`        | 否   | 浮點數，度   | `0`      | 它偏離太陽軌道多遠，往北或往南。`angle` 沿著軌道，`tilt` 橫跨軌道                                                              |
| `bodies[].body`        | 否   | 名稱         | 無       | 資源包的天體，以名稱或檔案名稱指定。若它設定了 `ring`，同樣的環會繪製在此天體周圍，位於其後方與前方                            |
| `bodies[].followsTime` | 否   | 布林值       | `true`   | 關閉時，它靜止懸掛在天空中，而不隨太陽轉動                                                                                     |
| `stars.count`          | 否   | 整數         | `1500`   | 星星的數量                                                                                                                     |
| `stars.size`           | 否   | 浮點數       | `0.15`   | 最小的星星；最大的比它再大三分之二                                                                                             |

### 霧、光照、雲與熱浪

*維度*

這些鍵位於 `sky` 區段，與較舊的鍵並列，舊的鍵運作如常。它們都只在用戶端繪製，因此專用伺服器會忽略，也不會改變任何存檔。

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

`fogColor: "sample"` 每秒讀取一次玩家周圍 33 乘 33 方塊範圍內的頂層方塊，依一天中的時間為它們的地圖顏色打光，再與天空顏色混合。霧會逐漸趨近每一次新的取樣。`fogDensity` 可與取樣的霧色、指定的霧色或未設定霧色搭配使用。在水下、熔岩中與失明時，仍維持原版的霧。

`lightSkyColor` 與 `lightBlockColor` 會為光照貼圖染色，因此每個被照亮的方塊與生物都會帶上該色調。`skyFactor` 縮放日光看起來的亮度，而伺服器用於生成與作物的光照等級維持不變。

沒有 `cloudLayers` 時，`cloudSpeed` 改變位於 `cloudHeight` 的那個原版雲層的速度。有 `cloudLayers` 時，每一項都是獨立的雲層，`cloudHeight`、`cloudSpeed` 與 `cloudColor` 會補上項目省略的部分。`renderClouds: false` 仍然不繪製任何雲。

`sunBrightness` 與 `moonBrightness` 在原版的雨天淡化之外，再讓太陽與月亮淡出，不論是在原版天空，還是在你自己於[天空渲染器](#天空渲染器)中設定的天空。

`heat` 在玩家所在生態域的溫度不低於 `minTemperature` 時，在畫面上覆蓋一層波浪狀的熱浪扭曲。沙漠為 2.0，平原為 0.8。扭曲會在幾秒內淡入淡出，在水下則維持關閉。它需要顯示卡的著色器支援，並且在其他全螢幕著色器（例如旁觀者視角）開啟時保持關閉。

`mode` 決定扭曲出現的位置。`screen` 會扭曲橫跨螢幕下半部的固定帶狀區域，不論玩家看向何處。`world` 則跟隨地形：比 `startDistance` 個方塊更近的地形保持清晰，扭曲朝著霧收攏的視距遠端逐漸增強，而天空永遠不會扭曲，不論玩家向下、平視或向上看。

| 鍵                     | 必填 | 值             | 預設          | 作用                                      |
| ---------------------- | ---- | -------------- | ------------- | ----------------------------------------- |
| `cloudLayers[].height` | 否   | 浮點數         | `cloudHeight` | 該雲層所在的位置                          |
| `cloudLayers[].speed`  | 否   | 浮點數         | `cloudSpeed`  | 飄移的速度。`0` 使其靜止，負值則反向      |
| `cloudLayers[].color`  | 否   | 十六進位顏色   | `cloudColor`  | 它的色調                                  |
| `heat.strength`        | 否   | 浮點數，0 到 1 | `0.1`         | 熱浪扭曲的強度                            |
| `heat.minTemperature`  | 否   | 浮點數         | `1.5`         | 會產生扭曲的最低生態域溫度                |
| `heat.dayOnly`         | 否   | 布林值         | `true`        | 開啟時，扭曲隨日光淡出，夜晚消失          |
| `heat.mode`            | 否   | 字串           | `screen`      | 扭曲出現的位置，`screen` 或 `world`       |
| `heat.startDistance`   | 否   | 浮點數         | `32`          | 在 `world` 模式下，扭曲從多少格方塊外開始 |

### 雪、流體、星星與閃電

*維度*

這些鍵同樣位於 `sky` 區段，同樣只在用戶端繪製。

```json
{
  "sky": {
    "snowColor": "#C8E0FF",
    "waterFogColor": "#103040",
    "lavaFogColor": "#802000",
    "starColor": "#FFE0A0",
    "starTwinkle": 0.5,
    "lightningColor": "#A080FF",
    "grassTint": "#A0FF80",
    "foliageTint": "#E0A040"
  }
}
```

`grassTint` 與草、高草、蕨和甘蔗的顏色相乘，`foliageTint` 與樹葉和藤蔓的顏色相乘。生態域自身的顏色依然會透出來，兩個鍵都可以用在環境的 `sky` 中，季節正是這樣改變植物的。手持物品和選單中的物品維持原色。

`snowColor` 為地面上的雪層與雪方塊染色。生態域自己的 `snowColor` 優先於維度的，而且顏色會像草那樣在生態域邊界上混合。

`waterFogColor` 與 `lavaFogColor` 取代攝影機在水下或熔岩中看到的霧色。夜晚、深度與夜視仍會像對原版顏色一樣使其變暗或變亮。

`starColor` 為星星染色，不論在原版天空，還是你自己於[天空渲染器](#天空渲染器)中設定的天空。`starTwinkle` 讓星星閃爍：星星分成八組，每組以自己的節奏變暗與變亮，其值是變暗的深度，因此值為 `1` 時，一組星星在最暗處會完全淡出。

`lightningColor` 為閃電染色。

| 鍵               | 必填 | 值             | 預設     | 作用                                   |
| ---------------- | ---- | -------------- | -------- | -------------------------------------- |
| `snowColor`      | 否   | 十六進位顏色   | 白色     | 雪層與雪方塊的色調                     |
| `waterFogColor`  | 否   | 十六進位顏色   | `050533` | 水下的霧色                             |
| `lavaFogColor`   | 否   | 十六進位顏色   | `991A00` | 熔岩中的霧色                           |
| `starColor`      | 否   | 十六進位顏色   | 白色     | 星星的色調                             |
| `starTwinkle`    | 否   | 浮點數，0 到 1 | `0.0`    | 星星閃爍時變暗的深度。`0` 使其穩定不變 |
| `grassTint`      | 否   | 十六進位顏色   | 白色     | 與草、高草、蕨和甘蔗的顏色相乘         |
| `foliageTint`    | 否   | 十六進位顏色   | 白色     | 與樹葉和藤蔓的顏色相乘                 |
| `lightningColor` | 否   | 十六進位顏色   | `737380` | 閃電的色調                             |

### 天空盒、極光與彩虹

*維度*

這些鍵同樣位於 `sky` 區段，同樣只在用戶端繪製。

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

用一張環繞整個天空的圖片取代六個面：

```json
{
  "sky": {
    "skybox": {
      "panorama": "mypack:textures/sky/panorama.png"
    }
  }
}
```

`skybox` 把你自己的圖片畫在天空上，位於日出光暈、太陽、月亮與星星之後。以完整的材質路徑提供立方體的全部六個面，依立方體展開圖排列：`up` 接在 `north` 的上緣，`down` 接在其下緣，`west` 在其左側，`east` 在其右側，`south` 則在 `east` 之後。或者只提供 `panorama`，以一張 2:1 的圖片環繞整個天空：它的左緣面向北方，順時針經過東、南、西方，最上一列是正上方，最下一列是正下方。缺少某個面又沒有 panorama 的天空盒會被略過，並在日誌中記錄錯誤。

`aurora` 在夜間於北方天空低處懸掛發光的簾幕。它們緩慢起伏，隨日落淡入，白天與下雨時消失。`color` 是簾幕底部的顏色，`topColor` 則是它們在頂部漸變成的顏色。

`rainbow` 在白天雨停之後，於太陽的對側顯示彩虹。它會在雨結束後的兩分鐘內淡出，新的陣雨會將其清除。

原版沒有任何鍵能為維度提供專屬的天空圖片、極光或彩虹，因此它們在每個版本上的運作方式相同。

| 鍵                | 必填 | 值           | 預設     | 作用                                                                             |
| ----------------- | ---- | ------------ | -------- | -------------------------------------------------------------------------------- |
| `skybox.<face>`   | 否   | 材質路徑     | 無       | 立方體的一個面：`up`、`down`、`north`、`east`、`south` 或 `west`。六個面都要提供 |
| `skybox.panorama` | 否   | 材質路徑     | 無       | 環繞整個天空的一張圖片，用來取代各個面                                           |
| `aurora.color`    | 否   | 十六進位顏色 | `40FF90` | 簾幕底部的顏色                                                                   |
| `aurora.topColor` | 否   | 十六進位顏色 | `8040FF` | 頂部的顏色，簾幕在此淡出                                                         |
| `rainbow`         | 否   | 布林值       | `false`  | 雨後顯示彩虹                                                                     |

### `physics` 區段

*維度*

| 鍵             | 必填 | 值             | 預設           | 作用                                            |
| -------------- | ---- | -------------- | -------------- | ----------------------------------------------- |
| `gravity`      | 否   | 浮點數，大於 0 | `1.0`          | 此處的下落加速度，為原版的倍率。`0.17` 近似月球 |
| `fallDamage`   | 否   | 浮點數，大於 0 | `1.0`          | 此處的摔落傷害，為倍率                          |
| `arrowGravity` | 否   | 浮點數，大於 0 | 跟隨 `gravity` | 箭在此處的下墜速度，為倍率                      |

這些與[世界物理](#世界物理)中的倍率相同，只是設定在維度上。世界範本中針對此維度的 `dimension=value` 行仍然優先；沒有維度的世界範本裸值，只涵蓋未自行設定任何內容的維度。在 [Galacticraft 天體](#galacticraft-天體)上，則由 Galacticraft 套用它們。

### `time` 區段

*維度*

```json
{
  "time": {
    "dayLength": 36000,
    "windows": [
      { "from": 13000, "to": 23000, "sky": { "fogDensity": 0.6, "fogColor": "#9AA4B0" } },
      { "from": 23000, "to": 2000, "sky": { "fogDensity": 0.35, "sun": { "color": "#FF9050", "auraColor": "#FF5020" } } },
      { "from": 2000, "to": 13000, "weather": { "precipitation": false } }
    ]
  }
}
```

`windows` 會在每天的某段時間內改變少數幾個 `sky` 與 `weather` 鍵：這裡是夜裡起霧，黎明前後霧變淡、太陽泛著朦朧的紅色，而且只在夜裡下雨。`from` 與 `to` 是一天中的時刻，採用慣用的 24000 刻刻度，0 是日出，6000 是正午，12000 是日落，18000 是午夜；它們會隨 `dayLength` 伸縮，所以 13000 一律緊接在日落之後。`from` 大於 `to` 的視窗會跨過午夜。視窗重疊時，清單中排在前面的那個生效。有 `fixedTime` 時一天中的時刻永遠不動，所以視窗不是一直生效，就是永遠不生效。

霧與太陽的變化會在視窗開始與結束時，用一兩秒淡入淡出。所有視窗之外，使用維度自己的鍵。

| 鍵                                | 必填 | 值                      | 預設         | 作用                                              |
| --------------------------------- | ---- | ----------------------- | ------------ | ------------------------------------------------- |
| `dayLength`                       | 否   | 整數，刻                | `24000`      | 此處一個完整晝夜的長度。月相仍然每 24000 刻轉一圈 |
| `windows[].from`                  | 是   | 整數，0 到 24000        |              | 視窗開始的時刻                                    |
| `windows[].to`                    | 是   | 整數，0 到 24000        |              | 視窗結束的時刻                                    |
| `windows[].sky.fogDensity`        | 否   | 浮點數，0 到 1          | 維度本身的值 | 視窗期間的 [`fogDensity`](#sky-區段)              |
| `windows[].sky.fogColor`          | 否   | 十六進位顏色或 `sample` | 維度本身的值 | 視窗期間的 [`fogColor`](#sky-區段)                |
| `windows[].sky.sun.color`         | 否   | 十六進位顏色            | 維度本身的值 | 視窗期間的 [`sun.color`](#天空渲染器)             |
| `windows[].sky.sun.auraColor`     | 否   | 十六進位顏色            | 維度本身的值 | 視窗期間的 [`sun.auraColor`](#天空渲染器)         |
| `windows[].weather.precipitation` | 否   | `false`                 | 維度本身的值 | 關閉時，雨與風暴會逐漸消退，並在視窗期間不再出現  |
| `windows[].weather.alwaysRain`    | 否   | 布林值                  | 維度本身的值 | 視窗期間的 [`alwaysRain`](#weather-區段)          |

視窗裡的其他任何鍵都會被忽略並在日誌中報錯；在 `precipitation` 關閉的維度裡寫 `precipitation: true` 或 `alwaysRain: true` 也一樣。

### `weather` 區段

*維度*

| 鍵                      | 必填 | 值                    | 預設                     | 作用                                                                                                                                                               |
| ----------------------- | ---- | --------------------- | ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `precipitation`         | 否   | 布林值                | `true`                   | 關閉時，此處永遠不會下雨、下雪或有風暴                                                                                                                             |
| `lightning`             | 否   | 布林值                | `true`                   | 關閉時，雨與風暴不帶閃電                                                                                                                                           |
| `snow`                  | 否   | 布林值                | `true`                   | 關閉時，雪永遠不會堆積                                                                                                                                             |
| `freeze`                | 否   | 布林值                | `true`                   | 關閉時，水永遠不會結冰                                                                                                                                             |
| `alwaysRain`            | 否   | 布林值                | `false`                  | 開啟時，此處一直下雨、沒有晴朗時段，優先於任何 `cycle`。`precipitation` 關閉時則省略                                                                               |
| `cycle.rainTicks`       | 否   | 整數或 `[min, max]`   | `[1000, 4600]`           | 一場陣雨持續的時間                                                                                                                                                 |
| `cycle.clearTicks`      | 否   | 整數或 `[min, max]`   | `[1000, 3000]`           | 陣雨之間的乾燥時段持續的時間                                                                                                                                       |
| `cycle.maxStrength`     | 否   | 浮點數，大於 0 至 1   | `0.6`                    | 陣雨最強的程度。每場陣雨會在此值的四分之一到全部之間變化。雷暴期間，降雨會忽略此值，增強到滿強度                                                                   |
| `cycle.thunderTicks`    | 否   | 整數或 `[min, max]`   | 無                       | 雷暴持續的時間。沒有它，循環永遠不會有風暴                                                                                                                         |
| `cycle.calmTicks`       | 否   | 整數或 `[min, max]`   | `[12000, 180000]`        | 風暴之間的平靜期持續的時間                                                                                                                                         |
| `cycle.thunderStrength` | 否   | 浮點數，大於 0 至 1   | `1`                      | 風暴有多暗。只有高於 `0.9` 才會落雷                                                                                                                                |
| `rain.particle`         | 否   | 粒子名稱              | `droplet`                | 雨落地處濺起的東西                                                                                                                                                 |
| `rain.sound`            | 否   | 音效名稱              | `minecraft:weather.rain` | 雨聲                                                                                                                                                               |
| `rain.volume`           | 否   | 浮點數                | `0.2`                    | 雨聲的音量，雨落在你頭頂上方時減半                                                                                                                                 |
| `rain.interval`         | 否   | 整數                  | `3`                      | 音效播放的稀疏程度；越高越稀疏，`0` 則每次機會都播放                                                                                                               |
| `rain.color`            | 否   | 十六進位顏色          | `#FFFFFF`                | 落雨的色調                                                                                                                                                         |
| `rain.snowColor`        | 否   | 十六進位顏色          | `#FFFFFF`                | 落雪的色調                                                                                                                                                         |
| `rain.colorMode`        | 否   | `multiply` 或 `solid` | `multiply`               | `rain.color` 如何與雨的紋理結合：`multiply` 會為灰藍色的雨絲染色，所以明亮的顏色會變暗；`solid` 以 `rain.color` 繪製雨絲，保留其形狀與透明度；夜間光照仍會使其變暗 |
| `rain.angle`            | 否   | 浮點數，0 到 180      | `0`                      | 偏離正下方的度數：`90` 為橫向吹，`180` 為直直向上升。繪製時最多傾斜 75 度                                                                                          |
| `rain.heading`          | 否   | 浮點數，度            | `0`                      | 吹向哪個方向：`0` 南，`90` 西，`180` 北，`270` 東                                                                                                                  |
| `rain.splashColor`      | 否   | 十六進位顏色          | 不著色                   | 雨滴落地時水花的色調。沒有它則水花不著色                                                                                                                           |
| `rain.splashUpward`     | 否   | 布林值                | `false`                  | 開啟時，上升的雨（`angle` 超過 `90`）仍會在地面濺起並播放聲音                                                                                                      |
| `wind.gust`             | 否   | 浮點數，0 到 90       | `15`                     | 陣風在高峰時加到 `angle` 的度數，永遠不會超過水平                                                                                                                  |
| `wind.every`            | 否   | 整數或 `[min, max]`   | `[200, 600]`             | 一陣風到下一陣風之間的刻數                                                                                                                                         |
| `wind.swing`            | 否   | 浮點數，0 到 180      | `30`                     | 陣風把 `heading` 向任一側擺動的度數                                                                                                                                |

`wind` 區段讓雨帶有陣風。不時有一陣風使雨最多再多傾斜 `gust` 度，並把風向最多往一側擺動 `swing` 度，在四秒內升起並消退；陣風每隔 `every` 刻出現一次。雨與雪會隨之傾斜，維度的環境粒子也會順著雨傾斜的方向飄動，不論有沒有陣風。有 `wind` 區段但沒有 `rain` 區段時，雨使用其預設值。

其他維度共用主世界的雨。`cycle` 讓這個維度擁有自己的天氣：陣雨依上述時間來去，不論主世界在做什麼。有 `thunderTicks` 時也會有風暴，依其自己的時間；風暴遇上陣雨時，會讓陣雨達到最強、使天空變暗，並且在 `lightning` 開啟時帶來閃電。[世界範本](#世界範本)中的 `weatherCeiling` 仍然限制雨能到達多高。帶有 `precipitation: false` 的[時間視窗](#time-區段)會把雨限制在一天的其餘時間：正在下的陣雨會在幾秒內消退，天氣則等到視窗結束；帶有 `alwaysRain` 的視窗會讓雨在它所佔的那段時間裡傾盆而下。

`rain` 區段改變此處雨與雪的外觀與聲音，不論有沒有 `cycle`；沒有它時，外觀與聲音與原版相同。它在 Galacticraft 的行星或衛星上運作方式相同。

### `ambience` 區段

*維度*

| 鍵               | 必填 | 值                  | 預設             | 作用                                                                                                                                         |
| ---------------- | ---- | ------------------- | ---------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| `music`          | 否   | 音效名稱            | 無               | 在此處取代一般曲目播放的音樂，創造模式也一樣。抵達時會截斷舊曲目                                                                             |
| `musicDelay`     | 否   | 整數或 `[min, max]` | `[12000, 24000]` | 兩首曲目之間的安靜刻數                                                                                                                       |
| `loopSound`      | 否   | 音效名稱            | 無               | 你在此處期間持續循環的音效，抵達時淡入，離開時淡出                                                                                           |
| `ambientSound`   | 否   | 音效名稱            | 無               | 偶爾播放的音效，如同現代生態域自行添加的那樣。由伺服器只傳送給該玩家                                                                         |
| `soundChance`    | 否   | 0.0 到 1.0          | `0.0111`         | 每刻播放 `ambientSound` 的機率                                                                                                               |
| `particle`       | 否   | 粒子名稱            | 無               | 飄浮在你周圍空氣中的粒子，為遊戲的粒子名稱之一，例如 `depthsuspend`、`townaura`、`reddust` 或 `mobSpellAmbient`                              |
| `particleChance` | 否   | 0.0 到 1.0          | `0.00625`        | 其密度，計算方式與現代生態域相同：每刻嘗試 16 格方塊內約 667 個位置，以及 32 格方塊內再多 667 個，每個不是完整方塊的位置，都以此機率顯示粒子 |
| `particleColor`  | 否   | 十六進位顏色        | 無               | 接受色調的粒子的色調：`reddust`、`mobSpell` 與 `mobSpellAmbient`                                                                             |

`ambience` 區段賦予維度自己的音樂、聲音與飄浮粒子，就像現代生態域那樣。「粒子」視訊設定會像對原版粒子那樣使其變稀疏，而遊戲的洞穴音效仍會播放。

### 環境

*維度*

```json
{
  "environments": {
    "storm": {
      "sky": { "skyColor": "#4A5260", "fogColor": "#5A6070", "fogDensity": 0.5, "cloudColor": "#3A3F48", "lightSkyColor": "#B0B8D0" },
      "ambience": { "loopSound": "minecraft:weather.rain" }
    },
    "aurora": {
      "sky": { "aurora": { "color": "#40FF90" }, "starBrightness": 1.0 }
    },
    "swarm": {
      "spawning": { "surfaceNightMonsterRate": 4.0, "undergroundNightMonsterRate": 4.0, "monsterCap": 210, "weights": { "minecraft:zombie": 10, "minecraft:creeper": 0 } }
    },
    "harvest": {
      "growth": { "crops": 2.0, "saplings": 2.0, "animals": 2.0, "breeding": 2.0 }
    },
    "bloodmoon": {
      "sky": { "skyColor": "#5A1010", "fogColor": "#401010" },
      "mobs": { "entities": ["#monster", "minecraft:zombie"], "health": 2.0, "damage": 1.5, "speed": 1.2, "equipment": { "head": { "item": "minecraft:iron_helmet", "dropChance": 0.0 }, "mainhand": { "item": "minecraft:iron_sword" } } }
    },
    "bounty": {
      "loot": { "minecraft:entities/cow": "mypack:entities/rich_cow", "minecraft:chests/simple_dungeon": "mypack:chests/bounty" },
      "trades": [{ "profession": "minecraft:farmer", "career": "farmer", "level": 1, "buy": { "item": "minecraft:wheat", "min": 10 }, "sell": { "item": "minecraft:emerald" } }]
    },
    "longnight": {
      "rules": { "dayLength": 48000, "weather": "thunder", "difficulty": "hard", "sleep": false, "weatherSpeed": 2.0, "localDifficulty": 4.0, "wakeAt": 1000 }
    },
    "siege": {
      "raids": { "every": 72000, "start": "mypack:raid", "waves": 2.0, "points": 2.0 }
    },
    "desertheat": {
      "biomes": ["minecraft:desert"],
      "sky": { "skyColor": "#E0A050", "fogColor": "#D8B070" }
    }
  }
}
```

`environments` 讓維度擁有可在遊戲進行中切換的其他外觀。每個環境是一組疊在維度自身鍵之上的 `sky` 與 `ambience` 鍵：省略的鍵沿用維度的值，`sun` 或 `aurora` 這類物件會與維度的物件合併，`cloudLayers` 或 `bodies` 這類清單則取代維度的清單。設定 `sun`、`moon`、`bodies` 或 `stars` 會啟用[天空渲染器](#天空渲染器)，`renderSky`、`renderClouds` 與 `renderWeather` 則能關閉並再次開啟原版渲染。

`/rdplserver environment <dimension> <name>` 把維度切換到某個環境，`default` 則恢復它自己的外觀；單獨使用 `/rdplserver environment` 會列出每個有環境的維度以及它目前使用的環境。此指令需要權限等級 2，因此函式可以執行它：[卡片規則](#卡片)的 `runs`、計分的回合或輪次、突襲或指令方塊都能這樣改變天空，可以在一天中的某個時刻、某一天、玩了一段時間之後或任何其他觸發時進行。維度以其 `id` 中的數字指定。使用中的環境會隨世界一起儲存，因此重新啟動後仍會保留，伺服器會在每位玩家加入時、以及環境改變時向所有人傳送它。[時間窗口](#time-區段)仍會疊加在它之上生效。

帶有 `biomes` 的環境只在這些生態域中生效。`/rdplserver environment <dimension> <name>` 會在整個維度所用的環境之外額外開啟它，`/rdplserver environment <dimension> <name> off` 則再次關閉它；對整個維度所用的環境使用 `off`，會讓維度恢復它自己的外觀。可以同時開啟多個，它們的生態域重疊時，最後開啟的那個優先。站在其某個生態域中的玩家會看到它，其他地方的維度看起來和之前一樣。每個使用中的環境都以同樣的方式列出、儲存和傳送。

環境還可以帶一個 `spawning` 區段，在環境開啟期間改變維度中的自然生成。其中的速率鍵與上限鍵，名稱和含義與[生成](#生成)相同，並會取代世界與生態域的值。`weights` 依倍數調整某個指定生物被選中的頻率：`10` 表示機率高出十倍，`0` 表示它不再自行生成。帶有 `biomes` 時，該區段只在這些生態域中生效；多個環境重疊的地方，由最後開啟的那個決定，與天空相同。上限和原版一樣，在整個維度範圍內統計該生物種類，但只在該區段生效的地方限制新的生成。該區段沒有寫出的鍵沿用通常的值。關閉環境會立刻結束這一切：已經生成的生物會保留。速率在世界的生成速率生效的任何地方都適用，包括生怪磚生成的生物；上限和權重只影響自然生成，生怪蛋與指令永遠不受影響。

環境還可以帶一個 `growth` 區段，在環境開啟期間加快或減慢維度中的生長。`crops` 涵蓋農作物、南瓜與西瓜的莖、可可豆、地獄疣、甘蔗、仙人掌、歌萊花，以及任何透過 Forge 作物生長事件生長的模組植物；`saplings` 涵蓋長成樹的樹苗；`animals` 決定幼年動物長大的速度；`breeding` 決定繁殖過的動物多久後能再次繁殖，設為 `0` 時，餵食的動物不再生出幼崽。每個值都是 `0` 到 `100` 的倍數：`1` 是通常速度，`0.5` 是一半，`2` 是兩倍，`0` 表示停止。大於 1 時，遊戲每次選中某株植物生長，它都會獲得額外的生長機會，因此光照、土壤和水仍然有影響。骨粉、餵食與指令永遠不受影響。帶有 `biomes` 時，該區段只在這些生態域中生效；多個環境重疊的地方，由最後開啟的那個決定，與天空相同。該區段沒有寫出的鍵沿用通常的速度。關閉環境會立刻恢復通常速度；已經長出的部分保持不變。

環境還可以帶一個 `mobs` 區段，在該環境開啟期間改變維度中的生物，適合血月之類的事件。`entities` 依 ID 指定要改變的生物，也可以用 `#monster`、`#creature`、`#ambient` 或 `#water_creature` 依類別指定；省略時涵蓋所有怪物。`health`、`damage` 和 `speed` 分別乘以生物的最大生命值、近戰攻擊傷害和行走速度；生物的目前生命值會隨最大值一起變化。`equipment` 會在生物空著的欄位放入一件物品，並指定它死亡時掉落的機率。生物採用哪個環境，取決於它生成或載入時所站的位置，而且每次切換環境時，所有已載入的生物都會重新判定一次；`biomes` 和重疊的處理方式與天空相同。只要該環境保持開啟，生物就保留這些改變，重新啟動後也一樣。環境關閉時，生物的生命值、傷害和速度恢復原樣，環境給予的裝備如果還在生物身上，就會被收回。箭、火球和爆炸的傷害不變，玩家也永遠不會被改變。

環境還可以帶有 `loot` 區段，在環境開啟期間把一張戰利品表換成另一張：生物掉落、釣魚、進度獎勵，以及世界生成的箱子和運輸礦車。箱子在第一次打開時才被填充，所以由那一刻開啟的環境決定裡面的內容。把一張表對應到 `minecraft:empty`，就什麼都不會掉落。`trades` 區段按[交易](#交易)的格式列出交易；它提到的職業的村民，在環境開啟期間會提供這些交易（最高到自己的等級）來取代自己原有的交易，環境關閉後則恢復自己的交易。每個村民兩份都會保留：已完成的交易、售罄的交易和補貨，會在環境回來時從原處繼續，重新啟動之後也一樣。列出的每個職業都要給出 1 級交易，否則該職業的新村民沒有任何交易可提供。流浪商人不會被改變。`biomes` 和重疊的處理與天空相同。

環境還可以帶一個 `rules` 區段，在環境開啟期間改變世界的運作方式。`dayLength` 會像 [`time.dayLength`](#time-區段) 一樣拉長一天，`weather` 把天氣固定為 `clear`、`rain` 或 `thunder`，`weatherSpeed` 讓該維度的天氣來去得更快或更慢，`0` 表示維持現狀；沒有 `weather.cycle` 時，它會加快主世界的天氣，其他維度共用主世界的天氣。`difficulty` 在此處取代世界的難易度，優先於 `worldDifficulty`，`localDifficulty` 設定局部難易度，範圍 `0` 到 `6.75`，決定怪物帶著盔甲生成或撿取物品的頻率。`sleep` 關閉後任何人都無法睡覺，已經睡著的人會醒來；`wakeAt` 設定一夜睡眠結束的時間，只在主世界移動時鐘，和原版的跳過一樣。`sleep` 和 `localDifficulty` 對 `biomes` 和重疊的處理與天空相同；其餘的鍵作用於整個維度，所以帶 `biomes` 的環境若給出這些鍵，會在記錄中留下錯誤並忽略該鍵。關閉環境會一次性恢復所有內容；天氣會在幾秒內逐漸恢復。

環境還可以帶一個 `raids` 區塊，在環境開啟期間改變維度中的[突襲](#突襲)。給出 `every` 後，每位玩家會統計自己在環境開啟的地方度過的刻數。計數達到 `every` 時，`start` 中列出的某個突襲會在該玩家身邊開始，就像他帶著該突襲的 `omen` 一樣：在他所處的村莊中，或者在突襲帶有 `anywhere` 時在他周圍。若突襲無法在那裡開始，計數保持滿值，時鐘每秒再試一次。任何開始的突襲都會把其 `reach` 範圍內每位玩家的計數重置為 0，因此 `every` 是兩次突襲之間的最短間隔。`waves` 和 `points` 會乘以在該環境下開始的每個突襲的波數和積分，由 `omen` 觸發的和由 `/rdplserver raid` 觸發的也包括在內。兩者都會四捨五入，突襲至少有一波；超出突襲自身清單的末尾後，它的最後一波會再次到來。突襲會保留開始時的波數和積分，即使環境之後關閉也是如此，它們會隨突襲一起儲存。有 `biomes` 時，時鐘按玩家所在位置判斷，波數和積分按突襲的中心判斷。多個環境重疊的地方，由最後開啟的那個決定。沒有環境給出 `every` 的地方計數暫停，計數隨玩家儲存，每個維度一個。

| 鍵                                                     | 必填            | 值                     | 預設           | 作用                                                                                                                                                                                                                                        |
| ------------------------------------------------------ | --------------- | ---------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `environments.<name>`                                  | 否              | 物件                   | 無             | 一個環境。名稱為一個單字，除 `default` 外皆可                                                                                                                                                                                               |
| `environments.<name>.biomes`                           | 否              | 生態域名稱清單         | 所有生態域     | 生態域名稱，例如 `minecraft:desert`，或 `#TYPE` 表示 Forge 生態域類型，例如 `#SNOWY`。這樣環境只在這些生態域中生效                                                                                                                          |
| `environments.<name>.sky`                              | 否              | 物件                   | 無             | [`sky` 區段](#sky-區段)中的鍵，但世界載入時只設定一次的鍵除外：`hasSkyLight`、`surfaceWorld`、`respawn`、`respawnDimension`、`spawning`、`nether`、`beds`、`waterVaporizes`、`groundLevel`、`movementFactor`、`fixedTime` 與 `ambientLight` |
| `environments.<name>.ambience`                         | 否              | 物件                   | 無             | [`ambience` 區段](#ambience-區段)中的鍵                                                                                                                                                                                                     |
| `environments.<name>.spawning`                         | 否              | 物件                   | 無             | [生成](#生成)中的速率鍵和上限鍵，加上 `weights`                                                                                                                                                                                             |
| `environments.<name>.spawning.weights`                 | 否              | 實體 ID 對應倍數的物件 | 無             | 調整該生物在其所在的每個生成清單中的權重；`0` 表示它不會自然生成                                                                                                                                                                            |
| `environments.<name>.growth`                           | 否              | 物件                   | 無             | `crops`、`saplings`、`animals` 與 `breeding` 的速度                                                                                                                                                                                         |
| `environments.<name>.growth.crops`                     | 否              | 數字，0 到 100         | `1`            | 農作物生長速度；`0` 表示停止                                                                                                                                                                                                                |
| `environments.<name>.growth.saplings`                  | 否              | 數字，0 到 100         | `1`            | 樹苗生長速度；`0` 表示停止                                                                                                                                                                                                                  |
| `environments.<name>.growth.animals`                   | 否              | 數字，0 到 100         | `1`            | 幼年動物長大的速度；`0` 表示一直保持幼年                                                                                                                                                                                                    |
| `environments.<name>.growth.breeding`                  | 否              | 數字，0 到 100         | `1`            | 繁殖後等待時間流逝的速度；`0` 表示無法繁殖                                                                                                                                                                                                  |
| `environments.<name>.mobs`                             | 否              | 物件                   | 無             | 環境開啟期間改變生物                                                                                                                                                                                                                        |
| `environments.<name>.mobs.entities`                    | 否              | 實體 ID 或類別的清單   | `["#monster"]` | ID，如 `minecraft:zombie`，或 `#monster`、`#creature`、`#ambient`、`#water_creature`                                                                                                                                                        |
| `environments.<name>.mobs.health`                      | 否              | 大於 0 的數字          | `1`            | 乘以最大生命值                                                                                                                                                                                                                              |
| `environments.<name>.mobs.damage`                      | 否              | 0 或更大的數字         | `1`            | 乘以近戰攻擊傷害                                                                                                                                                                                                                            |
| `environments.<name>.mobs.speed`                       | 否              | 0 或更大的數字         | `1`            | 乘以行走速度；`0` 讓生物原地不動                                                                                                                                                                                                            |
| `environments.<name>.mobs.equipment`                   | 否              | 欄位對應物品的物件     | 無             | 欄位為 `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet`；只填充空欄位                                                                                                                                                                 |
| `environments.<name>.mobs.equipment.<slot>.item`       | 是              | 物品 ID                | 無             | 給予的物品                                                                                                                                                                                                                                  |
| `environments.<name>.mobs.equipment.<slot>.dropChance` | 否              | 數字，0 到 1           | `0.085`        | 生物死亡時物品掉落的機率                                                                                                                                                                                                                    |
| `environments.<name>.loot`                             | 否              | 表 ID 到表 ID 的物件   | 無             | 環境開啟期間，把列出的每張表換成指定的表                                                                                                                                                                                                    |
| `environments.<name>.trades`                           | 否              | 交易列表               | 無             | 項目格式同[交易](#交易)；所列職業的村民只提供這些交易，最高到其等級                                                                                                                                                                         |
| `environments.<name>.trades[].profession`              | 是              | 職業 ID                | 無             | `career`、`level`、`buy`、`buySecondary`、`sell`、`maxUses` 的格式同[交易](#交易)                                                                                                                                                           |
| `environments.<name>.rules`                            | 否              | 物件                   | 無             | 環境開啟期間的世界規則                                                                                                                                                                                                                      |
| `environments.<name>.rules.dayLength`                  | 否              | 大於 0 的整數          | 維度自身的值   | 一天的長度，僅限整個維度                                                                                                                                                                                                                    |
| `environments.<name>.rules.weather`                    | 否              | 字串                   | 無             | 固定為 `clear`、`rain` 或 `thunder`，僅限整個維度                                                                                                                                                                                           |
| `environments.<name>.rules.weatherSpeed`               | 否              | 數字，0 到 100         | `1`            | 天氣變化的快慢，僅限整個維度                                                                                                                                                                                                                |
| `environments.<name>.rules.difficulty`                 | 否              | `peaceful`…`hard`      | 無             | 難易度，僅限整個維度                                                                                                                                                                                                                        |
| `environments.<name>.rules.localDifficulty`            | 否              | 數字，0 到 6.75        | 原版的值       | 局部難易度                                                                                                                                                                                                                                  |
| `environments.<name>.rules.sleep`                      | 否              | 布林值                 | `true`         | 關閉後任何人都無法睡覺                                                                                                                                                                                                                      |
| `environments.<name>.rules.wakeAt`                     | 否              | 整數，0 到 23999       | `0`            | 睡眠結束的時間，僅限整個維度                                                                                                                                                                                                                |
| `environments.<name>.raids`                            | 否              | 物件                   | 無             | 改變突襲：`every`、`start`、`waves` 和 `points`                                                                                                                                                                                             |
| `environments.<name>.raids.every`                      | 否              | 大於 0 的整數          | 無             | 玩家在此度過多少刻後，突襲在其身邊開始。需要 `start`                                                                                                                                                                                        |
| `environments.<name>.raids.start`                      | 有 `every` 時是 | 突襲 id 或其清單       | 無             | 時鐘發起的突襲，或時鐘從中隨機選取的突襲清單                                                                                                                                                                                                |
| `environments.<name>.raids.waves`                      | 否              | 數字，大於 0，最大 100 | `1`            | 乘以突襲派出的波數                                                                                                                                                                                                                          |
| `environments.<name>.raids.points`                     | 否              | 數字，0 到 100         | `1`            | 乘以獲勝的突襲發放的積分                                                                                                                                                                                                                    |

環境中的任何其他鍵，或上面列出的 `sky` 鍵，都會被忽略並在記錄中留下錯誤。以資源包已不再定義的環境儲存的世界，會恢復維度自身的外觀。

### 季節

*維度*

季節就是環境，`seasons` 依年週期開啟和關閉維度的環境。

```json
{
  "seasons": {
    "days": 2,
    "order": ["spring", "summer", ["autumn", "autumnlocal"], "winter"]
  },
  "environments": {
    "spring": { "growth": { "crops": 1.5 } },
    "summer": { "rules": { "weather": "clear" } },
    "autumn": { "sky": { "grassTint": "#E0A040", "foliageTint": "#FF8020" } },
    "autumnlocal": { "biomes": ["plains"], "sky": { "grassTint": "#E0A040", "foliageTint": "#FF0000" } },
    "winter": { "growth": { "crops": 0.0 }, "sky": { "snowColor": "#D8E8FF" } }
  }
}
```

`order` 的每一項給出一個環境名稱，或一組一起切換的名稱清單，各項輪流生效，每項持續 `days` 天，輪完一圈後重新開始。清單中只能有一個涵蓋整個維度的環境，其餘的需要 `biomes`，如上面的 `autumnlocal`。`environments` 區塊必須位於同一個維度檔案中。

一天等於主世界時鐘的 24000 刻，與卡片觸發器 `day` 計算的天相同，不論季節設在哪個維度。第 0 天開始第一個季節。無論維度是否已載入，伺服器每秒檢查一次：季節更替時，它關閉上一個季節的環境，開啟下一個季節的環境，並在日誌中寫下 `Dimension X enters the season 'name' on day N`，名稱取該項的第一個名字。它只會更動 `order` 中列出的名稱。

移動時鐘的指令或床也會移動季節，世界載入時進入其所在日對應的季節。手動切換的環境會保持到下一次季節更替。生態域環境疊加在維度自身的設定上，而不是季節上，因此需要保留的色調必須在其中重複寫出。`rules.weatherSpeed` 只在主世界或帶有 `weather.cycle` 的維度中生效。

| 鍵              | 必填              | 值                           | 預設 | 作用                                           |
| --------------- | ----------------- | ---------------------------- | ---- | ---------------------------------------------- |
| `seasons`       | 否                | 物件                         | 無   | 依年週期開啟和關閉維度的環境                   |
| `seasons.days`  | 有 `seasons` 時是 | 大於 0 的整數                | 無   | 每個季節持續的天數                             |
| `seasons.order` | 有 `seasons` 時是 | 環境名稱或名稱清單組成的清單 | 無   | 季節的先後順序。清單項目會讓其中的環境一起切換 |

## Galacticraft 天體

*世界*

`<namespace>/celestial/*.json` 與 `<namespace>/dimensions/*.json` 的 `galacticraft` 區段

安裝 Galacticraft 後，資源包可以把自己的星系、行星、衛星、小行星帶與太空站放上 Galacticraft 星圖，並讓資源包維度成為火箭可以飛往的地方。沒有 Galacticraft 時，這一切都會被跳過：`celestial/` 檔案被忽略，帶有 `galacticraft` 區段的維度不會被登錄，日誌會說明。

天體在地圖上的名稱取自資源包的語言檔案，使用 Galacticraft 所用的鍵：`solarsystem.<name>`、`star.<name>`、`planet.<name>`、`moon.<name>`，太空站則為 `satellite.<name>`。子星系使用 `planet.<name>`，小行星帶在恆星周圍使用 `planet.<name>`，在行星、子星系或衛星周圍則使用 `moon.<name>`。銀河系使用 `galaxy.<name>`，顯示在銀河系清單中，以及其星系的上方。

### 星系與僅地圖天體

*Galacticraft 天體*

`<namespace>/celestial/*.json`

這裡的檔案是一個星系、一個子星系，或是出現在地圖上但無處可降落的行星或衛星。

```json
{
  "kind": "system",
  "name": "ember",
  "galaxy": "milky_way",
  "galaxyImage": "mypack:textures/gui/ember_galaxy.png",
  "requires": ["mypack"],
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
  "ring": "mypack:textures/gui/ash_ring.png",
  "ringSize": 2.5,
  "tier": 3
}
```

```json
{
  "kind": "subsystem",
  "name": "kindle",
  "parent": "ember",
  "relativeSize": 0.6,
  "distance": 2.2,
  "orbitTime": 6,
  "galaxyspace": { "starType": "dwarf", "starColor": "red" }
}
```

```json
{
  "kind": "moon",
  "name": "clinker",
  "parent": "kindle"
}
```

| 鍵            | 必填     | 值                                      | 預設         | 作用                                                                                                                |
| ------------- | -------- | --------------------------------------- | ------------ | ------------------------------------------------------------------------------------------------------------------- |
| `kind`        | 是       | `system`、`planet`、`subsystem`、`moon` |              | 檔案要建立的東西                                                                                                    |
| `galaxy`      | 否       | 字串，`a-z`、`0-9` 與 `_`               | `milky_way`  | 星系所屬的銀河系。尚未被任何星系用過的名稱會開啟一個新的銀河系，擁有自己的地圖                                      |
| `galaxyImage` | 否       | 材質路徑                                | 銀河系的圖片 | 銀河系在銀河系清單中的圖塊。由該銀河系的第一個星系決定；僅在安裝 GalaxySpace 時讀取                                 |
| `mapPosition` | 星系必填 | `[x, y]` 或 `[x, y, z]`                 |              | 星系在銀河地圖上的位置。Galacticraft 會將數值乘以 500，太陽的外軌道約為 0.33，因此近距離的星系應使用小於 1 的數值。 |
| `star`        | 否       | [地圖鍵](#地圖鍵)的物件                 |              | 星系的恆星，畫在其中心                                                                                              |
| `requires`    | 否       | 清單，內容為模組 id 或資源包命名空間    | 無           | 除非全部都存在，否則會跳過此檔案                                                                                    |

這裡的行星、子星系或衛星採用[地圖鍵](#地圖鍵)，其 `tier` 預設為 `0`。它們在所有星系之後才放上地圖，先子星系，再行星，最後衛星，因此每個天體都可以環繞同一資源包中的天體運行。

安裝 GalaxySpace 後，AsmodeusCore 的銀河地圖會列出每個含有星系的銀河系並在它們之間切換，一次只繪製一個銀河系，因此 `mapPosition` 只在自己的銀河系內有意義。位於火箭出發地以外之銀河系中的天體，需要最高的火箭等級。Galacticraft 自己的地圖沒有銀河系切換功能：不論星系屬於哪個銀河系，它都一起繪製，銀河系只會顯示為星系上方的名稱。

衛星或小行星帶可以把另一顆衛星指定為 `parent`。它在銀河地圖上環繞那顆衛星運行，其軌道環以該衛星為中心，並在選取那顆衛星時顯示，就像選取行星時會顯示它的衛星一樣。它所指定的衛星可以來自任何 `celestial/` 檔案或維度，順序不限。

子星系是環繞星系之恆星運行的第二顆恆星。Galacticraft 把它視為行星，所以它像行星一樣運行，但地圖以太陽圖示繪製它，其等級永遠是 `-1`，且它沒有維度，因此沒有火箭能飛往那裡。只有衛星或小行星帶可以把它指定為 `parent`；這樣做的行星或太空站不會出現在地圖上，日誌會說明原因。安裝 GalaxySpace 後，AsmodeusCore 的地圖把它視為恆星，並在它周圍繪製其 `galaxyspace` `starColor` 的光暈。

### 地圖鍵

*Galacticraft 天體*

每個天體，不論是 `celestial/` 檔案、星系的 `star` 或維度的 `galacticraft` 區段，都用這些鍵把自己放在地圖上。

`<namespace>/celestial/*.json`

```json
{
  "kind": "planet",
  "name": "ash",
  "parent": "ember",
  "icon": "galacticraftcore:textures/gui/celestialbodies/mercury.png",
  "relativeSize": 0.5,
  "distance": 1.4,
  "scaledDistance": 1.4,
  "orbitTime": 2.5,
  "phaseShift": 1.2,
  "ring": "mypack:textures/gui/ash_ring.png",
  "ringSize": 2.5,
  "ringColor": "19E599",
  "tier": 3
}
```

| 鍵               | 必填             | 值           | 預設                                                | 作用                                                                                                                                                                                                                                                |
| ---------------- | ---------------- | ------------ | --------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`           | 否               | 字串         | 檔案名稱                                            | 天體的名稱，其語言鍵與每一個 `parent` 都使用它                                                                                                                                                                                                      |
| `parent`         | 衛星或太空站必填 | 名稱         | 行星、子星系或小行星帶為 `sol`                      | 行星或子星系所環繞的星系，衛星所環繞的行星、子星系或衛星，太空站所環繞的行星或衛星，或小行星帶所環繞的星系、行星、子星系或衛星                                                                                                                      |
| `icon`           | 否               | 材質路徑     | Galacticraft 的火星、月球、太陽、小行星或太空站圖示 | 地圖上的圖片                                                                                                                                                                                                                                        |
| `relativeSize`   | 否               | 浮點數       | `1.0`，衛星或太空站為 `0.2667`                      | 它在地圖上的大小                                                                                                                                                                                                                                    |
| `distance`       | 否               | 浮點數       | `1.0`，衛星為 `13`，太空站為 `9`                    | 環繞的距離有多遠                                                                                                                                                                                                                                    |
| `scaledDistance` | 否               | 浮點數       | `distance`                                          | 對 RDPL 天體而言不會產生任何看得見的變化。Galacticraft 的地圖只按 `distance` 放置和排列天體。唯一讀取它的是 AsmodeusCore，它用兩者之間的差值，在環顏色為其自身標記值之一的天體周圍繪製一圈環帶，而十六進位的 `ringColor` 永遠不可能是這些標記值之一 |
| `orbitTime`      | 否               | 浮點數，年   | `1.0`，衛星為 `100`，太空站為 `20`                  | 在地圖上繞行一圈所需的時間。負值為反向運行                                                                                                                                                                                                          |
| `phaseShift`     | 否               | 浮點數，弧度 | `0`，太空站則相互錯開                               | 在軌道上的起始位置。沒有此鍵的太空站，會從環繞同一行星的最後一座太空站（包含其衛星的太空站）之後 2.4 弧度處開始，因此沒有兩座會位於同一位置                                                                                                         |
| `ring`           | 否               | 材質路徑     | 無                                                  | 僅限行星或衛星。只含環的圖片，以傾斜視角呈現且寬大於高，繪製在地圖上天體的周圍，位於其後方與前方，GalaxySpace 繪製土星就是如此。烘焙進 `icon` 的環會把行星擠進圖示的方形裡。指定此天體的[天空天體](#sky-區段)會顯示同樣的環                         |
| `ringSize`       | 否               | 浮點數       | `2.5`                                               | 環的寬度，為天體在地圖上寬度的倍數                                                                                                                                                                                                                  |
| `ringColor`      | 否               | 十六進位顏色 | `19E599`                                            | 軌道線，在地圖繪製軌道線之處                                                                                                                                                                                                                        |
| `tier`           | 否               | 整數         | 維度為 `1`，太空站取其母天體的，其餘為 `0`          | 地圖顯示它所需的火箭等級。安裝 GalaxySpace 時，AsmodeusCore 的地圖改依距離算出等級，環繞另一顆恆星的天體需要最高等級，除非 `config/AsmodeusCore/core.conf` 中的 `enableNewTierSystem` 已關閉                                                        |

### `galacticraft` 區段

*Galacticraft 天體*

`<namespace>/dimensions/*.json`

維度檔案中的 `galacticraft` 區段，會讓該維度自成一個行星或衛星，或成為[小行星帶](#小行星帶)或[太空站](#太空站)。區段之外的所有內容，包括天空、物理、時間與天氣，仍屬於該維度，不論有沒有 Galacticraft 都以同樣方式運作；區段只放 Galacticraft 會讀取的內容。

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
    "reachable": true,
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

此區段採用[地圖鍵](#地圖鍵)，另外還有：

| 鍵                | 必填 | 值                                       | 預設                          | 作用                                                                                                                           |
| ----------------- | ---- | ---------------------------------------- | ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| `kind`            | 否   | `planet`、`moon`、`asteroids`、`station` | `planet`                      | 維度是什麼                                                                                                                     |
| `reachable`       | 否   | 布林值                                   | `true`                        | 關閉時，它在地圖上，但沒有火箭會前往                                                                                           |
| `minTier`         | 否   | 整數                                     | `tier`                        | 可降落的最低火箭等級                                                                                                           |
| `landing`         | 否   | `lander`、`parachute`、`balloons`        | `lander`                      | 玩家降落的方式。`balloons` 需要 Galacticraft Planets，沒有它時就是著陸艇。Galacticraft 的 `disableLander` 會強制為 `parachute` |
| `landingHeight`   | 否   | 浮點數                                   | `900`，降落傘為 `250`         | 玩家抵達時的高度                                                                                                               |
| `arrival`         | 否   | `departure`、`spawn`                     | `departure`                   | 降落在火箭起飛處的上方，或此維度出生點的上方                                                                                   |
| `exitHeight`      | 否   | 浮點數                                   | `-1`                          | 離開此維度的火箭在哪個高度離開。大於 `0` 時設定該高度；`-1` 沿用標準的 `1200`，在 Rubic 世界則是世界上限高度加 `944`           |
| `rocketGui`       | 否   | 材質路徑                                 | Galacticraft 主世界所用的那張 | 飛行畫面                                                                                                                       |
| `checklist`       | 否   | 清單，內容為字串                         | 無                            | 發射前顯示的 Galacticraft 檢查清單鍵                                                                                           |
| `meteorFrequency` | 否   | 浮點數                                   | 取自 `density`                | 流星墜落的稀有程度，每名玩家附近大約每 750 刻乘以此數值發生一次。`0` 會停止流星                                                |
| `fuelMultiplier`  | 否   | 浮點數                                   | `1.0`                         | 從這裡出發的火箭消耗的燃料                                                                                                     |
| `soundReduction`  | 否   | 浮點數                                   | 取自 `density`                | 這種空氣中聲音變小的程度                                                                                                       |
| `solarEnergy`     | 否   | 浮點數                                   | `1.0`                         | 太陽能板在此處的輸出                                                                                                           |
| `netherPortals`   | 否   | 布林值                                   | `false`                       | 地獄傳送門能否在此點燃                                                                                                         |

| `atmosphere` 鍵 | 必填 | 值                                                                                          | 預設                           | 作用                                                            |
| --------------- | ---- | ------------------------------------------------------------------------------------------- | ------------------------------ | --------------------------------------------------------------- |
| `gases`         | 否   | 清單，內容為 `NITROGEN`、`OXYGEN`、`CO2`、`WATER`、`METHANE`、`HYDROGEN`、`HELIUM`、`ARGON` | 無                             | 空氣。無即為真空，而火需要 `OXYGEN`                             |
| `breathable`    | 否   | 布林值                                                                                      | 有氧氣且無 CO2                 | 玩家不穿太空服是否能呼吸                                        |
| `corrosive`     | 否   | 布林值                                                                                      | `false`                        | 在沒有護盾控制器時侵蝕護甲                                      |
| `temperature`   | 否   | 浮點數                                                                                      | `0`                            | Galacticraft 的熱等級。低於 0 為冷，高於 0 為熱；隔熱墊可以應對 |
| `wind`          | 否   | 浮點數                                                                                      | 有氣體時為 `1.0`，沒有則為 `0` | 使旗幟飄動並驅動風力發電                                        |
| `density`       | 否   | 浮點數                                                                                      | `1.0`                          | 空氣有多濃。它決定流星與音效的預設值                            |

| `dungeon` 鍵 | 必填 | 值         | 預設 | 作用                                    |
| ------------ | ---- | ---------- | ---- | --------------------------------------- |
| `spacing`    | 否   | 整數，方塊 | `0`  | Galacticraft 地城之間的距離。`0` 為沒有 |
| `chest`      | 否   | 戰利品表   | 無   | 其箱子中的戰利品                        |

Galacticraft 只在自己的地形中建造地城，因此 `dungeon` 只在有東西執行 Galacticraft 地城生成器的地方才有意義；RDPL 地形則不會。

雨在行星上的外觀與聲音，由維度自己的 [`weather.rain`](#weather-區段) 區段決定，如上方範例所示。

**由誰登錄維度。** 可到達之天體的維度由 Galacticraft 登錄，因此火箭與多人遊戲的用戶端都看得到；帶有 `reachable: false` 的則由 RDPL 登錄。如果天體無法被放置，原因是其母天體未知或名稱已被占用，該維度不會被登錄，日誌會說明原因。

### 小行星帶

*Galacticraft 天體*

`<namespace>/dimensions/*.json`

`galacticraft` 區段帶有 `kind: "asteroids"` 的維度是小行星帶：Galacticraft 自己的小行星場，在空無一物的虛空中生成。它需要 Galacticraft Planets 以及類型為 `void` 的 `terrain`；缺少任一項，該維度都不會被登錄，日誌會說明原因。

```json
{
  "id": 73,
  "terrain": { "type": "void" },
  "sky": {
    "skyColor": "000000",
    "fogColor": "000000",
    "starBrightness": 1.0,
    "renderClouds": false,
    "sunriseColors": false,
    "sun": { "size": 6 }
  },
  "physics": { "gravity": 0.1 },
  "time": { "dayLength": 24000 },
  "galacticraft": {
    "kind": "asteroids",
    "name": "slag",
    "parent": "ember",
    "distance": 1.75,
    "tier": 3,
    "landing": "lander",
    "landingHeight": 500,
    "arrival": "spawn",
    "dungeon": { "spacing": 704, "chest": "mypack:chests/slag_dungeon" },
    "beltWidth": 0.15
  }
}
```

小行星帶的 `parent` 是星系、行星、子星系或衛星。環繞星系時，它在地圖上如行星般呈現；環繞行星、子星系或衛星時，則如衛星般呈現。它採用 [`galacticraft` 區段](#galacticraft-區段)的所有鍵，下列這些除外，小行星帶會忽略它們：

| 鍵              | 在小行星帶中                                                             |
| --------------- | ------------------------------------------------------------------------ |
| `landing`       | 玩家抵達時位於最近小行星上的進入艙中，與 Galacticraft 自己的小行星帶相同 |
| `landingHeight` | 進入艙自行決定高度                                                       |
| `arrival`       | 進入艙挑選小行星                                                         |
| `dungeon`       | 資源包的小行星帶沒有廢棄基地                                             |

小行星帶另外還有一個自己的鍵，Galacticraft 的地圖與 AsmodeusCore 的地圖都會繪製：

| 鍵          | 必填 | 值     | 預設 | 作用                                                                                                                                                                                                                                 |
| ----------- | ---- | ------ | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `beltWidth` | 否   | 浮點數 | `0`  | 小行星帶的區域在地圖上畫得多寬，單位與 `distance` 相同，以其軌道為中心，邊緣與填色使用 `ringColor`。`0` 則畫出單純的軌道線。Galacticraft 自己的小行星帶畫得是其距離的 0.08 倍寬。AsmodeusCore 的地圖只在其區域顯示開啟時才顯示該區域 |

天空、重力、時間與天氣是維度自己的鍵，與任何資源包行星相同。其中五項在小行星帶中的預設值不同，以配合 Galacticraft 自己的設定：

| 鍵                                   | 沒有它時                                                                         |
| ------------------------------------ | -------------------------------------------------------------------------------- |
| `sky.fogColor`                       | `000000`，因此霧與地平線是黑色                                                   |
| `sky.renderClouds`                   | `false`                                                                          |
| `sky.sunriseColors`                  | `false`，小行星帶沒有日落                                                        |
| `sky.sun`, `sky.bodies`, `sky.stars` | 繪製 Galacticraft 自己的小行星天空：一顆小小的白色太陽、沒有月亮，以及密集的星空 |
| `time.dayLength`                     | 沒有白天：太陽靜止在地平線上，永遠是白天                                         |

設定 `sun`、`bodies` 或 `stars` 中的任何一個，就會改畫資源包的天空，而 `sky.renderSky` 關閉時仍然什麼都不畫。設定 `time.dayLength`（即使是 `24000`）或 `sky.fixedTime`，會讓小行星帶擁有自己的一天。

### 太空站

*Galacticraft 天體*

`<namespace>/dimensions/*.json`

`galacticraft` 區段帶有 `kind: "station"` 的維度檔案，讓玩家能透過 Galacticraft 自己的地圖按鈕與它自己的軌道維度，在資源包行星或衛星的軌道上建造太空站。檔案描述的是太空站的種類，而不是單一太空站：玩家建造的每座太空站都是獨立的維度，由 Galacticraft 建立並保存。

```json
{
  "id": 80,
  "sky": {
    "skyColor": "000000",
    "starBrightness": 1.0,
    "renderSky": true,
    "renderClouds": true,
    "renderWeather": true,
    "sun": { "size": 12 }
  },
  "time": { "dayLength": 12000 },
  "galacticraft": {
    "kind": "station",
    "name": "cinder_station",
    "parent": "cinder",
    "tier": 2,
    "checklist": ["equip_oxygen_suit", "equip_parachute"],
    "showName": true,
    "recipe": {
      "ingotTin": 32,
      "ingotIron": 24,
      "minecraft:wool:14": 8
    }
  }
}
```

| 鍵          | 必填 | 值                 | 預設                          | 作用                                                                                                                                                           |
| ----------- | ---- | ------------------ | ----------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `kind`      | 是   | `station`          |                               | 讓檔案成為太空站                                                                                                                                               |
| `parent`    | 是   | 名稱               |                               | 它環繞的行星或衛星：由資源包維度建立、可到達的那個。衛星的太空站在地圖上環繞該衛星，並在選取該衛星時顯示                                                       |
| `tier`      | 否   | 整數               | 母天體的                      | 到達太空站所需的火箭等級                                                                                                                                       |
| `showName`  | 否   | 布林值             | `false`                       | 開啟時，地圖在 Galacticraft 寫 `Station: <owner>` 之處，會把每座太空站列在此檔案的 `name` 之下；擁有者那一行仍保留。擁有者重新命名的太空站會保留擁有者取的名稱 |
| `recipe`    | 否   | 成分對應數量的物件 | Galacticraft 自己的太空站配方 | 建造一座的花費。成分是礦物辭典名稱、`modid:item` 或 `modid:item:meta`                                                                                          |
| `checklist` | 否   | 清單，內容為字串   | 無                            | 發射前顯示的 Galacticraft 檢查清單鍵                                                                                                                           |

其餘[地圖鍵](#地圖鍵)決定太空站在地圖上的位置。`galacticraft` 區段中沒有其他內容適用：太空站採用 Galacticraft 自己的太空站空氣、重力與抵達方式。

在區段之外，太空站只讀取下列維度鍵，沒有其他：

| 鍵                                                       | 沒有它時                                |
| -------------------------------------------------------- | --------------------------------------- |
| `id`                                                     | 必填。太空站使用此 id 以及下一個 id     |
| `sky.skyColor`, `sky.fogColor`                           | Galacticraft 的軌道顏色                 |
| `sky.starBrightness`                                     | Galacticraft 的軌道星星                 |
| `sky.sun`, `sky.bodies`, `sky.stars`                     | Galacticraft 的軌道天空，下方是其母天體 |
| `sky.renderSky`, `sky.renderClouds`, `sky.renderWeather` | `true`                                  |
| `time.dayLength`                                         | `24000`                                 |

**Id。** `id` 與 `id + 1` 是維度類型 id，不是太空站的維度；兩者都必須未被占用。每座建造的太空站在建造當下取得下一個空閒的維度 id，Galacticraft 把該 id、擁有者與太空站名稱保存在世界存檔中，因此太空站在重新啟動後仍在同一個 id。它的存檔資料夾屬於 Galacticraft，為 `DIM_SPACESTATION<id>`。

**每個天體一種太空站。** 已有太空站的行星或衛星，不論來自另一個資源包或另一個模組，都保留原有的太空站：第二個不會被登錄，日誌會說明。

### GalaxySpace 與 ExtraPlanets

*Galacticraft 天體*

`<namespace>/celestial/*.json` 與 `<namespace>/dimensions/*.json` 的 `galacticraft` 區段

兩個 Galacticraft 附加模組會讀取比 Galacticraft 更多的天體資訊。`galaxyspace` 物件可放在 `celestial/` 的行星或衛星、星系的 `star` 或維度的 `galacticraft` 區段中，安裝 GalaxySpace 時生效。`extraplanets` 物件只能放在維度的 `galacticraft` 區段中，安裝 ExtraPlanets 時生效。沒有該附加模組時，其物件不起作用，日誌會針對該天體說明一次，該天體仍會作為普通的 Galacticraft 天體建立。行星可以環繞 GalaxySpace 的星系，例如 `tauceti`、`barnards`、`acentauri` 或 `proxima`，不論有沒有這些物件。

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
      "weather": "frozen_storm",
      "weatherFrequency": 2.0,
      "freezeBlocks": false
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
    "galaxyspace": { "starType": "subgiant", "starColor": "orange", "auraColor": "#FF7A30", "habitableZone": [1.2, 0.6] }
  }
}
```

維度的 `gravity` 與 `dayLength` 也是 GalaxySpace 為該天體顯示並使用的內容；它們不需要自己的鍵。

| `galaxyspace` 鍵    | 必填 | 值                                                                                               | 預設               | 作用                                                                                                                                   |
| ------------------- | ---- | ------------------------------------------------------------------------------------------------ | ------------------ | -------------------------------------------------------------------------------------------------------------------------------------- |
| `pressure`          | 否   | 浮點數                                                                                           | `0`                | 氣壓。高於 `10` 會引起噁心，高於 `25` 會緩速，高於 `35` 會失明，高於 `45` 會造成傷害，除非 GalaxySpace 的護甲或其設定阻止              |
| `radiation`         | 否   | 布林值                                                                                           | `false`            | 太陽輻射，會在開闊天空下日光中站立的玩家身上累積，除非其護甲能屏蔽                                                                     |
| `class`             | 否   | `selena`、`desert`、`terra`、`oceanide`、`gasgiant`、`icegiant`、`asteroid`、`titan`、`iceworld` | 無                 | GalaxySpace 地圖與指南書顯示的行星類別                                                                                                 |
| `orbitEccentricity` | 否   | `[x, y]`                                                                                         | `[0, 0]`           | 沿各軸拉伸 GalaxySpace 地圖所繪的軌道。`0` 或更小則讓該軸保持圓形                                                                      |
| `orbitOffset`       | 否   | `[x, y]`                                                                                         | `[0, 0]`           | 移動該軌道的中心                                                                                                                       |
| `freezeBlocks`      | 否   | 布林值                                                                                           | `true`             | 放置在密封空氣之外的 GalaxySpace 液態甲烷與氦氫，在強熱下是否變成火，在強寒下是否消失；水永遠不會改變                                  |
| `thermalVariation`  | 否   | 浮點數                                                                                           | `0`                | 熱等級在正午與午夜之間的擺動幅度，為 `atmosphere.temperature` 的比例，若該值為 `0` 則直接以此數值為幅度。需要 GalaxySpace 的進階熱系統 |
| `solarWind`         | 否   | 浮點數                                                                                           | 恆星大小的平方     | 此處 GalaxySpace 太陽風板的輸出                                                                                                        |
| `weather`           | 否   | `dust_storm`、`frozen_storm`、`lightning_storm`、`meteoric_rain`                                 | 無                 | 來來去去的風暴。沙塵暴會傷害站在開闊天空下的人，流星雨會落下流星，雷暴會落雷                                                           |
| `weatherFrequency`  | 否   | 浮點數                                                                                           | `1`                | 雷暴的稀有度：每 (數值 × 100) 次檢查約落雷一次，數值越大越稀有；低於 0.01 則不會發生。僅當 `weather` 為 `LIGHTNING_STORM` 時生效       |
| `starType`          | 否   | `subdwarf`、`dwarf`、`subgiant`、`giant`、`supergiant`、`hypergiant`、`blackhole`                | 無                 | 恆星的類型，顯示在 GalaxySpace 地圖上                                                                                                  |
| `starColor`         | 否   | `brown`、`red`、`orange`、`yellow`、`white`、`lightblue`、`blue`，或 `M1` 到 `O3` 的類別         | 無                 | 恆星的顏色等級，顯示在 GalaxySpace 地圖上                                                                                              |
| `auraColor`         | 否   | 十六進位顏色                                                                                     | `starColor` 的顏色 | GalaxySpace 地圖上恆星周圍光暈的顏色，取代 `starColor` 的顏色                                                                          |
| `habitableZone`     | 否   | `[distance, width]`                                                                              | `[0, 0]`           | GalaxySpace 地圖上標示為可居住的恆星周圍帶狀區域                                                                                       |

`starType`、`starColor`、`auraColor` 與 `habitableZone` 只在星系的 `star` 或子星系中讀取，`orbitEccentricity` 與 `orbitOffset` 在子星系中也會讀取；其餘僅在行星或衛星中讀取。GalaxySpace 以每個行星為單位繪製其風暴，因此資源包的風暴會起作用，但不會顯示自己的天空。

| `extraplanets` 鍵  | 必填 | 值                                                             | 預設                                | 作用                                                                                                       |
| ------------------ | ---- | -------------------------------------------------------------- | ----------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| `pressure`         | 否   | 整數，`0` 到 `100`                                             | 無                                  | ExtraPlanets 氣壓。大於 `0` 會傷害沒有 ExtraPlanets 太空衣的玩家，並顯示在其 HUD 上                        |
| `radiation`        | 否   | 整數，`0` 到 `100`                                             | ExtraPlanets 對其他附加模組的預設值 | ExtraPlanets 輻射，會在太空衣等級未涵蓋的玩家身上累積                                                      |
| `temperatureDay`   | 否   | 浮點數                                                         | `atmosphere.temperature`            | 白天的熱等級，採 ExtraPlanets 的刻度，大約從 `-140` 到 `100`。需要 ExtraPlanets 的第 3 與第 4 階隔熱墊選項 |
| `temperatureNight` | 否   | 浮點數                                                         | `temperatureDay`                    | 夜間的熱等級                                                                                               |
| `lander`           | 否   | `general`、`jupiter`、`saturn`、`mercury`、`neptune`、`uranus` | Galacticraft 的著陸艇               | 玩家降落時乘坐的 ExtraPlanets 著陸艇，在 `landing` 為 `lander` 時                                          |

### 修改其他模組的天體

*Galacticraft 天體*

`<namespace>/celestial/*.json`

`kind` 為 `override` 的檔案不是新增天體，而是修改 Galacticraft 或其他模組已經註冊的天體在地圖上的外觀。它在所有模組載入完畢後才套用，因此也能作用於較晚完成設定的 Galacticraft 自帶行星。沒有 Galacticraft 時，該檔案會被略過。

```json
{
  "kind": "override",
  "body": "star.sol",
  "requires": ["galaxyspace"],
  "icon": "mypack:textures/gui/celestialbodies/sun.png",
  "galaxyspace": { "starType": "giant", "starColor": "orange", "auraColor": "#FF7A30" }
}
```

```json
{
  "kind": "override",
  "body": "planet.saturn",
  "ring": "mypack:textures/gui/saturn_ring.png",
  "ringSize": 3
}
```

| 鍵            | 必填 | 值                             | 預設                 | 作用                                                                                                                                  |
| ------------- | ---- | ------------------------------ | -------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| `kind`        | 是   | `override`                     |                      | 使該檔案成為對既有天體的修改                                                                                                          |
| `body`        | 是   | 翻譯鍵                         |                      | 要修改的天體，採用 Galacticraft 的鍵格式，例如 `star.sol` 或 `planet.saturn`                                                          |
| `icon`        | 否   | 紋理路徑                       | 天體自帶的           | 地圖上的圖片                                                                                                                          |
| `ring`        | 否   | 紋理路徑                       | 天體自帶的（如果有） | 僅限行星或衛星。只含光環的圖片，格式與地圖的 `ring` 鍵相同。它會取代天體已有的任何光環                                                |
| `ringSize`    | 否   | 浮點數                         | `2.5`                | 光環的寬度，為天體在地圖上寬度的倍數                                                                                                  |
| `galaxyspace` | 否   | 物件                           |                      | `starType`、`starColor` 與 `auraColor`，讀取方式與 [GalaxySpace 與 ExtraPlanets](#galaxyspace-與-extraplanets) 相同。需要 GalaxySpace |
| `requires`    | 否   | 模組 ID 或資源包命名空間的清單 | 無                   | 除非全部存在，否則略過該檔案                                                                                                          |

`body` 是天體名稱的鍵：系統主恆星為 `star.<name>`，其餘為 `planet.<name>`、`moon.<name>` 或 `satellite.<name>`。Galacticraft 自帶的天體是 `star.sol`、`planet.overworld`、`planet.mercury`、`planet.venus`、`planet.mars`、`planet.jupiter`、`planet.saturn`、`planet.uranus`、`planet.neptune`、`planet.asteroids`（Asteroids 模組）、`moon.moon` 與 `satellite.spacestation.overworld`。不區分大小寫，因此 `planet.saturn` 也會匹配 ExtraPlanets 的 `Saturn`。每個帶有該鍵的天體都會被修改，沒有匹配任何天體的鍵會記錄為錯誤。恆星的 `starType` 與 `starColor` 只取代這兩個值，GalaxySpace 為它保存的其餘內容（例如適居帶）保持不變。

天體的名稱和資訊面板的文字不在這裡設定。它們來自 lang 鍵 `<type>.<name>` 與 `gui.message.<name>.<line>.<0 or 1>.name`，其中 line 是 `daynightcycle`、`surfacegravity`、`surfacecomposition`、`atmosphere` 或 `meansurfacetemp`。資源包中的 lang 檔會像覆蓋其他內容一樣覆蓋它們。與 `body` 不同，lang 鍵會保留模組註冊該天體時使用的大小寫：ExtraPlanets 的水星是 `planet.Mercury`。

各處顯示的內容：

| 修改   | Galacticraft 地圖 | GalaxySpace 地圖                | ExtraPlanets 地圖                |
| ------ | ----------------- | ------------------------------- | -------------------------------- |
| `icon` | 是                | 是，星系清單讀取主恆星的圖示    | 是                               |
| 光暈   | 否                | 是                              | 否                               |
| 光環   | 是                | 是，取代 GalaxySpace 繪製的光環 | 是，取代 ExtraPlanets 繪製的光環 |

override 有幾樣東西不會改動：GalaxySpace 和 ExtraPlanets 地圖上天體的描述（它們根據自己的資料推算）、始終不可見的黑洞光暈，以及軌道線的顏色。光環不能被移除，只能被取代。玩家在其他模組的維度內看到的天空由該模組繪製，因此只有與該模組自身紋理資源路徑相同的資源包檔案才能影響它。

## 傳送門與閘門

*世界*

`<namespace>/blocks/*.json`

傳送門就是一般的方塊定義，因此適用同樣的路徑規則：檔案路徑即方塊的註冊名稱。

`portal` 方塊帶有一個 `portal` 區段：

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
    "owned": true,
    "walkIn": true
  },
  "variants": { "ruby_portal": { "meta": 0, "hardness": -1, "light": 11 } }
}
```

| 鍵                | 必填 | 值             | 預設    | 作用                                                     |
| ----------------- | ---- | -------------- | ------- | -------------------------------------------------------- |
| `dimension`       | 是   | 整數           |         | 傳送到的維度                                             |
| `returnDimension` | 否   | 整數           | `0`     | 返回時傳送到的維度                                       |
| `gate`            | 否   | 閘門名稱       | 無      | 必須處於開啟狀態才能通過的閘門                           |
| `cooldown`        | 否   | 整數，單位為刻 | `60`    | 同一名玩家再次使用前的冷卻時間                           |
| `platform`        | 否   | 布林值         | `true`  | 抵達時是否建造著陸平台                                   |
| `platformBlock`   | 否   | 方塊名稱       | 石頭    | 該平台的組成方塊                                         |
| `sound`           | 否   | 音效名稱       | 無      | 通過時播放                                               |
| `owned`           | 否   | 布林值         | `true`  | 只有建造者及其允許的人可以使用。有主的傳送門同時免疫爆炸 |
| `walkIn`          | 否   | 布林值         | `false` | 走進方塊即可傳送，就像地獄傳送門那樣。關閉時需要手動使用 |

### 傳送門框架

*傳送門與閘門*

`<namespace>/portalframes/*.json`

檔案路徑即框架的註冊名稱，維度隨後在 `frames` 中引用它。

框架只是玩家需要搭建的圖樣，僅此而已：它說明哪些方塊構成邊緣、洞口在哪裡，卻不說明傳送門通往何處。這是刻意的設計，因為維度是認領框架而非擁有框架，兩個維度可以認領同一個框架。

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

| 鍵          | 必填 | 值                                 | 預設       | 作用                                                         |
| ----------- | ---- | ---------------------------------- | ---------- | ------------------------------------------------------------ |
| `name`      | 否   | 字串                               | 檔名       | 日誌中使用的名稱                                             |
| `axis`      | 否   | `vertical`、`horizontal` 或 `both` | `vertical` | 是像地獄傳送門那樣豎立、像終界傳送門那樣平放，還是兩者皆可   |
| `legend`    | 是   | 單一字元對應方塊的物件             | 無         | 各列可以使用的方塊。帶狀態的方塊名稱與其他地方的讀取方式相同 |
| `rows`      | 是   | 字串清單                           | 無         | 圖樣，從最上面一列開始繪製                                   |
| `maxWidth`  | 否   | 整數                               | `21`       | `*` 最寬可拉伸出的洞口寬度                                   |
| `maxHeight` | 否   | 整數                               | `21`       | `*` 最高可拉伸出的洞口高度                                   |

有三個字元不是方塊。`.` 是傳送門所在的洞口，沒有洞口的框架會被拒絕。空格表示框架不在意的格子，因此要畫 L 形的邊框，把角落留空即可。`*` 表示重複：只有 `*` 的一列會依玩家實際搭建的數量重複其上一列，列內的 `*` 則以同樣方式重複它前面的字元。它也可以重複零次，所以把所有 `*` 劃掉後讀到的圖樣就是能夠點亮的最小形態，而上表的最大值則是最大形態。不含 `*` 的圖樣是精確的，玩家必須照樣搭建，不能多也不能少。

豎立的框架不論沿哪條水平軸、朝哪個方向搭建都能被辨識，所以建造者面朝哪個方向都無所謂。平放的框架則四個旋轉方向都能辨識。

**框架能有多大由資源包決定。** `maxWidth` 和 `maxHeight` 是 `*` 能拉伸到的最大洞口，小於它、直到下限之間的任何尺寸都會被接受，因此資源包可以決定它的傳送門最大是原版的 21，還是只有 4。下限是一名玩家的大小：豎立的框架要求洞口至少寬 1、高 2，平放的至少 1 乘 1，永遠達不到這個尺寸的圖樣會在載入時被拒絕並在日誌中留下一行，而不會變成一個沒人能走過去的框架。

**框架能拉伸得越多，尋找的成本就越高。** 列 `*` 與欄 `*` 同時存在，代表要嘗試兩個最大值之內的所有組合，所以兩個方向都能拉伸到 21 的框架就是 441 種圖樣。搜尋會放棄而不是當掉，並在日誌中說明，這就是該調低某個最大值或去掉一處拉伸的訊號。

**沒有任何機制阻止框架使用以打火石點燃的黑曜石，但框架會優先生效。** 框架的偵測發生在物品執行自身作用之前，所以這樣的框架會在原本應出現地獄傳送門的位置開啟資源包的維度。若想保留原版傳送門，請換一種方塊或另一種點火器。

### 用框架開啟維度

*傳送門與閘門*

`<namespace>/dimensions/*.json`

維度透過攜帶 `portal` 區段來經由框架開啟。框架與點燃它的物品共同決定維度，所以同一種框架形狀會因點燃方式不同而通往不同的地方。

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

| 鍵              | 必填 | 值                          | 預設                        | 作用                                             |
| --------------- | ---- | --------------------------- | --------------------------- | ------------------------------------------------ |
| `frames`        | 是   | 框架名稱清單                | 無                          | 開啟該維度的框架                                 |
| `ignitedBy`     | 否   | 物品名稱                    | `minecraft:flint_and_steel` | 玩家手持何物來點燃框架                           |
| `color`         | 否   | 十六進位色碼                | 白色                        | 傳送門繪製所用的顏色                             |
| `return`        | 否   | `built`、`player` 或 `none` | `built`                     | 是否提供返程：自動建造、由玩家建造，或完全不提供 |
| `gate`          | 否   | 閘門名稱                    | 無                          | 必須處於開啟狀態才能通過的閘門                   |
| `cooldown`      | 否   | 整數，單位為刻              | `60`                        | 同一名玩家再次通過前的冷卻時間                   |
| `platform`      | 否   | 布林值                      | `true`                      | 抵達時是否建造著陸平台                           |
| `platformBlock` | 否   | 方塊名稱                    | 石頭                        | 該平台的組成方塊                                 |
| `sound`         | 否   | 音效名稱                    | 無                          | 通過時播放                                       |
| `owned`         | 否   | 布林值                      | `false`                     | 只有點燃者及其允許的人可以使用                   |

洞口中的方塊不必由資源包編寫。帶有 `portal` 區段的維度會獲得一個專屬方塊，名為 `<namespace>:portal_<dimension>`，使用遊戲內建的傳送門材質並依 `color` 著色，走進去即可傳送而無須手動使用，且無法破壞。顏色會與材質相乘，原理與 `tintindex` 相同，所以 `#C77DFF` 保留了地獄的紫色，而 `#4CFFB0` 會讓它變成毒綠色。若想要完全不是原版材質的傳送門，請自行編寫一個一般的 `portal` 方塊，搭配自己的模型，以及用[像素圖](#以像素圖編寫的材質)繪製的材質，其中 `tint` 可以在兩種顏色之間漸變。

`return` 決定另一側會發生什麼。`built` 會依玩家搭建的尺寸立起同樣的框架並將其點亮，這與原版的行為一致。`player` 不會建造任何東西，但允許在那邊點亮同樣的框架，因此回家的路需要自己去找並自己搭。`none` 則完全拒絕在該維度中點亮該框架，這趟旅程只能單程。

**一個框架，多個維度。** 框架與點燃它的物品這一組合決定了維度，所以同一個 `standing_gate`，用打火石點燃與用資源包自己的點火器點燃，會開啟兩個不同的地方，各有各的顏色。兩個維度認領同一個框架*且*同一個物品是資源包的錯誤：第二個會被拒絕並在日誌中說明，而不是悄悄讓其中一個勝出。

破壞框架的任何一個方塊都會使傳送門熄滅，與原版一樣。

### 閘門

*傳送門與閘門*

`<namespace>/gates/*.json`

檔案路徑即閘門的註冊名稱，傳送門隨後在 `gate` 中引用它。

此處一次列出所有鍵。實際的檔案只寫需要的鍵。

```json
{
  "dimension": 12,
  "name": "The Ruby Gate",
  "scope": "player",
  "teamless": "player",
  "open": false,
  "unlock": {
    "hold": "mypack:ruby_key",
    "consume": "mypack:ruby",
    "consumeCount": 4,
    "craft": "mypack:ruby_pickaxe",
    "advancement": "mypack:story/ruby",
    "killed": "minecraft:wither",
    "killedCount": 2,
    "killedDrops": "mypack:ruby_key",
    "killedTally": "player",
    "team": "red",
    "objective": "quests",
    "scoreAtLeast": 5,
    "teamAndScore": "alone"
  },
  "unlockedMessage": "%dim% is now open",
  "blockedMessage": "You need %item% to enter %dim%",
  "safeReturn": true,
  "portalBlocks": ["mypack:ruby_portal"],
  "requires": ["mypack"]
}
```

| 鍵                | 必填 | 值                             | 預設                             | 作用                                                                                                                                                                                                                                                                                                                          |
| ----------------- | ---- | ------------------------------ | -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `dimension`       | 否   | 整數                           | 無                               | 它守衛的維度。沒有該鍵的閘門不守衛任何維度，只鎖住指定它的[傳送通道](#傳送通道)                                                                                                                                                                                                                                               |
| `name`            | 否   | 字串                           | 檔名                             | 顯示給玩家                                                                                                                                                                                                                                                                                                                    |
| `scope`           | 否   | `player`、`global`、`team`     | `player`                         | 一次針對一名玩家、針對整個世界，還是針對開啟者的整個隊伍（隊伍指資料包 `<namespace>/teams` 資料夾中的一方；玩家可透過列在該方的 `players` 中、使用 `/rdpl team join <名稱>`（該方將 `joinable` 設為 false 時除外）、單獨使用 `/rdpl team join` 加入 `balance` 中人數最少的一方，或經 `picks` 抽取加入，見 [隊伍](#誰會加入)） |
| `teamless`        | 否   | `player`、`refuse`             | `player`                         | 範圍為 `team` 時，不屬於任何隊伍的玩家如何處理：單獨為其解鎖，或拒絕通過                                                                                                                                                                                                                                                      |
| `open`            | 否   | 布林值                         | `false`                          | 是否一開始就處於開啟狀態                                                                                                                                                                                                                                                                                                      |
| `unlock`          | 否   | 物件                           |                                  | 用什麼來開啟。見下文                                                                                                                                                                                                                                                                                                          |
| `unlockedMessage` | 否   | 字串                           | `%dim% is now open`              | 開啟時顯示                                                                                                                                                                                                                                                                                                                    |
| `blockedMessage`  | 否   | 字串                           | `You need %item% to enter %dim%` | 拒絕時顯示                                                                                                                                                                                                                                                                                                                    |
| `safeReturn`      | 否   | 布林值                         | `false`                          | 被擋下的返程仍會降落在安全的地方，而不是被拒絕                                                                                                                                                                                                                                                                                |
| `requires`        | 否   | 模組 id 或資源包命名空間的清單 | 無                               | 除非全部存在，否則略過此閘門                                                                                                                                                                                                                                                                                                  |
| `portalBlocks`    | 否   | 方塊名稱清單                   | 所有傳送門                       | 將閘門限定於這些傳送門方塊，讓一個維度可以同時有受守衛的門與開放的門                                                                                                                                                                                                                                                          |

`unlock` 接受 `hold`（必須持有的物品）、`consume` 搭配 `consumeCount`（`1`）、`craft`（必須已合成過的物品）、`advancement`，以及 `killed`（實體名稱，誰擊殺一隻，閘門就為誰開啟，所以頭目可以掌握通往一個世界的鑰匙）搭配 `killedCount`（`1`，一隻不夠時使用），依範圍設定按每名玩家或整個世界累計。`killedRank`（生物等級名稱）只計入抽到該等級的生物的擊殺。加上 `killedDrops`（物品名稱）後，計入的擊殺會把該物品放在擊殺者腳邊，而不是開啟閘門，並且重新開始計數，所以鑰匙可以再次取得，並交給從未參與戰鬥的人；對同一個物品設定 `hold` 或 `consume` 的條件，即可讓它成為鑰匙。`%item%`、`%mob%` 和 `%dim%` 會自動填入。生物掉落的鑰匙在這裡不需要任何特殊處理：讓生物掉落該物品，再以 `hold` 或 `consume` 設定條件即可。`team`，以及 `objective` 搭配 `scoreAtLeast`，會為屬於該計分板隊伍、且在該目標中分數不低於該值的玩家開啟閘門，這與卡片的 `when` 所用的條件相同；兩者也可以單獨使用。條件在玩家穿過時檢查，符合後閘門會像其他解鎖方式一樣依其範圍開啟。範圍為 `team` 時，解鎖歸開啟者的隊伍所有：隊伍中的每個人都能通過，包括之後加入的玩家；不屬於任何隊伍的玩家開啟時，只對自己開放。`teamAndScore` 決定這兩項如何計算：`alone`（預設）時它們單獨就能開啟閘門；`with` 時它們成為附加條件，只有在滿足另一個解鎖鍵之後，且玩家同時屬於該隊伍並達到該分數，閘門才會開啟。`killedTally` 設為 `team` 時，`killedCount` 會合計擊殺者所屬隊伍的全部擊殺，而不是依每名玩家各自計算（`player`，預設）；不屬於任何隊伍的擊殺者仍單獨計數。`teamless` 設為 `refuse` 時，範圍為 `team` 的閘門會拒絕不屬於任何隊伍的玩家，而不是讓其只為自己解鎖（`player`，預設）。

**傳送通道用的閘門。** [傳送通道](#傳送通道)在 `gate` 中指定一個閘門，在該閘門對玩家開啟之前一直對其鎖定，範圍、解鎖規則與訊息和傳送門相同。為同一網路的所有傳送通道指定同一個閘門，就能把整個網路當作一體鎖上。要透過在傳送通道處消耗物品來開啟閘門，請把該傳送通道方塊列入 `portalBlocks`，並給閘門設定 `consume` 物品。下面這個閘門沒有 `dimension`，傳送通道方塊用 `"gate": "mypack:northgate_network"` 指定它：

```json
{
  "name": "Northgate Network",
  "scope": "player",
  "unlock": { "consume": "minecraft:emerald", "consumeCount": 3 },
  "portalBlocks": ["mypack:northgate"]
}
```

`/rdplserver gate list` 對沒有維度的閘門顯示 `-`。

## Rubic 世界

*世界*

`terrain` 設定中的 `rubicWorld` 會把維度的世界重建為 16×16×16 的立方體，而不是 256 格高的區塊柱，因此它的底部和頂部可以位於資源包指定的任何位置。地形生成本身沒有改變：原版的生成器和其他模組的世界生成照常運作，產生相同的陸地，只是在它的上方和下方多了世界。

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

所有鍵都在 `terrain` 群組中，與其他設定一樣寫在世界範本的 `settings` 區塊內：

| 鍵                                 | 值                  | 預設    | 作用                                                                                                                                                              |
| ---------------------------------- | ------------------- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `rubicWorld`                       | 布林值              | `false` | 啟用 Rubic 世界                                                                                                                                                   |
| `worldMinHeight`                   | 整數，16 的倍數     | `-64`   | 世界底部                                                                                                                                                          |
| `worldMaxHeight`                   | 整數，16 的倍數     | `320`   | 世界頂部                                                                                                                                                          |
| `rubicWorldDimensions`             | 整數清單            | 空      | 哪些維度成為 Rubic 維度。留空代表所有維度                                                                                                                         |
| `rubicWorldDimensionsAreBlacklist` | 布林值              | `false` | 改為把清單視為要排除的維度                                                                                                                                        |
| `terrainOffset`                    | 整數，16 的非負倍數 | `0`     | 將整個原版地形視窗向上平移。適用於一般的分層預設組合：設為 `272` 的平坦世界，其地表約在 y 275，高於原版的世界頂部。預設組合要求的裝飾與結構仍會在未平移的高度生成 |

**高度。** `worldMinHeight` 必須低於 `worldMaxHeight`，兩者都必須是 16 的倍數，並且都要在設定中 `rubicHeightLimit` 允許的範圍內（預設上下各 `4096` 格；僅限設定，絕不是資源包的鍵）。否則會被拒絕並在日誌中留下一行，世界將以 `-64` 到 `320` 建立。高度是有代價的：每 16 格就是每根區塊柱中多出的一個立方體，所以記憶體、磁碟和預先生成時間都隨之增加，`rubicHeightLimit` 的設定註解中列出了具體數字。

**地形視窗。** 維度自身的生成器保有自己的高度，主世界為 256 格，而 `terrainOffset` 平移的正是這個視窗。`worldMinHeight` 和 `worldMaxHeight` 只會在視窗的周圍增加空間，絕不會在視窗內部。抬高頂部並不會抬高陸地，只是增加天空；降低底部也不會加深生成器挖出的洞穴，只是增加深層世界。海平面同樣位於視窗之內，所以它會隨 `terrainOffset` 一起移動，並且由世界類型決定，而不是由任何 Rubic 鍵決定。要讓地表在世界中更高，請提高 `terrainOffset`。要在其上方或下方留出更多空間，請調整高度。視窗之外的每個立方體仍然會在每根區塊柱中生成並計算光照，所以更高的頂部無論上面有沒有東西填充，都要花費預先生成時間，而一旦 `skyStone` 填充了它，還要額外占用記憶體和磁碟。

**平移後的視窗對其他模組的影響。** 人口生成（population）是以區塊柱為單位執行，因此模組註冊的每個生成器仍然是每個區塊執行一次，座標不會被轉換。改變的是它自己的運算落在哪裡。向世界詢問地面在哪裡的生成器，不論是透過最高的實心方塊還是降水高度，都會跟隨平移後的地形：兩者皆能辨識 Rubic，涵蓋樹木、花朵和大部分裝飾。計算絕對高度的生成器，例如常見的「y 小於 64 的隨機高度」礦石模式，仍會在該高度寫入，而平移之後，那裡是遠在陸地之下的填充層或深層世界。海平面同樣不會被平移，所以以它為判斷依據的生成器讀到的是未平移的數字。這些寫入也會落在人口生成所載入的立方體之外，並在區塊柱生成人口時帶入它們自己的立方體。較大的 `terrainOffset` 適合自行描述生成的資源包，而不是疊加在另一個資源包的世界生成之上。若只是要多一些空間，往深處擴展的成本較低：視窗之下有一個完整的生成器，擁有自己的石頭、洞穴、礦脈、含水層和地城，並讓地表維持在其他所有生成器假設的高度，而視窗之上的空間是資源包必須自行布置的景觀。

**每個存檔決定一次。** 維度是否為 Rubic 維度及其高度，會在第一次載入時寫入存檔，並從此固定：Rubic 世界即使移除資源包仍然是 Rubic 世界，其高度之後也無法更改。主世界以外的維度沿用主世界的高度。既有的 Anvil 陸地不會被轉換，Rubic 將陸地存放在它自己的 `region2d`/`region3d` 檔案中，所以已經以 Anvil 格式生成過的維度會重新開始生成地形。請只對新世界啟用。

**排除維度。** 未列在 `rubicWorldDimensions` 的維度會在同一個存檔中保持一般的 Anvil 世界，Rubic 與 Anvil 維度可以自由混用。對於生成器直接寫入區塊內部、而不經過一般人口生成流程的維度，這是正確的做法。不論清單為何，伺服器類別被其他模組取代的世界會被略過，並在日誌中留下一行說明。

**視窗之外的空間。** 生成器自身的範圍保持其一般的形態，Rubic 世界在其周圍增加的空間，會用該範圍終止處的方塊填充：主世界下方是石頭，上方是空氣。頂部以基岩封住的維度，尤其是地獄，被視為封閉，所以它上方的空間保持為空，而不是塞滿屋頂之下的地獄石。屋頂本身不受影響。`deepStone` 指定視窗下方空間的方塊，`skyStone` 指定視窗上方空間的方塊。

**CubicChunks。** 不支援同時執行兩者。安裝了 CubicChunks 而資源包要求 `rubicWorld` 時，載入會停止並顯示訊息：請移除 CubicChunks，或從資源包中拿掉 `rubicWorld`，讓 CubicChunks 來建立世界。

### 將世界遷移到 CubicChunks 及遷回

*Rubic 世界*

**檔名。** Rubic 世界把它的區塊柱存放在 `region2d/<x>.<z>.2rdr`，把它的立方體存放在 `region3d/<x>.<y>.<z>.3rdr`。超出區域檔案容量的項目會放進旁邊的資料夾，名稱為檔名加上 `.ext`。在這些名稱出現之前建立的世界使用 `.2dr` 和 `.3dr`：維度載入時，模組會自行重新命名這些檔案和資料夾，每個維度留下一行日誌，所以較舊的世界不需要手動處理。

**開啟的區域檔案。** `chunks` 設定類別中的 `regionCacheLimit` 限制 Rubic 世界同時保持開啟的區域檔案數量，最大 `8192`。每個開啟的檔案大約占用 16 kB 記憶體和一個檔案控制代碼，而玩家在一般視距下會用到約三十個，因此繁忙的世界需要數百個。模組會在 64 與該上限之間自行調節：堆積記憶體寬裕時多保持一些，吃緊時就放掉一些檔案，並且從不會要求超過機器所允許的開啟檔案上限的四分之一。預設值 `0` 把上限交給模組決定。

**開啟 CubicChunks 世界。** 當資源包要求 `rubicWorld`，而正在開啟的世界被標記為 CubicChunks 世界時，模組會在動手之前先詢問，方式與 Forge 詢問缺少的註冊項目相同：單人遊戲中是確認畫面，專用伺服器上是主控台訊息，以 `/fml confirm` 或 `/fml cancel` 回答，或事先以 `-Dfml.queryResult=confirm` 指定。選擇是，Forge 的世界備份會以 zip 寫入 saves 資料夾，世界就地轉換為 Rubic 世界，然後繼續載入。選擇否，載入會停止，世界不會有任何更動。

**轉換器。** 同樣的轉換及其反向轉換，也可以在遊戲之外執行。儲存庫附有 [`scripts/convert_rubic_world.py`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/scripts)，可將 Rubic 世界轉成 CubicChunks 世界，或將 CubicChunks 世界轉成 Rubic 世界。它需要 Python 3，除此之外不需要任何東西。請先關閉遊戲並備份世界，再看過試跑結果之後才正式執行。

```
python3 scripts/convert_rubic_world.py to-cubic "saves/My World" --dry-run
python3 scripts/convert_rubic_world.py to-cubic "saves/My World"
```

| 引數             | 作用                                    |
| ---------------- | --------------------------------------- |
| `to-cubic`       | Rubic 世界變成 CubicChunks 世界         |
| `to-rubic`       | CubicChunks 世界變成 Rubic 世界         |
| `<world folder>` | 存檔資料夾，即包含 `level.dat` 的那一個 |
| `--dry-run`      | 列出每項變更，但不做任何變更            |

**它會變更什麼。** 在每個維度中，區域檔案及其 `.ext` 資料夾改用另一方的名稱（Rubic 用 `.2rdr` 和 `.3rdr`，CubicChunks 用 `.2dr` 和 `.3dr`），`data/rdplRubicData.dat` 變成 `data/cubicChunksData.dat`，或反過來，高度保持不變，儲存格式和相容性生成器則改為另一個模組所用的名稱。最後，`level.dat` 和 `level.dat_old` 中的標記會在 `isRubicWorld` 與 `isCubicWorld` 之間互換。立方體和區塊柱本身不會被重寫。

**它會拒絕什麼。** `level.dat` 標記與所要求方向不符的世界、另一個模組沒有的儲存格式或相容性生成器，以及目標已存在的重新命名。拒絕不會變更任何東西，被中斷的執行可以重複進行。

**什麼不會被帶過去。** 轉換後的世界只保留兩個模組都能理解的內容。資源包定義的方塊和維度在純 CubicChunks 下並不存在，而且兩個模組都會為對方儲存的立方體重新計算光照。

### 立方體串流載入

*Rubic 世界*

**立方體串流載入。** 四個 `chunks` 鍵決定立方體如何送達玩家，以及何時再次釋放。它們只在 Rubic 世界中起作用，預設值就是該子系統調校時所用的數值，所以不去動它們的資源包不會付出任何代價。

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

| 鍵                         | 值                 | 預設  | 作用                                                                                                                                             |
| -------------------------- | ------------------ | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| `verticalCubeLoadDistance` | 整數，單位為立方體 | `8`   | 區塊載入票證在玩家上方和下方各保持載入多少個立方體。同名的視訊設定滑桿是用戶端自己的視距，由遊玩的人而不是資源包設定                             |
| `cubesSentPerTick`         | 整數，單位為立方體 | `649` | 一個刻內最多可以傳送給玩家多少個立方體。調高它能更快填滿視野範圍，但會讓每個刻的封包變大；封包仍會在 1024 個立方體或 512 KB 時拆分，以先到者為準 |
| `cubeGenMillisPerRound`    | 整數，單位為毫秒   | `50`  | 一個刻最多可以花多久生成玩家正在等待的立方體                                                                                                     |
| `cubeGCInterval`           | 整數，單位為刻     | `200` | 無人觀看的立方體多久釋放一次                                                                                                                     |

**用戶端。** 視訊設定新增一個垂直彩現距離滑桿，是彩現距離的垂直版本（設定中的 `verticalCubeLoadDistance`，屬於遊玩的人）。`terrain` 群組中的其他一切，包括預先生成、世界物理、出生點和邊界，對 Rubic 世界一律照常適用。

## 深層世界

*世界*

另外九個 `terrain` 鍵會用現代風格的生成來填充 Rubic 世界在原版地形視窗周圍開闢出的空間。它們只在 Rubic 世界中起作用：

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

| 鍵             | 值                                             | 預設      | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| -------------- | ---------------------------------------------- | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `deepStone`    | `namespace:block`，中繼值寫成 `@meta`          | 無        | 視窗下方的世界所用的方塊，例如資源包自己的深板岩。它會在視窗最低的八層中融入視窗的石頭，就像現代版本融合深板岩的方式                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `skyStone`     | `namespace:block`，中繼值寫成 `@meta`          | 無        | 視窗上方的世界在其表面之下所用的方塊，由與雕刻下方深層世界相同的雜訊塑造成浮空陸地，所以下面是洞穴的地方，上面就是島嶼。留空則讓視窗上方的空間保持為空，一如既往。陸地帶有區塊柱自己的地表，頂層方塊及其下三層取自生態域，所以主世界的島嶼讀起來是草方塊覆在泥土上，再覆在這個方塊上。生態域或洞穴區域可以指定自己的 `skyStone`、`skyIslands` 和 `skyThickness`，所以一個高度帶或一個區域可以擁有自己的島嶼，逐區塊柱解析，區域優先於高度帶，高度帶優先於生態域。這些島嶼本身也會被裝飾：視窗上方的每個立方體都會針對該立方體內的表面執行生態域自己的地物，所以樹木、草、花、蘑菇、甘蔗和各種斑塊落在島嶼上，而不是像原版那樣散布在整根區塊柱上，而上方的生態域高度帶會以自己的數量進行裝飾。生態域自己的附加內容也會在那裡執行，而不只是共用的那些：沙漠水井、叢林西瓜、黑森林濃密的樹冠和蘑菇、針葉林巨石、冰原生態域上的冰刺，以及每個生態域放置的高花和高草。島嶼從不由會掉落的方塊構成：生態域的表面若會是沙子或礫石，島嶼就改用砂岩，其他情況則用它自己的 `skyStone`，因為在半空中沒有東西能撐住會掉落的方塊。牲畜群以同樣方式逐立方體放置，所以動物在陸地生成時就出現在島嶼上。表面深度隨雜訊在一到四層填充方塊之間變化，所以島嶼邊緣不是均勻的硬殼，而且硬殼沿著坡度測量而不是垂直向下，所以陡峭的面能保住泥土，而不會薄到消失。表面跟隨天空本身回報的生態域，先是洞穴區域，再是依高度分帶的生態域，最後是下方的區塊柱，所以在天空區域中指定 `minecraft:mesa`，就會在任何高度產生條帶狀硬化黏土的島嶼，與地面上相同的條帶，指定沙漠則會得到它的沙子，並因為沒有東西能撐住會掉落的方塊而轉為砂岩。動物會在上面棲息，可用 `spawning` 群組中的 `skyAnimals` 關閉。天空有多少變成陸地由 `skyIslands` 決定，其預設值 0.5 是群島：在生成的世界中，視窗上方大約八個立方體有七個是空的，最滿的一層約占三分之一，所以天空是用來飛越，而不是用來行走。把它調低到接近 0.2，這一帶就會閉合成起伏的天花板，上面立著丘陵，中間約五分之四是實心，可以在上面建造，但已不再是島嶼。島嶼在 `worldMaxHeight` 之下八格處停止，所以頂部不會被平切在世界頂部上，樹木和植物上方也有空間；`caves` 則如先前一樣填到世界頂部。每個 Rubic 維度都有自己的視窗，所以這會填充每個視窗的上方：在視窗高 128 的地獄，那就是基岩屋頂上方的空間，而打開頂部的接縫會把屋頂本身清除 |
| `skyShape`     | `islands` 或 `caves`                           | `islands` | 視窗上方的世界被塑造成什麼。`islands` 是浮空陸地。`caves` 是被洞穴貫穿的實心岩石，即深層世界自己的處理方式翻轉到上方，成品約 86% 是實心，與深層世界相同的岩石與洞穴比例。兩種方式都不會淹水，因為視窗上方不會查詢含水層。僅在 `skyStone` 指定了方塊時才會讀取                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `skyIslands`   | 數字，`-1` 到 `1`                              | `0.5`     | 天空聚成島嶼的容易程度。越低，島嶼散布在越多的天空中，其下的陰影也越深；越高，則島嶼越少越小。預設值讓大約八個立方體有七個是空的，並在約三分之一處達到高峰；約 `0.2` 時，這一帶閉合成帶有丘陵的天花板，中間約五分之四是實心。僅在 `skyStone` 指定了方塊且 `skyShape` 為 `islands` 時才會讀取                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `skyThickness` | 數字，`0` 到 `8`                               | `2.0`     | 島嶼有多實心。越高，島嶼越飽滿；越低，島嶼越中空，邊緣也逐漸薄到消失。僅在 `skyStone` 指定了方塊且 `skyShape` 為 `islands` 時才會讀取                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `skyHeights`   | 兩個整數，先最低後最高                         | 無        | 島嶼可到達的最低和最高方塊，與 `oreVeins` 的高度一樣，從視窗底部起算。留空則填滿視窗上方的整個世界，在高聳的世界中那是大片的天空。僅在 `skyStone` 指定了方塊時才會讀取                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `noiseCaves`   | `off`、`deep`、`world`                         | `off`     | 現代風格的雜訊洞穴：奶酪洞窟、義大利麵隧道、靠近地表的洞口，以及大型空間中的石柱。`deep` 只在視窗下方雕刻，`world` 則雕刻整個世界                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `deepRavines`  | 布林值                                         | `false`   | 在視窗下方的世界中切出原版風格的峽谷，也就是長而陡峭的大峽谷。峽谷穿過深層世界自己的流體時會沿用它們，在熔岩線之下充滿熔岩，在其上則保留含水層的水或其壓力牆，所以它絕不會把所切開的地方排乾。現代版本只在視窗內雕刻峽谷，所以除非開啟這個選項，深層世界沒有峽谷                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `oreVeins`     | 清單，內容為 `ore,extra,filler,lowest,highest` | 無        | 大型條帶狀礦脈，大部分是 `filler` 方塊，其中散布著 `ore`，並有少見的機率出現 `extra`，後者可以留空。高度從視窗底部起算，所以負數可到達深層世界                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |

水與熔岩在那裡各有表現。大量熔岩填滿最低的幾層，而上方的洞穴帶有局部含水層，採用與現代版本相同的取樣點與壓力機制，由 26.1.2 移植而來，所以靜止的水池位於各自的水位，而在兩個水位相遇或水與熔岩相遇之處，會有由雜訊塑造的深層石頭牆。在海洋之下，洞穴會向海平面淹水，就像現代版本把含水層與地表連結的方式。

**依維度設定。** `deepStone`、`noiseCaves`、`skyStone`、`skyShape`、`deepRavines` 和 `oreVeins` 各自接受單一值或清單，清單項目寫成 `dimension=value` 即僅適用於該維度。只要有任何項目指名了維度，這些項目就會直接決定該維度，未指名維度的項目在那裡會被忽略，所以單獨一個 `"1="` 後面不接任何內容，即可對該維度關閉這個鍵。沒有指名維度的值會作用於所有 Rubic 維度，唯獨終界除外，除非資源包指名它，否則終界保持虛空：填滿終界等於終結終界，而且填滿的終界還會讓原版的閘道搜尋失明，它會從 1024 格處往回走，只要持續遇到含有方塊的區塊就不會停。指名它，你就會得到你所要求的。

`noiseCaves` 開啟時，深層世界還會產生現代風格的怪物房間：視窗下方每根區塊柱約嘗試四次，且離世界底部不到六格的範圍不會出現，所以地城生怪磚及其箱子戰利品會像現代版本那樣出現在深層洞穴中。

`world` 範圍還會淘汰兩項會與重做後的洞穴衝突的原版遺留機制。原版在 y 10 以下的洞穴中傾倒的熔岩改由含水層判定，所以舊的熔岩視窗不復存在，而原版埋在地下的水湖，包括地表池塘，也會停止生成，就像現代版本捨棄它們一樣；取而代之的是含水層自己的水池。

`deep` 範圍則保持原版的高度帶不變，包括熔岩視窗，只封住兩者相接的接縫。視窗最底層的熔岩或水，若其正下方有深層洞穴開口，就會變成深層石頭，因此視窗不會排進下方的洞穴。

## 洞穴區域

*世界*

`<namespace>/caveregions/*.json`

檔案路徑即區域的名稱，世界生成項目隨後在 `caveRegions` 中引用它。那裡的單純名稱會採用該項目自己的命名空間。

在地下鋪上具名的區域，是現代洞穴生態域在資源包中的對應機制。地下被劃分為圓角的儲存格，寬度為 `caveRegionCells` 格、高度為 `caveRegionCellsY` 格，兩者都是 `terrain` 鍵，每個儲存格依權重擲出一個區域，或是不擲出任何區域。區域所做的一切都由種子碼決定性地產生，所以區塊之間彼此一致，而不必寫入跨越邊界的內容。

### 區域檔案

*洞穴區域*

此處一次列出所有鍵。實際的檔案只寫需要的鍵。

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
  "structureLoot": "mypack:chests/cave_shrine",
  "skyStone": "minecraft:sandstone",
  "skyIslands": 0.2,
  "skyThickness": 2.0,
  "ambientSound": "minecraft:block.water.ambient",
  "soundChance": 0.02,
  "particle": "dripWater",
  "particleChance": 0.002
}
```

| 鍵                  | 值               | 預設               | 作用                                                                                                                                                                                                                                                                                          |
| ------------------- | ---------------- | ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `weight`            | 整數             | `1`                | 此區域贏得的儲存格比例。`0` 會將其關閉                                                                                                                                                                                                                                                        |
| `minHeight`         | 整數             | 世界底部           | 區域存在的高度帶的底部                                                                                                                                                                                                                                                                        |
| `maxHeight`         | 整數             | `48`               | 該高度帶的頂部。中心位於高度帶之外的儲存格絕不會選中此區域                                                                                                                                                                                                                                    |
| `dimensions`        | 整數清單         | 全部               | 區域出現在哪些維度                                                                                                                                                                                                                                                                            |
| `floorCover`        | 方塊             | 無                 | 取代區域內洞穴地面的頂層方塊                                                                                                                                                                                                                                                                  |
| `floorChance`       | 0.0 到 1.0       | `1.0`              | 地面有多少被覆蓋                                                                                                                                                                                                                                                                              |
| `ceilingCover`      | 方塊             | 無                 | 取代區域內洞穴天花板的方塊                                                                                                                                                                                                                                                                    |
| `ceilingChance`     | 0.0 到 1.0       | `1.0`              | 天花板有多少被覆蓋                                                                                                                                                                                                                                                                            |
| `coverReplace`      | 方塊清單         | 任何類似石頭的方塊 | 覆蓋物可以取代什麼                                                                                                                                                                                                                                                                            |
| `waterLevel`        | 整數             | 無                 | 固定區域內每個含水層取樣點的水位，使其洞穴淹水到這個高度。水位會被限制在熔岩地面之上兩格到海平面之下一格之間（y 62，再加上任何 `terrainOffset`），所以更高的值淹得也不會比這更高。區域與乾燥洞穴相接處的牆，由與現代含水層相同的壓力雜訊塑造，而且水絕不會碰到熔岩地面。需要開啟 `noiseCaves` |
| `spawns`            | 清單             | 無                 | 在區域內生成的生物，與生態域的 `spawns` 接受的項目相同：`entity`、`type`（monster、creature、ambient 或 water）、`weight`，以及表示群體大小的 `min` 和 `max`。在地形視窗之下，看得見天空的位置交給生態域處理，覆蓋物也是如此；在視窗之上，唯一的陸地是天空生成的陸地，清單在開闊處同樣適用    |
| `keepDefaultSpawns` | 布林值           | `false`            | 保留生態域自己的生成清單，與區域的清單並用。關閉時，區域內完全由區域的清單取代它                                                                                                                                                                                                              |
| `structures`        | 清單             | 無                 | 每個區域儲存格放置一次的結構，位於儲存格的中心，並貼合到洞穴地面，就像現代版本讓洞穴生態域擁有自己的地標那樣。項目是 `namespace:name` 範本，或是 `{ "structure": "...", "weight": 3 }`，用於在多個之間做選擇                                                                                  |
| `structureChance`   | 0.0 到 1.0       | `1.0`              | 區域的每個儲存格實際得到結構的機率                                                                                                                                                                                                                                                            |
| `structureLoot`     | `namespace:path` | 無                 | 已放置結構內每個箱子第一次被開啟時，所填入戰利品的戰利品表                                                                                                                                                                                                                                    |
| `biome`             | 生態域名稱       | 無                 | 區域在其體積內回報的生態域，以 3D 生態域的形式寫入立方體。賦予區域自己的植被、草和水的顏色、音樂與環境音效，並讓原版的生成權重讀取它。上方的地表不受影響，因為只會寫入區域所占據的儲存格                                                                                                      |
| `skyStone`          | 方塊             | 世界設定           | 此區域內的天空島嶼在其表面之下所用的方塊，所以一個區域可以擁有自己的島嶼                                                                                                                                                                                                                      |
| `skyIslands`        | `-1` 到 `1`      | 世界設定           | 區域內的島嶼門檻。越低，聚集的陸地越多                                                                                                                                                                                                                                                        |
| `skyThickness`      | `0` 到 `8`       | 世界設定           | 區域的島嶼有多實心                                                                                                                                                                                                                                                                            |
| `ambientSound`      | 音效名稱         | 無                 | 不時對站在區域內的玩家播放的音效，就像現代生態域加入它們自己的洞穴音效那樣。由伺服器只傳送給該名玩家                                                                                                                                                                                          |
| `soundChance`       | 0.0 到 1.0       | `0.0111`           | 每個刻播放 `ambientSound` 的機率                                                                                                                                                                                                                                                              |
| `particle`          | 粒子名稱         | 無                 | 在區域內玩家周圍顯示的粒子，為遊戲的粒子名稱之一，例如 `dripWater`、`happyVillager` 或 `depthsuspend`。只有區域內的空氣會顯示它                                                                                                                                                               |
| `particleChance`    | 0.0 到 1.0       | `0.00625`          | 現代生態域的粒子密度：每個刻在 16 格內約 667 個位置，按每 10 刻一步統計，其中這一比例的位置會顯示粒子。統計數量上限為每 10 刻 200 個位置，所以高於約 0.03 的值不會再有差別                                                                                                                    |

### 儲存格

*洞穴區域*

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

| 設定                    | 類型           | 預設  | 作用                                                                                                                                |
| ----------------------- | -------------- | ----- | ----------------------------------------------------------------------------------------------------------------------------------- |
| `caveRegionCells`       | 整數，單位為格 | `128` | 區域儲存格的寬度                                                                                                                    |
| `caveRegionCellsY`      | 整數，單位為格 | `64`  | 區域儲存格的高度                                                                                                                    |
| `caveRegionPlainWeight` | 整數           | `4`   | 每個儲存格擲骰時，單純、無區域的地下所占的權重。越高，越多地下沒有任何區域：在只有一個權重為 1 的區域時，約五分之一的儲存格會得到它 |

地下有多少保持單純，由 `caveRegionPlainWeight` 這個 `terrain` 鍵決定，預設為 `4`：在只有一個權重為 1 的區域時，約五分之一的儲存格會得到該區域。覆蓋物在頂部有遮蔽時才會套用，所以延伸到地面以上的區域絕不會在地表顯示；在地形視窗之上，它們也會在開闊處套用，因為那裡的一切都是天空生成所造出的陸地。覆蓋物在每個洞穴中都能運作，不論是哪個生成器挖出的；`waterLevel` 是唯一需要雜訊洞穴的鍵，因為淹水是在洞穴雕刻時放置的。

### 區域內的地物

*洞穴區域*

地物透過一般[世界生成項目](#世界生成項目)上的兩個鍵與之連結。`caveRegions` 列出項目可以生成的區域，在放置的位置上檢查，所以蘑菇、水晶或其他任何東西只會出現在它們的區域內。`snap` 會先把每次嘗試垂直移到最近的洞穴表面：站立的東西用 `floor`，懸掛的東西用 `ceiling`。類似滴水石的區域不需要新的形狀：

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

`minecraft:air` 的 `replace` 很重要：已放置的形狀所覆寫的內容會對照 `replace` 檢查，其預設為石頭，所以任何建在開放洞穴空間中的東西都需要列出空氣。同一個項目搭配 `"snap": "floor"` 並且不設 `hanging`，即可長出對應的石筍。區域篩選適用於每一種已放置的形狀；`belt` 和 `field` 依自己的規則放置，會忽略它。

---

# 生成世界

## 世界生成項目

*生成世界*

`<namespace>/worldgen/*.json`

檔案路徑即項目的名稱，而 `belt` 和 `field` 形狀以它作為雜訊的種子，所以重新命名檔案會改變它所生成的內容。

描述會生成的東西。每個項目都是一個由**擴散**放置、並受允許位置篩選的**形狀**。

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
  "prospectAs": "Hematite",
  "then": ["mypack:quartz_halo=2", "empty=1", { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }],
  "thenCount": 1,
  "thenSpread": 6,
  "thenDepth": { "min": -8, "max": -2 },
  "requires": ["quark"],
  "shape": { "type": "cluster" },
  "spread": { "type": "even" }
}
```

只有 `block` 是必填的；其餘都可以省略並採用預設值。一個方塊不夠時，`blocks` 會取代 `block`，下方有它自己的範例。

### 它放置什麼

*世界生成項目*

| 鍵         | 必填 | 值                   | 預設                    | 作用                                                                            |
| ---------- | ---- | -------------------- | ----------------------- | ------------------------------------------------------------------------------- |
| `block`    | 是   | 方塊名稱             |                         | 放置的東西                                                                      |
| `meta`     | 否   | 整數                 | `0`                     | 該方塊的哪個變體                                                                |
| `blocks`   | 否   | 物件清單             | 無                      | 加權清單，用來取代單一方塊。見下文                                              |
| `size`     | 否   | 整數或範圍           | `8`                     | 一次嘗試放置多少方塊，或帶有半徑的形狀有多大                                    |
| `attempts` | 否   | 整數或範圍           | `8`                     | 每個區塊嘗試幾次                                                                |
| `sparse`   | 否   | 布林值               | `false`                 | 將方塊分散放置，而不是擠在一起                                                  |
| `shape`    | 否   | 物件                 | `{ "type": "cluster" }` | 它呈現的形態。見[形狀](#形狀)                                                   |
| `spread`   | 否   | 物件                 | `{ "type": "even" }`    | 放在哪裡。見[擴散](#擴散)                                                       |
| `replace`  | 否   | 方塊名稱或物件的清單 | `["minecraft:stone"]`   | 它可以取代什麼。見下文                                                          |
| `adjacent` | 否   | 方塊名稱或物件的清單 | 無                      | 只在該位置周圍接觸的 26 個方塊中有其中之一時才放置。與 `replace` 的三種形式相同 |

### 可生成的位置

*世界生成項目*

| 鍵                       | 必填 | 值                   | 預設       | 作用                                                                                                                                                                                                                             |
| ------------------------ | ---- | -------------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `minHeight`              | 否   | 整數                 | `0`        | 它會放置的最低 y                                                                                                                                                                                                                 |
| `maxHeight`              | 否   | 整數                 | `64`       | 它會放置的最高 y                                                                                                                                                                                                                 |
| `snap`                   | 否   | `floor` 或 `ceiling` | 無         | 先將每次嘗試垂直移到最近的洞穴地面或天花板                                                                                                                                                                                       |
| `snapDepth`              | 否   | 整數                 | `0`        | `snap` 之後再越過表面多遠：從地面往下，從天花板往上。`0` 停留在緊貼表面的開放空間中，`1` 是表面方塊本身，`2` 是它後面的那個。它可以覆寫什麼仍由 `replace` 決定，所以資源包可以靠這個把方塊成帶地放在剛好位於地下，而不是地面之上 |
| `dimensions`             | 否   | 整數清單             | 所有維度   | 它在哪些維度中執行                                                                                                                                                                                                               |
| `dimensionsAreBlacklist` | 否   | 布林值               | `false`    | 將該清單改為要避開的維度                                                                                                                                                                                                         |
| `biomes`                 | 否   | 生態域名稱清單       | 所有生態域 | 它在哪些生態域中執行。單純的名稱會先以資源包自己的命名空間讀取，再以 `minecraft` 讀取                                                                                                                                            |
| `biomeTypes`             | 否   | 字典類型清單         | 無         | 依類型指定生態域，例如 `FOREST` 或 `NETHER`                                                                                                                                                                                      |
| `biomesAreBlacklist`     | 否   | 布林值               | `false`    | 將這些清單改為要避開的項目                                                                                                                                                                                                       |
| `minTemperature`         | 否   | 浮點數               | `-100.0`   | 它會生成的最冷生態域                                                                                                                                                                                                             |
| `maxTemperature`         | 否   | 浮點數               | `100.0`    | 它會生成的最熱生態域                                                                                                                                                                                                             |
| `minRainfall`            | 否   | 浮點數               | `-100.0`   | 它會生成的最乾燥生態域                                                                                                                                                                                                           |
| `maxRainfall`            | 否   | 浮點數               | `100.0`    | 它會生成的最潮濕生態域                                                                                                                                                                                                           |
| `minDistanceFromSpawn`   | 否   | 整數，單位為格       | `0`        | 離世界出生點多遠才開始                                                                                                                                                                                                           |
| `caveRegions`            | 否   | 區域名稱清單         | 無         | 只在這些[洞穴區域](#洞穴區域)內生成                                                                                                                                                                                              |

### 地表標誌與跟隨者

*世界生成項目*

| 鍵                | 必填 | 值                                | 預設       | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ----------------- | ---- | --------------------------------- | ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `indicators`      | 否   | 清單，內容為 `block=weight`       | 無         | 在已生成的礦脈上方，散落在地表的方塊，讓玩家得以知道地下有什麼；請依礦脈的內容挑選。`empty=weight` 會讓一處保持空白                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `indicatorCount`  | 否   | 整數或範圍                        | `1`        | 每條已生成的礦脈得到多少個地表位置                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `indicatorSpread` | 否   | 整數，單位為格                    | `0`        | 標誌可以落在超出礦脈範圍多遠的地方                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `then`            | 否   | 清單，內容為 `name=weight` 或物件 | 無         | 在這個項目生成之後立即從它長出、附屬於它的世界生成項目：跟隨者的原點設在這條礦脈邊緣的外側，方向由 `thenSpread` 和 `thenDepth` 決定，所以兩者相接。項目是 `name=weight`，或是帶有 `name`、`weight` 以及自己的 `spread` 和 `depth`（整數或範圍）的物件，這些會僅對該跟隨者覆寫礦脈的設定，所以一份清單可以讓鑽石尖端向下、分支向側面。單純的名稱以此資源包的命名空間讀取，`empty=weight` 不排入任何東西。跟隨者保有自己的形狀、方塊、大小和 `replace`，但略過它自己的嘗試次數、機率、高度帶和生態域篩選，而且它自己也可以帶有 `then`，要多深由資源包決定；已在同一條鏈中生成過的項目會使鏈停止 |
| `thenCount`       | 否   | 整數或範圍                        | `1`        | 每條已生成的礦脈從該清單中挑出多少個不同的跟隨者，每個項目最多一次，所以數量等於清單長度時，每一個都會長出來                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `thenSpread`      | 否   | 整數，單位為格                    | 形狀的半徑 | 跟隨者生長的方向可以向側面偏多遠，從負的這個值到正的這個值之間擲出                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `thenDepth`       | 否   | 整數或範圍                        | `0`        | 方向向下（負）或向上偏多遠。`0` 且沒有側向偏移時，跟隨者會筆直向下垂掛                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `prospectAs`      | 否   | 字串                              | 檔名       | 探勘物品在讀數中如何稱呼這個項目，例如 `Hematite`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

### 回溯生成與需求

*世界生成項目*

| 鍵            | 必填 | 值                             | 預設       | 作用                         |
| ------------- | ---- | ------------------------------ | ---------- | ---------------------------- |
| `retrogen`    | 否   | 布林值                         | `false`    | 也在已存在的區塊中生成       |
| `retrogenKey` | 否   | 字串                           | 設定中的鍵 | 僅對此項目覆寫回溯生成鍵     |
| `requires`    | 否   | 模組 id 或資源包命名空間的清單 | 無         | 除非全部存在，否則略過該項目 |

### 加權方塊

*世界生成項目*

當一個項目不夠時，`blocks` 會取代 `block`。權重是相對的，所以 80 和 20 就是四比一。

```json
{
  "blocks": [
    { "block": "minecraft:wool", "meta": 2, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "lime" } }
  ]
}
```

| 鍵           | 必填 | 值             | 預設 | 作用                                               |
| ------------ | ---- | -------------- | ---- | -------------------------------------------------- |
| `block`      | 是   | 方塊名稱       |      | 放置的東西                                         |
| `meta`       | 否   | 整數           | `0`  | 哪個變體                                           |
| `weight`     | 否   | 整數           | `1`  | 這一個相對於其他的被選中頻率                       |
| `properties` | 否   | 屬性對值的物件 | 無   | 依名稱指定方塊狀態屬性，用於沒有自己中繼資料的狀態 |

即使使用 `blocks`，檔案的最上層仍然需要 `block` 和 `meta`，把第一個項目放在那裡是個好選擇。

### 取代目標

*世界生成項目*

`replace` 是一份清單，每個項目接受三種形式之一。

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

| 形式           | 範例                                                                      | 符合什麼                   |
| -------------- | ------------------------------------------------------------------------- | -------------------------- |
| 名稱           | `"minecraft:stone"`                                                       | 該方塊的每個狀態           |
| 名稱與中繼資料 | `"minecraft:stone:3"`                                                     | 僅該中繼資料，此處為閃長岩 |
| 物件           | `{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }` | 僅該狀態                   |

物件形式也可以用 `meta` 取代 `properties`，與冒號形式相同。使用 `"minecraft:air"` 可在開放空間中生成。

### 相鄰方塊

*世界生成項目*

`adjacent` 接受與 `replace` 相同的三種形式，並在其之上加上第二個條件：只有當接觸該位置的 26 個方塊（面、邊和角）中至少有一個符合清單時，才會使用該位置。省略時，不做任何檢查。

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

這會讓硫磺只放置在砂岩已經對洞穴或地表開放的位置，並放過被埋住的砂岩。尚不存在的區塊中的鄰居會被視為不符合，而不會被讀取，所以這個檢查絕不會造成區塊生成。

每種形狀都遵守它，因為它是決定單一方塊能否被取用的一部分。`geode` 分別指定它的外殼和填充物，這兩者的放置不受此檢查影響。

只指定了未註冊方塊的項目會連同錯誤一併被略過，而不是到處生成。

### 跟隨者項目

*世界生成項目*

世界生成項目的 `then` 清單中的項目是帶權重的名稱，或是當該跟隨者需要自己的方向時使用的物件。

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| 鍵       | 必填 | 值             | 預設                  | 作用                                                             |
| -------- | ---- | -------------- | --------------------- | ---------------------------------------------------------------- |
| `name`   | 是   | 項目名稱       |                       | 從這個項目長出的世界生成項目。單純的名稱以此資源包的命名空間讀取 |
| `weight` | 否   | 整數           | `1`                   | 這個跟隨者相對於清單中其他項目的被挑中頻率                       |
| `spread` | 否   | 整數，單位為格 | 該項目的 `thenSpread` | 這個跟隨者的方向可以向側面偏多遠，僅適用於此項目                 |
| `depth`  | 否   | 整數或範圍     | 該項目的 `thenDepth`  | 這個跟隨者的方向向下（負）或向上偏多遠，僅適用於此項目           |

`name=weight` 是只含這兩項的物件的簡寫，而 `empty=weight` 不排入任何東西。由於 `spread` 和 `depth` 是逐項設定，一份清單可以讓同一條礦脈的鑽石尖端筆直向下、分支向側面。

## 形狀

*生成世界*

帶有 `type` 的 `shape` 區塊。某個類型未列出的鍵會被它忽略。

此處一次列出所有鍵。實際的檔案只寫需要的鍵。標示給某個類型的鍵只由該類型讀取。

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
    "middle": "mypack:calcite",
    "budding": "mypack:budding_amethyst",
    "buddingChance": 0.083,
    "crystal": "mypack:amethyst_cluster",
    "crystalChance": 0.35,
    "crack": 0.95,
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
    "lootTable": "mypack:chests/crypt",
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
    "richAt": 0.88,
    "poorAt": 0.4,
    "rarity": 400,
    "rarityIsPerChunk": false
  }
}
```

| 類型         | 它造出什麼                                                                                                                                                                                                                                                            |
| ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `cluster`    | 預設的團塊，即礦脈。使用 `size`                                                                                                                                                                                                                                       |
| `largevein`  | 帶有分支、蜿蜒的長礦脈。使用 `size`                                                                                                                                                                                                                                   |
| `plate`      | 平坦的圓盤                                                                                                                                                                                                                                                            |
| `geode`      | 帶有外殼的中空空腔                                                                                                                                                                                                                                                    |
| `decoration` | 地表散布，例如花或蘑菇。使用 `size`                                                                                                                                                                                                                                   |
| `tree`       | 一整棵樹                                                                                                                                                                                                                                                              |
| `vines`      | 附著在既有事物上的藤蔓。使用 `size`                                                                                                                                                                                                                                   |
| `basin`      | 向中間加深的碗狀                                                                                                                                                                                                                                                      |
| `spire`      | 逐漸變細的柱子                                                                                                                                                                                                                                                        |
| `nodule`     | 粗糙的球                                                                                                                                                                                                                                                              |
| `vent`       | 碰到東西就停止的窄柱                                                                                                                                                                                                                                                  |
| `imprint`    | 你的 `.nbt` 範本之一。能放進一個區塊內的範本會被微調位置，使它完整落在正在建造的區塊內，而不是伸進尚未建好的鄰近區塊，不論它朝向哪個方向；比區塊更大的範本只會放在周圍地面已經存在的地方                                                                              |
| `belt`       | 橫跨數個區塊的團塊，用於石頭區域                                                                                                                                                                                                                                      |
| `field`      | 同時對每個方塊算出的礦脈，與硬度組共用它們的形狀                                                                                                                                                                                                                      |
| `vein`       | 以種子雜訊場圍繞原點算出的礦床，做法與 Immersive Geology 相同：每個區塊寫入其 24 格範圍內觸及它的每條礦脈的自己那一片，所以不會連鎖生成，而且 `/rdplserver vein` 能在陸地生成之前就告訴你礦脈會在哪裡。使用 `size`、`attempts`、`rarity` 和高度帶；`pattern` 決定外觀 |
| `spring`     | 從洞穴牆壁滲出的流體：放在上方、下方和三側都是岩石、有一側開放的位置，並設為流動                                                                                                                                                                                      |

### 大小與形態

*形狀*

| 鍵              | 適用於                                   | 值                           | 預設                           | 作用                                                                                              |
| --------------- | ---------------------------------------- | ---------------------------- | ------------------------------ | ------------------------------------------------------------------------------------------------- |
| `type`          | 全部                                     | 上述形狀之一                 | `cluster`                      | 哪一種形狀                                                                                        |
| `radius`        | plate、geode、basin、spire、nodule、vent | 整數或範圍                   | `6`                            | 它有多寬                                                                                          |
| `height`        | plate、geode、basin、spire、vent、tree   | 整數或範圍                   | `1`，geode 為 `8`，tree 為 `5` | 它有多高或多厚                                                                                    |
| `width`         | geode                                    | 整數或範圍                   | `12`                           | 空腔的整體寬度                                                                                    |
| `plane`         | plate、basin、spire、vent                | `circle`、`square`           | `circle`                       | 它的底面形狀                                                                                      |
| `slim`          | plate、largevein、nodule                 | 布林值                       | `false`                        | plate：薄一層。largevein：單一方塊的分支。nodule：中空的殼                                        |
| `hanging`       | spire、vent                              | 布林值                       | `false`                        | 從天花板向下生長，而不是從地面向上                                                                |
| `taper`         | spire                                    | `straight`、`bell`、`needle` | `straight`                     | 寬度如何向尖端收窄。`straight` 均勻變窄，`bell` 在下方維持寬度再驟降，`needle` 立刻變細成一個長尖 |
| `outline`       | geode                                    | 方塊名稱                     | 無                             | 外殼方塊                                                                                          |
| `fill`          | geode                                    | 方塊名稱                     | 無                             | 填充中間的東西。省略時，中間是中空的                                                              |
| `middle`        | geode                                    | 方塊名稱                     | 無                             | 位於本體與 `outline` 之間的殼，即現代紫水晶晶洞的方解石                                           |
| `budding`       | geode                                    | 方塊名稱                     | 無                             | 取代面向中空中心的本體方塊，就像紫水晶母岩一樣。需要 `fill`                                       |
| `buddingChance` | geode                                    | 0.0 到 1.0                   | `0.083`                        | 那些本體方塊有多少會成為母岩                                                                      |
| `crystal`       | geode                                    | 方塊名稱                     | 無                             | 在 `budding` 方塊旁邊長入空腔中，就像紫水晶簇一樣                                                 |
| `crystalChance` | geode                                    | 0.0 到 1.0                   | `0.35`                         | 那些位置有多少會長出一個                                                                          |
| `crack`         | geode                                    | 0.0 到 1.0                   | `0`                            | 晶洞被劈開的機率：從中間穿過每一層、通向一側的管道，以 `fill` 填滿。現代紫水晶晶洞使用 `0.95`     |

### 放置

*形狀*

| 鍵                 | 適用於           | 值           | 預設                | 作用                                                                                                                                                                      |
| ------------------ | ---------------- | ------------ | ------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `surface`          | decoration、tree | 方塊名稱清單 | 無                  | 它會放在什麼上面                                                                                                                                                          |
| `seeSky`           | decoration       | 布林值       | `true`              | 只放置在看得見天空的位置                                                                                                                                                  |
| `checkStay`        | decoration       | 布林值       | `true`              | 只放置在方塊能存活的位置                                                                                                                                                  |
| `stackHeight`      | decoration       | 整數或範圍   | `1`                 | 疊放多少個                                                                                                                                                                |
| `scatterX`         | decoration、tree | 整數         | `8`                 | 它向側面游移多遠                                                                                                                                                          |
| `scatterY`         | decoration、tree | 整數         | `4`                 | 它在垂直方向游移多遠                                                                                                                                                      |
| `scatterZ`         | decoration、tree | 整數         | `8`                 | 它向側面游移多遠                                                                                                                                                          |
| `rarity`           | 任何             | 整數         | 無（belt 為 `400`） | 每隔這麼多個區塊放置一次。對 belt 而言，這會拉開各個 belt 的間距；對任何其他形狀而言，它會管控整個項目，所以每這麼多個區塊中只有一個會擲出它的 `attempts`。`field` 忽略它 |
| `rarityIsPerChunk` | 任何             | 布林值       | `false`             | 將 `rarity` 改為每個區塊得到多少次放置                                                                                                                                    |

### 樹木

*形狀*

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

沒有 `log` 或 `leaves` 的 `tree` 什麼也不會生成，並在日誌中說明。指定 `structure`，或在 `structures` 下指定數個，會在每個位置種下該範本，而不是長出一棵樹，此時就不需要 `log` 或 `leaves`；使用範本的樹所讀取的 `turns`、`mirrors`、`integrity`、`lootTable` 和 `locateAs` 與 `imprint` 完全相同。

| 鍵       | 適用於 | 值       | 預設    | 作用           |
| -------- | ------ | -------- | ------- | -------------- |
| `log`    | tree   | 方塊名稱 | 無      | 樹幹方塊       |
| `leaves` | tree   | 方塊名稱 | 無      | 樹葉方塊       |
| `vines`  | tree   | 布林值   | `false` | 從樹葉垂掛藤蔓 |

### 放置範本

*形狀*

| 鍵           | 適用於        | 值               | 預設  | 作用                                                                                                                                                                                                      |
| ------------ | ------------- | ---------------- | ----- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structure`  | imprint、tree | `namespace:name` | 無    | 要放置的範本                                                                                                                                                                                              |
| `integrity`  | imprint、tree | 1 到 100         | `100` | 範本的方塊實際出現的百分比                                                                                                                                                                                |
| `lootTable`  | imprint、tree | `namespace:path` | 無    | 已放置範本內每個箱子第一次被開啟時所填入戰利品的戰利品表，其他任何接受戰利品表的容器也一樣，包括界伏盒或模組的板條箱。涵蓋 `structure` 和 `structures` 的每個項目；每個箱子擲出自己的種子碼               |
| `structures` | imprint、tree | 清單             | 無    | 可在其中選擇的數個範本，每次放置一個。每個項目是 `{ "structure": "namespace:name", "weight": 3 }`，或是機率均等的單純名稱。會覆寫 `structure`                                                             |
| `turns`      | imprint、tree | 清單             | 任意  | 它可以以哪個方向放置：`none`、`quarter`、`half`、`threequarter`。項目可以帶有 `weight`。省略時，四種的機率相等                                                                                            |
| `mirrors`    | imprint、tree | 清單             | 無    | 同時翻轉：`none`、`leftright`、`frontback`，可選擇性加上 `weight`。要指定自己權重的項目是帶有 `mirror` 和 `weight` 的物件，例如 `{ "mirror": "leftright", "weight": 2 }`，`turns` 項目也一樣，改用 `turn` |
| `at`         | imprint       | 兩個整數，x 和 z | 無    | 在該區塊生成時，在地表的這些方塊座標上剛好放置一次，而不是依機率。見[位於精確位置的結構](#位於精確位置的結構)                                                                                             |
| `locateAs`   | imprint、tree | 字串             | 無    | 將這個項目放置的每個結構登記在該名稱之下，讓 `/locate <name>` 找到最近的一個。見[尋找已放置的結構](#尋找已放置的結構)                                                                                     |

對於沒有內建類型涵蓋的形狀，`imprint` 就是辦法：把它建成 `.nbt` 範本再放置，用 `structures` 加以變化，用 `turns` 和 `mirrors` 加以轉向，並用 `integrity` 把它溶解成比你畫的檔案更粗糙的東西。

### 位於精確位置的結構

*形狀*

原版結構可以用 `terrain` 設定中的 `structureAt` 固定在精確的位置，寫成 `structure=x,z` 項目，一行一個：`"structureAt": ["villages=1000,-500"]`。**x 和 z 是方塊座標，不是區塊座標**，結構會在包含該方塊的區塊中生成；村莊的水井就立在該方塊本身，而其他結構則從遊戲在該區塊中會讓它開始的地方開始。每個想要的實例一個項目。它的間距、分隔、最小出生點距離和平地檢查全都不再適用，所以位置由資源包負責，而相距不到一個區塊的兩個固定點會把兩個結構放進同一個區塊。結構建立之後，會依一般規則貼合到其區塊的地面。

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAt": ["villages=1000,-500", "Fortress=-2000,300"]
  }
}
```

| 設定          | 類型                         | 預設 | 作用                                                                                                                                                        |
| ------------- | ---------------------------- | ---- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureAt` | 清單，內容為 `structure=x,z` | 無   | 將原版結構固定在精確的位置，每個想要的實例一個項目。x 和 z 是方塊座標，結構會在包含該方塊的區塊中生成；它的間距、分隔、最小出生點距離和平地檢查全都不再適用 |

`imprint` 項目在其形狀中以 `"at": [x, z]` 以同樣方式固定，在該區塊生成時，在地表的這些座標上剛好放置一次，而不是依機率。它可以與 `locateAs` 搭配，所以固定的結構也能用 /locate 找到。

### 尋找已放置的結構

*形狀*

帶有 `"locateAs": "Crypt"` 的 `imprint` 項目，會將它放置的每個結構登記在該名稱之下，之後 `/locate Crypt` 就會指向最近的一個，名稱也會出現在 Tab 鍵補全中。只能找到已經生成的結構，因為資源包的結構是在區塊建立時依機率放置，而不是放在遊戲能預測的格網上。這些名稱存放在世界的存檔中，所以重新啟動後依然有效，在伺服器上也能運作。以這種方式登記的名稱，也可以用 `gotoPlaceLevels` 給它自己的權限，讓資源包把誰可以被帶到它自己的結構，與原版結構分開決定。

### 礦場與礦脈的鍵

*形狀*

| 鍵          | 適用於      | 值                            | 預設                    | 作用                                                                                                                                                                |
| ----------- | ----------- | ----------------------------- | ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `field`     | field       | 物件                          | `{ "type": "speckle" }` | 場如何計算。與硬度組的 `field` 鍵相同，詳見[分佈場](#分佈場)：`speckle` 搭配 `chances` 和 `spread`，或 `seeded` 搭配 `cell`、`seeds`、`reach`、`arms` 和 `armReach` |
| `threshold` | field、vein | 0.0 到 1.0                    | `0.5`（vein 為 `0.4`）  | 場在一個方塊上必須多強才會放置。越低，填得越多                                                                                                                      |
| `fade`      | field       | 整數                          | `0`                     | 讓高度帶的頂部逐漸稀疏，而不是平整地結束：在高度範圍頂部的這麼多格內，每個方塊被放置的機率逐步變小，與引擎在 `deepStone` 與上方世界相接處所呈現的外觀相同           |
| `pattern`   | vein        | `default`、`banded` 或 `tube` | `default`               | 礦床的外觀：扭曲的團塊、每隔幾格堆疊的層，或在岩石中蜿蜒的中空管道                                                                                                  |
| `density`   | vein        | 0.0 到 1.0                    | `1.0`                   | 符合條件的方塊中實際被放置的比例，逐方塊擲硬幣                                                                                                                      |
| `rich`      | vein        | 方塊名稱                      | 無                      | 放置在場範圍中高於 `threshold` 的最高五分之一，也就是礦床的核心，取代項目的方塊                                                                                     |
| `poor`      | vein        | 方塊名稱                      | 無                      | 放置在該範圍的最低五分之二，也就是外圍，取代項目的方塊；中間是項目自己的方塊。省略任一層，就在那裡放置項目的方塊                                                    |
| `richAt`    | vein        | 0.0 到 1.0                    | `0.88`                  | 富礦層在該範圍中的起點：`0.88` 讓富礦方塊只限於礦床最強的八分之一，數字越低，富礦核心越肥厚，`1.0` 則完全沒有富礦方塊                                               |
| `poorAt`    | vein        | 0.0 到 1.0                    | `0.4`                   | 項目自己的方塊開始的位置：低於此值就放置 `poor` 方塊，所以 `0.4` 給出最低五分之二的外圍，`0.0` 則沒有貧礦外圍。會限制在 `richAt` 以內                               |

### 帶狀區域

*形狀*

`belt` 是比一個區塊大得多的球體，用於石頭區域而不是礦脈。它的 `radius` 就是球的大小，每個區塊都根據世界種子碼和項目自己的名稱，自行算出附近的球從哪裡開始，所以不論區塊以什麼順序生成，belt 都能完整產生，而且絕不會有任何東西寫進相鄰的區塊。

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

belt 會忽略 `attempts` 和 `spread`，因為它是逐區塊放置而不是逐次嘗試。`minHeight` 和 `maxHeight` 是中心所在的高度帶，球體會延伸到超出該高度帶 `radius` 的地方。`replace` 決定它吃掉什麼，`biomes` 以及溫度和降雨限制是在中心檢查的，所以 belt 要嘛完整出現，要嘛完全不出現，不會在生態域邊緣被切斷。

成本隨 `radius` 的立方成長，而低 `rarity` 會使之倍增，所以請從預設值開始，緩慢提高半徑。

### 礦場

*形狀*

`field` 不在某一點放置任何東西，而是一次放置所有東西。它不是挑選一個位置再圍繞它建造形狀，而是對區塊內、在 `minHeight` 和 `maxHeight` 之間的每個方塊提出一個問題，並在答案至少達到 `threshold` 的地方放置。這個問題與硬度組所問的相同，所以兩者描述的是同樣的礦脈，資源包可以做出彼此一致的組與項目。

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

| 鍵          | 必填 | 值         | 預設  | 作用                                                          |
| ----------- | ---- | ---------- | ----- | ------------------------------------------------------------- |
| `threshold` | 否   | 0.0 到 1.0 | `0.5` | 場必須多強才會放置方塊                                        |
| `field`     | 是   | 物件       | 無    | 硬度組接受的同一個物件，具有相同的 `speckle` 和 `seeded` 類型 |

低 `threshold` 會取走大部分的場並給出寬闊的礦層，高的則只取每一團的中央，給出小而分散的礦囊。使用 `speckle` 會得到許多細小的斑點，使用 `seeded` 會得到較圓的斑塊，一旦有了分支，則是之間伸出捲鬚的結。

與 belt 一樣，field 會忽略 `attempts` 和 `spread`，因為它是逐區塊詢問而不是逐次嘗試，而且絕不會寫進相鄰的區塊。它由世界種子碼和項目自己的名稱算出，所以相同的種子碼永遠給出相同的礦脈，名稱不同的兩個項目也絕不會對齊。`replace`、`adjacent`、`biomes` 和氣候限制都照常適用。

`field` 礦脈是唯一一種由你描述而不是挑選的形狀。它執行與硬度組相同的格點，所以帶有幾條分支的 `seeded` 會給出朝鄰居伸出捲鬚的結，那是礦脈而不是團塊，而 `threshold` 決定其中多少夠堅實而可以放置：

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

這些鍵放在它們自己的 `field` 物件中，而不是與 `type` 並列，因為形狀上的 `type` 已經寫明是 `field`。

## 擴散

*生成世界*

帶有 `type` 的 `spread` 區塊。

此處一次列出所有鍵。實際的檔案只寫需要的鍵。標示給某個類型的鍵只由該類型讀取。

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

| 類型        | 它把東西放在哪裡                     |
| ----------- | ------------------------------------ |
| `even`      | 高度之間的任何地方，均勻分布。預設值 |
| `centered`  | 偏向某一個高度，離得越遠越稀疏       |
| `sprawl`    | 橫跨一段高度範圍的碎形礦脈           |
| `terrain`   | 跟隨地表                             |
| `cavern`    | 在洞穴地面或頂部                     |
| `submerged` | 在水或其他流體之下                   |

| 鍵                  | 適用於   | 值           | 預設           | 作用                                                                                                                                                                                          |
| ------------------- | -------- | ------------ | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`              | 全部     | 上述擴散之一 | `even`         | 哪一種擴散                                                                                                                                                                                    |
| `center`            | centered | 整數         | 高度範圍的中點 | 它聚集在哪個高度周圍                                                                                                                                                                          |
| `range`             | centered | 整數         | 高度範圍的一半 | 它離該高度多遠                                                                                                                                                                                |
| `smoothness`        | centered | 1 到 8       | `2`            | 把多少次 `range` 的擲骰相加。高度帶從 `center` 向外延伸約 `range` 乘以此數的一半，所以每多一次都會讓它變寬，而且帶內有更多礦脈聚集在 `center` 附近。預設值 `2` 時，高度帶向兩側各延伸 `range` |
| `veinHeight`        | sprawl   | 整數         | 高度範圍       | 一條礦脈有多高                                                                                                                                                                                |
| `veinDiameter`      | sprawl   | 整數         | `12`           | 一條礦脈有多寬                                                                                                                                                                                |
| `verticalDensity`   | sprawl   | 1 到 100     | `16`           | 垂直方向有多實心                                                                                                                                                                              |
| `horizontalDensity` | sprawl   | 1 到 100     | `32`           | 水平方向有多實心                                                                                                                                                                              |
| `offsetMin`         | terrain  | 整數         | `0`            | 距離地表的最低偏移                                                                                                                                                                            |
| `offsetMax`         | terrain  | 整數         | `offsetMin`    | 距離地表的最高偏移                                                                                                                                                                            |
| `ceiling`           | cavern   | 布林值       | `false`        | 附著在洞穴頂部，而不是地面                                                                                                                                                                    |

## 結構地圖

*生成世界*

結構地圖將範本組合成格網上一座具名的建築，遠遠超過單一 `.nbt` 檔案 32 格的限制。每一層都畫成一列列的單一字元，一個字元對應一個儲存格，並疊在前一層之上一個儲存格高。最多 8 層、每層 8×8 個儲存格，在預設的 32 格儲存格下，每邊是 256 格，也就是原版的建造高度。

`<namespace>/structuremaps/*.json`

```json
{
  "name": "Castle",
  "cell": 32,
  "ground": 0,
  "spacing": 64,
  "chance": 25,
  "at": [128, -64],
  "dimensions": [0],
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

地圖還可以沉入地下，並填充其範本中的生成器和箱子：

```json
{
  "cell": 24,
  "depth": [8, 20],
  "spacing": 48,
  "chance": 30,
  "spawners": ["minecraft:zombie=3", "minecraft:skeleton=2", "mypack:angry_cow"],
  "loot": "minecraft:chests/simple_dungeon",
  "layers": [
    {
      "palette": { "a": "mypack:dungeon_rooms" },
      "map": ["aa",
              "aa"]
    }
  ]
}
```

| 設定         | 類型             | 預設  | 作用                                                                                                                                                                                                                                  |
| ------------ | ---------------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`       | 文字             | 檔名  | 地圖在日誌中的稱呼                                                                                                                                                                                                                    |
| `cell`       | 數字             | `32`  | 格網間距，單位為格，最大 48。小於儲存格的範本放在儲存格的角落，所以全尺寸的部件可以無縫對接                                                                                                                                           |
| `ground`     | 數字             | `0`   | 哪一層以地形表面為地板。在它之前的層向下挖，建築的地下室就是這樣來的                                                                                                                                                                  |
| `at`         | 兩個數字         | 無    | 在精確的方塊座標固定一份複本，做法與 `structureAt` 固定村莊相同                                                                                                                                                                       |
| `spacing`    | 數字             | `0`   | 以相隔這麼多個區塊的格網散布複本，並依世界種子碼抖動。`0` 不散布任何複本，所以只有 `at` 的地圖剛好建造一次                                                                                                                            |
| `chance`     | 數字             | `100` | 會建造複本的格網位置所占的百分比                                                                                                                                                                                                      |
| `depth`      | 數字，或兩個數字 | 無    | 讓建築沉入地形之下：`ground` 層的地板位於取樣地表以下這麼多格，而不是與地表齊平。兩個數字分別是最小值和最大值，每個副本依世界種子各自抽取。適用於地圖散布或以 `at` 固定的副本；由村莊或城市放置的地圖仍把其 `ground` 層的地板放在地表 |
| `spawners`   | 實體 id 清單     | 無    | 地圖範本中每個儲存時沒有生物、或只有豬的生成器所生成的生物。項目可以帶權重，如 `id=2`；儲存了豬以外其他生物的生成器會保留它。沒有任何內容註冊的 id（開啟 `vanillaClients` 時資源包的實體變體）不參與抽取                              |
| `loot`       | 戰利品表 id      | 無    | 地圖範本中每個儲存時既沒有戰利品表也沒有物品的箱子和陷阱箱所使用的戰利品表；儲存時帶有其中任一項的箱子維持不變                                                                                                                        |
| `dimensions` | 清單             | 全部  | 地圖可以建造的維度 id                                                                                                                                                                                                                 |
| `layers`     | 清單             | 無    | 各層，由下往上，每一層包含一個 `palette` 和一個 `map`                                                                                                                                                                                 |

調色盤以資源包 `<namespace>/structures/` 中的註冊鍵來指名範本。

| 值                                          | 作用                                                                                                                    |
| ------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `"a": "mypack:keep"`                        | 該層的每個 `a` 儲存格都放置這個範本                                                                                     |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | 每個 `a` 儲存格依權重從清單中擲出，根據世界種子碼和儲存格的位置，所以建築的兩份複本會不同，但同一個世界永遠建造出同一座 |
| `.`                                         | 空的儲存格，不放置任何東西                                                                                              |

每份複本會依世界種子碼擲出四個朝向之一，整座建築連同範本一起轉動，所以跨儲存格相接的牆依然相接。地面層以建築中央下方取樣的地形表面為地板。每個區塊只建造格網中屬於它的那一片，所以橫跨許多區塊的建築不會連鎖生成，不論區塊以什麼順序載入。類型為 `template` 的[村莊地塊](#村莊地塊)也可以將地圖指定為它的 `structure`，使複合建築成為村莊建築。

地圖會切入地形：範本包含的每個方塊（包括空氣）都會取代原位置上的方塊，而空的儲存格或較小範本周圍的空間則維持實心，因此要把每個房間的空氣寫進範本，並讓豎井的儲存格一直延伸到地表。

## 村莊地塊

*生成世界*

`<namespace>/villages/*.json`

檔案路徑即地塊的名稱，`villagePieces` 隨後可以指名它來保留或捨棄它。

檔案在此新增一個村莊可以建造的部件，與原版的並存。有兩種，以 `type` 選擇。

此處一次列出所有鍵。實際的檔案只寫需要的鍵。標示給某個類型的鍵只由該類型讀取。

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
  "apron": 2,
  "requires": ["mypack"]
}
```

### 每個地塊

*村莊地塊*

| 鍵           | 適用於 | 值                             | 預設             | 作用                                                                                                                                                                                                                                         |
| ------------ | ------ | ------------------------------ | ---------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`       | 全部   | `farm` 或 `template`           | `farm`           | 哪一種地塊                                                                                                                                                                                                                                   |
| `weight`     | 全部   | 整數                           | `3`              | 此地塊相對於資源包其他地塊的被挑中頻率                                                                                                                                                                                                       |
| `leastCount` | 全部   | 整數                           | `1`              | 每個村莊最少幾個，在加上村莊大小之前                                                                                                                                                                                                         |
| `mostCount`  | 全部   | 整數                           | `4`              | 每個村莊最多幾個，在加上村莊大小之前                                                                                                                                                                                                         |
| `width`      | 全部   | 整數                           | `7`              | 沿路徑方向的大小                                                                                                                                                                                                                             |
| `height`     | 全部   | 整數                           | `4`              | 地面之上清出的高度                                                                                                                                                                                                                           |
| `depth`      | 全部   | 整數                           | `9`              | 遠離路徑方向的大小                                                                                                                                                                                                                           |
| `apron`      | 全部   | 整數                           | `2`              | 地塊下方的地面可以與道路高度相差多遠，超過就會被拒絕或沿著道路滑動：其下填土、或切入其上隆起處的方塊數就是那麼多，而且最高與最低的角之間不超過那麼多。丘陵中的寬地塊需要更多。設得很高，地塊就會直接在斜坡上階梯化，在不對的地方會吃掉一座山 |
| `ground`     | 全部   | 方塊名稱                       | `minecraft:dirt` | 在斜坡上墊在下面的東西                                                                                                                                                                                                                       |
| `requires`   | 全部   | 模組 id 或資源包命名空間的清單 | 無               | 除非全部存在，否則地塊會被捨棄                                                                                                                                                                                                               |

每個資源包地塊都以一個項目提供給村莊，所以村莊要求地塊時，`weight` 決定選中你的哪一個地塊。一次放置所使用的是哪個地塊，會寫入村莊自己的資料，所以載入時能正確重建。

### 農場

*村莊地塊*

`farm` 是原版的田地，改以描述而非程式碼來定義：一塊你指定大小的地塊，以一種方塊圍邊，內部是一列列被水渠隔開的土壤，並依你的清單逐方塊挑選作物種下。

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

| 鍵         | 適用於 | 值           | 預設                 | 作用                               |
| ---------- | ------ | ------------ | -------------------- | ---------------------------------- |
| `crops`    | farm   | 方塊名稱清單 | 小麥                 | 每個方塊種一株，處於隨機的生長階段 |
| `edge`     | farm   | 方塊名稱     | `minecraft:log`      | 地塊周圍的框                       |
| `soil`     | farm   | 方塊名稱     | `minecraft:farmland` | 各列由什麼構成                     |
| `water`    | farm   | 布林值       | `true`               | 在各列之間放一條水渠               |
| `rowWidth` | farm   | 整數         | `2`                  | 每列土壤有多寬                     |

### 由範本建構

*村莊地塊*

`template` 改為放置你的某個 `.nbt` 結構，並使其轉向面對村莊道路。

```json
{
  "type": "template",
  "weight": 2,
  "width": 9,
  "height": 6,
  "depth": 9,
  "structure": "mypack:blacksmith_shed",
  "lootTable": "mypack:chests/smithy",
  "orderSign": "Mine",
  "orderX": 3,
  "orderY": 1,
  "orderZ": 2
}
```

如果 `template` 的 `structure` 指向你的某張[結構地圖](#結構地圖)，就會把整個組合體當作該地塊放置。此時地塊的大小由地圖決定，也就是其佔地範圍與堆疊的層數乘以單元格，因此不會讀取 `width`、`height`、`depth` 和 `integrity`。地圖 `ground` 之前的層會向下挖成地下室，帶權重的調色盤單元格仍會依每棟建築各自抽選，所以同一張地圖蓋出的兩座塔可以不同。

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| 鍵               | 適用類型 | 值               | 預設  | 作用                                                                                       |
| ---------------- | -------- | ---------------- | ----- | ------------------------------------------------------------------------------------------ |
| `structure`      | template | `namespace:name` | 無    | 要放置的範本，或你的某張結構地圖，此時由它決定地塊的大小                                   |
| `integrity`      | template | 1 至 100         | `100` | 範本中出現的方塊所佔的百分比                                                               |
| `lootTable`      | template | `namespace:path` | 無    | 放置後的範本內每個箱子在第一次被打開時，都會依此戰利品表填充。指定了結構地圖的地塊不受影響 |
| `villagers`      | 全部     | 整數             | `0`   | 該地塊生成多少人                                                                           |
| `villagerEntity` | 全部     | `namespace:name` | 村民  | 誰住在那裡，例如你自己的實體變體                                                           |
| `villagerX`      | 全部     | 整數             | `1`   | 他們出現的位置，沿地塊寬度方向                                                             |
| `villagerY`      | 全部     | 整數             | `1`   | 他們出現的位置，高於地板多少                                                               |
| `villagerZ`      | 全部     | 整數             | `1`   | 他們出現的位置，進入地塊的深度                                                             |
| `orderSign`      | template | 字串             | 無    | 訂單的 `sign` 字詞，不分大小寫。地塊立起該告示牌，訂單在旁邊的箱子處開啟，僅限空閒工人     |
| `orderX`         | template | 整數             | `1`   | 告示牌所在位置，沿地塊寬度方向                                                             |
| `orderY`         | template | 整數             | `1`   | 告示牌所在位置，高於地板多少                                                               |
| `orderZ`         | template | 整數             | `1`   | 告示牌所在位置，進入地塊的深度                                                             |

## 城市配置地圖

*生成世界*

城市地圖在網格上繪製村莊的街道平面圖，一個字元對應一個單元格，村莊會依這張圖來佈置，而不是自行生長。街道、廣場和地塊所生成的部件與自然生長的村莊相同，因此所有道路選項、橋梁、碼頭、斷頭路、燈柱、末端迴轉圈和廣場中央裝飾都照樣適用。世界範本在 `villageLayout` 中指定地圖。

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

| 設定       | 類型 | 預設     | 作用                                                                                                                                                   |
| ---------- | ---- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`     | 文字 | 檔案名稱 | 該地圖在日誌中的名稱                                                                                                                                   |
| `cell`     | 數字 | `48`     | 網格間距，單位為格，8 至 128。道路依資源包的道路寬度沿單元格中線延伸，地塊置中放在各自的單元格裡，所以一個單元格要容納最寬的地塊，再加上臨街所需的空間 |
| `palette`  | 物件 | 無       | 每個字元鋪設什麼，列於下表                                                                                                                             |
| `map`      | 清單 | 無       | 各列，最多 64 乘 64 個單元格。比最寬的一列短的列，在其末端之後視為開闊                                                                                 |
| `settings` | 物件 | 無       | 僅用於這張地圖的村莊設定，使用世界範本所用的名稱，例如 `villagePathCenterBlock`。它們優先於範本的設定，而生態域自己的村莊設定又優先於它們              |

| 值                                                                      | 作用                                                                                                                                                                                                                                                                                                                                                        |
| ----------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `"#": "street"`                                                         | 沿某一列或某一行連續排列的街道單元格會成為一個道路方塊區，寬度為資源包的道路寬度。列方向與行方向的連續段相交處，路口與其他位置一樣照常繪製。在兩個方向上都沒有連續段的孤立街道單元格，會沿列方向鋪成一小段短道                                                                                                                                              |
| `"+": "plaza"`                                                          | 一口水井及其廣場環。連續段會穿過廣場單元格，所以街道在水井處交會，而位於十字路口的廣場會像圓環一樣，把它的水井或其 `villageWellStructure` 中央裝飾立在路口正中。檔案中的第一個廣場就是村莊自己的水井，它把地圖固定在村莊建立的位置；沒有廣場的地圖則以該位置為中心                                                                                          |
| `"a": "alley"`                                                          | 一條窄道。建築面向它，但它不連通任何地方，仍依一般的小巷規則處理                                                                                                                                                                                                                                                                                            |
| `"J": "junction"`                                                       | 向兩個方向都鋪設的街道單元格，因此即使圖中只有一個方向穿過它，那裡也會有一個路口。穿過它的那條支路長一個單元格                                                                                                                                                                                                                                              |
| `"b": "bulb"`                                                           | 以末端迴轉圈作結的街道單元格。地圖一旦有 bulb 單元格，就只有位於 bulb 單元格內的道路末端才會得到迴轉圈，且每個有空間的末端都會有；沒有 bulb 單元格的地圖則維持四個末端中有三個                                                                                                                                                                              |
| `"E": { "kind": "elevated", "height": 8 }`                              | 抬升到橋面上的街道單元格，橋面比其所在相連高架單元格範圍下方的最高地面高出 `height` 格，取值 2 至 64，兩端各有一段每列升一格的坡道。該範圍內相交的街道隨之抬升。如果某段高架的橋面或坡道會觸及被鐵路或水井以自身高度佔用的列，則它維持在地面高度，並在日誌中留下一行記錄。任何值都可以這樣寫成物件，由 `kind` 指明類別                                      |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | 以自己的道路鍵鋪設並鋪面的街道，這些鍵優先於地圖和範本的設定。它的寬度取決於它自己的 `villagePathExtraWidth`、`villagePathSidewalkWidth` 和標線，路面、標線和人行道則取決於它自己的方塊鍵，所以大道或小巷都可以畫上自己專屬的標記。一段連續的街道採用其第一個設有任何鍵的單元格的鍵。無論多寬或多窄，畫出來的街道始終是街道：它絕不會被當成小巷或末端迴轉圈 |
| `"T": "mypack:tower"`                                                   | 一個地塊單元格，依該地塊定義鋪設，置中放在單元格內，並面向最近的街道                                                                                                                                                                                                                                                                                        |
| `"T": ["mypack:a=3", "mypack:b=1"]`                                     | 同上，但依權重根據世界種子碼和該單元格的位置抽選，所以同一個世界總會在那裡鋪設同樣的地塊                                                                                                                                                                                                                                                                    |
| `"g": "grow"`                                                           | 交給自然生長。設有 `villagePlotsLeast` 時，生長出的街區和街道填補會填滿這類單元格並從地圖向外擴展；沒有設定時，該單元格維持開闊                                                                                                                                                                                                                             |
| `.`                                                                     | 開闊的地面，不鋪設任何內容                                                                                                                                                                                                                                                                                                                                  |

每張地圖都會根據世界種子碼抽選四個朝向之一並整體旋轉，所以平面圖從任何一側看都一樣。道路最先鋪設，所以會與道路或其他地塊重疊的地塊會維持開闊並在日誌中留下一行記錄，沒有任何資源包提供的地塊名稱同樣會使其單元格維持開闊。地圖不會改變各部件的裝飾方式：道路鍵、`villageBlocks`、燈和水井替換都與自然生長的村莊一樣讀取。繪製出的地圖不會向外生長：街道旁不會填補小巷，其道路末端會得到迴轉圈（照常是四個中有三個，或依其 bulb 單元格所指定的），但沿途不會有房屋。

## 連綿城市

*生成世界*

連綿城市沒有邊界。`villageCitySpacing` 設為 `1` 時，世界中的每個街區都是一座獨立的城鎮：一個中心有水井的廣場，以及從中延伸出去、與周圍街區的街道相接的街道。玩家走到哪裡，城市就生成到哪裡，城鎮之間沒有空曠的郊野。

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

| 設定                  | 類型                 | 預設    | 作用                                                                                                                                          |
| --------------------- | -------------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `terrainAdaptation`   | 布林值               | `false` | 鋪設 RDPL 自己的城市街道。連綿城市需要開啟它                                                                                                  |
| `villageCitySpacing`  | 整數，0 至 256       | `0`     | `1` 讓每個街區都成為一座城鎮，城市正是因此而連綿不斷。在 1.12.2 上只有 `1` 會帶來變化：`0` 和其他任何數字都照舊依 `structureSpacing` 排定村莊 |
| `villageBlockSizes`   | `size=weight` 的清單 | 空      | 平行街道之間的街區有多深，每個街區抽選一次，因此城市會混合細密與疏闊的街網                                                                    |
| `villagePlotsMost`    | 整數，0 或更大       | `0`     | 一個街區最多安置多少個地塊。0 表示不設上限                                                                                                    |
| `villagePlotsBackRow` | 布林值               | `true`  | 在每個正對街道的地塊後方再安置一個地塊，使每個街區的內部也被蓋滿                                                                              |

街區是世界上的一個正方形，寬度為最大地塊的兩倍，再加上一個廣場和每側一條街道，向上取整到 16 格，且不小於 96 格。其他所有街道設定，從路面方塊到路燈、橋梁、隧道和下水道，裝點連綿城市的方式與其他城市相同。`villagePlotsLeast` 不起作用，因為每個街區本身已是一座完整的城鎮。規劃中地塊和水井合計不超過兩個的街區會留空，與任何這麼小的城市一樣。

**街道如何相接。** 每個街區都把水井建在街區內的同一個區塊中，並把它的主十字路口，也就是在水井處交叉的兩條街道，一路鋪到街區的邊緣。因此相鄰街區的十字街道會連成筆直的大道，貫穿整個世界，並在兩個街區之間的接縫處相接，雙方都無需參照對方。街區所鋪設的任何東西都不會越過它的邊緣，所以無論哪個鄰居先生成，街區生成的結果都相同，它的水井也不會移動。

**代價。** 連綿城市的每個區塊都是建成區，因此新地形在任何地方的開銷都與大城市中心相當：放置建築並為其計算光照。快速飛行的玩家會超過生成速度，在新街區生成期間刻速率會下降。用 `pregenOnNewWorld` 或 `/rdplserver pregen` 預先生成玩家起始的區域，並維持適中的視距，可以控制住這一點，但在玩家探索新地形期間，TPS 仍會低於一般世界。存檔會隨探索過的地形增長，與任何建成區一樣。遊戲只在記憶體中保留距玩家或出生點 96 個區塊以內的村莊，其餘的連同存檔的村莊紀錄一起收起，當附近的地形再次生成時，再把它原樣讀回。

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

| 設定                  | 類型   | 預設    | 作用                                                                                                                                                                       |
| --------------------- | ------ | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `retrogen`            | 布林值 | `false` | 為每個標有 `"retrogen": true` 的世界生成條目，補上在該條目出現之前就已儲存的區塊。關閉時，已存在的區塊不受影響                                                             |
| `adoptExistingChunks` | 布林值 | `false` | 第一次見到舊區塊時會發生什麼：開啟時，它被標記為彷彿該資源包已經生成過它，永遠不會補生成；關閉時，它與其他區塊一樣被補生成。要填補既有的世界，請開啟 `retrogen` 並關閉此項 |

帶有 `"retrogen": true` 的條目會生成到你加入它之前就已儲存的區塊中。每個區塊都會記錄它已經歷過什麼，所以不會重複做任何事。

條目上的旗標只是把條目標為符合條件。補生成是否啟用由 `retrogen` 設定決定，資源包可以在其 `settings` 區段中設定它，玩家也可以在設定中調整，預設為關閉。與之搭配的 `adoptExistingChunks` 決定第一次見到舊區塊時的處理：開啟時，該區塊被標記為彷彿該資源包已經生成過它，永遠不會補生成；關閉時，它與其他區塊一樣被補生成。在 `adoptExistingChunks` 同時開啟的情況下開啟 `retrogen` 不會有任何作用，因為每個舊區塊在排入佇列之前就已被排除。要填補既有的世界，請同時開啟 `retrogen` 並關閉 `adoptExistingChunks`。

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

在設定中更改 `retrogenKey` 會使每個區塊再次符合條件，這會在舊礦脈之上再加入新礦脈，使密度加倍。這是刻意的設計，也是該鍵需要手動更改的原因。

每刻補生成多少個已儲存的區塊，由設定 `worldgen` 類別中的 `retrogenChunksPerTick` 決定，預設為 `2`。數值越大完成得越快，但卡頓也越明顯。

## 預先生成

*生成世界*

提前生成世界的陸地，這樣遊玩時就沒有人需要生成區塊：沒有區塊延遲，磁碟佔用大小可預知，只需在一開始等待一次，而不是在頭一個小時裡不斷卡頓。

出生點周圍的 12 個區塊一定會被接管，無論資源包或設定怎麼寫，因為在任何人加入之前遊戲自己就恰好會生成這麼多。如果不管它，這片地面會在未打光的狀態下出現，並在玩家走過時一個區塊一個區塊地被修整；接管之後，它會一次完成，玩家落腳的就是已經處理好的地面。`pregenOnNewWorld` 設定要再向外延伸多遠，而指令則手動執行一次。

`/rdplserver pregen <radius>` 會生成執行該指令的位置周圍這麼多個區塊範圍內的所有區塊。`status` 會顯示進度，`stop` 會結束它，`<radius> relight` 則只對已經存在的陸地執行光照處理，修整執行時無法觸及的接縫，而從未生成的部分則不予理會。

執行期間所有人都會被控制住：變為旁觀者、固定在原地、畫面中央顯示一行脈動的文字，周圍的世界暫停。每位玩家進入時的遊戲模式會在被控制時寫入玩家資料，所以執行中途儲存的存檔、當機或重新加入，都不會讓任何人一直停留在旁觀者模式；執行結束時會準確恢復所取走的模式，或在設定了 `worldGameMode` 時恢復為資源包指定的模式。進度每完成十分之一就會播報，每次執行結束時會對自己的那片方形區域重新打光，全部完成後玩家被釋放並收到問候。每個維度生成到了哪裡會儲存在世界中，所以已完成的世界不會再次執行，除非某個維度的陸地所在的任何檔案從磁碟上遺失，這會被察覺並使該維度重新來過。

在資源包中，這些鍵放在[世界範本](#世界範本)的 `settings` 區段中，與其他每個 `chunks` 鍵一樣。下面列出其中的每一項，只有 `pregenBorderLimit` 不在其中，因為它只存在於設定中：

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

### 生成的內容

*預先生成*

`config/mct_resourcedatapackloader_mixin.cfg`

```ini
chunks {
    I:pregenBorderLimit=8192
}
```

| 鍵                            | 作用                                                                                                                                                                                                                                          | 設定它的原因                                                     |
| ----------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------- |
| `pregenOnNewWorld`            | 在任何人遊玩之前，圍繞出生點生成的區塊半徑。12 是下限，0 表示取這個下限而不是什麼都不做，因為遊戲自己本來就會在出生點周圍生成 12 個區塊：執行時會接管這片地面並一次為其打光，而不是任由它跟在玩家身後慢慢出現。調高它可以比遊戲本身延伸得更遠 | 設定資源包在遊戲本來就會生成的地面之外再延伸多遠                 |
| `pregenDimensions`            | 要生成哪些維度，依序進行，每個維度圍繞它自己的出生點                                                                                                                                                                                          | 加入地獄、終界，或你自己的維度                                   |
| `pregenAllDimensions`         | 生成所有已註冊的維度而不是一份清單，主世界優先                                                                                                                                                                                                | 適用於維度很多的資源包。每個模組的維度都會算在內，所以要留意大小 |
| `pregenDimensionsWhenEntered` | 這些維度在有人第一次踏入時才生成，並再次控制住所有人直到完成                                                                                                                                                                                  | 大多數玩家從不前往的維度；從不去的玩家不用付出任何代價           |
| `pregenToBorder`              | 把每個維度一路填滿到其世界邊界，而不是依半徑                                                                                                                                                                                                  | 有邊界的世界                                                     |
| `pregenBorderLimit`           | 邊界最多可以延伸多遠，超過則拒絕執行。僅限設定，不是資源包的鍵                                                                                                                                                                                | 防止失控執行的保護措施；只有在清楚它所需的時間和磁碟空間時才調高 |

### 一次執行的行為

*預先生成*

| 鍵                     | 作用                                                                                                                                                                                   | 設定它的原因                                                     |
| ---------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------- |
| `pregenResume`         | 被停止或中斷的執行會從中斷處繼續。執行開始時，其維度、中心和半徑會寫入存檔，因此當機、斷電或中途退出，在下次載入時都會在中斷位置後約十秒內恢復。以指令或看門狗刻意停止的執行會維持停止 | 伺服器上的長時間執行；小型執行即使不用它，重新開始的代價也很低   |
| `pregenKeepLoaded`     | 在執行後方保持載入的區塊數，使得一個區塊在被修整和打光時，它的相鄰區塊都已就緒                                                                                                         | 如果重新打光回報有很多區塊被留到之後處理，就調高它；會佔用記憶體 |
| `pregenPauseAbove`     | 當等待寫入的區塊達到這麼多時，執行會暫時休息                                                                                                                                           | 磁碟較慢時調低它                                                 |
| `pregenMillisPerRound` | 每刻最多可以花多長時間生成陸地                                                                                                                                                         | 在空的世界中調高，在有人遊玩的伺服器上調低                       |

預先生成有自己的快速光照路徑，當安裝了 Alfheim 或 Phosphor 之類的光照引擎時，它會退讓，改由該引擎來完成工作。無論哪種方式，最終得到的都是已完成且光照完整的陸地。

發布前請自己執行一遍，使用準備發布的半徑，從頭到尾。區塊數隨半徑的平方增長，兩個方向各 63 就是一萬六千個區塊，500 則超過一百萬，每個約十 KB，所以測試世界的 region 資料夾和實際耗時，才是應當擺在玩家面前的真實數字。不要發布從未執行過的半徑。

### 向玩家顯示什麼

*預先生成*

| 鍵                                                                                  | 作用                                                                                                                                                                                                                                                                                                                                | 設定它的原因                                                                                     |
| ----------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| `pregenRunningSays`、`pregenRelightSays`、`pregenFinishedSays`、`pregenStoppedSays` | 各階段的聊天訊息。前兩個可以包含用於百分比的 `%d`，其後可接用於維度名稱的 `%s`，或用 `%1$d` 和 `%2$s` 以任意順序放置它們，並且一律以該階段的 ` - ETA 00:00:00` 結尾，這不是一項設定。完成和停止的訊息在所有要求的內容都做完時說一次，以整個過程的 ` - Total time 00:00:00` 結尾，這同樣不是一項設定                                 | 用你的資源包的口吻改寫它們，在生成多個維度時寫上維度名稱，或讓它們靜音                           |
| `pregenSpectatingSays`                                                              | 生成陸地期間畫面中央的控制提示行。保持預設時，它會使用每位玩家的語言；留空則不顯示                                                                                                                                                                                                                                                  | 請保持在大約三十五個字元以內，否則小視窗會將其截斷                                               |
| `pregenLogo`                                                                        | 預先生成結束時標誌所在的位置：`left`、`center` 或 `right`，位於畫面中央文字上方，顯示幾秒鐘，然後隨著霧一起淡出                                                                                                                                                                                                                     | 它一律會顯示；無法辨識的詞會當作 `center` 處理                                                   |
| `welcomeSays`                                                                       | 綠色的問候語，在每次登入時以及預先生成之後顯示。單獨的條目是適用於所有地方的那一行；`dimension=message` 條目會為該維度覆寫它，並且也會問候每位抵達該維度的玩家，例如 `"-1=Welcome to the Nether!"`。`=` 之後留空的訊息會使該維度靜音；空清單則什麼也不顯示。保持預設時，它會使用每位玩家的語言                                      | 單獨的一行寫上你的資源包名稱；再加入維度行，為每個世界設定主題。每行請保持在大約三十五個字元以內 |
| `saysCard`                                                                          | 把本模組所說的內容，也就是歡迎語、預先生成進度和威脅提示行，顯示為右下角的一張卡片，而不是聊天訊息。卡片滑入，停留八秒後淡出，在開啟的畫面之上也會顯示                                                                                                                                                                              | 當聊天很繁忙，或希望這些內容讀起來像是世界的一部分而不是閒聊時，請開啟它                         |
| `saysIcon`                                                                          | 繪製在卡片上的一個物品，例如 `minecraft:compass`。留空則不繪製                                                                                                                                                                                                                                                                      | 為卡片配上你的資源包的徽記                                                                       |
| `saysColor`                                                                         | 卡片的背景顏色，十六進位，例如 `1E2630`。留空則使用深石板色                                                                                                                                                                                                                                                                         | 與你的資源包的配色相搭配                                                                         |
| `saysImage`                                                                         | 來自資源包用戶端資源的一張 PNG，例如 `rubyworld:textures/gui/card.png`，拉伸後鋪滿卡片作為其背景，繪製在顏色之上。留空則不繪製                                                                                                                                                                                                      | 為卡片配上一塊繪製的面板；請讓圖像寬而矮，它會被拉伸到文字所需的任何大小                         |
| `saysBackground`                                                                    | 繪製卡片的面板、邊框和顏色條，以及玩家被控制期間畫面中央歡迎語和提示後面的深色背景。關閉後只剩下文字（保留其陰影），如果設定了 `saysImage` 則還有它                                                                                                                                                                                 | 讓文字漂浮在世界之上，或讓繪製的 `saysImage` 獨立呈現                                            |
| `saysFont`                                                                          | 繪製卡片文字所用的字型，以 `namespace:name` 命名，例如 `rubyworld:runes`。留空則使用 RDPL 字型 `resourcedatapackloader:rdpl`。它所指的檔案見「卡片」一節                                                                                                                                                                            | 為卡片配上你的資源包專屬的字型                                                                   |
| `toasts`                                                                            | 遊戲的哪些彈出通知（右上角的彈出框）會顯示。`true` 全部顯示，`false` 全部不顯示；清單則只顯示其中列出的種類：`advancements`，`recipes` 表示已解鎖的配方，`tutorial` 表示操作教學提示，`system` 表示遊戲自身的通知，`other` 表示其餘各項未涵蓋的所有彈出通知，例如其他模組的。預設不顯示任何彈出通知。玩家的用戶端會在加入時採用該值 | 當你的資源包以進度引導玩家，而其餘彈出通知只會礙事時，保留 `["advancements"]`                    |

### 備份與地圖重置

*預先生成*

| 鍵                      | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                       | 設定它的原因                  |
| ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------- |
| `pregenBackup`          | 預先生成完成後，在玩家仍被控制期間，將世界複製為一份原始備份。這樣生成只需付出一次代價：之後的重置，或使用同一資源包和種子碼的新世界，會還原這份副本而不是再次生成，這比預先生成兩次快得多。該副本保存在存檔之外，位於存檔旁邊的 `rdpl-pristine/<world>`，因此其他模組的備份不會把它一併清掉，它也不會出現在它們管理的資料夾中。如果某份副本所對應的資源包與目前載入的不再相符，它會被丟棄，並根據手上的世界重新保存，所以更換資源包絕不會重置成別人的地圖 | `false`                       |
| `pregenBackupSays`      | 製作該副本期間向玩家顯示的畫面中央提示行，後面帶有百分比。留空則不顯示，副本會在靜默中製作                                                                                                                                                                                                                                                                                                                                                                 | `Pack requested world backup` |
| `resetSays`             | `/rdplserver reset` 或一回合結束把地圖恢復原樣期間向玩家顯示的畫面中央提示行。留空則靜默重置                                                                                                                                                                                                                                                                                                                                                               | `Pack requested map reset`    |
| `resetSendsTo`          | 重置後把玩家送到哪裡：`spawn`、形如 `x,y,z` 的位置，或用 `dimension:x,y,z` 把他們送進另一個世界，重置時把所有人放到大廳而不是回到競技場，就是這樣做到的                                                                                                                                                                                                                                                                                                    | `spawn`                       |
| `resetRuns`             | 在重置清空地圖之後執行的一個函式，以 `namespace:path` 命名。它負責重新建造競技場，因為以函式製作地圖的資源包可以直接再執行一次。留空則不執行任何內容                                                                                                                                                                                                                                                                                                       | 空                            |
| `resetClearsEntities`   | 移除所有非玩家實體。生物、掉落物和經驗球全部消失，這樣地圖就回到了開始時的樣子                                                                                                                                                                                                                                                                                                                                                                             | `true`                        |
| `resetClearsScores`     | 把資源包保存的每個目標重置為零，使新一場比賽從零開始。隊伍本身會保留                                                                                                                                                                                                                                                                                                                                                                                       | `true`                        |
| `resetClearsInventory`  | 清空每位玩家的物品欄，包括盔甲和副手，使一回合從地圖發放的物品開始，而不是上一回合留下的。隨後會立刻再次發放陣營的 `gives`                                                                                                                                                                                                                                                                                                                                 | `false`                       |
| `resetClearsExperience` | 把每位玩家的經驗重置為 0 級                                                                                                                                                                                                                                                                                                                                                                                                                                | `false`                       |

---

# 遊戲模式

## 世界介紹

*遊戲模式*

`<namespace>/worldintro/*.json`

檔案名稱可以自己決定，只會讀取資料夾。資源包提供的每個介紹都會執行，依資源包順序。

在玩家進入世界、取得控制權之前，顯示一系列頁面。可以是圖片上捲動的文字、標題卡、幻燈片，或依序出現的三者。

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

| 鍵         | 必填 | 值                           | 預設    | 作用                                                 |
| ---------- | ---- | ---------------------------- | ------- | ---------------------------------------------------- |
| `pages`    | 是   | 頁面清單                     | 無      | 依序顯示。沒有頁面的檔案會被拒絕並回報錯誤           |
| `once`     | 否   | 布林值                       | `false` | 每位玩家在每個世界中只播放一次，而不是每次加入都播放 |
| `music`    | 否   | 音效事件名稱                 | 無      | 整個流程使用的一首曲目，隨第一頁開始播放             |
| `requires` | 否   | 模組 id 或資源包命名空間清單 | 無      | 除非全部存在，否則跳過該介紹                         |

### 頁面

*世界介紹*

`pages` 中的每個條目：

| 鍵            | 必填 | 值                   | 預設           | 作用                                                                                                                            |
| ------------- | ---- | -------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `mode`        | 否   | `scroll` 或 `static` | `scroll`       | 文字捲動，或文字靜止不動直到玩家繼續                                                                                            |
| `text`        | 否   | `.txt` 檔案的路徑    | 無             | 文字內容。只有圖片的頁面則省略                                                                                                  |
| `background`  | 否   | 材質路徑             | 平鋪的泥土背景 | 一個背景                                                                                                                        |
| `backgrounds` | 否   | 材質路徑清單         | 無             | 多個，循環播放。如果兩者都給，則疊加到 `background` 上                                                                          |
| `interval`    | 否   | 秒                   | `5.0`          | 當背景多於一個時，每個背景停留多久                                                                                              |
| `time`        | 否   | 秒                   | 依文字計算     | 捲動頁面從頭到尾需要多久。對於靜止頁面，或任何類型的最後一頁，它表示多久之後頁面自動翻過，如果沒有它則會等待按鈕                |
| `direction`   | 否   | `up` 或 `down`       | `up`           | 捲動文字的移動方向                                                                                                              |
| `textScale`   | 否   | 數字                 | `1.0`          | 乘以字型大小。`static` 頁面會把文字換行到螢幕寬度減去兩側邊距，當文字仍會壓到按鈕下方時，會縮小繪製，最多縮小到一半，直到放得下 |
| `settle`      | 否   | 布林值               | `false`        | 以最後一行置中作為結束，而不是一直捲出螢幕                                                                                      |

### 文字與時間

*世界介紹*

文字檔放在 `<namespace>/texts/*.txt`。純文字，每個段落佔一行，空行會保留為空行。`.md` 檔案的讀取方式相同，兩種檔案都支援下文的格式。`PLAYERNAME` 會被替換為玩家的名稱，與原版終界詩篇的替換方式相同。

`time` 設定一個頁面持續多久，因此無論頁面上只有一行還是二十行，同一頁佔用的時間都相同。請透過頁面上放多少內容來調節閱讀速度。省略 `time` 時，頁面的速度與原版工作人員名單相同，文字越多，耗時自然越長。

### 文字格式

*世界介紹*

介紹文字支援 Markdown。不含任何標記的檔案，顯示效果與純文字完全一致。

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

| 標記     | 寫法                                            | 顯示效果                                                                                                                                                                                         |
| -------- | ----------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 標題     | 行首的 `# `、`## `、`### `                      | 加粗並放大：分別為文字大小的兩倍、一點五倍和一點二五倍，對齊方式與內文相同                                                                                                                       |
| 粗體     | `**text**`                                      | 字型的粗體字形                                                                                                                                                                                   |
| 斜體     | `*text*`                                        | 字型的斜體字形                                                                                                                                                                                   |
| 粗斜體   | `***text***`                                    | 傾斜的粗體字形                                                                                                                                                                                   |
| 刪除線   | `~~text~~`                                      | 帶刪除線                                                                                                                                                                                         |
| 程式碼   | `` `text` ``                                    | 染成水藍色                                                                                                                                                                                       |
| 連結     | `[text](url)`                                   | 只顯示文字，帶底線；不可點擊                                                                                                                                                                     |
| 符文     | `{runic}text{/runic}`                           | 文字以符文密碼 `resourcedatapackloader:rdpl_runic` 顯示，該行其餘部分維持原字型；其中的粗體和斜體採用該密碼字型的粗體和斜體字形。標題、清單項目和引用中均可使用，未閉合的 `{runic}` 會照原樣顯示 |
| 項目符號 | 行首的 `- ` 或 `* `                             | 顯示為圓點，換行後的文字縮排對齊到文字下方；標記前加兩個空格即巢狀一層                                                                                                                           |
| 編號     | 行首的 `1. `                                    | 依所寫的數字顯示，縮排方式相同                                                                                                                                                                   |
| 引用     | 行首的 `> `                                     | 縮排並變暗                                                                                                                                                                                       |
| 分隔線   | 單獨一行的 `---`                                | 橫跨文字寬度的水平線                                                                                                                                                                             |
| 圖片     | 單獨一行的 `![alt](namespace:textures/....png)` | 顯示圖片，縮小到文字寬度並保持比例；若無法讀取則顯示替代文字                                                                                                                                     |
| 跳脫     | 標記前加 `\`，例如 `\*`                         | 該標記以一般字元顯示                                                                                                                                                                             |

表格和圍欄程式碼區塊（位於 ``` 行之間）會以純文字繪製，標記照原樣保留。捲動頁面的時間推算與靜止頁面的縮放適配，都以排版後的高度為準，圖片也計算在內。卡片的標題與各行、Says 訊息以及歡迎與保持提示，支援從粗體到符文的行內標記，每則一行。

### 玩法

*世界介紹*

捲動頁面在時間到後切換到下一頁。最後一頁不會自動前進，而是停下等待。底部有 **Next Page** 和 **Skip All**，最後一頁則只有一個 **Continue to World**。按 Esc 與 Skip All 的效果相同。靜態頁面的每一行都置中。捲動頁面則像工作人員名單一樣維持固定的欄寬。

在單人遊戲中，世界會在介紹背後暫停，因此玩家閱讀時不會有東西悄悄逼近。唯一的例外是介紹開啟時仍在生成的陸地：此時生成會在頁面背後繼續進行，玩家維持旁觀者狀態，直到繼續進入世界，即使生成已先行完成也是如此。在伺服器上，世界持續運行，而原版用戶端根本不會看到介紹，照常加入。歡迎問候會等到頁面關閉後才出現，因此不會被頁面遮住而錯過。

`once` 記錄在玩家的存檔資料中，死亡後依然有效。`/rdplserver intro` 會為執行者清除該記錄，因此他們下次加入時介紹會再次播放。它不會當場重播，以免成為在遊戲進行中回到入場流程的途徑。

背景會拉伸以填滿視窗，因此 16:9 的圖片適合 16:9 的視窗，而正方形圖片看起來會被壓扁。請依所需形狀裁切圖片，而不要依賴自動適配。`music` 接受任何已註冊的音效事件，無論是原版的，還是你自己的資源包透過 `sounds` 加入的。它不會循環，所以較短的曲目播完後就是一片寂靜。

如果有多個資源包都提供了介紹，它們的頁面會依資源包順序首尾相接地播放，而不是只讓其中一個生效。如果只想要一個，請用 `requires` 加以限制。

## 隊伍

*遊戲模式*

`<namespace>/teams/*.json`

檔案名稱由你自行決定，只會讀取該資料夾，多個檔案會疊加。每個檔案是一個陣營。

一個陣營就是遊戲自帶計分板上的真實隊伍，因此 `/scoreboard teams list` 看得到它，它的成員在存檔和重新載入後依然保留，而沒有安裝本模組的用戶端也會像對待任何原版隊伍一樣顯示顏色和名牌。成員資格依名稱計算，因此任何有名稱或 UUID 的對象都可以加入陣營：玩家、殭屍、村民、盔甲架。

只有資源包要求時才會設立陣營：任何位置都沒有 `teams` 資料夾時，本模組不會加入隊伍，不監聽任何事件，也不提供該指令。編輯了檔案的伺服器管理員可以執行 `/rdpl reload`，在不重新啟動的情況下把改動套用到執行中的世界。

```json
{
  "name": "red",
  "displayName": "Red Team",
  "color": "red",
  "friendlyFire": false,
  "joinable": false,
  "prefix": "[R] ",
  "suffix": " *",
  "scoreboard": true,
  "mobFriendlyFire": false,
  "seeFriendlyInvisibles": true,
  "nameTags": "hideForOtherTeams",
  "deathMessages": "always",
  "collision": "pushOtherTeams",
  "spawnBox": [0, 60, 0, 16, 80, 16],
  "balance": true,
  "spawn": "23,64,10",
  "lead": "first",
  "leadOn": "kaboom",
  "leadIs": "Steve",
  "leadSays": "You lead the red side",
  "entities": ["mypack:zombie_a", "mypack:sapper_a"],
  "picks": 0,
  "picksFrom": ["players"],
  "gives": ["minecraft:iron_pickaxe", { "item": "minecraft:bread", "count": 8 }],
  "standIn": { "entity": "mypack:herobrine", "at": "23,31,0" },
  "leadRuns": "mypack:lead_chosen",
  "down": { "seconds": 30, "reviveSeconds": 5 }
}
```

### 陣營

*隊伍*

| 設定          | 類型   | 預設     | 作用                                                                                                                                                                       |
| ------------- | ------ | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | 文字   | 檔案名稱 | 隊伍在計分板上的名稱，1 到 16 個字元。`/scoreboard` 和其他檔案使用的就是它                                                                                                 |
| `displayName` | 文字   | 名稱     | 向玩家顯示的內容，用以取代名稱                                                                                                                                             |
| `color`       | 文字   | `white`  | 十六種文字顏色之一。它為名牌著色，也是每隊側邊欄欄位所依據的鍵                                                                                                             |
| `prefix`      | 文字   | 空       | 放在成員名稱前面，位於顏色之後                                                                                                                                             |
| `suffix`      | 文字   | 空       | 放在成員名稱後面                                                                                                                                                           |
| `scoreboard`  | 布林值 | `true`   | 該陣營是否作為遊戲計分板上的隊伍存在。關閉則完全不設立隊伍：其生物改為在名稱中帶上陣營的顏色，沒有任何機制阻止它們互相戰鬥，也不會有積分記到它頭上，因為計分是依隊伍進行的 |

### 戰鬥與可見度

*隊伍*

| 設定                    | 類型   | 預設           | 作用                                                                                                                      |
| ----------------------- | ------ | -------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `friendlyFire`          | 布林值 | `false`        | 成員之間是否可以互相傷害。同時也是 `mobFriendlyFire` 的預設值                                                             |
| `mobFriendlyFire`       | 布林值 | `friendlyFire` | 陣營的生物是否會被己方的爆炸和投擲的 TNT 傷到，單靠遊戲本身永遠不會阻止這種情況。關閉則保護己方；開啟則維持遊戲原有的行為 |
| `seeFriendlyInvisibles` | 布林值 | `true`         | 成員隱形時彼此是否能看見                                                                                                  |
| `nameTags`              | 文字   | `always`       | `always`、`never`、`hideForOtherTeams` 或 `hideForOwnTeam`                                                                |
| `deathMessages`         | 文字   | `always`       | 同樣的四個詞，決定成員死亡時通知哪些人                                                                                    |
| `collision`             | 文字   | `always`       | `always`、`never`、`pushOtherTeams` 或 `pushOwnTeam`                                                                      |

### 誰會加入

*隊伍*

| 設定        | 類型   | 預設    | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| ----------- | ------ | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entities`  | 清單   | 空      | 實體 id，其每一次生成都會加入這個陣營，例如 `minecraft:zombie` 或你自己的實體                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `players`   | 清單   | 空      | 登入時加入這個陣營的玩家名稱                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `spawnBox`  | 清單   | 無      | 六個整數，x y z 到 x y z。在範圍內生成的任何東西都會加入，兩個角的順序可以任意                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `joinable`  | 布林值 | `true`  | 玩家是否可以用 `/rdpl team join` 加入。對只給生物使用的陣營，請設為 false                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `balance`   | 布林值 | `false` | 不指定名稱的 `/rdpl team join` 是否可以把玩家放進這裡。在允許的陣營中，會選擇玩家最少的那個                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `picks`     | 數字   | `0`     | 這個陣營隨機抽取多少名成員。每一回合開始時，陣營會讓上一次抽到的成員回到原本站的位置，並從 `picksFrom` 所指的全部對象中重新抽取；兩次抽取之間，來自該範圍的登入或生成會立刻填補空位。它的用途是：從所有人中抽出一名玩家，自成一個陣營                                                                                                                                                                                                                                                                                             |
| `picksFrom` | 清單   | 空      | 抽取的來源：`players` 表示所有在線玩家，實體 id 表示該種類的每一隻活著的生物                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `standIn`   | 物件   | 無      | 在沒有玩家加入時代守該陣營的生物：`{ "entity": "mypack:herobrine", "at": "23,31,0" }` 會在主世界的那個位置保持一隻該實體存活，缺少時就生成一隻，並在有玩家加入該陣營的瞬間將其移除，因此遊戲會在對抗 AI 的狀態下進行，直到有玩家接手這個角色。每五秒檢查一次；該位置必須在已載入的地面上。在有大廳的遊戲中（`opens.by: leader`），替身只會在大廳等待期間和回合開始時生成，所以倒下的替身會在回合其餘時間及其結束後一直不再出現，直到所有人回到大廳；沒有大廳時，在以 `ends.lastStanding` 結束的回合進行期間，倒下的替身不會被補上 |

加入的方式有三種，一個陣營可以三種並用。`entities` 列出實體 id，該種類的任何東西在生成時就會加入，資源包就是以此讓生物擁有陣營，而無須改動生物本身。`spawnBox` 圈出世界的一個角落，在其中生成的任何東西都會加入，適合雙方使用同一種生物的競技場。`players` 則直接指名玩家。除此之外，玩家可以用 `/rdpl team join <name>` 加入，除非該陣營把 `joinable` 設為 false，並可以用 `/rdpl team leave` 離開。

### 初始裝備與出生點

*隊伍*

| 設定    | 類型 | 預設 | 作用                                                                                                                                                                                                                                           |
| ------- | ---- | ---- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `gives` | 清單 | 空   | 玩家加入該陣營時放進其物品欄的物品，每一項是物品名稱，或帶有 `item`、`count` 和 `unbreakable` 的物件，最後一項用來製作永不磨損的物品。它們會放進任何空格，沒有空格時就掉在玩家腳邊。在清空物品欄的重置（`resetClearsInventory`）之後會再次發放 |
| `spawn` | 文字 | 無   | 主世界中的 `x,y,z`，回合開始時該陣營的玩家會被放到這裡，讓各陣營從自己的地面出發；沒有它時，他們會留在重置或大廳讓他們所在的位置                                                                                                               |

### 主導者

*隊伍*

| 設定       | 類型 | 預設                               | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ---------- | ---- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `lead`     | 文字 | `none`                             | 陣營的主導者如何產生：`none`；`first` 表示在線成員中最早加入該陣營的人，所以當有人離開時會依加入順序往下傳，等人回來又會回到他身上，他們抵達時（越過介紹與任何控制之後）以及輪到他們時都會收到通知；`topScore` 表示 `leadOn` 所指目標上分數最高的人；`appointed` 表示 `leadIs` 所指的玩家；`vote` 表示成員投票選出的人；`claim` 表示最先認領的人。主導者只是一個標籤和一種顏色，別無其他：它不賦予任何權力，所以主導者登出不會破壞任何東西 |
| `leadOn`   | 文字 | 空                                 | 搭配 `topScore`，成員據以排名的目標。每次讀取時都會重新計算，因此會隨分數變動                                                                                                                                                                                                                                                                                                                                                              |
| `leadIs`   | 文字 | 空                                 | 搭配 `appointed`，擔任主導者的玩家                                                                                                                                                                                                                                                                                                                                                                                                         |
| `leadSays` | 文字 | `You are the current round leader` | 主導權交到玩家手上時告知他：在他們抵達自己所主導的陣營時、在他們認領時，或在 `first` 主導者傳給他們時（此時訊息會帶有離開的人是誰）。`{side}` 是陣營的顯示名稱；留空則不告知                                                                                                                                                                                                                                                               |
| `leadRuns` | 文字 | 空                                 | 一個函式，`namespace:path`，每次主導權交給某位玩家時執行一次：第一位主導者，以及之後的每一次交接。它以主導者的身分、在其位置執行，權限等同進度獎勵的函式，所以 `@s` 就是主導者。每秒檢查一次；離線的主導者會在下次上線時補執行。重新啟動會重新決定主導者                                                                                                                                                                                   |

### 倒地

*隊伍*

| 設定            | 類型 | 預設                                                                            | 作用                                                                                                                                                                                       |
| --------------- | ---- | ------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `seconds`       | 數字 | `0`                                                                             | 該陣營的玩家倒地後多少秒會失血而死。`0` 或沒有 `down` 物件則關閉此功能：陣營的玩家照常死亡，也不監聽任何事件。失血而死是真正的死亡，因此得分、淘汰和戰利品都在那時結算，而不是在玩家倒地時 |
| `reviveSeconds` | 數字 | `5`                                                                             | 未倒地的隊友需要潛行站在倒地玩家 2 格以內多少秒才能將其救起。一旦停止潛行或走遠，進度會重新開始                                                                                            |
| `says`          | 文字 | `You are down and bleed out in {seconds} seconds unless a teammate revives you` | 玩家倒地時對其說的話。`{seconds}` 是距離失血而死的時間。留空則不說任何話                                                                                                                   |
| `revivedSays`   | 文字 | `You were revived`                                                              | 隊友將玩家救起時對其說的話。留空則不說任何話                                                                                                                                               |

帶有 `down` 的陣營成員不會死亡，而是倒地：生命值維持在 1，既不能移動也不能跳躍（會被從坐騎上拉下，一旦移動就被放回倒地的位置，緩速效果拉滿作為提示），會發光，除虛空、`/kill` 和失血而死外不受任何傷害，也不能攻擊、使用物品或點擊方塊與實體。虛空和 `/kill` 會直接殺死未倒地的玩家，失血而死則算作把他擊倒的敵人的擊殺。被救起的玩家以最大生命值的一半回來。沒有陣營的玩家照常死亡，生物不受影響。該狀態只保存在記憶體中：倒地時登出的玩家會死亡，伺服器在有人倒地時停止，則此人會以 1 點生命值且沒有效果的狀態回來。這些訊息以 `rdpl:downed` 和 `rdpl:revived` 卡片的形式顯示

回合結束或重置時，所有倒地的人會立即解除：效果移除，生命值恢復滿。

## 計分

*遊戲模式*

`<namespace>/scoring/*.json`

檔案名稱由你自行決定，只會讀取該資料夾，多個檔案會疊加。每個檔案是一個目標。

一個目標就是遊戲自帶計分板上的真實目標，因此 `/scoreboard players list` 讀得到它，它的分數也會隨存檔保留。`criterion` 是遊戲自己計算的依據：`dummy` 用於只有這個資源包會改動的分數，或是 `deathCount`、`playerKillCount`、`totalKillCount`、`health`，或遊戲認得的任何 `stat.` 或 `achievement.` 名稱。

```json
{
  "name": "kaboom",
  "displayName": "Kills",
  "criterion": "dummy",
  "render": "integer",
  "individuals": true,
  "carries": false,
  "awardsTo": "wins",
  "display": "sidebar",
  "teamTotals": true,
  "tiebreak": true,
  "points": {
    "kill": { "mypack:zombie_a": 1, "mypack:zombie_b": 1 },
    "death": -1,
    "revive": 2,
    "raid": true,
    "order": true,
    "ownKill": -1
  },
  "opens": {
    "by": "leader",
    "lobby": "0,64,0",
    "says": "Waiting for {leader} to start the round",
    "leaderSays": "Type /rdpl round start",
    "lobbyJoins": true,
    "joinsSays": "Round is in progress, you can join after it ends"
  },
  "ends": {
    "afterMinutes": 10,
    "atScore": 50,
    "afterRounds": 5,
    "lastStanding": true,
    "outSays": "You are out until the round ends",
    "locksTeams": true,
    "resets": true,
    "intermissionSeconds": 10,
    "intermissionSays": "Round cooldown {seconds}",
    "startsSays": "Round starting in {seconds}"
  },
  "results": {
    "card": true,
    "title": "Final standings",
    "icon": "minecraft:tnt",
    "seconds": 15,
    "image": "mypack:textures/gui/result.png",
    "background": "1E2630"
  }
}
```

### 目標

*計分*

| 設定          | 類型   | 預設             | 作用                                                                                                                                                               |
| ------------- | ------ | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`        | 文字   | 檔案名稱         | 目標在計分板上的名稱，1 到 16 個字元                                                                                                                               |
| `displayName` | 文字   | 名稱             | 向玩家顯示的內容，用以取代名稱                                                                                                                                     |
| `criterion`   | 文字   | `dummy`          | 遊戲自己計算的依據。無法辨識的依據會被拒絕，並留下一行說明                                                                                                         |
| `display`     | 文字   | 空               | `sidebar`、`list`、`belowName` 或 `sidebar.team.<color>`。留空則在任何地方都不顯示；沒有可開啟的計分板畫面                                                         |
| `render`      | 文字   | 該依據本身的設定 | `integer` 或 `hearts`                                                                                                                                              |
| `teamTotals`  | 布林值 | `true`           | 積分記在以該成員所屬隊伍命名的一列上                                                                                                                               |
| `individuals` | 布林值 | `false`          | 積分也會記在該成員自己的一列上                                                                                                                                     |
| `carries`     | 布林值 | `false`          | 目標在地圖重置後仍然保留，而不是隨之清除。累計回合勝場的比賽統計就是一例                                                                                           |
| `awardsTo`    | 文字   | 空               | 另一個目標，本目標結束時會給它一分，給領先的陣營。同分的排名不發放任何東西                                                                                         |
| `tiebreak`    | 布林值 | `false`          | 在頂端同分結束的回合，會以世界的隨機數從並列的陣營中抽出一個，記錄這次抽籤，並照常授予。沒有 `awardsTo` 的目標（即比賽）以同樣方式抽籤，並在結果頂端列出抽中的陣營 |

### 積分

*計分*

| 設定             | 類型   | 預設    | 作用                                                                                                                                          |
| ---------------- | ------ | ------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `points.kill`    | 物件   | 空      | 實體 id 對應積分，計給擊殺者的陣營。`minecraft:player` 計算擊殺玩家的分數；`entity@rank`，例如 `minecraft:zombie@brute`，只對該等級的生物計分 |
| `points.death`   | 整數   | `0`     | 成員死亡時的積分，無論死因為何。可以為負                                                                                                      |
| `points.revive`  | 整數   | `0`     | 使倒地隊友復活的成員獲得的分數，記在復活者名下。可以為負                                                                                      |
| `points.raid`    | 布林值 | `false` | 接收每場獲勝[突襲](#突襲)的 `points`：範圍內的每位玩家都會獲得相應積分，記在所屬陣營的一列上，啟用 `individuals` 時也記在本人的一列上         |
| `points.order`   | 布林值 | `false` | 計入每個已完成[工作訂單](#工作訂單)的 `points`：每位獲得獎勵的玩家在其陣營的列上獲得該點數，開啟 `individuals` 時在自己的列上也獲得           |
| `points.ownKill` | 整數   | `0`     | 擊殺者自己陣營的成員被擊殺時的積分，取代 `kill` 的值。0 表示不計分；負數是懲罰                                                                |

`points` 是本模組在遊戲自己計算的內容之上額外加入的部分，會送進同一個目標，所以 `/scoreboard` 仍然讀得到。`kill` 表示每擊殺一個實體 id 值多少分，計給擊殺者的陣營；`death` 表示每當陣營的一名成員死亡值多少分，可以為負。搭配 `teamTotals`，積分會記在以隊伍命名的一列上，這讓側邊欄能顯示四個陣營，而不是每隻生物各佔一列。`individuals` 會另外為每位成員加一列，預設為關閉，因為每個生物 UUID 各佔一列讀起來只是雜訊。

### 回合如何結束

*計分*

| 設定                | 類型   | 預設                               | 作用                                                                                                                                                                                                                                                                                                                                                                         |
| ------------------- | ------ | ---------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `ends.atScore`      | 整數   | `0`                                | 比賽在任何一個陣營達到此分數的瞬間結束。0 表示永遠不因分數而結束                                                                                                                                                                                                                                                                                                             |
| `ends.afterMinutes` | 整數   | `0`                                | 比賽在這麼多分鐘後結束。0 表示永遠不因時間而結束                                                                                                                                                                                                                                                                                                                             |
| `ends.afterRounds`  | 整數   | `0`                                | 對於另一個目標以 `awardsTo` 授予的目標：一旦總共授予了這麼多回合，比賽就結束，無論是誰拿下的。0 表示永遠不因回合數而結束                                                                                                                                                                                                                                                     |
| `ends.lastStanding` | 布林值 | `false`                            | 當只剩一個陣營屹立不倒時，回合結束。參與的陣營是回合開始時上面有玩家或活著的生物的陣營，至少兩個；死亡的玩家出局，以旁觀者身分等到回合結束，而玩家全部出局或離開且生物全部死亡的陣營即為淘汰。留到最後的陣營拿下該回合，`awardsTo` 無論分數為何都會為該陣營記錄。搭配 `resets` 和 `opens.by: leader` 時，遊戲隨後會回到大廳。這類回合進行期間，陣營的 `standIn` 不會再次生成 |
| `ends.outSays`      | 文字   | `You are out until the round ends` | 告知被淘汰的玩家的內容。留空則不說任何話                                                                                                                                                                                                                                                                                                                                     |
| `ends.locksTeams`   | 布林值 | `true`                             | 回合進行中加入陣營要等到回合結束，因此沒有人能在計分的回合中途插進來                                                                                                                                                                                                                                                                                                         |

`ends` 決定比賽的結束，可以是任何一個陣營達到 `atScore` 的瞬間，或是 `afterMinutes` 分鐘過去之後。隨後會顯示排名，由遊戲本身排序：以聊天訊息呈現，若 `results` 要求則以卡片呈現。沒有安裝本模組的玩家會以聊天行收到同樣的排名，因此沒有人會看不到結果。搭配 `resets` 時，這個結束就是一回合的結束：排名會停留 `intermissionSeconds` 秒，同時動作列上倒數冷卻，地圖重置回歡迎狀態，然後在五秒倒數後開始下一回合。`awardsTo` 把該回合授予領先的陣營，記在一個跨重置保留（`carries`）的目標上。被保留的目標可以自行結束：用 `atScore` 打多戰制，用 `afterRounds` 打固定回合數，並在之後的重置時清除其排名，開啟全新的一場比賽。

### 回合 Boss 血條

*計分*

```json
{
  "name": "kaboom",
  "criterion": "dummy",
  "ends": { "afterMinutes": 10 },
  "bossBar": {
    "name": "Kaboom",
    "color": "blue",
    "style": "notched_10",
    "background": { "texture": "mymod:textures/gui/boss_back.png", "width": 182, "height": 19, "x": 0, "y": 0 }
  }
}
```

| 設定                 | 類型 | 預設                   | 作用                                                                                                                                                                        |
| -------------------- | ---- | ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `bossBar`            | 物件 | 無                     | 回合進行期間，向所有玩家顯示 Boss 血條                                                                                                                                      |
| `bossBar.name`       | 文字 | 計分項的 `displayName` | 血條的標題                                                                                                                                                                  |
| `bossBar.color`      | 文字 | `purple`               | `pink`、`blue`、`red`、`green`、`yellow`、`purple` 或 `white`                                                                                                               |
| `bossBar.style`      | 文字 | `progress`             | `progress`、`notched_6`、`notched_10`、`notched_12` 或 `notched_20`                                                                                                         |
| `bossBar.background` | 物件 | 無                     | 繪製在血條及其標題後方的紋理：`texture`（`namespace:路徑.png`，必填），`width` 和 `height`（預設為 182 和血條所在列的高度），`x` 和 `y`（相對標題列左上角的偏移，預設為 0） |

血條在回合開始時出現，回合結束時消失。設有 `ends.afterMinutes` 時，血條隨時間流逝而變短。否則，設有 `ends.atScore` 時，血條隨領先一方接近該分數而變長。兩者都沒有則始終保持滿格。未知的 `color` 或 `style` 會記入日誌，並使用預設值。

### 回合之間

*計分*

| 設定                       | 類型   | 預設                          | 作用                                                                               |
| -------------------------- | ------ | ----------------------------- | ---------------------------------------------------------------------------------- |
| `ends.resets`              | 布林值 | `false`                       | 回合結束時重置地圖，如 `resetSays` 和其他重置設定所述，然後開啟新的一回合          |
| `ends.intermissionSeconds` | 整數   | `10`                          | 排名在結束與重置之間停留多久                                                       |
| `ends.intermissionSays`    | 文字   | `Round cooldown {seconds}`    | 回合結束後的中場休息期間，每秒顯示在動作列上，`{seconds}` 倒數到重置。留空則不顯示 |
| `ends.startsSays`          | 文字   | `Round starting in {seconds}` | 重置後開啟下一回合的五秒倒數期間顯示在動作列上，`{seconds}` 持續倒數。留空則不顯示 |

### 大廳

*計分*

`opens` 物件的設定決定一回合何時開始，以及玩家在哪裡等待。

| 設定               | 類型   | 預設                                               | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| ------------------ | ------ | -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `opens.by`         | 文字   | `auto`                                             | `auto` 在重置五秒後自動開啟下一回合。`leader` 則改為把遊戲停在大廳：重置之後以及世界第一次載入時，不計任何分數、不跑任何計時，陣營可以自由加入與離開，只有在某個陣營的主導者或管理員執行 `/rdpl round start` 時才會開啟回合，而且在有人還在閱讀世界介紹時不行；接著五秒倒數開始，進行抽取，每個陣營被放到自己的 `spawn`。世界等待期間，直到五秒倒數結束，玩家留在原地，不能破壞、放置、使用、攻擊或丟棄任何東西，也不會受到傷害，嘗試時會看到等待提示，其餘所有活著的東西都靜止不動：沒有 AI，沒有移動。指令仍然有效，所以可以加入陣營並開始回合 |
| `opens.says`       | 文字   | `Waiting for {leader} to start the round`          | 像歡迎語一樣在畫面中央閃現，給每位不是主導者的玩家：在他們越過介紹抵達大廳、歡迎語顯示過之後；在一回合結束後大廳再次開啟時；每當它因主導者的來去而改變時；以及在他們嘗試大廳拒絕的事情時。`{leader}` 是各陣營的主導者，沒有人主導時則為 `a leader`。留空則不顯示                                                                                                                                                                                                                                                                                 |
| `opens.leaderSays` | 文字   | `Type /rdpl round start`                           | 在同樣的時機以同樣的方式閃現給主導一個陣營的玩家，取代 `opens.says`。留空則不顯示                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `opens.lobby`      | 文字   | 無                                                 | 主世界中的 `x,y,z`，或另一個世界中的 `dimension:x,y,z`，例如 `-1:0,64,0`，在大廳持續期間所有人等待的地方：每位玩家和每一隻屬於某陣營的活著的生物，都會站在該點周圍的一圈上，各自面向圓心，所以他們站著互相瞪視。每個對象分到的弧長等於它自己的寬度再加兩格，因此互不重疊，人數增加時圓圈會變大；每當有人加入或離開，就會重新排列。高度是他們所站的地板，在上下三格內尋找。玩家和生物可直接進出該世界，不需要建造傳送門。回合開始時，玩家前往其陣營的 `spawn`，仍然站著的生物則被放回它原本所在的世界中的原位                                     |
| `opens.lobbyJoins` | 布林值 | `false`                                            | 在回合中途登入的玩家，改為放進大廳並以旁觀者身分等到回合結束，而不是放在他們登出時的位置。需要 `opens.lobby`                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `opens.joinsSays`  | 文字   | `Round is in progress, you can join after it ends` | 告知他們的內容。留空則不說任何話                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |

### 重置回合

*計分*

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
    "cooldownSeconds": 60,
    "leadSays": "{player} reset the round",
    "voteSays": "{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds",
    "tallySays": "Reset the round? {yes} yes, {no} no, {seconds}",
    "passSays": "The vote passed, so the round is reset",
    "failSays": "The vote failed, so the round goes on"
  }
}
```

| 設定                    | 類型 | 預設                                                                                      | 作用                                                                                                                                                                                                                      |
| ----------------------- | ---- | ----------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `reset.lead`            | 文字 | `none`                                                                                    | 回合進行期間，`/rdpl round reset` 對陣營主導者的作用。`now` 立即結束回合並重置地圖；`vote` 改為發起投票；`none` 則不給主導者專屬的決定權，所以在 `players` 允許的情況下，主導者和其他玩家一樣發起投票。管理員一律立即重置 |
| `reset.players`         | 文字 | `none`                                                                                    | `vote` 讓任何陣營的玩家都能用 `/rdpl round reset` 發起投票。`none` 則把重置交給主導者                                                                                                                                     |
| `reset.teams`           | 清單 | 空                                                                                        | 其玩家可以發起投票的陣營。留空表示每個陣營                                                                                                                                                                                |
| `reset.passPercent`     | 整數 | `51`                                                                                      | 必須投贊成票才能重置回合的投票者比例，1 到 100。`51` 是過半，`100` 是所有人                                                                                                                                               |
| `reset.voteSeconds`     | 整數 | `30`                                                                                      | 一次投票持續多久，至少五秒。結果一旦確定，它就會提前結束                                                                                                                                                                  |
| `reset.cooldownSeconds` | 整數 | `60`                                                                                      | 投票失敗後多久才能再發起另一次。使用 `now` 的主導者不受其限制                                                                                                                                                             |
| `reset.leadSays`        | 文字 | `{player} reset the round`                                                                | 回合被立即重置時告知所有人，`{player}` 是重置的人。留空則不說任何話                                                                                                                                                       |
| `reset.voteSays`        | 文字 | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | 發起投票時告知所有人，`{player}` 是發起的人。留空則不說任何話                                                                                                                                                             |
| `reset.tallySays`       | 文字 | `Reset the round? {yes} yes, {no} no, {seconds}`                                          | 投票期間每秒顯示在動作列上，`{seconds}` 持續倒數。留空則不顯示                                                                                                                                                            |
| `reset.passSays`        | 文字 | `The vote passed, so the round is reset`                                                  | 投票通過時告知所有人。留空則不說任何話                                                                                                                                                                                    |
| `reset.failSays`        | 文字 | `The vote failed, so the round goes on`                                                   | 投票失敗時告知所有人。留空則不說任何話                                                                                                                                                                                    |

重置會把回合就地中斷。排名會顯示在 `The round was reset` 之下，沒有人被授予該回合，中場休息開始倒數，地圖的重置就像回合以 `ends.resets` 結束一樣，在 `opens.by` 為 `leader` 的情況下回到大廳。無論回合本身會不會自行結束，它都有效，但在大廳中、開啟回合的倒數期間，或回合已結束且其重置正在進行時無效；此時仍在進行的投票會被丟棄。

每位在線且屬於某陣營的玩家都能投票，無論在哪個陣營，用 `/rdpl round vote yes` 或 `no`，並可以在投票進行期間更改。發起投票的人已投贊成票，時間到時尚未投票的玩家算作反對。在有陣營的資源包中，不屬於任何陣營的玩家既不能發起也不能投票；在沒有陣營的資源包中，每位在線玩家都可以。第一個其 `reset` 允許任何人重置的計分檔案就是被採用的那一個。

### 結果

*計分*

| 設定                 | 類型   | 預設               | 作用                                         |
| -------------------- | ------ | ------------------ | -------------------------------------------- |
| `results.card`       | 布林值 | `false`            | 把排名顯示為卡片，而不是聊天訊息             |
| `results.title`      | 文字   | 名稱加上 `results` | 卡片的標題                                   |
| `results.icon`       | 文字   | 空                 | 繪製在卡片上的一個物品，例如 `minecraft:tnt` |
| `results.image`      | 文字   | 空                 | 繪製在卡片上以取代物品的一張圖像             |
| `results.background` | 文字   | 深石板色           | 卡片的背景顏色                               |
| `results.seconds`    | 整數   | `8`                | 卡片停留多久，至少一秒                       |

### 輪流行動

*計分*

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
    "mobTypes": ["minecraft:zombie"],
    "mobTags": ["pawn"]
  }
}
```

| 設定                | 類型 | 預設     | 作用                                                                                                                                                                                               |
| ------------------- | ---- | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `turns.order`       | 文字 | `fixed`  | 誰何時行動，在每個循環開始時決定。`fixed` 維持陣營最初出現的順序，`random` 每個循環重新洗牌，`lowestFirst` 從這個目標上分數最低的陣營開始，`lastWinner` 從上一回合獲勝的陣營開始，其餘維持固定順序 |
| `turns.seconds`     | 整數 | `60`     | 一個回合持續多久，至少一秒                                                                                                                                                                         |
| `turns.gapSeconds`  | 整數 | `3`      | 回合之間的暫停，期間所有陣營等待。0 表示直接接續                                                                                                                                                   |
| `turns.held`        | 文字 | `frozen` | 玩家如何等待自己的回合：`frozen` 讓他們站著不動，並阻止他們攻擊、挖掘、建造、使用或丟棄，就像大廳一樣；`spectator` 或 `adventure` 在輪到他們之前讓他們進入該遊戲模式，之後再還給他們原本的模式     |
| `turns.endsAtScore` | 整數 | `0`      | 一旦有陣營在該目標上達到此分數，輪流行動就結束，回合也隨之結束。0 表示永遠不因分數而結束                                                                                                           |
| `turns.cycles`      | 整數 | `0`      | 經過這麼多個循環後，輪流行動結束，回合也隨之結束，一個循環是每個參與的陣營各進行一次。0 表示持續到有其他條件結束回合                                                                               |
| `turns.mobTags`     | 清單 | 空       | 計分板標籤，會把不屬於任何隊伍的生物帶進輪流行動，每個標籤一組                                                                                                                                     |
| `turns.mobTypes`    | 清單 | 空       | 實體 id，會把不屬於任何隊伍的生物帶進輪流行動，每個 id 一組                                                                                                                                        |

`turns` 讓各陣營在回合進行時輪流行動。每個隊伍是一個陣營；沒有隊伍時，每位玩家各自是一個陣營，而屬於某隊伍的生物跟著該隊伍走。輪到一個陣營時，其他所有陣營都在等待：其玩家依 `held` 所述，其生物則像大廳那樣被凍結在原地。沒有人參與的陣營，或玩家全部被淘汰的陣營，會被跳過。

每個回合都會在聊天中宣布，其計時會倒數在動作列上。輪到的陣營在剩餘 10 秒時和 3 秒時，會在聊天中收到警告並伴隨一聲鈴響。`/rdplserver game pass`，或用帶有 `passesTurn` 的物品按右鍵，可以提前結束一個回合：玩家只能讓自己陣營的回合過去，而指令方塊或主控台則會讓當前輪到的任何人過去。

以 `endsAtScore` 或 `cycles` 結束，與其他任何結束方式一樣會結束回合：顯示排名，套用 `awardsTo` 和 `tiebreak`，而 `ends.resets` 會重置地圖。只有第一個帶有 `turns` 的計分檔案會輪流行動。措辭來自 `turn`、`turnclock`、`turnwarn`、`turnout`、`turnpass`、`turngap`、`notturn` 和 `noturns` 這些鍵，骰子檔案的 `says` 可以更改它們。

## 突襲

*遊戲模式*

`<namespace>/raids/*.json`

檔案名稱由你自行決定，只會讀取該資料夾，多個檔案會疊加。每個檔案是一場突襲。

突襲就是遊戲從 1.14 起具備的那種，建立在 1.12.2 已有的村莊之上。當帶有 `omen` 效果的玩家位於村莊內時，突襲就會開始：該效果被移除，並為村莊中心 `reach` 範圍內的每位玩家顯示一條頭目血條。經過 `waveDelay` 刻後，第一波在村莊周圍的一圈上抵達，朝中心走去，沿途攻擊玩家、村民和鐵魔像。突襲者絕不會傷害或鎖定彼此，所以兩者之間的流箭或誤擊不會有任何作用。血條顯示該波剩餘的生命值，並在剩下兩個或更少時改為計算突襲者人數。一波消滅後，下一波會等待 `waveDelay` 刻。最後一波消滅且兩秒內沒有任何東西回來時，突襲即告勝利；在一波抵達之後，若所有村民死亡或村莊本身消失，則告失敗。無論哪種結果，血條都會顯示三十秒，並以範圍內的每位玩家的身分執行對應的函式。

進行中的突襲會隨世界儲存，突襲者在重新載入後會繼續行進。它會在和平模式下、經過 `timeout` 刻後，或村莊周圍沒有任何位置能容納一波時，不經結局而停止。村莊只有在有村民找到過它的門之後才算數，所以突襲需要遊戲已經注意到的村莊。[環境](#環境)還可以按時鐘發起突襲，並改變突襲的波數和積分。

一波突襲在村莊中時，村民會跑進室內最近的、村莊已知的門並待在那裡。突襲者會破壞擋路的木門以接近他們，每扇門十二秒，在普通和困難難度且 `mobGriefing` 開啟時如此；鐵門擋得住。類型為 `bell` 的方塊無論在哪裡都是村莊的鐘，並依[鐘](#鐘)所述鳴響；突襲的 `bell` 可指定任何其他方塊，使其像鐘一樣鳴響。一波抵達時，村莊中的每個鐘都會鳴響，被指定的方塊在玩家使用時也會鳴響：48 格內的村民躲藏十五秒，48 格內的突襲者發光三秒。沒有任何東西會生成鐘。想要鐘的資源包要自行定義該方塊，放進 NBT 結構，並把該結構放置在村莊中，作為地塊或水井替換，讓鐘立在村民居住的地方。

```json
{
  "omen": "mypack:bad_omen",
  "bossBar": { "name": "Raid", "color": "red", "style": "notched_10" },
  "waveDelay": 300,
  "spawnDistance": 32,
  "sound": "mypack:raid_horn",
  "wins": "mypack:raid_won",
  "loses": "mypack:raid_lost",
  "points": 10,
  "loot": "mypack:raid_reward",
  "anywhere": true,
  "reach": 96,
  "timeout": 48000,
  "bell": "mypack:village_bell",
  "waves": [
    [ { "entity": "minecraft:vindication_illager", "count": 2 }, { "entity": "mypack:raider", "count": { "min": 1, "max": 3 } } ],
    [ { "entity": "minecraft:evocation_illager" }, { "entity": "minecraft:vindication_illager", "count": 4 } ]
  ]
}
```

### 突襲事件

*突襲*

| 設定            | 類型           | 預設     | 作用                                                                                                                                                                                                                                            |
| --------------- | -------------- | -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `omen`          | 效果名稱       | 無，必填 | 其持有者位於村莊內時會觸發突襲的效果。任何已註冊的效果都可以，包括資源包自己的藥水                                                                                                                                                              |
| `name`          | 文字           | `Raid`   | 頭目血條的標題                                                                                                                                                                                                                                  |
| `color`         | 文字           | `red`    | 血條的顏色：`pink`、`blue`、`red`、`green`、`yellow`、`purple` 或 `white`                                                                                                                                                                       |
| `bossBar`       | 物件           | 無       | 頭目血條：`name`、`color`、`style` 和 `background`，與生物的 `bossBar` 相同。省略 `name` 或 `color` 時沿用上方的 `name` 和 `color`，省略 `style` 則為 `notched_10`                                                                              |
| `waves`         | 波次清單       | 無，必填 | 每一波是一份群組清單，各波依序出現                                                                                                                                                                                                              |
| `anywhere`      | 布林值         | `false`  | 為 `true` 時，不祥之兆也會在不靠近任何村莊的玩家身上觸發此突襲，並以該玩家為中心。這樣的突襲以及用 `/rdplserver raid` 開始的突襲不需要村莊：無法失敗，只會以勝利或 `timeout` 結束，並敲響中心 32 格內的每一口鐘。積分與戰利品和其他突襲一樣發放 |
| `waveDelay`     | 整數           | `300`    | 第一波之前，以及一波結束到下一波之間的刻數                                                                                                                                                                                                      |
| `spawnDistance` | 整數           | `32`     | 一波從距離村莊中心多遠的地方抵達。最先嘗試兩倍於此的距離，然後是此距離，再來是村莊內部                                                                                                                                                          |
| `reach`         | 整數           | `96`     | 中心周圍這麼多格內的玩家會看到血條，結尾函式也以他們的身分執行。偏離超出此範圍十六格的突襲者會離開突襲                                                                                                                                          |
| `timeout`       | 整數           | `48000`  | 未完成的突襲在這麼多刻後不經結局而停止。`0` 表示永不停止                                                                                                                                                                                        |
| `sound`         | 音效名稱       | 無       | 每一波抵達時，從該波來襲的方向播放給範圍內的每位玩家                                                                                                                                                                                            |
| `wins`          | 函式           | 無       | 突襲獲勝時，以範圍內每位玩家的身分執行                                                                                                                                                                                                          |
| `loses`         | 函式           | 無       | 突襲失敗時，以範圍內每位玩家的身分執行                                                                                                                                                                                                          |
| `points`        | 整數           | `0`      | 突襲獲勝時記給範圍內每位玩家的積分，計入每個開啟了 `points.raid` 的目標。失敗不計分                                                                                                                                                             |
| `loot`          | 戰利品表       | 無       | 突襲獲勝時，為範圍內的每位玩家各擲出一次的戰利品表。物品會放入其背包，背包已滿則掉在腳邊                                                                                                                                                        |
| `bell`          | 方塊名稱或清單 | 無       | 像鐘一樣鳴響的其他方塊，在玩家使用它們時以及每一波抵達時。類型為 `bell` 的方塊不必指名也會鳴響。請透過 NBT 結構把它放進村莊，因為沒有任何東西會生成它                                                                                           |

### 一個群組

*突襲*

| 設定     | 類型                      | 預設     | 作用                                                       |
| -------- | ------------------------- | -------- | ---------------------------------------------------------- |
| `entity` | 實體名稱                  | 無，必填 | 來襲的是什麼。實體變體保留其全部自身行為，並獲得行進的行為 |
| `count`  | 整數或 `{ "min", "max" }` | `1`      | 來多少                                                     |

---

## 卡片

*遊戲模式*

`<namespace>/cards/*.json`

檔案名稱由你自行決定，只會讀取該資料夾，多個檔案會疊加。每個檔案是一條規則，其 id 為 `<namespace>:<file name>`。規則等待觸發，檢查其 `when`，然後向其對象顯示一張卡片；它也可以執行一個函式。用戶端不需要任何東西：沒有安裝本模組的玩家，角落卡片會以聊天行收到，中央卡片則以標題顯示。

本模組自己說的每一則訊息都是一條內建規則，列於下方。資源包可以寫一個以該 id 命名的檔案，`rdpl/cards/<name>.json`，來更改其中一條，它不需要觸發：沒寫的部分維持現狀，而 `{text}` 代表本模組原本會說的訊息。沒有寫任何這類檔案的資源包，所有訊息都與以前一樣。

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
  "when": {
    "timeFrom": 0,
    "timeTo": 12000,
    "dayAtLeast": 3,
    "advancement": "minecraft:story/mine_stone",
    "gameMode": "survival",
    "team": "miners",
    "objective": "kills",
    "scoreAtLeast": 5
  },
  "repeat": "once_per_player",
  "audience": "dimension",
  "runs": "mypack:desert_entered"
}
```

```json
{
  "lines": ["{text}", "Speak to the gatekeeper for more."],
  "icon": "minecraft:ender_eye",
  "cooldown": 30
}
```

第二個檔案儲存為 `rdpl/cards/gate_blocked.json`，把關閉的閘門所顯示的紅色動作列文字，變成帶有圖示和第二行的卡片，並且最多每三十秒顯示一次。

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

第三個在玩家第一次加入時，以沒有背後面板的中央卡片問候玩家，只有文字及其陰影，以資源包自己的字型繪製。

### 觸發器

*卡片*

| 觸發器               | 需要                 | 觸發時機                                                                                                                                                                                |
| -------------------- | -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `command`            | 無                   | 執行 `/rdplserver card <rule> [players]`。該指令會略過 `when`、`repeat` 和 `cooldown`，但仍會執行 `runs`。任何規則都可以這樣顯示，無論其觸發器為何                                      |
| `first_join`         | 無                   | 玩家第一次加入世界                                                                                                                                                                      |
| `dimension_enter`    | `dimension`          | 玩家抵達該維度                                                                                                                                                                          |
| `biome_enter`        | `biomes`             | 玩家從別處走進其中一個生態域                                                                                                                                                            |
| `structure_enter`    | `structures`         | 玩家從外面走進其中一個結構                                                                                                                                                              |
| `advancement`        | `advancement`        | 玩家獲得該進度                                                                                                                                                                          |
| `time_of_day`        | `time`               | 當玩家在該維度中時，日夜時鐘經過該刻，範圍 `0` 到 `23999`，與 `time.windows` 使用相同刻度，並依該維度的 `dayLength` 換算。設有 `fixedTime` 的維度永遠不會觸發。以指令或床設定的時鐘不算 |
| `day`                | 無，或 `day`         | 該維度中新的一天開始；有 `day` 時，只在那一天                                                                                                                                           |
| `craft`              | `item`               | 玩家合成該物品                                                                                                                                                                          |
| `pickup`             | `item`               | 玩家撿起該物品                                                                                                                                                                          |
| `kill`               | `entity`             | 玩家擊殺該實體，或其中第 `count` 個                                                                                                                                                     |
| `raid_won`           | 無，或 `raid`        | 突襲獲勝時，範圍內的每位玩家各觸發一次。帶 `raid` 時，只有該突襲會觸發                                                                                                                  |
| `raid_lost`          | 無，或 `raid`        | 突襲失敗時，範圍內的每位玩家各觸發一次。帶 `raid` 時，只有該突襲會觸發                                                                                                                  |
| `respawn`            | 無                   | 玩家死亡後重生                                                                                                                                                                          |
| `death`              | 無                   | 玩家死亡                                                                                                                                                                                |
| `y_level`            | `below` 或 `above`   | 玩家的高度低於或高於該值                                                                                                                                                                |
| `play_time`          | `minutes`            | 玩家在世界中的時間達到這麼多分鐘，從世界介紹關閉時算起，若沒有向其顯示介紹則從加入時算起                                                                                                |
| `roll`               | 無，或 `roll`        | 玩家用 `/rdplserver game` 或帶 `rolls` 的物品擲骰時。填了 `roll` 就只有那一種擲骰會觸發，`atLeast` 和 `atMost` 限定總和                                                                 |
| `order_done`         | 無，或 `order`       | 工作訂單完成，對每位獲得獎勵的玩家各觸發一次。帶有 `order` 時，只有 `sign` 為該詞的訂單才會觸發                                                                                         |
| `downed`             | 無                   | 擁有 `down` 的一方的成員倒地而不是死亡時                                                                                                                                                |
| `revived`            | 無                   | 倒地的玩家被隊友救起時。卡片發給被救起的玩家                                                                                                                                            |
| `score`              | `objective`          | 玩家在該目標上的分數達到 `score`                                                                                                                                                        |
| `portway_activate`   | 無                   | 玩家對傳送通道按右鍵將其啟動。`portway` 或 `network` 可將其限定為某一個                                                                                                                 |
| `portway_travel`     | 無                   | 一次傳送通過了全部檢查，即將發生，此時玩家尚未移動                                                                                                                                      |
| `portway_arrive`     | 無                   | 玩家透過傳送通道抵達                                                                                                                                                                    |
| `portway_refused`    | 無                   | 傳送通道或傳送物品把玩家拒之門外。`reason` 說明原因                                                                                                                                     |
| `portway_paid`       | 無                   | 一次傳送扣除了不為零的費用                                                                                                                                                              |
| `portway_forget`     | 無                   | 站點離開玩家的清單，原因是玩家忘掉了它、指令移除了它，或傳送通道已不存在                                                                                                                |
| `portway_bind`       | 無                   | 玩家把傳送物品綁定到一個傳送通道                                                                                                                                                        |
| `exposure_level`     | 無，或 `exposure`    | 玩家在某種危險中的等級發生變化，無論升高還是降低。帶有 `exposure` 時，只對該危險生效；帶有 `level` 時，只有達到該等級的變化才會觸發。加入後的第一次檢查絕不會觸發                       |
| `round_start`        | 無                   | 倒數結束後回合開啟：對每位玩家各觸發一次，或對 `audience` 觸發一次。重設之後和 `/rdplserver round start` 之後都會觸發                                                                   |
| `environment_change` | 無，或 `environment` | 維度中的某個環境因指令或季節而啟用，或 `default` 恢復時：對其中每位玩家各觸發一次，或對 `audience` 觸發一次。帶有 `environment` 時，只對該環境生效。載入已儲存的世界絕不會觸發          |

危險等級在每次掃描時讀取，因此卡片會在變化後該危險的 `scanInterval` 之內觸發。

生態域、結構、高度、遊玩時間和分數每秒對每位玩家檢查一次，並在從外到內的變化時觸發，絕不會在加入後的第一次檢查時觸發。對象不是 `player` 的 `time_of_day` 或 `day` 規則，會為該維度觸發一次，而不是對其中每位玩家各觸發一次。

玩家仍開著世界介紹時觸發的卡片會等待，並在介紹關閉時顯示，無論其觸發器為何，包括 `command` 在內。如果玩家在那之前離開，它就會被丟棄。

### 觸發器設定

*卡片*

`<namespace>/cards/*.json`

```json
{
  "trigger": "score",
  "dimension": "the_nether",
  "structures": ["Fortress"],
  "radius": 48,
  "advancement": "minecraft:story/mine_stone",
  "item": "minecraft:diamond_sword",
  "entity": "minecraft:zombie",
  "count": 5,
  "rank": "brute",
  "raid": "mypack:siege",
  "below": 20,
  "above": 80,
  "time": 6000,
  "day": 3,
  "minutes": 30,
  "roll": "2d6",
  "order": "Mine",
  "exposure": "radiation",
  "level": 2,
  "atLeast": 10,
  "atMost": 12,
  "objective": "kills",
  "score": 5,
  "requires": ["mypack"]
}
```

| 設定                 | 類型                         | 預設     | 作用                                                                                                                                                                                |
| -------------------- | ---------------------------- | -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `trigger`            | 文字                         | 無，必填 | 上述觸發器之一。內建規則不需要                                                                                                                                                      |
| `dimension`          | 文字                         | 無       | 維度 id，例如 `-1`，或其名稱，例如 `the_nether`。對 `dimension_enter` 而言是進入的那個維度；對其他所有觸發器而言，則把規則限制在該維度中的玩家                                      |
| `biomes`             | 清單                         | 無       | 生態域名稱，例如 `minecraft:desert`，或 `#TYPE` 表示 Forge 生態域類型，例如 `#SNOWY`                                                                                                |
| `structures`         | 清單                         | 無       | `Village`、`Temple`、`Mansion`、`Monument`、`Mineshaft`、`Stronghold`、`Fortress` 或 `EndCity`，或資源包透過 `structures` 放置的結構名稱，後者在其放置位置的 `radius` 範圍內算數    |
| `radius`             | 整數                         | `32`     | 離資源包自己的結構多近才算在內                                                                                                                                                      |
| `advancement`        | 文字                         | 無       | 進度 id                                                                                                                                                                             |
| `item`               | 文字                         | 無       | 物品，寫法與資源包其他地方相同，例如 `minecraft:diamond_sword`                                                                                                                      |
| `entity`             | 文字                         | 無       | 實體 id，例如 `minecraft:zombie`                                                                                                                                                    |
| `count`              | 整數                         | `1`      | 對 `kill` 而言：需要擊殺多少次。規則觸發後會重新計數                                                                                                                                |
| `rank`               | 文字                         | 無       | 用於 `kill`：只計入抽到此生物等級的生物。只寫名稱時使用該卡片的命名空間                                                                                                             |
| `raid`               | 文字                         | 無       | 用於 `raid_won` 和 `raid_lost`：突襲的註冊鍵。留空表示任何突襲                                                                                                                      |
| `below`, `above`     | 整數                         | 無       | 對 `y_level` 而言：要低於或高於的高度                                                                                                                                               |
| `time`               | 整數                         | `0`      | 對 `time_of_day` 而言：一天中的刻                                                                                                                                                   |
| `day`                | 整數                         | 無       | 對 `day` 而言：唯一要觸發的那一天。沒有它則每一天都觸發                                                                                                                             |
| `minutes`            | 整數                         | 無       | 用於 `play_time`                                                                                                                                                                    |
| `roll`               | 文字                         | 無       | 用於 `roll`：`coin`、形如 `2d6` 的寫法（`d6` 與 `1d6` 相同），或資料包裡骰子、牌組的名稱。不填時，每次擲骰都會觸發該規則                                                            |
| `order`              | 文字                         | 無       | 用於 `order_done`：訂單的 `sign` 詞。不填時，每個完成的訂單都會觸發該規則                                                                                                           |
| `exposure`           | 文字                         | 無       | 用於 `exposure_level`：危險在 `exposures` 下的檔案名稱。不填時，每種危險都會觸發該規則                                                                                              |
| `level`              | 整數                         | 無       | 用於 `exposure_level`：僅當玩家的等級變為該值時觸發；`0` 表示不再暴露。不填時，每次變化都會觸發                                                                                     |
| `environment`        | 文字                         | 無       | 用於 `environment_change`：啟用的環境名稱，例如 `night`，或 `default`。不填時，每個啟用的環境都會觸發該規則                                                                         |
| `atLeast`, `atMost`  | 整數                         | 無       | 用於 `roll`：能觸發該規則的最小和最大總和。硬幣正面算 1、反面算 0，資料包裡的骰子或牌組則按結果在其檔案中的位置計，從 1 開始                                                        |
| `objective`, `score` | 文字，整數                   | 無，`1`  | 對 `score` 而言：目標，以及要達到的值                                                                                                                                               |
| `portway`            | 文字                         | 無       | 用於 `portway_*` 觸發器：傳送通道的方塊 id，例如 `mypack:northgate`。省略則任意傳送通道都可                                                                                         |
| `network`            | 文字                         | 無       | 用於 `portway_*` 觸發器：傳送通道所屬的網路。省略則任意網路都可                                                                                                                     |
| `reason`             | 文字                         | 無       | 用於 `portway_refused`：`gate`、`dimension`、`cost`、`cooldown`、`nowhere`、`network`、`travel` 或 `item`。用於 `portway_forget`：`gone`、`player` 或 `command`。省略則任意原因都可 |
| `requires`           | 模組 id 或資源包命名空間清單 | 無       | 除非全部存在，否則跳過該檔案                                                                                                                                                        |

### 時機

*卡片*

`when` 包含在觸發器觸發的當下必須全部成立的條件。

| 設定                        | 類型       | 檢查內容                                                                   |
| --------------------------- | ---------- | -------------------------------------------------------------------------- |
| `biomes`                    | 清單       | 玩家位於這些生態域之一，寫法與觸發器相同                                   |
| `environments`              | 清單       | 玩家所在維度中，這些環境之一處於啟用狀態，包括 `default`                   |
| `timeFrom`, `timeTo`        | 整數       | 日夜時鐘在此區間內，區間可以跨越午夜，例如 `13000` 到 `1000`               |
| `dayAtLeast`                | 整數       | 天數至少為此值                                                             |
| `advancement`               | 文字       | 玩家擁有此進度                                                             |
| `portway`, `network`        | 文字       | 玩家的清單或全域傳送通道中，有該方塊 id 或該網路的傳送通道。兩者可只寫其一 |
| `gameMode`                  | 文字       | 玩家處於此遊戲模式：`survival`、`creative`、`adventure` 或 `spectator`     |
| `team`                      | 文字       | 玩家屬於此計分板隊伍                                                       |
| `dietAtLeast`               | 整數       | 玩家記住的餐食中至少有這麼多種不同的食物（需要飲食多樣性檔案）             |
| `objective`, `scoreAtLeast` | 文字，整數 | 玩家在該目標上的分數至少為此值                                             |

### 該卡片

*卡片*

| 設定         | 類型   | 預設             | 作用                                                                                                                                                  |
| ------------ | ------ | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| `title`      | 文字   | 無               | 第一行，在中央卡片上以較大字級繪製                                                                                                                    |
| `lines`      | 清單   | 無               | 最多十六行。除了內建規則外，規則需要標題或行。`{player}`、`{dim}`、`{biome}` 和 `{day}` 會被填入；`{text}` 是內建訊息，單獨放在一行時會給出它的每一行 |
| `style`      | 文字   | `corner`         | `corner` 是 `saysCard` 所顯示的右下角卡片；`center` 是畫面中央的卡片；`chat` 是聊天行；`bar` 是動作列                                                 |
| `icon`       | 文字   | `saysIcon`       | 繪製在角落卡片上的一個物品。留空則不繪製                                                                                                              |
| `color`      | 文字   | `saysColor`      | 卡片的背景顏色，十六進位                                                                                                                              |
| `image`      | 文字   | `saysImage`      | 來自資源包用戶端資源的一張 PNG，拉伸後鋪滿卡片作為其背景                                                                                              |
| `background` | 布林值 | `saysBackground` | `false` 會去掉面板、其邊框和顏色條；文字保留其陰影，`image` 仍會繪製                                                                                  |
| `font`       | 文字   | `saysFont`       | 繪製卡片文字所用的字型，格式為 `namespace:name`。留空則使用 RDPL 字型                                                                                 |
| `ticks`      | 整數   | `0`              | 卡片停留多久，包括淡出。`0` 保持標準的 `160`                                                                                                          |
| `audience`   | 文字   | `player`         | 誰會看到：`player`、`everyone`、`dimension`（玩家所在維度中的所有人）或 `team`（玩家的計分板隊伍）                                                    |
| `repeat`     | 文字   | `always`         | `always`、`once_per_player`、`once_per_world` 或 `once_per_session`（玩家重新登入後再次出現）                                                         |
| `cooldown`   | 整數   | `0`              | 同一位玩家再次觸發該規則之前的秒數                                                                                                                    |
| `runs`       | 文字   | 無               | 規則觸發時以該玩家的身分執行的函式                                                                                                                    |

當 `saysCard` 關閉時，角落卡片會改走聊天。玩家看過什麼會隨玩家保存，所以死亡和在維度之間移動後依然保留；`once_per_world` 則隨世界保存。

RDPL 字型 `resourcedatapackloader:rdpl` 是所有文字的預設：卡片、Says 訊息、歡迎與保持提示、世界介紹，以及遊戲自己的選單、聊天、HUD 和工具提示。它的粗體和斜體字形是 `resourcedatapackloader:rdpl_bold` 和 `resourcedatapackloader:rdpl_italic`。附魔台的文字仍保持遊戲原有的。

RDPL 隨附這些字型和字元。卡片、提示或介紹的 `font` 可以用簡稱，或完整 id 來指定 RDPL 字型：

| 名稱                                                   | 繪製內容                                                                                                                                             |
| ------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| `rdpl`（或 `resourcedatapackloader:rdpl`）             | RDPL 字型，含西里爾字母（U+0400 至 U+04FF）和盧恩字母（U+16A0 至 U+16F8）                                                                            |
| `rdpl_runic`（或 `resourcedatapackloader:rdpl_runic`） | 一種符文密碼：字母 A 至 Z 和 a 至 z 以符文繪製，其他所有字元以 RDPL 字型繪製。粗體段落以 `rdpl_runic_bold` 繪製，斜體段落以 `rdpl_runic_italic` 繪製 |
| 盧恩字母，U+16A0 至 U+16F8                             | 直接寫成盧恩字元本身（ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ），在 RDPL 字型繪製的任何文字中都可以，包括聊天；粗體和斜體段落保留其字形                                          |

卡片字型是位於 `assets/<namespace>/textures/font/<name>.png` 的一張 PNG，是一個 16 乘 16 字形的網格，排列方式與遊戲自己的 `ascii.png` 相同，卡片會依從中讀到的字形寬度調整大小。旁邊的 `<name>_cyrillic.png`，同樣的網格，容納 Unicode U+0400 至 U+04FF，用來繪製西里爾字母；沒有它時，西里爾字母來自遊戲自己的字頁。`<name>_runes.png`，同樣的網格，容納 U+1600 至 U+16FF，以同樣方式繪製盧恩字母。1.20.1 和 1.21.1 的版本改為讀取位於 `assets/<namespace>/font/<name>.json` 的字型定義，其 `bitmap` 提供者可以指向同一張 PNG，所以同時提供兩種檔案的資源包，在三個版本上繪製出的字樣相同。`minecraft:default` 指的是遊戲的字型。沒有任何資源包提供的字型會退回遊戲的字型，並在 `rdpl.log` 中留下一則警告。

資源包可以提供自己的 `assets/resourcedatapackloader/textures/font/rdpl.png`（以及 `rdpl_bold.png`、`rdpl_italic.png` 和它們的 `_cyrillic.png` 與 `_runes.png` 圖集）來更改 RDPL 字型，它會在所有地方取代原字型，包括遊戲的文字。提供 `assets/minecraft/textures/font/ascii.png`（例如原版的副本）的資源包，則會讓遊戲自己的文字改用該字型，西里爾字母和盧恩字母來自遊戲的字頁；除非 `saysFont` 為 `minecraft:default`，否則 RDPL 自己的文字仍使用 RDPL 字型。

卡片的標題與各行、Says 訊息以及歡迎與保持提示，支援「世界介紹」「文字格式」下表中的行內標記：粗體、斜體、粗斜體、刪除線、程式碼、連結和符文段落。粗體段落以字型的 `_bold` 字形繪製，斜體段落以其 `_italic` 字形繪製；沒有該字形的字型，該段落採用遊戲的粗體或斜體樣式，卡片會依實際繪製的段落調整大小。沒有安裝本模組的玩家會得到同樣的標記作為聊天格式，符文段落則顯示為其原本的字母。

### 內建規則

*卡片*

| Id                       | 訊息                                   | 其文字來源                                                                                                                  |
| ------------------------ | -------------------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| `rdpl:gate_unlocked`     | 閘門開啟                               | [閘門](#閘門)中的 `unlockedMessage`                                                                                         |
| `rdpl:gate_blocked`      | 關閉的閘門把玩家擋回，顯示在動作列上   | [閘門](#閘門)中的 `blockedMessage`                                                                                          |
| `rdpl:team_joined`       | 玩家加入一個陣營                       | 該陣營的 `displayName`                                                                                                      |
| `rdpl:team_lead`         | 陣營的主導者交到某位玩家手上           | [隊伍](#隊伍)中的 `leadSays`                                                                                                |
| `rdpl:team_picked`       | 玩家被選入一個陣營                     | 該陣營的 `displayName`                                                                                                      |
| `rdpl:team_round_ended`  | 回合結束，因此玩家被移到一個陣營       | 該陣營的 `displayName`                                                                                                      |
| `rdpl:downed`            | 玩家倒地而不是死亡                     | [隊伍](#隊伍)中的 `down.says`                                                                                               |
| `rdpl:revived`           | 隊友將倒地的玩家救起                   | [隊伍](#隊伍)中的 `down.revivedSays`                                                                                        |
| `rdpl:lobby_joins`       | 在回合中途登入的玩家被送往大廳         | [大廳](#大廳)中的 `opens.joinsSays`                                                                                         |
| `rdpl:lobby_note`        | 畫面中央的大廳提示行                   | [大廳](#大廳)中的 `opens.says`、`opens.leaderSays`                                                                          |
| `rdpl:scoring_results`   | 一回合結束時給每位玩家的排名           | [結果](#結果)中的 `results.card`、`results.title`、`results.icon`、`results.image`、`results.background`、`results.seconds` |
| `rdpl:scoring_out`       | 被淘汰的玩家                           | [回合如何結束](#回合如何結束)中的 `ends.outSays`                                                                            |
| `rdpl:reset_lead`        | 主導者重置回合                         | [重置回合](#重置回合)中的 `reset.leadSays`                                                                                  |
| `rdpl:reset_vote`        | 發起重置投票                           | `reset.voteSays`                                                                                                            |
| `rdpl:reset_pass`        | 投票通過                               | `reset.passSays`                                                                                                            |
| `rdpl:reset_fail`        | 投票失敗                               | `reset.failSays`                                                                                                            |
| `rdpl:anvil_waits`       | 鐵砧的加工等待某個進度                 | [鐵砧操作](#鐵砧操作)                                                                                                       |
| `rdpl:threat`            | 玩家的威脅等級改變                     | `threatSays`                                                                                                                |
| `rdpl:prospect`          | 探勘結果回報的每一行                   | 探勘結果                                                                                                                    |
| `rdpl:prospect_none`     | 探勘一無所獲                           | 語言檔                                                                                                                      |
| `rdpl:portway_activated` | 玩家把傳送通道加入清單時，顯示在動作列 | 語言檔                                                                                                                      |
| `rdpl:portway_refused`   | 傳送通道拒絕玩家時，顯示在動作列       | 語言檔                                                                                                                      |
| `rdpl:portway_forgot`    | 站點離開玩家的清單時，顯示在動作列     | 語言檔                                                                                                                      |
| `rdpl:board_result`      | 棋盤遊戲結束                           | 語言檔                                                                                                                      |
| `rdpl:pregen_ended`      | 預先生成完成或停止                     | [預先生成](#預先生成)中的 `pregenFinishedSays`、`pregenStoppedSays`                                                         |
| `rdpl:pregen_running`    | 玩家在預先生成期間加入時看到的進度行   | `pregenRunningSays`                                                                                                         |

`welcomeSays` 不是規則，並保留其標誌；`first_join` 或 `dimension_enter` 規則會附加在它之上。一回合的動作列倒數和統計維持原設定所定的樣子。

## 對話

*遊戲模式*

`<namespace>/dialogs/*.json`

檔案名稱由你自行決定，只會讀取該資料夾，多個檔案會疊加。每個檔案是一個對話，其 id 為 `<namespace>:<file name>`。實體變體透過 `dialog` 鍵開啟對話：對該實體按右鍵時，會顯示對話的 `start` 節點，取代實體平時按右鍵會做的事，例如村民的交易畫面。

```json
{
  "start": "hello",
  "title": "{npc}",
  "icon": "minecraft:emerald",
  "color": "3A2A10",
  "font": "mypack:runes",
  "nodes": {
    "hello": {
      "lines": ["Welcome, {player}.", "What brings you to {biome}?"],
      "replies": [
        { "text": "Who are you?", "goto": "who" },
        { "text": "I want work.", "runs": "mypack:give_work", "card": "mypack:work_card" },
        { "text": "Adventure only.", "when": { "gameMode": "adventure" } },
        { "text": "Goodbye." }
      ]
    },
    "who": { "title": "The Guide", "lines": ["I keep the road."], "replies": [{ "text": "Back.", "goto": "hello" }] }
  }
}
```

第一個回覆通往 `who` 節點，該節點有自己的 `title`，並有一個返回的回覆。第二個會執行一個函式並顯示一張卡片。第三個只有玩家處於冒險模式時才會顯示，最後一個沒有 `goto`，所以會結束對話。比螢幕高的面板可用滑鼠滾輪或右側的捲軸捲動。

### 對話鍵

*對話*

| 設定              | 類型                         | 預設             | 作用                                                                |
| ----------------- | ---------------------------- | ---------------- | ------------------------------------------------------------------- |
| `start`           | 文字                         | 無，必填         | 對話開始的節點名稱。`start` 不是其任何節點的對話會被捨棄            |
| `nodes`           | 物件                         | 無，必填         | 各節點，每個都有自己的名稱。最多 64 個                              |
| `title`           | 文字                         | 變體的名稱       | 面板的第一行。`{npc}` 是實體的名稱                                  |
| `icon`            | 文字                         | `saysIcon`       | 與卡片相同，見 [該卡片](#該卡片)                                    |
| `color`           | 文字                         | `saysColor`      | 與卡片相同，見 [該卡片](#該卡片)                                    |
| `image`           | 文字                         | `saysImage`      | 與卡片相同，見 [該卡片](#該卡片)                                    |
| `background`      | 布林值                       | `saysBackground` | 與卡片相同，見 [該卡片](#該卡片)                                    |
| `font`            | 文字                         | `saysFont`       | 與卡片相同，見 [該卡片](#該卡片)                                    |
| `titleColor`      | 文字                         | `FFFFFF`         | 標題的顏色，格式為十六進位值                                        |
| `textColor`       | 文字                         | `E8E8E8`         | 標題下方各行的顏色，格式為十六進位值                                |
| `replyColor`      | 文字                         | `FFFFFF`         | 回覆的顏色，格式為十六進位值                                        |
| `hoverColor`      | 文字                         | `FFFF55`         | 指標所在回覆的顏色，格式為十六進位值                                |
| `hoverBackground` | 文字                         | `FFFFFF`         | 指標所在回覆後方色帶的顏色，格式為十六進位值。以 25% 的不透明度繪製 |
| `history`         | 布林值                       | `true`           | `false` 會移除用來翻回本次對話中已顯示行的 `<` 與 `>` 按鈕          |
| `requires`        | 模組 id 或資源包命名空間清單 | 無               | 除非全部存在，否則跳過該檔案                                        |

### 節點鍵

*對話*

| 設定      | 類型 | 預設       | 作用                                                                    |
| --------- | ---- | ---------- | ----------------------------------------------------------------------- |
| `title`   | 文字 | 對話的標題 | 在顯示此節點期間，取代對話的 `title`                                    |
| `lines`   | 清單 | 無         | 最多十六行。`{player}`、`{npc}`、`{dim}`、`{biome}` 和 `{day}` 會被填入 |
| `replies` | 清單 | 無         | 最多八個回覆，以編號清單顯示。沒有回覆的節點只顯示它的行，對話到此結束  |

### 回覆鍵

*對話*

| 設定    | 類型   | 預設                    | 作用                                                                                                               |
| ------- | ------ | ----------------------- | ------------------------------------------------------------------------------------------------------------------ |
| `text`  | 文字   | 無，必填                | 玩家讀到的回覆文字。沒有 `text` 的回覆會被捨棄                                                                     |
| `goto`  | 文字   | 無                      | 接下來前往的節點名稱。`goto` 沒有指向任何節點的回覆會被捨棄                                                        |
| `card`  | 文字   | 無                      | 選擇該回覆時顯示的 [卡片](#卡片) 中的規則 id。其 `when`、`repeat` 和 `cooldown` 會被略過，`runs` 仍會執行          |
| `runs`  | 文字   | 無                      | 選擇該回覆時以玩家身分執行的函式。回覆就是這樣執行指令的                                                           |
| `when`  | 物件   | 無                      | [時機](#時機) 中的條件。不符合條件的回覆會被隱藏                                                                   |
| `close` | 布林值 | 沒有 `goto` 時為 `true` | 選擇後結束對話。設定了 `goto` 時不起作用                                                                           |
| `trade` | 布林值 | `false`                 | 選擇後開啟說話者自己的交易並結束對話，因此 `goto` 會被忽略。不是商人或正在交易的說話者什麼也不會開啟，對話仍然結束 |

選擇一個回覆時，先執行 `runs`，再顯示 `card`，然後前往 `goto` 或結束對話。對話本身不保存任何狀態：它不會被存檔，已關閉的對話，或因玩家登出而中斷的對話，都無法恢復。需要記住某件事的分支，應在其函式中設定一個分數或標籤。

安裝了本模組的用戶端會把對話顯示為靠近畫面底部的面板，每個回覆佔一行。點擊某一行，或按它的編號（1 到 8）；按 Esc 或物品欄鍵可關閉對話。原版用戶端會以聊天行收到同樣的文字，每個回覆是可點擊的 `[n]`，會執行 `/rdplserver talk n`。只有在實體存活且離玩家不超過 8 格時，伺服器才接受選擇，並忽略目前節點沒有的編號；玩家離開超過 8 格時，畫面會自行關閉。缺少 `start`、沒有 `text` 的回覆和未知的 `goto` 會寫入日誌，並捨棄該對話或該回覆；`dialog` 沒有任何資源包提供的實體變體也會寫入日誌，並保持沒有對話。

## 骰子與牌組

*遊戲模式*

`<namespace>/dice/*.json`

檔案名稱由你決定，多個檔案會疊加合併。一個檔案可定義各面帶有權重的骰子、抽出後不放回的卡牌牌組、預設由誰聽到擲骰結果，以及結果的措辭。擲骰本身透過 [`/rdplserver game`](#遊戲) 以及任何帶有 [`rolls`](#物品檔案鍵) 的物品進行。

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
    "pickplayer": "{player} picks {result}",
    "pickteam": "{player} picks the team {result}",
    "heads": "Heads",
    "tails": "Tails",
    "lastnone": "Nobody has rolled yet",
    "nobody": "Nobody is here to hear it",
    "notallowed": "You may not do that",
    "usage": "Use: /rdplserver game coin",
    "die": "{player} rolls {dice}d{sides}: {result}",
    "packdie": "{player} rolls the {die}: {result}",
    "dice": "{player} rolls {dice}: {rolls} makes {result}",
    "advantage": "{player} rolls {dice} with advantage: {first}, {second}, so {result}",
    "disadvantage": "{player} rolls {dice} with disadvantage: {first}, {second}, so {result}",
    "pickmember": "{player} picks {result} from {team}",
    "draw": "{player} draws {result} from {deck}, {left} left",
    "reshuffled": "{player} reshuffles {deck} and draws {result}, {left} left",
    "shuffle": "{player} shuffles {deck}, {left} cards",
    "left": "{left} cards left in {deck}",
    "empty": "{deck} is empty",
    "nodeck": "There is no deck called {deck}",
    "teamroll": "{member} rolls {dice}: {result}",
    "teamrollwin": "The team roll comes to {result}, {score} points",
    "tiebreak": "{objective} is tied between {sides}: {result} wins",
    "notie": "{objective} is not tied",
    "noobjective": "There is no objective called {objective}",
    "turn": "{group} to move, {seconds} seconds",
    "turnclock": "{group}: {seconds} seconds",
    "turnwarn": "{group}: only {seconds} seconds left",
    "turnout": "{group} ran out of time",
    "turnpass": "{group} passes",
    "turngap": "Next turn in {seconds} seconds",
    "notturn": "It is not your turn",
    "noturns": "Turns are not running",
    "badsides": "{sides} is not a number of sides",
    "badroll": "{roll} is not a roll",
    "badaudience": "{audience} is not an audience",
    "nodie": "There is no die called {name}",
    "noteam": "There is no team called {team}",
    "coin": "{player} tosses the old coin: {result}"
  }
}
```

| 鍵         | 類型 | 預設  | 作用                                                                                                                                                                                                                |
| ---------- | ---- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `audience` | 文字 | `all` | 未自行指定對象的擲骰由誰聽到：`self`、`team`、`all`、`radius <blocks>` 或 `silent`。以第一個設定它的資源包為準；之後的會記入日誌                                                                                    |
| `dice`     | 物件 | 空    | 骰子名稱對應一個「面對應權重」的物件。權重為 2 的面出現的機率是權重為 1 的兩倍。權重為 1 以上的整數                                                                                                                 |
| `decks`    | 物件 | 空    | 牌組名稱對應其卡牌清單，或對應物件 `{ "cards": [...], "reshuffle": false }`。`reshuffle` 未設定時為 `true`：從空牌組抽牌時，會將所有牌重新洗牌後再抽。設為 `false` 時，牌組會保持為空，直到執行 `game deck shuffle` |
| `says`     | 物件 | 空    | 措辭鍵對應文字，用來在所有語言中取代模組自帶的措辭。鍵及其佔位符見下方                                                                                                                                              |

骰子或牌組的名稱屬於第一個載入它的資源包。另一個資源包若使用相同名稱，或使用名稱 `coin`，會被略過，並在日誌中記錄一則錯誤。資源包骰子以 `game die <name>` 擲出並顯示其面；存為分數時，以該面在檔案中的位置計算，從 1 開始。

牌組是一疊越抽越少的牌。`game deck draw <name>` 從剩下的牌中隨機抽一張，抽出的牌不會放回，直到空牌組在下一次抽牌時自行重新洗牌，或 `game deck shuffle <name>` 將所有牌放回。牌組會隨世界一同儲存，因此重新啟動不會重新洗牌。

每次擲骰都使用世界自己的隨機數，並連同執行者、執行了什麼，以及結果一起寫入日誌。`game last` 會顯示最近幾次。結果以伺服器組好的一般聊天行送出，因此沒有安裝本模組的玩家也看得到。骰子記法由個數、`d`、面數和可選的修正值組成：`3d8-2` 表示三顆八面骰相加再減 2，`d20` 表示一顆二十面骰。聊天與日誌會把擲骰寫成文字，例如「Boss 擲了 3 顆八面骰，減 2：[2, 6, 7] = 13」，`{dice}` 即為這段文字。

物品可以保存自己擲出的結果，卡片可以對一次擲骰作出反應。下面的物品使用時會擲 `2d6`，告知聽眾，並把總和作為持有者的分數寫入目標 `dice_total`。下面的卡片在有人擲 `2d6` 且點數達到 10 或更高時顯示，並執行一個可以讀取該分數的函數。

```json
{
  "type": "basic",
  "rolls": "2d6",
  "rollStore": "dice_total",
  "cooldown": 20,
  "variants": { "dice_pair": { "meta": 0 } }
}
```

```json
{
  "trigger": "roll",
  "roll": "2d6",
  "atLeast": 10,
  "title": "幸運一擲",
  "lines": ["{player} 擲出了高點數。"],
  "runs": "mypack:lucky_throw"
}
```

無論是 `/rdplserver game` 還是物品擲骰，都會在分數存好之後觸發卡片。來自主控台或指令方塊的擲骰沒有玩家，因此不會觸發任何卡片。不使用 `rollStore` 和 `roll` 時，物品和卡片的行為與以前相同。

| 措辭鍵                                                        | 佔位符                                                       |
| ------------------------------------------------------------- | ------------------------------------------------------------ |
| `coin`、`pickplayer`、`pickteam`                              | `{player}`、`{result}`                                       |
| `heads`、`tails`、`lastnone`、`nobody`、`notallowed`、`usage` | 無                                                           |
| `die`                                                         | `{player}`、`{dice}`、`{sides}`、`{result}`                  |
| `packdie`                                                     | `{player}`、`{die}`、`{result}`                              |
| `dice`                                                        | `{player}`、`{dice}`、`{rolls}`、`{result}`                  |
| `advantage`、`disadvantage`                                   | `{player}`、`{dice}`、`{first}`、`{second}`、`{result}`      |
| `pickmember`                                                  | `{player}`、`{team}`、`{result}`                             |
| `draw`、`reshuffled`                                          | `{player}`、`{deck}`、`{result}`、`{left}`                   |
| `shuffle`                                                     | `{player}`、`{deck}`、`{left}`                               |
| `left`、`empty`、`nodeck`                                     | `{deck}`，`left` 另有 `{left}`                               |
| `teamroll`                                                    | `{member}`、`{dice}`、`{result}`                             |
| `teamrollwin`                                                 | `{result}`、`{score}`                                        |
| `tiebreak`                                                    | `{objective}`、`{sides}`、`{result}`                         |
| `notie`、`noobjective`                                        | `{objective}`                                                |
| `turn`、`turnclock`、`turnwarn`                               | `{group}`、`{seconds}`                                       |
| `turnout`、`turnpass`                                         | `{group}`                                                    |
| `turngap`                                                     | `{seconds}`                                                  |
| `notturn`、`noturns`                                          | 無                                                           |
| `badsides`、`badroll`、`badaudience`、`nodie`、`noteam`       | 依序為 `{sides}`、`{roll}`、`{audience}`、`{name}`、`{team}` |

模組自帶的措辭位於其語言檔案中，鍵為 `rdpl.game.<key>`，因此資源包也可以逐一語言更改它。

## 桌上遊戲

*遊戲模式*

`<namespace>/games/*.json`

一個檔案就是一款桌上遊戲，以檔案名稱命名。它設定棋盤、雙方、棋子及其走法、開局局面，以及結果的獎勵。`game board start <game> <board>` 會在發送者所站的位置或指定的位置擺出棋盤：格子鋪在下方一格，直行向東延伸，橫列向南延伸，每個棋子都是資源包選定的生物，靜止不動、無聲且不受傷害，並以所屬陣營命名。被吃掉的棋子站在棋盤旁邊。

```json
{
  "name": "Chess",
  "board": { "files": 8, "ranks": 8, "light": "minecraft:quartz_block", "dark": "minecraft:coal_block" },
  "quiet": false,
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

| 鍵       | 類型          | 預設             | 作用                                                                                                  |
| -------- | ------------- | ---------------- | ----------------------------------------------------------------------------------------------------- |
| `name`   | 文字          | 檔案名稱         | 在聊天與結果中顯示的名稱                                                                              |
| `board`  | 物件          | 8 × 8            | `files` 與 `ranks`，各為 2 到 16，以及鋪在格子下方的方塊狀態 `light` 與 `dark`。省略時，地面保持原樣  |
| `sides`  | 清單          | `White`、`Black` | 兩個由 `name` 與 `color`（聊天顏色）組成的物件。第一方先走，並以大寫字母擺子                          |
| `pieces` | 物件          | 無               | 棋子名稱對應一個由下方棋子鍵組成的物件                                                                |
| `setup`  | 清單          | 無               | 每一橫列一段文字，第 1 橫列在前。棋子的字母決定擺放哪種棋子，第一方用大寫，第二方用小寫，`.` 表示空格 |
| `rules`  | 物件          | 無               | `mustCapture`、`chainCaptures`、`quietDraw` 與 `repeatDraw`，見下方                                   |
| `clock`  | 物件          | 無               | 每方的 `minutes`，以及每步之後加上的 `addSeconds`。時間用完的一方判負                                 |
| `ai`     | 數字          | `2`              | 電腦的等級，1 到 4，用於無人執掌的一方                                                                |
| `result` | 物件          | 無               | 對局結束時發放的 `objective`，以及 `win`、`draw` 與 `loss` 分數，省略時為 1、0、0                     |
| `quiet`  | true 或 false | `false`          | 不在聊天中顯示走棋、選子與將軍的訊息。拒絕提示與對局結果仍會送出，每一步也仍會寫入 `logs/rdpl.log`    |

| 棋子鍵      | 類型          | 作用                                                                                                                                                                                                                                                 |
| ----------- | ------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `letter`    | 文字          | 用於 `setup` 與 `game board show` 的字母                                                                                                                                                                                                             |
| `value`     | 數字          | 電腦眼中它的價值                                                                                                                                                                                                                                     |
| `mobs`      | 清單          | 每一方的生物，第一方在前。`mob` 可為雙方指定同一個                                                                                                                                                                                                   |
| `moves`     | 清單          | 以物件表示的步法：`steps`，從本方底線看的 `[file, rank]` 偏移清單；`slides`，一路前進直到被擋住；`captures`，`both`（預設）、`never`、`only` 或 `hop`，後者會跳過對方的一枚棋子，落到其後的空格並將其吃掉；以及 `firstRange`，第一次移動時可以走幾步 |
| `royal`     | true 或 false | 這枚棋子被將死時該方判負，且任何一步都不能讓它處於被攻擊的狀態。沒有王棋時，無棋可走的一方判負                                                                                                                                                       |
| `enPassant` | true 或 false | 可以吃掉剛從它旁邊走過兩格的棋子                                                                                                                                                                                                                     |
| `castles`   | 文字          | 一個搭檔棋子：這枚棋子朝尚未移動過的搭檔走兩格，搭檔會跳過它                                                                                                                                                                                         |
| `promotes`  | 清單          | 到達底線時可以變成什麼。除非著法指定其他棋子，否則使用第一個                                                                                                                                                                                         |

| 規則            | 作用                                                                                                                                         |
| --------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| `mustCapture`   | 能吃子的一方必須吃子                                                                                                                         |
| `chainCaptures` | 吃子的跳躍之後，同一枚棋子只要還能吃就繼續吃                                                                                                 |
| `quietDraw`     | 雙方合計連續多少步既沒有吃子、也沒有可升變棋子的移動，對局即判和。0 表示永不判和                                                             |
| `repeatDraw`    | 同一局面在同一方走棋的情況下出現這麼多次時，對局判和；3 即為西洋棋的三次重複局面。只計算上一次吃子或可升變棋子移動之後的局面。0 表示永不判和 |

一方屬於第一個對其棋子按右鍵的玩家；若該玩家在計分板隊伍中，則屬於該隊伍，因此之後任何成員都能操作這一方。無人執掌的一方，會在另一方已有人執掌後由電腦來走，或在 `game board ai` 為其設定等級時立即由電腦來走；電腦在伺服器執行緒之外思考，其著法則在伺服器執行緒上執行。對一枚棋子按右鍵會顯示它能去哪裡，再對一個格子或對方的一枚棋子按右鍵即可移動過去。`game board move` 以格子名稱做同樣的事，例如 `e2 e4`，升變時在末尾加上棋子名稱。當一步棋造成升變且 `promotes` 列出不只一種棋子時，玩家要從聊天中可點擊的選項裡挑選；10 秒內沒有選擇，或玩家改為點擊棋盤時，會採用第一項。電腦會選擇它判斷最好的一項。吃子的棋子會先攻擊被吃的棋子，帶有揮擊動作、受傷閃紅與擊中音效，但不造成傷害，之後被吃的棋子會移到棋盤旁屬於它的那一列。

`game board resign` 表示認輸。`game board draw` 提出和棋，對方以同一指令接受；電腦在局面較佳時會拒絕。`game board takeback` 請求撤銷請求者的上一步，對方以同一指令同意；對電腦時會立即撤銷。

棋盤以著法清單的形式保存在世界的儲存資料中。世界載入或資源包重新載入時，局面會依這份清單重新演算，棋子會依其重新擺放，棋盤上任何舊的棋子都會被移除。

結束時，`result` 會在各方執掌者的目標中發放分數，記為玩家列或隊伍列。結果會送給棋盤附近的玩家與執掌一方的玩家：若有卡片檔案設定了 `rdpl:board_result` 這個 id，就以該卡片顯示，否則以一行聊天訊息顯示。措辭位於模組的語言檔案中，鍵為 `rdpl.game.board.<key>`。

---

# 控制

## 控制層

*控制*

所有會阻止或改變生成的項目都被分組，每個群組在設定的 `control` 類別中有一個鍵，可設為三個值：

| 值        | 意義                                   |
| --------- | -------------------------------------- |
| `default` | 由資源包決定。設定值作為後備           |
| `global`  | 以設定為準。資源包中的對應部分會被忽略 |
| `off`     | 該群組完全停用，任何資源包都無法啟用   |

這些群組為 `ores`、`biomes`、`generators`、`structures`、`spawning`、`bedrock`、`voidWorld`、`recipes`、`terrain`、`replacements`、`villages`、`entities`、`chunks`、`blastPlaster`、`commands` 和 `server`。

設定依 **生態域 → 世界範本 → 設定** 的順序解析。世界範本的 `settings` 區段使用與設定相同的鍵名，因此資源包設定它們的方式和你自己設定的一樣：

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

群組的控制為 `default` 時這些設定優先，為 `global` 時會被忽略，為 `off` 時則不論任何資源包怎麼說，整個群組都不起作用。

## 各群組的作用

*控制*

### 礦石

*各群組的作用*

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

| 設定                             | 類型                | 預設            | 作用                                                                                                                                                       |
| -------------------------------- | ------------------- | --------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blockOres`                      | 布林值              | `false`         | 阻止所有模組與 Minecraft 生成礦石，`oreWhitelist` 中列出的模組除外。只有經由 Forge 礦石生成事件的生成才能被攔截，這包含 Minecraft 與大多數模組，但並非全部 |
| `oreWhitelist`                   | 模組 id 清單        | `["minecraft"]` | 在 `blockOres` 開啟期間仍可生成礦石的模組                                                                                                                  |
| `oreTypes`                       | 礦石類型清單        | 無              | 阻擋所套用的礦石類型，以 Forge 名稱書寫，如 `COAL`、`IRON`。留空表示所有類型                                                                               |
| `oreTypesAreBlacklist`           | 布林值              | `true`          | 開啟時，`oreTypes` 中的類型就是被阻擋的類型。關閉時，只有這些類型會生成                                                                                    |
| `blockOreDimensions`             | 整數清單            | 無              | 礦石阻擋所套用的維度，留空表示所有維度。範圍之外的維度完全不受影響，因此主世界維持阻擋的同時，其他模組的礦石仍會在那裡生成                                 |
| `blockOreDimensionsAreBlacklist` | 布林值              | `false`         | 開啟時，所列的維度不受礦石阻擋影響                                                                                                                         |
| `prospectItems`                  | `item=entries` 清單 | 無              | 當潛行的玩家手持其中一件物品破壞方塊時，用來探勘 `vein` 型世界生成項目的物品。格式見下方段落                                                               |
| `prospectItemsAreBlacklist`      | 布林值              | `false`         | 開啟時，每件物品的清單就是它不讀取的項目                                                                                                                   |
| `prospectWear`                   | 整數                | `2`             | 一次探勘性破壞對工具造成的磨損是正常磨損的幾倍。`2`，即兩倍，是允許的最小值，沒有耐久度的物品不付出任何代價                                                |
| `prospectSlow`                   | 整數                | `2`             | 手持有標記物品的潛行玩家破壞方塊所需時間是正常的幾倍。`1` 為正常速度                                                                                       |
| `prospectDrops`                  | 布林值              | `false`         | 開啟時，在探勘模式下破壞的方塊仍會掉落物品並給予經驗值。關閉時，樣本會被消耗掉                                                                             |

**讀數。** `prospectItems` 的項目寫成 `item=entry|entry[,radius in chunks]`，或以 `item=*[,radius]` 表示所有礦脈項目，半徑預設為 8。潛行的玩家手持這類物品破壞方塊時，會針對它讀取的每個項目收到 `Possible hit on <ore> <direction> of this location, <deeper down | higher up | at about this depth>`；方向是從被破壞的方塊指向最近一處已排定礦脈的八個羅盤方位之一，絕不會給出具體座標；該方塊已位於礦脈範圍內時為 `at this location`，半徑內沒有任何已排定的礦脈時為 `No sign of anything here`。礦石的名稱取自項目的 `prospectAs`，否則取自其檔案名稱。讀數會重播生成時所做的同樣隨機判定，因此對尚未生成的土地也是準確的，有標記的物品會在其提示文字中說明它探勘的是什麼。

### 生態域

*各群組的作用*

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

| 設定                               | 類型           | 預設          | 作用                                                                                                                                                                                                    |
| ---------------------------------- | -------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blockBiomes`                      | 布林值         | `false`       | 阻止所有生態域生成，`biomeWhitelist` 中模組的生態域除外。被阻擋的生態域會在完成的生態域地圖上被替換，這是唯一能處理海洋、蘑菇島、惡地變種、叢林、丘陵與海岸的方式。全部阻擋後，主世界會自行變成虛空世界 |
| `biomeWhitelist`                   | 模組 id 清單   | `minecraft`   | 在 `blockBiomes` 開啟期間，其生態域仍會生成的模組。資源包的生態域使用該資源包的命名空間                                                                                                                 |
| `biomeNames`                       | 生態域名稱清單 | 無            | 依名稱套用的生態域，不論歸誰所有，也不論白名單怎麼設定。可以是 `Birch Forest` 這類通用名稱，或登錄名稱                                                                                                  |
| `biomeNamesAreBlacklist`           | 布林值         | `true`        | 開啟時，`biomeNames` 中的名稱會被阻擋。關閉時，只有這些名稱會生成                                                                                                                                       |
| `blockBiomeDimensions`             | 整數清單       | `0`，即主世界 | 生態域阻擋所套用的維度。留空表示所有維度                                                                                                                                                                |
| `blockBiomeDimensionsAreBlacklist` | 布林值         | `false`       | 開啟時，阻擋會略過所列的維度。關閉時，只對它們生效                                                                                                                                                      |

`blockBiomes` 與 `biomeWhitelist` 依模組運作，`biomeNames` 搭配 `biomeNamesAreBlacklist` 則依名稱運作。被阻擋的生態域會在完成的生態域地圖上被替換，這是唯一能處理海洋、蘑菇島、惡地變種、叢林、丘陵與海岸的方式，因為它們是在模組可編輯的清單之外選定的。阻擋所有生態域後，主世界會自行變成虛空世界。`blockBiomeDimensions` 將這一切限制在特定維度，留空表示所有維度，而 `blockBiomeDimensionsAreBlacklist` 會把該清單變成排除清單。

### 生成器

*各群組的作用*

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

| 設定                                   | 類型                       | 預設          | 作用                                                                                                                                                      |
| -------------------------------------- | -------------------------- | ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blockWorldGenerators`                 | 布林值                     | `false`       | 阻止其他模組透過自己的世界生成器生成內容，模組正是藉此加入 Forge 事件看不到的東西，例如史萊姆島、洞穴水晶之類。本模組自身的資源包生成永遠不會被阻擋       |
| `generatorWhitelist`                   | 模組 id 清單               | `minecraft`   | 仍可執行自身生成器的模組                                                                                                                                  |
| `blockedGenerators`                    | 模組 id 或類別名稱片段清單 | 無            | 被直接阻擋的個別生成器，不論白名單如何設定                                                                                                                |
| `blockGeneratorDimensions`             | 整數清單                   | `0`，即主世界 | 此項所套用的維度。留空表示所有維度                                                                                                                        |
| `blockGeneratorDimensionsAreBlacklist` | 布林值                     | `false`       | 開啟時，阻擋會略過所列的維度；關閉時，只對所列維度生效                                                                                                    |
| `generatorTypes`                       | 類型清單                   | 無            | 依生成器產出的內容而非所屬者來阻擋：`ores`、`structures`、`flora`、`lakes`、`terrain`，或用於都對不上的生成器的 `unknown`                                 |
| `generatorTypesAreBlacklist`           | 布林值                     | `true`        | 開啟時，所列類型會被阻擋；關閉時，只有所列類型會生成                                                                                                      |
| `generatorTypeMap`                     | `pattern=type` 清單        | 無            | 為類別名稱無法說明用途的生成器指定類型，其中 pattern 是模組 id 或生成器類別名稱的一部分。對應項目會先於內建關鍵字檢查，因此也能修正關鍵字判斷錯誤的生成器 |
| `logBlockedGenerators`                 | 布林值                     | `true`        | 在每個生成器第一次被阻擋時，記錄該生成器及其被判定的類型。`/rdplserver generators` 依模組與類型顯示累計總數                                               |

`blockWorldGenerators` 會阻止其他模組透過自己的世界生成器生成內容，模組正是藉此加入 Forge 事件看不到的東西，如史萊姆島、洞穴水晶之類。`generatorWhitelist` 保留指定的模組，`blockedGenerators` 指定個別生成器，而本模組自身的資源包生成永遠不會被阻擋。`blockGeneratorDimensions` 將範圍限制在特定維度，並可用 `blockGeneratorDimensionsAreBlacklist` 反轉該清單。

`generatorTypes` 依生成器產出的內容而非所屬模組來阻擋：`ores`、`structures`、`flora`、`lakes`、`terrain`，或用於都對不上的生成器的 `unknown`。`generatorTypesAreBlacklist` 決定方向：開啟時，所列類型被阻擋；關閉時，只有所列類型會生成。類型不論白名單如何設定都會生效，做法與 `oreTypes` 相同，因此你可以讓所有模組都不再添加礦石，同時保留它們的地城和樹木。

類型取自生成器的類別名稱，並與每種類型的內建關鍵字清單比對。這能正確判讀大多數模組，`NetherOreGenerator` 是 ores，`SlimeIslandGenerator` 是 structures，但名稱與用途毫無關聯的生成器，例如 ProjectRed 的 `SimpleGenHandler` 或 Draconic Evolution 的 `DEWorldGenHandler`，會被判為 `unknown`。`generatorTypeMap` 用來手動修正這些情況，每行一個 `pattern=type`，其中 pattern 是模組 id 或生成器類別名稱的一部分：

```
mrtjpcore=ores
deworldgenhandler=structures
```

對應項目會先於內建關鍵字檢查，因此也能修正關鍵字判斷錯誤的生成器。開啟 `logBlockedGenerators` 後，每個生成器在第一次被阻擋時都會連同被判定的類型一起記錄，而 `/rdplserver generators` 會依模組與類型顯示累計總數。

### 取代

*各群組的作用*

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

| 設定                                     | 類型               | 預設    | 作用                                                                                                              |
| ---------------------------------------- | ------------------ | ------- | ----------------------------------------------------------------------------------------------------------------- |
| `blockReplacements`                      | `block=block` 清單 | 無      | 將已存在區塊中的方塊換掉，兩側都可附帶選用的 meta。每個區塊在載入時處理一次，並在自身資料中標記，因此不會重複處理 |
| `blockReplacementDimensions`             | 整數清單           | 無      | 此項所套用的維度。留空表示所有維度                                                                                |
| `blockReplacementDimensionsAreBlacklist` | 布林值             | `false` | 開啟時，取代會略過所列的維度；關閉時，只對所列維度生效                                                            |
| `blockReplacementMinHeight`              | 整數               | `0`     | 檢查的最低 y 座標                                                                                                 |
| `blockReplacementMaxHeight`              | 整數               | `255`   | 檢查的最高 y 座標                                                                                                 |
| `blockReplacementKey`                    | 字串               | `0000`  | 更改它之後，每個區塊都會重新經過一次取代                                                                          |

`blockReplacements` 將已存在區塊中的方塊換掉，每行一個 `block=block`，兩側都可附帶選用的 meta：

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

每個區塊在從磁碟載入時處理一次，並在該區塊自身的資料中標記，因此不會重複處理。首次生成的區塊不會立即清理，而是在下次載入時清理，因為生成期間相鄰的區塊仍在向其中寫入內容。位於已探索區域邊緣的區塊會被清理但不會被標記，因此待周圍的土地生成後會再清理一次。`blockReplacementDimensions` 與 `blockReplacementDimensionsAreBlacklist` 決定在何處生效，`blockReplacementMinHeight` 與 `blockReplacementMaxHeight` 決定檢查世界中的哪一段高度，而 `blockReplacementKey` 是一個字串，更改它即可讓每個區塊重新處理一遍。不論 `retrogen` 是否開啟，它都會執行，因為需要清理的世界通常不會想再添加新的礦脈。它只會替換方塊：模組以結構形式生成的內容無法用這種方式移除，因為被它取代的原有地形從未被記錄下來。

### 村莊

*各群組的作用*

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
    "villageLayout": "mypack:downtown",
    "villageCitySpacing": 1,
    "villagePathSupportBlock": "minecraft:cobblestone",
    "villagePathBridgeBlock": "minecraft:planks",
    "villagePathBridgeBarrierBlock": "minecraft:fence",
    "villagePathBridgeBarrierHeight": 1,
    "villagePathBridgeSidewalkBlock": "minecraft:stone_slab",
    "villagePathBridgeDrop": 4,
    "villagePathBridgeFrameBlock": "minecraft:log",
    "villagePathBridgeFrameTopBlock": "minecraft:planks",
    "villagePathBridgeFrameHeight": 4,
    "villagePathBridgeFrameRun": 24,
    "villagePathBridgeFrameLeast": 24,
    "villagePathPiers": ["railed", "pilings", "boardwalk"],
    "villagePathPierCargo": ["minecraft:chest=1", "minecraft:hay_block=2,2", "empty=5"],
    "villagePathPierLoot": "resourcedatapackloader:chests/pier_cargo",
    "villagePathTunnelBlock": "minecraft:stonebrick",
    "villagePathTunnelDepth": 10,
    "villagePathTunnelLightBlock": "minecraft:glowstone",
    "villagePathTunnelLightRun": 8,
    "villageSewerBlock": "minecraft:stonebrick",
    "villageSewerDepth": 8,
    "villageSewerHeight": 3,
    "villageSewerWidth": 5,
    "villageSewerWaterBlock": "minecraft:water",
    "villageSewerWalkBlock": "minecraft:stone_slab",
    "villageSewerLightBlock": "minecraft:glowstone",
    "villageSewerLightRun": 8,
    "villageSewerLadderBlock": "minecraft:ladder",
    "villageSewerCoverBlock": "minecraft:trapdoor",
    "villageSewerMossBlock": "minecraft:mossy_cobblestone",
    "villageSewerMossChance": 25,
    "villageSewerVineBlock": "minecraft:vine",
    "villageSewerVineChance": 20,
    "villageSewerWellEntrance": true,
    "villageSubwayLines": 1,
    "villageSubwayDepth": 24,
    "villageSubwaySpacing": 64,
    "villageSubwayDirection": "ew",
    "villageSubwayWidth": 3,
    "villageSubwayTracks": 1,
    "villageSubwayTrackGap": 2,
    "villageSubwayBlock": "minecraft:rail",
    "villageSubwayTrackSeat": "auto",
    "villageSubwayBedBlock": "minecraft:gravel",
    "villageSubwayTieBlock": "minecraft:planks",
    "villageSubwayTieRun": 2,
    "villageSubwayShoulderBlock": "minecraft:cobblestone",
    "villageSubwayShoulderWidth": 1,
    "villageSubwayPowerBlock": "minecraft:golden_rail",
    "villageSubwayPowerBase": "minecraft:redstone_block",
    "villageSubwayPowerRun": 16,
    "villageSubwayTunnelBlock": "minecraft:stonebrick",
    "villageSubwayTunnelLightBlock": "minecraft:glowstone",
    "villageSubwayTunnelLightRun": 8,
    "villageSubwayClimb": 8,
    "villageSubwayTail": 48,
    "villageSubwaySurfaces": 25,
    "villageSubwayStationLength": 12,
    "villageSubwayStationRun": 96,
    "villageSubwayStationRepeat": 12,
    "villageSubwayStationFoot": 4,
    "villageSubwayPlatformWidth": 3,
    "villageSubwayPlatformBlock": "minecraft:stone_slab",
    "villageSubwayStation": "mypack:subway_station",
    "villageSubwayRailingBlock": "minecraft:iron_bars",
    "villageSubwayBenchBlock": "minecraft:oak_stairs",
    "villageSubwayBenchEndBlock": "minecraft:log",
    "villageSubwayBenchLength": 5
  }
}
```

| 設定                        | 類型                        | 預設   | 作用                                                                                                                                                                              |
| --------------------------- | --------------------------- | ------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villageBlocks`             | `original=replacement` 清單 | 無     | 村莊建築所用的方塊，在所有其他模組處理完之後才套用。一組方塊後可附帶機率與條件，此時它就成為一條規則；各欄位見下方表格                                                            |
| `villagePieces`             | 建築片段名稱清單            | 無     | 每行一個原版村莊建築片段名稱：`house1`、`house2`、`house3`、`house4garden`、`church`、`woodhut`、`hall`、`field1`、`field2`。資源包的地塊以其自身範本命名，其他模組加入的片段亦同 |
| `villagePiecesAreBlacklist` | 布林值                      | `true` | 開啟時，所列片段會被阻擋；關閉時，只有所列片段會生成，且白名單只會移除原版自身的片段                                                                                              |
| `villagePlotsLeast`         | 整數                        | `0`    | 村莊至少接受的已建地塊數，計入房屋、農田與資源包地塊，但不計道路、火把或水井。佈局小於此數的村莊會重新生長幾次，並取最大的佈局。`0` 保持原版行為                                  |
| `villagePlotsBackRow`       | 布林值                      | `true` | 村莊生長完成後，再進行第二輪，在每個面向街道的地塊正後方安置一個地塊並使其朝向街道，採用相同的隨機判定與相同的空間檢測，如此兩條街之間的街廓內部就會被蓋滿，而不是留空            |
| `villagePlotsMost`          | 整數                        | `0`    | 最多允許的數量；達到上限時村莊會直接停止生長，不再增加建築與道路。`0` 保持原版行為                                                                                                |
| `villageTieStreets`         | 布林值                      | `true` | 開啟時，無法將街道延伸並接上現有村莊的街區，會鋪設一條筆直的連接街道，通往與它對齊的最近街道。關閉時，這樣的街區會被拆除                                                          |
| `villageCitySpacing`        | 整數，0 到 256              | `0`    | 啟用 `terrainAdaptation` 時，`1` 會讓世界中的每個街區都成為城鎮，即連綿城市。在 1.12.2 上只有 `1` 會產生變化：`0` 與其他任何數字都維持由 `structureSpacing` 排定村莊              |
| `villageBlockSizes`         | `size=weight` 清單          | 無     | 城市中平行街道之間的街廓有多深，依廣場位置為每個街區擲定一次。留空則讓每個街廓都取資源包所附的最大地塊                                                                            |
| `villageLayout`             | 字串                        | 空     | 指定一張[城市配置地圖](#城市配置地圖)，依繪製好的街道規劃來配置村莊，而不是讓它自行生長                                                                                           |

村莊與其他所有結構一樣，使用相同的 `structure=value` 清單，名稱為 `villages`，因此 `structureSpacing`、`structureMinDistanceFromSpawn`、`structureBiomes` 與 `structureBiomesAreBlacklist` 都對它們有效。不是黑名單的 `structureBiomes` 清單，還會加入該結構自身清單中從未包含的任何已指名生態域，因此可以讓村莊出現在山地中，為此請以登錄名稱指定，因為只有登錄名稱才能新增。其間距有 9 的下限，因為原版會從中減去 8。`villagePieces` 屬於同一個群組，所以一個開關就涵蓋村莊位於何處以及用什麼建造的所有設定，而 `villages` 群組只涵蓋資源包加入的地塊。

`villageBlocks` 以 `original=replacement` 組合替換村莊所用的建築方塊：`minecraft:cobblestone=mypack:ruby_brick`。它在所有其他模組處理完之後才套用，因此資源包永遠優先，即使面對依生態域替換村莊材料的模組也是如此。兩側都接受單純的方塊名稱，或帶有狀態的名稱。道路則由 `villagePathBlock` 及其同類設定另行指定。

一組方塊之後可以附帶機率與條件，以逗號分隔的欄位寫出，此時它就是一條規則，而不是單純的替換。`minecraft:cobblestone=minecraft:mossy_cobblestone,20` 會讓村莊鋪設的圓石有五分之一風化；`minecraft:planks=minecraft:sandstone,100,under=minecraft:sand` 只會在房屋蓋在沙子上的地方更換地板。這一組之後的欄位可以任意順序給出，若某個項目包含無法辨識的欄位，則整筆被拒絕，而不是只套用一半。

| 欄位     | 值             | 預設  | 作用                                 |
| -------- | -------------- | ----- | ------------------------------------ |
| chance   | 整數，1 到 100 | `100` | 規則生效的機率，以百分比計           |
| `at=`    | 方塊名稱       | 無    | 僅當正在建造的位置上原本就有此方塊時 |
| `under=` | 方塊名稱       | 無    | 僅當此方塊正好位於該位置的正下方時   |

單純的組合在建築片段詢問遊戲該用什麼來建造時作答，因此會一次改變該方塊的所有牆面。規則則在方塊實際被放置的地方逐個位置判定，這正是機率與條件得以生效的原因，而且它看到的是即將被放置的方塊，也就是單純的組合處理過之後的結果。機率落在哪些位置，由世界種子碼與該位置本身算出，因此無論生成多少次，同一個世界總是風化同樣的方塊。

道路永遠不適用規則，因此坡度、橋梁與路口設計仍會讀取它們所鋪設的道路。範本地塊鋪設的是它自己的 `.nbt` 檔案，而不是依遊戲的方式建造，所以規則不會深入其中；其方塊完全由該檔案決定。不論 `terrainAdaptation` 是否開啟，單純的組合與規則都有效。

`villagePieces` 指定原版村莊建築片段，`house1`、`house2`、`house3`、`house4garden`、`church`、`woodhut`、`hall`、`field1` 與 `field2`，而 `villagePiecesAreBlacklist` 決定方向，因此你可以去掉原版的小麥田而保留房屋，也可以只列出你想要的片段。資源包地塊以其自身範本命名：可以寫完整名稱 `mypack:big_house`，也可以只寫 `big_house`，或者隨你喜好使用地塊自己的名稱。因此資源包可以附帶十個地塊，而世界範本可以去掉其中一個，不影響其餘九個。其他模組加入的片段亦同，包括 Tektopia 的房屋或 Recurrent Complex 的地塊：白名單只會移除原版自身的片段，所以列出你想要的原版片段不會悄悄刪掉別人的片段。若要去掉某個模組片段，請使用黑名單並寫出其名稱，例如 `tekhouse2` 之類。

`villagePlotsLeast` 與 `villagePlotsMost` 限定一座村莊建造多少個地塊，計入房屋、農田與資源包地塊，不計道路、火把或水井。佈局低於下限的村莊會以更大的規模重新生長，嘗試幾次後取最大的佈局，因此在局促的地形上仍可能達不到要求。達到上限時，村莊會直接停止生長：不再增加建築，也不再增加道路。任一端設為 `0`，該端就維持原版行為。

`villageTieStreets` 在未設定時預設開啟，它會為無法將街道延伸並接上現有村莊的街區鋪設連接街道：一條全寬的筆直街道，從該街區的某個街道末端通往與之對齊的最近現有街道，條件是該線路比道路寬度更長，不超過 112 列，不穿過任何建築片段，不緊鄰平行街道，不穿過隧道，並且平坦到足以行走。沒有它時，這樣的街區會被拆除，這使得建在崎嶇地面上的城市只剩下廣場和四條街道；有了它，街區就會併入並繼續生長。建在平地上的城市很少需要它，而它預設開啟，是為了讓要求大型城市的資源包能得到大型城市；若要讓建在崎嶇地面上的城市保持較小，可將其關閉。

`villageBlockSizes` 以加權的 `size=weight` 項目設定城市中平行街道之間的街廓有多深：`32=3` 與 `64=1` 會讓四個街區中有三個採用 32 深的街廓，其餘採用 64。每個街區依其廣場位置擲定一次大小，因此同一個世界總是得到同樣的組合。其街道沿長度方向每隔兩個街廓加一個道路寬度就分出支路，因此 16 的街區是細密的網格，64 的街區則是粗疏的網格；兩條平行街道之間會保留相應數量的方塊，再加上相鄰街區的深度，所以 32 的街區緊鄰 64 的街區時，二者之間相隔 96。只有深度合適的地塊才會建在街區的街道旁，而在合適的地塊中，一個地塊的機率等於其權重乘以其寬度，因此深街廓偏向能填滿它們的建築，而 64 寬的地塊永遠不會面向 32 深的街廓。街道長度與廣場間距仍取決於資源包所附的最大地塊，這正是每個街區都能連通城市的原因。留空則讓每個街廓都依那個最大地塊的大小，且街道只在末端分支，與以前一樣。

`villageLayout` 指定一張[城市配置地圖](#城市配置地圖)，依繪製好的街道規劃來配置村莊，而不是讓它自行生長；留空則照常生長。

#### 村莊道路

*村莊*

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

以下所有設定都只在 `terrainAdaptation` 開啟時才有作用。它們預設全部為空或為零，此時原版的道路會維持原樣。

**混合方塊。** 部分方塊設定接受混合方案，而不只是單一方塊：以逗號分隔的多個方塊，每個方塊後面接一個空格與一個權重，例如 `"minecraft:stonebrick 3, minecraft:cobblestone 1"`。沒有權重的方塊算一份。每放置一個方塊，都會依世界種子碼與其位置從混合方案中擲定，因此同一個世界總是建出相同的圖案。接受混合方案的設定有 `villagePathVergeBlock`、`villagePathVergeWaterBlock`、`villagePathTunnelBlock`、`villagePathBridgeFrameBlock`、`villagePathBridgeFrameTopBlock`、`villageRailTunnelBlock`、`villageRailDeckBlock`、`villageRailSupportBlock`、`villageRailBarrierBlock`、`villageRailBridgeFrameBlock`、`villageRailBridgeFrameTopBlock`、`villageSubwayTunnelBlock`、`villageSubwayPlatformBlock`、`villageSubwayRailingBlock`、`villageSubwayBenchEndBlock` 和 `villageSewerMossBlock`。其他所有方塊設定都一律使用混合方案中的第一個方塊。

| 設定                         | 類型               | 預設                | 作用                                                                                                                                                                                                                                                                                                                                                                                                                         |
| ---------------------------- | ------------------ | ------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villagePathBlock`           | 方塊               | 空                  | 路面。留空則沿用該生態域會使用的方塊：沙地上用砂岩，惡地上用硬化黏土，泥土上用草徑                                                                                                                                                                                                                                                                                                                                           |
| `villagePathVergeBlock`      | 方塊               | 空                  | 村莊必須造地時，道路兩側以及地塊底下所填入的方塊。留空則順應地形，鋪上該生態域自身的填充方塊，並在原本會是泥土的地方於頂部鋪上草                                                                                                                                                                                                                                                                                             |
| `villagePathVergeWaterBlock` | 方塊               | `minecraft:planks`  | 該填充物位於水面上方時會變成什麼，這樣延伸到湖面上的路側就不會是一根泥土柱。它也用來裝飾留在水面上的石質門階                                                                                                                                                                                                                                                                                                                 |
| `villagePathCenterBlock`     | 方塊               | 空                  | 道路正中的中心線。留空則不畫                                                                                                                                                                                                                                                                                                                                                                                                 |
| `villagePathCenterDash`      | 數字               | `0`                 | 讓該線呈虛線：N 格線，接著 1 格路面。以世界座標為基準，因此一個道路片段的虛線會延續到下一個片段。`0` 保持實線                                                                                                                                                                                                                                                                                                                |
| `villagePathLineBlock`       | 方塊               | 空                  | 道路與人行道之間的邊線。留空則不畫                                                                                                                                                                                                                                                                                                                                                                                           |
| `villagePathSidewalkBlock`   | 方塊               | 空                  | 人行道，鋪在邊線之外，與路面齊平。留空則不鋪                                                                                                                                                                                                                                                                                                                                                                                 |
| `villagePathSidewalkWidth`   | 數字               | `2`                 | 設定 `villagePathSidewalkBlock` 之後，每條人行道的寬度                                                                                                                                                                                                                                                                                                                                                                       |
| `villagePathExtraWidth`      | 數字               | `0`                 | 在原版的 3 格之外，每側額外增加的道路格數。它會加寬道路片段本身，因此房屋會離寬闊的街道更遠                                                                                                                                                                                                                                                                                                                                  |
| `villagePathMinimumWidth`    | 數字               | `0`                 | 值得鋪設的最窄道路。無法容納完整裝飾的路段會降為光禿禿的 3 格寬小巷；窄於此寬度則完全不鋪，村莊會繞開它進行佈局。`0` 表示從不拒絕                                                                                                                                                                                                                                                                                            |
| `villagePathAlleyBlock`      | 方塊               | 空                  | 小巷的路面，小巷是太窄而無法容納線條與人行道的道路。小巷穿行於與其相交的街道的人行道之間，自身不帶人行道，且在小巷與街道相接處不畫行人穿越道。留空則以道路方塊鋪設小巷                                                                                                                                                                                                                                                       |
| `villagePathAlleyChance`     | 數字               | `0`                 | 一條道路被鋪成小巷而不是拓寬為完整街道的機率（百分比）。`0` 表示只在放不下完整街道的地方鋪小巷，實際上只會出現在擁擠的第一個街區。調高它會改變鋪設哪些道路，進而重塑整張街道圖；設為 50 時實測多出了七個分岔路口，因此調高後請檢查結果                                                                                                                                                                                       |
| `villagePathFlatRun`         | 數字               | `6`                 | 一條道路在升降一格之前保持同一高度的格數。以世界座標為基準，因此相鄰片段彼此一致。`0` 表示每格都升降，就像原版的坡道那樣                                                                                                                                                                                                                                                                                                     |
| `villagePathIntersects`      | 清單               | 無                  | 畫在路口上的設計，以資源包 `<namespace>/pathintersects/` 中的登錄鍵命名。只寫一項則所有路口畫得一樣；寫多項則依權重為每個路口挑選一項                                                                                                                                                                                                                                                                                        |
| `villagePathDeadEnds`        | 清單               | 無                  | 一條沒有長出迴轉圓環的斷頭路要如何封口，選項見下方。只寫一項則所有斷頭路封口方式相同；寫多項則依世界種子碼為每個末端擲定一項。留空則斷頭路保持敞開                                                                                                                                                                                                                                                                           |
| `villagePathLampBlock`       | 方塊或帶資料的方塊 | `minecraft:fence`   | 沿路路燈柱所用的方塊，堆疊在路緣上。留空則不立路燈柱                                                                                                                                                                                                                                                                                                                                                                         |
| `villagePathLampHeight`      | 數字               | `3`                 | 燈柱在燈頭之前有幾格高                                                                                                                                                                                                                                                                                                                                                                                                       |
| `villagePathLampTopBlock`    | 方塊或帶資料的方塊 | `minecraft:wool:15` | 燈柱頂端的燈頭。留空則保持光禿                                                                                                                                                                                                                                                                                                                                                                                               |
| `villagePathLampSideBlock`   | 方塊或帶資料的方塊 | `minecraft:torch`   | 掛在燈頭兩側、朝外的光源。留空則不掛                                                                                                                                                                                                                                                                                                                                                                                         |
| `villagePathLampStructure`   | 文字               | 空                  | 整盞燈改用結構檔案放置，取代堆疊三個燈方塊，寫成 `mypack:street_lamp`，並從該資源包的 `structures` 資料夾讀取。它以燈的位置為中心，最底層位於路緣上，其放置的方塊會被固定，不會被其他內容覆寫。留空則堆疊方塊                                                                                                                                                                                                                |
| `villageWellStructure`       | 清單               | 無                  | 作為廣場中心裝飾放置的結構檔案，取代水井，寫成加權的 `name=weight` 項目，如 `mypack:plaza_spire=3`，從該資源包的 `structures` 資料夾讀取，並依每口井的位置擲定一次，因此同一口井總是得到同一個結構。`empty=weight` 項目表示保留水井所占的份額。所選結構以水井 6×6 的佔地為中心，最底層位於廣場地面上，其下方的地面會被鋪平，並且它放置的方塊會被固定，使廣場裝飾不會動到它們。較寬的結構會向廣場環帶擴展。沒有項目則建造水井 |

道路由中間向外裝飾：先是中心線，接著是路面，再是邊線，最後是人行道。放不下的寬度會退而求其次，而不是溢出，因此狹窄的路段會先悄悄失去人行道，然後才失去路面。

`villagePathBlock` 及其同類設定優先於 `villageBlocks`。被指定的道路方塊會原樣使用，而對應表只會改動道路本來會自行選擇的內容。把它們留空，由對應表決定，資源包就能藉此保留與生態域相符的路面，同時又為其重新上色。

**燈方塊帶有資料。** 三個燈方塊接受單純的名稱、帶中繼資料的名稱，或在大括號中帶方塊實體資料的名稱，例如 `minecraft:skull:1{SkullType:3}`。大括號中的內容會以 NBT 讀取，並在方塊放置之後套用到方塊實體上，其他模組的燈就是藉此保留它所需的設定。錯誤的 NBT 會被回報並忽略，而不會妨礙燈的建造。

**斷頭路。** 一條沒有長出迴轉圓環的斷頭路，會由 `villagePathDeadEnds` 封口，依世界種子碼為每個末端擲定一種樣式。未設定其方塊的樣式會退出擲定，因此在 `villagePathBridgeBarrierBlock` 指定方塊之前，`barrier` 不會封住任何東西，而小巷的末端永遠不會採用 `sidewalk`。

| 值         | 作用                                                              |
| ---------- | ----------------------------------------------------------------- |
| `sidewalk` | 以人行道方塊鋪滿末端那一列                                        |
| `barrier`  | 沿末端那一列立起護欄方塊，高度為 `villagePathBridgeBarrierHeight` |

**路口設計。** `villagePathIntersects` 指定資源包附帶的檔案，每個檔案都是一幅小圖，描繪兩條道路相交處要畫什麼，以單一字元組成的列來繪製，一個字元對應一個方塊。

`<namespace>/pathintersects/*.json`

檔案的路徑就是該設計的登錄鍵，`villagePathIntersects` 隨後用它來指定設計。

```json
{
  "name": "Crosswalk",
  "weight": 3,
  "legend": { "w": "minecraft:quartz_block", "y": "minecraft:wool@4" },
  "mouth": ["wwww", "....", "wwww"],
  "corner": ["yy.", "y..", "..."]
}
```

| 鍵       | 值                     | 預設     | 作用                                                                                                                                   |
| -------- | ---------------------- | -------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| `name`   | 字串                   | 檔案名稱 | 日誌中使用的名稱                                                                                                                       |
| `weight` | 整數，1 以上           | `1`      | 列出多個設計時，該設計在路口中所占的份額                                                                                               |
| `legend` | 單一字元對應方塊的物件 | 無       | 各列中除下列角色字元之外還可使用的字元。已經是角色字元的字元會被拒絕，並記入一行日誌                                                   |
| `mouth`  | 字串清單               | 無       | 畫在每個入口上的列，位於相交道路之外。第一列最靠近路口，其餘的向外延伸。字元沿道路橫向排列，若某列比道路寬度短則重複                   |
| `corner` | 字串清單               | 無       | 畫在路口內部的列。第一列最靠近相交道路的邊緣，而在一列之中，第一個字元最靠近道路自身的邊緣，向內依序排列。圖案沒有涵蓋到的格子保持不變 |

有五個字元是角色而不是方塊，因此它們會跟隨道路已有的裝飾：`r` 是路面，`l` 是邊線，`s` 是人行道，`.` 讓方塊完全維持原樣，`c` 為保留字元，畫成路面。資源包從未設定其方塊的角色會退回為路面，而任何其他字元都會在 `legend` 中查找，同樣退回為路面。

路口採用哪個設計，由世界種子碼和路口自身的位置算出，因此同一個世界總是畫出相同的路口。設計只在三條或更多街道相交處繪製，即路口或水井廣場；兩條街道相交只會呈現為一個普通的彎角。

#### 村莊橋梁與碼頭

*村莊*

| 設定                             | 類型 | 預設                                       | 作用                                                                                                                                                                   |
| -------------------------------- | ---- | ------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villagePathSupportBlock`        | 方塊 | 空                                         | 地面為裸露岩石處的路面本身，以及水上道路底下的橋墩和橋腳。留空則沿用原版的礫石，沙漠村莊則用砂岩                                                                       |
| `villagePathBridgeBlock`         | 方塊 | 空                                         | 道路跨越水面所用的方塊。留空則沿用原版的木板                                                                                                                           |
| `villagePathBridgeBarrierBlock`  | 方塊 | 空                                         | 沿橋面兩側邊緣堆疊的護欄。留空則不建                                                                                                                                   |
| `villagePathBridgeBarrierHeight` | 數字 | `1`                                        | 這些護欄有幾格高                                                                                                                                                       |
| `villagePathBridgeSidewalkBlock` | 方塊 | 空                                         | 道路跨水處人行道的橋面鋪裝。留空則沿用一般人行道方塊過橋                                                                                                               |
| `villagePathBridgeDrop`          | 數字 | `0`                                        | 道路的坡度必須高出地面多少格，其下的落差才會架橋而不是填實。`0` 讓道路留在地面上：它們只在水上架橋，別處不架。`3` 是鐵路棧橋所遵循的規則。這會改變坡度，而不僅僅是裝飾 |
| `villagePathBridgeFrameBlock`    | 方塊 | 空                                         | 長橋上方的框架：橋面兩側各立一根柱子，頂部橫一根梁。每個框架都有一根橋墩一路通到橋面下的地面，且在它所在的那一列不會立路燈柱。留空則不建                               |
| `villagePathBridgeFrameTopBlock` | 方塊 | 空                                         | 該框架頂部的橫梁。留空則使用 `villagePathBridgeFrameBlock`                                                                                                             |
| `villagePathBridgeFrameHeight`   | 數字 | `4`                                        | 框架在橋面上方留出多少格淨空，橫梁位於其上一格                                                                                                                         |
| `villagePathBridgeFrameRun`      | 數字 | `24`                                       | 當橋長到足以容納多個框架時，相鄰框架相隔多少列                                                                                                                         |
| `villagePathBridgeFrameLeast`    | 數字 | `24`                                       | 能得到框架的最短橋段。更短的橋保持樸素                                                                                                                                 |
| `villagePathPiers`               | 清單 | 無                                         | 在水上終止的道路所用的碼頭樣式，選項見下方。架橋的末段會變成碼頭；寫多項則為每個碼頭擲定一種樣式。留空則這樣的橋仍是普通的橋                                           |
| `villagePathPierCargo`           | 清單 | 無                                         | 擺放在碼頭欄杆內側的貨物，寫成加權項目，選項見下方。每個碼頭每隔一列就在兩側各擲定一次該清單，因此權重決定碼頭顯得多擁擠。留空則碼頭上空無一物                         |
| `villagePathPierLoot`            | 文字 | `resourcedatapackloader:chests/pier_cargo` | 帶物品欄的貨物所填入的戰利品表，在第一次被開啟時擲定。留空則這類貨物是空的                                                                                             |

**平整的橋面。** 每座橋從頭到尾都位於同一高度，不論兩岸高低如何；兩側的道路會以坡道與之銜接。

**乾地落差。** 道路會把低窪處填實，只在水上架橋，除非 `villagePathBridgeDrop` 指定了高度：坡度高出地面超過該格數的那一列，會改為在橋腳上鋪設橋面，就像鐵路棧橋跨越溝壑那樣。它改變的是坡度而不是裝飾，因此啟用它所佈局的村莊，與未啟用的不一樣。

**橋上框架。** 一段至少有 `villagePathBridgeFrameLeast` 列的架橋路段，在 `villagePathBridgeFrameBlock` 指定了方塊之後，會在橋面上方設置框架：每側一根柱子，頂部一根橫梁，其下有 `villagePathBridgeFrameHeight` 格淨空。長橋上會立多個框架，相隔 `villagePathBridgeFrameRun` 列，並以該路段的中點為中心對稱分布，因此同一座橋總是帶著同樣的框架。另一條道路穿過該橋的那一列保持敞開，而碼頭完全不帶框架，突堤不算橋。

**碼頭。** 一條延伸到水面上並止於空處的道路，在 `villagePathPiers` 至少指定一種樣式之後，會成為碼頭，而不是通向虛無的橋。寫多項則依世界種子碼和碼頭的末端為每個碼頭擲定一種樣式，因此同一個世界總是建出相同的碼頭。不論樣式為何，每個碼頭都立在以支撐方塊製成的樁上，樁每隔四列打到橋面兩側邊緣下方的河床。橋面是橋方塊，欄杆和立柱是護欄方塊，樁是支撐方塊。

| 值          | 作用                                                         |
| ----------- | ------------------------------------------------------------ |
| `railed`    | 保留完整的橋面，樸素無線條也無人行道帶，並以護欄方塊封住遠端 |
| `pilings`   | 每隔四列把兩側的護欄改為立柱，立在同樣的支撐之上             |
| `boardwalk` | 把橋面收窄到道路的核心寬度                                   |

**碼頭貨物。** `villagePathPierCargo` 在碼頭上擺放貨物。每隔一列在兩側各擲定一次該清單，位置在欄杆內側一欄，這樣橋面中央留出可供行走的空地，從不擠占帶欄杆的末端列，也不會讓兩件貨物並排擺放，因為兩個相鄰的箱子會合併成一個大型箱子。只有一疊中的每個方塊都放得下時才會擺放，而在不同高度寫兩次同一個方塊，就是讓碼頭出現大小混雜的貨堆的辦法。

| 值                          | 作用                                   |
| --------------------------- | -------------------------------------- |
| `<block>=<weight>`          | 一個方塊及其所占位置的份額，擺放一格高 |
| `<block>=<weight>,<height>` | 同上，但堆疊指定的格數，範圍 1 到 8    |
| `empty=<weight>`            | 橋面上保持空著的份額                   |

帶有戰利品物品欄的方塊，包括箱子，會從 `villagePathPierLoot` 填入，在玩家第一次開啟時擲定，方式與原版箱子相同。內建的戰利品表是容易取得的海上打撈物。資源包可以在 `resourcedatapackloader` 命名空間下附帶自己的 `loot_tables/chests/pier_cargo.json` 來取代它，也可以指定自己的戰利品表。

#### 村莊隧道

*村莊*

| 設定                          | 類型 | 預設 | 作用                                                                                                                                                                                                                   |
| ----------------------------- | ---- | ---- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villagePathTunnelBlock`      | 方塊 | 空   | 道路穿過山丘時用來襯砌的方塊，而不是把山丘切開：鑿孔兩側的牆壁及其上方的頂板。留空則不鑿隧道，道路像以前一樣切開山丘                                                                                                   |
| `villagePathTunnelDepth`      | 數字 | `10` | 路面之上必須有多少地面，一段路才會被鑿穿而不是切開。埋得這麼深的隆起如果連續十二列或更多，就會維持水平並鑿穿，其較淺的引道則被切開；更短的土包像以前一樣被切開。只有在 `villagePathTunnelBlock` 指定了方塊之後才會計入 |
| `villagePathTunnelLightBlock` | 方塊 | 空   | 沿隧道頂板中心線嵌入的光源。留空則不設燈                                                                                                                                                                               |
| `villagePathTunnelLightRun`   | 數字 | `8`  | 這些燈之間相隔多少格。以世界座標為基準，因此一個道路片段的燈會延續到下一個片段；太短而碰不到這些位置的隧道，會在正中間點一盞燈                                                                                         |

**隧道。** 沒有隧道方塊時，遇到山丘的道路會爬上去，每列最多升一格，並且在短隆起上最深只切入兩格。一旦 `villagePathTunnelBlock` 指定了方塊，高出道路 `villagePathTunnelDepth` 或更多且至少持續十二列的隆起就會被鑿穿：道路在整個隆起處維持較高一側的高度，每一列只要其上有那麼多地面，就會被鑿出四格高的孔洞，牆壁和頂板使用襯砌方塊，而洞口之前較淺的幾列則作為引道被切開。遇到的是一面看不到另一側的山壁，而不是能望過去的山丘時，道路同樣不會爬升：它維持抵達時的高度並尋找山的另一側，最遠搜尋到該片段原本終點之外 98 列。若在這個範圍內找到，且其間的地面沒有其他片段，該片段就會被延長，從對側洞口出來，因此隧道總是能穿通。若找不到，道路就停在山腳，永不進入山中。整條街道一併穿行而過，車道、線條和人行道皆然，並由 `villagePathTunnelLightBlock` 每隔 `villagePathTunnelLightRun` 格從頂板照明，而路燈柱與路側裝飾則止於洞口。路口永遠不會被鑿穿，因此相交的街道總是在露天與這條路相接。道路將要鑿穿的路段旁不安置任何地塊，也沒有街道從中分出，因此房屋永遠不會面向隧道，也不會有路口切入隧道；找不到其他空間安置地塊的街區，會在那裡少鋪幾條街道。

#### 村莊下水道

*村莊*

| 設定                       | 類型     | 預設              | 作用                                                                                                                                                                                                                                           |
| -------------------------- | -------- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villageSewerBlock`        | 方塊名稱 | 無                | 村莊街道與小巷底下的下水道所用的襯砌方塊：地板、兩側牆壁與頂板。留空則不挖下水道                                                                                                                                                               |
| `villageSewerDepth`        | 數字     | `8`               | 下水道地板位於街道自身路面之下多深。下水道跟隨其所在的街道，因此上坡的街道底下是上坡的下水道                                                                                                                                                   |
| `villageSewerHeight`       | 數字     | `3`               | 步道上方有幾格淨空                                                                                                                                                                                                                             |
| `villageSewerWidth`        | 數字     | `5`               | 下水道橫向的寬度，含兩側的牆壁在內。偶數會被向上進位，讓水渠保持在正中                                                                                                                                                                         |
| `villageSewerWaterBlock`   | 方塊名稱 | `minecraft:water` | 填滿中間水渠的方塊。留空則水渠是乾的                                                                                                                                                                                                           |
| `villageSewerWalkBlock`    | 方塊名稱 | 無                | 水渠兩側步道的鋪面。留空則踩在襯砌方塊上                                                                                                                                                                                                       |
| `villageSewerLightBlock`   | 方塊名稱 | 無                | 嵌入水渠上方頂板作為光源的方塊。留空則不設燈                                                                                                                                                                                                   |
| `villageSewerLightRun`     | 數字     | `8`               | 這些燈之間相隔多少格。以世界座標為基準，因此一個道路片段的燈會延續到下一個片段                                                                                                                                                                 |
| `villageSewerLadderBlock`  | 方塊名稱 | 無                | 用來攀爬人孔豎井的方塊，沿豎井從街道設置到下水道步道。留空則豎井保持敞開                                                                                                                                                                       |
| `villageSewerCoverBlock`   | 方塊名稱 | 無                | 蓋住人孔的方塊，與路面齊平地嵌在東西向街道上，位於每條街道或小巷與它相接之處，以及該街道穿過下水道環路的廣場上。常見的選擇是木製地板門：鐵製的會接收紅石訊號，玩家無法徒手開啟，等於把下水道對他們封閉。留空則井口保持敞開                     |
| `villageSewerMossBlock`    | 方塊名稱 | 無                | 零星混入襯砌的第二種方塊，例如在普通石頭中混入長苔的石頭。留空則整條下水道只用一種方塊襯砌                                                                                                                                                     |
| `villageSewerMossChance`   | 0 到 100 | `25`              | 襯砌方塊中有百分之幾會變成第二種方塊。依方塊位置從世界種子碼擲定，因此同一條下水道總是呈現相同的樣子                                                                                                                                           |
| `villageSewerVineBlock`    | 方塊名稱 | 無                | 零星掛在下水道牆壁內側的方塊，例如藤蔓。它會攀附其所靠的那面牆。留空則什麼也不掛                                                                                                                                                               |
| `villageSewerVineChance`   | 0 到 100 | `20`              | 牆邊的格子中有百分之幾帶有它。依方塊位置從世界種子碼擲定，因此同一條下水道總是掛著相同的東西                                                                                                                                                   |
| `villageSewerWellEntrance` | 布林值   | `true`            | 在水井周圍的廣場環帶底下設一圈下水道環路，每條街道的下水道都穿過它，並在每條東西向街道穿過它的兩側，於廣場上設一個通往環路的人孔，使這些下水道成為一個相互連通的系統，並在鎮中心留有入口。關閉時，每條街道的下水道止於水井，廣場沒有下去的通道 |

**下水道。** 指定 `villageSewerBlock` 後，會在每條街道與小巷底下、該街道自身路面之下 `villageSewerDepth` 格處挖一條下水道。它不是自成一體的網路：它跟隨道路，因此街道通往哪裡，下水道就通往哪裡，街道轉彎它也轉彎，街道爬升它也爬升，兩條下水道會在十字路口底下相遇，因為其上方的街道相遇；街道或小巷止於另一條道路處，它的下水道會繼續穿到那條路底下與另一條相接。迴轉圓環的圓端和架在橋上的那一列都不帶下水道。其剖面是一層襯砌的地板，中間是一條填入 `villageSewerWaterBlock` 的水渠，兩側是鋪有 `villageSewerWalkBlock` 的步道，上方有 `villageSewerHeight` 格淨空和一層襯砌的頂板，橫向寬 `villageSewerWidth`，含兩側的牆壁。`villageSewerLightBlock` 每隔 `villageSewerLightRun` 格在水渠上方的頂板中嵌入一盞燈。下水道升高時絕不會高到干擾其上方的街道，而道路與世界底部之間沒有空間的路段會被略過，而不是硬擠進去。

#### 村莊鐵路

*村莊*

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

鐵路線是一段筆直的軌道，沿單一軸線橫貫整個村莊，並在兩端越過最後一個片段繼續延伸。它在第一條街道之前鋪設，因此城鎮會圍繞它生長：線路上不會有房屋，街道只能筆直穿過它，也沒有任何東西從它分出。和道路一樣，它需要 `terrainAdaptation`。`villageRailLines` 預設為 `0`，即不鋪設，村莊維持原樣。

| 設定                             | 類型                 | 預設   | 作用                                                                                                                                                                                                                            |
| -------------------------------- | -------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villageRailLines`               | 數字                 | `0`    | 每座村莊有多少條線路穿過。`0` 表示不鋪                                                                                                                                                                                          |
| `villageRailSpacing`             | 數字                 | `48`   | 同一座村莊中，一條線路的路基與下一條之間至少相隔多少格空地。`1` 讓它們僅隔一格，資源包就是這樣建出平行線路的車場的                                                                                                              |
| `villageRailDirection`           | 文字                 | `any`  | 線路的走向：`ew` 為東西向，`ns` 為南北向，`any` 為每座村莊隨機擲定。`e`、`w`、`n` 和 `s` 的含義與之相同                                                                                                                         |
| `villageRailWidth`               | 數字                 | `3`    | 路基的最小寬度。`3` 在正中承載一條軌道，`5` 承載兩條；要求的軌道數超出該寬度所能容納時，路基會加寬以容納它們                                                                                                                    |
| `villageRailTracks`              | 數字                 | `0`    | 同一個路基承載多少條軌道，並排鋪設，彼此相隔 `villageRailTrackGap`。**路基會加寬以容納全部軌道**，所以三條軌道共用一個路基，而不會變成三條線路。`0` 表示寬度不足五格的路基鋪一條軌道，更寬的鋪兩條                              |
| `villageRailTrackGap`            | 數字                 | `2`    | 路基上的軌道彼此相隔多少格，以中心到中心計。允許的最小值 `2` 會在它們之間留一格路基，這樣可以避免它們像相鄰的鐵軌那樣互相彎向對方                                                                                               |
| `villageRailBlock`               | 方塊                 | 空     | 軌道。留空則鋪設原版鐵軌，礦車可在其上行駛；任何其他方塊都會原樣鋪設                                                                                                                                                            |
| `villageRailTrackSeat`           | `auto`、`on` 或 `in` | `auto` | 軌道的位置。`auto` 把鐵軌方塊放在路基上，而把任何其他方塊與路基表面齊平地嵌入；`on` 一律鋪在路基上；`in` 一律嵌入路基。資源包若要用鐵塊或半磚做出鐵軌的樣子，而不是礦車鐵軌，就把軌道嵌入路基，這樣平交道會與路面齊平地穿過路面 |
| `villageRailBedBlock`            | 方塊                 | 空     | 軌道底下的路基。留空則鋪礫石                                                                                                                                                                                                    |
| `villageRailTieBlock`            | 方塊                 | 空     | 每隔 `villageRailTieRun` 列橫鋪在路基上的枕木。留空則鋪木板                                                                                                                                                                     |
| `villageRailTieRun`              | 數字                 | `2`    | 枕木相隔多少列                                                                                                                                                                                                                  |
| `villageRailShoulderBlock`       | 方塊                 | 空     | 裝飾路基最外側的幾欄，是軌道旁的維護小路，相當於鐵路版的道路人行道。留空則不鋪                                                                                                                                                  |
| `villageRailShoulderWidth`       | 數字                 | `1`    | 該路肩每側有幾欄寬，加在 `villageRailWidth` 之外                                                                                                                                                                                |
| `villageRailPowerBlock`          | 方塊                 | 空     | 每隔 `villageRailPowerRun` 列嵌入線路的動力軌道。留空則使用原版動力鐵軌；不是鐵軌的方塊則直接鋪在那裡                                                                                                                           |
| `villageRailPowerBase`           | 方塊                 | 空     | 動力軌道底下用來供能的方塊。留空則使用紅石方塊                                                                                                                                                                                  |
| `villageRailPowerRun`            | 數字                 | `0`    | 每隔這麼多列，在原版鐵軌軌道中嵌入一根位於紅石方塊上的動力鐵軌，讓礦車持續行駛。`0` 表示不設動力軌道，且任何非原版鐵軌的軌道都會忽略它                                                                                          |
| `villageRailSupportBlock`        | 方塊                 | 空     | 棧橋底下的立柱。留空則使用原木                                                                                                                                                                                                  |
| `villageRailDeckBlock`           | 方塊                 | 空     | 棧橋承載路基的橋面。留空則使用木板                                                                                                                                                                                              |
| `villageRailBarrierBlock`        | 方塊                 | 空     | 沿棧橋橋面兩側邊緣的護欄。留空則不立                                                                                                                                                                                            |
| `villageRailBridgeFrameBlock`    | 方塊                 | 空     | 長棧橋上方的框架：橋面兩側各立一根柱子，頂部橫一根梁。每個帶框架的列，其支撐柱也一路通到路基。留空則不建                                                                                                                        |
| `villageRailBridgeFrameTopBlock` | 方塊                 | 空     | 該框架頂部的橫梁。留空則使用 `villageRailBridgeFrameBlock`                                                                                                                                                                      |
| `villageRailBridgeFrameHeight`   | 數字                 | `4`    | 框架在橋面上方留出多少格淨空，橫梁位於其上一格                                                                                                                                                                                  |
| `villageRailBridgeFrameRun`      | 數字                 | `24`   | 當棧橋長到足以容納多個框架時，相鄰框架相隔多少列                                                                                                                                                                                |
| `villageRailBridgeFrameLeast`    | 數字                 | `24`   | 能得到框架的最短棧橋。更短的棧橋保持樸素                                                                                                                                                                                        |
| `villageRailTunnelBlock`         | 方塊                 | 空     | 線路穿過山丘時襯砌牆壁與頂板的方塊。留空則不鑿隧道，所有山丘都被切開                                                                                                                                                            |
| `villageRailTunnelDepth`         | 數字                 | `6`    | 路基之上必須有多少地面，一段路才會被鑿穿而不是切開。需要 `villageRailTunnelBlock`                                                                                                                                               |
| `villageRailTunnelLightBlock`    | 方塊                 | 空     | 沿鐵路隧道頂板中心線嵌入的光源。留空則不設燈                                                                                                                                                                                    |
| `villageRailTunnelLightRun`      | 數字                 | `8`    | 這些隧道燈之間相隔多少格，以世界座標為基準，使各片段彼此一致                                                                                                                                                                    |
| `villageRailClimb`               | 數字                 | `8`    | 線路每升降一格，要先水平延伸多少列。`1` 表示與道路一樣陡                                                                                                                                                                        |
| `villageRailTail`                | 數字                 | `48`   | 線路在兩端越過村莊最後一個片段後繼續延伸多遠                                                                                                                                                                                    |

**線路走向。** 各線路平行，沿 `villageRailDirection` 指定的軸線延伸，並依次從水井廣場向外隔開，先一側再另一側，每條線路的路基與下一條的路基之間至少保留 `villageRailSpacing` 格地面。線路絕不穿過廣場或地塊：它在第一條街道之前鋪設，因此村莊的每條街道與每棟房屋都圍繞它安置，待村莊佈局完成後，它會被修剪為已生長的村莊加上兩端各 `villageRailTail` 的長度。

**坡度。** 鐵路不像道路那樣爬升。它的路基跟隨經過長距離平滑後的地面，最多每隔 `villageRailClimb` 列變化一格高度。地面下陷超過三格的地方，線路改走棧橋，在每四列一根的 `villageRailSupportBlock` 立柱上鋪 `villageRailDeckBlock`，不論是跨水還是跨溝壑都一樣。地面升高的地方，線路會被切開，或者在路基之上的地面達到 `villageRailTunnelDepth` 深且持續十二列或更多時，以 `villageRailTunnelBlock` 鑿穿。整條線路沿線在路基之上保留四格淨空。棧橋從頭到尾位於同一高度，其兩側的路基以坡道與該高度銜接；當維持棧橋水平與爬升速率互相衝突時，以水平為準，其旁的坡道可能比 `villageRailClimb` 所說的更早升降。一段至少有 `villageRailBridgeFrameLeast` 列的棧橋，在 `villageRailBridgeFrameBlock` 指定了方塊之後會設置橋上框架，相隔 `villageRailBridgeFrameRun` 列，並以棧橋中點為中心對稱分布，每個帶框架的列，其支撐柱也隨之一路通到路基。道路穿過線路的那一列保持敞開。

**交叉口。** 街道筆直地穿過線路，並在路基兩側各延伸出七格或更多。將止於線路上或這七格之內的街道，在坡度允許時會繼續穿越過去，否則就在距路基七格處停下；將起始於線路上或沿著線路延伸的街道則被拒絕。在交叉口，是街道遷就線路的坡度，而不是反過來，並在兩側以其自身可行走的坡度以坡道升降到該高度。路面保持原樣，軌道在其上高出一格穿過，因此礦車可以穿過道路，村民也可以穿過軌道。鑿穿山丘的線路根本不會被穿越：街道從隧道上方經過。

**門階。** 在 `terrainAdaptation` 開啟時，村莊建築不會在自身範圍之外放置樓梯方塊：原版在門前放置的門階樓梯會被省去，因為道路臨街面和地塊前的鋪面自會把地面延伸到門口。

**軌道。** `villageRailBlock` 為空時，軌道是沿線路方向轉向的原版鐵軌，而 `villageRailPowerRun` 每隔若干列就在紅石方塊上設一根動力鐵軌，讓礦車能行駛整條線路。資源包若想用鐵塊、鐵柵欄或其他任何東西，只需指定它們，線路就會原樣以該方塊來裝飾。

#### 村莊地鐵

*村莊*

| 設定                            | 類型 | 預設   | 作用                                                                                                                                     |
| ------------------------------- | ---- | ------ | ---------------------------------------------------------------------------------------------------------------------------------------- |
| `villageSubwayLines`            | 數字 | `0`    | 一座村莊挖多少條地下鐵路線。0 表示不挖，也不進行任何擲定，因此村莊的佈局與沒有地鐵時完全相同                                             |
| `villageSubwayDepth`            | 數字 | `24`   | 路基位於地表之下多深。線路的坡度依據其上方的地面來定，因此它在該深度跟隨地勢起伏，而不是水平延伸                                         |
| `villageSubwaySpacing`          | 數字 | `64`   | 一座村莊的各條地鐵線路彼此保持多遠的間距                                                                                                 |
| `villageSubwayDirection`        | 字串 | `any`  | 地鐵線路的走向：`ew` 為東西向，`ns` 為南北向，或 `any` 為每座村莊隨機擲定                                                                |
| `villageSubwayWidth`            | 數字 | `3`    | 路基有多寬，不含路肩                                                                                                                     |
| `villageSubwayTracks`           | 數字 | `0`    | 路基承載多少條平行軌道。0 表示寬度允許多少就鋪多少                                                                                       |
| `villageSubwayTrackGap`         | 數字 | `2`    | 平行軌道彼此相隔多遠                                                                                                                     |
| `villageSubwayBlock`            | 方塊 | 空     | 軌道方塊。留空則鋪設原版鐵軌                                                                                                             |
| `villageSubwayTrackSeat`        | 字串 | `auto` | 軌道是放在路基上、嵌入路基，還是以 `auto` 讓方塊自行決定                                                                                 |
| `villageSubwayBedBlock`         | 方塊 | 空     | 構成路基的方塊。留空則使用礫石                                                                                                           |
| `villageSubwayTieBlock`         | 方塊 | 空     | 橫鋪在路基上作為枕木的方塊。留空則使用木板                                                                                               |
| `villageSubwayTieRun`           | 數字 | `2`    | 枕木相隔多少格                                                                                                                           |
| `villageSubwayShoulderBlock`    | 方塊 | 空     | 路基兩側的方塊。留空則不設路肩                                                                                                           |
| `villageSubwayShoulderWidth`    | 數字 | `1`    | 該路肩有多寬                                                                                                                             |
| `villageSubwayPowerBlock`       | 方塊 | 空     | 動力軌道方塊。留空則使用原版動力鐵軌                                                                                                     |
| `villageSubwayPowerBase`        | 方塊 | 空     | 設在動力軌道底下用來驅動它的方塊。留空則使用紅石方塊                                                                                     |
| `villageSubwayPowerRun`         | 數字 | `0`    | 動力軌道相隔多少格。0 表示不設置                                                                                                         |
| `villageSubwayTunnelBlock`      | 方塊 | 空     | 襯砌鑿孔的方塊：兩側的牆壁及其上方的頂板。留空則鑿孔及其車站都不加襯砌                                                                   |
| `villageSubwayTunnelLightBlock` | 方塊 | 空     | 嵌入隧道頂板作為光源的方塊。留空則不設燈                                                                                                 |
| `villageSubwayTunnelLightRun`   | 數字 | `8`    | 這些燈之間相隔多少格，以世界座標為基準，使各片段彼此一致                                                                                 |
| `villageSubwayClimb`            | 數字 | `8`    | 一條線路要延伸多少格，才可以升降一格                                                                                                     |
| `villageSubwayTail`             | 數字 | `48`   | 線路在越過村莊自身的片段之後，延伸多遠才停止                                                                                             |
| `villageSubwaySurfaces`         | 數字 | `25`   | 一條地鐵線路在一端爬升到地表，並從那裡作為普通鐵路繼續延伸的機率（百分比），身後是隧道，前方是露天軌道。`0` 讓每條地鐵在全長上都埋在地下 |

**爬出地面。** `villageSubwaySurfaces` 是一條線路不再從頭到尾埋在地下，而是在一端爬升到地表，並從那裡作為普通鐵路繼續延伸的機率（百分比），身後是隧道，前方是露天軌道。爬升遵循 `villageSubwayClimb`，即每經過那麼多列升一格，因此深度為 `villageSubwayDepth` 的線路，僅坡道就要花去深度乘以爬升率那麼多列，並且在其後還需要相當長的一段距離才名副其實；兩者都沒有空間的線路就只會留在地下。在帶有車站的短線路上，調高 `villageSubwayClimb` 才能為兩者騰出空間。

#### 地鐵站

*村莊*

| 設定                         | 類型 | 預設                   | 作用                                                                                                                                                                                                                                                                                                   |
| ---------------------------- | ---- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `villageSubwayStationLength` | 數字 | `0`                    | 車站大廳有多少格長，以線路最靠近水井的那一列為中心。0 表示完全不建車站                                                                                                                                                                                                                                 |
| `villageSubwayStationRun`    | 數字 | `0`                    | 沿線路在水井處的車站之後，其餘車站相隔多少格。每個車站都會沿線稍微滑動，尋找能容納它的地面，找不到的地方就不建。0 表示只建那一個                                                                                                                                                                       |
| `villageSubwayStationRepeat` | 數字 | `12`                   | 車站建築中有多少層重複，使一份建築適用於任何深度：豎井以這一段的整份副本增長，而走廊吸收多出的部分。它必須是樓梯的整數圈，否則各段樓梯無法銜接。`0` 表示從不擴展建築                                                                                                                                   |
| `villageSubwayStationFoot`   | 數字 | `4`                    | 車站建築底部有多少層只鋪設一次，位於重複部分之前。地板和通向月台的門洞就在這裡                                                                                                                                                                                                                         |
| `villageSubwayPlatformWidth` | 數字 | `3`                    | 大廳在路基兩側各向外拓寬多遠以構成月台                                                                                                                                                                                                                                                                 |
| `villageSubwayPlatformBlock` | 方塊 | 空                     | 月台地板所用的方塊。留空則用隧道襯砌鋪地板                                                                                                                                                                                                                                                             |
| `villageSubwayStation`       | 文字 | 空                     | 每個車站所依據的結構檔案，寫成 `mypack:subway_station`，並從該資源包的 `structures` 資料夾讀取。其方塊依建造時的樣子鋪設，以海綿代表隧道襯砌，其空氣格會被挖空，因此地下所立的就是這份建築本身，而不是對它的描述。只有當此項指定了能載入的建築時，線路才會有車站：留空，或名稱無法載入，則完全不建車站 |
| `villageSubwayRailingBlock`  | 方塊 | `minecraft:iron_bars`  | 圍在車站樓梯頂部、與街道相通處的方塊，避免有人走進井裡。留空則樓梯頂部不設欄杆                                                                                                                                                                                                                         |
| `villageSubwayBenchBlock`    | 方塊 | `minecraft:oak_stairs` | 設在車站月台上以及樓梯頂部旁的長椅座位。樓梯方塊會被轉向背離線路，看起來就是長椅；任何方塊都可以。留空則不設長椅                                                                                                                                                                                       |
| `villageSubwayBenchEndBlock` | 方塊 | `minecraft:log`        | 車站長椅兩端的扶手。留空則座位兩端光禿                                                                                                                                                                                                                                                                 |
| `villageSubwayBenchLength`   | 數字 | `5`                    | 車站長椅有多長，含扶手在內。`0` 表示不設長椅                                                                                                                                                                                                                                                           |

**車站。** 設定了 `villageSubwayStationLength` 且 `villageSubwayStation` 指定了能載入的建築之後，地鐵線路會在最靠近水井之處得到一個車站，並沿線每隔 `villageSubwayStationRun` 格再有一個。其中每一個都會向兩側滑動幾格，尋找地面能容納的位置，避開已被占用的車站，附近找不到可行位置時就直接不建，因此線路上永遠不會有沒有出入口的大廳。大廳是向兩側各拓寬 `villageSubwayPlatformWidth` 的路基，地板鋪 `villageSubwayPlatformBlock`，牆壁和頂板採用隧道襯砌，並由隧道自身的 `villageSubwayTunnelLightBlock` 和 `villageSubwayTunnelLightRun` 照明。從月台出發，一條走廊通向一個樓梯井，樓梯井爬升到道路旁邊的街道上，絕不在道路底下，也絕不穿過水井廣場或房屋；爬升過長而無法直行時，走廊會先沿大廳折返。街道層的樓梯頂部圍著一圈 `villageSubwayRailingBlock` 欄杆，靠近的一端留作入口敞開，而月台上以及樓梯頂部旁各有一張長椅，座位為 `villageSubwayBenchBlock`，扶手為 `villageSubwayBenchEndBlock`，長度為 `villageSubwayBenchLength`。

**手工建造車站。** `villageSubwayStation` 指定每個車站所依據的結構檔案，沒有它就不建任何車站。資源包藉此附帶某人親手建造的造型，而不是靠設定描述出來的造型。在世界中把它建好，用任意方塊標記該結構，匯出，然後隨資源包一起放置：其方塊依建造時的樣子鋪設，以海綿代表隧道襯砌，其空氣格會被挖空。一份建築可適用於任何深度，因為它的中間部分會重複：`villageSubwayStationFoot` 層在底部只鋪設一次，包含地板和通向月台的門洞，然後把接下來的 `villageSubwayStationRepeat` 層整份堆疊上去，直到建築抵達街道。該段必須是樓梯的整數圈，否則兩份副本銜接處的各段樓梯無法對接。建築自帶通往街道的出口。

#### 鐵路連結

*村莊*

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

鐵路連結把相鄰的村莊連成一個網路。村莊在村莊網格（`structureSpacing`）中每個單元格建立一座，而連結沿著兩個單元格之間的接縫鋪設：每座村莊的第一條線路越過其尾端繼續延伸成為支線，筆直地通向接縫，並與沿接縫鋪設的幹線成直角相接。幹線從一條支線延伸到另一條，絕不越過任何一端。它需要 `villageRailLines`，在沒有地面線路的資源包中則需要 `villageSubwayLines`，並且預設關閉。

| 設定                           | 類型       | 預設    | 作用                                                                                                         |
| ------------------------------ | ---------- | ------- | ------------------------------------------------------------------------------------------------------------ |
| `villageRailLinks`             | true/false | `false` | 連結第一條線路隔著接縫相對的相鄰村莊                                                                         |
| `villageRailLinkLeast`         | 數字       | `128`   | 所鋪連結的最短長度，支線加幹線再加支線，以格計                                                               |
| `villageRailLinkMost`          | 數字       | `1024`  | 所鋪連結的最長長度，支線加幹線再加支線，以格計                                                               |
| `villageRailLinkBridgeMost`    | 數字       | `96`    | 一條連結可能需要的最長橋梁。需要跨越更寬的水面或更深的落差的連結不會鋪設                                     |
| `villageRailLinkTunnelMost`    | 數字       | `192`   | 在 `villageRailTunnelBlock` 會鑿隧道的情況下，一條連結可能需要的最長隧道。需要鑿得更遠的連結不會鋪設         |
| `villageRailLinkStation`       | 文字       | `both`  | 每條支線在幹線之前的車站：`both` 在線路兩側各鋪一個月台，`one` 在駛向幹線的列車左側鋪一個月台，`none` 則不建 |
| `villageRailLinkStationLength` | 數字       | `16`    | 車站月台有多少列長。`0` 表示不建車站                                                                         |
| `villageRailLinkPlatformWidth` | 數字       | `3`     | 每個月台有多少格寬                                                                                           |
| `villageRailLinkPlatformBlock` | 方塊       | 空      | 建造月台所用的方塊。留空則使用石磚                                                                           |

**哪些村莊會連結。** 只有當兩座村莊位於相鄰的單元格中，它們的第一條線路沿著穿過其間接縫的軸線延伸，並且整條連結沿軌道從井到井測得的長度介於 `villageRailLinkLeast` 與 `villageRailLinkMost` 格之間時，兩座村莊才會連結。判定的每一部分都由種子碼和這兩座村莊的位置算出，因此無論先生成哪座村莊或區塊，結果都相同。無法完整建成的連結根本不會鋪設，絕不會只建一半：需要比設定所允許更長的橋或隧道、越過世界邊界、撞上林地府邸、讓兩座村莊比 `structureSeparation` 所允許的更近，或使交匯處太靠近單元格的角落，都屬於這種情況。幹線只會鋪向確實已建立的村莊：當 `structureMost` 之類的上限擋下了鄰村，或鄰村長得太小而無法保留時，幹線的兩半都不會建，越過該村莊自身尾端的支線也不會建。固定位置的村莊以同樣的方式連結，每個單元格一座；含有兩座固定村莊的單元格，兩者都不連結。其他村莊在生長時會避開連結的支線與幹線，就像它們彼此避讓那樣。

**坡度。** 支線與幹線都是鐵路線，其坡度、架橋、鑿隧道與交叉的處理與村莊線路完全相同，使用上文的 `villageRailClimb` 以及棧橋和隧道設定。支線與幹線相接處，兩者都保持水平，其旁的車站也是如此。

**交匯處。** 支線只與幹線靠近的那條軌道相接。該軌道在支線中線與之相遇處斷開，支線的左軌道向左彎入，右軌道向右彎入，而遠側的軌道筆直穿過。以兩條軌道、幹線在上方、支線從下方而來為例：

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` 是路基，`o` 是軌道。當兩條支線會在相距幾格之內抵達時，第二座村莊的第一條線路會移過去與第一條對齊，兩者改在十字路口相接：每條支線只依上述方式併入各自靠近的軌道，幹線的兩條軌道都在支線中心處斷開，並且沒有任何鐵軌與另一條交叉：

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

單軌幹線沒有第二條軌道可以給另一條支線，因此支線會在單軌上迎頭相遇的連結不會鋪設。在單軌的情況下，支線的軌道向左彎入幹線軌道，而彎道之外的幹線軌道則止於其上。這些彎道的形狀是固定設定的，因此原版鐵軌只在繪製的交匯處轉彎，別處不會。

**車站。** 支線在交匯處之前的最後幾列是車站：由 `villageRailLinkPlatformBlock` 製成、與鐵軌齊平的月台，外側邊緣圍以 `villageSubwayRailingBlock` 欄杆，每個月台的中段設一張 `villageSubwayBenchBlock` 長椅。

**地鐵。** 在只有地鐵線路的資源包中，連結承載村莊的第一條地鐵線路。該線路從地下爬升向幹線，坡道長 `villageSubwayDepth` 乘以 `villageSubwayClimb` 列，並在地表抵達車站和交匯處；這類村莊只在一側連結，即連結較短的那一側，而幹線是依據 `villageRail` 設定建造的地面鐵路。當某條支線沒有空間容納該坡道及其車站時，幹線則改為下沉到地鐵：整條連結，包括支線和幹線，都留在 `villageSubwayDepth` 的地下，依據 `villageSubway` 設定建造，並在沒有車站的情況下於同樣的交匯處相接。

#### 村莊裝飾

*村莊*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"],
    "villageDecorSpacing": 3
  }
}
```

| 設定                  | 類型               | 預設 | 作用                                                                                                                                                 |
| --------------------- | ------------------ | ---- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villageDecor`        | `name=weight` 清單 | 無   | 沿村莊道路的路側散布本資源包自己的世界生成內容。名稱是世界生成登錄鍵，權重是該項目在各位置中所占的份額，而 `empty=weight` 是保持空白的位置所占的份額 |
| `villageDecorSpacing` | 整數，1 到 16      | `3`  | 沿村莊道路每一側的裝飾位置之間的間隔，單位為格。限制在 1 到 16 之間。比間距更寬的範本可能會擠到相鄰的範本                                            |

`villageDecor` 沿村莊道路的路側散布資源包自己的世界生成內容，這能避免村莊看起來只是幾棟房屋立在光禿禿的草地上。每個項目寫成 `name=weight`：名稱是世界生成登錄鍵，例如 `mypack:street_flowers`，權重是該項目在各位置中所占的份額。名稱 `empty` 表示保持空白的位置所占的份額，這一項最需要設對，因為沒有它的清單會填滿每個路側上的每個位置，村莊就會變成一座苗圃而不是一條街道。

道路每一側每隔 `villageDecorSpacing` 個方塊就是一個位置，預設是三個，依世界座標計數，因此間距會從一個道路片段延續到下一個片段。比間距更寬的範本可能會擠到相鄰的範本。位於村莊任何片段之內、道路本身之上、門前，或者地面不是立在堅實之物上的開闊空氣處的位置，都會被略過。每個位置上長出什麼，由世界種子碼和該位置本身算出，因此同一個世界總是以同樣的方式散布。

該名稱指向 `<namespace>/worldgen/*.json` 中一個普通的世界生成項目，因此 `decoration`、`tree` 或 `imprint` 都可以，且各自保留自己的方塊、大小和散布方式。這裡只用到該項目的形態：它的生態域、維度、高度和稀有度是它在整個世界中自行播撒的方式，而村莊並不參考它們，所以專為路側編寫的項目最好不要用於別處。路側是立在地面上的開闊空氣，因此這樣的項目需要把 `replace` 設為 `minecraft:air`；從未寫出 `replace` 的項目會被賦予通常的預設值 `minecraft:stone`，在這裡悄悄地什麼也不放。

在 `terrainAdaptation` 開啟期間，一個位置上長出的任何東西都會被固定下來，免受村莊自身整理的影響，因此立在路側的樹不會在下一個區塊被裝飾時再被砍掉。關閉它時，沒有整理需要抵禦，散布的效果相同。

#### 依生態域區分的村莊設定

*村莊*

**一個生態域可以有不同的建法。** `settings` 中的 `biomes` 物件為某個指定的生態域保存它自己的村莊設定，因此沙漠村莊可以鋪砂岩街道，而平原村莊鋪混凝土，二者無需成為不同的資源包。生態域可以用 id 指定，例如 `minecraft:desert`，也可以用 Forge 生態域類型指定，例如 `SANDY`、`SNOWY`、`MESA`、`JUNGLE` 等；精確的 id 先於類型被查看，因此可以為某一個生態域覆寫一條通用規則。某個區段中未提及的所有內容，都退回為其上方的一般設定。

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

道路、橋梁、鐵路、地鐵、車站或下水道所接受的每個方塊設定都適用於此，並且加權混合語法在區段內部與在外部同樣有效。生態域是在建造每個片段時讀取的，而在地面生態域發生變化的地方，方塊會重新選取，因此跨出沙漠的道路或鐵路會在邊界處當場更換材料。世界載入時的一行日誌會說明資源包附帶了多少個區段並列出它們的名稱，開啟除錯後，每個生態域還會說明它採用了哪個區段，或者說明它沒有採用任何區段以及它本會對應哪個區段。

### Blast Plaster

*各群組的作用*

爆炸之後會發生什麼，由 `<namespace>/blastplaster/*.json` 決定。`default` 交由資源包決定；`global` 忽略資源包檔案，讓本模組自身的預設值覆蓋 Blast Plaster 的設定；`off` 則把控制權完全交還給 Blast Plaster 自己的設定。

### 結構

*各群組的作用*

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

| 設定                            | 類型                                       | 預設    | 作用                                                                                                                                                 |
| ------------------------------- | ------------------------------------------ | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureSpacing`              | `structure=chunks` 清單                    | 原版    | 結構以多大間距排定。適用於神殿、海底神殿、林地府邸、終界城和要塞；對 `mineshafts` 而言，該數字表示每這麼多個區塊出現一個，因為原版就是這樣放置它們的 |
| `structureSeparation`           | `structure=chunks` 清單                    | 原版    | 同類結構之間最近允許多近。適用於海底神殿、林地府邸、終界城、要塞和村莊；對村莊而言，它是相鄰兩座村莊之間至少相隔的區塊數，不論網格本來允許多近       |
| `structureMinDistanceFromSpawn` | `structure=blocks` 清單                    | 原版    | 結構距離世界出生點多遠才開始生成                                                                                                                     |
| `structureBiomes`               | `structure=biome,biome` 清單               | 原版    | 結構在哪些生態域中生成，可用登錄名稱或生態域字典類型指定。適用於除終界城以外的所有結構，因為在此版本中終界只有一個生態域                             |
| `structureBiomesAreBlacklist`   | `structure=true` 或 `structure=false` 清單 | `false` | 每個結構的生態域清單的方向                                                                                                                           |
| `structureSpawns`               | `structure=entity:weight:least:most` 清單  | 原版    | 不論周圍生態域如何規定，結構都會生成的生物。只有神殿、海底神殿和地獄要塞設有這樣的清單；等號後留空一行會讓該結構不再生成任何自身的生物               |
| `structureSpawners`             | `structure=entity` 清單                    | 原版    | 原版結構內的生怪磚生成什麼，以逗號分隔則每個生怪磚隨機選一個。會放置生怪磚的有四種：地城、廢棄礦坑、地獄要塞和要塞                                   |
| `structureAt`                   | `structure=x,z` 清單                       | 無      | 把結構固定在精確的位置。參見[位於精確位置的結構](#位於精確位置的結構)                                                                                |
| `structureMost`                 | `structure=count` 清單                     | 無      | 一個維度最多可容納多少個某種結構。只有村莊讀取它，且用 `structureAt` 固定的村莊不論如何都會建立                                                      |

可以依名稱關閉原版結構，並依維度分別設定。放置由四個寫成 `structure=value` 的清單控制，每行一條：`structureSpacing` 決定排定間距，`structureSeparation` 決定同類結構最近能多近，`structureMinDistanceFromSpawn` 決定從多遠開始出現，`structureBiomes` 與 `structureBiomesAreBlacklist` 決定允許出現在哪裡。

```
temples=24
monuments=40
mineshafts=200
```

```
temples=minecraft:desert,SANDY
monuments=minecraft:deep_ocean
```

並非每種結構都理解每項設定。間距適用於神殿、海底神殿、林地府邸、終界城和要塞；對 `mineshafts` 而言，該數字表示每這麼多個區塊出現一個，而非網格，因為原版就是這樣放置它們的。間隔適用於海底神殿、林地府邸、終界城、要塞和村莊；對村莊而言，它是相鄰兩座村莊之間至少相隔的區塊數，不論網格本來允許多近。`structureMost` 限制一個維度最多可容納多少個某種結構，例如 `villages=100`：一旦建立了這麼多座，不論網格會把下一座放在哪裡，都不再建立新的，而用 `structureAt` 固定的村莊則不論如何都會建立。只有村莊讀取它。生態域設定適用於除終界城以外的所有結構，因為在此版本中終界只有一個生態域，無從選擇。終界城仍然在網格內自行挑選位置：它們只會坐落在地表達到 y60 的外島上，所以提高間距能讓它們變稀疏，卻無法把一座終界城放到虛空之上。地獄要塞位於原版沒有公開的固定網格上，因此只有生態域清單和出生點距離清單對它們有效。村莊保留自己的 `villageSpacing`、`villageBiomes` 等設定。

`structureSpawns` 取代結構所生成的生物，不論周圍生態域如何規定，寫成 `structure=namespace:entity:weight:least:most`，以逗號分隔：

```
netherbridges=minecraft:blaze:10:2:3,minecraft:wither_skeleton:8:5:5
temples=minecraft:witch:1:1:1
monuments=
```

在此版本中，只有神殿、海底神殿和地獄要塞設有這樣的清單；村莊的村民由建築片段自身放置，而廢棄礦坑、要塞和終界城則改用生怪磚和已放置的生物。如上方 monuments 那一行，等號後留空，會讓該結構不再生成任何自身的生物。

`structureSpawners` 指定原版結構內的生怪磚生成什麼，寫成 `structure=namespace:entity`，以逗號分隔則每個生怪磚隨機選一個：

```
dungeons=minecraft:zombie,minecraft:husk
mineshafts=minecraft:cave_spider
netherbridges=minecraft:wither_skeleton
strongholds=minecraft:silverfish
```

有四種原版結構會放置生怪磚：地城房間、廢棄礦坑走廊、地獄要塞王座和要塞傳送門房間。每一種都單獨生效，所以其他模組放置的生怪磚不會被觸及。地城通常從各模組透過 Forge 加入的清單中挑選，因此在此處指定它們也會接管這一選擇。

間距決定結構在何處排定，所以在已存在的世界裡修改它，已有的結構保持原樣，新的結構則會放在另一套網格上。

### 生成

*各群組的作用*

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

| 設定                          | 類型                    | 預設   | 作用                                                                                                                                                                 |
| ----------------------------- | ----------------------- | ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `surfaceDayMonsterRate`       | 浮點數                  | `1.0`  | 白天地表敵對生物生成的倍率，`1.0` 即原版，因此可以關閉白天地表的生成而不影響洞穴                                                                                     |
| `surfaceNightMonsterRate`     | 浮點數                  | `1.0`  | 夜間地表的同類倍率                                                                                                                                                   |
| `undergroundDayMonsterRate`   | 浮點數                  | `1.0`  | 白天地下的同類倍率                                                                                                                                                   |
| `undergroundNightMonsterRate` | 浮點數                  | `1.0`  | 夜間地下的同類倍率                                                                                                                                                   |
| `monsterCap`                  | 整數                    | `-1`   | 同時可載入多少敵對生物。原版為 70，`-1` 表示保持不變                                                                                                                 |
| `creatureCap`                 | 整數                    | `-1`   | 被動動物的同類上限。原版為 10                                                                                                                                        |
| `ambientCap`                  | 整數                    | `-1`   | 蝙蝠之類生物的同類上限。原版為 15                                                                                                                                    |
| `waterCreatureCap`            | 整數                    | `-1`   | 魷魚的同類上限。原版為 5                                                                                                                                             |
| `monsterSpawnLight`           | 整數                    | `-1`   | 在原版檢查之外，敵對生物生成所能容忍的最高方塊光照。`0` 是現代版本的規則，火把可以完全保護一個洞穴；`-1` 保留原版的隨機判定                                          |
| `skyAnimals`                  | 布林值                  | `true` | 被動生物是否會在 Rubic 世界於其地形視窗之上生成的陸地上定居，尤其是上方的空島。關閉後動物和蝙蝠只留在下方的地面上。生怪磚不受這兩項影響                              |
| `threatItems`                 | `item=level,count` 清單 | 無     | 會提高攜帶者威脅分數的物品，末尾可帶選用的 `,each` 或 `,batch`：預設的 `each` 對持有的每一個都加上該等級，最多計到 `count` 個；`batch` 則每持有整整 `count` 個加一次 |
| `threatLevels`                | 整數清單                | 無     | 進入各個檔位的分數，遞增，所以 `10, 25, 50` 構成三個檔位。留空則關閉威脅等級                                                                                         |
| `threatMost`                  | 整數                    | `-1`   | 分數上限。`-1` 表示不設上限                                                                                                                                          |
| `threatSpawnRate`             | 浮點數                  | `1.0`  | 在最高檔位攜帶者 128 格範圍內，在其他倍率之上縮放敵對生物的生成，較低檔位按比例分得一部分                                                                            |
| `threatNotice`                | 浮點數，單位為格        | `0.0`  | 敵對生物（包括原版的）能在多遠的額外距離外發現最高檔位的攜帶者，同樣按比例分攤給較低檔位                                                                             |
| `threatSays`                  | `band=message` 清單     | 無     | 玩家自身檔位變化時以黃色顯示的訊息，檔位 `0` 是降回第一檔以下時的那一句                                                                                              |

各生態域的生物生成倍率與上限。敵對生物的生成由 `surfaceDayMonsterRate`、`surfaceNightMonsterRate`、`undergroundDayMonsterRate` 和 `undergroundNightMonsterRate` 縮放，每個都是倍率，`1.0` 即原版，因此可以關閉白天地表的生成而不影響洞穴。上限包括 `monsterCap`、被動動物的 `creatureCap`、蝙蝠之類的 `ambientCap` 以及魷魚的 `waterCreatureCap`；原版分別為 70、10、15 和 5，`-1` 表示保持不變。`monsterSpawnLight` 在原版檢查之外限制敵對生物生成所能容忍的方塊光照：`0` 是現代版本的規則，火把可以完全保護一個洞穴，預設的 `-1` 保留原版的隨機判定。`skyAnimals` 決定被動生物是否會在 Rubic 世界於其地形視窗之上生成的陸地上定居，尤其是上方的空島：預設的 `true` 讓原版的畜群出現在頂層方塊所在之處，`false` 則讓動物和蝙蝠只留在下方的地面上。生怪磚不受這兩項影響。

威脅等級會為每位玩家所攜帶的東西計分，並讓世界做出回應。`threatItems` 列出計分的物品，寫成 `item=level,count` 項目，末尾可帶選用的 `,each` 或 `,batch`：預設的 `each` 對持有的每一個都加上該等級，最多計到 `count` 個；`batch` 則每持有整整 `count` 個加一次該等級，只算完整的批次。超過物品堆疊上限的 count 會被截到堆疊上限，所以一整組就是單一項目所能計的最大數量，物品還可以帶中繼資料，例如 `minecraft:dye:4`。每個持有物品的已載入實體都是攜帶者，不只是玩家：玩家的主物品欄、盔甲和副手、掉落的物品堆、任何帶物品欄的實體（如載運箱子的騾或運輸礦車），以及其他任何生物手持和穿戴的物品，因此一片區域會在躺在、騎行在或走動在那裡的東西周圍保持危險。創造模式和旁觀模式的玩家得分為零。`threatLevels` 是進入各個檔位的分數，遞增，所以 `[10, 25, 50]` 構成三個檔位，`threatMost` 限制分數上限，`-1` 表示不設上限。分數每五秒計算一次。`threatSpawnRate` 在最高檔位攜帶者 128 格範圍內，在其他倍率之上縮放敵對生物的生成，較低檔位按比例分得變化量的一部分：`2.0` 在最高檔翻倍，在三檔中的第一檔則增加三分之一。`threatNotice` 是敵對生物（包括原版的）能在多遠的額外距離外發現最高檔位的攜帶者，同樣按比例分攤給較低檔位。`threatSays` 是玩家自身檔位變化時以黃色顯示的訊息，寫成 `band=message` 項目，檔位 `0` 是降回第一檔以下時的那一句。實體變體可以設定 `threatLeast`，使其只在 128 格內有攜帶者處於該檔位或更高檔位時才自然生成。兩個清單中任何一個留空都會關閉威脅等級。

### 安置結構

*各群組的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"]
  }
}
```

| 設定                  | 類型                  | 預設                                         | 作用                                                                                                                                            |
| --------------------- | --------------------- | -------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureAdaptation` | `structure=mode` 清單 | 村莊和林地府邸為 `beard_thin`，其餘為 `none` | 地形要適應哪些結構以及如何適應，涵蓋村莊、要塞、廢棄礦坑、海底神殿和林地府邸。模式有 `none`、`bury`、`beard_thin`、`beard_box` 和 `encapsulate` |

`structureAdaptation` 決定地形要適應哪些結構以及如何適應，寫成 `structure=mode` 項目，例如 `"mansions=bury"`、`"monuments=none"`，涵蓋村莊、要塞、廢棄礦坑、海底神殿和林地府邸，並使用現代版本採用的五種模式：`none`、`bury`、`beard_thin`、`beard_box` 和 `encapsulate`。除非另行指定，村莊和林地府邸為 `beard_thin`，其餘一律為 `none`。神殿目前還不能指定，因為神殿只在建造時才放置自己，地形來不及對它做出適應。

### 安置村莊

*各群組的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "terrainAdaptation": true
  }
}
```

| 設定                | 類型   | 預設    | 作用                                                                                                                         |
| ------------------- | ------ | ------- | ---------------------------------------------------------------------------------------------------------------------------- |
| `terrainAdaptation` | 布林值 | `false` | 重新設計村莊如何選擇地面並坐落其上：整平的道路、安置妥當的建築、堆高的環形土台，以及本節描述的其他一切。它鋪設的內容是永久的 |

**它鋪設的內容是永久的。** 它在世界生成時就重塑地形，所以它寫進存檔的一切都會留在那裡。舊版本生成的村莊不會被新版本重新處理或修復，因此用同一個種子碼、在兩個不同版本的模組上生成的兩個世界並不會一致，世界裡的村莊只是那些區塊生成當天的一份快照。

`terrainAdaptation` 重新設計村莊如何選擇地面並坐落其上，其概念移植自現代版本安置結構的方式，並在此基礎上更進一步。村莊只會建立在地面起伏不超過十格的區塊上，且絕不會距離另一座村莊不到八個區塊；沒有這種區塊的區域則完全不會建立村莊。水井坐落在其佔地所碰到的最低地面上，整座村莊隨之移動，其餘一切都以此為基準整平。

道路在鋪設時就會整平：路面跟隨道路寬度範圍內最低的天然地面，凸起處被削平，凹陷處被填上，坡度每一步不超過一格，較短的溝壑則用木板橋跨過。路面材料跟隨所經過的地面：泥土上是草徑，沙子上是砂岩，惡地上是硬化黏土，石頭和礫石上是礫石，水面上是木板，所以沙漠村莊得到的是砂岩街道而不是土路，道路也不再於地面不是草地的地方消失。兩條道路相交時，會在兩者中較低的坡度處會合，因為只有雙方都能到達的高度，才不會在兩者之間留下台階。

每棟建築坐落在其所臨道路之上一格處，這個高度取自已鋪好的道路，或在道路尚未建造時，根據道路將要整平到的地面來預測，因此門前的台階落在路面上，門則位於台階之後。如果某棟建築佔地的任何一部分之下需要超過兩格的人造地面，它就不會建在那裡：它會沿著自己的道路滑動最多十二格，尋找最淺的落腳處，找不到就整棟捨棄，所以建在崎嶇地面上的村莊會變得更稀疏，而不是懸空架在那裡。建築周圍的一圈在下坡一側被墊高，在上坡一側被削低，再往外一圈則又淺一格。

農田保持原版自己的地面高度。路燈柱立在它所照亮的道路的坡度上，而不是路旁的路肩，在道路高出路邊之處，其下會填上地面；原版自己的火把柱不會出現在配置中，因為這些路燈取而代之。每棟建築下方的地面會向下填到最近的承托面，使用與其所在之處相同的材料，牆壁和門洞從山坡中開鑿出來，屋頂上的泥土被清走，結構中的任何一棵樹都會被整棵砍倒，樹葉隨木頭一同消失，但仍屬於某根留存樹枝的每一片樹葉則保持不動。林地府邸和零散的地物（神殿、小屋、冰屋）在放置之前，也要先通過同樣的平地標準。

它在地形生成時就重塑地形本身，因此開啟它所生成的世界和關閉時生成的世界不同，現代版本也帶有同樣的警告，而且除非資源包或設定檔要求，否則它是關閉的。

### 基岩

*各群組的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "flatBedrock": true,
    "flatBedrockRetrogen": false,
    "flatBedrockRetrogenKey": "0000",
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

| 設定                                | 類型                   | 預設               | 作用                                                                                                 |
| ----------------------------------- | ---------------------- | ------------------ | ---------------------------------------------------------------------------------------------------- |
| `flatBedrock`                       | 布林值                 | `false`            | 用平整的層取代世界底部參差不齊的基岩。除非開啟 `flatBedrockRetrogen`，否則只對新區塊生效             |
| `flatBedrockRetrogen`               | 布林值                 | `false`            | 同時也把已存在區塊中的基岩整平。每個區塊只處理一次並記住，而且無法復原：原本的樣式沒有記錄在任何地方 |
| `flatBedrockRetrogenKey`            | 文字                   | `0000`             | 修改它可讓所有區塊重新符合整平條件。已存在的區塊只有在開啟 `flatBedrockRetrogen` 時才會被處理        |
| `bedrockLayers`                     | 整數                   | `1`                | 保留多少層基岩                                                                                       |
| `flatBedrockRoof`                   | 布林值                 | `false`            | 如果維度有基岩頂，例如地獄的頂部，也一併整平                                                         |
| `flatBedrockFiller`                 | 方塊                   | 空                 | 用什麼取代被移除的基岩。留空則依維度選擇：石頭、地獄石、終界石                                       |
| `flatBedrockFillers`                | `dimension=block` 清單 | 地獄和終界的預設值 | 依維度指定的填充物，對所列維度覆寫 `flatBedrockFiller`                                               |
| `flatBedrockDimensions`             | 整數清單               | `0`，主世界        | 在哪些維度中整平。留空表示所有維度                                                                   |
| `flatBedrockDimensionsAreBlacklist` | 布林值                 | `false`            | 開啟時，整平會跳過所列維度。關閉時，只對它們生效                                                     |
| `flatBedrockBiomes`                 | 生態域名稱清單         | 無                 | 在哪些生態域中整平，可用易讀名稱或註冊名稱。留空表示所有生態域                                       |
| `flatBedrockBiomeTypes`             | 字典類型清單           | 無                 | 與 `flatBedrockBiomes` 並用、需要整平的生態域字典類型。`OCEAN`、`RIVER`、`MOUNTAIN` 等等             |
| `flatBedrockBiomesAreBlacklist`     | 布林值                 | `false`            | 開啟時，整平會跳過所列生態域。關閉時，只對它們生效                                                   |

`flatBedrock` 用平整的層取代參差不齊的那一層，可依維度和生態域分別設定，填充方塊由你選擇。`flatBedrockRetrogen` 對已存在的區塊也這樣做。它無法復原，原本的樣式沒有記錄在任何地方。`bedrockLayers` 設定保留多少層，`flatBedrockRoof` 在維度有基岩頂時也處理頂部，`flatBedrockFiller` 是取代被移除基岩的方塊，留空則依維度選擇，也可以改用 `flatBedrockFillers` 為每個維度各指定一種。作用於哪些維度和生態域，由 `flatBedrockDimensions`、`flatBedrockBiomes` 和 `flatBedrockBiomeTypes` 決定，`flatBedrockDimensionsAreBlacklist` 和 `flatBedrockBiomesAreBlacklist` 則把這些清單變成排除清單。

### 遠處緩慢更新刻

*各群組的作用*

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

實體是伺服器最大的開銷，而其中大多數離任何玩家都很遠。`slowDistantEntities` 讓 `slowDistance` 格內沒有玩家的區塊，每 `slowRate` 刻只更新一刻，所以其中的東西仍然會移動、漂浮、燃燒和消失，只是步調較慢。沒有任何東西會被完全擱置而不更新。

| 鍵                    | 必填 | 值                                          | 預設                  | 作用                                                                                       |
| --------------------- | ---- | ------------------------------------------- | --------------------- | ------------------------------------------------------------------------------------------ |
| `slowDistantEntities` | 否   | 布林值                                      | `true`                | 是否減慢任何東西                                                                           |
| `slowedKinds`         | 否   | `items`、`experience`、`projectiles` 的清單 | `{items, experience}` | 哪些種類會減少更新刻。任何能自行思考的東西一律改為被減慢，不在此處列出。機器永遠不會被減慢 |
| `slowDistance`        | 否   | 整數，64 以上                               | `192`                 | 距離最近的玩家多遠，區塊才會被減慢                                                         |
| `slowRate`            | 否   | 整數，1 到 20                               | `4`                   | 被減慢的區塊每這麼多刻獲得一刻更新。`1` 表示不減慢任何東西                                 |
| `neverSlowed`         | 否   | 實體名稱清單                                | 無                    | 無論離得多遠都不受影響                                                                     |
| `slowRecheck`         | 否   | 整數，1 到 100                              | `20`                  | 多久重新計算一次到最近玩家的距離                                                           |

任何能自行思考的東西，每一隻生物、動物、村民和魔像，無論來自哪個模組，都與其餘的區別對待，且完全不列在 `slowedKinds` 中。它們永遠不會被減少更新刻，因為玩家看得到它們行走。取而代之的是，它們每一刻都照常更新，但思考的頻率降低：負責決定下一步要做什麼的那部分思維（這也是最耗費資源的部分），每 `slowRate` 刻才詢問一次，而不是每三刻一次。它照樣移動、下墜、溺水、燃燒和尋路，與原本完全一樣，只是在沒有人靠近時比較少改變主意。沒有什麼可看到的，沒有一步一頓，也沒有補趕，而且玩家走近時，它在進入視野之前就已恢復成平常的狀態。因為無法被察覺，所以這不是一個可選項：只要減慢功能開啟，它就會發生。

被減少更新刻的東西仍以正常的速度老化。掉落物和經驗球各自帶有一個決定何時消失的計數器，在被減慢的區塊不處理的那一刻，該計數器照樣往前推進。所以掉落物仍然在地上停留五分鐘，而不是二十分鐘。減少的只是它每刻所做的事，絕不是它能存在多久。

被某種東西刻意保持載入的區塊永遠不會被減慢，無論離得多遠。那些就是區塊載入器所維持的區塊，而維持它們的全部意義，就在於其中的東西繼續運轉，所以主人不在時仍在運作的農場，會依它建造時的速度工作。世界出生點周圍的區塊不屬於此類，因為沒有任何東西要求保留它們，所以它們和其他地方一樣會被減慢。

整個區塊要麼一起被減慢，要麼一起不被減慢，所以其中的東西仍然表現得合乎情理：掉落物落在同一堆裡，生物仍然跟隨身旁的那一隻。每位玩家都只為自己計算，所以獨自在外的人無論身在何處，周圍都有一片安靜的空間。被騎乘、被命名、被馴服、被拴住、發光、被阻止消失、帶有效果，或已經在追逐玩家的東西，無論離得多遠都不受影響，所有機器也一樣。它適用於每個世界，包括模組新增的世界。

### 觀察區塊處理

*各群組的作用*

開啟 `worldgenDebug` 後，每一百輪會輸出一行，說明世界如何花費區塊處理的工作量：新生成了多少區塊，有多少區塊在被放掉之後又得取回，其中有多少是從磁碟讀取，而不是從仍在等待寫入的佇列中取出，開啟了多少區域檔案以及它們被同時全部關閉了幾次，還有任一時刻持有區塊數與未完成寫入數的最大值。它是為了判斷生成陸地的時間究竟花在生成上，還是花在把同一塊地面取回上而寫的，所以值得在大規模預先生成之前開啟，之後再關閉。

隨後還有三行：一行是把區塊寫回儲存空間，一行是區塊的光照計算，一行則把陸地本身的生成拆分為地面、遊戲附加其上的裝飾，以及各個模組附加其上的裝飾，並列出最慢的五個名稱。這樣，一個卡頓的世界就可以被解讀為四項獨立的開銷，而不是一項，並且能指出具體是哪個模組，而不必靠猜。

### 提前造陸

*各群組的作用*

篇幅大到有自己獨立的一節，請見[預先生成](#預先生成)。

### 等待輪到的方塊

*各群組的作用*

水的蔓延、熔岩的冷卻和作物的生長，都是方塊等待一段時間後才做某件事，而遊戲把它們全都放在同一個堆裡。每次寫出一個區塊時，遊戲都要從頭到尾走訪整個堆，尋找屬於該區塊的那幾個，所以世界裡這類方塊越多，每次寫入就越慢，不論正在寫入的區塊本身有沒有這樣的方塊。現在它們依所在區塊排序，而一旦堆發生變化或回合推進，這個排序就會被丟棄並重做，所以寫出一個區塊時只需查看與它有關的那一小撮。

### 為區塊內的方塊擴充空間

*各群組的作用*

區塊是以切片來保存的，每個切片持有一份其中方塊種類的清單，起初只有容納十六種的空間。超過十六種，就得建一份更大的清單，並把切片中四千個方塊逐一複製過去，到三十二種時再來一次，六十四種時又一次。含有幾種石頭和礦石的地面全都會超過這些大小，所以為了一點空間，要重複做四次。現在第一次空間不足時，就直接跳到這些大小中最大的一個，這樣只需複製一次而不是四次，代價是每個切片多佔幾 KB，而這些切片本來就會在轉眼間用到。

### 準備待寫入的區塊

*各群組的作用*

區塊在寫出之前，要先轉換成寫到磁碟上的形式，這需要走訪它的每一個方塊，並依名稱在一張表裡逐一查找。地面是由大段相同的東西構成的，所以同一塊石頭的同一次查找要做上成千上萬遍，現在只需把上一次的答案保留下來，在下一個方塊相同時直接重複使用。這不是一個可以關閉的功能，因為沒有什麼需要權衡的：不論怎樣，答案都是一樣的。

### 寫出區塊

*各群組的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "hurryWritesAbove": 100
  }
}
```

| 設定               | 類型             | 預設  | 作用                                                                                                                               |
| ------------------ | ---------------- | ----- | ---------------------------------------------------------------------------------------------------------------------------------- |
| `hurryWritesAbove` | 整數，單位為區塊 | `100` | 等待寫入的已完成區塊達到多少時，寫入執行緒不再於每寫完一個後休息百分之一秒，而是盡可能快地寫。`0` 讓它始終保持休息，與遊戲本身一致 |

遊戲在自己的執行緒上一次寫出一個已完成的區塊，每寫完一個就休息百分之一秒。這使它不論磁碟多快，都被限制在每秒約一百個區塊，在有人遊玩時綽綽有餘，但在大量造陸時遠遠不夠，於是未寫入的區塊就堆積在記憶體裡。`hurryWritesAbove` 規定等待的區塊數達到多少時，它不再休息，而是盡可能快地寫。`100` 是預設值，與遊戲本身開始壓住生成的那個臨界點一致；`0` 讓它始終保持休息，與遊戲本身一致。在等待數量較少時什麼都不會改變，而這正是遊玩中的每一個尋常時刻。

每次清理執行時，都會即時為它寫一行，指明執行的是哪個清掃器、耗時多久、清理前後持有多少，以及當時遊戲擁有多少空間。如果這個空間發生變化，會特別說明，因為空間的增長本身就是這類停頓中最長的那些的成因：啟動時空間少於最終所需的遊戲，會在與它正在做的事毫無關係的時刻反覆停下來擴充。啟動時就給它允許的最大空間，可以完全避免這一點。

最後一行說明自上次查看以來丟棄了多少工作廢料、清理它花了多長時間、共清掃了幾次，以及遊戲目前持有它被允許的空間中的多少。造陸本身就會丟棄大量東西，因為每個區塊在寫出之前都會被轉換成全新的陣列，而這種清理發生在回合之間而不是回合之中，所以它表現為一次卡頓，而不是體現在上面任何一項計數的耗時裡。

### 出生點區塊

*各群組的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["0=64", "7=0"]
  }
}
```

| 設定               | 類型                    | 預設  | 作用                                                                                                                                                                                                                                                                   |
| ------------------ | ----------------------- | ----- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `spawnChunkRadius` | 整數，單位為格          | `128` | 距離世界出生點多遠（以方塊計）的區塊，不論是否有人在場都保持載入。B 格會從出生點區塊向每個方向保持 `r = (B + 8) / 16` 個區塊，共 `(2r+1)²` 個，世界啟動時會在其周圍準備 `(2r+9)²` 個區塊。`128` 就是遊戲的做法，保持 289 個、準備 625 個，`0` 則不保持也不準備任何區塊 |
| `spawnChunkRadii`  | `dimension=blocks` 清單 | 無    | 一次為一個維度設定半徑，對所列維度覆寫 `spawnChunkRadius`                                                                                                                                                                                                              |

遊戲會讓世界出生點周圍的區塊不論是否有人在場都保持載入，好讓各模組有一個始終在更新的地方。它在每個方向上是 128 格，約 289 個區塊，而且在遊戲中無法調整。`spawnChunkRadius` 設定這個距離。`128` 就是遊戲的做法，也是預設值，較小的數字保持較小的錨點，`0` 則完全不保持，於是出生點區域和其他地方一樣會被卸載。`spawnChunkRadii` 一次為一個維度設定半徑，寫成 `dimension=blocks`，每行一條，對所列維度覆寫 `spawnChunkRadius`。

只有被註冊為保持其出生點的維度才會保持，在遊戲本身中那只有主世界，地獄和終界從來不保持，所以對它們設定此項不會有任何改變。模組新增的維度只有在該模組提出要求時才會保持，而這樣做的模組往往帶著一份資源包根本不想要的、多出來的 289 個區塊。世界是否保持載入是另一回事，此項不會觸及：被模組標記為保持載入的維度在 `0` 時仍然保持載入，只是不再保持區塊。大多數把出生點當作錨點的模組想要的是那裡有點什麼，而不是 289 個區塊的量，所以一個小數字通常能讓它們繼續運作，而 `0` 則不行。

### 虛空世界

*各群組的作用*

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

| 設定                              | 類型           | 預設              | 作用                                                                                     |
| --------------------------------- | -------------- | ----------------- | ---------------------------------------------------------------------------------------- |
| `voidWorld`                       | 布林值         | `false`           | 生成一個空世界，在出生點放一個平台，並阻止生物、動物、結構以及模組本來會在那裡生成的一切 |
| `voidPlatformBlock`               | 方塊           | `minecraft:stone` | 平台由什麼構成                                                                           |
| `voidPlatformSize`                | 整數，單位為格 | `9`               | 平台有多寬，向下取整為奇數，使它以出生點為中心                                           |
| `voidPlatformHeight`              | 整數           | `64`              | 平台位於世界底部之上多高的位置                                                           |
| `voidWorldDimensions`             | 整數清單       | `0`，主世界       | 哪些維度被清空。只有主世界會得到平台                                                     |
| `voidWorldDimensionsAreBlacklist` | 布林值         | `false`           | 開啟時，所列維度是被保持原樣的那些                                                       |

`voidWorld` 生成一個空世界，在出生點放一個平台，並阻止生物、動物、結構以及模組本來會在那裡生成的一切。平台的方塊、大小和高度是 `voidPlatformBlock`、`voidPlatformSize` 和 `voidPlatformHeight`；大小向下取整為奇數格，使平台以出生點為中心。`voidWorldDimensions` 選擇清空哪些世界，預設只有主世界，`voidWorldDimensionsAreBlacklist` 則把該清單變成要保持原樣的那些。地獄和終界的清空方式與主世界相同，不論它們是此版本自己建構的，還是被某個模組取代後的版本。只有主世界會得到平台，所以通往被清空的地獄或終界的通道，需要資源包自己提供。被清空的終界也沒有終界龍、終界水晶和基岩噴泉，因為建構它們的戰鬥根本沒有開始。

### 終界龍

*各群組的作用*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

| 設定          | 類型   | 預設   | 作用                                                                                                             |
| ------------- | ------ | ------ | ---------------------------------------------------------------------------------------------------------------- |
| `dragonFight` | 布林值 | `true` | 整件事是否發生：終界龍、它的血條、終界水晶、它所站立的噴泉，以及玩家用終界水晶發起的重生。屬於 `structures` 分組 |

`dragonFight` 屬於 `structures` 分組，決定整件事是否發生：終界龍、它的血條、終界水晶、它所站立的噴泉，以及玩家用終界水晶發起的重生。被清空的終界除非資源包要求，否則不含這些，而一般的終界除非資源包另有規定，否則就有這些，所以不論哪種情況，`dragonFight` 都值得設定。

### 地形

*各群組的作用*

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

| 設定                            | 類型         | 預設                             | 作用                                                                                                                                                                                                                                                                                                           |
| ------------------------------- | ------------ | -------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldType`                     | 字串         | 空                               | 每個新世界所採用的世界類型，不論在建立它的畫面上選了什麼：`default`、`largebiomes`、`amplified`、`customized`，或模組新增的某個類型。專用伺服器會在世界載入之前把它作為 `level-type` 寫入 `server.properties`，除非 `level-type` 已經指定了某個 `worldTypeExceptions` 中的類型。留空則把選擇權交給建立世界的人 |
| `worldTypeExceptions`           | 世界類型清單 | `flat`、`debug_all_block_states` | `worldType` 放行的那些選項                                                                                                                                                                                                                                                                                     |
| `worldSeed`                     | 字串         | 空                               | 每個新世界所採用的種子碼，依輸入時的寫法書寫：數字原樣使用，其他內容則依遊戲的方式轉換成數字。專用伺服器會在世界載入之前把它作為 `level-seed` 寫入 `server.properties`                                                                                                                                         |
| `terrainWorldTypes`             | 世界類型清單 | 無                               | 地形設定究竟套用於哪些世界類型。留空表示所有類型                                                                                                                                                                                                                                                               |
| `terrainWorldTypesAreBlacklist` | 布林值       | `false`                          | 開啟時，所列的世界類型是被保持原樣的那些                                                                                                                                                                                                                                                                       |

`worldType` 決定新世界屬於哪一種世界，不論在建立它的畫面上選了什麼，`default`、`largebiomes`、`amplified`、`customized`，或模組新增的某個類型，例如 `biomesop` 或 `realistic`。圍繞某一種世界類型打造的資源包在此指定它，每個新世界就都以這種方式建立。留空（預設）則把選擇權交給建立世界的人。已存在的世界保持它被建立時的類型，而沒有任何東西提供的名稱會被記入日誌並忽略。`worldTypeExceptions` 指定被放行的選項，起初是超平坦和除錯世界，因為想要統一世界類型的資源包，很少打算從正在測試的人手中奪走超平坦，並且建立世界的人在進入世界後，會在聊天中被告知一次：該資源包選擇了它的類型。這則訊息由設定檔透過 `tellWorldType` 決定，而不是由資源包決定，所以遊玩的人可以為自己關閉它，任何資源包都無法把它重新打開。更改類型時，建立世界時所用的設定會被捨棄，因為它們是為之前選擇的類型寫的。

`worldSeed` 決定每個新世界所採用的種子碼，不論在建立它的畫面上輸入了什麼。它依輸入時的寫法書寫：數字原樣使用，其他內容則依遊戲把一個詞轉換成數字的方式轉換，所以 `Hollow Ridge` 和 `-4172144997902289642` 都可以，而且總是得到同一個世界。留空（預設）則把選擇權交給建立世界的人。已存在的世界保持它被建立時的種子碼，所以這只決定新世界會得到什麼。圍繞某一張地圖打造的資源包，在此指定它的種子碼，用該資源包建立的每個世界就都是那張地圖。

`generatorOptions` 塑造主世界本身，包括海平面、熔岩海洋和每一種地形雜訊，格式與自訂世界類型所寫的相同。它在世界建立時套用，此後不再套用，所以已存在的世界保持原樣。已經帶有自身選項的世界會保留這些選項，日誌會指出所使用的字串。專用伺服器會在世界載入之前，把它們作為 `generator-settings` 寫入 `server.properties`。

自帶設定而從不查看世界設定的世界類型，例如 Quark 的 realistic，會得到合併進它自己設定中的資源包設定，所以除非資源包要求別的，它原本被打造出的形態得以保留。

`terrainWorldTypes` 指定設定究竟套用於哪些世界類型，`default`、`customized`、`biomesop`、`realistic` 等等，`terrainWorldTypesAreBlacklist` 則把它變成要保持原樣的清單。留空（預設）表示所有世界類型。塑造一般世界、卻想讓某個模組的世界類型保持該模組原樣的資源包，在此指定它即可：不合併任何內容，不交出任何東西，該模組自己的自訂畫面也保持開啟。名稱會與世界建立時所用的世界類型進行比對，所以指定一個此處沒有提供的名稱，只是永遠不會符合，不會產生任何代價。

下面關於 Biomes O' Plenty 的一切，只有在安裝了該模組時才會發生，因為這項工作由僅在它存在時才載入的相容程式碼完成。沒有它就沒有 `biomesop` 世界類型可選，指定了它的資源包會得到世界實際建立時所用的世界類型。

在 Biomes O' Plenty 世界上，同樣的設定會被轉換成該模組所讀取的措辭，所以資源包不需要另備一份。`biomeSize` 變成它的五種大小之一，雜訊和縮放設定原樣傳遞，凡是它從不讀取的內容都會被省略，並在日誌中留一行說明。該模組讀取的內容遠少於自訂世界類型，而且完全不從它的設定中讀取海平面、洞穴、湖泊或結構開關，所以這些會被直接交給它，資源包設定它們的方式與任何其他世界相同。

有兩件事它自行決定。河流出自它自己的圖層，沒有設定，所以 `riverSize` 在那裡毫無意義。而海洋、山脈和區域究竟位於何處，也出自它的圖層，只能透過 `landScheme`、`tempScheme`、`rainScheme` 和 `biomeSize` 影響，所以資源包是以該模組的方式，而不是自訂世界類型的方式，來塑造那個世界。單一生態域的世界仍然可以由資源包製作：封鎖所有生態域，並把你想要的那個設為範本的 `default`，這在它的世界類型上與在任何其他世界類型上的效果相同。

資源包所做的其他一切，包括封鎖生態域和礦石、取代方塊、整平基岩、結構放置、它自己的世界生成，從來都不經過那個字串，在任何世界類型上的效果都相同。

#### generatorOptions 的鍵

*每個鍵及其預設值*

主世界的 `generatorOptions` 是一個寫成字串的 JSON 物件，其中的引號需要跳脫。它沒有寫出的鍵保持此處列出的預設值。它適用於維度的 `overworld` 地形類型和 `customized` 世界類型；超平坦預設組合保留自己的格式，本表不涵蓋。

```json
{
  "settings": {
    "worldType": "customized",
    "generatorOptions": "{\"seaLevel\":40,\"oceanFluid\":\"mypack:molten_ruby\"}"
  }
}
```

同樣的鍵寫成一般 JSON，每個鍵都帶預設值：

```json
{
  "coordinateScale": 684.412,
  "heightScale": 684.412,
  "upperLimitScale": 512.0,
  "lowerLimitScale": 512.0,
  "depthNoiseScaleX": 200.0,
  "depthNoiseScaleZ": 200.0,
  "depthNoiseScaleExponent": 0.5,
  "mainNoiseScaleX": 80.0,
  "mainNoiseScaleY": 160.0,
  "mainNoiseScaleZ": 80.0,
  "baseSize": 8.5,
  "stretchY": 12.0,
  "seaLevel": 63,
  "useLavaOceans": false,
  "oceanFluid": "",
  "useWaterLakes": true,
  "waterLakeChance": 4,
  "useLavaLakes": true,
  "lavaLakeChance": 80,
  "useCaves": true,
  "useRavines": true,
  "useDungeons": true,
  "dungeonChance": 8,
  "useStrongholds": true,
  "useVillages": true,
  "useMineShafts": true,
  "useTemples": true,
  "useMonuments": true,
  "useMansions": true,
  "biomeDepthWeight": 1.0,
  "biomeDepthOffset": 0.0,
  "biomeScaleWeight": 1.0,
  "biomeScaleOffset": 0.0,
  "fixedBiome": -1,
  "biomeSize": 4,
  "riverSize": 4,
  "dirtSize": 33,
  "dirtCount": 10,
  "dirtMinHeight": 0,
  "dirtMaxHeight": 256,
  "gravelSize": 33,
  "gravelCount": 8,
  "gravelMinHeight": 0,
  "gravelMaxHeight": 256,
  "graniteSize": 33,
  "graniteCount": 10,
  "graniteMinHeight": 0,
  "graniteMaxHeight": 80,
  "dioriteSize": 33,
  "dioriteCount": 10,
  "dioriteMinHeight": 0,
  "dioriteMaxHeight": 80,
  "andesiteSize": 33,
  "andesiteCount": 10,
  "andesiteMinHeight": 0,
  "andesiteMaxHeight": 80,
  "coalSize": 17,
  "coalCount": 20,
  "coalMinHeight": 0,
  "coalMaxHeight": 128,
  "ironSize": 9,
  "ironCount": 20,
  "ironMinHeight": 0,
  "ironMaxHeight": 64,
  "goldSize": 9,
  "goldCount": 2,
  "goldMinHeight": 0,
  "goldMaxHeight": 32,
  "redstoneSize": 8,
  "redstoneCount": 8,
  "redstoneMinHeight": 0,
  "redstoneMaxHeight": 16,
  "diamondSize": 8,
  "diamondCount": 1,
  "diamondMinHeight": 0,
  "diamondMaxHeight": 16,
  "lapisSize": 7,
  "lapisCount": 1,
  "lapisCenterHeight": 16,
  "lapisSpread": 16
}
```

| 鍵                        | 類型   | 預設      | 作用                                                                                                                                                                                                                                                                           |
| ------------------------- | ------ | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `coordinateScale`         | 浮點數 | `684.412` | 主地形雜訊的水平縮放                                                                                                                                                                                                                                                           |
| `heightScale`             | 浮點數 | `684.412` | 主地形雜訊的垂直縮放                                                                                                                                                                                                                                                           |
| `upperLimitScale`         | 浮點數 | `512.0`   | 從上方限制地形的雜訊縮放                                                                                                                                                                                                                                                       |
| `lowerLimitScale`         | 浮點數 | `512.0`   | 從下方限制地形的雜訊縮放                                                                                                                                                                                                                                                       |
| `depthNoiseScaleX`        | 浮點數 | `200.0`   | 深度雜訊沿 x 軸的縮放                                                                                                                                                                                                                                                          |
| `depthNoiseScaleZ`        | 浮點數 | `200.0`   | 深度雜訊沿 z 軸的縮放                                                                                                                                                                                                                                                          |
| `depthNoiseScaleExponent` | 浮點數 | `0.5`     | 沒有效果：遊戲的深度雜訊會忽略它，所以改動它不會有任何變化。保留它，是為了讓帶有它的自訂世界字串照原樣被讀取                                                                                                                                                                   |
| `mainNoiseScaleX`         | 浮點數 | `80.0`    | 主雜訊沿 x 軸被拉伸的程度                                                                                                                                                                                                                                                      |
| `mainNoiseScaleY`         | 浮點數 | `160.0`   | 主雜訊沿 y 軸被拉伸的程度                                                                                                                                                                                                                                                      |
| `mainNoiseScaleZ`         | 浮點數 | `80.0`    | 主雜訊沿 z 軸被拉伸的程度                                                                                                                                                                                                                                                      |
| `baseSize`                | 浮點數 | `8.5`     | 地形的基礎高度                                                                                                                                                                                                                                                                 |
| `stretchY`                | 浮點數 | `12.0`    | 地形在垂直方向被拉伸的程度                                                                                                                                                                                                                                                     |
| `seaLevel`                | 整數   | `63`      | 海洋填充到的 y 高度                                                                                                                                                                                                                                                            |
| `useLavaOceans`           | 布林值 | `false`   | 用熔岩取代水來填充海洋                                                                                                                                                                                                                                                         |
| `oceanFluid`              | 字串   | 空        | 構成海洋、河流與海平面以下填充物的流體，可以是任何流體 ID，例如 `minecraft:lava`，或資料包中的流體，如 `mypack:molten_ruby`；它必須有流體方塊。留空則使用水，`useLavaOceans` 為 true 時使用熔岩；此鍵優先於 `useLavaOceans`。未載入的流體會記入日誌，改由 `useLavaOceans` 決定 |
| `useWaterLakes`           | 布林值 | `true`    | 是否生成水湖                                                                                                                                                                                                                                                                   |
| `waterLakeChance`         | 整數   | `4`       | 水湖的機率，為幾分之一；數值越大越稀有                                                                                                                                                                                                                                         |
| `useLavaLakes`            | 布林值 | `true`    | 是否生成熔岩湖                                                                                                                                                                                                                                                                 |
| `lavaLakeChance`          | 整數   | `80`      | 熔岩湖的機率，為幾分之一；數值越大越稀有                                                                                                                                                                                                                                       |
| `useCaves`                | 布林值 | `true`    | 是否生成洞穴                                                                                                                                                                                                                                                                   |
| `useRavines`              | 布林值 | `true`    | 是否生成峽谷                                                                                                                                                                                                                                                                   |
| `useDungeons`             | 布林值 | `true`    | 是否生成地城                                                                                                                                                                                                                                                                   |
| `dungeonChance`           | 整數   | `8`       | 每個區塊的地城嘗試次數                                                                                                                                                                                                                                                         |
| `useStrongholds`          | 布林值 | `true`    | 是否生成要塞                                                                                                                                                                                                                                                                   |
| `useVillages`             | 布林值 | `true`    | 是否生成村莊                                                                                                                                                                                                                                                                   |
| `useMineShafts`           | 布林值 | `true`    | 是否生成廢棄礦坑                                                                                                                                                                                                                                                               |
| `useTemples`              | 布林值 | `true`    | 是否生成神殿、冰屋和女巫小屋                                                                                                                                                                                                                                                   |
| `useMonuments`            | 布林值 | `true`    | 是否生成海底神殿                                                                                                                                                                                                                                                               |
| `useMansions`             | 布林值 | `true`    | 是否生成林地府邸                                                                                                                                                                                                                                                               |
| `biomeDepthWeight`        | 浮點數 | `1.0`     | 每個生態域自身基礎高度的權重                                                                                                                                                                                                                                                   |
| `biomeDepthOffset`        | 浮點數 | `0.0`     | 加到每個生態域基礎高度上的偏移                                                                                                                                                                                                                                                 |
| `biomeScaleWeight`        | 浮點數 | `1.0`     | 每個生態域自身高度起伏的權重                                                                                                                                                                                                                                                   |
| `biomeScaleOffset`        | 浮點數 | `0.0`     | 加到每個生態域高度起伏上的偏移                                                                                                                                                                                                                                                 |
| `fixedBiome`              | 整數   | `-1`      | 每一列所用的生態域數字 id；-1 表示不指定                                                                                                                                                                                                                                       |
| `biomeSize`               | 整數   | `4`       | 生態域大小，以縮放步數計，一般為 4，大型生態域為 6                                                                                                                                                                                                                             |
| `riverSize`               | 整數   | `4`       | 河流大小，以縮放步數計                                                                                                                                                                                                                                                         |
| `dirtSize`                | 整數   | `33`      | 泥土：礦脈大小（方塊數）                                                                                                                                                                                                                                                       |
| `dirtCount`               | 整數   | `10`      | 泥土：每個區塊的礦脈數                                                                                                                                                                                                                                                         |
| `dirtMinHeight`           | 整數   | `0`       | 泥土：最低 y 高度                                                                                                                                                                                                                                                              |
| `dirtMaxHeight`           | 整數   | `256`     | 泥土：最高 y 高度                                                                                                                                                                                                                                                              |
| `gravelSize`              | 整數   | `33`      | 礫石：礦脈大小（方塊數）                                                                                                                                                                                                                                                       |
| `gravelCount`             | 整數   | `8`       | 礫石：每個區塊的礦脈數                                                                                                                                                                                                                                                         |
| `gravelMinHeight`         | 整數   | `0`       | 礫石：最低 y 高度                                                                                                                                                                                                                                                              |
| `gravelMaxHeight`         | 整數   | `256`     | 礫石：最高 y 高度                                                                                                                                                                                                                                                              |
| `graniteSize`             | 整數   | `33`      | 花崗岩：礦脈大小（方塊數）                                                                                                                                                                                                                                                     |
| `graniteCount`            | 整數   | `10`      | 花崗岩：每個區塊的礦脈數                                                                                                                                                                                                                                                       |
| `graniteMinHeight`        | 整數   | `0`       | 花崗岩：最低 y 高度                                                                                                                                                                                                                                                            |
| `graniteMaxHeight`        | 整數   | `80`      | 花崗岩：最高 y 高度                                                                                                                                                                                                                                                            |
| `dioriteSize`             | 整數   | `33`      | 閃長岩：礦脈大小（方塊數）                                                                                                                                                                                                                                                     |
| `dioriteCount`            | 整數   | `10`      | 閃長岩：每個區塊的礦脈數                                                                                                                                                                                                                                                       |
| `dioriteMinHeight`        | 整數   | `0`       | 閃長岩：最低 y 高度                                                                                                                                                                                                                                                            |
| `dioriteMaxHeight`        | 整數   | `80`      | 閃長岩：最高 y 高度                                                                                                                                                                                                                                                            |
| `andesiteSize`            | 整數   | `33`      | 安山岩：礦脈大小（方塊數）                                                                                                                                                                                                                                                     |
| `andesiteCount`           | 整數   | `10`      | 安山岩：每個區塊的礦脈數                                                                                                                                                                                                                                                       |
| `andesiteMinHeight`       | 整數   | `0`       | 安山岩：最低 y 高度                                                                                                                                                                                                                                                            |
| `andesiteMaxHeight`       | 整數   | `80`      | 安山岩：最高 y 高度                                                                                                                                                                                                                                                            |
| `coalSize`                | 整數   | `17`      | 煤炭：礦脈大小（方塊數）                                                                                                                                                                                                                                                       |
| `coalCount`               | 整數   | `20`      | 煤炭：每個區塊的礦脈數                                                                                                                                                                                                                                                         |
| `coalMinHeight`           | 整數   | `0`       | 煤炭：最低 y 高度                                                                                                                                                                                                                                                              |
| `coalMaxHeight`           | 整數   | `128`     | 煤炭：最高 y 高度                                                                                                                                                                                                                                                              |
| `ironSize`                | 整數   | `9`       | 鐵：礦脈大小（方塊數）                                                                                                                                                                                                                                                         |
| `ironCount`               | 整數   | `20`      | 鐵：每個區塊的礦脈數                                                                                                                                                                                                                                                           |
| `ironMinHeight`           | 整數   | `0`       | 鐵：最低 y 高度                                                                                                                                                                                                                                                                |
| `ironMaxHeight`           | 整數   | `64`      | 鐵：最高 y 高度                                                                                                                                                                                                                                                                |
| `goldSize`                | 整數   | `9`       | 金：礦脈大小（方塊數）                                                                                                                                                                                                                                                         |
| `goldCount`               | 整數   | `2`       | 金：每個區塊的礦脈數                                                                                                                                                                                                                                                           |
| `goldMinHeight`           | 整數   | `0`       | 金：最低 y 高度                                                                                                                                                                                                                                                                |
| `goldMaxHeight`           | 整數   | `32`      | 金：最高 y 高度                                                                                                                                                                                                                                                                |
| `redstoneSize`            | 整數   | `8`       | 紅石：礦脈大小（方塊數）                                                                                                                                                                                                                                                       |
| `redstoneCount`           | 整數   | `8`       | 紅石：每個區塊的礦脈數                                                                                                                                                                                                                                                         |
| `redstoneMinHeight`       | 整數   | `0`       | 紅石：最低 y 高度                                                                                                                                                                                                                                                              |
| `redstoneMaxHeight`       | 整數   | `16`      | 紅石：最高 y 高度                                                                                                                                                                                                                                                              |
| `diamondSize`             | 整數   | `8`       | 鑽石：礦脈大小（方塊數）                                                                                                                                                                                                                                                       |
| `diamondCount`            | 整數   | `1`       | 鑽石：每個區塊的礦脈數                                                                                                                                                                                                                                                         |
| `diamondMinHeight`        | 整數   | `0`       | 鑽石：最低 y 高度                                                                                                                                                                                                                                                              |
| `diamondMaxHeight`        | 整數   | `16`      | 鑽石：最高 y 高度                                                                                                                                                                                                                                                              |
| `lapisSize`               | 整數   | `7`       | 青金石：礦脈大小（方塊數）                                                                                                                                                                                                                                                     |
| `lapisCount`              | 整數   | `1`       | 青金石：每個區塊的礦脈數                                                                                                                                                                                                                                                       |
| `lapisCenterHeight`       | 整數   | `16`      | 青金石礦脈的中心所在的 y 高度                                                                                                                                                                                                                                                  |
| `lapisSpread`             | 整數   | `16`      | 青金石礦脈在中心上下延伸的範圍                                                                                                                                                                                                                                                 |

### 伺服器

*各群組的作用*

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

`control.server` 決定這個分組：資源包可以設定的 `server.properties` 各行，以及遊戲模式、難易度，和對區域網路開放的世界中的指令。在專用伺服器上，資源包在此設定的每個值，都會在伺服器啟動時寫入 `server.properties`，所以該檔案寫明了目前生效的內容，伺服器已經讀取過的那些值也會一併設定到它身上。單人世界則取用整合伺服器所擁有的內容，各列的說明中有寫明。留空，或數字寫 `-1`，則保留伺服器本身的值，而 `control.server` 設為 `off` 時，每一行都維持伺服器原有的樣子。

| 設定                   | 類型                                                           | 預設   | 作用                                                                                                                                                                                                                                                                                              |
| ---------------------- | -------------------------------------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldGameMode`        | `survival`、`hardcore`、`creative`、`adventure` 或 `spectator` | 空     | 每個新世界起始的模式，在單人遊戲中只在建立時套用。專用伺服器每次啟動都會把每個世界設為其 `server.properties` 中的模式，所以在那裡，資源包的模式會在世界載入之前寫入 `server.properties`（`gamemode` 和 `hardcore`）。`hardcore` 是生存模式加上整個存檔的極限模式旗標，`creative` 還會一併啟用作弊 |
| `worldLanCommands`     | 布林值                                                         | `true` | 在單人世界中對區域網路開放的玩家，是否可以為所有加入的人開啟指令。`false` 會讓「對區域網路開放」畫面中的「允許作弊」按鈕變灰並保持關閉，而且不論怎麼要求，包括 `/publish`，該世界都會在沒有指令的情況下開放                                                                                       |
| `worldDifficulty`      | 清單                                                           | 無     | 把難易度鎖定為 `peaceful`、`easy`、`normal` 或 `hard`。單獨寫一個難易度會涵蓋每個維度，而 `dimension=difficulty` 這一行則會為該維度覆寫它。專用伺服器會把主世界的難易度作為 `difficulty` 寫入 `server.properties`                                                                                 |
| `worldForceGameMode`   | 布林值                                                         | 空     | 加入的玩家是否每次都被放回伺服器的遊戲模式，也就是 `force-gamemode` 那一行。對區域網路開放的單人世界也會採用它                                                                                                                                                                                    |
| `worldPvp`             | 布林值                                                         | 空     | 玩家之間是否可以互相傷害，也就是 `pvp` 那一行。單人世界也會採用它                                                                                                                                                                                                                                 |
| `worldFlight`          | 布林值                                                         | 空     | 在生存模式中飛行的玩家是否被放過而不是被踢出，也就是 `allow-flight` 那一行。單人世界也會採用它                                                                                                                                                                                                    |
| `worldSpawnProtection` | 整數，-1 以上                                                  | `-1`   | 出生點周圍多少格內只有管理員可以建造，也就是 `spawn-protection` 那一行，0 表示沒有。只有專用伺服器會保護它的出生點                                                                                                                                                                                |
| `worldNether`          | 布林值                                                         | 空     | 是否可以進入地獄，也就是 `allow-nether` 那一行。`false` 在單人世界中也會把它關閉                                                                                                                                                                                                                  |
| `worldCommandBlocks`   | 布林值                                                         | 空     | 指令方塊是否會執行，也就是 `enable-command-block` 那一行。單人世界本來就會執行它們，而 `false` 在那裡也會把它們關閉                                                                                                                                                                               |
| `worldIdleTimeout`     | 整數，-1 以上                                                  | `-1`   | 玩家閒置多少分鐘後會被踢出，也就是 `player-idle-timeout` 那一行，0 表示永不。單人世界也會採用它                                                                                                                                                                                                   |
| `worldMotd`            | 文字                                                           | 空     | 在伺服器清單中顯示於伺服器名稱下方的那一行，也就是 `motd` 那一行。對區域網路開放的單人世界會用它取代擁有者和世界名稱來顯示                                                                                                                                                                        |
| `worldMaxSize`         | 整數，-1 到 29999984                                           | `-1`   | 世界邊界最遠可以延伸到距離中心多遠（以方塊計），也就是 `max-world-size` 那一行。單人世界也會採用它                                                                                                                                                                                                |
| `worldStructures`      | 布林值                                                         | 空     | 新世界是否生成結構，也就是 `generate-structures` 那一行，以及世界畫面上的「生成結構」選項。只在世界建立時套用                                                                                                                                                                                     |
| `worldSpawnMonsters`   | 布林值                                                         | 空     | 敵對生物是否生成，也就是 `spawn-monsters` 那一行。`false` 在單人世界中也會阻止它們                                                                                                                                                                                                                |
| `worldSpawnAnimals`    | 布林值                                                         | 空     | 動物是否生成，也就是 `spawn-animals` 那一行。單人世界也會採用它                                                                                                                                                                                                                                   |
| `worldSpawnNpcs`       | 布林值                                                         | 空     | 村民是否生成，也就是 `spawn-npcs` 那一行。單人世界也會採用它                                                                                                                                                                                                                                      |
| `worldViewDistance`    | 整數，-1 到 32                                                 | `-1`   | 專用伺服器向每位玩家傳送世界的範圍有多少個區塊，也就是 `view-distance` 那一行。單人世界則依照彩現距離                                                                                                                                                                                             |
| `worldBuildHeight`     | 整數，-1 到 256                                                | `-1`   | 方塊可以放置的最高 y，也就是 `max-build-height` 那一行，會調整為 64 到 256 之間 16 的倍數。單人世界也會採用它                                                                                                                                                                                     |

**`worldGameMode`**（`server` 分組）：`survival`、`hardcore`、`creative`、`adventure` 或 `spectator`。只在建立世界時套用；已存在的世界不受影響，之後更改模式也不會被動到。`hardcore` 是生存模式加上原版整個存檔的極限模式旗標；`creative` 還會一併啟用作弊，就像建立畫面上的核取方塊那樣。建立畫面開啟時會預先選好該模式（以及資源包的種子碼）；玩家可以在那裡更改，但資源包會在建立時把它改回來。`adventure` 和 `spectator` 不會出現在該畫面上，而是在世界建立時直接套用。

**`worldLanCommands`**（`server` 分組）：`true`（預設）讓「對區域網路開放」畫面維持原版的樣子。`false` 會讓其中的「允許作弊」按鈕變灰並保持關閉，所以對區域網路開放世界的玩家，無法把指令交給每個加入的人。不論是什麼要求指令，包括 `/publish`，世界在伺服器端也都會在沒有指令的情況下開放。它只管轄對區域網路開放這件事；專用伺服器不受影響。

**`worldDifficulty`**（`server` 分組）：`peaceful`、`easy`、`normal` 或 `hard`。單獨寫一個值會涵蓋每個維度；`dimension=difficulty` 這幾行（`-1=hard`）則依維度個別覆寫。這個鎖定在暫停選單中同樣有效。留空（預設）則把難易度交給玩家決定。

### 日誌記錄

*各群組的作用*

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

| 設定                   | 類型   | 預設   | 作用                                                   |
| ---------------------- | ------ | ------ | ------------------------------------------------------ |
| `logBlockedOres`       | 布林值 | `true` | 在每個模組和礦石類型第一次被擋下時記錄                 |
| `logBlockedBiomes`     | 布林值 | `true` | 記錄各模組有哪些生態域被封鎖的數量                     |
| `logBlockedGenerators` | 布林值 | `true` | 在每個模組和生成器第一次被封鎖時記錄                   |
| `logBlockedRecipes`    | 布林值 | `true` | 記錄各模組有多少配方被封鎖的數量                       |
| `logBlockReplacements` | 布林值 | `true` | 在每項取代第一次進行時記錄，並在世界補上進度時記錄總數 |

`logBlockedOres`、`logBlockedBiomes`、`logBlockedRecipes` 和 `logBlockReplacements` 各自會在某樣東西第一次被擋下時記錄，讓你能看到封鎖規則實際攔到了什麼，而不必憑缺少的東西去猜。當某條規則似乎什麼都沒做，或做得太多時，它們是最先該開啟的東西。

### 配方

*各群組的作用*

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

| 設定                  | 類型                         | 預設        | 作用                                                                                                                                                                                                                                   |
| --------------------- | ---------------------------- | ----------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blockRecipes`        | 布林值                       | `false`     | 移除所有合成配方，只保留 `recipeWhitelist` 中模組的配方。白名單預設只有 `minecraft`，所以原版配方會保留，其他所有模組的配方都會被移除；要列出你自己資源包的命名空間，才能保留它的配方。CraftTweaker 和 GroovyScript 新增的配方一律保留 |
| `recipeWhitelist`     | 模組 id 清單                 | `minecraft` | 合成配方得以保留的模組                                                                                                                                                                                                                 |
| `blockedRecipeMods`   | 模組 id 清單                 | 無          | 合成配方被直接移除的模組，不論白名單怎麼寫                                                                                                                                                                                             |
| `blockFurnaceRecipes` | 布林值                       | `false`     | 對熔爐配方做同樣的事，模組由產出的物品來判讀                                                                                                                                                                                           |
| `furnaceWhitelist`    | 模組 id 清單                 | `minecraft` | 熔爐配方得以保留的模組                                                                                                                                                                                                                 |
| `blockedFurnaceMods`  | 模組 id 清單                 | 無          | 熔爐配方被直接移除的模組                                                                                                                                                                                                               |
| `recipeMatch`         | `recipe`、`output` 或 `both` | `recipe`    | 封鎖合成配方時，從哪裡判讀模組 id：配方本身的名稱、它所製作的物品，或兩者皆可，兩者皆可時只要任一符合就封鎖，只要任一在白名單中就放過                                                                                                  |

`blockRecipes` 和 `blockFurnaceRecipes` 會移除所有東西，只留下白名單中的模組。兩個白名單預設都只有 `minecraft`，所以原版配方會保留，其他所有模組的配方都會被移除；要列出你自己資源包的命名空間，才能保留它的配方。CraftTweaker 和 GroovyScript 新增的配方，不論白名單怎麼寫都一律保留。白名單是 `recipeWhitelist` 和 `furnaceWhitelist`；`blockedRecipeMods` 和 `blockedFurnaceMods` 則反過來，不論白名單怎麼寫，都會移除所指名模組的配方。`recipeMatch` 決定封鎖合成配方時，從哪裡判讀模組 id：`recipe`（預設）使用配方本身的名稱，`output` 使用它所製作的物品，`both` 則是任一符合就封鎖，任一在白名單中就放過。

---

# 其他模組

## Universal Tweaks

*其他模組*

Universal Tweaks 與本模組的數項原版調整重疊。在重疊之處，本模組會退讓（每次都會記錄，並指出略過了什麼），而不是讓兩個模組去修改同一個方法。

| 重疊的項目        | 本模組何時讓步                            |
| ----------------- | ----------------------------------------- |
| `promptLeafDecay` | Universal Tweaks 開啟了 `Fast Leaf Decay` |
| `lenientPaths`    | Universal Tweaks 開啟了 `Lenient Paths`   |
| `cactusMaxHeight` | 已安裝 Universal Tweaks                   |
| `caneMaxHeight`   | 已安裝 Universal Tweaks                   |
| 地獄傳送門返回    | 已安裝 Universal Tweaks                   |

前兩項會從 `config/Universal Tweaks - Tweaks.cfg` 讀取 Universal Tweaks 自己的開關，所以在那裡把其中一項關掉，就會把那項工作交還給本模組。高度這一對沒有這種開關可讀，只有 `Cactus Size` 和 `Sugar Cane Size`，所以只要有 Universal Tweaks 存在，本模組就會讓步，你要改在那裡設定高度。

**地獄傳送門返回**：本模組會記錄你進入地獄的位置，並讓你回到那裡，而不是採用原版的最近傳送門搜尋。Universal Tweaks 有自己的處理方式，所以只要安裝了它，這項就會被完全略過。

**這一切都不會影響資源包。** 上面所說的一切，都是針對 Minecraft 自己的仙人掌、甘蔗、樹葉、小徑和傳送門。你的資源包所定義的方塊帶有它們自己的行為，而 `portals/*.json` 下的資源包傳送門是另一套系統，Universal Tweaks 完全看不到。

## Mo' Villages

*其他模組*

Mo' Villages 新增村莊生態域，並替換村莊的材料，這兩件事資源包也都可以設定。與 Universal Tweaks 的重疊不同，這裡是資源包擁有最後決定權。

| 重疊的項目                | 會發生什麼事                                                                                                                                     |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| 村莊的 `structureSpacing` | Mo' Villages 在本模組提出要求之後，會依 `villageDistance` 設定自己的間距。如果資源包指定了間距，本模組會把資源包的數字放回去，並在日誌中說明一次 |
| `villageBlocks`           | Mo' Villages 依生態域替換村莊材料，並把替換標記為最終結果。資源包的對應表會在那之後套用，所以資源包優先                                          |
| 村莊的 `structureBiomes`  | Mo' Villages 把它的生態域加進遊戲自己的清單中。資源包的白名單仍然決定什麼能留下                                                                  |

這裡沒有任何東西需要開啟。如果資源包沒有指定間距，也沒有指定方塊對應表，Mo' Villages 就會維持原樣，任它隨意運作。

兩者同時安裝時，有兩件事值得知道。Mo' Villages 也會設定 `minTownSeparation`，這在 1.12 中完全沒有作用：這個欄位寫入一次後，遊戲和本模組都從不讀取。另外，村莊的方塊是由 Mo' Villages 在 `villageBlocks` 執行之前依生態域決定的，所以同時對應原本的方塊和 Mo' Villages 替換成的方塊，就能涵蓋任一種村莊，也就是 `minecraft:cobblestone=...` 和 `minecraft:brick_block=...` 一起寫。

## CoFH World

*其他模組*

需要 CoFH World 的模組在沒有它的情況下也能載入，這項需求會被自動移除，但真正呼叫其 API 而會當機的模組除外。

它們自己的生成於是不會發生，因為讀取它們 `assets/<modid>/world/*.json` 的正是 CoFH World。資源包應當負責補上這一塊。

若不這麼做，`readCofhWorldFiles` 會直接從模組 jar 中讀取這些檔案，並透過本模組來生成它們。它預設是關閉的，而且在安裝了真正的 CoFH World 時會退讓，由後者照常生成。每個會產出東西的 CoFH 生成器和分佈都會被轉換，對應到上面的形狀與分佈方式。這些形狀是本模組自己的幾何，所以湖或尖塔看起來不會完全一樣。加權的結構清單、旋轉與鏡像表、忽略方塊清單，以及石筍的錐度全都會一併帶過來。錐度是依形狀而非公式來比對，所以尖塔的輪廓很接近，但並不完全相同。

把這些檔案轉寫成資源包是受支援的做法，也是改變它們所生成內容的唯一途徑。

## Lost Cities

*其他模組*

Lost Cities 用自己的生成器取代主世界生成器，所以任何接在一般生成器上的東西，在它的世界裡都會失效。只在安裝了 Lost Cities 時才載入的相容程式碼，會帶過來三樣東西：

- `generatorOptions` 塑造城市之間與城市之下的陸地。Lost Cities 只讀取雜訊設定，所以地面高度、水位、洞穴、湖泊和結構開關都來自它自己的設定檔，`seaLevel` 在它的世界裡不起作用；日誌中的摘要也會這麼說。`terrainWorldTypes` 像對待其他類型一樣管控它，以 `lostcities` 來比對。
- 虛空世界可以運作，完全封鎖生態域清單所造成的虛空世界也包括在內。城市和陸地都不見了，而平台和出生點的行為與其他地方相同。
- 資源包生態域的 `stoneBlock` 會取代其下的石頭，Lost Cities 擁有的每一種地貌類型都適用，包括一般、浮空、太空和洞窟。

城市本身不是本模組可以更動的。它們有多大、多常見，建築由什麼構成，地面和水位，這一切都在 `config/lostcities` 下 Lost Cities 自己的設定檔裡，它的建築 JSON 則透過同一處的 `assets` 設定來處理。提供 Lost Cities 世界的資源包，會把這些檔案一併附上，就像附上任何其他模組的設定一樣。

把 `worldType` 設為 `lostcities`，會讓每個新世界都成為 Lost Cities 世界，就像讓它成為 `biomesop` 或 `realistic` 一樣。強制指定類型會捨棄世界原本會帶有的設定，所以世界會落在 Lost Cities 的預設設定檔上，而 `config/lostcities/general.cfg` 中的 `defaultProfile` 指明那是哪一個。反過來說，強制指定其他類型的資源包，會讓選了 Lost Cities 的玩家失去它，所以打算讓這個選擇保持開放的資源包，要把 `lostcities` 加進 `worldTypeExceptions`。

其他一切本來就沒經過生成器，運作方式與任何地方都相同：資源包的世界生成、礦石與生態域封鎖、結構間距與生成器、平整基岩、回溯生成、預先生成，以及它的兩個箱子戰利品表，都會像其他戰利品表一樣覆寫與注入。

## Blast Plaster 整合

*其他模組*

`<namespace>/blastplaster/*.json`

檔案名稱由你決定，只會讀取這個資料夾，而且多個檔案會疊加。

Blast Plaster（本模組的相依項目）負責處理爆炸後的行為：逐個方塊修復彈坑、能辨識樹木的砍伐、掉落物控制。它本身只讀取一份全域設定。由資源包驅動時，它會**依維度**回應，由資源包提供決定，而不是要玩家去編輯設定檔。村莊的伐樹也沿用它的樹木幾何，這就是為什麼新道路上方的樹會整棵倒下。沒有資源包檔案時，Blast Plaster 的行為與單獨安裝時完全相同。

寫在檔案最上層的鍵會套用到所有地方；`dimensions` 區段則依維度 id 為單一維度覆寫它們。資源包沒有指名的任何項目，都保留 Blast Plaster 自己設定檔所寫的內容，所以資源包只設定它在意的那幾項，其餘不去動。

一次列出所有的鍵。實際的檔案只會寫它需要的那些。

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

`explosionMode` 是主要的開關：`HEAL` 會隨時間修復彈坑，`EJECT_DROPS` 會留下坑洞並掉落大約三分之一的方塊（原版行為），`VISUAL_TOSS` 會留下坑洞且什麼都不掉落。由資源包驅動時，預設值是 `EJECT_DROPS`（而不是 Blast Plaster 的 `HEAL`），所以未設定的安裝行為與原版相同。

| 鍵                                                                                                          | 值                                   | 作用                                                               |
| ----------------------------------------------------------------------------------------------------------- | ------------------------------------ | ------------------------------------------------------------------ |
| `explosionMode`                                                                                             | `HEAL`、`EJECT_DROPS`、`VISUAL_TOSS` | 爆炸之後會發生什麼                                                 |
| `healCreepers`、`healNonPlayerTNT`、`healWither`、`healAll`                                                 | true 或 false                        | 哪些爆炸會被處理                                                   |
| `processPlayerIgnitedTNT`                                                                                   | true 或 false                        | 玩家點燃的 TNT 是否與其餘的一起處理                                |
| `customEntitiesToHeal`                                                                                      | 實體名稱清單                         | 來自其他模組的爆炸，寫成 `modid:entity`                            |
| `healFullTrees`                                                                                             | true 或 false                        | 被爆炸削到的樹會整棵被取走或復原，而不是被攔腰截斷                 |
| `maxTreeSize`                                                                                               | 數字                                 | 一棵樹最多可佔用多少方塊，超過就放著不管                           |
| `minimumTicksBeforeHeal`、`randomTickVar`                                                                   | 數字                                 | 多久之後才開始修復，以及修復的步調有多不規則                       |
| `overrideBlocks`                                                                                            | true 或 false                        | 修復時是否覆蓋坑洞裡後來蓋上的東西                                 |
| `enableFakeTossedBlocks`                                                                                    | true 或 false                        | 從爆炸中飛出的碎片                                                 |
| `enableExplosionFlash`                                                                                      | true 或 false                        | 爆炸瞬間的明亮閃光                                                 |
| `explosionFlashDuration`、`explosionFlashLightLevel`、`explosionFlashParticleCount`、`explosionFlashPulses` | 數字                                 | 閃光持續多久、燃燒得多亮、噴出多少粒子，以及脈動幾次               |
| `enableExplosionSmoke`                                                                                      | true 或 false                        | 事後升起的煙柱                                                     |
| `explosionSmokeDuration`、`explosionSmokeParticleCount`                                                     | 數字                                 | 煙霧停留多久，以及有多濃                                           |
| `playerTNTAlwaysDrops`、`playerTNTDropFullBlocks`                                                           | true 或 false                        | 玩家自己的 TNT 會留下什麼                                          |
| `enableDropSuppression`、`dtSpecialDrops`                                                                   | true 或 false                        | 爆炸範圍內的掉落物，以及 Dynamic Trees 自己的掉落物                |
| `preventMobDrops`                                                                                           | true 或 false                        | 被爆炸炸死的生物是否仍會掉落物品                                   |
| `blockConversions`                                                                                          | 規則清單                             | 被炸到的方塊會變成什麼，而不是照原樣回來，讓建築每炸一次就磨損一級 |

`blockConversions` 決定被炸到的方塊會變成什麼，而不是照原樣回來。規則的寫法是 `<source>=<result>[@chance]`：來源是方塊 id、帶有 meta 的方塊 id（`minecraft:log:1`），或前面加 `#` 的礦物字典名稱；結果是方塊 id、帶有 meta 的方塊 id，或是 `nothing`，表示讓該處留空；機率從 0.0 到 1.0，預設為 1.0。第一條符合的規則優先，所以具體的規則要放在廣泛的規則之上，而已經是某條規則結果的方塊，不會再被轉換，所以一面牆每炸一次只會退一級，而不是磨損到什麼都不剩。

**完全原版的外觀：** `EJECT_DROPS`，加上 `healFullTrees`、`enableFakeTossedBlocks`、`enableExplosionFlash`、`enableExplosionSmoke`、`preventMobDrops` 和 `playerTNTAlwaysDrops` 全部關閉。每個鍵都可以依維度設定。

**原版用戶端**看不到任何異樣。閃光是唯一會放置方塊的功能，所以設定了 `vanillaClients` 時它會被強制關閉；其餘全是原版用戶端看得懂的粒子和物品。

不是資源包鍵的項目：Blast Plaster 的除錯日誌，以及它把原木對應到樹葉的配對（樹木的辨識在整個遊戲中必須只有一個答案）。兩者都留在 Blast Plaster 自己的設定檔中。

## 墓碑模組

*其他模組*

不需要任何設定。`player_loot` 物品會在任何墓碑模組讀取之前，併入一般的死亡掉落物，所以它們會跟著物品欄一起進入墓碑，Gravestone、GraveStone Mod、Corail Tombstone 以及其他任何讀取死亡掉落清單的模組都適用。每個項目中的 `dropLoose` 會繞過掉落清單，讓物品散落在地上給擊殺者，而不是進入墓碑。鍵以及 `dropLoose` 的注意事項：[玩家戰利品](#玩家戰利品)。

---

# 參考資料

## 值清單

*值清單*

下方表格中凡是寫著「其中一種材料」之類的地方，解析器都接受以下這些名稱。無法辨識的名稱會記入日誌，並以預設值取代。

### 接受的名稱

*值清單*

**方塊材料。** `air`、`grass`、`ground`、`wood`、`rock`、`iron`、`anvil`、`water`、`lava`、`leaves`、`plants`、`vine`、`sponge`、`cloth`、`fire`、`sand`、`circuits`、`carpet`、`glass`、`redstone_light`、`tnt`、`coral`、`ice`、`packed_ice`、`snow`、`crafted_snow`、`cactus`、`clay`、`gourd`、`dragon_egg`、`portal`、`cake`、`web`、`piston`、`barrier`、`structure_void`。

**音效類型。** `wood`、`ground`、`plant`、`stone`、`metal`、`glass`、`cloth`、`sand`、`snow`、`ladder`、`anvil`、`slime`。

**地圖顏色。** `air`、`grass`、`sand`、`cloth`、`tnt`、`ice`、`iron`、`foliage`、`snow`、`clay`、`dirt`、`stone`、`water`、`wood`、`quartz`、`adobe`、`magenta`、`light_blue`、`yellow`、`lime`、`pink`、`gray`、`silver`、`cyan`、`purple`、`blue`、`brown`、`green`、`red`、`black`、`gold`、`diamond`、`lapis`、`emerald`、`obsidian`、`netherrack`。

**彩現層。** `solid`、`cutout`、`cutout_mipped`、`translucent`。留空時，方塊會依其類型挑選合適的一個。

**稀有度。** `common`、`uncommon`、`rare`、`epic`。

**火把粒子。** `none`、`flame`、`colored`。`colored` 使用 `particleColor`。

**工具類別。** `pickaxe`、`axe`、`shovel`、`sword`。

**盔甲欄位。** `head` 或 `helmet`、`chest` 或 `chestplate`、`legs` 或 `leggings`、`feet` 或 `boots`。

**色調。** `biome`、`none`，或六位數的十六進位顏色。定義中任何地方的顏色都是十六進位，前面可以有或沒有 `#`。

**行為**，用於 `behavesAs`。`till`、`path`、`bush`、`animals`。

**植物類型**，用於 `plantTypes`。`plains`、`desert`、`beach`、`cave`、`water`、`nether`、`crop`。

**結構**，用於世界範本，以及 `structures` 分組自己的清單。`villages`、`mineshafts`、`strongholds`、`temples`、`monuments`、`mansions`、`netherbridges`、`endcities`、`caves`、`ravines`，還有 `reccomplex`，它會關閉 Recurrent Complex 自行生成的一切，包括它的天然結構和裝飾替身，而世界中已經存在的東西則不受影響。另有八個名稱指的是填充步驟所放置的東西，而不是結構生成器：`dungeons`、`waterlakes`、`lavalakes`、`netherlava`、`fire`、`glowstone`、`ice` 和 `animals`。

**生物類型**，用於生態域的生成與生成率。`creature`、`monster`、`ambient`、`water_creature`。

**角色**，用於世界範本的 `roles`。`ocean`、`river`、`beach`、`mushroom`、`swamp`、`hills`、`mountain`、`jungle`、`forest`、`savanna`、`sandy`、`mesa`、`snowy`、`wasteland`、`plains`、`water`。每一個都指名一個擔任該角色的生態域，在封鎖移除了原本會擔任該角色的生態域之後才補上。

**礦石類型**，用於 `oreTypes`。`COAL`、`IRON`、`GOLD`、`REDSTONE`、`DIAMOND`、`LAPIS`、`EMERALD`、`QUARTZ`、`DIRT`、`GRAVEL`、`DIORITE`、`GRANITE`、`ANDESITE`、`SILVERFISH`、`CUSTOM`。

### 世界設定

*值清單*

下列 `terrain` 鍵，一起寫在世界範本的 `settings` 區段中：

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

| 設定             | 類型               | 預設 | 作用                                                                                                                                        |
| ---------------- | ------------------ | ---- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldName`      | 字串               | 空   | 預先填入建立世界畫面的名稱欄，存檔資料夾隨之而定。只有在欄位仍是遊戲預設值時才會填入，而且不像種子碼和遊戲模式，之後不會再重新套用          |
| `worldSpawn`     | `x,z` 或 `x,y,z`   | 空   | 每個新世界的出生位置，只在建立時套用。沒有寫 y 時，採用該世界類型地面高度處的地表                                                           |
| `worldBorder`    | 整數，單位為格     | `0`  | 每個新世界所獲得的邊界直徑，也就是 `/worldborder set` 所接受的數值。`0` 表示不動邊界                                                        |
| `worldTime`      | 整數，單位為刻     | `-1` | 主世界被鎖定的時間，單位為刻，即 /time set 接受的數值，所以 18000 是午夜。時鐘會停住、永不走動，新世界和既有世界都一樣。`-1` 讓時間照常流動 |
| `weatherCeiling` | `dimension=y` 清單 | 無   | 雨和雪所能到達的最高 y。單獨寫一個數字會涵蓋每個維度                                                                                        |
| `cloudHeight`    | `dimension=y` 清單 | 無   | 雲繪製所在的 y。單獨寫一個數字會涵蓋每個維度，留空則保留遊戲本身的高度                                                                      |

**`worldName`**（`terrain` 分組）預先填入建立世界畫面的名稱欄；存檔資料夾照常隨之而定。只有在欄位仍是遊戲預設值時才會填入，所以玩家輸入的名稱絕不會被覆蓋，而且不像種子碼和遊戲模式，之後不會再重新套用：建立時欄位裡是什麼，名稱就是什麼。

**`worldSpawn`**（`terrain` 分組）：`x,z` 或 `x,y,z`。只在建立時套用。沒有寫 y 時，採用該世界類型地面高度處的地表。非整數的項目會被回報並忽略。對超平坦尤其重要：原版的出生點搜尋會在海平面找草方塊，在圖層堆疊上永遠找不到，可能會漫遊到幾百格之外，`worldSpawn` 可以把它固定住。

**`worldBorder`**（`terrain` 分組）：邊界直徑，單位為格，也就是 `/worldborder set` 所接受的數值。在建立時套用；`0`（預設）表示不動邊界；之後仍然可以用指令移動它。設定檔中的 `worldBorderLimit` 限制資源包可以要求的上限，要求超過的資源包會被拒絕並記入日誌，而不是被截斷，所以資源包無法交給伺服器一個管理員沒有同意的邊界。

**`worldTime`**（`terrain` 分組）：與 `/time set` 所接受的相同的刻數值（`18000` 是午夜，`6000` 是正午）。鎖定主世界的時鐘；所有讀取一天中時間的東西（生物生成、睡覺）看到的都是被鎖定的值。`-1`（預設）讓時間照常流動。它是自訂維度 `fixedTime` 在主世界的對應項，並且獨立於 `doDaylightCycle`。

**`cloudHeight`**（`terrain` 分組）：雲繪製所在的 y。單獨寫一個數字會涵蓋每個維度；`dimension=y` 這幾行（`0=384`）則依維度個別覆寫。擁有高樓的資源包會設定它，讓天際線站在雲下，而不是穿過雲層；在 rubic 世界中它是絕對的 y，所以提高的天花板是把雲放在其上的位置。留空（預設）則保留遊戲本身的高度，在主世界是 128，在 rubic 世界中隨 `terrainOffset` 上移。

**`weatherCeiling`**（`terrain` 分組）：雨和雪所能到達的最高 y。單獨寫一個數字會涵蓋每個維度；`dimension=y` 這幾行（`0=128`）則依維度個別覆寫。在它之上不會下雨，雪不會堆積，鍋爐不會裝水，閃電不會落下，也不會繪製任何降水；在它之下，天氣不變。留空（預設）表示沒有上限。冰取決於溫度而不是降水，所以在這條線之上仍然會結冰。

### 世界物理

*值清單*

**世界物理**，四個 `terrain` 鍵，每個都是原版的倍率（`1.0` 表示不變），每個都可以單獨寫一個值套用於所有維度，或用 `dimension=value` 個別覆寫：

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

| 設定                    | 縮放的對象                                                                   | 備註                                                                                             |
| ----------------------- | ---------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| `worldGravity`          | 玩家、生物、掉落物、掉落中的方塊、箭、投擲出的實體、TNT 和經驗球的下墜加速度 | `0.17` 近似月球；跳躍的弧線和拋射物的射程會自動跟著變                                            |
| `worldFallDamage`       | 摔落傷害                                                                     | 低重力維度通常會想讓它跟著調整                                                                   |
| `worldJumpStrength`     | 跳躍速度                                                                     | 以同樣的方式縮放跳躍力度。它乘以的是跳躍的速度，所以高度的增長比數值更快：1.5 大約讓跳躍高度加倍 |
| `worldTerminalVelocity` | 最大下墜速度，以原版上限的比例表示                                           | 鞘翅飛行不受影響                                                                                 |

四個都留空（預設）則保留原版的物理。在 Galacticraft 維度上，重力鍵會縮放 Galacticraft 自己的重力。

### 世界接縫

*值清單*

**世界接縫**，把維度垂直堆疊起來：從世界的底部或頂部離開時，實體會在相同的 x 和 z 被送入下方或上方的維度。

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

| 設定                | 值                                                       | 預設    | 作用                                                             |
| ------------------- | -------------------------------------------------------- | ------- | ---------------------------------------------------------------- |
| `worldBelow`        | `dimension=target` 這幾行，或是涵蓋每個維度的單獨一個 id | 無      | 掉出世界底部時進入的維度                                         |
| `worldAbove`        | 同上                                                     | 無      | 升出生成的頂部時進入的維度，指的是地獄的頂部，而不是它的建造上限 |
| `worldSeamEntities` | 布林值                                                   | `true`  | 物品、生物和其他實體是否會穿越，還是只有玩家會                   |
| `worldSeamBedrock`  | 布林值                                                   | `false` | 在接縫邊界保留基岩。關閉時，邊界不會生成基岩，所以通路可以被挖開 |

兩份清單都留空（預設）則讓每個世界都保持封閉。世界最外側的方塊層就是它的門口：進入底層會把你帶往下方，進入頂層則會把你帶回上方。抵達時會落在門口之外，向下移動時落在內側三層，向上移動時落在內側一層，所以不會直接彈回去。往下來時，還會把抵達處上方的各層一路切開到門口，讓入口從下方依然看得見，也能當作回去的路。

打破門口層中的一個方塊，另一側的世界就會透過它顯現出來：下方維度的天空會出現在地板之下，上方維度的天空會出現在天花板之上。這只在用戶端的視距範圍內繪製，對世界本身沒有任何改變。動量會一併帶過去。

玩家的穿越會被記住。往下會標記出洞口，而在它附近往上回來時，會落在上次那個洞讓你落下的位置，所以經常使用的豎井，總是把你帶回同一個已知的位置，而不是某個新的地方。第一次返回時才會算出落點：如果該處地面之下有地面，就用那個位置，否則從接縫處往外逐格依高度尋找最近的可站立空間，再不然就在洞口旁邊的邊緣挖出一處空穴，因為一條直直往下挖的豎井，還沒有屬於自己的壁架。在附近沒有你自己的洞口之處往上穿越，就只會在那裡建立新的落點。從下方抵達、但附近完全沒有可站立之處時，會退回到該列的地表。如果該位置在岩石之中，腳部和頭部會被清出空間，並確實地破壞那些方塊，讓它們掉落，容器也包括在內。

連鎖是透過讓每個維度各有自己的幾行來堆疊的，而騎乘者和坐騎是分別穿越的。

關卡適用於玩家。尚未解鎖目標的玩家會收到關卡的拒絕訊息，並被放回上次站過的地面，或接縫附近的壁架；接縫不會放置任何方塊，所以上了鎖的豎井無法靠掉下去來刷。物品和生物沒有自己的關卡：開啟 `worldSeamEntities` 時，不論是誰弄丟的，它們都會穿越，關閉時，它們會掉過敞開的地板，並像掉進任何洞一樣消失。`worldSeamBedrock` 則會改為封住地板，而保留基岩的資源包要自己提供通路，常見的做法是用[屬性覆寫](#屬性覆寫)，給 `minecraft:bedrock` 一個正的 `hardness`。在接縫出現之前生成的區塊，保留它們原有的基岩。

**Rubic 世界**：`rubicWorld`、`worldMinHeight`、`worldMaxHeight`、`rubicWorldDimensions`、`rubicWorldDimensionsAreBlacklist` 和 `terrainOffset` 也都是 `terrain` 鍵：請見 [Rubic 世界](#rubic-世界)。

## 資料夾清單

*參考資料*

每個資料夾，連同它的完整路徑，以及指向描述它的那一節的連結，都在[檔案放在哪裡](#檔案放在哪裡)中。

## 指令

*參考資料*

### 你自己的指令

*指令*

`/rdpl` 在你自己的機器上執行，不需要任何權限，因為它所碰觸的一切都是你自己的。重新載入會重新掃描你擁有的資料夾，把你的[屬性覆寫](#屬性覆寫)重新套用到你自己那份方塊和物品上，並更新你自己的資源；它不會觸及任何伺服器，所以伺服器那份副本要用 `/rdplserver reload` 重新載入。在單人遊戲中，兩者是同一台機器，所以 `/rdpl reload` 也會重新載入整合伺服器的戰利品表、進度和函數，與原版自己的重新載入相同。它在任何伺服器上都能運作，不論伺服器有沒有安裝本模組。

| 指令                                                                                          | 等級     | 作用                                                                                                                                          |
| --------------------------------------------------------------------------------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdpl list`                                                                                  | 無       | 每個已載入的資源包、它的優先順序，以及它包含的內容。點一下資源包，就能查詢其中的檔案                                                          |
| `/rdpl which <namespace:path>`                                                                | 無       | 哪個資源包提供了指定的檔案，以及它遮蔽了哪些資源包                                                                                            |
| `/rdpl reload`                                                                                | 無       | 重新掃描資料夾並重新載入所有內容                                                                                                              |
| `/rdpl reload <group>`                                                                        | 無       | 只重新載入其中一類：`textures`、`models`、`languages`、`sounds` 或 `shaders`                                                                  |
| `/rdpl unused`                                                                                | 無       | 你的資源包中目前沒有任何東西要求過的檔案，通常是路徑打錯了                                                                                    |
| `/rdpl config unused`                                                                         | 無       | `rdploader/config` 中，已沒有任何已安裝資源包定義的選項檔案                                                                                   |
| `/rdpl config prune`                                                                          | 無       | 刪除那些檔案                                                                                                                                  |
| `/rdpl pixelmap <namespace:path>`                                                             | 無       | [像素圖](#以像素圖編寫的材質)最後呈現出來的樣子，逐個字元顯示                                                                                 |
| `/rdpl biome list`                                                                            | 無       | 每個可以生成的生態域，以及它的 id                                                                                                             |
| `/rdpl biome list all`                                                                        | 無       | 同上，但包括沒有任何東西能生成的生態域                                                                                                        |
| `/rdpl biome here`                                                                            | 無       | 你所站立的生態域                                                                                                                              |
| `/rdpl biome find <name>`                                                                     | 依伺服器 | 已連結。轉交給 `/rdplserver biome find`，這是唯一知道種子碼的一方                                                                             |
| `/rdpl team`                                                                                  | 無       | 資源包派出的各陣營，每個以自己的顏色顯示，你在哪一邊，以及各自由誰領導                                                                        |
| `/rdpl team join [name]`                                                                      | 無       | 加入一個陣營。只會提供資源包保持開放的陣營；由生物組成的陣營不是你能走進去的。省略名稱則會被放進依 `balance` 接受玩家的陣營中玩家最少的那一邊 |
| `/rdpl team leave`                                                                            | 無       | 離開你所在的陣營                                                                                                                              |
| `/rdpl team vote <player>`                                                                    | 無       | 在資源包以投票決定領導者之處，為你這一邊的領導者投票。平手時沒有人領導                                                                        |
| `/rdpl team claim`                                                                            | 無       | 在資源包允許認領、且你這一邊沒有人擔任領導者之處，接下領導者的位置                                                                            |
| `/rdpl round start`                                                                           | 無       | 開始回合，適用於資源包把回合留在大廳等待的情況（`opens.by`）。限陣營領導者或管理員使用                                                        |
| `/rdpl round reset`                                                                           | 無       | 重設進行中的回合，或依資源包的 `reset` 所允許的方式，發起投票來重設。限陣營領導者、資源包允許發起投票的陣營中的玩家，或管理員使用             |
| `/rdpl round vote yes`、`no`                                                                  | 無       | 在進行中的重設回合投票中投票。限陣營中的玩家使用                                                                                              |
| `/rdpl oregen`、`generators`、`gate`、`dimensions`、`pregen`、`intro`、`goto`、`vein`、`game` | 依伺服器 | 已連結。逐字轉交給 `/rdplserver`，由它決定，所以請見下方的表格                                                                                |

**哪些伺服器子指令有連結，以及其餘為什麼沒有。** 只有在用戶端對該名稱沒有自己的意義時，伺服器子指令才會得到轉交：`oregen`、`generators`、`gate`、`dimensions`、`pregen`、`intro`、`goto`、`vein`、`team` 和 `game` 只可能是伺服器的意思，所以 `/rdpl` 把它們交過去。用戶端也有的那六個，`reload`、`list`、`which`、`unused`、`config` 和 `biome`，維持它們針對你的資源包和你的用戶端的原本意義，轉交它們會使這個意義消失。`biome find` 是共用名稱中唯一本來就屬於伺服器的部分，因為只有伺服器知道世界種子碼，所以只有這一種形式會被轉交，而 `biome list` 和 `biome here` 仍留在你這邊。這也決定了權限：由伺服器自己的管理員檢查來裁定，用戶端既無法作弊，也不會被告知偽造的答案。

**`/rdpl` 也能到達伺服器指令。** 任何 `/rdpl` 自己不處理的指令，也就是 `oregen`、`generators`、`gate`、`dimensions`、`pregen`、`intro`、`goto`、`vein`、`team` 和 `game`，都會直接轉交給 `/rdplserver`，並提供在按 Tab 補全中，所以在單人遊戲中只需要輸入一個指令。它們逐字轉交，伺服器照常決定，權限等一切都不變，所以輸入較短的名稱不會多開放任何東西。兩者都有的子指令，`reload`、`list`、`which`、`unused`、`biome` 和 `config`，留給 `/rdpl`，意思是用戶端自己的資源包。`biome find` 是共用名稱中的唯一例外：只有伺服器知道世界種子碼，所以這個形式會被轉交，而 `biome list` 和 `biome here` 則由你自己的用戶端回答。

**日常編輯：** 在大型模組包中，`/rdpl reload textures` 比 F3+T 快得多。F3+T 仍然有效，會重新載入所有內容。當你*新增*或*刪除*檔案時，請用一般的 `/rdpl reload`，因為這會改變資料夾的內容。

### 伺服器指令

*指令*

在專用伺服器上，`/rdplserver` 對伺服器自己那份資料夾做同樣的事。「等級」欄是傳送者所需的權限等級：`3` 是管理員，`2` 也允許指令方塊，`0` 是任何玩家，而 `4` 高於管理員，沒有人達得到。只有 `intro`、`team`、`card` 和三種 `goto` 形式對管理員以下開放，其中 `goto` 是資源包可以調整的那一個。

#### 資源包與檔案

*伺服器指令*

| 指令                                 | 等級 | 作用                                                        |
| ------------------------------------ | ---- | ----------------------------------------------------------- |
| `/rdplserver reload`                 | 3    | 重新掃描伺服器的資料夾並重新載入所有內容                    |
| `/rdplserver list`                   | 3    | 伺服器載入的每個資源包、它的優先順序，以及它包含的內容      |
| `/rdplserver which <namespace:path>` | 3    | 哪個資源包提供了指定的檔案，以及它遮蔽了哪些資源包          |
| `/rdplserver unused`                 | 3    | 伺服器的資源包中，目前沒有任何東西要求過的檔案              |
| `/rdplserver config unused`          | 3    | `rdploader/config` 中，已沒有任何已安裝資源包定義的選項檔案 |
| `/rdplserver config prune`           | 3    | 刪除那些檔案                                                |

`/rdplserver reload` 現在也會重新載入生成鎖和配方鎖。

#### 世界與生成

*伺服器指令*

| 指令                                | 等級 | 作用                                                                                                                                                          |
| ----------------------------------- | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver oregen`                | 3    | 被封鎖的礦石生成的累計數量，依模組和類型分列                                                                                                                  |
| `/rdplserver generators`            | 3    | 被封鎖的世界生成器的累計數量，依模組和類型分列                                                                                                                |
| `/rdplserver biome`                 | 3    | 伺服器上每個可以生成的生態域                                                                                                                                  |
| `/rdplserver biome list [all]`      | 3    | 同上，並附上每個生態域的 id，而 `all` 會包括沒有任何東西能生成的那些                                                                                          |
| `/rdplserver biome here`            | 3    | 你所站立的生態域。主控台沒有站在任何地方，所以從那裡執行時，它會改為要求指定玩家                                                                              |
| `/rdplserver biome here <player>`   | 3    | 該玩家所站立的生態域，這是主控台和腳本想要的形式                                                                                                              |
| `/rdplserver biome find <name>`     | 3    | 離你最近、有該生態域生成的地方，不必為了尋找而生成區塊                                                                                                        |
| `/rdplserver dimensions`            | 3    | 每個維度，包括資源包新增的維度                                                                                                                                |
| `/rdplserver vein <entry> [radius]` | 3    | `vein` 形狀的世界生成項目，在執行處周圍這麼多個區塊（1 到 64，預設 8）範圍內，其礦脈種在何處，由近到遠排列，不論那些區塊是否已經存在。`/rdpl vein` 會轉交給它 |

#### 閘門指令

*伺服器指令*

| 指令                                      | 等級 | 作用                     |
| ----------------------------------------- | ---- | ------------------------ |
| `/rdplserver gate list`                   | 3    | 每個閘門，以及它是否開啟 |
| `/rdplserver gate check <player>`         | 3    | 玩家通過了哪些閘門       |
| `/rdplserver gate grant <player> <gate>`  | 3    | 為玩家開啟一個閘門       |
| `/rdplserver gate revoke <player> <gate>` | 3    | 再把它關上               |

#### 傳送通道指令

*伺服器指令*

| 指令                                                          | 等級 | 作用                                                                                   |
| ------------------------------------------------------------- | ---- | -------------------------------------------------------------------------------------- |
| `/rdplserver portway list <player>`                           | 2    | 玩家清單中的站點                                                                       |
| `/rdplserver portway grant <player> <x> <y> <z> [dimension]`  | 2    | 把該位置的傳送通道加入玩家的清單。省略維度時使用執行者所在的維度                       |
| `/rdplserver portway forget <player> <x> <y> <z> [dimension]` | 2    | 從玩家的清單中移除一個站點                                                             |
| `/rdplserver portway forget <player> all`                     | 2    | 清空玩家的清單                                                                         |
| `/rdplserver portway forget <player> network <name>`          | 2    | 移除某個網路的站點                                                                     |
| `/rdplserver portway send <player> <x> <y> <z> [dimension]`   | 2    | 把玩家送到該位置的傳送通道，帶上隨行內容、音效、粒子與抵達卡片，不檢查閘門、費用與冷卻 |
| `/rdplserver portway cooldown <player> <seconds>`             | 2    | 設定玩家在下一次傳送前必須等待的時間。`0` 表示清除                                     |

#### 預先生成指令

*伺服器指令*

| 指令                                  | 等級 | 作用                                                                  |
| ------------------------------------- | ---- | --------------------------------------------------------------------- |
| `/rdplserver pregen <radius>`         | 3    | 生成執行指令處周圍該區塊數範圍內的每個區塊。參見[預先生成](#預先生成) |
| `/rdplserver pregen <radius> relight` | 3    | 僅對已存在的陸地執行光照處理                                          |
| `/rdplserver pregen status`           | 3    | 一次執行的進度                                                        |
| `/rdplserver pregen stop`             | 3    | 結束它                                                                |

#### 玩家、隊伍與回合

*伺服器指令*

| 指令                                                                                     | 等級 | 作用                                                                                                                                                                                                                 |
| ---------------------------------------------------------------------------------------- | ---- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver intro`                                                                      | 0    | 讓世界介紹在你下次加入時再次播放。任何玩家都可以執行，且只會清除他自己的紀錄                                                                                                                                         |
| `/rdplserver team`、`team join [name]`、`team leave`、`team vote <player>`、`team claim` | 0    | 與上文的 `/rdpl team` 各形式相同，那些指令會轉交到這裡                                                                                                                                                               |
| `/rdplserver round start`                                                                | 0    | 與 `/rdpl round start` 相同，該指令會轉交到這裡                                                                                                                                                                      |
| `/rdplserver round reset`、`round vote yes`、`round vote no`                             | 0    | 與上文的 `/rdpl round` 各形式相同，那些指令會轉交到這裡                                                                                                                                                              |
| `/rdplserver card <rule> [players]`                                                      | 2    | 依 id 或檔名，向指定的玩家或你自己顯示一條[卡片規則](#卡片)。`when`、`repeat` 和 `cooldown` 會被略過                                                                                                                 |
| `/rdplserver talk <number>`                                                              | 0    | 在已開啟的對話中依編號選擇一個回覆。原版用戶端中可點擊的回覆會執行它。[對話](#對話)                                                                                                                                  |
| `/rdplserver talk open <players> <dialog> <npc>`                                         | 2    | 為指定的玩家開啟對話，並以指定的實體作為說話者，對話可依 ID 或檔名指定。說話者必須與玩家在同一世界，且相距不超過 8 格。[對話](#對話)                                                                                 |
| `/rdplserver raid <突襲> [玩家 或 x y z]`                                                | 3    | 在指定玩家處、指定位置，或兩者都不填時在你自己處開始指定的[突襲](#突襲)，無需村莊。若 `reach` 範圍內已有另一場突襲正在進行則會被拒絕                                                                                 |
| `/rdplserver environment [<dimension> <name>]`                                           | 2    | 把維度切換到它的某個[環境](#環境)，或以 `default` 恢復它自己的外觀。不帶參數時，列出每個有環境的維度以及它目前使用的環境                                                                                             |
| `/rdplserver reset`                                                                      | 3    | 讓地圖恢復到一個回合結束時的樣子：所有人被攔住，實體被清掃，分數被清零，執行 `resetRuns`，玩家被送到 `resetSendsTo` 並被釋放，並以起始計數開啟一個回合，如[預先生成](#預先生成)下的重置設定所述。不會從 `/rdpl` 轉交 |

#### 前往各處

*伺服器指令*

| 指令                                | 等級                 | 作用                                                                                                                                                                           |
| ----------------------------------- | -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `/rdplserver goto <structure>`      | `gotoLevel`、`3`     | 帶你前往最近一個還沒有人去過的該結構，尋找時不會在途中生成陸地                                                                                                                 |
| `/rdplserver goto <structure> next` | `gotoNextLevel`、`3` | 帶你繼續前往你在本次工作階段中尚未被帶去過的最近一個，無論它之前是否有人去過                                                                                                   |
| `/rdplserver goto <structure> back` | `gotoBackLevel`、`3` | 帶你前往它之前的那一個，沿著本次工作階段把你送去過的地方往回退                                                                                                                 |
| `/rdplserver goto <biome>`          | `gotoLevel`、`3`     | 帶你前往該生態域最近的地點，以 id 指定，例如 `minecraft:river`，並落在其地表；`next` 與 `back` 的用法與結構相同。指令方塊或主控台無論權限等級如何設定，一律可以執行任何 `goto` |

#### 遊戲

*伺服器指令*

| 指令                                                       | 等級     | 作用                                                                                                       |
| ---------------------------------------------------------- | -------- | ---------------------------------------------------------------------------------------------------------- |
| `/rdplserver game coin`                                    | 0        | 擲一枚硬幣。正面計為 1，反面計為 0                                                                         |
| `/rdplserver game die <sides>`                             | 0        | 擲一顆 2 到 1000 面的骰子                                                                                  |
| `/rdplserver game die <name>`                              | 0        | 依權重擲一顆[資源包骰子](#骰子與牌組)                                                                      |
| `/rdplserver game dice <roll>`                             | 0        | 擲最多 100 顆骰子並加總，如 `2d6`、`d20` 或 `3d8-2`。每顆骰子都會顯示                                      |
| `/rdplserver game advantage [roll]`, `disadvantage [roll]` | 0        | 擲兩次，取較大的總數或較小的總數。未指定時為 `1d20`                                                        |
| `/rdplserver game pick player`                             | 0        | 隨機選一名線上玩家                                                                                         |
| `/rdplserver game pick team [team]`                        | 0        | 隨機選一支計分板隊伍，或所指定隊伍的一名線上成員                                                           |
| `/rdplserver game deck draw <name>`                        | 0        | 從資源包牌組的剩餘牌中抽一張                                                                               |
| `/rdplserver game deck left <name>`                        | 0        | 牌組還剩多少張牌                                                                                           |
| `/rdplserver game deck shuffle <name>`                     | 2        | 把所有牌放回                                                                                               |
| `/rdplserver game teamroll [roll]`                         | 0        | 發送者一方的每個人各擲一次，最高者獲勝，平手則抽籤決定。沒有隊伍時只有發送者自己擲                         |
| `/rdplserver game tiebreak [objective]`                    | 2        | 從某個目標並列第一的各方中抽出一方：指定的目標，否則是第一個帶 `tiebreak` 的計分目標，再否則是第一個       |
| `/rdplserver game board list`                              | 0        | 世界中的每副棋盤，及其遊戲、位置和狀態                                                                     |
| `/rdplserver game board start <game> <board> [x y z]`      | 2        | 在發送者所站的位置或給定的位置擺出一副棋盤，並擺好棋子                                                     |
| `/rdplserver game board end <board>`                       | 2        | 移除一副棋盤及其棋子                                                                                       |
| `/rdplserver game board show <board>`                      | 0        | 以字母逐行顯示局面，以及每一方的執掌者、其用時和輪到誰走                                                   |
| `/rdplserver game board move <board> <from> <to> [piece]`  | 0        | 依格子名稱走子，例如 `e2 e4`，並給出升變成的棋子                                                           |
| `/rdplserver game board resign <board>`                    | 0        | 認輸                                                                                                       |
| `/rdplserver game board draw <board>`                      | 0        | 提出和棋，或接受對方的提議                                                                                 |
| `/rdplserver game board takeback <board>`                  | 0        | 請求悔棋上一步，或同意對方的請求                                                                           |
| `/rdplserver game board ai <board> <side> <level>`         | 2        | 讓電腦以 1 到 4 級執掌一方，或用 0 交還                                                                    |
| `/rdplserver game last [count]`                            | 0        | 最近的擲骰，最新的在前：10 次，或給定的次數，最多 50                                                       |
| `/rdplserver game pass`                                    | 0        | 當計分檔案設定了輪流行動時，提前結束發送者所在陣營的回合。從指令方塊或主控台執行時，結束目前正在行動的陣營 |
| `/rdplserver game <roll> store <objective>`                | 同該擲骰 | 把擲骰結果寫入發送者自己在該計分項上的分數                                                                 |
| `/rdplserver game <roll> audience <who>`                   | 同該擲骰 | 改變誰會得知這次擲骰，取值見下文                                                                           |

任何擲骰都可以以 `store <objective>` 結尾，把結果數字寫入發送者自己在該目標中的分數；也可以以 `audience <who>` 結尾，取代資源包的預設值：`self`、`team`（發送者一方，沒有隊伍時只有發送者）、`all`、`radius <blocks>`（同一世界中該距離內的玩家）或 `silent`，後者只寫入紀錄。`/rdpl game` 會轉交給它。

### 誰可以使用 goto

*指令*

**開放 `goto`。** `/rdplserver` 的每一部分都需要管理員，即 3 級，只有 `intro` 和 `team` 除外，它們是玩家自己的指令，一律為 0 級；還有 `card`，它是 2 級，這樣指令方塊就能顯示卡片；`game` 的各部分則有[各自的等級](#誰可以使用-game)。三種 `goto` 形式是資源包唯一可以決定的部分：每種都帶有自己的權限等級，資源包或設定可以將其調低，彼此之間以及與指令的其餘部分之間互不影響。

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "gotoLevel": 3,
    "gotoNextLevel": 2,
    "gotoBackLevel": 3,
    "gotoPlaceLevels": ["Crypt=2", "Stronghold=0", "Mansion=4"],
    "locateLevel": 3
  }
}
```

`locateLevel` 位於同一設定群組，決定誰可以執行原版的 `/locate`。預設值 `2` 就是原版的等級，因此在資源包設定它之前不會有任何變化。`0` 讓任何玩家都能執行，`3` 只留給管理員，`4` 則限定為 4 級管理員。

| 設定              | 所控制的內容                     |
| ----------------- | -------------------------------- |
| `locateLevel`     | 原版的 `/locate`                 |
| `gotoLevel`       | `goto <structure>`               |
| `gotoNextLevel`   | `goto <structure> next`          |
| `gotoBackLevel`   | `goto <structure> back`          |
| `gotoPlaceLevels` | 某一個指定地點，對三種形式都生效 |

該值是發送者所需的權限等級。預設為 `3`（管理員）。`2` 還允許協管員，`0` 則對任何玩家開放。無論等級如何設定，指令方塊或主控台一律可以執行 `goto`，因此資源包可以把傳送放在按鈕或壓力板上，而不會暴露 `/rdplserver` 的其餘部分。這三項設定相互獨立：例如，可以讓 `next` 對協管員開放以便進行村莊巡遊，而 `back` 仍僅限管理員。

由於 `intro` 對所有人開放，任何玩家都能使用 `/rdplserver` 本身，所以其他每個子指令都會自行檢查是否為管理員，並以訊息拒絕。Tab 自動補全與此一致：非管理員會看到 `intro`，在資源包設定了陣營時看到 `team`，一旦某個等級允許他們使用，還會看到 `goto`。

`gotoPlaceLevels` 以 `name=level` 項目為單一地點覆寫這三項設定，如上例所示。名稱就是你在 `goto` 之後輸入的內容：可以是原版的名稱，如 `Village` 或 `Mansion`，也可以是在 imprint 項目上以 `locateAs` 註冊的名稱。比對時忽略大小寫。等級 `4` 高於管理員，會對所有人關閉該地點，這是在其餘 `goto` 開放的同時隱藏某一個地點的辦法。傳送通道不是 `goto` 的地點：傳送通道之間的傳送依循傳送通道自身的鍵，`gotoPlaceLevels` 對其不適用。

一個項目為該地點的三種形式設定同一個等級。未列出的地點退回到上面的三項設定，未註冊的名稱則永遠不會符合。

Tab 自動補全遵循相同的規則，因此在 `goto` 之後，發送者只會看到他們實際可以被帶去的地點。

這些設定位於 `commands` 群組中，所以設定中的 `control.commands` 決定了資源包是否能設定它們，而該項為 `off` 時，無論資源包要求什麼，一切都維持為管理員等級。

### 誰可以使用 game

*指令*

`game` 的每個部分都有自己的等級：每種擲骰為 0，`deck shuffle`、`tiebreak`、`board start`、`board end` 和 `board ai` 為 2。`gameLevels` 以 `part=level` 項目更改其中任意一個，部分即 `game` 之後的內容。

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "gameLevels": ["coin=0", "deck draw=0", "deck shuffle=3", "tiebreak=4"]
  }
}
```

| 設定         | 管轄什麼                                                                                                                                                                                                |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `gameLevels` | `game` 的一個部分：`coin`、`die`、`dice`、`advantage`、`disadvantage`、`pick`、`deck draw`、`deck shuffle`、`deck left`、`teamroll`、`tiebreak`、`last`、`pass`，或 `board` 加其動作，例如 `board move` |

等級刻度與 `goto` 相同，`4` 會對所有人關閉某個部分。Tab 自動補全只提供發送者可以使用的部分。`gameLevels` 與 `goto` 的設定一起位於 `commands` 群組中。

## 須知

*參考資料*

- CraftTweaker 和 GroovyScript 在 RDPL 之後執行，因此它們的改動仍然優先。
- 配方只在啟動時載入，所以修改配方需要重新啟動，而不是重新載入。
- 儲存在世界自身的資料夾中的函數仍然優先於資源包中的函數，該世界自己的進度也是如此。
- 已經生成的結構會一直保持載入，直到你離開該世界。
- 檔名區分大小寫。如果你檔案的大小寫與遊戲請求的不一致，RDPL 仍會載入它，但會發出警告，因為在 Linux 上它根本找不到。設定的 `packs` 類別中的 `warnOnCaseMismatch` 可關閉這個警告。
- 在 `rdploader` 中放入一個 `pack.png`，即可為資源包設定圖示。沒有的話會顯示 RDPL 的圖示。
- 可以透過 `config/mct_resourcedatapackloader_mixin.cfg` 中的 `rootDirectory` 選項移動或重新命名該資料夾。也可以使用絕對路徑，需要重新啟動。
- 引用裸原版模型的方塊狀態也會繼承原版的材質。`cube_all` 和 `cross` 這類父模型從方塊狀態取得材質，沒有問題。
- `forge_marker: 1` 不支援 multipart，所以藤蔓的方塊狀態必須是普通的原版 multipart，並把材質寫在模型裡。
- Forge 載入畫面以深色繪製，並以本模組的標誌取代 Forge 的標誌。`client` 設定類別中的 `darkSplash` 可恢復 Forge 自己的顏色；無論如何，在 `config/splash.properties` 中手動設定的顏色都不會被改動，且需要重新啟動。

## 遇到問題時

*參考資料*

**首先檢查 `logs/rdpl.log`。** RDPL 所做的一切都記錄在那裡，而不是主紀錄中。進度、戰利品表、配方、函數、結構以及每一項內容都會連同其來源的資源包一併記錄，任何格式有誤的內容都會連同原因一起記錄。

**材質和其他資源則不同。** 它們被請求得太頻繁，無法逐一記錄，因此改由 `/rdpl unused` 列出你的資源包中尚無任何內容請求過的檔案。請在遊戲載入完畢後執行。路徑正確的檔案一定會被請求，所以列出的任何檔案通常都是拼寫錯誤，但要記住有些檔案只在需要時才載入，例如你所玩語言之外的其他語言。

**內部沒有 `assets` 目錄的 zip 會被略過，** `rdploader` 中的任何資料夾也一樣，紀錄會對此作出說明。

**`/rdpl which minecraft:textures/blocks/stone.png`** 會準確告訴你是哪個資源包在提供某個檔案，以及它覆蓋了什麼。

**想看每個資源包提供了什麼，** 可在設定的 `packs` 類別中開啟 `logContents`。紀錄隨後會列出找到的每個資源包，以及它提供的各類檔案數量。預設為關閉。

**請求名稱裡帶 `#` 的檔案**，通常意味著模型裡有一個沒人填入的材質變數。同一類別中的 `traceUnresolvedVariables` 會在首次出現這種請求時記錄一份堆疊追蹤，並指出是誰發出的請求。預設為關閉。

**如果你懷疑是世界生成的加速最佳化在作怪，** 設定 `worldgen` 類別中的 `disableOptimizations` 會停用所有預先生成和生成最佳化，讓你確認其中是否有一項是原因。預設為關閉，需要重新啟動。

## 額外功能：原版調整

*參考資料*

對原版行為的小改動，每一項都在 `tweaks` 設定類別中開關。

| 選項                   | 預設 | 作用                                                       |
| ---------------------- | ---- | ---------------------------------------------------------- |
| `promptLeafDecay`      | 開   | 失去樹幹的樹葉會在一秒內凋零，而不是等待隨機刻             |
| `lenientPaths`         | 開   | 可以在方塊下方製作土徑，並且在其上方放置方塊時土徑仍會保留 |
| `unbreakableSpawners`  | 關   | 生怪磚無法被挖掘或炸毀                                     |
| `modernChestPlacement` | 開   | 儲物箱的並排方式與 1.13 及之後相同                         |

另外一些位於 `content` 類別而不是 `tweaks`：

| 選項                | 預設 | 作用                                                         |
| ------------------- | ---- | ------------------------------------------------------------ |
| `cactusMaxHeight`   | `3`  | 原版仙人掌最高能長多高                                       |
| `caneMaxHeight`     | `3`  | 原版甘蔗最高能長多高                                         |
| `shovelPaths`       | 開   | 鏟子可將標記為 `behavesAs` path 的方塊變成土徑，潛行時可還原 |
| `shovelPathBecomes` | 空   | 鏟子把這些方塊變成什麼，填方塊名稱。留空則為土徑             |
| `shovelPathReverts` | 空   | 潛行用鏟子時把土徑還原成什麼，填方塊名稱。留空則為泥土       |
| `hoeTilling`        | 開   | 鋤頭可翻耕標記為 `behavesAs` till 的方塊                     |
| `hoeTillsInto`      | 空   | 鋤頭把這些方塊變成什麼，填方塊名稱。留空則為農地             |

另有兩項位於其他類別：

| 選項                    | 預設 | 作用                                                                                                                                                                                                 |
| ----------------------- | ---- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `loadingScreenPercent`  | 開   | 位於 `client` 類別。在世界載入畫面顯示出生區域的百分比，這樣首次載入較慢時會顯示進度，而不是一直停在「Building terrain」                                                                             |
| `fixTinkersModelErrors` | 關   | 位於 `compat` 類別。屏蔽 Tinkers' Construct 和 Construct's Armory 為每件工具、部件和護甲輸出的「Could not load material model」與「Could not load multimodel」錯誤。不會改變所用的模型，需要重新啟動 |

**這些會讓位於 Universal Tweaks**，因為它修改的是相同的原版方塊。具體何時讓位，參見 [Universal Tweaks](#universal-tweaks)。

**這一切都不會影響資源包。** 這些選項只改變 Minecraft 自己的仙人掌、甘蔗、樹葉和土徑。你的資源包以 `"type": "cane"` 定義的方塊帶有自己的 `growth` 部分，無論安裝了什麼其他內容，它都會長到你設定的高度。`lenientPaths` 還會為使用 `behavesAs` 的資源包方塊解除同樣的限制，Universal Tweaks 不會觸及這一點，因此這一半無論如何都維持開啟。

### 不可破壞的生怪磚

*額外功能：原版調整*

`unbreakableSpawners` 讓生怪磚方塊擁有基岩的數值：不可破壞的硬度，以及無物能夠倖免的爆炸抗性。無論鎬子多好，玩家都無法挖掘它，苦力怕、TNT 或會 `explodes` 的資源包實體也無法將其炸毀。創造模式仍然可以移除它們，正如仍可移除基岩一樣，因此資源包作者永遠不會被鎖在自己的建築之外。它需要重新啟動，因為這些數值是在遊戲完成載入時一次性設定的。

**它針對的是方塊，而不是生怪磚本身。** 沒有針對單一生怪磚的開關。該選項改變的是 `minecraft:mob_spawner` 本身，因此會一次性影響世界中的每一個生怪磚：放置生怪磚的四種原版結構、任何模組放置的生怪磚，以及你自己的資源包放置的生怪磚。

最後一點就是自訂結構的答案。位於你某個 `.nbt` 範本中、由 `imprint` 項目放置的生怪磚，是帶有自己的方塊實體的普通生怪磚方塊，所以選項一開啟它就被涵蓋了。照常在結構裡放置一個生怪磚，在範本的方塊實體資料中設定它生成什麼，再開啟 `unbreakableSpawners`，你地城裡的那一個就和原版的一樣不可破壞。資源包裡無需為此加入任何內容，也沒有辦法只保護你自己的，而讓世界中其餘的維持可破壞。

### 箱子放置

*額外功能：原版調整*

`modernChestPlacement` 讓儲物箱和陷阱儲物箱的放置方式與 1.13 及之後相同。

- 儲物箱只會與其正左側或正右側的單個儲物箱相連，並且只有兩者朝向相同時才會如此。位於另一個儲物箱前方或後方的儲物箱絕不會與其相連。
- 潛行可使新儲物箱維持單個，除非點擊的是單個儲物箱的側面：這時它會與該儲物箱相連，並轉向與其相同的朝向。
- 儲物箱可以緊挨著大型儲物箱放置，並維持為單個，因此可以沿牆擺出一排儲物箱。

每個儲物箱都會記住它的搭檔，所以已放置的儲物箱在重新載入後仍維持成對或單個，漏斗或管道只會填充它所接觸的那個儲物箱，破壞其中一半會使另一半變為單個。在該選項開啟之前放置的儲物箱，或由世界生成和結構放置的儲物箱，仍像 1.12 一直以來那樣配對。沒有安裝 RDPL 的用戶端仍會把兩個相接的單個儲物箱繪製成一個大型儲物箱，不過開啟時它們是分開的。

## 額外功能：修正 JEI 外掛程式衝突

*參考資料*

有些模組會在提供配方的模組完成初始化之前就查詢 JEI 的配方登錄表，這會使紀錄裡充斥著數以百計無害但惱人的錯誤，並可能悄悄破壞某個模組的 JEI 整合。RDPL 會自動偵測到這種情況並修正通知順序。它適用於 Just Enough Items 和 Had Enough Items。如果兩者都沒有安裝，則什麼也不會發生。

## 額外功能：減少啟動錯誤

*參考資料*

- 引用了沒有任何模組實際註冊過的物品的配方，通常是在模組自己的設定中被停用的內容，會被略過，而不是擲出解析錯誤。數量只會記錄一次。（`skipMissingItems`）
- 獎勵某個後來被腳本移除的配方的進度仍會載入，而不是失敗。它們只是永遠不會解鎖該配方，整組情況會彙總成一行。（`tolerateMissingInAdvancements`）

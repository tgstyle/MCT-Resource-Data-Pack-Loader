# Resource Data Pack Loader

**一個資料夾，即可覆寫 Minecraft 或任何模組提供的內容，以 JSON 定義新內容，並控制世界的生成方式；在每個世界、用戶端與伺服器上皆能生效，不需要玩家開啟任何開關。**

十三個可直接運作的範例。將其中任何一個直接放進 `rdploader`，看看每個檔案是怎麼寫的。

- [RDPLExamplePack.zip](../example/RDPLExamplePack.zip) 用到了載入器能讀取的幾乎所有檔案類型：方塊、物品、一種流體、一個創造模式分頁、生態域、一個世界範本、一個設有閘門的維度、世界生成、一種藥水及其釀造、一名村民及其交易、配方、戰利品、對原版內容的覆寫、一個音效、一個進度與一個函數。其自述檔說明了在遊戲中應檢查什麼。
- [RDPLExampleOrePackVoid.zip](../example/RDPLExampleOrePackVoid.zip) 將主世界變成一片空無一物的虛空，世界生成的內容懸浮在半空中，每個高度帶各有一種形狀，方便單獨觀察每一種。
- [RDPLExampleVeinShapes.zip](../example/RDPLExampleVeinShapes.zip) 在一般的主世界中放入三種礦脈，每種對應一種礦脈樣式（單純、條帶狀與管狀），各有富礦、普通與貧礦三個等級，並在其上方的地表放置幾個標記方塊，供你據此探勘。
- [RDPLExampleVeinShapesVoid.zip](../example/RDPLExampleVeinShapesVoid.zip) 將同樣的三種礦脈樣式懸浮在空無一物的虛空中，讓每種形狀都能完整看清。
- [RDPLExampleDeepWorld.zip](../example/RDPLExampleDeepWorld.zip) 將主世界的底部降到 -320，在原版世界之下再生成 256 格方塊的世界：包括深層石頭混合（安山岩從深板岩中逐漸過渡而出）、一路延伸到底部的遊戲自帶雜訊洞穴，以及三個可向下探索的洞穴區域，各有自己的裝飾與氛圍。
- [RDPLExampleContainers.zip](../example/RDPLExampleContainers.zip) 新增附有物品欄的方塊與隨身物品，格數從三格到允許的最大值各種尺寸齊備，並附有戰利品表、以原版材質圖染色而成的箱子模型、全部以像素圖繪製的材質，以及可在 Curios 中配戴的一個小袋和一個肩包。
- [RDPLExampleMegaCity32.zip](../example/RDPLExampleMegaCity32.zip) 產生一個超平坦世界，城市街區首尾相接鋪滿地面，擴展到一千個地塊並固定在原點；有混凝土街道、街道之下的下水道、其下的兩條地鐵線和一條穿過城市的鐵路，以及最高 123 格的框架建築。
- [RDPLExampleMegaCity64.zip](../example/RDPLExampleMegaCity64.zip) 是同一座城市，世界高度提升到 512、雲層抬升到 384，配有行道樹，以及建在 16、32 和 64 格寬地塊上、最高 251 格的框架高樓。這座城市沒有邊界：世界中的每個街區都是一座獨立的城鎮，無論走到哪裡，街道都會一路延伸下去。
- [RDPLExampleCityCustomMap.zip](../example/RDPLExampleCityCustomMap.zip) 以一張城市地圖來繪製同一座城市，而不是隨機生成：一張每格 48 個方塊的字元網格，搭配調色盤指明街道、廣場、小巷與依權重挑選的建築，因此街區配置由手工排定。
- [MCTKamikazeDemo.zip](../example/MCTKamikazeDemo.zip) 讓四個陣營在永夜籠罩的基岩競技場中互相對抗：每一方都是真正的原版計分板隊伍，其生物在生成時加入；每擊殺一隻其他陣營的生物，本方就得分；每回合兩分鐘，結束時顯示結算畫面，三個回合構成一場比賽。
- [RDPLExampleRaid.zip](../example/RDPLExampleRaid.zip) 將一個平坦世界布置在遊戲自帶的一座村莊周圍，玩家加入時會獲得不祥之兆：共五波敵人，包括原版的災厄村民、一名女巫，以及資源包自帶的掠奪者、突襲隊長、擲斧者與劫毀獸；不祥之兆與村莊英雄是資源包自帶的效果，並配有像素圖圖示，另有資源包自帶的鐘方塊、一種能讓不祥之兆重新出現的飲品，以及結束突襲的函數。
- [RDPLExampleGameHall.zip](../example/RDPLExampleGameHall.zip) 將一個平坦世界變成遊戲大廳：資源包自帶的加權骰子、一副會自動重新洗牌的生物牌，以及一副在洗牌前一直為空的命運牌、一個右鍵即可擲出 2d6 的骰盅、與莊家逐回合擲骰、平手時以抽牌決勝的骰子對決，以及以生物作棋子、與電腦對弈的西洋棋和跳棋。
- [RDPLExampleColony.zip](../example/RDPLExampleColony.zip) 會在關閉介紹後自動建起一座工作場地，讓村民工人承擔每一種工作訂單：作業範圍隨鎬子提升的礦工、把砍下的木頭留在身上的伐木工、始終保持固定小麥庫存的農夫，以及搬運鵝卵石的搬運工；每個訂單都可透過告示牌、工具、雇用或庫存箱來開啟。

本指南適用於 1.20.1 與 1.21.1 版本。兩者讀取相同的資源包；兩者不同的少數地方以 **1.20.1** 和 **1.21.1** 標出。

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
- [為 1.12.2 編寫的資源包](#為-1122-編寫的資源包)

**方塊與物品**
- [方塊](#方塊)
- [容器](#容器)
- [Curios 欄位類型](#curios-欄位類型)
- [鐘](#鐘)
- [模型、方塊狀態與材質](#模型方塊狀態與材質)
- [讓原版正確對待你的方塊](#讓原版正確對待你的方塊)
- [物品](#物品)
- [流體](#流體)
- [材料、分頁、音效、標籤](#材料分頁音效標籤)
- [屬性覆寫](#屬性覆寫)
- [硬度群組](#硬度群組)

**合成、戰利品與交易**
- [停用的方塊與物品](#停用的方塊與物品)
- [熔爐配方與燃料](#熔爐配方與燃料)
- [配方鎖](#配方鎖)
- [工具提示](#工具提示)
- [語言閘門](#語言閘門)
- [藥水、藥水類型與釀造](#藥水藥水類型與釀造)
- [鐵砧操作](#鐵砧操作)
- [詞綴與插槽](#詞綴與插槽)
- [附魔台](#附魔台)
- [方塊掉落物](#方塊掉落物)
- [右鍵收穫](#右鍵收穫)
- [玩家戰利品](#玩家戰利品)
- [村民與交易](#村民與交易)

**生物與危險**
- [實體變體](#實體變體)
- [生物等級](#生物等級)
- [工作訂單](#工作訂單)
- [曝露設定](#曝露設定)

**世界**
- [世界範本](#世界範本)
- [遊戲規則](#遊戲規則)
- [生態域](#生態域)
- [維度](#維度)
- [傳送門與閘門](#傳送門與閘門)
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
- [Blast Plaster 整合](#blast-plaster-整合)

**參考資料**
- [值清單](#值清單)
- [資料夾清單](#資料夾清單)
- [指令](#指令)
- [須知](#須知)
- [遇到問題時](#遇到問題時)
- [額外功能：原版調整](#額外功能原版調整)
- [未沿用的鍵](#未沿用的鍵)

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

資源包有兩個根目錄，與原版資源包相同。`assets/` 存放用戶端繪製和播放的內容：模型、方塊狀態、材質、語言檔案、音效，以及世界介紹的文字。`data/` 存放其餘一切：本模組讀取的每一種定義，以及資源包所取代的原版資料檔案。本指南中的每個路徑都從命名空間起寫，所以對於命名空間為 `mypack` 的資源包，`<namespace>/blocks/*.json` 在磁碟上就是 `data/mypack/blocks/ruby_ore.json`，`<namespace>/models/` 則是 `assets/mypack/models/`。每個章節都會在標題下方重複列出自己的路徑。

`data/` 之下：

| 路徑                                         | 內容                                                                                                                |
| -------------------------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| `<namespace>/blocks/*.json`                  | 方塊定義。[方塊](#方塊)                                                                                             |
| `<namespace>/curioslots/*.json`              | 可配戴物品的 Curios 欄位類型。[Curios 欄位類型](#curios-欄位類型)                                                   |
| `<namespace>/items/*.json`                   | 物品定義。[物品](#物品)                                                                                             |
| `<namespace>/fluids/*.json`                  | 附帶方塊與桶的流體。[流體](#流體)                                                                                   |
| `<namespace>/materials/*.json`               | 工具與盔甲的材料。[材料、分頁、音效、標籤](#材料分頁音效標籤)                                                       |
| `<namespace>/tabs/*.json`                    | 創造模式分頁。[材料、分頁、音效、標籤](#材料分頁音效標籤)                                                           |
| `<namespace>/sounds/*.json`                  | 音效事件。[材料、分頁、音效、標籤](#材料分頁音效標籤)                                                               |
| `<namespace>/biomes/*.json`                  | 生態域定義。[生態域](#生態域)                                                                                       |
| `<namespace>/worldgen/*.json`                | 生成什麼，以及在哪裡生成。[世界生成項目](#世界生成項目)                                                             |
| `<namespace>/caveregions/*.json`             | 繪製在地下的具名區域。[洞穴區域](#洞穴區域)                                                                         |
| `<namespace>/dimensions/*.json`              | 維度定義。[維度](#維度)                                                                                             |
| `<namespace>/worldtemplates/*.json`          | 將整個世界的設定放進一個檔案。[世界範本](#世界範本)                                                                 |
| `<namespace>/worldintro/*.json`              | 玩家進入世界時顯示的頁面。[世界介紹](#世界介紹)                                                                     |
| `<namespace>/gates/*.json`                   | 傳送門與維度的條件。[傳送門與閘門](#傳送門與閘門)                                                                   |
| `<namespace>/gamerules/*.json`               | 新世界的遊戲規則。[遊戲規則](#遊戲規則)                                                                             |
| `<namespace>/teams/*.json`                   | 原版計分板上的陣營，以及加入陣營的對象。[隊伍](#隊伍)                                                               |
| `<namespace>/scoring/*.json`                 | 目標、積分，以及比賽如何結束。[計分](#計分)                                                                         |
| `<namespace>/raids/*.json`                   | 玩家把不祥之兆帶進村莊時，前來突襲村莊的各波敵人。[突襲](#突襲)                                                     |
| `<namespace>/entities/*.json`                | 以既有實體為基礎建立的實體變體。[實體變體](#實體變體)                                                               |
| `<namespace>/hardness/*.json`                | 針對一組方塊的挖掘時間與爆炸倍率。[硬度群組](#硬度群組)                                                             |
| `<namespace>/affixes/*.json` | 物品在玩家第一次持有時隨機出的屬性加成與插槽。[詞綴與插槽](#詞綴與插槽) |
| `<namespace>/gems/*.json` | 鐵砧為換取屬性加成而嵌入插槽的物品。[詞綴與插槽](#詞綴與插槽) |
| `<namespace>/anvils/*.json`                  | 鐵砧對指定物品施加的附魔、由此獲得的進度，以及在此之前的鎖定。[鐵砧操作](#鐵砧操作)                                 |
| `<namespace>/enchanting/*.json`              | 附魔台提供什麼、要求什麼、拿走什麼：依附魔、依物品以及整體。 [附魔台](#附魔台)                                      |
| `<namespace>/cards/*.json`                   | 由觸發器顯示的螢幕卡片，以及本模組自己發出的訊息。[卡片](#卡片)                                                     |
| `<namespace>/dialogs/*.json`                 | 與實體變體配合的分支對話：文字節點，以及前進、執行函式或顯示卡片的回覆。[對話](#對話)                               |
| `<namespace>/dice/*.json`                    | 資源包的加權骰面骰子、牌組、誰能聽到擲骰結果，以及結果的文字。[骰子與牌組](#骰子與牌組)                             |
| `<namespace>/games/*.json`                   | 以生物為棋子的桌上遊戲：棋盤、棋子及其走法，以及結果的獎勵。[桌上遊戲](#桌上遊戲)                                   |
| `<namespace>/ranks/*.json`                   | 生物生成時可以抽取的隨機層級：額外生命值、傷害、效果、掉落物和名稱標籤。[生物等級](#生物等級)                       |
| `<namespace>/orders/*.json`                  | 實體變體在箱子旁為玩家完成的工作。[工作訂單](#工作訂單)                                                             |
| `<namespace>/exposures/*.json`               | 讓靠近指定方塊、攜帶指定物品或位於指定維度、生態域、結構、洞穴區域或高度的玩家受到曝露的危險。[曝露設定](#曝露設定) |
| `<namespace>/overrides/<target>/<name>.json` | 就地修改既有方塊、物品與藥水類型的屬性。[屬性覆寫](#屬性覆寫)                                                       |
| `<namespace>/villages/*.json`                | 城市或村莊可以興建的地塊。[村莊地塊](#村莊地塊)                                                                     |
| `<namespace>/pathintersects/*.json`          | 繪製在村莊道路交會處的圖樣。[村莊道路](#村莊道路)                                                                   |
| `<namespace>/structuremaps/*.json`           | 在網格上組合成一棟大型建築的範本。[結構地圖](#結構地圖)                                                             |
| `<namespace>/citymaps/*.json`                | 手繪的街道配置，城市依此規劃而非隨機生成。[城市配置地圖](#城市配置地圖)                                             |
| `<namespace>/portalframes/*.json`            | 玩家可以建造並點燃的框架。[傳送門框架](#傳送門框架)                                                                 |
| `<namespace>/blastplaster/*.json`            | Blast Plaster 在爆炸後依維度執行的動作。[Blast Plaster 整合](#blast-plaster-整合)                                   |
| `<namespace>/structures/*.nbt`               | 供樹苗、`imprint` 與模組覆寫使用的範本。[可以覆寫的內容](#可以覆寫的內容)                                           |
| `<namespace>/recipes/*.json`                 | 合成配方，新增或取代。[可以覆寫的內容](#可以覆寫的內容)                                                             |
| `<namespace>/recipe_removals/*.json`         | 依名稱、命名空間或產出刪除的配方。[可以覆寫的內容](#可以覆寫的內容)                                                 |
| `<namespace>/recipe_locks/*.json`            | 玩家取得進度之前保持鎖定的配方。[配方鎖](#配方鎖)                                                                   |
| `<namespace>/spawn_locks/*.json`             | 條件滿足之前不會生成的生物。[生成鎖](#生成鎖)                                                                       |
| `<namespace>/disabled/*.json`                | 退出遊戲的方塊與物品。[停用的方塊與物品](#停用的方塊與物品)                                                         |
| `<namespace>/furnace/*.json`                 | 新增與移除的熔爐配方。[熔爐配方與燃料](#熔爐配方與燃料)                                                             |
| `<namespace>/tooltips/*.json`                | 額外的工具提示行。[工具提示](#工具提示)                                                                             |
| `<namespace>/fuels/*.json`                   | 燃燒時間。[熔爐配方與燃料](#熔爐配方與燃料)                                                                         |
| `<namespace>/brewing/*.json`                 | 釀造台配方。[藥水、藥水類型與釀造](#藥水藥水類型與釀造)                                                             |
| `<namespace>/potions/*.json`                 | 藥水效果。[藥水、藥水類型與釀造](#藥水藥水類型與釀造)                                                               |
| `<namespace>/potion_types/*.json`            | 由這些效果構成的瓶裝藥水。[藥水、藥水類型與釀造](#藥水藥水類型與釀造)                                               |
| `<namespace>/villagers/*.json`               | 村民職業。[村民與交易](#村民與交易)                                                                                 |
| `<namespace>/trades/*.json`                  | 職業收購與販售的物品。[村民與交易](#村民與交易)                                                                     |
| `<namespace>/loot_tables/*.json`             | 被取代的戰利品表。[可以覆寫的內容](#可以覆寫的內容)                                                                 |
| `<namespace>/loot_injections/*.json`         | 加入既有戰利品表的一個池。[可以覆寫的內容](#可以覆寫的內容)                                                         |
| `<namespace>/block_drops/*.json`             | 為資源包並不擁有的方塊新增或取代的掉落物。[方塊掉落物](#方塊掉落物)                                                 |
| `<namespace>/harvests/*.json`                | 右鍵即可收穫並補種的作物。[右鍵收穫](#右鍵收穫)                                                                     |
| `<namespace>/player_loot/*.json`             | 玩家死亡時抽取的戰利品表。[玩家戰利品](#玩家戰利品)                                                                 |
| `<namespace>/advancements/*.json`            | 進度。[可以覆寫的內容](#可以覆寫的內容)                                                                             |
| `<namespace>/functions/*.mcfunction`         | 函數檔案。[可以覆寫的內容](#可以覆寫的內容)                                                                         |
| `<namespace>/tags/<kind>/*.json`             | 標籤，使用遊戲自己的格式。[材料、分頁、音效、標籤](#材料分頁音效標籤)                                               |
| `<namespace>/registry_remap/*.json`          | 將舊名稱對應到新名稱。[登錄表重新命名](#登錄表重新命名)                                                             |

`assets/` 之下：

| 路徑                                                                                            | 內容                                                          |
| ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------- |
| `<namespace>/models/`、`<namespace>/blockstates/`、`<namespace>/textures/`、`<namespace>/lang/` | 一般的資產資料夾。[模型、方塊狀態與材質](#模型方塊狀態與材質) |
| `<namespace>/langgates/<name>/gate.json`                                                        | 僅在閘門成立期間載入的語言條目。[語言閘門](#語言閘門)         |
| `<namespace>/sounds.json`                                                                       | 遊戲讀取的音效索引，與 `data/` 下的 `sounds/` 定義並列        |
| `<namespace>/texts/*.txt`                                                                       | 純文字檔案，供世界介紹使用。[世界介紹](#世界介紹)             |

**1.21.1** 以單數形式命名原版資料資料夾：`loot_table/`、`recipe/`、`advancement/`、`function/`、`structure/`、`tags/item/`、`tags/block/`。資源包在此處兩種寫法都可以；上面的複數名稱會按其單數對應項讀取，所以同一個資源包可用於兩個版本。

## 如何閱讀表格

*開始使用*

每個檔案都是標準 JSON。以下是一個具代表性的世界生成項目：

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

本指南中的鍵表會說明某個鍵是否必填、它存放什麼，以及省略時的預設值。無法辨識的值會被記入日誌並以預設值取代，不會使遊戲當機。全文使用的值類型如下：

| 表格寫的是                                             | 你要寫成                                                                                                                                                         |
| ------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 整數                                                   | `8`                                                                                                                                                              |
| 整數，刻                                               | `100`（20 刻 = 1 秒）                                                                                                                                            |
| 整數或範圍                                             | `8`，或以 `{ "min": 4, "max": 12 }` 在兩者之間隨機取值                                                                                                           |
| 0 到 15、1 到 100 等                                   | 落在該範圍內的整數                                                                                                                                               |
| 浮點數                                                 | `0.5`                                                                                                                                                            |
| 布林值                                                 | `true` 或 `false`                                                                                                                                                |
| 字串                                                   | `"words in quotes"`                                                                                                                                              |
| 方塊名稱、物品名稱                                     | `"minecraft:stone"`。方塊狀態就是名稱旁邊並列 `properties`：`{ "block": "minecraft:oak_log", "properties": { "axis": "x" } }`                                    |
| `namespace:name`                                       | `"mypack:ruby_ore"`                                                                                                                                              |
| 生態域名稱、音效名稱、分頁名稱                         | 同樣是加引號的 `namespace:name` 形式                                                                                                                             |
| 維度 id                                                | `"minecraft:overworld"`、`"minecraft:the_nether"`、`"minecraft:the_end"`，或資源包自己的 `"mypack:verdant"`。1.12.2 的數字 `0`、`-1` 和 `1` 仍會被當作這三個維度 |
| 十六進位顏色                                           | 六位十六進位數字，`"A0C8FF"`，`#` 可有可無                                                                                                                       |
| 材質路徑                                               | `"mypack:block/ruby_ore"`                                                                                                                                        |
| 整數清單                                               | `[4, 12]`                                                                                                                                                        |
| 方塊名稱清單                                           | `["minecraft:stone", "minecraft:andesite"]`                                                                                                                      |
| 生態域名稱清單                                         | `["minecraft:windswept_hills", "mypack:ruby_hills"]`                                                                                                             |
| 生態域類型清單                                         | `["mountain", "forest"]`，即[值清單](#值清單)中列出的類型詞，每個詞代表一個生態域標籤                                                                            |
| 模組 id 或資源包命名空間清單                           | `["quark", "mypack"]`                                                                                                                                            |
| 物件清單                                               | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`，鍵依該物件自己的表                                                                                       |
| 物件                                                   | `{ "type": "cluster" }`，鍵依其自己的表                                                                                                                          |
| 由角色對應到生態域的物件，或由變體名稱對應到變體的物件 | 鍵是前者，值是後者：`{ "ocean": "mypack:ruby_ocean" }`                                                                                                           |

大多數定義也接受 `requires`，這是一份由模組 id 或資源包命名空間組成的清單，列出的項目必須存在，否則該檔案會被跳過。

## 唯一的規則

*開始使用*

打開 jar，找到你想更改的檔案，並從 `assets` 或 `data` 起複製它的路徑：

```
assets/minecraft/textures/block/iron_ore.png                 （位於 Minecraft jar 中）
rdploader/assets/minecraft/textures/block/iron_ore.png       （你的覆寫）

data/minecraft/loot_tables/blocks/iron_ore.json              （位於 Minecraft jar 中）
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    （你的覆寫）
```

`assets` 或 `data` 之後的路徑，永遠與 jar 內的路徑完全相同。沒有任何東西被改名或搬動。

## 整理資源包

*開始使用*

散裝檔案可放在 `rdploader/assets/<namespace>/` 與 `rdploader/data/<namespace>/` 下運作。也可以用 zip 打包成一組。`rdploader` 中的資料夾永遠不會被當成資源包：它會被跳過，並在日誌中留下警告，所以請先把資源包壓成 zip 再放進去。壓縮時，請選取內容後壓縮，而不是壓縮包住它們的資料夾：最上層是單一資料夾、裡面才包著 `assets` 或 `data` 的 zip 會被跳過，日誌會說明。

```
rdploader/assets/minecraft/textures/block/iron_ore.png
rdploader/MyTextures.zip
```

**放錯資料夾的資源包。** 啟動時，在讀取 `rdploader` 之前，RDPL 會巡視遊戲的 `resourcepacks` 資料夾與每個世界的 `datapacks` 資料夾（專用伺服器上則是 `level-name` 所指的世界），並把找到的每個 RDPL 資源包 zip 移到 `rdploader`。zip 內含 RDPL 定義檔案，例如 `data/<namespace>/blocks/`，或在 1.12.2 資源包中為 `assets/<namespace>/blocks/`，即視為 RDPL 資源包。一般的資源包或資料包則留在原處。`rdploader` 中已有同名的 zip 時，該 zip 會留在原位，資料夾形式的 RDPL 資源包也一樣；兩者都會有警告。每次搬移都會寫入 `logs/rdpl.log`。遊戲會自行把被搬走的資源包從資源包清單或世界的資料包中移除，此後 RDPL 從 `rdploader` 載入它。

**優先順序。** 當兩個資源包含有相同的檔案時，請在名稱前加上 `RDPL` 與一個數字作為前綴；數字越大載入得越晚，也就越優先：

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

不分大小寫；數字之後的空格、連字號或底線可有可無；前綴不會顯示在顯示名稱中。沒有前綴的資源包最先載入，並且會輸給任何有編號的資源包。優先順序也會決定世界生成項目的順序，當一個資源包鋪下的方塊被另一個取代時，這一點就很重要。

**同一資源包內。** 命名空間依字母順序載入；每個命名空間內，先載入版本資料夾中的檔案，再載入資源包其餘部分的檔案，最後載入 RDPL 從針對其他遊戲版本的資源包轉換而來的檔案，各自依路徑排序。兩個檔案不會使用同一路徑，因此大多數內容在同一資源包內不會衝突。會衝突的是在檔案內部命名的內容：隊伍、計分目標與骰子保留第一個定義並記錄一則錯誤，而遊戲規則、右鍵收穫規則、Blast Plaster 設定與登錄表重新命名則保留最後一個。遊戲僅以其路徑命名，因此同一路徑出現在兩個命名空間中時，保留第一個。

**停用資源包**的方法是在其名稱後加上 `.disabled`。

**一個 zip 通用所有版本。** zip 可以為其支援的每個 Minecraft 版本各帶一個 `versions/<version>/` 資料夾：`versions/1.12.2/`、`versions/1.20.1/`、`versions/1.21.1/`，在 26.x 上則是它實際執行的確切版本 `versions/26.1.2/`、`versions/26.2/` 或 `versions/26.3/`。每個資料夾的配置都像該版本資源包的根目錄，包含 `pack.mcmeta`。執行中版本的資料夾下的檔案，會取代根目錄下相同路徑的檔案被讀取；根目錄由所有版本共用，其他版本的資料夾則永遠不會被讀取。把各版本讀法相同的內容放在根目錄，只把有差異的部分放進版本資料夾，一個 zip 就能在這四個版本上載入。

**資源包會在版本之間轉換。** 載入為不同版本線編寫的資源包時，第一次會以相同方式轉換，並把變更寫入其自己的 `versions/<version>/` 資料夾，如上所述；這在載入時自動發生，包括 `/rdpl reload` 或 `/rdplserver reload` 觸發的載入，不需要專屬指令。每一對版本都可雙向轉換，因此 1.12.2、1.20.1、1.21.1 或 26.x 的資源包可以在其他任一版本上載入。26.3 的資源包同樣可以在每條版本線上載入，在較舊的版本上會以 26.2 的格式讀取，較舊的資源包也可以在 26.3 上載入。一方有而另一方無法容納的內容會被捨棄，日誌會指出每一項捨棄：從 26.3 向下轉換時，被捨棄的包括除錯用密度函數、資源包自己的含水層排除與地表高度，以及 26.2 沒有的指令；向上轉換到 26.3 時，則包括生成目標的 `depth` 與 `offset`，以及雜訊路由器的礦脈鍵。[未沿用的鍵](#未沿用的鍵)列出全部內容。

根目錄的 `pack.mcmeta` 有最好，但並非必要：本模組會以自己的單一項目，將每個資源包呈現給遊戲，並附上遊戲預期的資源包格式，因此資源包永遠不會因格式編號而過時。在它旁邊放一個 `pack.png`，即可為該資料夾的項目提供圖示；沒有的話，該項目會顯示 RDPL 圖示。

## 資源包：誰優先

*開始使用*

預設情況下，RDPL 檔案位於玩家所選資源包之上，因此資源包無法覆寫它們。在 `RDPL` 前綴之後加上 `O` 或 `N`，即可逐個 RDPL 包決定：

```
rdploader/RDPLO Branding        永遠優先；資源包無法更動它
rdploader/RDPLN BaseTextures    資源包可以覆寫它
rdploader/RDPL1O Seasonal       優先順序與覆寫合併使用
```

沒有字母的 RDPL 包遵循設定選項 `overrideResourcePacks`。`/rdpl list` 會標出具有覆寫權的 RDPL 包。字母必須位於前綴末尾（其後接空格、連字號、底線或什麼都沒有），因此 `RDPLOverhaul` 是一個名為 `Overhaul` 的 RDPL 包，而不是帶有 `O` 旗標。

相同的層級也適用於資料包。標記為 `N` 的 RDPL 包位於世界自己的 `datapacks` 資料夾所帶資料包之下，標記為 `O` 的則位於它們之上。

---

# 資源包如何運作

## 定義如何運作

*資源包如何運作*

除了覆寫檔案的資料夾之外，還有描述新事物的資料夾。一個定義檔案會把同類的一個或多個事物歸在 `variants` 之下，`variants` 內的每個鍵都是一個登錄名稱：`data/mypack/blocks/ore.json` 中帶有名為 `ruby_ore` 的變體，就會登錄 `mypack:ruby_ore`。檔案本身的名稱只是分組，沒有其他作用；一個檔案可以只放一個方塊，也可以放一打共用設定的方塊。

登錄發生在載入器所提供的最低優先順序，所以如果真正的模組登錄了相同名稱，就以模組為準，你的檔案會被忽略。這裡的任何東西都無法取代模組。

**界線在哪裡。** 任何需要自己的方塊實體、畫面、物品欄或逐刻邏輯的東西，都需要真正的模組，唯一的例外是[容器](#容器)類型，它自帶物品欄與畫面。除此之外的一切都可以做。

### 你的命名空間就是你的模組

*定義如何運作*

你選擇的命名空間，在一切實際用途上都相當於一個模組 id。它不會作為模組被載入，也永遠不會出現在模組清單中，但所有讀取模組 id 的地方都會讀取你的命名空間：

- 登錄名稱形如 `mypack:ruby_ore`，與模組的完全一樣，並且會寫入每一個包含它們的存檔。
- 設定中的礦石、生態域與配方白名單會與它比對，所以 `oreWhitelist = mypack` 會保留你的礦石，並擋下其他人的方塊。
- `/rdpl which`、`/rdplserver oregen` 與各類報告都依它分組。
- JEI、標籤與其他模組的查詢，也以同樣方式看待它。

所以請在一開始選定一個名稱，之後永遠不要更改。重新命名命名空間會讓世界中已經放置的一切變成孤兒，就像模組更改自己的 id 一樣，而 `registry_remap` 正是用來修復這個問題的。

這是雙向的：`requires` 既接受已安裝的模組 id，也同樣接受資源包命名空間，因此一個資源包可以依賴另一個，並在後者未安裝時被跳過。

`requires` 中指定的模組或資源包若未安裝，就會跳過該定義：一行日誌會寫入 `logs/rdpl.log`，指出缺少了什麼，遊戲則繼續進行。如果預期的方塊沒有出現在創造模式分頁中，那行日誌就是第一個該看的地方。

`requires` 只接受單純的 id。沒有版本範圍語法，所以它只能表明某個模組必須存在，而不能指定是哪個版本。

模組自己的 id `resourcedatapackloader` 是保留的。在它之下定義內容會被忽略並記入日誌，因為那會宣稱擁有本模組所登錄的東西。覆寫本模組自己的資產仍然可以，只是不能在那裡登錄內容。

下面的每個表都遵循[如何閱讀表格](#如何閱讀表格)中的慣例。

大多數定義也接受 `requires`，這是一份由模組 id 或資源包命名空間組成的清單，列出的項目必須存在，否則該檔案會被跳過。

### 資源包選項

*定義如何運作*

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

資源包可以在 `assets` 與 `data` 旁帶一個 `config` 資料夾，內含 JSON 檔案，列出真假選項及其預設值：

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

頂層帶有 `"hide": true` 的檔案，會讓該資源包的選項完全不出現在選項畫面與產生的檔案中，而這些選項仍會依預設值控制內容。有兩種情況需要這樣做：尚未準備好發布的內容，以及範本資源包，其選項只是把各定義串聯起來的機制，而不是任何人應該去做的選擇。移除該鍵即可發布它們。這也可以逐個選項使用：選項物件內部的 `"hide": true` 只會隱藏那一個選項，這樣已完成的資源包就能帶有控制未完成內容的開關，或範本閘門，而兩者都不會顯示出來：

    { "enablePackB": { "default": false, "hide": true } }

由於被隱藏的選項無法切換，預設為 true 而被隱藏的選項實際上等於強制開啟，適用於必須透過選項機制保持串接、但不屬於任何人選擇的內容。

選項也可以是一個帶有說明的物件，說明會顯示在選項畫面中該選項名稱的下方：

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

啟動時，資源包的選項檔案會變成一份使用者自己擁有的真正設定檔，以資源包命名，即 `rdploader/config/PackA.json`，以資源包的預設值建立，並在資源包更新時合併，使新選項到來時不會動到使用者已經設定的內容。變更會在下次啟動遊戲時生效，但[語言閘門](#語言閘門)讀取的選項除外，它們在按下「完成」時立即生效。玩家則在選取世界與建立世界畫面上的「資源包選項」按鈕中切換。選項只屬於有名稱的資源包，也就是 zip，因為產生的檔案以資源包命名；`rdploader/assets` 與 `rdploader/data` 下的散裝檔案沒有資源包名稱，也不帶選項，所以散裝內容若需要開關，就請把它壓成有名稱的資源包。

任何定義的 `requires` 清單都可以用 `config:` 項目指定一個選項：`"requires": ["config:enableTestingContent"]` 只會在該選項為 true 時註冊該內容，就如同缺少模組時會跳過一樣。只寫名稱時會檢查每個資源包的檔案，且所有定義了該名稱的資源包必須一致；`"config:PackA:enableTestingContent"` 則指定單一資源包。沒有任何資源包定義的選項視為 false，並只警告一次。

控制世界建立時所用內容的選項，會被該世界記住，每次世界儲存時重新記錄。更改它並再次開啟世界時，若這項更改讓世界中的內容變成未登錄，會先備份世界到遊戲自己的 `backups` 資料夾，做法與編輯世界畫面完全相同；若備份失敗，世界就不會開啟。第一位進入主世界的玩家會被告知哪些選項改變了，以及（若有備份的話）已建立副本。

`file:` 項目則以遊戲資料夾下是否存在某個檔案或資料夾作為條件，用來把內容與 RDPL 自己的資源包以外的東西連動，例如另一個模組的資源包：`"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` 只會在安裝了那個確切的檔案時才註冊該內容。路徑是相對於遊戲資料夾，一律使用正斜線，且不得含有 `..`。

### 繼承定義

*定義如何運作*

方塊或物品定義可以用 `"inherits"` 從同一類型的另一個定義開始，指定任一變體的登錄名稱，然後覆寫有差異的部分：

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

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

範本永遠不會註冊，而 `jacks_ore` 會帶著範本的材料、音效、工具、分頁、經驗值掉落與抗性註冊，只覆寫硬度。子定義必須寫出自己的 `requires`，這裡清為空清單，因為否則它會繼承父定義的 `requires`，並隨之消失。

## 可以覆寫的內容

*資源包如何運作*

- **模組資產資料夾中的任何東西**，包括材質、模型、方塊狀態、語言檔案、音效、字型、啟動畫面標語、指南書與手冊
- **進度、戰利品表、標籤與函數**，在伺服端生效，所以專用伺服器上同樣有效
- **配方**，取代模組的配方，或新增你自己的
- **結構範本**，即模組用於生成建築的 `.nbt` 檔案，位於 `<namespace>/structures/` 下
- **登錄表重新命名**，在模組更改方塊或物品名稱時，讓舊世界仍能運作
- **配方移除**，依名稱、命名空間或產出刪除合成配方
- **停用的方塊與物品**，讓任何方塊或物品退出遊戲，見[停用的方塊與物品](#停用的方塊與物品)
- **戰利品注入**，在戰利品表中加入一個池，而不是取代整張表
- **方塊掉落物**，新增或取代任何方塊被破壞時掉落的內容，經驗值也包括在內
- **玩家戰利品**，在玩家死亡時擲出戰利品表，可疊加在其所攜帶的物品之上，也可取而代之
- **既有方塊、物品與藥水的屬性**，包括硬度、光照、堆疊數量、任何東西上的食物，以及藥水的效果，見[屬性覆寫](#屬性覆寫)
- **熔爐配方、燃料燃燒時間、創造模式分頁與音效事件**

在此版本中，方塊掉落什麼取決於它的戰利品表：要改變石頭掉落什麼，就附上 `data/minecraft/loot_tables/blocks/stone.json`；若要在不取代的前提下加入內容，則用戰利品注入或[方塊掉落物](#方塊掉落物)規則，後者也可以給予經驗值。

RDPL 適合取代一兩個配方，而你自己內容的配方則應加在隨附的資源包中。若要對整個模組包做完整的配方控制，KubeJS 與 CraftTweaker 是更好的選擇；這裡的檔案仍會完全取代原檔，所以只想更改一個材料或刪掉一個戰利品項目時，請用那些工具。

## 伺服端資源包

*資源包如何運作*

資源包可以只放在伺服器上，玩家使用純原版用戶端，但有一項限制：**資源包中不得登錄任何東西**。本模組接受任何遠端；由資源包決定。原版用戶端使用的是自己附帶的登錄表，所以會往其中新增內容的資源包必須兩邊都有。

| 只放伺服器即可                                                                                                                                            | 用戶端也需要資源包                                                                                           |
| --------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| `worldgen`、`worldtemplates`、`gamerules`、`structures`、`structuremaps`、`citymaps`、`villages`、`pathintersects`、`caveregions`、`biomes`、`dimensions` | `blocks`、`items`、`fluids`、`materials`、`containers`                                                       |
| `recipes`、`recipe_removals`、`furnace`、`fuels`、`brewing`、`anvils`、`affixes`、`gems`、`enchanting`、`tags`、`disabled`                                                   | `potions`、`potion_types`、`sounds`、`tabs`、`exposures`                                                     |
| `loot_tables`、`loot_injections`、`player_loot`、`advancements`、`functions`                                                                              | `entities`、`villagers`、`portalframes`                                                                      |
| `gates`、`cards`、`trades`、`registry_remap`、`teams`、`scoring`、`ranks`、`raids`、`hardness`、`blastplaster`                                            | `models`、`blockstates`、`textures`、`lang`、`worldintro`、`overrides`（用戶端資料夾：沒有用戶端時就不要放） |
| 整個控制層、設定與預先生成                                                                                                                                |                                                                                                              |

右側欄是硬性限制：原版用戶端沒有的方塊、物品、實體類型、音效與藥水效果，無法向它描述，而維度自己的傳送門是資源包的方塊之一；曝露設定只會與那些內容一起載入。左側欄之所以可行，是因為其中的一切要嘛完全在伺服端執行，要嘛以原版本來就會讀取的資料包項目傳到用戶端（生態域、洞穴區域、維度類型），要嘛透過原版本來就能溝通的封包傳達（由伺服器填入的合成結果格、一般的進度封包、閘門拒絕時的狀態訊息，以及由原版遊戲模式、標題與傳送封包構成的預先生成停留）。

設定步驟：

1. 在設定中啟用 `vanillaClients`（`content` 類別，需要重新啟動）。它會強制執行右側欄：這些資料夾在載入時會被跳過，每個被跳過的檔案都會在日誌中列出，因此誤放的方塊檔案只會變成一行日誌，而不會造成連線被拒。
2. 無論如何都不要把定義放進右側的資料夾；被跳過的檔案只是累贅。資源包引用物品的地方（閘門的 `hold`、`killedDrops`、配方產出、交易），只能指定原版或伺服器上其他兩邊都有的模組所提供的物品。指定了資源包自己地面方塊的生態域，會保留基礎生態域的地面；由自己的傳送門開啟的維度需要那個傳送門方塊，所以請用指令把玩家送過去。
3. 在此版本中，實體變體本身就是獨立的實體類型，所以屬於右側欄：開啟 `vanillaClients` 時它們會被跳過，其生成也一併跳過，日誌會指出它們。
4. 照常安裝在伺服器上，連同 Blast Plaster，這是本模組所需要的，它同樣不登錄任何東西。玩家的機器上什麼都不用放；`/rdpl` 對他們並不存在。
5. 用同一版本的乾淨原版用戶端加入一次來測試。失敗會很明顯：連線在門口就被拒絕，而不是之後悄悄壞掉。
6. 兩個可接受的外觀缺口：伺服器新增的配方可以合成，但不會出現在配方書中；造陸期間的停留只是普通的旁觀者停留，進度顯示在動作列上，沒有本模組自己的用戶端所繪製的霧與標誌。

## 登錄表重新命名

*資源包如何運作*

`<namespace>/registry_remap/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

當模組為其方塊或物品改名時，在改名之前儲存的世界會失去它們。在此放入一個檔案，即可將舊名稱對應到新名稱：

```json
{
  "registry": "minecraft:item",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

| 鍵         | 必填 | 值                       | 預設 | 作用                                                                                                                                                                                                                                                                                     |
| ---------- | ---- | ------------------------ | ---- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `registry` | 是   | 登錄表名稱               | 無   | 這些名稱所屬的登錄表。1.12.2 的名稱 `minecraft:blocks`、`minecraft:items`、`minecraft:biomes`、`minecraft:enchantments`、`minecraft:potions`、`minecraft:potiontypes`、`minecraft:soundevents`、`minecraft:entities` 與 `minecraft:villagerprofessions` 會被當作其對應的現代登錄表來讀取 |
| `mapping`  | 是   | 舊名稱對應到新名稱的物件 | 無   | 每個舊名稱，以及它現在的名稱                                                                                                                                                                                                                                                             |

登錄表指的是該項目所屬的那一個，以遊戲稱呼它的方式命名：`minecraft:item`、`minecraft:block`、`minecraft:entity_type` 等。重新命名可以串接，所以先把 A 對應到 B、之後再把 B 對應到 C，會讓 A 直接變成 C。

## 模組 API

*資源包如何運作*

模組可以在自己的 jar 內附帶 RDPL 內容，因此不需要另外的資源包。在 jar 的根目錄放一個名為 `rdploader` 的資料夾，並完全照資源包的方式配置它：

```
thatmod.jar
  META-INF/mods.toml                （1.21.1：META-INF/neoforge.mods.toml）
  rdploader/data/thatmod/blocks/ruby_ore.json
  rdploader/assets/thatmod/textures/block/ruby_ore.png
```

模組附帶的內容是預設值，不是覆寫。它載入時位於資源包資料夾中的每個資源包之下，所以資源包作者寫的任何內容都優先於它；而且模組只能在自己的模組檔案中宣告的命名空間下提供檔案。其他命名空間下的檔案會被忽略並產生警告，命名空間內嵌套的 `rdploader` 資料夾也一樣，因此模組無法悄悄重新定義其他模組或資源包作者的內容。

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

## 為 1.12.2 編寫的資源包

*資源包如何運作*

為 1.12.2 版本線製作的資源包可以直接載入。載入器會依其 `pack.mcmeta` 格式、`assets/` 下沒有 `data/` 與之並列的定義資料夾，或 `.lang` 檔案來辨識，並將它轉換到此版本。zip 只會在其自身內部轉換一次：此版本讀法不同的每個檔案，都會寫進 zip 的 `versions/1.20.1/` 資料夾（1.21.1：`versions/1.21.1/`），下列各項皆已處理完成，而根目錄的 1.12.2 檔案維持原樣，所以同一個 zip 仍可在 1.12.2 上載入，如[一個 zip 通用所有版本](#整理資源包)所述。轉換程式會在它寫出的資料夾中放一個 `port.stamp` 檔案，記錄 RDPL 的版本。已有此版本資料夾的 zip，會透過該資料夾讀取；當其 `port.stamp` 記載的是另一個 RDPL 版本時，轉換程式會重新寫出該資料夾，取代其中每個檔案並在日誌中逐一指出；沒有 `port.stamp` 的資料夾，例如資源包作者自己寫的，則永遠不會再被轉換。轉換程式取代掉的根目錄 1.12.2 檔案，例如 `assets/` 下的定義、`.lang` 檔案、1.12.2 的方塊狀態與模型，以及 `textures/blocks/` 與 `textures/items/` 下的材質，在此版本上不會被讀取；只有轉換程式原樣放行的根目錄檔案，例如音效，仍會被讀取。zip 會先寫成暫存檔，完整寫完後才取代原檔。`rdploader/assets` 下的散裝檔案不會被改寫；每次掃描資料夾時，都會透過同一個轉換程式讀取它們。

- 定義資料夾從 `assets/<namespace>/` 移到 `data/<namespace>/`，原版資料資料夾也隨之一起移動：配方、戰利品表、戰利品注入、進度、函數與結構，並改為 1.21.1 讀取的單數名稱（`recipe`、`loot_table`、`advancement`、`function`、`structure`、`tags/item`）。
- `textures/blocks/` 與 `textures/items/` 會以 `textures/block/` 與 `textures/item/` 提供，無論是在模型、像素圖還是檔案本身之中。位於 `models/item/<file>/<variant>.json` 的物品模型，則以 `models/item/<variant>.json` 提供。
- 每個帶中繼資料的 id，`minecraft:wool:14` 或 `minecraft:dye:4`，都會經過遊戲自己的資料修正器，也就是升級 1.12.2 世界所用的同一份程式碼，因此會變成它後來成為的方塊或物品：`minecraft:red_wool`、`minecraft:lapis_lazuli`。在扁平化之後以屬性形式保留下來的方塊狀態，`minecraft:log:1` 變成 `minecraft:oak_log` 並帶 `axis=y`，會以 `properties` 物件輸出。資源包自己的 id 則透過它自己的定義解析：`mypack:materials:5` 變成 `meta` 為 5 的那個變體，`mypack:ruby_ore` 則是檔案的第一個變體，因為在此每個變體都是獨立的方塊。實體與生態域名稱以同樣方式修正，維度編號則變成 id。
- `variants` 保留其鍵；`meta` 會被捨棄，`oreDict` 則透過礦物辭典對應到慣例標籤的方式，變成 `tags`。`oredict/*.json` 檔案會依其加入或移除的每個名稱，各變成一個物品標籤檔案：`-name` 形式的移除會落進標籤的 `remove` 清單，移除 `*` 則取代該標籤。單獨寫的 `creativeTab` 會採用資源包的命名空間，1.12.2 原版分頁標籤如 `misc` 則變成最接近的原版分頁。
- `.lang` 檔案會以遊戲讀取的 `.json` 提供，`tile.mypack:file.variant.name` 變成 `block.mypack.variant`，`item.` 同理，`itemGroup.x` 變成 `itemGroup.mypack.x`，`fluid.x` 同時變成兩個流體鍵，其餘一律照原樣。`langgates/<name>/` 資料夾裡的 `.lang` 檔案也以同樣方式轉換。
- 1.12.2 的方塊狀態完全不會被提供。改為讀取它的材質，並以產生器尋找的名稱提供，即 `textures/block/<variant>.png`，若方塊狀態中有 `end`、`top` 或 `bottom`，則加上 `_top` 與 `_bottom`，於是方塊狀態與模型會為每個變體產生，就像為在此編寫的資源包所做的一樣。
- 配方會失去 `data` 並換成扁平化後的 id，`forge:ore_shaped` 變成 `minecraft:crafting_shaped`，其 `ore` 材料變成 `tag`；戰利品表以同樣方式失去 `set_data`；進度的 `item` 帶 `data` 時則變成 `items`。進度的 `background` 從 `textures/blocks/` 移到 `textures/block/`，其圖示則以 `id` 指明物品。
- 配方的 1.12.2 Forge 詞彙也會一併轉換：`forge:ore_dict` 或 `minecraft:item` 材料類型會被捨棄，`minecraft:item_nbt` 材料變成 `forge:nbt`（1.21.1：`neoforge:components`），其 nbt 經過資料修正器處理，`minecraft:item_exists` 變成 `forge:item_exists`（1.21.1：每個條件都改用其 `neoforge` 名稱，置於 `neoforge:conditions` 下），沒有命名空間的物品會採用該配方的命名空間，`data` 為 32767 則變成包含每個變體的清單。模組 `_constants.json` 中的 `#CONSTANT` 無法一併轉換，日誌會指出它。凡是清單列出物品之處，`name:*` 會變成該物品曾有的每個變體，單一值則取第一個；這涵蓋 `recipe_removals` 的產出與 `furnace` 的移除，它們同樣以物品來轉換。鐵砧的 `item` 或 `with` 寫成 `name:*` 時，會變成包含每個變體的清單，其中任何一個都符合。
- 自 1.12.2 以來改名的戰利品表，在資源包指定它們的任何地方都會被改名：`loot_injections` 的目標、`player_loot` 的表，以及 `loot_table` 項目（1.21.1：其 `value`），因此 `minecraft:entities/zombie_pigman` 變成 `minecraft:entities/zombified_piglin`。帶 `inverse` 的 `killed_by_player` 變成 `inverted` 條件，帶 `on_fire` 的 `entity_properties` 變成 `flags` 謂詞，`set_attributes` 的名稱如 `generic.maxHealth` 變成 `generic.max_health`（1.21.1：運算採用新名稱，修飾器的 `name` 變成其 `id`）。
- 對被扁平化拆開的 1.12.2 方塊所做的覆寫，例如 `overrides/minecraft/wool.json`，會被當作對它變成的每個方塊的覆寫，也就是全部十六種羊毛，因為 1.12.2 是一次更改所有變體。
- 遊戲規則檔案的 `gameLoopFunction` 會變成 `#minecraft:tick` 函數標籤，寫成 `data/minecraft/tags/function/tick.json`，因為這項遊戲規則已不存在。資源包自己的維度編號會透過它的 `dimensions` 檔案讀取，所以 `dimensions/verdant.json` 中的 `"id": 7` 會讓資源包在任何指定處都把 7 當成 `mypack:verdant`。`villageBlocks` 配對的兩側都會被修正，機率保留，`registry_remap` 檔案則可以繼續使用 1.12.2 的複數 `minecraft:blocks` 與 `minecraft:items`。
- 世界範本若關閉了 1.12.2 在某個維度中有的每種結構，也會一併關閉只有此版本才有的結構：主世界的 `ancient_cities`、`buried_treasures`、`ocean_ruins`、`pillager_outposts`、`ruined_portals`、`shipwrecks`、`trail_ruins` 與 `trial_chambers`，以及地獄的 `nether_fossils`。只要有一個 1.12.2 名稱保持開啟，其餘就不動。產生器控制鍵 `blockWorldGenerators` 及其同類，會在轉換後的範本中被省略並在日誌留下一行，因為這裡沒有東西讀取它們。平坦範本 `generatorOptions` 圖層中的方塊名稱會以同樣方式修正，因此 `minecraft:grass` 變成 `minecraft:grass_block`。
- 函數會逐行改寫成此版本的指令語法。帶資料值的 id 會通過同樣的資料修正器，所以 `give @p minecraft:wool 1 14` 變成 `give @p minecraft:red_wool 1`，`give @p mypack:materials 1 5` 則給予 `meta` 為 5 的那個變體，物品、實體與方塊 nbt 的修正方式與世界相同（1.21.1：物品的 nbt 變成它的元件）。`testforblock`、`testfor` 與 `scoreboard players test` 變成 `execute if`，`execute <entity> <x> <y> <z> [detect ...]` 變成 `execute as ... at @s [positioned ...] [if block ...] run`，`effect` 採用 `give` 與 `clear`，`blockdata`、`entitydata` 與 `replaceitem` 變成 `data merge` 與 `item replace`，`scoreboard teams` 與 `scoreboard players tag` 變成 `team` 與 `tag`。選擇器以 `scores` 取代 `score_X_min` 與 `score_X`，以 `distance` 取代 `r` 與 `rm`，以 `level` 取代 `l` 與 `lm`，以 `gamemode` 取代 `m`，以 `limit` 與 `sort` 取代 `c`，以 `x_rotation` 與 `y_rotation` 取代 `rx` 與 `ry`。附魔與效果編號、粒子與音效名稱、遊戲模式與難度編號、以秒為單位的 `weather` 持續時間，以及對另一個實體的相對 `tp`，也都會一併轉換。轉換程式無法處理的行會被改成註解（行首加 `# `），日誌會指出檔案、行號與原因；函數仍會載入，只是少了該行，直到以手動方式還原。`block_drops` 照原樣轉換，其 `meta` 併入方塊名稱或其 `properties`。
- 1.12.2 的平坦世界地板位於 y 0，而此版本從底部 y -64 開始鋪設平坦世界，所以轉換程式會把高度一併下移。世界範本的 `worldType` 為 `flat` 或 `superflat` 時，其 `worldSpawn` 與 `resetSendsTo` 會下移 64（若範本指定了 `worldMinHeight`，則下移到該值），而當資源包附帶的每個世界範本都是平坦時，隊伍的 `spawn`、`standIn` 的 `at` 與 `spawnBox` 高度，以及計分檔案的 `opens.lobby` 也一樣下移。使用 `flat` 地形的資源包維度會下移它的 `groundLevel`，任何指定該維度的位置也同樣處理（若有 `minHeight`，則以其為準）。函數不會說明自己在哪裡執行，所以當資源包的主世界是平坦時，函數中的每個絕對 y 都會下移 64，包括選擇器的 `y`，日誌會說明一次；`~` 與 `^` 高度則不動。採用一般地形的世界保留所有座標，因為它的地表仍在海平面。

日誌會為每個轉換的資源包寫一行摘要，並為它搬動、省略或無法轉換的每個檔案各寫一行，而此版本不再讀取的每個鍵，仍會由遇到它的剖析器指出。轉換是盡力而為，並不是完成品：請閱讀那些行，並手動完成它們所指出的部分，先從任何被改成註解的指令行與任何找不到名稱的材質開始。這些修正請做在資源包根目錄或另一個 zip 中，不要做在轉換程式寫出的 `versions/` 資料夾裡：另一個版本的 RDPL 會重新寫出該資料夾。

---

# 方塊與物品

## 方塊

*方塊與物品*

`<namespace>/blocks/*.json`

`variants` 內的每個鍵都是一個方塊，登錄在資源包的命名空間之下：同時含有 `ruby_ore` 與 `deep_ruby_ore` 的檔案，會登錄 `mypack:ruby_ore` 與 `mypack:deep_ruby_ore`，並共用檔案在 `variants` 之外寫的每一項設定。檔案本身的名稱只是分組。

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
  "container": { "rows": 3, "columns": 9 },
  "bell": { "swing": true, "sound": "minecraft:block.note_block.bell" },
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

### 類型

*方塊*

| 類型         | 你會得到什麼                                                                                                                                                  |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `basic`      | 一般的方塊。缺少 `type` 時使用                                                                                                                                |
| `ore`        | 掉落自身以外的東西，受幸運與絲綢之觸影響                                                                                                                      |
| `falling`    | 像沙子或礫石一樣掉落                                                                                                                                          |
| `slab`       | 下半、上半與雙層，兩個可在手中合併                                                                                                                            |
| `stairs`     | 轉角與斜坡自動處理                                                                                                                                            |
| `fence`      | 與鄰近方塊連接，也與其他模組的柵欄連接                                                                                                                        |
| `pane`       | 像玻璃片一樣連接                                                                                                                                              |
| `wall`       | 像鵝卵石牆一樣連接，帶有牆柱形狀                                                                                                                              |
| `door`       | 兩個方塊高，可用手開啟，也會對紅石作出反應                                                                                                                    |
| `trapdoor`   | 裝在方塊頂部或底部的鉸鏈翻板，可用手或紅石開啟                                                                                                                |
| `fence_gate` | 柵欄線中的一道門，可用手或紅石開啟，遇到牆時會降低                                                                                                            |
| `banner`     | 立在柱上或掛在牆上的旗幟，有十六種站立旋轉，帶有你自己的圖案                                                                                                  |
| `ladder`     | 可攀爬，靠牆放置                                                                                                                                              |
| `torch`      | 可放在牆上與地面，帶有粒子。依所寫的原樣發出該變體的 `light`，所以 `0` 的火把不會發光                                                                         |
| `portway`    | 會把自己加入使用者的站點清單，並開啟一個選單，可前往其中任一站點，可設定限制、消耗與閘門。高度為一格或兩格，可朝向玩家。帶有方塊實體                          |
| `bell`       | 像遊戲中村莊裡那種鐘：從側面使用、被紅石觸發或被拋射物擊中時會響，在框架內擺動，並讓附近的突襲者發光。其方塊狀態帶有朝向與懸掛方式                            |
| `log`        | 朝你放置時所貼的那一面旋轉，並帶有 `minecraft:logs` 標籤，讓砍樹與 Blast Plaster 把它當成樹幹                                                                 |
| `leaves`     | 會枯萎、可用剪刀剪下、會染色並掉落樹苗，並帶有 `minecraft:leaves` 標籤。保持 `opaque` 時會畫成實心，像快速樹葉；設為 `"opaque": false` 即可透視               |
| `sapling`    | 長成一棵樹，或長成你的某個結構                                                                                                                                |
| `crop`       | 經歷多個階段生長，掉落種子與作物產物；檔案沒寫的那一項，就用小麥種子與小麥                                                                                    |
| `flower`     | 站在土壤上的單格植物                                                                                                                                          |
| `cane`       | 像甘蔗或仙人掌一樣向上成柱生長                                                                                                                                |
| `vine`       | 攀爬並掛在方塊側面。有 `growth` 時會向下長到 `maxHeight`，有 `spread` 時還會向側面延伸到相鄰的牆；沒有時則維持放置時的樣子                                    |
| `portal`     | 把走進去的一切送到另一個維度                                                                                                                                  |
| `container`  | 容納玩家可開啟的物品欄，大小不限，並可在第一次開啟時從戰利品表自行填滿。繪製成一般方塊或箱子，視資源包的要求而定。沒有 `container` 物件時，容納三列、每列九格 |

### 檔案鍵

*方塊*

| 鍵                           | 必填           | 值                                                | 預設                                                                                               | 作用                                                                                                                                                                                                                                                                                                                                              |
| ---------------------------- | -------------- | ------------------------------------------------- | -------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `variants`                   | 是             | 變體名稱對應到變體的物件                          |                                                                                                    | 每個項目一個方塊。鍵是它的登錄名稱，並決定它的方塊狀態、模型、材質與語言鍵的名稱                                                                                                                                                                                                                                                                  |
| `type`                       | 否             | 上列類型之一                                      | `basic`                                                                                            | 方塊採用哪種形態                                                                                                                                                                                                                                                                                                                                  |
| `material`                   | 否             | [方塊材料](#值清單)之一                           | `rock`                                                                                             | 方塊的行為與它在 1.12.2 上的材料相同：徒手破壞時會不會掉落東西、活塞如何對待它、熔岩會不會點燃它、流動的液體會不會沖走它，以及放置的方塊會不會取代它。`log` 一律是 `wood`，`leaves` 一律是 `leaves`，`vine` 是 `vine`，`torch` 或 `ladder` 是 `circuits`，`crop` 是 `plants`，而 `stairs` 與 `wall` 的行為與其 `modelBlock` 相同                  |
| `soundType`                  | 否             | [音效類型](#值清單)之一                           | `stone`；`log` 為 `wood`，`leaves` 與 `crop` 為 `plant`，`stairs` 與 `wall` 則採用 `modelBlock` 的 | 腳步聲、破壞與放置的聲音                                                                                                                                                                                                                                                                                                                          |
| `mapColor`                   | 否             | [地圖顏色](#值清單)之一                           | 取自材料                                                                                           | 它在地圖上的樣子                                                                                                                                                                                                                                                                                                                                  |
| `harvestTool`                | 否             | `pickaxe`、`axe`、`shovel`、`hoe`、`sword`        | `pickaxe`                                                                                          | 由哪種工具採集，會自動寫入遊戲的 `mineable` 標籤；`sword` 會寫進 `resourcedatapackloader:mineable/sword`，由資源包自己的 `sword` 工具挖掘。對掉落物而言，只有在需要工具的材料上才有影響。與 1.12.2 相同，鎬子也能全速挖掘 `rock`、`iron` 與 `anvil`，斧頭則能全速挖掘 `wood`、`plants` 與 `vine`。其他任何名稱，例如 `shears`，會被記入日誌並略過 |
| `harvestToolLevel`           | 否             | 0 到 4                                            | `0`                                                                                                | 0 木、1 石、2 鐵、3 鑽石、4 獄髓，會自動寫入 `needs_*_tool` 標籤。`basic`、`ore`、`container`、`portal`、`fence`、`pane`、`log`、`falling`、`slab` 或 `wall` 方塊會忽略它，改採變體的 `harvestLevel`，與 1.12.2 相同                                                                                                                              |
| `silkHarvest`                | 否             | 布林值                                            | `true`                                                                                             | 絲綢之觸是否回傳方塊本身                                                                                                                                                                                                                                                                                                                          |
| `opensWith`                  | 否             | 物品 id                                           | 無                                                                                                 | 讓方塊成為上鎖的箱子：破壞它會掉落方塊本身，而用指定物品右鍵會消耗一個、播放方塊的破壞音效、發放變體的 `drops` 清單並移除方塊。任何其他點擊會顯示語言檔案中的動作列文字 `block.<pack>.<block>.locked`                                                                                                                                             |
| `openSound`                  | 否             | 音效名稱                                          | 破壞音效                                                                                           | 上鎖箱子開啟時播放的聲音，取代破壞音效。1.12.2 的名稱仍可讀取，見[音效名稱](#值清單)                                                                                                                                                                                                                                                              |
| `expDrop`                    | 否             | 含 `min` 與 `max` 的物件                          | 無                                                                                                 | 玩家破壞方塊，或會收集經驗的資源包生物挖掉它時掉落的經驗值；活塞、水與爆炸不會掉落。只有在 `silkHarvest` 開啟時，絲綢之觸才會拿走它                                                                                                                                                                                                               |
| `creativeTab`                | 否             | 分頁名稱                                          | 無                                                                                                 | 它出現在哪個分頁，見[創造模式分頁](#創造模式分頁)                                                                                                                                                                                                                                                                                                 |
| `renderLayer`                | 否             | `solid`、`cutout`、`cutout_mipped`、`translucent` | 依類型而定                                                                                         | 如何繪製                                                                                                                                                                                                                                                                                                                                          |
| `opaque`                     | 否             | 布林值                                            | `true`                                                                                             | 是否完全擋住視線與光線；帶箱子模型的容器預設為 `false`                                                                                                                                                                                                                                                                                            |
| `fullCube`                   | 否             | 布林值                                            | 與 `opaque` 相同                                                                                   | 是否填滿它的整個空間                                                                                                                                                                                                                                                                                                                              |
| `lightOpacity`               | 否             | 0 到 255                                          | 不透明時為 `255`，否則為 `0`                                                                       | 吸收多少光：15 以上會完全擋住，`0` 則讓陽光直接穿過。`slab` 沿用遊戲自己的值，而箱子模型的容器會讓光穿過                                                                                                                                                                                                                                          |
| `slipperiness`               | 否             | 浮點數                                            | `0.6`                                                                                              | 冰是 `0.98`                                                                                                                                                                                                                                                                                                                                       |
| `flammability`               | 否             | 整數                                              | `0`                                                                                                | 火焰燒毀它的容易程度                                                                                                                                                                                                                                                                                                                              |
| `fireSpread`                 | 否             | 整數                                              | `0`                                                                                                | 火焰從它蔓延的容易程度                                                                                                                                                                                                                                                                                                                            |
| `explosionResistanceDivisor` | 否             | 浮點數                                            | `1.0`                                                                                              | 用來除每個變體對爆炸的 `resistance`                                                                                                                                                                                                                                                                                                               |
| `modelBlock`                 | 否             | 方塊名稱                                          | `minecraft:stone`                                                                                  | 當你的方塊沒有附帶材質也沒有自己的模型時，借用其模型的方塊                                                                                                                                                                                                                                                                                        |
| `itemModel`                  | 否             | `state`、`item`                                   | `state`                                                                                            | `state` 跟隨方塊狀態，`item` 尋找自己的檔案 `models/item/<name>.json`                                                                                                                                                                                                                                                                             |
| `tint`                       | 否             | `biome`、`none`，或十六進位顏色                   | 無                                                                                                 | 需要模型中的 `tintindex` 才會顯示                                                                                                                                                                                                                                                                                                                 |
| `plantTypes`                 | 否             | [植物類型](#值清單)清單                           | 無                                                                                                 | 可以種在它上面的東西                                                                                                                                                                                                                                                                                                                              |
| `behavesAs`                  | 否             | `till`、`path`、`bush`、`animals` 的清單          | 無                                                                                                 | 要沿用的原版行為                                                                                                                                                                                                                                                                                                                                  |
| `bounds`                     | 否             | 六個數字的清單，0 到 1                            | 完整方塊                                                                                           | 碰撞箱，寫成 `[x1, y1, z1, x2, y2, z2]`                                                                                                                                                                                                                                                                                                           |
| `requires`                   | 否             | 模組 id 或資源包命名空間清單                      | 無                                                                                                 | 除非全部存在，否則跳過該檔案                                                                                                                                                                                                                                                                                                                      |
| `particle`                   | 僅限 torch     | `none`、`flame`、`colored`                        | `flame`                                                                                            | 火把上方的粒子                                                                                                                                                                                                                                                                                                                                    |
| `particleColor`              | 僅限 torch     | 十六進位顏色                                      | `FFFFFF`                                                                                           | 當 `particle` 為 `colored` 時使用                                                                                                                                                                                                                                                                                                                 |
| `smoke`                      | 僅限 torch     | 布林值                                            | `true`                                                                                             | 是否冒煙                                                                                                                                                                                                                                                                                                                                          |
| `leafSapling`                | 僅限 leaves    | 方塊名稱                                          | 無                                                                                                 | 它們掉落的樹苗                                                                                                                                                                                                                                                                                                                                    |
| `leafSaplingChance`          | 僅限 leaves    | 整數                                              | `5`                                                                                                | 樹葉被破壞時掉落樹苗的機率（百分比，0 到 100）。越高掉落得越頻繁，每級幸運增加 2                                                                                                                                                                                                                                                                  |
| `seed`                       | 僅限 crop      | 物品名稱                                          | `minecraft:wheat_seeds`                                                                            | 種下它的物品，以及未成熟作物掉落的東西                                                                                                                                                                                                                                                                                                            |
| `produce`                    | 僅限 crop      | 物品名稱                                          | `minecraft:wheat`                                                                                  | 收成得到的東西                                                                                                                                                                                                                                                                                                                                    |
| `maxAge`                     | 僅限 crop      | 整數                                              | `7`                                                                                                | 有多少生長階段                                                                                                                                                                                                                                                                                                                                    |
| `growth`                     | 僅限植物       | 物件                                              | 無                                                                                                 | 見[生長](#生長)                                                                                                                                                                                                                                                                                                                                   |
| `sapling`                    | 僅限 sapling   | 物件                                              | 無                                                                                                 | 見[樹苗](#樹苗)                                                                                                                                                                                                                                                                                                                                   |
| `portal`                     | 僅限 portal    | 物件                                              | 無                                                                                                 | 見[傳送門與閘門](#傳送門與閘門)                                                                                                                                                                                                                                                                                                                   |
| `container`                  | 僅限 container | 物件                                              | 無                                                                                                 | 見[容器](#容器)                                                                                                                                                                                                                                                                                                                                   |
| `portway`                    | 僅限傳送通道   | 物件                                              | 無                                                                                                 | 見[傳送通道](#傳送通道)                                                                                                                                                                                                                                                                                                                           |
| `bell`                       | 僅限 bell      | 物件                                              | 無                                                                                                 | 見[鐘](#鐘)                                                                                                                                                                                                                                                                                                                                       |

### 變體鍵

*方塊*

| 鍵             | 必填        | 值                                   | 預設     | 作用                                                                                                                                                           |
| -------------- | ----------- | ------------------------------------ | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hardness`     | 否          | 浮點數                               | `1.0`    | 破壞它要多久。黑曜石是 `50`，`-1` 為無法破壞                                                                                                                   |
| `resistance`   | 否          | 浮點數                               | `5.0`    | 以 1.12.2 的讀法計算的爆炸抗性：方塊只保留該數值的五分之三，所以 `10` 會得到石頭的 `6`                                                                         |
| `light`        | 否          | 0 到 15                              | `0`      | 發出的光                                                                                                                                                       |
| `harvestLevel` | 否          | 0 到 4                               | `0`      | `basic`、`ore`、`container`、`portal`、`fence`、`pane`、`log`、`falling`、`slab` 或 `wall` 方塊的工具等級，取代檔案的 `harvestToolLevel`。其他類型則依檔案的值 |
| `rarity`       | 否          | `common`、`uncommon`、`rare`、`epic` | `common` | 提示文字中的名稱顏色                                                                                                                                           |
| `maxSize`      | 否          | 1 到 64                              | `64`     | 堆疊數量                                                                                                                                                       |
| `tags`         | 否          | 標籤 id 清單                         | 無       | 此變體會被寫入的方塊與物品標籤，例如 1.20.1 上的 `forge:ores/ruby` 或 1.21.1 上的 `c:ores/ruby`。標籤檔案會自動產生                                            |
| `drops`        | 否          | 掉落物清單                           | 掉落自身 | 破壞它會得到什麼                                                                                                                                               |
| `portal`       | 僅限 portal | 物件                                 | 檔案的   | 此變體自己的傳送門，取代檔案的，寫法如[傳送門與閘門](#傳送門與閘門)。檔案仍需要有自己的一個                                                                    |

**名稱是永久的。** 變體的鍵會寫進每個包含它的存檔。之後重新命名會讓已放置的方塊變成空氣，除非用[登錄表重新命名](#登錄表重新命名)把舊名稱對應到新名稱。一個檔案可以放任意多個變體；每個都是獨立的方塊，1.12.2 資源包的 `meta` 鍵會被忽略，並在日誌中留下註記。

### 掉落物

*方塊*

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

| 鍵            | 必填   | 值             | 預設                             | 作用                                                                                                                                                                                       |
| ------------- | ------ | -------------- | -------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `block`       | 二選一 | 方塊或物品名稱 |                                  | 掉落的內容                                                                                                                                                                                 |
| `entity`      | 二選一 | 實體名稱       |                                  | 方塊破壞時放出的實體，用來取代物品                                                                                                                                                         |
| `amount`      | 否     | 整數或範圍     | `1`                              | 數量                                                                                                                                                                                       |
| `chance`      | 否     | 0 到 100       | `100`，`guaranteed` 關閉時為 `0` | 掉落到底會不會發生的機率                                                                                                                                                                   |
| `weight`      | 否     | 整數           | `0`                              | 大於零時，此項目加入一個只會掉出一個東西的池。見下文                                                                                                                                       |
| `bonusChance` | 否     | 整數清單       | 無                               | 機率（百分比）清單，每個幸運等級一個項目，從沒有幸運開始。每個值是掉落物再多出一次、數量相同的機率，與 `chance` 用同一次骰子判定，所以最多只會多出一份。最後一個項目也適用於更高的幸運等級 |
| `guaranteed`  | 否     | 布林值         | `true`                           | `chance` 的舊式簡寫。開啟為 `100`，關閉為 `0`                                                                                                                                              |

每個沒有 `weight` 的項目都是各自獨立判定，所以有三個這類項目的方塊可能三個全掉，也可能一個都不掉。給項目加上 `weight` 後，它們就不再獨立：會組成一個池，方塊每次破壞時恰好從中選出一個，機率與權重成正比。上例中，鑽石與綠寶石以一比四共用一個池，因此兩者之一必定掉出，其中綠寶石佔五分之四；紅寶石與煤炭則各自單獨判定，蠹魚又是另一回事。物品與實體分開入池，所以有權重的物品和有權重的實體不會互相競爭。

指定 `entity` 的項目，會在方塊原本所在之處放出一個實體，面向隨機方向，而生物會依當地難度獲得其平常的生成處理，因此出現時會帶有它本來會有的裝備與效果。`amount` 決定數量，`chance` 決定頻率，`weight` 則把它放進實體池。它在方塊破壞時發生，不論是怎麼破壞的，所以爆炸或活塞放出它們，與鎬子沒有差別。`bonusChance` 與幸運對實體沒有意義，會被忽略。

同時指定 `block` 與 `entity` 的掉落項目會使用實體，並在日誌中說明。

掉落物會寫進產生的戰利品表，位於資源包命名空間下的 `loot_tables/blocks/<name>.json`，除非資源包在該路徑自帶了一個，此時方塊使用資源包的檔案，`drops` 不會被讀取。

### 生長

*方塊*

適用於 `crop`、`flower`、`cane` 與 `vine`。

```json
{
  "growth": {
    "stages": 8,
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

| 鍵                | 必填 | 值             | 預設           | 作用                                                                                                                                                                                                      |
| ----------------- | ---- | -------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `stages`          | 否   | 整數           | `16`           | 完成之前的生長階段數。藤蔓會在這麼多次隨機刻中嘗試生長一次                                                                                                                                                |
| `spread`          | 否   | 整數           | `0`            | 僅限藤蔓。`0` 或 `1` 會關閉向側面的蔓延。大於這個值時，只有當側面兩格、上下一格範圍內（含它自身）的同種藤蔓少於這個數量時，藤蔓才會向側面蔓延一格，所以數值越大，長得越密。它延伸的範圍不會超過下一個方塊 |
| `maxHeight`       | 否   | 整數           | `3`            | 甘蔗與藤蔓。柱子長多高，或藤蔓往下垂多遠；`1` 的藤蔓既不生長也不蔓延                                                                                                                                      |
| `soil`            | 否   | 方塊名稱清單   | 該類型的一般值 | 它所立足的方塊                                                                                                                                                                                            |
| `drop`            | 否   | 物品名稱       | 無             | 甘蔗與藤蔓。破壞時掉落的東西；花會掉落自身                                                                                                                                                                |
| `dropCount`       | 否   | 整數           | `1`            | 甘蔗與藤蔓。數量                                                                                                                                                                                          |
| `needsSky`        | 否   | 布林值         | `false`        | 只在看得見天空處生長                                                                                                                                                                                      |
| `needsWater`      | 否   | 布林值         | `false`        | 只在水附近生長                                                                                                                                                                                            |
| `waterRange`      | 否   | 整數           | `1`            | 那些水可以離多遠                                                                                                                                                                                          |
| `damage`          | 否   | 布林值         | `false`        | 傷害碰到它的東西                                                                                                                                                                                          |
| `damageAmount`    | 否   | 浮點數，半顆心 | `1.0`          | 造成多少傷害                                                                                                                                                                                              |
| `breaksNeighbors` | 否   | 布林值         | `false`        | 會破壞放在它旁邊的方塊，就像仙人掌                                                                                                                                                                        |

### 蔓延

*方塊*

適用於 `basic` 與 `ore` 方塊。指定了可蔓延到哪些方塊的方塊，行為就像草蔓延到泥土上。

```json
{
  "spreadsOnto": ["minecraft:dirt", "minecraft:coarse_dirt"]
}
```

| 鍵            | 必填 | 值           | 預設 | 作用                                   |
| ------------- | ---- | ------------ | ---- | -------------------------------------- |
| `spreadsOnto` | 否   | 方塊名稱清單 | 無   | 它蔓延到的方塊。第一個是它變回去的方塊 |

此方塊會接收隨機刻，並沿用原版草的行為。上方光照為 9 以上時，它每刻會在一個隨機位置嘗試四次，範圍為左右各一格以內、從下方三格到上方一格。若該位置是所列方塊之一、其上方光照為 4 以上，且其上方的方塊遮擋的光不超過 2，該位置就會變成本方塊。當上方光照低於 4 且其上方的方塊遮擋的光超過 2 時，它會變回清單中的第一個方塊。`minecraft:dirt` 僅指普通泥土，`minecraft:coarse_dirt` 則是粗泥土。

### 樹苗

*方塊*

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

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

`structure` 會用你的某個範本取代生成的樹，這是建造生成器做不到之物的方法，方塊中其餘部分都不必再寫。改在 `structures` 下列出多個，樹苗每次長大時會從中挑一個，這樣一片樹林就不會一再是同一棵樹：

```json
{
  "sapling": {
    "structures": [
      { "structure": "mypack:ruby_tree", "weight": 3 },
      { "structure": "mypack:ruby_tree_tall", "weight": 1 }
    ]
  }
}
```

| 鍵           | 必填 | 值               | 預設                   | 作用                                                                                                                                          |
| ------------ | ---- | ---------------- | ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `soil`       | 否   | 方塊名稱清單     | 無                     | 它可以在什麼上面生長                                                                                                                          |
| `stages`     | 否   | 整數             | `2`                    | 變成樹之前的生長階段數                                                                                                                        |
| `chance`     | 否   | 整數             | `7`                    | 每次隨機刻有 N 分之一的機率                                                                                                                   |
| `light`      | 否   | 0 到 15          | `9`                    | 需要的光照等級                                                                                                                                |
| `log`        | 否   | 方塊名稱         | `minecraft:oak_log`    | 樹幹方塊                                                                                                                                      |
| `leaves`     | 否   | 方塊名稱         | `minecraft:oak_leaves` | 樹葉方塊                                                                                                                                      |
| `height`     | 否   | 整數             | `4`                    | 樹幹高度                                                                                                                                      |
| `vines`      | 否   | 布林值           | `false`                | 從樹葉上垂下藤蔓                                                                                                                              |
| `structure`  | 否   | `namespace:name` | 無                     | 長成這個範本，而不是產生出來的樹                                                                                                              |
| `structures` | 否   | 清單             | 無                     | 要長成的多個範本，每次生長時挑一個。每個項目寫成 `{ "structure": "namespace:name", "weight": 3 }`，或只寫名稱表示機率均等。會取代 `structure` |

## 容器

*方塊與物品*

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
    "chestModel": true,
    "guiTexture": "mypack:textures/gui/crate.png",
    "guiWidth": 176,
    "guiHeight": 222
  },
  "variants": { "crate": { "hardness": 2.5 } }
}
```

`<namespace>/items/*.json`

```json
{
  "type": "container",
  "container": {
    "rows": 2,
    "columns": 9,
    "curioSlot": "belt"
  },
  "variants": { "pouch": { "maxSize": 1 } }
}
```

| 設定         | 類型         | 預設    | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| ------------ | ------------ | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `rows`       | 整數         | `3`     | 有幾列格子，1 到 9                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `columns`    | 整數         | `9`     | 一列有幾個格子，1 到 12                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `lootTable`  | 文字         | 空      | 第一次有任何東西接觸其內容時（玩家開啟、漏斗、比較器或破壞它）擲入方塊的戰利品表，就像地城箱子被填入一樣。玩家放置的容器永遠不會抽取。空值則讓它一開始是空的                                                                                                                                                                                                                                                                                   |
| `chestModel` | 布林值或文字 | `false` | 繪製成帶可開啟蓋子的箱子，而不是以你自己的材質繪製的一般方塊。`true` 使用原版箱子圖樣；像 `mypack:entity/chest/strongbox` 這樣的材質名稱則改用你自己的箱子圖樣表，放置的方塊與物品皆然。箱子模型的方塊也會把 `opaque` 預設為 `false`，就像原版箱子，所以光不會在方塊處被切斷，箱子也不會被畫得很暗                                                                                                                                             |
| `guiTexture` | 文字         | 空      | 畫面使用你自己的背景圖片。空值則以原版箱子畫面拼出符合列數與欄數所需大小的畫面                                                                                                                                                                                                                                                                                                                                                                 |
| `guiWidth`   | 整數         | 無      | 畫面寬度，從該圖片的左上角起算，圖片視為 256 乘 256 的圖樣表，使用 `guiTexture` 時必填                                                                                                                                                                                                                                                                                                                                                         |
| `guiHeight`  | 整數         | 無      | 畫面高度，使用 `guiTexture` 時必填                                                                                                                                                                                                                                                                                                                                                                                                             |
| `curioSlot`  | 文字         | 空      | 僅限物品：可以配戴它的 Curios 欄位，`back`、`belt`、`body`、`charm`、`head`、`necklace`、`ring`，或其他模組加入的任何欄位。背包通常用 `back`。未安裝 Curios 時會被忽略，而物品的其他一切仍可運作。1.12.2 的鍵 `bauble` 會被當作這個鍵讀取，並採用 Baubles 的名稱：`amulet` 變成 `necklace`，`ring` 給兩個戒指欄位，`belt`、`head`、`body` 與 `charm` 保持原名，而 `trinket` 適用於上述每個欄位。任何其他名稱都會讓物品無法配戴，並留下一行錯誤 |

**九列乘十二欄是上限**，這是畫面所能容納的最大值。要求更多的資源包會被削減到這個上限，並留下一行錯誤說明。關於最高的一種有一點要注意：九列的畫面高 276 像素，而 1080 的顯示器在 GUI 縮放 `auto` 下只給 270，所以頂部與底部各會被裁掉三像素；縮放 3 就能完整顯示。

**畫面是繪製出來的，不是附帶的。** 九欄以下、六列以下的容器直接使用原版箱子畫面，所以外觀與該大小的箱子完全相同。更大的則在繪製時由同一張圖片組合而成：上緣、重複到足夠的一列格子，以及帶有玩家自己物品欄的底部，所以資源包可以要求原版畫面沒有涵蓋的大小，而不必附帶自己的圖片。若資源包想要自己的外觀，`guiTexture` 會覆蓋這一切，此時 `guiWidth` 與 `guiHeight` 必須說明它有多大，否則會使用繪製出來的那個，並留下一行錯誤說明。

**方塊會做什麼。** 它的內容會跨存檔與重新載入保留，被破壞時會掉出，會依裝滿程度回應比較器，並保留製作時的列數與欄數，因此之後在資源包中更改它們，不會影響世界中已有的容器。容器物品或容器方塊不能放進容器裡。`chestModel` 也會給它箱子的開啟音效與蓋子動畫；關閉時，方塊如同任何其他方塊從自己的材質繪製，所以板條箱、木桶或櫥櫃都行得通。

**替箱子上色。** 箱子圖樣表就是一般的材質，所以像素圖可以不畫任何像素就重新上色原版的：`extends` 它並給一個 `tint`，然後在 `chestModel` 中指定那張像素圖。

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

**容器物品就是小袋**，一種 `container` 類型的物品，帶有同樣的 `container` 區段，內含 `rows` 與 `columns`；以右鍵開啟，換手時會保留內容。沒有 `container` 物件時，它只有一列九格。給它 `curioSlot`，在裝有 Curios 的地方它就會放進那個欄位，並可用按鍵在不脫下的情況下開啟，預設為 `V`，可在控制設定中 Resource Data Pack Loader 之下重新綁定。再按一次，若已開啟一個配戴中的容器，會移到你配戴的下一個並循環，因此同時配戴好幾個都能碰到。這個按鍵只在有 Curios 時才出現，而物品的其他一切，右鍵與它的物品欄，不論有沒有都能運作。小袋的可配戴性會自動寫入該欄位的 Curios 標籤。

**戰利品表在第一次使用時填入**，不是在放置方塊時，這正是它在結構中好用的原因：誰先開啟誰就得到那次抽取的結果，而漏斗或比較器先接觸到它時同樣會抽取。同一張表也可用於印記形狀或村莊地塊上的 `lootTable`，因此資源包可以透過世界生成放置這些容器，並以同樣方式補充物品。

**任何物品都可以配戴。** `curioSlot` 不只是容器的鍵，也是物品的鍵，所以戒指、項鍊或護身符不需要容器。物品會放進該 Curios 欄位，在欄位中的期間，`wornEffects` 與 `wornAttributes` 會作用於配戴者。1.12.2 的鍵 `bauble` 會當作 `curioSlot` 讀取。

```json
{
  "curioSlot": "ring",
  "wornEffects": [
    { "potion": "minecraft:speed", "amplifier": 1, "ambient": true }
  ],
  "wornAttributes": [
    { "attribute": "maxHealth", "amount": 10.0, "operation": 0 }
  ],
  "variants": { "ring": { "maxSize": 1 } }
}
```

`wornEffects` 的每一項接受 `potion`、`amplifier`、`ambient` 與 `showParticles`，其中 `showParticles` 在這裡預設為關閉。它沒有 `duration`：效果會在物品配戴期間持續維持，摘下後 1 到 2 秒內消失。`wornAttributes` 的每一項接受 `attribute`、`amount` 與 `operation`，其中 `0` 為相加，`1` 為乘以基礎值，`2` 為乘以總值，修改會隨物品摘下而移除。沒有 `uuid`：修改依物品命名，所以同時配戴兩件相同的物品時共用同一個修改，不會疊加。兩者每秒讀取一次，不會隨玩家儲存。

## Curios 欄位類型

*物品*

`<namespace>/curioslots/*.json`

```json
{
  "size": 2,
  "order": 5,
  "icon": "mypack:slot/gem",
  "cosmetic": true,
  "dropRule": "ALWAYS_KEEP",
  "renderToggle": true,
  "entities": ["minecraft:player"]
}
```

這裡的檔案會新增一種 Curios 欄位類型，或改寫 Curios 已有的類型，物品在 `curioSlot` 中寫它的名稱。檔名就是欄位的 id，所以 `gem.json` 就是 `gem` 欄位。玩家看到的名稱來自 lang 鍵 `curios.identifier.gem`，空欄位圖示是 `assets/<namespace>/textures/slot/<name>.png` 的紋理。1.12.2 版本沒有這個資料夾，因為 Baubles 的欄位是固定的。

| 鍵             | 必填 | 值                                                 | 預設               | 作用                                                  |
| -------------- | ---- | -------------------------------------------------- | ------------------ | ----------------------------------------------------- |
| `size`         | 否   | 整數                                               | `1`                | 欄位有幾格                                            |
| `order`        | 否   | 整數                                               | Curios 預設值      | 欄位在 Curios 畫面中的位置，數值小的在前              |
| `icon`         | 否   | `namespace:name`                                   | 無                 | 空格圖示，取自該命名空間的 `textures/slot/<name>.png` |
| `cosmetic`     | 否   | 布林值                                             | `false`            | 為欄位的每一格新增一個外觀格                          |
| `dropRule`     | 否   | `DEFAULT`, `ALWAYS_DROP`, `ALWAYS_KEEP`, `DESTROY` | Curios 預設值      | 配戴者死亡時其中的物品如何處理                        |
| `renderToggle` | 否   | 布林值                                             | Curios 預設值      | 欄位是否帶有隱藏所配戴物品的按鈕                      |
| `entities`     | 否   | 實體 id 清單                                       | `minecraft:player` | 哪些實體會獲得該欄位                                  |

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
    "sound": "minecraft:block.note_block.bell",
    "resonateSound": "minecraft:block.note_block.chime"
  },
  "variants": { "village_bell": { "hardness": 5.0, "resistance": 30 } }
}
```

以及它的方塊狀態 `assets/mypack/blockstates/village_bell.json`，以 `attachment` 與 `facing` 為鍵：

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

| 設定            | 類型     | 預設                               | 作用                                                                                                   |
| --------------- | -------- | ---------------------------------- | ------------------------------------------------------------------------------------------------------ |
| `swing`         | 布林值   | `true`                             | 從專屬的模型檔案繪製擺動的部分，並在鐘響時讓它擺動。`false` 則由方塊狀態的模型繪製整個鐘，沒有任何動畫 |
| `sound`         | 音效名稱 | `minecraft:block.note_block.bell`  | 鐘響時播放。空值則無聲地響                                                                             |
| `resonateSound` | 音效名稱 | `minecraft:block.note_block.chime` | 突襲者在附近、鐘產生共鳴時播放。空值則無聲地共鳴                                                       |

**它的懸掛方式與遊戲自己的鐘相同。** 放在方塊頂部時站在地面上，朝向你面對的方向；放在方塊下方時掛在天花板；靠著牆時掛在那面牆上，而當另一側也是實心時，則掛在兩面牆之間。承載它的東西消失時它會掉落，而兩面牆之間的鐘在其中一面消失時，會變成單牆鐘。四種的碰撞箱都沿用原版，所以不讀取 `bounds`。

**什麼會敲響它。** 對鐘身的側面使用，位置在橫樑以下：地面鐘是橫樑橫跨的兩個面，牆上的鐘是緊鄰牆的兩個面，天花板鐘則是任何一側。頂部、底部以及鐘身以上的任何位置都沒有作用。紅石訊號在接通時敲響一次，箭、雪球或任何其他拋射物擊中手能碰到的一側時也會敲響。鐘身會朝遠離被擊中那一側的方向擺動兩秒半；紅石則讓它沿鐘所面對的方向擺動。

**鐘響會發生什麼。** 32 格內的村民會聽到，並跑回家躲十五秒，就像遊戲自己的鐘讓他們做的一樣。當 32 格內有突襲者時，鐘會在鐘響後四分之一秒產生共鳴，兩秒後 48 格內的每個突襲者會發光三秒，並在鐘旁各自站立的那一側出現彩色粒子。突襲者指的是[突襲](#突襲)派出的任何生物，以及遊戲自己的災厄村民和女巫。這種類型的鐘對每場突襲來說都是村莊的鐘：每一波到來時它都會響，不必在突襲的 `bell` 中指名。

**模型。** 鐘以 `attachment` 與 `facing` 為鍵，共十六種狀態，`powered` 不在鍵中。開啟 `swing` 時，這些模型只繪製框架，擺動的部分是獨立的模型檔案 `<namespace>:block/<name>_body`：本模組以該路徑載入它，由渲染器繪製，所以名稱不由你選，也永遠不會出現在方塊狀態中。它在鐘身所在的方塊空間中建模，並繞著向內半格、向上四分之三格的點傾斜，與原版相同。關閉 `swing` 時沒有鐘身模型，十六個框架模型就繪製完整的鐘。為手持而寫的物品模型會把框架與鐘身一起繪製，所以鐘在手中顯示完整；若要以其他方式繪製，請附上 `models/item/<name>.json`。

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
    "deny": ["minecraft:the_nether"],
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
  "variants": { "portway": { "hardness": 5.0, "resistance": 30 } }
}
```

| 設定               | 類型         | 預設                              | 作用                                                                                                                                                                                                                                                                                                                                                                                                           |
| ------------------ | ------------ | --------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `activateSound`    | 音效名稱     | `minecraft:block.beacon.activate` | 玩家把它加入清單時，在傳送通道處播放。留空則不播放                                                                                                                                                                                                                                                                                                                                                             |
| `travelSound`      | 音效名稱     | `minecraft:block.portal.travel`   | 玩家傳送時，在兩端播放。留空則不播放                                                                                                                                                                                                                                                                                                                                                                           |
| `names`            | 文字清單     | 空                                | 已放置傳送通道的名稱。每個傳送通道依自己的位置從清單中取一個名稱，每次都相同，除非它以別的方式命名。清單只有一項時，所有傳送通道同名                                                                                                                                                                                                                                                                           |
| `allow`            | 維度 ID 清單 | 空                                | 此傳送通道僅在這些維度中生效，並只列出這些維度的傳送通道。留空表示允許所有維度                                                                                                                                                                                                                                                                                                                                 |
| `deny`             | 維度 ID 清單 | 空                                | 此傳送通道在這些維度中不生效，也不列出這些維度的傳送通道。優先於 `allow`                                                                                                                                                                                                                                                                                                                                       |
| `crossDimension`   | 真或假       | `true`                            | 設為 `false` 時，選單只列出同一維度的傳送通道                                                                                                                                                                                                                                                                                                                                                                  |
| `gate`             | 閘門名稱     | 空                                | 玩家啟動此傳送通道、開啟它的選單並前往它，需要先開啟的[閘門](#閘門)。閘門未開啟時，站點顯示為已鎖定。為同一網路的所有傳送通道指定同一個閘門，就能把整個網路一起鎖上。留空表示不需要                                                                                                                                                                                                                            |
| `network`          | 文字         | 空                                | 此傳送通道所屬的群組。傳送通道只列出並只能前往自己網路中的傳送通道，其餘的選單不會列出。留空表示共用的預設網路                                                                                                                                                                                                                                                                                                 |
| `global`           | 真或假       | `false`                           | 設為 `true` 時，每位玩家不必啟動就能在選單中看到此傳送通道。否則玩家只能看到自己啟動過的                                                                                                                                                                                                                                                                                                                       |
| `scope`            | 文字         | `player`                          | 決定誰共享站點：`player` 每位玩家各有一份清單，`team` 讓隊伍全員共享任一成員啟動的站點（隊伍指資料包 `<namespace>/teams` 資料夾中的一方；玩家可透過列在該方的 `players` 中、使用 `/rdplserver team join <名稱>`（該方將 `joinable` 設為 false 時除外）、單獨使用 `/rdplserver team join` 加入 `balance` 中人數最少的一方，或經 `picks` 抽取加入，見 [隊伍](#誰會加入)），`global` 同上。沒有隊伍時使用個人清單 |
| `tall`             | 真或假       | `false`                           | 像門一樣高兩格。見[高度與朝向](#高度與朝向)                                                                                                                                                                                                                                                                                                                                                                    |
| `facing`           | 真或假       | `false`                           | 朝向放置它的玩家，抵達的旅行者出現在它朝向的那一側。見[高度與朝向](#高度與朝向)                                                                                                                                                                                                                                                                                                                                |
| `activateParticle` | 粒子名稱     | 空                                | 玩家把它加入清單時，在傳送通道處顯示的粒子，例如 `minecraft:portal`。留空則不顯示                                                                                                                                                                                                                                                                                                                              |
| `travelParticle`   | 粒子名稱     | 空                                | 在玩家出發處與抵達處顯示的粒子。留空則不顯示                                                                                                                                                                                                                                                                                                                                                                   |
| `carry`            | 物件         | 無                                | 隨玩家一同前往的東西。見[隨行](#隨行)                                                                                                                                                                                                                                                                                                                                                                          |
| `cost`             | 物件         | 免費                              | 一次傳送的費用，見下方表格                                                                                                                                                                                                                                                                                                                                                                                     |
| `menu`             | 物件         | 見[選單](#選單)                   | 傳送選單的初始排序方式和按鈕圖示，見下方的選單表格                                                                                                                                                                                                                                                                                                                                                             |

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
  "variants": { "portway": { "hardness": 5.0 } }
}
```

`tall` 會讓傳送通道像門一樣高兩格。放置時上方需要有空間，上下兩半會一起放下，破壞任意一半都會同時移除兩半，並只掉落一個傳送通道。站點是下半部分，使用上半部分與使用下半部分相同。`facing` 會讓它朝向放置它的玩家，隨結構範本旋轉與鏡像，抵達的旅行者出現在它朝向的那一側，並面朝同一個方向。

方塊狀態依 `facing`（`north`、`east`、`south`、`west`，有朝向時）與 `half`（`lower`、`upper`，高的傳送通道時）取值，依此順序以逗號分隔，例如 `facing=north,half=lower`。資源包沒有提供方塊狀態時，RDPL 會代為產生。結構範本會像門一樣儲存高傳送通道的兩半。如果資源包為高傳送通道自帶方塊狀態，就還要為物品提供 `models/item/<name>.json`，因為 RDPL 不會從資源包的方塊狀態推導物品模型。

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

定義方塊或物品只會登錄它。它*看起來*如何，則是一組資產檔案，放在遊戲所用的相同資料夾與格式中，位於你自己的命名空間下，而在此版本中，其中大部分會替你寫好。

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**附上材質，其餘的自動產生。** 對於資源包沒有附帶方塊狀態的每個方塊，本模組會寫出該類型所需的方塊狀態與模型，指向 `textures/block/<name>.png`，其中 `<name>` 是變體的鍵；對於沒有 `models/item/<name>.json` 的每個物品，則寫出指向 `textures/item/<name>.png` 的物品模型。既沒有材質也沒有自己模型的方塊，會借用 `modelBlock` 的外觀，預設為石頭，所以不會有東西畫成紫黑方塊。附上你自己的 `blockstates/<name>.json`，本模組就不會為該方塊產生任何東西，改用你的；`models/item/<name>.json` 亦同。

| 類型                                                          | 它尋找的材質檔案                                                 | 產生自                                                                                                                                                                                                         |
| ------------------------------------------------------------- | ---------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `basic`、`ore`、`falling`                                     | `<name>`，有附帶時，頂面與底面用 `<name>_top` 與 `<name>_bottom` | `cube_all`，有頂部或底部材質時為 `cube_bottom_top`                                                                                                                                                             |
| `flower`、`sapling`、`cane`、`leaves`、沒有箱子的 `container` | `<name>`                                                         | `cube_all`、`cross` 或 `leaves`                                                                                                                                                                                |
| `log`                                                         | `<name>` 用於側面，`<name>_top` 用於兩端                         | `cube_column`                                                                                                                                                                                                  |
| `slab`                                                        | `<name>`                                                         | `slab`、`slab_top` 與 `cube_all` 的雙層                                                                                                                                                                        |
| `stairs`                                                      | `<name>`                                                         | `stairs`、`inner_stairs`、`outer_stairs`，四十種狀態全部寫出                                                                                                                                                   |
| `fence`                                                       | `<name>`                                                         | 以 multipart 形式的 `fence_post` 與 `fence_side`，以及供手持的 `fence_inventory`                                                                                                                               |
| `wall`                                                        | `<name>`                                                         | 以 multipart 形式的牆柱與牆側範本，以及供手持的 `wall_inventory`                                                                                                                                               |
| `pane`                                                        | `<name>` 用於玻璃片，`<name>_top` 用於邊緣                       | 以 multipart 形式的五個玻璃片範本                                                                                                                                                                              |
| `door`                                                        | `<name>_top` 與 `<name>_bottom`，或用 `<name>` 兼用              | 八個門模型及其三十二種狀態                                                                                                                                                                                     |
| `trapdoor`                                                    | `<name>`                                                         | 三個可朝向的地板門模型                                                                                                                                                                                         |
| `fence_gate`                                                  | `<name>`                                                         | 四個柵欄門模型，關閉與開啟，在牆中與不在牆中                                                                                                                                                                   |
| `ladder`、`vine`、`torch`                                     | `<name>`                                                         | 各自對應的遊戲自有範本                                                                                                                                                                                         |
| `bell`                                                        | `<name>`                                                         | 遊戲自己的鐘框架，分別為 `<name>_floor`、`<name>_ceiling`、`<name>_wall` 與 `<name>_between_walls`，本模組的 `pack_bell_body` 作為 `<name>_body`，以及涵蓋這些的十六種 `attachment` 與 `facing` 狀態的方塊狀態 |
| `crop`                                                        | `<name>_stage0` 到 `<name>_stage<maxAge>`，或用 `<name>` 兼用    | 每個階段一個 `crop` 模型，`age=0` 到 `7` 對應到它們                                                                                                                                                            |
| `portal`                                                      | `<name>`，或地獄傳送門的                                         | `fullCube` 方塊用 `cube_all`，就像 1.12.2 的傳送門方塊；不是 fullCube 的，例如維度的框架傳送門，則為三個傳送門板，每個軸一個                                                                                   |
| `banner`                                                      | 它自己的圖樣表，見[旗幟](#旗幟)                                  | 遊戲的旗幟模型                                                                                                                                                                                                 |
| 帶 `chestModel` 的 `container`                                | `chestModel` 中指定的箱子圖樣表                                  | 本模組的 `pack_chest` 模型                                                                                                                                                                                     |

每個材質都從 `textures/block/` 下尋找，名稱就是變體的鍵，所以登錄為 `ruby_ore` 的方塊需要 `textures/block/ruby_ore.png`，其他什麼都不必寫。物品以平面方式繪製的方塊，也就是門、梯子、火把、樹苗、花、甘蔗、藤蔓或玻璃片，手持時若存在 `textures/item/<name>.png` 就採用它，不存在時則用它的方塊材質。

**物品**採用 `textures/item/<name>.png` 與產生的 `item/generated` 模型，工具則用 `item/handheld`。附上 `models/item/<name>.json` 即可用其他方式繪製。

**流體**完全不需要模型；模型會由 `still` 與 `flow` 材質產生。

**有多個變體的方塊就是多個方塊。** `variants` 下的每個鍵各自登錄，因此各有自己的方塊狀態、模型與材質，以該鍵命名。沒有帶 `blocks` 屬性的共用方塊狀態，方塊狀態中也不需要說明它是哪個變體：`blockstates/ruby_ore.json` 是紅寶石礦的，`blockstates/deep_ruby_ore.json` 則是深層的。

### 編寫你自己的內容

*模型、方塊狀態與材質*

所有產生出來的東西都可以取代。資源包附帶的方塊狀態會原樣使用，採用遊戲自己的格式：以方塊屬性為鍵的原版 `variants`，或 `multipart`。各類型的屬性都是遊戲自己的：原木的 `axis`，半磚的 `type`，樓梯的 `facing`、`half` 與 `shape`，門的 `facing`、`half`、`hinge` 與 `open`，地板門的 `facing`、`half` 與 `open`，柵欄門的 `facing`、`in_wall` 與 `open`，作物與甘蔗的 `age`，樹苗的 `stage`，柵欄或玻璃片的 `north`、`east`、`south`、`west`，牆與藤蔓則再加上 `up`，站立旗幟的 `rotation` 與牆上旗幟的 `facing`，傳送門的 `axis`，鐘的 `attachment` 與 `facing`（其 `powered` 不放進鍵中）。`basic`、`ore`、`falling`、`leaves`、`flower` 或 `container` 方塊只有一種狀態，鍵為 `""`。

讓模型指向接受材質的父模型，而不是已完成的原版模型：`cube_all` 接受 `all`；`cube_column` 接受 `end` 與 `side`；`cross` 接受 `cross`；樓梯的父模型接受 `bottom`、`top` 與 `side`；`fence_post` 與 `fence_side` 接受 `texture`；`template_wall_post` 與 `template_wall_side` 接受 `wall`；玻璃片範本接受 `pane` 與 `edge`；門的父模型接受 `top` 與 `bottom`；`template_orientable_trapdoor_*` 與 `template_fence_gate*` 接受 `texture`；`template_torch` 接受 `torch`；`crop` 接受 `crop`；`vine` 與 `ladder` 則接受與自己同名的。指定了已完成的原版模型（如 `oak_door_bottom_left`）的模型，不論方塊狀態怎麼寫，都會連同原版的材質一起繼承。

### 旗幟

*模型、方塊狀態與材質*

旗幟是唯一一種方塊的形狀與模型部件的形狀各走各路的類型，所以值得完整說明。

**它會登錄兩個方塊。** 一個定義會給你以自己名稱命名的站立旗幟，以及另一個名為 `<name>_wall` 的方塊，用於懸掛的旗幟。兩者都需要方塊狀態；只有站立的那個有物品，由該物品決定放置哪一個：點擊方塊頂部時放站立的，點擊側面時放牆上的。你永遠不會直接放置牆上的方塊，它也不需要自己的物品。

**站立的旗幟以十六分之一轉動。** 它的屬性是 `rotation`，範圍 `0` 到 `15`，因為旗幟是以十六分之一而非四分之一轉動。方塊狀態自己的 `y` 只接受 0、90、180 與 270，所以每個旋轉都指向各自的小模型，該模型以你的旗幟模型為父模型，並用 `transform` 轉動它：

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

……一路到 `15`，每個都再多轉 `-22.5` 度。符號與遊戲自己的旗幟一致，它們是依旋轉值的負數轉動。請把模型建成朝南，因為玩家面朝南放置的旗幟最後指向那裡。牆上的方塊是一般的方塊狀態，有 0、90、180 與 270 的常規四個 `facing` 項目，因為它沒有任何分數角度。轉換資源包時，1.12.2 資源包的 Forge 方塊狀態會被轉成完全一樣的樣子。

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

**沒有方塊狀態時，它由圖樣表繪製。** 資源包沒有為其附帶方塊狀態的旗幟，會得到產生的方塊狀態，遊戲的旗幟渲染器會以原版旗幟的形狀，從 `textures/entity/banner/<name>.png` 的圖樣表繪製它，配置方式與原版旗幟相同。這是此版本新增的功能；站立與牆上的方塊各自依自己的方塊狀態決定。

**它上面沒有顏色或圖案。** 資源包旗幟不像原版旗幟那樣帶有圖層清單。圖案就是材質，就像門的外觀就是它的材質，一個定義就是一面旗幟。染色與疊加圖案不是資源包能碰到的。

**它採用你給的 `material`。** 石頭旗幟就像它自稱的石頭一樣，用鎬子挖。

### 以像素圖編寫的材質

*模型、方塊狀態與材質*

材質可以是 JSON 檔案而非 PNG。把它放在 PNG 會放的地方，並在整個名稱後加上 `.json`，所以 `textures/block/panel.png.json` 會回應對 `textures/block/panel.png` 的每個請求。其他一切都不變：模型照舊指向 `mypack:block/panel`，圖集、mipmap 與動畫 `.mcmeta` 全都能用，因為遊戲收到的仍是 PNG。範例資源包連一個 PNG 都沒有附帶；其中的每個材質都是像素圖。

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

| 鍵        | 必填       | 值                       | 預設 | 作用                                                 |
| --------- | ---------- | ------------------------ | ---- | ---------------------------------------------------- |
| `size`    | 是，或繼承 | `widthxheight`           |      | 橫向與縱向各有多少像素                               |
| `rows`    | 是，或繼承 | 文字清單                 |      | 每列像素一個字串，每個像素一個字元，由上往下         |
| `palette` | 是，或繼承 | 物件                     |      | 字元對應到顏色，`#RRGGBB` 或 `#AARRGGBB`             |
| `extends` | 否         | 另一個像素圖             |      | 本像素圖所延伸的起點                                 |
| `tint`    | 否         | 含 `from` 與 `to` 的物件 |      | 沿兩種顏色之間的色階，為所有繼承來的內容重新上色     |
| `notes`   | 否         | 物件                     |      | 字元對應到說明其用途的一行文字，會被繼承，永遠不繪製 |

**不需要宣告名稱。** 檔案自己的路徑就是它的名稱，與 PNG 完全相同，所以位於 `assets/mypack/textures/block/panel.png.json` 的像素圖，在模型中就是 `mypack:block/panel`，位於 `assets/mypack/textures/item/gem.png.json` 的像素圖，在物品模型中就是 `mypack:item/gem`。沒有任何東西特別指向像素圖；方塊或物品照舊指定它的材質，永遠不知道拿到的是兩者中的哪一個。這也表示方塊與物品資料夾保持分開，如同 PNG：`textures/block/gem.png.json` 與 `textures/item/gem.png.json` 是兩個不同的材質，並作為兩個不同的檔案快取。

**大小隨你決定**，每邊最大 4096，兩邊不必相同。`16x16` 是一般的方塊面，`16x32` 則是門的一半或動畫會用到的那種高長條。大小是經過檢查而不是猜的：每列像素給一行、每個像素給一個字元，否則該像素圖會被拒絕，日誌會指出是哪一列以及發現了什麼。調色盤中沒有顏色的字元會保持透明，所以 `.` 或空格就是一個洞。

**範本才是重點。** `extends` 指定另一個像素圖，寫成 `namespace:path`，或同一資源包內的單純路徑，延伸它的檔案會繼承其 `size`、`rows` 與 `palette`。它自己寫出的內容優先，而且不必全部寫出，所以一個完整的變體可以只是幾種顏色：

```json
{
  "extends": "mypack:textures/block/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

這就是一張完整的第二種材質：同樣的形狀，換成紫珀色，而且如果範本中的形狀日後被重畫，所有變體都會跟著更新。變體也可以改為給出自己的 `rows` 並保留範本的調色盤，這是反過來的做法，即相同的顏色配上不同的圖案。繼承最多八層，迴圈會被偵測並回報，而指定了無人提供的範本的像素圖會被回報，而不是畫成空白。

**兩張材質中哪一張是範本**，取決於哪一張包含更多區別，而不是哪一張先畫。變體為每個字元給一種顏色，所以範本中稱為同一字元的每個像素，在變體中都是同一種顏色。因此畫在石頭上的礦石無法繼承石頭的 `rows`：石頭把斑點位置都稱為一般石頭，而變體無論怎麼寫都無法把一個字元拆成兩個。反過來就行得通。讓礦石當範本，石頭色調與礦石色調就各自有自己的字元，第二種礦石只要四種顏色：

```json
{
  "extends": "mypack:textures/block/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

真的想要不同圖案的變體，會像上面那樣給出自己的 `rows`，然後只繼承調色盤。當顏色才是重點、形狀無關緊要時，這樣做值得；當形狀才是重點時，就把形狀放進範本，讓變體去指定顏色。

**範本完全不必是材質。** 像素圖只有在其路徑以 `.png` 結尾時才會提供給遊戲，所以位於 `textures/block/ore_template.json` 的範本，遊戲看不到，純粹用來被延伸；而位於 `textures/block/ore_template.png.json` 的則也會回應對 `ore_template.png` 的請求。把共用的形狀命名為不帶 `.png` 的名稱，就不會有東西意外地請求到它。

**範本也可以是真正的圖片而不是像素圖。** 把 `extends` 指向任何資源包或遊戲本身提供的 PNG，調色盤的意義就變了：鍵變成該圖片中已有的顏色，值則是要換上的顏色。不做任何描繪，也不寫 `rows`，所以資源包可以就地替原版或模組的材質重新上色：

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

這就是用原版自己的石頭做成的紅寶石礦：四種斑點色調被換掉，其他每個像素維持原樣。圖片中不存在的顏色只是永遠比對不到，大小取自圖片，除非你自己指定，而指定的大小必須與之相符。

`extends` 優先採用像素圖：它先在該路徑找像素圖，只有在沒有資源包提供時才退而使用圖片。兩者都不是的名稱會被回報，而不是畫成空白。以圖片為基礎的建構是用戶端的工作，因為讀取的是遊戲自己的資源，所以專用伺服器從不執行。

**範本可以上色而不必重畫。** `tint` 給出兩種顏色，並沿兩者之間的色階為像素圖所繼承的一切重新上色。每個繼承來的顏色，其亮度就是它在該色階上的位置，所以黑色落在 `from`，白色落在 `to`，兩者之間的每個色調依比例混合。透明度維持不變。因此一張灰階範本加兩種顏色就是一個完整的變體：

```json
{
  "extends": "mypack:textures/item/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` 可以省略，此時它是黑色，上色就變成一般的相乘，與繪製時的 `tintindex` 形式相同。差別在於這種上色是一次畫進 PNG 並快取的，所以每幀零成本，而且能作用在沒有任何東西上色的材質上，但它也無法像 `grass` 或 `foliage` 那樣隨生態域變化。

範本仍是一張一般的像素圖：打開來看，它畫出來就是它本來的灰色。兩種顏色都接受 `#RRGGBB`、`#AARRGGBB` 或開頭為 `0x` 的形式，兩者都不是的值會讓該像素圖不被繪製，而不是畫成錯誤的顏色。上色與其他內容一樣會被繼承，沿繼承鏈往下第一個出現的生效，所以變體自己的上色優先於它所延伸的那個。它也適用於圖片範本，此時在調色盤的顏色替換之後執行。

**上色是兩種顏色之間的色階**，所以只適合色調落在同一條色階上的材質。有兩個不相關區域的形狀，例如礦石的石頭與斑點，就不屬於此類，需要把調色盤完整寫出來。

**知道範本的字元代表什麼**是延伸範本時最麻煩的部分，這就是上面 `notes` 區段的用途：字元對應到一行簡短說明，與調色盤一樣被繼承，且永遠不繪製。替範本的字元加上標籤，延伸它的人就知道該覆寫哪些。

`/rdpl pixelmap <namespace:path>` 會回報一個像素圖實際的結果，這是不必打開鏈上每個檔案就能寫出變體的可靠方法：

```
oretest:textures/block/ruby_ore.png is 16x16
  built from oretest:textures/block/ruby_ore.png.json
  built from oretest:textures/block/gem_ore.png.json
  rows come from oretest:textures/block/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  礦石本體，每個礦塊的大部分
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  礦石陰影，最暗的色調
  a  #747474  86 pixel(s)  set by gem_ore.png.json   石頭，最常見的色調
```

每個字元都會與它的顏色、涵蓋多少像素、鏈上哪個檔案設定了它，以及該檔案說它的用途一起列出。路徑可以用簡短寫法 `mypack:block/panel`，也可以寫完整。顯示 0 像素的字元，是調色盤有指名、但列從未使用的字元，通常表示某一列有錯字。

**繪製好的圖片會存在磁碟上**，位於 `rdploader/pixelmap-cache`，每個命名空間一個資料夾，檔名為材質名稱後面加上其來源的雜湊值。雜湊涵蓋整條鏈，包括像素圖本身與它上面的每個範本，所以編輯範本會改變每個繼承它的變體的戳記，它們都會被重畫。像素圖重畫時，舊的檔案會被清掉。

每次掃描資源包時也會巡視該資料夾，凡是沒有任何資源包再提供其像素圖的圖片都會被刪除，連同留下的空資料夾。重新命名材質、移除資源包、刪除像素圖，其快取的圖片也會跟著消失，而不是永遠留在那裡。刪除整個資料夾只會多花重畫它們的時間，而且掃描資源包時會略過它，所以它絕不會被誤認為資源包。

PNG 永遠優先。如果 `panel.png` 與 `panel.png.json` 同時存在，就會提供 PNG，像素圖不會被繪製，所以日後可以用手繪的材質取代產生的材質，而不必更動任何指向它的東西。

**沒有人需要手寫這些檔案。** 儲存庫在 `pixelmap/` 中附帶整個往返流程的腳本：`png_to_pixelmap.py` 把一個 PNG 轉成像素圖，`convert_pack.py` 對資源包中的每個材質這麼做，`verify_pack.py` 則繪製轉換後資源包的像素圖並與它們來源的 PNG 比較，這樣在把原圖收起來之前，轉換結果就可以信任。

### 值得留意的陷阱

*模型、方塊狀態與材質*

**指定了已完成原版模型的模型，也會繼承原版的材質。** `torch`、`ladder`、`oak_door_bottom_left` 與 `wheat_stage0` 全都帶有自己的材質，所以指向其中一個的模型，不論你在旁邊放什麼，都會得到原版的外觀。`cube_all`、`cross` 與 `crop` 這類父模型，其材質取自指定它們的模型，行為正常，門、地板門與柵欄門範本也一樣。

**名稱來自語言檔案。** 方塊或物品在 `lang/en_us.json` 給它名稱之前，會顯示原始鍵，而這些鍵是遊戲自己的：方塊以及放置它的物品用 `block.mypack.ruby_ore`，物品用 `item.mypack.ruby`，創造模式分頁用 `itemGroup.mypack.tab`，流體用 `fluid_type.mypack.molten_ruby` 與 `fluid.mypack.molten_ruby`，藥水效果用 `effect.mypack.ruby_sight`，實體變體用 `entity.mypack.angry_cow`，生態域用 `biome.mypack.ruby_forest`。每個只有一個名稱；在此版本中，沒有任何東西需要把鍵寫兩次。

## 讓原版正確對待你的方塊

*方塊與物品*

原版在十幾個地方是以身分檢查自己的方塊，所以明明該能運作的資源包方塊常常不行。兩個鍵可以解決。

```json
{
  "material": "ground",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "hardness": 0.6 } }
}
```

**`plantTypes`** 列出你的方塊支援的植物類型，這樣樹苗、作物與花就能種在上面：`plains`、`desert`、`beach`、`cave`、`water`、`nether` 與 `crop`。**1.21.1** 完全沒有植物類型；在那裡，`behavesAs` 中的 `bush` 做的是 `plains` 原本做的事，其餘由植物自己的土壤清單決定，這個鍵會被讀取後忽略。

**`behavesAs`** 讓原版把你的方塊當成它自己的某種方塊：

| 值        | 作用                                                                                |
| --------- | ----------------------------------------------------------------------------------- |
| `till`    | 鋤頭會把它變成耕地，或變成設定中 `hoeTillsInto` 所指定的東西                        |
| `path`    | 鏟子會把它變成土徑，或變成 `shovelPathBecomes` 所指定的東西                         |
| `bush`    | 花、草與樹苗可以種在上面並留在上面，就像泥土一樣。等同於 `plantTypes` 中的 `plains` |
| `animals` | 動物在光照下會在上面生成，就像在草方塊上一樣                                        |

## 物品

*方塊與物品*

`<namespace>/items/*.json`

`variants` 內的每個鍵都是一個物品，登錄在資源包的命名空間之下，所以含有 `ruby_apple` 與 `dried_ruby_apple` 的檔案，會登錄 `mypack:ruby_apple` 與 `mypack:dried_ruby_apple`；檔案本身的名稱只是分組。除非資源包附帶 `models/item/<name>.json`，否則每個物品的模型都由 `textures/item/<name>.png` 產生。

所有的鍵一次全部列出。實際的檔案只需寫出需要的那些。標示為適用於某一類型的鍵，只有該類型才會讀取。

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
  "rollStore": "dice_total",
  "locates": "Village",
  "passesTurn": false,
  "attackReach": 3.0,
  "sweep": { "shape": "box", "damage": 0.5 },
  "combo": { "window": 30, "steps": [ { "damage": 1.0 }, { "damage": 2.0 } ] },
  "curioSlot": "ring",
  "wornEffects": [ { "potion": "minecraft:speed", "amplifier": 1 } ],
  "wornAttributes": [ { "attribute": "maxHealth", "amount": 10.0, "operation": 0 } ],
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

### 物品類型

*物品*

| 類型            | 你會得到什麼                                                                              |
| --------------- | ----------------------------------------------------------------------------------------- |
| `basic`         | 一般的物品。缺少 `type` 時使用                                                            |
| `food`          | 可食用，有飢餓值與飽和度                                                                  |
| `drink`         | 用喝的而不是吃的，並歸還空容器                                                            |
| `tool`          | 由材料製成的鎬子、斧頭、鏟子、鋤頭或劍                                                    |
| `armor`         | 由材料製成的頭盔、胸甲、護腿或靴子                                                        |
| `seed`          | 種下你的某個作物                                                                          |
| `potion`        | 使用時施加你的藥水效果                                                                    |
| `potion_bottle` | 容納你的藥水類型，並在創造模式分頁中顯示                                                  |
| `portway`       | 傳送到傳送通道：開啟列出所有站點的選單，或帶你前往已綁定的那一個，見[傳送通道](#傳送通道) |
| `container`     | 小袋：手持或配戴的物品欄，見[容器](#容器)                                                 |

`potion_bottle` 以 `potionTypes` 列出它能存放的內容，這是藥水類型名稱的陣列，例如 `["mypack:ruby_tonic"]`。清單為空的會什麼都不登錄，日誌會說明。

帶有 `locates` 的一般物品就是定位指南針。右鍵會尋找該名稱最近的地點並讓物品指向那裡，持有者會得知指針停在了哪裡。名稱可以是像 `Village` 這樣的原版結構、像 `minecraft:river` 這樣的生物群系 ID，或用 `locateAs` 註冊的名稱。該物品的堆疊上限為 1，`cooldown` 決定它在多長時間內拒絕再次搜尋。

`<namespace>/items/*.json`

```json
{
  "locates": "Village",
  "cooldown": 100,
  "variants": {
    "seeker": {
      "maxSize": 1
    }
  }
}
```

目標儲存在物品堆中，因此物品會一直指向它，直到下一次右鍵，在其他維度則會旋轉。指針由物品自己的模型繪製，使用與原版指南針相同的 `angle` 覆寫，每幀一項：

`assets/mypack/models/item/seeker.json`

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "mypack:item/seeker_16"
  },
  "overrides": [
    { "predicate": { "angle": 0.0 }, "model": "mypack:item/seeker_16" },
    { "predicate": { "angle": 0.03125 }, "model": "mypack:item/seeker_17" },
    { "predicate": { "angle": 0.0625 }, "model": "mypack:item/seeker_18" }
  ]
}
```

### 物品檔案鍵

*物品*

| 鍵               | 必填          | 值                                  | 預設                 | 作用                                                                                                                                                                                       |
| ---------------- | ------------- | ----------------------------------- | -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `variants`       | 是            | 變體名稱對應到變體的物件            |                      | 每個項目一個物品。鍵是它的登錄名稱，並決定其模型、材質與語言鍵的名稱                                                                                                                       |
| `type`           | 否            | 上列類型之一                        | `basic`              | 物品採用哪種類型                                                                                                                                                                           |
| `creativeTab`    | 否            | 分頁名稱                            | 無                   | 它出現在哪個分頁，見[創造模式分頁](#創造模式分頁)                                                                                                                                          |
| `material`       | 工具、盔甲    | 材料名稱                            | 無                   | 由你的哪個材料製成                                                                                                                                                                         |
| `toolClass`      | 工具          | `pickaxe`、`axe`、`shovel`、`sword` | 無                   | 是哪種工具。`sword` 是 1.12.2 的工具，而不是原版的劍：它造成 3 加上材料 `damage` 的傷害，能挖掘 `harvestTool` 為 `sword` 的方塊，接受挖掘類附魔，既不橫掃也不砍蜘蛛網                      |
| `slot`           | 盔甲          | `head`、`chest`、`legs`、`feet`     | 無                   | 穿戴的位置。`helmet`、`chestplate`、`leggings` 與 `boots` 也可用                                                                                                                           |
| `eat`            | 食物          | 布林值                              | `false`              | 使用食用動畫                                                                                                                                                                               |
| `alwaysEdible`   | 食物          | 布林值                              | `false`              | 飢餓值已滿時也能食用                                                                                                                                                                       |
| `useDuration`    | drink, potion | 整數，刻                            | `32`                 | 用於 `drink` 和 `potion`：喝下它需要多久，單位為刻（20 刻 = 1 秒）。其他類型會忽略它                                                                                                       |
| `attackReach`    | 否            | 浮點數，格                          | `3.0`                | 用主手物品攻擊時能打到的距離。對生物按右鍵時同樣適用。請參閱[武器](#武器)                                                                                                                  |
| `sweep`          | 否            | 物件                                | 無                   | 同時傷害滿力度一擊周圍的其他生物。請參閱[武器](#武器)                                                                                                                                      |
| `combo`          | 否            | 物件                                | 無                   | 間隔不超過 `window` 刻的連續攻擊依序走過 `steps`。請參閱[武器](#武器)                                                                                                                      |
| `attackSpeed`    | 否            | 浮點數                              | 依工具類別而定       | 對 `tool` 而言是攻擊速度屬性，劍的寫法是 `-2.4`                                                                                                                                            |
| `cooldown`       | 否            | 整數，刻                            | `0`                  | 對 `food`、`drink` 與 `potion` 而言，物品被消耗後拒絕再次使用的時間；對會 `rolls` 的物品而言，則是兩次擲骰之間的間隔                                                                       |
| `container`      | 飲品          | 物品名稱                            | 無                   | 留下的東西，例如瓶子。在 `container` 物品上，這個鍵改為小袋自己的設定，見[容器](#容器)                                                                                                     |
| `crop`           | 種子          | 方塊名稱                            | 無                   | 它種下的作物                                                                                                                                                                               |
| `soil`           | 種子          | 方塊名稱                            | `minecraft:farmland` | 可以種在什麼上面                                                                                                                                                                           |
| `rolls`          | 否            | `coin`、`d6`、`2d6+1`、骰子或牌組   | 無                   | 對一般物品而言，右鍵會像 `/rdplserver game` 那樣擲它，並告知資源包的預設對象。見[骰子與牌組](#骰子與牌組)                                                                                  |
| `rollStore`      | 否            | 目標名稱                            | 無                   | 用於帶 `rolls` 的物品：每次使用時，把擲骰總和作為持有者的分數記入這個計分板目標。目標不存在時，擲骰會被拒絕並在聊天中提示一行                                                              |
| `portway`        | portway       | 物件                                | 無                   | 傳送物品的設定：與行程相關的 [傳送通道鍵](#傳送通道)，加上 `consume` 與 `bind`，見[傳送物品](#傳送物品)                                                                                    |
| `curioSlot`      | 否            | Curios 欄位名稱                     | 無                   | 物品可配戴的 Curios 欄位，適用於任何類型的物品。未安裝 Curios 時會被忽略                                                                                                                   |
| `wornEffects`    | 否            | 效果清單                            | 無                   | 物品位於其 Curios 欄位期間持續作用於配戴者的藥水效果，寫作 `potion`、`amplifier`、`ambient` 與 `showParticles`                                                                             |
| `wornAttributes` | 否            | 屬性修改清單                        | 無                   | 物品位於其 Curios 欄位期間持續作用於配戴者的屬性修改，寫作 `attribute`、`amount` 與 `operation`                                                                                            |
| `locates`        | 否            | `Village`, `minecraft:river`        | 無                   | 對一般物品而言，右鍵會尋找該名稱最近的地點，並像指南針一樣讓物品指向那裡。名稱可以是原版結構或生物群系，也可以是用 `locateAs` 註冊的名稱。該物品的堆疊上限為 1。請參閱上方的定位指南針段落 |
| `passesTurn`     | 否            | 布林值                              | `false`              | 對一般物品而言，右鍵會像 `/rdplserver game pass` 那樣讓持有者跳過回合。見[輪流行動](#輪流行動)                                                                                             |
| `requires`       | 否            | 模組 id 或資源包命名空間清單        | 無                   | 除非全部存在，否則跳過該檔案                                                                                                                                                               |

### 物品變體鍵

*物品*

| 鍵           | 必填       | 值                                   | 預設     | 作用                                                                                                                                                                |
| ------------ | ---------- | ------------------------------------ | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `maxSize`    | 否         | 1 到 64                              | `64`     | 堆疊數量                                                                                                                                                            |
| `rarity`     | 否         | `common`、`uncommon`、`rare`、`epic` | `common` | 提示文字中的名稱顏色                                                                                                                                                |
| `healAmount` | 食物       | 整數，半根雞腿                       | `0`      | 恢復的飢餓值                                                                                                                                                        |
| `saturation` | 食物       | 浮點數                               | `0.0`    | 恢復的飽和度                                                                                                                                                        |
| `tags`       | 否         | 標籤 id 清單                         | 無       | 此變體會被寫入的物品標籤；標籤檔案會自動產生                                                                                                                        |
| `potion`     | 食物、飲品 | `potion,duration,amplifier`          | 無       | 此變體被吃下或喝下時施加的效果。第四個部分 `true` 會讓它變成環境效果。有益效果在提示文字中以綠色命名，等級高於 0 時後面接羅馬數字，並且它給的任何效果都不會顯示粒子 |

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
      { "damage": 2.0, "sweep": { "shape": "line", "reach": 5.0, "width": 2.0 } },
      { "damage": 3.0, "sweep": { "shape": "circle", "reach": 3.0 } }
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

檔案的路徑就是流體方塊的登錄名稱。`name` 指定流體本身、它的桶與語言鍵的名稱，除非檔案另行設定，否則就是檔案的路徑。

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

| 鍵            | 必填 | 值                           | 預設             | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ------------- | ---- | ---------------------------- | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | 否   | 字串                         | 檔案名稱         | 流體及其桶的登錄名稱（`<name>_bucket`）；方塊則保留檔案的路徑                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `still`       | 否   | 材質路徑                     | 原版水的靜止材質 | 靜止流體的材質                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `flow`        | 否   | 材質路徑                     | 原版水的流動材質 | 流動流體的材質                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `color`       | 否   | 十六進位顏色                 | 無               | 套用在那些材質上的色調。在預設水材質上，它會乘以 1.12.2 水的藍色，所以為 1.12.2 挑選的顏色在這裡看起來一樣                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `bucket`      | 否   | 布林值                       | `true`           | 為它登錄一個桶                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `luminosity`  | 否   | 0 到 15                      | `0`              | 發出的光                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `density`     | 否   | 整數                         | `1000`           | 負值會像氣體一樣向上浮                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `temperature` | 否   | 整數，克耳文                 | `300`            | 水是 300，熔岩是 1300                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `viscosity`   | 否   | 整數                         | `1000`           | 它流動得多慢：流體每 viscosity / 200 刻前進一次。水是 1000，熔岩是 6000                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `gaseous`     | 否   | 布林值                       | `false`          | 當作氣體處理                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `creativeTab` | 否   | 分頁名稱                     | 無               | 桶出現的分頁                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `block`       | 否   | 物件                         |                  | 流體方塊。`material`（`water`）：`water` 可以游泳與溺水、讓船漂浮、撲滅燃燒中的生物、讓農地保持濕潤，並在地獄倒入時蒸發；`lava` 會點燃站在其中的一切、不能游泳或溺水，並使用熔岩桶的音效；任何其他材料都不做這些事。`flammability`（`0`）與 `fireSpread`（`0`）：火焰燃盡此方塊以及從它蔓延的容易程度。`quantaPerBlock`（`0`，視為 8）：它從水源流出多遠，與 1.12.2 相同，比數字少一格；這裡的流體可到達 1、2、3 或 7 格，所以 4 與 5 流 3 格，6 以上流 7 格。`potions`（無，一份效果清單，給予站在其中的一切，每個寫成 `potion,duration,amplifier`，可選的第四部分 `true` 表示環境效果） |
| `requires`    | 否   | 模組 id 或資源包命名空間清單 | 無               | 除非全部存在，否則跳過該檔案                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |

## 材料、分頁、音效、標籤

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

| 鍵               | 必填 | 值             | 預設                    | 作用                                                                                                 |
| ---------------- | ---- | -------------- | ----------------------- | ---------------------------------------------------------------------------------------------------- |
| `harvestLevel`   | 否   | 0 到 4         | `1`                     | 工具等級。0 木、1 石、2 鐵、3 鑽石、4 獄髓                                                           |
| `durability`     | 否   | 整數           | `250`                   | 損壞前可使用的次數                                                                                   |
| `efficiency`     | 否   | 浮點數         | `6.0`                   | 挖掘速度。鑽石是 8                                                                                   |
| `damage`         | 否   | 浮點數         | `2.0`                   | 攻擊傷害加成                                                                                         |
| `enchantability` | 否   | 整數           | `14`                    | 附魔的優劣程度。金是 22                                                                              |
| `repairItem`     | 否   | 物品名稱       | 無                      | 在鐵砧中修理以它製成之工具的材料。以它製成的盔甲不能這樣修理，與 1.12.2 相同                         |
| `reduction`      | 否   | 四個整數的清單 | `[2, 5, 6, 2]`          | 盔甲值，依靴子、護腿、胸甲、頭盔的順序                                                               |
| `toughness`      | 否   | 浮點數         | `0.0`                   | 盔甲韌性，如同鑽石所具有的                                                                           |
| `equipSound`     | 否   | 音效名稱       | `item.armor.equip_iron` | 穿上盔甲時的聲音                                                                                     |
| `armorTexture`   | 否   | 材質前綴       | 檔案名稱                | 穿著時的盔甲材質，從該命名空間下的 `textures/models/armor/<name>_layer_1.png` 與 `_layer_2.png` 讀取 |

### 創造模式分頁

*材料、分頁、音效、標籤*

`<namespace>/tabs/*.json`

方塊、物品與桶在 `creativeTab` 中指定它們的分頁。完整 id，如 `mypack:rubypack` 或 `minecraft:combat`，會照寫的使用。單獨的名稱則按 1.12.2 讀取分頁標籤的方式處理：`buildingBlocks` 對應 `minecraft:building_blocks`，`decorations` 對應 `minecraft:functional_blocks`，`redstone` 對應 `minecraft:redstone_blocks`，`transportation` 與 `tools` 對應 `minecraft:tools_and_utilities`，`misc` 與 `materials` 對應 `minecraft:ingredients`，`food` 與 `brewing` 對應 `minecraft:food_and_drinks`，`combat` 對應 `minecraft:combat`，其他任何單獨的名稱，則是指定它的檔案所屬命名空間中的分頁 `<namespace>:<name>`。沒有任何檔案宣告的分頁會自動建立，標題取自 `itemGroup.<namespace>.<name>`，並以其中第一個物品作為圖示。分頁檔案的路徑就是分頁的名稱，除非 `label` 另行指定。

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| 鍵      | 必填 | 值       | 預設     | 作用                                                                                                         |
| ------- | ---- | -------- | -------- | ------------------------------------------------------------------------------------------------------------ |
| `label` | 否   | 字串     | 檔案名稱 | 分頁的 id：方塊與物品在 `creativeTab` 中指定它，顯示的名稱則來自語言檔案中的 `itemGroup.<namespace>.<label>` |
| `icon`  | 否   | 物品名稱 | 無       | 顯示在分頁上的物品                                                                                           |

### 音效

*材料、分頁、音效、標籤*

`<namespace>/sounds/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

原版的 `sounds.json` 格式，所以資源包可以附帶自己的音訊。這裡的檔案登錄音效事件；用戶端仍透過資源包自己的 `assets/<namespace>/sounds.json` 讀取音訊，所以兩者都要附上，索引放在 `assets` 下，事件放在 `data` 下。

### 標籤

*材料、分頁、音效、標籤*

`<namespace>/tags/<kind>/*.json`

標籤是遊戲自己的格式，放在遊戲自己的資料夾中，資源包像附帶在資料包裡一樣附上它們：`tags/items/ores/ruby.json`（1.21.1：`tags/item/`）內含 `{ "values": ["mypack:ruby_ore"] }`，就把這個礦石放進 `mypack:ores/ruby`，而 `data/forge/tags/items/ores/ruby.json`（1.21.1：`data/c/...`）下的檔案，則是加進每個模組都會讀取的共用慣例標籤。資源包自己的方塊與物品則改在變體的 `tags` 中指定其標籤，檔案會替你寫出；`harvestTool` 與 `harvestToolLevel` 同樣會寫出 `mineable` 與 `needs_*_tool` 標籤。

1.12.2 的礦物辭典就是標籤所取代的東西。它的名稱對應到慣例標籤：`oreRuby` 在 1.20.1 上是 `forge:ores/ruby`，在 1.21.1 上是 `c:ores/ruby`，`ingotCopper` 是 `ingots/copper`，`gemRuby` 是 `gems/ruby`，`dustX` 是 `dusts/x`，`nuggetX` 是 `nuggets/x`，`blockX` 是 `storage_blocks/x`，而 `logWood`、`plankWood` 與 `stickWood` 則是遊戲自己的 `minecraft:logs`、`minecraft:planks` 與慣例的 `rods/wooden`。標籤檔案的 `"remove": [...]` 會從標籤中拿掉單一項目，`"replace": true` 搭配空的 `"values": []` 則將它清空，所以來自較低層資源包或模組的標籤可以被縮減或清除。若要一次把物品從所有標籤中移除，並退出遊戲，請用[停用的方塊與物品](#停用的方塊與物品)。

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

| 鍵             | 值                                           | 作用                                                                                                                                                      |
| -------------- | -------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `hardness`     | 浮點數                                       | 挖掘時間，與方塊定義所用的數值相同。沒有 `resistance` 時，它也會把爆炸抗性提高到至少同樣的數值，與 1.12.2 相同                                            |
| `resistance`   | 浮點數                                       | 以 1.12.2 的讀法計算的爆炸抗性：方塊只保留該數值的五分之三，所以 `10` 會得到石頭的 `6`                                                                    |
| `slipperiness` | 浮點數                                       | `0.6` 是一般地面，`0.98` 是冰                                                                                                                             |
| `light`        | `0` 到 `15`                                  | 發出的光                                                                                                                                                  |
| `lightOpacity` | `0` 到 `15`                                  | 方塊擋住多少光                                                                                                                                            |
| `soundType`    | 音效類型之一                                 | 腳步、放置與破壞的聲音                                                                                                                                    |
| `harvestTool`  | `pickaxe`、`axe`、`shovel`、`hoe` 或 `sword` | 以什麼快速挖掘，會寫入工具標籤；`harvestToolLevel` 預設為 `0`，決定等級：1 石、2 鐵、3 鑽石、4 以上獄髓。沒有正確工具時方塊會不會掉落，維持方塊原本的設定 |
| `flammability` | 整數                                         | 它多容易燒盡；`fireSpread` 預設為 `5`，是火焰蔓延到它的容易程度                                                                                           |

### 物品屬性

*屬性覆寫*

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

在 `overrides/minecraft/oak_planks.json` 中，這會讓木板的破壞速度大約跟泥土一樣快，並讓它們可以被吃。`food` 接受 `heal`（`1`）、`saturation`（`0.6`）、`alwaysEdible`（`false`；`true` 允許在飢餓值已滿時食用）與 `effects`，其項目的寫法與藥水類型的完全相同。本來就是食物的物品會採用新的 `heal`、`saturation` 與 `alwaysEdible`；其上的 `effects` 不受支援，日誌會說明。當可食用物品會放置方塊時，請瞄準天空來吃，因為瞄準方塊會放置它：這是原版的使用順序，不是錯誤。

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

覆寫是即時的。原始數值在第一次更改前就被記住，所以停用資源包並執行 `/rdplserver reload`，就會讓一切恢復原狀，不必重新啟動；每次進入世界時也會發生同樣的事。每個目標一個檔案：兩個資源包覆寫同一個東西時，較後面資源包的檔案會整個取代較前面的，日誌會說明。

兩個值得知道的限制。自己的程式碼會計算某個屬性的方塊或物品會忽略其背後的欄位，所以覆寫雖然套用，卻不會改變任何東西；原版只有階梯的爆炸抗性這樣做，但模組可以在任何地方這樣做。此外，改為可食用的物品只對沒有自己右鍵行為的物品有效：已經在使用時會做某件事的物品，會繼續做那件事。

覆寫需要資源包同時放在用戶端與伺服器上，因為挖掘速度、光與食用都發生在玩家的畫面上，所以不適用於伺服端資源包。`content` 設定類別中的 `overrides` 可以完全關閉該資料夾。

## 硬度群組

*方塊與物品*

`<namespace>/hardness/*.json`

檔案的路徑只用來在日誌中稱呼該群組，沒有其他東西會讀取，所以多個檔案可以疊加。

為一組方塊提供挖掘時間倍率，依每個方塊的位置隨機取值。方塊本身不會被更改：不登錄任何東西，不往世界寫入任何東西，沒有資源包就開啟的世界就是一般的原版。

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

### 挖掘與爆破

*硬度群組*

| 鍵                | 必填 | 值                               | 預設     | 作用                                                       |
| ----------------- | ---- | -------------------------------- | -------- | ---------------------------------------------------------- |
| `blocks`          | 是   | 方塊名稱或物件的清單             |          | 該群組。與世界生成 `replace` 的形式相同                    |
| `except`          | 否   | 方塊名稱或物件的清單             | 無       | 不論 `blocks` 怎麼寫，都會從群組中拿出                     |
| `miningTime`      | 否   | 數字，或含 `min` 與 `max` 的物件 | `1.0`    | 方塊的破壞要花多少倍時間，對玩家與會 `digs` 的生物一視同仁 |
| `blastResistance` | 否   | 數字，或含 `min` 與 `max` 的物件 | `1.0`    | 乘上方塊的爆炸抗性                                         |
| `buckets`         | 否   | 1 到 256                         | `10`     | 範圍被分成多少個步驟                                       |
| `minHeight`       | 否   | 整數                             | 世界底部 | 低於此高度，隨機值固定為最難的那一級                       |
| `maxHeight`       | 否   | 整數                             | 世界頂部 | 高於此高度，隨機值固定為最難的那一級                       |
| `field`           | 否   | 物件                             | 見下文   | 隨機結果聚集而成的形狀                                     |
| `requires`        | 否   | 模組 id 或資源包命名空間清單     | 無       | 除非全部存在，否則跳過該檔案                               |

單一數字會讓群組中的每個方塊有相同倍率，不做任何隨機取值。`min` 與 `max` 則依位置隨機取值：分佈場為空處是 `max`，團塊中央是 `min`，兩者之間的步數由 `buckets` 決定。

### 冒險挖掘與解鎖

*硬度群組*

| 鍵            | 必填 | 值               | 預設    | 作用                                                                                                                                                                                                                                                                                                     |
| ------------- | ---- | ---------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `keeps`       | 否   | 布林值           | `false` | 挖掉後方塊仍留在原處：掉落物、經驗值、工具磨損與破壞音效都照常發生，方塊仍在原地可以再次挖掘，因此該群組是一條不會枯竭的礦脈，速度由 `miningTime` 決定。創造模式照常能移除它                                                                                                                             |
| `adventure`   | 否   | 物件             | 無      | 誰可以在冒險模式下破壞該群組，那裡原本什麼都無法破壞。`tools` 列出手上必須持有其中之一的物品，留空則持有任何東西都可以；`teams`、`players` 與 `entities` 指定誰，隊伍用名稱、玩家用名字、生物則用實體 id（供 `digs` 任務使用），三者都留空表示持有該工具的任何人。生存與創造模式不受影響                 |
| `advancement` | 否   | `namespace:path` | 無      | 玩家必須先取得該進度，此群組才對其生效。兩個群組可以指定同一個方塊，一個有進度條件、一個沒有，已解鎖的那個優先；沒有該進度的玩家會得到一般群組，若沒有則是原版。生物沒有進度，所以有進度條件的群組永遠不會用於 `digs` 任務，而爆炸抗性與材質的隨機結果不屬於任何玩家，取自一般群組                       |
| `becomes`     | 否   | 物件             | 無      | 一旦任何玩家獲得 `advancement`，該群組的方塊就會在整個世界範圍內變成另一種方塊：`{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`。每個已載入的區塊會立刻被掃過一遍，之後載入或生成的區塊也會在進入時被掃過，因此舊方塊就此永遠消失。替新方塊指定一個自己的群組，即可改變它的挖掘方式 |

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

清單依由軟到硬的相反順序讀取：最後一項是最軟的檔位，第一項則比最硬的檔位高一檔。依上面的數值，各檔機率合計為千分之 190，因此每千個方塊中有 810 個起初就落在最硬的檔位；接著擴散會把其中一部分調高，最後約有十分之七的方塊停在最硬的檔位，其餘的則散布在各檔之間。

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
    "": [
      { "model": "mypack:block/stone_step0", "weight": 1 },
      { "model": "mypack:block/stone_step1", "weight": 1 }
    ]
  }
}
```

Minecraft 本來就會依方塊的位置挑選變體，而硬度群組把這個挑選的桶子交給它，因此材質與倍率始終一致。範例資源包的紅寶石石頭正是這樣做的。

有三件事必須正確，而且錯的時候沒有一件會自己出聲。

**剛好 `buckets` 個項目，權重全都相同。** 分段被當作清單中的位置使用，所以長度不同的清單，或權重不同的清單，會悄悄指向錯誤的材質。

**模型名稱前面要加 `block/`。** 在這個版本上，方塊狀態要寫出模型檔案的完整名稱，所以 `"model": "mypack:block/stone_step0"` 讀取的是 `models/block/stone_step0.json`；而只寫 `mypack:stone_step0` 會去找並不存在的 `models/stone_step0.json`，該項目會被默默丟棄。

**鍵要與遊戲查詢的鍵一致。** 只有一種狀態的方塊，鍵寫作 `""`，原版石頭就是這樣。帶有屬性的方塊，鍵由它的所有屬性組成，所以覆寫原木時需要 `axis=x`、`axis=y` 和 `axis=z`，各自帶一個清單。

開啟 `worldgenDebug` 後，進入世界時會對照烘焙後的模型檢查每個硬度群組，並指出方塊狀態、有多少變體留下、每個變體最後採用什麼材質，以及遊戲為此合併了哪些資源包。這是找出以上三種問題最快的方法，當覆寫共用方塊狀態改變了群組從未指定的狀態時，它也會發出警告。

### 它不涉及的範圍

*硬度群組*

只有玩家自己的挖掘會被改變。破壞方塊的機器直接讀取方塊的硬度，不受影響。玩家放置的方塊和其他方塊一樣隨機取值，因為隨機結果綁定的是位置而不是方塊，被搬到別處的方塊會改用新位置所決定的數值。

---

# 合成、戰利品與交易

## 停用的方塊與物品

*合成、戰利品與交易*

`<namespace>/disabled/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

讓方塊和物品退出遊戲，但不取消註冊，所以世界會保留它們的 id，刪除該檔案就能讓一切恢復。原版、模組與資源包的內容一視同仁，資源包自己的方塊和物品也包括在內，被停用的方塊會連帶停用它的物品，被停用的物品也會連帶停用它的方塊。

```json
{
  "requires": ["thermal"],
  "names": ["thermal:tin_ore", "thermal:deepslate_tin_ore", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "tags": ["forge:ores/tin"]
}
```

| 鍵           | 必填 | 值                   | 預設 | 作用                                                                                                 |
| ------------ | ---- | -------------------- | ---- | ---------------------------------------------------------------------------------------------------- |
| `names`      | 否   | 方塊與物品名稱的清單 | 無   | 要停用的內容。以 `*` 結尾的名稱會匹配所有以其餘部分開頭的名稱                                        |
| `namespaces` | 否   | 模組 id 清單         | 無   | 該模組的所有方塊與物品                                                                               |
| `tags`       | 否   | 標籤名稱的清單       | 無   | 該名稱的物品標籤中的所有物品，以及方塊標籤中的所有方塊，且這兩個標籤隨後都會被清空。允許在前面加 `#` |
| `requires`   | 否   | 模組 id 清單         | 無   | 除非列出的模組全部已載入，否則會略過該檔案。`config:` 與 `file:` 項目的作用和其他各處相同            |

被停用的方塊或物品：

- 從所有創造模式分頁與搜尋分頁中消失，並在 JEI 中隱藏
- 沒有以它為產物或原料的配方：以它為結果的所有類型的配方都會被移除，包括合成、烹飪、切石與鍛造，只有它能填入的欄位所在的配方也一併移除。欄位還能放其他東西的配方會保留，而標籤欄位則隨標籤一起失去它
- 從所有物品標籤與方塊標籤中移除，改為列在 `resourcedatapackloader:disabled` 中
- 從所有戰利品抽取中剔除，包括箱子、生物與釣魚，也從方塊掉落物以及村民與流浪商人的交易中剔除，掉落出來的該物品堆疊會消失
- 無法放置、使用、揮動或撿起，玩家嘗試時手中的堆疊會被刪除
- 無論該物品的堆疊出現在哪裡都會被刪除：玩家的物品欄和終界箱在登入時以及之後每秒刪除，玩家打開任何容器時從該容器刪除，箱子和其他物品欄則在其區塊載入時刪除
- 會從已放置的世界中移除：隨著區塊載入，它的每個方塊都會變成空氣，其方塊實體也一併消失

若想把已放置的方塊換成別的東西而不是直接移除，請在世界範本中替它們寫一行 `blockReplacements`，例如 `thermal:tin_ore=minecraft:stone`，參見[取代](#取代)。被取代流程換掉的方塊交由該流程處理。其他模組保存在自己機器內部的配方屬於那個模組，這裡無法觸及。從創造模式分頁與 JEI 中隱藏是在用戶端進行的，因此原版用戶端仍會列出該物品。設定中的 `content.disabled` 可以關閉這個資料夾。

若要清空一個標籤但讓其中的物品繼續留在遊戲中，請改用帶有 `"replace": true` 且 `"values": []` 為空的標籤檔案，參見[標籤](#標籤)。

## 熔爐配方與燃料

*合成、戰利品與交易*

`<namespace>/furnace/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

新增與移除熔爐配方。移除時也會一併去掉對應的高爐、煙燻爐與營火配方，因為 1.12.2 把所有烹飪配方都放在同一份熔爐清單裡；新增則只新增熔爐配方。

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

| 鍵           | 必填 | 值       | 預設  | 作用                                                                                                               |
| ------------ | ---- | -------- | ----- | ------------------------------------------------------------------------------------------------------------------ |
| `input`      | 是   | 物品名稱 | 無    | 放入什麼                                                                                                           |
| `output`     | 是   | 物品名稱 | 無    | 產出什麼                                                                                                           |
| `count`      | 否   | 整數     | `1`   | 產出多少個                                                                                                         |
| `experience` | 否   | 數字     | `0.0` | 每燒煉一個物品獲得的經驗值，最多一點：1.0 或更高時，每取出一個物品給一點，所以 `count` 為 2 就給兩點。鐵礦石給 0.7 |

如果某個輸入已經有配方可以燒煉，那麼針對它的新增會被忽略，日誌會指出擋路的那條配方，這與 1.12.2 一致；要取代它，請在同一檔案或更早的檔案中先把那條配方移除。

`remove` 底下的項目可以是單純的物品名稱，這會移除所有產出它的配方，也可以是指明 `input`、`result` 或兩者以縮小範圍的物件。兩者都沒有指明的移除項會被略過，日誌會說明這一點。

檔案依載入順序生效，每個檔案先執行移除再執行新增。因此後面檔案中的移除也會去掉更早檔案所做的新增，但永遠影響不到排在它之後的新增。新增的配方算作其產物所屬模組的熔爐配方，所以 `blockFurnaceRecipes` 和 `blockedFurnaceMods` 會像對待其他配方一樣封鎖它。

`<namespace>/fuels/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "tag": "forge:gems/ruby", "burnTime": 800 }
  ]
}
```

| 鍵         | 必填   | 值             | 預設 | 作用                       |
| ---------- | ------ | -------------- | ---- | -------------------------- |
| `item`     | 二選一 | 物品名稱       | 無   | 會燃燒的物品               |
| `tag`      | 二選一 | 標籤 id        | 無   | 該標籤中的所有物品都會燃燒 |
| `burnTime` | 是     | 整數，單位為刻 | `0`  | 煤炭是 1600，木板是 300    |

## 配方鎖

*合成、戰利品與交易*

`<namespace>/recipe_locks/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。在玩家取得進度之前，讓合成配方保持鎖定。

```json
{
  "advancement": "mypack:deep_miner",
  "names": ["minecraft:diamond_pickaxe", "thermalfoundation:*"],
  "namespaces": ["tconstruct"],
  "outputs": ["minecraft:diamond_sword"]
}
```

| 鍵            | 必填 | 值               | 預設 | 作用                                                                                  |
| ------------- | ---- | ---------------- | ---- | ------------------------------------------------------------------------------------- |
| `advancement` | 是   | `namespace:path` | 無   | 玩家合成這些配方所需的進度。沒有此鍵的檔案會被略過，日誌會註明                        |
| `names`       | 否   | 清單             | 無   | 要攔下的配方名稱。結尾的 `*` 會比對所有以該文字開頭的名稱，例如 `thermalfoundation:*` |
| `namespaces`  | 否   | 清單             | 無   | 這些命名空間的所有配方                                                                |
| `outputs`     | 否   | 物品名稱清單     | 無   | 合成其中任一物品的所有配方                                                            |

沒有該進度的玩家，在符合的配方上會看到空的結果格，工作台、物品欄的 2x2 合成格以及配方書填入合成格時都是如此；擁有該進度的玩家照常合成。熔爐、鐵砧和自動合成器不受影響。多個檔案會疊加，同時符合兩個檔案的配方需要兩個進度都具備。沒有選中任何配方的檔案會被略過，日誌會註明。

## 工具提示

*物品、方塊與附魔*

`<namespace>/tooltips/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。如果已載入的語言檔中有該鍵，每一行就是語言鍵，否則視為純文字。物品的行加在最後，顯示為灰色；附魔的行緊接在附魔名稱下方，附魔書和附魔物品都一樣。設定中的 `content.tooltips` 可關閉這個資料夾。

```json
{
  "items": [
    { "item": "mypack:ruby", "lines": ["tooltip.mypack.ruby", "Found deep underground."] }
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

| 鍵            | 必填 | 值       | 預設 | 作用                     |
| ------------- | ---- | -------- | ---- | ------------------------ |
| `item`        | 是   | 物品名稱 | 無   | 物品，或方塊對應的物品   |
| `enchantment` | 是   | 附魔名稱 | 無   | 獲得這些行的附魔         |
| `lines`       | 是   | 文字清單 | 無   | 語言鍵或純文字，依序顯示 |

## 語言閘門

*隨資源包選項而定的語言條目*

`<namespace>/langgates/<name>/gate.json`

閘門是一個存放語言條目的資料夾，這些條目只在它的 `requires` 成立期間載入。資料夾裡有一個 `gate.json`，以及每個語言代碼一個檔案，例如 `en_us.json`，寫法與 `<namespace>/lang/` 裡的檔案相同，只包含閘門要改動的條目。閘門成立時，它的條目會加入該語言，並壓過資源包裡相同的鍵；否則語言原樣提供。

`assets/mypack/langgates/stupid/gate.json`

```json
{ "requires": ["config:stupidNames"] }
```

`assets/mypack/langgates/stupid/en_us.json`

```json
{ "block.mypack.block_a": "Stupid Block!" }
```

| 鍵         | 必填 | 值       | 預設     | 作用                                                                                           |
| ---------- | ---- | -------- | -------- | ---------------------------------------------------------------------------------------------- |
| `requires` | 否   | 文字清單 | 一律生效 | 每一項都必須成立。`config:` 選項、`file:` 路徑、模組 ID 和資源包命名空間的用法與其他定義中相同 |

閘門依資源包的順序生效，其次依資料夾名稱（不分大小寫），較後面的勝出。同一個鍵的兩個閘門構成「或」的關係，因為每個閘門各自成立即可。閘門補充的是它自己所在[層級](#資源包誰優先)的語言，無論 `lang` 檔案或閘門在哪個資源包裡，所以標記為 `O` 的資源包中的 `lang` 檔案仍然壓過標記為 `N` 的資源包中的閘門。針對遊戲本體或其他模組的鍵的閘門，放在該命名空間下的 `assets/minecraft/langgates/<name>/`，疊加在模組自帶的檔案之上。如果任何地方都沒有該代碼的 `lang` 檔案，成立的閘門會單獨提供這門語言，不成立的閘門則什麼也不新增。

沒有 `gate.json` 的資料夾會被略過，日誌會說明；不是 JSON 物件的檔案同樣會被略過，且只提示一次。`requires` 裡的模組 ID 會讓遊戲要求安裝該模組，這與其他各處一致；如果條目只想在裝了某個模組時才生效，請使用 `file:` 路徑或資源包命名空間。

在資源包選項畫面按下「完成」時，如果改動的選項是閘門讀取的，語言會立即重新載入，無需重新啟動。其他變更，例如 `file:` 項，在按下 F3+T 後生效。

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
  "iconTexture": "mypack:textures/effect/ruby_sight.png",
  "attributes": [
    { "attribute": "minecraft:generic.movement_speed", "uuid": "91AEAA56-376B-4498-935B-2F7F68070635", "amount": 0.2, "operation": 2 }
  ]
}
```

`<namespace>/potions/*.json`

```json
{
  "color": "C0304A",
  "icon": { "x": 2, "y": 1 }
}
```

| 鍵              | 必填 | 值           | 預設                              | 作用                                                                                                                                            |
| --------------- | ---- | ------------ | --------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`          | 否   | 翻譯鍵       | `effect.<namespace>.<name>`       | 玩家看到的名稱                                                                                                                                  |
| `color`         | 否   | 十六進位色碼 | `FFFFFF`                          | 粒子顏色                                                                                                                                        |
| `badEffect`     | 否   | 布林值       | `false`                           | 視為有害效果，因此發酵蜘蛛眼會將其反轉                                                                                                          |
| `beneficial`    | 否   | 布林值       | `false`                           | 顯示為增益效果                                                                                                                                  |
| `instant`       | 否   | 布林值       | `false`                           | 一次性生效，而不是隨時間持續                                                                                                                    |
| `effectiveness` | 否   | 浮點數       | `0.5`                             | 接受它只是為了讓 1.12.2 的檔案能夠載入，在所有版本中都不起作用；1.12.2 原版同樣從不讀取 Potion.effectiveness                                    |
| `attributes`    | 否   | 物件清單     | 無                                | `attribute`（遊戲中的 id，例如 `minecraft:generic.movement_speed`）、`uuid`（1.12.2 會讀取，此版本忽略）、`amount`（`0.0`）、`operation`（`0`） |
| `icon`          | 否   | 物件         | 無                                | `x` 和 `y`，即圖示在 1.12.2 狀態圖集中的欄與列，不寫則各為 `0`。僅在沒有 `iconTexture` 時讀取                                                   |
| `iconTexture`   | 否   | 材質路徑     | RDPL 圖示，設定了 `icon` 時則為無 | 資源包自帶的圖片，例如 `mypack:textures/effect/rage.png`，整張繪製為圖示                                                                        |

效果的圖示是材質 `assets/<namespace>/textures/mob_effect/<name>.png`，與遊戲自帶的一樣是 18 x 18，資源包在該處提供的檔案永遠優先。否則，`iconTexture` 指向資源包自帶的圖片，會被整張複製到該處；單獨寫 `icon` 則依圖示在 1.12.2 狀態圖集中的位置挑選原版效果的圖示，`x` 向右、`y` 向下，均從 0 起算，例如跳躍提升是 `{ "x": 2, "y": 1 }`；兩者都沒寫的效果會顯示 RDPL 圖示。

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

| 鍵         | 必填 | 值       | 預設           | 作用                                                                                                                                                                                                                                           |
| ---------- | ---- | -------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `baseName` | 否   | 字串     | 命名空間與名稱 | 為藥水命名：語言鍵 `item.minecraft.potion.effect.<baseName>`，其他形態則把 `potion` 換成 `splash_potion`、`lingering_potion` 或 `tipped_arrow`。盛放它的 `potion_bottle` 顯示相同的名稱，1.12.2 資源包的 `potion.effect.<baseName>` 鍵會被轉換 |
| `effects`  | 是   | 物件清單 |                | 見下文                                                                                                                                                                                                                                         |

每個效果接受 `potion`（必填）、`duration`（`3600`）、`amplifier`（`0`）、`ambient`（`false`）和 `showParticles`（`true`）。

### 釀造

*藥水、藥水類型與釀造*

`<namespace>/brewing/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

每個項目要麼是 `input`、`ingredient` 和 `output`，把一種物品釀成另一種；要麼是 `from`、`ingredient` 和 `to`，把一種藥水類型變成另一種。無論哪一種，`ingredient` 都是必填，項目也接受 `requires`，這樣就能略過某一個配方，而不必略過整個檔案。

## 鐵砧操作

*合成、戰利品與交易*

`<namespace>/anvils/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。每個檔案對應一項操作。

將指定物品放入鐵砧左側欄位，並把它的 `with` 物品放入右側欄位，鐵砧便會交還附有所列附魔的左側物品，或交還其 `result`，所需等級即所列等級；除非數量要求更多，否則兩邊各消耗一個，任一堆疊中剩餘的部分仍留在鐵砧中。取出成品時還可以獲得一項進度，並且在獲得該進度之前，該物品可以被限制使用：一把必須經過加工才能揮動的劍。

```json
{
  "item": "minecraft:iron_sword",
  "with": "minecraft:wooden_sword",
  "result": "minecraft:golden_sword",
  "levels": 3,
  "enchantments": { "minecraft:sharpness": 2 },
  "grants": "mypack:sword_rite",
  "locks": true
}
```

| 鍵             | 必填 | 值                                               | 預設     | 作用                                                                                                                                                                                             |
| -------------- | ---- | ------------------------------------------------ | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `item`         | 是   | 物品名稱、物品名稱清單，或 `{ "item", "count" }` |          | 放入左欄的內容（清單中任意一種物品均可），以及一項操作消耗多少個，預設一個；堆疊中剩下的留給下一次。`{ "item": "minecraft:coal", "count": 8 }` 配上鑽石 `result`，就是八個煤炭換一顆鑽石         |
| `with`         | 是   | 物品名稱、物品名稱清單，或 `{ "item", "count" }` |          | 放入右欄的內容（清單中任意一種物品均可），以及消耗多少個，預設一個：`{ "item": "minecraft:coal", "count": 10 }` 要求至少一疊十個並消耗十個。鐵砧不會為單獨一件物品給出結果，所以每項操作都是一對 |
| `result`       | 否   | 物品名稱，或 `{ "item", "count" }`               | 左側物品 | 取代左側物品交出的內容及其數量，預設一個，並保留左側物品的標籤，所以一把不可破壞的鐵鎬加十個煤炭可以換回一把不可破壞的鑽石鎬。附魔會加在最終交出的那件物品上                                     |
| `levels`       | 否   | 整數                                             | `1`      | 這項操作花費的經驗等級，至少為 1                                                                                                                                                                 |
| `enchantments` | 否   | 附魔名稱到等級的物件                             | 無       | 物品交還時帶有的附魔。已經有同等或更高等級的附魔會保持不變，沒有任何可提升之處時鐵砧不會給出結果，除非設定了 `grants`                                                                            |
| `grants`       | 否   | `namespace:path`                                 | 無       | 取出成果時獲得的進度。請把它放在 `advancements/` 下，並使用 `impossible` 條件，這樣其他途徑就無法獲得它                                                                                          |
| `locks`        | 否   | 布林值                                           | `false`  | 在玩家獲得 `grants` 之前，該物品不能用來攻擊任何東西、不能使用，也不能用來挖掘；它進入玩家手中時，會告知玩家它在等待什麼。仍然允許把它放進鐵砧，這正是解鎖它的方式                               |

鐵砧本身的修復與合併不受影響：只有當左側是指定物品、右側是其 `with` 時，這項規則才會回應。

帶有 `collectsExperience` 的生物也會在這裡花費自己的等級。當它主手拿著 `item`、副手拿著 `with`，並且有足夠的 `levels` 可付時，它會走向水平 16 格、上下 4 格範圍內的鐵砧、開裂的鐵砧或損壞的鐵砧，進入 3 格以內後進行一次操作：等級從它自己身上扣除，與玩家被扣除的方式相同，`with` 被消耗，鐵砧像玩家使用時那樣磨損，成果最終落在它的主手上。當它走過某個鐵砧操作在 `with` 中指明的掉落物時，會把它撿起放入副手。`grants` 和 `locks` 只與玩家有關，所以生物不會從 `grants` 中獲得任何東西，也不受任何鎖定的限制。

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

| 鍵              | 必填 | 值                                          | 預設               | 作用                                                                                          |
| --------------- | ---- | ------------------------------------------- | ------------------ | --------------------------------------------------------------------------------------------- |
| `match`         | 是   | 物品名稱、`#tag` 或 `*`                     |                    | 規則針對的左欄物品。物品名稱優先於標籤，標籤優先於 `*`；同類之間以最後載入的檔案為準          |
| `repairWith`    | 否   | 物品名稱、`#tag` 或 `*`                     | 物品自身的修復材料 | 從右欄修復它的東西                                                                            |
| `repairPerUnit` | 否   | 大於 0 且不超過 1 的數                      | `0.25`             | 每個修復物品恢復的滿耐久比例                                                                  |
| `repairCost`    | 否   | 1 到 32767 的整數，或 `{ "multiplier": n }` | 原版               | 修復花費的等級。數字取代原版價格，大於 0 且不超過 100 的倍率按比例縮放它，四捨五入且至少為 1  |
| `combineCost`   | 否   | 同 `repairCost`                             | 原版               | 合併兩件該物品時的同類設定                                                                    |
| `bookCost`      | 否   | 同 `repairCost`                             | 原版               | 右欄為附魔書時的同類設定                                                                      |
| `penalty`       | 否   | `"vanilla"`、`"none"` 或 `{ "factor": n }`  | `"vanilla"`        | 累積懲罰每次使用後如何增長。原版將其加倍再加一，`none` 維持不變，0 到 16 的係數將其相乘再加一 |
| `repair`        | 否   | 布林值                                      | `true`             | 為 `false` 時鐵砧不修復它                                                                     |
| `combine`       | 否   | 布林值                                      | `true`             | 為 `false` 時鐵砧不合併兩件它；附魔書仍可使用                                                 |

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

| 鍵        | 必填 | 值                         | 預設   | 作用                                                                                  |
| --------- | ---- | -------------------------- | ------ | ------------------------------------------------------------------------------------- |
| `item`    | 是   | 物品名稱     |        | 進行隨機的物品。它不能堆疊：可堆疊的物品屬於錯誤，該檔案會被跳過                      |
| `rolls`   | 否   | `{ "min", "max" }`         | `1`    | 隨機出多少個不同的詞綴                                                                |
| `sockets` | 否   | `{ "min", "max" }`         | `0`    | 隨機出多少個寶石插槽                                                                  |
| `affixes` | 是   | 物件清單                   |        | 可隨機出的內容，每個項目如下。屬性未知的項目會被跳過                                  |

`affixes` 的每個項目接受：

| 鍵          | 必填 | 值                                                               | 預設   | 作用                                                                                  |
| ----------- | ---- | ---------------------------------------------------------------- | ------ | ------------------------------------------------------------------------------------- |
| `name`      | 否   | 文字，或語言鍵                                                   | 無     | 物品提示框中為它顯示的一行                                                            |
| `attribute` | 是   | 屬性名稱                                                         |        | 它改變的內容，例如 `generic.attackDamage` 或 `generic.maxHealth`                      |
| `min`       | 否   | 數字                                                             | `0`    | 它增加的最小值                                                                        |
| `max`       | 否   | 數字                                                             | `min`  | 它增加的最大值。兩值相同則得到精確加成，否則在兩者之間隨機                            |
| `operation` | 否   | `0`、`1` 或 `2`                                                  | `0`    | `0` 加到屬性上，`1` 乘以其基礎值，`2` 乘以總值                                        |
| `slot`      | 否   | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet` 或 `any`  | `any`  | 物品必須處於哪個欄位，加成才生效                                                      |
| `weight`    | 否   | int                                                              | `1`    | 與其他項目相比的機率。同一物品的詞綴互不相同，按權重抽取                              |

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

| 鍵          | 必填 | 值                                                               | 預設     | 作用                                                                                  |
| ----------- | ---- | ---------------------------------------------------------------- | -------- | ------------------------------------------------------------------------------------- |
| `item`      | 是   | 物品名稱                                           |          | 寶石，放在鐵砧的右格                                                                  |
| `name`      | 否   | 文字，或語言鍵                                                   | 該物品   | 寶石嵌入插槽後提示框所顯示的內容                                                      |
| `attribute` | 是   | 屬性名稱                                                         |          | 寶石改變的內容                                                                        |
| `amount`    | 否   | 數字                                                             | `0`      | 它增加的量                                                                            |
| `operation` | 否   | `0`、`1` 或 `2`                                                  | `0`      | 與詞綴相同                                                                            |
| `slot`      | 否   | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet` 或 `any`  | `any`    | 與詞綴相同                                                                            |
| `levels`    | 否   | int                                                              | `1`      | 嵌入寶石花費的經驗等級，最少為 1，且始終低於鐵砧的「過於昂貴」界線                    |

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

附魔台只提供 `#minecraft:in_enchanting_table` 標籤中的附魔，所以資源包新增的附魔也必須在該標籤中；`off` 可以在不動標籤的情況下排除一個。

### 每種物品

```json
{
  "match": "minecraft:shears",
  "enchantable": true,
  "enchantability": 10,
  "enchantments": ["minecraft:efficiency", "minecraft:unbreaking"]
}
```

| 鍵               | 必填 | 值                      | 預設             | 作用                                                                                         |
| ---------------- | ---- | ----------------------- | ---------------- | -------------------------------------------------------------------------------------------- |
| `match`          | 是   | 物品名稱、`#tag` 或 `*` |                  | 放在附魔台上的物品。物品名稱優先於標籤，標籤優先於 `*`；同類之間以最後載入的檔案為準         |
| `enchantable`    | 否   | 布林值                  | 原版             | `true` 讓附魔台接受原版拒絕的物品，`false` 則拒絕原版會接受的物品                            |
| `enchantability` | 否   | 整數，1 或以上          | 物品自身的值     | 它接受附魔的能力：越高，抽到的附魔越強越多。原版的範圍是 1 到 25；鑽石工具為 10，金工具為 22 |
| `enchantments`   | 否   | 附魔名稱清單            | 適合該物品的附魔 | 附魔台可以為它抽取的僅有附魔，不論它們原本是否適合該物品                                     |

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

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

方塊的戰利品表決定它掉落什麼，但提供戰利品表會整個接管原有的表，而且戰利品表無法給出經驗值。這裡的一條規則指定一個方塊，以及破壞它時在常規掉落物之外或取而代之的掉落內容，包括經驗值，同時不動到該方塊自己的戰利品表。

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

`<namespace>/block_drops/*.json`

```json
{
  "block": "minecraft:wheat",
  "properties": { "age": "7" },
  "drops": [
    { "item": "minecraft:gold_nugget", "chance": 0.1 }
  ]
}
```

| 鍵            | 必填 | 值               | 預設    | 作用                                                                                                    |
| ------------- | ---- | ---------------- | ------- | ------------------------------------------------------------------------------------------------------- |
| `block`       | 是   | 方塊 id          |         | 規則所監看的方塊                                                                                        |
| `properties`  | 否   | 屬性到值的物件   | 無      | 只匹配帶有這些值的狀態，例如原木上的 `{ "axis": "x" }`；不寫則匹配所有狀態。1.12.2 的 `meta` 不會被讀取 |
| `replace`     | 否   | 布林值           | `false` | 在擲出這些掉落物之前，是否先丟棄常規掉落物                                                              |
| `advancement` | 否   | `namespace:path` | 無      | 該規則只對擁有這項進度的玩家生效，因此同一個方塊可以在此之前掉落一種東西，在此之後掉落另一種            |
| `drops`       | 是   | 掉落物清單       |         | 方塊被破壞時，每一項各自單獨擲骰                                                                        |

每一項掉落物：

| 鍵           | 必填                    | 值                          | 預設     | 作用                                                                                                                                                                                                           |
| ------------ | ----------------------- | --------------------------- | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `item`       | 是，除非有 `experience` | 物品 id                     |          | 掉落什麼                                                                                                                                                                                                       |
| `experience` | 否                      | 數字或 `low-high`           |          | 取代物品，以經驗球的形式給出這麼多經驗值，在範圍內均勻擲出。`chance` 和 `silkTouch` 的適用方式與物品相同                                                                                                       |
| `count`      | 否                      | 數字或 `low-high`           | `1`      | 數量多少，在範圍內均勻擲出                                                                                                                                                                                     |
| `chance`     | 否                      | 小數                        | `1.0`    | 掉落發生的機率，`0.05` 表示每二十次破壞掉一次                                                                                                                                                                  |
| `fortune`    | 否                      | 整數                        | `0`      | 工具上每一級幸運，最多額外增加這麼多個                                                                                                                                                                         |
| `silkTouch`  | 否                      | `either`、`only` 或 `never` | `either` | 該掉落物是需要絲綢之觸的收穫、拒絕絲綢之觸，還是不在意。當玩家用絲綢之觸工具破壞一個依 1.12.2 的判斷方式可被絲綢之觸採集的方塊時，即視為絲綢之觸採集：沒有方塊實體的完整方塊，或玻璃片、鐵柵欄、蜘蛛網與終界箱 |

規則會看到每一次會掉落方塊戰利品的破壞，與 1.12.2 相同：玩家的破壞，以及爆炸、活塞、流動的水、生物，還有正在挖穿該方塊的資源包生物，這些情況會擲出每一條沒有 `advancement` 的規則。當爆炸削減方塊自己的掉落物時，這裡每個擲出的物品以相同的機率保留；經驗值不會被削減。同一個方塊的多條規則全部生效，其中任何一條的 `replace` 都會先清除常規掉落物。

## 右鍵收穫

*合成、戰利品與交易*

`<namespace>/harvests/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。

右鍵點擊成熟的作物通常沒有任何效果。這裡的檔案會讓所列作物在成熟時可由玩家右鍵收穫：作物掉落平常的戰利品，並立即在原地以年齡 0 重新種下，一次點擊就取代了破壞方塊再補種的兩個步驟。沒有檔案時一切照舊；想把選擇權交給玩家的資源包，可以在 `requires` 中加入 `config:` 項目，做法與「資源包選項」一節相同。

```json
{
  "requires": ["config:enableRightClickHarvest"],
  "crops": ["minecraft:wheat", "minecraft:carrots", "minecraft:potatoes", "minecraft:beetroots", "minecraft:nether_wart"],
  "replant": true
}
```

`<namespace>/harvests/*.json`

```json
{
  "crops": ["minecraft:beetroots", "minecraft:nether_wart"],
  "age": 3,
  "replant": true
}
```

| 鍵         | 必填 | 值           | 預設                   | 作用                                                                                 |
| ---------- | ---- | ------------ | ---------------------- | ------------------------------------------------------------------------------------ |
| `crops`    | 是   | 方塊 ID 清單 |                        | 右鍵即可收穫的作物方塊，原版或資源包的皆可。沒有任何東西註冊的方塊會被略過並寫入日誌 |
| `age`      | 否   | int          | 該方塊允許的最大 `age` | 檔案內所有作物視為成熟的年齡                                                         |
| `replant`  | 否   | boolean      | `true`                 | 作物是原地回到年齡 0，還是掉落後被移除                                               |
| `requires` | 否   | 字串清單     | 無                     | 必須存在的模組 ID、資源包命名空間或 `config:` 選項，與所有定義相同                   |

任何帶有整數屬性 `age` 的方塊都可以列入。未成熟的作物、潛行的玩家和旁觀者仍依一般右鍵處理。掉落與破壞該作物時相同，會計入手持工具的幸運以及「方塊掉落」一節的所有規則；重新種下時，若掉落物中有一顆該作物的種子，就會從中扣除一顆。村民的 `harvestFarmland` 任務是獨立的，不受影響。

## 玩家戰利品

*合成、戰利品與交易*

`<namespace>/player_loot/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

遊戲沒有替玩家提供專屬的戰利品表：死亡時只掉落物品欄，也沒有資源包可以覆寫的表名。RDPL 新增了一張，在玩家死亡時擲出：

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

`add` 會把表中的物品與物品欄一起掉落，適合擊殺懸賞。`replace` 會丟棄物品欄，只掉落表中擲出的內容。

關閉 `rollOnKeepInventory` 時，`keepInventory` 下的死亡（以及一律保留物品欄的旁觀者死亡）不會擲出任何東西。開啟它則能讓保留物品欄的世界中，死亡依然有代價。

多個檔案會疊加，各自獨立判定。只要有任何一個適用的項目是 `replace`，物品欄就會在擲出之前被清空一次，因此與之並存的 `add` 項目仍然會生效。

這張表就是一張依名稱查找的普通戰利品表：它可以放在資源包的 `loot_tables/entities/player.json`，可以是任何原版或模組的表，也可以由 `loot_injections` 觸及。戰利品上下文：死亡的玩家是被掠奪的實體，擊殺者（如果有）是擊殺的玩家，傷害來源也已設定，所以 `killed_by_player`、`entity_properties`、`random_chance_with_looting` 等等都能正常運作。

有一個戰利品函式是 RDPL 自有的，可用於任何帶有被掠奪實體的表：`rdpl:killed_name` 會以受害者的名稱為掉落物命名。`format` 決定顯示名稱的形式（`%s` 代表受害者，預設只顯示名稱），而 `tag` 則會把純名稱寫入一個 NBT 字串鍵，供自行讀取它的物品使用。

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**墓碑模組。** 擲出的物品會在任何墓碑模組讀取之前加入一般的死亡掉落物，所以它們會與其他物品一起進入墓碑（`replace` 會讓表的內容而不是物品欄進入墓碑）。無需任何設定。

`dropLoose` 完全繞過掉落物清單：物品會被直接放進世界，所以墓碑模組永遠看不到它們；物品欄進入墓碑，表中的物品則散落在地上留給擊殺者。適用於屬於擊殺者而不是受害者墓碑的戰利品。沒有墓碑模組時，它幾乎沒有差別。注意：這些物品在下游任何環節取消掉落之前就已經存在，所以凡是在死亡被取消後不應保留的項目，都應當關閉它。

在 `data` 設定類別中將 `playerLoot` 設為 `false`，即可完全關閉該資料夾。

## 村民與交易

*合成、戰利品與交易*

`<namespace>/villagers/*.json`

檔案的路徑就是職業的註冊名稱，因此 `mypack/villagers/jeweller.json` 會註冊 `mypack:jeweller`，隨後交易會在 `profession` 中引用它。

```json
{
  "jobSite": "mypack:gem_bench",
  "workSound": "minecraft:entity.villager.work_toolsmith"
}
```

| 鍵          | 必填 | 值       | 預設 | 作用                                                                                                                                                                                                 |
| ----------- | ---- | -------- | ---- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `jobSite`   | 否   | 方塊名稱 | 無   | 村民認領以擔任該職業的方塊，就像鍛造台讓村民成為工具匠那樣。不寫時，沒有任何方塊會分配這個職業：與 1.12.2 一樣，生成或繁殖出的村民會被隨機分配到它，一直保持，而且沒有可工作的方塊，因此永遠不會補貨 |
| `workSound` | 否   | 音效名稱 | 無   | 它在該方塊處工作時播放的聲音                                                                                                                                                                         |

職業分支（career）是 1.12.2 的概念，遊戲已不再有：一個職業就是一套交易，所以原先有兩個職業分支的資源包要提供兩個村民檔案。村民的外觀是普通材質，放在 `assets/<namespace>/textures/entity/villager/profession/<name>.png` 和 `textures/entity/zombie_villager/profession/<name>.png`，與遊戲存放自己材質的位置完全相同。一筆交易如果同時寫了原版 1.12.2 的職業和它的 `career`，例如 `minecraft:smith` 配 `armor`，就會歸入該職業分支後來變成的職業，這裡是 `minecraft:armorer`。1.12.2 檔案中的 `texture` 和 `zombieTexture`，即資源包自帶的整張外觀，在資源包於這兩個路徑上沒有內容時會被複製過去，外觀會繪製在村民自己的外觀之上。

### 交易

*村民與交易*

`<namespace>/trades/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

```json
{
  "trades": [
    {
      "profession": "mypack:jeweller",
      "level": 1,
      "maxUses": 12,
      "xp": 2,
      "buy": { "item": "minecraft:emerald", "min": 2, "max": 4 },
      "buySecondary": { "item": "minecraft:paper", "min": 1 },
      "sell": { "item": "mypack:ruby", "min": 1 }
    }
  ]
}
```

| 鍵             | 必填 | 值       | 預設 | 作用                                                                             |
| -------------- | ---- | -------- | ---- | -------------------------------------------------------------------------------- |
| `profession`   | 是   | 職業名稱 |      | 這是誰的交易。原版 1.12.2 名稱連同它的 `career` 也會被讀取，見上文               |
| `level`        | 否   | 整數     | `1`  | 它出現在哪個交易等級，1 到 5。更高的等級併入 5 級，即村民所能達到的最高級        |
| `buy`          | 是   | 物品堆疊 | 無   | 村民收取的物品，按物品堆疊的寫法書寫（見下文）。沒有收購或出售物品的交易會被略過 |
| `buySecondary` | 否   | 物品堆疊 | 無   | 村民在 `buy` 之外收取的第二件物品，寫法相同                                      |
| `sell`         | 是   | 物品堆疊 | 無   | 村民給出的物品，按物品堆疊的寫法書寫（見下文）                                   |
| `maxUses`      | 否   | 整數     | `12` | 鎖定之前可以使用的次數                                                           |
| `xp`           | 否   | 整數     | `2`  | 每次交易村民獲得的、用於升級的經驗值                                             |

物品堆疊由 `item` 加上 `min`（`1`）和 `max`（預設為 `min`）構成，因此固定價格只需寫 `min`。

交易也可以取決於開啟視窗的玩家。不符合條件的玩家看不到該交易，其他玩家不受影響。多個條件必須同時成立，而且每次開啟視窗時都會檢查。

```json
{
  "trades": [
    {
      "profession": "minecraft:farmer",
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

| 鍵                          | 必填 | 值               | 預設值 | 作用                                                                                                                                                                                       |
| --------------------------- | ---- | ---------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `advancement`               | 否   | `namespace:path` | 無     | 只有獲得此進度的玩家能看到該交易                                                                                                                                                           |
| `team`                      | 否   | 隊伍名稱         | 無     | 只有此計分板隊伍的成員能看到該交易                                                                                                                                                         |
| `objective`, `scoreAtLeast` | 否   | 文字，整數       | 無     | 只有在該目標中的分數不低於此值的玩家才能看到該交易。兩者都需要，沒有分數的玩家不符合條件                                                                                                   |
| `priceScale`                | 否   | 物件             | 無     | 將 `buy` 的數量乘以 `clamp(1 + score * perPoint, min, max)` 並四捨五入，最小為 1，最大為堆疊上限。`objective` 和 `perPoint` 必填，`min` 預設為 `0.0`，`max` 預設為 `10.0`，沒有分數的玩家按 0 計算 |

`priceScale` 只改變 `buy`，絕不改變 `buySecondary`，縮放後的價格仍會疊加遊戲本身的需求和聲望調整。遊戲透過職業和物品（`buy`、`buySecondary` 和 `sell`）把條件交易與村民的其他交易區分開，所以請為它指定該職業其他交易都沒有用過的物品。兩個資料包交易使用相同的物品時，後一個會被跳過並寫入日誌。已經生成交易的村民會保留原有交易。

---

# 生物與危險

## 實體變體

*生物與危險*

`<namespace>/entities/*.json`

檔案的路徑就是變體的註冊名稱，因此 `mypack/entities/angry_cow.json` 會註冊 `mypack:angry_cow`，`becomes`、生怪蛋和世界存檔所引用的都是它。

這裡的檔案會以一個已存在的實體為基礎，製作出一個新實體。它是真正獨立的實體，有自己的註冊名稱、在世界中的名稱和生怪蛋，若你有提供，也會有自己的戰利品表；它沿用另一個實體的行為，而不是取代它。被複製的實體不會有任何改變。

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

```json
{
  "entity": "minecraft:cow",
  "name": "Angry Cow",
  "showName": false,
  "texture": "mypack:textures/entity/angry_cow.png",
  "lootTable": "mypack:entities/angry_cow",
  "profession": "mypack:jeweller",
  "baby": 0.05,
  "keepsBaseBaby": false,
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
  "stepHeight": 0.6,
  "climbs": false,
  "teleports": true,
  "walks": false,
  "breathesUnderwater": false,
  "swims": false,
  "amphibious": false,
  "waterSlowdown": 0.8,
  "absorption": 0,
  "hurtResistance": 20,
  "ignoresEffects": ["minecraft:poison"],
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
  "bright": false,
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
  "attackReach": 3.0,
  "knockback": 0.4,
  "hitEffects": true,
  "hitFire": true,
  "threatLeast": 1,
  "threatHostile": 2,
  "persistent": false,
  "silent": false,
  "picksUpLoot": false,
  "collectsExperience": false,
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
  "digs": false,
  "throwReturns": false,
  "charges": false,
  "pounces": false,
  "sniffs": 0,
  "fleesWhenHurt": 0.0,
  "sleepsByDay": false,
  "home": 0,
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
  "biomeTypes": ["plains"],
  "trackingRange": 80,
  "trackVelocity": true,
  "trackingFrequency": 3,
  "tasks": [ "-wander", { "task": "watchClosest", "priority": 6, "entity": "minecraft:player", "distance": 8 } ],
  "requires": ["mypack"]
}
```

### 識別

*實體變體*

| 鍵                | 必填 | 值                           | 預設             | 作用                                                                                                                                   |
| ----------------- | ---- | ---------------------------- | ---------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| `entity`          | 是   | `namespace:name`             | 無               | 作為基礎的實體。任何模組的都可以，只要它使用普通的世界建構子                                                                           |
| `name`            | 否   | 字串                         | 無               | 它在世界中、死亡訊息中以及生怪蛋上顯示的名稱                                                                                           |
| `showName`        | 否   | 布林值                       | `false`          | 不必注視它也顯示名稱                                                                                                                   |
| `profession`      | 否   | `namespace:name`             | 隨機             | 對村民而言，它從事的職業                                                                                                               |
| `dialog`          | 否   | 字串                         | 無               | 以 `namespace:name` 指定對話，對該實體按右鍵時開啟。[對話](#對話)                                                                      |
| `baby`            | 否   | 布林值或 0.0 到 1.0          | `false`          | 生成幼體的頻率，且會一直保持幼體。`true` 表示總是，數字則表示其中的比例                                                                |
| `becomes`         | 否   | 清單                         | 無               | 它在生成時可能變成的其他變體，依權重決定。見下文                                                                                       |
| `egg`             | 否   | 布林值或物件                 | `true`           | 生怪蛋，顏色與它所複製的實體的生怪蛋相同。`{ "primary": "AABBCC", "secondary": "112233" }` 可指定自己的顏色，`false` 則不提供生怪蛋    |
| `egg.primary`     | 否   | 字串                         | 被複製實體的顏色 | 蛋底色的十六進位色碼，如 `AABBCC`。不寫時，蛋沿用該變體所複製實體的底色                                                                |
| `egg.secondary`   | 否   | 字串                         | 被複製實體的顏色 | 蛋斑點的十六進位色碼，如 `112233`。不寫時，蛋沿用該變體所複製實體的斑點色                                                              |
| `becomes.variant` | 是   | 字串                         | 無               | 在寫成物件的 `becomes` 項目中，要變成的變體，寫其登錄名稱。項目也可以只寫這個名稱。沒有寫變體的項目會被略過                            |
| `keepsBaseBaby`   | 否   | 布林值                       | `false`          | 基礎實體自己的幼體判定是否也照常進行。不寫時，基於殭屍的變體只依 `baby` 所述生成幼體，不會出現殭屍自身判定產生的小孩，也不會出現雞騎士 |
| `requires`        | 否   | 模組 id 或資源包命名空間清單 | 無               | 除非全部存在，否則該變體會被省略                                                                                                       |

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

項目可以只寫變體名稱，權重為 `1`；也可以是帶有 `variant`（必填）和 `weight`（`1`，最小為 `1`）的物件。寫上它自己的名字，就是讓它保持原樣的方式，權重就是機率。把這段寫在 `mypack:walker` 上，一顆蛋和一條生成項目就會給出大多數的 walker，偶爾夾雜一個小的，就像殭屍蛋偶爾會給你一隻幼年殭屍。它發生在生物進入世界的時候，所以對蛋、`/summon` 和自然生成都成立，而到來的生物是所選變體的真實個體，具備該變體所寫的一切。生怪磚則更嚴格：它只在與所設變體同一基礎生物、同一隊伍的變體中擲骰，所以殭屍生怪磚給出的是該隊伍的殭屍及其幼體，永遠不會出現其他種類或其他隊伍的生物，就像原版的殭屍生怪磚始終是殭屍生怪磚。透過這種方式得到的變體不會再次轉變，所以兩個變體可以互相指名而不會陷入空轉。

**`baby` 的用武之地。** 遊戲本身沒有殭屍寶寶這種獨立的生物：只有一種殭屍，在生成時判定自己是否為幼體。`baby` 決定頻率，因此 `"baby": 0.05` 是原版的習慣，`"baby": true` 則是一律幼體。變體不會在此之上再套用殭屍自身的判定，所以不會出現 `baby` 沒有要求的幼體或小雞騎士；`keepsBaseBaby` 會把這項判定還回來。兩者其實是達成同一目的的兩種方式，該選哪一個取決於你想要的差異：單用 `baby` 會得到一個有時是幼體的變體，`becomes` 則會得到幾個在你喜歡的任何方面不同的變體，兩者混用也無妨。

### 外觀

*實體變體*

| 鍵           | 必填 | 值                                     | 預設       | 作用                                                                         |
| ------------ | ---- | -------------------------------------- | ---------- | ---------------------------------------------------------------------------- |
| `texture`    | 否   | `namespace:textures/entity/<file>.png` | 無         | 專屬的外觀貼圖，配置方式與被複製的實體相同                                   |
| `leftHanded` | 否   | 布林值                                 | `false`    | 用另一隻手持武器                                                             |
| `glowing`    | 否   | 布林值                                 | `false`    | 隔著牆也能看到輪廓                                                           |
| `invisible`  | 否   | 布林值                                 | `false`    | 不繪製本體，但其裝備仍會繪製                                                 |
| `scale`      | 否   | 浮點數                                 | `1.0`      | 繪製的大小，以及碰撞箱的大小                                                 |
| `angryScale` | 否   | 浮點數                                 | `scale`    | 有攻擊目標時它膨脹到的大小，以及失去目標後三秒內的大小                       |
| `width`      | 否   | 浮點數                                 | 基礎實體的 | 碰撞箱的寬度，在套用 `scale` 之前                                            |
| `height`     | 否   | 浮點數                                 | 基礎實體的 | 碰撞箱的高度，在套用 `scale` 之前                                            |
| `bright`     | 否   | 布林值                                 | `false`    | 無論站在何處都以最大亮度繪製，如同正午陽光下，因此不會被夜晚、陰影或洞穴壓暗 |
| `hideArmor`  | 否   | 布林值                                 | `false`    | 穿著盔甲，但不繪製出來                                                       |
| `hideHeld`   | 否   | 布林值                                 | `false`    | 對手中所持之物同樣如此                                                       |
| `tint`       | 否   | 十六進位顏色                           | 無         | 在繪製時為實體著色                                                           |
| `tintParts`  | 否   | `body`、`armor`、`held` 的清單         | `["body"]` | 著色涉及哪些部位                                                             |

`scale` 會同時改變兩端的模型和碰撞箱，因此所見即所擊。自行改變大小的生物，例如長大中的動物或殭屍寶寶，會以它選定的大小為基準進行縮放，所以兩者不會衝突。`angryScale` 在它有目標時使其膨脹，失去目標時恢復到 `scale`。由於用戶端從不被告知生物正在追獵什麼，疾跑旗標便負責傳遞這項資訊，它只會設定在使用 `angryScale` 的變體上，其他地方都不會設定，所以讀取你的變體疾跑狀態的模組會看到它發生變化。在低矮的天花板下長大是可能的，與史萊姆長大的情形相同，因此差值應保持適度。

`texture` 會被綁定，取代實體原本使用的那張材質，無論它繼承的是哪種算繪器，因此對模組實體和原版實體都有效。它必須與繪製所用的模型相符，因為模型是基礎實體的，這只是一張外觀貼圖，而不是新的形狀。各圖層保有它們自己的材質，所以換過外觀的殭屍身上，盔甲看起來仍像盔甲。

盔甲只會繪製在渲染器帶有盔甲層的實體上，也就是類人生物和村民。牛或蜘蛛的變體可以攜帶盔甲並獲得其防護，但不會有東西把它繪製出來，所以對這類生物，用 `attributes` 下的 `armor` 通常是讓它變堅韌更整潔的辦法。`hideArmor` 針對另一種情況：類人生物想讓盔甲留在裝備欄裡，為了防護或為了某個讀取它們的模組，卻不想讓它被看見。

### 它的音效

*實體變體*

| 鍵            | 必填 | 值     | 預設       | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ------------- | ---- | ------ | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `sounds`      | 否   | 物件   | 基礎實體的 | `ambient`、`hurt` 和 `death`，各為一個已登錄的音效事件，1.12.2 的名稱仍可讀取，參見[音效名稱](#值清單)。另有三個是基礎實體沒有對應聲音的：`target` 在它每次取得目標時播放一次，`explode` 是它爆炸時的聲音，無論是用 `explodes` 自爆還是用 `throws` 投擲 TNT。`throw` 在它用 `throws` 投擲任何東西時播放，取代雪球投擲聲，或 TNT 的引信嘶嘶聲。`targetVaries` 讓每次 `target` 播放的音高在該半音數範圍內隨機上下偏移，所以 `3` 會向任一方向游移四分之一個八度；`0` 則原樣播放。爆炸聲會取代遊戲自己的爆炸聲 |
| `soundVolume` | 否   | 數字   | `1.0`      | 這些聲音有多響                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `soundPitch`  | 否   | 數字   | `1.0`      | 播放的音調有多高。小於 1 更低沉，大於 1 更尖細                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `silent`      | 否   | 布林值 | `false`    | 不發出任何聲音                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

### 生命值、傷害與效果

*實體變體*

| 鍵                  | 必填 | 值                                              | 預設        | 作用                                                                                                                                                                                                                            |
| ------------------- | ---- | ----------------------------------------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `immuneTo`          | 否   | 傷害類型清單                                    | 無          | 它不受影響的傷害，用 1.12.2 使用的名稱，如 `fall`、`drown`、`explosion`、`explosion.player`、`magic`、`indirectMagic`、`mob`、`player`、`inWall` 等，或用傷害類型 id。參見[傷害類型](#值清單)                                   |
| `fallDamage`        | 否   | 小數                                            | `1.0`       | 墜落造成傷害的倍率。`0` 表示免除墜落傷害                                                                                                                                                                                        |
| `absorption`        | 否   | 小數                                            | `0`         | 在生命值之上額外增加的心數                                                                                                                                                                                                      |
| `creatureAttribute` | 否   | `undefined`、`undead`、`arthropod` 或 `illager` | 基礎實體的  | 它被視為哪一類，所以不死剋星和治療藥水會據此對待它                                                                                                                                                                              |
| `effects`           | 否   | 物件清單                                        | 無          | 它始終擁有的效果：`{ "potion": "minecraft:strength", "amplifier": 1 }`                                                                                                                                                          |
| `fireproof`         | 否   | 布林值                                          | `false`     | 完全不會著火，所以永遠不會被火或熔岩傷害，白天也不會燃燒                                                                                                                                                                        |
| `invulnerable`      | 否   | 布林值                                          | `false`     | 除了虛空和創造模式之外，不受任何傷害                                                                                                                                                                                            |
| `attributes`        | 否   | 物件                                            | 無          | `maxHealth`、`movementSpeed`、`attackDamage`、`attackSpeed`、`knockbackResistance`、`followRange`、`armor`。實體通常沒有的屬性會被賦予它。對近戰生物而言，`attackSpeed` 是每秒攻擊次數，遊戲中為 `1`，所以 `2` 表示攻擊頻率加倍 |
| `hurtResistance`    | 否   | 整數，單位為刻                                  | 遊戲的 `20` | 受到一次攻擊後，多久之內不會再次受傷。比它的一半更快的攻擊會被忽略，所以攻擊速度快的攻擊者需要一個數值更低的目標                                                                                                                |
| `ignoresEffects`    | 否   | 效果名稱清單                                    | 無          | 永遠不會作用在它身上的效果，無論是誰或什麼施加的：一次攻擊、一瓶噴濺藥水、一座烽火台、一支箭、`/effect`。`all` 會拒絕所有效果，所以變體從一張白紙開始。它自己的 `effects` 仍會加在它身上                                        |

### 移動

*實體變體*

| 鍵               | 必填 | 值             | 預設       | 作用                                                                                                                                                                                                                                                           |
| ---------------- | ---- | -------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `jumpMultiplier` | 否   | 小數           | `1.0`      | 乘以它跳躍的速度。高度的增長比數值更快，所以 `2.0` 跳得約是所複製實體的三倍半高，`0.5` 約為三分之一高                                                                                                                                                          |
| `maxFallHeight`  | 否   | 整數           | 基礎實體的 | 尋路時它願意下落多高                                                                                                                                                                                                                                           |
| `noAI`           | 否   | 布林值         | `false`    | 站在被放置的地方，什麼也不做                                                                                                                                                                                                                                   |
| `leashable`      | 否   | 布林值         | `false`    | 可以被拴繩牽著走，即使它所複製的實體從來不能                                                                                                                                                                                                                   |
| `steerable`      | 否   | 布林值         | `false`    | 被騎乘時可以操控                                                                                                                                                                                                                                               |
| `pathPriorities` | 否   | 物件           | 無         | 它願意走過什麼，如 `WATER`、`LAVA`、`DANGER_FIRE`、`DOOR_WOOD_CLOSED` 以及遊戲路徑類型的其餘各項，每項是一個數字，負數表示絕不通行。1.12.2 的 `DANGER_CACTUS` 和 `DAMAGE_CACTUS` 會被讀作 `DANGER_OTHER` 和 `DAMAGE_OTHER`，此版本把仙人掌與甜莓叢歸在這兩項下 |
| `stepHeight`     | 否   | 小數，單位為格 | 基礎實體的 | 它不用跳躍就能走上多高的台階                                                                                                                                                                                                                                   |
| `climbs`         | 否   | 布林值         | 基礎實體的 | 開啟時，它會爬上走到的任何牆壁，像蜘蛛一樣，無論基礎是什麼。關閉時，它什麼都不爬，連梯子也不爬，蜘蛛則會留在地面上                                                                                                                                             |
| `teleports`      | 否   | 布林值         | `true`     | 終界使者或界伏蚌是否可以傳送。關閉時，它留在原地，白天和在水中也是如此                                                                                                                                                                                         |
| `walks`          | 否   | 布林值         | `false`    | 兔子像其他動物那樣行走，而不是蹦跳著移動。只有兔子會讀取它                                                                                                                                                                                                     |

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

| 鍵              | 必填 | 值             | 預設              | 作用                                                                                                                                                                                                                                                   |
| --------------- | ---- | -------------- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `hostile`       | 否   | 布林值         | `false`           | 攻擊搆得到的東西，受傷時會還擊。無論基礎是什麼，敵對變體在遊戲中都算作怪物，所以受怪物上限約束。和平難度只在其基礎是怪物時才會清除它；其他基礎會保留，但在和平難度下無法傷害玩家。它會丟掉基礎自帶的動物任務：繁殖、被引誘、跟隨父母、主人或同類、坐下 |
| `targets`       | 否   | 實體名稱清單   | 玩家              | 敵對時它去尋找什麼。`minecraft:player` 可被識別，儘管玩家並不是已登錄的實體                                                                                                                                                                            |
| `attackReach`   | 否   | 小數，單位為格 | 它的體型          | 近戰攻擊能打多遠。遊戲按寬度的兩倍計算，所以放大的生物能從更遠處打中；這裡則直接設定該值                                                                                                                                                               |
| `knockback`     | 否   | 小數           | 基礎實體的，`0.4` | 它的攻擊推得多用力。`0` 表示完全不推                                                                                                                                                                                                                   |
| `hitEffects`    | 否   | 布林值         | `true`            | 它是否會像所複製的實體那樣對被擊中的對象施加效果：凋零骷髏的凋零、洞穴蜘蛛的劇毒、屍殼的飢餓。關閉時，它只造成傷害                                                                                                                                     |
| `hitFire`       | 否   | 布林值         | `true`            | 當所複製的實體會這樣做時，它是否會點燃被擊中的對象：燃燒的殭屍、烈焰使者的火球。關閉時，它的任何行為都不會在目標身上引起火焰                                                                                                                           |
| `passive`       | 否   | 布林值         | `false`           | 無論它平時的行為如何，都不再攻擊任何東西                                                                                                                                                                                                               |
| `threatLeast`   | 否   | 整數           | `0`               | 128 格內的玩家或其他攜帶者必須處於的最低威脅檔位，變體才會自然生成。`0` 表示照常生成                                                                                                                                                                   |
| `threatHostile` | 否   | 整數           | `0`               | 玩家必須處於的最低威脅檔位，變體才會主動攻擊他們。低於該檔位時，變體對該玩家是溫馴的，但被打時仍會還擊。`0` 表示照常攻擊                                                                                                                               |

`hostile` 還會去掉讓生物逃跑的行為：原本會躲避玩家或受傷時驚慌的動物，一旦敵對就都不會了，否則它會逃離自己本應攻擊的對象。它需要一個在地面行走的實體，因為它使用的是原版賦予自家怪物的同一套攻擊行為。飛行或游泳的基礎實體會被記錄到日誌並保持原樣。`passive` 的適用範圍更廣，但只能觸及按原版方式建構的行為，敵意寫在自身刻更新或傷害程式碼中的模組，不是資源包能勸退的。

### 裝備、掉落物與經驗值

*實體變體*

| 鍵                   | 必填 | 值                          | 預設       | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| -------------------- | ---- | --------------------------- | ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `lootTable`          | 否   | `namespace:entities/<name>` | 基礎實體的 | 它掉落什麼。不寫時，它掉落所複製的實體掉落的東西                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `experience`         | 否   | 整數                        | 基礎實體的 | 它掉落多少經驗值                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `collectsExperience` | 否   | 布林值                      | `false`    | 像玩家一樣收集經驗值：八格內的經驗球會飄向它並在接觸時被吸收，其裝備上的修補會被優先修復，點數依玩家自己的曲線累積成等級，並透過存檔保存在生物身上。它所擊殺的對象會像玩家擊殺一樣掉落經驗值，它的 `digs` 任務所破壞的方塊會掉落該方塊自己的經驗值，`block_drops` 的經驗值擲骰對它也同樣生效。它死亡時每級掉落七點，最多一百點，除非開啟了 `keepInventory`。帶有 `xp` 或 `level` 條件的計分項會把它的總經驗值與等級記在以其 UUID 命名的一列上，所以函數可以用 `execute if score` 或 `scores={<objective>=N..}` 選擇器讀取它們。它像玩家一樣把等級花在鐵砧操作上，參見[鐵砧操作](#鐵砧操作) |
| `dropChance`         | 否   | 0 到 1                      | `0`        | 每件裝備掉落的可能性                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `picksUpLoot`        | 否   | 布林值                      | `false`    | 撿起它走過的東西                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `equipment`          | 否   | 物件                        | 無         | `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet`，各為一個物品名稱                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |

變體會掉落被複製實體所掉落的東西，因為戰利品表是固定在該實體自己的程式碼中，而不是依名稱查找的。`lootTable` 會把它指向你自己的表，然後你像其他表一樣，把它提供在 `loot_tables/entities/<name>.json`。

### 特殊行為

*實體變體*

| 鍵               | 必填 | 值             | 預設               | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ---------------- | ---- | -------------- | ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `digs`           | 否   | 布林值         | `false`            | 用手中的工具挖穿它與目標之間的一切：鏟子挖泥土、沙子和礫石，鎬挖石頭，斧砍木頭，並且只能挖開該工具材質能破壞的方塊，因此木鎬永遠挖不開鐵礦石，不到鑽石級別就挖不開黑曜石。一個方塊所需的時間與玩家用該工具相同，掉落相同的東西，並會磨損工具。用 `equipment` 給它工具；空手則什麼也挖不了，而在 `mobGriefing` 關閉的地方它也什麼都不挖。它從不尋找繞路：有目標時徑直走向目標，挖掉擋路的一切，工具無法打開方塊時就站著推。需要 `hostile`。它無須看見目標就能選定目標，因為它朝向挖掘的東西本來就在某物之後 |
| `throws`         | 否   | 布林值         | `false`            | 從遠處把手中所持之物投向目標，若是 TNT，則點燃它並後撤。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `throwAmmo`      | 否   | 整數           | 無                 | 它有多少可以投擲。不寫則永遠不會用完                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `throwReload`    | 否   | 整數，單位為秒 | `explosionFuse` 刻 | 手保持空著多久才再拿出一個，單位為秒。未設定時，等待的刻數與 `explosionFuse` 相同（除非另行設定，否則為 30 刻，即 1.5 秒）                                                                                                                                                                                                                                                                                                                                                                                 |
| `throwRetreat`   | 否   | 整數，單位為秒 | `explosionFuse` 刻 | 投擲之後躲開多久才轉身回來，單位為秒。未設定時，躲開的刻數與 `explosionFuse` 相同（除非另行設定，否則為 30 刻，即 1.5 秒）                                                                                                                                                                                                                                                                                                                                                                                 |
| `throwPower`     | 否   | 浮點數         | `1.0`              | 投擲的力度。加倍大致會使射程加倍                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `throwArc`       | 否   | 浮點數         | `0.35`             | 拋得有多高。越高滯空越久，接近零是平直投擲，低於零則向下投擲                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `throwReturns`   | 否   | 布林值         | `false`            | 投出之物像三叉戟一樣飛行：命中時造成變體的 `attackDamage` 點傷害，基礎實體沒有則為 8，然後像忠誠附魔召回三叉戟那樣飛回它手中。它永遠不會用完，瞄準目標的方式與骷髏相同，`throwPower` 越高越快，難度越高散布越小，並且投擲者在物體飛行期間原地不動，因此 `throwAmmo`、`throwReload`、`throwRetreat` 和 `throwArc` 對它不適用。TNT 仍照常投擲                                                                                                                                                                |
| `explodes`       | 否   | 布林值         | `false`            | 像苦力怕一樣在目標身旁自爆。需要 `hostile`                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `explosionPower` | 否   | 數字           | `3.0`              | 爆炸有多大。苦力怕是 3，TNT 是 4。在苦力怕或地獄幽靈基礎上，寫下這個鍵或 `explosionFuse` 還會在沒有 `explodes` 的情況下設定基礎自己的爆炸：苦力怕的爆炸大小與引信、地獄幽靈的火球，均取整數，所以只給了 `explosionFuse` 的地獄幽靈會以 3 而不是它自己的 1 爆炸                                                                                                                                                                                                                                             |
| `explosionFuse`  | 否   | 整數，單位為刻 | `30`               | 它在引爆前嘶嘶作響多久，同時也是苦力怕基礎自己的引信                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
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

| 鍵      | 必填 | 值   | 預設 | 作用                                                                                           |
| ------- | ---- | ---- | ---- | ---------------------------------------------------------------------------------------------- |
| `tasks` | 否   | 清單 | 無   | 遊戲擁有的任何任務，依名稱以你選擇的優先順序加入變體，或從其基礎實體自帶的任務中去除。清單見下 |

**遊戲擁有的任何任務。** 上面的鍵是 RDPL 自己的行為。`tasks` 則能觸及原版自身使用的每一種任務，適用於任何基礎實體：一個項目是一個物件，寫明 `task` 及其 `priority`，外加該任務所讀取的內容；以 `-` 開頭的名稱會去掉基礎實體自帶的該種任務。優先順序從 0 起先執行，原版自己的優先順序在 1 到 8 之間，因此 0 的任務勝過基礎實體的一切行為，而 9 的任務只在沒有其他事可做時才會執行。

```json
{
  "entity": "minecraft:cow",
  "tasks": [
    "-wander",
    { "task": "avoidEntity", "priority": 3, "entity": "minecraft:player", "distance": 8, "speed": 1.0, "nearSpeed": 1.4 },
    { "task": "watchClosest", "priority": 6, "entity": "minecraft:wolf", "distance": 12 },
    { "task": "wanderAvoidWater", "priority": 7, "speed": 0.8 },
    { "task": "wander", "priority": 8, "speed": 1.0, "chance": 120 },
    { "task": "follow", "priority": 5, "speed": 1.0, "near": 3, "distance": 10 },
    { "task": "leapAtTarget", "priority": 1, "leap": 0.5 },
    { "task": "tempt", "priority": 4, "items": ["minecraft:wheat"], "speed": 1.2, "scared": false },
    { "task": "attackMelee", "priority": 2, "speed": 1.2, "memory": true },
    { "task": "openDoor", "priority": 6, "close": true },
    { "task": "moveThroughVillage", "priority": 5, "speed": 1.0, "nocturnal": true },
    { "task": "nearestAttackableTarget", "priority": 2, "entity": "minecraft:zombie", "sight": true, "nearby": false },
    { "task": "hurtByTarget", "priority": 1, "help": true }
  ]
}
```

該清單在 `hostile`、`passive` 和上述各行為生效之後才套用，因此它擁有最終決定權。移動身體的任務會相互排斥：只有當優先順序更靠前的任務都沒有在移動生物時，它才會執行；而怪物自帶的攻擊位於優先順序 2，所以殭屍上的撲躍或逃跑需要優先順序 1，否則永遠輪不到它；蜘蛛和狼出於同樣的原因，把撲躍排在攻擊之前。基礎實體已經在執行的任務會被再新增一次，而不是被取代；請先移除舊的。有些任務只有在基礎實體具備其所驅動的東西時才有意義：弓箭戰鬥需要會射擊的基礎實體，坐下需要可馴服的基礎實體，交易需要村民。在無法承載該任務的基礎實體上要求它時，日誌會說明它需要哪種基礎實體，而變體會在沒有它的情況下運作。此版本中的村民靠遊戲的大腦而不是任務運作，所以下面的村民列只涉及大腦留給任務的部分。

```json
{
  "entity": "minecraft:witch",
  "tasks": [
    { "task": "attackRanged", "priority": 2, "speed": 1.0, "cooldown": 30, "distance": 12 }
  ]
}
```

| 鍵          | 類型           | 預設           | 作用                                                                                                                                                                                          |
| ----------- | -------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `priority`  | 整數           | 必填           | 它在基礎實體各任務中的位置。越小越先執行                                                                                                                                                      |
| `speed`     | 數字           | 該任務的一般值 | 任務執行時它移動的速度，作為行走速度的乘數                                                                                                                                                    |
| `nearSpeed` | 數字           | `1.2`          | `avoidEntity`：它所躲避之物靠近時的乘數                                                                                                                                                       |
| `distance`  | 數字，單位為格 | 該任務的一般值 | 它能看多遠、跟隨多遠、射擊多遠或保持多遠                                                                                                                                                      |
| `near`      | 數字，單位為格 | 該任務的一般值 | `follow`、`followOwner`、`followOwnerFlying`：它走近到多近才停下                                                                                                                              |
| `chance`    | 數字           | 該任務的一般值 | `wander`：每隔這麼多刻擲一次；`wanderAvoidWater`：陸地上的一步遊蕩無視水域規則、可能選中水中位置的機率，0 到 1（未設定時為 `0.001`）；`watchClosest`、`watchClosest2`：每刻注視的機率，0 到 1 |
| `leap`      | 數字           | `0.4`          | `leapAtTarget`：跳得多高                                                                                                                                                                      |
| `cooldown`  | 整數，單位為刻 | `20`           | `attackRanged`、`attackRangedBow`：兩次射擊之間的刻數                                                                                                                                         |
| `entity`    | 實體名稱       | 無             | 任務尋找、躲避、注視或與之繁殖的實體。`minecraft:player` 可被辨識                                                                                                                             |
| `items`     | 物品名稱清單   | 無             | `tempt`：玩家手中舉著的物品                                                                                                                                                                   |
| `sight`     | 布林值         | `true`         | `nearestAttackableTarget`、`targetNonTamed`：只針對它看得見的                                                                                                                                 |
| `nearby`    | 布林值         | `false`        | `nearestAttackableTarget`：只針對在它自身跟隨範圍內的                                                                                                                                         |
| `help`      | 布林值         | `false`        | `hurtByTarget`：附近的同類也加入戰鬥                                                                                                                                                          |
| `memory`    | 布林值         | `false`        | `attackMelee`、`zombieAttack`：對失去視線的目標繼續追擊                                                                                                                                       |
| `close`     | 布林值         | `false`        | `openDoor`：在身後關上門                                                                                                                                                                      |
| `nocturnal` | 布林值         | `false`        | `moveThroughVillage`：只在夜間                                                                                                                                                                |
| `scared`    | 布林值         | `false`        | `tempt`：玩家移動過快會打破魔咒                                                                                                                                                               |

`List` 欄說明任務位於何處。`tasks` 是生物所做的事；`targets` 是它如何挑選追擊對象，而沒有對應攻擊的目標任務本身不起任何作用。

| 任務                      | 需求             | 清單      | 讀取                                       | 作用                                                                       |
| ------------------------- | ---------------- | --------- | ------------------------------------------ | -------------------------------------------------------------------------- |
| `attackMelee`             | 行走的生物       | `tasks`   | `speed`, `memory`                          | 走向目標並攻擊它                                                           |
| `attackRanged`            | 會射擊的基礎實體 | `tasks`   | `speed`, `cooldown`, `distance`            | 保持距離，並射出其基礎實體所射的東西                                       |
| `attackRangedBow`         | 會射擊的怪物     | `tasks`   | `speed`, `cooldown`, `distance`            | 骷髏的弓箭戰鬥：橫向移動、拉弓、放箭                                       |
| `avoidEntity`             | 行走的生物       | `tasks`   | `entity`, `distance`, `speed`, `nearSpeed` | 指定實體進入 `distance` 範圍內時逃離它                                     |
| `beg`                     | 狼               | `tasks`   | `distance`                                 | 向手持食物的玩家乞食                                                       |
| `breakDoor`               | 任何基礎實體     | `tasks`   |                                            | 在困難難度下破壞擋路的木門                                                 |
| `creeperSwell`            | 苦力怕           | `tasks`   |                                            | 在目標旁發出嘶嘶聲並爆炸                                                   |
| `defendVillage`           | 鐵魔像           | `targets` |                                            | 攻擊任何襲擊過村民的對象                                                   |
| `eatGrass`                | 任何基礎實體     | `tasks`   |                                            | 像綿羊一樣吃草                                                             |
| `findEntityNearest`       | 任何基礎實體     | `targets` | `entity`                                   | 以最近的指定實體為目標，如同史萊姆或地獄幽靈選定目標的方式                 |
| `findEntityNearestPlayer` | 任何基礎實體     | `targets` |                                            | 以它搆得到的最近玩家為目標                                                 |
| `fleeSun`                 | 行走的生物       | `tasks`   | `speed`                                    | 被陽光照到時尋找陰涼處                                                     |
| `follow`                  | 任何基礎實體     | `tasks`   | `speed`, `near`, `distance`                | 跟隨同類                                                                   |
| `followGolem`             | 村民             | `tasks`   |                                            | 跟隨手持罌粟花的鐵魔像                                                     |
| `followOwner`             | 可馴服的基礎實體 | `tasks`   | `speed`, `near`, `distance`                | 跟隨主人，落後太遠時傳送到主人身邊                                         |
| `followOwnerFlying`       | 可馴服的基礎實體 | `tasks`   | `speed`, `near`, `distance`                | 同上，但以飛行方式                                                         |
| `followParent`            | 動物             | `tasks`   | `speed`                                    | 幼體緊跟同類的成年個體                                                     |
| `harvestFarmland`         | 村民             | `tasks`   | `speed`                                    | 收穫成熟的作物並重新種下                                                   |
| `hurtByTarget`            | 行走的生物       | `targets` | `help`                                     | 反擊任何打了它的對象                                                       |
| `landOnOwnersShoulder`    | 鸚鵡             | `tasks`   |                                            | 停在主人的肩膀上                                                           |
| `leapAtTarget`            | 任何基礎實體     | `tasks`   | `leap`                                     | 從近處向目標撲躍                                                           |
| `llamaFollowCaravan`      | 駱馬             | `tasks`   | `speed`                                    | 排在被牽著的駱馬後面                                                       |
| `lookAtTradePlayer`       | 村民             | `tasks`   |                                            | 面向正在與它交易的玩家                                                     |
| `lookAtVillager`          | 鐵魔像           | `tasks`   |                                            | 時不時向村民遞出罌粟花並看著它                                             |
| `lookIdle`                | 任何基礎實體     | `tasks`   |                                            | 時不時四處張望                                                             |
| `mate`                    | 動物             | `tasks`   | `speed`, `entity`                          | 處於求愛狀態時繁殖，對象是同類或所指定的 `entity`                          |
| `moveIndoors`             | 行走的生物       | `tasks`   |                                            | 夜幕降臨時進入村莊的房屋                                                   |
| `moveThroughVillage`      | 行走的生物       | `tasks`   | `speed`, `nocturnal`                       | 沿村莊的道路從一扇門走到另一扇門                                           |
| `moveTowardsRestriction`  | 行走的生物       | `tasks`   | `speed`                                    | 走遠後走回其家的位置                                                       |
| `moveTowardsTarget`       | 行走的生物       | `tasks`   | `speed`, `distance`                        | 向遠處的目標逼近                                                           |
| `nearestAttackableTarget` | 行走的生物       | `targets` | `entity`, `sight`, `nearby`                | 以最近的指定實體為目標                                                     |
| `ocelotAttack`            | 任何基礎實體     | `tasks`   |                                            | 貓的潛行與撲擊                                                             |
| `ocelotSit`               | 貓               | `tasks`   | `speed`                                    | 坐在箱子、床和點燃的熔爐上。馴服後的山貓變成了貓，所以這需要貓作為基礎實體 |
| `openDoor`                | 任何基礎實體     | `tasks`   | `close`                                    | 打開它經過的木門                                                           |
| `ownerHurtByTarget`       | 可馴服的基礎實體 | `targets` |                                            | 攻擊任何打了其主人的對象                                                   |
| `ownerHurtTarget`         | 可馴服的基礎實體 | `targets` |                                            | 攻擊其主人所打的對象                                                       |
| `panic`                   | 行走的生物       | `tasks`   | `speed`                                    | 受傷或著火時奔逃                                                           |
| `play`                    | 村民             | `tasks`   | `speed`                                    | 孩子們互相玩追逐遊戲                                                       |
| `restrictOpenDoor`        | 行走的生物       | `tasks`   |                                            | 夜間留在村莊的門內                                                         |
| `restrictSun`             | 行走的生物       | `tasks`   |                                            | 白天待在陰影裡                                                             |
| `runAroundLikeCrazy`      | 馬、驢、騾或駱馬 | `tasks`   | `speed`                                    | 甩掉尚未信任的騎乘者                                                       |
| `sit`                     | 可馴服的基礎實體 | `tasks`   |                                            | 被命令時坐下                                                               |
| `skeletonRiders`          | 骷髏馬           | `tasks`   |                                            | 玩家靠近時召來骷髏騎士，即陷阱馬                                           |
| `swimming`                | 任何基礎實體     | `tasks`   |                                            | 讓頭保持在水面以上                                                         |
| `targetNonTamed`          | 可馴服的基礎實體 | `targets` | `entity`, `sight`                          | 在尚未被馴服時以指定實體為目標                                             |
| `tempt`                   | 行走的生物       | `tasks`   | `items`, `speed`, `scared`                 | 跟隨手持 `items` 之一的玩家                                                |
| `tradePlayer`             | 村民             | `tasks`   |                                            | 交易時站著不動                                                             |
| `villagerInteract`        | 村民             | `tasks`   |                                            | 與其他村民聊天                                                             |
| `villagerMate`            | 村民             | `tasks`   |                                            | 村莊有空位時繁殖                                                           |
| `wander`                  | 行走的生物       | `tasks`   | `speed`, `chance`                          | 四處閒逛                                                                   |
| `wanderAvoidWater`        | 行走的生物       | `tasks`   | `speed`, `chance`                          | 閒逛，避開水                                                               |
| `wanderAvoidWaterFlying`  | 行走的生物       | `tasks`   | `speed`                                    | 在空中閒逛並棲息在樹上                                                     |
| `watchClosest`            | 任何基礎實體     | `tasks`   | `entity`, `distance`, `chance`             | 注視最近的指定實體，未指定則注視玩家                                       |
| `watchClosest2`           | 任何基礎實體     | `tasks`   | `entity`, `distance`, `chance`             | 同上，在另一個任務執行時仍保持                                             |
| `zombieAttack`            | 殭屍             | `tasks`   | `speed`, `memory`                          | 殭屍的攻擊，雙臂抬起                                                       |

### 生成與消失

*實體變體*

| 鍵                  | 必填 | 值             | 預設       | 作用                                                                                                                                                                                                                                                       |
| ------------------- | ---- | -------------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `despawns`          | 否   | 布林值         | `true`     | 關閉後，即使通常會被清除，它也會留下                                                                                                                                                                                                                       |
| `despawnAfter`      | 否   | 整數，單位為秒 | 無         | 它在世界中存在這麼久之後會悄然消失，無論周圍有多遠沒有人                                                                                                                                                                                                   |
| `persistent`        | 否   | 布林值         | `false`    | 永不消失                                                                                                                                                                                                                                                   |
| `ignoresSpawnRules` | 否   | 布林值         | `false`    | 被放在哪裡就在哪裡生成，忽略它繼承來的規則                                                                                                                                                                                                                 |
| `spawns`            | 否   | 物件清單       | 無         | `creatureType`、`weight`、`min` 和 `max`，與生態域使用的結構相同；`weight` 預設為 8，`min` 預設為 1，`max` 預設為 4。`creatureType` 是[生物類型](#值清單)之一，省略時為 `creature`，並決定該項目加入哪個生成清單；類型為遊戲所不認識的項目不會新增任何內容 |
| `biomes`            | 否   | 生態域名稱清單 | 每個生態域 | 這些生成被新增到哪裡，可用生態域 id，或 1.12.2 中對原版生態域顯示的名稱，如 `Extreme Hills`。如果既沒有這個也沒有 `biomeTypes`，每個生態域都會採用，包括地獄和終界，而被兩個清單都匹配到的生態域，每個生成只採用一次                                       |
| `biomeTypes`        | 否   | 生態域類型清單 | 無         | 同上，但依類型詞                                                                                                                                                                                                                                           |

**有保存期限的生物。** `despawnAfter` 從生物首次進入世界的那一刻起以秒計時，時間一到就悄然將其移除：沒有死亡，沒有掉落物，沒有聲音，就像它自己走開後被清除了一樣。計時資訊會寫入生物本身，所以存檔並重新載入後仍會繼續計時，而不是每次區塊重新載入時從頭開始。

它是獨立的機制，不受 `despawns` 和 `persistent` 所管規則的影響。後兩者決定遊戲是否可以因為生物遠離所有玩家而將其清除；這一項則承諾它會在設定的時間消失，不論其他條件。一隻生物可以同時 `persistent` 又有保存期限，這正適合為一場戰鬥或活動召喚出來、不應比活動存在更久的生物。

計時依據世界時間，因此無人遊玩時會暫停，區塊未載入的那段時間也不計入。

### 生成條件

*實體變體*

```json
{
  "dimensions": ["minecraft:the_nether"],
  "structures": ["minecraft:fortress"],
  "radius": 32,
  "timeFrom": 13000,
  "timeTo": 23000,
  "dayAtLeast": 3,
  "advancement": "minecraft:story/mine_stone",
  "objective": "kills",
  "scoreAtLeast": 5
}
```

| 鍵                          | 必填 | 值               | 預設     | 作用                                                                                                                                                                                                                               |
| --------------------------- | ---- | ---------------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `dimensions`                | 否   | 維度 ID 清單     | 所有維度 | 可以自然生成的維度，例如 `minecraft:the_nether`；沒有命名空間的 ID 按 `minecraft:` 讀取                                                                                                                                            |
| `structures`                | 否   | 清單             | 無       | 可以自然生成的位置：`minecraft:fortress` 這樣的結構 ID，或用 `#tag` 填寫 `#minecraft:village` 這樣的結構標籤，生成點位於其中任一部件內部時成立；也可以填寫資料包透過 `structures` 放置的結構名稱，在放置位置的 `radius` 範圍內成立 |
| `radius`                    | 否   | 整數             | `32`     | 距離資料包自帶結構多遠仍算在結構內                                                                                                                                                                                                 |
| `timeFrom`、`timeTo`        | 否   | 整數，刻         | 無       | 可以生成的時段，可以跨過午夜，例如從 `13000` 到 `1000`。兩者都需要                                                                                                                                                                 |
| `dayAtLeast`                | 否   | 整數             | 無       | 從第幾天起可以生成                                                                                                                                                                                                                 |
| `advancement`               | 否   | `namespace:path` | 無       | 僅當 128 格內有玩家擁有此進度時才生成                                                                                                                                                                                              |
| `objective`、`scoreAtLeast` | 否   | 字串，整數       | 無       | 僅當 128 格內有玩家在此計分項上至少有該分數時才生成。兩者都需要                                                                                                                                                                    |

沒有設定任何這些條件的變體照常生成；設定了多個條件的變體，只在全部滿足的地點和時間生成。它們限制自然生成，包括區塊生成時的生成；生怪蛋、`/summon`、生怪磚和 `becomes` 不受限制。`advancement`、`objective` 和 `scoreAtLeast` 會對生成點 128 格內的每名玩家（不含旁觀者）一起判定：必須由同一名玩家全部滿足，附近沒有玩家時，設定了其中任何一項的變體不會生成。

### 生成鎖

*實體變體*

`<namespace>/spawn_locks/*.json`

檔案名稱由你自行決定，只會讀取這個資料夾，多個檔案會疊加。在與變體相同類型的條件成立之前，攔下所列生物的自然生成，因此不需變體檔案也能限制任何生物。

```json
{
  "entities": ["minecraft:blaze", "mymod:thing"],
  "dimensions": ["minecraft:the_nether"],
  "structures": ["minecraft:fortress"],
  "radius": 32,
  "timeFrom": 13000,
  "timeTo": 23000,
  "dayAtLeast": 3,
  "objective": "kills",
  "scoreAtLeast": 5,
  "advancement": "minecraft:story/enter_the_nether"
}
```

| 鍵                                                                                                                   | 必填 | 值             | 預設 | 作用                                                                     |
| -------------------------------------------------------------------------------------------------------------------- | ---- | -------------- | ---- | ------------------------------------------------------------------------ |
| `entities`                                                                                                           | 是   | 實體名稱清單   | 無   | 要攔下的生物，例如 `minecraft:blaze`。沒有此鍵的檔案會被略過，日誌會註明 |
| `dimensions`, `structures`, `radius`, `timeFrom`, `timeTo`, `dayAtLeast`, `advancement`, `objective`, `scoreAtLeast` | 否   | 與生成條件相同 | 無   | 與生成條件中相同的鍵，意義也相同。一個都沒設定的檔案會被略過，日誌會註明 |

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

`storage` 為任何實體的變體提供物品欄位、流體儲槽和能量緩衝，每一項只有在寫了其物件時才會提供。每一項都作為實體的物品、流體或能量能力對外提供，因此任何向實體輸送物品、流體或能量的東西都能搆到它。如果基礎實體自己就回應該能力，比如生物回應其雙手和盔甲，馬或運輸箱礦車回應其物品欄，則由資源包的儲存取而代之作出回應，對每一側都是如此。玩家潛行並對實體按右鍵即可開啟介面。內容物會隨實體一起儲存。

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

| 鍵      | 必填   | 值          | 預設 | 作用                                                               |
| ------- | ------ | ----------- | ---- | ------------------------------------------------------------------ |
| `item`  | 三選一 | 物品 id     | 無   | 一個物品，寫作 `namespace:name`                                    |
| `tag`   | 三選一 | 物品標籤 id | 無   | 該標籤下的每個物品，如 `c:ingots/iron`                             |
| `fluid` | 三選一 | 流體 id     | 無   | 以 id 指定的流體，如 `minecraft:water`。只由流體過濾器讀取         |
| `max`   | 否     | 整數        | `0`  | 一次最多持有的數量，按所有欄位合計，流體則以 mB 計。`0` 表示無限制 |

## 生物等級

*生物與危險*

`<namespace>/ranks/*.json`

檔案路徑就是等級的名稱，多個檔案會疊加。等級是生成的生物可能獲得的一種層級。生物第一次進入世界時，遊戲會依 `weight` 從所有指明該生物的等級中抽出一個，並永久套用：等級會隨生物一起儲存，不會再次抽取。只有 `entities` 和 `weight` 的等級就是普通層級，可以用它來決定普通生物維持多大的出現比例。等級適用於任何生物，包括原版生物和實體變體，變體會保留自己的名稱。等級會與環境的 `mobs` 區塊疊加：兩者都會乘上生物的屬性。

```json
{
  "entities": ["minecraft:zombie", "#monster"],
  "weight": 10,
  "name": "Brute %s",
  "health": 2.0,
  "damage": 1.5,
  "effects": [{"potion": "minecraft:strength", "amplifier": 1}],
  "loot": 2.0
}
```

| 鍵         | 必填 | 值                   | 預設           | 作用                                                                                                                                       |
| ---------- | ---- | -------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| `entities` | 否   | 實體 ID 或類別的清單 | `["#monster"]` | 等級可以落在哪些生物上：如 `minecraft:zombie` 的 ID，或 `#monster`、`#creature`、`#ambient`、`#water_creature`。實體變體用它自己的 ID 指定 |
| `weight`   | 否   | 1 或更大的整數       | `1`            | 該等級相對於指明同一生物的其他等級的機率：它的權重除以總和                                                                                 |
| `name`     | 否   | 字串                 | 無             | 顯示在生物上方的名稱。`%s` 會變成生物自己的名稱。已有自訂名稱的生物會保留它                                                                |
| `health`   | 否   | 大於 0 的數          | `1`            | 乘以生物的最大生命值。生物以滿生命值生成                                                                                                   |
| `damage`   | 否   | 0 或更大的數         | `1`            | 乘以生物的近戰攻擊傷害                                                                                                                     |
| `effects`  | 否   | 清單                 | 無             | 永久效果，每項是帶有 `potion` ID 和 `amplifier`（預設 `0`）的物件。粒子會被隱藏                                                            |
| `loot`     | 否   | 1 或更大的數         | `1`            | 乘以生物掉落的每種可堆疊物品。小數部分是每組多掉一個的機率                                                                                 |

工具、盔甲和生物攜帶的裝備不會被乘算，帶有物品欄的生物（例如馱運動物）會被跳過。等級檔案中的錯誤（例如未知的鍵、不存在的實體 ID 或超出範圍的值）會寫入記錄檔，整個檔案會被跳過。刪除等級檔案後，已經抽到該等級的生物維持原樣，沒有任何等級指明的生物不受影響。只有伺服器會讀取等級，所以原版用戶端也可以加入。

## 工作訂單

*生物與危險*

`<namespace>/orders/*.json`

檔案名稱隨你取，只會讀取資料夾，多個檔案會疊加。一個檔案包含一個 `orders` 清單，旁邊還有三個作用於整個資源包的設定；由最後讀取的、設定了它的檔案決定，而優先順序最高的資源包最後讀取。

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
      "blocks": "c:ores/iron",
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
      "blocks": "c:crops/wheat",
      "sign": "Farm",
      "tool": "hoe",
      "standing": 64,
      "area": 16,
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

`orders` 旁邊的設定：

| 鍵          | 必填 | 值       | 預設    | 作用                                                                                           |
| ----------- | ---- | -------- | ------- | ---------------------------------------------------------------------------------------------- |
| `stockName` | 否   | 字串     | `Stock` | 讓放下的箱子成為庫存箱的名稱，不分大小寫。留空則關閉庫存箱                                     |
| `hire`      | 否   | 物品名稱 | 無      | 僱用無主工人：不潛行時手持此物品對它按右鍵，它就歸你，物品被消耗。無主工人不屬於任何玩家或隊伍 |
| `spawnCap`  | 否   | 整數     | `1`     | 沒有工人前來時，一份訂單最多可在其箱子處生成多少工人。`0` 不生成                               |

訂單的鍵：

| 鍵           | 必填            | 值                                 | 預設    | 作用                                                                                                                                                                                                                 |
| ------------ | --------------- | ---------------------------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `job`        | 是              | `gather`、`mine`、`farm` 或 `haul` |         | `gather` 和 `mine` 挖掘 `blocks` 指定的方塊（以方塊本身或其掉落物匹配），只要有一面暴露在空氣或非完整方塊旁。`farm` 收穫成熟作物，並用其自身的種子補種。`haul` 把 `blocks` 指定的物品從其他箱子和容器搬進訂單的箱子  |
| `blocks`     | 是，`farm` 除外 | 物品標籤 id                        | 無      | 訂單想要的東西，以物品標籤表示，例如 `c:ores/iron`、`minecraft:logs` 或 `c:crops/wheat`。在 `mine` 訂單中，標籤內方塊掉落的東西也計入，因此 `c:ores/iron` 會以粗鐵填滿。在 `farm` 訂單上，它只收穫會掉落該物品的作物 |
| `area`       | 否              | 整數                               | `16`    | 工作從箱子起延伸多遠，按每條軸的方塊數計                                                                                                                                                                             |
| `areaByTier` | 否              | 整數清單                           | 無      | 依工人工具的挖掘等級決定範圍：第一項用於無工具或等級 0，之後每級一項，超出清單的等級都用最後一項。會覆寫 `area`                                                                                                      |
| `deliver`    | 否              | `chest` 或 `self`                  | `chest` | `chest` 把收集到的東西送進訂單的箱子。`self` 把它留在工人自己的儲存裡，訂單在收集時計數                                                                                                                              |
| `limit`      | 否              | 整數                               | `64`    | 訂單關閉前需要多少物品                                                                                                                                                                                               |
| `standing`   | 否              | 整數                               | `0`     | 大於 `0` 時訂單永不關閉：它讓箱子裡保持這麼多物品，少了就工作。此時不讀取 `limit`                                                                                                                                    |
| `wins`       | 否              | 函式                               | 無      | 訂單完成時，以每位獲得獎勵的玩家身分執行：開啟訂單的玩家，或為隊伍開啟時該隊伍的每位線上成員                                                                                                                         |
| `points`     | 否              | 整數                               | `0`     | 訂單完成時記給每位獲得獎勵的玩家的點數，計入每個開啟了 `points.order` 的計分項。永不完成的訂單不支付任何點數                                                                                                         |
| `loot`       | 否              | 戰利品表                           | 無      | 訂單完成時，為每位獲得獎勵的玩家各抽取一次的戰利品表。物品進入其物品欄，物品欄已滿時掉落在其腳下                                                                                                                     |
| `workers`    | 否              | 整數                               | `1`     | 同時處理該訂單的最多工人數                                                                                                                                                                                           |
| `priority`   | 否              | 整數                               | `0`     | 閒置的工人先接優先順序最高的開放訂單，其次是最近的                                                                                                                                                                   |
| `takers`     | 是              | 清單                               |         | 誰可以處理它：變體名稱、`team:<team>` 表示計分板隊伍的任一成員，或 `tag:<tag>` 表示帶有該標籤的任一實體                                                                                                              |
| `sign`       | 否              | 字串                               | 無      | 告示牌第一行上開啟該訂單的詞，不分大小寫                                                                                                                                                                             |
| `speed`      | 否              | 數字                               | `1`     | 乘以工人的挖掘速度。`2` 比拿同樣工具的玩家快一倍                                                                                                                                                                     |
| `tool`       | 否              | 工具種類                           | 無      | 透過右鍵把此訂單交給工人的工具種類，例如 `pickaxe`、`axe`、`shovel` 或 `hoe`。工具無法採集某方塊的工人會回箱子取一把這種工具                                                                                         |

**工人如何工作。** 被接單者點名的變體，或在任一接單者是隊伍或標籤時的所有變體，閒置時每秒尋找一次工作。它接下箱子位於自身範圍外加 32 格以內的最佳開放訂單，以租約持有，停止工作時租約失效，儲存並重新載入後租約仍然保留。挖掘耗時與手持工人工具的玩家相同，計入效率，幸運與絲綢之觸都影響掉落。挖到的東西在變體有 `storage` 物品時放進其中，否則放進副手，絕不掉在地上。拿不下時，它走到箱子把所有東西放進去。每挖一個方塊都會磨損工具，工具損壞的工人會回箱子取一把同種類的新工具，沒有則休息一分鐘。工人死亡時會掉落工具和手中的物品，其儲存像往常一樣只灑出一次。`mobGriefing` 不會阻止它，因為這項工作是玩家要求的。

**誰為誰工作。** 開啟訂單的玩家在計分板隊伍中時，只有該隊伍的工人會接單。否則訂單歸該玩家所有，而工人歸第一個給它工作的玩家，無論是透過工具、透過 `hire`，或接下該玩家的訂單；此後它只處理該玩家的訂單。被隊伍中的玩家雇用或遞給工具的閒置工人會加入該隊伍。

**在箱子處生成。** 一個訂單十秒沒有工人時，會在其箱子旁生成一個它的變體接單者，每個訂單最多 `spawnCap` 個。生成的工人屬於訂單的隊伍或玩家。

## 曝露設定

*生物與危險*

`<namespace>/exposures/*.json`

檔案的路徑就是危害的名稱，其死亡訊息來自語言鍵 `death.attack.rdpl.<file name>`。曝露設定只在 `load` 開啟且 `vanillaClients` 關閉時載入。

資源包自訂的危害：指定的方塊、物品、維度、生態域、結構、洞穴區域和高度，會讓站在這些方塊附近、攜帶這些物品或停留在這些維度中的玩家受到曝露，分為若干等級，每個等級施加效果和週期性傷害。危害也可以從附近的生物和玩家那裡染上，或隨雨落下（[傳染與天氣](#傳染與天氣)）。一個檔案定義一種危害；多個檔案並行運作。

```json
{
  "blocks": [ "mypack:nuclear_waste=2", "mypack:uranium_ore" ],
  "items": [ "mypack:nuclear_waste" ],
  "dimensions": [ "minecraft:the_nether" ],
  "biomes": [ "minecraft:desert=2", "#minecraft:is_badlands" ],
  "structures": [ "minecraft:mansion", "#minecraft:village", "mypack:ruin=2" ],
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

| 鍵                    | 必填   | 值                                      | 預設   | 作用                                                                                                                                                             |
| --------------------- | ------ | --------------------------------------- | ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blocks`              | 九選一 | `block` 或 `block=level` 的清單         |        | 會讓站在附近的玩家受到曝露的方塊。不寫等級則為 1                                                                                                                 |
| `items`               | 九選一 | `item` 或 `item=level` 的清單           |        | 會讓攜帶或穿戴它們的玩家受到曝露的物品                                                                                                                           |
| `dimensions`          | 九選一 | `dim` 或 `dim=level` 的清單             |        | 會讓其中任何玩家受到曝露的維度 id                                                                                                                                |
| `biomes`              | 九選一 | `biome` 或 `biome=level` 的清單         |        | 讓站在其中的任何玩家受到曝露的生態域。`#` 表示生態域標籤                                                                                                         |
| `structures`          | 九選一 | `structure` 或 `structure=level` 的清單 |        | 讓身處其內部的玩家受到曝露的結構。`#` 表示結構標籤。資源包結構在 `range` 個方塊範圍內生效                                                                        |
| `caveRegions`         | 九選一 | `region` 或 `region=level` 的清單       |        | 讓身處其中的玩家受到曝露的洞穴區域。只寫名稱時使用本檔案的命名空間                                                                                               |
| `heights`             | 九選一 | `from:to=level` 的清單                  |        | 讓身處其中的玩家受到曝露的 Y 軸區間。某一側留空表示沒有限制                                                                                                      |
| `levels`              | 是     | 等級清單                                |        | 嚴重程度的階梯，第一項是 1 級。玩家獲得任何來源所達到的最高等級                                                                                                  |
| `immunity`            | 否     | 藥水名稱                                | 無     | 帶有該效果的人完全不會受到曝露                                                                                                                                   |
| `immuneItems`         | 否     | 物品名稱清單                            | 無     | 穿戴或手持其中任一物品（盔甲欄與雙手）的實體完全不會受到曝露，與 `immunity` 相同。每個護甲部位指定一件即可，任一件都足夠。無法辨識的物品名稱會記錄為錯誤並略過。 |
| `immuneTeams`         | 否     | 計分板隊伍名稱清單                      | 無     | 這些隊伍的成員（套件中的隊伍檔案或 `/scoreboard teams`）不會受到曝露。無法辨識的隊伍名稱永遠不會符合。                                                           |
| `onClear`             | 否     | 函式名稱                                | 無     | 玩家的等級降到 0 時以該玩家身分執行，包括切換到創造模式時。                                                                                                      |
| `scanInterval`        | 否     | 刻                                      | `20`   | 多久檢查一次周圍環境和物品欄                                                                                                                                     |
| `range`               | 否     | 格                                      | `10`   | 方塊的曝露範圍有多遠，為球形                                                                                                                                     |
| `sourcesForNextLevel` | 否     | 整數                                    | `0`    | 附近有這麼多個同一等級的來源，就會把該等級再推高一級。`0` 表示關閉此功能                                                                                         |
| `skipsCreative`       | 否     | 布林值                                  | `true` | 創造模式和旁觀模式的玩家不受影響                                                                                                                                 |

### 等級

*曝露設定*

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
  "weatherDimensions": [ "minecraft:overworld" ],
  "levels": [ { "effect": "mypack:sickness_1", "damage": 1.0 }, { "effect": "mypack:sickness_2", "damage": 2.0, "damageInterval": 80 } ]
}
```

| 鍵                  | 必填   | 值                                      | 預設               | 作用                                                                                              |
| ------------------- | ------ | --------------------------------------- | ------------------ | ------------------------------------------------------------------------------------------------- |
| `carriers`          | 九選一 | 清單，內容為 `entity` 或 `entity=level` |                    | 一律以該等級傳播危害的生物，或 `minecraft:player`。未指定等級則為 1                               |
| `contagious`        | 否     | 布林值                                  | `false`            | 任何受到曝露的對象都會以其所處等級傳播危害                                                        |
| `catchers`          | 否     | 清單，內容為實體名稱                    | `minecraft:player` | 可被感染的對象。生物只會從帶原者與帶有者身上感染，不會從方塊、物品或天氣感染                      |
| `contagionRange`    | 否     | 方塊                                    | `4`                | 帶原者或帶有者的影響範圍，球形                                                                    |
| `contagionChance`   | 否     | `0` 到 `1`                              | `0.1`              | 每次掃描帶原者或帶有者時，範圍內每個感染對象被感染的機率                                          |
| `contagionDuration` | 否     | 刻                                      | `1200`             | 被感染的危害維持所感染等級的時間。再次感染會重新計時                                              |
| `weather`           | 九選一 | 清單，內容為 `kind` 或 `kind=level`     |                    | `rain` 讓被雨淋到的玩家受到曝露：頭頂為開闊天空，且位於會下雨的生態域。`thunder` 在暴風雨期間計算 |
| `weatherDimensions` | 否     | `dim` 清單                              | 每個維度           | 天氣會造成曝露的維度 id                                                                           |

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
  "roles": { "ocean": "mypack:ruby_ocean" },
  "requires": ["mypack"]
}
```

| 鍵           | 必填 | 值                                | 預設     | 作用                                                                                                                                                                                                                                                                                                                                                                                                         |
| ------------ | ---- | --------------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`       | 否   | 字串                              | 檔案名稱 | 顯示在日誌和報告中                                                                                                                                                                                                                                                                                                                                                                                           |
| `default`    | 否   | 生態域名稱或 `void`               | `void`   | 填充被封鎖移除的生態域的內容。`void` 會留下虛空生態域。未登錄的生態域會被記入日誌，並改用虛空。`fallback` 是同一個鍵的另一個名字                                                                                                                                                                                                                                                                             |
| `roles`      | 否   | 角色到生態域的物件                | 無       | 充當特定角色的生態域：`ocean`、`river`、`beach`、`mushroom`、`swamp`、`hills`、`mountain`、`jungle`、`forest`、`savanna`、`sandy`、`mesa`、`snowy`、`wasteland`、`plains` 和 `water`，無論檔案中依什麼順序書寫，都依此順序檢查，因此一個既是海洋又是雪地的被封鎖生態域會採用海洋角色。指名 `void` 或未登錄生態域的角色會落到下一個。角色只在範本的 `dimensions` 中生效；在其他地方，被封鎖的生態域會變成虛空 |
| `structures` | 否   | [結構名稱](#值清單)到布林值的物件 | 無       | 開啟或關閉的原版結構                                                                                                                                                                                                                                                                                                                                                                                         |
| `settings`   | 否   | 物件                              | 無       | 範本所設定的設定值                                                                                                                                                                                                                                                                                                                                                                                           |
| `dimensions` | 否   | 維度 id 清單                      | 每個維度 | 它適用於哪些維度                                                                                                                                                                                                                                                                                                                                                                                             |
| `requires`   | 否   | 模組 id 或資源包命名空間清單      | 無       | 除非全部存在，否則會略過該範本                                                                                                                                                                                                                                                                                                                                                                               |

`settings` 使用與設定檔相同的鍵名，因此不需要另外學習對照表。

哪個範本生效由 `worldTemplate` 設定選項決定。保持 `auto` 時，提供範本的最高優先順序資源包勝出，遵循與其他一切相同的順序；當不止一個資源包提供範本時，日誌會列出它們全部以及生效的那一個，因為其餘的都不起作用，連其設定也一樣。在那裡指名一個範本即可直接選用。內建了五個，可以這樣指名：`void`、`vanilla`（海洋、河流、海灘、蘑菇地、沼澤和丘陵保持為遊戲自己的，其他各處為平原）、`ocean`（保留河流和海灘，其他各處為海洋）、`plains` 和 `desert`。`auto` 絕不會選用內建範本。

**同一個生態域可以有不同的建法。** `settings` 內的 `biomes` 物件為指定的生態域保存它自己的村莊設定，因此沙漠村莊鋪砂岩街道，而平原村莊鋪混凝土，兩者無需分成不同的資源包。可以用生態域 id 指名，如 `minecraft:desert`，也可以用本模組對應到生態域標籤的類型詞之一（`sandy`、`snowy`、`desert`、`forest`、`jungle`、`mountain`、`ocean`、`swamp`、`hot`、`cold` 等等），或寫出完整的標籤，`#minecraft:is_forest`；精確 id 先於類型被檢查，因此可以為某一個生態域覆寫一條通用規則。凡是某一節中未指名的內容，都回落到其上方的一般設定。

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

道路、橋梁、鐵路、地鐵、車站或下水道所接受的每個方塊設定都適用於此，並且加權混合語法在區段內部與在外部同樣有效。生態域是在建造每個片段時讀取的，而在地面生態域發生變化的地方，方塊會重新選取，因此跨出沙漠的道路或鐵路會在邊界處當場更換材料。世界載入時的一行日誌會說明資源包附帶了多少個區段並列出它們的名稱，開啟除錯後，每個生態域還會說明它採用了哪個區段，或者說明它沒有採用任何區段以及它本會對應哪個區段。

## 遊戲規則

*世界*

`<namespace>/gamerules/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

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

每個鍵是這些規則所屬世界的 id，`minecraft:overworld`、`minecraft:the_nether`、`minecraft:the_end`，資源包自己的，或者某個模組使用的；1.12.2 的數字 `0`、`-1` 和 `1` 仍被當作原版的這三個。值是字串，與 `/gamerule` 指令中一樣，所以寫 `"false"` 而不是 `false`。這些規則套用於新世界。檔案中省略的規則在該世界中使用遊戲自己的預設值，而不是存檔其餘部分所用的值，帶有該資源包的用戶端讀取同樣的規則。維度檔案則在 `gameRules` 區段中帶有同樣的規則，那只會套用於該世界。

## 生態域

*世界*

`<namespace>/biomes/*.json`

檔案的路徑就是生態域的登錄名稱，因此 `mypack/biomes/ruby_forest.json` 登錄的是 `mypack:ruby_forest`。`name` 只是向玩家顯示的內容，而語言檔案中的 `biome.mypack.ruby_forest` 則以每種語言說出它。

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

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
  "skyStone": "minecraft:end_stone",
  "skyIslands": 0.5,
  "skyThickness": 2.0,
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
  "craters": { "chance": 0.1, "minRadius": 8, "maxRadius": 16, "depth": 6 },
  "requires": ["mypack"]
}
```

### 該生態域

*生態域*

| 鍵          | 必填 | 值                           | 預設               | 作用                                                                                                                                                                                                                                                                                                                                                                             |
| ----------- | ---- | ---------------------------- | ------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`      | 否   | 字串                         | 檔案名稱           | 向玩家顯示的名稱                                                                                                                                                                                                                                                                                                                                                                 |
| `types`     | 否   | 生態域類型清單               | 推斷               | 把該生態域寫入這些類型詞所代表的標籤，如 `forest`、`cold`、`wet` 或 `nether`，以便其他模組找到它。省略時，類型依遊戲當年推斷的方式從生態域推斷：三棵樹或更多則為 `forest` 或 `jungle`，否則為 `plains`，由溫度和降雨量得出 `hot`、`cold`、`wet` 和 `dry`，由樹木數量得出 `sparse` 或 `dense`，由 `snow` 得出 `snowy`，由沙子、菌絲土或陶土地面得出 `sandy`、`mushroom` 或 `mesa` |
| `baseBiome` | 否   | 生態域名稱                   | `minecraft:plains` | 用於複製設定的現有生態域。不是遊戲或某個模組所提供的生態域時會被記入日誌，並改用平原                                                                                                                                                                                                                                                                                             |
| `requires`  | 否   | 模組 id 或資源包命名空間清單 | 無                 | 除非全部存在，否則會略過該檔案                                                                                                                                                                                                                                                                                                                                                   |

在此版本中，生態域是資料包項目，會替你寫入 `worldgen/biome/` 下，而其下的地形屬於雜訊設定而非生態域，這就是為什麼沒有 `baseHeight` 或 `heightVariation`：陸地的形狀取決於氣候把該生態域放在何處，與遊戲自己的生態域一樣。1.12.2 的 `id` 會被讀取並忽略。

### 氣候

*生態域*

| 鍵            | 必填 | 值           | 預設    | 作用                                                                                                     |
| ------------- | ---- | ------------ | ------- | -------------------------------------------------------------------------------------------------------- |
| `temperature` | 否   | 小數         | `0.5`   | 低於 0.15 下雪，高於 1.0 為沙漠般的炎熱                                                                  |
| `rainfall`    | 否   | 小數，0 到 1 | `0.5`   | 有多潮濕                                                                                                 |
| `rain`        | 否   | 布林值       | `true`  | 是否會出現天氣                                                                                           |
| `snow`        | 否   | 布林值       | `false` | 雨是否以雪的形式落下。只有溫度低於 0.15 的地方才會下雪，而這一項不會改變溫度，因此較溫暖的生態域仍會下雨 |

### 地面與顏色

*生態域*

| 鍵             | 必填 | 值                | 預設       | 作用                                                                                        |
| -------------- | ---- | ----------------- | ---------- | ------------------------------------------------------------------------------------------- |
| `topBlock`     | 否   | 方塊名稱          | 草地       | 地表方塊                                                                                    |
| `fillerBlock`  | 否   | 方塊名稱          | 泥土       | 地表正下方                                                                                  |
| `stoneBlock`   | 否   | 方塊名稱          | 石頭       | 地面的主體                                                                                  |
| `skyStone`     | 否   | 方塊名稱          | 沿用世界的 | 該生態域中天空陸地所用的方塊，取代世界的 `skyStone`。僅在世界的 `skyStone` 指定了方塊時使用 |
| `skyIslands`   | 否   | 數字，-1.0 到 1.0 | 沿用世界的 | 該生態域中天空陸地聚成島嶼的難易程度，取代世界的 `skyIslands`                               |
| `skyThickness` | 否   | 數字，0.0 到 8.0  | 沿用世界的 | 該生態域中天空島嶼有多厚實，取代世界的 `skyThickness`                                       |
| `waterColor`   | 否   | 十六進位色碼      | `FFFFFF`   | 水的色調                                                                                    |
| `grassColor`   | 否   | 十六進位色碼      | 取自氣候   | 草的色調，取代溫度和降雨量所給出的顏色                                                      |
| `foliageColor` | 否   | 十六進位色碼      | 取自氣候   | 樹葉的色調，方式相同                                                                        |
| `snowColor`    | 否   | 十六進位色碼      | 取自維度   | 地面積雪的色調，優先於維度的 `snowColor`                                                    |

### 裝飾與生成

*生態域*

| 鍵                  | 必填 | 值                                                                                 | 預設           | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| ------------------- | ---- | ---------------------------------------------------------------------------------- | -------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `decoration`        | 否   | 物件                                                                               | 基礎生態域的值 | 每區塊的數量，改變基礎生態域已經放置的內容。它讀取的名稱有 `trees`、`flowers`、`grass`、`deadbush`、`mushrooms`、`bigmushrooms`、`reeds`、`cacti`、`sand`、`gravel`、`clay` 和 `waterlily`，以及開關 `falls`（湖泊和泉水）、`pumpkins`、`desertwells`、`ice`（冰刺和冰塊地帶）、`fossils` 和 `rocks`（森林巨石），其中任何大於零的值都保持基礎生態域自己的比率，零或更小則移除該地物，還有 `extratreechance`，即多一棵樹的百分比機率，其中 `0` 也會移除基礎生態域所擲出的額外那棵樹。對基礎生態域不放置的種類寫數量不會新增任何內容；為此請寫一個世界生成項目。其他任何名稱都會被記入日誌並忽略 |
| `spawns`            | 否   | 物件清單                                                                           | 原版清單       | 見下文                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `keepDefaultSpawns` | 否   | 布林值                                                                             | `false`        | 在你的清單之外保留原版的清單                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `spawnChance`       | 否   | 小數，小於 1                                                                       | `0.1`          | 陸地首次生成時，再放置另一群的機率。只要成功，遊戲就會繼續擲骰，因此 1 永不停止，會一直填滿世界直到沒有空間。任何大於等於 0.99 的值都會被拒絕，改用 0.99                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `spawnRates`        | 否   | 由 `surfaceDay`、`surfaceNight`、`undergroundDay`、`undergroundNight` 到倍數的物件 | 無             | 敵對生物在此處多久生成一次，取代全域設定。見下文                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |

生成項目接受 `entity`（必填）、`type`（預設 `creature`，取 `monster`、`creature`、`ambient` 或 `water` 之一，`water_creature` 這類名稱中的底線可有可無）、`weight`（`10`）、`min`（`1`）和 `max`（`min`）。

`spawnRates` 只針對敵對生物，不涉及其他任何東西。它只接受四個鍵，沒有其他鍵：`surfaceDay` 與 `surfaceNight` 用於看得見天空的地方，`undergroundDay` 與 `undergroundNight` 用於看不見天空的地方。每個值都是敵對生物允許出現頻率的倍率，`1` 為一般頻率，`0` 完全阻止生成，小於 1 會減少部分嘗試，大於 1 則放行遊戲原本會拒絕的嘗試，因此 `2` 表示兩倍。省略某個鍵，表示該生態域不做決定，改用該時間與地點的全域設定。此處寫入的其他任何內容都不是鍵，會被忽略，因此以生物類型命名的頻率完全不會有作用。

### 生成位置

*生態域*

| 鍵            | 必填 | 值                                       | 預設 | 作用                                                                                           |
| ------------- | ---- | ---------------------------------------- | ---- | ---------------------------------------------------------------------------------------------- |
| `placement`   | 否   | 物件                                     | 無   | 它在何處生成。見下文                                                                           |
| `villageType` | 否   | `oak`、`sandstone`、`acacia` 或 `spruce` | 無   | 站在此處的村莊用什麼建造：平原、沙漠、莽原或針葉林村莊。留空則建造平原村莊，與沒有這個鍵時一樣 |

| 鍵            | 必填 | 值                                          | 預設    | 作用                                                                                                                                                                                        |
| ------------- | ---- | ------------------------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `climate`     | 否   | `icy`、`cool`、`medium`、`warm` 或 `desert` | 無      | 它加入哪個氣候帶，即主世界自己的生態域所依據分配的那五個。省略、`weight` 為 0 或寫了此處未列出的氣候時，該生態域會被登錄但永遠不會被放置，除非範本的 `roles`、維度的 `biome` 或高度帶要求它 |
| `weight`      | 否   | 整數                                        | `10`    | 在該氣候帶中與相鄰生態域相比被選中的頻率                                                                                                                                                    |
| `villages`    | 否   | 布林值                                      | `false` | 允許生成村莊                                                                                                                                                                                |
| `strongholds` | 否   | 布林值                                      | `false` | 允許生成要塞                                                                                                                                                                                |
| `playerSpawn` | 否   | 布林值                                      | `false` | 世界出生點可以放在這裡                                                                                                                                                                      |

### 高度帶

*生態域*

| 鍵          | 必填 | 值             | 預設       | 作用                                                                                                                                                                       |
| ----------- | ---- | -------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `minHeight` | 否   | 整數           | 無         | 該生態域作為 3D 生態域接管的最低 y。設定任一高度都會把該生態域變成一個帶：在帶外，該縱列保持自己的生態域，而在帶內，世界的每個 4×4×4 單元格都回報為這一個                  |
| `maxHeight` | 否   | 整數           | 無         | 該帶的最高 y                                                                                                                                                               |
| `replaces`  | 否   | 生態域名稱清單 | 每個生態域 | 把該帶限制在自身生態域在此列出的那些縱列，因此高山帶可以覆蓋在山地之上而不觸及其他地方。該縱列自身的生態域是其地表處的那一個。沒有 `minHeight` 或 `maxHeight` 時它不起作用 |

### 隕石坑

*生態域*

```json
{
  "craters": { "chance": 0.1, "minRadius": 8, "maxRadius": 16, "depth": 6 }
}
```

`craters` 物件會在該生態域所在之處的地面壓出圓形的坑，就像月球布滿隕石坑一樣。沒有它，該生態域就沒有隕石坑。隕石坑是地形首次生成時的一部分，依據世界種子放置，已經存在的地形維持原樣。隕石坑要麼完整，要麼不存在，絕不會被區塊邊界截斷：村莊附近是否形成隕石坑，在其任一區塊首次生成時就已決定，並儲存在存檔裡，因此之後的每個區塊都與之一致，重新啟動後也一樣。它們在該生態域出現的每個維度中都有效，無論是主世界、地獄、終界還是平坦世界，也無論是資源包自有的維度還是原版維度，而且不需要其他模組。

隕石坑中心所在的生態域決定它是否形成以及有多大，所以靠近生態域邊緣的隕石坑會稍微伸進相鄰的生態域。坑從地面向下挖，中心最深，向邊緣陡峭地升起，隨後地形自身的地表處理會從坑底上方走過，所以坑底會得到該生態域在那裡鋪設的草、沙或石頭。平坦維度不鋪設地面，所以那裡的坑底就是下面的地層。在水下或熔岩中，坑會被該液體填滿。隕石坑絕不會挖穿一列中最後一個固體方塊。

隕石坑絕不會出現在村莊、其建築、城市街道、地塊和鐵路連接（包括尚待鋪設的）或[結構地圖](#結構地圖)周圍 12 格以內，這樣它們就能立在為其規劃的地面上。

| 鍵          | 必填 | 值                 | 預設  | 作用                                                               |
| ----------- | ---- | ------------------ | ----- | ------------------------------------------------------------------ |
| `chance`    | 否   | 浮點數，`0` 到 `1` | `0.1` | 每個區塊包含隕石坑中心的可能性。`0` 會關閉隕石坑                   |
| `minRadius` | 否   | 整數，`2` 到 `64`  | `8`   | 隕石坑的最小半徑，單位為方塊                                       |
| `maxRadius` | 否   | 整數，`2` 到 `64`  | `16`  | 最大半徑，絕不低於 `minRadius`。每個隕石坑在兩者之間均勻地選取半徑 |
| `depth`     | 否   | 整數，`1` 到 `32`  | `6`   | 坑中心有多少個方塊深。越靠近邊緣越淺                               |

### 隨高度變化的溫度

*生態域*

**隨高度變化的溫度。** 生態域隨海拔升高而變冷，這就是山頂有雪、某條線以上不下雨的原因。三個 `terrain` 鍵可以移動這條曲線，這對於地面遠高於或遠低於遊戲所假定高度的維度很重要。不設定時，遊戲自己的曲線保持不變，因此不去動它們的資源包什麼都不會改變。

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

| 鍵                             | 值   | 預設       | 作用                                                                                       |
| ------------------------------ | ---- | ---------- | ------------------------------------------------------------------------------------------ |
| `biomeTemperatureCenterY`      | 整數 | `80`       | 曲線的度量起點高度。在此高度或以下，生態域回報其自身未經改動的 `temperature`               |
| `biomeTemperatureHeightFactor` | 小數 | `-0.00125` | 高於該高度後每格溫度變化多少，即遊戲自己的每 40 格 0.05。負值隨海拔變冷，正值變暖          |
| `biomeTemperatureScaleMaxY`    | 整數 | 無         | 曲線停止的高度，這樣比遊戲自身更高的世界就不會一路冷卻到頂部。不設定時，曲線延伸到世界頂部 |

## 維度

*世界*

`<namespace>/dimensions/*.json`

檔案的路徑就是維度的 id，因此 `mypack/dimensions/verdant.json` 就是 `mypack:verdant`，傳送門、閘門、遊戲規則檔案和 `/execute in` 所指的都是它。此版本沒有數字 id，1.12.2 的 `id` 或 `suffix` 會被讀取並忽略。

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

```json
{
  "requires": ["mypack"],
  "terrain": {
    "type": "overworld",
    "minHeight": -64,
    "maxHeight": 320,
    "generatorOptions": { "seaLevel": 63, "useLavaOceans": false, "oceanFluid": "mypack:molten_ruby" },
    "structures": false
  },
  "biomes": {
    "source": "single",
    "biome": "mypack:ruby_forest",
    "biomeList": ["minecraft:plains", { "biome": "minecraft:forest", "weight": 20 }, "mypack:ruby_forest"]
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
    "cloudSpeed": 2.0,
    "cloudLayers": [
      { "height": 140, "speed": 1.0, "color": "#FFFFFF" },
      { "height": 220, "speed": -3.0, "color": "#C0A0FF" }
    ],
    "groundLevel": 63,
    "movementFactor": 4.0,
    "fogColor": "20102A",
    "fogDensity": 0.4,
    "fogGroundWeight": 0.6,
    "showFog": false,
    "skyColor": "3B1E4A",
    "fixedTime": 18000,
    "sunriseColors": true,
    "ambientLight": 0.1,
    "lightSkyColor": "#FFD8A0",
    "lightBlockColor": "#A0C0FF",
    "skyFactor": 0.7,
    "starBrightness": 0.8,
    "sunBrightness": 0.5,
    "moonBrightness": 0.3,
    "heat": { "strength": 0.1, "minTemperature": 1.5, "dayOnly": true, "mode": "world", "startDistance": 32 },
    "renderSky": true,
    "renderClouds": true,
    "renderWeather": true,
    "sun": { "texture": "mypack:textures/environment/red_sun.png", "size": 18 },
    "moon": { "texture": "mypack:textures/environment/pink_moon_phases.png", "size": 20 },
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
    "alwaysRain": false,
    "cycle": { "rainTicks": [1000, 4600], "clearTicks": [1000, 3000], "maxStrength": 0.6, "thunderTicks": [3600, 15600], "calmTicks": [12000, 60000], "thunderStrength": 1.0 },
    "rain": { "particle": "minecraft:rain", "sound": "minecraft:weather.rain", "volume": 0.2, "interval": 3, "color": "#88AAFF", "colorMode": "solid", "snowColor": "#FFFFFF", "angle": 30, "heading": 90, "splashUpward": true },
    "wind": { "gust": 15, "every": [200, 600], "swing": 30 }
  },
  "ambience": { "music": "mypack:music.ruby", "musicDelay": [1200, 3600], "loopSound": "mypack:ambient.ruby_wind", "ambientSound": "minecraft:ambient.cave", "soundChance": 0.0111, "particle": "minecraft:dust", "particleChance": 0.00625, "particleColor": "#FF4060" },
  "gameRules": { "doMobSpawning": "false" },
  "portal": { "frames": ["mypack:standing_gate"], "ignitedBy": "minecraft:flint_and_steel" }
}
```

### 頂層

*維度*

| 鍵          | 必填 | 值                           | 預設 | 作用                                                  |
| ----------- | ---- | ---------------------------- | ---- | ----------------------------------------------------- |
| `gameRules` | 否   | 物件                         | 無   | 只在此處適用的規則                                    |
| `portal`    | 否   | 物件                         | 無   | 開啟此維度的框架。見[用框架開啟維度](#用框架開啟維度) |
| `requires`  | 否   | 模組 id 或資源包命名空間清單 | 無   | 除非全部存在，否則會略過該檔案                        |

在此版本中，維度是資料包項目：維度類型和雜訊設定會替你寫入資源包的命名空間下，因此原版用戶端在加入時會被告知它的存在，並像前往其他任何維度一樣前往那裡。維度在世界下保有它自己的存檔資料夾，以其 id 命名，在有人處於其中，或有 `forceload` 保持著某個區塊時載入。

### 既有維度

*維度*

```json
{
  "sky": { "moon": { "texture": "mypack:textures/environment/pink_moon_phases.png" } },
  "weather": { "alwaysRain": true }
}
```

位於 `minecraft/dimensions/overworld.json`、`the_nether.json` 或 `the_end.json` 的檔案，或位於另一個模組或資料包所新增維度的 id 處的檔案（例如 `othermod/dimensions/caverns.json`），會修改那個維度，而不是新增一個。檔案寫下的每個鍵都在那裡生效，省略的每個鍵都保持該維度自身的行為，因此它的關卡資料和區塊保持原樣，現有存檔照常載入。

`terrain.type` 會更換生成器：此後生成的區塊都由它生成，`generatorOptions`、`structures` 和 `biomes` 區段也隨之生效；沒有 `terrain.type` 時，這些鍵會被忽略，日誌中會說明，因為在此版本中維度的生態域屬於其生成器。維度類型所含的鍵，例如 `hasSkyLight` 或 `ambientLight`，會寫入該維度自己的類型。使用遊戲共用類型之一、或在檔案中內嵌寫出類型的模組維度會保持其類型不變，日誌中會說明。摘要中的 `dimensions.existing` 一行列出以這種方式套用的每個檔案。

只設定 `texture` 的 `sun` 或 `moon`，會替換該維度原本天空中的圖片，月亮則保留其月相。設定 `bodies`、`stars`、`sun.size` 或 `moon.size` 會把整個天空換成 [RDPL 自己的天空](#天空渲染器)。

### `terrain` 區段

*維度*

| 鍵                 | 必填 | 值                                           | 預設           | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| ------------------ | ---- | -------------------------------------------- | -------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`             | 否   | `overworld`、`flat`、`void`、`nether`、`end` | `overworld`    | 由遊戲的哪個生成器來建構它，其雜訊設定會被複製並由下面的鍵修改                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `minHeight`        | 否   | 整數，16 的倍數                              | 該類型自身的值 | 維度的底部。低於該類型自身的值時，會在地形之下形成深層世界，見[深層世界](#深層世界)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `maxHeight`        | 否   | 整數，16 的倍數                              | 該類型自身的值 | 其頂部之上的那一格方塊                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `generatorOptions` | 否   | 物件、文字或清單                             | 無             | 對於 `overworld` 和其他類型，是一個物件，或 1.12.2 所寫的該物件的文字，包含 `seaLevel`、`useLavaOceans`、`oceanFluid`（任何流體 ID，例如 `minecraft:lava`，或資料包中的流體如 `mypack:molten_ruby`，其方塊會填充海平面以下的海洋與河流；它優先於 `useLavaOceans`，未載入的流體會記入日誌，改由 `useLavaOceans` 決定），以及設為 false 以在此維度中省略相應內容的 `useCaves`、`useRavines`、`useDungeons`、`useLavaLakes`、`useStrongholds`、`useVillages`、`useMineShafts`、`useTemples`、`useMonuments` 和 `useMansions`，還有地形形狀鍵 `coordinateScale`、`heightScale`、`upperLimitScale`、`lowerLimitScale`、`mainNoiseScaleX`、`mainNoiseScaleY`、`mainNoiseScaleZ`、`depthNoiseScaleX`、`depthNoiseScaleZ`、`depthNoiseScaleExponent`、`baseSize`、`stretchY`、`biomeDepthWeight`、`biomeDepthOffset`、`biomeScaleWeight` 和 `biomeScaleOffset`，它們依主世界設定中所述的方式塑造此維度的地形。礦石鍵（coal、iron、gold、redstone、diamond、dirt、gravel、granite、diorite 和 andesite 的 `<ore>Size`、`<ore>Count`、`<ore>MinHeight` 和 `<ore>MaxHeight`，以及 `lapisSize`、`lapisCount`、`lapisCenterHeight` 和 `lapisSpread`）以及 `dungeonChance`、`lavaLakeChance`、`waterLakeChance` 和 `useWaterLakes` 依主世界設定中所述的方式放置此維度的礦石、地城和湖泊，只作用於此維度；`biomeSize` 和 `riverSize` 作用於整個世界，只從主世界的設定讀取。對於 `flat`，是由下而上的各層，如 `"minecraft:bedrock"`、`"59*minecraft:stone"`、`"3*minecraft:dirt"`、`"minecraft:grass_block"`，這也是預設的地面，或者 1.12.2 的超平坦文字，其生態域編號設定生態域，其 `decoration`、`lava_lake` 和結構名稱的讀取方式與主世界的 `generatorOptions` 相同 |
| `structures`       | 否   | 布林值                                       | `true`         | 原版結構是否生成                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |

### `biomes` 區段

*維度*

| 鍵          | 必填             | 值                                            | 預設               | 作用                                                                                                                                                                                                                                                                                                                                           |
| ----------- | ---------------- | --------------------------------------------- | ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `source`    | 否               | `inherit`、`single`、`list`                   | `inherit`          | `inherit` 無論地形類型如何都使用主世界自己的生態域圖，因此 `nether` 或 `end` 維度會在自己的地面上得到主世界的生態域；`single` 在各處使用同一個生態域，`list` 在任何地形類型下（包括 `flat`、`nether` 和 `end`）都把 `biomeList` 中的生態域分布到整個維度。其他情況下 `flat` 維度只有一個生態域，即 `single` 指定的那個或其超平坦文字所指的那個 |
| `biome`     | 使用 `single` 時 | 生態域名稱                                    | `minecraft:plains` | 是哪個生態域                                                                                                                                                                                                                                                                                                                                   |
| `biomeList` | 使用 `list` 時   | 生態域名稱或 `{"biome", "weight"}` 物件的清單 | 無                 | `list` 唯一使用的生態域。每個區域寬約 256 格，邊界圓潤而不規則，並依世界種子碼從清單中隨機取一個生態域，機率與其 `weight` 成正比（單寫名稱時為 10，與生態域定義相同），因此權重相同時各生態域出現頻率大致相同；任意兩個都可能相鄰。不是已登錄生態域的名稱會記入日誌並被略過；若一個都不剩，該維度使用 `minecraft:plains`                       |

### `sky` 區段

*維度*

| 鍵                 | 必填 | 值                      | 預設    | 作用                                                                                                                                       |
| ------------------ | ---- | ----------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| `hasSkyLight`      | 否   | 布林值                  | `true`  | 日光是否能照入                                                                                                                             |
| `surfaceWorld`     | 否   | 布林值                  | `true`  | 地圖與指南針是否如主世界般運作                                                                                                             |
| `respawn`          | 否   | 布林值                  | `true`  | 玩家是否在此重生                                                                                                                           |
| `respawnDimension` | 否   | 維度 id                 | 無      | 他們改為在哪裡重生                                                                                                                         |
| `spawning`         | 否   | 布林值                  | `true`  | 生物是否生成。關閉會阻止一切生成，包括生怪磚，無論 `spawning` 群組怎麼說                                                                   |
| `nether`           | 否   | 布林值                  | `false` | 在傳送門與天花板方面視為地獄                                                                                                               |
| `beds`             | 否   | 布林值                  | `true`  | 關閉時，床會爆炸                                                                                                                           |
| `waterVaporizes`   | 否   | 布林值                  | `false` | 放置的水會蒸發：水桶與發射器產生蒸氣而不是水，破壞或融化的冰不留下任何東西，熔岩的流動方式與地獄相同。世界中既有的水或由地形產生的水則保留 |
| `cloudHeight`      | 否   | 整數                    | `128`   | 雲所在的高度。指名此維度的 `cloudHeight` 設定，或不帶維度的設定，會勝過它                                                                  |
| `cloudColor`       | 否   | 十六進位顏色            | 無      | 雲的色調                                                                                                                                   |
| `cloudSpeed`       | 否   | 浮點數                  | `1.0`   | 雲飄移的速度。`0` 使其靜止，負值則反向                                                                                                     |
| `cloudLayers`      | 否   | 清單，內容為物件        | 無      | 多個雲層。見[霧、光照、雲與熱浪](#霧光照雲與熱浪)                                                                                          |
| `groundLevel`      | 否   | 整數                    | `63`    | 海平面，用於地平線、出生點搜尋，以及傳送門抵達或在虛空上方墜落後落地的位置                                                                 |
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

`sun.color` 為太陽著色，`sun.auraColor` 則在太陽周圍鋪上一層柔和的光暈，即太陽霧靄。兩者都像太陽一樣，隨降雨和 `sunBrightness` 而減弱。它們既適用於[既有維度](#既有維度)的原版天空，也適用於 RDPL 自己的天空，而且[時間視窗](#time-區段)可以在一天的某段時間內改變它們。

| 鍵                     | 必填 | 值             | 預設     | 作用                                                                                                                           |
| ---------------------- | ---- | -------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------ |
| `sun.texture`          | 否   | 材質路徑       | 原版太陽 | 太陽的圖片                                                                                                                     |
| `sun.size`             | 否   | 浮點數         | `30`     | 太陽在距離 100 處寬度的一半。`0` 會隱藏它                                                                                      |
| `sun.color`            | 否   | 十六進位顏色   | 白色     | 太陽的色調                                                                                                                     |
| `sun.auraColor`        | 否   | 十六進位顏色   | 無       | 太陽周圍光暈的顏色。不設定則沒有光暈                                                                                           |
| `moon.texture`         | 否   | 材質路徑       | 原版月亮 | 八個月相排成的一張圖，橫四直二。不設定時繪製原版月亮                                                                           |
| `moon.size`            | 否   | 浮點數         | `20`     | 月亮在距離 100 處寬度的一半。`0` 會隱藏它。與 `bodies` 並用時省略                                                              |
| `bodies[].texture`     | 是   | 材質路徑       |          | 天體的圖片                                                                                                                     |
| `bodies[].size`        | 否   | 浮點數         | `20`     | 在距離 100 處寬度的一半。原版月亮為 `20`                                                                                       |
| `bodies[].angle`       | 否   | 浮點數，度     | `180`    | 它在太陽軌道上位於太陽後方多遠。`180` 是原版月亮所在的位置。`followsTime` 關閉時，從正上方起算，因此 `0` 是天頂，`90` 是地平線 |
| `bodies[].tilt`        | 否   | 小數，單位為度 | `0`      | 它偏離太陽軌跡多遠，向北或向南                                                                                                 |
| `bodies[].followsTime` | 否   | 布林值         | `true`   | 關閉時，它靜止懸掛在天空中，而不隨太陽轉動                                                                                     |
| `stars.count`          | 否   | 整數           | `1500`   | 星星的數量                                                                                                                     |
| `stars.size`           | 否   | 浮點數         | `0.15`   | 最小的星星；最大的比它再大三分之二                                                                                             |

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

在此版本中，`waterFogColor` 會在整個維度內取代每個生態域在其 `effects` 中設定的 `water_fog_color`。未設定該鍵時，原版生態域 JSON 的 `water_fog_color` 照常生效。

`starColor` 為星星染色，不論在原版天空，還是你自己於[天空渲染器](#天空渲染器)中設定的天空。`starTwinkle` 讓星星閃爍：星星分成八組，每組以自己的節奏變暗與變亮，其值是變暗的深度，因此值為 `1` 時，一組星星在最暗處會完全淡出。

`lightningColor` 為閃電染色。

| 鍵               | 必填 | 值           | 預設         | 作用                                 |
| ---------------- | ---- | ------------ | ------------ | ------------------------------------ |
| `snowColor`      | 否   | 十六進位色碼 | 白色         | 雪層和雪塊的色調                     |
| `waterFogColor`  | 否   | 十六進位色碼 | 生態域的顏色 | 水下的霧色                           |
| `lavaFogColor`   | 否   | 十六進位色碼 | `991A00`     | 熔岩中的霧色                         |
| `starColor`      | 否   | 十六進位色碼 | 白色         | 星星的色調                           |
| `starTwinkle`    | 否   | 小數，0 到 1 | `0.0`        | 星星閃爍時變暗的程度。`0` 表示不閃爍 |
| `grassTint`      | 否   | 十六進位顏色 | 白色         | 與草、高草、蕨和甘蔗的顏色相乘       |
| `foliageTint`    | 否   | 十六進位顏色 | 白色         | 與樹葉和藤蔓的顏色相乘               |
| `lightningColor` | 否   | 十六進位色碼 | `737380`     | 閃電的色調                           |

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

`skybox` 把你自己的圖片畫在天空上，位於日出光暈、太陽、月亮與星星之後。以完整的材質路徑提供立方體的全部六個面，依立方體展開圖排列：`up` 接在 `north` 的上緣，`down` 接在其下緣，`west` 在其左側，`east` 在其右側，`south` 則在 `east` 之後。或者只提供 `panorama`，以一張 2:1 的圖片環繞整個天空：它的左緣面向北方，順時針經過東、南、西方，最上一列是正上方，最下一列是正下方。缺少某個面又沒有 panorama 的天空盒會被略過，並在日誌中記錄錯誤。

`aurora` 在夜間於北方天空低處懸掛發光的簾幕。它們緩慢起伏，隨日落淡入，白天與下雨時消失。`color` 是簾幕底部的顏色，`topColor` 則是它們在頂部漸變成的顏色。

`rainbow` 在白天雨停之後，於太陽的對側顯示彩虹。它會在雨結束後的兩分鐘內淡出，新的陣雨會將其清除。

原版沒有任何鍵能為維度提供專屬的天空圖片、極光或彩虹，因此它們在每個版本上的運作方式相同。

```json
{
  "sky": {
    "skybox": { "panorama": "mypack:textures/sky/panorama.png" }
  }
}
```

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

這些與範本鍵 `worldGravity` 和 `worldFallDamage` 是相同的倍數，只是設定在維度上。世界範本中針對此維度的 `dimension=value` 一行仍然優先；不帶維度的世界範本值只覆蓋那些自己什麼都沒設定的維度。

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

`windows` 在每天的某段時間內改變 `sky` 與 `weather` 的幾個鍵：此處是夜間的霧、黎明前後較淡的霧與朦朧的紅色太陽，以及只在夜間下的雨。`from` 與 `to` 是一天中的時刻，採用通常的 24000 刻刻度，其中 0 為日出，6000 為正午，12000 為日落，18000 為午夜；它們會隨 `dayLength` 伸縮，因此 13000 永遠是日落剛過。`from` 大於 `to` 的視窗會跨過午夜。視窗重疊時，清單中靠前的那個生效。有 `fixedTime` 時一天中的時刻不會推移，所以視窗要麼一直生效，要麼從不生效。

視窗開始與結束時，霧與太陽的變化會在一兩秒內淡入淡出。所有視窗之外，使用維度自身的鍵。

| 鍵                                | 必填 | 值                      | 預設         | 作用                                              |
| --------------------------------- | ---- | ----------------------- | ------------ | ------------------------------------------------- |
| `dayLength`                       | 否   | 整數，刻                | `24000`      | 此處一個完整晝夜的長度。月相仍然每 24000 刻轉一圈 |
| `windows[].from`                  | 是   | 整數，0 到 24000        |              | 視窗開始的時刻                                    |
| `windows[].to`                    | 是   | 整數，0 到 24000        |              | 視窗結束的時刻                                    |
| `windows[].sky.fogDensity`        | 否   | 浮點數，0 到 1          | 沿用維度的值 | 視窗期間的 [`fogDensity`](#sky-區段)              |
| `windows[].sky.fogColor`          | 否   | 十六進位顏色或 `sample` | 沿用維度的值 | 視窗期間的 [`fogColor`](#sky-區段)                |
| `windows[].sky.sun.color`         | 否   | 十六進位顏色            | 沿用維度的值 | 視窗期間的 [`sun.color`](#天空渲染器)             |
| `windows[].sky.sun.auraColor`     | 否   | 十六進位顏色            | 沿用維度的值 | 視窗期間的 [`sun.auraColor`](#天空渲染器)         |
| `windows[].weather.precipitation` | 否   | `false`                 | 沿用維度的值 | 關閉，視窗期間雨和風暴會消退並保持停止            |
| `windows[].weather.alwaysRain`    | 否   | 布林值                  | 沿用維度的值 | 視窗期間的 [`alwaysRain`](#weather-區段)          |

視窗中的任何其他鍵都會被忽略並在日誌中報錯，在 `precipitation` 關閉的維度中使用 `precipitation: true` 或 `alwaysRain: true` 也是如此。

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
| `cycle.maxStrength`     | 否   | 浮點數，大於 0 至 1   | `0.6`                    | 陣雨最強的程度。每場陣雨會在此值的四分之一到全部之間變化。雷暴期間，降雨會無視此值，增強到滿強度                                                                   |
| `cycle.thunderTicks`    | 否   | 整數或 `[min, max]`   | 無                       | 雷暴持續的時間。沒有它，循環永遠不會有風暴                                                                                                                         |
| `cycle.calmTicks`       | 否   | 整數或 `[min, max]`   | `[12000, 180000]`        | 風暴之間的平靜期持續的時間                                                                                                                                         |
| `cycle.thunderStrength` | 否   | 浮點數，大於 0 至 1   | `1`                      | 風暴有多暗。只有高於 `0.9` 才會落雷                                                                                                                                |
| `rain.particle`         | 否   | 粒子 id               | `minecraft:rain`         | 雨滴落地處濺起的粒子                                                                                                                                               |
| `rain.sound`            | 否   | 音效名稱              | `minecraft:weather.rain` | 雨聲                                                                                                                                                               |
| `rain.volume`           | 否   | 浮點數                | `0.2`                    | 雨聲的音量，雨落在你頭頂上方時減半                                                                                                                                 |
| `rain.interval`         | 否   | 整數                  | `3`                      | 音效播放的稀疏程度；越高越稀疏，`0` 則每次機會都播放                                                                                                               |
| `rain.color`            | 否   | 十六進位顏色          | `#FFFFFF`                | 落雨的色調                                                                                                                                                         |
| `rain.snowColor`        | 否   | 十六進位顏色          | `#FFFFFF`                | 落雪的色調                                                                                                                                                         |
| `rain.colorMode`        | 否   | `multiply` 或 `solid` | `multiply`               | `rain.color` 如何與雨的紋理結合：`multiply` 會為灰藍色的雨絲染色，所以明亮的顏色會變暗；`solid` 以 `rain.color` 繪製雨絲，保留其形狀與透明度；夜間光照仍會使其變暗 |
| `rain.angle`            | 否   | 浮點數，0 到 180      | `0`                      | 偏離正下方的度數：`90` 為橫向吹，`180` 為直直向上升。繪製時最多傾斜 75 度                                                                                          |
| `rain.heading`          | 否   | 浮點數，度            | `0`                      | 吹向哪個方向：`0` 南，`90` 西，`180` 北，`270` 東                                                                                                                  |
| `rain.splashUpward`     | 否   | 布林值                | `false`                  | 開啟時，上升的雨（`angle` 超過 `90`）仍會在地面濺起並播放聲音                                                                                                      |
| `wind.gust`             | 否   | 浮點數，0 到 90       | `15`                     | 陣風在高峰時加到 `angle` 的度數，永遠不會超過水平                                                                                                                  |
| `wind.every`            | 否   | 整數或 `[min, max]`   | `[200, 600]`             | 一陣風到下一陣風之間的刻數                                                                                                                                         |
| `wind.swing`            | 否   | 浮點數，0 到 180      | `30`                     | 陣風把 `heading` 向任一側擺動的度數                                                                                                                                |

`wind` 區段讓雨帶有陣風。不時有一陣風使雨最多再多傾斜 `gust` 度，並把風向最多往一側擺動 `swing` 度，在四秒內升起並消退；陣風每隔 `every` 刻出現一次。雨與雪會隨之傾斜，維度的環境粒子也會順著雨傾斜的方向飄動，不論有沒有陣風。有 `wind` 區段但沒有 `rain` 區段時，雨使用其預設值。

其他維度共用主世界的雨。`cycle` 讓這個維度擁有自己的天氣：陣雨依上述時間來去，不論主世界在做什麼。有 `thunderTicks` 時也會有風暴，依其自己的時間；風暴遇上陣雨時，會讓陣雨達到最強、使天空變暗，並且在 `lightning` 開啟時帶來閃電。[世界範本](#世界範本)中的 `weatherCeiling` 仍然限制雨能到達多高。帶有 `precipitation: false` 的[時間視窗](#time-區段)會把降雨限制在一天的其餘時間：已經在下的陣雨會在幾秒內消退，天氣會一直等到視窗結束；帶有 `alwaysRain` 的視窗則讓雨在它所占的那段時間裡傾盆而下。

`rain` 區段會改變此處雨雪的外觀和聲音，無論有沒有 `cycle`；沒有 `cycle` 時，雨雪的外觀和聲音與原版一致。

### `ambience` 區段

*維度*

| 鍵               | 必填 | 值                  | 預設             | 作用                                                                                                                                         |
| ---------------- | ---- | ------------------- | ---------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| `music`          | 否   | 音效名稱            | 無               | 在此處取代一般曲目播放的音樂，創造模式也一樣。抵達時會截斷舊曲目                                                                             |
| `musicDelay`     | 否   | 整數或 `[min, max]` | `[12000, 24000]` | 兩首曲目之間的安靜刻數                                                                                                                       |
| `loopSound`      | 否   | 音效名稱            | 無               | 你在此處期間持續循環的音效，抵達時淡入，離開時淡出                                                                                           |
| `ambientSound`   | 否   | 音效名稱            | 無               | 不時播放的音效，即生態域的附加音效                                                                                                           |
| `soundChance`    | 否   | 0.0 到 1.0          | `0.0111`         | 每刻播放 `ambientSound` 的機率                                                                                                               |
| `particle`       | 否   | 粒子 id             | 無               | 在你周圍空氣中飄浮的粒子，例如 `minecraft:ash`、`minecraft:white_ash`、`minecraft:crimson_spore` 或 `minecraft:dust`                         |
| `particleChance` | 否   | 0.0 到 1.0          | `0.00625`        | 其密度，計算方式與現代生態域相同：每刻嘗試 16 格方塊內約 667 個位置，以及 32 格方塊內再多 667 個，每個不是完整方塊的位置，都以此機率顯示粒子 |
| `particleColor`  | 否   | 十六進位色碼        | 無               | 可著色粒子的顏色：`minecraft:dust` 和 `minecraft:entity_effect`                                                                              |

`ambience` 區段為維度提供自己的音樂、音效和飄浮粒子。這些鍵直接提供給遊戲自身的生態域效果（音樂、環境循環音、附加音效和環境粒子），所以聽起來、看起來都和生態域的一樣，「粒子」影像設定也會同樣減少它們。在這裡設定的鍵優先於該維度的所有生態域；省略的鍵保留各生態域自己的，因此帶有自身效果的原版生態域 JSON 同樣有效。

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
      "growth": { "crops": 2.0, "saplings": 2.0, "animals": 2.0, "breeding": 1.5 }
    },
    "bloodmoon": {
      "sky": { "skyColor": "#5A1010", "fogColor": "#401010" },
      "mobs": { "entities": ["#monster"], "health": 2.0, "damage": 1.5, "speed": 1.2, "equipment": { "head": { "item": "minecraft:iron_helmet", "dropChance": 0.0 }, "mainhand": { "item": "minecraft:iron_sword" } } }
    },
    "bounty": {
      "loot": { "minecraft:entities/cow": "mypack:entities/rich_cow", "minecraft:chests/simple_dungeon": "mypack:chests/bounty" },
      "trades": [
        { "profession": "minecraft:farmer", "career": "farmer", "level": 1, "buy": { "item": "minecraft:wheat", "min": 10 }, "sell": { "item": "minecraft:emerald" } }
      ]
    },
    "longnight": {
      "rules": { "dayLength": 48000, "weather": "thunder", "difficulty": "hard", "sleep": false, "weatherSpeed": 2.0, "localDifficulty": 3.0, "wakeAt": 1000 }
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

`/rdplserver environment <dimension> <name>` 把維度切換到某個環境，`default` 則恢復它自己的外觀；單獨使用 `/rdplserver environment` 會列出每個有環境的維度以及它目前使用的環境。此指令需要權限等級 2，因此函式可以執行它：[卡片規則](#卡片)的 `runs`、計分的回合或輪次、突襲或指令方塊都能這樣改變天空，可以在一天中的某個時刻、某一天、玩了一段時間之後或任何其他觸發時進行。維度以其 ID 指定，例如 `minecraft:overworld` 或 `mypack:mars`。使用中的環境會隨世界一起儲存，因此重新啟動後仍會保留，伺服器會在每位玩家加入時、以及環境改變時向所有人傳送它。[時間窗口](#time-區段)仍會疊加在它之上生效。

帶有 `biomes` 的環境只在這些生態域中生效。`/rdplserver environment <dimension> <name>` 會在整個維度所用的環境之外額外開啟它，`/rdplserver environment <dimension> <name> off` 則再次關閉它；對整個維度所用的環境使用 `off`，會讓維度恢復它自己的外觀。可以同時開啟多個，它們的生態域重疊時，最後開啟的那個優先。站在其某個生態域中的玩家會看到它，其他地方的維度看起來和之前一樣。每個使用中的環境都以同樣的方式列出、儲存和傳送。

環境還可以帶一個 `spawning` 區段，在環境開啟期間改變維度中的自然生成。其中的速率鍵與上限鍵，名稱和含義與[生成](#生成)相同，並會取代世界與生態域的值。`weights` 依倍數調整某個指定生物被選中的頻率：`10` 表示機率高出十倍，`0` 表示它不再自行生成。帶有 `biomes` 時，該區段只在這些生態域中生效；多個環境重疊的地方，由最後開啟的那個決定，與天空相同。上限和原版一樣，在整個維度範圍內統計該生物種類，但只在該區段生效的地方限制新的生成。該區段沒有寫出的鍵沿用通常的值。關閉環境會立刻結束這一切：已經生成的生物會保留。速率在世界的生成速率生效的任何地方都適用，包括生怪磚生成的生物；上限和權重只影響自然生成，生怪蛋與指令永遠不受影響。

環境還可以帶一個 `growth` 區段，在環境開啟期間加快或減慢該維度中的生長。`crops` 涵蓋農作物、莖類、可可豆、地獄疣、甘蔗、仙人掌、歌萊花，以及任何透過 NeoForge 作物事件生長的模組植物；`saplings` 涵蓋長成樹的樹苗；`animals` 涵蓋幼年動物長大的速度；`breeding` 涵蓋繁殖過的動物多快可以再次繁殖，設為 `0` 時，餵食的動物不再產生幼崽。每個鍵都是 `0` 到 `100` 的倍率：`1` 為一般速度，`0.5` 為一半，`2` 為兩倍，`0` 為停止。超過 1 時，每當遊戲選中植物進行生長，它都會獲得額外的生長機會，因此光照、土壤和水仍然有作用。骨粉、餵食和指令永遠不受影響。帶有 `biomes` 時，該區段只在這些生態域中生效；多個環境重疊的地方，由最後開啟的那個決定，與天空相同。該區段沒有寫出的鍵沿用一般速度。關閉環境會立刻恢復一般速度；已經長出的內容維持不變。

環境還可以帶一個 `mobs` 區段，在環境開啟期間改變該維度中的生物，可用於血月之類的事件。`entities` 用 id 指定要改變的生物，或用 `#monster`、`#creature`、`#ambient`、`#water_creature` 依類別指定；省略時涵蓋所有怪物。`health`、`damage` 與 `speed` 分別乘以生物的最大生命值、近戰攻擊傷害與行走速度；生物的目前生命值會隨最大值一起縮放。`equipment` 會給每個生物一件放在其空欄位中的物品，並設定它死亡時掉落的機率。生物採用哪個環境，取決於它生成或被載入時所站的位置，並且每當切換環境時，對每個已載入的生物重新判定；`biomes` 與重疊的處理方式與天空相同。只要該環境保持開啟，生物就保留這些改變，重啟後也一樣。環境關閉時，生物的生命值、傷害與速度恢復原狀，環境給予的裝備若仍在生物身上則會消失。箭、火球與爆炸的傷害不變，玩家永遠不會被改變。

環境還可以帶有 `loot` 區段，在環境開啟期間把一張戰利品表換成另一張：生物掉落、釣魚、進度獎勵，以及世界生成的箱子和運輸礦車。箱子在第一次打開時才被填充，所以由那一刻開啟的環境決定裡面的內容。把一張表對應到 `minecraft:empty`，就什麼都不會掉落。`trades` 區段按[交易](#交易)的格式列出交易；它提到的職業的村民，在環境開啟期間會提供這些交易（最高到自己的等級）來取代自己原有的交易，環境關閉後則恢復自己的交易。每個村民兩份都會保留：已完成的交易、售罄的交易和補貨，會在環境回來時從原處繼續，重新啟動之後也一樣。列出的每個職業都要給出 1 級交易，否則該職業的新村民沒有任何交易可提供。流浪商人不會被改變。`biomes` 和重疊的處理與天空相同。

環境還可以帶有 `rules` 區段，在環境開啟期間改變世界的運作方式。`dayLength` 像 [`time.dayLength`](#time-區段) 一樣拉長一天，`weather` 把天氣固定為 `clear`、`rain` 或 `thunder`，`weatherSpeed` 讓維度的天氣來去得更快或更慢，`0` 則維持現狀；沒有 `weather.cycle` 時，它會加快主世界的天氣，其他維度與主世界共用這一天氣。`difficulty` 在此處取代世界的難度，優先於 `worldDifficulty`；`localDifficulty` 設定局部難度，範圍 `0` 到 `6.75`，決定生物帶著盔甲生成或撿取物品的頻率。`sleep` 關閉後無人能睡覺，已經入睡的人會醒來；`wakeAt` 設定睡過一夜後結束的時刻，與原版跳過夜晚一樣，只在主世界移動時鐘。`sleep` 和 `localDifficulty` 遵循 `biomes` 與重疊的處理，與天空相同；其他鍵涵蓋整個維度，因此帶 `biomes` 的環境若給出這類鍵，會記入日誌並忽略該鍵。關閉環境會立即恢復一切；天氣在幾秒內逐漸恢復。

環境還可以帶一個 `raids` 區塊，在環境開啟期間改變維度中的[突襲](#突襲)。給出 `every` 後，每位玩家會統計自己在環境開啟的地方度過的刻數。計數達到 `every` 時，`start` 中列出的某個突襲會在該玩家身邊開始，就像他帶著該突襲的 `omen` 一樣：在他所處的村莊中，或者在突襲帶有 `anywhere` 時在他周圍。若突襲無法在那裡開始，計數保持滿值，時鐘每秒再試一次。任何開始的突襲都會把其 `reach` 範圍內每位玩家的計數重置為 0，因此 `every` 是兩次突襲之間的最短間隔。`waves` 和 `points` 會乘以在該環境下開始的每個突襲的波數和積分，由 `omen` 觸發的和由 `/rdplserver raid` 觸發的也包括在內。兩者都會四捨五入，突襲至少有一波；超出突襲自身清單的末尾後，它的最後一波會再次到來。突襲會保留開始時的波數和積分，即使環境之後關閉也是如此，它們會隨突襲一起儲存。有 `biomes` 時，時鐘按玩家所在位置判斷，波數和積分按突襲的中心判斷。多個環境重疊的地方，由最後開啟的那個決定。沒有環境給出 `every` 的地方計數暫停，計數隨玩家儲存，每個維度一個。

| 鍵                                                     | 必填            | 值                     | 預設           | 作用                                                                                                                                                                                                                                        |
| ------------------------------------------------------ | --------------- | ---------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `environments.<name>`                                  | 否              | 物件                   | 無             | 一個環境。名稱為一個單字，除 `default` 外皆可                                                                                                                                                                                               |
| `environments.<name>.biomes`                           | 否              | 生態域名稱清單         | 所有生態域     | 生態域 id，例如 `minecraft:desert`，或用 `#tag` 表示生態域標籤，例如 `#minecraft:is_ocean`。這樣環境只在這些生態域中生效                                                                                                                    |
| `environments.<name>.sky`                              | 否              | 物件                   | 無             | [`sky` 區段](#sky-區段)中的鍵，但世界載入時只設定一次的鍵除外：`hasSkyLight`、`surfaceWorld`、`respawn`、`respawnDimension`、`spawning`、`nether`、`beds`、`waterVaporizes`、`groundLevel`、`movementFactor`、`fixedTime` 與 `ambientLight` |
| `environments.<name>.ambience`                         | 否              | 物件                   | 無             | [`ambience` 區段](#ambience-區段)中的鍵                                                                                                                                                                                                     |
| `environments.<name>.spawning`                         | 否              | 物件                   | 無             | [生成](#生成)中的倍率和上限鍵，外加 `weights`                                                                                                                                                                                               |
| `environments.<name>.spawning.weights`                 | 否              | 實體 ID 對應倍率的物件 | 無             | 在該生物所在的每個生成清單中縮放它的權重；`0` 使它不再自然生成                                                                                                                                                                              |
| `environments.<name>.growth`                           | 否              | 物件                   | 無             | `crops`、`saplings`、`animals` 與 `breeding` 的速度                                                                                                                                                                                         |
| `environments.<name>.growth.crops`                     | 否              | 數字，0 到 100         | `1`            | 農作物生長速度；`0` 使其停止                                                                                                                                                                                                                |
| `environments.<name>.growth.saplings`                  | 否              | 數字，0 到 100         | `1`            | 樹苗生長速度；`0` 使其停止                                                                                                                                                                                                                  |
| `environments.<name>.growth.animals`                   | 否              | 數字，0 到 100         | `1`            | 幼年動物長大的速度；`0` 使其維持幼年                                                                                                                                                                                                        |
| `environments.<name>.growth.breeding`                  | 否              | 數字，0 到 100         | `1`            | 繁殖後等待時間流逝的速度；`0` 使繁殖停止                                                                                                                                                                                                    |
| `environments.<name>.mobs`                             | 否              | 物件                   | 無             | 在環境開啟期間改變生物                                                                                                                                                                                                                      |
| `environments.<name>.mobs.entities`                    | 否              | 實體 ID 或類別的清單   | `["#monster"]` | 諸如 `minecraft:zombie` 的 ID，或 `#monster`、`#creature`、`#ambient`、`#water_creature`                                                                                                                                                    |
| `environments.<name>.mobs.health`                      | 否              | 大於 0 的數字          | `1`            | 乘以最大生命值                                                                                                                                                                                                                              |
| `environments.<name>.mobs.damage`                      | 否              | 0 或更大的數字         | `1`            | 乘以近戰攻擊傷害                                                                                                                                                                                                                            |
| `environments.<name>.mobs.speed`                       | 否              | 0 或更大的數字         | `1`            | 乘以行走速度；`0` 使生物停在原地                                                                                                                                                                                                            |
| `environments.<name>.mobs.equipment`                   | 否              | 欄位對應物品的物件     | 無             | 欄位有 `mainhand`、`offhand`、`head`、`chest`、`legs`、`feet`；只會填入空欄位                                                                                                                                                               |
| `environments.<name>.mobs.equipment.<slot>.item`       | 是              | 物品 ID                | 無             | 給予的物品                                                                                                                                                                                                                                  |
| `environments.<name>.mobs.equipment.<slot>.dropChance` | 否              | 數字，0 到 1           | `0.085`        | 生物死亡時該物品掉落的機率                                                                                                                                                                                                                  |
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
    "autumnlocal": { "biomes": ["minecraft:plains"], "sky": { "grassTint": "#E0A040", "foliageTint": "#FF0000" } },
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

## 傳送門與閘門

*世界*

`<namespace>/blocks/*.json`

傳送門就是一個普通的方塊定義，所以路徑規則相同，每個變體都是一個傳送門方塊。

`portal` 方塊帶有一個 `portal` 區段：

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
    "owned": true,
    "walkIn": false
  },
  "variants": { "ruby_portal": { "hardness": -1, "light": 11 } }
}
```

| 鍵                | 必填 | 值             | 預設                  | 作用                                                     |
| ----------------- | ---- | -------------- | --------------------- | -------------------------------------------------------- |
| `dimension`       | 是   | 維度 id        |                       | 它把你送到哪裡                                           |
| `returnDimension` | 否   | 維度 id        | `minecraft:overworld` | 它把你送回哪裡                                           |
| `gate`            | 否   | 閘門名稱       | 無                    | 必須處於開啟狀態才能通過的閘門                           |
| `cooldown`        | 否   | 整數，單位為刻 | `60`                  | 同一名玩家再次使用前的等待時間                           |
| `platform`        | 否   | 布林值         | `true`                | 抵達時建造一個落腳平台                                   |
| `platformBlock`   | 否   | 方塊名稱       | 石頭                  | 該平台由什麼方塊構成                                     |
| `sound`           | 否   | 音效名稱       | 無                    | 通過時播放。參見[音效名稱](#值清單)                      |
| `owned`           | 否   | 布林值         | `true`                | 只有建造者及其允許的人可以使用。有主的傳送門同時免疫爆炸 |
| `walkIn`          | 否   | 布林值         | `false`               | 走進方塊即可傳送，就像地獄傳送門那樣。關閉時則需用手使用 |

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
| `sound`         | 否   | 音效名稱                    | 無                          | 通過時播放。參見[音效名稱](#值清單)              |
| `owned`         | 否   | 布林值                      | `false`                     | 只有點燃者及其允許的人可以使用                   |

站在洞口裡的方塊不由資源包編寫。帶有 `portal` 區段的維度會得到一個屬於自己的方塊，以遊戲自帶的傳送門材質依 `color` 繪製，走進去即可傳送而無需用手使用，並且不可破壞。顏色會與材質相乘，方式和 `tintindex` 一樣，所以 `#C77DFF` 保留地獄的紫色，而 `#4CFFB0` 會把它變成毒綠色。若想要完全不是原版材質的傳送門，請編寫你自己的普通 `portal` 方塊並配上自己的材質，需要的話可以畫成[像素圖](#以像素圖編寫的材質)，其中 `tint` 可以在兩種顏色之間漸變。

`return` 決定另一側會發生什麼。`built` 會依玩家搭建的尺寸立起同樣的框架並將其點亮，這與原版的行為一致。`player` 不會建造任何東西，但允許在那邊點亮同樣的框架，因此回家的路需要自己去找並自己搭。`none` 則完全拒絕在該維度中點亮該框架，這趟旅程只能單程。

**一個框架，多個維度。** 框架與點燃它的物品這一組合決定了維度，所以同一個 `standing_gate`，用打火石點燃與用資源包自己的點火器點燃，會開啟兩個不同的地方，各有各的顏色。兩個維度認領同一個框架*且*同一個物品是資源包的錯誤：第二個會被拒絕並在日誌中說明，而不是悄悄讓其中一個勝出。

破壞框架的任何一個方塊都會使傳送門熄滅，與原版一樣。

### 閘門

*傳送門與閘門*

`<namespace>/gates/*.json`

檔案路徑即閘門的註冊名稱，傳送門隨後在 `gate` 中引用它。

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

```json
{
  "dimension": "mypack:ruby_world",
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

| 鍵                | 必填 | 值                           | 預設                             | 作用                                                                                                                                                                                                                                                                                                                                      |
| ----------------- | ---- | ---------------------------- | -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `dimension`       | 否   | 維度 id                      | 無                               | 它守衛的維度。沒有它的閘門不守衛任何維度，只鎖住指向它的[傳送通道](#傳送通道)                                                                                                                                                                                                                                                             |
| `name`            | 否   | 字串                         | 檔案名稱                         | 顯示給玩家                                                                                                                                                                                                                                                                                                                                |
| `scope`           | 否   | `player`、`global`、`team`   | `player`                         | 一次針對一名玩家、針對整個世界，還是針對開啟者的整個隊伍（隊伍指資料包 `<namespace>/teams` 資料夾中的一方；玩家可透過列在該方的 `players` 中、使用 `/rdplserver team join <名稱>`（該方將 `joinable` 設為 false 時除外）、單獨使用 `/rdplserver team join` 加入 `balance` 中人數最少的一方，或經 `picks` 抽取加入，見 [隊伍](#誰會加入)） |
| `teamless`        | 否   | `player`、`refuse`           | `player`                         | 範圍為 `team` 時，不屬於任何隊伍的玩家如何處理：單獨為其解鎖，或拒絕通過                                                                                                                                                                                                                                                                  |
| `open`            | 否   | 布林值                       | `false`                          | 是否一開始就處於開啟狀態                                                                                                                                                                                                                                                                                                                  |
| `unlock`          | 否   | 物件                         |                                  | 什麼能開啟它。見下文                                                                                                                                                                                                                                                                                                                      |
| `unlockedMessage` | 否   | 字串                         | `%dim% is now open`              | 開啟時顯示                                                                                                                                                                                                                                                                                                                                |
| `blockedMessage`  | 否   | 字串                         | `You need %item% to enter %dim%` | 拒絕時顯示                                                                                                                                                                                                                                                                                                                                |
| `safeReturn`      | 否   | 布林值                       | `false`                          | 被擋回的玩家會被安置在他們試圖離開的那個世界中的安全位置：若床或已充能的重生錨仍在，就放在其旁邊，否則放在該世界的出生點                                                                                                                                                                                                                  |
| `requires`        | 否   | 模組 id 或資源包命名空間清單 | 無                               | 除非全部存在，否則會略過該閘門                                                                                                                                                                                                                                                                                                            |
| `portalBlocks`    | 否   | 方塊名稱清單                 | 每個傳送門                       | 將該閘門限定於這些傳送門方塊，這樣一個維度就可以同時擁有一個受守衛的傳送門和一個敞開的傳送門                                                                                                                                                                                                                                              |

`unlock` 接受 `hold`（必須持有的物品）、`consume` 搭配 `consumeCount`（`1`）、`craft`（必須已合成過的物品）、`advancement`，以及 `killed`（實體名稱，誰擊殺一隻，閘門就為誰開啟，所以頭目可以掌握通往一個世界的鑰匙）搭配 `killedCount`（`1`，一隻不夠時使用），依範圍設定按每名玩家或整個世界累計。加上 `killedDrops`（物品名稱）後，計入的擊殺會把該物品放在擊殺者腳邊，而不是開啟閘門，並且重新開始計數，所以鑰匙可以再次取得，並交給從未參與戰鬥的人；對同一個物品設定 `hold` 或 `consume` 的條件，即可讓它成為鑰匙。`%item%`、`%mob%` 和 `%dim%` 會自動填入。生物掉落的鑰匙在這裡不需要任何特殊處理：讓生物掉落該物品，再以 `hold` 或 `consume` 設定條件即可。`team`，以及 `objective` 搭配 `scoreAtLeast`，會為屬於該計分板隊伍、且在該目標中分數不低於該值的玩家開啟閘門，這與卡片的 `when` 所用的條件相同；兩者也可以單獨使用。條件在玩家穿過時檢查，符合後閘門會像其他解鎖方式一樣依其範圍開啟。範圍為 `team` 時，解鎖歸開啟者的隊伍所有：隊伍中的每個人都能通過，包括之後加入的玩家；不屬於任何隊伍的玩家開啟時，只對自己開放。`teamAndScore` 決定這兩項如何計算：`alone`（預設）時它們單獨就能開啟閘門；`with` 時它們成為附加條件，只有在滿足另一個解鎖鍵之後，且玩家同時屬於該隊伍並達到該分數，閘門才會開啟。`killedTally` 設為 `team` 時，`killedCount` 會合計擊殺者所屬隊伍的全部擊殺，而不是依每名玩家各自計算（`player`，預設）；不屬於任何隊伍的擊殺者仍單獨計數。`teamless` 設為 `refuse` 時，範圍為 `team` 的閘門會拒絕不屬於任何隊伍的玩家，而不是讓其只為自己解鎖（`player`，預設）。

閘門也守衛遊戲自帶的維度：`dimension` 為 `minecraft:the_nether` 的閘門會擋在每一個地獄傳送門前面。

**傳送通道的閘門。** [傳送通道](#傳送通道)在 `gate` 中指定一個閘門，在該閘門對玩家開啟之前，它一直保持鎖定。scope、開啟規則與訊息和傳送門相同。給同一網路的所有傳送通道填同一個閘門，就能一次鎖住整個網路。若要讓玩家在傳送通道旁消耗物品來開啟閘門，請把該傳送通道方塊列入 `portalBlocks`，並給閘門設定 `consume` 物品。這樣的閘門沒有 `dimension`，傳送通道方塊這樣指定它：`"gate": "mypack:northgate_network"`：

```json
{
  "name": "Northgate Network",
  "scope": "player",
  "unlock": { "consume": "minecraft:emerald", "consumeCount": 3 },
  "portalBlocks": ["mypack:northgate"]
}
```

`/rdplserver gate list` 對沒有維度的閘門顯示 `-`。

## 深層世界

*世界*

主世界可以比遊戲預設的更高或更深，而地形下方開闢出的空間會由它自己的生成來填充。四個 `terrain` 鍵可以做到這一點，和其餘的鍵一樣寫在世界範本的 `settings` 區段中；資源包維度則在它自己的 `terrain` 中用 `minHeight` 和 `maxHeight` 來實現同樣的效果。

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

| 鍵               | 值                                                | 預設  | 作用                                                                                                                                                                                                                                         |
| ---------------- | ------------------------------------------------- | ----- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldMinHeight` | 整數，16 的倍數，最低到 -2032                     | `-64` | 主世界最低的方塊。遊戲自帶的底部是 -64；更低就會在原版地形之下形成一個深層世界，那裡是實心的石頭，直到世界生成層把它挖開，或者 `noiseCaves` 把遊戲的洞穴延伸下去。只透過生成的預設組合套用，所以在資源包之前建立的世界會保持原有高度         |
| `worldMaxHeight` | 整數，16 的倍數，最高到 2032，且至多比底部高 4064 | `320` | 主世界頂部之上的那個方塊。遊戲自帶的頂部是 320；更高會在原版地形之上留出開闊的天空                                                                                                                                                           |
| `deepStone`      | 方塊名稱                                          | 無    | 當底部低於 -64 時，原版地形之下的世界所用的方塊，例如資源包自己的深板岩。它會在 -64 以下的八層中與深板岩漸變融合，就像深板岩與石頭融合那樣。留空則保持石頭                                                                                   |
| `noiseCaves`     | `off`、`deep` 或 `world`                          | `off` | 當底部低於 -64 時，遊戲的洞穴、隧道、麵條洞穴和含水層在何處延續：`off` 讓原版地形之下保持為實心深層石頭，交給世界生成層去挖掘；`deep` 讓它們一直延伸到底部，並把熔岩湖移到最底下的十層；`world` 在此版本上意思相同，因為原版地形本來就有它們 |

深層世界是資源包自己的世界生成項目、洞穴區域和硬度群組發揮作用的地方：項目上的 `minHeight` 和 `maxHeight` 可以一直延伸到底部。遊戲自帶的放置地物仍然侷限於原版地形：高度從世界底部算起的地物，包括遊戲的鑽石和低處的紅石，仍從 -64 算起，而會落到 -64 以下的放置會被省略，就像底部為 -64 的世界裡那樣。1.12.2 的 `deepRavines`、`oreVeins` 以及 Rubic 世界本身在這裡都沒有對應項，因為此引擎自帶的生成本來就能從底部延伸到頂部。

## 天空陸地

*世界*

當 `worldMaxHeight` 超過 320 時，原版地形上方的開闊天空可以填入它自己的陸地。`skyStone` 負責開啟，遊戲自帶的洞穴雜訊負責塑形：下方本會是洞穴的地方，在上方就是實心，於是天空聚成漂浮的島嶼。這些鍵寫在世界範本的 `settings` 區段中；`maxHeight` 超過 320 的資源包維度同樣適用。

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldMaxHeight": 1024,
    "skyStone": "mypack:cloudstone",
    "skyShape": "islands",
    "skyIslands": 0.4,
    "skyThickness": 3.0,
    "skyHeights": [400, 900],
    "skyAnimals": false
  }
}
```

| 鍵             | 值                   | 預設      | 作用                                                                                                                                                                                                      |
| -------------- | -------------------- | --------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `skyStone`     | 方塊名稱             | 無        | 當 `worldMaxHeight` 超過 320 時，原版地形上方的天空由什麼方塊構成，並由遊戲自帶的洞穴雜訊塑成漂浮的陸地，所以下方是洞穴的地方，上方就是島嶼。其上的表層與填充層來自各生態域的地表規則。留空則天空保持空白 |
| `skyShape`     | `islands` 或 `caves` | `islands` | 原版地形上方的天空被塑成什麼：`islands` 是漂浮陸地，`caves` 是被洞穴穿透的實心岩石。僅在 `skyStone` 指定了方塊時使用                                                                                      |
| `skyIslands`   | 數字，-1.0 到 1.0    | `0.5`     | 天空聚成島嶼的難易程度，從 -1（幾乎連成一片）到 1（空無一物）。越低，陸地鋪滿的天空越多；越高，碎塊越少也越小。僅在 `skyStone` 指定了方塊且 `skyShape` 為 `islands` 時使用                                |
| `skyThickness` | 數字，0.0 到 8.0     | `2.0`     | 天空島嶼有多厚實。越高，島嶼越飽滿；越低，島嶼越空，邊緣也逐漸薄到消失。僅在 `skyStone` 指定了方塊且 `skyShape` 為 `islands` 時使用                                                                       |
| `skyHeights`   | 兩個數字的清單       | 空        | 天空陸地可以到達的最低和最高方塊。留空則填滿原版地形上方的整個世界。僅在 `skyStone` 指定了方塊時使用                                                                                                      |
| `skyAnimals`   | 布林值               | `true`    | 怪物以外的生物是否會在原版地形上方的天空陸地上自然生成，尤其是天空島嶼。`false` 讓動物、蝙蝠以及其他所有非怪物生物留在下方的地面。生怪磚不受影響。屬於 `spawning` 群組的鍵                                |

`skyStone` 和 `skyShape` 也可以寫成清單：單獨的值對所有維度生效，寫成 `dimension=value` 的項目（例如 `mypack:skylands=caves`）只設定該維度。生態域檔案中的 `skyStone`、`skyIslands` 和 `skyThickness` 會改變該生態域上方的天空陸地，洞穴區域中的同名鍵則改變區域內的天空陸地，並優先於生態域的設定；洞穴區域只有在其 `maxHeight` 超過 320 的地方才會延伸到天空。天空陸地從不低於 320 開始，頂部也會與世界頂端保持距離。島嶼下方的遊戲地面地物仍會找到下方的地面，每座島嶼也會得到其生態域的植物、樹木和動物。

## 洞穴區域

*世界*

`<namespace>/caveregions/*.json`

檔案路徑即區域的名稱，世界生成項目隨後在 `caveRegions` 中引用它。那裡的單純名稱會採用該項目自己的命名空間。

在地下繪製具名區域，是遊戲洞穴生態域在資源包中的對應物。地下被劃分為圓角的單元格，寬 `caveRegionCells` 個方塊，高 `caveRegionCellsY` 個方塊，二者都是 `terrain` 鍵，每個單元格依權重抽取一個區域，也可能一個都不抽中。區域所做的一切都由種子碼確定性地得出，所以區塊之間互相一致，而無需跨邊界寫入。

### 區域檔案

*洞穴區域*

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

```json
{
  "weight": 3,
  "minHeight": -56,
  "maxHeight": 16,
  "waterLevel": 0,
  "skyStone": "minecraft:end_stone",
  "skyIslands": 0.5,
  "skyThickness": 2.0,
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
  "particleChance": 0.002,
  "requires": ["mypack"]
}
```

| 鍵                  | 值                           | 預設                                                         | 作用                                                                                                                                                                                                                                                 |
| ------------------- | ---------------------------- | ------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `weight`            | 整數                         | `1`                                                          | 此區域贏得的單元格份額。`0` 將其關閉                                                                                                                                                                                                                 |
| `minHeight`         | 整數                         | 世界底部                                                     | 區域存在的高度帶的底部                                                                                                                                                                                                                               |
| `maxHeight`         | 整數                         | `48`                                                         | 該高度帶的頂部。中心位於高度帶之外的單元格永遠不會選中此區域                                                                                                                                                                                         |
| `waterLevel`        | 整數                         | 無                                                           | 把區域內的地下水位固定在這個高度，取代那裡的含水層。它保持在海平面以下，並且至少比深層熔岩高兩個方塊                                                                                                                                                 |
| `skyStone`          | 方塊                         | 無                                                           | 區域內天空陸地所用的方塊，取代生態域和世界的 `skyStone`                                                                                                                                                                                              |
| `skyIslands`        | -1.0 到 1.0                  | 沿用生態域的                                                 | 區域內天空陸地聚成島嶼的難易程度，優先於生態域和世界的 `skyIslands`                                                                                                                                                                                  |
| `skyThickness`      | 0.0 到 8.0                   | 沿用生態域的                                                 | 區域內天空島嶼有多厚實，優先於生態域和世界的 `skyThickness`                                                                                                                                                                                          |
| `dimensions`        | 維度 id 清單                 | 全部                                                         | 區域出現在哪些維度中，包括資源包自己的維度。帶 `biome` 的區域只會在生態域依氣候放置的地方顯示它：主世界、地獄，以及繼承主世界生態域的資源包維度                                                                                                      |
| `floorCover`        | 方塊                         | 無                                                           | 取代區域內洞穴地面的頂層方塊                                                                                                                                                                                                                         |
| `floorChance`       | 0.0 到 1.0                   | `1.0`                                                        | 地面有多少會被覆蓋                                                                                                                                                                                                                                   |
| `ceilingCover`      | 方塊                         | 無                                                           | 取代區域內洞穴頂部的方塊                                                                                                                                                                                                                             |
| `ceilingChance`     | 0.0 到 1.0                   | `1.0`                                                        | 頂部有多少會被覆蓋                                                                                                                                                                                                                                   |
| `coverReplace`      | 方塊清單                     | 遊戲的基礎石頭、礦石、鵝卵石、砂岩、陶土、終界石和黑曜石方塊 | 覆蓋層可以取代什麼。列出的方塊只匹配其自身：`minecraft:stone` 不會同時覆蓋安山岩、深板岩或凝灰岩，所以要把地面可能出現的每一種石頭都列出來                                                                                                           |
| `spawns`            | 清單                         | 無                                                           | 在區域內生成的生物，與生態域的 `spawns` 接受的項目相同：`entity`、`type`（monster、creature、ambient 或 water）、`weight`（`8`），以及表示群體規模的 `min`（`1`）和 `max`（`4`）。能看到天空的位置交給生態域處理，覆蓋層也是如此                     |
| `keepDefaultSpawns` | 布林值                       | `false`                                                      | 在區域自己的清單之外保留生態域原有的生成清單。關閉時，區域的清單在區域內完全取代它                                                                                                                                                                   |
| `structures`        | 清單                         | 無                                                           | 每個區域單元格放置一次的結構，位於單元格的中心，並貼合到洞穴地面，就像遊戲為洞穴生態域提供地標那樣。項目是 `namespace:name` 範本，或者用 `{ "structure": "...", "weight": 3 }` 在多個之間選擇                                                        |
| `structureChance`   | 0.0 到 1.0                   | `1.0`                                                        | 區域的每個單元格實際得到其結構的機率                                                                                                                                                                                                                 |
| `structureLoot`     | `namespace:path`             | 無                                                           | 已放置結構內的每個箱子首次被開啟時所用的戰利品表                                                                                                                                                                                                     |
| `biome`             | 生態域名稱                   | 無                                                           | 區域在其體積內回報的生態域，以 3D 生態域的形式寫入。讓區域擁有自己的植被、草地和水的顏色、音樂與環境音，並讓原版的生成權重讀取它。上方的地表不受影響，因為只寫入區域所佔據的單元格。省略時，區域保留周圍原有的生態域，仍會鋪設它的覆蓋層、結構和生成 |
| `requires`          | 模組 id 或資源包命名空間清單 | 無                                                           | 除非全部存在，否則會略過該區域                                                                                                                                                                                                                       |
| `ambientSound`      | 音效名稱                     | 無                                                           | 不時向站在區域內的玩家播放的音效，就像遊戲的生態域加入自己的洞穴音效那樣。由伺服器僅傳送給該玩家                                                                                                                                                     |
| `soundChance`       | 0.0 到 1.0                   | `0.0111`                                                     | `ambientSound` 每刻播放的機率                                                                                                                                                                                                                        |
| `particle`          | 粒子名稱                     | 無                                                           | 在區域內玩家周圍顯示的粒子，是遊戲中自身不帶設定的粒子之一，例如 `minecraft:dripping_water`、`minecraft:happy_villager` 或 `minecraft:underwater`。`dripWater` 這樣的 1.12.2 名稱會隨其資源包一併轉換。只有區域內的空氣方塊才會顯示它                |
| `particleChance`    | 0.0 到 1.0                   | `0.00625`                                                    | 現代生態域的粒子密度：每刻玩家 16 個方塊範圍內約 667 個位置，按每 10 刻一步統計，其中這一比例的位置會顯示粒子。統計數量上限為每 10 刻 200 個位置，所以超過約 0.03 的值不會有任何差別                                                                 |

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

地下有多少保持普通，由 `caveRegionPlainWeight` 這個 `terrain` 鍵決定，預設 `4`：只有一個權重為 1 的區域時，約五分之一的單元格會得到該區域。覆蓋層作用於頂蓋之下，所以延伸到地面之上的區域永遠不會在地表顯示。覆蓋層在每個洞穴中都有效，無論是哪種生成器挖出的。

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

檔案路徑決定項目的名稱，而 `belt`、`field` 和 `vein` 形狀會以它為種子生成雜訊，所以重新命名檔案會改變它生成的內容。

描述會生成的東西。每個項目都是一個由**擴散**放置、並受允許位置篩選的**形狀**。

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

只有 `block` 是必填的；其餘都可以省略並採用預設值。一個方塊不夠時，`blocks` 會取代 `block`，下方有它自己的範例。

### 它放置什麼

*世界生成項目*

| 鍵         | 必填 | 值                   | 預設                    | 作用                                                                        |
| ---------- | ---- | -------------------- | ----------------------- | --------------------------------------------------------------------------- |
| `block`    | 是   | 方塊名稱             |                         | 放置的內容                                                                  |
| `blocks`   | 否   | 物件清單             | 無                      | 加權清單，代替單個方塊使用。見下文                                          |
| `size`     | 否   | 整數或範圍           | `8`                     | 一次嘗試放置多少個方塊，或帶半徑的形狀有多大                                |
| `attempts` | 否   | 整數或範圍           | `8`                     | 每個區塊嘗試多少次                                                          |
| `replace`  | 否   | 方塊名稱或物件的清單 | `["minecraft:stone"]`   | 它可以取代什麼。見下文                                                      |
| `adjacent` | 否   | 方塊名稱或物件的清單 | 無                      | 只有當這些之一位於緊鄰該位置的 26 個方塊之中時才放置。形式與 `replace` 相同 |
| `sparse`   | 否   | 布林值               | `false`                 | 把方塊分散開，而不是緊密堆在一起                                            |
| `shape`    | 否   | 物件                 | `{ "type": "cluster" }` | 它呈現的形態。參見[形狀](#形狀)                                             |
| `spread`   | 否   | 物件                 | `{ "type": "even" }`    | 它被放在哪裡。參見[擴散](#擴散)                                             |

### 可生成的位置

*世界生成項目*

| 鍵                       | 必填 | 值                   | 預設       | 作用                                                                                                                                                                                                                   |
| ------------------------ | ---- | -------------------- | ---------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `minHeight`              | 否   | 整數                 | `0`        | 它會放置的最低 y 座標                                                                                                                                                                                                  |
| `maxHeight`              | 否   | 整數                 | `64`       | 它會放置的最高 y 座標                                                                                                                                                                                                  |
| `dimensions`             | 否   | 維度 id 清單         | 每個維度   | 它在哪些維度中運作                                                                                                                                                                                                     |
| `dimensionsAreBlacklist` | 否   | 布林值               | `false`    | 把該清單變成需要避開的維度                                                                                                                                                                                             |
| `biomes`                 | 否   | 生態域名稱清單       | 每個生態域 | 它在哪些生態域中運作。不帶命名空間的名稱先依資源包自身的命名空間讀取，再依 `minecraft` 讀取                                                                                                                            |
| `biomeTypes`             | 否   | 生態域類型清單       | 無         | 依類型詞指定生態域，例如 `forest` 或 `nether`                                                                                                                                                                          |
| `biomesAreBlacklist`     | 否   | 布林值               | `false`    | 把這些清單變成需要避開的                                                                                                                                                                                               |
| `minTemperature`         | 否   | 小數                 | `-100.0`   | 它會生成的最冷生態域                                                                                                                                                                                                   |
| `maxTemperature`         | 否   | 小數                 | `100.0`    | 它會生成的最熱生態域                                                                                                                                                                                                   |
| `minRainfall`            | 否   | 小數                 | `-100.0`   | 它會生成的最乾燥生態域                                                                                                                                                                                                 |
| `maxRainfall`            | 否   | 小數                 | `100.0`    | 它會生成的最潮濕生態域                                                                                                                                                                                                 |
| `minDistanceFromSpawn`   | 否   | 整數，單位為格       | `0`        | 距世界出生點多遠才開始生成                                                                                                                                                                                             |
| `caveRegions`            | 否   | 區域名稱清單         | 無         | 只在這些[洞穴區域](#洞穴區域)內生成                                                                                                                                                                                    |
| `snap`                   | 否   | `floor` 或 `ceiling` | 無         | 先把每次嘗試垂直移動到最近的洞穴地面或頂部                                                                                                                                                                             |
| `snapDepth`              | 否   | 整數                 | `0`        | `snap` 隨後越過表面多遠，從地面向下、從頂部向上。`0` 停留在緊貼表面的開闊空間中，`1` 是表面方塊本身，`2` 是它後面的那個。它可以覆寫什麼仍由 `replace` 決定，所以資源包可以藉此把一種方塊分層放在地面之下而不是地面之上 |

### 地表標誌與跟隨者

*世界生成項目*

| 鍵                | 必填 | 值                                | 預設       | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ----------------- | ---- | --------------------------------- | ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `indicators`      | 否   | `block=weight` 清單               | 無         | 在已生成的礦脈上方的地表散落的方塊，讓玩家能知道地下有什麼；請依礦脈的內容挑選。`empty=weight` 會讓一個位置保持空白。沒有權重或權重小於 1 的項目會被記入日誌並略過，而且村莊或城市的街道與建築上不會留下任何指示方塊                                                                                                                                                                                                                                                                                                                                                                          |
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
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "minecraft:oak_log", "weight": 20, "properties": { "axis": "x" } }
  ]
}
```

| 鍵           | 必填 | 值               | 預設 | 作用                                                 |
| ------------ | ---- | ---------------- | ---- | ---------------------------------------------------- |
| `block`      | 是   | 方塊名稱         |      | 放置的內容                                           |
| `weight`     | 否   | 整數             | `1`  | 這一個相對於其他方塊被選中的頻率                     |
| `properties` | 否   | 屬性對應值的物件 | 無   | 依名稱指定的方塊狀態屬性，用於方塊預設狀態以外的狀態 |

即使使用了 `blocks`，檔案頂層仍然需要 `block`；把第一個項目放在那裡就很合適。

### 取代目標

*世界生成項目*

`replace` 是一個清單，每個項目採用兩種形式之一。

```json
{
  "replace": [
    "minecraft:stone",
    { "block": "minecraft:oak_log", "properties": { "axis": "y" } }
  ]
}
```

| 形式 | 範例                                                              | 匹配內容           |
| ---- | ----------------------------------------------------------------- | ------------------ |
| 名稱 | `"minecraft:stone"`                                               | 該方塊的每一種狀態 |
| 物件 | `{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }` | 僅該狀態           |

末尾帶有中繼資料的 1.12.2 名稱 `minecraft:stone:3` 會匹配該方塊的每一種狀態，並在日誌中說明，因為帶中繼資料的那些方塊現在已是各自獨立的方塊：請寫 `minecraft:diorite`。使用 `"minecraft:air"` 可在開闊空間中生成。

### 相鄰方塊

*世界生成項目*

`adjacent` 接受與 `replace` 相同的形式，並在其基礎上增加第二個條件：只有當緊鄰該位置的 26 個方塊（面、稜和角）中至少有一個匹配該清單時，才會使用這個位置。省略時不做任何檢查。

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

所有的鍵一次全部列出。實際的檔案只需寫出需要的那些。標示為適用於某一類型的鍵，只有該類型才會讀取。

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
    "middle": "minecraft:calcite",
    "budding": "minecraft:budding_amethyst",
    "buddingChance": 0.083,
    "crystal": "minecraft:amethyst_cluster",
    "crystalChance": 0.35,
    "crack": 0.2,
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
    "richAt": 0.88,
    "poorAt": 0.4,
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

| 鍵              | 適用於                                         | 值                           | 預設                           | 作用                                                                                                    |
| --------------- | ---------------------------------------------- | ---------------------------- | ------------------------------ | ------------------------------------------------------------------------------------------------------- |
| `type`          | 全部                                           | 上述形狀之一                 | `cluster`                      | 哪種形狀                                                                                                |
| `radius`        | plate, geode, basin, spire, nodule, vent, belt | 整數或範圍                   | `6`，belt 為 `32`              | 它有多寬                                                                                                |
| `height`        | plate, geode, basin, spire, vent, tree         | 整數或範圍                   | `1`，geode 為 `8`，tree 為 `5` | 它有多高或多厚                                                                                          |
| `width`         | geode                                          | 整數或範圍                   | `12`                           | 腔體的整體跨度                                                                                          |
| `plane`         | plate, basin, spire, vent                      | `circle`, `square`           | `circle`                       | 它的佔地形狀                                                                                            |
| `slim`          | plate, largevein, nodule                       | 布林值                       | `false`                        | plate：薄一層。largevein：單方塊分支。nodule：空心外殼                                                  |
| `hanging`       | spire, vent                                    | 布林值                       | `false`                        | 從頂部向下生長，而不是從地面向上生長                                                                    |
| `taper`         | spire                                          | `straight`, `bell`, `needle` | `straight`                     | 寬度朝尖端如何收窄。`straight` 均勻收窄，`bell` 下方保持寬度然後驟降，`needle` 立刻變細成一根長長的尖端 |
| `outline`       | geode                                          | 方塊名稱                     | 無                             | 外殼方塊                                                                                                |
| `fill`          | geode                                          | 方塊名稱                     | 無                             | 中間填充什麼。省略時，中間是空心的                                                                      |
| `middle`        | geode                                          | 方塊名稱                     | 無                             | 位於主體與 `outline` 之間的一層殼，相當於遊戲紫水晶晶洞的方解石                                         |
| `budding`       | geode                                          | 方塊名稱                     | 無                             | 替換面向中空部分的主體方塊，就像紫水晶母岩那樣。需要 `fill`                                             |
| `buddingChance` | geode                                          | 0.0 到 1.0                   | `0.083`                        | 這些主體方塊中有多少會變成母岩                                                                          |
| `crystal`       | geode                                          | 方塊名稱                     | 無                             | 長在 `budding` 方塊旁邊的空腔中，就像紫水晶簇那樣                                                       |
| `crystalChance` | geode                                          | 0.0 到 1.0                   | `0.35`                         | 這些位置中有多少會長出晶體                                                                              |
| `crack`         | geode                                          | 0.0 到 1.0                   | `0`                            | 晶洞被裂開的機率：從中間向外穿過每一層、位於一側的一條管道，以 `fill` 填充。遊戲的紫水晶晶洞使用 `0.95` |

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

| 鍵       | 適用於 | 值       | 預設    | 作用           |
| -------- | ------ | -------- | ------- | -------------- |
| `log`    | tree   | 方塊名稱 | 無      | 樹幹方塊       |
| `leaves` | tree   | 方塊名稱 | 無      | 樹葉方塊       |
| `vines`  | tree   | 布林值   | `false` | 從樹葉垂掛藤蔓 |

沒有 `log` 或 `leaves` 的 `tree` 什麼也不會生成，並在日誌中說明。指定 `structure`，或在 `structures` 下指定數個，會在每個位置種下該範本，而不是長出一棵樹，此時就不需要 `log` 或 `leaves`；使用範本的樹所讀取的 `turns`、`mirrors`、`integrity`、`lootTable` 和 `locateAs` 與 `imprint` 完全相同。

### 放置範本

*形狀*

| 鍵                 | 適用於        | 值                                        | 預設  | 作用                                                                                                                                                                                        |
| ------------------ | ------------- | ----------------------------------------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structure`        | imprint、tree | `namespace:name`                          | 無    | 要放置的範本                                                                                                                                                                                |
| `integrity`        | imprint、tree | 1 到 100                                  | `100` | 範本的方塊實際出現的百分比                                                                                                                                                                  |
| `lootTable`        | imprint、tree | `namespace:path`                          | 無    | 已放置範本內每個箱子第一次被開啟時所填入戰利品的戰利品表，其他任何接受戰利品表的容器也一樣，包括界伏盒或模組的板條箱。涵蓋 `structure` 和 `structures` 的每個項目；每個箱子擲出自己的種子碼 |
| `structures`       | imprint、tree | 清單                                      | 無    | 可在其中選擇的數個範本，每次放置一個。每個項目是 `{ "structure": "namespace:name", "weight": 3 }`，或是機率均等的單純名稱。會覆寫 `structure`                                               |
| `turns`            | imprint、tree | 清單                                      | 任意  | 它可以以哪個方向放置：`none`、`quarter`、`half`、`threequarter`。項目可以帶有 `weight`。省略時，四種的機率相等                                                                              |
| `turns[].turn`     | imprint, tree | `none`, `quarter`, `half`, `threequarter` | 無    | `turns` 的物件項目所指定的旋轉，如 `{ "turn": "half", "weight": 2 }`                                                                                                                        |
| `mirrors[].mirror` | imprint, tree | `none`, `leftright`, `frontback`          | 無    | `mirrors` 的物件項目所指定的翻轉，如 `{ "mirror": "leftright", "weight": 2 }`                                                                                                               |
| `mirrors`          | imprint, tree | 清單                                      | 無    | 同時翻轉：`none`、`leftright`、`frontback`，可帶 `weight`。要為項目指定自己的權重，寫作 `{ "mirror": "leftright", "weight": 2 }`，`turns` 的項目同理，只是改用 `turn`                       |
| `at`               | imprint       | 兩個整數，x 和 z                          | 無    | 在該區塊生成時，在地表的這些方塊座標上剛好放置一次，而不是依機率。見[位於精確位置的結構](#位於精確位置的結構)                                                                               |
| `locateAs`         | imprint, tree | 字串                                      | 無    | 把此項目放置的每個結構以該名稱註冊，讓 `/rdplserver locate <name>` 能找到最近的一個。參見[尋找已放置的結構](#尋找已放置的結構)                                                              |

對於沒有內建類型涵蓋的形狀，`imprint` 就是辦法：把它建成 `.nbt` 範本再放置，用 `structures` 加以變化，用 `turns` 和 `mirrors` 加以轉向，並用 `integrity` 把它溶解成比你畫的檔案更粗糙的東西。

### 位於精確位置的結構

*形狀*

原版結構透過 `terrain` 設定中的 `structureAt` 固定到精確位置，寫成 `structure=x,z` 項目，每行一個：`"structureAt": ["villages=1000,-500"]`。**其中的 x 和 z 是方塊座標，不是區塊座標**，結構會在包含該方塊的區塊中生成。在 `terrainAdaptation` 把村莊鋪成城市街道的情況下，被固定的村莊的水井就位於該方塊本身；當該方塊離街區邊緣只有幾個方塊遠時，則位於街區允許的最近處；其他結構，以及沒有啟用它而鋪設的村莊，則從遊戲在該區塊中本會讓它們開始的地方開始。每個想要的實例寫一個項目。它的間距、間隔、最小生成距離和平地檢查全部不再起作用，所以位置由資源包自己負責，兩個相距不到一個區塊的固定位置會在同一個區塊中放入兩個結構。結構一旦建立，就依一般規則貼合其區塊處的地面。

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAt": ["villages=1000,-500"]
  }
}
```

| 設定          | 類型                         | 預設 | 作用                                                                                                                                                        |
| ------------- | ---------------------------- | ---- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `structureAt` | 清單，內容為 `structure=x,z` | 無   | 將原版結構固定在精確的位置，每個想要的實例一個項目。x 和 z 是方塊座標，結構會在包含該方塊的區塊中生成；它的間距、分隔、最小出生點距離和平地檢查全都不再適用 |

`imprint` 項目以同樣的方式固定，在其形狀中寫 `"at": [x, z]`，在該區塊生成時於地表這些座標處精確放置一次，而不是依機率放置。它可以與 `locateAs` 組合，所以被固定的結構也能被找到。

### 尋找已放置的結構

*形狀*

帶有 `"locateAs": "Crypt"` 的 `imprint` 項目會把它放置的每個結構以該名稱註冊，之後 `/rdplserver locate Crypt` 會指向最近的一個，而且該名稱會出現在 Tab 補全中；`/rdplserver goto Crypt` 會把你帶到那裡。只有已經生成的結構才能被找到，因為資源包結構是在區塊生成時依機率放置的，而不是依遊戲可以預測的網格。這些名稱保存在世界存檔中，所以重新啟動後仍然有效，在伺服器上也能用。以這種方式註冊的名稱還可以用 `gotoPlaceLevels` 賦予它自己的權限，所以資源包可以把誰能被傳送到它自己的結構，與原版結構分開決定。

### 礦場與礦脈的鍵

*形狀*

| 鍵          | 適用於      | 值                            | 預設                    | 作用                                                                                                                                                                |
| ----------- | ----------- | ----------------------------- | ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `field`     | field       | 物件                          | `{ "type": "speckle" }` | 場如何計算。與硬度組的 `field` 鍵相同，詳見[分佈場](#分佈場)：`speckle` 搭配 `chances` 和 `spread`，或 `seeded` 搭配 `cell`、`seeds`、`reach`、`arms` 和 `armReach` |
| `threshold` | field、vein | 0.0 到 1.0                    | `0.5`（vein 為 `0.4`）  | 場在一個方塊上必須多強才會放置。越低，填得越多                                                                                                                      |
| `fade`      | field       | 整數                          | `0`                     | 讓高度帶的頂部逐漸稀疏，而不是平整地結束：在高度範圍頂部的這麼多格內，每個方塊被放置的機率逐步變小，與引擎在 `deepStone` 與上方世界相接處所呈現的外觀相同           |
| `pattern`   | vein        | `default`、`banded` 或 `tube` | `default`               | 礦床的外觀：扭曲的團塊、每隔幾格堆疊的層，或在岩石中蜿蜒的中空管道                                                                                                  |
| `density`   | vein        | 0.0 到 1.0                    | `1.0`                   | 符合條件的方塊中實際被放置的比例，逐方塊擲硬幣                                                                                                                      |
| `rich`      | vein        | 方塊名稱                      | 無                      | 從 `richAt` 起，在場強高於 `threshold` 的範圍內放置，即礦床的核心，取代項目的方塊                                                                                   |
| `poor`      | vein        | 方塊名稱                      | 無                      | 放置在該範圍的最低五分之二，也就是外圍，取代項目的方塊；中間是項目自己的方塊。省略任一層，就在那裡放置項目的方塊                                                    |
| `richAt`    | vein        | 0.0 到 1.0                    | `0.88`                  | 富礦層在該範圍中的起點：`0.88` 讓富礦方塊只限於礦床最強的八分之一，數字越低，富礦核心越肥厚，`1.0` 則完全沒有富礦方塊                                               |
| `poorAt`    | vein        | 0.0 到 1.0                    | `0.4`                   | 項目自己的方塊開始的位置：低於此值就放置 `poor` 方塊，所以 `0.4` 給出最低五分之二的外圍，`0.0` 則沒有貧礦外圍。會限制在 `richAt` 以內                               |

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

### 帶狀區域

*形狀*

`belt` 是一個比單一區塊大得多的球體，用於石頭區域而不是礦脈。它的 `radius` 是球體的大小（預設 32），每個區塊都根據世界種子碼和項目自己的名稱，各自算出附近的球體從哪裡開始，所以無論區塊以何種方式生成，帶狀區域都會完整出現，而且絕不會向相鄰區塊寫入任何內容。

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

## 擴散

*生成世界*

帶有 `type` 的 `spread` 區塊。

所有的鍵一次全部列出。實際的檔案只需寫出需要的那些。標示為適用於某一類型的鍵，只有該類型才會讀取。

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

| 鍵                  | 適用於   | 值           | 預設           | 作用                                                                                                                                                                                    |
| ------------------- | -------- | ------------ | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`              | 全部     | 上述擴散之一 | `even`         | 哪一種擴散                                                                                                                                                                              |
| `center`            | centered | 整數         | 高度範圍的中點 | 它聚集在哪個高度周圍                                                                                                                                                                    |
| `range`             | centered | 整數         | 高度範圍的一半 | 它離該高度多遠                                                                                                                                                                          |
| `smoothness`        | centered | 1 到 8       | `2`            | 把 `range` 的多少次擲骰相加。分布帶從 `center` 向外延伸約 `range` 乘以此數的一半，所以每多一次擲骰它就更寬，其中更多礦脈聚集在 `center` 附近。預設值 `2` 時，分布帶向兩側各延伸 `range` |
| `veinHeight`        | sprawl   | 整數         | 高度範圍       | 一條礦脈有多高                                                                                                                                                                          |
| `veinDiameter`      | sprawl   | 整數         | `12`           | 一條礦脈有多寬                                                                                                                                                                          |
| `verticalDensity`   | sprawl   | 1 到 100     | `16`           | 垂直方向有多實心                                                                                                                                                                        |
| `horizontalDensity` | sprawl   | 1 到 100     | `32`           | 水平方向有多實心                                                                                                                                                                        |
| `offsetMin`         | terrain  | 整數         | `0`            | 距離地表的最低偏移                                                                                                                                                                      |
| `offsetMax`         | terrain  | 整數         | `offsetMin`    | 距離地表的最高偏移                                                                                                                                                                      |
| `ceiling`           | cavern   | 布林值       | `false`        | 附著在洞穴頂部，而不是地面                                                                                                                                                              |

## 結構地圖

*生成世界*

結構地圖把多個範本在網格上組合成一座有名稱的完整建築，遠遠超出單一 `.nbt` 檔案 48 格方塊的上限。每一層都以若干行單一字元繪製，一個字元對應一個儲存格，而且每一層疊放在前一層之上，高度為一個儲存格。最多 8 層，每層 8 乘 8 個儲存格；在預設儲存格大小 32 下，每邊為 256 格方塊。

`<namespace>/structuremaps/*.json`

```json
{
  "cell": 32,
  "ground": 0,
  "spacing": 64,
  "chance": 25,
  "dimensions": ["minecraft:overworld"],
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

```json
{
  "cell": 32,
  "at": [1000, -500],
  "layers": [
    { "palette": { "a": "mypack:keep_base" }, "map": ["a"] }
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
| `cell`       | 數字             | `32`  | 網格間距，以方塊計，最大 48。小於儲存格的範本放在儲存格的角落，因此滿尺寸的構件可以無縫拼接                                                                                                                                           |
| `ground`     | 數字             | `0`   | 哪一層以地表為底。位於其前的層向下挖掘，建築的地下室就是這樣來的                                                                                                                                                                      |
| `at`         | 兩個數字         | 無    | 將一份副本固定在精確的方塊座標上，方式與 `structureAt` 固定村莊相同                                                                                                                                                                   |
| `spacing`    | 數字             | `0`   | 在相隔這麼多區塊的網格上散佈副本，並依據世界種子碼加以抖動。`0` 表示不散佈，因此只有 `at` 的地圖恰好只會建造一次                                                                                                                      |
| `chance`     | 數字             | `100` | 網格點中會建造副本的百分比                                                                                                                                                                                                            |
| `depth`      | 數字，或兩個數字 | 無    | 讓建築沉入地形之下：`ground` 層的地板位於取樣地表以下這麼多格，而不是與地表齊平。兩個數字分別是最小值和最大值，每個副本依世界種子各自抽取。適用於地圖散布或以 `at` 固定的副本；由村莊或城市放置的地圖仍把其 `ground` 層的地板放在地表 |
| `spawners`   | 實體 id 清單     | 無    | 地圖範本中每個儲存時沒有生物、或只有豬的生成器所生成的生物。項目可以帶權重，如 `id=2`；儲存了豬以外其他生物的生成器會保留它。沒有任何內容註冊的 id（開啟 `vanillaClients` 時資源包的實體變體）不參與抽取                              |
| `loot`       | 戰利品表 id      | 無    | 地圖範本中每個儲存時既沒有戰利品表也沒有物品的箱子和陷阱箱所使用的戰利品表；儲存時帶有其中任一項的箱子維持不變                                                                                                                        |
| `dimensions` | 維度 id 清單     | 全部  | 地圖可在哪些維度建造，包括資源包自己的維度                                                                                                                                                                                            |
| `layers`     | 清單             | 無    | 各層，由下而上，每層由一個 `palette` 和一個 `map` 組成                                                                                                                                                                                |

調色盤以資源包 `<namespace>/structures/` 中的註冊鍵來指名範本。

| 值                                          | 作用                                                                                                                    |
| ------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `"a": "mypack:keep"`                        | 該層的每個 `a` 儲存格都放置這個範本                                                                                     |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | 每個 `a` 儲存格依權重從清單中擲出，根據世界種子碼和儲存格的位置，所以建築的兩份複本會不同，但同一個世界永遠建造出同一座 |
| `.`                                         | 空的儲存格，不放置任何東西                                                                                              |

每份副本會依據世界種子碼從四個朝向中抽取一個，整座建築連同其中的範本一起轉動，因此跨儲存格相接的牆體依然相接；地圖只會旋轉，絕不會鏡像。地面層以建築中央下方取樣到的地表為底，整張地圖共用這一個高度。對遊戲而言，散佈的地圖本身就是一個結構，透過為你寫好的結構集放置，因此每個區塊只建造自己所佔的那一片網格，跨越許多區塊的建築無需連鎖生成即可出現，與區塊的載入順序無關。類型為 `template` 的[村莊地塊](#村莊地塊)也可以把地圖指定為它的 `structure`，這樣複合結構就成為一座城市建築。

地圖會切入地形：範本包含的每個方塊（包括空氣）都會取代原位置上的方塊，而空的儲存格或較小範本周圍的空間則維持實心，因此要把每個房間的空氣寫進範本，並讓豎井的儲存格一直延伸到地表。

## 村莊地塊

*生成世界*

`<namespace>/villages/*.json`

檔案路徑即地塊的名稱，`villagePieces` 隨後可以指名它來保留或捨棄它。

此處的檔案會新增一種資源包的城市和村莊可以建造的構件。透過 `type` 選擇，共有兩種。

所有的鍵一次全部列出。實際的檔案只需寫出需要的那些。標示為適用於某一類型的鍵，只有該類型才會讀取。

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

### 每個地塊

*村莊地塊*

| 鍵           | 適用於 | 值                           | 預設             | 作用                                                                                                                                                                                                                                           |
| ------------ | ------ | ---------------------------- | ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`       | 全部   | `farm` 或 `template`         | `farm`           | 地塊的種類                                                                                                                                                                                                                                     |
| `weight`     | 全部   | 整數                         | `3`              | 與資源包中其他地塊相比，這個地塊被選中的頻率                                                                                                                                                                                                   |
| `leastCount` | 全部   | 整數                         | `1`              | 每個街區的最少數量：街區沿街道安置地塊，數量上限在其可建造地塊中最低的 `leastCount` 與最高的 `mostCount` 之間隨機抽取，兩者在城市成長期間均加上 16，或加上 `villagePlotsLeast` 的三十二分之一（若該值更大）。位於其他地塊後方的地塊不計入      |
| `mostCount`  | 全部   | 整數                         | `4`              | 該隨機範圍的上限                                                                                                                                                                                                                               |
| `width`      | 全部   | 整數                         | `7`              | 沿街道方向的寬度                                                                                                                                                                                                                               |
| `height`     | 全部   | 整數                         | `4`              | 在地面以上清出的高度                                                                                                                                                                                                                           |
| `depth`      | 全部   | 整數                         | `9`              | 遠離街道方向的進深                                                                                                                                                                                                                             |
| `apron`      | 全部   | 整數                         | `2`              | 地塊下方的地面與街道標高相差多少仍可接受，超出則被拒絕或沿街道滑動：即其下的填土或其上隆起處削平的方塊數，且最高角與最低角之間的差也不超過此值。丘陵中的寬地塊需要更大的值。設得過高，地塊會徑直在斜坡上形成梯田，在不合適的地方會把一座山削平 |
| `ground`     | 全部   | 方塊名稱                     | `minecraft:dirt` | 在斜坡上墊在下方的材料                                                                                                                                                                                                                         |
| `requires`   | 全部   | 模組 id 或資源包命名空間清單 | 無               | 除非全部存在，否則該地塊會被排除                                                                                                                                                                                                               |

地塊是資源包自己的城市沿街道建造的內容，而且每個範本地塊還會作為房屋之一加入遊戲自帶的村莊，入口位於其正面中央。若完全沒有地塊檔案，城市會依各街區所在生態域的村莊類型，建造遊戲自帶的村莊房屋。一旦某條街道要求一個地塊，就由 `weight` 決定選中你的哪一個；`villages` 設定中的 `villagePieces` 則以 `mypack:smithy`、`smithy` 或該地塊所建造的結構來指明範本保留哪些地塊。街道本身如何鋪設、裝飾、架橋、開鑿隧道和鋪設鐵軌，由[各群組的作用](#各群組的作用)下的 `village*` 設定決定。

### 農場

*村莊地塊*

`farm` 是一塊用描述而非程式碼定義的田地：一個依你要求的大小劃出的地塊，以某種方塊作邊框，內部填滿以水渠隔開的土壤行，並依方塊從你的清單中挑選作物種下。

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

| 鍵         | 適用於 | 值           | 預設                 | 作用                               |
| ---------- | ------ | ------------ | -------------------- | ---------------------------------- |
| `crops`    | farm   | 方塊名稱清單 | 小麥                 | 每個方塊種一株，處於隨機的生長階段 |
| `edge`     | farm   | 方塊名稱     | `minecraft:oak_log`  | 地塊四周的邊框                     |
| `soil`     | farm   | 方塊名稱     | `minecraft:farmland` | 各列由什麼構成                     |
| `water`    | farm   | 布林值       | `true`               | 在各列之間放一條水渠               |
| `rowWidth` | farm   | 整數         | `2`                  | 每列土壤有多寬                     |

### 由範本建構

*村莊地塊*

`template` 則改為放置你的一個 `.nbt` 結構，並轉向面對街道。

```json
{
  "type": "template",
  "weight": 2,
  "width": 9,
  "height": 6,
  "depth": 9,
  "structure": "mypack:blacksmith_shed",
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

城市地圖在網格上繪製一座城市的街道規劃，一個字元對應一個儲存格，城市依據這幅繪圖佈局，而不是隨機生成。街道、廣場和地塊與隨機城市所用的構件完全相同，因此所有街道選項、橋梁、隧道、地鐵、下水道、路燈和廣場中心裝飾物都原樣適用。世界範本在 `villageLayout` 中指定地圖。

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

| 設定       | 類型 | 預設 | 作用                                                                                                                                                   |
| ---------- | ---- | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `cell`     | 數字 | `48` | 網格間距，以方塊計，8 到 128。街道沿儲存格中線延伸，寬度為資源包的街道寬度，地塊則位於各自儲存格的中央，因此儲存格需要容納最寬的地塊，並留出臨街的餘地 |
| `palette`  | 物件 | 無   | 每個字元鋪設什麼，見下表                                                                                                                               |
| `map`      | 清單 | 無   | 各行，最多 64 乘 64 個儲存格。比最寬的行短的行，在其末尾之後視為開放                                                                                   |
| `settings` | 物件 | 無   | 僅用於這張地圖的城市設定，沿用世界範本所用的名稱，例如 `villagePathCenterBlock`。它們優先於範本中的設定，而生態域自己的設定仍優先於它們                |

| 值                                                                      | 作用                                                                                                                                                                                                                                                                                                                                                               |
| ----------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `"#": "street"`                                                         | 沿某一行或某一列連續的街道儲存格會成為一條符合資源包寬度的街道。行方向與列方向的街道相交處，路口與其他路口一樣鋪設。在兩個方向上都沒有連續段的孤立街道儲存格，會沿行方向鋪成一小段短支路                                                                                                                                                                           |
| `"+": "plaza"`                                                          | 帶有中心裝飾物的廣場。連續段會穿過廣場儲存格，因此街道在廣場處相會；位於交叉口的廣場會像環島一樣，在十字路口中央立起其 `villageWellStructure` 中心裝飾物。檔案中的第一個廣場就是城市自己的中心，它把地圖固定到城市奠基的位置；沒有廣場的地圖則以該處為中心                                                                                                         |
| `"a": "alley"`                                                          | 一條狹窄的通道。建築面向它，但它不連接任何東西，仍依小巷規則處理                                                                                                                                                                                                                                                                                                   |
| `"J": "junction"`                                                       | 向兩個方向都鋪設的街道儲存格，因此即使繪圖中只有一個方向穿過，那裡也會出現一個十字路口。穿過它的支臂只有一個儲存格長                                                                                                                                                                                                                                               |
| `"b": "bulb"`                                                           | 以環形盡頭路收尾的街道儲存格。地圖中一旦有了環形儲存格，只有位於環形儲存格內的街道末端才會得到環形盡頭路；沒有任何環形儲存格的地圖，則依據世界種子碼，讓四個死巷中的三個成為環形盡頭路。環形盡頭路只會安置在其範圍內沒有地塊、其他街道、鐵路或廣場中心裝飾物的位置：它會縮小以適應空間，最小可至比街道略寬一點，若在任何尺寸下都沒有空間，該末端就保持為普通的盡頭 |
| `"E": { "kind": "elevated", "height": 8 }`                              | 抬升到橋面上的街道儲存格，橋面在其相連的抬升儲存格路段下方最高地面之上 `height` 格方塊處，範圍 2 到 64，兩端各有一條每行升高一格的坡道。路段內部的街道交叉口隨之升起，沿途的地塊仍留在地面上。若橋面或坡道會觸及鐵路或廣場中心裝飾物，該路段保持在地面高度，並在日誌中留下一行記錄。任何值都可以像這樣寫成物件，由 `kind` 指明是哪個詞                             |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | 使用自己的一組街道鍵鋪設和鋪面的街道，這些鍵優先於地圖和範本的設定。它的寬度取決於自己的 `villagePathExtraWidth`、`villagePathSidewalkWidth` 和線條，其路面、線條和人行道取決於自己的方塊鍵，因此大道或小巷都可以用專屬的標記繪製。一段連續街道採用其中第一個設定了任何鍵的儲存格的鍵。無論多寬多窄，繪製出的街道始終是街道：絕不會被當作小巷                      |
| `"T": "mypack:tower"`                                                   | 一個地塊儲存格，依該地塊定義鋪設，位於儲存格中央並面向最近的街道                                                                                                                                                                                                                                                                                                   |
| `"T": ["mypack:a=3", "mypack:b=1"]`                                     | 同上，但依據世界種子碼和儲存格的位置依權重抽取，因此同一個世界在那裡總是鋪設相同的地塊                                                                                                                                                                                                                                                                             |
| `"g": "grow"`                                                           | 交給隨機生成的佈局處理，它會填充這類儲存格，並從地圖向外擴展                                                                                                                                                                                                                                                                                                       |
| `.` 或 `open`                                                           | 開放的地面，不鋪設任何東西                                                                                                                                                                                                                                                                                                                                         |

每張地圖會依據世界種子碼從四個朝向中抽取一個並整體轉動，因此規劃圖從任何一側看都一樣。街道最先鋪設，所以若地塊會與街道或另一個地塊重疊，該地塊會保持開放並在日誌中留下一行記錄；若某個地塊名稱沒有任何資源包提供，其儲存格同樣保持開放。地圖不會改變構件的裝飾方式：街道鍵、`villageBlocks`、路燈和廣場中心裝飾物，其表現都與隨機城市一致。

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

| 設定                  | 類型                 | 預設    | 作用                                                                                                   |
| --------------------- | -------------------- | ------- | ------------------------------------------------------------------------------------------------------ |
| `terrainAdaptation`   | 布林值               | `false` | 鋪設 RDPL 自己的城市街道。連綿城市需要開啟它                                                           |
| `villageCitySpacing`  | 整數，0 到 256       | `16`    | `1` 讓每個街區都成為一座城鎮，城市正是因此而連綿不斷。更大的數值會排定彼此分開、在各自邊界處終止的城市 |
| `villageBlockSizes`   | `size=weight` 的清單 | 空      | 平行街道之間的街廓有多深，每個街區抽取一次，因此城市會混合細密與稀疏的街網                             |
| `villagePlotsMost`    | 整數，0 或更大       | `0`     | 一個街區最多安置多少個地塊。0 表示不設上限                                                             |
| `villagePlotsBackRow` | 布林值               | `true`  | 在每個正對街道的地塊後方再安置一個地塊，使每個街廓的內部也被蓋滿                                       |

其他所有街道設定，從路面方塊到路燈、橋梁、隧道和下水道，裝點連綿城市的方式與其他城市相同。`villagePlotsLeast` 不起作用，因為每個街區本身已是一座完整的城鎮。規劃中地塊和水井合計不超過兩個的街區會留空，與任何這麼小的城市一樣。

**街道如何相接。** 每個街區都把它的主十字路口，即在水井處交叉的兩條街道，鋪在街區內的同一位置。因此相鄰街區的十字街道會連成筆直的大道，貫穿整個世界，而從每個廣場伸出的街道會與下一個街區的街道相接。無論鄰居如何佈局，街區的水井都不會移動。

**代價。** 連綿城市的每個區塊都是建成區，因此新地形在任何地方的開銷都與大城市中心相當：放置建築並為它們計算光照。快速飛行的玩家會超過生成速度，在新街區生成期間刻速率會下降。用 `pregenOnNewWorld` 或 `/rdplserver pregen` 預先生成玩家起步的區域，並保持適中的視距，可以控制住這一點，但在玩家探索新地形期間，TPS 仍會低於一般世界。存檔會隨探索過的地形增長，與任何建成區一樣。遊戲在記憶體中最多保留 1024 個街區規劃，再次需要已釋放的規劃時，會依據種子碼重新生成，與之前完全相同。

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

| 設定                  | 類型   | 預設    | 作用                                                                                                                                                                                           |
| --------------------- | ------ | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `retrogen`            | 布林值 | `false` | 為每個標記了 `"retrogen": true` 的世界生成項目，補上在該項目出現之前儲存的區塊。關閉時，已存在的區塊保持不變。無論開關與否，區塊在生成時都會被標記，因此之後再開啟時只會處理比資源包更舊的區塊 |
| `adoptExistingChunks` | 布林值 | `false` | 首次遇到舊區塊時的處理方式：開啟時，該區塊會被蓋章，視作已由這個資源包生成過，永遠不會補生成；關閉時，它與其他區塊一樣被補生成。要填充一個既有的世界，請開啟 `retrogen` 並關閉此項             |

帶有 `"retrogen": true` 的條目會生成到你加入它之前就已儲存的區塊中。每個區塊都會記錄它已經歷過什麼，所以不會重複做任何事。

項目上的旗標只表示該項目有資格參與。補生成由 `retrogen` 設定開啟，資源包可以在其 `settings` 區段中設定，玩家也可以在設定檔中設定，預設為關閉。與之配合，`adoptExistingChunks` 決定首次遇到舊區塊時發生什麼：開啟時，該區塊會被蓋章，視作已由這個資源包生成過，永遠不會補生成；關閉時，它與其他區塊一樣被補生成。在 `adoptExistingChunks` 同樣開啟時開啟 `retrogen` 不會有任何效果，因為每個舊區塊在排入佇列之前就已被註銷。要填充一個既有的世界，請同時開啟 `retrogen` 並關閉 `adoptExistingChunks`。設定檔中的 `retrogenChunksPerTick` 預設為 `2`，表示每刻補生成多少個舊區塊。

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

## 預先生成

*生成世界*

提前生成世界的陸地，這樣遊玩時就沒有人需要生成區塊：沒有區塊延遲，磁碟佔用大小可預知，只需在一開始等待一次，而不是在頭一個小時裡不斷卡頓。

出生點周圍的前 12 個區塊一律會被處理，無論資源包或設定檔如何設定，因為在任何人加入之前，遊戲自己恰好就會生成這麼多。`pregenOnNewWorld` 設定再向外延伸多遠，而指令則可以手動執行一次。

`/rdplserver pregen <radius>` 會生成執行位置周圍該區塊半徑內的每一個區塊。`status` 會回報進度，`stop` 會終止它。地形由遊戲自己的區塊系統生成，每次同時處理 `pregenChunksInFlight` 個區塊，每次一個由 32 乘 32 個區塊組成的區域檔案，各區域從中心起依環狀依序處理，同一區域內的區塊沿希爾伯特曲線排列，而且每個區域完成後才開始下一個，生成的區塊已完成光照與收尾，因此之後無需再執行光照處理。

執行期間所有人都會被固定：變為旁觀者，原地不動，螢幕中央顯示一條脈動的線，進度顯示在動作列上，周圍的天空保持靜止，每個維度中的所有生物和機器都被凍結，正在生成的那個維度的時間與天氣也停在原處。每位玩家抵達時的模式會在被固定時寫入該玩家，因此執行途中儲存的存檔、當機或重新加入都不會讓任何人滯留在旁觀者模式；執行結束時會原樣歸還所取走的模式，若資源包設定了 `worldGameMode` 則歸還該模式，`hardcore` 則歸還生存模式。裝有該模組的用戶端在被固定時會看到視野起霧，之後看到標誌淡入；原版用戶端則只看到普通的固定畫面。每個維度已生成到什麼程度會儲存在世界中，因此已完成的世界不會再次執行。結束的訊息會在世界備份之前通知所有人，由主控台或指令方塊啟動的執行會把計數回報到那裡。

在資源包中，這些鍵放在[世界範本](#世界範本)的 `settings` 區段中，與其他每個 `chunks` 鍵一樣。下面列出其中的每一項，只有 `pregenBorderLimit` 不在其中，因為它只存在於設定中：

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

### 生成的內容

*預先生成*

`config/resourcedatapackloader-common.toml`

```toml
[chunks]
pregenBorderLimit = 8192
```

| 鍵                            | 作用                                                                                                                                                                  | 設定它的原因                                             |
| ----------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------- |
| `pregenOnNewWorld`            | 在任何人遊玩之前，圍繞出生點生成的區塊半徑。12 是下限，0 表示取這個下限而不是什麼都不生成，因為遊戲本來就會自行在出生點周圍生成 12 個區塊。調高它可以延伸得比遊戲更遠 | 設定資源包在遊戲已生成的地形之外延伸多遠                 |
| `pregenDimensions`            | 要生成哪些維度，依 id 依序列出，每個維度圍繞自己的出生點                                                                                                              | 新增地獄、終界或你自己的維度                             |
| `pregenAllDimensions`         | 伺服器持有的每一個維度，而不是一份清單，主世界在前，其餘依 id 順序                                                                                                    | 維度很多的資源包。每個模組的維度都算在內，所以要留意體積 |
| `pregenDimensionsWhenEntered` | 這些維度在有人首次踏入時才生成，並再次固定所有人直到完成                                                                                                              | 大多數玩家不會去的維度；從不前往的玩家無需付出任何代價   |
| `pregenToBorder`              | 將每個維度填充到其世界邊界，而不是依半徑，並以邊界為中心                                                                                                              | 有邊界的世界                                             |
| `pregenBorderLimit`           | 邊界最多可延伸多遠，以區塊計，向任一方向，超出則拒絕執行。僅限設定檔，絕不是資源包的鍵                                                                                | 防止失控的執行；只有在清楚它所需的時間和磁碟空間時才調高 |

請在發布之前，依要發布的半徑親自從頭到尾執行一次。區塊數隨半徑的平方增長，每個方向 63 就是一萬六千個區塊，500 則超過一百萬，所以你測試世界的區域資料夾大小和實際耗時，才是擺在玩家面前的真實數字。不要發布從未執行過的半徑。

### 一次執行的行為

*預先生成*

| 鍵                     | 作用                                                                                                                                                                                                                                   | 設定它的原因                                           |
| ---------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------ |
| `pregenResume`         | 被停止或中斷的執行從中斷處繼續。執行開始時，其維度、中心和半徑會寫入存檔，已完成的計數每十秒寫入一次，因此當機、斷電或中途離開，在下次載入時都會從中斷處約十秒之內恢復。主動停止的執行，無論是透過指令還是停滯監看程式，都保持停止狀態 | 伺服器上的長時間執行；小規模執行即使沒有它也能輕鬆重啟 |
| `pregenChunksInFlight` | 執行一次向遊戲請求多少個區塊。更多會讓生成執行緒更忙碌，而讓伺服器對被固定圍觀的人回應更慢                                                                                                                                             | 在空伺服器上調高，在有人遊玩的伺服器上調低             |

### 向玩家顯示什麼

*預先生成*

| 鍵                                                             | 作用                                                                                                                                                                                                                                                                                                                                                                                                  | 設定它的原因                                                                                             |
| -------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| `pregenRunningSays`, `pregenFinishedSays`, `pregenStoppedSays` | 各階段的訊息。第一條可以包含表示百分比的 `%d`，其後可以包含表示維度名稱的 `%s`，或用 `%1$d` 和 `%2$s` 以任意順序放置它們。保持預設值時，它們會使用每位玩家各自的語言                                                                                                                                                                                                                                  | 以資源包自己的口吻改寫，在生成多個維度時寫出維度名稱，或將其靜音                                         |
| `pregenSpectatingSays`                                         | 生成地形期間螢幕中央的固定提示行。保持預設值時，它會使用每位玩家各自的語言；留空則不顯示任何內容                                                                                                                                                                                                                                                                                                      | 請保持在約三十五個字元以內，否則小視窗會將其截斷                                                         |
| `pregenLogo`                                                   | 預先生成完成時標誌所在的位置：`left`, `center` 或 `right`，位於螢幕中央文字之上，顯示幾秒鐘，然後隨霧一同淡出                                                                                                                                                                                                                                                                                         | 它總會顯示；未知的詞會被當作 `center`                                                                    |
| `welcomeSays`                                                  | 綠色的問候語，每次登入以及預先生成之後都會顯示。單獨的一項是適用於所有地方的那一行；`dimension=message` 形式的項目會為該維度覆寫它，並向每個抵達該維度的人問候，例如 `"minecraft:the_nether=Welcome to the Nether!"`。維度也可以寫成 1.12.2 的 `0`, `-1` 或 `1`，在地形生成期間抵達的人不會收到問候。`=` 之後的空訊息會讓該維度靜音；空清單則不顯示任何內容。保持預設值時，它會使用每位玩家各自的語言 | 用一條單獨的行寫出你資源包的名稱；再新增各維度的行，為每個世界設定主題。請將每行保持在約三十五個字元以內 |
| `saysCard`                                                     | 將這個模組發出的各行文字，即歡迎語、中途加入的玩家收到的地形生成提示、執行的結束（執行進度仍顯示在動作列上），以及威脅提示行，以卡片形式顯示在右下角，而不是顯示在聊天中。卡片滑入，停留八秒後淡出，即使開啟了介面也會顯示在其上方                                                                                                                                                                    | 當聊天欄很忙，或這些文字應當讀起來像是世界的一部分而不是閒聊時，可將其開啟                               |
| `saysIcon`                                                     | 繪製在卡片上的物品，例如 `minecraft:compass`。留空則不繪製                                                                                                                                                                                                                                                                                                                                            | 為卡片配上資源包的標誌                                                                                   |
| `saysColor`                                                    | 卡片的背景顏色，十六進位，例如 `1E2630`。留空則使用深石板色                                                                                                                                                                                                                                                                                                                                           | 與資源包的配色相配                                                                                       |
| `saysImage`                                                    | 來自資源包用戶端資源的 PNG，例如 `rubyworld:textures/gui/card.png`，拉伸後鋪滿卡片作為背景，繪製在顏色之上。留空則不繪製                                                                                                                                                                                                                                                                              | 為卡片配上一塊繪製的面板；圖片請保持寬而矮，它會被拉伸到文字所需的大小                                   |
| `saysBackground`                                               | 繪製卡片的面板、邊框和顏色條，以及玩家被固定時螢幕中央歡迎語和提示後方的深色背景。關閉時只剩下文字（保留其陰影）以及 `saysImage`（若已設定）                                                                                                                                                                                                                                                          | 讓文字漂浮在世界之上，或者讓繪製的 `saysImage` 獨立呈現                                                  |
| `saysFont`                                                     | 卡片文字所用的字型，以 `namespace:name` 命名，例如 `rubyworld:runes`。留空則使用 RDPL 字型 `resourcedatapackloader:rdpl`。它所指向的檔案在「卡片」一節中說明                                                                                                                                                                                                                                          | 為卡片配上資源包自己的字體                                                                               |
| `toasts`                                                       | 顯示遊戲的哪些彈出通知，即右上角的彈窗。`true` 顯示全部，`false` 全部不顯示；清單則只顯示其中列出的種類：`advancements`，`recipes` 表示已解鎖的配方，`tutorial` 表示操作指引提示，`system` 表示遊戲自己的通知，`other` 表示其餘各項未涵蓋的所有彈出通知，例如其他模組的。預設不顯示任何一種。玩家的用戶端會在加入時採用該值                                                                           | 當資源包用進度來引導玩家，而其餘提示只會礙事時，請保留 `["advancements"]`                                |

### 備份與地圖重置

*預先生成*

| 鍵                      | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                           | 設定它的原因                                          |
| ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------- |
| `pregenBackup`          | 預先生成完成後，在玩家仍被固定時，將世界複製為一份原始備份。這樣生成只需付出一次代價：之後的重設，或使用同一資源包和種子碼的新世界，會還原這份副本而不是再次生成，這比預先生成兩次快得多。副本儲存在存檔之外，位於它旁邊的 `rdpl-pristine/<world>`，因此其他模組的備份不會將它一併清掃，它也不會出現在它們管理的資料夾中。若副本所對應的資源包與目前載入的不再一致，它會被丟棄，並根據手上的世界重新儲存，因此更換資源包絕不會重設成別人的地圖 | `false`                                               |
| `pregenBackupSays`      | 製作該副本期間向玩家顯示的螢幕中央提示行，其後附有百分比。留空則不顯示任何內容，副本會靜默製作                                                                                                                                                                                                                                                                                                                                                 | `Pack requested world backup`                         |
| `resetSays`             | `/rdplserver reset` 或一輪回合結束將地圖復原期間，向玩家顯示的螢幕中央提示行。留空則靜默重設                                                                                                                                                                                                                                                                                                                                                   | `Pack requested map reset`                            |
| `resetSendsTo`          | 重設時把玩家放在哪裡：`spawn`，形如 `x,y,z` 或 `x,z` 的座標（其中高度為海平面之上一格），或在任一者前加上 `dimension:` 以把他們送入另一個世界，維度可寫 id，也可寫 1.12.2 的 `0`, `-1` 或 `1`，重設就是靠它把所有人放進大廳，而不是放回競技場                                                                                                                                                                                                  | `spawn`                                               |
| `resetRuns`             | 重設清除地圖之後執行的函數，以 `namespace:path` 命名。它負責重新建造競技場，因為用函數製作地圖的資源包只需再執行一次即可。留空則不執行任何內容                                                                                                                                                                                                                                                                                                 | 空                                                    |
| `resetClearsEntities`   | 移除所有不是玩家的實體。生物、掉落的物品和經驗值全部消失，這樣才能讓地圖恢復到起始狀態                                                                                                                                                                                                                                                                                                                                                         | `true`                                                |
| `resetClearsScores`     | 將資源包維護的每個目標重設為零，使新的比賽從零開始。隊伍本身會被保留                                                                                                                                                                                                                                                                                                                                                                           | `true`                                                |
| `resetClearsInventory`  | 清空每位玩家的物品欄，包括盔甲和副手，使一輪回合從地圖發放的物品開始，而不是從上一輪留下的物品開始。陣營的 `gives` 隨後立即再次發放                                                                                                                                                                                                                                                                                                            | `false`                                               |
| `resetClearsExperience` | 將每位玩家的經驗值重設為 0 級                                                                                                                                                                                                                                                                                                                                                                                                                  | `false`                                               |
| `spawnChunkRadius`      | 距出生點多遠（以方塊計）的區塊無論是否有玩家在場都保持載入，取整到整個區塊：每個方向 `(blocks + 8) / 16`，因此預設的 `128` 保持 8。世界啟動時，在伺服器就緒之前，主世界會在此基礎上每個方向再多準備 4 個區塊的正方形範圍。`0` 表示既不準備也不保持任何區塊。RDPL 用自己的區塊票據來保持它們，因此在 1.21.1 上，只要此鍵生效，`spawnChunkRadius` 遊戲規則就不起作用                                                                             | 讓出生點的機器或農場持續運作，或用 `0` 關閉出生點區塊 |
| `spawnChunkRadii`       | 以 `dimension=blocks` 形式書寫的主世界半徑，如 `minecraft:overworld=64`，它會覆寫 `spawnChunkRadius`。只有主世界有出生點區塊，因此其他維度的項目不會改變任何東西                                                                                                                                                                                                                                                                               | 在依維度設定半徑的資源包中，設定出生區域的大小        |

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

文字檔案放在 `assets/<namespace>/texts/*.txt`。純文字，一行一個段落，空行保持為空行。`.md` 檔案以相同方式讀取，兩種檔案都支援下面的格式。`PLAYERNAME` 會被替換為玩家的名字，與原版終界詩篇所用的替換相同。

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

陣營是遊戲自己計分板上的一支真正的隊伍，因此 `/team list` 能看到它，它的成員在存檔和重新載入後依然保留，沒有這個模組的用戶端顯示的顏色和名牌，也與任何原版隊伍一樣。成員關係依名稱確定，因此任何有名稱或 UUID 的東西都可以加入陣營：玩家、殭屍、村民、盔甲座。

```json
{
  "name": "red",
  "displayName": "Red Team",
  "color": "red",
  "prefix": "[R] ",
  "suffix": "!",
  "scoreboard": true,
  "friendlyFire": false,
  "mobFriendlyFire": false,
  "seeFriendlyInvisibles": true,
  "nameTags": "always",
  "deathMessages": "always",
  "collision": "always",
  "joinable": false,
  "balance": false,
  "entities": ["mypack:zombie_a", "mypack:sapper_a"],
  "spawnBox": [0, 60, 0, 16, 80, 16],
  "picks": 0,
  "picksFrom": ["players"],
  "gives": ["minecraft:iron_pickaxe", { "item": "minecraft:bread", "count": 8 }, { "item": "minecraft:iron_sword", "unbreakable": true }],
  "standIn": { "entity": "mypack:herobrine", "at": "23,31,0" },
  "spawn": "0,64,0",
  "lead": "topScore",
  "leadOn": "kaboom",
  "leadIs": "Steve",
  "leadSays": "You are the current round leader",
  "leadRuns": "mypack:lead_chosen"
}
```

### 陣營

*隊伍*

| 設定          | 類型   | 預設     | 作用                                                                                                                                                                       |
| ------------- | ------ | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | 文字   | 檔案名稱 | 隊伍在計分板上的名稱，1 到 16 個字元。`/team` 和其他檔案使用的就是它                                                                                                       |
| `displayName` | 文字   | 名稱     | 向玩家顯示的內容，用以取代名稱                                                                                                                                             |
| `color`       | 文字   | `white`  | 十六種文字顏色之一。它為名牌著色，也是每隊側邊欄欄位所依據的鍵                                                                                                             |
| `prefix`      | 文字   | 空       | 放在成員名稱前面，位於顏色之後                                                                                                                                             |
| `suffix`      | 文字   | 空       | 放在成員名稱後面                                                                                                                                                           |
| `scoreboard`  | 布林值 | `true`   | 該陣營是否作為遊戲計分板上的隊伍存在。關閉則完全不設立隊伍：其生物改為在名稱中帶上陣營的顏色，沒有任何機制阻止它們互相戰鬥，也不會有積分記到它頭上，因為計分是依隊伍進行的 |

只有在資源包要求時才會設立陣營：若各處都沒有 `teams` 資料夾，這個模組不會新增任何隊伍，不監聽任何東西，也不提供該指令。編輯了檔案的伺服器管理員可以執行 `/rdplserver reload`，在不重新啟動的情況下把變更套用到正在執行的世界。

### 戰鬥與可見度

*隊伍*

| 設定                    | 類型   | 預設           | 作用                                                                                                                      |
| ----------------------- | ------ | -------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `friendlyFire`          | 布林值 | `false`        | 成員之間是否可以互相傷害。同時也是 `mobFriendlyFire` 的預設值                                                             |
| `mobFriendlyFire`       | 布林值 | `friendlyFire` | 陣營的生物是否會被己方的爆炸和投擲的 TNT 傷到，單靠遊戲本身永遠不會阻止這種情況。關閉則保護己方；開啟則維持遊戲原有的行為 |
| `seeFriendlyInvisibles` | 布林值 | `true`         | 成員隱形時彼此是否能看見                                                                                                  |
| `nameTags`              | 文字   | `always`       | `always`, `never`, `hideForOtherTeams` 或 `hideForOwnTeam`，不分大小寫                                                    |
| `deathMessages`         | 文字   | `always`       | 同樣的四個詞，決定成員死亡時通知哪些人                                                                                    |
| `collision`             | 文字   | `always`       | `always`, `never`, `pushOtherTeams` 或 `pushOwnTeam`，不分大小寫                                                          |

### 誰會加入

*隊伍*

| 設定        | 類型   | 預設    | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| ----------- | ------ | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `entities`  | 清單   | 空      | 實體 id，其每一次生成都會加入這個陣營，例如 `minecraft:zombie` 或你自己的實體                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `players`   | 清單   | 空      | 登入時加入這個陣營的玩家名稱                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `spawnBox`  | 清單   | 無      | 六個整數，x y z 到 x y z。在範圍內生成的任何東西都會加入，兩個角的順序可以任意                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `joinable`  | 布林值 | `true`  | 玩家是否可以用 `/rdplserver team join` 加入。對只供生物使用的陣營請設為 false                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `balance`   | 布林值 | `false` | 不帶名稱的 `/rdplserver team join` 是否可以把玩家放到這裡。在允許它的陣營中，會選擇玩家最少的那一個                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `picks`     | 數字   | `0`     | 這個陣營隨機抽取多少名成員。每一回合開始時，陣營會讓上一次抽到的成員回到原本站的位置，並從 `picksFrom` 所指的全部對象中重新抽取；兩次抽取之間，來自該範圍的登入或生成會立刻填補空位。它的用途是：從所有人中抽出一名玩家，自成一個陣營                                                                                                                                                                                                                                                                                             |
| `picksFrom` | 清單   | 空      | 抽取的來源：`players` 表示所有在線玩家，實體 id 表示該種類的每一隻活著的生物                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `standIn`   | 物件   | 無      | 在沒有玩家加入時代守該陣營的生物：`{ "entity": "mypack:herobrine", "at": "23,31,0" }` 會在主世界的那個位置保持一隻該實體存活，缺少時就生成一隻，並在有玩家加入該陣營的瞬間將其移除，因此遊戲會在對抗 AI 的狀態下進行，直到有玩家接手這個角色。每五秒檢查一次；該位置必須在已載入的地面上。在有大廳的遊戲中（`opens.by: leader`），替身只會在大廳等待期間和回合開始時生成，所以倒下的替身會在回合其餘時間及其結束後一直不再出現，直到所有人回到大廳；沒有大廳時，在以 `ends.lastStanding` 結束的回合進行期間，倒下的替身不會被補上 |

有三種加入方式，一個陣營可以同時使用。`entities` 指明實體 id，該類型的任何東西在生成時加入，資源包就是這樣在不改動生物本身的情況下為牠們分配陣營的。`spawnBox` 劃定世界的一角，在其中生成的任何東西都會加入，適合雙方使用同一種生物的競技場。`players` 則直接指名玩家。除此之外，玩家可以用 `/rdplserver team join <name>` 加入，除非該陣營把 `joinable` 設為 false，並可用 `/rdplserver team leave` 離開。

### 初始裝備與出生點

*隊伍*

| 設定                | 類型   | 預設    | 作用                                                                                                                                                                                                                    |
| ------------------- | ------ | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `gives`             | 清單   | 空      | 玩家加入陣營時放入其物品欄的物品，單一物品寫物品名稱，多個或不會磨損的物品寫 `{ "item", "count", "unbreakable" }`，放入任意空閒欄位，沒有空位時則丟在其腳邊。在清空物品欄的重設（`resetClearsInventory`）之後會再次發放 |
| `gives.unbreakable` | 布林值 | `false` | 在 `gives` 項目的物件寫法中，使該物品永不磨損                                                                                                                                                                           |
| `spawn`             | 文字   | 無      | 主世界中的 `x,y,z`，回合開始時該陣營的玩家會被放到這裡，讓各陣營從自己的地面出發；沒有它時，他們會留在重置或大廳讓他們所在的位置                                                                                        |

### 主導者

*隊伍*

| 設定       | 類型 | 預設                               | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ---------- | ---- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `lead`     | 文字 | `none`                             | 陣營的主導者如何產生：`none`；`first` 表示在線成員中最早加入該陣營的人，所以當有人離開時會依加入順序往下傳，等人回來又會回到他身上，他們抵達時（越過介紹與任何控制之後）以及輪到他們時都會收到通知；`topScore` 表示 `leadOn` 所指目標上分數最高的人；`appointed` 表示 `leadIs` 所指的玩家；`vote` 表示成員投票選出的人；`claim` 表示最先認領的人。主導者只是一個標籤和一種顏色，別無其他：它不賦予任何權力，所以主導者登出不會破壞任何東西 |
| `leadOn`   | 文字 | 空                                 | 搭配 `topScore`，成員據以排名的目標。每次讀取時都會重新計算，因此會隨分數變動                                                                                                                                                                                                                                                                                                                                                              |
| `leadIs`   | 文字 | 空                                 | 搭配 `appointed`，擔任主導者的玩家                                                                                                                                                                                                                                                                                                                                                                                                         |
| `leadSays` | 文字 | `You are the current round leader` | 主導權交到玩家手上時告知他：在他們抵達自己所主導的陣營時、在他們認領時，或在 `first` 主導者傳給他們時（此時訊息會帶有離開的人是誰）。`{side}` 是陣營的顯示名稱；留空則不告知                                                                                                                                                                                                                                                               |
| `leadRuns` | 文字 | 空                                 | 一個函式，`namespace:path`，每次主導權交給某位玩家時執行一次：第一位主導者，以及之後的每一次交接。它以主導者的身分、在其位置執行，權限等同進度獎勵的函式，所以 `@s` 就是主導者。每秒檢查一次；離線的主導者會在下次上線時補執行。重新啟動會重新決定主導者                                                                                                                                                                                   |

## 計分

*遊戲模式*

`<namespace>/scoring/*.json`

檔案名稱由你自行決定，只會讀取該資料夾，多個檔案會疊加。每個檔案是一個目標。

目標是遊戲自己計分板上的一個真正的目標，因此 `/scoreboard players list` 能讀到它，它的分數在存檔後依然保留。`criterion` 是遊戲自行統計的內容：`dummy` 表示只有這個資源包才會改動的分數，或者 `deathCount`、`playerKillCount`、`totalKillCount`、`health`、`air`、`armor`、`food`、`level`、`xp`、`trigger`，以及任何以 `/scoreboard` 所接受的寫法書寫的統計，例如 `minecraft.custom:minecraft.jump`。1.12.2 的統計如 `stat.jump` 會被讀作它後來變成的那一項。

```json
{
  "name": "kaboom",
  "displayName": "Kills",
  "criterion": "dummy",
  "display": "sidebar",
  "render": "integer",
  "teamTotals": true,
  "individuals": false,
  "carries": false,
  "awardsTo": "totals",
  "tiebreak": true,
  "points": {
    "kill": { "mypack:zombie_a": 1, "mypack:zombie_b": 1 },
    "death": -1,
    "raid": true,
    "order": true,
    "ownKill": -1
  },
  "opens": { "by": "leader", "lobby": "0,64,0", "says": "Waiting for {leader} to start the round", "leaderSays": "Type /rdpl round start", "lobbyJoins": true, "joinsSays": "Round is in progress, you can join after it ends" },
  "ends": {
    "afterMinutes": 10,
    "atScore": 100,
    "afterRounds": 3,
    "lastStanding": false,
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
    "image": "mypack:textures/gui/results.png",
    "background": "1E2630",
    "seconds": 15
  }
}
```

### 目標

*計分*

| 設定          | 類型   | 預設       | 作用                                                                                                                                                           |
| ------------- | ------ | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`        | 文字   | 檔案名稱   | 目標在計分板上的名稱，1 到 16 個字元                                                                                                                           |
| `displayName` | 文字   | 名稱       | 向玩家顯示的名稱，用以代替 `name`                                                                                                                              |
| `criterion`   | 文字   | `dummy`    | 遊戲自行統計的內容。未知的準則會被拒絕，並輸出一行說明                                                                                                         |
| `display`     | 文字   | 空         | `sidebar`, `list`, `belowName`（也接受 `below_name`）或 `sidebar.team.<color>`。留空則不在任何地方顯示；沒有可供開啟的計分板介面                               |
| `render`      | 文字   | 準則自帶的 | `integer` 或 `hearts`                                                                                                                                          |
| `teamTotals`  | 布林值 | `true`     | 積分記到以成員所屬隊伍命名的行上                                                                                                                               |
| `individuals` | 布林值 | `false`    | 積分同時也記到成員自己的一行上                                                                                                                                 |
| `carries`     | 布林值 | `false`    | 目標在地圖重設時保留，而不是隨之清空。回合勝場的比賽累計就是一例                                                                                               |
| `awardsTo`    | 文字   | 空         | 該目標結束時向其授予一分的另一個目標，授予領先的陣營。等級排名不授予任何東西                                                                                   |
| `tiebreak`    | 布林值 | `false`    | 以並列第一結束的回合會用世界的隨機數從並列各方中抽出一方，把抽籤寫入日誌，並照常給它記分。比賽（沒有 `awardsTo` 的目標）也同樣抽籤，並在結果頂部寫出抽中的一方 |

### 積分

*計分*

| 設定             | 類型   | 預設    | 作用                                                                                                                                  |
| ---------------- | ------ | ------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| `points.kill`    | 物件   | 空      | 實體 id 對應積分，計給擊殺者的陣營。`minecraft:player` 計算擊殺玩家的分數                                                             |
| `points.death`   | 整數   | `0`     | 成員死亡時的積分，無論死因為何。可以為負                                                                                              |
| `points.raid`    | 布林值 | `false` | 接收每場獲勝[突襲](#突襲)的 `points`：範圍內的每位玩家都會獲得相應積分，記在所屬陣營的一列上，啟用 `individuals` 時也記在本人的一列上 |
| `points.order`   | 布林值 | `false` | 計入每個已完成[工作訂單](#工作訂單)的 `points`：每位獲得獎勵的玩家在其陣營的列上獲得該點數，開啟 `individuals` 時在自己的列上也獲得   |
| `points.ownKill` | 整數   | `0`     | 擊殺者自己陣營的成員被擊殺時的積分，取代 `kill` 的值。0 表示不計分；負數是懲罰                                                        |

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

| 設定                       | 類型   | 預設                          | 作用                                                                                                 |
| -------------------------- | ------ | ----------------------------- | ---------------------------------------------------------------------------------------------------- |
| `ends.resets`              | 布林值 | `false`                       | 回合結束時重設地圖，具體如[世界範本](#世界範本)下的 `resetSays` 及其他重設設定所述，然後開啟新的回合 |
| `ends.intermissionSeconds` | 整數   | `10`                          | 從結束到重設之間，排名停留多久                                                                       |
| `ends.intermissionSays`    | 文字   | `Round cooldown {seconds}`    | 回合結束後的休整期間，每秒顯示在動作列上，`{seconds}` 倒數至重設。留空則不顯示                       |
| `ends.startsSays`          | 文字   | `Round starting in {seconds}` | 重設後開啟下一回合的五秒倒數期間顯示在動作列上，`{seconds}` 倒數。留空則不顯示                       |

### 大廳

*計分*

| 設定               | 類型   | 預設                                               | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| ------------------ | ------ | -------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `opens`            | 物件   | 空                                                 | 包含下面的大廳設定，每一項都寫在其中。不寫時，各項都保持預設值                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `opens.by`         | 文字   | `auto`                                             | `auto` 在重設五秒後自動開啟下一回合。`leader` 則讓遊戲停在大廳：重設之後以及世界首次載入時，不計任何分，也沒有計時，陣營可以自由加入和離開，只有當某個陣營的主導者或管理員執行 `/rdplserver round start` 時回合才會開啟，而且在仍有人在閱讀世界介紹時不會開啟；隨後進行五秒倒數，抽籤完成，每個陣營被送到各自的 `spawn`。在世界等待期間，直到五秒倒數結束，玩家留在原地，不能破壞、放置、使用、攻擊或丟棄任何東西，也不會受到傷害，嘗試時會看到等待提示，其他所有生物也都靜止不動：沒有 AI，沒有移動。指令仍然可用，因此可以加入陣營並開始回合 |
| `opens.says`       | 文字   | `Waiting for {leader} to start the round`          | 像歡迎語一樣顯示在螢幕中央，對象是每個不是主導者的玩家：當他們越過介紹進入大廳、歡迎語顯示過之後；回合結束後大廳再次開啟時；主導者來去而內容變化時；以及他們嘗試大廳所禁止的事情時。`{leader}` 是所有陣營的主導者，沒有主導者時則為 `a leader`。留空則不顯示                                                                                                                                                                                                                                                                                   |
| `opens.leaderSays` | 文字   | `Type /rdpl round start`                           | 以相同方式、在相同時刻顯示給主導某個陣營的玩家，取代 `opens.says`。留空則不顯示                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `opens.lobby`      | 文字   | 無                                                 | 主世界中寫作 `x,y,z`，其他世界中寫作 `dimension:x,y,z`，例如 `minecraft:the_nether:0,64,0`，大廳持續期間所有人在這裡等待：每個玩家以及每個屬於某陣營的存活生物都被安置在該點周圍的一個圓環上，各自面朝圓心，於是彼此怒目相對。每個單位分得的弧長為其自身寬度加兩格，因此互不重疊，人數增多時圓環也隨之擴大；每當有人加入或離開，圓環會重新佈局。高度取他們所站立的地面，在上下三格範圍內尋找。玩家和生物可以直接進入那個世界並返回，不會建造傳送門。回合開啟時玩家前往所屬陣營的 `spawn`，仍站著的生物則被放回它原來所在世界中的原位           |
| `opens.lobbyJoins` | 布林值 | `false`                                            | 回合中途登入的玩家，會被放進大廳以旁觀者身分等待回合結束，而不是出現在他們登出時的位置。需要 `opens.lobby`                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `opens.joinsSays`  | 文字   | `Round is in progress, you can join after it ends` | 對他們顯示的話。留空則不說                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |

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
    "voteSays": "{player} calls a vote to reset the round",
    "tallySays": "Reset the round? {yes} yes, {no} no, {seconds}",
    "passSays": "The vote passed, so the round is reset",
    "failSays": "The vote failed, so the round goes on"
  }
}
```

| 設定                    | 類型 | 預設                                                                                      | 作用                                                                                                                                                                                                                    |
| ----------------------- | ---- | ----------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `reset.lead`            | 文字 | `none`                                                                                    | 回合進行時，`/rdplserver round reset` 對陣營主導者的作用。`now` 立即結束回合並重設地圖；`vote` 改為發起投票；`none` 不給主導者任何特權，因此在 `players` 允許的情況下，主導者像其他玩家一樣發起投票。管理員總是立即重設 |
| `reset.players`         | 文字 | `none`                                                                                    | `vote` 允許任何陣營的玩家用 `/rdplserver round reset` 發起投票。`none` 則把重設留給主導者                                                                                                                               |
| `reset.teams`           | 清單 | 空                                                                                        | 其玩家可以發起投票的陣營。留空表示所有陣營                                                                                                                                                                              |
| `reset.passPercent`     | 整數 | `51`                                                                                      | 回合要被重設，投贊成票的投票者所佔的比例，1 到 100。`51` 即過半，`100` 即全體                                                                                                                                           |
| `reset.voteSeconds`     | 整數 | `30`                                                                                      | 一次投票持續多久，最少五秒。結果一旦確定就會提前結束                                                                                                                                                                    |
| `reset.cooldownSeconds` | 整數 | `60`                                                                                      | 投票失敗後，要隔多久才能再次發起。使用 `now` 的主導者不受其限制                                                                                                                                                         |
| `reset.leadSays`        | 文字 | `{player} reset the round`                                                                | 回合被立即重設時告知所有人，`{player}` 為執行重設者。留空則不說                                                                                                                                                         |
| `reset.voteSays`        | 文字 | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | 發起投票時告知所有人，`{player}` 為發起者。留空則不說                                                                                                                                                                   |
| `reset.tallySays`       | 文字 | `Reset the round? {yes} yes, {no} no, {seconds}`                                          | 投票期間每秒顯示在動作列上，`{seconds}` 倒數。留空則不顯示                                                                                                                                                              |
| `reset.passSays`        | 文字 | `The vote passed, so the round is reset`                                                  | 投票通過時告知所有人。留空則不說                                                                                                                                                                                        |
| `reset.failSays`        | 文字 | `The vote failed, so the round goes on`                                                   | 投票未通過時告知所有人。留空則不說                                                                                                                                                                                      |

重置會把回合就地中斷。排名會顯示在 `The round was reset` 之下，沒有人被授予該回合，中場休息開始倒數，地圖的重置就像回合以 `ends.resets` 結束一樣，在 `opens.by` 為 `leader` 的情況下回到大廳。無論回合本身會不會自行結束，它都有效，但在大廳中、開啟回合的倒數期間，或回合已結束且其重置正在進行時無效；此時仍在進行的投票會被丟棄。

每位在線且屬於某個陣營的玩家都可以投票，無論所屬哪個陣營，用 `/rdplserver round vote yes` 或 `no`，並且可以在投票進行期間更改投票。發起投票的人視為已投贊成票，時間結束時尚未投票的玩家按反對計。在設有陣營的資源包中，不屬於任何陣營的玩家既不能發起也不能參與投票；在沒有陣營的資源包中，每位在線玩家都可以。使用的是第一個其 `reset` 允許任何人重設的計分檔案。

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
    "mobTags": ["duelist"],
    "mobTypes": ["minecraft:zombie"]
  }
}
```

| 設定                | 類型 | 預設     | 作用                                                                                                                                                                                           |
| ------------------- | ---- | -------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `turns.order`       | 文字 | `fixed`  | 誰在何時行動，在每一輪開始時決定。`fixed` 保持各陣營首次出現的順序，`random` 每一輪重新打亂，`lowestFirst` 從此目標上分數最低的陣營開始，`lastWinner` 從贏得上一回合的陣營開始，其餘依固定順序 |
| `turns.seconds`     | 整數 | `60`     | 每次行動持續多久，至少一秒                                                                                                                                                                     |
| `turns.gapSeconds`  | 整數 | `3`      | 兩次行動之間的停頓，期間所有陣營都在等待。0 表示直接繼續                                                                                                                                       |
| `turns.held`        | 文字 | `frozen` | 玩家如何等待輪到自己：`frozen` 讓其站著不動，並像大廳那樣不讓其攻擊、挖掘、建造、使用或丟棄物品；`spectator` 或 `adventure` 把其切換到該遊戲模式，輪到自己時再換回原來的模式                   |
| `turns.endsAtScore` | 整數 | `0`      | 一旦有陣營在此目標上達到這個分數，輪流行動就結束，這一回合也隨之結束。0 表示從不因分數結束                                                                                                     |
| `turns.cycles`      | 整數 | `0`      | 這麼多輪之後輪流行動結束，這一回合也隨之結束；一輪就是在場的每個陣營各行動一次。0 表示一直進行到別的條件結束這一回合                                                                           |
| `turns.mobTags`     | 清單 | 空       | 讓不在隊伍中的生物加入輪流行動的計分板標籤，每個標籤一組                                                                                                                                       |
| `turns.mobTypes`    | 清單 | 空       | 讓不在隊伍中的生物加入輪流行動的實體 id，每個 id 一組                                                                                                                                          |

`turns` 讓各陣營在回合進行時輪流行動。每個隊伍是一個陣營；沒有隊伍時，每位玩家各自是一個陣營，而屬於某隊伍的生物跟著該隊伍走。輪到一個陣營時，其他所有陣營都在等待：其玩家依 `held` 所述，其生物則像大廳那樣被凍結在原地。沒有人參與的陣營，或玩家全部被淘汰的陣營，會被跳過。

每個回合都會在聊天中宣布，其計時會倒數在動作列上。輪到的陣營在剩餘 10 秒時和 3 秒時，會在聊天中收到警告並伴隨一聲鈴響。`/rdplserver game pass`，或用帶有 `passesTurn` 的物品按右鍵，可以提前結束一個回合：玩家只能讓自己陣營的回合過去，而指令方塊或主控台則會讓當前輪到的任何人過去。

以 `endsAtScore` 或 `cycles` 結束，與其他任何結束方式一樣會結束回合：顯示排名，套用 `awardsTo` 和 `tiebreak`，而 `ends.resets` 會重置地圖。只有第一個帶有 `turns` 的計分檔案會輪流行動。措辭來自 `turn`、`turnclock`、`turnwarn`、`turnout`、`turnpass`、`turngap`、`notturn` 和 `noturns` 這些鍵，骰子檔案的 `says` 可以更改它們。

## 突襲

*遊戲模式*

`<namespace>/raids/*.json`

檔案名稱由你自行決定，只會讀取該資料夾，多個檔案會疊加。每個檔案是一場突襲。

突襲是遊戲自 1.14 起就有的那種，在遊戲自帶的某個村莊上展開。當帶有 `omen` 效果的玩家進入村莊時突襲開始：該效果被移除，村莊中心 `reach` 範圍內的每個玩家都會看到一個頭目血條。經過 `waveDelay` 刻後，第一波在村莊周圍的圓環上出現，向中心進發，沿途攻擊玩家、村民和鐵魔像。突襲者之間不會互相傷害或以彼此為目標，因此牠們之間的流矢或誤擊不會造成任何影響。頭目血條顯示該波剩餘的生命值，並在剩下兩個或更少的突襲者時開始統計突襲者數量。一波消滅後，下一波等待 `waveDelay` 刻。最後一波消滅且兩秒內沒有新的突襲者出現時，突襲勝利；在已有一波到來之後，若所有村民死亡或村莊本身消失，則突襲失敗。無論哪種結果，頭目血條都會顯示三十秒，並且對範圍內的每個玩家以其身分執行相應的函數。

進行中的突襲會隨世界一起儲存，其突襲者在重新載入後繼續行軍。突襲在和平難度下、經過 `timeout` 刻後，或村莊周圍沒有任何地點能容納一波時，會無結局地停止。村莊是遊戲記錄其村莊據點的地方，即村民佔用的床、工作方塊和鐘：其中心是這些點的中央，範圍至少向外延伸 32 格，其村民是在該範圍內、且與中心高度相差在四格以內的村民。遊戲自身的 `minecraft:bad_omen` 會先觸發遊戲自身的突襲，因此突襲要使用其資源包自己的效果。[環境](#環境)還可以按時鐘發起突襲，並改變突襲的波數和積分。

一波突襲來到村莊期間，村民會跑回家並留在那裡，就像遊戲中的鐘敲響時一樣。突襲者會拆毀擋路的木門以追到村民，每扇門十二秒，在一般和困難難度下且 `mobGriefing` 開啟時生效；鐵門擋得住。類型為 `bell` 的方塊無論位於何處都是村莊的鐘，並依[鐘](#鐘)所述敲響；突襲的 `bell` 可以指定其他任何方塊當作鐘來敲響。一波到來時，村莊中的每一口鐘都會敲響，被指定的方塊在玩家使用時也會敲響：48 格內的村民躲藏十五秒，48 格內的突襲者發光三秒。`minecraft:bell` 本來就存在於遊戲的村莊中，可以被指定，而且它仍會依遊戲自己的方式敲響；資源包自己的鐘方塊則透過 NBT 結構放置到村莊中，作為地塊或廣場的中心裝飾。

```json
{
  "omen": "mypack:bad_omen",
  "name": "Raid",
  "color": "red",
  "waveDelay": 300,
  "spawnDistance": 32,
  "reach": 96,
  "timeout": 48000,
  "sound": "mypack:raid_horn",
  "wins": "mypack:raid_won",
  "loses": "mypack:raid_lost",
  "points": 10,
  "loot": "mypack:raid_reward",
  "anywhere": true,
  "bell": "minecraft:bell",
  "waves": [
    [ { "entity": "minecraft:vindicator", "count": 2 }, { "entity": "mypack:raider", "count": { "min": 1, "max": 3 } } ],
    [ { "entity": "minecraft:evoker" }, { "entity": "minecraft:vindicator", "count": 4 } ]
  ]
}
```

### 突襲事件

*突襲*

| 設定            | 類型           | 預設     | 作用                                                                                                                                                                                                                                            |
| --------------- | -------------- | -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `omen`          | 效果名稱       | 無，必填 | 持有者在村莊內時觸發突襲的效果。任何已註冊的效果都可以，包括資源包自己的藥水                                                                                                                                                                    |
| `name`          | 文字           | `Raid`   | 頭目血條的標題                                                                                                                                                                                                                                  |
| `color`         | 文字           | `red`    | 血條的顏色：`pink`, `blue`, `red`, `green`, `yellow`, `purple` 或 `white`                                                                                                                                                                       |
| `waves`         | 波次清單       | 無，必填 | 每一波是一個群組的清單，各波依序到來                                                                                                                                                                                                            |
| `anywhere`      | 布林值         | `false`  | 為 `true` 時，不祥之兆也會在不靠近任何村莊的玩家身上觸發此突襲，並以該玩家為中心。這樣的突襲以及用 `/rdplserver raid` 開始的突襲不需要村莊：無法失敗，只會以勝利或 `timeout` 結束，並敲響中心 32 格內的每一口鐘。積分與戰利品和其他突襲一樣發放 |
| `waveDelay`     | 整數           | `300`    | 第一波之前，以及一波結束到下一波之間的刻數                                                                                                                                                                                                      |
| `spawnDistance` | 整數           | `32`     | 一波到來時距村莊中心多遠。最先嘗試此距離的兩倍處，然後是此距離處，再然後是村莊內部                                                                                                                                                              |
| `reach`         | 整數           | `96`     | 中心周圍這麼多格內的玩家會看到頭目血條，結束函數也會以他們的身分執行。越過該範圍十六格的突襲者會脫離突襲                                                                                                                                        |
| `timeout`       | 整數           | `48000`  | 未完成的突襲經過這麼多刻後無結局地停止。`0` 表示永不停止                                                                                                                                                                                        |
| `sound`         | 音效名稱       | 無       | 每波到來時，從該波來的方向向範圍內的每個玩家播放                                                                                                                                                                                                |
| `wins`          | 函數           | 無       | 突襲勝利時，以範圍內每個玩家的身分執行                                                                                                                                                                                                          |
| `loses`         | 函數           | 無       | 突襲失敗時，以範圍內每個玩家的身分執行                                                                                                                                                                                                          |
| `points`        | 整數           | `0`      | 突襲獲勝時記給範圍內每位玩家的積分，計入每個開啟了 `points.raid` 的目標。失敗不計分                                                                                                                                                             |
| `loot`          | 戰利品表       | 無       | 突襲獲勝時，為範圍內的每位玩家各擲出一次的戰利品表。物品會放入其背包，背包已滿則掉在腳邊                                                                                                                                                        |
| `bell`          | 方塊名稱或清單 | 無       | 在玩家使用時以及每波到來時，像鐘一樣敲響的其他方塊。類型為 `bell` 的方塊無需指定也會敲響                                                                                                                                                        |

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
  "biomes": ["minecraft:desert", "#minecraft:is_badlands"],
  "title": "The Dry Lands",
  "lines": ["Day {day}, {player}.", "Water is scarce from here on."],
  "style": "center",
  "image": "mypack:textures/gui/desert_card.png",
  "color": "3A2A10",
  "ticks": 120,
  "when": { "timeFrom": 0, "timeTo": 12000, "biomes": ["minecraft:desert"], "dayAtLeast": 3, "advancement": "minecraft:story/mine_stone", "gameMode": "survival", "team": "red", "objective": "kills", "scoreAtLeast": 5 },
  "repeat": "once_per_player"
}
```

```json
{
  "lines": ["{text}", "Speak to the gatekeeper for more."],
  "icon": "minecraft:ender_eye",
  "cooldown": 30,
  "audience": "player",
  "runs": "mypack:card_shown"
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

| 觸發器             | 需要               | 觸發時機                                                                                                                                                                                |
| ------------------ | ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `command`          | 無                 | 執行 `/rdplserver card <rule> [players]`。該指令會略過 `when`、`repeat` 和 `cooldown`，但仍會執行 `runs`。任何規則都可以這樣顯示，無論其觸發器為何                                      |
| `first_join`       | 無                 | 玩家第一次加入世界                                                                                                                                                                      |
| `dimension_enter`  | `dimension`        | 玩家抵達該維度                                                                                                                                                                          |
| `biome_enter`      | `biomes`           | 玩家從別處走進其中一個生態域                                                                                                                                                            |
| `structure_enter`  | `structures`       | 玩家從外面走進其中一個結構                                                                                                                                                              |
| `advancement`      | `advancement`      | 玩家獲得該進度                                                                                                                                                                          |
| `time_of_day`      | `time`             | 當玩家在該維度中時，日夜時鐘經過該刻，範圍 `0` 到 `23999`，與 `time.windows` 使用相同刻度，並依該維度的 `dayLength` 換算。設有 `fixedTime` 的維度永遠不會觸發。以指令或床設定的時鐘不算 |
| `day`              | 無，或 `day`       | 該維度中新的一天開始；有 `day` 時，只在那一天                                                                                                                                           |
| `craft`            | `item`             | 玩家合成該物品                                                                                                                                                                          |
| `pickup`           | `item`             | 玩家撿起該物品                                                                                                                                                                          |
| `kill`             | `entity`           | 玩家擊殺該實體，或其中第 `count` 個                                                                                                                                                     |
| `respawn`          | 無                 | 玩家死亡後重生                                                                                                                                                                          |
| `death`            | 無                 | 玩家死亡                                                                                                                                                                                |
| `y_level`          | `below` 或 `above` | 玩家的高度低於或高於該值                                                                                                                                                                |
| `play_time`        | `minutes`          | 玩家在世界中的時間達到這麼多分鐘，從世界介紹關閉時算起，若沒有向其顯示介紹則從加入時算起                                                                                                |
| `roll`             | 無，或 `roll`      | 玩家用 `/rdplserver game` 或帶 `rolls` 的物品擲骰時。填了 `roll` 就只有那一種擲骰會觸發，`atLeast` 和 `atMost` 限定總和                                                                 |
| `order_done`       | 無，或 `order`     | 工作訂單完成，對每位獲得獎勵的玩家各觸發一次。帶有 `order` 時，只有 `sign` 為該詞的訂單才會觸發                                                                                         |
| `portway_activate` | 無                 | 玩家按右鍵啟動一個傳送通道。用 `portway` 或 `network` 限定其一                                                                                                                          |
| `portway_travel`   | 無                 | 行程通過所有檢查，即將在玩家動身之前發生                                                                                                                                                |
| `portway_arrive`   | 無                 | 玩家透過傳送通道抵達                                                                                                                                                                    |
| `portway_refused`  | 無                 | 傳送通道或傳送物品拒絕了玩家，`reason` 給出原因                                                                                                                                         |
| `portway_paid`     | 無                 | 一次行程扣除了不為 0 的消耗                                                                                                                                                             |
| `portway_forget`   | 無                 | 一個站點離開玩家的清單：被玩家遺忘、被指令移除，或傳送通道已不存在                                                                                                                      |
| `portway_bind`     | 無                 | 玩家把傳送物品綁定到一個傳送通道                                                                                                                                                        |
| `score`            | `objective`        | 玩家在該目標上的分數達到 `score`                                                                                                                                                        |
| `exposure_level`   | 無，或 `exposure`  | 玩家在某種危險中的等級發生變化，無論升高還是降低。帶有 `exposure` 時，只對該危險生效；帶有 `level` 時，只有達到該等級的變化才會觸發。加入後的第一次檢查絕不會觸發                       |
| `round_start`      | 無                 | 倒數結束後回合開啟：對每位玩家各觸發一次，或對 `audience` 觸發一次。重設之後和 `/rdplserver round start` 之後都會觸發                                                                   |

危險等級在每次掃描時讀取，因此卡片會在變化後該危險的 `scanInterval` 之內觸發。

生態域、結構、高度、遊玩時間和分數每秒對每位玩家檢查一次，並在從外到內的變化時觸發，絕不會在加入後的第一次檢查時觸發。對象不是 `player` 的 `time_of_day` 或 `day` 規則，會為該維度觸發一次，而不是對其中每位玩家各觸發一次。

玩家仍開著世界介紹時觸發的卡片會等待，並在介紹關閉時顯示，無論其觸發器為何，包括 `command` 在內。如果玩家在那之前離開，它就會被丟棄。

### 觸發器設定

*卡片*

```json
{
  "trigger": "kill",
  "dimension": "minecraft:the_nether",
  "biomes": ["minecraft:desert"],
  "structures": ["minecraft:village_plains"],
  "radius": 32,
  "advancement": "minecraft:story/mine_stone",
  "item": "minecraft:diamond_sword",
  "entity": "minecraft:zombie",
  "count": 3,
  "below": 0,
  "above": 64,
  "time": 13000,
  "day": 5,
  "minutes": 30,
  "roll": "2d6",
  "order": "Mine",
  "exposure": "heat",
  "level": 2,
  "atLeast": 10,
  "atMost": 12,
  "objective": "kills",
  "score": 5,
  "requires": ["mypack"]
}
```

| 設定                 | 類型                         | 預設     | 作用                                                                                                                                                                                                                |
| -------------------- | ---------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `trigger`            | 文字                         | 無，必填 | 上面的觸發器之一。內建規則不用寫                                                                                                                                                                                    |
| `dimension`          | 文字                         | 無       | 維度 id，例如 `minecraft:the_nether`；沒有命名空間的 id 會依 `minecraft:` 讀取。對於 `dimension_enter`，它是進入的那個維度；對於其他所有觸發器，它把規則限制在該維度中的玩家                                        |
| `biomes`             | 清單                         | 無       | 生態域 id，例如 `minecraft:desert`，或用 `#tag` 表示生態域標籤，例如 `#minecraft:is_ocean`                                                                                                                          |
| `structures`         | 清單                         | 無       | 結構 id，例如 `minecraft:village_plains`，或用 `#tag` 表示結構標籤，例如 `#minecraft:village`，在玩家位於其中某個部件內時計入；也可以是資源包透過 `structures` 放置的結構的名稱，在距其放置位置 `radius` 範圍內計入 |
| `radius`             | 整數                         | `32`     | 多近才算位於資源包自己的結構之內                                                                                                                                                                                    |
| `advancement`        | 文字                         | 無       | 進度 id                                                                                                                                                                                                             |
| `item`               | 文字                         | 無       | 物品，寫法與資源包中其他地方相同，例如 `minecraft:diamond_sword`                                                                                                                                                    |
| `entity`             | 文字                         | 無       | 實體 id，例如 `minecraft:zombie`                                                                                                                                                                                    |
| `count`              | 整數                         | `1`      | 用於 `kill`：需要擊殺多少次。規則觸發後重新開始計數                                                                                                                                                                 |
| `below`, `above`     | 整數                         | 無       | 用於 `y_level`：要降到其下或升到其上的高度                                                                                                                                                                          |
| `time`               | 整數                         | `0`      | 用於 `time_of_day`：一天中的刻                                                                                                                                                                                      |
| `day`                | 整數                         | 無       | 用於 `day`：唯一觸發的那一天。不寫則每天都觸發                                                                                                                                                                      |
| `minutes`            | 整數                         | 無       | 用於 `play_time`                                                                                                                                                                                                    |
| `roll`               | 文字                         | 無       | 用於 `roll`：`coin`、形如 `2d6` 的寫法（`d6` 與 `1d6` 相同），或資料包裡骰子、牌組的名稱。不填時，每次擲骰都會觸發該規則                                                                                            |
| `order`              | 文字                         | 無       | 用於 `order_done`：訂單的 `sign` 詞。不填時，每個完成的訂單都會觸發該規則                                                                                                                                           |
| `exposure`           | 文字                         | 無       | 用於 `exposure_level`：危險在 `exposures` 下的檔案名稱。不填時，每種危險都會觸發該規則                                                                                                                              |
| `level`              | 整數                         | 無       | 用於 `exposure_level`：僅當玩家的等級變為該值時觸發；`0` 表示不再暴露。不填時，每次變化都會觸發                                                                                                                     |
| `atLeast`, `atMost`  | 整數                         | 無       | 用於 `roll`：能觸發該規則的最小和最大總和。硬幣正面算 1、反面算 0，資料包裡的骰子或牌組則按結果在其檔案中的位置計，從 1 開始                                                                                        |
| `objective`, `score` | 文字，整數                   | 無，`1`  | 用於 `score`：計分項和要達到的值                                                                                                                                                                                    |
| `portway`            | 文字                         | 無       | 用於 `portway_*` 觸發：傳送通道的方塊 id，例如 `mypack:northgate`。不填則任何傳送通道都符合                                                                                                                         |
| `network`            | 文字                         | 無       | 用於 `portway_*` 觸發：傳送通道的網路。不填則任何網路都符合                                                                                                                                                         |
| `reason`             | 文字                         | 無       | 用於 `portway_refused`：`gate`、`dimension`、`cost`、`cooldown`、`nowhere`、`network`、`travel` 或 `item`；用於 `portway_forget`：`gone`、`player` 或 `command`。不填則任何原因都符合                               |
| `requires`           | 模組 id 或資源包命名空間清單 | 無       | 除非全部存在，否則跳過該檔案                                                                                                                                                                                        |

### 時機

*卡片*

`when` 包含在觸發器觸發的當下必須全部成立的條件。

| 設定                        | 類型       | 檢查內容                                                                             |
| --------------------------- | ---------- | ------------------------------------------------------------------------------------ |
| `biomes`                    | 清單       | 玩家位於這些生態域之一，寫法與觸發器相同                                             |
| `timeFrom`, `timeTo`        | 整數       | 日夜時鐘在此區間內，區間可以跨越午夜，例如 `13000` 到 `1000`                         |
| `dayAtLeast`                | 整數       | 天數至少為此值                                                                       |
| `advancement`               | 文字       | 玩家擁有此進度                                                                       |
| `portway`、`network`        | 文字       | 玩家的清單或全域站點中有一個具有該方塊 id 或屬於該網路的傳送通道。兩者都可以省略其一 |
| `gameMode`                  | 文字       | 玩家處於此遊戲模式：`survival`、`creative`、`adventure` 或 `spectator`               |
| `team`                      | 文字       | 玩家屬於此計分板隊伍                                                                 |
| `objective`, `scoreAtLeast` | 文字，整數 | 玩家在該目標上的分數至少為此值                                                       |

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
| `ticks`      | 整數   | `0`              | 卡片停留多久，包括淡出。`0` 沿用標準的 160                                                                                                            |
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

卡片字型是位於 `assets/<namespace>/font/<name>.json` 的字型定義，格式與遊戲自帶的字型相同，卡片的尺寸依該字型的字寬確定。`bitmap` 提供器若其 `file` 為 `<namespace>:font/<name>.png`、其 `chars` 為遊戲 `ascii.png` 的十六行，則讀取的 PNG 與 1.12.2 版本在 `assets/<namespace>/textures/font/<name>.png` 使用的是同一張，因此同一個資源包能在三個版本上繪製出相同的字樣。`minecraft:default` 指代遊戲的字型。沒有任何資源包提供的字型會退回為遊戲的字型，並在 `rdpl.log` 中記錄一則警告。

資源包可以透過提供自己的 `assets/resourcedatapackloader/font/rdpl.json`，或其所用的 `assets/resourcedatapackloader/textures/font/` 下的 PNG，來變更 RDPL 字型；兩者任一都會在所有地方取代它，包括遊戲自身的文字。遊戲自身的文字使用 RDPL 字型，是因為本模組附帶了 `assets/minecraft/font/default.json`，其中 RDPL 字型排在最前，其餘所有字元則落到遊戲自己的字型上。資源包的 `assets/minecraft/font/default.json` 先於本模組的被讀取，因此原版那份的副本會讓遊戲的文字恢復其自己的字型；RDPL 自己的文字仍使用 RDPL 字型，除非 `saysFont` 為 `minecraft:default`。

卡片的標題與各行、Says 訊息以及歡迎與保持提示，支援「世界介紹」「文字格式」下表中的行內標記：粗體、斜體、粗斜體、刪除線、程式碼、連結和符文段落。粗體段落以字型的 `_bold` 字形繪製，斜體段落以其 `_italic` 字形繪製；沒有該字形的字型，該段落採用遊戲的粗體或斜體樣式，卡片會依實際繪製的段落調整大小。沒有安裝本模組的玩家會得到同樣的標記作為聊天格式，符文段落則顯示為其原本的字母。

### 內建規則

*卡片*

| Id                       | 訊息                                           | 其文字來源                                                                                                                  |
| ------------------------ | ---------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| `rdpl:gate_unlocked`     | 閘門開啟                                       | [閘門](#閘門)中的 `unlockedMessage`                                                                                         |
| `rdpl:gate_blocked`      | 關閉的閘門把玩家擋回，顯示在動作列上           | [閘門](#閘門)中的 `blockedMessage`                                                                                          |
| `rdpl:portway_activated` | 玩家把一個傳送通道加入自己的清單，顯示在動作列 | 語言檔                                                                                                                      |
| `rdpl:portway_refused`   | 傳送通道拒絕了玩家，顯示在動作列               | 語言檔                                                                                                                      |
| `rdpl:portway_forgot`    | 一個站點離開玩家的清單，顯示在動作列           | 語言檔                                                                                                                      |
| `rdpl:team_joined`       | 玩家加入一個陣營                               | 該陣營的 `displayName`                                                                                                      |
| `rdpl:team_lead`         | 陣營的主導者交到某位玩家手上                   | [隊伍](#隊伍)中的 `leadSays`                                                                                                |
| `rdpl:team_picked`       | 玩家被選入一個陣營                             | 該陣營的 `displayName`                                                                                                      |
| `rdpl:team_round_ended`  | 回合結束，因此玩家被移到一個陣營               | 該陣營的 `displayName`                                                                                                      |
| `rdpl:lobby_joins`       | 在回合中途登入的玩家被送往大廳                 | [大廳](#大廳)中的 `opens.joinsSays`                                                                                         |
| `rdpl:lobby_note`        | 畫面中央的大廳提示行                           | [大廳](#大廳)中的 `opens.says`、`opens.leaderSays`                                                                          |
| `rdpl:scoring_results`   | 一回合結束時給每位玩家的排名                   | [結果](#結果)中的 `results.card`、`results.title`、`results.icon`、`results.image`、`results.background`、`results.seconds` |
| `rdpl:scoring_out`       | 被淘汰的玩家                                   | [回合如何結束](#回合如何結束)中的 `ends.outSays`                                                                            |
| `rdpl:reset_lead`        | 主導者重置回合                                 | [重置回合](#重置回合)中的 `reset.leadSays`                                                                                  |
| `rdpl:reset_vote`        | 發起重置投票                                   | `reset.voteSays`                                                                                                            |
| `rdpl:reset_pass`        | 投票通過                                       | `reset.passSays`                                                                                                            |
| `rdpl:reset_fail`        | 投票失敗                                       | `reset.failSays`                                                                                                            |
| `rdpl:anvil_waits`       | 鐵砧的加工等待某個進度                         | [鐵砧操作](#鐵砧操作)                                                                                                       |
| `rdpl:threat`            | 玩家的威脅等級改變                             | `threatSays`                                                                                                                |
| `rdpl:prospect`          | 探勘結果回報的每一行                           | 探勘結果                                                                                                                    |
| `rdpl:prospect_none`     | 探勘一無所獲                                   | 語言檔                                                                                                                      |
| `rdpl:board_result`      | 一局棋盤遊戲結束                               | 語言檔案                                                                                                                    |
| `rdpl:pregen_ended`      | 預先生成完成或停止                             | [預先生成](#預先生成)中的 `pregenFinishedSays`、`pregenStoppedSays`                                                         |
| `rdpl:pregen_running`    | 玩家在預先生成期間加入時看到的進度行           | `pregenRunningSays`                                                                                                         |

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

第一個回覆通往 `who` 節點，該節點有自己的 `title`，並有一個返回的回覆。第二個會執行一個函式並顯示一張卡片。第三個只有玩家處於冒險模式時才會顯示，最後一個沒有 `goto`，所以會結束對話。 較高的面板可以用滑鼠滾輪或右側邊緣的捲軸捲動。

### 對話鍵

*對話*

| 設定              | 類型                         | 預設             | 作用                                                                       |
| ----------------- | ---------------------------- | ---------------- | -------------------------------------------------------------------------- |
| `start`           | 文字                         | 無，必填         | 對話開始的節點名稱。`start` 不是其任何節點的對話會被捨棄                   |
| `nodes`           | 物件                         | 無，必填         | 各節點，每個都有自己的名稱。最多 64 個                                     |
| `title`           | 文字                         | 變體的名稱       | 面板的第一行。`{npc}` 是實體的名稱                                         |
| `icon`            | 文字                         | `saysIcon`       | 與卡片相同，見 [該卡片](#該卡片)                                           |
| `color`           | 文字                         | `saysColor`      | 與卡片相同，見 [該卡片](#該卡片)                                           |
| `image`           | 文字                         | `saysImage`      | 與卡片相同，見 [該卡片](#該卡片)                                           |
| `background`      | 布林值                       | `saysBackground` | 與卡片相同，見 [該卡片](#該卡片)                                           |
| `font`            | 文字                         | `saysFont`       | 與卡片相同，見 [該卡片](#該卡片)                                           |
| `titleColor`      | 文字                         | `FFFFFF`         | 標題的顏色，使用十六進位代碼                                               |
| `textColor`       | 文字                         | `E8E8E8`         | 內文行的顏色，使用十六進位代碼                                             |
| `replyColor`      | 文字                         | `FFFFFF`         | 回覆的顏色，使用十六進位代碼                                               |
| `hoverColor`      | 文字                         | `FFFF55`         | 滑鼠所指回覆的顏色，使用十六進位代碼                                       |
| `hoverBackground` | 文字                         | `FFFFFF`         | 滑鼠所指回覆背後色帶的顏色，使用十六進位代碼。色帶的不透明度為 25%         |
| `history`         | 布林值                       | `true`           | 顯示 `<` 和 `>` 按鈕，用來翻回這次對話中已經顯示過的行。`false` 會移除它們 |
| `requires`        | 模組 id 或資源包命名空間清單 | 無               | 除非全部存在，否則跳過該檔案                                               |

### 節點鍵

*對話*

| 設定      | 類型 | 預設       | 作用                                                                    |
| --------- | ---- | ---------- | ----------------------------------------------------------------------- |
| `title`   | 文字 | 對話的標題 | 在顯示此節點期間，取代對話的 `title`                                    |
| `lines`   | 清單 | 無         | 最多十六行。`{player}`、`{npc}`、`{dim}`、`{biome}` 和 `{day}` 會被填入 |
| `replies` | 清單 | 無         | 最多八個回覆，以編號清單顯示。沒有回覆的節點只顯示它的行，對話到此結束  |

### 回覆鍵

*對話*

| 設定    | 類型   | 預設                    | 作用                                                                                                      |
| ------- | ------ | ----------------------- | --------------------------------------------------------------------------------------------------------- |
| `text`  | 文字   | 無，必填                | 玩家讀到的回覆文字。沒有 `text` 的回覆會被捨棄                                                            |
| `goto`  | 文字   | 無                      | 接下來前往的節點名稱。`goto` 沒有指向任何節點的回覆會被捨棄                                               |
| `card`  | 文字   | 無                      | 選擇該回覆時顯示的 [卡片](#卡片) 中的規則 id。其 `when`、`repeat` 和 `cooldown` 會被略過，`runs` 仍會執行 |
| `runs`  | 文字   | 無                      | 選擇該回覆時以玩家身分執行的函式。回覆就是這樣執行指令的                                                  |
| `when`  | 物件   | 無                      | [時機](#時機) 中的條件。不符合條件的回覆會被隱藏                                                          |
| `close` | 布林值 | 沒有 `goto` 時為 `true` | 選擇後結束對話。設定了 `goto` 時不起作用                                                                  |

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
    "coin": "{player} tosses the old coin: {result}",
    "pickplayer": "{player} picks a player: {result}",
    "pickteam": "{player} picks a team: {result}",
    "heads": "heads",
    "tails": "tails",
    "lastnone": "No rolls yet",
    "nobody": "There is nobody to pick from",
    "notallowed": "You are not allowed to run that part of /rdplserver game",
    "usage": "/rdplserver game <coin|die|dice|deck|teamroll|tiebreak>",
    "die": "{player} rolls {dice}: {result}",
    "packdie": "{player} rolls {die}: {result}",
    "dice": "{player} rolls {dice}: {rolls} = {result}",
    "advantage": "{player} rolls {dice} with advantage: {first} and {second}, keeps {result}",
    "disadvantage": "{player} rolls {dice} with disadvantage: {first} and {second}, keeps {result}",
    "pickmember": "{player} picks from {team}: {result}",
    "draw": "{player} draws from {deck}: {result} ({left} left)",
    "reshuffled": "{deck} was empty, so it is reshuffled: {player} draws {result} ({left} left)",
    "shuffle": "{player} shuffles {deck}: {left} cards",
    "left": "{deck} has {left} card(s) left",
    "empty": "{deck} is empty, so shuffle it first",
    "nodeck": "There is no deck called {deck}",
    "teamroll": "{member} rolls {dice}: {result}",
    "teamrollwin": "{result} wins the team roll with {score}",
    "tiebreak": "The tie in {objective} between {sides} goes to {result}",
    "notie": "{objective} has no tie at the top",
    "noobjective": "There is no objective called {objective}",
    "turn": "{group}'s turn: {seconds} seconds",
    "turnclock": "{group}'s turn: {seconds}",
    "turnwarn": "{group}: {seconds} seconds left",
    "turnout": "{group} ran out of time",
    "turnpass": "{group} passes the turn",
    "turngap": "Next turn in {seconds}",
    "notturn": "It is not your turn",
    "noturns": "No turns are running",
    "badsides": "A die has 2 to 1000 sides, not {sides}",
    "badroll": "'{roll}' is not a roll such as 2d6+1, with 1 to 100 dice of 2 to 1000 sides",
    "badaudience": "'{audience}' is not self, team, all, radius <blocks> or silent",
    "nodie": "There is no die called {name}",
    "noteam": "There is no team called {team}"
  }
}
```

| 鍵         | 類型 | 預設  | 作用                                                                                                                                                                                                        |
| ---------- | ---- | ----- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `audience` | 文字 | `all` | 沒有指定聽眾的擲骰由誰聽到：`self`, `team`, `all`, `radius <blocks>` 或 `silent`。以第一個設定它的資源包為準；之後的會寫入日誌                                                                              |
| `dice`     | 物件 | 空    | 骰子名稱對應一個由面和權重組成的物件。權重為 2 的面出現的次數是權重為 1 的兩倍。權重是從 1 起的整數                                                                                                         |
| `decks`    | 物件 | 空    | 牌組名稱對應它的卡牌清單，或對應物件 `{ "cards": [...], "reshuffle": false }`。`reshuffle` 不寫時為 `true`：從空牌組抽牌會把所有牌重新洗好再抽。設為 `false` 時，牌組保持為空，直到執行 `game deck shuffle` |
| `says`     | 物件 | 空    | 文字鍵對應在所有語言中取代本模組自帶文字的文字。鍵及其佔位符見下                                                                                                                                            |

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
  "variants": { "dice_pair": { "maxSize": 16 } }
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

| 文字鍵                                                        | 佔位符                                                       |
| ------------------------------------------------------------- | ------------------------------------------------------------ |
| `coin`, `pickplayer`, `pickteam`                              | `{player}`, `{result}`                                       |
| `heads`, `tails`, `lastnone`, `nobody`, `notallowed`, `usage` | 無                                                           |
| `die`                                                         | `{player}`, `{dice}`, `{sides}`, `{result}`                  |
| `packdie`                                                     | `{player}`, `{die}`, `{result}`                              |
| `dice`                                                        | `{player}`, `{dice}`, `{rolls}`, `{result}`                  |
| `advantage`, `disadvantage`                                   | `{player}`, `{dice}`, `{first}`, `{second}`, `{result}`      |
| `pickmember`                                                  | `{player}`, `{team}`, `{result}`                             |
| `draw`, `reshuffled`                                          | `{player}`, `{deck}`, `{result}`, `{left}`                   |
| `shuffle`                                                     | `{player}`, `{deck}`, `{left}`                               |
| `left`, `empty`, `nodeck`                                     | `{deck}`，`left` 另有 `{left}`                               |
| `teamroll`                                                    | `{member}`, `{dice}`, `{result}`                             |
| `teamrollwin`                                                 | `{result}`, `{score}`                                        |
| `tiebreak`                                                    | `{objective}`, `{sides}`, `{result}`                         |
| `notie`, `noobjective`                                        | `{objective}`                                                |
| `turn`, `turnclock`, `turnwarn`                               | `{group}`, `{seconds}`                                       |
| `turnout`, `turnpass`                                         | `{group}`                                                    |
| `turngap`                                                     | `{seconds}`                                                  |
| `notturn`, `noturns`                                          | 無                                                           |
| `badsides`, `badroll`, `badaudience`, `nodie`, `noteam`       | 依序為 `{sides}`, `{roll}`, `{audience}`, `{name}`, `{team}` |

模組自帶的措辭位於其語言檔案中，鍵為 `rdpl.game.<key>`，因此資源包也可以逐一語言更改它。

## 桌上遊戲

*遊戲模式*

`<namespace>/games/*.json`

一個檔案就是一款桌上遊戲，以檔案名稱命名。它設定棋盤、雙方、棋子及其走法、開局局面，以及結果的獎勵。`game board start <game> <board>` 會在發送者所站的位置或指定的位置擺出棋盤：格子鋪在下方一格，直行向東延伸，橫列向南延伸，每個棋子都是資源包選定的生物，靜止不動、無聲且不受傷害，並以所屬陣營命名。被吃掉的棋子站在棋盤旁邊。

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
  "quiet": false,
  "result": { "objective": "boardwins", "win": 3, "draw": 1, "loss": -1 }
}
```

| 鍵       | 類型          | 預設             | 作用                                                                                               |
| -------- | ------------- | ---------------- | -------------------------------------------------------------------------------------------------- |
| `name`   | 文字          | 檔案名稱         | 在聊天和結果中顯示的名稱                                                                           |
| `board`  | 物件          | 8 × 8            | `files` 和 `ranks`，各 2 到 16，以及鋪在格子下面的方塊狀態 `light` 和 `dark`。不寫時地面保持原樣   |
| `sides`  | 清單          | `White`, `Black` | 兩個由 `name` 和 `color`（聊天顏色）組成的物件。第一方先走，用大寫字母擺子                         |
| `pieces` | 物件          | 無               | 棋子名稱對應一個由下方棋子鍵組成的物件                                                             |
| `setup`  | 清單          | 無               | 每行一段文字，第 1 行在前。棋子的字母放置該棋子，第一方用大寫，第二方用小寫，`.` 留空該格          |
| `rules`  | 物件          | 無               | `mustCapture`, `chainCaptures`, `quietDraw` 和 `repeatDraw`，見下                                  |
| `clock`  | 物件          | 無               | 每方的 `minutes`，以及每步後加上的 `addSeconds`。用完時間的一方判負                                |
| `ai`     | 數字          | `2`              | 電腦的等級，1 到 4，用於無人執掌的一方                                                             |
| `result` | 物件          | 無               | 對局結束時發放的 `objective`，以及 `win`, `draw`, `loss` 分數，不寫時為 1、0、0                    |
| `quiet`  | true 或 false | `false`          | 不在聊天中發送走棋、選子和將軍的訊息。拒絕提示和對局結果照常發送，每一步也照常寫入 `logs/rdpl.log` |

| 棋子鍵      | 類型          | 作用                                                                                                                                                                                                                                   |
| ----------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `letter`    | 文字          | 在 `setup` 和 `game board show` 中使用的字母                                                                                                                                                                                           |
| `value`     | 數字          | 它對電腦而言的價值                                                                                                                                                                                                                     |
| `mobs`      | 清單          | 每一方的生物，第一方在前。`mob` 為雙方給出同一個                                                                                                                                                                                       |
| `moves`     | 清單          | 以物件表示的步法：`steps`，從本方一端看的 `[file, rank]` 偏移清單；`slides`，一直走到被擋住為止；`captures`，`both`（預設）、`never`、`only` 或 `hop`，後者跳過對方的一枚棋子落到其後的空格並吃掉它；以及 `firstRange`，首步可以走幾步 |
| `royal`     | true 或 false | 這枚棋子被將死時該方判負，且任何一步都不能讓它處於被攻擊狀態。沒有王棋時，無棋可走的一方判負                                                                                                                                           |
| `enPassant` | true 或 false | 可以吃掉剛從它旁邊走過兩格的棋子                                                                                                                                                                                                       |
| `castles`   | 文字          | 一個搭檔棋子：這枚棋子朝未動過的搭檔走兩格，搭檔跳過它                                                                                                                                                                                 |
| `promotes`  | 清單          | 到達底線時可以變成什麼。除非著法指定其他，否則用第一個                                                                                                                                                                                 |

| 規則            | 作用                                                                                                                               |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| `mustCapture`   | 能吃子的一方必須吃子                                                                                                               |
| `chainCaptures` | 吃子的跳躍之後，同一枚棋子只要還能吃就繼續吃                                                                                       |
| `quietDraw`     | 雙方合計連續多少步沒有吃子、也沒有可升變棋子走動之後，對局判和。0 表示永不判和                                                     |
| `repeatDraw`    | 同一局面在同一方走棋時出現這麼多次後，對局判和；3 即西洋棋的三次重複局面。只計算上次吃子或可升變棋子走動之後的局面。0 表示永不判和 |

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

這些群組是 `ores`, `generators`, `biomes`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `replacements`, `villages`, `entities`, `chunks`, `blastPlaster`, `commands` 和 `server`。

設定依 **生態域區段 → 世界範本 → 設定檔** 的順序解析。世界範本的 `settings` 區段使用與設定檔相同的鍵名，因此資源包設定它們的方式與你自己設定的相同：

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

某群組的 control 為 `default` 時，這些設定生效；為 `global` 時被忽略；為 `off` 時，無論任何資源包怎麼寫，整個群組都不起作用。範本中寫了但沒有任何東西讀取的鍵，會在日誌中警告一次，`biomes` 區段中不屬於村莊設定的鍵也一樣。

設定檔是 `config/resourcedatapackloader-common.toml`。下面的每個鍵在其中都使用相同的名稱，位於各自的類別下，清單寫成遊戲設定格式所用的 TOML 清單。

## 各群組的作用

*控制*

下面的每項設定都透過其所在的群組讀取，因此該群組的 `control` 鍵決定由資源包還是設定檔擁有最終決定權。資源包可以設定的項目，會以相同的名稱出現在世界範本的 `settings` 區段中；標註為 **僅限設定檔** 的項目只從設定檔中讀取，資源包寫了會被警告並忽略。預設值是設定檔中的預設值。

### 礦石

*各群組的作用*

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

此群組由 `control.ores` 控制。依模組和礦石類型阻止礦石生成。

| 設定                             | 類型            | 預設            | 作用                                                                                                                                                                                                                                                                                                 |
| -------------------------------- | --------------- | --------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blockOres`                      | 布林值          | `false`         | 阻止所有模組以及 Minecraft 本身生成礦石。只有 oreWhitelist 中的模組仍會生成。礦石是指建立在遊戲的礦石或散佈礦石地物上的放置地物，也就是 1.12.2 為其觸發礦石事件的那些，涵蓋 Minecraft 和大多數模組的礦石、泥土、礫石和各種石頭。資源包自己的世界生成項目絕不會被阻止，無論是 blockOres 還是 oreTypes |
| `logBlockedOres`                 | 布林值          | `true`          | 每個模組和礦石類型第一次被阻止時記入日誌                                                                                                                                                                                                                                                             |
| `oreWhitelist`                   | 模組 id 清單    | `["minecraft"]` | `blockOres` 開啟時仍允許生成礦石的模組                                                                                                                                                                                                                                                               |
| `prospectItems`                  | 清單            | 空              | 用來探勘礦脈狀世界生成項目的物品，潛行的玩家用其破壞方塊時觸發，寫作 item=entry\|entry[,以區塊計的半徑] 或 item=*[,半徑]，例如 minecraft:compass=iron_vein\|coal_seam 或 mypack:rod=*,12。讀數會給出礦石名稱和一個羅盤方位                                                                           |
| `prospectItemsAreBlacklist`      | 布林值          | `false`         | 開啟時，每個物品的清單是它不讀取的項目                                                                                                                                                                                                                                                               |
| `prospectDrops`                  | 布林值          | `false`         | 開啟時，在探勘模式下破壞的方塊仍會掉落並給予經驗值。關閉時，樣本被消耗掉                                                                                                                                                                                                                             |
| `prospectSlow`                   | 整數，1 到 100  | `2`             | 手持帶標記物品的潛行玩家破壞一個方塊要花多少倍的時間。`1` 為正常速度                                                                                                                                                                                                                                 |
| `prospectWear`                   | 整數，2 到 1000 | `2`             | 一次探勘破壞對工具造成正常磨損的多少倍。`2`，即兩倍，是允許的最小值，沒有耐久度的物品不付出代價                                                                                                                                                                                                      |
| `oreTypes`                       | 清單            | 空              | 本設定適用的礦石類型，無論由誰生成、白名單如何規定。已知類型：COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH，其他任何礦石用 CUSTOM                                                                           |
| `oreTypesAreBlacklist`           | 布林值          | `true`          | 開啟時，`oreTypes` 中的類型被阻止。關閉時，只有這些類型會生成                                                                                                                                                                                                                                        |
| `blockOreDimensions`             | 清單            | 空              | 礦石阻止適用的維度，留空表示所有維度。範圍之外的維度完全不受影響，因此主世界仍被阻止時，其他模組的礦石仍會在那裡生成                                                                                                                                                                                 |
| `blockOreDimensionsAreBlacklist` | 布林值          | `false`         | 開啟時，所列維度不受礦石阻止影響                                                                                                                                                                                                                                                                     |

### 生態域

*各群組的作用*

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

此群組由 `control.biomes` 控制。依模組和名稱阻止生態域，以及用什麼來取代它們。

| 設定                               | 類型         | 預設                      | 作用                                                                                                                                                                                                                              |
| ---------------------------------- | ------------ | ------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `logBlockedBiomes`                 | 布林值       | `true`                    | 依模組統計並記錄哪些生態域被阻止                                                                                                                                                                                                  |
| `blockBiomes`                      | 布林值       | `false`                   | 阻止除 biomeWhitelist 中的模組之外的所有生態域生成。被阻止的生態域變成虛空生態域，或世界範本的 roles 和 fallback 所指定的生態域。當世界範本為虛空、沒有 roles 且預設為虛空時，阻止所有生態域會使 voidWorldDimensions 成為虛空世界 |
| `biomeWhitelist`                   | 模組 id 清單 | `["minecraft"]`           | `blockBiomes` 開啟時，其生態域仍會生成的模組。資源包的生態域使用該資源包的命名空間                                                                                                                                                |
| `biomeNames`                       | 清單         | 空                        | 本設定適用的生態域，依 id（如 minecraft:birch_forest）或依遊戲顯示的名稱（如 Birch Forest）。作為黑名單時，無論歸誰所有都會被阻止。作為白名單時，所列生態域在 blockBiomes 開啟時仍需要它的模組位於 biomeWhitelist 中              |
| `biomeNamesAreBlacklist`           | 布林值       | `true`                    | 開啟時，`biomeNames` 中的名稱被阻止。關閉時，只有這些名稱會生成                                                                                                                                                                   |
| `blockBiomeDimensions`             | 清單         | `["minecraft:overworld"]` | 生態域阻止適用的維度。留空表示所有維度                                                                                                                                                                                            |
| `blockBiomeDimensionsAreBlacklist` | 布林值       | `false`                   | 開啟時，阻止會略過所列維度。關閉時，只對它們生效                                                                                                                                                                                  |

### 生成器

*各群組的作用*

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

此群組由 `control.generators` 控制。依模組及其生成的內容，阻止其他模組的世界生成。生成器是一個放置地物，歸其 id 的命名空間所有；Minecraft 自己的地物、本模組的地物以及資源包的世界生成項目絕不會被阻止。

| 設定                                   | 類型                     | 預設                      | 作用                                                                                                                                                                                                                               |
| -------------------------------------- | ------------------------ | ------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blockWorldGenerators`                 | 布林值                   | `false`                   | 阻止所有模組透過其自己的放置地物進行生成，模組就是這樣新增史萊姆島、洞穴水晶之類的東西。只有 generatorWhitelist 中的模組仍會生成                                                                                                   |
| `generatorWhitelist`                   | 模組 id 清單             | `["minecraft"]`           | `blockWorldGenerators` 開啟時仍允許生成的模組                                                                                                                                                                                      |
| `blockedGenerators`                    | 模組 id 或 id 片段的清單 | 空                        | 直接被阻止的個別生成器，無論白名單如何規定，依模組 id 或放置地物 id 的一部分指定                                                                                                                                                   |
| `blockGeneratorDimensions`             | 清單                     | `["minecraft:overworld"]` | 生成器阻止適用的維度。留空表示所有維度                                                                                                                                                                                             |
| `blockGeneratorDimensionsAreBlacklist` | 布林值                   | `false`                   | 開啟時，阻止會略過所列維度。關閉時，只對它們生效                                                                                                                                                                                   |
| `generatorTypes`                       | 清單                     | 空                        | 本設定適用的類型，無論生成器歸誰所有、白名單如何規定：`ores`, `structures`, `flora`, `lakes`, `terrain`，或 `unknown` 表示沒有任何詞匹配的生成器。類型取自地物 id 中的詞，因此 `crystal_ore` 是 ores，`slime_island` 是 structures |
| `generatorTypesAreBlacklist`           | 布林值                   | `true`                    | 開啟時，`generatorTypes` 中的類型被阻止。關閉時，只有這些類型會生成                                                                                                                                                                |
| `generatorTypeMap`                     | `pattern=type` 的清單    | 空                        | 為 id 無法說明其類型的生成器指定類型，pattern 是模組 id 或地物 id 的一部分，例如 mymod=ores。對應項目會先於內建的詞檢查，因此它們也能糾正內建詞語判斷錯誤的生成器                                                                  |
| `logBlockedGenerators`                 | 布林值                   | `true`                    | 每個生成器第一次被阻止時，連同賦予它的類型一起記入日誌。`/rdplserver generators` 依模組和類型顯示累計總數                                                                                                                          |

### 取代

*各群組的作用*

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

此群組由 `control.replacements` 控制。對已經存在的區塊進行方塊替換。

| 設定                                     | 類型                | 預設    | 作用                                                                                                                                                                                                    |
| ---------------------------------------- | ------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blockReplacements`                      | 清單                | 空      | 區塊載入時被換出的方塊，寫作 block=block，兩側都可帶選填的狀態，例如 minecraft:andesite=minecraft:stone 或 minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]。每個區塊只處理一次，新區塊也包括在內 |
| `blockReplacementDimensions`             | 清單                | 空      | 本設定適用的維度。留空表示所有維度                                                                                                                                                                      |
| `blockReplacementDimensionsAreBlacklist` | 布林值              | `false` | 開啟時，替換會略過所列維度。關閉時，只對它們生效                                                                                                                                                        |
| `blockReplacementMinHeight`              | 整數，-2032 到 2031 | `-64`   | 尋找的最低 y                                                                                                                                                                                            |
| `blockReplacementMaxHeight`              | 整數，-2032 到 2031 | `319`   | 尋找的最高 y                                                                                                                                                                                            |
| `blockReplacementKey`                    | 字串                | `0000`  | 變更它，每個區塊都會重新經歷一次替換                                                                                                                                                                    |
| `logBlockReplacements`                   | 布林值              | `true`  | 每次替換第一次發生時記入日誌，世界追上進度時再記一個總數                                                                                                                                                |

### 村莊與城市

*各群組的作用*

此群組由 `control.villages` 控制。資源包所鋪設的城市和村莊街道：它們的形狀、裝飾、橋梁、隧道、鐵軌、地塊和廣場。

#### 村莊道路

*村莊與城市*

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

**混合方塊。** 有些方塊設定接受一個混合，而不是單一方塊：方塊之間用逗號隔開，每個方塊後面跟一個空格和一個權重，如 `"minecraft:stone_bricks 3, minecraft:cobblestone 1"`。沒有權重的方塊計一次。每放置一個方塊，都會根據世界種子碼和它所在的位置對混合進行抽取，因此同一個世界總會建出相同的圖案。接受混合的設定有 `villagePathVergeBlock`、`villagePathVergeWaterBlock`、`villagePathTunnelBlock`、`villagePathBridgeFrameBlock`、`villagePathBridgeFrameTopBlock`、`villageRailTunnelBlock`、`villageRailDeckBlock`、`villageRailSupportBlock`、`villageRailBarrierBlock`、`villageRailBridgeFrameBlock`、`villageRailBridgeFrameTopBlock`、`villageSubwayTunnelBlock`、`villageSubwayPlatformBlock`、`villageSubwayRailingBlock`、`villageSubwayBenchEndBlock` 和 `villageSewerMossBlock`。其他所有方塊設定在各處都只使用混合中的第一個方塊。

| 設定                       | 類型                 | 預設                   | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| -------------------------- | -------------------- | ---------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villagePathBlock`         | 文字                 | 空                     | terrainAdaptation 鋪設城市道路時的路面。留空則保留生態域本來會使用的方塊：沙子上用砂岩，惡地上用陶土，泥土上用土徑                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `villagePathExtraWidth`    | 整數，0 或更大       | `0`                    | terrainAdaptation 鋪設道路時，每側在通常的 3 格之外額外增加的道路寬度。這會加寬街道本身，因此街道之間的街廓會從寬路旁退後                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `villageBlockSizes`        | `size=weight` 的清單 | 空                     | 城市平行街道之間的街廓有多深，根據廣場位置為每個街區抽取一次。留空則把每個街廓都設為資源包所帶最大地塊的大小                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `villageCitySpacing`       | 整數，0 到 256       | `16`                   | 城市街區之間的排定間隔，以街區為單位，街區的大小由地塊決定（最大地塊的兩倍，加上一個廣場和兩側各一條街道，向上取整到 16 格，至少 96）：每個這麼多街區組成的方格中有一個帶有城市，為 1 時每個街區都是城市，即一個中心有水井的廣場，以及從中伸出、與下一個街區的街道相接的街道。0 表示不排定任何城市。資源包沒有設定它時，由 `structureSpacing` 的 villages=chunks 來設定，至少 9 個區塊。每個方格把城市建在它最平坦的街區上，高低差不超過 10 格，位於村莊生態域中（由 `structureBiomes` 的 villages= 選擇），且不在林地府邸可能起始之處；`structureSeparation`、`structureMinDistanceFromSpawn` 和 `structureMost` 的 villages= 讓城市彼此保持距離，就像它們對村莊所做的那樣，`structureAt` 的 villages=x,z 則只在它所固定的位置建立城市。城市只會向其第一口水井附近範圍內、地面起伏不超過 6 格且高於水位線的街區擴展，地塊和水井合計不超過兩個的城市不會被建造 |
| `villagePathAlleyBlock`    | 方塊                 | 空                     | 小巷的路面，小巷是窄到無法容納線條和人行道的道路。小巷穿行於其所連接街道的人行道之間，自身沒有人行道，且在與街道相接處不會畫出行人穿越道。留空則用道路方塊鋪設小巷                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `villagePathAlleyChance`   | 整數，0 或更大       | `0`                    | 一條街道被鋪成小巷而不是全寬的百分比機率。0 表示不鋪設小巷                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `villagePathMinimumWidth`  | 整數，0 或更大       | `0`                    | 允許的最窄街道。會被鋪得比這更窄的街道根本不鋪，街區圍繞這個缺口佈局。0 表示從不拒絕                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `villagePathFlatRun`       | 整數，0 或更大       | `6`                    | 街道每保持一個坡度至少這麼多格才升降一級，以世界座標為錨，使各段之間保持一致。0 或 1 允許街道每格升降一次                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `villagePlotsLeast`        | 整數，0 或更大       | `0`                    | 城市要擴展到多少個地塊：圍繞其中心一圈一圈地新增街區，直到它們至少容納這麼多個，但絕不超過 villagePlotsMost。0 表示只鋪設中心街區                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `villagePlotsMost`         | 整數，0 或更大       | `0`                    | 城市最多可以容納多少個地塊：擴展會在將超過它的那個街區之前停止，任何街區都不會再安置更多，而達到該數目的街區會省去沒有地塊正對著的小巷。0 表示不設上限                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `villagePlotsBackRow`      | 布林值               | `true`                 | 村莊擴展完成後，第二輪會在每個正對著街道的地塊正後方安置一個地塊，朝向它，使用相同的抽取和相同的空間測試，這樣兩條街道之間街廓的內部會被蓋滿，而不是空著                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `villageTieStreets`        | 布林值               | `true`                 | 開啟時，無法把街道擴展到既有村莊的街區，會鋪設一條筆直的連接街道，通向與它對齊的最近的街道。關閉時，這樣的街區會被拆回                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `villageLayout`            | 文字                 | 空                     | 取代街區規劃而鋪設的城市地圖，命名形如 mypack:downtown，從該資源包的 citymaps 資料夾讀取。留空則照常規劃街區                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `villagePathCenterBlock`   | 方塊                 | 空                     | 道路正中的一條中線。留空則不畫                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `villagePathCenterDash`    | 整數，0 或更大       | `0`                    | 讓該線呈虛線：N 格線，然後 1 格路面。以世界座標為錨，因此一段道路的虛線會延續到下一段。`0` 表示保持實線                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `villagePathLineBlock`     | 方塊                 | 空                     | 道路與人行道之間的邊線。留空則不畫                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `villagePathSidewalkBlock` | 方塊                 | 空                     | 人行道，與道路齊平，鋪在邊線之外。留空則不鋪                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `villagePathSidewalkWidth` | 整數，0 或更大       | `2`                    | 每條人行道多寬，在設定了 `villagePathSidewalkBlock` 之後生效                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `villagePathLampBlock`     | 文字                 | `minecraft:oak_fence`  | 街邊燈柱所用的方塊，在路緣上堆疊 villagePathLampHeight 高。街道或小巷的每一端各立一根，另一條街道與之相接處立一根，其間每隔 7 到 12 格立一根，立在較低的一側，只有較低一側沒有空間時才立在另一側，盡頭路的邊緣則圍成一圈。橋上、隧道中以及距門兩格以內都不立。留空則不立燈柱                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `villagePathLampHeight`    | 整數，1 或更大       | `3`                    | 燈柱在燈頭之前有幾格高                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `villagePathLampTopBlock`  | 方塊                 | `minecraft:black_wool` | 柱頂的燈頭。留空則讓它光禿禿的                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `villagePathLampSideBlock` | 方塊                 | `minecraft:torch`      | 掛在燈頭兩側、朝外的光源。留空則不掛                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `villagePathLampStructure` | 文字                 | 空                     | 作為整根燈柱放置的結構檔案，取代堆疊那三種燈柱方塊，命名形如 `mypack:street_lamp`，從該資源包的 `structures` 資料夾讀取。它以燈位為中心，最低一層位於路緣上，它所放置的方塊被鎖定，不會被其他東西覆蓋。留空則堆疊方塊                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `villageWellStructure`     | 清單                 | 空                     | 作為每個廣場中心裝飾放置的結構檔案，每行一個帶權重的項目，寫作 name=weight，如 mypack:plaza_spire=3，每個廣場抽取一次。它以一個六格見方、清理並以 villagePathBlock 鋪平的區域為中心，其最低一層位於該地面上。留空、空份額，或結構無法載入時，會在那裡建造遊戲自己的水井。不是 name=weight 寫法的項目會被略過                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `villagePathDeadEnds`      | 清單                 | 空                     | 斷頭街道如何封口，每行一個項目，依每個盡頭分別抽取：sidewalk 用人行道方塊鋪滿盡頭那一排，barrier 沿它立起 villagePathBridgeBarrierBlock，高 villagePathBridgeBarrierHeight；其他任何項目都被略過。它們只封沒有長出盡頭路的盡頭，沒有設定方塊的樣式會退出抽取，小巷的盡頭只採用 barrier。留空則讓這類盡頭敞開                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `villagePathIntersects`    | 清單                 | 空                     | 畫在路口的設計，依註冊表鍵命名，取自資源包的 `<namespace>/pathintersects/`。一個項目讓每個路口都畫得一樣；多個項目則依權重為每個路口挑選                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |

**路口設計。** `villagePathIntersects` 指定資源包附帶的檔案，每個檔案都是一幅小圖，描繪道路相交處要畫什麼，以單一字元組成的行來繪製，一個字元對應一個方塊。

`<namespace>/pathintersects/*.json`

檔案的路徑就是該設計的登錄鍵，`villagePathIntersects` 隨後用它來指定設計。

```json
{
  "name": "Crosswalk",
  "weight": 3,
  "legend": { "w": "minecraft:quartz_block", "y": "minecraft:yellow_wool" },
  "mouth": ["wwww", "....", "wwww"],
  "corner": ["yy.", "y..", "..."]
}
```

| 鍵       | 值                     | 預設     | 作用                                                                                                                               |
| -------- | ---------------------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| `name`   | 字串                   | 檔案名稱 | 設計的標籤，不影響它的繪製方式                                                                                                     |
| `weight` | 整數，1 及以上         | `1`      | 列出多個設計時，該設計在路口中所佔的份額                                                                                           |
| `legend` | 單一字元對應方塊的物件 | 無       | 行中除下列角色字元之外還可使用的字元。已經是角色字元的字元會被拒絕，並記入一行日誌                                                 |
| `mouth`  | 字串清單               | 無       | 畫在每個入口上的行，位於相交道路之外。第一行最靠近路口，其餘的向外延伸。字元沿道路橫向排列，若某行比道路寬度短則重複               |
| `corner` | 字串清單               | 無       | 畫在路口內部的行。第一行最靠近相交道路的邊緣，而在一行之中，第一個字元最靠近道路自身的邊緣，向內依序排列。圖案未涵蓋的格子保持不變 |

有五個字元是角色而不是方塊，因此它們會跟隨道路已有的裝飾：`r` 是路面，`l` 是邊線，`s` 是人行道，`.` 讓方塊完全保持原樣，`c` 為保留字元，畫成路面。資源包從未設定其方塊的角色會退回為路面，而任何其他字元都會在 `legend` 中尋找，同樣退回為路面。既沒有 `mouth` 也沒有 `corner` 的設計會被丟棄，並記入一行日誌。

路口採用哪個設計，由世界種子碼和路口自身的位置算出，因此同一個世界總是畫出相同的路口。設計只在三條或更多街道相交處繪製，即路口或水井廣場；兩條街道相交只會呈現為一個普通的彎角。

#### 村莊橋梁與碼頭

*村莊與城市*

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

| 設定                             | 類型           | 預設                                       | 作用                                                                                                                                                                                                                                                               |
| -------------------------------- | -------------- | ------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `villagePathSupportBlock`        | 文字           | 空                                         | 地面為裸露岩石之處的路面本身，以及水上街道之下的橋墩和橋腳。留空則保留原版的礫石，沙漠城市用砂岩                                                                                                                                                                   |
| `villagePathBridgeBlock`         | 文字           | 空                                         | 街道或碼頭跨水所用的方塊。留空則用村莊木材的木板鋪面：莽原村莊用金合歡，針葉林村莊用雲杉，其他地方用橡木                                                                                                                                                           |
| `villagePathBridgeSidewalkBlock` | 方塊           | 空                                         | 道路跨水處人行道的橋面鋪裝。留空則讓一般的人行道方塊延伸過去                                                                                                                                                                                                       |
| `villagePathBridgeBarrierBlock`  | 方塊           | 空                                         | 沿橋面兩側邊緣堆疊的護欄。橋面落在地面上之處不設。留空則不建                                                                                                                                                                                                       |
| `villagePathBridgeBarrierHeight` | 整數，1 或更大 | `1`                                        | 這些護欄有幾格高                                                                                                                                                                                                                                                   |
| `villagePathBridgeDrop`          | 整數，0 或更大 | `0`                                        | 道路的坡面必須離地多高，其下的落差才會被架橋跨過，而不是被填實。`0` 讓道路貼著地面：它們只跨水，別的不跨。`3` 是鐵路棧橋所遵循的規則。這會改變坡面，而不只是裝飾                                                                                                   |
| `villagePathVergeBlock`          | 文字           | 空                                         | 城市必須造地之處，街道旁和地塊下的地面所填的方塊：地塊之間的溝槽，以及跨越缺口通向街道的填土，該處的街道若是橋，則改用 villagePathBridgeBlock 鋪面。留空則跟隨其所在的地面：沙子、陶土、礫石，或在本來會是泥土之處用頂部帶草的泥土                                 |
| `villagePathVergeWaterBlock`     | 方塊           | `minecraft:oak_planks`                     | 該填土位於水上時變成什麼，這樣延伸到湖上的路肩就不會是一根泥土柱。它也會裝飾留在水上的石質門階                                                                                                                                                                     |
| `villagePathBridgeFrameBlock`    | 方塊           | 空                                         | 長橋上方的框架：橋面每側一根立柱，頂上一根橫樑。每個框架都帶有一個向下通到橋面下地面的橋墩，其所在那一排不豎燈柱。留空則不建                                                                                                                                       |
| `villagePathBridgeFrameTopBlock` | 方塊           | 空                                         | 該框架頂上的橫樑。留空則使用 `villagePathBridgeFrameBlock`                                                                                                                                                                                                         |
| `villagePathBridgeFrameHeight`   | 整數，1 或更大 | `4`                                        | 框架在橋面上方留出多少格淨空，橫樑位於其上一格                                                                                                                                                                                                                     |
| `villagePathBridgeFrameRun`      | 整數，1 或更大 | `24`                                       | 橋長到足以容納多個框架時，框架間隔多少排。它們圍繞橋跨段的中點對稱分佈                                                                                                                                                                                             |
| `villagePathBridgeFrameLeast`    | 整數，1 或更大 | `24`                                       | 能得到框架的最短橋跨段。更短的橋保持樸素                                                                                                                                                                                                                           |
| `villagePathPiers`               | 清單           | 空                                         | 在水上斷頭的街道所用的碼頭樣式：架橋的尾段變成碼頭，而不是一座通向虛無的橋。樣式有 railed、pilings 和 boardwalk；多個項目則每個碼頭抽取一個。留空則讓這樣的尾段保持為普通橋                                                                                        |
| `villagePathPierCargo`           | 清單           | 空                                         | 擺在碼頭欄杆內側的貨物，寫作 block=weight 項目，block=weight,height 表示堆疊，或 empty=weight 表示留空的份額。方塊可以在方括號中帶上狀態，帶朝向的方塊會轉向碼頭的中間。不在 1 到 8 之間的高度按一格堆放。其餘每一排在每一側都對清單進行抽取。留空則讓碼頭光禿禿的 |
| `villagePathPierLoot`            | 文字           | `resourcedatapackloader:chests/pier_cargo` | 有物品欄的貨物方塊用來填充的戰利品表，在第一次被開啟時抽取。資源包可以在該名稱下提供自己的戰利品表來取代內建的那張。留空則讓它們保持為空                                                                                                                           |

#### 村莊隧道

*村莊與城市*

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

| 設定                          | 類型           | 預設 | 作用                                                                                                                                                                                                                 |
| ----------------------------- | -------------- | ---- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villagePathTunnelBlock`      | 文字           | 空   | 街道穿山而過、而不是把山劈開之處，用來襯砌街道的方塊：隧道兩側的牆以及其上的頂。留空則不鑿隧道，讓街道爬上山去                                                                                                       |
| `villagePathTunnelDepth`      | 整數，1 或更大 | `10` | 路面之上要有多厚的土層，一段路才會被鑿穿而不是劈開。埋得這麼深、持續十二排或更多的隆起會被保持水平並鑿穿，其較淺的引道則被劈開；較短的土包仍像以前一樣被劈開。只有在 `villagePathTunnelBlock` 指定了方塊之後才起作用 |
| `villagePathTunnelLightBlock` | 方塊           | 空   | 沿隧道頂部中線嵌入的光源。留空則不設光                                                                                                                                                                               |
| `villagePathTunnelLightRun`   | 整數，1 或更大 | `8`  | 這些光源相隔多少格。以世界座標為錨，因此一段道路的光源會延續到下一段；短得搆不到其中某個位置的隧道，只在正中點亮一次                                                                                                 |

#### 村莊下水道

*村莊與城市*

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

| 設定                       | 類型           | 預設              | 作用                                                                                                                                                                                                                                                   |
| -------------------------- | -------------- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `villageSewerBlock`        | 方塊名稱       | 空                | 村莊街道和小巷之下的下水道所用的襯砌方塊：它的地面、兩側的牆和頂。留空則不挖下水道                                                                                                                                                                     |
| `villageSewerDepth`        | 整數，4 或更大 | `8`               | 下水道地面位於街道自身路面之下多深。下水道跟隨它所在的街道，因此爬升的街道帶著爬升的下水道。需要 `villageSewerBlock`                                                                                                                                   |
| `villageSewerHeight`       | 整數，2 或更大 | `3`               | 步道上方有多少格淨空                                                                                                                                                                                                                                   |
| `villageSewerWidth`        | 整數，3 或更大 | `5`               | 下水道有多寬，橫向計算，包括兩側的牆。偶數會被向上取整，使水道保持居中                                                                                                                                                                                 |
| `villageSewerWaterBlock`   | 方塊名稱       | `minecraft:water` | 填滿中間水道的東西。留空則讓水道保持乾燥                                                                                                                                                                                                               |
| `villageSewerWalkBlock`    | 方塊名稱       | 空                | 水道兩側步道的鋪面。留空則在襯砌方塊上行走                                                                                                                                                                                                             |
| `villageSewerLightBlock`   | 方塊名稱       | 空                | 作為光源嵌入水道上方頂部的方塊。留空則不設光                                                                                                                                                                                                           |
| `villageSewerLightRun`     | 整數，1 或更大 | `8`               | 這些光源相隔多少格。以世界座標為錨，因此一段道路的光源會延續到下一段                                                                                                                                                                                   |
| `villageSewerLadderBlock`  | 文字           | 空                | 檢修孔豎井用來攀爬的方塊，從街道沿豎井一路設置到下水道頂部。留空則讓豎井敞開                                                                                                                                                                           |
| `villageSewerCoverBlock`   | 方塊名稱       | 空                | 蓋住檢修孔的方塊，與東西向街道的路面齊平設置，設在街道或小巷與之相接之處，以及該街道穿過下水道環路的廣場上。通常選用木頭地板門：鐵地板門接受紅石訊號而玩家無法徒手開啟，這會把下水道對他們關閉。留空則讓井口敞開                                       |
| `villageSewerMossBlock`    | 方塊名稱       | 空                | 在襯砌中這裡那裡混入的第二種方塊，例如在普通石頭中混入青苔石。留空則全程只用一種方塊襯砌下水道                                                                                                                                                         |
| `villageSewerMossChance`   | 0 到 100       | `25`              | 襯砌方塊中有百分之幾會變成那第二種方塊。根據世界種子碼對每個方塊位置抽取，因此同一條下水道總會得到相同的結果                                                                                                                                           |
| `villageSewerVineBlock`    | 方塊名稱       | 空                | 這裡那裡掛在下水道牆壁內側的方塊，例如藤蔓。它會附著在它所靠的那面牆上。留空則什麼也不掛                                                                                                                                                               |
| `villageSewerVineChance`   | 0 到 100       | `20`              | 牆邊的格子中有百分之幾掛有它。根據世界種子碼對每個方塊位置抽取，因此同一條下水道總會掛出相同的樣子                                                                                                                                                     |
| `villageSewerWellEntrance` | 布林值         | `true`            | 在水井周圍的廣場環下方建一圈下水道環路，每條街道的下水道都穿過它，並在東西向街道穿過環路的每一側，於廣場上各設一個檢修孔通向環路，這樣各條下水道便成為一個相連的系統，並在城鎮中心有一個入口。關閉時，每條街道的下水道止於水井處，廣場上沒有下去的通道 |

**下水道。** 指定 `villageSewerBlock` 後，會在每條街道和小巷之下挖一條下水道，位於該街道自身路面之下 `villageSewerDepth` 格。它不是獨立的網路：它跟隨街道，街道通向哪裡它就通向哪裡，街道爬升它也爬升，十字路口下的兩條下水道相遇，因為它們上方的街道相遇；街道或小巷在另一條街道處終止時，它的下水道會繼續延伸到那條街道之下與之相接。盡頭路和架在橋上的路段沒有下水道。其剖面為襯砌的地面，中間一條填滿 `villageSewerWaterBlock` 的水道，兩側各一條以 `villageSewerWalkBlock` 鋪面的步道，`villageSewerHeight` 格的淨空和襯砌的頂，寬 `villageSewerWidth`（包括兩側的牆），並且 `villageSewerLightBlock` 每隔 `villageSewerLightRun` 格在水道上方的頂部嵌入一個光源。地鐵隧道從下水道的深度範圍穿過時，無論在街道之下還是它旁邊，下水道會在那裡整面封死，那裡的任何軌道則保持不動。`villageSewerWellEntrance` 開啟時，街道的下水道止於水井周圍的環路，關閉時則止於水井本身。下水道絕不會上升到干擾其上街道的程度，街道與世界底部之間沒有空間的路段會被略過，而不是被硬擠進去。

#### 村莊鐵路

*村莊與城市*

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

| 設定                             | 類型                 | 預設   | 作用                                                                                                                                                                                                                            |
| -------------------------------- | -------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villageRailLines`               | 整數，0 或更大       | `0`    | 一座城市有多少條鐵路線穿過，在任何街道之前鋪設，讓城鎮圍繞它們成長，每條都貫穿整座城市的長度。villageCitySpacing 為 1 時每個街區都是一座城市，各自帶有自己的鐵路線。0 表示不鋪設                                                |
| `villageRailSpacing`             | 整數，1 或更大       | `48`   | 同一座城市中，一條鐵路線的路基與下一條之間至少要有多少格空地。1 讓它們相隔一格，資源包就是這樣建出平行線路的調車場的                                                                                                            |
| `villageRailDirection`           | 文字                 | `any`  | 線路的走向：`ew` 為東西向，`ns` 為南北向，`any` 為每座城市隨機抽取。`e`, `w`, `n` 和 `s` 的讀法相同                                                                                                                             |
| `villageRailWidth`               | 整數，3 或更大       | `3`    | 路基的最小寬度。`3` 在正中承載一條軌道，`5` 承載兩條；要求的軌道數超過其容量的路基，會加寬以容納它們                                                                                                                            |
| `villageRailBlock`               | 方塊                 | 空     | 軌道。留空則鋪設原版鐵軌，礦車可在其上行駛；任何其他方塊都會原樣鋪設                                                                                                                                                            |
| `villageRailTrackSeat`           | `auto`、`on` 或 `in` | `auto` | 軌道的位置。`auto` 把鐵軌方塊放在路基上，而把任何其他方塊與路基表面齊平地嵌入；`on` 一律鋪在路基上；`in` 一律嵌入路基。資源包若要用鐵塊或半磚做出鐵軌的樣子，而不是礦車鐵軌，就把軌道嵌入路基，這樣平交道會與路面齊平地穿過路面 |
| `villageRailBedBlock`            | 方塊                 | 空     | 軌道底下的路基。留空則鋪礫石                                                                                                                                                                                                    |
| `villageRailTieBlock`            | 文字                 | 空     | 每隔 villageRailTieRun 排橫鋪在路基上的枕木。留空則鋪橡木材                                                                                                                                                                     |
| `villageRailTieRun`              | 整數，1 或更大       | `2`    | 枕木相隔多少排                                                                                                                                                                                                                  |
| `villageRailTracks`              | 整數，0 或更大       | `0`    | 同一個路基承載多少條軌道，並排且相距 `villageRailTrackGap`。**路基會加寬以容納它們全部**，因此三條軌道共用一個路基，而不是變成三條線路。`0` 在不足五格寬的路基上鋪一條軌道，在更寬的路基上鋪兩條                                |
| `villageRailTrackGap`            | 整數，2 或更大       | `2`    | 一個路基上的軌道相隔多少格，中心到中心。`2` 是允許的最小值，在它們之間留一格路基，這能防止它們像相鄰鐵軌那樣彼此彎向對方                                                                                                        |
| `villageRailShoulderBlock`       | 方塊                 | 空     | 裝飾路基最外側的幾欄，是軌道旁的維護小路，相當於鐵路版的道路人行道。留空則不鋪                                                                                                                                                  |
| `villageRailShoulderWidth`       | 整數，0 或更大       | `1`    | 該路肩每側有多少列寬，加在 `villageRailWidth` 之外。需要 `villageRailShoulderBlock`                                                                                                                                             |
| `villageRailPowerBlock`          | 方塊                 | 空     | 每隔 `villageRailPowerRun` 列嵌入線路的動力軌道。留空則使用原版動力鐵軌；不是鐵軌的方塊則直接鋪在那裡                                                                                                                           |
| `villageRailPowerBase`           | 方塊                 | 空     | 動力軌道底下用來供能的方塊。留空則使用紅石方塊                                                                                                                                                                                  |
| `villageRailPowerRun`            | 整數，0 或更大       | `0`    | 每隔這麼多排，在原版鐵軌線路中嵌入一根位於紅石方塊之上的動力鐵軌，讓礦車保持滾動。`0` 表示不供能，除原版鐵軌之外的任何軌道都會忽略它                                                                                            |
| `villageRailClimb`               | 整數，1 或更大       | `8`    | 線路每爬升或下降一格，要保持水平行進多少排。`1` 讓它的坡度和道路一樣陡                                                                                                                                                          |
| `villageRailTail`                | 整數，0 或更大       | `48`   | 鐵路線越過城市最後一個街區，向兩端繼續延伸多遠                                                                                                                                                                                  |
| `villageRailSupportBlock`        | 文字                 | 空     | 線路跨越水面或落差處，棧橋下的立柱方塊。留空則使用橡木原木                                                                                                                                                                      |
| `villageRailDeckBlock`           | 文字                 | 空     | 棧橋承載路基的橋面。留空則使用橡木材                                                                                                                                                                                            |
| `villageRailBarrierBlock`        | 方塊                 | 空     | 沿棧橋橋面兩側邊緣的護欄。留空則不立                                                                                                                                                                                            |
| `villageRailBridgeFrameBlock`    | 方塊                 | 空     | 長棧橋上方的框架：橋面兩側各立一根柱子，頂部橫一根梁。每個帶框架的列，其支撐柱也一路通到路基。留空則不建                                                                                                                        |
| `villageRailBridgeFrameTopBlock` | 方塊                 | 空     | 該框架頂部的橫梁。留空則使用 `villageRailBridgeFrameBlock`                                                                                                                                                                      |
| `villageRailBridgeFrameHeight`   | 整數，2 或更大       | `4`    | 框架在橋面上方留出多少格淨空，橫樑位於其上一格                                                                                                                                                                                  |
| `villageRailBridgeFrameRun`      | 整數，2 或更大       | `24`   | 棧橋長到足以容納多個框架時，框架間隔多少排                                                                                                                                                                                      |
| `villageRailBridgeFrameLeast`    | 整數，2 或更大       | `24`   | 能得到框架的最短棧橋。更短的棧橋保持樸素                                                                                                                                                                                        |
| `villageRailTunnelBlock`         | 文字                 | 空     | 鐵路線穿山而過、而不是爬上山去之處，用來襯砌線路的方塊。留空則不鑿隧道                                                                                                                                                          |
| `villageRailTunnelDepth`         | 整數，1 或更大       | `6`    | 路基之上要有多厚的土層，一段路才會被鑿穿而不是劈開。需要 `villageRailTunnelBlock`                                                                                                                                               |
| `villageRailTunnelLightBlock`    | 方塊                 | 空     | 沿鐵路隧道頂板中心線嵌入的光源。留空則不設燈                                                                                                                                                                                    |
| `villageRailTunnelLightRun`      | 整數，1 或更大       | `8`    | 這些隧道光源相隔多少格，以世界座標為錨，使各段保持一致                                                                                                                                                                          |

**線路的走向。** 線路相互平行，沿 `villageRailDirection` 指定的軸，從城市的第一口水井開始依序向外分佈，先一側再另一側，每條的路基與下一條之間至少保留 `villageRailSpacing` 格的地面。鐵路起點避開廣場及其周圍的地塊，凡是會順著街道鋪設之處就滑向一旁；地鐵則從水井所在的那一排起步，滑向 `villageSubwaySpacing` 之內最近的街道，因此它從道路之下穿行。線路在地塊之前鋪設，因此沒有地塊立在露天軌道上，它貫穿整座城市，並在兩端越過最後一個街區再延伸 `villageRailTail`，在距離擋路的其他任何城市還有七格處停下。它所經過的每個街區都鋪設自己的那一段，無論城市是否在那裡成長。

**坡度。** 鐵路不像街道那樣爬坡。它的路基跟隨被長距離抹平的地面，且每隔至少 `villageRailClimb` 排才變化一格高度；地鐵跟隨其下方 `villageSubwayDepth` 處的地面，若某條線路沿線任何地方都無法保持這個深度，在其襯砌所需的、位於世界底部之上的六格空間之內，則根本不鋪設。地面落差超過三格或被水覆蓋之處，線路走在棧橋上：一個以 `villageRailDeckBlock` 鋪成的橋面，軌道在其上或嵌入其中，沒有枕木或路肩，橋面兩側邊緣之下每四排立 `villageRailSupportBlock` 立柱，每根立柱向下抵達堅實地面，最多 24 格。地面抬升之處，線路被劈開，或者一旦路基之上的土層連續十二排或更多達到 `villageRailTunnelDepth` 深，就用 `villageRailTunnelBlock` 鑿穿；只要仍有一格土層作頂，鑿穿就繼續進行。水在三格之內的露天劈開路段，會用隧道襯砌砌牆，一直砌到水面，路基旁邊的地面則在塌落處被墊起。整條線路沿線在路基之上保持四格淨空。棧橋從頭到尾處於同一高度，其兩側的路基以坡道與該高度相接；在保持棧橋水平與爬升速率相衝突之處，以水平為準，其旁邊的坡道可能比 `villageRailClimb` 所說的更早一級一級變化。長度達到 `villageRailBridgeFrameLeast` 排或更多的棧橋，一旦 `villageRailBridgeFrameBlock` 指定了方塊，就帶有頂部框架，相隔 `villageRailBridgeFrameRun` 排，圍繞棧橋中點對稱分佈，每個帶有框架的那一排也帶有其支撐立柱。街道與線路交叉的那一排不設框架。

**交叉口。** 街道筆直穿過線路。在交叉口，線路在街道上以及兩側各多一排的範圍內保持水平，街道向線路看齊地調整坡度，絕不會反過來，並以其自身的坡度升降到那個高度。路面保持原貌，軌道則高出一格橫穿過去，或者在 `villageRailTrackSeat` 把軌道設在路基裡時與路面齊平，這樣礦車可以穿過街道，村民可以穿過軌道。在高出六格或更多的街道之下鑿穿的線路根本不交叉：街道保持它自己的坡度，從隧道上方經過。該高度是依據街道的地面讀取的，這個地面被抹平成每排最多爬升一格，先於任何交叉口、水井或鐵路對它的約束。

**門階。** 地面塌落處，每個地塊的每扇門前的台階會被用土墊起，而留在水上的石質台階則用 `villagePathVergeWaterBlock` 裝飾。

**軌道。** `villageRailBlock` 為空時，軌道是順著線路方向轉好的原版鐵軌，`villageRailPowerRun` 每隔若干排在一個紅石方塊之上設置一根已通電的動力鐵軌，讓礦車能一路行駛完整條線路；嵌入路基的軌道不帶動力鐵軌。想要鐵磚塊、鐵柵欄或其他任何東西的資源包，直接指定它們即可：帶軸的方塊，例如原木，會順著線路方向轉向，其他方塊則依原樣鋪設。每個鐵路、地鐵、車站和下水道方塊都可以在方括號中攜帶其狀態，上面的「混合方塊」列出了依方塊逐個抽取加權混合的那些設定。

#### 村莊地鐵

*村莊與城市*

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

| 設定                            | 類型           | 預設   | 作用                                                                                                                                                                                                                     |
| ------------------------------- | -------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `villageSubwayLines`            | 整數，0 或更大 | `0`    | 一座城市挖多少條地下鐵路線。0 表示不挖，也不抽取任何東西，因此城市的佈局與沒有它們時完全一樣                                                                                                                             |
| `villageSubwayDepth`            | 整數，6 或更大 | `24`   | 路基位於地表之下多深。線路依據其上方的地面確定坡度，因此它在該深度跟隨地勢起伏，而不是水平延伸                                                                                                                           |
| `villageSubwaySpacing`          | 整數，1 或更大 | `64`   | 一座城市的地鐵線路彼此保持多遠                                                                                                                                                                                           |
| `villageSubwayDirection`        | 文字           | `any`  | 地鐵線路的走向：ew 為東西向，ns 為南北向，any 為每座城市隨機抽取                                                                                                                                                         |
| `villageSubwayWidth`            | 整數，3 或更大 | `3`    | 路基有多寬，不含路肩                                                                                                                                                                                                     |
| `villageSubwayBlock`            | 方塊           | 空     | 軌道方塊。留空則鋪原版鐵軌                                                                                                                                                                                               |
| `villageSubwayTrackSeat`        | 字串           | `auto` | 軌道是放在路基上、嵌入路基中，還是用 `auto` 由方塊自己決定                                                                                                                                                               |
| `villageSubwayBedBlock`         | 方塊           | 空     | 路基所用的方塊。留空則使用礫石                                                                                                                                                                                           |
| `villageSubwayTieBlock`         | 方塊           | 空     | 橫鋪在路基上作為枕木的方塊。留空則使用木板                                                                                                                                                                               |
| `villageSubwayTieRun`           | 整數，1 或更大 | `2`    | 枕木相隔多少格                                                                                                                                                                                                           |
| `villageSubwayTracks`           | 整數，0 或更大 | `0`    | 路基承載多少條平行軌道。0 表示在寬度允許的範圍內盡可能多                                                                                                                                                                 |
| `villageSubwayTrackGap`         | 整數，2 或更大 | `2`    | 平行軌道相距多遠                                                                                                                                                                                                         |
| `villageSubwayShoulderBlock`    | 方塊           | 空     | 路基兩側的方塊。留空則沒有路肩                                                                                                                                                                                           |
| `villageSubwayShoulderWidth`    | 整數，0 或更大 | `1`    | 該路肩有多寬                                                                                                                                                                                                             |
| `villageSubwayPowerBlock`       | 方塊           | 空     | 動力鐵軌方塊。留空則使用原版動力鐵軌                                                                                                                                                                                     |
| `villageSubwayPowerBase`        | 方塊           | 空     | 放在動力鐵軌下用來驅動它的方塊。留空則使用紅石方塊                                                                                                                                                                       |
| `villageSubwayPowerRun`         | 整數，0 或更大 | `0`    | 動力鐵軌相隔多少格。0 表示不鋪                                                                                                                                                                                           |
| `villageSubwayTunnelBlock`      | 文字           | 空     | 襯砌隧道的方塊：兩側的牆和其上的頂。留空則挖出隧道及其車站而不襯砌                                                                                                                                                       |
| `villageSubwayTunnelLightBlock` | 方塊           | 空     | 作為光源嵌入隧道頂部的方塊。留空則不設光                                                                                                                                                                                 |
| `villageSubwayTunnelLightRun`   | 整數，1 或更大 | `8`    | 這些光源相隔多少格，以世界座標為錨，使各段保持一致                                                                                                                                                                       |
| `villageSubwayClimb`            | 整數，1 或更大 | `8`    | 線路行進多少格後才可以升降一格                                                                                                                                                                                           |
| `villageSubwayTail`             | 整數，0 或更大 | `48`   | 線路越過城市自身的部件後再延伸多遠才停止                                                                                                                                                                                 |
| `villageSubwaySurfaces`         | 整數，0 到 100 | `25`   | 一條地鐵線路在某一端爬升到地表，並從那裡作為普通鐵路繼續延伸的百分之幾機率，身後是隧道，前方是露天軌道。爬升每升一格需要 villageSubwayClimb 排，所以深處的線路要花很長一段才能升上來。0 讓每條地鐵在整個長度上都埋在地下 |

**爬出地面。** `villageSubwaySurfaces` 是一條線路不從頭到尾保持埋於地下，而是在某一端爬升到地表並從那裡作為普通鐵路繼續延伸的百分之幾機率：身後是隧道，前方是露天軌道。爬升遵循 `villageSubwayClimb`，每隔那麼多排升一格，因此深 `villageSubwayDepth` 的線路僅坡道就要花 depth 乘以 climb 排，且在它之外還需要相當長的一段，才配得上這個名稱；沒有空間容納兩者的線路則只是留在地下。爬升的起點不會比線路所穿行街道的遠端更近，因此它是從城市街道的另一頭冒出來，而不是從街道中間穿出，在升起的那一段上的地塊會為它讓路。車站在爬升確定之後才被佔用，並避開坡道。`villageSubwayTunnelBlock` 為空時，隧道及其車站和樓梯都不襯砌地開挖。

#### 地鐵站

*村莊與城市*

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

| 設定                         | 類型           | 預設                   | 作用                                                                                                                                                                                                                                                                                                 |
| ---------------------------- | -------------- | ---------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villageSubwayStationLength` | 整數，0 或更大 | `0`                    | 車站站廳的長度（以方塊計），以路線最靠近水井的那一排為中心。0 表示完全不建車站                                                                                                                                                                                                                       |
| `villageSubwayStationRun`    | 整數，0 或更大 | `0`                    | 沿路線，除了離城市第一口水井最近的那一座之外，其餘車站彼此相隔多少方塊。0 表示只在水井處建那一座                                                                                                                                                                                                     |
| `villageSubwayPlatformWidth` | 整數，0 或更大 | `3`                    | 站廳在路基兩側各向外拓寬多遠，用來做出月台                                                                                                                                                                                                                                                           |
| `villageSubwayPlatformBlock` | 方塊           | 空                     | 月台地面所鋪的方塊。留空則以隧道襯砌鋪地                                                                                                                                                                                                                                                             |
| `villageSubwayRailingBlock`  | 方塊           | `minecraft:iron_bars`  | 圍在車站樓梯口、通往街道處的欄杆方塊，免得有人走進水井。留空則樓梯口不設欄杆                                                                                                                                                                                                                         |
| `villageSubwayBenchBlock`    | 方塊           | `minecraft:oak_stairs` | 設在車站月台上及樓梯口旁的長椅座面。樓梯方塊會轉為背向路線，看起來就像長椅；任何方塊皆可。留空則不設長椅                                                                                                                                                                                             |
| `villageSubwayBenchEndBlock` | 方塊           | `minecraft:oak_log`    | 車站長椅兩端的扶手。留空則座面兩端都光禿禿的                                                                                                                                                                                                                                                         |
| `villageSubwayBenchLength`   | 整數，0 到 32  | `5`                    | 車站長椅的長度，含扶手。`0` 表示不設長椅                                                                                                                                                                                                                                                             |
| `villageSubwayStation`       | 文字           | 空                     | 取自資源包 structures 資料夾、用作車站的結構：它的豎井、樓梯，以及從街道進入的通道。在世界中手工建造一個，用結構方塊儲存，再把檔案複製到資源包中：其實心格會被鋪設，空氣格會被挖空，所以形狀就是建築本身，而不是對它的描述。留空則完全不建車站，無法載入的名稱會在日誌中記錄一則錯誤，且不建任何車站 |
| `villageSubwayStationFoot`   | 整數，0 到 64  | `4`                    | 車站建築底部有多少層只鋪設一次，位於重複的部分之前。地板和通往月台的門洞就在這裡                                                                                                                                                                                                                     |
| `villageSubwayStationRepeat` | 整數，0 到 64  | `12`                   | 車站建築有多少層會重複，使同一座建築能適用於任何深度：豎井以這一段的整份複本向上增長，走廊則吸收剩下的部分。它必須恰好是樓梯轉一整圈的高度，否則各段樓梯無法銜接。`0` 表示建築絕不增長                                                                                                               |

**車站。** 只有當 `villageSubwayStation` 指定了一個能載入的建築時，地鐵路線才會有車站：留空時沒有站廳、沒有樓梯，也沒有入口，而無法載入的名稱會在日誌中記錄一則錯誤，且不建任何車站。指定建築之後，只要設定了 `villageSubwayStationLength` 和 `villageSubwayPlatformWidth`，路線就會在城市第一口水井所在的那一排設一座車站，並沿路線每隔 `villageSubwayStationRun` 個方塊再設一座。每座車站最多向兩側各滑動 48 個方塊，為其建築尋找位置：緊鄰一條在該位置的整個長度上都與路線並行的街道，避開所有街道、水井和廣場，且與已佔用的車站至少相距車站長度再加七個方塊；站在該位置上的地塊會讓出空間，找不到這種位置的車站會被捨去。不設車站的路線不開站廳，因此絕不會有一個沒有入口的站廳。站廳沿其長度保持水平：路基向兩側各拓寬 `villageSubwayPlatformWidth`，以 `villageSubwayPlatformBlock` 鋪地，以隧道襯砌砌牆並加頂，由隧道自己的 `villageSubwayTunnelLightBlock` 和 `villageSubwayTunnelLightRun` 照明，並在兩端將隧道橫向封死。從月台起有一條走廊通往車站建築，建築爬升到道路旁的街道，絕不在其下方；建築升到八個方塊內最近街道的坡度，也就是該街道在地面上保持的坡度，而不是架在其上的任何橋面或坡道，在沒有街道之處則升到地面的高度；當城市建造時該高度比月台高出不到三個方塊、建築即使增長也無法完成爬升，或通往月台的走廊會延伸超過 32 個方塊時，該車站會被捨去，日誌會說明原因。那條街道與建築之間的地面會調整到同一高度，低陷處填平，頭頂清空，這樣人就能從道路走進車站。一張以 `villageSubwayBenchEndBlock` 為扶手、長 `villageSubwayBenchLength` 的 `villageSubwayBenchBlock` 長椅立在月台上。

**手工建造車站。** `villageSubwayStation` 指定一個用作車站的結構檔案，資源包就是這樣提供某人親手建造的形狀，而不是在設定中描述出來的形狀。在世界中建好它，用結構方塊儲存（大小要涵蓋整座車站，包括其中的空氣，並勾選「包含實體」），把檔案從世界的 `generated/<namespace>/structures/` 資料夾複製到資源包的 `<namespace>/structures/` 資料夾，並隨資源包放置。車站敞開之處請使用空氣，不要用結構空位：其方塊按所建的樣子鋪設，海綿格變成隧道襯砌，空氣格被挖空，站在其中的任何東西，如礦車或盔甲座，也會隨之而來。它從車站所佔位置的角落安置。同一座建築能適用於任何深度，是因為它的中間部分會重複：底部先鋪一次 `villageSubwayStationFoot` 層，帶有地板和通往月台的門洞，然後接下來 `villageSubwayStationRepeat` 層的整份複本向上堆疊，直到建築抵達街道。那一段必須恰好是樓梯轉一整圈的高度，否則兩份複本相接之處，各段樓梯無法銜接。一條兩格高的走廊從門洞通往月台，在落差太長、無法直行之處沿站廳折返；建築頭頂的地面向上清空八格，街道層的開口周圍圍一圈 `villageSubwayRailingBlock` 欄杆，旁邊立一張長椅。無法載入的建築會讓任何地方都不建車站，並在日誌中記錄一則錯誤；其建築無法升到街道的車站，在城市規劃時被捨去，沒有站廳，並記錄原因。建築自帶通往街道的出口。

#### 鐵路連結

*村莊與城市*

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

| 設定                           | 類型           | 預設    | 作用                                                                                                                                   |
| ------------------------------ | -------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| `villageRailLinks`             | true/false     | `false` | 連接相鄰的城市，這些城市的第一條路線隔著一道接縫彼此相對。需要 `villageRailLines`，或在沒有地面路線的資源包中需要 `villageSubwayLines` |
| `villageRailLinkLeast`         | 整數，0 或更大 | `128`   | 鋪設的最短連結，支線加幹線再加支線，以方塊計                                                                                           |
| `villageRailLinkMost`          | 整數，0 或更大 | `1024`  | 鋪設的最長連結，支線加幹線再加支線，以方塊計                                                                                           |
| `villageRailLinkBridgeMost`    | 整數，0 或更大 | `96`    | 一條連結可能需要的最長橋梁。需要跨越更寬的水面或更深落差的連結不會被鋪設                                                               |
| `villageRailLinkTunnelMost`    | 整數，0 或更大 | `192`   | 在 `villageRailTunnelBlock` 開鑿隧道之處，一條連結可能需要的最長隧道。需要鑿得更長的連結不會被鋪設                                     |
| `villageRailLinkStation`       | 文字           | `both`  | 每條支線在接入幹線之前的車站：`both` 在路線兩側各鋪一個月台，`one` 在駛向幹線的列車左側鋪單一月台，`none` 則不建                       |
| `villageRailLinkStationLength` | 整數，0 或更大 | `16`    | 車站月台有多少排長。`0` 表示不建車站                                                                                                   |
| `villageRailLinkPlatformWidth` | 整數，0 或更大 | `3`     | 每個月台有多少方塊寬                                                                                                                   |
| `villageRailLinkPlatformBlock` | 方塊           | 空      | 月台所用的建材方塊。留空則使用石磚                                                                                                     |

**什麼是連結。** 鐵路連結把相鄰的城市連成一個網路。城市依城市網格（`villageCitySpacing`）每格一座地建立，連結沿著兩個格子之間的接縫延伸：每座城市的第一條路線越過其尾段，作為支線筆直伸向接縫，並與沿接縫垂直鋪設的幹線相接。幹線從一條支線延伸到另一條，絕不越過任何一條。它需要 `villageRailLines`，或在沒有地面路線的資源包中需要 `villageSubwayLines`，並且預設關閉。

**哪些城市會連結。** 兩座城市只有在位於相鄰的格子中、其第一條路線沿跨越兩者之間接縫的軸線延伸，且整條連結沿軌道從水井到水井量得的長度介於 `villageRailLinkLeast` 和 `villageRailLinkMost` 個方塊之間時才會連結。判定的每個環節都由種子碼和兩座城市的選址算出，因此無論先生成哪座城市或哪個區塊，結果都相同。無法完整建成的連結根本不會鋪設，絕不會只建一半：包括需要比設定所允許的更長的橋或隧道、越過世界邊界、撞上林地府邸、讓兩座城市靠得比 `structureSeparation` 所允許的更近，或使接合處離格子的角落太近的那些。幹線只會朝著真正建立起來的城市鋪設：當 `structureMost` 之類的上限擋住了鄰居，或它長得太小而保不住時，幹線的兩半和越過該城市自身尾段的支線都不會建造。固定位置的城市以同樣方式連結，每格一座；含有兩個固定點的格子則兩邊都不連。其他城市在成長時會避開連結的支線和幹線，就像它們彼此避開一樣。

**坡度。** 支線和幹線都是鐵路路線，其坡度、架橋、開鑿隧道和交叉的處理與城市路線完全相同，使用的是 `villageRailClimb` 以及上面的棧橋和隧道設定。支線與幹線相接之處，兩者都保持水平，旁邊的車站也是如此。

**交匯處。** 支線只與幹線靠近的那條軌道相接。該軌道在支線中線與之相遇處斷開，支線的左軌道向左彎入，右軌道向右彎入，而遠側的軌道筆直穿過。以兩條軌道、幹線在上方、支線從下方而來為例：

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` 是路基，`o` 是軌道。兩條支線會在幾個方塊之內先後抵達之處，第二座城市的第一條路線會移過去與第一條對齊，兩者改在一個十字路口相會：每條支線只匯入它自己的近側軌道，與上述完全相同，幹線的兩條軌道都在支線中心被截斷，且沒有任何鐵軌穿越另一條：

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

**地鐵。** 在只有地鐵路線的資源包中，連結承載的是城市的第一條地鐵路線。路線從地下爬向幹線，坡道長 `villageSubwayDepth` 乘以 `villageSubwayClimb` 排，在地表抵達車站和交匯處；這種城市只在一側連結，也就是連結較短的那一側，幹線是依據 `villageRail` 設定建成的地面鐵路。支線沒有空間容納那段坡道及其車站之處，幹線改為下到地鐵：整條連結，支線和幹線，都留在 `villageSubwayDepth` 的地下，依據 `villageSubway` 設定建成，並在同樣的交匯處相會，不設車站。

#### 村莊裝飾

*村莊與城市*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"],
    "villageDecorSpacing": 3
  }
}
```

| 設定                  | 類型          | 預設 | 作用                                                                                                                                                                                                                                                                                                                                                                 |
| --------------------- | ------------- | ---- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `villageDecor`        | 清單          | 空   | 散布在城市街道旁的裝飾，寫成 name=weight 配對，名稱指向資源包提供的世界生成，如 mypack:street_flowers=2。名稱 empty 表示留空位置所佔的比例，不是 name=weight 寫法的項目會被忽略。街道每側路肩上每隔 villageDecorSpacing 個方塊（預設三個）就對清單抽取一次，落在該處的地面上，無論它多高，但不會出現在隧道中、地塊下、廣場上，或距門兩個方塊以內。留空則什麼也不散布 |
| `villageDecorSpacing` | 整數，1 到 16 | `3`  | 沿街道每一側的裝飾位置之間的間隔，以方塊計。限制在 1 到 16 之間。比間隔更寬的範本可能會擠到相鄰的範本                                                                                                                                                                                                                                                                |

### 結構

*各群組的作用*

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

`control.structures` 決定這個分組。原版結構的停用、間距、間隔、出生點距離、生態域、生成物、固定位置與地形適應。

| 設定                            | 類型                                               | 預設                                                 | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| ------------------------------- | -------------------------------------------------- | ---------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `structureSpacing`              | 清單                                               | 空                                                   | 原版結構的排定間距（以區塊計），寫成 structure=chunks 項目：可用 1.12.2 的名稱 temples、monuments、mansions、mineshafts、strongholds、netherbridges、endcities 和 villages，或任何結構集 id，例如 pillager_outposts。對 mineshafts 而言，該數字表示每這麼多個區塊出現一個；對 strongholds 則是環的間距。地獄要塞沿用自己的網格，netherbridges 間距對它無效                                                                                                                                                                                                         |
| `structureSeparation`           | 清單                                               | 空                                                   | 同種結構的兩個實例之間允許的最小距離（以區塊計），寫成 structure=chunks 項目；對 strongholds 則是環的分布範圍。Temples、mineshafts 和 netherbridges 沿用自己的間隔，此項對它們無效。對 monuments 而言，若間隔大到等於間距，會被降為間距減一，日誌中會註明                                                                                                                                                                                                                                                                                                          |
| `structureMost`                 | 清單                                               | 空                                                   | 一個維度最多可容納的村莊數量，寫成 villages=count（例如 villages=100）；其他結構不設上限：一旦已建立這麼多個，就不會再有區塊建立新的，以 structureAt 固定的區塊除外。0 或沒有該項目表示不設上限                                                                                                                                                                                                                                                                                                                                                                    |
| `structureSpawners`             | 清單，內容為 `structure=entity`                    | 空                                                   | 原版結構內的生怪磚會生成什麼，以逗號分隔則每個生怪磚隨機挑一個。會放置生怪磚的有四種：dungeons、mineshafts、地獄要塞和 strongholds                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `structureMinDistanceFromSpawn` | 清單                                               | 空                                                   | 結構的起點距世界出生點至少多遠，以方塊計，寫成 structure=blocks 項目。從世界出生點量起；新世界仍在選擇出生點時，若資源包設定了 worldSpawn 則從它量起，否則從世界原點量起                                                                                                                                                                                                                                                                                                                                                                                           |
| `structureBiomes`               | 清單                                               | 空                                                   | 結構可以在哪裡生成，寫成 structure=biome,biome 項目，可填生態域 id、遊戲中顯示的名稱（如 Birch Forest）、不帶命名空間的原版名稱（如 desert），或生態域類型（如 SANDY）                                                                                                                                                                                                                                                                                                                                                                                             |
| `structureBiomesAreBlacklist`   | 清單，內容為 `structure=true` 或 `structure=false` | 空                                                   | 每個結構的生態域清單的方向                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `structureSpawns`               | 清單                                               | 空                                                   | 無論生態域如何規定，結構都會生成的生物，寫成 structure=namespace:entity:weight:least:most 項目，以逗號分隔。該清單會整個取代結構自帶的生物清單，不論其中每種生物屬於哪一類；= 之後為空清單則什麼都不生成                                                                                                                                                                                                                                                                                                                                                           |
| `structureAt`                   | 清單，內容為 `structure=x,z`                       | 空                                                   | 將結構固定在精確的位置。參見[位於精確位置的結構](#位於精確位置的結構)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `structureAdaptation`           | 清單                                               | mansions 為 `beard_thin`；其他結構保持其原版適應方式 | 地形如何適應結構，寫成 structure=mode 項目，模式有 none、bury、beard_thin、beard_box 和 encapsulate                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `terrainAdaptation`             | 布林值                                             | `false`                                              | 鋪設 RDPL 自己的城市街道，使其嵌入地形，而不是在每處凹陷上方架在支柱上，並同時讀取 villagePath 和 villageRail 選項。這會改變地形，因此開啟後建立的世界與未開啟時不同。城市依 villageCitySpacing 的設定排定，設為 0 則沒有城市                                                                                                                                                                                                                                                                                                                                      |
| `villagePieces`                 | 清單                                               | 空                                                   | 在此指定的村莊地塊，每行一個，可寫 villages 檔案的完整 id（如 mypack:smithy）、其簡短名稱，或範本地塊所建造的結構名稱。當 villagePiecesAreBlacklist 開啟時，此處列出的結構在遊戲載入它的任何地方也會被留空，包括遊戲自帶的村莊房屋，例如 minecraft:village/plains/houses/plains_small_house_1                                                                                                                                                                                                                                                                      |
| `villagePiecesAreBlacklist`     | 布林值                                             | `true`                                               | 開啟時，villagePieces 中的地塊被禁止建造。關閉時，只建造這些地塊                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `villageBlocks`                 | 清單                                               | 空                                                   | 建造村莊地塊所用的方塊，寫成 original=replacement 配對，如 minecraft:cobblestone=mypack:ruby_brick。兩側都可以帶方括號中的狀態，此時原方塊必須與之完全相符。一組配對還可以加上百分比機率，如 minecraft:cobblestone=minecraft:mossy_cobblestone,20，機率依該方塊放置處的世界種子碼計算；at=block 表示僅在該方塊所在處生效，under=block 表示僅在其上方生效。沒有機率或條件的配對最先套用，因此帶條件的配對可以對它們的結果做風化處理。它作用於農場與城市所建造的遊戲自帶村莊房屋；街道、水井、燈以及你的範本地塊中的結構永遠不受影響。留空則每個方塊保持放置時的樣子 |

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
    "skyAnimals": true,
    "threatItems": ["minecraft:diamond_sword=5,1", "minecraft:diamond=1,16,batch"],
    "threatLevels": [10, 25, 50],
    "threatMost": -1,
    "threatSpawnRate": 2.0,
    "threatNotice": 16.0,
    "threatSays": ["1=Something out there has taken notice of you.", "0=The world loses interest in you."]
  }
}
```

`control.spawning` 決定這個分組。生物生成上限、敵對生物的生成速率與光照上限。

| 設定                          | 類型                        | 預設   | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| ----------------------------- | --------------------------- | ------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `surfaceDayMonsterRate`       | 數字，0.0 到 4.0            | `1.0`  | 白天地表敵對生物生成的倍率，`1.0` 即原版，因此可以關閉白天地表的生成而不影響洞穴                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `surfaceNightMonsterRate`     | 數字，0.0 到 4.0            | `1.0`  | 同上，針對夜晚的地表                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `undergroundDayMonsterRate`   | 數字，0.0 到 4.0            | `1.0`  | 同上，針對白天的地下                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `undergroundNightMonsterRate` | 數字，0.0 到 4.0            | `1.0`  | 同上，針對夜晚的地下                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `monsterCap`                  | 整數，-1 到 1000            | `-1`   | 同時可載入的敵對生物數量。原版為 70，`-1` 表示不更動                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `creatureCap`                 | 整數，-1 到 1000            | `-1`   | 同上，針對被動動物。原版為 10                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `ambientCap`                  | 整數，-1 到 1000            | `-1`   | 同上，針對蝙蝠之類。原版為 15                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `waterCreatureCap`            | 整數，-1 到 1000            | `-1`   | 同上，針對魷魚。原版為 5                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `monsterSpawnLight`           | 整數，-1 到 15              | `-1`   | 在原版檢查之外，敵對生物仍可生成的最高方塊光照。-1 表示只保留原版規則。生怪磚不受影響                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `skyAnimals`                  | 布林值                      | `true` | 怪物以外的生物是否會在原版地形上方的天空陸地上自然生成，尤其是空島。`false` 讓動物、蝙蝠以及其他所有非怪物的生物留在下方地面。生怪磚不受影響                                                                                                                                                                                                                                                                                                                                                                                                          |
| `threatItems`                 | 清單                        | 空     | 會提高玩家威脅等級的物品，寫成 item=level,count 項目，結尾可選擇加上 ,each 或 ,batch，例如 minecraft:diamond_sword=5,1 或 minecraft:diamond=1,16,batch。預設的 each 對持有的每一個都加上該等級，最多計入 count 個；batch 則每持有 count 個加一次該等級。超過物品堆疊上限的 count 會被截為堆疊上限。每個持有物品的已載入實體都是攜帶者：玩家的主物品欄、盔甲和副手，掉落的物品堆，任何帶有物品欄的實體（如載箱騾或儲物箱礦車），以及其他生物手持的物品和穿戴的盔甲。留空則關閉威脅等級。即使 `control.spawning` 為 `off`，設定檔自身的威脅設定依然生效 |
| `threatLevels`                | 清單                        | 空     | 進入各個等級帶所需的分數，遞增排列，因此 `10, 25, 50` 構成三個等級帶。留空則關閉威脅等級                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `threatMost`                  | 整數，-1 到 100000          | `-1`   | 為分數設定上限。`-1` 表示不設上限                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `threatSpawnRate`             | 數字，0.0 到 8.0            | `1.0`  | 在處於最高等級帶的攜帶者 128 個方塊範圍內，於其他倍率之外再調整敵對生物的生成，較低的等級帶按比例分得一部分                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `threatNotice`                | 數字，0.0 到 64.0           | `0.0`  | 敵對生物（包括原版生物）能多出幾個方塊的距離發現處於最高等級帶的攜帶者，同樣分配給較低的等級帶                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `threatSays`                  | 清單，內容為 `band=message` | 空     | 當玩家自己所在的等級帶改變時以黃色顯示的文字，等級帶 `0` 是回落到第一個等級帶以下時的文字                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

### 基岩

*各群組的作用*

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
    "flatBedrockRetrogen": true,
    "flatBedrockRetrogenKey": "0001"
  }
}
```

`control.bedrock` 決定這個分組。平坦基岩及其維度與生態域清單。

| 設定                                | 類型                           | 預設                                                                                     | 作用                                                                                                                                                                     |
| ----------------------------------- | ------------------------------ | ---------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `flatBedrock`                       | 布林值                         | `false`                                                                                  | 將世界底部參差不齊的基岩替換為平坦的層。僅對新區塊生效，除非開啟 `flatBedrockRetrogen`                                                                                   |
| `flatBedrockDimensions`             | 清單                           | `["minecraft:overworld"]`                                                                | 要平整的維度，依 id。留空表示所有維度。其他模組在維度檔案中定義的維度同樣依 id 生效；在程式碼中建立的維度會記錄一則警告，並保持原樣                                      |
| `flatBedrockDimensionsAreBlacklist` | 布林值                         | `false`                                                                                  | 開啟時，平整會跳過所列的維度。關閉時，只對它們生效                                                                                                                       |
| `bedrockLayers`                     | 整數，1 到 5                   | `1`                                                                                      | 保留多少層基岩                                                                                                                                                           |
| `flatBedrockBiomes`                 | 清單，內容為生態域名稱         | 空                                                                                       | 要平整的生態域，可用易讀名稱或註冊名稱。留空表示所有生態域                                                                                                               |
| `flatBedrockBiomesAreBlacklist`     | 布林值                         | `false`                                                                                  | 開啟時，平整會跳過所列的生態域。關閉時，只對它們生效                                                                                                                     |
| `flatBedrockRoof`                   | 布林值                         | `false`                                                                                  | 在維度有基岩頂蓋之處（如地獄頂部）也將其平整                                                                                                                             |
| `flatBedrockFiller`                 | 方塊                           | 空                                                                                       | 替換被移除基岩的方塊。留空則依維度挑選：石頭、地獄石、終界石                                                                                                             |
| `flatBedrockFillers`                | 清單，內容為 `dimension=block` | `["minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"]` | 依維度指定的填充方塊，對所列的維度覆寫 `flatBedrockFiller`                                                                                                               |
| `flatBedrockBiomeTypes`             | 清單                           | 空                                                                                       | 要平整基岩的生態域類型，與 flatBedrockBiomes 並用，可用生態域標籤（如 minecraft:is_ocean）或 1.12.2 的類型名稱（如 OCEAN）。flatBedrockBiomesAreBlacklist 同樣適用於它們 |
| `flatBedrockRetrogen`               | 布林值                         | `false`                                                                                  | 也平整已存在區塊中的基岩。每個區塊只處理一次並記住這一點，且無法復原：原本的形態沒有記錄在任何地方                                                                       |
| `flatBedrockRetrogenKey`            | 文字                           | `0000`                                                                                   | 修改它可使每個區塊再次有資格進行基岩平整                                                                                                                                 |

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

`control.entities` 決定這個分組。遠離所有玩家的實體以較慢的步調更新。

| 設定                  | 類型             | 預設                      | 作用                                                                                                                                                                                                                                                 |
| --------------------- | ---------------- | ------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `slowDistantEntities` | 布林值           | `true`                    | 對遠離所有玩家的實體降低更新刻的頻率。任何東西都不會完全停止更新，只是步調變慢                                                                                                                                                                       |
| `slowedKinds`         | 清單             | `["items", "experience"]` | 哪些種類會減少更新刻：items、experience、projectiles，最後一項指箭、三叉戟、投擲的雪球、雞蛋、藥水、經驗瓶和終界珍珠，以及駱馬的口水。凡是能自行思考的實體一律會被減慢，無需在此列出：它們選擇下一步行動的頻率降低，但仍每刻移動。機器永遠不會被減慢 |
| `slowDistance`        | 整數，64 到 4096 | `192`                     | 區塊距最近玩家多遠（以方塊計）才會被減慢。遊戲在 64 個方塊外就不再向玩家通報大多數實體，因此不要低於這個值                                                                                                                                           |
| `slowRate`            | 整數，1 到 20    | `4`                       | 被減慢的區塊每這麼多刻獲得一刻更新。1 表示完全不減慢，20 表示每秒一次                                                                                                                                                                                |
| `neverSlowed`         | 清單             | 空                        | 無論多遠都不受影響的實體，寫成 namespace:name                                                                                                                                                                                                        |
| `slowRecheck`         | 整數，1 到 100   | `20`                      | 每隔多少刻重新計算一次到最近玩家的距離。每位玩家各自獨立計算，因此獨自待在遠處的人周圍依然有屬於自己的安靜空間                                                                                                                                       |

### 造陸、停留與模組的提示

*各群組的作用*

`control.chunks` 決定這個分組。出生點區塊半徑、預先生成、回溯生成與重置、歡迎文字、提示卡片，以及遊戲的彈出通知。

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "retrogen": false,
    "adoptExistingChunks": false,
    "saysCard": true,
    "saysIcon": "minecraft:compass",
    "saysColor": "1E2630",
    "saysImage": "rubyworld:textures/gui/card.png",
    "saysBackground": true,
    "saysFont": "rubyworld:runes",
    "toasts": true,
    "pregenOnNewWorld": 63,
    "pregenToBorder": false,
    "pregenAllDimensions": false,
    "pregenResume": true,
    "pregenChunksInFlight": 32,
    "pregenBackup": false,
    "resetClearsEntities": true,
    "resetClearsScores": true,
    "resetClearsInventory": false,
    "resetClearsExperience": false,
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["minecraft:overworld=64"],
    "welcomeSays": ["Welcome to Ruby World!", "minecraft:the_nether=Welcome to the Nether!"],
    "pregenDimensions": ["minecraft:overworld", "minecraft:the_nether"],
    "pregenDimensionsWhenEntered": ["minecraft:the_end"],
    "pregenRunningSays": "Building your world, %d%% done",
    "pregenFinishedSays": "Your world is ready",
    "pregenStoppedSays": "World building stopped",
    "pregenSpectatingSays": "Spectating until the world is ready",
    "pregenLogo": "center",
    "pregenBackupSays": "Pack requested world backup",
    "resetSays": "Pack requested map reset",
    "resetSendsTo": "spawn",
    "resetRuns": "rubyworld:rebuild_arena"
  }
}
```

`config/resourcedatapackloader-common.toml`

```toml
[worldgen]
retrogen = false
adoptExistingChunks = false

[chunks]
saysCard = true
saysIcon = "minecraft:compass"
saysColor = "1E2630"
saysImage = "rubyworld:textures/gui/card.png"
saysBackground = true
saysFont = "rubyworld:runes"
toasts = true
pregenOnNewWorld = 63
pregenToBorder = false
pregenAllDimensions = false
pregenResume = true
pregenChunksInFlight = 32
pregenBackup = false
resetClearsEntities = true
resetClearsScores = true
resetClearsInventory = false
resetClearsExperience = false
spawnChunkRadius = 128
spawnChunkRadii = ["minecraft:overworld=64"]
welcomeSays = ["Welcome to Ruby World!", "minecraft:the_nether=Welcome to the Nether!"]
pregenBorderLimit = 8192
pregenDimensions = ["minecraft:overworld", "minecraft:the_nether"]
pregenDimensionsWhenEntered = ["minecraft:the_end"]
pregenRunningSays = "Building your world, %d%% done"
pregenFinishedSays = "Your world is ready"
pregenStoppedSays = "World building stopped"
pregenSpectatingSays = "Spectating until the world is ready"
pregenLogo = "center"
pregenBackupSays = "Pack requested world backup"
resetSays = "Pack requested map reset"
resetSendsTo = "spawn"
resetRuns = "rubyworld:rebuild_arena"
```

| 設定                          | 類型               | 預設                                     | 作用                                                                                                                                                                                                                                                                                                                                                                                                                 |
| ----------------------------- | ------------------ | ---------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `retrogen`                    | 布林值             | `false`                                  | 讓已存在的區塊補上帶有 \"retrogen\": true 的世界生成項目。關閉時，已存在的區塊保持不變。無論開關，區塊在生成時都會被標記，因此之後再開啟只會影響比資源包更早的區塊                                                                                                                                                                                                                                                   |
| `adoptExistingChunks`         | 布林值             | `false`                                  | 將已存在的區塊視為由此資源包生成，直接加上標記，而不是留給回溯生成處理。替換一個已生成同樣礦石的模組時開啟它，這樣回溯生成就不會讓礦石翻倍。之後新增的世界生成項目仍會回溯生成到這些區塊中                                                                                                                                                                                                                           |
| `saysCard`                    | 布林值             | `false`                                  | 將此模組說的話，即歡迎語、中途加入的玩家收到的造陸說明、運行結束的提示（運行進度仍顯示在動作列），以及威脅提示，顯示為右下角的卡片，而不是聊天訊息。卡片滑入，停留八秒後淡出，開啟畫面時也會顯示在其上方                                                                                                                                                                                                             |
| `saysIcon`                    | 文字               | 空                                       | 繪製在卡片上的物品，例如 minecraft:compass。留空則不繪製                                                                                                                                                                                                                                                                                                                                                             |
| `saysColor`                   | 文字               | 空                                       | 卡片的背景顏色，十六進位，例如 1E2630。留空則使用深石板色                                                                                                                                                                                                                                                                                                                                                            |
| `saysImage`                   | 文字               | 空                                       | 來自資源包用戶端資源的 PNG，拉伸後作為卡片背景，例如 rubyworld:textures/gui/card.png，繪製在顏色之上。留空則不繪製                                                                                                                                                                                                                                                                                                   |
| `saysBackground`              | 布林值             | `true`                                   | 繪製卡片的面板、邊框和彩色條紋，以及歡迎與停留提示後方的深色背景。關閉後只剩文字（保留其陰影），若設定了 saysImage 則還有該圖片                                                                                                                                                                                                                                                                                      |
| `saysFont`                    | 文字               | 空                                       | 卡片文字所用的字型，寫成 namespace:name，例如 rubyworld:runes 對應資源包的 assets/rubyworld/font/runes.json。留空則使用 RDPL 字型                                                                                                                                                                                                                                                                                    |
| `toasts`                      | 布林值             | `false`                                  | 顯示遊戲的彈出通知，即右上角用於進度、解鎖配方、教學提示和系統通知的彈出視窗，其他模組的彈出通知也包括在內。關閉則一律不顯示。在下一次加入世界或伺服器時生效。世界範本可以改為列出要顯示的種類                                                                                                                                                                                                                       |
| `pregenOnNewWorld`            | 整數，0 到 8192    | `0`                                      | 在任何人開始遊玩之前，新世界在出生點周圍多大範圍內（以區塊計）完成造陸。遊戲自己會在出生點周圍生成 12 個區塊，因此 12 是下限，0 表示這個下限而不是什麼都不做：遊戲本來就要生成的地面會在一次有組織的流程中被接手並完成光照，而不是零零散散地陸續出現。調高它可以比遊戲涵蓋得更遠                                                                                                                                     |
| `pregenToBorder`              | 布林值             | `false`                                  | 新世界是否將陸地一直生成到世界邊界，而不是固定的區塊數，並以邊界而非出生點為中心。邊界從未向內收縮過的世界沒有可抵達的邊界，會被跳過                                                                                                                                                                                                                                                                                 |
| `pregenAllDimensions`         | 布林值             | `false`                                  | 為伺服器擁有的每個維度（包括模組維度）造陸，先主世界，其餘依 id 順序，而不是只處理 pregenDimensions 中的維度。在 pregenDimensionsWhenEntered 中列出的維度仍留給第一位造訪者                                                                                                                                                                                                                                          |
| `pregenResume`                | 布林值             | `false`                                  | 被停止或中途中斷的執行，下次載入世界時是否從中斷處繼續，而不是重新開始                                                                                                                                                                                                                                                                                                                                               |
| `pregenChunksInFlight`        | 整數，1 到 512     | `32`                                     | 一次造陸執行同時向遊戲請求多少個區塊。更多會讓生成執行緒更忙，而伺服器對被留在原地觀看的人回應更慢                                                                                                                                                                                                                                                                                                                   |
| `pregenBackup`                | 布林值             | `false`                                  | 預先生成完成後、玩家仍被留在原地時，將世界複製為一份原始備份。重置時還原的就是這份副本；資源包已不相符的副本會被丟棄並重新保留                                                                                                                                                                                                                                                                                       |
| `resetClearsEntities`         | 布林值             | `true`                                   | 地圖重置時移除所有非玩家的實體                                                                                                                                                                                                                                                                                                                                                                                       |
| `resetClearsScores`           | 布林值             | `true`                                   | 地圖重置時將資源包保存的每個目標歸零，讓新的一場比賽從零開始。隊伍本身會保留                                                                                                                                                                                                                                                                                                                                         |
| `resetClearsInventory`        | 布林值             | `false`                                  | 地圖重置時清空每位玩家的物品欄，包括盔甲和副手                                                                                                                                                                                                                                                                                                                                                                       |
| `resetClearsExperience`       | 布林值             | `false`                                  | 地圖重置時將每位玩家的經驗設回零級                                                                                                                                                                                                                                                                                                                                                                                   |
| `spawnChunkRadius`            | 整數，0 到 1024    | `128`                                    | 距出生點多遠（以方塊計）的區塊會一直保持載入，無論玩家是否在那裡，按每個方向 (方塊數 + 8) / 16 取整到整區塊，因此 128 表示每個方向保持 8 個區塊。世界啟動時，主世界會在伺服器就緒之前向每個方向多準備 4 個區塊的方形範圍，128 時即 25 乘 25 個區塊。0 表示不準備也不保持任何區塊，出生點區域會像其他地方一樣卸載。RDPL 用自己的票證保持這些區塊，因此在 1.21.1 上，只要此鍵生效，spawnChunkRadius 遊戲規則就不起作用 |
| `spawnChunkRadii`             | 清單               | 空                                       | 為主世界寫成 dimension=blocks 的半徑，如 minecraft:overworld=64，覆寫 spawnChunkRadius。只有主世界有出生點區塊，因此其他維度的項目不會改變任何東西                                                                                                                                                                                                                                                                   |
| `welcomeSays`                 | 清單               | `[WELCOME]`                              | 歡迎語，每次登入和預先生成之後以綠色顯示。單獨寫的項目是適用於所有地方的文字；dimension=message 項目會為該維度覆寫它，並且在每次抵達該維度時也問候玩家，例如 minecraft:the_nether=Welcome to the Nether!。= 之後的訊息為空會讓該維度靜音；空清單則什麼都不顯示。保持此預設值時，會使用每位玩家各自的語言                                                                                                             |
| `pregenBorderLimit`           | 整數，1 到 1875000 | `8192`                                   | 在拒絕向邊界造陸之前，邊界允許延伸的最遠距離，每個方向以區塊計。它的用途是防止一個失誤執行數週，而不是用來調高的，資源包也無法設定它。邊長 8192 的正方形含有 2.68 億個區塊 **僅限設定。**                                                                                                                                                                                                                            |
| `pregenDimensions`            | 清單               | `["minecraft:overworld"]`                | 新世界在哪些維度中造陸，依 id，按給定順序逐個進行                                                                                                                                                                                                                                                                                                                                                                    |
| `pregenDimensionsWhenEntered` | 清單               | 空                                       | 不在一開始、而是在有人首次踏入時才造陸的維度，範圍相同，同樣會留住所有人直到完成。同時列在這裡和 pregenDimensions 中的維度，直接在一開始完成                                                                                                                                                                                                                                                                         |
| `pregenRunningSays`           | 文字               | `World pregeneration running, %d%% done` | 世界生成期間玩家看到的進度訊息，其中 %d 是百分比，第二個 %s 是維度。留空則不告知玩家任何內容。保持此預設值時，會使用每位玩家各自的語言                                                                                                                                                                                                                                                                               |
| `pregenFinishedSays`          | 文字               | `World pregeneration finished`           | 生成完成時玩家看到的訊息。留空則不告知玩家任何內容。保持此預設值時，會使用每位玩家各自的語言                                                                                                                                                                                                                                                                                                                         |
| `pregenStoppedSays`           | 文字               | `World pregeneration stopped`            | 生成被提前停止時玩家看到的訊息。留空則不告知玩家任何內容。保持此預設值時，會使用每位玩家各自的語言                                                                                                                                                                                                                                                                                                                   |
| `pregenSpectatingSays`        | 文字               | `Spectating until the world is ready`    | 世界生成期間玩家被留在旁觀者模式時看到的螢幕中央訊息。留空則不顯示。保持此預設值時，會使用每位玩家各自的語言                                                                                                                                                                                                                                                                                                         |
| `pregenLogo`                  | 文字               | `center`                                 | 預先生成完成時標誌所在的位置：left、center 或 right，位於螢幕中央文字上方。它一律會顯示；未知的詞會按 center 處理                                                                                                                                                                                                                                                                                                    |
| `pregenBackupSays`            | 文字               | `Pack requested world backup`            | 複製該備份期間玩家看到的螢幕中央訊息。留空則不顯示                                                                                                                                                                                                                                                                                                                                                                   |
| `resetSays`                   | 文字               | `Pack requested map reset`               | /rdpl reset 還原地圖期間向玩家顯示的螢幕中央文字。留空則靜默重置                                                                                                                                                                                                                                                                                                                                                     |
| `resetSendsTo`                | 文字               | `spawn`                                  | 重置後玩家被送到哪裡：spawn、形如 x,y,z 的座標，或用 dimension:x,y,z 將他們送入另一個世界                                                                                                                                                                                                                                                                                                                            |
| `resetRuns`                   | 文字               | 空                                       | 重置清空地圖之後執行的函數，寫成 namespace:path。留空則不執行任何內容                                                                                                                                                                                                                                                                                                                                                |

### 虛空世界

*各群組的作用*

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

`control.voidWorld` 決定這個分組。虛空世界的生成及其平台。

| 設定                              | 類型                | 預設                      | 作用                                                                                                                                                                                                                                                       |
| --------------------------------- | ------------------- | ------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `voidWorld`                       | 布林值              | `false`                   | 將所列的維度生成為空無一物的空間，在出生點設一個平台，且沒有任何生物：原版的三個維度經由生成的預設組合處理，資源包自己的維度檔案則經由該檔案處理；所列的其他任何維度在造陸時會被清空。此選擇會在世界建立時隨世界保存，因此之後開啟或關閉不會改動既有的世界 |
| `voidWorldDimensions`             | 清單                | `["minecraft:overworld"]` | 哪些維度會變成虛空，依 id。留空表示沒有，或在 voidWorldDimensionsAreBlacklist 開啟時表示所有維度。其他模組在維度檔案中定義的維度同樣依 id 生效，透過對該檔案生成的覆寫；在程式碼中建立的維度會記錄一則警告，並保持原樣                                     |
| `voidWorldDimensionsAreBlacklist` | 布林值              | `false`                   | 開啟時，所列的維度是保持原樣的那些                                                                                                                                                                                                                         |
| `voidPlatformBlock`               | 方塊                | `minecraft:stone`         | 平台的組成方塊                                                                                                                                                                                                                                             |
| `voidPlatformHeight`              | 整數，-2032 到 2031 | `64`                      | 虛空世界平台所在的 y                                                                                                                                                                                                                                       |
| `voidPlatformSize`                | 整數，1 或更大      | `9`                       | 平台的寬度，向下取整為奇數，使其以出生點為中心                                                                                                                                                                                                             |
| `voidWorld`                       | 文字                | `default`                 | 虛空世界的生成及其平台 [default\|global\|off]                                                                                                                                                                                                              |

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
    "worldSeed": "Hollow Ridge",
    "worldName": "Ruby World",
    "worldType": "largebiomes",
    "worldTypeExceptions": ["flat", "debug_all_block_states"],
    "generatorOptions": {"seaLevel": 63, "useMansions": false},
    "worldMinHeight": -128,
    "worldMaxHeight": 384,
    "terrainOffset": 48,
    "deepStone": "minecraft:blackstone",
    "skyStone": "minecraft:end_stone",
    "skyShape": "islands",
    "skyIslands": 0.5,
    "skyThickness": 2.0,
    "skyHeights": [330, 380],
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

`control.terrain` 決定這個分組。世界建立時的名稱與種子碼、generatorOptions、洞穴區域、雲層高度，以及世界之間的接縫。

| 設定                    | 類型                | 預設                                 | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| ----------------------- | ------------------- | ------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldSeed`             | 字串                | 空                                   | 每個新世界使用的種子碼，寫成輸入時的樣子：數字照原樣使用，其他內容則依遊戲的做法轉換成數字。專用伺服器也用它建立世界，並將它寫入 `server.properties` 的 `level-seed`。留空則不干預這項選擇                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `worldName`             | 文字                | 空                                   | 開啟建立世界的畫面時，新世界的名稱。留空則保持遊戲取的名稱                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `worldType`             | 文字                | 空                                   | 塑形後的世界所建立於其上的世界類型，為 default、largebiomes、amplified 或 flat 之一，1.12.2 的名稱 customized 和 default_1_1 讀作 default；flat 是由 generatorOptions 的層建成的超平坦主世界，上面有資源包的城市。下方的形狀（高度、深層石頭、海平面、基岩、虛空）會生成為一個獨立的世界預設組合，列在世界畫面的「世界類型」之下，無論原先選了什麼，都會在那裡選定。專用伺服器會將它寫入 `server.properties` 的 `level-type`，指名該預設組合，若沒有任何塑形則指名遊戲自己的預設組合，除非 `level-type` 已指名 worldTypeExceptions 之一。留空則建立在 default 之上                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `worldTypeExceptions`   | 清單                | `["flat", "debug_all_block_states"]` | 玩家選擇後，生成的預設組合不去動的世界類型，例如 flat 或 debug_all_block_states。留空表示每種選擇都會被取代                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `generatorOptions`      | 文字                | 空                                   | 主世界的地形設定，寫成 JSON 物件，鍵為 1.12.2 customized 世界類型所寫入的那些。此處會讀取：seaLevel、useLavaOceans、oceanFluid（任何流體 ID，例如 minecraft:lava，或資料包中的流體如 mypack:molten_ruby，其方塊會填充海平面以下的海洋與河流；它優先於 useLavaOceans，未載入的流體會記入日誌，改由 useLavaOceans 決定）、fixedBiome，以及設為 false 的 useCaves、useRavines、useDungeons、useLavaLakes、useStrongholds、useVillages、useMineShafts、useTemples、useMonuments 和 useMansions。搭配 worldType flat 時，它改為各層，由下而上，如 1.12.2 超平坦文字 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village，或各層的清單；各層之後的數字是生態域，其後的 village、biome_1、mineshaft、stronghold、oceanmonument、lava_lake 和 decoration 會開啟對應的項目。只在世界建立時套用到世界。專用伺服器會將它寫入 `server.properties` 的 `generator-settings`，平坦各層則寫成遊戲自己的平坦 JSON，除非 `level-type` 已指名 worldTypeExceptions 之一。留空則讓地形維持世界類型所產生的樣子。礦石的鍵有 coalSize、coalCount、coalMinHeight 和 coalMaxHeight，iron、gold、redstone、diamond、dirt、gravel、granite、diorite 和 andesite 也都是同樣四個，而青金石是 lapisSize、lapisCount、lapisCenterHeight 和 lapisSpread。每個鍵的意義都與 1.12.2 相同：該大小的礦脈，每個區塊那麼多條，位於從最小值起、不含最大值的 y 範圍，青金石則圍繞其中心，向兩側各展開 spread。設定數量或高度會關閉遊戲對該礦石的其他放置，但惡地的額外金礦脈除外，那只受大小影響；單獨設大小會調整它每一條礦脈的大小，而只設數量或高度、不設大小，則使用 1.12.2 的預設大小。dungeonChance 是每個區塊的地城嘗試次數，深層地城按比例跟著變，lavaLakeChance 相對預設的 80 調整熔岩湖有多稀少，biomeSize 預設為 4，largebiomes 為 6，每一步將溫度與植被雜訊移動一個八度。地形形狀的鍵與 1.12.2 中的意義相同，且在 1.12.2 的預設值時讓陸地維持遊戲所產生的樣子：coordinateScale 與 heightScale，各為 684.412，決定地形雜訊取樣的範圍有多寬、多高，upperLimitScale 與 lowerLimitScale，各為 512，以及 mainNoiseScaleX、mainNoiseScaleY 和 mainNoiseScaleZ，80、160 和 80，用來除它，baseSize，8.5，biomeDepthWeight，1，以及 biomeDepthOffset，0，抬高或壓低陸地，stretchY，12，biomeScaleWeight，1，以及 biomeScaleOffset，0，拉伸或壓平它的丘陵，而 depthNoiseScaleX 與 depthNoiseScaleZ，各為 200，決定 1.12.2 深度雜訊的大小，這些鍵中任何一個都會開啟它，在預設 baseSize 下它會讓地表最多下移 2.4 個方塊或上移 0.85 個方塊；depthNoiseScaleExponent 會被讀取但不起作用，與 1.12.2 相同。riverSize 預設為 4，每一步將河流雜訊移動一個八度。waterLakeChance 會在沙漠以外加入水湖，地下是每那麼多個區塊一座，地表則約是每那個數目的三分之四倍個區塊一座，除非 useWaterLakes 為 false。礦石、地城和湖泊的鍵只會重新塑造以生成的預設組合建立的世界中遊戲自己的地物：其他維度，以及沒有使用該預設組合建立的世界，保留遊戲自己的 |
| `worldMinHeight`        | 整數，-2032 到 2016 | `-64`                                | 主世界最低的方塊，為 16 的倍數，最低到 -2032。遊戲自己的底部是 -64；調得更低會在原版地形下方形成一個深層世界，那裡是實心石頭，直到世界生成層將它挖開，或 noiseCaves 把遊戲的洞穴延伸下去。只透過生成的預設組合套用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `worldMaxHeight`        | 整數，-2016 到 2032 | `320`                                | 主世界頂端上方的那個方塊，為 16 的倍數，最高到 2032，比 worldMinHeight 高出至多 4064。遊戲自己的頂端是 320；調得更高會在原版地形上方留下開闊的天空                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `terrainOffset`         | 整數，0 到 4064     | `0`                                  | 整個原版地形範圍向上平移多少個方塊，為 16 的倍數。海平面和地表層隨之移動，洞穴、礦脈和生態域深度也跟著平移後的陸地走，而遊戲自己放置的地物保持原有高度。worldMaxHeight 必須維持在 320 加上偏移量以上，否則平移會被拒絕。只透過生成的預設組合套用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `deepStone`             | 文字                | 空                                   | 當 worldMinHeight 低於 -64 時，原版地形下方的世界所用的方塊，例如資源包自己的深板岩。它在 -64 下方的八層中逐漸融入深板岩，就像深板岩逐漸融入石頭一樣。留空則保持石頭                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `noiseCaves`            | 文字                | `off`                                | 當 worldMinHeight 低於 -64 時，遊戲的洞穴、隧道、麵條洞穴和含水層在哪裡延續：off 讓原版地形下方的世界保持為實心深層石頭，留給世界生成層去挖；deep 將它們一路延伸到底部，並把熔岩湖移到最底下十層；world 在此版本上意義相同，因為原版地形本來就有它們                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `skyStone`              | 文字或清單          | 空                                   | 當 worldMaxHeight 超過 320 時，原版地形上方的天空所用的方塊，由遊戲自己的洞穴雜訊塑成浮空陸地，所以下方是洞穴之處，上方就是島嶼。其上的表層和填充層來自各生態域的地表規則。留空則天空保持空無                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `skyShape`              | 文字或清單          | `islands`                            | 原版地形上方的天空被塑成什麼：islands 為浮空陸地，caves 為鑿穿了洞穴的實心岩石。只有在 skyStone 指名了方塊時才會使用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `skyIslands`            | 小數，-1.0 到 1.0   | `0.5`                                | 天空聚成島嶼的容易程度，從 -1 的幾乎連成一片陸地，到 1 的空蕩蕩的天空。越低，陸地散布在天空中越多處；越高，剩下的碎片越少也越小。只有在 skyStone 指名了方塊且 skyShape 為 islands 時才會使用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `skyThickness`          | 小數，0.0 到 8.0    | `2.0`                                | 天空島嶼有多厚實。越高，島嶼越飽滿；越低，島嶼越中空，邊緣也逐漸薄到消失。只有在 skyStone 指名了方塊且 skyShape 為 islands 時才會使用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `skyHeights`            | 清單                | 空                                   | 天空島嶼可達到的最低與最高方塊，寫成兩個數字。留空則填滿原版地形上方的整個世界。只有在 skyStone 指名了方塊時才會使用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `worldSpawn`            | 文字                | 空                                   | 每個新世界的出生位置，寫成 x,z 或 x,y,z。沒有 y 時，採用世界的平均地面高度，即海平面上一格，在平坦世界上則是各層的頂端，遊戲再像處理任何出生點那樣，在那裡找到安全的落腳處。只在世界建立時套用到世界。留空則把選擇交給遊戲                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `worldBorder`           | 整數，0 到 60000000 | `0`                                  | 每個新世界的世界邊界寬度，以方塊計。只在世界建立時套用到世界。0 表示讓邊界保持在遊戲設定之處                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `worldTime`             | 整數，-1 到 23999   | `-1`                                 | 鎖定主世界的時間，以刻計，與 /time set 接受的數字相同，所以 18000 是午夜。時鐘停止且永不移動：/time set 無法移動它，鎖定期間日數不會繼續計數，移除該設定後時間會從鎖定的時刻繼續運行。-1 表示讓時間照常運行                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `caveRegionPlainWeight` | 整數，0 或更大      | `4`                                  | 單純、沒有區域的地下，相對於各洞穴區域自身權重的權重。越高，地下沒有任何區域的部分越多                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `caveRegionCells`       | 整數，16 或更大     | `128`                                | 洞穴區域儲存格的寬度，以方塊計。資源包的洞穴區域會以大約這個大小的儲存格塗繪在地下                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| `caveRegionCellsY`      | 整數，16 或更大     | `64`                                 | 洞穴區域儲存格的高度，以方塊計                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `worldGravity`          | 清單                | 空                                   | 縮放重力，寫成原版的倍率，1.0 為不變，0.17 近似月球，對所有實體生效。單獨寫的數值涵蓋每個維度，寫成 dimension=value 的項目則只涵蓋該維度，且優先於單獨的數值。留空則不更動重力                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `worldFallDamage`       | 清單                | 空                                   | 以同樣方式縮放摔落傷害，0.5 為減半，2.0 為加倍                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `worldJumpStrength`     | 清單                | 空                                   | 以同樣方式縮放跳躍力。它乘以跳躍的速度，所以高度的增長比數值更快：1.5 大約讓跳躍高度翻一倍                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `worldTerminalVelocity` | 清單                | 空                                   | 以同樣方式縮放生物或玩家墜落的最高速度，0.5 為以原版最高速度的一半墜落                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| `weatherCeiling`        | 清單                | 空                                   | 雨和雪所能到達的最高 y，寫成 dimension=y 項目。單獨寫的數字涵蓋每個維度。在它之上不下雨、雪不堆積、鍋釜不裝水、閃電不落下，也不繪製任何降水；在它之下，天氣不變。冰取決於溫度而非降水，所以在該線之上仍會形成。留空表示沒有上限                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `cloudHeight`           | 清單                | 空                                   | 雲繪製的 y，寫成 dimension=y 項目。單獨寫的數字涵蓋每個維度。它優先於資源包維度自己的 `cloudHeight`。留空則保持遊戲自己的雲層高度，主世界為 192                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| `worldBelow`            | 清單                | 空                                   | 把另一個維度疊在這個維度之下：從世界底部掉出去，會把你帶進所指名的維度，在相同的 x 和 z 抵達其天花板之下，仍在墜落。項目寫成 dimension=target，例如 minecraft:overworld=minecraft:the_nether，把地獄懸在主世界之下；單獨寫的 id 涵蓋每個維度。要挖穿需要移除地板的基岩，這由 worldSeamBedrock 決定。留空表示地板仍是地板                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `worldAbove`            | 清單                | 空                                   | 對天花板同樣的設定：升過世界頂端會把你帶進所指名的維度，在其地板之上抵達。寫法與 worldBelow 相同                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| `worldSeamEntities`     | 布林值              | `true`                               | 掉落物、生物和其他實體是否也能通過世界接縫，或只有玩家可以。騎乘者與坐騎一次通過一個                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| `worldSeamBedrock`      | 布林值              | `false`                              | 仍然保留接縫邊界上的基岩。關閉時，地板或天花板帶有 worldBelow 或 worldAbove 接縫的維度不會在那裡生成基岩，所以可以挖出通路。已生成的區塊保持原有的樣子                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |

### 伺服器

*各群組的作用*

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

`control.server` 決定這個分組：資源包可以設定的 `server.properties` 各行，以及遊戲模式、難易度，和對區域網路開放的世界中的指令。在專用伺服器上，資源包在此設定的每個值，都會在伺服器啟動時寫入 `server.properties`，所以該檔案寫明了目前生效的內容，伺服器已經讀取過的那些值也會一併設定到它身上。單人世界則取用整合伺服器所擁有的內容，各列的說明中有寫明。留空，或數字寫 `-1`，則保留伺服器本身的值，而 `control.server` 設為 `off` 時，每一行都維持伺服器原有的樣子。

| 設定                      | 類型                 | 預設   | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ------------------------- | -------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldGameMode`           | 文字                 | 空     | 每個新世界以哪種方式開始，為 survival、hardcore、creative、adventure 或 spectator 之一。Hardcore 是死亡即結束整個世界的生存模式，全世界通用，與世界畫面上的選項相同。留空則保持建立世界的人所選的。世界畫面只提供 survival、hardcore 和 creative，所以 adventure 和 spectator 是在建立世界時設定的。專用伺服器在每次啟動時會把每個世界設為其 server.properties 的模式，所以在那裡，資源包的模式會在世界載入之前寫入 server.properties（gamemode 和 hardcore） |
| `privacy`                 | 布林值               | `true` | 遊戲的遙測與聊天舉報是否關閉：不傳送任何遙測事件，用戶端不簽署任何聊天訊息，伺服器不保留聊天工作階段也不要求有，所以沒有任何訊息可被舉報。省略此項的資源包會採用 `tweaks` 類別的 `privacy` 設定選項。在下一次加入世界或伺服器時生效                                                                                                                                                                                                                           |
| `worldLanCommands`        | 布林值               | `true` | 將單人世界開放給區域網路的玩家，是否可以為所有加入的人開啟指令。`false` 會讓「開放區域網路」畫面的「允許作弊」按鈕變灰並固定在關閉，無論如何要求，包括 `/publish`，世界開放時都不帶指令                                                                                                                                                                                                                                                                       |
| `worldDifficulty`         | 清單                 | 空     | 鎖定難易度，為 peaceful、easy、normal 或 hard 之一。單獨寫的難易度涵蓋每個維度，寫成 dimension=difficulty 的項目，如 minecraft:the_nether=hard，則只涵蓋該維度，且優先於單獨的難易度。世界自己的設定保持原樣，並在移除該項目後恢復。專用伺服器會把主世界的難易度寫入 `server.properties` 的 `difficulty`。留空則保持所選的                                                                                                                                    |
| `worldForceGameMode`      | 布林值               | 空     | 加入的玩家是否每次都會被放回伺服器的遊戲模式，即 `force-gamemode` 那一行。對區域網路開放的世界本來就是這樣，而 `false` 在那裡也會停止這個行為                                                                                                                                                                                                                                                                                                                 |
| `worldPvp`                | 布林值               | 空     | 玩家之間是否可以互相傷害，即 `pvp` 那一行。單人世界也會採用                                                                                                                                                                                                                                                                                                                                                                                                   |
| `worldFlight`             | 布林值               | 空     | 在生存模式中飛行的玩家是否不被理會而非被踢出，即 `allow-flight` 那一行。單人世界也會採用                                                                                                                                                                                                                                                                                                                                                                      |
| `worldSpawnProtection`    | 整數，-1 或更大      | `-1`   | 出生點周圍多少個方塊內只有管理員可以建造，即 `spawn-protection` 那一行，0 表示沒有。只有專用伺服器會保護其出生點                                                                                                                                                                                                                                                                                                                                              |
| `worldNether`             | 布林值               | 空     | 是否可以進入地獄，即 `allow-nether` 那一行。`false` 在單人世界中也會將它關閉                                                                                                                                                                                                                                                                                                                                                                                  |
| `worldCommandBlocks`      | 布林值               | 空     | 指令方塊是否執行，即 `enable-command-block` 那一行。單人世界本來就會執行它們，而 `false` 在那裡也會將它們關閉                                                                                                                                                                                                                                                                                                                                                 |
| `worldIdleTimeout`        | 整數，-1 或更大      | `-1`   | 玩家閒置多少分鐘後會被踢出，即 `player-idle-timeout` 那一行，0 表示永不。單人世界也會採用                                                                                                                                                                                                                                                                                                                                                                     |
| `worldMotd`               | 文字                 | 空     | 伺服器列表中顯示在伺服器名稱下方的那一行，即 `motd` 那一行。對區域網路開放的單人世界會以它取代擁有者與世界名稱                                                                                                                                                                                                                                                                                                                                                |
| `worldMaxSize`            | 整數，-1 到 29999984 | `-1`   | 世界邊界能延伸到離中心最遠多少個方塊，即 `max-world-size` 那一行。單人世界也會採用                                                                                                                                                                                                                                                                                                                                                                            |
| `worldStructures`         | 布林值               | 空     | 新世界是否生成結構，即 `generate-structures` 那一行，以及世界畫面上的「生成結構」選項。只在世界建立時套用到世界                                                                                                                                                                                                                                                                                                                                               |
| `worldSpawnMonsters`      | 布林值               | 空     | 敵對生物是否生成，即 `spawn-monsters` 那一行。`false` 在單人世界中也會阻止它們                                                                                                                                                                                                                                                                                                                                                                                |
| `worldSpawnAnimals`       | 布林值               | 空     | 動物是否生成，即 `spawn-animals` 那一行。`false` 在單人世界中也會阻止它們                                                                                                                                                                                                                                                                                                                                                                                     |
| `worldSpawnNpcs`          | 布林值               | 空     | 村民是否生成，即 `spawn-npcs` 那一行。`false` 在單人世界中也會阻止它們                                                                                                                                                                                                                                                                                                                                                                                        |
| `worldViewDistance`       | 整數，-1 到 32       | `-1`   | 專用伺服器向每位玩家傳送世界的區塊距離，即 `view-distance` 那一行。單人世界改為遵循渲染距離                                                                                                                                                                                                                                                                                                                                                                   |
| `worldSimulationDistance` | 整數，-1 到 32       | `-1`   | 專用伺服器在每位玩家周圍讓世界持續運轉的區塊距離，即 `simulation-distance` 那一行。單人世界改為遵循自己的設定                                                                                                                                                                                                                                                                                                                                                 |

### 配方

*各群組的作用*

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

`control.recipes` 決定這個分組。合成配方與熔爐配方的封鎖及其白名單。熔爐封鎖也會連帶封鎖高爐、煙燻爐和營火的配方，因為 1.12.2 把所有烹飪配方都放在同一份熔爐清單中。切石機與鍛造配方永遠不會被封鎖，因為 1.12.2 沒有這些。

`config/resourcedatapackloader-common.toml`

```toml
[recipes]
blockRecipes = true
recipeWhitelist = ["minecraft", "mypack"]
blockedRecipeMods = ["tconstruct"]
recipeMatch = "recipe"
blockFurnaceRecipes = true
furnaceWhitelist = ["minecraft", "mypack"]
blockedFurnaceMods = ["tconstruct"]
logBlockedRecipes = true
furnace = true
removals = true
skipMissingItems = true
```

| 設定                  | 類型                         | 預設            | 作用                                                                                                                                                                                |
| --------------------- | ---------------------------- | --------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `blockRecipes`        | 布林值                       | `false`         | 移除所有合成配方，但 `recipeWhitelist` 中的模組所提供的除外。白名單預設只有 `minecraft`，所以原版配方會保留，其他所有模組的配方都會被移除；請列出你自己資源包的命名空間以保留其配方 |
| `recipeWhitelist`     | 清單，內容為模組 id          | `["minecraft"]` | 其合成配方得以保留的模組                                                                                                                                                            |
| `blockedRecipeMods`   | 清單，內容為模組 id          | 空              | 其合成配方會被直接移除的模組，不論白名單怎麼說                                                                                                                                      |
| `recipeMatch`         | `recipe`、`output` 或 `both` | `recipe`        | 封鎖合成配方時，從哪裡讀取模組 id：配方自己的名稱、它製作出的物品，或兩者皆可，任一者符合就封鎖，任一者在白名單中就放行                                                             |
| `blockFurnaceRecipes` | 布林值                       | `false`         | 對熔爐配方做同樣的事，模組依產出的物品判定                                                                                                                                          |
| `furnaceWhitelist`    | 清單，內容為模組 id          | `["minecraft"]` | 其熔爐配方得以保留的模組                                                                                                                                                            |
| `blockedFurnaceMods`  | 清單，內容為模組 id          | 空              | 其熔爐配方會被直接移除的模組                                                                                                                                                        |
| `logBlockedRecipes`   | 布林值                       | `true`          | 記錄每個模組被封鎖數量的統計                                                                                                                                                        |
| `furnace`             | 布林值                       | `true`          | 套用 furnace/*.json 檔案，這些檔案可新增及移除熔爐燒煉配方 **僅限設定。**                                                                                                           |
| `removals`            | 布林值                       | `true`          | 套用 recipe_removals/*.json 檔案，這些檔案依名稱、命名空間或產出刪除合成配方 **僅限設定。**                                                                                         |
| `skipMissingItems`    | 布林值                       | `true`          | 略過使用了未註冊物品的配方，而不是讓它們失敗。數量只會記錄一次 **僅限設定。**                                                                                                       |

### 指令

*各群組的作用*

`control.commands` 決定這個分組。誰可以執行此模組自己的指令：goto 的權限等級。

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "locateLevel": 2,
    "gotoLevel": 3,
    "gotoNextLevel": 3,
    "gotoBackLevel": 3,
    "gotoPlaceLevels": ["rubyworld:crypt=2"]
  }
}
```

`config/resourcedatapackloader-common.toml`

```toml
[commands]
locateLevel = 2
gotoLevel = 3
gotoNextLevel = 3
gotoBackLevel = 3
gotoPlaceLevels = ["rubyworld:crypt=2"]
```

| 設定              | 類型         | 預設 | 作用                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| ----------------- | ------------ | ---- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `locateLevel`     | 整數，0 到 4 | `2`  | 執行原版 /locate 所需的權限等級。2 就是原版的等級，因此在設定之前不會有任何變化。0 讓任何玩家都能執行，3 只留給管理員，4 則限定為 4 級管理員                                                                                                                                                                                                                                                                                                                                        |
| `gotoLevel`       | 整數，0 到 4 | `3`  | 執行 /rdplserver goto <name> 所需的權限等級，該指令會把發送者帶到最近的一處。3 是管理員，也是指令其他部分所在的等級。2 讓協管員能執行，0 讓任何玩家都能輸入。無論此項設定為何，指令方塊或主控台一律能執行，因此資源包可以把這個跳轉放在按鈕或壓力板上，而不必把指令的其餘部分交給玩家。無論此項設定為何，/rdplserver 的其他部分都維持在 3                                                                                                                                           |
| `gotoNextLevel`   | 整數，0 到 4 | `3`  | 執行 /rdplserver goto <name> next 所需的權限等級，該指令會略過上一次帶發送者去的那一處，改找另一處。刻度與 gotoLevel 相同                                                                                                                                                                                                                                                                                                                                                           |
| `gotoBackLevel`   | 整數，0 到 4 | `3`  | 執行 /rdplserver goto <name> back 所需的權限等級，該指令會把發送者送回上一處。刻度與 gotoLevel 相同                                                                                                                                                                                                                                                                                                                                                                                 |
| `gotoPlaceLevels` | 清單         | 空   | 針對單一地點的權限等級，寫成 name=level 項目，每行一個，僅對該地點覆寫上面三項設定，且對它的三種形式全都生效。名稱就是你在 goto 之後會輸入的內容，所以可以是原版的名稱，如 Village 或 Mansion，或資源包以 locateAs 為自己的結構註冊的名稱。刻度相同：3 為管理員，2 為協管員，0 為任何人；無論此項設定為何，指令方塊或主控台都可以前往任何地方。資源包因此可以對自己的遺跡開放前往的途徑，同時讓所有原版結構保持關閉，或反過來。沒有任何東西註冊過的名稱會被忽略，並在日誌中留下註記 |

### 世界生成，僅限設定

*各群組的作用*

這些 `worldgen` 鍵不屬於任何分組，只屬於設定檔。

`config/resourcedatapackloader-common.toml`

```toml
[worldgen]
worldgenDebug = false
worldTemplate = "auto"
tellWorldType = true
worldBorderLimit = 60000000
retrogenKey = "0000"
retrogenChunksPerTick = 2
```

| 設定                    | 類型                | 預設       | 作用                                                                                                                                                     |
| ----------------------- | ------------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `worldgenDebug`         | 布林值              | `false`    | 將其他訊息所指的除錯行寫入 logs/rdpl.log，例如哪個資源包提供了某個檔案，以及每個指令做了什麼。非常冗長                                                   |
| `worldTemplate`         | 文字                | `auto`     | 套用哪個世界範本的設定。資源包在 worldtemplates/*.json 中新增範本，你在這裡以 namespace:name 指名。'auto' 會從優先順序最高的資源包挑選範本。留空則不使用 |
| `tellWorldType`         | 布林值              | `true`     | 玩家加入以生成的預設組合建立的世界時，在聊天中告知是哪個範本塑造了它。資源包無法設定此項                                                                 |
| `worldBorderLimit`      | 整數，1 到 60000000 | `60000000` | 資源包可透過 worldBorder 要求的最寬邊界。要求更多的資源包會被拒絕，邊界保持在遊戲設定之處。資源包無法設定此項                                            |
| `retrogenKey`           | 文字                | `0000`     | 修改此項可使每個區塊對每一個世界生成項目再次有資格進行回溯生成。新的礦脈會疊加在既有的內容之上                                                           |
| `retrogenChunksPerTick` | 整數，1 或更大      | `2`        | 每刻要補上多少個已生成的區塊。越高越快，但越卡頓                                                                                                         |

### `packs` 類別

*各群組的作用*

如何找到並提供資源包資料夾。僅限設定。

`config/resourcedatapackloader-common.toml`

```toml
[packs]
rootDirectory = "rdploader"
overrideResourcePacks = true
warnOnCaseMismatch = true
logContents = false
traceUnresolvedVariables = false
```

| 設定                       | 類型   | 預設        | 作用                                                                                                    |
| -------------------------- | ------ | ----------- | ------------------------------------------------------------------------------------------------------- |
| `rootDirectory`            | 文字   | `rdploader` | 載入資源包的資料夾，相對於 .minecraft 目錄。也可使用絕對路徑。需要重新啟動                              |
| `overrideResourcePacks`    | 布林值 | `true`      | 將素材資源包插入玩家所選資源包與世界自身資料包之上。名為 RDPLO... 的資源包一律覆寫，RDPLN... 則永不覆寫 |
| `warnOnCaseMismatch`       | 布林值 | `true`      | 當某個檔案只是因為檔案系統不分大小寫才符合時發出警告。這類資源包在 Linux 上會失效                       |
| `logContents`              | 布林值 | `false`     | 記錄找到的每個資源包及其提供的檔案數量                                                                  |
| `traceUnresolvedVariables` | 布林值 | `false`     | 第一次請求檔名中含有「#」的檔案時記錄堆疊追蹤，指出是誰請求的                                           |

### `content` 類別

*各群組的作用*

方塊、物品、流體，以及資源包定義的其他一切。僅限設定。

`config/resourcedatapackloader-common.toml`

```toml
[content]
load = true
vanillaClients = false
sounds = true
fuels = true
tooltips = true
potions = true
brewing = true
villagers = true
entities = true
overrides = true
disabled = true
hardness = true
shovelPaths = true
shovelPathBecomes = ""
shovelPathReverts = ""
hoeTilling = true
hoeTillsInto = ""
caneMaxHeight = 3
cactusMaxHeight = 3
```

| 設定                | 類型           | 預設    | 作用                                                                                                                                                                                     |
| ------------------- | -------------- | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `load`              | 布林值         | `true`  | 註冊資源包定義的方塊、物品、流體、材料和創造模式分頁，並載入它們的曝露設定。需要重新啟動                                                                                                 |
| `vanillaClients`    | 布林值         | `false` | 為純原版用戶端提供服務：不註冊任何資源包內容，沒有方塊、物品、流體或創造模式分頁，也不載入曝露設定，因此沒有安裝此模組的用戶端也能加入。所有僅存在於伺服器上的功能仍然有效。需要重新啟動 |
| `sounds`            | 布林值         | `true`  | 註冊 sounds/*.json 所指名的音效事件，讓資源包能附帶自己的音訊                                                                                                                            |
| `fuels`             | 布林值         | `true`  | 套用 fuels/*.json 檔案，這些檔案為物品設定熔爐燃燒時間                                                                                                                                   |
| `tooltips`          | 布林值         | `true`  | 套用 tooltips/*.json 檔案，它們為物品和方塊加上工具提示行，並在附魔名稱下方加上說明                                                                                                      |
| `potions`           | 布林值         | `true`  | 註冊資源包中 potions/*.json 與 potion_types/*.json 所描述的藥水效果與藥水類型。需要重新啟動                                                                                              |
| `brewing`           | 布林值         | `true`  | 套用 brewing/*.json 檔案，這些檔案新增釀造台配方                                                                                                                                         |
| `villagers`         | 布林值         | `true`  | 註冊 villagers/*.json 所描述的村民職業，並套用 trades/*.json 中的交易。需要重新啟動                                                                                                      |
| `entities`          | 布林值         | `true`  | 註冊資源包中 entities/*.json 所描述的實體變體。需要重新啟動                                                                                                                              |
| `overrides`         | 布林值         | `true`  | 套用 overrides/<namespace>/<name>.json 檔案，這些檔案更動已存在的方塊、物品和藥水類型的屬性，無論是原版還是模組的                                                                        |
| `disabled`          | 布林值         | `true`  | 套用 disabled/*.json 檔案，這些檔案讓方塊與物品退出遊戲：沒有創造模式分頁、JEI 條目、配方、戰利品、交易、標籤，無法放置、使用或撿起，且它們的物品堆會被刪除                              |
| `hardness`          | 布林值         | `true`  | 套用 hardness/*.json 檔案，這些檔案為一組方塊設定挖掘時間與爆炸抗性倍率，依每個方塊位置抽取                                                                                              |
| `shovelPaths`       | 布林值         | `true`  | 讓鏟子將標記為 behavesAs path 的方塊變成土徑，潛行時則將土徑還原                                                                                                                         |
| `shovelPathBecomes` | 文字           | 空      | 鏟子將這些方塊變成什麼。留空則使用土徑                                                                                                                                                   |
| `shovelPathReverts` | 文字           | 空      | 以鏟子潛行時將土徑變回什麼。留空則使用泥土                                                                                                                                               |
| `hoeTilling`        | 布林值         | `true`  | 讓鋤頭耕標記為 behavesAs till 的方塊                                                                                                                                                     |
| `hoeTillsInto`      | 文字           | 空      | 鋤頭將這些方塊變成什麼。留空則使用農地                                                                                                                                                   |
| `caneMaxHeight`     | 整數，1 到 255 | `3`     | 原版甘蔗能長到多高。原版為 3。資源包定義的甘蔗方塊使用自己的生長區段，並忽略此項                                                                                                         |
| `cactusMaxHeight`   | 整數，1 到 255 | `3`     | 同上，針對原版仙人掌                                                                                                                                                                     |

### `data` 類別

*各群組的作用*

戰利品、函數和登錄表名稱。僅限設定。

`config/resourcedatapackloader-common.toml`

```toml
[data]
lootInjections = true
playerLoot = true
registryRemaps = true
anvils = true
blockDrops = true
functions = true
```

| 設定             | 類型   | 預設   | 作用                                                                                                               |
| ---------------- | ------ | ------ | ------------------------------------------------------------------------------------------------------------------ |
| `lootInjections` | 布林值 | `true` | 套用 loot_injections/*.json 檔案，這些檔案為已存在的戰利品表新增獎勵池，而不是取代整張表                           |
| `playerLoot`     | 布林值 | `true` | 套用 player_loot/*.json 檔案，這些檔案在玩家死亡時抽取戰利品表並掉落其產物，疊加在物品欄之上或取而代之             |
| `registryRemaps` | 布林值 | `true` | 套用 registry_remap 檔案，這些檔案重新命名登錄表項目，讓重新命名之前儲存的世界保留它們的方塊與物品，而不是遺失     |
| `affixes`        | 布林值 | `true` | 套用 affixes/*.json 和 gems/*.json 檔案，讓物品在玩家第一次持有時獲得隨機屬性加成與插槽，並讓鐵砧把寶石嵌入插槽 |
| `anvils`         | 布林值 | `true` | 套用 anvils/*.json 檔案，這些檔案讓鐵砧以等級花費為物品加上指定的附魔，在取出時獲得進度，並在那之前禁止使用該物品  |
| `enchanting`     | 布林值 | `true` | 套用 enchanting/*.json 檔案，依附魔、依物品以及整張附魔台改變附魔台提供什麼、要求什麼、拿走什麼                    |
| `blockDrops`     | 布林值 | `true` | 套用 block_drops/*.json 檔案，這些檔案在方塊每次破壞時新增或取代其掉落物（包括經驗值），適用於資源包並不擁有的方塊 |
| `functions`      | 布林值 | `true` | 從資源包載入 .mcfunction 檔案，使它們在每個世界都能運作                                                            |

### `tweaks` 類別

*各群組的作用*

對原版行為的小改動。僅限設定，唯獨 `privacy` 資源包也能設定；參見[額外功能：原版調整](#額外功能原版調整)。

`config/resourcedatapackloader-common.toml`

```toml
[tweaks]
promptLeafDecay = true
lenientPaths = true
unbreakableSpawners = false
experimentalWarning = false
privacy = true
darkSplash = true
```

| 設定                  | 類型   | 預設    | 作用                                                                                                                                                                                                                                                                                                                 |
| --------------------- | ------ | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `promptLeafDecay`     | 布林值 | `true`  | 失去樹木的樹葉會在一秒內枯萎，而不是等待隨機刻                                                                                                                                                                                                                                                                       |
| `lenientPaths`        | 布林值 | `true`  | 土徑可以做在方塊下方，上方放置方塊時也會保留                                                                                                                                                                                                                                                                         |
| `unbreakableSpawners` | 布林值 | `false` | 生怪磚無法被挖掉或炸毀。創造模式仍能移除它們。需要重新啟動                                                                                                                                                                                                                                                           |
| `experimentalWarning` | 布林值 | `false` | 建立或開啟世界時顯示遊戲的實驗性設定警告。關閉則視同你已點選繼續                                                                                                                                                                                                                                                     |
| `privacy`             | 布林值 | `true`  | 關閉遊戲的遙測與聊天舉報：不會傳送或記錄任何遙測事件，用戶端不簽署任何聊天訊息，伺服器不保留聊天工作階段也不要求有，因此任何人傳送的訊息都無法被舉報，且用戶端不會顯示伺服器未強制安全聊天的警告通知。資源包可在世界範本的 `settings` 中以 `privacy` 設定，位於[伺服器](#伺服器)分組。在下一次加入世界或伺服器時生效 |
| `darkSplash`          | 布林值 | `true`  | 以深色繪製載入畫面，並用資源包載入器的標誌取代遊戲的標誌：標誌在畫面建立時被替換，且遊戲自己的單色標誌選項若仍為關閉則會被開啟，於下次啟動時生效。關閉則保持該選項原樣                                                                                                                                               |

---

# 其他模組

## Blast Plaster 整合

*其他模組*

`<namespace>/blastplaster/*.json`

檔案名稱由你決定，只有資料夾會被讀取，而且多個檔案可以疊加。

Blast Plaster 負責處理爆炸後的行為：逐方塊修復彈坑、考量樹木結構的砍伐、掉落物控制。單獨使用時，它讀取一份全域設定。由資源包驅動時，它會**依維度**作答，由資源包附上決定，而不是要求玩家去編輯設定檔。沒有資源包檔案，或沒有安裝 Blast Plaster，這裡的一切都不起作用，該資料夾會被略過，並在日誌中留下一行。

寫在檔案最上層的鍵會套用到所有地方；`dimensions` 區段則依維度 id 為單一維度覆寫它們。資源包沒有指名的任何項目，都保留 Blast Plaster 自己設定檔所寫的內容，所以資源包只設定它在意的那幾項，其餘不去動。

這裡一次列出所有鍵。實際的檔案只寫需要的那些。

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

`blockConversions` 決定被炸到的方塊變成什麼，而不是照原樣回來。一條規則寫成 `<source>=<result>[@chance]`：來源是方塊 id，或前面加 `#` 的方塊標籤；結果是方塊 id，或寫 `nothing` 讓該空間保持空著；機率介於 0.0 到 1.0，預設為 1.0。第一條符合的規則勝出，所以具體的規則要放在寬泛的規則之上，而已經是某條規則結果的方塊永遠不會再被轉換，如此一面牆每次爆炸只會磨損一級，而不會被磨到什麼都不剩。

**完全原版的外觀：** `EJECT_DROPS`，加上 `healFullTrees`、`enableFakeTossedBlocks`、`enableExplosionFlash`、`enableExplosionSmoke`、`preventMobDrops` 和 `playerTNTAlwaysDrops` 全部關閉。每個鍵都可以依維度設定。

**原版用戶端**看不到任何異樣。閃光是唯一會放置方塊的功能，所以設定了 `vanillaClients` 時它會被強制關閉；其餘全是原版用戶端看得懂的粒子和物品。

不是資源包鍵的項目：Blast Plaster 的除錯日誌，以及它把原木對應到樹葉的配對（樹木的辨識在整個遊戲中必須只有一個答案）。兩者都留在 Blast Plaster 自己的設定檔中。

---

# 參考資料

## 值清單

*值清單*

### 接受的名稱

*值清單*

下方表格中凡是寫著「其中一種材料」之類的地方，解析器都接受以下這些名稱。無法辨識的名稱會記入日誌，並以預設值取代。

**方塊材料。** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`。遊戲本身已不再有材料；每個名稱的作用就是該材料在 1.12.2 上的作用：它決定地圖顏色、方塊是否需要工具才會掉落東西、活塞如何對待它、熔岩是否會點燃它、流動的液體是否會沖走它，以及放置的方塊是否會取代它。

**音效類型。** `wood`、`ground`、`plant`、`stone`、`metal`、`glass`、`cloth`、`sand`、`snow`、`ladder`、`anvil`、`slime`。

**地圖顏色。** `air`、`grass`、`sand`、`cloth`、`tnt`、`ice`、`iron`、`foliage`、`snow`、`clay`、`dirt`、`stone`、`water`、`wood`、`quartz`、`adobe`、`magenta`、`light_blue`、`yellow`、`lime`、`pink`、`gray`、`silver`、`cyan`、`purple`、`blue`、`brown`、`green`、`red`、`black`、`gold`、`diamond`、`lapis`、`emerald`、`obsidian`、`netherrack`。

**彩現層。** `solid`、`cutout`、`cutout_mipped`、`translucent`。留空時，方塊會依其類型挑選合適的一個。

**稀有度。** `common`、`uncommon`、`rare`、`epic`。

**火把粒子。** `none`、`flame`、`colored`。`colored` 使用 `particleColor`。

**工具類別。** `pickaxe`、`axe`、`shovel`、`hoe`、`sword`。

**盔甲欄位。** `head` 或 `helmet`、`chest` 或 `chestplate`、`legs` 或 `leggings`、`feet` 或 `boots`。

**色調。** `biome`、`none`，或六位數的十六進位顏色。定義中任何地方的顏色都是十六進位，前面可以有或沒有 `#`。

**行為**，用於 `behavesAs`。`till`、`path`、`bush`、`animals`。

**植物類型**，用於 `plantTypes`。`plains`、`desert`、`beach`、`cave`、`water`、`nether`、`crop`。僅限 1.20.1；1.21.1 會讀取該鍵但忽略它。

**生態域類型**，表格寫「生態域類型清單」之處，代表生態域標籤的詞，用於 `biomeTypes`、生態域的 `types`、範本的 `roles` 以及 `biomes` 區段：`ocean`, `deepocean`, `beach`, `river`, `mountain`, `mesa`, `hills`, `coniferous`, `jungle`, `forest`, `savanna`, `overworld`, `nether`, `end`, `hot`, `cold`, `sparse`, `dense`, `wet`, `dry`, `spooky`, `dead`, `lush`, `mushroom`, `magical`, `rare`, `plateau`, `modified`, `water`, `desert`, `plains`, `swamp`, `sandy`, `snowy`, `wasteland`, `void`。原版的詞對應到 `minecraft:is_*` 標籤，其餘對應到慣例標籤，1.20.1 上是 `forge:is_*`，1.21.1 上是 `c:is_*`。寫出完整標籤的 `minecraft:is_forest` 或 `#minecraft:is_forest`，則照原樣採用。不分大小寫，因此 1.12.2 的 `FOREST` 仍然可讀。

**角色**，用於世界範本的 `roles`。可用上面任何一個生態域類型詞：每個詞指名一個生態域，在封鎖移除了帶有該標籤的生態域之後，由它填補這些位置，因此 `"ocean": "mypack:ruby_ocean"` 會在原本海洋被封鎖之處放上紅寶石海洋。

**結構**，用於世界範本的 `structures` 以及 `structures` 分組自己的清單：1.12.2 的名稱 `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges` 和 `endcities`，或遊戲或模組附帶的任何結構集，例如 `pillager_outposts`, `ancient_cities`, `trail_ruins`, `shipwrecks`, `ocean_ruins`, `ruined_portals`, `nether_fossils`, `buried_treasures`, `desert_pyramids`, `jungle_temples`, `igloos`, `swamp_huts`, `woodland_mansions`, `ocean_monuments`, `nether_complexes`, `end_cities`。1.12.2 的名稱會被讀作它原本代表的那些結構集，所以 `temples` 是沙漠神殿、叢林神廟、冰屋和女巫小屋的總和。1.12.2 的 populate 名稱也會讀取，視為它們在此版本世界中所對應的部分：`caves` 是洞穴雕刻器（雜訊洞穴是 `noiseCaves`），`ravines` 是峽谷，`dungeons` 是怪物房，`lavalakes` 是熔岩湖，`netherlava` 是地獄中開放的熔岩泉，`fire` 是地獄的火焰堆，`glowstone` 是它的螢光石，`ice` 是結凍的頂層，`animals` 是生成區塊時放置的動物。`waterlakes` 可接受但不起任何作用，因為此版本沒有水湖。

**生物類型**，用於生態域生成與比率。`creature`、`monster`、`ambient`、`water`。實體變體的生成也接受此版本的其他清單名稱，例如 `water_ambient` 或 `underground_water_creature`。

**礦石類型**，用於 `oreTypes`。`COAL`, `IRON`, `COPPER`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `TUFF`, `CLAY`, `SILVERFISH`，以及代表任何其他礦石的 `CUSTOM`。

**傷害類型**，用於實體變體的 `immuneTo`，不分大小寫，有無底線皆可。1.12.2 的名稱，各自涵蓋它在當時涵蓋的範圍：`inFire`（營火也算）、`onFire`（無人發射的火球也算）、`lava`、`hotFloor`、`inWall`（世界邊界也算）、`cramming`、`drown`、`starve`、`cactus`、`fall`、`flyIntoWall`、`outOfWorld`（`/kill` 也算）、`generic`、`magic`、`indirectMagic`、`wither`、`anvil`、`fallingBlock`、`dragonBreath`、`fireworks`、`lightningBolt`、`thorns`、`arrow`、`fireball`、`thrown`，`mob` 代表生物的攻擊、駱馬的口水、界伏蚌的子彈或凋零怪的頭顱，`player` 代表玩家的攻擊，`explosion` 代表無人引爆的爆炸，例如床，而 `explosion.player` 代表生物或玩家引爆的爆炸，例如苦力怕或點燃的 TNT。此版本自己的傷害也有名稱：`fire` 代表任一種燃燒，另有 `lightning`、`void`、`freeze`、`dryOut` 和 `sweetBerry`。其他任何名稱都會被當作傷害類型 id 讀取，`minecraft:sonic_boom` 或模組自己的。

**音效名稱**，用於實體變體的 `sounds`、鎖箱的 `openSound` 和傳送門的 `sound`：任何已註冊的音效事件，無論是遊戲的、模組的，或資源包透過 `sounds` 新增的。1.12.2 的名稱會被讀作該音效現在的名稱，所以 `entity.endermen.scream` 播放 `entity.enderman.scream`，`block.cloth.step` 播放 `block.wool.step`，`entity.small_slime.squish` 播放 `entity.slime.squish_small`，`record.cat` 播放 `music_disc.cat`。此版本已移除的四種鸚鵡模仿音，即終界使者、北極熊、狼和殭屍豬人的，什麼都不播放。

## 資料夾清單

*值清單*

每個資料夾，連同它的完整路徑，以及指向描述它的那一節的連結，都在[檔案放在哪裡](#檔案放在哪裡)中。

## 指令

*值清單*

### 你自己的指令

*指令*

`/rdpl` 在你自己的機器上執行，不需要任何權限，因為它碰到的一切都是你的。重新載入會重新掃描你擁有的資料夾並更新你自己的資源；它不會連到任何伺服器，所以伺服器上的副本要改用 `/rdplserver reload` 重新載入。在單人遊戲中兩者是同一台機器，所以 `/rdpl reload` 也會重新套用你的[屬性覆寫](#屬性覆寫)並重新編組你的隊伍。

| 指令                                                                                                       | 等級     | 作用                                                                           |
| ---------------------------------------------------------------------------------------------------------- | -------- | ------------------------------------------------------------------------------ |
| `/rdpl list`                                                                                               | 無       | 每個已載入的資源包、其優先順序，以及它包含的內容。點選資源包即可查詢其中的檔案 |
| `/rdpl which <namespace:path>`                                                                             | 無       | 哪個資源包提供指定的檔案，以及它遮蔽了哪些資源包                               |
| `/rdpl reload`                                                                                             | 無       | 重新掃描資料夾並重新載入一切，包括遊戲自己的資源重新載入                       |
| `/rdpl unused`                                                                                             | 無       | 你的資源包中尚無任何東西請求過的檔案，通常是路徑拼錯了                         |
| `/rdpl config unused`                                                                                      | 無       | `rdploader/config` 中不再有任何已安裝資源包定義的選項檔案                      |
| `/rdpl config prune`                                                                                       | 無       | 刪除這些檔案                                                                   |
| `/rdpl pixelmap <namespace:path>`                                                                          | 無       | 一張[像素圖](#以像素圖編寫的材質)解析出來的結果，逐字元顯示                    |
| `/rdpl biome`, `biome list`, `biome list all`                                                              | 無       | 每個能生成的生態域及其 id；`all` 包含那些沒有任何東西能生成的                  |
| `/rdpl biome here`                                                                                         | 無       | 你所站的生態域：其名稱、id 和編號                                              |
| `/rdpl biome find <name>`                                                                                  | 伺服器的 | 已連結。逐字轉交給 `/rdplserver`，由它決定，所以請見下方表格                   |
| `/rdpl locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen`, `game` | 伺服器的 | 已連結。逐字轉交給 `/rdplserver`，由它決定，所以請見下方表格                   |

**哪些伺服器子指令有連結，以及其餘為何沒有。** `locate`、`goto`、`vein`、`gate`、`pregen`、`intro`、`team`、`round`、`dimensions`、`oregen` 和 `game` 只可能指伺服器的，因為只有伺服器知道世界、其玩家與回合，所以 `/rdpl` 把它們轉交過去。在單人遊戲中，這些指令之後的 Tab 自動補全提供的內容與 `/rdplserver` 相同；在伺服器上，`goto` 提供原版結構名稱。其餘的 `reload`、`list`、`which`、`unused`、`config`、`pixelmap` 和 `biome` 保有它們對你的資源包與用戶端的原本含義。連結指令由伺服器自己的權限檢查決定，因此用戶端既無法作弊，也不會被告知捏造的答案。

**日常編輯：** F3+T 重新載入材質、模型和語言檔案，`/reload` 重新載入伺服器的資料。當你*新增*或*刪除*檔案時請用 `/rdpl reload`，因為這會改變資料夾的內容。

### 伺服器指令

*指令*

在專用伺服器上，`/rdplserver` 對伺服器自己的資料夾副本做同樣的事。等級欄是發送者所需的權限等級：`3` 是管理員，`2` 也允許指令方塊，`0` 是任何玩家，`4` 高於管理員，誰都無法使用。

#### 資源包與檔案

*伺服器指令*

| 指令                                    | 等級 | 作用                                                         |
| --------------------------------------- | ---- | ------------------------------------------------------------ |
| `/rdplserver reload`                    | 3    | 重新掃描伺服器的資料夾並重新載入一切，然後重新編組隊伍與目標 |
| `/rdplserver list`                      | 3    | 伺服器載入的每個資源包、其優先順序，以及它包含的內容         |
| `/rdplserver which <namespace:path>`    | 3    | 哪個資源包提供指定的檔案，以及它遮蔽了哪些資源包             |
| `/rdplserver unused`                    | 3    | 伺服器資源包中尚無任何東西請求過的檔案                       |
| `/rdplserver config unused`             | 3    | `rdploader/config` 中不再有任何已安裝資源包定義的選項檔案    |
| `/rdplserver config prune`              | 3    | 刪除這些檔案                                                 |
| `/rdplserver pixelmap <namespace:path>` | 3    | 一張像素圖解析出來的結果                                     |

#### 世界與生成

*伺服器指令*

| 指令                                | 等級 | 作用                                                                                                                                                                      |
| ----------------------------------- | ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver oregen`                | 3    | 被封鎖的礦石生成的累計總數，依模組與類型                                                                                                                                  |
| `/rdplserver generators`            | 3    | 被封鎖的世界生成器的累計總數，依模組與類型                                                                                                                                |
| `/rdplserver biome list [all]`      | 3    | 伺服器上每個能生成的生態域，附編號、id 和名稱；`all` 包含那些沒有任何東西能生成的                                                                                         |
| `/rdplserver biome`                 | 3    | 你所站的生態域，以及資源包對它的處理：其 id、編號和名稱，`blockBiomes` 是否開啟、哪個世界範本正在生效，以及地面、其下的方塊，和 y 40 處的石頭                             |
| `/rdplserver biome here [player]`   | 3    | 你，或所指名的玩家，所站的生態域：其名稱、id 和編號。主控台須指名一位玩家                                                                                                 |
| `/rdplserver biome find <name>`     | 3    | 在 6400 個方塊內，符合該 id 或顯示名稱的生態域生成的最近地點：其座標，以及與執行處的距離。若沒有那麼近的，或名稱不符合任何生態域，則會說明。`/rdpl biome find` 會轉交給它 |
| `/rdplserver dimensions`            | 3    | 每個維度，包括資源包新增的                                                                                                                                                |
| `/rdplserver vein <entry> [radius]` | 3    | 在執行處這麼多個區塊（1 到 64，預設 8）範圍內，`vein` 型世界生成項目的礦脈排定在哪裡，由近到遠，無論那些區塊是否已存在。`/rdpl vein` 會轉交給它                           |

#### 閘門指令

*伺服器指令*

| 指令                                      | 等級 | 作用                                   |
| ----------------------------------------- | ---- | -------------------------------------- |
| `/rdplserver gate`, `gate list`           | 3    | 每道閘門、其維度、其範圍，以及是否開啟 |
| `/rdplserver gate check <player>`         | 3    | 玩家通過了哪些閘門                     |
| `/rdplserver gate grant <player> <gate>`  | 3    | 為玩家開啟一道閘門                     |
| `/rdplserver gate revoke <player> <gate>` | 3    | 再將它關上                             |

#### 傳送通道指令

*伺服器指令*

| 指令                                                          | 等級 | 作用                                                                                     |
| ------------------------------------------------------------- | ---- | ---------------------------------------------------------------------------------------- |
| `/rdplserver portway list <player>`                           | 2    | 玩家清單中的站點                                                                         |
| `/rdplserver portway grant <player> <x> <y> <z> [dimension]`  | 2    | 把該位置的傳送通道加入玩家的清單。不寫維度則取執行者所在維度                             |
| `/rdplserver portway forget <player> <x> <y> <z> [dimension]` | 2    | 從玩家清單中移除一個站點                                                                 |
| `/rdplserver portway forget <player> all`                     | 2    | 清空玩家的清單                                                                           |
| `/rdplserver portway forget <player> network <name>`          | 2    | 移除某一個網路的站點                                                                     |
| `/rdplserver portway send <player> <x> <y> <z> [dimension]`   | 2    | 把玩家傳送到該位置的傳送通道，帶上隨行對象、音效、粒子與抵達觸發，不檢查閘門、消耗與冷卻 |
| `/rdplserver portway cooldown <player> <seconds>`             | 2    | 設定玩家下次行程前需要等待的時間，`0` 表示清除                                           |

#### 預先生成指令

*伺服器指令*

| 指令                          | 等級 | 作用                                                              |
| ----------------------------- | ---- | ----------------------------------------------------------------- |
| `/rdplserver pregen <radius>` | 3    | 生成執行處周圍該區塊數範圍內的每個區塊。參見[預先生成](#預先生成) |
| `/rdplserver pregen status`   | 3    | 一次執行進行到哪裡                                                |
| `/rdplserver pregen stop`     | 3    | 結束它                                                            |

#### 玩家、隊伍與回合

*伺服器指令*

| 指令                                                                                     | 等級 | 作用                                                                                                                                                                                                                 |
| ---------------------------------------------------------------------------------------- | ---- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver intro`                                                                      | 0    | 讓世界介紹在你下次加入時再播放一次。任何玩家都能執行，而且它只會清除他們自己的紀錄；沒有資源包有介紹時會被拒絕                                                                                                       |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0    | 資源包編組的各方，以及加入與離開它們的方式，只有當資源包編組了各方時才提供，參見[隊伍](#隊伍)                                                                                                                        |
| `/rdplserver round start`, `round reset`, `round vote yes`, `round vote no`              | 0    | 開始資源包在大廳中保留的回合、重置進行中的回合或發起投票來重置，並在投票中表決，依資源包的計分規則而定，且只有玩家能開始回合；只有當資源包計分時才向玩家提供，參見[計分](#計分)                                      |
| `/rdplserver card <rule> [players]`                                                      | 2    | 依 id 或檔名，向指定的玩家或你自己顯示一條[卡片規則](#卡片)。`when`、`repeat` 和 `cooldown` 會被略過                                                                                                                 |
| `/rdplserver talk <number>`                                                              | 0    | 在已開啟的對話中依編號選擇一個回覆。原版用戶端中可點擊的回覆會執行它。[對話](#對話)                                                                                                                                  |
| `/rdplserver raid <突襲> [玩家 或 x y z]`                                                | 3    | 在指定玩家處、指定位置，或兩者都不填時在你自己處開始指定的[突襲](#突襲)，無需村莊。若 `reach` 範圍內已有另一場突襲正在進行則會被拒絕                                                                                 |
| `/rdplserver environment [<dimension> <name>]`                                           | 2    | 把維度切換到它的某個[環境](#環境)，或以 `default` 恢復它自己的外觀。不帶參數時，列出每個有環境的維度以及它目前使用的環境                                                                                             |
| `/rdplserver reset`                                                                      | 3    | 讓地圖恢復到一個回合結束時的樣子：所有人被攔住，實體被清掃，分數被清零，執行 `resetRuns`，玩家被送到 `resetSendsTo` 並被釋放，並以起始計數開啟一個回合，如[預先生成](#預先生成)下的重置設定所述。不會從 `/rdpl` 轉交 |

#### 前往各處

*伺服器指令*

| 指令                                | 等級                 | 作用                                                                                                                                                                                                      |
| ----------------------------------- | -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/rdplserver locate <name>`         | 3                    | 資源包以該 `locateAs` 名稱放置的最近的結構                                                                                                                                                                |
| `/rdplserver goto <structure>`      | `gotoLevel`，`3`     | 帶你前往最近一處還沒有人去過的，查找時不會生成沿途的陸地。資源包以 `locateAs` 註冊的地點則是放置的最近一處，無論是否去過。`temple` 指所有零星地物：沙漠神殿與叢林神廟、女巫小屋和冰屋。造陸進行時會被拒絕 |
| `/rdplserver goto <structure> next` | `gotoNextLevel`，`3` | 帶你繼續前往本次工作階段中還沒被帶去過的最近一處，無論之前是否有人造訪過。你八個區塊內的會被略過；對資源包的地點，則是 128 個方塊以外最近的一處                                                           |
| `/rdplserver goto <structure> back` | `gotoBackLevel`，`3` | 帶你前往前一處，沿著本次工作階段送你去過的地方往回走                                                                                                                                                      |
| `/rdplserver goto <biome>`          | `gotoLevel`，`3`     | 帶你前往該生態域中最近的地點，以其 id 指名，如 `minecraft:river`，落在其地表上；`next` 和 `back` 的作用與結構相同。函數或指令方塊可以在等級 2 執行任何 `goto`                                             |

#### 遊戲

*伺服器指令*

| 指令                                                       | 等級     | 作用                                                                                                     |
| ---------------------------------------------------------- | -------- | -------------------------------------------------------------------------------------------------------- |
| `/rdplserver game coin`                                    | 0        | 擲硬幣。正面算 1，反面算 0                                                                               |
| `/rdplserver game die <sides>`                             | 0        | 擲一顆 2 到 1000 面的骰子                                                                                |
| `/rdplserver game die <name>`                              | 0        | 依權重擲一顆[資源包骰子](#骰子與牌組)                                                                    |
| `/rdplserver game dice <roll>`                             | 0        | 擲最多 100 顆骰子並加總，例如 `2d6`、`d20` 或 `3d8-2`。每顆骰子都會顯示                                  |
| `/rdplserver game advantage [roll]`, `disadvantage [roll]` | 0        | 擲兩次，取較高（或較低）的總和。省略時擲骰為 `1d20`                                                      |
| `/rdplserver game pick player`                             | 0        | 隨機挑選一位在線玩家                                                                                     |
| `/rdplserver game pick team [team]`                        | 0        | 隨機挑選一個計分板隊伍，或所指名隊伍中的一位在線成員                                                     |
| `/rdplserver game deck draw <name>`                        | 0        | 從資源包牌組剩下的牌中抽一張牌                                                                           |
| `/rdplserver game deck left <name>`                        | 0        | 牌組還剩幾張牌                                                                                           |
| `/rdplserver game deck shuffle <name>`                     | 2        | 把每張牌放回去                                                                                           |
| `/rdplserver game teamroll [roll]`                         | 0        | 發送者這一方的每個人都擲骰，最高者獲勝，平手則為和局。沒有隊伍時由發送者獨自擲骰                         |
| `/rdplserver game tiebreak [objective]`                    | 2        | 在某個目標上並列最高的各方中抽出一方：所指名的那個，否則是第一個帶有 `tiebreak` 的計分目標，否則是第一個 |
| `/rdplserver game board list`                              | 0        | 世界中的每個棋盤，附其遊戲、位置和狀態                                                                   |
| `/rdplserver game board start <game> <board> [x y z]`      | 2        | 在發送者所站之處或所給的位置架設棋盤，並放置其棋子                                                       |
| `/rdplserver game board end <board>`                       | 2        | 移除棋盤及其棋子                                                                                         |
| `/rdplserver game board show <board>`                      | 0        | 以字母顯示的局面，逐排顯示，附上每一方由誰持有、其時間，以及輪到誰走                                     |
| `/rdplserver game board move <board> <from> <to> [piece]`  | 0        | 以格子名稱移動棋子，例如 `e2 e4`，附上升變後所成的棋子                                                   |
| `/rdplserver game board resign <board>`                    | 0        | 認輸                                                                                                     |
| `/rdplserver game board draw <board>`                      | 0        | 提議和棋，或接受對方的提議                                                                               |
| `/rdplserver game board takeback <board>`                  | 0        | 要求悔棋，或同意對方的要求                                                                               |
| `/rdplserver game board ai <board> <side> <level>`         | 2        | 讓電腦以 1 到 4 的等級執行一方，或用 0 交還                                                              |
| `/rdplserver game last [count]`                            | 0        | 最近的擲骰結果，最新的在前：10 筆，或所給的筆數，最多 50                                                 |
| `/rdplserver game <roll> store <objective>`                | 該擲骰的 | 把擲骰的數字寫入發送者自己在該目標上的分數                                                               |
| `/rdplserver game <roll> audience <who>`                   | 該擲骰的 | 覆寫資源包對該擲骰的預設受眾，如表格下方所述                                                             |
| `/rdplserver game pass`                                    | 0        | 在分數檔案採輪流制之處，提前結束發送者這一方的回合。從指令方塊或主控台執行時，則結束輪到的任何一方的回合 |

任何擲骰都可以以 `store <objective>` 結尾，把結果數字寫入發送者自己在該目標中的分數；也可以以 `audience <who>` 結尾，取代資源包的預設值：`self`、`team`（發送者一方，沒有隊伍時只有發送者）、`all`、`radius <blocks>`（同一世界中該距離內的玩家）或 `silent`，後者只寫入紀錄。`/rdpl game` 會轉交給它。

### 誰可以使用 goto

*指令*

**開放 `goto`。** `/rdplserver` 的每個部分都需要管理員，也就是等級 3，唯獨 `intro`、`team` 和 `round` 例外，任何玩家都可以執行，就像 1.12.2 一樣，以及 `game`，其各部分有[各自的等級](#誰可以使用-game)。三種 `goto` 形式是資源包唯一可決定的部分：每一種都有自己的權限等級，資源包或設定可以調低它，與另外兩種及指令其餘部分各自獨立。想讓玩家能使用 `reset` 的資源包，可把它放在指令方塊或函數上，那會以等級 3 執行。

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

這個值就是發送者所需的權限等級。`3`（管理員）為預設。`2` 也允許指令方塊，因此資源包可以把跳轉放在按鈕或壓力板上，而不必暴露 `/rdplserver` 的其餘部分。`0` 對任何玩家開放。三項設定彼此獨立：例如 `next` 對指令方塊開放以進行村莊巡覽，而 `back` 仍只限管理員。小於 0 的值視為 0，大於 4 的視為 4，且無論設定為何，管理員永遠看得到 `goto` 本身。

`gotoPlaceLevels` 針對單一地點覆寫這三項設定，寫成 `name=level` 項目，如上方範例。名稱就是你在 `goto` 之後會輸入的內容：原版的名稱如 `village` 或 `mansion`，或在 `imprint` 項目上以 `locateAs` 註冊的名稱。比對不分大小寫。等級 `4` 高於管理員，會對所有人關閉該地點，這是在 `goto` 其餘部分開放時隱藏某個地點的方法。傳送通道不是 `goto` 的地點：傳送通道之間的傳送依循傳送通道自身的鍵，`gotoPlaceLevels` 對其不適用。

一個項目會為該地點的三種形式設定同一個等級。未列出的地點會退回上面的三項設定，而未註冊的名稱永遠不會符合。Tab 自動補全遵循同樣的規則，所以在 `goto` 之後，發送者只會看到他們真能被帶去的地點。

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

| 設定         | 所控制的內容                                                                                                                                                                                            |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `gameLevels` | `game` 的一個部分：`coin`、`die`、`dice`、`advantage`、`disadvantage`、`pick`、`deck draw`、`deck shuffle`、`deck left`、`teamroll`、`tiebreak`、`last`、`pass`，或 `board` 加其動作，例如 `board move` |

等級刻度與 `goto` 相同，`4` 會對所有人關閉某個部分。Tab 自動補全只提供發送者可以使用的部分。`gameLevels` 與 `goto` 的設定一起位於 `commands` 群組中。

## 須知

*值清單*

- KubeJS 和 CraftTweaker 在 RDPL 之後執行，所以它們的更動仍然優先。
- 配方、戰利品表、進度和函數在此都是遊戲自己的資料檔案，所以 `/reload` 會讀取編輯，`/rdpl reload` 會讀取新檔案。
- 已經生成的結構會一直保持載入，直到你離開該世界。
- 檔名的大小寫有差。如果你的檔案大小寫與遊戲所請求的不符，RDPL 仍會載入它但會警告你，因為在 Linux 上根本找不到它。
- 在 `rdploader` 中放入一個 `pack.png`，即可為資源包設定圖示。沒有的話會顯示 RDPL 的圖示。
- 資料夾可用 `config/resourcedatapackloader-common.toml` 中的 `rootDirectory` 選項移動或改名。絕對路徑也可以，需要重新啟動。
- 指名某個已完成的原版模型的模型也會繼承原版的材質。像 `cube_all` 和 `cross` 這樣的父模型會從指名它們的模型取得材質，沒有問題。
- 當 `tweaks` 類別中的 `privacy` 開啟時（預設即開啟），遊戲的遙測與聊天舉報是關閉的：不會傳送任何東西，不簽署任何聊天訊息，執行此模組的伺服器不為任何人保留聊天工作階段，而加入未強制安全聊天的伺服器也不顯示警告通知。
- 世界會記住被更動的資源包選項；當更動使世界保有的內容變成未註冊時，世界會在再次開啟之前備份到遊戲的 `backups` 資料夾，若該備份失敗則保持關閉。

## 遇到問題時

*值清單*

**首先檢查 `logs/rdpl.log`。** RDPL 所做的一切都記錄在那裡，而不是主紀錄中。進度、戰利品表、配方、函數、結構以及每一項內容都會連同其來源的資源包一併記錄，任何格式有誤的內容都會連同原因一起記錄。

**材質和其他資源則不同。** 它們被請求得太頻繁，無法逐一記錄，因此改由 `/rdpl unused` 列出你的資源包中尚無任何內容請求過的檔案。請在遊戲載入完畢後執行。路徑正確的檔案一定會被請求，所以列出的任何檔案通常都是拼寫錯誤，但要記住有些檔案只在需要時才載入，例如你所玩語言之外的其他語言。

**內部沒有 `assets` 或 `data` 目錄的 zip 會被略過，** `rdploader` 中的任何資料夾也一樣，日誌會說明。頂層是一個包住它們的資料夾的 zip 同樣會被略過。

**`/rdpl which minecraft:textures/block/stone.png`** 會確切告訴你是哪個資源包在提供某個檔案，以及它遮蔽了什麼。

**為 1.12.2 編寫的資源包是經過向前移植來讀取的。** 參見[為 1.12.2 編寫的資源包](#為-1122-編寫的資源包)；日誌會列出它移動、略過或無法移植的每個檔案。

## 額外功能：原版調整

*值清單*

對原版行為的小改動，每一項都在 `tweaks` 設定類別中開關。

| 選項                  | 預設 | 作用                                                                     |
| --------------------- | ---- | ------------------------------------------------------------------------ |
| `promptLeafDecay`     | 開   | 失去樹木的樹葉會在一秒內枯萎，而不是等待隨機刻                           |
| `lenientPaths`        | 開   | 土徑可以做在方塊下方，上方放置方塊時也會保留                             |
| `unbreakableSpawners` | 關   | 生怪磚無法被挖掉或炸毀。創造模式仍能移除它們。需要重新啟動               |
| `experimentalWarning` | 關   | 建立或開啟世界時顯示遊戲的實驗性設定警告。關閉則視同你已點選繼續         |
| `privacy`             | 開   | 關閉遊戲的遙測與聊天舉報；參見[須知](#須知)                              |
| `darkSplash`          | 開   | 深色載入畫面，附資源包載入器的標誌；遊戲的單色標誌選項會在下次啟動時開啟 |

另有四項位於 `content` 類別而不是 `tweaks`：

| 選項              | 預設 | 作用                                                         |
| ----------------- | ---- | ------------------------------------------------------------ |
| `cactusMaxHeight` | `3`  | 原版仙人掌最高能長多高                                       |
| `caneMaxHeight`   | `3`  | 原版甘蔗最高能長多高                                         |
| `shovelPaths`     | 開   | 鏟子可將標記為 `behavesAs` path 的方塊變成土徑，潛行時可還原 |
| `hoeTilling`      | 開   | 鋤頭可耕標記為 `behavesAs` till 的方塊                       |

**這些都不會影響資源包。** 這些選項只改變 Minecraft 自己的仙人掌、甘蔗、樹葉和土徑。你的資源包以 `"type": "cane"` 定義的方塊自帶 `growth` 區段，會長到你所指定的任何高度，無論還安裝了什麼。

### 不可破壞的生怪磚

*額外功能：原版調整*

`unbreakableSpawners` 讓生怪磚方塊擁有基岩的數值，即不可破壞的硬度和沒有任何東西能撐過的爆炸抗性。無論鎬子多好，玩家都挖不掉它，苦力怕、TNT 或會 `explodes` 的資源包實體也都炸不掉。創造模式仍能移除它們，就像仍能移除基岩一樣，所以資源包作者永遠不會被鎖在自己的建築之外。它需要重新啟動，因為方塊的數值是在註冊時設定的。

**針對的是方塊，不是生怪磚。** 沒有逐個生怪磚的開關。該選項改變的是 `minecraft:spawner` 本身，因此一次影響世界中的每個生怪磚：放置它的原版結構、任何模組放置的，以及你自己的資源包放置的。位於你的 `.nbt` 範本內、由 `imprint` 項目放置的生怪磚，是帶有自己方塊實體的普通生怪磚方塊，所以選項一開就涵蓋了它。

## 未沿用的鍵

*值清單*

1.12.2 資源包可以寫、但此版本不讀取的內容，以及原因。寫了其中一項的資源包仍會載入；該鍵不起任何作用。標示*尚未移植*的列，是在該部分移植之後才加入 1.12.2、仍待移植過來的。其餘每一列在這個引擎上都無法、或無須移植。標示*自 26.3 起*的列，是 26.3 資源包可以寫、但此版本無法容納的內容；日誌會在每一項被捨棄時指出。此表會隨各條線的進展保持最新。

`<namespace>/blocks/*.json`

```json
{
  "type": "basic",
  "meta": 0,
  "oreDict": "oreRuby",
  "modelMeta": 0,
  "variants": { "ruby_ore": { "hardness": 3.0 } }
}
```

`<namespace>/biomes/*.json`

```json
{
  "id": 5,
  "baseHeight": 0.2,
  "heightVariation": 0.1
}
```

`<namespace>/dimensions/*.json`

```json
{
  "id": 7,
  "suffix": "_ruby",
  "keepLoaded": true
}
```

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "rubicWorld": true,
    "rubicHeightLimit": 512,
    "regionCacheLimit": 64,
    "deepRavines": true,
    "oreVeins": true,
    "readCofhWorldFiles": true,
    "loadingScreenPercent": true,
    "disableOptimizations": true,
    "fixTinkersModelErrors": true,
    "modernChestPlacement": false
  }
}
```

`<namespace>/entities/*.json`

```json
{
  "entity": "minecraft:cow",
  "galacticraft": true
}
```

`<namespace>/overrides/<target>/<name>.json`

```json
{
  "harvestTool": "shears"
}
```

`<namespace>/villagers/*.json`

```json
{
  "careers": ["farmer"]
}
```

`<namespace>/trades/*.json`

```json
{
  "trades": [
    { "profession": "minecraft:farmer", "career": "farmer", "buy": { "item": "minecraft:wheat" }, "sell": { "item": "minecraft:emerald" } }
  ]
}
```

`<namespace>/gamerules/*.json`

```json
{
  "minecraft:overworld": { "gameLoopFunction": "mypack:tick" }
}
```

`<namespace>/biomes/*.json`

```json
{
  "placement": { "villageSpawn": true }
}
```

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "oreDictionary": true,
    "disableOverrides": false,
    "tolerateMissingInAdvancements": true,
    "terrainWorldTypes": ["default"],
    "terrainWorldTypesAreBlacklist": false,
    "pregenKeepLoaded": true,
    "pregenPauseAbove": 90,
    "pregenMillisPerRound": 50,
    "pregenRelightSays": "Relighting",
    "hurryWritesAbove": 100,
    "rubicWorldDimensions": ["minecraft:overworld"],
    "rubicWorldDimensionsAreBlacklist": false,
    "verticalCubeLoadDistance": 8,
    "cubeGCInterval": 20,
    "cubeGenMillisPerRound": 20,
    "cubesSentPerTick": 25
  }
}
```

| 鍵                                                                                                                                                                  | 位置                                                                                          | 原因                                                                                                                                                                                                                                          |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `meta`                                                                                                                                                              | 方塊、物品、世界生成、方塊掉落物                                                              | 自從扁平化以來 id 不再帶有中繼資料。移植時會讓每個 `name:meta` 經過遊戲的資料修復器，並丟棄該鍵                                                                                                                                               |
| `oreDict`                                                                                                                                                           | 方塊、物品、熔爐、燃料、實體儲存篩選                                                          | 礦物辭典已不存在。移植時會將它轉成慣例標籤上的 `tags`，而燃料或儲存篩選項目的則轉成 `tag`                                                                                                                                                     |
| `galacticraft`                                                                                                                                                      | 實體變體                                                                                      | 此版本沒有 Galacticraft，所以變體沒有火箭等級、燃料箱、貨物或酬載可設定                                                                                                                                                                       |
| `oreDictionary`                                                                                                                                                     | 設定                                                                                          | 礦物辭典已不存在，所以沒有礦物辭典檔案可關閉。標籤取代了它的工作，移植時會依資源包的 `oreDict` 寫出標籤                                                                                                                                       |
| `modelMeta`                                                                                                                                                         | 方塊                                                                                          | 模型是依變體生成的，所以沒有可據以對應的中繼資料                                                                                                                                                                                              |
| `disableOverrides`, `tolerateMissingInAdvancements`                                                                                                                 | 設定                                                                                          | 資料包只要以同名附上一個，就能取代原版配方或進度                                                                                                                                                                                              |
| `#CONSTANT` 物品名稱                                                                                                                                                | 配方                                                                                          | 配方常數存在於 1.12.2 模組的 `_constants.json` 中，沒有任何資源包帶有它，此版本也沒有任何模組有。請直接寫出該常數所代表的物品或標籤                                                                                                           |
| `harvestTool` 不是 `pickaxe`, `axe`, `shovel`, `hoe` 和 `sword` 的值                                                                                                | 屬性覆寫                                                                                      | 在此，工具依方塊標籤挖掘，且只有這五種有標籤。1.12.2 模組自創的工具類別沒有標籤可寫，所以該方塊的工具保持不動，日誌會說明                                                                                                                     |
| `careers`                                                                                                                                                           | 村民                                                                                          | 自 1.14 起就沒有職涯了，所以每個職涯都變成各自的村民檔案                                                                                                                                                                                      |
| `career`                                                                                                                                                            | 交易、實體                                                                                    | 沒有職涯；請直接寫職業本身。指名原版 1.12.2 職業及其職涯的交易，會歸到該職涯變成的職業                                                                                                                                                        |
| `gameLoopFunction`                                                                                                                                                  | 遊戲規則                                                                                      | 該遊戲規則已不存在；`#minecraft:tick` 函數標籤會每刻執行一個函數，移植時會寫出一個                                                                                                                                                            |
| `id`                                                                                                                                                                | 生態域、維度                                                                                  | 生態域與維度以其資源位置識別，絕不用編號                                                                                                                                                                                                      |
| `suffix`, `keepLoaded`                                                                                                                                              | 維度                                                                                          | 存檔資料夾依維度的名稱而定。只有主世界有出生點區塊，所以必須保持載入的維度要使用 `forceload`                                                                                                                                                  |
| `baseHeight`, `heightVariation`                                                                                                                                     | 生態域                                                                                        | 地形高度屬於雜訊設定，不屬於生態域                                                                                                                                                                                                            |
| `placement.villageSpawn`                                                                                                                                            | 生態域                                                                                        | 村民隨村莊結構本身而來                                                                                                                                                                                                                        |
| `rubicWorld`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist`, `verticalCubeLoadDistance`, `cubeGCInterval`, `cubeGenMillisPerRound`, `cubesSentPerTick` | Rubic 世界                                                                                    | Rubic 世界是 1.12.2 突破 256 格高世界的辦法。在此，維度的 `minHeight` 和 `maxHeight` 決定它的大小，由區塊系統串流載入                                                                                                                         |
| `rubicHeightLimit`                                                                                                                                                  | Rubic 世界                                                                                    | Rubic 世界的高度上限隨它一起消失了；改由維度的 `minHeight` 和 `maxHeight` 決定大小，沒有另外可調高的上限                                                                                                                                      |
| `regionCacheLimit`                                                                                                                                                  | Rubic 世界                                                                                    | 它對 Rubic 世界自己開啟的區域檔案的快取隨 Rubic 世界一起消失了；此處的區塊系統自行管理自己的檔案                                                                                                                                              |
| `deepRavines`, `oreVeins`                                                                                                                                           | 深層世界                                                                                      | 引擎自己的地形本來就在 y 0 以下運作，帶有自己的峽谷和大型礦脈                                                                                                                                                                                 |
| 客製化 `generatorOptions` 中編號高於 39 的 `fixedBiome`                                                                                                             | 設定                                                                                          | 它指名了此版本無法對應的生態域                                                                                                                                                                                                                |
| `terrainWorldTypes`, `terrainWorldTypesAreBlacklist`                                                                                                                | 設定                                                                                          | 世界類型在此是世界預設組合，世界範本自己指名其類型                                                                                                                                                                                            |
| `lake` 和 `dungeon` 在超平坦文字中                                                                                                                                  | 設定、維度                                                                                    | 遊戲自 1.18 起就沒有水湖了，只有主世界的 waterLakeChance 能把它們帶回來。平坦世界上的地城隨 `decoration` 而來，不會單獨出現                                                                                                                   |
| `inherit` 作為 `flat` 或 `void` 維度的 `biomes` 來源                                                                                                                | 維度                                                                                          | 平坦生成器只含單一生態域，所以這種維度採用其超平坦文字所指名的那個，否則是平原                                                                                                                                                                |
| 另一個模組在 `flatBedrockDimensions` 或 `voidWorldDimensions` 中以程式碼定義的維度                                                                                  | 設定                                                                                          | 基岩與虛空會寫進生成的世界預設組合、資源包自己的維度檔案，以及對於另一個模組在 `data/<namespace>/dimension` 下的維度檔案中定義的維度，寫進對該檔案生成的覆寫。另一個模組以程式碼建立的維度無法觸及：RDPL 會記錄指名它的警告，並保持其地形不動 |
| 模組的世界類型，或 `debug_all_block_states`，用作 `worldType`                                                                                                       | 設定                                                                                          | 世界類型現在是世界預設組合，而塑形後的世界是建立在遊戲自己的預設、巨大生態域、放大化或平坦雜訊之上。除錯世界沒有可塑形的地形                                                                                                                  |
| `pregenKeepLoaded`, `pregenPauseAbove`, `pregenMillisPerRound`, `pregenRelightSays`, `hurryWritesAbove`                                                             | 預先生成                                                                                      | 它們調校的是 1.12.2 的區塊寫入器與重新打光流程。此處的區塊系統在生成陸地時就會打光，並依自己的排程寫入                                                                                                                                        |
| `readCofhWorldFiles`                                                                                                                                                | 設定                                                                                          | 此引擎上沒有 CoFH World 及其自己的檔案格式。如 1.12.2 已建議的，把那些檔案翻譯成資源包，仍是跨過來的辦法                                                                                                                                      |
| `villagePathLamp*` 中的 `name:meta`                                                                                                                                 | 村莊                                                                                          | id 不帶中繼資料，所以燈方塊以方括號中的狀態寫成，而大括號中的方塊資料仍可讀取                                                                                                                                                                 |
| `harvestTool` 指名 `shears` 或模組的工具類別                                                                                                                        | 方塊                                                                                          | 此處的工具讀取方塊標籤，而非類別名稱，所以沒有任何東西會回應類別名稱。請在變體的 `tags` 下寫出該模組工具自己的方塊標籤                                                                                                                        |
| `creativeTab` 中另一個模組的分頁標籤                                                                                                                                | 方塊、物品、流體                                                                              | 分頁現在以其 id 識別，所以單純的標籤會被讀作資源包自己的分頁。請以 id 指名該模組的分頁，例如 `modid:main`                                                                                                                                     |
| `/rdpl reload <group>`                                                                                                                                              | 指令                                                                                          | 遊戲一次就重新載入所有資源，所以材質、模型、語言、音效和著色器無法單獨重新載入。`/rdpl reload` 或 F3+T 會全部重新載入                                                                                                                         |
| `modernChestPlacement`                                                                                                                                              | 原版調整                                                                                      | 自 1.13 起遊戲就這樣配對箱子：箱子只有在兩者朝向相同時才會與旁邊的單一箱子合併，潛行則讓它保持單獨                                                                                                                                            |
| `loadingScreenPercent`                                                                                                                                              | 設定                                                                                          | 遊戲自己的世界載入畫面已經顯示出生點區域準備好了多少                                                                                                                                                                                          |
| `disableOptimizations`                                                                                                                                              | 設定                                                                                          | 它會停用 1.12.2 的預先生成與生成最佳化，這些最佳化是為那個引擎寫的，此處沒有對應物                                                                                                                                                            |
| `fixTinkersModelErrors`                                                                                                                                             | 設定                                                                                          | 它壓下 1.12.2 版 Tinkers' Construct 和 Construct's Armory 為每件工具、零件和盔甲所記錄的模型錯誤。這個修正伸入那些版本內部，因此在此沒有可作用的對象                                                                                          |
| `achievement.` 準則                                                                                                                                                 | 計分、函數                                                                                    | 成就變成了進度，進度不保留可計數的分數。`stat.` 準則會被讀作它變成的統計資料                                                                                                                                                                  |
| `toggledownfall`, `stats`                                                                                                                                           | 函數                                                                                          | 這些指令已不存在。`weather` 指名要設定的天氣，`execute store` 保存指令的結果                                                                                                                                                                  |
| `gamerule gameLoopFunction`，以及資源包自創的遊戲規則                                                                                                               | 函數                                                                                          | 遊戲決定存在哪些遊戲規則。移植時會把遊戲規則檔案的 `gameLoopFunction` 轉成 `#minecraft:tick` 標籤，但設定它的指令行會保留下來，留待你移除                                                                                                     |
| 帶有資料值 `-1` 或 `*`、後來變成多個方塊或物品的名稱                                                                                                                | 函數：`clear`, `testforblock`, `execute ... detect`, `fill ... replace`, `clone ... filtered` | 現在一項測試只指名一個方塊或物品。請指名所指的那一個，或使用標籤如 `#minecraft:wool`                                                                                                                                                          |
| 寫成 `name=value` 配對、但不是該方塊完整 1.12.2 狀態的方塊狀態，以及另一個模組的方塊或物品上的資料值                                                                | 函數                                                                                          | 資料修復器只認得完整的 1.12.2 狀態，而其他模組的中繼資料沒有可對應的扁平化名稱                                                                                                                                                                |
| `footstep` 和 `take` 粒子、`locate Temple`、對多於一個目標的 `spreadplayers`                                                                                        | 函數                                                                                          | 這些粒子已不存在，神殿現在是四種結構，而 `spreadplayers` 只接受一個目標引數                                                                                                                                                                   |
| `debug_functions`                                                                                                                                                   | 雜訊設定，*自 26.3 起*                                                                        | 26.2 及更早版本沒有除錯用密度函數，因此該清單會被捨棄                                                                                                                                                                                         |
| `aquifers` 中的 `exclusion` 和 `surface_level`                                                                                                                      | 雜訊設定，*自 26.3 起*                                                                        | 26.2 及更早版本把兩者都內建了，因此資源包自己的會被捨棄                                                                                                                                                                                       |
| 指明了 `noise_router` 氣候軸以外任何內容的 `spawn_target` 項目                                                                                                      | 雜訊設定，*自 26.3 起*                                                                        | 較早的生成目標只涵蓋五個氣候軸，因此該項目會被捨棄                                                                                                                                                                                            |
| 透過 `material_rule` 放置的礦脈                                                                                                                                     | 雜訊設定，*自 26.3 起*                                                                        | 26.2 無法透過材料規則放置礦脈，因此它們會被捨棄                                                                                                                                                                                               |
| 洞穴雕刻器自己的 `count`、`thickness`、`weird_thickness_bias` 或 `start_vertical_radius_multiplier`                                                                 | 雕刻器，*自 26.3 起*                                                                          | 26.2 無法設定它們，因此使用原版主世界或地獄的洞穴形狀                                                                                                                                                                                         |
| 作為修飾器的 `creature_world_gen_spawn_probability`，以及附加到其維度值上的屬性                                                                                     | 生態域，*自 26.3 起*                                                                          | 26.2 只接受單純的數值：保留預設的生成機率，並由生態域自己的值取代維度的值                                                                                                                                                                     |
| 不相同的 x 和 z 偏移，以及設為 false 的 `normalize`                                                                                                                 | 已放置地物、雜訊，*自 26.3 起*                                                                | 26.2 以同一個量擴散兩個軸，因此兩者都使用 x 偏移，並且總是對雜訊做正規化                                                                                                                                                                      |
| 26.2 沒有的地物類型，或由 26.2 沒有的方塊所建構的 26.3 地物                                                                                                         | 地物、生態域，*自 26.3 起*                                                                    | 這樣的地物不會放置任何東西，而生態域會略去沒有 26.2 對應物的 26.3 已放置地物                                                                                                                                                                  |
| `straw_bed_rule`，以及床規則中的 `destroy_on_leave`                                                                                                                 | 環境屬性，*自 26.3 起*                                                                        | 26.2 兩種規則都沒有，因此兩者都會被捨棄                                                                                                                                                                                                       |
| 陶罐裝飾物中帶數量或組件的物品                                                                                                                                      | 資料檔案、函數，*自 26.3 起*                                                                  | 26.2 讀取的是單純的物品，因此數量與組件會被捨棄                                                                                                                                                                                               |
| `compute`、`posteffect`、`item fill`、`item override` 和 `execute if slots`，以及 `swing` 的動畫                                                                    | 函數，*自 26.3 起*                                                                            | 26.2 沒有這些指令，因此該行會被捨棄；`swing` 行會保留並以一般方式揮動                                                                                                                                                                         |
| 不是 `up` 的 `shade_direction_override`，以及 `trim_overrides`                                                                                                      | 模型、裝備素材，*自 26.3 起*                                                                  | 26.2 對這樣的元素會照常著色，並從盔甲紋飾材料的 `override_armor_assets` 讀取各裝備的紋飾調色盤                                                                                                                                                |
| 釀造出的物品上的組件，以及用於釀造的物品標籤或清單                                                                                                                  | 釀造配方，*自 26.3 起*                                                                        | 26.2 的釀造配方兩者都無法指明：組件會被捨棄，而從標籤或清單釀造的配方則整條捨棄                                                                                                                                                               |
| 26.2 沒有對應形式的數值提供器，以及不為 0 的備用值                                                                                                                  | 戰利品表、謂詞、物品修飾器，*自 26.3 起*                                                      | 該提供器變為 0，缺失的分數或儲存的數字則退回 0                                                                                                                                                                                                |
| 除 `blocks` 和 `state` 之外的方塊條件測試、26.2 只讀取一個的 id 集合、謂詞或物品修飾器的標籤、指向具名結構的探險家地圖                                              | 戰利品表、謂詞、物品修飾器，*自 26.3 起*                                                      | 該測試會被捨棄，只保留第一個 id，標籤會被讀作它所指的那一個 id，而地圖則指向寶藏結構                                                                                                                                                          |

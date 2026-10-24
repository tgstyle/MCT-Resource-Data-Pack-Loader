Resource Data Pack Loader
=========================

이 폴더에 넣은 파일은 모드나 Minecraft 자체가 제공하는 파일을 대체합니다.
싱글플레이와 서버를 포함한 모든 월드에 적용되며, 따로 켤 필요는 없습니다.


재정의 그 이상
--------------

이곳의 팩은 JSON 파일로 새 블록, 아이템, 생물 군계, 차원 전체를 정의할 수
있습니다. 무엇이 어디에 생성될지 정하고, 열쇠나 처치해야 하는 몹으로 차원을
잠그며, 월드의 땅을 미리 만들어 두어 청크를 기다릴 일이 없게 할 수도 있습니다.
모드와 함께 제공되는 HOWTO.md에 모든 내용이 있습니다.


파일 추가 방법
--------------

모드의 jar를 열어 변경할 파일을 찾고, 'assets' 또는 'data'부터 시작하는
경로를 복사하세요.

철광석 텍스처를 교체하려면, Minecraft jar 안의 파일은 다음과 같습니다.

    assets/minecraft/textures/block/iron_ore.png

따라서 교체할 파일은 다음 위치에 둡니다.

    rdploader/assets/minecraft/textures/block/iron_ore.png

전리품 테이블은 assets가 아닌 data 아래에 있으며, 방식은 같습니다.

    data/minecraft/loot_table/blocks/iron_ore.json
    rdploader/data/minecraft/loot_table/blocks/iron_ore.json

규칙은 이것이 전부입니다. 'assets' 또는 'data' 뒤의 경로는 항상 jar 안의
경로와 같으므로, 이름을 바꾸거나 옮길 필요가 없습니다.


정리해 두기
-----------

파일을 zip으로 묶어 이름 있는 팩으로 관리할 수도 있습니다.

    rdploader/MyTextures.zip        (zip 최상위에 'assets' 또는 'data')

압축할 때는 폴더가 아니라 폴더 안의 내용을 선택해서 압축하세요. 최상위가
폴더 하나이고 그 안에 'assets' 또는 'data'가 들어 있는 zip은 건너뛰며,
로그에 그 사실이 기록됩니다.

rdploader 안의 폴더는 팩으로 취급하지 않고 건너뛰며, 로그에 그 사실이
기록됩니다. 낱개 파일은 assets나 data 아래에 두고, 팩은 zip으로 묶은 뒤
이곳에 넣으세요.

같은 파일이 두 곳에 있으면 이름 있는 팩이 낱개 파일보다 우선하며,
/rdpl which 명령어로 어느 쪽이 적용되었는지 확인할 수 있습니다.


팩 우선순위
-----------

이름 있는 팩 두 개에 같은 파일이 있으면, zip 이름 앞에 RDPL과 숫자를 붙여
어느 쪽이 적용될지 정합니다. RDPL0이 가장 먼저 로드되고 숫자가 클수록
나중에 로드되며, 마지막에 로드된 팩이 우선합니다.

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

대문자와 소문자 모두 사용할 수 있고, 숫자 뒤의 공백, 대시, 밑줄은 생략해도
됩니다. 접두사는 로그와 /rdpl list에 표시되는 팩 이름에서 제거되므로,
RDPL1 SeasonalTextures는 SeasonalTextures로 표시됩니다.


모드 API
--------

모드는 자체 jar 안에 RDPL 콘텐츠를 담을 수 있습니다. rdploader라는 폴더에
팩과 똑같은 구조로 넣으면 됩니다.

    thatmod.jar
      META-INF/neoforge.mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

이 파일들은 재정의가 아니라 기본값입니다. 모드의 팩은 이 폴더의 모든 팩보다
먼저 로드되므로 여기에 넣은 파일이 항상 우선하며, 모드는 자신의
neoforge.mods.toml에 선언한 네임스페이스 아래의 파일만 제공할 수 있습니다.
그 밖의 파일은 경고와 함께 무시되므로, 어떤 모드도 다른 모드나 사용자의
콘텐츠를 몰래 다시 정의할 수 없습니다.

RDPL 콘텐츠를 담은 모드는 처음 감지될 때 config/mods.json에 등록됩니다.

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

enabled를 false로 설정하면 해당 모드의 콘텐츠가 꺼집니다. priority를 -1로
두면 모든 것의 아래에 유지되고, 숫자를 지정하면 번호가 붙은 팩들과 함께
위의 순서에서 해당 위치를 차지합니다. 로그에는 팩이 낮은 순서부터 나열되며
모드 자체의 팩에는 표시가 붙으므로, 보이지 않게 로드되는 것은 없습니다.


리소스 팩
---------

기본적으로 이곳의 파일은 플레이어가 옵션 화면에서 고르는 리소스 팩보다
위에 놓이므로, 리소스 팩으로 재정의할 수 없습니다. 모드팩 로고 같은 것에는
알맞지만, 사람들이 다시 입힐 수 있게 하고 싶은 텍스처에는 맞지 않습니다.

팩마다 정하려면 RDPL 접두사 뒤에 O 또는 N을 붙이세요.

    rdploader/RDPLO Branding          항상 우선, 리소스 팩이 건드릴 수 없음
    rdploader/RDPLN BaseTextures      리소스 팩이 재정의할 수 있음
    rdploader/RDPL1O Seasonal         우선순위와 항상 우선, 둘 다 적용

문자가 없는 팩은 설정의 overrideResourcePacks 옵션을 따르며, /rdpl list에서
재정의하는 팩에 표시가 붙습니다.

데이터 팩에도 같은 규칙이 적용됩니다. N이 붙은 팩은 월드가 자체
datapacks 폴더에 가진 데이터 팩보다 아래에, O가 붙은 팩은 그 위에 놓입니다.

접두사가 없는 팩은 번호가 붙은 모든 팩보다 먼저 알파벳 순서로 로드되므로,
번호가 붙은 팩이 번호가 없는 팩보다 항상 우선합니다.

팩을 삭제하지 않고 끄려면 이름 끝에 .disabled를 붙이세요.

    rdploader/RDPL1 SeasonalTextures.zip.disabled

해당 팩은 건너뛰며 로그에 그 사실이 기록됩니다. 접미사를 제거하면 다시
켜집니다.


변경할 수 있는 항목
-------------------

텍스처, 모델, 블록 상태, 언어 파일, 소리, 글꼴, 스플래시 문구, 그리고 안내서나
매뉴얼처럼 모드가 assets 폴더에 두는 모든 것.

발전 과제, 전리품 테이블, 레시피, 태그, 함수, 구조물 템플릿, 그리고 모드가
data 폴더에 두는 모든 것. 이들은 서버 측이므로 전용 서버에서도 작동하며,
변경 사항은 /reload로 적용됩니다.

무언가를 없앨 수도 있습니다. recipe_removals의 파일은 이름, 네임스페이스,
결과물을 기준으로 레시피를 삭제하고, disabled의 파일은 등록을 해제하지 않고
블록과 아이템을 사용할 수 없게 만듭니다. 그래서 월드는 기존 id를 유지하고,
파일을 지우면 모든 것이 돌아옵니다. 전리품 주입은 기존 전리품 테이블을
대체하지 않고 풀을 추가하며, block_drops는 직접 만들지 않은 블록의 드롭을
추가하거나 대체합니다. 화로 레시피, 연료 연소 시간, 크리에이티브 탭,
소리 이벤트도 각각 별도의 파일입니다.

모루에 새 작업을 가르칠 수도 있습니다. anvils의 파일은 아이템, 오른쪽 슬롯에
함께 놓는 아이템, 지정한 레벨에서 모루가 제공하는 마법 부여나 결과물을
정합니다. 결과물을 꺼내면 발전 과제를 달성하게 할 수 있고, 그 발전 과제를
달성하기 전까지 아이템을 사용할 수 없게 둘 수도 있습니다.

tooltips 안의 파일은 아이템과 블록에 회색 툴팁 줄을 추가하고, 마법 부여
이름 아래에 설명을 붙입니다.

hardness 파일은 블록 그룹의 채굴 시간과 폭발 저항을 정하고, exposures 파일은
방사선 같은 위험 요소를 정의합니다. 위험 요소는 지정한 블록 근처에 있거나,
지정한 아이템을 지녔거나, 지정한 차원에 있는 플레이어에게 영향을 줍니다.
각 단계마다 효과와 피해를 적용하며, 주변의 몹과 플레이어에게서 옮을 수도
있고 비와 함께 내릴 수도 있습니다.


새 콘텐츠 추가
--------------

팩은 JSON으로 기술한 자체 블록, 아이템, 유체를 추가할 수도 있습니다. 이를
위해 모드를 작성하거나 빌드할 필요는 없습니다.

정의는 data 아래에 두며, 종류마다 폴더가 하나씩 있습니다. "variants" 안의
각 키가 이름이므로, 다음 위치의 파일이

    rdploader/data/mypack/blocks/ores.json

ruby_ore라는 변형을 담고 있으면 mypack:ruby_ore가 등록됩니다. 파일 자체의
이름은 항목을 묶는 용도일 뿐입니다. 실제 모드가 이미 같은 이름을 등록했다면
모드가 우선하며 해당 변형은 건너뜁니다.

가장 단순한 블록은 몇 줄이면 됩니다.

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

모델, 블록 상태, 텍스처, 언어 항목은 이 폴더의 다른 파일과 마찬가지로
assets 아래에 직접 제공해야 합니다.

다음은 각각 data/<yourpack> 아래의 폴더입니다.

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
    tooltips         harvests         carries          dialogs
    veins            diets            enchantments     dice
    games            orders           ranks            recipe_locks
    spawn_locks

블록은 "type" 필드로 정하는 다음 형태로 나뉩니다.

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

아이템은 다음 형태로 나뉩니다.

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

물약 유형의 이름은 언어 키 item.minecraft.potion.effect.<baseName>으로 정하며,
다른 형태에는 potion 대신 splash_potion, lingering_potion, tipped_arrow를
씁니다. potion_bottle 아이템은 이들을 담는 나만의 컨테이너입니다. 다른
아이템처럼 creativeTab을 받으며, potionTypes에 지정한 물약 유형을 담습니다.

villagers/<name>.json 파일은 직업을 정의합니다. trades/*.json 파일은 직업이
직접 만든 것이든 Minecraft의 것이든 모든 직업에 거래를 추가하며, 직업과
거래가 나타나는 레벨을 지정합니다.

entities/<name>.json 파일은 이미 있는 엔티티를 바탕으로 새 엔티티를
만듭니다. 바탕이 되는 엔티티와 달라지는 점을 지정하는데, 이름, 외형, 체력과
공격력, 이동 방식, 전투 방식, 드롭이 해당합니다. 고유한 스폰 알과 전리품
테이블을 가진 별개의 엔티티이며, 바탕이 된 엔티티는 그대로 남습니다. 자체
저장소도 가질 수 있습니다. 아이템 슬롯, 유체 탱크, 에너지 버퍼는 파이프와
케이블이 연결되며, 플레이어는 웅크린 채 우클릭하여 엽니다.

villages/<name>.json 파일은 도시나 마을이 직접 만든 .nbt 템플릿으로 지을 수
있는 구획을 추가하고, raids/<name>.json 파일은 플레이어가 흉조를 마을에
가져왔을 때 마을로 공격 웨이브를 보냅니다.

cards/<name>.json 파일은 플레이어가 생물 군계에 들어가는 것처럼 무언가
일어났을 때 화면에 카드를 표시하며, 같은 폴더에 모드 자체 메시지의 이름으로
만든 파일은 해당 메시지의 문구를 바꿉니다.

worldintro/<name>.json 파일은 누군가 월드에 들어와 조작권을 얻기 전에 일련의
페이지를 재생합니다. 문구는 assets/<yourpack>/texts 아래의 일반 .txt
파일입니다. 플레이어마다 한 번만 재생하거나 접속할 때마다 재생할 수 있습니다.

teams/<name>.json 파일은 게임의 점수판에 한 진영을 세우고 누가 참가하는지
정하며, scoring/<name>.json 파일은 그 진영에 점수를 주고 경기가 어떻게
끝나는지 정하는 목표입니다.


월드 전체
---------

팩은 개별 요소에만 국한되지 않습니다. dimensions/<name>.json은 자체 지형,
생물 군계, 하늘을 가진 차원을 등록합니다. gates/<name>.json은 아이템을
들고 있거나 소비하는 것처럼 차원에 도달하는 조건을 설정합니다. portal 유형의
블록은 들어간 대상을 다른 차원으로 보내며, portalframes는 플레이어가 직접
프레임을 짓고 불을 붙일 수 있게 합니다.

worldtemplates/<name>.json은 월드 설정을 하나의 파일로 모아, 팩이 수십 개의
설정 편집을 요구하는 대신 월드 형태 전체를 한 번에 제공할 수 있게 합니다.
해수면이나 바다가 용암인지 같은 오버월드 자체의 모양도 정할 수 있습니다.

차원은 머리 위의 모습과 동작도 정할 수 있습니다. 안개, 빛의 색조, 해와 달의
어두워짐, 구름 계층, 아지랑이가 사용자의 화면에 그려지며, 날씨는 비, 눈,
폭풍이 오는지, 소나기가 얼마나 지속되는지, 비의 색, 입자, 소리, 각도를
정합니다.

worldgen은 광석만이 아닙니다. 항목 하나가 작은 블록 덩어리부터 직접 만든
.nbt 템플릿까지 하나의 모양을 배치하고, 얼마나 자주, 얼마나 높이, 어느 생물
군계에 나타날지 정합니다.

biomes/<name>.json 파일은 생물 군계를 정의합니다. 기후와 색상, 구성 블록,
장식, 스폰되는 대상, 생성되는 위치가 포함됩니다.


한계
----

이것은 어떤 것이 무엇인지를 기술할 뿐, 시간에 따라 무엇을 하는지는
다루지 않습니다. 블록 엔티티, 화면, 매 틱 실행되는 코드가 필요한 것은 여전히
실제 모드가 필요하지만 예외가 둘 있습니다. container 유형의 블록은 자체 화면이
있는 인벤토리를 가지며, 엔티티 변형은 저장소를 가질 수 있습니다. 기계는
만들 수 없지만, 광석, 울타리, 음식, 유체는 가능합니다.


다른 버전용 팩
--------------

이 모드의 1.12.2 계열용으로 만든 팩은 그대로 로드됩니다. zip은 한 번 자체
안에서 이 버전용 versions 폴더로 변환되고 1.12.2 파일은 그대로 남으므로,
같은 zip이 모든 버전에서 계속 작동합니다.


변경 사항 확인
--------------

텍스처, 모델, 언어 파일, 그 밖에 assets 아래의 모든 것을 다시 불러오려면
F3+T를 누르세요. 서버에서나 data 아래의 항목은 /reload를 입력하세요.

새 파일을 추가하거나 삭제했다면 대신 /rdpl reload를 사용하세요. 이미 있던
파일을 수정한 경우에는 F3+T나 /reload만으로 충분합니다.

/rdpl list는 로드된 모든 팩과 그 내용을 보여 줍니다. 팩에 마우스를 올려
확인하세요.

/rdpl which minecraft:textures/block/stone.png는 어느 팩이 해당 파일을
제공하는지, 그 아래에 가려진 팩은 무엇인지 보여 줍니다.

/rdpl config unused는 rdploader/config에 있는 옵션 파일 중 설치된 어떤 팩도
더 이상 정의하지 않는 것을 나열하고, /rdpl config prune은 그 파일을
삭제합니다. /rdpl pixelmap은 픽셀 맵이 어떻게 나왔는지 보여 주고,
/rdpl biome list와 here는 생물 군계에 대해 알려 줍니다.

이 명령어들은 사용자의 컴퓨터에 있는 파일만 읽으므로 관리자 권한 없이
작동합니다. 전용 서버에서는 /rdplserver reload가 서버의 사본을 다시
검색하며, /rdplserver list, which, unused가 서버에 대해 응답합니다.


문제가 생겼을 때
----------------

먼저 로그를 확인하세요. logs/rdpl.log에는 로드된 모든 팩과 건너뛴 모든 팩이
이유와 함께 나열되며, 문제가 있으면 이유를 설명하는 경고로 기록됩니다.

/rdpl unused는 팩 안에서 아직 아무도 요청하지 않은 파일을 나열하며, 대개
경로의 오타를 뜻합니다. 게임이 로딩을 마친 뒤에 실행하세요. 플레이 중인
언어가 아닌 다른 언어처럼 필요할 때만 로드되는 파일도 있다는 점을
염두에 두세요.

대소문자는 중요합니다. 파일 이름이 Stone.png인데 게임이 stone.png를
요청했다면 로드는 되지만, 경고가 이름을 바꾸라고 알려 줍니다. 꼭 바꾸세요.
이 모드 외의 곳에서는 해당 파일을 아예 찾지 못하기 때문입니다. 언어 파일에서
특히 자주 실수하는데, 이름은 en_US.json이 아니라 en_us.json입니다.

파일이 'assets' 또는 'data' 폴더 안에 있는지 확인하세요. 둘 중 어느 것도
없는 zip은 건너뛰며 로그에 그 사실이 기록됩니다.


발전 과제와 레시피
------------------

스크립트가 추가하거나 대체한 레시피는 스크립트가 붙인 이름으로 식별됩니다.
발전 과제로 그 레시피를 해금하려면, 해당 레시피를 지정하는 발전 과제 파일을
이곳에 넣으세요. 그러면 발전 과제가 처음부터 끝까지 다시 작동합니다.

그런 레시피에는 스크립트에서 고정된 이름을 지정하세요. 자동으로 만들어진
이름은 레시피를 수정하는 순간 바뀔 수 있으므로, 발전 과제가 가리키게 하기에
안전하지 않습니다.


rdploader 폴더 자체는 config/resourcedatapackloader-common.toml의
rootDirectory 옵션으로 옮기거나 이름을 바꿀 수 있습니다. 절대 경로도
사용할 수 있으며, 재시작해야 적용됩니다.

이 파일 옆에 pack.png를 두면 팩에 아이콘이 생깁니다.

이 파일은 모드가 작성하며 내용이 바뀔 때마다 최신 상태로 갱신되므로,
여기에 직접 입력한 내용은 다음에 게임을 시작할 때 교체됩니다.

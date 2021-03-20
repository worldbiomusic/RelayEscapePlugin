# 설명
- 플러그인 전체 구조에 대한 문서


# 구조
- 모든것은 Main룸에서 진행되는 RelayEscape게임을 중점으로 진행됨
- 모든 클래스는 Main 클래스를 통해서 서로 인스턴스를 주고 
- 파일저장은 yml을 사용하지 않고, 자바 Serializable을 이용해서 객체를 저장/읽기 함
- 서버는 시간 사이클로 돌아감

# 서버의 릴레이 시작 포인트
- Main -> GameManager 생성자 -> init()메소드에서 resetRelay()를 실행해서 릴레이를 시작함

# 중요한 클래스들
- `Main`: 시작 클래스
- `GameManager`: Main룸의 RelayEscape게임 관련된 이벤트 처리 클래스
- `CommonListener`: 서버에서 발생하는 모든 이벤트 순서 처리 클래스
- `RelayManager`: RelayEscape게임의 시간 흐름관련된 대부분 관리(이벤트 처리 제외)
- `PlayerDataManager`: 플레이어 데이터를 관리하는 클래스
- `DataManager`: 플러그인에서 자바 Serializable을 사용해서 객체 관리해주는 클래스


# 소스코드 폴더 구조
- cmd: 명령어 관련
- data: 데이터 형식 관련
- listener: 이벤트 처리 관련
- util: 매니저 관련, 잡다한것 관련


# 데이터 파일
위치: plugins/RelayEscapePlugin
- `player.dat`: PlayerData클래스 데이터가 들어있는 파일로 서버에서 플레이어 관리하는데 쓰이는 정보가 포함됨
- `room.dat`: 서버의 모든 Room클래스 데이터가 들어있는 파일
- `minigame.dat`: MiniGame클래스의 게임타입과 랭크 데이터가 들어있는 파일
- `npc.dat`: NPC(EntityPlayerData 클래스)데이터를 저장하는 파일
- `skinData.dat`: SkinManager클래스의 플레이어 스킨 데이터(texture, signature)를 저장하는 파일

---
# 클래스 링크
클래스 세부 설명

- ## cmd
> - [Commands](Commands.md): 

- ## data
> - [BlockData](BlockData.md): Room의 블럭을 저장할때 사용하는 클래스
> - [MiniGameLocation](MiniGameLocation.md): 미니게임 위치 관련
> - [PlayerData](PlayerData.md): 서버에서 관리하는 플레이어 데이터 관련
> - [Room](Room.md): 룸 
> - [RoomLocation](RoomLocation.md): 룸 위치 관련
> - [RoomLocker](RoomLocker.md): 룸 블럭 잠금관련(꼭 룸에 종속되진 않음)

- ## listerner
- 모든 이벤트를 CommndListener에서 순서를 관리하고 다른 클래스로 넘겨줄까 고민중!
> - [CommonListener](CommonListener.md): 
> - [GameManager](GameManager.md): 
> - [GoodsListener](GoodsListener.md): 
> - [EventBlockListener](EventBlockListener.md): 

- ## util
> - [Main](Main.md): 
> - [PlayerDataManager](PlayerDataManager.md): 
> - [RankManager](RankManager.md): 
> - [RelayManager](RelayManager.md): 
> - [RoomManager](RoomManager.md): 
> - [Setting](Setting.md): 
> - [StageManager](StageManager.md): 
> - [WorldEditAPIController](WorldEditAPIController.md): 유저가 만든 룸을 월드에딧 스카미틱 파일(.schem)로 관리해주는 클래스

- ## util.config
> - [DataManager](DataManager.md): 
> - [DataMember](DataMember.md): 

- ## util.enum
> - [MiniGameType](MiniGameType.md): 
> - [RelayTime](RelayTime.md): 
> - [Role](Role.md): 
> - [RolePermission](RolePermission.md): 
> - [RoomType](RoomType.md): 

- ## util.minigame
> - [MiniGameManager](MiniGameManager.md): 
> - [MiniGame](MiniGame.md): 
> - [SoloMiniGame](SoloMiniGame.md): 
> - [CooperativeMiniGame](CooperativeMiniGame.md): 
> - [BattleMiniGame](BattleMiniGame.md): 
> - [MiniGameRankManager](MiniGameRankManager.md): SoloMiniGame, CooperativeMiniGame의 rank를  계산해주는 클래스

- ## util.shop
> - [GoodsRole](GoodsRole.md): 
> - [ShopGoods](ShopGoods.md): 
> - [ShopManager](ShopManager.md): 

- ## util.discord
> - [DiscordBot](DiscordBot.md): 서버 디스코드 채널에 사용되는 디스코드 봇 클래스
> - [ChatListener](ChatListener.md): 디스코드 봇 리스너 클래스

- ## util.google
> - [GoogleDrive](GoogleDrive.md): 유저가 만든 룸 파일(.schem) 을 자동으로 구글 드라이브에 올리는 클래스 












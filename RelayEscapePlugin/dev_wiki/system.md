# 설명
- 플러그인 전체 구조에 대한 문서


# 구조
- 모든것은 Main룸에서 진행되는 RelayEscape게임을 중점으로 진행됨
- 모든 클래스는 Main을 통해서 서로 상호작용함
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
> - [Commands][Commands]: 

- ## data
> - [BlockData][BlockData]: Room의 블럭을 저장할때 사용하는 클래스
> - [MiniGameLocation][MiniGameLocation]: 미니게임 위치 관련
> - [PlayerData][PlayerData]: 서버에서 관리하는 플레이어 데이터 관련
> - [Room][Room]: 룸 
> - [RoomLocation][RoomLocation]: 룸 위치 관련
> - [RoomLocker][RoomLocker]: 룸 블럭 잠금관련(꼭 룸에 종속되진 않음)

- ## listerner
- 모든 이벤트를 CommndListener에서 순서를 관리하고 다른 클래스로 넘겨줄까 고민중!
> - [CommonListener][CommonListener]: 
> - [GameManager][GameManager]: 
> - [GoodsListener][GoodsListener]: 
> - [EventBlockListener][EventBlockListener]: 

- ## util
> - [Main][Main]: 
> - [PlayerDataManager][PlayerDataManager]: 
> - [RankManager][RankManager]: 
> - [RelayManager][RelayManager]: 
> - [RoomManager][RoomManager]: 
> - [Setting][Setting]: 
> - [StageManager][StageManager]: 

- ## util.config
> - [DataManager][DataManager]: 
> - [DataMember][DataMember]: 

- ## util.enum
> - [MiniGameType][MiniGameType]: 
> - [RelayTime][RelayTime]: 
> - [Role][Role]: 
> - [RolePermission][RolePermission]: 
> - [RoomType][RoomType]: 

- ## util.minigame
> - [MiniGameManager][MiniGameManager]: 
> - [MiniGame][MiniGame]: 
> - [SoloMiniGame][SoloMiniGame]: 
> - [CooperativeMiniGame][CooperativeMiniGame]: 
> - [BattleMiniGame][BattleMiniGame]: 
> - [MiniGameRankManager][MiniGameRankManager]: SoloMiniGame, CooperativeMiniGame의 rank를  계산해주는 클래스

- ## util.shop
> - [GoodsRole][GoodsRole]: 
> - [ShopGoods][ShopGoods]: 
> - [ShopManager][ShopManager]: 


[Commands]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/Commands.md
[BlockData]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/BlockData.md
[MiniGameLocation]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/MiniGameLocation.md
[PlayerData]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/PlayerData.md
[Room]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/Room.md
[RoomLocation]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/RoomLocation.md
[RoomLocker]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/RoomLocker.md
[CommonListener]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/CommonListener.md
[GameManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/GameManager.md
[GoodsListener]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/GoodsListener.md
[Main]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/Main.md
[PlayerDataManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/PlayerDataManager.md
[RankManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/RankManager.md
[RelayManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/RelayManager.md
[RoomManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/RoomManager.md
[Setting]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/Setting.md
[DataManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/DataManager.md
[DataMember]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/DataMember.md
[MiniGameType]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/MiniGameType.md
[RelayTime]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/RelayTime.md
[Role]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/Role.md
[RolePermission]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/RolePermission.md
[RoomType]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/RoomType.md
[MiniGameManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/MiniGameManager.md
[MiniGame]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/MiniGame.md
[SoloMiniGame]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/SoloMiniGame.md
[CooperativeMiniGame]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/CooperativeMiniGame.md
[BattleMiniGame]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/BattleMiniGame.md
[MiniGameRankManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/MiniGameRankManager.md
[GoodsRole]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/GoodsRole.md
[ShopGoods]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/ShopGoods.md
[ShopManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/ShopManager.md
[StageManager]: https://github.com/worldbiomusic/RelayEscapePlugin/blob/edit-wiki/EscaperPlugin/wiki/StageManager.md















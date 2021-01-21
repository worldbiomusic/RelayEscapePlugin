# 설명
- 

# 사용법
- 

# 주의사항
- PVP관련된 미니게임에선 플레이어가 죽게 놯두면 안됨, 대신 체력 계산해서 죽는데미지이면 미니게임 관전 지역(getDeadPlayerRepsawnLocation())으로 tp해야 함
(lobby가서 다른 미니게임 시작하면 pvp관련 미니게임 끝날때 lobby로 tp되므로 꼬임)
- PVP관련된 미니게임에선 1명 남았을 시 게임 종료해야 함
(클래스에 변수 1개 추가하고 this.getAllPlayer().size()에서 빼서 플레이어가 죽을때마다 1명 남은것을 검사해야 함)

# 개선할 것
- pDataManager static으로 놯둬서 MiniGame클래스 내에 pDataManager 메소드 인자로 넘겨준거 다 삭제하기

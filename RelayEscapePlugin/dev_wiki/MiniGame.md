# 설명
- 미니게임 공통 슈퍼 클래스

# 사용법
- Solo, Cooperative, Battle을 구현할때 순서에 맞게 잘 

# 게임 추가하는 순서
1. 게임 구상
2. Solo / Cooperative / Battle 중 게임 유형 선택
3. 선택한 게임 유형 클래스 상속해서 클래스 새로 만들기 (생성자에 MiniGameType 직접 설정해서 넘기기)
4. MiniGameType에 새로운 게임 enum 추가(이벤트 반응범위: pos1~pos2, 스폰지점: roomLoc 등등)
6. MiniGameManager의 registerGames()에 새로운 게임 리스트에 추가
7. 게임에 사용되는 event는 CommonListener클래스에서 이벤트 잡아서 MiniGameManager의 processEvent(e)로 넘기기  
(정확한 이벤트 클래스만을 넘겨야 함 ex. 블럭 부수기면 CommonListener에서 BlockBreak이벤트리스너에서 넘기기)  
(CommonListener에서 다른 이벤트 처리하는것들과 순서 중요하게 해야 함)  


# 게임 구성하는 방법
1. 선택한 게임 유형 클래스 상속해서 abstract 메소드 구현하기
> - processEvent(): event를 넘겨받아서 필요한 것으로 형 변환한다음에 사용해야 함 (보통 점수 관리에 사용)
> - getGameTutorialStrings(): 게임 시작시 출력되는 튜토리얼 문자열을 반환해야 함
2. 필요한 메소드는 오버라이딩 해서 수정해서 사용
> - runTaskAfterStartGame(): 게임 시작(activated) 직후 바로 실행되는 메소드로 미니게임에 필요한 것(ex.칼, 곡괭이)을 지급하는 용도로 사용  
> - runTaskAfterExitGame(): 게임 종료(exitGame()) 직후 바로 실행되는 메소드


# 캐치하는 이벤트 종류
- BlockEvent: 블럭 관련
- EntityEvent: 엔티티 관련
- PlayerEvent: 플레이어 관련

# 미니게임 유저 플레이 과정
1. player가 표지판을 클릭해서 MiniGameManager의 enterRoom() 실행
2. enterRoom()에서 해당 미니게임이 activated 상태가 아닐시 해당 미니게임의 enterRoom() 실행
3. Solo, Cooperative, Battle 미니게임마다 처리 알고리즘이 다름

`처리 알고리즘`
- Solo
> 3.1. 이미 플레이중인 사람이 있으면 x  
> 3.2. 토큰 검사  
> 3.3. 게임 준비, 유저 세팅, 태스크 활성화  
> 3.4. 게임 진행  
- Cooperative
> 3.1. master 없으면 토큰 검사, 게임 준비, 유저 세팅, 태스크 활성화  
> 3.2. master 있으면 wait list로 추가    
> 3.2.1. master의 허락으로 입장시 토큰 검사, 유저 세팅  
> 3.3. 게임 진행  
- Battle
> 3.1. 토큰 검사  
> 3.2. 누군가 없을때 게임 준비, 유저 세팅, 태스크 활성화  
> 3.3. 누군가 있을때 유저 세팅  
> 3.4. 게임 진행  

4. 게임 종료, 보상 지급

# 미니게임 이벤트 처리 과정 (processEvent())
`그냥 Event로 한개로 공통으로 받아서 넘겨도 될것같긴 한데, 다른 이벤트와 순서가 중요한 경우가 있어서 방법 찾는중`
1. 미니게임에서 사용되는 이벤트를 다른 리스너에서 받아서 MiniGameManager의 processEvent(e)에 이벤트를 넘김
2. MiniGameManager의 processEvent(e) 메소드에서 각 이벤트를 instanceof 연산자로 체크한다음에 형 변환을 하고, Location을 추출한 다음에
위치에 맞는 미니게임을 골라서 해당 미닌게임에게 이벤트를 전달(MiniGame.processEvent(e))
3. 미니게임에서 processEvent(e)로 이벤트를 받고, 필요한 형 변환을 한다음, 조건에 맞게 처리(점수, 행동, 등등)

# 미니게임 종류별 제한해야하는것
- 게임유형이 비슷한것들은 미리 만들어놓은것 보고 따라서 처리해야 함  
- `pvp관련`
- 체력이 0이하로 떨어질때 죽게 놯두지 말고, 관전지역으로 tp해줘야 함(+점수 처리)
- 칼, 화살 둘다 이벤트 

- `jump map관련`
- RESPAWN, HURT 이벤트 블럭 사용금지

# 주의사항
- 서브 클래스에서 수퍼 클래스 메소드 오바라이딩 할떄 `super.method()` 꼭! 하기

# 개선할 것
- 각 Solo, Cooperative, Battle 미니게임 클래스에서 중복되는 구간이 많이 존재함, 하지만 다른 구간도 껴있어서 공통적인것을
MiniGame클래스로 빼고, MiniGameInterface를 따로 만들어서 메소드는 강제하기

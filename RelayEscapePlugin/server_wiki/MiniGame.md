# `English`
# Description
- by paying tokens and playing the game within a given time, you can get rewards for each minigame ranking section

# Type
- [Solo-MiniGame][Solo-MiniGame]: a minigame that you can challenge yourself
- [Cooperative-MiniGame][Cooperative-MiniGame]: a minigame that wo or more players can enjoy in cooperative
- [Battle-MiniGame][Battle-MiniGame]: a minigame that two players can battle each other

# Ranking Update
only 1 highest score was scored (not duplicate)
- Solo-MiniGame: highest score with your name
- Cooperative-MiniGame: highest score with your team member names
- Battle-MiniGame: no rank

# Reward
Reward is determined by the competition of scores among users, not by absolute standard score
Rewards are paid based on the rank score quartile(ascending order)
- 1st quartile (0%~25%): 1/2 times the entrance fee token
- 2nd quartile (25%-50%): 2/2 times the entrance fee token
- 3rd quartile (50%~75%): 3/2 times the entrance fee token
- 4th quartile (75%-100%): 4/2 times the admission fee
- 1st place: 6/2 times the entrance fee token

# Penalty
- You get a penalty when you leave the minigame during playing (penalty: token decrease fee x 2)


# Exception Handling
- Exceptions caused by one's intention or mistake are handle without reward (server quit)
- Unavoidancable exceptions are handled normally until the game is played and receive reward(RelayTime changed)

# Sign
> [MINIGAME]  
> `game title`  
> TOKEN `amount`  
> `game type`  
---------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------
# `Korean`
# 설명
- 토큰을 지불하고 주어진 시간내에 게임을 해서 달성한 스코어로 각 미니게임 랭크순위 구간에 따른 보상을 얻을 수 있음

# 타입
- [솔로 미니게임][Solo-MiniGame]: 혼자서 도전할 수 있는 미니게임
- [협동 미니게임][Cooperative-MiniGame]: 2인이상이서 협동해서 즐길 수 있는 미니게임
- [배틀 미니게임][Battle-MiniGame]: 2명이서 서로 배틀할 수 있는 미니게임

# 랭크 업데이트
최고점수 1개만 기록됨(중복 안됨)
- 솔로 미니게임: 자신의 이름으로 최고 점수
- 협동 미니게임: 협동한 인원구성의 이름으로 최고 점수
- 배틀 미니게임: 랭크 없음

# 보상
절대적인 기준이 아닌 유저들간의 점수의 경쟁으로 보상이 결정됨  
랭크점수 4분위수(오름차순)를 기준으로 보상이 지급됨  
- 1분위수(0%~25%): 입장료 토큰의 1/2배
- 2분위수(25%~50%): 입장료 토큰의 2/2배
- 3분위수(50%~75%): 입장료 토큰의 3/2배
- 4분위수(75%~100%): 입장료 토큰의 4/2배
- 1등: 입장료 토큰의 6/2배

# 패널티
- 자신의 의도로 플레이중인 미니게임을 나가면 패털티가 부여됩니다 (패널티: 입장료 x 2 토큰 감소)

# Exception Handling
- 자신의 의도, 실수로 발생한 예외는 보상을 받지 못하고 처리됩니다 (서버 나기기)
- 어쩔 수 없는 예외는 게임이 진행된 구간까지 정상적으로 처리되어 종료됩니다 (RelayTime의 변경)

# 표지판
> [MINIGAME]  
> `game title`  
> TOKEN `amount`  
> `game type`  









[Solo-MiniGame]: https://github.com/worldbiomusic/RelayEscape/wiki/Solo-MiniGame
[Cooperative-MiniGame]: https://github.com/worldbiomusic/RelayEscape/wiki/Cooperative-MiniGame
[Battle-MiniGame]: https://github.com/worldbiomusic/RelayEscape/wiki/Battle-MiniGame
















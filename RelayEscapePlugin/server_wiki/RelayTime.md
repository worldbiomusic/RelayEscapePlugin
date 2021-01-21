# `English`
# Description
- The server has Time Cycle which manages player's everything  
- Sequence: `WaitingTime -> MakingTime -> TestingTime -> ChallengingTime  -> WaitingTime -> ...` (cycle)
- At the beggining of each time, goods that math the role of the RelayTime are automatically add to inventory
- Every RelayTime has its own life time, after the end of the life time, it automatically moves to the next RelayTime according to the cycle flow

# RelayTime Cycle Flow
![RelayTimeCycleFlow](https://github.com/worldbiomusic/RelayEscape/blob/main/imgs/RelayTimeCycleFlow.png)

-----
# RelayTime List
## [WaitingTime][WaitingTime]
- Time when waiter can prepare
> - Maker(`Waiter`): prepare for next Time
> - Challenger(`Waiter`): prepare for next Time

## [MakingTime][MakingTime]
- Time when maker can create room
> - Maker(`Maker`): make room(should put core block)
> - Challenger(`Waiter`): enjoy other rooms

## [TestingTime][TestingTime]
- Time when tester try to pass the room test
> - Maker(`Tester`): should clear own room for certainty
> - Challenger(`Waiter`): enjoy other rooms

## [ChallengingTime][ChallengingTime]
- Time when challenger try to find the core
> - Maker(`Viewer`): just watch
> - Challenger(`Challenger`): try to find the core
---------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------
# `Korean`
# 설명
- 이 서버는 유저의 모든걸 관리하는 시간사이클 가지고 있습니다
- 순서: `WaitingTime -> MakingTime -> TestingTime -> ChallengingTime  -> WaitingTime -> ...` (cycle)
- 매 타임이 시작될 때마다 `해당 타임의 역할`에 맞는 굿즈가 자동으로 인벤토리에 지급됩니다
- 모든 시간사이클은 각자의 수명시간을 가지고 있습니다. 수명시간이 다 한 후에는 사이클흐름에 맞게 다음 시간사이클로 자동으로 넘어갑니다

# 릴레이타임 사이클 흐름
![RelayTimeCycleFlow](https://github.com/worldbiomusic/RelayEscape/blob/main/imgs/RelayTimeCycleFlow.png)

-----
# RelayTime List
## [WaitingTime][WaitingTime]
- 모든 플레이어가 준비를 하는 타임
> - Maker(`Waiter`): 다음 시간 준비
> - Challenger(`Waiter`): 다음 시간 준비

## [MakingTime][MakingTime]
- 메이커가 룸을 만드는 타임
> - Maker(`Maker`): 룸을 만듬(코어 놓아야 함)
> - Challenger(`Waiter`): 서버 즐기기

## [TestingTime][TestingTime]
- 테스터가 만든 룸을 테스트하는 타임
> - Maker(`Tester`): 확실성을 위해서 테스트를 성공해야 함
> - Challenger(`Waiter`): 서버 즐기기

## [ChallengingTime][ChallengingTime]
- 도전자들이 도전하는 타임
> - Maker(`Viewer`): 관전
> - Challenger(`Challenger`): 코어를 찾기




[WaitingTime]: https://github.com/worldbiomusic/RelayEscape/wiki/WaitingTime
[MakingTime]: https://github.com/worldbiomusic/RelayEscape/wiki/MakingTime
[TestingTime]: https://github.com/worldbiomusic/RelayEscape/wiki/TestingTime
[ChallengingTime]: https://github.com/worldbiomusic/RelayEscape/wiki/ChallengingTime
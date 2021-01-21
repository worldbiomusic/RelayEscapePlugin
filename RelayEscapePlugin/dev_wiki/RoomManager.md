# 설명
- Room 클래스를 저장하고, 관리해주는 클래스
- DataMember를 구현해서 Room데이터들을 저장함
- Room 클래스는 Serializable을 구현해서 저장함

# 정보
- roomData: 로드한 룸 데이터들 (Map<String, Room>)
- rooms: 룸 타입마다 현재 지정된 룸 변수 저장 (Map<RoomType, Room>)
- 룸 타입: `Main`, `Practice`, `Mini_Game`, `Fun`

# 사용법
- 룸 가져오기: getRoom()
- 룸 설정하기: setRoom()
- 룸 잠금/해제하기: lockRoom(), unlockRoom()
- 룸 블럭 채우기: fillSpace()
- Main룸 기록 시작/저장: startMainRoomDurationTime(), recordMainRoomDurationTime()


# 기본 룸 basic rooms
- `empty`: 텅 빈 룸으로 룸을 비어있게 초기화할 때 사용
- `base`: core가 간단히 설치되어 쉽게 clear할 수 있도록 만든 베이스 맵


# 주의사항
- 서버의 MainRoom에서 기본적으로 사용할 룸은 생성자에서 바로 registerBasicRooms()로 등록시킴
- 룸 저장할때 Material.AIR는 null로 저장함 = 룸 채울때(fillSpace()) null은 Material.AIR로 채움 -> 데이터 크기 작게


# 개선할 것
- 룸 BlockData저장관련해서 크기 더 줄이는 법(확실x)
> 1. HashMap<Location, BlockData>으로 저장(fill 하기 전에 모두 AIR로 변경후에 하면 필요한 Location블럭만 저장하면 필요한 위치블럭만 저장해서 불러와서 사용가능)
> 2. Stream부분을 더 빠르게
> 3. 유저의 Room데이터를 일정기간 후에 삭제(굿즈로 연장 가능)
- fillSpace() 코드가 너무 더러움


# 상황
- 일반 BlockData를(AIR도 BlockData로 저장) 저장하면 약 0.012KB이지만, null로 저장하면 0.001KB크기로 저장되어 12배 공간 이득을 볼 수 있음
- 1초에 62.5KB정도 저장하는 속도가 나옴
- [AIR를 null로 저장했을때]
> 룸 개수: 51개  
> 크기: 255KB  
> 룸 1개당 크기: 5KB  
> null(AIR)저장 크기 = 0.001KB  
> 일반블럭 저장크기 = 0.012KB  
> *12배 차이남  

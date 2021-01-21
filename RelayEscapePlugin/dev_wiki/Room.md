# 설명
- 서버의 룸 데이터를 관리하는 클래스
- 블럭데이터는 직접 만든 BlockData클래스의 리스트로 관리됨 (Material과 state가 기록됨)

# 속성
- `String title`: 룸 제목
- `String maker`: 룸 제작자
- `List<BlockData> blocks`: 룸에 저장된 블럭 데이터들
- `int challengingCount`: 룸이 MainRoom에 ChallengingTime때 도전맵으로 등장한 횟수
- `int clearCount`: ChallengingTime때 clear된 횟수
- `LocalDateTime birth`: 룸 생성날짜
- `int voted`: 투표수
- `double avgDurationTime`: 룸의 평균 clear 시간

# 속성에 관하여
- blocks안에 있는 Block들의 위치정보는 사용하면 안됨 (정해진 Room에 순서대로 block들이 정해지는것이기 때문)
- avgDurationTime는 분(min) 단위

# 사용법
- 생성자로 객체를 만들어서 사용

# 주의사항
- 

# 개선할 것
- voted 사용하기

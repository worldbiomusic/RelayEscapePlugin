# 설명
- 미니게임의 위치 구간 관련 클래스
- 이 클래스가 사용되는 곳은 단순히 미니게임 관련 이벤트가 발생했을때 이벤트 위치가 미니게임구간(이 클래스)내에서 발생한
이벤트일때 넘겨주는 조건 검사에 사용됨

# 사용법
- static으로 접근
- 필요한 메소드 사용

# 주의사항
- 이 클래스에서 지정한 미니게임의 위치 구간 검사에 사용되는 목적일뿐임 (ex.FIND_THE_RED의 꽃 블럭들 구간만 위치가 등록되어있음)

# 개선할 것
- 위치로 미니게임 구간 검사하는 코드가 중복이 너무 많음 (reflection이나 배열로 간소화 가능할듯)

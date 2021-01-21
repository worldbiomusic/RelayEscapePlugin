# 설명
- 플러그인 메인 클래스

# 사용법
- 플러그인 내에서 사용되는 대부분 객체(인스턴스)의 생성과 서로 참조변수등록이 이루어 이는 만남의 장소

# 구조
- `setupMain`: Main클래스 세팅
- `setupTools`: 서버에서 공통적으로 사용되는 static클래스들 세팅
- `setupManagers`: 서버에서 사용되는 인스턴스의 매니저관련 클래스 세팅
- `registerListeners`: 리스너 등록
- `registerCommands`: 커맨드 등록


# 주의사항
- setupManagers()에서 서로 생성자로 인스턴스를 많이 넘겨주는 곳이므로 순서가 꼬이지 않게 조심
- DataManager.registerMember()메소드는 해당 멤버가 초기화(생성자 호출)된 뒤 코드에 선언되야 함


# 개선할 것
- setupTools

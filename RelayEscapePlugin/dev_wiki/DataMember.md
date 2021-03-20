# 설명
- DataManager에 등록될 멤버 클래스들이 구현해야 하는 interface
- 보통 데이터 클래스들을 관리하는 클래스들이 사용함 (예.PlayerDataManger(DataMember)가 PlayerData(Serializable)데이터를 관리)

# 코드
```
public interface DataMember
{
	public void installData(Object obj);
	
	public Object getData();
	
	public String getDataMemberName();
}
```

# 사용법
- `installData(Object obj)`: DataManager에서 distributeData()로 멤버객체에게 로드한 데이터를 넘겨줄때 호출하는 메소드  
> 멤버 객체의 데이터에 할당하면 됨
- `getData()`: DataManager에서 save()로 멤버객체에게서 데이터를 가져와서 저장할때 호출되는 메소드
> 멤버객체는 저장할 데이터를 반환하면 됨
- `getDataMemberName()`: 멤버객체의 이름으로 파일이름이 `반환값.dat`으로 저장됨, DataManager에서 멤버 구분하는데 사용됨
> 멤버 이름을 반환하면 됨

# 주의사항
- DataManager에서 해당객체에게 넘겨줄 데이터(로드된 데이터)가 없으면 getInstall()이 아예 호출되지 않는다
- 클래스에서 관리하는 데이터 클래스는 Serializable을 구현해야 함
- 클래스에서 관리하는 데이터 클래스에서 저장하지 않을 변수는 `transient` 선언해야 함
- 클래스에서 관리하는 데이터 클래스 객체를 로드해서 불러오므로 각 데이터 클래스 객체들은 생성자를 실행하지 않으므로  
`transient`로 선언된 변수들은 초기화가 안 되어 있음 (초기화 해줘야 하는 상황이 가끔 생김(NullPointer때문에))
- 멤버클래스는 저장될 변수들을 생성자같은곳에서 처음에는 초기화(처음에 데이터를 초기화 한다음에 저장이 되야 하기 때문에)
- 만약 리스트 관련 여러 객체가 있고, 미래에 추가될 수 있는 변수는 생성자에서 초기화는 물론, getInstall(Object obj)메소드에서 한번더 모든 변수를 등록해줘야 함(이것을 안하고 생성자에서만 등록하면, 새로 추가한것이 데이터에 못들어감, 저장한것이 나중에 데이터로 할당되기 때문에)
- DataMember를 구현한 클래스들을 사용하려면 전에 DataManager의 register후 distributeData()메소드가 실행되서 데이터를 정상적으로 받은 뒤 사용해야 한다

# 개선할 것
- getInstall()메소드에서 데이터 업데이트 해줘야 하는 번거로움.... 

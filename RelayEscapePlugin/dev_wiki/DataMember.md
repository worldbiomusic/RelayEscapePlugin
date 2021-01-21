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
- DataManager에서 해당객체에게 넘겨줄 데이터(로드된 데이터)가 없으면 getInstall()이 호출되지 않는다
- 클래스에서 관리하는 데이터 클래스는 Serializable을 구현해야 함
- 클래스에서 관리하는 데이터 클래스에서 저장하지 않을 변수는 `transient` 선언해야 함
- 클래스에서 관리하는 데이터 클래스 객체를 로드해서 불러오므로 각 데이터 클래스 객체들은 생성자를 실행하지 않으므로  
`transient`로 선언된 변수들은 초기화가 안 되어 있음 (초기화 해줘야 하는 상황이 가끔 생김(NullPointer때문에))

# 개선할 것
- 

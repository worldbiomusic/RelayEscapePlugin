package com.wbm.plugin.util.data.json;

import java.io.Serializable;

public interface JsonDataMember extends Serializable {
	// 데이터 주기: 각 메소드에서 Gson으로 String을 원하는 데이터 타입으로 변환시켜서 사용해야 함
	public Object getData();

	// 데이터 받아오기
	public void distributeData(String jsonString);

	// 확장자까지 .json 붙이기
	public String getFileName();
}

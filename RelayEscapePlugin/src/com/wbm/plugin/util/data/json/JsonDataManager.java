package com.wbm.plugin.util.data.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonDataManager {
	/*
	 * [설명]
	 * 
	 * json데이타 관리해주는 클래스
	 * 
	 * 모든 파일은 data/ 로 시작하는곳에서 모든 데이터 관리함
	 * 
	 * 
	 * [참고: JSON <-> Map]
	 * 
	 * map to json:
	 * https://www.delftstack.com/ko/howto/java/how-to-convert-hashmap-to-json-
	 * object-in-java/
	 * 
	 * json to map:
	 * https://www.delftstack.com/ko/howto/java/how-to-convert-json-to-map-in-java/
	 */

	Gson gson;

	Map<String, JsonDataMember> members;

	public JsonDataManager() {
		this.members = new HashMap<>();
		this.gson = new GsonBuilder().setPrettyPrinting().create();

		this.makeResourceRootDir();
	}

	public void registerMember(JsonDataMember member) {
		this.members.put(member.getFileName(), member);
	}

	public void unregisterMember(JsonDataMember member) {
		this.members.remove(member.getFileName());
	}

	public void distributeAllData() {
		// 모든 멤버에게 데이터 배분
		for (JsonDataMember member : this.members.values()) {
			String jsonString = this.load(new File(member.getFileName()));
			member.distributeData(jsonString);
		}
	}

	public void saveAllData() {
		// 모든 멤버의 데이터 저장
		for (JsonDataMember member : this.members.values()) {
			Object obj = member.getData();
			File f = new File(member.getFileName());
			this.save(f, obj);
		}
	}

	private void makeResourceRootDir() {
		// data dir 생성
		File dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
	}

	/*
	 * [사용법]
	 * 
	 * Player p = new Player("abcd");
	 * 
	 * dataM.save(new File("test.json"), p);
	 */
	public void save(File file, Object obj) {
		try {
			File f = this.resourceFile(file);

//			if (f.exists()) {
			Writer writer = new FileWriter(f);
			this.gson.toJson(obj, writer);

			writer.close();
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* [코드 참고] Gson Github: */
	// https://github.com/google/gson/blob/master/gson/src/main/java/com/google/gson/Gson.java
//	public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
//	    Object object = fromJson(json, (Type) classOfT);
//	    return Primitives.wrap(classOfT).cast(object);
//	  }

	/*
	 * [사용법]
	 * 
	 * Player p = dataM.load(new File("test.json"), Player.class);
	 */
	public <T> T load(File file, Class<T> classOfT) {
		/*
		 * class 자체를 json파일로 사용할 때 사용하는 메소드
		 */
		T returnObj = null;
		try {
			File f = this.resourceFile(file);
			Reader reader = new FileReader(f);

			Object obj = this.gson.fromJson(reader, Object.class);
			String objString = this.gson.toJson(obj);

			returnObj = this.gson.fromJson(objString, (Type) classOfT);

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObj;
	}

	public String load(File file) {
		/*
		 * Object를 json파일로 사용할 때 사용하는 메소드
		 */
		String jsonString = null;
		try {
			File f = this.resourceFile(file);
			if (!f.exists()) {
				return null;
			}

			Reader reader = new FileReader(f);

			Object obj = this.gson.fromJson(reader, Object.class);
			jsonString = this.gson.toJson(obj);

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	public File resourceFile(File file) {
		return new File("data" + File.separator + file);
	}

	public boolean exists(File file) {
		return this.resourceFile(file).exists();
	}
}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

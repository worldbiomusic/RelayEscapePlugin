package com.wbm.plugin.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
public class DataManager
/*
 * 여러개 클래스의 java Serializable로 Object read/write해주는 클래스 여기서 사용되는 클래스들은
 * Serializable을 구현해야 함!
 */
{
	File baseDir;
	Map<String, Object> map;
	Map<String, DataMember> members;

	public DataManager(String baseDirPath)
	{
		// set baseDir
		this.baseDir=new File(baseDirPath);

		// init maps
		this.map=new HashMap<>();
		this.members = new HashMap<>();

		// load map data
		this.loadData();
	}
	
	public void registerMember(DataMember member) {
		this.members.put(member.getDataMemberName(), member);
	}

	public void distributeData()
	{
		// distribute map data to members
		for(Entry<String, DataMember> memberEntry : this.members.entrySet()) {
			String memberName = memberEntry.getKey();
			DataMember member = memberEntry.getValue();
			
			Object data = this.map.get(memberName);
			// 없으면 null을 반환하기 때문에 줄 필요가 없음 (member에서는 null처리 안해도 됨, 확실한 데이터가 있을때만 호출되므로)
			if(this.map.containsKey(data)) {
				member.installData(data);
			}
		}
	}
	
	public void save()
	{
		// gether members data to map
		for(Entry<String, DataMember> memberEntry : this.members.entrySet()) {
			String memberName = memberEntry.getKey();
			DataMember member = memberEntry.getValue();
			Object data = member.getData();
			this.map.put(memberName, data);
		}
		
		// save to file
		this.saveDataToFile();
	}

	private void saveDataToFile()
	{
		try
		{
			// write object(map) to file
			File file=new File(this.baseDir, "allData.dat");
			FileOutputStream fos=new FileOutputStream(file);
			ObjectOutputStream oos=new ObjectOutputStream(fos);

			oos.writeObject(this.map);
			oos.close();
			fos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void loadData()
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try
		{
			// make baseDir
			if(! this.baseDir.exists()) {
				this.baseDir.mkdir();
			}
			
			
			// read
			File file=new File(this.baseDir, "allData.dat");
			
			// make file
			if(! file.exists()) {
				file.createNewFile();
			}
			
			fis=new FileInputStream(file);
			
			// 아무것도 없지 않을때 실행 (아무것도 없는데 object 읽으면 EOFException 발생)
			if(! (fis.available() == 0)) {
				ois=new ObjectInputStream(fis);
				
				this.map=(Map<String, Object>)ois.readObject();
				
				ois.close();
			}
			
			
			fis.close();
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

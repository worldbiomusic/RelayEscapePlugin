package com.wbm.plugin.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.wbm.plugin.Main;

public class DataManager
/*
 * 여러개 클래스의 java Serializable로 Object read/write해주는 클래스 여기서 사용되는 클래스들은
 * Serializable을 구현해야 함!
 * 
 * [고민중] 각 member마다 각 파일 저장 vs 한파일에 모든 멤버 map으로 저장 [결론] 각 파일 저장: 한개의 파일이 커지면
 * IO시간이 너무 오래걸릴수 있음
 * 
 * [고민중] save할때 굳이 다시 데이터를 회수할 필요가 잇나? 객체가 ram에 등록되고 나눠줘서 DataMember에서 관리되면
 * 레퍼런스가 아직 끊기지 않으니까 그냥 데이터 저장하면 되지 않나?
 */
{
    File baseDir;

    // member 저장공간
    Map<String, DataMember> members;

    public DataManager(String baseDirPath) {
	// set baseDir
	this.baseDir = new File(baseDirPath);

	// init maps
	this.members = new HashMap<>();

    }

    public void registerMember(DataMember member) {
	this.members.put(member.getDataMemberName(), member);
//		// 바로 distribute 실행
	this.distributeData(member);
    }

    public void distributeData() {
	// distribute map data to members
	for (Entry<String, DataMember> memberEntry : this.members.entrySet()) {
	    DataMember member = memberEntry.getValue();

	    // load map data
	    Object loadedData = this.loadData(member);

	    // 없으면 null을 반환하기 때문에 줄 필요가 없음 (member에서는 null처리 안해도 됨, 확실한 데이터가 있을때만 호출되므로)
	    if (loadedData != null) {
		member.installData(loadedData);
	    }
	}
    }

    public void distributeData(DataMember member) {
	// distribute map data to member
	// load map data
	Object loadedData = this.loadData(member);

	// 없으면 null을 반환하기 때문에 줄 필요가 없음 (member에서는 null처리 안해도 됨, 확실한 데이터가 있을때만 호출되므로)
	if (loadedData != null) {
	    member.installData(loadedData);
	}
    }

    public void save() {
	// gather members data and save each file
	for (Entry<String, DataMember> memberEntry : this.members.entrySet()) {
	    DataMember member = memberEntry.getValue();
	    Object data = member.getData();

	    // save each Object data to file
	    this.saveDataToFile(member, data);
	}

    }

    private void saveDataToFile(DataMember member, Object data) {
	try {
	    // write object(map) to file
	    File file = new File(this.baseDir, member.getDataMemberName() + ".dat");

	    FileOutputStream fos = new FileOutputStream(file);
	    ObjectOutputStream oos = new ObjectOutputStream(fos);

	    oos.writeObject(data);

	    oos.close();
	    fos.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public Object loadData(DataMember member) {
	Object data = null;

	FileInputStream fis = null;
	ObjectInputStream ois = null;
	try {
	    // make baseDir
	    if (!this.baseDir.exists()) {
		this.baseDir.mkdir();
	    }

	    // read
	    File file = new File(this.baseDir, member.getDataMemberName() + ".dat");

	    // make file
	    if (!file.exists()) {
		file.createNewFile();
	    }

	    fis = new FileInputStream(file);

	    // 아무것도 없지 않을때 실행 (아무것도 없는데 object 읽으면 EOFException 발생)
	    if (fis.available() != 0) {
		ois = new ObjectInputStream(fis);

		data = ois.readObject();

		ois.close();
	    }

	    fis.close();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return data;
    }

    public void loopSavingData(int delay) {
	// save메소드를 일정 주기로 실행해서 데이터 중간중간 저장
	Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		save();
	    }
	},20 * delay , 20 * delay);
    }
}

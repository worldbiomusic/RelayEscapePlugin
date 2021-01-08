package com.wbm.plugin.util.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager
{
	// ConfigManager는 configMap에 데이터를 넣고 사용하고 끝날때 파일에 이 데이터를 저장함
	
	// config에 데이터를 저장할때 데이터형 자제를 저장 (ConfigurationSerializable 등록되어있는것만 가능)
	
	// yml파일에 각 첫번째 string으로 저장할 객체들의 이름(string), 이용할 클래스(ConfigurationMember)
	Map<String, ConfigurationMember> members;
	
	String baseDirPath;
	File file;
	FileConfiguration config;
	
	public ConfigManager(String baseDirPath) throws Exception {
		this.baseDirPath = baseDirPath;
		
		this.init();
	}
	
	public void init() throws Exception {
		// member 초기화
		this.members = new HashMap<>();
		
		
		// baseDir만들기
		File baseDir = new File(this.baseDirPath);
		if(! baseDir.exists()) {
			baseDir.mkdir();
		}
		
		// config.yml 만들기
		this.file = new File(this.baseDirPath, "config.yml");
		if(! this.file.exists() ) {
			this.file.createNewFile();
		}
		
		this.config = YamlConfiguration.loadConfiguration(this.file);
	}
	
	public void registerMember(String memberName, ConfigurationMember member) {
		this.members.put(memberName, member);
		
//		if(! this.configMap.containsKey(memberName)) {
//			this.configMap.put(memberName, );
//		}
	}
	
	public void installEachConfigData() {
		for(String memberName : this.members.keySet()) {
//			ConfigurationSection obj = this.configMap.get(memberName);
			this.members.get(memberName).installConfigData(this.config);
		}
	}
	
	public void saveEachConfigData() {
		for(String memberName : this.members.keySet()) {
			// ConfigurationMember들의 각 데이터 가져오기
			Object obj=this.members.get(memberName).getConfigData();
			this.config.set(memberName, obj);
		}
	}
	
	
	public void saveFile() {
		this.saveEachConfigData();
		
		System.out.println("this.config: " + this.config);
		System.out.println("this.file: " + this.file);
		if(this.config == null) {
			System.out.println("config null");
		}
		if(this.file == null) {
			System.out.println("file null");
		}
		
		try
		{
			this.config.save(this.file);
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
























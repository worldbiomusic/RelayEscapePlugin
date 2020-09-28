package com.wbm.plugin.util.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/*
 * 한개의 config파일에 여러 member를 정해서 데이터를 넣을 수 있음 (2중 hashmap은 getValues()를 사용하삼
 */
public class ConfigManager2
{
	// ConfigManager는 configMap에 데이터를 넣고 사용하고 끝날때 파일에 이 데이터를 저장함
	
	// config에 데이터를 저장할때 데이터형 자제를 저장 (ConfigurationSerializable 등록되어있는것만 가능)
	
	// yml파일에 각 첫번째 string으로 저장할 객체들의 이름(string), 이용할 클래스(ConfigurationMember)
	Map<String, ConfigurationMember> members;
	
	String baseDirPath;
	File file;
	FileConfiguration config;
	
	public ConfigManager2(String baseDirPath) throws Exception {
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
		
		// load config
		this.config = YamlConfiguration.loadConfiguration(this.file);
		
	}
	
	public void registerMember(ConfigurationMember member) {
		this.members.put(member.getConfigMemberName(), member);
		
//		if(! this.configMap.containsKey(memberName)) {
//			this.configMap.put(memberName, );
//		}
	}
	
	public void distributeEachConfigData() {
		for(String memberName : this.members.keySet()) {
//			ConfigurationSection obj = this.configMap.get(memberName);
			this.members.get(memberName).installConfigData(this.config);
//			this.config.get("playerData");
		}
	}
	
	public void saveEachConfigData() {
		for(String memberName : this.members.keySet()) {
			// ConfigurationMember들의 각 데이터 가져오기
			Object obj = this.members.get(memberName).getConfigData();
			
			this.config.set(memberName, obj);
			
			
//			Map<String, Object> tmp = this.config.getConfigurationSection("player").getValues(false);
//			for(String str : tmp.keySet()) {
//				BroadcastTool.printConsleMessage("get values (key): " + ChatColor.RED + str);
//				BroadcastTool.printConsleMessage("get values (values): " + ChatColor.RED + tmp.get(str));
//			}
		}
	}
	
	
	public void saveFile() throws Exception {
		this.saveEachConfigData();
		this.config.save(this.file);
	}
}
























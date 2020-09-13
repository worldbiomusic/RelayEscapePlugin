package com.wbm.plugin.util.config;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.bukkit.configuration.ConfigurationSection;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.configuration.file.YamlConfiguration;
//
public class ConfigManager2
{
//	// ConfigManager는 configMap에 데이터를 넣고 사용하고 끝날때 파일에 이 데이터를 저장함
//	
//	// config에 데이터를 저장할때 데이터형 자제를 저장 (ConfigurationSerializable 등록되어있는것만 가능)
//	
//	// yml파일의 각 첫번째 string과 value 데이터
//	Map<String, ConfigurationSection> configMap;
//	
//	// yml파일에 각 첫번째 string으로 저장할 객체들의 이름(string), 이용할 클래스(ConfigurationMember)
//	Map<String, ConfigurationMember> members;
//	
//	String baseDirPath;
//	File file;
//	FileConfiguration config;
//	
//	public ConfigManager2(String baseDirPath) throws Exception {
//		this.baseDirPath = baseDirPath;
//		
//		this.init();
//	}
//	
//	public void init() throws Exception {
//		// member 초기화
//		this.configMap = new HashMap<>();
//		this.members = new HashMap<>();
//		
//		
//		// baseDir만들기
//		File baseDir = new File(this.baseDirPath);
//		if(! baseDir.exists()) {
//			baseDir.mkdir();
//		}
//		
//		// config.yml 만들기
//		this.file = new File(this.baseDirPath, "config.yml");
//		if(! this.file.exists() ) {
//			this.file.createNewFile();
//		}
//		
//		this.config = YamlConfiguration.loadConfiguration(this.file);
//		
//		// this.configMap에 데이터 로드하기
//		for(String key: this.config.getKeys(false)) {
//			ConfigurationSection obj = this.config.getConfigurationSection(key);
//			this.configMap.put(key, obj);
//		}
//	}
//	
//	public void registerMember(String memberName, ConfigurationMember member) {
//		this.members.put(memberName, member);
//		
////		if(! this.configMap.containsKey(memberName)) {
////			this.configMap.put(memberName, );
////		}
//	}
//	
//	public void installEachConfigData() {
//		for(String memberName : this.members.keySet()) {
////			ConfigurationSection obj = this.configMap.get(memberName);
//			this.members.get(memberName).installConfigData(this.config);
//			this.config.getConfigurationSection("playerData").getValues(arg0)
//		}
//	}
//	
//	public void saveEachConfigData() {
//		for(String memberName : this.members.keySet()) {
//			// ConfigurationMember들의 각 데이터 가져오기
//			ConfigurationSection obj = this.members.get(memberName).getConfigData();
//			this.configMap.put(memberName, obj);
//			this.config.set(memberName, obj);
//		}
//	}
//	
//	
//	public void saveFile() throws Exception {
//		this.saveEachConfigData();
//		
//		System.out.println("this.config: " + this.config);
//		System.out.println("this.file: " + this.file);
//		
//		this.config.save(this.file);
//	}
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

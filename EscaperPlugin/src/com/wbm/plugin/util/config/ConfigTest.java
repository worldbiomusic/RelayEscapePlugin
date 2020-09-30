package com.wbm.plugin.util.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.general.BroadcastTool;

@SuppressWarnings("unchecked")
public class ConfigTest
{
	Map<String, Object> map;
	Map<Integer, String> map2;
	Map<Integer, String> map3;

	File file;
	FileConfiguration config;
	String baseDirPath;

	public ConfigTest(String baseDirPath)
	{
		this.baseDirPath=baseDirPath;

		// map
		this.map=new HashMap<>();

		// file
		this.file=new File(this.baseDirPath, "test.yml");
		this.config=YamlConfiguration.loadConfiguration(this.file);

		
		
//		this.save();
		
		this.printContent();
	}

	void testMapPut()
	{
		// 2
		this.map2=new HashMap<>();
		this.map2.put(4, "four");
		this.map2.put(5, "five");
		this.map2.put(6, "six");

		// 3
		this.map3=new HashMap<>();
		this.map3.put(7, "seven");
		this.map3.put(8, "eight");
		this.map3.put(9, "nine");

		// map put
		this.map.put("2", this.map2);
		this.map.put("3", this.map3);
	}

	void testPrintMap()
	{
		Map<String, Object> load=(Map<String, Object>)this.config.get("map1");
		Map<Integer, String> get1=(Map<Integer, String>)load.get("2");
		Map<Integer, String> get2=(Map<Integer, String>)load.get("3");

		BroadcastTool.printConsoleMessage("configTest: "+ChatColor.RED+get1.get(5));
		BroadcastTool.printConsoleMessage("configTest: "+ChatColor.RED+get2.get(9));

	}

	public void save()
	{
		try
		{
			for(String key : this.map.keySet()) {
				this.config.set(key, this.map.get(key));
			}
			
			// 이것만 넣었을때 에러가 안남 -> 위의 key, value쌍에서 value(Object형)값이 뭔가 
			// (정답)잘못된것이 들어가면 에러가 발생하는것 (ConfigurationSerializable이 제대로 안되서 발생한 문제!) 
//			this.config.set("1", 123);
			
			// NulPointerError가 났는데 위에 stack이 더 있으므로 내부에서 에러남!! (이유: 내가 잘못넣어서 발생 or api버그)
			this.config.save(this.file);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void saveData(Object data)
	{
		this.map.put("players", (Map<String, Object>)data);
	}
	
	public void printContent() {
//		try
//		{
//			this.config.load(this.file);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		if(this.config.contains("players")) {
			Map<String, Object> players = (Map<String, Object>)this.config.get("players");
			for(String key : players.keySet()) {
				PlayerData pData = (PlayerData)players.get(key);
				BroadcastTool.printConsoleMessage("configTest: "+ChatColor.RED+ pData.getName());
			}
		}
			
	}
}

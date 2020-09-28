package com.wbm.plugin.util.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.wbm.plugin.util.general.BroadcastTool;

/*
 * 각 member마다 각각 file을 사용함 (2중 해쉬 사용 금지)
 */
public class ConfigManager3
{
	// ConfigManager는 configMap에 데이터를 넣고 사용하고 끝날때 파일에 이 데이터를 저장함

	// config에 데이터를 저장할때 데이터형 자제를 저장 (ConfigurationSerializable 등록되어있는것만 가능)

	// yml파일에 각 첫번째 string으로 저장할 객체들의 이름(string), 이용할 클래스(ConfigurationMember)
	Map<String, ConfigurationMember> members;

	String baseDirPath;

	Map<String, File> files;

	Map<String, FileConfiguration> configs;

	public ConfigManager3(String baseDirPath) throws Exception
	{
		this.baseDirPath=baseDirPath;

		this.init();
	}

	public void init() throws Exception
	{
		// member 초기화
		this.members=new HashMap<>();

		// baseDir만들기
		File baseDir=new File(this.baseDirPath);
		if(!baseDir.exists())
		{
			baseDir.mkdir();
		}

		// files
		this.files=new HashMap<>();

		// configs
		this.configs=new HashMap<>();
	}

	public void registerMember(ConfigurationMember member)
	{
		BroadcastTool.printConsleMessage("key: "+ChatColor.RED+"registerMember");
		this.members.put(member.getConfigMemberName(), member);

		File f=new File(this.baseDirPath, member.getConfigMemberName()+".yml");
		BroadcastTool.printConsleMessage("file size: "+ChatColor.RED+ f.length());

		// make file
		if(!f.exists())
		{
			try
			{
				BroadcastTool.printConsleMessage("key: "+ChatColor.RED+"make new file!!!!!!!!!");
				f.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		// add files
		this.files.put(member.getConfigMemberName(), f);

		// add configs
		FileConfiguration config=YamlConfiguration.loadConfiguration(f);
		this.configs.put(member.getConfigMemberName(), config);

		// ex print
		Set<String> keys=config.getKeys(false);

		for(String uuid : keys)
		{
			BroadcastTool.printConsleMessage("key: "+ChatColor.RED+ uuid);
		}
	}

	public void distributeEachConfigData()
	{
		for(String memberName : this.members.keySet())
		{
			this.members.get(memberName).installConfigData(this.configs.get(memberName));
		}
	}

	public void saveEachConfigData()
	{
		for(String memberName : this.members.keySet())
		{
			// ConfigurationMember들의 각 데이터 가져오기
			Object obj=this.members.get(memberName).getConfigData();
			@SuppressWarnings("unchecked")
			Map<String, Object> map=(Map<String, Object>)obj;

			for(String key : map.keySet())
			{
				this.configs.get(memberName).set(key, map.get(key));
			}
		}
	}

	public void saveFile() throws Exception
	{
		this.saveEachConfigData();

		for(String member : this.members.keySet())
		{
			this.configs.get(member).save(this.files.get(member));
		}
	}
}

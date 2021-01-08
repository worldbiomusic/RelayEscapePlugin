package com.wbm.plugin.util.config;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigurationMember
{
	public void installConfigData(FileConfiguration obj);
	
	// Object key, value 형태로 넘길때!!  
	// key는 무조건 String지원해야 함
	// value는 primitive타입아니면 무조건 ConfigurationSerializable지원해야 함
	public Object getConfigData();
	
	public String getConfigMemberName();
}

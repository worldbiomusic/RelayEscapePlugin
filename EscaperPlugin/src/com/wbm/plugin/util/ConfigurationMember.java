package com.wbm.plugin.util;

import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigurationMember
{
	public void installConfigData(FileConfiguration obj);
	public Map<String, Object> getConfigData();
}

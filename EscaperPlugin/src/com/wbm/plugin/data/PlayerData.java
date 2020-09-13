package com.wbm.plugin.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.wbm.plugin.util.enums.Role;

public class PlayerData implements ConfigurationSerializable
{
	
	transient UUID uuid;
	String name;
	Role role;
	
	public PlayerData(UUID uuid, String name, Role role) {
		this.uuid = uuid;
		this.name = name;
		this.role = role;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid=uuid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role=role;
	}
	
	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> serialData = new HashMap<>();
		serialData.put("uuid", this.uuid.toString());
		serialData.put("name", this.name);
		serialData.put("role", this.role.name());
		
		
		return null;
	}
	
	
}

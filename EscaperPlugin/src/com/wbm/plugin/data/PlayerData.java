package com.wbm.plugin.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.wbm.plugin.util.enums.Role;

public class PlayerData implements Serializable
{
	private static final long serialVersionUID=1L;
	UUID uuid;
	String name;
	transient Role role;
	int token;
	
	public PlayerData(UUID uuid, String name, Role role, int token) {
		this.uuid = uuid;
		this.name = name;
		this.role = role;
		this.token = token;
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
	
	public int getToken()
	{
		return token;
	}

	public void setToken(int token)
	{
		this.token=token;
	}

	@Override
	public String toString()
	{
		return "PlayerData [uuid="+uuid+"\nname="+name+"\nrole="+role+"\ntoken="+token+"]";
	}

	
	// ConfigurationSerializable 용
//	@Override
//	public Map<String, Object> serialize()
//	{
//		Map<String, Object> serialData = new HashMap<>();
//		serialData.put("name", this.name);
//		serialData.put("token", this.token);
//		
//		
//		return serialData;
//	}
}

package com.wbm.plugin.data;

import java.util.UUID;

import com.wbm.plugin.util.Role;

public class PlayerData
{
	UUID uuid;
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
	
	public void changeRole()
	{
		if(this.role == Role.CHALLENGER)
			this.role = Role.MAKER;
		else
			this.role = Role.CHALLENGER;
	}
	
	
}

package com.wbm.plugin.data;

import java.io.Serializable;
import java.util.UUID;

import com.wbm.plugin.util.enums.Role;

public class PlayerData implements Serializable
{
	private static final long serialVersionUID=1L;
	UUID uuid;
	String name;
	transient Role role;
	int token;
	
	int challengingCount;
	int clearCount;
	int voted;
	
	public PlayerData(UUID uuid, String name, Role role) {
		this(uuid, name, role, 0, 0, 0, 0);
	}
	
	public PlayerData(
			UUID uuid, String name, Role role, int token
			, int challengingCount, int clearCount, int voted ) {
		this.uuid = uuid;
		this.name = name;
		this.role = role;
		this.token = 0;
		this.challengingCount = 0;
		this.clearCount = 0;
		this.voted = 0;
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
	
	public void addToken(int token) {
		this.token += token;
	}
	
	public void subToken(int token) {
		this.token -= token;
	}

	public int getChallengingCount()
	{
		return challengingCount;
	}

	public void setChallengingCount(int challengingCount)
	{
		this.challengingCount=challengingCount;
	}
	
	public void addChallengingCount(int challengingCount) {
		this.challengingCount += challengingCount;
	}
	
	public void subChallengingCount(int challengingCount) {
		this.challengingCount -= challengingCount;
	}

	public int getClearCount()
	{
		return clearCount;
	}

	public void setClearCount(int clearCount)
	{
		this.clearCount=clearCount;
	}
	
	public void addClearCount(int clearCount) {
		this.clearCount += clearCount;
	}
	
	public void subClearCount(int clearCount) {
		this.clearCount -= clearCount;
	}

	public int getVoted()
	{
		return voted;
	}

	public void setVoted(int voted)
	{
		this.voted=voted;
	}
	
	public void addVoted(int voted) {
		this.voted += voted;
	}
	
	public void subVoted(int voted) {
		this.voted -= voted;
	}

	@Override
	public String toString()
	{
		return "PlayerData [\nuuid="+uuid+", \nname="+name+", \ntoken="+token+", \nchallengingCount="+challengingCount
				+", \nclearCount="+clearCount+", \nvoted="+voted+"]";
	}
	
	
	

}

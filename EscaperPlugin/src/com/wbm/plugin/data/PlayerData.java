package com.wbm.plugin.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.enums.GamePermission;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.shop.ShopGoods;

public class PlayerData implements Serializable
{
	/*
	 * 속성 추가할때 고칠것
	 * 1.get, set method
	 * 2.toString()에 추가
	 * 3.constructor 관리
	 * 4.data저장하는것이면 data부분도 관리
	 */
	private static final long serialVersionUID=1L;
	UUID uuid;
	String name;
	transient Role role;
	int token;
	
	int challengingCount;
	int clearCount;
	int voted;
	
	List<ShopGoods> goods;
	
	transient GamePermission gamePermission;
	
	public PlayerData(UUID uuid, String name, Role role) {
		this(uuid, name, role, 0, 0, 0, 0);
	}
	
	public PlayerData(
			UUID uuid, String name, Role role, int token
			, int challengingCount, int clearCount, int voted ) {
		this.uuid = uuid;
		this.name = name;
		this.role = role;
		this.token = token;
		this.challengingCount = challengingCount;
		this.clearCount = clearCount;
		this.voted = voted;
		
		this.goods = new ArrayList<>();
		
		this.gamePermission = null;
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
	    /*
	     * Role과 GameMode는 동시에 이루어져야 하기 때문에 밑에 Role에 따라
	     * Game모드 설정을 따로 추가함
	     */
		this.role=role;
		this.setPlayerGameModeWithRole();
	}

	public void setPlayerGameModeWithRole()
	{
		GameMode mode=GameMode.SURVIVAL;

		if(role==Role.MAKER)
		{
			mode=GameMode.CREATIVE;
		}
		else if(role==Role.CHALLENGER)
		{
			mode=GameMode.SURVIVAL;
		}
		else if(role==Role.TESTER)
		{
			mode=GameMode.SURVIVAL;
		}
		else if(role==Role.VIEWER)
		{
			mode=GameMode.CREATIVE;
		}
		else if(role==Role.WAITER)
		{
			mode=GameMode.SURVIVAL;
		}

		Player p=Bukkit.getPlayer(uuid);
		p.setGameMode(mode);
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


	public List<ShopGoods> getGoods()
	{
		return goods;
	}

	public void setGoods(List<ShopGoods> makingGoods)
	{
		this.goods=makingGoods;
	}
	
	public void addGoods(ShopGoods goods) {
		this.goods.add(goods);
	}

	public boolean doesHaveGoods(ShopGoods goods) {
		return this.goods.contains(goods);
	}
	
	public GamePermission getGamePermission()
	{
		return gamePermission;
	}

	public void setGamePermission(GamePermission gamePermission)
	{
		this.gamePermission=gamePermission;
	}

	@Override
	public String toString()
	{
		return "PlayerData " + 
				", \nuuid: "+this.uuid+
				", \nname: "+this.name+
				", \nrole: "+this.role+
				", \ntoken: "+this.token+
				", \nchallengingCount: "+this.challengingCount +
				", \nclearCount: "+this.clearCount+
				", \nvoted: "+this.voted + 
				", \ngoods: " + this.goods + 
				", \ngamePermission=: " + this.gamePermission;
	}
	
	
	

}

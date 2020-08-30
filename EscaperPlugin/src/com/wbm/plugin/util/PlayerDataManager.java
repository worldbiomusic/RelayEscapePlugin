package com.wbm.plugin.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.wbm.plugin.data.PlayerData;

public class PlayerDataManager
{
	Map<UUID, PlayerData> playerList;
	
	public PlayerDataManager() {
		this.playerList = new HashMap<UUID, PlayerData>();
	}
	
	public void addPlayerData(PlayerData pData) {
		this.playerList.put(pData.getUUID(), pData);
	}
	
	public void deletePlayerData(UUID uuid) {
		if(this.playerList.containsKey(uuid)) {
			this.playerList.remove(uuid);
		}
	}
	
	public PlayerData getPlayerData(UUID uuid) {
		if(this.playerList.containsKey(uuid)) {
			return this.playerList.get(uuid);
		}
		
		return null;
	}
	
	public PlayerData getMaker() {
		Collection<PlayerData> set = this.playerList.values();
		Iterator<PlayerData> it = set.iterator();
		
		while(it.hasNext()) {
			PlayerData pData = it.next();
			if(pData.getRole() == Role.MAKER) {
				return pData;
			}
		}
		
		return null;
	}
}

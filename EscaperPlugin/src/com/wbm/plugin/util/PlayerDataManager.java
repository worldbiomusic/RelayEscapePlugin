package com.wbm.plugin.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.config.ConfigurationMember;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.BroadcastTool;

public class PlayerDataManager implements ConfigurationMember
{
	// online Player만 가지고 있는 데이터
	Map<UUID, PlayerData> onlinePlayerList;
	
	// server 켜질때 config데이터 담긴 데이터
	// player quit할때 onlinePlayerList에서 데이터를 여기로 저장해주는 데이터 보관소 
	// (서버 꺼질때 config파일에 데이터 넘겨서 저장)
	Map<UUID, PlayerData> allPlayerSavigData;
	
	// 실제적인 Role에 상관없이 현재 Time의 제작자를 가리키는 변수
	// Waiting, Making, Testing Time에서는 무조건 있고, 
	// ChallengingTime에는 있을수도(Challenging일때 나간 경우) 없을수도 있음(Challenge아니었을때 나가서 null인 경우)
	Player maker;
	
	public PlayerDataManager() {
		this.onlinePlayerList = new HashMap<UUID, PlayerData>();
		this.allPlayerSavigData = new HashMap<UUID, PlayerData>();
	}
	
	public void addPlayerData(PlayerData pData) {
		this.onlinePlayerList.put(pData.getUUID(), pData);
	}
	
	public void saveAndRemovePlayerData(UUID uuid) {
		// server quit할때 사용
		if(this.onlinePlayerList.containsKey(uuid)) {
			// this.allPlayerSavigData에 데이터 저장
			PlayerData pData = this.onlinePlayerList.get(uuid);
			this.allPlayerSavigData.put(uuid, pData);
			
			// this.onlinePlayerList에서 데이터 삭제
			this.onlinePlayerList.remove(uuid);
		}
	}
	
	public PlayerData getPlayerData(UUID uuid) {
		return this.onlinePlayerList.get(uuid);
	}
	
	public boolean isFirstJoin(UUID uuid) {
		return this.allPlayerSavigData.containsKey(uuid);
	}
	
	public Player getMaker() {
		return this.maker;
	}
	
	public void registerMaker(Player p) {
		this.maker = p;
	}
	
	public void unregisterMaker() {
		this.maker = null;
	}
	
	public boolean makerExists() {
		return (this.maker == null) ? false : true;
	}
	
	public boolean isMaker(Player p) {
		return ( p.getUniqueId().equals(this.maker.getUniqueId()) );
	}
	
	public void printAllPlayer() {
		for(PlayerData pData : this.onlinePlayerList.values()) {
			Bukkit.getServer().broadcastMessage("player: " + pData.getName());
			Bukkit.getServer().broadcastMessage("role: " + pData.getRole());
			Bukkit.getServer().broadcastMessage("-----------------------------");
		}
	}
	
	public void changePlayerRole(UUID uuid, Role role) {
		// gamemode, role바꾸기
		PlayerData pData = this.getPlayerData(uuid);
		
		if(pData == null) {
			BroadcastTool.printConsleMessage("[Bug]: changePlayerRole()- no player in onlinePlayerList");
			return;
		}
		
		pData.setRole(role);

		this.setPlayerGameModeWithRole(uuid);
	}
	
	public void setPlayerGameModeWithRole(UUID uuid) {
		PlayerData pData = this.getPlayerData(uuid);
		if(pData == null) {
			return;
		}
		
		Role role = pData.getRole();
		
		GameMode mode = GameMode.SURVIVAL;
		
		if(role == Role.MAKER) {
			mode = GameMode.CREATIVE;
		} else if(role == Role.CHALLENGER) {
			mode = GameMode.SURVIVAL;
		} else if(role == Role.TESTER) {
			mode = GameMode.SURVIVAL;
		} else if(role == Role.VIEWER) {
			mode = GameMode.SPECTATOR;
		} else if(role == Role.WAITER) {
			mode = GameMode.SURVIVAL;
		}
		
		
		Player p = Bukkit.getPlayer(uuid);
		p.setGameMode(mode);
	}

	@Override
	public void installConfigData(FileConfiguration config)
	// UUID를 String으로 저장해서(String으로만 일을 수 있어서) String을 UUID로 변환해서 데이터를 가져옴
	{
		// sectionSize == 0 조건 추가한 이유:
		// playerData: {}이렇게 있는데 {}가 null이 아니라서 여기로 조건이 분기되는데
		// 아무것도 없는상태라서 Map으로 Casting이 안되서 오류나서 조건 한번더 분기해줌
		
//		ConfigurationSection section = (ConfigurationSection)obj;
//		int sectionSize = section.getKeys(false).size();
//		if(sectionSize == 0) {
//			this.playerList = new HashMap<UUID, PlayerData>();
//		} else {
		
		boolean first = !config.contains("player");
		
		Map<String, Object> tmp;
		if(first) {
			config
			.createSection("player")
			.createSection("playerData");
		}
		
			tmp = (Map<String, Object>)config
					.getConfigurationSection("player")
					.getConfigurationSection("playerData").getValues(false);
			
			// TODO: foreach loop: tmp의 Entry로 간단하게 바꾸기
			for(String uuid : tmp.keySet()) {
				this.onlinePlayerList.put(UUID.fromString(uuid), (PlayerData)tmp.get(uuid));
			}
//		}
	}

	@Override
	public Map<String, Object> getConfigData()
	// UUID는 String형태로 저장해야지 읽어올 수 있기 때문에  Map<String, PlayerData>로 변환해서 저장
	{
		// TODO Auto-generated method stub
		Map<String, PlayerData> tmp = new HashMap<>();
		for(UUID uuid: this.onlinePlayerList.keySet()) {
			tmp.put(uuid.toString(), this.onlinePlayerList.get(uuid));
		}
		
		Map<String, Object> map=new HashMap<>();
		map.put("playerData", tmp);
		
		return map;
	}
}

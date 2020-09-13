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

public class PlayerDataManager implements ConfigurationMember
{
	Map<UUID, PlayerData> playerList;
	
	// 실제적인 Role에 상관없이 한 판의 제작자를 가리키는 변수
	Player maker;
	
	public PlayerDataManager() {
		this.playerList = new HashMap<UUID, PlayerData>();
	}
	
	public void addPlayerData(PlayerData pData) {
		this.playerList.put(pData.getUUID(), pData);
	}
	
//	public void deletePlayerData(UUID uuid) {
//		if(this.playerList.containsKey(uuid)) {
//			this.playerList.remove(uuid);
//		}
//	}
	
	public PlayerData getPlayerData(UUID uuid) {
		if(this.playerList.containsKey(uuid)) {
			return this.playerList.get(uuid);
		}
		
		return null;
	}
	
	// TODO: 메소드 이름 getMakerPlayerData -> getMaeker로 바꾸기 
	public Player getMaker() {
		return this.maker;
	}
	
	public void registerMaker(Player p) {
		this.maker = p;
	}
	
	public void printAllPlayer() {
		for(PlayerData pData : this.playerList.values()) {
			Bukkit.getServer().broadcastMessage("player: " + pData.getName());
			Bukkit.getServer().broadcastMessage("role: " + pData.getRole());
			Bukkit.getServer().broadcastMessage("-----------------------------");
		}
	}
	
	public void changePlayerRole(UUID uuid, Role role) {
		// gamemode, role바꾸기
		PlayerData pData = this.getPlayerData(uuid);
		pData.setRole(role);

		this.setPlayerGameModeWithRole(uuid);
	}
	
	public void setPlayerGameModeWithRole(UUID uuid) {
		PlayerData pData = this.getPlayerData(uuid);
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
				this.playerList.put(UUID.fromString(uuid), (PlayerData)tmp.get(uuid));
			}
//		}
	}

	@Override
	public Map<String, Object> getConfigData()
	// UUID는 String형태로 저장해야지 읽어올 수 있기 때문에  Map<String, PlayerData>로 변환해서 저장
	{
		// TODO Auto-generated method stub
		Map<String, PlayerData> tmp = new HashMap<>();
		for(UUID uuid: this.playerList.keySet()) {
			tmp.put(uuid.toString(), this.playerList.get(uuid));
		}
		
		Map<String, Object> map=new HashMap<>();
		map.put("playerData", tmp);
		
		return map;
	}
}

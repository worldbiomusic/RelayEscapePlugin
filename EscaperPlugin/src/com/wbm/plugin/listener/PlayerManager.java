package com.wbm.plugin.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.enums.Role;

public class PlayerManager implements Listener 
{
	// 되도록 player UUID 로 정보 처리하기 (편함)
	
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	RelayManager relayManager;
	
	public PlayerManager(
			PlayerDataManager pDataManager, 
			RoomManager roomManager,
			RelayManager relayManager) 
	{
		this.pDataManager = pDataManager;
		this.roomManager = roomManager;
		this.relayManager = relayManager;
		
		this.init();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.sendMessage("hello challenger");

		
		// PlayerData 처리
		this.processPlayerData(p);
	}
	
	void init() {
		// PlayerDataManager의 maker초기화
		this.pDataManager.registerMaker(null);
		
		// 서버 리로드하면 서버에 남아있는 플레이어들 다시 등록
		this.reRegisterAllPlayer();
	}
	
	void processPlayerData(Player p) {
		// data 처리
		UUID uuid = p.getUniqueId();
		
		// PlayerDataManager에 데이터 없는지 확인 (=서버 켜고 처음 들어옴)
		PlayerData pData;
		if((pData = this.pDataManager.getPlayerData(uuid)) == null) {
			String name = p.getName();
			Role role = Role.CHALLENGER;
			pData = new PlayerData(uuid, name, role);
			this.pDataManager.addPlayerData(pData);
		}
		
		
		// gamemode 처리
		this.pDataManager.setPlayerGameModeWithRole(uuid);
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
//		Player p = e.getPlayer();
//		
//		UUID pUuid = p.getUniqueId();
//		Player maker = this.pDataManager.getMakerPlayerData();
//		if(maker.isOnline()) {
//			return;
//		}
//		UUID makerUuid = maker.getUniqueId();
//		
//		// Maker가 제작중에 서버를 나갔을시 (제작후에는 상관없음) 
//		// TODO: 나중에 시간시스템 넣으면 제작자가 제작중에 나갔을때만 조건 추가해야 함
//		if(pUuid == makerUuid) {
//			Bukkit.getServer().broadcastMessage("Maker quit server!");
//			Bukkit.getServer().broadcastMessage("Main room will be changed to base room");
//			
//			this.roomManager.setBaseMainRoom();
//		}
//		
//		// pDataManager 삭제하지말고 HashMap에 데이터 저장하고 있다가, 서버 끝날떄 파일로 저장하기 (또는 시간주기) 
////		this.pDataManager.deletePlayerData(uuid);
//		
//		// 나간 player Role.CHALLENGER로 바꾸기
//		PlayerData pData = this.pDataManager.getPlayerData(pUuid);
//		pData.setRole(Role.CHALLENGER);
	}
	
	public void reRegisterAllPlayer() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			this.processPlayerData(p);
		}
	}
}

























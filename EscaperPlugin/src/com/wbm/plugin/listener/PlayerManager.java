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
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.BroadcastTool;

// TODO: PlayerManager를 GameManager로 통합하기 (기능 중복)


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
		
		// 모든 player는 무조건 Challenger이고,  각 Time에 맞는 Challenger의 Role로 역할이 배정됨! (w, m, t = Waiter, c = Challenger)
		// (Challeging때 나간 Maker가 다시 들어온 경우 제외)
		PlayerData pData;
		
		Role baseRole = Role.WAITER;
		RelayTime time = this.relayManager.getCurrentTime();
//		if(time == RelayTime.WAITING
//				|| time == RelayTime.MAKING
//				|| time == RelayTime.TESTING) {
//			role = Role.WAITER;
//		} else 
		
		
		if(time == RelayTime.CHALLENGING) {
			baseRole = Role.CHALLENGER;
		}
		
		
		
		
		// TODO: config연동됬을때 활성화 시킬 코드
//		// PlayerDataManager에 데이터 없는지 확인 (= 서버 처음 들어옴)
//		if(this.pDataManager.isFirstJoin(uuid)) {
//			String name = p.getName();
//			pData = new PlayerData(uuid, name, baseRole);
//		} 
//		// 전에 들어왔음 (바꿀것은 Role밖에 없음)
//		else {
//			pData = this.pDataManager.getPlayerData(uuid);
//			
//			// maker가 남아있는경우는 Maker가 ChallengingTime일떄 나간경우임!
//			// -> role을 유지해서 viewer로 겜모를바꿔서 자신이 만든룸을 clear못하게 만들어야 함
//			if(this.pDataManager.makerExists()) {
//				Player maker = this.pDataManager.getMaker();
//				// 들어온사람이 전에 나간 Maker였을때
//				if(uuid.equals(maker.getUniqueId())) {
//					p.sendMessage("you are Viewer in your room(structure)");
//					baseRole = Role.VIEWER;
//				}
//			}
//			
//			// role변경
//			pData.setRole(baseRole);
//		}
		// TODO: config연동됬을때 활성화 시킬 코드
		
		
		
		
		
		
		// TODO: config연동 안했을때 사용코드
		if(time == RelayTime.CHALLENGING) {
			// maker가 남아있는경우는 Maker가 ChallengingTime일떄 나간경우임!
			// -> role을 유지해서 viewer로 겜모를바꿔서 자신이 만든룸을 clear못하게 만들어야 함
			if(this.pDataManager.makerExists()) {
				// 들어온사람이 전에 나간 Maker였을때
				Player maker = this.pDataManager.getMaker();
				if(uuid.equals(maker.getUniqueId())) {
					p.sendMessage("you are Viewer in your room(structure)");
					// 바꿀 목표 데이터
					baseRole = Role.VIEWER;
				}
			}
		}
		// TODO: config연동 안했을때 사용코드
		
		
		
		
		// 최종 PlayerData객체 만들기
		String name = p.getName();
		pData = new PlayerData(uuid, name, baseRole);
		
		
		
		// playerDataManager에 데이터 add
		this.pDataManager.addPlayerData(pData);
		// gamemode 처리
		this.pDataManager.setPlayerGameModeWithRole(uuid);
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		Player maker = this.pDataManager.getMaker();
		if(maker == null) {
			return;
		}
		
		// Maker가 나갔을 때
		if(p.equals(maker)) {
			RelayTime time = this.relayManager.getCurrentTime();
			
			// Time = WAITING, MAKING, TESTING일떄
			if(time == RelayTime.WAITING
					|| time == RelayTime.MAKING
					|| time == RelayTime.TESTING) {
				// msg보내기
				BroadcastTool.sendMessageToEveryone("Maker quit server");
				
				// Room 초기화
				this.roomManager.setBaseMainRoom();
				
				// PlayerDataManager maker = null 처리
				this.pDataManager.registerMaker(null);
				
				// RelayTime set to CHALLENGING
				this.relayManager.startAnotherTime(RelayTime.CHALLENGING);
			}
			
			
			// Time = CHALLENGING 일때
			// -> 재접해서 다시 클리어 방지!
			else if(time == RelayTime.CHALLENGING) {
				// PlayerDataManager maker = null 처리하지 않고, 다시 들어올때 maker에 있는 player로 maker판별!
				// -> 수행할 동작이 없음
			}
		}
		
		// PlayerDataManager 처리
		this.pDataManager.saveAndRemovePlayerData(p.getUniqueId());
	}
	
	public void reRegisterAllPlayer() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			this.processPlayerData(p);
		}
	}
}

























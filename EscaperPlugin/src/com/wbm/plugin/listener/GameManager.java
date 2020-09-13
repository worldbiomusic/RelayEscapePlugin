package com.wbm.plugin.listener;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.RelayManager;
import com.wbm.plugin.util.RolePermission;
import com.wbm.plugin.util.RoomManager;
import com.wbm.plugin.util.enums.Role;

public class GameManager implements Listener 
{
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	RelayManager relayManager;
	
	
	public GameManager(
			PlayerDataManager pDataManager,
			RoomManager roomManager,
			RelayManager relayManager) 
	{
		this.pDataManager = pDataManager;
		this.roomManager = roomManager;
		this.relayManager = relayManager;
		
		// main room base로 설정
		this.roomManager.setBaseMainRoom();
	}
	
	
	
	@EventHandler
	public void onPlayerBreakCore(BlockBreakEvent e) {
		// Tester, Challenger의 core부수는 상황
		
		Block block = e.getBlock();
		Material mat = block.getType();
		
		Player p = e.getPlayer();
		UUID pUuid = p.getUniqueId();
		PlayerData pData = this.pDataManager.getPlayerData(pUuid);
		Role role = pData.getRole();
		
		

		// core체크
		if(mat.equals(Material.GLOWSTONE)) {
		// Role별로 권한 체크
			// Time: Challenging / Role: Challenger
			if(role == Role.CHALLENGER) {
				
				// 1. 현재 Maker에게 이제 Challenger라는 메세지 전송
				Player maker = this.pDataManager.getMaker();
				if(maker != null && maker.isOnline()) {
					maker.sendMessage("you are now Challenger");
				}
				
				// 2. 클리어한  maker는 pDataManager의 maker로 등록
				this.pDataManager.registerMaker(p);
				
				
				// 3. main room 초기화
				this.roomManager.setEmptyMainRoom();
				
				// 4. block 파괴
				block.setType(Material.AIR);
				
				// 5.next relay 시작
				this.relayManager.stopCurrentTaskAndStartNextTime();
			
			} 
			// Time: Testing / Role: Tester 
			else if(role == Role.TESTER) {
				// 1.next relay 시작
				this.relayManager.stopCurrentTaskAndStartNextTime();
				// 2.이벤트 취소
				e.setCancelled(true);
			}
		}
		
		
	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		PlayerData pData = this.pDataManager.getPlayerData(uuid);
		Role role = pData.getRole();
		
		boolean permission = false;
		
		// Role별로 권한 체크
		if(role == Role.MAKER) {
			permission = RolePermission.MAKER_BREAKBLOCK;
		} else if(role == Role.CHALLENGER) {
			permission = RolePermission.CHALLENGER_BREAKBLOCK;
		} else if(role == Role.TESTER) {
			permission = RolePermission.TESTER_BREAKBLOCK;
		} else if(role == Role.VIEWER) {
			permission = RolePermission.VIEWER_BREAKBLOCK;
		} else if(role == Role.WAITER) {
			permission = RolePermission.WAITER_BREAKBLOCK;
		}
		
		e.setCancelled(!permission);
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		PlayerData pData = this.pDataManager.getPlayerData(uuid);
		Role role = pData.getRole();

		boolean permission = false;
		
		// Role별로 권한 체크
		if(role == Role.MAKER) {
			permission = RolePermission.MAKER_PLACEBLOCK;
		} else if(role == Role.CHALLENGER) {
			permission = RolePermission.CHALLENGER_PLACEBLOCK;
		} else if(role == Role.TESTER) {
			permission = RolePermission.TESTER_PLACEBLOCK;
		} else if(role == Role.VIEWER) {
			permission = RolePermission.VIEWER_PLACEBLOCK;
		} else if(role == Role.WAITER) {
			permission = RolePermission.WAITER_PLACEBLOCK;
		}
		
		e.setCancelled(!permission);
	}
}
























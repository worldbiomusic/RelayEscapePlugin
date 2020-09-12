package com.wbm.plugin.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
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
import com.wbm.plugin.util.Role;
import com.wbm.plugin.util.RolePermission;
import com.wbm.plugin.util.RoomManager;

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
		
		
		Block block = e.getBlock();
		Material mat = block.getType();
		
		Player p = e.getPlayer();
		UUID pUuid = p.getUniqueId();
		PlayerData pData = this.pDataManager.getPlayerData(pUuid);
		Role role = pData.getRole();
		
		

		// core체크
		if(mat.equals(Material.GLOWSTONE)) {
		// Role별로 권한 체크
			if(role == Role.CHALLENGER) {
				
				// 1. 현재 Maker -> Challenger로 변경
				PlayerData makerPData = this.pDataManager.getMakerPlayerData();
				if(makerPData != null) {
					UUID makerUUID = makerPData.getUUID();
					this.pDataManager.changePlayerRole(makerUUID, Role.CHALLENGER);
					
					Player maker = Bukkit.getPlayer(makerUUID);
					maker.sendMessage("you are now Challenger");
				}
				
				// 2. 클리어한 Challenger -> Maker로 변경
				this.pDataManager.changePlayerRole(pUuid, Role.MAKER);
				p.sendMessage("you are now Maker");
				
				
				// 3. main room 초기화
				this.roomManager.setEmptyMainRoom();
				
				// 4. block 파괴
				block.setType(Material.AIR);
				
				// 5.새로운 relay 시작
				this.relayManager.readyForNewRelay();
			
			} else if(role == Role.TESTER) {
				// 1.View로 역할변경
				this.pDataManager.changePlayerRole(pUuid, Role.VIEWER);
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
		}
		
		e.setCancelled(!permission);
	}
}
























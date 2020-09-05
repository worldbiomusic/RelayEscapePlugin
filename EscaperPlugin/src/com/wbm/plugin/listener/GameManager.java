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
import com.wbm.plugin.util.Role;
import com.wbm.plugin.util.RoomManager;

public class GameManager implements Listener 
{
	PlayerDataManager pDataManager;
	PlayerManager pManager;
	RoomManager roomManager;
	
	public GameManager(
			PlayerDataManager pDataManager,
			PlayerManager pManager,
			RoomManager roomManager) 
	{
		this.pDataManager = pDataManager;
		this.pManager = pManager;
		this.roomManager = roomManager;
		
		// main room base로 설정
		this.roomManager.setBaseMainRoom();
	}
	
	
	
	@EventHandler
	public void onGameEnd(BlockBreakEvent e) {
		
		
		Block block = e.getBlock();
		Material mat = block.getType();
		
		Player newMaker = e.getPlayer();
		UUID newMakerUUID = newMaker.getUniqueId();
		PlayerData newMakerPData = this.pDataManager.getPlayerData(newMakerUUID);
		Role newMakerRole = newMakerPData.getRole();
		
		// challeger가 부순지 확인
		if(newMakerRole == Role.CHALLENGER && 
				mat.equals(Material.GLOWSTONE)) {
			// 공간 초기화 (공기블럭)
			
			
			// 1. 현재 Maker -> Challenger로 변경
			// gamemode 변경
			PlayerData olderMakerPData = this.pDataManager.getMaker();
			if(olderMakerPData != null) {
				UUID olderMakerUUID = olderMakerPData.getUUID();
				this.pManager.changePlayerRole(olderMakerUUID);
				
				Player olderMaker = Bukkit.getPlayer(olderMakerUUID);
				olderMaker.sendMessage("you are now Challenger");
			}
			
			// 2. 클리어한 Challenger -> Maker로 변경
			this.pManager.changePlayerRole(newMakerUUID);
			newMaker.sendMessage("you are now Maker");
			
			
			// 3. main room 초기화
			this.roomManager.setEmptyMainRoom();
		}
	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		PlayerData pData = this.pDataManager.getPlayerData(uuid);
		Role role = pData.getRole();
		
		// challenger 금지
		if(role == Role.CHALLENGER) {
			e.setCancelled(true);
			p.sendMessage("Challegner can't break block");
		}
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		PlayerData pData = this.pDataManager.getPlayerData(uuid);
		Role role = pData.getRole();

		// challenger 금지
		if(role == Role.CHALLENGER) {
			e.setCancelled(true);
			p.sendMessage("Challegner can't place block");
		}
	}
}
























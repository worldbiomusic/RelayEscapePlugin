package com.wbm.plugin.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.Role;

public class PlayerManager implements Listener 
{
	// 되도록 player UUID 로 정보 처리하기 (편함)
	
	PlayerDataManager pDataManager;
	
	public PlayerManager(PlayerDataManager pDataManager) {
		this.pDataManager = pDataManager;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.sendMessage("hello challenger");

		// data 처리
		UUID uuid = p.getUniqueId();
		String name = p.getName();
		Role role = Role.CHALLENGER;
		PlayerData pData = new PlayerData(uuid, name, role);
		this.pDataManager.addPlayerData(pData);
		
		// gamemode 처리
		this.setPlayerGameModeWithRole(uuid);
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		UUID uuid = p.getUniqueId();
		this.pDataManager.deletePlayerData(uuid);
	}
	
	
	public void changePlayerRole(UUID uuid) {
		// gamemode, role바꾸기
		PlayerData pData = this.pDataManager.getPlayerData(uuid);
		pData.changeRole();

		this.setPlayerGameModeWithRole(uuid);
	}
	
	public void setPlayerGameModeWithRole(UUID uuid) {
		PlayerData pData = this.pDataManager.getPlayerData(uuid);
		Role role = pData.getRole();
		
		GameMode mode = (role == Role.CHALLENGER) 
				? GameMode.SURVIVAL : GameMode.CREATIVE;
		
		Player p = Bukkit.getPlayer(uuid);
		p.setGameMode(mode);
	}
	
}

























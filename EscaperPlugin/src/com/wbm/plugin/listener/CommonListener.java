package com.wbm.plugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.wbm.plugin.util.general.SpawnLocationTool;

public class CommonListener implements Listener
{

	@EventHandler
	public static void onFoodLevelChanged(FoodLevelChangeEvent e)
	{
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		e.getDrops().clear();
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{

		String msg=e.getMessage();

		String translatedMsg="";
		switch (msg)
		{
			case "1":
				translatedMsg="HI";
				break;
			case "2":
				translatedMsg="BYE";
				break;
			case "3":
				translatedMsg="FUXX";
				break;
			case "4":
				translatedMsg="FOLLOW ME";
				break;
			case "5":
				translatedMsg="VOTE";
				break;
			default:
				e.setCancelled(true);
				return;

		}

		e.setMessage(translatedMsg);
	}
	
	@EventHandler
	public void onPlayerDamanged(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			// 나중에 Maker가 Waiter기다리는것 때리게  만들 예정
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.teleport(SpawnLocationTool.joinLocation);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(SpawnLocationTool.respawnLocation);
	}
}




























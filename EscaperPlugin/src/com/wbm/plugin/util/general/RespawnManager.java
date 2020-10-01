package com.wbm.plugin.util.general;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnManager implements Listener
{
	Location joinLocation;
	Location respawnLocation;

	public RespawnManager(Location joinLocation, 
			Location respawnLocation)
	{
		this.joinLocation = joinLocation;
		this.respawnLocation = respawnLocation;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.teleport(this.joinLocation);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(this.respawnLocation);
	}
}

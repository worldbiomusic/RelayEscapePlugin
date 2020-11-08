package com.wbm.plugin.util.general;

import org.bukkit.Location;
import org.bukkit.event.Listener;

public class SpawnLocationTool implements Listener
{
	public static Location joinLocation;
	public static Location respawnLocation;
	public static Location lobby;

	public SpawnLocationTool(Location joinLocation, 
			Location respawnLocation,
			Location lobby)
	{
		SpawnLocationTool.joinLocation = joinLocation;
		SpawnLocationTool.respawnLocation = respawnLocation;
		SpawnLocationTool.lobby = lobby;
	}
}

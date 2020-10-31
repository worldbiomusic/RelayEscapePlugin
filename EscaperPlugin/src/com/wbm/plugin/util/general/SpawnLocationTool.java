package com.wbm.plugin.util.general;

import org.bukkit.Location;
import org.bukkit.event.Listener;

public class SpawnLocationTool implements Listener
{
	public static Location joinLocation;
	public static Location respawnLocation;

	public SpawnLocationTool(Location joinLocation, 
			Location respawnLocation)
	{
		SpawnLocationTool.joinLocation = joinLocation;
		SpawnLocationTool.respawnLocation = respawnLocation;
	}
}

package com.wbm.plugin.util.general;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TeleportTool
{
	static Map<String, Location> locations;
	
	public static void tp(Entity src, double x, double y, double z) {
		src.teleport(new Location(src.getWorld(), x, y, z));
	}
	
	public static void tp(Entity src, Location dst) {
		src.teleport(dst);
	}
	
	public static void tp(List<? extends Entity> entities , Location dst) {
	    for(Entity entity : entities) {
		entity.teleport(dst);
	    }
	}
	
	public static void tp(Entity src, Entity dst) {
		src.teleport(dst.getLocation());
	}
	
	public static void tp(Entity src, String locationName) {
		src.teleport(TeleportTool.locations.get(locationName));
	}
	
	public static void allTpToEntity(Entity dst) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(dst.getLocation());
		}
	}
	
	public static void allTpToLocation(Location loc) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(loc);
		}
	}

	public static void allTpToLocationWithoutThem(Location loc, List<Player> them) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(!them.contains(p)) {
				p.teleport(loc);
			}
		}
	}
	
	public static void tpAfterTick(Entity src, long tick) {
		
	}
	
	public static void randomTp(Entity src, Location loc1, Location loc2) {
		// loc1과 loc2사이에서 random tp
		
	}
	
	public static void swapTp(Entity e1, Entity e2) {
		Location e1Loc = e1.getLocation();
		e1.teleport(e2.getLocation());
		e2.teleport(e1Loc);
	}
	
	public static void tpToRandomEntity(Entity src, Entity[] dst) {
		int r = (int)(Math.random() * dst.length);
		Entity randomEntity = dst[r];
		src.teleport(randomEntity);
	}
}

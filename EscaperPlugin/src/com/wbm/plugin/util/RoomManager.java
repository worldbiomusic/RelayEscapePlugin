package com.wbm.plugin.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class RoomManager
{
	public RoomManager() {
		
	}
	
	public void setPlaceWithBlock(Location loc1, Location loc2, Material mat) {
		this.loopPlaceBlockWithBlock(loc1, loc2, mat);
	}
	
	void loopPlaceBlockWithBlock(Location loc1, Location loc2, Material mat) {
		int dx = (int)loc2.getX() - (int)loc1.getX();
		int dy = (int)loc2.getY() - (int)loc1.getY();
		int dz = (int)loc2.getZ() - (int)loc1.getZ();
		
//		Bukkit.getServer().broadcastMessage("dx: " + dx);
//		Bukkit.getServer().broadcastMessage("dz: " + dz);
		
		for(int x = 0; x <= dx; x++) {
			for(int z = 0; z <= dz; z++) {
				for(int y = 0; y <= dy; y++) {
					Location loc = loc1.clone();
					loc.add(x, y, z);
					
					loc.getBlock().setType(mat);
				}
			}
		}
		
	}
	
	
	
	public void setEmptyMainRoom() {
		World world = Bukkit.getWorld("world");
		Location loc1 = new Location(world, 1, 4, 1);
		Location loc2 = new Location(world, 10, 50, 10);
		this.setPlaceWithBlock(loc1, loc2, Material.AIR);
	}
	
	public void setBaseMainRoom() {
		// Maker뽑기위해 일단 이전맵을 돌려야 하는 메서드지만 아직 기반이없어서
		// base room으로 가운에 glowstone하나 설치해서 Maker 한사람 구하기가 목적
		this.setEmptyMainRoom();
		
		World world = Bukkit.getWorld("world");
		Location loc1 = new Location(world, 5, 4, 5);
		Location loc2 = new Location(world, 5, 4, 5);
		this.setPlaceWithBlock(loc1, loc2, Material.GLOWSTONE);
	}
}

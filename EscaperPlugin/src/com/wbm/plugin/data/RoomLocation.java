package com.wbm.plugin.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.wbm.plugin.util.enums.RoomType;
import com.wbm.plugin.util.general.LocationTool;
import com.wbm.plugin.util.general.MathTool;










public class RoomLocation
{
	// world
	private static final World world = Bukkit.getWorld("world");
	
	// main
	public static final Location mainPos1 = new Location(world, 1, 4, 1);
	public static final Location mainPos2 = new Location(world, 10, 50, 10);
	
	// practice
	public static final Location practicePos1 = new Location(world, 21, 4, 21);
	public static final Location practicePos2 = new Location(world, 30, 50, 30);
	
	public static int getRoomBlockCount(RoomType roomType) {
		Location pos1 = null, pos2 = null;
		if(roomType == RoomType.MAIN) {
			pos1 = RoomLocation.mainPos1;
			pos2 = RoomLocation.mainPos2;
		} else if(roomType == RoomType.PRACTICE) {
			pos1 = RoomLocation.practicePos1;
			pos2 = RoomLocation.practicePos2;
		}
		
		int dx = MathTool.getDiff((int)pos1.getX(), (int)pos2.getX());
		int dy = MathTool.getDiff((int)pos1.getY(), (int)pos2.getY());
		int dz = MathTool.getDiff((int)pos1.getZ(), (int)pos2.getZ());
		
		return dx * dy * dz;
	}
	
	public static RoomType getRoomTypeWithLocation(Location loc) {
		Location target = loc;
		
		if(LocationTool.isIn(mainPos1, target, mainPos2)) {
			return RoomType.MAIN;
		} else if(LocationTool.isIn(practicePos1, target, practicePos2)) {
			return RoomType.PRACTICE;
		}
		
		return null;
	}
}

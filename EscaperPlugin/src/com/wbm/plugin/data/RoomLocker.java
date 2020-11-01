package com.wbm.plugin.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class RoomLocker
{
	public static World w = Bukkit.getWorld("world");
	public static List<Location> mainLocker
	= new ArrayList<>();
	public static ItemStack mainLockerItem 
	= new ItemStack(Material.CONCRETE);
	
	static {
		mainLocker.add(new Location(w, 9, 5, 11));
		mainLocker.add(new Location(w, 10, 5, 11));
		mainLocker.add(new Location(w, 11, 5, 11));
		mainLocker.add(new Location(w, 11, 5, 10));
		mainLocker.add(new Location(w, 11, 5, 9));
	}
	
	public static ItemStack air = new ItemStack(Material.AIR);
}


























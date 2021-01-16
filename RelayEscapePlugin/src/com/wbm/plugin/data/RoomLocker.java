package com.wbm.plugin.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.Setting;
import com.wbm.plugin.util.general.ItemStackTool;

public class RoomLocker
{
	public static World w = Setting.world;
	public static List<Location> mainLocker
	= new ArrayList<>();
	public static ItemStack mainLockerItem
	= ItemStackTool.item(Material.CONCRETE, (byte)14);
	
	static {
		mainLocker.add(Setting.getLoationFromSTDLOC(9, 5, 11));
		mainLocker.add(Setting.getLoationFromSTDLOC(10, 5, 11));
		mainLocker.add(Setting.getLoationFromSTDLOC(11, 5, 11));
		mainLocker.add(Setting.getLoationFromSTDLOC(11, 5, 10));
		mainLocker.add(Setting.getLoationFromSTDLOC(11, 5, 9));
	}
	
	public static ItemStack air = new ItemStack(Material.AIR);
}


























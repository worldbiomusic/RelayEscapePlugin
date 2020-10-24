package com.wbm.plugin.util.general;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryTool
{
	public static void addItemToPlayer(Player p, ItemStack item) {
		Inventory inv= p.getInventory();
		inv.addItem(item);
	}
	
	public static void removeItemFromPlayer(Player p, ItemStack item) {
		Inventory inv= p.getInventory();
		inv.remove(item);
	}
	
	public static void clearPlayerInv(Player p) {
			p.getInventory().clear();
	}
	
	public static void clearAllPlayerInv() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.getInventory().clear();
		}
	}
	
	public static void addItemToAll(ItemStack item) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.getInventory().addItem(item);
		}
	}
	
	public static void removeItemFromAll(ItemStack item) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.getInventory().removeItem(item);
		}
	}
	
	public static boolean giveItemFromAToB(Player src, Player tar, ItemStack item) {
		Inventory srcInv= src.getInventory();
		if(srcInv.contains(item)) {
			srcInv.removeItem(item);
			Inventory tarInv = tar.getInventory();
			tarInv.addItem(item);
		}

		return false;
	}
}

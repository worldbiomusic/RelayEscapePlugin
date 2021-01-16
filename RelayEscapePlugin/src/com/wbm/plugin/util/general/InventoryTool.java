package com.wbm.plugin.util.general;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryTool {
    public static void addItemToPlayer(Player p, ItemStack item) {
	Inventory inv = p.getInventory();
	inv.addItem(item);
    }

    public static void addItemToPlayers(List<Player> all, ItemStack item) {
	for (Player p : all) {
	    addItemToPlayer(p, item);
	}
    }

    public static void addItemsToPlayer(Player p, ItemStack[] items) {
	Inventory inv = p.getInventory();
	for (ItemStack item : items) {
	    inv.addItem(item);
	}
    }
    
    public static void addItemsToPlayers(List<Player> all, ItemStack[] items) {
	for (ItemStack item : items) {
	    addItemToPlayers(all, item);
	}
    }

    public static void removeItemFromPlayer(Player p, ItemStack item) {
	Inventory inv = p.getInventory();
	inv.remove(item);
    }
    
//    public static void removeItemWithDisplayNameFromPlayer(Player p, ItemStack item) {
//	Inventory inv = p.getInventory();
//	inv.remove(arg0);
//	
//    }

    public static void clearPlayerInv(Player p) {
	p.getInventory().clear();
    }

    public static void clearPlayerInv(List<Player> players) {
	for (Player p : players) {
	    p.getInventory().clear();
	}
    }

    public static void clearAllPlayerInv() {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.getInventory().clear();
	}
    }

    public static void addItemToAll(ItemStack item) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.getInventory().addItem(item);
	}
    }

    public static void removeItemFromAll(ItemStack item) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.getInventory().removeItem(item);
	}
    }

    public static boolean giveItemFromAToB(Player src, Player tar, ItemStack item) {
	Inventory srcInv = src.getInventory();
	if (srcInv.contains(item)) {
	    srcInv.removeItem(item);
	    Inventory tarInv = tar.getInventory();
	    tarInv.addItem(item);
	}

	return false;
    }

    public static boolean containsItem(Player p, ItemStack item) {
	return p.getInventory().contains(item);
    }

    public static ItemStack getItemWithMaterialAndDisplay(Player p, ItemStack targetItem) {
	ItemStack[] items = p.getInventory().getStorageContents();
	for (ItemStack playerItem : items) {
	    if (ItemStackTool.isSameWithMaterialNDisplay(playerItem, targetItem)) {
		return playerItem;
	    }
	}

	return null;
    }
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

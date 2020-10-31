package com.wbm.plugin.util.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class KitTool
{
	private static Map<String, List<ItemStack>> kits = new HashMap<>();
	
	public static void addKit(String kitName, List<ItemStack> items) { 
		kits.put(kitName, items);
	}
	
	public static void addKit(String kitName, ItemStack ...items) {
		List<ItemStack> arrayItems = new ArrayList<ItemStack>();
		for(ItemStack item : items) {
			arrayItems.add(item);
		}
		kits.put(kitName, arrayItems);
	}
	
	public static void addItemToKit(String kitName, ItemStack item) {
		if(kits.containsKey(kitName)) {
			kits.get(kitName).add(item);
		}
	}
	
	
	
	public static void removeItemFromKit(String kitName, ItemStack item) {
		if(kits.containsKey(kitName)) {
			kits.get(kitName).remove(item);
		}
	}
	
	public static List<ItemStack> getKitList(String kitName) {
		return kits.get(kitName);
	}
	
	public static ItemStack[] getKitArray(String kitName) {
		
		return kits.get(kitName).toArray(new ItemStack[0]);
	}
}

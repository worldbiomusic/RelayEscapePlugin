package com.wbm.plugin.util.general;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")

public class ItemStackTool
{
	public static ItemStack item(Material mat) {
		return new ItemStack(mat);
	}
	
	public static ItemStack item(Material mat, byte data) {
		ItemStack item = new ItemStack(mat, 1, (short)1, data);
		
		return item;
	}
	
	public static ItemStack item(Material mat, int amount, 
			short damage, byte data) {
		ItemStack item = new ItemStack(mat, amount, damage, data);
		
		return item;
	}
	
	public static ItemStack item(Material mat, String displayName, String ...lore) {
		ItemStack item = new ItemStack(mat);
		
		item = setItemMeta(item, displayName, lore);
		
		return item;
	}
	
	public static ItemStack item(Material mat, int amount, 
			short damage, byte data, String displayName, String ...lore) {
		ItemStack item = new ItemStack(mat, amount, damage, data);
		
		// item meta
		item = setItemMeta(item, displayName, lore);
		
		return item;
	}
	
	private static ItemStack setItemMeta(ItemStack item, String displayName, String ...lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		List<String> lores = new ArrayList<>();
		for(String l : lore) {
			lores.add(l);
		}
		
		meta.setLore(lores);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static boolean isSameWithMateiralNDisplay(ItemStack item1, ItemStack item2) {
		if(item1 != null && item2 != null ) {
			if(item1.getType() == item2.getType()) {
				String item1Display = item1.getItemMeta().getDisplayName();
				String item2Display = item2.getItemMeta().getDisplayName();
				if(item1Display != null && item2Display != null) {
					if(item1Display.equals(item2Display)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static boolean isSameWithMaterialNData(ItemStack item1, ItemStack item2) {
		if(item1 != null && item2 != null ) {
			if(item1.getType().equals(item2.getType())) {
				if(item1.getData().equals(item2.getData())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static ItemStack block2ItemStack(Block b) {
		return new ItemStack(ItemStackTool.item(b.getType(), b.getData()));
	}
}




























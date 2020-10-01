package com.wbm.plugin.util.general;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BanItemTool
{
	public static void main(String[] args)
	{
		System.out.println(Material.values().length);
		for(Material mat : Material.values()) {
			System.out.println(mat.name());
		}
	}
	
	List<Material> items;
	
	public BanItemTool() {
		this(new ArrayList<>());
	}

	public BanItemTool(List<Material> items)
	{
		this.items=items;
	}
	
	public void banAllItem() {
		for(Material item : Material.values()) {
			this.banItem(item);
		}
	}

	public void banItem(Material item)
	{
		if(!(this.items.contains(item)))
		{
			this.items.add(item);
		}
	}
	
	public void unbanItem(Material item)
	{
		this.items.remove(item);
	}
	
	public boolean containsItem(ItemStack item) {
		Material mat = item.getType();
		return this.items.contains(mat);
	}
	
	public boolean containsItem(Material mat) {
		return this.items.contains(mat);
	}

	public List<Material> getItems()
	{
		return items;
	}

	public void setItems(List<Material> items)
	{
		this.items=items;
	}

}

package com.wbm.plugin.data;

import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockData implements Serializable

{
	private static final long serialVersionUID=1L;
	
	Material material;
	byte data;
	
	public BlockData(Material mat, int data) {
		this.material = mat;
		this.data = (byte)data;
	}
	
	public Material getMaterial()
	{
		return material;
	}
	public void setMaterial(Material material)
	{
		this.material=material;
	}
	public byte getData()
	{
		return data;
	}
	public void setData(byte data)
	{
		this.data=data;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItemStack() {
		return new ItemStack(this.material, 1, (short)1, this.data);
	}
	
	
	@Override
	public String toString()
	{
		return "BlockData [material="+material+", data="+data+"]";
	}
	
}

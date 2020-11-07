package com.wbm.plugin.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.ItemStackTool;

public enum ShopGoods
{
	/*
	 * 서버에서 파는 다양한 물건 저장소
	 * makingBlock, makingTool, toy 등
	 */
	
	// makingBlock
	WHITE_WOOL(ItemStackTool.item(Material.WOOL, (byte)0)),
	BLACK_WOOL(ItemStackTool.item(Material.WOOL, (byte)15)),
	GLASS(ItemStackTool.item(Material.GLASS)),
	// 이 템으로 클릭시 list나오고 명령어로 가능하게 하기(명령어에서 이 템 가지고 있나 체크)
	ROOM_MANAGER(ItemStackTool.item(Material.CHEST, "ROOM_MANAGER", "can load room which you made before")),
	
	// makingTool
	UNDER_BLOCK(ItemStackTool.item(Material.STICK, "UNDER_BLOCK", "create stone under your foot")),
	SPAWN(ItemStackTool.item(Material.WOOD_SWORD, "SPAWN", "teleport to spawn")),
	
	// challengerTool
	HALF_TIME(ItemStackTool.item(Material.WATCH, "HALF_TIME", "delete half of challenging time")),
	
	// viewrTool
	GHOST(ItemStackTool.item(Material.WOOD_SWORD, "GHOST", "can move other player's view easily"));
	
	
	// toy
	
	
	
	
	ItemStack item;
	
	ShopGoods(ItemStack item) {
		this.item = item;
		
		
//		ItemStackTool.item(Material.WOOD_SWORD, "Spawn", "teleport to spawn")
	}
	
	public ItemStack getGoods() {
		return this.item;
	}
	
	public static boolean isRoleGoods(Role role, ShopGoods goods) {
		if(role == Role.MAKER) {
			if(goods == ShopGoods.ROOM_MANAGER
					|| goods == ShopGoods.UNDER_BLOCK
					|| goods == ShopGoods.SPAWN) {
				return true;
			}
		}
		else if(role == Role.TESTER) {
			if(goods == ShopGoods.SPAWN) {
				return true;
			}
		}
		else if(role == Role.CHALLENGER) {
			if(goods == ShopGoods.HALF_TIME) {
				return true;
			}
		}
		else if(role == Role.VIEWER) {
			if(goods == ShopGoods.GHOST) {
				return true;
			}
		}
		
		
		return false;
	}
}




















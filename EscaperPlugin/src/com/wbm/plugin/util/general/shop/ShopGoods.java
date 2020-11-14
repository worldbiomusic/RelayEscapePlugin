package com.wbm.plugin.util.general.shop;

import java.util.ArrayList;
import java.util.List;

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
	GLOWSTONE(ItemStackTool.item(Material.GLOWSTONE)),
	DIRT(ItemStackTool.item(Material.DIRT)),
	COBBLESTONE(ItemStackTool.item(Material.COBBLESTONE)),
	JACK_O_LANTERN(ItemStackTool.item(Material.JACK_O_LANTERN)),
	GLASS(ItemStackTool.item(Material.GLASS)),
	FENCE(ItemStackTool.item(Material.FENCE)),
	WHITE_WOOL(ItemStackTool.item(Material.WOOL, (byte)0)),
	BLACK_WOOL(ItemStackTool.item(Material.WOOL, (byte)15)),
	// event making block
	JUMPING(ItemStackTool.item(Material.STAINED_GLASS, 1, (short)1, (byte)0, "JUMPING", "super jump event block")),
	RESPAWN(ItemStackTool.item(Material.STAINED_GLASS, 1, (short)1, (byte)1, "RESPAWN", "respawn event block")),
	TRAP(ItemStackTool.item(Material.STAINED_GLASS, 1, (short)1, (byte)2, "TRAP", "random trap event block")),
	FLICKING(ItemStackTool.item(Material.STAINED_GLASS, 1, (short)1, (byte)3, "FLICKING", "flicking event block")),
	SOUND_TERROR(ItemStackTool.item(Material.STAINED_GLASS, 1, (short)1, (byte)4, "SOUND_TERROR", "sound terror event block")),
	TERRORIST(ItemStackTool.item(Material.STAINED_GLASS, 1, (short)1, (byte)5, "TERRORIST", "terror to all player event block")),
	
	
	// makingTool
	// 이 템으로 클릭시 list나오고 명령어로 가능하게 하기(명령어에서 이 템 가지고 있나 체크)
	ROOM_MANAGER(ItemStackTool.item(Material.BOOK, "ROOM_MANAGER", "can load room which you made before")),
	UNDER_BLOCK(ItemStackTool.item(Material.STICK, "UNDER_BLOCK", "create stone under your foot")),
	SPAWN(ItemStackTool.item(Material.WOOD_DOOR, "SPAWN", "teleport to spawn")),
	BLOCKS(ItemStackTool.item(Material.CHEST, "BLOCKS", "open inventory which has blocks you can use")),
	
	// challengerTool
	HALF_TIME(ItemStackTool.item(Material.WATCH, "HALF_TIME", "delete half of challenging time")),
	
	// viewrTool
	GHOST(ItemStackTool.item(Material.SKULL_ITEM, 1, (short)1, (byte)3, "GHOST", "can move other player's view easily"));
	
	
	// toy
	
	
	
	
	ItemStack item;
	
	ShopGoods(ItemStack item) {
		this.item = item;
//		ItemStackTool.item(Material.WOOD_SWORD, "Spawn", "teleport to spawn")
//		Material.STAINED_GLASS
	}
	
	public ItemStack getGoods() {
		return this.item;
	}
	
	public static boolean isRoleGoods(Role role, ShopGoods goods) {
		if(role == Role.MAKER) {
			for(ShopGoods makerGoods : getMakerGoods()) { 
				if(goods == makerGoods) {
					return true;
				}
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
	
	public static List<ShopGoods> getMakerGoods() {
		List<ShopGoods> goods = new ArrayList<>();
		goods.add(ShopGoods.ROOM_MANAGER);
		goods.add(ShopGoods.UNDER_BLOCK);
		goods.add(ShopGoods.SPAWN);
		goods.add(ShopGoods.BLOCKS);
		return goods;
	}
	
	public static List<ShopGoods> getMakingBlocks() {
		/*
		 * GLOWSTONE(ItemStackTool.item(Material.GLOWSTONE)),
			DIRT(ItemStackTool.item(Material.DIRT)),
			COBBLESTONE(ItemStackTool.item(Material.COBBLESTONE)),
			JACK_O_LANTERN(ItemStackTool.item(Material.JACK_O_LANTERN)),
			GLASS(ItemStackTool.item(Material.GLASS)),
			FENCE(ItemStackTool.item(Material.FENCE)),
			WHITE_WOOL(ItemStackTool.item(Material.WOOL, (byte)0)),
			BLACK_WOOL(ItemStackTool.item(Material.WOOL, (byte)15)),
		 */
		List<ShopGoods> blocks = new ArrayList<>();
		blocks.add(ShopGoods.GLOWSTONE);
		blocks.add(ShopGoods.DIRT);
		blocks.add(ShopGoods.COBBLESTONE);
		blocks.add(ShopGoods.JACK_O_LANTERN);
		blocks.add(ShopGoods.GLASS);
		blocks.add(ShopGoods.FENCE);
		blocks.add(ShopGoods.WHITE_WOOL);
		blocks.add(ShopGoods.BLACK_WOOL);
		
		// event blocks
		blocks.add(ShopGoods.JUMPING);
		blocks.add(ShopGoods.RESPAWN);
		blocks.add(ShopGoods.TRAP);
		blocks.add(ShopGoods.FLICKING);
		blocks.add(ShopGoods.SOUND_TERROR);
		blocks.add(ShopGoods.TERRORIST);
		
		return blocks;
	}
}




















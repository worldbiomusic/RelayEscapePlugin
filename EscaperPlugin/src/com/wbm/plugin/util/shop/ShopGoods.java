package com.wbm.plugin.util.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.ItemStackTool;

public enum ShopGoods {
    /*
     * 서버에서 파는 다양한 물건 저장소 makingBlock, makingTool, toy 등
     */

    // makingBlock
    GLOWSTONE(ItemStackTool.item(Material.GLOWSTONE), GoodsRole.MAKING_BLOCK),
    DIRT(ItemStackTool.item(Material.DIRT), GoodsRole.MAKING_BLOCK),
    COBBLESTONE(ItemStackTool.item(Material.COBBLESTONE), GoodsRole.MAKING_BLOCK),
    JACK_O_LANTERN(ItemStackTool.item(Material.JACK_O_LANTERN), GoodsRole.MAKING_BLOCK),
    GLASS(ItemStackTool.item(Material.GLASS), GoodsRole.MAKING_BLOCK),
    FENCE(ItemStackTool.item(Material.FENCE), GoodsRole.MAKING_BLOCK),
    WHITE_WOOL(ItemStackTool.item(Material.WOOL, (byte) 0), GoodsRole.MAKING_BLOCK),
    BLACK_WOOL(ItemStackTool.item(Material.WOOL, (byte) 15), GoodsRole.MAKING_BLOCK),
    // event making block
    JUMPING(ItemStackTool.item(Material.STAINED_GLASS, 1, (short) 1, (byte) 0, "JUMPING", "super jump event block"),
	    GoodsRole.MAKING_BLOCK),
    RESPAWN(ItemStackTool.item(Material.STAINED_GLASS, 1, (short) 1, (byte) 1, "RESPAWN", "respawn event block"),
	    GoodsRole.MAKING_BLOCK),
    TRAP(ItemStackTool.item(Material.STAINED_GLASS, 1, (short) 1, (byte) 2, "TRAP", "random trap event block"),
	    GoodsRole.MAKING_BLOCK),
    FLICKING(ItemStackTool.item(Material.STAINED_GLASS, 1, (short) 1, (byte) 3, "FLICKING", "flicking event block"),
	    GoodsRole.MAKING_BLOCK),
    SOUND_TERROR(ItemStackTool.item(Material.STAINED_GLASS, 1, (short) 1, (byte) 4, "SOUND_TERROR",
	    "sound terror event block"), GoodsRole.MAKING_BLOCK),
    HURT(ItemStackTool.item(Material.STAINED_GLASS, 1, (short) 1, (byte) 5, "HURT", "hurt payer"),
	    GoodsRole.MAKING_BLOCK),

    // makingTool
    // 이 템으로 클릭시 list나오고 명령어로 가능하게 하기(명령어에서 이 템 가지고 있나 체크)
    ROOM_MANAGER(ItemStackTool.item(Material.BOOK, "ROOM_MANAGER", "can load room which you made before"),
	    GoodsRole.MAKING),
    UNDER_BLOCK(ItemStackTool.item(Material.STICK, "UNDER_BLOCK", "create stone under your foot"), GoodsRole.MAKING),
    SPAWN(ItemStackTool.item(Material.WOOD_DOOR, "SPAWN", "teleport to spawn"), GoodsRole.MAKING),
    CHEST(ItemStackTool.item(Material.CHEST, "CHEST", "open inventory which has blocks you can use"), GoodsRole.MAKING),
    HIGH_10(ItemStackTool.item(Material.TRIPWIRE_HOOK, "HIGH_10", "allow room high limit up to 10"), GoodsRole.ROOM_HIGH),
    HIGH_15(ItemStackTool.item(Material.TRIPWIRE_HOOK, "HIGH_15", "allow room high limit up to 15"), GoodsRole.ROOM_HIGH),

    // challengerTool
    HALF_TIME(ItemStackTool.item(Material.WATCH, "HALF_TIME", "delete half of challenging time"),
	    GoodsRole.CHALLENGING),

    // viewrTool
    GHOST(ItemStackTool.item(Material.SKULL_ITEM, 1, (short) 1, (byte) 3, "GHOST",
	    "can move other player's view easily"), GoodsRole.VIEWING);

    // toy

    ItemStack item;
    GoodsRole goodsRole;

    ShopGoods(ItemStack item, GoodsRole goodsRole) {
	this.item = item;
	this.goodsRole = goodsRole;
    }
    
    public GoodsRole getGoodsRole() {
	return this.goodsRole;
    }

    public ItemStack getItemStack() {
	return this.item;
    }

    public static List<ShopGoods> getPlayerRoleGoods(Role role) {
	List<ShopGoods> goods = new ArrayList<>();
	for (ShopGoods good : ShopGoods.values()) {
	    if (good.isRoleGood(role)) {
		goods.add(good);
	    }
	}
	return goods;
    }
    
    public static List<ShopGoods> getGoodsRoleGoods(GoodsRole role) {
	List<ShopGoods> goods = new ArrayList<>();
	for (ShopGoods good : ShopGoods.values()) {
	    if (good.goodsRole == role) {
		goods.add(good);
	    }
	}
	return goods;
    }
    
    public boolean equals(ShopGoods other) {
	return this.name().equals(other.name());
    }

    public boolean isRoleGood(Role role) {
	if (this.goodsRole == GoodsRole.WAITING && role == Role.WAITER
		|| this.goodsRole == GoodsRole.MAKING && role == Role.MAKER
		|| this.goodsRole == GoodsRole.TESTING && role == Role.TESTER
		|| this.goodsRole == GoodsRole.CHALLENGING && role == Role.CHALLENGER
		|| this.goodsRole == GoodsRole.VIEWING && role == Role.VIEWER) {
	    return true;
	}

	return false;
    }
}

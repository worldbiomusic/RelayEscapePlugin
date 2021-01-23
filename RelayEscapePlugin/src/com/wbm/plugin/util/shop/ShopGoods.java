package com.wbm.plugin.util.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;

public enum ShopGoods {
    /*
     * 서버에서 파는 다양한 물건 저장소 makingBlock, makingTool, toy 등
     * 
     * [주의] 먹는것 추가하면 안됨!
     */

    // makingBlock

    // basic
    GLOWSTONE(ItemStackTool.item(Material.GLOWSTONE), GoodsRole.MAKING_BLOCK),
    DIRT(ItemStackTool.item(Material.DIRT), GoodsRole.MAKING_BLOCK),

    // 1f
    COBBLESTONE(ItemStackTool.item(Material.COBBLESTONE), GoodsRole.MAKING_BLOCK),
    JACK_O_LANTERN(ItemStackTool.item(Material.JACK_O_LANTERN), GoodsRole.MAKING_BLOCK),
    GLASS(ItemStackTool.item(Material.GLASS), GoodsRole.MAKING_BLOCK),
    FENCE(ItemStackTool.item(Material.FENCE), GoodsRole.MAKING_BLOCK),
    WHITE_WOOL(ItemStackTool.item(Material.WOOL, (byte) 0), GoodsRole.MAKING_BLOCK),
    BLACK_WOOL(ItemStackTool.item(Material.WOOL, (byte) 15), GoodsRole.MAKING_BLOCK),

    // 2f
    COAL_BLOCK(ItemStackTool.item(Material.COAL_BLOCK), GoodsRole.MAKING_BLOCK),
    REDSTONE_BLOCK(ItemStackTool.item(Material.REDSTONE_BLOCK), GoodsRole.MAKING_BLOCK),
    LAPIS_BLOCK(ItemStackTool.item(Material.LAPIS_BLOCK), GoodsRole.MAKING_BLOCK),
    IRON_BLOCK(ItemStackTool.item(Material.IRON_BLOCK), GoodsRole.MAKING_BLOCK),
    GOLD_BLOCK(ItemStackTool.item(Material.GOLD_BLOCK), GoodsRole.MAKING_BLOCK),
    DIAMOND_BLOCK(ItemStackTool.item(Material.DIAMOND_BLOCK), GoodsRole.MAKING_BLOCK),

    // 3f
    WOOD(ItemStackTool.item(Material.WOOD), GoodsRole.MAKING_BLOCK),
    ACACIA_STAIRS(ItemStackTool.item(Material.ACACIA_STAIRS), GoodsRole.MAKING_BLOCK),
    WOOD_STEP(ItemStackTool.item(Material.WOOD_STEP), GoodsRole.MAKING_BLOCK),
    LOG(ItemStackTool.item(Material.LOG), GoodsRole.MAKING_BLOCK),
    LOG2(ItemStackTool.item(Material.LOG, (byte) 2), GoodsRole.MAKING_BLOCK),
    LOG3(ItemStackTool.item(Material.LOG, (byte) 3), GoodsRole.MAKING_BLOCK),

    // 4f
    GRASS(ItemStackTool.item(Material.GRASS), GoodsRole.MAKING_BLOCK),
    DIRT2(ItemStackTool.item(Material.DIRT, (byte) 2), GoodsRole.MAKING_BLOCK),
    GRAVEL(ItemStackTool.item(Material.GRAVEL), GoodsRole.MAKING_BLOCK),
    STONE(ItemStackTool.item(Material.STONE), GoodsRole.MAKING_BLOCK),
    STONE6(ItemStackTool.item(Material.STONE, (byte) 6), GoodsRole.MAKING_BLOCK),
    STONE2(ItemStackTool.item(Material.STONE, (byte) 2), GoodsRole.MAKING_BLOCK),

    // 5f
    SANDSTONE(ItemStackTool.item(Material.SANDSTONE), GoodsRole.MAKING_BLOCK),
    SANDSTONE2(ItemStackTool.item(Material.SANDSTONE, (byte) 2), GoodsRole.MAKING_BLOCK),
    SMOOTH_STAIRS(ItemStackTool.item(Material.SMOOTH_STAIRS), GoodsRole.MAKING_BLOCK),
    STEP1(ItemStackTool.item(Material.STEP, (byte) 1), GoodsRole.MAKING_BLOCK),
    SANDSTONE1(ItemStackTool.item(Material.SANDSTONE, (byte) 1), GoodsRole.MAKING_BLOCK),
    SAND(ItemStackTool.item(Material.SAND, (byte) 2), GoodsRole.MAKING_BLOCK),

    // 6f
    WATER_BUCKET(ItemStackTool.item(Material.WATER_BUCKET), GoodsRole.MAKING_BLOCK),
    SLIME_BLOCK(ItemStackTool.item(Material.SLIME_BLOCK), GoodsRole.MAKING_BLOCK),
    BARRIER(ItemStackTool.item(Material.BARRIER), GoodsRole.MAKING_BLOCK),
    PACKED_ICE(ItemStackTool.item(Material.PACKED_ICE), GoodsRole.MAKING_BLOCK),

    // 7f
    CACTUS(ItemStackTool.item(Material.CACTUS), GoodsRole.MAKING_BLOCK),
    WEB(ItemStackTool.item(Material.WEB), GoodsRole.MAKING_BLOCK),

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
    UP_TP(ItemStackTool.item(Material.STAINED_GLASS, 1, (short) 1, (byte) 6, "UP_TP", "teleport player 3 block up"),
	    GoodsRole.MAKING_BLOCK),
    DOWN_TP(ItemStackTool.item(Material.STAINED_GLASS, 1, (short) 1, (byte) 7, "DOWN_TP",
	    "teleport player 3 block down"), GoodsRole.MAKING_BLOCK),

    // makingTool
    // 이 템으로 클릭시 list나오고 명령어로 가능하게 하기(명령어에서 이 템 가지고 있나 체크)
    ROOM_MANAGER(ItemStackTool.item(Material.BOOK, "ROOM_MANAGER", "can load room which you made before"),
	    GoodsRole.MAKING),
    UNDER_BLOCK(ItemStackTool.item(Material.STICK, "UNDER_BLOCK", "create stone under your foot"), GoodsRole.MAKING),
    SPAWN(ItemStackTool.item(Material.WOOD_DOOR, "SPAWN", "teleport to spawn"), GoodsRole.MAKING, GoodsRole.TESTING,
	    GoodsRole.CHALLENGING, GoodsRole.VIEWING),
    CHEST(ItemStackTool.item(Material.CHEST, "CHEST", "open inventory which has blocks you can use"), GoodsRole.MAKING),
    FINISH(ItemStackTool.item(Material.ARROW, "FINISH", "finish MakingTime and go next to the TesetingTime"),
	    GoodsRole.MAKING),
    BLOCK_CHANGER(
	    ItemStackTool.item(Material.WOOD_PICKAXE, "BLOCK_CHANGER",
		    "Change the block immediately with the block you are holding", "===Mode===", "off"),
	    GoodsRole.MAKING),
    HIDE(ItemStackTool.item(Material.BOWL, "HIDE", "You can hide you from everyone", "===Mode===", "off"),
	    GoodsRole.MAKING, GoodsRole.TESTING),

    /*
     * 밑의 ROOM_SETTING 관련 굿즈 제작시 지켜야 하는 사항
     * 
     * kind_# (kind = 굿즈 종류, #숫자)
     * 
     * PlayerData에서 getRoomSettingGoodsHighestValue(kind)메소드로 최대값 가져올 수 있게 규칙을 정함
     */
    // ROOM_SETTING
    HIGH_5(ItemStackTool.item(Material.TRIPWIRE_HOOK, "HIGH_5", "allow room high limit up to 5"),
	    GoodsRole.ROOM_SETTING),
    HIGH_10(ItemStackTool.item(Material.TRIPWIRE_HOOK, "HIGH_10", "allow room high limit up to 10"),
	    GoodsRole.ROOM_SETTING),
    HIGH_15(ItemStackTool.item(Material.TRIPWIRE_HOOK, "HIGH_15", "allow room high limit up to 15"),
	    GoodsRole.ROOM_SETTING),
    HIGH_20(ItemStackTool.item(Material.TRIPWIRE_HOOK, "HIGH_20", "allow room high limit up to 20"),
	    GoodsRole.ROOM_SETTING),
    HIGH_25(ItemStackTool.item(Material.TRIPWIRE_HOOK, "HIGH_25", "allow room high limit up to 25"),
	    GoodsRole.ROOM_SETTING),
    HIGH_30(ItemStackTool.item(Material.TRIPWIRE_HOOK, "HIGH_30", "allow room high limit up to 30"),
	    GoodsRole.ROOM_SETTING),

    // 제작시간 굿즈
    MAKINGTIME_5(ItemStackTool.item(Material.TRIPWIRE_HOOK, "MAKINGTIME_5", "MakingTime increases to 5 min"),
	    GoodsRole.ROOM_SETTING),
    MAKINGTIME_10(ItemStackTool.item(Material.TRIPWIRE_HOOK, "MAKINGTIME_10", "MakingTime increases to 10 min"),
	    GoodsRole.ROOM_SETTING),
    MAKINGTIME_15(ItemStackTool.item(Material.TRIPWIRE_HOOK, "MAKINGTIME_15", "MakingTime increases to 15 min"),
	    GoodsRole.ROOM_SETTING),

    // challengerTool
    REDUCE_TIME(ItemStackTool.item(Material.WATCH, "HALF_TIME", "delete half of challenging time"),
	    GoodsRole.CHALLENGING),
    SUPER_STAR(ItemStackTool.item(Material.GLOWSTONE_DUST, "SUPER_STAR", "you are now supre star", "===Mode===", "off"),
	    GoodsRole.CHALLENGING),

    // viewrTool
    GHOST(ItemStackTool.item(Material.GHAST_TEAR, 1, (short) 1, (byte) 3, "GHOST",
	    "can move other player's view easily"), GoodsRole.VIEWING),

    // ALWAYS
    GOODS_LIST(ItemStackTool.item(Material.PAPER, 1, (short) 1, (byte) 3, "GOODS_LIST", "open Goods List GUI"),
	    GoodsRole.ALWAYS),

    /*
     * CASH
     */
    CHAT(ItemStackTool.item(Material.TORCH, "CHAT", "player can chat"), GoodsRole.IN_POCKET),
    TOKEN_500(ItemStackTool.item(Material.EMERALD, "TOKEN_500", "Click to get 500 Token"), GoodsRole.ALWAYS),
    COLOR_CHAT(ItemStackTool.item(Material.NAME_TAG, "COLOR_CHAT", "Your name will be changed with random color"),
	    GoodsRole.ALWAYS),

    // battle kit
    CHAIN_BOOTS(ItemStackTool.item(Material.CHAINMAIL_BOOTS, "CHAIN_BOOTS", "it's just chain boots"), GoodsRole.BATTLE);

    ItemStack item;
    List<GoodsRole> goodsRole;

    ShopGoods(ItemStack item, GoodsRole... goodsRole) {
	this.item = item;

	this.goodsRole = new ArrayList<>();
	for (GoodsRole role : goodsRole) {
	    this.goodsRole.add(role);
	}
    }

    public ItemStack getItemStack() {
	return this.item;
    }

    public boolean equals(ShopGoods other) {
	return this.name().equals(other.name());
    }

    public static List<ShopGoods> getGoodsWithGoodsRole(GoodsRole role) {
	/*
	 * 굿즈 역할로 구분된 굿즈 리스트 반환
	 */
	List<ShopGoods> goods = new ArrayList<>();
	for (ShopGoods good : ShopGoods.values()) {
	    if (good.isGoodsRoleGoods(role)) {
		goods.add(good);
	    }
	}
	return goods;
    }

    public boolean isGoodsRoleGoods(GoodsRole r) {
	// 굿즈의 역할이 맞는지
	return this.goodsRole.contains(r);
    }

    public static List<ShopGoods> getPlayerRoleGoods(Role role) {
	/*
	 * 플레이어 역할에 맞는 굿즈 리스트 반환
	 */
	List<ShopGoods> goods = new ArrayList<>();
	for (ShopGoods good : ShopGoods.values()) {
	    if (good.isRoleGood(role)) {
		goods.add(good);
	    }
	}
	return goods;
    }

    public boolean isRoleGood(Role role) {
	/*
	 * 해당 굿즈가 역할에 맞는 굿즈인지 검사
	 */

	switch (role) {
	case WAITER:
	    return this.goodsRole.contains(GoodsRole.WAITING);
	case MAKER:
	    return this.goodsRole.contains(GoodsRole.MAKING);
	case TESTER:
	    return this.goodsRole.contains(GoodsRole.TESTING);
	case CHALLENGER:
	    return this.goodsRole.contains(GoodsRole.CHALLENGING);
	case VIEWER:
	    return this.goodsRole.contains(GoodsRole.VIEWING);
	default:
	    return false;
	}
    }

    public static void giveGoodsToPleyer(PlayerDataManager pDataManager, Player p) {
	/*
	 * playerData가 가지고 있는 good중 해당 role에 맞는 good만을 인벤토리에 추가함 이 메소드가 실행되기 전에 선행되야 하는
	 * 것: player role 변경!
	 * 
	 * * 각 Role에 맞는 Goods중에서 가지고 있는 Goods 인벤에 지급
	 */
	PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
	// 플레이어 역할에 맞는 굿즈 제공
	for (ShopGoods good : ShopGoods.getPlayerRoleGoods(pData.getRole())) {
	    if (pData.hasGoods(good)) {
		InventoryTool.addItemToPlayer(p, good.getItemStack());
	    }
	}
	// 항상 가지고 있어야 하는 굿즈(ALWAYS) 제공
	for (ShopGoods good : ShopGoods.getGoodsWithGoodsRole(GoodsRole.ALWAYS)) {
	    if (pData.hasGoods(good)) {
		InventoryTool.addItemToPlayer(p, good.getItemStack());
	    }
	}

    }

    public static void giveGoodsToPleyers(PlayerDataManager pDataManager, List<Player> players) {
	for (Player p : players) {
	    giveGoodsToPleyer(pDataManager, p);
	}
    }
}
